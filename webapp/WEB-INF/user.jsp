<!DOCTYPE html>
<%@page import="com.xlx.treds.user.bean.MakerCheckerMapBean.CheckerType"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
boolean lAdmin=request.getParameter("adm")!=null;
%>
<html>
    <head>
        <title>TREDS | Users</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/jquery.autocomplete.css" rel="stylesheet">
        <link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
        <style>
        .value-list .input-group-addon {
        	width:40px;
        }
        </style>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Users" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contAppUser">
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Users</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<div class="col-sm-2 state-T"><section><label for="domain" class="label">Member Code:</label></section></div>
								<div class="col-sm-4 state-T">
									<section class="select">
									<select id="domain"><option value="">Select Member Code</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="loginId" class="label">Login Id:</label></section></div>
								<div class="col-sm-4">
									<section class="input">
									<input type="text" id="loginId" placeholder="Login Id">
									<b class="tooltip tooltip-top-right"></b></section>
								</div>
								<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="status"><option value="">Select Status</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
								<div class="col-sm-2"><section><label for="type" class="label">Type:</label></section></div>
								<div class="col-sm-4">
									<section class="select">
									<select id="type"><option value="">Select Type</option></select>
									<b class="tooltip tooltip-top-right"></b><i></i></section>
								</div>
							</div>
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
			 		<a href="javascript:;" class="right_links" id=btnDownloadCSV style=''><span class="fa fa-download"></span> Download CSV</a>
					<a href="javascript:;" class="right_links" id=btnDownloadReportCSV style=''><span class="fa fa-download"></span> User Report</a>
					<a href="javascript:;" class="right_links secure" data-seckey="user-save" id=btnReset2FA><span class="glyphicon glyphicon-reset"></span> Reset 2FA</a>
					<a href="javascript:;" class="right_links secure" data-seckey="user-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="user-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
<%if (lAdmin) { %>
					<a href="javascript:;" class="right_links secure" data-seckey="user-save" id=btnToggleAPI><span class="glyphicon glyphicon-"></span> Toggle API</a>
<%} %>
					<a href="javascript:;" class="right_links state-P1" id=btnDownloadAgreement style=''><span class="fa fa-download"></span> Download Agreement</a>
					<a href="javascript:;" class="right_links state-P1 state-F1 state-S1" id=btnChkLimit><span class="glyphicon glyphicon-edit"></span> Checker Level</a>
					<a href="javascript:;" class="right_links state-P1 state-F1 state-S1 state-T" id=btnGlobalSettingChk><span class="glyphicon glyphicon-edit"></span> Level Info</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="id"></th>
									<th data-width="60px" data-name="domain">Member Code</th>
									<th data-width="80px" data-name="loginId">Login Id</th>
									<th data-width="60px" data-name="status">Status</th>
									<th data-width="140px" data-name="reason">Reason</th>
									<th data-width="60px" data-name="type">Type</th>
									<th data-width="60px" data-name="salutation">Title</th>
									<th data-width="70px" data-name="firstName">First Name</th>
									<th data-width="70px" data-name="middleName">Middle Name</th>
									<th data-width="70px" data-name="lastName">Last Name</th>
<%if (lAdmin) { %>
									<th data-width="70px" data-name="enableAPI">API Enabled</th>
<%} %>
									<th data-width="170px" data-name="rmListDesc">Roles</th>
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
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">User</h1>
				</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2 state-T"><section><label for="domain" class="label">Member Code:</label></section></div>
						<div class="col-sm-4 state-T">
							<section class="select">
							<select id="domain"><option value="">Select Member Code</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="loginId" class="label">Login Id:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="loginId" placeholder="Login Id">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2 state-T"><section><label for="type" class="label">Type:</label></section></div>
						<div class="col-sm-4 state-T">
							<section class="select">
							<select id="type"><option value="">Select Type</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row" style="display:none">
						<div class="col-sm-2" ><section><label for="password1" class="label">Password:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="password"  autocomplete="off" id="password1" placeholder="Password">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="status"><option value="">Select Status</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="reason" class="label">Reason:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="reason" placeholder="Reason">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="salutation" class="label">Title:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="salutation"><option value="">Select Title</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="firstName" class="label">First Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="firstName" placeholder="First Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="middleName" class="label">Middle Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="middleName" placeholder="Middle Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="lastName" class="label">Last Name:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="lastName" placeholder="Last Name">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="telephone" class="label">Telephone:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="telephone" placeholder="Telephone">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="mobile" class="label">Mobile:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="mobile" placeholder="Mobile">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="email" placeholder="Email">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="altEmail" class="label">Alternate Email:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="altEmail" placeholder="Alternate Email">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="rmIdList" class="label">Roles :</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rmIdList" multiple="multiple" data-role="multiselect"></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="forcePasswordChange" class="label">Force Change Password:</label></section></div>
						<div class="col-sm-4">
							<section class="inline-group">
							<label class="radio"><input type=radio id="forcePasswordChange"><i></i><span></span>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="ipList" class="label">IP White List:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<div class="input-group"><input type="text" class="form-control" id="ipList" placeholder="IP White List">
							<span class="input-group-btn"><button class="btn btn-default btn-success" type="button">Add</button></span>
							<b class="tooltip tooltip-top-right"></b></div>
							<div class="value-list"></div>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="resetPassword" class="label">Reset Password</label></section></div>
						<div class="col-sm-4">
							<section>
							<label class="checkbox"><input type=checkbox id="resetPassword"><i></i>
							<b class="tooltip tooltip-top-left"></b>( Reset and Send mail )</label>
							</section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="locationIdList" class="label">Locations : </label></section></div>
						<div class="col-sm-10">
							<section class="select">
							<select id="locationIdList" multiple="multiple" data-role="multiselect"></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row  state-P state-S state-T">		
						<div class="col-sm-2"><section><label for="fullOwnership" class="label" onchange="toggleFullOwnership()">Full Ownership:</label></section></div>
						<div class="col-sm-4">
							<section>
							<label class="checkbox"><input type=checkbox id="fullOwnership"><i></i>
							<b class="tooltip tooltip-top-left"></b></label>
							</section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="ownerAuId" class="label">Instrument Owner:</label></section></div>
						<div class="col-sm-4">
							<section class="input select">
							<input type="text" id="ownerAuId" placeholder="Select Instrument Owner" data-role="xautocompletefield" data-others="false"/>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="row state-F state-T state-S state-P">
						<div class="col-sm-2"><section><label for="minUserLimit" class="label">Min User Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="minUserLimit" placeholder="Minimum Limit">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="maxUserLimit" class="label">Max User Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="maxUserLimit" placeholder="Maximum Limit">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row state-T">
						<div class="col-sm-2"><section><label for="rmLocation" class="label">RM Location:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="rmLocation"><option value="">RM Location</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<h2>Checkers</h2>
					<div class="row state-P state-S state-T">
						<div class="col-sm-2"><section><label for="checkersInstrument" class="label">Instrument:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersInstrument" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="checkersInstrumentCounter" class="label">Instrument Counter:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersInstrumentCounter" multiple="multiple"  data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row state-F state-T">
						<div class="col-sm-2"><section><label for="checkersPlatformLimit" class="label">Platform Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersPlatformLimit" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="checkersBuyerLimit" class="label">Buyer Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersBuyerLimit" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row state-F state-T">
						<div class="col-sm-2"><section><label for="checkersBuyerSellerLimit" class="label">Buyer Seller Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersBuyerSellerLimit" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="checkersUserLimit" class="label">User Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersUserLimit" multiple="multiple" data-role="bootstrapDualListbox"></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row state-F state-T">
						<div class="col-sm-2"><section><label for="checkersBid" class="label">Bidding:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="checkersBid" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
					<h2>Checkers Level</h2>
					<div class="row state-T state-P1 state-S1">
						<div class="col-sm-2"><section><label for="instLevel" class="label">Instrument:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="instLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="instCntrLevel" class="label">Instrument Counter:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="instCntrLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="row state-T state-F1" >
						<div class="col-sm-2"><section><label for="bidLevel" class="label">Bid:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="bidLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>

						<div class="col-sm-2"><section><label for="platformLimitLevel" class="label">Platform Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="platformLimitLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="row state-T state-F1" >
						<div class="col-sm-2"><section><label for="buyerLimitLevel" class="label">Buyer Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="buyerLimitLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>

						<div class="col-sm-2"><section><label for="buyerSellerLimitLevel" class="label">Buyer Seller Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="buyerSellerLimitLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="row state-T state-F1" >
						<div class="col-sm-2"><section><label for="userLimitLevel" class="label">User Limit:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="userLimitLevel"><option value="">Select Level</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<span id="updateRow"> </span>
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
				</fieldset>
    		</div>
    	</div>
    	<!-- frmMain -->
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
					<div class="row state-T state-P1 state-S1">
							<div class="col-sm-4"><section ><label for="instLevel" class="label">Instrument Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="instLevel" placeholder="instrument">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row state-T state-P1 state-S1">
							<div class="col-sm-4"><section ><label for="instCntrLevel" class="label">Instrument Counter Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="instCntrLevel" placeholder="instrument Counter">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row state-T state-F1">
							<div class="col-sm-4"><section ><label for="bidLevel" class="label">Bid Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="bidLevel" placeholder="bid">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row state-T state-F1">
							<div class="col-sm-4"><section ><label for="platformLimitLevel" class="label">Platform Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="platformLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row state-T state-F1">
							<div class="col-sm-4"><section ><label for="buyerLimitLevel" class="label">Buyer Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="buyerLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row state-T state-F1">
							<div class="col-sm-4"><section ><label for="buyerSellerLimitLevel" class="label">Buyer Seller Limit Level:</label></section></div>
							<div class="col-sm-6">
							<section class="input">
							<input type="text" id="buyerSellerLimitLevel" placeholder="limit">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>	
					<div class="row state-T state-F1">
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
		<div class="modal fade" tabindex=-1 id="mdlChk"><div class="modal-dialog  modal-md modalmedium"><div class="modal-content">
	</div></div></div> 
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>
   	<script src="../js/bootstrap-multiselect.js"></script>
   	<script src="../js/jquery.bootstrap-duallistbox.js"></script>
   	<script id="tplCheckerData" type="text/x-handlebars-template">
<div class="modal-header">Checker Info <button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i
            class="fa fa-close"></i></button>
</div>
<div class="modal-body">
    <fieldset>
        <div class="card">
            <div class="card-body">
                <div role="tabpanel">
                    <ul class="nav nav-tabs" role="tablist">
                        {{#each entitySetting}}
                            <li role="presentation" class="{{#if @first}}active{{/if}}"><a href='#Tab{{@key}}'
                                    aria-controls="Tab{{@key}}" role="tab" data-toggle="tab">{{@key}}</a></li>
                        {{/each}}
                    </ul>
                    <div class="tab-content">
                        {{#each entitySetting}}
                            <div role="tabpanel" class="tab-pane {{#if @first}}active{{/if}}" id="Tab{{@key}}">
                                <table id='{{@key}}' class="table cell-border">
                                    <thead>
                                        <tr>
                                            <th width='30%'>Level</th>
                                            <th width='35%'>Login</th>
                                            <th width='35%'>Name</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {{#each this}}
                                            {{#each this}}
                                                <tr>
                                                    <td width='30%'>{{level}}</td>
                                                    <td width='35%'>{{login}}</td>
                                                    <td width='35%'>{{name}}</td>
                                                </tr>
                                            {{/each}}
                                        {{/each}}
                                    </tbody>
                                </table>
                            </div>
                        {{/each}}
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>
		</script>
	<script type="text/javascript">
	var crudAppUser$ = null, crudAppUser = null, mainForm = null;
	var tplCheckerData;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class).getJsonConfig()%>;
		tplCheckerData=Handlebars.compile($('#tplCheckerData').html());
		var lConfig = {
				resource: "user",
				autoRefresh: true,
				preSaveHandler: function(pData) {
					if ((pData.email!=null)&&(pData.email==pData.altEmail)) {
						alert("Both email ids should not be same.");
						return false;
					}
					if (!loginData.platform) {
						pData.domain=loginData.domain;
						if (!pData.type)
							pData.type=2;
					}
					toggleFullOwnership();
					return true;
				},
				preModifyHandler: function(pData){
					//toggleLimits(pData.type);
					return true;
				},
				postModifyHandler: function(pData) {
					$("#updateRow").html("");
					toggleFullOwnership();
					toggleLimits(pData.type);
					populateCheckers(pData.checkersInstrument,'checkersInstrument','<%=CheckerType.Instrument.getCode()%>');
					populateCheckers(pData.checkersPlatformLimit,'checkersPlatformLimit','<%=CheckerType.Platform_Limit.getCode()%>');
					populateCheckers(pData.checkersBuyerLimit,'checkersBuyerLimit','<%=CheckerType.Buyer_Limit.getCode()%>');
					populateCheckers(pData.checkersBuyerSellerLimit,'checkersBuyerSellerLimit','<%=CheckerType.Buyer_Seller_Limit.getCode()%>');
					populateCheckers(pData.checkersUserLimit,'checkersUserLimit','<%=CheckerType.User_Limit.getCode()%>');
					populateCheckers(pData.checkersBid,'checkersBid','<%=CheckerType.Bid.getCode()%>');
					populateCheckers(pData.checkersInstrumentCounter,'checkersInstrumentCounter','<%=CheckerType.InstrumentCounter.getCode()%>');
					populateOwner(pData.ownerAuId);
					populateRoles(pData.rmIdList);
					populateLocation(pData.locationIdList,'locationIdList');
					var lLevels = {'instLevel':pData.instLevel,'instCntrLevel':pData.instCntrLevel,'bidLevel':pData.bidLevel,'platformLimitLevel':pData.platformLimitLevel,'buyerSellerLimitLevel':pData.buyerSellerLimitLevel,'buyerLimitLevel':pData.buyerLimitLevel,'userLimitLevel':pData.userLimitLevel};
					populateLevels(lLevels);
					//toggleLimits(pData.type);
					getUserData(pData.id);
					$('#forcePasswordChange1').prop('disabled',true);
					$('#forcePasswordChange').prop('disabled',true);
					mainForm.enableDisableField('rmLocation',true,false);
					return true;
				},
				postNewHandler: function() {
					mainForm.getField('fullOwnership').setValue('Y')
					toggleFullOwnership();
					$('#forcePasswordChange1').prop('disabled',true);
					$('#forcePasswordChange').prop('disabled',true);
					mainForm.enableDisableField('rmLocation',true,false);
					populateLevels();
					return true;
				}
			};
		if(loginData.platform) {
			$.each(lFormConfig.fields, function(pIndex,pValue){
				if ((pValue.name==='checkers') || (pValue.name==='rmIdList') || (pValue.name==='ownerAuId')) {
					pValue.dataSetType=null;
					pValue.dataSetValues=null;
				}
			});
		}
		lConfig = $.extend(lConfig, lFormConfig);
		crudAppUser$ = $('#contAppUser').xcrudwrapper(lConfig);
		crudAppUser = crudAppUser$.data('xcrudwrapper');
		mainForm = crudAppUser.options.mainForm;
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
						"name": "code",
						"label": "Code",
						"dataType": "STRING"
					},
					{
						"name": "instCntrLevel",
						"label": "Instrument Counter Level",
						"nonDatabase":true,
						"dataType": "INTEGER",
						"maxValue":10,
						"minValue":0
					}
				]
		}
		crudfrmCheckerLimit$ = $('#frmCheckerLimit').xform(lLevelConfig);
     	crudfrmCheckerLimit = crudfrmCheckerLimit$.data('xform');
		if (!loginData.platform) {
			mainForm.alterField(['domain', 'type'], false, false);
		} else {
			$('#frmMain #domain').on('change', function() {
				populateCheckers(null,'checkersInstrument','<%=CheckerType.Instrument.getCode()%>');
				populateCheckers(null,'checkersPlatformLimit','<%=CheckerType.Platform_Limit.getCode()%>');
				populateCheckers(null,'checkersBuyerLimit','<%=CheckerType.Buyer_Limit.getCode()%>');
				populateCheckers(null,'checkersBuyerSellerLimit','<%=CheckerType.Buyer_Seller_Limit.getCode()%>');
				populateCheckers(null,'checkersUserLimit','<%=CheckerType.User_Limit.getCode()%>');
				populateCheckers(null,'checkersBid','<%=CheckerType.Bid.getCode()%>');
				populateCheckers(null,'checkersInstrumentCounter','<%=CheckerType.InstrumentCounter.getCode()%>');
				populateOwner(null);
				populateRoles(null);
				populateLocation(null,'locationIdList');
			});
		}
		$('#btnReset2FA').on('click', function(){
			var lSelected = crudAppUser.getSelectedRow().data();
			if (lSelected == null) {
				alert("Please select a row");
				return;
			}
			$('#btnReset2FA').prop('disabled',true);
			$.ajax( {
	            url: 'user/reset2FA/'+lSelected.id,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	alert("2FA security settings cleared.");
	            },
	        	error: errorHandler,
				complete: function() {
					$('#btnReset2FA').prop('disabled',false);
				}
	        });		
		});
		$('#btnToggleAPI').on('click', function(){
			var lSelected = crudAppUser.getSelectedRow().data();
			if (lSelected == null) {
				alert("Please select a row");
				return;
			}
			$('#btnToggleAPI').prop('disabled',true);
			$.ajax( {
	            url: 'user/toggleAPI/'+lSelected.id,
	            type: "GET",
	            success: function( pObj, pStatus, pXhr) {
	            	alert("API Status toggled successfully.");
	            },
	        	error: errorHandler,
				complete: function() {
					$('#btnToggleAPI').prop('disabled',false);
				}
	        });		
		});
		$('#resetPassword').on('click', function(){
			var lResetPwd =  mainForm.getField('resetPassword').getValue();
			if(!lResetPwd) lResetPwd = 'N';
			mainForm.getField('forcePasswordChange').setValue(lResetPwd);
		});
		$('#btnDownloadCSV').on('click', function() {
			var lFilter=crudAppUser.options.searchForm.getValue();
			lFilter.columnNames=crudAppUser.options.tableConfig.columnNames;
			downloadFile('user/all',null,JSON.stringify(lFilter));
		});
		$('#btnDownloadReportCSV').on('click', function() {
			var lFilter=crudAppUser.options.searchForm.getValue();
			downloadFile('user/reportDownload',null,JSON.stringify(lFilter));
		});
		$( "#fullOwnership" ).change(function() {
			toggleFullOwnership();
		});
		$('#btnDownloadAgreement').on('click', function() {
			var lSelected = crudAppUser.getSelectedRow().data();
			if (lSelected == null) {
				alert("Please select a row");
				return;
			}
			downloadFile('puraggacc/downloadclickwrap?<%=AppConstants.CLICKWRAP_QUERYPARAMETER_FILENAME%>=<%=AppConstants.CLICKWRAP_FILECODE_AGREEMENT%>',null,JSON.stringify({"columnNames" : [],"domain" : lSelected.domain,"getImage":true  }) );
		});
		
		$('#btnChkLimit').on('click', function() {
			$.ajax({
			    url: 'appentity/chksetting/'+loginData.domain,
			    type: 'GET',
			    success: function( pObj, pStatus, pXhr) { 
			    	crudfrmCheckerLimit.setValue(pObj);
		            crudfrmCheckerLimit.enableDisableField('code',false,false);
		            $('#mdlCheckerLimit').modal('show');
			    },
			    error: function( pObj, pStatus, pXhr) {
			    	oldAlert('Error');
			    }
			});
            
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
			        },
			        error: function( pObj, pStatus, pXhr) {
			        	alert(pObj.responseJSON.messages[0]);
			        }
			    });
			}
		});
		$('#btnGlobalSettingChk').on('click', function() {
			var lUrl = 'user/getglobalcheckersetting'
			if (loginData.domain=='TREDS'){
				var lSelected = crudAppUser.getSelectedRow().data();
				if (lSelected == null) {
					alert("Please select a row");
					return;
				}
				lUrl += '?domain='+lSelected.domain;
			}
			$.ajax({
		        url:lUrl,
		        async : false,
		        type: 'GET',
		        success: function( pObj, pStatus, pXhr) {
		        	$('#mdlChk .modal-content').html(tplCheckerData(pObj));
		        	var lDataTableConf = {
		        			"order": [[ 0, "asc" ]],
		        			"paging": false,
		        			"searching": false
		        	};
		        	$('#Instrument').DataTable(lDataTableConf);
		        	$('#Instrument_Counter').DataTable(lDataTableConf);
		        	$('#Bid').DataTable(lDataTableConf);
		        	$('#Platform_Limit').DataTable(lDataTableConf);
		        	$('#User_Limit').DataTable(lDataTableConf);
		        	$('#Buyer_Limit').DataTable(lDataTableConf);
		        	$('#BuyerSeller_Limit').DataTable(lDataTableConf);
		           	showModal($('#mdlChk'));
		        },
		        error: function( pObj, pStatus, pXhr) {
		        	alert(pObj.responseJSON.messages[0]);
		        }
		    });
		});
	});
	function populateCheckers(pVal, pField,pType) {
		var lDomain = mainForm.getField('domain').getValue();
		var lFldCheckers = mainForm.getField(pField);
		lFldCheckers.options.dataSetType = 'RESOURCE';
		lFldCheckers.options.dataSetValues = 'user/checkers?domain='+lDomain+'&type='+pType;
		lFldCheckers.init();
		if (pVal) lFldCheckers.setValue(pVal);
	}
	function populateOwner(pVal){
		var lDomain = mainForm.getField('domain').getValue();
		var lFldOwner = mainForm.getField('ownerAuId');
		lFldOwner.options.dataSetType = 'RESOURCE';
		lFldOwner.options.dataSetValues = 'user/checkers?domain='+lDomain;
		lFldOwner.init();
		if (pVal) lFldOwner.setValue(pVal);
	}
	function populateRoles(pVal) {
		var lDomain = mainForm.getField('domain').getValue();
		var lFldRoles = mainForm.getField('rmIdList');
		lFldRoles.options.dataSetType = 'RESOURCE';
		lFldRoles.options.dataSetValues = 'user/roles?domain='+lDomain;
		lFldRoles.init();
		if (pVal) lFldRoles.setValue(pVal);
	}
	function populateLocation(pVal) {
		var lDomain = mainForm.getField('domain').getValue();
		var lField= mainForm.getField('locationIdList');
		lField.options.dataSetType = 'RESOURCE';
		lField.options.dataSetValues = 'companylocation/all?aecode='+lDomain;
		lField.init();
		if (pVal) lField.setValue(pVal);
	}
	function toggleFullOwnership(){
		var toggle = mainForm.getField('fullOwnership').getValue();
		var enable = true;
		if(toggle == "Y"){ 
			enable = false;
		}
		mainForm.enableDisableField('ownerAuId',enable,false);
	}
	function toggleLimits(pType){
		var enable = true;
		var clear = false;
		var lFields = ["minUserLimit","maxUserLimit"];
		if('<%=AppUserBean.Type.Admin.getCode()%>'==pType){
			enable = false;
			clear = true;
		}
		mainForm.enableDisableField(lFields,enable,clear);
	}
	
	function getUserData(pId){
		var lData = {id:pId,"columnNames":["id","recordCreateTime","recordUpdateTime"]};
		$.ajax( {
            url: 'user/all',
            type: "POST",
            data:JSON.stringify(lData),
            success: function( pObj, pStatus, pXhr) {
            	var tmpData = "";
            	if(pObj[0][1]!=null){
            		tmpData += "<b>Create Time</b>  : "+ pObj[0][1];
            	}
            	if(pObj[0][2]!=null){
            		tmpData += "<br><b>Update Time</b> : "+ pObj[0][2];
            	}
            	$("#updateRow").html(tmpData);
            },
        	error: errorHandler,
			complete: function() {
			}
        });		
	}
	
	function populateLevels(pLevels) {
		var lDomain = mainForm.getField('domain').getValue();
		for(var lLevelKey in pLevels){
			var lField= mainForm.getField(lLevelKey);
			lField.options.dataSetType = 'RESOURCE';
			lField.options.dataSetValues = 'user/getentitylimit/'+lLevelKey+'?domain='+lDomain;
			lField.init();
			if (pLevels[lLevelKey]) lField.setValue(pLevels[lLevelKey]);
		}
	}
	
	</script>
   	
    </body>
</html>