<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.EntityNotificationSettingBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lParamEntityCode = (String)request.getAttribute("code");
String lParamEntityName = (String)request.getAttribute("name");
%>
<html lang="en">
	<head>
		<title>TREDS | Notifications</title>
		<%@include file="includes1.jsp" %>
	</head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="OTP Notification Settings" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	<div class="container" id="contEntityNotificationSetting">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">OTP Notification Settings <%=(lParamEntityCode!=null && lParamEntityName!=null)?"("+lParamEntityCode+" - "+lParamEntityName+")":"" %></h1>
			</div>
<% 
	if (lParamEntityCode!=null && lParamEntityName!=null) 
		{ 
%>
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" id=btnAppEntities style=''><span class="fa fa-user"></span> Back to Entities</a>
					<a></a>
				</div>
			</div>
<%
		};
%>
		</div>
		
		<div style="display:none" id="frmSearch">
		</div>
		<div style="display:none" id="frmMain">
		</div>
	</div>
	
	<div class="modal fade" id="mdlCommunication" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Edit </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmEditCommunication">
			<fieldset>
				<div class="row" id="Sms">
					<div class="col-sm-4"><section><label for="mobileList" class="label">Mobile No:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<div class="input-group"><input type="text" id="mobileList" placeholder="Mobile No">
						<span class="input-group-btn"><button class="btn btn-default btn-sm" type="button">Add</button></span>
						<b class="tooltip tooltip-top-right"></b></div>
						<div class="value-list"></div></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="Email">
					<div class="col-sm-4"><section><label for="emailList" class="label">Email:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<div class="input-group"><input type="text" id="emailList" placeholder="Email">
						<span class="input-group-btn"><button class="btn btn-default btn-sm" type="button">Add</button></span>
						<b class="tooltip tooltip-top-right"></b></div>
						<div class="value-list"></div></section>
						<section class="view"></section>
					</div>
				</div>
				<div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-back"></span> Back</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>
	
   	<script id="tplList" type="text/x-handlebars-template">
		<div class="cloudTabs">
			<ul class="cloudtabs nav nav-tabs">
{{#each meta}}
				<li><a href="#tab{{idx}}" data-toggle="tab">{{cat}}</a></li>
{{/each}}
			</ul>
		</div>
		<div class="tab-content">
{{#each meta}}
			<div class="tab-pane panel panel-default" id="tab{{idx}}">
				<table class="table table-hover">
				<thead><tr>
					<th width="15%">Event</th>
					<th width="17%">Info</th>
					<th width="8%">Implicit Mobile No.</th>
					<th width="3%"></th>
					<th width="13%">Explicit Mobile No.</th>
					<th width="8%">Implicit Email.</th>
					<th width="3%"></th>
					<th width="17%">Explicit Email.</th>
				</tr></thead>
				<tbody>
{{#each list}}
					<tr>
						<td>{{description}}</td>
						<td>{{messageInfo}}</td>
						{{#ifCond smsImplicit '==' smsExplicit}} 
							<td>
							<input type=checkbox id='chkSmsImp{{notificationType}}' onClick="javascript:toggleNotifications('chkSmsImp','{{notificationType}}','Sms');saveNotifications('{{notificationType}}')" >
							{{MessageInfo}}
							</td>
							<td>
							<input type=checkbox id='chkSmsExp{{notificationType}}' onClick="javascript:toggleNotifications('chkSmsExp','{{notificationType}}','Sms');saveNotifications('{{notificationType}}')" >
							</td>
						{{else}}  
							<td>	
							{{#if smsImplicit}}
							<input type=checkbox id='chkSmsImp{{notificationType}}' onClick="javascript:toggleNotifications('chkSmsImp','{{notificationType}}','Sms');saveNotifications('{{notificationType}}')" >
							{{messageInfo}}
							{{/if}}
							</td>
							<td>{{#if smsExplicit}}
							<input type=checkbox id='chkSmsExp{{notificationType}}' onClick="javascript:toggleNotifications('chkSmsExp','{{notificationType}}','Sms');saveNotifications('{{notificationType}}')" >
							{{/if}}
							</td>	
						{{/ifCond}}	
						<td>
							{{#if smsExplicit}}
							<ul style="list-style-type: none;padding:0px;margin:0px;" id='lstSms{{notificationType}}'>
							</ul>
							<button class="btn btn-sm btn-info" onClick="javascript:editList('{{notificationType}}','Sms');"><span class="fa fa-pencil"></span></button>
							{{/if}}
						</td>

						{{#ifCond emailImplicit '==' emailExplicit}} 
							<td>
							<input type=checkbox id='chkEmailImp{{notificationType}}' onClick="javascript:toggleNotifications('chkEmailImp','{{notificationType}}','Email');saveNotifications('{{notificationType}}')" >
							{{MessageInfo}}
							</td>
							<td>
							<input type=checkbox id='chkEmailExp{{notificationType}}' onClick="javascript:toggleNotifications('chkEmailExp','{{notificationType}}','Email');saveNotifications('{{notificationType}}')" >
							</td>
						{{else}}  
							<td>	
							{{#if emailImplicit}}
							<input type=checkbox id='chkEmailImp{{notificationType}}' onClick="javascript:toggleNotifications('chkEmailImp','{{notificationType}}','Email');saveNotifications('{{notificationType}}')" >
							{{messageInfo}}
							{{/if}}
							</td>
							<td>{{#if emailExplicit}}
							<input type=checkbox id='chkEmailExp{{notificationType}}' onClick="javascript:toggleNotifications('chkEmailExp','{{notificationType}}','Email');saveNotifications('{{notificationType}}')" >
							{{/if}}
							</td>	
						{{/ifCond}}	
						<td>
							{{#if emailExplicit}}
							<ul style="list-style-type: none;padding:0px;margin:0px;" id='lstEmail{{notificationType}}'>
							</ul>
							<button class="btn btn-sm btn-info" onClick="javascript:editList('{{notificationType}}','Email');"><span class="fa fa-pencil"></span></button>
							{{/if}}
						</td>


					</tr>
{{/each}}
				</tbody>
				</table>
			</div>
{{/each}}
		</div>
	</script>
	<%@include file="footer1.jsp" %>
	<script type="text/javascript">
		var crudEntityNotificationSetting$ = null;
		var tplList;
		var communicationForm$ = null, communicationForm=null;
		var myData = null;
		var lEntityCode = '<%=lParamEntityCode%>';
		$(document).ready(function() {
			tplList = Handlebars.compile($('#tplList').html());

			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(EntityNotificationSettingBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "entotpnot",
					autoRefresh: true,
					preSearchHandler: function(pFilter) {
						pFilter.code='<%=lParamEntityCode%>';
						return true;
					},
					postSearchHandler:function (pData) {
						myData = pData;
						$('#contEntityNotificationSetting #frmSearch').html(tplList(pData));
						$('.nav-tabs a:first').tab('show');
						//loop through the meta and fetch the data from hash and set the controls appropriately.
						$.each(pData.meta,function(pIdx,pVal){
							//looping throught the tab-list which contains list of the events (pVal.list=tablist)
							$.each(pVal.list,function(pIdx,pListVal){
								var lNotificationType = pListVal.notificationType; 
								//fetching data from the data
								var lData = pData.data[lNotificationType]; 
								//we are not skipping the rest if data is null because we assume that if data is not present then the user has ticked the same.
								var lMessageType = null; 
								var lImplicit = null;
								var lExplicit = null;
								//FOR SMS
								if(lData!=null && lData.smsMessageType!=null){
									lMessageType = lData.smsMessageType;
								}
								lImplicit = (lMessageType!=null && (lMessageType=='<%=AppConstants.MessageType.Both.getCode()%>') || (lMessageType=='<%=AppConstants.MessageType.Implicit.getCode()%>'));
								lExplicit = (lMessageType!=null && ((lMessageType=='<%=AppConstants.MessageType.Both.getCode()%>') || (lMessageType=='<%=AppConstants.MessageType.Explicit.getCode()%>')));
								//this determines whether we have controls or not
								if(pListVal.smsImplicit && $('#chkSmsImp'+lNotificationType)!=null){
									$('#chkSmsImp'+lNotificationType).attr('checked', lImplicit);
								}
								if(pListVal.smsExplicit && $('#chkSmsExp'+lNotificationType)!=null){
									$('#chkSmsExp'+lNotificationType).attr('checked', lExplicit);
									//setting the mobile list
									if($('#lstSms'+lNotificationType)!=null){
										if(lData!=null && lData.mobileList!=null&&lData.mobileList!=''){
											var lMobiles = lData.mobileList; //its an array
											for(var lPtr=0; lPtr < lMobiles.length; lPtr++){
												 $('#lstSms'+lNotificationType).append("<li>"+lMobiles[lPtr]+"</li>");
											}
										}
									}
								}
								//FOR EMAIL
								lMessageType = null; 
								if(lData!=null && lData.emailMessageType!=null){
									lMessageType = lData.emailMessageType;
								}
								lImplicit = (lMessageType!=null && (lMessageType=='<%=AppConstants.MessageType.Both.getCode()%>') || (lMessageType=='<%=AppConstants.MessageType.Implicit.getCode()%>'));
								lExplicit = (lMessageType!=null && ((lMessageType=='<%=AppConstants.MessageType.Both.getCode()%>') || (lMessageType=='<%=AppConstants.MessageType.Explicit.getCode()%>')));
								//this determines whether we have controls or not
								if(pListVal.emailImplicit && $('#chkEmailImp'+lNotificationType)!=null){
									$('#chkEmailImp'+lNotificationType).attr('checked', lImplicit);
								}
								if(pListVal.emailExplicit && $('#chkEmailExp'+lNotificationType)!=null){
									$('#chkEmailExp'+lNotificationType).attr('checked', lExplicit);
									//setting the email list
									if($('#lstEmail'+lNotificationType)!=null){
										if(lData!=null && lData.emailList!=null&&lData.emailList!=''){
											var lEmails = lData.emailList; //its an array
											for(var lPtr=0; lPtr < lEmails.length; lPtr++){
												 $('#lstEmail'+lNotificationType).append("<li>"+lEmails[lPtr]+"</li>");
											}
										}
									}
								}

								
							});
						});
						return false;
					}
					
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudEntityNotificationSetting$ = $('#contEntityNotificationSetting').xcrudwrapper(lConfig);
			crudEntityNotificationSetting = crudEntityNotificationSetting$.data('xcrudwrapper');
			searchForm = crudEntityNotificationSetting.options.searchForm;
			$('#btnAppEntities').on('click', function() {
				location.href="appentity";
			});
			var lMobileConfig = {
					"fields": [
							{
								"name": "notificationType",
								"label": "Notification Type",
								"dataType": "STRING",
								"maxLength": 50,
								"fieldType":"PRIMARY",
								"notNull": true
							},
							{
								"name": "mobile",
								"label": "Mobile",
								"dataType": "STRING",
								"maxLength": 500,
								"nonJson":true,
								"jsonField":"mobileList"
							},
							{
								"name": "mobileList",
								"label": "Mobile",
								"dataType":"STRING",
								"maxLength": 50,
								"nonDatabase":true,
								"databaseField":"mobile",
								"allowMultiple": true,
								"maxItems":1,
								"pattern": "^(\\+?\\d{1,4}[\\s-])?(?!0+\\s+,?$)[1-9][0-9]{9}\\s*,?$",
								"patternMessage":"Please ensure the it is a mobile number."
							},
							{
								"name":"smsMessageType",
								"label":"Sms Message Type",
								"dataType": "STRING",
								"maxLength": 1,
								"notNull": true,
								"dataSetType":"ENUM",
								"dataSetValues":"com.xlx.treds.AppConstants.MessageType"
							},
							{
								"name": "email",
								"label": "Email",
								"dataType": "STRING",
								"maxLength": 50,
								"nonJson":true,
								"jsonField":"emailList"
							},
							{
								"name": "emailList",
								"label": "Email",
								"dataType":"STRING",
								"maxLength": 50,
								"nonDatabase":true,
								"databaseField":"email",
								"allowMultiple": true,
								"maxItems":1,
								"pattern": "[a-zA-Z0-9._-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z0-9.-]{2,4}",
								"patternMessage":"Please ensure the email id is valid.E.g. AmitM@gmail.com"
							},
							{
								"name":"emailMessageType",
								"label":"Email Message Type",
								"dataType": "STRING",
								"maxLength": 1,
								"notNull": true,
								"dataSetType":"ENUM",
								"dataSetValues":"com.xlx.treds.AppConstants.MessageType"
							}
					]
				};
				communicationForm$ = $('#frmEditCommunication').xform(lMobileConfig);
				communicationForm = communicationForm$.data('xform');
				
				$('#btnSave').on('click', function() {
			       	if($('#mobileList').is(":visible") && $('#mobileList').val()!=null && $('#mobileList').val()!=''&& communicationForm.getValue()['mobileList']==null ){
						alert("Please Click On Add To Save Mobile");
						return;
					}
			       	if($('#emailList').is(":visible") && $('#emailList').val()!=null && $('#emailList').val()!=''&& communicationForm.getValue()['emailList']==null ){
						alert("Please Click On Add To Save Email");
						return;
					}
					var lMobiles = communicationForm.getValue()['mobileList']; //this is an array
					var lEmails = communicationForm.getValue()['emailList']; //this is an array
					saveSettings(communicationForm.getValue()['notificationType'],lMobiles,lEmails,lEntityCode);
				});	
		});
		
		function editList(notifyType, communicationType){
			var lTmpData = myData.data[notifyType];
			var lMobiles = null;
			var lEmails = null;
			if (lTmpData!=null){
				lMobiles = (lTmpData.mobileList);//mobiles is a string
				lEmails = (lTmpData.emailList);//emails is a string
			}
			communicationForm.setValue({'notificationType': notifyType, 'smsMessageType': getMessageTypeSelected(notifyType,'Sms'), 'mobileList': lMobiles, 'emailMessageType': getMessageTypeSelected(notifyType,'Email'), 'emailList': lEmails});
			if (communicationType=='Sms'){
				communicationForm.enableDisableField('mobileList',true,false);
				communicationForm.enableDisableField('emailList',false,false);
				$("#Email").attr("style", "display:none");
				$("#Sms").attr("style", "display:block");
			}else if (communicationType=='Email'){
				communicationForm.enableDisableField('emailList',true,false);
				communicationForm.enableDisableField('mobileList',false,false);
				$("#Sms").attr("style", "display:none");
				$("#Email").attr("style", "display:block");
			}
			showModal($('#mdlCommunication'));
		}
		function getMessageTypeSelected(notifyType, communicationType){
			var lImplicit=false; lExplicit=false;
			if($('#chk'+communicationType+'Imp'+notifyType)!=null) lImplicit = $('#chk'+communicationType+'Imp'+notifyType).is(':checked');
			if($('#chk'+communicationType+'Exp'+notifyType)!=null) lExplicit = $('#chk'+communicationType+'Exp'+notifyType).is(':checked');
			if(lImplicit && lExplicit){
				return '<%=AppConstants.MessageType.Both.getCode()%>';
			}else if (lImplicit){
				return '<%=AppConstants.MessageType.Implicit.getCode()%>';
			}else if (lExplicit){
				return '<%=AppConstants.MessageType.Explicit.getCode()%>';
			}
			return '<%=AppConstants.MessageType.None.getCode()%>';
		}
		function toggleNotifications(buttonPrefix, notifyType, communicationType){
			var lChecked = false; 
			var lOtherChecked = false;
			var lOtherButtonPrefix = (buttonPrefix=='chk'+communicationType+'Exp'?'chk'+communicationType+'Imp':'chk'+communicationType+'Exp');
			if($('#'+buttonPrefix+notifyType)!=null){
				lChecked = $('#'+buttonPrefix+notifyType).is(':checked');
				if(lChecked){
					lOtherChecked = $('#'+lOtherButtonPrefix+notifyType).is(':checked');
					if(lOtherChecked){
						$('#'+lOtherButtonPrefix+notifyType). prop("checked", false);
					}
				}
			}
			if($('#chk'+communicationType+'Exp'+notifyType)!=null) lExplicit = $('#chk'+communicationType+'Exp'+notifyType).is(':checked');
		}
		function saveNotifications(notifyType){
			//fetch the mobileList from the data and send the same for saving.
			var lTmpData = myData.data[notifyType];
			var lMobiles = null;
			var lEmails = null;
			if(lTmpData!=null){
				lMobiles = lTmpData['mobileList'];
				lEmails = lTmpData['emailList'];
			}
			saveSettings(notifyType, lMobiles, lEmails, lEntityCode);
		}
		function saveSettings(notifyType, mobiles, emails, lEntityCode){
			//we will be saving for both and not individually.
			//the smsMessageType will be whatever is checked
			//the mobile list will be the one sent from the function for smsExplicit, and one from the data for smsImplicit
			var lData = {};
			lData['notificationType'] = notifyType;
			var lSmsMessageType = getMessageTypeSelected(notifyType,'Sms');
			var lEmailMessageType = getMessageTypeSelected(notifyType,'Email');
			lData['smsMessageType'] = lSmsMessageType;
			lData['emailMessageType'] = lEmailMessageType;
			lData['mobileList'] = mobiles;
			lData['emailList'] = emails;
			lData['code'] = lEntityCode;

			$.ajax( {
		           url: 'entotpnot',
		           type: 'POST',
		           data: JSON.stringify(lData),
		           success: function( pObj, pStatus, pXhr) {
		           		alert("Saved successfully", "Information", function() {
		           			//after saving it is important to save whatevere is sent to the local data 
		           			$('#mdlCommunication').modal('hide');
							//update the data
							if(myData.data!=null){
								var lTmpData = myData.data[notifyType];
								if(lTmpData!=null){
									lTmpData['mobileList'] = mobiles;
									lTmpData['smsMessageType'] = lSmsMessageType;
									lTmpData['emailList'] = emails;
									lTmpData['emailMessageType'] = lEmailMessageType;
								}else{
									//TODO:for insert
									//CREATE NOTIFICATION HASH
									// INSERT mobileList and smsMessageType into it as list
									var lNewData = {'smsMessageType' :lSmsMessageType, 'mobileList' : mobiles, 'emailMessageType' :lEmailMessageType, 'emailList' : emails};
									myData.data[notifyType] = lNewData;
								}
							}
							if($('#lstSms'+notifyType)!=null){
								$('#lstSms'+notifyType).empty();
								if(mobiles!=null){
									for(var lPtr=0; lPtr < mobiles.length; lPtr++){
										 $('#lstSms'+notifyType).append("<li>"+mobiles[lPtr]+"</li>");
									}
								}
							}
							if($('#lstEmail'+notifyType)!=null){
								$('#lstEmail'+notifyType).empty();
								if(emails!=null){
									for(var lPtr=0; lPtr < emails.length; lPtr++){
										 $('#lstEmail'+notifyType).append("<li>"+emails[lPtr]+"</li>");
									}
								}
							}
		           		});
		           },
		       	error: errorHandler
		       });	
		}
		
			
	</script>
	</body>
</html>