
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.common.utilities.FormatHelper"%>
<%@page import="java.sql.Date"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!
public String formatData(Object pData)
{
	try
	{
		if(pData != null)
			return StringEscapeUtils.escapeXml(pData.toString());
	}
	catch(Exception lEx)
	{
		
	}
	return "";
}
public String formatDecimal(Object pData) {
    if (pData != null) {
        BigDecimal lBigDecimal = (BigDecimal)pData;
        DecimalFormat lDecimalFormat = new DecimalFormat("##,##,##,##,##0.00");
        return lDecimalFormat.format(lBigDecimal.doubleValue());
    }
    return "";
}
%>
<%
Map<String, Object> lData = (Map<String, Object>) request.getAttribute("data");
String lImagePath = "";
if (lData.containsKey("imagePath")){
	lImagePath += lData.get("imagePath");
}
lImagePath += "../images/logo2x.png";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
<title>Agreement File</title>
<style type="text/css">
		@media screen {.pagebreak  { height:10px; border-top:1px dotted #999; margin-bottom:13px;display:none;} } 
		@media print {.pagebreak { height:0; page-break-before:always; margin:0; border-top:none; size:portrait} } 
		.pagebreak{display:none;} 
		@page { 
				 /*size: 8.50in 9.50in;*//* width height */
			size: Legal ;   /* auto is the initial value */;
				 /* size:355.6mm 215.9mm; */
			margin-top: 10px;
			margin-left:1%;
			margin-right:1%;
			border: 1px solid black;
		}
		@bottom-left {
			content: element(footer);
		}
		.footerPageNo{color:#666;} 
		#page-footer{text-align: left;display:none;} 
		h2 {
			color: #298FCE;
			font-family:Arial;
			font-size: 24px;
		}
		.mytable {
		    border-collapse: collapse;
		}
		
		.mytable th{
		    border: 1px solid black;
		}
		.mytable td {
		    border: 1px solid black;
		}
		p {
		 color : black;
		}

</style>
</head>
<body>
<div class='header'>
<center>
<table border="0" cellspacing="10" cellpadding="0">
	<tr>
		<td style='text-align:left' width="20%"><img src="<%=lImagePath%>" width="108px" height="36px" border="0px" /></td>
		<td><h2>TEXT OF CLICK-WRAP AGREEMENT FOR SELLER ON TREDS PLATFORM</h2></td>
	</tr>
</table>
</center>
</div>

<div style="margin:20px;">
<p>
	By clicking &#34;<strong>I Agree</strong>&#34; tab on the TReDS Platform we, as Suppliers, declare, agree and confirm to the relevant Financier (which term shall include any transferee, assignee or successor of such Financier) who bids and or finance on the TReDS Platform for financing of the instruments/ Factoring Units representing Trade Receivables of the Seller, that:
</p>
<p>
    a)	We shall upload or authorize upload, in any manner whatsover of only unpaid bill/ invoices and other instruments and factoring units representing Trade Receivable arising out of genuine and bona fide commercial transaction of sale and supply of goods and services and such invoices or factoring units has not been financed on any other similar platform for transacting in receivables or under any working capital financing or any other arrangement provided by any other lender and free of charge whatsoever
</p>
<p>
    b)	Any factoring unit uploaded and / or accepted on the Platform shall not be uploaded on any other similar platform for transacting in receivables or any other arrangement provided by any other lender.
</p>
<p>
    c)	We remain <%=lData.get("msmeType")%> enterprise as per the relevant definition under MSME Act and have no reasons to believe otherwise.
</p>
<p>
    d)	In case the platform intimates about the error in a transaction, we shall agree to abide by the decision of the platform in this regard. 
</p>
<p>
    e)	In case we identify any funding in violation with clause (a) or any other error, we shall forthwith inform the Platform and shall agree to abide by the decision of the platform in this regard. 
</p>
<p>
    f)	We irrevocably authorize the platform for the following
</p>
<p>
    i)	Send a deed of assignment to the financier whose bid is accepted through mail or any other mode using under my electronic / actual/ digital signature
</p>
<p>
   ii)	Send a Notice of Assignment to financier who has funded us against the accepted bid through mail or any other mode under my electronic / actual/ digital signature with a copy to the Buyer
</p>
<p>
    Capitalised terms used but not defined herein shall bear the meaning ascribed to them under the respectivemaster agreement executed by us or the Financier with RXIL.
</p>

			<% 
			if (lData.containsKey("acceptDetails")){
				List<Map<String,Object>> lAcceptDetailsList = (List<Map<String,Object>>)lData.get("acceptDetails");
			%>
			<div>
			   <b>Aggreement Acceptance Details:</b>
			   <br></br><br></br>
			    <table class="mytable">
						<thead>
						    <th>Member ID</th>
							<th>Name</th>
							<th>User ID</th>
							<th>User Name</th>
							<th>Date Time</th>
						</thead>
						<tbody>
							<% 
							for (Map<String,Object> lAcceptDetails : lAcceptDetailsList){
						%>
							<tr >
								<td align="right"><%=formatData(lAcceptDetails.get("memberId"))%></td>
								<td align="right"><%=formatData(lAcceptDetails.get("name"))%></td>
								<td align="right"><%=formatData(lAcceptDetails.get("userId"))%></td>
								<td align="right"><%=formatData(lAcceptDetails.get("userName"))%></td>
								<td align="right"><%=formatData(lAcceptDetails.get("dateTime"))%></td>
							</tr>
						<% 
							}
						%>
						</tbody>
					</table>
			</div>
			<%
			}
			%> 
</div>

</body>

</html>