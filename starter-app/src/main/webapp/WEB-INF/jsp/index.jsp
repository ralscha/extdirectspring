<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.Locale"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<!doctype html> 
<html>
<head>
    <meta charset="utf-8">
    <title>Starter</title>
    <link rel="stylesheet" type="text/css" href="http://extjs.cachefly.net/ext-4.0.2a/resources/css/ext-all.css">
    
    <script type="text/javascript" src="i18n.js"></script>
    
    <spring:eval expression="@environment.acceptsProfiles('development')" var="isDevelopment" />
    <c:if test="${isDevelopment}">  
	    <link rel="stylesheet" type="text/css" href="css/app.css">
	    <link rel="stylesheet" type="text/css" href="http://extjs.cachefly.net/ext-4.0.2a/examples/ux/css/ItemSelector.css">
	    
	    <script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/ext-all-debug.js"></script>
	    <script type="text/javascript" src="loader.js"></script>
		
	    <script type="text/javascript" src="api.js"></script>
	    <script type="text/javascript" src="direct.js"></script>
	    	    
	    <script type="text/javascript" src="app.js"></script>
    </c:if> 
    
    <c:if test="${not isDevelopment}">
		<link rel="stylesheet" type="text/css" href="wro/app.css?v=<spring:eval expression='@environment["application.version"]'/>" />
		<script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/ext-all.js"></script>
	    <script type="text/javascript" src="wro/app.js?v=<spring:eval expression='@environment["application.version"]'/>"></script>   
    </c:if>

	<% Locale locale = RequestContextUtils.getLocale(request); %>
    <% if (locale != null && locale.getLanguage().toLowerCase().equals("de")) { %>
      <script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/locale/ext-lang-de.js"></script>
    <% } %>	
    
</head>
<body></body>
</html>