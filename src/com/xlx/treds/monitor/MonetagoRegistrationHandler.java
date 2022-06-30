package com.xlx.treds.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.text.DateFormatter;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;

public class MonetagoRegistrationHandler extends DefaultHandler {
	
	public static final String monetagoregistrations = "monetagoregistrations";
	public static final String monetagoregistrationcount = "monetagoregistrationcount";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
	
    public MonetagoRegistrationHandler(){
    	super();
    }
    
    @Override
    protected List<GenericBean> getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
        List<GenericBean> lRtnList = new ArrayList<GenericBean>(); 
        String lReportId = getId();
        Date lFilterFromDate =  null;
        Date lFilterToDate = null;
        Date lDate = null;
        Long lCount = null;
        Time lTime = null;
        String lLedgerId = null;
        GenericBean lFilterBean = new GenericBean();
        DBHelper lDbHelper = DBHelper.getInstance();
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        if(Objects.isNull(lFilterBean.getProperty("fromDate"))){
        	throw new CommonBusinessException("From date is mandatory");
        }
        if(Objects.isNull(lFilterBean.getProperty("toDate"))){
        	throw new CommonBusinessException("To date is mandatory");
        }
        lFilterFromDate = (Date)lFilterBean.getProperty("fromDate");
        lFilterToDate = (Date)lFilterBean.getProperty("toDate");
		StringBuilder lSql = new StringBuilder();
		if(monetagoregistrationcount.equals(lReportId)){
			lSql.append(" SELECT MRDATE , COUNT(*) MRCOUNT from ( ");
		}
		lSql.append(" SELECT TO_DATE(TO_CHAR(MRREXCHRECVDATETIME,'DD-MM-YYYY'),'DD-MM-YYYY') MRDATE,  TO_DATE(TO_char (MRREXCHRECVDATETIME, 'HH:MI:SS'),'HH:MI:SS') mrcreateTime, ");
		lSql.append(" MRROUTPUTTXNID MRLEDGERID FROM MONETAGOREQUESTRESPONSES ");
		lSql.append(" WHERE MRRTYPE IN ('R','RB') AND MRRAPIRESPONSESTATUS = 'S' ");
		if(lFilterFromDate != null && lFilterToDate != null){
    		lSql.append(" AND MRREXCHRECVDATETIME between ").append(lDbHelper.formatDate(lFilterFromDate));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
		}else{
    		if (lFilterFromDate!=null){
        		lSql.append(" AND MRREXCHRECVDATETIME >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lSql.append(" AND MRREXCHRECVDATETIME <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
		}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
		if(monetagoregistrations.equals(lReportId)){
			lSql.append(" ORDER BY to_char(MRREXCHRECVDATETIME,'dd-MM-yyyy'), TO_CHAR (MRREXCHRECVDATETIME, 'HH:MI:SS') ");
		}
		if(monetagoregistrationcount.equals(lReportId)){
			lSql.append(" ) group by MRDATE");
		}
        List<GenericBean> lList = getGenericDAO().findListFromSql(lDbHelper.getConnection(), lSql.toString(), pRecordCount);
    return lList;
    }
} 
