<%@page import="com.xlx.treds.instrument.bean.FactoringUnitBean.Status"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = StringEscapeUtils.escapeHtml(request.getParameter("fuid"));
String lFin = StringEscapeUtils.escapeHtml(request.getParameter("fin"));
String lUrl = "fuview?id="+lId;
boolean lFinancier =  "Y".equals(lFin);
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
	<div class="content">
		
    	<!-- frmMain -->
	    	<div class="xform view">
	    	<fieldset>
			<div id="divFactUnit">
    			
			</div>
			<hr>
    		<div>
				<div class="row">
					<div class="col-md-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-close" data-dismiss="modal">OK</button>
						</div>
					</div>
				</div>
    		</div>
	    	</fieldset>
    	</div>
    	<!-- frmMain -->
	</div>

   	<script id="tplFactoringUnitView" type="text/x-handlebars-template">
		<h3>Factoring Unit</h3>
<div class="row">
	<div class="col-md-6">
			<div class="row"><div class="col-md-6"><section><label class="label">FactoringUnit Id:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{id}}</section>
			</div></div>
			<div class="row"><div class="col-md-6"><section><label class="label">Due Date:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{maturityDate}}</section>
			</div></div>

				{{#ifCond enableExtension '==' '<%=CommonAppConstants.Yes.Yes%>'}}
 				<div class="row">
					<div class="col-md-6"><section><label class="label">Extended creditPeriod (Days):</label></section></div>
					<div class="col-md-6">
						<section class="view">{{extendedCreditPeriod}}</section>
					</div>
				</div>
 				<div class="row">
					<div class="col-md-6"><section><label class="label">Extended Due Date:</label></section></div>
					<div class="col-md-6">
						<section class="view">{{extendedDueDate}}</section>
					</div>
				</div>
				{{/ifCond}}



			<div class="row"><div class="col-md-6"><section><label class="label">Amount:</label></section></div>
			<div class="col-md-6">
				<section class="view"><span class="fa fa-cust{{currency}}"></span>  {{#formatDec}}{{amount}}{{/formatDec}}</section>
			</div></div>

		<div class="row" id="vwModSLeg3">
			<div class="col-md-6"><section><label class="label">Settle Leg 3:</label></section></div>
			<div class="col-md-6">
					<section class="view">{{settleLeg3FlagDesc}}{{#if isBuyer}} <button class="btn btn-sm btn-default" id="btnModSLeg3" title="Modify Leg3 Settlement"><span class="fa fa-pencil"></span></button>{{/if}}</section>
			</div>
		</div>
		{{#if isBuyer}}<div class="xform hidden" id="frmModSLeg3">
			<div class="row">
				<div class="col-md-6"><section><label for="settleLeg3Flag" class="label">Settle Leg 3:</label></section></div>
				<div class="col-md-6">
					<section class="inline-group view">
					<label class="radio" style="display: block !important;"><input type=radio id="settleLeg3Flag"><i></i><span></span>
					<b class="tooltip tooltip-top-left"></b></label>
					</section>
				</div>
			</div><br>
			<div class="row">
				<div class="col-md-6"><section><label for="" class="label"></label></section></div>
				<div class="col-md-6"><div class="btn-groupX pull-right">
					<button type="button" class="btn btn-sm btn-primary" id=btnSaveModSLeg3 title="Update Leg 3 Settlement Flag"><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-sm btn-close" id=btnCloseModSLeg3 title="Close"><span class="fa fa-close"></span> Cancel</button>
				</div></div>
			</div>
		</div>{{/if}}

		<div class="row">
			<div class="col-md-6"><section><label class="label">Factoring Period:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{factorStartDateTime}} to </section>
			</div>
		</div>
		<div class="row" id=vwModEndTime>
			<div class="col-md-6"><section><label class="label"></label></section></div>
			<div class="col-md-6">
				<section class="view">{{factorEndDateTime}}{{#if owner}} <button class="btn btn-sm btn-default" id="btnModEndTime" title="Modify End Time"><span class="fa fa-pencil"></span></button>{{/if}}</section>
			</div>
		</div>
		{{#if owner}}<div class="xform hidden" id="frmModEndTime">
			<div class="row">
				<div class="col-md-6"><section><label class="label"></label></section></div>
				<div class="col-sm-6">
					<section class="input view">
					<input type="text" id="factorEndDateTime" placeholder="Factoring End Date Time" data-role="datetimepicker">
					<b class="tooltip tooltip-top-right"></b></section>
				</div>
			</div>
			<br>
			<div class="row">
				<div class="col-sm-12"><div class="btn-groupX pull-right">
					<button type="button" class="btn btn-sm btn-primary" id=btnSaveModEndTime title="Update End Date Time"><span class="fa fa-save"></span> Save</button>
					<button type="button" class="btn btn-sm btn-close" id=btnCloseModEndTime title="Close"><span class="fa fa-close"></span> Cancel</button>
				</div></div>
			</div>
		</div>{{/if}}

	</div>

		<div class="col-md-6">
		<div class="row">
			<div class="col-md-6"><section><label class="label">Auto Accept:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{autoAccept}}</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Auto Acceptable Bid Types:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{autoAcceptableBidTypes}}</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Cost Bearer:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{costBearingType}}</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Platform Charges:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{chargeBearer}}</section>
			</div>
		</div>
<% 
	if (lFinancier)
	{
%>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Bid Rate Range:</label></section></div>
			<div class="col-md-6">
				<section class="view">{{minBidRate}} - {{maxBidRate}}</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Balance Limit:</label></section></div>
			<div class="col-md-6">
				<section class="view"><span class="fa fa-cust{{currency}}"></span>  {{#formatDec}}{{balanceLimit}}{{/formatDec}}</section>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6"><section><label class="label">Balance Bid Limit:</label></section></div>
			<div class="col-md-6">
				<section class="view"><span class="fa fa-cust{{currency}}"></span>  {{#formatDec}}{{balanceBidLimit}}{{/formatDec}}</section>
			</div>
		</div>
<%
	}
%>
		</div>
		</div>
		<div class="row">
		<div class="col-md-6">
		</div>
		</div>

			{{#each insts}}
			
			<h3>Instrument</h3>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Instrument Id:</label></section></div>
					<div class="col-md-3">
						<section class="view"> <a href="javascript:viewIN('{{id}}')" title="View Instrument"  style="color:blue !important">{{id}}</a>  </section>
					</div>
			<div class="col-md-3"><section><label class="label">Auto Convert:</label></section></div>
			<div class="col-md-3">
				<section class="view">{{autoConvert}}</section>
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
					<div class="col-md-3"><section><label class="label">Seller GST State:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supGstStateDesc}}</section>
					</div>
					<div class="col-md-3"><section><label class="label">Seller GSTN:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{supGstn}}</section>
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
						<div class="col-md-3"><section><label class="label">Credit Period (days):</label></section></div>
						<div class="col-md-3">
							<section class="view">{{creditPeriod}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Tenor:</label></section></div>
						<div class="col-md-3">
							<section class="view">{{tenure}}</section>
						</div>
				</div>
				<div class="row">
						<div class="col-md-3"><section><label class="label">Cash Discount Amount:</label></section></div>
						<div class="col-md-3">
							<section class="view">{{cashDiscountAmount}}</section>
						</div>
						<div class="col-md-3"><section><label class="label">Adj Amount:</label></section></div>
						<div class="col-md-3">
							<section class="view">{{adjAmount}}</section>
						</div>
				</div>
				<div class="row">
					<div class="col-md-3"><section><label class="label">Status:</label></section></div>
					<div class="col-md-3">
						<section class="view">{{status}} </section>
					</div>
				</div>
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
				<h3>Attachments</h3>
				{{#if groupFlag}}
				{{else}}
				{{#if instImage}}
				<div class="row"><div class="col-md-3"><section><label class="label">Invoice:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{instImage}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{#encodeURIComponent instImage}}{{/encodeURIComponent}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if creditNoteImage}}
				<div class="row"><div class="col-md-3"><section><label class="label">Credit Note:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{creditNoteImage}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{creditNoteImage}}" download> <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if sup1}}
				<div class="row"><div class="col-md-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{sup1}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup1}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if sup2}}
				<div class="row"><div class="col-md-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{sup2}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup2}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if sup3}}
				<div class="row"><div class="col-md-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{sup3}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup3}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if sup4}}
				<div class="row"><div class="col-md-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{sup4}}{{/imageName}}  <a href="upload/INSTRUMENTS/{{sup4}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{#if sup5}}
				<div class="row"><div class="col-md-3"><section><label class="label">Supporting:</label></section></div>
				<div class="col-md-3">
					<section class="view">
					{{#imageName}}{{sup5}}{{/imageName}}<a href="upload/INSTRUMENTS/{{sup5}}" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/if}}
				{{/if}}
				<div class="row" id="agreement"><div class="col-sm-3"><section><label class="label">Click Wrap Agreement:</label></section></div>
				<div class="col-sm-3">
					<section class="view">
						agreement.pdf  <a href="javascript:downloadAgreement({{id}},'{{../purchaser}}');" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				<div class="row" id="deedofassignment"><div class="col-sm-3"><section><label class="label">Deed Of Assignment:</label></section></div>
				<div class="col-sm-3">
					<section class="view">
						deedofassignment.pdf  <a href="javascript:downloadDeed({{../id}});" download>  <span class="fa fa-download"> Download</span></a>
					</section>
				</div></div>
				{{/each}}
	</script>


	<div class="modal fade" tabindex=-1 id="mdlClubbedInvoice"><div class="modal-dialog  modal-lg"><div class="modal-content">
	</div></div></div>

	<script id="tplClubbedInstruments" type="text/x-handlebars-template">
		<div class="modal-header">Details of clubbed instruments <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button>
		</div>
		<div class="modal-body">
		<fieldset>
		<div class="row">
			<div class="col-sm-12">
				<table class="table" id="instClub"><thead>
				<tr>
					<th width=125px >ID </th>
					<th > Invoice No. </th>
					<th >Invoice Amount </th>
					<th >Cash Discount Amount</th>
					<th> Adj Amount</th>
					<th> TDS Amount</th>
					<th>Net Amount</th>
					<th width=90px > </th>
				</tr></thead>
				<tbody>
{{#if splitlist}}
		{{#each splitlist}}
				<tr>
					<td>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td><button type="button" id='btnViewInst' onclick="viewClubbedInstrument({{id}})">View</button></td>
				</tr>
		{{/each}}
{{/if}}
{{#if inst}}
		{{#each inst}}
				<tr>
					<td>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td></td>
				</tr>
		{{/each}}
{{/if}}
				</tbody>
				</table>
			</div>
		</div>
		{{#if paymentAdvise}}
		<div class="row">
		<h3><b><center>CV Number : {{cvNumber}}</center></b></h3>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table"><thead>
				<tr>
					<th>Vendor Invoice Number</th>
					<th>Invoice Date</th>
					<th>Inv Amount (Rs.)</th>
					<th>FI Document No Details</th>
					<th>Code</th>
					<th>Debit/Credit Amount(Rs.)</th>
				</tr></thead>
				<tbody>
		{{#each paymentAdvise}}
				<tr>
					<td>{{inv_num}}</td>
					<td>{{inv_date}}</td>
					<td>{{amount}}</td>
					<td>{{document_number}}</td>
					<td>{{deduction_reason}}</td>
					<td>{{dedudcted_amount}}</td>
					<td></td>
				</tr>
		{{/each}}
				</tbody>
				</table>
			</div>
		</div>
		</fieldset>
		</div>
	{{/if}}

	</script>

	<script type="text/javascript">
	var tplFactoringUnitView;
	var fuModForm$,fuModForm;
	var fuModSLeg3$,fuModSLeg3;
	var tplClubbedInstruments;
	$(document).ready(function() {
		tplFactoringUnitView = Handlebars.compile($('#tplFactoringUnitView').html());
		tplClubbedInstruments=Handlebars.compile($('#tplClubbedInstruments').html());
		Handlebars.registerHelper('imageName', function(options) {
			var lImg = options.fn(this);
			var lPos=lImg.indexOf('.');
			return lPos>0?lImg.substring(lPos+1):lImg;
		});
		$.ajax({
			url: '<%=lUrl%>',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
 				$('#divFactUnit').html(tplFactoringUnitView(pObj));
 				var lIsSupplierFlag = (loginData.domain==pObj.supplier);
 				if(lIsSupplierFlag){
 					$('#agreement').hide();
 					if ('<%=Status.Factored.toString()%>'==pObj.status ||
 							'<%=Status.Leg_1_Failed.toString()%>'==pObj.status ||
 								'<%=Status.Leg_1_Settled.toString()%>'==pObj.status ||
 									'<%=Status.Leg_2_Failed.toString()%>'==pObj.status ||
 									'<%=Status.Leg_2_Settled.toString()%>'==pObj.status ||
 											'<%=Status.Leg_3_Generated.toString()%>'==pObj.status){
 						// Remove comment while releasing deed of assignment
 						$('#deedofassignment').show();
 					}
 					
 				}
 				if (pObj.owner) {
 	 				var lFUConfig = {
 	 						"fields": [
 	 							{
 	 								"name":"id",
 	 								"dataType":"INTEGER"
 	 							},
 	 						 	{
 	 								"name":"factorEndDateTime",
 	 								"label":"Factoring End Date",
 	 								"dataType":"DATETIME",
 	 								"notNull": true,
 	 								"format":"dd-MMM-yyyy HH:mm:ss"
 	 							},
 	 							{
 	 								"name":"recordVersion",
 	 								"dataType":"INTEGER"
 	 							}
 	 						]
 	 					};
 	 				fuModForm$ = $('#frmModEndTime').xform(lFUConfig);
 	 				fuModForm = fuModForm$.data('xform');
 	 				fuModForm.setValue(pObj);
 					console.log('herer 1');
 	 				$('#btnModEndTime').on('click',function(){
 	 					$('#frmModEndTime').removeClass('hidden');
 	 					$('#vwModEndTime').addClass('hidden');
 	 				});
 	 				$('#btnSaveModEndTime').on('click',function(){
 	 					var lData = fuModForm.getValue();
 	 					if (lData.factorEndDateTime=='') {
 	 						alert("Factoring end time required");
 	 						return;
 	 					}
 						$('#btnSaveModEndTime').prop('disabled',true);
 	 					$.ajax( {
 	 				        url: "factunitsp/update",
 	 				        type: "PUT",
 	 				        data: JSON.stringify(lData),
 	 				        success: function( pObj, pStatus, pXhr) {
 	 				        	alert("Updated Successfully");
 	 				        	closeRemote();
 	 				        },
 	 				        error: errorHandler,
 							complete: function() {
 			        			$('#btnSaveModEndTime').prop('disabled',false);
 							}
 	 					});
 	 				});
 	 				$('#btnCloseModEndTime').on('click',function(){
 	 					$('#frmModEndTime').addClass('hidden');
 	 					$('#vwModEndTime').removeClass('hidden');
 	 				});
 				}
 				var lFUConfig2 = {
 						"fields": [
 							{
 								"name":"id",
 								"dataType":"INTEGER"
 							},
 						 	{
 								"name":"settleLeg3Flag",
 								"label":"Enable Leg 3 Settlement",
 								"dataType":"STRING",
								"dataSetType": "ENUM",
								"dataSetValues": [{"text": "Yes","value": "Y"},{"text": "No","value": "N"}]
 						 	},
 							{
 								"name":"recordVersion",
 								"dataType":"INTEGER"
 							}
 						]
 					};
 				fuModSLeg3$ = $('#frmModSLeg3').xform(lFUConfig2);
 				fuModSLeg3 = fuModSLeg3$.data('xform');
 				fuModSLeg3.setValue(pObj);
 				$('#btnModSLeg3').on('click',function(){
 					$('#frmModSLeg3').removeClass('hidden');
 					$('#vwModSLeg3').addClass('hidden');
 				});
 				$('#btnSaveModSLeg3').on('click',function(){
 					var lData = fuModSLeg3.getValue();
 					if (lData.SettleLeg3Flag=='') {
 						alert("Settle Leg3 Flag required.");
 						return;
 					}
					$('#btnSaveModSLeg3').prop('disabled',true);
 					$.ajax( {
 				        url: "factunitsp/updateleg3flag",
 				        type: "PUT",
 				        data: JSON.stringify(lData),
 				        success: function( pObj, pStatus, pXhr) {
 				        	alert("Updated Successfully");
 				        	closeRemote();
 				        },
 				        error: errorHandler,
						complete: function() {
		        			$('#btnSaveModSLeg3').prop('disabled',false);
						}
 					});
 				});
 				$('#btnCloseModSLeg3').on('click',function(){
 					$('#frmModSLeg3').addClass('hidden');
 					$('#vwModSLeg3').removeClass('hidden');
 				});

			},
			error: errorHandler,
		});
	});
	function downloadAgreement(instId , purchaser){
		var lHash = {"domain": purchaser,"instId": instId,"getImage":true };
		downloadFile('puraggacc/downloadclickwrap?<%=AppConstants.CLICKWRAP_QUERYPARAMETER_FILENAME%>=<%=AppConstants.CLICKWRAP_FILECODE_AGREEMENT%>',null,JSON.stringify(lHash));
	}
	function downloadDeed(fuId){
		downloadFile('factunitsp/downloaddeedofassignmenthtml/'+fuId);
	}
	function viewIN(pId) {
		var lUrl= 'factunitfin/viewclubbeddetails/' + pId  ;
			$.ajax({
	        url: lUrl,
	        type: 'GET',
	        success: function( pObj, pStatus, pXhr) { 
	        	$('#mdlClubbedInvoice .modal-content').html(tplClubbedInstruments(pObj));
	        	showModal($('#mdlClubbedInvoice'));
	        },
	    	error: errorHandler
	    	});
		//showRemote('instview?id='+pId, 'modal-xl', true,'Instrument Details');
	}
	</script>

