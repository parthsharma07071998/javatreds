<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = StringEscapeUtils.escapeHtml(request.getParameter("id"));
String lFinal = StringEscapeUtils.escapeHtml(request.getParameter("final"));
String lUrl = "companyview?id="+lId;
if (lFinal != null) lUrl += "&final="+lFinal;
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
        <style>
        .xform section {
        	margin:0px;
        }
        .xform .label, .xform section.view {
        	padding-top: 0px;
        	padding-bottom: 0px;
        	height:20px;
        }
        </style>
    <div>
	<div class="content" id="frmContent">
    	<!-- frmMain -->
	    <div class="xform view">
	    	<fieldset>
			<div id="divData">
			</div>
    		<div class="modal-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
    		</div>
    		</fieldset>
    	</div>
    	<!-- frmMain -->
	</div>
	</div>

  	<div class="modal fade" tabindex=-1 id="mdl-image"><div class="modal-dialog modal-xl"><div class="modal-content"><div class="modal-body"><div class="container">
	<div class="xform box">
   		<div class="modal-header">
			<span></span>
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-close" data-dismiss="modal"><span class="fa fa-close"></span></button>
					</div>
				</div>
			</div>
   		</div>
    	<div class="box-body text-center">
    	</div>
    		<div class="box-footer">
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
					</div>
				</div>
			</div>
    		</div>
    </div>
</div></div></div></div></div>

	<div id="imageDiv" style="display:none">
		<div id="imageBox">
		</div>
		<div class="modal-footer">
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" id="btnImageClose" onClick="javascript:closeImage()"><span class="fa fa-close"></span> Close</button>
					</div>
				</div>
			</div>
   		</div>
	</div>


   	<script id="tplKYCDocumentView" type="text/x-handlebars-template">
				<h3>Enclosures/Uploads</h3>
				<div class="row">
					{{#ifCond creatorIdentity '==' 'J'}}
					<center><h1><a href='{{documentsUrl}}' target="_blank">Click Here !</a></h1></center>
					{{/ifCond}}
					{{#ifCond creatorIdentity '!=' 'J'}}
					<div class="col-sm-12">
						<table class="table table-striped table-bordered" style="width:100%"><tbody>
						<tr>
							<th style="width:4%">Sr No</th>
							<th style="width:20%">Document Type</th>
							<th style="width:20%">Document</th>
							<th style="width:20%">File Name</th>
							<th style="width:20%">Remarks</th>
						</tr>
						{{#each kycDocuments.meta}}
						{{#each data}}
						<tr>
							<td>{{#if @index}}{{else}}{{../../srNo}}{{/if}}</td>
							<td>{{../documentTypeDesc}} {{#if mand}}*{{/if}}</td>
							<td>{{documentDesc}}</td>
							<td>{{#if fileName}}{{fileName}}<button class="btn btn-link" onClick="javascript:showImage('upload/KYCDOCUMENTS/{{fileName}}')">View</button>{{/if}}</td>
							<td>{{remarks}}</td>
						</tr>
						{{/each}}
						{{/each}}
						</tbody></table>
					</div>
					{{/ifCond}}
				</div>
	</script>

	<script type="text/javascript">
	var metaOG, myContacts, dataNew, metaMulti, metaNew, data;
	var tplKYCDocumentView;
	$(document).ready(function() {
		Handlebars.registerHelper('imageName', function(options) {
			var lImg = options.fn(this);
			var lPos=lImg.indexOf('.');
			return lPos>0?lImg.substring(lPos+1):lImg;
		});
		tplKYCDocumentView = Handlebars.compile($('#tplKYCDocumentView').html());
		$.ajax({
			url: '<%=lUrl%>',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
		    	metaOG = pObj.kycDocuments.meta;
		    	myContacts=pObj.kycDocuments.catContacts;
		    	data = pObj.kycDocuments.data;
		    	dataNew = {};

		    	var lContacts;
		    	metaMulti=[];
		    	metaNew=[];
		    	var lRepeatTypes=[];
		    	//duplicate as per contacts
		    	for(var i=metaOG.length-1; i>=0; i--){
					lMeta = metaOG[i];
					if(lMeta.repeatDocType){
						metaMulti.push(lMeta);
						metaOG.splice(i,1);
					}else{
		    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value;
		    			lMeta.idx = lKey;
		    			//find data convert and attach
			    		var lTmpData = data[lKey]; //this is hash 
		    			var lData = {documentType:lMeta.documentType};
			    		if(lTmpData){
			    			lData = lTmpData[0];
				    	}else{
		    				lData.document = lMeta.document;
		    				lData.documentCat = lMeta.docCat;
		    				lData.documentType = lMeta.documentType;
		    				lData.documentDesc = lMeta.documentList[0].text;
			    		}
	    				lData.docCatDesc = lMeta.docCatDesc;
	    				lData.documentFor = lMeta.documentFor;
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
					    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value + "^" + lContacts[lPtr].ccId;
					    			lMeta.idx = lKey;
					    			//find data convert and attach
						    		var lTmpData = data[lKey]; //this is hash 
					    			var lData = {documentType:lMeta.documentType};
						    		if(lTmpData){
						    			lData = lTmpData[0];
						    		}else{
					    				lData.document = lMeta.document;
					    				lData.documentCat = lMeta.docCat;
					    				lData.documentType = lMeta.documentType;
					    				lData.documentDesc = lMeta.documentList[0].text;
						    		}
				    				lData.docCatDesc = lMeta.docCatDesc;
				    				lData.documentFor = lMeta.documentFor;
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
				
		    	pObj.kycDocuments.meta = metaNew;
		    	pObj.kycDocuments.data = dataNew;
		    	
		    	metaNew.sort(SortByName);
		    	
		    	var lSrNo =0;
		    	$.each(metaNew, function(pKey, pMeta) {
		    		lSrNo += 1;
		    		pMeta.srNo = lSrNo;		    		
		    	});
 				$('#divData').html(tplKYCDocumentView(pObj));
			},
			error: errorHandler,
		
		});
	});
	function showImage(pImage) {
		if(pImage.toLowerCase().endsWith(".pdf")){
			window.open(pImage);
		}else{
			dispImage(true);
			document.getElementById("imageBox").innerHTML = '<center><img src="'+pImage+'" style="max-width:100%" /></center>';
		}
	}
	function closeImage(){
		dispImage(false);
	}
	function dispImage(pShowImage){
		document.getElementById("imageDiv").style.display = pShowImage?"":"none";
		document.getElementById("frmContent").style.display = pShowImage?"none":"";
		document.getElementById("mdl-image").style.display = pShowImage?"none":"";
	}
	function SortByName(a, b){
		  var aName = a.docCatDesc.toLowerCase();
		  var bName = b.docCatDesc.toLowerCase(); 
		  var aType = (a.documentFor?a.documentFor.toLowerCase():"");
		  var bType = (b.documentFor?b.documentFor.toLowerCase():""); 
		  var lRetVal = ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
		  if(lRetVal>=0 && aType!="" && bType!=""){
			  lRetVal = (aType > bType) ? 1 : lRetVal;
		  }
		  return lRetVal;
	}
	</script>
