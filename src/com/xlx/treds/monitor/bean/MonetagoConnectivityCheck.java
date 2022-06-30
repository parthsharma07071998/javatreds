package com.xlx.treds.monitor.bean;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.xlx.treds.InstAuthJerseyClient;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.OtherResourceCache;

public class MonetagoConnectivityCheck implements IMonitorHandler{
	
	private final String code = "monetagoConnectivityCheck";
		
	@Override
	public void appendData(Map<String, Object> pData) {
		
		Map<String, Object> lData = new HashMap<>();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		try{
			Date lDate = OtherResourceCache.getInstance().getCurrentDate();
			Boolean lMonetagoConnectivityFlag  = MonetagoTredsHelper.getInstance().checkConnectivity(OtherResourceCache.getInstance().getPreviousDate(lDate, 1),lDate);
			Boolean lInstAuthConnectivityFlag = InstAuthJerseyClient.getInstance().getAllSuppliers();
			 lData.put("monetagoConnectivityFlag", lMonetagoConnectivityFlag);
			 lData.put("instAuthConnectivityFlag", lInstAuthConnectivityFlag);			
		}catch (Exception e) {
			e.printStackTrace();
		}
		pData.put(code, lData);
	}
	
	

}
