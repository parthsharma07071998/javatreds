<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.CompanyLocationBean.LocationType"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyContactBean"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.common.utilities.DBHelper"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyLocationBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lEntityId = request.getParameter("entityId"); 
if(lEntityId==null) lEntityId="0";
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<html>
	<head>
		<title>TREDS | Registration</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Locations/Branches" />
	</jsp:include>

	<div class="content" id="contCompanyLocation">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Locations/Branches</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> Add Location/Branch</a>
					<a></a>
				</div>
			</div>
			
			<div class="tab-pane panel panel-default" >
				<div class="row">
					<div class="col-sm-12">
						<div class="row" id="divLocations">
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<div style="display:none" id="frmMain" class="xform">
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="name" placeholder="Location/Branch Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="vat" class="label">Value Added Tax (VAT) registration No.:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="vat" placeholder="Value Added Tax (VAT)">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
									
					<div class="row">
						<div class="col-sm-12">
								<h4><span class="fa fa-building"></span> Address <a href="">  
								<small><a class="btn btn-link pull-right" id="chk-reg-adr">Copy Registered Address</a></small> 
								<small><a class="btn btn-link pull-right" id="chk-cor-adr">Copy Correspondence Address</a></small> 
						</div>
						<div class="col-sm-6">
								<div class="row">
									<div class="col-sm-4"><section><label for="line1" class="label">Address:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="line1" placeholder="Line 1">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section><label for="line2" class="label"></label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="line2" placeholder="Line 2">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section><label for="line3" class="label"></label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="line3" placeholder="Line 3">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">		
									<div class="col-sm-4"><section><label for="enableSettlement" class="label" onchange="toggleSettlement()">Enable Settlement:</label></section></div>
									<div class="col-sm-8">
										<section>
										<label class="checkbox"><input type=checkbox id="enableSettlement"><i></i>
										<b class="tooltip tooltip-top-left"></b></label>
										</section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section ><label for="settlementCLId" class="label">Settlement Location:</label></section></div>
									<div class="col-sm-8">
										<section class="select settlementCLId">
										<select id="settlementCLId"><option value="">Select Location</option></select>
										<b class="tooltip tooltip-top-right"></b><i></i></section>
										<section class="view"></section>
									</div>
								</div>
								

						</div>
						<div class="col-sm-6">
								<div class="row">
									<div class="col-sm-4"><section><label for="zipCode" class="label">Zip Code:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="zipCode" placeholder="Zip Code">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>					
								<div class="row">
									<div class="col-sm-4"><section><label for="city" class="label">City:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="city" placeholder="City">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section><label for="district" class="label">District:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="district" placeholder="District">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>					
								<div class="row">
									<div class="col-sm-4"><section><label for="state" class="label">State:</label></section></div>
									<div class="col-sm-8">
										<section class="select">
										<select id="state"><option value="">Select State</option></select>
										<b class="tooltip tooltip-top-right"></b><i></i></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section><label for="country" class="label">Country:</label></section></div>
									<div class="col-sm-8">
										<section class="select">
										<select id="country"><option value="">Select Country</option></select>
										<b class="tooltip tooltip-top-right"></b><i></i></section>
										<section class="view"></section>
									</div>
								</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-6">
								<div class="row">
									<div class="col-sm-4"><section><label for="cbdId" class="label">Bank Details</label></section></div>
									<div class="col-sm-8">
										<section class="select cbdId">
										<select id="cbdId"><option value="">Select Bank</option></select>
										<b class="tooltip tooltip-top-right"></b><i></i></section>
										<section class="view"></section>
									</div>
									<div class="col-sm-4"><section><label for="gstn" class="label">GST No:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="gstn" placeholder="GST No">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>

								</div>
								<div class="row">
									<input type="hidden" id="gstScannedFileName" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
									<div class="col-sm-4"><section><label for="gstScannedFileName" class="label">GST Scanned File:</label></section></div>
									<div class="col-sm-8">
										<section id='invoiceButtons' >
											<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
											<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
											<span class="upl-info"></span>
											<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
										</section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"><section><label for="remarks" class="label">Remarks:</label></section></div>
									<div class="col-sm-8">
										<section class="input">
										<input type="text" id="remarks" placeholder="Remarks">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
						</div>
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4"><section ><label for="locationType" class="label">Location Type:</label></section></div>
								<div class="col-sm-8">
									<section class="select locationType">
									<select id="locationType"><option value="">Select Location</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
	       				<div class="col-sm-12">
							<div class="row">
								<h4>
								<span class="fa fa-user">
								</span>
								Contact Details
								</h4>
							</div>
							
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
								<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="email" placeholder="Email">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="telephone" class="label">Telephone:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="telephone" placeholder="Telephone">
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
								
								<div class="col-sm-2"><section><label for="fax" class="label">FAX:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="fax" placeholder="FAX">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>							
           				</div>
					</div>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
				</fieldset>
	          </div>
             </div>	
		</div>
		<!-- frmMain -->

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script id="tplLocations" type="text/x-handlebars-template">

		{{#each this}}
		<div class="col-md-6 col-sm-12">
			<div class="lighter-gray-bx">
				<div class="panel-heading">
					<h3 class="panel-title">{{#ifCond locationType '==' "<%=LocationType.RegOffice%>"}}*{{/ifCond}} {{name}} - GSTN : {{gstn}} {{#if settlementName}}<b>(Mapped)</b>{{/if}} {{#if bankBranchName}}<b>(Settlement)</b>{{/if}} {{#if bankNACHStatus}}<b>{{bankNACHStatus}}</b>{{/if}}</h3>
					<div class="panel_right_info pull-right">
						<a></a>
						{{#ifCond creatorIdentity '!=' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyLocation.modifyHandler(null,[{{id}},{{isProvisional}}],false)"><i class="glyphicon glyphicon-pencil"></i></a>
						&nbsp;
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyLocation.removeHandler(null,[[{{id}}]])"><i class="glyphicon glyphicon-trash"></i></a>
						{{/ifCond}}
						{{#ifCond creatorIdentity '==' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyLocation.viewHandler(null,[{{id}},{{isProvisional}}])"><i class="glyphicon glyphicon-eye-open"></i> </a>		
						{{/ifCond}}
						<a></a>
					</div>
				</div>
			<div class="row horiz_table" >
				<div class="col-md-6">
					<table class="table">
						<tbody>
							<tr>
								<td class="table-header">Address</td>
								<td class="table-content">{{line1}}</td>
							</tr>
							<tr>
								<td class="table-header">Address 2</td>
								<td class="table-content">{{#if line3}}{{line3}}{{/if}}</td>
							</tr>
							<tr>
								<td class="table-header">State</td>
								<td class="table-content">{{state}}</td>
							</tr>
							<tr>
								<td class="table-header">Zip Code</td>
								<td class="table-content">{{zipCode}}</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="col-md-6">
					<table class="table">
						<tbody>
							<tr>
								<td class="table-header">Address 1</td>
								<td class="table-content">{{#if line2}}{{line2}}{{/if}}</td>
							</tr>
							<tr>
								<td class="table-header">City</td>
								<td class="table-content">{{city}}</td>
							</tr>
							<tr>
								<td class="table-header">District</td>
								<td class="table-content">{{district}}</td>
							</tr>
							<tr>
								<td class="table-header">Country</td>
								<td class="table-content">{{country}}</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="col-md-12">
					<table class="table">
						<tbody>
							<tr>
								<td class="table-header">{{#if settlementName}}Location :{{/if}} {{#if bankBranchName}}Bank :{{/if}}</td>
								<td class="table-content">{{#if settlementName}}{{settlementName}}{{/if}} {{#if bankBranchName}}{{bankBranchName}}{{/if}}</td>
							</tr>	
						</tbody>
					</table>
				</div>		
			</div>
			</div>
		</div>
		{{/each}}
		{{^if this}}No Data!{{/if}}
	</script>
	<script type="text/javascript">
	var crudCompanyLocation$ = null;
	var crudCompanyLocation = null;
	var tplLocations;
	var mainForm;
	var locwiseSettle=false;
	var cdId = <%=lEntityId%>;
	$(document).ready(function() {
		tplLocations = Handlebars.compile($('#tplLocations').html());
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyLocationBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "companylocation",
				keyFields:["id","isProvisional"],
				autoRefresh: true,
				preSearchHandler: function(pFilter) {
					pFilter.cdId=cdId;
					pFilter.recordVersion = 1;
					pFilter.isProvisional=<%=lIsProv%>;
					return true;
				},
				postSearchHandler: function(pData) {
					var lObj = (pData!=null?pData.data:null);
					locwiseSettle = pData.settleEnabled;
					cdId = pData.cdId;
					$('#divLocations').html(tplLocations(lObj));
					if (pData.creatorIdentity=='J'){
						$('#btnNew').hide();
					}
					return false;
				},
				preSaveHandler: function(pObj) {
					pObj.cdId=cdId;
					return true;
				},
				postModifyHandler: function(pObj) {
					if(locwiseSettle){
						if ((pObj.settlementCLId==null && pObj.cbdId==null)){
// 							mainForm.enableDisableField('cbdId',true,false);
// 							mainForm.enableDisableField('settlementCLId',true,false);
							mainForm.enableDisableField('enableSettlement',true,false);	
							$('#enableSettlement').prop('checked', true);
						}
						populateSettlementLocations(pObj.settlementCLId, pObj.cbdId);
						toggleSettlement();
					}else{
						var lFields =  ['settlementCLId', 'cbdId', 'enableSettlement'];
						mainForm.enableDisableField(lFields,false,false);
						mainForm.alterField(lFields,false,false);
						$('#enableSettlement').prop('checked', false);
					}
					if (pObj.creatorIdentity=='J'){
						mainForm.setViewMode(true);
						$('#chk-reg-adr').hide();
						$('#chk-cor-adr').hide();
					}
					setOldModifiedValue(pObj.modifiedData);
					return true;
				},
				postNewHandler: function() {
					if(locwiseSettle){
						$('#enableSettlement').prop('checked', true);
						mainForm.enableDisableField('settlementCLId',false,false);
						mainForm.alterField('settlementCLId',false,false);
						mainForm.alterField('cbdId',true,false);
						populateSettlementLocations();
					}else{
						var lFields = ['settlementCLId', 'cbdId', 'enableSettlement'];
						mainForm.enableDisableField(lFields,false,false);
						mainForm.alterField('lFields',false,false);
						$('#enableSettlement').prop('checked', false);
					}
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudCompanyLocation$ = $('#contCompanyLocation').xcrudwrapper(lConfig);
		crudCompanyLocation = crudCompanyLocation$.data('xcrudwrapper');
		mainForm = crudCompanyLocation.options.mainForm;
		mainForm.alterField(['gstn'], true, false);
		//mainForm.alterField(['gstn','gstScannedFileName'], true, false);

		$('#chk-reg-adr').on('click', function() {
			var lUrl= 'companylocation/regOff/' + cdId;
			$.ajax({
		        url: lUrl,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
					var lFields = ['line1','line2','line3','zipCode','country','state','district','city','salutation','firstName','middleName','lastName','email','telephone','mobile','fax','gstn'];
					$.each(lFields, function(pIndex, pValue){
						mainForm.getField(pValue).setValue(pObj[pValue]);
					});
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	
		        }
		    });
		});

		$('#chk-cor-adr').on('click', function() {
			var lUrl= 'company/address/cor' + ((cdId>0)?"?entityId="+cdId:"");
			$.ajax({
		        url: lUrl,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
					var lFields = ['Line1','Line2','Line3','ZipCode','Country','State','District','City','Salutation','FirstName','MiddleName','LastName','Email','Telephone','Mobile','Fax'];
					$.each(lFields, function(pIndex, pValue){
						mainForm.getField(pValue.charAt(0).toLowerCase()+pValue.slice(1)).setValue(pObj['cor'+pValue]);
						if(pValue=='Gstn')mainForm.getField('gstn').setValue(pObj['gstn']);
					});
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	
		        }
		    });
		});
		
	});

	function populateSettlementLocations(pSettlementCLId, pCbdId) {
		var lEntity = cdId;
		var lField=mainForm.getField("settlementCLId");
		var lClId=(mainForm.formData!=null?mainForm.formData.id:"");
		var lOptions=lField.getOptions()
		lOptions.dataSetValues = "companylocation/settlelov?cdId="+lEntity+"&clId="+lClId+"&isProv=<%=lIsProv%>";
		lField.init();
		if (pSettlementCLId != null)
			lField.setValue(pSettlementCLId);
		//
		lField=mainForm.getField("cbdId");
		var lOptions=lField.getOptions()
		if (!lEntity) lOptions.dataSetValues=[];
		else lOptions.dataSetValues = "companybankdetail/lov?cdId="+lEntity+"&isProv=<%=lIsProv%>";
		lField.init();
		if (pCbdId != null)
			lField.setValue(pCbdId);
	}
	$( "#enableSettlement" ).change(function() {
		  toggleSettlement();
	});
	
	function toggleSettlement(){
		var toggle = mainForm.getField('enableSettlement').getValue();
		var temp=false,temp1=true;
		if(toggle == "Y"){
			 temp=true;
			 temp1=false;
			 var tempClr=mainForm.getField("settlementCLId");
				tempClr.setValue("");
		}
		else{
		var tempClr=mainForm.getField("cbdId");
		tempClr.setValue("");
		}
		mainForm.enableDisableField('cbdId',temp,false);
		mainForm.enableDisableField('settlementCLId',temp1,false);
		mainForm.alterField('cbdId',temp,false);
		mainForm.alterField('settlementCLId',temp1,false);
	}
	
	function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudCompanyLocation.options.fieldGroups.update;
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