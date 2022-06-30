
<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierRelationshipHistoryBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierLinkBean"%>
<%
// Boolean lNew = (Boolean)request.getAttribute("adm");
// boolean lNewBool = (lNew != null) && lNew.booleanValue();
// String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
boolean lAdmin = request.getParameter("adm")!=null;
String lSupplier = (String)request.getParameter("supplier");
String lPurchaser = (String)request.getParameter("purchaser");
%>
<html>
	<head>
		<title>TREDS | Buyer Seller Relationship History</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Buyer Seller Relationship History" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contPurchaserSupplierRelationshipHistory" >		
		<div id="frmSearch">
			<!-- <header>null</header> -->
			
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supplier" placeholder="Seller">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purchaser" placeholder="Buyer">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-group pull-right">
							<button type="button" class="btn btn-primary" id=btnSearch><span class="fa fa-search"></span> Search</button>
							<button type="button" class="btn btn-default" id=btnFilter><span class="fa fa-filter"></span> Filter</button>
							<button type="button" class="btn btn-default" id=btnNew><span class="fa fa-plus"></span> New</button>
							<button type="button" class="btn btn-default data-downloader" data-type="excel"><span class="fa fa-file-excel-o"></span> Download</button>
							<button type="button" class="btn btn-default data-downloader" data-type="csv"><span class="fa fa fa-file-text-o"></span> Download Csv</button>
							<button type="button" class="btn btn-default" id=btnUploadData><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="btn btn-default" id=btnModify><span class="fa fa-pencil"></span> Modify</button>
							<button type="button" class="btn btn-default" id=btnView><span class="fa fa-eye"></span> View</button>
							<button type="button" class="btn btn-default" id=btnRemove><span class="fa fa-minus"></span> Remove</button>
<!-- 							<button type="button" class="btn btn-default" id=btnCurRelation><span class="fa fa-minus"></span> Current Relation</button> -->
						</div>
					</div>
				</div>
			</fieldset>
			<fieldset>
				<div class="row">
					<div class="col-sm-12">


						<table class="table table-bordered table-condensed" id="tblData" width="100%" data-scroll-y="300px">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="supplier">Seller</th>
								<th data-width="100px" data-name="purchaser">Buyer</th>
								<th data-width="100px" data-name="startDate">Start Date</th>
								<th data-width="100px" data-name="endDate">End Date</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>null</header> -->

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supplier" placeholder="Seller">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purchaser" placeholder="Buyer">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="startDate" class="label">Start Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="startDate" placeholder="Start Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="endDate" class="label">End Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="endDate" placeholder="End Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-primary" id=btnSave><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
					<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>

	<script type="text/javascript">
		var crudPurchaserSupplierRelationshipHistory$= null, crudPurchaserSupplierRelationshipHistory;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierRelationshipHistoryBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "pursuprelationshiphistory",
					autoRefresh: true,
		};
			lConfig = $.extend(lConfig, lFormConfig);
			crudPurSupRelationshipHistory$ = $('#contPurchaserSupplierRelationshipHistory').xcrudwrapper(lConfig);
			crudPurSupRelationshipHistory = crudPurSupRelationshipHistory$.data('xcrudwrapper');
			searchForm=crudPurSupRelationshipHistory.options.searchForm;
			searchForm.getField('purchaser').setValue("<%=(String)request.getParameter("purchaser")%>");
			searchForm.getField('supplier').setValue("<%=(String)request.getParameter("supplier")%>");
        });
	</script>


</body>
</html>