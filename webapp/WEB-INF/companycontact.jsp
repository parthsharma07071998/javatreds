<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.common.base.CommonConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyContactBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
<head>
<title>TREDS | Registration</title>
<%
	String lTab = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("tab")));
	if (StringUtils.isBlank(lTab))
		lTab = "tabAll";
	String lEntityId = StringEscapeUtils
			.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("entityId")));
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
%>
<%@include file="includes1.jsp"%>
<link href="../css/datatables.css" rel="stylesheet" />
<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
</head>
<body class="skin-blue">
	<jsp:include page="regheader1.jsp">
		<jsp:param name="title" value="Registration" />
		<jsp:param name="desc" value="Management" />
	</jsp:include>

	<div class="content" id="contCompanyContact">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Management Details</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div style="display: none" id="frmSearch">
			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" id=btnNew><span
						class="glyphicon glyphicon-plus-sign"></span> Add Contact</a> <a></a>
				</div>
				<div class="">
					<a href="javascript:;" class="right_links" id=btnUltBeneficiary><span
						class="glyphicon glyphicon-plus-sign"></span> Ultimate Beneficiary</a> <a></a>
				</div>
			</div>
			<div>
				<div class="cloudTabs">
					<ul class="cloudtabs nav nav-tabs" id="mgmtTypeTabs">
						<li class="active1"><a href="#tabPromoters" data-toggle="tab">Promoter/Director/Partner</a></li>
						<li><a href="#tabAuthPer" data-toggle="tab">Authorised
								Officials</a></li>
						<li><a href="#tabAdmin" data-toggle="tab">Administrator</a></li>
						<li><a href="#tabUltimateBeneficiary" data-toggle="tab">Ultimate Beneficiary</a></li>
						<li><a href="#tabAll" data-toggle="tab">All</a></li>
					</ul>
				</div>
			</div>
			<div class="tab-pane panel panel-default" style="backcolor: none">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<div class="tab-content no-padding">
								<div id="tabPromoters" class="tab-pane active1"></div>
								<!-- tabPromoters -->

								<div id="tabAuthPer" class="tab-pane"></div>
								<!-- tabAuthPer -->
								<div id="tabAdmin" class="tab-pane"></div>
								<!-- tabAdmin -->
								<div id="tabUltimateBeneficiary" class="tab-pane"></div>
								<!-- tabUltimateBeneficiary -->
								<div id="tabAll" class="tab-pane"></div>
								<!-- tabAll -->

							</div>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->

		<!-- frmMain -->
		<div style="display: none" id="frmMain" class="xform">
			<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="salutation" class="label">Title:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="salutation"><option value="">Select
										Title</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="firstName" class="label">First Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="firstName" placeholder="First Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="middleName" class="label">Middle Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="middleName" placeholder="Middle Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="lastName" class="label">Last Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="lastName" placeholder="Last Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class = "nonmandfield">
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="gender" class="label">Gender:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="gender"><option value="">Select</option></select> <b
									class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="DOB" class="label">Date of Birth:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<i class="icon-append fa fa-clock-o"></i> <input type="text"
									id="DOB" placeholder="Date of Birth" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="pan" class="label">PAN:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="pan" placeholder="PAN"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2 nonmandfield" >
							<section>
								<label for="uidId" class="label">UID Id:</label>
							</section>
						</div>
						<div class="col-sm-4 nonmandfield">
							<section class="input">
								<input type="text" id="uidId" placeholder="UID Id"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="nonmandfield">
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="designation" class="label">Designation:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="designation"><option value="">Select
									</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="email" class="label">Email:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="email" placeholder="Email"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="telephone" class="label">Telephone No:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="telephone" placeholder="Telephone">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="noaEmail" class="label">Notice of Assignment
									Email:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="noaEmail" placeholder="Email"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="mobile" class="label">Mobile No:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="mobile" placeholder="Mobile"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="fax" class="label">FAX:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="fax" placeholder="FAX"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<div class="row" id="">
						<h4>
							<div class="col-sm-12">
								<section>
									<label for="cersaiDetails" class="label"><h4>Father's
											/Spouse Name</h4></label>
								</section>
							</div>
						</h4>
					</div>

					<div class="row" id="divCersaiDetails">
						<div class="col-sm-2">
							<section>
								<label for="cersaiSalutation" class="label">Title:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="cersaiSalutation"><option value="">Select
										Title</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="cersaiFirstName" class="label">First Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="cersaiFirstName" placeholder="First Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="cersaiMiddleName" class="label">Middle Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="cersaiMiddleName"
									placeholder="Middle Name"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="cersaiLastName" class="label">Last Name:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="cersaiLastName" placeholder="Last Name">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<hr></hr>

					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="admin" class="label"><b>Roles Assigned</b></label>
							</section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="admin" class="label">Administrator: </label>
							</section>
						</div>						
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox id="admin"><i></i>
									<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="adminAuthDate" class="label">Date of
									Authorisation (Admin):</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<i class="icon-append fa fa-clock-o"></i> <input type="text"
									id="adminAuthDate" placeholder="Date of Authorisation (Admin)"
									data-role="datetimepicker"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="authPer" class="label">Authorised Official</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox
									id="authPer"><i></i> <b
									class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div id="divAuthDates">
							<div class="col-sm-2">
								<section>
									<label for="authPerAuthDate" class="label">Date of
										Authorisation (Auth.Off.):</label>
								</section>
							</div>
							<div class="col-sm-4">
								<section class="input">
									<i class="icon-append fa fa-clock-o"></i> <input type="text"
										id="authPerAuthDate"
										placeholder="Date of Authorisation (Auth.Off.)"
										data-role="datetimepicker"> <b
										class="tooltip tooltip-top-right"></b>
								</section>
								<section class="view"></section>
							</div>
						</div>
					</div>
					<div class="row" style = "display : none">
						<div class="col-sm-2">
							<section>
								<label for="ultimateBeneficiary" class="label"></label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox id="ultimateBeneficiary"><i></i>
									<b class="tooltip tooltip-top-left"></b>(Ultimate Beneficiary Owner )</label>
							</section>
							<section class="view"></section>
						</div>
<!-- 						<div class="col-sm-2"> -->
<!-- 							<section> -->
<!-- 								<label for="ultimateBeneficiaryDate" class="label">Date of -->
<!-- 									Beneficiary:</label> -->
<!-- 							</section> -->
<!-- 						</div> -->
<!-- 						<div class="col-sm-4"> -->
<!-- 							<section class="input"> -->
<!-- 								<i class="icon-append fa fa-clock-o"></i> <input type="text" -->
<!-- 									id="ultimateBeneficiaryDate" placeholder="Date of Beneficiary(Ultimate)" -->
<!-- 									data-role="datetimepicker"> <b -->
<!-- 									class="tooltip tooltip-top-right"></b> -->
<!-- 							</section> -->
<!-- 							<section class="view"></section> -->
<!-- 						</div> -->
					</div>

					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="promoter" class="label">Promoter: </label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox
									id="promoter"><i></i> <b
									class="tooltip tooltip-top-left"></b></label>
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
								<select id="status"><option value="">Select</option></select> <b
									class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<hr></hr>
					<span id='cersaiPromoter' style="display:none;">
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="chiefPromoter" class="label">Chief Promoter:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox
									id="chiefPromoter"><i></i> <b
									class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="cersaiFlag" class="label">Authorized for
									CERSAI assignment:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox
									id="cersaiFlag"><i></i><span></span> <b
									class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" id="divChiefPromoter">
						<div class="col-sm-2">
							<section>
								<label for="cpCat" class="label">Category:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="cpCat"><option value="">Select
										Category</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="cpWomenEnt" class="label">Women:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox
									id="cpWomenEnt"><i></i><span></span> <b
									class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<hr></hr>
					</span>
					<div class="row" >
						<div class="col-sm-2">
							<section>
								<label for="dinNo" class="label">DIN No:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="dinNo" placeholder="DIN No"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="nationality" class="label">Nationality:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="inline-group">
								<label class="radio"><input type=radio id="nationality"><i></i><span></span>
									<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="othersNationality" class="label">Country:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="othersNationality"><option value="">Select
										Country</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2">
							<section>
								<label for="residentailStatus" class="label">Residential
									Status:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="inline-group">
								<label class="radio"><input type=radio
									id="residentailStatus"><i></i><span></span> <b
									class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<hr></hr>

					<div class="row" id='resDiv'>
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resLine1" class="label">Residential Address:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resLine1" placeholder="Line 1">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resLine2" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resLine2" placeholder="Line 2">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resLine3" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resLine3" placeholder="Line 3">
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
										<label for="resZipCode" class="label">Zip Code:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resZipCode" placeholder="Zip Code">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resCity" class="label">City:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resCity" placeholder="City"> <b
											class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resDistrict" class="label">District:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="resDistrict" placeholder="District">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resState" class="label">State:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="select">
										<select id="resState"><option value="">Select
												State</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="resCountry" class="label">Country:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="select">
										<select id="resCountry"><option value="">Select
												Country</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
									</section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>

					

					<div class="row" id='nriDiv'>
						<div class="col-sm-6">
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriLine1" class="label">
											Residential Address:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriLine1" placeholder="Line 1">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriLine2" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriLine2" placeholder="Line 2">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriLine3" class="label"></label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriLine3" placeholder="Line 3">
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
										<label for="nriZipCode" class="label">Zip Code:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriZipCode" placeholder="Zip Code">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriCity" class="label">City:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriCity" placeholder="City"> <b
											class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriDistrict" class="label">District:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriDistrict" placeholder="District">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriState" class="label">State:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="input">
										<input type="text" id="nriState" placeholder="State">
										<b class="tooltip tooltip-top-right"></b>
									</section>
									<section class="view"></section>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-4">
									<section>
										<label for="nriCountry" class="label">Country:</label>
									</section>
								</div>
								<div class="col-sm-8">
									<section class="select">
										<select id="nriCountry"><option value="">Select
												Country</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
									</section>
									<section class="view"></section>
								</div>
							</div>
						</div>
					</div>

					<hr></hr>

					<div class="row" style="display:none">
						<div class="col-sm-2">
							<section>
								<label for="occupation" class="label">Occupation:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="occupation"><option value="">Select
										Occupation</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="othersOccupation" class="label">Others:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="othersOccupation"
									placeholder="Occupation"> <b
									class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<div class="row" style="display:none">
						<div class="col-sm-2">
							<section>
								<label for="grossIncome" class="label">Gross Income:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="select">
								<select id="grossIncome"><option value="">Select
										Income</option></select> <b class="tooltip tooltip-top-right"></b><i></i>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2">
							<section>
								<label for="networth" class="label">Net Worth:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<input type="text" id="networth" placeholder="Net Worth">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>

					<div class="row" style="display:none">
						<div class="col-sm-2">
							<section>
								<label for="date" class="label">Net Worth as on Date:</label>
							</section>
						</div>
						<div class="col-sm-4">
							<section class="input">
								<i class="icon-append fa fa-clock-o"></i> <input type="text"
									id="date" placeholder="Date" data-role="datetimepicker">
								<b class="tooltip tooltip-top-right"></b>
							</section>
							<section class="view"></section>
						</div>
					</div>
					</div>

					<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<input type="hidden" id="force" />
									<button type="button" class="btn btn-info btn-lg btn-enter"
										id=btnSave>
										<span class="fa fa-save"></span> Save
									</button>
									<button type="button"
										class="btn btn-info-inverse btn-lg btn-close" id=btnClose>
										<span class="fa fa-close"></span> Close
									</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmMain -->
	</div>

	<%@include file="footer1.jsp"%>
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script id="tplContacts" type="text/x-handlebars-template">
		{{#each this}}
		<div class="col-md-6 col-sm-12">
			<div class="lighter-gray-bx">
				<div class="panel-heading">
					<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
						{{#if admin}} - Administrator{{/if}}
						{{#if promoter}} - Promoter{{/if}}
						{{#if authPer}} - Auth. Person{{/if}}
						{{#if ultimateBeneficiary}} - Ultimate Beneficiary{{/if}}
					</h3>
					<div class="panel_right_info pull-right">
						{{#ifCond creatorIdentity '!=' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyContact.modifyHandler(null,[{{id}},{{isProvisional}}],false)"><i class="glyphicon glyphicon-pencil"></i> </a>
						&nbsp;
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyContact.removeHandler(null,[[{{id}}]])"><i class="glyphicon glyphicon-trash"></i> </a>
						{{/ifCond}}
						{{#ifCond creatorIdentity '==' "J"}}
						<a href="javascript:;" class="right_links" onClick="javascript:crudCompanyContact.viewHandler(null,[{{id}},{{isProvisional}}])"><i class="glyphicon glyphicon-eye-open"></i> </a>		
						{{/ifCond}}
						<a></a>
					</div>
				</div>
			{{#if ultimateBeneficiary}} 
			{{else}}
			<div class="row horiz_table" >
				<div class="col-md-6">
					<table class="table">
						<tbody><tr>
							<td class="table-header">Designation</td>
							<td class="table-content">{{designation}}</td>
						</tr>
						<tr>
							<td class="table-header">Email</td>
							<td class="table-content">{{email}}</td>
						</tr>
						{{#if admin}}
						<tr>
							<td class="table-header">NOA Email</td>
							<td class="table-content">{{noaEmail}}</td>
						</tr>
						{{/if}}
					</tbody></table>
				</div>
				<div class="col-md-6">
					<table class="table">
						<tbody><tr>
							<td class="table-header">Telephone</td>
							<td class="table-content">{{telephone}}</td>
						</tr>
						<tr>
							<td class="table-header">Mobile</td>
							<td class="table-content">{{mobile}}</td>
						</tr>
						<tr>
							<td class="table-header">Fax</td>
							<td class="table-content">{{fax}}</td>
						</tr>
					</tbody></table>
				</div>
				
			</div>
{{/if}}	
			</div>
		</div>

		</div>
		{{/each}}
		{{^if this}}No Data!{{/if}}
	</script>
	<script type="text/javascript">
	var crudCompanyContact$ = null;
	var crudCompanyContact = null;
	var mainForm = null;
	var tplContacts;
	var lFormConfig = null;
	var dataHash = null; //Key=tabName, Value=HashOfJsonObjs
	var lCounter=0;
	
	$(document).ready(function() {
		tplContacts = Handlebars.compile($('#tplContacts').html());
		lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(CompanyContactBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "companycontact",
				keyFields:["id","isProvisional"],
				autoRefresh: true,
				preSearchHandler: function(pFilter) {
					pFilter.cdId=<%=lEntityId%>;
					pFilter.isProvisional=<%=lIsProv%>;
					return true;
				},
				postNewHandler: function() {
						mainForm.getField('nationality').setValue('I');
						setAdmin(null);
						setPromoter(null);
						setChiefPromoter(null);
						setAuthorisedPerson(null);
						setUltimateBeneficiary(null);
						setAuthorisedAdmin(null);
						setNationality(mainForm.getField('nationality').getValue());
						
						mainForm.enableDisableField('residentailStatus', true, false);
						mainForm.getField('residentailStatus').setValue('RI');
						setNationality(mainForm.getField('nationality').getValue());
						//setResDetails(mainForm.getField('residentailStatus').getValue(),mainForm.getField('nationality').getValue());
						mainForm.getField('residentailStatus').setValue('RI');
						//setNRIDetails(mainForm.getField('residentailStatus').getValue(),mainForm.getField('nationality').getValue());
						setOccupation(null);
						ultimateBeneficiaryChanges(mainForm, true);
						mainForm.alterField('designation', true, false);
						makeFieldsMandatory();
						setAddrDetails(mainForm.getField('residentailStatus').getValue(),mainForm.getField('nationality').getValue());
					return true;
				},
				postModifyHandler: function(pObj) {
						setAdmin(pObj.admin);
						setPromoter(pObj.promoter);
						setChiefPromoter(pObj.chiefPromoter);
						setAuthorisedPerson(pObj.authPer);
						setUltimateBeneficiary(pObj.ultimateBeneficiary);
						setAuthorisedAdmin(pObj.admin);
						setOccupation(pObj.occupation);
						mainForm.setValue(pObj);
						makeFieldsMandatory();
						if ($('#promoter').prop('checked')){ 
							$('#cersaiPromoter').show();
						}
						if (pObj.ultimateBeneficiary != null){ 
							ultimateBeneficiaryChanges(mainForm,false);
						}else{
							ultimateBeneficiaryChanges(mainForm, true);
						}
						setNationality(pObj.nationality);
						//setResDetails(pObj.residentailStatus,pObj.nationality);
						//setNRIDetails(pObj.residentailStatus,pObj.nationality);
						setAddrDetails(pObj.residentailStatus,pObj.nationality)
						mainForm.getField('othersNationality').setValue(pObj.othersNationality);
						mainForm.getField('residentailStatus').setValue(pObj.residentailStatus);
						if (pObj.ultimateBeneficiary == null){ 
							mainForm.alterField('designation', true, false);
						}
						setOldModifiedValue(pObj.modifiedData);
					return true;
				},
				postSearchHandler: function(pData) {
					var lTempHash;
					$('#tabPromoters').html(null);
					$('#tabAuthPer').html(null);
					$('#tabAdmin').html(null);
					$('#tabUltimateBeneficiary').html(null);
					//
					lSearchData = pData.list;
					dataHash = new Array();
					dataHash.push(new Array()); //"tabPromoters"
					dataHash.push(new Array()); //"tabAuthPer"
					dataHash.push(new Array()); //"tabAdmin"
					dataHash.push(new Array()); //"tabUltimateBeneficiary"
					dataHash.push(new Array()); //"tabAll"
					
					lSearchData.forEach(function(item) {
							if(item.promoter=="Yes"){
								lTempHash = dataHash[0];
								lTempHash.push(item);
								//console.log("tabPromoters :: " +item.firstName); 
							}
							if(item.authPer=="Yes"){
								lTempHash = dataHash[1];
								lTempHash.push(item);
								//console.log("tabAuthPer :: " +item.firstName); 
							}
							if(item.admin=="Yes"){
								lTempHash = dataHash[2];
								lTempHash.push(item);
								//console.log("tabAdmin :: " +item.firstName); 
							}
							if(item.ultimateBeneficiary=="Yes"){
								lTempHash = dataHash[3];
								lTempHash.push(item);
								//console.log("tabAdmin :: " +item.firstName); 
							}
								dataHash[4].push(item);
					});
					$('#tabPromoters').html(tplContacts(dataHash[0]));
					$('#tabAuthPer').html(tplContacts(dataHash[1]));
					$('#tabAdmin').html(tplContacts(dataHash[2]));
					$('#tabUltimateBeneficiary').html(tplContacts(dataHash[3]));
					$('#tabAll').html(tplContacts(dataHash[4]));
					if (pData.creatorIdentity=='J'){
						$('#btnUltBeneficiary').hide();
						$('#btnNew').hide();
					}
					return false;
				},
				preCheckHandler : function() {
					if (mainForm.getField('ultimateBeneficiary').getValue()!=null){
						mainForm.alterField('designation', false, false);
						var lFields = lFormConfig.fieldGroups.res;
						mainForm.alterField(lFields, false, false);
						lFields = lFormConfig.fieldGroups.nri;
						mainForm.alterField(lFields, false, false);
					}
					return true;
				},
				preSaveHandler: function(pObj) {
					var lRole = (($('#admin').val()==='<%=CommonAppConstants.YesNo.Yes.getCode()%>') || ($('#authPer').val()==='<%=CommonAppConstants.YesNo.Yes.getCode()%>') || ($('#promoter').val()==='<%=CommonAppConstants.YesNo.Yes.getCode()%>') || ($('#ultimateBeneficiary').val()==='<%=CommonAppConstants.YesNo.Yes.getCode()%>'));
					if (!lRole) {
						alert('Atleast one Role Assigned should be selected.','Error', function() {
							$('#admin').focus();
						});
						return false;
					}
					pObj.cdId=<%=lEntityId%>;
					return true;
				},
				errorHandler: function( pXhr, pStatus, pError ) {
					var lMsg = null;
					try {
						var lErrObj = JSON.parse(pXhr.responseText);
						lMsg = lErrObj.messages[0];
					} catch (e) {
					}
					if (!lMsg) lMsg = "Some error occurred : " + pXhr.responseText;
					if (lMsg.substring(0,5) === 'WARN:') {
						var lConfMsg = "Person with name " + lMsg.substring(5) + " is already defined as administrator. <br>Do you want to replace the existing administrator?";
						confirm(lConfMsg, "Administrator Replacement", null, function(pYes) {
							if (pYes) {
								mainForm.getField('force').setValue('Y');
								crudCompanyContact.saveHandler();
							}
						});
					} else {
						alert(lMsg, "Error");
					}
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		crudCompanyContact$ = $('#contCompanyContact').xcrudwrapper(lConfig);
		crudCompanyContact = crudCompanyContact$.data('xcrudwrapper');
		mainForm = crudCompanyContact.options.mainForm;
		
		$('#admin').on('change', function() {
			setAdmin(mainForm.getField('admin').getValue());
			setAuthorisedAdmin(mainForm.getField('admin').getValue());
		});
		$('#promoter').on('change', function() {
			setPromoter(mainForm.getField('promoter').getValue());
			if ($('#promoter').prop('checked')){ 
				$('#cersaiPromoter').show();
			}else{
				$('#cersaiPromoter').hide();
			}
		});
		$('#chiefPromoter').on('change', function() {
			setChiefPromoter(mainForm.getField('chiefPromoter').getValue());
		});
		$('#authPer').on('change', function() {
			setAuthorisedPerson(mainForm.getField('authPer').getValue());
		});
		$('#ultimateBeneficiary').on('change', function() {
			setUltimateBeneficiary(mainForm.getField('ultimateBeneficiary').getValue());
		});
		$('#nationality, #nationality1').on('change', function() {
			setNationality(mainForm.getField('nationality').getValue());
		});
		$('#residentailStatus, #residentailStatus1').on('change', function() {
			var lNationality = mainForm.getField('nationality').getValue();
			var lResStatus = mainForm.getField('residentailStatus').getValue();
			setAddrDetails(lResStatus,lNationality);
			//setResDetails(lResStatus,lNationality);
			//setNRIDetails(lResStatus,lNationality );
		});
		$('#occupation').on('change', function() {
			setOccupation(mainForm.getField('occupation').getValue());
		});
		$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
			if ($('#frmMain').is(':visible')) {
				crudCompanyContact.closeHandler();
			}
		});
		$('#btnUltBeneficiary').on('click', function(){
			mainForm = crudCompanyContact.options.mainForm;
			crudCompanyContact.newHandler();
			ultimateBeneficiaryChanges(mainForm, false);
		});
		showTab('<%=lTab%>');
	});
	function showTab(pTab) {
		$('#frmSearch .nav-tabs a[href="#'+pTab+'"]').tab('show');
	}
	function setAdmin(pVal) {
		var lFlag = (pVal!=null);
		var lFields = lFormConfig.fieldGroups.admin;
		mainForm.enableDisableField(lFields, lFlag, true);
// 		mainForm.alterField(lFields, lFlag, false);
		mainForm.alterField(["noaEmail"], lFlag, false);			
		mainForm.enableDisableField(["noaEmail"], lFlag, false);
	}
	function setPromoterOrAuthorisedPerson() {
		var lPromoterFlag = mainForm.getField('promoter').getValue();
		var lAuthPerFlag = mainForm.getField('authPer').getValue();
		var lFlag = lPromoterFlag || lAuthPerFlag;
		lFields = ["cersaiFlag"];
		mainForm.enableDisableField(lFields, lFlag, !lFlag);
		mainForm.alterField(lFields, false, false);			
	}
	function setPromoter(pVal) {
		var lFlag = (pVal!=null);
		var lFields = lFormConfig.fieldGroups.promoter;
		mainForm.enableDisableField(lFields, lFlag, true);
		mainForm.alterField(lFields, false, false);
		if(!lFlag) setChiefPromoter(null);
		setPromoterOrAuthorisedPerson();
	}
	function setChiefPromoter(pVal) {
		var lFlag = (pVal == '<%=CommonAppConstants.Yes.Yes.getCode()%>');
		var lFields = ["cpCat","cpWomenEnt"] ;//lFormConfig.fieldGroups.chiefPromoter;
		mainForm.enableDisableField(lFields, lFlag, true);
		lFields = ["cpWomenEnt"] ;
		mainForm.alterField(lFields, false, false);
	}
	function setAuthorisedPerson(pVal) {
		var lFlag = (pVal == '<%=CommonAppConstants.Yes.Yes.getCode()%>');
		var lFields = lFormConfig.fieldGroups.authorisedPerson; 
		mainForm.enableDisableField(lFields, lFlag, true);
//		mainForm.alterField(lFields, lFlag, false);
// 		setPromoterOrAuthorisedPerson();
	}
	function setUltimateBeneficiary(pVal) {
		var lFlag = (pVal == '<%=CommonAppConstants.Yes.Yes.getCode()%>');
		setPromoterOrAuthorisedPerson();
	}
	function setAuthorisedAdmin(pVal) {
		var lFlag = (pVal == '<%=CommonAppConstants.Yes.Yes.getCode()%>');
			var lFields = lFormConfig.fieldGroups.admin;
			mainForm.enableDisableField(lFields, lFlag, true);
// 			mainForm.alterField(lFields, lFlag, false);
		}
	function setNationality(pVal) {
		if(pVal=='I'){
			lFlag=(pVal=='I')
			var lFields = lFormConfig.fieldGroups.res;
			mainForm.enableDisableField(lFields, lFlag, false);
			var lFields = ["resLine1","resCountry","resState","resDistrict","resCity","resZipCode"];
			mainForm.alterField(lFields, lFlag, false);
			lFields = lFormConfig.fieldGroups.nri;
			mainForm.enableDisableField(lFields, !lFlag, true);
			mainForm.alterField(lFields, !lFlag, false);
			mainForm.alterField("pan", true, false);
			var lFields = 'othersNationality';
			mainForm.enableDisableField(lFields, !lFlag, true);
			mainForm.alterField(lFields, !lFlag, false);
			$('#nriDiv').hide();
			$('#resDiv').show();
		}else if(pVal=='O'){
			var lFlag = (pVal=='O');
			//enable
			//address
			var lFields = lFormConfig.fieldGroups.nri;
			mainForm.alterField(lFields, false, false);
			mainForm.enableDisableField(lFields, lFlag, false);
			var lFields = ["nriLine1","nriCountry","nriZipCode"];
			mainForm.alterField(lFields, lFlag, false);
			mainForm.alterField("pan", false, false);
			// disable
			var lFields = lFormConfig.fieldGroups.res;
			mainForm.enableDisableField(lFields, !lFlag, true);
			mainForm.alterField(lFields, !lFlag, false);
			$('#resDiv').hide();
			$('#nriDiv').show();
		}
		var lFields = 'othersNationality';
		mainForm.enableDisableField(lFields, (pVal=='O'), true);
		mainForm.alterField(lFields, (pVal=='O'), false);
		var lFields = 'residentailStatus';
		mainForm.enableDisableField(lFields, (pVal=='I'), true);
		mainForm.alterField(lFields, (pVal=='I'), false);
		if (pVal=='O'){
			mainForm.getField('residentailStatus').setValue('FN');
		}
		
	}
	
	function setNRIDetails(pVal,pVal1) {
		var lFlag = (pVal=='NRI' || (pVal=='FN' && pVal1=='O'));
		var lFields = lFormConfig.fieldGroups.nri;
		mainForm.enableDisableField(lFields, lFlag, false);
		if (lFlag){
			$('#resDiv').hide();
			var lFields = lFormConfig.fieldGroups.res;
			mainForm.alterField(lFields, false, false);
			$('#nriDiv').show();
		}
		var lFields = ["nriLine1","nriCountry","nriZipCode"];
		mainForm.alterField(lFields, lFlag, false)
	}
	
	function setResDetails(pVal,pVal1) {
		var lFlag = (pVal1=='I' && pVal=='RI' );
		var lFields = lFormConfig.fieldGroups.res;
		mainForm.enableDisableField(lFields, lFlag, false);
		if (lFlag){
			$('#nriDiv').hide();
			var lFields = lFormConfig.fieldGroups.nri;
			mainForm.alterField(lFields, false, false);
			$('#resDiv').show();
		}
		var lFields = ["resLine1","resCountry","resZipCode","resCity","resState","resDistrict"];
		mainForm.alterField(lFields, lFlag, false)
	}
	
	function setOccupation(pVal) {
		var lFlag = (pVal=='OTR');
		var lFields = 'othersOccupation';
		mainForm.enableDisableField(lFields, lFlag, true);
		mainForm.alterField(lFields, lFlag, false);
	}
	function makeFieldsMandatory(){
		var lFields = ['pan','gender'];
		mainForm.alterField(lFields, true, false);
	}
	function ultimateBeneficiaryChanges(pmainForm,pFlag){
		lFields = ["salutation","pan","firstName","cersaiSalutation","cersaiFirstName","DOB","gender","telephone","mobile","email","nationality","residentailStatus","resLine1","resZipCode","resCity","resDistrict","resState","resCountry","nriLine1","nriZipCode","nriCity","nriDistrict","nriState","nriCountry"];
		mainForm.alterField(lFields, pFlag, false);
		if(!pFlag){
			$('.nonmandfield').hide();
			$('#ultimateBeneficiary').prop('checked',true);
		}else{
			$('.nonmandfield').show();
			lFields = ["cersaiSalutation","cersaiFirstName","cersaiLastName","cersaiMiddleName"];
			mainForm.enableDisableField(lFields, true, false);
			$('#ultimateBeneficiary').prop('checked',false);
		}
		
	}
	
	function setAddrDetails(pVal,pVal1) {
		var lRIFlag = (pVal1=='I' && pVal=='RI' );
		var lNRIFlag = (pVal=='NRI' || (pVal=='FN' && pVal1=='O'));
		if (lNRIFlag){
			var lFields = lFormConfig.fieldGroups.nri;
			mainForm.enableDisableField(lFields, lNRIFlag, false);
			if (lNRIFlag){
				$('#resDiv').hide();
				var lFields = lFormConfig.fieldGroups.res;
				mainForm.alterField(lFields, false, false);
				$('#nriDiv').show();
			}
			var lFields = ["nriLine1","nriCountry","nriZipCode"];
			mainForm.alterField(lFields, lNRIFlag, false);
		}
		if (lRIFlag){
			var lFields = lFormConfig.fieldGroups.res;
			mainForm.enableDisableField(lFields, lRIFlag, false);
			if (lRIFlag){
				$('#nriDiv').hide();
				var lFields = lFormConfig.fieldGroups.nri;
				mainForm.alterField(lFields, false, false);
				$('#resDiv').show();
			}
			var lFields = ["resLine1","resCountry","resZipCode","resCity","resState","resDistrict"];
			mainForm.alterField(lFields, lRIFlag, false)
		}
		
	}
	function setOldModifiedValue(pModifiedJson){
     	//clear the previous red marking
     	var lFieldGroup = crudCompanyContact.options.fieldGroups.update;
     	for(var lPtr=0; lPtr < lFieldGroup.length;  lPtr++){
     		$('#'+lFieldGroup[lPtr]).css('border-color','');
     	}
     	//mark modified fields red
     	if(pModifiedJson!=null&&pModifiedJson!=''){
     		var lData = pModifiedJson;
     		if(lData!=null){
     			for(var key in lData){
     				//$('#ov-'+key).html(lData[key]);
     				$('#'+key).css('border-color','red');
     			}
     		}
     	}
     }
	</script>
</body>
</html>