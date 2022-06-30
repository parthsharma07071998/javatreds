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
	<style>
.email tbody td {
	padding: 0px !important;
}
</style>
	</head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Notification Settings" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	<div class="container" id="contEntityNotificationSetting">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Email Notification Settings <%=(lParamEntityCode!=null && lParamEntityName!=null)?"("+lParamEntityCode+" - "+lParamEntityName+")":"" %></h1>
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
	
	<div class="modal fade" id="mdlEditEmails" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Edit Emails</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmEditEmails">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="emailList" class="label">Email List:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<div class="input-group"><input type="text" id="emailList" placeholder="Email List">
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
				<table class="table email table-hover">
				<thead><tr>
					<th width="30%">Event</th>
					<th width="30%">Implicit - Mailer Info</th>
					<th width="5%"></th>
					<th width="25%">Explicit -Email Ids</th>
					<th width="10%">Mail Rm</th>
				</tr></thead>
				<tbody>
{{#each list}}
{{#each this}}
{{#each this}}
						<tr>
						{{#if @first}} <td rowspan='{{../../this.length}}' class="showborder">{{@../key}}</td> {{/if}}
						{{#ifCond implicit '==' explicit}} 
							<td>
							<input type=checkbox id='chkImp{{notificationType}}' onClick="javascript:saveNotifications('{{notificationType}}')" >
							{{mailerInfo}}
								{{#if extraSettings}}
							  		<br>
							    	<ul>
							    	{{#each extraSettings}}
							        	<li>{{this}}</li>
									{{/each}}
									</ul>
								{{/if}}
							</td>
							<td>
							<input type=checkbox id='chkExp{{notificationType}}' onClick="javascript:saveNotifications('{{notificationType}}')" >
							</td>
						{{else}}  
							<td>	
							{{#if implicit}}
							<input type=checkbox id='chkImp{{notificationType}}' onClick="javascript:saveNotifications('{{notificationType}}')" >
							{{mailerInfo}}
							{{/if}}
							</td>
							<td>{{#if explicit}}
							<input type=checkbox id='chkExp{{notificationType}}' onClick="javascript:saveNotifications('{{notificationType}}')" >
							{{/if}}
							</td>	
						{{/ifCond}}	
						<td>
							{{#if explicit}}
							<ul style="list-style-type: none;padding:0px;margin:0px;" id='lst{{notificationType}}'>
							</ul>
							<button class="btn btn-sm btn-info" onClick="javascript:editEmailList('{{notificationType}}');"><span class="fa fa-pencil"></span></button>
							{{/if}}
						</td>
						<td>
							<input type=checkbox id='chkRm{{notificationType}}' onClick="javascript:saveRmNotifications('{{notificationType}}')" >
						</td>
		</tr>
{{/each}}
{{/each}}
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
		var emailsForm$ = null, emailsForm=null;
		var myData = null;
		var lEntityCode = '<%=lParamEntityCode%>';
		$(document).ready(function() {
			tplList = Handlebars.compile($('#tplList').html());

			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(EntityNotificationSettingBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "entnot",
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
							$.each(pVal.list,function(pIdx,pGrpList){
								$.each(pGrpList,function(pIdx,pGrpVal){
									$.each(pGrpVal,function(pIdx,pListVal){
								var lNotificationType = pListVal.notificationType; 
								//fetching data from the data
								var lData = pData.data[lNotificationType]; 
								//we are not skipping the rest if data is null because we assume that if data is not present then the user has ticked the same.
								var lMailerType = null; 
								if(lData!=null && lData.mailerType!=null){
									lMailerType = lData.mailerType;
								}
								var lImplicit = (lMailerType==null || (lMailerType=='<%=AppConstants.MailerType.Both.getCode()%>') || (lMailerType=='<%=AppConstants.MailerType.Implicit.getCode()%>'));
								var lExplicit = (lMailerType!=null && ((lMailerType=='<%=AppConstants.MailerType.Both.getCode()%>') || (lMailerType=='<%=AppConstants.MailerType.Explicit.getCode()%>')));
								//this determines whether we have controls or not
								if(pListVal.implicit && $('#chkImp'+lNotificationType)!=null){
									$('#chkImp'+lNotificationType).attr('checked', lImplicit);
								}
								if(lData!=null && lData.mailRm && $('#chkRm'+lNotificationType)!=null){
									var lBoolFlag = false;
									if (lData.mailRm =='<%=CommonAppConstants.YesNo.Yes.getCode()%>'){
										lBoolFlag = true;								
									}
									$('#chkRm'+lNotificationType).attr('checked', lBoolFlag);
								}
								if(pListVal.explicit && $('#chkExp'+lNotificationType)!=null){
									$('#chkExp'+lNotificationType).attr('checked', lExplicit);
									//setting the email list
									if($('#lst'+lNotificationType)!=null){
										if(lData!=null && lData.emailList!=null&&lData.emailList!=''){
											var lEmails = lData.emailList; //its an array
											for(var lPtr=0; lPtr < lEmails.length; lPtr++){
												 $('#lst'+lNotificationType).append("<li>"+lEmails[lPtr]+"</li>");
											}
										}
									}
								}
									$('#chk'+lNotificationType).attr('checked', pListVal.mandatory);
									$('#chk'+lNotificationType).attr('disabled', true);
									$('#chkImp'+lNotificationType).attr('disabled', pListVal.mandatory);
									});
								});
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
			var lEmailConfig = {
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
								"name": "email",
								"label": "Email",
								"dataType": "STRING",
								"maxLength": 500,
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
								"maxItems":10,
								"pattern": "[a-zA-Z0-9._-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z0-9.-]{2,4}",
								"patternMessage":"Please ensure the email id is valid.E.g. AmitM@gmail.com"
							},
							{
								"name":"mailerType",
								"label":"Mailer Type",
								"dataType": "STRING",
								"maxLength": 1,
								"notNull": true,
								"dataSetType":"ENUM",
								"dataSetValues":"com.xlx.treds.AppConstants.MailerType"
							}
					]
				};
				emailsForm$ = $('#frmEditEmails').xform(lEmailConfig);
				emailsForm = emailsForm$.data('xform');
				
				$('#btnSave').on('click', function() {
			       	if($('#emailList').val()!=null && $('#emailList').val()!=''&& emailsForm.getValue()['emailList']==null ){
						alert("Please Click On Add To Save Email");
						return;
					}
					var lEmails = emailsForm.getValue()['emailList']; //this is an array
					saveSettings(emailsForm.getValue()['notificationType'],lEmails,lEntityCode);
				});	
		});
		
		function editEmailList(notifyType){
			var lTmpData = myData.data[notifyType];
			var lEmails = null;
			if (lTmpData!=null){
				lEmails = (lTmpData.emailList);//emails is a string
			}
			emailsForm.setValue({'notificationType': notifyType, 'mailerType': getMailerTypeSelected(notifyType), 'emailList': lEmails});
			showModal($('#mdlEditEmails'));
		}
		function getMailerTypeSelected(notifyType){
			var lImplicit=false; lExplicit=false;
			if($('#chkImp'+notifyType)!=null) lImplicit = $('#chkImp'+notifyType).is(':checked');
			if($('#chkExp'+notifyType)!=null) lExplicit = $('#chkExp'+notifyType).is(':checked');
			if(lImplicit && lExplicit){
				return '<%=AppConstants.MailerType.Both.getCode()%>';
			}else if (lImplicit){
				return '<%=AppConstants.MailerType.Implicit.getCode()%>';
			}else if (lExplicit){
				return '<%=AppConstants.MailerType.Explicit.getCode()%>';
			}
			return '<%=AppConstants.MailerType.None.getCode()%>';
		}
		function saveNotifications(notifyType){
			//fetch the emailList from the data and send the same for saving.
			var lTmpData = myData.data[notifyType];
			var lEmails = null;
			if(lTmpData!=null){
				lEmails = lTmpData['emailList'];
			}
			saveSettings(notifyType, lEmails , lEntityCode);
		}
		
		function saveSettings(notifyType, emails ,lEntityCode){
			//we will be saving for both and not individually.
			//the mailerType will be whatever is checked
			//the email list will be the one sent from the function for explicit, and one from the data for implicit
			var lData = {};
			lData['notificationType'] = notifyType;
			var lMailerType = getMailerTypeSelected(notifyType);
			lData['mailerType'] = lMailerType;
			lData['emailList'] = emails;
			lData['code'] = lEntityCode;

			$.ajax( {
		           url: 'entnot',
		           type: 'POST',
		           data: JSON.stringify(lData),
		           success: function( pObj, pStatus, pXhr) {
		           		alert("Saved successfully", "Information", function() {
		           			//after saving it is important to save whatevere is sent to the local data 
		           			$('#mdlEditEmails').modal('hide');
							//update the data
							if(myData.data!=null){
								var lTmpData = myData.data[notifyType];
								if(lTmpData!=null){
									lTmpData['emailList'] = emails;
									lTmpData['mailerType'] = lMailerType;
								}else{
									//TODO:for insert
									//CREATE NOTIFICATION HASH
									// INSERT emailList and mailerType into it as list
									var lNewData = {'mailerType' :lMailerType, 'emailList' : emails};
									myData.data[notifyType] = lNewData;
								}
							}
							if($('#lst'+notifyType)!=null){
								$('#lst'+notifyType).empty();
								if(emails!=null){
									for(var lPtr=0; lPtr < emails.length; lPtr++){
										 $('#lst'+notifyType).append("<li>"+emails[lPtr]+"</li>");
									}
								}
							}
		           		});
		           },
		       	error: errorHandler
		       });	
		}
		
		function saveMandatory(notifyType){
			var lTmpData = myData.data[notifyType];
			oldAlert(lTmpData.mandatory)
		}
		
		
		function saveRmNotifications(notifyType){
			var lRm;
			var lData = {};
			lData['notificationType'] = notifyType;
			lData['code'] = '<%=lParamEntityCode%>';
			if($('#chkRm'+notifyType)!=null) lRm = $('#chkRm'+notifyType).is(':checked');
			lData['mailRm'] = lRm==true?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
			$.ajax( {
		           url: 'entnot/saveRm',
		           type: 'POST',
		           data: JSON.stringify(lData),
		           success: function( pObj, pStatus, pXhr) {
		        	   if(myData.data!=null){
							var lTmpData = myData.data[notifyType];
							if(lTmpData!=null){
								lTmpData['mailRm'] = lData['mailRm'];
							}else{
								//TODO:for insert
								//CREATE NOTIFICATION HASH
								// INSERT emailList and mailerType into it as list
								var lNewData = {'mailRm' :lData['mailRm']};
								myData.data[notifyType] = lNewData;
							}
						}
		           },
			       	error: errorHandler
			       });	
		}
		
		
		
			
	</script>
	</body>
</html>