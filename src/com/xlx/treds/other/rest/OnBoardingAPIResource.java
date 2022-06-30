package com.xlx.treds.other.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyKYCDocumentBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.entity.bo.CompanyBankDetailBO;
import com.xlx.treds.entity.bo.CompanyContactBO;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.entity.bo.CompanyKYCDocumentBO;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.entity.bo.CompanyShareEntityBO;
import com.xlx.treds.entity.bo.CompanyShareIndividualBO;
import com.xlx.treds.other.bean.OnBoardLogger;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

@Path("/v1/onboard")
public class OnBoardingAPIResource {
	
	private BeanMeta companyDetailBeanMeta;
	private BeanMeta companyContactBeanMeta;
	private BeanMeta companyLocationBeanMeta;
	private BeanMeta companyBankDetailBeanMeta;
	private BeanMeta companyDocumentBeanMeta;
	private BeanMeta companyShareEntityBeanMeta;
	private BeanMeta companyShareIndividualBeanMeta;
	private CompanyDetailBO companyDetailBO;
	public OnBoardingAPIResource() {
		super();
		companyDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyDetailBean.class);
		companyContactBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyContactBean.class);
		companyLocationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyLocationBean.class);
		companyBankDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyBankDetailBean.class);
		companyDetailBO = new CompanyDetailBO();
		companyDocumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyKYCDocumentBean.class);
		companyShareIndividualBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyShareIndividualBean.class);
		companyShareEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyShareEntityBean.class);
	}
	
	@POST
    @Secured (secKey="create-company")
    @Path("/createcompany")
	public String createCompany(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
    		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    		if (!AccessControlHelper.getInstance().hasAccess("create-company", lUserBean)){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
    		JsonSlurper lJsonSlurper = new JsonSlurper();
    		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
                sanitize(lMap);
    		CompanyDetailBean lFilterBean = new CompanyDetailBean();
    		companyDetailBeanMeta.validateAndParse(lFilterBean, lMap, null);
    		lMap.put("creatorIdentity", "J");
    		CompanyDetailBean lCompanyDetailBean = companyDetailBO.createEntity(pExecutionContext,lMap , lUserBean);
    		lResponse = companyDetailBeanMeta.formatAsJson(lCompanyDetailBean, lCompanyDetailBean.FIELDGROUP_OUTGOINGREQUESTONBOARDING, null, false, false);
		}catch(Exception e) {
			lResponse = e.getMessage();
			throw e;
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
	
	@POST
	@Secured (secKey="update-company")
	@Path("/updatecompany")
	public String updateCompany(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			if (!AccessControlHelper.getInstance().hasAccess("update-company", lUserBean)){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
			if(!lMap.containsKey("currency") || StringUtils.isBlank(lMap.get("currency").toString())){
				lMap.put("currency", AppConstants.CURRENCY_INR);
			}
			if(lMap != null){
				updateCountry("corCountry", lMap);
				String[] lListName = new String[] {"bankDetails","shareIndividuals","shareEntities","locations","contacts","contacts"};
				String[] lFieldInList = new String[] {"country","country","country","country","resCountry","nriCountry"};
				for(int lPtr=0; lPtr < lListName.length; lPtr++){
					if (lMap.containsKey(lListName[lPtr])) {
						List<Map<String,Object>> lList =  (List<Map<String, Object>>) lMap.get(lListName[lPtr]);
						for(Map<String, Object> lInnerMap : lList){
							updateCountry(lFieldInList[lPtr], lInnerMap);
						}
					}
				}
			}
			String lSubSegment = (String) lMap.get("subSegment");
			if (lSubSegment==null) {
				throw new CommonBusinessException("Subsegment is mandatory.");
			}
			if(lSubSegment.contains((String) lMap.get("industry"))){
				String[] lSegment = CommonUtilities.splitString(lSubSegment, ".");
				if(lSegment.length > 1){
				lMap.put("subSegment", lSegment[1]);
				}
			}
			try {
                sanitize(lMap);
			}catch(Exception lEx) {
				throw new CommonBusinessException("Please check the data payload. Improper format.");
			}
			CompanyDetailBean lFilterBean = new CompanyDetailBean();
			companyDetailBeanMeta.validateAndParse(lFilterBean, lMap, null);
			lResponse = companyDetailBO.updateCompany(pExecutionContext,lMap , lUserBean);
    	}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
	
	
	@POST
	@Secured (secKey="update-contact")
	@Path("/updatecontact")
	public String updateContact(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
			if(lMap!= null){
				updateCountry("resCountry", lMap);
				updateCountry("nriCountry", lMap);
			}
	                sanitize(lMap);
			CompanyContactBean lFilterBean = new CompanyContactBean();
			companyContactBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not Found.");
			}
			CompanyContactBO lCompanyContactBO = new CompanyContactBO();
			lResponse = lCompanyContactBO.saveCompanyContact(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId());
		}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
		}
	
	@POST
	@Secured (secKey="update-location")
	@Path("/updatelocation")
	public String updateLocation(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
    		updateCountry("country", lMap);
	                sanitize(lMap);
			CompanyLocationBean lFilterBean = new CompanyLocationBean();
			companyLocationBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not Found.");
			}
			CompanyLocationBO lCompanyLocationBO = new CompanyLocationBO();
			lResponse = lCompanyLocationBO.saveCompanyLocation(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId());
		}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}

	@POST
	@Secured (secKey="update-bank")
	@Path("/updatebankdetail")
	public String updateBank(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
    		updateCountry("country", lMap);
	                sanitize(lMap);
			CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
			companyBankDetailBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not Found.");
			}
			CompanyBankDetailBO lCompanyBankDetailBO = new CompanyBankDetailBO();
			lResponse = lCompanyBankDetailBO.saveCompanyBank(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId());
    	}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
	
	@POST
	@Secured (secKey="update-document")
	@Path("/updatedocument")
	public String updateDocument(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
	                sanitize(lMap);
			CompanyKYCDocumentBean lFilterBean = new CompanyKYCDocumentBean();
			companyDocumentBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not found.");
			}
			Long lContactRefid = null;
			if(lMap.containsKey("contactRefId") && lMap.get("contactRefId")!=null) {
				lContactRefid = Long.valueOf(lMap.get("contactRefId").toString());
			}
			CompanyKYCDocumentBO lCompanyKYCDocumentBO = new CompanyKYCDocumentBO();
			lResponse = lCompanyKYCDocumentBO.saveCompanyDocument(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId(),lContactRefid);
    	}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
	
	@POST
	@Secured (secKey="update-entity")
	@Path("/updateshareentity")
	public String updateIndividual(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
    		updateCountry("country", lMap);
	                sanitize(lMap);
			CompanyShareEntityBean lFilterBean = new CompanyShareEntityBean();
			companyShareEntityBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not Found.");
			}
			CompanyShareEntityBO lCompanyShareEntityBO = new CompanyShareEntityBO();
			lResponse = lCompanyShareEntityBO.saveCompanyShareEntity(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId());
    	}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
	
	@POST 
	@Secured (secKey="update-individual")
	@Path("/updateshareindividual")
	public String updateEntity(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		int lRequestId = OnBoardLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
		String lResponse = null;
    	try {
			AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
    		updateCountry("country", lMap);
	                sanitize(lMap);
			CompanyShareIndividualBean lFilterBean = new CompanyShareIndividualBean();
			companyShareIndividualBeanMeta.validateAndParse(lFilterBean, lMap, null);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCompanyCode());
			if ( lAppEntityBean == null ) {
				throw new CommonBusinessException("Company not Found.");
			}
			CompanyShareIndividualBO lCompanyShareIndividualBO = new CompanyShareIndividualBO();
			lResponse = lCompanyShareIndividualBO.saveCompanyShareIndividual(pExecutionContext, lUserBean, lFilterBean, lAppEntityBean.getCdId());
    	}catch(Exception e) {
			lResponse = e.getMessage();
			throw new CommonBusinessException(e.getMessage());
		}finally {
			OnBoardLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
		return lResponse;
	}
    
    private void sanitize( Map<String,Object> pMap){
        for(String lKey : pMap.keySet()){
                Object lValue = pMap.get(lKey);
                if (lValue instanceof List){
                        for(Object lValue2 : ((List)lValue)){
                                sanitize((Map<String,Object>) lValue2);
                        }
                }else if( lValue == null ||StringUtils.isEmpty(lValue.toString()) ){
                        pMap.put(lKey, null);
                }
        }
    }
    public void updateCountry(String pFieldCode, Map<String,Object> pMap){
    	if(CommonUtilities.hasValue(pFieldCode) && pMap != null){
    		if(pMap.containsKey(pFieldCode)){
    			Object lValue = pMap.get(pFieldCode);
    			if(lValue instanceof String && "INDIA".equals(lValue)){
    				pMap.put(pFieldCode, "India");
    			}
    		}
    	}
    }
}
