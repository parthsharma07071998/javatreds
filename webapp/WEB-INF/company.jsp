<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.CompanyLocationBean.LocationType"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
<head>
<title>TREDS | Registration</title>
<%
String lTab=StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("tab")));
if (StringUtils.isBlank(lTab)) lTab = "tabGeneral";
String lEntityId = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("entityId")));
if(lEntityId==null||lEntityId=="")lEntityId="0";
String lIsProv = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("isProv"))); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<%@include file="includes1.jsp"%>
<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet" />
<style>
.nav li a.state-error:before {
	left: 5px !important;
}
</style>
</head>
<body class="page-body">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Entity Details" />
	</jsp:include>


	<div class="content" id="contCompanyDetail">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Entity Details</h1>
				</div>
			</div>
		<div class="xform" id="frmMain">
			<div>
			<div class="cloudTabs">
				<ul class="cloudtabs nav nav-tabs">
					<li class="active1"><a href="#tabGeneral" data-toggle="tab">General
							Information </a></li>
					<li><a href="#tabAddress" data-toggle="tab">Address &
							Contact Details</a></li>
					<li><a href="#tabMSME" data-toggle="tab">MSME Status</a></li>
					<li><a href="#tabOthers" data-toggle="tab">Others</a></li>
				</ul>
			</div>
			</div>
			<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<div class="tab-content no-padding">
								<div id="tabGeneral" class="tab-pane active1">
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="code" class="label">Member Code:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="code"
													placeholder="Member Code"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="companyName" class="label">Name of
													Applicant Entity:</label>
											</section>
										</div>
										<div class="col-sm-8">
											<section class="input">
												<input type="text" id="companyName"
													placeholder="Name of Applicant Entity"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="type" class="label">Entity Type:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="type"><option value="">Select
														Entity Type</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="constitution" class="label">Constitution:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="constitution"><option value="">Select
														Constitution</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="finCertificateNo" class="label">RBI
													Registration Certificate No:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="finCertificateNo"
													placeholder="RBI Registration Certificate Number">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="finCertificateIssueDate" class="label">Issue
													Date:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="finCertificateIssueDate" placeholder="Issue Date"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="cinNo" class="label">CIN No:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="cinNo" placeholder="CIN No">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="dateOfIncorporation" class="label">Date
													of Incorporation:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="dateOfIncorporation"
													placeholder="Date of Incorporation"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="existenceYears" class="label">Years in
													Business:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="existenceYears"><option value="">Select
														Years in Business</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="pan" class="label">Permanent Account
													Number (PAN):</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="pan"
													placeholder="Permanent Account Number (PAN)"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="industry" class="label">Industry:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="industry"><option value="">Select
														Industry</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="subSegment" class="label">Sub-Segment:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="subSegment"><option value="">Select
														Sub-Segment</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="sector" class="label">Sector:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="sector"><option value="">Select
														Sector</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="exportOrientation" class="label">Export
													Orientation:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="exportOrientation"><option value="">Select
														Export Orientation</option></select> <b class="tooltip tooltip-top-left"></b><i></i>
											</section>
											<section class="view"></section>
										</div>

										<div class="col-sm-2">
											<section>
												<label for="currency" class="label">Currency:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="currency"><option value="">Select
														Currency</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="financierCategory" class="label">Financer
													Category:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="financierCategory"><option value="">Select
														Financier Category</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>

									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="companyDesc" class="label">Brief
													Description of Activity:</label>
											</section>
										</div>
										<div class="col-sm-8">
											<section class="textarea">
												<textarea id="companyDesc"
													placeholder="Brief Description of Activity" rows=5></textarea>
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
								</div>
								<div id="tabAddress" class="tab-pane">

									<hr>
									<div class="row">
										<div class="col-sm-12">
											<h4>
												<span class="fa fa-building"></span> Correspondence Address
												<small><a class="btn btn-link pull-right" id="chk-cor-adr" onclick="populateRegLocation()">
														Same as Registered Address</a></small>
											</h4>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-6">
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corLine1" class="label">Address:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corLine1" placeholder="Line 1">
														<b class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corLine2" class="label"></label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corLine2" placeholder="Line 2">
														<b class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corLine3" class="label"></label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corLine3" placeholder="Line 3">
														<b class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
										</div>
										<div class="col-sm-6">
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corZipCode" class="label">Zip Code:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corZipCode" placeholder="Zip Code">
														<b class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corCity" class="label">City:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corCity" placeholder="City">
														<b class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corDistrict" class="label">District:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="input">
														<input type="text" id="corDistrict"
															placeholder="District"> <b
															class="tooltip tooltip-top-right"></b>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corState" class="label">State:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="select">
														<select id="corState"><option value="">Select
																State</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
													</section>
													<section class="view"></section>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<section>
														<label for="corCountry" class="label">Country:</label>
													</section>
												</div>
												<div class="col-sm-8">
													<section class="select">
														<select id="corCountry"><option value="">Select
																Country</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
													</section>
													<section class="view"></section>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="corSalutation" class="label">Title:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="corSalutation"><option value="">Select
														Title</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="corFirstName" class="label">First Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corFirstName"
													placeholder="First Name"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="corMiddleName" class="label">Middle
													Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corMiddleName"
													placeholder="Middle Name"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="corLastName" class="label">Last Name:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corLastName" placeholder="Last Name">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="corEmail" class="label">Email:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corEmail" placeholder="Email">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="corTelephone" class="label">Telephone:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corTelephone" placeholder="Telephone">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-2">
											<section>
												<label for="corMobile" class="label">Mobile:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corMobile" placeholder="Mobile">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section>
												<label for="corFax" class="label">FAX:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="corFax" placeholder="FAX"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
								</div>
								<!-- tabAddresses -->
							
								<div id="tabMSME" class="tab-pane">
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="msmeStatus" class="label">MSME Status:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="msmeStatus"><option value="">Select
														MSME Status</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="msmeRegType" class="label">MSME
													Registration Type:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="msmeRegType"><option value="">Select
														MSME Reg. Type</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row" id='contMsme'>
										<div class="col-sm-4">
											<section>
												<label for="msmeRegNo" class="label">MSME
													Registration No:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="msmeRegNo"
													placeholder="MSME Registration No"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="msmeRegDate" class="label">Registration
													Date:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="msmeRegDate" placeholder="Registration Date"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="caName" class="label">Name of CA Firm:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="caName" placeholder="Name of CA Firm">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="caMemNo" class="label">Membership No of
													CA Firm:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="caMemNo"
													placeholder="Membership No of CA Firm"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="caCertDate" class="label">Date of CA
													Certificate:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="caCertDate" placeholder="Date of CA Certificate"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="udin" class="label">
												Udin No. of CA Certificate:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="udin" placeholder="Udin No">
												<b class="tooltip tooltip-top-right"></b>
											</section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="invtCPM" class="label">Investment in
													Core Plant and Machinery ( <span class="fa fa-rupee"></span>
													lakhs):
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="invtCPM"
													placeholder="Investment in Core Plant and Machinery (lakhs)">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for=invtDateCPM class="label">Invtestment as
													on date:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<i class="icon-append fa fa-clock-o"></i> <input type="text"
													id="invtDateCPM"
													placeholder="Investment in Core Pt & Mach as on date"
													data-role="datetimepicker"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
								</div>
								<div id="tabOthers" class="tab-pane">
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="annualMsmePurchase" class="label">Annual
													MSME Purchases ( <span class="fa fa-rupee"></span> lakhs):
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="annualMsmePurchase"
													placeholder="Annual MSME Purchases (lakhs)"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="salesTo" class="label">Sales Turnover as
													per last audited account ( <span class="fa fa-rupee"></span>
													lakhs):
												</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="salesTo"
													placeholder="Sales Turnover as per last audited account (lakhs)">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="salesYear" class="label">Financial Year:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="select">
												<select id="salesYear"><option value="">Select
														Financial Year</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="customer1" class="label">
												Name of Buyers:<br></br>
											    (Yearly Turnover in Rs. Cr)
												</label>
											</section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer1" placeholder="Name 1">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer1City"
													placeholder="Location"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="turnOver1" placeholder="Turn Over 1">
												<b class="tooltip tooltip-top-right"></b>
											</section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="yearsInRelation1" placeholder="Years In Relation 1">
												<b class="tooltip tooltip-top-right"></b>
											</section>
									</div>
									</div>
									<div class="row">
										<div class="col-sm-4"></div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer2" placeholder="Name 2">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer2City"
													placeholder="Location 2"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="turnOver2" placeholder="Turn Over 2">
												<b class="tooltip tooltip-top-right"></b>
											</section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="yearsInRelation2" placeholder="Years In Relation2">
												<b class="tooltip tooltip-top-right"></b>
											</section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4"></div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer3" placeholder="Name 3">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer3City"
													placeholder="Location 3"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
											<div class="col-sm-2">
											<section class="input">
												<input type="text" id="turnOver3" placeholder="Turn Over 3">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="yearsInRelation3" placeholder="Years In Relation3">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4"></div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer4" placeholder="Name 4">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer4City"
													placeholder="Location 4"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
											<div class="col-sm-2">
											<section class="input">
												<input type="text" id="turnOver4" placeholder="Turn Over 4">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="yearsInRelation4" placeholder="Years In Relation4">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4"></div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer5" placeholder="Name 5">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="customer5City"
													placeholder="Location 5"> <b
													class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
											<div class="col-sm-2">
											<section class="input">
												<input type="text" id="turnOver5" placeholder="Turn Over 5">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
										<div class="col-sm-2">
											<section class="input">
												<input type="text" id="yearsInRelation5" placeholder="Years In Relation5">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="cashDiscountPercent" class="label">Cash Discount %:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="cashDiscountPercent" placeholder="Cash Discount %">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">		
										<div class="col-sm-4">
											<section>
											<label for="enableLocationwiseSettlement" class="label">Enable Locationwise Settlement:</label>
											</section>
										</div>
										<div class="col-sm-8">
											<section>
												<label class="checkbox"><input type=checkbox id="enableLocationwiseSettlement"><i></i>
												<b class="tooltip tooltip-top-left"></b>
												</label>
											</section>
										<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4">
											<section>
												<label for="regWebsite" class="label">Web-site:</label>
											</section>
										</div>
										<div class="col-sm-4">
											<section class="input">
												<input type="text" id="regWebsite" placeholder="Web-site">
												<b class="tooltip tooltip-top-right"></b>
											</section>
											<section class="view"></section>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<fieldset class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info-inverse btn-lg" id=btnPrevious>
										<span class="fa fa-backward"></span> Previous
									</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnValidate>
										<span class="fa fa-check"></span> Validate
									</button>
									<button type="button" class="btn btn-info btn-enter btn-lg" id=btnSave><div id="saveText">Save & Go To Next</div></button>
								</div>
							</div>
						</div>
					</fieldset>
				</fieldset>
			</div>
		</div>
	</div>



	<%@include file="footer1.jsp"%>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var crudCompanyDetail$ = null;
		var crudCompanyDetail = null;
		var mainForm = null;
		var formConfig;
		var approved=false;
		$(document).ready(function() {
			formConfig = <%=BeanMetaFactory.getInstance()
					.getBeanMeta(CompanyDetailBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "company",
					keyFields:["id","isProvisional"],
					autoRefresh: true,
					modify: [<%=lEntityId%>,<%=lIsProv%>],
					postNewHandler: function() {
						approved=false;
						return true;
					},
					postModifyHandler: function(pObj) {
						setEntityType(pObj.type);
						setConstitution(pObj.constitution,pObj.type);
						populateSubSegment(pObj.subSegment);
						mainForm.setValue(pObj);
						if (pObj.companyFlag=='S' && pObj.msmeRegType==null){
							pObj['msmeRegType']='UAN';
							mainForm.getField('msmeRegType').setValue('UAN');
							mainForm.enableDisableField('msmeRegType', false, false);
							//mainForm.getField('msmeRegNoLabel').setValue("Udyog Aadhar No");
						}
						setMsmeRegType(pObj.msmeRegType);
						setRegNos(pObj.type,pObj.sector,pObj.stExempted);
						var lFields = [ 'code','companyName','type','constitution' ];
						mainForm.enableDisableField(lFields, false, false);
						approved = pObj.approvalStatus=='<%=AppConstants.CompanyApprovalStatus.Approved.getCode()%>';
						if(pObj.constitution == null){
							var lFields = [ 'constitution' ];
							mainForm.enableDisableField(lFields, true, true);
						}
						if (pObj.companyFlag=='S'){
							lFields = [ 'customer1'];
							mainForm.alterField(lFields, true, false);
						}
						lFields = [ 'caName','caMemNo','caCertDate','invtCPM','invtDateCPM'];
						mainForm.alterField(lFields, false, false);
						if (pObj.creatorIdentity=='J'){
							mainForm.setViewMode(true);
							$('#btnValidate').hide();
							$('#btnSave').hide();
							$('#chk-cor-adr').hide();
						}
						lFields = [];
						for(lPtr=1; lPtr <=5; lPtr++){
							lFields.push('customer'+lPtr,'customer'+lPtr+'City','turnOver'+lPtr,'yearsInRelation'+lPtr);
						}
						mainForm.enableDisableField(lFields, (pObj.companyFlag=='S'?true:false), false);
						mainForm.alterField(lFields, false, false);
					},
					saveHandler: function(pEvent){
						if (approved) {
							var lErrors = crudCompanyDetail.options.mainForm.check();
							if ((lErrors != null) && (lErrors.length > 0)) {
								crudCompanyDetail.showError();
								return;
							}
						}
						var lData = crudCompanyDetail.options.mainForm.getValue();
						crudCompanyDetail.options.btnSave$.prop('disabled',true);
						$.ajax( {
				            url: crudCompanyDetail.options.resource,
				            type: "PUT",
				            data:JSON.stringify(lData),
				            success: function( pObj, pStatus, pXhr) {
				            	crudCompanyDetail.options.mainForm.formData.recordVersion = crudCompanyDetail.options.mainForm.formData.recordVersion + 1;
				            	alert("Saved successfully", "Information");
				            	$('.nav-tabs > .active').next('li').find('a').trigger('click');
				            },
				        	error: errorHandler,
							complete: function() {
								crudCompanyDetail.options.btnSave$.prop('disabled',false);
							}
				        });					
					}
			};
			lConfig = $.extend(lConfig, formConfig);
			crudCompanyDetail$ = $('#contCompanyDetail').xcrudwrapper(lConfig);
			crudCompanyDetail = crudCompanyDetail$.data('xcrudwrapper');
			mainForm = crudCompanyDetail.options.mainForm;
			
			$('#btnValidate').on('click', function(pEvent) {
				var lErrors = crudCompanyDetail.options.mainForm.check();
				if ((lErrors != null) && (lErrors.length > 0)) {
					crudCompanyDetail.showError();
				} else {
					alert("Data is valid", "Information");
				}
				$('#frmMain .nav li a').each(function(){
					var lThis$ = $(this);
					var lTabPage = lThis$.attr('href');
					var lErrorFields = $('#frmMain .tab-content ' + lTabPage + ' .state-error');
					if (lErrorFields!=null && lErrorFields.length > 0) lThis$.addClass('state-error');
					else lThis$.removeClass('state-error');
				});
			});
			$('#type').on('change', function() {
				setEntityType(mainForm.getField('type').getValue());
				setRegNos(mainForm.getField('type').getValue(), mainForm.getField('sector').getValue(), mainForm.getField('stExempted').getValue());
			});
			$('#stExempted').on('change', function() {
				setRegNos(mainForm.getField('type').getValue(), mainForm.getField('sector').getValue(), mainForm.getField('stExempted').getValue());
			});
			$('#constitution').on('change', function() {
				setConstitution(mainForm.getField('constitution').getValue(),null);
			});
			$('#industry').on('change', function() {
				populateSubSegment(null);
			});
			$('#chk-gstcor-adr').on('click', function() {
				var lFields = ['Line1','Line2','Line3','ZipCode','Country','State','City','Salutation','FirstName','MiddleName','LastName','Email','Telephone','Mobile','Fax','District'];
				$.each(lFields, function(pIndex, pValue){
					mainForm.getField('gst'+pValue).setValue(mainForm.getField('cor'+pValue).getValue());
				});
			});
			$('#msmeRegType').on('change', function() {
				setMsmeRegType(mainForm.getField('msmeRegType').getValue());
			});
			$('#sector').on('change', function() {
				setRegNos(mainForm.getField('type').getValue(), mainForm.getField('sector').getValue(), mainForm.getField('stExempted').getValue());
			});
			$('#btnPrevious').on('click', function() {
            	$('.nav-tabs > .active').prev('li').find('a').trigger('click');
			});
			$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
				  var target = $(e.target).attr("href") // activated tab
				  var lVisible = (target=="#tabGeneral");
				  if(lVisible){
					  $('#btnPrevious').hide();
				  }else{
					  $('#btnPrevious').show();
				  }
				});
			showTab('<%=lTab%>');
		});
		function showTab(pTab) {
			$('#frmMain .nav-tabs a[href="#'+pTab+'"]').tab('show');
		}
		function populateSubSegment(pSubSegment) {
			var lInd=mainForm.getField('industry').getValue();
			if (!lInd) return;
			var lField=mainForm.getField('subSegment');
			var lOptions=lField.getOptions()
			lOptions.dataSetValues = "company/subsegment/"+lInd;
			lField.init();
			if (pSubSegment) lField.setValue(pSubSegment);
		}
		function setEntityType(pVal) {
			if(pVal==null)pVal="";
			var lSupp = pVal && (pVal.length==3) && (pVal.charAt(0)==='Y');
			var lPurc = pVal && (pVal.length==3) && (pVal.charAt(1)==='Y');
			var lFin = pVal && (pVal.length==3) && (pVal.charAt(2)==='Y');
			
			var lFields = {};
			$.each(formConfig.fieldGroups.financier,function(pIndex,pValue){
				lFields[pValue]=false;
			});
			$.each(formConfig.fieldGroups.purchaser,function(pIndex,pValue){
				lFields[pValue]=false;
			});
			$.each(formConfig.fieldGroups.supplier,function(pIndex,pValue){
				lFields[pValue]=false;
			});
			if (lFin) {
				$.each(formConfig.fieldGroups.financier,function(pIndex,pValue){
					lFields[pValue]=true;
				});
			}
			if (lPurc) {
				$.each(formConfig.fieldGroups.purchaser,function(pIndex,pValue){
					lFields[pValue]=true;
				});
			}
			if (lSupp) {
				$.each(formConfig.fieldGroups.supplier,function(pIndex,pValue){
					lFields[pValue]=true;
				});
			}
			$.each(lFields, function(pKey,pValue) {
				mainForm.enableDisableField(pKey, pValue, !pValue);
				mainForm.alterField(pKey, pValue, false);				
			});
			if (lSupp || lPurc || lFin) {
				lFields =  [ 'msmeRegType','msmeRegNo','msmeRegDate' ];
				mainForm.enableDisableField(lFields, lSupp, false); //show for Supplier and Hide for Purchaser & Financier
				mainForm.alterField(lFields, false, false);
				lFields =  [ 'msmeRegNo' ];
				mainForm.alterField(lFields, false, false);
			}
			mainForm.enableDisableField(['cashDiscountPercent'], lPurc, false); //show for Purchaser only
			mainForm.alterField(['cashDiscountPercent'], false, true);
			var lEnable = lFin;
			if(!lFin&&!lPurc&&!lSupp) lEnable=true;//if nothing is selected
			lFields =  [ 'constitution' ];
			mainForm.enableDisableField(lFields, !lEnable, true);
			mainForm.alterField(lFields, false, false);
			if(lPurc || lSupp){
				lFields =  [ 'finCertificateNo','finCertificateIssueDate' ];
				mainForm.enableDisableField(lFields,false, true);
				mainForm.alterField(lFields, false, false);
			}
			
		}
		function setConstitution(pVal,pEntityType) {
			if (pEntityType!=null){
				var lSupp = pEntityType && (pEntityType.length==3) && (pEntityType.charAt(0)==='Y');
				var lPurc = pEntityType && (pEntityType.length==3) && (pEntityType.charAt(1)==='Y');
				var lFin = pEntityType && (pEntityType.length==3) && (pEntityType.charAt(2)==='Y');
			}
			if (lFin){
				var lFields = ['dateOfIncorporation'];
				mainForm.enableDisableField(lFields, lFin, false);
				mainForm.alterField(lFields, !lFin, false);
			}else{
				var lPubPri = (pVal=='<%=AppConstants.RC_CONSTITUENTS_PRIVATE%>')||(pVal=='<%=AppConstants.RC_CONSTITUENTS_PUBLIC%>');
				var lFields = ['cinNo','dateOfIncorporation'];
				mainForm.enableDisableField(lFields, lPubPri, !lPubPri);
				mainForm.alterField(lFields, lPubPri, false);
			}
		}
		function setMsmeRegType(pVal) {
			var lMsme = !(pVal==null||pVal=='');
 			var lFields =  [ 'msmeRegNo','msmeRegDate', 'udin' ];
			mainForm.enableDisableField(lFields, lMsme, !lMsme);
			mainForm.alterField(lFields, lMsme, false);
			lFields =  [ 'msmeRegNo' ];
			mainForm.alterField(lFields, false, false);
			lFields =  [ 'msmeRegDate' ];
			mainForm.alterField(lFields, false, false);
			if (mainForm.getField('msmeRegType').getValue()=='UAN'){
				$('#contMsme label').text('Udyog Aadhar No');				
				$('#msmeRegNo').attr('placeholder','Udyog Aadhar No');				
			}
		}
		function setRegNos(pEntityType, pSector, pSTExempted)
		{
			return;
		
		}
		function populateRegLocation() {
			var lFields = ['Line1','Line2','Line3','ZipCode','Country','State','City','Salutation','FirstName','MiddleName','LastName','Email','Telephone','Mobile','Fax','District'];
			var lData = {"columnNames":lFields,"cdId":"<%=lEntityId%>","locationType":"<%=LocationType.RegOffice.getCode()%>","descr":"1"};
			var lUrl="companylocation/all";
				$.ajax({
			        url: lUrl,
			        data: JSON.stringify(lData),
			        type: 'POST',
			        success: function( pObj, pStatus, pXhr) {
			        	var lPtr=0;
			        	var lName;
		 				$.each(lFields, function(pIndex, pValue){
		 					if(pObj.length > 0){
		 						lName = lFields[lPtr];
		 						lName = lName.charAt(0).toLowerCase()+ lName.slice(1);
			 					mainForm.getField('cor'+lFields[lPtr]).setValue(pObj[0][lName]);
		 					}
		 					lPtr++;
		 				});
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	oldAlert("Some error occured");
			        }
			    });
		}

	</script>

</body>
</html>