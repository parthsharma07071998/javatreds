<!DOCTYPE html>
<%@page import="com.xlx.treds.master.bean.HolidayMasterBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Holidays</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Holidays" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contHolidayMaster">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title"> Holidays</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="fromDate" class="label">From Date:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="fromDate" placeholder="From Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
							<div class="col-sm-2"><section><label for="toDate" class="label">To Date:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<i class="icon-append fa fa-clock-o"></i>
								<input type="text" id="toDate" placeholder="To Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b></section>
							</div>
						</div>
					</div>
					<div class="col-md-12">
						<div class="form-group">
							<div class="col-sm-2"><section><label for="type" class="label">Type:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="type"><option value="">Select Type</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
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
		</div>
		<div class="filter-block clearfix">
			<div class="">
				<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
				<a href="javascript:;" class="right_links secure" data-seckey="holiday-view" id=btnDownloadCSV><span class="fa fa-download"></span> Download CSV</a>
				<a href="javascript:;" class="right_links secure" data-seckey="holiday-delete" id=btnRemove><span class="glyphicon glyphicon-remove"></span> Remove</a>
				<a href="javascript:;" class="right_links secure" data-seckey="holiday-view" id=btnUploadHoliday><span class="glyphicon glyphicon-remove"></span> Upload</a>
				<a href="javascript:;" class="right_links secure" data-seckey="holiday-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
				<a href="javascript:;" class="right_links secure" data-seckey="holiday-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
				<a></a>
			</div>
		</div>
		</div>
		<!-- frmSearch -->
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " id="tblData">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="80px" data-name="date">Date</th>
								<th data-width="80px" data-name="day">Day</th>
								<th data-width="200px" data-name="desc">Desc</th>
								<th data-width="80px" data-name="type">Type</th>
								<th data-width="80px" data-name="disableShifting">Disable Shifting</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
		<!-- frmMain -->
		<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content xform" id="frmMain">
		<div class="modal-header">Holiday</div>
		<div class="modal-body">
		<fieldset>
			<div>
				<div class="row form-group">
					<div class="col-sm-4"><section><label for="date" class="label">Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="date" placeholder="Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row form-group">
					<div class="col-sm-4"><section><label for="desc" class="label">Desc:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="desc" placeholder="Desc">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row form-group">
					<div class="col-sm-4"><section><label for="type" class="label">Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row form-group">
                       <div class="col-sm-4"><section><label for="disableShifting" class="label">Don't Shift Obligations:</label></section></div>
                       <div class="col-sm-8">
                               <section class="input inline-group">
                               <label class="radio"><input type=radio id="disableShifting"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
                               </section>
                               <section class="view"></section>
                       </div>
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
		</div></div></div>
    	<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudHolidayMaster$ = null;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "holiday",
				autoRefresh: true
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudHolidayMaster$ = $('#contHolidayMaster').xcrudwrapper(lConfig);
		crudHolidayMaster = crudHolidayMaster$.data('xcrudwrapper');
		
		$('#btnDownloadCSV').on('click', function() {
			var lFilter=crudHolidayMaster.options.searchForm.getValue();
			lFilter.columnNames=crudHolidayMaster.options.tableConfig.columnNames;
			downloadFile('holiday/all',null,JSON.stringify(lFilter));
		});
		
		$('#btnUploadHoliday').on('click',function(){
			showRemote('upload?url=holidaymaster', null, false);
		});
	});
	</script>
   	
    </body>
</html>