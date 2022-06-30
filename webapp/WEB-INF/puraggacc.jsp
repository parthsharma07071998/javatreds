<%@page import="java.util.Map"%>
<%@page import="com.xlx.treds.ClickWrapHelper"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Date"%>
<%@page import="com.xlx.common.registry.bean.RegistryEntryBean"%>
<%@page import="com.xlx.common.utilities.FormatHelper"%>
<%@page import="com.xlx.common.registry.RegistryHelper"%>
<%@page import="com.xlx.treds.AppInitializer"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
        <%@include file="includes1.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agreement Acceptance</title>

<%
String lDomain=request.getParameter("domain");
Map<String, Object> lData = (Map<String, Object>) request.getAttribute("data");
String lWarningStr=StringEscapeUtils.escapeHtml(request.getParameter(AppConstants.CLICKWRAP_QUERYPARAMETER_WARNING));
boolean lWarning = (lWarningStr!=null?Boolean.parseBoolean(lWarningStr):false);
%>
</head>
<body>
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<script type="text/javascript">
	function displayAgreement(){
		document.getElementById("agreementContents").style.display = '';
		document.getElementById("amendmentContents").style.display = 'none';
	}
	function displayAmendment(){
		document.getElementById("agreementContents").style.display = 'none';
		document.getElementById("amendmentContents").style.display = '';
	}
	function downloadAgreement(){
		downloadFile('puraggacc/downloadclickwrap?<%=AppConstants.CLICKWRAP_QUERYPARAMETER_FILENAME%>=<%=AppConstants.CLICKWRAP_FILECODE_AGREEMENT%>',null,JSON.stringify({"columnNames" : [],"skipLog" : true,"getImage":true }));
	}
	function acceptAgreement(){
    	confirm('You are about to accept the Agreement. Are you sure?','Confirmation','Yes',function(pYes) {
			if (pYes) {
				$.ajax( {
		            url: 'puraggacc/accept',
		            type: "POST",
		            success: function( pObj, pStatus, pXhr) {
						if (loginData.resourceGroup === "<%=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT%>") {
			        		alert("Thank You for accepting the agreement. You will be logged off now. You may continue by logging with the existing credentials.", "Information", function() {
								logout();
			        		});
						}else{
			        		alert("Thank You for accepting the agreement.", "Information", function() {
								location.href = "home";
			        		});
						}
		            },
		        	error: errorHandler,
		        	complete: function() {
		        		//$('#btnSave').prop('disabled',false);
		        	}
		        });	
			}
    	});				
	}
	</script>

	<h1>Agreement / Acceptance</h1>
	<div class="cloudTabs">
		<ul class="cloudtabs nav nav-tabs">
			 <li class="active"><a href="tabAgreement" data-toggle="tab" onClick='javascript:displayAgreement()'>Agreement </a></li>
			 <li  style="display:none;"><a href="tabAmendment" data-toggle="tab" onClick="javascript:displayAmendment()">Amendment </a></li>
	 	</ul>
	</div>

	<div id="agreementContents" style="display:; width:100%; height:300px; overflow:hidden;overflow-y:scroll; margin:2 auto;" >
	<jsp:include page='<%=lData.get("path").toString()%>'>
    	<jsp:param name="title" value="" />
    	<jsp:param name="desc" value="" />
    </jsp:include>	
    </div>

	<hr>
	
	<div>
		<button type="button" class="btn btn-info btn-lg btn-enter" id=btnDnldAgreement onclick="javascript:downloadAgreement();"><span class="fa fa-download"></span> Download Agreement</button>
	</div>

	<hr>

	<div>
		<label class="checkbox"><input type=checkbox id="agreeTAndC" onClick='javascript:document.getElementById("btnAccept").disabled=!document.getElementById("agreeTAndC").checked;'><i>I AGREE TO THE TERMS & CONDITIONS</i></label>
		<button type="button" class="btn btn-info btn-primary btn-lg" id=btnAccept disabled onClick="javascript:acceptAgreement();" ><span class="glyphicon glyphicon-ok"></span> Accept</button>
		<button type="button" class="btn btn-info  btn-lg" id=btnSkip style='display:<%=lWarning?"":"none"%>' onClick='javascript:location.href = "home";' ><span class="glyphicon glyphicon-remove"></span> Skip</button>
	</div>
	
	<hr>
   	<%@include file="footer1.jsp" %>

</body>
</html>