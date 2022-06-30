<%@page import="com.xlx.treds.instrument.bean.GstSummaryBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.xlx.treds.TredsHelper"%>
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
String lRupeeSymbol = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACx0lEQVRYR73XS6hXVRQG8J+PKB2YJDYIB80KRMIUFFJJMcIsQpwUVKMIZz5KahQEgShm1tCRVEYaBFGQ9qAHRYM0EoeC1CRr0gOpUMrik31gc7j3PO7/5oLNOWfvfdb3nb3X/tY6c3TbTuzC1TJtTnXNff3ceGr66uf0/Yblbbj25Hp8Fb7ETT0khw4/h/1jCHyK9fi7fOk8pHXZv7iEG1qr8xUewOUxBNbiD8TpfCzEEtyONdiI21oOM/dFHMWNZWwufsSvUzHv2oK+pV2K3XgWAWns50Lwhz4HGR9CYEEJwnxdWgIyLfexvWVva18vY89sEHgSz+AK/iktMZH7XNOfr99QtqnB/Anvl1hI3GRO2qNjYmAZviv7PuRj+ua8gcfHEHhtqhf6UKYZv4DVUwViVwzk2CT6s9eN6GQZs6TNc67ZjqdwXwX+DY6UsXR/i7OzfQpqf18jx7axqOcrQ1ZryCno87MF71UiFe1YifN9Lw49hl1+Eqgf445q0iE8PQR8EgKJg814qZVgPsL2IseDOHRtwc1YV2lApPVW3Il7W3sesBPYMZ3kTsemi8BdODMgAZ3DQeTYjrYuAncXArXTSHCSykWcxrs4hb9GI5cXuggksKKEdT3wJ7bhk+qMzxT72nt9xzAJJYFWW3L7/SVVTwQ+hEDmTCXJr+OJidEHrEAwFuHDkuNrzNSLr05Kom8LGv+Jhy/KMWz6EnhbkdJtxjaUQAAexDslxzeA3+OeUnLNiMQYAgFIZbuvhXQSDxfBGk1iLIHMfxOPtJAOlNrwfycQgMVFByJUjaVmCKnI8SgbuwKN8/zhfN4q134pOSLSPNhmSiAAUcS3W7kilc8m/D6UwSQEgvE8XmiBHcNj14tA6oLjpQaoMfPDcngIiUlXIBi34DOsqABTqD6ED/pIzAaBYKQGfKuQCXiq6YhUlDK/atPaf0nddiHYrwC4AAAAAElFTkSuQmCC";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
<title>Issue Details</title>
<style type="text/css">
.tblbig
{
	width:650px;
}
body
{
	font-size:12px;
    font-family:Arial,Trebuchet MS,Verdana;
}

@CHARSET "ISO-8859-1";
@media screen {
    .pagebreak  { height:10pt; border-top:1pt dotted #999; margin-bottom:13pt;}
}
@media print {
    .pagebreak { height:0; page-break-before:always; margin:0; border-top:none; }
}
@page { 
 /*size: 8.50in 9.50in;*//* width height */
 size: A4;   /* auto is the initial value */
 margin-top: 40pt;
 margin-left:1%;
 margin-right:1%;
 border: 1pt solid black;
    @bottom-left {
        content: element(footer);
    }
    @bottom-center {
        font-style: italic; 
        font-size:11pt;
        font-family:Arial,Trebuchet MS,Verdana;
        color:#ccc;
        content: "RXIL";
    }
    @top-center {
    content: element(current);
    text-align:center;
  }
}
body {
  counter-reset: page;           /* Set the section counter to 0 */
  line-height: 125%;
  font-size: 10pt;
  font-family:Arial,Trebuchet MS,Verdana;
}
table { 
    -fs-table-paginate: paginate;
    -fs-keep-with-inline: keep;
    border-spacing: 0pt;
    box-shadow: none;
    border-collapse: collapse;
    word-wrap: break-word;
}
td,th
{
    box-shadow: none;
    border-collapse: collapse;
    word-wrap: break-word; 
}
p
{
    word-wrap: break-word; 
    text-align: justify;
}
td.numbercol
{
    text-align:right;
    padding-right:2pt;
}
.small {/*font-size: xx-small;*/font-size: 10pt;color:#ccc;}
.footerPageNo{color:#666;}
#page-footer{text-align: left;}
span.page:before { content: counter(page); }
span.pagecount:before { content: counter(pages); }
/*Common css ends*/
.sectionHeader
{
	font-size: 11pt;
	font-weight: bold;
	padding-top: 2pt;
	padding-bottom: 2pt;
}
.highlight{color:#365F91;}
.tableHeader
{
	width:100%;
}
.tableHeader img
{
	opacity:0.4;
    max-height: 45pt;
    max-width: 45pt;
    height: 45pt;
}
.tableHeader td
{
	height: 50pt;
	vertical-align: middle;
}
.mainTable .mainHeader
{
	text-align: right;
	padding-left: 45pt;
	font-size: 12pt;
    width:90%;
}
.mainTable.imageLogo
{
    width:10%;
}

.mainTable .fldval
{
    padding:0px 5px 0px 5px;
}

.mainTable .fldlbl 
{
    font-weight: bold;
    padding:5px 5px 10px 5px;
}
.mainTable .fldhr 
{
	font-size:0px;
	border-top:1px solid #eee;
}
.mainTable .fldent {
	min-height:40px;
}

.mytable {
    border-collapse: collapse;
}

.mytable th{
    border: 0.5px solid black;
}
.mytable td {
    border: 0.5px solid black;
    padding:2px 2px 5px 2px;
}


</style>
</head>
<body>
<table border="0" cellspacing="5" cellpadding="0" width="100%"  class="mainTable">
	<thead>
        <tr>
            <th colspan='2'>
                <table border="0" cellspacing="0" cellpadding="0" class="tableHeader" >
                    <tbody>
                        <tr>
                            <td class='imageLogo'><img src="<%=TredsHelper.getInstance().getApplicationURL()%>../images/logo2x.png" width="108px" height="36px" border="0px" /></td>
                            <td class='mainHeader' colspan='2'><h2>RECEIVABLES EXCHANGE OF INDIA LIMITED</h2></td>
                        </tr>
                    </tbody>					
                </table>
            </th>
        </tr>
    </thead>

	<tr>
		<td class="fldhr" colspan="2"></td>
	</tr>
	<tr>
		<td class="fldlbl" style="width:50%">Factoring Unit:</td><td class="fldval" style="width:50%"><%=lData.get("fuId")%></td>
	</tr>
	<tr>
		<td class="fldlbl">Settlement Date:</td><td class="fldval"><%=lData.get("date")%></td>
	</tr>
	<tr>
		<td class="fldhr" colspan="2"></td>
	</tr>
	<tr>
		<td class="fldlbl">Factored Amount:</td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lData.get("amt"))%></td>
	</tr>
	<tr>
		<td class="fldlbl">Interest Charge:</td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lData.get("cost"))%></td>
	</tr>
	<% List<Map<String,Object>> lGstSummaryList = (List<Map<String,Object>>) lData.get("chgSumm");
	
	 for (Map<String,Object> lGstData : lGstSummaryList){
		 
	 %>
	<tr>
		<td class="fldlbl">Transaction Charges:<%=lGstData.get("ent")%></td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lGstData.get("chg"))%></td>
	</tr>
	<% if(lGstData.get("cgstValue")!=null && !lGstData.get("cgstValue").equals(BigDecimal.ZERO)){ %>
	<tr>
		<td class="fldlbl">CGST <%=formatDecimal(lGstData.get("cgst"))%>% + <%=formatDecimal(lGstData.get("cgstSurcharge"))%>% Surcharge:</td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lGstData.get("cgstValue"))%></td>
	</tr>
	<% } %>
	<% if(lGstData.get("sgstValue")!=null && !lGstData.get("sgstValue").equals(BigDecimal.ZERO)){ %>
	<tr>
		<td class="fldlbl">SGST <%=formatDecimal(lGstData.get("sgst"))%>% + <%=formatDecimal(lGstData.get("sgstSurcharge"))%>% Surcharge:</td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lGstData.get("sgstValue"))%></td>
	</tr>
	<% } %>
	<% if(lGstData.get("igstValue")!=null && !lGstData.get("igstValue").equals(BigDecimal.ZERO)){ %>
	<tr>
		<td class="fldlbl">IGST <%=formatDecimal(lGstData.get("igst"))%>% + <%=formatDecimal(lGstData.get("igstSurcharge"))%>% Surcharge:</td><td class="fldval"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lGstData.get("igstValue"))%></td>
	</tr>
	<% } %>
	
	<% } %>
	<tr>
		<td class="fldhr" colspan="2"></td>
	</tr>
	<tr>
		<td class="fldlbl">Debits</td><td class="fldlbl">Credits</td>
	</tr>
	<tr>
		<td class="fldhr"></td><td class="fldhr"></td>
	</tr>
	<tr>
		<td class="fldhr"></td><td class="fldhr"></td>
	</tr>
	<tr>
		<td style="vertical-align:top">
			<table border="0" style="width:100%">
<%
List<Map<String,Object>> lDebits = (List<Map<String,Object>>)lData.get("d");
for (Map<String,Object> lEntry : lDebits) {
%>
				<tr><td class="fldval fldent" style="width:50%"><%=formatData(lEntry.get("ename"))%> (<%=formatData(lEntry.get("e"))%>)</td>
				<td class="fldval fldent" style="width:50%;text-align:right"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lEntry.get("a"))%> <i><%=formatData(lEntry.get("n"))%></i></td></tr>
				<tr><td class="fldhr" colspan="2"></td></tr>
<%
}
%>
			</table>
		</td>
		<td style="vertical-align:top">
			<table border="0" style="width:100%">
<%
List<Map<String,Object>> lCredits = (List<Map<String,Object>>)lData.get("c");
for (Map<String,Object> lEntry : lCredits) {
%>
				<tr><td class="fldval fldent" style="width:50%"><%=formatData(lEntry.get("ename"))%> (<%=formatData(lEntry.get("e"))%>)</td>
				<td class="fldval fldent" style="width:50%;text-align:right"><img width="10px" height="10px" src="<%=lRupeeSymbol%>"/> <%=formatDecimal(lEntry.get("a"))%> <i><%=formatData(lEntry.get("n"))%></i></td></tr>
				<tr><td class="fldhr" colspan="2"></td></tr>
<%
}
%>
			</table>
		</td>
	</tr>


	<%
if(lData.containsKey("txnList")){
List<Map<String,Object>> lTxnList = (List<Map<String,Object>>)lData.get("txnList");
if(!lTxnList.isEmpty()){
%>
<tr class="pagebreak" >

<td colspan="4">
<h3><center>Settlement Details</center></h3><h4> <center>Factoring Unit <%=lData.get("fuId")%> </center></h4>
	<table  id="txnDetails" width="100%" class="mytable">
	<thead>
	<tr class="tableHeader" >
		<th width="30%"> Entity</th>
		<th width="15%" align="right"> Settled amt.  </th>
		<th width="15%" align="center"> Status</th>
		<th width="25%" align="right"> Payment ref. no. </th>
		<th width="15%" align="center"> Settlement date </th>
	</tr>
	</thead>
<%
for (Map<String,Object> lTxnData: lTxnList) {
%>
							<tr>
								<td ><%=lTxnData.get("entity") %></td>
								<td  align="right"><%=formatDecimal(lTxnData.get("amt"))%> </td>
								<td align="center"><%=lTxnData.get("status") %> </td>
								<td  align="right"><%=lTxnData.get("payRefNo") %> </td>
								<td align="center"><%=lTxnData.get("date") %> </td>
							</tr>
<%
}
%>
	</table>
	</td>
	</tr>
<%
}
}
%>
	
</table>	


</body>
</html>