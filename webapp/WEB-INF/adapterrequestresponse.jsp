
<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.adapter.bean.AdapterRequestResponseBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Iocl Resend</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Iocl Resend" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contAdapterRequestResponse" >		
		<div  id="frmSearch">
				<div class="filter-block clearfix">
						<div class="">
							<span class="right_links">
							<a href="javascript:;" id=btnResend><span class="fa fa-pencil"></span> Resend </a>
							</span>
						</div>
				</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered table-condensed" id="tblData" >
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="50px" data-name="entityCode">Entity Code</th>
								<th data-width="60px" data-name="processId">Process Id</th>
								<th data-width="100px" data-name="key">Instrument Id</th>
								<th data-width="50px" data-name="type">Type</th>
								<th data-width="100px" data-name="apiRequestType">Request Type</th>
								<th data-width="100px" data-name="apiRequestUrl">Request URL</th>
<!-- 								<th data-width="100px" data-name="apiRequestData">Request Dsta</th> -->
								<th data-width="100px" data-name="uid">Unique Identification</th>
								<th data-width="100px" data-name="timestamp">Timestamp</th>
								<th data-width="100px" data-name="requestStatus">Request Status</th>
								<th data-width="100px" data-name="responseAckStatus">Response Ack Status</th>
								<th data-width="100px" data-name="apiResponseUrl">Response Url</th>
<!-- 								<th data-width="100px" data-name="apiResponseData">Response Data</th> -->
								<th data-width="100px" data-name="apiResponseStatus">Response Status</th>
<!-- 								<th data-width="100px" data-name="apiResponseDataReturned">Response Data Returned</th> -->
<!-- 								<th data-width="100px" data-name="provResponseAckStatus">Provisional Response Ack Status</th> -->
<!-- 								<th data-width="100px" data-name="provResponseData">ProvResponseData</th> -->
							</tr></thead>
						</table>
					</div>
				</div>
				<div>
					<a class="secure" href="javascript:;" id=btnResend><span class="fa fa-minus"></span> Resend </a>
				</div>
			</fieldset>
			</div>
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<!-- <header>null</header> -->
		</div>
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>

	<script type="text/javascript">
		var crudAdapterRequestResponse$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AdapterRequestResponseBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "adapterrequestresponse",
					autoRefresh: true,
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
			crudAdapterRequestResponse$ = $('#contAdapterRequestResponse').xcrudwrapper(lConfig);
			crudAdapterRequestResponse = crudAdapterRequestResponse$.data('xcrudwrapper');
			mainForm = crudAdapterRequestResponse.options.mainForm;
			searchForm = crudAdapterRequestResponse.options.searchForm;
			$('#btnResend').on('click',function(){
				var lRows = crudAdapterRequestResponse.getSelectedRow();
				lData = {};
				if(lRows.length < 1 || lRows.length == 0){
					alert("Please select a row");
				}
				lData['id']=lRows.data().id;
				lData['apiResponseStatus']=lRows.data().apiResponseStatus;
				$.ajax({
			        url: 'adapterrequestresponse/resendresponse',
			        type: 'POST',
			        data:JSON.stringify(lData),
			        success: function( pObj, pStatus, pXhr) {
			        	crudAdapterRequestResponse.showSearchForm();
			        },
			    	error: errorHandler
			    	});
			});
		});
		
	</script>


</body>
</html>