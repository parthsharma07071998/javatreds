package com.xlx.treds.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class InstrumentTransactionTimeTxnMon extends DefaultHandler {
    public InstrumentTransactionTimeTxnMon(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
        GenericBean lFilterBean = new GenericBean();
        DBHelper lDbHelper = DBHelper.getInstance();
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        Date lFilterFromDate = (Date)lFilterBean.getProperty("fromDate");
        Date lFilterToDate = (Date)lFilterBean.getProperty("toDate");
        Time lFilterTime = (Time)lFilterBean.getProperty("time");
        if  (lFilterMap.get("days")==null) {
        	if (lFilterFromDate == null)
            	throw new CommonBusinessException("From date is mandatory");
            if (lFilterToDate == null)
            	throw new CommonBusinessException("To date is mandatory");
            if (lFilterTime == null)
            	throw new CommonBusinessException("Time is mandatory");
    	}
        lFilterBean.setProperty("time",null);
        Timestamp lPrevDate = null;
        Timestamp lCurrentDate = null;
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT INID,IWFSTATUSUPDATETIME INSTATUSUPDATETIME,IWFSTATUS INSTATUS,INPURCHASER,INSUPPLIER,INRECORDCREATETIME,INAMOUNT ");
		lSql.append(" FROM INSTRUMENTWORKFLOW,INSTRUMENTS  WHERE IWFSTATUS IN ('CHKAPP','COUAPP') AND INID=IWFINID ");
		if (lFilterToDate==null) {
			lFilterToDate = new Date(CommonUtilities.getCurrentDate().getTime());
    	}
    	if (lFilterFromDate==null) {
    		lFilterFromDate = CommonUtilities.addRemoveDays(lFilterToDate,-Integer.parseInt(lFilterMap.get("days").toString()));
    	}
		if(lFilterFromDate!=null && lFilterToDate!=null){
    		lSql.append(" AND INRECORDCREATETIME between ").append(lDbHelper.formatDate(lFilterFromDate));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
    	}else{
    		if (lFilterFromDate!=null){
        		lSql.append(" AND INRECORDCREATETIME >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lSql.append(" AND INRECORDCREATETIME <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
    	}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        List<GenericBean> lRtnList = new ArrayList<GenericBean>(); 
        String lPurchaser = null;
        String lSupplier = null;
        AppEntityBean lPurchaserBean = null;
        AppEntityBean lSupplierBean = null;
        GenericBean lPreviousBean = null;
        long lFilterTimeMillis = lFilterTime.getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET);
        for (GenericBean lGenericBean : lList) {
        	if (InstrumentBean.Status.Checker_Approved.getCode().equals((String) lGenericBean.getProperty("status"))) {
        		lPreviousBean = lGenericBean;
        	}else {
        		if (lPreviousBean!=null && Long.valueOf(lGenericBean.getProperty("id").toString()).equals(Long.valueOf(lPreviousBean.getProperty("id").toString())) 
        				&& InstrumentBean.Status.Counter_Approved.getCode().equals((String) lGenericBean.getProperty("status")) 
        				&& InstrumentBean.Status.Checker_Approved.getCode().equals((String) lPreviousBean.getProperty("status")) ) {
        			lPurchaser = (String) lGenericBean.getProperty("purchaser");
                	lSupplier = (String) lGenericBean.getProperty("supplier");
                	lPurchaserBean = TredsHelper.getInstance().getAppEntityBean(lPurchaser);
                	lSupplierBean = TredsHelper.getInstance().getAppEntityBean(lSupplier);
                	lGenericBean.setProperty("purName", lPurchaserBean.getName());
                	lGenericBean.setProperty("supName", lSupplierBean.getName());
                	lPrevDate = (Timestamp)lPreviousBean.getProperty("statusUpdateTime");
                	lCurrentDate =(Timestamp)lGenericBean.getProperty("statusUpdateTime");
                	if (lFilterTimeMillis>(lCurrentDate.getTime() - lPrevDate.getTime())) {
                		lGenericBean.setProperty("time",new Time(lCurrentDate.getTime() - lPrevDate.getTime() - Calendar.getInstance().get(Calendar.ZONE_OFFSET)));
                		lRtnList.add(lGenericBean);
                	}
        		}
        	}
        }
        return lRtnList;
    }
    

  
    

}
