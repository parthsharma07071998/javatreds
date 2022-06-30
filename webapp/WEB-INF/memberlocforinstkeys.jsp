
<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.instrument.bean.MemberLocationForInstKeysBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Member Location Instrument Keys</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
<!-- 		<link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Member Location Instrument Keys" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contMemberLocationForInstKeys" >
		<div class="page-title">
			<div class="title-env">
				<h1 class="title"> Member Location Instrument Keys</h1>
			</div>
		</div>		
<!-- frmSearch -->
		<div id="frmSearch">
		</div>
		
		<div class="xform" style="display:none" id="frmMain">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="clIdList" class="label">Company Location Id:</label></section></div>
					<div class="col-sm-10">
						<section class="select">
						<select id="clIdList" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
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
	<script src="../js/jquery.bootstrap-duallistbox.js"></script>

	<script type="text/javascript">
		var crudMemberLocationForInstKeys$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(MemberLocationForInstKeysBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "memberlocforinstkeys",
					autoRefresh: false,
					postSaveHandler: function(pObj) {
						alert("Saved Successfully", "Information", function() {
							crudMemberLocationForInstKeys.newHandler();
							getLocation();
	            		});
         				return false;
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
			crudMemberLocationForInstKeys$ = $('#contMemberLocationForInstKeys').xcrudwrapper(lConfig);
			crudMemberLocationForInstKeys = crudMemberLocationForInstKeys$.data('xcrudwrapper');
			mainForm = crudMemberLocationForInstKeys.options.mainForm;
			crudMemberLocationForInstKeys.newHandler();
			getLocation();
		});
		
		function getLocation(){
			$.ajax( {
		        url: 'memberlocforinstkeys/getlocations',
		        type: "GET",
		        success: function( pObj, pStatus, pXhr){
		        	mainForm.getField('clIdList').setValue(pObj.value);
		        },
		    	error: errorHandler
		    });
		}
		
	</script>


</body>
</html>