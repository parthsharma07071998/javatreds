package com.xlx.treds.instrument.rest;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/instcntr")
public class InstrumentCounterResource {

    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
	private List<String> defaultListFields, editFields;
	
    public InstrumentCounterResource() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime","counterModifiedFields"});
        editFields = Arrays.asList(new String[]{"id","autoAccept","costBearingType","chargeBearer","settleLeg3Flag", "purchaser","supplier"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void pageChecker(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/instcntr.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        InstrumentBean lFilterBean = new InstrumentBean();
        lFilterBean.setId(pId);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setCounterEntity(lUserBean.getDomain());
        if (TredsHelper.getInstance().checkOwnership(lUserBean) ) 
            lFilterBean.setCounterAuId(TredsHelper.getInstance().getOwnerAuId(lUserBean));
        InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        lInstrumentBean.populateNonDatabaseFields();
        instrumentBO.updateFieldsFromCounterUpdate(lInstrumentBean, lUserBean);
        return instrumentBeanMeta.formatAsJson(lInstrumentBean);//, null, defaultListFields, false);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-view")
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
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounter(pExecutionContext, lFilterBean, null, lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, -1,Boolean.FALSE);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForCounter(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            lResults.add(instrumentBeanMeta.formatAsArray(lInstrumentBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }
    


    @POST
    @Secured(secKey="instcntr-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = (AppEntityBean) TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListCounter(pExecutionContext, lFilterBean, null, lUserBean,lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, AppConstants.RECORDS_DOWNLOAD_ALL,Boolean.FALSE);
        // filter out records based on tab
        Long lTab = null;
        if(lMap.get("tab")!=null){
            lTab = Long.valueOf(((Number)lMap.get("tab")).longValue());
        }
        List<InstrumentBean> lFilteredList = new ArrayList<InstrumentBean>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
        	instrumentBO.setTabForCounter(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            if (lTab==null || lTab.equals(lInstrumentBean.getTab()))
            	lFilteredList.add(lInstrumentBean);
        }
        String lFileName = "InstrumentsCounting.csv";
	    if(lFilterBean.getGroupInId()!=null){
	    	lFileName = "GrpInstruments"+lFilterBean.getGroupInId()+".csv";
	    }
    	return new FileDownloadBean(lFileName, 
        		instrumentBeanMeta.formatBeansAsCsv(lFilteredList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }

    

    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-approve")
    public String update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, String> lDataHash = new HashMap<String,String>();
        InstrumentBean lInstrumentBean = new InstrumentBean();
        List<ValidationFailBean> lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, 
            pMessage, "fetchCounter", null);
        if(!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
        	if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)){
            	if(!(lValidationFailBeans.size()==1 && lValidationFailBeans.get(0).getName().equalsIgnoreCase("counterModifiedFields"))){
                    throw new CommonValidationException(lValidationFailBeans);
            	}
            }
        }
        if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus())){
        	lDataHash.put("action", "instrument approval");
        }
        if (!lDataHash.isEmpty()){
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_INSTCNTRAPPROVAL, AppConstants.TEMPLATE_PREFIX_INSTCNTRAPPROVAL, lDataHash);        	
        }
        return instrumentBO.updateCounterStatus(pExecutionContext, lInstrumentBean, lUserBean,null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-reject")
    @Path("/status/courej")
    public String updateStatusReject(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        return updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Rejected);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-return")
    @Path("/status/couchkpen")
    public String updateStatusApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        return updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Checker_Pending);
    } 
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-return")
    @Path("/status/couret")
    public String updateStatusReturn(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        return updateStatus(pExecutionContext, pRequest, pFilter, InstrumentBean.Status.Counter_Returned);
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
	   	 Map<String, String> lDataHash = new HashMap<String,String>();
         if (Status.Counter_Rejected.equals(pStatus)){
        	lDataHash.put("action", "instrument rejection");
         }else if (Status.Counter_Returned.equals(pStatus)){
        	 lDataHash.put("action", "instrument return");
         }else if (Status.Counter_Checker_Pending.equals(pStatus)){
         	lDataHash.put("action", "instrument approval");
         }
         if (!lDataHash.isEmpty()){
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_INSTCHKAPPROVAL, AppConstants.TEMPLATE_PREFIX_INSTCNTRAPPROVAL, lDataHash);        	
         }
	   	for(Long lId : lIdList ){
   		 instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
            lFilterBean.setStatus(pStatus);
            lFilterBean.setId(lId);
            if (Status.Counter_Checker_Pending.equals(pStatus)) {
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
        return new JsonBuilder(lRetMsg).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-approve")
    @Path("/addgroup")
    public String groupAdd(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Long> lInIds = (List<Long>)lJsonSlurper.parseText(pFilter);
        List<Map<String, Object>> lList = instrumentBO.createGroups(lConnection, lInIds, lUserBean,true);
        return new JsonBuilder(lList).toString();   
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="instcntr-approve")
    @Path("/removefromgroup/{parentId}/{ids}")
    public void removeFromGroup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
    	@PathParam("ids") String pIds, @PathParam("parentId") Long pParentId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<Long> lTmpList = new ArrayList<Long>();
        if(StringUtils.isNotEmpty(pIds)){
        	String[] lTmpList1 = CommonUtilities.splitString(pIds, ",");
        	for(int lPtr=0; lPtr < lTmpList1.length; lPtr++){
        		lTmpList.add(new Long(lTmpList1[lPtr]));
        	}
        }
        //lTmpList.add(pId);
        instrumentBO.removeFromGroup(pExecutionContext.getConnection(), pParentId,  lTmpList, lUserBean);
    }
    
    @POST
    @Secured(secKey="instcntr-approve")
    @Path("/downloadbulk")
    public Object downloadCsv(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
    	String pFilter) throws Exception {
    	String lData = null;
    	String lFileName = "InstrumentBulk.csv";
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        List<Long> lTmpList = new ArrayList<Long>();
        if(StringUtils.isNotEmpty(lMap.get("ids").toString())){
        	ArrayList<Object> lList = instrumentBO.getInstrumentData(pExecutionContext.getConnection(), lMap.get("ids").toString());
        	if (!lList.isEmpty()) {
        		String lFieldGroup = null;
        		if(lEntityBean.isPurchaser()) {
        			lFieldGroup = InstrumentBean.FIELDGROUP_PURCHASER_BULK;
        		}else if (lEntityBean.isSupplier()) {
        			lFieldGroup = InstrumentBean.FIELDGROUP_SUPPLIER_BULK; 
        		}
        		lData = TredsHelper.getInstance().getCsv(pExecutionContext.getConnection(), instrumentBeanMeta, lFieldGroup, null, lList, lUserBean);
        	}
        }else {
        	throw new CommonBusinessException("ERR");
        }
    	return new FileDownloadBean(lFileName, lData.getBytes(), null).getResponseForSendFile();
    }
}