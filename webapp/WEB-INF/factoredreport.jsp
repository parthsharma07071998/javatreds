<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.other.bean.FactoredReportBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
	<head>
		<title>Factored Report</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="null" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contFactoredReport">		
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin" id=divFilter>
				<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="fromDate" class="label">From Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="fromDate" placeholder="From Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="toDate" class="label">To Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="toDate" placeholder="To Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="fuPurchaser" class="label">Purchaser:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="fuPurchaser"><option value="">Select Purchaser</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="inSalesCategory" class="label">Sales Category:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="inSalesCategory"><option value="">Select Sales Category</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
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
					<a href="javascript:;" class="right_links" id=btnDownloadReport style=''><span class="fa fa-download"></span> Download </a>
					
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-width="100px" data-name="fuId">Factoring Unit Id.</th>
									<th data-width="100px" data-name="fuStatus">Status</th>
									<th data-width="100px" data-name="inInstNumber">Invoice Number</th>
									<th data-width="100px" data-name="inInstDate">Invoice Date</th>
									<th data-width="100px" data-name="inGoodsAcceptanceDate">Goods Acceptance Date</th>
									<th data-width="100px" data-name="l2Date">Leg 2 Date</th>
									<th data-width="100px" data-name="inInstImage">Instument</th>
									<th data-width="100px" data-name="inNetAmount">Net Amount</th>
									<th data-width="100px" data-name="inDeductions">Deduction and TDS</th>
									<th data-width="100px" data-name="fuAamount">Factored Amount</th>
									<th data-width="100px" data-name="inSalesCategory">Sales Category</th>
									<th data-width="100px" data-name="inFuAcceptDateTime">Factored Date Time</th>
									<th data-width="100px" data-name="l1Date">Leg 1 Date</th>
									<th data-width="100px" data-name="purchaser">Purchaser</th>
									<th data-width="100px" data-name="supplier">Supplier</th>
								</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudFactoredReport$ = null;
		var crudFactoredReport = null;
		var searchForm = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(FactoredReportBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "factoredreport",
					autoRefresh: true
					};
			lConfig = $.extend(lConfig, lFormConfig);
			crudFactoredReport$ = $('#contFactoredReport').xcrudwrapper(lConfig);
			crudFactoredReport = crudFactoredReport$.data('xcrudwrapper');
			searchForm=crudFactoredReport.options.searchForm;
			$('#btnDownloadReport').on('click', function() {
				var lData = {};
				var lFields = ['toDate','fromDate','fuPurchaser','inSalesCategory'];
				for (var lField in lFields ){
					lData[lFields[lField]] = searchForm.getField(lFields[lField]).getValue();
				}
				downloadFile('factoredreport/download',null,JSON.stringify(lData) );
			});
		});
		
	</script>


</body>
</html>