#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import java.util.Collection;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import ${package}.entity.Role;
import ${package}.entity.User;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class JpaUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private ImmutableSet<GrantedAuthority> authorities;
	private String password;
	private String username;
	private boolean enabled;
	private String fullName;
	private Locale locale;

	public JpaUserDetails(User user) {
		this.password = user.getPasswordHash();
		this.username = user.getUserName();
		this.enabled = user.isEnabled();
		this.fullName = Joiner.on(" ").skipNulls().join(user.getFirstName(), user.getName());

		if (StringUtils.hasText(user.getLocale())) {
			this.locale = new Locale(user.getLocale());
		} else {
			this.locale = Locale.ENGLISH;
		}

		Builder<GrantedAuthority> builder = ImmutableSet.builder();
		for (Role role : user.getRoles()) {
			builder.add(new SimpleGrantedAuthority(role.getName()));
		}

		this.authorities = builder.build();
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getFullName() {
		return fullName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Locale getLocale() {
		return locale;
	}

}
