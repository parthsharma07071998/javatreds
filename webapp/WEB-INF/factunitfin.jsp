<!DOCTYPE html>
<%@page import="com.xlx.treds.master.bean.AuctionChargeSlabBean"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargePlanBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.instrument.bean.FactoringUnitBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.auction.bean.BidBean"%>
<%
String lPurchaser = StringEscapeUtils.escapeHtml(request.getParameter("purchaser"));
String lSelTab = StringEscapeUtils.escapeHtml(request.getParameter("tab"));
%>
<html>
    <head>
        <title>TREDS | Auctions</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
        <style>
		.spinner {
		  width: 95px;
		  margin-right:auto;
		  margin-left:0px;
		}
		.spinner input {
		  text-align: right;
		}
		.input-group-btn-vertical {
		  position: relative;
		  white-space: nowrap;
		  width: 1%;
		  vertical-align: middle;
		  display: table-cell;
		}
		.input-group-btn-vertical > .btn {
		  display: block;
		  float: none;
		  width: 100%;
		  max-width: 100%;
		  padding: 8px;
		  margin-left: 0px;
		  position: relative;
		}
		.input-group-btn-vertical > .btn:first-child {
		  border-top-right-radius: 0px 4px 0px 0px;
		}
		.input-group-btn-vertical > .btn:last-child {
		  margin-top: -2px;
		  border-bottom-right-radius: 0px 0px 4px 0px;
		}
		.input-group-btn-vertical i{
		  position: absolute;
		  top: 0;
		  left: 4px;
		}
		.clsOpen, .clsReserved {
			width:70px;
			height:30px;
			text-align:right;
			padding-top:6px
		}   
		.clsReserved {
			color:red;
		}
		.bg-bid {
			background-color: #f8f8f8;
		}
		.panel .panel-heading {
			height:30px;
		} 
		#mdlAucPlan .modal-dialog  {width:900px;}
        </style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Auctions" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAppUser" style='margin:0px'>
	
		<!-- frmSearch -->
		<div>
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Auctions</h1>
				</div>
			</div>
			<header>
			</header>
			<div>
				<div class="cloudTabs">
					<ul class="cloudtabs nav nav-tabs">
						 <li><a href="#tab0" data-toggle="tab">In Auction <span id="badge0" class="badge bg-yellow"></span></a></li>
						 <li><a href="#tab3" data-toggle="tab">My Bids <span id="badge3" class="badge bg-aqua"></span></a></li>
						 <li><a href="#tab1" data-toggle="tab">My Factored <span id="badge1" class="badge bg-green"></span></a></li>
						 <li><a href="#tab2" data-toggle="tab">Other Factored <span id="badge2" class="badge bg-red"></span></a></li>
						 <li><a href="#tab4" data-toggle="tab">My History <span id="badge4" class="badge bg-green"></span></a></li>
						 <li><a href="#tab5" data-toggle="tab" id="tabForApproval">For Approval <span id="badge5" class="badge bg-green"></span></a></li>
					</ul>
				</div>
			</div>
			<div class="">
				<fieldset>
					<div id="tab0" class="hidden btn-group0">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a class="" href="javascript:;" onClick="javascript:showModal($('#mdlListAll'))"><span class="fa fa-plus"></span> Add Factoring Unit</a>
									<a class="" href="javascript:;" onClick="javascript:removeFromWatch()" id="btnRemoveFromWatch"><span class="fa fa-minus"></span> Remove Factoring Unit</a>
									<a class="secure" href="javascript:;" data-seckey="factunitfin-bid" onClick="javascript:editBidAdv()"><span class="fa fa-plus-square"></span> Place Bids</a>
									<a class="secure" href="javascript:;" data-seckey="factunitfin-bid" onClick="javascript:clearBid(null,$(this))"><span class="fa fa-minus-square"></span> Remove Bids</a>
									<a href="javascript:downloadCSV();" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
									<a href="javascript:;" id=btnViewAucPlan onClick="javascript:getPlan();"><span class="fa fa-eye""></span> View Auction Charge Plan</a>
									<a class="btn-enter" href="javascript:refreshClick();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-11 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">Instruments Details</h3>
							      </div>
							   </div>
							   <div class="col-md-1 col-sm-12 text-right">
							      <div class="panel-heading">All <input type=checkbox id="chkSelectAll"></div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					
					<div id="tab1" class="hidden btn-group1">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a href="javascript:downloadCSV();" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
									<a class="btn-enter" href="javascript:refreshClick();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-12 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">My Factored</h3>
							      </div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					
					<div id="tab2" class="hidden btn-group2">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a href="javascript:downloadCSV();" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
									<a class="btn-enter" href="javascript:refreshClick();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-12 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">Other Factored</h3>
							      </div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					
					<div id="tab3" class="hidden btn-group3">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a href="javascript:downloadCSV();" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
									<a class="btn-enter" href="javascript:refreshClick();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-12 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">My Bids</h3>
							      </div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					<div id="tab4" class="hidden btn-group4">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a href="javascript:;" onClick="javascript:showHistoryFilter();" ><span class="fa fa-filter" id='filter' style='color:black'></span> Filter</a>
									<a href="javascript:downloadCSV();" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
									<a class="btn-enter" href="javascript:refreshClick();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-12 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">My History</h3>
							      </div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					
					<div id="tab5" class="hidden btn-group5">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a class="" href="javascript:;" onclick="javascript:updateAppStatus(null,'<%=BidBean.AppStatus.Approved.getCode() %>',$(this),'Approve')"><span class="fa fa-"></span> Approve</a>
									<a class="" href="javascript:;" onclick="javascript:updateAppStatus(null,'<%=BidBean.AppStatus.Rejected.getCode() %>',$(this),'Reject')"><span class="fa fa-"></span> Reject</a>
									<a class="btn-enter" href="javascript:refreshForApproval();" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
								</span>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<div class="row">
							   <div class="col-md-11 col-sm-12">
							      <div class="panel-heading">
							         <h3 class="panel-title">For Approval</h3>
							      </div>
							   </div>
							   <div class="col-md-1 col-sm-12 text-right">
							      <div class="panel-heading">All <input type=checkbox id="chkSelectAllApp"></div>
							   </div>
							</div>
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
	    
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Auctions</h1>
				</div>
			</div>
			<div class="xform box">
			</div>
		</div>
		<!-- frmMain -->

	</div>

	<div class="modal fade" id="mdlListAll" tabindex=-1><div class="modal-dialog modal-xl"><div class="modal-content">
	<div class="modal-header"><span>Add Factoring Unit</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body"><div class="page-container">
	<div class="content" id="contFactoringUnit">
<!-- frmSearch -->
	<div id="frmSearch">
		<div class="xform tab-pane panel panel-default" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="purchaser"><option value="">Select Buyer</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="supplier"><option value="">Select Seller</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
				</div>
				

				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="amount" class="label">Amount Range:</label></section></div>
							<div class="col-sm-4"><div class="row"><div class="col-sm-6">
								<section class="input">
									<input type="text" id="amount" placeholder="From Amount">
									<b class="tooltip tooltip-top-right"></b></section>
									</div><div class="col-sm-6">
									<section class="input">
									<input type="text" id="filterAmount" placeholder="To Amount">
									<b class="tooltip tooltip-top-right"></b>
								</section>
							</div></div></div>
							
							<div class="col-sm-2"><section><label for="maturityDate" class="label">Maturity Date Range:</label></section></div>
							<div class="col-sm-4"><div class="row"><div class="col-sm-6">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="maturityDate" placeholder="From Maturity Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
								</div><div class="col-sm-6">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="filterMaturityDate" placeholder="To Maturity Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div></div></div>
							
							
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="filterSellerCategory" class="label">Seller Category:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="filterSellerCategory"><option value="">Select Seller Category</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
							<div class="col-sm-2"><section><label for="filterMsmeStatus" class="label">MSME Status:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="filterMsmeStatus"><option value="">Select MSME Status</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="" class="label">Tenor Range:</label></section></div>
							<div class="col-sm-4"><div class="row"><div class="col-sm-6">
								<section class="input">
								<input type="text" id="filterFromTenure" placeholder="From Tenor">
								<b class="tooltip tooltip-top-right"></b></section>
								</div><div class="col-sm-6">
								<section class="input">
								<input type="text" id="filterToTenure" placeholder="To Tenor">
								<b class="tooltip tooltip-top-right"></b></section>
							</div></div></div>
							<div class="col-sm-2"><section><label for="capRate" class="label">Cap Rate % Range:</label></section></div>
							<div class="col-sm-4"><div class="row"><div class="col-sm-6">
								<section class="input">
								<input type="text" id="capRate" placeholder="From CapRate%">
								<b class="tooltip tooltip-top-right"></b></section>
								</div><div class="col-sm-6">
								<section class="input">
								<input type="text" id="filterToCapRate" placeholder="To CapRate%">
								<b class="tooltip tooltip-top-right"></b></section>
							</div></div></div>
						</div>
					</div>
				</div>
				<div class="panel-body bg_white">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<input type=checkbox id="chkShowAll" value="N" style="display:none"/>
								<button type="button" class="btn btn-info btn-lg" id="btnAddToWatch"><span class="glyphicon glyphicon-plus"></span> Add to Watch</button>
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
		 						<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
		 						<a></a>
							</div>
						</div>
					</div>
			</div>
			</fieldset>
		</div>
	</div>
	<!-- frmSearch -->
		<div class="cloudTabs" style='margin-left:30px;' id="buyersTab">
			<ul class="cloudtabs nav nav-tabs">
				 <li><a href="#tab100" data-toggle="tab">My Buyers<span id="badge100" class="badge bg-green"></span></a></li>
				 <li class="active"><a href="#tab101" data-toggle="tab">All Buyers<span id="badge101" class="badge bg-yellow"></span></a></li>
			</ul>
		</div>
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " data-selector="multiple" id="tblData" width="100%">
							<thead><tr>
								<th data-width="160px" data-name="id">ID</th>
								<!-- <th data-width="100px" data-name="id">Id</th> -->
								<th data-width="100px" data-name="maturityDate">Maturity Date</th>
								<th data-width="110px" data-name="purName">Buyer</th>
								<th data-width="110px" data-name="supName">Seller</th>
								<th data-width="100px" data-name="amount">Amount</th>
								<th data-width="100px" data-name="tenure">Bal. Tenure</th>
								<th data-width="100px" data-name="acceptedHaircut">Cap.Rate %</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-width="100px" data-name="filterSellerCategory">Seller Category</th>
								<th data-width="100px" data-name="filterMsmeStatus">MSME Status</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
	</div></div></div></div></div></div>
	
	<div class="modal fade" id="mdlDepth" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
	</div></div></div>
	
	<div class="modal fade" id="mdlEditAdv" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Modify Bid</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmBid">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="rate" class="label">Rate %:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="rate" placeholder="Rate">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="rate" class="label">Retention Margin %:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="haircut" placeholder="Retention Margin %">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="validTill" class="label">Valid Till:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="validTill" placeholder="Valid Till" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-4"><section><label for="bidType" class="label">Bid Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
							<select id="bidType"><option value="">Select
									Bid Type</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
						</section>
						<section class="view"></section>
					</div>
				</div>
				
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Submit</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>

	<div class="modal fade" id="mdlHistoryFilter" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;History Filter</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmHistoryFilter">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="id" class="label">Factoring Unit:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="id" placeholder="Factoring Unit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="maturityDate" class="label">Start Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="maturityDate" placeholder="Start Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="filterMaturityDate" class="label">End Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="filterMaturityDate" placeholder="End Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="type" class="label">Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-4"><section><label for="txnType" class="label">Txn Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="txnType"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-4"><section><label for="pfId" class="label">Pay File Id:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="pfId" placeholder="Pay File Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnFilter><span class="fa fa-save"></span> Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg" id=btnHistFilterClr>Clear Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>
	
	
	<div class="modal fade" tabindex=-1 id="mdlClubbedInvoice"><div class="modal-dialog  modal-lg"><div class="modal-content">
	</div></div></div>
	
	<div class="modal fade" id="mdlAucPlan" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
	</div></div></div>
	<div class="modal fade" tabindex=-1 id="mdlBankDetail"><div class="modal-dialog  modal-lg"><div class="modal-content">
	</div></div></div>
	<div class="modal fade" tabindex=-1 id="mdlPurchaserRating"><div class="modal-dialog  modal-lg"><div class="modal-content">
	</div></div></div>

   	<%@include file="footer1.jsp" %>
   	<%@include file="pluginaucsumm.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.xtemplatetable.js"></script>
   	<script id="tpl0" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-11 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-3">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header ">TENOR</td>
                     <td class="table-content">{{unit.tenure}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BEST BID</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}
{{#ifHasAccess "factunitfin-depth"}}<button class="btn btn-default btn-sm" onClick="javascript:showDepth('{{unit.id}}')" title="Depth"><span class="fa  fa-chevron-down"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}} in {{bid.costLegDesc}}</td>
                  </tr>
				  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>		
				<tr>
                     <td class="table-header">BANK DETAILS</td>
					 <td class="table-content"><a href="javascript:viewBankDetails('{{unit.purBankName}}','{{unit.purIfsc}}','{{unit.purAccNo}}','{{unit.supBankName}}','{{unit.supIfsc}}','{{unit.supAccNo}}', {{#if unit.purDesignatedBankFlag}}'true'{{else}}'false'{{/if}},{{#if unit.supDesignatedBankFlag}}'true'{{else}}'false'{{/if}});" style="color:blue !important">Click Here</a></td>
                  </tr>
				<tr>
                     <td class="table-header">BUYER RATINGS</td>
					 <td class="table-content"><a href="javascript:purchaserRating('{{unit.purchaser}}');" style="color:blue !important">Click Here</a></td>
                  </tr>
				<tr>
                     <td class="table-header">B/S RELATIONSHIP</td>
					 <td class="table-content">{{unit.relationship}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-5">
            <table class="table bg-bid">
               <tbody>
                  <tr>
                     <td class="table-header">MY BID</td>
                     <td class="table-content">
<div id="vw-{{unit.id}}">
	<div class="cls{{bid.bidTypeDesc}}">{{#formatDec}}{{bid.rate}}{{/formatDec}}</div>
{{#if bid.hasBid}}
<div class="btn-group">
{{#if owner}}
	{{#ifHasAccess "factunitfin-bid"}}<button class="btn btn-success btn-sm" onClick="javascript:editBid('{{unit.id}}')" title="Modify"><span class="fa fa-pencil">
</button>{{/ifHasAccess}}{{#ifHasAccess "factunitfin-bid"}}<button class="btn btn-danger btn-sm" onClick="javascript:clearBid('{{unit.id}}', $(this))" title="Remove"><span class="fa fa-minus"></button>{{/ifHasAccess}}
{{/if}}
{{else}}

<div class="input-group-btn">
{{#if owner}}
	{{#ifHasAccess "factunitfin-bid"}}<button class="btn btn-success btn-sm" onClick="javascript:editBid('{{unit.id}}')" title="New Bid"><span class="fa fa-plus"></span> Bid</button>{{/ifHasAccess}}
{{/if}}
{{/if}}
{{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{bid.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
</div>
</div>

<div id="ed-{{unit.id}}" class="hidden">
	<div class="input-group spinner">
	<input type="text" value="{{#formatDec}}{{bid.rate}}{{/formatDec}}" class="form-control" style="height:30px"/>
	<div class="btn-group-vertical input-group-btn-vertical" role="group" aria-label="...">
		<button class="btn btn-default btn-sm" type="button" onClick="javascript:chgPr('{{unit.id}}',0.01)" style="padding:7px"><i class="fa fa-caret-up"></i></button>
		<button class="btn btn-default btn-sm" type="button" onClick="javascript:chgPr('{{unit.id}}',-0.01)" style="padding:7px"><i class="fa fa-caret-down"></i></button>
	</div>
	</div>
	<div class="btn-group">
		<button class="btn btn-success btn-sm" onClick="javascript:toggleBidType('{{unit.id}}', $(this))" title="Bid Type" id='bidType' style='display:none;'>O</button>
		<button class="btn btn-success btn-sm" onClick="javascript:saveBid('{{unit.id}}', $(this))" title="Submit"><span class="fa fa-save"></button>
		<button class="btn btn-default btn-sm" onClick="javascript:editBidAdv('{{unit.id}}')" title="Advance"><span class="fa fa-external-link"></button>
		<button class="btn btn-default btn-sm" onClick="javascript:viewBid('{{unit.id}}')" title="Cancel"><span class="fa fa-remove"></button>
	</div>
</div>

</div>
</div>
                     </td>
                     <td class="table-content"><div class="cls{{bid.provBidTypeDesc}}">{{#formatDec}}{{bid.provRate}}{{/formatDec}}&nbsp;</div>{{bid.provActionDesc}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{bid.haircut}}{{/formatDec}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">VALID TILL</td>
                     <td class="table-content">{{#if bid.rate}}{{#if bid.validTill}}{{bid.validTill}}{{else}}Good Till Auctioned{{/if}}{{/if}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provValidTill}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BID TYPE</td>
                     <td class="table-content">{{bid.bidTypeDesc}}</td>
                     <td class="table-content">{{bid.provBidTypeDesc}}</td>
                  </tr>
				{{#if financierBearShare}}
				  <tr>
                     <td class="table-header">PLATFORM CHARGES</td>
					 <td class="table-content">{{financierShare}}</td>
                  </tr>
				{{/if}}
               </tbody>
            </table>
         </div>
      </div>
   </div>
   <div class="col-md-1 action_links col-sm-12 text-right">
      <input type=checkbox class="chkRow" value="{{unit.id}}">
   </div>
</div>
{{/each}}
<p></p>
<p>
<b>Authorisation to RXIL:</b> In the event the bid is accepted and settled, RXIL is authorized by Assignor to send the Notice of assignment to Assignee.
</p>
	</script>
   	<script id="tpl1" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-12 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">INTEREST</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{bid.cost}}{{/formatDec}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}}</td>
                  </tr>
				  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ACCEPTED RATE</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}  {{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{unit.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{unit.acceptedHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">&nbsp;</td>
                     <td class="table-content">&nbsp;</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
{{/each}}
	</script>
   	<script id="tpl2" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-12 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}}</td>
                  </tr>
				  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ACCEPTED RATE</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}  {{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{bid.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{unit.acceptedHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">&nbsp;</td>
                     <td class="table-content">&nbsp;</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
{{/each}}
	</script>
   	<script id="tpl3" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-12 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BEST BID</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}
{{#ifHasAccess "factunitfin-depth"}}<button class="btn btn-default btn-sm" onClick="javascript:showDepth('{{unit.id}}')" title="Depth"><span class="fa  fa-chevron-down"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}} in {{bid.costLegDesc}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">Status</td>
                     <td class="table-content">{{unit.statusDesc}}</td>
                  </tr>
				  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>	
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table bg-bid">
               <tbody>
                  <tr>
                     <td class="table-header">MY BID</td>
                     <td class="table-content cls{{bid.bidTypeDesc}}">
{{#formatDec}}{{bid.rate}}{{/formatDec}}
{{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{bid.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
                     </td>
                     <td class="table-content">{{#formatDec}}{{bid.provRate}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{bid.haircut}}{{/formatDec}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">VALID TILL</td>
                     <td class="table-content">{{#if bid.rate}}{{#if bid.validTill}}{{bid.validTill}}{{else}}Good Till Auctioned{{/if}}{{/if}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provValidTill}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BID TYPE</td>
                     <td class="table-content">{{bid.bidTypeDesc}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provBidTypeDesc}}{{/formatDec}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
{{/each}}
	</script>

   	<script id="tpl4" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-12 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">INTEREST</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{bid.cost}}{{/formatDec}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}}</td>
                  </tr>

				  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>


               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ACCEPTED RATE</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}  {{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{bid.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{unit.acceptedHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">&nbsp;</td>
                     <td class="table-content">&nbsp;</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
{{/each}}
	</script>
	
   	<script id="tpl5" type="text/x-handlebars-template">
{{#each this}}
<div class="row table-row">
   <!-- Table Row 1 -->
   <div class="col-md-11 col-sm-12">
      <div class="row horiz_table table-row">
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">ID</td>
                     <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{unit.id}}')" title="View Factoring Unit"{{/ifHasAccess}} style="color:blue !important">{{unit.id}}</a></td>
                  </tr>
                  <tr>
                     <td class="table-header ">DUE DATE</td>
					{{#ifCond unit.enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.extendedDueDate}}</td>
					{{/ifCond}}
					{{#ifCond unit.enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
                     <td class="table-content">{{unit.maturityDate}}</td>
					{{/ifCond}}
                  </tr>
                  <tr>
                     <td class="table-header">AMOUNT</td>
                     <td class="table-content"><span class="fa fa-cust{{unit.currency}}"></span> {{#formatDec}}{{unit.amount}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BEST BID</td>
                     <td class="table-content">
{{#formatDec}}{{unit.acceptedRate}}{{/formatDec}}
{{#ifHasAccess "factunitfin-depth"}}<button class="btn btn-default btn-sm" onClick="javascript:showDepth('{{unit.id}}')" title="Depth"><span class="fa  fa-chevron-down"></button>{{/ifHasAccess}}
                     </td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr id="financierName" class="state-T">
                     <td class="table-header">FINANCIER</td>
                     <td class="table-content">{{bid.financierName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BUYER</td>
                     <td class="table-content">{{unit.purName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">SELLER</td>
                     <td class="table-content">{{unit.supName}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">COST BEARER</td>
                     <td class="table-content">{{unit.costBearer}} in {{bid.costLegDesc}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">Status</td>
                     <td class="table-content">{{unit.statusDesc}}</td>
                  </tr>
		  <tr>
                     <td class="table-header">INST COUNT</td>
					 <td class="table-content"><a {{#ifHasAccess "instview-fu"}}href="javascript:viewIN('{{unit.inId}}')" title="View Instrument"{{/ifHasAccess}} style="color:blue !important">{{unit.instCount}}</a></td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div class="col-md-4">
            <table class="table">
               <tbody>
                  <tr>
                     <td class="table-header">MY BID</td>
                     <td class="table-content cls{{bid.bidTypeDesc}}">
{{#formatDec}}{{bid.rate}}{{/formatDec}}
{{#ifHasAccess "instview-bidlog"}}<button class="btn btn-default btn-sm" onClick="javascript:viewBidLog('{{unit.id}}','{{bid.financierEntity}}')" title="Bid Log"><span class="fa fa-list"></button>{{/ifHasAccess}}
                     </td>
                     <td class="table-content">{{#formatDec}}{{bid.provRate}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">RET. MARGIN %</td>
                     <td class="table-content">{{#formatDec}}{{bid.haircut}}{{/formatDec}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provHaircut}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">VALID TILL</td>
                     <td class="table-content">{{#if bid.rate}}{{#if bid.validTill}}{{bid.validTill}}{{else}}Good Till Auctioned{{/if}}{{/if}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provValidTill}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header">BID TYPE</td>
                     <td class="table-content">{{bid.bidTypeDesc}}</td>
                     <td class="table-content">{{#formatDec}}{{bid.provBidTypeDesc}}{{/formatDec}}</td>
                  </tr>
                  <tr>
                     <td class="table-header"></td>
                     <td class="table-content"></td>
                     <td class="table-content">{{bid.provActionDesc}}</td>
                  </tr>
               </tbody>
            </table>
         </div>
      </div>
   </div>
   <div class="col-md-1 action_links col-sm-12 text-right">
      <input type=checkbox class="chkRow" value="{{unit.id}}">
   </div>
</div>
{{/each}}
	</script>
	
	<script id="tplClubbedInstruments" type="text/x-handlebars-template">
		<div class="modal-header">Details of clubbed instruments <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="instClub"><thead>
				<tr>
					<th width=125px >ID </th>
					<th > Invoice No. </th>
					<th >Invoice Amount </th>
					<th >Cash Discount Amount</th>
					<th> Adj Amount</th>
					<th> TDS Amount</th>
					<th>Net Amount</th>
					<th width=90px > </th>
				</tr></thead>
				<tbody>
{{#if splitlist}}
		{{#each splitlist}}
				<tr>
					<td>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td><button type="button" id='btnViewInst' onclick="viewClubbedInstrument({{id}})">View</button></td>
				</tr>
		{{/each}}
{{/if}}
{{#if inst}}
		{{#each inst}}
				<tr>
					<td>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td></td>
				</tr>
		{{/each}}
{{/if}}
				</tbody>
				</table>
			</div>
		</div>
		{{#if paymentAdvise}}
		<div class="row">
		<h3><b><center>CV Number : {{cvNumber}}</center></b></h3>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table"><thead>
				<tr>
					<th>Vendor Invoice Number</th>
					<th>Invoice Date</th>
					<th>Inv Amount (Rs.)</th>
					<th>FI Document No Details</th>
					<th>Code</th>
					<th>Debit/Credit Amount(Rs.)</th>
				</tr></thead>
				<tbody>
		{{#each paymentAdvise}}
				<tr>
					<td>{{inv_num}}</td>
					<td>{{inv_date}}</td>
					<td>{{amount}}</td>
					<td>{{document_number}}</td>
					<td>{{deduction_reason}}</td>
					<td>{{dedudcted_amount}}</td>
					<td></td>
				</tr>
		{{/each}}
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>
	{{/if}}

	</script>


	<script id="tplBankDetails" type="text/x-handlebars-template">
		<div class="modal-header">Bank Details<button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="instClub"><thead>
				<tr>
					<th  >Entity </th>
					<th >Bank Name </th>
					<th > Bank IFSC </th>
					<th >Bank Account No </th>
				</tr></thead>
				<tbody>
				<tr>
					<td>Purchaser</td>
					<td>{{purBankName}}</td>
					<td>{{purIfsc}}</td>
					<td>{{purAccNo}}</td>
				</tr>
				<tr>
					<td>Supplier</td>
					<td>{{supBankName}}</td>
					<td>{{supIfsc}}</td>
					<td>{{supAccNo}}</td>
				</tr>
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>

	</script>
	
	<script id="tplPurchaserRatings" type="text/x-handlebars-template">
		<div class="modal-header">Buyer Rating<button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="instClub"><thead>
				<tr>
					<th >Rating Agency </th>
					<th >Rating </th>
					<th>Rating Date</th>
					<th > Expiry Date</th>
					<th>Rating Type</th>
					<th>Status</th>
					<th>View Uploaded Docs</th>
				</tr></thead>
				<tbody>
				{{#each this}}
				<tr>
					<td>{{ratingAgency}}</td>
					<td>{{rating}}</td>
					<td>{{ratingDate}}</td>
					<td>{{expiryDate}}</td>
					<td>{{ratingType}}</td>
					<td>{{status}}</td>
					<td><a href="upload/BUYERCREDITRATINGS/{{ratingFile}}" style="color:blue !important">{{ratingFile}}</a></td>
				</tr>
				{{/each}}
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>
	</script>
	
   	<script id="tplMessage" type="text/x-handlebars-template">
<table class="table table-bordered">
<tbody><tr>
    <th style="width:140px">Id</th>
    <th>Message</th>
</tr>
{{#each this}}
<tr>
    <td>{{id}}</td>
    <td>{{#if error}}<span class="fa fa-remove text-danger"></span> {{error}}{{else}}<span class="fa fa-check-circle text-success"></span> Done {{#if message}}{{message}}{{/if}}{{/if}}</td>
</tr>
{{/each}}
</tbody></table>
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
   	<script id="tplDepth" type="text/x-handlebars-template">
<div class="modal-header"><span>{{fuId}}</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
<table class="table table-hover"><thead>
<tr>
<th></th>
<th style="text-align:right">Rate</th>
<th style="text-align:right">Bid Type</th>
<th style="text-align:right">Cost Leg</th>
<th style="text-align:right">Retention Margin %</th>
<th>Time</th>
</tr>
</thead><tbody>
{{#each depth}}
<tr>
	<td style="width:16px">{{#if own}}<span class="fa fa-star text-red"></span>{{/if}}</td>
    <td style="text-align:right">{{#formatDec}}{{rate}}{{/formatDec}}</td>
    <td style="text-align:right">{{bidType}}</td>
    <td style="text-align:right">{{costLeg}}</td>
    <td style="text-align:right">{{#formatDec}}{{haircut}}{{/formatDec}}</td>
    <td>{{timestamp}}</td>
</tr>
{{/each}}
</tbody></table>
<div class="row"><div class="col-sm-12">
<div class="btn-group pull-right">
<button type="button" class="btn btn-info btn-lg btn-close" data-dismiss="modal">OK</button>
</div>
</div></div>
</fieldset>
</div>
	</script>
	<script type="text/javascript">
	var allData,index;
	var templates,tplMessage,tplDepth,tplBankDetails,tplPurchaserRatings;
	var crudFactoringUnit$ = null, crudFactoringUnit=null;
	var bidForm$ = null, bidForm = null;
	var tplClubbedInstruments;
	var editAdvIds = null;// temporary variable to store bidids when edit advance
	var HISTORY_INDEX = 4;
	var histFilterForm$ = null, histFilterForm = null;
	var strHistoryFilters=null;
	var historyFilters = null;
	var historyData = null;
	var platform = false;
	$(document).ready(function() {
		templates=[];
		for (var lPtr=0;lPtr<6;lPtr++) {
			var lTableConfig={
				template:Handlebars.compile($('#tpl'+lPtr).html()),
				sortCols:[{text:"ID", value:"unit.id"},{text:"Buyer", value:"unit.purName"},
				          {text:"Seller", value:"unit.supName"},{text:"Cost Bearer", value:"unit.costBearer"},
				          {text:"Amount", value:"unit.amount"},{text:"My Bid", value:"bid.rate"},
				          ]
			};
			var lTplTable$=$('#tab'+lPtr).xtemplatetable(lTableConfig);
			templates[lPtr]=lTplTable$.data('xtemplatetable');
		}
		tplMessage = Handlebars.compile($('#tplMessage').html());
		tplDepth = Handlebars.compile($('#tplDepth').html());
		tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html());
		tplBankDetails=Handlebars.compile($('#tplBankDetails').html());
		tplPurchaserRatings = Handlebars.compile($('#tplPurchaserRatings').html());
		tplAPSView = Handlebars.compile($('#tplAucPlanSlabView').html());
		$('#btnAddToWatch').on('click', function() {
			var lRows=crudFactoringUnit.getSelectedRows();
			if (lRows==null || lRows.length==0) {
				alert("Please select one or more factoring units using check boxes.");
				return;
			}
			var lData = [];
			$.each(lRows, function(pIndex,pValue) {
				lData.push(pValue.data().id);
			});
			$('#btnAddToWatch').prop('disabled',true);
			$.ajax( {
		        url: "factunitfin/addwatch",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
	            	var lMessage = tplMessage(pObj);
	            	alert(lMessage);
	            	$.each(pObj,function(pIndex,pValue){
	            		if (pValue.data != null)
	            			mergeData(pValue.data);
	            	});
	            	showData();
					$('#mdlListAll').modal('hide');
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#btnAddToWatch').prop('disabled',false);
		        }
			});
		});
		if (loginData.secKeys['factunitfin-bid-check']==null)
			$('#contAppUser .nav-tabs #tabForApproval').hide();
		$('#contAppUser .nav-tabs a').on('shown.bs.tab', function(event){
		    var lRef1 = $(event.target).attr('href');         // active tab
		    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
		    var lTabIdx = parseInt(lRef1.substring(4));

		    if (lRef2)
		    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
		    $('.btn-group'+lTabIdx).removeClass('hidden');
		    var lIdVal = lRef1.substring(4);
		    if (lIdVal==5) {
	    		refreshForApproval();
	    	}
		    activeTab = lRef1;
		});
		$('#buyersTab .nav-tabs a').on('shown.bs.tab', function(event){
		    var lRef1 = $(event.target).attr('href');         // active tab
		    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
		    var lTabIdx = parseInt(lRef1.substring(4));

		    if (lRef2)
		    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
		    $('.btn-group'+lTabIdx).removeClass('hidden');
		    var lIdVal = lRef1.substring(4);
		    if(lIdVal>=100) {
		    	$('#chkShowAll').prop('checked',(lIdVal==101?true:false));
		    	if(lRef2) $('#btnSearch').click();	    		
	    	}
		});
		$('#btnSave').on('click', function(){
			var lErrors = bidForm.check();
			if ((lErrors != null) && (lErrors.length > 0)) {
				var lResp = appendError(bidForm.fields, true);
				alert(lResp[0] , "Validation Failed");
				return false;
			}
			var lData=bidForm.getValue();
			lData.ids=editAdvIds;
			updateBid(lData, $('#btnSave'));
		});
		$('#mdlListAll').on('shown.bs.modal', function(){
			if("0" == '<%=lSelTab%>')
				showTab('tab100');
			else if("1" == '<%=lSelTab%>')
				showTab('tab101');
			else
				showTab('tab100');
		});
		$('#chkSelectAll').change(function() {
			var lThis$ = $(this);
			var lChecked = lThis$.prop("checked")
			lThis$.parents('.tab-pane').find('.chkRow').prop('checked', lChecked);
		});
		$('#chkSelectAllApp').change(function() {
			var lThis$ = $(this);
			var lChecked = lThis$.prop("checked")
			lThis$.parents('.tab-pane').find('.chkRow').prop('checked', lChecked);
		});

		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "factunitfin",
				preSearchHandler:function(pFilter) {
					if ($('#chkShowAll').prop('checked'))
						pFilter.showall='Y';
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudFactoringUnit$ = $('#contFactoringUnit').xcrudwrapper(lConfig);
		crudFactoringUnit = crudFactoringUnit$.data('xcrudwrapper');
		
		var lBidConfig = {
			"fields": [
	           {
	               "dataType": "DECIMAL",
	               "decimalLength": 2,
	               "name": "rate",
	               "label": "Rate %",
	               "integerLength": 3,
		   			"maxValue":100,
					"minValue":0,
	               "pattern": "^[a-zA-Z0-9]*$"
	           },
	           {
	               "dataType": "DECIMAL",
	               "decimalLength": 2,
	               "name": "haircut",
	               "label": "Retention Margin %",
	               "integerLength": 3,
		   			"maxValue":100,
					"minValue":0,
	               "pattern": "^[a-zA-Z0-9]*$"
	           },
	           {
	               "dataType": "DATE",
	               "name": "validTill",
	               "label": "Valid Till"
	           },
				{
					"name":"bidType",
					"label":"Bid Type",
					"dataType":"STRING",
					"maxLength": 3,
					"notNull": true,
					"dataSetType": "STATIC",
					"dataSetValues": [{"text":"Reserved", "value":"RES"},{"text":"Open", "value":"OPN"}]
				}
			]
		};
		bidForm$ = $('#frmBid').xform(lBidConfig);
		bidForm = bidForm$.data('xform');
		
		<%
		String lTab = StringEscapeUtils.escapeHtml(request.getParameter("t"));
		if (lTab==null) lTab="0";
		%>
		$('#contAppUser li:eq(<%=lTab%>) a').tab('show');			
		getData(true);
		
		var lHistConfig = {
			"fields": [
					{
						"name": "id",
						"fieldType":"PRIMARY",
						"dataType":"INTEGER",
					},
					{
						"name":"purchaser",
						"label":"Buyer",
						"dataType":"STRING",
						"maxLength": 30,
						"dataSetType":"RESOURCE",
						"dataSetValues":"finaucset/purchasers"
					},
					{
						"name":"supplier",
						"label":"Seller",
						"dataType":"STRING",
						"maxLength": 30,
						"dataSetType":"RESOURCE",
						"dataSetValues":""
					},
					{
						"name":"maturityDate",
						"label":"Obligation Start Date",
						"dataType":"DATE"
					},
					{
						"name":"filterMaturityDate",
						"label":"Obligation End Date",
						"dataType":"DATE"
					},
			   		{
						"name":"txnType",
						"label":"Transaction Type",
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Debit", "value":"D"},{"text":"Credit", "value":"C"}],
						"desc":"Type of transaction, whether Debit/Credit."
					},
					{
						"name":"type",
						"label":"Type",
						"dataType":"STRING",
						"maxLength": 3,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Leg 1", "value":"L1"},{"text":"Leg 2", "value":"L2"},{"text":"Leg 3", "value":"L3"}],
						"desc":"The Leg of the obligation transaction."
					},
					{
						"name":"status",
						"label":"Status",
						"dataType":"STRING",
						"maxLength": 3,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Created", "value":"CRT"},{"text":"Ready", "value":"RDY"},{"text":"Sent", "value":"SNT"},{"text":"Success", "value":"SUC"},{"text":"Failed", "value":"FL"},{"text":"Cancelled", "value":"CNL"}],
						"desc":"Current status of obligation."
					},
					{
						"name":"pfId",
						"label":"Pay File Id",
						"dataType":"INTEGER",
						"desc":"Internal id of the pay file uploaded."
					}
			]
		};
		histFilterForm$ = $('#frmHistoryFilter').xform(lHistConfig);
		histFilterForm = histFilterForm$.data('xform');

		$('#btnFilter').on('click', function() {
			var lHistoryFilters = histFilterForm.getValue(true);
			strHistoryFilters = JSON.stringify(lHistoryFilters);
			if(strHistoryFilters!=null&&strHistoryFilters.length>2){
				historyFilters=lHistoryFilters;
			}
			getHistoryData();
        	$('#mdlHistoryFilter').modal('hide');
		});
		
		$('#frmHistoryFilter #purchaser').on('change', function() {
			populateSupplier(histFilterForm);
		});
		$('#btnHistFilterClr').on('click', function() {
			histFilterForm.setValue(null);
			$('#filter').attr('style','color:black');
		});
		
		
		
		
		platform = (loginData.domain == "<%=AppConstants.DOMAIN_PLATFORM%>");

	});
	
	function getData(pOnLoad) {
		$('#btnRefresh').prop('disabled',true);
		$.ajax( {
	        url: "factunitfin/watch",
	        type: "POST",
	        success: function( pObj, pStatus, pXhr) {
	        	allData=pObj;
	        	index={};
	        	$.each(allData, function(pIndex, pValue) {
	        		index[pValue.unit.id] = pIndex;
	        	});
	        	if(allData!=null && allData.length == HISTORY_INDEX){
	        		// add for history
	        		allData[HISTORY_INDEX] = historyData; 
		        	index[HISTORY_INDEX] = HISTORY_INDEX;
	        	}else if (historyData!=null && allData.length==0){
	        		for(var lPtr=0; lPtr<HISTORY_INDEX; lPtr++){
		        		allData.push(null);
	        		}
	        		allData[HISTORY_INDEX] = historyData; 
		        	index[HISTORY_INDEX] = HISTORY_INDEX;
	        	}	        	
	        	showData();
<%
if (lPurchaser != null) {
%>	        	if (pOnLoad) {
					crudFactoringUnit.options.searchForm.setValue({purchaser:"<%=lPurchaser%>"});
					$('#chkShowAll').prop('checked',true);
					javascript:showModal($('#mdlListAll'));
					setTimeout(crudFactoringUnit.searchHandler,200)
	        	}
<%
}
%>
	        },
	        error: errorHandler,
	        complete: function() {
	        	$('#btnRefresh').prop('disabled',false);
	        }
		});
	}
	function showData() {
    	var lTabData=[];
		for (var lPtr=0;lPtr<templates.length;lPtr++)
			lTabData[lPtr]=[];
    	$.each(allData,function(pIndex,pValue) {
			if (pValue !=null && pValue.tab != null && pValue.tab != HISTORY_INDEX){
				if ((pValue.bid.rate!=null) || (pValue.bid.provRate!=null))
					pValue.bid.hasBid=true;
	    		lTabData[pValue.tab].push(pValue);
	    		if ((pValue.bid.hasBid)&&(pValue.owner)&&(pValue.unit.status=='ACT'))
	    			lTabData[3].push(pValue);
			}
    	});
		for (var lPtr=0;lPtr<templates.length;lPtr++) {
			//$('#tab'+lPtr).html(templates[lPtr](lTabData[lPtr]));
			templates[lPtr].setData(lTabData[lPtr]);
			var lCount = lTabData[lPtr]?lTabData[lPtr].length:0;
			$('#badge'+lPtr).html(lCount<10?"0"+lCount:lCount);
		}
		$('#chkSelectAll').prop('checked', false);
		lOptions = {"name":"rate","label":"Rate","dataType":"DECIMAL","integerLength":3,"decimalLength":2};
    	$.each(allData,function(pIndex,pValue) {
    		if (pValue!=null && pValue.unit.status=='ACT') {
    			var lField$ = $('#ed-'+pValue.unit.id+' input').xformfield(lOptions);
    			pValue.rateField = lField$.data('xformfield');
    		}
    	});
    	refreshState(); //this is done for Treds Admin page to display Financier name.
	}

	function getHistoryData() {
		if(strHistoryFilters!=null&&strHistoryFilters.length>2){
			$('#btnRefresh').prop('disabled',true);
			$.ajax( {
		        url: "factunitfin/history",
		        type: "POST",
		        data: strHistoryFilters,
		        success: function( pObj, pStatus, pXhr) {
		        	if(pObj!=null&&pObj.length>0){
			        	historyData=pObj;
		        	}else{
		        		historyData=null;
		        	}
		        	if(allData.length==0){
		        		for(var lPtr=0; lPtr<HISTORY_INDEX; lPtr++){
			        		allData.push(null);
		        		}
		        	}
		        	allData[HISTORY_INDEX]=historyData;
					historyFilters = histFilterForm.getValue(true); //save the valid filter for refresh button
		        	showHistoryData();
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#btnRefresh').prop('disabled',false);
		        }
			});
		}else{
			alert("Please provide at least one filter for searching old Factored Units.");
		}		
	}
	function showHistoryData() {
    	var lTabData=allData;
    	var lPtr=HISTORY_INDEX;
		//$('#tab'+lPtr).html(templates[lPtr](lTabData[lPtr]));
		templates[lPtr].setData(lTabData[lPtr]);
		var lCount = lTabData[lPtr]?lTabData[lPtr].length:0;
		$('#badge'+lPtr).html(lCount<10?"0"+lCount:lCount);

		if(historyFilters!=null&&Object.keys(historyFilters).length>1){
			$('#filter').attr('style','color:red');
		}else{
			$('#filter').attr('style','color:black');
		}
		refreshState();
 	}
	
	function removeFromWatch() {
		var lIds=getIds(null, 0);
		if (lIds==null) {
			return;
		}
		$('#btnRemoveFromWatch').prop('disabled',true);
		$.ajax( {
	        url: "factunitfin/removewatch",
	        type: "POST",
	        data:JSON.stringify(lIds),
	        success: function( pObj, pStatus, pXhr) {
            	$.each(pObj,function(pIndex,pValue){
            		delete index[pValue];
            	});
            	for (var lPtr=allData.length-1;lPtr>=0;lPtr--) {
            		if (index[allData[lPtr].unit.id] == null)
            			allData.splice(lPtr,1);
            	}
            	showData();
	        },
	        error: errorHandler,
	        complete: function() {
	        	$('#btnRemoveFromWatch').prop('disabled',false);
				$('#chkSelectAll').prop('checked',false);
	        }
		});
	}	
	function mergeData(pRow) {
		var lIdx = index[pRow.unit.id];
		if (lIdx == null) {
			allData.push(pRow);
			index[pRow.unit.id] = allData.length-1;
		} else {
			allData.splice(lIdx, 1, pRow);
		}
	}
	function getIds(pId, pTabIdx) {
		if (pId == null) {
			var lIds = [];
			$("#tab" + pTabIdx + " .chkRow:checked").each(function() {
				lIds.push($(this).val());
			});
			if (lIds.length==0) {
				alert("Please select one or more factoring units using check boxes.");
				return null;
			}
			return lIds;
		} else if ($.isArray(pId))
			return pId;
		else 
			return [pId];
	}
	function editBid(pId) {
		$('#ed-'+pId).removeClass("hidden");
		$('#vw-'+pId).addClass("hidden");
		$('#ed-'+pId+' input').focus();
		var lIdx=index[pId];
		var lBidType = null;
		if (lIdx != null){
			lBidType = allData[lIdx].bid.bidType;
		}
		setBidTypeDisplay(lBidType);
	}
	function viewFU(pId) {
		showRemote('fuview?fuid='+pId+'&fin=Y', "modal-xl", false, pId);
	}
	function viewIN(pId) {
		var lUrl= 'factunitfin/viewclubbeddetails/' + pId  ;
			$.ajax({
	        url: lUrl,
	        type: 'GET',
	        success: function( pObj, pStatus, pXhr) { 
	        	$('#mdlClubbedInvoice .modal-content').html(tplClubbedInstruments(pObj));
	        	showModal($('#mdlClubbedInvoice'));
	        },
	    	error: errorHandler
	    	});
		//showRemote('instview?id='+pId, 'modal-xl', true,'Instrument Details');
	}
	function viewBidLog(pId,pFinancier) {
		showRemote('bidlogview?fuid='+pId+'&financierEntity='+pFinancier, "modal-xl", false, "Bid Log");
	}	
	function editBidAdv(pId) {
		var lIds=getIds(pId, 0);
		if (lIds==null) 
			return;
		var lRate=null,lHaircut=null,lValidTill=null,lBidType=null;
		editAdvIds = lIds;
		if (lIds.length==1) {
			var lIdx=index[lIds[0]];
			if (lIdx != null) {
				lRate=allData[lIdx].bid.rate;
				lHaircut=allData[lIdx].bid.haircut;
				lValidTill=allData[lIdx].bid.validTill;
				lBidType=allData[lIdx].bid.bidType;
			}
		}
		//oldAlert("lBidType :: " + lBidType);
		bidForm.setValue({rate:lRate,haircut:lHaircut,validTill:lValidTill,bidType:lBidType});
		showModal($('#mdlEditAdv'));
	}
	function viewBid(pId) {
		$('#vw-'+pId).removeClass("hidden");
		$('#ed-'+pId).addClass("hidden");
	}
	function clearBid(pId, pBtn$) {
		var lIds=getIds(pId, 0);
		if (lIds==null) 
			return;
		var lData = {ids:lIds}
		updateBid(lData, pBtn$);
	}
	function saveBid(pId, pBtn$) {
		var lIdx=index[pId];
		if (lIdx != null) {
			var lRate=allData[lIdx].rateField.getValue();
			if(lRate<=0){
				alert("Value should not be less than or equal to zero.");
				return;
			}
			var lData = {ids:[pId], rate: lRate, validTill: allData[lIdx].bid.validTill, haircut: allData[lIdx].bid.haircut, bidType: allData[lIdx].bid.bidType};
			updateBid(lData, pBtn$);
		}
	}
	function updateAppStatus(pId,pAppStatus,pBtn$, pAction) {
		var lIds=getIds(pId, 5);
		if (lIds==null) 
			return;
		confirm('You are about to ' + pAction + ' selected bid(s). Are you sure?', null, null, function(pYes){
			if (pYes) {
				var lData = {ids:lIds, appStatus:pAppStatus};
				updateBid(lData, pBtn$, true);
			}
		}); 
	}
	function chgPr(pId, pDelta) {
		var lIdx=index[pId];
		if (lIdx != null) {
			var lRate=allData[lIdx].rateField.getValue();
			if (lRate==null)
				lRate=0.01;
			else {
				lRate=parseFloat(lRate)+pDelta;
				if (lRate<=0) lRate=0.01;
			}
			allData[lIdx].rateField.setValue(lRate.toFixed(2));
		}
	}
	function showDepth(pFuId) {
		$.ajax( {
	        url: "factunitfin/depth/" + pFuId,
	        type: "GET",
	        success: function( pObj, pStatus, pXhr) {
	        	var lModal$ = $('#mdlDepth');
	        	lModal$.find('.modal-content').html(tplDepth(pObj));
	        	showModal(lModal$);
	        },
	        error: errorHandler
		});
	}
	function updateBid(pData, pBtn$, pStatus) {
		if (pBtn$) pBtn$.prop('disabled',true);
		$.ajax( {
	        url: "factunitfin/" + (pStatus?"updatebidstatus":"updatebid"),
	        type: "POST",
	        data: JSON.stringify(pData),
	        success: function( pObj, pStatus, pXhr) {
            	$.each(pObj,function(pIndex,pValue){
            		if(pValue.unit!=null){
                		var lIdx=index[pValue.unit.id];
                		if (lIdx != null) {
                			allData[lIdx].bid=pValue.bid;
                			allData[lIdx].unit=pValue.unit;
                			allData[lIdx].tab=pValue.tab;
                		}
            		}
            	});
            	$('#mdlEditAdv').modal('hide');
            	showData();
            	var lMessage = tplMessage(pObj);
            	alert(lMessage);
	        },
	        error: errorHandler,
			complete: function() {
				if (pBtn$) pBtn$.prop('disabled',false);
				$('#chkSelectAll').prop('checked',false);
			}
		});
	}
	function showTab(pTab) {
		$('#mdlListAll .nav-tabs a[href="#'+pTab+'"]').tab('show');
	}
	function toggleBidType(pId) {
		var lIdx=index[pId];
		if (lIdx != null) {
			var lBidType=allData[lIdx].bid.bidType;
			if(lBidType==null||lBidType==''){
				lBidType = '<%=BidBean.BidType.Reserved.getCode()%>';
			}else if('<%=BidBean.BidType.Reserved.getCode()%>' === lBidType){
				lBidType = '<%=BidBean.BidType.Open.getCode()%>';
			}else if('<%=BidBean.BidType.Open.getCode()%>' === lBidType){
				lBidType = '<%=BidBean.BidType.Reserved.getCode()%>';
			}
			setBidTypeData(pId, lBidType);
			setBidTypeDisplay(lBidType);
		}		
	}
	function setBidTypeData(pId,bidType){
		var lIdx=index[pId];
		if (lIdx != null) {
			if(bidType==null||bidType==''){
				bidType = '<%=BidBean.BidType.Reserved.getCode()%>';
			}
			allData[lIdx].bid.bidType = bidType;
		}
	}
	function setBidTypeDisplay(bidType){
		if(bidType==null||bidType==''){
			bidType = '<%=BidBean.BidType.Reserved.getCode()%>';
		}
		$('#bidType').html(bidType.substring(0,1));
	}
	function downloadCSV(){
		var lTabIdx = -1;
		if(activeTab!=null){
			lTabIdx = parseInt(activeTab.substring(4));
		}
		if(activeTab==null || lTabIdx < HISTORY_INDEX){
			downloadFile('factunitfin/watch',null,JSON.stringify({"columnNames" : []}));
		}else{
			downloadFile('factunitfin/history',null,JSON.stringify({"columnNames" : []}));
		}
	}
	function showHistoryFilter(){
		histFilterForm.setValue(historyFilters); //set the last valid filter
		showModal($('#mdlHistoryFilter'));		
	}
	function populateSupplier(pForm) {
		var lEntity=pForm.getField("purchaser").getValue();
		var lField=pForm.getField("supplier");
		var lOptions=lField.getOptions()
		lOptions.dataSetValues = "pursuplnk/supplier/"+lEntity;
		lField.init();
	}
	function refreshClick(){
		var lTabIdx = -1;
		if(activeTab!=null){
			lTabIdx = parseInt(activeTab.substring(4));
		}
		if(activeTab==null || lTabIdx < HISTORY_INDEX){
			getData();
		}else{
			getHistoryData();
		}
	}
	function refreshForApproval() {
		$.ajax( {
	        url: "factunitfin/approval",
	        type: "POST",
	        data: "",
	        success: function( pObj, pStatus, pXhr) {
	    		templates[5].setData(pObj);
	    		var lCount = pObj?pObj.length:0;
	    		$('#badge5').html(lCount<10?"0"+lCount:lCount);
	        },
	        error: errorHandler,
	        complete: function() {
	        	$('#btnRefresh').prop('disabled',false);
	        }
		});

	}
	function viewClubbedInstrument(pId){
	   		showRemote('instview?id='+pId, 'modal-xl', true,'Instrument Details');
	}
	function viewBankDetails(pPurBankName,pPurIfsc,pPurAccNo,pSupBankName,pSupIfsc,pSupAccNo,pPurDesigBank,pSupDesigBank){
		var lData = {};
		var lSupBankName = "";
		var lPurBankName = "";
		if (pPurDesigBank == "true") {
			lPurBankName = "*  "; 
		}
		lPurBankName += pPurBankName;
		lData.purBankName = lPurBankName;
		lData.purIfsc = pPurIfsc;
		lData.purAccNo = pPurAccNo;
		if (pSupDesigBank == "true") {
			lSupBankName = "*  "; 
		}
		lSupBankName += pSupBankName;
		lData.supBankName = lSupBankName;
		lData.supIfsc = pSupIfsc;
		lData.supAccNo = pSupAccNo;
		$('#mdlBankDetail .modal-content').html(tplBankDetails(lData));
    	showModal($('#mdlBankDetail'));
	}
	
	function purchaserRating(pPurchaserCode){
		var lData = {};
		lData['buyerCode'] = pPurchaserCode;
		$.ajax( {
	        url: 'buyercreditrating/purchaserrating',
	        type: "POST",
	        data: JSON.stringify(lData),
	        success: function( pObj, pStatus, pXhr){
	     		$('#mdlPurchaserRating .modal-content').html(tplPurchaserRatings(pObj));
	         	showModal($('#mdlPurchaserRating'));
	        },
	    	error: errorHandler
	    });
	}
	function getPlan(){
		$.ajax( {
	        url: 'auctionchargeplans/entity/'+loginData.domain,
	        type: "POST",
	        success: function( pObj, pStatus, pXhr){
	        	var lModal$ = $('#mdlAucPlan');
	        	lModal$.find('.modal-content').html(tplAPSView(pObj));
	        	showModal(lModal$);
	        },
	    	error: errorHandler
	    });
	}
	</script>
   	
    </body>
</html>