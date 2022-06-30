package com.xlx.treds.monitor.bean;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.AppConstants;

public class PurchaserFuReportMonitor implements IMonitorHandler {
	
	private final String  code = "purchaserFuReport";

	@Override
	public void appendData(Map<String, Object> pData) {
		
		ArrayList<Object> lList = new ArrayList<Object>();;
		Map<String, Object> lTmpData = null;
		if(pData == null){
			pData = new HashMap<String,Object>();
		}
		try{
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT * FROM PurchaserFuReoprt_vw ");
			try( Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				
				while (lResultSet.next()){
					lTmpData = new HashMap<>();
					lTmpData.put("FinancialYear", lResultSet.getString("FINACIALYEAR"));
					lTmpData.put("Constitution", lResultSet.getString("CONSTITUTION"));
					lTmpData.put("Status", lResultSet.getString("STATUS"));
					lTmpData.put("InvoiceCount", lResultSet.getInt("INCOUNT"));
					lTmpData.put("FactoringUnitCount", lResultSet.getInt("FUCOUNT"));
					lTmpData.put("Amount", lResultSet.getBigDecimal("AMOUNT"));
					lList.add(lTmpData);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		pData.put(code, lList);
	}

}
