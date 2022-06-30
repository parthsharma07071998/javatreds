package com.xlx.treds.user.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
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
import com.xlx.commonn.user.bean.IAppUserBean.Status;
import com.xlx.commonn.user.bean.RoleMasterBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/user")
public class AppUserResource {

    private AppUserBO appUserBO;
    private BeanMeta appUserBeanMeta;
	private List<String> defaultListFields;
	private List<String> reportFieldlist;
	private List<String> tredsReportFieldlist;
    private GenericDAO<AppUserBean> appUserDAO;
	
    public AppUserResource() {
        super();
        appUserBO = new AppUserBO();
        appUserBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","domain","loginId","password1","passwordUpdatedAt1","forcePasswordChange","status","reason","failedLoginCount","type","salutation","firstName","middleName","lastName","telephone","mobile","email","enable2FA"});
        reportFieldlist=Arrays.asList(new String[]{"email","recordUpdateTime","firstName","middleName","lastName","rmListDesc"});
        tredsReportFieldlist=Arrays.asList(new String[]{"domain","loginId","type","email","altEmail", "telephone", "mobile","firstName","middleName","lastName"});
        appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId, @QueryParam("view") Boolean pView,
        @QueryParam("lgn") Boolean pLogin) throws Exception {
        if ((pView != null) && (pView.booleanValue()))
            pRequest.getRequestDispatcher("/WEB-INF/userprofile.jsp").forward(pRequest, pResponse);
        else if ((pLogin != null) && pLogin.booleanValue())
            pRequest.getRequestDispatcher("/WEB-INF/loginpop.jsp").forward(pRequest, pResponse);
        else {
            if (pNew != null)
                pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
            else if ((pId != null)) {
                Object[] lKey = new Object[]{pId};
                String lModify = new JsonBuilder(lKey).toString();
                pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
            }
            pRequest.getRequestDispatcher("/WEB-INF/user.jsp").forward(pRequest, pResponse);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="user-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        AppUserBean lLoggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppUserBean lFilterBean = new AppUserBean();
        lFilterBean.setId(pId);
        AppUserBean lAppUserBean = appUserBO.findBean(pExecutionContext, lFilterBean, lLoggedInUserBean);
        return appUserBeanMeta.formatAsJson(lAppUserBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/profile")
    public String getProfile(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lLoggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppUserBean lFilterBean = new AppUserBean();
        lFilterBean.setId(lLoggedInUserBean.getId());
        return appUserBO.getJsonForProfile(pExecutionContext, lFilterBean, lLoggedInUserBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="user-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);

        AppUserBean lFilterBean = new AppUserBean();
        appUserBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AppUserBean> lAppUserList = appUserBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AppUserBean lAppUserBean : lAppUserList) {
        		lResults.add(appUserBeanMeta.formatAsArray(lAppUserBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey="user-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);

        AppUserBean lFilterBean = new AppUserBean();
        appUserBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;

    	final StringBuilder lData = new StringBuilder();
    	List<BeanFieldMeta> lFieldList =  appUserBeanMeta.getFieldListFromNames(lFields);

    	//for displaying header
    	int lRmListDescPosition = -1, lCounter=0;
    	if(lFieldList!=null){
    		for(BeanFieldMeta lBeanFieldMeta : lFieldList){
    			if(lData.length() > 0) lData.append(CommonConstants.COMMA);
    			lData.append(lBeanFieldMeta.getLabel());
    			if(lBeanFieldMeta.getName().equals("rmListDesc")) lRmListDescPosition = lCounter;
    			lCounter++;
    		}
            lData.append("\n");
    	}
        List<AppUserBean> lAppUserList = appUserBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        for (AppUserBean lAppUserBean : lAppUserList) {
        	final Object[] lRow = appUserBeanMeta.formatAsArray(lAppUserBean, null, lFields, true);
            if(lRow != null)
            {
            	for(int lPtr=0; lPtr < lRow.length; lPtr++){
            		if(lPtr>0) lData.append(CommonConstants.COMMA);
            		if(lRow[lPtr]!=null&&lRow[lPtr]!="null"){
                		if(lPtr==lRmListDescPosition)
                			lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[lPtr].toString().replaceAll(CommonConstants.COMMA, CommonConstants.COLUMN_SEPARATOR)));
                		else
                			lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[lPtr]));
            		}
            	}
                lData.append("\n");
            }
        }
        
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"users.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    
    @POST
    @Secured(secKey="user-view")
    @Path("/reportDownload")
    public Object reportListDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AppUserBean lFilterBean = new AppUserBean();
        appUserBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
    	final StringBuilder lData = new StringBuilder();
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
            AppEntityBean lAppEntityBean = null;
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
            List<BeanFieldMeta> lBeanFieldMetas = lBeanMeta.getFieldListFromNames(tredsReportFieldlist);
    	//for displaying header
        	lData.append("EntityType");
        	lData.append(CommonConstants.COMMA);
        	lData.append("EntityName");
        	for(BeanFieldMeta lBeanFieldMeta : lBeanFieldMetas) {
            	lData.append(CommonConstants.COMMA);
            	lData.append(lBeanFieldMeta.getLabel());
        	}
            lData.append("\n");
            List<AppUserBean> lAppUserList = appUserDAO.findList(pExecutionContext.getConnection(), lFilterBean, tredsReportFieldlist);
            for (AppUserBean lAppUserBean : lAppUserList) {
            	if(lAppEntityBean==null || !lAppEntityBean.getCode().equals(lAppUserBean.getDomain())) {
            		lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lAppUserBean.getDomain());
            	}
            	lData.append(TredsHelper.getInstance().getSanatisedObject(lAppEntityBean.getTypeDesc()));
            	lData.append(CommonConstants.COMMA);
            	lData.append(TredsHelper.getInstance().getSanatisedObject(lAppEntityBean.getName()));
            	lData.append(CommonConstants.COMMA);
            	lData.append(lBeanMeta.formatBeanAsDelimited(lAppUserBean, null, tredsReportFieldlist, true, ","));
                lData.append("\n");
            }        	
        }else {
	    	lData.append("UserEmailID");
	    	lData.append(CommonConstants.COMMA);
			lData.append("LastLoginDate");
			lData.append(CommonConstants.COMMA);
			lData.append("RoleName");
			lData.append(CommonConstants.COMMA);
			lData.append("UserName");
			lData.append("\n");
            List<AppUserBean> lAppUserList = appUserBO.findListWithLastLoginInfo(pExecutionContext, lFilterBean, reportFieldlist, lUserBean);
	        for (AppUserBean lAppUserBean : lAppUserList) {
	            	final Object[] lRow = appUserBeanMeta.formatAsArray(lAppUserBean, null, reportFieldlist, true);
	            if(lRow != null)
	            {
	            	String[] lRoles = new String[]{""};
	            	if(CommonUtilities.hasValue(lRow[5].toString())){
	            		lRoles = lRow[5].toString().split(CommonConstants.COMMA);
	            	}
	            	for(int lPtr=0; lPtr < lRoles.length; lPtr++){
	                	lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[0]));
	                	lData.append(CommonConstants.COMMA);
	                	if(lPtr==0)
	                		lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[1]));
	                	lData.append(CommonConstants.COMMA);
	                	lData.append(TredsHelper.getInstance().getSanatisedObject(lRoles[lPtr]));
	                	lData.append(CommonConstants.COMMA);
	                	lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[2]));
	                	if(lRow[3]!=null&&lRow[3]!="null")lData.append("."+TredsHelper.getInstance().getSanatisedObject(lRow[3]));
	                	if(lRow[4]!=null&&lRow[4]!="null")lData.append("."+TredsHelper.getInstance().getSanatisedObject(lRow[4]));
	                    lData.append("\n");
	            	}
	            }
            }        	
        }
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"RXILUserData.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="user-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="user-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppUserBean lAppUserBean = new AppUserBean();
        List<ValidationFailBean> lValidationFailBeans = appUserBeanMeta.validateAndParse(lAppUserBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : AppUserBean.FIELDGROUP_VALIDATEUPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appUserBO.save(pExecutionContext, lAppUserBean, lUserBean, pNew);
    }
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/checkers")
    @Secured
    public String checkers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @QueryParam("domain") String pDomain, @QueryParam("type") String pType) throws Exception {
    	if (AppConstants.DOMAIN_PLATFORM.equals(pDomain)){
    		return null;
    	}
        return lov(pExecutionContext, pRequest, pDomain,pType);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    @Secured
    public String users(@Context ExecutionContext pExecutionContext
    		, @Context HttpServletRequest pRequest,@QueryParam("adm") String pAdmin) throws Exception {
        String lAdmin = null;
    	if (pAdmin!=null && CommonAppConstants.Yes.Yes.getCode().equals(pAdmin)) {
    		lAdmin = AppConstants.DOMAIN_PLATFORM;
        }
    	return lov(pExecutionContext, pRequest, lAdmin==null?null:lAdmin,null);
    }
    
    public String lov(ExecutionContext pExecutionContext, HttpServletRequest pRequest, String pDomain,String pType) throws Exception {
    	  IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
          BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
          Map<String , List<String>> lFieldGroupMapForJson = null;
          List<String> lColumnList = null;
          lFieldGroupMapForJson = (Map<String , List<String>>)lBeanMeta.getJsonConfigMap().get(BeanMeta.KEY_FIELDGROUPS);
          lColumnList = lFieldGroupMapForJson.get("forlov");
          AppUserBean lFilterBean = new AppUserBean();
          lFilterBean.setDomain(pDomain);
          List<AppUserBean> lAppUserList = appUserBO.findList(pExecutionContext, lFilterBean, lColumnList, (AppUserBean)lUserBean);
          List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
          Long lLoggedInAUId  = lUserBean.getId();
          for (AppUserBean lAppUserBean : lAppUserList) {
        	  if (pType!=null && ( Status.Disabled.equals(lAppUserBean.getStatus()) 
        			  || Status.Suspended.equals(lAppUserBean.getStatus()) )) {
        		  continue;
        	  }
        	  if (StringUtils.isNotEmpty(pDomain)  && AppConstants.DOMAIN_PLATFORM.equals(pDomain) ) {
        		  if (CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI())) {
        			  continue;
        		  }
        	  }
              Map<String, Object> lData = new HashMap<String, Object>();
              lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppUserBean.getId());
              if (pType!=null) {
            	  String lLevel = appUserBO.getChecherLevel(pType, lAppUserBean);
            	  lData.put(BeanFieldMeta.JSONKEY_TEXT,(lLevel==null?lAppUserBean.getLoginId():lAppUserBean.getLoginId()+" "+lLevel));
              }else {
            	  lData.put(BeanFieldMeta.JSONKEY_TEXT,lAppUserBean.getLoginId());
              }
              //lData.put(BeanFieldMeta.JSONKEY_DESC, lAppUserBean.getLoginId());
        	  if (StringUtils.isNotEmpty(pDomain) && AppConstants.DOMAIN_PLATFORM.equals(pDomain) ) {
                  lData.put("location", lAppUserBean.getRmLocation());
        	  }
              lResults.add(lData);
          }
          return new JsonBuilder(lResults).toString();    
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles")
    @Secured
    public String roles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @QueryParam("domain") String pDomain) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
            pDomain = lUserBean.getDomain();
        List<RoleMasterBean> lRoleMasterBeans = appUserBO.getRoles(pExecutionContext, pDomain);
        RoleMasterBean lRoleMasterBean = null;
        boolean lPlatformUser = AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain());
        for (int lPtr=0; lPtr<lRoleMasterBeans.size(); lPtr++)
        {
            lRoleMasterBean = (RoleMasterBean)lRoleMasterBeans.get(lPtr);
			//for displaying only Public roles to users while private and public roles to platform users
            if(lPlatformUser || AppConstants.Owner.Public.getCode().equals(lRoleMasterBean.getOwner())){
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lRoleMasterBean.getId());
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lRoleMasterBean.getName());
                lResults.add(lData);
            }
        }
        return new JsonBuilder(lResults).toString();    
    }
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reset2FA/{id}")
    @Secured
    public void reset2FA(@Context ExecutionContext pExecutionContext, 
            @Context HttpServletRequest pRequest, @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        appUserBO.reset2FA(pExecutionContext, pId, lUserBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/toggleAPI/{id}")
    @Secured(secKey="user-save")
    public void toggleEnableApi(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        appUserBO.toggleEnableAPI(pExecutionContext, pId, lUserBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getentitylimit/{limit}")
    @Secured(secKey="user-view")
    public String getEntityLimit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @PathParam("limit") String pLimit,@QueryParam("domain") String pDomain) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pDomain!=null?pDomain:lUserBean.getDomain());
        Long lSize = new Long(0);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        if (pLimit.equals("instLevel")) {
        	lSize = lAppEntityBean.getInstLevel();
        }else if (pLimit.equals("bidLevel")) {
        	lSize = lAppEntityBean.getBidLevel();
        }else if (pLimit.equals("platformLimitLevel")) {
        	lSize = lAppEntityBean.getPlatformLimitLevel();
        }else if (pLimit.equals("instCntrLevel")) {
        	lSize = lAppEntityBean.getInstCntrLevel();
        }else if (pLimit.equals("userLimitLevel")) {
        	lSize = lAppEntityBean.getUserLimitLevel();
        }else if (pLimit.equals("buyerLimitLevel")) {
        	lSize = lAppEntityBean.getBuyerLimitLevel();
        }else if (pLimit.equals("buyerSellerLimitLevel")) {
        	lSize = lAppEntityBean.getBuyerSellerLimitLevel();
        }
        for (int lPtr=1; lPtr<=lSize.intValue(); lPtr++)
        {
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lPtr);
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lPtr);
                lResults.add(lData);
         }
		 return new JsonBuilder(lResults).toString();
	}
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getglobalcheckersetting")
    @Secured(secKey="user-save")
    public String getEntityLimit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            ,@QueryParam("domain") String pDomain) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pDomain!=null?pDomain:lUserBean.getDomain());
        if (!(lAppEntityBean.isFinancier() || lAppEntityBean.isSupplier() || lAppEntityBean.isPurchaser())) {
        	throw new CommonBusinessException("Please select a Buyer,Seller or Financier ");
        }
        HashMap<String,Object> lRtnMap = new HashMap<>();
        lRtnMap.put("entitySetting", TredsHelper.getInstance().getCheckerInfo(lAppEntityBean));
		return new JsonBuilder(lRtnMap).toString();
	}
}