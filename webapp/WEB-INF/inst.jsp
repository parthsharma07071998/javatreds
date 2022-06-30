<!DOCTYPE html>
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="com.xlx.commonn.AuthenticationHandler"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.common.base.CommonConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Instruments</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/jquery.autocomplete.css" rel="stylesheet">
<!--         <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
      <style type="text/css">
         #dispAgreement1 {
         overflow: scroll;
         height: 260px;
         }
         .frmfld th,td{
			padding : 4px;
			}
      </style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Instruments" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	<div class="content" id="contInstrument">
<!-- frmSearch -->
		<div id="frmSearch">
				<div class="page-title">
					<div class="title-env">
						<h1 class="title">Instruments</h1>
					</div>
				</div>
				<div>
				<div class="tab-pane panel panel-default no-margin collapse in" id=divFilterGrpAmt>
			<fieldset class="form-horizontal">
				<div class="col-sm-12">
					<section class="input">
						<b>Invoice Less than (Net Amount) :</b> <input type="number"  name="Amt" id='Amt'><br></span>
					</section>
				</div>
				<div class="panel-body bg_white">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch1 onclick="showFilteredData(true);"><span class="fa fa-search"></span> Search</button>
		 						<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr1 onclick="showFilteredData(false);">Clear Filter</button>
							</div>
						</div>
					</div>
			</div>
			</fieldset>
		</div>
                 <div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
					 <li><a href="#tab0" data-toggle="tab">Inbox <span id="badge0" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab1" data-toggle="tab">Checker Pending <span id="badge1" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab2" data-toggle="tab"><span id="spnTab2">Counter</span> Pending <span id="badge2" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab3" data-toggle="tab" style='display:none'>Ready for Auction<span id="badge3" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab4" data-toggle="tab">Rejected <span id="badge4" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab5" data-toggle="tab">In Auction <span id="badge5" class="badge badge-primary"></span></a></li>
					 <li style="display:none"><a href="#tab6" data-toggle="tab">Auctioned <span id="badge6" class="badge badge-primary"></span></a></li>
					 <li style="display:none"><a href="#tab7" data-toggle="tab">Settle Failed <span id="badge7" class="badge badge-primary"></span></a></li>
					 <li style="display:none"><a href="#tab8" data-toggle="tab">Expired <span id="badge8" class="badge badge-primary"></span></a></li>
					 <li style="display:none"><a href="#tab9" data-toggle="tab">Group <span id="badge9" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab10" data-toggle="tab">History <span id="badge10" class="badge badge-primary"></span></a></li>
			 		</ul>
			 	</div>
				<div class="filter-block clearfix">
					<div class="">
							<a class="btn-group0 left_links " href="javascript:displayFilter();" ><span class="glyphicon glyphicon-plus"></span>Filter</a>
						<span class="right_links">
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-save" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> Add New</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
							<a class="secure btn-group0 btn-group9 " data-seckey="inst-status" href="javascript:;" id=btnSubmit><span class="glyphicon glyphicon-circle-arrow-right"></span> Submit</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-delete" id=btnRemove><span class="glyphicon glyphicon-trash"></span> Remove</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-upload" id=btnInstructions><span class="glyphicon glyphicon-open"></span> Upload</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-save" id=btnAddToGroup><span class="glyphicon glyphicon-plus"></span> Group</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-save" id=btnRemoveFromGroup><span class="glyphicon glyphicon-plus"></span> Ungroup</a>
							<a class="secure " href="javascript:;" data-seckey="inst-view" id=btnClubbedInstruments><span class="glyphicon glyphicon-new-window"></span> View Group Details</a>
							<a class="secure btn-group10 hidden" href="javascript:;" data-seckey="inst-view" onClick="javascript:showHistoryFilter();" ><span class="fa fa-filter" id='filter' style='color:black'></span> Filter</a>
							<a 	href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span>CSV</a>
							<a class="secure" href="javascript:;" data-seckey="instview-view" id=btnViewInstru><span class="glyphicon glyphicon-eye-open"></span> View Instrument</a>
							<a class="secure btn-group0" href="javascript:;" data-seckey="inst-addverifiedinst" id=btnAddInvoice style=''><span class="fa fa-file-invoice"></span>Add Verified Invoice</a>
							<a class="btn-enter"  href="javascript:;" id=btnSearch><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
							<span id="spnColumnChooser" class="glyphicon glyphicon-plus"></span>
						</span>
					</div>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " data-col-chooser="spnColumnChooser" data-selector="multiple" id="tblData">
								<thead><tr>
									<th data-width="110px" data-name="id" data-class-name="select-checkbox" data-sel-exclude="true">Id</th>
									<th data-width="0px" data-name="tab" data-sel-exclude="true" data-visible="false"></th>
									<th data-width="120px" data-name="purName">Buyer</th>
									<th data-width="120px" data-name="supName">Seller</th>
									<th data-width="70px" data-name="poDate">Purchase Order Date</th>
									<th data-width="100px" data-name="poNumber">Purchase Order Number</th>
									<th data-width="100px" data-name="purLocation">Buyer Location</th>
									<th data-width="100px" data-name="instNumber">Invoice Number</th>
									<th data-width="100px" data-name="salesCategory">Sales Category</th>
									<th data-width="100px" data-name="supLocation">Seller Location</th>
									<th data-width="70px" data-name="instDate">Invoice Date</th>
									<th data-width="110px" data-name="goodsAcceptDate">Goods/Service Acceptance Date</th>
									<th data-width="70px" data-name="instDueDate">Invoice Due Date</th>
									<th data-width="70px" data-name="maturityDate">Maturity Date</th>
									<th data-width="80px" data-name="amount">Invoice Amount (in Actual)</th>
									<th data-width="80px" data-name="haircutPercent">Haircut %</th>
									<th data-width="80px" data-name="adjAmount">Deductions (in Actual)</th>
									<th data-width="80px" data-name="cashDiscountPercent">Cash Discount %</th>
									<th data-width="80px" data-name="cashDiscountValue">Cash Discount Value</th>
									<th data-width="80px" data-name="tdsAmount">TDS (in Actual)</th>
									<th data-width="100px" data-name="netAmount">Factoring Unit Cost (in Actual)</th>
									<th data-width="110px" data-name="status">Status</th>
<!-- 									<th data-width="110px" data-name="inMkrChkLevel">Checker level</th> -->
									<th data-width="80px" data-name="age">Aging</th>
									<th data-width="90px" data-name="fuId">Factoring Unit Id</th>
									<th data-width="70px" data-name="makerLoginId">Maker User</th>
									<th data-width="70px" data-name="checkerLoginId">Checker User</th>
									<th data-width="70px" data-name="ownerEntity">Owner</th>
									<th data-width="70px" data-name="ownerLoginId">Owner User</th>
									<th data-width="130px" data-name="statusUpdateTime">Status Update Time</th>
									<th data-width="0px" data-name="groupFlag" data-sel-exclude="true" data-visible="false"></th>
									<th data-width="0px" data-name="ebdId" data-sel-exclude="true" data-visible="false"></th>
								</tr></thead>
							</table>			 	
						</div>
					</div>
				</fieldset>
			</div>
			<div style="text-align: right">
				</span><b>* Legends -->  :</b></span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: #5899FD;" ></span><span style="padding-left:35px;">Eway Insturment</span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: #FFA746; "></span><span style="padding-left:35px;">Group Insturment</span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: white;"></span><span style="padding-left:35px;">Normal Insturment</span>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
        <%@include file="instform.jsp" %>
		<!-- frmMain -->
		<div class="modal fade" tabindex=-1 id="mdlInstruction"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
        <div class="modal-header"><span>&nbsp;IMPORTANT INSTRUCTIONS</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body" height='200px'>
		<table border=0 borcellspacing=1 cellpadding=2 width=100%>
        <tr><td class=frmfld>
                <ul>
                <li>File name can be anything.</li>
                <li>File should contain comma separated utilization information</li>
                <li>First record in the file should be the header record. (see example below)</li>
                <li>Subsequent records in the file should contain following field values separated by commas</li><br>
                </ul>       
                 <table border=1 class=frmfld width=100%>
                        <tr align=center>
                        <th>Headers</th>
						<th>Mandatory</th><th>Description</th><th>Value Range</th><th>Default Value</th></tr>
                            
                        <tr><td>purchaser</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>purLocation</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>supplier</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>supLocation</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr>><td>instNumber</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>instDate</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>poDate</td><td>Yes</td><td></td><td></td><td></td></tr> 
                            
                        <tr><td>poNumber</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>counterRefNum</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>description</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>goodsAcceptDate</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>currency</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>amount</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                         <tr><td>adjAmount</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>tdsAmount</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>creditPeriod</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                            
                         <tr><td>costBearingType</td><td></td><td></td><td><ul><li>S = Seller</li><li>P = Buyer</li><li>PD = Periodical Split</li><li>PC=Percentage Split</li></ul></td><td></td></tr>
                            
                         <tr><td>splittingPoint</td><td>Yes</td><td></td><td><ul><li>S=supplier</li><li>P=purchaser</li></ul></td><td></td></tr>
                            
                         <tr><td>preSplittingCostBearer</td><td></td><td></td><td><ul><li>S = Seller</li><li>P = Buyer</li></ul></td><td></td></tr>
                        
                         
                         <tr><td>postSplittingCostBearer</td><td></td><td></td><td><ul><li>S = Seller</li><li>P = Buyer</li></ul></td><td></td></tr>
                            
                        <tr><td>buyerPercent</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>bidAcceptingEntityType</td><td>Yes</td><td></td><td><ul><li>S = Seller</li><li>P = Buyer</li></ul></td><td></td></tr>
                            
                         <tr><td>instImage</td><td>Yes</td><td></td><td></td><td></td></tr> 
                            
                         <tr><td>creditNoteImage</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>sup1</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>sup2</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>sup3</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>sup4</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>sup5</td><td></td><td></td><td></td><td></td></tr>
                            
						 <tr><td>supportings</td><td></td><td></td><td></td><td></td></tr>
                        
                        </table>
                        <br>
				<ul>
                <li>A Single file should contain utilization for that Business date.</li>
                <li>The import will accept only those records where it matches the selected Hall Code.</li>
                <li>Item Codes not found in the list will be skipped.</li>
                <li>Only those utilizations in the file will be diplayed, rest will be reset to Zero (0).</li>
                </ul>
        </td></tr>
        <tr><td>SAMPLE FILE CONTENTS
        </td></tr>
        <tr><td class=frmfld >
        <pre>
purchaser|purLocation|supplier|supLocation|instNumber|instDate|poDate|poNumber|counterRefNum|description
|goodsAcceptDate|currency|amount|adjAmount|tdsAmount|creditPeriod|costBearingType|splittingPoint|preSplittingCostBearer
|postSplittingCostBearer|buyerPercent|bidAcceptingEntityType|instImage|creditNoteImage|sup1|sup2|sup3|sup4|sup5|supportings

XP0000018|Reg. Office|XS0000015|Reg. Office|12353211|22-MAR-2019|22-MAR-2019|4518781717||Garment
|22-MAR-2019|INR|40572|||90|||||||21036.pdf|||||||
        </pre>
        </td></tr>
        </table>
        <div>
       	<div class="box-footer" align="center">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnDownloadInstructions><span class="fa fa-download"></span> Download as PDF</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpload><span class="fa fa-upload"></span> Upload CSV</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUploadXls><span class="fa fa-upload"></span> Upload Excell</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
        </div>
</div>
		</div></div></div>

	<div class="modal fade" id="mdlTAndC" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;TREDS</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-12"><section><label for="amount" class="label">Please enter suitable reason/remarks</label></section></div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<section class="input">
							<input type="text" id="reason" placeholder="Remarks">
							<b class="tooltip tooltip-top-left">Reason for Submitting</b>
						</section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<label class="checkbox"><input type=checkbox id="agreeTAndC"><i></i><span><a href="javascript:toggleTandCDisplay();">I AGREE TO THE TERMS & CONDITIONS</a></span> <b class="tooltip tooltip-top-left">I Agree</b></label>
					</div>
				</div>
				<div class="row" id="chkAgreement1" style="display:none">
                     <div class="col-sm-12">
                          <label class="checkbox"><input type=checkbox id="agreement1"><i></i><span><a href="javascript:;" id="viewAgreement1">CLICK WRAP AGREEMENT</a></span> <b class="tooltip tooltip-top-left">CLICK WRAP AGREEMENT</b></label>
                     </div>
                </div>
				<div class="row">
					<div class="btn-groupX pull-right" >
						<button type="button" class="btn btn-info btn-primary btn-lg" id=btnFinalSubmit><span class="glyphicon glyphicon-circle-arrow-right"></span> Submit</button>
					</div>
				</div>
				<div class="row" id="dispTAndC" style="display:none">
					<div class="col-sm-12">
						<h3>TERMS & CONDITIONS </h3>
						<ol type="i">
						<li>The invoice underlying the product/service has not been uploaded to RXIL-TReDS platform in past. </li>
						<li>The invoice underlying the product/service has not been factored at any TReDS platform or financed elsewhere.</li>
						<li>The Seller falls under MSME category as on date.</li>
						</ol>
					</div>
				</div>
                           	<div id="dispAgreement1" style="display: none"></div>
			</fieldset>
		</div>
	</div>
	</div></div></div>

	<div class="modal fade" tabindex=-1 id="mdlClubbedInvoice"><div class="modal-dialog  modal-lg"><div class="modal-content">
	</div></div></div> 
	
	<div class="modal fade" id="mdlVerifiedInv" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Add Invoice Details</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="contMdlGstn">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="supplier"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="supClId" class="label">Seller Location:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="supClId"><option value="">Select Seller Location</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
						<section class="ov-view" id="ov-supClId"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="purchaser"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="purClId" class="label">Buyer Location:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="purClId"><option value="">Select Buyer Location</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
						<section class="ov-view" id="ov-purClId"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-2"><section><label for="instNumber" class="label">Invoice Number:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="instNumber" placeholder="Invoice Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="instDate" class="label">Invoice Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="instDate" placeholder="Invoice Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-2"><section><label for="amount" class="label">Invoice Amount <span class="fa fa-custINR curSymbol"></span> :</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="amount" placeholder="Invoice Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
						<section class="ov-view" id="ov-amount"></section>
					</div>
					<div class="col-sm-2"><section><label for="eway" class="label">E-Way</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="eway" placeholder="E-Way Bill No">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveInvoice><span class="fa fa-save"></span> Add Invoice</button>
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
      <%@include file="footer1.jsp"%>
      <script src="../js/bootstrap-datetimepicker.js"></script>
      <script src="../js/datatables.js?v1"></script>
   		<script src="../js/jquery.autocomplete.js"></script>
   		<script src="../js/jquery.bootstrap-duallistbox.js"></script>

	
      <script type="text/javascript">
         var crudInstrument$,crudInstrument,mainForm,gstnMainForm;
         var tplClubbedInstruments,tplMessage;
         var purSupLink;
         var tplCustomfields;
         var tabIdx, tabData;
         var lTnC = false; //terms and condition display initially false
         var lTnC1 = false;
         var lPurAgreement1 = false;
         var lPurHasCheckers = false;
         var lInboxData;
         var crudFields,insertFields,updateFields,mainFormFields;
         //
         $(document).ready(function() {
        	 MAIN_RESOURCE = "inst";
        	tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html());
        	tplCustomfields=Handlebars.compile($('#tplCustomfields').html());
        	tplMessage = Handlebars.compile($('#tplMessage').html());
         	var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class).getJsonConfig()%>;
         	var lSupplier=false, lPurchaser=false;
         	var lSelfCtrlId = null, lSelfCtrl2Id=null; //self controls
         	var lCntrLabel= "Counter", lCntrColName=""; //counter controls
         	var lEwayFieldGroup = null;
         	$.each(loginData.entityTypeList, function( index, value ) {
         		  if(value == 'S')
         			  lSupplier = true;
         		  else if(value == 'P')
         			  lPurchaser = true;
         		});
         	if(lPurchaser){
         		lCntrLabel="Seller";
         		lCntrColName="supName";
         		lSelfCtrlId = "purchaser";
         		lSelfCtrl2Id = 'purClId';
         		mEntityType = 'P';
         	}
         	else if(lSupplier){
         		lCntrLabel="Buyer";
         		lCntrColName="purName";
         		lSelfCtrlId = "supplier";
         		lSelfCtrl2Id = 'supClId';
         		mEntityType = 'S';
         	}
         	$("#spnTab2").html(lCntrLabel);
         	var displayColumns = ["id","instNumber","instDate","salesCategory",lCntrColName,"poNumber","goodsAcceptDate","maturityDate","netAmount","status","age"];
         	var lConfig = {
         			resource: "inst",
         			autoRefresh: true,
         			newDefault:{currency:'INR'},
         			defaultColumns : displayColumns,
         			tableConfig: {
						lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
						lengthChange:true,
						pageLength:10,
						select: {
							style:'multiple',
							allMethod: 'all',
							clearSelectionOnPaging: true 
						},
						createdRow:function( row, data, dataIndex ) {
							if(data.groupFlag){
								$(row).addClass('row'+data.groupFlag);
							}
							if(data.ebdId){
								$(row).addClass('rowEway');
							}
						},
					},
					preSearchHandler: function(pFilter){
						if (tabIdx==HISTORY_INDEX) {
							var lFilterCount = 0;
							$.each(histFilterForm.getValue(),function(pIdx,pVal){
								pFilter[pIdx]=pVal;
								if(pVal!=null&&pVal!="") lFilterCount+=1;
							});
							if(lFilterCount<=0){
								alert("Please select a filter for searching in history.");
								return false;
							}
						}
						pFilter.filtHistFlag = tabIdx==HISTORY_INDEX?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
						if(pFilter!=null&&Object.keys(pFilter).length>1){
							$('#filter').attr('style','color:red');
						}else{
							$('#filter').attr('style','color:black');
						}
						return true;
					},
         			postSearchHandler: function(pObj) {
         				$('#Amt').val("");
         				if (tabIdx==HISTORY_INDEX) {
         					tabData[HISTORY_INDEX]=pObj;
         				} else {
         					var lOldHistData = tabData==null?null:tabData[HISTORY_INDEX];
             				tabData = [];
             				$.each(pObj,function(pIdx,pValue){
             					var lData=tabData[pValue.tab];
             					if (lData==null) {
             						lData=[];
             						tabData[pValue.tab]=lData;
             					}
             					lData.push(pValue);
             				});
             				tabData[HISTORY_INDEX]=lOldHistData;//reput the history data
         				}
         				var lIdx;
         				$('.nav-tabs li a').each(function (index, element) {
         					lIdx = element.attributes['href'].value.substring(4);
         					if (tabData[lIdx]==null) tabData[lIdx]=[];
         					var lCount = tabData[lIdx]?tabData[lIdx].length:0;
         					$('#badge'+lIdx).html(lCount<HISTORY_INDEX?"0"+lCount:lCount);
         				});
         				showData();
         				//displaying terms and conditions only for inst maker
         				document.getElementById("termsAndConditions").style.display = "";
         				document.getElementById("btnSaveSubmit").style.display = "";
         	        	if(tabData!=null && tabData.length > 0){
         	       			lInboxData = tabData[0] ;
         	        	}else{
         	        		lInboxData = [];
         	        	}
         				return false;
         			},
         			postNewHandler: function() {
         				if (lPurchaser){
         					getCustomFields(null,null);
         				}
         				$('#creationKeys').hide();
         				setPurSupLink(true,true);
         				if(lSelfCtrlId!=null){
         					mainForm.getField(lSelfCtrlId).setValue(loginData.domain);
         					populateLoc(lSelfCtrlId,lSelfCtrl2Id,loginData.domain);
         				}
         		    	mainForm.enableDisableField([lSelfCtrlId],false,false);
         		    	lEwayFieldGroup=crudInstrument.options.fieldGroups.ewayBill;
         		    	if(lSupplier){
         		    		mainForm.enableDisableField(lEwayFieldGroup, false, false);
         		    	}
         				setAutoAcceptBid();
         				return true;
         			},
         			postModifyHandler: function(pObj) {
         				$('#creationKeys').hide();
         				setPurSupLink(true,false);
         				lEwayFieldGroup=crudInstrument.options.fieldGroups.ewayBill;
         		    	if(lSupplier){
         		    		mainForm.enableDisableField(lEwayFieldGroup, false, false);
         			 	}
         				populateLoc('supplier','supClId',pObj.supClId);
         				populateLoc('purchaser','purClId',pObj.purClId);
         				getCustomFields(mainForm.getField('purchaser').getValue(),pObj.cfId);
     					var tmpMap = JSON.parse(pObj.cfData);
     					for (var i in tmpMap){
     						mainForm.getField(i).setValue(tmpMap[i]);
     					}
         				onChangeSalesCategory();
         				setOldModifiedValue(pObj.counterModifiedFields);
         		    	mainForm.enableDisableField([lSelfCtrlId],false,false);
         				//this is done so that while saving we can identify whether it is save or SubmitAndSave
         				//$('#statusRemarks').prop('value', '');
         				$('#status').prop('value', '');
         				setAutoAcceptBid();
         				mainForm.getField('costBearingType').setValue(pObj.costBearingType);
         				mainForm.getField('creditPeriod').setValue(pObj.creditPeriod);
         				onCostBearingChange();
         				if(pObj.ebdId!=null){
         					mainForm.enableDisableField(crudInstrument.options.fieldGroups.eInvoiceFields, false, false);
         					mainForm.enableDisableField(crudInstrument.options.fieldGroups.ewayBill, false, false);
         				}
         				mainForm.enableDisableField(crudInstrument.options.fieldGroups.duedates, false, false);
         					populateInstrumentKeys(pObj.supplier,pObj.purchaser,pObj.id,pObj.supClId);
					    mainForm.getField('instrumentCreationKeysList').setValue(pObj.instrumentCreationKeysList);
         				return true;
         			},
         			preSaveHandler: function(pObj) {
         				if(!preSaveCommonValidation()){
         					$('#btnSaveSubmit').prop('disabled',false);
         					return false;
         				}
         				$('#btnSaveSubmit').prop('disabled',true);
         				return true;
         			},
         			postSaveHandler: function(pObj) {
         				$('#btnSaveSubmit').prop('disabled',false);
         				return true;
         			},
         			preCheckHandler: function() {
         				if(window.calculateCDandDeductions){
         					calculateCDandDeductions();
         				}
         				return true;
         			},
         			preModifyHandler: function(pData) {
         				if($('#btnModify').data('clicked', true) && ("<%=CommonAppConstants.Yes.Yes%>" == pData.groupFlag) ){
         					alert("Grouped Instrument can't be modified.");
         					return false;
         				}
         				if(pData.ebdId!=null){
         					mainForm.enableDisableField(crudInstrument.options.fieldGroups.eInvoiceFields, false, false);
         					mainForm.enableDisableField(crudInstrument.options.fieldGroups.ewayBill, false, false);
         	        		$("#ewayInv").show();
         	        		$("#ewayInv").attr("eway-id", pData.ebdId);
         	        	}else{
         	        		$("#ewayInv").hide();
         	        		$("#ewayInv").attr("eway-id", "");
         	        	}
         				mainForm.enableDisableField(crudInstrument.options.fieldGroups.duedates, false, false);
         				return true;
         			},
         			preRemoveHandler: function(pData) {
       					for(var i=0;i<pData.length;i++){
       						if($('#btnRemove').data('clicked', true) && ("<%=CommonAppConstants.Yes.Yes%>" == pData[i].groupFlag) ){
       							pData.pop();
               				}
       					}
       					if (pData.length>1){
       						deleteMultipleInstruments(pData);
       						return false;
       					}
         				return true;
         			},
         			errorHandler: function( pXhr, pStatus, pError ) {
         				$('#btnSaveSubmit').prop('disabled',false);
         				errorHandler(pXhr,pStatus,pError);
         			}
         	};
         	lConfig = $.extend(lConfig, lFormConfig);
         	crudInstrument$ = $('#contInstrument').xcrudwrapper(lConfig);
         	crudInstrument = crudInstrument$.data('xcrudwrapper');
         	crudFields = crudInstrument.options.fields;
         	insertFields = crudInstrument.options.fieldGroups.insert;
         	updateFields = crudInstrument.options.fieldGroups.update;
         	mainForm = crudInstrument.options.mainForm;
         	mainFormFields = mainForm.fields;
    		var lGstnConfig = {
    				"fields": [
    						{
    							"name":"purchaser",
    							"label":"Buyer",
    							"dataType":"STRING",
    							"maxLength": 30,
    							"dataSetType":"RESOURCE",
    							"dataSetValues":"gstmandate/purlov",
    							"notNull": true
    						},
    						{
    							"name":"supplier",
    							"label":"Supplier",
    							"dataType":"STRING",
    							"maxLength": 30,
    							"dataSetType":"RESOURCE",
    							"dataSetValues":"gstmandate/suplov",
    							"notNull": true
    						},
    						{
    							"name":"supClId",
    							"label":"Seller Location",
    							"dataType": "INTEGER",
    							"notNull": true
    						},
    						{
    							"name":"purClId",
    							"label":"Buyer Location",
    							"dataType": "INTEGER",
    							"notNull": true
    						},
    						{
    							"name":"amount",
    							"label":"Invoice Amount",
    							"dataType":"DECIMAL",
    							"integerLength":10,
    							"decimalLength":2,
    							"notNull": true,
    							"minValue": 0.01,
    							"format":"#,##,##,##,##,###.00"
    						},
    						{
    							"name":"instDate",
    							"label":"Invoice Date",
    							"dataType":"DATE",
    							"notNull": true
    						},
    						{
    							"name":"instNumber",
    							"label":"Invoice Number",
    							"dataType":"STRING",
    							"maxLength": 30,
    							"notNull": true
    						},
    						{
    							"name":"eway",
    							"label":"E-Way Bill No",
    							"maxLength":30,
    							"dataType":"STRING",
    							"notNull": true
    						}
    				]
    			};

         	crudgstn$ = $('#contMdlGstn').xform(lGstnConfig);
         	crudgstn = crudgstn$.data('xform');
         	fillSupplierPurchaser();
         	attachEvents();
         
         	if(lPurchaser){
         		getAgreementHtml();
         		getCheckers();
         	}
         	$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
 	        	document.getElementById("divFilterGrpAmt").style.display = "none";
         	    var lRef1 = $(event.target).attr('href');         // active tab
         	    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
         	    tabIdx = parseInt(lRef1.substring(4));
         	    if (lRef2)
         	    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
         	    $('.btn-group'+tabIdx).removeClass('hidden');
         	    showData();
         	});
         	$('#btnSubmit').on('click', function() {
         		var lRows=crudInstrument.getSelectedRows();
         		if(lRows.length<1){
         			alert("Please atleast one row.");	
         		}else{
         			$.each(lRows, function(pIndex,pValue) {
         				if ( !(pValue.data().status == '<%=InstrumentBean.Status.Checker_Returned.toString()%>' ||
         						pValue.data().status == '<%=InstrumentBean.Status.Counter_Returned.toString()%>' ||
         						pValue.data().status == '<%=InstrumentBean.Status.Drafting.toString()%>' ) ){
	 						alert("Instrument already submitted.");
	 						return false;
 						}
        			});
         			
         			$('#agreeTAndC').prop('checked',false);
             		$('#agreement1').prop('checked',false);
             		$('#reason').prop('value','');
        			$("#dispAgreement1").html(lAgreementHtml);
             		showModal($('#mdlTAndC'));
         		}
         	});
         	$('#btnFinalSubmit').on('click', function() {
         		if($('#agreeTAndC').prop('checked')){
         			
         			if(lPurchaser && !lPurHasCheckers && !($('#agreement1').prop('checked')) ){
         				alert("Please select click wrap agreement.");
         				return;
         			}
         			var lIds=null;
         			var lRows=crudInstrument.getSelectedRows();
         			$.each(lRows, function(pIndex,pValue) {
         				if (lIds==null){
         					lIds = pValue.data().id;
         				}else{
         					lIds += ","+pValue.data().id;
         				}
         				
            		});
         			var lData = {ids:lIds,status: '<%=InstrumentBean.Status.Submitted.getCode()%>',statusRemarks : $('#reason').prop('value'),acceptAgreement : 'Y'};
         			$('#btnFinalSubmit').prop('disabled',true);
         			$('#btnSubmit').prop('disabled',true);
         			$.ajax( {
         	            url: "inst/status",
         	            type: "POST",
         	            data:JSON.stringify(lData),
         	            success: function( pObj, pStatus, pXhr) {
         	            	alert(tplMessage(pObj));
                     		crudInstrument.showSearchForm();
                     		$('#mdlTAndC').modal('hide');
         	            },
         	        	error: errorHandler,
         	        	complete: function() {
         		        	$('#btnSubmit').prop('disabled',false);
                 			$('#btnFinalSubmit').prop('disabled',false);
         		        }
         	        });					
         		}else{
         			alert("Please select I agree to the Terms and Conditions.");
         		}
         	});
         	$('#btnAddToGroup').on('click', function() {
				confirm('You are about to group the unticked instruments. The ticked instruments will be excluded from grouping. Do you want to continue with the grouping process?','Group Creation Confirmation','Yes',function(pYes) {
					if (pYes) {
						var lRows=crudInstrument.getSelectedRows();
		    			var lSelectedData = new Map();
		    			$.each(lRows, function(pIndex,pValue) {
		    				lSelectedData.set(pValue.data().id,pValue.data().id);
		    			});
		    			var lData = [];
		         		var data = crudInstrument.options.dataTable.rows().data();
		         		data.each(function (value, index) {
                            if(lSelectedData.get(value.id)==null && value.groupFlag!='Yes'){
		         				lData.push(value.id);
		         			}
		         		});
		         		if(lData.length>1){
		         			$('#btnAddToGroup').prop('disabled',true);
			    			$.ajax( {
			    		        url: "inst/addgroup",
			    		        type: "POST",
			    		        data:JSON.stringify(lData),
			    		        success: function( pObj, pStatus, pXhr) {
			    		        	alert(tplMessage(pObj));
			    		        	crudInstrument.showSearchForm();
			    		        },
			    		        error: errorHandler,
			    		        complete: function() {
			    		        	$('#btnAddToGroup').prop('disabled',false);
			    		        }
			    			});
		         		}else{
		         			alert("No group created.");
		         		}
					}
				});
    		});
         	$('#btnRemoveFromGroup').on('click', function(){
         		var lRows=crudInstrument.getSelectedRows();
         		if(!lRows.length>0){
         			alert("Please select atlease one row.")
         		}else{
         			var lData = [];
         			$.each(lRows, function(pIndex,pValue) {
         				if(pValue.data().groupFlag=='Yes'){
	         				lData.push(pValue.data().id);
	         			}
        			});
             		$.ajax( {
        		        url: "inst/ungroup",
        		        type: "POST",
        		        data:JSON.stringify(lData),
        		        success: function( pObj, pStatus, pXhr) {
        		        	alert(tplMessage(pObj));
        		        	crudInstrument.showSearchForm();
        		        },
        		        error: errorHandler,
        		        complete: function() {
        		        }
        			});
         		}
         	});
         	$('#btnClubbedInstruments').on('click', function() {
				viewClubbedInstruments(crudInstrument,tplClubbedInstruments);
         	});

         	$('#btnViewInstru').on('click', function() {
         		var lRows=crudInstrument.getSelectedRows();
         		var lView = false;
         		if(lRows.length!=1){
         			if(lRows.length<1) alert("Please select a row.");
         			if(lRows.length>1) alert("Please select only one row.");	
         		}else{
         			var lData = null;
         			$.each(lRows, function(pIndex,pValue) {
         				lData = pValue.data().id;
         				lView = (pValue.data().groupFlag == 'Yes');
        			});
         			if(lView){
         				alert("Please select a non-group instrument.");
         			}else{
						showRemote('instview?id='+lData, 'modal-xl', true,'Instrument Details');
         			}
         		}
         	});
         	$('#btnUpload').on('click', function() {
         		$('#mdlInstruction').modal('hide');
         		showRemote('inst/instupload', null, false);
         	});
         	$('#btnUploadXls').on('click', function() {
         		$('#mdlInstruction').modal('hide');
         		showRemote('inst/instupload?type=xls', null, false);
         	});
        	$('#btnInstructions').on('click', function() {
         		showModal($('#mdlInstruction'));
         	});
         	//$('#frmSearch .nav-tabs a:first').tab('show');
         	 $("#btnDownloadCSV").on("click",function(){
         		 if(tabIdx !=null && tabIdx==HISTORY_INDEX){
         			var lHistoryFilters = histFilterForm.getValue(true);
                    if(lHistoryFilters==null){
                        lHistoryFilters = {};
                    }
                    if(lHistoryFilters!=null && Object.keys(lHistoryFilters).length <= 0){
                        alert("Please select a filter before downloading.");
                        return;
                    }
                    lHistoryFilters["tab"] = tabIdx;
      	     	 	lHistoryFilters["columnNames"] = crudInstrument.options.tableConfig.columnNames;
      	     	 	lHistoryFilters["filtHistFlag"] = tabIdx==HISTORY_INDEX?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
               		downloadFile('inst/all',null,JSON.stringify(lHistoryFilters));
         		 }else{
              		downloadFile('inst/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "tab": tabIdx}));
         		 }
         	});
         	$('#viewAgreement').on('click', function() {
         		$("#dispAgreement").html(lAgreementHtml);
         		$("#modelAgreement").modal('show');
         	});
         	$('#viewAgreement1').on('click', function() {
         		lPurAgreement1 =!lPurAgreement1;
         		document.getElementById("dispAgreement1").style.display = lPurAgreement1?"":"none";
         	});
         <%String lTab = StringEscapeUtils.escapeHtml(request.getParameter("t"));
            if (lTab == null)
            	lTab = "0";%>
         	$('#frmSearch li:eq(<%=lTab%>) a').tab('show');
         	
        	$('#contMdlGstn #supplier').on('change', function() {
        		var code = $('#contMdlGstn #supplier').val() ;
    			populateLocGSTN('supplier','supClId',code);
    		});
    		$('#contMdlGstn #purchaser').on('change', function() {
    			var code = $('#contMdlGstn #purchaser').val() ;
    			populateLocGSTN('purchaser','purClId',code);
        		
    		});
         	
         	$('#btnAddInvoice').on('click', function() {
         		var lRows=crudInstrument.getSelectedRows();
         		if(lRows.length!=0){
         			if(lRows.length>0) {
         				alert("Please unselect a row.");
         				return;
         			}
         		}
         		$("#mdlVerifiedInv").modal('show');	
         		crudgstn.setValue(null);
         	});
         	
         	$('#btnSaveInvoice').on('click', function() {
    			var lData = crudgstn.getValue(true);
    			var lErrors = crudgstn.check();
				if ((lErrors != null) && (lErrors.length > 0)) {
					showError(crudgstn);
					return;
				}
				public_vars.$pageLoadingOverlay.removeClass('loaded');
    			$.ajax( {
    		        url: "gstmandate/addInst",
    		        type: "POST",
    		        data: JSON.stringify(lData),
    		        success: function( pObj, pStatus, pXhr) {
    		        	if(pObj.ebdId){
    		        		$('#mdlVerifiedInv').modal('hide');    		        		
    		        	}
    		        	crudInstrument.newHandler();
    		        	populateLocationAndSetDataToForm(pObj);
    		        },
    		        error: function( pObj, pStatus, pXhr) {
   		        		alert(pObj.responseJSON.messages[0] , "Information", function() {
       					});
    		        },
    		        complete: function() {
    		        	 public_vars.$pageLoadingOverlay.addClass('loaded');
    		        }
    			});
            	
    		});
         	$("#btnDownloadInstructions").on('click',function(){
         		window.open("../static/InstrumentInstruction.pdf","_blank");
         	});
         });
         
         
         function showData() {
         	crudInstrument.options.dataTable.rows().clear();
         	if (tabData && (tabData[tabIdx] != null))
         		crudInstrument.options.dataTable.rows.add(tabData[tabIdx]).draw();
         }
         function setOldModifiedValue(pModifiedJson){
         	//clear the previous red marking
         	var lFieldGroup = crudInstrument.options.fieldGroups.fetchCounter;
         	for(var lPtr=0; lPtr < lFieldGroup.length;  lPtr++){
         		$('#'+lFieldGroup[lPtr]).css('border-color','');
         	}
         	//mark modified fields red
         	if(pModifiedJson!=null&&pModifiedJson!=''){
         		var lData = JSON.parse(pModifiedJson);
         		if(lData!=null){
         			for(var key in lData){
         				//$('#ov-'+key).html(lData[key]);
         				$('#'+key).css('border-color','red');
         			}
         		}
         	}
         }
         function toggleTandCDisplay(){
         	lTnC = !lTnC;
         	document.getElementById("dispTAndC").style.display = lTnC?"":"none";
         }
         function toggleTandCDisplay1(){
         	lTnC1 = !lTnC1;
         	document.getElementById("dispTAndC1").style.display = lTnC1?"":"none";
         }
         function getCheckers(){
         	$.ajax({
                 url: 'inst/getCheckers',
         				type : 'GET',
         				success : function(pObj, pStatus, pXhr) {
         					lPurHasCheckers = (pObj!=null && pObj.hasCheckers);
         					document.getElementById("panAgreement").style.display = lPurHasCheckers?"none":"";
         					document.getElementById("chkAgreement1").style.display = lPurHasCheckers?"none":"";
         				},
         				error : function(pObj, pStatus, pXhr) {
         					oldAlert('Error');
         				}
         			});
         }
         function displayFilter() {
        	 if(document.getElementById("divFilterGrpAmt").style.display == "none"){
        		 document.getElementById("divFilterGrpAmt").style.display = "block";
        	 }else{
        		 document.getElementById("divFilterGrpAmt").style.display = "none";
        	 }
        }
        function showFilteredData(pVal){
        	 //filter taking the 
        	 var lCount =0;
        	if(tabData!=null && tabData.length > 0){
        		if(!pVal){
        			tabData[0]= lInboxData ;
        			 $('#Amt').val("");
        			 	lCount = lInboxData.length;
        		}else{
       			 	//filtering apply
       			 	//set to lInboxFilterData
       			 	//set it to tabData
       			 	if($('#Amt').val()==''){
       			 		alert("Please enter a value");
       			 	}else{
       			 	var lTmpFilteredData = [];
    				lInboxData.forEach(function (lData){
    				    if(lData.netAmount < $('#Amt').val()){
    				    	lTmpFilteredData.push(lData);
    				    }
    				});
       			 	tabData[0] = lTmpFilteredData;
       			 	lCount = lTmpFilteredData.length;
       			 	}
    			}
            }
        	tabIndex = 0;
        	tabIdx = 0;
			showData();
			$('#badge'+0).html(lCount<HISTORY_INDEX?"0"+lCount:lCount);
       	}
       	function removeFromClubbing(pParentId){
       		var ids = null;
       	 	$(".sub_chk:checked").each(function() {  
       	 		if (ids==null){
       	 			ids = $(this).attr('data-id');
       	 		}else{
       	 			ids = ids +"," + $(this).attr('data-id');
       	 		}
       		});
       	 	if(ids!=null){
       	 		$.ajax({
                	url: 'inst/removefromgroup/'+pParentId+'/'+ids,
        				type : 'POST',
        				success : function(pObj, pStatus, pXhr) {
        					crudInstrument.showSearchForm();
        					$('#mdlClubbedInvoice').modal('hide');
        				},
        				error : function(pObj, pStatus, pXhr) {
        					alert(pObj.responseJSON.messages);
        				}
        			});
      		}else{
      			
      		}
       	 }
       	function downloadGroupCsv(pParentId) {
     		downloadFile('inst/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "groupInId": pParentId}));
     	}
       	
       	function deleteMultipleInstruments(pData) {
       		var lSelectedIds = [];
       		for(var i=0;i<pData.length;i++){
       			lSelectedIds.push(pData[i].id);
       		}
       		var lData = {};
       		if (lSelectedIds.length>0){
       			lData['idList'] = lSelectedIds;
       			$.ajax( {
    	            url: "inst/remove",
    	            type: "POST",
    	            data:JSON.stringify(lData),
    	            success: function( pObj, pStatus, pXhr) {
    	            	alert(tplMessage(pObj));
    	            },
    	        	error: errorHandler,
    	        	complete: function() {
    	        	}
    	        });			
       		}
       	}
       	
       	function fillSupplierPurchaserForInvoice(){
    		$.ajax({
                url: 'pursuplnk/lov',
                type: 'GET',
                success: function( pObj, pStatus, pXhr) {
                	purSupLink = pObj;
                	var lSup=[],lPur=[];
                	var lSupMap={},lPurMap={};
                	$.each(purSupLink,function(pIndex,pValue){
                		if (lSupMap[pValue.supplier]==null) {
                			lSupMap[pValue.supplier]=true;
                			var lDisp=pValue.supplier===loginData.domain?pValue.supName:(pValue.purchaserSupplierRef + '(' + pValue.supName + ')');
                			lSup.push({text:lDisp, value:pValue.supplier})
                		}
                		if (lPurMap[pValue.purchaser]==null) {
                			lPurMap[pValue.purchaser]=true;
                			var lDisp=pValue.purchaser===loginData.domain?pValue.purName:(pValue.supplierPurchaserRef + '(' + pValue.purName + ')');
                			lPur.push({text:lDisp, value:pValue.purchaser})
                		}
                	});
                	$.each([lSup,lPur],function(pIdx,pVal){
                		pVal.sort(function(a,b){
                			return a.text.toUpperCase().localeCompare(b.text.toUpperCase());
                		});
                	});
                	var lSupFld=crudgstn.getField('supplier');
                	lSupFld.options.dataSetType='STATIC';
                	lSupFld.options.dataSetValues=lSup;
                	lSupFld.init();
                	var lPurFld=crudgstn.getField('purchaser');
                	lPurFld.options.dataSetType='STATIC';
                	lPurFld.options.dataSetValues=lPur;
                	lPurFld.init();
                	console.log("fill called");
                },
            	error: errorHandler,
            });
    	}
    	function populateLocGSTN(pEntityField, pLocField, pCode) {
			var lField=crudgstn.getField(pLocField);
			var	lActiveOnly=true; 
			lField.options.dataSetType='STATIC';
			lField.options.dataSetValues=[];
			lField.options.dataSetValues = "gstmandate/settleactivelov?aecode="+pCode+"&activeOnly="+lActiveOnly;
			lField.init();
			$.each(purSupLink,function(pIndex,pValue){
   				if (pValue.purchaser==loginData.domain && pValue.supplier==crudgstn.getField('supplier').getValue()){
   					checkAccessOnInstrumentCreation(crudgstn,pValue.instrumentCreation,'supClId',['btnSaveInvoice'],'<%=PurchaserSupplierLinkBean.InstrumentCreation.Supplier.getCode()%>');
   				}else if (pValue.supplier==loginData.domain && pValue.purchaser==crudgstn.getField('purchaser').getValue()){
   					checkAccessOnInstrumentCreation(crudgstn,pValue.instrumentCreation,'purClId',['btnSaveInvoice'],'<%=PurchaserSupplierLinkBean.InstrumentCreation.Purchaser.getCode()%>');
   				}
   				return false;
    		});
		}
    	function populateLocationAndSetDataToForm(pObj){
    		var lField=null;
    		var lEntity=null;
    		if(pObj!=null && pObj.supplier==loginData.domain){
    			lField=mainForm.getField('purClId');
    			lEntity=pObj.purchaser;
    		}
    		if (pObj!=null && pObj.purchaser==loginData.domain){
    			lField=mainForm.getField('supClId');
    			lEntity=pObj.supplier;
    		}
			var	lActiveOnly=true; 
			lField.options.dataSetType='STATIC';
			lField.options.dataSetValues=[];
			lField.options.dataSetValues = "companylocation/settleactivelov?aecode="+lEntity+"&activeOnly="+lActiveOnly;
			lField.init();
        	//Set Data
        	mainForm.setValue(pObj);
        	if(pObj.ebdId!=null){
        		mainForm.enableDisableField(crudInstrument.options.fieldGroups.eInvoiceFields, false, false);
        		mainForm.enableDisableField(crudInstrument.options.fieldGroups.ewayBill, false, false);
        		$("#ewayInv").show();
        		$("#ewayInv").attr("eway-id", pObj.ebdId);
        	}else{
       			$("#ewayInv").hide();
        		$("#ewayInv").attr("eway-id", "");
        	}
    	}
    	function showError(pObj) {
			var lResp = appendError(pObj.fields, true);
			alert(lResp[0] , "Validation Failed", function() {
			});
		}
    	function populateInstrumentKeys(pSupplier,pPurchaser,pId,pSupClId){
    		lData = {};
     		lData['supplierCode'] = pSupplier;
     		lData['purchaserCode'] = pPurchaser;
     		lData['inId'] = pId;
     		lData['supClId'] = pSupClId;
     		$.ajax( {
		        url: "instrumentcreationkeys/lov",
		        type: "POST",
		        async:false,
		        data: JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
                	var lInstKeys = mainForm.getField('instrumentCreationKeysList');
                	lInstKeys.options.dataSetType='STATIC';
                	lInstKeys.options.dataSetValues=pObj;
                	lInstKeys.init();
		        },
		        error: function( pObj, pStatus, pXhr) {

		        },
		        complete: function() {
		        }
			});
    	}

     	
     	function getCustomFields(pCode,pCfId){
    		var lEntCode = pCode; 
    		if (lEntCode == null){
    			lEntCode = loginData.domain;
    		}
    		var lCustomConfig = null;
    		var lUrl = "appentity/instConf/"+lEntCode;
    		if (pCfId!=null){
    			lUrl +="?cfId="+pCfId;
    		}
    		crudInstrument.options.fields= [];
    		crudInstrument.options.fields.push(...crudFields);
    		mainForm.options.fieldGroups.insert = [];
    		mainForm.options.fieldGroups.insert.push(...insertFields);
    		mainForm.options.fieldGroups.update = [];
    		mainForm.options.fieldGroups.update.push(...updateFields);
    		mainForm.fields = [];
    		mainForm.fields.push(...mainFormFields);
     		if (lEntCode!=null){
     			$.ajax( {
     				url: lUrl,
     		        type: "GET",
     		        async:false,
     		        success: function( pObj, pStatus, pXhr) {
     		        	if (pObj == null){
     		        		
     		        	}else{
     		        		var lCustConf = {inputParams:pObj.inputParams};
         		        	$('#divCustomfields').html(tplCustomfields(lCustConf));
         		        	mainForm.getField('cfId').setValue(pObj.cfId);
         		        	crudInstrument.options.fields.push(...pObj.inputParams);
         		        	mainForm.options.fieldGroups.insert.push(...pObj.inputParams.map(pField => pField.name));
         		        	mainForm.options.fieldGroups.update.push(...pObj.inputParams.map(pField => pField.name));
         		        	$.each(pObj.inputParams, function(pIndex, pValue) {
         		        		mainForm.initField(pValue);
         					});
     		        	}
     		        },
     		        error: function( pObj, pStatus, pXhr) {

     		        },
     		        complete: function() {
     		        }
     			});
     		}
    	}
          	
      </script>
   </body>
</html>