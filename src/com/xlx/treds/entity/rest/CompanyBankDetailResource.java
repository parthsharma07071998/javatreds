package com.xlx.treds.entity.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bo.CompanyBankDetailBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companybankdetail")
@Singleton
public class CompanyBankDetailResource {

    private CompanyBankDetailBO companyBankDetailBO;
    private BeanMeta companyBankDetailBeanMeta;
	private List<String> defaultListFields;
	private String defaultListDBColumns;
	
    public CompanyBankDetailResource() {
        super();
        companyBankDetailBO = new CompanyBankDetailBO();
        companyBankDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyBankDetailBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","cdId","bank","line1","line2","line3","country","district","state","city","zipCode","accType","accNo","ifsc","salutation","firstName","middleName","lastName","email","mobile","defaultAccount","creatorIdentity","isProvisional"});
        defaultListDBColumns = "CBDId,CBDCdId,CBDBank,CBDLine1,CBDLine2,CBDLine3,CBDCountry,CBDState,CBDDistrict,CBDCity,CBDZipCode,CBDAccType,CBDAccNo,CBDIfsc,CBDSalutation,CBDFirstName,CBDMiddleName,CBDLastName,CBDEmail,CBDMobile,CBDDefaultAccount";
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/companybankdetail.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        lFilterBean.setId(pId);
        lFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(lUserBean));
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyBankDetailBean lCompanyBankDetailBean = companyBankDetailBO.findBean(pExecutionContext, lFilterBean);
        if(lCompanyBankDetailBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyBankDetailBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyBankDetailBeanMeta.formatAsJson(lCompanyBankDetailBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if(lMap!=null && lMap.size() >0){
        	Object lTemp = lMap.get("cdId");
        	if(lTemp!=null)lFilterBean.setCdId(new Long(lTemp.toString()));
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lFilterBean.setIsProvisional((boolean)lTemp);
        }
        Map<String,Object> lRtnMap = new HashMap<>();
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lFilterBean.getCdId(),lFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
    		lRtnMap.put("creatorIdentity",lCDBean.getCreatorIdentity());
    	}
        List<CompanyBankDetailBean> lCompanyBankDetailList = companyBankDetailBO.findList(pExecutionContext, lFilterBean, defaultListDBColumns, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CompanyBankDetailBean lCompanyBankDetailBean : lCompanyBankDetailList) {
        	if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        		lCompanyBankDetailBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        	}
        	lCompanyBankDetailBean.setIsProvisional(lFilterBean.getIsProvisional());
            lResults.add(companyBankDetailBeanMeta.formatAsMap(lCompanyBankDetailBean, null, defaultListFields, true, true));
        }
        lRtnMap.put("list",lResults);
        return new JsonBuilder(lRtnMap).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyBankDetailBean lCompanyBankDetailBean = new CompanyBankDetailBean();
        List<ValidationFailBean> lValidationFailBeans = companyBankDetailBeanMeta.validateAndParse(lCompanyBankDetailBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if(pNew) {
        	TredsHelper.getInstance().removeUnwantedValidation(lValidationFailBeans, "id");
        }
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
            throw new CommonValidationException(lValidationFailBeans);
        }
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) || 
        		AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
            if(lMap!=null && lMap.size() >0){
            	Object lTemp = lMap.get("cdId");
            	if(lTemp!=null)lCompanyBankDetailBean.setCdId(new Long(lTemp.toString()));
            }
        }
        pExecutionContext.setAutoCommit(false);
        //TODO: THIS SHOULD COME FROM FRONTEND
        lCompanyBankDetailBean.setIsProvisional(true);
        companyBankDetailBO.save(pExecutionContext, lCompanyBankDetailBean, lUserBean, pNew);
        pExecutionContext.commit();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        lFilterBean.setId(pId);
        //this will always be deletion from provisional
    	lFilterBean.setIsProvisional(true);
        companyBankDetailBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/entitybanks/{entity}")
    public String lovEntityBanks(@Context ExecutionContext pExecutionContext, 
            @Context HttpServletRequest pRequest, @PathParam("entity") String pEntityCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        String lEntityCode = null;
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
        	lEntityCode = pEntityCode;
        }else {
        	if(lUserBean.getDomain().equals(pEntityCode)){
            	lEntityCode = pEntityCode;
        	}
        }
        if(!CommonUtilities.hasValue(lEntityCode)){
    		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        TredsHelper lTredsHelper = TredsHelper.getInstance();
        AppEntityBean lAppEntityBean =  lTredsHelper.getAppEntityBean(lEntityCode);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        lFilterBean.setCdId(lAppEntityBean.getCdId());
        //TODO: receive extra parameter isProvisional from frontend
        lFilterBean.setIsProvisional(false);
        List<CompanyBankDetailBean> lCompanyBankDetailBeans = companyBankDetailBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Map<String, Object> lData;
        String lDisplayText = "";
        if(lCompanyBankDetailBeans!=null){
        for (CompanyBankDetailBean lCompanyBankDetailBean : lCompanyBankDetailBeans) {
            lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyBankDetailBean.getId());
            
            
            lDisplayText = CommonAppConstants.Yes.Yes.equals(lCompanyBankDetailBean.getDefaultAccount())?"* ":"";
            lDisplayText += TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCompanyBankDetailBean.getBank());
            lDisplayText += " [ " + lCompanyBankDetailBean.getAccNo() +" ] ";
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lDisplayText);
            lResults.add(lData);
        }
        }
        return new JsonBuilder(lResults).toString();
    }
        
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/lov")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, @QueryParam("cdId") Long pCdId, @QueryParam("isProv") boolean pIsProvisional) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Long lCdId = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, 
                new String[]{lUserBean.getDomain()});
        if(lAppEntityBean.isPlatform() ||
        		(lAppEntityBean.isRegistringEntity() && 
        				TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), pCdId, (AppUserBean) lUserBean))){
        	lCdId  = pCdId;
        }else{
        	lCdId=TredsHelper.getInstance().getCompanyId(lUserBean);
        }
        //
        if(lCdId != null && lCdId.longValue() > 0 ){
            lFilterBean.setCdId(lCdId);
            //TODO: receive extra parameter isProvisional from frontend
            lFilterBean.setIsProvisional(pIsProvisional);
            List<CompanyBankDetailBean> lCompanyBankDetailBeans = companyBankDetailBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
            Map<String, Object> lData;
            String lDisplayText = "";
            if(lCompanyBankDetailBeans!=null){
            for (CompanyBankDetailBean lCompanyBankDetailBean : lCompanyBankDetailBeans) {
                lData = new HashMap<String, Object>();
				//default bank to be included in the bank list - just mark star for default
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyBankDetailBean.getId());
                
                lDisplayText = CommonAppConstants.Yes.Yes.equals(lCompanyBankDetailBean.getDefaultAccount())?"* ":"";
                lDisplayText += TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCompanyBankDetailBean.getBank());
                lDisplayText += " [ " + lCompanyBankDetailBean.getAccNo() +" ] ";
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lDisplayText);
                lResults.add(lData);
            }
            }
        }
        return new JsonBuilder(lResults).toString();

    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/settlenach/{entity}")
    public String nachBanks(@Context ExecutionContext pExecutionContext, 
            @Context HttpServletRequest pRequest, @PathParam("entity") String pEntityCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        String lEntityCode = null;
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
        	lEntityCode = pEntityCode;
        }else {
        	if(lUserBean.getDomain().equals(pEntityCode)){
            	lEntityCode = pEntityCode;
        	}
        }
        if(!CommonUtilities.hasValue(lEntityCode)){
    		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        TredsHelper lTredsHelper = TredsHelper.getInstance();
        AppEntityBean lAppEntityBean =  lTredsHelper.getAppEntityBean(lEntityCode);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        lFilterBean.setCdId(lAppEntityBean.getCdId());
        //TODO: receive extra parameter isProvisional from frontend
        lFilterBean.setIsProvisional(false);
        List<CompanyBankDetailBean> lCompanyBankDetailBeans = companyBankDetailBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Map<String, Object> lData;
        String lDisplayText = "";
        if(lCompanyBankDetailBeans!=null){
	        for (CompanyBankDetailBean lCompanyBankDetailBean : lCompanyBankDetailBeans) {
	            lData = new HashMap<String, Object>();
	            lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyBankDetailBean.getId());
	
	            lDisplayText = CommonAppConstants.Yes.Yes.equals(lCompanyBankDetailBean.getDefaultAccount())?"* ":"";
	            lDisplayText += TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCompanyBankDetailBean.getBank());
	            lDisplayText += " [ " + lCompanyBankDetailBean.getAccNo() +" ] ";
	            lData.put(BeanFieldMeta.JSONKEY_TEXT, lDisplayText);
	            lResults.add(lData);
	        }
        }
        return new JsonBuilder(lResults).toString();
    }

}