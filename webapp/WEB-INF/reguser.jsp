<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.user.rest.RegisterResource"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Register</title>
        <%@include file="includes1.jsp" %>
        <script>
        	var publicPage = true;
        </script>
    </head>
    <body class="skin-blue">
    <jsp:include page="regheader1.jsp">
    	<jsp:param name="title" value="TREDS Registration Portal" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
    
    <div class="content container" id="contAppUser">
		<div class="row"><div class="col-sm-12 text-right"><a href="reglogin" class="btn btn-info btn-lg btn-enter" style="display:none">Continue With Saved Application</a></div></div>
    	<div class="xform box box-danger" id="frmMain">
    		<div class="box-body">
				<fieldset>
					<div class="row"><div class="col-sm-12"><h4>Entity Details</h4></div></div>
					<div class="row">
						<div class="col-sm-2"><section><label for="companyName" class="label">Name of Applicant Entity:</label></section></div>
						<div class="col-sm-8">
							<section class="input" >
							<input type="text" id="companyName" placeholder="Name of Applicant Entity">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="entityType"><option value="">Select Entity Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="constitution" class="label">Constitution:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="constitution"><option value="">Select Constitution</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					
					<div class="row">
						<div class="col-sm-2"><section><label for="pan" class="label">Permanent Account Number (PAN):</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="pan" placeholder="Permanent Account Number (PAN)">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<hr>
					<div class="row"><div class="col-sm-12"><h4>Login Details</h4></div></div>
					<div class="row">
						<div class="col-sm-2"><section><label for="loginId" class="label">Login Id:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="loginId" placeholder="Login Id">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-4">
							<section class="input">
							<button type="button" class="btn btn-default btn-sm" id="btn-checkLoginId">Check Availability</button>
							</section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="salutation" class="label">Title:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="salutation"><option value="">Title</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="firstName" class="label">First Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="firstName" placeholder="First Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="middleName" class="label">Middle Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="middleName" placeholder="Middle Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="lastName" class="label">Last Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="lastName" placeholder="Last Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="mobile" class="label">Mobile:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="mobile" placeholder="Mobile">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="email" placeholder="Email">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<hr>
					<div class="row">
						<div class="col-sm-2"><section><label for="secretQuestions" class="label">Captcha:</label></section></div>
						<div class="col-sm-4">
	                    	<img src="" id="imgCaptcha" /> <button type="button" class="btn btn-link" id="btnReload"><span class="fa fa-refresh"></span> Reload</button>
	                    	<section class="input">
	                        	<input type="text" id="captcha" placeholder="Enter the characters shown above"/>
	                        </section>
						</div>
					</div>
				</fieldset>
    		</div>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save and Continue</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnHome><span class="fa fa-remove"></span> Back</button>
						</div>
					</div>
				</div>
    		</div>
    	</div>	
	</div>
   	<%@include file="footer1.jsp" %>
	<script type="text/javascript">
		var crudAppUser$ = null;
		var crudAppUser = null;
		var mainForm = null;
		$(document).ready(function() {
			setCaptcha('Y');
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class, RegisterResource.CONFIG_REGUSER).getJsonConfig()%>;
			var lConfig = {
					resource: 'reguser',
					new: true,
						preSaveHandler: function(pObj) {
						var lReenterPass=$('#password2').val();
						if (pObj.password1 != lReenterPass) {
							alert('Password does not match with the re entered password.','Error', function() {
								$('#password2').focus();
							});
							return false;
						}else if (lReenterPass==''){
							alert('Please reenter the password.','Error', function() {
								$('#password2').focus();
							});
							return false;
						}
						var lCaptcha$=$('#captcha');
						if (lCaptcha$.val()=='') {
							alert('Please enter the captcha characters', 'Error', function() {lCaptcha$.focus();});
							return false;
						}
						pObj.domain=lCaptcha$.val();
						// secretQuestions & secretAnswers is a multi value field
						pObj.secretQuestions=[pObj.secretQuestions];
						pObj.secretAnswers=[pObj.secretAnswers];
						return true;
					},
					postSaveHandler: function(pObj) {
						if(loginData.domain=="<%=AppConstants.DOMAIN_PLATFORM%>"){
							location.href="reghome?entityId="+pObj.entityId+'&isProv=true';
						}else{
							postLogin(pObj);
							location.href="company";
						}
						return false;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			contAppUser$ = $('#contAppUser').xcrudwrapper(lConfig);
			contAppUser = contAppUser$.data('xcrudwrapper');
			mainForm = contAppUser.options.mainForm;

/* 			var lFields =  [ 'password2' ];
			mainForm.alterField(lFields, true, false);
			//mainForm.getField('password2').init();
			$('#password2').init(); */
			
			$('#btn-checkLoginId').on('click', function() {
				var lLoginId = $('#loginId').val();
				if (lLoginId == '') {
					alert("Please enter loginid",null,function() {$('#loginId').focus()});
					return;
				}
				$('#btn-checkLoginId').prop('disabled',true);
				$.ajax({
		            url: 'reguser/' + lLoginId,
		            type: 'GET',
		            success: function( pObj, pStatus, pXhr) {
		            	alert("Login Id is available");
		            },
		        	error: errorHandler,
			        complete: function() {
				        $('#btn-checkLoginId').prop('disabled',false);
				    }
		        });
			});
			$('#btnReload').on('click',function(pEvent) {
				setCaptcha('Y');
			});
			$('#btnHome').on('click',function(pEvent) {
				location.href='reglogin';
			});
			$('#entityType').on('change', function() {
				setEntityType(mainForm.getField('entityType').getValue());
			});
		});
		function setCaptcha(pForce) {
			var lImgPath = "captcha/"+pForce+"?"+((new Date()).getTime());
			var lImg$ = $("#imgCaptcha");
			/*lImg$.error(function(){
				lImg$.unbind('error');
				lImg$.attr('src','');
				alert("Unable to load captcha. Please contact system administrator.");
			});*/
			lImg$.attr('src',lImgPath);
			$('#divCaptcha').removeClass('hidden');
		}
		function setEntityType(pVal) {
			if(pVal==null)pVal="";
			var lSupp = pVal && (pVal.length==3) && (pVal.charAt(0)==='Y');
			var lPurc = pVal && (pVal.length==3) && (pVal.charAt(1)==='Y');
			var lFin = pVal && (pVal.length==3) && (pVal.charAt(2)==='Y');
			
			var lFields = {};
			var lEnable = lFin;
			if(!lFin&&!lPurc&&!lSupp) lEnable=true;//if nothing is selected
			lFields =  [ 'constitution' ];
			mainForm.enableDisableField(lFields, !lEnable, true);
			mainForm.alterField(lFields, !lEnable, false);
		}
	</script>   	
    </body>
</html>