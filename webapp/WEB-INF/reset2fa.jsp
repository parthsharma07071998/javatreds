<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.LoginBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lSecretKey = StringEscapeUtils.escapeHtml(request.getParameter("secretKey"));
%>
<html>
    <head>
        <title>Reset 2FA Settings</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/dataTables.bootstrap.css" rel="stylesheet">
    </head>
    <body class="skin-blue">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Reset 2FA Settings" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

    	
<div class="content">
	<div id="contTestUser"><div class="row"><div class="col-sm-6 col-sm-offset-3">
		<div class="xform box box-danger" id="form-login">
			<fieldset class="box-body">
				<div class="row">
					<div class="col-sm-4" ><section><label for="domain" class="label">Member Code:</label></section></div>
				    <div class="col-sm-8" >
				    	<section class="input">
								<input type="text" name="domain" id="domain" placeholder="Member Code">
								<b class="tooltip tooltip-bottom-right"></b>
						</section>
				      
				    </div>
				</div>
				<div class="row">
					<div class="col-sm-4" ><section><label for="login" class="label">Login:</label></section></div>
				    <div class="col-sm-8" >
				    	<section class="input">
								<input type="text" name="login" id="login" placeholder="Login Id">
								<b class="tooltip tooltip-bottom-right"></b>
						</section>
				      
				    </div>
				</div>
			</fieldset>
			<fieldset class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-primary" id=btnSubmit>Reset 2FA Settings</button>
						 	<button type="button" class="btn btn-default" id=btnClear>Clear</button>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
	</div></div></div>
</div>
    	
    	
    	<%@include file="footer1.jsp" %>
	   	<script src="../js/bootstrap-datetimepicker.js"></script>
	   	<script src="../js/datatables.js"></script>
		<script src="../js/dataTables.bootstrap.js"></script>
	<script type="text/javascript" src="../js/crypto.js"></script>
	
	<script type="text/javascript">
		var publicPage = true;
		var config = <%=BeanMetaFactory.getInstance().getBeanMeta(LoginBean.class).getJsonConfig()%>;
		var mdlForm = null;
		$(document).ready(function() {
			$("#form-login").xform(config);
			mdlForm = $("#form-login").data('xform');

			$('#btnSubmit').on('click',function(pEvent) {
				var lErrors = mdlForm.check();
				if ((lErrors == null) || (lErrors.length == 0)) {
	            	var lData = mdlForm.getValue();
	            	lData.details = "<%=lSecretKey%>";
					$('#btnSubmit').prop('disabled',true);
					$.ajax( {
			            url: "reset2fa",
			            type: "POST",
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
		            		alert("2FA Settings cleared successfully", null, function(){
		            			location.href = "home";
		            		});
			            },
			        	error: errorHandler,
						complete: function() {
						    $('#btnSubmit').prop('disabled',false);
						}
			        });
				}
			});
			$('#btnClear').on('click',function(pEvent) {
				mdlForm.setValue(null);
			});
			mdlForm.focus();
		});
	</script>   	
    </body>
</html>