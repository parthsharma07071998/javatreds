<!DOCTYPE html>
<%@page import="groovy.json.JsonBuilder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.xlx.treds.master.bean.AuctionCalendarBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%
String lDate=StringEscapeUtils.escapeHtml(request.getParameter("date"));
String lFilter=null;
if (lDate!=null){
    Map<String,Object> lMap = new HashMap<String, Object>();
    if (lDate != null) lMap.put("date",lDate);
    lFilter = new JsonBuilder(lMap).toString();
}
%>
<html>
    <head>
        <title>TREDS | Auction Calendar</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Auction Calendar" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAuctionCalendar">
		
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Auction Calendar</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<div class="col-sm-2"><section><label for="type" class="label">Auction Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="type"><option value="">Select Auction Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="date" class="label">Auction Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="date" placeholder="Auction Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
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
					<a href="javascript:showConfirmationWindow();" class="right_links" id="btnConfWindow"><span class="glyphicon glyphicon-new-window"></span> Confirmation Window</a>
					<a href="javascript:;" class="right_links secure" data-seckey="auccal-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="auccal-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
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
									<th data-width="60px" data-name="type">Auction Type</th>
									<th data-width="100px" data-name="date">Auction Date</th>
									<th data-width="140px" data-name="bidStartTime">Bidding Start Time</th>
									<th data-width="140px" data-name="bidEndTime">Bidding End Time</th>
									<th data-width="150px" data-name="active">Active</th>
									<th data-width="0px" data-name="confWinList"  data-sel-exclude="true" data-visible="false">Conf Window Json</th>
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
					<h1 class="title">Auction Calendar</h1>
				</div>
			</div>
    		<div class="xform box">
    			<fieldset>
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="type" class="label">Auction Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="type"><option value="">Select Auction Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="date" class="label">Auction Date:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="date" placeholder="Auction Date" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="bidStartTime" class="label">Bidding Start Time:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="bidStartTime" placeholder="Bidding Start Time" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="bidEndTime" class="label">Bidding End Time:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="bidEndTime" placeholder="Bidding End Time" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="active" class="label">Active:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="active"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
						</div>
					</div>
				</fieldset>
			<fieldset id="confWinList">
				<div style="display:none" id="confWinList-frmSearch">
					<div class="row">
						<div class="col-sm-12">
							<div class="filter-block clearfix">
								<div class="">
									<a href="javascript:;" class="left_links"  id="confWinList-btnNew"><span class="glyphicon glyphicon-plus"></span> New</a>
									<a href="javascript:;" class="left_links"  id="confWinList-btnModify"><span class="glyphicon glyphicon-pencil"></span> Modify</a>
									<a href="javascript:;" class="left_links"  id="confWinList-btnRemove"><span class="glyphicon glyphicon-remove"></span> Remove</a>
								</div>
							</div>
						</div>
					</div>
					
					<div class="tab-pane panel panel-default">
						<fieldset>
							<div class="row">
								<div class="col-sm-12">
									<table class="table table-bordered table-condensed" id="confWinList-tblData" width="700px">
										<thead><tr>
											<th data-width="140px" data-name="confStartTime" data-data="confStartTime">Confirmation Start Time</th>
											<th data-width="140px" data-name="confEndTime" data-data="confEndTime">Confirmation End Time</th>
											<th data-width="100px" data-name="settlementDate" data-data="settlementDate">Settlement Date (Leg1)</th>
											<th data-width="50px" data-name="active" data-data="active">Active</th>
										</tr></thead>
									</table>
								</div>
							</div>
						</fieldset>
					</div>
				</div>
				<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content">
				<div class="modal-header"><span>Confirmation Window</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
				<div class="modal-body modal-no-padding">
				<div id="confWinList-frmMain" class="xform">
						<fieldset>
							<div class="row">
								<div class="col-sm-4"><section><label for="confWinList-confStartTime" class="label">Start Time:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="confWinList-confStartTime" placeholder="Confirmation Start Time" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="confWinList-confEndTime" class="label">End Time:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="confWinList-confEndTime" placeholder="Confirmation End Time" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="confWinList-settlementDate" class="label">Settlement Date:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="confWinList-settlementDate" placeholder="Settlement Date (Leg1)" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4"><section><label for="confWinList-active" class="label">Active:</label></section></div>
								<div class="col-sm-8">
									<section class="inline-group">
									<label class="radio"><input type=radio id="confWinList-active"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
								</div>
							</div>
				    		<div class="modal-footer">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info btn-lg btn-enter" id="confWinList-btnSave"><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" id="confWinList-btnClose"><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</fieldset>
				</div>
				</div></div></div></div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
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

	<div class="modal fade" id="mdlConfWindow" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
	</div></div></div>

<script id="tplConfWindow" type="text/x-handlebars-template">
<div class="modal-header">Confirmation Windows<span>{{fuId}}</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
<table class="table table-hover"><thead>
<tr>
<th>Start Time</th>
<th>End Time</th>
<th>Settlement Date</th>
<th>Active</th>
</tr>
</thead><tbody>
{{#each this}}
<tr>
   <td>{{confStartTime}}</td>
   <td>{{confEndTime}}</td>
   <td>{{settlementDate}}</td>
   <td>{{active}}</td>
</tr>
{{/each}}
</tbody></table>
<div class="row"><div class="col-sm-12">
<div class="btn-group pull-right">
<button type="button" class="btn btn-info btn-lg btn-close" data-dismiss="modal">OK</button>
</div>
</div></div>
</fieldset>
</div>
	</script>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
		var crudAuctionCalendar$,crudAuctionCalendar;
		var tplConfWindow;
		tplConfWindow = Handlebars.compile($('#tplConfWindow').html());

		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "auccal",
					tableConfig:{order:[2,"desc"]}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudAuctionCalendar$ = $('#contAuctionCalendar').xcrudwrapper(lConfig);
			crudAuctionCalendar = crudAuctionCalendar$.data('xcrudwrapper');
			
			<%
			if (lFilter != null) {
			%>		var lFilt=<%=lFilter%>;
					crudAuctionCalendar.options.searchForm.setValue(lFilt);
					crudAuctionCalendar.searchHandler();
			<%
			}
			%>
			
		});
		
		function showConfirmationWindow() {
			var lSelected = crudAuctionCalendar.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
        	var lModal$ = $('#mdlConfWindow');
        	lModal$.find('.modal-content').html(tplConfWindow(crudAuctionCalendar.getSelectedRow().data().confWinList));
        	showModal(lModal$);
		}
	</script>   	
    </body>
</html>