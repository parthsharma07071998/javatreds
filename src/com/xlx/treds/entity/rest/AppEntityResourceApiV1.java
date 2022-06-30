package com.xlx.treds.entity.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.AppEntityBO;
import com.xlx.treds.entity.bo.CompanyLocationBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/appentity")
public class AppEntityResourceApiV1 {

	private static Logger logger = Logger.getLogger(AppEntityResourceApiV1.class);
    private AppEntityBO appEntityBO;
    private BeanMeta appEntityBeanMeta;
	private List<String> defaultListFields, lovFields;
    private CompanyLocationBO companyLocationBO;
    private BeanMeta companyLocationBeanMeta;
	
    public AppEntityResourceApiV1() {
        super();
        appEntityBO = new AppEntityBO();
        appEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","name","type","status"});
        lovFields = Arrays.asList(new String[]{"code"});
        //
        companyLocationBO = new CompanyLocationBO();
        companyLocationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyLocationBean.class);
    }
    
/*    @GET
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
*/    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/suppliers")
    public String suppliers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, null, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        append(pExecutionContext, pRequest, EntityType.Supplier, lResults, true);
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/suppliergstn")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	if(StringUtils.isEmpty(pFilter)){
    		pFilter = "{}";
    	}
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	boolean lDesciption = true;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        List<String> lCoLocListFields = Arrays.asList(new String[]{"id","cdid","name","gstn","companyCode"});
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = lCoLocListFields;
        companyLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        
        List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findLocationwiseGSTNList(pExecutionContext, lFilterBean, lFields, lUserBean, AppEntityBean.EntityType.Supplier);

        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();

        if(lCompanyLocationList!=null){
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
                lResults.add(companyLocationBeanMeta.formatAsMap(lCompanyLocationBean, null, lCoLocListFields, lDesciption, false));
            }
        }
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    }
	    return lResponse;
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/suppliergstnpan")
    public String listSupGstnPan(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	if(StringUtils.isEmpty(pFilter)){
    		pFilter = "{}";
    	}
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	boolean lDesciption = true;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        List<String> lCoLocListFields = Arrays.asList(new String[]{"gstn","companyCode"});
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = lCoLocListFields;
        companyLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        
        List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findLocationwiseGSTNList(pExecutionContext, lFilterBean, lFields, lUserBean, AppEntityBean.EntityType.Supplier);

        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();

        Map<String,Map<String, Object>> lCowiseMap = new HashMap<String, Map<String,Object>>();
        if(lCompanyLocationList!=null){
        	Map<String,Object> lTmpMap = null;
        	AppEntityBean lAEBean = null;
        	List<String> lGstns = null;
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
            	if(lCowiseMap.containsKey(lCompanyLocationBean.getCompanyCode())) {
                	lTmpMap = lCowiseMap.get(lCompanyLocationBean.getCompanyCode());
            	}else {
            		lTmpMap = new HashMap<String,Object>();
            		lCowiseMap.put(lCompanyLocationBean.getCompanyCode(), lTmpMap);
                	lTmpMap.put("companyCode",lCompanyLocationBean.getCompanyCode());
                	lTmpMap.put("pan", TredsHelper.getInstance().getCompanyPAN(lTmpMap.get("companyCode").toString()));
                	lTmpMap.put("companyName", TredsHelper.getInstance().getAppEntityBean(lCompanyLocationBean.getCompanyCode()).getName());
                	lTmpMap.put("gstn", new ArrayList<String>());
            	}
            	lGstns = (List<String> )lTmpMap.get("gstn");
            	if (!lGstns.contains(lCompanyLocationBean.getGstn())) {
            		lGstns.add(lCompanyLocationBean.getGstn());
            	}
            }
            for(String lTmp : lCowiseMap.keySet()) {
                lResults.add((Map<String, Object>) lCowiseMap.get(lTmp));
            }
        }
        
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    }
	    return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/purchasergstnpan")
    public String listPurGstnPan(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	if(StringUtils.isEmpty(pFilter)){
    		pFilter = "{}";
    	}
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	boolean lDesciption = true;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        List<String> lCoLocListFields = Arrays.asList(new String[]{"gstn","companyCode"});
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = lCoLocListFields;
        companyLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        
        List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findLocationwiseGSTNList(pExecutionContext, lFilterBean, lFields, lUserBean, AppEntityBean.EntityType.Supplier);

        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        
        Map<String,Map<String, Object>> lCowiseMap = new HashMap<String, Map<String,Object>>();
        if(lCompanyLocationList!=null){
        	Map<String,Object> lTmpMap = null;
        	AppEntityBean lAEBean = null;
        	List<String> lGstns = null;
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
            	if(lCowiseMap.containsKey(lCompanyLocationBean.getCompanyCode())) {
                	lTmpMap = lCowiseMap.get(lCompanyLocationBean.getCompanyCode());
            	}else {
            		lTmpMap = new HashMap<String,Object>();
            		lCowiseMap.put(lCompanyLocationBean.getCompanyCode(), lTmpMap);
                	lTmpMap.put("companyCode",lCompanyLocationBean.getCompanyCode());
                	lTmpMap.put("pan", TredsHelper.getInstance().getCompanyPAN(lTmpMap.get("companyCode").toString()));
                	lTmpMap.put("companyName", TredsHelper.getInstance().getAppEntityBean(lCompanyLocationBean.getCompanyCode()).getName());
                	lTmpMap.put("gstn", new ArrayList<String>());
            	}
            	lGstns = (List<String> )lTmpMap.get("gstn");
            	if (!lGstns.contains(lCompanyLocationBean.getGstn())) {
            		lGstns.add(lCompanyLocationBean.getGstn());
            	}
            }
            for(String lTmp : lCowiseMap.keySet()) {
                lResults.add((Map<String, Object>) lCowiseMap.get(lTmp));
            }
        }
        
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    }
	    return lResponse;
    }
/*    
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
*/
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
/*    
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
        if(!lAppEntityBean.isPurchaser()){
        	throw new CommonBusinessException(pCode+" is not a Purchaser.");
        }
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATEREQ_VER,null,false);
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
        appEntityBO.updateReqVer(pExecutionContext, lAppEntityBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/clickwrapVersions")
    public String getClickwrapVersions(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	ArrayList<Map<String,String>> lVersions = TredsHelper.getInstance().getClickWrapAggrementVersions();
        return new JsonBuilder(lVersions).toString();
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
*/    

}