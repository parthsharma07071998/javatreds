package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyShareIndividualBO {
    public static final String TABLENAME_PROV = "CompanyShareIndividual_P";
    
    private GenericDAO<CompanyShareIndividualBean> companyShareIndividualDAO;
    private GenericDAO<CompanyShareIndividualBean> companyShareIndividualProvDAO;


    public CompanyShareIndividualBO() {
        super();
        companyShareIndividualDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class);
        companyShareIndividualProvDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class,TABLENAME_PROV);
    }
    
    private GenericDAO<CompanyShareIndividualBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyShareIndividualProvDAO;
    	}
    	return companyShareIndividualDAO;
    }
    
    public CompanyShareIndividualBean findBean(ExecutionContext pExecutionContext, 
        CompanyShareIndividualBean pFilterBean) throws Exception {
        CompanyShareIndividualBean lCompanyShareIndividualBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCompanyShareIndividualBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lCompanyShareIndividualBean.getCdId(), pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyShareIndividualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        if(pFilterBean.getIsProvisional()) {
        	CompanyShareIndividualBean lCSIActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lCSIActualBean!=null) {
            	lCSIActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lCSIActualBean,lCompanyShareIndividualBean);
                lCompanyShareIndividualBean.setModifiedData(lDiffData);
                return lCompanyShareIndividualBean;
            }
        }
        return lCompanyShareIndividualBean;
    }
    
    public List<CompanyShareIndividualBean> findList(ExecutionContext pExecutionContext, CompanyShareIndividualBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pAppUserBean) throws Exception {
    	if(AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain())){
    		if(pFilterBean.getCdId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
    	}else if(AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain())){
    		if(pFilterBean.getCdId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
    		TredsHelper.getInstance().checkRegistringEntityAccess(pFilterBean.getCdId(), pAppUserBean.getId());
    	}else{
    		if(pFilterBean.getCdId()==null)
    			pFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(pAppUserBean));
    	}
        return getDAO(pFilterBean.getIsProvisional()).findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, CompanyShareIndividualBean pCompanyShareIndividualBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CompanyShareIndividualBean lOldCompanyShareIndividualBean = null;
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyShareIndividualBean.setCdId(pUserBean.getId());
        }else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyShareIndividualBean.getCdId()==null || pCompanyShareIndividualBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if(TredsHelper.getInstance().isRegistrationApproved(lConnection, pCompanyShareIndividualBean.getCdId())){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
        	if( !TredsHelper.getInstance().hasAccessOnCompany(lConnection, pCompanyShareIndividualBean.getCdId(), (AppUserBean) pUserBean))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyShareIndividualBean.setIsProvisional(true); //done in resource 
        GenericDAO<CompanyShareIndividualBean> lCompanyShareIndividualDAO = getDAO(pCompanyShareIndividualBean.getIsProvisional());
        if (pNew) {
        	pCompanyShareIndividualBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyShareIndividualDAO.getTableName()+".id"));
            pCompanyShareIndividualBean.setRecordCreator(pUserBean.getId());
            lCompanyShareIndividualDAO.insert(lConnection, pCompanyShareIndividualBean);
            //only for jocata 
            if(!pCompanyShareIndividualBean.getIsProvisional()) {
                lCompanyShareIndividualDAO.insertAudit(lConnection, pCompanyShareIndividualBean, AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyShareIndividualBean = findBean(pExecutionContext, pCompanyShareIndividualBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyShareIndividualBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyShareIndividualBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            if(pCompanyShareIndividualBean.getRecordVersion() == null){
            	pCompanyShareIndividualBean.setRecordVersion(lOldCompanyShareIndividualBean.getRecordVersion());
            }
            lCompanyShareIndividualDAO.getBeanMeta().copyBean(pCompanyShareIndividualBean, lOldCompanyShareIndividualBean, BeanMeta.FIELDGROUP_UPDATE, null);
            pCompanyShareIndividualBean.setRecordUpdator(pUserBean.getId());
            if (lCompanyShareIndividualDAO.update(lConnection, lOldCompanyShareIndividualBean, BeanMeta.FIELDGROUP_UPDATE) == 0){
            	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }
            lCompanyShareIndividualDAO.getBeanMeta().copyBean(pCompanyShareIndividualBean, lOldCompanyShareIndividualBean,BeanMeta.FIELDGROUP_UPDATE,null);
            //only for jocata
            if(!pCompanyShareIndividualBean.getIsProvisional()) {
            	lCompanyShareIndividualDAO.insertAudit(lConnection, lOldCompanyShareIndividualBean, AuditAction.Update, pUserBean.getId());
            }
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyShareIndividualBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        CompanyShareIndividualBean lCompanyShareIndividualBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);
    	if (lCompanyShareIndividualBean==null) {
         	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())) {
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        }
        if((AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))&& 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyShareIndividualBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        lCompanyShareIndividualBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyShareIndividualBean);  
       // getDAO(pFilterBean.getIsProvisional()).insertAudit(lConnection, lCompanyShareIndividualBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }

	public String saveCompanyShareIndividual(ExecutionContext pExecutionContext, AppUserBean pUserBean, CompanyShareIndividualBean pCompanyShareIndividualBean, Long pCdId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyShareIndividualBean.setCdId(pCdId);
		}
		CompanyShareIndividualBean lCompanyShareIndividualBean = new CompanyShareIndividualBean();
		lCompanyShareIndividualBean.setRefId(pCompanyShareIndividualBean.getRefId());
		lCompanyShareIndividualBean = getDAO(false).findBean(lConnection, lCompanyShareIndividualBean);
		if (lCompanyShareIndividualBean != null) {
			lNew = false;
			pCompanyShareIndividualBean.setId(lCompanyShareIndividualBean.getId());
		}
		save(pExecutionContext, pCompanyShareIndividualBean, pUserBean, lNew);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
	}
    
}
