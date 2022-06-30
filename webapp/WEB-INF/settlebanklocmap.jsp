
<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.SettleBankLocationMapBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Settlement Bank Location Map</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Settlement Bank Location Map" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contSettleBankLocationMap" >	
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Settlement Bank Location Map</h1>
			</div>
		</div>	
		<div id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
			<!-- <header>null</header> -->
			

			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="cdId" class="label">Company Detail Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="cdId" placeholder="Company Detail Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="clId" class="label">Company Location Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="clId" placeholder="Company Location Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="enableSetLoc" class="label">Enable Settlement Location:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="enableSetLoc"><option value="">Select Enable Settlement Location</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="settleClId" class="label">Settle Company Location Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="settleClId" placeholder="Settle Company Location Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="l1DCbdid" class="label">L1 Debit CBD Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="l1DCbdid" placeholder="L1 Debit CBD Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="l2DCbdid" class="label">L2 Debit CBD Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="l2DCbdid" placeholder="L2 Debit CBD Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="l1CCbdid" class="label">L1 Credit CBD Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="l1CCbdid" placeholder="L1 Credit CBD Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="l2CCbdid" class="label">L2 Credit CBD Id:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="l2CCbdid" placeholder="L2 Credit CBD Id">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
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
			</div>
			
			
			<div class="filter-block clearfix">
				<div class="">
				<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter"> Filter </a>
				<span class="right_links">
					<a href="javascript:;" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> New </a>
					<a href="javascript:;" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify </a>
					<a href="javascript:;" id=btnView><span class="glyphicon glyphicon-eye-open"></span> View </a></span>
				</span>
			</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered table-condensed" id="tblData">
								<thead><tr>
									<th data-width="100px" data-name="cdId">Company Detail Id</th>
									<th data-width="100px" data-name="clId">Company Location Id</th>
									<th data-width="100px" data-name="enableSetLoc">Enable Settlement Location</th>
									<th data-width="100px" data-name="settleClId">Settle Company Location Id</th>
									<th data-width="100px" data-name="l1DCbdid">L1 Debit CBD Id</th>
									<th data-width="100px" data-name="l2DCbdid">L2 Debit CBD Id</th>
									<th data-width="100px" data-name="l1CCbdid">L1 Credit CBD Id</th>
									<th data-width="100px" data-name="l2CCbdid">L2 Credit CBD Id</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>null</header> -->
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="cdId" class="label">Company Detail Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="cdId" onchange="getSettlementDetails()"><option value="">Select Company</option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="clId" class="label">Company Location Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="clId"><option value="">Select Company Location</option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="enableSetLoc" class="label">Enable Settlement Location:</label></section></div>
					<div class="col-sm-4">
						<section class="inline-group">
						<label class="checkbox"><input type=checkbox id="enableSetLoc"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="settleClId" class="label">Settle Company Location Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="settleClId"><option value="">Select Settlement Location Id</options></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="l1DCbdid" class="label">L1 Debit CBD Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="l1DCbdid"><option value="">Select L1 Debit CBD Id</option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="l2DCbdid" class="label">L2 Debit CBD Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="l2DCbdid"><option value="">Select L2 Debit CBD Id></option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="l1CCbdid" class="label">L1 Credit CBD Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="l1CCbdid"><option value="">Select L1 Credit CBD Id</option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="l2CCbdid" class="label">L2 Credit CBD Id:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="l2CCbdid"><option value="">Select L2 Credit CBD Id</option></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row" hidden>
					<div class="col-sm-2"><section><label for="isLocationEnable" class="label">Is Location Enable:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="isEnable" class="label">Is Location Enable:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<footer>
					<div class="btn-group pull-right">
						<button type="button" class="btn btn-primary" id=btnSave><span class="fa fa-save"></span> Save</button>						
						<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
					</div>
				</footer>
			</fieldset>
		</div>
	</div>
</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>

	<script type="text/javascript">
		var crudSettleBankLocationMap$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(SettleBankLocationMapBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "settlebanklocmap",
					autoRefresh: true,
					postNewHandler:function(){
// 						getCompanydetails();
						populateCompanyName(null);
			    		return true;
					},
					postModifyHandler:function(pObj){
// 						var lCdId = mainForm.getField('cdId');
// 						var lOptions=lCdId.getOptions();
// 						lOptions.dataSetValues = [];
// 						lOptions.dataSetValues = "settlebanklocmap/companydetails";
// 						lCdId.init();
// 						if(lCdId != null)
// 							lCdId.setValue(pObj.cdId);
						populateCompanyName(pObj.cdId)
						var lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid'];
						var lFieldValue = [pObj.l1DCbdid,pObj.l2DCbdid,pObj.l1CCbdid,pObj.l2CCbdid];
						populateBankDetails(lFields,pObj.cdId,lFieldValue);
						if(pObj.isLocationEnable == 'false' && pObj.isEnable == 'false'){
							var lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid'];
							mainForm.alterField(lFields,true,false);
							mainForm.enableDisableField(lFields,true,false);
							lFields = ['enableSetLoc','settleClId'];
							mainForm.enableDisableField(lFields,false,false);
							mainForm.alterField(lFields,false,false);
						}else if(pObj.isLocationEnable == 'true' && pObj.isEnable == 'true'){
							var lFields = ['settleClId'];
							mainForm.enableDisableField(lFields,false,true);
							lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid','clId'];
							mainForm.alterField(lFields,true,false);
							mainForm.enableDisableField(lFields,true,false);
						}else if(pObj.isLocationEnable == 'true' && pObj.isEnable == 'true'){
							var lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid'];
							mainForm.enableDisableField(lFields,false,true);
							mainForm.alterField(lFields,false,false);
							lFields = ['settleClId'];
							mainForm.enableDisableField(lFields,true,true);
							mainForm.alterField(lFields,true,false);
							lFields = ['clId'];
							mainForm.alterField(lFields,true,false);
						}
						return true;
					},
<%
if (lNewBool) {
%>					new: true,
<%
} else if (lModify != null) {
%>					modify: <%=lModify%>,
<%
}
%>			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudSettleBankLocationMap$ = $('#contSettleBankLocationMap').xcrudwrapper(lConfig);
			crudSettleBankLocationMap=crudSettleBankLocationMap$.data('xcrudwrapper');
			mainForm=crudSettleBankLocationMap.options.mainForm;
		});
		
		
	function getCompanydetails(){
		$.ajax({
			url:"settlebanklocmap/companydetails",
	        type: "GET",
	        success: function( pObj, pStatus, pXhr){
	    		var lCdId = mainForm.getField('cdId');
				var lOptions=lCdId.getOptions();
				lOptions.dataSetValues = [];
				lOptions.dataSetValues = pObj;
				lCdId.init();
	        },
	    	error: errorHandler
			
		});
	}

	
	function getSettlementDetails(){
		var lData = {};
		var lCdId = mainForm.getField('cdId').getValue();
		lData['cdId'] = mainForm.getField('cdId').getValue();
		$.ajax({
			url : "settlebanklocmap/companysettlementdetail",
			type : "POST",
			data : JSON.stringify(lData),
			success : function(pObj, pStatus, pXhr){
				if(pObj.companyDetailFlag == 'false' && pObj.enableSetLoc == '<%=CommonAppConstants.YesNo.No%>'){
					mainForm.getField('clId').setValue(null);
					var lFields = ['clId'];
					mainForm.enableDisableField(['clId'],true,true);
					mainForm.alterField(lFields,false,false);
					lFields = ['enableSetLoc','settleClId'];
					mainForm.enableDisableField(lFields,false,false);
					mainForm.alterField(lFields,false,false);
					lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid'];
					populateBankDetails(lFields,lCdId, null);
					mainForm.alterField(lFields,true,false);
					mainForm.enableDisableField(lFields,true,false);
					mainForm.getField('isLocationEnable').setValue('<%=Boolean.FALSE%>');
					return true;
				}else if(pObj.companyDetailFlag == 'true'){
					if(pObj.enableSetLoc == '<%=CommonAppConstants.Yes.Yes%>'){
						mainForm.getField('enableSetLoc').setValue('<%=CommonAppConstants.Yes.Yes.getCode()%>');
						var lDisableFields = ['settleClId'];
						mainForm.enableDisableField(lDisableFields,false,true);
						var lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid','clId'];
						mainForm.alterField(lFields,true,false);
						mainForm.enableDisableField(lFields,true,false);
						mainForm.getField('isLocationEnable').setValue('<%=Boolean.TRUE%>');
						mainForm.getField('isEnable').setValue('<%=Boolean.TRUE%>');
						return true;
					}else if(pObj.enableSetLoc == '<%=CommonAppConstants.YesNo.No%>'){
						mainForm.getField('enableSetLoc').setValue(null);
						var lFields = ['l1DCbdid','l2DCbdid','l1CCbdid','l2CCbdid'];
						mainForm.enableDisableField(lFields,false,true);
						mainForm.alterField(lFields,false,false);
						lFields = ['settleClId'];
						mainForm.enableDisableField(lFields,true,true);
						mainForm.alterField(lFields,true,false);
						populateSettlementLocation(lFields,lCdId);
						lFields = ['clId'];
						mainForm.alterField(lFields,true,false);
						populateLocation(lFields,lCdId)
						mainForm.getField('isLocationEnable').setValue('<%=Boolean.TRUE%>');
						mainForm.getField('isEnable').setValue('<%=Boolean.FALSE%>');
						return true;
					}
				}
			},
			error: errorHandler
		});
	}
	
	function populateCompanyName(pCdId){
		var lCdId = mainForm.getField('cdId');
		var lOptions=lCdId.getOptions();
		lOptions.dataSetValues = [];
		lOptions.dataSetValues = "settlebanklocmap/companydetails";
		lCdId.init();
		if(pCdId != null)
			lCdId.setValue(pCdId);
	}
	
	function populateBankDetails(pFields,pCdId,pFieldValue){
		var lFieldCount = 0;
		for(var lPtr=0; lPtr<pFields.length; lPtr++){
			var lTmpField = mainForm.getField(pFields[lPtr]);
			var lOptions=lTmpField.getOptions();
			if (!pCdId) lOptions.dataSetValues=[];
    		lOptions.dataSetValues = "companybankdetail/lov?cdId="+pCdId+"&isProv=false";
    		lTmpField.init();
    		if (pFieldValue != null){
    			lTmpField.setValue(pFieldValue[lFieldCount]);
    			lFieldCount++;
    		}
		}
	}
	
	function populateSettlementLocation(pFields,pCdId){
		for(var lPtr=0; lPtr<pFields.length; lPtr++){
			var lTmpField = mainForm.getField(pFields[lPtr]);
			var lOptions=lTmpField.getOptions();
			lOptions.dataSetValues = [];
			lOptions.dataSetValues = "companylocation/settlelov?cdId="+pCdId+"&isProv=false";
			lTmpField.init();	
		}
	}
	
	function populateLocation(pFields,pCdId){
		for(var lPtr=0; lPtr<pFields.length; lPtr++){
			var lTmpField = mainForm.getField(pFields[lPtr]);
			var lOptions=lTmpField.getOptions();
			lOptions.dataSetValues = [];
			lOptions.dataSetValues = "settlebanklocmap/companylocation/"+pCdId+"/"+"&isProvisional=false";
			lTmpField.init();	
		}
	}
		
	</script>


</body>
</html>
