package com.xlx.treds.monitor;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
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

public class InstrumentFrequentRejectionTxnMon extends DefaultHandler {
    public InstrumentFrequentRejectionTxnMon(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
        GenericBean lFilterBean = new GenericBean();
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        Long lFilterPercent = null;
        if (lFilterMap.get("filterPercent")!=null)
        	lFilterPercent = Long.valueOf(lFilterMap.get("filterPercent").toString());
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT  INMAKERENTITY IFRMAKER ,INCOUNTERENTITY IFRCOUNTER, ");
		lSql.append(" SUM(CASE WHEN DUP IS NOT NULL THEN DUP ELSE 0 END) IFRREJRETCOUNT ");
		lSql.append(" ,SUM(1) IFRTOTALINSTRUMENT ");
		lSql.append(" , Round((sum(case when DUP is not null then DUP else 0 end)/sum(1)*100),2) IFRPercent ");
		lSql.append(" from instruments left outer join ");
		lSql.append(" ( Select IWFINID,Count(IWFINID) Dup from instrumentworkflow where iwfstatus in ('COURET','COUREJ') group by iWFINID) WorkFlow ");
		lSql.append(" on (inid=iwfinid) group by  inmakerentity,inCounterentity ");
		if (lFilterPercent==null) {
			lSql.append(" having  Round((sum(case when DUP is not null then DUP else 0 end)/sum(1)*100),2) > 0");
		}else {
			lSql.append(" having Round((sum(case when DUP is not null then DUP else 0 end)/sum(1)*100),2) >= ").append(lFilterPercent).append(" ");
		}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);

        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        String lMaker = null;
        String lCounter = null;
        AppEntityBean lMakerBean = null;
        AppEntityBean lCounterBean = null;
        for (GenericBean lGenericBean : lList) {
        	lMaker = (String) lGenericBean.getProperty("maker");
        	lCounter = (String) lGenericBean.getProperty("counter");
        	lMakerBean = TredsHelper.getInstance().getAppEntityBean(lMaker);
        	lCounterBean = TredsHelper.getInstance().getAppEntityBean(lCounter);
        	lGenericBean.setProperty("makerName", lMakerBean.getName());
        	lGenericBean.setProperty("counterName", lCounterBean.getName());
        }
        return lList;
    }
    

  
    

}
