package com.xlx.treds.monitor.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.utilities.DBHelper;

public class OnlineEntityMonitor implements IMonitorHandler {
	

	private final String code = "onlineEntityMonitor";
	
	@Override
	public void appendData(Map<String, Object> pData) {
		Map<String, Object> lData = new HashMap<String, Object>();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		lData.put("purchasercount", 0);
		lData.put("suppliercount", 0);
		lData.put("financiercount", 0);
		lData.put("tredscount", 0);
		try {
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT sum(case when cdpurchaserflag = 'Y' then 1 else 0 end ) PURCHASER " );
			lSql.append(" ,sum(case when cdfinancierflag= 'Y' then 1 else 0 end ) FINANCIER " );
			lSql.append(" ,sum(case when cdsupplierflag = 'Y' then 1 else 0 end ) SUPPLIER " );
			lSql.append(" ,sum(case when cdsupplierflag is null and cdfinancierflag is null and cdpurchaserflag is null and audomain='TREDS' then 1 else 0 end ) TREDS " );
			lSql.append(" FROM LOGINSESSIONS, APPUSERS left outer join companydetails on (cdcode=AUDOMAIN) " );
			lSql.append(" WHERE LSSTATUS = 'S' " );
			lSql.append(" AND TO_DATE(LSRECORDCREATETIME,'dd-mm-yyyy') >= TO_DATE(SYSDATE,'dd-mm-yyyy') " );
			lSql.append(" AND LSAUID = AUID " );
			lSql.append(" group by cdpurchaserflag,cdsupplierflag ,cdfinancierflag " );
			System.out.println("Testing123 :"+lSql.toString());
			int purchserCount = 0 ,suppliercount = 0, tredscount = 0,financiercount = 0;
			try (Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				while (lResultSet.next()){
					purchserCount += lResultSet.getInt("PURCHASER");
					suppliercount +=  lResultSet.getInt("SUPPLIER");
					financiercount += lResultSet.getInt("FINANCIER");
					tredscount +=  lResultSet.getInt("TREDS");
				}
				lData.put( "purchasercount", purchserCount );
				lData.put( "suppliercount", suppliercount );
				lData.put( "financiercount", financiercount );
				lData.put( "tredscount", tredscount);
			}catch(Exception e){  
				
				e.printStackTrace();
			    //logger.info("********** PaymentSettlor :: Updating FUs Limit Utilisation post L2 : ERROR : " + lEx.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pData.put(code, lData);
	}

}
