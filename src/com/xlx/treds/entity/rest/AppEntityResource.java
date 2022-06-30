package com.xlx.treds.entity.rest;

import java.sql.Connection;
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

import com.xlx.common.base.CommonConstants;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.ClickWrapHelper;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.AppEntityBean.TwoFAType;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyWrapperBean;
import com.xlx.treds.entity.bo.AppEntityBO;
import com.xlx.treds.other.bean.CustomFieldBean;
import com.xlx.treds.other.bo.CustomFieldBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.entity.bo.CompanyDetailBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/appentity")
public class AppEntityResource {
	public static final String TABLENAME_PROV = "CompanyDetails_P";
    private AppEntityBO appEntityBO;
    private BeanMeta appEntityBeanMeta;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<CustomFieldBean> customFieldDAO;
	private GenericDAO<CompanyDetailBean>  companyDetailDAO;
	
	
    public AppEntityResource() {
        super();
        appEntityBO = new AppEntityBO();
        appEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","name","type","status"});
        lovFields = Arrays.asList(new String[]{"code"});
        customFieldDAO = new GenericDAO<CustomFieldBean>(CustomFieldBean.class);
        companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class, TABLENAME_PROV);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode, @QueryParam("type") String pType) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null)) {
            Object[] lKey = new Object[]{pCode};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        if(pType!=null){
        	pRequest.setAttribute("type", pType);
        }
        pRequest.getRequestDispatcher((pCode != null)?"/WEB-INF/appentityips.jsp":"/WEB-INF/appentity.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-view")
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AppEntityBean lFilterBean = new AppEntityBean();
        boolean lIsFinancier = false;
        String lFinEntity = null;
        //
        appEntityBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AppEntityBean> lAppEntityList = appEntityBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AppEntityBean lAppEntityBean : lAppEntityList) {
            lResults.add(appEntityBeanMeta.formatAsArray(lAppEntityBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, null, lResults, false);
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/buyfin")
    public String lovBuyFin(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        appendBuyFin(pExecutionContext, pRequest, null, lResults, false);
        return new JsonBuilder(lResults).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/purchasers")
    public String purchasers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Purchaser, lResults, true);
        return new JsonBuilder(lResults).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/suppliers")
    public String suppliers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Supplier, lResults, true);
        return new JsonBuilder(lResults).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/financiers")
    public String financiers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Financier, lResults, true);
        return new JsonBuilder(lResults).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/pursup")
    public String purchaserAndSuppliers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Purchaser, lResults, false);
        append(pExecutionContext, pRequest, EntityType.Supplier, lResults, false);
        return new JsonBuilder(lResults).toString();
    }

    private void append(ExecutionContext pExecutionContext, HttpServletRequest pRequest, EntityType pEntityType,
            List<Map<String, Object>> pResults, boolean pIncludeSelf) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<AppEntityBean> lAppEntityList = appEntityBO.findList(pExecutionContext, pEntityType, lUserBean, pIncludeSelf);
        for (AppEntityBean lAppEntityBean : lAppEntityList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lAppEntityBean.getName());
            pResults.add(lData);
        }
    }
    
    private void appendBuyFin(ExecutionContext pExecutionContext, HttpServletRequest pRequest, EntityType pEntityType,
            List<Map<String, Object>> pResults, boolean pIncludeSelf) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<AppEntityBean> lAppEntityList = appEntityBO.findList(pExecutionContext, pEntityType, lUserBean, pIncludeSelf);
        for (AppEntityBean lAppEntityBean : lAppEntityList) {
        	if(!lAppEntityBean.isSupplier()){
	            Map<String, Object> lData = new HashMap<String, Object>();
	            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
	            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCode());
	            lData.put(BeanFieldMeta.JSONKEY_DESC, lAppEntityBean.getName());
	            pResults.add(lData);
        	}
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/ips/{code}")
    public String getIps(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setCode(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())?pCode:lUserBean.getDomain());
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATEIPLIST,null,false);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/ips")
    public void updateIps(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, AppEntityBean.FIELDGROUP_UPDATEIPLIST, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.updateIps(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.save(pExecutionContext, lAppEntityBean, lUserBean, pNew);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-delete")
    @Path("/{code}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        appEntityBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/salesCategory")
    public String salesCategory(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        //fetch RefCode for - SECTOR and MSMESTATUS, concatinate with hifen
        ArrayList<RefCodeValuesBean> lSectors = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_SECTOR);
        ArrayList<RefCodeValuesBean> lMSMEStatus = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_MSMESTATUS);
        for(RefCodeValuesBean lSector : lSectors){
            for(RefCodeValuesBean lStatus : lMSMEStatus){
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lSector.getDesc() + " - " + lStatus.getDesc());
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lSector.getValue() + CommonConstants.KEY_SEPARATOR + lStatus.getValue());
                //lData.put(BeanFieldMeta.JSONKEY_DESC, lSector.getDesc() + " d " + lStatus.getDesc() );
                lResults.add(lData);
            }        	
        }
        return new JsonBuilder(lResults).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/2FA/{code}")
    public String get2FA(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATE2FA,null,false);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/2FA")
    public void update2FA(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, AppEntityBean.FIELDGROUP_UPDATE2FA, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.update2FA(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/reqVer/{code}")
    public String getReqVer(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        if(!(lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()) ){
        	throw new CommonBusinessException(pCode+" is not a Purchaser/Supplier.");
        }
        if (ClickWrapHelper.getInstance().isAgreementEnabled(lAppEntityBean.getCode())){
        	 return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATEREQ_VER,null,false);
        }else{
        	throw new CommonBusinessException(" Agreement Not enabled for "+(lAppEntityBean.isPurchaser()?"Buyer":"Seller" ));
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/reqVer")
    public void updateReqVer(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, AppEntityBean.FIELDGROUP_UPDATEREQ_VER, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if (ClickWrapHelper.getInstance().isAgreementEnabled(lAppEntityBean.getCode())){
            appEntityBO.updateReqVer(pExecutionContext, lAppEntityBean, lUserBean);
        }else{
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
    }
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/clickwrapVersionsPurchaser")
    public String getClickwrapVersionsPurchaser(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	ArrayList<Map<String,String>> lVersions = ClickWrapHelper.getInstance().getClickWrapAggrementVersions(AppEntityBean.EntityType.Purchaser);
    	if(RegistryHelper.getInstance().getBoolean(ClickWrapHelper.REGISTRY_PURCHASER_AGREEMENT_ENABLED)){
        	return new JsonBuilder(lVersions).toString();
        }else{
        	return null;
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/clickwrapVersionsSupplier")
    public String getClickwrapVersionsSupplier(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	ArrayList<Map<String,String>> lVersions = ClickWrapHelper.getInstance().getClickWrapAggrementVersions(AppEntityBean.EntityType.Supplier);
        if(RegistryHelper.getInstance().getBoolean(ClickWrapHelper.REGISTRY_SUPPLIER_AGREEMENT_ENABLED)){
        	return new JsonBuilder(lVersions).toString();
        }else{
        	return null;
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/2FAType")
    public String get2FAType(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	ArrayList<Map<String,String>> lVersions = new ArrayList<Map<String,String>>();
    	Map<String,String> lVersionObj = new HashMap<String,String>();
		lVersionObj.put(BeanFieldMeta.JSONKEY_TEXT, TwoFAType.OneTimePassword.getdesc());
		lVersionObj.put(BeanFieldMeta.JSONKEY_VALUE, TwoFAType.OneTimePassword.getCode());
		lVersions.add(lVersionObj);
		lVersionObj = new HashMap<String,String>();
		lVersionObj.put(BeanFieldMeta.JSONKEY_TEXT, TwoFAType.QuestionAndAnswer.getdesc());
		lVersionObj.put(BeanFieldMeta.JSONKEY_VALUE, TwoFAType.QuestionAndAnswer.getCode());
		lVersions.add(lVersionObj);
		lVersionObj = new HashMap<String,String>();
		lVersionObj.put(BeanFieldMeta.JSONKEY_TEXT, TwoFAType.Token.getdesc());
		lVersionObj.put(BeanFieldMeta.JSONKEY_VALUE, TwoFAType.Token.getCode());
		lVersions.add(lVersionObj);
        return new JsonBuilder(lVersions).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/splitsetting/{code}")
    public String getSplitSettings(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATESPLITSETTINGS,null,false);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/splitsetting")
    public void updateSplitSettings(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, AppEntityBean.FIELDGROUP_UPDATESPLITSETTINGS, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.updateSplitSettings(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/checkerlimitsetting")
    public void updateCheckerLimitSettings(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<String> lFields = Arrays.asList(new String[]{"code","instLevel","instCntrLevel","bidLevel","limitLevel"});
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParseUsingList(lAppEntityBean, pMessage, lFields, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.updateCheckerLimitSettings(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/chksetting/{code}")
    public String getBeanForChkSetting(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rm/{code}")
    public String getRm(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        if(lAppEntityBean!=null) {
        	if(StringUtils.isNotEmpty(lAppEntityBean.getRefererCode())) {
        		AppEntityBean lRefererAEBean = TredsHelper.getInstance().getAppEntityBean(lAppEntityBean.getRefererCode());
        		if(lRefererAEBean!=null) {
        			lAppEntityBean.setAggCompanyGSTN(lRefererAEBean.getAggCompanyGSTN());
        			lAppEntityBean.setAggContactPerson(lRefererAEBean.getAggContactPerson());
        			lAppEntityBean.setAggContactMobile(lRefererAEBean.getAggContactMobile());
        			lAppEntityBean.setAggContactEmail(lRefererAEBean.getAggContactEmail());
        		}
        	}
        }
        return appEntityBeanMeta.formatAsJson(lAppEntityBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/aggregator")
    public String aggregators(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Aggregator, lResults, true);
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Secured
    @Path("/creditreport")
    public void saveCreditReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pMessage) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
    	AppEntityBean lAppEntityBean = new AppEntityBean();
    	appEntityBeanMeta.validateAndParse(lAppEntityBean, lMap, null, null);
    	appEntityBO.saveCreditReport(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/rmloc")
    public String getRmLocations(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	ArrayList<RefCodeValuesBean> lList = TredsHelper.getInstance().getRefCodeValues("RMLOCATION");
    	List<Map<String, Object>> lResults = new ArrayList<>();
    	for (RefCodeValuesBean lRCVBean : lList) {
    		Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lRCVBean.getValue());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lRCVBean.getValue());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lRCVBean.getDesc());
            lResults.add(lData);
    	}
		return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/rmsave")
    public void updateRmSettings(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        appEntityBeanMeta.validateAndParse(lAppEntityBean, pMessage, null);
        appEntityBO.updateRmSettings(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/rmrefdetail/{code}")
    public String getReferalDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_REFERERDETAILS,null,false);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/rmloc/{auId}")
    public String getRmLocations(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("auId") Long pAuId) throws Exception {
    	Map<String, Object> lResults = new HashMap<String,Object>();
    	AppUserBean lAppUserBean = TredsHelper.getInstance().getAppUser(pAuId);
    	if(lAppUserBean!=null && lAppUserBean.getRmLocation()!=null) {
    		lResults.put("location", lAppUserBean.getRmLocation());
    	}
		return new JsonBuilder(lResults).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/instConf/{code}")
    public String getInstConf(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("code") String pCode, @QueryParam("cfId") Long pCfId) throws Exception {
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pCode);
    	if (lAppEntityBean.getCfId()!=null) {
    		CustomFieldBean lFilterBean = new CustomFieldBean();
        	lFilterBean.setCode(pCode);
        	lFilterBean.setId(lAppEntityBean.getCfId());
        	if (pCfId!=null) {
        		lFilterBean.setId(pCfId);
        	}
        	CustomFieldBean lBean = customFieldDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
        	return new JsonBuilder(lBean.getConfig()).toString();
    	}else {
    		return null;	
    	}
    }
   
   
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="appentity-delete")
    @Path("/removecompany/{code}")
    public void removeCompany(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBean lCdBean = new CompanyDetailBean();
        lCdBean.setCode(pCode);
        lCdBean.setIsProvisional(true);
        CompanyDetailBean lCompanyDetailBean = companyDetailDAO.findBean(pExecutionContext.getConnection(), lCdBean);
        if(lCompanyDetailBean!=null) {
            	Connection lConnection = pExecutionContext.getConnection();
            	CompanyWrapperBean lCompanyWrapperProvBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), true, lUserBean.getId());
            	CompanyWrapperBean lCompanyWrapperBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), false, lUserBean.getId());
            	if(!lCompanyWrapperProvBean.isJocataData()) {
                	lCompanyWrapperProvBean.deleteAllData();
                	lCompanyWrapperBean.deleteAllData();
            	}
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/gstnpan/{code}")
    public String getGstnPan(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        Map<String, Object> lResults = new HashMap<String,Object>();
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        lResults.put("pan", "");
        lResults.put("gstn", "");
        if(lAppEntityBean!=null) {
            lResults.put("pan", lAppEntityBean.getPan());
        }
        CompanyLocationBean lCLBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pExecutionContext.getConnection(), pCode);
        if(lCLBean!=null) {
            lResults.put("gstn", lCLBean.getGstn());
        }
        return new JsonBuilder(lResults).toString();
   }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/isInt/{code}")
    public String getIntegration(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        Map<String, Object> lResults = new HashMap<String,Object>();
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        lResults.put("isInt", CommonAppConstants.YesNo.No.getCode());
        if(lAppEntityBean != null){
        	if(lAppEntityBean.getPreferences() != null){
        		if(lAppEntityBean.getPreferences().getIsInt() != null){
        			if(CommonAppConstants.Yes.Yes.equals(lAppEntityBean.getPreferences().getIsInt())){
        				lResults.put("isInt", CommonAppConstants.YesNo.Yes.getCode());
        			}
        		}
        	}
        }
        return new JsonBuilder(lResults).toString();
   }

}