<!DOCTYPE html>
<%@page import="com.xlx.common.registry.bean.RefCodeValuesBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Reference Codes</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Reference Codes" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contRefCodeValues">
		<!-- frmSearch -->
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Reference Codes</h1>
			</div>
		</div>
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="RECId" class="label">Reference Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="RECId"><option value="">Select Reference Code</option></select>
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
					<a href="javascript:;" class="right_links" id=btnRemove><span class="glyphicon glyphicon-remove"></span> Remove</a>
					<a href="javascript:;" class="right_links" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
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
									<th data-width="100px" data-name="seqNo">Sequence No</th>
									<th data-width="100px" data-name="value">Value</th>
									<th data-width="200px" data-name="desc">Description</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
    	<div id="frmMain" style="display:none">
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="RECId" class="label">Reference Code:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="RECId"><option value="">Select Reference Code</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="seqNo" class="label">Sequence No:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="seqNo" placeholder="Sequence No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="value" class="label">Value:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="value" placeholder="Value">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="desc" class="label">Description:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="desc" placeholder="Description">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
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
			</fieldset>
    		</div>
    	</div>
    	<!-- frmMain -->
	</div>
   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudRefCodeValues$ = null,crudRefCodeValues;
	var mainForm;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RefCodeValuesBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "refcodevalue",
		};
		lConfig = $.extend(lConfig, lFormConfig);
		crudRefCodeValues$ = $('#contRefCodeValues').xcrudwrapper(lConfig);
		crudRefCodeValues = crudRefCodeValues$.data('xcrudwrapper');
		mainForm = crudRefCodeValues.options.mainForm;
	});
	

	</script>
   	
    </body>
</html>