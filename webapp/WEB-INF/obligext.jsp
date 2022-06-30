<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.treds.auction.bean.ObligationExtensionBean"%>
<%
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
	<head>
		<title>TREDS - Obligation Extension</title>
		<%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	</head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Obligation Extensions" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	
	<div class="content" id="contObligationExtension">
		<!-- frmSearch -->
		<div id="frmSearch" style="display:none">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Obligation Extensions</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="obId" class="label">Obligation Id:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="obId" placeholder="Obligation Id">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="oldDate" class="label">Obligation Date:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="oldDate" placeholder="Obligation Date" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2 state-F"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
						<div class="col-sm-2 state-F">
							<section class="select">
							<select id="purchaser"><option value="">Select Purchaser</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
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
					<a href="javascript:;" class="right_links btn-group0 btn-group1 btn-group2 secure" data-seckey="obligext-save" id=btnRemove><span class="glyphicon glyphicon-thrash"></span> withdraw</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-save" id=btnForApproval><span class="glyphicon glyphicon-ok"></span> Send For Approval</a>
					<a href="javascript:;" class="right_links btn-group2 secure" data-seckey="obligext-save" id=btnRtnBid><span class="glyphicon glyphicon-ok"></span> Return Bid</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-wf" id=btnRtnFin><span class="glyphicon glyphicon-ok"></span> Return</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-wf" id=btnPlaceBid><span class="glyphicon glyphicon-pencil"></span> Place Bid</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-wf" id=btnSubBid><span class="glyphicon glyphicon-ok"></span> Submit Bid</a>
					<a href="javascript:;" class="right_links btn-group0 secure" data-seckey="obligext-wf" id=btnReject><span class="glyphicon glyphicon-remove"></span> Reject</a>
					<a href="javascript:;" class="right_links btn-group2 secure" data-seckey="obligext-save" id=btnApprove><span class="glyphicon glyphicon-ok"></span> Approve</a>
					<a></a>
				</div>
			</div>
			<div class="cloudTabs" id="setTabs">
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered" data-selector="multiple" id="tblData">
								<thead><tr>
								<th data-width="150px" data-class-name="select-checkbox" data-name="obId"> Obligation Id</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-width="150px" data-name="purchaserName">Purchaser</th>
								<th data-width="150px" data-name="financierName">Financier</th>
								<th data-width="100px" data-name="oldDate">Obligation Date</th>
								<th data-width="100px" data-name="oldAmount">Old Amount</th>
								<th data-width="100px" data-name="newDate">Extended Due date</th>
								<th data-width="100px" class="hidePur" data-name="extendedBidRate">Extended Bid Rate</th>
								<th data-width="100px" class="hidePur" data-name="newAmount">New Amount</th>
								<th data-width="100px" data-name="tenor">Extended tenor</th>
								<th data-width="100px" class="hidePur hideFin" data-name="tredsCharge">Platform Charge</th>
								<th data-width="100px" class="hidePur" data-name="originalInterest">Original Interest</th>
								<th data-width="100px" class="hidePur" data-name="newInterest">New Interest</th>
								<th data-width="100px" class="hidePur1" data-name="penaltyRateApplied">Penalty Rate Applied</th>
								<th data-width="100px" data-name="remarks">Remarks</th>
								<th data-width="100px" data-name="upfrontCharge">Up Front Charge</th>
								<th data-visible="false" data-name="tab"></th>
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
					<h1 class="title">Obligation Extensions</h1>
				</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label class="label">Purchaser:</label></section></div>
						<div class="col-sm-4"><section id="lbl-purchaser"></section></div>
						<div class="col-sm-2"><section><label class="label">Financier:</label></section></div>
						<div class="col-sm-4"><section id="lbl-financier"></section></div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label class="label">Obligation Date:</label></section></div>
						<div class="col-sm-4"><section id="lbl-oldDate"></section></div>
						<div class="col-sm-2"><section><label class="label">Amount:</label></section></div>
						<div class="col-sm-4"><section id="lbl-oldAmount"></section></div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="newDate" class="label">Extended Due date:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="newDate" placeholder="Extended Due date" data-role="datetimepicker" onBlur="javascript:computeNewAmount()">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="upfrontCharge" class="label">Upfront Charge:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="upfrontCharge"><option value="">Upfront Charge</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" >	
						<div class="col-sm-2"><section><label class="label">New Amount:</label></section></div>
						<div class="col-sm-4"><section id="lbl-newAmount"></section></div>
					</div>
					<div class="row" >
						<div class="col-sm-2"><section><label class="label">Interest Rate %:</label></section></div>
						<div class="col-sm-4"><section id="lbl-interestRate"></section></div>
						<div class="col-sm-2"><section><label class="label">Interest:</label></section></div>
						<div class="col-sm-4"><section id="lbl-interest"></section></div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label class="label">Penalty Rate %:</label></section></div>
						<div class="col-sm-2"><section id="lbl-penaltyRate"></section></div>
						<div class="col-sm-2 text-right"><a class="btn btn-link" href="javascript:;" onClick="javascript:showRates()">View Rates</a></div>
						<div class="col-sm-2"><section><label class="label">Penalty:</label></section></div>
						<div class="col-sm-4"><section id="lbl-penalty"></section></div>
					</div>
					<div class="row" >
						<div class="col-sm-2"><section><label for="remarks" class="label">Remarks:</label></section></div>
						<div class="col-sm-8">
							<section class="input">
							<input type="text" id="remarks" placeholder="Remarks">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<input type="hidden" id="penalty">
								<input type="hidden" id="penaltyRate">
								<input type="hidden" id="newAmount">
								<input type="hidden" id="obId">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
				</fieldset>
			</div>
		</div>
		<!-- frmMain -->
	</div>
	
	<div class="modal fade" tabindex=-1 id="mdlSeekExt"><div class="modal-dialog modal-xl"><div class="modal-content">
	<div class="modal-header"><span>Seek Extension</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body modal-no-padding">
	<div class="xform" id="contSeekExt">
		<fieldset>
			<div class="row">
				<div class="col-sm-4"><section><label for="extendedBidRate" class="label">Extended Bid Rate: <span></span>:</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<input type="text" id="extendedBidRate" placeholder="Rate">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
			</div>
			<div id='tplExtData'>
			</div>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSubmitBid> Place & Submit</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnPlaceBidFinal>Place </button>
<!-- 							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button> -->
						</div>
					</div>
				</div>
			</div>
		</fieldset>
	</div>
	</div></div></div></div>
					
	<%@include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script id="tplRates" type="text/x-handlebars-template">
		<table class="table"><thead><tr><th>Upto Days</th><th>Penalty Rate %</th></tr></thead><tbody>
		{{#each penaltyList}}
		<tr><td>{{uptoDays}}</td><td>{{#formatDec}}{{rate}}{{/formatDec}}</td></tr>
		{{/each}}
		</tbody></table>
	</script>	
	<script type="text/javascript">
		var crudObligationExtension$,crudObligationExtension,mainForm,tplRates;
		var data,tabData,tabIdx=0;
		var modifyFlag = <%=StringUtils.isNotBlank(lModify)%>;//flag for error handling
		$(document).ready(function() {
			var lTabsPurchaser ='<ul class="cloudtabs nav nav-tabs">'
				 +'<li class="active"><a href="#tab0" data-toggle="tab">Pending <span id="badge0" class="badge bg-yellow"></span></a></li>'
				 +'<li><a href="#tab1" data-toggle="tab">Sent For Approval<span id="badge1" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab2" data-toggle="tab">For Bid Approval <span id="badge2" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab3" data-toggle="tab">Approved <span id="badge3" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab4" data-toggle="tab">Rejected <span id="badge4" class="badge bg-red"></span></a></li>'
				 +'<li><a href="#tab5" data-toggle="tab">Expired <span id="badge5" class="badge bg-green"></span></a></li>'
				 +'</ul>';
			var lTabsFinancier ='<ul class="cloudtabs nav nav-tabs">'
				 +'<li class="active"><a href="#tab0" data-toggle="tab">Pending <span id="badge0" class="badge bg-yellow"></span></a></li>'
				 +'<li><a href="#tab1" data-toggle="tab">Sent For Approval<span id="badge1" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab2" data-toggle="tab">Approved <span id="badge2" class="badge bg-green"></span></a></li>'
				 +'<li><a href="#tab3" data-toggle="tab">Rejected <span id="badge3" class="badge bg-red"></span></a></li>'
				 +'<li><a href="#tab4" data-toggle="tab">Expired <span id="badge4" class="badge bg-green"></span></a></li>'
				 +'</ul>';
			var lBuyer = (loginData.entityType.substring(1,2) == 'Y');
			var lFin = (loginData.entityType.substring(2,3) == 'Y');
			var lTab = null;
			if (lBuyer){
				lTab = lTabsPurchaser;
			}
			if (lFin){
				lTab = lTabsFinancier;
			}
			$("#setTabs").html(lTab);
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationExtensionBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "obligext",
					modify: <%=lModify%>,
					autoRefresh: true,
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
					postModifyHandler:function(pData) {
						data = pData;
						setOtherValues();
						modifyFlag=false;//reset error handling flag
						return true;
					},
					preSaveHandler:function(pObj) {
						computeNewAmount();
						return true;
					},
					errorHandler:function(pXhr, pStatus, pError) {
						if (modifyFlag) {
							var lMsg = null;
							try {
								var lErrObj = JSON.parse(pXhr.responseText);
								lMsg = lErrObj.messages[0];
							} catch (e) {
							}
							if (!lMsg) lMsg = "Some error occurred : " + pXhr.responseText;
						    alert(lMsg, "Error",function(){
								window.history.back();
						    });
						} else
							errorHandler(pXhr, pStatus, pError);
						
					}
			};
			var lExtFormConfig = {
				"fields": [
					{
						"name":"extendedBidRate",
						"label":"Extended Bid Rate",
						"dataType":"DECIMAL",
						"integerLength":3,
						"decimalLength":2,
						"notNull": true,
						"minValue": 0.01
					}
				]
			};
	     	extForm$ = $('#contSeekExt').xform(lExtFormConfig);
			extForm = extForm$.data('xform');
			lConfig = $.extend(lConfig, lFormConfig);
			crudObligationExtension$ = $('#contObligationExtension').xcrudwrapper(lConfig);
			crudObligationExtension = crudObligationExtension$.data('xcrudwrapper');
			mainForm = crudObligationExtension.options.mainForm;
			tplRates=Handlebars.compile($('#tplRates').html());
			
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
				var lRef1 = $(event.target).attr('href');         // active tab
				var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
				if (lBuyer){
					if (lRef1 == '#tab0' || lRef1 == '#tab1'){
						$('#tblData').DataTable().columns('.hidePur').visible( false );
					}else{
						$('#tblData').DataTable().columns('.hidePur').visible( true );
					}
					$('#tblData').DataTable().columns('.hidePur1').visible( false );
				}else{
					$('#tblData').DataTable().columns('.hidePur').visible( true );
					$('#tblData').DataTable().columns('.hidePur1').visible( true );
					$('#tblData').DataTable().columns('.hideFin').visible( false );
				}
				
				tabIdx = parseInt(lRef1.substring(4));
				if (lRef2)
					$('.btn-group'+lRef2.substring(4)).addClass('hidden');
				$('.btn-group'+tabIdx).removeClass('hidden');
	      	   showData();
			});
			$('#frmSearch .nav-tabs a:first').tab('show');
			$('.right_links').addClass('hidden');
			if (lBuyer){
				$('#tblData').DataTable().columns('.hidePur').visible( false );
				$('#tblData').DataTable().columns('.hidePur1').visible( false );
				$('.btn-group'+0).removeClass('hidden');
			}else{
				$('#tblData').DataTable().columns('.hideFin').visible( false );
				$('.btn-group'+0).removeClass('hidden');
			}
			$('#btnApprove').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.Approved.getCode()%>',$('#btnApprove'));
			});
			$('#btnReject').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.Rejected.getCode()%>',$('#btnReject'));
			});
			$('#btnForApproval').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.ForApproval.getCode()%>',$('#btnForApproval'));
			});
			$('#btnSubBid').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.BidApproval.getCode()%>',$('#btnForApproval'));
			});
			$('#btnRtnBid').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.BidReturned.getCode()%>',$('#btnForApproval'));
			});
			$('#btnRtnFin').on('click', function() {
				updateStatus('<%=ObligationExtensionBean.Status.Returned.getCode()%>',$('#btnForApproval'));
			});
			$('#btnPlaceBid').on('click', function() {
				showModal($('#mdlSeekExt'));
			});
			$('#btnSubmitBid').on('click', function() {
				placeAndSubmitBid(true);
			});
			$('#btnPlaceBidFinal').on('click', function() {
				placeAndSubmitBid(false);
			});
			$('#frmMain #upfrontCharge').on('change', function() {
				computeNewAmount();
			});
		});
		function showData() {
			crudObligationExtension.options.dataTable.rows().clear();
			if (tabData && (tabData[tabIdx] != null))
				crudObligationExtension.options.dataTable.rows.add(tabData[tabIdx]).draw();
		}

		function showRates() {
			alert(tplRates(data.penaltySetting));
		}
		function setOtherValues() {
			$('#lbl-purchaser').text(data.purchaserName);
			$('#lbl-financier').text(data.financierName);
			$('#lbl-oldDate').text(data.oldDate);
			$('#lbl-oldAmount').text(data.oldAmount);
			$('#lbl-penaltyRate').text(data.penaltyRate);
			$('#lbl-penalty').text(data.penalty);
			$('#lbl-newAmount').text(data.newAmount);
			$('#lbl-interestRate').text(data.interestRate);
			$('#lbl-interest').text(data.interest);
		}
		function computeNewAmount() {
			var lNewDate = mainForm.getField('newDate').getValue();
			var lUpfrontCharge = mainForm.getField('upfrontCharge').getValue();
			document.getElementById("btnSave").disabled = true;
			if (lUpfrontCharge != '<%=CommonAppConstants.Yes.Yes.getCode()%>'){
				lUpfrontCharge = 'N';
			}
			if (!lNewDate) {
				data.penalty='';
				data.penaltyRate='';
				data.newAmount='';
				setOtherValues();
			} else {
				$.ajax({
		            url: crudObligationExtension.options.resource + '/check/'+data.obId+'/'+lNewDate+'/'+lUpfrontCharge,
		            type: 'GET',
		            success: function( pObj, pStatus, pXhr) {
		            	mainForm.setValue(pObj);
		            	data = pObj;
		            	setOtherValues();
		            	document.getElementById("btnSave").disabled = false;
		            },
		        	error: errorHandler
		        });
			}
		}
		function updateStatus(pStatus, pBtn$) {
			var lRows = crudObligationExtension.getSelectedRows();
			if (lRows.length<=0){
				
			}else{
				var lIds= [];
				$.each(lRows, function(pIndex,pValue) {
	 				lIds.push(pValue.data().obId);
	    		});
				if(pStatus =='<%=ObligationExtensionBean.Status.BidReturned.getCode()%>'
						|| pStatus =='<%=ObligationExtensionBean.Status.Returned.getCode()%>'){
		 			prompt("Please enter suitable reason/remarks","Reason",function(pReason){
		 				if(pReason == null){
		 					alert("Remarks are mandatory, please fill the remarks and try again.");
		 					return false; 
		        		}
		 				updateStatus1(lIds, pStatus, pBtn$, pReason);			
		        	});	
				}else{
					updateStatus1(lIds, pStatus, pBtn$, null);	
				}
			}
		}
		
		function placeAndSubmitBid(pSubmit){
			if (extForm.getField('extendedBidRate').getValue() == null){
				alert("Please Fill the Rate.");
			}else{
				var lRows = crudObligationExtension.getSelectedRows();
				if (lRows.length<=0){
					
				}else{
					var lIds= [];
					$.each(lRows, function(pIndex,pValue) {
		 				lIds.push(pValue.data().obId);
		    		});
					var lData = {obIds:lIds,extendedBidRate: extForm.getField('extendedBidRate').getValue(),submit:pSubmit};
					$.ajax( {
			            url: "obligext/placebid",
			            type: "POST",
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
		            		alert("Status updated successfully", "Information", function() {
		            			crudObligationExtension.showSearchForm();
		            		});
			            },
			        	error: errorHandler,
			        	complete: function() {
			        	}
			        });	
				}
			}
		}
		function updateStatus1(pIds, pStatus, pBtn$, pRemarks){
			var lData = {obIds:pIds,status: pStatus,remarks : pRemarks};
			pBtn$.prop('disabled',true);
			$.ajax( {
	            url: "obligext/status",
	            type: "PUT",
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
	            	$('#mdlSeekExt').hide();
            		alert("Status updated successfully", "Information", function() {
            			crudObligationExtension.showSearchForm();
            		});
	            },
	        	error: errorHandler,
	        	complete: function() {
	        		pBtn$.prop('disabled',false);
	        	}
	        });	
		}
	</script>
	</body>
</html>
