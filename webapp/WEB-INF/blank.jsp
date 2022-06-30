<!DOCTYPE html>
<html lang="en">
	<head>
		<title>TREDS - </title>
		<%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Object" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	
	<div class="content" id="cont">
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Object</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"></div>
						<div class="col-sm-4">
						</div>
						<div class="col-sm-2"></div>
						<div class="col-sm-4">
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
					<a href="javascript:;" class="right_links" data-seckey="" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links" data-seckey="" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
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
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Object</h1>
				</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="domain" class="label">Member Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="domain"><option value="">Select Member Code</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
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
	</body>
</html>