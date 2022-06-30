<!DOCTYPE html>
<%@page import="com.xlx.treds.auction.bean.FacilitatorEntityMappingBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Entity NACH Codes</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Entity NACH Codes" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contFacilitatorEntityMapping">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Entity NACH Codes</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<!-- <div class="col-sm-2"><section><label for="facilitator" class="label">Facilitator:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="facilitator"><option value="">Select Facilitator</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div> -->
						<div class="col-sm-2"><section><label for="entityCode" class="label">Entity Code:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="entityCode"><option value="">Select Entity</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="mappingCode" class="label">Mapping Code:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="mappingCode" placeholder="Mapping Code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="active" class="label">Active:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="active"><option value="">Select Active</option></select>
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
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="facentmap-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="facentmap-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-width="80px" data-name="facilitator">Facilitator</th>
									<th data-width="80px" data-name="entityCode">Entity Code</th>
									<th data-width="100px" data-name="mappingCode">Mapping Code</th>
									<th data-width="0px" data-name="cbdId"  data-visible="false" >CBD Id</th>
									<th data-width="100px" data-name="designatedBankName">Designated Bank</th>
									<th data-width="80px" data-name="ifsc">IFSC</th>
									<th data-width="120px" data-name="accNo">Account No</th>
									<th data-width="80px" data-name="mandateAmount">Mandate Amount</th>
									<th data-width="50px" data-name="haircut">Haircut %</th>
									<th data-width="50px" data-name="active">Active</th>
									<th data-width="50px" data-name="expiry">Expiry Date</th> 
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content xform" id="frmMain">
		<div class="modal-header">Entity NACH Codes</div>
   		<div class="modal-body">
   			<fieldset>
			<div class="row">
				<div class="col-sm-4"><section><label for="facilitator" class="label">Facilitator:</label></section></div>
				<div class="col-sm-8">
					<section class="select">
					<select id="facilitator"><option value="">Select Facilitator</option></select>
					<b class="tooltip tooltip-top-right"></b><i></i></section>
					<section class="view"></section>
				</div>
				<div class="col-sm-4"><section><label for="entityCode" class="label">Entity Code:</label></section></div>
				<div class="col-sm-8">
					<section class="select">
					<select id="entityCode"><option value="">Select Entity</option></select>
					<b class="tooltip tooltip-top-right"></b><i></i></section>
					<section class="view"></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-4"><section><label for="cbdId" class="label">Designated Bank:</label></section></div>
				<div class="col-sm-8">
					<section class="select">
					<select id="cbdId"><option value="">Select Entity</option></select>
					<b class="tooltip tooltip-top-right"></b><i></i></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-4"><section><label for="locationList" class="label">Mapped Locations:</label></section></div>
				<div class="col-sm-8" >
				<section class="select" >
				<select multiple="" id="locationList" style="height:60px;"></select>
				<b class="tooltip tooltip-top-right"></b></section>
				<section class="view" ></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-4"><section><label for="mandateAmount" class="label">Mandate Amount:</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<input type="text" id="mandateAmount" placeholder="Mandate Amount">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
				<div class="col-sm-4"><section><label for="haircut" class="label">Haircut %:</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<input type="text" id="haircut" placeholder="Haircut %">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-4"><section><label for="mappingCode" class="label">Mapping Code:</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<input type="text" id="mappingCode" placeholder="Mapping Code">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
				<div class="col-sm-4"><section><label for="active" class="label">Active:</label></section></div>
				<div class="col-sm-8">
					<section class="select">
					<select id="active"><option value="">Select Active</option></select>
					<b class="tooltip tooltip-top-right"></b><i></i></section>
					<section class="view"></section>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-4"><section><label for="expiry" class="label">Expiry Date:</label></section></div>
				<div class="col-sm-8">
					<section class="input">
					<i class="icon-append fa fa-clock-o"></i>
					<input type="text" id="expiry" placeholder="Expiry Date" data-role="datetimepicker">
					<b class="tooltip tooltip-top-right"></b></section>
					<section class="view"></section>
				</div>
			</div>
	   		<div class="modal-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
	   		</div>
   			</fieldset>
   		</div>
		</div></div></div>
    	<!-- frmMain -->
	</div>
   	<%@include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/bootstrap-multiselect.js"></script>

	<script type="text/javascript">
		var crudFacilitatorEntityMapping$ = null,mainForm,crudFacilitatorEntityMapping;
		var prevCbdId=null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(FacilitatorEntityMappingBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "facentmap",
					keyFields:["facilitator","entityCode","cbdId"],
					autoRefresh: true,
					postModifyHandler: function(pObj) {
						populateBanks(mainForm);
						if(pObj){
							mainForm.getField("cbdId").setValue(pObj["cbdId"]);
							prevCbdId = pObj["cbdId"];
							getEntityType(mainForm.getField("entityCode").getValue());
						}
						populateLocations(mainForm) ;
						return true;
					},
					preSaveHandler: function(pData) {
						return true;
					},
					postSaveHandler: function(pData) {
						var message = "Saved successfully";
						if(pData){
							if(pData.Message!=null)	message=pData.Message;
						}
						alert(message,function() {
							if (!crudFacilitatorEntityMapping.options.searchForm || (crudFacilitatorEntityMapping.options.bulkEntry && crudFacilitatorEntityMapping.options.mainForm.method == "POST")) crudFacilitatorEntityMapping.newHandler();
			 				else crudFacilitatorEntityMapping.showSearchForm();
						});
						return false;
					},
					postNewHandler: function() {
						prevCbdId=null;
						$('#frmMain #mandateAmount').prop('disabled',false);
						$('#frmMain #haircut').prop('disabled',false);
						$('#frmMain #mappingCode').prop('disabled',false);
						$('#frmMain #locationList').prop('disabled',false);
						populateLocations(mainForm);
						return true;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudFacilitatorEntityMapping$ = $('#contFacilitatorEntityMapping').xcrudwrapper(lConfig);
			crudFacilitatorEntityMapping = crudFacilitatorEntityMapping$.data('xcrudwrapper');
			mainForm = crudFacilitatorEntityMapping$.data('xcrudwrapper').options.mainForm;
			
			$('#frmMain #entityCode').on('change', function() {
				populateBanks(mainForm);
			});
			$('#frmMain #cbdId').on('change', function() {
				populateLocations(mainForm) ;
				if(mainForm.mode=="update"){
					if(prevCbdId!=null && prevCbdId.toString()!=mainForm.getField("cbdId").getValue()){
						confirm('You are about to change the Designated Bank Account. Are you sure?','Confirmation','Yes',function(pYes) {
							if (!pYes) {
								mainForm.getField("cbdId").setValue(prevCbdId);
							}
						});
					}
				}
			});
		});
		
		function populateBanks(pForm) {
			$('#locationList').empty();
			var lEntity=pForm.getField("entityCode").getValue();
			var lField=pForm.getField("cbdId");
			var lOptions=lField.getOptions();
			if (!lEntity) lOptions.dataSetValues=[];
			else lOptions.dataSetValues = "companybankdetail/settlenach/"+lEntity;
			lField.init();
		}
		
		function populateLocations(pForm){
			$('#locationList').empty();
			var	lActiveOnly=false; 
			var lEntity=pForm.getField("entityCode").getValue();
			var lCbdId=pForm.getField("cbdId").getValue();
			var lField=pForm.getField("locationList");
			var lOptions=lField.getOptions();
			if (lEntity==null || lCbdId==null) {
				lOptions.dataSetValues=[];
			}else {
				lOptions.dataSetValues ="companylocation/settleactivelov?aecode="+lEntity+"&cbdId="+lCbdId+"&activeOnly="+lActiveOnly;
			}
			lField.init();
		}

		function getEntityType(entityCode){
			var lData=[];
			$.ajax( {
		        url: "appentity/"+entityCode,
		        type: "GET",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
		        	var lDisable = false;
					if(pObj){
						lDisable = (pObj.type=='YNN');
					}
					$('#frmMain #mandateAmount').prop('disabled',lDisable);
					$('#frmMain #haircut').prop('disabled',lDisable);
					$('#frmMain #mappingCode').prop('disabled',lDisable);
					if(lDisable){
						mainForm.getField("mandateAmount").setValue(0);
						mainForm.getField("haircut").setValue(0);
						mainForm.getField("mappingCode").setValue('XXXXXX');
					}
		        },
		        error: errorHandler,
				complete: function() {
				}
			});
		}
		
	</script>
   	
    </body>
</html>