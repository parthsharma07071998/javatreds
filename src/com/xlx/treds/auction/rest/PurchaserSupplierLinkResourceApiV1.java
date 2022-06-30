package com.xlx.treds.auction.rest;

import java.sql.Connection;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.Status;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkWorkFlowBean;
import com.xlx.treds.auction.bo.PurchaserSupplierLinkBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/pursuplnk")
public class PurchaserSupplierLinkResourceApiV1 {

	private static Logger logger = Logger.getLogger(PurchaserSupplierLinkResourceApiV1.class);
	
    private PurchaserSupplierLinkBO purchaserSupplierLinkBO;
    private BeanMeta purchaserSupplierLinkBeanMeta;
	private List<String> defaultListFields, lovFields;
    private GenericDAO<PurchaserSupplierLinkWorkFlowBean> purchaserSupplierLinkWorkFlowDAO;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkProvDAO;
	
    public PurchaserSupplierLinkResourceApiV1() {
        super();
        purchaserSupplierLinkBO = new PurchaserSupplierLinkBO();
        purchaserSupplierLinkBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class);
        purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        purchaserSupplierLinkProvDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        purchaserSupplierLinkWorkFlowDAO = new GenericDAO<PurchaserSupplierLinkWorkFlowBean>(PurchaserSupplierLinkWorkFlowBean.class);
        defaultListFields = Arrays.asList(new String[]{"supplier","purchaser","supplierPurchaserRef","creditPeriod","status","purchaserSupplierRef","costBearingType"});
        lovFields = Arrays.asList(new String[]{"supplier","purchaser"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("supplier") String pSupplier, @QueryParam("purchaser") String pPurchaser) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pSupplier != null) && (pPurchaser != null)) {
            Object[] lKey = new Object[]{pSupplier, pPurchaser};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/pursuplnk.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/get")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	//TODO: determine whether it is called by financier also
/*        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
        	if(!lUserBean.getDomain().equals(pSupplier) || !lUserBean.getDomain().equals(pPurchaser))
        		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
*/    	//
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	    	JsonSlurper lJsonSlurper = new JsonSlurper();
	    	String lSupplier = null;
	    	Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
	    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
	        Connection lConnection = pExecutionContext.getConnection();
	        if (lMap.containsKey("supplier")){
	        	lSupplier = (String)lMap.get("supplier");
	        }else{
	        	throw new CommonBusinessException("Supplier code not found.");
	        }
	        lFilterBean.setSupplier(lSupplier);
	        lFilterBean.setPurchaser(lUserBean.getDomain());
	        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkBO.findBean(pExecutionContext, lFilterBean);
	        lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	        
	        Map<String, Object> lDetailMap = purchaserSupplierLinkBeanMeta.formatAsMap(lPurchaserSupplierLinkBean, null, null, false, false); 
	        // workflow
	        List<Map<String, Object>> lWorkFlowMaps = new ArrayList<Map<String,Object>>();
	        DBHelper lDBHelper = DBHelper.getInstance();
	        String lSql = "SELECT * FROM PurchaserSupplierLinkWorkFlow WHERE PLWSUPPLIER = " + lDBHelper.formatString(lSupplier) + " AND PLWPURCHASER = " + lDBHelper.formatString(lUserBean.getDomain()) + " ORDER BY PLWSTATUSUPDATETIME DESC, PLWId DESC";
	        List<PurchaserSupplierLinkWorkFlowBean> lWorkFlows = purchaserSupplierLinkWorkFlowDAO.findListFromSql(lConnection, (String)lSql, 0);
	        for (PurchaserSupplierLinkWorkFlowBean lPSLinkWorkFlow : lWorkFlows) {
	            lWorkFlowMaps.add(purchaserSupplierLinkWorkFlowDAO.getBeanMeta().formatAsMap(lPSLinkWorkFlow, null, null, true, true));
	        }
	        lDetailMap.put("workFlows", lWorkFlowMaps);
	        
		    lResponse = new JsonBuilder(lDetailMap).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	    	if(StringUtils.isEmpty(pFilter)){
	    		pFilter = "{}";
	    	}
	        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        AppUserBean lAppUserBean = (AppUserBean) lUserBean;
	        JsonSlurper lJsonSlurper = new JsonSlurper();
	        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
	        purchaserSupplierLinkBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
	
	        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
	        String lSender  = (String)lMap.get(AppConstants.PARAM_SENDER);
	    	ApprovalStatus lApprovalStatus = null;
	    	Status lStatus = null;
	    	lApprovalStatus = lFilterBean.getApprovalStatus();
	    	lStatus = lFilterBean.getStatus();
	    	lFilterBean.setApprovalStatus(null);
	        if (lFields == null) lFields = defaultListFields;
	        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
	        List<PurchaserSupplierLinkBean> lFilteredList = new ArrayList<PurchaserSupplierLinkBean>();
	        Map<String, Object> lPurSupMap = new HashMap<>();
	        List<Object> lResults = new ArrayList<Object>();
	        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
	    		if(lApprovalStatus!=null){
	    			if(PurchaserSupplierLinkBean.ApprovalStatus.Draft.equals(lApprovalStatus)){
	                	if(lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_INBOX)  ){
	                    	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	                	}
	    			}else if(PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lApprovalStatus)){
	                	if(lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_PENDINGAPPROVAL)  ){
	                    	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	                	}
	    			} 
	    		}else{
	    			if(lStatus!=null){
	            		//only Active and Suspended sent for Api User
	                	if(Status.Active.equals(lStatus) && lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_ACTIVE)){
	                    	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	                		lFilteredList.add(lPurchaserSupplierLinkBean);
	                        lResults.add(purchaserSupplierLinkBeanMeta.formatAsArray(lPurchaserSupplierLinkBean, "apiList", lFields, true));        		
	                	}else if((Status.Suspended_by_Buyer.equals(lStatus) || Status.Suspended_by_Seller.equals(lStatus)) &&  lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_SUSPENDED) ){
	                    	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	                		lFilteredList.add(lPurchaserSupplierLinkBean);
	                        lResults.add(purchaserSupplierLinkBeanMeta.formatAsArray(lPurchaserSupplierLinkBean, "apiList", null, true));
	                	}
	    				
	    			}else{
	            		//only Active and Suspended sent for Api User
	                	if(lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_ACTIVE) || 
	                			lPurchaserSupplierLinkBean.getTab().equals(PurchaserSupplierLinkBean.TABINDEX_SUSPENDED) ){
	                    	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
	                		lFilteredList.add(lPurchaserSupplierLinkBean);
	                		lPurSupMap = purchaserSupplierLinkBeanMeta.formatAsMap(lPurchaserSupplierLinkBean, "apiList", null, false);
	                    	if (CommonAppConstants.Yes.Yes.equals(lFilterBean.getFetchSupGstn())) {
	                    		lPurSupMap.put("supGstn",TredsHelper.getInstance().getEntityGstnList(pExecutionContext.getConnection(),lPurchaserSupplierLinkBean.getSupplier()));
	                    	}
	                		lResults.add(lPurSupMap);
	                	}
	    			}
	    		}
	        }
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/lov")
    public String listMap(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, null, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findListForMap(pExecutionContext, lUserBean);

        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
            lResults.add(purchaserSupplierLinkBeanMeta.formatAsMap(lPurchaserSupplierLinkBean));
        }
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/purchaser")
    public String lovPur(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, null, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        lResponse =  purchaserSupplierLinkBO.findListForLov(pExecutionContext, true, lUserBean,null);
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/supplier")
    public String lovSup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, null, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        lResponse =  purchaserSupplierLinkBO.findListForLov(pExecutionContext, false, lUserBean,null);
		}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    }
	    return lResponse;
	}
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/supplier/{purchaser}")
    public String lovSup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("purchaser") String pPurchaser) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, null, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLinkBean lFilterBean = null;
        if(CommonUtilities.hasValue(pPurchaser)){
        	lFilterBean = new PurchaserSupplierLinkBean();
        	lFilterBean.setPurchaser(pPurchaser);
        }
	        lResponse = purchaserSupplierLinkBO.findListForLov(pExecutionContext, false, lUserBean, lFilterBean);
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    public String insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Connection lConnection = pExecutionContext.getConnection();
	        //conversion for IOCL
	        IClientAdapter lClientAdpater  = null;
	        String lOriginalMessage = pMessage;
	    	ProcessInformationBean lProcessInformationBean = null;
	        if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()) && 
	        		CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) ){
	            lClientAdpater = ClientAdapterManager.getInstance().getClientAdapter(lAppUserBean.getDomain());
	            //if api user is using our apis then there will be no adpater
	            if(lClientAdpater!=null){
	            	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_PURSUPLINK, lConnection);
	            	lProcessInformationBean.setClientDataForProcessing(pMessage);
	            	pMessage = lClientAdpater.convertClientDataToTredsData(lProcessInformationBean);
	            }
	        }

	        if(lClientAdpater==null) {
	            if(StringUtils.isEmpty(pMessage)){
	            	pMessage = "{}";
	        	}
	            PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
	            List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, 
	                pMessage, BeanMeta.FIELDGROUP_INSERT , null);
	            validatePSFields(lPurchaserSupplierLinkBean);	        	
	        }
	        
	    	PurchaserSupplierLinkBean lPSBean = null;
	    	lPSBean = save(pExecutionContext, pRequest, pMessage, true, null);
	        if(lClientAdpater!=null){
	        	lProcessInformationBean.setTredsDataForProcessing(lPSBean);
	        	lResponse =  lClientAdpater.convertTredsDataToClientData(lProcessInformationBean);
	        }else {
	        	//For API User : If the User has specified that it has to be submitted then it will be submitted by updating the ApprovalStatus to Submitted so that the workflow remains
	            if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()) && 
	            		CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) ){
	        		lPSBean.setInWorkFlow(Yes.Yes);
	            	lPSBean.setApprovalStatus(ApprovalStatus.Submitted);
	            	purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPSBean, lAppUserBean);
	            }
	            lResponse = purchaserSupplierLinkBeanMeta.formatAsJson(lPSBean,PurchaserSupplierLinkBean.FIELDGROUP_RESPFIELDSAPI,null, false, false);
	        }
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    public String update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        PurchaserSupplierLinkBean lPSBean = save(pExecutionContext, pRequest, pMessage, false, null);
	        lResponse = purchaserSupplierLinkBeanMeta.formatAsJson(lPSBean,PurchaserSupplierLinkBean.FIELDGROUP_RESPFIELDSAPI,null, false, false);
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }


    private PurchaserSupplierLinkBean save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew, IAppUserBean pAppUserBean) throws Exception {
        if(StringUtils.isEmpty(pMessage)){
        	pMessage = "{}";
    	}
       IAppUserBean lUserBean = null;
       if(pAppUserBean==null) {
    	   lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
       }else {
    	   lUserBean = pAppUserBean;
       }
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
        
        List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE , null);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
    	//if it is new then no need to check status and approvalStatus since it will be put as default
        if(pNew){
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)){
            	ValidationFailBean lFailBean = null;
            	Map<String, BeanFieldMeta>  lFieldMap = purchaserSupplierLinkBeanMeta.getFieldMap();
            	BeanFieldMeta lStatusMeta = lFieldMap.get("status");
            	BeanFieldMeta lAppStatusMeta = lFieldMap.get("approvalStatus");
            	for(int lPtr=lValidationFailBeans.size()-1; lPtr >= 0; lPtr--){
            		lFailBean =  lValidationFailBeans.get(lPtr);
            		if(lStatusMeta!=null && lStatusMeta.getLabel().equals(lFailBean.getName())){
            			lValidationFailBeans.remove(lPtr);
            		}
            		if(lAppStatusMeta!=null && lAppStatusMeta.getLabel().equals(lFailBean.getName())){
            			lValidationFailBeans.remove(lPtr);
            		}
            	}
            }
    	}
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if (PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getStatus())){
        	//API WILL NOT HAVE OTP VERIFICATION
        }
        validatePSFields(lPurchaserSupplierLinkBean);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
        		CommonAppConstants.Yes.Yes.equals( ((AppUserBean)lUserBean).getEnableAPI()) ){
	        IClientAdapter lClientAdpater = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
            //if api user is using our apis then there will be no adpater
            if(lClientAdpater==null){
            	//defaulting for BHEL
            	lPurchaserSupplierLinkBean.setPurchaserAutoApproveInvoice(YesNo.No);
            	lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(null);
            }
        }

        lPurchaserSupplierLinkBean= purchaserSupplierLinkBO.save(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean, pNew, false, null, true);
        return lPurchaserSupplierLinkBean;
    }
    
    private void validatePSFields(PurchaserSupplierLinkBean pPurchaserSupplierLinkBean) throws Exception {
        //,"purchaserSupplierRef"
    	//,"purchaserAutoApproveInvoice"-default N
    	//,"sellerAutoApproveInvoice"-default null
        String[] lMandatoryFields = new String[] { "supplier","purchaser","creditPeriod","bidAcceptingEntityType","costBearingType","chargeBearer","settleLeg3Flag","autoAccept","autoAcceptableBidTypes","autoConvert","invoiceMandatory","remarks","cashDiscountPercent","haircutPercent","instrumentCreation" };
        List<String> lMFList = new ArrayList<String>();
        for(String lField : lMandatoryFields) {
                lMFList.add(lField);
        }
        List<BeanFieldMeta> lMandtoryBFMeta = purchaserSupplierLinkBeanMeta.getFieldListFromNames(lMFList);
        Object lValue = null;
        String lErrorMessage = "";
        for(BeanFieldMeta lBeanFieldMeta : lMandtoryBFMeta) {
                lValue = lBeanFieldMeta.getProperty(pPurchaserSupplierLinkBean);
                if(lValue==null || StringUtils.isEmpty(lValue.toString())) {
                        lErrorMessage += lBeanFieldMeta.getName() + ":Cannot be empty(null). ";
                }
        }
        if(StringUtils.isNotEmpty(lErrorMessage)) {
            throw new CommonBusinessException(lErrorMessage);
        }
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/status")
    public String updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Connection lConnection = pExecutionContext.getConnection();
	        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
	        JsonSlurper lJsonSlurper = new JsonSlurper();
	        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
	        IClientAdapter lClientAdapter = null;
	        ProcessInformationBean lProcessInformationBean = null;
	        Connection lAdapterConnection = null;
	        Map<String,String> lRtnMap = new HashMap<String,String>();
	        ApiResponseStatus lResponseStatus = null;
	        //
	        try{
	    		lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
	    		if(lClientAdapter != null){
	    			lAdapterConnection = DBHelper.getInstance().getConnection();
	    			lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_PURSUPLINK,lAdapterConnection);
	    			lProcessInformationBean.setClientDataForProcessing(pMessage);
	    			lClientAdapter.logInComing(lProcessInformationBean, "/v1/pursuplnk/status",null, true, false);
	    			//conversion to actual data to be used by BO
	    			lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
	    			lMap = (Map<String, Object>)lProcessInformationBean.getProcessedTredsData();
	    		}
	        	//
	            String lPrimaryKey = null;
	            String[] lParts = new String[3];
	            String lSup = null;
	            String lPur = null;
	            //
	        	lPrimaryKey = (String) lMap.get("id");
	        	if(CommonUtilities.hasValue(lPrimaryKey)){
	        		lParts = lPrimaryKey.split("/");
	        		lSup = lParts[0]; 
	                lPur = lParts[1]; 
	        	}else{
	        		//throw error
	        	}
	    		lPurchaserSupplierLinkBean.setSupplier(lSup);
	    		lPurchaserSupplierLinkBean.setPurchaser(lPur);
	    		//"supplierPurchaserRef","sellerAutoApproveInvoice", "remarks"
	    		String lApprovalStatusCode = (String) lMap.get("approvalStatus");
	    		String lApprovalRemarks = (String) lMap.get("approvalRemarks");
	    		String lReference = null, lAutoApprove = null;
	    		//supplierPurchaserRef - sellerAutoApproveInvoice --> only if status is  it is approved
	    		ApprovalStatus lApprovalStatus = null;
	    		for(ApprovalStatus lAStatus : ApprovalStatus.values()){
	    			if(lAStatus.getCode().equals(lApprovalStatusCode)){
	    				lApprovalStatus = lAStatus;
	    				break;
	    			}
	    		}
	    		lPurchaserSupplierLinkBean.setApprovalStatus(lApprovalStatus);
	    		lPurchaserSupplierLinkBean.setRemarks(lApprovalRemarks);
	
	    		//these two status are returned by seller hence allowing him to save the same on returning also
	    		if(ApprovalStatus.Approved.equals(lApprovalStatus) ||
	    				ApprovalStatus.Returned.equals(lApprovalStatus)	){
	    			lReference= (String) lMap.get("supplierPurchaserRef");
	    			lAutoApprove= (String) lMap.get("sellerAutoApproveInvoice");
	    			lPurchaserSupplierLinkBean.setSupplierPurchaserRef(lReference);
	    			if(CommonAppConstants.YesNo.Yes.getCode().equals(lAutoApprove)){
	    				lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.Yes);
	    			}else{
	    				lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.No);
	    			}
	    			String lAutoConvert= (String) lMap.get("autoConvert");
	    			AutoConvert lTmpAutoConvert = AutoConvert.Auto;
	    			if(StringUtils.isNotBlank(lAutoConvert)){
	    				for(AutoConvert lTmp : AutoConvert.values() ){
	    					if(lTmp.getCode().equals(lAutoConvert)){
	    						lTmpAutoConvert = lTmp;
	    						break;
	    					}						
	    				}
	    			}
	    			lPurchaserSupplierLinkBean.setAutoConvert(lTmpAutoConvert);
	    		}
	    		String lBeanJson = purchaserSupplierLinkBeanMeta.formatAsJson(lPurchaserSupplierLinkBean);
	
	            List<ValidationFailBean> lValidationFailBeans = null;
	            
	            String lFieldGroup = null;
	
	            if(lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getPurchaser())){
	            	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS;
	            }else if(lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getSupplier())){
	            	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIERAPPROVALSTATUS;
	            }
	            lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, 
	            		lBeanJson, lFieldGroup, null);
	            
	            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
	                throw new CommonValidationException(lValidationFailBeans);
	            purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean);
	            lRtnMap.put("status", "success");
	            logger.info(new JsonBuilder(lRtnMap).toString());
	            lResponseStatus = ApiResponseStatus.Success;
	        }catch(Exception lMainException){
	        	lRtnMap.put("message", TredsHelper.getInstance().returnErrorMessage(lMainException));
	        	lResponseStatus = ApiResponseStatus.Failed;
	        	if (lClientAdapter!=null){
		        		lResponse = TredsHelper.getInstance().returnErrorMessage(lMainException); 
		        	}else{
	        	throw lMainException;
					}
	        }finally{
	            if(lClientAdapter!=null){
	            	//log the response to be sent
	            	lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lRtnMap).toString());
	            	lClientAdapter.logInComing(lProcessInformationBean, "/v1/pursuplnk/status",lResponseStatus, false, false);
	            }
	        	if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
	        		lAdapterConnection.close();
	        	}
	        }
		        if(StringUtils.isEmpty(lResponse)) {
			        lResponse = new JsonBuilder(lRtnMap).toString(); 
		        }
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/modifyCode")
    public void updateModifyCode(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pMessage) throws Exception {
    	 AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	 AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
    	 PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
         JsonSlurper lJsonSlurper = new JsonSlurper();
         Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
         String lPrimaryKey = (String) lMap.get("id");
         String[] parts = lPrimaryKey.split("/");
         String lSup = parts[0]; 
         String lPur = parts[1]; 
 		 lPurchaserSupplierLinkBean.setSupplier(lSup);
 		 lPurchaserSupplierLinkBean.setPurchaser(lPur);
 		 String lModifyCode = (String)lMap.get("code");
 		 if(lAppEntityBean!= null){
 			 if (lAppEntityBean.isPurchaser())
 		 		 lPurchaserSupplierLinkBean.setPurchaserSupplierRef(lModifyCode);
 			 else if (lAppEntityBean.isSupplier())
 		 		 lPurchaserSupplierLinkBean.setSupplierPurchaserRef(lModifyCode);
 		 }
 		 purchaserSupplierLinkBO.updateModifyCode(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/sendReminder")
    public void sendReminder(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pMessage) throws Exception {
    	 AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	 PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
         JsonSlurper lJsonSlurper = new JsonSlurper();
         Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
         String lPrimaryKey = (String) lMap.get("id");
         String[] parts = lPrimaryKey.split("/");
         String lSup = parts[0]; 
         String lPur = parts[1]; 
 		 lPurchaserSupplierLinkBean.setSupplier(lSup);
 		 lPurchaserSupplierLinkBean.setPurchaser(lPur);
 		 purchaserSupplierLinkBO.sendReminder(pExecutionContext.getConnection(), lPurchaserSupplierLinkBean, lUserBean);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@Secured(secKey="pursuplnk-save")
    @Path("/bulkupload")
    public String insertMultiple(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	List<Object> lResultList = new ArrayList<>();
    	AppUserBean lLoggedInAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	PurchaserSupplierLinkBean lPSBean = null;
    	List<Object> lInputList = (List<Object>) new JsonSlurper().parseText(pMessage);
    	String lPurchaser = null;

        if(!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInAppUserBean.getDomain()) ){
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppUserBean lPurchaserLogin = new AppUserBean();
    	for (Object lData : lInputList) {
    		lPurchaser = (String) ((Map<String,Object>)lData).get("purchaser");
    		lPurchaserLogin.setDomain(lPurchaser);
    		lPurchaserLogin.setId(lLoggedInAppUserBean.getId());
    		//
    		String lMessage = new JsonBuilder(lData).toString();
    		PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
    		lFilterBean.setPurchaser(lPurchaser);
    		lFilterBean.setSupplier((String) ((Map<String,Object>)lData).get("purchaser"));
    		PurchaserSupplierLinkBean lOldPurchaserSupplierLinkBean = purchaserSupplierLinkProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), lFilterBean);
    		PurchaserSupplierLinkBean lFinalLinkBean = purchaserSupplierLinkDAO.findByPrimaryKey(pExecutionContext.getConnection(), lFilterBean);
    		if ((lOldPurchaserSupplierLinkBean!=null || lFinalLinkBean!=null)) {
        		throw new CommonBusinessException("Link already created ");
        	}
    		try {
    			lPSBean = save(pExecutionContext, pRequest, lMessage, true,lPurchaserLogin);
        		lPSBean.setInWorkFlow(Yes.Yes);
            	lPSBean.setApprovalStatus(ApprovalStatus.Submitted);
            	purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPSBean, lPurchaserLogin);
            	lResultList.add(purchaserSupplierLinkBeanMeta.formatAsMap(lPSBean,PurchaserSupplierLinkBean.FIELDGROUP_RESPFIELDSAPI, null, false));
    		}catch(Exception e){
    			lResultList.add(e.getMessage());
    		}
            
    	}
	    	lResponse = new JsonBuilder(lResultList).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
}