<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierLimitUtilizationBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
  <head>
	<title>TREDS | Buyer Seller Limit Utilization.</title>
       <%@include file="includes1.jsp" %>
       <link href="../css/datatables.css" rel="stylesheet"/>
       <link href="../css/jquery.autocomplete.css" rel="stylesheet">

</head>
<body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Buyer Seller Limit Utilization."/>
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contPurchaserSupplierLimitUtilization">		
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Buyer Seller Limit Utilization</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="supplier"><option value="">Select Seller</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
						<div class="col-sm-4">
							<section class="input select">
							<input type="text" id="status" placeholder="Select Status" data-role="xautocompletefield" data-others="false"/>
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
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="purcsupplmtutil-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="purcsupplmtutil-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData" width="700px">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="supplier">Seller</th>
								<th data-width="300px" data-name="supName">Seller Name</th>
								<th data-width="100px" data-name="limit">Limit</th>
								<th data-width="100px" data-name="limitUtilized">Limit Utilized</th>
								<th data-width="100px" data-name="status">Status</th>
							</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
    	<div class="xform box" id="frmMain" style="display:none">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="supplier"><option value="">Select Seller</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
						<div class="col-sm-4">
							<section class="input select">
							<input type="text" id="status" placeholder="Select Status" data-role="xautocompletefield" data-others="false"/>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="limit" class="label">Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="limit" placeholder="Limit">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="limitUtilized" class="label">Limit Utilized:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="limitUtilized" placeholder="Limit Utilized">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" style="display:none">
						<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="purchaser"><option value="">Select Buyer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
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
    	<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>

	<script type="text/javascript">
		var crudPurchaserSupplierLimitUtilization$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLimitUtilizationBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "purcsupplmtutil",
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
			crudPurchaserSupplierLimitUtilization$ = $('#contPurchaserSupplierLimitUtilization').xcrudwrapper(lConfig);
		});
		
	</script>

</body>
</html>