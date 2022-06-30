package com.xlx.treds.monitor.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.master.bean.ConfirmationWindowBean;

public class EodAndSessionCheckMonitor implements IMonitorHandler {

	private final String code = "eodAndSessionMonitor";
	public static Logger logger = Logger.getLogger(EodAndSessionCheckMonitor.class);
	
	@Override
	public void appendData(Map<String, Object> pData) {
		Map<String, Object> lData = new HashMap<String, Object>();
		lData.put("eod", false);
		lData.put("bidding", false);
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
		Date lDate = OtherResourceCache.getInstance().getCurrentDate();
		lData.put("date", CommonUtilities.getDisplay("dd-MMM-yyyy", lDate));
		ConfirmationWindowBean lConfirmationWindowBean;
		try {
			lConfirmationWindowBean = OtherResourceCache.getInstance().getConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
			if(lConfirmationWindowBean!=null && CommonAppConstants.YesNo.Yes.equals(lConfirmationWindowBean.getActive())){
				lData.put("bidding", true);
			}
		} catch (Exception e) {
			logger.info("Error in EodAndSessionCheckMonitor : appendData() "+e.getMessage());
		}
		if(lDate.after(lBusinessDate)){
			lData.put("eod", false);
		}
		if(lDate.equals(lBusinessDate)){
			lData.put("eod", true);
		}
		
		pData.put(code, lData);
	}

}
