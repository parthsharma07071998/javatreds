package com.xlx.treds.user.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.LoginBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bo.RegisterBO;

import groovy.json.JsonBuilder;

@Path("/")
@Singleton
public class RegisterResource {
    public static final String CONFIG_REGUSER = "com/xlx/treds/user/bean/RegAppUserBean.json";
    private RegisterBO registerBO;
	private BeanMeta appUserMetaBeanMeta;
	private CompanyDetailBO companyDetailBO;
	
    public RegisterResource() {
        super();
        registerBO = new RegisterBO();
        companyDetailBO = new CompanyDetailBO();
        appUserMetaBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class, CONFIG_REGUSER);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/reguser")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/reguser.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/adminreglist")
    public void pageAdmin(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/adminreglist.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/reghome")
    public void pageHome(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
    		,@QueryParam ("domain") String pDomain, @QueryParam ("login") String pLogin) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/reghome.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/reglogin")
    public void pageLogin(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("domain", AppConstants.DOMAIN_REGUSER);
        pRequest.getRequestDispatcher("/WEB-INF/login.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reguser")
    public String insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        Map<String, Object> lMap = save(pExecutionContext, pRequest, pMessage, true);
        return new JsonBuilder(lMap).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reguser/{loginid}")
    public void checkLogin(@Context ExecutionContext pExecutionContext, @PathParam("loginid") String pLoginId) throws Exception {
        AppUserBean lAppUserBean = new AppUserBean();
        lAppUserBean.setDomain(AppConstants.DOMAIN_REGUSER);
        lAppUserBean.setLoginId(pLoginId);
        registerBO.checkUserExists(pExecutionContext, lAppUserBean);
    }

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registrationsetting/{id}")
    public String registrationSetting(@Context ExecutionContext pExecutionContext,@Context HttpServletRequest pRequest,@Context HttpServletResponse pResponse, @PathParam("id") String pId) throws Exception {
    	AppUserBean lLoggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if (pId==null) {
        	//exception
        }
    	return companyDetailBO.getSettings(pExecutionContext, lLoggedInUserBean,pId);
    }
    
    private Map<String, Object> save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        AppUserBean lAppUserBean = new AppUserBean();
        AppUserBean lLoggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(lLoggedInUserBean==null){
        	throw new CommonBusinessException("Only TReDS Admin can create the new entity through the registration form.");
        }
        List<ValidationFailBean> lValidationFailBeans = appUserMetaBeanMeta.validateAndParse(lAppUserBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        HttpSession lSession = pRequest.getSession();
        String lCaptcha = lSession==null?null:(String)lSession.getAttribute(AuthenticationHandler.ATTRIB_CAPTCHA);
        if (StringUtils.isEmpty(lCaptcha))
            throw new CommonBusinessException("Captcha not available");
        if (!lCaptcha.equalsIgnoreCase(lAppUserBean.getDomain()))// domain is always in upper case. Temporary fix
            throw new CommonBusinessException("Mismatch in Captcha characters.");
        lSession.invalidate();
        lAppUserBean = registerBO.save(pExecutionContext, lAppUserBean, lLoggedInUserBean, pNew);
        
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, Object> lReturnMap = null;
        //if the loggedin entity is TReds Admin then it should redirect to reghome using the entityId created.
        if(lUserBean!= null && ( AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) ||
        	AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())) ){
        	lReturnMap = new HashMap<String, Object>();
        	lReturnMap.put("entityId", lAppUserBean.getId()); //reghome?entityId=0000
        }else if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())) {
            // auto login
            LoginBean lLoginBean = new LoginBean();
            lLoginBean.setDomain(AppConstants.DOMAIN_REGUSER);
            lLoginBean.setLogin(lAppUserBean.getLoginId());
            lLoginBean.setEncPassword(lAppUserBean.getPassword1());
            lReturnMap = AuthenticationHandler.getInstance().loginAndReturnMap(pExecutionContext, pRequest, lLoginBean, null);
        }
        pExecutionContext.commitAndDispose();
        return lReturnMap;
        
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/saveregistrationsetting")
    public String saveRegistrationSetting(@Context ExecutionContext pExecutionContext,@Context HttpServletRequest pRequest,@Context HttpServletResponse pResponse, String pMessage) throws Exception {
    	AppUserBean lLoggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	return companyDetailBO.getSaveSettings(pExecutionContext, lLoggedInUserBean,pMessage);
    }

}