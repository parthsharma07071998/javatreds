package com.xlx.treds.monitor.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.treds.OtherResourceCache;

public class BillDetails implements IMonitorHandler{
	
	private final String code = "billDetails";

	@Override
	public void appendData(Map<String, Object> pData) {
		
		Map<String, Object> lData = new HashMap<String, Object>();
		
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		
		lData.put("bill", 0);
		lData.put("billcount", 0);
		lData.put("totalcharges", 0);
		lData.put("totalamount", 0);
		lData.put("creationmonth", 0);
		
		try{
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" select count(*) BILLCOUNT from Bills where to_char(BILRECORDCREATETIME,'mm-yyyy') = ");
			lSql.append(" '").append(FormatHelper.getDisplay("MM-yyyy", OtherResourceCache.getInstance().getCurrentDate())).append("' ");
			
			
			try(Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString())){
				while (lResultSet.next()){
					lData.put("bill", lResultSet.getInt("BILLCOUNT"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			 DBHelper lDbHelper = DBHelper.getInstance();
			 StringBuilder lSql = new StringBuilder();
			 lSql.append(" select ");
			 lSql.append(" count(*) COUNT ,sum(BILCHARGEAMOUNT) TOTALCHARGES,sum(BILFUAMOUNT) TOTALAMOUNT, ");
			 lSql.append(" to_char(max(bilrecordcreatetime),'mm-yyyy') CREATIONMONTH from Bills ");
			 lSql.append(" where to_char(bilrecordcreatetime,'mm-yyyy') ");
			 lSql.append(" = (select to_char(max(bilrecordcreatetime),'mm-yyyy') from Bills) ");
			 
			 try(Connection lConnection =lDbHelper.getConnection();
						Statement lStatement = lConnection.createStatement();
						ResultSet lResultSet = lStatement.executeQuery(lSql.toString())){
						 while(lResultSet.next()){
							 lData.put("billcount", lResultSet.getInt("COUNT"));
							 lData.put("totalcharges", lResultSet.getInt("TOTALCHARGES"));
							 lData.put("totalamount", lResultSet.getInt("TOTALAMOUNT"));
							 lData.put("creationmonth", lResultSet.getString("CREATIONMONTH"));
						 }
					 }
		}catch(Exception e){
			e.printStackTrace();
		}
		pData.put(code, lData);
		
	}

}
