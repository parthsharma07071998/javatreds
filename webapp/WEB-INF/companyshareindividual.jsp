<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.CompanyShareIndividualBean"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.common.base.CommonConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyContactBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
	<head>
		<title>TREDS | Registration</title>
<%
String lTab=StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("tab")));
if (StringUtils.isBlank(lTab)) lTab = "tabIndiv";
String lEntityId = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("entityId"))); 
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	</head>
	<body class="skin-blue">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Management" />
	</jsp:include>

	<div class="content" id="contCompanyShareIndividual">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Management Details</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> Add Contact</a>
					<a></a>
				</div>
			</div>
			<div>
				<div class="cloudTabs">
					<ul class="cloudtabs nav nav-tabs" id="mgmtTypeTabs">
						<li class="active1"><a href="#tabIndiv" data-toggle="tab">Individual</a></li>
					 </ul>
				</div>
			</div>
			<div class="tab-pane panel panel-default" style="backcolor:none">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<div class="tab-content no-padding">
								<div id="tabIndiv"  class="tab-pane active1"></div>
							</div>
						</div>
					</div>
				</fieldset>			
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<div style="display:none" id="frmMain" class="xform">
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="salutation" class="label">Title:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="salutation"><option value="">Select Title</option></select>
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
						<div class="col-sm-2"><section><label for="designation" class="label">Designation:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="designation"><option value="">Select </option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="DOB" class="label">Date of Birth:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="DOB" placeholder="Date of Birth" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="pan" class="label">PAN:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="pan" placeholder="PAN">
							<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="email" placeholder="Email">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="telephone" class="label">Telephone No:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="telephone" placeholder="Telephone">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="fax" class="label">FAX:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="fax" placeholder="FAX">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="mobile" class="label">Mobile No:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="mobile" placeholder="Mobile">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" id="">
						<h4>
								<div class="col-sm-12"><section><label for="familyDetails" class="label"><h4>Father's /Spouse Name</h4></label></section></div>
						</h4>
					</div>

					<div class="row" id="divfamilyDetails">
						<div class="col-sm-2"><section><label for="familySalutation" class="label">Title:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="familySalutation"><option value="">Select Title</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="familyFirstName" class="label">First Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="familyFirstName" placeholder="First Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="familyMiddleName" class="label">Middle Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="familyMiddleName" placeholder="Middle Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="familyLastName" class="label">Last Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="familyLastName" placeholder="Last Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" id="">
						<h4>
								<div class="col-sm-12"><section><label for="address" class="label"><h4>Address</h4></label></section></div>
						</h4>
					</div>
						<div class="row">
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="line1" class="label">Address:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="line1" placeholder="Line 1">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="line2" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="line2" placeholder="Line 2">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="line3" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="line3" placeholder="Line 3">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="zipCode" class="label">Zip Code:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="zipCode" placeholder="Zip Code">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="city" class="label">City:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="city" placeholder="City"> <b
											class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="district" class="label">District:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="district" placeholder="District">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="state" class="label">State:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="state" placeholder="State">
										<b class="tooltip tooltip-top-right"></b><i></i>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="country" class="label">Country:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="select">
										<select id="country"><option value="">Select
												Country</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
									</section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>
		    		<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<input type="hidden" id="force" />
									<input type="hidden" id="isProvisional" />
									<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
				</div>
				</fieldset>
			</div>
		</div>
		<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script id="tplContacts" type="text/x-handlebars-template">
		{{#each this}}
		<div class="col-md-6 col-sm-12">
			<div class="lighter-gray-bx">
				<div class="panel-heading">
					<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
					</h3>
					<div class="panel_right_info pull-right">
						{{#ifCond creatorIdentity '!=' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareIndividual.modifyHandler(null,[{{id}},{{isProvisional}}],false)"><i class="glyphicon glyphicon-pencil"></i> </a>
						&nbsp;
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareIndividual.removeHandler(null,[[{{id}}]])"><i class="glyphicon glyphicon-trash"></i> </a>
						{{/ifCond}}
						{{#ifCond creatorIdentity '==' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareIndividual.viewHandler(null,[{{id}},{{isProvisional}}])"><i class="glyphicon glyphicon-eye-open"></i> </a>		
						{{/ifCond}}
						<a></a>
					</div>
				</div>
			<div class="row horiz_table" >
				<div class="col-md-12">
					<table class="table">
						<tbody><tr>
							<td class="table-header">Designation</td>
							<td class="table-content">{{designation}}</td>
						</tr>
						<tr>
							<td class="table-header">Email</td>
							<td class="table-content">{{email}}</td>
						</tr>
						<tr>
							<td class="table-header">Telephone</td>
							<td class="table-content">{{telephone}}</td>
						</tr>
						<tr>
							<td class="table-header">Mobile</td>
							<td class="table-content">{{mobile}}</td>
						</tr>
						<tr>
							<td class="table-header">Fax</td>
							<td class="table-content">{{fax}}</td>
						</tr>
					</tbody></table>
				</div>
				
			</div>
			</div>
		</div>

		</div>
		{{/each}}
		{{^if this}}No Data!{{/if}}
	</script>
	<script type="text/javascript">
	var crudCompanyShareIndividual$ = null;
	var crudCompanyShareIndividual = null;
	var mainForm = null;
	var tplContacts;
	var lFormConfig = null;
	var dataHash = null; //Key=tabName, Value=HashOfJsonObjs
	var lCounter=0;
	
	$(document).ready(function() {
		tplContacts = Handlebars.compile($('#tplContacts').html());
		lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyShareIndividualBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "companyshareindividual",
				autoRefresh: true,
				keyFields:["id","isProvisional"],
				preSearchHandler: function(pFilter) {
					pFilter.cdId=<%=lEntityId%>;
					pFilter.isProvisional=<%=lIsProv%>;
					return true;
				},
				postSearchHandler: function(pData) {
					$('#tabIndiv').html(null);
					$('#tabIndiv').html(tplContacts(pData.list));
					if (pData.creatorIdentity == 'J'){
						$('#btnNew').hide();
					}
					return false;
				},
				preSaveHandler: function(pObj) {
					pObj.cdId=<%=lEntityId%>;
					return true;
				},
				postModifyHandler: function(pObj) {
					setOldModifiedValue(pObj.modifiedData);
					return true;
				},
				postNewHandler:function(){
					var lConstitution = getConstitution();
					if (!lConstitution){
						alert("You cannot create a new individual");
						return false;
					}
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudCompanyShareIndividual$ = $('#contCompanyShareIndividual').xcrudwrapper(lConfig);
		crudCompanyShareIndividual = crudCompanyShareIndividual$.data('xcrudwrapper');
		mainForm = crudCompanyShareIndividual.options.mainForm;
		
		$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
		});
		showTab('<%=lTab%>');
	});
	function showTab(pTab) {
		$('#frmSearch .nav-tabs a[href="#'+pTab+'"]').tab('show');
	}
	
	function getConstitution(){
		var lId = <%=lEntityId%>;
		var lUrl = null;
		if(lId != null){
			lUrl = 'companyshareentity/getConstitution?id='+lId;
		}else if(loginData.domain == '<%=AppConstants.DOMAIN_REGUSER%>' ){
			lUrl = 'companyshareentity/getConstitution?loginId='+loginData.login+'&domain='+loginData.domain;
		}
		var lRetval = false;
		if(lUrl != null){
			
			$.ajax( {
		            url: lUrl,
		            async : false,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	console.log(pObj)
	            	if((pObj == "Private Limited Company") || (pObj == "Public Limited Company")){
	            		lRetval = true;
	            	}
	            },
	        	error: errorHandler,
	        	complete: function() {
				}
	        });	
		}
		return lRetval;
	}
	
	function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudCompanyShareIndividual.options.fieldGroups.update;
     	for(var lPtr=0; lPtr < lFieldGroup.length;  lPtr++){
     		$('#'+lFieldGroup[lPtr]).css('border-color','');
     	}
     	//mark modified fields red
     	if(pModifiedJson!=null&&pModifiedJson!=''){
     		var lData = pModifiedJson;
     		if(lData!=null){
     			for(var key in lData){
     				//$('#ov-'+key).html(lData[key]);
     				$('#'+key).css('border-color','red');
     			}
     		}
     	}
     }
	</script>
	</body>
</html>