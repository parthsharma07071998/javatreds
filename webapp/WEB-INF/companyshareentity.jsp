<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyShareEntityBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lEntityId = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("entityId"))); 
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<html>
	<head>
		<title>Company Share Entity : The detailed information about the shareholder entity.</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	</head>
	<body class="skin-blue">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Company Share Entity : The detailed information about the shareholder entity." />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contCompanyShareEntity">		
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Entity Details</h1>
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
			<div class="row">
				<div class="col-sm-12 panel panel-default" id="data">
				</div>
				</div>
			</div>
		<!-- frmSearch -->
		
		<div style="display:none" id="frmMain" class="xform">
		<div>
			<div class="cloudTabs">
				<ul class="cloudtabs nav nav-tabs">
					<li class="active1"><a href="#tabGeneral" data-toggle="tab">General
							Information </a></li>
					<li ><a href="#tabAdd" data-toggle="tab">KMP(Key Management Person) and Address</a></li>
				</ul>
			</div>
			</div>
			<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<div class="tab-content no-padding">
								<div id="tabGeneral" class="tab-pane active1">
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="companyName" class="label">Name of
													Applicant Entity:</label>
											</section>
										</div>
										<div class="col-sm-8">
											<section class="input">
												<input type="text" id="companyName"
													placeholder="Name of Applicant Entity"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="benificiaryOwner" class="label">Benificiary Owner:
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="benificiaryOwner"
													placeholder="Benificiary Owner"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="constitution" class="label">Constitution:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="constitution" onchange="setConstitution()"><option value="">Select
														Constitution</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="regNo" class="label">
													Registration No:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="regNo"
													placeholder="Registration Number">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="pan" class="label">
													PAN:
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="pan" 
													placeholder="PAN">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="dateOfIncorporation" class="label">Date
													of Incorporation:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="dateOfIncorporation"
													placeholder="Date of Incorporation"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="industry" class="label">Industry:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="industry"><option value="">Select
														Industry</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="subSegment" class="label">Sub-Segment:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="subSegment"><option value="">Select
														Sub-Segment</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="companyDesc" class="label">Brief
													Description of Activity:</label>
											</section>
										</div>
										<div class="col-sm-8">
											<section class="textarea">
												<textarea id="companyDesc"
													placeholder="Brief Description of Activity" rows=5></textarea>
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row pull-right">
										<button type="button" class="btn btn-info-inverse btn-lg" id=btnNext href="#tabAdd">
											<span class="fa fa-forward"></span> Next
										</button>
										<button type="button" class="btn btn-info-inverse btn-lg btn-close" id='btnClose'><span class="fa fa-close"></span> Close</button>
									</div>
								</div>
								
								
								<div id="tabAdd" class="tab-pane">

									<hr>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="salutation" class="label">Title:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="salutation"><option value="">Select
														Title</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="firstName" class="label">First Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="firstName"
													placeholder="First Name"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="middleName" class="label">Middle
													Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="middleName"
													placeholder="Middle Name"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="lastName" class="label">Last Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="lastName" placeholder="Last Name">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="designation" class="label">Designation:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="designation"><option value="">Select
													</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="kmpPan" class="label">
													PAN:
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="kmpPan" 
													placeholder="PAN">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="telephone" class="label">Telephone:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="telephone" placeholder="Telephone">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="mobile" class="label">Mobile:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="mobile" placeholder="Mobile">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class='row'>
										<div class="col-sm-2">
											<section>
												<label for="email" class="label">Email:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="email" placeholder="Email">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="fax" class="label">FAX:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="fax" placeholder="FAX"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<hr>
									<div class="row">
										<div class="col-sm-12">
											<h4>
												<span class="fa fa-building"></span> Residential Address
												<small style="display:none"><a class="btn btn-link pull-right" id="chk-cor-adr" onclick="populateRegLocation()">
														Same as Registered Address</a></small>
											</h4>
										</div>
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
														<input type="text" id="city" placeholder="City">
														<b class="tooltip tooltip-top-right"></b>
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
														<input type="text" id="district"
															placeholder="District"> <b
															class="tooltip tooltip-top-right"></b>
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
									<div class="row">
											<fieldset class="box-footer">
											<div class="row">
												<div class="col-sm-12">
													<div class="btn-groupX pull-right">
														<button type="button" class="btn btn-info-inverse btn-lg" id=btnPrevious>
															<span class="fa fa-backward"></span> Previous
														</button>
														<button type="button" class="btn btn-info btn-enter btn-lg" id=btnSave>Save</button>
														<button type="button" class="btn btn-info-inverse btn-lg btn-close" id="btnClose1"><span class="fa fa-close"></span> Close</button>
													</div>
												</div>
											</div>
										</fieldset>
									</div>
								</div>
								<!-- tabAddresses -->
							</div>
						</div>
					</div>
				</fieldset>
			</div>
	</div>
</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	   	<script id="tplData" type="text/x-handlebars-template">
		{{#each this}}
		<div class="col-md-6 col-sm-12">
			<div class="lighter-gray-bx">
				<div class="panel-heading">
					<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
						{{#if admin}} - Administrator{{/if}}
						{{#if promoter}} - Promoter{{/if}}
						{{#if authPer}} - Auth. Person{{/if}}
					</h3>
					<div class="panel_right_info pull-right">
						{{#ifCond creatorIdentity '!=' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareEntity.modifyHandler(null,[{{id}},{{isProvisional}}],false)"><i class="glyphicon glyphicon-pencil"></i> </a>
						&nbsp;
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareEntity.removeHandler(null,[[{{id}}]])"><i class="glyphicon glyphicon-trash"></i> </a>
						{{/ifCond}}
						{{#ifCond creatorIdentity '==' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyShareEntity.viewHandler(null,[{{id}},{{isProvisional}}])"><i class="glyphicon glyphicon-eye-open"></i> </a>		
						{{/ifCond}}
						<a></a>
					</div>
				</div>
			<div class="row horiz_table" >
				<div class="col-md-12">
					<table class="table">
						<tbody>
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
	var tplData;
	var lFormConfig = null;
	var dataHash = null; //Key=tabName, Value=HashOfJsonObjs
	var lCounter=0;
	var lConstitution ='';
	var dataHash;
	
	
	$(document).ready(function() {
		tplData = Handlebars.compile($('#tplData').html());
		lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyShareEntityBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "companyshareentity",
				keyFields:["id","isProvisional"],
				autoRefresh: true,
				postNewHandler: function() {
					$('#frmMain .nav-tabs a[href="#tabGeneral"]').tab('show');
					if((lConstitution == "Private Limited Company") || (lConstitution == "Public Limited Company")){
						mainForm.getField("benificiaryOwner").setValue(lConstitution);
						mainForm.enableDisableField('benificiaryOwner', false, false);
						return true;
					}else{
						alert("You cannot create a new entity");
						return false;
					}
					
				},
				postSearchHandler: function(pData) {
					var lData = pData.list;
					dataHash = new Array();
					$('#data').html(null);
					lData.forEach(function(item) {
						dataHash.push(item);
					});
					$('#data').html(tplData(dataHash));
					if (pData.creatorIdentity == 'J'){
						$('#btnNew').hide();
					}
					return false;
				},
				preSaveHandler: function(pObj) {
					pObj.cdId=<%=lEntityId%>;
					return true;
				},
				preSearchHandler: function(pObj) {
					pObj.cdId=<%=lEntityId%>;
					pObj.isProvisional=<%=lIsProv%>;
					return true;
				},
				preModifyHandler: function() {
					$('#frmMain .nav-tabs a[href="#tabGeneral"]').tab('show');
					return true;
				},
				postModifyHandler: function(pObj) {
					mainForm.getField("benificiaryOwner").setValue(lConstitution);
					mainForm.enableDisableField('benificiaryOwner', false, false);
					populateSubSegment(pObj.subSegment);
					setOldModifiedValue(pObj.modifiedData);
					return true;
				},
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudCompanyShareEntity$ = $('#contCompanyShareEntity').xcrudwrapper(lConfig);
		crudCompanyShareEntity = crudCompanyShareEntity$.data('xcrudwrapper');
		mainForm = crudCompanyShareEntity.options.mainForm;
		
		$('#btnPrevious').on('click', function() {
        	$('.nav-tabs > .active').prev('li').find('a').trigger('click');
		});
		
		$('#btnNext').on('click', function() {
        	$('.nav-tabs > .active').next('li').find('a').trigger('click');
		});
		
		$('#industry').on('change', function() {
			populateSubSegment(null);
		});
		$('#btnClose1').on('click', function() {
			$('#btnClose').click();
		});
		getConstitution();
	});
	
	function populateSubSegment(pSubSegment) {
		var lInd=mainForm.getField('industry').getValue();
		if (!lInd) return;
		var lField=mainForm.getField('subSegment');
		var lOptions=lField.getOptions()
		lOptions.dataSetValues = "company/subsegment/"+lInd;
		lField.init();
		if (pSubSegment) lField.setValue(pSubSegment);
	}
	
	function getConstitution(){
		var lId = <%=lEntityId%>;
		var lUrl = null;
		if(lId != null){
			lUrl = 'companyshareentity/getConstitution?id='+lId;
		}else if(loginData.domain == '<%=AppConstants.DOMAIN_REGUSER%>' ){
			lUrl = 'companyshareentity/getConstitution?loginId='+loginData.login+'&domain='+loginData.domain;
		}
		if(lUrl != null){
			$.ajax( {
		            url: lUrl,
		            async : false,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	lConstitution = pObj;
	            },
	        	error: errorHandler,
	        	complete: function() {
				}
	        });	
		}
	}
	
	function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudCompanyShareEntity.options.fieldGroups.update;
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