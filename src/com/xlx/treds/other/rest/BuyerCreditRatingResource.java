
package com.xlx.treds.other.rest;

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
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.other.bean.BuyerCreditRatingBean;
import com.xlx.treds.other.bo.BuyerCreditRatingBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/buyercreditrating")
public class BuyerCreditRatingResource {
 
	public static final Logger logger = Logger.getLogger(BuyerCreditRatingResource.class);

    private BuyerCreditRatingBO buyerCreditRatingBO;
    private BeanMeta buyerCreditRatingBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public BuyerCreditRatingResource() {
        super();
        buyerCreditRatingBO = new BuyerCreditRatingBO();
        buyerCreditRatingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BuyerCreditRatingBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","buyerCode","ratingAgency","ratingDate","expiryDate","rating","ratingDesc","ratingType","purName","status","ratingFile"});
        lovFields = Arrays.asList(new String[]{"id","buyerCode"});
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
        pRequest.getRequestDispatcher("/WEB-INF/buyercreditrating.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        BuyerCreditRatingBean lFilterBean = new BuyerCreditRatingBean();
        lFilterBean.setId(pId);
        BuyerCreditRatingBean lBuyerCreditRatingBean = buyerCreditRatingBO.findBean(pExecutionContext, lFilterBean);
        return buyerCreditRatingBeanMeta.formatAsJson(lBuyerCreditRatingBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = null;
        BuyerCreditRatingBean lFilterBean = new BuyerCreditRatingBean();
        List<String> lFields = null;

        if(!StringUtils.isEmpty(pFilter)){
        	lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            buyerCreditRatingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
            lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        }

        if (lFields == null) lFields = defaultListFields;
        List<BuyerCreditRatingBean> lBuyerCreditRatingList = buyerCreditRatingBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (BuyerCreditRatingBean lBuyerCreditRatingBean : lBuyerCreditRatingList) {
            lResults.add(buyerCreditRatingBeanMeta.formatAsArray(lBuyerCreditRatingBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey = "buyercreditrating-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BuyerCreditRatingBean lFilterBean = new BuyerCreditRatingBean();
        buyerCreditRatingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<BuyerCreditRatingBean> lBuyerCreditRatingList = buyerCreditRatingBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(buyerCreditRatingBeanMeta.formatBeansAsList(lBuyerCreditRatingList, null, 
        		lFields, true), "BuyerCreditRating", buyerCreditRatingBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BuyerCreditRatingBean lBuyerCreditRatingBean = new BuyerCreditRatingBean();
        List<ValidationFailBean> lValidationFailBeans = buyerCreditRatingBeanMeta.validateAndParse(lBuyerCreditRatingBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        buyerCreditRatingBO.save(pExecutionContext, lBuyerCreditRatingBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-manage")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BuyerCreditRatingBean lFilterBean = new BuyerCreditRatingBean();
        lFilterBean.setId(pId);
        buyerCreditRatingBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-view")
    @Path("/purchaserrating")
    public String getPurchaserRating(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = null;
        BuyerCreditRatingBean lFilterBean = new BuyerCreditRatingBean();
        List<String> lFields = null;

        if(!StringUtils.isEmpty(pFilter)){
        	lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            buyerCreditRatingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
            lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        }

        if (lFields == null) lFields = defaultListFields;
        List<BuyerCreditRatingBean> lBuyerCreditRatingList = buyerCreditRatingBO.findValidRatings(pExecutionContext.getConnection(), lMap, lUserBean);

        List<Object> lResults = new ArrayList<Object>();
        for (BuyerCreditRatingBean lBuyerCreditRatingBean : lBuyerCreditRatingList) {
            lResults.add((Object) buyerCreditRatingBeanMeta.formatAsMap(lBuyerCreditRatingBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
       
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured(secKey="buyercreditrating-manage")
    @Path("/buyerratingupload")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
            pRequest.getRequestDispatcher("/WEB-INF/buyerratingupl.jsp").forward(pRequest, pResponse);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "buyercreditrating-view")
    @Path("/ratinglov")
    public String lov(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse,
    		@QueryParam("ratingType") String pRatingType) throws Exception {
       return OtherResourceCache.getInstance().getRatingTypeJson(pRatingType);
    }

}