<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.CircularBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
%>
<html>
	<head>
		<title>Dashboard for Monitor.</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/homePageStyle.css" rel="stylesheet">

<style type="text/css">
th.thDC {
	align:center;
	text-align:right;
}
td {
	text-align:right;
}
td.tdrh {
	text-align:left;
	color: black !important;
	font-size: 14px !important;
}
td.tdhl {
	background-color:#00FF00;
	text-align:right;
}
.panel-title1{
	position: relative;
    padding: 14px;
    margin: 0;
    color: white;
    background-color: darkorange;
    border-color: #ddd;
    font-size: 22px;
    font-weight: bold !important;
}
.tableheadmain{
	font-weight: bold !important;
	font-size: 17px;
}
.tablehead{
	background-color: #0e62c7 !important;
    font-size: 17px;
    width: 16%;
	color: white !important;
	text-transform: capitalize !important;
}
.total{
	color: black !important;
	font-weight: bold !important;
	font-size: 14px !important;
	cursor:pointer;
}
.tdDC{
	color: black !important;
	font-size: 14px !important;
}
</style>

	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Dashboard for Monitors" />
		<jsp:param name="desc" value="" />
	</jsp:include>
	
	<div id="divData" >
    </div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/jquery.xtemplatetable.js"></script>
	<script id="script-resource-12" src="../assets/js/highcharts.js"></script>
		<script id="tplDashboard" type="text/x-handlebars-template">
{{#ifHasAccess "findashboardmonitor-view"}}
				{{#if hasTransData}}
			<div class="row" id='invturnover' >
				<section class="col-lg-12" id="cont7"> 
					<div id=divinvturnover class="box box-danger">
						<div class="box-header">
							<div class="panel panel-default" >
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Tunover Consolidated Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%;  min-height: auto !important  margin:0 auto; ">
									<div class="row"  > <h4 align="right" style="color:black;margin-right:20px" > * Amount in Lakhs</h4></div>
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Transaction Summary</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANSCONS_TRANSSUMMARY}}
												<tr onClick="javascript:toggleTransDisplay('{{key}}')" style="cursor:pointer">
													<td  class="tdrh " id="{{key}}">{{desc}}</td>
													<td class=" {{#if curhighlight}}tdhl{{/if}}" >{{cur}} </td>
													<td class=" {{#if curmthhighlight}}tdhl{{/if}}" >{{curmth}} </td>
													<td class=" {{#if prevmthhighlight}}tdhl{{/if}}" >{{prevmth}} </td>
													<td class=" {{#if fyhighlight}}tdhl{{/if}}" >{{fy}}</td>
													<td class=" {{#if sihighlight}}tdhl{{/if}}" >{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}


				{{#if hasTransData}}
			<div class="row" id="invcountdiv">
				<section class="col-lg-12 " id="cont8"> 
					<div id=divTransInvCountSummary class="box box-danger ">
						<div class="box-header">
							<div class="panel panel-default">
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Count Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%;  min-height:190px;  margin:0 auto;">
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Sellers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_COUNT_MSMESTATUS}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_COUNT_MSMESTATUS_toggle')"{{/if}} {{#unless @last}} class="TRANS_COUNT_MSMESTATUS_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Buyers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_COUNT_CONSTITUTION}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_COUNT_CONSTITUTION_toggle')"{{/if}} {{#unless @last}} class="TRANS_COUNT_CONSTITUTION_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}


				{{#if hasTransData}}
			<div class="row" id="invamountdiv">
				<section class="col-lg-12" id="cont9"> 
					<div id=divinvamount class="box box-danger">
						<div class="box-header">
							<div class="panel panel-default">
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Tunover Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%";  min-height:310px;  margin:0 auto;">
								<div class="row"  > <h4 align="right" style="color:black;margin-right:20px" > * Amount in Lakhs</h4></div>
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Sellers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_MSMESTATUS}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_MSMESTATUS_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_MSMESTATUS_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}"  >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Buyers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_CONSTITUTION}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_CONSTITUTION_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_CONSTITUTION_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<!--<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Financiers</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_FINCATEGORY}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_FINCATEGORY_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_FINCATEGORY_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>-->

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}

{{/ifHasAccess}}
	</script>
	<script>
	var tplDashboard; 
	var oldData=null;
	var data;
		$(document).ready(function() {
			tplDashboard=Handlebars.compile($('#tplDashboard').html());
			reloadDashboard();
		});
		function reloadDashboard() {
			var lData=[];
			$.ajax( {
		        url: "findashboardmonitor/getData",
		        type: "GET",
		        success: function( pObj, pStatus, pXhr) {
		        	data=pObj;
		        	//for Regulatory Dashboard
		        	buildViewModel(data, oldData);
		        	oldData=data;
		        	//
					$('#divData').html(tplDashboard(data));
		      
		        	displayInvoiceall();

		        	//
		        	// bind drag events
					$('.drag-container').on('dragover', function(ev) {
						ev.preventDefault();
					});
					$('.drag-container').on('drop', function(ev) {
						ev.preventDefault();
						var lSrcId = ev.originalEvent.dataTransfer.getData("text");
						var lSrc$ = $('#'+lSrcId);
						var lTrg$ = $(ev.target);
						var lDest$ = lTrg$.closest('.drag-element');
						if (lDest$.length == 0)
							lDest$ = lTrg$.closest('.drag-container');
						var lSrcGrp = lSrc$.data('dragGroup');
						var lDestGrp = lDest$.data('dragGroup');
						if (((lSrcGrp==null)&&(lDestGrp==null))||(lSrcGrp===lDestGrp)) {
							if (lDest$.hasClass('drag-container')) lDest$.append(lSrc$);
							else if (lDest$.hasClass('drag-element')) lSrc$.insertBefore(lDest$);
						}
						saveLayout();
					});
					$('.drag-element .drag-thumb').on('dragstart', function(ev) {
						var lTrg$=$(ev.target).closest('.drag-element');
						var lId=lTrg$.attr('id');
						ev.originalEvent.dataTransfer.setData("text", lId);
					})
					$('.drag-element .drag-thumb').prop('draggable',true);
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#btnAddToWatch').prop('disabled',false);
		        }
			});
		}
		function buildViewModel(pData, pOldData){
			//check whether the Regulatory Data is available
			//do appropriately
/* 			traverse display order
				lkey=type^category
				mewdata = servermodel.countdata[lkey]
				olddata = oldservermodel.countdata[lkey]
				highlight(newdata,olddata)
				set newdata in viewmodel
			// totals
			lkey="TOTAL"
			mewdata = servermodel.countdata[lkey]
			olddata = oldservermodel.countdata[lkey]
			highlight(newdata,olddata)
			set viewmodaltotall = newdata */
			//travers entityGroups : Seller-Msme/Buyer-Constituion/Financier-FinCategory
			var lTypeKeys = ['regEntCatTypes','regEntCatTypes','regEntCatTypes','transTypes'];
			var lTypeDataKeys = ['regEntCatTypeData','transData','transData','transSummaryData'];
			var lTypePrefix = ['REG_','TRANS_COUNT_','TRANS_TO_','TRANSCONS_'];
			var lDataIndex = [-1,0,1,-1];
			//
			for(var lPtr=0; lPtr < lTypeKeys.length; lPtr++){
				var lNewData = pData[lTypeDataKeys[lPtr]];
				var lOldData = (pOldData!=null?pOldData[lTypeDataKeys[lPtr]]:null);
				var lCatTypes = pData[lTypeKeys[lPtr]];
				//add total to category
				//addTotalToCategory(lCatTypes);
				//
				var lViewData = getViewData(lCatTypes, lNewData, lOldData, lDataIndex[lPtr]);
				//
				Object.keys(lViewData).forEach(function (lKey) { 
					pData[lTypePrefix[lPtr]+lKey] = lViewData[lKey];
				});
			}
		}
		
		function addTotalToCategory(pCatTypes){
			if(pCatTypes!=null){
				for(var lPtr1=0; lPtr1 < pCatTypes.length ; lPtr1++){
					var lTmpCatTypes =pCatTypes[lPtr1]['catTypes'];
					var lFound=false;
					for(var lPtr2=0; lPtr2<lTmpCatTypes.length; lPtr2++){
						if(lTmpCatTypes[lPtr2]['value']=='TOTAL'){
							lFound=true;
							break;
						}
					}
					if(!lFound){
						//lTmpCatTypes.push({"value":"TOTAL", "desc":"Total"}); //adding total
					}
				}
			}
		}
		
		function getViewData(pCategoryTypes, pNewServerData, pOldServerData, pDataIndex) {
			var lViewData = {};
			var lRegEntCat = pCategoryTypes; //categorywise categorytypes
			var lOldData = pOldServerData;			
			//
			if(lRegEntCat != null){
				var lCat, lCatType, lCatTypeDes; 
				var lCatTypes; //List
				var lNewVal, lOldVal;
				for(var lPtr=0; lPtr < lRegEntCat.length; lPtr++){
					if(lPtr==0){
						//oldAlert("test");
					}
					lCat = lRegEntCat[lPtr]['entCat'];//key1
					lCatTypes = lRegEntCat[lPtr]['catTypes'];
					var lRowList = [];
					lViewData[lCat] = lRowList;
					//oldAlert("lCat : "+lCat);
					for(var lPtr1=0; lPtr1 <= lCatTypes.length; lPtr1++){
						if(lPtr1==lCatTypes.length){
							lCatType = 'TOTAL';
							lCatTypeDesc = 'Total';
						}else{
							lCatType = lCatTypes[lPtr1]['value']; //key2
							lCatTypeDesc = lCatTypes[lPtr1]['desc'];
						}
						var lRow = {};
						if(pNewServerData[lCat][lCatType] != null){
								Object.keys(pNewServerData[lCat][lCatType]).forEach(function (lKey) { 
									if(pDataIndex == -1){
										lNewVal = pNewServerData[lCat][lCatType][lKey];
									}else{
										lNewVal = pNewServerData[lCat][lCatType][lKey][pDataIndex];
									}
									lOldVal = getOldValue(lOldData, lCat, lCatType, lKey, pDataIndex);
									lRow[lKey] = lNewVal;
									if(lOldVal!=null){
										//compare old and new value
										if(lOldVal!=null && lNewVal!=null){
											if(lOldVal!=lNewVal){
												lRow[lKey+'highlight'] = true;
												//oldAlert(lKey+'highlight OldVal : ' + lOldVal + " NewVal "+lNewVal)
											}
										}
									}
							});
							lRow['key'] = lCatType;
							lRow['desc'] = lCatTypeDesc;
							lRowList.push(lRow);
						}
					}
				}
			}
			return lViewData;
		}
		
		function getOldValue(pOldData, pCat, pCatType, pKey,pDataIndex){
			var lValue = null;
			if(pOldData!=null && pOldData[pCat]!=null && pOldData[pCat][pCatType]!=null){
				if(pDataIndex == -1){
					lValue = pOldData[pCat][pCatType][pKey];
				}else{
					lValue = pOldData[pCat][pCatType][pKey][pDataIndex];
				}
			}
			return lValue
		}

		
		var dispInv = {'invcount':false, 'invamount':false};
		function toggleTransDisplay(pKey){
			dispInv[pKey] = !($('#'+pKey+'div').is(':visible'));
			displayInvoice(pKey,dispInv[pKey]);
		}
		function displayInvoiceall(){
			for(lKey in dispInv){
				displayInvoice(lKey,dispInv[lKey]);
			}
		}
		function displayInvoice(pKey,pDisplay){
			if(pDisplay)$('#'+pKey+'div').show();
			else $('#'+pKey+'div').hide();
		}
		function toggleTable(pKey){
			$('.'+pKey).toggle();
			$('.'+pKey).toggleClass('hide');
			
		}
	</script>

</body>
</html>