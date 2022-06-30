package com.xlx.treds.other.rest;

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
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.other.bean.GEMInvoiceBean;
import com.xlx.treds.other.bean.GEMInvoiceBean.Status;
import com.xlx.treds.other.bean.GEMInvoiceResendBean;
import com.xlx.treds.other.bo.GEMInvoiceBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/geminvoices")
public class GEMInvoiceResource {

    private GEMInvoiceBO gEMInvoiceBO;
    private BeanMeta gEMInvoiceBeanMeta;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<GEMInvoiceResendBean> gemInvoiceResendDAO;
	private GenericDAO<GEMInvoiceBean> gemInvoiceDAO;
	
    public GEMInvoiceResource() {
        super();
        gEMInvoiceBO = new GEMInvoiceBO();
        gEMInvoiceBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(GEMInvoiceBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","arrId","type","supplier","supGstn","purchaser","purGstn","purPan","goodsAcceptDate","poDate","poNumber","instDate","instDueDate","amount","adjAmount","creditPeriod","status"});
        lovFields = Arrays.asList(new String[]{"id","arrId"});
        gemInvoiceResendDAO = new GenericDAO<GEMInvoiceResendBean>(GEMInvoiceResendBean.class);
        gemInvoiceDAO = new GenericDAO<GEMInvoiceBean>(GEMInvoiceBean.class);
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
        pRequest.getRequestDispatcher("/WEB-INF/geminvoices.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        GEMInvoiceBean lFilterBean = new GEMInvoiceBean();
        lFilterBean.setId(pId);
        GEMInvoiceBean lGEMInvoiceBean = gEMInvoiceBO.findBean(pExecutionContext, lFilterBean);
        return gEMInvoiceBeanMeta.formatAsJson(lGEMInvoiceBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        GEMInvoiceBean lFilterBean = new GEMInvoiceBean();
        gEMInvoiceBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<GEMInvoiceBean> lGEMInvoiceList = gEMInvoiceBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (GEMInvoiceBean lGEMInvoiceBean : lGEMInvoiceList) {
            lResults.add(gEMInvoiceBeanMeta.formatAsArray(lGEMInvoiceBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<GEMInvoiceBean> lGEMInvoiceList = gEMInvoiceBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (GEMInvoiceBean lGEMInvoiceBean : lGEMInvoiceList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lGEMInvoiceBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lGEMInvoiceBean.getArrId());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
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
        GEMInvoiceBean lGEMInvoiceBean = new GEMInvoiceBean();
        List<ValidationFailBean> lValidationFailBeans = gEMInvoiceBeanMeta.validateAndParse(lGEMInvoiceBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        gEMInvoiceBO.save(pExecutionContext, lGEMInvoiceBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        GEMInvoiceBean lFilterBean = new GEMInvoiceBean();
        lFilterBean.setId(pId);
        gEMInvoiceBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/gemresendworkflow/{id}")
    public String gemResendWorkflow(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        ,@PathParam("id") Long pId ) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        GEMInvoiceResendBean lFilterBean = new GEMInvoiceResendBean();
        lFilterBean.setGiId(pId);
        StringBuilder lSql = new StringBuilder();
        lSql.append(" SELECT * FROM GEMINVOICERESEND ");
        lSql.append(" WHERE GEMGIID = ").append(pId);
        lSql.append(" ORDER BY GEMCREATEDATETIME DESC ");
        List<GEMInvoiceResendBean> lGEMInvoiceResendBeanList = gemInvoiceResendDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        List<Object> lResults = new ArrayList<Object>();
        for (GEMInvoiceResendBean lGEMInvoiceResendBean : lGEMInvoiceResendBeanList) {
            lResults.add(gemInvoiceResendDAO.getBeanMeta().formatAsMap(lGEMInvoiceResendBean, null, Arrays.asList(new String[]{"status","createDateTime","responseData"}), true));            
        }
        HashMap<String, Object> lMap = new HashMap<>();
        lMap.put("workflow", lResults);
        return new JsonBuilder(lMap).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/markAsClosed/{id}")
    public void markAsClosed(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        ,@PathParam("id") Long pId ) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        GEMInvoiceBean lBean = new GEMInvoiceBean();
        lBean.setId(pId);
        lBean.setStatus(Status.Closed);
        gemInvoiceDAO.update(pExecutionContext.getConnection(),lBean,Arrays.asList(new String[]{"id","status"}));
    }
}