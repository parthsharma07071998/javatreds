<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.test.bean.ReleaseQueryBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Release Queries</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Release Queries" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contReleaseQuery">		
		<div class="page-title">
				<div class="title-env">
					<h1 class="title">Release Queries</h1>
				</div>
		</div>
	
		<div id="frmSearch">
			<!-- <header>Release Queries</header> -->
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links"  id=btnRemove><span class="fa fa-minus"></span> Remove</a>
					<a href="javascript:;" class="right_links"  id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a href="javascript:;" class="right_links"  id=btnDownload><span class="fa fa-download"></span> Download</a>
					<a href="javascript:;" class="right_links" id=btnView><span class="fa fa-eye"></span> View</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData" width="1800px" data-scroll-y="300px">
							<thead><tr>
								<th data-width="100px" data-name="seqNo">SeqNo</th>
								<th data-width="100px" data-name="releaseKey">Release Key</th>
								<th data-width="100px" data-name="preCheckActionQuery">Pre Check Action Query</th>
								<th data-width="100px" data-name="preCheckActionQueryOutput">Pre Check Action Query Output</th>
								<th data-width="100px" data-name="preCheckActionQueryDateTime">Pre Check Action Query DATE TIME</th>
								<th data-width="100px" data-name="actionQuery">Action Query</th>
								<th data-width="100px" data-name="actionQueryOutput">Action Query Output</th>
								<th data-width="100px" data-name="actionQueryDateTime">Action Query Date Time</th>
								<th data-width="100px" data-name="postCheckActionQuery">Post Check Action Query</th>
								<th data-width="100px" data-name="postCheckActionQueryOutput">Post Check Action Query Output</th>
								<th data-width="100px" data-name="postCheckActionQueryDateTime">Post Check Action Query DATE TIME</th>
								<th data-width="100px" data-name="preCheckRollbackQuery">Pre Check Rollback Query</th>
								<th data-width="100px" data-name="preCheckRollbackQueryOutput">Pre Check Rollback Query Output</th>
								<th data-width="100px" data-name="preCheckRollbackQueryDateTime">Pre Check Rollback Query DATE TIME</th>
								<th data-width="100px" data-name="rollbackQuery">Rollback Query</th>
								<th data-width="100px" data-name="rollbackQueryDateTime">Rollback Query Date Time</th>
								<th data-width="100px" data-name="postCheckRollbackQuery">Post Check Rollback Query</th>
								<th data-width="100px" data-name="postCheckRollbackQueryOutput">Post Check Rollback Query Output</th>
								<th data-width="100px" data-name="postCheckRollbackQueryDateTime">Post Check Rollback Query DATE TIME</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>Release Queries</header> -->
			<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="seqNo" class="label">SeqNo:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="seqNo" placeholder="SeqNo">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="releaseKey" class="label">Release Key:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="releaseKey" placeholder="Release Key">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="preCheckActionQuery" class="label">Pre Check Action Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="preCheckActionQuery" placeholder="Pre Check Action Query">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="actionQuery" class="label">Action Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="actionQuery" placeholder="Action Query">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="postCheckActionQuery" class="label">Post Check Action Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="postCheckActionQuery" placeholder="Post Check Action Query">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="preCheckRollbackQuery" class="label">Pre Check Rollback Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="preCheckRollbackQuery" placeholder="Pre Check Rollback Query">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="rollbackQuery" class="label">Rollback Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="rollbackQuery" placeholder="Rollback Query">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="postCheckRollbackQuery" class="label">Post Check Rollback Query:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="postCheckRollbackQuery" placeholder="Post Check Rollback Query">
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
	<script type="text/javascript">
		var crudReleaseQuery$ = null;
		var lQueryFields = ["preCheckActionQuery", "postCheckActionQuery", "preCheckRollbackQuery", "rollbackQuery", "postCheckRollbackQuery"];
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ReleaseQueryBean.class).getJsonConfig()%>;
			$.each(lFormConfig.fields, function(pIndex,pValue){
				if (pValue.dataType=="STRING") {
					//oldAlert(pValue.name+" : "+ pValue.pattern);
					pValue.pattern="";
				}
			});
			var lConfig = {
					resource: "releasequeries",
					autoRefresh: true,
					tableConfig: {
						lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
						lengthChange:true,
						pageLength:10
						}
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
			crudReleaseQuery$ = $('#contReleaseQuery').xcrudwrapper(lConfig);
		});
		
	</script>


</body>
</html>