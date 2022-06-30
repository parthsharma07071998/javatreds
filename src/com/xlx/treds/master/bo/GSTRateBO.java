package com.xlx.treds.master.bo;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.master.bean.GSTRateBean;

public class GSTRateBO {
    
    private GenericDAO<GSTRateBean> gSTRateDAO;

    public GSTRateBO() {
        super();
        gSTRateDAO = new GenericDAO<GSTRateBean>(GSTRateBean.class);
    }
    
    public GSTRateBean findBean(ExecutionContext pExecutionContext, 
        GSTRateBean pFilterBean) throws Exception {
        GSTRateBean lGSTRateBean = gSTRateDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lGSTRateBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lGSTRateBean;
    }
    
    public List<GSTRateBean> findList(ExecutionContext pExecutionContext, GSTRateBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return gSTRateDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, GSTRateBean pGSTRateBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        GSTRateBean lOldGSTRateBean = null;
        //TODO: the validation has to be checked before deployment
        if(true || validateGSTRates(lConnection, pGSTRateBean)){
            if (pNew) {
                pGSTRateBean.setRecordCreator(pUserBean.getId());
                gSTRateDAO.insert(lConnection, pGSTRateBean);
                gSTRateDAO.insertAudit(lConnection, pGSTRateBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
            } else {
                lOldGSTRateBean = findBean(pExecutionContext, pGSTRateBean);
                pGSTRateBean.setRecordUpdator(pUserBean.getId());
                if (gSTRateDAO.update(lConnection, pGSTRateBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                    throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
                pGSTRateBean.setRecordCreator(lOldGSTRateBean.getRecordCreator());
                pGSTRateBean.setRecordCreateTime(lOldGSTRateBean.getRecordCreateTime());
                gSTRateDAO.insertAudit(lConnection, pGSTRateBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            }        	
        }
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, GSTRateBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        GSTRateBean lGSTRateBean = findBean(pExecutionContext, pFilterBean);
        lGSTRateBean.setRecordUpdator(pUserBean.getId());
        gSTRateDAO.delete(lConnection, lGSTRateBean);        

        pExecutionContext.commitAndDispose();
    }
    
    private boolean validateGSTRates(Connection pConnection, GSTRateBean pGstRateBean) throws Exception {
    	Date lPrevToDate = null, lNextFromDate = null;
    	StringBuilder lSql = new StringBuilder();
    	List<GSTRateBean> lData = null;
    	//
    	lSql.append("SELECT * FROM GSTRates WHERE GRRecordVersion > 0 ");
    	if(pGstRateBean.getId()!=null) lSql.append(" AND GRId = ").append(pGstRateBean.getId());
    	lSql.append(" ORDER BY GRFromDate ");
    	//
    	lData = gSTRateDAO.findListFromSql(pConnection, lSql.toString(), 0);
    	for (GSTRateBean lGstRateBean : lData){
    		if((pGstRateBean.getFromDate().compareTo(lGstRateBean.getToDate()) <= 0) && 
		 			(pGstRateBean.getToDate().compareTo(lGstRateBean.getFromDate()) >= 0 )){
		 		throw new CommonBusinessException("GST Rate already defined for overlapping window "+lGstRateBean.getFromDate() + " - "+lGstRateBean.getToDate());
		 	}
		 	// dbbean represents future window wrt input
		 	if(pGstRateBean.getFromDate().after(lGstRateBean.getToDate())) {
		 		if(lNextFromDate == null ||  lGstRateBean.getFromDate().before(lNextFromDate))
		 			lNextFromDate = lGstRateBean.getFromDate();
		 	}
		 	// past window
		 	if(lGstRateBean.getToDate().before(pGstRateBean.getFromDate())) {
		 		if(lPrevToDate == null ||  lGstRateBean.getToDate().after(lPrevToDate))
		 			lPrevToDate = lGstRateBean.getToDate();
		 	}
		 }
		 if(lNextFromDate !=null){
			 int lDayDiff = CommonUtilities.getDayDiff(lNextFromDate, pGstRateBean.getToDate());
			 if(lDayDiff > 1){
				 throw new CommonBusinessException("GST Rates not defined for period between " + pGstRateBean.getToDate() + " to " + lNextFromDate );
			 }
		 }
		 if(lPrevToDate !=null){
			 int lDayDiff = CommonUtilities.getDayDiff(pGstRateBean.getFromDate(), lPrevToDate);
			 if(lDayDiff > 1){
				 throw new CommonBusinessException("GST Rates not defined for period between " + lPrevToDate + " to " + pGstRateBean.getFromDate() );
			 }
		 }
    	//
    	return true;
    }
    
}
