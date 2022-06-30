<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentBean"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = StringEscapeUtils.escapeHtml(request.getParameter("id"));
boolean lForApprove = (request.getParameter("app")!=null);
boolean lForCounterCheckerApproval = (request.getParameter("couchkapp")!=null);
String lUrl = "instview?id="+lId;
%>
        <style>
        .xform section {
        	margin:0px;
        }
        .xform .label, .xform section.view {
        	padding-top: 0px;
        	padding-bottom: 0px;
        	height:auto;
        }
        </style>
	<div class="content" id="frmContent">
		
    	<!-- frmMain -->
    	<div class="xform view">
    		<fieldset>
			<div class="">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-group pull-right">
<%
if (lForApprove) { 
%>
	<button type="button" class="btn btn-info btn-lg" onClick="javascript:updateStatusPromptClickWrap('<%=InstrumentBean.Status.Checker_Approved.getCode()%>');" data-dismiss="modal"><span class="fa fa-check"></span> Approve</button>
<%
}else if (lForCounterCheckerApproval) { 
%>
	<button type="button" class="btn btn-info btn-lg" onClick="javascript:update('<%=InstrumentBean.Status.Counter_Approved.getCode()%>');" data-dismiss="modal"><span class="fa fa-check"></span> Approve</button>
<%
}
%>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
    		</div>
			<div id="divInstData">
    			
			</div>
    		</fieldset>
    	</div>
    	<!-- frmMain -->
	</div>

   	<script id="tplInstrumentView" type="text/x-handlebars-template">

			{{#if groupInId}}
				<hr>
				<h3> Instrument Group Info </h3>
				<br></br>
				<div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Group Instrument Id:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{groupInId}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Group Invoice No:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{groupInvoiceNo}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Group Counter Ref No:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{groupCountRefNo}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Group Description:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{groupDescription}}</section>
						</div>
					</div>
				</div>
			{{/if}}

			<h3>Instrument</h3>
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-3"></div>
					<div class="col-md-3"><section><label class="label">Id:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{id}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Seller:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supName}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Seller Location:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supLocation}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Seller GSTN:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supGstn}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Seller GST State:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supGstStateDesc}}</section>
					</div>
				</div>
				<p></p>				
				<div class="row">
					<div class="col-md-3"><section><label class="label">Buyer:</label></section></div>
					<div class="col-md-3">						
						<section class="view">{{purName}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Buyer Location:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{purLocation}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Buyer Settlement Location:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{settlePurLocation}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Buyer GSTN:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{purGstn}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Buyer Settlement GSTN:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{settlePurGstn}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Buyer GST State:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{purGstStateDesc}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Buyer Settlement GST State:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{settlePurGstStateDesc}}</section>
					</div>
				</div>
				<p></p>
         		{{#if instCrtKeys}}	
				<div class="row">
					<div class="col-md-3"><section><label class="label">Instrument Keys:</label></section></div>
					<div class="col-md-6">
						<section class="view"><ul>{{#each instCrtKeys}}<li>{{this}}</li>{{/each}}</ul></section>
					</div>
				</div>
				<p></p>
               		{{/if}}					
				<div class="row">
					<div class="col-md-3"><section><label class="label">PO Number:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{poNumber}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">PO Date:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{poDate}}</section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-3"><section><label class="label">Counter Reference:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{counterRefNum}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Goods Acceptance Date:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{goodsAcceptDate}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Sales Category:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{salesCategoryDesc}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Description:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{description}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Invoice Number:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{instNumber}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Invoice Date:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{instDate}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Invoice Due Date:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{instDueDate}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Statutory Due Date:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{statDueDate}}</section>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Status:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{status}} </section>
					</div>
					{{#if fuId}} 
						<div class="col-md-3"><section><label class="label">Factoring Unit Id:</label></section></div>
						<div class="col-md-3">
							<section class="view">{{fuId}} </section>
						</div>
					{{/if}}
				</div>
				<div class="row">
						<div class="col-md-3"><section><label class="label">Credit Period (days):</label></section></div>
						<div class="col-md-3">
							<section class="view">{{creditPeriod}}</section>
						</div>
						{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.toString()%>"}}
				 		<div class="col-md-3"><section><label class="label">Extension Enabled:</label></section></div>
						<div class="col-md-3">
							<section class="view">{{enableExtension}}</section>
						</div>
						{{/ifCond}}	
                 </div>
				{{#ifCond enableExtension '==' "<%=CommonAppConstants.Yes.Yes.toString()%>"}}
				<div class="row">
						<div class="col-md-3"><section><label class="label">Extended CreditPeriod (days):</label></section></div>
						<div class="col-md-3">
							<section class="view">{{extendedCreditPeriod}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Extended Due Date:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{extendedDueDate}}</section>
						</div>
				</div>
				{{/ifCond}}	
				<hr>
				<div class="row">
					<div class="col-md-6">
						<h3>Amount</h3>
						<div class="row"><div class="col-md-6"><section><label class="label">Currency:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{currency}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Invoice Amount:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{amount}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Haircut %:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{haircutPercent}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Deductions:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{adjAmount}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Cash Disc %:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{cashDiscountPercent}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Cash Disc Value:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{cashDiscountValue}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">TDS:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{tdsAmount}}{{/formatDec}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Factoring Unit Cost:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{#formatDec}}{{netAmount}}{{/formatDec}}</section>
						</div></div>
					</div>
					<div class="col-md-6">
						<h3>Auction Preferences</h3>
						<div class="row"><div class="col-md-6"><section><label class="label">Auto Accept Bids:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{autoAccept}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Cost Bearer:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{costBearingType}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Platform Charge Bearer:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{chargeBearer}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Settle Leg 3:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{settleLeg3Flag}}</section>
						</div></div>
						<div class="row"><div class="col-md-6"><section><label class="label">Auto Send for Auction:</label></section></div>
						<div class="col-md-6">
							<section class="view">{{autoConvert}}</section>
						</div></div>
					</div>
				</div>
				{{#if cfDetails}}
					<h3>Custom Fields</h3>
						<div class="row">
						{{#each cfDetails}}
							<div class="col-md-3"><section><label class="label">{{label}}:</label></section></div>
							<div class="col-md-3">						
							<section class="view">{{value}}</section>
							</div>
						{{/each}}
						</div>
				{{/if}}
				{{#if groupFlag}}
				{{else}}
				<hr>
				<h3>EWay Bill Details</h3>
				<br></br>
				<div>			
					<div class="row">
						<div class="col-md-3"><section><label class="label">E-Way Bill No:</label></section></div>
						<div class="col-md-3">						
						<section class="view">{{ewayBillNo}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Supply Type:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{supplyType}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Document Type:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{docType}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Document Number:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{docNo}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Document Date:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{docDate}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">From Pincode:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{fromPincode}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">To Pincode:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{toPincode}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Mode of Transport:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{transMode}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Transporter Name:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{transporterName}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Transporter Id:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{transporterId}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Transporter Doc No:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{transDocNo}}</section>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3"><section><label class="label">Transporter Doc Date:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{transDocDate}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Vehicle No:</label></section></div>
						<div class="col-md-3">
						<section class="view">{{vehicleNo}}</section>
					</div>
				</div>
				<hr>
				{{/if}}
				<h3>Attachments</h3>
				{{#if groupFlag}}
				{{else}}
				{{#if instImage}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Invoice:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{instImage}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{#encodeURIComponent instImage}}{{/encodeURIComponent}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if creditNoteImage}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Credit Note:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{creditNoteImage}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{creditNoteImage}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if sup1}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{sup1}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup1}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if sup2}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{sup2}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup2}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if sup3}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{sup3}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup3}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if sup4}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{sup4}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup4}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if sup5}}
				<div class="row"><div class="col-sm-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-sm-3">
					{{#imageName}}{{sup5}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup5}}" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{/if}}
				{{#if groupInId}}
				{{else}}
				<div class="row" id="agreement"><div class="col-sm-3"><section><label class="label">Click Wrap Agreement:</label></section></div>
				<div class="col-sm-3">
					agreement.pdf  <a href="javascript:downloadAgreement({{id}},'{{purchaser}}');" download>  <span class="fa fa-download"> Download</span></a>
				</div></div>
				{{/if}}
				{{#if groupInId}}
				{{else}}
				<h3>Instrument Workflow</h3>
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-striped table-bordered" style="width:100%"><tbody>
						<tr>
							<th>Entity</th>
							<th>Login ID</th>
							<th>Status</th>
							<th>Status Remarks</th>
							<th>Update Time</th>
						</tr>
						{{#each workFlows}}
						<tr>
							<td>{{entity}}</td>
							<td>{{loginId}}</td>
							<td>{{status}}</td>
							<td>{{statusRemarks}}</td>
							<td>{{statusUpdateTime}}</td>
						</tr>
						{{/each}}
						</tbody></table>
					</div>
				</div>
				{{/if}}
	</script>

	<script type="text/javascript">
	var tplInstrumentView;
	$(document).ready(function() {
		Handlebars.registerHelper('imageName', function(options) {
			var lImg = options.fn(this);
			var lPos=lImg.indexOf('.');
			return lPos>0?lImg.substring(lPos+1):lImg;
		});
		tplInstrumentView = Handlebars.compile($('#tplInstrumentView').html());
		$.ajax({
			url: '<%=lUrl%>',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
 				$('#divInstData').html(tplInstrumentView(pObj));
 				var lIsSupplierFlag = (loginData.domain==pObj.supplier);
 				if(lIsSupplierFlag){
 					$('#agreement').hide();
 				}
			},
			error: errorHandler,
		});
	});
	
	function downloadAgreement(instId , purchaser){
		var lHash = {"domain": purchaser,"instId": instId,"getImage":true };
		downloadFile('puraggacc/downloadclickwrap?<%=AppConstants.CLICKWRAP_QUERYPARAMETER_FILENAME%>=<%=AppConstants.CLICKWRAP_FILECODE_AGREEMENT%>',null,JSON.stringify(lHash));
	}
	
	</script>
