<!DOCTYPE html>
<%@page import="com.xlx.treds.AdminReportHandler"%>
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="java.util.List"%>
<%@page import="com.xlx.commonn.report.ReportFactory"%>
<%@page import="com.xlx.commonn.report.DefaultHandler"%>
<%@page import="com.xlx.treds.master.bean.HolidayMasterBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = request.getParameter("id");
DefaultHandler lHandler = ReportFactory.getInstance().getHandler(lId, null);
boolean lInst = AdminReportHandler.REPORTID_INST.equals(lId);
%>
<html>
    <head>
        <title><%=lHandler.getLabel() %></title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
		<script>
<%
if (lInst) {
%>
		function showDocuments() {
			var lSelected = crudReportTool.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			var lData=lSelected.data();
			var lImgs=["instImage","creditNoteImage","sup1","sup2","sup3","sup4","sup5"];
			var lLbls=["Invoice","Credit Note","Supporting 1","Supporting 2","Supporting 3","Supporting 4","Supporting 5"];
			var lHtml='<table class="table table-bordered" style="width:100%"><tbody>';
			for (var lPtr=0;lPtr<lImgs.length;lPtr++) {
				var lImg=lData[lImgs[lPtr]];
				if (lImg != null) {
					lHtml+='<tr><td>'+lLbls[lPtr]+'</td><td>'+lImg+'</td><td>';
					lHtml+='<a href="upload/INSTRUMENTS/'+lImg+'" download>  <span class="fa fa-download"> Download</span></a>';
					lHtml+='</td></tr>';
				}
			}
			lHtml+='</tbody></table>';
			alert(lHtml);
		}
<%
}
%>			
		</script>
    </head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="<%=lHandler.getLabel() %>" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contReportTool">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title"><%=lHandler.getLabel() %></h1>
			</div>
			<div class="pull-right">
<%
if (lInst) {
%>	
				<a class="btn btn-link btn-lg" href="javascript:;" onClick="javascript:showDocuments()"><span class="fa fa-"></span> Documents</a>
<%
}
%>			
				<a class="btn btn-link btn-lg" href="javascript:;" onClick="javascript:showFilter()"><span class="fa fa-search"></span> Search</a>
				<a class="btn btn-link btn-lg" href="javascript:;" onClick="javascript:downloadCSV()"><span class="fa fa-download"></span> Download CSV</a>
			</div>
		</div>
		<!-- frmSearch -->
		<div class="xform" id="frmSearch">
		<div class="modal fade" tabindex=-1 id="mdlFilter"><div class="modal-dialog"><div class="modal-content">
		<div class="modal-header">
		<span>Filter</span>
		<button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
			<div>
<%
List<BeanFieldMeta> lFilterFields = lHandler.getFilterFieldMetas();
for (BeanFieldMeta lBeanFieldMeta : lFilterFields) {
    if (lBeanFieldMeta.getDataSetType() != null) {
%>				<div class="row form-group">
					<div class="col-sm-4"><section><label for="<%=lBeanFieldMeta.getName() %>" class="label"><%=lBeanFieldMeta.getLabel() %>:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="<%=lBeanFieldMeta.getName()%>"><option value="">Select <%=lBeanFieldMeta.getLabel()%></option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
<%        
    } else {
        BeanFieldMeta.DataType lDataType = lBeanFieldMeta.getDataType();
        if ((lDataType == BeanFieldMeta.DataType.DATE) || (lDataType == BeanFieldMeta.DataType.TIME) || (lDataType == BeanFieldMeta.DataType.DATETIME)) {
%>				<div class="row form-group">
					<div class="col-sm-4"><section><label for="<%=lBeanFieldMeta.getName()%>" class="label"><%=lBeanFieldMeta.getLabel()%>:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="<%=lBeanFieldMeta.getName()%>" placeholder="<%=lBeanFieldMeta.getLabel()%>" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
<%        
        } else {
%>				<div class="row form-group">
					<div class="col-sm-4"><section><label for="<%=lBeanFieldMeta.getName()%>" class="label"><%=lBeanFieldMeta.getLabel()%>:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="<%=lBeanFieldMeta.getName()%>" placeholder="<%=lBeanFieldMeta.getLabel()%>">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
<%        
        }
    }
}
%>
    		<div class="box-footer">
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
		</div></div></div>
		</div>
		<!-- frmSearch -->
		<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered " id="tblData">
							<thead><tr>
<%
List<BeanFieldMeta> lListFields = lHandler.getListFieldMetas();
for (BeanFieldMeta lBeanFieldMeta : lListFields) {
%>								<th data-width="80px" data-name="<%=lBeanFieldMeta.getName()%>"><%=lBeanFieldMeta.getLabel() %></th>
<%    
}
%>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
		<!-- frmMain -->
		<div id="frmMain">
		</div>
    	<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudReportTool$,crudReportTool;
	$(document).ready(function() {
		var lFormConfig = <%=lHandler.getBeanMeta().getJsonConfig()%>;
		var lConfig = {
				tableConfig : {order:[]},
				resource: "rpttl/<%=lId%>",
				postSearchHandler:function(pData) {
					$('#mdlFilter').modal('hide');
					if (pData && pData.length > <%=DefaultHandler.MAX_RECORD_COUNT%>) {
						alert("Showing only <%=DefaultHandler.MAX_RECORD_COUNT%> records. Please refine filter or use download.");
					}
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudReportTool$ = $('#contReportTool').xcrudwrapper(lConfig);
		crudReportTool=crudReportTool$.data('xcrudwrapper');
	});
	function showFilter() {
		showModal($('#mdlFilter'));
	}
	function downloadCSV(){
		var lFilter=JSON.stringify(crudReportTool.options.searchForm.getValue());
		downloadFile("rpttl/<%=lId%>/download",null,lFilter);
	}

	</script>
   	
    </body>
</html>