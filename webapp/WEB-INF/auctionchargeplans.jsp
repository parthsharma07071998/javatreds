<!DOCTYPE html>
<%@page import="com.xlx.treds.master.bean.AuctionChargePlanBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargeSlabBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
	<head>
		<title>Auction Charge Plan : The plan for charges leved by TREDS platform.</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/jquery.autocomplete.css" rel="stylesheet">
	</head>
	<body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Auction Charge Plans" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAuctionChargePlans">		
		
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Auction Charge Plans</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<div class="col-sm-2"><section><label for="name" class="label">Plan Name:</label></section></div>
								<div class="col-sm-4">
									<section class="input select">
									<input type="text" id="name" placeholder="Select Plan" data-role="xautocompletefield" data-others="false"/>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="type" class="label">Plan Type:</label></section></div>
								<div class="col-sm-4">
									<section class="input select">
									<input type="text" id="type" placeholder="Select Plan Type" data-role="xautocompletefield" data-others="false"/>
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
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="auchrgplans-save" id=btnRemove><span class="fa fa-minus"></span> Remove</a>
					<a href="javascript:;" class="right_links secure" data-seckey="auchrgplans-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="auchrgplans-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>					<a></a>
				</div>
			</div>	

			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="name">Plan Name</th>
								<th data-width="100px" data-name="type">Plan Type</th>
							</tr></thead>
							</table>
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
					<h1 class="title">Auction Charge Slab</h1>
				</div>
			</div>
    		<div class="xform box">
    			<fieldset>
    			<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="name" class="label">Plan Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="name" placeholder="Plan Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="type" class="label">Plan Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="type"><option value="">Select Plan Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
				</fieldset>
				<fieldset id="auctionChargeSlabList">
					<div style="display:none" id="auctionChargeSlabList-frmSearch">
						<div class="row">
							<div class="col-sm-12">
								<div class="filter-block clearfix">
									<div class="">
										<a href="javascript:;" class="left_links"  id="auctionChargeSlabList-btnNew"><span class="glyphicon glyphicon-plus"></span> New</a>
										<a href="javascript:;" class="left_links"  id="auctionChargeSlabList-btnModify"><span class="glyphicon glyphicon-pencil"></span> Modify</a>
										<a href="javascript:;" class="left_links"  id="auctionChargeSlabList-btnRemove"><span class="glyphicon glyphicon-remove"></span> Remove</a>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane panel panel-default">
							<fieldset>
								<div class="row">
									<div class="col-sm-12">
										<table class="table table-bordered " id="auctionChargeSlabList-tblData">
											<thead><tr>
												<th data-width="100px" data-name="minAmount" data-data="minAmount">Min Amount (inclusive)</th>
												<th data-width="100px" data-name="maxAmount" data-data="maxAmount">Max Amount (exclusive)</th>
												<th data-width="100px" data-name="chargeType" data-data="chargeType">Charge Type</th>
												<th data-width="100px" data-name="chargePercentValue" data-data="chargePercentValue">Charge Percent Value</th>
												<th data-width="100px" data-name="chargeAbsoluteValue" data-data="chargeAbsoluteValue">Charge Absolute Value</th>
												<th data-width="100px" data-name="chargeMaxValue" data-data="chargeMaxValue">Max Value</th>
												<th data-width="100px" data-name="extendedChargeRate" data-data="extendedChargeRate">Extended Charge Rate</th>
											</tr></thead>
										</table>
									</div>
								</div>
							</fieldset>
						</div>
					</div>
					<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content">
					<div class="modal-header"><span>Slab Window</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
					<div class="modal-body modal-no-padding">
					<div id="auctionChargeSlabList-frmMain" class="xform">
					<!-- <header>Auction Charge Slabs : The charges leved by TREDS platform which can be absolute/percentage.</header> -->
						<fieldset>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-minAmount" class="label">Min Amount (inclusive):</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-minAmount" placeholder="Amount">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>	
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-maxAmount" class="label">Max Amount (exclusive)</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-maxAmount" placeholder="Amount">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-chargeType" class="label">Charge Type:</label></section></div>
								<div class="col-sm-6">
									<section class="select">
									<select id="auctionChargeSlabList-chargeType"><option value="">Select Charge Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-chargePercentValue" class="label">Charge Percent Value:</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-chargePercentValue" placeholder="Charge Value">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-chargeAbsoluteValue" class="label">Charge Absolute Value:</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-chargeAbsoluteValue" placeholder="Charge Value">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-chargeMaxValue" class="label">Max Value:</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-chargeMaxValue" placeholder="Max Value">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="auctionChargeSlabList-extendedChargeRate" class="label">Extended Charge Rate:</label></section></div>
								<div class="col-sm-6">
									<section class="input">
									<input type="text" id="auctionChargeSlabList-extendedChargeRate" placeholder="Extended Charge Rate">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							
						<div class="modal-footer">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id="auctionChargeSlabList-btnSave"><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id="auctionChargeSlabList-btnClose"><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
							
						</fieldset>
						</div>
						</div></div></div> 
						
						</div>
	   			</fieldset>
					<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
    				</div>
   			</fieldset>
			</div>
		</div>
	</div>

	   	<%@include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.autocomplete.js?1.2"></script>	
	<script type="text/javascript">
		var crudAuctionChargePlans$ = null,mainForm,mainFormSlab;

		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AuctionChargePlanBean.class).getJsonConfig()%>;
			var lFormConfigSlab = <%=BeanMetaFactory.getInstance().getBeanMeta(AuctionChargeSlabBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "auctionchargeplans",
					autoRefresh: true,
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
			crudAuctionChargePlans$ = $('#contAuctionChargePlans').xcrudwrapper(lConfig);
			crudAuctionChargePlans = crudAuctionChargePlans$.data('xcrudwrapper');
			mainForm = crudAuctionChargePlans.options.mainForm;

			mainFormSlab = mainForm.getField('auctionChargeSlabList').options.mainForm;
			
			mainForm.getField('auctionChargeSlabList').options.postModifyHandler=function(pObj){
				setDisplay(pObj.chargeType);
				return true;
			};
			
			mainForm.getField('auctionChargeSlabList').options.postNewHandler=function(pObj){
				setDisplay(null);
				return true;
			};
			
			$('#auctionChargeSlabList-frmMain #auctionChargeSlabList-chargeType').on('change', function() {
				setDisplay($('#auctionChargeSlabList-frmMain #auctionChargeSlabList-chargeType').val());
			});

		
		function setDisplay(chargeType){
			var lEnableAbs=false, lEnablePer=false, lEnableMax=false;
			if(chargeType=="<%=AuctionChargeSlabBean.ChargeType.Absolute.getCode()%>"){
				lEnableAbs = true;
			}else if(chargeType=="<%=AuctionChargeSlabBean.ChargeType.Percentage.getCode()%>"){
				lEnablePer = true;
			}else if(chargeType=="<%=AuctionChargeSlabBean.ChargeType.Threshold.getCode()%>"){
				lEnableAbs = true;
				lEnablePer = true;
				lEnableMax = true;
			}
			mainFormSlab.enableDisableField(['chargeAbsoluteValue'],lEnableAbs,!lEnableAbs);			
			mainFormSlab.enableDisableField(['chargePercentValue'],lEnablePer,!lEnablePer);			
			mainFormSlab.enableDisableField(['chargeMaxValue'],lEnableMax,!lEnableMax);			
			mainFormSlab.alterField(['chargeAbsoluteValue'], lEnableAbs, false);
			mainFormSlab.alterField(['chargePercentValue'], lEnablePer, false);
			mainFormSlab.alterField(['chargeMaxValue'], lEnableMax, false);
		}
		});
		
	</script>


</body>
</html>