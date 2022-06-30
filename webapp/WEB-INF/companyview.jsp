<!DOCTYPE html>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = StringEscapeUtils.escapeHtml(request.getParameter("id"));
String lFinal = StringEscapeUtils.escapeHtml(request.getParameter("final"));
String lEntityCode = StringEscapeUtils.escapeHtml(request.getParameter("entityCode"));
String lUrl = "companyview";
String lParam = "";
if (lId!=null) lParam += "id="+lId;
if (lFinal != null){
	if(lParam.length() > 0) lParam += "&";
	lParam += "final="+lFinal;
}
if (lEntityCode != null){
	if(lParam.length() > 0) lParam += "&";
	lParam += "entityCode="+lEntityCode;
}
lUrl += "?" + lParam;
%>
<html>
    <head>
        <title>TREDS | Entity Profile</title>
        <%@include file="includes1.jsp" %>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Entity Profile" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAppUser">
		<!-- frmMain -->
    	<div class="xform view" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Entity Profile</h1>
				</div>
			</div>
			<div class="filter-block clearfix" style='display:<%=CommonUtilities.hasValue(lEntityCode)?"":"none"%>'>
				<div class="">
					<span class="right_links">
						<a href="javascript:;" class="right_links" id=btnClose><span class="glyphicon glyphicon-remove"></span> Close</a>
						<a></a>
					</span>
				</div>
			</div>
			<div class="xform box">
				<div class="box-body panel-group panel-group-joined" id="accordion">
					<div class="panel panel-default">
					</div>
				</div>
			</div>
		</div>
    	<!-- frmMain -->
	</div>
  	<div class="modal fade" tabindex=-1 id="mdl-image"><div class="modal-dialog modal-xl"><div class="modal-content"><div class="modal-body"><div class="container">
	<div class="xform box">
   		<div class="modal-header">
			<span></span>
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-close" data-dismiss="modal"><span class="fa fa-close"></span></button>
					</div>
				</div>
			</div>
   		</div>
   		<fieldset>
    	<div class="box-body text-center">
    	</div>
    		<div class="box-footer">
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
					</div>
				</div>
			</div>
    		</div>
    	</fieldset>
    </div>
</div></div></div></div></div>

	<div id="imageDiv" style="display:none">
		<div id="imageBox">
		</div>
		<div class="modal-footer">
			<div class="row">
				<div class="col-sm-12">
					<div class="btn-groupX pull-right">
						<button type="button" class="btn btn-info-inverse btn-lg btn-close" id="btnImageClose" onClick="javascript:closeImage()"><span class="fa fa-close"></span> Close</button>
					</div>
				</div>
			</div>
   		</div>
	</div>

   	<%@include file="footer1.jsp" %>
   	<script id="tplCompanyView" type="text/x-handlebars-template">
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd1">General Details</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd1">
					<div class="panel-body">
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Name of applicant entity:</label></section></div>
							<div class="col-sm-8"><section class="view">{{companyName}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Entity Type:</label></section></div>
							<div class="col-sm-4"><section class="view">{{type}}</section></div>
							<div class="col-sm-2"><section><label class="label">Constitution:</label></section></div>
							<div class="col-sm-4"><section class="view">{{constitution}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">RBI Registration Certificate Number:</label></section></div>
							<div class="col-sm-4"><section class="view">{{finCertificateNo}}</section></div>
							<div class="col-sm-2"><section><label class="label">Issue Date:</label></section></div>
							<div class="col-sm-4"><section class="view">{{finCertificateIssueDate}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">CIN No:</label></section></div>
							<div class="col-sm-4"><section class="view">{{cinNo}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Date of Incorporation:</label></section></div>
							<div class="col-sm-4"><section class="view">{{dateOfIncorporation}}</section></div>
							<div class="col-sm-2"><section><label class="label">Existence in Business:</label></section></div>
							<div class="col-sm-4"><section class="view">{{existenceYears}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Industry Category:</label></section></div>
							<div class="col-sm-4"><section class="view">{{industry}}</section></div>
							<div class="col-sm-2"><section><label class="label">Industry Sub-segment:</label></section></div>
							<div class="col-sm-4"><section class="view">{{subSegment}} </section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Sector:</label></section></div>
							<div class="col-sm-4"><section class="view">{{sector}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Export Orientation:</label></section></div>
							<div class="col-sm-4"><section class="view">{{exportOrientation}}</section></div>
							<div class="col-sm-2"><section><label class="label">Currency:</label></section></div>
							<div class="col-sm-4"><section class="view">{{currency}}</section></div>
						</div>
						<hr>
						<div class="row">
							<div class="col-sm-2"><section><label class="label">Brief Description of Company:</label></section></div>
							<div class="col-sm-8"><section class="view">{{companyDesc}}</section></div>
						</div>
					</div>
				</div>

				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd2">Addresses</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd2">
					<div class="panel-body">					
					</div><div class="col-sm-12">
						<div class="row">
							<div class="col-sm-12"><h4><span class="fa fa-envelope"></span> Correspondence Address</h4>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Address:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corLine1}} {{corLine2}} {{corLine3}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label class="label">City:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corCity}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label class="label">State:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corState}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Country:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corCountry}}</section></div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Zip Code:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corZipCode}}</section></div>
						</div>					
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Contact Name:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corSalutation}} {{corFirstName}} {{corMiddleName}} {{corLastName}}</section></div>
						</div>					
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Telephone No:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corTelephone}}</section></div>
						</div>					
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Mobile:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corMobile}}</section></div>
						</div>					
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Fax:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corFax}}</section></div>
						</div>					
						<div class="row">
							<div class="col-sm-4"><section><label class="label">Email:</label></section></div>
							<div class="col-sm-8"><section class="view">{{corEmail}}</section></div>
						</div>					
					</div></div>
					</div>
				</div>

				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd3">Statutory Approvals</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd3">
					<div class="panel-body">
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Pemanant Account Number (PAN):</label></section></div>
					<div class="col-sm-4"><section class="view">{{pan}}</section></div>
					<div class="col-sm-2"><section><label class="label">Value Added Tax (VAT) Registration No.:</label></section></div>
					<div class="col-sm-4"><section class="view">{{vat}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Central Sales Tax (CST) Registration No.:</label></section></div>
					<div class="col-sm-4"><section class="view">{{cst}}</section></div>
					<div class="col-sm-2"><section><label class="label">Local Body Tax (LBT) Registration No.:</label></section></div>
					<div class="col-sm-4"><section class="view">{{lbt}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Service Tax Registration No:</label></section></div>
					<div class="col-sm-4"><section class="view">{{stRegNo}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Excise Registration No:</label></section></div>
					<div class="col-sm-4"><section class="view">{{exciseRegNo}}</section></div>
					<div class="col-sm-2"><section><label class="label">Tax deduction Account No. (TAN):</label></section></div>
					<div class="col-sm-4"><section class="view">{{tan}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Registered State GSTN:</label></section></div>
					<div class="col-sm-4"><section class="view">{{gstn}}</section></div>
				</div>
					</div>
				</div>

				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd4">MSME Details</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd4">
					<div class="panel-body">
				<div class="row">
					<div class="col-sm-2"><section><label class="label">MSME Status:</label></section></div>
					<div class="col-sm-4"><section class="view">{{msmeStatus}}</section></div>
					<div class="col-sm-2"><section><label class="label">MSME Registration No:</label></section></div>
					<div class="col-sm-4"><section class="view">{{msmeRegNo}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Registration Date:</label></section></div>
					<div class="col-sm-4"><section class="view">{{msmeRegDate}}</section></div>
					<div class="col-sm-2"><section><label class="label">Name of CA Firm:</label></section></div>
					<div class="col-sm-4"><section class="view">{{caName}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Membership No of CA Firm:</label></section></div>
					<div class="col-sm-4"><section class="view">{{caMemNo}}</section></div>
					<div class="col-sm-2"><section><label class="label">Date of CA Certificate:</label></section></div>
					<div class="col-sm-4"><section class="view">{{caCertDate}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label class="label">Investment in Core Plant & Machinery:</label></section></div>
					<div class="col-sm-4"><section class="view">{{invtCPM}}</section></div>
					<div class="col-sm-2"><section><label class="label">(Invt) Date of CA Certificate:</label></section></div>
					<div class="col-sm-4"><section class="view">{{invtDateCPM}}</section></div>
				</div>
					</div>
				</div>
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd5">Other Details</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd5">
					<div class="panel-body">
				<div class="row">
					<div class="col-sm-2"><section><label for="" class="label">Annual MSME Purchases ( <span class="fa fa-rupee"></span> lakhs):</label></section></div>
					<div class="col-sm-4"><section class="view">{{annualMsmePurchase}}</section></div>
					<div class="col-sm-2"><section><label for="" class="label">Sales Turnover as per last audited account ( <span class="fa fa-rupee"></span> lakhs):</label></section></div>
					<div class="col-sm-4"><section class="view">{{salesTo}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="" class="label">Financial Year:</label></section></div>
					<div class="col-sm-4"><section class="view">{{salesYear}}</section></div>
					<div class="col-sm-2"><section><label for="" class="label">Type of Banking Facilities:</label></section></div>
					<div class="col-sm-4"><section class="view">{{bankingType}}</section></div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="" class="label">Names of the Top 3 Customers:</label></section></div>
					<div class="col-sm-8"><section class="view" style="height:auto">{{customer1}}<br>{{customer2}}<br>{{customer3}}</section></div>
				</div>
					</div>
				</div>
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd6">Chief Promoters</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd6">
					<div class="panel-body">
						<div class="row">
							{{#each contacts}}{{#if promoter}}{{#if chiefPromoter}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
										</h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody><tr>
												<td class="table-header">Designation</td>
												<td class="table-content">{{designation}}</td>
											</tr>
											<tr>
												<td class="table-header">Women Enterprenuer</td>
												<td class="table-content">{{cpCat}}</td>
											</tr>
											<tr>
												<td class="table-header">Category</td>
												<td class="table-content">{{cpWomenEnt}}</td>
											</tr>
											<tr>
												<td class="table-header">DOB</td>
												<td class="table-content">{{dob}}</td>
											</tr>
											<tr>
												<td class="table-header">CERSAI Authorised</td>
												<td class="table-content">{{cersaiFlag}}</td>
											</tr>
											<tr>
												<td class="table-header">Father/Spouse Name</td>
												<td class="table-content">{{cersaiSalutation}} {{cersaiFirstName}} {{cersaiMiddleName}} {{cersaiLastName}}</td>
											</tr>
										</tbody></table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tr>
												<td class="table-header">Email</td>
												<td class="table-content">{{email}}</td>
											</tr>
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
								</div>
							</div>
							</div>
							{{/if}}{{/if}}{{/each}}
						</div>
					</div>
				</div>
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd7">Other Promoters</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd7">
					<div class="panel-body">
						<div class="row">
							{{#each contacts}}{{#if promoter}}{{#if chiefPromoter}}{{else}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
										</h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody><tr>
												<td class="table-header">Designation</td>
												<td class="table-content">{{designation}}</td>
											</tr>
											<tr>
												<td class="table-header">DOB</td>
												<td class="table-content">{{dob}}</td>
											</tr>
											<tr>
												<td class="table-header">CERSAI Authorised</td>
												<td class="table-content">{{cersaiFlag}}</td>
											</tr>
											<tr>
												<td class="table-header">Father/Spouse Name</td>
												<td class="table-content">{{cersaiSalutation}} {{cersaiFirstName}} {{cersaiMiddleName}} {{cersaiLastName}}</td>
											</tr>
										</tbody></table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tbody>
											<tr>
												<td class="table-header">Email</td>
												<td class="table-content">{{email}}</td>
											</tr>
											<tr>
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
								</div>
							</div>

							</div>
							{{/if}}{{/if}}{{/each}}
						</div>
					</div>
				</div>

				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd8">Authorized Officals</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd8">
					<div class="panel-body">
						<div class="row">
							{{#each contacts}}{{#if authPer}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
										</h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody><tr>
												<td class="table-header">Designation</td>
												<td class="table-content">{{designation}}</td>
											</tr>
											<tr>
												<td class="table-header">Authorization Date<br>(Board Resolution)</td>
												<td class="table-content">{{authPerAuthDate}}</td>
											</tr>
										</tbody></table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tr>
												<td class="table-header">Email</td>
												<td class="table-content">{{email}}</td>
											</tr>
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
								</div>
							</div>
							</div>
							{{/if}}{{/each}}
						</div>
					</div>
				</div>


				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd9">Administrator</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd9">
					<div class="panel-body">
						<div class="row">


							{{#each contacts}}{{#if admin}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title"> {{salutation}} {{firstName}} {{middleName}} {{lastName}} 
										</h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody><tr>
												<td class="table-header">Designation</td>
												<td class="table-content">{{designation}}</td>
											</tr>
											<tr>
												<td class="table-header">Authorization Date</td>
												<td class="table-content">{{adminAuthDate}}</td>
											</tr>
										</tbody></table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tbody>
											<tr>
												<td class="table-header">Email</td>
												<td class="table-content">{{email}}</td>
											</tr>
											<tr>
												<td class="table-header">NOA Email</td>
												<td class="table-content">{{noaEmail}}</td>
											</tr>
											<tr>
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
								</div>
							</div>

							</div>
							{{/if}}{{/each}}
						</div>
					</div>
				</div>



				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd10">Locations/Branches</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd10">
					<div class="panel-body">
						<div class="row">
							{{#each locations}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title"><b>{{name}} - GSTN : {{gstn}}</b></h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody>
												<tr>
													<td class="table-header">Address</td>
													<td class="table-content">{{line1}}</td>
												</tr>
												<tr>
													<td class="table-header">Address 1</td>
													<td class="table-content">{{#if line2}}{{line2}}{{/if}}</td>
												</tr>
												<tr>
													<td class="table-header">Address 2</td>
													<td class="table-content">{{#if line3}}{{line3}}{{/if}}</td>
												</tr>
												<tr>
													<td class="table-header">Zip Code</td>
													<td class="table-content">{{zipCode}}</td>
												</tr>
												<tr>
													<td class="table-header">City</td>
													<td class="table-content">{{city}}</td>
												</tr>
												<tr>
													<td class="table-header">State</td>
													<td class="table-content">{{state}}</td>
												</tr>
												<tr>
													<td class="table-header">Country</td>
													<td class="table-content">{{country}}</td>
												</tr>
											</tbody>
										</table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tbody>
												<tr>
													<td class="table-header">Contact Name</td>
													<td class="table-content">{{salutation}} {{firstName}} {{middleName}} {{lastName}}</td>
												</tr>
												<tr>
													<td class="table-header">Email</td>
													<td class="table-content">{{email}}</td>
												</tr>
												<tr>
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
											</tbody>
										</table>
									</div>
								</div>
								</div>
							</div>
							{{/each}}
						</div>
					</div>
				</div>



				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd11">Banking Details</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd11">
					<div class="panel-body">
						<div class="row">
							{{#each bankDetails}}
							<div class="col-md-12 col-sm-12">
								<div class="lighter-gray-bx">
									<div class="panel-heading">
										<h3 class="panel-title">{{bank}} {{#if defaultAccount}}- Default{{/if}} {{#if leadBank}}- Lead Bank{{/if}}</h3>
									</div>
								<div class="row horiz_table" >
									<div class="col-md-6">
										<table class="table">
											<tbody>
												<tr>
													<td class="table-header">Type</td>
													<td class="table-content"><b>{{accType}}</b></td>
												</tr>	
												<tr>
													<td class="table-header">Address</td>
													<td class="table-content">{{line1}}</td>
												</tr>
												<tr>
													<td class="table-header">Address 1</td>
													<td class="table-content">{{#if line2}}{{line2}}{{/if}}</td>
												</tr>
												<tr>
													<td class="table-header">Address 2</td>
													<td class="table-content">{{#if line3}}{{line3}}{{/if}}</td>
												</tr>
												<tr>
													<td class="table-header">City</td>
													<td class="table-content">{{city}}</td>
												</tr>
												<tr>
													<td class="table-header">Zip Code</td>
													<td class="table-content">{{zipCode}}</td>
												</tr>
												<tr>
													<td class="table-header">State</td>
													<td class="table-content">{{state}}</td>
												</tr>
												<tr>
													<td class="table-header">Country</td>
													<td class="table-content">{{country}}</td>
												</tr>
											</tbody>
										</table>
									</div>
									<div class="col-md-6">
										<table class="table">
											<tbody>
												<tr>
													<td class="table-header">Number</td>
													<td class="table-content">{{accNo}}</td>
												</tr>
												<tr>
													<td class="table-header">Relationship Manager</td>
													<td class="table-content">{{salutation}} {{firstName}} {{middleName}} {{lastName}}</td>
												</tr>
												<tr>
													<td class="table-header">Email</td>
													<td class="table-content">{{email}}</td>
												</tr>
												<tr>
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
											</tbody>
										</table>
									</div>
								</div>
								</div>
							</div>
							{{/each}}
						</div>
					</div>
				</div>



				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd12">Enclosures/Uploads</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd12">
					<div class="panel-body">
				<div class="row">
					{{#ifCond creatorIdentity '==' 'J'}}
					<center><h1><a href='{{documentsUrl}}' target="_blank">Click Here !</a></h1></center>
					{{/ifCond}}
					{{#ifCond creatorIdentity '!=' 'J'}}
					<div class="col-sm-12">
						<table class="table table-striped table-bordered" style="width:100%"><tbody>
						<tr>
							<th style="width:4%">Sr No</th>
							<th style="width:15%">Document Category</th>
							<th style="width:20%">Document Type</th>
							<th style="width:20%">Document</th>
							<th style="width:20%">File Name</th>
							<th style="width:20%">Remarks</th>
						</tr>
						{{#each kycDocuments.meta}}
						{{#each data}}
						<tr>
							<td>{{#if @index}}{{else}}{{../../srNo}}{{/if}}</td>
							<td>{{docCatDesc}}</td>
							<td>{{../documentTypeDesc}} {{#if mand}}*{{/if}}</td>
							<td>{{documentDesc}}{{#if documentFor}}<br>({{documentFor}}){{/if}}</td>
							<td>{{#if fileName}}{{fileName}}<button class="btn btn-link" onClick="javascript:showImage('upload/KYCDOCUMENTS/{{fileName}}')">View</button>{{/if}}</td>
							<td>{{remarks}}</td>
						</tr>
						{{/each}}
						{{/each}}
						</tbody></table>
					</div>
					{{/ifCond}}
				</div>
					</div>
				</div>
			{{#if buyerCreditRating}}
				<div class="panel-heading">
				<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd13">Buyer Credit Ratings</a>
				</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd13">
					<div class="panel-body">
				<div class="row">
					<div class="col-sm-12">
						<table class="table table-striped table-bordered" style="width:100%"><tbody>
						<thead>
						<tr>
							<th>Rating Agency </th>
							<th>Rating </th>
							<th>Rating Date</th>
							<th>Expiry Date</th>
							<th>Rating Type</th>
							<th>Status</th>
							<th>View Uploaded Docs</th>
						</tr></thead>
						{{#each buyerCreditRating}}
						<tr>
							<td>{{ratingAgency}}</td>
							<td>{{rating}}</td>
							<td>{{ratingDate}}</td>
							<td>{{expiryDate}}</td>
							<td>{{ratingType}}</td>
							<td>{{status}}</td>
							<td><a href="upload/BUYERCREDITRATINGS/{{ratingFile}}" style="color:blue !important">{{ratingFile}}</a></td>
						</tr>
						{{/each}}
						</tbody></table>
					</div>
				</div>
					</div>
				</div>
			{{/if}}
			

			{{#if creditReport}}
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd14">Credit Report</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd14">
					<div class="panel-body">
						<div class="row">
							<div class="col-sm-2">
								<section><label for="" class="label">Credit Report:</label></section>
							</div>
							<div class="col-sm-4">
								<section class="view">
									<a href="upload/CREDITREPORT/{{creditReport}}" style="color:blue !important">{{creditReport}}</a>
								</section>
							</div>
						</div>
					</div>
				</div>
			{{/if}}


			{{#if regAnnFees}}
				<div class="panel-heading">
					<h3 class="panel-title">
						<a class="collapsed" data-parent="#accordion" data-toggle="collapse" href="#accrd15">Registration and Annual Fees</a>
					</h3>
				</div>
				<div class="panel-collapse collapse" id="accrd15">
					<div class="panel-body">
						<div class="row">
							<div class="col-sm-12">
								<table class="table table-striped table-bordered" style="width:100%"><tbody>
								<thead>
								<tr>
									<th>Charge Type </th>
									<th>Date </th>
									<th>Charge Amt</th>
									<th>Request Type</th>
									<th>Extended Date</th>
									<th>Extension Count</th>
									<th>Payment Date</th>
									<th>Payment Amount</th>
									<th>Payment Ref</th>
									<th>Billed Entity</th>
								</tr></thead>
								{{#each regAnnFees}}
								<tr>
									<td>{{chargeType}}</td>
									<td>{{effectiveDate}}</td>
									<td>{{chargeAmount}}</td>
									<td>{{requestType}}</td>
									<td>{{extendedDate}}</td>
									<td>{{extensionCount}}</td>
									<td>{{paymentDate}}</td>
									<td>{{paymentAmount}}</td>
									<td>{{paymentRefrence}}</td>
									<td>{{billedEntityCode}}</td>
								</tr>
								{{/each}}
								</tbody></table>
							</div>
						</div>
					</div>
				</div>
			{{/if}}

	</script>
	<script type="text/javascript">
	var tplCompanyView;
	var metaOG, myContacts, dataNew, metaMulti, metaNew, data;
	$(document).ready(function() {
		tplCompanyView = Handlebars.compile($('#tplCompanyView').html());
		$.ajax({
			url: '<%=lUrl%>',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
				
		    	metaOG = pObj.kycDocuments.meta;
		    	myContacts=pObj.kycDocuments.catContacts;
		    	data = pObj.kycDocuments.data;
		    	dataNew = {};

		    	var lContacts;
		    	metaMulti=[];
		    	metaNew=[];
		    	var lRepeatTypes=[];
		    	//duplicate as per contacts
		    	for(var i=metaOG.length-1; i>=0; i--){
					lMeta = metaOG[i];
					if(lMeta.repeatDocType){
						metaMulti.push(lMeta);
						metaOG.splice(i,1);
					}else{
		    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value;
		    			lMeta.idx = lKey;
		    			//find data convert and attach
			    		var lTmpData = data[lKey]; //this is hash 
		    			var lData = {documentType:lMeta.documentType};
			    		if(lTmpData){
			    			lData = lTmpData[0];
				    	}else{
		    				lData.document = lMeta.document;
		    				lData.documentCat = lMeta.docCat;
		    				lData.documentType = lMeta.documentType;
		    				lData.documentDesc = lMeta.documentList[0].text;
			    		}
	    				lData.docCatDesc = lMeta.docCatDesc;
	    				lData.documentFor = lMeta.documentFor;
	    				lData.documentList = lMeta.documentList;
			    		if(!lMeta.data) lMeta.data =[];
			    		lMeta.data.push(lData);
	    				dataNew[lKey]= lData;
						metaNew.push(lMeta);
					}
		    	}
		    	$.each(myContacts, function(pKey, pContacts) {
					 lContacts = myContacts[pKey];
					 if(lContacts!=null){
			    		for (var lPtr=0; lPtr<lContacts.length;lPtr++) {
					    	for(var i=metaMulti.length-1; i>=0; i--){
					    		if(pKey==metaMulti[i].repeatDocType){
					    			lMeta = JSON.parse(JSON.stringify(metaMulti[i]));
					    			lMeta.documentFor = lContacts[lPtr].ccDisp; 
					    			lMeta.docForCCId = lContacts[lPtr].ccId;
					    			lKey = lMeta.docCat + "^" + lMeta.documentType+ "^" + lMeta.documentList[0].value + "^" + lContacts[lPtr].ccId;
					    			lMeta.idx = lKey;
					    			//find data convert and attach
						    		var lTmpData = data[lKey]; //this is hash 
					    			var lData = {documentType:lMeta.documentType};
						    		if(lTmpData){
						    			lData = lTmpData[0];
						    		}else{
					    				lData.document = lMeta.document;
					    				lData.documentCat = lMeta.docCat;
					    				lData.documentType = lMeta.documentType;
					    				lData.documentDesc = lMeta.documentList[0].text;
						    		}
				    				lData.docCatDesc = lMeta.docCatDesc;
				    				lData.documentFor = lMeta.documentFor;
				    				lData.docForCCId = lMeta.docForCCId; 
				    				lData.documentList = lMeta.documentList;
						    		if(!lMeta.data) lMeta.data =[];
						    		lMeta.data.push(lData);
				    				dataNew[lKey]= lData;
									metaNew.push(lMeta);
								}
					    	}
				    	}
		    		}
		    	});
				
		    	pObj.kycDocuments.meta = metaNew;
		    	pObj.kycDocuments.data = dataNew;
		    	
		    	metaNew.sort(SortByName);
		    	
		    	var lSrNo =0;
		    	$.each(metaNew, function(pKey, pMeta) {
		    		lSrNo += 1;
		    		pMeta.srNo = lSrNo;		    		
		    	});

		    	
				$('#frmMain .box-body').html(tplCompanyView(pObj));
			},
			error: errorHandler,
		});

		$('#btnCloseWin').on('click', function() {
			window.close();
		});
		$('#btnClose').on('click', function() {
			location.href = "<%=request.getContextPath()%>/rest/appentity";
		});
		
        $('.panel-group').on('focus.collapse.data-api', '[data-toggle=collapse]', function(e) {
           	var $this = $(this),
                href, target = $this.attr('data-target') || e.preventDefault() || (href = $this.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') //strip for ie7
                ,
                option = null
                $(target).collapse('toggle') 
        });

	});
	function showImage(pImage) {
		if(pImage.toLowerCase().endsWith(".pdf")){
			window.open(pImage);
		}else{
			dispImage(true);
			document.getElementById("imageBox").innerHTML = '<center><img src="'+pImage+'" style="max-width:100%" /></center>';
		}
	}
	function closeImage(){
		dispImage(false);
	}
	function dispImage(pShowImage){
		document.getElementById("imageDiv").style.display = pShowImage?"":"none";
		document.getElementById("contAppUser").style.display = pShowImage?"none":"";
		document.getElementById("mdl-image").style.display = pShowImage?"none":"";
	}
	function SortByName(a, b){
		  var aName = a.docCatDesc.toLowerCase();
		  var bName = b.docCatDesc.toLowerCase(); 
		  var aType = (a.documentFor?a.documentFor.toLowerCase():"");
		  var bType = (b.documentFor?b.documentFor.toLowerCase():""); 
		  var lRetVal = ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
		  if(lRetVal>=0 && aType!="" && bType!=""){
			  lRetVal = (aType > bType) ? 1 : lRetVal;
		  }
		  return lRetVal;
	}
	</script>
   	
    </body>
</html>