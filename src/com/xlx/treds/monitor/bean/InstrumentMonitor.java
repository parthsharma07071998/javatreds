package com.xlx.treds.monitor.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.OtherResourceCache;

public class InstrumentMonitor implements IMonitorHandler {

	private final String code = "instrumentMonitor";
	public static Logger logger = Logger.getLogger(InstrumentMonitor.class);
	@Override
	public void appendData(Map<String, Object> pData) {
		Map<String, Object> lData = new HashMap<String, Object>();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		lData.put("instrumentsCreatedToday", 0);
		lData.put("groupInstsCreatedToday", 0);
		lData.put("factoringUnitsCreatedToday", 0);
		lData.put("factoringUnitsFactoredToday", 0);
		try {
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT ");
			lSql.append(" SUM(CASE WHEN INGROUPINID IS NULL  AND INGROUPFLAG IS NULL THEN 1 ELSE 0 END) INSTCOUNT  ");
			lSql.append(" , SUM(CASE WHEN INGROUPINID IS NULL  AND INGROUPFLAG = 'Y' THEN 1 ELSE 0 END) GROUPCOUNT  ");
			lSql.append(" FROM INSTRUMENTS WHERE TO_CHAR(INRECORDCREATETIME,'DD-MM-YYYY') = TO_CHAR( ");
			lSql.append( lDbHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate()));
			lSql.append(" ,'DD-MM-YYYY') "); 
			lSql.append(" AND INRECORDVERSION > 0 ");
			
			if (logger.isDebugEnabled())
			    logger.debug(lSql.toString());
			
			try (Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				while (lResultSet.next()){
					lData.put("instrumentsCreatedToday", lResultSet.getInt("INSTCOUNT"));
					lData.put("groupInstsCreatedToday", lResultSet.getInt("GROUPCOUNT"));
				}
			}catch(Exception lEx){  
				logger.info("Error in InstrumentMonitor : finding result "+lEx.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT  ");
			lSql.append(" SUM ( CASE WHEN to_char(FURECORDCREATETIME,'dd-mm-yyyy') = to_char( ");
			lSql.append( lDbHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate()));
			lSql.append(" ,'dd-mm-yyyy') ");
			lSql.append(" THEN 1 ELSE 0 END ) FUCREATECOUNT ,");
			lSql.append(" SUM ( CASE WHEN to_char(FUACCEPTDATETIME,'dd-mm-yyyy') = to_char( ");
			lSql.append( lDbHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate()));
			lSql.append(" ,'dd-mm-yyyy') ");
			lSql.append(" THEN 1 ELSE 0 END ) FUACCEPTCOUNT");
			lSql.append(" FROM FACTORINGUNITS WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND to_char(FUACCEPTDATETIME,'dd-mm-yyyy') = to_char( ");
			lSql.append( lDbHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate()));
			lSql.append(" ,'dd-mm-yyyy' ) ");
			lSql.append(" OR to_char(FURECORDCREATETIME,'dd-mm-yyyy') = to_char( ");
			lSql.append( lDbHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate()));
			lSql.append(" ,'dd-mm-yyyy' ) ");
			try (Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				while (lResultSet.next()){
					lData.put("factoringUnitsCreatedToday", lResultSet.getInt("FUCREATECOUNT"));
					lData.put("factoringUnitsFactoredToday", lResultSet.getInt("FUACCEPTCOUNT"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		pData.put(code, lData);
	}

}
