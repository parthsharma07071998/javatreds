<!DOCTYPE html>
<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@page import="groovy.ui.Console"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargePlanBean"%>
<%@page import="com.xlx.treds.master.bean.AuctionChargeSlabBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.MemberwisePlanBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
String lType = (String)request.getParameter("type");
%>
<html>
	<head>
		<title>Memberwise Plan </title>
		 <%@include file="includes1.jsp" %>
		 <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
<style type="text/css">
#mdlAucPlan .modal-dialog  {width:900px;}
</style>
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Memberwise Plan" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contMemberwisePlan">		
		<div id="frmSearch">
				<div class="page-title">
				<div class="title-env">
					<h1 class="title">
						Entity Auction Plan
					</h1>
				</div>
			</div>
			<!-- <header>Memberwise Plan</header> -->
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="type" class="label">Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="type"><option value="">Select Entity Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="code"><option value="">Select Entity Code</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>	
					<div class="row">
						<div class="col-sm-2"><section><label for="effectiveStartDate" class="label">Effective From :</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="effectiveStartDate" placeholder="Effective From " data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="acpId" class="label">Plan Name:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="acpId"><option value="">Select Charge Plan</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
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
					<span class="right_links">
						<a class="secure btn-group0" href="javascript:;" data-seckey="memplan-save" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> Add New</a>
						<a 	href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
						<a class="secure btn-group0" href="javascript:;" data-seckey="memplan-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
						<a class="right_links" href="javascript:getPlan();" data-seckey="memplan-view" id=btnViewPlan><span class="glyphicon glyphicon-eye-open"></span> View Plan</a>
						<a></a>
					</span>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="acpId">Id</th>
									<th data-width="100px" data-name="code">Code</th>
									<th data-width="100px" data-name="type">Entity Type</th>
									<th data-width="100px" data-name="cdName">Company Name</th>
									<th data-width="100px" data-name="acpName">Plan Name</th>
									<th data-width="100px" data-name="effectiveStartDate">Effective From</th>
									<th data-width="100px" data-name="effectiveEndDate">Effective To</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<div class="modal fade" id="mdlAucPlan" tabindex=-1><div class="modal-dialog" ><div class="modal-content">
		</div></div></div>
		
		
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">
						<% if (lType != null && lType.equals(AppEntityBean.EntityType.Financier.getCode())){
						 %>	
						Financier
						<%
 						}else{
						%>	
						Buyer 
						<%}%>
						Auction Plan
					</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
	    		<div>
					<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="code"><option value="">Select Entity Code</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="" class="label">Effective From :</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="effectiveStartDate" placeholder="Effective From " data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="acpId" class="label">Plan Name:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="acpId"><option value="">Select Charge Plan</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>	
<% 
if (lType == null ){	
%>
					
					<div class="row">
						<div class="col-sm-2">
							<section>
							<label for="financierBearShare" class="label">Financier Bear Charge</label>
							</section>
						</div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="financierBearShare" onclick="showFinShare(false)"><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
					</div>	
<%
}
%>
				<div id="divfinancierShareCheck">
					<div class="row">
						<div class="col-sm-2"><section><label for="financierShare" class="label">Financier Charge</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="financierShare" placeholder="Financier Share" onchange="setSellerCharge()">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>	
					<div class="row" align="center">
						<h4>Calculation</h4>
						<div id="calculations"></div>
					</div>	
				</div>
					<div class="panel-body bg_white">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
					</fieldset>
    			</div>
   		 	</div>
    	</div>
    </div>
    
    
    	<script id="tplAucPlanSlabView" type="text/x-handlebars-template">
<div class="modal-header"><span>Auction Charge Plan & Slabs </span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>
		<div class="row">
			<div class="col-sm-4 col-sm-offset-2">
				<h3><b>Plan Name : {{name}}<b></h3>
			</div>
			<div class="col-sm-4 col-sm-offset-2">
				<h3><b>Plan Type : 
						{{#ifCond type '==' "<%=AuctionChargePlanBean.Type.Invoice.getCode()%>"}}Invoice{{/ifCond}}
						{{#ifCond type '==' "<%=AuctionChargePlanBean.Type.TurnOver.getCode()%>"}}Turnover{{/ifCond}}
				<b></h3>
			</div>
		</div>
		
		<div class="row">
			<div class="col-sm-12">
				
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-striped table-bordered" style="width:100%"><tbody>
				<tr>
					<th>Min Amount</th>
					<th>Max Amount</th>
					<th>Charge Type</th>
					<th>Charge Absolute Value</th>
					<th>Charge Percent Value</th>
					<th>Charge Max Value</th>
				</tr>
				{{#each auctionChargeSlabList}}
				<tr>
					<td>{{minAmount}}</td>
					<td>{{maxAmount}}</td>
					<td>
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Absolute.getCode()%>"}}Absolute{{/ifCond}}
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Percentage.getCode()%>"}}Percentage{{/ifCond}}		
						{{#ifCond chargeType '==' "<%=AuctionChargeSlabBean.ChargeType.Threshold.getCode()%>"}}Threshold{{/ifCond}}
					</td>
					<td>{{chargeAbsoluteValue}}</td>
					<td>{{chargePercentValue}}</td>
					<td>{{chargeMaxValue}}</td>
				</tr>
				{{/each}}
				</tbody></table>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-info btn-lg btn-close" data-dismiss="modal">OK</button>
				</div>
			</div>
		</div>
</fieldset>
</div>
	</script>
    
	<script id="tplChargeCal" type="text/x-handlebars-template">
			<table class="table table-striped table-bordered" style="width:100%"><tbody>
				<tr>
					<th>Invoice Min</th>
					<th>Invoice max</th>
					<th>Platform Charges</th>
					<th>Fin Split</th>
					<th>Other Bearer Split</th>
					<th>Min Txn Amt</th>
				</tr>
				{{#each slabs}}
				<tr>  
					<td>{{minAmount}}</td>
					<td>{{maxAmount}}</td>
					<td>{{chargePercent}}</td>
					<td>{{finChargePercent}}</td>
					<td>{{otherChargePercent}}</td>
					<td>{{minCharge}}</td>
				</tr>
				{{/each}}
			</table>
	</script>
    
   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>
	<script type="text/javascript">
		var crudMemberwisePlan$ = null;
		var tplAPSView,tplChargeCal;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(MemberwisePlanBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "memberwiseplan",
					keyFields:["code","effectiveStartDate"],
					autoRefresh: true,
					preModifyHandler : function(pData) {
						var lDateFormatter = new SimpleDateFormat(FORMAT_DATE);
						pData.effectiveStartDate=lDateFormatter.formatDate(pData.effectiveStartDate);
						return true;
					},
					postModifyHandler : function(pData) {
						showFinShare(false);
						$('#calculations').html(tplChargeCal(pData));
						return true;
					},
					postNewHandler: function() {
						$('#divfinancierShareCheck').hide();
						var lFields = ['financierShare'];
						mainForm.enableDisableField(lFields,false, false);
						return true;
					},
					preSearchHandler: function(pFilter){
						<%  

						if (lType != null && lType.equals(AppEntityBean.EntityType.Financier.getCode())){
							
						%>
							pFilter.type = '<%=lType%>';
						<%
						}
						%>
						return true;
					},
<%
if (lNewBool) {
%>					new: true,
<%
} else if (lModify != null) {
%>					modify: <%=lModify%>,
<%
}
%>			
};
			lConfig = $.extend(lConfig, lFormConfig);

<%  

if (lType != null && lType.equals(AppEntityBean.EntityType.Financier.getCode())){
	
%>
		$.each(lConfig.fields,function(pIdx,pVal){
			 if (pVal.name == 'code') {
					 pVal.dataSetValues="appentity/financiers";
		 }
 });
<%
}
%>	
			crudMemberwisePlan$ = $('#contMemberwisePlan').xcrudwrapper(lConfig);
			crudMemberwisePlan = crudMemberwisePlan$.data('xcrudwrapper');
			mainForm = crudMemberwisePlan.options.mainForm;
			searchForm=crudMemberwisePlan.options.searchForm
			tplAPSView = Handlebars.compile($('#tplAucPlanSlabView').html());		
			tplChargeCal = Handlebars.compile($('#tplChargeCal').html());
			$("#btnDownloadCSV").on("click",function(){
				var lFilter=searchForm.getValue(true);
				lFilter['columnNames'] = crudMemberwisePlan.getVisibleColumns();
				downloadFile('memberwiseplan/all',null,JSON.stringify(lFilter));
			});
			$('#financierShare').on('input', function() {
				if (mainForm.getField('acpId').getValue()==null){
					alert("Please select a plan first");
					return;
				}else{
					if (mainForm.getField('financierShare').getValue()==null){
						alert("Please set financier share.");
						return;
					}else{
						$.ajax( {
					        url: 'memberwiseplan/cal/'+mainForm.getField('acpId').getValue()+'/'+mainForm.getField('financierShare').getValue(),
					        type: "GET",
					        success: function( pObj, pStatus, pXhr){
					        	$('#calculations').html(tplChargeCal(pObj));
					        },
					    	error: errorHandler
					    });
					}
				}
			});
		});
		
		function getPlan(){
			var lSelected = crudMemberwisePlan.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			$.ajax( {
		        url: 'auctionchargeplans/'+lSelected.data()["acpId"],
		        type: "GET",
		        success: function( pObj, pStatus, pXhr){
		        	var lModal$ = $('#mdlAucPlan');
		        	lModal$.find('.modal-content').html(tplAPSView(pObj));
		        	showModal(lModal$);
		        },
		    	error: errorHandler
		    });
		}
		
		function showFinShare(pClear){
			if($('#financierBearShare').prop('checked') == true){
				$('#divfinancierShareCheck').show();
				var lFields = ['financierShare'];
				mainForm.enableDisableField(lFields,true, pClear);
			}else{
				$('#divfinancierShareCheck').hide();
			}
		}


	
	</script>
</body>
</html>