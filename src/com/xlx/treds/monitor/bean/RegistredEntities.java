package com.xlx.treds.monitor.bean;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.AppConstants;

public class RegistredEntities implements IMonitorHandler {
	
	private final String  code = "registeredEntities";

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
			lSql.append(" SELECT * FROM FINYEARWISEREGISTRATIONS_VW  ");
			try(Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				
				while (lResultSet.next()){
					lTmpData = new HashMap<>();
					lTmpData.put("FinancialYear", lResultSet.getString("FINACIALYEAR"));
					lTmpData.put("Buyer", lResultSet.getInt("PURCHASER"));
					lTmpData.put("Seller", lResultSet.getInt("SUPPLIER"));
					lTmpData.put("Finacier", lResultSet.getInt("FINANCIER"));
					lTmpData.put("cumulativeBuyer", lResultSet.getInt("CUMULATIVE_PURCHASER"));
					lTmpData.put("cumulativeSeller", lResultSet.getInt("CUMULATIVE_SUPPLIER"));
					lTmpData.put("cumulativeFinacier", lResultSet.getInt("CUMULATIVE_FINANCIER"));
					lList.add(lTmpData);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		pData.put(code, lList);
	}

}
