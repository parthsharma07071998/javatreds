<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.BidBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Auctions</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/jquery.autocomplete.css" rel="stylesheet">
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <style>
        .clsReserved {
			color:red;
		}
		.depth-hdr {
			font-size:15px;
		}    
		.depth-hdr-dt {
			font-size:18px;
		}    
		.readable {
			margin-bottom: 20px;
		}
        </style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Auctions" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
    
    <!-- frmSearch -->
	<div id="frmSearch">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Auctions</h1>
			</div>
		</div>
		<div>
			<div class="cloudTabs">
				<ul class="cloudtabs nav nav-tabs">
					 <li><a href="#tab0" data-toggle="tab">Ready for Auction <span id="badge0" class="badge bg-green"></span></a></li>
					 <li><a href="#tab1" data-toggle="tab">In Auction <span id="badge1" class="badge bg-green"></span></a></li>
					 <li><a href="#tab2" data-toggle="tab">Auctioned <span id="badge2" class="badge bg-blue"></span></a></li>
					 <li><a href="#tab3" data-toggle="tab">On Hold <span id="badge3" class="badge bg-red"></span></a></li>
					 <li><a href="#tab4" data-toggle="tab">History <span id="badge4" class="badge bg-red"></span></a></li>
				</ul>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<span class="right_links">
						<a class="secure btn-group0 hidden" href="javascript:;" data-seckey="factunitsp-activate" onClick="javascript:updateStatus(null, 'activate','send for auction', 0, $(this))"><span class="fa fa-check"></span> Send For Auction</a>
						<a class="secure btn-group0 hidden" href="javascript:;" data-seckey="factunitsp-withdraw" onClick="javascript:updateStatus(null, 'withdraw', 'withdraw', 0, $(this))"><span class="fa fa-remove"></span> Withdraw</a>
						<a class="secure btn-group1 hidden" href="javascript:;" data-seckey="factunitsp-acceptbid" onClick="javascript:acceptBid(null,null,null,$(this),false)"><span class="fa fa-check"></span> Accept Selected</a>
						<a class="secure btn-group1 hidden" href="javascript:;" data-seckey="factunitsp-suspend" onClick="javascript:updateStatus(null, 'suspend', 'supsend', 1, $(this))"><span class="fa fa-pause"></span> On Hold</a>
						<a class="secure btn-group1 hidden" href="javascript:;" data-seckey="factunitsp-withdraw" onClick="javascript:updateStatus(null, 'withdraw', 'withdraw', 1, $(this))"><span class="fa fa-remove"></span> Withdraw</a>
						<a class="secure btn-group3 hidden" href="javascript:;" data-seckey="factunitsp-activate" onClick="javascript:updateStatus(null,'activate','activate',3, $(this))"><span class="fa fa-plus"></span> Activate</a>
						<a class="secure btn-group3 hidden" href="javascript:;" data-seckey="factunitsp-withdraw" onClick="javascript:updateStatus(null, 'withdraw','withdraw', 3, $(this))"><span class="fa fa-remove"></span> Withdraw</a>
						<a class="secure btn-group4 hidden" href="javascript:;" data-seckey="factunitsp-withdraw" onClick="javascript:showHistoryFilter();" ><span class="fa fa-filter" id='filter' style='color:black'></span> Filter</a>
						<a href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
						<a class="btn-enter" href="javascript:;" id=btnRefresh><span class="fa fa-refresh"></span> Refresh</a>
					</span>
				</div>
			</div>
		</div>
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row  btn-group0">
					<div class="col-sm-12">
						<div id="tab0" class="tab-pane active">
		           		</div>
					</div>
				</div>
				<div class="row  btn-group1 hidden">
					<div class="col-sm-12">
						<div id="tab1" class="tab-pane active btn-group1">
		           		</div>
					</div>
				</div>
				<div class="row btn-group2 hidden">
					<div class="col-sm-12">
						<div id="tab2" class="tab-pane active">
		           		</div>
					</div>
				</div>
				<div class="row btn-group3 hidden">
					<div class="col-sm-12">
						<div id="tab3" class="tab-pane active">
		           		</div>
					</div>
				</div>
				<div class="row btn-group4 hidden">
					<div class="col-sm-12">
						<div id="tab4" class="tab-pane active">
		           		</div>
					</div>
				</div>
			</fieldset>
		</div>
	</div>
	<!-- frmSearch -->
    
    <p>
    </p>
	<div class="modal fade" id="mdlDepth" tabindex=-1><div class="modal-dialog  modal-xl"><div class="modal-content">
	</div></div></div>

	<!-- frmMain -->
	<div style="display:none" id="frmMain">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Auctions</h1>
			</div>
		</div>
		<div class="xform box">
		</div>
	</div>
	<!-- frmMain -->


	<div class="modal fade" id="mdlHistoryFilter" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;History Filter</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmHistoryFilter">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="id" class="label">Factoring Unit:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="id" placeholder="Factoring Unit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="maturityDate" class="label">Start Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="maturityDate" placeholder="Start Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="filterMaturityDate" class="label">End Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="filterMaturityDate" placeholder="End Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="purchaserRow">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="supplierRow">
					<div class="col-sm-4"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="acceptingEntity" class="label">Financier:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="acceptingEntity"><option value="">Select Financier</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-4"><section><label for="type" class="label">Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-4"><section><label for="txnType" class="label">Txn Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="txnType"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-8">
						<section class="input select">
						<input type="text" id="status" placeholder="Select Status" data-role="xautocompletefield" data-others="false"/>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-4"><section><label for="pfId" class="label">Pay File Id:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="pfId" placeholder="Pay File Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				
				
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnFilter><span class="fa fa-save"></span> Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>


   	<%@include file="footer1.jsp" %>
   	<%@include file="pluginaucsumm.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>
   	
   	<script id="tpl0" type="text/x-handlebars-template">
<table class="table table-hover"><thead><tr>
	 <th><input type=checkbox class="selectAll"></th>
    <th>ID</th>
    <th>Due Date</th>
    <th style="text-align:right">Amount</th>
    <th class="state-S state-T">Buyer</th>
    <th class="state-P state-T">Seller</th>
	<th>Cost Bearer</th>
	<th>Auto Accept</th>
    <th>&nbsp;</th>
   </tr></thead>
   <tbody>
{{#each this}}
<tr>
<td>{{#if auctionRights}}<input type=checkbox class="chkRow" value="{{id}}">{{/if}}</td>
<td><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{id}}')" title="View Factoring Unit" class="cust_link" {{/ifHasAccess}}  style="color:blue !important">{{id}}</a></td>
{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{extendedDueDate}}</td>
{{/ifCond}}
{{#ifCond enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{maturityDate}}</td>
{{/ifCond}}
<td style="text-align:right"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amount}}{{/formatDec}}</td>
<td class="state-S state-T">{{purName}}</td>
<td class="state-P state-T">{{supName}}</td>
<td>{{costBearerDesc}}</td>
<td>{{autoAccept}}</td>
<td>
{{#if auctionRights}}
	<div class="btn-group pull-right">
		{{#ifHasAccess "factunitsp-activate"}}<button type="button" class="btn btn-sm btn-default" onClick="javascript:updateStatus('{{id}}','activate','send for auction',0, $(this))"><span class="fa fa-check"></span> Send For Auction</button>{{/ifHasAccess}}
		<button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></button>
		<ul class="dropdown-menu">
			{{#ifHasAccess "factunitsp-withdraw"}}<li><a href="#" onClick="javascript:updateStatus('{{id}}','withdraw','withdraw',0)"><span class="fa fa-remove"></span> Withdraw</a></li>{{/ifHasAccess}}
		</ul>
	</div>
{{/if}}


</td>
</tr>
{{/each}}
</tbody></table>
</script>

<script id="tpl1" type="text/x-handlebars-template">
<table class="table table-hover"><thead><tr>
	 <th><input type=checkbox class="selectAll"></th>
    <th>ID</th>
    <th>Due Date</th>
    <th style="text-align:right">Amount</th>
    <th class="state-S state-T">Buyer</th>
    <th class="state-P state-T">Seller</th>
	<th>Cost Bearer</th>
    <th style="text-align:right">Best Bid</th>
    <th style="text-align:right">Haircut</th>
	<th>Auto Accept</th>
    <th>&nbsp;</th>
   </tr></thead>
   <tbody>
{{#each this}}
<tr>
<td>{{#if owner}}<input type=checkbox class="chkRow" value="{{id}}">{{/if}}</td>
<td><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{id}}')" title="View Factoring Unit" class="cust_link" {{/ifHasAccess}} style="color:blue !important">{{id}}</a></td>
{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{extendedDueDate}}</td>
{{/ifCond}}
{{#ifCond enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{maturityDate}}</td>
{{/ifCond}}
<td style="text-align:right"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amount}}{{/formatDec}}</td>
<td class="state-S state-T">{{purName}}</td>
<td class="state-P state-T">{{supName}}</td>
<td>{{costBearerDesc}}</td>
<td style="text-align:right">{{#formatDec}}{{acceptedRate}}{{/formatDec}}
{{#ifHasAccess "factunitfin-depth"}}<button class="btn btn-default btn-sm" onClick="javascript:showDepth('{{id}}')" title="Depth"><span class="fa  fa-chevron-down"></button>{{/ifHasAccess}}
</td>
<td style="text-align:right">{{#formatDec}}{{acceptedHaircut}}{{/formatDec}}</td>
<td>{{autoAccept}}</td>
<td>
{{#if ownerOrAuctionRights}}
	<div class="btn-group pull-right">
		{{#if owner}}			
		{{#ifHasAccess "factunitsp-acceptbid"}}<button type="button" class="btn btn-sm btn-default" onClick="javascript:acceptBid('{{id}}',{{bdId}},null,$(this),false)"><span class="fa fa-check"></span> Accept</button>{{/ifHasAccess}}
		{{/if}}
		<button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></button>
		<ul class="dropdown-menu">
		{{#if auctionRights}}			
			{{#ifHasAccess "factunitsp-suspend"}}<li><a href="#" onClick="javascript:updateStatus('{{id}}','suspend','put On Hold',1)"><span class="fa fa-pause"></span> On Hold</a></li>{{/ifHasAccess}}
			{{#ifHasAccess "factunitsp-withdraw"}}<li><a href="#" onClick="javascript:updateStatus('{{id}}','withdraw','withdraw',1)"><span class="fa fa-remove"></span> Withdraw</a></li>{{/ifHasAccess}}
		{{/if}}
		</ul>
	</div>
{{/if}}
</td>
</tr>
{{/each}}
</tbody></table>
<p></p>
<p>
<b>Authorisation to RXIL:</b> In the event the bid is accepted and settled, RXIL is authorized by Assignor to send the Notice of assignment to Assignee.
</p>
	</script>

   	<script id="tpl2" type="text/x-handlebars-template">
<table class="table table-hover">
    <thead><tr>
        <th>ID</th>
        <th>Due Date</th>
        <th style="text-align:right">Amount</th>
        <th class="state-S state-T">Buyer</th>
        <th class="state-P state-T">Seller</th>
        <th>Financier</th>
        <th>Cost Bearer</th>
        <th style="text-align:right">Accepted Rate</th>
        <th style="text-align:right">Haircut</th>
        <th style="width:40px"></th>
       </tr></thead>
       <tbody>
{{#each this}}
<tr>
<td><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{id}}')" title="View Factoring Unit" class="cust_link" {{/ifHasAccess}} style="color:blue !important">{{id}}</a></td>
{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{extendedDueDate}}</td>
{{/ifCond}}
{{#ifCond enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{maturityDate}}</td>
{{/ifCond}}
<td style="text-align:right"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amount}}{{/formatDec}}</td>
<td class="state-S state-T">{{purName}}</td>
<td class="state-P state-T">{{supName}}</td>
<td>{{finName}}</td>
<td>{{costBearerDesc}}</td>
<td style="text-align:right">{{#formatDec}}{{acceptedRate}}{{/formatDec}}</td>
<td style="text-align:right">{{#formatDec}}{{acceptedHaircut}}{{/formatDec}}</td>
<td></td>
</tr>
{{/each}}
</tbody></table>

	</script>
   	<script id="tpl3" type="text/x-handlebars-template">
   <table class="table table-hover">
       <thead><tr>
       	 <th><input type=checkbox class="selectAll"></th>
           <th>ID</th>
           <th>Due Date</th>
           <th style="text-align:right">Amount</th>
           <th class="state-S state-T">Buyer</th>
           <th class="state-P state-T">Seller</th>
           <th>
           </th>
       </tr></thead>
       <tbody>
{{#each this}}
<tr>
<td>{{#if owner}}<input type=checkbox class="chkRow" value="{{id}}">{{/if}}</td>
<td><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{id}}')" title="View Factoring Unit" class="cust_link" {{/ifHasAccess}} style="color:blue !important">{{id}}</a></td>
{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{extendedDueDate}}</td>
{{/ifCond}}
{{#ifCond enableExtension '!=' "<%=CommonAppConstants.Yes.Yes.getCode()%>"}} 
<td>{{maturityDate}}</td>
{{/ifCond}}
<td style="text-align:right"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amount}}{{/formatDec}}</td>
<td class="state-S state-T">{{purName}}</td>
<td class="state-P state-T">{{supName}}</td>
<td>
{{#if owner}}
	<div class="btn-group pull-right">
		{{#ifHasAccess "factunitsp-activate"}}<button type="button" class="btn btn-sm btn-default" onClick="javascript:updateStatus('{{id}}','activate','activate',3, $(this))"><span class="fa fa-plus"></span> Activate</button>{{/ifHasAccess}}
		{{#ifHasAccess "factunitsp-withdraw"}}<button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></button>
		<ul class="dropdown-menu">
			<li><a href="#" onClick="javascript:updateStatus('{{id}}','withdraw','withdraw',3)"><span class="fa fa-remove"></span> Withdraw</a></li>
		</ul>{{/ifHasAccess}}
	</div>
{{/if}}
</td>
</tr>
{{/each}}
    </tbody></table>
	</script>
	
	
<script id="tpl4" type="text/x-handlebars-template">
<table class="table table-hover">
    <thead><tr>
        <th>ID</th>
        <th>Factored Date</th>
        <th style="text-align:right">Amount</th>
        <th class="state-S state-T">Buyer</th>
        <th class="state-P state-T">Seller</th>
        <th>Financier</th>
        <th>Cost Bearer</th>
        <th style="text-align:right">Accepted Rate</th>
        <th style="text-align:right">Haircut</th>
        <th style="width:40px"></th>
       </tr></thead>
       <tbody>
{{#each this}}
<tr>
<td><a {{#ifHasAccess "instview-fu"}}href="javascript:viewFU('{{id}}')" title="View Factoring Unit" class="cust_link" {{/ifHasAccess}} style="color:blue !important">{{id}}</a></td>
<td>{{acceptDateTime}}</td>
<td style="text-align:right"><span class="fa fa-cust{{currency}}"></span> {{#formatDec}}{{amount}}{{/formatDec}}</td>
<td class="state-S state-T">{{purName}}</td>
<td class="state-P state-T">{{supName}}</td>
<td>{{finName}}</td>
<td>{{costBearerDesc}}</td>
<td style="text-align:right">{{#formatDec}}{{acceptedRate}}{{/formatDec}}</td>
<td style="text-align:right">{{#formatDec}}{{acceptedHaircut}}{{/formatDec}}</td>
<td></td>
</tr>
{{/each}}
</tbody></table>

	</script>
	
   	<script id="tplMessage" type="text/x-handlebars-template">
<table class="table table-bordered">
<tbody><tr>
    <th style="width:140px">Id</th>
    <th>Message</th>
</tr>
{{#each this}}
<tr>
    <td>{{id}}</td>
    <td>{{#if error}}<span class="fa fa-remove text-danger"></span> {{error}}{{else}}<span class="fa fa-check-circle text-success"></span> Done{{/if}}</td>
</tr>
{{/each}}
</tbody></table>
	</script>
   	<script id="tplDepth" type="text/x-handlebars-template">
<div class="modal-header"><span>Bid Details</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
<div class="modal-body">
<fieldset>

<p></p>
<div class="col-sm-12">
<div class="row"><div class="col-sm-2">Factoring Unit ID:</div><div class="col-sm-4 depth-hdr"><b>{{id}}</b></div><div class="col-sm-2" >Factoring Unit Amount:</div><div class="col-sm-4 depth-hdr"><b><span class="fa fa-cust{{currency}}"></span>{{#formatDec}} {{amount}}{{/formatDec}}</b></div></div>
</div>
<div class="col-sm-12 readable">
<div class="row"><div class="col-sm-2">Buyer Name:</div><div class="col-sm-4 depth-hdr"><b>{{purName}}</b></div><div class="col-sm-2" >Seller Name:</div><div class="col-sm-4 depth-hdr"><b>{{supName}}</b></div></div>
</div>
<div class="col-sm-12">
<div class="row"><div class="col-sm-2">Auction Start Date</div><div class="col-sm-4 depth-hdr-dt"><b>{{factorStartDateTime}}</b></div><div class="col-sm-2" >Leg 1 Date:</div><div class="col-sm-4 depth-hdr-dt"><b>{{settleDate}}</b></div></div>
</div>
<div class="col-sm-12 readable">
<div class="row"><div class="col-sm-2">Leg 2 Date:</div><div class="col-sm-4 depth-hdr-dt"><b>{{#ifCond enableExtension '==' "Yes"}}{{extendedDueDate}}{{/ifCond}}{{#ifCond enableExtension '!=' "Yes"}}{{maturityDate}}{{/ifCond}}</b></div><div class="col-sm-2" >Leg 3 Date:</div><div class="col-sm-4 depth-hdr-dt"><b>{{#ifCond settleLeg3FlagDesc '==' "Yes"}}{{statDueDate}}{{/ifCond}}</b></div></div>
</div>
<div class="col-sm-12">
<div class="row"><div class="col-sm-2">Auto Accept Bid:</div><div class="col-sm-4 depth-hdr"><b>{{autoAcceptDesc}}</b></div><div class="col-sm-2" >Leg 3 Settlement:</div><div class="col-sm-4 depth-hdr"><b>{{settleLeg3FlagDesc}}</b></div></div>
</div>
<div class="col-sm-12">
<div class="row"><div class="col-sm-2">Auto Acceptable Bid Type:</div><div class="col-sm-4 depth-hdr"><b>{{autoAcceptableBidTypesDesc}}</b></div><div class="col-sm-2">Platform Charges bearer:</div><div class="col-sm-4 depth-hdr"><b>{{chargeBearer}}</b></div></div>
</div>
<div class="col-sm-12 readable">
<div class="row"><div class="col-sm-2">Cost Bearer:</div><div class="col-sm-4 depth-hdr"><b>{{costBearingType}}</b></div></div>
</div>


<p></p><table class="table table-hover"><thead>
<tr>
<th style="text-align:right" width="100px">Rate %</th>
<th style="text-align:right" width="100px">Haircut %</th>
<th style="text-align:right" width="100px">Cost Leg</th>
<th style="text-align:right">Leg 1 Amount <span class="fa fa-cust{{currency}}"></span></th>
<th style="text-align:right">Leg 2 Amount <span class="fa fa-cust{{currency}}"></span></th>
{{#ifCond settleLeg3FlagDesc '==' "Yes"}}
<th style="text-align:right">Leg 3 Amount <span class="fa fa-cust{{currency}}"></span></th>
{{/ifCond}}
<th style="text-align:left">Time</th>
{{#if owner}}
<th style="text-align:left"  width="100px">Acceptance</th>
{{/if}}
</tr>
</thead><tbody>
{{#each depth}}
<tr>
    <td style="text-align:right" class="cls{{bidType}}" title="{{bidType}}">{{#formatDec}}{{rate}}{{/formatDec}}</td>
    <td style="text-align:right">{{#if haircut}}{{#formatDec}}{{haircut}}{{/formatDec}}{{else}}-{{/if}}</td>
    <td style="text-align:right">{{costLeg}}</td>
    <td style="text-align:right">{{#if leg1}}{{#formatDec}}{{leg1}}{{/formatDec}}{{else}}-{{/if}}</td>
    <td style="text-align:right">{{#if leg2}}{{#formatDec}}{{leg2}}{{/formatDec}}{{else}}-{{/if}}</td>
	{{#ifCond ../settleLeg3FlagDesc '==' "Yes"}}
    <td style="text-align:right">{{#if leg3}}{{#formatDec}}{{leg3}}{{/formatDec}}{{else}}-{{/if}}</td>
    {{/ifCond}}
	<td>{{timestamp}}</td>
{{#if ../owner}}
	<td style="width:60px">
{{#if bidType}}
	{{#ifHasAccess "factunitsp-acceptbid"}}<button type="button" class="btn btn-sm btn-default" onClick="javascript:acceptBid('{{../../../../fuId}}',{{id}},{{rate}},$(this),true)"><span class="fa fa-check-circle"></span> Accept</button>{{/ifHasAccess}}
{{else}}
	{{../../../autoAcceptDesc}}
{{/if}}
	</td>
{{/if}}
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
	<script type="text/javascript">
	var allData,index;
	var templates,tplMessage,tplDepth;
	var activeTab=null;
	var historyData=null;
	var HISTORY_INDEX = 4;
	var histFilterForm$ = null, histFilterForm = null;
	var strHistoryFilters=null;
	var historyFilters = null;
	var historyData = null;

	$(document).ready(function() {
		templates=[];
		for (var lPtr=0;lPtr<5;lPtr++)
			templates[lPtr] = Handlebars.compile($('#tpl'+lPtr).html());
		tplMessage = Handlebars.compile($('#tplMessage').html());
		tplDepth = Handlebars.compile($('#tplDepth').html());
		$('#btnRefresh').on('click', function() {
			var lTabIdx = -1;
			if(activeTab!=null){
				lTabIdx = parseInt(activeTab.substring(4));
			}
			if(activeTab==null || lTabIdx < HISTORY_INDEX){
				getData();
			}else{
				getHistoryData();
			}
		});
		$('.nav-tabs a').on('shown.bs.tab', function(event){
		    var lRef1 = $(event.target).attr('href');         // active tab
		    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
		    var lTabIdx = parseInt(lRef1.substring(4));
		    if (lRef2)
		    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
		    $('.btn-group'+lTabIdx).removeClass('hidden');
		    activeTab = lRef1;
		});
		
		$('.nav-tabs a:first').tab('show');
		getData();
				
		$('#btnDownloadCSV').on('click', function() {
			var lTabIdx = -1;
			if(activeTab!=null){
				lTabIdx = parseInt(activeTab.substring(4));
			}
			if(activeTab==null || lTabIdx < HISTORY_INDEX){
				downloadFile('factunitsp/all',null,JSON.stringify({"tab" : lTabIdx}));
			}else{
				var lFiltData = histFilterForm.getValue(true);
				if ($.isEmptyObject(lFiltData)) {
					alert("Please provide at least one filter for searching old Factored Units.");
					return;
				}
				downloadFile('factunitsp/history',null,JSON.stringify(lFiltData));
			}
		});
		
		var lHistConfig = {
			"fields": [
					{
						"name": "id",
						"fieldType":"PRIMARY",
						"dataType":"INTEGER",
					},
					{
						"name":"purchaser",
						"label":"Buyer",
						"dataType":"STRING",
						"maxLength": 30,
						"dataSetType":"RESOURCE",
						"dataSetValues":"appentity/purchasers"
					},
					{
						"name":"supplier",
						"label":"Seller",
						"dataType":"STRING",
						"maxLength": 30,
						"dataSetType":"RESOURCE",
						"dataSetValues":"appentity/suppliers"
					},
					{
						"name":"acceptingEntity",
						"label":"Financier",
						"dataType":"STRING",
						"maxLength": 10,
						"dataSetType":"RESOURCE",
						"dataSetValues":"appentity/financiers"
					},
					{
						"name":"maturityDate",
						"label":"Factoring Start Date",
						"dataType":"DATE"
					},
					{
						"name":"filterMaturityDate",
						"label":"Factoring End Date",
						"dataType":"DATE"
					},
			   		{
						"name":"txnType",
						"label":"Transaction Type",
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Debit", "value":"D"},{"text":"Credit", "value":"C"}],
						"desc":"Type of transaction, whether Debit/Credit."
					},
					{
						"name":"type",
						"label":"Type",
						"dataType":"STRING",
						"maxLength": 3,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Leg 1", "value":"L1"},{"text":"Leg 2", "value":"L2"},{"text":"Leg 3", "value":"L3"}],
						"desc":"The Leg of the obligation transaction."
					},
					{
						"name":"status",
						"label":"Status",
						"dataType":"STRING",
						"maxLength": 3,
						"dataSetType": "STATIC",
						"dataSetValues": [{"text":"Created", "value":"CRT"},{"text":"Ready", "value":"RDY"},{"text":"Sent", "value":"SNT"},{"text":"Success", "value":"SUC"},{"text":"Failed", "value":"FL"},{"text":"Cancelled", "value":"CNL"}],
						"desc":"Current status of obligation."
					},
					{
						"name":"pfId",
						"label":"Pay File Id",
						"dataType":"INTEGER",
						"desc":"Internal id of the pay file uploaded."
					}
			]
		};
		histFilterForm$ = $('#frmHistoryFilter').xform(lHistConfig);
		histFilterForm = histFilterForm$.data('xform');
		//
		fillSupplierPurchaser();
		
		$('#btnFilter').on('click', function() {
			var lHistoryFilters = histFilterForm.getValue(true);
			strHistoryFilters = JSON.stringify(lHistoryFilters);
			if(strHistoryFilters!=null&&strHistoryFilters.length>2){
				historyFilters=lHistoryFilters;
			}
			getHistoryData();
        	$('#mdlHistoryFilter').modal('hide');
		});
		
		var lSeller = (loginData.entityType.substring(0,1) == 'Y');
		var lBuyer = (loginData.entityType.substring(1,2) == 'Y');
		
		$('#purchaserRow').attr('style',lSeller?'':'display:none');
		$('#supplierRow').attr('style',lBuyer?'':'display:none');
	});
	
	function getData() {
		$('#btnRefresh').prop('disabled',true);
		$.ajax( {
	        url: "factunitsp/all",
	        type: "POST",
	        success: function( pObj, pStatus, pXhr) {
	        	allData=pObj;
	        	index={};
	        	$.each(allData, function(pIndex, pValue) {
	        		index[pValue.id] = pIndex;
	        	});
	        	if(allData!=null && allData.length == HISTORY_INDEX){
	        		// add for history
	        		allData[HISTORY_INDEX] = null; 
		        	index[HISTORY_INDEX] = HISTORY_INDEX;
	        	}else if (historyData!=null && allData.length==0){
	        		for(var lPtr=0; lPtr<HISTORY_INDEX; lPtr++){
		        		allData.push(null);
	        		}
	        		allData[HISTORY_INDEX] = historyData; 
		        	index[HISTORY_INDEX] = HISTORY_INDEX;
	        	}	        	
	        	showData();
	        },
	        error: errorHandler,
	        complete: function() {
	        	$('#btnRefresh').prop('disabled',false);
	        }
		});
	}
	function showData() {
    	var lTabData=[];
		for (var lPtr=0;lPtr<templates.length;lPtr++)
			lTabData[lPtr]=[];
		$.each(allData,function(pIndex,pValue) {
			if (pValue !=null && pValue.tab != null && pValue.tab != HISTORY_INDEX){
    			lTabData[pValue.tab].push(pValue);
			}
    	});
 		//TODO: Tlike the above loop we will have to have a template loop
 		for (var lPtr=0;lPtr<templates.length;lPtr++) {
			$('#tab'+lPtr).html(templates[lPtr](lTabData[lPtr]));
			var lCount = lTabData[lPtr]?lTabData[lPtr].length:0;
			$('#badge'+lPtr).html(lCount<10?"0"+lCount:lCount);
		}
		$('.selectAll').change(function() {
			var lThis$ = $(this);
			var lChecked = lThis$.prop("checked")
			lThis$.parents('table').find('.chkRow').prop('checked', lChecked);
		});
		refreshState();
	}
	
	function getHistoryData() {
		if(strHistoryFilters!=null&&strHistoryFilters.length>2){
			$('#btnRefresh').prop('disabled',true);
			$.ajax( {
		        url: "factunitsp/history",
		        type: "POST",
		        data: strHistoryFilters,
		        success: function( pObj, pStatus, pXhr) {
		        	if(pObj!=null&&pObj.length>0){
			        	historyData=pObj;
		        	}else{
		        		historyData=null;
		        	}
		        	allData[HISTORY_INDEX]=historyData;
					historyFilters = histFilterForm.getValue(true); //save the valid filter for Refresh button
		        	showHistoryData();
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#btnRefresh').prop('disabled',false);
		        }
			});
		}else{
			alert("Please provide at least one filter for searching old Factored Units.");
		}
	}
	function showHistoryData() {
    	var lTabData=allData;
    	var lPtr=HISTORY_INDEX;
		$('#tab'+lPtr).html(templates[lPtr](lTabData[lPtr]));
		var lCount = lTabData[lPtr]?lTabData[lPtr].length:0;
		$('#badge'+lPtr).html(lCount<10?"0"+lCount:lCount);

		if(historyFilters!=null&&Object.keys(historyFilters).length>1){
			$('#filter').attr('style','color:red');
		}else{
			$('#filter').attr('style','color:black');
		}
		
		$('.selectAll').change(function() {
			var lThis$ = $(this);
			var lChecked = lThis$.prop("checked")
			lThis$.parents('table').find('.chkRow').prop('checked', lChecked);
		});
		refreshState();
	}
	
	function acceptBid(pId, pBidId, pBidRate, pBtn$ ,pSpecificBidAccept) {
		var lIds = getIds(pId, 1);
		if (lIds != null) {
			var lMsg = "You are about to accept " + (pBidRate==null?"best bid":("bid with rate "+pBidRate)) + " for following factoring units.<br><br>"+lIds.join(", ");
			confirm(lMsg, null, null, function(pConfirm){
				if (pConfirm) {
					var lData = [];
					$.each(lIds, function(pIndex, pValue) {
						var lBidRate = pBidRate;
						var lHaircut;
						var lBidId = pBidId;
						if ((pBidId==null) && (lBidRate==null)) {
							var lIdx = index[pValue];
							if (lIdx == null) {
								alert("Error while processing factoring unit " + pValue + ". Please refresh.");
								lData = null;
								return false;
							}
							lBidRate = allData[lIdx].acceptedRate;
							lBidId = allData[lIdx].bdId;
							if (lBidRate == null) {
								alert("No bid to accept for factoring instrument " + pValue);
								lData = null;
								return false;
							}
							lHaircut = allData[lIdx].acceptedHaircut;
						}
						lData.push({id: pValue, bdId: lBidId, acceptedRate: lBidRate, acceptedHaircut: lHaircut, specificBidAccept: pSpecificBidAccept});
					});
					if (lData == null) // some error occured in above loop
						return;
					if (pBtn$) pBtn$.prop('disabled',true);
				    public_vars.$pageLoadingOverlay.removeClass('loaded');
					$.ajax( {
			            url: 'factunitsp/acceptbid',
			            type: 'POST',
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
			            	$('#mdlDepth').modal('hide');
			            	var lMessage = tplMessage(pObj);
			            	alert(lMessage, "ACCEPT BIDS");
			            	$.each(pObj,function(pIndex,pValue){
			            		mergeData(pValue.data);
			            	});
			            	showData();
			            },
			        	error: errorHandler,
						complete: function() {
							if (pBtn$) pBtn$.prop('disabled',false);
						    public_vars.$pageLoadingOverlay.addClass('loaded');
						}
			        });
				}
			});
		}
	}
	function updateStatus(pId, pResource, pMsg, pTab, pBtn$) {
		var lIds = getIds(pId, pTab);
		if (lIds != null) {
			confirm("You are about to " + pMsg + " " + lIds.length + " instrument(s)", "Confirmation", null, function(pConfirm) {
				if (pConfirm) {
					var lData = [];
					$.each(lIds, function(pIndex, pValue) {
						lData.push({id:pValue});
					});
					if (pBtn$) pBtn$.prop('disabled',true);
					$.ajax( {
			            url: 'factunitsp/' + pResource,
			            type: 'POST',
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
			            	var lMessage = tplMessage(pObj);
			            	alert(lMessage, pResource.toUpperCase() + " INSTRUMENTS");
			            	$.each(pObj,function(pIndex,pValue){
			            		mergeData(pValue.data);
			            	});
			            	showData();
			            },
			        	error: errorHandler,
						complete: function() {
							if (pBtn$) pBtn$.prop('disabled',false);
						}
			        });
				}
			});
		}
	}
	function showDepth(pFuId) {
		$.ajax( {
	        url: "factunitfin/depth/" + pFuId,
	        type: "GET",
	        success: function( pObj, pStatus, pXhr) {
	        	var lModal$ = $('#mdlDepth');
	        	lModal$.find('.modal-content').html(tplDepth(pObj));
	        	showModal(lModal$);
	        },
	        error: errorHandler
		});
	}
	function mergeData(pRow) {
		var lIdx = index[pRow.id];
		if (lIdx == null) {
			allData.push(pRow);
			index[pRow.id] = allData.length-1;
		} else {
			allData.splice(lIdx, 1, pRow);
		}
	}
	function getIds(pId, pTabIdx) {
		if (pId == null) {
			var lIds = [];
			$("#tab" + pTabIdx + " .chkRow:checked").each(function() {
				lIds.push($(this).val());
			});
			if (lIds.length==0) {
				alert("Please select one or more factoring units using check boxes.");
				return null;
			}
			return lIds;
		} else if ($.isArray(pId))
			return pId;
		else 
			return [pId];
	}
	function viewFU(pId) {
		showRemote('fuview?fuid='+pId, "modal-xl", true, pId);
	}
	function showHistoryFilter(){
		histFilterForm.setValue(historyFilters); //set the last vaild filter.
		showModal($('#mdlHistoryFilter'));		
	}
	function fillSupplierPurchaser(){
		$.ajax({
            url: 'pursuplnk/lov',
            type: 'GET',
            success: function( pObj, pStatus, pXhr) {
            	purSupLink = pObj;
            	var lSup=[],lPur=[];
            	var lSupMap={},lPurMap={};
            	$.each(purSupLink,function(pIndex,pValue){
            		if (lSupMap[pValue.supplier]==null) {
            			lSupMap[pValue.supplier]=true;
            			var lDisp=pValue.supplier===loginData.domain?pValue.supName:(pValue.purchaserSupplierRef + '(' + pValue.supName + ')');
            			lSup.push({text:lDisp, value:pValue.supplier})
            		}
            		if (lPurMap[pValue.purchaser]==null) {
            			lPurMap[pValue.purchaser]=true;
            			var lDisp=pValue.purchaser===loginData.domain?pValue.purName:(pValue.supplierPurchaserRef + '(' + pValue.purName + ')');
            			lPur.push({text:lDisp, value:pValue.purchaser})
            		}
            	});
            	$.each([lSup,lPur],function(pIdx,pVal){
            		pVal.sort(function(a,b){
            			return a.text.toUpperCase().localeCompare(b.text.toUpperCase());
            		});
            	});
            	var lSupFld=histFilterForm.getField('supplier');
            	lSupFld.options.dataSetType='STATIC';
            	lSupFld.options.dataSetValues=lSup;
            	lSupFld.init();
            	var lPurFld=histFilterForm.getField('purchaser');
            	lPurFld.options.dataSetType='STATIC';
            	lPurFld.options.dataSetValues=lPur;
            	lPurFld.init();
            	console.log("fill called");
            },
        	error: errorHandler,
        });
	}
	</script>
   	
    </body>
</html>