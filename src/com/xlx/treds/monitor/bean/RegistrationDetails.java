package com.xlx.treds.monitor.bean;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.AppConstants;

public class RegistrationDetails implements IMonitorHandler {
	
	private final String  code = "registrationDetails";

	@Override
	public void appendData(Map<String, Object> pData) {
		
		Map<String, Object> lData = new HashMap<String, Object>();
	
		if(pData == null){
			pData = new HashMap<String,Object>();
		}
		lData.put("Draft", 0);
		lData.put("Returned", 0);
		lData.put("Submitted", 0);
		try{
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT ");
			lSql.append(" sum( case when CDAPPROVALSTATUS IN (").append(DBHelper.getInstance().formatString(AppConstants.CompanyApprovalStatus.Submitted.getCode()));
			
		    lSql.append(" , ").append(DBHelper.getInstance().formatString(AppConstants.CompanyApprovalStatus.ReSubmitted.getCode()));
		    lSql.append(") then 1 else 0 end ) SUBMITTED, ");
			lSql.append(" sum( case when CDAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(AppConstants.CompanyApprovalStatus.Draft.getCode()));
			lSql.append("then 1 else 0 end ) DRAFT, ");
			lSql.append(" sum( case when CDAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(AppConstants.CompanyApprovalStatus.Returned.getCode()));
			lSql.append("then 1 else 0 end ) RETURNED ");
			lSql.append(" FROM  COMPANYDETAILS_P ");
			lSql.append(" WHERE CDRECORDVERSION > 0 ");
//			lSql.append(" group by CDAPPROVALSTATUS");
			
			try( Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				
				while (lResultSet.next()){
					lData.put("Draft", lResultSet.getInt("DRAFT"));
					lData.put("Returned", lResultSet.getInt("RETURNED"));
					lData.put("Submitted", lResultSet.getInt("SUBMITTED"));
					}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		pData.put(code, lData);
	}

}
