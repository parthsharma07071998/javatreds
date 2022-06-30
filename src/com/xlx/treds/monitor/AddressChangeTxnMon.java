package com.xlx.treds.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;

import groovy.json.JsonSlurper;

public class AddressChangeTxnMon extends DefaultHandler {
    public AddressChangeTxnMon(){
    	super();
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
    	GenericBean lFilterBean = new GenericBean();
    	DBHelper lDbHelper = DBHelper.getInstance();
    	Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        Date lFilterFromDate = (Date)lFilterBean.getProperty("fromDate");
        Date lFilterToDate = (Date)lFilterBean.getProperty("toDate");
		StringBuilder lSql1 = new StringBuilder();
		StringBuilder lSql2= new StringBuilder();
		StringBuilder lSql = new StringBuilder();
		StringBuilder lFilterSql = new StringBuilder();
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
			lFilterSql.append(" AND ACTIONTIME between ").append(lDbHelper.formatDate(lFilterFromDate));
			lFilterSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
    	}else{
    		if (lFilterFromDate!=null){
    			lFilterSql.append(" AND ACTIONTIME >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lFilterSql.append(" AND ACTIONTIME <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
    	}
		//
		lSql1.append(" SELECT 'DETAILS' CDTYPE,CDID CDKEY ,CDCODE,CDCOMPANYNAME ");
		lSql1.append(" ,NULL CDNAME,CDCORLINE1 CDLINE1,CDCORLINE2 CDLINE2,CDCORLINE3 CDLINE3 ");
		lSql1.append(" ,CDCORCOUNTRY CDCOUNTRY,CDCORSTATE CDSTATE,CDCORDISTRICT CDDISTRICT ");
		lSql1.append(" ,CDCORCITY CDCITY,CDCORZIPCODE CDZIPCODE,ACTIONTIME CDFIRSTCHANGE  ");
		lSql1.append(" FROM COMPANYDETAILS_A ");
		lSql1.append(" WHERE 1=1 ");
		lSql1.append(lFilterSql.toString());
		//
		//
		lSql2.append(" SELECT 'LOCATION' TYPE,CLID CDKEY,CDCODE,cdcompanyName , CLNAME CDNAME ");
		lSql2.append(" ,CLLINE1 CDLINE1,CLLINE2 CDLINE2,CLLINE3 CDLINE3,CLCOUNTRY CDCOUNTRY ");
		lSql2.append(" ,CLSTATE CDSTATE,CLDISTRICT CDDISTRICT,CDCORCITY CDCITY ");
		lSql2.append(" ,CLZIPCODE CDZIPCODE,ACTIONTIME CDFIRSTCHANGE ");
		lSql2.append(" FROM COMPANYLOCATIONS_A,COMPANYDETAILS WHERE CDID=CLCDID ");
		lSql2.append(lFilterSql.toString());
		//
		lSql.append(" SELECT * FROM ( ");
		lSql.append(lSql1.toString());
		lSql.append(" UNION ");
		lSql.append(lSql2.toString());
		lSql.append(" ) ");
    	if (StringUtils.isNotBlank(getOrderBy()))
    		lSql.append(" ORDER BY ").append(getOrderBy());
    	List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
    	List<GenericBean> lRtnList = new ArrayList<GenericBean>(); 
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	GenericBean lPrevBean = null;
    	String lPrevKey = null;
    	List<GenericBean> lCompareList=null;
    	Map<String, Object> lIpMap = new HashMap<>();
    	Long lId = null;
		String lType = null;
		String lKey = null;
		GenericBean lBeanToChange = null;
		HashMap<String, GenericBean> lMap = new HashMap<>();
    	for (GenericBean lBean:lList) {
    		Long lCount = null;
    		lId = (Long) lBean.getProperty("key");
			lType = (String) lBean.getProperty("type");
			lKey = lType+CommonConstants.KEY_SEPARATOR+lId.toString();
			if (!lMap.containsKey(lKey)) {
				lBeanToChange = new GenericBean();
				getGenericDAO().getBeanMeta().copyBean(lBean, lBeanToChange);
				lBeanToChange.setProperty("changeCount", new Long(0));
				lMap.put(lKey, lBeanToChange);
			}
			if (lPrevBean!=null) {
				if (lKey.equals(lPrevKey)) {
					if (!((String)lPrevBean.getProperty("zipCode")).equals(((String)lBean.getProperty("zipCode")))) {
						lBeanToChange = lMap.get(lKey);
						lCount =(Long) lBeanToChange.getProperty("changeCount");
						lBeanToChange.setProperty("changeCount",++lCount );
						if (lCount==1) {
							lBeanToChange.setProperty("firstChange",lBean.getProperty("firstChange"));
						}
						lBeanToChange.setProperty("lastChange",lBean.getProperty("firstChange"));
					}
				}
    		}
			lPrevBean = lBean;
			lPrevKey = lKey;
		}
    	lRtnList = lMap.values().stream().collect(Collectors.toList()); 
    	return lRtnList;
	}
}
