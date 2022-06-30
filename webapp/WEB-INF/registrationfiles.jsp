<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.other.bean.RegistrationFilesBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Registration Files</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
		<style>
			.grid-container {
			  display: inline-grid;
			  grid-template-columns: auto auto auto auto auto;
			  background-color: white;
			  padding: 10px;
			  grid-gap: 20px;
			}
			.grid-item {
			  background-color: #ffcc99;
			  padding: 15px;
			  font-size: 14px;
			  text-align: left;
			  height: 100px;
  			  width: 187px;
			}

		</style>
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Registration Files" />
		<jsp:param name="desc" value="" />
	</jsp:include>

			<!-- <header>null</header> -->
<div class="content" id="contRegistrationFiles">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Registration Files</h1>
			</div>
		</div>
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<div class="col-sm-2"><section><label for="entityType" class="label">Entity Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="entityType"><option value="">Select Entity Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="constitution" class="label">Constitution:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="constitution"><option value="">Select Constitution</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="">
				<fieldset>
					<div id="tab1">
						<div class="filter-block clearfix">
							<div class="">
								<span class="left_links">
									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select>
								</span>
								<span class="right_links">
									<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
									<a class="secure right_links btn-group0" href="javascript:;" data-seckey="circulars-save" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span>New</a>
									<a></a>
								</span>
							</div>
						</div>
						<div >
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
				</fieldset>
			</div>	
		</div>
			
		<div class="xform" style="display:none" id="frmMain">
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
						<div class="col-sm-2"><section><label for="constitution" class="label">Constitution:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="constitution"><option value="">Select Constitution</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<input type="hidden" id="storageFileName" data-role="xuploadfield" data-file-type="REGISTRATIONS" />
						<div class="col-sm-2"><section><label for="storageFileName" class="label">Storage File Name:</label></section></div>
						<div class="col-sm-4">
						<section id='storageButtons' class="state-mandatory">
							<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
							<span class="upl-info"></span>
							<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
						</section>
					</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
				</fieldset>
			</div>	
		</div>
</div>
	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
		<script src="../js/jquery.xtemplatetable.js"></script>
		<script id="tpl" type="text/x-handlebars-template">
<div class="grid-container">
		{{#each this}}
			<div class="grid-item" > 
				<div>
				<i><b><span>{{entity}}</span></b></i> 
				<br>
				<i><span>{{constitution}}</span></i> 
				<br>			
				<span ><a href="javascript:downloadFile('{{storageFileName}}')" title="Download File" style="color:blue !important"><span class="fa fa-download">&nbsp;&nbsp;&nbsp;</span>{{fileName}}</a></span>			
				</div>
			</div>
		{{/each}}
	</div>
</div>
	</script>
	<script type="text/javascript">
		var crudRegistrationFiles$ = null;
		var templates;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RegistrationFilesBean.class).getJsonConfig()%>;
			var lTableConf={
					template:Handlebars.compile($('#tpl').html()),
					sortCols:[{text:"ID", value:"id"},
					          ]
				};
				var lTplTable$=$('#tab1').xtemplatetable(lTableConf);
				templates=lTplTable$.data('xtemplatetable');
			var lConfig = {
					resource: "registrationfiles",
					autoRefresh: true,
					postSearchHandler: function(pObj) {
						templates.setData(pObj);
						return false;
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
			crudRegistrationFiles$ = $('#contRegistrationFiles').xcrudwrapper(lConfig);
		});
		
		function downloadFile(pFileName){
			location.href="upload/REGISTRATIONS/"+pFileName;
		}
	</script>


</body>
</html>