package com.xlx.treds.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.model.PropertyNode.EndComparator;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

import groovy.json.JsonSlurper;

public class SameIpTransactionTxnMon extends DefaultHandler {
    public SameIpTransactionTxnMon(){
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
    	if  (lFilterMap.get("days")==null) {
    		if (lFilterFromDate == null)
        		throw new CommonBusinessException("From date is mandatory");
        	if (lFilterToDate == null)
        		throw new CommonBusinessException("To date is mandatory");
    	}
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT INID ,IWFENTITY INENTITY ,LSDETAILS INDETAILS,INMAKERENTITY,INCOUNTERENTITY,ININSTNUMBER,INAMOUNT ");
    	lSql.append(" FROM INSTRUMENTWORKFLOW , LOGINSESSIONS ,INSTRUMENTS ");
    	lSql.append(" WHERE IWFINID=INID AND LSAUID=IWFAUID AND LSDETAILS IS NOT NULL AND LSSTATUS IN ('S','C') ");
    	lSql.append(" AND IWFSTATUSUPDATETIME BETWEEN LSRECORDCREATETIME AND NVL(LSRECORDUPDATETIME,SYSDATE) ");
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
    	if (StringUtils.isNotBlank(getOrderBy()))
    		lSql.append(" ORDER BY ").append(getOrderBy());
    	List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
    	List<GenericBean> lRtnList = new ArrayList<GenericBean>(); 
    	String lCounter = null;
    	String lMaker = null;
    	AppEntityBean lCounterBean = null;
    	AppEntityBean lMakerBean = null;
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	GenericBean lPrevBean = null;
    	Long lPrevInId = null;
    	Set<String> lMakerIpList=null;
    	Set<String> lCounterIpList=null;
    	Map<String, Object> lIpMap = new HashMap<>();
    	int lPtr=0;
    	while (true) {
    		GenericBean lBean = null;
    		Long lId = null;
    		if (lPtr < lList.size()) {
    			lBean = lList.get(lPtr);
    			lId = (Long) lBean.getProperty("id");
    		}
    		if ((lPrevInId == null) || !lPrevInId.equals(lId) || (lBean == null)) {
    			if (lPrevInId != null) {
    				String lDuplicateIp = null;
    				for (String lIp : lMakerIpList) {
    					if (lCounterIpList.contains(lIp)) {
    						lDuplicateIp=lIp;
    						break;
    					}
    				}
    				if (lDuplicateIp != null) {
    					lPrevBean.setProperty("duplicateIp", lDuplicateIp);
	        			lCounter = (String) lPrevBean.getProperty("counterEntity");
	                	lMaker = (String) lPrevBean.getProperty("makerEntity");
	                	lCounterBean = TredsHelper.getInstance().getAppEntityBean(lCounter);
	                	lMakerBean = TredsHelper.getInstance().getAppEntityBean(lMaker);
	                	lPrevBean.setProperty("counterName", lCounterBean.getName());
	                	lPrevBean.setProperty("makerName", lMakerBean.getName());
    					lRtnList.add(lPrevBean);
    				}
    			}
    			if (lBean != null) {
    				lPrevBean = lBean;
    				lPrevInId = lId;
    				lMakerIpList = new HashSet<>();
    				lCounterIpList = new HashSet<>();
    			}
    			if (lBean == null) {
    				break;
    			}
    		} // end if ((lPrevInId == null) || !lPrevInId.equals((Long) lBean.getProperty("inId")) || (lBean==null)) 
    		lIpMap = (Map<String, Object>) lJsonSlurper.parseText((String)lBean.getProperty("details"));
    		if (((String)lBean.getProperty("counterEntity")).equals(((String)lBean.getProperty("entity")))){
    			lCounterIpList.add(lIpMap.get("requestIp").toString());
    		} else if (((String)lBean.getProperty("makerEntity")).equals(((String)lBean.getProperty("entity")))) {
    			lMakerIpList.add(lIpMap.get("requestIp").toString());
    		}
    		lPtr++;
    	} // end while
    	return lRtnList;
    }
}
