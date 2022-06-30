<%@page import="com.xlx.common.utilities.FormatHelper"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="com.xlx.treds.monetago.bean.MonetagoEwaybillVehicleListDetailsBean"%>
<%@page import="com.xlx.treds.monetago.bean.MonetagoEwaybillItemListBean"%>
<%@page import="com.xlx.treds.monetago.bean.EwayInstrumentWrapperBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Objects"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
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
public BigDecimal formatGstRate (Object pData) {
	BigDecimal lBigDecimal = new BigDecimal(0);
    if (pData != null) {
    	if (! pData.equals(new BigDecimal(-1))){
    		lBigDecimal = (BigDecimal)pData;
    	}
    }
    return lBigDecimal;
}
%>
<%
SimpleDateFormat lDateTimeFormatter = BeanMetaFactory.getInstance().getDateTimeFormatter();
String lContextPath=request.getContextPath();
EwayInstrumentWrapperBean lBean =  (EwayInstrumentWrapperBean) request.getAttribute("data");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >

<html>
<head>
<!-- <link href="../../css/reporthtml.css" rel="stylesheet" /> -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Tax Invoice</title>
<style type="text/css">
body
{
	font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;
	font-size: 11.0px; 
	color:#444;
}
.tab {
	margin: 0px 0px 0px 2px;
	width: 100%;
	border-collapse: collapse;
	border-spacing: 0;
	text-align: left;
	page-break-inside: avoid;
	-fs-table-paginate: paginate;
	-fs-keep-with-inline: keep;
}
.data th{
	border-top: 0.4pt solid black !important;
	border-right: 0.4pt solid black !important;
	border-bottom: 0.4pt solid black !important;
	border-left: 0.4pt solid black !important;
	padding: 3px, 10px;
}
.total{
	margin: 0px 0px 20px 2px;
	border-collapse: collapse;
	border-spacing: 0;
	text-align: left;
	page-break-inside: avoid;
}
.total tr td:first-child{
	border-left: 0.4pt solid black !important;
}
.total tr td:last-child{
	border-right: 0.4pt solid black !important;
}
.total td{
	border-bottom: 0.4pt solid black !important;
}

.mainData th{
	border-top: 0.4pt solid black !important;
	border-right: 0.4pt solid black !important;
	border-bottom: 0.4pt solid black !important;
	border-left: 0.4pt solid black !important;
	padding: 3px, 10px;
}
.mainData td:first-child{
	border-left: 0.4pt solid black !important;
	padding: 2px, 4px;
	vertical-align: top;
}
.mainData td{
	border-right: 0.4pt solid black !important;
	border-left: 0.4pt solid black !important;
	padding: 2px, 4px;
	vertical-align: top;
}
.mainData tr:last-child td{
	border-bottom: 0.4pt solid black !important;
	vertical-align: top;
}
.data td{
	border-right: 0.4pt solid black !important;
	border-bottom: 0.4pt solid black !important;
	border-left: 0pt solid black !important;
	border-top: 0pt solid black !important;
	padding: 2px, 4px;
	vertical-align: top;
}
.data td:first-child{
	border-left: 0.4pt solid black !important;
}
.headerTable {
	border: 0.4pt solid black !important;
}
.invTab td:first-child{
	border-right: 0.4pt solid black!important;
	border-bottom: 0.4pt solid black !important;
}
.invTab td:last-child{
	border-bottom: 0.4pt solid black !important;
}
@media screen {
    .pagebreak  { height:10px; border-top:0.5px dotted #999; margin-bottom:13px; }
}
@media print {
    .pagebreak { height:0; margin:0; border-top:none; }
}
@page {	
	size: A4;	
	margin-left: 15px;
	margin-right: 15px;
	margin-top: 250px;
	margin-bottom: 200px;
	background-position: right top;
	
	@top-left {
    	content: element(pageHeader);
  	}
  	
  	@bottom-left {
    	content: element(pageFooter);
  	}  
}

#pageHeader {
 	position: running(pageHeader); 
 	margin-left: 8.9px;	
 	margin-right: 8.9px;
}

#pageFooter {	
	position: running(pageFooter);
	width: 100%;
	height: 200px;	
}

#pageFooter div {
	background-position: left bottom;
	background-repeat: no-repeat;
	width: 100%;
	font-size: 5px !important; 
}

</style>
</head>
<body>
	<header id="pageHeader">
	<h1><center><b><u>e-Invoice</u></b></center></h1>
	<br></br>
		<table  class="headerTable" width="100%">
			<tr>
				<td  width="50%" style="border-right:0.4pt solid black !important;">	
					<table style="width: 100%; height:100%">
						<tr style="border-bottom: 0.4pt solid black !important;">
							<td valign="top" style="border-bottom:0.4pt solid black !important;" >
								<table>
									<tr>
										<td style="text-align: left">
										<b>Supplier</b><br></br>
										<%=formatData(lBean.getConsignerDetSellerName())%><br></br>
										<%=formatData(lBean.getSellerEwayAddress())%><br></br>
										GSTN : <%=formatData(lBean.getSellerGstn())%><br></br>
										State Name:<%=formatData(lBean.getSupplierState())%> Code: <%=formatData(lBean.getSupplierStateCode())%><br></br>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td valign="top">
								<table >
									<tr>
										<td style="text-align: left">
											<b>Purchaser</b><br></br>
											<%=formatData(lBean.getConsignerDetPurchaserName())%><br></br>
											<%=formatData(lBean.getPurchaserEwayAddress())%><br></br>
											GSTN : <%=formatData(lBean.getPurchaserGstn())%><br></br>
											State Name:<%=formatData(lBean.getPurchaserState())%> Code: <%=formatData(lBean.getPurchaserStateCode())%><br></br>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
				<td width="50%" valign="top" >
					<table style="height: 100%; width: 100%; text-align: top" class="invTab">
						<tr>
							<td style="text-align: left">
							<b>Invoice No.</b><br></br>
							<%=formatData(lBean.getDocNo())%><br></br>
							</td>
							<td style="text-align: left">
							<b>Invoice Dated.</b><br></br>
							<%=formatData(lBean.getInvoiceDate())%><br></br>
							</td>
						</tr>
						<tr>
							<td style="text-align: left">
							<b>E Way of Client</b><br></br>
							<%=formatData(lBean.getEwayBillNo())%><br></br>
							</td>
							<td style="text-align: left">
							<b>Vehicle No.</b><br></br>
							<%=formatData(lBean.getVeichleNo())%><br></br>
							</td>
						</tr>
						<tr>
							<td style="text-align: left">
							<b>E Way Bill valid from</b><br></br>
							<%=formatData(FormatHelper.getDisplay("dd-MM-yyyy hh:mm a",lBean.getEwayBillValidFrom()))%><br></br>
							</td>
							<td style="text-align: left">
							<b>E Way Bill valid until</b><br></br>
							<%=formatData(FormatHelper.getDisplay("dd-MM-yyyy hh:mm a",lBean.getEwayBillValidUpto()))%><br></br>
							</td>
						</tr>
						<tr>
							<td rowspan="2"  valign="top" style="border-right:0.4pt solid !important;border-bottom:0pt solid !important">
							<b>Terms of Delivery</b>
							</td><td style="text-align: left">
							<b>Po No.</b><br></br>
							<%=formatData(lBean.getPoNumber())%><br></br>
							</td>
						</tr>
						<tr>
							<td style="text-align: left; border-right:0pt solid !important;border-bottom:0pt solid !important" >
							<b>Po Dated.</b><br></br>
							<%=formatData(lBean.getPoDate())%><br></br>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</header>
	<div id="pageContent">
	<table style="width: 100%; height:400px;" class="mainData tab" cellpadding="0" cellspacing="0">
	<thead>
		<tr >
			<th width="3%">Sr no</th>
			<th width="42%"  > Product Name-Desc [Code]</th>
			<th width="15%"  > HSN code</th>
			<th width="5%"  >GST Rate</th>
			<th width="5%"  >Qty.</th>
			<th width="5%"  >Cess Non Advol</th>
			<th width="5%"  >Cess Rate</th>
			<th width="20%"  >Taxable amount</th>
		</tr>	
    </thead>

	<tbody style="text-align: top "  >
<%-- 			<% for(int lPtr=0; lPtr <= 3 ; lPtr++) { %> --%>
			<% for (MonetagoEwaybillItemListBean lEwayInfoBean : lBean.getItems()){ %>	
			<tr>
				<td valign="top" style="text-align: center" > <%=formatData(lEwayInfoBean.getItemNo())%></td>
				<td valign="top" style="text-align: left"  ><b><%=formatData(lEwayInfoBean.getProductName())%> - <%=formatData(lEwayInfoBean.getProductDesc())%> [<%=formatData(lEwayInfoBean.getProductId())%>] </b> </td>
				<td valign="top" style="text-align: left" ><%=formatData(lEwayInfoBean.getHsnCode())%></td>
				<td valign="top" style="text-align: center"   ><%=formatData(lEwayInfoBean.getTotalGstRate())%></td>
				<td valign="top" style="text-align: center"  ><b><%=formatData(lEwayInfoBean.getQuantity())%> <%=formatData(lEwayInfoBean.getQtyUnit())%></b></td>
				<td valign="top" style="text-align: right" ><%=formatData(lEwayInfoBean.getCessNonAdvol())%></td>
				<td valign="top" style="text-align: center"  ><%=formatData(lEwayInfoBean.getCessRate())%></td>
				<td valign="top" style="text-align: right" ><b><%=formatDecimal(lEwayInfoBean.getTaxableAmount())%></b></td>
			</tr>	
			<% } %>
<%-- 			<% } %> --%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  > Total </td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right; border-top:0.4pt solid black !important;" ><%= formatDecimal(lBean.getTotalTaxableValue())%> </td>
			</tr>
			<%if (lBean.getCgstValue().compareTo(new BigDecimal(0)) == 1){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>Central Tax (CGST)</b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getCgstValue())%></b></td>
			</tr>
			<%}%>
			<%if (lBean.getSgstValue().compareTo(new BigDecimal(0)) == 1){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>State Tax (SGST)</b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getSgstValue())%></b></td>
			</tr>
			<%}%>
			<%if (lBean.getIgstValue().compareTo(new BigDecimal(0)) == 1){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>Integrated Tax (IGST)</b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getIgstValue())%></b></td>
			</tr>
			<%}%>
			<%if (lBean.getCessValue().compareTo(new BigDecimal(0)) == 1){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>Cess </b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getCessValue())%></b></td>
			</tr>
			<%}%>
			<%if (lBean.getCessNonAdvolValue().compareTo(new BigDecimal(0)) == 1){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>Cess Non Advol Value</b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getCessNonAdvolValue())%></b></td>
			</tr>
			<%}%>
			<%if (lBean.getRoundOff().compareTo(new BigDecimal(0)) != 0){%>
			<tr>
				<td valign="top" style="text-align: center" > </td>
				<td valign="top" style="text-align: right"  ><b>Round Off</b></td>
				<td valign="top" style="text-align: left" ></td>
				<td valign="top" style="text-align: center"   ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ></td>
				<td valign="top" style="text-align: center"  ></td>
				<td valign="top" style="text-align: right" ><b><%= formatDecimal(lBean.getRoundOff())%></b></td>
			</tr>
			<%}%>
 	</tbody>
 	</table>
 	<table width="100%" class="total" cellpadding="0" cellspacing="0">
 		<tr style="height:30px" >
			<td width="81.1%" style=" padding-left:6px "   > Total </td>
			<td width="18.9%" style="text-align: right; padding-right:6px " > <b><%= formatDecimal(lBean.getTotalInvoiceValue())%></b> </td>
		</tr>
 		<tr>
 			<td  valign="top"  height="30px" style=" padding-left:6px "  >
 				Amount Chargable in (words) : <br></br>
 				<br></br>
 				<b><%=CommonUtilities.getAmountInWords(lBean.getTotalInvoiceValue().doubleValue())%><b>
			</td>
			<td style="text-align: right; padding-right:6px "  >
				<i>E. & O.E</i>
			</td>
		</tr>
 	</table>




		 			<table class="data" cellpadding="0" cellspacing="0" >
							<tr>
								<th width="10%"  >Entered Date</th>
								<th width="5%"  >From Place</th>
								<th width="20%"  >From State</th>
								<th width="5%"  >Group No.</th>
								<th width="15%"  >Trans Doc Date</th>
								<th width="5%"  >Trans Doc No.</th>
								<th width="5%"  >Trans Mode</th>
								<th width="5%"  >Tripsht No.</th>
								<th width="10%"  >UPD Mode</th>
								<th width="10%"  >User GSTIN Transin</th>
								<th width="10%"  >Vehicle No.</th>
							</tr>	
							<% for (MonetagoEwaybillVehicleListDetailsBean lEwayVehicleBean : lBean.getVehicleDetails()){ %>	
							<tr>
								<td valign="top"  ><%=formatData(FormatHelper.getDisplay("dd-MM-yyyy hh:mm a",lEwayVehicleBean.getEnteredDate()))%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getFromPlace())%></td>
								<td valign="top"  ><%=formatData(TredsHelper.getInstance().getGSTStateDesc(lEwayVehicleBean.getFromState().toString()))%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getGroupNo())%></td>
								<td valign="top"  ><%=formatData(FormatHelper.getDisplay("dd-MM-yyyy",lEwayVehicleBean.getTransDocDate()))%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getTransDocNo())%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getTransModeDesc())%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getTripshtNo())%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getUpdMode())%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getUserGSTINTransin())%></td>
								<td valign="top"  ><%=formatData(lEwayVehicleBean.getVehicleNo())%></td>
							</tr>	
							<% } %>	
					</table>




	</div>
	<footer id="pageFooter">
	<table class="page-footer" width="100%" style="padding-right: 5px;">
      <tr>
        <td width="50%" >
        </td>
        <td width="50%" style="border:0.4pt solid black !important; text-align: right">
        	For <%=formatData(lBean.getConsignerDetSellerName())%>
        	<br></br>Authenticated By RXIL-TReDS from NIC Eway Bill portal
        	<br></br>and the necessary consent has been taken from the above SUPPLIER
        	<br></br><br></br><br></br><br></br><br></br>Authorised Signatory
        </td>
      </tr>
      <tr>
        <td >
        </td>
        <td style="text-align: right">
        	<span> Data extracted from : <a href="https://ewaybill.nic.in"> https://ewaybill.nic.in </a> On : <%= FormatHelper.getDisplay("dd-MM-yyyy HH:mm:ss",lBean.getInvoiceFetchTime())%></span>
        </td>
      </tr>
    </table>
	</footer>
</body>
</html>