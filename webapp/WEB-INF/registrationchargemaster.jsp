<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.RegistrationChargeMasterBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Registration Charge Master list</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
		<link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
	</head>
    <body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Registration Charge Master list" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contRegistrationChargeMaster" >		
		<div  id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Registration Charge Master</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
						<div class="col-md-12">
							<div class="form-group">
					<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="entityType"><option value="">Select Entity Type</option></select>
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
					<a href="javascript:;" class="right_links secure" data-seckey="regchrgmstr-manage" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="regchrgmstr-manage" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData" width="700px" data-scroll-y="300px">
							<thead><tr>
								<th data-width="300px" data-name="entityType">Entity Type</th>
								<th data-width="200px" data-name="registrationCharge">Registration Charge Amount</th>
								<th data-width="200px" data-name="annualCharge">Annual Charge Amount</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Registration Charge Master</h1>
				</div>
			</div>
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="entityType"><option value="">Select Entity Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="registrationCharge" class="label">Registration Charge:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="registrationCharge" placeholder="Registration Charge Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="annualCharge" class="label">Annual Charge:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="annualCharge" placeholder="Annual Charge Amount">
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
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
    <script src="../js/bootstrap-multiselect.js"></script>
	<script type="text/javascript">
		var crudRegistrationChargeMaster$ = null;
		var crudRegistrationChargeMaster = null;
		var mainForm = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RegistrationChargeMasterBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "regchrgmstr",
					autoRefresh: true
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudRegistrationChargeMaster$ = $('#contRegistrationChargeMaster').xcrudwrapper(lConfig);
			crudRegistrationChargeMaster=crudRegistrationChargeMaster$.data('xcrudwrapper');
			mainForm=crudRegistrationChargeMaster.options.mainForm;
		});
		
	</script>


</body>
</html>
