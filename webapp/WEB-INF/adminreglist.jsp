<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants.CompanyApprovalStatus"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Registrations</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Registrations" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
    
    <script id="tplLocations" type="text/x-handlebars-template">
    {{#each this}}
		<div class="row">	
			<div class="col-sm-6">	
				<span><label class="label"><h4>Name: <b>{{name}}</b></h4></label></span>
			</div>
			<div class="col-sm-2"><section><label for="enableSettlement_{{id}}" class="label">Enable Settlement:</label></section></div>
			<div class="col-sm-4">
				<section>
				<label class="checkbox"><input type=checkbox id="enableSettlement_{{id}}" onchange="toggleSettlement({{id}})"><i></i>
				<b class="tooltip tooltip-top-left"></b></label>
				</section>
				<section class="view"></section>
			</div>
		</div>
		<div class="row">		
			<div class="col-sm-2"><section ><label for="settlementCLId_{{id}}" class="label">Settlement Location:</label></section></div>
			<div class="col-sm-4">
				<section class="select settlementCLId">
				<select id="settlementCLId_{{id}}"><option value="">Select Location</option></select>
				<b class="tooltip tooltip-top-right"></b><i></i></section>
				<section class="view"></section>
			</div>
			<div class="col-sm-2"><section><label for="cbdId_{{id}}" class="label">Bank Details</label></section></div>
			<div class="col-sm-4">
				<section class="select cbdId">
				<select id="cbdId_{{id}}"><option value="">Select Bank</option></select>
				<b class="tooltip tooltip-top-right"></b><i></i></section>
				<section class="view"></section>
			</div>
		</div>
	<hr></hr>
	{{/each}}
    </script>

	<div class="content" id="contCompanyDetail">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Registration</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="code" class="label">Entity Code:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="code" placeholder="Entity Code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="registrationNo" class="label">Registration No:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="registrationNo" placeholder="Registration No">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="approvalStatus" class="label">Approval Status:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="approvalStatus"><option value="">Select Approval Status</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="companyName" class="label">Name of applicant entity:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="companyName" placeholder="Company Name">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="type" class="label">Entity Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="type"><option value="">Select Entity Type</option></select>
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
			<div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
                 		 <li><a href="#tab0" data-toggle="tab"><label id="tabLabel">Inbox/Draft</label> <span id="badge0" class="badge bg-green"></span></a></li>
						 <li><a href="#tab1" data-toggle="tab">For Approval <span id="badge1" class="badge bg-red"></span></a></li>
						 <li><a href="#tab2" data-toggle="tab">Approved <span id="badge2" class="badge bg-red"></span></a></li>
						 <li><a href="#tab3" data-toggle="tab">Rejected <span id="badge3" class="badge bg-red"></span></a></li>
						 <li><a href="#tab4" data-toggle="tab">In Modification <span id="badge4" class="badge bg-red"></span></a></li>
						 <li><a href="#tab5" data-toggle="tab">Modification For Approval <span id="badge5" class="badge bg-red"></span></a></li>
						 <li><a href="#tab6" data-toggle="tab">Jocata Draft<span id="badge6" class="badge bg-red"></span></a></li>
						 <li><a href="#tab7" data-toggle="tab">Jocata Approved<span id="badge7" class="badge bg-red"></span></a></li>
					</ul>
					
				</div>	
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links comm" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure btn-group2" data-seckey="company-add" id=btnStartMod><span class="glyphicon glyphicon-edit"></span> Start Modification</a>
                	<a href="javascript:;" class="right_links secure btn-group0 btn-group1 btn-group4" data-seckey="company-add" id=btnEdit><span class="glyphicon glyphicon-edit"></span> Edit</a>
                	<a href="javascript:;" class="right_links secure btn-group0" data-seckey="company-add" id=btnAdd><span class="glyphicon glyphicon-edit"></span> New</a>					
					<a href="javascript:;" class="right_links secure" data-seckey="reguser-upload" id=btnRegUpload><span class="glyphicon glyphicon-upload"></span> Upload</a>					
					<a href="javascript:;" class="right_links secure btn-group1 btn-group5" data-seckey="company-approve" id=btnApprove><span class="glyphicon glyphicon-ok"></span> Approve</a>
					<a href="javascript:;" class="right_links secure btn-group1" data-seckey="company-reject" id=btnReject><span class="glyphicon glyphicon-trash"></span> Reject</a>
					<a href="javascript:;" class="right_links secure btn-group1" data-seckey="company-return" id=btnReturn><span class="glyphicon glyphicon-remove"></span> Return</a>
					<a href="javascript:;" class="left_links state-T comm" data-seckey="company-add" id=btnSettings style=''><span class="glyphicon glyphicon-edit"></span>Settings</a>
					<a href="javascript:;" class="left_links state-T btn-group0 btn-group1 btn-group2" data-seckey="company-add" id=btnCompanyNameChange style=''><span class="glyphicon glyphicon-edit"></span>Change Name</a>
					<a href="https://www.rxil.in/" class="right_links btn-group0"  id=btnLink>Supplier Registration</a>
					<a href="javascript:;" class="right_links secure comm" data-seckey="company-view" id=btnViewComp><span class="glyphicon glyphicon-eye-open"></span> View</a>
					<a href="javascript:;" class="right_links comm" id="btnDownloadPDF"><span class="fa fa-print"></span> Download PDF</a>
					<a href="javascript:;" class="right_links secure comm" data-seckey="company-view" id=btnViewKYCDocs><span class="glyphicon glyphicon-envelope"></span> Enclosures</a>
					<a href="javascript:;" class="right_links secure btn-group4" data-seckey="company-add" id=btnSubmitAppMod><span class="glyphicon glyphicon-eye-open"></span> Submit</a>
					<a href="javascript:;" class="right_links secure btn-group0 btn-group1" data-seckey="appentity-delete" id=btnDelete><span class="glyphicon glyphicon-remove"></span> Delete</a>
					<a></a>
				</div>
			</div>
		</div>
		<!-- frmSearch -->
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
					<table class="table table-bordered " id="tblData">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="code">Member Code</th>
								<th data-width="100px" data-name="loginId">Login Id</th>
								<th data-width="160px" data-name="companyName">Company Name</th>
								<th data-width="80px" data-name="constitution">Constitution</th>
								<th data-width="100px" data-name="type">Type</th>
								<th data-width="60px" data-name="approvalStatus">Approval Status</th>
								<th data-width="160px" data-name="registrationNo">Registration No</th>
								<th data-width="100px" data-name="creatorLoginId">Creator Login Id</th>
								<th data-visible="false" data-name="creatorIdentity">Creator Identity</th>
								<th data-visible="false" data-name="tab"></th>
								<th data-visible="false" data-name="isProvisional"></th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>		
		<!-- frmMain -->
    	<div class="xform box" id="frmMain">
    		<div class="box-body">
    			
    		</div>
    		<div class="modal-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
    		</div>
    	</div>
    	<!-- frmMain -->
    	<div class="modal fade" id=mdlSetting tabindex=-1><div class="modal-dialog modal-xl"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Registration Settings</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmSetting">
				<fieldset>
					<center><h3><a  href="javascript:;" onClick="$('#companyDetails').toggle();" >Details</a></h3></center>
					<hr></hr>
					<div id="companyDetails" style="display:none">
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="cashDiscountPercent" class="label">Cash Discount %:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="cashDiscountPercent" placeholder="Cash Discount %">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>	
						<div class="col-sm-2">
							<section>
							<label for="enableLocationwiseSettlement" class="label">Enable Locationwise Settlement:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox id="enableLocationwiseSettlement" onchange="toggleData()"><i></i>
								<b class="tooltip tooltip-top-left"></b>
								</label>
							</section>
						<section class="view"></section>
						</div>
					</div>
					<hr />
					</div>
					<center><h3><a  href="javascript:;" onClick="$('#contLocations').toggle();" >Locations</a></h3></center>
					<div id="contLocations" class="hidden"> </div>
					<input type="hidden" id="id" name="id" value="">
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveSetting><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
		<div class="modal fade" id="mdlChangeCompName" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
		<div class="modal-header"><span>&nbsp;Change Company Name</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body">
				<div class="xform box" id="frmCompanyDetail">
				<fieldset>
					<div class="row">
						<div class="col-sm-4"><section><label for="code" class="label">Company Code:</label></section></div>
						<div class="col-sm-8">
							<section class="input">
								<input type="text" id="code" name="code" readonly>
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-4"><section><label for="companyName" class="label">Company Name:</label></section></div>
						<div class="col-sm-8">
							<section class="input">
								<input type="text" id="companyName" name="companyName" placeholder="company Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" style="display: none">
						<div class="col-sm-4"><section><label for="id" class="label">Company Id</label></section></div>
						<div class="col-sm-8">
							<section class="input">
								<input type="text" id="id" name="code">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
				</fieldset>
				</div>
			</div>
			<div class="modal-footer">
				<footer>
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpdateCompName><span class="fa fa-save"></span> Save</button>
						<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
					</div>
				</footer>
		</div>
		</div></div></div>
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
		var crudCompanyDetail$ = null, crudCompanyDetail = null;
		var tabData = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyDetailBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "company",
					preSearchHandler: function(pFilter) {
						pFilter.isProvisional=true;
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
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudCompanyDetail$ = $('#contCompanyDetail').xcrudwrapper(lConfig);
			crudCompanyDetail = crudCompanyDetail$.data('xcrudwrapper');
			var lType = crudCompanyDetail.options.searchForm.getField('type');
			lType.options.dataSetValues = lType.options.dataSetValues.filter(pData => pData.value != 'NNN');
			lType.options.dataSetValues = lType.options.dataSetValues.filter(pData => pData.value != 'AAA');
			lType.init();
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
			$('.right_links').addClass('hidden');
			$('.left_links').addClass('hidden');
			$('.comm').removeClass('hidden');
			$('.btn-group'+0).removeClass('hidden');
			$('#btnApprove').on('click', function() {
				updateStatus('A');
			});
			$('#btnReject').on('click', function() {
				updateStatus('R');
			});
			$('#btnReturn').on('click', function() {
				updateStatus('B');
			});
			$('#btnViewComp').on('click', function() {
				var lSelected = crudCompanyDetail.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				window.open('printhtml/'+crudCompanyDetail.selectedRowKey(lSelected.data())+'/'+lSelected.data().isProvisional+'?loginKey='+loginData.loginKey);
			});
			$('#btnDownloadPDF').on('click', function() {
				var lSelected = crudCompanyDetail.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				window.open('printpdf/'+crudCompanyDetail.selectedRowKey(lSelected.data())+'/'+lSelected.data().isProvisional+'?loginKey='+loginData.loginKey);
			});
			$('#btnViewKYCDocs').on('click', function() {
				var lSelected = crudCompanyDetail.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				showRemote('<%=request.getContextPath()%>/rest/companyview?id='+crudCompanyDetail.selectedRowKey(lSelected.data())+'&viewKYC=true', 'modal-xl', true);
			});
			$('#btnEdit').on('click', function() {
				var lSelected = crudCompanyDetail.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				if(lSelected.data().creatorIdentity == 'J' && lSelected.data().approvalStatus == null){
					alert(" Record cannot be edited ");
					return true;
				}
// 				alert(JSON.stringify(lSelected.data()));
				window.open('<%=request.getContextPath()%>/rest/reghome?entityId='+crudCompanyDetail.selectedRowKey(lSelected.data())+'&isProv='+lSelected.data().isProvisional);
			});
			$('#btnAdd').on('click', function() {
				window.open('<%=request.getContextPath()%>/rest/reguser');
			});
			$('#btnRegUpload').on('click', function() {
				showRemote('upload?url=reguser', null, false);
			});
			$('#btnSettings').on('click', function() {
	            var lSelected = crudCompanyDetail.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            if (lSelected.data().creatorIdentity=='J' && lSelected.data().approvalStatus!= null){
	            	 $.ajax({
	     		        url: 'registrationsetting/'+lSelected.data().id,
	     		        type: 'GET',
	     		        success: function( pObj, pStatus, pXhr) { 
	     		        	populateCompanyDetails(pObj);
	     		        },
	     		        error: function( pObj, pStatus, pXhr) {
	     		        	
	     		        }
	     		    });
	            	//$('#mdlSetting').modal('show');
	            }else{
	            	alert("Cannot be changed");
                    return;
	            }
				
			});
			$('#btnSaveSetting').on('click', function() {
				$.ajax({
			        url: "saveregistrationsetting",
			        data: JSON.stringify(crudSetting.getValue()),
			        type: 'POST',
			        success: function( pObj, pStatus, pXhr) {
			        	
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	oldAlert("Some error occured");
			        }
			    });
			});
			$('#btnCompanyNameChange').on('click', function() {
	            var lSelected = crudCompanyDetail.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            if(lSelected.length > 0){
	    			var lCompanyDetailConfig ={
	    					"fields":[
  	    						{
	    							"name": "id",
	    							"label": "Company Id",
	    							"nonDatabase":true,
	    							"dataType": "INTEGER"
	    						},
	    						{
	    							"name": "code",
	    							"label": "Company Code",
	    							"nonDatabase":true,
	    							"dataType": "STRING",
	    							"maxValue":10,
	    							"minValue":0
	    						},
	    						{
	    							"name": "companyName",
	    							"label": "Name of Applicant Entity",
	    							"dataType": "STRING",
	    							"minLength": 3,
	    							"maxLength": 100,
	    							"notNull": true,
	    							"pattern":"^([a-zA-Z])|(\\w{1}.*[a-zA-Z\\.])",
	    							"patternMessage":"Name should start with character or number and should have atleast one character",
	    							"wildCard": "CONTAINS"
	    						}
	    					]
	    			}
	    			crudfrmCompanyInfo$ = $('#frmCompanyDetail').xform(lCompanyDetailConfig);
	    			crudfrmCompanyInfo = crudfrmCompanyInfo$.data('xform');
					crudfrmCompanyInfo.setValue(lSelected.data());
		        	$('#mdlChangeCompName').modal('show');	
	            }
			});
			$('#btnUpdateCompName').on('click', function() {
				var lErrors = crudfrmCompanyInfo.check();
				if ((lErrors != null) && (lErrors.length > 0)) {
					alert(lErrors);
					return;
				}
				lData = {};
				lData = crudfrmCompanyInfo.getValue();
				$.ajax( {
		            url: "updatecompanyname",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Company name updated successfully");
			        	$('#mdlChangeCompName').modal('hide');	
	            		crudCompanyDetail.searchHandler();
		            },
		        	error: errorHandler,
		        });	
			});
			
			$('#btnStartMod').on('click', function() {
	            var lSelected = crudCompanyDetail.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            var lData = {id:crudCompanyDetail.selectedRowKey(lSelected.data())};
	            $.ajax( {
		            url: "company/startmod",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudCompanyDetail.searchHandler();
	            		});
		            },
		        	error: errorHandler,
		        });	
	            
			});
			
			$('#btnSubmitAppMod').on('click', function(){
	            var lSelected = crudCompanyDetail.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            var lData = {id:crudCompanyDetail.selectedRowKey(lSelected.data())};
	            $.ajax( {
		            url: "company/changeappmod",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudCompanyDetail.searchForm();
	            		});
		            },
		        	error: errorHandler,
		        });	
			});
			
			$('#btnDelete').on('click', function(){
	            var lSelected = crudCompanyDetail.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            $.ajax( {
		            url: "appentity/removecompany/"+lSelected.data().code,
		            type: "DELETE",
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudCompanyDetail.searchForm();
	            		});
		            },
		        	error: errorHandler,
		        });	
			});
		});
		function updateStatus(pStatus) {
			var lSelected = crudCompanyDetail.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			prompt("Please enter suitable reason/remarks","Reason",function(pReason){
				var lData = {cdId:crudCompanyDetail.selectedRowKey(lSelected.data()),approvalStatus: pStatus,reason : pReason};
				$.ajax( {
		            url: "company/status/"+pStatus.toLowerCase(),
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
	            		alert("Status updated successfully", "Information", function() {
	            			crudCompanyDetail.showSearchForm();
	            		});
		            },
		        	error: errorHandler,
		        });					
			});
		}
		
		function populateCompanyDetails(pData){
			crudSetting = null;
			crudSetting$ = null;
			var lLocConfig = [
				{
					"name": "id",
					"dataType": "INTEGER"
				},
				{
					"name": "name",
					"label": "Location Name",
					"dataType": "STRING",
					"maxLength": 50
				},
				{
					"name": "enableSettlement",
					"label": "Enable Settlement",
					"dataType":"STRING",
					"maxLength": 1,
					"dataSetType":"ENUM",
					"dataSetValues":[{
	                    "text": "Yes",
	                    "value": "Y"
	                }]
				},
				{
					"name": "cbdId",
					"label":"Company Bank",
					"dataType": "INTEGER",
					"dataSetType":"RESOURCE",
					"dataSetValues": ""
				},
				{
					"name": "settlementCLId",
					"label":"Settlement Location",
					"dataType": "INTEGER",
					"dataSetType":"RESOURCE",
					"dataSetValues":""		
				}
			];
			
			var lLocationsConfig = [];
			for(var lPtr=0; lPtr<pData.locations.length; lPtr++) {
				var lLocData = pData.locations[lPtr];
				for (var lCPtr=0; lCPtr<lLocConfig.length; lCPtr++) {
					var lConfig = {};
					lConfig = $.extend(true, lConfig, lLocConfig[lCPtr]);
					lConfig.name = lConfig.name +'_'+ lLocData.id;
					lLocationsConfig.push(lConfig);
				}
			}
			
			var lSettingConfig ={
					"resource": "location",
					"fields":[
						{
							"name": "cashDiscountPercent",
							"label": "Cash Discount %",
							"dataType": "DECIMAL",
							"integerLength": 3,
							"decimalLength":2,
							"minValue": 0,
							"maxValue": 100
						},
						{
							"name": "enableLocationwiseSettlement",
							"label": "Enable Locationwise Settlement",
							"dataType":"STRING",
							"maxLength": 1,
							"dataSetType":"ENUM",
							"dataSetValues":[{
			                    "text": "Yes",
			                    "value": "Y"
			                }]
						},
						{
							"name": "id",
							"dataType": "INTEGER"
						}
					]
			}		
			$('#mdlSetting').modal('show');
			$('#frmSetting').data('xform', null);
			var tplLocations = Handlebars.compile($('#tplLocations').html());
			$('#contLocations').html(tplLocations(pData.locations));
			var lSettingConfigConf = $.extend({},lSettingConfig);
			lSettingConfigConf.fields.push(...lLocationsConfig);
			crudSetting$ = $('#frmSetting').xform(lSettingConfigConf);
			console.log(lSettingConfigConf);
			crudSetting = crudSetting$.data('xform');
 			console.log('crudSetting', crudSetting);
			crudSetting.setValue(pData);
			var lLocations = pData.locations;
			for(var lPtr=0; lPtr<lLocations.length; lPtr++) {
				var lLocData = lLocations[lPtr];
				populateSettlementLocations(lLocData.id,pData.id,lLocData.settlementCLId, lLocData.cbdId);
				crudSetting.getField("enableSettlement"+'_'+lLocData.id).setValue(lLocData.enableSettlement);
				toggleSettlement(lLocData.id);
			}			
		}
		
		var crudSetting$, crudSetting;
		
		function toggleSettlement(pId){
			crudSetting.enableDisableField('cbdId'+'_' + pId,true,false);
			crudSetting.enableDisableField('settlementCLId'+'_' + pId,true,false);
			var toggle = crudSetting.getField('enableSettlement'+'_' + pId).getValue();
			var temp=false,temp1=true;
			if(toggle == "Y"){
				 temp=true;
				 temp1=false;
				 var tempClr=crudSetting.getField("settlementCLId" +'_'+ pId);
					tempClr.setValue("");
			}
			else{
				var tempClr=crudSetting.getField("cbdId"+'_' + pId);
				tempClr.setValue("");
			}
			crudSetting.enableDisableField('cbdId' +'_'+ pId,temp,false);
			crudSetting.enableDisableField('settlementCLId'+'_' + pId,temp1,false);
			crudSetting.alterField('cbdId'+'_' + pId,temp,false);
			crudSetting.alterField('settlementCLId'+'_' + pId,temp1,false);
		}
		
		function toggleData(){
			var toggle = crudSetting.getField('enableLocationwiseSettlement').getValue();
			if(toggle == "Y"){
				$('#contLocations').removeClass('hidden');
			}else{
				$('#contLocations').addClass('hidden');
			}

		}
		
		function populateSettlementLocations(pClid,pCdId,pSettlementCLId, pCbdId) {
			var lEntity = pCdId;
			var lField=crudSetting.getField("settlementCLId"+'_'+pClid);
			var lOptions=lField.getOptions();
			lOptions.dataSetValues = "companylocation/settlelov?cdId="+lEntity+"&clId="+pClid;
			lField.init();
			if (pSettlementCLId != null)
				lField.setValue(pSettlementCLId);
			//
			lField=crudSetting.getField("cbdId"+'_'+pClid);
			var lOptions=lField.getOptions();
			lOptions.dataSetValues = "companybankdetail/lov?cdId="+lEntity;
			lField.init();
			if (pCbdId != null)
				lField.setValue(pCbdId);
		}
		
		function showData() {
			crudCompanyDetail.options.dataTable.rows().clear();
			if (tabData && (tabData[tabIdx] != null)){
				crudCompanyDetail.options.dataTable.rows.add(tabData[tabIdx]).draw();
			}
		}
		
		
	</script>
   	
    </body>
</html>