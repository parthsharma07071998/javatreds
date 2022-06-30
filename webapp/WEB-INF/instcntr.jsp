<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Counter Approvals</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/>
    </head>
<style type="text/css">
#dispAgreementCntr {
	overflow: scroll;
	height: 300px;
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
    	<jsp:param name="title" value="Counter Approvals" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contInstrument">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Counter Approvals</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="tab-pane panel panel-default no-margin collapse in" id=divFilterGrpAmt>
			<fieldset class="form-horizontal">
				<div class="col-sm-12">
					<section class="input">
						<b>Invoice Less than (Net Amount) :</b> <input type="number"  name="Amt" id='Amt'><br></span>
					</section>
				</div>
				<div class="panel-body bg_white">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch1 onclick="showFilteredData(true);"><span class="fa fa-search"></span> Search</button>
		 						<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr1 onclick="showFilteredData(false);">Clear Filter</button>
							</div>
						</div>
					</div>
			</div>
			</fieldset>
			</div>
			<div>
				<div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
						 <li><a href="#tab0" data-toggle="tab">Inbox <span id="badge0" class="badge badge-primary"></span></a></li>
						 <li><a href="#tab1" data-toggle="tab">Checker Pending <span id="badge1" class="badge badge-primary"></span></a></li>
						 <!-- <li><a href="#tab2" data-toggle="tab">Counter Pending <span id="badge2" class="badge badge-primary"></span></a></li> -->
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
							<a class="btn-group0 left_links " href="javascript:displayFilter();" ><span class="glyphicon glyphicon-plus"></span>Filter</a>
						<span class="right_links">
							<a class="secure btn-group0 btn-group9" href="javascript:;" data-seckey="instcntr-approve" id=btnModify1><span class="glyphicon glyphicon-ok"></span> Approve</a> 
							<a class="secure btn-group0" href="javascript:;" data-seckey="instcntr-reject" id=btnReject><span class="glyphicon glyphicon-trash" style="color:red"></span> Reject</a>
							<a class="secure btn-group0 btn-group9" href="javascript:;" data-seckey="instcntr-return" id=btnReturn><span class="glyphicon glyphicon-remove"></span> Return</a>
							<span class="state-P"><a class="secure btn-group0" href="javascript:;" data-seckey="instcntr-approve" id=btnAddToGroup><span class="glyphicon glyphicon-plus"></span> Group</a></span>
							<a class="secure btn-group10 hidden" href="javascript:;" data-seckey="instcntr-view" onClick="javascript:showHistoryFilter();" ><span class="fa fa-filter" id='filter' style='color:black'></span> Filter</a>
							<a href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span>CSV</a>
 							<a class="secure " href="javascript:;" data-seckey="instcntr-view" id=btnClubbedInstruments><span class="glyphicon glyphicon-eye"></span> View Group Details</a> 
							<a class="secure" href="javascript:;" data-seckey="instview-view" id=btnViewInstru><span class="glyphicon glyphicon-eye-open"></span> View Instrument</a>
							<a class="btn-enter" href="javascript:;" id=btnSearch><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
							<div class = "btn-group">
						      <a href="javascript:;"  type = "button" data-toggle = "dropdown">Bulk Approval<span class = "caret"></span></a>
								<ul class = "dropdown-menu" role = "menu">
								<li><a href="javascript:;" id=btnDownloadCSVBulk style=''><span class="fa fa-download"></span>Bulk CSV</a></li>
							    <li><a class="secure btn-group0" href="javascript:;" data-seckey="inst-upload" id=btnUpload><span class="glyphicon glyphicon-open"></span> Upload CSV</a></li>
							</ul>
							</div>
							<span id="spnColumnChooser" class="glyphicon glyphicon-plus"></span>
						</span>
					</div>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " data-col-chooser="spnColumnChooser" data-selector="multiple" id="tblData">
								<thead>
									<tr>
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
<!-- 										<th data-width="110px" data-name="inCntChkLevel">Checker level</th> -->
										<th data-width="80px" data-name="creditPeriod">Credit Period</th>
										<th data-width="80px" data-name="extendedCreditPeriod">Extended Credit Period</th>
										<th data-width="80px" data-name="isApiVerified">API verified</th>
										<th data-width="80px" data-name="age">Aging</th>
										<th data-width="90px" data-name="fuId">Factoring Unit Id</th>
										<th data-width="70px" data-name="makerLoginId">Maker User</th>
										<th data-width="70px" data-name="checkerLoginId">Checker User</th>
										<th data-width="70px" data-name="ownerEntity">Owner</th>
										<th data-width="70px" data-name="ownerLoginId">Owner User</th>
										<th data-width="130px" data-name="statusUpdateTime">Status Update Time</th>
										<th data-width="0px" data-name="groupFlag" data-sel-exclude="true" data-visible="false"></th>
										<th data-width="0px" data-name="ebdId" data-sel-exclude="true" data-visible="false"></th>
										<th data-width="0px" data-name="isGemInvoice" data-sel-exclude="true" data-visible="false"></th>
									</tr>
								</thead>
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
		
		<!-- frmMain -->
        <%@include file="instform.jsp" %>
		<!-- frmMain -->

	
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
										<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave style="display:none !important"><span class="fa fa-check"></span> Save</button>
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

   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.bootstrap-duallistbox.js"></script>

	<script type="text/javascript">
		var crudInstrument$,crudInstrument,mainForm;
		var tplClubbedInstruments,tplMessage;
		var tabIdx, tabData;
		var lSupplier=false, lPurchaser=false;
		var lInboxData;
		var tplCustomfields;
		var crudFields;
		$(document).ready(function() {
			MAIN_RESOURCE = "instcntr";
			tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html());
			tplMessage = Handlebars.compile($('#tplMessage').html());
			tplCustomfields=Handlebars.compile($('#tplCustomfields').html());
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class).getJsonConfig()%>;
			var lSelfCtrlId = null, lSelfCtrl2Id=null; //self controls
			var lCntrLabel= "", lCntrColName=""; //counter controls
			var lSelfCtrlId = "";
			$.each(loginData.entityTypeList, function( index, value ) {
				  if(value == 'S')
					  lSupplier = true;
				  else if(value == 'P')
					  lPurchaser = true;
				});
			if(lPurchaser){
				lCntrColName="supName";
				lSelfCtrlId = "purchaser";
				mEntityType = 'P';
			}
			else if(lSupplier){
				lCntrColName="purName";
				lSelfCtrlId = "supplier";
				mEntityType = 'S';
			}
			var displayColumns = ["id","instNumber","instDate","salesCategory",lCntrColName,"poNumber","goodsAcceptDate","maturityDate","netAmount","status","age","isApiVerified"];
			var lConfig = {
					resource: "instcntr",
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
						pFilter.filtHistFlag = tabIdx==HISTORY_INDEX?'<%=CommonAppConstants.YesNo.Yes.getCode()%>':'<%=CommonAppConstants.YesNo.No.getCode()%>';
						if(pFilter!=null&&Object.keys(pFilter).length>1){
							$('#filter').attr('style','color:red');
						}else{
							$('#filter').attr('style','color:black');
						}
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
						if(tabData!=null && tabData.length > 0){
         	       					lInboxData = tabData[0] ;
         	        			}else{
         	        				lInboxData = [];
         	        			}
						return false;
					},
					postModifyHandler: function(pObj) {
						$('#creationKeys').hide();
						setPurSupLink(true,false);
						populateLoc('supplier','supClId',pObj.supClId);
						populateLoc('purchaser','purClId',pObj.purClId);
						onChangeSalesCategory();
						initForm();
						
						mainForm.getField('costBearingType').setValue(pObj.costBearingType);
						mainForm.getField('creditPeriod').setValue(pObj.creditPeriod);
						mainForm.getField('extendedCreditPeriod').setValue(pObj.extendedCreditPeriod);
						mainForm.getField('autoAccept').setValue(pObj.autoAccept);
						mainForm.getField('autoAcceptableBidTypes').setValue(pObj.autoAcceptableBidTypes);

						onCostBearingChange();
						setAutoAcceptBid();
						onBidAcceptingEntityTypeChange();
						mainForm.options.btnSave$.html('Approve');
						$('#'+lSelfCtrlId).prop('disabled',true);
						if(pObj!=null){
							var lChkFields = ['haircutPercent','cashDiscountPercent', 'adjAmount', 'cashDiscountValue'];
							for(lPtr=0; lPtr < lChkFields.length; lPtr++ ){
								if(pObj[lChkFields[lPtr]]!=mainForm.getField(lChkFields[lPtr]).getValue()){
									$('#'+lChkFields[lPtr]).css('border-color','red');
								}
							}
						}
						if(lPurchaser){
							document.getElementById("panAgreement").style.display = "";
							lEwayFieldGroup=crudInstrument.options.fieldGroups.ewayBill;
	         		    			mainForm.enableDisableField(lEwayFieldGroup, true, false);
						}
						if (pObj.groupFlag=='Y'){
							mainForm.enableDisableField(crudInstrument.options.fieldGroups.insert, false, false);
							mainForm.enableDisableField('statDueDate', false, false);
							mainForm.enableDisableField('instDueDate', false, false);
							mainForm.enableDisableField('autoAcceptableBidTypes', false, false);
							mainForm.enableDisableField('autoAccept', false, false);
						}
						if (pObj.ebdId!=null){
							mainForm.enableDisableField(crudInstrument.options.fieldGroups.eInvoiceFields, false, false);
	         				mainForm.enableDisableField(crudInstrument.options.fieldGroups.ewayBill, false, false);
						}
						populateInstrumentKeys(pObj.supplier,pObj.purchaser,pObj.id,pObj.supClId);
						mainForm.getField('instrumentCreationKeysList').setValue(pObj.instrumentCreationKeysList);
						hasAccessToLocationsKey(pObj.purchaser,pObj.purClId);
						getCustomFields(pObj.purchaser,pObj.cfId);
						var tmpMap = JSON.parse(pObj.cfData);
     					for (var i in tmpMap){
     						mainForm.getField(i).setValue(tmpMap[i]);
     						mainForm.enableDisableField(i,false,false);
     					}
	     				if(pObj.isGemInvoice!=null && pObj.isGemInvoice=='true'){
		     				disableFieldsForGemInvoice();
	     				}
						return true;
					},
					preSaveHandler: function(pData) {
						if(!preSaveCommonValidation())
							return false;
						
						var lAgreement=$('#agreement').prop('checked')
						if(lPurchaser && !lAgreement){
							alert("Please select click wrap agreement.");
							return false;
						}

						pData.status = '<%=InstrumentBean.Status.Counter_Checker_Pending.getCode()%>';
						pData.statusRemarks = 'counter approving';
						return true;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudInstrument$ = $('#contInstrument').xcrudwrapper(lConfig);
			crudInstrument = crudInstrument$.data('xcrudwrapper');
			mainForm = crudInstrument.options.mainForm;
			crudFields = crudInstrument.options.fields;
			//
			if(lPurchaser){
				getAgreementHtml();
			}
			//
			fillSupplierPurchaser(); 
			attachEvents();
			//
			
			$('#btnModify1').on('click', function() {
				var lRows=crudInstrument.getSelectedRows();
	     		if(lRows.length<=0) {
	     			alert("Please select a row.");
	     		}else{
	     			if(lRows.length==1){
	     				crudInstrument.modifyHandler();
	     				if(lRows[0].data().isGemInvoice){
		     				disableFieldsForGemInvoice();
	     				}
		     		}else{
		     			updateStatus('<%=InstrumentBean.Status.Counter_Checker_Pending.getCode()%>',$('#btnApprove'));
		     		} 	
	     		}
			});
			$('#btnApprove').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Counter_Checker_Pending.getCode()%>',$('#btnApprove'));
			});
			$('#btnReject').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Counter_Rejected.getCode()%>',$('#btnReject'));
			});
			$('#btnReturn').on('click', function() {
				updateStatus('<%=InstrumentBean.Status.Counter_Returned.getCode()%>',$('#btnReturn'));
			});
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
				document.getElementById("divFilterGrpAmt").style.display = "none";
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
			//$('#frmSearch .nav-tabs a:first').tab('show') 
			$('#btnDownloadCSV').on('click', function() {
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
                	 downloadFile('instcntr/all',null,JSON.stringify(lHistoryFilters));
          		 }else{
     				downloadFile('instcntr/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "tab": tabIdx}));
          		 }
			});
			$('#btnDownloadCSVBulk').on('click', function() {
				var lRows=crudInstrument.getSelectedRows();
				if(!lRows.length>0){
         				alert("Please select atlease one row.")
					return;
				}
				var lIds = null;
     				$.each(lRows, function(pIndex,pValue) {
	     				if (lIds==null){
	     					lIds = pValue.data().id;
	     				}else{
	     					lIds += ","+pValue.data().id;
	     				}
     				
	        		});
    				downloadFile('instcntr/downloadbulk',null,JSON.stringify({"ids":lIds}));
			});
			

			$('#btnClubbedInstruments').on('click', function() {
				viewClubbedInstruments(crudInstrument,tplClubbedInstruments);
         	});
			$('#btnUpload').on('click', function() {
         		showRemote('upload?url=instcntr', null, false);
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
	 			var lSelectedIds = [];
     			$.each(lRows, function(pIndex,pValue) {
     				lInId = pValue.data().id;
     				if(lRows.length<1) {
		 					alert("Please select a row.");
		 					return;
		 			}
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
<%--      			if  (pStatus == "<%=InstrumentBean.Status.Checker_Returned.getCode()%>"){ --%>
//      				alert("Grouped Instrument can't be returned.");
//          			return;
//      			}
     		}
			prompt("Please enter suitable reason/remarks","Reason",function(pReason){
 				if(pReason == null){
 					alert("Remarks are mandatory, please fill the remarks and try again.");
 					return false; 
 				}
				var lData = {id:lInId,status: pStatus,statusRemarks : pReason};
				lData['idList'] = lSelectedIds;
				delete lData['id'];
				pBtn$.prop('disabled',true);
				$.ajax( {
		            url: "instcntr/status/"+pStatus.toLowerCase(),
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert(tplMessage(pObj));
	            		crudInstrument.showSearchForm();
		            },
		        	error: errorHandler,
		        	complete: function() {
		        		pBtn$.prop('disabled',false);
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
			lFieldGroup = crudInstrument.options.fieldGroups.update;
			mainForm.enableDisableField(lFieldGroup, false, false);
			//enable counter changable
			if (loginData.entityType.substring(0,1) == 'Y') {
				lFieldGroup = crudInstrument.options.fieldGroups.counterSellerDisplay;
			}else if (loginData.entityType.substring(1,2) == 'Y') {
				lFieldGroup = crudInstrument.options.fieldGroups.counterBuyerDisplay;
			}
			mainForm.enableDisableField(lFieldGroup, true, false);
			
			var lSupVal=mainForm.getField('supplier').getValue();
	    	var lPurVal=mainForm.getField('purchaser').getValue();
	    	var lSupFlg=(loginData.domain===lSupVal);
	    	var lPurFlg=(loginData.domain===lPurVal);

			var lFld = mainForm.getField('extendedCreditPeriod').getValue();
			mainForm.enableDisableField(['enableExtension'],(lPurFlg && (lFld!=null)),false);			
			mainForm.enableDisableField(['extendedDueDate'],false,false);			
			mainForm.enableDisableField(['extendedCreditPeriod'],false,false);			

			mainForm.enableDisableField(['autoConvert'],false,false);			
			//last minitue request to disable all b/s link changes
			var lFields = ["autoConvert", "costBearingType", "buyerPercent","splittingPoint", "preSplittingCostBearer", "postSplittingCostBearer","chargeBearer","settleLeg3Flag","bidAcceptingEntityType","cashDiscountValue","netAmount"];
			mainForm.enableDisableField(lFields, false, false);
			var lHaircutPercent = mainForm.getField('haircutPercent').getValue();
			var lHaircutPresent=(lHaircutPercent==null || lHaircutPercent==''|| lHaircutPercent==0)?false:true;
			mainForm.enableDisableField(['adjAmount'],!lHaircutPresent,false);    		
		}
		
		function disableFieldsForGemInvoice(){
			//disable all
			var lFieldGroup = crudInstrument.options.fieldGroups.update;
			mainForm.enableDisableField(lFieldGroup, false, false);
		}
		$('#viewAgreement').on('click', function() {
			$("#dispAgreement").html(lAgreementHtml);
			$("#modelAgreement").modal('show');
		});
       	function removeFromClubbing(pParentId){
       		var ids = null;
       	 	$(".sub_chk:checked").each(function() {  
       	 		if (ids==null){
       	 			ids = $(this).attr('data-id');
       	 		}else{
       	 			ids = ids +"," + $(this).attr('data-id');
       	 		}
       		});
       	 	if(ids!=null){
       	 		$.ajax({
                	url: 'instcntr/removefromgroup/'+pParentId+'/'+ids,
        				type : 'POST',
        				success : function(pObj, pStatus, pXhr) {
        					crudInstrument.showSearchForm();
        					$('#mdlClubbedInvoice').modal('hide');
        				},
        				error : function(pObj, pStatus, pXhr) {
        					alert(pObj.responseJSON.messages);
        				}
        			});
      		}else{
      			
      		}
       	 }
       		
    	$('#btnAddToGroup').on('click', function() {
			confirm('You are about to group the unticked instruments. The ticked instruments will be excluded from grouping. Do you want to continue with the grouping process?','Group Creation Confirmation','Yes',function(pYes) {
				if (pYes) {
					var lRows=crudInstrument.getSelectedRows();
					var lSelectedData = new Map();
					$.each(lRows, function(pIndex,pValue) {
						lSelectedData.set(pValue.data().id,pValue.data().id);
					});
					var lData = [];
		     		var data = crudInstrument.options.dataTable.rows().data();
		     		data.each(function (value, index) {
		     			if(lSelectedData.get(value.id)==null && value.groupFlag!='Yes'){
		     				lData.push(value.id);
		     			}
		     		});
					
					$('#btnAddToGroup').prop('disabled',true);
					$.ajax( {
				        url: "instcntr/addgroup",
				        type: "POST",
				        data:JSON.stringify(lData),
				        success: function( pObj, pStatus, pXhr) {
				        	alert(tplMessage(pObj));
				        	crudInstrument.showSearchForm();
				        },
				        error: errorHandler,
				        complete: function() {
				        	$('#btnAddToGroup').prop('disabled',false);
				        }
					});
				}
			});
		});
    	function downloadGroupCsv(pParentId) {
     		downloadFile('instcntr/all',null,JSON.stringify({"columnNames" : crudInstrument.getVisibleColumns(), "groupInId": pParentId}));
     	}
    	function displayFilter() {
       	 if(document.getElementById("divFilterGrpAmt").style.display == "none"){
       		 document.getElementById("divFilterGrpAmt").style.display = "block";
       	 }else{
       		 document.getElementById("divFilterGrpAmt").style.display = "none";
       	 }
       }
       function showFilteredData(pVal){
       	 //filter taking the 
       	 var lCount =0;
       	if(tabData!=null && tabData.length > 0){
       		if(!pVal){
       			tabData[0]= lInboxData ;
       			 $('#Amt').val("");
       			 	lCount = lInboxData.length;
       		}else{
      			 	//filtering apply
      			 	//set to lInboxFilterData
      			 	//set it to tabData
      			 	if($('#Amt').val()==''){
      			 		alert("Please enter a value");
      			 	}else{
      			 	var lTmpFilteredData = [];
   				lInboxData.forEach(function (lData){
   				    if(lData.netAmount < $('#Amt').val()){
   				    	lTmpFilteredData.push(lData);
   				    }
   				});
      			 	tabData[0] = lTmpFilteredData;
      			 	lCount = lTmpFilteredData.length;
      			 	}
   			}
           }
       	tabIndex = 0;
			showData();
			$('#badge'+0).html(lCount<10?"0"+lCount:lCount);
      	}

       function populateInstrumentKeys(pSupplier,pPurchaser,pId,pSupClId){
   		lData = {};
    		lData['supplierCode'] = pSupplier;
    		lData['purchaserCode'] = pPurchaser;
    		lData['inId'] = pId;
    		lData['supClId'] = pSupClId;
    		$.ajax( {
		        url: "instrumentcreationkeys/lov",
		        type: "POST",
		        async:false,
		        data: JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
               	var lInstKeys = mainForm.getField('instrumentCreationKeysList');
               	lInstKeys.options.dataSetType='STATIC';
               	lInstKeys.options.dataSetValues=pObj;
               	lInstKeys.init();
		        },
		        error: function( pObj, pStatus, pXhr) {

		        },
		        complete: function() {
		        }
			});
   	}
       
    	
    	function getCustomFields(pCode,pCfId){
   		var lEntCode = pCode; 
   		if (lEntCode == null){
   			lEntCode = loginData.domain;
   		}
   		var lCustomConfig = null;
   		var lUrl = "appentity/instConf/"+lEntCode;
   		if (pCfId!=null){
   			lUrl +="?cfId="+pCfId;
   		}
    		if (lEntCode!=null){
    			$.ajax( {
    				url: lUrl,
    		        type: "GET",
    		        async:false,
    		        success: function( pObj, pStatus, pXhr) {
						if (pObj == null){
     		        		
     		        	}else{
		   		        	var lCustConf = {inputParams:pObj.inputParams};
		   		        	$('#divCustomfields').html(tplCustomfields(lCustConf));
		   		        	mainForm.getField('cfId').setValue(pObj.cfId);
		   		        	crudInstrument.options.fields = crudFields;
		   		        	crudInstrument.options.fields.push(...pObj.inputParams);
		   		        	$.each(pObj.inputParams, function(pIndex, pValue) {
		   		        		mainForm.initField(pValue);
		   					});
     		        	}
    		        },
    		        error: function( pObj, pStatus, pXhr) {

    		        },
    		        complete: function() {
    		        }
    			});
    		}
   	}

	 </script>
   	
    </body>
</html>