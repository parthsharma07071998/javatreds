package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLimitUtilizationBean;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/purcsupplmtutil")
public class PurchaserSupplierLimitUtilizationResource {

    private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
    private BeanMeta purchaserSupplierLimitUtilizationBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public PurchaserSupplierLimitUtilizationResource() {
        super();
        purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
        purchaserSupplierLimitUtilizationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLimitUtilizationBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","supplier","purchaser","limit","limitUtilized","status"});
        lovFields = Arrays.asList(new String[]{"id","supplier","purchaser"});
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
        pRequest.getRequestDispatcher("/WEB-INF/purcsupplmtutil.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLimitUtilizationBean lFilterBean = new PurchaserSupplierLimitUtilizationBean();
        lFilterBean.setId(pId);
        lFilterBean.setPurchaser(lUserBean.getDomain());
        PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean = purchaserSupplierLimitUtilizationBO.findBean(pExecutionContext, lFilterBean, lUserBean);
        return purchaserSupplierLimitUtilizationBeanMeta.formatAsJson(lPurchaserSupplierLimitUtilizationBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PurchaserSupplierLimitUtilizationBean lFilterBean = new PurchaserSupplierLimitUtilizationBean();
        purchaserSupplierLimitUtilizationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PurchaserSupplierLimitUtilizationBean> lPurchaserSupplierLimitUtilizationList = purchaserSupplierLimitUtilizationBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean : lPurchaserSupplierLimitUtilizationList) {
            lResults.add(purchaserSupplierLimitUtilizationBeanMeta.formatAsArray(lPurchaserSupplierLimitUtilizationBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-view")
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<PurchaserSupplierLimitUtilizationBean> lPurchaserSupplierLimitUtilizationList = purchaserSupplierLimitUtilizationBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean : lPurchaserSupplierLimitUtilizationList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lPurchaserSupplierLimitUtilizationBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lPurchaserSupplierLimitUtilizationBean.getSupplier());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean = new PurchaserSupplierLimitUtilizationBean();
        List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLimitUtilizationBeanMeta.validateAndParse(lPurchaserSupplierLimitUtilizationBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        purchaserSupplierLimitUtilizationBO.save(pExecutionContext, lPurchaserSupplierLimitUtilizationBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="purcsupplmtutil-save")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLimitUtilizationBean lFilterBean = new PurchaserSupplierLimitUtilizationBean();
        lFilterBean.setId(pId);
        lFilterBean.setPurchaser(lUserBean.getDomain());
        purchaserSupplierLimitUtilizationBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}