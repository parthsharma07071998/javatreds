<%@page import="com.xlx.common.registry.bean.RefCodeValuesBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.xlx.commonn.user.bean.IAppUserBean"%>
<%@page import="com.xlx.common.registry.RefMasterHelper"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.bean.LoginBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
		<style>
		#contSecurity .divScroll {
			border:1px solid #ccc;
			padding:8px;
		}
		#contSecurity .grp1, #contSecurity .grp2, #contSecurity .grp3, #contSecurity .grp4, #contSecurity .grp5 {
			display: none;
		}
		#contSecurity.grp1 .grp1, #contSecurity.grp2 .grp2, #contSecurity.grp3 .grp3, #contSecurity.grp4 .grp4, #contSecurity.grp5 .grp5 {
			display: block !important;
		}
		#contSecurity.form-box {
			width:100% !important;
			margin-top:0px !important;
		}
		div {
			  padding-top: 5px;
			  padding-right: 5px;
			  padding-bottom: 5px;
			  padding-left: 5px;
		}
		</style>
        <div class="form-box" id="contSecurity">
            <div id="frmMain" class="xform" >
            	<!--  Server Verification tab  -->
				<div class="grp1">
	                <div class="body">
	                	Following settings help in preventing you from fishing attacks. 
	                	Every time you login we will present the secret verification text and image configured by you,
	                	thus allowing you to verify our servers.
	                	<br>
	                    <div class="form-group">
	                        <input type="text" name="secretText" id="secretText" class="form-control" placeholder="Verfication Text (Any text)"/>
	                    </div>
	                    <div class="form-group">
							Verification Image
							<input type="hidden" id="secretImage" data-role="xuploadfield" data-file-type="VERIFICATIONIMAGES" />
							<div>
								<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
								<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
								<span class="upl-info"></span>
								<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
							</div>
							<div style="width:50%">
								<img class="upl-img-preview" />
							</div>
	                    </div>
	                </div>
	                <div class="footer">  
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnNext" >Next</button>
	                </div>
	            </div>
	            
	            
            	<!--  Two factor authentication tab  -->
				<div class="grp2">
	                <div class="body">
	                	<div class="divScroll">
		                	2 Factor authentication is an additional security measure in addition to the login id and password. 
		                	During every login we will prompt a question to you randomly selected from the set of questions provided by you. 
		                	You will be required to provide correct answer to that question.
		                	<br>Please provide answers to following set of questions.
	                	</div>
	                	<br>
	                    <div class="form-group"><div class="divScroll" id="divQuestions">
<%
ArrayList<RefCodeValuesBean> lList = RefMasterHelper.getInstance().getRefCodeValues(IAppUserBean.REFCODE_SECURITY_QUESTION);
for (RefCodeValuesBean lRefCodeValuesBean : lList) {
%>	                    <div><%=lRefCodeValuesBean.getDesc() %></div>
						<div class="form-group">
	                        <input type="text" id="secretAnswer-<%=lRefCodeValuesBean.getValue() %>" data-question="<%=lRefCodeValuesBean.getValue() %>" class="form-control"/>
	                    </div>
<%
}
%>
	                    </div></div>          
	                   	
                    </div>
	                <div class="footer">  
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnNext">Next</button>
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnPrev">Back</button>
	                </div>
                </div>
				<div class="grp3">
	                <div class="body">
	                	<div class="divScroll">
		                	2 Factor authentication is an optional security measure in addition to the login id and password. 
		                	During every login we will send a random one time password (OTP) to your email and/or mobile using SMS. 
		                	You will be required to provide the OTP to be able to login successfully.
		                	<br>Please provide your email Ids and mobile numbers for the same.
	                	</div>
	                	<div class="form-group"><div class="divScroll">
		                    <div class="form-group">
		                    <div>Email Id (Separate by commas in case of multiple)</div>
							<div class="form-group">
		                        <input type="text" id="otpEmail" data-question="Email Id" class="form-control"/>
		                    </div>
		                    </div>          
		                    <div class="form-group">
		                    <div>Mobile Number (Separate by commas in case of multiple)</div>
							<div class="form-group">
		                        <input type="text" id="otpMobile" data-question="Mobile Number" class="form-control"/>
		                    </div>
		                    </div>
	                	</div></div>
                    </div>
	                <div class="footer">  
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnNext">Next</button>
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnPrev">Back</button>
	                </div>
                </div>
				<div class="grp4">
	                <div class="body">
	                	<div class="divScroll">
		                	2 Factor authentication is an optional security measure in addition to the login id and password. 
		                	During every login an OTP will be generated using RSA based OTP generator mobile app. 
		                	You will be required to provide the OTP to be able to login successfully.
		                	<br>Please provide the secret random key.
	                	</div>
	                	<div class="form-group"><div class="divScroll1">
		                    <div class="form-group">
			                    <div>Secret Key</div>
								<div class="form-group" id="-divSecSettTokenQr-"></div>
			                    <input type="hidden" id="rsaTokenKey" data-question="Secret Key (Base32)" class="form-control"/>
			                    <div><button type="button" class="btn bg-olive btn-primary btn-block btnGenTokenUri">Generate</button></div>
		                    </div>          
	                	</div></div>
	                   	
                    </div>
	                <div class="footer">  
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnNext">Next</button>
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnPrev">Back</button>
	                </div>
                </div>
				<div class="grp5">
	                <div class="footer">  
	                	<button type="button" class="btn bg-olive btn-primary btn-block" id=btnSave>Update Security Settings</button>
	                	<button type="button" class="btn bg-olive btn-primary btn-block btnPrev">Back</button>
	                </div>
                </div>
            </div>

        </div>

	
	<script type="text/javascript">
		var crudSecurity$ = null;
		var crudSecurity = null, mainFormSecurity = null;
		var dataSec = null;
		var groups = [];
		var groupIdx = -1;
		function initializeSecurity() {
			var setQrCode=function(pQrCode) {
				if(pQrCode != null && pQrCode != "") {
					showQrCode("-divSecSettTokenQr-",pQrCode);
				} else {
					$('-divSecSettTokenQr-').html("");
				}	
				console.log("pQrCode : "+pQrCode, new Date().getTime());
				$('#rsaTokenKey').val(pQrCode==null?"":pQrCode);
				console.log( $('#rsaTokenKey').val() );
			}
			function generateTokenUri() {
				$.ajax( {
		            url: 'security/gentokenuri',
		            type: "POST",
		            data:JSON.stringify({}),
		            success: function( pObj, pStatus, pXhr) {
		            	console.log(pObj);
		            	setQrCode(pObj.uri);
		            },
		        	error: errorHandler,
					complete: function() {
						//$('#btnReset2FA').prop('disabled',false);
					}
		        });		
			}
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class, null, "security").getJsonConfig()%>;
			lFormConfig.fieldGroups.update=lFormConfig.fieldGroups.security;
			var lConfig = {
					resource: "security",
					modify: [0],
					postModifyHandler: function(pObj) {
						console.log(pObj, new Date().getTime());
						dataSec = pObj;
						$('#divQuestions input').val('');
						groupIdx = -1;
						groups = [];
						if (dataSec.enableServerVerification) {
							groups.push(1);
							console.log("push 1", new Date().getTime());
						}
						if (dataSec.enable2FAQuestions) {
							if (dataSec.secretQuestions) {
								$.each(dataSec.secretQuestions, function(pIndex,pValue){
									$('#secretAnswer-'+pValue).val(dataSec.secretAnswers[pIndex]);
								});
								groups.push(2);
								console.log("push 2", new Date().getTime());
							}
						}
						if (dataSec.enable2FAOtp) {
							$('#otpEmail').val(dataSec.otpEmail);
							$('#otpMobile').val(dataSec.otpMobile);
							groups.push(3);
							console.log("push 3", new Date().getTime());
						}
						console.log("dataSec.enable2FAToken : "+dataSec.enable2FAToken, new Date().getTime());
						console.log("dataSec.rsaTokenKey : "+dataSec.rsaTokenKey, new Date().getTime());
						if (dataSec.enable2FAToken) {
							console.log("push 4 start", new Date().getTime());
							setQrCode(dataSec.rsaTokenKey);													
							groups.push(4);
							console.log("push 4 done", new Date().getTime());
						}
						groups.push(5);
						console.log("push 5", new Date().getTime());
						showPage(1);
					},
					preSaveHandler: function(pData) {
						if (dataSec.enable2FAQuestions) {
							pData.secretQuestions=[];
							pData.secretAnswers=[];
							$('#divQuestions input').each(function(pIndex) {
								var lThis$=$(this);
								var lAns = lThis$.val();
								if (lAns != "") {
									pData.secretQuestions.push(lThis$.data('question'));
									pData.secretAnswers.push(lAns)
								}
							});
						}
						if (dataSec.enable2FAOtp) {
							pData.otpEmail = $('#otpEmail').val();
							pData.otpMobile = $('#otpMobile').val();
						}
						console.log("ps dataSec.enable2FAToken : "+dataSec.enable2FAToken, new Date().getTime());
						console.log("ps1 dataSec.rsaTokenKey : "+dataSec.rsaTokenKey, new Date().getTime());
						console.log( $('#rsaTokenKey').val() );
						if (dataSec.enable2FAToken) {
							pData.rsaTokenKey = $('#rsaTokenKey').val();
						}
						console.log("ps2 dataSec.rsaTokenKey : "+dataSec.rsaTokenKey, new Date().getTime());
						return true;
					},
					postSaveHandler: function(pData) {
						alert("Security settings updated successfully", null, function(){
							closeRemotePage();
							if (loginData.resourceGroup === "SECURITY") {
								alert("Kindly login again with your new security preferences.", null, logout);
								
							}
						});
						return false;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudSecurity$ = $('#contSecurity').xcrudwrapper(lConfig);
			crudSecurity = crudSecurity$.data('xcrudwrapper');
			mainFormSecurity = crudSecurity.options.mainForm;
			$('#contSecurity .btnNext').on('click', function() {
				showPage(1);
			});
			$('#contSecurity .btnPrev').on('click', function() {
				showPage(-1);
			});
			$('#contSecurity .divScroll').slimscroll({
		        alwaysVisible: true,
		        size: "10px",
		        height: "120px"
		    });
			$("#contSecurity .btnGenTokenUri").off('click').on('click', function() {
				generateTokenUri();
			});
		}
		function showPage(pOffset) {
			groupIdx += pOffset;
			if (groupIdx < 0) {
				groupIdx = 0;
				return;
			}
			if (groupIdx >= groups.length) {
				groupIdx = groups.length-1;
				return;
			}
			//1=domain+loginid,2=verify server,3=password+2fa*+captcha*,4=password+2fa*+captcha*+newpass*
			$('#contSecurity').removeClass('grp1 grp2 grp3 grp4 grp5');
			$('#contSecurity').addClass('grp'+groups[groupIdx]);
		}
		initializeSecurity();
	</script>		
