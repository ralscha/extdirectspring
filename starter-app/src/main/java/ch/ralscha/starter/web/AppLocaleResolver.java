package ch.ralscha.starter.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import ch.ralscha.starter.config.JpaUserDetails;

public class AppLocaleResolver extends AbstractLocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return request.getLocale();
		} else if (authentication.getPrincipal() instanceof JpaUserDetails) {
			return ((JpaUserDetails) authentication.getPrincipal()).getLocale();
		} else if (getDefaultLocale() != null) {
			return getDefaultLocale();
		} else {
			return Locale.ENGLISH;
		}
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new UnsupportedOperationException("Cannot change locale - use a different locale resolution strategy");
	}

}
