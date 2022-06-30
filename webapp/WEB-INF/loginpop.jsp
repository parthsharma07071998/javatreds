<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.AuthenticationHandler"%>
<%@page import="com.xlx.common.registry.RegistryHelper"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.bean.LoginBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
	boolean lChangePassword = request.getParameter("CHGPASS") != null;
String lTitle = lChangePassword?"Change Password":"Login";
String lPasswordLabel = lChangePassword?"Old Password":"Password";
String lDomain = (String)request.getAttribute("domain");
boolean lDomainExists = StringUtils.isNotBlank(lDomain);
Long lMaxLength = RegistryHelper.getInstance().getLong(AuthenticationHandler.REGISTRY_PASSWORD_MAX_LENGTH);
if (lMaxLength == null) lMaxLength = Long.valueOf(30);
%>
		<style>
		#contLogin .grp1, #contLogin .grp2, #contLogin .grp3 {
			padding-top: 10px;
			display: none;
		}
		#contLogin.grp1 .grp1, #contLogin.grp2 .grp2, #contLogin.grp3 .grp3 {
			display: block !important;
		}
		#contLogin {
			padding: 20px; 
			background-color: rgba(255, 255, 255, 0.85);
			border-radius: 3px;
		}
		#contLogin .form-group label {
			padding-bottom:0px;
			margin-bottom:-5px;
		}
		#contLogin input[type=text], #contLogin input[type=password] {
			background:transparent;
			border: 0px;
			border-bottom: 1px solid #ccc;
			color1: #0066a8;
		}
		#contLogin .btn-primary {
			background-color: #14202e;
			color: #ff7b1c;
			font-weight: bold;
			font-size: 18px;
		}
		#contLogin .btn-link {
			color: #14202e;
			font-size: 12px;
		}
		#contLogin .btn-default {
			background-color: transparent;
			color: #ff7b1c;
			font-weight: bold;
			font-size: 18px;
			border-color: #8c8c8c;
		}
		#contLogin #divTitle {
			color: #ff7b1c;
			font-size:22px;
		}
		#contLogin #divTitle div {
			color: #3c8dbc;
			font-size:16px;
		}
		</style>
        <div id="contLogin">
			<div class="row"><div class="col-xs-6"><img src="../images/logo.png" style="width:109px;height:50px"/></div><div class="col-xs-6" id=divTitle></div></div>
            <div id="frmMain" class="xform">
            	<!--  Login tab  -->
				<div class="grp1">
				<h4 style="color:red;margin-left: 0px !important;"><b>Note : </b> Servers will go down for maintenance today, Aug 8th at 9:30 PM (IST). Inconvenience caused is deeply regretted. Servers will be back online by Aug 10th 9:00 AM (IST) </h4>
	                <div >
	                    <div class="form-group">
<%
	if (lDomainExists) {
%>	                        <input type="hidden" name="domain" id="domain"/>
<%
	} else {
%>	                        <label class="label">Member Code:</label><input type="text" name="domain" id="domain" class="form-control" placeholder="Member Code"  autocomplete="off"/>
<%
	}
%>
	                    </div>
	                    <div class="form-group">
	                        <label class="label">Login ID:</label><input type="text" name="login" id="login" class="form-control" placeholder="Login ID" autocomplete="off"/>
	                    </div>
	                </div>
	                <div>  
	                	<button type="button" class="btn btn-primary btn-block" id=btnNext>CONTINUE</button>
<%
	if (!lDomainExists) {
%>
	                	<button type="button" class="btn btn-default btn-block" id=btnRegister onClick="javascript:location.href='reguser'" style="display:none">NEW REGISTRATION</button>
<%
	}
%>
	                    <div class="text-right"><input type="checkbox" id="remember_me"/> Remember me</div>
	                </div>
	                <div class="pull-center">
	                	<a href="javascript:showRemote('<%=request.getContextPath()%>/static/privacy.html','modal-xl scroll-body',false);">Privacy Policy</a> | 
	                	<a href="javascript:showRemote('<%=request.getContextPath()%>/static/disclaimer.html', 'modal-xl scroll-body', false);">Disclaimer</a>
	                </div>
	            </div>
	            
	            
            	<!--  Server verify tab  -->
				<div class="grp2">
	                <div >
	                    <div id="divVerify" class="form-group">
	                        
	                    </div>
		            </div>
	                <div>  
	                	<button type="button" class="btn btn-primary btn-block" id=btnVerify>Ok</button>
	                </div>
		        </div>
		        
		        
            	<!--  Password tab  -->
				<div class="grp3">
	                <div >
	                    <div id="divPassword" class="form-group hidden">
	                        <label class="label">Password:</label><input type="password"  autocomplete="off" name="password" id="password" autocomplete="off" class="form-control" placeholder="<%=lPasswordLabel%>" />
<%
	if (!lChangePassword) {
%>
		                	<div style="width:100%" class="text-right"><button type="button" class="btn btn-link" id=btnForgot>Forgot your password?</button></div>
<%
	}
%>
	                    </div>
						<div id="divChangePassword" class="hidden">
		                    <div class="form-group">
		                        <label class="label">New Password:</label><input type="password"  autocomplete="off" name="newPassword1" id="newPassword1" autocomplete="off" class="form-control" placeholder="New Password"/>
		                    </div>          
		                    <div class="form-group">
		                        <label class="label">Re-enter Password:</label><input type="password"  autocomplete="off" name="newPassword2" id="newPassword2" autocomplete="off" class="form-control" placeholder="Re-enter Password"/>
		                    </div>
	                   	</div>
						<div id="divQuestion">
						</div>
	                    <div class="form-group hidden" id="divCaptcha">
	                    	<img src="data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=" id="imgCaptcha" /> <button type="button" class="btn btn-link" id="btnReload"><span class="fa fa-refresh"></span> Reload</button>
	                        <input type="text" name="captcha" id="captcha" class="form-control" placeholder="Enter the characters shown above"/>
	                    </div>
                    </div>
	                <div>  
	                	<button type="button" class="btn btn-primary btn-block" id=btnLogin><%=lTitle%></button>
<%
	if (!lChangePassword) {
%>
	                	<button type="button" class="btn btn-default btn-block" id=btnBack>Back</button>                                                        
<%
                                                        	}
                                                        %>
	                </div>
                </div>
            </div>

        </div>

   

	<script src="../js/crypto.js?v1"></script>
	<script src="../js/jquery.caret.js?v1"></script>
	
	<script type="text/javascript">
		var DISPLAYMODE_LOGIN = 1;
		var DISPLAYMODE_VERIFICATION = 2;
		var DISPLAYMODE_PASSWORD = 3;
		var crudLogin$ = null;
		var crudLogin = null, mainForm = null;
		var userDetail;// login context
		var fldDomain, fldLogin, fldCaptcha, fldPassword, fldNewPassword1, fldNewPassword2;
		var formMode;// form mode : NORMAL, FORGOTPASSWORD, FORGOT2FA
		var lLocalStorage = '<%=lDomainExists?1:0%>';
		var globalPassword = null;
		var newCaretPosition = null;
		function initializeLogin() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(LoginBean.class).getJsonConfig()%>;
			$.each(lFormConfig.fields,function(pIdx,pVal){
				if ((pVal.name=='password')||(pVal.name=='newPassword1')||(pVal.name=='newPassword2')) {
					pVal.maxLength=<%=lMaxLength%>;
				}
			});
			var lConfig = {
					resource: "login",
					new: true,
					preSaveHandler: function(pData) {
						//oldAlert("userDetail.salt : "+userDetail.salt);
						pData.password = globalPassword;
						pData.encPassword = CryptoJS.SHA1(CryptoJS.SHA1(pData.password)+userDetail.salt).toString();
						pData.password = "";
						pData["<%=AppUserBean.FIELD_MODE%>"]=<%=AppUserBean.MODE_BROWSER%>;
						pData.factor = 1;
						if (userDetail.questions) {
							pData.factor=2;
							appendAnswers(pData);
						}
						return pData;
					},
					postSaveHandler: function(pObj) {
						$('#btnLogin').prop('disabled',false);
		            	if (pObj.questions) {
		            		$.extend(userDetail,pObj);
		            		setDisplayMode(DISPLAYMODE_PASSWORD);
		            		return false;
		            	}
						if (pObj.status=='F') {
							if (pObj.captcha) {
								setCaptcha('N');
								userDetail.captcha = true;
							}
							alert(pObj.reason, "<%=lTitle%> Failed");
						} else {
		            		if ((pObj.reason != null) && (pObj.reason != '')) {
		            			alert(pObj.reason, "Login Message", function() {
				            		if (pObj.status=='P') {
				            			setChangePassword();
				            		}
		            			});
		            		}
							if (pObj.status=='S') {
								closeRemotePage();
								postLogin(pObj,<%=lChangePassword?"false":"true"%>);
								if ( !(pObj.domain==='REGUSER') && (pObj.resourceGroup === "<%=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT%>" || pObj.warnAgreement)) {
									location.href='puraggacc?<%=AppConstants.CLICKWRAP_QUERYPARAMETER_WARNING%>='+(pObj.warnAgreement!=null?pObj.warnAgreement:false)+'&domain='+pObj.domain;
									return false;
								}
								if (pObj.quesMissing) {
									var lMandatory = pObj.resourceGroup === "SECURITY";
									var lMessage = "You have not updated your security settings for 2 factor authentication.<br>";
									if (lMandatory) 
										lMessage += "Please note that 2 factor authentication is mandatory";
									else
										lMessage += "You can configure 2 factor authentication now";
									lMessage += "<br><br>Do you want to configure 2 factor authentication?";
									confirm(lMessage,"Security Settings",null,function(pYes){
									var lUrl = pObj.domain==='REGUSER'?'reghome?isProv=true':'home'; 
										if (pYes) {
											lUrl += "?sec="+(lMandatory?2:1); 
										}
										else if (lMandatory) {
											alert("Login without 2 factor authentication not allowed.", null, function(){
												logout();
											})
											return;
										}
									location.href=lUrl;
									});
								}
								else
									location.href=pObj.domain==='REGUSER'?'reghome?isProv=true':'home';
							}
						}
						return false;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudLogin$ = $('#contLogin').xcrudwrapper(lConfig);
			crudLogin = crudLogin$.data('xcrudwrapper');
			mainForm = crudLogin.options.mainForm;
			//crudLogin.options.btnSave$=$('#btnLogin');
			
			fldDomain = mainForm.getField('domain');
			fldLogin = mainForm.getField('login');
			fldCaptcha = mainForm.getField('captcha');
			fldPassword = mainForm.getField('password');
			fldNewPassword1 = mainForm.getField('newPassword1');
			fldNewPassword2 = mainForm.getField('newPassword2');

			fldDomain.options.label='Member Code';
			fldPassword.options.label='<%=lPasswordLabel%>';
			var lDomain = localStorage.getItem('domain'+lLocalStorage);
			var lLogin = localStorage.getItem('login'+lLocalStorage);
			if ((lDomain != null) || (lLogin != null)) $('#remember_me').prop('checked', true);

			if (loginData && loginData.login) {
				lDomain=loginData.domain;
				lLogin=loginData.login;
			}
<%
if (lDomainExists) {
%>			lDomain="<%=lDomain%>";
<%
}
%>
			fldDomain.setValue(lDomain);
			fldLogin.setValue(lLogin);

			$('#btnReload').on('click',function(pEvent) {
				setCaptcha('Y');
			});
			$('#password').on("cut copy paste",function(e) {
			      e.preventDefault();
			});
			$('#divPassword #password').on('keydown',function(pEvent) {
				console.log("pEvent.keyCode : "+pEvent.keyCode);
				console.log("pEvent.charCode : "+pEvent.charCode);
				var lkey = pEvent.keyCode || pEvent.charCode;
			    if( lkey == 8 || lkey == 46 ) {
			    	pEvent.preventDefault();
			    	var lCursorPosition = $(this).caret();
			    	if (lCursorPosition == null)
			    		return false;
			    	if (lkey == 8) {
			    		if (lCursorPosition == 0)
			    			return false;
			    		var lPasswordArray = Array.from(globalPassword);
			    		var lIndextoReplace =  lCursorPosition - 1;
			    		lPasswordArray.splice(lIndextoReplace, 1);
			    		var lPassword = lPasswordArray.toString();
			    		globalPassword = lPassword.split(",").join("");
			    		newCaretPosition = lIndextoReplace;
			    	} else {
			    		if (lCursorPosition == globalPassword.length)
			    			return false;
			    		var lPasswordArray = Array.from(globalPassword);
			    		lPasswordArray.splice(lCursorPosition, 1);
			    		var lPassword = lPasswordArray.toString();
			    		globalPassword = lPassword.split(",").join("");
			    		newCaretPosition = lCursorPosition;
			    	}
			    }
			});
			$('#divPassword #password').on('keyup',function(pEvent) {
				var lkey = event.keyCode || event.charCode;
			    if( lkey == 8 || lkey == 46 ) {
			    	var lPasswordLength = globalPassword.length;
					var lDummyPassword = null;
					for (var i=0; i<lPasswordLength; i++) {
						if (lDummyPassword == null)
							lDummyPassword = "*";
						else
							lDummyPassword = lDummyPassword+"*";
					}
					mainForm.getField('password').setValue(lDummyPassword);
					if (newCaretPosition != null) {
						$(this).caret(newCaretPosition);
						newCaretPosition = null;
					}
			    }
			});
			$('#divPassword #password').on('keypress',function(pEvent) {
				pEvent.preventDefault();
				var lKey = String.fromCharCode(pEvent.which);
				if (globalPassword == null)
					globalPassword = lKey;
				else
					globalPassword = globalPassword + lKey;
				var lPasswordLength = globalPassword.length;
				var lDummyPassword = null;
				for (var i=0; i<lPasswordLength; i++) {
					if (lDummyPassword == null)
						lDummyPassword = "*";
					else
						lDummyPassword = lDummyPassword+"*";
				}
				mainForm.getField('password').setValue(lDummyPassword);
			});
			$('#btnLogin').on('click',function(pEvent) {
				if (formMode == <%=AuthenticationHandler.MODE_LOGIN%>) {
					$('#btnLogin').prop('disabled',true);
					if (!crudLogin.saveHandler()) {
						$('#btnLogin').prop('disabled',false);
					}
				} else {
					var lErrors = mainForm.check();
					if ((lErrors != null) && (lErrors.length > 0)) {
						crudLogin.showError();
						return;
					}
					var lData = mainForm.getValue();
					lData.password = globalPassword;
					var lUrl;
					if (formMode==<%=AuthenticationHandler.MODE_FORGOTPASSWORD%>) {
						appendAnswers(lData);
						lUrl = "forgotpassword";
					} else {
						lData.encPassword = CryptoJS.SHA1(CryptoJS.SHA1(lData.password)+userDetail.salt).toString();
						lData.password = "";
						lUrl = "forgot2fa";
					}

					$('#btnLogin').prop('disabled',true);
					$.ajax( {
			            url: lUrl,
			            type: "POST",
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
			            	globalPassword = null;
			            	closeRemotePage();
			            	alert(pObj.message);
			            	setDisplayMode(DISPLAYMODE_LOGIN);
			            },
			        	error: function(pXhr, pStatus, pError) {
			        		globalPassword = null;
			        		setCaptcha('Y');
			        		userDetail.captcha = true;
			        		errorHandler(pXhr, pStatus, pError);
			        	},
			        	complete: function() {
			        		$('#btnLogin').prop('disabled',false);
			        	}
			        });	
				}
			});
			$('#btnNext').on('click',function(pEvent) {				
				getUserDetails(<%=AuthenticationHandler.MODE_LOGIN%>);
			});
			
			$('#btnForgot').on('click',function(pEvent) {
				getUserDetails(<%=AuthenticationHandler.MODE_FORGOTPASSWORD%>);
			});
			$('#btnVerify').on('click',function(pEvent) {
				setDisplayMode(DISPLAYMODE_PASSWORD);
			});
			$('#btnBack').on('click',function(pEvent) {
				globalPassword = null;
				setDisplayMode(DISPLAYMODE_LOGIN);
			});
			setDisplayMode(DISPLAYMODE_LOGIN);
<%if (lChangePassword) {%>
			if ((fldDomain.getValue()!="") && (fldLogin.getValue()!=""))
				$('#btnNext').trigger('click');
<%}%>
		}
		function setDisplayMode(pMode) {
			$('#contLogin').removeClass('grp1 grp2 grp3');
			$('#contLogin').addClass('grp'+pMode);
			if (pMode==DISPLAYMODE_LOGIN) {
				userDetail={};
				$('#divTitle').html("<%=lChangePassword?"Change Password":"Log in<div>to your account</div>"%>");
				fieldMandatory([fldPassword,fldCaptcha,fldNewPassword1,fldNewPassword2], false);
				fldDomain.focus();
			} else if (pMode==DISPLAYMODE_VERIFICATION) {
				$('#divTitle').html("Server Verfication");
        		$('#btnVerify').focus();
            	var lSecurityTxt = "";
            	if (userDetail.secretText) lSecurityTxt += "<h4>Verification String:</h4><label>" + htmlEscape(userDetail.secretText) + "</label>";
            	if (userDetail.secretImage) lSecurityTxt += "<h4>Verification Image</h4><img class='img-responsive' src='upload/VERIFICATIONIMAGES/" + userDetail.secretImage + "' />";
        		$('.grp2 #divVerify').html(lSecurityTxt);
			} else if (pMode==DISPLAYMODE_PASSWORD) {
				$('#btnLogin').prop('disabled',false);
				var lTitle = <%=lDomainExists?"fldLogin.getValue()":"fldDomain.getValue() + ' ' + fldLogin.getValue()"%>
				$('#divTitle').html(lTitle);
				// questions div
        		var lHtml = '';
				if (userDetail.questions) {
<%if (!lChangePassword) {%>					
	        		if (formMode==<%=AuthenticationHandler.MODE_LOGIN%>)
        				lHtml+='<div class="form-group text-right"><button type="button" class="btn btn-link" onClick="javascript:getUserDetails(<%=AuthenticationHandler.MODE_FORGOT2FA%>)">Forgot 2FA?</button></div>';                                                        
<%}%>
        			$.each(userDetail.questions, function(pIndex, pValue){
	        			lHtml+='<div class="form-group"><label class="text-left">'+htmlEscape(pValue.question)+'</label></div>';
	        			lHtml+='<div class="form-group"><input type="text" id="securityAnswer'+pIndex+'" class="form-control" placeholder="Answer"></div>';
	        		});
        			lHtml+='<div class="form-group text-right"><button type="button" class="btn btn-link" onClick="javascript:getUserDetails(<%=AuthenticationHandler.MODE_GET2FA%>)">Resend</button></div>';                                                        
				}
        		$('#divQuestion').html(lHtml);
        		$('#divQuestion').removeClass('hidden');
            	// login btn label
				if (formMode==<%=AuthenticationHandler.MODE_LOGIN%>) {
            		$('#btnLogin').html('<%=lTitle%>');
            	} else if (formMode==<%=AuthenticationHandler.MODE_FORGOTPASSWORD%>) {
            		$('#btnLogin').html('Recover Password')
            	} else if (formMode==<%=AuthenticationHandler.MODE_FORGOT2FA%>) {
            		$('#btnLogin').html('Clear 2FA Settings');
            	}
				// password fld
				fieldMandatory(fldPassword, (formMode==<%=AuthenticationHandler.MODE_LOGIN%>) 
						|| (formMode==<%=AuthenticationHandler.MODE_FORGOT2FA%>));
				if ((userDetail.questions==null) && (formMode==<%=AuthenticationHandler.MODE_LOGIN%>)) {
					$('#divPassword').removeClass('hidden');
					fldPassword.setValue("");
					fldNewPassword1.setValue(null);
					fldNewPassword2.setValue(null);
					fldPassword.focus();
				} else {
					$('#divPassword').addClass('hidden');
					$('#divChangePassword').addClass('hidden');
				}
				// captcha
				if (userDetail.captcha)
            		setCaptcha('N');
            	else {
            		fieldMandatory(fldCaptcha, false);
            		$('#divCaptcha').addClass('hidden');
            	}
			}
		}
		function fieldMandatory(pFld,pFlag) {
			if (pFld) {
				if ($.isArray(pFld)) {
					$.each(pFld,function(pIndex, pValue){
						pValue.options.notNull = pFlag;
						pValue.options.notBlank = pFlag;
					});
				} else {
					pFld.options.notNull = pFlag;
					pFld.options.notBlank = pFlag;
				}
			}
		}
		function getUserDetails(pMode) {
			fieldMandatory([fldPassword,fldCaptcha,fldNewPassword1,fldNewPassword2], false);
			var lErrors = mainForm.check();
			if ((lErrors != null) && (lErrors.length > 0)) {
				crudLogin.showError();
				return;
			}
			if ($('#remember_me').is(':checked')) {
				localStorage.setItem('domain'+lLocalStorage,fldDomain.getValue());
				localStorage.setItem('login'+lLocalStorage,fldLogin.getValue());
			} else {
				localStorage.removeItem('domain'+lLocalStorage);
				localStorage.removeItem('login'+lLocalStorage);
			}
			var lDomain = fldDomain.getValue();
			var lLoginId = fldLogin.getValue();
			if(lDomain != null)
				lDomain = lDomain.toUpperCase();
			if(lLoginId != null)
				lLoginId = lLoginId.toUpperCase();
			var lUrl = "login/"+lDomain+"/"+lLoginId+"?mode="+pMode;
			$.ajax( {
	            url: lUrl,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	userDetail=pObj;
	            	if (pMode != <%=AuthenticationHandler.MODE_GET2FA%>)
	            		formMode = pMode;
	            	else {
	            		alert("OTP resent successfully.");
<%if (lChangePassword) {%>
	            		return;
<%}%>
	            	}
<%if (lChangePassword) {%>
					setDisplayMode(DISPLAYMODE_PASSWORD);
         		    setChangePassword();
<%} else {%>
         		    $('#divChangePassword').addClass('hidden');
         		    setDisplayMode(((userDetail.secretText||userDetail.secretImage)&&(formMode==<%=AuthenticationHandler.MODE_LOGIN%>))
         		    	?DISPLAYMODE_VERIFICATION:DISPLAYMODE_PASSWORD);
<%}%>
	            },
	        	error: errorHandler,
	        });	
		}
		function appendAnswers(pData) {
			pData.securityAnswers=[];
			var lValid = true;
			if (userDetail.questions != null) {
				$.each(userDetail.questions,function(pIndex,pValue){
					var lField$ = $('#securityAnswer'+pIndex);
					var lAnswer = lField$.val();
					if (lAnswer == '') {
						alert("Please enter answer to the question.",null, function(){lField$.focus()});
						lValid = false;
						return false;
					}
					pData.securityAnswers.push(lAnswer);
				})
			}
			return lValid;
		}
		function setChangePassword() {
			$('#divQuestion').addClass('hidden');
			$('#divChangePassword').removeClass('hidden');
			fieldMandatory([fldNewPassword1, fldNewPassword2], true);
			$('#btnLogin').html('Change Password')
			fldNewPassword1.focus();
		}
		function setCaptcha(pForce) {
			var lImgPath = "captcha/"+pForce+"?"+((new Date()).getTime());
			var lImg$ = $("#imgCaptcha");
			lImg$.off("error").on( "error", function(){
				lImg$.off('error');
				lImg$.attr('src','data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=');
				alert("Unable to load captcha. Please contact system administrator.");
			});
			lImg$.attr('src',lImgPath);
			fldCaptcha.setValue(null);
			fieldMandatory(fldCaptcha, true);
			$('#divCaptcha').removeClass('hidden');
		}
		initializeLogin();
	</script>		
