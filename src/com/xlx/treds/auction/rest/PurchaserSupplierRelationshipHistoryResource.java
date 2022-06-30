
package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierRelationshipHistoryBean;
import com.xlx.treds.auction.bo.PurchaserSupplierRelationshipHistoryBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/pursuprelationshiphistory")
@Singleton
public class PurchaserSupplierRelationshipHistoryResource {

	public static final Logger logger = LoggerFactory.getLogger(PurchaserSupplierRelationshipHistoryResource.class);

    private PurchaserSupplierRelationshipHistoryBO purchaserSupplierRelationshipHistoryBO;
    private BeanMeta purchaserSupplierRelationshipHistoryBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public PurchaserSupplierRelationshipHistoryResource() {
        super();
        purchaserSupplierRelationshipHistoryBO = new PurchaserSupplierRelationshipHistoryBO();
        purchaserSupplierRelationshipHistoryBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierRelationshipHistoryBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","supplier","purchaser","startDate","endDate"});
        lovFields = Arrays.asList(new String[]{"id","supplier"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        boolean lViewFlag = pRequest.getParameter("adm")!=null;
        String lJsp = null;
        if (lViewFlag)
        	lJsp = "pursuprelationshiphistory.jsp";
        else
        	lJsp = "pursuprelationshiphistory.jsp";
        pRequest.getRequestDispatcher("/WEB-INF/" + lJsp).include(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "pursuprelationshiphistory-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        lFilterBean.setId(pId);
        PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean = purchaserSupplierRelationshipHistoryBO.findBean(pExecutionContext, lFilterBean);
        return purchaserSupplierRelationshipHistoryBeanMeta.formatAsJson(lPurchaserSupplierRelationshipHistoryBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "pursuprelationshiphistory-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        purchaserSupplierRelationshipHistoryBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PurchaserSupplierRelationshipHistoryBean> lPurchaserSupplierRelationshipHistoryList = purchaserSupplierRelationshipHistoryBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean : lPurchaserSupplierRelationshipHistoryList) {
            lResults.add(purchaserSupplierRelationshipHistoryBeanMeta.formatAsArray(lPurchaserSupplierRelationshipHistoryBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
//    @Secured(secKey = "pursuprelationshiphistory-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        purchaserSupplierRelationshipHistoryBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PurchaserSupplierRelationshipHistoryBean> lPurchaserSupplierRelationshipHistoryList = purchaserSupplierRelationshipHistoryBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(purchaserSupplierRelationshipHistoryBeanMeta.formatBeansAsList(lPurchaserSupplierRelationshipHistoryList, null, 
        		lFields, true), "PurchaserSupplierRelationshipHistory", purchaserSupplierRelationshipHistoryBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<PurchaserSupplierRelationshipHistoryBean> lPurchaserSupplierRelationshipHistoryList = purchaserSupplierRelationshipHistoryBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean : lPurchaserSupplierRelationshipHistoryList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lPurchaserSupplierRelationshipHistoryBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lPurchaserSupplierRelationshipHistoryBean.getSupplier());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "pursuprelationshiphistory-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "pursuprelationshiphistory-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean = new PurchaserSupplierRelationshipHistoryBean();
        List<ValidationFailBean> lValidationFailBeans = purchaserSupplierRelationshipHistoryBeanMeta.validateAndParse(lPurchaserSupplierRelationshipHistoryBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        purchaserSupplierRelationshipHistoryBO.save(pExecutionContext, lPurchaserSupplierRelationshipHistoryBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "pursuprelationshiphistory-manage")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        lFilterBean.setId(pId);
        purchaserSupplierRelationshipHistoryBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getrelations")
    public String getRelationship(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        return purchaserSupplierRelationshipHistoryBO.getRelationship(pExecutionContext, lMap, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/changeRelation")
    public void changeRelation(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        purchaserSupplierRelationshipHistoryBO.changeRelation(pExecutionContext, lMap, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addRelation")
    public void addRelation(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        purchaserSupplierRelationshipHistoryBO.addRelation(pExecutionContext, lMap, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/removeRelation/{id}")
    public void removeRelation(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        PurchaserSupplierRelationshipHistoryBean lPurSupRelBean = new PurchaserSupplierRelationshipHistoryBean();
        lPurSupRelBean.setId(pId);
        purchaserSupplierRelationshipHistoryBO.removeRelation(pExecutionContext, lPurSupRelBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPurSupRelHistory")
    public String getPurSupRelHistory(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        purchaserSupplierRelationshipHistoryBeanMeta.validateAndParse(lFilterBean, lMap, null);
        Map<String, Object> lResultMap = purchaserSupplierRelationshipHistoryBO.getPurSupRelHistory(pExecutionContext.getConnection(), lFilterBean, lUserBean);
        return new JsonBuilder(lResultMap).toString();
    }
}