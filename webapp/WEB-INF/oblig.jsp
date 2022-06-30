<!DOCTYPE html>
<%@page import="com.xlx.treds.auction.bean.ObligationBean.Type"%>
<%@page import="com.xlx.treds.auction.bean.ObligationBean.Status"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="groovy.json.JsonBuilder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.xlx.treds.auction.bean.ObligationBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lTxnType=StringEscapeUtils.escapeHtml(request.getParameter("type"));
String lDate1=StringEscapeUtils.escapeHtml(request.getParameter("d1"));
String lDate2=StringEscapeUtils.escapeHtml(request.getParameter("d2"));
String lPfId=StringEscapeUtils.escapeHtml(request.getParameter("pfId"));
String lFilter=null;
if ((lTxnType!=null)||(lDate1!=null)||(lDate2!=null)||(lPfId!=null)) {
    Map<String,Object> lMap = new HashMap<String, Object>();
    if (lTxnType != null) lMap.put("txnType",lTxnType);
    if (lDate1 != null) lMap.put("date",lDate1);
    if (lDate2 != null) lMap.put("filterToDate",lDate2);
    if (lPfId != null) lMap.put("pfId",lPfId);
    lFilter = new JsonBuilder(lMap).toString();
}
boolean lAdmin = request.getParameter("adm")!=null;
%>
<html>
    <head>
        <title>TREDS | Settlements</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/jquery.autocomplete.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
		<style type="text/css">
		    @media screen and (min-width: 992px) {
		        .modalLarge {
		          width: 1200px; 
		        }
		    }
		    .paymentDet{
			    align: center;
			    width: 80%;
			    cell-padding: 2px;
			    margin-left: 10px;
		    }
		</style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Settlements" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contObligation">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Settlements</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<div class="col-sm-4"><section><label for="date" class="label">From Date:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="date" placeholder="From Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
							<div class="col-sm-4"><section><label for="filterToDate" class="label">To Date:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="filterToDate" placeholder="To Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<div class="col-sm-4"><section><label for="fuId" class="label">Factoring Unit Id:</label></section></div>
							<div class="col-sm-8">
								<section class="input">
								<input type="text" id="fuId" placeholder="Factoring Unit Id">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						<div class="col-sm-4"><section><label for="status" class="label">Status:</label></section></div>
							<div class="col-sm-8">
								<section class="input select">
								<input type="text" id="status" placeholder="Select Status" data-role="xautocompletefield" data-others="false"/>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group">
							<div class="col-sm-2  state-F state-T state-P"><section><label for="type" class="label">Type:</label></section></div>
							<div class="col-sm-4  state-F state-T state-P">
								<section class="select">
								<select id="type"><option value="">Select Type</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2  state-F state-T state-P"><section><label for="txnType" class="label">Txn Type:</label></section></div>
							<div class="col-sm-4  state-F state-T state-P">
								<section class="select">
								<select id="txnType"><option value="">Select Type</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group">
							<div class="col-sm-2  state-F state-T"><section><label for="salesCategory" class="label">Sales Category:</label></section></div>
							<div class="col-sm-4  state-F state-T">
								<section class="select">
								<select id="salesCategory"><option value="">Select Sales Category</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2 state-T"><section><label for="billingStatus" class="label">Billing Status:</label></section></div>
							<div class="col-sm-4 state-T">
								<section class="select">
								<select id="billingStatus"><option value="">Select Billing Status</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="filterBidAcceptFromDate" class="label">Bid Accept From Date:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="filterBidAcceptFromDate" placeholder="Bid Accept From Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
							<div class="col-sm-2"><section><label for="filterBidAcceptToDate" class="label">Bid Accept To Date:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="filterBidAcceptToDate" placeholder="Bid Accept To Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>
<%
if (lAdmin) {
%>
					<div class="col-sm-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="txnEntity" class="label">Entity:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="txnEntity"><option value="">Select Entity</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2"><section><label for="pfId" class="label">Pay File Id:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<input type="text" id="pfId" placeholder="Pay File Id">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>
<!-- 					<h4 align="center">Filter for Settle Report Only</h4> -->
<!-- 					<div class="col-sm-12">  -->
<!-- 						<div class="form-group"> -->
<!-- 							<div class="col-sm-2"><section><label for="instId" class="label">Instrument Id:</label></section></div> -->
<!-- 							<div class="col-sm-4"> -->
<!-- 								<section class="input"> -->
<!-- 								<input type="text" id="instId" placeholder="Instrument Id"> -->
<!-- 								<b class="tooltip tooltip-top-right"></b></section> -->
<!-- 							</div> -->
<!-- 							<div class="col-sm-2"><section><label for="goodsAcceptanceDate" class="label">Goods Acceptance Date:</label></section></div> -->
<!--  							<div class="col-sm-4"> -->
<!--  								<section class="input"> -->
<!-- 								<i class="icon-append fa fa-clock-o"></i> -->
<!-- 								<input type="text" id="goodsAcceptanceDate" placeholder="Goods Acceptance Date" data-role="datetimepicker"> -->
<!-- 								<b class="tooltip tooltip-top-right"></b></section> -->
<!-- 						</div> -->
<!-- 						</div> -->
<!-- 					</div> -->
<!-- 					<div class="col-sm-12">  -->
<!-- 						<div class="form-group"> -->
<!-- 							<div class="col-sm-2"><section><label for="statutoryDate" class="label">Statutory Date:</label></section></div> -->
<!-- 							<div class="col-sm-4"> -->
<!-- 								<section class="input"> -->
<!-- 								<i class="icon-append fa fa-clock-o"></i> -->
<!-- 								<input type="text" id="statutoryDate" placeholder="Statutory Date" data-role="datetimepicker"> -->
<!-- 								<b class="tooltip tooltip-top-right"></b></section> -->
<!-- 							</div> -->
<!-- 						</div> -->
<!-- 					</div> -->
<%
}
%>
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
		<div class="filter-block clearfix">
			<div class="">
				<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
			<span class="right_links">
				<a href="javascript:;"  id=btnBreakup><span class="fa-pencil-square-o"></span>Show Breakup</a>
				<a href="javascript:;"  id=btnObligationSplits><span class="fa-pencil-square-o"></span>Split details</a>
<!-- 				<a href="javascript:;" class="secure" data-seckey="obligext-save" id=btnExtend><span class="fa-clock-o"></span>  Extension</a> -->
				<a href="javascript:;" class="secure" data-seckey="obligext-save" id=btnSeekExt><span class="fa-clock-o"></span>Seek Extension</a>
				<a href="javascript:;"   class='secure'  data-seckey="oblig-reqoutsettle" id=btnOutSettlement style=''><span class="fa-pencil-square-o"></span>Outside-Settlement</a>
				<a class='secure state-P state-S' href="javascript:;"  id=btnPaymentAdvice><span class="fa fa-eye"></span>Payment Advice</a></li>
				<%
					if (lAdmin) {
					%><div class = "btn-group">
					<a href="javascript:;"  type = "button" data-toggle = "dropdown">Actions<span class = "caret"></span></a>
					<ul class = "dropdown-menu" role = "menu">
			      
					<li><a href="javascript:;"  id=btnModifyParent><span class="fa fa-edit"></span>Modify</a></li>
					<li><a href="javascript:;"  id=btnRollBack><span class="fa-undo"></span>Bring Back To Auction</a></li>
					<li><a href="javascript:;"  id=btnRecalculate><span class="fas fa-plus-circle"></span>Recalculate</a></li>
				</ul>
				</div>
				<%
					}
					%>
				<div class = "btn-group">
			      <a href="javascript:;"  type = "button" data-toggle = "dropdown">Downloads<span class = "caret"></span></a>
					<ul class = "dropdown-menu" role = "menu">
					<li><a href="javascript:;"  id=btnDownloadPdf><span class="fa-download"></span>Breakup</a></li>
					<li><a href="javascript:;"  id=btnClubbedInstruments><span class="glyphicon glyphicon-new-window"></span>Invoice Details</a></li>
					<li><a href="javascript:;"  id=btnDownloadCSV style=''><span class="fa fa-download"></span>CSV</a></li>
					<li><a href="javascript:;"  id=btnDownloadReport><span class="fa-download"></span>Settlement Report</a></li>
					<li class='secure state-P state-S'><a href="javascript:;"  id=btnDownloadPayAdvPdf><span class="fa-download"></span>Payment Advice PDF</a></li>
					<li class='secure state-F'><a href="javascript:;"  id=btnDownloadCersai   data-seckey="cersaifiles-view" style='' value=''><span class="fa fa-pencil-square-o"></span>B/S Details(Cersai)</a></li>
					<%
					if (lAdmin) {
					%>
									<li><a href="javascript:;"  id=btnNOA style=""><span class="fa-download"></span>NOA</a></li>
									<li><a href="javascript:downloadDeed();" download>  <span class="fa fa-download"> Download Deed</span></a></li>
					<%
					}
					%>
				</ul>
				</div>
				<span id="spnColumnChooser" class="glyphicon glyphicon-plus"></span>

				</span>
			</div>
		</div>
		</div>
		<!-- frmSearch -->
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " data-col-chooser="spnColumnChooser" data-selector="multiple" id="tblData">
							<thead><tr>
								<th data-width="110px" data-class-name="select-checkbox"  data-name="id">Obligation Id</th>
								<th data-width="110px" data-name="fuId">Factoring Unit Id</th>
<%
if (lAdmin) {
%>
								<th data-width="150px" data-name="txnEntityName">Entity</th>
<%
}
%>
								<th data-width="80px" data-name="txnType">Txn Type</th>
								<th data-width="80px" data-name="date">Date</th>
								<th data-width="80px" data-name="amount">Net Amount</th>
								<th data-width="40px" data-name="type">Type</th>
								<th data-width="50px" data-name="status">Status</th>
<%
if (lAdmin) {
%>
								<th data-width="50px" data-name="billingStatus">Billing Status</th>
								<th data-width="50px" data-name="pfId">Pay File</th>
								<th data-width="50px" data-name="fileSeqNo">Record No</th>
								<th data-width="80px" data-name="payDetail1">Detail 1</th>
								<th data-width="80px" data-name="payDetail2">Detail 2</th>
								<th data-width="80px" data-name="payDetail3">Detail 3</th>
								<th data-width="80px" data-name="payDetail4">Detail 4</th>
<%
}
%>
								<th data-width="80px" data-name="pan">PAN</th>
								<th data-width="180px" data-name="salesCategoryDesc">Sales Category</th>
								<th data-width="180px" data-name="bidAcceptDateTime">Bid Accept Date Time</th>
								<th data-width="50px" data-name="invoiceCount">Invoice Count</th>
								<th data-width="50px" data-name="allowExtension">Extension Allowed</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
		
		<!-- frmMain -->
		<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content xform" id="frmMain">
		<div class="modal-header">Holiday</div>
		<div class="modal-body">
			<div>
				<div>
					<div class="row form-group">
					</div>
	   			</div>
	   		</div>
			</div>
	   		<div class="modal-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
	   		</div>
		</div></div></div>
   		<!-- frmMain -->
		
		<div class="modal fade" id=mdlChangeSettlor tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Change Settlor Type</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
			<div class="xform box" id="frmChangeSettlor">
				<fieldset>
					<div class="row">
						<div class="col-sm-4"><section><label for="paymentSettlor" class="label">Settlor Type</label></section></div>
						<div class="col-sm-6">
							<section class="select">
							<select id="paymentSettlor"><option value=""></option>
							</select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>		
					<div class="row">
						<input id="lKeyList" style="display:none"></input>
						<input id="isSplits" style="display:none"></input>
					</div>	
					
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnApplySplitsSettlor onClick="applySettlor()"><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
	    		</div>
			</div>
		</div></div></div>
	</div>
		
		<div class="modal fade" id="mdlUtrNumber" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
		<div class="modal-header"><span>&nbsp;UTR NUMBER</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body">
		<div class="xform box" id="contupdateUTRNumber">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="fuId" class="label">FuId:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="fuId" placeholder="FUID">
						<b class="tooltip tooltip-top-right"></b></section>
	</div>
					<div class="col-sm-4"><section><label for="utrNumber" class="label">UTR Number:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="utrNumber" placeholder="UTR NUMBER">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-4"><section><label for="remark" class="label">Remarks:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="remark" placeholder="Remarks">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
				<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpdateUtrNumber><span class="fa fa-save"></span> Save</button>
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
						<a></a>
					</div>
					</div>
				</div>
				</div>
		</div>
		</div>
		</div></div></div>
	
	<div class="modal fade" tabindex=-1 id="mdlBreakup"><div class="modal-dialog"><div class="modal-content">
	</div></div></div>
	
	<div class="modal fade" tabindex=-1 id="mdlObliSplit"><div class="modal-dialog modal-lg modalLarge"><div class="modal-content">
	</div></div></div>
	
	<div class="modal fade" tabindex=-1 id="mdlClubbedInvoice"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
	</div></div></div> 
	<div class="modal fade" tabindex=-1 id="mdlObligationSettlement"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
	</div></div></div> 
	
<div class="modal fade" id='ConfirmBox' tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Alert</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>You are about to mark the status as Success/Fail.<span id='fuId'></span></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" onclick="updateOutSideSettlement('<%=ObligationBean.Status.Success.getCode()%>')">Sucess</button>
        <button type="button" class="btn btn-danger" onclick="updateOutSideSettlement('<%=ObligationBean.Status.Failed.getCode()%>')">Fail</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
	<div class="modal fade" id="mdlRecalculate" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Recalculate</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="contRecalculate">
			<fieldset>
				<div class="row" style="display: none">
					<div class="col-sm-4"><section><label for="fuId" class="label">Factoring Unit Id:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="fuId" placeholder="Factoring Unit Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="date" class="label">Select Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="date" placeholder="Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnRecalculateValues><span class="fa fa-save"></span>Submit</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>
	
	<div class="modal fade" tabindex=-1 id="mdlSeekExt"><div class="modal-dialog modal-xl"><div class="modal-content">
	<div class="modal-header"><span>Seek Extension</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body modal-no-padding">
	<div class="xform" id="contSeekExt">
		<fieldset>
			<div class="row">
				<div class="col-sm-2"><section><label for="newDate" class="label">Date:</label></section></div>
				<div class="col-sm-4">
					<section class="input">
					<i class="icon-append fa fa-clock-o"></i>
					<input type="text" id="newDate" placeholder="Date" data-role="datetimepicker"  onBlur="javascript:computeNewAmount()">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
				<div class="col-sm-2"><section><label for="upfrontCharge" class="label">Upfront Charge:</label></section></div>
				<div class="col-sm-4">
					<section>
					<label class="checkbox"><input type=checkbox id="upfrontCharge" onclick="javascript:computeNewAmount()"><i></i>
					<b class="tooltip tooltip-top-left"></b></label>
					</section>
					<section class="view"></section>
				</div>
			</div>
			<div id='tplExtData'>
			</div>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnGenSubExt> Generate & Submit</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnGenExt> Generate</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
	</div>
	</div></div></div></div>
	
   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>
   	<script src="../js/datatables.js"></script>
   	<script id="tplBreakup" type="text/x-handlebars-template">
	<div class="modal-header">Breakup <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
	<fieldset>
	<div class="row"><div class="col-sm-6"><b>Factoring Unit:</b></div><div class="col-sm-6">{{fuId}}</div></div>
	<div class="row"><div class="col-sm-6"><b>Settlement Date:</b></div><div class="col-sm-6">{{date}}</div></div>
	<hr>
	<div class="row"><div class="col-sm-6"><b>Factored Amount:</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amt}}{{/formatDec}}</div></div>
	<div class="row"><div class="col-sm-6"><b>Interest Charge:</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{cost}}{{/formatDec}}</div></div>
{{#each chgSumm}}
	<div class="row"><div class="col-sm-6"><b>Transaction Charges[({{ent}})]:</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{chg}}{{/formatDec}}</div></div>
	{{#if cgstValue}}
	<div class="row"><div class="col-sm-6"><b>CGST {{cgst}}% + {{cgstSurcharge}}% Surcharge</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{cgstValue}}{{/formatDec}}</div></div>
	{{/if}}
	{{#if sgstValue}}
	<div class="row"><div class="col-sm-6"><b>SGST {{sgst}}% + {{sgstSurcharge}}% Surcharge</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{sgstValue}}{{/formatDec}}</div></div>
	{{/if}}
	{{#if igstValue}}
	<div class="row"><div class="col-sm-6"><b>IGST {{igst}}% + {{igstSurcharge}}% Surcharge</b></div><div class="col-sm-6"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{igstValue}}{{/formatDec}}</div></div>
	{{/if}}
{{/each}}
	<hr>
	<div class="row">
		<div class="col-sm-6">
			<table class="table"><thead><tr><th colspan="2">Debits <span class="fa fa-cust{{currency}}"></span> </th></tr></thead><tbody>
{{#each d}}
			<tr>
				<td width="50%">{{ename}} ({{e}})</td>
				<td width="50%" class="text-right">{{#formatDec}}{{a}}{{/formatDec}} <i>{{n}}</i></td>
			</tr>
{{/each}}
			</tbody></table>
		</div>
		<div class="col-sm-6">
			<table class="table"><thead><tr><th colspan="2">Credits <span class="fa fa-cust{{currency}}"></span> </th></tr></thead><tbody>
{{#each c}}
			<tr>
				<td width="50%">{{ename}} ({{e}})</td>
				<td width="50%" class="text-right">{{#formatDec}}{{a}}{{/formatDec}} <i>{{n}}</i></td>
			</tr>
{{/each}}
			</tbody></table>
		</div>
	</div>
	<div class="modal-footer">
		<div class="row">
			<div class="col-sm-12">
				<div class="btn-groupX pull-right">
					{{#if noa}}
					<button type="button" class="btn btn-info btn-lg" onClick="javascript:viewNOA({{fuId}})" >View NOA</button>
					{{/if}}
					<button type="button" class="btn btn-enter btn-info btn-lg" data-dismiss="modal">OK</button>
				</div>
			</div>
		</div>
	</div>
	</fieldset>
	</div>
	</script>
	

	<script id="tplObligationSplits" type="text/x-handlebars-template">
		<div class="modal-header">Obligation Splits <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="splitDetails"><thead>
				<tr>
					<th> <input type=checkbox class="chkSelectAll" onclick="selectAll()"> Obid </th>
					<th> Part </th>
					<th> Amt <span class="fa fa-cust{{currency}}"></span> </th>
					<th> Status </th>
					<th> Settled Amt <span class="fa fa-cust{{currency}}"></span> </th>
					<th> Payment Ref No </th>
					<th> Response Code </th>
					<th> Settlor </th>
					<th> Remarks </th>
				</tr></thead>
				<tbody>
		{{#each splitlist}}
				<tr>
					<td><input type="checkbox" class="chkbox" data-id="{{partNumber}}"/>{{obid}}</td>
					<td>{{partNumber}}</td>
					<td>{{amount}}</td>
					<td>{{status}}</td>
					<td>{{settledAmount}}</td>
					<td>{{paymentRefNo}}</td>
					<td>{{responseCode}}</td>
					<td>{{settlor}}</td>
					<td>{{remarks}}</td>
<%
if (lAdmin) {
%>
				{{#if modify}}<td><button type="button" id='btnModification' onclick="modifyObligation({{@root.fuId}},'{{@root.type}}',{{partNumber}})">Modify</button></td>{{/if}}			
<%
}
%>
				</tr>
		{{/each}}
				<tr>
<%
if (lAdmin) {
%>
					<td><button type="button" id='btnSplitsChangeSettlor' onclick="getDataSettlor({{fuId}},'{{type}}')">Change</button></td>
<%
}
%>
					{{#ifCond status '==' '<%= ObligationBean.Status.L2_Set_Outside.toString()%>'}}
						{{#ifHasAccess "oblig-appoutsettle"}}
							<td><button type="button" id='btnOutsideSettlor' onclick="markAsOutsideSettled({{fuId}})">markAsOtsideSettled</button></td>
						{{/ifHasAccess}}
					{{/ifCond}}
				</tr>
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>

	</script>
	
	<script id="tplClubbedInstruments" type="text/x-handlebars-template">
		<div class="modal-header"> Instrument Details <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="instClub"><thead>
				<tr>
					<th width=135px> Instrument Id </th>
					<th width=135px> Factoring Unit Id </th>
					<th > Invoice No. </th>
					<th >Invoice Amount </th>
					<th >Cash Discount Amount</th>
					<th> Adj Amount</th>
					<th> TDS Amount</th>
					<th>Net Amount</th>
					{{#if splitsummary.intL1Amt}}<th>Interest L1 Amount</th>{{/if}}
					{{#if splitsummary.intL2Amt}}<th>Interest L2 Amount</th>{{/if}}
					{{#if splitsummary.intL2ExtAmt}}<th>Interest L2 Ext Amount</th>{{/if}}
				</tr></thead>
				<tbody>
		{{#each splitlist}}
				<tr>
					<td>{{id}}</td>
					<td>{{../fuId}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					{{#if ../splitsummary.intL1Amt}}<th>{{intL1Amt}}</th>{{/if}}
					{{#if ../splitsummary.intL2Amt}}<th>{{intL2Amt}}</th>{{/if}}
					{{#if ../splitsummary.intL2ExtAmt}}<th>{{intL2ExtAmt}}</th>{{/if}}
				</tr>
		{{/each}}
		{{#if splitsummary}}
				<tr style="font-weight:bold">
					<td></td>
					<td></td>
					<td></td>
					<td>{{splitsummary.amt}}</td>
					<td>{{splitsummary.cashdiscountAmt}}</td>
					<td>{{splitsummary.adjAmt}}</td>
					<td>{{splitsummary.tdsAmt}}</td>
					<td>{{splitsummary.netAmt}}</td>
					{{#if splitsummary.intL1Amt}}<td>{{splitsummary.intL1Amt}}</td>{{/if}}
					{{#if splitsummary.intL2Amt}}<td>{{splitsummary.intL2Amt}}</td>{{/if}}
					{{#if splitsummary.intL2ExtAmt}}<td>{{splitsummary.intL2ExtAmt}}</td>{{/if}}
				</tr>
		{{/if}}
				</tbody>
				</table>
				<td><button type="button" id='btnDownloadjson' onclick="downloadClubbeddetails({{fuId}})">Download</button></td>
			</div>
		</div>
		</fieldset>
		</div>

	</script>
	
		<script id="tplExt" type="text/x-handlebars-template">
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="sucExt"><thead>
				<tr>
					<th width=135px> Obligation </th>
					<th width=135px> Purchaser </th>
					<th width=135px> Financier</th>
					<th width=135px>Obligation Date</th>
					<th width=135px> Old Amount </th>
					<th width=135px>Extended Due date</th>
					<th style="display:none;" width=135px> New Amount</th>
					<th style="display:none;" width=135px> Rate %</th>
					<th style="display:none;" width=135px>Interest</th>
					<th style="display:none;" width=135px> Penalty Rate %</th>
					<th style="display:none;" width=135px> Penalty</th>
					<th style="display:none;" width=135px> Default Rates</th>
				</tr></thead>
				<tbody>
		{{#each success}}
				<tr>
					<td>{{obId}}</td>
					<td>{{purchaser}}</td>
					<td>{{financier}}</td>
					<td>{{oldDate}}</td>
					<td>{{oldAmount}}</td>
					<td>{{newDate}}</td>
					<td style="display:none;">{{newAmount}}</td>
					<td style="display:none;">{{interestRate}}</td>
					<td style="display:none;">{{interest}}</td>
					<td style="display:none;">{{penaltyRate}}</td>
					<td style="display:none;">{{penalty}}</td>
					<td style="display:none;">
					{{#each penaltySetting.penaltyList}}
					<p>Days - {{uptoDays}} : Rate - {{rate}}</p>
					{{/each}}
					</td>
				</tr>
		{{/each}}
				</tbody>
				</table>
			</div>
		</div>

<div class="row">
			<div class="col-sm-12">
				<table class="table" id="suc"><thead>
				<tr>
					<th width=135px> Id </th>
					<th width=135px> Message</th>
				</tr></thead>
				<tbody>
		{{#each error}}
				<tr>
					<td>{{id}}</td>
					<td>{{message}}</td>
				</tr>
		{{/each}}
				</tbody>
				</table>
			</div>
		</div>

	</script>
	
	<script id="tplPaymentDetails" type="text/x-handlebars-template">
	
	<div class="modal-header"> Payment Details <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
	<div>
		<table  class="table table-hover" align="center">
		<tr>
		<th>Payment detials1</th>
		<th>Payment detials2</th>
		<th>Entity Name</th>
		<th>Entity Code</th>
		</tr>
		<tr>
		<td>{{paymentDetails1}}</td>
		<td>{{paymentDetails2}}</td>
		<td>{{paymentDetails3}}</td>
		<td>{{paymentDetails4}}</td>
		</tr>
		</table>
			<td><button type="button" class="btn btn-primary btn-sm" id='markAsL2ProvOutside' onclick="markAsL2ProvOutside({{fuId}})"> Mark As L2 Prove Outside</button></td>
	</div>
	</div>
	</script>
		<script id="tplMessage" type="text/x-handlebars-template">
		<table class="table">
			<thead><tr><th>Action</th><th>Remarks</th></tr></thead>
			<tbody>
		{{#each this}}
			<tr><td>{{act}}</td><td>{{rem}}</td></tr>
		{{/each}}
			</tbody>
		</table>
	</script>
	<script type="text/javascript">
	var crudObligation$ = null,crudObligation,tplBreakup, searchForm,tplObligationSplits;
	var tplClubbedInstruments;
	var tplExt;
	var tplPaymentDetails;
	var lInstrumentData;
	var crudutr;
	var crudutr$;
	var lSplitsData;
	var tplMessage;
	var lExtData = null;
	$(document).ready(function() {
		tplBreakup=Handlebars.compile($('#tplBreakup').html());
		tplObligationSplits=Handlebars.compile($('#tplObligationSplits').html());
		tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html());
		tplExt=Handlebars.compile($('#tplExt').html());
		tplPaymentDetails=Handlebars.compile($('#tplPaymentDetails').html());
		tplMessage = Handlebars.compile($('#tplMessage').html());
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class).getJsonConfig()%>;
<%
if (lAdmin) {
%>
		$.each(lFormConfig.fields, function(pIndex,pValue){
			if (pValue.name=="txnEntity") {
				pValue.dataSetType="RESOURCE";
				pValue.dataSetValues="appentity/all";
			}
		});
<%
}
%>
		var lConfig = {
				resource: "oblig",
				postSearchHandler: function(pData) {
					return true;
				}
		};
		var lReportConfig =[
					{
						"name":"instId",
						"label":"Instrument Id",
						"dataType":"Integer",
					},
					{
						"name":"goodsAcceptanceDate",
						"label":"Goods Acceptance  Date",
						"dataType":"DATE",
					},
					{
						"name":"statutoryDate",
						"label":"Statutory Date",
						"dataType":"DATE",
					}
			];
		lFormConfig.fields.push(...lReportConfig);
		lConfig = $.extend(lConfig,lFormConfig);
		crudObligation$ = $('#contObligation').xcrudwrapper(lConfig);
		crudObligation=crudObligation$.data('xcrudwrapper');
		searchForm = crudObligation.options.searchForm;
		var lUpdateUtrConfig = {
				"fields": [						
						{
							"name":"utrNumber",
							"label":"Utr Number",
							"dataType":"STRING",
							"maxLength": 30,
							"notNull": true
						},
						{
							"name":"remark",
							"label":"remark",
							"maxLength":30,
							"dataType":"STRING",
						},
						{
							"name":"fuId",
							"label":"Utr FuId",
							"dataType":"INTEGER",
							"maxLength": 15
						}
				]
			};
     	crudutr$ = $('#contupdateUTRNumber').xform(lUpdateUtrConfig);
     	crudutr = crudutr$.data('xform');
     	var lExtFormConfig = {
			"fields": [
					{
						"name":"newDate",
						"label":"Date",
						"dataType":"DATE",
						"notNull": true
					}
			]
		};
     	extForm$ = $('#contSeekExt').xform(lExtFormConfig);
		extForm = extForm$.data('xform');
<%
if (lFilter != null) {
%>		var lFilt=<%=lFilter%>;
		crudObligation.options.searchForm.setValue(lFilt);
		crudObligation.searchHandler();
<%
}
%>
$('#btnBreakup').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
	var lUrl=crudObligation.options.resource + '/breakup/' + lData.fuId + '/' + dateFormatter.formatDate(lData.date) + '/' ;
	$.ajax({
        url: lUrl,
        type: 'GET',
        success: function( pObj, pStatus, pXhr) {
        	$('#mdlBreakup .modal-content').html(tplBreakup(pObj));
        	showModal($('#mdlBreakup'));
        },
    	error: errorHandler
    });
	}
});

$('#btnObligationSplits').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
	var lUrl=crudObligation.options.resource + '/oblisplit/' + lData.id  ;
	var lFuId=lData.fuId;
	var ltype=lData.type;
	var lStatus=lData.status;
	$.ajax({
        url: lUrl,
        type: 'GET',
        success: function( pObj, pStatus, pXhr) { 
        	pObj["fuId"] = lFuId;
        	pObj["type"] = ltype;
        	pObj["status"] = lStatus;
        	if(pObj.type != null && pObj.fuId != null){
        		$('#mdlObliSplit .modal-content').html(tplObligationSplits(pObj));
            	showModal($('#mdlObliSplit'));
            	refreshState();
        	}
        },
    	error: errorHandler
    });
	}
});

$('#btnExtend').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
		});
	$.ajax({
        url: 'obligext/check/' + lData.id,
        type: 'GET',
        async:false,
//         data:JSON.stringify(lData),
        success: function( pObj, pStatus, pXhr) { 
        	location.href='obligext?obId=' + lData.id;
        },
        error: function( pObj, pStatus, pXhr) {
        	alert(pObj.responseJSON.messages);
        }
	});
	}
});
$('#btnDownloadPdf').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
	window.open('oblig/breakuppdf/'+lData.fuId+'/'+dateFormatter.formatDate(lData.date)+'?loginKey='+loginData.loginKey);
	}
	});

$('#btnDownloadCSV').on('click', function() {
	var lFilter=searchForm.getValue(true);
	lFilter['columnNames'] = crudObligation.getVisibleColumns();
	downloadFile('oblig/all',null,JSON.stringify(lFilter));
});
$('#btnDownloadReport').on('click', function() {
	downloadFile('oblig/obliReport',null,JSON.stringify(searchForm.getValue(true)));
});
$('#btnNOA').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
		if(lData.type == '<%=ObligationBean.Type.Leg_1.toString()%>' && 
				lData.status == '<%=ObligationBean.Status.Success.toString()%>'){
			window.open('oblig/noapdf/fu/'+lData.fuId+'?loginKey='+loginData.loginKey);
		}else{
			alert("Please select successful Leg 1 obligation.");
		}
	}
});
$('#btnClubbedInstruments').on('click', function() {
 	viewClubbedInstruments(crudObligation,tplClubbedInstruments);	
});
$('#btnDownloadCersai').on('click', function() {
	downloadFile("oblig/downloadCersaiReport",null,null); //get
});
$('#btnRollBack').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	var lFuid= null;
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
		var  lRowData = null;
		$.each(lRows, function(pIndex,pValue) {
			lRowData = pValue.data();
		});
		if (lRowData.type !='<%=ObligationBean.Type.Leg_1.toString()%>'){
			alert ("Please select a Leg 1  transaction.");
			return;
		}
		if (lRowData.status!='<%=ObligationBean.Status.Failed.toString()%>'){
			alert ("Please select a Leg 1 failed transaction.");
			return;
		}
		lFuid = lRowData.fuId;
		var lUrl= 'oblig/bringBackToAuction' ;
	   	var lData = {"fuId":lFuid};
		$.ajax({
	       url: lUrl,
	       type: 'POST',
	       data:JSON.stringify(lData),
	       success: function( pObj, pStatus, pXhr) { 
			       	 alert('Success');
	       },
	   	   error: errorHandler
	   	});
	}
});
$('#btnModifyParent').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
		var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
		});
		if (lData.status == '<%=Status.Created%>' || lData.status == '<%=Status.Ready%>'){
			//location.href='modifyOblig?fuId='+lData.fuId+'&type='+lData.type;
			location.href='oblimodreq?fuId='+lData.fuId+'&type='+lData.type+'&partNo=0';
		}else{
			alert("Invalid status.");
		}
	}
});
$('#btnRecalculate').on('click',function(){
	var lFuIds = [];
	var lData = null;
	var lFlag = true;
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length<1){
		alert("Please select a row.");
	}else{
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
			if (lFuIds==null){
				lFuIds.push(pValue.data().fuId);
			}else{
				lFuIds.push(pValue.data().fuId);
			}
			if(lData.type == '<%=Type.Leg_2%>' || lData.type == '<%=Type.Leg_3%>'){
				lFlag = false;
				alert(" Only L1 obligations are allowed. ");
				return false;
			}else {
				if (!(lData.status == '<%=Status.Created%>' || lData.status == '<%=Status.Ready%>' || lData.status == '<%=Status.Sent%>')){
					lFlag = false;
					alert(" Cannot process obligations ");
					return false;
				}
			}
		});
	}
	if(lFlag){
		var lFactIds = lFuIds.filter(function(elem, index, self) {
			return index === self.indexOf(elem);
		})
	   	showModal($('#mdlRecalculate'));
		crudrecalculate.getField('fuId').setValue(lFactIds);
	}
});
var lRecalculate = {
		"fields": [						
				{
					"name":"fuId",
					"label":"Factoringunit Id",
					"dataType":"STRING",
					"maxLength": 30,
					"notNull": true
				},
				{
					"name":"date",
					"label":"Date",
					"dataType":"DATE"
				}
		]
	};
	crudrecalculate$ = $('#contRecalculate').xform(lRecalculate);
	crudrecalculate = crudrecalculate$.data('xform');
$('#btnRecalculateValues').on('click',function(){
	var lData={};
	lData['fuId'] = crudrecalculate.getField('fuId').getValue();
	lData['date'] = crudrecalculate.getField('date').getValue();
	var lUrl= 'oblig/recalculate' ;
	$.ajax({
	       url: lUrl,
	       type: 'POST',
	       data:JSON.stringify(lData),
	       success: function( pObj, pStatus, pXhr) { 
	    	   alert(tplMessage(pObj));
	    	   $('#mdlRecalculate').hide();
	    	   crudObligation.searchHandler();
	       },
	   	error: errorHandler
	   	});
	
});


$('#btnOutSettlement').on('click',function(){
	outSettlement();	
});

$('#btnSeekExt').on('click', function() {
	extForm.getField('newDate').setValue(loginData.curDate);
	$('#tplExtData').html(tplExt(null));
	lExtData = null;
	$("#upFrontCharge").prop( "checked", false );
	showModal($('#mdlSeekExt'));
});
$('#btnGenExt').on('click', function() {
	var lData = lExtData;
	lData['submit'] = false;
	generateExtension(lData);
});
$('#btnGenSubExt').on('click', function() {
	var lData = lExtData;
	lData['submit'] = true;
	generateExtension(lData);
});
$('#btnPaymentAdvice').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length<1){
		alert("Please select a row.")
	}else{
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
	showRemote('oblig/paymentadvisehtml/'+lData.fuId+'?loginKey='+loginData.loginKey, "modal-lg", false, "Payment Advise");
	}
});

$('#btnDownloadPayAdvPdf').on('click', function() {
	var lRows=crudObligation.getSelectedRows();
	if(lRows.length!=1){
		if(lRows.length<1) alert("Please select a row.");
		if(lRows.length>1) alert("Please select only one row.");	
	}else{
	var lData = null;
		$.each(lRows, function(pIndex,pValue) {
			lData = pValue.data();
	});
	window.open('oblig/paymentadvisepdf/'+lData.fuId +'?loginKey='+loginData.loginKey);
	}
	});
});

	function updateSettlor(pDataKey, pIsSplits, pSettlor){
	   	var lData = {"keyList":pDataKey,"isSplits":pIsSplits,"paymentSettlor":pSettlor};
		var lUrl= 'oblig/changeSettlor' ;
		$.ajax({
	       url: lUrl,
	       type: 'POST',
	       data:JSON.stringify(lData),
	       success: function( pObj, pStatus, pXhr) { 
	    	   $('#mdlChangeSettlor').modal('hide');
	    	   alert("Saved successfully");
	       },
	   	error: errorHandler
	   	});
	}

	function viewNOA(fuId) {
		showRemote('oblig/noahtml/fu/'+fuId+'?loginKey='+loginData.loginKey, "modal-xl", false, "Notice of Assignment");
	}
	function viewClubbedInstruments(crudObligation,pTplClubbedInstruments){
		var lRows=crudObligation.getSelectedRows();
		if(lRows.length!=1){
			if(lRows.length<1) alert("Please select a row.");
			if(lRows.length>1) alert("Please select only one row.");	
		}else{
			var lData = null;
				$.each(lRows, function(pIndex,pValue) {
					lData = pValue.data();
			});
			var lUrl= 'oblig/viewclubbeddetails/' + lData.fuId  ;
			$.ajax({
	        url: lUrl,
	        type: 'GET',
	        success: function( pObj, pStatus, pXhr) { 
	           	$('#mdlClubbedInvoice .modal-content').html(pTplClubbedInstruments(pObj));
	           	showModal($('#mdlClubbedInvoice'));
	        },
	    	error: errorHandler
	    	});
		}
	}
	
	function modifyObligation(fuId,type,partNo){
		var lData = {"fuId":fuId,"type":type,"partNo":partNo};
		$.ajax({
	        url: 'oblig/checkModi',
	        type: 'POST',
		    data:JSON.stringify(lData),
	        success: function( pObj, pStatus, pXhr) { 
	        	if (pObj){
	        		location.href='oblimodreq?fuId='+fuId+'&type='+type+'&partNo='+partNo;
	        	}else{
	        		alert("Access Denied.");
	        	}
	        },
	    	error: errorHandler
	    	});
		
	}	
	function downloadClubbeddetails(parentId){
		downloadFile('oblig/downloadClubbeddetails/'+parentId,null, null); //get
	}
	function getType(lData) {
		var lType = null;
		if (lData == '<%=ObligationBean.Type.Leg_1%>'){
			lType = '<%=ObligationBean.Type.Leg_1.getCode()%>';
		}else if (lData == '<%=ObligationBean.Type.Leg_2%>'){
			lType = '<%=ObligationBean.Type.Leg_2.getCode()%>';
		}else if (lData == '<%=ObligationBean.Type.Leg_3%>'){
			lType = '<%=ObligationBean.Type.Leg_3.getCode()%>';
		}
		return lType;
	}
	
	function getDataSettlor(pFuId,pType){
		var lKey = null
   		var lDataKey = [] ;
   	 	$(".chkbox:checked").each(function() {  
   	 		lKey = pFuId+'^'+getType(pType)+'^'+$(this).attr('data-id');
	   	 	if(!lDataKey.includes(lKey)){
	   	 		lDataKey.push(lKey);
			}
   		});
   	 	$('#btnApplySplitsSettlor').show();
   		var lData = {keyList:lDataKey,isSplits:true};
   		$('#mdlObliSplit').modal('hide');
   		populateSettlor();
   	    $('#mdlChangeSettlor').modal('show');
   	 	lSplitsData = lData;
 	}
	
	function applySettlor(){
		var settlor =$('#mdlChangeSettlor #paymentSettlor').val();
		updateSettlor(lSplitsData.keyList, lSplitsData.isSplits, settlor);
		lSplitsData = null;
	}
	function populateSettlor(){
		var option = null;
	    if( $('#mdlChangeSettlor #paymentSettlor option').length == 1){
	       	option = document.createElement('option');
	        option.text = '<%=AppConstants.FACILITATOR_NPCI%>';
	        option.value = '<%=AppConstants.FACILITATOR_NPCI%>';
	        $('#mdlChangeSettlor #paymentSettlor').append(option);
	       	option = document.createElement('option');
	        option.text = '<%=AppConstants.FACILITATOR_DIRECT%>';
	        option.value = '<%=AppConstants.FACILITATOR_DIRECT%>';
	        $('#mdlChangeSettlor #paymentSettlor').append(option);
	    }
 	}
	function selectAll(){
		if ($('.chkSelectAll').is(':checked')) {
			$(".chkbox").prop( "checked", true );
		}else{
			$(".chkbox").prop( "checked", false );
		}
	}
	
	function outSettlement(){
		var lRows = crudObligation.getSelectedRow();
		lData = {};
		if(lRows.length < 1 || lRows.length == 0){
			alert("Please select a row");
		}
		lData['fuId'] = lRows.data().fuId;
		lData['type'] = lRows.data().type;
		lData['status'] = lRows.data().status;
		if(lData['type'] != '<%= ObligationBean.Type.Leg_2 %>'){
			alert("Please select obligation of type Leg 2 only");
		}else if(lData['status'] == '<%= ObligationBean.Status.Failed %>'){
			$.ajax({
		        url: 'oblig/outsettlement',
		        type: 'POST',
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) { 
		        	$('#mdlObligationSettlement .modal-content').html(tplPaymentDetails(pObj));
		           	showModal($('#mdlObligationSettlement'));
		        },
		    	error: errorHandler
		    	});
		}else if(lData['status'] == '<%= ObligationBean.Status.L2_Prov_Outside %>'){
			updateUtrNumber(lData)
		}
	}
	
	function markAsL2ProvOutside(pData){
		$.ajax({
	        url: 'oblig/markOutForSettle/'+pData,
	        type: 'GET',
	        success: function( pObj, pStatus, pXhr) { 
	        	alert("Saved successfully");
	        },
	    	error: errorHandler,
	    	complete: function(){
	    		$('#mdlObligationSettlement').modal('hide');
	    		crudObligation.searchHandler();
	    	}
	    	});
	}
	function updateUtrNumber(pData){
		crudutr.getField('fuId').setValue(pData['fuId']);
		$('#mdlUtrNumber').modal('show');
		crudutr.enableDisableField('fuId',false,false);
		$('#btnUpdateUtrNumber').on('click',function(){
			var lModalData = {};
			lModalData['fuID'] = crudutr.getField('fuId').getValue();
			lModalData['UtrNumber'] = crudutr.getField('utrNumber').getValue();
			lModalData['Remarks'] = crudutr.getField('remark').getValue();
			$.ajax({
		        url: 'oblig/updateUTR',
		        type: 'POST',
		        data:JSON.stringify(lModalData),
		        success: function( pObj, pStatus, pXhr) {
		        	alert("Saved successfully");
		        },
		    	error: errorHandler,
		    	complete: function(){
		    		$('#mdlUtrNumber').modal('hide');
		    		crudObligation.searchHandler();
		    	}
		    	});
		});
	}
	function markAsOutsideSettled(pFuId){
		showModal($('#ConfirmBox'));
		$("#ConfirmBox #fuId").attr("data-id",pFuId);
		$('#mdlObliSplit').modal('hide');
	}
	function updateOutSideSettlement(pStatus){
		var lFuId =$("#ConfirmBox #fuId").data("id");
		if (lFuId!=null && pStatus!=null){
			$.ajax({
				url: 'oblig/markStatus/'+lFuId+'/'+pStatus,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) { 
		        	alert("Saved successfully");
		        },
		    	error: errorHandler,
		    	complete: function(){
		    		crudObligation.searchHandler();
		    		$("#ConfirmBox").modal('hide');
		    	}
			});
		}
	}
	
	function computeNewAmount() {
		var lNewDate = extForm.getField('newDate').getValue();
		var lRows=crudObligation.getSelectedRows();
		if (lRows.length>0){
			var lIds= [];
			$.each(lRows, function(pIndex,pValue) {
    				lIds.push(pValue.data().id);
       		});
			var lData = {newDate:lNewDate,obIds:lIds,upfrontCharge:$('#upfrontCharge').is(':checked')};
			$.ajax({
	            url:'obligext/check',
	            type: 'POST',
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
	            	$('#tplExtData').html(tplExt(pObj));
	            	lExtData = {list:pObj.success};
	            	$('#sucExt').DataTable({
	    				autoWidth: true,
	    				scrollX: "100%",
	            	});
	            },
	        	error: errorHandler
	        });
		}
	}
	
	function generateExtension(pData){
		if (pData !=null) {
			$.ajax({
		        url:'obligext/save',
		        type: 'POST',
		        data:JSON.stringify(pData),
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlSeekExt').hide();
		        	alert('Success');
		        },
		    	error: errorHandler
		    });
		}else{
			alert ('No Data try again');
		}
	}
	
	function downloadDeed(){
		var lRows = crudObligation.getSelectedRows();
		if(lRows.length!=1){
			if(lRows.length<1) alert("Please select a row.");
			if(lRows.length>1) alert("Please select only one row.");	
		}else{
			var lData = null;
			$.each(lRows, function(pIndex,pValue) {
				lData = pValue.data();
			});
			downloadFile('factunitsp/downloaddeedofassignmenthtml/'+lData.fuId);
		}
	}
	</script>
   	
    </body>
</html>