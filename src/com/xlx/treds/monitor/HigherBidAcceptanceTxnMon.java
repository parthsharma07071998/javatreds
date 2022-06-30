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

public class HigherBidAcceptanceTxnMon extends DefaultHandler {
    public HigherBidAcceptanceTxnMon(){
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
		lSql.append(" SELECT BDFUID fufuid ,BDID fuNABDID,BDRATE FURATE ,BDFINANCIERENTITY FUFINANCIER ,FUSUPPLIER,FUACCEPTEDRATE,FUFINANCIER FUACCEPTEDFINANCIER,FUAMOUNT,FUACCEPTDATETIME  ");
		lSql.append(" FROM BIDS JOIN FACTORINGUNITS ON (FUID=BDFUID) WHERE BDSTATUS IN ('NAT') AND FUACCEPTEDRATE>BDRATE ");
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
    		lSql.append(" AND FURECORDCREATETIME between ").append(lDbHelper.formatDate(lFilterFromDate));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
    	}else{
    		if (lFilterFromDate!=null){
        		lSql.append(" AND FURECORDCREATETIME >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lSql.append(" AND FURECORDCREATETIME <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
    	}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        String lFinancier = null;
        String lAcceptedFinancier = null;
        String lSupplier = null;
        AppEntityBean lFinancierBean = null;
        AppEntityBean lAcceptedFinancierBean = null;
        AppEntityBean lSupplierBean = null;
        for (GenericBean lGenericBean : lList) {
        	lFinancier = (String) lGenericBean.getProperty("financier");
        	lAcceptedFinancier = (String) lGenericBean.getProperty("acceptedFinancier");
        	lSupplier = (String) lGenericBean.getProperty("supplier");
        	lFinancierBean = TredsHelper.getInstance().getAppEntityBean(lFinancier);
        	lAcceptedFinancierBean = TredsHelper.getInstance().getAppEntityBean(lAcceptedFinancier);
        	lSupplierBean = TredsHelper.getInstance().getAppEntityBean(lSupplier);
        	lGenericBean.setProperty("financierName", lFinancierBean.getName());
        	lGenericBean.setProperty("supplierName", lSupplierBean.getName());
        	lGenericBean.setProperty("acceptedFinancierName", lAcceptedFinancierBean.getName());
        }
        return lList;
    }
    

  
    

}
