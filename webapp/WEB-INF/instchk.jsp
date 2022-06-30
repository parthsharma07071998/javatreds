<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.AppConstants.AutoAcceptBid"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | For Checking</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
    </head>
<style type="text/css">
#dispAgreementChk {
	overflow: scroll;
	height: 300px;
}
.group{
	color:OrangeRed !important;
}
.color-box {
   width: 15px;
   height: 15px;
   display: inline-block;
   position: absolute;
}
.rowYes{
background-color: #FFA746 !important;
}
.rowEway{
background-color: #5899FD !important;
}

</style>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="For Checking" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contInstrument">
<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">For Checking</h1>
				</div>
			</div>
			<div>
				<div class="cloudTabs">
	                 <ul class="cloudtabs nav nav-tabs">
						 <li><a href="#tab0" data-toggle="tab">Inbox <span id="badge0" class="badge badge-primary"></span></a></li>
						 <!-- <li><a href="#tab1" data-toggle="tab">Checker Pending <span id="badge1" class="badge badge-primary"></span></a></li> -->
						 <li><a href="#tab2" data-toggle="tab"><span id="spnTab2">Counter</span> Pending <span id="badge2" class="badge badge-primary"></span></a></li>
						 <li><a href="#tab3" data-toggle="tab" style='display:none'>Ready For Auction <span id="badge3" class="badge badge-primary"></span></a></li>
						 <li><a href="#tab4" data-toggle="tab">Rejected <span id="badge4" class="badge badge-primary"></span></a></li>
						 <li><a href="#tab5" data-toggle="tab">In Auction <span id="badge5" class="badge badge-primary"></span></a></li>
						 <li style="display:none"><a href="#tab6" data-toggle="tab">Auctioned <span id="badge6" class="badge badge-primary"></span></a></li>
						 <li style="display:none"><a href="#tab7" data-toggle="tab">Settlement Failed <span id="badge7" class="badge badge-primary"></span></a></li>
						 <li style="display:none"><a href="#tab8" data-toggle="tab">Expired <span id="badge8" class="badge badge-primary"></span></a></li>
						 <li style="display:none"><a href="#tab9" data-toggle="tab">Group <span id="badge9" class="badge badge-primary"></span></a></li>
						 <li><a href="#tab10" data-toggle="tab">History <span id="badge10" class="badge badge-primary"></span></a></li>
				 	</ul>
				</div>
				<div class="filter-block clearfix">
					<div class="">
						<span class="right_links">
							<a class="secure btn-group0 btn-group9" href="javascript:;" data-seckey="instchk-approve" id=btnViewApprove><span class="glyphicon glyphicon-ok"></span> Approve</a>
							<a class="secure btn-group0 btn-group9" href="javascript:;" data-seckey="instchk-reject" id=btnReject><span class="glyphicon glyphicon-trash"></span> Reject</a>
							<a class="secure btn-group0 btn-group9" href="javascript:;" data-seckey="instchk-return" id=btnReturn><span class="glyphicon glyphicon-remove"></span> Return</a>
							<a class="secure " href="javascript:;" data-seckey="instchk-view" id=btnClubbedInstruments><span class="glyphicon glyphicon-eye"></span> View Group Details</a>
							<a class="secure btn-group10 hidden" href="javascript:;" data-seckey="inst-view" onClick="javascript:showHistoryFilter();" ><span class="fa fa-filter" id='filter' style='color:black'></span> Filter</a>
							<a href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span>CSV</a> 
							<a class="secure" href="javascript:;" data-seckey="instview-view" id=btnViewInstru><span class="glyphicon glyphicon-eye-open"></span> View Instrument</a>
							<a class="btn-enter" href="javascript:;" id=btnSearch><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
							<span id="spnColumnChooser" class="glyphicon glyphicon-plus"></span>
						</span>
					</div>
				</div>
			</div>
			
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " data-col-chooser="spnColumnChooser" data-selector="multiple" id="tblData" >
								<thead><tr>
									<th data-width="110px" data-name="id" data-class-name="select-checkbox" data-sel-exclude="true" >Id</th>
									<th data-width="0px" data-name="tab" data-sel-exclude="true" data-visible="false"></th>
									<th data-width="120px" data-name="purName">Buyer</th>
									<th data-width="120px" data-name="supName">Seller</th>
									<th data-width="70px" data-name="poDate">Purchase Order Date</th>
									<th data-width="100px" data-name="poNumber">Purchase Order Number</th>
									<th data-width="100px" data-name="purLocation">Buyer Location</th>
									<th data-width="100px" data-name="supLocation">Seller Location</th>
									<th data-width="100px" data-name="instNumber">Invoice Number</th>
									<th data-width="100px" data-name="salesCategory">Sales Category</th>
									<th data-width="70px" data-name="instDate">Invoice Date</th>
									<th data-width="110px" data-name="goodsAcceptDate">Goods/Service Acceptance Date</th>
									<th data-width="70px" data-name="instDueDate">Invoice Due Date</th>
									<th data-width="70px" data-name="maturityDate">Maturity Date</th>
									<th data-width="80px" data-name="amount">Invoice Amount (in Actual)</th>
									<th data-width="80px" data-name="haircutPercent">Haircut %</th>
									<th data-width="80px" data-name="adjAmount">Deductions (in Actual)</th>
									<th data-width="80px" data-name="cashDiscountPercent">Cash Discount %</th>
									<th data-width="80px" data-name="cashDiscountValue">Cash Discount Value</th>
									<th data-width="80px" data-name="tdsAmount">TDS (in Actual)</th>
									<th data-width="100px" data-name="netAmount">Factoring Unit Cost (in Actual)</th>
									<th data-width="110px" data-name="status">Status</th>
									<th data-width="80px" data-name="age">Aging</th>
									<th data-width="90px" data-name="fuId">Factoring Unit Id</th>
									<th data-width="70px" data-name="makerLoginId">Maker User</th>
									<th data-width="70px" data-name="checkerLoginId">Checker User</th>
									<th data-width="70px" data-name="ownerEntity">Owner</th>
									<th data-width="70px" data-name="ownerLoginId">Owner User</th>
									<th data-width="130px" data-name="statusUpdateTime">Status Update Time</th>
									<th data-width="0px" data-name="groupFlag" data-sel-exclude="true" data-visible="false"></th>
									<th data-width="0px" data-name="ebdId" data-sel-exclude="true" data-visible="false"></th>
								</tr></thead>
							</table>			 	
						</div>
					</div>
				</fieldset>
			</div>
			<div style="text-align: right">
				</span><b>* Legends -->  :</b></span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: #5899FD;" ></span><span style="padding-left:35px;">Eway Insturment</span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: #FFA746; "></span><span style="padding-left:35px;">Group Insturment</span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="color-box" style="background-color: white;"></span><span style="padding-left:35px;">Normal Insturment</span>
			</div>
		</div>
		<!-- frmSearch -->
		
		
		
 		<div class="modal fade" id="mdlCW" tabindex=-1>
			<div class="modal-dialog modal-md">
				<div class="modal-content">
					<div class="modal-header">
						<span>&nbsp;TREDS</span>
						<button type="button" class="btn btn-sm pull-right"
							data-dismiss="modal">
							<i class="fa fa-close"></i>
						</button>
					</div>
					<div class="modal-body">
						<div class="xform box">
							<fieldset>
								<div class="row state-P">
									<div class="col-sm-12">
										<label class="checkbox"><input type=checkbox
											id="agreementchk"><i></i><span><a
												href="javascript:;" id="viewAgreementChk">CLICK WRAP
													AGREEMENT</a></span> <b class="tooltip tooltip-top-left">I Agree</b></label>
									</div>
								</div>
								<div class="row">
									<div class="btn-groupX pull-right">
										<button type="button" class="btn btn-info btn-primary btn-lg"
											id=btnFinalSubmit>
											<span class="glyphicon glyphicon-circle-arrow-right"></span>
											Ok
										</button>
									</div>
								</div>

								<div id="dispAgreementChk" style="display: none"></div>

							</fieldset>
						</div>
					</div>
				</div>
			</div>
		</div>

		
		
		<div class="modal fade" tabindex=-1>
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<span>Auction Preferences</span>
						<button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
					</div>
					<div class="modal-body modal-no-padding">
						<!-- frmMain -->
						<div style="display:none" id="frmMain">
							<div class="page-title">
								<div class="title-env">
									<h1 class="title">Auction Preferences</h1>
								</div>
							</div>
					    	<div class="xform box">
					    		<div class="box-body panel-group panel-group-joined" id="accordion">
					    			<div class="panel panel-default">
									<div class="panel-heading">
										<h3 class="panel-title">
											<a data-parent="#accordion" data-toggle="collapse" href="#accrd1">Auction Preferences</a>
										</h3>
									</div>
									<div class="panel-collapse collapse in" id="accrd1">
										<div class="panel-body">

										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-12">
									<div class="btn-groupX pull-right">
										<button type="button" class="btn btn-enter btn-info btn-lg" id=btnApprove><span class="fa fa-check"></span> Approve</button>
										<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
									</div>
								</div>
							</div>
						</div>
					</div>
						
					</div>
				</div>
			</div>
		</div>
		
		<div class="modal fade" tabindex=-1 id="mdlClubbedInvoice"><div class="modal-dialog  modal-lg"><div class="modal-content">
		</div></div></div> 
		
	</div>

	<div class="modal fade" id="mdlHistoryFilter" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;History Filter</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmHistoryFilter">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="id" class="label">Instrument Id:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="id" placeholder="Instrument Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="instNumber" class="label">Invoice Number:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="instNumber" placeholder="Invoice Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="fuId" class="label">Factoring Unit:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="fuId" placeholder="Factoring Unit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row state-S" id="purchaserRow">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4"><section><label for="purchaserRef" class="label">Buyer Reference:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="purchaserRef" placeholder="Buyer Reference">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row state-P" id="supplierRow">
					<div class="col-sm-4"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4"><section><label for="supplierRef" class="label">Seller Reference:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="supplierRef" placeholder="Seller Reference">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>

				<div class="row">
					<div class="col-sm-4"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="groupFlag" class="label">Group Flag:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="groupFlag"><option value="">Select Group Flag</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnFilter><span class="fa fa-filter"></span> Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg" id=btnHistFilterClr>Clear Filter</button>
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
   	
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>


	<script id="tplClubbedInstruments" type="text/x-handlebars-template">
		<div class="modal-header">Details of clubbed instruments <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table"><thead>
				<tr>
					<th width=125px >ID </th>
					<th > Invoice No. </th>
					<th >Invoice Amount </th>
					<th >Cash Discount Amount</th>
					<th> Adj Amount</th>
					<th> TDS Amount</th>
					<th>Net Amount</th>
					<th width=90px > </th>
				</tr></thead>
				<tbody>
		{{#each splitlist}}
				<tr>
					<td>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td><button type="button" id='btnViewInst' onclick="viewClubbedInstrument({{id}})">View</button></td>
				</tr>
		{{/each}}
				<tr style="font-weight:bold">
					<td></td>
					<td></td>
					<td>{{splitsummary.amt}}</td>
					<td>{{splitsummary.cashdiscountAmt}}</td>
					<td>{{splitsummary.adjAmt}}</td>
					<td>{{splitsummary.tdsAmt}}</td>
					<td>{{splitsummary.netAmt}}</td>
					<td></td>
				</tr>
				<tr>
					<td><button type="button" id='btnDownloadGroupCSV' onclick="downloadGroupCsv({{parentId}})" class="fa fa-download" >Download CSV</button></td>
				</tr>
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>

	</script>   	
   	<script id="tplMessage" type="text/x-handlebars-template">
		<table class="table">
			<thead><tr><th>Action</th><th>Remarks</th></tr></thead>
			<tbody>
			{{#each this}}
				<tr><td>{{act}}</td><td>{{rem}}</td></tr>
			{{/each}}
			</tbody>
		</table>
	</script>
   	
	<script type="text/javascript">
		var crudInstrument$,crudInstrument,mainForm;
		var tabIdx, tabData;
		var lSupplier=false, lPurchaser=false;
		var tplClubbedInstruments,tplMessage;
		//
		var HISTORY_INDEX = 10;
		var histFilterForm$ = null, histFilterForm = null;
		var MAIN_RESOURCE = "instchk";
		//
		$(document).ready(function() {
			MAIN_RESOURCE = "instchk";
			tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html(),null, null);
        	tplMessage = Handlebars.compile($('#tplMessage').html());
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class).getJsonConfig()%>;
			var lSelfCtrlId = null, lSelfCtrl2Id=null; //self controls
			var lCntrLabel= "Counter", lCntrColName=""; //counter controls
			$.each(loginData.entityTypeList, function( index, value ) {
				  if(value == 'S')
					  lSupplier = true;
				  else if(value == 'P')
					  lPurchaser = true;
				});
			if(lPurchaser){
				lCntrLabel="Seller";
				lCntrColName="supName";
				lSelfCtrlId = "purchaser";
				mEntityType = 'P';
			}
			else if(lSupplier){
				lCntrLabel="Buyer";
				lCntrColName="purName";
				lSelfCtrlId = "supplier";
				mEntityType = 'S';
			}
			$("#spnTab2").html(lCntrLabel);
			var displayColumns = ["id","instNumber","instDate","salesCategory",lCntrColName,"poNumber","goodsAcceptDate","maturityDate","netAmount","status","age"];
			var lConfig = {
					resource: "instchk",
					autoRefresh: true,
					defaultColumns : displayColumns,
					tableConfig: {
						lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
						lengthChange:true,
						pageLength:10,
						select: {
							style:'multiple',
							allMethod: 'all',
							clearSelectionOnPaging: true 
						},
						createdRow:function( row, data, dataIndex ) {
							if(data.groupFlag){
								$(row).addClass('row'+data.groupFlag);
							}
							if(data.ebdId){
								$(row).addClass('rowEway');
							}
						}
					},
					preSearchHandler: function(pFilter){
						if (tabIdx==HISTORY_INDEX) {
							var lFilterCount = 0;
							$.each(histFilterForm.getValue(),function(pIdx,pVal){
								pFilter[pIdx]=pVal;
								if(pVal!=null&&pVal!="") lFilterCount+=1;
							});
							if(lFilterCount<=0){
								alert("Please select a filter for searching in history.");
								return false;
							}
						}
						if(pFilter!=null&&Object.keys(pFilter).length>1){
							$('#filter').attr('style','color:red');
						}else{
							$('#filter').attr('style','color:black');
						}
						pFilter.filtHistFlag = tabIdx==HISTORY_INDEX?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
						return true;
					},
					postSearchHandler: function(pObj) {
						if (tabIdx==HISTORY_INDEX) {
         					tabData[HISTORY_INDEX]=pObj;
         				} else {
         					var lOldHistData = tabData==null?null:tabData[HISTORY_INDEX];
							tabData = [];
							$.each(pObj,function(pIdx,pValue){
								var lData=tabData[pValue.tab];
								if (lData==null) {
									lData=[];
									tabData[pValue.tab]=lData;
								}
								lData.push(pValue);
								tabData[HISTORY_INDEX]=lOldHistData;//reput the history data
							});
         				}
						var lIdx;
						$('.nav-tabs li a').each(function (index, element) {
							lIdx = element.attributes['href'].value.substring(4);
							if (tabData[lIdx]==null) tabData[lIdx]=[];
							var lCount = tabData[lIdx]?tabData[lIdx].length:0;
							$('#badge'+lIdx).html(lCount<10?"0"+lCount:lCount);
						});
						showData();
						$('#'+lSelfCtrlId).prop('disabled',true);
						return false;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudInstrument$ = $('#contInstrument').xcrudwrapper(lConfig);
			crudInstrument = crudInstrument$.data('xcrudwrapper');
			mainForm = crudInstrument.options.mainForm;

			if(lPurchaser){
				getAgreementHtml();
			}

			$('#btnApprove').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Checker_Approved.getCode()%>',$('#btnApprove'));
			});
			$('#btnReject').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Checker_Rejected.getCode()%>',$('#btnReject'));
			});
			$('#btnReturn').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Checker_Returned.getCode()%>',$('#btnReturn'));
			});
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
			    var lRef1 = $(event.target).attr('href');         // active tab
			    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
			    tabIdx = parseInt(lRef1.substring(4));
			    if (lRef2)
			    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
			    $('.btn-group'+tabIdx).removeClass('hidden');
			    showData();
			});
			
			$('#btnViewInstru').on('click', function() {
				var lSelected = crudInstrument.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				if(lSelected.data().groupFlag == 'Yes'){
     				alert("Please select a non-group instrument.");
     			}else{
     				showRemote('instview?id='+crudInstrument.selectedRowKey(lSelected.data()), 'modal-xl', true);
     			}
				
			});
			$('#btnViewApprove').on('click', function() {
				var lRows=crudInstrument.getSelectedRows();
		 		if(lRows.length!=1){
		 			if(lRows.length<1) {
		 				alert("Please select a row.");
		 			}else{
		 				updateStatusPromptClickWrap('<%=InstrumentBean.Status.Checker_Approved.getCode()%>');		 				
		 			}
		 		}else{
		 			var lData = null , lDataId=null;
		 			$.each(lRows, function(pIndex,pValue) {
		 				lData = pValue.data().status;
		 				lDataId = pValue.data().id;
					});
		 			if (lData == '<%=InstrumentBean.Status.Submitted.toString()%>' ){
		 				showRemote('instview?app=Y&id='+lDataId, 'modal-xl', true);
					}else{
						alert("Invalid Instrument Status.");
					}
		 		}
			});
			$('#btnClubbedInstruments').on('click', function() {
			 		var lRows=crudInstrument.getSelectedRows();
			 		if(lRows.length!=1){
			 			if(lRows.length<1) alert("Please select a row.");
			 			if(lRows.length>1) alert("Please select only one row.");	
			 		}else{
			 			var lData = null;
			 			var lView = false;
			 			$.each(lRows, function(pIndex,pValue) {
			 				lData = pValue.data().id;
			 				lView = (pValue.data().groupFlag == 'Yes');
						});
			 			if (lView){
				 			var lUrl= 'inst/viewclubbeddetails/' + lData  ;
				 			$.ajax({
				 	        url: lUrl,
				 	        type: 'GET',
				 	        success: function( pObj, pStatus, pXhr) { 
				 	        	$('#mdlClubbedInvoice .modal-content').html(tplClubbedInstruments(pObj));
				 	        	showModal($('#mdlClubbedInvoice'));
				 	        },
				 	    	error: errorHandler
				 	    	});
			 			}else{
			 				alert("Please select a grouped instrument. ");
			 			}
			 		}
	     	});
			 $("#btnDownloadCSV").on("click",function(){
         		 if(tabIdx !=null && tabIdx==HISTORY_INDEX){
       				var lHistoryFilters = histFilterForm.getValue(true);
                    if(lHistoryFilters==null){
                        lHistoryFilters = {};
                    }
                    if(lHistoryFilters!=null && Object.keys(lHistoryFilters).length <= 0){
                        alert("Please select a filter before downloading.");
                        return;
                    }
                    lHistoryFilters["tab"] = tabIdx;
      	     	 	lHistoryFilters["columnNames"] = crudInstrument.options.tableConfig.columnNames;
      	     	 	lHistoryFilters["filtHistFlag"] = tabIdx==HISTORY_INDEX?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
               		downloadFile('instchk/all',null,JSON.stringify(lHistoryFilters));
          		 }else{
     				downloadFile('instchk/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "tab": tabIdx}));
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
									"name": "fuId",
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
									"name":"purchaserRef",
									"label":"Buyer Short Name",
									"dataType":"STRING",
									"maxLength": 30
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
									"name":"supplierRef",
									"label":"Seller Short Name",
									"dataType":"STRING",
									"maxLength": 30
								},
								{
									"name":"status",
									"label":"Status",
									"dataType":"STRING",
									"maxLength": 3,
									"dataSetType": "STATIC",
									"dataSetValues":[{"text":"Expired", "value":"EXP"},{"text":"Leg 3 Generated", "value":"LEG3"},{"text":"Factored", "value":"FACT"},{"text":"Leg 1 Settled", "value":"L1SET"},{"text":"Leg 1 Failed", "value":"L1FAIL"},{"text":"Leg 2 Settled", "value":"L2SET"},{"text":"Leg 2 Failed", "value":"L2FAIL"}],
									"desc":"Current status of obligation."
								},
								{
									"name":"instNumber",
									"label":"Invoice Number",
									"dataType":"STRING",
									"maxLength": 30
								},
								{
									"name":"groupFlag",
									"label":"Grouping",
									"dataType":"STRING",
									"maxLength": 1,
									"dataSetType": "STATIC",
									"dataSetValues":[{"text":"Grouped", "value":"Y"}],
									"desc":"Grouped Instruments"
								}
						]
					};
			histFilterForm$ = $('#frmHistoryFilter').xform(lHistConfig);
			histFilterForm = histFilterForm$.data('xform');

			$('#btnFilter').on('click', function() {
				$('#mdlHistoryFilter').modal('hide');
				crudInstrument.searchHandler();
			});
			
			$('#btnHistFilterClr').on('click', function() {
				histFilterForm.setValue(null);
				$('#filter').attr('style','color:black');
			});
			
			
			

<%
String lTab = StringEscapeUtils.escapeHtml(request.getParameter("t"));
if (lTab==null) lTab="0";
%>
			$('#frmSearch li:eq(<%=lTab%>) a').tab('show');
		});
		function updateStatus(pStatus, pBtn$) {
			var isGroup = false;
			var lRows=crudInstrument.getSelectedRows();
 			if(lRows.length<1){
 				alert("Please select a row.");
 				return;
 			}
 			var lSelectedData = null;
 			var lSelectedIds = [];
 			$.each(lRows, function(pIndex,pValue) {
 				lSelectedData = pValue.data().id;
				lSelectedIds.push(pValue.data().id);
 				if("<%=CommonAppConstants.Yes.Yes%>"==pValue.data().groupFlag){
    					isGroup = true;
    				}
			});
 			if (isGroup){
     			if (pStatus == "<%=InstrumentBean.Status.Checker_Rejected.getCode()%>"){
     				alert("Grouped Instrument can't be removed.");
     				return;
     			} 
     			if  (pStatus == "<%=InstrumentBean.Status.Checker_Returned.getCode()%>"){
     				alert("Grouped Instrument can't be returned.");
         			return;
     			}
     		}
 			prompt("Please enter suitable reason/remarks","Reason",function(pReason){
 				if(pReason == null){
 					alert("Remarks are mandatory, please fill the remarks and try again.");
 					return false; 
 				}
				var lData = {id:lSelectedData,status: pStatus,statusRemarks : pReason};
				lData['idList'] = lSelectedIds;
				delete lData['id'];
				if (pBtn$) pBtn$.prop('disabled',true);
				var lStatus = pStatus;
				$.ajax( {
		            url: "instchk/status/"+pStatus.toLowerCase(),
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
		            		alert(tplMessage(pObj));
		            },
		        	error: errorHandler,
		        	complete: function() {
		        		if (pBtn$) pBtn$.prop('disabled',false);
		        		crudInstrument.showSearchForm();
		        	}
		        });					
			});
		}
		function showData() {
			crudInstrument.options.dataTable.rows().clear();
			if (tabData && (tabData[tabIdx] != null))
				crudInstrument.options.dataTable.rows.add(tabData[tabIdx]).draw();
		}
		function initForm() {
			var lFieldGroup;
			//disable all
			lFieldGroup = crudInstrument.options.fieldGroups.insert;
			mainForm.enableDisableField(lFieldGroup, false, false);
			mainForm.enableDisableField(['autoConvert'],false,false);
		}
		function updateStatusPromptClickWrap(pStatus){
			if(lPurchaser){
				showModal($('#mdlCW'));
				$('#viewAgreementChk').prop('checked',false);
				document.getElementById("dispAgreementChk").style.display = "none";
			}else{
				updateStatus(pStatus);
			}
		}
		$('#btnFinalSubmit').on('click', function() {
			if($('#agreementchk').prop('checked')){
				updateStatus('<%=InstrumentBean.Status.Checker_Approved.getCode()%>',$('#btnFinalSubmit'));
				$("#mdlCW").modal('hide');
			}else{
				alert("Please select click wrap agreement.");
			}
		});
		
		$('#viewAgreementChk').on('click', function() {
			$("#dispAgreementChk").html(lAgreementHtml);
			var lDisplay = (document.getElementById("dispAgreementChk").style.display=="");
			document.getElementById("dispAgreementChk").style.display = !lDisplay?"":"none";
		});
		

		function getAgreementHtml(){
			$.ajax({
	              url: 'puraggacc/clickwraphtml?domain='+loginData.domain+'&skipLog=true',
	      				type : 'GET',
	    				dataType: "html",
	      				success : function(pObj, pStatus, pXhr) {
	      					lAgreementHtml = pObj;
	      				},
	      				error : function(pObj, pStatus, pXhr) {
	      					oldAlert('Error');
	      				}
	      			});
	      }
		function viewClubbedInstrument(pId){
	   		showRemote('instview?id='+pId, 'modal-xl', true,'Instrument Details');
		 }
		function downloadGroupCsv(pParentId) {
     		downloadFile('inst/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "groupInId": pParentId}));
     	}
		
	 	function showHistoryFilter(){
			showModal($('#mdlHistoryFilter'));		
		}
	 


	</script>
   	
    </body>
</html>