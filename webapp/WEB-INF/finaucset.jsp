<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.auction.bean.FinancierAuctionSettingBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
FinancierAuctionSettingBean.Level lLevel = (FinancierAuctionSettingBean.Level)request.getAttribute("level");
String lTitle = null;
boolean lFinancier = true, lPurchaser = false, lSupplier = false, lUser = false;
switch(lLevel) {
case Financier_Self:
    lTitle = "Platform Limits";
    break;
case Financier_Buyer:
    lTitle = "Buyer Limits";
    lPurchaser = true;
    break;
case Financier_Buyer_Seller:
    lTitle = "Buyer-Seller Limits";
    lPurchaser = true;
    lSupplier = true;
    break;
case Financier_User:
    lTitle = "User Limits";
    lUser = true;
    break;
case System_Buyer:
    lTitle = "Buyer Limits(Bank)";
    lFinancier = false;
    lPurchaser = true;
    break;
}
%>
<html>
    <head>
        <title>TREDS | <%=lTitle %></title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
        <link href="../css/jquery.autocomplete.css" rel="stylesheet">
    </head>
    <body class="skin-blue">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="<%=lTitle %>" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contFinancierAuctionSetting">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title"><%=lTitle%></h1>
			</div>
		</div>
	
<!-- frmSearch -->
		<div id="frmSearch">
<%
if (lLevel != FinancierAuctionSettingBean.Level.Financier_Self){
%>		
			<div class="xform tab-pane panel panel-default no-margin collapse" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
	<%
	if (lPurchaser) {
	%>				
						<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="purchaser"><option value="">Select Buyer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
		<%
		if (!lSupplier) {
		%>				
						<div class="col-sm-2"><section><label for="purchaserRef" class="label">Buyer Code:</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="purchaserRef" placeholder="Buyer Internal Code">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
	<%
		}
	}
	if (lSupplier) {
	%>				
						<div class="col-sm-2"><section><label for="supplier" class="label">Seller:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="supplier"><option value="">Select Seller</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
	<%
	}
	if (lUser) {
	%>				
						<div class="col-sm-2"><section><label for="auId" class="label">User:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="auId"><option value="">Select User</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
	<%
	}
	%>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
	<%
	if (lPurchaser) {
	%>				
									<button type="button" class="btn btn-ShowTotalLimit btn-info btn-lg" id=btnShowTotalLimit><span class="fa fa-search"></span> Total Limit</button>
	<%
	}
	%>				
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
			 						<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
								</div>
							</div>
						</div>
				</div>
				</fieldset>
			</div>
<%
}
%>		
                 <div class="cloudTabs">
                 	<ul class="cloudtabs nav nav-tabs">
					 <li><a href="#tab0" data-toggle="tab" >Draft <span id="badge0" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab1" data-toggle="tab" >Checker Pending <span id="badge1" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab2" data-toggle="tab" >For Approval <span id="badge2" class="badge badge-primary"></span></a></li>
					 <li><a href="#tab3" data-toggle="tab" >Active <span id="badge3" class="badge badge-primary"></span></a></li>
			 		</ul>
			 	</div>

			<div class="filter-block clearfix">
				<div class="">
					<a href="javascript:;" class="right_links" onClick="javascript:downloadCsv()"><span class="fa fa-download"></span> Download CSV</a>
					<a href="javascript:;" class="right_links" onClick="javascript:crudFinancierAuctionSetting.searchHandler()"><span class="glyphicon glyphicon-refresh"></span> Refresh</a>
<%-- 					<a href="javascript:;" class="right_links secure" data-seckey="finaucset-<%=lLevel.getCode()%>-upload" id=btnUpload><span class="glyphicon glyphicon-open"></span> Upload</a> --%>
					
					<a href="javascript:;" class="right_links secure" data-seckey="finaucset-<%=lLevel.getCode()%>-upload" id=btnFinaucset><span class="glyphicon glyphicon-open"></span> Upload</a>
					
					
					<a href="javascript:;" class="right_links secure" data-seckey="finaucset-save-<%=lLevel.getCode()%>" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
<%
if (lLevel != FinancierAuctionSettingBean.Level.Financier_Self) {
%>
					<a class="left_links collapsed" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="finaucset-save-<%=lLevel.getCode()%>" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
<%
} else {
%>					<a href="javascript:;" class="right_links" onClick="javascript:viewLendingRates()"><span class="glyphicon glyphicon-eye-open"></span> Lending Rates</a>
<%    
}
%>
<%
if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer) || (lLevel == FinancierAuctionSettingBean.Level.Financier_User)) {
%>
					<a href="javascript:;" class="right_links" onClick="javascript:viewEffRates()"><span class="glyphicon glyphicon-eye-open"></span> Effective Rates</a>	
<%    
}
%>
					<span class="btn-group0 hidden">
						<a href="javascript:;" class="right_links secure" data-seckey="finaucset-save-<%=lLevel.getCode()%>" onClick="javascript:updateAppStatus('<%=FinancierAuctionSettingBean.ApprovalStatus.Deleted.getCode()%>','Withdraw')"><span class=""></span> Withdraw</a>
						<a href="javascript:;" class="right_links secure" data-seckey="finaucset-save-<%=lLevel.getCode()%>" onClick="javascript:updateAppStatus('<%=FinancierAuctionSettingBean.ApprovalStatus.Submitted.getCode()%>','Submit')"><span class=""></span> Submit</a>
						<a></a>
					</span>
					<span class="btn-group2 hidden">
						<a href="javascript:;" class="right_links secure" data-seckey="finaucset-status-checker-<%=lLevel.getCode()%>" onClick="javascript:updateAppStatus('<%=FinancierAuctionSettingBean.ApprovalStatus.Returned.getCode()%>','Return')"><span class=""></span> Return</a>
						<a href="javascript:;" class="right_links secure" data-seckey="finaucset-status-checker-<%=lLevel.getCode()%>" onClick="javascript:updateAppStatus('<%=FinancierAuctionSettingBean.ApprovalStatus.Rejected.getCode()%>','Reject')"><span class=""></span> Reject</a>
						<a href="javascript:;" class="right_links secure" data-seckey="finaucset-status-checker-<%=lLevel.getCode()%>" onClick="javascript:updateAppStatus('<%=FinancierAuctionSettingBean.ApprovalStatus.Approved.getCode()%>','Approve')"><span class=""></span> Approve</a>
						<a></a>
					</span>

				</div>
			</div>
			
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered"  id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="id">Id</th>
									<th data-width="120px" data-name="finName" data-sel-exclude="true" data-visible="false" id="finName">Financier</th>
	<%
	if (lPurchaser) {
	%>
									<th data-width="120px" data-name="purName">Buyer</th>
									<th data-width="120px" data-name="purchaserRef">Buyer Code</th>
	<%
	}
	if (lLevel == FinancierAuctionSettingBean.Level.Financier_Self) {
	%>
									<th data-width="120px" data-name="financierRef">Reference Code</th>
	<% 
	}
	if (lSupplier) {
	%>
									<th data-width="120px" data-name="supName">Seller</th>
									<th data-width="120px" data-name="supplierRef">Seller Code</th>
	<%
	}
	if (lUser) {
	%>
									<th data-width="70px" data-name="loginId">User</th>
	<%
	}
	%>
									<th data-width="60px" data-name="currency">Currency</th>
									<th data-width="70px" data-name="limit">Exposure Limit</th>
									<th data-width="70px" data-name="utilised">Utilized Exposure Limit</th>
									<th data-width="70px" data-name="balance">Balance Exposure Limit</th>
									<th data-width="70px" data-name="bidLimit">Bidding Limit</th>
									<th data-width="70px" data-name="bidLimitUtilised">Utilized Bidding Limit</th>
									<th data-width="80px" data-name="utilPercent">Utilization %</th>
	<%
	if (lLevel == FinancierAuctionSettingBean.Level.Financier_Self) {
	%>
									<th data-visible="false" data-name="baseRateList"></th>
	<%
	}
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer) || (lLevel == FinancierAuctionSettingBean.Level.Financier_User)) {
	%>
									<th data-visible="false" data-name="effRateRange"></th>
	<%
	}
	if ((lLevel != FinancierAuctionSettingBean.Level.Financier_Buyer_Seller) && (lLevel != FinancierAuctionSettingBean.Level.System_Buyer)) {
	%>
		<%
			if(lLevel != FinancierAuctionSettingBean.Level.Financier_Buyer) {
		%>
									<th data-width="70px" data-name="minBidRate">Minimum Bid Rate</th>
									<th data-width="70px" data-name="maxBidRate">Maximum Bid Rate</th>
		<%
			}
		%>
		<%
		if (lLevel != FinancierAuctionSettingBean.Level.Financier_Self) {
		%>
									<th data-width="70px" data-name="minSpread">Minimum Spread</th>
									<th data-width="70px" data-name="maxSpread">Maximum Spread</th>
		<%
		}
		%>
	<%
	}
	%>
	<%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Self) || (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
	%>
									<th data-width="70px" data-name="purchaserCostLeg">Interest Collection Leg</th>
	<%
	}
	%>
	<%
	if (lLevel == FinancierAuctionSettingBean.Level.Financier_Self) {
	%>
									<th data-width="70px" data-name="bypassCheckForDelete">Bypass Checker for Bid Cancellation</th>
									<th data-width="70px" data-name="withdrawBidModChecker">Withdraw Bid upon Modification</th>
	<%
	}
	%>
									<th data-width="60px" data-name="expiryDate">Expiry Date</th>
									<th data-width="60px" data-name="active">Status</th>
									<th data-width="60px" data-name="effectiveStatus">Effective Status</th>
									<th data-width="80px" data-name="makerUserLogin">Maker</th>
									<th data-width="80px" data-name="checkerUserLogin">Checker</th>
									<th data-width="80px" data-name="approvalStatus">Approval Status</th>
									<th data-visible="false" data-name="checkerFlag"></th>
										<%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
	%>
									<th data-width="70px"  data-name="financierLocation">Settlement Location</th>
	<%
	}
	%>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		
		<!-- frmMain -->
		<!-- Finuploader Closed -->
		
				<div class="modal fade" tabindex=-1 id="mdlFinaucset"><div class="modal-dialog  modal-lg modalLarge"><div class="modal-content">
        <div class="modal-header"><span>&nbsp;IMPORTANT INSTRUCTIONS</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body" height='200px'>
		<table border=0 borcellspacing=1 cellpadding=2 width=100%>
        <tr><td class=frmfld>
                <ul>
                <li>File name can be anything.</li>
                <li>File should contain comma separated utilization information</li>
                <li>First record in the file should be the header record. (see example below)</li>
                <li>Subsequent records in the file should contain following field values separated by commas</li><br>
                </ul>       
                 <table border=1 class=frmfld width=100%>
                        <tr align=center>
                        <th>Headers</th>
						<th>Mandatory</th><th>Description</th><th>Value Range</th><th>Default Value</th></tr>
                            
                        <tr><td>loginId</td><td>Yes</td><td></td><td></td><td></td></tr>
                            
                        <tr><td>minBidRate</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>maxBidRate</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>minSpread</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr>><td>maxSpread</td><td></td><td></td><td></td><td></td></tr>
                        
                        <tr><td>bidLimit</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>bidLimitUtilised</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>sellerLimitMandatory</td><td></td><td></td><td><ul><li>Y=Yes</li><li>N=No</li></ul></td><td></td></tr> 
                            
                        <tr><td>balance</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>checkerFlag</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>supplier</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>limit</td><td></td><td></td><td></td><td></td></tr>      
                            
                        <tr><td>finClId</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>id</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>supName</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>cersaiCode</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>level</td><td>Yes</td><td></td><td><ul><li>YNNN = Financier Self</li><li>YYNN = Financier Buyer</li><li>YYYN = Financier Buyer Seller</li><li>YNNY = Financier User</li><li>NYNN = System Buyer</li></ul></td><td></td></tr>
                            
                         <tr><td>purchaser</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>baseRate1</td><td></td><td></td><td></td><td></td></tr>                       
                         
                         <tr><td>baseRate2</td><td></td><td></td><td></td><td></td></tr>
                            
                        <tr><td>tenure1</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>tenure2</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>active</td><td>Yes</td><td></td><td><ul><li>Y = Active</li><li>N = Suspended</li></ul></td><td></td></tr> 
                            
                         <tr><td>finName</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>effectiveStatus</td><td></td><td></td><td><ul><li>Y = Active</li><li>N = In-Active</li></ul></td><td></td></tr>
                            
                         <tr><td>recordCreator</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>auId</td><td></td><td>The logged-in-user of the financier for which the current limit is to be applied.</td><td></td><td></td></tr>
                            
                         <tr><td>financier</td><td></td><td></td><td></td><td></td></tr>
                            
                         <tr><td>financierRef</td><td></td><td></td><td></td><td></td></tr>
                            
						 <tr><td>utilised</td><td>Yes</td><td></td><td></td><td></td></tr>
						 
						 <tr><td>effRateRange</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>expiryDate</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>currency</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>bypassCheckForDelete</td><td></td><td></td><td>Y=Yes</td><td></td></tr>
						 
						 <tr><td>makerAUId</td><td>Yes</td><td></td><td></td><td></td></tr>
						 
						 <tr><td>financierLocation</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>approvalStatus</td><td>Yes</td><td></td><td><ul><li>DFT = Draft</li><li>SUB = Submitted</li><li>RET = Returned</li><li>REJ = Rejected</li><li>APP = Approved</li><li>DEL = Deleted</li></ul></td><td></td></tr>
						 
						 <tr><td>approvalRemarks</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>purchaserRef</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>purchaserCostLeg</td><td></td><td></td><td><ul><li>L1 = Leg 1</li><li>L2 = Leg 2</li></ul></td><td></td></tr>
						 
						 <tr><td>userName</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>withdrawBidModChecker</td><td></td><td></td><td><ul><li>Y = Yes</li></ul></td><td></td></tr>
						 
						 <tr><td>chkLevel</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>checkerAUId</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>supplierRef</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>purName</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>utilPercent</td><td></td><td></td><td></td><td></td></tr>
						 
						 <tr><td>rateRangeType</td><td></td><td></td><td><ul><li>A = Absolute</li><li>S = Spread</li></ul></td><td></td></tr>
						 
                        </table>
                        <br>
				<ul>
                <li>A Single file should contain utilization for that Business date.</li>
                <li>The import will accept only those records where it matches the selected Hall Code.</li>
                <li>Item Codes not found in the list will be skipped.</li>
                <li>Only those utilizations in the file will be diplayed, rest will be reset to Zero (0).</li>
                </ul>
        </td></tr>
    	
		<tr><td>SAMPLE FILE CONTENTS
        </td></tr>
        
    <%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Self)) {
	%>
        <tr><td class=frmfld >
        <pre>
active,approvalRemarks,approvalStatus,auId,balance,baseRate1,tenure1,baseRate2,tenure2,bidLimit,bidLimitUtilised,bypassCheckForDelete,
cersaiCode,checkerAUId,checkerFlag,checkerUserLogin,chkLevel,currency,effRateRange,effectiveStatus,expiryDate,finClId,finName,financier,
financierLocation,financierRef,id,isLocationEnabled,level,limit,loginId,makerAUId,maxBidRate,maxSpread,minBidRate,minSpread,purName,purchaser,
purchaserCostLeg,purchaserRef,rateRangeType,sellerLimitMandatory,supName,supplier,supplierRef,userName,utilPercent,utilised,withdrawBidModChecker
Y,null,APP,,100004200200,8.52,11,9.52,46,100001520020,0,Y,142,0,,,0,INR,,Y,,,Financier 19,XF0000019,,xf19,76,,YNNN,100004200200,ADMIN,58,15.38,,5.3,,
null,null,L2,null,A,,null,null,null,null,0,0,Y,,
        </pre>
        </td></tr>
	<%
	}
	%>
        
    <%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
	%>
        <tr><td class=frmfld >
        <pre>
active,approvalRemarks,approvalStatus,auId,balance,bidLimit,bidLimitUtilised,bypassCheckForDelete,cersaiCode,checkerAUId,
checkerFlag,checkerUserLogin,chkLevel,currency,effRateRange,effectiveStatus,expiryDate,finClId,finName,financier,
financierLocation,financierRef,id,isLocationEnabled,level,limit,loginId,makerAUId,maxBidRate,maxSpread,minBidRate,
minSpread,purName,purchaser,purchaserCostLeg,purchaserRef,rateRangeType,supName,supplier,supplierRef,userName,
utilPercent,utilised,withdrawBidModChecker
Y,null,APP,58,120000,12004,0.1,,null,120,,USER1,0,INR,,Y,,,Financier19,XF0000019,null,XF0000019,83,,YYNN,120000,ADMIN,58,0.1,5.22,0.1,-5.11,Purchaser21,
XP0000021,L1,pref21onetyyre,S,null,null,null,null,0,0,,
        </pre>
        </td></tr>
	<%
	}
	%>
	   
	<%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer_Seller)) {
	%>
        <tr><td class=frmfld >
        <pre>
active,approvalRemarks,approvalStatus,auId,balance,bidLimit,bidLimitUtilised,bypassCheckForDelete,cersaiCode,checkerAUId,
checkerFlag,checkerUserLogin,chkLevel,currency,effRateRange,effectiveStatus,expiryDate,finClId,finName,financier,
financierLocation,financierRef,id,isLocationEnabled,level,limit,loginId,makerAUId,maxBidRate,maxSpread,minBidRate,
minSpread,purName,purchaser,purchaserCostLeg,purchaserRef,rateRangeType,sellerLimitMandatory,supName,supplier,
supplierRef,userName,utilPercent,utilised,withdrawBidModChecker
Y,null,APP,58,100000,0.1,9,,null,0,,null,0,INR,,Y,29-Jul-2021,,Financier19,XF0000019,null,null,85,,YYYN,100000,ADMIN,58,0.1,0.0,0.5,0.0,Purchaser&18,
XP0000018,,pref18ONE1wrwrw,,,Supplier15,XS0000015,XS0000015,null,0,0,,
        </pre>
        </td></tr>
	<%
	}
	%>
	
	<%
	if ((lLevel == FinancierAuctionSettingBean.Level.Financier_User)) {
	%>
        <tr><td class=frmfld >
        <pre>
loginId,minBidRate,minSpread,recordUpdator,maxBidRate,bidLimitUtilised,sellerLimitMandatory,balance,maxSpread,
checkerFlag,supplier,limit,bidLimit,finClId,id,supName,cersaiCode,level,purchaser,baseRate1,baseRate2,tenure1,
tenure2,active,finName,effectiveStatus,recordCreator,auId,financier,financierRef,utilised,effRateRange,expiryDate,
currency,bypassCheckForDelete,makerAUId,financierLocation,approvalStatus,approvalRemarks,purchaserRef,purchaserCostLeg,
userName,withdrawBidModChecker,chkLevel,checkerAUId,supplierRef,purName,utilPercent,rateRangeType
USER1,1.2,0.9,,5.3,,Y,100000,1.9,,,1000000,5.2,,50,,,YNNN,,11,12,8,9,Y,Financier 19,Y,,120,XF0000019,XF0000019,1000.5,,
29-Jul-2021,INR,Y,49,Reg Office,SUB,hfghghghghgh,,,,Y,,,,,,,
        </pre>
        </td></tr>
	<%
	}
	%>
	
        </table>
        <div>
       	<div class="box-footer" align="center">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id=btnUpload data-seckey="finaucset-<%=lLevel.getCode()%>-upload"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
							<a></a>
						</div>
					</div>
				</div>
    		</div>
        </div>
</div>
		</div></div></div>
		
		
		
		
		
		<!-- Finuploader Closed -->
		<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content">
		<div class="modal-header"><span>&nbsp;<%=lTitle%></span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
		<div class="modal-body modal-no-padding">
    	<div class="xform box box-danger" id="frmMain">
    		<div class="box-body">
<%
if (lLevel == FinancierAuctionSettingBean.Level.Financier_Self) {
%>
			<ul class="nav nav-tabs" style="background-color:#eee">
			  <li class="active"><a data-toggle="tab" href="#gen">General</a></li>
			  <li><a data-toggle="tab" href="#rate">Lending Rates</a></li>
			</ul>
<%
}
%>    		
			<div class="tab-content">
			<div id="gen" class="tab-pane active">
<%
if (lPurchaser) {
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="purchaser" class="label">Buyer:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaser" onChange="javascript:setPurchaserRef()"><option value="">Select Buyer</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>

				<div class="row">
					<div class="col-sm-4"><section><label for="purchaserRef" class="label">Buyer Code:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="purchaserRef" placeholder="Buyer Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
<%
}
if (lSupplier) {
%>				
				<div class="row">
					<div class="col-sm-4"><section><label for="supplier" class="label">Seller:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="supplier" onChange="javascript:setSupplierRef()"><option value="">Select Seller</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="supplierRef" class="label">Seller Code:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="supplierRef" placeholder="Seller Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
<%
}
if (lUser) {
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="auId" class="label">User:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="auId"><option value="">Select User</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
<%
}
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="limit" class="label">Exposure Limit:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="limit" placeholder="Exposure Limit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4">
						<section class="select">
						<select id="currency"><option value="">Select Currency</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
<%
if ((lLevel != FinancierAuctionSettingBean.Level.Financier_Buyer_Seller) && (lLevel != FinancierAuctionSettingBean.Level.System_Buyer)) {
%>
		<%
			if(lLevel != FinancierAuctionSettingBean.Level.Financier_Buyer) {
			    if (lLevel == FinancierAuctionSettingBean.Level.Financier_User) {
		%>
			
				<div class="callout callout-info">
					Select either of these optional ranges to prevent erroneous entries at the time of bidding.
				</div>
				<div class="row">
					<div class="col-sm-4"></div>
					<div class="col-sm-8">
						<section class="inline-group">
						<label class="radio"><input type=radio id="rateRangeType"><i></i><span></span>
						</label>
						</section>
						<section class="view"></section>
					</div>
				</div>
		<%
			    }
		%>
				<div class="row">
					<div class="col-sm-4"><section><label for="minBidRate" class="label">Absolute Bid Rate Range:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="minBidRate" placeholder="Minimum Bid Rate">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="maxBidRate" placeholder="Maximum Bid Rate">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
		<%
			}
		%>
		<%
		if (lLevel != FinancierAuctionSettingBean.Level.Financier_Self) {
		%>
				<div class="callout callout-info">
					The Spread would be with respect to the Base Rate/MCLR specified in Platform Limit.
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="minSpread" class="label">Rate Spread:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="minSpread" placeholder="Minimum Spread">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="maxSpread" placeholder="Maximum Spread">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
		<%
		}
		%>
<%
}
%>


<%
if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Self) || (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
%>
				<div class="callout callout-info">
					In case of Buyers bearing the discounting cost, the interest cost can be collected either during leg1 or leg2.
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="purchaserCostLeg" class="label">Interest Collection Leg (if Buyer bears cost):</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="purchaserCostLeg"><option value="">Select</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
<%
}
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="active" class="label">Status:</label></section></div>
					<div class="col-sm-8">
						<section class="input select">
						<input type="text" id="active" placeholder="Select Active" data-role="xautocompletefield" data-others="false"/>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="effectiveStatus" class="label">Effective Status:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="effectiveStatus"><option value="">Select</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>

				<div class="row">
					<div class="col-sm-4"><section><label for="bidLimit" class="label">Bidding Limit:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="bidLimit" placeholder="Bidding Limit">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-4"><section><label for="expiryDate" class="label">Expiry Date:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="expiryDate" placeholder="Expiry Date" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
				
<%
if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="sellerLimitMandatory" class="label">Buyer-Seller Limit Mandatory:</label></section></div>
					<div class="col-sm-8">
					<section class="input">
						<label class="checkbox"><input type=checkbox id="sellerLimitMandatory"><i></i>
						<b class="tooltip tooltip-top-left"></b></label></section>
						<section class="view"></section>
					</div>
				</div>
				
				<div class="row" id="locationRow">
					<div class="col-sm-4"><section><label for="finClId" class="label">Settlement Location:</label></section></div>
					<div class="col-sm-8">
						<section class="select">
						<select id="finClId"><option value="">Select Location</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>

<%
}
%>

<%
if (lLevel == FinancierAuctionSettingBean.Level.Financier_Self) {
%>
				<div class="row">
					<div class="col-sm-4"><section><label for="financierRef" class="label">Reference Code:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="financierRef" placeholder="Refrence Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4"><section><label for="cersaiCode" class="label">Cersai Code:</label></section></div>
					<div class="col-sm-8">
						<section class="input">
						<input type="text" id="cersaiCode" placeholder="Cersai Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-8"><section><label for="bypassCheckForDelete" class="label">Bypass Checker for Bid Cancellation:</label></section></div>
					<div class="col-sm-4">
						<section>
						<label class="checkbox"><input id="bypassCheckForDelete" type="checkbox"><i></i><span></span></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-8"><section><label for="withdrawBidModChecker" class="label">Withdraw Bid upon Modification Sent to Checker:</label></section></div>
					<div class="col-sm-4">
						<section>
						<label class="checkbox"><input id="withdrawBidModChecker" type="checkbox"><i></i><span></span></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				</div>
				<div id="rate" class="tab-pane">
				<div class="row">
					<div class="col-sm-12">
						<fieldset id="baseRateList">
							<div style="display:none" id="baseRateList-frmSearch">
								<div class="row">
									<div class="col-sm-12">
										<div class="btn-group nonView">
											<button type="button" class="btn btn-sm btn-default" id="baseRateList-btnNew"><span class="fa fa-plus"></span></button>
											<button type="button" class="btn btn-sm btn-default" id="baseRateList-btnModify"><span class="fa fa-pencil"></span></button>
											<button type="button" class="btn btn-sm btn-default" id="baseRateList-btnRemove"><span class="fa fa-minus"></span></button>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12">
										
										<table class="table table-striped table-bordered table-condensed" id="baseRateList-tblData" width="240px" data-scroll-y="90px">
											<thead><tr>
												<th data-width="120px" data-name="tenure" data-data="tenure">Tenure</th>
												<th data-width="120px" data-name="baseRate" data-data="baseRate">Base Rate</th>
											</tr></thead>
										</table>
									</div>
								</div>
							</div>
							<div class="modal fade" tabindex=-1><div class="modal-dialog modal-xl"><div class="modal-content"><div class="modal-body no-padding"><div class="container">
							<div id="baseRateList-frmMain" class="xform">
								<header>Lending Rates</header>
									<fieldset>
										<div class="row">
											<div class="col-sm-6"><section><label for="baseRateList-tenure" class="label">Tenure:</label></section></div>
											<div class="col-sm-6">
												<section class="input">
												<input type="text" id="baseRateList-tenure" placeholder="Tenure">
												<b class="tooltip tooltip-top-right"></b></section>
												<section class="view"></section>
											</div>
										</div>
										<div class="row">
											<div class="col-sm-6"><section><label for="baseRateList-baseRate" class="label">Base Rate:</label></section></div>
											<div class="col-sm-6">
												<section class="input">
												<input type="text" id="baseRateList-baseRate" placeholder="Base Rate">
												<b class="tooltip tooltip-top-right"></b></section>
												<section class="view"></section>
											</div>
										</div>
									</fieldset>
								<footer>
									<div class="btn-group pull-right">
										<button type="button" class="btn btn-sm btn-primary" id="baseRateList-btnSave"><span class="fa fa-save"></span></button>
										<button type="button" class="btn btn-sm btn-close" id="baseRateList-btnClose"><span class="fa fa-close"></span></button>
									</div>
								</footer>
							</div>
							</div></div></div></div></div>
						</fieldset>
					</div>
				</div>
				</div>
				</div>
<%
}
%>


    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-info btn-lg btn-enter" id="btnSaveLimit"><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
							<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
    		</div>
    		</div>
    	</div>
    	</div></div></div></div>
    	<!-- frmMain -->
	</div>

   	<%@include file="footer1.jsp" %>
   	<script id="tplRates" type="text/x-handlebars-template">
	<table class="table">
	<thead><tr><th>Tenure</th><th>Rate %</th></tr></thead>
	<tbody>
{{#each this}}
	<tr><td>{{tenure}}</td><td>{{#formatDec}}{{baseRate}}{{/formatDec}}</td></tr>
{{/each}}
	</tbody>
	</table>
	</script>
   	<script id="tplEffRates" type="text/x-handlebars-template">
	<table class="table">
	<thead><tr><th>Tenure</th><th>Range %</th></tr></thead>
	<tbody>
{{#each this}}
	<tr><td>{{day}}</td><td>{{#formatDec}}{{min}}{{/formatDec}} - {{#formatDec}}{{max}}{{/formatDec}}</td></tr>
{{/each}}
	</tbody>
	</table>
	</script>
	   	
   	<script src="../js/bootstrap-datetimepicker.js"></script>
   	<script src="../js/datatables.js"></script>
   	<script src="../js/jquery.autocomplete.js"></script>
	<script type="text/javascript">
	var crudFinancierAuctionSetting$ = null, crudFinancierAuctionSetting, mainForm, searchForm;
	var tabIdx=-1,tabData=null;
	var tplRates,tplEffRates;
	var lastSearchFilter=null;
	var lPurchCostLeg = null;
	var lDispMsg = false;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(FinancierAuctionSettingBean.class).getJsonConfig()%>;
		
		<%
		if (lPurchaser&&lSupplier) {
		%>
			$.each(lFormConfig.fields, function(pIndex,pValue){
				if (pValue.name=="purchaser") {
					pValue.dataSetValues="finaucset/purchasers";
				}else if (pValue.name=="supplier") {
					pValue.dataSetValues="";
				}
			});
		<%
		}
		%>
		
		var lConfig = {
				resource: "finaucset",
				autoRefresh: true,
				newDefault:{currency:'INR'},
				preSearchHandler: function(pData) {
					pData.level="<%=lLevel.getCode()%>";
					return true;
				},
				postSearchHandler: function(pObj) {
					tabData = [];
					$.each(pObj,function(pIdx,pValue){
						var lIdx = 0;
						if (pValue.approvalStatus=='<%=FinancierAuctionSettingBean.ApprovalStatus.Submitted%>') {
							if (pValue.checkerFlag=='Y') lIdx = 2;
							else lIdx = 1;
						} else if (pValue.approvalStatus=='<%=FinancierAuctionSettingBean.ApprovalStatus.Approved%>')
							lIdx = 3;
						var lData=tabData[lIdx];
						if (lData==null) {
							lData=[];
							tabData[lIdx]=lData;
						}
						lData.push(pValue);
					});
					var lIdx;
					var lTabIdx=-1;
					$('#frmSearch .nav-tabs li a').each(function (index, element) {
						lIdx = element.attributes['href'].value.substring(4);
						if (tabData[lIdx]==null) {
							if (lIdx==tabIdx) tabIdx=-1;
							$(element).parent('li').hide();
						} else {
							if (lTabIdx<0) lTabIdx=lIdx;
							$(element).parent('li').show();
							var lCount = tabData[lIdx]?tabData[lIdx].length:0;
							$('#badge'+lIdx).html(lCount<10?"0"+lCount:lCount);
						}
					});
					if (tabIdx >= 0)
						showData();
					else {
						tabIdx=lTabIdx;
						if (tabIdx>=0)
							$('#frmSearch .nav-tabs li:eq('+tabIdx+') a').tab('show');
						else
							showData();
					}
					return false;
				},
				preSaveHandler: function(pData) {
					pData.level="<%=lLevel.getCode()%>";
					pData.financier=loginData.domain;
					lDispMsg = (lPurchCostLeg!=pData.purchaserCostLeg);
					//oldAlert(lPurchCostLeg + " : " + pData.purchaserCostLeg + " : "+ lDispMsg);
					return true;
				},
				postSaveHandler: function(pData) {
					<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer) {
					%>
						if(lDispMsg){
							alert("Saved Successfully. <br> NOTE : In order to apply the cost leg change, please remove and add the factoring unit to the watch list.", "Alert!", function() {
				 				crudFinancierAuctionSetting.showSearchForm();
							});
							return false;
						}
					<%    
					}
					%>
					return true;
				},
				postNewHandler: function() {
					if (<%=lUser%>) {
						mainForm.enableDisableField('bidLimit',false,false);
					}
<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer_Seller) {
%>						mainForm.enableDisableField('purchaserRef',false,false);
<%
					}
%>
					<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer) {
					%>
					checkLocationEnable(loginData.domain);					
					<%
					}
					%>
					
					return true;
				},
				postModifyHandler: function(pObj) {
					lPurchCostLeg = pObj.purchaserCostLeg;
					if (<%=lUser%>) {
						mainForm.enableDisableField('bidLimit',false,false);
					}
					<%
					if (lPurchaser&&lSupplier) {
					%>
					populateSupplier(mainForm,pObj.supplier);
					<%
					}
					%>
					<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_User) {
					%>
					onChangeRateRangeType();
					<%
					}
					%>
					<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer) {
					%>
					var lLocEnabled = pObj.isLocationEnabled!=null && pObj.isLocationEnabled=='<%=CommonAppConstants.Yes.Yes.getCode()%>';
					setFinancerLocation(lLocEnabled,pObj.financier, pObj.finClId);
					<%
					}
					%>
					<%
					if (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer_Seller) {
%>						mainForm.enableDisableField('purchaserRef',false,false);
						setPurchaserRef();
<%
					}
%>
					return true;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);
		
		var lPlatform = (loginData.domain == "<%=AppConstants.DOMAIN_PLATFORM%>");
		if(lPlatform){
			$('#frmSearch #finName').attr('data-sel-exclude',false);
			$('#frmSearch #finName').attr('data-visible',true);
		}
		
		tplRates = Handlebars.compile($('#tplRates').html());
		tplEffRates = Handlebars.compile($('#tplEffRates').html());
		
		crudFinancierAuctionSetting$ = $('#contFinancierAuctionSetting').xcrudwrapper(lConfig);
		crudFinancierAuctionSetting = crudFinancierAuctionSetting$.data('xcrudwrapper');
		mainForm = crudFinancierAuctionSetting$.data('xcrudwrapper').options.mainForm;
		searchForm = crudFinancierAuctionSetting$.data('xcrudwrapper').options.searchForm;

<%
if (lPurchaser) {
%>
		mainForm.alterField('purchaser',true,false);
		mainForm.alterField('minSpread',true,false);
		mainForm.alterField('maxSpread',true,false);
<%
}
if (lSupplier) {
%>

		mainForm.alterField('supplier',true,false);
<%
}
if (lUser) {
%>
		mainForm.alterField('auId',true,false);
		mainForm.enableDisableField('bidLimit', true, false); 


		$('#limit1').on('change', function() {
			mainForm.getField('bidLimit').setValue(mainForm.getField('limit').getValue());
		});	
<%
}
if ((lLevel == FinancierAuctionSettingBean.Level.Financier_Self) || (lLevel == FinancierAuctionSettingBean.Level.Financier_Buyer)) {
%>		mainForm.alterField('purchaserCostLeg',true,false);
<%
}
%>
	$('#limit').on('change', function() {
	<% if (lUser){
		%>
		mainForm.getField('bidLimit').setValue(mainForm.getField('limit').getValue());
		<%
		}
		%>
		
});	

$('#btnSaveLimit').on('click', function() {
	var lLimit = mainForm.getField('limit').getValue();
	var lBidLimit = mainForm.getField('bidLimit').getValue();
	if (!isNaN(lLimit) && !isNaN(lBidLimit) && (Number(lBidLimit) < Number(lLimit))) {
		confirm('Warning : The Bid Limit set is less than the total limit. Do you want to save?','Confirmation','Yes',function(pYes) {
			if (pYes)
				crudFinancierAuctionSetting.saveHandler();
		});
		return;
	}
	crudFinancierAuctionSetting.saveHandler();
});

$('#frmMain #purchaser').on('change', function() {
	<%
	if (lPurchaser&&lSupplier) {
	%>
		populateSupplier(mainForm);
	<%
	}
	%>
});

$('#frmSearch #purchaser').on('change', function() {
	<%
	if (lPurchaser&&lSupplier) {
	%>
		populateSupplier(searchForm);
	<%
	}
	%>
});


$('input[type=radio][name=rateRangeType]').change(function() {
	onChangeRateRangeType();
});

$('#btnUpload').on('click', function() {
	showRemote('upload?url=finaucset-<%=lLevel.getCode()%>&level=<%=lLevel.getCode()%>', null, false);
});

$('#frmSearch .nav-tabs a').on('shown.bs.tab', function(event){
    var lRef1 = $(event.target).attr('href');         // active tab
    var lRef2 = $(event.relatedTarget).attr('href');  // previous tab
    tabIdx = parseInt(lRef1.substring(4));
    if (lRef2)
    	$('.btn-group'+lRef2.substring(4)).addClass('hidden');
    $('.btn-group'+tabIdx).removeClass('hidden');
    showData();
});

$('#btnShowTotalLimit').on('click', function() {
	var lEntity= searchForm.getField("purchaser").getValue();
	if(lEntity==null){
		alert("Please select a Buyer for displaying limits.", "Overall buyer limits");
		return;
	}
	$.ajax( {
        url: "finaucset/utilization/"+lEntity,
        type: "GET",
        data:null,
        success: function( pObj, pStatus, pXhr) {
        	if(pObj!=null && pObj.finCount!=null){
        		var lTable = "<table class='table table-bordered' style='width:100%'><tbody>";
        		lTable += "<tr><td><b> Purchaser </b></td><td colspan=2>" + pObj.purchaser + "</td></tr>";
        		lTable += "<tr><td><b> Financier Count </b></td><td colspan=2>" + pObj.finCount + "</td></tr>";
        		lTable += "<tr><td><b> Limit </b></td><td colspan=2>" + handleBarDecFormatter.formatNumber(pObj.limit) + "</td></tr>";
        		lTable += "<tr><td><b> Limit Utilised </b></td><td>" + handleBarDecFormatter.formatNumber(pObj.utilised) + "</td><td>" + handleBarDecFormatter.formatNumber(pObj.utilisedPerc) + " % </td></tr>";
        		lTable += "<tr><td><b> Limit UnUtilised </b></td><td>" + handleBarDecFormatter.formatNumber(pObj.unUtilised) + "</td><td>" + handleBarDecFormatter.formatNumber(pObj.unUtilisedPerc) + " % </td></tr>";
        		lTable += "</body></table>";
        		alert(lTable,"Overall buyer limits");
        	}else{
        		alert("No Data found.", "Overall buyer limits");
        	}
        },
    	error: errorHandler
    });
});


$('#btnFinaucset').on('click',function(){
	showModal($('#mdlFinaucset'));
});


});
	
	function displayWarning(limit,bidLimit){
		return false;
	}
	function populateSupplier(pForm, pVal) {
		var lEntity=pForm.getField("purchaser").getValue();
		var lField=pForm.getField("supplier");
		var lOptions=lField.getOptions()
		if (!lEntity) lOptions.dataSetValues=[];
		else lOptions.dataSetValues = "pursuplnk/supplier/"+lEntity;
		lField.init();
		if (pVal)
			lField.setValue(pVal);
	}
	function showData() {
		var lDataTable=crudFinancierAuctionSetting.options.dataTable;
		lDataTable.rows().clear();
		if ((tabIdx>=0) && tabData && (tabData[tabIdx] != null))
			lDataTable.rows.add(tabData[tabIdx]).draw();
		else
			lDataTable.rows.add([]).draw();
	}
	function updateAppStatus(pStatus, pDesc) {
		var lSelected = crudFinancierAuctionSetting.getSelectedRow();
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		prompt("You are about to " + pDesc + " selected record.<br>Please enter suitable reason/remarks",pDesc,function(pReason){
			var lData = {id:crudFinancierAuctionSetting.selectedRowKey(lSelected.data()),approvalStatus: pStatus,approvalRemarks : pReason, level:"<%=lLevel.getCode()%>"};
			$.ajax( {
	            url: "finaucset/status",
	            type: "POST",
	            data:JSON.stringify(lData),
	            success: function( pObj, pStatus, pXhr) {
            		alert("Status updated successfully", "Information", function() {
            			crudFinancierAuctionSetting.showSearchForm();
            		});
	            },
	        	error: errorHandler
	        });					
		});
	}
	function setPurchaserRef() {
		var lPurchRef = mainForm.getField('purchaserRef').getValue();
		var lPurch = $("#contFinancierAuctionSetting #frmMain #purchaser option:selected").text();
		var lPos = lPurch.indexOf('-');
		if (lPos > 0)
			mainForm.getField('purchaserRef').setValue(lPurch.substring(0,lPos));
	}
	function setSupplierRef() {
		var lSuppRef = mainForm.getField('supplierRef').getValue();
		var lSupp = $("#contFinancierAuctionSetting #frmMain #supplier option:selected").text();
		var lPos = lSupp.indexOf('-');
		if (lPos > 0)
			mainForm.getField('supplierRef').setValue(lSupp.substring(0,lPos));
	}
	function onChangeRateRangeType() {
		var lAbsolute=mainForm.getField('rateRangeType').getValue()=='<%=FinancierAuctionSettingBean.RateRangeType.Absolute.getCode()%>';
		mainForm.enableDisableField(['minBidRate','maxBidRate'],lAbsolute,!lAbsolute);
		mainForm.enableDisableField(['minSpread','maxSpread'],!lAbsolute,lAbsolute);
	}
	function viewLendingRates() {
		var lSelected = crudFinancierAuctionSetting.getSelectedRow();
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		alert(tplRates(lSelected.data().baseRateList),'Lending Rates');
	}
	function viewEffRates() {
		var lSelected = crudFinancierAuctionSetting.getSelectedRow();
		
		if ((lSelected==null)||(lSelected.length==0)) {
			alert("Please select a row");
			return;
		}
		var lData=lSelected.data().effRateRange.replace(/&quot;/g, '\"');// un encode 
		alert(tplEffRates(JSON.parse(lData)),'Effective Rates');
	}
	function downloadCsv() {
		var lFilter = {columnNames: crudFinancierAuctionSetting.getVisibleColumns(),tab:tabIdx,level:"<%=lLevel.getCode()%>"};
		downloadFile('finaucset/all',null,JSON.stringify(lFilter));
	}
	function populateLoc(pEntity, pLoc) {
		var lField=mainForm.getField("finClId");
		var lOptions=lField.getOptions()
		//var	lActiveOnly=true; 
		if (!pEntity) lOptions.dataSetValues=[];
		else lOptions.dataSetValues = "companylocation/settleactivelov?aecode="+pEntity; //+"&activeOnly="+lActiveOnly;
		lField.init();
		if (pLoc) lField.setValue(pLoc);
		//oldAlert(lField.size());
	}
	function setFinancerLocation(pEnabled,pEntity,pClId){
		mainForm.enableDisableField('finClId',pEnabled,pEnabled);
		if(pEnabled){
			$('#locationRow').show();
			populateLoc(pEntity, pClId);
		}else{
				$('#locationRow').hide();
		}
	}
	function checkLocationEnable(pCode){
		$.ajax( {
	        url: "finaucset/islocationenabled/"+pCode,
	        type: "GET",
	        success: function( pObj, pStatus, pXhr) {
	        	setFinancerLocation(pObj,pCode,null);
	        },
	    	error: errorHandler
	    });
	}
	</script>
   	
    </body>
</html>

