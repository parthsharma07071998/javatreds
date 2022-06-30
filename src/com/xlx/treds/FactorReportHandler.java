package com.xlx.treds;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.other.bean.FactoredReportBean;

public class FactorReportHandler extends DefaultHandler {
    public FactorReportHandler(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	 FactoredReportBean lFilterBean = (FactoredReportBean) super.getGenericDAO().newBean();
         super.getBeanMeta().validateAndParse(lFilterBean, pFilterMap, null);
         StringBuilder lSql = new StringBuilder();
         StringBuilder lFilterSql = new StringBuilder();
         DBHelper lDbHelper = DBHelper.getInstance();
         if (lFilterBean.getFromDate()!=null && lFilterBean.getToDate()!=null){
        	 lFilterSql.append(" AND TO_DATE(FUACCEPTDATETIME,'dd-mm-yyyy') BETWEEN ");
        	 lFilterSql.append(lDbHelper.formatDate(lFilterBean.getFromDate()));
        	 lFilterSql.append(" AND ");
        	 lFilterSql.append(lDbHelper.formatDate(lFilterBean.getToDate()));
     	}else{
     		if (lFilterBean.getFromDate()!=null){
     			lFilterSql.append(" AND TO_DATE(fainFuAcceptDateTime,'dd-mm-yyyy') >=  ");
     			lFilterSql.append(lDbHelper.formatDate(lFilterBean.getFromDate()));
     		}
     		if (lFilterBean.getToDate()!=null){
     			lFilterSql.append(" AND TO_DATE(fainFuAcceptDateTime,'dd-mm-yyyy') <= ");
     			lFilterSql.append(lDbHelper.formatDate(lFilterBean.getToDate()));
         	}
     	}
     	if (lFilterBean.getFuPurchaser()!=null){
     		lFilterSql.append(" AND faFUPURCHASER = ").append(lDbHelper.formatString(lFilterBean.getFuPurchaser()));
     	}
     	if (lFilterBean.getInSalesCategory()!=null){
     		lFilterSql.append(" AND fainSalesCategory = ").append(lDbHelper.formatString(lFilterBean.getInSalesCategory()));
     	}
     	lFilterBean.setFromDate(null);
     	lFilterBean.setToDate(null);
     	lFilterBean.setFuPurchaser(null);
     	lFilterBean.setInSalesCategory(null);
        lSql.append(super.getGenericDAO().getListSql(lFilterBean, (String)null));
	     if(!lFilterSql.toString().isEmpty()){
	    	 if (!lSql.toString().toUpperCase().contains(" WHERE ")){
	    		 lSql.append(" WHERE 1=1 ");
	         }
	    	 lSql.append(lFilterSql.toString());
	     }
	        
	     if (StringUtils.isNotBlank(super.getOrderBy()))
	         lSql.append(" ORDER BY ").append(super.getOrderBy());
	     List lList = super.getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
	     List<FactoredReportBean> lRtnList = new ArrayList<>();
	     for(Object lBean : lList){
	    	 FactoredReportBean lReportBean  = (FactoredReportBean) lBean;
	    	 lReportBean.setPurchaser(TredsHelper.getInstance().getAppEntityBean(lReportBean.getFuPurchaser()).getName());
	    	 lReportBean.setSupplier(TredsHelper.getInstance().getAppEntityBean(lReportBean.getFuPurchaser()).getName());
	    	 if (lReportBean.getGroupFlag()==null){
	    		 lRtnList.add(lReportBean);
	    	 }
	     }
	     return lRtnList;
    }
}
