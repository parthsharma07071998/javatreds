<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.OutsideSettlementReqBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
boolean lTredsFin = request.getParameter("fin")!=null;
boolean lAdm = request.getParameter("adm")!=null;
if (!lAdm) lAdm = lTredsFin;
%>
<html>
	<head>
		<title>Outside Settlement Request</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Outside Settlement Request" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contOutsideSettlementReq">		
		<div id="frmSearch" style="display:none">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Outside Settlement Request</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin" id=divFilter>
				<fieldset class="form-horizontal">
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
					<a href="javascript:;" class="right_links btn-group0 btn-group2 secure"  id=btnModify  data-seckey="outsettle-save"  ><span class="fa fa-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links"  id=btnView1><span class="fa fa-eye"></span> View</a>
				<% if (lAdm){
						if (lTredsFin){ %>
							<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnReject><span class="fa fa-minus"></span> Remove</a>
							<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnReturn><span class="fa fa-minus"></span> Return</a>
							<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnApprove><span class="glyphicon glyphicon-ok"></span> Approve</a>
					<% }else{ %>
							<a href="javascript:;" class="right_links btn-group0 btn-group2 secure"  data-seckey="outsettle-save" id=btnForApproval><span class="glyphicon glyphicon-ok"></span> Send For Approval</a>
					<% } %>
				<% }else{ %>
						<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnReject><span class="fa fa-minus"></span> Remove</a>
						<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnReturn><span class="fa fa-minus"></span> Return</a>
						<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="outsettle-finstatus" id=btnApprove><span class="glyphicon glyphicon-ok"></span> Approve</a>
						<a href="javascript:;" class="right_links btn-group0 btn-group2 secure"  data-seckey="outsettle-save" id=btnForApproval><span class="glyphicon glyphicon-ok"></span> Send For Approval</a>
				<% } %>
					<a></a>
				</div>
			</div>
			<div class="cloudTabs" id="setTabs">
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered" id="tblData">
							<thead><tr>
								<th data-width="100px" data-name="id">Id</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-width="100px" data-name="buyerCode">Buyer</th>
								<th data-width="100px" data-name="financierCode">Financier</th>
								<th data-width="100px" data-name="createDate">Create Date</th>
								<th data-width="100px" data-name="createrAuId">Creator Id</th>
								<th data-width="100px" data-name="approveRejectDate">Approval/Rejection Date</th>
								<th data-width="100px" data-name="approveRejectAuId">Approver/Rejector Id</th>
								<th data-visible="false" data-name="tab"></th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
			</div>
		</div>
		
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Outside Settlement Request</h1>
				</div>
			</div>
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="buyerCode" class="label">Buyer Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="buyerCode" placeholder="Buyer Code" readonly>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="financierCode" class="label">Financier Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="financierCode" placeholder="Financier Code" readonly>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2" hidden><section><label for="id" class="label">Id:</label></section></div>
					<div class="col-sm-4" hidden>
						<section class="input">
						<input type="text" id="id" placeholder="Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2" hidden><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-4" hidden>
						<section class="select">
						<select id="status"><option value="" readonly>Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div id="divFuDetails">
					</div>
				</div>
			</fieldset>
			<fieldset id="outSettleDetailList">
				<div style="display:none" id="outSettleDetailList-frmSearch">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-group nonView childBtns">
								<button type="button" class="btn btn-default" id="outSettleDetailList-btnNew"><span class="fa fa-plus"></span></button>
								<button type="button" class="btn btn-default" id="outSettleDetailList-btnModify"><span class="fa fa-pencil"></span></button>
								<button type="button" class="btn btn-default" id="outSettleDetailList-btnRemove"><span class="fa fa-minus"></span></button>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">
							
							<table class="table table-bordered table-condensed" id="outSettleDetailList-tblData" >
								<thead><tr>
									<th data-visible="false" data-name="id" data-data="id">Id</th>
									<th data-width="100px" data-name="osrId" data-data="osrId">OsrId</th>
									<th data-width="100px" data-name="paymentRefNo" data-data="paymentRefNo">Payment Ref No</th>
									<th data-width="100px" data-name="accName" data-data="accName">Account Name</th>
									<th data-width="100px" data-name="bankName" data-data="bankName">Bank Name</th>
									<th data-width="100px" data-name="ifsc" data-data="ifsc">IFSC</th>
									<th data-width="100px" data-name="branchName" data-data="branchName">Branch Name</th>
									<th data-width="100px" data-name="accountNo" data-data="accountNo">Account No</th>
									<th data-width="100px" data-name="amount" data-data="amount">Amount</th>
									<th data-width="100px" data-name="date" data-data="date">Date</th>
									<th data-width="100px" data-name="type" data-data="type">Type</th>
									<th data-visible="false" data-name="recordVersion" data-data="recordVersion">RecordVersion</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</div>
				<div class="modal fade" tabindex=-1><div class="modal-dialog modal-xl"><div class="modal-content"><div class="modal-body"><div class="container">
				<div id="outSettleDetailList-frmMain" class="xform">
					<!-- <header>Outside Settlement Details</header> -->
						<fieldset>
							<div class="row">
								<div class="col-sm-2" hidden><section><label for="outSettleDetailList-osrId" class="label" >OsrId:</label></section></div>
								<div class="col-sm-4" hidden>
									<section class="input">
									<input type="text" id="outSettleDetailList-osrId" placeholder="OsrId">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-amount" class="label">Amount:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-amount" placeholder="Amount">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-paymentRefNo" class="label">Payment Ref No:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-paymentRefNo" placeholder="Payment Ref No">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-date" class="label">Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="outSettleDetailList-date" placeholder="Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-type" class="label">Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="outSettleDetailList-type"><option value="">Select Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<hr></hr>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="outSettleDetailList-accName" class="label">Account Name:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-accName" placeholder="Account Name">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-bankName" class="label">Bank Name:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-bankName" placeholder="Bank Name">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="outSettleDetailList-ifsc" class="label">IFSC:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-ifsc" placeholder="IFSC">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="outSettleDetailList-branchName" class="label">Branch Name:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-branchName" placeholder="Branch Name">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="outSettleDetailList-accountNo" class="label">Account No:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="outSettleDetailList-accountNo" placeholder="Account No">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
						</fieldset>
					<footer>
						<div class="btn-group">
							<button type="button" class="btn btn-primary" id="outSettleDetailList-btnSave"><span class="fa fa-save"></span></button>
							<button type="button" class="btn btn-close" id="outSettleDetailList-btnClose"><span class="fa fa-close"></span></button>
						</div>
					</footer>
				</div>
				</div></div></div></div></div>
			</fieldset>
			<footer>
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-primary " id=btnApproveFinal><span class="fa fa-save"></span> Approve</button>
					<button type="button" class="btn btn-primary " id=btnSave><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div></div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script id="tplFuDetails" type="text/x-handlebars-template">	
		<table border='1px'>
				<thead>
                     <tr>
						<th>FuId {{id}}</th>
                        <th>Amount</th>
                     </tr>
                </thead>
                <tbody>
                      <tr>
						{{#each this}}
							<td>{{@key}}</td>
                         	<td>{{this}}</td>
						{{/each}}
                         
                      </tr>
                </tbody>
			
		</table>
	</script>
	
	<script type="text/javascript">
		var crudOutsideSettlementReq$ = null;
		var crudOutsideSettlementReq = null;
		var mainForm = null;
		var mainFormModDetails = null;
		var data,tabData,tabIdx=0;
		var tplFuDetails;
		var lAmt ;
		var lTredsFin = <%=lTredsFin%>
		var isViewMode,isApprove;
		$(document).ready(function() {
			tplFuDetails = Handlebars.compile($('#tplFuDetails').html());
			var lTabsPurchaser ='<ul class="cloudtabs nav nav-tabs">'
				 +'<li class="active"><a href="#tab0" data-toggle="tab">Pending <span id="badge0" class="badge bg-yellow"></span></a></li>'
				 +'<li><a href="#tab1" data-toggle="tab">Sent For Approval<span id="badge1" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab2" data-toggle="tab">Returned <span id="badge2" class="badge bg-red"></span></a></li>'
				 +'<li><a href="#tab3" data-toggle="tab">Approved <span id="badge3" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab4" data-toggle="tab">Rejected <span id="badge4" class="badge bg-red"></span></a></li>'
				 +'</ul>';
			var lTabsFinancier ='<ul class="cloudtabs nav nav-tabs">'
				 +'<li class="active"><a href="#tab0" data-toggle="tab">Pending <span id="badge0" class="badge bg-yellow"></span></a></li>'
				 +'<li><a href="#tab1" data-toggle="tab">Returned<span id="badge1" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab2" data-toggle="tab">Approved <span id="badge2" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab3" data-toggle="tab">Rejected <span id="badge3" class="badge bg-red"></span></a></li>'
				 +'</ul>';
			var lBuyer = (loginData.entityType.substring(1,2) == 'Y');
			var lTreds = (loginData.domain == 'TREDS');
			var lFin = (loginData.entityType.substring(2,3) == 'Y');
			var lTab = null;
			if (lBuyer||(lTreds && !lTredsFin)){
				lTab = lTabsPurchaser;
			}
			if (lFin || (lTreds && lTredsFin)){
				lTab = lTabsFinancier;
			}
			$("#setTabs").html(lTab);
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(OutsideSettlementReqBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "outsetreq",
					autoRefresh: true,
					preSearchHandler: function(pFilter) {
						if (lTredsFin){
							pFilter.isFin=true;
						}
						return true;
					},
					postSearchHandler: function(pObj) {
						tabData = [];
						$.each(pObj,function(pIdx,pValue){
							var lData=tabData[pValue.tab];
							if (lData==null) {
								lData=[];
								tabData[pValue.tab]=lData;
							}
							lData.push(pValue);
						});
						var lIdx;
						$('.nav-tabs li a').each(function (index, element) {
							lIdx = element.attributes['href'].value.substring(4);
							if (tabData[lIdx]==null) tabData[lIdx]=[];
							var lCount = tabData[lIdx]?tabData[lIdx].length:0;
							$('#badge'+lIdx).html(lCount<10?"0"+lCount:lCount);
						});
						showData();
						return false;
					},
					postNewHandler: function(){
						$('#btnSave').show();
						$('#btnForApproval').hide();
						return true;
					},
					postModifyHandler: function(pObj){
						lAmt=0;
						$('#divFuDetails').html(tplFuDetails(pObj.fuDetails));
						var lMap = pObj.fuDetails;
						Object.keys(lMap).forEach(function(key) {
							lAmt = lAmt + lMap[key];
						});
						$('.childBtns').show();
						$('#btnApproveFinal').hide();
						
						if(isViewMode) {
							$('.childBtns').hide();
							if (isApprove){
								$('#btnApproveFinal').show();
							}
						}
						return true;
					},
					preSaveHandler: function(pObj){
						var lTmpAmt=0;
						$.each( pObj.outSettleDetailList, function( key, value ) { 
							lTmpAmt = lTmpAmt + parseFloat(value.amount);
						});
						if(lAmt!=lTmpAmt){
							alert("Amount Mismatch");
							return false;
						}
						return true;
					},
					postCloseHandler: function (pObj) {
						isViewMode = false;
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
			crudOutsideSettlementReq$ = $('#contOutsideSettlementReq').xcrudwrapper(lConfig);
			crudOutsideSettlementReq = crudOutsideSettlementReq$.data('xcrudwrapper');
			mainForm = crudOutsideSettlementReq.options.mainForm;
			mainFormModDetails = mainForm.getField('outSettleDetailList').options.mainForm;
			mainForm.getField('outSettleDetailList').options.postNewHandler=function(){
				mainFormModDetails.getField('osrId').setValue(mainForm.getField('id').getValue());
				return true;
			};
			
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
				var lRef1 = $(event.target).attr('href');         // active tab
				var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
				tabIdx = parseInt(lRef1.substring(4));
				if (lRef2)
					$('.btn-group'+lRef2.substring(4)).addClass('hidden');
				$('.btn-group'+tabIdx).removeClass('hidden');
	      	   showData();
			});
			$('#frmSearch .nav-tabs a:first').tab('show');
// 			$('.right_links').addClass('hidden');
			$('#btnApproveFinal').on('click', function() {
				updateStatus('<%=OutsideSettlementReqBean.Status.Approved.getCode()%>',$('#btnApprove'));
			});
			$('#btnReject').on('click', function() {
				updateStatus('<%=OutsideSettlementReqBean.Status.Rejected.getCode()%>',$('#btnReject'));
			});
			$('#btnReturn').on('click', function() {
				updateStatus('<%=OutsideSettlementReqBean.Status.Returned.getCode()%>',$('#btnReject'));
			});
			$('#btnForApproval').on('click', function() {
				updateStatus('<%=OutsideSettlementReqBean.Status.Sent.getCode()%>',$('#btnForApproval'));
			});
			$('#btnView1').on('click', function() {
				var lRow = crudOutsideSettlementReq.getSelectedRow();
				if(lRow.length < 1 || lRow.length == 0){
					alert("Please select a row");
				}else{
					isViewMode = true;
					crudOutsideSettlementReq.viewHandler(null,[lRow.data().id]);					
				}
			});
			$('#btnApprove').on('click', function() {
				var lRow = crudOutsideSettlementReq.getSelectedRow();
				if(lRow.length < 1 || lRow.length == 0){
					alert("Please select a row");
				}else{
					isApprove = true;
					isViewMode = true;
					crudOutsideSettlementReq.viewHandler(null,[lRow.data().id]);
				}
			});
			
		});
		
		function showData() {
			crudOutsideSettlementReq.options.dataTable.rows().clear();
			if (tabData && (tabData[tabIdx] != null)){
				crudOutsideSettlementReq.options.dataTable.rows.add(tabData[tabIdx]).draw();
			}
		}
		
		function updateStatus(pStatus, pBtn$) {
			var lRow = crudOutsideSettlementReq.getSelectedRow();
			if(lRow.length < 1 || lRow.length == 0){
				alert("Please select a row");
			}else{
				var lData = {id:lRow.data().id,status: pStatus};
				pBtn$.prop('disabled',true);
				$.ajax( {
		            url: "outsetreq/status",
		            type: "PUT",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudOutsideSettlementReq.showSearchForm();
	            		});
		            },
		        	error: errorHandler,
		        	complete: function() {
		        		pBtn$.prop('disabled',false);
		        	}
		        });	
			}
		}
		
	</script>


</body>
</html>