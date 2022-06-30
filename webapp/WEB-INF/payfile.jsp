<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.auction.bean.ObligationBean"%>
<%@page import="com.xlx.treds.auction.bean.ICICIPaymentImpl"%>
<%@page import="com.xlx.treds.auction.bean.PaymentFileBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Payment Interface</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Payment Interface" />
    </jsp:include>

	<div class="content" id="contPaymentFile">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Payment Interface</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="date" class="label">Date:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="date" placeholder="Date" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="fileType" class="label">File Type:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="fileType"><option value="">Select File Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="facilitator" class="label">Facilitator:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="facilitator"><option value="">Select Facilitator</option></select>
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
					<a href="javascript:;" class="right_links" id=btnProcess><span class="fa fa-spinner"></span> Process File</a>
					<a href="javascript:;" class="right_links" id=btnUpload><span class="fa fa-pencil"></span> Upload File</a>
					<a href="javascript:;" class="right_links" id=btnInst><span class="fa fa-download"></span> Instructions</a>
					<a href="javascript:;" class="right_links" id=btnDownload><span class="fa fa-download"></span> Download</a>
					<a href="javascript:;" class="right_links" id=btnNew><span class="fa fa-plus"></span> Generate</a>
					<a href="javascript:;" class="right_links" id=btnGenerateNOA><span class="fa fa-plus"></span> Generate NOA</a>
					<a href="javascript:;" class="right_links" id=btnUploadMod><span class="fa fa-pencil"></span>Upload Modi Req</a>
					<a href="javascript:;" class="right_links" id=btnGenerateObliModiRequests><span class="fa fa-plus"></span>Generate Obli</a>
					<a href="javascript:;" class="right_links" id=btnSettlementFile><span class="fa fa-plus"></span>Generate Settlement File</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-width="50px" data-name="id">Id</th>
									<th data-width="70px" data-name="date">Date</th>
									<th data-width="50px" data-name="fileType">File Type</th>
									<th data-width="70px" data-name="facilitator">Facilitator</th>
									<th data-width="220px" data-name="fileName">File Name</th>
									<th data-width="60px" data-name="recordCount">No of Records</th>
									<th data-width="70px" data-name="totalValue">Total Value</th>
									<th data-width="150px" data-name="generatedTime">Generated Time</th>
									<th data-width="150px" data-name="status">Status</th>
									<th data-width="150px" data-name="returnUploadedTime">Return upload time</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<div class="modal fade" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
		<div class="modal-header"><span>Generate Payment File</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
		<div class="xform" id="frmMain">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="date" class="label">Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="date" placeholder="Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="facilitator" class="label">Facilitator:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="facilitator"><option value="">Select Facilitator</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" id="fileTypeRow" style="display:none">
					<div class="col-sm-4"><section><label for="fileType" class="label">File Type:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="fileType"><option value="">Select File Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="col-sm-8">
					<section>
						<label class="checkbox"><input type=checkbox id="skipL1FileGeneration"><i></i>
							<b class="tooltip tooltip-top-left"></b>Skip Leg 1 File Generation</label>
					</section>
					<section class="view"></section>
				</div>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave> Generate</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
		</div></div></div></div>
		
		<div class="modal fade" tabindex=-1 id="mdlSettle"><div class="modal-dialog modal-md"><div class="modal-content">
		<div class="modal-header"><span>Generate Settlement File</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
		<div class="xform" id="contGenSettle">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="date" class="label">Date:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="date" placeholder="Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnGenSettle> Generate</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
		</div></div></div></div>
		<img class="hidden" src="../images/loading.gif" id="img-loading"/>
	</div>

   	<%@include file="footer1.jsp" %>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/datatables.js"></script>
	<script id="tplMessage" type="text/x-handlebars-template">
		<table class="table">
			<thead><tr><th>ID</th><th>Remarks</th></tr></thead>
			<tbody>
{{#each this}}
	<tr><td>{{act}}</td><td>{{rem}}</td></tr>
{{/each}}
			</tbody>
		</table>
	</script>
	<script type="text/javascript">
	var crudPaymentFile$,crudPaymentFile,searchForm,mainForm;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(PaymentFileBean.class).getJsonConfig()%>;
		tplMessage = Handlebars.compile($('#tplMessage').html());
		var params = (new URL(document.location)).searchParams;
		var lDateParamVal = loginData.curDate;
		var lL1ParamVal;
		var tplMessage;
		if (params.get("date")!=null){
			lDateParamVal= params.get("date");
		}
		if (params.get("skipL1FileGeneration")!=null){
			lL1ParamVal= params.get("skipL1FileGeneration");
		}
		var lConfig = {
				resource: "payfile",
				newDefault:{date:lDateParamVal,skipL1FileGeneration:lL1ParamVal},
				errorHandler : function(pData) {
					var lData = JSON.parse(pData.responseText);
					var lMsg = JSON.parse(lData.messages[0]);
					alert(tplMessage(lMsg));
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudPaymentFile$ = $('#contPaymentFile').xcrudwrapper(lConfig);
		crudPaymentFile=crudPaymentFile$.data('xcrudwrapper');
		searchForm=crudPaymentFile.options.searchForm;
		mainForm=crudPaymentFile.options.mainForm;
		searchForm.setValue({date:lDateParamVal});
		crudPaymentFile.searchHandler();
		$('#btnUpload').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			showRemote('upload?url=payfile&pfId='+crudPaymentFile.selectedRowKey(lSelected.data()), null, false);
		});
		$('#btnUploadMod').on('click', function() {
			showRemote('upload?url=oblimodreq', null, false);
		});
		$('#btnDownload').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			var status = lSelected.data()["status"];
			if((status=="Return File Uploaded")){
				alert("Download avilable only for the generated files.");
			}else{
				downloadFile('payfile/contents/'+crudPaymentFile.selectedRowKey(lSelected.data()), $('#btnDownload'))
			}
		});
		
		$('#btnInst').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			location.href='oblig?adm=Y&pfId='+lSelected.data().id;
		});
		$('#frmMain #facilitator').on('change', function() {
				$('#fileTypeRow').attr('style','display:none');
				// setting debit code since filetype is mandatory
				mainForm.getField('fileType').setValue('<%=ObligationBean.TxnType.Debit.getCode()%>');
				//mainForm.enableDisableField('fileType',false,false);
			mainForm.init();
		});
		$('#btnGenerateNOA').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
	//		location.href='oblig?adm=Y&pfId='+lSelected.data().id;
			var lData = " { 'pfId' : " + lSelected.data().id + " }";
			$.ajax( {
	            url: 'batchtask/generateNOA',
	            type: 'POST',
	            data:lData,
	            success: function( pObj, pStatus, pXhr) {
	            	alert("done");
	            },
	        	error: errorHandler,
	        	complete: function() {
	        		
	        	}
	        });
		});
		$('#btnProcess').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			checkProcessing(lSelected);
		});
		
		$('#btnGenerateObliModiRequests').on('click', function() {
			var lSelected = crudPaymentFile.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			downloadFile('oblimodreq/generateObliModiRequests/'+lSelected.data().id, null,null)
		});
		var lSettleFormConfig = {
				"fields": [
						{
							"name":"date",
							"label":"Date",
							"dataType":"DATE",
							"notNull": true
						}
				]
			};
		settleForm$ = $('#contGenSettle').xform(lSettleFormConfig);
		settleForm = settleForm$.data('xform');
		
		
		$('#btnSettlementFile').on('click', function() {
			settleForm.getField('date').setValue(loginData.curDate);
			showModal($('#mdlSettle'));
		});
		
		
     	$('#btnGenSettle').on('click', function() {
     		$('#mdlSettle').modal('hide');
			downloadFile('payfile/settlementFile/'+settleForm.getField('date').getValue(), null,null)
		});

	});
	
	function process(pSelected){
		if(!$("#btnProcess").prop('processing')){
			$("#btnProcess").prop('processing',true);
			confirm('You are about to process the payment file. Are you sure?','Confirmation','Yes',function(pYes) {
				if (pYes) {
					loading$.removeClass('hidden');
					$.ajax( {
						url: 'payfile/processFile/'+pSelected.data().id ,
						type: 'GET',
						success: function( pObj, pStatus, pXhr) {
							loading$.addClass('hidden');
							alert("File processed successfully");
							$("#btnProcess").removeProp('processing');
							$('#btnSearch').click();
						},
						error: errorHandler,
						complete: function() {
							
						}
					});
				}
			});	
		}else{
			alert("File is processing please wait.....");	
		}
	}
	
	function checkProcessing(pSelected){
		$.ajax({
			url: 'payfile/paymentFileInfo/'+pSelected.data().id ,
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
				process(pSelected);
			},
			error: errorHandler,
			complete: function() {
			}
	   });
	}
	</script>
    </body>
</html>