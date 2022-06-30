<!DOCTYPE html>
<%@page import="com.xlx.treds.instrument.bean.InstReportBean"%>
<%@page import="com.xlx.treds.auction.bean.BidBean"%>
<%@page import="groovy.json.JsonBuilder"%>
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.user.bean.RoleMasterBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Instruments Report</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
        <style>
	        .set{
	        	font-color:BLACK;
	        }
	        .btn-default {
	        	color: unset;
	        	background-color: unset;
	        }
	        .rowYes{
				background-color: #FFA746 !important;
			}
        </style>
        
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Instruments_Report" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="instReport">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Instruments Report</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<fieldset>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="inst_Id" class="label">Instrument ID:</label>
								</section>
							</div>
							<div class="col-sm-2">
								<section class="input">
									<input type="text" id="inst_Id" placeholder="Instrument Id">
									<b class="tooltip tooltip-top-right"></b>
								</section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="inst_Status" class="label">Instrument
										Status:</label>
								</section>
							</div>
							<div class="col-sm-6">
								<section class="select">
									<select id="inst_Status" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
									<section class="view"></section>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="fact_Id" class="label">FactoringUnit ID:</label>
								</section>
							</div>
							<div class="col-sm-2">
								<section class="input">
									<input type="text" id="fact_Id" placeholder="FactoringUnit ID">
									<b class="tooltip tooltip-top-right"></b>
								</section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="fact_Status" class="label">FactoringUnit
										Status:</label>
								</section>
							</div>
							<div class="col-sm-6">
								<section class="select">
									<select id="fact_Status" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="bid_Id" class="label">Bid Id:</label>
								</section>
							</div>
							<div class="col-sm-2">
								<section class="input">
									<input type="text" id="bid_Id" placeholder="Bid Id"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="bid_Status" class="label">Bid Status:</label>
								</section>
							</div>
							<div class="col-sm-6">
								<section class="select">
									<select id="bid_Status" multiple="multiple" data-role="multiselect"></select>
									<b class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="inst_SalesCategory" class="label">Sales
										Category:</label>
								</section>
							</div>
							<div class="col-sm-10">
								<section class="select">
									<select id="inst_SalesCategory" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="fact_Purchaser" class="label">Buyer:</label>
								</section>
							</div>
							<div class="col-sm-10">
								<section class="select">
									<select id=fact_Purchaser multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="fact_Supplier" class="label">Seller:</label>
								</section>
							</div>
							<div class="col-sm-10">
								<section class="select">
									<select id="fact_Supplier" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="bid_FinancierEntity" class="label">Financier:</label>
								</section>
							</div>
							<div class="col-sm-10">
								<section class="select">
									<select id="bid_FinancierEntity" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="inst_AggregatorEntity" class="label">Aggregator:</label>
								</section>
							</div>
							<div class="col-sm-10">
								<section class="select">
									<select id="inst_AggregatorEntity" multiple="multiple"
										data-role="multiselect"></select> <b
										class="tooltip tooltip-top-right"></b><i></i>
								</section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label for="inst_IsAggregatorCreated" class="label">Aggregator Created:</label></section></div>
							<div class="col-sm-10">
								<section class="select">
								<select id="inst_IsAggregatorCreated"><option value="">Select</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</fieldset>
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
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>		
					<span class="right_links">
						<a href="javascript:crudInstrumentReport.searchHandler();"><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
						<a href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
						<span id="spnColumnChooser" class="glyphicon glyphicon-plus"></span>
					</span>
				</div>
			</div>
			<!-- frmMain -->
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " data-col-chooser="spnColumnChooser" data-selector="multiple" id="tblData">
								<thead><tr>
										<th data-width="90px" data-name="inst_Id">Id</th>
										<th data-width="120px" data-name="inst_PurName">Buyer</th>
										<th data-width="120px" data-name="inst_SupName">Seller</th>
										<th data-width="70px" data-name="inst_PoDate">Purchase Order Date</th>
										<th data-width="100px" data-name="inst_PoNumber">Purchase Order Number</th>
										<th data-width="100px" data-name="inst_PurLocation">Buyer Location</th>
										<th data-width="100px" data-name="inst_InstNumber">Invoice Number</th>
										<th data-width="100px" data-name="inst_SalesCategory">Sales Category</th>
										<th data-width="100px" data-name="inst_SupLocation">Seller Location</th>
										<th data-width="70px" data-name="inst_InstDate">Invoice Date</th>
										<th data-width="110px" data-name="inst_GoodsAcceptDate">Goods/Service Acceptance Date</th>
										<th data-width="70px" data-name="inst_InstDueDate">Invoice Due Date</th>
										<th data-width="70px" data-name="inst_MaturityDate">Maturity Date</th>
										<th data-width="100px" data-name="fact_ExtendedDueDate">Extended Maturity Date</th>
										<th data-width="80px" data-name="inst_Amount">Invoice Amount (in Actual)</th>
										<th data-width="80px" data-name="inst_HaircutPercent">Haircut %</th>
										<th data-width="80px" data-name="inst_AdjAmount">Deductions (in Actual)</th>
										<th data-width="80px" data-name="inst_CashDiscountPercent">Cash Discount %</th>
										<th data-width="80px" data-name="inst_CashDiscountValue">Cash Discount Value</th>
										<th data-width="80px" data-name="inst_TdsAmount">TDS (in Actual)</th>
										<th data-width="100px" data-name="inst_NetAmount">Factoring Unit Cost (in Actual)</th>
										<th data-width="110px" data-name="inst_Status">Status</th>
										<th data-width="80px" data-name="inst_Age">Aging</th>
										<th data-width="80px" data-name="inst_CostBearingType">Cost Bearing Type</th>
										<th data-width="80px" data-name="inst_SplittingPoint">Splitting Point</th>
										<th data-width="80px" data-name="inst_PreSplittingCostBearer">Pre Splitting Cost Bearer</th>
										<th data-width="80px" data-name="inst_PostSplittingCostBearer">Post Splitting Cost Bearer</th>
										<th data-width="80px" data-name="inst_BuyerPercent">Buyer Percent</th>
										<th data-width="80px" data-name="inst_SellerPercent">Seller Percent</th>
										<th data-width="80px" data-name="inst_ChargeBearer">Charge Bearer</th>
										<th data-width="80px" data-name="inst_SplittingPointCharge">Splitting Point Bearer</th>
										<th data-width="80px" data-name="inst_PreSplittingCharge">Pre Splitting Charge Bearer</th>
										<th data-width="80px" data-name="inst_PostSplittingCharge">Post Splitting Charge Bearer</th>
										<th data-width="80px" data-name="inst_BuyerPercentCharge">Buyer Percent Charge</th>
										<th data-width="80px" data-name="inst_SellerPercentCharge">Seller Percent Charge</th>
										<th data-width="80px" data-name="inst_BidAcceptingEntityType">Bid Accepting Entity Type</th>
										<th data-width="100px" data-name="inst_MonetagoLedgerId">Monetago LedgerId</th>
										<th data-width="100px" data-name="inst_MonetagoFactorTxnId">Monetago FactorTxId</th>
										<th data-width="100px" data-name="inst_MonetagoCancelTxnId">Monetago CancelTxId</th>
										<th data-width="70px" data-name="inst_MakerLoginId">Maker User</th>
										<th data-width="70px" data-name="inst_CheckerLoginId">Checker User</th>
										<th data-width="70px" data-name="inst_OwnerEntity">Owner</th>
										<th data-width="70px" data-name="inst_OwnerLoginId">Owner User</th>
										<th data-width="130px" data-name="inst_StatusUpdateTime">Inst Status Update Time</th>
									  	<th data-width="100px" data-name="fact_Id">Factoring Unit ID</th>
										<th data-width="100px" data-name="fact_Currency">Currency</th>
										<th data-width="100px" data-name="fact_Amount">Amount</th>
										<th data-width="100px" data-name="fact_PurchaserRef">Buyer Reference</th>
										<th data-width="100px" data-name="fact_SupplierRef">Seller Reference</th>
										<th data-width="100px" data-name="fact_Status">Factoring Status</th>
										<th data-width="100px" data-name="fact_FactorStartDateTime">Factor Start Date</th>
										<th data-width="100px" data-name="fact_FactorEndDateTime">Factor End Date</th>
										<th data-width="100px" data-name="fact_FactorMaxEndDateTime">Factor Max End Date Time</th>
										<th data-width="100px" data-name="fact_AutoAccept">AutoAccept</th>
										<th data-width="100px" data-name="fact_AutoAcceptableBidTypes">Auto Acceptable BidTypes</th>
									 	<th data-width="100px" data-name="fact_SettleLeg3Flag">Settle Leg3 Flag</th>
										<th data-width="100px" data-name="fact_BdId">Accepted Bid ID</th>
										<th data-width="100px" data-name="fact_AcceptedRate">Accepted Rate</th>
										<th data-width="100px" data-name="fact_AcceptedHaircut">Accepted Haircut</th>
										<th data-width="100px" data-name="fact_FactoredAmount">Factored Amount</th>
										<th data-width="100px" data-name="fact_PurchaserLeg1Interest">Purchaser Leg1 Interest</th>
										<th data-width="100px" data-name="fact_SupplierLeg1Interest">Supplier Leg1 Interest</th>
										<th data-width="100px" data-name="fact_PurchaserLeg2Interest">Purchaser Leg2 Interest</th>
										<th data-width="100px" data-name="fact_Financier">Financier</th>
										<th data-width="100px" data-name="gst_charge">Charges</th>
										<th data-width="100px" data-name="gst_cgstValue">CGST Value</th>
										<th data-width="100px" data-name="gst_sgstValue">SGST Value</th>
										<th data-width="100px" data-name="gst_igstValue">IGST Value</th>
										<th data-width="100px" data-name="fact_AcceptingEntity">Accepting Entity</th>
										<th data-width="100px" data-name="fact_AcceptingLoginId">Acceting Login Id</th>
										<th data-width="100px" data-name="fact_AcceptDateTime">Accept Date Time</th>
										<th data-width="100px" data-name="fact_Tenure">Tenor</th>
										<th data-width="100px" data-name="fact_LimitUtilized">Limit Utilized</th>
										<th data-width="100px" data-name="fact_PurSupLimitUtilized">PurSup Limit Utilization</th> 
										<th data-width="100px" data-name="bid_FinancierEntity">Financier Entity</th>
										<th data-width="100px" data-name="bid_FinancierLoginId">Financier Login</th>
										<th data-width="100px" data-name="bid_Rate">Bid Rate</th>
										<th data-width="100px" data-name="bid_Haircut">Haircut</th>
										<th data-width="100px" data-name="bid_ValidTill">ValidTill</th>
										<th data-width="100px" data-name="bid_Status">Bid Stauts</th>
										<th data-width="100px" data-name="bid_Id">Bid ID</th>
										<th data-width="100px" data-name="bid_Timestamp">Time Stamp</th>
										<th data-width="100px" data-name="bid_BidType">Bid Type</th>
										<th data-width="100px" data-name="bid_ProvRate">Bid ProRate</th>
										<th data-width="100px" data-name="bid_ProvHaircut">ProvHaircut</th>
										<th data-width="100px" data-name="bid_ProvValidTill">ProvValidTill</th>
										<th data-width="100px" data-name="bid_ProvBidType">ProvBidType</th>
										<th data-width="100px" data-name="bid_ProvAction">ProvAction</th>
										<th data-width="100px" data-name="bid_AppStatus">Bid App Status</th>
										<th data-width="100px" data-name="bid_LimitUtilised">Bid Limit Utilised</th>
										<th data-width="100px" data-name="bid_BidLimitUtilised">Bid Limit Utilised</th>
										<th data-width="100px" data-name="bid_CostLeg">Cost Leg</th> 
										<th data-width="100px" data-name="inst_GroupId">Group Id</th> 
										<th data-width="100px" data-name="inst_Group">Group Flag</th> 
										<th data-width="100px" data-name="inst_Count">Inst Count</th> 
										<th data-width="100px" data-name="inst_AggregatorEntity">Aggregator</th>
										<th data-width="100px" data-name="inst_IsAggregatorCreated">Created by Aggregator</th>
										
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
			<!-- frmMain -->
		</div>	
		<!-- frmSearch -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-multiselect.js"></script>
	<script type="text/javascript">
		var crudInstrumentReport$ = null,crudInstrumentReport = null;
		var mainForm,searchForm;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(InstReportBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "instreport",
					autoRefresh: true,
					tableConfig: {
						lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
						lengthChange:true,
						pageLength:10,
						createdRow:function( row, data, dataIndex ) {
							if(data.inst_IsAggregatorCreated){
								$(row).addClass('row'+data.inst_IsAggregatorCreated);
							}
						},
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudInstrumentReport$ = $('#instReport').xcrudwrapper(lConfig);
			crudInstrumentReport = crudInstrumentReport$.data('xcrudwrapper');
			mainForm = crudInstrumentReport.options.mainForm;
			searchForm =crudInstrumentReport.options.searchForm;
			
			
			$("#btnDownloadCSV").on("click",function(){
				var lFilter=searchForm.getValue(true);
				lFilter['columnNames'] = crudInstrumentReport.getVisibleColumns();
				downloadFile('instreport/all',null,JSON.stringify(lFilter));
			});
		});
		
	</script>
   	
    </body>
</html>