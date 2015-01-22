package ch.ralscha.extdirectspring.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

public interface ExtRequestListener {
	void beforeRequest(ExtDirectRequest directRequest, ExtDirectResponse directResponse,
			HttpServletRequest request, HttpServletResponse response, Locale locale);
	
	void afterRequest(ExtDirectRequest directRequest, ExtDirectResponse directResponse,
			HttpServletRequest request, HttpServletResponse response, Locale locale);
}
