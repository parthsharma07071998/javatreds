<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Purchaser Aggregator</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Purchaser Aggregator" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contAppEntity">		
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Purchaser Aggregator</h1>
				</div>
			</div>
			
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="code" placeholder="Code">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
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
					<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="puragg-save" id=btnRemove><span class="fa fa-minus"></span> Remove</a>
					<a href="javascript:;" class="right_links secure" data-seckey="puragg-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="puragg-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a href="javascript:;" class="right_links secure" data-seckey="puragg-view" id=btnDownload><span class="fa fa-download"></span> Download</a>
					<a href="javascript:;" class="right_links secure" data-seckey="puragg-view" id=btnView><span class="fa fa-eye"></span> View</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData" width="1200px" data-scroll-y="300px">
							<thead><tr>
								<th data-width="100px" data-name="code">Code</th>
								<th data-width="100px" data-name="name">Name</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-visible="false" data-width="100px" data-name="aggCompanyGSTN">Company GSTN</th>
								<th data-visible="false" data-width="100px" data-name="aggContactPerson">Contact Person</th>
								<th data-visible="false" data-width="100px" data-name="aggContactMobile">Contact Mob Num</th>
								<th data-visible="false" data-width="100px" data-name="aggContactEmail">Contact Email</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>App Entity</header> -->
			<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="code" placeholder="Code" readonly>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="purchaserList" class="label">Purchasers :</label></section></div>
					<div class="col-sm-10">
						<section class="select">
						<select id="purchaserList" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="aggCompanyGSTN" class="label">Company Gstn:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggCompanyGSTN" placeholder="Company Gstn">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="aggContactPerson" class="label">Contact Person:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactPerson" placeholder="Contact Person">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="aggContactMobile" class="label">Contact Mobile No.:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactMobile" placeholder="Contact Mobile No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="aggContactEmail" class="label">Contact Email:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactEmail" placeholder="Contact Email">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/jquery.bootstrap-duallistbox.js"></script>
	<script type="text/javascript">
		var crudAppEntity$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "puraggregator",
					autoRefresh: true,
					postModifyHandler: function(pObj) {
						mainForm.getField('aggCompanyGSTN').setValue(pObj.aggCompanyGSTN);
						mainForm.getField('aggContactPerson').setValue(pObj.aggContactPerson);
						mainForm.getField('aggContactMobile').setValue(pObj.aggContactMobile);
						mainForm.getField('aggContactEmail').setValue(pObj.aggContactEmail);
						return true;
					},
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
			crudAppEntity$ = $('#contAppEntity').xcrudwrapper(lConfig);
			crudAppEntity = crudAppEntity$.data('xcrudwrapper');
			mainForm = crudAppEntity.options.mainForm;
			mainForm.alterField('code', false, true);
		});
		
	</script>


</body>
</html>