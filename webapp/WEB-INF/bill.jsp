<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.bill.bean.BillBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>

<html lang="en">
  <head>
	
	<title>Bills generated for collection of TReDS charges.</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Bills" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contBill">		
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Bills</h1>
				</div>
			</div>

			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="billYearMonth" class="label">Bill for Year/Month:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="billYearMonth" placeholder="Bill for Year/Month" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2 state-T"><section><label for="entity" class="label">Entity Code:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="input">
						<input type="text" id="entity" placeholder="Entity Code">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2 state-T"><section><label for="billingType" class="label">Type:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="select">
						<select id="billingType"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
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
					<a href="javascript:;" class="right_links" id=btnViewBill style=''><span class="fa fa-eye"></span> View Bill</a>
					<a href="javascript:;" class="right_links" id=btnDownloadBill style=''><span class="fa fa-download"></span> Download Bill</a>
					<a href="javascript:;" class="right_links state-T" id=btnGenerateBill style=''><span class="fa fa-plus"></span> Generate Bill</a>
					<a href="javascript:;" class="right_links state-T" id=btnGenerateDaily style=''><span class="fa fa-plus"></span> Generate Daily Bill</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

							<table class="table table-bordered " id="tblData" data-selector="multiple" data-col-chooser="spnColumnChooser">
							<thead><tr>
								<th data-width="150px" data-name="billNumber" data-class-name="select-checkbox" data-sel-exclude="true">Bill Number</th>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="billingType">Bill Type</th>
								<th data-width="100px" data-name="billYearMonth">Bill for Year/Month</th>
								<th data-width="100px" data-name="billDate">Date</th>
								<th data-width="100px" data-name="entity">Entity Code</th>
								<th data-width="100px" data-name="entName">Entity Name</th>
								<th data-width="100px" data-name="entGstn">Reg. GST No</th>
								<th data-width="100px" data-name="entLine1">Address Line 1</th>
								<th data-width="100px" data-name="entLine2">Address Line 2</th>
								<th data-width="100px" data-name="entLine3">Address Line 3</th>
								<th data-width="100px" data-name="entCountry">Entity Country</th>
								<th data-width="100px" data-name="entState">Entity State</th>
								<th data-width="100px" data-name="entDistrict">Entity District</th>
								<th data-width="100px" data-name="entCity">Entity City</th>
								<th data-width="100px" data-name="entZipCode">Entity Zip Code</th>
								<th data-width="100px" data-name="fuAmount">FU Amount</th>
								<th data-width="100px" data-name="chargeAmount">Charge Amount</th>
								<th data-width="100px" data-name="cgst">CGST %</th>
								<th data-width="100px" data-name="sgst">SGST %</th>
								<th data-width="100px" data-name="igst">IGST %</th>
								<th data-width="100px" data-name="cgstSurcharge">CGST Surcharge % </th>
								<th data-width="100px" data-name="sgstSurcharge">SGST Surcharge % </th>
								<th data-width="100px" data-name="igstSurcharge">IGST Surcharge % </th>
								<th data-width="100px" data-name="cgstValue">CGST Amt.</th>
								<th data-width="100px" data-name="sgstValue">SGST Amt.</th>
								<th data-width="100px" data-name="igstValue">IGST Amt.</th>
								<th data-width="100px" data-name="totalGstValue">Total GST Amt.</th>
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
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
    		</div>
		</div>
    	</div>
    	<!-- frmMain -->
	</div>
		<div class="modal fade" id=mdlGenerateDaily tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
		<div class="modal-header"><span>Generate Daily Bill</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
		<div class="xform" id="mod-frmMain">
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
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnGen> Generate</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
		</div></div></div></div>
   	<%@include file="footer1.jsp" %>
	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>

	<script type="text/javascript">
		var crudBill$ = null,crudBill, searchForm;
		var tplMessage;
		$(document).ready(function() {
			tplMessage = Handlebars.compile($('#tplMessage').html());
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(BillBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "bill",
					autoRefresh: true,
					tableConfig: {
						lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
						lengthChange:true,
						pageLength:10,
						select: {
							style:'multiple',
							allMethod: 'all',
							clearSelectionOnPaging: true 
						}
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudBill$ = $('#contBill').xcrudwrapper(lConfig);
			crudBill=crudBill$.data('xcrudwrapper');
			searchForm=crudBill.options.searchForm;
			var lModifyConfig = {
					"fields": [
						{
							"name":"date",
							"label":"Date",
							"dataType":"DATE",
							"notNull": true
						}
					]
				};
			modForm$ = $('#mod-frmMain').xform(lModifyConfig);
			modForm = modForm$.data('xform');
			
			$('#btnViewBill').on('click', function() {
				var lRows=crudBill.getSelectedRows();
				if (lRows==null || lRows.length==0) {
					alert("Please select a row");
					return;
				}
				if (lRows.length>1){
					alert("Please select only one row");
					return;
				}
				$.each(lRows, function(pIndex,pValue) {
					showRemote('bill/taxinvoice/'+pValue.data().id+'?loginKey='+loginData.loginKey, "modal-lg", false, "Tax Invoice");
				});

			});
			
			$('#btnDownloadBill').on('click', function() {
				
				var lRows=crudBill.getSelectedRows();
				if (lRows==null || lRows.length==0) {
					alert("Please select a row");
					return;
				}
				var lData = [];
				$.each(lRows, function(pIndex,pValue) {
					lData.push(pValue.data().id);
				});
				if(lData){
					downloadFile('bill/taxinvoicezip',null,JSON.stringify({"ids":lData }) );
				}
			});
			

			$('#btnGenerateBill').on('click', function() {
				var lData = " { ";
				lData += " 'billYearMonth' : '" + searchForm.getField("billYearMonth").getValue() + "' ";
				lData += ", 'billtype':'M'}";
				var lBillYearMth= searchForm.getField("billYearMonth").getValue();
				if(lBillYearMth==null){
					alert("Please select a Year and Month for generating bills.", "Generate bills.");
					return;
				}
				generateBills(lBillYearMth,lData);
			});
			$('#btnGen').on('click', function(){
				var lData = " { ";
				lData += " 'billYearMonth' : '" + modForm.getField('date').getValue() + "' ";
				lData += ", 'billtype':'D' }";
				var lBillDate= modForm.getField('date').getValue();
				if(lBillDate==null){
					alert("Please select a Date for generating bills.", "Generate bills.");
					return;
				}
				generateBills(lBillDate,lData);
			});
			$('#btnGenerateDaily').on('click', function() {
				$('#mdlGenerateDaily').modal('show');
			});
		});
		
		function generateBills(pBillDate,pData){
			confirm("You are about to generate bills for " + pBillDate + ". Are you sure?",null,null,function(pConf){
				if (pConf) {
					$.ajax( {
			            url: 'batchtask/generateBills',
			            type: 'POST',
			            data:pData,
			            success: function( pObj, pStatus, pXhr) {
			            	alert(tplMessage(pObj));
			            },
			        	error: errorHandler,
			        	complete: function() {
			        	}
				    });
				}
			});	
		}

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

</body>
</html>