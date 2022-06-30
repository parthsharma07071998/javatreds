<!DOCTYPE html>
<%@page import="com.xlx.treds.auction.bean.ObligationBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.ObligationSplitsBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
boolean lAdmin = (request.getParameter("adm")!=null);
%>
<html>
	<head>
		<title>ObligationSplits : Complete list of splitted obligations </title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="ObligationSplits : Complete list of splitted obligations " />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contObligationSplits">
		<!-- frmSearch -->
		<div id="frmSearch" style="display:none">
			<div class="xform tab-pane panel panel-default no-margin" id=divFilter>
			<fieldset  class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="transactionEntity" class="label">Purchaser:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="transactionEntity"><option value="">Select Entity</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="financierEntity" class="label">Financier:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="financierEntity"><option value="">Select Entity</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="paymentSettlor" class="label">Settlor:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="paymentSettlor"><option value="">Select Settlor</option></select>
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
					<a href="javascript:;" class="right_links"  id=btnCreateReq><span class="glyphicon glyphicon-thrash"></span> Create</a>
					<a></a>
				</div>
			</div>
<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-bordered table-condensed" id="tblData" data-selector="multiple">
							<thead><tr>
								<th data-width="120px" data-class-name="select-checkbox" data-name="obid">Obligation Id</th>
								<th data-width="50px" data-name="partNumber">PartNo</th>
								<th data-width="100px" data-name="factUntId">Factoring Unit Id</th>
								<th data-width="100px" data-name="transactionEntity">Purchaser</th>
								<th data-width="100px" data-name="financierEntity">Financier</th>
								<th data-width="100px" data-name="legType">Type</th>
								<th data-width="100px" data-name="amount">Amount</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-width="100px" data-name="pfId">Pay File Id</th>
								<th data-width="100px" data-name="paymentSettlor">Settlor</th>
								<th data-width="100px" data-name="settlorProcessed">Settlor</th>
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
		</div>
	</div>
</div>

	<%@include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudObligationSplits$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationSplitsBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "obsplit",
					preSearchHandler: function(pFilter) {
						pFilter.forModification = true;
						<%if (lAdmin){%>
							if (pFilter.transactionEntity==null){
								alert("Please select a entity");
								return false;
							}
						<%}else{%>
							pFilter.transactionEntity = loginData.domain;
						<%}%>
						pFilter.status = '<%=ObligationBean.Status.Failed.getCode()%>';
						pFilter.legType = '<%=ObligationBean.Type.Leg_2.getCode()%>';
						return true;
					},
					autoRefresh: false,
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
			crudObligationSplits$ = $('#contObligationSplits').xcrudwrapper(lConfig);
			crudObligationSplits = crudObligationSplits$.data('xcrudwrapper');
			mainForm = crudObligationSplits.options.mainForm;
			$('#btnCreateReq').on('click',function(){
				var lRows=crudObligationSplits.getSelectedRows();
         		if(!lRows.length>0){
         			alert("Please select atlease one row.")
         		}else{
         			var lData = null;
         			var lPurchaser,lFinancier;
         			$.each(lRows, function(pIndex,pValue) {
         				if(pValue.data().obid !=null 
         						&& pValue.data().partNumber!=null){
         					if (lData==null) {
         						lData = "'"+pValue.data().obid+"^"+pValue.data().partNumber+"'";
         					}else{
         						lData += ","+"'"+pValue.data().obid+"^"+pValue.data().partNumber+"'";
         					}
         					
	         			}
         				if (pValue.data().transactionEntity!=null){
         					if (lPurchaser==null){
         						lPurchaser = pValue.data().transactionEntity;
         					}else{
         						if (lPurchaser!=pValue.data().transactionEntity){
         							alert('Please select same Transaction Entity');
         							return;
         						}
         					}
         				}
         				if (pValue.data().financierEntity!=null){
	         				if (lFinancier==null){
	         					lFinancier = pValue.data().financierEntity;
	     					}else{
								if (lFinancier!=pValue.data().financierEntity){
									alert('Please select same Financier Entity');
									return;
         						}
	     					}
						}
        			});
         			var lDataToSend = {'splitList':lData,'buyerCode':lPurchaser,'financierCode':lFinancier}
             		$.ajax( {
        		        url: "outsetreq/create",
        		        type: "POST",
        		        data:JSON.stringify(lDataToSend),
        		        success: function( pObj, pStatus, pXhr) {
        		        	location.href='outsetreq?id='+pObj.id;
        		        },
        		        error: errorHandler,
        		        complete: function() {
        		        }
        			});
         		}
			});
		});
		
	</script>


</body>
</html>