<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.notialrt.bean.NotiAlrtBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>TREDS | Alerts</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Alerts" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contNotiAlrt">		
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Alerts</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="key" class="label">Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="key" placeholder="Key">
						<b class="tooltip tooltip-top-right"></b></section>
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
					<span class="right_links">
						<a class="right_links" href="javascript:mainForm.setViewMode(true);"  id="btnView"><span class="glyphicon glyphicon-eye-open"></span> View</a>
						<a></a>
					</span>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " id="tblData">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="type">Type</th>
								<th data-width="100px" data-name="key">Key</th>
								<th data-width="100px" data-name="alertDesc">Alert Description</th>
								<th data-width="100px" data-name="recordCreateTime">Generated Time</th>
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
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="key" class="label">Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="key" placeholder="Key">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="alertDesc" class="label">Alert Description:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="alertDesc" placeholder="Alert Description">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
		var crudNotiAlrt$ = null;
		var crudNotiAlrt = null;
		var mainForm = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(NotiAlrtBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "notialrt",
					autoRefresh: true,
					preModifyHandler : function(pData) {
						mainForm.setViewMode(true);
						return true;
					},
				};
			lConfig = $.extend(lConfig, lFormConfig);
			crudNotiAlrt$ = $('#contNotiAlrt').xcrudwrapper(lConfig);
			crudNotiAlrt = crudNotiAlrt$.data('xcrudwrapper');
			mainForm = crudNotiAlrt.options.mainForm;
			
		});
		
	</script>


</body>
</html>