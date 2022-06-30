<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.commonn.bean.LoginBean"%>
<%
String lSecretKey = StringEscapeUtils.escapeHtml(request.getParameter("secretKey"));
%>
<html>
    <head>
        <title>TREDS | Recover Password</title>
        <%@include file="includes.jsp" %>
    </head>
    <body class="skin-blue">
    <jsp:include page="header.jsp">
    	<jsp:param name="title" value="Recover Password" />
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
				<div class="row">
				    <div class="col-sm-4" ><section><label for="newPassword1" class="label">New Password:</label></section></div>
				    <div class="col-sm-8">
				    	<section class="input state-mandatory">
								<input type="password"  autocomplete="off" name="newPassword1" id="newPassword1" placeholder="New Password">
								<b class="tooltip tooltip-bottom-right"></b>
				    	</section>
				      
				    </div>
			  	</div>
				<div class="row">
				    <div class="col-sm-4" ><section><label for="newPassword2" class="label">Reenter Password:</label></section></div>
				    <div class="col-sm-8">
				    	<section class="input state-mandatory">
								<input type="password"  autocomplete="off" name="newPassword2" id="newPassword2" placeholder="Reenter Password">
								<b class="tooltip tooltip-bottom-right"></b>
				    	</section>
				      
				    </div>
			  	</div>
			</fieldset>
			<fieldset class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-primary" id=btnSubmit>Set Password</button>
						 	<button type="button" class="btn btn-default" id=btnClear>Clear</button>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
	</div></div></div>
</div>

    
   	<%@include file="footer1.jsp" %>
	<script type="text/javascript" src="../js/crypto.js"></script>
	
	<script type="text/javascript">
		var publicPage = true;
		var config = <%=BeanMetaFactory.getInstance().getBeanMeta(LoginBean.class).getJsonConfig()%>;
		var mdlForm = null;
		$(document).ready(function() {
			$("#form-login").xform(config);
			mdlForm = $("#form-login").data('xform');
			mdlForm.getField("newPassword1").options.notNull=true;
			mdlForm.getField("newPassword2").options.notNull=true;

			$('#btnSubmit').on('click',function(pEvent) {
				var lErrors = mdlForm.check();
				if ((lErrors == null) || (lErrors.length == 0)) {
	            	var lData = mdlForm.getValue();
	            	if (lData.newPassword1 != lData.newPassword2) {
						alert("New Password does not match with the re entered password.");
						return;
					}
	            	lData.details = "<%=lSecretKey%>";
					$('#btnSubmit').prop('disabled',true);
					$.ajax( {
			            url: "resetpassword",
			            type: "POST",
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
		            		alert("Password set successfully", null, function(){
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
			$('#btnReload').on('click',function(pEvent) {
				setCaptcha();
			});
			mdlForm.focus();
		});
		function setCaptcha() {
			var lImgPath = "captcha/N"+"?"+((new Date()).getTime());
			var lImg$ = $("#imgCaptcha");
			lImg$.error(function(){
				lImg$.unbind('error');
				lImg$.attr('src','');
				$("#rowCaptcha").hide();
			});
			$("#rowCaptcha").show();
			lImg$.attr('src',lImgPath);
		}
	</script>   	
    </body>
</html>