#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
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
    <c:if test="${symbol_dollar}{isDevelopment}">
	    <script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/ext-all-debug.js"></script>	    
	    <script type="text/javascript" src="login.js"></script>
    </c:if>
    <c:if test="${symbol_dollar}{not isDevelopment}">
	    <script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/ext-all.js"></script> 
		<script type="text/javascript" src="wro/login.js?v=<spring:eval expression='@environment["application.version"]'/>"></script>        
	</c:if>
	    
	<% Locale locale = RequestContextUtils.getLocale(request); %>
    <% if (locale != null && locale.getLanguage().toLowerCase().equals("de")) { %>
      <script type="text/javascript" src="http://extjs.cachefly.net/ext-4.0.2a/locale/ext-lang-de.js"></script>
    <% } %>	
	<c:if test="${symbol_dollar}{not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
	   <script type="text/javascript">
	   Ext.onReady(function() {
	   Ext.Msg.alert(i18n.error, i18n.login_failed);
	   });
	   </script>
	</c:if>    
</head>
<body>


</body>
</html>