<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.CompanyKYCDocumentBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyContactBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lEntityId = request.getParameter("entityId"); 
if(lEntityId==null) lEntityId="0";
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<html>
	<head>
		<title>TREDS | Registration</title>
		<%@include file="includes1.jsp" %>
	</head>
	<body class="page-body">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Enclosures/Uploads" />
	</jsp:include>

	<div class="content" id="contKYCDocument">
		<!-- frmSearch -->
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Enclosures/Uploads</h1>
				</div>
			</div>
		<div class="xform">
			<div  id="frmSearch">
			</div>
		</div>
		<!-- frmSearch -->

		<!-- frmMain -->
		<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content">
		<div class="modal-header">Upload Document</div>
		<div class="modal-body">
		<div class="xform" id="frmMain">
			<div class="box-body">
				<fieldset>
					<div class="row">
						<div class="col-sm-4"><section><label for="document" class="label">Document:</label></section></div>
						<div class="col-sm-8">
							<section class="select">
							<select id="document"><option value="">Select Document</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4"><section><label for="fileName" class="label">Document File:</label></section></div>
						<div class="col-sm-8">
							<input type="hidden" id="fileName" data-role="xuploadfield" data-file-type="KYCDOCUMENTS" />
							<div>
								<button type="button" class="upl-btn-upload btn btn-lg btn-success"><span class="fa fa-upload"></span> Upload</button>
								<button type="button" class="upl-btn-clear btn btn-lg btn-default"><span class="fa fa-remove"></span> Clear</button>
								<span class="upl-info"></span>
								<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
							</div>
							<div style="width:50%">
								<img class="upl-img-preview" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4"><section><label for="remarks" class="label">Remarks:</label></section></div>
						<div class="col-sm-8">
							<section class="input">
							<input type="text" id="remarks" placeholder="Remarks">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
			<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<input type="hidden" id="documentType">
							<input type="hidden" id="documentCat">
							<input type="hidden" id="docForCCId">							
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
			</div>
				</fieldset>
			</div>
		</div>
		</div></div></div></div>
		<!-- frmMain -->		
	</div>

   	<%@include file="footer1.jsp" %>
   	<script id="tplDocs" type="text/x-handlebars-template">
		{{#if creatorEntity}}
		<center><h1><a href='{{documentsUrl}}' target="_blank">Click Here !</a></h1></center>
		{{else}}
		<div class="text-right">* Mandatory Documents</div>
		<div>
		<div class="cloudTabs">
		<ul class="cloudtabs nav nav-tabs">
		{{#each this}}
			<li><a href="#tab{{docCat}}" data-toggle="tab">{{docCatDesc}}</a></li>
		{{/each}}
		</ul>
		</div>
		</div>

			<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">


		<div class="tab-content no-padding">
		{{#each this}}
		<div id="tab{{docCat}}" class="tab-pane">
		<table class="table table-striped table-bordered"><tbody>
		<tr>
			<th style="width:5%">Sr No</th>
			<th style="width:20%">Document Type</th>
			<th style="width:20%">Document</th>
			<th style="width:20%">File Name</th>
			<th style="width:20%">Remarks</th>
			<th style="width:15%"></th>
		</tr>
		{{#each list}}
		{{#each data}}
		<tr>
			<td>{{#if @index}}{{else}}{{../../srNo}}{{/if}}</td>
			<td>{{../documentTypeDesc}} {{#if mand}}*{{/if}}</td>
			<td>{{documentDesc}} {{#if documentFor}}<br>({{documentFor}}){{/if}}</td>
			<td>{{#if fileName}}<a href="javascript:downloadFile('{{fileName}}')" title="Download File" style="color:blue !important">{{fileName}}</a>{{/if}}</td>
			<td>{{remarks}}</td>
			<td>
				{{#if ../soft}}
				{{#if fileName}}<a href="javascript:;" class="right_links" onClick="javascript:modifyHandler('{{../../../idx}}',{{../id}},{{../isProvisional}})"><i class="fa fa-pencil lg">Modify</i> </a>
				<a href="javascript:;" class="right_links" onClick="javascript:crudKYCDocument.removeHandler(null,[[{{../id}}]])"><i class="fa fa-remove"></i> Remove</a> <a></a>{{else}}
				
				<button class="btn btn-info btn-lg" onClick="javascript:newHandler('{{../../../idx}}')"><i class="fa fa-upload"></i> Upload</button>{{/if}}
				{{else}}
				Only Hard copy required.
				{{/if}}
			</td>
		</tr>
		{{/each}}
		{{/each}}
		</tbody></table>
		</div>
		{{/each}}
		</div>

		</div>
		</div>
		</fieldset>
		</div>
		{{/if}}
	</script>
	<script type="text/javascript">
	var tplDocs, mainForm;
	var crudKYCDocument$ = null;
	var crudKYCDocument = null;
	var data,tabData,tabIdx,myContacts,dataNew,metaMulti,metaNew,metaOG,myIndiv,myEntity,myEntityKmp,myLoc;
	$(document).ready(function() {
	    $("#testDocument").click(function () {
	        $("#thedialog").attr('src', $(this).attr("href"));
	        $("#somediv").dialog({
	            width: 400,
	            height: 450,
	            modal: true,
	            close: function () {
	                $("#thedialog").attr('src', "about:blank");
	            }
	        });
	        return false;
	    });
		tplDocs = Handlebars.compile($('#tplDocs').html());
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyKYCDocumentBean.class).getJsonConfig()%>;
		var cdId = <%=lEntityId%>;
		var lConfig = {
				resource: "companykycdocument",
				autoRefresh: true,
				preSearchHandler: function(pFilter) {
					pFilter.cdId=cdId;
					pFilter.isProvisional=<%=lIsProv%>;
					return true;
				},
				postSearchHandler: function(pObj) {
			    	data = pObj.meta;
			    	metaOG = pObj.meta;
			    	update = pObj.update;
			    	myContacts=pObj.catContacts;
			    	myEntity = pObj.catEntity;
			    	myEntityKmp = pObj.catEntityKmp;
			    	myIndiv = pObj.catIndiv;
			    	myBank = pObj.catBank;
			    	myLoc = pObj.catLoc
			    	tabData = [];
			    	var lTabMap = {};
			    	dataNew = {};
			    	var lContacts;
			    	var lEntities;
			    	var lIndividuals;
			    	var lKmpEntities;
			    	var lBanks;
			    	var lLocs;
			    	metaMulti=[];
			    	metaNew=[];
			    	var lRepeatTypes=[];
			    	var lNoData = pObj.creatorIdentity;
			    	if (lNoData=='J'){
			    		var lData = {"creatorEntity":"J"
			    				,"documentsUrl":pObj.documentsUrl};
			    		$('#frmSearch').html(tplDocs(lData));
			    		return;
			    	}
			    	//duplicate as per contacts
			    	for(var i=metaOG.length-1; i>=0; i--){
						lMeta = metaOG[i];
						if(lMeta.repeatDocType!=""){
							metaMulti.push(lMeta);
							metaOG.splice(i,1);
						}else{
							//for multi documents also the key for finding the meta place will be the first item in the list
			    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value;
			    			lMeta.idx = lKey;
			    			//find data convert and attach
				    		var lTmpData = pObj.data[lKey]; //this is hash 
			    			var lData = {documentType:lMeta.documentType};
				    		if(lTmpData){
				    			lData = lTmpData[0];
				    		}else{
			    				lData.document = lMeta.document;
			    				lData.documentCat = lMeta.docCat;
			    				lData.documentType = lMeta.documentType;
			    				lData.documentDesc = lMeta.documentList[0].text;
			    				lUpdateItems=update[lData.document];
				    		}
		    				lData.docCatDesc = lMeta.docCatDesc;
		    				lData.documentList = lMeta.documentList;
				    		if(!lMeta.data) lMeta.data =[];
				    		lMeta.data.push(lData);
		    				dataNew[lKey]= lData;
							metaNew.push(lMeta);
						}
			    	}
			    	$.each(myContacts, function(pKey, pContacts) {
						 lContacts = myContacts[pKey];
						 if(lContacts!=null){
				    		for (var lPtr=0; lPtr<lContacts.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lContacts[lPtr].ccDisp; 
						    			lMeta.docForCCId = lContacts[lPtr].ccId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lContacts[lPtr].ccId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lContacts[lPtr].ccDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	$.each(myEntity, function(pKey, pEntity) {
			    		lEntities = myEntity[pKey];
						 if(lEntities!=null){
				    		for (var lPtr=0; lPtr<lEntities.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lEntities[lPtr].cseDisp; 
						    			lMeta.docForCCId = lEntities[lPtr].cseId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lEntities[lPtr].cseId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lEntities[lPtr].cseDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	$.each(myIndiv, function(pKey, pIndiv) {
			    		lIndividuals = myIndiv[pKey];
						 if(lIndividuals!=null){
				    		for (var lPtr=0; lPtr<lIndividuals.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lIndividuals[lPtr].csiDisp; 
						    			lMeta.docForCCId = lIndividuals[lPtr].csiId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lIndividuals[lPtr].csiId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lIndividuals[lPtr].csiDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	$.each(myEntityKmp, function(pKey, pEntity) {
			    		lKmpEntities = myEntityKmp[pKey];
						 if(lKmpEntities!=null){
				    		for (var lPtr=0; lPtr<lKmpEntities.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lKmpEntities[lPtr].cseDisp; 
						    			lMeta.docForCCId = lKmpEntities[lPtr].cseId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lKmpEntities[lPtr].cseId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lKmpEntities[lPtr].cseDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	$.each(myBank, function(pKey, pBank) {
			    		lBank = myBank[pKey];
						 if(lBank!=null){
				    		for (var lPtr=0; lPtr<lBank.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lBank[lPtr].csbDisp; 
						    			lMeta.docForCCId = lBank[lPtr].csbId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lBank[lPtr].csbId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lBank[lPtr].csbDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	
			    	$.each(myLoc, function(pKey, pLoc) {
			    		lLocs = myLoc[pKey];
						 if(lLocs!=null){
				    		for (var lPtr=0; lPtr<lLocs.length;lPtr++) {
						    	for(var i=metaMulti.length-1; i>=0; i--){
						    		if(pKey==metaMulti[i].repeatDocType){
						    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
						    			lMeta.documentFor = lLocs[lPtr].cslDisp; 
						    			lMeta.docForCCId = lLocs[lPtr].cslId;

						    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value+ "^" + lLocs[lPtr].cslId;
						    			lMeta.idx = lKey;
						    			//find data convert and attach
							    		var lTmpData = pObj.data[lKey]; //this is hash 
						    			var lData = {documentType:lMeta.documentType};
							    		if(lTmpData){
							    			lData = lTmpData[0];
							    		}else{
						    				lData.document = lMeta.document;
						    				lData.documentCat = lMeta.docCat;
						    				lData.documentType = lMeta.documentType;
						    				lData.documentDesc = lMeta.documentList[0].text;
						    				lData.documentFor = lMeta.documentFor;
						    				lUpdateItems=update[lData.document];
							    		}
						    			lData.documentFor = lLocs[lPtr].cslDisp; 
					    				lData.docCatDesc = lMeta.docCatDesc;
					    				lData.docForCCId = lMeta.docForCCId; 
					    				lData.documentList = lMeta.documentList;
							    		if(!lMeta.data) lMeta.data =[];
							    		lMeta.data.push(lData);
					    				dataNew[lKey]= lData;
										metaNew.push(lMeta);
									}
						    	}
					    	}
			    		}
			    	});
			    	pObj.data = dataNew;
			    	$.each(metaNew, function(pIndex, pMeta) {
			    		var lTabList=lTabMap[pMeta.docCat];
			    		if (lTabList==null) {
			    			lTabList=[];
			    			tabData.push({docCat:pMeta.docCat,docCatDesc:pMeta.docCatDesc, list:lTabList});
			    			lTabMap[pMeta.docCat]=lTabList;
			    		}
			    		lTabList.push(pMeta);
			    		pMeta.srNo = lTabList.length;
			    	});

			    	$('#frmSearch').html(tplDocs(tabData));
			    	if (tabIdx) $('.nav-tabs a[href="'+tabIdx+'"]').tab('show');
			    	else $('.nav-tabs a:first').tab('show');

			    	$('.nav-tabs a').on('shown.bs.tab', function(event){
					    tabIdx = $(event.target).attr('href');         // active tab
					});
					
			    	return false;
				},
				preSaveHandler: function(pObj) {
					pObj.cdId=<%=lEntityId%>;
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudKYCDocument$ = $('#contKYCDocument').xcrudwrapper(lConfig);
		crudKYCDocument = crudKYCDocument$.data('xcrudwrapper');
		mainForm = crudKYCDocument.options.mainForm;
	});
	function newHandler(pIdx) {
		populateDocumentList(pIdx);
		crudKYCDocument.newHandler();
		mainForm.getField('documentType').setValue(dataNew[pIdx].documentType);
		mainForm.getField('documentCat').setValue(dataNew[pIdx].documentCat);
		mainForm.getField('docForCCId').setValue(dataNew[pIdx].docForCCId);
		var lField = mainForm.getField('document');
		if (lField.getOptions().dataSetValues.length==1)
			lField.setValue(lField.getOptions().dataSetValues[0].value);
	}
	function modifyHandler(pIdx, pId,pIsProvisional) {
		populateDocumentList(pIdx);
		crudKYCDocument.modifyHandler(null,[pId,pIsProvisional],false);
	}
	function populateDocumentList(pIdx) {
		var lField = mainForm.getField('document');
		var lOptions=lField.getOptions();
		lOptions.dataSetValues = dataNew[pIdx].documentList;
		lField.init();
		
	}
	function downloadFile(pFileName){
		location.href="upload/KYCDOCUMENTS/"+pFileName;
	}
	
	function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudKYCDocument.options.fieldGroups.update;
     	for(var lPtr=0; lPtr < lFieldGroup.length;  lPtr++){
     		$('#'+lFieldGroup[lPtr]).css('border-color','');
     	}
     	//mark modified fields red
     	if(pModifiedJson!=null&&pModifiedJson!=''){
     		var lData = pModifiedJson;
     		if(lData!=null){
     			for(var key in lData){
     				//$('#ov-'+key).html(lData[key]);
     				$('#'+key).css('border-color','red');
     			}
     		}
     	}
     }
	</script>
	</body>
</html>