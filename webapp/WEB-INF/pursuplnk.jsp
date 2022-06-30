<!DOCTYPE html>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.PlatformStatus"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.xlx.common.utilities.FormatHelper"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargeSlabBean"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargePlanBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierLinkBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
boolean lAdmin=request.getParameter("adm")!=null;
%>
<html>
    <head>
        <title>TREDS | Buyer Seller Link</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
<style>
.xform .label, .xform section.view-s {
	display: block;
	padding: 7px 10px;
	height: 30px;
	font-size: 15px;
	font-weight: 400 !important;
	text-align: left;
	white-space: normal;
	color:rgb(33,33,33);
}
.xform section.view-s {
	display: none;
}
.xform.view-s section.view-s {
	display: block !important;
}
.xform section.view-s ul {
	box-sizing: border-box;
    list-style: none;
    margin: 0;
    padding: 0 5px;
    width: 100%;
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.xform section.view-s li {
	background-color: rgba(240,240,240,.3);
    border: 1px solid rgba(235,235,235,.9);
    margin-right: 5px;
    padding: 5px 5px;
    float: left;
}
.xform.view-s .label {
	color:#aaa;	
}
.xform.view-s .nonView,
.xform.view-s .input,
.xform.view-s .select,
.xform.view-s .textarea,
.xform.view-s .radio,
.xform.view-s .checkbox,
.xform.view-s .toggle {
	display: none !important;
}
.xform.view-p section.view-p {
	display: ;
}
#mdlAucPlan .modal-dialog  {width:900px;}
.xform.view-p .nonView,
.xform.view-p .input,
.xform.view-p .select,
.xform.view-p .textarea,
.xform.view-p .radio,
.xform.view-p .checkbox,
.xform.view-p .toggle {
	display: ;
}
.frmfld th,td{
padding : 4px;
}
</style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Buyer Seller Link"/>
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contPurchaserSupplierLink">
<!-- frmSearch -->
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Buyer Seller Link</h1>
				</div>
			</div>
		<div id="frmSearch">
<%
if (lAdmin) {
%>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-12">
						<div class="form-group">
							<div class="col-sm-2  state-T"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
							<div class="col-sm-4  state-T">
								<section class="select">
								<select id="purchaser"><option value="">Select Purchaser</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2   state-T "><section><label for="supplier" class="label">Supplier:</label></section></div>
							<div class="col-sm-4   state-T ">
								<section class="select">
								<select id="supplier"><option value="">Select Supplier</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
				</div>
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
			</fieldset>
		</div>
<%
}
%>
				<div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
						 <li><a href="#tab0" data-toggle="tab"><label id="tabLabel">Inbox/Draft</label> <span id="badge0" class="badge bg-green"></span></a></li>
						 <li><a href="#tab1" data-toggle="tab">Active Links <span id="badge1" class="badge bg-yellow"></span></a></li>
						 <li id="tabPending" style="display:"><a href="#tab2" data-toggle="tab">Pending Acceptance<span id="badge2" class="badge bg-red"></span></a></li>
						 <li><a href="#tab3" data-toggle="tab">Suspended <span id="badge3" class="badge bg-red"></span></a></li>
					</ul>
				</div>
				<div class="filter-block clearfix">
					<div class="">
						<span class="right_links">
							<a id="spnColumnChooser" class="right_links glyphicon glyphicon-plus"></a>
							<a class="right_links" href="javascript:;" id=btnSearch><span class="fa fa-refresh"></span> Refresh</a>
							<% if (!lAdmin){%>
								<a class="right_links" href="javascript:mainForm.setViewMode(true); $('#frmMain #btnApprove').attr('style','display:none'); $('#frmMain #btnReject').attr('style','display:none'); $('#frmMain #btnEdit').attr('style','display:none');" data-seckey="pursuplnk-view" id=btnView><span class="glyphicon glyphicon-eye-open"></span> View</a>
								<a class="right_links" href="javascript:;" id=btnViewWorkFlow onClick="javascript:viewWorkFlow();"><span class="fa fa-stack-overflow"></span> View WorkFlow</a>
								<a class="right_links" href="javascript:;" data-seckey="pursuplnk-save" id=btnCapRate onClick="javascript:setCapRate()"><span class="fa fa-eye"></span> Cap Rate</a>
								<a class="right_links" href="javascript:;" data-seckey="pursuplnk-save" id=btnDownload onClick="javascript:downloadCsv()"><span class="fa fa-download"></span> Download CSV</a>	
								<a class="right_links state-P btn-group0 btn-group1 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnModify><span class="fa fa-pencil"></span> Modify</a>
								<a class="right_links state-P btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-upload" id=btnInstructions><span class="fa fa-upload"></span> Upload</a>
								<a class="right_links state-P btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnNew><span class="fa fa-plus"></span> New</a>
								<a class="right_links state-P btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnCreatetemplate  onClick="javascript:getTemplate()"><span class="fa fa-plus"></span> Template</a>
								<a class="right_links state-P btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnSubmit onClick="javascript:updateAppStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.Submitted.getCode()%>','Submit')"><span class="fa fa-arrow-circle-right"></span> Submit</a>
								<a class="right_links state-P btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnWithdraw onClick="javascript:updateAppStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.Withdraw.getCode()%>','Withdraw')"><span class="fa fa-scissors"></span> Withdraw</a>
								<a class="right_links btn-group3 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnReActivate onClick="javascript:updateAppStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.ReActivate.getCode()%>','ReActivate')"><span class="fa fa-check-square-o"></span> ReActivate</a>
								<a class="right_links btn-group1 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnSuspend onClick="javascript:updateAppStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.Suspended.getCode()%>','Suspend')"><span class="fa fa-unlink"></span> Suspend</a>
								<a class="right_links" href="javascript:;" data-seckey="pursuplnk-save" id=btnSendReminder style="display:none" onClick="javascript:sendReminder();"><span class="fa fa-envelope-o"></span> Send Reminder</a>			
								<a class="right_links state-S btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnRequestModification onClick="javascript:btnModify.click(); $('#frmMain #btnApprove').attr('style','display:none'); $('#frmMain #btnReject').attr('style','display:');"><span class="fa fa-mail-reply"></span> Request Modification</a>
								<a class="right_links state-S btn-group0 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnAccept onClick="javascript:btnModify.click(); $('#frmMain #btnApprove').attr('style','display:'); $('#frmMain #btnReject').attr('style','display:none');"><span class="fa fa-check"></span> Accept</a>
								<a class="right_links btn-group1 hidden" href="javascript:;" data-seckey="pursuplnk-save" id=btnModifyCode onClick="javascript:modifyCode();"><span class="fa fa-pencil"></span>Modify Code</a>
							<% }else{%>
								<a class="right_links state-T" href="javascript:;" id=btnActivateRelation><span class="fa fa-link"></span>Activate Relation</a>
								<a class="right_links state-T" href="javascript:;" id=btnSuspendRelation><span class="fa fa-link"></span>Suspend Relation</a>
								<a class="right_links state-T" href="javascript:;" onClick="javascript:getAuthPurchaser();" id=btnCreateLinkPlatform ><span class="fa fa-link" ></span>Create Link</a>
							<% }%>
							<a></a>
						</span>
					</div>
				</div>
				<div class="tab-pane panel panel-default">
					<fieldset>
						<div class="row">
							<div class="col-sm-12">
								<table class="table table-bordered " data-col-chooser="spnColumnChooser" id="tblData">
									<thead>
										<tr>
											<th data-width="0px" data-name="supplier" data-sel-exclude="true" data-visible="false">Seller Code</th>
											<th data-width="0px" data-name="purchaser" data-sel-exclude="true" data-visible="false">Buyer Code</th>
											<th data-width="120px" data-name="supName" id="supName">Seller</th>
											<th data-width="120px" data-name="purName" id="purName">Buyer</th>
											<th data-width="100px" data-name="supplierPurchaserRef">Buyer Internal Code</th>
											<th data-width="100px" data-name="creditPeriod">Default Credit Period</th>
											<th data-width="100px" data-name="extendedCreditPeriod">Extended Credit Period</th>
											<th data-width="100px" data-name="purchaserSupplierRef">Supplier Internal Code</th>
											<th data-width="80px" data-name="costBearingType">Default Cost Bearer</th>
											<th data-width="80px" data-name="bidAcceptingEntityType">Bid Accepting EntityType</th>
											<th data-width="100px" data-name="splittingPoint">Splitting Point</th>
											<th data-width="100px" data-name="preSplittingCostBearer">PreSplitting CostBearer</th>
											<th data-width="100px" data-name="postSplittingCostBearer">PostSplitting CostBearer</th>
											<th data-width="100px" data-name="buyerPercent">Buyer Percent</th>
											<th data-width="100px" data-name="chargeBearer">Default Platform Charge Bearer</th>
											<th data-width="80px" data-name="settleLeg3Flag">Leg 3 Settlement</th>
											<th data-width="100px" data-name="cashDiscountPercent">Cash Discount %</th>
											<th data-width="100px" data-name="haircutPercent">Haircut %</th>
											<th data-width="80px" data-name="approvalStatus">Approval Status</th>
											<th data-width="80px" data-name="status">Status</th>
											<th data-width="180px" data-name="remarks">Remarks</th>
											<th data-width="80px" data-name="platformStatus">Platform Status</th>
											<th data-width="80px" data-name="relationFlag">Relation Flag</th>
											<th data-width="80px" data-name="platformReasonCode">Platform Reason Code</th>
											<th data-width="80px" data-name="relationEffectiveDate">Relation Effective Date</th>
											<th data-width="80px" data-name="platformRemarks">Platform Remarks</th>
											<th data-width="80px" data-name="relationDoc">Relation Document</th>
											<th data-visible="false" data-name="tab"></th>
										</tr>
									</thead>
								</table>			 	
							</div>
						</div>
					</fieldset>
				</div>
		</div>
		<!-- frmSearch -->

		<div class="modal fade" id="mdlPSLinkWorkFlow" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
		</div></div></div>
		
		
		<div class="modal fade" id="mdlShowRelation" tabindex=-1><div class="modal-dialog"><div class="modal-content">
		</div></div></div>
		
		
		<div class="modal fade" id="mdlPurSupRelation" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
		</div></div></div>
		
	
		<div class="modal fade" id="mdlAuthPurchaser" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
		<div class="modal-header"><span>Authorize Purchasers </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body">
			<fieldset>
					<div class="xform box" id="authorizeTreds">
						<div class="row">
								<div class="col-sm-4"><section><label for="purchaser" class="label">Purchaser :</label></section></div>
								<div class="col-sm-8">
									<section class="select">
									<select id="purchaser"><option value="">Select Purchaser</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
						</div>
						<div class="box-footer" align="center">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info btn-lg btn-enter" id=btnOpenTemplate><span class="fa fa-download"></span> Open Template</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
									<a></a>
								</div>
							</div>
						</div>
			       </div>
					</div>
			</fieldset>
		</div>
		</div></div></div>
		
		
		<div class="modal fade" id="mdlAucPlan" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
		</div></div></div>
		<div class="modal fade" tabindex=-1 id="mdlPSLinkImpInstruction"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
        <div class="modal-header"><span>&nbsp;IMPORTANT INSTRUCTIONS</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body" height='200px'>
<!-- 		<div class="modal fade" id="mdlPSLinkImpInstruction" tabindex=-1><div class="modal-dialog" ><div class="modal-content"> -->
		<table class=frmtbl border=0 borcellspacing=1 cellpadding=2 width=100%>
    
        <tr><td class=frmfld>
                <ul>
                <li>File name can be anything.</li>
                <li>File should contain comma separated utilization information</li>
                <li>First record in the file should be the header record. (see example below)</li>
                <li>Subsequent records in the file should contain following field values separated by commas</li><br>
                </ul>
		     <table border=1 class=frmfld width='100%'>
                        <tr align=center>
                        <th>Headers</th>
						<th>Mandatory</th><th>Description</th><th>Value Range</th><th>Default Value</th></tr>
                        <tr><td>Supplier</td><td>Yes</td><td>Entity Code</td><td></td><td></td></tr>
                        <tr><td>Purchaser</td><td>Yes</td><td>Entity Code</td><td></td><td></td></tr>
                        <tr><td>SupplierPurchaserRef</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>CreditPeriod</td><td></td><td></td><td></td><td></td></tr>
                        <tr>><td>ExtendedCreditPeriod</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>PurchaserSupplierRef</td><td></td><td></td><td></td><td></td></tr>
                        <!-- <tr><td>Period1CostBearer</td><td></td><td></td><td></td><td></td></tr> 
                        <tr><td>period1CostPercent</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>period2CostBearer</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>period2CostPercent</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>period3CostBearer</td><td></td><td></td><td></td><td></td></tr>
                        <tr><td>period3CostPercent</td><td></td><td></td><td></td><td></td></tr> -->
                        <tr><td>bidAcceptingEntityType</td><td></td><td></td><td><ul><li>S=supplier</li><li>P=purchaser</li></ul></td><td></td></tr>
                         <tr><td>chargeBearer</td><td></td><td></td><td></td><td></td></tr>
                         <tr><td>settleLeg3Flag</td><td></td><td></td><td><ul><li>Y=Yes</li><li>N=No</li></ul></td><td></td></tr>
                         <tr><td>autoAccept</td><td>Yes</td><td>On receipt of Bid / Cut-off time / Disabled </td><td><ul><li>R=ON RECEIPT OF BID </li><li>C=CutOffTime</li><li>D=Disabled</li></ul></td><td></td></tr>
                         <tr><td>autoAcceptableBidTypes</td><td></td><td>Open Bids / All Bids</td><td><ul><li>YY=All bids</li><li>YN=Open Bids</li></ul></td><td></td></tr>
                         <tr><td>autoConvert</td><td>Yes</td><td>Conversion to factoring unit</td><td><ul><li>Y=Auto</li><li>S=supplier</li><li>P=purchaser</li></ul></td><td></td></tr>
                         <tr><td>purchaserAutoApproveInvoice</td><td></td><td></td><td><ul><li>Y=Yes</li><li>N=No</li></ul></td><td></td></tr>
                         <tr><td>invoiceMandatory</td><td></td><td></td><td><ul><li>Y=Yes</li><li>N=No</li></ul></td><td>N</td></tr>
                    <!--    <tr><td>remarks</td><td></td><td></td><td></td><td></td></tr>
                         <tr><td>cashDiscountPercent</td><td>Yes</td><td></td><td></td><td></td></tr>
                         <tr><td>haircutPercent</td><td>Yes</td><td></td><td></td><td>0</td></tr>   -->
                         <tr><td>costBearingType</td><td></td><td></td><td><ul><li>S=supplier</li><li>P=purchaser</li><li>PD=Periodical_Split</li><li>PC=Percentage_Split</li></ul></td><td></td></tr>
                         <tr><td>buyerPercent</td><td></td><td></td><td></td><td></td></tr>
                         <tr><td>preSplittingCostBearer</td><td></td><td></td><td><ul><li>S=supplier</li><li>P=purchaser</li></ul></td><td></td></tr>
                         <tr><td>postSplittingCostBearer</td><td></td><td></td><td><ul><li>S=supplier</li><li>P=purchaser</li></ul></td><td></td></tr>
                         <tr><td>splittingPoint</td><td></td><td></td><td><ul><li>SDD=Statutory Due Date</li><li> IDD=Invoice Due Date</li></ul></td><td></td></tr>
                         <tr><td>sellerPercent</td><td></td><td></td><td></td><td></td></tr>
						 <tr><td>sellerAutoApproveInvoice</td><td></td><td></td><td><ul><li>Y=Yes</li><li>N=No</li></ul></td><td>N</td></tr>
                        </table>
			<br>
		<ul>
                <li>A Single file should contain utilization for that Business date.</li>
                <li>The import will accept only those records where it matches the selected Hall Code.</li>
                <li>Item Codes not found in the list will be skipped.</li>
                <li>Only those utilizations in the file will be diplayed, rest will be reset to Zero (0).</li>
                </ul>
        </td></tr>
        <tr><td class=frmlbl >SAMPLE FILE CONTENTS
        </td></tr>
        <tr><td class=frmfld >
        <pre>
supplier,purchaser,supplierPurchaserRef,creditPeriod,extendedCreditPeriod,purchaserSupplierRef,chargeBearer,settleLeg3Flag,autoAccept,autoConvert,costBearingType,bidAcceptingEntityType,buyerPercent,preSplittingCostBearer,postSplittingCostBearer,splittingPoint,sellerPercent	
AD0000589,BA0000047,40,65,30,abcd,S,Y,D,P,,S,P,,,,,,N,Y,N
        </pre>
        </td></tr>
        </table>
        <div>
       	<div class="box-footer" align="center">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnDownloadPSLInstructions><span class="fa fa-download"></span> Download as PDF</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpload><span class="fa fa-upload"></span> Continue</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
        </div>
</td></tr>
</table>
</div>
		</div></div></div>
		
		<div class="modal fade" tabindex=-1 id="mdlSupplierList"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
        <div class="modal-header"><span>&nbsp;Supplier List</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body" height='200px'>
		<div class="xform box" id="frmSupplierFilter">
			<fieldset>
<!-- 				<div class="row"> -->
<!-- 					<div class="col-sm-4"><section><label for="purchaser" class="label">Purchaser :</label></section></div> -->
<!-- 					<div class="col-sm-8"> -->
<!-- 						<section class="input"> -->
<!-- 						<input id="purchaser" readonly><option value=""></option></select> -->
<!-- 						<b class="tooltip tooltip-top-right"></b><i></i></section> -->
<!-- 					</div> -->
<!-- 				</div> -->
				<div class="row">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Purchaser :</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser"><option value="">Select Purchaser</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="supplier" class="label">Supplier :</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier" multiple="multiple" data-role="multiselect"><option value="">Select Supplier</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableCdtPer" class="label">Enable Credit Period: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableCdtPer" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showCredPer" hidden>
					<div class="col-sm-4"><section><label for="creditPeriod" class="label">Default Credit Period:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="creditPeriod" placeholder="Default Credit Period">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableExtCdtPer" class="label">Enable Ext Credit Period: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableExtCdtPer" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showExtCdtPer" hidden>
					<div class="col-sm-4"><section><label for="extendedCreditPeriod" class="label">Ext Credit Period:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="extendedCreditPeriod" placeholder="Ext Credit Period">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableSendAuc" class="label">Enable Send For Auction: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableSendAuc" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showAutoConvert" hidden>
					<div class="col-sm-4"><section><label for="autoConvert" class="label">Send for Auction:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="autoConvert"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableInvMandatory" class="label">Enable Invoice Mandatory: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableInvMandatory" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showInvMandatory" hidden>
					<div class="col-sm-4"><section><label for="invoiceMandatory" class="label">Invoice Mandatory:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="invoiceMandatory"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableAutoAccept" class="label">Enable Auto Accept Bids: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableAutoAccept" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showAutoAccept" hidden>
						<div class="col-sm-"><section><label for="autoAccept" class="label">Auto Accept Bids:</label></section></div>
						<div class="col-sm-8">
							<section class="inline-group">
							<label class="radio"><input type=radio id="autoAccept"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableAutoAcceptBidType" class="label">Enable Auto Accept Bids Type: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableAutoAcceptBidType" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showAutoAcceptBidType" hidden>
						<div class="col-sm-4"><section><label for="autoAcceptableBidTypes" class="label">Auto Acceptable Bid Types:</label></section></div>
						<div class="col-sm-8">
							<section class="inline-group">
							<label class="radio"><input type=radio id="autoAcceptableBidTypes"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enablePurAutoAppInv" class="label">Enable Auto Approve Invoice (Buyer): </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enablePurAutoAppInv" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showPurAutoAppInv" hidden>
					<div class="col-sm-4"><section><label for="purchaserAutoApproveInvoice" class="label">Auto Approve Invoice (Buyer):</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="purchaserAutoApproveInvoice"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableSelAutoAppInv" class="label">Enable Auto Approve Invoice (Seller): </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableSelAutoAppInv" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showSelAutoAppInv" hidden>
					<div class="col-sm-4"><section><label for="sellerAutoApproveInvoice" class="label">Auto Approve Invoice (Seller):</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="sellerAutoApproveInvoice"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableChargeBearer" class="label">Enable Charge Bearer: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableChargeBearer" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div id="showChargeBearer" hidden>
				<div class="row">
					<div class="col-sm-4"><section><label for="chargeBearer" class="label">Default Platform Charge Bearer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="chargeBearer" onChange="javascript:onChangeOfCostBearing()"><option value="">Select Charge Bearer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div id="percentSplitCharge" style="display:none">	
					<div class="row">
						<div class="col-sm-4"><section><label for="splittingPointCharge" class="label">Splitting Point:</label></section></div>
						<div class="col-sm-8">
							<section class="select">
							<select id="splittingPointCharge" onChange=""><option value="">Select splitting Point</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="preSplittingCharge" class="label">PreSplitting Charge Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="preSplittingCharge"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="postSplittingCharge" class="label">PostSplitting Charge Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="postSplittingCharge"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
				</div> 	
				<div class="row" id="splitCharge" style="display:none">
					<div class="col-sm-4"><section><label for="buyerPercentCharge" class="label">Charge by Buyer Percent%:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="buyerPercentCharge" placeholder="Buyer Percent">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					 </div>
					<div class="col-sm-4"><section><label for="sellerPercentCharge" class="label">Charge by Seller Percent%:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="sellerPercentCharge" placeholder="Seller Percent">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					 </div>
				</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableCostBearingType" class="label">Enable Cost Bearing: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableCostBearingType" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>	
				<div id="showCostBearingType" hidden>
				<div class="row">
					<div class="col-sm-4"><section><label for="costBearingType" class="label">Default Cost Bearer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="costBearingType" onChange="javascript:onChangeOfCostBearing()"><option value="">Select Cost Bearer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
			   </div>
				<div id="percentSplit" style="display:none">	
					<div class="row">
						<div class="col-sm-4"><section><label for="splittingPoint" class="label">Splitting Point:</label></section></div>
						<div class="col-sm-8">
							<section class="select">
							<select id="splittingPoint" onChange=""><option value="">Select splitting Point</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="preSplittingCostBearer" class="label">PreSplitting Cost Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="preSplittingCostBearer"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="postSplittingCostBearer" class="label">PostSplitting Cost Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="postSplittingCostBearer"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
				</div> 	
				<div class="row" id="split" style="display:none">
					<div class="col-sm-4"><section><label for="buyerPercent" class="label">Cost by Buyer Percent%:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="buyerPercent" placeholder="Buyer Percent">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					 </div>
					<div class="col-sm-4"><section><label for="sellerPercent" class="label">Cost by Seller Percent%:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="sellerPercent" placeholder="Seller Percent">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					 </div>
				</div>
				</div>
			   	<div class="row">
					<div class="col-sm-4"><section><label for="enableBidAcceptingEntityType" class="label">Enable Bid Accepting EntityType: </label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableBidAcceptingEntityType" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>	
				<div class="row" id="showBidAccEntityType" hidden>
					<div class="col-sm-4"><section><label for="bidAcceptingEntityType" class="label">Bid Accepting EntityType:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="bidAcceptingEntityType"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableCashDis" class="label">Enable Cash Discount :</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableCashDis" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showCashDis" hidden>
					<div class="col-sm-4"><section><label for="cashDiscountPercent" class="label">Cash Discount %:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="cashDiscountPercent" placeholder="Cash Discount %">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableHaircut" class="label">Enable Haircut % :</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableHaircut" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showHaircut" hidden>>
					<div class="col-sm-4"><section><label for="haircutPercent" class="label">Haircut %:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="haircutPercent" placeholder="Haircut %">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableSettleLeg3Flag" class="label">Enable Settle Leg 3 :</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableSettleLeg3Flag" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showSettleLeg3Flag" hidden>
					<div class="col-sm-4"><section><label for="settleLeg3Flag" class="label">Settle Leg 3:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="settleLeg3Flag"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
					<div class="col-sm-4 col-sm-offset-1">
						<h4><a class="right_links" href="javascript:;" id=btnViewAucPlan onClick="javascript:getPlan();"><span class="fa fa-columns"></span> View Auction Charge Plan</a></h4>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableInstrumentCreation" class="label">Enable Instrument Creation:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableInstrumentCreation" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showInstrumentCreation" hidden>
					<div class="col-sm-4"><section><label for="instrumentCreation" class="label">Invoice Creation:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="instrumentCreation" name="instrumentCreation"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view view-s"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableRemarks" class="label">Enable Remarks:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableRemarks" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="showRemarks" hidden>
					<div class="col-sm-4"><section><label for="remarks" class="label">Remarks:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="remarks" placeholder="Remarks">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableRelation" class="label">Enable Relation:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableRelation" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div id="showRelation" hidden>
					<div id="purSupReload">
						<div class="row">
							<div class="col-sm-4"><section><label for="relationFlag" class="label">Relation Flag:</label></section></div>
							<div class="col-sm-8">
								<section class="select">
								<select id="relationFlag"><option value="">Select Relation Flag</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
						<div class="row">
							<input type="hidden" id="relationDoc" data-role="xuploadfield" data-file-type="PURSUPRELATIONDOCS" />
							<div class="col-sm-4"><section><label for="relationDoc" class="label">Relation Doc.:</label></section></div>
							<div class="col-sm-8">
								<section id='relationDoc' >
									<button type="button" class="upl-btn-upload btn btn-lg btn-success"><span class="fa fa-upload"></span> Upload</button>
									<button type="button" class="upl-btn-clear btn btn-lg btn-default"><span class="fa fa-remove"></span> Clear</button>
									<span class="upl-info"></span>
									<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
								</section>
							</div>
							<div class="col-sm-4"><section><label for="relationEffectiveDate" class="label">Relation Effective Date:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="relationEffectiveDate" placeholder="Relation Effective Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section class="input">
								<input type="button" id="btnRelationHist" class="btn btn-enter btn-info btn-lg" value="History" onclick="showRelationModal()"></input>
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>
				</div>	
				<div class="row">
					<div class="col-sm-4"><section><label for="enableBuyerTds" class="label">Enable Buyer Tds:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableBuyerTds" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>	
				<div id="showBuyerTds" hidden>
				<div id="buyerTdsDiv">	
					<div class="row">
						<div class="col-sm-4"><section><label for="buyerTds" class="label">Buyer TDS:</label></section></div>
						<div class="col-sm-8">
							<section class="inline-group">
								<label class="radio"><input type=radio id="buyerTds" onclick="showBuyerTds()"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div id="buyTdsPer" hidden>
						<div class="row">
							<div class="col-sm-4"><section><label for="buyerTdsPer" class="label">Buyer TDS %:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
									<input type="text" id="buyerTdsPer" placeholder="Buyer Tds %">
									<b class="tooltip tooltip-top-right"></b></section>
								<section class="view view-s"></section>
							 </div>
						</div>
					</div>
				</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="enableSellerTds" class="label">Enable Seller Tds:</label></section></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableSellerTds" onclick="showHiddenDiv()"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>	
				<div id="showSellerTds" hidden>
				<div id="sellerTdsDiv">	
					<div class="row">
						<div class="col-sm-4"><section><label for="sellerTds" class="label">Seller TDS:</label></section></div>
						<div class="col-sm-8">
							<section class="inline-group">
								<label class="radio"><input type=radio id="sellerTds" onclick="showSellerTds()"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div id="selTdsPer" hidden>
						<div class="row">
							<div class="col-sm-4"><section><label for="sellerTdsPer" class="label">Seller TDS %:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
									<input type="text" id="sellerTdsPer" placeholder="Seller Tds %">
									<b class="tooltip tooltip-top-right"></b></section>
								<section class="view view-s"></section>
						 	</div>
						</div>
					</div>
				</div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info-inverse btn-lg" id=btnSave onclick="saveDataList()">Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>
		
		
		<!-- frmMain -->
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Buyer Seller Link</h1>
			</div>
		</div>
		<div style="display:none" class="xform box" id="frmMain">
    		<div>
				<fieldset>
					<div class="row" id="divAuthorizeRxil">
						<div class="col-sm-2"><section><label for="authorizeRxil" class="label">Authorize RXIL:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
								<label class="radio"><input type=radio id="authorizeRxil" ><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
						<div class="col-sm-4">
							<section class="select view-p">
							<select id="supplier" onChange="javascript:setSupplierRef()"><option value="">Select Seller</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="purchaser" onChange="javascript:setPurchaserRef()"><option value="">Select Buyer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="supGstn" class="label">Seller GSTN:</label></section></div>
						<div class="col-sm-4" >
							<section class="input">
							<input type="text" id="supGstn" placeholder="Seller GSTN" disabled>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="supPan" class="label">Seller PAN:</label></section></div>
						<div class="col-sm-4" >
							<section class="input">
							<input type="text" id="supPan" placeholder="Seller PAN" disabled>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="supplierPurchaserRef" class="label">Buyer Internal Code:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="supplierPurchaserRef" placeholder="Buyer Internal Code">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="purchaserSupplierRef" class="label">Seller Internal Code:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="purchaserSupplierRef" placeholder="Seller Internal Code">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="creditPeriod" class="label">Default Credit Period:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="creditPeriod" placeholder="Default Credit Period">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="extendedCreditPeriod" class="label">Extended Credit Period:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="extendedCreditPeriod" placeholder="Extended Credit Period">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="autoConvert" class="label">Send for Auction:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="autoConvert"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="invoiceMandatory" class="label">Invoice Mandatory:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="invoiceMandatory"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="autoAccept" class="label">Auto Accept Bids:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="autoAccept"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="autoAcceptableBidTypes" class="label">Auto Acceptable Bid Types:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="autoAcceptableBidTypes"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-3"><section><label for="purchaserAutoApproveInvoice" class="label">Auto Approve Invoice (Buyer):</label></section></div>
						<div class="col-sm-3">
							<section class="inline-group">
							<label class="radio"><input type=radio id="purchaserAutoApproveInvoice"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-3"><section><label for="sellerAutoApproveInvoice" class="label">Auto Approve Invoice (Seller):</label></section></div>
						<div class="col-sm-3">
							<section class="inline-group">
							<label class="radio"><input type=radio id="sellerAutoApproveInvoice"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="chargeBearer" class="label">Default Platform Charge Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="chargeBearer" onChange="javascript:onCostBearingChange()"><option value="">Select Charge Bearer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div id="percentSplitCharge" style="display:none">	
						<div class="row">
							<div class="col-sm-2"><section><label for="splittingPointCharge" class="label">Splitting Point:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="splittingPointCharge" onChange=""><option value="">Select splitting Point</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
								<section class="view view-s"></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label for="preSplittingCharge" class="label">PreSplitting Charge Bearer:</label></section></div>
							<div class="col-sm-4">
								<section class="inline-group">
								<label class="radio"><input type=radio id="preSplittingCharge"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
								</section>
								<section class="view view-s"></section>
							</div>
							<div class="col-sm-2"><section><label for="postSplittingCharge" class="label">PostSplitting Charge Bearer:</label></section></div>
							<div class="col-sm-4">
								<section class="inline-group">
								<label class="radio"><input type=radio id="postSplittingCharge"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
								</section>
								<section class="view"></section>
							</div>
						</div>
					</div> 	
					<div class="row" id="splitCharge" style="display:none">
						<div class="col-sm-2"><section><label for="buyerPercentCharge" class="label">Charge by Buyer Percent%:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="buyerPercentCharge" placeholder="Buyer Percent">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						 </div>
						<div class="col-sm-2"><section><label for="sellerPercentCharge" class="label">Charge by Seller Percent%:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="sellerPercentCharge" placeholder="Seller Percent">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						 </div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="costBearingType" class="label">Default Cost Bearer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="costBearingType" onChange="javascript:onCostBearingChange()"><option value="">Select Cost Bearer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
				   </div>
					<div id="percentSplit" style="display:none">	
						<div class="row">
							<div class="col-sm-2"><section><label for="splittingPoint" class="label">Splitting Point:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="splittingPoint" onChange=""><option value="">Select splitting Point</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
								<section class="view view-s"></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label for="preSplittingCostBearer" class="label">PreSplitting Cost Bearer:</label></section></div>
							<div class="col-sm-4">
								<section class="inline-group">
								<label class="radio"><input type=radio id="preSplittingCostBearer"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
								</section>
								<section class="view view-s"></section>
							</div>
							<div class="col-sm-2"><section><label for="postSplittingCostBearer" class="label">PostSplitting Cost Bearer:</label></section></div>
							<div class="col-sm-4">
								<section class="inline-group">
								<label class="radio"><input type=radio id="postSplittingCostBearer"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
								</section>
								<section class="view"></section>
							</div>
						</div>
					</div> 	
					<div class="row" id="split" style="display:none">
						<div class="col-sm-2"><section><label for="buyerPercent" class="label">Cost by Buyer Percent%:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="buyerPercent" placeholder="Buyer Percent">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						 </div>
						<div class="col-sm-2"><section><label for="sellerPercent" class="label">Cost by Seller Percent%:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="sellerPercent" placeholder="Seller Percent">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						 </div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="bidAcceptingEntityType" class="label">Bid Accepting EntityType:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="bidAcceptingEntityType"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="cashDiscountPercent" class="label">Cash Discount %:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="cashDiscountPercent" placeholder="Cash Discount %">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="haircutPercent" class="label">Haircut %:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="haircutPercent" placeholder="Haircut %">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="settleLeg3Flag" class="label">Settle Leg 3:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="settleLeg3Flag"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-4 col-sm-offset-1">
							<h4><a class="right_links" href="javascript:;" id=btnViewAucPlan onClick="javascript:getPlan();"><span class="fa fa-columns"></span> View Auction Charge Plan</a></h4>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="instrumentCreation" class="label">Invoice Creation:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="instrumentCreation" name="instrumentCreation"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
					</div>
				<div id="buyerTdsDiv">	
					<div class="row">
						<div class="col-sm-2"><section><label for="buyerTds" class="label">Buyer TDS:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
								<label class="radio"><input type=radio id="buyerTds" onclick="showBuyerTds()"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="buyerTdsPercent" class="label">Buyer TDS %:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="buyerTdsPercent" placeholder="Buyer Tds %">
								<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
						 </div>
					</div>
				</div>
				<div id="sellerTdsDiv">	
					<div class="row">
						<div class="col-sm-2"><section><label for="sellerTds" class="label">Seller TDS:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
								<label class="radio"><input type=radio id="sellerTds" onclick="showSellerTds()"><i></i><span></span>
								<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view view-s"></section>
						</div>
						<div class="col-sm-2"><section><label for="sellerTdsPercent" class="label">Seller TDS %:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="sellerTdsPercent" placeholder="Seller Tds %">
								<b class="tooltip tooltip-top-right"></b></section>
							<section class="view view-s"></section>
					 	</div>
					</div>
				</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="remarks" class="label">Remarks:</label></section></div>
						<div class="col-sm-10">
							<section class="input">
							<input type="text" id="remarks" placeholder="Remarks">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row hidden">	
							<div class="col-sm-4">
								<section class="select">
								<select id="status" ><option value="">Select Status</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>							
							<div class="col-sm-4">
								<section class="select">
								<select id="approvalStatus" ><option value="">Select App Status</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>							
					</div>
					<div id="purSupReload">
						<div class="row">
							<div class="col-sm-2"><section><label for="relationFlag" class="label">Relation Flag:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="relationFlag"><option value="">Select Relation Flag</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
						<div class="row">
							<input type="hidden" id="relationDoc" data-role="xuploadfield" data-file-type="PURSUPRELATIONDOCS" />
							<div class="col-sm-2"><section><label for="relationDoc" class="label">Relation Doc.:</label></section></div>
							<div class="col-sm-4">
								<section id='relationDoc' >
									<button type="button" class="upl-btn-upload btn btn-lg btn-success"><span class="fa fa-upload"></span> Upload</button>
									<button type="button" class="upl-btn-clear btn btn-lg btn-default"><span class="fa fa-remove"></span> Clear</button>
									<span class="upl-info"></span>
									<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
								</section>
							</div>
							<div class="col-sm-2"><section><label for="relationEffectiveDate" class="label">Relation Effective Date:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="relationEffectiveDate" placeholder="Relation Effective Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section class="input">
								<input type="button" id="btnRelationHist" class="btn btn-enter btn-info btn-lg" value="History" onclick="showRelationModal()"></input>
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>

					<div class="panel-body bg_white">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
							<div class="btn-groupX pull-right  state-P">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
							</div>
							<div class="btn-groupX pull-right  state-T">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnPlatformSave><span class="fa fa-save"></span> Save</button>
							</div>
							<div class="btn-groupX pull-right state-S">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnApprove onClick="javascript:updateStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.Approved.getCode()%>','Accept')"><span class="fa fa-check"></span> Approve</button>
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnReject onClick="javascript:updateStatus('<%=PurchaserSupplierLinkBean.ApprovalStatus.Returned.getCode()%>','Return')"><span class="fa fa-mail-reply"></span> Return</button>
							</div>
						</div>
					</div>
				</fieldset>
    		</div>
    	</div>
    	<!-- frmMain -->
	</div>
	
<script id="tplPurSupRelation" type="text/x-handlebars-template">
<div class="modal-header"><span>Buyer Supplier Relation </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<div class="xform box" id="relationshipStatus">
			<div class="row" colspan="2">
				<div class="col-sm-2"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
				<div class="col-sm-4">
					<section class="input">
					<input type="text" id="purchaser" placeholder="Purchaser" readonly>
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
				<div class="col-sm-2"><section><label for="supplier" class="label">Supplier:</label></section></div>
				<div class="col-sm-4">
					<section class="input">
					<input type="text" id="supplier" placeholder="Supplier" readonly>
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
			</div>
			<div class="row" style="display:none;">
				<div class="col-sm-4"><section><label for="platformStatus" class="label">Platform Status :</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<input type="text" id="platformStatus" placeholder="Platform Status">
					<b class="tooltip tooltip-top-right"></b></section>
				</div>
			</div>
			<div class="row" style="display:none;">
				<div class="col-sm-4"><section><label for="platformReasonCode" class="label">Platform Status :</label></section></div>
				<div class="col-sm-8">
					<section class="select">
					<select id="platformReasonCode"><option value="">Select Platform Reason Code</option></select>
					<b class="tooltip tooltip-top-right"></b><i></i></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2"><section><label for="platformRemarks" class="label">Platform Remarks :</label></section></div>
				<div class="col-sm-10">
					<section class="textarea">
					<textarea id="platformRemarks" placeholder="Platform Remarks"></textarea>
					<b class="tooltip tooltip-top-right"></b></section>
				</div>
			</div>
			<br>
			</br>
			<div class="row">
				<center><input type="button" class="btn btn-info btn-lg" value="" id=btnActivateSuspend onclick="activateSuspendRelation()"></input></center>
			</div>
		</div>
</fieldset>
</div>
</script>


<script id="tplShowRelation" type="text/x-handlebars-template">
<div class="modal-header"><span>Buyer Supplier Relation </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<div>
			{{#if relationshipHistory}}
				<table class="table table-striped table-bordered" style="width:100%">
					<tr>
						<th>Relationship Flag</th>
						<th>Relationship Effective Date</th>
						<th>Relationship Document</th>
					</tr>
					{{#each relationshipHistory}}
						<tr>	
							<td>{{relationFlag}}</td>
							<td>{{startDate}}</td>
							<td>{{relationDocName}}</td>
						</tr>
					{{/each}}
				</table>
			{{/if}}
		</div>
</fieldset>
</div>
</script>


<script id="tplPSLinkWorkFlowView" type="text/x-handlebars-template">
<div class="modal-header"><span>{{purchaser}} - {{supplier}}</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<h3>Purchaser Supplier Link Workflow</h3>
		<div class="row">
			<div class="col-sm-12">
				
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-striped table-bordered" style="width:100%"><tbody>
				<tr>
					<th>Entity</th>
					<th>Login ID</th>
					<th>Status</th>
					<th>Status Remarks</th>
					<th>Update Time</th>
				</tr>
				{{#each workFlows}}
				<tr>
					<td>{{entity}}</td>
					<td>{{loginId}}</td>
					<td>{{status}}</td>
					<td>{{statusRemarks}}</td>
					<td>{{statusUpdateTime}}</td>
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
	
	<script id="tplAucPlanSlabView" type="text/x-handlebars-template">
<div class="modal-header"><span>Auction Charge Plan & Slabs </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<div class="row">
			<div class="col-sm-4 col-sm-offset-2">
				<h3><b>Plan Name : {{name}}<b></h3>
			</div>
			<div class="col-sm-4 col-sm-offset-2">
				<h3><b>Plan Type : 
						{{#ifCond type '==' "<%=AuctionChargePlanBean.Type.Invoice.getCode()%>"}}Invoice{{/ifCond}}
						{{#ifCond type '==' "<%=AuctionChargePlanBean.Type.TurnOver.getCode()%>"}}Turnover{{/ifCond}}
				<b></h3>
			</div>
		</div>
		
		<div class="row">
			<div class="col-sm-12">
				
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-striped table-bordered" style="width:100%"><tbody>
				<tr>
					<th>Min Amount</th>
					<th>Max Amount</th>
					<th>Charge Type</th>
					<th>Charge Absolute Value</th>
					<th>Charge Percent Value</th>
					<th>Charge Max Value</th>
				</tr>
				{{#each auctionChargeSlabList}}
				<tr>
					<td>{{minAmount}}</td>
					<td>{{maxAmount}}</td>
					<td>
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Absolute.getCode()%>"}}Absolute{{/ifCond}}
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Percentage.getCode()%>"}}Percentage{{/ifCond}}		
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Threshold.getCode()%>"}}Threshold{{/ifCond}}
					</td>
					<td>{{chargeAbsoluteValue}}</td>
					<td>{{chargePercentValue}}</td>
					<td>{{chargeMaxValue}}</td>
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
	
   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
	var crudPurchaserSupplierLink$ = null,crudPurchaserSupplierLink,mainForm;
	var crudmodifypursuprel$ = null,crudmodifypursuprel,crudshowrelation$=null,crudshowrelation;
	var crudactsusrelation$=null,crudactsusrelation;
	var crudactauthorizeperson$= null,crudactauthorizeperson;
	var tabIdx, tabData;
	var tplPSLinkWorkView;
	var tplAPSView;
	var tplPurSupRelation;
	var tplPurSupRelHistory;
	var mPurchaser = false;
	var mSupplier = false;
	var lModifyRelation;
	var lShowRelation;
	var lBuyerSellerTds;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class).getJsonConfig()%>;

		lActSusRelation = {
				"fields": [						
						{
							"name":"purchaser",
							"label":"Purchaser",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						},
						{
							"name":"supplier",
							"label":"Supplier",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						},
						{
							"name":"platformStatus",
							"label":"Platform Status",
							"dataType":"STRING",
							"maxLength": 3,
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Active", "value":"ACT"},{"text":"Suspended", "value":"SUS"}]
						},
						{
							"name":"platformReasonCode",
							"label":"Platform Reason Code",
							"dataType":"STRING",
							"maxLength": 1,
							"dataSetType":"RESOURCE",
							"dataSetValues": "pursuplnk/platformreasoncode"
						},
						{
							"name":"platformRemarks",
							"label":"platformRemarks",
							"dataType":"STRING",
							"maxLength":100
						}
				]
			};
		lShowRelation = {
				"fields": [		
						{
							"name":"purchaser",
							"label":"Purchaser",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						},
						{
							"name":"supplier",
							"label":"Supplier",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						},
						{
							"name":"lastRelEffectiveDate",
							"label":"Last Rel Effective Date",
							"dataType":"STRING"
						},
						{
							"name":"relationEffectiveDate",
							"label":"Relation Effective Date",
							"dataType":"DATE",
							"format":"dd-MM-yyyy"
						},
						{
							"name":"relationDoc",
							"label":"Relation Doc",
							"dataType":"STRING",
							"maxLength": 30
						},
						{
							"name":"relationFlag",
							"label":"Relation Flag",
							"dataType":"STRING",
							"maxLength": 1
						}
				]
			};	
		
		lAuthPurchaser = {
				"fields": [		
						{
							"name":"purchaser",
							"label":"Purchaser",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						}
				]
			};
		
		lSupplierList = {
				"fields": [		
						{
							"name":"purchaser",
							"label":"Purchaser",
							"dataType":"STRING",
							"maxLength": 30,
							"dataSetType":"RESOURCE",
							"dataSetValues":"appentity/purchasers"
						},
						{
							"name":"supplier",
							"label":"Supplier List",
							"dataType":"STRING"
						},
						{
							"name":"enableCdtPer",
							"label":"Enable Credit Period",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableExtCdtPer",
							"label":"Enable Ext Credit Period",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableSendAuc",
							"label":"Enable Send for Auction",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableInvMandatory",
							"label":"Enable Invoice Mandatory",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableAutoAccept",
							"label":"Enable Auto Accept",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableAutoAcceptBidType",
							"label":"Enable Auto Accept Bid Type",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enablePurAutoAppInv",
							"label":"Enable Auto Approve Invoice (Buyer)",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableSelAutoAppInv",
							"label":"Enable Auto Approve Invoice (Seller)",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableChargeBearer",
							"label":"Enable Charge Bearer",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableCostBearingType",
							"label":"Enable Cost Bearing Type",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableBidAcceptingEntityType",
							"label":"Enable Bid Accepting Entity Type",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableCashDis",
							"label":"Enable Cash Discount",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableHaircut",
							"label":"Enable Haircut %",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableSettleLeg3Flag",
							"label":"Enable Settle Leg3",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableInstrumentCreation",
							"label":"Enable Instrument Creation",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableRemarks",
							"label":"Enable Remarks",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableRelation",
							"label":"Enable Relation",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableBuyerTds",
							"label":"Enable Buyer Tds",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						},
						{
							"name":"enableSellerTds",
							"label":"Enable Seller Tds",
							"dataType": "STRING",
							"maxLength": 1,
							"defaultValue":"N",
							"dataSetType":"STATIC",
							"dataSetValues": [{"text":"Yes", "value":"Y"}]
						}
						
					
				]
			};
		
		enableRemarks
		lFields = ['creditPeriod','extendedCreditPeriod','costBearingType','autoConvert','invoiceMandatory','instrumentCreation','autoAccept','autoAcceptableBidTypes','purchaserAutoApproveInvoice','sellerAutoApproveInvoice','chargeBearer','buyerPercent','sellerPercent','bidAcceptingEntityType','cashDiscountPercent','haircutPercent','settleLeg3Flag','remarks','relationFlag','relationDoc','relationEffectiveDate','buyerTds','buyerTdsPer','sellerTds','sellerTdsPer','splittingPointCharge','preSplittingCharge','postSplittingCharge','buyerPercentCharge','sellerPercentCharge','splittingPoint','preSplittingCostBearer','postSplittingCostBearer','buyerPercent','sellerPercent'];
		var lLength = lFields.length;
// 		var lSupConfig = [];
		
		$.each(lFormConfig.fields, function(pIndex,pValue){
			for(var lPtr = 0; lPtr <= lLength; lPtr++){
				if(pValue.name == lFields[lPtr]){
					lSupplierList.fields.push(pValue);
				}
			}
		});
		
		supplierListForm$ = $('#frmSupplierFilter').xform(lSupplierList);
		supplierListForm = supplierListForm$.data('xform');	
		
		var lConfig = {
				resource: "pursuplnk",
				autoRefresh: true,
				keyFields:["supplier","purchaser"],
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
				postNewHandler: function() {
					initForm();
					//set default values 
					mainForm.getField('approvalStatus').setValue("<%=PurchaserSupplierLinkBean.ApprovalStatus.Draft.getCode()%>");
					mainForm.getField('status').setValue("<%=PurchaserSupplierLinkBean.Status.Active.getCode()%>");
					mainForm.getField('autoConvert').setValue("<%=AppConstants.AutoConvert.Auto.getCode()%>");
					
					if(mSupplier){
						mainForm.enableDisableField("suppplier", false, false);
						mainForm.getField('supplier').setValue(loginData.domain);
						mainForm.getField('sellerAutoApproveInvoice').setValue("<%=CommonAppConstants.YesNo.No.getCode()%>");
						$('#buyerTdsDiv').hide();
						showSellerTds();
					}else if(mPurchaser){
						mainForm.enableDisableField("purchaser", false, false);
						mainForm.getField('purchaser').setValue(loginData.domain);
						mainForm.getField('autoAccept').setValue("<%=AppConstants.AutoAcceptBid.CutOffTime.getCode()%>");
						mainForm.getField('autoAcceptableBidTypes').setValue("<%=AppConstants.AutoAcceptableBidTypes.AllBids.getCode()%>");
						mainForm.getField('purchaserAutoApproveInvoice').setValue("<%=CommonAppConstants.YesNo.No.getCode()%>");
						mainForm.enableDisableField("purchaserAutoApproveInvoice", false, false);
						mainForm.enableDisableField("instrumentCreation", true, false);
						var lFields = ['relationDoc', 'relationEffectiveDate', 'relationFlag'];
						mainForm.alterField(lFields,false,false);
						lFields = ['buyerTds','buyerTdsPercent','sellerTds','sellerTdsPercent'];
						mainForm.alterField(lFields,false,false);
						$('#sellerTdsDiv').hide();
						showBuyerTds();
					}
					var lFields = ['supGstn', 'supPan'];
					mainForm.enableDisableField(lFields,false,false);
					mainForm.enableDisableField("invoiceMandatory", false, false);
					mainForm.getField('invoiceMandatory').setValue("<%=CommonAppConstants.YesNo.Yes.getCode()%>");
					chargeaccess(mainForm.getField('purchaser').getValue());
					if (mPurchaser || loginData.domain=='TREDS'){
						getIntegration(mainForm.getField('purchaser').getValue());
						if ( loginData.domain=='TREDS'){
							showBuyerTds();
							$('#sellerTdsDiv').hide();
							showBuyerTds();
						}
					}
					mainForm.getField('authorizeRxil').setValue('<%=CommonAppConstants.YesNo.No.getCode()%>');
					$('#divAuthorizeRxil').hide();
					return true;
				},
				postModifyHandler: function(pObj) {
					mainForm.enableDisableField(['purchaser','supplier','purchaserSupplierRef','supplierPurchaserRef'],false,false);					
					initForm();
					onCostBearingChange();	
					$('#divAuthorizeRxil').hide();
					if (pObj.authorizeRxil!=null){
						mainForm.getField('authorizeRxil').setValue(pObj.authorizeRxil);
					}else{
						mainForm.getField('authorizeRxil').setValue('<%=CommonAppConstants.YesNo.No.getCode()%>');
					}
					if (loginData.entityType.substring(0,1) == 'Y') {
						if(mainForm.getField('supplierPurchaserRef').getValue()==null||
								mainForm.getField('supplierPurchaserRef').getValue()==''){
							mainForm.enableDisableField(['supplierPurchaserRef'],true,false);
						}
						if(mainForm.getField('sellerAutoApproveInvoice').getValue() == null){
							mainForm.getField('sellerAutoApproveInvoice').setValue("<%=CommonAppConstants.YesNo.No.getCode()%>");
						}
						$('#buyerTdsDiv').hide();
						showSellerTds();
					}else if (loginData.entityType.substring(1,2) == 'Y') {
						mainForm.enableDisableField("instrumentCreation", true, false);
						if(mainForm.getField('purchaserSupplierRef').getValue()==null||
								mainForm.getField('purchaserSupplierRef').getValue()==''){
							mainForm.enableDisableField(['purchaserSupplierRef'],true,false);
						}
						mainForm.enableDisableField(['purchaserAutoApproveInvoice'],false,false);
						var lFields = ['relationDoc', 'relationEffectiveDate', 'relationFlag'];
						mainForm.alterField(lFields,false,false);
						mainForm.enableDisableField('relationDoc',pObj.relationFlag=='<%=CommonAppConstants.YesNo.Yes.getCode()%>',false);
						$('#sellerTdsDiv').hide();
						showBuyerTds();
						if (pObj.supplier == 'TEMPLATE'){
							var lSupTmpField=mainForm.getField("supplier");
							var lOptions=lSupTmpField.getOptions();
							lOptions.dataSetValues = [{"text":"TEMPLATE","value":"TEMPLATE"}];
							lSupTmpField.init();
							mainForm.getField("supplier").setValue("TEMPLATE");
							mainForm.enableDisableField(['purchaserAutoApproveInvoice'],true,false);
							$('#divAuthorizeRxil').show();
						}
					}else if (loginData.domain='TREDS'){
						mainForm.enableDisableField(['supplier','purchaserSupplierRef'],true,false);
					}
					var lFields = ['supGstn', 'supPan'];
					mainForm.enableDisableField(lFields,false,false);
					mainForm.enableDisableField("invoiceMandatory", false, false);
					lFields = ['buyerTds','buyerTdsPercent','sellerTds','sellerTdsPercent'];
					mainForm.alterField(lFields,false,false);
					mainForm.enableDisableField(lFields,true,false);
					if (loginData.entityType.substring(1,2) == 'Y' || loginData.domain=='TREDS'){
						getIntegration(mainForm.getField('purchaser').getValue());
						if ( loginData.domain=='TREDS'){
							showBuyerTds();
							$('#sellerTdsDiv').hide();
							showBuyerTds();
						}
					}
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		
		 var lPlatform = (loginData.domain == "<%=AppConstants.DOMAIN_PLATFORM%>");
		 if(!lPlatform){
			 var lSupp = (loginData.entityType.substring(0,1) == 'Y');
			 if(lSupp){
				 $('#frmSearch #supName').attr('data-sel-exclude',true);
				 $('#frmSearch #supName').attr('data-visible',false);
			 }
			 var lPurch = (loginData.entityType.substring(1,2) == 'Y');
			 if(lPurch){
				 $('#frmSearch #purName').attr('data-sel-exclude',true);
				 $('#frmSearch #purName').attr('data-visible',false);
			 }
			 if(lSupp){
				 $('#frmSearch #tabLabel')[0].innerText = "Inbox/Pending Acceptance";
				 $('#frmSearch #tabPending').attr('style','display:none');
			 }
		 }

		crudPurchaserSupplierLink$ = $('#contPurchaserSupplierLink').xcrudwrapper(lConfig);
		crudPurchaserSupplierLink=crudPurchaserSupplierLink$.data('xcrudwrapper');
		mainForm=crudPurchaserSupplierLink.options.mainForm;
		tplAPSView = Handlebars.compile($('#tplAucPlanSlabView').html());
		tplShowRelation = Handlebars.compile($('#tplShowRelation').html());
		tplPurSupRelation = Handlebars.compile($('#tplPurSupRelation').html());
		//
		//
		$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
			var lRef1 = $(event.target).attr('href');         // active tab
			var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
			tabIdx = parseInt(lRef1.substring(4));
			if (lRef2)
				$('.btn-group'+lRef2.substring(4)).addClass('hidden');
			$('.btn-group'+tabIdx).removeClass('hidden');
      	   showData();
		});
		$('#frmSearch .nav-tabs a:first').tab('show');
			
		$('#frmMain #split #buyerPercent').on('blur', function() {
			var lBuyerPercent=mainForm.getField('buyerPercent').getValue();
			var x = parseFloat(lBuyerPercent);
			if (isNaN(x) || x < 0 || x > 100){
				alert ('Buyer percent value should be between 0 to 100%');
				resetForm($('#frmMain #split'));
			} 
			else{
				var lSellerPercent=(100-x);
				mainForm.getField('sellerPercent').setValue(lSellerPercent);
			}
 		});
		$('#frmMain #splitCharge #buyerPercentCharge').on('blur', function() {
			var lBuyerPercent=mainForm.getField('buyerPercentCharge').getValue();
			var x = parseFloat(lBuyerPercent);
			if (isNaN(x) || x < 0 || x > 100){
				alert ('Buyer percent value should be between 0 to 100%');
				resetForm($('#frmMain #splitCharge'));
			} 
			else{
				var lSellerPercent=(100-x);
				mainForm.getField('sellerPercentCharge').setValue(lSellerPercent);
			}
 		});
		$("#btnDownloadPSLInstructions").on('click',function(){
     			window.open("../static/PSLInstruction.pdf","_blank");
     	});
		
		$("#btnActivateRelation").on('click',function(){
			var lSelected = crudPurchaserSupplierLink.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			if(1==2 && lSelected.data().platformStatus == null){
				alert("Relation not found.");
				return true;
			}else if(1==2 && lSelected.data().platformStatus == '<%=PlatformStatus.Active%>'){
				alert("Relation is already active.");
				return true;
			}else{
				$('#mdlPurSupRelation').find('.modal-content').html(tplPurSupRelation);
				crudactsusrelation$ = $('#relationshipStatus').xform(lActSusRelation);
				crudactsusrelation = crudactsusrelation$.data('xform');
				crudactsusrelation.getField('purchaser').setValue(lSelected.data().purchaser);
				crudactsusrelation.getField('supplier').setValue(lSelected.data().supplier);
				crudactsusrelation.getField('platformStatus').setValue('<%=PlatformStatus.Active.getCode()%>');
				crudactsusrelation.getField('platformReasonCode').setValue(1);
				$('#mdlPurSupRelation #btnActivateSuspend').val('<%=PlatformStatus.Active%>');
				showModal($('#mdlPurSupRelation'));
			}
		});
		$("#btnSuspendRelation").on('click',function(){
			var lSelected = crudPurchaserSupplierLink.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			// Doubt whether below condition is required
			if(1==2 && lSelected.data().platformStatus == null){
				alert("Relation not found.");
				return true;
			}else if( 1==2 && lSelected.data().platformStatus == '<%=PlatformStatus.Suspended%>'){
				alert("Relation is already suspended.");
				return true;
			}else{
				$('#mdlPurSupRelation').find('.modal-content').html(tplPurSupRelation);
				crudactsusrelation$ = $('#relationshipStatus').xform(lActSusRelation);
				crudactsusrelation = crudactsusrelation$.data('xform');				
				crudactsusrelation.getField('purchaser').setValue(lSelected.data().purchaser);
				crudactsusrelation.getField('supplier').setValue(lSelected.data().supplier);
				crudactsusrelation.getField('platformStatus').setValue('<%=PlatformStatus.Suspended.getCode()%>');
				crudactsusrelation.getField('platformReasonCode').setValue(2);
				$('#mdlPurSupRelation #btnActivateSuspend').val('<%=PlatformStatus.Suspended%>');
				showModal($('#mdlPurSupRelation'));
			}
		});
		
		$('#frmMain #relationFlag').on('change', function(){
			mainForm.enableDisableField('relationDoc',mainForm.getField('relationFlag').getValue()=='<%=CommonAppConstants.YesNo.Yes.getCode()%>',false);

		});
		
		$('#btnOpenTemplate').on('click', function(){
			var lPurchaser = crudactauthorizeperson.getField('purchaser').getValue();
			if (lPurchaser!=null){
				crudPurchaserSupplierLink.modifyHandler(null,['TEMPLATE',lPurchaser],false);
				$('#mdlAuthPurchaser').hide();
				$(".page-body").css("overflow-y","scroll");
				var lData = {};
				lData['purchaser'] = lPurchaser;
				$.ajax( {
		            url: "pursuplnk//template/suppliers",
		            type: "POST",
		            async: false,
		            data: JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	                	var lSupp = mainForm.getField('supplier');
	                	lSupp.options.dataSetType='STATIC';
	                	lSupp.options.dataSetValues=pObj;
	                	lSupp.init();
		            },
		        	error: errorHandler,
			    	complete: function(){
			    	}
		        });	
			}else{
				alert ('Please select a purchaser');
			}
		});
		
		$('#btnPlatformSave').on('click', function(){
			saveLink();
		});
		
	});
		
	function initForm() {
		if (loginData.entityType.substring(0,1) == 'Y') { 
			//for supplier
			mainForm.enableDisableField(crudPurchaserSupplierLink.options.fieldGroups.updatePurchaser, false, false);
			mainForm.alterField(crudPurchaserSupplierLink.options.fieldGroups.updateSupplier, true, false);
			mainForm.enableDisableField("bidAcceptingEntityType",false,false);
			mSupplier=true;
		}
		if (loginData.entityType.substring(1,2) == 'Y') {
			//for purchaser
			mainForm.enableDisableField(crudPurchaserSupplierLink.options.fieldGroups.updateSupplier, false, false);
			mainForm.alterField(crudPurchaserSupplierLink.options.fieldGroups.updatePurchaser, true, false);
			mainForm.enableDisableField("sellerPercent",false,false);
			mainForm.enableDisableField("sellerPercentCharge",false,false);
			mainForm.alterField(['extendedCreditPeriod'], false, false);
			$('#frmMain #percentSplit').attr('style','display:none');
			$('#frmMain #split').attr('style','display:none');
			$('#frmMain #percentSplitCharge').attr('style','display:none');
			$('#frmMain #splitCharge').attr('style','display:none');
			mPurchaser = true;
		}
		mainForm.enableDisableField("autoConvert",(mPurchaser||mSupplier),false);
		//if(mSupplier) {
			//$('#frmMain #myMainXForm').addClass('view-s');
			//$('#frmMain #remarks').addClass('view-p');
		//}
	}
	function showData() {
		crudPurchaserSupplierLink.options.dataTable.rows().clear();
		if (tabData && (tabData[tabIdx] != null))
			crudPurchaserSupplierLink.options.dataTable.rows.add(tabData[tabIdx]).draw();
		
		var lPurch = (loginData.entityType!= null && loginData.entityType.substring(1,2) == 'Y');
		var lShow = ((lPurch && tabIdx == 2));
		$('#frmSearch #btnSendReminder').attr('style',lShow?'':'display:none');
	}
	function setCapRate()
	{
		var lUrl = "caprate?counterEntity=";
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
		if(lSelected.data())
		{
			var lPurchaser = lSelected.data()["purchaser"];
			if(lPurchaser!=null)
			{
				lUrl += lPurchaser;
				location.href = lUrl;
			}
		}
	}
	function setSupplierRef() {
		if (loginData.entityType.substring(1,2) == 'Y') {
		var lSupp = $("#contPurchaserSupplierLink #frmMain #supplier option:selected")[0].value;
			if(lSupp!=null){
			mainForm.getField('purchaserSupplierRef').setValue(lSupp);
			}
			updateSupplierDetails(lSupp);
		}
	}
	function setPurchaserRef() {
		if (loginData.entityType.substring(0,1) == 'Y') {
		var lPur = $("#contPurchaserSupplierLink #frmMain #purchaser option:selected")[0].value;
		if(lPur!=null)
			mainForm.getField('supplierPurchaserRef').setValue(lPur);
		}
	}
	function updateAppStatus(pStatus, pDesc) {
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		prompt("You are about to " + pDesc + " selected record.<br>Please enter suitable reason/remarks",pDesc,function(pReason){
			var lData = {id:crudPurchaserSupplierLink.selectedRowKey(lSelected.data()),approvalStatus: pStatus,approvalRemarks : pReason};
			$.ajax( {
	            url: "pursuplnk/status",
	            type: "POST",
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
            		alert("Status updated successfully", "Information", function() {
            			crudPurchaserSupplierLink.showSearchForm();
            		});
	            },
	        	error: errorHandler
	        });					
		});
	}
	function updateStatus(pStatus, pDesc) {
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		// validations
		var lErrors = mainForm.check();
		if ((lErrors != null) && (lErrors.length > 0)) {
			crudPurchaserSupplierLink.showError();
			return;
		}

		var lRemarks = mainForm.getField('remarks').getValue();
		var lAutoApprove = mainForm.getField('sellerAutoApproveInvoice').getValue();
		
		if(lRemarks==null||lRemarks==""){
			alert("Please input the remarks before performing the action.");
			return;
		}

		confirm("You are about to " + pDesc + " selected record. Are you sure?",'Confirmation','Yes',function(pYes){
			if (pYes) {
				var lData = null;
				var lRef = mainForm.getField('supplierPurchaserRef').getValue();
				var lAutoCon=mainForm.getField('autoConvert').getValue();
				var lBuyerTds = mainForm.getField('buyerTds').getValue();
				var lBuyerTdsPercent = mainForm.getField('buyerTdsPercent').getValue();
				var lSellerTds = mainForm.getField('sellerTds').getValue();
				var lSellerTdsPercent = mainForm.getField('sellerTdsPercent').getValue();
				if(pStatus=='<%=PurchaserSupplierLinkBean.ApprovalStatus.Approved.getCode()%>'){
					lData = {id:crudPurchaserSupplierLink.selectedRowKey(lSelected.data()),approvalStatus: pStatus, supplierPurchaserRef : lRef, sellerAutoApproveInvoice : lAutoApprove, approvalRemarks : lRemarks, autoConvert : lAutoCon, buyerTds:lBuyerTds, buyerTdsPercent:lBuyerTdsPercent,sellerTds:lSellerTds,sellerTdsPercent:lSellerTdsPercent};
				}else if(pStatus=='<%=PurchaserSupplierLinkBean.ApprovalStatus.Returned.getCode()%>'){
					lData = {id:crudPurchaserSupplierLink.selectedRowKey(lSelected.data()),approvalStatus: pStatus, supplierPurchaserRef : lRef, sellerAutoApproveInvoice : lAutoApprove, approvalRemarks : lRemarks, autoConvert : lAutoCon};
				}
				$.ajax( {
		            url: "pursuplnk/status",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudPurchaserSupplierLink.showSearchForm();
	            		});
		            },
		        	error: errorHandler
		        });					
			} 
		});
	}
	function modifyCode() {
	
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
		var lRef=lSelected.data()["supplierPurchaserRef"];
		var lEntity = "";
		if (loginData.entityType.substring(0,1) == 'Y') {
			//SUPPLIER
			lEntity="Buyer";
			lRef=lSelected.data()["supplierPurchaserRef"];
		}else if (loginData.entityType.substring(1,2) == 'Y') {
			//PURCHASER
			lEntity="Supplier";
			lRef=lSelected.data()["purchaserSupplierRef"];
		}
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		    prompt("Enter the " + lEntity + " Internal Code", lEntity + " Internal Code",function(pCode){
			var lData = {id:crudPurchaserSupplierLink.selectedRowKey(lSelected.data()), code : pCode};
			$.ajax( {
	            url: "pursuplnk/modifyCode",
	            type: "POST",
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
            		alert(lEntity + " Internal Code update successfully", "Information", function() {
            			crudPurchaserSupplierLink.showSearchForm();
            		});
	            },
	        	error: errorHandler
	        });					
		});
	}
	
	function onCostBearingChange(){
		var lSplitPercent=false;
		var lSplitPeriodical=false;
		var lSplitPercentCharge=false;
		var lSplitPeriodicalCharge=false;
		var lNoSplit=false;
		var lCostBearingType=mainForm.getField('costBearingType').getValue(); 
		var lSupplier = (loginData.entityType.substring(0,1) == 'Y');
		
		lSplitPercent = (lCostBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodical = (lCostBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		lNoSplit = ((lCostBearingType == "<%=AppConstants.CostBearingType.Seller.getCode()%>") || (lCostBearingType == "<%=AppConstants.CostBearingType.Buyer.getCode()%>") );
		//
		$('#frmMain #split').attr('style',lSplitPercent?'':'display:none');
		$('#frmMain #percentSplit').attr('style',lSplitPeriodical?'':'display:none');
		//
		mainForm.alterField("buyerPercent", lSplitPercent, false);
		mainForm.alterField("sellerPercent", lSplitPercent, false);
		mainForm.alterField("splittingPoint", lSplitPeriodical, false);
		mainForm.alterField("preSplittingCostBearer", lSplitPeriodical,false);
		mainForm.alterField("postSplittingCostBearer", lSplitPeriodical,false);
		mainForm.alterField("bidAcceptingEntityType", (lSplitPercent || lSplitPeriodical) ,false);
		
		var lChargeBearingType=mainForm.getField('chargeBearer').getValue(); 
		
		lSplitPercentCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodicalCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		//
		$('#frmMain #splitCharge').attr('style',lSplitPercentCharge?'':'display:none');
		$('#frmMain #percentSplitCharge').attr('style',lSplitPeriodicalCharge?'':'display:none');
		//
		mainForm.alterField("buyerPercentCharge", lSplitPercentCharge, false);
		mainForm.alterField("sellerPercentCharge", lSplitPercentCharge, false);
		mainForm.alterField("splittingPointCharge", lSplitPeriodicalCharge, false);
		mainForm.alterField("preSplittingCharge", lSplitPeriodicalCharge,false);
		mainForm.alterField("postSplittingCharge", lSplitPeriodicalCharge,false);
		//
		if(lNoSplit) mainForm.getField('bidAcceptingEntityType').setValue(lCostBearingType);
		//
		mainForm.enableDisableField("bidAcceptingEntityType",!lSupplier&&!lNoSplit,false);
	}
	
	function downloadCsv() {
		//var lFilter={tabIndex : tabIdx };
		//downloadFile('pursuplnk/all',null,JSON.stringify(lFilter));
		downloadFile('pursuplnk/all',null,JSON.stringify({"columnNames" : crudPurchaserSupplierLink.getVisibleColumns(), "tab": tabIdx}));
	}

	$('#btnUpload').on('click', function() {
		$('#mdlPSLinkImpInstruction').modal('hide');
		showRemote('upload?url=pursuplnk', null, false);
	});
	$('#btnInstructions').on('click', function() {
		showModal($('#mdlPSLinkImpInstruction'));	
	});
	function resetForm($form){
	    $form.find('input:text, input:password, input:file, select, textarea').val('');
	    $form.find('input:radio, input:checkbox')
	    .removeAttr('checked').removeAttr('selected');
	}
	function viewWorkFlow(){
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		var lPurchaser = lSelected.data()["purchaser"];
		var lSupplier = lSelected.data()["supplier"];
		var lUrl = 'pursuplnk/'+lSupplier+'/'+lPurchaser; 
		tplPSLinkWorkView = Handlebars.compile($('#tplPSLinkWorkFlowView').html());
		$.ajax({
			url: lUrl,
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
	        	var lModal$ = $('#mdlPSLinkWorkFlow');
	        	lModal$.find('.modal-content').html(tplPSLinkWorkView(pObj));
	        	showModal(lModal$);
			},
			error: errorHandler,
		});
	}
	function sendReminder(){
		var lSelected = crudPurchaserSupplierLink.getSelectedRow();
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
	    prompt("You are about to send a Reminder to Counter for Acceptance.",function(pCode){
			var lData = {id:crudPurchaserSupplierLink.selectedRowKey(lSelected.data())};
			$.ajax( {
	            url: "pursuplnk/sendReminder",
	            type: "POST",
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
	           		alert("Reminder mail sent successfully", "Information", function() {
	           		});
	            },
	        	error: errorHandler
	        });					
		});
	}
	function getPlan(){
		var lPurchaser = null;
		if(loginData.entityType.substring(1,2) == 'Y'){
			lPurchaser = loginData.domain;
		}else{
			var lSelected = crudPurchaserSupplierLink.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			lPurchaser = lSelected.data()["purchaser"];
		}
		$.ajax( {
	        url: 'auctionchargeplans/entity/'+lPurchaser,
	        type: "POST",
	        success: function( pObj, pStatus, pXhr){
	        	var lModal$ = $('#mdlAucPlan');
	        	lModal$.find('.modal-content').html(tplAPSView(pObj));
	        	showModal(lModal$);
	        },
	    	error: errorHandler
	    });
	}	
	
	function activateSuspendRelation(){
		lData={};
		lData['purchaser'] = $('#mdlPurSupRelation #purchaser').val();
		lData['supplier'] = $('#mdlPurSupRelation #supplier').val();
		lData['platformStatus'] = $('#mdlPurSupRelation #platformStatus').val();
		lData['platformRemarks'] = $('#mdlPurSupRelation #platformRemarks').val();
		lData['platformReasonCode'] = $('#mdlPurSupRelation #platformReasonCode').val();
		$.ajax( {
            url: "pursuplnk/updaterelationstatus",
            type: "POST",
            data:JSON.stringify(lData),
            success: function( pObj, pStatus, pXhr) {
            	alert("Relation updated successfully.");
            	$('#mdlPurSupRelation').hide();
            },
        	error: errorHandler,
	    	complete: function(){
	    		refreshState()	    	
	    	}
        });	
	}
	
	function showRelationModal(){
		var lData = {};
		var lRelValue = null
		lData['purchaser'] = mainForm.getField('purchaser').getValue();
		lData['supplier'] = mainForm.getField('supplier').getValue();
		$.ajax( {
            url: "pursuprelationshiphistory/getPurSupRelHistory",
            type: "POST",
            async: false,
            data:JSON.stringify(lData),
            success: function( pObj, pStatus, pXhr) {
            	$('#mdlShowRelation').find('.modal-content').html(tplShowRelation(pObj));
            },
        	error: errorHandler,
        });
		showModal($('#mdlShowRelation'));
	}
	
	function chargeaccess(pCode){
		var lTempField=mainForm.getField("chargeBearer");
		var lOptions=lTempField.getOptions();
		$('.reset').html('<label class="radio"><input type=radio id="chargeBearer"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>');
		lOptions.dataSetValues = [];
		lTempField.init();
		lOptions.dataSetValues = "pursuplnk/chargeaccess/"+pCode;
		lTempField.init();
	}
	
	function updateSupplierDetails(pSupp){
		//set blank
		mainForm.getField('supGstn').setValue("");
		mainForm.getField('supPan').setValue("");
		if(pSupp!=null){
			//ajax call for PAN and GSTN
			$.ajax( {
	            url: "appentity/gstnpan/"+pSupp,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	if(pObj!=null){
		        		mainForm.getField('supGstn').setValue(pObj.gstn);
		        		mainForm.getField('supPan').setValue(pObj.pan);
	            	}
	            },
	        	error: errorHandler,
		    	complete: function(){
		    	}
	        });	
		}
	}
	function showBuyerTds(){
		if(mainForm.getField('buyerTds').getValue()=='<%=CommonAppConstants.YesNo.Yes.getCode()%>'){
			mainForm.enableDisableField('buyerTdsPercent',true,false);
			mainForm.alterField('buyerTdsPercent',true,false);
		}else {
			mainForm.enableDisableField('buyerTdsPercent',false,false);
			mainForm.alterField('buyerTdsPercent',false,false);
		}
	} 
	
	function showSellerTds(){
		if(mainForm.getField('sellerTds').getValue()=='<%=CommonAppConstants.YesNo.Yes.getCode()%>'){
			mainForm.enableDisableField('sellerTdsPercent',true,false);
			mainForm.alterField('sellerTdsPercent',true,false);
		}else {
			mainForm.enableDisableField('sellerTdsPercent',false,false);
			mainForm.alterField('sellerTdsPercent',false,false);
		}
	}
	
	function getIntegration(pCode){
		if (pCode!=null){
			$.ajax( {
				url: "appentity/isInt/"+pCode,
	            type: "GET",
	            async: false,
	            success: function( pObj, pStatus, pXhr) {
	            	if(pObj!=null){
				if(pObj.isInt == '<%=CommonAppConstants.Yes.Yes.getCode()%>'){
					mainForm.enableDisableField('invoiceMandatory',true,false);
				}else{
					mainForm.enableDisableField('invoiceMandatory',false,false);
				}
	            	}
	            },
	        	error: errorHandler,
		    	complete: function(){
		    	}
	        });	
		}
	}
	
	function getTemplate(){
		crudPurchaserSupplierLink.modifyHandler(null,["TEMPLATE",loginData.domain],false);
	}
	
	function saveLink(){
		var lData = [];
		lData.push(mainForm.getValue());
		$.ajax( {
			url: "v1/pursuplnk/bulkupload",
            type: "POST",
            async: false,
            data:JSON.stringify(lData),
            success: function( pObj, pStatus, pXhr) {
            	if(pObj!=null){
					alert("Success");
            	}
            },
        	error: errorHandler,
	    	complete: function(){
	    	}
        });	
	}
	
	function getAuthPurchaser(){
		var lData = {};
		lData['authorizeRxil'] = '<%=CommonAppConstants.YesNo.Yes.getCode()%>';
			$.ajax( {
	            url: "pursuplnk/authPurchaser",
	            type: "POST",
	            data: JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
					crudactauthorizeperson$ = $('#authorizeTreds').xform(lAuthPurchaser);
	        		crudactauthorizeperson = crudactauthorizeperson$.data('xform');
                	var lPurchaser = crudactauthorizeperson.getField('purchaser');
                	lPurchaser.options.dataSetType='STATIC';
                	lPurchaser.options.dataSetValues=pObj;
                	lPurchaser.init();
	            	showModal($('#mdlAuthPurchaser'));
	            },
	        	error: errorHandler,
		    	complete: function(){
		    	}
	        });	
		showModal($('#mdlAuthPurchaser'));
		
	}
	
	function getSuppList(){
		if(loginData.domain != 'TREDS'){
			supplierListForm.getField('purchaser').setValue(loginData.domain);
		}
		getSupplierList();
// 		supplierListForm.getField('purchaser').setValue(loginData.domain);
		showModal($('#mdlSupplierList'));
	}
	
	function chargeAccess(pCode){
		var lTempField=supplierListForm.getField("chargeBearer");
		var lOptions=lTempField.getOptions();
		$('.reset').html('<label class="radio"><input type=radio id="chargeBearer"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>');
		lOptions.dataSetValues = [];
		lTempField.init();
		lOptions.dataSetValues = "pursuplnk/chargeaccess/"+pCode;
		lTempField.init();
	}
	
	function onChangeOfCostBearing(){
		var lSplitPercent=false;
		var lSplitPeriodical=false;
		var lSplitPercentCharge=false;
		var lSplitPeriodicalCharge=false;
		var lNoSplit=false;
		var lCostBearingType=supplierListForm.getField('costBearingType').getValue(); 
		var lSupplier = (loginData.entityType.substring(0,1) == 'Y');
		
		lSplitPercent = (lCostBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodical = (lCostBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		lNoSplit = ((lCostBearingType == "<%=AppConstants.CostBearingType.Seller.getCode()%>") || (lCostBearingType == "<%=AppConstants.CostBearingType.Buyer.getCode()%>") );
		
		$('#frmSupplierFilter #split').attr('style',lSplitPercent?'':'display:none');
		$('#frmSupplierFilter #percentSplit').attr('style',lSplitPeriodical?'':'display:none');
		//
		supplierListForm.alterField("buyerPercent", lSplitPercent, false);
		supplierListForm.alterField("sellerPercent", lSplitPercent, false);
		supplierListForm.alterField("splittingPoint", lSplitPeriodical, false);
		supplierListForm.alterField("preSplittingCostBearer", lSplitPeriodical,false);
		supplierListForm.alterField("postSplittingCostBearer", lSplitPeriodical,false);
		supplierListForm.alterField("bidAcceptingEntityType", (lSplitPercent || lSplitPeriodical) ,false);
		
		var lChargeBearingType=supplierListForm.getField('chargeBearer').getValue(); 
		
		lSplitPercentCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodicalCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		//
		$('#frmSupplierFilter #splitCharge').attr('style',lSplitPercentCharge?'':'display:none');
		$('#frmSupplierFilter #percentSplitCharge').attr('style',lSplitPeriodicalCharge?'':'display:none');
		//
		supplierListForm.alterField("buyerPercentCharge", lSplitPercentCharge, false);
		supplierListForm.alterField("sellerPercentCharge", lSplitPercentCharge, false);
		supplierListForm.alterField("splittingPointCharge", lSplitPeriodicalCharge, false);
		supplierListForm.alterField("preSplittingCharge", lSplitPeriodicalCharge,false);
		supplierListForm.alterField("postSplittingCharge", lSplitPeriodicalCharge,false);
		//
		if(lNoSplit) supplierListForm.getField('bidAcceptingEntityType').setValue(lCostBearingType);
		//
		supplierListForm.enableDisableField("bidAcceptingEntityType",!lNoSplit,false);		
	}
	
	function showHiddenDiv(){
		if(supplierListForm.getField('enableCdtPer').getValue() == 'Y'){
			$('#showCredPer').show();
		}else{
			$('#showCredPer').hide();
		}
		if(supplierListForm.getField('enableExtCdtPer').getValue() == 'Y'){
			$('#showExtCdtPer').show();
		}else{
			$('#showExtCdtPer').hide();
		}
		if(supplierListForm.getField('enableSendAuc').getValue() == 'Y'){
			$('#showAutoConvert').show();
		}else{
			$('#showAutoConvert').hide();
		}
		if(supplierListForm.getField('enableInvMandatory').getValue() == 'Y'){
			$('#showInvMandatory').show();
		}else{
			$('#showInvMandatory').hide();
		}
		if(supplierListForm.getField('enableAutoAccept').getValue() == 'Y'){
			$('#showAutoAccept').show();
		}else{
			$('#showAutoAccept').hide();
		}
		if(supplierListForm.getField('enableAutoAcceptBidType').getValue() == 'Y'){
			$('#showAutoAcceptBidType').show();
		}else{
			$('#showAutoAcceptBidType').hide();
		}
		if(supplierListForm.getField('enablePurAutoAppInv').getValue() == 'Y'){
			$('#showPurAutoAppInv').show();
		}else{
			$('#showPurAutoAppInv').hide();
		}
		if(supplierListForm.getField('enableSelAutoAppInv').getValue() == 'Y'){
			$('#showSelAutoAppInv').show();
		}else{
			$('#showSelAutoAppInv').hide();
		}
		if(supplierListForm.getField('enableChargeBearer').getValue() == 'Y'){
			$('#showChargeBearer').show();
		}else{
			$('#showChargeBearer').hide();
		}
		if(supplierListForm.getField('enableCostBearingType').getValue() == 'Y'){
			$('#showCostBearingType').show();
		}else{
			$('#showCostBearingType').hide();
		}
		if(supplierListForm.getField('enableBidAcceptingEntityType').getValue() == 'Y'){
			$('#showBidAccEntityType').show();
		}else{
			$('#showBidAccEntityType').hide();
		}
		if(supplierListForm.getField('enableCashDis').getValue() == 'Y'){
			$('#showCashDis').show();
		}else{
			$('#showCashDis').hide();
		}
		if(supplierListForm.getField('enableHaircut').getValue() == 'Y'){
			$('#showHaircut').show();
		}else{
			$('#showHaircut').hide();
		}
		if(supplierListForm.getField('enableSettleLeg3Flag').getValue() == 'Y'){
			$('#showSettleLeg3Flag').show();
		}else{
			$('#showSettleLeg3Flag').hide();
		}
		if(supplierListForm.getField('enableInstrumentCreation').getValue() == 'Y'){
			$('#showInstrumentCreation').show();
		}else{
			$('#showInstrumentCreation').hide();
		}
		if(supplierListForm.getField('enableRemarks').getValue() == 'Y'){
			$('#showRemarks').show();
		}else{
			$('#showRemarks').hide();
		}
		if(supplierListForm.getField('enableRelation').getValue() == 'Y'){
			$('#showRelation').show();
		}else{
			$('#showRelation').hide();
		}
		if(supplierListForm.getField('enableBuyerTds').getValue() == 'Y'){
			$('#showBuyerTds').show();
		}else{
			$('#showBuyerTds').hide();
		}
		if(supplierListForm.getField('enableSellerTds').getValue() == 'Y'){
			$('#showSellerTds').show();
		}else{
			$('#showSellerTds').hide();
		}
	}
	
	function saveDataList(){
		var lData = {};
		lData['purchaser'] = supplierListForm.getField('purchaser').getValue();
		lData['supplier'] = supplierListForm.getField('supplier').getValue();
		if(supplierListForm.getField('enableCdtPer').getValue() == 'Y'){
			lData['enableCdtPer'] = supplierListForm.getField('enableCdtPer').getValue();
			lData['creditPeriod'] = supplierListForm.getField('creditPeriod').getValue();
		}
		if(supplierListForm.getField('enableExtCdtPer').getValue() == 'Y'){
			lData['enableExtCdtPer'] = supplierListForm.getField('enableExtCdtPer').getValue();
			lData['extendedCreditPeriod'] = supplierListForm.getField('extendedCreditPeriod').getValue();
		}
		if(supplierListForm.getField('enableSendAuc').getValue() == 'Y'){
			lData['enableSendAuc'] = supplierListForm.getField('enableSendAuc').getValue();
			lData['autoConvert'] = supplierListForm.getField('autoConvert').getValue();
		}
		if(supplierListForm.getField('enableInvMandatory').getValue() == 'Y'){
			lData['enableInvMandatory'] = supplierListForm.getField('enableInvMandatory').getValue();
			lData['invoiceMandatory'] = supplierListForm.getField('invoiceMandatory').getValue();
		}
		if(supplierListForm.getField('enableAutoAccept').getValue() == 'Y'){
			lData['enableAutoAccept'] = supplierListForm.getField('enableAutoAccept').getValue();
			lData['autoAccept'] = supplierListForm.getField('autoAccept').getValue();
		}
		if(supplierListForm.getField('enableAutoAcceptBidType').getValue() == 'Y'){
			lData['enableAutoAcceptBidType'] = supplierListForm.getField('enableAutoAcceptBidType').getValue();
			lData['autoAcceptableBidTypes'] = supplierListForm.getField('autoAcceptableBidTypes').getValue();
		}
		if(supplierListForm.getField('enablePurAutoAppInv').getValue() == 'Y'){
			lData['enablePurAutoAppInv'] = supplierListForm.getField('enablePurAutoAppInv').getValue();
			lData['purchaserAutoApproveInvoice'] = supplierListForm.getField('purchaserAutoApproveInvoice').getValue();
		}
		if(supplierListForm.getField('enableSelAutoAppInv').getValue() == 'Y'){
			lData['enableSelAutoAppInv'] = supplierListForm.getField('enableSelAutoAppInv').getValue();
			lData['sellerAutoApproveInvoice'] = supplierListForm.getField('sellerAutoApproveInvoice').getValue();
		}
		if(supplierListForm.getField('enableChargeBearer').getValue() == 'Y'){
			lData['enableChargeBearer'] = supplierListForm.getField('enableChargeBearer').getValue();
			lData['chargeBearer'] = supplierListForm.getField('chargeBearer').getValue();
			lData['splittingPointCharge'] = supplierListForm.getField('splittingPointCharge').getValue();
			lData['preSplittingCharge'] = supplierListForm.getField('preSplittingCharge').getValue();
			lData['postSplittingCharge'] = supplierListForm.getField('postSplittingCharge').getValue();
			lData['buyerPercentCharge'] = supplierListForm.getField('buyerPercentCharge').getValue();
			lData['sellerPercentCharge'] = supplierListForm.getField('sellerPercentCharge').getValue();
		}
		if(supplierListForm.getField('enableCostBearingType').getValue() == 'Y'){
			lData['enableCostBearingType'] = supplierListForm.getField('enableCostBearingType').getValue();
			lData['costBearingType'] = supplierListForm.getField('costBearingType').getValue();
			lData['splittingPoint'] = supplierListForm.getField('splittingPoint').getValue();
			lData['preSplittingCostBearer'] = supplierListForm.getField('preSplittingCostBearer').getValue();
			lData['postSplittingCostBearer'] = supplierListForm.getField('postSplittingCostBearer').getValue();
			lData['buyerPercent'] = supplierListForm.getField('buyerPercent').getValue();
			lData['sellerPercent'] = supplierListForm.getField('sellerPercent').getValue();
		}
		if(supplierListForm.getField('enableBidAcceptingEntityType').getValue() == 'Y'){
			lData['enableBidAcceptingEntityType'] = supplierListForm.getField('enableBidAcceptingEntityType').getValue();
			lData['bidAcceptingEntityType'] = supplierListForm.getField('bidAcceptingEntityType').getValue();
		}
		if(supplierListForm.getField('enableCashDis').getValue() == 'Y'){
			lData['enableCashDis'] = supplierListForm.getField('enableCashDis').getValue();
			lData['cashDiscountPercent'] = supplierListForm.getField('cashDiscountPercent').getValue();
		}
		if(supplierListForm.getField('enableHaircut').getValue() == 'Y'){
			lData['enableHaircut'] = supplierListForm.getField('enableHaircut').getValue();
			lData['haircutPercent'] = supplierListForm.getField('haircutPercent').getValue();
		}
		if(supplierListForm.getField('enableSettleLeg3Flag').getValue() == 'Y'){
			lData['enableSettleLeg3Flag'] = supplierListForm.getField('enableSettleLeg3Flag').getValue();
			lData['settleLeg3Flag'] = supplierListForm.getField('settleLeg3Flag').getValue();
		}
		if(supplierListForm.getField('enableInstrumentCreation').getValue() == 'Y'){
			lData['enableInstrumentCreation'] = supplierListForm.getField('enableInstrumentCreation').getValue();
			lData['instrumentCreation'] = supplierListForm.getField('instrumentCreation').getValue();
		}
		if(supplierListForm.getField('enableRemarks').getValue() == 'Y'){
			lData['enableRemarks'] = supplierListForm.getField('enableRemarks').getValue();
			lData['remarks'] = supplierListForm.getField('remarks').getValue();
		}
		if(supplierListForm.getField('enableRelation').getValue() == 'Y'){
			lData['enableRelation'] = supplierListForm.getField('enableRelation').getValue();
			lData['relationFlag'] = supplierListForm.getField('relationFlag').getValue();
			lData['relationDoc'] = supplierListForm.getField('relationDoc').getValue();
			lData['relationEffectiveDate'] = supplierListForm.getField('relationEffectiveDate').getValue();
		}
		if(supplierListForm.getField('enableBuyerTds').getValue() == 'Y'){
			lData['enableBuyerTds'] = supplierListForm.getField('enableBuyerTds').getValue();
			lData['buyerTds'] = supplierListForm.getField('buyerTds').getValue();
			lData['buyerTdsPer'] = supplierListForm.getField('buyerTdsPer').getValue();
		}
		if(supplierListForm.getField('enableSellerTds').getValue() == 'Y'){
			lData['enableSellerTds'] = supplierListForm.getField('enableSellerTds').getValue();
			lData['sellerTds'] = supplierListForm.getField('sellerTds').getValue();
			lData['sellerTdsPer'] = supplierListForm.getField('sellerTdsPer').getValue();
		}
		$.ajax({
            url: "pursuplnk/savesupplierlist",
            type: "POST",
            data: JSON.stringify(lData),
            success: function( pObj, pStatus, pXhr) {
            	alert("Updated successfully.");
            	$('#mdlSupplierList').hide();
            	supplierListForm.setValue(null);
            },
        	error: errorHandler,
	    	complete: function(){
	    		refreshState();	  
	    	}
		});
	}
	
	function getSupplierList(){
		var lEntity = supplierListForm.getField('purchaser').getValue();
		var lSupplier=supplierListForm.getField("supplier");
		var lOptions=lSupplier.getOptions();
		lOptions.dataSetValues = [];
		lSupplier.init();
		lOptions.dataSetValues = "pursuplnk/supplierlist/"+lEntity;
		lSupplier.init();
	}
</script>   	
    </body>
</html>	