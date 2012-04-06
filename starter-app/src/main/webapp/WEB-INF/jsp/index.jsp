<!doctype html> 
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.Locale"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta charset="utf-8">
	<link rel="shortcut icon" href="<c:url value="/favicon.ico"/>" /> 	
    <title>starter</title>
    
    <link rel="stylesheet" type="text/css" href="http://cdn.sencha.io/ext-4.0.7-gpl/resources/css/ext-all.css">
    <!-- 
    <link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css?v=<spring:eval expression='@environment["extjs.version"]'/>">
     -->
    <spring:eval expression="@environment.acceptsProfiles('development')" var="isDevelopment" />
    <c:if test="${isDevelopment}">
	    <link rel="stylesheet" type="text/css" href="resources/css/app.css">
	    <link rel="stylesheet" type="text/css" href="ux/css/ClearButton.css">
	    <link rel="stylesheet" type="text/css" href="ux/css/Notification.css">
	    <link rel="stylesheet" type="text/css" href="ux/css/BoxSelect.css">
	    
	    <script charset="utf-8" src="http://cdn.sencha.io/ext-4.0.7-gpl/ext-all-debug.js"></script>
	    <!-- 
	    <script src="extjs/ext-all-debug.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script>
	     -->
	     
	    <script src="i18n.js"></script>
	    <script src="loader.js"></script>
		
	    <script src="api.js"></script>
	    <script src="direct.js"></script>
	    	    
	    <script src="app.js"></script>
    </c:if> 
    
    <c:if test="${not isDevelopment}">
		<link rel="stylesheet" type="text/css" href="wro/app.css?v=<spring:eval expression='@environment["application.version"]'/>" />
		<script src="i18n.js"></script>
		
		<script charset="utf-8" src="http://cdn.sencha.io/ext-4.0.7-gpl/ext-all.js"></script>
		<!-- 
		<script src="extjs/ext-all.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script>
		 -->
		 
	    <script src="wro/app.js?v=<spring:eval expression='@environment["application.version"]'/>"></script>   
    </c:if>

	<% Locale locale = RequestContextUtils.getLocale(request); %>
    <% if (locale != null && locale.getLanguage().toLowerCase().equals("de")) { %>
      <script src="http://cdn.sencha.io/ext-4.0.7-gpl/locale/ext-lang-de.js"></script>
      <!-- 
      <script src="extjs/locale/ext-lang-de.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script>
       -->
      <script src="ux/lang-de.js?v=<spring:eval expression='@environment["application.version"]'/>"></script>
    <% } %>	
    
</head>
<body></body>
</html>