<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.auction.bean.ObligationBean.Type"%>
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.ObligationBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
String lFuId = request.getParameter("fuId");
String lType = request.getParameter("type");
%>
<html>
	<head>
		<title>Obligations Modification</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Obligations  Modification" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contObligation">	
		<div class="page-title">
				<div class="title-env">
					<h1 class="title">Obligation Modification Request</h1>
				</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">	
		<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
			<fieldset class="form-horizontal">
			<div class="panel-body bg_white">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-default" id=btnSettlor><span class="fa fa-bank"></span> Settlor</button>
							<button type="button" class="btn btn-default" id=btnDate><span class="fa fa-calendar"></span> Date</button>
							<button type="button" class="btn btn-default" id=btnRemarks><span class="fa "></span> Remarks</button>
							<button type="button" class="btn btn-default" id=btnSaveObli><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-default" id=btnBack><span class="fa fa-close"></span> Back</button>
						</div>
						</div>
					</div>
			</div>
			</fieldset>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " id="tblData" >
							<thead><tr>
								<th data-width="100px" data-name="id">Id</th>
								<th data-width="100px" data-name="fuId">Factoring Unit</th>
								<th data-width="80px" data-name="txnEntity">Transacting Entity</th>
								<th data-width="40px" data-name="txnType">Transaction Type</th>
								<th data-width="60px" data-name="date">Date</th>
								<th data-width="110px" data-name="amount">Amount</th>
								<th data-width="45px" data-name="paymentSettlor">Settlor</th>
								<th data-width="40px" data-name="type">Type</th>
								<th data-width="55px" data-name="status">Status</th>
								<th data-width="55px" data-name="respRemarks">Remarks</th>
							</tr></thead>
						</table>
					</div>
				</div>
				</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>Obligations : Complete list of obligations generated for the factored bid. It also captures the current state of the obligation as well as the settelment details of the particular obligation legwise.</header> -->
			<footer>
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-primary" id=btnSave><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
					<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
			<div class="modal fade" id=mdlChangeSettlor tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Change Settlor Type</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
			<div class="xform box" id="obliSettlor-frmMain">
				<fieldset>
					<div class="row">
						<div class="col-sm-4"><section><label for="settlor" class="label">Settlor Type</label></section></div>
						<div class="col-sm-6">
							<section class="select">
							<select id="settlor"><option value=""></option>
							</select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>		
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveSettlor><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
	    		</div>
			</div>
		</div></div></div>
						
		<div class="modal fade" id=mdlChangeDate tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
		<div class="modal-header"><span>Obligation Modification Details</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
		<div id="obliDate-frmMain" class="xform box">
			<!-- <header>Obligations : Complete list of obligations generated for the factored bid. It also captures the current state of the obligation as well as the settelment details of the particular obligation legwise.</header> -->
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
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info btn-lg btn-enter" id="btnSaveDate"><span class="fa fa-save"></span> Save</button>
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
					</div>
				</fieldset>
			</div>
		</div>
		</div></div></div>
		
		<div class="modal fade" id=mdlAddRemarks tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
		<div class="modal-header"><span>Obligation Modification Details</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
		<div id="obliRemarks-frmMain" class="xform box">
			<!-- <header>Obligations : Complete list of obligations generated for the factored bid. It also captures the current state of the obligation as well as the settelment details of the particular obligation legwise.</header> -->
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="remarks" class="label">Remarks:</label></section></div>
						<div class="col-sm-10">
							<section class="input">
							<input type="text" id="remarks" placeholder="Remarks">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info btn-lg btn-enter" id="btnSaveRemarks"><span class="fa fa-save"></span> Save</button>
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
					</div>
				</fieldset>
			</div>
		</div>
		</div></div></div>
	</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudObligation$ = null;
		var TableData;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "modifyOblig",
					autoRefresh: true,
					preSearchHandler: function(pFilter) {
						pFilter.fuId = <%=lFuId%>;
						pFilter.type = new String('<%=lType%>');
						return true;
					},
					postSearchHandler: function(pObj){
						TableData = pObj;
						return true;
					},
					postModifyHandler: function(pObj) {
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
			crudObligation$ = $('#contObligation').xcrudwrapper(lConfig);
			crudObligation = crudObligation$.data('xcrudwrapper');
			$('#btnSettlor').on('click', function(){
				var lSelected = crudObligation.getSelectedRow();
		            if ((lSelected==null)||(lSelected.length==0)) {
		                    alert("Please select a row");
		                    return;
		        }
	            if (lSelected.data().id==null || lSelected.data().id==''){
					alert('Selected row cannot be modified.');
					return;
				}
	            populateSettlor();
				$('#mdlChangeSettlor').modal('show');
			});
			$('#btnSaveSettlor').on('click', function(){
				var pSettlor = "";
				pSettlor = $('#settlor').val();
				var lTableData = formatDataForXhr(TableData,true);
				var lSelected = crudObligation.getSelectedRow();
				var lData = {id:lSelected.data().id,txnEntity:lSelected.data().txnEntity,txnType:lSelected.data().txnType,table: lTableData,settlor:pSettlor};
				$.ajax( {
		            url: "modifyOblig/changeSettlor",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
		            	$('#mdlChangeSettlor').modal('hide');
		            	var lData = formatDataForXhr(pObj,false);
		            	crudObligation.options.dataTable.rows().clear();
		             	crudObligation.options.dataTable.rows.add(lData).draw();
		             	TableData = lData;
		            },
		        	error: errorHandler,
		        	complete: function() {
		        	}
		        });
			});
			$('#btnDate').on('click', function(){
				var lSelected = crudObligation.getSelectedRow();
		            if ((lSelected==null)||(lSelected.length==0)) {
		                    alert("Please select a row");
		                    return;
		        }
	            if (lSelected.data().id==null || lSelected.data().id==''){
					alert('Selected row cannot be modified.');
					return;
				}
				$('#mdlChangeDate').modal('show');
			});
			$('#btnSaveDate').on('click', function(){
				var lSelected = crudObligation.getSelectedRow();
				var lDate = modDateForm.getField('date').getValue();
				var lTableData = formatDataForXhr(TableData,true);
				var lData = {id:lSelected.data().id,table: lTableData,date:lDate};
				$.ajax( {
		            url: "modifyOblig/changeDate",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
		            	$('#mdlChangeDate').modal('hide');
		            	var lData = formatDataForXhr(pObj,false);
		            	crudObligation.options.dataTable.rows().clear();
		             	crudObligation.options.dataTable.rows.add(lData).draw();
		             	TableData = lData;
		            },
		        	error: errorHandler,
		        	complete: function() {
		        	}
		        });
			});
			$('#btnSaveObli').on('click', function(){
				if (TableData!=null){
					var lData = {"table":formatDataForXhr(TableData,true)}
					$.ajax( {
			            url: "modifyOblig/save",
			            type: "POST",
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
		            		alert("Status updated successfully");
			            },
			        	error: errorHandler,
			        	complete: function() {
			        	}
			        });
				}
			});
			$('#btnRemarks').on('click', function(){
				var lSelected = crudObligation.getSelectedRow();
		            if ((lSelected==null)||(lSelected.length==0)) {
		                    alert("Please select a row");
		                    return;
		        }
	            if (lSelected.data().id==null || lSelected.data().id==''){
					alert('Selected row cannot be modified.');
					return;
				}
				$('#mdlAddRemarks').modal('show');
			});
			$('#btnSaveRemarks').on('click', function(){
				var lSelected = crudObligation.getSelectedRow();
				var lRemarks= $('#remarks').val();
				var lTableData = formatDataForXhr(TableData,true);
				var lData = {id:lSelected.data().id,table: lTableData,respRemarks:lRemarks};
				$.ajax( {
		            url: "modifyOblig/addRemarks",
		            type: "POST",
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
		            	$('#mdlAddRemarks').modal('hide');
		            	var lData = formatDataForXhr(pObj,false);
		            	crudObligation.options.dataTable.rows().clear();
		             	crudObligation.options.dataTable.rows.add(lData).draw();
		             	TableData = lData;
		            },
		        	error: errorHandler,
		        	complete: function() {
		        	}
		        });
			});
			$('#btnBack').on('click', function(){
				location.href='oblig?adm=Y';
			});
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
		modDateForm$ = $('#obliDate-frmMain').xform(lModifyConfig);
		modDateForm = modDateForm$.data('xform');
		});
		
		function populateSettlor(){
			var option = null;
		    if( $('#mdlChangeSettlor #settlor option').length == 1){
		       	option = document.createElement('option');
		        option.text = '<%=AppConstants.FACILITATOR_NPCI%>';
		        option.value = '<%=AppConstants.FACILITATOR_NPCI%>';
		        $('#mdlChangeSettlor #settlor').append(option);
		       	option = document.createElement('option');
		        option.text = '<%=AppConstants.FACILITATOR_DIRECT%>';
		        option.value = '<%=AppConstants.FACILITATOR_DIRECT%>';
		        $('#mdlChangeSettlor #settlor').append(option);
		    }
	 	}
		function formatDataForXhr(pData,pXHR) {
			var lRowCount = pData==null?0:pData.length;
			var lColumns = crudObligation.options.tableConfig.columns;
        	var lColCount = lColumns.length;
        	var lData = [];
        	for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++){
        		///lData.push({});
        		var lRow = $.extend(true,{}, pData[lRowPtr]);
    			lData.push(lRow);
        	}
        	for (var lColPtr=0;lColPtr<lColCount;lColPtr++) {
        		var lColConfig = lColumns[lColPtr];
        		var lParser = lColConfig.parser;
        		for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++) {
        			var lRow = lData[lRowPtr];
    				var lValue = lRow[lColConfig.name];
    				lRow[lColConfig.name] = lValue;
    				if (lValue != null && lParser) {
    					if (pXHR){
    						lRow[lColConfig.name] = lParser.formatDate(lValue);
    					}else{
    						lRow[lColConfig.name] = lParser.parseDate(lValue);
    					}
    				}
        		}
        	}
        	return lData;
		}
		

		
	</script>


</body>
</html>