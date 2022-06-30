package com.xlx.treds.user.bo;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class RegisterBO {
    
    private GenericDAO<AppUserBean> appUserDAO;
    
    public RegisterBO() {
        super();
        appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
    }
    
    public AppUserBean findBean(ExecutionContext pExecutionContext, 
            AppUserBean pFilterBean) throws Exception {
        return appUserDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
    }
    
    public AppUserBean save(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, AppUserBean pLoggedInAppUserBean, boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        if(pLoggedInAppUserBean == null){
        //	throw new CommonBusinessException("Only TReDS Admin can create the new entity through the registration form.");
        } 
        
        if (pNew) {
        	createRegUser(pExecutionContext, pAppUserBean, pLoggedInAppUserBean);
        	createCompanyDetails(pExecutionContext, pAppUserBean, pLoggedInAppUserBean, pNew);
        }
        pExecutionContext.commit();
        return pAppUserBean;
    }
    
    public AppUserBean createRegUser(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, AppUserBean pLoggedInAppUserBean) throws Exception {
        pAppUserBean.setDomain(AppConstants.DOMAIN_REGUSER);
        checkUserExists(pExecutionContext, pAppUserBean);
        pAppUserBean.setEnable2FA(CommonAppConstants.YesNo.No);
        pAppUserBean.setStatus(IAppUserBean.Status.Active);
        pAppUserBean.setType(AppUserBean.Type.RegisteringUser);
        pAppUserBean.setRecordCreator(pLoggedInAppUserBean.getId());
        if(!CommonUtilities.hasValue(pAppUserBean.getPassword1())){
        	//for the new user set the default password and set the first question with the password as the answer
            String lPassword = RegistryHelper.getInstance().getString(AppConstants.REGISTRY_DEFAULTPASSWORD);
            if (StringUtils.isBlank(lPassword)) lPassword = "Treds@123";
            pAppUserBean.setPassword1(CommonUtilities.encryptSHA(lPassword));
            //TODO: How to softcore the security settings ????
            Map<String,List<String>> lSeqMap = new HashMap<String,List<String>>();
            lSeqMap.put("q", Arrays.asList(new String[]{"SQ1"}));
            lSeqMap.put("a", Arrays.asList(new String[]{lPassword}));
            //
            pAppUserBean.setSecuritySettings(new JsonBuilder(lSeqMap).toString());
        }else{
            pAppUserBean.setPassword1(CommonUtilities.encryptSHA(pAppUserBean.getPassword1()));
        }
        pAppUserBean.setPasswordUpdatedAt1(new Timestamp(System.currentTimeMillis()));
        pAppUserBean.setOtherSettings(pAppUserBean.getOtherSettings());
        AuthenticationHandler.getInstance().getUserDataSource().addUserBean(pExecutionContext, pAppUserBean);
        return pAppUserBean;
    }
    
    public CompanyDetailBean createCompanyDetails(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, AppUserBean pLoggedInAppUserBean, boolean pNew) throws Exception {
    	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
    	CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
    	//
    	if(pAppUserBean.getId()!=null) {
    		//NORMAL
    		lCompanyDetailBean.setId(pAppUserBean.getId());
    	}else {
    		//API BASED CREATION
    		//check whether already exists based on PAN
    		lCompanyDetailBean.setPan(pAppUserBean.getPan());
    		lCompanyDetailBean = lCompanyDetailBO.findBean(pExecutionContext, lCompanyDetailBean, pLoggedInAppUserBean, false);
    		//
    		if(lCompanyDetailBean!=null && lCompanyDetailBean.getId()!=null) {
    			throw new CommonBusinessException("Company already created for PAN : " + pAppUserBean.getPan()+".");
    		}
        	lCompanyDetailBean.setId( DBHelper.getInstance().getUniqueNumber(pExecutionContext.getConnection(), AppUserBean.ENTITY_NAME) );
    	}
    	
    	lCompanyDetailBean.setCompanyName(pAppUserBean.getCompanyName());
    	lCompanyDetailBean.setType(pAppUserBean.getEntityType());
    	lCompanyDetailBean.setPan(pAppUserBean.getPan());
    	lCompanyDetailBean.setConstitution(pAppUserBean.getConstitution());
    	lCompanyDetailBean.setCurrency(AppConstants.CURRENCY_INR);

        lCompanyDetailBean.setRecordCreator(pLoggedInAppUserBean.getId());
        lCompanyDetailBean.setApprovalStatus(CompanyApprovalStatus.Draft);
        
        lCompanyDetailBO.save(pExecutionContext, lCompanyDetailBean, pAppUserBean, pLoggedInAppUserBean, pNew);
        return lCompanyDetailBean;
    }
    
    public void checkUserExists(ExecutionContext pExecutionContext, AppUserBean pAppUserBean) throws Exception {
        IAppUserBean lAppUserBean = AuthenticationHandler.getInstance().getUserDataSource().getUserBean(pExecutionContext, 
                pAppUserBean.getDomain(), pAppUserBean.getLoginId());
        if (lAppUserBean != null)
            throw new CommonBusinessException("Login Id already in use");
    }
}
