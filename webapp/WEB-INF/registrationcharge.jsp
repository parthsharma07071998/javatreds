<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants.RegEntityType"%>
<%@page import="com.xlx.treds.entity.bean.RegistrationChargeBean.RequestType"%>
<%@page import="com.xlx.treds.entity.bean.RegistrationChargeBean.ApprovalStatus"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.RegistrationChargeBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Registration charge</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
		 <style>
	        hr {
			  border: 0;
			  clear:both;
			  display:block;
			  width: 96%;               
			  background-color:grey;
			  height: 1px;
			}
        </style>
		
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Registration charge" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contRegistrationCharge" >
	<div id="frmSearch">		
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<!-- <header>Registration charge</header> -->

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="entityCode" class="label">Entity Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="entityCode" placeholder="Entity Code">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="entityType"><option value="">Select Entity Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="effectiveStartDate" class="label">Renewal From Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="effectiveStartDate" placeholder="Effective From Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="effectiveEndDate" class="label">Renewal To Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="effectiveEndDate" placeholder="Effective To Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="requestType" class="label">Request Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="requestType"><option value="">Select Request Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="chargeType" class="label">Charge Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="chargeType"><option value="">Select Charge Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="approvalStatus" class="label">Approval Status:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="approvalStatus"><option value="">Select Approval Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
			</fieldset>
			<div class="panel-body bg_white">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
	 						<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
						</div>
					</div>
				</div>
			</div>
		</div>	
			<fieldset>
				<div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
						 <li><a href="#tab0" data-toggle="tab"><label id="tabLabel">Inbox/Draft</label> <span id="badge0" class="badge bg-green"></span></a></li>
						 <li><a href="#tab1" data-toggle="tab">For Approval <span id="badge1" class="badge bg-red"></span></a></li>
						 <li><a href="#tab2" data-toggle="tab">Approved <span id="badge2" class="badge bg-red"></span></a></li>
					</ul>
				</div>					
				<div class="filter-block clearfix">
						<div class="">
							<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter"> Filter </a>
							<span class="right_links">
							<a class="secure btn-group0" href="javascript:;" data-seckey="regchrg-manage" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify </a>
							<a class="secure data-downloader" data-type="csv" data-seckey="regchrg-view" id=btnDownload><span class="glyphicon glyphicon-download-alt"></span> Download </a>
							<a class="secure" href="javascript:;" data-seckey="regchrg-view" id=btnView><span class="glyphicon glyphicon-eye-open"></span> View </a>
							<a class="right_links" href="javascript:;" id=btnViewHistory onClick="javascript:viewHistory();"><span class="fa fa-stack-overflow"></span> View History</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="regchrg-submit" onClick="javascript:submit();" id=btnSubmit><span class="glyphicon glyphicon-eye-open"></span> Submit </a>
							<a class="secure btn-group1" href="javascript:;" data-seckey="regchrg-approve" onClick="javascript:updateChecker('<%=ApprovalStatus.Approved.getCode()%>');" id=btnApprove><span class="glyphicon glyphicon-eye-open"></span> Approve </a>
							<a class="secure btn-group1" href="javascript:;" data-seckey="regchrg-return" onClick="javascript:updateChecker('<%=ApprovalStatus.Returned.getCode()%>');" id=btnReturn><span class="glyphicon glyphicon-eye-open"></span> Return </a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="regchrg-manage" id=btnRemove><span class="glyphicon glyphicon-trash"></span> Remove </a>
							<a class="secure btn-group2" href="javascript:;" data-seckey="regchrg-reext" onClick="javascript:reExtend();" id=btnSubmit><span class="glyphicon glyphicon-eye-open"></span> Re-Extend </a>
							<a class="secure btn-group2" href="javascript:;" data-seckey="regchrg-extpay" onClick="javascript:createCharge('<%=RequestType.Payment.getCode()%>');" id=btnExtPayment><span class="glyphicon glyphicon-eye-open"></span> Extension Payment </a>
							<a class="secure btn-group2" href="javascript:;" data-seckey="regchrg-extwaive" onClick="javascript:createCharge('<%=RequestType.Waiver.getCode()%>');" id=btnExtWaiver><span class="glyphicon glyphicon-eye-open"></span> Extension Waiver </a>
							<a class="secure" href="javascript:;" data-seckey="regfxls-upload" id=btnUploadReg><span class="glyphicon glyphicon-upload-alt"></span> Upload Reg </a>
							<a class="secure" href="javascript:;" data-seckey="annfxls-upload" id=btnUploadAnnual><span class="glyphicon glyphicon-upload-alt"></span> Upload Annual </a>
							<!-- regfxls-upload -->
							<!-- annfxls-upload -->
							</span>
						</div>
				</div>	
			</fieldset>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData" >
							<thead><tr>
								<th data-visible="false" data-width="100px" data-name="id">Id</th>
								<th data-width="100px" data-name="entityCode">Entity Code</th>
								<th data-width="100px" data-name="entityType">Entity Type</th>
								<th data-width="100px" data-name="chargeType">Charge Type</th>
								<th data-width="100px" data-name="effectiveDate">Effective Date</th>
								<th data-width="100px" data-name="chargeAmount">Charge Amount</th>
								<th data-width="100px" data-name="requestType">Request Type</th>
								<th data-width="100px" data-name="extendedDate">Extended Date</th>
								<th data-width="100px" data-name="extensionCount">Extenstion Count</th>
								<th data-width="100px" data-name="paymentDate">Payment Date</th>
								<th data-width="100px" data-name="paymentAmount">Payment Amount</th>
								<th data-width="100px" data-name="paymentRefrence">Payment Refrence</th>
								<th data-width="100px" data-name="billedEntityCode">Billed Entity Code</th>
								<th data-width="100px" data-name="billedEntityClId">Location</th>
								<th data-width="100px" data-name="remarks">Remarks</th>
								<th data-width="100px" data-name="supportingDoc">Supporting Doc</th>
								<th data-width="100px" data-name="makerAuId">Maker User</th>
								<th data-width="100px" data-name="makerTimestamp">Maker Timestamp</th>
								<th data-width="100px" data-name="checkerAuId">Checker User</th>
								<th data-width="100px" data-name="checkerTimestamp">Checker Timestamp</th>
								<th data-width="100px" data-name="approvalStatus">Approval Status</th>
								<th data-visible="false" data-name="tab"></th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
	</div>	

		<div class="modal fade" id="mdlRegAnnChrgHistory" tabindex=-1><div class="modal-dialog modal-lg" ><div class="modal-content">
		</div></div></div>

		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>Registration charge</header> -->
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
	    	<div>
			<fieldset>
				<div class="row hidden">
					<div class="col-sm-2"><section><label for="id" class="label">Id:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="id" placeholder="Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="entityCode" class="label">Entity Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="entityCode" placeholder="Entity Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="entityType"><option value="">Select Entity Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="chargeType" class="label">Charge Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="chargeType"><option value="">Select Charge Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="chargeAmount" class="label">Charge Amount:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="chargeAmount" placeholder="Charge Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="registrationDate" class="label">Registration Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="registrationDate" placeholder="Registration Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="effectiveDate" class="label">Last Renewed On:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="effectiveDate" placeholder="Effective Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="renewalDate" class="label">Next Renewal On:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="renewalDate" placeholder="Renewal Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				
				<hr></hr>
				<div class="row">
					<div class="col-sm-2"><section><label for="requestType" class="label">Request Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="requestType" onChange="javascript:enableDisable()"><option value="">Select Request Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr></hr>
				<div class="row">
					<div class="col-sm-2"><section><label for="extendedDate" class="label">Extended Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="extendedDate" placeholder="Extended Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="extensionCount" class="label">Extenstion Count:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="extensionCount" placeholder="Extenstion Count">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<hr></hr>
				<div class="row">
					<div class="col-sm-2"><section><label for="paymentDate" class="label">Payment Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="paymentDate" placeholder="Payment Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="paymentAmount" class="label">Payment Amount:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="paymentAmount" placeholder="Payment Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="paymentRefrence" class="label">Payment Refrence:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="paymentRefrence" placeholder="Payment Refrence">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="billedEntityCode" class="label">Billed Entity Code:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="billedEntityCode" onChange="javascript:populateLocations('billedEntityClId')"><option value="">Select Billed Entity Code</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="billedEntityClId" class="label">Location:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="billedEntityClId" ><option value="">Select Billing Location</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr></hr>
				<div class="row">
					<div class="col-sm-2"><section><label for="remarks" class="label">Remarks:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="remarks" placeholder="Remarks">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<hr></hr>
				<div class="row">
					<input type="hidden" id="supportingDoc" data-role="xuploadfield" data-file-type="REGCHARGESUPPORTDOC" />
					<div class="col-sm-2"><section><label for="relationDoc" class="label">Supporting Doc.:</label></section></div>
					<div class="col-sm-4">
						<section id='supportingDoc' >
							<button type="button" class="upl-btn-upload btn btn-lg btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="upl-btn-clear btn btn-lg btn-default"><span class="fa fa-remove"></span> Clear</button>
							<span class="upl-info"></span>
							<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
						</section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-group pull-right">
						<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>						
						<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
   		</div>
 		</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	
	<script id="tplRegChargeHistory" type="text/x-handlebars-template">
<div class="modal-header"><span></span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<h3>Registration/Annual Charge History</h3>
		<div class="row">
			<div class="col-sm-12">
				
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-striped table-bordered" style="width:100%"><tbody>
								<thead>
								<tr>
									<th>Charge Type </th>
									<th>Date </th>
									<th>Charge Amt</th>
									<th>Request Type</th>
									<th>Extended Date</th>
									<th>Extension Count</th>
									<th>Payment Date</th>
									<th>Payment Amount</th>
									<th>Payment Ref</th>
									<th>Billed Entity</th>
								</tr>
								</thead>
								{{#each regAnnFees}}
								<tr>
									<td>{{chargeType}}</td>
									<td>{{effectiveDate}}</td>
									<td>{{chargeAmount}}</td>
									<td>{{requestType}}</td>
									<td>{{extendedDate}}</td>
									<td>{{extensionCount}}</td>
									<td>{{paymentDate}}</td>
									<td>{{paymentAmount}}</td>
									<td>{{paymentRefrence}}</td>
									<td>{{billedEntityCode}}</td>
								</tr>
								{{/each}}
				</tbody></table>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-info btn-lg btn-close" data-dismiss="modal">OK</button>
				</div>
			</div>
		</div>
</fieldset>
</div>

	</script>
	
	
	<script type="text/javascript">
		var crudRegistrationCharge$ = null;
		var crudRegistrationCharge = null;
		var tplRegChrgHistory;
		var mainForm = null;
		var tabData = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RegistrationChargeBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "regchrg",
					autoRefresh: true,
					postModifyHandler: function(pObj){
						mainForm.enableDisableField(['entityCode','entityType','chargeType','effectiveDate','renewalDate','chargeAmount','registrationDate','extensionCount'], false,false);
						enableDisable();
						if (pObj.entityType == '<%=RegEntityType.Supplier.getCode() %>'){
							populateEntity(pObj.billedEntityCode,'suppliers');
						}else if (pObj.entityType == '<%=RegEntityType.Purchaser.getCode() %>'){
							populateEntity(pObj.billedEntityCode,'purchasers');
						}else if(pObj.entityType == '<%=RegEntityType.Financier.getCode() %>'){
							populateEntity(pObj.billedEntityCode,'financiers');
						}
						if (pObj.requestType == '<%=RequestType.Payment.getCode() %>'){
							populateLocations('billedEntityClId',pObj.billedEntityClId);
						}
						return true;
					},
					postSearchHandler: function(pObj) {
						tabData = [];
						$.each(pObj,function(pIdx,pValue){
							var lData=tabData[pValue.tab];
							if (lData==null) {
								lData=[];
								tabData[pValue.tab]=lData;
							}
							lData.push(pValue);
						});
						var lIdx;
						$('.nav-tabs li a').each(function (index, element) {
							lIdx = element.attributes['href'].value.substring(4);
							if (tabData[lIdx]==null) tabData[lIdx]=[];
							var lCount = tabData[lIdx]?tabData[lIdx].length:0;
							$('#badge'+lIdx).html(lCount<10?"0"+lCount:lCount);
						});
						showData();
						return false;
					},
<%
if (lNewBool) {
%>					new: true,
<%
} else if (lModify != null) {
%>					modify: <%=lModify%>,
<%
}
%>			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudRegistrationCharge$ = $('#contRegistrationCharge').xcrudwrapper(lConfig);
			crudRegistrationCharge=crudRegistrationCharge$.data('xcrudwrapper');
			mainForm=crudRegistrationCharge.options.mainForm;
			
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
				var lRef1 = $(event.target).attr('href');         // active tab
				var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
				tabIdx = parseInt(lRef1.substring(4));
				if (lRef2)
					$('.btn-group'+lRef2.substring(4)).addClass('hidden');
				$('.btn-group'+tabIdx).removeClass('hidden');
	      	   showData();
			});
			$('#frmSearch .nav-tabs a:last').tab('show');
			$('#frmSearch .nav-tabs a:first').tab('show');
			//$('.right_links').addClass('hidden');
			
			$('#btnUploadReg').on('click', function() {
				showRemote('upload?url=regfxls', null, false);
			});
			$('#btnUploadAnnual').on('click', function() {
				showRemote('upload?url=annfxls', null, false);
			});
		});
		
		function enableDisable(){
			if (mainForm.getField('requestType').getValue() == "<%=RegistrationChargeBean.RequestType.Extenstion.getCode()%>"){
				mainForm.enableDisableField(['extendedDate'],true,false);
				mainForm.enableDisableField(['paymentRefrence','paymentDate','paymentAmount','billedEntityClId','billedEntityCode'],false,false);
			}else if (mainForm.getField('requestType').getValue() == "<%=RegistrationChargeBean.RequestType.Payment.getCode()%>"){
				mainForm.enableDisableField(['extendedDate'],false,false);
				mainForm.enableDisableField(['paymentRefrence','paymentDate','paymentAmount','billedEntityClId','billedEntityCode'],true,false);
				populateLocations('billedEntityClId',mainForm.getField('billedEntityCode').getValue());
			}else{
				mainForm.enableDisableField(['extendedDate','extensionCount','paymentRefrence','paymentDate','paymentAmount','billedEntityClId','billedEntityCode'],false,false);
			}
		}
		
		function submit(){
			var lSelected = crudRegistrationCharge.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			$.ajax( {
		        url: 'regchrg/submit/'+lSelected.data()["id"],
		        type: "GET",
		        success: function( pObj, pStatus, pXhr){
		        	alert("Submitted successfully.");
		        	crudRegistrationCharge.searchHandler();
		        },
		    	error: errorHandler
		    });
		}
		
		function reExtend(){
			var lSelected = crudRegistrationCharge.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			if(lSelected.data()["requestType"]!="<%=RegistrationChargeBean.RequestType.Extenstion.toString()%>"){
				alert("Please select an extension request type.");
				return;
			}
			$.ajax( {
		        url: 'regchrg/reextend/'+lSelected.data()["id"],
		        type: "GET",
		        success: function( pObj, pStatus, pXhr){
		        	alert("ReExtended successfully.");
		        	crudRegistrationCharge.searchHandler();
		        },
		    	error: errorHandler
		    });
		}
		function createCharge(pRequestType){
			var lSelected = crudRegistrationCharge.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			if(lSelected.data()["requestType"]!="<%=RegistrationChargeBean.RequestType.Extenstion.toString()%>"){
				alert("Please select an extension request type.");
				return;
			}
			var lData = {"code":lSelected.data()["entityCode"],"requestType":pRequestType,"id":lSelected.data()["id"]};
			$.ajax( {
		        url: 'regchrg/createchrg',
		        data: JSON.stringify(lData),
		        type: 'POST',
		        success: function( pObj, pStatus, pXhr){
		        	alert("Charge created successfully.");
		        	crudRegistrationCharge.searchHandler();
		        },
		    	error: errorHandler
		    });
		}
		
		function updateChecker(pStatus){
			var lSelected = crudRegistrationCharge.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			$.ajax( {
		        url: 'regchrg/checker/'+lSelected.data()["id"]+'/'+pStatus,
		        type: "GET",
		        success: function( pObj, pStatus, pXhr){
		        	if(pStatus=='<%=ApprovalStatus.Approved.getCode()%>'){
			        	alert("Approved successfully.");
		        	}else if(pStatus=='<%=ApprovalStatus.Returned.getCode()%>'){
			        	alert("Returned successfully.");
		        	}
		        	crudRegistrationCharge.searchHandler();
		        },
		    	error: errorHandler
		    });
		}
		
		function showData() {
			crudRegistrationCharge.options.dataTable.rows().clear();
			if (tabData && (tabData[tabIdx] != null)){
				crudRegistrationCharge.options.dataTable.rows.add(tabData[tabIdx]).draw();
			}
		}
		
		function populateLocations(pFieldName,pValue){
			$('#'+pFieldName).empty();
			var lField=mainForm.getField(pFieldName);
			var lOptions=lField.getOptions();
			if (mainForm.getField('billedEntityCode').getValue()==null) {
				lOptions.dataSetValues=[];
			}else {
				lOptions.dataSetValues ="companylocation/all?aecode="+mainForm.getField('billedEntityCode').getValue();
			}
			lField.init();
			if (pValue!=null){
				mainForm.getField(pFieldName).setValue(pValue);
			}
		}
		
		function populateEntity(pValue,pType){
			var lField=mainForm.getField('billedEntityCode');
			var lOptions=lField.getOptions();
			if (mainForm.getField('billedEntityCode').getValue()==null) {
				lOptions.dataSetValues=[];
			}else {
				lOptions.dataSetValues ="appentity/"+pType;
			}
			lField.init();
			if (pValue!=null){
				lField.setValue(pValue);
			}
		}
		function viewHistory(){
			var lSelected = crudRegistrationCharge.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			var lData = {"code":lSelected.data()["entityCode"]};
			tplRegChrgHistory = Handlebars.compile($('#tplRegChargeHistory').html());
			$.ajax({
				url: 'regchrg/history',
				type: 'POST',
		        data: JSON.stringify(lData),
				success: function( pObj, pStatus, pXhr) {
		        	var lModal$ = $('#mdlRegAnnChrgHistory');
		        	lModal$.find('.modal-content').html(tplRegChrgHistory(pObj));
		        	showModal(lModal$);
				},
				error: errorHandler,
			});
		}


	</script>


</body>
</html>