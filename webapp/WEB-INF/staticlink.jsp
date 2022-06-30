<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.SystemUtils"%>
<%@page import="com.xlx.commonn.CommonBusinessException"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.common.registry.RegistryHelper"%>
<html>
    <head>
        <title>TREDS</title>
        <%@include file="includes1.jsp" %>
    </head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
   	 <%
		String lPage = request.getParameter("link");
    	RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
    	File lFile = new File(lRegistryHelper.getString(AppConstants.REGISTRY_STATICDOCUMENTS)+lPage);
   		BufferedReader input = new BufferedReader(new FileReader(lFile));
   	  	String line = "";
   	  	while ((line=input.readLine()) != null)
   	  	{
   	  	   out.println(line);
   	  	}
   	  	out.flush();
   	  	input.close();
	 %>

   	<%@include file="footer1.jsp" %>

    </body>
</html>