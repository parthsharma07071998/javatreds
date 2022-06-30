<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.CircularBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
boolean lAdmin = (request.getParameter("adm")!=null);
%>
<html>
	<head>
		<title>Circular Details : The list of all the circulars created by TReDS.</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Circular Details : The list of all the circulars created by TReDS." />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contCircular">		
				<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Circulars</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="row">
					<div class="col-sm-2"><section><label for="circularNo" class="label">Circular No:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="circularNo" placeholder="Circular No">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="filterFromDate" class="label">From Date:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="filterFromDate" placeholder="To Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="filterToDate" class="label">To Date:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="filterToDate" placeholder="To Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row state-T">
					<div class="state-T col-sm-2"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
					<div class="state-T col-sm-2">
						<section class="select">
						<select id="purchaser"><option value="">Select Purchaser</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="state-T col-sm-2"><section><label for="supplier" class="label">Supplier:</label></section></div>
					<div class="state-T col-sm-2">
						<section class="select">
						<select id="supplier"><option value="">Select Supplier</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="state-T col-sm-2"><section><label for="financier" class="label">Financier:</label></section></div>
					<div class="state-T col-sm-2">
						<section class="select">
						<select id="financier"><option value="">Select Financier</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row state-T">
					<div class="col-sm-2"><section><label for="department" class="label">Department:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="department"><option value="">Select Department</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="admin" class="label">Admin:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="admin"><option value="">Select Admin</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="user" class="label">User:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="user"><option value="">Select User</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div>
					<div class="col-sm-2"><section><label for="fileName" class="label">File Name:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="fileName" placeholder="File Name">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					</div>
					<div id='forArchive' style="display:none">
					<div class="col-sm-2"><section><label for="archive" class="label">Archive :</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="archive"><option value="">Archive</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					</div>
					<div class="col-sm-2"><section><label for="category" class="label">Circular Category:</label></section></div>
					<div class="col-sm-2">
						<section class="select">
						<select id="category"><option value="">Select Circular Category</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
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
			 <div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
					 <li><a href="#tab1" data-toggle="tab"> Circulars / Release Notes <span id="badge1" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab2" data-toggle="tab"> Old Circulars / Release Notes <span id="badge2" class="badge badge-primary"></span></a></li>
			 		 <li><a href="#tab3" data-toggle="tab"> Archive <span id="badge3" class="badge badge-primary"></span></a></li>
			 		</ul>
			 </div>
			 <div class="">
				<fieldset>
					<div id="tab1" class="hidden btn-group1">
						<div class="filter-block clearfix">
							<div class="">
<!-- 								<span class="left_links"> -->
<!-- 									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select> -->
<!-- 								</span> -->
								<span class="right_links">
									<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
									<a class="secure right_links btn-group0" href="javascript:;" data-seckey="circulars-save" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span>New</a>
									<a></a>
								</span>
							</div>
						</div>
						<div >
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
				
					<div id="tab2" class="hidden btn-group2">
						<div class="filter-block clearfix">
							<div class="">
<!-- 								<span class="left_links"> -->
<!-- 									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select> -->
<!-- 								</span> -->
								<span class="right_links">
									<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
									<a class="secure right_links btn-group0" href="javascript:;" onclick="archiveData(true,2);" data-seckey="circulars-save" id=btnArchive><span class="fa fa-archive"></span>Archive</a>
									<a></a>
								</span>
							</div>
						</div>
						<div >
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
						</div>
					</div>
					
					<div id="tab3" class="hidden btn-group3">
						<div class="filter-block clearfix">
							<div class="">
<!-- 								<span class="left_links"> -->
<!-- 									Sort On <select class="" id="elemSortCol"></select> <select class="" id="elemSortOrd"></select> -->
<!-- 								</span> -->
								<span class="right_links">
									<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
									<a class="secure right_links btn-group0" href="javascript:;" onclick="archiveData(false,3);" data-seckey="circulars-save" id=btnUnArchive><span class="fa fa-archive"></span>UnArchive</a>
									<a></a>
								</span>
							</div>
						</div>
						<div >
							<div id="elemTable">
							</div>
		           		</div>
						<div class="row">
							<div class="col-md-4 col-sm-4"></div>
							<div class="col-md-4 col-sm-4 text-center">
		                    	<span id="elemCurPage"></span>.
		                    	Page Size: <select class="" id="elemPageSize"></select>
		                    </div>
		                    <div class="col-md-4 col-sm-4 text-right">
		                        <ul class="pagination pagination-sm" style="margin:0px" id="elemPager"></ul>
		                    </div>
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
					<h1 class="title">User</h1>
				</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
					<div class="col-sm-2"><section><label for="circularNo" class="label">Circular No:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="circularNo" placeholder="Circular No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="date" class="label">Circular Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="date" placeholder="Circular Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="title" class="label">Title:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<input type="text" id="title" placeholder="Title">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="description" class="label">Description:</label></section></div>
					<div class="col-sm-10">
						<section class="input">
						<textarea id="description" placeholder="Description" rows="7" cols="154" wrap="hard" >
						</textarea>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="department" class="label">Department:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="department"><option value="">Select Department</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="category" class="label">Circular Category:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="category"><option value="">Select Circular Category</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2 state-T"><section><label for="admin" class="label">Admin:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="admin"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2 state-T"><section><label for="purchaser" class="label">Purchaser:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="purchaser"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="user" class="label">User:</label></section></div>
					<div class="col-sm-4">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="user"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2 state-T"><section><label for="supplier" class="label">Supplier:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="supplier"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="archive" class="label">Archive :</label></section></div>
					<div class="col-sm-4">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="archive"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2 state-T"><section><label for="financier" class="label">Financier:</label></section></div>
					<div class="col-sm-4 state-T">
						<section class="input inline-group">
						<label class="radio"><input type=radio id="financier"><i></i><span></span><b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<input type="hidden" id="storageFileName" data-role="xuploadfield" data-file-type="CIRCULARS" />
					<div class="col-sm-2"><section><label for="storageFileName" class="label">Storage File Name:</label></section></div>
					<div class="col-sm-4">
						<section id='storageButtons' class="state-mandatory">
							<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
							<span class="upl-info"></span>
							<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
						</section>
					</div>
					<div class="col-sm-2"><section><label for="displayAsNewForDays" class="label">Display As New For Days:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="displayAsNewForDays" placeholder="Display As New For Days">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
				</fieldset>
    		</div>
    	</div>
    	<!-- frmMain -->
	</div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/jquery.xtemplatetable.js"></script>
	<script id="tpl" type="text/x-handlebars-template">
		{{#each this}}
<div class="col-md-12 col-sm-12">
				<div class="lighter-gray-bx"  style="background-color:white">
			<div class="row table-row card" >
 			  <!-- Table Row 1 -->
   				<div class="col-md-12">
				<div class="row horiz_table" >
        		 <div class="col-md-12">
            	<table class="table">
              	 <tbody>
                  <tr style="border: 1px solid grey">
<%
if (lAdmin) {
%>
				<td style="width:15%" bgcolor="#ffcc99" align="center" class="table-content"><span><input type="checkbox" class="chkbox{{tab}}" data-id="{{id}}"/>&nbsp;&nbsp;<b><a href="javascript:;" class="right_links" onClick="javascript:crudCircular.modifyHandler(null,[{{id}}],false)"><i class="glyphicon glyphicon-pencil"></i>&nbsp;&nbsp;</a></span>{{date}}<br>({{category}})</b></td>
<%
}else{
%>
	<td style="width:15%" bgcolor="#ffcc99" align="center" class="table-content"><b>{{date}}<br>({{category}})</b></td>
<%	
}
%>
 					 <td class="table-content" bgcolor="#ffcc99" style="width:80%"><i>{{circularNo}}</i><br><b>{{title}}</b></td>
					 
					
<%
if (lAdmin) {
%>
				 	<td class="table-content" bgcolor="#ffcc99" align="center" ><span ><b><a href="javascript:;" class="right_links" onClick="javascript:removeCircular({{id}},{{tab}})"><i class="glyphicon glyphicon-remove"></i></a></b></span></td>
<%
}else{
%>
	 				<td class="table-content" bgcolor="#ffcc99" align="center" ></td>
<%	
}
%>
 				  </tr>
                  <tr>
 					 <td style="width:15%" class="table-content"></td>
                     <td colspan="2" class="table-content"><b>{{description}}</b></td>
                  </tr>
				  <tr  >
					 <td style="width:15%" class="table-content"></td>
                     <td style="border-top:1px solid darkgray" class="table-content"><a href="javascript:downloadFile('{{storageFileName}}')" title="Download File" style="color:blue !important"><span class="fa fa-download">&nbsp;&nbsp;&nbsp;</span>{{fileName}}</a></td>
                  </tr>
                </tbody>
            	</table>
         		</div>
         		</div>
         		</div>
      			</div> 
			</div>
			</div>
		{{/each}}
	</script>
	<script type="text/javascript">
		var crudCircular$ = null, crudCircular = null, mainForm = null;
		var tabIdx, tabData;
		var searchTabIdx;
		var templates;
		var allData = [];
		var idList = [];
		$(document).ready(function() {
			templates=[];
			for (var lPtr=1;lPtr<4;lPtr++) {
				var lTableConf={
					template:Handlebars.compile($('#tpl').html())
				};
				var lTplTable$=$('#tab'+lPtr).xtemplatetable(lTableConf);
				templates[lPtr]=lTplTable$.data('xtemplatetable');
			}
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CircularBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "circulars",
					autoRefresh: true,
					keyFields :['id'],
					preSearchHandler: function(pFilter) {
						if(tabIdx){
							pFilter['tab'] = tabIdx;
							serachTabIdx = tabIdx;
						}
						return true;
					},
					postSearchHandler: function(pObj) {
						tabData[tabIdx]=[];
						$.each(pObj,function(pIdx,pValue){
							tabData[pValue.tab].push(pValue);
						});
						showData();
						return false;
					},
					postModifyHandler: function(pData) {
						mainForm.enableDisableField(['circularNo', 'date' ], false, false);
						return true;
					}
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
			crudCircular$ = $('#contCircular').xcrudwrapper(lConfig);
			crudCircular = crudCircular$.data('xcrudwrapper');
			mainForm = crudCircular.options.mainForm;
			
			$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
			    var lRef1 = $(event.target).attr('href');         // active tab
			    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
			    tabIdx = parseInt(lRef1.substring(4));
			    if (lRef2)
			    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
			    $('.btn-group'+tabIdx).removeClass('hidden');
			    showData();
			});
			$('#contCircular li:eq(0) a').tab('show');
		});
		 
		function downloadFile(pFileName){
			location.href="upload/CIRCULARS/"+pFileName;
		}
		
		function showData() {
			if(tabData==null){
				tabData = [];
				for (var lPtr=0;lPtr<templates.length;lPtr++)
					tabData[lPtr]=[];
			}
			for (var lPtr=1;lPtr<templates.length;lPtr++) {
				templates[lPtr].setData(null);
				templates[lPtr].setData(tabData[lPtr]);
				var lCount = tabData[lPtr]?tabData[lPtr].length:0;
				$('#badge'+lPtr).html(lCount<10?"0"+lCount:lCount);
			}
		}
		
		function archiveData(pArchiveFlag,pTab){
			var lDataKey = [] ;
			var lKey ;
			$(".chkbox"+pTab+":checked").each(function() {  
	   	 		lKey = $(this).attr('data-id');
		   	 	if(!lDataKey.includes(lKey)){
		   	 		lDataKey.push(lKey);
				}
	   		});
			if(lDataKey.length>0){
				var lUrl= 'circulars/archive' ;
		   		var lData = {archiveFlag:pArchiveFlag,ids:lDataKey};
				$.ajax({
		        url: lUrl,
		        type: 'POST',
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) { 
		        },
		    	error: errorHandler
		    	});
			}else{
				alert('Please select atleast circular.');
			}
			
		}
		function removeCircular(pId,pTab){
			$.each(tabData[pTab],function(pIdx,pValue){
				if (pValue.id==pId){
					crudCircular.removeHandler(null,pValue,false);
					return;
				}
			});
		}
	</script>


</body>
</html>