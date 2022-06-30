<!DOCTYPE html>
<%@page import="com.xlx.treds.monetago.bean.GstnMandateBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.bill.bean.BillBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>

<html lang="en">
  <head>
	
	<title>GSTN's</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
        <style>
	        .loadeer{
			  border: 16px solid #f3f3f3;
			  border-radius: 50%;
			  border-top: 16px solid black;
			  width: 120px;
			  height: 120px;
			  -webkit-animation: spin 2s linear infinite; /* Safari */
			  animation: spin 2s linear infinite;
			}
			@keyframes spin {
			  0% { transform: rotate(0deg); }
			  100% { transform: rotate(360deg); }
			}
			#divLoader{
			    display: flex;
		    	align-items: center;
		    	justify-content: center;
		    	height: 100vh;
			}
	</style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="GSTN" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contGSTN">	
		<div id="divData">
			<div id=divLoader   style=display:none;>
				<div  class="loadeer" ></div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="mdlEditEmails" tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Edit Emails</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmEditEmails">
			<fieldset>
				<div class="row">
					<div class="col-sm-4">
						<section>
							<label for="gstn" class="label">GST No:</label>
						</section>
					</div>
					<div class="col-sm-8">
						<section class="input">
							<input type="text" id="gstn"
								placeholder="GST NO."> <b
								class="tooltip tooltip-top-right"></b>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="emailList" class="label">Email List:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<div class="input-group"><input type="text" id="emailList" placeholder="Email List">
						<span class="input-group-btn"><button class="btn btn-default btn-sm" type="button">Add</button></span>
						<b class="tooltip tooltip-top-right"></b></div>
						<div class="value-list"></div></section>
						<section class="view"></section>
					</div>
				</div>
				<div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnRegisterGst><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-back"></span> Back</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>

   	<%@include file="footer1.jsp" %>
	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>

	<script type="text/javascript">
		var tplTbl;
		var emailsForm$ = null, emailsForm=null;
		var lGstnWiseEmails ={};
		$(document).ready(function() {
			tplTbl = Handlebars.compile($('#tplTbl').html());
			var lConfig = {
					resource: "gstmandate",
					autoRefresh: true
			};
			var lEmailConfig = {
					"fields": [
							{
								"name": "emailList",
								"label": "Email",
								"dataType":"STRING",
								"maxLength": 50,
								"nonDatabase":true,
								"allowMultiple": true,
								"maxItems":3,
								"notNull": true,
								"pattern": "[a-zA-Z0-9._-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z0-9.-]{2,4}",
								"patternMessage":"Please ensure the email id is valid.E.g. AmitM@gmail.com"
							},
							{
								"name": "gstn",
								"label": "GSTN",
								"dataType": "STRING",
								"maxLength": 30,
								"notNull": true
							}
					]
				};
			emailsForm$ = $('#frmEditEmails').xform(lEmailConfig);
			emailsForm = emailsForm$.data('xform');
			fetchGstn();
			
			$('#btnRegisterGst').on('click', function() {
    			var lErrors = emailsForm.check();
				if ((lErrors != null) && (lErrors.length > 0)) {
					//showError(emailsForm);
					return;
				}
				var lEmails = emailsForm.getField('emailList').getValue();
				if(lEmails.length!=3){
					alert("only 3 emails allowed");
					return;
				}else{
					var lData = {'gstn':emailsForm.getField('gstn').getValue(), "emails":lEmails} ;
					$('#mdlEditEmails').hide();
					$.ajax( {
			            url: 'gstmandate/addSupplier',
			            type: 'POST',
			            data : JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
			            },
			        	error: errorHandler,
			        	complete: function() {
			        	}
				    });
				}
			});
		});
		function register(pGstn,pEmails){
			var lEmails = pEmails.split(",");
			emailsForm.setValue({'gstn':pGstn,'emailList': lEmails});
			showModal($('#mdlEditEmails'));
			emailsForm.getField('gstn').disable();
		}
		function fetchGstn(){
			$('#divLoader').show();
			var lGstnData ;
			$.ajax( {
	            url: 'gstmandate/gstn',
	            type: 'GET',
	            success: function( pObj, pStatus, pXhr) {
	            	lGstnData = tplTbl(pObj)
	            },
	        	error: errorHandler,
	        	complete: function() {
	        		$('#divLoader').hide();
	        		$('#divData').html(lGstnData);
	        	}
		    });
		}
		
		function showError(pObj) {
			var lResp = appendError(pObj.fields, true);
			alert(lResp[0] , "Validation Failed", function() {
			});
		}
	</script>
	
   	<script id="tplTbl" type="text/x-handlebars-template">
		<table class="table">
			<thead>
				<tr>
					<th>GSTN</th>
					<th>STATUS</th>
					<th>Register</th>
					<th>Consent</th>
				</tr>
			</thead>
			<tbody>
				{{#each this}}
				<tr>
					<td>{{gstn}}</td>
					<td>{{status}}</td>
					<td>{{#if consentType}}{{else}}<button type="button" id='btn{{gstn}}' onclick="register('{{gstn}}','{{emailList}}')"> Register </button> {{/if}}</td>
					<td>{{consentTypeDesc}}<td>
				</tr>
				{{/each}}
			</tbody>
		</table>
	</script>

</body>
</html>