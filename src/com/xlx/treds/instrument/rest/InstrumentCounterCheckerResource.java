package com.xlx.treds.instrument.rest;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/instcntrchk")
public class InstrumentCounterCheckerResource {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentCounterCheckerResource.class);

    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public InstrumentCounterCheckerResource() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime"});
        lovFields = Arrays.asList(new String[]{"id","id"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void pageChecker(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/instcntrchk.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntrchk-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        InstrumentBean lFilterBean = new InstrumentBean();
        lFilterBean.setId(pId);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setCounterEntity(lUserBean.getDomain());
        if (TredsHelper.getInstance().checkOwnership(lUserBean) ) 
            lFilterBean.setCounterCheckerAuId(TredsHelper.getInstance().getOwnerAuId(lUserBean));
        InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        return instrumentBeanMeta.formatAsJson(lInstrumentBean);//, null, defaultListFields, false);
    }
        
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntrchk-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounterChecking(pExecutionContext, lFilterBean, null, lUserBean , lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, -1,null);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForCounterChecker(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            lResults.add(instrumentBeanMeta.formatAsArray(lInstrumentBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey="instcntrchk-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounterChecking(pExecutionContext, lFilterBean, null, lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, AppConstants.RECORDS_DOWNLOAD_ALL,null);
        // filter out records based on tab
        Long lTab = null;
        if(lMap.get("tab")!=null){
            lTab = Long.valueOf(((Number)lMap.get("tab")).longValue());
        }
        List<InstrumentBean> lFilteredList = new ArrayList<InstrumentBean>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForCounterChecker(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            if (lTab.equals(lInstrumentBean.getTab()))
            	lFilteredList.add(lInstrumentBean);
        }
        String lFileName = "InstrumentsCounterChecking.csv";
        if(lFilterBean.getGroupInId()!=null){
        	lFileName = "GrpInstruments"+lFilterBean.getGroupInId()+".csv";
        }
    	return new FileDownloadBean(lFileName, 
        		instrumentBeanMeta.formatBeansAsCsv(lFilteredList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntrchk-view")
    @Path("/status/couapp")
    public String updateStatusApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	return updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Approved);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntrchk-view")
    @Path("/status/couchkret")
    public String updateStatusReturn(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentBean lFilterBean = new InstrumentBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Map<String, Object>> lRetList = new ArrayList<Map<String,Object>>();
        instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
        lFilterBean.setStatus(InstrumentBean.Status.Counter_Checker_Return);
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	   	List<Long> lIdList = (List<Long>) lMap.get("idList");
        if(lIdList != null){
            for(Long lId : lIdList){
            	instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
            	lFilterBean.setId(lId);
                lFilterBean.setStatus(InstrumentBean.Status.Counter_Checker_Return);
                List<Map<String, Object>> lReturnMap = instrumentBO.UpdateCounterCheckerStatus(pExecutionContext, lFilterBean, lUserBean);
            	lRetList.addAll(lReturnMap);
            }
        }
    	return new JsonBuilder(lRetList).toString();
    }

    private String updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, 
            String pFilter, InstrumentBean.Status pStatus) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
   	 	JsonSlurper lJsonSlurper = new JsonSlurper();
        InstrumentBean lFilterBean = new InstrumentBean();
        List<Map<String, Object>> lRetMsg = new ArrayList<Map<String,Object>>();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	   	 List<Long> lIdList = (List<Long>) lMap.get("idList");
	   	 if(lIdList!=null && !lIdList.isEmpty()){
	   		 //do nothing
	   	 }else{
	   		 lIdList = new ArrayList<Long>();
	   		 lIdList.add((Long)lMap.get("id"));
	   	 }
	   	 Map<String, String> lDataHash = new HashMap<String,String>();
	     if (Status.Counter_Approved.equals(pStatus)){
	    	lDataHash.put("action", "instrument approval");
	     }else if (Status.Counter_Checker_Return.equals(pStatus)){
	    	 lDataHash.put("action", "instrument return");
	     }else if (Status.Counter_Checker_Pending.equals(pStatus)){
		    	lDataHash.put("action", "instrument approval");
	     }
         if (!lDataHash.isEmpty()){
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_INSTCHKAPPROVAL, AppConstants.TEMPLATE_PREFIX_INSTCHKAPPROVAL, lDataHash);        	
         }
    	 for(Long lId : lIdList ){
    		 instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
             lFilterBean.setStatus(pStatus);
             lFilterBean.setId(lId);
                 if (Status.Counter_Approved.equals(pStatus)) {
                    	InstrumentBean lInstBean= instrumentBO.findBean(pExecutionContext, lFilterBean);
                    	lInstBean.populateNonDatabaseFields();
                    	lInstBean.setStatus(pStatus);
                    	lInstBean.setStatusRemarks(lFilterBean.getStatusRemarks());
                    	lFilterBean = lInstBean;
                 }
                 try{
                 	String lReturnMsg = instrumentBO.updateCounterStatus(pExecutionContext, lFilterBean, lUserBean,null);
                 	 Map<String, Object> lRetMap = (Map<String, Object>)lJsonSlurper.parseText(lReturnMsg);
                   	 EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), lRetMap.get("message").toString());
                }catch(Exception e){
                   	 EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage());
                }
    	 }
 	//
    return new JsonBuilder(lRetMsg).toString();
    }
}