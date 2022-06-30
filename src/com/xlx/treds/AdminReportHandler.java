package com.xlx.treds;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.print.attribute.standard.MediaSize.Other;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstReportBean.Bid_Status;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class AdminReportHandler extends DefaultHandler {
    public static final String REPORTID_INST = "inst";
    public static final String REPORTID_PSLINK = "pursuplnk";
    public static final String REPORTID_FACTORING = "factoring";
    public static final String REPORTID_FACTORINGBID = "factoringbid";
    public static final String REPORTID_FACTUNIT = "factunit";
    
    public AdminReportHandler(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
    	DBHelper lDbHelper = DBHelper.getInstance();
    	if(pAppUserBean!=null){
    		if(!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain())){
	            if (REPORTID_FACTORING.equals(lReportId)) {
	            	pFilterMap.put("financier", pAppUserBean.getDomain());
	            }
	            else if (REPORTID_FACTORINGBID.equals(lReportId)){
	            	pFilterMap.put("financierEntity", pAppUserBean.getDomain());
	            }
    		}
    	}
    	List lList = null;
    	if (REPORTID_FACTUNIT.equals(lReportId)) {
    		FactoringUnitBean lFilterBean = new FactoringUnitBean();
    		getGenericDAO().getBeanMeta().validateAndParse(lFilterBean, pFilterMap, null, null);
    		StringBuilder lSql = new StringBuilder();
    		if ( (  Objects.isNull(lFilterBean.getFromFactorStartDate()) && Objects.isNull(lFilterBean.getToFactorEndDate()) ||
    				Objects.isNull(lFilterBean.getFromFactorStartDate()) || Objects.isNull(lFilterBean.getToFactorEndDate()) ) 
    				&& (  Objects.isNull(lFilterBean.getFromAcceptanceDate()) && Objects.isNull(lFilterBean.getToAcceptanceDate()) ||
    	    				Objects.isNull(lFilterBean.getFromAcceptanceDate()) || Objects.isNull(lFilterBean.getToAcceptanceDate()) )) {
    			throw new CommonBusinessException("Please select atleast one date range filter");
    		}else {
    			if (!Objects.isNull(lFilterBean.getFromFactorStartDate()) && !Objects.isNull(lFilterBean.getToFactorEndDate())) {
    				long lDateDiff = OtherResourceCache.getInstance().getDiffInDays( lFilterBean.getToFactorEndDate(),lFilterBean.getFromFactorStartDate());
    				if (lDateDiff < 0 && lDateDiff > 31) {
    					throw new CommonBusinessException("Date range should be less than 31 days");
    				}
    			}
    			if (!Objects.isNull(lFilterBean.getFromAcceptanceDate()) && !Objects.isNull(lFilterBean.getToAcceptanceDate())) {
    				long lDateDiff = OtherResourceCache.getInstance().getDiffInDays(lFilterBean.getToAcceptanceDate(),lFilterBean.getFromAcceptanceDate());
    				if (lDateDiff > 0 && lDateDiff > 31) {
    					throw new CommonBusinessException("Date range should be less than 31 days");
    				}
    			}
    		}
    		lSql.append(" SELECT FACTORINGUNITS.* FROM FACTORINGUNITS " );
    		
    		if (CommonAppConstants.Yes.Yes.equals(lFilterBean.getFetchActiveBids())) {
    			lSql.append(" JOIN BIDS ON ( BDFUID=FUID ) ");
    			lSql.append(" WHERE 1=1 ");
    			lSql.append(" AND ( BDRATE IS NOT NULL OR BDPROVRATE IS NOT NULL) ");
    			lSql.append(" AND BDSTATUS = ").append(lDbHelper.formatString(Bid_Status.Active.getCode()));
    		}else {
    			lSql.append(" WHERE 1=1 ");
    		}
    		if (!(Objects.isNull(lFilterBean.getFromFactorStartDate()) && Objects.isNull(lFilterBean.getToFactorEndDate()))) {
    			lSql.append(" AND ( TO_DATE(TO_CHAR(FUFACTORSTARTDATETIME,'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getFromFactorStartDate()));
    			lSql.append(" AND ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getToFactorEndDate()));
    			lSql.append(" OR  TO_DATE(TO_CHAR(FUFACTORENDDATETIME,'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getFromFactorStartDate()));
    			lSql.append(" AND ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getToFactorEndDate()));
    			lSql.append(" ) ");
    		}
    		if (!(Objects.isNull(lFilterBean.getFromAcceptanceDate()) && Objects.isNull(lFilterBean.getToAcceptanceDate()))) {
    			lSql.append(" AND TO_DATE(TO_CHAR(FUACCEPTDATETIME,'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getFromAcceptanceDate()));
    			lSql.append(" AND ");
    			lSql.append(lDbHelper.formatDate(lFilterBean.getToAcceptanceDate()));
    		}
    		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
    		if (StringUtils.isNotBlank(getOrderBy()))
                lSql.append(" ORDER BY ").append(getOrderBy());
            lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
            
    	}else {
    		lList = super.getBeanList(pConnection, pFilterMap, pRecordCount, pAppUserBean);
    	}
        if (REPORTID_INST.equals(lReportId)) 
            setInstrumentLocationDetails(pConnection, lList);
        if (REPORTID_PSLINK.equals(lReportId)) 
            setPurchaserSupplierLinkDetails(pConnection, lList);
        return lList;
    }
    

    private void setInstrumentLocationDetails(Connection pConnection, List<InstrumentBean> pList) throws Exception {
    	if(pList!=null && pList.size() > 0){
            for (InstrumentBean lInstrumentBean : pList) {
            	lInstrumentBean.populateNonDatabaseFields();
            }
    	}
    }
    
    private void setPurchaserSupplierLinkDetails(Connection pConnection, List<PurchaserSupplierLinkBean> pList) throws Exception {
    	if(pList!=null && pList.size() > 0){
	    	for(PurchaserSupplierLinkBean lBean : pList){
	    		lBean.populateNonDatabaseFields();
	    	}
    	}
	}

}
