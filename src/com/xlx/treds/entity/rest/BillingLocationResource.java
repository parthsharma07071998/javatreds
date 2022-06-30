package com.xlx.treds.entity.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.BillingLocationBean;
import com.xlx.treds.entity.bo.BillingLocationBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/billinglocation")
public class BillingLocationResource {

    private BillingLocationBO billingLocationBO;
    private BeanMeta billingLocationBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public BillingLocationResource() {
        super();
        billingLocationBO = new BillingLocationBO();
        billingLocationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BillingLocationBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","id","billLocId"});
        lovFields = Arrays.asList(new String[]{"code","id"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode, @QueryParam("id") Long pId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null) && (pId != null)) {
            Object[] lKey = new Object[]{pCode, pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/billinglocation.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/{code}/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("id") Long pId) throws Exception {
        BillingLocationBean lFilterBean = new BillingLocationBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setId(pId);
        BillingLocationBean lBillingLocationBean = billingLocationBO.findBean(pExecutionContext.getConnection(), lFilterBean);
        return billingLocationBeanMeta.formatAsJson(lBillingLocationBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/checkaccess")
    public void checkAccess(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BillingLocationBean lFilterBean = new BillingLocationBean();
        billingLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCode());
        if (lAEBean.getPreferences().getElb()==null || !CommonAppConstants.Yes.Yes.equals(lAEBean.getPreferences().getElb())) {
        	throw new CommonBusinessException("Access Denied.");
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BillingLocationBean lFilterBean = new BillingLocationBean();
        billingLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCode());
        if (lAEBean.getPreferences().getElb()==null || !CommonAppConstants.Yes.Yes.equals(lAEBean.getPreferences().getElb())) {
        	throw new CommonBusinessException("Access Denied.");
        }
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<BillingLocationBean> lBillingLocationList = billingLocationBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (BillingLocationBean lBillingLocationBean : lBillingLocationList) {
            lResults.add(billingLocationBeanMeta.formatAsArray(lBillingLocationBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BillingLocationBean lBillingLocationBean = new BillingLocationBean();
        List<ValidationFailBean> lValidationFailBeans = billingLocationBeanMeta.validateAndParse(lBillingLocationBean, 
            pMessage, null, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        billingLocationBO.save(pExecutionContext, lBillingLocationBean, lUserBean);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/{code}/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BillingLocationBean lFilterBean = new BillingLocationBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setId(pId);
        billingLocationBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}