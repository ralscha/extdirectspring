#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<!doctype html> 
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.Locale"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<html>
<head>
    <meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">	
    <title>${artifactId}</title>    
    <link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css?v=<spring:eval expression='@environment["extjs.version"]'/>">    
    
    <spring:eval expression="@environment.acceptsProfiles('development')" var="isDevelopment" />    
    <c:if test="${symbol_dollar}{isDevelopment}">
        <link rel="stylesheet" type="text/css" href="resources/css/app.css">
        <link rel="stylesheet" type="text/css" href="resources/css/Notification.css">
	    <script src="extjs/ext-all-debug.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script>
	    <script src="i18n.js"></script>
	    <script src="ux/window/Notification.js"></script>
	    <script src="login.js"></script>
    </c:if>
    <c:if test="${symbol_dollar}{not isDevelopment}">
        <link rel="stylesheet" type="text/css" href="wro/login.css?v=<spring:eval expression='@environment["application.version"]'/>" />
      <script src="extjs/ext-all.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script> 
		<script src="wro/login.js?v=<spring:eval expression='@environment["application.version"]'/>"></script>        
	</c:if>
	    
	<% Locale locale = RequestContextUtils.getLocale(request); %>
    <% if (locale != null && locale.getLanguage().toLowerCase().equals("de")) { %>
      <script src="extjs/locale/ext-lang-de.js?v=<spring:eval expression='@environment["extjs.version"]'/>"></script>
    <% } %>	
	<c:if test="${symbol_dollar}{not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
	   <script type="text/javascript">
	   Ext.onReady(function() {
	     Ext.ux.window.Notification.error(i18n.error, i18n.login_failed);
	   });
	   </script>
	</c:if>    
</head>
<body>
  <!--[if lt IE 9 ]>
    <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js"></script>
    <script>window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
  <![endif]-->
</body>
</html>