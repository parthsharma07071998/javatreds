<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lType = (String)request.getAttribute("type");
%>
<html>
    <head>
        <title>TREDS | Entities</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Entities" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAppEntity">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Entities</h1>
			</div>
		</div>
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="code" class="label">Code:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="code" placeholder="Code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="name" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="name" class="label">PAN:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="pan" placeholder="PAN">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="type" class="label">Entity Type:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="type"><option value="">Select Entity Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="status"><option value="">Select Status</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="twoFaType" class="label">2FAType:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="twoFaType"><option value="">Select Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="rating" class="label">Rating:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="rating"><option value="">Select Rating</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>

					<div class="row">
						<div class="col-sm-2"><section><label for="rmUserId" class="label">RM:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="rmUserId"><option value="">Select RM</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="rmLocation" class="label">RM Location:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="rmLocation"><option value="">Select RM Location</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="rsmUserId" class="label">RSM:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="rsmUserId"><option value="">Select RSM</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="rsmLocation" class="label">RSM Location:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="rsmLocation"><option value="">Select RSM Location</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
									<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:crudAppEntity.searchHandler();" class="right_links"><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
					<a href="javascript:;" class="right_links secure" data-seckey="company-view" id=btnViewComp><span class="glyphicon glyphicon-eye-open"></span> View</a>
					<a href="javascript:;" class="right_links" id=btnDownloadAgreement style=''><span class="fa fa-download"></span> Download Agreement</a>
					<a href="javascript:;" class="right_links" id=btnEmailNot style=''><span class="fa fa-download"></span> Email Settings</a>
					<div class = "btn-group right_links state-T">
				      <a href="javascript:;" type = "button" data-toggle = "dropdown"><span class="fa fa-cog"></span> Settings<span class = "caret"></span></a>
						<ul class = "dropdown-menu" role = "menu">
							<li><a href="javascript:;" class="left_links" id=btnEmailSettings style=''><span class="fa fa-envelope"></span> Email Settings</a></li>
							<li><a href="javascript:;" class="left_links" id=btnSplitSettings style=''><span class="glyphicon glyphicon-edit"></span> Split Settings</a></li>
							<li><a href="javascript:;" class="left_links" id=btn2faSettings><span class="glyphicon glyphicon-edit"></span> 2FA Settings</a></li>
							<li><a href="javascript:;" class="left_links" id=btnReqVer><span class="glyphicon glyphicon-edit"></span> Click wrap agreement</a></li>
							<li><a href="javascript:;" class="left_links" id=btnChkLimit><span class="glyphicon glyphicon-edit"></span> Checker Limit</a></li>
							<li><a href="javascript:;" class="left_links" id=btnEntityPref><span class="glyphicon glyphicon-edit"></span> Entity Preference</a></li>
							<li><a href="javascript:;" class="left_links" id=btnBillingLocation><span class="glyphicon glyphicon-edit"></span> Billing Location</a></li>
							<li><a href="javascript:;" class="left_links" id=btnCreditReport><span class="glyphicon glyphicon-edit"></span> Credit Report</a></li>
							<li><a href="javascript:;" class="left_links" id=btnRm><span class="glyphicon glyphicon-edit"></span> RM Settings</a></li>
						</ul>
					</div>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-width="70px" data-name="code">Code</th>
									<th data-width="250px" data-name="name">Name</th>
									<th data-width="70px" data-name="type">Entity Type</th>
									<th data-width="80px" data-name="pan">PAN</th>
									<th data-width="70px" data-name="status">Status</th>
									<th data-width="60px" data-name="financierCount">Financier Count</th>
									<th data-width="60px" data-name="rmUserLogin">RM Login</th>
									<th data-width="60px" data-name="rmUserName">RM Name</th>
									<th data-width="60px" data-name="rmLocationDesc">RM Location</th>
									<th data-width="60px" data-name="rsmUserLogin">RSM Login</th>
									<th data-width="60px" data-name="rsmUserName">RSM Name</th>
									<th data-width="60px" data-name="rsmLocationDesc">RSM Location</th>
									<th class="hideCol" data-width="110px" data-name="twoFaTypeDesc" id="tblTwoFaTypeDesc" >2FA Type</th>
									<th class="hideCol" data-width="110px" data-name="requiredAgreementVersion" id="tblRequiredAgreementVersion" >Click wrap agreement</th>
									<th class="hideCol" data-width="70px" data-name="allowObliSplitting">Allow Obligation Splitting</th>
									<th class="hideCol" data-width="70px" data-name="instLevel">Instrument Level</th>
									<th class="hideCol" data-width="70px" data-name="bidLevel">Bid Level</th>
									<th class="hideCol" data-width="70px" data-name="limitLevel">Limit Level</th>
									
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
		</div>
		<!-- frmMain -->
			<div class="modal fade" id="mdl2FA" tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;2FA Settings</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frm2FA">
					<fieldset>
						<div class="row">
							<div class="col-sm-4"><section ><label for="code" class="label">Code:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="code" placeholder="code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						</div>
						<div class="row">
							<div class="col-sm-4"><section><label for="twoFaType" class="label">2FA Setting:</label></section></div>
							<div class="col-sm-6">
								<section class="select">
								<select id="twoFaType"><option value="">Select Types</option>
								</select>
								<b class="tooltip tooltip-top-right"></b><i></i></section>
								<section class="view"></section>
							</div>
						</div>
						<input type="hidden" id="recordVersion" name="recordVersion" value="">
					</fieldset>
		    		<div class="box-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" href="javascript:crudRoleMaster.searchHandler();" class="btn btn-info btn-lg btn-enter" id=btnSave2FA><span class="fa fa-save"></span> Save</button>
									<button type="button" href="javascript:crudRoleMaster.searchHandler();" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
									<a></a>
								</div>
							</div>
						</div>
		    		</div>
				</div>
		</div>
		</div></div></div>
	
		

		<div class="modal fade" id=mdlReqVer tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Required Agreement Version</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmReqVer">
				<fieldset>
					<div class="row">
							<div class="col-sm-4"><section ><label for="code" class="label">Code:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="code" placeholder="code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4"><section><label for="requiredAgreementVersion" class="label">Required Agreement Version:</label></section></div>
						<div class="col-sm-6">
							<section class="select">
							<select id="requiredAgreementVersion"><option value="">Select Types</option>
							</select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>					
					<input type="hidden" id="recordVersion" name="recordVersion" value="">
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveReqVer><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>


		<div class="modal fade" id=mdlSplitSetting tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Allow Obligation Splitting</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmSplitSetting">
				<fieldset>
					<div class="row">
							<div class="col-sm-4"><section ><label for="code" class="label">Code:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="code" placeholder="code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4"><section><label for="allowObliSplitting" class="label">Allow Obligation Splitting:</label></section></div>
						<div class="col-sm-6">
							<section class="select">
							<select id="allowObliSplitting"><option value=""></option>
							</select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>					
					<input type="hidden" id="recordVersion" name="recordVersion" value="">
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveSplitSetting><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
		
		<div class="modal fade" id=mdlCheckerLimit tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Checker Limit</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmCheckerLimit">
				<fieldset>
					<div class="row">
							<div class="col-sm-4"><section ><label for="code" class="label">Code:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="code" placeholder="code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
							<div class="col-sm-4"><section ><label for="instLevel" class="label">Instrument Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="instLevel" placeholder="instrument">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
							<div class="col-sm-4"><section ><label for="instCntrLevel" class="label">Instrument Counter Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="instCntrLevel" placeholder="instrument Counter">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row">
							<div class="col-sm-4"><section ><label for="bidLevel" class="label">Bid Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="bidLevel" placeholder="bid">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
							<div class="col-sm-4"><section ><label for="platformLimitLevel" class="label">Platform Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="platformLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row">
							<div class="col-sm-4"><section ><label for="buyerLimitLevel" class="label">Buyer Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="buyerLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row">
							<div class="col-sm-4"><section ><label for="buyerSellerLimitLevel" class="label">Buyer Seller Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="buyerSellerLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row">
							<div class="col-sm-4"><section ><label for="userLimitLevel" class="label">User Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="userLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>		
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveLimit><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
		<div class="modal fade" id=mdlCreditReport tabindex=-1><div class="modal-dialog modal-md"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Credit Report</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmCreditReport">
				<fieldset>
					<div class="row">
							<div class="col-sm-4"><section ><label for="code" class="label">Code:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="code" placeholder="code" readonly>
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
					<input type="hidden" id="creditReport" data-role="xuploadfield" data-file-type="CREDITREPORT" />
					<div class="col-sm-4"><section><label for="creditReport" class="label">Credit Report:</label></section></div>
					<div class="col-sm-6">
						<section id='creditReport'>
							<button type="button" class="upl-btn-upload btn btn-sm btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="upl-btn-clear btn btn-sm btn-default"><span class="fa fa-remove"></span> Clear</button>
							<span class="upl-info"></span>
							<span class="upl-uploading"><span class="fa fa-cog fa-spin"></span></span>
						</section>
					</div>
					</div>
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnCreditReport onclick="saveCreditReport();"><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
	<div class="modal fade" id=mdlRm tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Regional Manager</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmRm">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="code" class="label">Company Code:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="code" placeholder="Company Code" readonly>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="name" class="label">Company Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="name" placeholder="Company Name" readonly>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="rmUserId" class="label">RM:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rmUserId"><option value="">Select RM</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="rmLocation" class="label">RM Location:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rmLocation"><option value="">Select RM Location</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="rsmUserId" class="label">RSM:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rsmUserId"><option value="">Select RSM</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="rsmLocation" class="label">RSM Location:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rsmLocation"><option value="">Select RSM Location</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
					<center><h3>Revenue Sharing Settings</h3></center>
					<div class="row">
						<div class="col-sm-2"><section><label for="businessSource" class="label">Business Source:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="businessSource"><option value="">Select Business Source</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="refererCode" class="label">Referer Code:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="refererCode"><option value="">Select Refere Code</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
					<center><h3>Referrer Details</h3></center>
					<div class="row">
					<div class="col-sm-2"><section><label for="aggCompanyGSTN" class="label">Company Gstn:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggCompanyGSTN" placeholder="Company Gstn">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="aggContactPerson" class="label">Contact Person:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactPerson" placeholder="Contact Person">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="aggContactMobile" class="label">Contact Mobile No.:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactMobile" placeholder="Contact Mobile No">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="aggContactEmail" class="label">Contact Email:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="aggContactEmail" placeholder="Contact Email">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
					<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
					<center><h3>Revenue Sharing</h3></center>
					<div class="row">
						<div class="col-sm-2"><section><label for="transFeeType" class="label">Transaction:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="transFeeType"><option value="">Select Trans Fee Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="transFeePerc" class="label">Percentage:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="transFeePerc" placeholder="Transaction fee Percentage">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="transFeeAmt" class="label">Amount:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="transFeeAmt" placeholder="Transaction Fee Amt.">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="regFeeType" class="label">Registration:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="regFeeType"><option value="">Select Registration Fee Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="regFeePerc" class="label">Percentage:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="regFeePerc" placeholder="Registration Fee Percentage">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="regFeeAmt" class="label">Amount:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="regFeeAmt" placeholder="Registration Fee Amt.">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="annFeeType" class="label">Annual:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="annFeeType"><option value="">Select Annual Fee Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="annFeePerc" class="label">Percentage:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="annFeePerc" placeholder="Annual Fee">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="annFeeAmt" class="label">Amount:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="annFeeAmt" placeholder="Annual Fee Amt.">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<hr style="height:1px; border:none; color:#000; background-color:#000;"/>
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveRm><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
			<div class="modal fade" id=mdlCustomfields tabindex=-1><div class="modal-dialog modal-lg"><div class="modal-content">
			<div class="modal-header"><span>&nbsp;Custom Fields</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
			<div class="modal-body">
				<div class="xform box" id="frmCustomfields">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="field1lable" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="field1lable" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field1Type" class="label">Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field1Type"><option value="">Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field1Mandatory" class="label">Mandatory:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field1Mandatory"><option value="">Mandatory</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="field2lable" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="field2lable" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field2Type" class="label">Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field2Type"><option value="">Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field2Mandatory" class="label">Mandatory:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field2Mandatory"><option value="">Mandatory</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="field3lable" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="field3lable" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field3Type" class="label">Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field3Type"><option value="">Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field3Mandatory" class="label">Mandatory:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field3Mandatory"><option value="">Mandatory</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="field4lable" class="label">Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="field4lable" placeholder="Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field4Type" class="label">Type:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field4Type"><option value="">Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="field4Mandatory" class="label">Mandatory:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="field4Mandatory"><option value="">Mandatory</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
				</fieldset>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSaveCustomfields><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								<a></a>
							</div>
						</div>
					</div>
	    		</div>
				</div>
			</div>
		</div></div></div>
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudAppEntity$ = null, crudAppEntity,searchForm;
	var versionFetched=false, twoFATypesFetched=false;
	var lPurchaserData = null;
	var lSupplierData = null;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "appentity",
				autoRefresh: true,
				preSearchHandler: function(pFilter){
					 var lIsPlatform = (loginData.domain == "<%=AppConstants.DOMAIN_PLATFORM%>");
					 if (!lIsPlatform) {
						 $('#tblData').DataTable().columns( '.hideCol' ).visible( false );
					 }
					 return true;
				},
			};
		lConfig = $.extend(lConfig, lFormConfig);
		var lLevelConfig ={
				"fields":[
					{
						"name": "instLevel",
						"label": "Instrument Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "bidLevel",
						"label": "Bid Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "platformLimitLevel",
						"label": "Platform Limit Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "buyerLimitLevel",
						"label": "Buyer Limit Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "buyerSellerLimitLevel",
						"label": "Buyer Seller Limit Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "userLimitLevel",
						"label": "User Limit Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "instCntrLevel",
						"label": "Instrument Counter Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"notNull":true,
						"patternMessage":"Please enter the value between 0 and 10 ",
						"maxValue":10,
						"minValue":0
					},
					{
						"name": "code",
						"label": "Code",
						"dataType": "STRING"
					}
				]
		}
		
		var lRmConfig ={
				"fields":[
					{
						"name":"code",
						"label":"Company Code",
						"dataType":"STRING",
						"fieldType":"PRIMARY",
						"maxLength": 10
					},
					{
						"name": "name",
						"label":"Company Name",
						"dataType": "STRING",
						"maxLength": 50
					},
					{
						"name":"rmUserId",
						"label":"RM",
						"dataType":"STRING",
						"maxLength": 20,
						"dataSetType":"RESOURCE",
						"dataSetValues": "user/all?adm=Y"
					},
					{
						"name": "rmLocation",
						"label": "RM Location",
						"dataType":"STRING",
						"maxLength": 20,
						"dataSetType":"RESOURCE",
						"dataSetValues": "appentity/rmloc"
					},
					{
						"name":"rsmUserId",
						"label":"RSM",
						"dataType":"STRING",
						"maxLength": 20,
						"dataSetType":"RESOURCE",
						"dataSetValues": "user/all?adm=Y"
					},
					{
						"name": "rsmLocation",
						"label": "RSM Location",
						"dataType":"STRING",
						"maxLength": 20,
						"dataSetType":"RESOURCE",
						"dataSetValues": "appentity/rmloc"
					},
					{
						"name":"businessSource",
						"label":"Business Source",
						"nonDatabase":true,
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues":[{"text":"Direct", "value":"D"},{"text":"Referred", "value":"R"}]
					},
					{
						"name": "refererCode",
						"label": "Referer Code",
						"nonDatabase":true,
						"dataType": "STRING",
						"maxLength": 10,
						"dataSetType":"RESOURCE",
			  			"dataSetValues":"appentity/aggregator"
					},
					{
						"name": "transFeeType",
						"label": "Trans Fee Type",
						"nonDatabase":true,
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues":[{"text":"Absolute", "value":"A"},{"text":"Percentage", "value":"P"}]
					},
					{
						"name": "regFeeType",
						"label": "Reg Fee Type",
						"nonDatabase":true,
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues":[{"text":"Absolute", "value":"A"},{"text":"Percentage", "value":"P"}]
					},
					{
						"name": "annFeeType",
						"label": "Ann Fee Type",
						"nonDatabase":true,
						"dataType":"STRING",
						"maxLength": 1,
						"dataSetType": "STATIC",
						"dataSetValues":[{"text":"Absolute", "value":"A"},{"text":"Percentage", "value":"P"}]
					},
					{
						"name": "transFeePerc",
						"label": "Trans Fee",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"maxValue":100,
						"minValue":0
					},
					{
						"name": "regFeePerc",
						"label": "Reg Fee",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"maxValue":100,
						"minValue":0
					},
					{
						"name": "annFeePerc",
						"label": "Ann Fee",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"maxValue":100,
						"minValue":0
					},
					{
						"name": "transFeeAmt",
						"label": "Trans Fee Absolute",
						"nonDatabase":true,
						"dataType":"DECIMAL",
						"integerLength":10,
						"decimalLength":2
					},
					{
						"name": "regFeeAmt",
						"label": "Reg Fee Absolute",
						"nonDatabase":true,
						"dataType":"DECIMAL",
						"integerLength":10,
						"decimalLength":2
					},
					{
						"name": "annFeeAmt",
						"label": "Ann Fee Absolute",
						"nonDatabase":true,
						"dataType":"DECIMAL",
						"integerLength":10,
						"decimalLength":2
					},
					{
						"name": "aggCompanyGSTN",
						"label": "Reg. GST No.",
						"dataType":"STRING",
						"maxLength": 30,
						"conversion" : ["toUpper"],
						"pattern": "PATTERN_GST",
						"desc":"GST No.",
						"nonDatabase": true
					},
					{
						"name": "aggContactPerson",
						"label": "Contact Person",
						"dataType": "STRING",
						"maxLength": 100,
						"nonDatabase": true
					},
					{
						"name": "aggContactMobile",
						"label": "Contact Mobile Number",
						"dataType":"STRING",
						"maxLength": 30,
						"pattern": "PATTERN_MOBILE",
						"nonDatabase": true
					},
					{
						"name": "aggContactEmail",
						"label": "Contact Email",
						"dataType":"STRING",
						"maxLength": 50,
						"pattern": "PATTERN_EMAIL",
						"nonDatabase": true
					}
				]
		}
		
		 var lPlatform = (loginData.domain == "<%=AppConstants.DOMAIN_PLATFORM%>");
		 if(!lPlatform){
			 var lFin = (loginData.entityType.substring(2,3) == 'Y');
			 if(lFin){
				 $.each(lConfig.fields,function(pIdx,pVal){
					 if (pVal.name=='type') {
						 if(pVal.dataSetValues!=null){
							 for(var lPtr=pVal.dataSetValues.length-1; lPtr>=0; lPtr--){
								 if (pVal.dataSetValues[lPtr].value=='NNY' || pVal.dataSetValues[lPtr].value=='NNN')
									 	pVal.dataSetValues.splice(lPtr,1);
							 }
						 }
					 }
				 });
			 }
		 }
		 if(!lPlatform){	
			$('#frmSearch #tblRequiredAgreementVersion').attr('data-visible',false);
			$('#frmSearch #tblTwoFaTypeDesc').attr('data-visible',false);
		}
		crudAppEntity$ = $('#contAppEntity').xcrudwrapper(lConfig);
		crudAppEntity = crudAppEntity$.data('xcrudwrapper');
		mainForm = crudAppEntity.options.mainForm;
		searchForm=crudAppEntity.options.searchForm;
		crudfrmCheckerLimit$ = $('#frmCheckerLimit').xform(lLevelConfig);
     	crudfrmCheckerLimit = crudfrmCheckerLimit$.data('xform');
     	crudfrmRm$ = $('#frmRm').xform(lRmConfig);
     	crudfrmRm = crudfrmRm$.data('xform');
<%
if (lType != null) {
%>
		crudAppEntity.options.searchForm.setValue({type:'<%=lType%>'});
		crudAppEntity.searchHandler();
<%	
}
%>		
		$.ajax({
		    url: 'appentity/clickwrapVersionsPurchaser',
		    type: 'GET',
		    success: function( pObj, pStatus, pXhr) { 
		    	lPurchaserData = pObj;
		    	versionFetched=true;
		    },
		    error: function( pObj, pStatus, pXhr) {
		    	oldAlert('Error');
		    }
		});
		
		$.ajax({
		    url: 'appentity/clickwrapVersionsSupplier',
		    type: 'GET',
		    success: function( pObj, pStatus, pXhr) { 
		    	lSupplierData = pObj;
		    	versionFetched=true;
		    },
		    error: function( pObj, pStatus, pXhr) {
		    	oldAlert('Error');
		    }
		});
	
		$('#btnViewComp').on('click', function() {
			var lSelected = crudAppEntity.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			location.href = 'companyview?final=Y&entityCode='+crudAppEntity.selectedRowKey(lSelected.data());
		});
		
		$('#btn2faSettings').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            if(!twoFATypesFetched){
                $.ajax({
    		        url: 'appentity/2FAType',
    		        type: 'GET',
    		        success: function( pObj, pStatus, pXhr) { 
    		        	for(index in pObj){
    				       var option = document.createElement('option');
    				       option.text = pObj[index].text;
    				       option.value = pObj[index].value;
    				       $('#mdl2FA #twoFaType').append(option);
    					}
    		        	twoFATypesFetched=true;
    		        },
    		        error: function( pObj, pStatus, pXhr) {
    		        	oldAlert('Error');
    		        }
    		    });
            }
			$.ajax({
		        url: 'appentity/2FA/'+crudAppEntity.selectedRowKey(lSelected.data()),
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdl2FA #twoFaType').val(pObj.twoFaType);
		        	$('#mdl2FA #code').val(pObj.code);
		        	$('#mdl2FA #recordVersion').val(pObj.recordVersion);
		        	$('#mdl2FA #code').prop('disabled',true);
		        	$('#mdl2FA').modal('show');		        	
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	oldAlert('Error');
		        }
		    });
			
    	});
		$('#btnSave2FA').on('click', function() {
			var lCode = $('#mdl2FA #code').val();
			var lRecordVersion = $('#mdl2FA #recordVersion').val();
			var ltwoFaType = $('#mdl2FA #twoFaType').val();
			var lData = {"code":lCode ,"twoFaType":ltwoFaType ,"recordVersion":lRecordVersion };
			$.ajax({
		        url: 'appentity/2FA',
		        data: JSON.stringify(lData),
		        type: 'PUT',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdl2FA').modal('hide');
		        	$('#btnSearch').click();
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	oldAlert('Error' + pObj);
		        }
		    });
		});
		
		$('#btnReqVer').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            
            if(versionFetched){
            	var lObj = null;
            	if(lSelected.data().type == "Buyer"){
            		lObj = lPurchaserData;
            	}
            	if(lSelected.data().type == "Seller"){
            		lObj = lSupplierData;
            	}
            	$('#mdlReqVer #requiredAgreementVersion').empty();
            	for(index in lObj){
    		       var option = document.createElement('option');
    		       option.text = lObj[index].text;
    		       option.value = lObj[index].value;
    		       $('#mdlReqVer #requiredAgreementVersion').append(option);
    			}
            }

			$.ajax({
		        url: 'appentity/reqVer/'+crudAppEntity.selectedRowKey(lSelected.data()),
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlReqVer #requiredAgreementVersion').val(pObj.requiredAgreementVersion);
		        	$('#mdlReqVer #code').val(pObj.code);
		        	$('#mdlReqVer #recordVersion').val(pObj.recordVersion);
		        	$('#mdlReqVer #code').prop('disabled',true);
		        	$('#mdlReqVer').modal('show');		        	
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	alert(pObj.responseJSON.messages[0]);
		        	//oldAlert('Error : ' + pObj.responseJSON.messages[0]);
		        }
		    });
    	});
		$('#btnBillingLocation').on('click', function() {
			var lSelected = crudAppEntity.getSelectedRow();
			if ((lSelected==null)||(lSelected.length==0)) {
				alert("Please select a row");
				return;
			}
			var lData = {"code":lSelected.data().code}
			$.ajax({
		        url: 'billinglocation/checkaccess',
		        data: JSON.stringify(lData),
		        type: 'POST',
		        success: function( pObj, pStatus, pXhr) {
		        	location.href='billinglocation?code='+lSelected.data().code;
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	oldAlert('Error');
		        }
		    });
		});
		$('#btnSaveReqVer').on('click', function() {
			var lCode = $('#mdlReqVer #code').val();
			var lRecordVersion = $('#mdlReqVer #recordVersion').val();
			var lRequiredAgreementVersion = $('#mdlReqVer #requiredAgreementVersion').val();
			var lData = {"code":lCode ,"requiredAgreementVersion":lRequiredAgreementVersion ,"recordVersion":lRecordVersion };
			$.ajax({
		        url: 'appentity/reqVer',
		        data: JSON.stringify(lData),
		        type: 'PUT',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlReqVer').modal('hide');
		        	$('#btnSearch').click();
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	oldAlert('Error');
		        }
		    });
		});
		
		$('#btnDownloadAgreement').on('click', function() {
			var lSelected = crudAppEntity.getSelectedRow().data();
			if (lSelected == null) {
				alert("Please select a row");
				return;
			}
			if(lSelected.type == "Buyer" || lSelected.type == "Seller"){
				downloadFile('puraggacc/downloadclickwrap',null,JSON.stringify({"columnNames" : [],"domain" : lSelected.code,"getImage":true,"skipLog":false }) );
			}else{
				alert("Please select a buyer/seller.");
			}
		});
		$('#btnEmailSettings').on('click', function() {
			var lSelected = crudAppEntity.getSelectedRow().data();
			if (lSelected == null) {
				alert("Please select a row");
				return;
			}
			location.href="entnot?code="+lSelected.code+"&name="+lSelected.name;
		});
		
		$('#btnSplitSettings').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            $.ajax({
   		        url: 'appentity/splitsetting/'+crudAppEntity.selectedRowKey(lSelected.data()),
   		        type: 'GET',
   		        success: function( pObj, pStatus, pXhr) {
   		        	//
			        var option = null;
			        if( $('#mdlSplitSetting #allowObliSplitting option').length == 1){
	   		        	option = document.createElement('option');
				        option.text = '<%=CommonAppConstants.YesNo.Yes.toString()%>';
				        option.value = '<%=CommonAppConstants.YesNo.Yes.getCode()%>';
				        $('#mdlSplitSetting #allowObliSplitting').append(option);
	   		        	option = document.createElement('option');
				        option.text = '<%=CommonAppConstants.YesNo.No.toString()%>';
				        option.value = '<%=CommonAppConstants.YesNo.No.getCode()%>';
				        $('#mdlSplitSetting #allowObliSplitting').append(option);
			        }
   		        	//
   		        	$('#mdlSplitSetting #allowObliSplitting').val(pObj.allowObliSplitting);
   		        	$('#mdlSplitSetting #code').val(pObj.code);
	   		     	$('#mdlSplitSetting #recordVersion').val(pObj.recordVersion);
		        	$('#mdlSplitSetting #code').prop('disabled',true);
		        	$('#mdlSplitSetting').modal('show');	
   		        },
   		        error: function( pObj, pStatus, pXhr) {
   		        	alert('error');
   		        }
    		});
        });
		
		$('#btnSaveSplitSetting').on('click', function() {
			var lCode = $('#mdlSplitSetting #code').val();
			var lRecordVersion = $('#mdlSplitSetting #recordVersion').val();
			var lallowObliSplitting = $('#mdlSplitSetting #allowObliSplitting').val();
			var lData = {"code":lCode ,"allowObliSplitting":lallowObliSplitting ,"recordVersion":lRecordVersion };
			$.ajax({
		        url: 'appentity/splitsetting',
		        data: JSON.stringify(lData),
		        type: 'PUT',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlSplitSetting').modal('hide');
		        	$('#btnSearch').click();
		        	alert("Saved Successfully.");
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	alert(pObj.responseJSON.messages[0]);
		        }
		    });
		});
		
		$('#btnChkLimit').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            $.ajax({
			    url: 'appentity/chksetting/'+lSelected.data().code,
			    type: 'GET',
			    async:false,
			    success: function( pObj, pStatus, pXhr) { 
			    	crudfrmCheckerLimit.setValue(pObj);
		            crudfrmCheckerLimit.enableDisableField('code',false,false);
		            $('#mdlCheckerLimit').modal('show');
			    },
			    error: function( pObj, pStatus, pXhr) {
			    	oldAlert('Error');
			    }
			});
            $('#mdlCheckerLimit').modal('show');
    	});
		
		$('#btnRm').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            $.ajax({
			    url: 'appentity/rm/'+lSelected.data().code,
			    type: 'GET',
			    async:false,
			    success: function( pObj, pStatus, pXhr) {
			    	if (pObj==null){
			    		crudfrmRm.getField("code").setValue(lSelected.data().code);
			    		crudfrmRm.getField("name").setValue(lSelected.data().name);
			    	}else{
			    		crudfrmRm.setValue(pObj);
			    		if (pObj.rmSettings!=null){
	 			    		if (pObj.rmSettings.annFeeType!=null){
	 			    			crudfrmRm.getField("annFeeType").setValue(pObj.rmSettings.annFeeType);
	 			    			enableDisableFeeFields('A');
	 			    		}
	 			    		if (pObj.rmSettings.transFeeType!=null){
	 				    		crudfrmRm.getField("transFeeType").setValue(pObj.rmSettings.transFeeType);
	 				    		enableDisableFeeFields('T');
	 			    		}
	 			    		if (pObj.rmSettings.regFeeType!=null){
	 				    		crudfrmRm.getField("regFeeType").setValue(pObj.rmSettings.regFeeType);
	 				    		enableDisableFeeFields('R');
	 			    		}
	 			    		if (pObj.rmSettings.annFeeType!=null){
	 			    			crudfrmRm.getField("annFeeType").setValue(pObj.rmSettings.annFeeType);
	 			    		}
	 			    		if (pObj.rmSettings.transFeePerc!=null){
	 				    		crudfrmRm.getField("transFeePerc").setValue(pObj.rmSettings.transFeePerc);
	 			    		}
	 			    		if (pObj.rmSettings.regFeePerc!=null){
	 				    		crudfrmRm.getField("regFeePerc").setValue(pObj.rmSettings.regFeePerc);
	 			    		}
	 			    		if (pObj.rmSettings.annFeeAmt!=null){
	 			    			crudfrmRm.getField("annFeeAmt").setValue(pObj.rmSettings.annFeeAmt);
	 			    		}
	 			    		if (pObj.rmSettings.transFeeAmt!=null){
	 				    		crudfrmRm.getField("transFeeAmt").setValue(pObj.rmSettings.transFeeAmt);
	 			    		}
	 			    		if (pObj.rmSettings.regFeeAmt!=null){
	 				    		crudfrmRm.getField("regFeeAmt").setValue(pObj.rmSettings.regFeeAmt);
	 			    		}
	 			    		changeBusinessSource();
			    		}
			    		crudfrmRm.enableDisableField(['companyCode','companyName','rmLocation','rsmLocation','aggCompanyGSTN','aggContactPerson','aggContactMobile','aggContactEmail'],false,false);	
			    	}
			    	$('#mdlRm').modal('show');
			    },
			    error: function( pObj, pStatus, pXhr) {
			    	oldAlert('Error');
			    }
			});
    	});
		
		$('#btnSaveRm').on('click', function() {
			var lData = crudfrmRm.getValue();
			$.ajax({
		        url: 'appentity/rmsave',
		        data: JSON.stringify(lData),
		        async : false,
		        type: 'POST',
		        success: function( pObj, pStatus, pXhr) {
		        	crudAppEntity.searchHandler();
					$('#mdlRm').modal('hide');
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	alert(pObj.responseJSON.messages[0]);
		        }
		    });
    	});
		
		$('#businessSource').on('change', function() {
			changeBusinessSource();
		});
		
		$('#regFeeType').on('change', function(){
			enableDisableFeeFields('R');
		});
		$('#transFeeType').on('change', function(){
			enableDisableFeeFields('T');
		});
		$('#annFeeType').on('change', function(){
			enableDisableFeeFields('A');
		});

		$('#refererCode').on('change', function() {
			$.ajax({
		        url: 'appentity/rmrefdetail/'+crudfrmRm.getField("refererCode").getValue(),
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	crudfrmRm.getField("aggCompanyGSTN").setValue(pObj.aggCompanyGSTN);
		        	crudfrmRm.getField("aggContactPerson").setValue(pObj.aggContactPerson);
		        	crudfrmRm.getField("aggContactMobile").setValue(pObj.aggContactMobile);
		        	crudfrmRm.getField("aggContactEmail").setValue(pObj.aggContactEmail);
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	clearAggDetails();
		        	oldAlert('Error');
		        }
		    });
		});

		$('#mdlRm #rmUserId').on('change', function() {
			$.ajax({
		        url: 'appentity/rmloc/'+crudfrmRm.getField("rmUserId").getValue(),
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	crudfrmRm.getField("rmLocation").setValue(pObj.location);
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	//oldAlert('Error');
		        }
		    });
		});

		$('#mdlRm #rsmUserId').on('change', function() {
			$.ajax({
		        url: 'appentity/rmloc/'+crudfrmRm.getField("rsmUserId").getValue(),
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	crudfrmRm.getField("rsmLocation").setValue(pObj.location);
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	//oldAlert('Error');
		        }
		    });
		});
		$('#btnEmailNot').on('click', function() {
		    var lSelected = crudAppEntity.getSelectedRow();
	            if ((lSelected==null)||(lSelected.length==0)) {
	                    alert("Please select a row");
	                    return;
	            }
	            location.href='entnot?code='+lSelected.data().code+'&name='+lSelected.data().name;
		});
		$('#btnEntityPref').on('click', function() {
            var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            location.href='entpref?code='+lSelected.data().code;
    	});
		$('#btnSaveLimit').on('click', function() {
			var lAllErrors = crudfrmCheckerLimit.check();
			if (lAllErrors.length==0){
				var lData = {"code":crudfrmCheckerLimit.getField('code').getValue(),
						"instLevel":crudfrmCheckerLimit.getField('instLevel').getValue(),
						"instCntrLevel":crudfrmCheckerLimit.getField('instCntrLevel').getValue(),
						"bidLevel":crudfrmCheckerLimit.getField('bidLevel').getValue(),
						"platformLimitLevel":crudfrmCheckerLimit.getField('platformLimitLevel').getValue(),
						"buyerLimitLevel":crudfrmCheckerLimit.getField('buyerLimitLevel').getValue(),
						"buyerSellerLimitLevel":crudfrmCheckerLimit.getField('buyerSellerLimitLevel').getValue(),
						"userLimitLevel":crudfrmCheckerLimit.getField('userLimitLevel').getValue()
						};
				$('#mdlCheckerLimit').modal('hide');
				$.ajax({
			        url: 'appentity/checkerlimitsetting',
			        data: JSON.stringify(lData),
			        async : false,
			        type: 'PUT',
			        success: function( pObj, pStatus, pXhr) {
			        	crudAppEntity.searchHandler();
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	alert(pObj.responseJSON.messages[0]);
			        }
			    });
			}
		});

		var lCdtReportConfig ={
				"fields":[
								{
									"name": "code",
									"label": "Code",
									"dataType": "STRING"
								},
						  		{
									"name":"creditReport",
									"label":"Credit Report",
									"dataType":"STRING",
									"maxLength":50
								}
							]
					}
		crudCdtReport$ = $('#frmCreditReport').xform(lCdtReportConfig);
		crudCdtReport = crudCdtReport$.data('xform');
		
		$('#btnCreditReport').on('click',function(){
			var lSelected = crudAppEntity.getSelectedRow();
            if ((lSelected==null)||(lSelected.length==0)) {
                    alert("Please select a row");
                    return;
            }
            $('#mdlCreditReport #code').val(lSelected.data().code);
            $('#mdlCreditReport').modal('show');
		});
});
	
	function clearAggDetails(){
    	crudfrmRm.getField("aggCompanyGSTN").setValue("");
    	crudfrmRm.getField("aggContactPerson").setValue("");
    	crudfrmRm.getField("aggContactMobile").setValue("");
    	crudfrmRm.getField("aggContactEmail").setValue("");
	}
	
	function saveCreditReport(){
		var lData={};
		lData['code'] = crudCdtReport.getField('code').getValue();
		lData['creditReport'] = crudCdtReport.getField('creditReport').getValue();
        $.ajax({
		    url: 'appentity/creditreport',
		    data: JSON.stringify(lData),
		    type: 'POST',
		    success: function( pObj, pStatus, pXhr) { 
		    	alert("Saved Successfully");
		    	$('#mdlCreditReport').modal('hide');
		    	return true;
		    },
		    error: function( pObj, pStatus, pXhr) {
		    	$('#mdlCreditReport').modal('hide');
		    	return true;
		    }
		});
	}
	
	function changeBusinessSource(){
		var lValue = crudfrmRm.getField("businessSource").getValue();
		var lKeyFields = ['refererCode','transFeeType','regFeeType','annFeeType','transFeePerc','regFeePerc','annFeePerc','transFeeAmt','regFeeAmt','annFeeAmt'];
		if(lValue=='<%=AppEntityBean.BusinessSource.Direct.getCode()%>'){
        	crudfrmRm.enableDisableField(lKeyFields,false,true);				
		}else if(lValue=='<%=AppEntityBean.BusinessSource.Referal.getCode()%>'){
			lKeyFields = ['refererCode','transFeeType','regFeeType','annFeeType'];
			crudfrmRm.enableDisableField(lKeyFields,true,false);				
		}else{
        	clearAggDetails();
		}
	}
	
	function enableDisableFeeFields(pType){
		var lVal = null;
		var lEnable;
		var lDisable;
		if ('A'== pType){
			lVal = crudfrmRm.getField("annFeeType").getValue();
			if ('A' == lVal){
				lEnable = 'annFeeAmt';
				lDisable = 'annFeePerc';
			}else{
				lEnable = 'annFeePerc';
				lDisable = 'annFeeAmt';
			}
		}else if ('R'== pType){
			lVal = crudfrmRm.getField("regFeeType").getValue();
			if ('A' == lVal){
				lEnable = 'regFeeAmt';
				lDisable = 'regFeePerc';
			}else{
				lEnable = 'regFeePerc';
				lDisable = 'regFeeAmt';
			}
		}else if ('T'== pType){
			lVal = crudfrmRm.getField("transFeeType").getValue();
			if ('A' == lVal){
				lEnable = 'transFeeAmt';
				lDisable = 'transFeePerc';
			}else{
				lEnable = 'transFeePerc';
				lDisable = 'transFeeAmt';
			}
		}
		crudfrmRm.enableDisableField(lEnable,true,false);	
		crudfrmRm.enableDisableField(lDisable,false,false);	
	}
	</script>
   	
    </body>
</html>