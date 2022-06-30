		<!-- frmMain -->
<%@page import="com.xlx.commonn.bean.BeanFieldMeta"%>
<%@page import="com.xlx.treds.auction.bean.PurchaserSupplierLinkBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants.CostBearingType"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.treds.AppConstants.AutoAcceptableBidTypes"%>
<%@page import="com.xlx.treds.instrument.bean.InstrumentBean"%>
<%@page import="com.xlx.treds.AppConstants.AutoAcceptBid"%>
<%@page import="com.xlx.treds.AppConstants"%>

<style type="text/css">
   #dispAgreement {
   overflow: scroll;
   height: 300px;
   }
   .rowYes{
	background-color: #FFA746 !important;
	}
	.rowEway{
	background-color: #5899FD !important;
	}
	.color-box {
	    width: 15px;
	    height: 15px;
	    display: inline-block;
	    position: absolute;
	}
	.bootstrap-duallistbox-container .btn.moveall, .bootstrap-duallistbox-container .btn.removeall,
	.bootstrap-duallistbox-container .btn.move, .bootstrap-duallistbox-container .btn.remove  {
	    width: 50% !important;
	    display: inline-block;
	    text-align: center;
	    padding: 0px;	
	}

</style>

<div style="display:none" id="frmMain">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Instrument</h1>
			</div>
		</div>
    	<div class="xform box">
    		<fieldset>
    		<div class="box-body panel-group panel-group-joined" id="accordion">
    			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle" data-parent="#accordion" data-toggle="collapse" href="#accrd1">Instruments Details</a>
						</h3>
					</div>
					<div class="panel-collapse collapse in" id="accrd1">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-2"></div>
								<div class="col-sm-4"> <span id="ewayInv" style="display:none"></span></div>
								<div class="col-sm-2"><section><label for="id" class="label">Id:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="id" placeholder="Id" readonly>
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="supplier"><option value="">Select Seller</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="supClId" class="label">Seller Location:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="supClId"><option value="">Select Seller Location</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-supClId"></section>
								</div>
							</div>
							
							<div class="row">
								<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="purchaser"><option value="">Select Buyer</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="purClId" class="label">Buyer Location:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="purClId"><option value="">Select Buyer Location</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-purClId"></section>
								</div>
							</div>
							
							<div class="row">
								<div class="col-sm-2"><section><label for="poNumber" class="label">Purchase Order Number:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="poNumber" placeholder="Purchase Order Number">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="poDate" class="label">Purchase Order Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="poDate" placeholder="Purchase Order Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							
							<div class="row">
								<div class="col-sm-2"><section><label for="counterRefNum" class="label">Counter Reference:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="counterRefNum" placeholder="Counter Reference">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="instDate" class="label">Invoice Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="instDate" placeholder="Invoice Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="salesCategory" class="label">Sales Category:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="salesCategory"><option value="">Select Sales Category</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="instNumber" class="label">Invoice Number:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="instNumber" placeholder="Invoice Number">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="description" class="label">Goods/Service Description:</label></section></div>
								<div class="col-sm-10">
									<section class="input">
									<input type="text" id="description" placeholder="Description">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="goodsAcceptDate" class="label">Goods/Service Acceptance Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="goodsAcceptDate" placeholder="Goods/Service Acceptance Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-goodsAcceptDate"></section>
								</div>
								<div class="col-sm-2"><section><label for="creditPeriod" class="label">Credit Period:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="creditPeriod" placeholder="Credit Period">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-creditPeriod"></section>
								</div>
							</div>	
						<div class="row">
								<div class="col-sm-2"><section><label for='enableExtension' class="label">Enable Extension</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="checkbox"><input type=checkbox id="enableExtension"><i></i><span></span>
									</section>
									
								</div>
	                   		<div class="col-sm-2"><section><label for="extendedcreditPeriod" class="label">ExtendedCredit Period:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="extendedCreditPeriod" placeholder="Extended Credit Period">
									<section class="view"></section>
									<section class="ov-view" id="ov-extendedcreditPeriod"></section>
						    </div>
						</div>
						<div class="row" id="creationKeys">
							<div class="col-sm-2"><section><label for="instrumentCreationKeysList" class="label">Instrument Keys :</label></section></div>
							<div class="col-sm-10">
								<section class="select">
								<select id="instrumentCreationKeysList" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
								<b class="tooltip tooltip-top-right"></b></section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row">
						<div class="col-sm-2"><section><label for="extendedDueDate" class="label">Extended Due Date</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="extendedDueDate" placeholder="Extended Due Date" data-role="datetimepicker">
									</section>
									<section class="view"></section>
									<section class="ov-view" id="ov-extendedDueDate"></section>
								</div>
						
						</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="instDueDate" class="label">Invoice Due Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="instDueDate" placeholder="Invoice Due Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="statDueDate" class="label">Statutory Due Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="statDueDate" placeholder="Statutory Due Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>
				</div>
    			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd2">Amount</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd2">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-2"><section><label for="currency" class="label">Currency:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="currency"><option value="">Select Currency</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2"><section><label for="amount" class="label">Invoice Amount <span class="fa fa-custINR curSymbol"></span> :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="amount" placeholder="Invoice Amount">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-amount"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="haircutPercent" class="label">Haircut % :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="haircutPercent" placeholder="Haircut %">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-haircutPercent"></section>
								</div>
								<div class="col-sm-2"><section><label for="adjAmount" class="label">Deductions <span class="fa fa-custINR curSymbol"></span> :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="adjAmount" placeholder="Deductions">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-adjAmount"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="cashDiscountPercent" class="label">Cash Discount % :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="cashDiscountPercent" placeholder="Cash Discount %">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-cashDiscountPercent"></section>
								</div>
								<div class="col-sm-2"><section><label for="cashDiscountValue" class="label">Cash Discount Value <span class="fa fa-custINR curSymbol"></span> :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="cashDiscountValue" placeholder="Cash Discount Value">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-cashDiscountValue"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"></div>
								<div class="col-sm-4"></div>
								<div class="col-sm-2"><section><label for="tdsAmount" class="label">TDS <span class="fa fa-custINR curSymbol"></span> :</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="tdsAmount" placeholder="TDS">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
									<section class="ov-view" id="ov-tdsAmount"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"></div>
								<div class="col-sm-4"></div>
								<div class="col-sm-2"><section><label for="netAmount" class="label">Factoring Unit Cost <span class="fa fa-custINR curSymbol"></span>:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="netAmount" placeholder="Factoring Unit Cost" readonly disabled>
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>
				</div>
    			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd3">Auction Preferences</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd3">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-2"><section><label for="autoConvert" class="label">Auto Send for Auction:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="autoConvert"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
									<section class="ov-view" id="ov-autoConvert"></section>
								</div>
							</div>
						<div class="row">
							<div class="col-sm-2"><section><label for="chargeBearer" class="label">Default Platform Charge Bearer:</label></section></div>
							<div class="col-sm-4">
								<section class="select">
								<select id="chargeBearer"><option value="">Select Charge Bearer</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
								<section class="view"></section>
							</div>
						</div>
						<div id="percentSplitCharge" style="display:none">	
							<div class="row">
								<div class="col-sm-2"><section><label for="splittingPointCharge" class="label">Splitting Point:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="splittingPointCharge" onChange=""><option value="">Select splitting Point</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view view-s"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="preSplittingCharge" class="label">PreSplitting Charge Bearer:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="preSplittingCharge"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view view-s"></section>
								</div>
								<div class="col-sm-2"><section><label for="postSplittingCharge" class="label">PostSplitting Charge Bearer:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="postSplittingCharge"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
								</div>
							</div>
						</div> 	
						<div class="row" id="splitCharge" style="display:none">
							<div class="col-sm-2"><section><label for="buyerPercentCharge" class="label">Charge by Buyer Percent%:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<input type="text" id="buyerPercentCharge" placeholder="Buyer Percent">
								<b class="tooltip tooltip-top-right"></b></section>
								<section class="view view-s"></section>
							 </div>
							<div class="col-sm-2"><section><label for="sellerPercentCharge" class="label">Charge by Seller Percent%:</label></section></div>
							<div class="col-sm-4">
								<section class="input">
								<input type="text" id="sellerPercentCharge" placeholder="Seller Percent">
								<b class="tooltip tooltip-top-right"></b></section>
								<section class="view view-s"></section>
							 </div>
						</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="costBearingType" class="label">Default Cost Bearer:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="costBearingType"><option value="">Select Cost Bearer</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
									<section class="view"></section>
								</div>
							</div>
							<div id="percentSplit" style="display:none">	
								<div class="row">
									<div class="col-sm-2"><section><label for="splittingPoint" class="label">Splitting Point:</label></section></div>
									<div class="col-sm-4">
										<section class="select">
										<select id="splittingPoint" onChange=""><option value="">Select splitting Point</option></select>
										<b class="tooltip tooltip-top-right"></b><i></i></section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-2"><section><label for="preSplittingCostBearer" class="label">PreSplitting Cost Bearer:</label></section></div>
									<div class="col-sm-4">
										<section class="inline-group">
										<label class="radio"><input type=radio id="preSplittingCostBearer"><i></i><span></span>
										<b class="tooltip tooltip-top-left"></b></label>
										</section>
										<section class="view"></section>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-2"><section><label for="postSplittingCostBearer" class="label">PostSplitting Cost Bearer:</label></section></div>
									<div class="col-sm-4">
										<section class="inline-group">
										<label class="radio"><input type=radio id="postSplittingCostBearer"><i></i><span></span>
										<b class="tooltip tooltip-top-left"></b></label>
										</section>
										<section class="view"></section>
									</div>
								</div>
							</div>
							<div id="split" style="display:none">
								<div class="row">
									<div class="col-sm-2"><section><label for="buyerPercent" class="label">Cost by Buyer Percent%:</label></section></div>
									<div class="col-sm-4">
										<section class="input">
										<input type="text" id="buyerPercent" placeholder="Buyer Percent">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									 </div>
								</div>
								<div class="row">
									<div class="col-sm-2"><section><label for="sellerPercent" class="label">Cost by Seller Percent%:</label></section></div>
									<div class="col-sm-4">
										<section class="input">
										<input type="text" id="sellerPercent" placeholder="Seller Percent">
										<b class="tooltip tooltip-top-right"></b></section>
										<section class="view"></section>
									</div>
								</div>
							</div>
							<div class="row" id="bidAcceptingEntityTypeRadio">
								<div class="col-sm-2"><section><label for="bidAcceptingEntityType" class="label">Bid Accepting Entity Type:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="bidAcceptingEntityType" name="bidAcceptingEntityType" ><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
									<section class="ov-view" id="ov-chargeBearer"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="autoAccept" class="label">Auto Accept Bids:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="autoAccept"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="autoAcceptableBidTypes" class="label">Auto Acceptable Bid Types:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="autoAcceptableBidTypes"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="settleLeg3Flag" class="label">Settle Leg 3:</label></section></div>
								<div class="col-sm-4">
									<section class="inline-group">
									<label class="radio"><input type=radio id="settleLeg3Flag"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
									</section>
									<section class="view"></section>
									<section class="ov-view" id="ov-settleLeg3Flag"></section>
								</div>
							</div>
							
						</div>
					</div>
				</div>
    			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd4">Attachments</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd4">
						<div class="panel-body">
							<div class="row">
								<input type="hidden" id="instImage" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="instImage" class="label">Invoice:</label></section></div>
								<div class="col-sm-4">
									<section id='invoiceButtons' class="state-mandatory">
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="creditNoteImage" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="creditNoteImage" class="label">Credit Note:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="sup1" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="sup1" class="label">Supporting 1:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="sup2" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="sup2" class="label">Supporting 2:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="sup3" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="sup3" class="label">Supporting 3:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="sup4" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="sup4" class="label">Supporting 4:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
							</div>
							<div class="row">
								<input type="hidden" id="sup5" data-role="xuploadfield" data-file-type="INSTRUMENTS" />
								<div class="col-sm-2"><section><label for="sup5" class="label">Supporting 5:</label></section></div>
								<div class="col-sm-4">
									<section>
										<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
										<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
										<span class="upl-info"></span>
										<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
									</section>
								</div>
								<input type="hidden" id="status" placeholder="Status">
								<input type="hidden" id="statusRemarks" placeholder="Status Remarks">
								
							</div>
						</div>
					</div>
				</div>
    			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd6">Eway Details</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd6">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-6"></div>
								<div class="col-sm-2"><section><label for="ewayBillNo" class="label">E-Way Bill No:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="ewayBillNo" placeholder="E-Way Bill No">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="supplyType" class="label">Supply Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="supplyType"><option value="">Select Supply Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>	
								<div class="col-sm-2"><section><label for="docType" class="label">Document Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="docType"><option value="">Select Document Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>						
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="docNo" class="label">Document Number:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="docNo" placeholder="Document Number">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
								<div class="col-sm-2"><section><label for="docDate" class="label">Document Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="docDate" placeholder="Document Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>	
							<div class="row">								
								<div class="col-sm-2"><section><label for="fromPincode" class="label">From Pincode:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="fromPincode" placeholder="From Pincode">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
								<div class="col-sm-2"><section><label for="toPincode" class="label">To Pincode:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="toPincode" placeholder="To Pincode">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="transMode" class="label">Mode of Transport:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="transMode"><option value="">Select Mode of Transport</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="transporterName" class="label">Transporter Name:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="transporterName" placeholder="Transporter Name">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="transporterId" class="label">Transporter Id:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="transporterId" placeholder="Transporter Id">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
								<div class="col-sm-2"><section><label for="transDocNo" class="label">Transporter Doc No:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="transDocNo" placeholder="Transporter Doc No">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-2"><section><label for="transDocDate" class="label">Transporter Doc Date:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<i class="icon-append fa fa-clock-o"></i>
									<input type="text" id="transDocDate" placeholder="Transporter Doc Date" data-role="datetimepicker">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
								<div class="col-sm-2"><section><label for="vehicleNo" class="label">Vehicle No:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="vehicleNo" placeholder="Vehicle No">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
						</div>
					</div>
				</div>
    			<div class="panel panel-default" style="display:none" id="termsAndConditions">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd5">Terms and Conditions</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd5">
						<div class="panel-body">
			    			<div class="row">
								<div class="col-sm-12"><section><label for="" class="label">Please enter suitable reason/remarks</label></section></div>
							</div>
							<div class="row">
								<div class="col-sm-12">
									<section class="input">
										<input type="text" id="reason1" placeholder="Remarks">
										<b class="tooltip tooltip-top-left">Reason for Submitting</b>
									</section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-12">
									<label class="checkbox"><input type=checkbox id="agreeTAndC1"><i></i><span><a href="javascript:toggleTandCDisplay1();">I AGREE TO THE TERMS & CONDITIONS</a></span> <b class="tooltip tooltip-top-left">I Agree</b></label>
								</div>
							</div>
							<div class="row" id="dispTAndC1" style="display:none">
								<div class="col-sm-12">
									<h3>TERMS & CONDITIONS </h3>
									<ol type="i">
									<li>The invoice underlying the product/service has not been uploaded to RXIL-TReDS platform in past. </li>
									<li>The invoice underlying the product/service has not been factored at any TReDS platform or financed elsewhere.</li>
									<li>The Seller falls under MSME category as on date.</li>
									</ol>
								</div>
							</div>
						</div>
					</div>
				</div>
				
 				<div class="panel panel-default" style="display:none" id="panAgreement">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd7">Purchaser Agreement</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd7">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-12">
									<label class="checkbox"><input type=checkbox id="agreement"><i></i><span><a href="javascript:;" id="viewAgreement">CLICK WRAP AGREEMENT</a></span> <b class="tooltip tooltip-top-left">CLICK WRAP AGREEMENT</b></label>
								</div>
							</div>
						</div>
					</div>
				</div>	
				
				<div class="panel panel-default"  id="customFields">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a class="accordion-toggle collapsed" class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd8">Custom Fields</a>
						</h3>
					</div>
					<div class="panel-collapse collapse" id="accrd8">
						<div class="panel-body">
							<div class="row">
								<div class="col-sm-2" hidden><section><label for="cfId" class="label">CFID:</label></section></div>
								<div class="col-sm-4" hidden>
									<section class="input">
									<input type="text" id="cfId" placeholder="Custom Fields Id">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
							</div>
							<div id="divCustomfields" class="row">
								
							</>
						</div>
					</div>
				</div>	
    			
    		
    		</div>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<span id='pFormInfo'></span>
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveSubmit style="display:none"><span class="fa fa-save"></span> Save & Submit</button>
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
			</div>
			<!--   <input type="hidden" id="factorMaxEndDateTime" name="factorMaxEndDateTime"> -->
    		</fieldset>
    	</div>
    	</div>
    	<!-- frmMain -->
    	
	<div class="modal fade" id="mdlHistoryFilter" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;History Filter</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmHistoryFilter">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="id" class="label">Instrument Id:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="id" placeholder="Instrument Id">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="instNumber" class="label">Invoice Number:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="instNumber" placeholder="Invoice Number">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="fuId" class="label">Factoring Unit:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="fuId" placeholder="Factoring Unit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row state-S" id="purchaserRow">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4"><section><label for="purchaserRef" class="label">Buyer Reference:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="purchaserRef" placeholder="Buyer Reference">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row state-P" id="supplierRow">
					<div class="col-sm-4"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4"><section><label for="supplierRef" class="label">Seller Reference:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="supplierRef" placeholder="Seller Reference">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>

				<div class="row">
					<div class="col-sm-4"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="groupFlag" class="label">Group Flag:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="groupFlag"><option value="">Select Group Flag</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnFilter><span class="fa fa-filter"></span> Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg" id=btnHistFilterClr>Clear Filter</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
		</div>
	</div>
	</div></div></div>
    	
	<div class="modal fade" id="modelAgreement" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;TREDS PURCHASER AGREEMENT</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box">
					<fieldset>
                        			<div class="row" id="dispAgreement">
						</div>
					</fieldset>
				</div>
			</div>
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
					<th width=135px> ID <input type=checkbox class="chkSelectAll" onclick="selectAll()"></th>
					<th > Invoice No. </th>
					<th >Invoice Amount </th>
					<th >Cash Discount Amount</th>
					<th> Adj Amount</th>
					<th> TDS Amount</th>
					<th>Net Amount</th>
					<th width=80px > </th>
				</tr></thead>
				<tbody>
		{{#each splitlist}}
				<tr>
					<td><input type="checkbox" class="sub_chk" data-id="{{id}}"/>{{id}}</td>
					<td>{{instNumber}}</td>
					<td>{{amt}}</td>
					<td>{{cashdiscountAmt}}</td>
					<td>{{adjAmt}}</td>
					<td>{{tdsAmt}}</td>
					<td>{{netAmt}}</td>
					<td><button type="button" id='btnViewInst' onclick="viewClubbedInstrument({{id}})">View</button></td>
				</tr>
		{{/each}}
				<tr style="font-weight:bold">
					<td></td>
					<td></td>
					<td>{{splitsummary.amt}}</td>
					<td>{{splitsummary.cashdiscountAmt}}</td>
					<td>{{splitsummary.adjAmt}}</td>
					<td>{{splitsummary.tdsAmt}}</td>
					<td>{{splitsummary.netAmt}}</td>
					<td></td>
				</tr>
				<tr>
					<td><button type="button" id='btnRemove' onclick="removeFromClubbing({{parentId}})">Remove</button></td>
					<td><button type="button" id='btnDownloadGroupCSV' onclick="downloadGroupCsv({{parentId}})" class="fa fa-download" >Download CSV</button></td>
				</tr>
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
	<script id="tplMessage" type="text/x-handlebars-template">
		<table class="table">
			<thead><tr><th>Action</th><th>Remarks</th></tr></thead>
			<tbody>
			{{#each this}}
				
                                <tr>
                                                <td {{#if error}} style="color : red" {{/if}}>{{act}}</td>
                                                <td {{#if error}} style="color : red" {{/if}}>{{rem}}</td>
                                </tr>
			{{/each}}
			</tbody>
		</table>
	</script>

	<script id="tplCustomfields" type="text/x-handlebars-template">	
{{#each inputParams}}
	{{#if dataSetType}}
						<div class="col-sm-2"><section><label for="{{name}}" class="label">{{label}}:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="{{name}}"><option value="">Select {{label}}</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
	{{else}}
		{{#ifIn dataType '<%=BeanFieldMeta.DataType.DATETIME.toString()%>' '<%=BeanFieldMeta.DataType.DATE.toString()%>' '<%=BeanFieldMeta.DataType.TIME.toString()%>'}}
						<div class="col-sm-2"><section><label for="{{name}}" class="label">{{label}}:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<i class="icon-append fa fa-clock-o"></i>
							<input type="text" id="{{name}}" placeholder="{{label}}" data-role="datetimepicker">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
		{{else}}
						<div class="col-sm-2"><section><label for="{{name}}" class="label">{{label}}:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="{{name}}" placeholder="{{label}}">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
		{{/ifIn}}
	{{/if}}
{{/each}}
</script>

    	
	<script type="text/javascript">
	var dueDateHash={};
	var mEntityType=null;
	var lPuragree=false;
	var lAgreementHtml="";
    //
	var HISTORY_INDEX = 10;
	var histFilterForm$ = null, histFilterForm = null;
	var MAIN_RESOURCE = "inst";
	//	
	function fillSupplierPurchaser(){
		$.ajax({
            url: 'pursuplnk/lov',
            type: 'GET',
            success: function( pObj, pStatus, pXhr) {
            	purSupLink = pObj;
            	var lSup=[],lPur=[];
            	var lSupMap={},lPurMap={};
            	$.each(purSupLink,function(pIndex,pValue){
            		if (lSupMap[pValue.supplier]==null) {
            			lSupMap[pValue.supplier]=true;
            			var lDisp=pValue.supplier===loginData.domain?pValue.supName:(pValue.purchaserSupplierRef + '(' + pValue.supName + ')');
            			lSup.push({text:lDisp, value:pValue.supplier})
            		}
            		if (lPurMap[pValue.purchaser]==null) {
            			lPurMap[pValue.purchaser]=true;
            			var lDisp=pValue.purchaser===loginData.domain?pValue.purName:(pValue.supplierPurchaserRef + '(' + pValue.purName + ')');
            			lPur.push({text:lDisp, value:pValue.purchaser})
            		}
            	});
            	$.each([lSup,lPur],function(pIdx,pVal){
            		pVal.sort(function(a,b){
            			return a.text.toUpperCase().localeCompare(b.text.toUpperCase());
            		});
            	});
            	var lSupFld=mainForm.getField('supplier');
            	lSupFld.options.dataSetType='STATIC';
            	lSupFld.options.dataSetValues=lSup;
            	lSupFld.init();
            	var lPurFld=mainForm.getField('purchaser');
            	lPurFld.options.dataSetType='STATIC';
            	lPurFld.options.dataSetValues=lPur;
            	lPurFld.init();
            	console.log("fill called");
            },
        	error: errorHandler,
        });
	}
	
	function attachEvents(){
		$('#frmMain #supplier').on('change', function() {
			populateLoc('supplier','supClId',null);
			setPurSupLink(true,true);
		});
		$('#frmMain #supClId').on('change', function() {
			var lSupplier=mainForm.getField('supplier').getValue();
			var lPurchaser=mainForm.getField('purchaser').getValue();
			var lId=mainForm.getField('id').getValue();
			var lSupClId=mainForm.getField('supClId').getValue();
			populateInstrumentKeys(lSupplier,lPurchaser,lId,lSupClId);
		});
		$('#frmMain #purClId').on('change', function() {
			var lPurchaser=mainForm.getField('purchaser').getValue();
			var lPurClId=mainForm.getField('purClId').getValue();
			hasAccessToLocationsKey(lPurchaser,lPurClId);
		});
		$('#frmMain #purchaser').on('change', function() {
			populateLoc('purchaser','purClId',null);
			setPurSupLink(true,true);
			var lSupplier=mainForm.getField('supplier').getValue();
			var lPurchaser=mainForm.getField('purchaser').getValue();
			getCustomFields(lPurchaser,null);
			var lId=mainForm.getField('id').getValue();
			var lSupClId=mainForm.getField('supClId').getValue();
			populateInstrumentKeys(lSupplier,lPurchaser,lId,lSupClId);
    		
		});
		$('#amount, #adjAmount, #tdsAmount, #haircutPercent, #cashDiscountPercent').on('blur', function() {
			calculateCDandDeductions();
		});
		$('#goodsAcceptDate, #instDate, #creditPeriod, #supplier, #enableExtension').on('blur', function() {
			var lSupplier=mainForm.getField('supplier').getValue();
			var lPurchaser=mainForm.getField('purchaser').getValue();
			var lDate1=mainForm.getField('goodsAcceptDate').getValue();
			var lDate2=mainForm.getField('instDate').getValue();
			var lPeriod=mainForm.getField('creditPeriod').getValue();
			var lPeriod2=mainForm.getField('extendedCreditPeriod').getValue();
			var lExtension=mainForm.getField('enableExtension').getValue();
			if(lExtension==null)lExtension = "";
			var lDate=(lDate1!=null&&lDate1!="")?lDate1:lDate2;
			mainForm.getField('goodsAcceptDate').setValue(lDate);
			if ((lDate!=null&&lDate!="")&&(lPeriod!=null&&lPeriod!="")&&(lSupplier!=null&&lSupplier!="")) {
				var lKey=lSupplier+'^'+lDate+'^'+lPeriod+'^'+lExtension;
				var lDates=dueDateHash[lKey];
				if (lDates != null) {
	            	mainForm.getField('instDueDate').setValue(lDates.instDueDate);
	            	mainForm.getField('statDueDate').setValue(lDates.statDueDate);
	            	mainForm.getField('extendedDueDate').setValue(lDates.extendedDueDate);
	            	return;
				}
				var lData={supplier:lSupplier,goodsAcceptDate:lDate,creditPeriod:lPeriod,extendedCreditPeriod:lPeriod2,enableExtension:lExtension,purchaser:lPurchaser};
				$.ajax( {
		            url: 'inst/duedate',
		            type: 'POST',
		            data:JSON.stringify(lData),
		            success: function( pObj, pStatus, pXhr) {
		            	dueDateHash[lKey]=pObj;
		            	mainForm.getField('instDueDate').setValue(pObj.instDueDate);
		            	mainForm.getField('statDueDate').setValue(pObj.statDueDate);
		            	mainForm.getField('extendedDueDate').setValue(pObj.extendedDueDate);
		            },
		        	error: errorHandler,
					complete: function() {
					}
		        });
			}
		});
		$('#frmMain #salesCategory').on('change', function() {
			onChangeSalesCategory();
		});
        $('.panel-group').on('focus.collapse.data-api', '[data-toggle=collapse]', function(e) {
           	var $this = $(this),
                href, target = $this.attr('data-target') || e.preventDefault() || (href = $this.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') //strip for ie7
                ,
                option = null
                $(target).collapse('toggle') 
        });
		$('#frmMain #currency').on('change', function() {
			onCurrencyChange();
		});
		
		$('#autoAccept, #autoAccept1, #autoAccept2 ').on('click', function() {
			if(mainForm.getField('supplier').getValue()){
				var lDisabled = $(this).prop('value') == "<%=AppConstants.AutoAcceptBid.Disabled.getCode()%>";
		    	mainForm.enableDisableField(['autoAcceptableBidTypes'],!lDisabled,lDisabled);
			}
		});
		$('#frmMain #costBearer').on('change', function(){
			onCostBearingChange();
			setAutoAcceptBid();
		});
		$('#btnSaveSubmit').on('click', function() {
			var lTAndC=$('#agreeTAndC1').prop('checked');
			var lAgreement=$('#agreement').prop('checked')
			var lPurchaser=(mainForm.getField('purchaser').getValue()==loginData.domain);
			
			if(!lTAndC) {
				alert("Please select I agree to the Terms and Conditions.");
				return;
			}
			if(lPurchaser && !lPurHasCheckers && !lAgreement){
				alert("Please select click wrap agreement.");
				return;
			}
			$('#statusRemarks').prop('value', $('#reason1').prop('value'));
			$('#status').prop('value', '<%=InstrumentBean.Status.Submitted.getCode()%>');
			$('#btnSave').trigger("click");
		});
		$('#frmMain #bidAcceptingEntityTypeRadio input').on('change', function(){
			onBidAcceptingEntityTypeChange();
		});
		$('#frmMain #enableExtension').on('change', function(){
			onExtensionChange();
		});
		
		//
 $('.nav-tabs a').on('shown.bs.tab', function(event){
		    var lRef1 = $(event.target).attr('href');         // active tab
		    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
		    var lTabIdx = parseInt(lRef1.substring(4));
		    if (lRef2)
		    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
		    $('.btn-group'+lTabIdx).removeClass('hidden');
		    activeTab = lRef1;
		});
		var lHistConfig = {
				"fields": [
						{
							"name": "id",
							"fieldType":"PRIMARY",
							"dataType":"INTEGER",
						},
						{
							"name": "fuId",
							"fieldType":"PRIMARY",
							"dataType":"INTEGER",
						},
						{
							"name":"purchaser",
							"label":"Buyer",
							"dataType":"STRING",
							"maxLength": 30,
							"dataSetType":"RESOURCE",
							"dataSetValues":"appentity/purchasers"
						},
						{
							"name":"purchaserRef",
							"label":"Buyer Short Name",
							"dataType":"STRING",
							"maxLength": 30
						},
						{
							"name":"supplier",
							"label":"Seller",
							"dataType":"STRING",
							"maxLength": 30,
							"dataSetType":"RESOURCE",
							"dataSetValues":"appentity/suppliers"
						},
						{
							"name":"supplierRef",
							"label":"Seller Short Name",
							"dataType":"STRING",
							"maxLength": 30
						},
						{
							"name":"status",
							"label":"Status",
							"dataType":"STRING",
							"maxLength": 3,
							"dataSetType": "STATIC",
							"dataSetValues":[{"text":"Expired", "value":"EXP"},{"text":"Leg 3 Generated", "value":"LEG3"},{"text":"Factored", "value":"FACT"},{"text":"Leg 1 Settled", "value":"L1SET"},{"text":"Leg 1 Failed", "value":"L1FAIL"},{"text":"Leg 2 Settled", "value":"L2SET"},{"text":"Leg 2 Failed", "value":"L2FAIL"}],
							"desc":"Current status of obligation."
						},
						{
							"name":"instNumber",
							"label":"Invoice Number",
							"dataType":"STRING",
							"maxLength": 30
						},
						{
							"name":"groupFlag",
							"label":"Grouping",
							"dataType":"STRING",
							"maxLength": 1,
							"dataSetType": "STATIC",
							"dataSetValues":[{"text":"Grouped", "value":"Y"}],
							"desc":"Grouped Instruments"
						}
				]
			};
		histFilterForm$ = $('#frmHistoryFilter').xform(lHistConfig);
		histFilterForm = histFilterForm$.data('xform');

		$('#btnFilter').on('click', function() { 
			$('#mdlHistoryFilter').modal('hide');
			crudInstrument.searchHandler();
		});
		
		$('#btnHistFilterClr').on('click', function() {
			histFilterForm.setValue(null);
			$('#filter').attr('style','color:black');
		});
		$('#ewayInv').on('click', function() {
			var lId = $("#ewayInv").attr("eway-id");
			var lFileName = $("#ewayInv").attr("eway-fileName");
			window.location.href= "upload/EWAYINVOICE/"+lFileName;
		});
		
	}
	function viewClubbedInstruments(pCrudInstrument,pTplClubbedInstruments){
 		var lRows=pCrudInstrument.getSelectedRows();
 		if(lRows.length!=1){
 			if(lRows.length<1) alert("Please select a row.");
 			if(lRows.length>1) alert("Please select only one row.");	
 		}else{
 			var lData = null;
 			var lView = false;
 			$.each(lRows, function(pIndex,pValue) {
 				lData = pValue.data().id;
 				lView = (pValue.data().groupFlag == 'Yes');
			});
 			if (lView){
 				var lUrl= 'inst/viewclubbeddetails/' + lData  ;
 	 			$.ajax({
 	 	        url: lUrl,
 	 	        type: 'GET',
 	 	        success: function( pObj, pStatus, pXhr) { 
 	 	        	$('#mdlClubbedInvoice .modal-content').html(pTplClubbedInstruments(pObj));
 	 	        	showModal($('#mdlClubbedInvoice'));
 	 	        },
 	 	    	error: errorHandler
 	 	    	});
 			}else{
 				alert("Please select a grouped instrument. ");
 			}
 			
 		}
	}
	function setPurSupLink(pSet,pCheckInstCreation) {
		var lSupVal=mainForm.getField('supplier').getValue();
    	var lPurVal=mainForm.getField('purchaser').getValue();
    	var lSupFlg=(loginData.domain===lSupVal);
    	var lPurFlg=(loginData.domain===lPurVal);
    	var lPurSupLink;
    	if ((lSupFlg||lPurFlg)) {
    		$.each(purSupLink,function(pIndex,pValue){
    			if ((pValue.supplier===lSupVal)&&(pValue.purchaser===lPurVal)) {
    				if (lPurFlg && pCheckInstCreation){
    					checkAccessOnInstrumentCreation(mainForm, pValue.instrumentCreation,'supClId',['btnSave','btnSaveSubmit'],'<%=PurchaserSupplierLinkBean.InstrumentCreation.Supplier.getCode()%>');
    				}
    				if (lSupFlg && pCheckInstCreation){
    					checkAccessOnInstrumentCreation(mainForm, pValue.instrumentCreation,'purClId',['btnSave','btnSaveSubmit'],'<%=PurchaserSupplierLinkBean.InstrumentCreation.Purchaser.getCode()%>',mainForm);
    				}
    				lPurSupLink = pValue;
    				return false;
    			}
    		});
    	}
    	mainForm.enableDisableField(['autoAccept'],lSupFlg,false);
    	mainForm.enableDisableField(['autoAcceptableBidTypes'],lSupFlg,false);
    	
    	mainForm.enableDisableField(['costBearingType'],lPurFlg,!lPurFlg);
    	mainForm.enableDisableField(['chargeBearer'],lPurFlg,!lPurFlg);
    	mainForm.enableDisableField(['settleLeg3Flag'],lPurFlg,!lPurFlg);
		mainForm.enableDisableField(['bidAcceptingEntityType'],lPurFlg,!lPurFlg);
    	mainForm.alterField(['costBearingType'], lPurFlg, false);
    	mainForm.alterField(['chargeBearer'], lPurFlg, false);
    	mainForm.alterField(['settleLeg3Flag'], lPurFlg, false);
		mainForm.alterField(['bidAcceptingEntityType'], lPurFlg, false);
		
		mainForm.enableDisableField(['buyerPercent'],false,false);
		mainForm.enableDisableField(['splittingPoint'],false,false);
		mainForm.enableDisableField(['salesCategory'],false,false);

		//mainForm.enableDisableField(['haircutPercent'],false,false);

		//mainForm.enableDisableField(['cashDiscountPercent','cashDiscountValue'],false,false);
		mainForm.enableDisableField(['cashDiscountValue'],false,false);

		if (lPurSupLink!=null) {
    		var lTmp = "";
			lFlds = ['creditPeriod','costBearingType','chargeBearer','settleLeg3Flag','extendedCreditPeriod','bidAcceptingEntityType','buyerPercent','sellerPercent','preSplittingCostBearer','splittingPoint','postSplittingCostBearer','salesCategory','autoConvert','autoAccept','autoAcceptableBidTypes','haircutPercent','cashDiscountPercent','cashDiscountValue','buyerPercentCharge','sellerPercentCharge','preSplittingCharge','splittingPointCharge','postSplittingCharge'];
    		if(mainForm.mode!="insert"){
    			lFlds = ['creditPeriod','costBearingType','chargeBearer','settleLeg3Flag','extendedCreditPeriod','bidAcceptingEntityType','buyerPercent','sellerPercent','preSplittingCostBearer','splittingPoint','postSplittingCostBearer','salesCategory','autoConvert','autoAccept','autoAcceptableBidTypes','buyerPercentCharge','sellerPercentCharge','preSplittingCharge','splittingPointCharge','postSplittingCharge'];
    		}
    		$.each(lFlds,function(pIndex, pValue){
    			var lFld = mainForm.getField(pValue);
    			if ((lFld.getValue()==null) || (lFld.getValue=='') || pSet)
    				lFld.setValue(lPurSupLink[pValue]);
    		});
    		var lInvMandatory=(lPurSupLink['invoiceMandatory']=='Y')?true:false;
    		$('#frmMain #invoiceButtons').prop('class', lInvMandatory?"state-mandatory":"");
    		var lHaircutPresent=(lPurSupLink['haircutPercent']==null || lPurSupLink['haircutPercent']=='' || lPurSupLink['haircutPercent']==0)?false:true;
    		mainForm.enableDisableField(['adjAmount'],!lHaircutPresent,false);    		
    	}
		onCostBearingChange();
		setAutoAcceptBid();
		var lFld = mainForm.getField('extendedCreditPeriod').getValue();
		mainForm.enableDisableField(['enableExtension'],(lPurFlg && (lFld!=null)),false);
		mainForm.enableDisableField(['extendedCreditPeriod'],(lPurFlg && (lFld!=null)),false);
		if(lPurFlg){
			var lCBType = mainForm.getField('costBearingType').getValue();
			//if purchaser then disable the costBearingType appropriately on basis of settings in the PSLink
			var lDisableType = null;
			if(lCBType == '<%=CostBearingType.Percentage_Split.getCode()%>'){
				lDisableType = '<%=CostBearingType.Periodical_Split.getCode()%>';
			}
			else if(lCBType == '<%=CostBearingType.Periodical_Split.getCode()%>'){
				lDisableType = '<%=CostBearingType.Percentage_Split.getCode()%>';
			}
			$("input[name='costBearingType']",'#frmMain').each(function() {
				$(this).prop('disabled',($(this).val()==lDisableType));
			});
		}
		onBidAcceptingEntityTypeChange();
		onExtensionChange();
		if (lPurSupLink!=null) {
			var lInWorkFlow = lPurSupLink['inWorkFlow'];
			if(lInWorkFlow != null && lInWorkFlow == '<%=CommonAppConstants.Yes.Yes.getCode()%>' && 
					(mainForm.getField('id').getValue() == null || mainForm.getField('id').getValue() == '') ){
				alert('WARNING : New Instrument cannot be created since the Buyer-Seller Link is Work Flow.');
			}
		}
		mainForm.enableDisableField(['autoConvert'],false,false);
		//last minitue request to disable all b/s link changes
		var lFields = ["autoConvert", "costBearingType", "buyerPercent","splittingPoint", "preSplittingCostBearer", "postSplittingCostBearer","chargeBearer","settleLeg3Flag","bidAcceptingEntityType",'buyerPercentCharge','sellerPercentCharge','preSplittingCharge','splittingPointCharge','postSplittingCharge'];
		mainForm.enableDisableField(lFields, false, false);
		calculateCDandDeductions();
	}
	function populateLoc(pEntityField, pLocField, pLoc) {
		var lEntity=mainForm.getField(pEntityField).getValue();
		var lField=mainForm.getField(pLocField);
		var lOptions=lField.getOptions()
		var	lActiveOnly=true; 
		if (!lEntity) lOptions.dataSetValues=[];
		else lOptions.dataSetValues = "companylocation/settleactivelov?aecode="+lEntity+"&activeOnly="+lActiveOnly;
		lField.init();
		if (pLoc) lField.setValue(pLoc);
	}
	function onChangeSalesCategory() {
		//var lEnable = (mainForm.getField('salesCategory').getValue().startsWith('SERV'));
		//mainForm.enableDisableField('tdsAmount',lEnable,false);
	}
	
	function onCurrencyChange() {
		var lSymbol = mainForm.getField("currency").getValue();
		$(".curSymbol").attr("class","fa fa-cust"+ lSymbol +" curSymbol")
	}
	
	function preSaveCommonValidation(){
		if(mainForm.getField('autoAccept').getValue()!=null &&
				mainForm.getField('autoAccept').getValue()!="<%=AutoAcceptBid.Disabled.getCode()%>" ){
			if(mainForm.getField('autoAcceptableBidTypes').getValue()==null ||
					mainForm.getField('autoAcceptableBidTypes').getValue()==""){
				alert("Auto accept Bid Types not selected.");
				return false;
			}
		}
		if($("#ewayInv").attr("eway-id")){
			return true;	
		}
    	if( $('#frmMain #invoiceButtons').prop('class') == "state-mandatory"  && mainForm.getField('instImage').getValue() == '' ){
			alert("Invoice mandatory.");
			return false;
    	}
		return true;
	}
	function setAutoAcceptBid(){
		var lcostBearingType = mainForm.getField('costBearingType').getValue();
		var lEnable=false;
		if(mEntityType == 'P' && lcostBearingType == "<%=AppConstants.CostBearingType.Buyer.getCode()%>"){
			lEnable=true;
		}else if(mEntityType == 'S' && lcostBearingType == "<%=AppConstants.CostBearingType.Seller.getCode()%>"){
			lEnable=true;
		}
		mainForm.enableDisableField(['autoAccept'],lEnable,false);
		mainForm.enableDisableField(['autoAcceptableBidTypes'],lEnable,false);
		if(!lEnable && (mainForm.getField('autoAccept').getValue()==null||mainForm.getField('autoAccept').getValue()=='')){
			mainForm.getField('autoAccept').setValue("<%=AppConstants.AutoAcceptBid.Disabled.getCode()%>");
		}
	}
	
	function onCostBearingChange(){
		var lSplitPercent=false;
		var lSplitPeriodical=false;
		var lSplitPercentCharge=false;
		var lSplitPeriodicalCharge=false;
		var lCostBearingType=mainForm.getField('costBearingType').getValue();
		
		lSplitPercent = (lCostBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodical = (lCostBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		//
		$('#frmMain #split').attr('style',lSplitPercent?'':'display:none');
		$('#frmMain #percentSplit').attr('style',lSplitPeriodical?'':'display:none');
		//since everthing is disabled temporarily keeping this check commented.
		//mainForm.enableDisableField(['bidAcceptingEntityType'],(mEntityType == 'P'&&(lSplitPercent||lSplitPeriodical)),false);
		if(!(lSplitPercent || lSplitPeriodical)){
			mainForm.getField('bidAcceptingEntityType').setValue(lCostBearingType);
		}
		
		var lChargeBearingType=mainForm.getField('chargeBearer').getValue(); 
		
		lSplitPercentCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Percentage_Split.getCode()%>");
		lSplitPeriodicalCharge = (lChargeBearingType == "<%=AppConstants.CostBearingType.Periodical_Split.getCode()%>");
		//
		$('#frmMain #splitCharge').attr('style',lSplitPercentCharge?'':'display:none');
		$('#frmMain #percentSplitCharge').attr('style',lSplitPeriodicalCharge?'':'display:none');
		//
		mainForm.alterField("buyerPercentCharge", lSplitPercentCharge, false);
		mainForm.alterField("sellerPercentCharge", lSplitPercentCharge, false);
		mainForm.alterField("splittingPointCharge", lSplitPeriodicalCharge, false);
		mainForm.alterField("preSplittingCharge", lSplitPeriodicalCharge,false);
		mainForm.alterField("postSplittingCharge", lSplitPeriodicalCharge,false);
		
	}
	
	function onBidAcceptingEntityTypeChange(){
		var lBidAcceptingEntityType=$('input[name="bidAcceptingEntityType"]:checked', '#frmMain').val();
		mainForm.enableDisableField(['autoAccept'], (mEntityType == lBidAcceptingEntityType) ,false);
		mainForm.enableDisableField(['autoAcceptableBidTypes'], (mEntityType == lBidAcceptingEntityType) ,false);
		
	}
	function onExtensionChange(){
		var lExtentionEnabled=mainForm.getField('enableExtension').getValue();
		mainForm.enableDisableField(['extendedCreditPeriod'],lExtentionEnabled,false);
	}

	function calculateCDandDeductions(){
		var lAmount = mainForm.getField('amount').getValue();
		if (lAmount != null) {
			var lHaircutPercent = mainForm.getField('haircutPercent').getValue();
			lHaircutPercent = lHaircutPercent * 1.0;
			var lHaircut = ((lHaircutPercent!=null)?(roundToXDigits(lAmount*lHaircutPercent/100,2)): 0);
			var lAdjAmount = ((lHaircutPercent!=null && lHaircutPercent!=0.00)?lHaircut : mainForm.getField('adjAmount').getValue());
			lAdjAmount = lAdjAmount * 1.0;
			lAdjAmount = lAdjAmount.toFixed(2);
			mainForm.getField('adjAmount').setValue(lAdjAmount);
			//
			var lCDPercent = mainForm.getField('cashDiscountPercent').getValue();
			lCDPercent = lCDPercent * 1.0;
			var lCDValue = ((lCDPercent!=null)?(roundToXDigits(lAmount*lCDPercent/100,2)) : 0);
			lCDValue = lCDValue * 1.0;
			mainForm.getField('cashDiscountValue').setValue(lCDValue.toFixed(2));
			//
			var lTdsAmount = mainForm.getField('tdsAmount').getValue();
			if (lAdjAmount != null) lAmount -= lAdjAmount;
			lAmount -= lCDValue;
			if (lTdsAmount != null) lAmount -= lTdsAmount;
			mainForm.getField('netAmount').setValue(lAmount.toFixed(2));
			mainForm.enableDisableField(['adjAmount'],(lHaircut==0),false);
		}
	}
	function roundToXDigits(value, digits) {
	    if(!digits){
	        digits = 2;
	    }
	    value = value * Math.pow(10, digits);
	    value = Math.round(value);
	    value = value / Math.pow(10, digits);
	    return value;
	}
	 function getAgreementHtml(){
      	$.ajax({
              url: 'puraggacc/clickwraphtml?domain='+loginData.domain+'&skipLog=true',
      				type : 'GET',
    				dataType: "html",
      				success : function(pObj, pStatus, pXhr) {
      					lAgreementHtml = pObj;
      				},
      				error : function(pObj, pStatus, pXhr) {
      					oldAlert('Error');
      				}
      			});
      }
    function viewClubbedInstrument(pId){
  		showRemote('instview?id='+pId, 'modal-xl', true,'Instrument Details');
  	}
    
 	function showHistoryFilter(){
		//histFilterForm.setValue(historyFilters); //set the last vaild filter.
		showModal($('#mdlHistoryFilter'));		
	}
 	
 	function selectAll(){
		if ($('.chkSelectAll').is(':checked')) {
			$(".sub_chk").prop( "checked", true );
		}else{
			$(".sub_chk").prop( "checked", false );
		}
	}
 	
 	function checkAccessOnInstrumentCreation(pForm ,pStatus ,pField ,pToggleBtnArray ,pCheckStatus){
 		if ( pStatus == pCheckStatus ){
 			pForm.enableDisableField(pField,false,true);
 			$.each(pToggleBtnArray,function(pIndex,pValue){
 				$('#'+pValue).hide();
 				if (pIndex==0){
 					$('#pFormInfo').show();
 					$('#pFormInfo').text("Instrument Creation blocked in purchaser supplier link.");
 				}
 			});
			alert("Instrument Creation blocked in purchaser supplier link.");
			return false;
		} else {
			pForm.enableDisableField(pField,true,false);
			$.each(pToggleBtnArray,function(pIndex,pValue){
 				$('#'+pValue).show();
 				$('#pFormInfo').hide();
				$('#pFormInfo').text("Instrument Creation blocked in purchaser supplier link.");
 			});
		}
 	}
 	
 	function hasAccessToLocationsKey(pPurchaser,pPurClId){
		lData = {};
 		lData['purchaserCode'] = pPurchaser;
 		lData['purClId'] = pPurClId;
		$.ajax( {
	        url: "instrumentcreationkeys/hasaccess",
	        type: "POST",
	        async:false,
	        data: JSON.stringify(lData),
	        success: function( pObj, pStatus, pXhr) {
	        	if (pObj==false){
	    			$('#creationKeys').hide();
		    	}else{
		    		$('#creationKeys').show();
		    	}
	        },
	        error: function( pObj, pStatus, pXhr) {

	        },
	        complete: function() {
	        }
		});
	}

	</script>    	

	