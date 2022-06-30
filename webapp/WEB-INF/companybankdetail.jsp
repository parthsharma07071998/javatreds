<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyBankDetailBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lEntityId = request.getParameter("entityId"); 
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<html>
	<head>
		<title>TREDS | Registration</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	</head>
	<body class="page-body">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Banking Details" />
	</jsp:include>

	<div class="content" id="contCompanyBankDetail">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Banking Details</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> Add Bank Details</a>
					<a></a>
				</div>
			</div>
			
			<div class="tab-pane panel panel-default">
				<div class="row">
					<div id="divBankDetails">
					</div>
				</div>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<div style="display:none" id="frmMain" class="xform">
			<div>
			<div class="cloudTabs">
				<ul class="cloudtabs nav nav-tabs">
					<li class="active1"><a href="#tabGeneral" data-toggle="tab">General
							Information </a></li>
					<li ><a href="#tabGenera2" data-toggle="tab">General
							second </a></li>
				</ul>
			</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<div class="tab-content no-padding">
<div id="tabGeneral" class="tab-pane active1">
							
					<div class="row">
						<div class="col-sm-2"><section><label for="bank" class="label">Bank:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="bank"><option value="">Select Bank</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="accType" class="label">Type of Account:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="accType"><option value="">Select Type of Account</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="ifsc" class="label">IFSC:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="ifsc" placeholder="IFSC">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="branchName" class="label">Branch Name:</label></section></div>
						<div class="col-sm-4">
							<section><label id="branchName" class="label"></label></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="accNo" class="label">Account Number:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="accNo" placeholder="Account Number">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="bankingType" class="label">Type of Banking Facilities:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="bankingType"><option value="">Select Type of Banking Facilities</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="defaultAccount" class="label">Lead Bank:</label></section></div>
						<div class="col-sm-4">
							<section>
							<label class="checkbox"><input type=checkbox id="leadBank"><i></i>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="defaultAccount" class="label">Designated Transaction A/c:</label></section></div>
						<div class="col-sm-4">
							<section>
							<label class="checkbox"><input type=checkbox id="defaultAccount"><i></i>
							<b class="tooltip tooltip-top-left"></b>(Default a/c for settlement)</label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">
							<h4>
							<span class="fa fa-building">
							</span>
							Branch Address
							</h4>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4"><section><label for="line1" class="label">Line 1:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="line1" placeholder="Line 1">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="line2" class="label">Line 2:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="line2" placeholder="Line 2">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="line3" class="label">Line 3:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="line3" placeholder="Line 3">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4"><section><label for="city" class="label">City:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="city" placeholder="City">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="zipCode" class="label">Zip Code:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="zipCode" placeholder="Zip Code">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="district" class="label">District:</label></section></div>
								<div class="col-sm-8">
									<section class="input">
									<input type="text" id="district" placeholder="District">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="state" class="label">State:</label></section></div>
								<div class="col-sm-8">
									<section class="select">
									<select id="state"><option value="">Select State</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-4"><section><label for="country" class="label">Country:</label></section></div>
								<div class="col-sm-8">
									<section class="select">
									<select id="country"><option value="">Select Country</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>
</div>

<div id="tabGenera2" class="tab-pane">

					<div class="row">
						<div class="col-sm-12">
							<h4>
							<span class="fa fa-user">
							</span>
							Relationship Manager Contact Details
							</h4>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="salutation" class="label">Title:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="salutation"><option value="">Select Title</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="state" class="label">First Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="firstName" placeholder="First Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="state" class="label">Middle Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="middleName" placeholder="Middle Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="state" class="label">Last Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="lastName" placeholder="Last Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="email" placeholder="Email">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="mobile" class="label">Mobile:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="mobile" placeholder="Mobile">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="telephone" class="label">Telephone:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="telephone" placeholder="Telephone">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="fax" class="label">FAX:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="fax" placeholder="FAX">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
		    		<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
				</div>
</div>


					</div>
					</div>
					</div>

				</fieldset>
			</div>
		</div>
		</div>
		<!-- frmMain -->

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script id="tplBankDetails" type="text/x-handlebars-template">
		{{#each this}}
		<div class="col-md-6 col-sm-12">
			<div class="lighter-gray-bx">
				<div class="panel-heading">
					<h3 class="panel-title">{{bank}} {{#if defaultAccount}}- Default{{/if}}</h3>
					<div class="panel_right_info pull-right">
						{{#ifCond creatorIdentity '!=' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyBankDetail.modifyHandler(null,[{{id}},{{isProvisional}}],false)"><i class="glyphicon glyphicon-pencil"></i></a>
						&nbsp;
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyBankDetail.removeHandler(null,[[{{id}}]])"><i class="glyphicon glyphicon-trash"></i></a>
						{{/ifCond}}
						{{#ifCond creatorIdentity '==' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyBankDetail.viewHandler(null,[{{id}},{{isProvisional}}])"><i class="glyphicon glyphicon-eye-open"></i> </a>		
						{{/ifCond}}
						<a></a>
					</div>
				</div>
			<div class="row horiz_table" >
				<div class="col-md-6">
					<table class="table">
						<tbody>
							<tr>
								<td class="table-header">Type</td>
								<td class="table-content"><b>{{accType}}</b></td>
							</tr>	
							<tr>
								<td class="table-header">Address</td>
								<td class="table-content">{{line1}}</td>
							</tr>
							<tr>
								<td class="table-header">Address 2</td>
								<td class="table-content">{{#if line3}}{{line3}}{{/if}}</td>
							</tr>
							<tr>
								<td class="table-header">State</td>
								<td class="table-content">{{state}}</td>
							</tr>
							<tr>
								<td class="table-header">Zip Code</td>
								<td class="table-content">{{zipCode}}</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="col-md-6">
					<table class="table">
						<tbody>
							<tr>
								<td class="table-header">Number</td>
								<td class="table-content">{{accNo}}</td>
							</tr>
							<tr>
								<td class="table-header">Address 1</td>
								<td class="table-content">{{#if line2}}{{line2}}{{/if}}</td>
							</tr>
							<tr>
								<td class="table-header">City</td>
								<td class="table-content">{{city}}</td>
							</tr>
							<tr>
								<td class="table-header">District</td>
								<td class="table-content">{{district}}</td>
							</tr>
							<tr>
								<td class="table-header">Country</td>
								<td class="table-content">{{country}}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			</div>
		</div>
		{{/each}}
		{{^if this}}No Data!{{/if}}
	</script>
	<script type="text/javascript">
	var crudCompanyBankDetail$ = null;
	var crudCompanyBankDetail = null;
	var mainForm = null;
	var tplBankDetails;
	$(document).ready(function() {
		tplBankDetails = Handlebars.compile($('#tplBankDetails').html());
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyBankDetailBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "companybankdetail",
				keyFields:["id","isProvisional"],
				autoRefresh: true,
				preSearchHandler: function(pFilter) {
					pFilter.cdId=<%=lEntityId%>;
					pFilter.isProvisional=<%=lIsProv%>;
					return true;
				},
				postSearchHandler: function(pData) {
					if (pData.creatorIdentity == 'J'){
						$('#btnNew').hide();
					}
					$('#divBankDetails').html(tplBankDetails(pData.list));
					return false;
				},
				postModifyHandler: function(pObj) {
					checkIFSC();
					setBankingType(pObj.bankingType);
					setOldModifiedValue(pObj.modifiedData);
					return true;
				},
				postNewHandler: function(pObj) {
					checkIFSC();
					setBankingType(null);
					return true;
				},
				preSaveHandler: function(pObj) {
					pObj.cdId=<%=lEntityId%>;
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudCompanyBankDetail$ = $('#contCompanyBankDetail').xcrudwrapper(lConfig);
		crudCompanyBankDetail = crudCompanyBankDetail$.data('xcrudwrapper');
		mainForm = crudCompanyBankDetail.options.mainForm;
		$('#bankingType').on('change', function() {
			setBankingType(mainForm.getField('bankingType').getValue());
		});
		$('#accType').on('change', function() {
			setAccountType(mainForm.getField('accType').getValue());
		});
		$('#ifsc').on('blur', function() {
			checkIFSC();
		});
		$('#bank').on('change', function() {
			checkIFSC();
		});
		
	});
	function checkIFSC()
	{
    	$('#branchName').html("");
		var lBank = mainForm.getField('bank').getValue();
		var lIfsc = mainForm.getField('ifsc').getValue();
		if (lBank!=null && lIfsc != null) {
			if(!(lIfsc.substr(0,4) == (lBank.substr(0,4))))
			{
	        	$('#branchName').html("IFSC Code does not belong to the selected bank.");
				//alert("The IFSC Code does not belong to the selected Bank.");
				return;
			}
			var lUrl= '<%=request.getContextPath()%>/rest/bbdtl/'+lIfsc;
			$.ajax({
		        url: lUrl,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#branchName').html(pObj['branchname']);
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	$('#branchName').html("INVALID IFSC Code");
		        }
		    });
		}
	}
	function setBankingType(pVal) {
		var lCons = pVal==='<%=CompanyBankDetailBean.BankingType.Consortium.getCode()%>';
		mainForm.enableDisableField('leadBank', lCons, !lCons);
	}
	function setAccountType(pVal) {
		 var lTearmLoan = (pVal!=null&&pVal=='<%=CompanyBankDetailBean.AccType.Term_Loan.getCode()%>');
		var lFields =  [ 'ifsc' ];
		mainForm.enableDisableField(lFields, true, lTearmLoan);
		mainForm.alterField(lFields, !lTearmLoan, false);
	}
    function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudCompanyBankDetail.options.fieldGroups.update;
     	for(var lPtr=0; lPtr < lFieldGroup.length;  lPtr++){
     		$('#'+lFieldGroup[lPtr]).css('border-color','');
     	}
     	//mark modified fields red
     	if(pModifiedJson!=null&&pModifiedJson!=''){
     		var lData = pModifiedJson;
     		if(lData!=null){
     			for(var key in lData){
     				//$('#ov-'+key).html(lData[key]);
     				$('#'+key).css('border-color','red');
     			}
     		}
     	}
     }

	</script>
	</body>
</html>