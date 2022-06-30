<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.other.bean.CustomFieldBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
	Boolean lNew = (Boolean) request.getAttribute(CommonAppConstants.PARAM_NEW);
	boolean lNewBool = (lNew != null) && lNew.booleanValue();
	String lModify = (String) request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Custom Fields</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
        
	</head>
    <body class="page-body">
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contCustomField">
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links"><span class="glyphicon glyphicon-edit" id="btnModify"></span> Edit</a>
					<a href="javascript:;" class="right_links"><span class="glyphicon glyphicon-edit" id="btnNew"></span> New</a>
					<a href="http://localhost:8080/treds/rest/customfields/BA0000047" class="right_links"><span class="glyphicon glyphicon-edit" id="btnNew1"></span> LINK</a>
					
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-width="70px" data-name="code">Code</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Custom Fields</h1>
				</div>
			</div>
    		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2" hidden><section><label for="id" class="label">Id:</label></section></div>
					<div class="col-sm-4" hidden>
						<section class="input">
						<input type="text" id="id" placeholder="Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="code" placeholder="Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="fields" class="label">Required Fields:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="fields"><option value="">Select Fields</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
				<center><h3>Field 1 Details</h3></center>
				<div class="row">
					<div class="col-sm-2"><section><label for="field1label" class="label">Frontend Label:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field1label" placeholder="Label">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field1Name" class="label">Api Payload Name/Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field1Name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field1Type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field1Type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field1Mandatory" class="label">Field1Mandatory:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field1Mandatory"><option value="">Select Field1Mandatory</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
				<center><h3>Field 2 Details</h3></center>
				<div class="row">
					<div class="col-sm-2"><section><label for="field2label" class="label">Frontend Label:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field2label" placeholder="Label">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field2Name" class="label">Api Payload Name/Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field2Name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field2Type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field2Type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field2Mandatory" class="label">Field2Mandatory:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field2Mandatory"><option value="">Select Field2Mandatory</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
				<center><h3>Field 3 Details</h3></center>
				<div class="row">
					<div class="col-sm-2"><section><label for="field3label" class="label">Frontend Label:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field3label" placeholder="Label">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field3Name" class="label">Api Payload Name/Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field3Name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field3Type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field3Type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field3Mandatory" class="label">Field3Mandatory:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field3Mandatory"><option value="">Select Field3Mandatory</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
				<center><h3>Field 4 Details</h3></center>
				<div class="row">
					<div class="col-sm-2"><section><label for="field4label" class="label">Frontend Label:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field4label" placeholder="Label">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field4Name" class="label">Api Payload Name/Key:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="field4Name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field4Type" class="label">Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field4Type"><option value="">Select Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="field4Mandatory" class="label">Field4Mandatory:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="field4Mandatory"><option value="">Select Field4Mandatory</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-primary" id=btnSave><span class="fa fa-save"></span> Save</button>
				</div>
			</footer>
			</div>
		</div>
	</div>

	<%@include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
		var crudCustomField$,crudCustomField,mainForm;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CustomFieldBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "customfields",
					modify: <%=lModify%>,
					<%if (lNewBool) {%>	
						new: true,
					<%} else if (lModify != null) {%>
						modify: <%=lModify%>,
					<%}%>
					postModifyHandler: function(pObj) {
						mainForm.enableDisableField('code',false,false);
						mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds4,false,false);
						mainForm.alterField(crudCustomField.options.fieldGroups.lFlds4Mandatory,false,false);
						enableDisable();
						return true;
					},
					postSaveHandler: function(pObj) {
						alert('success')
						return false;
					},
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudCustomField$ = $('#contCustomField').xcrudwrapper(lConfig);
			crudCustomField = crudCustomField$.data('xcrudwrapper');
         	mainForm = crudCustomField.options.mainForm;
		});
		
		$('#fields').on('change', function() {
			enableDisable();
		});
		
		function enableDisable(){
			var lNoOfFields = mainForm.getField('fields').getValue();
    		var lClrFields = null;
    		if (lNoOfFields=="1"){
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds4,false,false);
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds1,true,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds4Mandatory,false,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds1Mandatory,true,false);
    			lClrFields = crudCustomField.options.fieldGroups.lFlds4.filter(x => !crudCustomField.options.fieldGroups.lFlds1.includes(x));
    			mainForm.enableDisableField(lClrFields,false,true);
    		}else if (lNoOfFields=="2"){
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds4,false,false);
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds2,true,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds4Mandatory,false,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds2Mandatory,true,false);
    			lClrFields = crudCustomField.options.fieldGroups.lFlds4.filter(x => !crudCustomField.options.fieldGroups.lFlds2.includes(x));
    			mainForm.enableDisableField(lClrFields,false,true);
    		}else if (lNoOfFields=="3"){
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds4,false,false);
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds3,true,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds4Mandatory,false,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds3Mandatory,true,false);
    			lClrFields = crudCustomField.options.fieldGroups.lFlds4.filter(x => !crudCustomField.options.fieldGroups.lFlds3.includes(x));
    			mainForm.enableDisableField(lClrFields,false,true);
    		}else if (lNoOfFields=="4"){
    			mainForm.enableDisableField(crudCustomField.options.fieldGroups.lFlds4,true,false);
    			mainForm.alterField(crudCustomField.options.fieldGroups.lFlds4Mandatory,true,false);
    		}
		}

		
	</script>


</body>
</html>