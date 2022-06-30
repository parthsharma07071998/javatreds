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

public class PaymentBehaviourTxnMon extends DefaultHandler {
    public PaymentBehaviourTxnMon(){
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
		lSql.append(" SELECT OBTXNENTITY OBENTITY,CDCOMPANYNAME OBNAME,SUM(OBAMOUNT) OBAMOUNT,SUM(OBORIGINALAMOUNT) OBORIGINALAMOUNT,SUM(NVL(OBSETTLEDAMOUNT,0)) OBTOTAL,ROUND((SUM(NVL(OBSETTLEDAMOUNT,0))/SUM(OBAMOUNT)*100),2) OBPERCENT FROM OBLIGATIONS,COMPANYDETAILS,FACTORINGUNITS ");
		lSql.append(" WHERE OBTXNENTITY=CDCODE  ");
		lSql.append(" AND CDPURCHASERFLAG='Y'  ");
		lSql.append(" AND OBRECORDVERSION>0  ");
		lSql.append(" AND FURECORDVERSION>0  ");
		lSql.append(" AND OBTYPE ='L2' ");
		lSql.append(" AND OBSTATUS != 'SFT' " );
		lSql.append(" AND FUID=OBFUID AND FUSTATUS IN ('L2SET','L2FAIL') ");
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
    		lSql.append(" AND OBDATE between ").append(lDbHelper.formatDate(lFilterFromDate));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
    	}else{
    		if (lFilterFromDate!=null){
        		lSql.append(" AND OBDATE >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lSql.append(" AND OBDATE <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
    	}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
		lSql.append(" GROUP BY OBTXNENTITY,CDCOMPANYNAME ");
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        return lList;
    }
    

  
    

}
