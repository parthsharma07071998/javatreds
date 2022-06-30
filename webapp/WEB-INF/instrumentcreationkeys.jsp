
<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentCreationKeysBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>null</title>
		<%@include file="includes.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
	</head>
	<body class="skin-blue">
	<jsp:include page="header.jsp">
		<jsp:param name="title" value="null" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contInstrumentCreationKeys" >		
		<div class="xform" style="display:none" id="frmSearch">
			<!-- <header>null</header> -->
			

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="refType" class="label">Ref Type:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="refType"><option value="">Select Ref Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="refDate" class="label">Ref Date:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="refDate" placeholder="Ref Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="refNo" class="label">Ref No:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="refNo" placeholder="Ref No">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="poNumber" class="label">PO Number:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="poNumber" placeholder="PO Number">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="slNumber" class="label">S1 Number:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="slNumber" placeholder="S1 Number">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="purchaserCode" class="label">Purchaser Code:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="purchaserCode" placeholder="Purchaser Code">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="internalVendorRefNo" class="label">Internal Vendor Ref No:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="internalVendorRefNo" placeholder="Internal Vendor Ref No">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="supplierCode" class="label">Supplier Code:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="supplierCode" placeholder="Supplier Code">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="supplierGstn" class="label">Supplier Gstn:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="supplierGstn" placeholder="Supplier Gstn">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="inId" class="label">InId:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="inId" placeholder="InId">
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
						</div>
					</div>
				</div>
			</fieldset>
			<fieldset>
				<div class="row">
					<div class="col-sm-12">


						<table class="table table-bordered table-condensed" id="tblData" width="1000px" data-scroll-y="300px">
							<thead><tr>
								<th data-width="100px" data-name="refType">Ref Type</th>
								<th data-width="100px" data-name="refDate">Ref Date</th>
								<th data-width="100px" data-name="refNo">Ref No</th>
								<th data-width="100px" data-name="poNumber">PO Number</th>
								<th data-width="100px" data-name="slNumber">S1 Number</th>
								<th data-width="100px" data-name="purchaserCode">Purchaser Code</th>
								<th data-width="100px" data-name="internalVendorRefNo">Internal Vendor Ref No</th>
								<th data-width="100px" data-name="supplierCode">Supplier Code</th>
								<th data-width="100px" data-name="supplierGstn">Supplier Gstn</th>
								<th data-width="100px" data-name="inId">InId</th>
							</tr></thead>
						</table>

					</div>
				</div>
			</fieldset>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>null</header> -->

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="refType" class="label">Ref Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="refType"><option value="">Select Ref Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="refDate" class="label">Ref Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="refDate" placeholder="Ref Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="refNo" class="label">Ref No:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="refNo" placeholder="Ref No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="poNumber" class="label">PO Number:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="poNumber" placeholder="PO Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="slNumber" class="label">S1 Number:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="slNumber" placeholder="S1 Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="purchaserCode" class="label">Purchaser Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purchaserCode" placeholder="Purchaser Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="internalVendorRefNo" class="label">Internal Vendor Ref No:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="internalVendorRefNo" placeholder="Internal Vendor Ref No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="supplierCode" class="label">Supplier Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supplierCode" placeholder="Supplier Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="supplierGstn" class="label">Supplier Gstn:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supplierGstn" placeholder="Supplier Gstn">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="inId" class="label">InId:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="inId" placeholder="InId">
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

	<%@ include file="footer.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>

	<script type="text/javascript">
		var crudInstrumentCreationKeys$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(InstrumentCreationKeysBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "instrumentcreationkeys",
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
			crudInstrumentCreationKeys$ = $('#contInstrumentCreationKeys').xcrudwrapper(lConfig);
		});
		
	</script>


</body>
</html>