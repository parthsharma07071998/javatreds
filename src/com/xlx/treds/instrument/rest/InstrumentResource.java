package com.xlx.treds.instrument.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
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
@Path("/inst")
public class InstrumentResource {

	private static Logger logger = Logger.getLogger(InstrumentResource.class);
	
    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<InstrumentBean> instrumentDAO;

    public InstrumentResource() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime"});
        lovFields = Arrays.asList(new String[]{"id","id"});
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
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
        pRequest.getRequestDispatcher("/WEB-INF/inst.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        InstrumentBean lFilterBean = new InstrumentBean();
        lFilterBean.setId(pId);
        InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        lInstrumentBean.populateNonDatabaseFields();
        return instrumentBeanMeta.formatAsJson(lInstrumentBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-view")
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
        List<InstrumentBean> lInstrumentList = instrumentBO.findListMaker(pExecutionContext, lFilterBean, null, 
        		lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, -1);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForMaker(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            lResults.add(instrumentBeanMeta.formatAsArray(lInstrumentBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }


    @POST
    @Secured(secKey="inst-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = new ArrayList<String>((List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES));
        if (lFields == null || lFields.isEmpty()) lFields = defaultListFields;
        List<InstrumentBean> lInstrumentList = instrumentBO.findListMaker(pExecutionContext, lFilterBean, null, 
        		lUserBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE, AppConstants.RECORDS_DOWNLOAD_ALL);
        // filter out records based on tab
        Long lTab = null;
        if(lMap.get("tab")!=null){
            lTab = Long.valueOf(((Number)lMap.get("tab")).longValue());
        }
        List<InstrumentBean> lFilteredList = new ArrayList<InstrumentBean>();
        for (InstrumentBean lInstrumentBean : lInstrumentList) {
            instrumentBO.setTabForMaker(lInstrumentBean, lFilterBean.getFiltHistFlag()==CommonAppConstants.YesNo.Yes?Boolean.TRUE:Boolean.FALSE);
            if (lTab == null || lTab.equals(lInstrumentBean.getTab()))
            	lFilteredList.add(lInstrumentBean);
        }
        String lFileName = "instruments.csv";
	    if(lFilterBean.getGroupInId()!=null){
	    	lFileName = "GrpInstruments"+lFilterBean.getGroupInId()+".csv";
	    }
    	return new FileDownloadBean(lFileName, 
        		instrumentBeanMeta.formatBeansAsCsv(lFilteredList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/duedate")
    public String getDueDates(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, pMessage, InstrumentBean.FIELDGROUP_DUEDATEREQUEST, null);
        instrumentBO.setUpdatedDueDates(lFilterBean);
        String response = instrumentBeanMeta.formatAsJson(lFilterBean, InstrumentBean.FIELDGROUP_DUEDATERESPONSE, null, true);
        System.out.println("response -- " + response);
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentBean lInstrumentBean = new InstrumentBean();
        Connection lConnection = pExecutionContext.getConnection();
        Connection lAdapterConnection = DBHelper.getInstance().getConnection();
        List<ValidationFailBean> lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : InstrumentBean.FIELDGROUP_UPDATESAVE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        //this
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        if (lMap.containsKey("instrumentCreationKeysList")){
        	lInstrumentBean.setInstrumentCreationKeysList((List<String>) lMap.get("instrumentCreationKeysList"));
        }
        Map<String, Object> lDiffMap = getExtraPayloadRecd(instrumentDAO,lMap);
        boolean lSubmit = false;
        String lRemarks = null;
        if(lMap.containsKey("status")){
        	String lStatusStr = (String) lMap.get("status");
        	lRemarks = (String) lMap.get("statusRemarks");
        	lSubmit = InstrumentBean.Status.Submitted.getCode().equals(lStatusStr);
        }
        if(lSubmit){
        	Map<String, String> lDataHash = new HashMap<String,String>();
        	lDataHash.put("action", "instrument submition");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, lConnection, AppConstants.OTP_NOTIFY_TYPE_INSTSUBMIT, AppConstants.TEMPLATE_PREFIX_INSTSUBMIT, lDataHash);        	
        }
        String lLoginKey = null;
        lLoginKey = AuthenticationHandler.getInstance().getLoginKey(pRequest);
        instrumentBO.save(pExecutionContext, lInstrumentBean, lUserBean, pNew, false, lSubmit, lRemarks,false,lLoginKey,lDiffMap);
        if (lSubmit){
        	try{
        		IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstrumentBean.getPurchaser());
        		if(lClientAdapter != null){
        			ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST,lAdapterConnection);
        			lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
        			lProcessInformationBean.setEntityCode(lInstrumentBean.getPurchaser());
        			lClientAdapter.sendResponseToClient(lProcessInformationBean);
        		}
        	}catch(Exception lEx){
        		logger.info("Error : " + lEx.getMessage());
        	}finally{
        		if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
    				lAdapterConnection.close();
    			}
        	}
        }        
        pExecutionContext.dispose();
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
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Connection lAdapterConnection = DBHelper.getInstance().getConnection();
        //Domain Check?
        InstrumentBean lFilterBean = new InstrumentBean();
        instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        List<Long> lTmpList = new ArrayList<Long>();
        if(StringUtils.isNotEmpty(lMap.get("ids").toString())){
        	String[] lTmpList1 = CommonUtilities.splitString(lMap.get("ids").toString(), ",");
        	for(int lPtr=0; lPtr < lTmpList1.length; lPtr++){
        		lTmpList.add(new Long(lTmpList1[lPtr]));
        	}
        }
        Map<String, String> lDataHash = new HashMap<String,String>();
        if(!lTmpList.isEmpty() && Status.Submitted.equals(lFilterBean.getStatus())){
        	lDataHash.put("action", "instrument submition");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_INSTSUBMIT, AppConstants.TEMPLATE_PREFIX_INSTSUBMIT, lDataHash);
        }
        List<Map<String, Object>> lList = new ArrayList<Map<String,Object>>();
        for(Long lId : lTmpList) {
        	lFilterBean.setId(lId);
        	try {
        		lList.addAll(instrumentBO.updateMakerStatus(pExecutionContext, lFilterBean, lUserBean));
        	}catch (Exception e) {
        		List<Map<String, Object>> pMessages = new ArrayList<Map<String, Object>>();
        		Map<String, Object> lErrMap = new HashMap<>();
        		lErrMap.put("error", true);
        		lErrMap.put("act", lFilterBean.getId());
        		lErrMap.put("rem", e.getMessage());
        		pMessages.add(lErrMap);
        		lList.addAll(pMessages);
			}
        	InstrumentBean lInstrumentBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
        	if (Status.Checker_Approved.equals(lInstrumentBean.getStatus())){
            	try{
            		IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstrumentBean.getPurchaser());
            		if(lClientAdapter != null){
            			ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST,lAdapterConnection);
            			lInstrumentBean.populateNonDatabaseFields();
            			lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
            			lClientAdapter.sendResponseToClient(lProcessInformationBean);
            		}
            	}catch(Exception lEx){
            		logger.info("Error : " + lEx.getMessage());
            	}
            }
            pExecutionContext.dispose();
        }
        if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
			lAdapterConnection.close();
		}
		return new JsonBuilder(lList).toString();
    }
    
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured(secKey="inst-save")
    @Path("/instupload")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse, @QueryParam("type") String pType) throws Exception {
		if (pType!=null) {
			pRequest.getRequestDispatcher("/WEB-INF/instuplxls.jsp").forward(pRequest, pResponse);
		}else{
			pRequest.getRequestDispatcher("/WEB-INF/instupl.jsp").forward(pRequest, pResponse);
	    }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/getCheckers")
    public String getCheckers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, Object> lResults = new HashMap<String, Object>();
       java.sql.Connection lConnection = pExecutionContext.getConnection();
       AppUserBO lAppUserBO = new AppUserBO();
       List<MakerCheckerMapBean> lCheckers = lAppUserBO.getCheckers(lConnection, lUserBean.getId(), MakerCheckerMapBean.CheckerType.Instrument);
       lResults.put("hasCheckers", (lCheckers!=null && lCheckers.size()>0));            
       lResults.put("checkers", lCheckers);            
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    @Path("/addgroup")
    public String groupAdd(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Long> lInIds = (List<Long>)lJsonSlurper.parseText(pFilter);
        List<Map<String, Object>> lList = instrumentBO.createGroups(lConnection, lInIds, lUserBean,false);
        return new JsonBuilder(lList).toString();   
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
    @Path("/ungroup")
    public String ungroup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Long> lInIds = (List<Long>)lJsonSlurper.parseText(pFilter);
        List<Map<String, Object>> lList =  instrumentBO.unGroup(lConnection, lInIds, lUserBean);
        return new JsonBuilder(lList).toString();   
   }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/viewclubbeddetails/{id}")
    public String getdetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return new JsonBuilder(instrumentBO.findJsonForClubbedInstruments(lConnection, pId, lUserBean)).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="inst-save")
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
            instrumentBO.removeFromGroup(pExecutionContext.getConnection(), pParentId,  lTmpList, lUserBean);
        }
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