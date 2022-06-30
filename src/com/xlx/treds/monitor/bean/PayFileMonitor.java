package com.xlx.treds.monitor.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.treds.auction.bean.PaymentFileBean;

public class PayFileMonitor implements IMonitorHandler {

	private final String code = "payFileMonitor";
	public static Logger logger = Logger.getLogger(PayFileMonitor.class);
	@Override
	public void appendData(Map<String, Object> pData) {
		List<Map<String, Object>> lList = new ArrayList();
		Map<String, Object> lData = new HashMap<>();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		String lStatus = null;
		try {
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT PFFACILITATOR FACILITATOR ");
			lSql.append(" , PFSTATUS STATUS ");
			lSql.append(" , PFID FILEID");
			lSql.append(" , PFDATE FILEDATE ");
			lSql.append(" ,  PFRECORDCOUNT TOTALRECORDS ");
			lSql.append(" , PFTOTALVALUE TOTALVALUE ");
			lSql.append(" FROM PAYMENTFILES ");
			lSql.append(" WHERE PFRECORDVERSION > 0 ");
			lSql.append(" AND PFSTATUS != 'R' ");
			lSql.append(" ORDER BY PFDATE DESC ");
			
			if (logger.isDebugEnabled())
			    logger.debug(lSql.toString());
			
			try (Connection lConnection =lDbHelper.getConnection();
					Statement lStatement = lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				while (lResultSet.next()){
					lData = new HashMap<String,Object>();
					lData.put("FACILITATOR",lResultSet.getString("FACILITATOR"));
					if (PaymentFileBean.Status.Return_File_Uploaded.getCode().equals(lResultSet.getString("STATUS"))){
						lStatus = PaymentFileBean.Status.Return_File_Uploaded.toString();
					}else if (PaymentFileBean.Status.Return_File_Processed.getCode().equals(lResultSet.getString("STATUS"))){
						lStatus = PaymentFileBean.Status.Return_File_Processed.toString();
					}else if (PaymentFileBean.Status.Generated.getCode().equals(lResultSet.getString("STATUS"))){
						lStatus = PaymentFileBean.Status.Generated.toString();
					}else if (PaymentFileBean.Status.Interim_File_Uploaded.getCode().equals(lResultSet.getString("STATUS"))){
						lStatus = PaymentFileBean.Status.Interim_File_Uploaded.toString();
					}else if (PaymentFileBean.Status.Interim_File_Processed.getCode().equals(lResultSet.getString("STATUS"))){
						lStatus = PaymentFileBean.Status.Interim_File_Processed.toString();
					}
					lData.put("ID",lResultSet.getString("STATUS"));
					lData.put("STATUS",lStatus);
					lData.put("FILEID",lResultSet.getInt("FILEID"));
					lData.put("FILEDATE",FormatHelper.getDisplay("dd-MMM-yyyy",lResultSet.getDate("FILEDATE")));
					lData.put("TOTALRECORDS",lResultSet.getLong("TOTALRECORDS"));
					lData.put("TOTALVALUE",lResultSet.getString("TOTALVALUE"));
					lList.add(lData);
				}
			}catch(Exception lEx){  
				logger.info("Error in PayFileMonitor : finding result "+lEx.getMessage());
			}
		} catch (Exception e) {
			logger.info("Error in PayFileMonitor : appendData() "+e.getMessage());
		}
		pData.put(code, lList);
	}

}
