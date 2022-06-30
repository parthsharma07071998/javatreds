package com.xlx.treds.master.rest;

import java.sql.Date;
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

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.master.bean.AuctionChargePlanBean;
import com.xlx.treds.master.bo.AuctionChargePlansBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/auctionchargeplans")
public class AuctionChargePlansResource {

    private AuctionChargePlansBO auctionChargePlansBO;
    private BeanMeta auctionChargePlanBeanMeta;
	private List<String> defaultListFields, lovFields;
	String defaultPlanListFields;
	private AuctionChargePlansBO auctionChargeSlabsBO;
	private BeanMeta auctionChargeSlabBeanMeta;
	
	
    public AuctionChargePlansResource() {
        super();
        auctionChargePlansBO = new AuctionChargePlansBO();
        auctionChargePlanBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionChargePlanBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","name","type"});
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
        pRequest.getRequestDispatcher("/WEB-INF/auctionchargeplans.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="auchrgplans-save")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        AuctionChargePlanBean lFilterBean = new AuctionChargePlanBean();
        lFilterBean.setId(pId);
        AuctionChargePlanBean lAuctionChargePlansBean = auctionChargePlansBO.findBean(pExecutionContext, lFilterBean);
        return auctionChargePlanBeanMeta.formatAsJson(lAuctionChargePlansBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="auchrgplans-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AuctionChargePlanBean lFilterBean = new AuctionChargePlanBean();
        
        auctionChargePlanBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        if(StringUtils.isNotEmpty(lFilterBean.getName())){
        	lFilterBean.setId(new Long(lFilterBean.getName()));
        	lFilterBean.setName(null);
        }
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AuctionChargePlanBean> lAuctionChargePlansList = auctionChargePlansBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AuctionChargePlanBean lAuctionChargePlansBean : lAuctionChargePlansList) {
            lResults.add(auctionChargePlanBeanMeta.formatAsArray(lAuctionChargePlansBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured (secKey="auchrgplans-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AuctionChargePlanBean lFilterBean = new AuctionChargePlanBean();
        auctionChargePlanBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AuctionChargePlanBean> lAuctionChargePlansList = auctionChargePlansBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AuctionChargePlanBean lAuctionChargePlansBean : lAuctionChargePlansList) {
            lResults.add(auctionChargePlanBeanMeta.formatAsArray(lAuctionChargePlansBean, null, lFields, true));            
        }
    	return new FileDownloadBean("AuctionChargePlan.csv", 
    			auctionChargePlanBeanMeta.formatBeansAsCsv(lAuctionChargePlansList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured 
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<AuctionChargePlanBean> lAuctionChargePlansList = auctionChargePlansBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (AuctionChargePlanBean lAuctionChargePlansBean : lAuctionChargePlansList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAuctionChargePlansBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAuctionChargePlansBean.getName());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="auchrgplans-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="auchrgplans-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AuctionChargePlanBean lAuctionChargePlansBean = new AuctionChargePlanBean();
        List<ValidationFailBean> lValidationFailBeans = auctionChargePlanBeanMeta.validateAndParse(lAuctionChargePlansBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        auctionChargePlansBO.save(pExecutionContext, lAuctionChargePlansBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="auchrgplans-save")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AuctionChargePlanBean lFilterBean = new AuctionChargePlanBean();
        lFilterBean.setId(pId);
        auctionChargePlansBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured 
    @Path("/entity/{code}")
    public String getAucplan(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
    	Date lBusinessDate = TredsHelper.getInstance().getBusinessDate(); 
        MemberwisePlanBean lMemberwisePlanBean = TredsHelper.getInstance().getPlan(pExecutionContext.getConnection(), pCode, lBusinessDate, true);
        AuctionChargePlanBean lAuctionChargePlansBean = TredsHelper.getInstance().getPlanDetails(pExecutionContext.getConnection(),lMemberwisePlanBean.getAcpId() );
        return auctionChargePlanBeanMeta.formatAsJson(lAuctionChargePlansBean);
    }

}