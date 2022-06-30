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
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyShareEntityBO {
    public static final String TABLENAME_PROV = "CompanyShareEntity_P";
    
    private GenericDAO<CompanyShareEntityBean> companyShareEntityDAO;
    private GenericDAO<CompanyShareEntityBean> companyShareEntityProvDAO;

    public CompanyShareEntityBO() {
        super();
        companyShareEntityDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class);
        companyShareEntityProvDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class,TABLENAME_PROV);
    }
    
    private GenericDAO<CompanyShareEntityBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyShareEntityProvDAO;
    	}
    	return companyShareEntityDAO;
    }
    
    public CompanyShareEntityBean findBean(ExecutionContext pExecutionContext, 
        CompanyShareEntityBean pFilterBean) throws Exception {
        CompanyShareEntityBean lCompanyShareEntityBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCompanyShareEntityBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lCompanyShareEntityBean.getCdId(),pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyShareEntityBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        if(pFilterBean.getIsProvisional()) {
        	CompanyShareEntityBean lCSEActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lCSEActualBean!=null) {
            	lCSEActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                //
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lCSEActualBean,lCompanyShareEntityBean);
                lCompanyShareEntityBean.setModifiedData(lDiffData);
                return lCompanyShareEntityBean;
            }
        }
        return lCompanyShareEntityBean;
    }
    
    public List<CompanyShareEntityBean> findList(ExecutionContext pExecutionContext, CompanyShareEntityBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
    		if(pFilterBean.getCdId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
    	}else if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
    		if(pFilterBean.getCdId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
    		TredsHelper.getInstance().checkRegistringEntityAccess(pFilterBean.getCdId(), pUserBean.getId());
    	}else{
    		if(pFilterBean.getCdId()==null)
    			pFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(pUserBean));
    	}
        return getDAO(pFilterBean.getIsProvisional()).findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, CompanyShareEntityBean pCompanyShareEntityBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CompanyShareEntityBean lOldCompanyShareEntityBean = null;
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyShareEntityBean.setCdId(pUserBean.getId());
        }else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyShareEntityBean.getCdId()==null || pCompanyShareEntityBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
    	else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if( !TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), pCompanyShareEntityBean.getCdId(), (AppUserBean) pUserBean))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyShareEntityBean.setIsProvisional(true);//THIS IS DONE IN RESOURCE -SINCE DATA COMING FROM JOCATA DIRECTLY SAVES TO MAIN TABLE
        GenericDAO<CompanyShareEntityBean> lCompanyShareEntityDAO = getDAO(pCompanyShareEntityBean.getIsProvisional());
        //
        if (pNew) {
        	pCompanyShareEntityBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyShareEntityDAO.getTableName()+".id"));
            pCompanyShareEntityBean.setRecordCreator(pUserBean.getId());
            lCompanyShareEntityDAO.insert(lConnection, pCompanyShareEntityBean);
            //only for jocata
            if(!pCompanyShareEntityBean.getIsProvisional()) {
            	lCompanyShareEntityDAO.insertAudit(lConnection, pCompanyShareEntityBean, AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyShareEntityBean = findBean(pExecutionContext, pCompanyShareEntityBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyShareEntityBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyShareEntityBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            if(pCompanyShareEntityBean.getRecordVersion() == null){
            	pCompanyShareEntityBean.setRecordVersion(lOldCompanyShareEntityBean.getRecordVersion());
            }
            lCompanyShareEntityDAO.getBeanMeta().copyBean(pCompanyShareEntityBean, lOldCompanyShareEntityBean, BeanMeta.FIELDGROUP_UPDATE, null);
            pCompanyShareEntityBean.setRecordUpdator(pUserBean.getId());
            if (lCompanyShareEntityDAO.update(lConnection, lOldCompanyShareEntityBean, BeanMeta.FIELDGROUP_UPDATE) == 0){
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }
            lCompanyShareEntityDAO.getBeanMeta().copyBean(pCompanyShareEntityBean, lOldCompanyShareEntityBean,BeanMeta.FIELDGROUP_UPDATE,null);
            //only for jocata
            if(!pCompanyShareEntityBean.getIsProvisional()) {
                lCompanyShareEntityDAO.insertAudit(lConnection, lOldCompanyShareEntityBean, AuditAction.Update, pUserBean.getId());
            }
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyShareEntityBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CompanyShareEntityBean lCompanyShareEntityBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);
        if (lCompanyShareEntityBean==null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())) {
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        }
        if((AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))&& 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyShareEntityBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        lCompanyShareEntityBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyShareEntityBean);  
    	//getDAO(pFilterBean.getIsProvisional()).insertAudit(lConnection, lCompanyShareEntityBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }

	public String saveCompanyShareEntity(ExecutionContext pExecutionContext, AppUserBean pUserBean, CompanyShareEntityBean pCompanyShareEntityBean, Long pCdId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyShareEntityBean.setCdId(pCdId);
		}
		CompanyShareEntityBean lCompanyShareEntityBean = new CompanyShareEntityBean();
		lCompanyShareEntityBean.setRefId(pCompanyShareEntityBean.getRefId());
		lCompanyShareEntityBean = getDAO(false).findBean(lConnection, lCompanyShareEntityBean);
		if (lCompanyShareEntityBean != null) {
			lNew = false;
			pCompanyShareEntityBean.setId(lCompanyShareEntityBean.getId());
		}
		save(pExecutionContext, pCompanyShareEntityBean, pUserBean, lNew);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
		
	}
    
}
