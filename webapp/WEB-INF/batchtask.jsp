<!DOCTYPE html>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.treds.master.bean.AuctionCalendarBean"%>
<%@page import="com.xlx.treds.master.bean.ConfirmationWindowBean"%>
<html>
    <head>
        <title>TREDS | Batch Tasks</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/homePageStyle.css" rel="stylesheet">
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Batch Tasks" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
    

	<div class="content" id="contBatchTasks">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Batch Tasks</h1>
				</div>
			</div>
		<div class="col-sm-12 col-md-6 col-lg-6">
		<div class="xform box" >
			<fieldset class="box-body">
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnEOD>Perform EOD</button>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnGenLeg3>Generate Leg3 for Expired Factoring Units</button>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenObliShift>Adjust Holiday Obligations & Statutory/Maturity dates</button>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnArchive>Perform Archive</button>
					</div>
				</div>
				<hr>

				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenNOAMailer>Perform NOA Mailing</button>
					</div>
				</div>
				<hr>

				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenGenFinSettleFiles>Generate Fin. Settlement Files</button>
					</div>
				</div>
				<hr>

				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenGenFinMISFiles>Generate Fin. MIS Report Files</button>
					</div>
				</div>
				<hr>

				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenObliDue>Send mail Obligations due</button>
					</div>
				</div>
				<hr>
				
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnRBI>RBI Report</button>
					</div>
				</div>
				<hr>
				
				<div class="row" style="display:none">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnTransferFilesSFTP>Transfer Files (SFTP)</button>
					</div>
				</div>
				
				<div class="row" style="display:none">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnOpenBulkMailer>Bulk Mailing</button>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnPaymentAdvice>Payment Advice</button>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-sm-12 text-center">
						<button type="button" class="btn btn-info btn-lg" id=btnPurSupBank>Purchaser Supplier Bank Notification</button>
					</div>
				</div>
				<hr>
			</fieldset>
		</div>
		</div>
		<div class="col-sm-12 col-md-6 col-lg-6">
		    <div class="content" id="divData">
		    </div>
		    
		</div>
		
	</div>
		<!-- frmSearch -->
	
	<div class="modal fade" id="mdlShiftObli" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Shift dates from Holiday</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmShiftObli">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="holidayDate" class="label">Holiday Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="holidayDate" placeholder="From Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnObliShift><span class="fa fa-share"></span> Shift</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
		
	<div class="modal fade" id="mdlSendNOAMail" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Send NOA Mail</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmSendNOAMail">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="settlementDate" class="label">Business Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="settlementDate" placeholder="Business Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnNOAMailer><span class="fa fa-share"></span> Send Mail</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
	
		
	<div class="modal fade" id="mdlGenFinSettleFiles" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Generate Financier Settlement Files</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmFinSettleFiles">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="businessDate" class="label">Business Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="businessDate" placeholder="Business Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="leg" class="label">Settlement Leg:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="leg"><option value="L2">Leg 2</option><option value="L1">Leg 1</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnGenFinSettleFiles><span class="fa fa-share"></span> Generate Files</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
	
		<div class="modal fade" id="mdlPaymentAdvice" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Payment Advice</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmPaymentAdvice">
				<div class="row">
					<div class="col-sm-4"><section><label for="paySettlementDate" class="label">Settlement Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="paySettlementDate" placeholder="Settlement Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnGeneratePaymentAdvice onclick="getPaymentAdvice()"><span class="fa fa-share"></span> Generate Payment Advice</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
	
			<div class="modal fade" id="mdlpurSupBankNoti" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Purchaser Supplier Bank Notification</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmpurSupBank">
				<div class="row">
					<div class="col-sm-4"><section><label for="purSupBankNotifyDate" class="label">Notification Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="purSupBankNotifyDate" placeholder="Notification Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnSendPurSupNotification onclick="sendPurSupNotification()"><span class="fa fa-share"></span> Send Bank notification</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
	
	<div class="modal fade" id="mdlGenFinMISFiles" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>Generate Financier MIS Files</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmFinMISFiles">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="startObligationDate" class="label">Start Obligation Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="startObligationDate" placeholder="Start Obligation Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="endObligationDate" class="label">End Obligation Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="endObligationDate" placeholder="End Obligation Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnGenFinMISFiles><span class="fa fa-share"></span> Generate Files</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>

		
		<div class="modal fade" id="mdlGenFinMISFiles" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Generate Financier Settlement Files</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmFinMISFiles">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="businessDate" class="label">Business Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="businessDate" placeholder="Business Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="leg" class="label">Settlement Leg:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="leg"><option value="L2">Leg 2</option><option value="L1">Leg 1</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnGenFinMISFiles><span class="fa fa-share"></span> Generate Files</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>	
		
	<div class="modal fade" id="mdlSendBulkMail" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Send Bulk Mail</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmSendBulkMail">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="bulkMailTemplates" class="label">Bulk Mail Templates:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="bulkMailTemplates"><option value="">Select Bulk Mail Templates</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="businessDate" class="label">Business Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="businessDate" placeholder="Business Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnBulkMail><span class="fa fa-share"></span> Send Bulk Mail</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
	
	<div class="modal fade" id="mdlSendObliDue" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;Send L2 Next 5 Days Obligations</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform" id="frmSendObliDue">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="obliDue" class="label">Settlement Leg:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="obliDue">
								<option value="sendObliDue">Leg 1 and 2 Oblig Due</option>
								<option value="sendL1TransDetails">Leg 1 Trans Details</option>
								<option value="sendL2Next5DayObliMail">Leg 2 Next 5 Days</option>
						</select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="businessDate2" class="label">Business Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="businessDate2" placeholder="Business Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<footer>
				<div class="btn-groupX pull-right">
					<button type="button" class="btn btn-info btn-lg btn-primary" id=btnObliDue><span class="fa fa-share"></span> Send Mail</button>
					<button type="button" class="btn btn-info-inverse btn-lg btn-Close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
				</div>
			</footer>
		</div>
	</div>
	</div></div></div>
	
		<div class="modal fade" id="mdlRBI" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
	<div class="modal-header"><span>&nbsp;History Filter</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	<div class="modal-body">
		<div class="xform box" id="frmRBI">
			<fieldset>
				<div class="row">
					<div class="col-sm-4"><section><label for="monthAndYear" class="label">Month & Year:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="monthAndYear" placeholder="Month & Year" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnRBISearch><span class="fa fa-save"></span> Download</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
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
   	
	<script id="tplDashboard" type="text/x-handlebars-template">
			<div class="row">
				<!-- Left col -->
						<section class="drag-container" id="cont1"> 
						{{#if aucCal}}<div id=divAucCal class="box box-danger drag-element">
								<div class="box-header">
									<div class="panel panel-default">
										<div class="panel-heading drag-thumb">
											<h3 class="panel-title">Auction Calendar</h3>
										</div>
										<div class="panel-body">
											<div class="row calendar-row">
												<div class="col-sm-6">
													<label>Date <span>{{aucCal.date}}</span></label>
													<label>Status <span 
													{{#ifCond aucCal.status '==' "<%=AuctionCalendarBean.Status.Closed%>"}}
														style="color:red"
													{{/ifCond}}
													{{#ifCond aucCal.status '==' "<%=AuctionCalendarBean.Status.Bidding%>"}}
														style="color:green"
													{{/ifCond}}
													{{#ifCond aucCal.status '==' "<%=AuctionCalendarBean.Status.Pending%>"}}
														style="color:orange"
													{{/ifCond}}
													>{{aucCal.status}}</span></label>
												</div>
												<div class="col-sm-6">
													<label>Start Time <span>{{aucCal.bidStartTimeTime}} &nbsp;&nbsp;</span></label>
													<label>End Time <span>{{aucCal.bidEndTimeTime}}  &nbsp;&nbsp;</span></label>
												</div>
											</div>
											<div class="table-responsive">
												<table class="rwd-table table responsive">
													<thead>
													<tr>
														<th>Confirmation Window</th>
														<th align="center"  style="text-align: center;">Status</th>
														<th style="text-align: right;">Settlement Date</th>
													</tr>
													</thead>	
													<tbody>
														{{#each aucCal.confWinList}}
															<tr>
																<td>{{confStartTimeTime}} - {{confEndTimeTime}}</td>
																<td align="center"  
																	{{#ifCond status '==' "<%=ConfirmationWindowBean.Status.Closed%>"}}
																		style="color:red !important"
																	{{/ifCond}}
																	{{#ifCond status '==' "<%=ConfirmationWindowBean.Status.Open%>"}}
																		style="color:green !important"
																	{{/ifCond}}
																	{{#ifCond status '==' "<%=ConfirmationWindowBean.Status.Pending%>"}}
																		style="color:orange !important"
																	{{/ifCond}}
																		>{{status}}</td>
																<td align="right">{{settlementDate}}</td>
															</tr>
														{{/each}}
													</tbody>
												</table>
											</div>
										</div>
                                	</div>
                                </div>
                            </div>{{/if}}
                        </section>
					</div>
	</script>   	
   	<script id="tplMessage" type="text/x-handlebars-template">
		<table class="table">
			<thead><tr><th>Action</th><th>Remarks</th></tr></thead>
			<tbody>
{{#each this}}
	<tr><td>{{act}}</td><td>{{rem}}</td></tr>
{{/each}}
			</tbody>
		</table>
	</script>
	<script type="text/javascript">
		var tplDashboard;
		var tplMessage;
		$(document).ready(function() {
			tplMessage = Handlebars.compile($('#tplMessage').html());
			$('#btnEOD').on('click',function() {
				batchTask('eod','eod',null);
			});
			$('#btnArchive').on('click',function() {
				batchTask('archive','archive',null);
			});
			$('#btnGenLeg3').on('click',function() {
				batchTask('genLeg3','generate leg3 for expired FUs',null);
			});
			$('#btnOpenObliShift').on('click',function() {
				openObligationShift();
			});
			$('#btnOpenNOAMailer').on('click',function() {
				openNOASendMail();
			});
			$('#btnOpenGenFinSettleFiles').on('click',function() {
				openGenFinSettleFiles();
			});
			$('#btnOpenGenFinMISFiles').on('click',function() {
				openGenFinMISFiles();
			});
			$('#btnOpenObliDue').on('click',function() {
				openSendObliDue();
			});
			$('#btnOpenBulkMailer').on('click',function() {
				openBulkMailer();
			});
			$('#btnObliShift').on('click',function() {
				var lData = "{'name':'holidayDate','dataType':'DATE','value':'" + $('#holidayDate').val() + "'}"
				batchTask('oblishift','obligation and stautory/maturity date shift', lData);
			});
			$('#btnObliDue').on('click',function() {
				var lData = "{'name':'settlementDate','dataType':'DATE','value':'" + $('#businessDate2').val() + "'}"
				batchTask($('#obliDue').val(),$('#obliDue option:selected').text(),lData);
			});
			$('#btnGenFinSettleFiles').on('click',function() {
				var lData = " { ";
				lData += " 'businessDate' : '" + $('#businessDate').val() + "' ";
				lData += ", ";
				lData += " 'leg' : '" + $('#leg').val() + "' ";
				lData += " }";
				batchTask('generateFinLegFiles','Generate Fin Settlement Files',lData);
			});
			$('#btnGenFinMISFiles').on('click',function() {
				var lData = " { ";
				lData += " 'startObligationDate' : '" + $('#startObligationDate').val() + "' ";
				lData += ", ";
				lData += " 'endObligationDate' : '" + $('#endObligationDate').val() + "' ";
				lData += " }";
				batchTask('generateFinMISFiles','Generate Fin MIS Files',lData);
			});
			$('#frmSendBulkMail #btnBulkMail').on('click',function() {
				var lData = " { ";
				lData += " 'businessDate' : '" + $('#businessDate').val() + "' ";
				lData += ", ";
				lData += " 'template' : '" + $('#bulkMailTemplates').val() + "' ";
				lData += " }";
				batchTask('sendBatchMail','<p>Send Bulk Mail : ' + $('#bulkMailTemplates option:selected').text() + ' for ' + $('#businessDate').val() + '.</p>' ,lData);
			});
			$('#btnNOAMailer').on('click',function() {
				var lData = "{'name':'settlementDate','dataType':'DATE','value':'" + $('#settlementDate').val() + "'}"
				batchTask('sendNOAMail','Send NOA Mails',lData);
			});
			$('#btnTransferFilesSFTP').on('click',function() {
				var lData = " { } ";
				batchTask('transferFiles','Transfer Files : ' ,lData);
			});
			
			$('#holidayDate')['datetimepicker']({name:'holidayDate',dataType:'DATE',notNull:true});
			$('#settlementDate')['datetimepicker']({name:'settlementDate',dataType:'DATE',notNull:true});
			$('#businessDate2')['datetimepicker']({name:'businessDate2',dataType:'DATE',notNull:true});
			$('#businessDate')['datetimepicker']({name:'businessDate',dataType:'DATE',notNull:true});
			$('#startObligationDate')['datetimepicker']({name:'startObligationDate',dataType:'DATE',notNull:true});
			$('#endObligationDate')['datetimepicker']({name:'endObligationDate',dataType:'DATE',notNull:true});
			$('#billingDate')['datetimepicker']({name:'billingDate',dataType:'DATE',notNull:true});
			$('#paySettlementDate')['datetimepicker']({name:'paySettlementDate',dataType:'DATE',notNull:true});
			$('#purSupBankNotifyDate')['datetimepicker']({name:'purSupBankNotifyDate',dataType:'DATE',notNull:true});
			//
			tplDashboard=Handlebars.compile($('#tplDashboard').html());
			reloadAuctionCalendar();
			//reloadBatchTemplates($('#mdlSendBulkMail'));
			var lRBIConfig = {
					"fields": [
						{
							"name":"monthAndYear",
							"label":"Month And Year",
							"dataType":"DATE",
							"format":"yyyy-MM"
						}
					]
				};
			RBIForm$ = $('#frmRBI').xform(lRBIConfig);
			RBIForm = RBIForm$.data('xform');
			
			$('#btnRBI').on('click', function() {
				var lModal$ = $('#mdlRBI');
	        	showModal(lModal$);
			});
			
			$('#btnRBISearch').on('click', function() {
				getRBIReport();
			});
			
			$('#btnPaymentAdvice').on('click', function() {
				paymentAdvice();
			});
			$('#btnPurSupBank').on('click',function(){
				purSupBankNotification();
			});
   		});
		function batchTask(pUrl,pMsg,pData) {
			confirm("You are about to perform " + pMsg + ". Are you sure?",null,null,function(pConf){
				if (pConf) {
					$('#contBatchTasks button').prop('disabled',true);
					$.ajax( {
			            url: 'batchtask/' + pUrl,
			            type: 'POST',
			            data:pData,
			            success: function( pObj, pStatus, pXhr) {
			            	alert(tplMessage(pObj));
			            	reloadAuctionCalendar();
			            },
			        	error: errorHandler,
			        	complete: function() {
			        		$('#contBatchTasks button').prop('disabled',false);
			        	}
			        });
				}
			});
		}
		function openObligationShift() {
        	var lModal$ = $('#mdlShiftObli');
        	showModal(lModal$);
		}
		function openNOASendMail() {
        	var lModal$ = $('#mdlSendNOAMail');
        	showModal(lModal$);
		}
		function openGenFinSettleFiles() {
        	var lModal$ = $('#mdlGenFinSettleFiles');
        	showModal(lModal$);
		}
		function openGenFinMISFiles() {
        	var lModal$ = $('#mdlGenFinMISFiles');
        	showModal(lModal$);
		}
		function openSendObliDue() {
        	var lModal$ = $('#mdlSendObliDue');
        	showModal(lModal$);
		}
		function openBulkMailer() {
        	var lModal$ = $('#mdlSendBulkMail');
        	//reloadBatchTemplates(lModal$);
        	showModal(lModal$);
		}
		function reloadAuctionCalendar() {
			var lData=[];
			$.ajax( {
		        url: "dashboard/auCal",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
		        	data=pObj;
		        	$('#divData').html(tplDashboard(data));
		        },
		        error: errorHandler,
		        complete: function() {

		        }
			});
		}
		function reloadBatchTemplates(pModal) {
			var lData=[];
			$.ajax( {
		        url: "batchtask/bulkMailTemplates",
		        type: "GET",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
		        	data=pObj;
		        	var lCombo$ = pModal.find('#bulkMailTemplates');
		        	lCombo$.empty();
		        	lCombo$.append($("<option> </option>").val('').html('Select Bulk Mail Templates'));
		             $.each(data, function () {
		            	 lCombo$.append($("<option> </option>").val(this['value']).html(this['text']));
		             });
		        },
		        error: errorHandler,
		        complete: function() {
		        }
			});
		}
		
		function getRBIReport(){
			var lData = RBIForm.getField('monthAndYear').getValue();
			if (lData==null || lData==''){
				alert('Please select month and year.');
			}else{
				downloadFile('batchtask/rbiReport',null,JSON.stringify({"monthAndYear":lData}));
			}
		}
		
		function paymentAdvice() {
        	var lModal$ = $('#mdlPaymentAdvice');
        	showModal(lModal$);
		}
		
		function purSupBankNotification() {
        	var lModal$ = $('#mdlpurSupBankNoti');
        	showModal(lModal$);
		}
		
		function getPaymentAdvice(){
			var lSettlementDate = $('#paySettlementDate').val();
			var lData = {};
			lData['settlementDate'] = lSettlementDate;
			$.ajax( {
		        url: "v1/aggpaymentadvice",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
					alert("Please check your mail");
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#mdlPaymentAdvice').modal('hide');
		        }
			});
		}
		
		function sendPurSupNotification(){
			var lNotificationDate = $('#purSupBankNotifyDate').val();
			var lData = {};
			lData['purSupBankNotifyDate'] = lNotificationDate;
			$.ajax( {
		        url: "batchtask/sendpursupnoti",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
					alert("Please check your mail");
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#mdlpurSupBankNoti').modal('hide');
		        }
			});
		}
		
		</script>
    </body>
</html>