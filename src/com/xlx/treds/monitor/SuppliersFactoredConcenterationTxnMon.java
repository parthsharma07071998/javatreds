package com.xlx.treds.monitor;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.monitor.bean.L2NachFailBean;
import com.xlx.treds.monitor.bean.SuppliersFactoredConcentrationBean;

public class SuppliersFactoredConcenterationTxnMon extends DefaultHandler {
    public SuppliersFactoredConcenterationTxnMon(){
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
		lSql.append(" WHERE 1=1 ");
		SuppliersFactoredConcentrationBean lBean = (SuppliersFactoredConcentrationBean) lFilterBean;
        super.getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
        if (StringUtils.isNotBlank(super.getOrderBy()))
            lSql.append(" ORDER BY ").append(super.getOrderBy());
        List lList = super.getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        return lList;
    }
    

  
    

}
