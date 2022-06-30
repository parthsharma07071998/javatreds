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
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;

public class IoclConnectivityCheck implements IMonitorHandler{
	
	private final String code = "ioclConnectivityCheck";
	public static Logger logger = Logger.getLogger(IoclConnectivityCheck.class);

	@Override
	public void appendData(Map<String, Object> pData) {
		HashMap<String, Object> lData = null;
		List<Map<String, Object>> lList = new ArrayList();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		try {
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT ");
			lSql.append(" ARRAPIREQUESTURL URL ");
			lSql.append(" ,COUNT(*) COUNT ");
			lSql.append(" ,MIN(ARRTIMESTAMP) FIRSTFAILTIME ");
			lSql.append(" , MAX(ARRTIMESTAMP) LASTFAILTIME ");
			lSql.append(" ,LISTAGG(ARRID, ',') WITHIN GROUP (ORDER BY ARRID) IDLIST ");
			lSql.append(" FROM ADAPTERREQUESTRESPONSES ");
			lSql.append(" WHERE ARRTYPE = ").append(lDbHelper.formatString(AdapterRequestResponseBean.Type.Out.getCode()));
			lSql.append(" AND ARRAPIRESPONSESTATUS = ").append(lDbHelper.formatString(AdapterRequestResponseBean.ApiResponseStatus.Failed.getCode()));
			lSql.append(" AND ARRENTITYCODE = ").append(lDbHelper.formatString("IN0000399"));
			lSql.append(" GROUP BY ARRAPIREQUESTURL ");
			try (Connection lConnection =lDbHelper.getConnection();
				Statement lStatement = lConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
					while (lResultSet.next()){
						lData = new HashMap<String, Object>();
						lData.put("count", lResultSet.getInt("COUNT"));
						lData.put("url",lResultSet.getString("URL"));
						lData.put("firstfailtime", FormatHelper.getDisplay("dd-MM-yyyy HH:mm", lResultSet.getString("FIRSTFAILTIME") ));
						lData.put("lastfailtime", FormatHelper.getDisplay("dd-MM-yyyy HH:mm", lResultSet.getString("LASTFAILTIME") ));
						lData.put("idList", lResultSet.getString("IDLIST"));
						lData.put("urlDesc",getDescription(lResultSet.getString("URL")));
						lList.add(lData);
					}
				}catch(Exception lEx){  
					logger.info("Error in IoclConnectivityCheck : finding result "+lEx.getMessage());
				}
			} catch (Exception e) {
				logger.info("Error in IoclConnectivityCheck : appendData() "+e.getMessage());
			}
			pData.put(code,lList);
			}
	
	private static String getDescription(String pUrl) {
		if (pUrl.contains("ZMSME_INST_DET")) {
			return "Instrument for Approval";
		}else if (pUrl.contains("ZVENLIN")) {
			return "Purchaser Supplier Link";
		}else if (pUrl.contains("PAYACK")) {
			return "Payment Acknowledgement";
		}else if (pUrl.contains("PAYSTATUS")) {
			return "Payment Status";
		}
		return "";
	}

}
