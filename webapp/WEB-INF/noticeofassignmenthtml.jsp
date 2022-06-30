
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
String lRupeeSymbol = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACx0lEQVRYR73XS6hXVRQG8J+PKB2YJDYIB80KRMIUFFJJMcIsQpwUVKMIZz5KahQEgShm1tCRVEYaBFGQ9qAHRYM0EoeC1CRr0gOpUMrik31gc7j3PO7/5oLNOWfvfdb3nb3X/tY6c3TbTuzC1TJtTnXNff3ceGr66uf0/Yblbbj25Hp8Fb7ETT0khw4/h/1jCHyK9fi7fOk8pHXZv7iEG1qr8xUewOUxBNbiD8TpfCzEEtyONdiI21oOM/dFHMWNZWwufsSvUzHv2oK+pV2K3XgWAWns50Lwhz4HGR9CYEEJwnxdWgIyLfexvWVva18vY89sEHgSz+AK/iktMZH7XNOfr99QtqnB/Anvl1hI3GRO2qNjYmAZviv7PuRj+ua8gcfHEHhtqhf6UKYZv4DVUwViVwzk2CT6s9eN6GQZs6TNc67ZjqdwXwX+DY6UsXR/i7OzfQpqf18jx7axqOcrQ1ZryCno87MF71UiFe1YifN9Lw49hl1+Eqgf445q0iE8PQR8EgKJg814qZVgPsL2IseDOHRtwc1YV2lApPVW3Il7W3sesBPYMZ3kTsemi8BdODMgAZ3DQeTYjrYuAncXArXTSHCSykWcxrs4hb9GI5cXuggksKKEdT3wJ7bhk+qMzxT72nt9xzAJJYFWW3L7/SVVTwQ+hEDmTCXJr+OJidEHrEAwFuHDkuNrzNSLr05Kom8LGv+Jhy/KMWz6EnhbkdJtxjaUQAAexDslxzeA3+OeUnLNiMQYAgFIZbuvhXQSDxfBGk1iLIHMfxOPtJAOlNrwfycQgMVFByJUjaVmCKnI8SgbuwKN8/zhfN4q134pOSLSPNhmSiAAUcS3W7kilc8m/D6UwSQEgvE8XmiBHcNj14tA6oLjpQaoMfPDcngIiUlXIBi34DOsqABTqD6ED/pIzAaBYKQGfKuQCXiq6YhUlDK/atPaf0nddiHYrwC4AAAAAElFTkSuQmCC";
String lTmpAdd = null;
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

body
{
	font-family:Arial;
	font-size: 12.5px;
}

@page { 
	 /*size: 8.50in 9.50in;*//* width height */
	 size: A4;   /* auto is the initial value */
	 margin-top: 15%;
	 margin-left:5%;
	 margin-right:5%;
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
    .pagebreak  { height:10px; border-top:0px dotted #999; margin-bottom:13px; }
}

div.indent{ padding-left: 1.8em }

</style>
</head>
<body>
<div class='header'>
<center>
<table border="0" cellspacing="10" cellpadding="0">
	<tr>
		<td style='text-align:left' width="20%"><img src="<%=TredsHelper.getInstance().getApplicationURL()%>../images/logo2x.png" width="108px" height="36px" border="0px" /></td>
		<td><h2>RECEIVABLES EXCHANGE OF INDIA LIMITED</h2></td>
	</tr>
</table>
</center>
</div>

<div style="margin:20px;">
	<p>
		<div>Date : <%=formatData(lData.get("settlementDate"))%></div>
		To,
		<%
			String[] lKeys = new String[] {"pName","pAdd1","pAdd2","pAdd3","pCity","pZipCode","pDistrict","pState" };
			for(int lPtr=0; lPtr < lKeys.length; lPtr++){
				lTmpAdd = (String)lData.get(lKeys[lPtr]);
				if(lPtr==4 && CommonUtilities.hasValue(lTmpAdd) && CommonUtilities.hasValue((String)lData.get(lKeys[lPtr+1]))) {
					lTmpAdd += " - " + (String)lData.get(lKeys[lPtr+1]);
				}else if (lPtr==5){
					continue;
				}		
				if(CommonUtilities.hasValue(lTmpAdd)){
		%>
			<div class="indent"><%=formatData(lTmpAdd)%></div> 
		<% 
				}
			}
		%>
		</p>
		
		<p>
		Dear Sir/ Madam,
		</p>
		<center>
		<p>
		<u><b>Notice of Assignment under Section 7 of the Factoring Regulation Act, 2011</b></u>
		</p>
		</center>
		<p>
		Please take this notice of assignment of all Receivables of <%=formatData(lData.get("sName"))%> (Seller) under the following invoice(s) which are due and payable by you to the seller and which have been factored  on the TREDS Platform of RXIL in terms of Master Agreement entered into by you with RXIL and the applicable Business Rules of RXIL in favour of the Financer as detailed below :
		</p>
		<p>
		This Notice of Assignment is in relation to the Invoices and Amount thereof:
		</p>
		
		<table class="mytable">
			<thead>
				<th width="150px">Invoice number</th>
				<th width="120px">Invoice date</th>
				<th width="150px">Invoice amount (Rs)</th>
				<th width="150px">Factoring unit id</th>
				<th width="100px">Factoring unit date</th>
				<th width="150px">Factored amount (Rs)</th>
				<th width="100px">Factored Amount Due date</th>
			</thead>
			<tbody>
		<%
		List<Map<String,Object>> lFUnits = (List<Map<String,Object>>)lData.get("fUnits");
		for (Map<String,Object> lFUnit : lFUnits) {
		%>
			<%
			List<Map<String,Object>> lChilds = (List<Map<String,Object>>)lFUnit.get("childList");
			int lPtr=0;
			boolean lChildflag = false;
			if (lChilds!= null && lChilds.size()>0){
			for (Map<String,Object> lChild : lChilds) {
			%>
				<tr<%if(lPtr==0){%> height="35" <%}%>>
					<td ><%=formatData(lChild.get("invoiceNo"))%></td>
					<td align="right"><%=formatData(lChild.get("invoiceDate"))%></td>
					<td align="right"><%=formatData(lChild.get("invoiceAmt"))%></td>
					<td align="center"><%=formatData(lChild.get("fuId"))%></td>
					<td align="right"><%=formatData(lChild.get("fuDate"))%></td>
					<td align="right"><%=formatData(lChild.get("fuAmt"))%></td>
					<td align="right"><%=formatData(lChild.get("fuDueDate"))%></td>
				</tr>
			<%
				lPtr++;
				lChildflag = true;
			}
			}
			%>
			<%	
				if(!lChildflag){
			%>
				<tr >
					<td><%=formatData(lFUnit.get("invoiceNo"))%></td>
					<td align="right"><%=formatData(lFUnit.get("invoiceDate"))%></td>
					<td align="right"><%=formatData(lFUnit.get("invoiceAmt"))%></td>
					<td align="center"><%=formatData(lFUnit.get("fuId"))%></td>
					<td align="right"><%=formatData(lFUnit.get("fuDate"))%></td>
					<td align="right"><%=formatData(lFUnit.get("fuAmt"))%></td>
					<td align="right"><%=formatData(lFUnit.get("fuDueDate"))%></td>
				</tr>
			<%
			}
			%>
				
			<%
			}
			%>
			</tbody>
		</table>
		<br></br>
		<p>Please take notice that the Factoring Unit as above stands assigned consequent upon financing/ refinancing on it in terms of the aforesaid Master Agreement:</p>
		<br></br>
		<% if (lData.get("sDesigBank")!=null){%>
		<p><u><b>Assignor</b></u></p>
    	<table class="mytable">
	        <thead>
	            <th>Name of Assignor</th>
	            <th>Registered Address</th>
	        </thead>
	        <tr>
	            <td>
	            	<p><%=formatData(lData.get("sName")) %></p>
	            	<p>Bank : <%=formatData(lData.get("sDesigBank")) %></p>
	            	<p>Type :<%=formatData(lData.get("sDesigBAnkAccType")) %></p>
	            	<p>Email :<%=formatData(lData.get("sDesigBankEmail")) %></p>
	            </td>
	            <td><%=formatData(lData.get("sAdd")) %></td>
	        </tr>
		</table>	
		<br></br>
		<% }%>
		<p>
		<b><u>Factor / Assignee</u></b>
		</p>
		<table class="mytable">
			<thead>
				<th width="100px">Name of Factor/Assignee</th>
				<th width="200px">Registered Address</th>
				<th width="150px">Bank Account No</th>
				<th width="200px">Bank n Branch</th>
				<th width="100px">IFSC Code</th>
			</thead>
			<tbody>
		<%
				int lTotal = 0;
				lTotal = Integer.parseInt(lData.get("fBankCount").toString());
				for(int lPtr=0; lPtr < lTotal; lPtr++){
		%>
				<tr>
					<td><%=formatData(lData.get("fName"))%></td>
					<td><%=formatData(lData.get("fAdd"))%></td>
					<td><%=formatData(lData.get("fBankAcNo"+lPtr))%></td>
					<td><%=formatData(lData.get("fBankBranch"+lPtr))%></td>
					<td><%=formatData(lData.get("fBankIFSC"+lPtr))%></td>
				</tr>
		<%
				}
		%>
			</tbody>
		</table>
		<br></br>
    	<p>Please take note that by virtue of the assignment as above the receivables under the above mentioned Invoices stand assigned to the Factor/Assignee- </p>
        <ul>
            <li>
                the Assignor has ceased to be the owner of the above mentioned invoices and the factored amount and the Assignee is constituted as the owner of the invoices and the factored amount in place of the Assignor.
            </li>
            <li>The Assignee shall have exclusive right to recover the Factored Amount from the Buyer and in case Buyer defaults then from the Assignor.
            </li>
            <li>You <%=formatData(lData.get("pName"))%> shall arrange to pay the factored amount on the due date(s) to the Factor/Assignee.</li>
            <li>You <%=formatData(lData.get("pName"))%> confirm that payment to the Factor/Assignee for these receivables shall alone constitute a good and valid discharge.</li>
        </ul>	
		<br></br>
		    <p>Please also note that Debit for the factored amount along with interest (if not recovered upfront) will be effected on the respective due date from the designated bank account given by you <%=formatData(lData.get("pName"))%> to RXIL and credit shall be given to the bank account of the Factor/ Assignee.</p>
		<br></br>
		    <p>The Assignment of the above receivables to the Factor/Assignee will be governed and regulated under the Factoring Regulation Act, 2011 including any statutory amendments thereto.</p>
		<br></br>
		    <p>Kindly keep sufficient balance in your designated bank account on due date(s) for repayment of above invoice(s) and interest (if any). Charge against a particular invoice out of all the above invoice shall be released only after successful payment/credit of the factored amount (along with interest, if any) in the above account of the Factor/Assignee.</p>
		<br></br>
		    <p>All the details in respect to particular Factoring Unit is available on the TReDS platform.</p>
		<br></br>
		<p>
		<b>Receivables Exchange of India Ltd</b>
		</p>
		<p>
		<b>(For and on behalf of the Seller)</b>
		</p>
			
</div>




<div class='footer'>
<center>
This is a computer generated letter and does not require any signature.
</center>
</div>

</body>

</html>