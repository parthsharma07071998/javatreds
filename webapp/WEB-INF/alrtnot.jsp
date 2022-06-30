<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.AlertNotificationSettingBean"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AlertNotificationSettingBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title></title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contAlertNotificationSetting">		
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

	<%@ include file="footer1.jsp" %>
<script id="tplList" type="text/x-handlebars-template">
		<div class="cloudTabs">
			<ul class="cloudtabs nav nav-tabs">
{{#each meta}}
				<li><a href="#tab{{key}}" data-toggle="tab">{{type}}</a></li>
{{/each}}
			</ul>
		</div>
		<div class="tab-content">
{{#each meta}}
			<div class="tab-pane panel panel-default" id="tab{{key}}">
				<table class="table table-hover">
				<thead><tr>
					<th width="32%">Event</th>
					<th width="13%">Mobile No.</th>
					<th width="17%">Email.</th>
				</tr></thead>
				<tbody>
{{#each data}}
					<tr>
						<td>{{description}}</td>
						<td>
							<ul style="list-style-type: none;padding:0px;margin:0px;" id='lstSms{{notificationType}}'>
							</ul>
							<button class="btn btn-sm btn-info" onClick="javascript:editList('{{notificationType}}','Sms');"><span class="fa fa-pencil"></span></button>
						</td>
						<td>
							<ul style="list-style-type: none;padding:0px;margin:0px;" id='lstEmail{{notificationType}}'>
							</ul>
							<button class="btn btn-sm btn-info" onClick="javascript:editList('{{notificationType}}','Email');"><span class="fa fa-pencil"></span></button>
						</td>


					</tr>
{{/each}}
				</tbody>
				</table>
			</div>
{{/each}}
	</script>
	<script type="text/javascript">
		var crudAlertNotificationSetting$ = null;
		var tplList;
		var communicationForm$ = null, communicationForm=null;
		var myData = null;
		$(document).ready(function() {
			tplList = Handlebars.compile($('#tplList').html());

			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AlertNotificationSettingBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "alrtnot",
					autoRefresh: true,
					postSearchHandler:function (pData) {
						myData = pData;
						$('#contAlertNotificationSetting #frmSearch').html(tplList(pData));
						$('.nav-tabs a:first').tab('show');
						//loop through the meta and fetch the data from hash and set the controls appropriately.
						$.each(pData.meta,function(pIdx,pVal){
							//looping throught the tab-list which contains list of the events (pVal.list=tablist)
							$.each(pVal.data,function(pIdx,pListVal){
								var lNotificationType = pListVal.notificationType; 
								//fetching data from the data
								var lData = pData.data[lNotificationType]; 
								//we are not skipping the rest if data is null because we assume that if data is not present then the user has ticked the same.
								var lMessageType = null; 
								if($('#lstSms'+lNotificationType)!=null){
									if(lData!=null && lData.mobileList!=null&&lData.mobileList!=''){
										var lMobiles = lData.mobileList; //its an array
										for(var lPtr=0; lPtr < lMobiles.length; lPtr++){
											 $('#lstSms'+lNotificationType).append("<li>"+lMobiles[lPtr]+"</li>");
										}
									}
								}
								//setting the email list
								if($('#lstEmail'+lNotificationType)!=null){
									if(lData!=null && lData.emailList!=null&&lData.emailList!=''){
										var lEmails = lData.emailList; //its an array
										for(var lPtr=0; lPtr < lEmails.length; lPtr++){
											 $('#lstEmail'+lNotificationType).append("<li>"+lEmails[lPtr]+"</li>");
										}
									}
								}
							});
						});
						return false;
					}
					
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudEntityNotificationSetting$ = $('#contAlertNotificationSetting').xcrudwrapper(lConfig);
			crudEntityNotificationSetting = crudEntityNotificationSetting$.data('xcrudwrapper');
			searchForm = crudEntityNotificationSetting.options.searchForm;
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
								"maxLength": 500,
								"nonDatabase":true,
								"databaseField":"mobile",
								"allowMultiple": true,
								"maxItems":3,
								"pattern": "^(\\+?\\d{1,4}[\\s-])?(?!0+\\s+,?$)[1-9][0-9]{9}\\s*,?$",
								"patternMessage":"Please ensure the it is a mobile number."
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
								"maxLength": 500,
								"nonDatabase":true,
								"databaseField":"email",
								"allowMultiple": true,
								"maxItems":3,
								"pattern": "[a-zA-Z0-9._-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z0-9.-]{2,4}",
								"patternMessage":"Please ensure the email id is valid.E.g. AmitM@gmail.com"
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
					saveSettings(communicationForm.getValue()['notificationType'],lMobiles,lEmails);
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
			communicationForm.setValue({'notificationType': notifyType, 'mobileList': lMobiles, 'emailList': lEmails});
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
		function saveNotifications(notifyType){
			//fetch the mobileList from the data and send the same for saving.
			var lTmpData = myData.data[notifyType];
			var lMobiles = null;
			var lEmails = null;
			if(lTmpData!=null){
				lMobiles = lTmpData['mobileList'];
				lEmails = lTmpData['emailList'];
			}
			saveSettings(notifyType, lMobiles, lEmails);
		}
		function saveSettings(notifyType, mobiles, emails){
			//we will be saving for both and not individually.
			//the smsMessageType will be whatever is checked
			//the mobile list will be the one sent from the function for smsExplicit, and one from the data for smsImplicit
			var lData = {};
			lData['notificationType'] = notifyType;
			lData['mobileList'] = mobiles;
			lData['emailList'] = emails;
			$.ajax( {
		           url: 'alrtnot',
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
									lTmpData['emailList'] = emails;
								}else{
									//TODO:for insert
									//CREATE NOTIFICATION HASH
									// INSERT mobileList and smsMessageType into it as list
									var lNewData = { 'mobileList' : mobiles, 'emailList' : emails};
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