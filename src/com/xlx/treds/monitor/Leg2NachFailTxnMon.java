package com.xlx.treds.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.monitor.bean.L2NachFailBean;

public class Leg2NachFailTxnMon extends DefaultHandler {
    public Leg2NachFailTxnMon(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
    	DBHelper lDbHelper = DBHelper.getInstance();
    	Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
		Object lFilterBean = super.getGenericDAO().newBean();
		super.getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
		StringBuilder lSql = new StringBuilder();
		lSql.append(super.getGenericDAO().getListSql(null, (String)null));
		Long lFilterPercent = null;
		if  (lFilterMap.get("filterPercent")!=null) {
			lFilterPercent = Long.valueOf(lFilterMap.get("filterPercent").toString());
		}
		lSql.append(" WHERE 1=1 ");
    	L2NachFailBean lBean = (L2NachFailBean) lFilterBean;
    	if  (lFilterMap.get("days")==null) {
    		if (lBean.getFromDate() == null)
        		throw new CommonBusinessException("From date is mandatory");
        	if (lBean.getToDate() == null)
        		throw new CommonBusinessException("To date is mandatory");
    	}
    	if (lBean.getToDate() == null) {
    		lBean.setToDate(new Date(CommonUtilities.getCurrentDate().getTime()));
    	}
    	if (lBean.getFromDate() == null) {
    		lBean.setFromDate(CommonUtilities.addRemoveDays(lBean.getToDate(),-Integer.parseInt(lFilterMap.get("days").toString())));
    	}
    	if(lBean.getFromDate()!=null && lBean.getToDate()!=null){
    		lSql.append(" AND L2OBLIGATIONDATE between ").append(lDbHelper.formatDate(lBean.getFromDate()));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lBean.getToDate()));
    	}else{
    		if (lBean.getFromDate()!=null){
        		lSql.append(" AND L2OBLIGATIONDATE >= ").append(lDbHelper.formatDate(lBean.getFromDate()));
        	}
        	if (lBean.getToDate()!=null){
        		lSql.append(" AND L2OBLIGATIONDATE <= ").append(lDbHelper.formatDate(lBean.getToDate()));
        	}
    	}
    	if (lFilterPercent==null) {
			lSql.append(" and  L2failPercent > 0");
		}else {
			lSql.append(" and L2failPercent  >= ").append(lFilterPercent).append(" ");
		}
        super.getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
        if (StringUtils.isNotBlank(super.getOrderBy()))
            lSql.append(" ORDER BY ").append(super.getOrderBy());
        List lList = super.getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        return lList;
    }
    

  
    

}
