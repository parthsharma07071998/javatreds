
<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.other.bean.BuyerCreditRatingBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
	<head>
		<title>Buyer Rating</title> 
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet"/>
		<link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Buyer Rating" />
		<jsp:param name="desc" value="" />
	</jsp:include>
	
	<div class="content" id="contBuyerCreditRating" >		
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Buyer Rating</h1>
			</div>
		</div>
		<div  id="frmSearch">
		<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
			<!-- <header>null</header> -->
			
			<fieldset class="form-horizontal">
				<div class="row">
					<div class="col-sm-2"><section><label for="buyerCode" class="label">Buyer Code:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="buyerCode"><option value="">Select Buyer Code</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="filterRatingAgencyList" class="label">Rating Agency:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="filterRatingAgencyList" multiple="multiple" data-role="multiselect"><option value="">Select Rating Agency</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="filterRatingList" class="label">Rating:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="filterRatingList" multiple="multiple" data-role="multiselect"><option value="">Select Rating</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="ratingType" class="label">Rating Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="ratingType"><option value="">Select Rating Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="pan" class="label">Pan:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="pan" placeholder="pan">
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
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnNew><span class="glyphicon glyphicon-plus-sign"></span> New </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnInstructions><span class="glyphicon glyphicon-upload"></span> Upload </a>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-view" id=btnDownloadCSV><span class="glyphicon glyphicon-download-alt"></span> Download </a>
							<span class='state-T'><a class="secure" href="javascript:;" data-seckey="buyercreditrating-view" id=btnView><span class="glyphicon glyphicon-eye-open"></span> View </a></span>
							<a class="secure" href="javascript:;" data-seckey="buyercreditrating-manage" id=btnRemove><span class="glyphicon glyphicon-trash"></span> Remove </a>
							</span>
						</div>
				</div>
			<div class="tab-pane panel panel-default">
			<fieldset>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-bordered table-condensed" id="tblData" data-scroll-y="300px">
							<thead><tr>
								<th data-visible="false" data-width="100px" data-name="id">Buyer Credit Rating Id</th>
								<th data-width="100px" data-name="buyerCode">Buyer Code</th>
								<th data-width="200px" data-name="purName">Buyer</th>
								<th data-width="200px" data-name="ratingAgency">Rating Agency</th>
								<th data-width="100px" data-name="ratingType">Rating Type</th>
								<th data-width="100px" data-name="rating">Rating</th>
								<th data-width="100px" data-name="status">Status</th>
								<th data-width="100px" data-name="ratingDate">Rating Date</th>
								<th data-width="100px" data-name="expiryDate">Expiry Date</th>
								<th data-width="100px" data-name="remarks">Remarks</th>
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
	    	<div>
				<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="buyerCode" class="label">Buyer Code:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="buyerCode"><option value="">Select Buyer Code</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="ratingAgency" class="label">Rating Agency:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="ratingAgency"  onchange="changeRating()"><option value="">Select Rating Agency</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="ratingType" class="label">Rating Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="ratingType"><option value="">Select Rating Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
					<div class="col-sm-2"><section><label for="rating" class="label">Rating:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="rating"><option value="">Select Rating</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="ratingDate" class="label">Rating Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="ratingDate" placeholder="Rating Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="remarks" class="label">Remarks:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="remarks" placeholder="Remarks">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="expiryDays" class="label">Expiry Days:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="expiryDays" placeholder="Expiry Days">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<input type="hidden" id="ratingFile" data-role="xuploadfield" data-file-type="BUYERCREDITRATINGS" />
					<div class="col-sm-2"><section><label for="ratingFile" class="label">Rating File:</label></section></div>
					<div class="col-sm-4">
						<section>
							<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
							<span class="upl-info"></span>
							<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
						</section>
					</div>
				</div>
			<footer>
				<div class="btn-group pull-right">
						<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>						
						<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
			</fieldset>
		</div>
   		</div>
 		</div>
	</div>
	
			<div class="modal fade" tabindex=-1 id="mdlInstruction"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
        <div class="modal-header"><span>&nbsp;IMPORTANT INSTRUCTIONS</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body" id="tableInstructions" height='200px'>
	
        </div>
		</div></div></div>

	<%@ include file="footer1.jsp" %>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
    <script src="../js/bootstrap-multiselect.js"></script>
	<script id="tplInstructions" type="text/x-handlebars-template">
	<table border=0 borcellspacing=1 cellpadding=2 width=100%>
    <tr><td class=frmfld>
            <ul>
            <li>File name can be anything.</li>
            <li>File should contain comma separated utilization information</li>
            <li>First record in the file should be the header record. (see example below)</li>
            <li>Subsequent records in the file should contain following field values separated by commas</li>
            </ul>       
             <table border=1 class=frmfld width=100%>
                    <tr align=center>
                    <th>Headers</th>
					<th>Mandatory</th>
                    <th>Description</th>
                    <th>Value Range</th>
                    <th>Default Value</th>
                    </tr>
                        
                    <tr>
                        <td>buyerCode</td>
                        <td>Yes</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                        
                    <tr>
                        <td>ratingAgency</td>
                        <td>Yes</td>
                        <td></td>
                        <td style="padding-left:10px;">
                            <table>
							<tr><th>Key</th><th>Value</th></tr>
							{{#each ratingAgency}}
								<tr><td width='100px'>{{text}}</td>
								<td>{{value}}</td></tr>
							{{/each}}
            				</table>
                        </td>
                        <td></td>
                    </tr>
                        
                    <tr>
                        <td>ratingDate</td>
                        <td>Yes</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    
                    <tr>
                        <td>rating</td>
                        <td>Yes</td>
                        <td></td>
                        <td style="padding-left:10px;">
                            <table>
							<tr><th>Key</th><th>Value</th></tr>
							{{#each rating}}
								<tr><td width='100px'>{{text}}</td>
								<td>{{value}}</td></tr>
							{{/each}}
            				</table>
                        </td>
                        <td></td>
                    </tr>
                    
                    <tr>
                        <td>ratingType</td>
                        <td>Yes</td>
                        <td></td>
                        <td style="padding-left:10px;">
						   <table>
							<tr><th>Key</th><th>Value</th></tr>
							{{#each ratingType}}
								<tr><td width='100px'>{{text}}</td>
								<td>{{value}}</td></tr>
							{{/each}}
            				</table>
						</td>
                        <td></td>
                    </tr>

                    <tr>
                        <td>ratingFile</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td>remarks</td>
                        <td>Yes</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    </table>
                    <br>
    </td></tr>
    <tr><td>SAMPLE FILE CONTENTS
    </td></tr>
    <tr><td class=frmfld >
    <pre>
buyerCode|ratingAgency|ratingDate|rating|ratingType|expiryDays|ratingFile|remarks
XP0000018|{{#each ratingAgency}}{{#if @first}}{{value}}{{/if}}{{/each}}|01-01-2019|{{#each rating}}{{#if @first}}{{value}}{{/if}}{{/each}}|{{#each ratingType}}{{#if @first}}{{value}}{{/if}}{{/each}}|365|ratingFile.pdf|remarks
    </pre>
    </td></tr>
    </table>
<div class="box-footer" align="center">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpload onclick="uploadCSVFile()"><span class="fa fa-upload"></span> Continue</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
	</script>
	<script type="text/javascript">
		var crudBuyerCreditRating$ = null;
		var mainForm;
		var searchForm;
		var tplInstructions;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(BuyerCreditRatingBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "buyercreditrating",
					autoRefresh: true,
					postNewHandler:function(pObj){
						mainForm.enableDisableField('expiryDays',true,false);
			    		mainForm.getField("expiryDays").setValue('365');
			    		return true;
					},
					postModifyHandler:function(pObj){
						var lField=mainForm.getField("rating");
			    		populateRatings(mainForm,lField);
			    		mainForm.getField("rating").setValue(pObj.rating);
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
%>			
			};
			tplInstructions=Handlebars.compile($('#tplInstructions').html());
			lConfig = $.extend(lConfig, lFormConfig);
			crudBuyerCreditRating$ = $('#contBuyerCreditRating').xcrudwrapper(lConfig);
			crudBuyerCreditRating = crudBuyerCreditRating$.data('xcrudwrapper');
			mainForm = crudBuyerCreditRating.options.mainForm;
			searchForm = crudBuyerCreditRating.options.searchForm;
			
	    	$('#btnInstructions').on('click', function() {
	    		var lMap = {};
	    		lMap["rating"] = mainForm.getField('rating').options.dataSetValues;
	    		lMap["ratingAgency"] = mainForm.getField('ratingAgency').options.dataSetValues;
	    		lMap["ratingType"] = mainForm.getField('ratingType').options.dataSetValues;
	    		$('#tableInstructions').html(tplInstructions(lMap));
	     		showModal($('#mdlInstruction'));
	     	});
	    	$('#btnInstructions').on('click', function() {
	    		var lMap = {};
	    		lMap["rating"] = mainForm.getField('rating').options.dataSetValues;
	    		lMap["ratingAgency"] = mainForm.getField('ratingAgency').options.dataSetValues;
	    		lMap["ratingType"] = mainForm.getField('ratingType').options.dataSetValues;
	    		$('#tableInstructions').html(tplInstructions(lMap));
	     		showModal($('#mdlInstruction'));
	     	});
	    	$('#frmSearch #ratingType').on('change', function() {
	    		var lField=searchForm.getField("filterRatingList");
	    		populateRatings(searchForm,lField);
	     	});
	    	$('#frmMain #ratingType').on('change', function() {
	    		var lField=mainForm.getField("rating");
	    		populateRatings(mainForm,lField);
	    		var lRatingAgency = mainForm.getField('ratingAgency').getValue();
	    		var lRatingType = mainForm.getField('ratingType').getValue();
	    		changeRating(lRatingAgency,lRatingType);
	     	});
	    	
	    	$('#frmMain #ratingAgency').on('change', function() {
	    		var lField=mainForm.getField("rating");
	    		populateRatings(mainForm,lField);
	    		var lRatingAgency = mainForm.getField('ratingAgency').getValue();
	    		var lRatingType = mainForm.getField('ratingType').getValue();
	    		changeRating(lRatingAgency,lRatingType);
	     	});
	    	
	    	$('#btnDownloadCSV').on('click', function(){
				var lData=crudBuyerCreditRating.options.searchForm.getValue(true);
				lData['columnNames'] = crudBuyerCreditRating.getVisibleColumns();
				if (lData!=null){
					downloadFile('buyercreditrating/download/csv',null,JSON.stringify(lData));
				}
			});
		});
		
		
		
		function uploadCSVFile() {
     		$('#mdlInstruction').modal('hide');
     		showRemote('buyercreditrating/buyerratingupload', null, false);
     	};
    	
    	function populateRatings(pForm,pField){
    		var lOptions=pField.getOptions();
    		lOptions.dataSetType = "RESOURCE";
    		lOptions.dataSetValues = "buyercreditrating/ratinglov?ratingType="+pForm.getField('ratingType').getValue();
    		pField.init();
    	}
    	
    	function changeRating(pRatingAgency, pRatingType){
    		if(pRatingAgency != null && pRatingType != null){
	    		if(pRatingAgency == 'RC0'){
	    			mainForm.getField('rating').setValue(pRatingType+'.NR');
	    			mainForm.enableDisableField('rating',false,false);
	    		}
    		}else{
    			mainForm.enableDisableField('rating',true,false);
    		}
    	}

    	</script>


</body>
</html>