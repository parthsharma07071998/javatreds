package com.xlx.treds.instrument.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bean.InstrumentCreationKeysBean;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.instrument.bo.InstrumentCreationKeysBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/instcntr")
public class InstrumentCounterResourceApiV1 {

	private static Logger logger = Logger.getLogger(InstrumentCounterResourceApiV1.class);
	
    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
	private List<String> defaultListFields, editFields;
	
    public InstrumentCounterResourceApiV1() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime","counterModifiedFields","supGstn","purGstn","instNumber","counterRefNum","supplierRef","purchaserRef"});
        editFields = Arrays.asList(new String[]{"id","autoAccept","costBearingType","chargeBearer","settleLeg3Flag", "purchaser","supplier"});
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-view")
    @Path("/get")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        InstrumentBean lFilterBean = new InstrumentBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
    	Long lInstId = null;
    	Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
    	 if (lMap.containsKey("id")){
    		 lInstId = Long.valueOf(((Number)lMap.get("id")).longValue());
         }else{
         	throw new CommonBusinessException("Instrument Not found.");
         }
        lFilterBean.setId(lInstId);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setCounterEntity(lUserBean.getDomain());
        if (TredsHelper.getInstance().checkOwnership(lUserBean) ) 
            lFilterBean.setCounterAuId(TredsHelper.getInstance().getOwnerAuId(lUserBean));
        InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        lInstrumentBean.populateNonDatabaseFields();
        instrumentBO.updateFieldsFromCounterUpdate(lInstrumentBean, lUserBean);
        IClientAdapter lClientAdpater = null;
        ProcessInformationBean lProcessInformationBean = null;
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
        		CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){

            if(lClientAdpater==null) lClientAdpater = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
            //if api user is using our apis then there will be no adpater
            if(lClientAdpater!=null){
            	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, pExecutionContext.getConnection());
            }
        }
        if(lClientAdpater!=null){
        	lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
        	lResponse = lClientAdpater.convertTredsDataToClientData(lProcessInformationBean);
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
    @Secured(secKey="instcntr-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	if(StringUtils.isEmpty(pFilter)){
    		pFilter = "{}";
    	}
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        //defaulting the list an only showing instruments for approval.
        //20200330 - this was causing problem for BHEL ISG integration. 
        //but not know for whom we required that we show only instrument for checker approval.
        //if (lFilterBean.getStatus()==null){
        //	lFilterBean.setStatus(Status.Checker_Approved);
        //}
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounter(pExecutionContext, lFilterBean, null, lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, -1,Boolean.FALSE);
        List<Object> lResults = new ArrayList<Object>();
        IClientAdapter lClientAdpater  = null;
	   	 ProcessInformationBean lProcessInformationBean = null;
	     if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
	     		CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
	
	         if(lClientAdpater==null) {
	        	 lClientAdpater = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
	         }
	         //if api user is using our apis then there will be no adpater
	         if(lClientAdpater!=null){
	         	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, pExecutionContext.getConnection());
	         }
	     }
	     Map<String,Object> lTmpMap = null;
	     Map<String, Object> lCustomFieldMap = null ;
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
             if(lClientAdpater!=null){
             	lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
             	lClientAdpater.convertTredsDataToClientData(lProcessInformationBean);
             	lResults.add(lProcessInformationBean.getProcessedClientData());
             }else {
            	 lTmpMap = instrumentBeanMeta.formatAsMap(lInstrumentBean, null, lFields, false);
                 if (lInstrumentBean.getCfId()!=null && lInstrumentBean.getCfData()!=null) {
                 	 lCustomFieldMap = (Map<String, Object>)lJsonSlurper.parseText(lInstrumentBean.getCfData());
                 	 if (lCustomFieldMap!=null) {
                 		 lTmpMap.putAll(lCustomFieldMap);
                 	 }
                 }
                 lResults.add(lTmpMap);            
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
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-approve")
    public String update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, String> lDataHash = new HashMap<String,String>();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        InstrumentBean lInstrumentBean = new InstrumentBean();
        Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        lInstrumentBean.setId(Long.parseLong(lMap.get("id").toString()));
        if(lMap.containsKey("counterRefNum") && StringUtils.isNotEmpty((String)lMap.get("counterRefNum"))) {
            lInstrumentBean.setCounterRefNum((String)lMap.get("counterRefNum"));
        }
        lInstrumentBean = instrumentBO.findBean(pExecutionContext.getConnection(), lInstrumentBean);
        InstrumentBean lApiFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lApiFilterBean, lMap, null, null);
        if (lInstrumentBean==null){
        	throw new CommonBusinessException("instrument not found");
        }
        lInstrumentBean.populateNonDatabaseFields();
        if(Status.Counter_Approved.getCode().equals(lMap.get("status"))){
            lInstrumentBean.setStatus(Status.Counter_Approved);        	
        }else{
        	throw new CommonBusinessException("Invalid Status.Please add a valid status.");
        }
		String lDispFieldGroup = null;
		if (lUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
			lDispFieldGroup = "counterBuyerDisplayApi";
		} else if (lUserBean.getDomain().equals(lInstrumentBean.getSupplier())) {
			lDispFieldGroup = "counterSellerDisplayApi";
		}
		GenericDAO<InstrumentBean> instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class); 
		List<BeanFieldMeta> lFieldsMeta = instrumentDAO.getBeanMeta().getFieldMetaList(lDispFieldGroup, null);
		BeanFieldMeta lFieldMeta = null;
		Object lNewVal=null;
		List<String> lFieldList = new ArrayList<String>();
		for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
			lFieldMeta = lFieldsMeta.get(lPtr);
			if(!lMap.containsKey(lFieldMeta.getName())) {
				continue;
			}
			lNewVal = lMap.get(lFieldMeta.getName());
			if(lNewVal == null) {
				continue;
			}
			lFieldList.add(lFieldMeta.getName());
		}
		if(!lFieldList.isEmpty()) {
			InstrumentBean lRecdBean = new InstrumentBean();
			instrumentBeanMeta.validateAndParse(lRecdBean, lMap, null);
			instrumentBeanMeta.copyBean(lRecdBean, lInstrumentBean,null, lFieldList);
		}
		BigDecimal lNetAmount = lInstrumentBean.getAmount();
		if (lInstrumentBean.getAdjAmount() != null)	lNetAmount = lNetAmount.subtract(lInstrumentBean.getAdjAmount());
		if (lInstrumentBean.getCashDiscountValue() != null) lNetAmount = lNetAmount.subtract(lInstrumentBean.getCashDiscountValue());
		if (lInstrumentBean.getTdsAmount() != null)	lNetAmount = lNetAmount.subtract(lInstrumentBean.getTdsAmount());
		if (lNetAmount.compareTo(BigDecimal.ZERO) <= 0)
			throw new CommonBusinessException("Factoring unit cost cannot be zero or negative.");
		if (lNetAmount.compareTo(lInstrumentBean.getNetAmount()) != 0)
			throw new CommonBusinessException("Net amount does not tally as per calculations.");
		lInstrumentBean.setNetAmount(lNetAmount);
        Map<String , Object> lReturnMap = (Map<String, Object>) lJsonSlurper.parseText(instrumentBO.updateCounterStatus(pExecutionContext, lInstrumentBean, lUserBean,lApiFilterBean));
        lReturnMap.put("id", lInstrumentBean.getId());
	        lResponse = new JsonBuilder(lReturnMap).toString();
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
    @Secured(secKey="instcntr-reject")
    @Path("/status/courej")
    public String updateStatusReject(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	//conversion for IOCL
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        Map<String , Object> lReturnMap = new HashMap<String ,Object>();
        try{
        	if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()) && 
            		CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) ){
                lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lAppUserBean.getDomain());
                //if api user is using our apis then there will be no adpater
                if(lClientAdapter!=null){
                	lAdapterConnection = DBHelper.getInstance().getConnection();
                	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
                	lProcessInformationBean.setClientDataForProcessing(pFilter);
                	lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/courej",null, true, false);
                	pFilter = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
                }
            }
            List<Map> lList = (ArrayList<Map>) lJsonSlurper.parseText(updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Rejected));
            for(Map lMap : lList){
                lReturnMap.put("id", lMap.get("act"));
                lReturnMap.put("message", lMap.get("rem"));
            }
            logger.info(new JsonBuilder(lReturnMap).toString());
        }catch(Exception lMainException){
        	lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
        	lResponseStatus = ApiResponseStatus.Failed;
        	if (lClientAdapter!=null){
        		return TredsHelper.getInstance().returnErrorMessage(lMainException); 
        	}
        	return new JsonBuilder(lReturnMap).toString();
        }finally{
        	if(lClientAdapter!=null){
			 	//log the response to be sent
				lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lReturnMap).toString());
				lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/courej",lResponseStatus, false, false);
			}
			if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
				lAdapterConnection.close();
			}
        }
    	if(lClientAdapter!=null){
            PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
    	}
	    	lResponse = new JsonBuilder(lReturnMap).toString();
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
    @Secured(secKey="instcntr-return")
    @Path("/status/couret")
    public String updateStatusReturn(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	//conversion for IOCL
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        Map<String , Object> lReturnMap = new HashMap<String ,Object>();
        try{
        	if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()) && 
            		CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) ){
                lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lAppUserBean.getDomain());
                //if api user is using our apis then there will be no adpater
                if(lClientAdapter!=null){
                	lAdapterConnection = DBHelper.getInstance().getConnection();
                	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
                	lProcessInformationBean.setClientDataForProcessing(pFilter);
                	lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couret",null, true, false);
                	pFilter = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
                }
            }
            List<Map> lList = (ArrayList<Map>) lJsonSlurper.parseText(updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Returned));
            for(Map lMap : lList){
                lReturnMap.put("id", lMap.get("act"));
                lReturnMap.put("message", lMap.get("rem"));
            }
            logger.info(new JsonBuilder(lReturnMap).toString());
        }catch(Exception lMainException){
        	lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
        	lResponseStatus = ApiResponseStatus.Failed;
        	if (lClientAdapter!=null){
        		return TredsHelper.getInstance().returnErrorMessage(lMainException); 
        	}
        	return new JsonBuilder(lReturnMap).toString();
        }finally{
        	if(lClientAdapter!=null){
			 	//log the response to be sent
        		lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lReturnMap).toString());
        		lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couret",lResponseStatus, false, false);
			}
			if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
				lAdapterConnection.close();
			}
        }
		if(lClientAdapter!=null){
			PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
		}
		lResponse = new JsonBuilder(lReturnMap).toString();
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
    @Path("/status/couapp")
    public String updateStatusApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	//conversion for IOCL
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        Map<String , Object> lReturnMap = new HashMap<String ,Object>();
        //
        try{
        	if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()) && 
            		CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) ){
                lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lAppUserBean.getDomain());
                //if api user is using our apis then there will be no adpater
                if(lClientAdapter!=null){
                	lAdapterConnection = DBHelper.getInstance().getConnection();
                	lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
                	lProcessInformationBean.setClientDataForProcessing(pFilter);
                	lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couapp",null, true, false);
                	pFilter = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
                }
            }
            List<Map> lList = (ArrayList<Map>) lJsonSlurper.parseText(updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Approved));
            for(Map lMap : lList){
                lReturnMap.put("id", lMap.get("act"));
                lReturnMap.put("message", lMap.get("rem"));
            }
            logger.info(new JsonBuilder(lReturnMap).toString());
        }catch(Exception lMainException){
        	lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
        	lReturnMap.put("status", "FL");
        	lResponseStatus = ApiResponseStatus.Failed;
        	if (lClientAdapter!=null){
        		return TredsHelper.getInstance().returnErrorMessage(lMainException); 
        	}
	    	    return new JsonBuilder(lReturnMap).toString();
        }finally{
			if(lClientAdapter!=null){
			 	//log the response to be sent
			lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lReturnMap).toString());
			lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couapp",lResponseStatus, false, false);
			}
			if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
				lAdapterConnection.close();
			}
        }
		if(lClientAdapter!=null){
			PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
		}
			lResponse = new JsonBuilder(lReturnMap).toString();
		}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
  
    private String updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, 
            String pFilter, InstrumentBean.Status pStatus) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentBean lFilterBean = new InstrumentBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Map<String, Object>> lRetMsg = new ArrayList<Map<String,Object>>();
   	 	//
	   	 Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	   	 List<Long> lIdList = (List<Long>) lMap.get("idList");
	   	 if(lIdList!=null && !lIdList.isEmpty()){
	   		 //do nothing
	   	 }else{
	   		 lIdList = new ArrayList<Long>();
	   		 lIdList.add((Long)lMap.get("id"));
	   	 }
	   	 if (pStatus.equals(InstrumentBean.Status.Counter_Returned)) {
	   		if (!lMap.containsKey("statusRemarks") || StringUtils.isBlank(lMap.get("statusRemarks").toString())) {
		   		throw new CommonBusinessException("Remarks are mandatory.");
		   	}
	   	 }
	   	for(Long lId : lIdList ){
	   		instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
            lFilterBean.setStatus(pStatus);
            lFilterBean.setId(lId);
            try{
            	InstrumentBean lDbBean = instrumentBO.findBean(pExecutionContext.getConnection(), lFilterBean);
            	if (!lUserBean.getLocationIdList().isEmpty()) {
        			if (lUserBean.getDomain().equals(lDbBean.getPurchaser())) {
        				if(!lUserBean.getLocationIdList().contains(lDbBean.getPurClId())){
        					throw new CommonBusinessException("Access Denied on location.");
        				}
        			}else if (lUserBean.getDomain().equals(lDbBean.getSupplier())) {
        				if(!lUserBean.getLocationIdList().contains(lDbBean.getSupClId())) {
        					throw new CommonBusinessException("Access Denied on location.");
        				}
        			}
        		}
            	if ( Status.Counter_Approved.equals(lFilterBean.getStatus())) {
            		if ( Status.Checker_Approved.equals(lDbBean.getStatus()) || Status.Counter_Checker_Return.equals(lDbBean.getStatus())) {
            			List<MakerCheckerMapBean> lCheckers = null;
                		AppUserBO lAppUserBO = new AppUserBO();
                		lCheckers = lAppUserBO.getCheckers(pExecutionContext.getConnection(), lUserBean.getId(),MakerCheckerMapBean.CheckerType.InstrumentCounter);
                		if (lCheckers != null && lCheckers.size() > 0) {
                			lFilterBean.setStatus(InstrumentBean.Status.Counter_Checker_Pending);
                		}
            		}else {
            			throw new CommonBusinessException("Invalid instrument status.");
            		}
            	}
            	String lReturnMsg = instrumentBO.updateCounterStatus(pExecutionContext, lFilterBean, lUserBean,lFilterBean);
            	Map<String, Object> lRetMap = (Map<String, Object>)lJsonSlurper.parseText(lReturnMsg);
              	TredsHelper.getInstance().appendMessage(lRetMsg, lFilterBean.getId().toString(),lRetMap.get("message").toString(), lRetMap);
           }catch(Exception e){
        	   logger.info(e.getStackTrace());
        	   TredsHelper.getInstance().appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage(),null);
           }
	   	}
        return new JsonBuilder(lRetMsg).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status/precouapp")
    public String updateStatusPreApproval(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	    	JsonSlurper lJsonSlurper = new JsonSlurper();
	    	String lRetMsg = null;
	    	Map<String , Object> lReturnMap = new HashMap<String ,Object>();
	        Map<String,Object> lFilterMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
	        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        IClientAdapter lClientAdapter = null;
	        ProcessInformationBean lProcessInformationBean = null;
	        Connection lAdapterConnection = null;
	        ApiResponseStatus lResponseStatus = null;
	        Status lStatus = null;
	        //
        try{
	        	 if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
	             		CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
	        		lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
	         		if(lClientAdapter != null){
	         			lAdapterConnection = DBHelper.getInstance().getConnection();
	         			lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST_PRE,lAdapterConnection);
	         			lProcessInformationBean.setClientDataForProcessing(pFilter);
	         			lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/precouapp",null, true, false);
	         			pFilter = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
	         			lFilterMap = (Map<String, Object>)lProcessInformationBean.getProcessedTredsData();
	         		}
	             }
	             if(Status.Counter_Rejected.getCode().equals(lFilterMap.get("status"))){
	            	lStatus = Status.Counter_Rejected;
	             	lRetMsg = updateStatus(pExecutionContext, pRequest, pFilter,lStatus); 
	             }else if(Status.Counter_Returned.getCode().equals(lFilterMap.get("status"))){
	            	lStatus = Status.Counter_Returned;
	             	lRetMsg = updateStatus(pExecutionContext, pRequest, pFilter,lStatus); 
	             }else if(Status.Counter_Approved.getCode().equals(lFilterMap.get("status"))){
	                 lReturnMap.put("id", lFilterMap.get("id"));
	                 lReturnMap.put("message", "No action in pre-approve.");
	             }else{
	            	 throw new CommonBusinessException("Please send a valid Status.");
	             }
	             if(CommonUtilities.hasValue(lRetMsg) ){
	                 List<Map> lList = (ArrayList<Map>) lJsonSlurper.parseText(lRetMsg);
	                 for(Map lMap : lList){
	                     lReturnMap.put("id", lMap.get("act"));
	                     lReturnMap.put("message", lMap.get("rem"));
	                 }
	             }
	             logger.info(new JsonBuilder(lReturnMap).toString());
	        }catch(Exception lMainException){
	        	lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
	        	lResponseStatus = ApiResponseStatus.Failed;
	        	lReturnMap.put("status", "FL");
	        	if (lClientAdapter!=null){
	        		return TredsHelper.getInstance().returnErrorMessage(lMainException); 
	        	}
	        	return new JsonBuilder(lReturnMap).toString();
	        }finally{
				if(lClientAdapter!=null){
				 	//log the response to be sent
					lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lReturnMap).toString());
					lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/precouapp",lResponseStatus, false, false);
				}
				if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
					lAdapterConnection.close();
				}
	        }
	        if(lStatus!=null && (Status.Counter_Rejected.equals(lStatus) || Status.Counter_Returned.equals(lStatus)) ){
	            PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
	        }
	        lResponse = new JsonBuilder(lReturnMap).toString();
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
    @Path("/status/coumod")
    public String counterModification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
		JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String , Object> lReturnMap = new HashMap<String ,Object>();
        Map<String,Object> lFilterMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //
        try{
			if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
			 		CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
			}
        	instrumentBO.counterModification(pExecutionContext.getConnection(), lFilterMap, lUserBean);
            lReturnMap.put("id", lFilterMap.get("id"));
        	lReturnMap.put("status", "SUC");
            lReturnMap.put("message", "Counter data updated successfully.");
        }catch(Exception lMainException){
            lReturnMap.put("id", lFilterMap.get("id"));
        	lReturnMap.put("status", "FL");
        	lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
        }finally{
	        lResponse = new JsonBuilder(lReturnMap).toString();
			ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }

	    return lResponse;
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/instcreationkeys")
    public String instCreationKeys(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	try {
    		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
    		if(lAppEntityBean!=null){
    			AppEntityPreferenceBean lAEPrefBean = lAppEntityBean.getPreferences();
    			if(lAEPrefBean!=null&& !Yes.Yes.equals(lAEPrefBean.getIck())){
    				throw new CommonBusinessException("Entity does not support Instrument keys.");
    			}
    		}
	    	JsonSlurper lJsonSlurper = new JsonSlurper();
	        List<Map<String,Object>> lReturnMapList = new ArrayList<Map<String,Object>>();
	        List<Map<String,Object>> lList = (List<Map<String,Object>>) lJsonSlurper.parseText(pFilter);
	        if(lList != null){
	        	InstrumentCreationKeysBean lBean = null;
	        	InstrumentCreationKeysBO lInstrumentCreationKeysBO = new InstrumentCreationKeysBO();
	            for(Map<String,Object> lMap : lList){
	            	try {
		            	lBean = lInstrumentCreationKeysBO.saveInstCreationKeys(pExecutionContext.getConnection(), lMap , lUserBean);
	            	}catch(Exception lEx) {
	            		TredsHelper.getInstance().appendMessage(lReturnMapList, (lBean!=null?lBean.getKey():""), lEx.getMessage(), null);
	            	}
	            }
	            if(lReturnMapList.size() == 0) {
            		TredsHelper.getInstance().appendMessage(lReturnMapList, "", "Instrument Keys saved succesfully.", null);
	            }
	            lResponse = new JsonBuilder(lReturnMapList).toString();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/instcreationkeys/all")
    public String fetchInstCreationKeys(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	try {
    		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
    		if(lAppEntityBean!=null){
    			AppEntityPreferenceBean lAEPrefBean = lAppEntityBean.getPreferences();
    			if(lAEPrefBean!=null&& !Yes.Yes.equals(lAEPrefBean.getIck())){
    				throw new CommonBusinessException("Entity does not support Instrument keys.");
    			}
    		}
	        Map<String,Object> lReturnMap  = new HashMap<String,Object>();
	        List<Map<String,Object>> lUsedList = new ArrayList<Map<String,Object>>();
	        List<Map<String,Object>> lUnusedList = new ArrayList<Map<String,Object>>();
	        String lPurEntity = lUserBean.getDomain();
        	List<InstrumentCreationKeysBean> lBeans = null;
        	InstrumentCreationKeysBO lInstrumentCreationKeysBO = new InstrumentCreationKeysBO();
            GenericDAO<InstrumentCreationKeysBean> instrumentCreationKeysDAO = new GenericDAO<InstrumentCreationKeysBean>(InstrumentCreationKeysBean.class);
            try {
        		lBeans = lInstrumentCreationKeysBO.getInstCreationList(pExecutionContext.getConnection(), lPurEntity);
        		if(lBeans!=null && lBeans.size() > 0) {
        			for(InstrumentCreationKeysBean lBean : lBeans) {
        				if(lBean.getInId()!=null) {
        					lUsedList.add(instrumentCreationKeysDAO.getBeanMeta().formatAsMap(lBean, InstrumentCreationKeysBean.FIELDGROUP_INSTSTATUSUSEDLIST, null, false));
        				}else {
        					lUnusedList.add(instrumentCreationKeysDAO.getBeanMeta().formatAsMap(lBean,InstrumentCreationKeysBean.FIELDGROUP_INSTSTATUSUNUSEDLIST, null, false));
        				}
        			}
        		}
        		lReturnMap.put("usedKeys", lUsedList);
        		lReturnMap.put("unUsedKeys", lUnusedList);
        	}catch(Exception lEx) {
        	}
            lResponse = new JsonBuilder(lReturnMap).toString();
    	}catch(Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}finally {
			//ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
		}
	    return lResponse;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-view")
    @Path("/expall")
    public String listExpired(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	if(StringUtils.isEmpty(pFilter)){
    		pFilter = "{}";
    	}
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
       	lFilterBean.setStatus(Status.Expired);
       	if (!lMap.containsKey("fromDate") || StringUtils.isEmpty(lMap.get("fromDate").toString() )) {
       		throw new CommonBusinessException(" from Date is mandatory");
       	}
       	if (!lMap.containsKey("toDate") || StringUtils.isEmpty(lMap.get("toDate").toString() )) {
       		throw new CommonBusinessException(" from Date is mandatory");
       	}
       	lFilterBean.setFromDate(FormatHelper.getDate(lMap.get("fromDate").toString(), AppConstants.DATE_FORMAT));
       	lFilterBean.setToDate(FormatHelper.getDate(lMap.get("toDate").toString(), AppConstants.DATE_FORMAT));
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounter(pExecutionContext, lFilterBean, null, lUserBean, Boolean.TRUE, -1,Boolean.TRUE);
        List<Object> lResults = new ArrayList<Object>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            lResults.add(instrumentBeanMeta.formatAsMap(lInstrumentBean, null, lFields, false));            
        }
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.error("Error in listExpired : ", e);
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
}