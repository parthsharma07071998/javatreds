<!DOCTYPE html>
<%@page import="groovy.json.JsonBuilder"%>
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.user.bean.RoleMasterBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
List<Map<String, Object>> lList = new ArrayList<Map<String, Object>>();
for (AppConstants.Owner lOwner : AppConstants.Owner.class.getEnumConstants()) {
    Map<String, Object> lValMap = new HashMap<String, Object>();
    lValMap.put(BeanFieldMeta.JSONKEY_VALUE, lOwner.getCode());
    lValMap.put(BeanFieldMeta.JSONKEY_TEXT, lOwner.toString());
    lList.add(lValMap);
}
String lOwnerJson = new JsonBuilder(lList).toString();
%>
<html>
    <head>
        <title>TREDS | Roles</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Roles" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contRoleMaster">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Roles</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="name" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2 state-T"><section><label for="userTypeList" class="label">User Types:</label></section></div>
						<div class="col-sm-2 state-T">
							<section class="select">
							<select id="userTypeList" multiple="multiple" data-role="multiselect"></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2 state-T"><section><label for="owner" class="label">Role Type:</label></section></div>
						<div class="col-sm-2 state-T">
							<section class="select">
							<select id="owner"><option value="">Select Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
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
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>		
					<span class="right_links">
						<a href="javascript:crudRoleMaster.searchHandler();"><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
						<a href="javascript:;" class="secure" data-seckey="role-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
						<a href="javascript:;" class="secure" data-seckey="role-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
						<a href="javascript:;" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
						<a href="javascript:;" id=btnDownloadReportCSV style=''><span class="fa fa-download"></span> Roles Report</a>
						<a></a>
					</span>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="id">Id</th>
									<th data-width="200px" data-name="name">Name</th>
									<th data-width="300px" data-name="desc">Description</th>
									<th data-width="100px" data-name="active">Active</th>
									<th data-width="100px" data-name="owner">Role Type</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
    	<div class="xform box box-danger" id="frmMain" style="display:none">
    		<div class="box-body">
				<fieldset>
					<div class="row form-group">
						<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="name" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="desc" class="label">Description:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="desc" placeholder="Desc">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row form-group">
						<div class="col-sm-2"><section><label for="userTypeList" class="label">User Types:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="userTypeList" multiple="multiple" data-role="multiselect"></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="active" class="label">Active:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="active"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
						</div>
					</div>
					<div class="row form-group">
						<div class="col-sm-2"><section><label for="owner" class="label">Role Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="owner"><option value="">Select Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" id="divActions">
					</div>
		    		<div class="box-footer">
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
				</fieldset>
    		</div>
    	</div>
    	<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-multiselect.js"></script>
   	<script id="tplActions" type="text/x-handlebars-template">
{{#each this}}
<div class="col-sm-4">
<label class="checkbox"><input name="actGrp" type="checkbox" onClick=javascript:checkGrp(this.checked,{{grpId}})><i></i><h2>{{grpName}}</h2></label>
{{#each actions}}
<div class="row"><div class="col-sm-1"></div><div class="col-sm-11">
<section>
<label class="checkbox"><input class="grp-{{../grpId}}" name="actionList" value="{{id}}" type="checkbox"><i></i><span>{{name}}</span></label>
</section>
</div></div>
{{/each}}
</div>
{{/each}}
   	</script>
	<script type="text/javascript">
		var crudRoleMaster$,crudRoleMaster;
		var mainForm;
		var tplActions;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(RoleMasterBean.class).getJsonConfig()%>;
			$.each(lFormConfig.fields,function(pIdx,pVal){
				if (pVal.name=='owner') {
					pVal.dataSetType='STATIC';
					pVal.dataSetValues = <%=lOwnerJson%>;
					pVal.notNull = true;
					pVal.label = "Role Type";
				}
			});
			var lConfig = {
					resource: "role",
					autoRefresh: true,
					postModifyHandler: function(pData) {
						populateActions(pData.userTypeList,pData.actionList);
						return true;
					},
					preSaveHandler: function(pData) {
						pData.actionList=getActions();
						return true;
					}
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudRoleMaster$ = $('#contRoleMaster').xcrudwrapper(lConfig);
			crudRoleMaster = crudRoleMaster$.data('xcrudwrapper');
			mainForm = crudRoleMaster.options.mainForm;
			searchForm=	crudRoleMaster.options.searchForm;
			tplActions = Handlebars.compile($('#tplActions').html());
			$("#btnDownloadCSV").on("click",function(){
				var lFilter=searchForm.getValue(true);
				lFilter['columnNames'] = crudRoleMaster.getVisibleColumns();
				downloadFile('role/all',null,JSON.stringify(lFilter));
			});
			$("#btnDownloadReportCSV").on("click",function(){
				var lFilter=searchForm.getValue(true);
				lFilter['columnNames'] = ["name","desc"]
				downloadFile('role/rolesReport',null,JSON.stringify(lFilter));
			});
			$('#frmMain #userTypeList').on('change', function(pEvent){
				var lTypes = mainForm.getField('userTypeList').getValue();
				populateActions(lTypes);
			});
		});
		function populateActions(pTypes, pActions) {
			$('#divActions').html(null);
			if ((pTypes != null)&&(pTypes.length>0)) {
				$.ajax({
					url: 'role/actions?ut='+pTypes.join(),
		            type: 'GET',
		            success: function( pObj, pStatus, pXhr) {
		            	$('#divActions').html(tplActions(pObj));
		            	if (pActions) setActions(pActions);
		            },
		        	error: errorHandler
				});
			}
		}
		function setActions(pActionList) {
			var lActionList$=$('input[name=actionList]');
			lActionList$.val(pActionList);
		}
		function getActions() {
			var lActions=[];
			var lActionList$=$('input[name=actionList]');
			lActionList$.filter(':checked').each(function(){
				lActions.push($(this).val());
			});
			return lActions;
		}
		function checkGrp(pChecked,pGrpId) {
			$('#divActions .grp-'+pGrpId).each(function(){
				$(this).prop('checked', pChecked);
			})
		}
	</script>
   	
    </body>
</html>