<!DOCTYPE html>
<%@page import="com.xlx.common.registry.bean.RegistryEntryBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html lang="en">
  <head>
	<title>Registry</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Registry" />
    	<jsp:param name="desc" value="Manage" />
    </jsp:include>
	<div class="container" id="regEntries">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Registry</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display:none" id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="name" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
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
					<a href="javascript:;" class="right_links" id=btnViewEntry><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="id">Id</th>
									<th data-width="100px" data-name="name">Name</th>
									<th data-width="100px" data-name="value">Value</th>
									<th data-width="100px" data-name="description">Description</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		<div class="xform box" style="display:none" id="frmEntry">
			<fieldset>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnCloseEntry><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
		</div>
		<!-- frmMain -->
	</div>

	<div id="divEntryForm"></div>
	
	<%@include file="footer1.jsp" %>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
<script id="tplValue" type="text/x-handlebars-template">
<div class="row"><div class="col-sm-3"><b>Name:</b></div><div class="col-sm-9">{{name}}</div></div>
<div class="row"><div class="col-sm-3"><b>Desc:</b></div><div class="col-sm-9">{{desc}}
<ul>
{{#each cols}}
<li><b>{{name}}:</b> {{desc}}</li>
{{/each}}
</ul>
</div></div>
{{#multi}}
<div class="row"><div class="col-sm-12"><button class="btn btn-default pull-right" onClick="javascript:openEntryForm(-1)"><span class="fa fa-plus"></span> Add</button></div></div>
{{/multi}}
<table class="table table-hover">
<thead><tr>
{{#each cols}}
<th>{{name}}</th>
{{/each}}
<th> </th></tr></thead>
<tbody>
{{#each rows}}
<tr>
{{#each vals}}<td>{{.}}</td>
{{/each}}
<th><button class="btn btn-sm btn-link" onClick="javascript:openEntryForm({{@index}})"><span class="fa fa-pencil"></span> Edit</button>
<button class="btn btn-sm btn-link" onClick="javascript:deleteEntry({{@index}})"><span class="fa fa-remove"></span> Remove</button>
</tr>
{{/each}}
</tbody>
</table>
</script>
<script id="tplForm" type="text/x-handlebars-template">
<div class="container" id="contRegistryForm">		
		<div class="xform" style="display:none" id="frmSearch">
		</div>
		<div class="xform box box-danger" id="frmMain">
			<fieldset>
{{#each cols}}
				<div class="row">
					<div class="col-sm-4"><section><label for="{{name}}" class="label">{{name}}:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="{{name}}" placeholder="{{name}}">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
{{/each}}
    		<footer>
				<div class="btn-groupX pull-right">
					<input type="hidden" id="reId">
					<input type="hidden" id="revId">
					<button type="button" class="btn btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
			</fieldset>
		</div>
	</div>
</script>
<script type="text/javascript">
var crudRegEntries$ = null, crudRegEntries = null;
var crudRegForm$ = null, crudRegForm = null;
var tplValue,tplForm,reId,data;
$(document).ready(function() {
	tplValue=Handlebars.compile($('#tplValue').html());
	tplForm=Handlebars.compile($('#tplForm').html());
	var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RegistryEntryBean.class).getJsonConfig()%>;
	var lConfig = {
			resource: "registry",
			autoRefresh: true
	};
	lConfig = $.extend(lConfig, lFormConfig);
	crudRegEntries$ = $('#regEntries').xcrudwrapper(lConfig);
	crudRegEntries = crudRegEntries$.data('xcrudwrapper');
	$('#btnViewEntry').on('click', function() {
		var lSelected = crudRegEntries.getSelectedRow().data();
		if (lSelected != null) {
			reId = crudRegEntries.selectedRowKey(lSelected);
			openRegistryEntry();
		} else {
			alert('Please select a row.');
		}
	});
	$('#btnCloseEntry').on('click', function() {
		$('#frmEntry').hide();
		crudRegEntries.showSearchForm();
	});
});
function openRegistryEntry() {
	$.ajax({
        url: 'registry/' + reId,
        type: 'GET',
        success: function( pObj, pStatus, pXhr) {
        	data=pObj;
        	$('#frmEntry fieldset').html(tplValue(data));
        	$('#divEntryForm').html(tplForm(data));
        	$('#frmSearch').hide();
        	$('#frmEntry').show();
        	var lUpdateFields=[];
        	var lCols=data.cols.slice();
        	lCols.push({name:"reId", dataType:"INTEGER"});
        	lCols.push({name:"revId", dataType:"INTEGER"});
        	$.each(lCols, function(pIdx,pVal){
        		lUpdateFields.push(pVal.name);
        	});
        	var lConfig={fields:lCols,fieldGroups:{update:lUpdateFields},saveHandler:saveHandler, closeHandler:closeHandler};
        	crudRegForm$ = $('#contRegistryForm').xcrudwrapper(lConfig);
        	crudRegForm = crudRegForm$.data('xcrudwrapper');
        },
    	error: errorHandler
    });
}
function openEntryForm(pIdx) {
	var lData={reId:reId};
	if (pIdx >= 0) {
		lData.revId=data.rows[pIdx].id;
		$.each(data.cols, function(pIndex, pVal) {
			lData[pVal.name]=data.rows[pIdx].vals[pIndex];
		});
	}
	crudRegForm.options.mainForm.setValue(lData);
	crudRegForm.options.mainForm.setMode("update");
	crudRegForm.showMainForm(false);
	$('#frmEntry').hide();
}
function deleteEntry(pIdx) {
	var lData={reId:reId,revId:data.rows[pIdx].id};
	var lUrl = 'registry/'+reId+'/'+(data.rows[pIdx].id==null?'-1':data.rows[pIdx].id);
	confirm('You are about to delete the selected record. Are you sure?','Confirmation','Yes',function(pYes) {
		if (pYes) {
			$.ajax( {
	            url: lUrl,
	            type: 'DELETE',
	            success: function( pObj, pStatus, pXhr) {
	            	alert("Deleted successfully", "Information", function() {
	            		openRegistryEntry();
	            	});
	            },
	        	error: errorHandler
	        });					
		}
	});
}
function saveHandler() {
	var lErrors = crudRegForm.options.mainForm.check();
	if ((lErrors != null) && (lErrors.length > 0)) {
		crudRegForm.showError();
		return false;
	}
	var lData = crudRegForm.options.mainForm.getValue();
	$.ajax( {
           url: 'registry',
           type: crudRegForm.options.mainForm.method,
           data:JSON.stringify(lData),
           success: function( pObj, pStatus, pXhr) {
           		alert("Saved successfully", "Information", function() {
           			crudRegForm.showSearchForm();
           			openRegistryEntry();
           		});
           },
       	error: errorHandler
       });					
}
function closeHandler() {
	crudRegForm.options.mainForm.setValue(null);
	crudRegForm.options.mainForm.setMode(null);
	crudRegForm.showSearchForm();
	openRegistryEntry();
}
</script>

</body>
</html>