<!DOCTYPE html>
<%@page import="com.xlx.treds.other.bean.GEMInvoiceBean.Status"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.other.bean.GEMInvoiceBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>TREDS | GEM Invoices</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contGEMInvoice">	
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Alerts</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="arrId" class="label">arr Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="arrId" placeholder="arr Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>

					<div class="col-sm-2"><section><label for="purPan" class="label">Purchaser PAN:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="purPan" placeholder="Purchaser PAN">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="goodsAcceptDate" class="label">Goods/Service Acceptance Date:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="goodsAcceptDate" placeholder="Goods/Service Acceptance Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
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
					
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
							<table class="table table-bordered " data-col-chooser="spnColumnChooser"  id="tblData">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-visible="false" data-name="arrId">arr Id</th>
								<th data-width="100px" data-name="supName">Supplier</th>
								<th data-width="100px" data-name="supplier">Supplier Code</th>
								<th data-width="100px" data-name="supGstn">Supplier GSTN</th>
								<th data-width="100px" data-name="supLocation">Supplier Location</th>
								<th data-width="100px" data-name="supPan">Supplier PAN</th>
								<th data-width="100px" data-name="purName">Purchaser</th>
								<th data-width="100px" data-name="purchaser">Purchaser Code</th>
								<th data-width="100px" data-name="purGstn">Purchaser GSTN</th>
								<th data-width="100px" data-name="purLocation">Purchaser Location</th>
								<th data-width="100px" data-name="purPan">Purchaser PAN</th>
								<th data-width="100px" data-name="instNumber">Invoice Number</th>
								<th data-width="100px" data-name="goodsAcceptDate">Goods/Service Acceptance Date</th>
								<th data-width="100px" data-name="poDate">Purchase Order Date</th>
								<th data-width="100px" data-name="poNumber">Purchase Order Number</th>
								<th data-width="100px" data-name="instDate">Invoice Date</th>
								<th data-width="100px" data-name="instDueDate">Invoice Due Date</th>
								<th data-width="100px" data-name="amount">Invoice Amount</th>
								<th data-width="100px" data-name="adjAmount">Adjustment Amount</th>
								<th data-width="100px" data-name="creditPeriod">Credit Period</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-visible="false" data-name="creator">Creator Id</th>
								<th data-name="dmy1" data-sel-exclude=true>Actions</th>
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
    		<div class="xform box">

			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="arrId" class="label">arr Id:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="arrId" placeholder="arr Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="type" class="label">File Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="type"><option value="">Select File Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="supplier" class="label">Supplier:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supplier" placeholder="Supplier">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="supGstn" class="label">Supplier GSTN:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supGstn" placeholder="Supplier GSTN">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="supPan" class="label">Supplier PAN:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="supPan" placeholder="Supplier PAN">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purchaser" placeholder="Purchaser">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="purGstn" class="label">Purchaser GSTN:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purGstn" placeholder="Purchaser GSTN">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="purPan" class="label">Purchaser PAN:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="purPan" placeholder="Purchaser PAN">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="goodsAcceptDate" class="label">Goods/Service Acceptance Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="goodsAcceptDate" placeholder="Goods/Service Acceptance Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="poDate" class="label">Purchase Order Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="poDate" placeholder="Purchase Order Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="poNumber" class="label">Purchase Order Number:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="poNumber" placeholder="Purchase Order Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="instDate" class="label">Invoice Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="instDate" placeholder="Invoice Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="instDueDate" class="label">Invoice Due Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="instDueDate" placeholder="Invoice Due Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="amount" class="label">Invoice Amount:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="amount" placeholder="Invoice Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="adjAmount" class="label">Adjustment Amount:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="adjAmount" placeholder="Adjustment Amount">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="creditPeriod" class="label">Credit Period:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="creditPeriod" placeholder="Credit Period">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
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
	</div>
	
	<div class="modal fade" id="mdlWorkFlow" tabindex=-1><div class="modal-dialog modal-lg" ><div class="modal-content">
	</div></div></div>
	
	<script id="tplWorkFlow" type="text/x-handlebars-template">
<div class="modal-header"><span>Workflow</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table"><thead>
				<tr>
					<th >Response </th>
					<th >Create Time</th>
					<th> Status</th>
				</tr></thead>
				<tbody>
		{{#each workflow}}
				<tr>
					<td>{{responseData}}</td>
					<td>{{createDateTime}}</td>
					<td>{{status}}</td>
				</tr>
		{{/each}}
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
</div>
</div>

	</script>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudGEMInvoice$ = null;
		var tplWorkFlow;
		$(document).ready(function() {
			tplWorkFlow=Handlebars.compile($('#tplWorkFlow').html());
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(GEMInvoiceBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "geminvoices",
					autoRefresh: true,
					tableConfig: {
						columnDefs: [ {
					        "targets": -1,
					        "render": function ( data, type, row ) {
					        	var lHtml = '<div class="btn-group">'
									+'<button type="button" class="btn btn-xs form-action-handler" data-action="resend" style="background-color: #e87523;"><span class="fa fa-repeat"> Resend</span></button>'
									+'<button type="button" class="btn btn-primary btn-xs form-action-handler" data-action="workflow"><span class="fa fa-paper-plane"> Workflow</span></button>'
									+'<button type="button" class="btn btn-danger btn-xs form-action-handler" data-action="closed"><span class="fa fa-window-close"> Closed</span></button></div>'
			                    return lHtml;
			                },
					    }],
						formActionHandler:function(pEvent, pData, pAction) {
					    	if (pAction == 'resend') {
					    		if (pData.status == '<%=Status.Closed.toString()%>' || 
					    				pData.status == '<%=Status.Success.toString()%>'){
					    			alert('Invalid Instrument Status');
					    		}else{
					    			resend(pData)
					    		}
					    	}else if (pAction == 'workflow') {
					    		workflow(pData.id)
					    	}else if (pAction == 'closed') {
					    		if (pData.status == '<%=Status.Pending.toString()%>'){
					    			alert('Invalid Instrument Status');
					    		}else{
					    			markAsClosed(pData.id);
					    		}
					    	}
					    }
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
			crudGEMInvoice$ = $('#contGEMInvoice').xcrudwrapper(lConfig);
			crudGEMInvoice = crudGEMInvoice$.data('xcrudwrapper');
		});
		function resend(pData){
			var lData = {};
			lData['arrId']= pData.arrId;
			lData['id']= pData.id;
			lData['creator'] = pData.creator;
			$.ajax({
		        url: 'v1/gem/addinvoice',
		        data: JSON.stringify(lData),
		        async : false,
		        type: 'POST',
		        success: function( pObj, pStatus, pXhr) {
		        	crudGEMInvoice.searchHandler();
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	alert(pObj.responseJSON.messages[0]);
		        }
		    });
		}
		
		function workflow(pId){
			$.ajax({
		        url: 'geminvoices/gemresendworkflow/'+pId,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlWorkFlow .modal-content').html(tplWorkFlow(pObj));
		        	showModal($('#mdlWorkFlow'));
		        },
		        error: function( pObj, pStatus, pXhr) {
		        }
		    });
		}
		
		function markAsClosed(pId){
			$.ajax({
		        url: 'geminvoices/markAsClosed/'+pId,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	crudGEMInvoice.searchHandler();
		        },
		        error: function( pObj, pStatus, pXhr) {
		        }
		    });
		}
	</script>


</body>
</html>