package com.xlx.treds.entity.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.entity.bo.AppEntityBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/entpref")
public class AppEntityPreferenceResource {

    private AppEntityBO appEntityBO;
    private BeanMeta appEntityMeta;
	private BeanMeta appEntityPreferenceMeta;
	
    public AppEntityPreferenceResource() {
        super();
        appEntityBO = new AppEntityBO();
        appEntityMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        appEntityPreferenceMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityPreferenceBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
 //   @Path("/{code}")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("code") String pEntityCode) throws Exception {	
        Object[] lKey = new Object[]{pEntityCode};
        String lModify = new JsonBuilder(lKey).toString();
        pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        pRequest.getRequestDispatcher("/WEB-INF/entpref.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Secured
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("code") String pEntityCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pEntityCode);
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        if (lAppEntityBean.getPreferences() == null)
        	lAppEntityBean.setPreferences(new AppEntityPreferenceBean());
        
        String lFieldGroup = null;
    	if (lAppEntityBean.isPurchaser())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_BUYERFIELDS;
    	else if (lAppEntityBean.isSupplier())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_SELLERFIELDS;
    	else if (lAppEntityBean.isFinancier())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_FINANCIERFIELDS;

    	Map<String, Object> lMap = appEntityPreferenceMeta.formatAsMap(lAppEntityBean.getPreferences());
    	if (lFieldGroup != null)
    		lMap.put("fieldGroup", lFieldGroup);
        return new JsonBuilder(lMap).toString();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        String lEntityCode = (String)lMap.get("code");
        appEntityBO.saveEntityPreferences(pExecutionContext, lEntityCode, lMap, lUserBean);
    }
}