package com.xlx.treds.master.rest;

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
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bo.AuctionCalendarBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/auccal")
public class AuctionCalendarResource {

    private AuctionCalendarBO auctionCalendarBO;
    private BeanMeta auctionCalendarBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public AuctionCalendarResource() {
        super();
        auctionCalendarBO = new AuctionCalendarBO();
        auctionCalendarBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","date","bidStartTime","bidEndTime","active"});
        lovFields = Arrays.asList(new String[]{"id","type"});
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
        pRequest.getRequestDispatcher("/WEB-INF/auccal.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="auccal-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        AuctionCalendarBean lFilterBean = new AuctionCalendarBean();
        lFilterBean.setId(pId);
        AuctionCalendarBean lAuctionCalendarBean = auctionCalendarBO.findBean(pExecutionContext, lFilterBean);
        return auctionCalendarBeanMeta.formatAsJson(lAuctionCalendarBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="auccal-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AuctionCalendarBean lFilterBean = new AuctionCalendarBean();
        auctionCalendarBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AuctionCalendarBean> lAuctionCalendarList = auctionCalendarBO.findList(pExecutionContext, lFilterBean, null, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AuctionCalendarBean lAuctionCalendarBean : lAuctionCalendarList) {
            lResults.add(auctionCalendarBeanMeta.formatAsArray(lAuctionCalendarBean, null, null, true));            
        }
        return new JsonBuilder(lResults).toString();      
//        if(lAuctionCalendarList!=null && lAuctionCalendarList.size() > 0)
//        	return auctionCalendarBeanMeta.formatListAsJson(lAuctionCalendarList, null, lFields, true, true);
//        return "";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<AuctionCalendarBean> lAuctionCalendarList = auctionCalendarBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (AuctionCalendarBean lAuctionCalendarBean : lAuctionCalendarList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAuctionCalendarBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAuctionCalendarBean.getType());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="auccal-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="auccal-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AuctionCalendarBean lAuctionCalendarBean = new AuctionCalendarBean();
        List<ValidationFailBean> lValidationFailBeans = auctionCalendarBeanMeta.validateAndParse(lAuctionCalendarBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        auctionCalendarBO.save(pExecutionContext, lAuctionCalendarBean, lUserBean, pNew);
    }


}