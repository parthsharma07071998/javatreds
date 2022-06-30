
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
SimpleDateFormat lDateTimeFormatter = BeanMetaFactory.getInstance().getDateTimeFormatter();
String lContextPath=request.getContextPath();
Map<String, Object> lData = (Map<String, Object>) request.getAttribute("data");
List<Map<String, Object>> lList = (List<Map<String,Object>>) request.getAttribute("factoringUnitDetails");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
<title>Issue Details</title>
<style type="text/css">
div.header {
    display: block; text-align: center; 
    position: running(header);
}
div.footer {
    display: block; text-align: center;
    position: running(footer);
}
div.content {page-break-after: always;}
h2 {
	color: #298FCE;
	font-family:Arial;
	font-size: 28px;
}
.myTable {
    border-collapse: separate;
    width:100%; 
    table-layout: fixed;
    border-spacing: 5px;
}
.myTable th{
    text-align:center;
}
.myTable td{
	padding: 3px;
	word-wrap:break-word;
}
.myValue {
	text-align:left;
	border: 1px solid black;
}
.myLabel { 
	font-weight: bold;
}

.myTable1 {
    border-collapse: collapse;
    width:100%; 
	border-spacing: 5px;
	table-layout: auto;
}
.myTable1 th{
    border: 1px solid black;
    text-align:center;
}
.myTable1 td{
    border: 1px solid black;
    text-align:center;
    padding: 3px;
}
.myLabel1{
	font-weight: bold;
	text-align:left;
}
.myValue1{
	text-align:right;
}

.myTable2 {
    border-collapse: collapse;
    width:40%; 
	margin-left:60%;
	table-layout: auto;
	float: right;
}
.myTable2 td {
    border: 1px solid black;
    width:50%;
}

.myTable3 {
    border-collapse: separate;
    width:40%; 
	table-layout: auto;
	border-spacing: 5px;
}
.myTable4 {
    border-collapse: separate;
    width:60%; 
	table-layout: auto;
	border-spacing: 5px;
}
p.normal {
    font-weight: bold;
    margin-left:65%;
}

hr { 
    display: block;
    margin-top: 0.5em;
    margin-bottom: 0.5em;
    margin-left: 2%;
    margin-right: 2%;
    border-style: inset;
    border-width: 1px;
} 
body
{
	font-family:Arial;
	font-size: 11.0px;
}

@page { 
	 /*size: 8.50in 9.50in;*//* width height */
	 size: A4 landscape;   /* auto is the initial value */
	 margin-top: 5%;
	 margin-left:2%;
	 margin-right:2%;
	 padding: 0px;
	 
	 border: 0pt solid black;
	 
	 @top-right {
        content: element(header); 
        vertical-align: bottom;
        padding-bottom: 10px;
    }
	@bottom-left {
	    content: element(footer);
	}
	@bottom-right {
	    font-style: italic; 
	    font-size:11px;
	    color:black;
	    content: counter(page);
	 text-align:center;
	}
	@top-center {
	  content: element(current);
	  text-align:center;
	}
}
@media print
{
	.pagebreak { height:0; page-break-before:always; margin:0; border-top:none; }
}
@media screen{
    .pagebreak  { height:5px; border-top:0px dotted #999; margin-bottom:6px; }
}

div.indent{ padding-left: 1.8em }

</style>
</head>
<body>
<div class='header'>
<center>
<table border="0" cellspacing="5" cellpadding="10" width="100%">
	<tr>
		<td style='text-align:center' width="25%"><img src="<%=TredsHelper.getInstance().getApplicationURL()%>../images/logo2x.png" width="108px" height="36px" border="0px" /></td>
		<td style='text-align:left' cellpadding="10"><h2>  RECEIVABLES EXCHANGE OF INDIA LIMITED</h2></td>
	</tr>
</table>
</center>
</div>
<center><b><u>TAX INVOICE&lt;FOR SERVICES&gt;</u></b></center>
<div style="margin:1%;">
<table class="myTable">
	<tbody>
		<tr>
			<th width="30%"></th>
			<th width="70%"></th>
			<th width="30%"></th>
			<th width="70%"></th>
 		</tr>
		<tr>
		</tr>
		<tr>
			<td class="myLabel">Supplier Name</td>
			<td class="myValue"><%=formatData(lData.get("tredsName"))%></td>
			<td class="myLabel">Nature of Transaction</td>
			<td class="myValue"><%=formatData(lData.get("tredsNatureOfTrans"))%></td>
		</tr>
		<tr>
			<td class="myLabel">Supplier Address </td>
			<td class="myValue"><%=formatData(lData.get("tredsAddress"))%></td>
			<td class="myLabel"> GSTIN Of RXIL</td>
			<td class="myValue"><%=formatData(lData.get("tredsGstn"))%></td>
		</tr>
		<tr>
			<td class="myLabel">Telephone</td>
			<td class="myValue"><%=formatData(lData.get("tredsTelephone"))%></td>
			<td class="myLabel"> Supplier State</td>
			<td class="myValue"><%=formatData(lData.get("tredsStateDescription"))%></td>
		</tr>
		<tr>
			<td class="myLabel">E-Mail</td>
			<td class="myValue"><%=formatData(lData.get("tredsEmail"))%></td>
			<td class="myLabel"> State Code</td>
			<td class="myValue"><%=formatData(lData.get("tredsState"))%></td>
		</tr>
	</tbody>
</table>
<hr>
</hr>
<p>
</p>
&#160;&#160;<b>Provided Services to..</b>
<table class="myTable">
	<tbody>
		<tr>
			<th width="30%"></th>
			<th width="70%"></th>
			<th width="30%"></th>
			<th width="70%"></th>
 		</tr>
		<tr>
			<td class="myLabel">Client Name</td>
			<td class="myValue"><%=formatData(lData.get("entName"))%></td>
			<td class="myLabel"> Orignal Invoice No.</td>
			<td class="myValue"><%=formatData(lData.get("billNumber"))%></td>
		</tr>
		
		<tr>
			<td class="myLabel">Client Address</td>
			<td class="myValue"><%=formatData(lData.get("entAddress"))%></td>
			<td class="myLabel">Orignal Invoice Date </td>
			<td class="myValue"><%=formatData(lData.get("billDate"))%></td>
		</tr>
			
		<tr>
			<td></td>
			<td ></td>
			<td class="myLabel">Revised Invoice No. </td>
			<td class="myValue">NA</td>
		</tr>
		<tr>
			<td></td>
			<td ></td>
			<td class="myLabel">Revised Invoice Date </td>
			<td class="myValue">NA</td>
		</tr>
		<tr>
			<td class="myLabel">Place Of Supply(STATE)</td>
			<td class="myValue"><%=formatData(lData.get("entStateDescription"))%></td>
			<td class="myLabel">State Code </td>
			<td class="myValue"><%=formatData(lData.get("entState"))%></td>
		</tr>
		<tr>
			<td class="myLabel">GSTIN Of Client</td>
			<td class="myValue"><%=formatData(lData.get("entGstn"))%></td>
			<td></td>
			<td ></td>
			
		</tr>		
		<tr>
			<td class="myLabel">PAN Of Client</td>
			<td class="myValue"><%=formatData(lData.get("entPan"))%></td>
			<td class="myLabel">Deemed Export</td>
			<td class="myValue">No</td>
		</tr>
		<tr>
			<td class="myLabel">Attention To.</td>
			<td class="myValue"><%=formatData(lData.get("entAdminFullName"))%></td>
		</tr>		
	</tbody>
</table>
<p>
</p>
<p>
</p>
<p>
<hr>
</hr>
</p>
<p>
</p>
<p>
</p>
<table class="myTable1">
	<tbody>
		<tr>
			<th width="2%">Sr. No.</th>
			<th width="10%"> Description</th>
			<th width="5%"> SAC</th>
			<th> Gross Value</th>
			<th width="5%">Abatement</th>
			<th width="5%">Receivers Liability</th>
			<th>Taxable Value</th>
			<th colspan="2">IGST</th>
			<th colspan="2">CGST</th>
			<th colspan="2">SGST</th>
			<th> Total</th>
		</tr>	
		<tr>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th>(Amt in Rs.)</th>
		</tr>	
		<tr>
			<td>1</td>
			<td><%=formatData(lData.get("tredsSACDesc"))%></td>
			<td><%=formatData(lData.get("tredsSACCode"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("chargeAmount"), true))%></td>
			<td>0</td>
			<td>0</td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("chargeAmount"), true))%></td>
			<td><%=formatData(lData.get("igst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("igstValue"), true))%></td>
			<td><%=formatData(lData.get("cgst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("cgstValue"), true))%></td>
			<td><%=formatData(lData.get("sgst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("sgstValue"), true))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("totalValue"), true))%></td>
		</tr>	
	</tbody>
</table>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<table class="myTable2">
	<tbody>
		<tr>
			<td class="myLabel1">Total</td>
			<td class="myValue1"><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("totalValue"), true))%></td>
		</tr>	
		<tr>
			<td class="myLabel1">Advance Received</td>
			<td class="myValue1"><%=formatData("0")%></td>
		</tr>	
		<tr>
			<td class="myLabel1">Balance Payable</td>
			<td class="myValue1"><%=formatData(TredsHelper.getInstance().getFormattedAmount(lData.get("totalValue"), true))%></td>
		</tr>
	</tbody>
</table>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<p>
</p>
<table class="myTable4">
<tbody>
		<tr>
			<td width="20%"></td>
			<td></td>
		</tr>
		<tr>
			<td class="myLabel">Rupees In Word:</td>
			<td><%=formatData((Object)CommonUtilities.getAmountInWords(Double.parseDouble(lData.get("totalValue").toString())))%></td>
		</tr>
		<tr>
			<td class="myLabel">Remark,if any</td>
			<td class="myValue">The above stated amount is being recovered from your account with each transaction and the bill were raised on monthy basis for the consolidated amount.</td>
		</tr>
	</tbody>
</table>

<table class="myTable3">
	<tbody>
		<tr>
			<td class="myLabel" width="30%">RXIL CIN No.</td>
			<td class="myValue"><%=formatData(lData.get("tredsCin"))%></td>
		</tr>
		<tr>
			<td class="myLabel">RXIL PAN No.</td>
			<td class="myValue"><%=formatData(lData.get("tredsPan"))%></td>
		</tr>
	</tbody>
</table>
<p>
</p>
<p>
</p>	
<p class="normal">
For Receivable Exchange Of India Ltd.<br></br><br></br>
sd/-
</p>
</div>
<div>
<p>
</p>	
<center><b><u>ANNEXURE</u></b></center>
<p>
</p>
<p>
</p>	
<table class="myTable1">
	<tbody>
		<tr>
			<th width="5%">Sr. No.</th>
			<th width="10%"> Factoring Unit Id</th>
			<th width="10%"> Invoice Number</th>
			<th width="10%"> FactoringUnit Amount</th>
			<th>Taxable Value</th>
			<th colspan="2">IGST</th>
			<th colspan="2">CGST</th>
			<th colspan="2">SGST</th>
			<th> Total</th>
		</tr>	
		<tr>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th> Rate </th>
			<th> Amt. </th>
			<th>(Amt in Rs.)</th>
		</tr>	
<%
int lPtr = 1;
for(Map<String, Object> lMap : lList){
%>
		<tr>	
			<td><%=lPtr++%></td>
			<td><%=formatData(lMap.get("fuId"))%></td>
			<td><%=formatData(lMap.get("invNumber"))%></td>
			<td><%=formatData(lMap.get("fuAmount"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lMap.get("charge"), true))%></td>
			<td><%=formatData(lMap.get("igst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lMap.get("igstValue"), true))%></td>
			<td><%=formatData(lMap.get("cgst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lMap.get("cgstValue"), true))%></td>
			<td><%=formatData(lMap.get("sgst"))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lMap.get("sgstValue"), true))%></td>
			<td><%=formatData(TredsHelper.getInstance().getFormattedAmount(lMap.get("totalValue"), true))%></td>
		</tr>
<%
}					
%>	
	</tbody>
</table>
</div>
<div class='footer'>
<center>
*This is a computer generated invoice and does not require any signature.
</center>
</div>

</body>

</html>