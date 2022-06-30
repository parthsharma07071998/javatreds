package com.xlx.treds;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class StatutoryReport  {

	public static Logger logger = Logger.getLogger(StatutoryReport.class);
	
	private static StatutoryReport theInstance;
	
	public static StatutoryReport getInstance() {
		if (theInstance == null) {
			synchronized (StatutoryReport.class) {
				if (theInstance == null) {
					StatutoryReport tmpTheInstance = new StatutoryReport();
					try{
						theInstance = tmpTheInstance;
					}catch(Exception lEx){
						logger.info("Error in StatutoryReport : " + lEx.getMessage());
					}
				}
			}
		}
		return theInstance;
	}

	public Map<String, Object> getMonthlyRBIReport(String pMonthAndYear){
		StringBuilder lData = new StringBuilder();
		StringBuilder lSqlData = new StringBuilder();
				
		Map<String,Object> lMap = null;
		Set<Long> lBFFUnits =  new HashSet<Long>();
		Set<Long> lCurrMnthFUnits =  new HashSet<Long>();
		Set<Long> lTotalMnthFUnits =  new HashSet<Long>();
		Set<Long> lExpiredFUnits =  new HashSet<Long>();
		Set<Long> lWithdrawnFUnits =  new HashSet<Long>();
		Set<Long> lCurrentUnAcceptFUnits =  new HashSet<Long>();
		Set<Long> lCurrentAcceptFUnits =  new HashSet<Long>();
		Set<Long> lUnFinancedFUnits =  new HashSet<Long>();
		Set<Long> lFinancedFUnits =  new HashSet<Long>();
		Set<Long> lUnFinancedandBFFUnits =  new HashSet<Long>();
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		Long lYearAndMonth = new Long(pMonthAndYear.replace("-", "")); 
   		Set<Long> lDiffrenceList = new HashSet<Long>();

		//
   		lSql.append(" SELECT * FROM ( ");
		lSql.append(" SELECT InstChilds.InId ");
		lSql.append(" , InstChilds.InStatus As InstStatus ");
		lSql.append(" , NVL(InstGrp.InId,0) As GroupInId ");
		lSql.append(" , NVL(FUnits.FuId,0)AS FuId");
		lSql.append(" , NVL(FUnits.FUStatus,'') As FactUnitStatus ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstChilds.INRecordCreateTime, 'YYYYMM')),0) As InstCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstGrp.InFactorMaxEndDateTime, 'YYYYMM')),0) As InExpiryMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstChilds.INStatusUpdateTime, 'YYYYMM')),0) As InstStatusMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FURecordCreateTime, 'YYYYMM')),0) As FUCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuAcceptDateTime, 'YYYYMM')),0) As FUAcceptanceMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuFactorMaxEndDateTime, 'YYYYMM')),0) As FuExpiryMonth ");
		lSql.append(" , InstGrp.InNetAmount As GroupNetAmount ");
		lSql.append(" , InstChilds.InNetAmount As ChilNetAmount ");
		lSql.append(" , InstChilds.InNetAmount As NetAmount ");
		lSql.append(" , NVL(FUnits.FUAmount,0) As FUAmount ");
		lSql.append(" , NVL(FUnits.FUFactoredAmount,0) As FactoredAmount ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FUStatusUpdateTime, 'YYYYMM')),0) As FUStatusUpdateTime ");
		lSql.append(" FROM Instruments InstGrp ");
		lSql.append(" LEFT OUTER JOIN factoringunits FUnits ON ( InstGrp.INRecordVersion > 0 AND InstGrp.INGroupFlag = 'Y' AND InstGrp.INGroupINId IS NULL AND InstGrp.InFuId = FUnits.FuId ) ");
		lSql.append(" JOIN Instruments InstChilds ON (InstGrp.InId = InstChilds.InGroupInId) ");
		lSql.append(" WHERE InstGrp.INRecordVersion > 0 ");
		lSql.append(" AND InstGrp.INGroupFlag = 'Y' AND InstGrp.INGroupINId IS NULL ");
		lSql.append(" AND (FUnits.FURecordVersion IS NULL OR FUnits.FURecordVersion > 0) ");
		lSql.append(" AND (FUnits.FUStatus IS NULL OR FUnits.FUStatus != 'WTHDRN' ) ");
		lSql.append(" UNION ");
		lSql.append(" SELECT InstNormal.InId ");
		lSql.append(" , InstNormal.InStatus As InstStatus ");
		lSql.append(" , 0 As GroupInId ");
		lSql.append(" , NVL(FUnits.FuId,0) AS FuId");
		lSql.append(" , NVL(FUnits.FUStatus,'') As FactUnitStatus ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstNormal.INRecordCreateTime, 'YYYYMM')),0) As InstCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstNormal.InFactorMaxEndDateTime, 'YYYYMM')),0) As InExpiryMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(InstNormal.INStatusUpdateTime, 'YYYYMM')),0) As InstStatusMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FURecordCreateTime, 'YYYYMM')),0) As FUCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuAcceptDateTime, 'YYYYMM')),0) As FUAcceptanceMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FuFactorMaxEndDateTime, 'YYYYMM')),0) As FuExpiryMonth ");
		lSql.append(" , InstNormal.InNetAmount As GroupNetAmount ");
		lSql.append(" , 0 As ChildNetAmount ");
		lSql.append(" , InstNormal.InNetAmount As NetAmount ");
		lSql.append(" , NVL(FUnits.FUAmount,0) As FUAmount ");
		lSql.append(" , NVL(FUnits.FUFactoredAmount,0) As FactoredAmount ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnits.FUStatusUpdateTime, 'YYYYMM')),0) As FUStatusUpdateTime ");
		lSql.append(" FROM Instruments InstNormal ");
		lSql.append(" LEFT OUTER JOIN factoringunits FUnits ON ( InstNormal.INRecordVersion >= 0 ");
		lSql.append(" AND InstNormal.INGroupFlag IS NULL ");
		lSql.append(" AND InstNormal.INGroupINId IS NULL ");
		lSql.append(" AND InstNormal.InFuId = FUnits.FuId ) ");
		lSql.append(" WHERE InstNormal.INRecordVersion >= 0 ");
		lSql.append(" AND InstNormal.INGroupFlag IS NULL ");
		lSql.append(" AND InstNormal.INGroupINId IS NULL ");
		lSql.append(" AND (FUnits.FURecordVersion IS NULL OR FUnits.FURecordVersion > 0) ");
		lSql.append(" AND (FUnits.FUStatus IS NULL OR FUnits.FUStatus != 'WTHDRN' ) ");
		//
		lSql.append(" UNION ");
		lSql.append(" SELECT 0 InId ");
		lSql.append(" , '' As InstStatus ");
		lSql.append(" , 0 As GroupInId ");
		lSql.append(" , NVL(FUnitsWithdrn.FuId,0) AS FuId");
		lSql.append(" , NVL(FUnitsWithdrn.FUStatus,'') As FactUnitStatus ");
		lSql.append(" , 0 As InstCreationMonth ");
		lSql.append(" , 0 As InExpiryMonth ");
		lSql.append(" , 0 As InstStatusMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnitsWithdrn.FURecordCreateTime, 'YYYYMM')),0) As FUCreationMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnitsWithdrn.FuAcceptDateTime, 'YYYYMM')),0) As FUAcceptanceMonth ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnitsWithdrn.FuFactorMaxEndDateTime, 'YYYYMM')),0) As FuExpiryMonth ");
		lSql.append(" , 0 As GroupNetAmount ");
		lSql.append(" , 0 As ChildNetAmount ");
		lSql.append(" , 0 As NetAmount ");
		lSql.append(" , NVL(FUnitsWithdrn.FUAmount,0) As FUAmount ");
		lSql.append(" , NVL(FUnitsWithdrn.FUFactoredAmount,0) As FactoredAmount ");
		lSql.append(" , NVL(TO_NUMBER(TO_CHAR(FUnitsWithdrn.FUStatusUpdateTime, 'YYYYMM')),0) As FUStatusUpdateTime ");
		lSql.append(" FROM factoringunits FUnitsWithdrn ");
		lSql.append(" WHERE FUnitsWithdrn.FURecordVersion > 0 AND FUnitsWithdrn.FUStatus = 'WTHDRN' ");
		//
		lSql.append(" ) MyQuery ");
		lSql.append(" WHERE ( FUAcceptanceMonth = 0 OR FUAcceptanceMonth >= ").append(lYearAndMonth).append(" ) ");
		lSql.append(" AND ( InExpiryMonth >= ").append(lYearAndMonth).append(" OR FuExpiryMonth >= ").append(lYearAndMonth).append(" ) ");

		int lTotalCount = 0;
		int lInExpireCount = 0;

		try (Connection lConnection = lDbHelper.getConnection();
				Statement lStatement = lConnection.createStatement();
				   ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
			lMap = new HashMap<String,Object>();
			//lMap.put("sqlquery", lSql.toString());
			//lMap.put("error","");
			long lInstCreateMth ;
			long lInExpiryMth;
			long lInStatusMth;
			long lFuCreationMth;
			long lFuAcceptMth;
			long lFuExpiryMth;
			long lFuStatusUpdateMth;
			String lInstStatus = null;
			String lFuStatus = null;
			BigDecimal lNetAmount = BigDecimal.ZERO;
			BigDecimal lFuAmount = BigDecimal.ZERO;
			long lFuId;
			Long lFuIdLong;
			//
			setDefaults(lMap);
			//
	    	lData.append("TOCHECK : YearMonth : InstStatus : ExpiryYearMth : FuStatus : FuExpiryMth :: YearAndMonth==InExpiryYearMth :: InstExpiredCode==InstStatus").append("<br>");
	    	lSqlData.append("<br>");
	    	for(int lCol=1; lCol <= 16; lCol++){
	    		if (lResultSet.getMetaData().getColumnName(lCol)!=null){
			    	lSqlData.append(lResultSet.getMetaData().getColumnName(lCol));
	    		}
		    	lSqlData.append(",");
	    	}
		    while (lResultSet.next()){
		    	lFuId = lResultSet.getLong("FUID");
		    	lFuIdLong = new Long(lFuId);
		    	lSqlData.append("<br>");
		    	for(int lCol=1; lCol <= 16; lCol++){
		    		if (lResultSet.getObject(lCol)!=null){
				    	lSqlData.append(lResultSet.getObject(lCol).toString());
		    		}
			    	lSqlData.append(",");
		    	}
		    	//
		    	lTotalCount++;
		    	//
		    	lInstCreateMth  = lResultSet.getLong("InstCreationMonth");
		    	lInExpiryMth  = lResultSet.getLong("InExpiryMonth");
		    	lInStatusMth  = lResultSet.getLong("InstStatusMonth");
		    	lFuCreationMth  = lResultSet.getLong("FUCreationMonth");
		    	lFuAcceptMth  = lResultSet.getLong("FUAcceptanceMonth");
		    	lFuExpiryMth  = lResultSet.getLong("FuExpiryMonth");
		    	lFuStatusUpdateMth = lResultSet.getLong("FUStatusUpdateTime");
		    	//
		    	lNetAmount = lResultSet.getBigDecimal("NetAmount");
		    	lFuAmount = lResultSet.getBigDecimal("FUAmount");
				//
				lInstStatus = lResultSet.getString("InstStatus");
				lFuStatus = lResultSet.getString("FactUnitStatus");
		    	//
		    	//SECTION I - brought forward
		    	if(lInstCreateMth < lYearAndMonth && 
		    			( (lInExpiryMth >= lYearAndMonth) 
		    				|| (InstrumentBean.Status.Counter_Rejected.getCode().equals(lInstStatus) && lInStatusMth >= lYearAndMonth) ) ){
		    		addCount(lMap, "1^1^1");
		    		addCount(lMap, "1^1^2");
		    		addAmount(lMap, "1^1^3", lNetAmount);
		    	}
		    	//if it is a factoring unit and it not accepted before the date
	    		if( (lFuCreationMth > 0 && lFuCreationMth < lYearAndMonth)  
	    				&& ( (FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuStatusUpdateMth >= lYearAndMonth)
	    				|| (!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuAcceptMth == 0 && lFuExpiryMth >= lYearAndMonth)
	    				|| (!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuAcceptMth != 0  && lFuAcceptMth >= lYearAndMonth) ) ){
	    			if ( lFuId > 0 ){
				    	if (!lBFFUnits.contains(lFuIdLong) ){
				    		lBFFUnits.add(lFuIdLong);
				    		addCount(lMap, "1^2^1");
				    		addAmount(lMap, "1^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "1^2^2");
	    			}
		    	}
		    	//
		    	//SECTION II - During the Month.
		    	//first condition : bf instruments and not converted into fu or converted into fu after this month
		    	if(lInstCreateMth == lYearAndMonth){
		    		addCount(lMap, "2^1^1");
		    		addCount(lMap, "2^1^2");
		    		addAmount(lMap, "2^1^3", lNetAmount);
		    	}
	    		if( ((lFuCreationMth > 0 && lFuCreationMth == lYearAndMonth)) 	){
	    			if ( lFuId>0 ){
				    	if (!lCurrMnthFUnits.contains(lFuIdLong) ){
				    		lCurrMnthFUnits.add(lFuIdLong);
				    		addCount(lMap, "2^2^1");
				    		addAmount(lMap, "2^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "2^2^2");
	    			}
	    		}
	    		//
	    		//SECTION III - Total for the Month.
		    	if(lInstCreateMth <= lYearAndMonth && 
	    			((lInExpiryMth >= lYearAndMonth) &&
	    			(lFuCreationMth == 0  || lFuCreationMth >= lYearAndMonth))){
		    		addCount(lMap, "3^1^1");
		    		addCount(lMap, "3^1^2");
		    		addAmount(lMap, "3^1^3", lNetAmount);
		    	}
	    		
	    		if( (lFuCreationMth > 0 && lFuCreationMth <= lYearAndMonth)  
	    				&& ( (FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuStatusUpdateMth >= lYearAndMonth)
	    				|| (!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuAcceptMth == 0 && lFuExpiryMth >= lYearAndMonth)
	    				|| (!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuAcceptMth != 0  && lFuAcceptMth >= lYearAndMonth)) ){
	    			if ( lFuId>0 ){
				    	if (!lTotalMnthFUnits.contains(lFuIdLong)){
				    		lTotalMnthFUnits.add(lFuIdLong);
				    		addCount(lMap, "3^2^1");
				    		addAmount(lMap, "3^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "3^2^2");
	    			}
	    		}
	    		//
		    	lData.append("TOCHECK : "+lYearAndMonth + " : "+lInstStatus + " : " + lInExpiryMth + " : "+ lFuStatus+ " : "+ lFuExpiryMth).append(" :: ").append(lYearAndMonth==lInExpiryMth).append(" :: ").append(InstrumentBean.Status.Expired.getCode().equals(lInstStatus)).append("<br>");
		   		lDiffrenceList.addAll(lTotalMnthFUnits);
	    		//SECTION IV - Expired FU's and Inst's for which due date is over .
	    		if( lYearAndMonth== lInExpiryMth && InstrumentBean.Status.Expired.getCode().equals(lInstStatus)){
	    			lInExpireCount++;
		    		addCount(lMap, "4^1^1");
		    		addCount(lMap, "4^1^2");
		    		addAmount(lMap, "4^1^3", lNetAmount);
	    		}
	    		if( lYearAndMonth==lFuExpiryMth && FactoringUnitBean.Status.Expired.getCode().equals(lFuStatus)){
	    			if ( lFuId>0 ){
	    				if (!lExpiredFUnits.contains(lFuIdLong)){
				    		lExpiredFUnits.add(lFuIdLong);
				    		addCount(lMap, "4^2^1");
				    		addAmount(lMap, "4^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "4^2^2");
	    			}
	    		}
	    		//
	    		//SECTION V - withdrawn FU's and Inst's for which due date is over .
	    		//checking fu for instrument sincce inst are not withdrawn
	    		if( lYearAndMonth==lInExpiryMth && lYearAndMonth==lFuExpiryMth && FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus)){
		    		addCount(lMap, "5^1^1");
		    		addCount(lMap, "5^1^2");
		    		addAmount(lMap, "5^1^3", lNetAmount);
	    		}
	    		if( lFuCreationMth > 0 && FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lYearAndMonth == lFuStatusUpdateMth ){
	    			if ( lFuId>0 ){
				    	if (!lWithdrawnFUnits.contains(lFuIdLong)){
				    		lWithdrawnFUnits.add(lFuIdLong);
				    		addCount(lMap, "5^2^1");
				    		addAmount(lMap, "5^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "5^2^2");
	    			}
	    		}
	    		//
	    		//SECTION VI - Unaccepted FU Units at the end of the month
	    		if ( (lInstCreateMth <= lYearAndMonth && lInExpiryMth >= lYearAndMonth) &&
	    			 (lFuCreationMth==0 || lFuCreationMth >lYearAndMonth) ) {
		    		addCount(lMap, "6^1^1");
		    		addCount(lMap, "6^1^2");
		    		addAmount(lMap, "6^1^3", lNetAmount);
	    		}
	    		if (!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && 
	    				(lInstCreateMth <= lYearAndMonth && lInExpiryMth >= lYearAndMonth) &&
		    			 (lFuCreationMth==0 || lFuCreationMth >lYearAndMonth) ) {
	    			if ( lFuId>0 ){
			    		if (!lCurrentUnAcceptFUnits.contains(lFuIdLong)){
				    		lCurrentUnAcceptFUnits.add(lFuIdLong);
				    		addCount(lMap, "6^2^1");
				    		addAmount(lMap, "6^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "6^2^2");
	    			}
	    		}		
	    		
	    		//
	    		//SECTION VII - Accepted FU Units at the end of the month
	    		if(lYearAndMonth==lFuCreationMth ){
		    		addCount(lMap, "7^1^1");
		    		addCount(lMap, "7^1^2");
		    		addAmount(lMap, "7^1^3", lNetAmount);
	    		}
	    		if(!FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lYearAndMonth==lFuCreationMth){
	    			if ( lFuId>0 ){
	    				if (!lCurrentAcceptFUnits.contains(lFuIdLong)){
				    		lCurrentAcceptFUnits.add(lFuIdLong);
				    		addCount(lMap, "7^2^1");
				    		addAmount(lMap, "7^2^3", lFuAmount);
				    	}
			    		addCount(lMap, "7^2^2");
	    			}
	    		}	
	    		//
	    		//
	    		
	    		//SECTION VIII - fu expired during the month
	    		lMap.put("8^1^1",lMap.get("4^2^1"));
	    		lMap.put("8^1^2",lMap.get("4^2^2"));
	    		lMap.put("8^1^3",lMap.get("4^2^3"));
	    		
	    		//SECTION IX - fu withdrawn during the month
	    		lMap.put("9^1^1",lMap.get("5^2^1"));
	    		lMap.put("9^1^2",lMap.get("5^2^2"));
	    		lMap.put("9^1^3",lMap.get("5^2^3"));
	    		
	    		//SECTION X - Unfinanced FUs during the month (but available for bidding)
	    		if(lFuCreationMth > 0 && lFuCreationMth<=lYearAndMonth && 
	    			((lFuAcceptMth==0 && lFuExpiryMth > lYearAndMonth ) || 
    					(lFuAcceptMth==0 && lFuStatusUpdateMth > lYearAndMonth) ||
 	    				(lFuAcceptMth>lYearAndMonth)) ){
	    			if ( lFuId>0 ){
	    				if (!lUnFinancedFUnits.contains(lFuIdLong)){
	    					lUnFinancedFUnits.add(lFuIdLong);
				    		addCount(lMap, "10^1^1");
				    		addAmount(lMap, "10^1^3", lFuAmount);
				    	}
			    		addCount(lMap, "10^1^2");
	    			}
	    		}	
	    		
	    		//SECTION XI - Financed FUs
	    		if(lFuAcceptMth==lYearAndMonth && 
	    			(FactoringUnitBean.Status.Factored.getCode().equals(lFuStatus)
	    			  || FactoringUnitBean.Status.Leg_1_Settled.getCode().equals(lFuStatus)
	    			  || FactoringUnitBean.Status.Leg_2_Settled.getCode().equals(lFuStatus) 
	    			  || FactoringUnitBean.Status.Leg_2_Failed.getCode().equals(lFuStatus) ) 
	    			){
	    			if ( lFuId>0 ){
	    				if (!lFinancedFUnits.contains(lFuIdLong)){
	    					lFinancedFUnits.add(lFuIdLong);
				    		addCount(lMap, "11^1^1");
				    		addAmount(lMap, "11^1^3", lFuAmount);
				    	}
			    		addCount(lMap, "11^1^2");
	    			}
	    		}	
	    		
	    		//SECTION XII - Unaccepted and unfinanced FUs
	    		if( lFuCreationMth<=lYearAndMonth 
	    			&& ((lFuAcceptMth > 0 && lFuAcceptMth>lYearAndMonth) 
	    					|| (lFuAcceptMth==0 && FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuStatusUpdateMth>lYearAndMonth) 
	    					|| (lFuAcceptMth==0 && !FactoringUnitBean.Status.Withdrawn.getCode().equals(lFuStatus) && lFuExpiryMth>lYearAndMonth) ) ){
	    			if ( lFuId>0 ){
	    				if (!lUnFinancedandBFFUnits.contains(lFuIdLong)){
	    					lUnFinancedandBFFUnits.add(lFuIdLong);
				    		addCount(lMap, "12^1^1");
				    		addAmount(lMap, "12^1^3", lFuAmount);
				    	}
			    		addCount(lMap, "12^1^2");
	    			}
		    	}	
	    	}
		    //lMap.put("totalcount", "Total Count " + lTotalCount);
		    //lMap.put("expirecount","Expire Count " + lInExpireCount);
		   // lMap.put("lParam", lYearAndMonth);
		    //lMap.put("error", "");
		} catch (Exception e) {
			//lMap.put("error", "Error in getBroughtForwardData() : "+e.getMessage()); 
		}
//		lMap.put("data",lData.toString());
//		lMap.put("sqldata",lSqlData.toString());
//		lDiffrenceList.addAll(lTotalMnthFUnits);
//		for(Long lId : lExpiredFUnits){
//			lDiffrenceList.remove(lId);
//		}
//		for(Long lId : lWithdrawnFUnits){
//			lDiffrenceList.remove(lId);
//		}
//		for(Long lId : lFinancedFUnits){
//			lDiffrenceList.remove(lId);
//		}
//		for(Long lId : lUnFinancedandBFFUnits){
//			lDiffrenceList.remove(lId);
//		}
//		lMap.put("diffIds", lDiffrenceList);
//	    //logger.info("1 Total Count " + lTotalCount);
//	    //logger.info("1 Expire Count " + lInExpireCount);
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
	private void setDefaults(Map<String,Object> pMap){
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
	    pMap.put("8^1^1", lCount);
	    pMap.put("8^1^2", lCount);
	    pMap.put("8^1^3", BigDecimal.ZERO);
	    pMap.put("9^1^1", lCount);
	    pMap.put("9^1^2", lCount);
	    pMap.put("9^1^3", BigDecimal.ZERO);
	    pMap.put("10^1^1", lCount);
	    pMap.put("10^1^2", lCount);
	    pMap.put("10^1^3", BigDecimal.ZERO);
	    pMap.put("11^1^1", lCount);
	    pMap.put("11^1^2", lCount);
	    pMap.put("11^1^3", BigDecimal.ZERO);
	    pMap.put("12^1^1", lCount);
	    pMap.put("12^1^2", lCount);
	    pMap.put("12^1^3", BigDecimal.ZERO);
	}
	
	private Map<String,Object> removeUnwantedData(Map<String,Object> pMap){
//	    pMap.remove("1^1^1");
//	    pMap.remove("1^1^2");
//	    pMap.remove("1^1^3");
//	    pMap.remove("2^1^1");
//	    pMap.remove("2^1^2");
//	    pMap.remove("2^1^3");
//	    pMap.remove("3^1^1");
//	    pMap.remove("3^1^2");
//	    pMap.remove("3^1^3");
//	    pMap.remove("4^1^1");
//	    pMap.remove("4^1^2");
//	    pMap.remove("4^1^3");
//	    pMap.remove("5^1^1");
//	    pMap.remove("5^1^2");
//	    pMap.remove("5^1^3");
//	    pMap.remove("6^1^1");
//	    pMap.remove("6^1^2");
//	    pMap.remove("6^1^3");
//	    pMap.remove("7^1^1");
//	    pMap.remove("7^1^2");
//	    pMap.remove("7^1^3");
	    return pMap;
	}
	
	public byte[] getExcelData(String pMonthAndYear){
		Map<String, Object> lResultMap = getMonthlyRBIReport(pMonthAndYear);
		Map<String, Object> lMap = removeUnwantedData(lResultMap);
		Workbook wb = null; 
		try {
			
			wb = WorkbookFactory.create(new File(getTemplate()) );
	    	for(String lKey : lMap.keySet() ){
	    		String lNewKey = "report"+lKey.replace("^", ""); 
	    		Name lTempName = wb.getName(lNewKey);
	    		CellAddress lCellAddress = new CellAddress(lTempName.getRefersToFormula().split("!")[1].replace("$", ""));
	    		wb.getSheetAt(0).getRow(lCellAddress.getRow()).getCell(lCellAddress.getColumn()).setCellValue(lMap.get(lKey).toString());
	    	}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	try {
	    		wb.write(bos);
	    		bos.close();
	    		return bos.toByteArray();
	    	}catch(Exception e){
	    		
	    	}

	    } catch (Exception e) {
			logger.info("Error in reading file :"+e.getMessage());
		} 
		return null;
	}
	
	 public String getTemplate(){
			RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
			return lRegistryHelper.getString(AppConstants.REGISTRY_STATUTORYREPORTTEMPLATE);
	    }
}