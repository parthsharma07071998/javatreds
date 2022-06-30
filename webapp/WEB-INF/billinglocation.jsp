<!DOCTYPE html>
<%@page import="groovy.json.JsonBuilder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.BillingLocationBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lCode=StringEscapeUtils.escapeHtml(request.getParameter("code"));
String lFilter=null;
if (lCode!=null) {
    Map<String,Object> lMap = new HashMap<String, Object>();
    if (lCode != null) lMap.put("code",lCode);
    lFilter = new JsonBuilder(lMap).toString();
}
%>
<html>
	<head>
		<title>Location and their billing location.</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Location and their billing location." />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contBillingLocation">		
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Billing Locations</h1>
				</div>
			</div>
			<div class="filter-block clearfix">
				<div class="">
							<span class="right_links">
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> New </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnInstructions><span class="glyphicon glyphicon-upload"></span> Upload </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-view" id=btnDownloadCSV><span class="glyphicon glyphicon-download-alt"></span> Download </a>
							<span class='state-T'><a class="secure" href="javascript:;" data-seckey="buyercreditrating-view" id=btnView><span class="glyphicon glyphicon-eye-open"></span> View </a></span>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnRemove><span class="glyphicon glyphicon-trash"></span> Remove </a>
							</span>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData">
							<thead><tr>
								<th data-width="100px" data-name="code">Code</th>
								<th data-width="50px" data-name="id">Id</th>
								<th data-width="125px" data-name="name">Name</th>
								<th data-width="125px" data-name="gstn">GSTN</th>
								<th data-width="50px" data-name="billLocId">BillLocId</th>
								<th data-width="125px" data-name="billLocName">Bill Loction Name</th>
								<th data-width="125px" data-name="billLocGstn">Bill Location Gstn</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>Location and their billing location.</header> -->

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="code" placeholder="Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="id" class="label">Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="id"><option value="">Select Id</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="billLocId" class="label">Bill Location Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="billLocId"><option value="">Select Bill Location Id</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
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
	<script type="text/javascript">
		var crudBillingLocation$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(BillingLocationBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "billinglocation",
					autoRefresh: false,
					keyFields:["code","id"],
					postModifyHandler: function(pData) {
						populateLocations("id",pData.id);
						populateLocations("billLocId",pData.billLocId);
						return true;
					},
// 					postSaveHandler: function(pObj) {
// 						alert("Saved Successfully", "Information", function() {
<%-- 							<% --%>
// 							if (lFilter != null) {
<%-- 							%>		var lFilt1=<%=lFilter%>; --%>
// 									crudBillingLocation.options.searchForm.setValue(lFilt1);
// 									crudBillingLocation.searchHandler();
<%-- 							<% --%>
// 							}
<%-- 							%> --%>
// 	            		});
//          				return false;
//          			},	 
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudBillingLocation$ = $('#contBillingLocation').xcrudwrapper(lConfig);
			crudBillingLocation = crudBillingLocation$.data('xcrudwrapper');
			mainForm = crudBillingLocation$.data('xcrudwrapper').options.mainForm;
			<%
			if (lFilter != null) {
			%>		var lFilt=<%=lFilter%>;
					crudBillingLocation.options.searchForm.setValue(lFilt);
					crudBillingLocation.searchHandler();
			<%
			}
			%>
		});
		
		function populateLocations(pFieldName,pValue){
			$('#'+pFieldName).empty();
			var lField=mainForm.getField(pFieldName);
			var lOptions=lField.getOptions();
			if (mainForm.getField('code').getValue()==null) {
				lOptions.dataSetValues=[];
			}else {
				lOptions.dataSetValues ="companylocation/all?aecode="+mainForm.getField('code').getValue();
			}
			lField.init();
			mainForm.getField(pFieldName).setValue(pValue);
		}
	</script>


</body>
</html>