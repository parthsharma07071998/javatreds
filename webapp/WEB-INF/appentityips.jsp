<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
    <head>
        <title>TREDS | IP Whitelisting</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        <style>
        .value-list .input-group-addon {
        	width:40px;
        }
        </style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="IP Whitelisting" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAppEntity">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">IP Whitelisting</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch" style="display:none">
		</div>
		<!-- frmSearch -->
		<!-- frmMain -->
		<div id="frmMain">
			<div class="xform tab-pane panel panel-default no-margin">
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="ipList" class="control-label">IP White List:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<div class="input-group"><input type="text" class="form-control" id="ipList" placeholder="IP White List">
							<span class="input-group-btn"><button class="btn btn-default btn-success" type="button">Add</button></span>
							<b class="tooltip tooltip-top-right"></b></div>
							<div class="value-list"></div>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
				</fieldset>
			</div>
		</div>
		<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudAppEntity$ = null, crudAppEntity;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class, null, AppEntityBean.FIELDGROUP_UPDATEIPLIST).getJsonConfig()%>;
		var lConfig = {
				resource: "appentity/ips",
				modify: <%=lModify%>,
				postSaveHandler:function() {
					location.href='home';
					return true;
				},
				closeHandler:function() {
					location.href='home';
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudAppEntity$ = $('#contAppEntity').xcrudwrapper(lConfig);
		crudAppEntity = crudAppEntity$.data('xcrudwrapper');
		mainForm = crudAppEntity.options.mainForm;
	});
	</script>
   	
    </body>
</html>