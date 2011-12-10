#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.TREE_LOAD;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

import com.google.common.collect.Sets;

@Service
public class NavigationService {

	@Autowired
	private MessageSource messageSource;

	private MenuNode root;

	public NavigationService() throws JsonParseException, JsonMappingException, IOException {
		Resource menu = new ClassPathResource("/menu.json");
		ObjectMapper mapper = new ObjectMapper();
		root = mapper.readValue(menu.getInputStream(), MenuNode.class);
	}

	@ExtDirectMethod(TREE_LOAD)
	@PreAuthorize("isAuthenticated()")
	public MenuNode getNavigation(Locale locale) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		MenuNode copyOfRoot = new MenuNode(root, authentication.getAuthorities());
		upateIdAndLeaf(new MutableInt(0), copyOfRoot, locale);

		return copyOfRoot;
	}

	private void upateIdAndLeaf(MutableInt id, MenuNode parent, Locale locale) {
		parent.setId(id.intValue());
		parent.setText(messageSource.getMessage(parent.getText(), null, parent.getText(), locale));
		id.add(1);

		parent.setLeaf(parent.getChildren().isEmpty());

		Set<MenuNode> removeChildren = Sets.newHashSet();
		for (MenuNode child : parent.getChildren()) {
			//Remove child if it has no children and it's not a leaf
			if (child.getView() == null && child.getChildren().isEmpty()) {
				removeChildren.add(child);
			} else {
				upateIdAndLeaf(id, child, locale);
			}
		}

		parent.getChildren().removeAll(removeChildren);
	}

}
