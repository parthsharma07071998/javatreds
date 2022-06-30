
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
		<td style='text-align:left' width="20%"><img src="<%=TredsHelper.getInstance().getApplicationURL()%>../images/logo2x.png" width="108px" height="36px" border="0px" /></td>
		<td><h2>TEXT OF CLICK WRAP AGREEMENT BY BUYER ON TREDS PLATFORM</h2></td>
	</tr>
</table>
</center>
</div>

<div style="margin:20px;">
<p>
	By clicking &#34;<strong>I Agree</strong>&#34; tab on the TReDS Platform we, as Buyers, declare, agree and confirm to the relevant Financier (which term shall
	include any transferee, assignee or successor of such Financier) who bids and or finance on the TReDS Platform for financing of the instruments/ Factoring
	Units representing Trade Receivables of the Seller, that: 
</p>
<p>
    a)	We agree and confirm that financing of the instrument of Trade Receivables /factoring unit(s) by a Financier as well as any subsequent transfer, if any, 
	of such Factoring Units by a Financier to its assignee, shall amount to assignment of such underlying receivables represented by such instruments/factoring
	unit(s), in favour of such Financier and we agree and hereby irrevocably consent to the same and waive all right of notices whatsoever in this regard.
	Further, a notice of assignment by seller either by itself or through its authorized agent shall constitute a valid and binding notice for the purpose 
	of provisions of section 8 of the Factoring Regulation Act, 2011.
</p>
<p>
    b)	Upon acceptance of the bid (either by us or the Seller)  of the Financier for financing /factoring of the Factoring Units, we undertake to pay to the 
	Financier, on the relevant Due Date of the Factoring Unit, the amount due on such factored unit(s) on such Due Date(s), along with applicable interest and 
	other charges, if any, without any protest, demur, or proof and irrespective of and notwithstanding any right of set off available to us with the relevant 
	Seller. Our liability shall not be affected by and be independent of any dispute or differences, whatsoever between us and the relevant Seller and upon 
	acceptance of the offer by the Financier (either by us or the Seller), we shall not be entitled to dispute the payment of amounts due to the Financier, on 
	account of quality of the underlying securities, goods or services  or otherwise.  The Financier shall have and continue to have absolute charge on the goods and 
	supplies made by the Seller to us and also on the underlying security interest, if any, until the discharge of our obligations to pay the due amount to the Financier. 
</p>
<p>
    c)	Our obligation to pay the amount due under the relevant Factoring Unit to the Financier(s) shall constitute our independent legally binding and
	 enforceable obligation, independent and irrespective of any underlying transaction with the Seller, without any further action or execution of any
	  further agreement between us and the Financier and which we shall discharge on the relevant Due Date(s). The Financier shall not be required to give any
	   notice of demand whatsoever in this regard to us and we waive our right in this regard.
</p>
<p>
   d)	We have /shall mke necessary arrangements with our Banker for the payment to the Financier of all the amounts due under the Factoring Units to be
    factored/financed on the TReDS Platform, on the relevant Due Date(s) and for this purpose we have irrevocably authorized our bankers to debit, without any 
	further instructions from us, our bank account towards payment of the amount due under such Factoring Units in accordance with the instructions received
	 from RXIL/Financer or generated through TReDs Platform, whether electronically or otherwise. 
</p>
<p>
    e)	On our failure to discharge our obligation and repay any amount due under the Factoring Units on the Due Date to the Financier, the Financier shall be
	 entitled to enforce our obligation under the Applicable Laws and take all necessary steps to recover the amounts due and payable by us under such Factoring
	  Unit and also enforce underlying security interest thereof.
</p>
<p>
   f)	We understand, agree and confirm that declarations and assertions made by us hereunder and also by way of declaration and assertion to RXIL under any
    agreement(s) and/or the General Conditions/Business Rules for Participation on TReDs/Usage Terms shall also form the basis of any decision by the Financier
	 in relation to factoring/financing of the Factoring Units on the TReDS Platform, thereby form an integral part of the terms governing financing of
	  factoring units by the Financiers and thus can be enforced by the relevant Financer (including, as the case may be, its respective successors, assigns and
	   transferees) against us as an independent and binding obligation. 
</p>
<p>
    g)	Any advice/ payment instruction generated through TReDS Platform, shall without any other or further proof be final, conclusive and binding on us.
</p>
<p>
    h)	We hereby convey our unequivocal and irrevocable authority to the Seller and also appoint the Seller as our agent to exercise any of the rights that may
	 be available to us in regards to selecting /accepting any offer or bid of the Financier(s) for factoring of the accepted/uploaded Factoring Unit and
	  undertake to be bound by the terms and obligations set out herein in relation to such Factoring Units, in the same manner as if such actions were
	   undertaken by us. .
</p>
<p>
    i)	We agree, undertake and confirm that we will maintain and preserve such information, records, books and documents pertaining to our transaction with the
	 Seller, including the records, invoices, receipts or any other document evidencing the underlying transactions between the Seller and us, for such period
	  as may be specified or required by RXIL or the Financier from time to time. We further agree to make all reasonable endeavours to co-operate with the
	   Financier in relation to any dispute, difference or proceeding arising out of any trade of the Factoring Unit undertaken or any obligation thereunder.
</p>
<p>
    Capitalised terms used but not defined herein shall bear the meaning ascribed to them under the respective master agreement executed by us or the Financier
	 with RXIL.
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