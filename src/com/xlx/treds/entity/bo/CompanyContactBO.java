package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyContactBean.Nationality;
import com.xlx.treds.entity.bean.CompanyContactBean.ResidentailStatus;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyContactBO {
    public static final String TABLENAME_PROV = "CompanyContacts_P";
    private GenericDAO<CompanyContactBean> companyContactDAO;
    private GenericDAO<CompanyContactBean> companyContactProvDAO;

    public CompanyContactBO() {
        super();
        companyContactDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        companyContactDAO= new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        companyContactProvDAO= new GenericDAO<CompanyContactBean>(CompanyContactBean.class, TABLENAME_PROV);
    }

    private GenericDAO<CompanyContactBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyContactProvDAO;
    	}
    	return companyContactDAO;
    }
    
    public CompanyContactBean findBean(ExecutionContext pExecutionContext, 
        CompanyContactBean pFilterBean) throws Exception {
        CompanyContactBean lCompanyContactBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCompanyContactBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lCompanyContactBean.getCdId(), pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyContactBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        if(pFilterBean.getIsProvisional()) {
        	CompanyContactBean lActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lActualBean!=null) {
            	lActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                //
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lActualBean,lCompanyContactBean);
                lCompanyContactBean.setModifiedData(lDiffData);
                return lCompanyContactBean;
            }
        }
        return lCompanyContactBean;
    }
    
    public List<CompanyContactBean> findList(ExecutionContext pExecutionContext, CompanyContactBean pFilterBean, 
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
    
    public void save(ExecutionContext pExecutionContext, CompanyContactBean pCompanyContactBean, IAppUserBean pUserBean, 
        boolean pNew,Map<String,Object> pDataMap) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyContactBean.setCdId(pUserBean.getId());
        }else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyContactBean.getCdId()==null || pCompanyContactBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if(TredsHelper.getInstance().isRegistrationApproved(lConnection, pCompanyContactBean.getCdId())){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
        	if( !TredsHelper.getInstance().hasAccessOnCompany(lConnection, pCompanyContactBean.getCdId(), (AppUserBean) pUserBean))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }

        CompanyContactBean lOldCompanyContactBean = null;
        
        if(!CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getPromoter()) 
        		&& !CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getAuthPer()) 
        		&& !CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getAdmin()) 
        		&& !CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getUltimateBeneficiary()) )
        {
            throw new CommonBusinessException("Select at least one role.");
        }
        if( CommonAppConstants.Yes.Yes.equals(((AppUserBean)pUserBean).getEnableAPI()) ){
        	if(pCompanyContactBean.getGender() == null && StringUtils.isNotBlank(pCompanyContactBean.getSalutation())){
        		pCompanyContactBean.setGender(TredsHelper.getInstance().getGender(pCompanyContactBean.getSalutation()));
        	}
        }
        if (pCompanyContactBean.getPromoter() ==  CommonAppConstants.Yes.Yes && 
        	 pCompanyContactBean.getChiefPromoter() == CommonAppConstants.Yes.Yes) {
            if (pCompanyContactBean.getCpCat() == null)
                throw new CommonBusinessException("Category manadatory for chief promoter");
        }
        /* Comment jocata (Mandar) make date non mandatory
        if (pCompanyContactBean.getAuthPer() == CommonAppConstants.Yes.Yes) {
            if (pCompanyContactBean.getAuthPerAuthDate() == null)
                throw new CommonBusinessException("Authorisation date mandatory for authorised users");
        }
        if (pCompanyContactBean.getAdmin() == CommonAppConstants.Yes.Yes) {
            if (pCompanyContactBean.getAdminAuthDate() == null)
                throw new CommonBusinessException("Admin authorisation date mandatory for admin users");
        }
        */
        if (pCompanyContactBean.getCersaiFlag() == CommonAppConstants.Yes.Yes) {
            if (pCompanyContactBean.getCersaiSalutation() == null)
                throw new CommonBusinessException("Father/Husband Salutation mandatory for CERSAI registered persons");
            if (pCompanyContactBean.getCersaiFirstName() == null)
                throw new CommonBusinessException("Father/Husband First Name mandatory for CERSAI registered persons");
        }
        if (pCompanyContactBean.getPan()==null &&  pCompanyContactBean.getUltimateBeneficiary()==null ){
        	if (!Nationality.Others.equals(pCompanyContactBean.getNationality() )) {
        		throw new CommonBusinessException("Please enter your PAN details.");
        	}
        }
        
        if (pCompanyContactBean.getDesignation()==null && !CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getUltimateBeneficiary())){
        	throw new CommonBusinessException("Please select a designation.");
        }
        
        if (pCompanyContactBean.getGender()==null && !CommonAppConstants.Yes.Yes.equals(pCompanyContactBean.getUltimateBeneficiary())){
        	throw new CommonBusinessException("Please select a gender.");
        }
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyContactBean.setIsProvisional(true);//THIS IS DONE IN RESOURCE -SINCE DATA COMING FROM JOCATA DIRECTLY SAVES TO MAIN TABLE
        GenericDAO<CompanyContactBean> lCompanyContactDAO = getDAO(pCompanyContactBean.getIsProvisional()); //this is true ie from provisional

        if (pNew) {
        	pCompanyContactBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyContactDAO.getTableName()+".id"));
            pCompanyContactBean.setRecordCreator(pUserBean.getId());
            lCompanyContactDAO.insert(lConnection, pCompanyContactBean);
            //only for jocata
            if(!pCompanyContactBean.getIsProvisional()) {
                lCompanyContactDAO.insertAudit(lConnection, pCompanyContactBean, AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyContactBean = findBean(pExecutionContext, pCompanyContactBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyContactBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyContactBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            pCompanyContactBean.setRecordUpdator(pUserBean.getId());
            if(pCompanyContactBean.getRecordVersion() == null){
            	pCompanyContactBean.setRecordVersion(lOldCompanyContactBean.getRecordVersion());
            }
            lCompanyContactDAO.getBeanMeta().copyBean(pCompanyContactBean, lOldCompanyContactBean,BeanMeta.FIELDGROUP_UPDATE,null);
            if (lCompanyContactDAO.update(lConnection, lOldCompanyContactBean, BeanMeta.FIELDGROUP_UPDATE) == 0){
            	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }
            //only for jocata
            if(!pCompanyContactBean.getIsProvisional()) {
            	lCompanyContactDAO.insertAudit(lConnection, lOldCompanyContactBean, AuditAction.Update, pUserBean.getId());
            }
        }
        //logic not needed for api user (Jocata)
        if( !CommonAppConstants.Yes.Yes.equals(((AppUserBean)pUserBean).getEnableAPI()) ) {
	        // if admin then remove other admin if any
	        if (pCompanyContactBean.getAdmin() != null) {
	        	StringBuilder lDuplicateSql = new StringBuilder();
	        	lDuplicateSql.append("SELECT * FROM ");
	        	if(pCompanyContactBean.getIsProvisional()) {
	        		lDuplicateSql.append("CompanyContacts_P");
	        	}else {
	        		lDuplicateSql.append("CompanyContacts");
	        	}
	        	lDuplicateSql.append(" WHERE CCRecordVersion > 0");
	        	lDuplicateSql.append(" AND CCCdId = ").append(pCompanyContactBean.getCdId());
	        	lDuplicateSql.append(" AND CCAdmin = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()));
	        	if (pCompanyContactBean.getId() != null)
	        		lDuplicateSql.append(" AND CCId != ").append(pCompanyContactBean.getId());
	            List<CompanyContactBean> lContacts = lCompanyContactDAO.findListFromSql(pExecutionContext.getConnection(), 
	            		lDuplicateSql.toString(), 0);
	            if (lContacts.size() > 0) {
	            	if (pCompanyContactBean.getForce() == null) {
	            		// TODO warning
	            		throw new CommonBusinessException("WARN:" + lContacts.get(0).getFirstName() + " " + lContacts.get(0).getLastName()); 
	            	} else {
	                    for (CompanyContactBean lCompanyContactBean : lContacts) {
	                        if (!lCompanyContactBean.getId().equals(pCompanyContactBean.getId())) {
		                            lCompanyContactBean.setAdmin(null);
		                            lCompanyContactBean.setAdminAuthDate(null);
		                            lCompanyContactBean.setRecordUpdator(pUserBean.getId());
		                            lCompanyContactDAO.update(pExecutionContext.getConnection(), lCompanyContactBean, BeanMeta.FIELDGROUP_UPDATE);
	                            	//lCompanyContactDAO.insertAudit(lConnection, lOldCompanyContactBean, AuditAction.Update, pUserBean.getId());
	                            }
	                        }
	                    }
	            	}
	            }
	        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyContactBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        // check if registration details are editable
        CompanyContactBean lCompanyContactBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);
        if (lCompanyContactBean==null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
    	if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())) {
    		CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
    		lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
    	}
		if((AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
				AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))  && 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyContactBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        pExecutionContext.setAutoCommit(false);
        lCompanyContactBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyContactBean);
    	//getDAO(pFilterBean.getIsProvisional()).insertAudit(lConnection, lCompanyContactBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }
    
    public void validateNationaltyDetails(CompanyContactBean pCompanyContactBean,boolean pNew) throws Exception{
    	if (pCompanyContactBean.getNationality()!=null){
	    	if (Nationality.Indian.equals(pCompanyContactBean.getNationality()) ){
	    		if (pCompanyContactBean.getNationality()!=null){
	    			if (ResidentailStatus.Residential_Indian.equals(pCompanyContactBean.getResidentailStatus())){
	            		checkIndianAddress(pCompanyContactBean);
	    			}else if (ResidentailStatus.Non_Residential_Indian.equals(pCompanyContactBean.getResidentailStatus())){
	    				checkIndianAddress(pCompanyContactBean);
	    				checkInternationalAddress(pCompanyContactBean);
	            	}
	    		}else{
	    			throw new CommonBusinessException("Please select residential status.");
	    		}
	        }
	        if (Nationality.Others.equals(pCompanyContactBean.getNationality()) ){
	        	checkIndianAddress(pCompanyContactBean);
	        }
    	}else{
        	throw new CommonBusinessException("Please select nationality.");
    	}
    }
    
    public void checkIndianAddress(CompanyContactBean pCompanyContactBean) throws Exception{
    	if (pCompanyContactBean.getResLine1()==null){
    		throw new CommonBusinessException("Residential address Line 1 mandatory.");
    	}
    	if (pCompanyContactBean.getResCity()==null){
    		throw new CommonBusinessException("Please select Residential address City.");
    	}
    	if (pCompanyContactBean.getResCountry()==null){
    		throw new CommonBusinessException("Please select Residential address Country.");
    	}
    	if (pCompanyContactBean.getResDistrict()==null){
    		throw new CommonBusinessException("Please select Residential address District.");
    	}
    	if (pCompanyContactBean.getResZipCode()==null){
    		throw new CommonBusinessException("Please select Residential address Zip Code.");
    	}
    	if (pCompanyContactBean.getResState()==null){
    		throw new CommonBusinessException("Please select Residential address State.");
    	}
    	
    }
    
    public void checkInternationalAddress(CompanyContactBean pCompanyContactBean) throws Exception{
    	if (pCompanyContactBean.getNriLine1()==null){
    		throw new CommonBusinessException("Permanenat / Overseas address Line 1 mandatory.");
    	}
    	if (pCompanyContactBean.getNriCountry()==null){
    		throw new CommonBusinessException("Please select Permanenat / Overseas address Country.");
    	}
    	if (pCompanyContactBean.getNriZipCode()==null){
    		throw new CommonBusinessException("Please select Permanenat / Overseas address Zip Code.");
    	}
    }
	
	public String saveCompanyContact(ExecutionContext pExecutionContext,AppUserBean pUserBean,CompanyContactBean pCompanyContactBean, Long pCdId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyContactBean.setCdId(pCdId);
		}
		if(pCompanyContactBean.getRefId()!=null) {
			CompanyContactBean lCompanyContactBean = new CompanyContactBean();
			lCompanyContactBean.setRefId(pCompanyContactBean.getRefId());
			lCompanyContactBean = getDAO(false).findBean(lConnection, lCompanyContactBean);
			if (lCompanyContactBean != null) {
				lNew = false;
				pCompanyContactBean.setId(lCompanyContactBean.getId());
			}
		}else {
			throw new CommonBusinessException("refId missing.");
		}
		save(pExecutionContext, pCompanyContactBean, pUserBean, lNew, null);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
	}
}
