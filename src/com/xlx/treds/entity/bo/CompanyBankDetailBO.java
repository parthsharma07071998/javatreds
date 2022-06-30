package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyBankDetailBO {
    public static final String TABLENAME_PROV = "CompanyBankDetails_P";
    //
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailProvDAO;

    public CompanyBankDetailBO() {
        super();
        companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        companyBankDetailProvDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class, TABLENAME_PROV);
    }
    
    private GenericDAO<CompanyBankDetailBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyBankDetailProvDAO;
    	}
    	return companyBankDetailDAO;
    }
    
    public CompanyBankDetailBean findBean(ExecutionContext pExecutionContext, 
        CompanyBankDetailBean pFilterBean) throws Exception {
    	Connection lConnection  = pExecutionContext.getConnection();
        CompanyBankDetailBean lCompanyBankDetailBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);
        if (lCompanyBankDetailBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(lConnection, lCompanyBankDetailBean.getCdId(),pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyBankDetailBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        //
        if(pFilterBean.getIsProvisional()) {
            CompanyBankDetailBean lCBDActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lCBDActualBean!=null) {
            	lCBDActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                //
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lCBDActualBean,lCompanyBankDetailBean);
                lCompanyBankDetailBean.setModifiedData(lDiffData);
                return lCompanyBankDetailBean;
            }
        }
        //
        return lCompanyBankDetailBean;
    }
    
    public List<CompanyBankDetailBean> findList(ExecutionContext pExecutionContext, CompanyBankDetailBean pFilterBean, 
        String pColumnList, IAppUserBean pAppUserBean) throws Exception {
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
    
    public void save(ExecutionContext pExecutionContext, CompanyBankDetailBean pCompanyBankDetailBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyBankDetailBean.setCdId(pUserBean.getId());
        }else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyBankDetailBean.getCdId()==null || pCompanyBankDetailBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
    	else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if( !TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), pCompanyBankDetailBean.getCdId(), (AppUserBean) pUserBean))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        if((AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())) && 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, pCompanyBankDetailBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        if (pCompanyBankDetailBean.getLeadBank() == CommonAppConstants.Yes.Yes) {
            if (pCompanyBankDetailBean.getBankingType() != CompanyBankDetailBean.BankingType.Consortium) 
                throw new CommonBusinessException("Bank cannot be a lead bank");
        }
        if (pCompanyBankDetailBean.getAccType() == CompanyBankDetailBean.AccType.Term_Loan) {
            if (pCompanyBankDetailBean.getLeadBank() != CommonAppConstants.Yes.Yes)
                throw new CommonBusinessException("Term loan allowed only for Lead bank in Consortium");
            if(CommonAppConstants.Yes.Yes.equals(pCompanyBankDetailBean.getDefaultAccount()))
            	throw new CommonBusinessException("Term loan cannot be marked Designated account.");
        }
        if(CommonUtilities.hasValue(pCompanyBankDetailBean.getIfsc()))
        {
        	if(!pCompanyBankDetailBean.getIfsc().startsWith(pCompanyBankDetailBean.getBank()))
            	throw new CommonBusinessException("The IFSC Code does not belong to the selected Bank.");
            BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
            lFilterBean.setIfsc(pCompanyBankDetailBean.getIfsc());
            BankBranchDetailBean lBankBranchDetailBean = (new GenericDAO<BankBranchDetailBean>(BankBranchDetailBean.class)).findByPrimaryKey(pExecutionContext.getConnection(), lFilterBean);
            if (lBankBranchDetailBean == null) 
                throw new CommonBusinessException("Invalid IFSC Code.");
        }
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyBankDetailBean.setIsProvisional(true);//THIS IS DONE IN RESOURCE -SINCE DATA COMING FROM JOCATA DIRECTLY SAVES TO MAIN TABLE
        GenericDAO<CompanyBankDetailBean> lCompanyBankDetailDAO = getDAO(pCompanyBankDetailBean.getIsProvisional()); //this is true ie from provisional

        CompanyBankDetailBean lOldCompanyBankDetailBean = null;
        if (pNew) {
        	pCompanyBankDetailBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyBankDetailDAO.getTableName()+".id"));
            pCompanyBankDetailBean.setRecordCreator(pUserBean.getId());
            lCompanyBankDetailDAO.insert(lConnection, pCompanyBankDetailBean);
            //only for jocata
            if(!pCompanyBankDetailBean.getIsProvisional()) {
            	lCompanyBankDetailDAO.insertAudit(lConnection, pCompanyBankDetailBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyBankDetailBean = findBean(pExecutionContext, pCompanyBankDetailBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyBankDetailBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyBankDetailBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            //old and new compare - default
            //if from yes to no - check whether this id has been used inthe companylocation cdid, cbdid
            //lOldCompanyBankDetailBean.getCdId(), lOldCompanyBankDetailBean.getId()
            if (TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, lOldCompanyBankDetailBean.getCdId(),pCompanyBankDetailBean.getIsProvisional()) && 
            		TredsHelper.getInstance().isBankMappedToLocation(lConnection, lOldCompanyBankDetailBean.getCdId(),lOldCompanyBankDetailBean.getId())){
            	throw new CommonBusinessException("Bank mapped to Location.");
            }
            if(pCompanyBankDetailBean.getRecordVersion() == null){
            	pCompanyBankDetailBean.setRecordVersion(lOldCompanyBankDetailBean.getRecordVersion());
            }
            lCompanyBankDetailDAO.getBeanMeta().copyBean(pCompanyBankDetailBean, lOldCompanyBankDetailBean, BeanMeta.FIELDGROUP_UPDATE, null);
            lOldCompanyBankDetailBean.setRecordUpdator(pUserBean.getId());
          
            if (lCompanyBankDetailDAO.update(lConnection, lOldCompanyBankDetailBean, BeanMeta.FIELDGROUP_UPDATE) == 0){
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }
            //only for jocata
            if(!pCompanyBankDetailBean.getIsProvisional()) {
            	lCompanyBankDetailDAO.insertAudit(lConnection, lOldCompanyBankDetailBean, AuditAction.Update, pUserBean.getId());
            }
        }
        // if default then remove other default account if any
        if (pCompanyBankDetailBean.getDefaultAccount() != null) {
            CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
            lFilterBean.setCdId(pCompanyBankDetailBean.getCdId());
            lFilterBean.setDefaultAccount(CommonAppConstants.Yes.Yes);
            List<CompanyBankDetailBean> lBankDetails = lCompanyBankDetailDAO.findList(pExecutionContext.getConnection(), lFilterBean, (String)null);
            for (CompanyBankDetailBean lCompanyBankDetailBean : lBankDetails) {
                if (!lCompanyBankDetailBean.getId().equals(pCompanyBankDetailBean.getId())) {
                    lCompanyBankDetailBean.setDefaultAccount(null);
                    lCompanyBankDetailBean.setRecordUpdator(pUserBean.getId());
                    lCompanyBankDetailDAO.update(pExecutionContext.getConnection(), lCompanyBankDetailBean, BeanMeta.FIELDGROUP_UPDATE);
                	//lCompanyBankDetailDAO.insertAudit(lConnection, lCompanyBankDetailBean, AuditAction.Update, pUserBean.getId());
                }
            }
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyBankDetailBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        CompanyBankDetailBean lCompanyBankDetailBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean); //this is true ie. from provisional
        if (lCompanyBankDetailBean==null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())) {
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        }
        if( (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()) ) && 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyBankDetailBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        pExecutionContext.setAutoCommit(false);
        lCompanyBankDetailBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyBankDetailBean);   
        //getDAO(pFilterBean.getIsProvisional()).insertAudit(lConnection, lCompanyBankDetailBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }
    
    public String saveCompanyBank(ExecutionContext pExecutionContext,AppUserBean pUserBean,CompanyBankDetailBean pCompanyBankDetailBean, Long pCdId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyBankDetailBean.setCdId(pCdId);
		}
		CompanyBankDetailBean lCompanyBankDetailBean = new CompanyBankDetailBean();
		lCompanyBankDetailBean.setRefId(pCompanyBankDetailBean.getRefId());
		lCompanyBankDetailBean = getDAO(false).findBean(lConnection, lCompanyBankDetailBean);
		if (lCompanyBankDetailBean != null) {
			lNew = false;
			pCompanyBankDetailBean.setId(lCompanyBankDetailBean.getId());
		}
		save(pExecutionContext, pCompanyBankDetailBean, pUserBean, lNew);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
	}
}
