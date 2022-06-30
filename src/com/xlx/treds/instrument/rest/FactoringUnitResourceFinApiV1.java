package com.xlx.treds.instrument.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.FactoringUnitBidBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/factunitfin")
public class FactoringUnitResourceFinApiV1 {

    private FactoringUnitBO factoringUnitBO;
    private InstrumentBO instrumentBO;
    private BeanMeta factoringUnitBeanMeta;
    private BeanMeta obligationBeanMeta;
    private BeanMeta bidBeanMeta;
    private List<String> defaultListFields;
    public static final Logger logger = Logger.getLogger(FactoringUnitResourceFinApiV1.class);
    
    public FactoringUnitResourceFinApiV1() {
        super();
        instrumentBO = new InstrumentBO();
        factoringUnitBO = new FactoringUnitBO();
        factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
        bidBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BidBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","maturityDate","purchaser","supplier","amount","tenure","acceptedHaircut","capRate","status","filterSellerCategory","filterMsmeStatus"});
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/factunitfin.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	List<Object> lResults = new ArrayList<Object>();
    	try{
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            FactoringUnitBean lFilterBean = new FactoringUnitBean();
            factoringUnitBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
            List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
            boolean lShowAll = lMap.containsKey("showall");
            List<FactoringUnitBean> lFactoringUnitList = factoringUnitBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean, lShowAll);
            for (FactoringUnitBean lFactoringUnitBean : lFactoringUnitList) {
                if (lFields == null)
                    lResults.add(factoringUnitBeanMeta.formatAsMap(lFactoringUnitBean, null, defaultListFields, false));
                else
                    lResults.add(factoringUnitBeanMeta.formatAsArray(lFactoringUnitBean, null, lFields, true));            
            }
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally{
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return new JsonBuilder(lResults).toString();
    }   

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/finReport")
    public Object financierReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	List<Map<String, Object>> lJsonData = new ArrayList<Map<String, Object>>();
    	try{
    		JsonSlurper lJsonSlurper = new JsonSlurper();
	        if(!CommonUtilities.hasValue(pFilter)) pFilter = "{}";
	        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	        ObligationBean lFilterBean = new ObligationBean();
	        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
	        lJsonData = factoringUnitBO.getFinancierObligationJson(pExecutionContext.getConnection(), lFilterBean , lUserBean);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally{
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return new JsonBuilder(lJsonData).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/history")
    public String listHistory(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	try{
            if(!CommonUtilities.hasValue(pFilter)){
            	throw new CommonBusinessException("Please provide at least one filter");
            }
    		FactoringUnitBean lFilterFUBean = getFilterFUBean(pFilter);
    		ObligationBean lFilterObliBean = getFilterObliBean(pFilter);
    		lResponse =  factoringUnitBO.findHistoryFin(pExecutionContext, lUserBean, lFilterFUBean, lFilterObliBean);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally{
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/approval")
    public String listApproval(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	try{
    		lResponse = factoringUnitBO.findForApproval(pExecutionContext, lUserBean);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally{
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/watch")
    public String watchList(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
        	lResponse = factoringUnitBO.findWatchListFin(pExecutionContext, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/addwatch")
    public String watchAdd(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
        	JsonSlurper lJsonSlurper = new JsonSlurper();
            List<Object> lFuIds = (List<Object>)lJsonSlurper.parseText(pFilter);
            lResponse = factoringUnitBO.addToWatch(pExecutionContext, lFuIds, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/removewatch")
    public String watchRemove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
            JsonSlurper lJsonSlurper = new JsonSlurper();
            List<Object> lFuIds = (List<Object>)lJsonSlurper.parseText(pFilter);
            lResponse = factoringUnitBO.removeFromWatch(pExecutionContext, lFuIds, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-bid")
    @Path("/updatebid")
    public String updateBid(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
		Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            BidBean lBidBean = new BidBean();
            bidBeanMeta.validateAndParse(lBidBean, lData, null);
            List<Object> lFuIds = (List<Object>)lData.get("ids");
            lBidBean.setAppStatus(null);
            Map <String,String> lDataHash = new HashMap<String, String>();
            if (factoringUnitBO.hasCheckerForBid(lConnection, lUserBean.getId())){
            	
    			lDataHash.put("action", "Bid submission");
            }else{
                lDataHash.put("action", "Bid modification");
            }
            if (lDataHash!=null)
            	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, lConnection, AppConstants.OTP_NOTIFY_TYPE_BIDENTRY, AppConstants.OTP_NOTIFY_TYPE_BIDENTRY, lDataHash);
            lResponse = factoringUnitBO.updateBid(pExecutionContext, lFuIds, lBidBean, lUserBean);
            
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;	
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-bid-check")
    @Path("/updatebidstatus")
    public String updateBidStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            BidBean lBidBean = new BidBean();
            bidBeanMeta.validateAndParse(lBidBean, lData, null);
            List<Object> lFuIds = (List<Object>)lData.get("ids");
            if (lBidBean.getAppStatus() == null)
                throw new CommonBusinessException("Approval Status missing");
            Map<String, String> lDataHash = new HashMap<String,String>();
            if (BidBean.AppStatus.Approved.equals(lBidBean.getAppStatus())){
            	lDataHash.put("action", "Bid approval/rejection");
            }else if (BidBean.AppStatus.Rejected.equals(lBidBean.getAppStatus())){
            	lDataHash.put("action", "Bid approval/rejection");
            }
            if (!lDataHash.isEmpty()){
            	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_BIDCHKAPPREJ, AppConstants.TEMPLATE_PREFIX_BIDCHKAPPREJ, lDataHash);        	
            }
            lResponse = factoringUnitBO.updateBid(pExecutionContext, lFuIds, lBidBean, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-depth")
    @Path("/depth/{id}")
    public String depth(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("id") Long pId) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pId.toString(), this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
        	lResponse = factoringUnitBO.depth(pExecutionContext, pId, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }
    
    private FactoringUnitBean getFilterFUBean(String pFilter){
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, lMap, null);
		return lFactoringUnitBean;
    }
    private ObligationBean getFilterObliBean(String pFilter){
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		ObligationBean lObligationBean = new ObligationBean();
		obligationBeanMeta.validateAndParse(lObligationBean, lMap, null);
		return lObligationBean;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/viewclubbeddetails/{id}")
    public String getdetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pId.toString(), this.getClass().getName(),0);
    	String lResponse = null;
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
        	lResponse = new JsonBuilder(instrumentBO.findJsonForInstruments(lConnection, pId, lUserBean)).toString();
        }catch(Exception e){
        	lResponse = e.getMessage();
        	logger.debug(e.getStackTrace());
        	throw e;
        }finally{
        	ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }
    
    
//    @POST
//    @Secured(secKey="factunitfin-view")
//    @Path("/cersai")
//    public Object cersaiReportDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
//        String pFilter) throws Exception {
//        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        JsonSlurper lJsonSlurper = new JsonSlurper();
//        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
//        String lFactorDateStr = (String)lMap.get("factorDate");
//        Date lFactorDate = CommonUtilities.getDate(lFactorDateStr, "DD-MM-YYYY");
//        CersaiFileGenerator lCersaiFileGenerator = new CersaiFileGenerator();
//        
//    	return  lCersaiFileGenerator.cersaiDownload(pExecutionContext.getConnection(), lUserBean.getDomain(), lFactorDate, lUserBean);
//    }

    
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/calculatesplitcharge")
//    public String getFinPlatformCharge(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
//    		String pMessage) throws Exception{
//    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//    	JsonSlurper lJsonSlurper = new JsonSlurper();
//    	Map<String,Object> lRtnMap = new HashMap<String, Object>();
//    	Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
//    	FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean(); 
//    	lMap.get(lMap.get("fuId"));
//    	lFactoringUnitBean = factoringUnitDAO.findBean(pExecutionContext.getConnection(), lFactoringUnitBean);
//    	BidBean lBidBean = new BidBean(); 
//    	lMap.get(lMap.get("bidId"));
//    	lBidBean = bidDAO.findBean(pConnection, lBidBean);
//    	return factoringUnitBO.calculateFinacierChargeSharePercent(pExecutionContext.getConnection(),pMessage);
//    }
    
    
}