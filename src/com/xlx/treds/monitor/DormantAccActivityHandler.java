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

public class DormantAccActivityHandler extends DefaultHandler {
    public DormantAccActivityHandler(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
        GenericBean lFilterBean = new GenericBean();
        DBHelper lDbHelper = DBHelper.getInstance();
        Long lDiffrence = null;
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
    	lDiffrence = (Long) lFilterBean.getProperty("diffrence");
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT ");
		lSql.append(" OBTXNENTITY TMENTITY, ");
		lSql.append(" CDCOMPANYNAME TMCOMPANYNAME , ");
		lSql.append(" OBPAYDETAIL1 TMACCOUNTNO , ");
		lSql.append(" CBDIFSC TMIFSC , ");
		lSql.append(" TO_DATE(TO_CHAR(MIN(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY') TMBEGINDATE , ");
		lSql.append(" TO_DATE(TO_CHAR(MAX(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY') TMENDDATE , ");
		lSql.append(" TO_DATE(TO_CHAR(MAX(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY') - TO_DATE(TO_CHAR(MIN(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY')  TMDIFFRENCE ");
		lSql.append(" FROM ");
		lSql.append(" OBLIGATIONS,COMPANYBANKDETAILS,COMPANYDETAILS ");
		lSql.append(" WHERE 1=1");
		lSql.append(" AND OBRECORDVERSION > 0 ");
		lSql.append(" AND OBPAYDETAIL1 IS NOT null  ");
		lSql.append(" AND OBPAYDETAIL1=CBDACCNO ");
		lSql.append(" AND CDID=CBDCDID ");
		lSql.append(" GROUP BY OBTXNENTITY,OBPAYDETAIL1 ,CBDIFSC,CDCOMPANYNAME ");
		lSql.append(" HAVING TO_DATE(TO_CHAR(MAX(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY') - TO_DATE(TO_CHAR(MIN(OBDATE),'DD-MM-YYYY'),'DD-MM-YYYY') <= ").append(lDiffrence).append(" ");
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        HashMap<String, String> lBankHash = TredsHelper.getInstance().getBankName();
        for (GenericBean lGenericBean : lList) {
        	lGenericBean.setProperty("bankName", lBankHash.get(lGenericBean.getProperty("ifsc").toString().substring(0, 4)));
        }
        return lList;
    }
    

  
    

}
