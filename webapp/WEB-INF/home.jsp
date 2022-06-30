<!DOCTYPE html>
<%@page import="com.xlx.treds.master.bean.AuctionCalendarBean"%>
<%@page import="com.xlx.treds.master.bean.ConfirmationWindowBean"%>
<%
String lSec = request.getParameter("sec");
%>
<html>
    <head>
        <title>TREDS | Home</title>
        <%@include file="includes1.jsp" %>
		<link href="../css/homePageStyle.css" rel="stylesheet">
    </head>
    <body class="page-body">
	    <jsp:include page="header1.jsp">
			<jsp:param name="title" value="Welcome To RXIL" />
			<jsp:param name="desc" value="The Online Factoring Platform" />
		 </jsp:include>
    <div class="content" id="divData">
    </div>
			
	<%@include file="footer1.jsp" %>
	
<style type="text/css">
th.thDC {
	align:center;
	text-align:right;
}
td {
	text-align:right;
}
td.tdrh {
	text-align:left;
	color: black !important;
	font-size: 14px !important;
}
td.tdhl {
	background-color:#00FF00;
	text-align:right;
}
.panel-title1{
	position: relative;
    padding: 14px;
    margin: 0;
    color: white;
    background-color: darkorange;
    border-color: #ddd;
    font-size: 22px;
    font-weight: bold !important;
}
.tableheadmain{
	font-weight: bold !important;
	font-size: 17px;
}
.tablehead{
	background-color: #0e62c7 !important;
    font-size: 17px;
    width: 16%;
	color: white !important;
	text-transform: capitalize !important;
}
.total{
	color: black !important;
	font-weight: bold !important;
	font-size: 14px !important;
	cursor:pointer;
}
.tdDC{
	color: black !important;
	font-size: 14px !important;
}
</style>
	<script id="script-resource-12" src="../assets/js/highcharts.js"></script>
	<script id="tplPurchLimit" type="text/x-handlebars-template">
	</script>
	<script id="tplMyBuy" type="text/x-handlebars-template">
					{{#ifHasAccess "widmybuy-view"}}<div class="box-header">
						<div class="panel panel-default">
								<div class="panel-heading drag-thumb">
									<h3 class="panel-title">My Buyers</h3>
									<a href="factunitfin" class="ViewDetails"> View Details</a>
								</div>
								<div class="panel-body"  style="width:100%; min-height:310px; height:310px; overflow:hidden; margin:0 auto;">
									<div>
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th>Buyer</th>
													<th style="text-align:right">Set Limit<i class="fa fa-rupee"></i></th>
													<th style="text-align:right">UnUtil Limit<i class="fa fa-rupee"></i></th>
													<th style="text-align:right">Units</th>
													<th style="text-align:right">Amt<i class="fa fa-rupee"></i></th>
													<th style="text-align:right">Min Tenor</th>
													<th style="text-align:right">Max Tenor</th>
												</tr>
											</thead>
											<tbody>
											{{#each data}}<tr>
												<td><a href="factunitfin?purchaser={{pur}}&&tab=0" title="{{purName}}">{{pur}}</a></td>
												<td style="text-align:right">{{lmt}}</td>
												<td style="text-align:right">{{bal}}</td>
												<td style="text-align:right">{{cnt}}</td>
												<td style="text-align:right">{{amt}}</td>
												<td style="text-align:right">{{min}}</td>
												<td style="text-align:right">{{max}}</td>
											</tr>{{/each}}
											</tbody>	
										</table>
									</div>
								</div>
								<div class="pull-right">
									<ul class="pagination">
										<li class="paginate_button previous"> 
											<a style="text-align:left" href="javascript:page({{prev}})"><i class="fa  fa-angle-double-left"></i> Prev </a>
										</li>
										<li class="paginate_button next"> 
											<a style="text-align:left" href="javascript:page({{next}})"> Next <i class="fa  fa-angle-double-right"></i></a>
										</li>
									</ul>
								</div>
							</div>
						</div>{{/ifHasAccess}}
	</script>
   	<script id="tplOthBuy" type="text/x-handlebars-template">
					{{#ifHasAccess "widothbuy-view"}}<div class="box-header">
						<div class="panel panel-default">
								<div class="panel-heading drag-thumb">
									<h3 class="panel-title">Other Buyers</h3>
									<a href="factunitfin" class="ViewDetails">View Details</a>
								</div>
								<div class="panel-body"  style="width:100%; min-height:310px; height:310px; overflow:hidden; margin:0 auto;">
									<div>
										<table class="rwd-table table responsive">
											<thead>
												<tr>
                                                <th>Buyer</th>
                                                <th style="text-align:right">No of Units</th>
                                                <th style="text-align:right">Amt <i class="fa fa-rupee"></i></th>
                                                <th style="text-align:right">Min Tenor</th>
                                                <th style="text-align:right">Max Tenor</th>
												</tr>
											</thead>
											<tbody>
											{{#each data}}<tr>
                                                <td><a href="factunitfin?purchaser={{pur}}&&tab=1">{{pur}}</a></td>
                                                <td style="text-align:right">{{cnt}}</td>
                                                <td style="text-align:right">{{amt}}</td>
                                                <td style="text-align:right">{{min}}</td>
                                                <td style="text-align:right">{{max}}</td>
											</tr>{{/each}}
											</tbody>
										</table>
									</div>
								</div>
								<div class="pull-right">
									<ul class="pagination">
										<li class="paginate_button previous"> 
                                        	<a class="paginate_button previous" href="javascript:page({{prev}})"><i class="fa  fa-angle-double-left"></i> Prev</a>
										</li>
										<li class="paginate_button next"> 
                                        	<a class="paginate_button next" href="javascript:page({{next}})">Next <i class="fa  fa-angle-double-right"></i></a>
										</li>
									</ul>
								</div>
							</div>
						</div>{{/ifHasAccess}}
	</script>
   	<script id="tplDashboard" type="text/x-handlebars-template">
{{#ifHasAccess "appentity-view"}}
                    {{#if tredSum}}<div class="col-md-12"><div class="col-md-4 col-sm-6">
						<div class="biscuit_panel panel panel-default navyblue-color">
							<div class="panel-body">
								<a href="appentity?type=YNN"><i class="droplets"><img src="../assets/images/droplet4.png" alt=""></i></a>
								<h2>{{tredSum.sup}}{{^if tredSum.sup}}0{{/if}}</h2>
                                <small>Sellers</small>                                
							</div>
						</div>
						<div class="biscuit_panel panel panel-default navyblue-color">
							<div class="panel-body">
								<a href="appentity?type=NYN"><i class="droplets"><img src="../assets/images/droplet4.png" alt=""></i></a>
								<h2>{{tredSum.pur}}{{^if tredSum.pur}}0{{/if}}</h2>
                                <small>Buyers</small>
							</div>
						</div>
						<div class="biscuit_panel panel panel-default navyblue-color">
							<div class="panel-body">
								<a href="appentity?type=NNY"><i class="droplets"><img src="../assets/images/droplet4.png" alt=""></i></a>
								<h2>{{tredSum.fin}}{{^if tredSum.fin}}0{{/if}}</h2>
                                <small>Financers</small>                                
							</div>
						</div>
					</div>
					<div class="col-md-4 col-sm-6">
						<div class="biscuit_panel panel panel-default persiangreen-color" style="display:none">
							<div class="panel-body">
								<a href="inst?t=3"><i class="droplets"><img src="../assets/images/droplet4.png" alt=""></i></a>
								<h2>{{tredSum.instCount}}{{^if tredSum.instCount}}0{{/if}}</h2>
                                <small>Instruments Factored</small>
							</div>
						</div>
					</div>
			</div>
{{/if}}
{{/ifHasAccess}}
		 {{#if instSum}}<div class="row" id="divSPSum">
			{{#ifHasAccess "widinstmk-view"}}<div class="col-md-4 col-sm-12">
				<div class="panel panel-default">
					<div class="panel-heading no-bottom-border">
						<h3 class="panel-title">Instrument Details</h3>
					</div>
					<div class="panel-body"  style="width:100%; height:150px; overflow:hidden; margin:0 auto;" >
						<div>
							<div id="instDetails""></div>
						</div>
					</div>
				</div>
			</div>{{/ifHasAccess}}


					{{#ifHasAccess "widinstmk-view"}}<div class="col-md-4 col-sm-6">
						<div class="biscuit_panel panel panel-default navyblue-color">
							<div class="panel-body">
								<a href="inst?t=0"><i class="droplets"><img src="../assets/images/droplet1.png" alt=""></i></a>
								<h2>{{instSum.DRFT}}{{^if instSum.DRFT}}0{{/if}}</h2>
                                <small class="inbox_text">Inbox</small>                                
							</div>
						</div>
						<div class="biscuit_panel panel panel-default persiangreen-color">
							<div class="panel-body">
								<a href="factunitsp"><i class="droplets"><img src="../assets/images/droplet2.png" alt=""></i></a>
								<h2>{{fuSum.RDY}}{{^if fuSum.RDY}}{{/if}}</h2>
                                <small>Ready for Auction</small>
							</div>
						</div>
					</div>{{/ifHasAccess}}

					<div class="col-md-4 col-sm-6">
						{{#ifHasAccess "widinstch-view"}}<div class="biscuit_panel panel panel-default red-color">
							<div class="panel-body">
								<a href="instchk?t=0"><i class="droplets"><img src="../assets/images/droplet3.png" alt=""></i></a>
								<h2> {{instSum.SUB_1}}{{^if instSum.SUB_1}}0{{/if}}</h2>
                                <small class="inbox_text">Checker Pending</small>                                
							</div>
						</div>{{/ifHasAccess}}
						{{#ifHasAccess "widinstco-view"}}<div class="biscuit_panel panel panel-default purple-color">
							<div class="panel-body">
								<a href="instcntr?t=0"><i class="droplets"><img src="../assets/images/droplet4.png" alt=""></i></a>
								<h2> {{instSum.CHKAPP_1}}{{^if instSum.CHKAPP_1}}0{{/if}}</h2> 
                                <small>Counter Pending</small>
							</div>
						</div>{{/ifHasAccess}}
					</div>
					<div class="col-md-4 col-sm-6">
						{{#ifHasAccess "widinstcoch-view"}}<div class="biscuit_panel panel panel-default red-color">
							<div class="panel-body">
								<a href="instcntrchk?t=0"><i class="droplets"><img src="../assets/images/droplet3.png" alt=""></i></a>
								<h2> {{instSum.COUCHKPEN_1}}{{^if instSum.COUCHKPEN_1}}0{{/if}}</h2>
                                <small class="inbox_text">Counter Checker Pending</small>                                
							</div>
						</div>{{/ifHasAccess}}
					</div>
		</div>{{/if}}



		 {{#if purchLimit}}<div class="row" id="divPurchLimit">
			{{#ifHasAccess "widinstmk-view"}}<div class="col-md-4 col-sm-12">
				<div class="panel panel-default">
					<div class="panel-heading no-bottom-border">
						<h3 class="panel-title">Financier Limits</h3>
					</div>
					<div class="panel-body"  style="width:100%; height:150px; overflow:hidden; margin:0 auto;" >
						<div>
							<div id="purcLimitDetails""></div>
						</div>
					</div>
				</div>
			</div>{{/ifHasAccess}}
			</div>{{/if}}

                    <div class="row">
					{{#ifHasAccess "widauccal-view"}}
                        <section class="col-lg-6 drag-container" id="cont1"> 
                            {{#if finBuyers}}<div id=divFinBuyers0 class="box box-primary drag-element">
                            </div>
                            <div id=divFinBuyers1 class="box box-primary drag-element">
                            </div>{{/if}}

                            {{#if aucCal}}<div id=divAucCal class="box box-danger drag-element">
								<div class="box-header">
									<div class="panel panel-default">
										<div class="panel-heading drag-thumb">
											<h3 class="panel-title">Auction Calendar</h3>
											<a href="auccal?date={{aucCal.date}}" class="ViewDetails">View Details</a>
										</div>
										<div class="panel-body"  style="width:100%;  min-height:310px; height:310px; overflow:hidden; margin:0 auto;">
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
														<th class="thDC">Status</th>
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
											<div class="row calendar-row col-sm-9">
												<label>Next Business Date <span>{{nextBusinessDate}}</span></label>
											</div>
										</div>
                                	</div>
                                </div>
                            </div>{{/if}}
                        </section>{{/ifHasAccess}}
						
					{{#ifHasAccess "widnextauccal-view"}}
                        <section class="col-lg-6 drag-container" id="cont2"> 
                            {{#if finBuyers}}<div id=divFinBuyers0 class="box box-primary drag-element">
                            </div>
                            <div id=divFinBuyers1 class="box box-primary drag-element">
                            </div>{{/if}}

                            {{#if aucCalNext}}<div id=divAucCalNext class="box box-danger drag-element">
								<div class="box-header">
									<div class="panel panel-default">
										<div class="panel-heading drag-thumb">
											<h3 class="panel-title">Auction Calendar</h3>
											<a href="auccal?date={{aucCalNext.date}}" class="ViewDetails">View Details</a>
										</div>
										<div class="panel-body"  style="width:100%;  min-height:310px; height:310px; overflow:hidden; margin:0 auto;">
											<div class="row calendar-row">
												<div class="col-sm-6">
													<label>Date <span>{{aucCalNext.date}}</span></label>
													<label>Status <span 
													{{#ifCond aucCalNext.status '==' "<%=AuctionCalendarBean.Status.Closed%>"}}
														style="color:red"
													{{/ifCond}}
													{{#ifCond aucCalNext.status '==' "<%=AuctionCalendarBean.Status.Bidding%>"}}
														style="color:green"
													{{/ifCond}}
													{{#ifCond aucCalNext.status '==' "<%=AuctionCalendarBean.Status.Pending%>"}}
														style="color:orange"
													{{/ifCond}}
													>{{aucCalNext.status}}</span></label>
												</div>
												<div class="col-sm-6">
													<label>Start Time <span>{{aucCalNext.bidStartTimeTime}} &nbsp;&nbsp;</span></label>
													<label>End Time <span>{{aucCalNext.bidEndTimeTime}}  &nbsp;&nbsp;</span></label>
												</div>
											</div>
											<div class="table-responsive">
												<table class="rwd-table table responsive">
													<thead>
													<tr>
														<th>Confirmation Window</th>
														<th class="thDC">Status</th>
														<th style="text-align: right;">Settlement Date</th>
													</tr>
													</thead>	
													<tbody>
														{{#each aucCalNext.confWinList}}
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
                        </section>{{/ifHasAccess}}

                       {{#ifHasAccess "widoblirec-view"}}{{#if obgRecv}}  <section class="col-lg-6 drag-container" id="cont3"> 
                          <div id=divObgRecv class="box box-primary drag-element">
					<div class="box-header" >
						<div class="panel chart_block panel-default">
							<div class="panel-heading drag-thumb">
								<h3 class="panel-title">Obligations - Receivables</h3>
							</div>
							<div class="panel-body" style="width:100%; min-height:320px; height:320px; overflow:hidden; margin:0 auto">
								<div class="chart-item-bg">
                                <div class="tabbable" style="align:center"style="width:100%; height:320px;" >
                                    <div class="tab-content">
                                    <div id="tabOR0" class="tab-pane"><div id="tabOblRec0" class="tab-pane-content"></div></div>
                                    <div id="tabOR1" class="tab-pane active"><div id="tabOblRec1" class="tab-pane-content"></div></div>
                                    <div id="tabOR2" class="tab-pane"><div id="tabOblRec2" class="tab-pane-content"></div></div>
                                    <div id="tabOR3" class="tab-pane"><div id="tabOblRec3" class="tab-pane-content"></div></div>
                                    </div>
                                    <ul class="nav nav-tabs" style="margin-top:-160px;">
                                    <li><a href="#tabOR0" data-toggle="tab">Prev Week</a></li>
                                    <li class="active"><a href="#tabOR1" data-toggle="tab">Today</a></li>
                                    <li><a href="#tabOR2" data-toggle="tab">Tomorrow</a></li>
                                    <li><a href="#tabOR3" data-toggle="tab">Next 30</a></li>
                                    </ul>
                                </div>
								</div>
							</div>
						</div>
					</div>
				</div>
				</section>{{/if}}{{/ifHasAccess}}

                            {{#ifHasAccess "widoblipay-view"}}{{#if obgPay}}<section class="col-lg-6 drag-container" id="cont4">
					<div id=divObgPay class="box box-primary drag-element">
					<div class="box-header">
						<div class="panel chart_block panel-default">
							<div class="panel-heading  drag-thumb">
								<h3 class="panel-title">Obligations - Payables</h3>
							</div>
							<div class="panel-body"  style="width:100%; min-height:320px;  height:320px; overflow:hidden; margin:0 auto;">
								<div class="chart-item-bg">
                                <div class="tabbable" style="align:center">
                                    <div class="tab-content">
                                    <div id="tabOP0" class="tab-pane"><div id="tabOblPay0" class="tab-pane-content"></div></div>
                                    <div id="tabOP1" class="tab-pane active"><div id="tabOblPay1" class="tab-pane-content"></div></div>
                                    <div id="tabOP2" class="tab-pane"><div id="tabOblPay2" class="tab-pane-content"></div></div>
                                    <div id="tabOP3" class="tab-pane"><div id="tabOblPay3" class="tab-pane-content"></div></div>
                                    </div>
                                    <ul class="nav nav-tabs" style="margin-top:-160px;">
                                    <li><a href="#tabOP0" data-toggle="tab">Prev Week</a></li>
                                    <li class="active"><a href="#tabOP1" data-toggle="tab">Today</a></li>
                                    <li><a href="#tabOP2" data-toggle="tab">Tomorrow</a></li>
                                    <li><a href="#tabOP3" data-toggle="tab">Next 30</a></li>
                                    </ul>
                                    </div>
								</div>
							</div>
						</div>
					</div>
                   </div>
				</section>{{/if}}{{/ifHasAccess}}


                            {{#if chkInst}}<div id=divChkInst class="box box-danger drag-element">
                                <div class="box-header drag-thumb">
                                    <i class="fa fa-cloud"></i>
                                    <h3 class="box-title">My Approvals</h3>  
                                </div><!-- /.box-header -->
                                <div class="box-body">
				                    <div class="row">
				                        {{#each chkInst}}<div class="col-sm-4"><a href="instchk?t={{@index}}">
				                            <div class="small-box bg-{{b}}">
				                                <div class="inner"><h3>{{c}}</h3><h4><i class="fa fa-rupee"></i> {{v}}</h4></div>
				                                <div class="icon"><i class="fa fa-file-o"></i></div>
				                                <span class="small-box-footer">{{l}}</span>
				                            </div></a>
				                        </div>{{/each}}
				                    </div>  
                                </div>
                            </div>{{/if}}
                        </div>

<div class="row">
{{#ifHasAccess "widrecentfact-view"}}{{#if finFactored}}
                        <section class="col-lg-6 drag-container" id="cont5"> 
			<div id=divRecentFact class="box box-primary drag-element">
					<div class="box-header">
						<div class="panel panel-default">
							<div class="panel-heading  drag-thumb">
								<h3 class="panel-title">RECENTLY Factored INSTRUMENTS</h3>
							</div>
							<div class="panel-body no_padding"  style="width:100%;  min-height:310px; height:310px; overflow:hidden; margin:0 auto;">
								<div class="row no_padding no_margin biscuit_bx">
									<div class="col-sm-6">
										<div class="panel navyblue-color">
											<div class="panel-body">
												<small>My Factored</small><a href="factunitfin?t=2"><i class="droplets"><img src="../assets/images/factored_instru1.png" alt=""></i></a>
												<h2>{{finFactored.myFact}}</h2>
							                               </div>
										</div>
									</div>						
									<div class="col-sm-6">
										<div class="panel persiangreen-color">
											<div class="panel-body">
								                    	  <small>Other Factored</small><a href="factunitfin?t=3"><i class="droplets"><img src="../assets/images/factored_instru2.png" alt=""></i></a>
											<h2>{{finFactored.otherFact}}</h2>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
</section>
{{/if}}{{/ifHasAccess}}
</div>

{{#ifHasAccess "widmonitor-view"}}

				{{#if hasRegData}}
			<div class="row">
				<section class="col-lg-12 " id="cont6"> 
					<div id=divRegEntities class="box box-danger ">
						<div class="box-header">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h3 class="panel-title1">Registered Entities</h3>
								</div>
								<div class="panel-body"  style="width:100%;  min-height:310px;  margin:0 auto;">
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Registered Sellers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each REG_MSMESTATUS}}
												<tr {{#if @last}} onClick="javascript:toggleTable('REG_MSMESTATUS_toggle')"{{/if}} {{#unless @last}} class="REG_MSMESTATUS_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Registered Buyers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each REG_CONSTITUTION}}
												<tr {{#if @last}} onClick="javascript:toggleTable('REG_CONSTITUTION_toggle')"{{/if}} {{#unless @last}} class="REG_CONSTITUTION_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Registered Financiers</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each REG_FINCATEGORY}}
												<tr {{#if @last}} onClick="javascript:toggleTable('REG_FINCATEGORY_toggle')"{{/if}} {{#unless @last}} class="REG_FINCATEGORY_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}


				{{#if hasTransData}}
			<div class="row" id='invturnover' >
				<section class="col-lg-12" id="cont7"> 
					<div id=divinvturnover class="box box-danger">
						<div class="box-header">
							<div class="panel panel-default" >
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Tunover Consolidated Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%;  min-height: auto !important  margin:0 auto; ">
									<div class="row"  > <h4 align="right" style="color:black;margin-right:20px" > * Amount in Lakhs</h4></div>
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Transaction Summary</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANSCONS_TRANSSUMMARY}}
												<tr onClick="javascript:toggleTransDisplay('{{key}}')" style="cursor:pointer">
													<td  class="tdrh " id="{{key}}">{{desc}}</td>
													<td class=" {{#if curhighlight}}tdhl{{/if}}" >{{cur}} </td>
													<td class=" {{#if curmthhighlight}}tdhl{{/if}}" >{{curmth}} </td>
													<td class=" {{#if prevmthhighlight}}tdhl{{/if}}" >{{prevmth}} </td>
													<td class=" {{#if fyhighlight}}tdhl{{/if}}" >{{fy}}</td>
													<td class=" {{#if sihighlight}}tdhl{{/if}}" >{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}


				{{#if hasTransData}}
			<div class="row" id="invcountdiv">
				<section class="col-lg-12 " id="cont8"> 
					<div id=divTransInvCountSummary class="box box-danger ">
						<div class="box-header">
							<div class="panel panel-default">
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Count Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%;  min-height:310px;  margin:0 auto;">
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Sellers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_COUNT_MSMESTATUS}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_COUNT_MSMESTATUS_toggle')"{{/if}} {{#unless @last}} class="TRANS_COUNT_MSMESTATUS_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Buyers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_COUNT_CONSTITUTION}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_COUNT_CONSTITUTION_toggle')"{{/if}} {{#unless @last}} class="TRANS_COUNT_CONSTITUTION_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Financiers</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_COUNT_FINCATEGORY}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_COUNT_FINCATEGORY_toggle')"{{/if}} {{#unless @last}} class="TRANS_COUNT_FINCATEGORY_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}


				{{#if hasTransData}}
			<div class="row" id="invamountdiv">
				<section class="col-lg-12" id="cont9"> 
					<div id=divinvamount class="box box-danger">
						<div class="box-header">
							<div class="panel panel-default">
								<div class="panel-heading ">
									<h3 class="panel-title1">Transaction Invoice Tunover Summary</h3>
								</div>
								<div class="panel-body"  style="width:100%";  min-height:310px;  margin:0 auto;">
								<div class="row"  > <h4 align="right" style="color:black;margin-right:20px" > * Amount in Lakhs</h4></div>
									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Sellers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_MSMESTATUS}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_MSMESTATUS_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_MSMESTATUS_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}"  >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Buyers </th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_CONSTITUTION}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_CONSTITUTION_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_CONSTITUTION_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

									<div class="table-responsive">
										<table class="rwd-table table responsive">
											<thead>
												<tr>
													<th class="tablehead tableheadmain"> Financiers</th>
													<th class="thDC tablehead">Today</th>
													<th class="thDC tablehead">Current Month</th>
													<th class="thDC tablehead">Previous Month</th>
													<th class="thDC tablehead">This Fin Year</th>
													<th class="thDC tablehead">Since Inception</th>
												</tr>
											</thead>	
											<tbody>
											{{#each TRANS_TO_FINCATEGORY}}
												<tr {{#if @last}} onClick="javascript:toggleTable('TRANS_TO_FINCATEGORY_toggle')"{{/if}} {{#unless @last}} class="TRANS_TO_FINCATEGORY_toggle hide"{{/unless}}>
													<td class="tdrh  {{#if @last}} total{{/if}}" >{{desc}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curhighlight}} tdhl{{/if}}">{{cur}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if curmthhighlight}} tdhl{{/if}}">{{curmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if prevmthhighlight}} tdhl{{/if}}">{{prevmth}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if fyhighlight}} tdhl{{/if}}">{{fy}}</td>
													<td class="tdDC  {{#if @last}} total{{/if}} {{#if sihighlight}} tdhl{{/if}}">{{si}}</td>
												</tr>
											{{/each}}
											</tbody>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
				{{/if}}

{{/ifHasAccess}}


	</script>

<script>
	var tplDashboard,tplFinBuyers,tplPurchLimit;
	var mRec = ["Receivable", "Received","tabOblRec"]; //PieTitle, Pending/Recived
	var mPay = ["Payable", "Paid","tabOblPay"]; //PieTitle, Pending/Recived
	var mLimit = ["Total Limit", "Limit Utilised","tabpurchLimit"]; 
	var oldData=null;
	var data;
		$(function() {
			tplDashboard=Handlebars.compile($('#tplDashboard').html());
			tplFinBuyers=[];
			tplFinBuyers[0]=Handlebars.compile($('#tplMyBuy').html());
			tplFinBuyers[1]=Handlebars.compile($('#tplOthBuy').html());
			tplPurchLimit=Handlebars.compile($('#tplPurchLimit').html());
<%
if (lSec != null) {
%>			showRemote('security','modal-sm', false, 'Security Settings');
<%
}
if (!"2".equals(lSec)) {
%>
			if (loginData && loginData.login)
				reloadDashboard();
<%
}
%>
		});
		function reloadDashboard() {
			var lData=[];
			$.ajax( {
		        url: "dashboard/data",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
		        	data=pObj;
		        	//for Regulatory Dashboard
		        	buildViewModel(data, oldData);
		        	oldData=data;
		        	//
					$('#divData').html(tplDashboard(data));
		        	if (data.finBuyers) {
		        		page(0,0);
		        		page(1,0);
		        	}
		        	displayInvoiceall();

		        	//
		        	// bind drag events
					$('.drag-container').on('dragover', function(ev) {
						ev.preventDefault();
					});
					$('.drag-container').on('drop', function(ev) {
						ev.preventDefault();
						var lSrcId = ev.originalEvent.dataTransfer.getData("text");
						var lSrc$ = $('#'+lSrcId);
						var lTrg$ = $(ev.target);
						var lDest$ = lTrg$.closest('.drag-element');
						if (lDest$.length == 0)
							lDest$ = lTrg$.closest('.drag-container');
						var lSrcGrp = lSrc$.data('dragGroup');
						var lDestGrp = lDest$.data('dragGroup');
						if (((lSrcGrp==null)&&(lDestGrp==null))||(lSrcGrp===lDestGrp)) {
							if (lDest$.hasClass('drag-container')) lDest$.append(lSrc$);
							else if (lDest$.hasClass('drag-element')) lSrc$.insertBefore(lDest$);
						}
						saveLayout();
					});
					$('.drag-element .drag-thumb').on('dragstart', function(ev) {
						var lTrg$=$(ev.target).closest('.drag-element');
						var lId=lTrg$.attr('id');
						ev.originalEvent.dataTransfer.setData("text", lId);
					})
					$('.drag-element .drag-thumb').prop('draggable',true);
					loadLayout();
					makeChart(data.obgRecv,mRec,'C');
					makeChart(data.obgPay,mPay,'D');
					chartInstDetails(data.instSum);
					chartpurchLimits(data.purchLimit);
		        },
		        error: errorHandler,
		        complete: function() {
		        	$('#btnAddToWatch').prop('disabled',false);
		        }
			});
		}
		function page(pIdx,pPage) {
			var lData = data.finBuyers[pIdx];
			var lSubSet=[];
			var lPageSize=5;
			var lStart = pPage*lPageSize,lEnd=lStart+lPageSize;;
			for (var lPtr=lStart;lPtr<lEnd;lPtr++) {
				if (lPtr<lData.length) lSubSet.push(lData[lPtr]);
			}
			var lPageData={data:lSubSet};
			if (pPage>0) lPageData.prev=pIdx+','+(pPage-1);
			if (lData.length>lEnd) lPageData.next=pIdx+','+(pPage+1);
			$('#divFinBuyers'+pIdx).html(tplFinBuyers[pIdx](lPageData));
		}
		function saveLayout() {
			var lConfig=[];
			$('.drag-element').each(function(pIndex, pValue){
				var lThis$=$(this);
				var lId=lThis$.prop('id');
				var lParent=lThis$.closest('.drag-container').prop('id');
				lConfig.push([lId,lParent]);
			});
			localStorage.setItem(loginData.domain+"."+loginData.login+'.home',JSON.stringify(lConfig));
		}
		function loadLayout() {
			var lLayout=localStorage.getItem(loginData.domain+"."+loginData.login+'.home');
			if (lLayout!=null) {
				var lOrder=JSON.parse(lLayout);
				$.each(lOrder, function(pIndex,pValue){
					$('#'+pValue[1]).append($('#'+pValue[0]));
				});
			}
		}
		
		function makeChart(pObj,pTitles,pDrCr) {
			if(pObj==null)return;
			var lData = {};
			var lPending, lReceived,lTotal=10;
			var lD1,lD2;
            for(var lPtr=0; lPtr<pObj.length; lPtr++){
            	lData={};
            	lPending = 0;
            	lReceived = 0;
            	lTotal = 0;
            	if(pObj[lPtr]["a"]!=""){
            		lTotal = pObj[lPtr]["a"];
            		lPending = pObj[lPtr]["p"];
            		lReceived = pObj[lPtr]["s"];
            		lD1 = pObj[lPtr]["d1"];
            		lD2 = pObj[lPtr]["d2"];

                	lData = {type: 'pie', name: pObj[lPtr]["l"] , colorByPoint: true, innerSize: '65%',
	                			point: { events: {
	            					click: function(e) {
	            						location.href = e.point.url;
	            						e.preventDefault();
	            					}
	            				}
	            			},
    						data: [{name: 'Pending', y:lPending, drilldown: 'Pending', url:"oblig?type="+pDrCr+"&d1="+lD1+"&d2="+lD2 }, { name: pTitles[1], y:lReceived,drilldown: pTitles[1] , url:"oblig?status=SUC&type=D&d1="+lD1+"&d2="+lD2} ],
    						showInLegend: false	
    					};
        		    $('#'+pTitles[2]+lPtr).highcharts({
        				colors: ["#fcd036", "#86c653"],
        				credits: {enabled:false},
        		        exporting: {enabled: false },
        		        chart: {plotBackgroundColor: null,backgroundColor: null,height:400,plotBorderWidth: 0,plotShadow: false},
        		        tooltip: {pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},
        				plotOptions: {pie: {dataLabels: {enabled: true, distance:20,format: '<b>{point.name}</b><br/>{point.y}',style: {fontWeight: 'bold',color: 'black'}},startAngle: -90,endAngle: 90, center: ['45%', '45%']}},
        				title: {text: 'Total '+pTitles[0]+'<br/><span>'+lTotal+'</span>', align: 'center', verticalAlign: 'middle',y:-50, x:-25},
        		        series: [lData] 
        		    });
            	}
            	else{
        		    $('#'+pTitles[2]+lPtr).highcharts({credits: {enabled:false}, title:{text:'No Data', align: 'center'}, chart: {plotBackgroundColor: null,backgroundColor: null,height:400,plotBorderWidth: 0,plotShadow: false},series :[]});		
            	}
            }		
		}
		
		function chartInstDetails(pObj){
			var lData = {};
			var lInAuction=0, lAuctioned=0, lTotal=0;
			if(pObj!=null){
				if(pObj["FACUNT"]!=null)lInAuction=pObj["FACUNT"];
				if(pObj["FACT"]!=null)lAuctioned=pObj["FACT"];
				lTotal = lInAuction+lAuctioned;
			}
				
        	lData = {type: 'pie', name: lTotal , colorByPoint: 'true',innerSize: '50%',size: '45%',
        			point: { events: {
        					click: function(e) {
        						location.href = e.point.url;
        						e.preventDefault();
        					}
        				}
        			},
					data: [{name: 'In Auction', y:lInAuction, drilldown: 'In Auction', url:"inst?t=5" }, { name: 'Auctioned', y:lAuctioned,drilldown: 'Auctioned', url:"inst?t=6"}],
					showInLegend: false	
				};
        	if(lTotal>0){
    			$('#instDetails').highcharts({
    				colors: ["#fcd036", "#86c653"],
    				credits: {enabled:false},
    				allowHTML: true,
    		        exporting: {enabled: false },
    		        chart: { plotBackgroundColor: null,backgroundColor: null,plotBorderWidth: 0,plotShadow: false},
    		        tooltip: {pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},
    				plotOptions: {pie: {dataLabels: {x: 20,y: 0,enabled: true, distance:10,format: '<b>{point.name}</b><br/>{point.y}',style: {color: 'black'}},startAngle: -90,endAngle: 90, center: ['50%', '20%']}},
    				title: {text: '<br/><span>'+lTotal+'<br/> No of Instruments </span>', style: {fontWeight: 'bold',color: 'black',fontSize:'13px'}, align: 'center', verticalAlign: 'middle',y:-90},
    		        series: [lData]
    		
    		    });
        	}
        	else{
    			$('#instDetails').highcharts(null);
        	}
		}

		function chartpurchLimits(pObj){
			var lData = {};
			var lUnUtilised=0, lUtilised=0, lTotalLimit=0, lFinCount=0;
			if(pObj!=null){
				if(pObj["finCount"]!=null)lFinCount=pObj["finCount"];
				if(pObj["limit"]!=null)lTotalLimit=pObj["limit"];
				if(pObj["utilised"]!=null)lUtilised=pObj["utilised"];
				if(pObj["unUtilised"]!=null)lUnUtilised=pObj["unUtilised"];
			}
			var lUtilisedStr = "", lUnUtilisedStr = "", lTotalLimitStr="";
			lUtilisedStr = handleBarDecFormatter.formatNumber(lUtilised/100000);
			lUtilisedStr += " L";
			lUnUtilisedStr = handleBarDecFormatter.formatNumber(lUnUtilised/100000);
			lUnUtilisedStr += " L";
			lTotalLimitStr = handleBarDecFormatter.formatNumber(lTotalLimit/100000);
			lTotalLimitStr += " L";
        	lData = {type: 'pie', name: lTotalLimit , colorByPoint: 'true',innerSize: '45%',size: '43%',
					data: [{ name: 'Utilized', y:lUtilised, strAmt:lUtilisedStr}, {name: 'Un-Utilized', y:lUnUtilised, strAmt:lUnUtilisedStr}],
					showInLegend: false	
				};
        	if(lTotalLimit>0){
    			$('#purcLimitDetails').highcharts({
    				colors: ["#fcd036", "#86c653"],
    				credits: {enabled:false},
    				allowHTML: true,
    		        exporting: {enabled: false },
    		        chart: {plotBackgroundColor: null,backgroundColor: null,plotBorderWidth: 0,plotShadow: false},
    		        tooltip: {pointFormat: '{point.strAmt}: <b>{point.percentage:.2f}%</b>'},
    				plotOptions: {pie: {dataLabels: {x: 20,y: -3,enabled: true, distance:10,format: '<b>{point.name}</b><br/>{point.strAmt} [{point.percentage:.2f}%]',style: {color: 'black'}},startAngle: -90,endAngle: 90, center: ['50%', '20%']}},
    				title: {text: 'Fin('+lFinCount+')<br/>'+lTotalLimitStr, align: 'center', style: {fontWeight: 'bold',color: 'black',fontSize:'13px'},verticalAlign: 'middle',y:-90, x:0},
    		        series: [lData]
    		    });
        	}
        	else{
    			$('#purcLimitDetails').highcharts(null);
        	}
		}

		function openEntityList() {
			var lData={type:"YNN"};
			$.ajax( {
		        url: "appentity",
		        type: "POST",
		        data:JSON.stringify(lData),
		        success: function( pObj, pStatus, pXhr) {
		        	data=pObj;
		        },
		        error: errorHandler,
		        complete: function() {
		        }
			});
		}
		
		function buildViewModel(pData, pOldData){
			//check whether the Regulatory Data is available
			//do appropriately
/* 			traverse display order
				lkey=type^category
				mewdata = servermodel.countdata[lkey]
				olddata = oldservermodel.countdata[lkey]
				highlight(newdata,olddata)
				set newdata in viewmodel
			// totals
			lkey="TOTAL"
			mewdata = servermodel.countdata[lkey]
			olddata = oldservermodel.countdata[lkey]
			highlight(newdata,olddata)
			set viewmodaltotall = newdata */
			//travers entityGroups : Seller-Msme/Buyer-Constituion/Financier-FinCategory
			var lTypeKeys = ['regEntCatTypes','regEntCatTypes','regEntCatTypes','transTypes'];
			var lTypeDataKeys = ['regEntCatTypeData','transData','transData','transSummaryData'];
			var lTypePrefix = ['REG_','TRANS_COUNT_','TRANS_TO_','TRANSCONS_'];
			var lDataIndex = [-1,0,1,-1];
			//
			for(var lPtr=0; lPtr < lTypeKeys.length; lPtr++){
				var lNewData = pData[lTypeDataKeys[lPtr]];
				var lOldData = (pOldData!=null?pOldData[lTypeDataKeys[lPtr]]:null);
				var lCatTypes = pData[lTypeKeys[lPtr]];
				//add total to category
				//addTotalToCategory(lCatTypes);
				//
				var lViewData = getViewData(lCatTypes, lNewData, lOldData, lDataIndex[lPtr]);
				//
				Object.keys(lViewData).forEach(function (lKey) { 
					pData[lTypePrefix[lPtr]+lKey] = lViewData[lKey];
				});
			}
		}
		
		function addTotalToCategory(pCatTypes){
			if(pCatTypes!=null){
				for(var lPtr1=0; lPtr1 < pCatTypes.length ; lPtr1++){
					var lTmpCatTypes =pCatTypes[lPtr1]['catTypes'];
					var lFound=false;
					for(var lPtr2=0; lPtr2<lTmpCatTypes.length; lPtr2++){
						if(lTmpCatTypes[lPtr2]['value']=='TOTAL'){
							lFound=true;
							break;
						}
					}
					if(!lFound){
						//lTmpCatTypes.push({"value":"TOTAL", "desc":"Total"}); //adding total
					}
				}
			}
		}
		
		function getViewData(pCategoryTypes, pNewServerData, pOldServerData, pDataIndex) {
			var lViewData = {};
			var lRegEntCat = pCategoryTypes; //categorywise categorytypes
			var lOldData = pOldServerData;			
			//
			if(lRegEntCat != null){
				var lCat, lCatType, lCatTypeDes; 
				var lCatTypes; //List
				var lNewVal, lOldVal;
				for(var lPtr=0; lPtr < lRegEntCat.length; lPtr++){
					if(lPtr==0){
						//oldAlert("test");
					}
					lCat = lRegEntCat[lPtr]['entCat'];//key1
					lCatTypes = lRegEntCat[lPtr]['catTypes'];
					var lRowList = [];
					lViewData[lCat] = lRowList;
					//oldAlert("lCat : "+lCat);
					for(var lPtr1=0; lPtr1 <= lCatTypes.length; lPtr1++){
						if(lPtr1==lCatTypes.length){
							lCatType = 'TOTAL';
							lCatTypeDesc = 'Total';
						}else{
							lCatType = lCatTypes[lPtr1]['value']; //key2
							lCatTypeDesc = lCatTypes[lPtr1]['desc'];
						}
						var lRow = {};
						if(pNewServerData[lCat][lCatType] != null){
								Object.keys(pNewServerData[lCat][lCatType]).forEach(function (lKey) { 
									if(pDataIndex == -1){
										lNewVal = pNewServerData[lCat][lCatType][lKey];
									}else{
										lNewVal = pNewServerData[lCat][lCatType][lKey][pDataIndex];
									}
									lOldVal = getOldValue(lOldData, lCat, lCatType, lKey, pDataIndex);
									lRow[lKey] = lNewVal;
									if(lOldVal!=null){
										//compare old and new value
										if(lOldVal!=null && lNewVal!=null){
											if(lOldVal!=lNewVal){
												lRow[lKey+'highlight'] = true;
												//oldAlert(lKey+'highlight OldVal : ' + lOldVal + " NewVal "+lNewVal)
											}
										}
									}
							});
							lRow['key'] = lCatType;
							lRow['desc'] = lCatTypeDesc;
							lRowList.push(lRow);
						}
					}
				}
			}
			return lViewData;
		}
		
		function getOldValue(pOldData, pCat, pCatType, pKey,pDataIndex){
			var lValue = null;
			if(pOldData!=null && pOldData[pCat]!=null && pOldData[pCat][pCatType]!=null){
				if(pDataIndex == -1){
					lValue = pOldData[pCat][pCatType][pKey];
				}else{
					lValue = pOldData[pCat][pCatType][pKey][pDataIndex];
				}
			}
			return lValue
		}
		
		<%
		if (!"2".equals(lSec)) {
		%>
			var lastRefreshTime = 0;
			function handleClock(pMillis) {
				if (loginData.secKeys['widmonitor-view']!=null){
					if(lastRefreshTime!=0 && (pMillis - lastRefreshTime) > 120000){
						reloadDashboard();
					}
					lastRefreshTime = pMillis;
				}
			}
		<%
		}
		%>
		
		var dispInv = {'invcount':false, 'invamount':false};
		function toggleTransDisplay(pKey){
			dispInv[pKey] = !($('#'+pKey+'div').is(':visible'));
			displayInvoice(pKey,dispInv[pKey]);
		}
		function displayInvoiceall(){
			for(lKey in dispInv){
				displayInvoice(lKey,dispInv[lKey]);
			}
		}
		function displayInvoice(pKey,pDisplay){
			if(pDisplay)$('#'+pKey+'div').show();
			else $('#'+pKey+'div').hide();
		}
		function toggleTable(pKey){
			$('.'+pKey).toggle();
			$('.'+pKey).toggleClass('hide');	
		}
</script>
    </body>
</html>