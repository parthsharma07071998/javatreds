<!DOCTYPE html>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.commonn.bean.LoginSessionBean"%>
<%
String lType = (String)request.getAttribute("type");
%>
<html>
    <head>
        <title>TREDS | Login History</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Entities" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contLoginSession">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Login History</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin">
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2 state-T"><section><label for="domain" class="label">Entity:</label></section></div>
						<div class="col-sm-2 state-T">
							<section class="input">
							<input type="text" id="domain" placeholder="Entity">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2 state-admin"><section><label for="loginId" class="label">Login Id:</label></section></div>
						<div class="col-sm-2 state-admin">
							<section class="input">
							<input type="text" id="loginId" placeholder="Login Id">
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
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="80px" data-name="domain">Entity</th>
								<th data-width="80px" data-name="loginId">Login Id</th>
								<th data-width="80px" data-name="status">Status</th>
								<th data-width="250px" data-name="reason">Reason</th>
								<th data-width="100px" data-name="requestIp">IP</th>
								<th data-width="130px" data-name="recordCreateTime">Login Time</th>
								<th data-width="130px" data-name="recordUpdateTime">Logout Time</th>
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
		</div>
		<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudLoginSession$ = null;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(LoginSessionBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "loginsess"
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudLoginSession$ = $('#contLoginSession').xcrudwrapper(lConfig);
	});
	</script>
   	
    </body>
</html>