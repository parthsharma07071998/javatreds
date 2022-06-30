<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.treds.auction.bo.PurchaserSupplierCapRateBO"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierCapRateBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
String lCounterEntity = (String)request.getParameter("counterEntity");
if(lCounterEntity==null) lCounterEntity="";
%>
<html lang="en">


<head>
	<title id="myTitle">TREDS | Buyer Seller Cap Rate.[<%=lCounterEntity%>]</title>
       <%@include file="includes1.jsp" %>
       <link href="../css/datatables.css" rel="stylesheet"/>
       <style>
       #divMatrix td {
       	padding: 0 8px;
       }
       #divMatrix td.headUsance {
       	background-color:rgb(0,128,128);
       	color:white;
       	text-align:center;
       }
       #divMatrix td.headUsanceLt {
       	background-color:rgba(0,128,128,0.3);
       	width:130px;
       }
       
       #divMatrix td.headRetention {
       	background-color:rgb(0,11,224);
       	color:white;
       	text-align:center;
       	width:130px;
       }
       #divMatrix td.headRetentionLt {
       	background-color:rgba(0,11,224,0.3);
       }
       #divMatrix td.cellHaircut {
       	background-color:#eee;
       }
       </style>
</head>
<body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Buyer Seller Cap Rate."/>
    	<jsp:param name="desc" value="" />
    </jsp:include>
	<div class="page-title">
		<div class="title-env">
			<h1 class="title" id="pageTitle">Buyer Seller Cap Rate. [<%=lCounterEntity%>]</h1>
		</div>
	</div>
    <div class="content">
		<div class="box">
			<div class="row">
				<div class="col-sm-12">
				<div class="col-sm-4"></div>
					<div class="box-title btn-groupX pull-right">
						<button type="button" class="btn btn-danger btn-lg" onClick="javascript:removeRowCol()"><span class="fa fa-minus"></span> Remove</button>
					</div>
				</div>
			</div>
			<div  class="box-body">
				<div id="divMatrix">
	
				</div>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter secure" data-seckey="caprate-save" onClick="javascript:saveMatrix()" id="btnSave"><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" onClick="javascript:closePage()"><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
	    		</div>
			</div>
		</div>
	</div>
	
	

   	<%@include file="footer1.jsp" %>

	<script type="text/javascript">	
		var dataModel,crudMatrix,mainFormMatrix;
		$(document).ready(function() {
			getData();
			
		});
		function getData() {
			$.ajax({
	            url: 'caprate?counterEntity='+'<%=lCounterEntity%>',
	            type: 'GET',
	            success: function( pObj, pStatus, pXhr) {
	    			dataModel=pObj;
	    			if(dataModel==null)
    				{
	    				dataModel = {};
	    				dataModel['<%=PurchaserSupplierCapRateBO.CAPRATEDATA_COUNTER_ENTITY%>'] = '<%=lCounterEntity%>';
	    				dataModel['<%=PurchaserSupplierCapRateBO.CAPRATEDATA_COLUMNS%>'] = [];
	    				dataModel['<%=PurchaserSupplierCapRateBO.CAPRATEDATA_ROWS%>'] = [];
	    				dataModel['<%=PurchaserSupplierCapRateBO.CAPRATEDATA_DATA_CAPRATES%>'] = [];
    				}
	    			viewFromDataModel();
	            },
	        	error: errorHandler
	        });
		}
		function viewFromDataModel() {
			var lHtml = '<div id="contMatrix"><div id="frmMain" class="xform"><table cellpadding="5" cellspacing="5">';
			var lFields = [];
			var lFormData = {};
			var lColRow1='',lColRow3='';
			var lName;
			$.each(dataModel.cols,function(pColIdx, pColVal){
				lName='fromUsance'+pColIdx;
		    	lColRow1+='<td class="headUsanceLt"><input type="hidden" id="'+lName+'">';
		    	lFields.push({name:lName,dataType:'INTEGER',notNull:true,label:'From Tenor'});
				lFormData[lName]=pColVal.fromUsance;
				
		    	lName='toUsance'+pColIdx;
		    	lColRow1+='<table border="0"><tr><td style="width:30px;text-align:center"><input type="checkbox" class="chkCol" value="'+pColIdx+'" id="col'+pColIdx+'"</td><td>';
		    	lColRow1+='<section class="input"><input type="text" id="'+lName+'"><b class="tooltip tooltip-right"></b></section>'
		    	lColRow1+='</td></tr></table></td>';
		    	lFields.push({name:lName,dataType:'INTEGER',notNull:true,label:'Tenor Upto'});
				lFormData[lName]=pColVal.toUsance;
				
		    	lColRow3+='<td></td>';
			});
			lHtml+='<tr><td class="headRetention" rowspan="2"><h3>Retention Margin(%)</h3></td>';
			lHtml+='<td class="headUsance" colspan="'+dataModel.cols.length+'"><h3>Tenor (No. of days)</h3></td>';
			lHtml+='<td><button type="button" class="btn btn-success btn-lg" onClick="javascript:addCol()"><span class="fa fa-plus"></span> Add</button></td></tr>';
			lHtml+='<tr>'+lColRow1+'</tr>';
			$.each(dataModel.rows,function(pRowIdx, pRowVal){
				lName='fromHaircut'+pRowIdx;
		    	lHtml+='<tr><td class="headRetentionLt"><input type="hidden" id="'+lName+'">';
		    	lFields.push({name:lName,dataType:'DECIMAL',notNull:true,integerLength:3,decimalLength:2,label:'From Haircut'});
		    	lFormData[lName]=pRowVal.fromHaircut;

				lName='toHaircut'+pRowIdx;
				lHtml+='<table border="0"><tr><td style="width:30px;text-align:center"><input type="checkbox" class="chkRow" value="'+pRowIdx+'" id="row'+pRowIdx+'"'+'</td><td>';
		    	lHtml+='<section class="input"><input type="text" id="'+lName+'"><b class="tooltip tooltip-top-right"></b></section>';
		    	lHtml+='</td></tr></table></td>';
		    	lFields.push({name:lName,dataType:'DECIMAL',notNull:true,integerLength:3,decimalLength:2,label:'Retention Margin Upto'});
		    	lFormData[lName]=pRowVal.toHaircut;
		    	
		    	$.each(dataModel.cols,function(pColIdx, pColVal){
					lName='capRate'+pRowIdx+'_'+pColIdx;
		    		lHtml+='<td class="cellHaircut"><section class="input"><input type="text" id="'+lName+'" placeholder="Cap Rate"><b class="tooltip tooltip-top-right"></b></section></td>';
			    	lFields.push({name:lName,dataType:'DECIMAL',notNull:true,integerLength:3,decimalLength:2,label:'Cap Rate%'});
			    	if ((dataModel.data.length>pRowIdx) && (dataModel.data[pRowIdx].length>pColIdx))
			    		lFormData[lName]=dataModel.data[pRowIdx][pColIdx];
		    	});
		    	lHtml+='<td></td></tr>'
			});
			lHtml+='<tr><td><button type="button" class="btn btn-success btn-lg" onClick="javascript:addRow()"><span class="fa fa-plus"></span> Add</button></td>'+lColRow3+'</tr>';
			lHtml+='</table></div></div>';

			$('#divMatrix').html(lHtml);
			var lFieldNames=[];
			$.each(lFields,function(pIdx,pVal){
				lFieldNames.push(pVal.name);
			});
			var lConfig = {
				resource:'xxx',
				fields:lFields,
				fieldGroups:{insert:lFieldNames},
				preSaveHandler:function(pData) {
					alert(JSON.stringify(pData));
					return false;
				}
				};
			var lCrudMatrix$ = $('#contMatrix').xcrudwrapper(lConfig);
			crudMatrix=lCrudMatrix$.data('xcrudwrapper');
			mainFormMatrix=crudMatrix.options.mainForm;
			mainFormMatrix.setValue(lFormData);
<%
if (StringUtils.isBlank(lCounterEntity)) {
%>
			$('#pageTitle').html("Buyer Seller Cap Rate.["+loginData.domain+"]");
			$('#myTitle').html("TREDS | Buyer Seller Cap Rate.["+loginData.domain+"]");
<%
}
%>
		}
		function dataModelFromView() {
			var lFormData = mainFormMatrix.getValue();
			$.each(dataModel.cols,function(pColIdx, pColVal){
				lName='fromUsance'+pColIdx;
				pColVal.fromUsance=lFormData[lName];
		    	lName='toUsance'+pColIdx;
				pColVal.toUsance=lFormData[lName];
			});
			dataModel.data=[];
			$.each(dataModel.rows,function(pRowIdx, pRowVal){
				lName='fromHaircut'+pRowIdx;
		    	pRowVal.fromHaircut=lFormData[lName];
				lName='toHaircut'+pRowIdx;
		    	pRowVal.toHaircut=lFormData[lName];
		    	dataModel.data[pRowIdx]=[];
		    	$.each(dataModel.cols,function(pColIdx, pColVal){
					lName='capRate'+pRowIdx+'_'+pColIdx;
					dataModel.data[pRowIdx][pColIdx]=lFormData[lName];	    		
		    	});
			});
		}
		function addRow() {
			dataModelFromView();
			dataModel.rows.push({});
			viewFromDataModel();
		}
		function addCol() {
			dataModelFromView();
			dataModel.cols.push({});
			viewFromDataModel();
		}
		function removeRowCol() {
			var lRowIdxs={};
			$('.chkRow:checked').each(function() {
				lRowIdxs[$(this).val()]=true;
			});
			var lColIdxs={};
			$('.chkCol:checked').each(function() {
				lColIdxs[$(this).val()]=true;
			});
			if ($.isEmptyObject(lRowIdxs) && $.isEmptyObject(lColIdxs)) {
				alert("Please select one or more Tenor bands and/or Retention Margin bands to remove");
				return;
			}
			dataModelFromView();
			for (var lPtr=dataModel.rows.length-1;lPtr>=0;lPtr--) {
				if (lRowIdxs[lPtr]==true) {
					dataModel.rows.splice(lPtr,1);
					dataModel.data.splice(lPtr,1);
				}
			}
			for (var lPtr=dataModel.cols.length-1;lPtr>=0;lPtr--) {
				if (lColIdxs[lPtr]==true) {
					dataModel.cols.splice(lPtr,1);
					$.each(dataModel.data,function(pIdx,pVal){
						pVal.splice(lPtr,1);
					});
				}
			}
			viewFromDataModel();
		}
		function saveMatrix() {
			// set from values as previous to values + precision
			var lFormData = mainFormMatrix.getValue();
			$.each(dataModel.cols,function(pColIdx, pColVal){
				var lFromUsance = 0;
				if (pColIdx>0) lFromUsance = parseInt(lFormData['toUsance'+(pColIdx-1)])+1;
				mainFormMatrix.getField('fromUsance'+pColIdx).setValue(lFromUsance);
			});
			$.each(dataModel.rows,function(pRowIdx, pRowVal){
				var lFromHaircut = 0.0;
				if (pRowIdx>0) lFromHaircut = parseFloat(lFormData['toHaircut'+(pRowIdx-1)])+0.01;
				mainFormMatrix.getField('fromHaircut'+pRowIdx).setValue(lFromHaircut);
			});

			// validations
			var lErrors = mainFormMatrix.check();
			if ((lErrors != null) && (lErrors.length > 0)) {
				crudMatrix.showError();
				return;
			}

			dataModelFromView();
    		$('#btnSave').prop('disabled',true);
			$.ajax( {
	            url: 'caprate',
	            type: "POST",
	            data: JSON.stringify(dataModel),
	            success: function( pObj, pStatus, pXhr) {
            		alert("Saved successfully", "Information", function() {
            			closePage();
            		});
	            },
	        	error: errorHandler,
	        	complete: function() {
	        		$('#btnSave').prop('disabled',false);
	        	}
	        });					

		}
		function closePage() {
			if('<%=lCounterEntity%>'=='null' || '<%=lCounterEntity%>'=='')
				location.href='home';
			else 
				location.href='pursuplnk';
		}
	</script>

</body>
</html>