package com.xlx.treds.instrument.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AggregatorLogger;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/inst")
public class InstrumentResourceApiV1 {
	private static Logger logger = Logger.getLogger(InstrumentResourceApiV1.class);

    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
	private List<String> defaultListFields, lovFields;
	private AppUserBO appUserBO;
	private GenericDAO<InstrumentBean> instrumentDAO;

    public InstrumentResourceApiV1() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime","instNumber"});
        lovFields = Arrays.asList(new String[]{"id","id"});
        appUserBO = new AppUserBO();
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-view")
    @Path("/get")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = null;
        List<String> lFields = null;
        boolean lDescriptive = false;
        Long lInstId = null;
        if(CommonUtilities.hasValue(pFilter)){
            lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
            if (lMap.containsKey("id")){
       		 lInstId = Long.valueOf(((Number)lMap.get("id")).longValue());
            }else{
            	throw new CommonBusinessException("Please send a valid instrument Id.");
            }
            if(lMap.containsKey(BeanMeta.PARAM_COLUMNNAMES)){
                lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
                if (lFields == null) lFields = defaultListFields;
            }
            if(lMap.containsKey("descriptive")){
                lDescriptive = (boolean)lMap.get("descriptive");
            }
        }
    	InstrumentBean lFilterBean = new InstrumentBean();
        lFilterBean.setId(lInstId);
        InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        lInstrumentBean.populateNonDatabaseFields();
        
        lResponse =  instrumentBeanMeta.formatAsJson(lInstrumentBean,null,lFields,lDescriptive);
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
    @Secured(secKey="inst-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListMaker(pExecutionContext, lFilterBean, null, 
        		lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, -1);
        List<Object> lResults = new ArrayList<Object>();
        Map<String,Object> lTmpMap = null;
        Map<String, Object> lCustomFieldMap = null ;
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForMaker(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            lTmpMap = instrumentBeanMeta.formatAsMap(lInstrumentBean, null, lFields, false);
            if (lInstrumentBean.getCfId()!=null && lInstrumentBean.getCfData()!=null) {
            	 lCustomFieldMap = (Map<String, Object>)lJsonSlurper.parseText(lInstrumentBean.getCfData());
            	 if (lCustomFieldMap!=null) {
            		 lTmpMap.putAll(lCustomFieldMap);
            	 }
            }
            lResults.add(lTmpMap);            
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/duedate")
    public String getDueDates(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, pMessage, InstrumentBean.FIELDGROUP_DUEDATEREQUEST, null);
        instrumentBO.setUpdatedDueDates(lFilterBean);
        return instrumentBeanMeta.formatAsJson(lFilterBean, InstrumentBean.FIELDGROUP_DUEDATERESPONSE, null, false);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    public String insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    		lResponse = save(pExecutionContext, pRequest, pMessage, true);
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
    @Secured(secKey="inst-save")
    public String update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	int lRequestId =  ApiLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    		lResponse = save(pExecutionContext, pRequest, pMessage, false);
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    private String save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentBean lInstrumentBean = new InstrumentBean();
        List<ValidationFailBean> lValidationFailBeans = null;
        //this
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        Map<String, Object> lDiffMap = getExtraPayloadRecd(instrumentDAO,lMap);
        lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, 
                pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT+"Api" : BeanMeta.FIELDGROUP_UPDATE+"Api", null);
//        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
//            throw new CommonValidationException(lValidationFailBeans);
        if (!pNew) {
        	InstrumentBean lDbInstrumentBean = instrumentBO.findBean(pExecutionContext.getConnection(), lInstrumentBean);
        	if (lDbInstrumentBean!=null) {
        		List<String> lFields = new ArrayList<String>();
        		for (String lKey:lMap.keySet()) {
        			lFields.add(lKey);
        		}
        		instrumentBeanMeta.copyBean(lInstrumentBean, lDbInstrumentBean, null,lFields);
        		lInstrumentBean = lDbInstrumentBean;
        		instrumentBO.updateNetAmount(lInstrumentBean, false, true, true);
        	}
        }
        
        boolean lSubmit = false;
        String lRemarks = null;
        if(lMap.containsKey("status")){
        	String lStatusStr = (String) lMap.get("status");
        	lRemarks = (String) lMap.get("statusRemarks");
        	lSubmit = InstrumentBean.Status.Submitted.getCode().equals(lStatusStr);
        }
//        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
//        if (lInstrumentBean.getPurchaser()==null && lAppEntityBean.isPurchaser()){
//        	lInstrumentBean.setPurchaser(lUserBean.getDomain());
//        }else if(lInstrumentBean.getSupplier()==null && lAppEntityBean.isSupplier()){
//        	lInstrumentBean.setSupplier(lUserBean.getDomain());
//        }
        
        instrumentBO.save(pExecutionContext, lInstrumentBean, lUserBean, pNew, false, lSubmit, lRemarks,true,null,lDiffMap);
        pExecutionContext.dispose();
        return instrumentBeanMeta.formatAsJson(lInstrumentBean, InstrumentBean.FIELDGROUP_APIRESPONSE, null, false);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-delete")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentBean lFilterBean = new InstrumentBean();
        lFilterBean.setId(pId);
        instrumentBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-status")
    @Path("/status")
    public String updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //Domain Check?
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
        
        Map<String, String> lDataHash = new HashMap<String,String>();
        if( Status.Submitted.equals(lFilterBean.getStatus())){
        	lDataHash.put("action", "instrument submition");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_INSTSUBMIT, AppConstants.TEMPLATE_PREFIX_INSTSUBMIT, lDataHash);
        }
        List<Map<String, Object>> lList = instrumentBO.updateMakerStatus(pExecutionContext, lFilterBean, lUserBean);
	        lResponse = new JsonBuilder(lList).toString();
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
    @Secured
    @Path("/getCheckers")
    public String getCheckers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, Object> lResults = new HashMap<String, Object>();
       java.sql.Connection lConnection = pExecutionContext.getConnection();
       List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, lUserBean.getId(), MakerCheckerMapBean.CheckerType.Instrument);
       lResults.put("hasCheckers", (lCheckers!=null && lCheckers.size()>0));            
       lResults.put("checkers", lCheckers);            
        return new JsonBuilder(lResults).toString();
    }

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-delete")
    @Path("/remove")
    public String removeInstruments(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest); 
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Map<String, Object>> lRetMsg = new ArrayList<Map<String,Object>>();
    	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	   	 List<Long> lIdList = (List<Long>) lMap.get("idList");
	   	 if(lIdList!=null && !lIdList.isEmpty()){
	   		InstrumentBean lFilterBean = null;
	   		 for(Long id : lIdList){
	   			lFilterBean = new InstrumentBean();
		        lFilterBean.setId(id);
		        try{
			        instrumentBO.delete(pExecutionContext, lFilterBean, lUserBean);
			      	EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), "Success");
	           }catch(Exception e){
	              	EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage());
	           }
	   		 }
	   	 }
	   	 return new JsonBuilder(lRetMsg).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    @Path("/invoicegroup")
    public String addGroupedInvoice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
        if(StringUtils.isEmpty(pMessage)){
        	throw new CommonBusinessException("No Data received.");
        }
    	try{
            AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
            Map<String, Object> lMap = parseJson(pMessage);
            lResponse = instrumentBO.createGroupInstrument(pExecutionContext,lMap,lUserBean, false);
            pExecutionContext.getConnection().commit();
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally {
    		AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    public static Map<String, Object>  parseJson(String pMessage) throws Exception{
    	try{
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
            return lMap;
    	}catch(Exception lEx){
    		throw new CommonBusinessException("Please check the payload.");
    	}
    }
    
    private Map<String,Object> getExtraPayloadRecd(GenericDAO pGenericDAO,  Map<String,Object> pPayLoad) {
        if(pPayLoad!=null&&pPayLoad.size() > 0) {
            List<BeanFieldMeta> lFieldsMeta = pGenericDAO.getBeanMeta().getFieldMetaList(null, null);
            Map<String,Object> lDiffHash = new HashMap<String,Object>();
            Set<String> lBeanFields = new HashSet<String>();
            //
            for(BeanFieldMeta lFieldMeta : lFieldsMeta) {
                lBeanFields.add(lFieldMeta.getName());
            }
            for(String lKey: pPayLoad.keySet()) {
                if(!lBeanFields.contains(lKey)) {
                    lDiffHash.put(lKey, pPayLoad.get(lKey));
                }
            }
            if (lDiffHash.size() > 0) {
                return lDiffHash;
            }
        }
        return null;
    } 
}