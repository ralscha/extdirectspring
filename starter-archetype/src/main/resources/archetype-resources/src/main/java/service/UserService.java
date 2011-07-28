#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.FORM_POST;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.filter.StringFilter;
import ${package}.entity.QUser;
import ${package}.entity.Role;
import ${package}.entity.User;
import ${package}.repository.RoleRepository;
import ${package}.repository.UserRepository;
import ${package}.util.Util;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.mysema.query.BooleanBuilder;

@Service
@Controller
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MessageSource messageSource;

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ExtDirectStoreResponse<User> load(ExtDirectStoreReadRequest request) {

		Page<User> page;
		if (request.getFilters().isEmpty()) {
			page = userRepository.findAll(Util.createPageRequest(request));
		} else {
			StringFilter filter = (StringFilter) request.getFilters().iterator().next();
			String filterValue = filter.getValue();

			BooleanBuilder bb = new BooleanBuilder();
			if (StringUtils.hasText(filterValue)) {
				String likeValue = "%" + filterValue.toLowerCase() + "%";
				bb.or(QUser.user.userName.lower().like(likeValue));
				bb.or(QUser.user.name.lower().like(likeValue));
				bb.or(QUser.user.firstName.lower().like(likeValue));
				bb.or(QUser.user.email.lower().like(likeValue));
			}

			page = userRepository.findAll(bb, Util.createPageRequest(request));
		}
		return new ExtDirectStoreResponse<User>((int) page.getTotalElements(), page.getContent());
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("isAuthenticated()")
	public List<Role> loadAllRoles() {
		return roleRepository.findAll();
	}

	@ExtDirectMethod(STORE_MODIFY)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void destroy(List<User> destroyUsers) {
		for (User user : destroyUsers) {
			userRepository.delete(user);
		}
	}

	@ExtDirectMethod(FORM_POST)
	@ResponseBody
	@RequestMapping(value = "/userFormPost", method = RequestMethod.POST)
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ExtDirectResponse userFormPost(HttpServletRequest request, Locale locale,
			@RequestParam(required = false) String roleIds, @Valid User modifiedUser, BindingResult result) {

		//Check uniqueness of userName and email
		if (!result.hasErrors()) {
			BooleanBuilder bb = new BooleanBuilder(QUser.user.userName.equalsIgnoreCase(modifiedUser.getUserName()));
			if (modifiedUser.getId() != null) {
				bb.and(QUser.user.id.ne(modifiedUser.getId()));
			}
			if (userRepository.count(bb) > 0) {
				result.rejectValue("userName", null, messageSource.getMessage("user_usernametaken", null, locale));
			}

			bb = new BooleanBuilder(QUser.user.email.equalsIgnoreCase(modifiedUser.getEmail()));
			if (modifiedUser.getId() != null) {
				bb.and(QUser.user.id.ne(modifiedUser.getId()));
			}
			if (userRepository.count(bb) > 0) {
				result.rejectValue("email", null, messageSource.getMessage("user_emailtaken", null, locale));
			}
		}

		if (!result.hasErrors()) {

			if (StringUtils.hasText(modifiedUser.getPasswordHash())) {
				modifiedUser.setPasswordHash(passwordEncoder.encode(modifiedUser.getPasswordHash()));
			}

			Set<Role> roles = Sets.newHashSet();
			if (StringUtils.hasText(roleIds)) {
				Iterable<String> roleIdsIt = Splitter.on(",").split(roleIds);
				for (String roleId : roleIdsIt) {
					roles.add(roleRepository.findOne(Long.valueOf(roleId)));
				}
			}

			if (modifiedUser.getId() != null) {
				User dbUser = userRepository.findOne(modifiedUser.getId());
				if (dbUser != null) {
					dbUser.getRoles().clear();
					dbUser.getRoles().addAll(roles);
					dbUser.update(modifiedUser);
				}
			} else {
				modifiedUser.setCreateDate(new Date());
				modifiedUser.setRoles(roles);
				userRepository.save(modifiedUser);
			}
		}

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
		builder.addErrors(result);
		return builder.build();

	}

}
