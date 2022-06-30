package com.xlx.treds.other.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class MonthlyTransactionsWrapper
{
	public static Logger logger = Logger.getLogger(MonthlyTransactionsWrapper.class);
	private static MonthlyTransactionsWrapper theInstance;
	private static final String MTH_DATE_FORMAT = "YYYYMM";
	//
	public static MonthlyTransactionsWrapper getInstance()
	{
		if (theInstance == null)
		{
			synchronized(TredsHelper.class)
			{
				if (theInstance == null)
				{
					try
					{
						MonthlyTransactionsWrapper tmpTheInstance = new MonthlyTransactionsWrapper();
						theInstance = tmpTheInstance;
					}
					catch(Exception lException)
					{
						logger.fatal("Error while instantiating MonthlyTransactionsWrapper",lException);
					}
				}
			}
		}
		return theInstance;
	}
	
	public Map<String, Object> getBroughtForwardData(String pMonthAndYear)
	{
		StringBuilder lData = new StringBuilder();
		StringBuilder lSqlData = new StringBuilder();
				
		Map<String,Object> lMap = null;
		Set<Long> lBFFUnits =  new HashSet<Long>();
		Set<Long> lCurrMnthFUnits =  new HashSet<Long>();
		Set<Long> lTotalMnthFUnits =  new HashSet<Long>();
		Set<Long> lExpiredFUnits =  new HashSet<Long>();
		Set<Long> lWithdrawnFUnits =  new HashSet<Long>();
		Set<Long> lCurrentUnAcceptFUnits =  new HashSet<Long>();
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		Long lYearAndMonth = new Long(pMonthAndYear.replace("-", "")); 
		//
		lSql.append(" SELECT * FROM ( ");
		lSql.append(" SELECT InstChilds.InId ");
		lSql.append(" , InstChilds.InStatus As InstStatus ");
		lSql.append(" , InstGrp.InId As GroupInId ");
		lSql.append(" , FUnits.FuId ");
		lSql.append(" , FUnits.FUStatus As FactUnitStatus ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstChilds.INRecordCreateTime, '").append(MTH_DATE_FORMAT).append("')),0) As InstCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstGrp.InFactorMaxEndDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As InExpiryMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FURecordCreateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FUCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuAcceptDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FUAcceptanceMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuFactorMaxEndDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FuExpiryMonth ");
		lSql.append(" , InstGrp.InNetAmount As GroupNetAmount ");
		lSql.append(" , InstChilds.InNetAmount As ChilNetAmount ");
		lSql.append(" , InstChilds.InNetAmount As NetAmount ");
		lSql.append(" , FUnits.FUAmount As FUAmount ");
		lSql.append(" , FUnits.FUFactoredAmount As FactoredAmount ");
		lSql.append(" FROM InstrumentsRBI InstGrp ");
		lSql.append(" LEFT OUTER JOIN factoringunitsRBI FUnits ON ( InstGrp.INRecordVersion > 0 AND InstGrp.INGroupFlag = 'Y' AND InstGrp.INGroupINId IS NULL AND InstGrp.InFuId = FUnits.FuId ) ");
		lSql.append(" JOIN InstrumentsRBI InstChilds ON (InstGrp.InId = InstChilds.InGroupInId) ");
		lSql.append(" WHERE InstGrp.INRecordVersion > 0 ");
		lSql.append(" AND InstGrp.INGroupFlag = 'Y' AND InstGrp.INGroupINId IS NULL ");
		lSql.append(" AND (FUnits.FURecordVersion IS NULL OR FUnits.FURecordVersion > 0) ");
		lSql.append(" UNION ");
		lSql.append(" SELECT InstNormal.InId ");
		lSql.append(" , InstNormal.InStatus As InstStatus ");
		lSql.append(" , NULL As GroupInId ");
		lSql.append(" , FUnits.FuId ");
		lSql.append(" , FUnits.FUStatus As FactUnitStatus ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstNormal.INRecordCreateTime, '").append(MTH_DATE_FORMAT).append("')),0) As InstCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstNormal.InFactorMaxEndDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As InExpiryMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FURecordCreateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FUCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuAcceptDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FUAcceptanceMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuFactorMaxEndDateTime, '").append(MTH_DATE_FORMAT).append("')),0) As FuExpiryMonth ");
		lSql.append(" , InstNormal.InNetAmount As GroupNetAmount ");
		lSql.append(" , 0 As ChildNetAmount ");
		lSql.append(" , InstNormal.InNetAmount As NetAmount ");
		lSql.append(" , FUnits.FUAmount As FUAmount ");
		lSql.append(" , FUnits.FUFactoredAmount As FactoredAmount ");
		lSql.append(" FROM InstrumentsRBI InstNormal ");
		lSql.append(" LEFT OUTER JOIN factoringunitsRBI FUnits ON ( InstNormal.INRecordVersion > 0 ");
		lSql.append(" AND InstNormal.INGroupFlag IS NULL ");
		lSql.append(" AND InstNormal.INGroupINId IS NULL ");
		lSql.append(" AND InstNormal.InFuId = FUnits.FuId ) ");
		lSql.append(" WHERE InstNormal.INRecordVersion > 0 ");
		lSql.append(" AND InstNormal.INGroupFlag IS NULL ");
		lSql.append(" AND InstNormal.INGroupINId IS NULL ");
		lSql.append(" AND (FUnits.FURecordVersion IS NULL OR FUnits.FURecordVersion > 0) ");
		lSql.append(" ) MyQuery ");
		lSql.append(" WHERE ( FUAcceptanceMonth = 0 OR FUAcceptanceMonth >= ").append(lYearAndMonth).append(" ) ");
		lSql.append(" AND ( InExpiryMonth >= ").append(lYearAndMonth).append(" OR FuExpiryMonth >= ").append(lYearAndMonth).append(" ) ");

		int lTotalCount = 0;
		int lInExpireCount = 0;

		try (Connection lConnection = lDbHelper.getConnection();
				Statement lStatement = lConnection.createStatement();
				   ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
			lMap = new HashMap<String,Object>();
			lMap.put("sqlquery", lSql.toString());
			lMap.put("error","");
			int lTemp = 0;
			Long lInstCreateMth = null;
			Long lInExpiryMth = null;
			Long lFuCreationMth = null;
			Long lFuAcceptMth = null;
			Long lFuExpiryMth = null;
			String lInstStatus = null;
			String lFuStatus = null;
			BigDecimal lNetAmount = BigDecimal.ZERO;
			BigDecimal lFuAmount = BigDecimal.ZERO;
			//
			setDefaults(lMap);
			//
	    	lData.append("TOCHECK : YearMonth : InstStatus : ExpiryYearMth : FuStatus : FuExpiryMth :: YearAndMonth==InExpiryYearMth :: InstExpiredCode==InstStatus").append("<br>");
		    while (lResultSet.next()){
		    	lSqlData.append("<br>");
		    	for(int lCol=0; lCol < 15; lCol++){
			    	lSqlData.append(lResultSet.getString(lCol)).append(",");
		    	}
		    	//
		    	lTotalCount++;
		    	//
		    	lInstCreateMth  = lResultSet.getLong("InstCreationMonth");
		    	lInExpiryMth  = lResultSet.getLong("InExpiryMonth");
		    	lFuCreationMth  = lResultSet.getLong("FUCreationMonth");
		    	lFuAcceptMth  = lResultSet.getLong("FUAcceptanceMonth");
		    	lFuExpiryMth  = lResultSet.getLong("FuExpiryMonth");
		    	//
		    	lNetAmount = lResultSet.getBigDecimal("NetAmount");
		    	lFuAmount = lResultSet.getBigDecimal("FUAmount");
				//
				lInstStatus = lResultSet.getString("InstStatus");
				lFuStatus = lResultSet.getString("FactUnitStatus");
		    	//
		    	//SECTION I - brought forward
		    	if(lInstCreateMth < lYearAndMonth && 
		    			((lInExpiryMth >= lYearAndMonth) &&
		    			(lFuCreationMth == 0  || lFuCreationMth >= lYearAndMonth))){
		    		addCount(lMap, "1^1^1");
		    		addCount(lMap, "1^1^2");
		    		addAmount(lMap, "1^1^3", lNetAmount);
		    	}
		    	//if it is a factoring unit and it not accepted before the date
	    		if( (lFuCreationMth > 0 && lFuCreationMth < lYearAndMonth)  
	    				&& ((lFuAcceptMth == 0 && lFuExpiryMth >= lYearAndMonth)
	    				|| (lFuAcceptMth != 0  && lFuAcceptMth >= lYearAndMonth))){
			    	if (!lBFFUnits.contains(lResultSet.getLong("FUID")) ){
			    		lBFFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "1^2^1");
			    		addAmount(lMap, "1^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "1^2^2");
		    	}
		    	//
		    	//SECTION II - During the Month.
		    	if (lInstCreateMth == lYearAndMonth &&
		    			(lFuCreationMth == 0  || lFuCreationMth > lYearAndMonth) ){
		    		addCount(lMap, "2^1^1");
		    		addCount(lMap, "2^1^2");
		    		addAmount(lMap, "2^1^3", lNetAmount);
		    	}
	    		if( (lFuCreationMth == lYearAndMonth)  
	    				&& ((lFuAcceptMth == 0 && lFuExpiryMth >= lYearAndMonth)
	    				|| (lFuAcceptMth != 0  && lFuAcceptMth > lYearAndMonth))){
			    	if (!lCurrMnthFUnits.contains(lResultSet.getLong("FUID")) ){
			    		lCurrMnthFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "2^2^1");
			    		addAmount(lMap, "2^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "2^2^2");
		    	}
	    		//
	    		//SECTION III - Total for the Month.
	    		//bf and expired in this month
	    		if( (lInstCreateMth <= lYearAndMonth && lInExpiryMth == lYearAndMonth) ){
		    		addCount(lMap, "3^1^1");
		    		addCount(lMap, "3^1^2");
		    		addAmount(lMap, "3^1^3", lNetAmount);
	    		}
	    		if( lFuCreationMth <= lYearAndMonth &&
	    				((lFuAcceptMth == 0 && lFuExpiryMth == lYearAndMonth) ||
	    						(lFuAcceptMth == lYearAndMonth)) ){
			    	if (!lTotalMnthFUnits.contains(lResultSet.getLong("FUID"))){
			    		lTotalMnthFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "3^2^1");
			    		addAmount(lMap, "3^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "3^2^2");
	    		}
	    		//
		    	lData.append("TOCHECK : "+lYearAndMonth + " : "+lInstStatus + " : " + lInExpiryMth + " : "+ lFuStatus+ " : "+ lFuExpiryMth).append(" :: ").append(lYearAndMonth.equals(lInExpiryMth)).append(" :: ").append(InstrumentBean.Status.Expired.getCode().equals(lInstStatus)).append("<br>");
	    		//SECTION IV - Expired FU's and Inst's for which due date is over .
	    		if( lYearAndMonth.equals(lInExpiryMth) && InstrumentBean.Status.Expired.getCode().equals(lInstStatus)){
	    			lInExpireCount++;
		    		addCount(lMap, "4^1^1");
		    		addCount(lMap, "4^1^2");
		    		addAmount(lMap, "4^1^3", lNetAmount);
	    		}
	    		if( lYearAndMonth.equals(lFuExpiryMth) && FactoringUnitBean.Status.Expired.getCode().equals(lFuStatus)){
			    	if (!lExpiredFUnits.contains(lResultSet.getLong("FUID"))){
			    		lExpiredFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "4^2^1");
			    		addAmount(lMap, "4^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "4^2^2");
	    		}
	    		//
	    		//SECTION V - withdrawn FU's and Inst's for which due date is over .
	    		//checking fu for instrument sincce inst are not withdrawn
	    		if( lYearAndMonth.equals(lInExpiryMth) && lYearAndMonth.equals(lFuExpiryMth) && FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus)){
		    		addCount(lMap, "5^1^1");
		    		addCount(lMap, "5^1^2");
		    		addAmount(lMap, "5^1^3", lNetAmount);
	    		}
	    		if( lYearAndMonth.equals(lFuExpiryMth) && FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus)){
			    	if (!lWithdrawnFUnits.contains(lResultSet.getLong("FUID"))){
			    		lWithdrawnFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "5^2^1");
			    		addAmount(lMap, "5^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "5^2^2");
	    		}
	    		//
	    		//SECTION VI - Unaccepted FU Units at the end of the month
	    		if( lInExpiryMth >= lYearAndMonth &&
	    		 	 (lFuCreationMth==0 || lFuCreationMth > lYearAndMonth)){
		    		addCount(lMap, "6^1^1");
		    		addCount(lMap, "6^1^2");
		    		addAmount(lMap, "6^1^3", lNetAmount);
	    		}
	    		if( lFuExpiryMth >= lYearAndMonth &&
		    		 	 (lFuAcceptMth==0 || lFuAcceptMth > lYearAndMonth)){
			    	if (!lCurrentUnAcceptFUnits.contains(lResultSet.getLong("FUID"))){
			    		lCurrentUnAcceptFUnits.add(lResultSet.getLong("FUID"));
			    		addCount(lMap, "6^2^1");
			    		addAmount(lMap, "6^2^3", lFuAmount);
			    	}
		    		addCount(lMap, "6^2^2");
	    		}		    
	    	}
		    lMap.put("totalcount", "Total Count " + lTotalCount);
		    lMap.put("expirecount","Expire Count " + lInExpireCount);
		    lMap.put("lParam", lYearAndMonth);
		} catch (Exception e) {
			lMap.put("error", "Error in getBroughtForwardData() : "+e.getMessage()); 
		}
		lMap.put("data",lData.toString());
		lMap.put("sqldata",lSqlData.toString());
	    //logger.info("1 Total Count " + lTotalCount);
	    //logger.info("1 Expire Count " + lInExpireCount);
		return lMap;
	}
	
	public void addCount(Map<String,Object> pMap,String pKey){
		int lCount = 0;
		if(!pMap.containsKey(pKey)){
			pMap.put(pKey, lCount);
		}else{
			lCount = (int) pMap.get(pKey);
		}
		pMap.put(pKey, (lCount + 1));
	}
	
	public void addAmount(Map<String,Object> pMap,String pKey,BigDecimal pAmount){
		if(!pMap.containsKey(pKey)){
			pMap.put(pKey, BigDecimal.ZERO);
		}
		pMap.put(pKey, ((BigDecimal) pMap.get(pKey)).add(pAmount));
		
	}
	public void setDefaults(Map<String,Object> pMap){
		int lCount = 0;
	    pMap.put("1^1^1", lCount);
	    pMap.put("1^1^2", lCount);
	    pMap.put("1^1^3", BigDecimal.ZERO);
	    pMap.put("1^2^1", lCount);
	    pMap.put("1^2^2", lCount);
	    pMap.put("1^2^3", BigDecimal.ZERO);
	    pMap.put("2^1^1", lCount);
	    pMap.put("2^1^2", lCount);
	    pMap.put("2^1^3", BigDecimal.ZERO);
	    pMap.put("2^2^1", lCount);
	    pMap.put("2^2^2", lCount);
	    pMap.put("2^2^3", BigDecimal.ZERO);
	    pMap.put("3^1^1", lCount);
	    pMap.put("3^1^2", lCount);
	    pMap.put("3^1^3", BigDecimal.ZERO);
	    pMap.put("3^2^1", lCount);
	    pMap.put("3^2^2", lCount);
	    pMap.put("3^2^3", BigDecimal.ZERO);
        pMap.put("4^1^1", lCount);
	    pMap.put("4^1^2", lCount);
	    pMap.put("4^1^3", BigDecimal.ZERO);
	    pMap.put("4^2^1", lCount);
	    pMap.put("4^2^2", lCount);
	    pMap.put("4^2^3", BigDecimal.ZERO);
	    pMap.put("5^1^1", lCount);
	    pMap.put("5^1^2", lCount);
	    pMap.put("5^1^3", BigDecimal.ZERO);
	    pMap.put("5^2^1", lCount);
	    pMap.put("5^2^2", lCount);
	    pMap.put("5^2^3", BigDecimal.ZERO);
	    pMap.put("6^1^1", lCount);
	    pMap.put("6^1^2", lCount);
	    pMap.put("6^1^3", BigDecimal.ZERO);
	    pMap.put("6^2^1", lCount);
	    pMap.put("6^2^2", lCount);
	    pMap.put("6^2^3", BigDecimal.ZERO);
	    pMap.put("7^1^1", lCount);
	    pMap.put("7^1^2", lCount);
	    pMap.put("7^1^3", BigDecimal.ZERO);
	    pMap.put("7^2^1", lCount);
	    pMap.put("7^2^2", lCount);
	    pMap.put("7^2^3", BigDecimal.ZERO);
	}
}
