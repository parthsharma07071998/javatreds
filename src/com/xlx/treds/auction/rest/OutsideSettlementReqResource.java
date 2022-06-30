package com.xlx.treds.auction.rest;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

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

import com.xlx.treds.auction.bean.ObligationExtensionBean;
import com.xlx.treds.auction.bean.OutsideSettlementReqBean;
import com.xlx.treds.auction.bo.OutsideSettlementReqBO;
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
import com.xlx.commonn.other.bean.FileDownloadBean;

@Path("/outsetreq")
public class OutsideSettlementReqResource {

    private OutsideSettlementReqBO outsideSettlementReqBO;
    private BeanMeta outsideSettlementReqBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public OutsideSettlementReqResource() {
        super();
        outsideSettlementReqBO = new OutsideSettlementReqBO();
        outsideSettlementReqBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(OutsideSettlementReqBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","status","createDate","createrAuId","approveRejectDate","approveRejectAuId"});
        lovFields = Arrays.asList(new String[]{"id","status"});
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
        pRequest.getRequestDispatcher("/WEB-INF/outsetreq.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="outsettle-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
    	OutsideSettlementReqBean lFilterBean = new OutsideSettlementReqBean();
        lFilterBean.setId(pId);
        return outsideSettlementReqBO.findBeanJson(pExecutionContext, lFilterBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="outsettle-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        OutsideSettlementReqBean lFilterBean = new OutsideSettlementReqBean();
        outsideSettlementReqBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        boolean lFin = false;
        if (lMap.containsKey("isFin")) {
        	lFin = (boolean) lMap.get("isFin");
        }
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<OutsideSettlementReqBean> lOutsideSettlementReqList = outsideSettlementReqBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean,lFin);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (OutsideSettlementReqBean lOutsideSettlementReqBean : lOutsideSettlementReqList) {
            lResults.add(outsideSettlementReqBeanMeta.formatAsArray(lOutsideSettlementReqBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey="outsettle-view")
//    @Path("/all")
//    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
//        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        List<OutsideSettlementReqBean> lOutsideSettlementReqList = outsideSettlementReqBO.findList(pExecutionContext, null, lovFields, lUserBean, l);
//        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
//        for (OutsideSettlementReqBean lOutsideSettlementReqBean : lOutsideSettlementReqList) {
//            Map<String, Object> lData = new HashMap<String, Object>();
//            lData.put(BeanFieldMeta.JSONKEY_VALUE, lOutsideSettlementReqBean.getId());
//            lData.put(BeanFieldMeta.JSONKEY_TEXT, lOutsideSettlementReqBean.getStatus());
//            lResults.add(lData);
//        }
//        return new JsonBuilder(lResults).toString();
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="outsettle-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="outsettle-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        OutsideSettlementReqBean lOutsideSettlementReqBean = new OutsideSettlementReqBean();
        List<ValidationFailBean> lValidationFailBeans = outsideSettlementReqBeanMeta.validateAndParse(lOutsideSettlementReqBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        outsideSettlementReqBO.save(pExecutionContext, lOutsideSettlementReqBean, lUserBean, pNew);
    }

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    @Secured(secKey="outsettle-save")
    public String createRequest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        OutsideSettlementReqBean lOutsideSettlementReqBean = new OutsideSettlementReqBean();
        List<ValidationFailBean> lValidationFailBeans = outsideSettlementReqBeanMeta.validateAndParse(lOutsideSettlementReqBean, 
            pMessage, BeanMeta.FIELDGROUP_INSERT , null);
        outsideSettlementReqBO.createRequest(pExecutionContext, pRequest, lOutsideSettlementReqBean,lUserBean);
        Map<String,Object> lMap = new HashMap<String, Object>();
        lMap.put("id",lOutsideSettlementReqBean.getId());
        return new JsonBuilder(lMap).toString();
    }

    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey={"outsettle-save", "outsettle-finstatus"})
    @Path("/status")
    public void updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        OutsideSettlementReqBean lOutsideSettlementReqBean = new OutsideSettlementReqBean();
        List<ValidationFailBean> lValidationFailBeans = outsideSettlementReqBeanMeta.validateAndParse(lOutsideSettlementReqBean, 
            pMessage, null, null);
        outsideSettlementReqBO.updateStatus(pExecutionContext, lOutsideSettlementReqBean, lUserBean);
    }

}