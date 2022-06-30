package com.xlx.treds.monitor.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.master.bean.HolidayMasterBean;

public class HolidayCheckMonitor implements IMonitorHandler {

	private final String code = "holidayCheck";
	public static Logger logger = Logger.getLogger(HolidayCheckMonitor.class);
	
	@Override
	public void appendData(Map<String, Object> pData) {
		Map<String, Object> lData = new HashMap<String, Object>();
		if(pData == null){
			pData = new HashMap<String, Object>();
		}
		//search loginsession - loggedin user  as per their type and entity 
		//(multiple user of same entities will be counted only once) 	
		lData.put("trading", false);
		lData.put("clearing", false);
		try {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(HolidayMasterBean.ENTITY_NAME);
			Date lDate = OtherResourceCache.getInstance().getCurrentDate();
			Date[] lFilter = new Date[] { lDate };
			Calendar lCalendar = Calendar.getInstance();
			lCalendar.setTime(lDate);
			lCalendar.set(Calendar.HOUR_OF_DAY, 0);
			lCalendar.set(Calendar.MINUTE, 0);
			lCalendar.set(Calendar.SECOND, 0);
			lCalendar.set(Calendar.MILLISECOND, 0);
			lData.put("date", CommonUtilities.getDisplay("dd-MMM-yyyy", lDate));
			String lTradingWeekDays = RegistryHelper.getInstance().getString(AppConstants.REGISTRY_TRADINGWEEKDAYS);
			String lClearingWeekDays = RegistryHelper.getInstance().getString(AppConstants.REGISTRY_CLEARINGWEEKDAYS);
			if (lTradingWeekDays.charAt(lCalendar.get(Calendar.DAY_OF_WEEK) - 1) == 'Y') { 
				HolidayMasterBean lHolidayMasterBean = (HolidayMasterBean) lMemoryTable.selectSingleRow(HolidayMasterBean.f_Date, lFilter);
				if (lHolidayMasterBean != null && (lHolidayMasterBean.getType() == HolidayMasterBean.Type.Trading)){
					lData.put("trading", true);
				}
			}
			if (lClearingWeekDays.charAt(lCalendar.get(Calendar.DAY_OF_WEEK) - 1) == 'Y') { 
				HolidayMasterBean lHolidayMasterBean = (HolidayMasterBean) lMemoryTable.selectSingleRow(HolidayMasterBean.f_Date, lFilter);
				if (lHolidayMasterBean != null && (lHolidayMasterBean.getType() == HolidayMasterBean.Type.Clearing)){
					lData.put("clearing", true);
				}
			}
			pData.put(code, lData);
		} catch (Exception e) {
			logger.info("Error in HolidayCheckMonitor : appendData() "+e.getMessage());
		}
	}

}
