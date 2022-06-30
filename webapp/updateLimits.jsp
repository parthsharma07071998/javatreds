<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.treds.auction.bean.FinancierAuctionSettingBean"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.xlx.commonn.GenericDAO"%>
<%@page import="com.xlx.common.base.GenericBean"%>
<%@page import="com.xlx.treds.instrument.bean.FactoringUnitBean"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.xlx.common.utilities.DBHelper"%>
<%@page import="com.xlx.treds.TredsHelper"%>
<%
String lUpdateStr = request.getParameter("update");
boolean lUpdate = false;
if(StringUtils.isNotEmpty(lUpdateStr)){
	lUpdate = (lUpdateStr.equals("Y"));
}
//String lSql = "select fulimitids, sum(FULIMITUTILIZED) FULIMITUTILIZED from factoringunits where FURECORDVERSION > 0 AND FUSTATUS IN ('FACT','L1SET','L2FAIL')  group by fulimitids";
String lSql = "select fulimitids, sum(FULIMITUTILIZED) FULIMITUTILIZED , sum(FULIMITUTILIZED) FUPURSUPLIMITUTILIZED from factoringunits ";
lSql += " where FURECORDVERSION > 0 AND FUSTATUS IN ('FACT','L1SET','L2FAIL') group by fulimitids ";
lSql += " UNION ALL ";
lSql += " SELECT 	BDLIMITIDS AS fulimitids,SUM(BDLIMITUTILISED) FULIMITUTILIZED,SUM(BDBIDLIMITUTILISED) FUPURSUPLIMITUTILIZED ";
lSql += " FROM BIDS, FACTORINGUNITS WHERE BDFUID = fuid AND FUSTATUS  = 'ACT' AND BDSTATUS = 'ACT' AND BDRATE IS NOT NULL AND BDBIDTYPE = 'RES' GROUP BY BDLIMITIDS ";

Connection lConnection =  DBHelper.getInstance().getConnection();
GenericDAO<FactoringUnitBean> factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
List<FactoringUnitBean> lFUBeans = factoringUnitDAO.findListFromSql(lConnection, lSql.toString(), -1);
Map<Long, BigDecimal> lFASLimits = new HashMap<Long, BigDecimal>();//key=LimitId, value=CumulativeLimitAmount 
Map<Long, BigDecimal> lFASBidLimits = new HashMap<Long, BigDecimal>();//key=LimitId, value=CumulativeBidLimitAmount 
String lUpdateSql = "";
%>
<html>
<body>
<%
if(lFUBeans != null){
	String[] lLimitIds = null;
	Long lLimitId = null;
	BigDecimal lTotalLimit = BigDecimal.ZERO, lTotalBidLimit = BigDecimal.ZERO;
	%>
	 Total Records fetched = <%=lFUBeans.size()%>
	 <br> <br>
	<% 
	for(FactoringUnitBean lFUBean : lFUBeans  ){
		if(CommonUtilities.hasValue(lFUBean.getLimitIds())){
			lLimitIds = lFUBean.getLimitIds().split(",");
			if(lLimitIds!=null){
				for(int lPtr=0; lPtr < lLimitIds.length; lPtr++){
					if(CommonUtilities.hasValue(lLimitIds[lPtr])){
						lLimitId = new Long(lLimitIds[lPtr]);
						//
						if(!lFASLimits.containsKey(lLimitId)){
							lTotalLimit = BigDecimal.ZERO;	
						}else {
							lTotalLimit = lFASLimits.get(lLimitId);
						}
						lTotalLimit = lTotalLimit.add(lFUBean.getLimitUtilized());
						lFASLimits.put(lLimitId, lTotalLimit);
						//
						if(!lFASBidLimits.containsKey(lLimitId)){
							lTotalBidLimit = BigDecimal.ZERO;	
						}else {
							lTotalBidLimit = lFASBidLimits.get(lLimitId);
						}
						lTotalBidLimit = lTotalBidLimit.add(lFUBean.getLimitUtilized());
						lFASBidLimits.put(lLimitId, lTotalBidLimit);
						//
					}
				}
			}
		}
	}
}
%>
<h1>---<%=lFASLimits.size() %>- <%=lFASBidLimits.size() %></h1>
<table border="1">
<tr>
	<th>Limit Id</th>
	<th>FAS Level</th>
	<th>FAS Fin</th>
	<th>FAS Buy</th>
	<th>FAS Sell</th>
	<th>Calculated Limit</th>
	<th>Limit in Db</th>
	<th>Limit Diff</th>
	<th>Calculated Bid Limit</th>
	<th>Bid Limit in Db</th>
	<th>Bid Limit Diff</th>
	<th>UPDATE FACTORINGUNITS SET FULIMITUTILIZED = FUFACTOREDAMOUNT+FUPURCHASERLEG2INTEREST WHERE FURECORDVERSION > 0 AND FUSTATUS IN ('FACT','L1SET','L2FAIL')
AND FULIMITUTILIZED != FUFACTOREDAMOUNT+FUPURCHASERLEG2INTEREST;</th>
	<!-- 
	<th>Fu Ids</th>
	<th>History</th>
	-->
</tr>
<%
	lSql = "SELECT * FROM FINANCIERAUCTIONSETTINGS ORDER BY FASFINANCIER, FASPURCHASER, FASSUPPLIER, FASLEVEL DESC";
 	List<FinancierAuctionSettingBean> lFASList = financierAuctionSettingDAO.findListFromSql(lConnection, lSql, -1);
 	Long lLimitId =null;
 	BigDecimal lComputedLimit = BigDecimal.ZERO, lComputedBidLimit = BigDecimal.ZERO;
 	List<String> lUpdateList = new ArrayList<String>();
	for(FinancierAuctionSettingBean lFASBean : lFASList){
		//if(lFASBean!=null)
		{
			lLimitId = lFASBean.getId();
			//
			lComputedLimit = BigDecimal.ZERO;
			if(lFASLimits.containsKey(lLimitId)) 
				lComputedLimit = lFASLimits.get(lLimitId);
			//
			lComputedBidLimit = BigDecimal.ZERO;
			if(lFASBidLimits.containsKey(lLimitId)) 
				lComputedBidLimit = lFASBidLimits.get(lLimitId);
			BigDecimal lUtilised = lFASBean.getUtilised()==null?BigDecimal.ZERO:lFASBean.getUtilised();
			BigDecimal lBidLimitUtilised = lFASBean.getBidLimitUtilised()==null?BigDecimal.ZERO:lFASBean.getBidLimitUtilised();
			//
			lUpdateSql = "";
			if(lComputedLimit.subtract(lUtilised).setScale(2)!= BigDecimal.ZERO.setScale(2) ||
					lComputedBidLimit.subtract(lBidLimitUtilised).setScale(2)!= BigDecimal.ZERO.setScale(2)){
				lUpdateSql = "UPDATE FINANCIERAUCTIONSETTINGS SET FASUTILISED = " + lComputedLimit + ", FASBIDLIMITUTILISED = " + lComputedBidLimit + "  WHERE FASID = "+ lLimitId +";";
				lUpdateList.add(lUpdateSql);
			}
%>
<tr>
	<td><%=lLimitId%></td>
	<td><%=lFASBean.getLevel()%></td>
	<td><%=(lFASBean.getFinancier()!=null?lFASBean.getFinancier():"")%></td>
	<td><%=(lFASBean.getPurchaser()!=null?lFASBean.getPurchaser():"")%></td>
	<td><%=(lFASBean.getSupplier()!=null?lFASBean.getSupplier():"")%></td>
	<td><%=lComputedLimit%></td>
	<td><%=lFASBean.getUtilised()%></td>
	<td><%=lComputedLimit.subtract(lUtilised)%></td>
	<td><%=lComputedBidLimit%></td>
	<td><%=lFASBean.getBidLimitUtilised()%></td>
	<td><%=lComputedBidLimit.subtract(lBidLimitUtilised)%></td>
	<td><%=lUpdateSql%></td>
</tr>
<%		
		}
	}
%>
</table>
<h1>lFASList <%=lFASList.size() %></h1>

<%		
	if(lUpdate){
		for (String lQuery: lUpdateList){
			%>
			<%
			financierAuctionSettingDAO.executeUpdate(lConnection, StringUtils.chop(lQuery));
		}
	}
%>


</body>
</html>
