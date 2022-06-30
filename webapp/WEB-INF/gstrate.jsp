<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.GSTRateBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
  <head>
	<title>GST Rates : Stores GST Rates along with surcharge for a particular period .</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="GST Rates" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contGSTRates">		
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">GST Rates</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="fromDate" class="label">From Date:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="fromDate" placeholder="From Date" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="toDate" class="label">To Date:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="toDate" placeholder="To Date" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="cgst" class="label">CGST:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="cgst" placeholder="CGST">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="sgst" class="label">SGST:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="sgst" placeholder="SGST">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="igst" class="label">IGST:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="igst" placeholder="IGST">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="cgstSurcharge" class="label">CGST Surcharge:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="cgstSurcharge" placeholder="CGST Surcharge">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="sgstSurcharge" class="label">SGST Surcharge:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="sgstSurcharge" placeholder="SGSTSurcharge">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="igstSurcharge" class="label">IGST Surcharge:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="igstSurcharge" placeholder="IGSTSurcharge">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
								<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
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
								<th data-width="100px" data-name="fromDate">From Date</th>
								<th data-width="100px" data-name="toDate">To Date</th>
								<th data-width="100px" data-name="cgst">CGST</th>
								<th data-width="100px" data-name="cgstSurcharge">CGST Surcharge</th>
								<th data-width="100px" data-name="sgst">SGST</th>
								<th data-width="100px" data-name="sgstSurcharge">SGSTSurcharge</th>
								<th data-width="100px" data-name="igst">IGST</th>
								<th data-width="100px" data-name="igstSurcharge">IGSTSurcharge</th>
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
					<h1 class="title">GST Rates along with surcharge for a particular period.</h1>
				</div>
			</div>
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="fromDate" class="label">From Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="fromDate" placeholder="From Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="toDate" class="label">To Date:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="toDate" placeholder="To Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="cgst" class="label">CGST:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="cgst" placeholder="CGST">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="cgstSurcharge" class="label">CGST Surcharge:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="cgstSurcharge" placeholder="CGST Surcharge">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="sgst" class="label">SGST:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="sgst" placeholder="SGST">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="sgstSurcharge" class="label">SGST Surcharge:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="sgstSurcharge" placeholder="SGST Surcharge">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="igst" class="label">IGST:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="igst" placeholder="IGST">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="igstSurcharge" class="label">IGST Surcharge:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="igstSurcharge" placeholder="IGST Surcharge">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
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
		</div>
    	</div>
    	<!-- frmMain -->
	</div>
   	<%@include file="footer1.jsp" %>
	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>

	<script type="text/javascript">
		var crudGSTRates$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(GSTRateBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "gstrate",
					autoRefresh: true,
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
			crudGSTRates$ = $('#contGSTRates').xcrudwrapper(lConfig);
		});
		
	</script>

</body>
</html>