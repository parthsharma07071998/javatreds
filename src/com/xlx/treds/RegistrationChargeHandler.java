package com.xlx.treds;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.monitor.bean.SuppliersFactoredConcentrationBean;

public class RegistrationChargeHandler extends DefaultHandler{
	
    public static final String REPORTID_PAYMENTNOTRECEIVED = "paymentnotreceived";
    public static final String REPORTID_WAIVERPROVIDED = "waiverprovided";
    public static final String REPORTID_EXTENSIONPROVIDED = "extensionprovided";
    
    
    public RegistrationChargeHandler(){
    	super();
    }
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
    	DBHelper lDbHelper = DBHelper.getInstance();
		Object lFilterBean = super.getGenericDAO().newBean();
		super.getBeanMeta().validateAndParse(lFilterBean, pFilterMap, null);
		StringBuilder lSql = new StringBuilder();
		lSql.append(super.getGenericDAO().getListSql(null, (String)null));
		RegistrationChargeBean lRCBean = (RegistrationChargeBean) lFilterBean;
		if(REPORTID_PAYMENTNOTRECEIVED.equals(lReportId)){
			if(lRCBean.getPaymentStartDate() != null){
				lSql.append(" AND RCPAYMENTDATE >=").append(DBHelper.getInstance().formatDate(lRCBean.getPaymentStartDate()));
			}
			if(lRCBean.getPaymentEndDate() != null){
				lSql.append(" AND RCPAYMENTDATE <=").append(DBHelper.getInstance().formatDate(lRCBean.getPaymentEndDate()));
			}
		}
		if(REPORTID_WAIVERPROVIDED.equals(lReportId)){
			if(lRCBean.getEffectiveStartDate() != null){
				lSql.append(" AND RCEFFECTIVEDATE >=").append(DBHelper.getInstance().formatDate(lRCBean.getEffectiveStartDate()));
			}
			if(lRCBean.getEffectiveEndDate() != null){
				lSql.append(" AND RCEFFECTIVEDATE <=").append(DBHelper.getInstance().formatDate(lRCBean.getEffectiveEndDate()));
			}
		}
		if(REPORTID_EXTENSIONPROVIDED.equals(lReportId)){
			if(lRCBean.getExtendedStartDate() != null){
				lSql.append(" AND RCEXTENDEDDATE >=").append(DBHelper.getInstance().formatDate(lRCBean.getExtendedStartDate()));
			}
			if(lRCBean.getExtendedEndDate() != null){
				lSql.append(" AND RCEXTENDEDDATE <=").append(DBHelper.getInstance().formatDate(lRCBean.getExtendedEndDate()));
			}
		}
		if(lRCBean.getEntityType() != null){
			lSql.append(" AND RCENTITYTYPE =").append(DBHelper.getInstance().formatString(lRCBean.getEntityType().toString()));
		}
        List lList = super.getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        return lList;
    }
}
