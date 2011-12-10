package ch.ralscha.starter.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MenuNode {
	private int id;
	private String text;
	private String view;
	private boolean leaf;
	private boolean expanded;
	private String iconCls;

	private Set<String> roles = Sets.newHashSet();

	private List<MenuNode> children = Lists.newArrayList();

	public MenuNode() {
		//default constructor
	}

	public MenuNode(MenuNode source, Collection<? extends GrantedAuthority> authorities) {
		this.text = source.getText();
		this.view = source.getView();
		this.expanded = source.isExpanded();
		this.iconCls = source.getIconCls();

		for (MenuNode sourceChild : source.getChildren()) {
			if (hasRole(sourceChild, authorities)) {
				children.add(new MenuNode(sourceChild, authorities));
			}
		}
	}

	private boolean hasRole(MenuNode child, Collection<? extends GrantedAuthority> authorities) {
		if (child.getRoles().isEmpty()) {
			return true;
		}

		for (GrantedAuthority grantedAuthority : authorities) {
			if (child.getRoles().contains(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<MenuNode> getChildren() {
		return children;
	}

	public void setChildren(List<MenuNode> children) {
		this.children = children;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

}