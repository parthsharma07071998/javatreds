<%@page import="com.xlx.treds.entity.bean.CompanyShareIndividualBean"%>
<%@page import="com.xlx.treds.entity.bo.CompanyShareIndividualBO"%>
<%@page import="com.xlx.treds.entity.bean.CompanyShareEntityBean"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.entity.bean.CompanyBankDetailBean"%>
<%@page import="com.xlx.treds.entity.bean.CompanyBankDetailBean.BankingType"%>
<%@page import="com.xlx.treds.entity.bean.CompanyLocationBean"%>
<%@page import="com.xlx.treds.entity.bean.CompanyContactBean"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.treds.entity.bean.RegistrationWrapperBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<html>
<%
String lContextPath=request.getContextPath();
if(request == null) 
	System.out.println("request is null");
HashMap<String, Object> lAttributes = (HashMap<String, Object>) request.getAttribute("Attribs");
RegistrationWrapperBean lRegistrationWrapperBean = null;
String lTemp = null;
if(lAttributes!=null)
	lRegistrationWrapperBean = (RegistrationWrapperBean)lAttributes.get(RegistrationWrapperBean.ENTITY_NAME);

int lSrNo=1, lAppx1SrNo=1, lAppx2SrNo=1;
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> </meta>
<link rel="stylesheet" type="text/css" href="<%=lContextPath%>/css/reportPDF.css" media="print"></link>
<link rel="stylesheet" type="text/css" href="<%=lContextPath%>/css/reportView.css" media="screen"></link>
<title>Insert title here</title>

</head>
<body>
<table class="myTblPageHead" border="0" cellspacing="0" cellpadding="0" width="100%">
	<thead class="myTheadPageHead">
        <tr>
            <th width="100%" valign="top" height="30px">
                   Application for Registration
            </th>
        </tr>
	</thead>
	<tbody>
<table class="myTblGeneral" border="0" cellspacing="0" cellpadding="0" width="100%">
	<thead>
        <tr>
            <th class="myTheadGeneral" width="100%" colspan="6" valign="top" >
                    GENERAL INFORMATIONS &amp; CONTACT DETAILS
            </th>
        </tr>
	</thead>
    <tbody>
        <tr>
            <td class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Name of the applicant entity
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCompanyName())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Applicant Type
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getApplicantTypeDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Constitution
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getConstitutionDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td  class="myTdLabel" width="40%" valign="top">
                    Financier Category
            </td>
            <td  class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getFinancierCategoryDesc())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                   RBI Registration Number &amp; Date of issue
            </td>
            <td  class="myTdLabel" width="8%" valign="top">
                    Reg No.
            </td>
            <td class="myTdValue" width="18%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getFinCertificateNo())%>
            </td>
            <td  class="myTdLabel" width="9%" valign="top">
                    Date
            </td>
            <td class="myTdValue" width="18%" valign="top">
            	<%=TredsHelper.getInstance().getFormattedDate(lRegistrationWrapperBean.getCompanyDetails().getFinCertificateIssueDate())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo" width="5%" rowspan="16" valign="top">
                    <%=lSrNo++%>
            </td>
            <td  class="myTdLabel" width="40%" valign="top">
                    Address &amp; contact details
            </td>
            <td class="myTdLabel" width="26%" colspan="2" valign="top">
                    Registered Address
            </td>
            <td class="myTdLabel" width="27%" colspan="2" valign="top">
                    Correspondence Address
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Line -1
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegLine1())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorLine1())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Line -2
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegLine2())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorLine2())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Line -3
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegLine3())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorLine3())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="40%" valign="top">
                    Zip Code
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegZipCode())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorZipCode())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    City
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegCity())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorCity())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    District
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegDistrict())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCorDistrict())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    State
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegStateDesc())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCorStateDesc())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Country
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegCountryDesc())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCorCountryDesc())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Name of the Contact Person
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegName())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCorName())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Telephone No.
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegTelephone())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorTelephone())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Mobile Number
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegMobile())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorMobile())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Fax No.
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegFax())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorFax())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Email Address
            </td>
            <td  class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegEmail())%>
            </td>
            <td  class="myTdValue" width="27%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCorEmail())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    GSTN
            </td>
            <td class="myTdValue" width="26%" colspan="2" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegOfficeGSTN())%>
            </td>
            <td class="myTdValue" width="27%" colspan="2" valign="top">
            	
            </td>
        </tr>
        <tr>
            <td class="myTdLabel" width="40%" valign="top">
                    Website (if any)
            </td>
            <td class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getRegWebsite())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Date of Establishment / Incorporation
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getFormattedDate(lRegistrationWrapperBean.getCompanyDetails().getDateOfIncorporation())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Years in Business
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getExistenceYearsDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    CIN Number
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCinNo())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Industry Category
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getIndustryDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Industry Sub-segment
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getSubSegmentDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Sector (Manufacturing/Service)
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getSectorDesc())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Export Orientation
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getExportOrientationDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Currency of business
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCurrencyDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    Brief description of the activity
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCompanyDesc())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td  class="myTdLabel" width="40%" valign="top">
                    Permanent Account Number (PAN)
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getPan())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo" width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td  class="myTdLabel" width="40%" valign="top">
                    Registered State GSTN
            </td>
            <td  class="myTdValue" width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getRegOfficeGSTN())%>
            </td>
        </tr>
    </tbody>
</table>
<table  class="myTblGeneral" border="0" cellspacing="0" cellpadding="0" width="100%">
	<thead>
        <tr>
            <th  class="myTheadGeneral" width="100%" colspan="6" valign="top">
                    MSME STATUS DETAILS
            </th>
        </tr>
	</thead>
    <tbody>
        <tr>
            <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel" width="40%" valign="top">
                    MSME Registration Type
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getMsmeRegTypeDesc())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    MSME Status (Micro/Small/Medium)
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getMsmeStatus())%>
            </td>
        </tr>
        <tr>
            <td  class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Registration Number &amp; Date
            </td>
            <td class="myTdLabel"  width="8%" valign="top">
                    Reg No.
            </td>
            <td class="myTdValue"  width="22%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getMsmeRegNo())%>
            </td>
            <td class="myTdLabel"  width="8%" valign="top">
                    Date
            </td>
            <td class="myTdValue"  width="15%" valign="top">
             	<%=TredsHelper.getInstance().getFormattedDate(lRegistrationWrapperBean.getCompanyDetails().getMsmeRegDate())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo"  width="5%" rowspan="4" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Name of the CA certificate issuing Firm
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCaName())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="40%" valign="top">
                    Membership Number of the CA Firm
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCaMemNo())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="40%" valign="top">
                    Date of CA Certificate
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getFormattedDate(lRegistrationWrapperBean.getCompanyDetails().getCaCertDate())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="40%" valign="top">
                    Investment in Core Plant &amp; Machinery (As per CA certificate)
            </td>
            <td class="myTdLabel"  width="8%" valign="top">
                    ` Lakh
            </td>
            <td class="myTdValue"  width="22%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getInvtCPM())%>
            </td>
            <td class="myTdLabel"  width="8%" valign="top">
                    As on (date)
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getFormattedDate(lRegistrationWrapperBean.getCompanyDetails().getInvtDateCPM())%>
            </td>
        </tr>
    </tbody>
</table>
<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
	<thead>
        <tr>
            <th class="myTheadGeneral"  width="100%" colspan="6" valign="top">
                    OTHER INFORMATIONS
            </th>
        </tr>
	</thead>
    <tbody>
        <tr>
            <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Annual MSME Purchases (` lakh)
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getAnnualMsmePurchase())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo"  width="5%" rowspan="2" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Sales (Turnover) as per last Audited Account
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getSalesTo())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="40%" valign="top">
                    Financial Year for above sales figure
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getSalesYear())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo"  width="5%" rowspan="3" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" rowspan="3" valign="top">
                    Names of the Top 3 Customers
            </td>
            <td class="myTdLabel"  width="4%" valign="top">
                    1
            </td>
            <td class="myTdValue"  width="25%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer1())%>
            </td>
            <td class="myTdLabel"  width="8%" rowspan="3" valign="top">
                    City
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer1City())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="4%" valign="top">
                    2
            </td>
            <td class="myTdValue"  width="25%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer2())%>
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer2City())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="4%" valign="top">
                    3
            </td>
            <td class="myTdValue"  width="25%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer3())%>
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer3City())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="4%" valign="top">
                    4
            </td>
            <td class="myTdValue"  width="25%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer4())%>
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer4City())%>
            </td>
        </tr>
        <tr>
            <td class="myTdLabel"  width="4%" valign="top">
                    5
            </td>
            <td class="myTdValue"  width="25%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer5())%>
            </td>
            <td class="myTdValue"  width="15%" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCustomer5City())%>
            </td>
        </tr>
        <tr>
            <td class="myTdSrNo"  width="5%" rowspan="3" valign="top">
                    <%=lSrNo++%>
            </td>
            <td class="myTdLabel"  width="40%" valign="top">
                    Cash Discount %
            </td>
            <td class="myTdValue"  width="54%" colspan="4" valign="top">
            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCompanyDetails().getCashDiscountPercent())%>
            </td>
        </tr>
    </tbody>
</table>
<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr>
            <th  class="myTheadGeneral" width="100%" colspan="4" valign="top">
                    Share Holders 
            </th>
        </tr>
    </thead>
    <tbody>
    <%
    	if(lRegistrationWrapperBean.showShareEntity())
    	{
    %>
    <tr>
         <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
         </td>
         <td width="95%" >
         <%
         	CompanyShareEntityBean lCSEBean1 = null, lCSEBean2 = null;
        	int lCount = lRegistrationWrapperBean.getCompanyShareEntities().size();
            for(int lPtr=0; lPtr < lCount; lPtr+=2)
            {
            	lCSEBean1 = lRegistrationWrapperBean.getCompanyShareEntities().get(lPtr);                	
            	lCSEBean2 = lRegistrationWrapperBean.getCompanyShareEntities().get(lPtr+1);                	
         %>
         	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th class="myTheadGeneral"  width="40%" valign="top">
			                    Entity
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    Entity-<%=lPtr+1%>
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    Entity-<%=lPtr+2%>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Name of the Entity
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getCompanyName())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getCompanyName())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Benificiary Owner
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<!-- Get from company details bean -->
			            	<%=((lCSEBean1.getId()==null)?"":TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getConstitutionDesc()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<!-- Get from company details bean -->
			            	<%=((lCSEBean2.getId()==null)?"":TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getConstitutionDesc()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Constitution
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntityConstitutionDesc(lCSEBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntityConstitutionDesc(lCSEBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Registration No
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getRegNo())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getRegNo())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getPan())%>
			            </td>
			         </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Date of Incorporation
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCSEBean1.getDateOfIncorporation()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCSEBean2.getDateOfIncorporation()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel" width="40%" valign="top">
			                    Industry Category
			            </td>
			            <td  class="myTdValue" width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntityIndustryDesc(lCSEBean1))%>
			            </td>
			            <td  class="myTdValue" width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntityIndustryDesc(lCSEBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel" width="40%" valign="top">
			                    Industry Sub-segment
			            </td>
			             <td  class="myTdValue" width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntitySubSegmentDesc(lCSEBean1))%>
			            </td>
			            <td  class="myTdValue" width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getEntitySubSegmentDesc(lCSEBean2))%>
			            </td>
			        </tr>
			         <tr>
			            <td class="myTdLabel" width="40%" valign="top">
			                    Brief description of the activity
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getCompanyDesc())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getCompanyDesc())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Name of the Promoter
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareEntityFullName(lCSEBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareEntityFullName(lCSEBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                   KMP PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getKmpPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getKmpPan())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getTelephone())%>
			            </td>
			        </tr>
			         <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Fax Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getFax())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getFax())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getMobile())%>
			            </td>
			        </tr> 
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean1.getDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSEBean2.getDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCSEBean1.getState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCSEBean2.getState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCSEBean1.getCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCSEBean2.getCountry()))%>
			            </td>
			        </tr>	        
			    </tbody>
			</table>
         	<%
                } //for each Share Entity
         	%>
         </td>
    </tr>
    <%
    	} //Share Entity
   	%>
   	<%
    	if(lRegistrationWrapperBean.showShareIndivudals())
    	{
    %>
 	<tr> 
         <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
         </td>
         <td width="95%" >
         <%
         	CompanyShareIndividualBean lCSIBean1 = null, lCSIBean2 = null;
        	int lCount = lRegistrationWrapperBean.getCompanyShareIndividuals().size();
            for(int lPtr=0; lPtr < 2; lPtr+=2)
            {
            	lCSIBean1 = lRegistrationWrapperBean.getCompanyShareIndividuals().get(lPtr);                	
            	lCSIBean2 = lRegistrationWrapperBean.getCompanyShareIndividuals().get(lPtr+1);                
         %>
         	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th class="myTheadGeneral"  width="40%" valign="top">
			                    Individual
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    Individual-<%=lPtr+1%>
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    Individual-<%=lPtr+2%>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Name of the Promoter
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareIndividualFullName(lCSIBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareIndividualFullName(lCSIBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean2.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean2.getTelephone())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean1.getPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean2.getPan())%>
			            </td>
			         </tr>
			         <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Fax Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean1.getFax())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean2.getFax())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCSIBean2.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Father's/Spouse Name
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareIndividualFamilyFullName(lCSIBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareIndividualFamilyFullName(lCSIBean2))%>
			            </td>
			        </tr>         
			    </tbody>
			</table>
         	<%
                } //for each Share Individual
         	%>
         </td>
    </tr>
    <%
    	} //Share Individual
   	%>
    
    </tbody>
</table>



<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr>
            <th  class="myTheadGeneral" width="100%" colspan="4" valign="top">
                    MANAGEMENT 
            </th>
        </tr>
    </thead>
    <tbody>
    <%
    	if(lRegistrationWrapperBean.showChiefPromoters())
    	{
    %>
    <tr>
         <td class="myTdSrNo"  width="5%" valign="top">
                    <%=lSrNo++%>
         </td>
         <td width="95%" >
         <%
         	CompanyContactBean lCCBean1 = null, lCCBean2 = null;
        	int lCount = lRegistrationWrapperBean.getChiefPromoters().size();
            for(int lPtr=0; lPtr < lCount; lPtr+=2)
            {
            	lCCBean1 = lRegistrationWrapperBean.getChiefPromoters().get(lPtr);                	
               	lCCBean2 = lRegistrationWrapperBean.getChiefPromoters().get(lPtr+1);                	
         %>
         	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th class="myTheadGeneral"  width="40%" valign="top">
			                    CHIEF PROMOTERS
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    CHIEF PROMOTER-<%=lPtr+1%>
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    CHIEF PROMOTER-<%=lPtr+2%>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Name of the Promoter
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Designation
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean1.getDesignation()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean2.getDesignation()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Date of Birth
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getDOB()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean2.getDOB()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Father's/Spouse Name
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCercaiFullName(lCCBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCercaiFullName(lCCBean2))%>
			            </td>
			        </tr>
			        			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Gender
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getGender() == null ? null : lCCBean1.getGender().toString())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
					<%=TredsHelper.getInstance().getCleanData(lCCBean2.getGender() == null ? null : lCCBean2.getGender().toString())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getPan())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    UID No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getUidId())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getUidId())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getTelephone())%>
			            </td>
			        </tr>
			         <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Fax Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getFax())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getFax())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Category
			                    (General/SC/ST/Minority)
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCPCategory(lCCBean1.getCpCat()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCPCategory(lCCBean2.getCpCat()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Women Entrepreneur
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean1.getCpWomenEnt()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean2.getCpWomenEnt()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Whether authorized for CERSAI registration
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean1.getCersaiFlag()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean2.getCersaiFlag()))%>
			            </td>
			        </tr>
			    	<tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getResState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getResState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getResCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getResCountry()))%>
			            </td>
			        </tr>
							         <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getNriState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getNriState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getNriCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getNriCountry()))%>
			            </td>
			        </tr>
			    </tbody>
			</table>
         	<%
                } //for each chief promoter
         	%>
         </td>
    </tr>
    <%
    	} //Chief Promoters

    	if(lRegistrationWrapperBean.showOtherPromoters())
    	{
    %>
    <tr>
        <td class="myTdSrNo" width="5%" valign="top">
				<%=lSrNo++%>
        </td>
        <td width="95%">
         <%
         	CompanyContactBean lCCBean1 = null, lCCBean2 = null;
        	int lCount = lRegistrationWrapperBean.getOtherPromoters().size();
            for(int lPtr=0; lPtr < lCount; lPtr+=2)
            {
            	lCCBean1 = lRegistrationWrapperBean.getOtherPromoters().get(lPtr);                	
               	lCCBean2 = lRegistrationWrapperBean.getOtherPromoters().get(lPtr+1);                	
         %>
	        <table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th  class="myTheadGeneral" width="40%" valign="top">
			                    OTHER PROMOTERS
			            </th>
			            <th  class="myTheadGeneral" width="30%" valign="top">
			                    PROMOTER-<%=lPtr+1%>
			            </th>
			            <th  class="myTheadGeneral" width="30%" valign="top">
			                    PROMOTER-<%=lPtr+2%>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Name of the Promoter
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Designation
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean1.getDesignation()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean2.getDesignation()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Date of Birth
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getDOB()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean2.getDOB()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Father's/Spouse Name
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCercaiFullName(lCCBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCercaiFullName(lCCBean2))%>
			            </td>
			        </tr>
			        			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Gender
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getGender() == null ? null : lCCBean1.getGender().toString())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
							<%=TredsHelper.getInstance().getCleanData(lCCBean2.getGender() == null ? null : lCCBean2.getGender().toString())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getPan())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    UID No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getUidId())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getUidId())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getTelephone())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Whether authorized for CERSAI registration
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean1.getCersaiFlag()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lCCBean2.getCersaiFlag()))%>
			            </td>
			        </tr>
			        		         <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getResState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getResState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getResCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getResCountry()))%>
			            </td>
			        </tr>
							         <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getNriState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getNriState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getNriCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getNriCountry()))%>
			            </td>
			        </tr>
			    </tbody>
			</table>
         	<%
                } //for each other promoters
         	%>
        </td>
    </tr>
    <%
    	} //Other Promoters
    	//
    	if(lRegistrationWrapperBean.showAuthOfficials())
    	{
    %>
    <tr>
        <td class="myTdSrNo"  width="5%"  valign="top">
				<%=lSrNo++%>
        </td>
        <td width="95%">
        <%
         	CompanyContactBean lCCBean1 = null, lCCBean2 = null;
        	int lCount = lRegistrationWrapperBean.getAuthOfficials().size();
            for(int lPtr=0; lPtr < lCount; lPtr+=2)
            {
            	lCCBean1 = lRegistrationWrapperBean.getAuthOfficials().get(lPtr);                	
               	lCCBean2 = lRegistrationWrapperBean.getAuthOfficials().get(lPtr+1);                	
         %>
        	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th class="myTheadGeneral"  width="40%" valign="top">
			                    AUTHORIZED OFFICIAL
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    OFFICIAL-1
			            </th>
			            <th class="myTheadGeneral"  width="30%" valign="top">
			                    OFFICIAL-2
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Name of the authorized official
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Designation
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean1.getDesignation()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean2.getDesignation()))%>
			            </td>
			        </tr>
			         <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Date of Birth
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getDOB()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean2.getDOB()))%>
			            </td>
			        </tr>
			        			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    Gender
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getGender() == null ? null : lCCBean1.getGender().toString())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
						<%=TredsHelper.getInstance().getCleanData(lCCBean2.getGender() == null ? null : lCCBean2.getGender().toString())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getPan())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getPan())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="43%" valign="top">
			                    UID No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getUidId())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getUidId())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getTelephone())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Authorization (Board Resolution) Date
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getAuthPerAuthDate()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean2.getAuthPerAuthDate()))%>
			            </td>
			        </tr>
			    	<tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getResDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getResState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getResState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getResCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getResCountry()))%>
			            </td>
			        </tr>
							         <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean2.getNriDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getNriState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean2.getNriState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getNriCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean2.getNriCountry()))%>
			            </td>
			        </tr>
			    </tbody>
			</table>
         	<%
                } //for each auth official 
         	%>
        </td>
    </tr>
    <%
    	} //Auth Officials
    	if(lRegistrationWrapperBean.showAdmin())
    	{
    %>
    <tr>
        <td class="myTdSrNo"  width="5%"  valign="top">
				<%=lSrNo++%>
        </td>
        <td width="95%">
         <%
         	CompanyContactBean lCCBean1 = lRegistrationWrapperBean.getAdmin();
         %>
        	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
			        <tr>
			            <th  class="myTheadGeneral"  width="95%" colspan="3" valign="top">
		                    ADMINISTRATOR
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Name of the Administrator
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Designation
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getDesignationDesc(lCCBean1.getDesignation()))%>
			            </td>
			        </tr>
			         <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Date of Birth
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getDOB()))%>
			            </td>
			        </tr>
				<tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Gender
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getGender() == null ? null : lCCBean1.getGender().toString())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    PAN No.
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getPan())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    UID No.
			            </td>
			            <td class="myTdValue"  width="27%"  colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getUidId())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    NOA Email Address
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNoaEmail())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Telephone Number
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getTelephone())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Fax Number
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getFax())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="41%" valign="top">
			                    Authorization (Board Resolution) Date
			            </td>
			            <td class="myTdValue"  width="54%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(TredsHelper.getInstance().getFormattedDate(lCCBean1.getAdminAuthDate()))%>
			       	    </td>
			        </tr>
				<tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Res Line -3
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  City
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  District
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getResDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  State
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getResState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                   Res  Country
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getResCountry()))%>
			            </td>
			        </tr>
							         <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    City
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    District
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCCBean1.getNriDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    State
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCCBean1.getNriState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Country
			            </td>
			            <td class="myTdValue"  width="27%" colspan="2" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCCBean1.getNriCountry()))%>
			            </td>
			        </tr>
			    </tbody>
			</table>
        </td>
    </tr>
    <%
    	} //Admin
    %>
    </tbody>
</table>

<%
	if(lRegistrationWrapperBean.showLocations())
	{
%>
<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr>
            <th  class="myTheadGeneral" width="100%" colspan="4" valign="top">
                    LOCATIONS/BRANCHES
            </th>
        </tr>
    </thead>
    <tbody>
    <tr>
         <td class="myTdSrNo"  width="5%" valign="top">
				<%=lSrNo++%>
         </td>
         <td width="95%">
	        <%
	         	CompanyLocationBean lCLBean1 = null, lCLBean2 = null;
	        	int lCount = lRegistrationWrapperBean.getLocationBranches().size();
	            for(int lPtr=0; lPtr < lCount; lPtr+=2)
	            {
	            	lCLBean1 = lRegistrationWrapperBean.getLocationBranches().get(lPtr);                	
	            	lCLBean2 = lRegistrationWrapperBean.getLocationBranches().get(lPtr+1);                	
	         %>
         	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
			    <thead>
				<tr>
				    <th class="myTheadGeneral"  width="40%" valign="top">
				    	LOCATION/BRANCH
				    </th>
				    <th class="myTheadGeneral"  width="30%" valign="top">
	                    LOCATION/BRANCH - 1
				    </th>
				    <th class="myTheadGeneral"  width="30%" valign="top">
	                    LOCATION/BRANCH - 2
				    </th>
				</tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td  class="myTdLabel" width="40%" valign="top">
			                    Location/Branch Name
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getName())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getName())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Goods And Services Tax (GST) Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getGstn())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getGstn())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Address Line -1
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getLine1())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getLine1())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line- 2
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getLine2())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getLine2())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Line -3
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getLine3())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getLine3())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Zip Code
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getZipCode())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getZipCode())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    City
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getCity())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getCity())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    District
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getDistrict())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getDistrict())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    State
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCLBean1.getState()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getStateGSTDesc(lCLBean2.getState()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Country
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCLBean1.getCountry()))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lCLBean2.getCountry()))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Name of the Contact Person
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getLocationContactFullName(lCLBean1))%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getLocationContactFullName(lCLBean2))%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Telephone No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getTelephone())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getTelephone())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Mobile Number
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getMobile())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getMobile())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Fax No.
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getFax())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getFax())%>
			            </td>
			        </tr>
			        <tr>
			            <td class="myTdLabel"  width="40%" valign="top">
			                    Email Address
			            </td>
			            <td class="myTdValue"  width="27%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean1.getEmail())%>
			            </td>
			            <td class="myTdValue"  width="26%" valign="top">
			            	<%=TredsHelper.getInstance().getCleanData(lCLBean2.getEmail())%>
			            </td>
			        </tr>
			    </tbody>
			</table>
         	<%
                } //for each location/branch 
         	%>
         </td>
    </tr>
    </tbody>
</table>
<%
  	} //Locations
  	if(lRegistrationWrapperBean.showBankingDetails())
  	{  	
%>
<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr>
            <td  class="myTheadGeneral" width="100%" colspan="4" valign="top">
                    BANKING DETAILS
            </td>
        </tr>
    </thead>
    <tbody>
    <tr>
         <td class="myTdSrNo"  width="5%" valign="top">
				<%=lSrNo++%>
         </td>
         <td width="95%">
  	        <%
	         	CompanyBankDetailBean lBDBean1 = null, lBDBean2 = null;
	        	int lCount = lRegistrationWrapperBean.getBankDetails().size();
	            for(int lPtr=0; lPtr < lCount; lPtr+=2)
	            {
	            	lBDBean1 = lRegistrationWrapperBean.getBankDetails().get(lPtr);                	
	            	lBDBean2 = lRegistrationWrapperBean.getBankDetails().get(lPtr+1);                	
	         %>
         	<table class="myTblGeneral"  border="0" cellspacing="0" cellpadding="0" width="100%">
		    <thead>
		    <tr>
	            <td width="40%" valign="top">
	                    Type of Banking Facility (Sole /Multiple /Consortium)
	            </td>
	            <td width="54%" colspan="2" valign="top">
	            </td>
		    </tr>
			<tr>
			    <th class="myTheadGeneral"  width="40%" valign="top">
			    BANK ACCOUNTS
			    </th>
			    <th class="myTheadGeneral"  width="30%" valign="top">
			    BANK ACCOUNT - 1
			    </th>
			    <th class="myTheadGeneral"  width="30%" valign="top">
			    BANK ACCOUNT - 2
			    </th>
			</tr>
		    </thead>
		    <tbody>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Name of the Bank
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankDesc(lBDBean1.getBank()))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankDesc(lBDBean2.getBank()))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Type of Account
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankAccountTypeDesc(lBDBean1.getAccType()))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankAccountTypeDesc(lBDBean2.getAccType()))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Whether Lead Bank (Under Consortium)
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lBDBean1.getLeadBank()))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lBDBean2.getLeadBank()))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Whether Designated Transaction Account (Yes/No)
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lBDBean1.getDefaultAccount()))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getYesNoDesc(lBDBean2.getDefaultAccount()))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Account Number
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getAccNo())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getAccNo())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    IFSC Code
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getIfsc())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getIfsc())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Branch Address Line-1
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getLine1())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getLine1())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Line-2
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getLine2())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getLine2())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Line -3
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getLine3())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getLine3())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Zip Code
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getZipCode())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getZipCode())%>
		            </td>
		        </tr>
		        <tr>
		            <td  class="myTdLabel" width="40%" valign="top">
		                    City
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getCity())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getCity())%>
		            </td>
		        </tr>
		        <tr>
		            <td  class="myTdLabel" width="40%" valign="top">
		                    District
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getDistrict())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getDistrict())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    State
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=lRegistrationWrapperBean.getStateDesc(lBDBean1.getState())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=lRegistrationWrapperBean.getStateDesc(lBDBean2.getState())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Country
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lBDBean1.getCountry()))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getCountryDesc(lBDBean2.getCountry()))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Name of the Relationship Manager
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankRealMgrContactFullName(lBDBean1))%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getBankRealMgrContactFullName(lBDBean2))%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Telephone No.
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getTelephone())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getTelephone())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Mobile Number
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getMobile())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getMobile())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Fax No.
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getFax())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getFax())%>
		            </td>
		        </tr>
		        <tr>
		            <td class="myTdLabel"  width="40%" valign="top">
		                    Email Address
		            </td>
		            <td class="myTdValue"  width="27%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean1.getEmail())%>
		            </td>
		            <td class="myTdValue"  width="26%" valign="top">
		            	<%=TredsHelper.getInstance().getCleanData(lBDBean2.getEmail())%>
		            </td>
		        </tr>
      		    </tbody>
			</table>
         	<%
                } //for each location/branch 
         	%>
         </td>
    </tr>
    </tbody>
</table>
<%
  	} //Banking Details
%>

<table class="myTblGeneral" border="0" cellspacing="0" cellpadding="0" width="100%">
    <tbody>
        <tr>
            <th class="myTheadGeneral"  width="95%" valign="top">
                    DECLARATION
            </th>
        </tr>
        <tr>
            <td class="myTdStaticText" colspan="2" valign="top">
                    I/We hereby certify that
            </td>
        </tr>
        <tr>
            <td class="myTdStaticText" colspan="2" valign="top">
                <ol class="myOlTag">
                    <li class="myIlTag" >
                        all information furnished by me/ us above in this Application &amp; Appendix/Annexure/Statements and other papers/ documents enclosed
                        are true and correct to the best of my/ our knowledge and belief;
                    </li>
                    <li class="myIlTag" >
                        there are no arrears of statutory dues and no government enquiries/ proceedings/ prosecution/ legal action are pending/ initiated
                        against the enterprise/ unit/ promoters/ directors/ partners/ proprietor;
                    </li>
                    <li class="myIlTag" >
                        I/ We also confirm that I/ none of the promoters or directors or partners have at any time declared themselves as insolvent
                    </li>
                    <li class="myIlTag" >
                        I/We have no objection to RXIL/its representatives making necessary enquiries/verifications (including in CIBIL or any other credit
                        information agencies data base) while considering my/our application for registration on TReDS platform/We undertake to furnish all
                        other information that may be required by RXIL in connection with my/our application for registration;
                    </li>
                </ol>
            </td>
        </tr>
    </tbody>
</table>

<p> </p>
<p> </p>

<table class="myTblSign" width="100%" border="0">
	<tr>
		<td class="myTdLabelSign">
		<b>Date</b>
		</td>
		<td class="myTdLabelSign">
		</td>
	</tr>
	<tr>
		<td class="myTdLabelSign">
		<b>Place</b>
		</td>
		<td class="myTdLabelSign" align="right" style="text-align: right;">
		<b>Authorized Signatory(s)</b>
		</td>
	</tr>
	<tr>
		<td class="myTdLabelSign">
		</td>
		<td class="myTdLabelSign" align="right" style="text-align: right;">
		 (Directors / Partners /Proprietor/Trustees/Karta)
		</td>
	</tr>
</table>
<br></br>
<br></br>

<table class="myTblKyc" border="0" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr>
            <th class="myTheadKyc" width="100%" colspan="2" valign="top">
                    LIST OF ACCEPTABLE ENCLOSURES/UPLOADS FOR INDIVIDUALS
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td class="myTdLabelKyc" width="50%" valign="top">
                    # Identity Proof
                    (Any one of the following)
            </td>
            <td class="myTdLabelKyc" width="50%" valign="top">
                    *Address Proof (Applicable for entity also)
                    (Any one of the following not older than 3 months)
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Passport
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Electricity Bill
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    PAN Card
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Telephone Bill
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Voter's Identity Card
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Postpaid Mobile Bill
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Driving License
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Piped Gas Bill
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Aadhaar Card
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Water Bill
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Identity card with Photograph issued by Central/State Government Departments, Statutory/Regulatory Authorities, Public Sector Undertakings,
                    Scheduled Commercial Banks, and Public Financial Institutions;
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Property or Municipal Tax receipt
            </td>
        </tr>
        <tr>
            <td class="myTdLabelKyc" valign="top">
                    Letter issued by a gazetted officer, with a duly attested photograph of the person.
            </td>
            <td class="myTdLabelKyc" valign="top">
                    Bank account or Post Office savings bank account statement with address
            </td>
        </tr>
    </tbody>
</table>

<p>
</p>

<table class="myTblSign" width="100%" border="0">
	<tr>
		<td class="myTdLabelSign">
		<b>Date</b>
		</td>
		<td class="myTdLabelSign">
		</td>
	</tr>
	<tr>
		<td class="myTdLabelSign">
		<b>Place</b>
		</td>
		<td class="myTdLabelSign" align="right" style="text-align: right;">
		<b>Authorized Signatory(s)</b>
		</td>
	</tr>
	<tr>
		<td class="myTdLabelSign">
		</td>
		<td class="myTdLabelSign" align="right" style="text-align: right;">
		 (Directors / Partners /Proprietor/Trustees/Karta)
		</td>
	</tr>
</table>
<br></br>
<table class="myTblGeneral" border="0" cellspacing="0" cellpadding="0" width="100%">
   <tbody>
      <tr>
         <th class="myTheadGeneral" width="30%">
            <p><strong>Name of Applicant Entity</strong></p>
         </th>
         <th class="myTheadGeneral" width="70%">
         </th>
      </tr>
   </tbody>
</table>
<p></p>
<br></br>
<table class="myTblOthersDoc" width="100%">
      <tr>
         <th width="100%">
            Other Documents Required
         </th>
      </tr>
</table>
<p></p>
<table class="myTblOthersDocReq" width="100%">
   <tbody>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>Proprietorship</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>Bank Statement (Last 2 months)</li>
               <li>IT Returns or Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
               <li>Udyog Aadhar</li>
               <li>
                  Any one of following
                  <ul>
                     <li>Registration Certificate From State Govt Or Statutory Body</li>
                     <li>Registration Under Shop &amp; Establishment</li>
                     <li>Trade License</li>
                  </ul>
               </li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>Partnership</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>Partnership Deed</li>
               <li>Bank Statement (Last 2 months)</li>
               <li>Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
               <li>Udyog Aadhar</li>
               <li>
                  Any one of following
                  <ul>
                     <li>Registration Certificate From State Govt Or Statutory Body</li>
                     <li>Registration Under Shop &amp; Establishment</li>
                     <li>Trade License</li>
                  </ul>
               </li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>Private Limited Company / Public Ltd (Unlisted) </strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>List of Directors</li>
               <li>List of Shareholders with % shareholding (For entity and shareholding entity)</li>
               <li>Bank Statement (Last 2 months)</li>
               <li>Certificate of Incorporation with AoA &amp; MOA</li>
               <li>Udyog Aadhar</li>
               <li>Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>Private Limited Company / Public Ltd (Listed)</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>Bank Statement (Last 2 months)</li>
               <li>Certificate of Incorporation with AoA &amp; MOA</li>
               <li>Udyog Aadhar</li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>HUF</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>HUF Deed</li>
               <li>Bank Statement (Last 2 months)</li>
               <li>Udyog Aadhar</li>
               <li>Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
               <li>
                  Any one of following
                  <ul>
                     <li>Registration Certificate From State Govt Or Statutory Body</li>
                     <li>Registration Under Shop &amp; Establishment</li>
                     <li>Trade License</li>
                  </ul>
               </li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>Trust/ Society</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>Trust Deed</li>
               <li>Bank Statement (Last 2 months)</li>
               <li>Udyog Aadhar</li>
               <li>Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
               <li>
                  Any one of following
                  <ul>
                     <li>Registration Certificate From State Govt Or Statutory Body</li>
                     <li>Registration Under Shop &amp; Establishment</li>
                     <li>Trade License</li>
                  </ul>
               </li>
            </ul>
         </td>
      </tr>
      <tr>
         <th class="myTableOthersDocHeaders" width="30%">
            <p><strong>LLP</strong></p>
         </th>
         <td class="myTableOthersDocdata" width="70%">
            <ul>
               <li>Bank Statement (Last 2 months)</li>
               <li>Partnership Deed</li>
               <li>Udyog Aadhar</li>
               <li>Financials<br></br>
                  (Balance Sheet &amp; P&amp;L statement for last FY)
               </li>
               <li>
                  Any one of following
                  <ul>
                     <li>Registration Certificate From State Govt Or Statutory Body</li>
                     <li>Registration Under Shop &amp; Establishment</li>
                     <li>Trade License</li>
                  </ul>
               </li>
            </ul>
         </td>
      </tr>
   </tbody>
</table>
<p><strong>Beneficial Ownership details if applicable</strong></p>
<br></br>
<br></br>
<p class="myTdLabelSign">
	<strong> Sign &amp; Stamp of Authorised Official(s)</strong>
</p>
<br></br>
<h2 style="text-align:center"><u>SIGNATURE CARD FOR ALL INDIVIDUALS MENTIONED IN THIS </u></h2>
<h2 style="text-align:center"><u>APPLICATION FORM / KYC FORM</u></h2>
<br></br>
<h3><u>Promoters / All Directors / Proprietor/ All Partners / Trustees of Trust/ HUF (Karta)</u></h3>
<h4><em>(Add multiple if required)</em></h4>
<%
         	CompanyContactBean lCCBean1 = null;
        	int lCount1 = lRegistrationWrapperBean.getChiefPromoters().size();
            for(int lPtr=0; lPtr < lCount1; lPtr++)
            {
            	lCCBean1 = lRegistrationWrapperBean.getChiefPromoters().get(lPtr);
            	if(lCCBean1.getId()!=null){
%>
<br></br>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
            }
%>
<%
        	lCount1 = lRegistrationWrapperBean.getOtherPromoters().size();
            for(int lPtr=0; lPtr < lCount1; lPtr++)
            {
            	lCCBean1 = lRegistrationWrapperBean.getOtherPromoters().get(lPtr);
            	if(lCCBean1.getId()!=null){
%>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean1))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
            }
%>
<h3><u>Authorised Officials</u></h3>
<h4><em>(Add multiple if required)</em></h4>
<%
         	CompanyContactBean lCCBean2 = null;
        	int lCount2 = lRegistrationWrapperBean.getAuthOfficials().size();
            for(int lPtr=0; lPtr < lCount2; lPtr++)
            {
            	lCCBean2 = lRegistrationWrapperBean.getAuthOfficials().get(lPtr);
            	if(lCCBean2.getId()!=null){
%>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean2))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
            }
%>
<h3><u>Administrators</u></h3>
<h4><em>(Add multiple if required)</em></h4>
<%
         	CompanyContactBean lCCBean3 = null;
            lCCBean3 = lRegistrationWrapperBean.getAdmin();
            if(lCCBean3.getId()!=null){
%>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getContactFullName(lCCBean3))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
%>
<h3><u>Other Individual having &gt;20% shareholding in unlisted entity</u></h3>
<h4><em>(Add multiple if required)</em></h4>
<%
         	CompanyShareIndividualBean lCSIBean = null;
        	int lIndividualCount = lRegistrationWrapperBean.getCompanyShareIndividuals().size();
            for(int lPtr=0; lPtr < lIndividualCount; lPtr++)
            {
            	lCSIBean = lRegistrationWrapperBean.getCompanyShareIndividuals().get(lPtr);
            	if(lCSIBean.getId()!=null){
%>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareIndividualFullName(lCSIBean))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
            }
%>

<h3><u>KMP of Share Entity</u></h3>
<h4><em>(Add multiple if required)</em></h4>
<%
         	CompanyShareEntityBean lCSEBean = null;
        	int lEntityCount = lRegistrationWrapperBean.getCompanyShareEntities().size();
            for(int lPtr=0; lPtr < lEntityCount; lPtr++)
            {
            	lCSEBean = lRegistrationWrapperBean.getCompanyShareEntities().get(lPtr);
            	if(lCSEBean.getId()!=null){
%>
<table class="myTblSignCard" width="100%">
   <tbody>
      <tr>
         <td class="myTblSignCardData" width="33%">
            <center><p><%=TredsHelper.getInstance().getCleanData(lRegistrationWrapperBean.getShareEntityFullName(lCSEBean))%></p></center>
         </td>
         <td class="myTblSignCardData" width="34%">
            <center><p>Photo</p></center>
         </td>
         <td class="myTblSignCardData" width="33%">
            <center><p>Full Sign</p></center>
         </td>
      </tr>
   </tbody>
</table>
<%
				}
            }
%>
<br></br>
<h3><u>Declaration</u></h3>
<table class="myTblDeclaration" width="100%">
   <tbody>
      <tr>
         <td class="myTblDecl" colspan="2" width="60%">
            <p>This form is Annexure to the form filled by us on RXIL TReDS platform through reg login provided to us.</p>
            <p>I /we hereby declare that the particulars given herein are true, correct and complete to the best of my/our knowledge and belief. The documents submitted along with the application are genuine and I am/we are not making this application for the purpose of contravention of any Act, Rules, Regulations or any Statute or Legislation or any Notifications/ Directions issued by any governmental or statutory authority from time to time. I/we hereby undertake to promptly inform any changes to the information provided herein above.<br></br> I/we hereby authorise RXIL, its authorised agents and representatives to disclose, share, remit in any form, mode or manner all /any of the information provided by me/us. I/we hereby agree to provide any additional information/documents that may be required in connection with this application.</p>
         </td>
      </tr>
      <tr>
         <td class="myTblDec2" width="20%">
            <p><strong>Place: </strong></p>
         </td>
         <td class="myTblDec2" width="20%">
            <p><strong>Date:</strong></p>
         </td>
      </tr>
   </tbody>
</table>
<br></br>
<table class="myTblGuideLines" width="100%">
   <tbody>
      <tr>
         <th class="myTblGuideLinesHeaders" colspan="2" width="100%">
            <p align="center"><strong>IMPORTANT NOTES - PLEASE READ BEFORE FILLING UP THE FORM</strong></p>
         </th>
      </tr>
      <tr>
         <td class="myTblGuideLinesData" width="50%">
            <ol>
               <li>This Application Form is meant to enable a person/non individual to comply with the Customer Identification Programme laid down under the Prevention of Money Laundering Act, 2002 (PMLA) hereinafter referred to as Know Your Customer (KYC) requirements. It is for use by INDIVIDUALS only. A separate form is provided for non-individual entities such as Hindu Undivided Family (HUF), Corporates, Trusts, Societies, etc.</li>
               <li>This form also meant for providing information and documents required for KYC compliance. KYC compliance is a must for availing any facility.</li>
               <li>The KYC process requires customers to provide identity and address information supported by documentary evidence to comply with the KYC requirements, apart from other information and copies of relevant documents as per the prescribed guidelines. RXIL reserves the right to seek any additional information/ documentation in terms of the PMLA / RBI guidelines at any point of time.</li>
               <li>RXIL will not be liable for any errors or omissions on the part of the applicant in this Application (KYC Application) Form.</li>
               <li>Power of Attorney (PoA) Holder: Customers desirous of acting through a PoA must note that the KYC compliance requirements are mandatory for both the PoA issuer (i.e., customer) and the Attorney (i.e., the holder of PoA), both of whom should be KYC compliant in their independent capacity.</li>
            </ol>
         </td>
         <td class="myTblGuideLinesData" width="50%">
            <ol>
               <li>RXIL and its Directors, employees and agents shall not be liable in any manner for any claims arising whatsoever on account of rejection of any application due to non-compliance with the provisions of the PMLA or where RXIL believes that transaction(s) by an applicant / investors is / are suspicious in nature within the purview of the PMLA and RBI Guidelines and requires reporting the same to Financial Intelligence Unit - India (FIU-IND).</li>
               <li>Joint Holders: Joint holders need to be individually KYC compliant, for e.g. in case of three joint holders, all holders need to fill up the KYC form and should be KYC compliant.</li>
               <li>Minors: In case of a Minor, the Guardian should be KYC compliant and accordingly fill up the KYC form. However, in respect of the minor, photocopy of the School Leaving Certificate / Marksheet issued by Higher Secondary Board / Passport of Minor / Birth Certificate (to prove the age to qualify for minor status) must be provided. The Minor, upon attaining majority, should immediately apply for KYC compliance in his/her own capacity in order to be able to transact further in his/her own capacity.</li>
               <li>As per the guidelines, KYC compliance is also required in respect of Admin User. third party mortgagors / guarantors / pledgers etc.</li>
               <li>For non-residents and foreign nationals (subject to RBI and FEMA guidelines), copy of passport /PIO Card/OCI Card and proper overseas address proof are mandatory.</li>
               <li>In case of Merchant Navy NRI's, Mariner's declaration or certified copy of CDC (Continuous Discharge Certificate) is to be submitted.</li>
            </ol>
         </td>
      </tr>
      <tr>
         <th class="myTblGuideLinesHeaders" colspan="2" width="100%">
            <p align="center"><strong>GUIDELINES FOR FILLING UP THIS APPLICATION FORM</strong></p>
         </th>
      </tr>
      <tr>
         <td class="myTblGuideLinesData" width="50%">
            <p>General</p>
            <ol>
               <li>The Application Form should be completed in ENGLISH and in BLOCK LETTERS.</li>
               <li><u>Please Tick in the appropriate box wherever applicable</u></li>
               <li><u>Please write NA, if any column is not applicable.</u></li>
               <li><u>Please mention if initial KYC compliance / subsequent Change or periodic Updation.</u></li>
               <li>Please fill the form in legible handwriting so as to avoid errors in your application processing. Please do not overwrite. Corrections should be made by canceling and re-writing, and such corrections should be counter-signed by the applicant.</li>
               <li>Applications incomplete in any respect and / or not accompanied by required documents are liable to be rejected.</li>
               <li>You are required to submit a copy of Proof-of-Identity document and a Proof-of-Address document (apart from copies of all other required documents) as per the extant Guidelines.</li>
               <li>Any document having an expiry date should be valid on the date of submission.</li>
               <li>Copies of all the documents submitted by the applicant should be self-attested and accompanied by originals for verification. In case the originals of any document is not produced for verification, then the copies should be properly attested by -</li>
               <li>a Notary Public / Gazetted Officer / Manager of a Scheduled Commercial Bank or Multinational Foreign Banks (Name, Designation and Seal should be affixed on the copy).</li>
               <li>In case of NRIs / Foreign Entities- authorized officials of overseas branches of scheduled commercial Banks registered in India, Notary Public, Court Magistrate, Judge, Indian Embassy / Consulate General in the country where the person reside.</li>
               <li>Unattested photocopies of an original document are not acceptable.</li>
               <li>If the documents including attestation / certifications are in regional language or foreign language then the same has to be translated into English for submission.</li>
            </ol>
            <p>A.			Identity Details</p>
            <ol>
               <li>Customer ID : Customer ID is to be filled after creating the unique customer ID for the Customer by the Exchange. Mention PAN in the space provided and submit original PAN card which will be returned across the counter after verification.</li>
               <li>Name : Please state your name as Title (Mr / Mrs / Ms / Dr / Commander / etc.), First, Middle and Last Name in the space provided. This should match with the name as mentioned in the PAN card / id-proof document submitted failing which the application is liable to be rejected. If the PAN card has a name by which the applicant has been known differently in the past, than the one provided in this application form, then requisite proof should be provided e.g. marriage certificate, or gazetted copy of name change.</li>
               <li>Entity Name: This should be match exactly with the name as mentioned in the PAN card and other supporting documents; otherwise, the application is liable to be rejected. If the PAN card and other supporting documents has a name by which the applicant has been known differently in the past, than the one provided in this application form, then requisite proof should be provided e.g. Name Change Certificate.</li>
               <li>Date of Birth: Please ensure that this matches with the Date of Birth as indicated in the PAN card/other documentary proof.</li>
               <li>Please mention the date of incorporation or registration of your organization. If your company is listed on any stock exchange, please mention the details.</li>
               <li>Please affix and sign across the most recent passport-size colour photograph.</li>
            </ol>
         </td>
         <td class="myTblGuideLinesData" width="50%">
            <br></br>
            <br></br>
            <p>B.			Address Details.</p>
            <ol>
               <li>The Addresses mentioned should match with the address in the 'Proof-of-Address'; submitted as supporting document. Otherwise, the application (KYC application) is liable to be rejected.</li>
               <li>Address for Communication: Please provide here the address where you wish to receive all communications sent by the Exchange.</li>
               <li>Contact Details: Please provide your Telephone / Email contact details etc.</li>
               <li>Address for correspondence / Registered Office / Overseas address : Entities having a separate correspondence address apart from that of the registered office address etc., they should mention same separately. Entities having any Overseas Address must quote the same supported by required documents duly certified.</li>
               <li>Permanent Address / Overseas Address: If you are a Resident Indian, and your Permanent address is different from the one mentioned in the Address for Correspondence, please state it here. If you are a Non-Resident Indian or a Person of Indian Origin, it is mandatory for you to state your Overseas Address here along with proper proof for the same &amp; copy of passport as mandatory documents.</li>
            </ol>
            <br></br>
            <p>C. Other details</p>
            <ol>
               <li>Gross Annual Income details: Pl. include both taxable and tax-free incomes.</li>
               <li> Occupation details: <u>If you are in service</u>, pl. indicate name of the organization. <u>If you are a professional</u>, pl. write name of the profession viz., lawyer, doctor, CA, consultant, stock broker etc. <u>If you are self-employed</u>, pl. indicate nature of the business viz., manufacturing, trading, service, real estate, etc., and also the type of industry/product/sector eg. Manufacture of plastic bags, jeweler, grocery shop, hardware shop etc.</li>
               <li>Politically Exposed Persons (PEP) are defined as individuals who are or have been entrusted with prominent public functions in a foreign country eg. Heads of Sates or of Governments, senior politicians, senior government/judicial/military officers, senior executives of state-owned corporations, important political party officials, etc. If the applicant is a PEP, it should be indicated.</li>
               <li><u>TO PROVIDE MORE/ADDITIONAL INFORMATION, IF ANY</u> : <u>For providing more information at any column or for any additional information,additional sheets may be used and stapled with this application (KYC application).</u></li>
            </ol>
            <br></br>
            <p>Please submit the filled in KYC Form along with the entire set of supporting documents. Please also submit a photocopy of the Form for acknowledgement purpose, which you can retain for your records.</p>
            <p>Other important notes, after the KYC Acknowledgement is issued to you :</p>
            <ol>
               <li>Signature on this application (KYC Application) Form should match with that on the Application for any facility like Loan/Fixed Deposit Application Form etc.</li>
               <li>If there are any changes in an Applicant's details after submission of this form such as Name, Address, Status, etc., the change should be registered expeditiously. Similarly, periodic updation of KYC details should be provided to the Exchange as per the guidelines. Please indicate the purpose.</li>
               <li>Original / Attested copies of documents supporting the change will be required to be submitted together with the KYC Details Change Form. Latest attested copies of supporting documents to be provided along with KYC Updation Form.</li>
            </ol>
         </td>
      </tr>
   </tbody>
</table>
<br></br>
	
` <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tbody>
	        <tr>
	            <td  class="myTdLabel" width="15%" valign="top">
	                    Name
	            </td>
	            <td  class="myTdValue" width="85%"  valign="top">
	            </td>
	        </tr>
	        <tr>
	            <td  class="myTdLabel"  valign="top">
	                    Designation 
	            </td>
	            <td  class="myTdValue"  valign="top">
	            </td>
	        </tr>
	        <tr>
	            <td  class="myTdLabel"  valign="top">
	                    Telephone
	            </td>
	            <td  class="myTdValue"  valign="top">
	            </td>
	        </tr>
	        <tr>
	            <td  class="myTdLabel"  valign="top">
	                    Email Id
	            </td>
	            <td  class="myTdValue"  valign="top">
	            </td>
	        </tr>
	        <tr>
	            <td  class="myTdLabel"  valign="top">
	                    Place:
	            </td>
	            <td  class="myTdValue"  valign="top">
	            </td>
	        </tr>
	        <tr>
	            <td  class="myTdLabel"  valign="top">
	                    Date:
	            </td>
	            <td  class="myTdValue"  valign="top">
	            </td>
	        </tr>
	    </tbody>
</table>
</tbody>
</table>
</body>
</html>