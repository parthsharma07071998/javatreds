<!DOCTYPE html>
<%@page import="com.xlx.treds.auction.bean.ObligationBean"%>
<%@page
	import="com.xlx.treds.auction.bean.ObligationModificationRequestBean.Status"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page
	import="com.xlx.treds.auction.bean.ObligationModificationRequestBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
	Boolean lNew = (Boolean) request.getAttribute(CommonAppConstants.PARAM_NEW);
	boolean lNewBool = (lNew != null) && lNew.booleanValue();
	String lModify = (String) request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
<head>
<title>Obligations Modification Request</title>
<%@include file="includes1.jsp"%>
<link href="../css/datatables.css" rel="stylesheet" />
<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
<!-- <link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/> -->
</head>
<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Obligations Modification Request" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="content" id="contObligationModificationRequest">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Obligation Modification Request</h1>
			</div>
		</div>
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in"
				id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="fuId" class="label">Factoring Unit:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="input">
								<input type="text" id="fuId" placeholder="Factoring Unit">
								<b class="tooltip tooltip-top-right"></b>
							</section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="partNumber" class="label">PartNo:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="input">
								<input type="text" id="partNumber" placeholder="PartNo">
								<b class="tooltip tooltip-top-right"></b>
							</section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="type" class="label">Type:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="select">
								<select id="type"><option value="">Select Type</option></select>
								<b class="tooltip tooltip-top-right"></b><i></i>
							</section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="date" class="label">Request Date:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="input">
								<i class="icon-append fa fa-clock-o"></i> <input type="text"
									id="date" placeholder="Request Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b>
							</section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="status" class="label">Status:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="select">
								<select id="status"><option value="">Select
										Status</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="createrLogin" class="label">Creater Login:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="input">
								<input type="text" id="createrLogin" placeholder="Creater Login">
								<b class="tooltip tooltip-top-right"></b>
							</section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="approveRejectLogin" class="label">Approver/Rejector
									Login:</label>
							</section>
						</div>
						<div class="col-sm-2">
							<section class="input">
								<input type="text" id="approveRejectLogin"
									placeholder="Approver/Rejector Login"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
						</div>
					</div>

					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg"
										id=btnSearch>
										<span class="fa fa-search"></span> Search
									</button>
									<button type="button" class="btn btn-info-inverse btn-lg"
										id=btnFilterClr>Clear Filter</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links collapsed" href="javascript:;"
						data-toggle="collapse" data-target="#divFilter">Filter</a> <a
						href="javascript:;" class="secure right_links"
						data-seckey="oblimodreq-approve" id=btnApprove style=''><span
						class="glyphicon glyphicon-edit"></span>Approve</a> <a
						href="javascript:;" class="secure right_links"
						data-seckey="oblimodreq-save" id=btnModify style=''><span
						class="glyphicon glyphicon-edit"></span>Modify</a> <a
						href="javascript:;" class="secure right_links"
						data-seckey="oblimodreq-save" id=btnSend style=''><span
						class="glyphicon glyphicon-edit"></span>Send</a> <a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered" id="tblData" width="1000px " data-selector="multiple"
								data-scroll-y="300px">
								<thead>
									<tr>
										<th data-width="100px" data-visible="false" data-name="id">Id</th>
										<th data-width="150px" data-name="fuId"  data-class-name="select-checkbox">Factoring Unit</th>
										<th data-width="50px" data-name="partNumber">Part No.</th>
										<th data-width="80px" data-name="type">Type</th>
										<th data-width="100px" data-name="date">Request Date</th>
										<th data-width="100px" data-name="status">Status</th>
										<th data-width="100px" data-name="createDate">Create Date</th>
										<th data-width="100px" data-name="createrName">Creator
											Name</th>
										<th data-width="100px" data-name="approveRejectDate">Approval
											/ Rejection Date</th>
										<th data-width="100px" data-name="approveRejectName">Approver
											/ Rejector Name</th>
										<th data-width="100px" data-name="remarks">Remarks</th>
										<th data-width="100px" data-visible="false"
											data-name="isPreModification">Pre Modification</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>

		<div class="xform" style="display: none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Modification Request</h1>
				</div>
			</div>
			<div class="xform box">
				<fieldset>
					<fieldset>
						<div class="row" style="display: none">
							<div class="col-sm-2">
								<section>
									<label for="id" class="label">Id:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="id" placeholder="Id"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="fuId" class="label">Factoring Unit:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="fuId" placeholder="Factoring Unit">
									<b class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="partNumber" class="label">PartNo:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="partNumber" placeholder="PartNo">
									<b class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="type" class="label">Type:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="select">
									<select id="type"><option value="">Select
											Type</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-2">
								<section>
									<label for="date" class="label">Request Date:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<i class="icon-append fa fa-clock-o"></i> <input type="text"
										id="date" placeholder="Request Date"
										data-role="datetimepicker"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="status" class="label">Status:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="select">
									<select id="status"><option value="">Select
											Status</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row" style="display: none">
							<div class="col-sm-2">
								<section>
									<label for="createDate" class="label">Revised Date:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<i class="icon-append fa fa-clock-o"></i> <input type="text"
										id="createDate" placeholder="Revised Date"
										data-role="datetimepicker"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="createrAuId" class="label">Creator Id:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="createrAuId" placeholder="Creator Id">
									<b class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row" style="display: none">
							<div class="col-sm-2">
								<section>
									<label for="createrName" class="label">Creater Name:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="createrName" placeholder="Creater Name">
									<b class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="createrLogin" class="label">Creater Login:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="createrLogin"
										placeholder="Creater Login"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row" style="display: none">
							<div class="col-sm-2">
								<section>
									<label for="approveRejectDate" class="label">Approval/Rejection
										Date:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<i class="icon-append fa fa-clock-o"></i> <input type="text"
										id="approveRejectDate" placeholder="Approval/Rejection Date"
										data-role="datetimepicker"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="approveRejectAuId" class="label">Approver/Rejector
										Id:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="approveRejectAuId"
										placeholder="Approver/Rejector Id"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
						<div class="row" style="display: none">
							<div class="col-sm-2">
								<section>
									<label for="approveRejectName" class="label">Approver/Rejector
										Name:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="approveRejectName"
										placeholder="Approver/Rejector Name"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
							<div class="col-sm-2">
								<section>
									<label for="approveRejectLogin" class="label">Approver/Rejector
										Login:</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<input type="text" id="approveRejectLogin"
										placeholder="Approver/Rejector Login"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
					</fieldset>
					<fieldset id="obliModDetailsList">
						<div style="display: none" id="obliModDetailsList-frmSearch">
							<div class="row">
								<div class="col-sm-12">
									<div class="filter-block clearfix">
										<div class="">
											<a href="javascript:;" class="secure left_links"
												data-seckey="oblimodreq-save"
												id="obliModDetailsList-btnModify"><span
												class="glyphicon glyphicon-pencil"></span> Modify</a>
										</div>
									</div>
								</div>
							</div>
							<div class="tab-pane panel panel-default">
								<fieldset>
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered"
												id="obliModDetailsList-tblData">
												<thead>
													<tr>
														<th data-width="100px" data-name="id" data-visible="false"
															data-data="id">Id</th>
														<th data-width="100px" data-name="omrId"
															data-visible="false" data-data="omrId">OmrId</th>
														<th data-width="100px" data-name="obId" data-data="obId">Obligation
															Id</th>
														<th data-width="50px" data-name="partNumber"
															data-visible="false" data-data="partNumber">Part No.</th>
														<th data-width="40px" data-name="txnType"
															data-data="txnType">Txn Type</th>
														<th data-width="80px" data-name="origAmount"
															data-data="origAmount">Original Amount</th>
														<th data-width="80px" data-name="revisedAmount"
															data-data="revisedAmount">Revised Amount</th>
														<th data-width="100px" data-name="origDate"
															data-data="origDate">Original Date</th>
														<th data-width="100px" data-name="revisedDate"
															data-data="revisedDate">Revised Date</th>
														<th data-width="50px" data-name="origStatus"
															data-data="origStatus">Original Status</th>
														<th data-width="50px" data-name="revisedStatus"
															data-data="revisedStatus">Revised Status</th>
														<th data-width="100px" data-name="paymentSettlor"
															data-data="paymentSettlor">Settlor</th>
														<th data-width="100px" data-name="paymentRefNo"
															data-data="paymentRefNo">Payment Ref No</th>
														<th data-width="100px" data-name="remarks"
															data-data="remarks">Remarks</th>
														<th data-visible="false" data-name="recordVersion"
															data-data="recordVersion">RecordVersion</th>
													</tr>
												</thead>
											</table>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="tab-pane panel panel-default">
								<fieldset>
									<div class="row">
										<div class="col-sm-12">
											<table id="totals" width="460px">
												<tr>
													<td width="150px">Original Debit Total</td>
													<td width="80px" id="originalDebit"></td>
													<td width="80px" id="revisedDebit"></td>
													<td width="150px">Revised Debit Total</td>
												</tr>
												<tr>
													<td>Original Credit Total</td>
													<td id="originalCredit"></td>
													<td id="revisedCredit"></td>
													<td>Revised Credit Total</td>
												</tr>
											</table>
										</div>
									</div>
								</fieldset>
							</div>
						</div>

						<div class="modal fade" tabindex=-1>
							<div class="modal-dialog modal-lg">
								<div class="modal-content">
									<div class="modal-header">
										<span>Obligation Modification Details</span>
										<button type="button" class="btn btn-sm pull-right"
											data-dismiss="modal">
											<i class="fa fa-close"></i>
										</button>
									</div>
									<div class="modal-body modal-no-padding">
										<div id="obliModDetailsList-frmMain" class="xform">
											<!-- <header>Obligations : Complete list of obligations generated for the factored bid. It also captures the current state of the obligation as well as the settelment details of the particular obligation legwise.</header> -->
											<fieldset>
												<div class="row" style="display: none">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-id" class="label">Id:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-id"
																placeholder="Id"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-omrId" class="label">OmrId:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-omrId"
																placeholder="OmrId"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row" style="display: none">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-obId" class="label">Obligatin
																Id:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-obId"
																placeholder="Obligatin Id"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-partNumber" class="label">PartNo:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-partNumber"
																placeholder="PartNo"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row" style="display: none">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-txnType" class="label">Transaction
																Type:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="select">
															<select id="obliModDetailsList-txnType"><option
																	value="">Select Transaction Type</option></select> <b
																class="tooltip tooltip-top-right"></b><i></i>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-origAmount" class="label">Original
																Amount:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-origAmount"
																placeholder="Original Amount"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-revisedAmount"
																class="label">Revised Amount:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-revisedAmount"
																placeholder="Revised Amount"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-origDate" class="label">Original
																Date:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<i class="icon-append fa fa-clock-o"></i> <input
																type="text" id="obliModDetailsList-origDate"
																placeholder="Original Date" data-role="datetimepicker">
															<b class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-revisedDate" class="label">Revised
																Date:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<i class="icon-append fa fa-clock-o"></i> <input
																type="text" id="obliModDetailsList-revisedDate"
																placeholder="Revised Date" data-role="datetimepicker">
															<b class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-origStatus" class="label">Original
																Status:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="select">
															<select id="obliModDetailsList-origStatus"><option
																	value="">Select Original Status</option></select> <b
																class="tooltip tooltip-top-right"></b><i></i>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-revisedStatus"
																class="label">Revised Status:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="select">
															<select id="obliModDetailsList-revisedStatus"><option
																	value="">Select Revised Status</option></select> <b
																class="tooltip tooltip-top-right"></b><i></i>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-paymentSettlor"
																class="label">Revised Settlor:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="select">
															<select id="obliModDetailsList-paymentSettlor"><option
																	value="">Select Settlor Type</option></select> <b
																class="tooltip tooltip-top-right"></b><i></i>
														</section>
														<section class="view"></section>
													</div>
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-paymentRefNo"
																class="label">Payment Ref No:</label>
														</section>
													</div>
													<div class="col-sm-4">
														<section class="input">
															<input type="text" id="obliModDetailsList-paymentRefNo"
																placeholder="Payment ref No."> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-2">
														<section>
															<label for="obliModDetailsList-remarks" class="label">Remarks:</label>
														</section>
													</div>
													<div class="col-sm-10">
														<section class="input">
															<input type="text" id="obliModDetailsList-remarks"
																placeholder="Remarks"> <b
																class="tooltip tooltip-top-right"></b>
														</section>
														<section class="view"></section>
													</div>
												</div>
							<fieldset>
							<center><h2>Calculate Interest</h4></center>
							<div class="row">
								<div class="col-sm-2">
								<section><label for="obliModDetailsList-dayDiff" class="label">Day Diff:</label></section>
								<span><section class="input">
									<input type="text" id="obliModDetailsList-dayDiff" placeholder="Day Diff">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section></span>
								</div>
								<div class="col-sm-2">
								<section><label for="obliModDetailsList-rate" class="label">Rate :</label></section>
								<section class="input">
									<input type="text" id="obliModDetailsList-rate" placeholder="Rate">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-2">
								<section><label for="obliModDetailsList-intrest" class="label">Interest:</label></section>
								<section class="input">
									<input type="text" id="obliModDetailsList-intrest" placeholder="Intrest">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-3">
								<section><label for="obliModDetailsList-calculatedRevAmt" class="label">Calculated Revised Amt:</label></section>
								<section class="input">
									<input type="text" id="obliModDetailsList-calculatedRevAmt" placeholder="Calculated Revised Amt">
									<b class="tooltip tooltip-top-right"></b></section>
									<section class="view"></section>
								</div>
								<div class="col-sm-3">
								<br><br>
								<section><button type="button" class="btn btn-info btn-sm btn-close" id="obliModDetailsList-btnApplyIntrest"><span class="fa fa-close"></span> Add Intrest</button></section>
								</div>
							</div>
							</fieldset>
												<footer>
													<div class="modal-footer">
														<div class="btn-group pull-right">
															<button type="button"
																class="btn btn-info btn-lg btn-enter secure"
																data-seckey="oblimodreq-save"
																id="obliModDetailsList-btnSave">
																<span class="fa fa-save"></span> Save
															</button>
															<button type="button"
																class="btn btn-info-inverse btn-lg btn-close"
																id="obliModDetailsList-btnClose">
																<span class="fa fa-close"></span> Close
															</button>
														</div>
													</div>
												</footer>
											</fieldset>
										</div>
									</div>
								</div>
							</div>
						</div>
					</fieldset>

					<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<a href="#" class="secure" data-seckey="oblimodreq-approve">
										<button type="button" class="btn btn-danger btn-lg btn-enter"
											id=btnReject>
											<span class="fa fa-pencil"></span> Reject
										</button>
									</a> <a href="#" class="secure" data-seckey="oblimodreq-approve">
										<button type="button" class="btn btn-info btn-lg btn-enter"
											id=btnFinalApprove>
											<span class="fa fa-pencil"></span> Approve
										</button>
									</a> <a href="#" class="secure" data-seckey="oblimodreq-save">
										<button type="button" class="btn btn-info btn-lg btn-enter"
											id=btnSave>
											<span class="fa fa-save"></span> Save
										</button>
									</a>
									<button type="button"
										class="btn btn-info-inverse btn-lg btn-close" id=btnClose>
										<span class="fa fa-close"></span> Close
									</button>
									<button type="button"
										class="btn btn-info-inverse btn-lg btn-close" id=btnBack>
										<span class="fa fa-back"></span> Settlement Menu
									</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
	</div>


	<%@ include file="footer1.jsp"%>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudObligationModificationRequest$ = null;
		var mainForm = null;
		var originalData = null;
		var lRevisedStatusMap = null;
		var lRevDebit,lRevCredit;
		var lModificationFlag = false;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationModificationRequestBean.class).getJsonConfig()%>;
			var lModConfig =[
				{
					"name":"dayDiff",
					"label":"Day Diff",
					"dataType":"Integer",
					"nonDatabase":true
				},
				{
					"name":"rate",
					"label":"Rate",
					"dataType":"Integer",
					"nonDatabase":true
				},
				{
					"name":"intrest",
					"label":"Intrest",
					"dataType":"Integer",
					"nonDatabase":true
				},
				{
					"name":"calculatedRevAmt",
					"label":"Calculated Revised Amt",
					"dataType":"Integer",
					"nonDatabase":true
				}
			];
			var lConfig = {
					resource: "oblimodreq",
					autoRefresh: true,
					postModifyHandler: function(pObj) {
						enableDisable();
						if (pObj.status=='<%=Status.Created.getCode()%>'){
							$('#btnFinalApprove').hide();
							$('#btnReject').hide();
						}
						if (pObj.status=='<%=Status.Rejected.getCode()%>' ||
								pObj.status=='<%=Status.Applied.getCode()%>' ||
									pObj.status=='<%=Status.Approved.getCode()%>' ){
							$('#btnFinalApprove').hide();
							$('#btnReject').hide();
							$('#obliModDetailsList-btnModify').hide();
						}
						addAcceptedRate(pObj.obliModDetailsList,pObj.acceptedRate);
						originalData = pObj.obliModDetailsList;
						calculateTotals(pObj.obliModDetailsList);
						return true;
					},
					postSaveHandler: function(pData) {
						lModificationFlag = false;
						return true;
					},
<%if (lNewBool) {%>					new: true,
<%} else if (lModify != null) {%>					modify: <%=lModify%>,
<%}%>			};
			var lFormModList = lFormConfig.fields;
			for (var i = 0; i < lFormModList.length; i++) {
			    if (lFormModList[i].name == 'obliModDetailsList'){
			    	lFormModList[i].fields.push(...lModConfig);
			    }
			}
			lConfig = $.extend(lConfig, lFormConfig);
			crudObligationModificationRequest$ = $('#contObligationModificationRequest').xcrudwrapper(lConfig);
			crudObligationModificationRequest = crudObligationModificationRequest$.data('xcrudwrapper');
			mainForm = crudObligationModificationRequest.options.mainForm;
			mainFormModDetails = mainForm.getField('obliModDetailsList').options.mainForm;
			$.ajax({
		        url: 'oblimodreq/revisedStatus',
		        type: 'GET',
		        success: function( pData, pStatus, pXhr) { 
		        	lRevisedStatusMap = pData;

		        },
		        error: function( pObj, pStatus, pXhr) {
		        	oldAlert('Error');
		        }
		    });
			mainForm.getField('obliModDetailsList').options.postModifyHandler=function(pObj){
				setRevisedStatus(lRevisedStatusMap,pObj.origStatus);
				enableDisableChild();
				CalculateDateDiff();
				if ('<%=ObligationBean.Status.Created.getCode()%>'==pObj.origStatus ||
						'<%=ObligationBean.Status.Ready.getCode()%>'==pObj.origStatus 	){
					enableDisableOnReady();
				}
				return true;
			};
			mainForm.getField('obliModDetailsList').options.postSaveHandler=function(pObj){
				if((pObj.revisedStatus=='SUC' && pObj.paymentSettlor=='DIRECT') ){
					alert(' For Direct Settlements status should be marked as Ready/Created ');
					return false;
				}
				lModificationFlag = true;
				enableDisableChild();
				updateData(originalData, pObj);
				calculateTotals(originalData);
				return true;
			};
			$('#btnFinalApprove').on('click', function(){
				if (lModificationFlag){
					alert('Please save the changes before approving.');
				}else{
					updateStatus('<%=ObligationModificationRequestBean.Status.Approved.getCode()%>',false, null);
				}
			});

		   $("#obliModDetailsList-revisedDate").on('changeDate',function(){
			   CalculateDateDiff();
			});
			$('#btnReject').on('click', function(){
				promptAndUpdate();
			});
			function updateStatus(pStatus,pId,pRemarks){
				var id= null;
				if (pId){
					id = pId;
				}else{
					id = mainForm.formData.id;
				}
				var lUrl = null;
				var lData = null;
				if (pStatus=='<%=ObligationModificationRequestBean.Status.Sent.getCode()%>'){
					lUrl = 'oblimodreq/status/';
					lData={id:id,status:pStatus};
				}else{
					lData={'id':id,'status':pStatus};
					lUrl = 'oblimodreq/checker/';
				}
				$.ajax({
			        url: lUrl,
			        type: 'POST',
			        data: JSON.stringify(lData),
			        success: function( pData, pStatus, pXhr) { 
						alert("Saved successfully. Please Refresh.");
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	alert(pObj.responseJSON.messages);
			        }
			    });
			}
			$('#btnApprove').on('click', function(){
				var lRows = crudObligationModificationRequest.getSelectedRows();
				var ids = null;
				if(lRows.length<1){
         			alert("Please atleast one row.");	
         		}else{
         			$.each(lRows, function(pIndex,pValue) {
         				if ( !(pValue.data().status == '<%=ObligationModificationRequestBean.Status.Sent.getCode()%>')){
	 						alert("InvalidStatus.");
	 						return false;
 						}else{
 							if (ids==null){
 								ids = pValue.data().id;
 	         				}else{
 	         					ids += ","+pValue.data().id;
 	         				}
 						}
        			});
         			if (lRows.length==1){
         				$('#btnModify').click();
         			}else{
         				var lData = {};
         				lData['ids'] = ids;
         				lData['status'] = '<%=ObligationModificationRequestBean.Status.Approved.getCode()%>';
         				$.ajax({
        			        url: 'oblimodreq/checker',
        			        type: 'POST',
        			        data: JSON.stringify(lData),
        			        success: function( pData, pStatus, pXhr) { 
        						alert("Saved successfully. Please Refresh.");
        			        },
        			        error: function( pObj, pStatus, pXhr) {
        			        	alert(pObj.responseJSON.messages);
        			        }
        			    });
         			}
         		}
			});
			$('#btnSend').on('click', function(){
				var lSelected = crudObligationModificationRequest.getSelectedRow();
				if ((lSelected==null)||(lSelected.length==0)) {
					alert("Please select a row");
					return;
				}
				if (lSelected.data().status=='<%=Status.Created.getCode()%>'){
					updateStatus('<%=ObligationModificationRequestBean.Status.Sent.getCode()%>',lSelected.data().id ,null);
				}else{
					alert("Invalid Status.")
				}
			});
			$('#btnBack').on('click', function(){
				location.href='oblig?adm=Y';
			});
			$('#obliModDetailsList-btnApplyIntrest').on('click', function(){
				mainFormModDetails.getField('revisedAmount').setValue(mainFormModDetails.getField('calculatedRevAmt').getValue());
			});
			function enableDisable(){
				var lFields =  ['fuId', 'partNumber', 'type' , 'date' , 'status' ];
				var lAlterFields =  ['id','createDate','createrAuId'];
// 				mainForm.enableDisableField(lFields,false,false);
				mainForm.disable();
				mainForm.alterField(lAlterFields,false,false);
				mainForm.enableDisableField('obliModDetailsList',true,false);
			}
			function enableDisableChild(){ 
				var lDisableFields =  ['id', 'omrId' ,'obId' , 'partNo', 'partNumber' , 'txnType' , 'origAmount' , 'origStatus' , 'origDate'];
				var lAlterFields =  ['id', 'omrId' ];
				mainFormModDetails.enableDisableField(lDisableFields,false,false);
				mainFormModDetails.alterField(lAlterFields,false,false);
			}
			function enableDisableOnReady(){ 
				var lDisableFields =  [ 'revisedAmount' , 'revisedStatus' , 'paymentRefNo'];
				mainFormModDetails.enableDisableField(lDisableFields,false,false);
			}
			function calculateTotals(pObj){ 
				var originalDebit = 0;
				var originalCredit = 0;
				var revisedDebit = 0;
				var revisedCredit = 0;
				pObj.forEach(function(ldata) {
					if (ldata.txnType == 'D'){
						originalDebit = originalDebit + ldata.origAmount;
						revisedDebit = revisedDebit + ldata.revisedAmount;
					}else if (ldata.txnType == 'C'){
						originalCredit = originalCredit + ldata.origAmount;
						revisedCredit = revisedCredit + ldata.revisedAmount;
					}
				});
				lRevDebit=revisedDebit.toFixed(2);
				lRevCredit=revisedCredit.toFixed(2);
				$('#originalDebit').html(originalDebit.toFixed(2));
				$('#originalCredit').html(originalCredit.toFixed(2));
				$('#revisedDebit').html(lRevDebit);
				$('#revisedCredit').html(lRevCredit);
			}
			function addAcceptedRate(pObj,pAcceptedRate){ 
				pObj.forEach(function(ldata) {
					if(pAcceptedRate!=null){
						ldata['rate'] = pAcceptedRate;
					}
				});
			}
			function updateData(pOriginalData, pDataToUpdate){
				pOriginalData.forEach(function(ldata) {
					if(ldata.obId==pDataToUpdate.obId){
						ldata.revisedAmount = Number(pDataToUpdate.revisedAmount);
						ldata.origAmount = Number(ldata.origAmount);
					}
					ldata.revisedStatus = pDataToUpdate.revisedStatus;
					ldata.revisedDate = pDataToUpdate.revisedDate;
					ldata.paymentSettlor = pDataToUpdate.paymentSettlor;
				});
			}
			function setRevisedStatus(pData,pOldStatus){
				for (var key in pData) {         
				    if(key==pOldStatus){
				    	pObj=pData[key];
				    	$('#obliModDetailsList-frmMain #obliModDetailsList-revisedStatus').empty();
			    		for(index in pObj){
    				        var option = document.createElement('option');
    				        option.text = pObj[index].text;
    				        option.value = pObj[index].value;
    				        $('#obliModDetailsList-frmMain #obliModDetailsList-revisedStatus').append(option);
	    				}
				    }
				}
			}

		});
		
		function promptAndUpdate(){
			prompt("Please enter suitable reason/remarks","Reason",function(pReason){
				var lUrl = null;
				var lData = null;
				var id= mainForm.formData.id;
				var pStatus ='<%=ObligationModificationRequestBean.Status.Rejected.getCode()%>';
				lData = {id:id,status:pStatus,remarks:pReason};
				lUrl = 'oblimodreq/checker';
				$.ajax({
			        url: lUrl,
			        type: 'POST',
			        data: JSON.stringify(lData),
			        success: function( pData, pStatus, pXhr) { 
						alert("Saved successfully. Please Refresh.");
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	alert(pObj.responseJSON.messages);
			        }
			    });
	        });	
		}
		
		function CalculateDateDiff(){
			 var lDayDiff =Math.ceil((new Date(mainFormModDetails.getField('revisedDate').getValue()).getTime() - new Date(mainFormModDetails.getField('origDate').getValue()).getTime()) / (1000 * 3600 * 24));
			 mainFormModDetails.getField('dayDiff').setValue(lDayDiff);
			 if (lDayDiff<0){
				 alert('Revised Date should be after Original Date.');
				 return;
			 }
			 var lRate = mainFormModDetails.getField('rate').getValue();
			 lRate = parseFloat(lRate, 10);
			 var lOrigAmount = mainFormModDetails.getField('origAmount').getValue();
			 lOrigAmount = parseFloat(lOrigAmount, 10);
			 var lDailyIntrest = ((lRate/365)*lOrigAmount);
			 lDailyIntrest = ((lOrigAmount*lRate)/365);
			 var lIntrest = (lDailyIntrest*lDayDiff).toFixed(2);
			 lIntrest = parseFloat(lIntrest, 10);
			 mainFormModDetails.getField('intrest').setValue(lIntrest);
			 var lCalculatedRevAmt = (lOrigAmount+lIntrest).toFixed(2);
			 mainFormModDetails.getField('calculatedRevAmt').setValue(lCalculatedRevAmt);
		}

		
	</script>


</body>
</html>