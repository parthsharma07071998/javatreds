
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
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
<title>Deed of Assignment</title>
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
</style>
</head>
<body>
<div class='header'>
<center>
<table border="0" cellspacing="10" cellpadding="0">
	<tr>
		<td style='text-align:left' width="20%"><img src="<%=TredsHelper.getInstance().getApplicationURL()%>../images/logo2x.png" width="108px" height="36px" border="0px" /></td>
		<td><h2>DEED OF ASSIGNMENT</h2></td>
	</tr>
</table>
</center>
</div>

<div style="margin:20px;">
	<p align="justify">
	This Assignment made by <b><%=formatData(lData.get("user"))%></b> (hereinafter referred as the <b>Seller/Assignor</b>, which term shall include, wherever the context so permits,
	its successors, permitted assigns, legal heirs, legal representatives and executors);   in favour of 
	<b><%=formatData(lData.get("companyName"))%></b> having its registered office at <b><%=formatData(lData.get("location"))%></b>
	a banking company under the Banking Regulation Act, 1949/ a non-banking financial company registered as factor under the Reserve Bank of India Act, 1934, (hereinafter referred as the Financier/Assignee which term shall include, wherever the context so permits its successors, assigns, legal heirs, legal representatives and executors).
	</p>
<p>
    <b>WITNESSETH:</b>
	<br></br>
	<br></br>
		<b>A.</b>WHEREAS Assignor is Participating as "Seller" on the electronic platformnamely "<b>TReDS</b>" set up by Receivables Exchange of India Limited <b>(RXIL)</b> for 
		and assignee is participating as "Financier" the purpose of facilitating factoring of its Trade Receivables arising out of commercial transaction of 
		sale and supply of services and goods to its Buyers <b>(Buyers)</b> (the said trade receivable hereinafter referred to as the "<b>Factoring Units</b>") as per the 
		terms and conditions of Master Agreement and Business Rules of RXIL .
        
	<br></br>
	<br></br>
		<b>B.</b>WHEREAS the bid of the Financier/Assignee for factoring/financing of the factoring units having been accepted by the Seller/Buyer in terms of 
		Business Rules of RXIL.

</p>
<p>
   <b>NOW, THEREFORE, TO ALL WHOM IT MAY CONCERN:</b>  Be it known that in consideration of the payment by ASSIGNEE to ASSIGNOR of the Factored amount the receipt of which is hereby acknowledged, and for other good and valuable consideration, ASSIGNOR hereby sells, assigns and transfers to the Assignee the full and exclusive right (including the right to claim the priority), rights, title and interest in and to the receivable in the below mentioned instrument/Invoices (Receivable):
    
</p>
<div>
   <p> <b>C.	<span style="text-decoration: underline;">Particulars of Invoices and Amount thereof:</span></b> </p>
    <br></br>
    <br></br>
    <table class="mytable">
			<thead>
				<th>Invoice number</th>
				<th>Debtor</th>
				<th>Invoice date</th>
				<th>Invoice amount (Rs)</th>
				<th>Factoring unit id</th>
				<th>Factoring unit date</th>
				<th>Factored amount (Rs)</th>
			</thead>
			<tbody>
				<%
				List<Map<String,Object>> lInstDetils = (List<Map<String,Object>>)lData.get("instDetils");
				for (Map<String,Object> lTmp : lInstDetils) {
				%>
					<tr >
						<td align="right"><%=formatData(lTmp.get("invoiceNo"))%></td>
						<td align="right"><%=formatData(lTmp.get("debtor"))%></td>
						<td align="right"><%=formatData(lTmp.get("invoiceDate"))%></td>
						<td align="right"><%=formatData(lTmp.get("invoiceAmt"))%></td>
						<td align="center"><%=formatData(lTmp.get("fuId"))%></td>
						<td align="right"><%=formatData(lTmp.get("fuDate"))%></td>
						<td align="right"><%=formatData(lTmp.get("fuAmt"))%></td>
					</tr>
				<%
				}
				%>
			</tbody>
		</table>

</div>
<p>
   <b>D.	<span style="text-decoration: underline;">In this connection the Assignor further covenant and confirm that :</span></b>
</p>
<p>
1]	the Account(s) on account of the above receivables are due and the Assignor has not received payment for the same or any part thereof;warrants that it has full title to the said receivables, full authority to sell, transfer and assign and that the same are sold free and clear of all liens, encumbrances and any known claims;
</p>
<p>
2] it shall, from time to time, execute and deliver such further documents and perform such further acts, as may be required to perfect, protect and more fully evidence the Assignee's title over the said receivables; 
</p>
<p>
3]	it shall hold underlying documents pertaining to the said receivables in trust and as agent for Assignee and shall hand over all such documents, writings, agreements, amounts, monies, to the Assignee/its Representative on demand; and shall not claimor exercise any right of deduction, lien (general or specific) or set-off on, over or in respect of the said receivables, amounts, writings or things;
</p>
<p>
4]shall bear all such imposts, duties and taxes which may be levied before the date hereof by any statutory or regulatory authority pertaining to the receivable.
</p>
<p>
   The authorized representatives of the aforesaid Seller/Assignee have hereunto fixed their signatures on the date as mentioned below. 
</p>
<br></br>
 <%
 	Map<String, Object> lUserDetails = (Map<String,Object>) lData.get("userDetails");        		
 %>
<table>
    <tr>
        <td style = "padding-right: 5em" width="50%">ASSIGNOR</td>
        <td style = "padding-right: 15em" align="left">ASSIGNEE</td>  
    </tr>
    <tr>
    	<td>
        	<%=formatData(lData.get("user"))%>
        </td>
    	<td>
        	<%=formatData(lData.get("companyName"))%>
        </td>  
    </tr>
    <tr>
    	<td>
        	<%=formatData(lUserDetails.get("cntrUserName"))%>
        </td>
    	<td>
        	<%=formatData(lUserDetails.get("finBidUser"))%>
        </td>  
    </tr>
    <tr>
    	<td>
	        <%=formatData(lUserDetails.get("cntrUserTime"))%>
        </td>
    	<td>
            <%=formatData(lUserDetails.get("finBidTime"))%>
        </td>  
    </tr>
</table>
 <br></br>
 <br></br>
<p>Date of Deed of Assignment: <%=lUserDetails.get("acceptDate")%></p>
 <br></br>
<div>
   <b><u>Bid details:</u></b>
   <br></br><br></br>
    <table class="mytable" width="100%">
			<thead>
			    <th>Buyer name</th>
				<th>Buyer user name</th>
				<th>Seller name</th>
				<th>Seller user name</th>
			</thead>
			<tbody>
				<tr >
					<td align="right"><%=formatData(lUserDetails.get("buyer"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("buyerUser"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("seller"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("sellerUser"))%></td>
				</tr>
			</tbody>
	</table>
	<br></br>
    <table class="mytable" width="100%">
			<thead>
				<th>FU ID</th>
				<th>FU amount</th>
				<th>Accepted bid rate</th>
				<th>Date and time</th>
			</thead>
			<tbody>
				<tr >
					<td align="right"><%=formatData(lUserDetails.get("fuId"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("fuAmount"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("bidRate"))%></td>
					<td align="right"><%=formatData(lUserDetails.get("acceptDate"))%></td>
				</tr>
			</tbody>
	</table>


</div>
</div>

<div class='footer'>
	<center>
	This is a computer generated letter and does not require any signature.
	</center>
</div>

</body>

</html>