package com.xlx.treds.entity.rest;

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
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bo.PurchaserAggregatorBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/puraggregator")
public class PurchaserAggregatorResource {

    private PurchaserAggregatorBO purchaserAggregatorBO;
    private BeanMeta appEntityBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public PurchaserAggregatorResource() {
        super();
        purchaserAggregatorBO = new PurchaserAggregatorBO();
        appEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","cdId","name","type","status"});
        lovFields = Arrays.asList(new String[]{"name","code"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null)) {
            Object[] lKey = new Object[]{pCode};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/puraggregator.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="puragg-view")
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        AppEntityBean lAppEntityBean = purchaserAggregatorBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured (secKey="puragg-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AppEntityBean lFilterBean = new AppEntityBean();
        appEntityBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AppEntityBean> lAppEntityList = purchaserAggregatorBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AppEntityBean lAppEntityBean : lAppEntityList) {
            lResults.add(appEntityBeanMeta.formatAsArray(lAppEntityBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured
    @Path("/all")
    public String listCsv(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AppEntityBean lFilterBean = new AppEntityBean();
        appEntityBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AppEntityBean> lAppEntityList = purchaserAggregatorBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

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
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lFilterBean = new AppEntityBean();
        List<AppEntityBean> lAppEntityList = purchaserAggregatorBO.findList(pExecutionContext, lFilterBean, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (AppEntityBean lAppEntityBean : lAppEntityList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCdId());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="puragg-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="puragg-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, pNew ? AppEntityBean.FIELDGROUP_INSERTPURCHASERAGGERGATOR: AppEntityBean.FIELDGROUP_UPDATEPURCHASERAGGERGATOR, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)){
            //throw new CommonValidationException(lValidationFailBeans);
        }
        purchaserAggregatorBO.save(pExecutionContext, lAppEntityBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="puragg-save")
    @Path("/{code}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(pCode);
        purchaserAggregatorBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}