package com.xlx.treds.monitor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
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
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.monitor.bean.L2NachFailBean;
import com.xlx.treds.monitor.bean.SuppliersFactoredConcentrationBean;

public class ChangeInInvoiceCreatorTxnMon extends DefaultHandler {
    public ChangeInInvoiceCreatorTxnMon(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
        GenericBean lFilterBean = new GenericBean();
        DBHelper lDbHelper = DBHelper.getInstance();
        BigDecimal lFilterThreshold = null;
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        Date lFilterFromDate = (Date)lFilterBean.getProperty("fromDate");
        Date lFilterToDate = (Date)lFilterBean.getProperty("toDate");
    	lFilterThreshold = (BigDecimal) lFilterBean.getProperty("threshold");
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT INSUPPLIER,INPURCHASER, ");
		lSql.append(" SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END) INPURCHASERCOUNT, ");
		lSql.append(" SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END) INSUPPLIERCOUNT, ");
		lSql.append(" SUM(1) INTOTALCOUNT, ");
		lSql.append(" ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) INPURCHASERPERCENT, ");
		lSql.append(" ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) INSUPPLIERPERCENT, ");
		lSql.append(" CASE WHEN ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) < ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)AND ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) >= ").append(lFilterThreshold).append(" THEN 'P'  ");
		lSql.append(" WHEN ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)< ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)AND ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) >=  ").append(lFilterThreshold).append(" THEN 'S' ");
		lSql.append(" ELSE '0' END INTHRESHOLDENTITY ");
		lSql.append(" FROM INSTRUMENTS  ");
		lSql.append(" WHERE 1=1");
		if  (lFilterMap.get("days")==null) {
    		if (lFilterFromDate == null)
        		throw new CommonBusinessException("From date is mandatory");
        	if (lFilterToDate == null)
        		throw new CommonBusinessException("To date is mandatory");
    	}
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
		lSql.append(" GROUP BY INSUPPLIER,INPURCHASER");
		lSql.append(" HAVING CASE ");
		lSql.append(" WHEN ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) < ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)AND ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) >= ").append(lFilterThreshold).append(" THEN 'P'  ");
		lSql.append(" WHEN ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)< ROUND((SUM(CASE WHEN INPURCHASER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2)AND ROUND((SUM(CASE WHEN INSUPPLIER=INMAKERENTITY THEN 1 ELSE 0 END)/SUM(1)*100),2) >= ").append(lFilterThreshold).append(" THEN 'S' ");
		lSql.append(" ELSE '0' END  != '0' ");
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        String lPurchaser = null;
        String lSupplier = null;
        AppEntityBean lPurchaserBean = null;
        AppEntityBean lSupplierBean = null;
        for (GenericBean lGenericBean : lList) {
        	lPurchaser = (String) lGenericBean.getProperty("purchaser");
        	lSupplier = (String) lGenericBean.getProperty("supplier");
        	lPurchaserBean = TredsHelper.getInstance().getAppEntityBean(lPurchaser);
        	lSupplierBean = TredsHelper.getInstance().getAppEntityBean(lSupplier);
        	lGenericBean.setProperty("purName", lPurchaserBean.getName());
        	lGenericBean.setProperty("supName", lSupplierBean.getName());
        }
        return lList;
    }
    

  
    

}
