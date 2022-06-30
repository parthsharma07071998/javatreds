package com.xlx.treds.instrument.rest;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AggregatorLogger;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PaymentAdviceBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bo.PurchaserAggregatorBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.AggregatorInstrumentBO;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.other.bean.CashInvoicePaymentAdviceGenerator;
import com.xlx.treds.other.bean.GEMInvoiceBean;
import com.xlx.treds.other.bean.GEMInvoiceResendBean;
import com.xlx.treds.other.bean.GEMInvoiceResendBean.Status;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1")
public class AggregatorInstrumentResourceApiV1 {
	private static Logger logger = Logger.getLogger(AggregatorInstrumentResourceApiV1.class);

	private AggregatorInstrumentBO aggregatorInstrumentBO;
	private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
    private BeanMeta factoringUnitBeanMeta;
    private BeanMeta obligationBeanMeta;
    private BeanMeta obligationSplitBeanMeta;
	private List<String> defaultListFields, lovFields, gemsFieldsList;
	private GenericDAO<AdapterRequestResponseBean> adapterRequestResponseDAO;
	private GenericDAO<GEMInvoiceBean> gemInvoiceDAO;
	private GenericDAO<GEMInvoiceResendBean> gemInvoiceResendDAO;
	private	PurchaserAggregatorBO purchaserAggregatorBO;


	public static String FIELDGROUP_AGGFACTUNIT = "aggFactUnit";
	public static String FIELDGROUP_AGGPARENTOBLI = "aggParentObli";
	public static String FIELDGROUP_AGGCHILDOBLI = "aggChildObli";
	public static String FIELDGROUP_GEMSADDINVOICE = "gemsAddinvoice";

	
    public AggregatorInstrumentResourceApiV1() {
        super();
        aggregatorInstrumentBO = new AggregatorInstrumentBO();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
        obligationSplitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationSplitsBean.class);
        adapterRequestResponseDAO = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class);
        gemInvoiceDAO = new GenericDAO<GEMInvoiceBean>(GEMInvoiceBean.class);
        gemInvoiceResendDAO = new GenericDAO<GEMInvoiceResendBean>(GEMInvoiceResendBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime"});
        lovFields = Arrays.asList(new String[]{"id","id"});
        gemsFieldsList = Arrays.asList(new String[]{"gemSenderId", "gemMesssageId","gemUniqueRequestIdentifier","gemCreatedDate","gemSupplier","gemSupplierId","gemSupEmail","gemPurMinistryState","gemPurDept","gemOrg","gemPurEmail"});
        purchaserAggregatorBO = new PurchaserAggregatorBO();
    }

  
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instci-save")
    @Path("/agginvoicegroup")
    //ONLY FOR CASH INVOICE 
    public String addGroupedInvoice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        if(StringUtils.isEmpty(pMessage)){
        	throw new CommonBusinessException("No Data received.");
        }
    	try{
            AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		    if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
		                    CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
		        lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
		        //if api user is using our apis then there will be no adpater
		        if(lClientAdapter!=null){
		                lAdapterConnection = DBHelper.getInstance().getConnection();
		                lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
		                lProcessInformationBean.setClientDataForProcessing(pMessage);
		                lClientAdapter.logInComing(lProcessInformationBean, "/v1/agginvoicegroup",null, true, false);

		        }
		    }
            Map<String, Object> lMap = parseJson(pMessage);
            lResponse = instrumentBO.createGroupInstrument(pExecutionContext,lMap,lUserBean,false);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in addGroupedInvoice : ", e);;
    		throw e;
    	}finally {
    		AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    		try {
        		if(lAdapterConnection!=null) {
        			lAdapterConnection.close();
        		}		
    		}catch(Exception lEx) {
    			logger.error("Error in addGroupedInvoice adapter conn : ", lEx);
    		}
    	}
        return lResponse;
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instci-save")
    @Path("/os/agginvoicegroup")
    //ONLY FOR CASH INVOICE - outstanding invoices
    public String addOutstandingGroupedInvoice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        if(StringUtils.isEmpty(pMessage)){
        	throw new CommonBusinessException("No Data received.");
        }
    	try{
            AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		    if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
		                    CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
		        lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
		        //if api user is using our apis then there will be no adpater
		        if(lClientAdapter!=null){
		                lAdapterConnection = DBHelper.getInstance().getConnection();
		                lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
		                lProcessInformationBean.setClientDataForProcessing(pMessage);
		                lClientAdapter.logInComing(lProcessInformationBean, "/v1/out/agginvoicegroup",null, true, false);
		        }
		    }
            Map<String, Object> lMap = parseJson(pMessage);
            lResponse = instrumentBO.createGroupInstrument(pExecutionContext,lMap,lUserBean, true);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator add outstanding group invoice : ", e);
    		throw e;
    	}finally {
    		AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    		try {
        		if(lAdapterConnection!=null) {
    		lAdapterConnection.close();
        		}		
    		}catch(Exception lEx) {
    			logger.error("Error in addGroupedInvoice adapter conn : ", lEx);
    		}
    	}
        return lResponse;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="fuci-status")
    @Path("/aggfustatus")
    //ONLY FOR CASH INVOICE 
    public String getGroupedInstFUStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	try{
            Map<String, Object> lMap =  parseJson(pMessage);
            List<Long> lFuIds = (List<Long>) lMap.get("fuIds");
            List<String> lFuColumns = null, lObliColumns = null, lObliSplitColumns = null;
            String lFuFieldGrp = FIELDGROUP_AGGFACTUNIT, lObliFieldGrp = FIELDGROUP_AGGPARENTOBLI, lObliSplitFieldGrp = FIELDGROUP_AGGCHILDOBLI;
            //
            if(lMap.containsKey("fuColumns")) {
            	lFuColumns = (List<String>) lMap.get("fuColumns");
            	if(lFuColumns != null && lFuColumns.size() > 0) {
            		lFuFieldGrp = null;
            	}
            }
            if(lMap.containsKey("obliColumns")) {
            	lObliColumns = (List<String>) lMap.get("obliColumns");
            	if(lObliColumns != null && lObliColumns.size() > 0) {
            		lObliFieldGrp = null;
            	}
            }
            if(lMap.containsKey("obliSplitColumns")) {
            	lObliSplitColumns = (List<String>) lMap.get("obliSplitColumns");
            	if(lObliSplitColumns != null && lObliSplitColumns.size() > 0) {
            		lObliSplitFieldGrp = null;
            	}
            }
            //
        	List<String> lFieldGroups = new ArrayList<String>();
        	lFieldGroups.add(lFuFieldGrp);
        	lFieldGroups.add(lObliFieldGrp);
        	lFieldGroups.add(lObliSplitFieldGrp);
        	List<List<String>> lColumns = new ArrayList<List<String>>();
        	lColumns.add(lFuColumns);
        	lColumns.add(lObliColumns);
        	lColumns.add(lObliSplitColumns);
            if(lFuIds != null && lFuIds.size() > 0) {
            	lResponse = getFuStatus(pExecutionContext.getConnection(), lFuIds, lFieldGroups, lColumns, lUserBean);
            }else {
                List<String> lPurchaserCodes = (List<String>) lMap.get("buyerCodes");

                if(StringUtils.isBlank((String) lMap.get("fromDate")) && StringUtils.isBlank((String) lMap.get("toDate"))){
                	throw new CommonBusinessException("At least one field is required.");
                }
                Date lFromDate = null;
                if(StringUtils.isNotEmpty((String) lMap.get("fromDate")) ){
                	lFromDate = CommonUtilities.getDate(lMap.get("fromDate").toString(),AppConstants.DATE_FORMAT);
                }
                Date lToDate = null;
                if(StringUtils.isNotEmpty((String) lMap.get("toDate")) ){
                	lToDate = CommonUtilities.getDate(lMap.get("toDate").toString(),AppConstants.DATE_FORMAT);
                }
            	Map<Long, Object[]> lFuObliDataHash = aggregatorInstrumentBO.getFactoringUnits(pExecutionContext.getConnection(), lFromDate, lToDate, lPurchaserCodes, lUserBean);
            	List<Map<String,Object>> lReturnList = new ArrayList<Map<String,Object>>();
            	if(lFuObliDataHash!=null && lFuObliDataHash.size() > 0) {
                	Map<String,Object> lFuMap = null;
            		for(Object[] lFuObliData : lFuObliDataHash.values()) {
            			try{
            				lFuMap = getData(lFuObliData, lFieldGroups, lColumns);
                    	}catch(Exception ex){
                    		lFuMap = new HashMap<String, Object>();
            				if( lFuObliData!=null && lFuObliData.length > 0 && lFuObliData[0]!=null && lFuObliData[0] instanceof FactoringUnitBean ) {
                        		lFuMap.put("id", ((FactoringUnitBean) lFuObliData[0]).getId());
            				}else {
                        		lFuMap.put("id", 0);
            				}
                    		lFuMap.put("error", true);
                    		lFuMap.put("message", ex.getMessage());
                    	}finally {
                    		lReturnList.add(lFuMap);
                    	}
            		}
            	}
            	if(!lReturnList.isEmpty()) {
            		lResponse =  new JsonBuilder(lReturnList).toString();
            	}
            }
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator factoringunit status : ", e);
    		throw e;
    	}finally {
    		//AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    private String getFuStatus(Connection pConnection, List<Long> pFuIds, List<String> pFieldGroups, List<List<String>> pColumns,IAppUserBean pUserBean) throws Exception {
    	//
    	String lResponse = null;
        if(pFuIds != null && pFuIds.size() > 0) {
        	List<Map<String,Object>> lReturnList = new ArrayList<Map<String,Object>>();
        	Map<String,Object> lFuMap = null;
        	//
        	for(int lPtr=0; lPtr < pFuIds.size(); lPtr++) {
            	Object[] lFuObli = aggregatorInstrumentBO.getFactoringUnits(pConnection,  pFuIds.get(lPtr),pUserBean);
            	try{
            		lFuMap = getData(lFuObli, pFieldGroups, pColumns);
            	}catch(Exception ex){
            		lFuMap = new HashMap<String, Object>();
            		lFuMap.put("id", pFuIds.get(lPtr));
            		lFuMap.put("error", true);
            		lFuMap.put("message", ex.getMessage());
            	}finally {
            		lReturnList.add(lFuMap);
            	}
        	}
        	if(!lReturnList.isEmpty()) {
        		lResponse =  new JsonBuilder(lReturnList).toString();
        	}
        }
        return lResponse;
    }
    
    private Map<String,Object> getData(Object[] pFuObli, List<String> pFieldGroups, List<List<String>> pColumns) throws Exception{
    	Map<String,Object> lFuMap = null;
    	if(pFuObli!=null) {
			if(pFuObli[0] == null){
				throw new CommonBusinessException("Factoring unit not found");
			}
        	Map<String,Object> lObliMap = null;
    		FactoringUnitBean lFUBean = (FactoringUnitBean) pFuObli[0];
    		List<ObligationDetailBean>  lObliDetailBeans = (List<ObligationDetailBean>) pFuObli[1];
    		//
    		lFuMap = new HashMap<String, Object>();
    		lFuMap = factoringUnitBeanMeta.formatAsMap(lFUBean, pFieldGroups.get(0), pColumns.get(0), false);
    		//
    		List<Map<String,Object>> lObliMapList = new ArrayList<Map<String,Object>>();
        	lFuMap.put("obligationList", lObliMapList);
        	//
    		//create obligation list and obliidwise split list
        	if(lObliDetailBeans!=null) {
        	Map<Long,ObligationBean> lTmpObligations = new HashMap<Long,ObligationBean>();
        	Map<Long,List<ObligationSplitsBean>> lTmpObligationSplits = new HashMap<Long,List<ObligationSplitsBean>>(); //key=ParentObligationId
        	List<ObligationSplitsBean> lTmpObligationSplitsList = null;
        	List<Map<String,Object>> lObliSplitMapList = null;
        	Map<String,Object> lObliSplitMap = null;
        	//
    		//create obligation list and obliidwise split list
    		for(ObligationDetailBean lObliDetailBean : lObliDetailBeans) {
    			if(!lTmpObligations.containsKey(lObliDetailBean.getObligationBean().getId())) {
    				lTmpObligations.put(lObliDetailBean.getObligationBean().getId(), lObliDetailBean.getObligationBean());
    			}
    			if(!lTmpObligationSplits.containsKey(lObliDetailBean.getObligationBean().getId())) {
    				lTmpObligationSplits.put(lObliDetailBean.getObligationBean().getId(), new ArrayList<ObligationSplitsBean>());
    			}
    			lTmpObligationSplitsList = lTmpObligationSplits.get(lObliDetailBean.getObligationBean().getId());
    			lTmpObligationSplitsList.add(lObliDetailBean.getObligationSplitsBean());
    		}
    		//
    		for(ObligationBean lTmpObli : lTmpObligations.values()) {
    			//add obligation map
    			lObliMap = obligationBeanMeta.formatAsMap(lTmpObli, pFieldGroups.get(1), pColumns.get(2), false);
    			lObliMapList.add(lObliMap);
    			//add obligation split list
    			lObliSplitMapList = new ArrayList<Map<String,Object>>();
            	lObliMap.put("transactionList", lObliSplitMapList);
            	//add obligation splits to the above list
            	lTmpObligationSplitsList = lTmpObligationSplits.get(lTmpObli.getId());
        		for(ObligationSplitsBean lTmpObliSplit : lTmpObligationSplitsList) {
        			lObliSplitMap = obligationSplitBeanMeta.formatAsMap(lTmpObliSplit, pFieldGroups.get(2), pColumns.get(2), false);
        			lObliSplitMapList.add(lObliSplitMap);
            		}
        		}
    		}
    	}
    	return lFuMap;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="fuci-mis")
    @Path("/aggmisdata")
    //ONLY FOR CASH INVOICE 
    public String getMISdata(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	boolean lObligation = false;
    	try{
        	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
            Map<String, Object> lMap = parseJson(pMessage);
            if (!lMap.containsKey("buyerCodes") || lMap.get("buyerCodes")==null || ((List<String>)lMap.get("buyerCodes")).size()==0  ) {
            	throw new CommonBusinessException("Buyer Codes is mandatory.");
            }
            if (StringUtils.isBlank((String) lMap.get("type"))) {
            	throw new CommonBusinessException("Type is mandatory.");
            }
            if (lMap.containsKey("obligation")) {
            	lObligation = (Boolean) lMap.get("obligation");
            }
            ObligationBean.Type lType =  (ObligationBean.Type) TredsHelper.getInstance().getValue(ObligationBean.class, "type", (String) lMap.get("type"));
            if(!(ObligationBean.Type.Leg_1.equals(lType) || ObligationBean.Type.Leg_2.equals(lType) || ObligationBean.Type.Leg_3.equals(lType))) {
            	throw new CommonBusinessException("Incorrect Type passed.");
            }
            if(StringUtils.isBlank((String) lMap.get("fromDate")) && StringUtils.isBlank((String) lMap.get("toDate"))){
            	throw new CommonBusinessException("At least one field is required.");
            }
            List<String> lPurchaserCodes = (List<String>) lMap.get("buyerCodes");
            Date lFromDate = null;
            if(StringUtils.isNotEmpty((String) lMap.get("fromDate")) ){
            	lFromDate = CommonUtilities.getDate(lMap.get("fromDate").toString(),AppConstants.DATE_FORMAT);
            }
            Date lToDate = null;
            if(StringUtils.isNotEmpty((String) lMap.get("toDate")) ){
            	lToDate = CommonUtilities.getDate(lMap.get("toDate").toString(),AppConstants.DATE_FORMAT);
            }
            Map<Long, Object[]> lResultMap = new HashMap<Long, Object[]>();
            //validation 
            //
        	lResultMap = aggregatorInstrumentBO.getDataForMIS(pExecutionContext.getConnection(), lPurchaserCodes, lType, lFromDate, lToDate , lObligation, lUserBean);	
        	List<Object> lReturnMapList = aggregatorInstrumentBO.formatMISData(lResultMap);
            if(lReturnMapList != null){
            	lResponse =  new JsonBuilder(lReturnMapList).toString();
            }
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator misdata : ", e);
    		throw e;
    	}finally {
    		//AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
    
    @POST
    @Secured(secKey="fuci-mis")
    @Path("/aggmisdata")
    //ONLY FOR CASH INVOICE 
    public Response listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	logger.info("aggmisdata - pdf download.");
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	Connection lConnection = pExecutionContext.getConnection();
    	String lCVNumber = null;
    	//
        Map<String, Object> lMap =  parseJson(pMessage);
        if(lMap.containsKey("cvNumber")) {
            lCVNumber = (String) lMap.get("cvNumber");
        }
        if(StringUtils.isEmpty(lCVNumber)) {
        	throw new CommonBusinessException("cvNumber is Manadatory.");
        }
    	//
    	CashInvoicePaymentAdviceGenerator lCIPayAdviceGenerator = new CashInvoicePaymentAdviceGenerator();
    	Map<String, Map<Long, PaymentAdviceBean>> lDataMap = lCIPayAdviceGenerator.getCashInvoicePayAdvice(lConnection, null, lCVNumber);
    	if(lDataMap!=null && lDataMap.size() > 0) {
    		for (String lSupplier : lDataMap.keySet()){
    			Map<Long, PaymentAdviceBean> lFUwisePayAdv =  lDataMap.get(lSupplier);
    			for(Long lFuId : lFUwisePayAdv.keySet()) {
    				ByteArrayOutputStream lByteStream = CashInvoicePaymentAdviceGenerator.createPdf(lFUwisePayAdv.get(lFuId));
    				if(lByteStream!= null) {
    					return TredsHelper.getInstance().sendFileContents((lCVNumber +".pdf"), lByteStream.toByteArray());    					
    				}
    				break;
    			}
    		}
    	}
    	return null;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="fuci-with")
    @Path("/aggwithdrawfactunit")
    public String getWithdrawFu(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , String pMessage) throws Exception{
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try{
        	AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
            Map<String, Object> lMap = parseJson(pMessage);
            List<Long> lFuIds = (List<Long>) lMap.get("fuIds");
        	Object lRetVal = null; 
            if(lFuIds != null && lFuIds.size() > 0) {
            	return aggregatorInstrumentBO.getWithdrawFactoringUnits(pExecutionContext, lFuIds , lUserBean);
            }else{
            	lRetVal = new HashMap<String, Object>();
            	((Map<String, Object>)lRetVal).put("error", "No factoring units found in list.");
            }
            lResponse =  new JsonBuilder(lRetVal).toString();
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator withdraw factoringunit : ", e);
    		throw e;
    	}finally {
    		AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey= {"instgem-save","instgem-saveplatform"})
    @Path("/gem/addinvoice")
    public String addInvoice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pMessage) throws Exception {
        int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
        String lResponse = null;
        // for gem
        Connection lConnection = pExecutionContext.getConnection();
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        Long lArrId = null;
        GEMInvoiceBean lGemInvoiceBean = null;
        boolean lIsPlatform = false;
        Map<String, Object> lInstrumentMap = null;
        try{
            AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
            Map<String, Object> lMap = parseJson(pMessage);
            if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
                            CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
                lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
                //if api user is using our apis then there will be no adpater
                if(lClientAdapter!=null){
                        lAdapterConnection = DBHelper.getInstance().getConnection();
                        lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
                        lProcessInformationBean.setClientDataForProcessing(pMessage);
                        lArrId = lClientAdapter.logInComing(lProcessInformationBean, "/v1/gem/addinvoice",null, true, false);
                        pMessage = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
                }
            }
            Map<String,Object> lGemsMap = new HashMap<String,Object>();
            //IN CASE OF RESEND ONLY
            if (AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
            	lIsPlatform = true;
            	AdapterRequestResponseBean lArrBean = new AdapterRequestResponseBean();
            	lArrBean.setId(new Long(lMap.get("arrId").toString()));
            	lArrBean = adapterRequestResponseDAO.findBean(lConnection, lArrBean);
            	lGemsMap = (Map<String, Object>) new JsonSlurper().parseText(lArrBean.getApiRequestData());
            	lUserBean = TredsHelper.getInstance().getAppUser(new Long(lMap.get("creator").toString()));
                lMap.putAll(lGemsMap);
                lGemInvoiceBean = new GEMInvoiceBean();
                lGemInvoiceBean.setId(new Long(lMap.get("id").toString()));
                lGemInvoiceBean = gemInvoiceDAO.findBean(lConnection, lGemInvoiceBean);
            }
            List<ValidationFailBean> lTmpVal = new ArrayList<ValidationFailBean>();
            List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
            InstrumentBean lInstrumentBean = new InstrumentBean();
            //
            //TODO: CHANGE THE FIELD GROUP
            instrumentBeanMeta.validateAndParse(lInstrumentBean, lMap, FIELDGROUP_GEMSADDINVOICE, lTmpVal);
            if(lTmpVal!=null && lTmpVal.size() > 0) {
                    for(ValidationFailBean lVFBean :  lTmpVal ){
                            AggregatorInstrumentBO.appendMessage(lMessages, lVFBean.getName(), lVFBean.getMessage());
                    }
            }
            if(lMessages.size() > 0){
                    lResponse = new JsonBuilder(lMessages).toString();
                    throw new CommonBusinessException(lResponse);
            }
            for(String lField : gemsFieldsList){
                if(lMap.containsKey(lField)){
                    lGemsMap.put(lField, lMap.get(lField));
                }
            }
            lInstrumentBean.setOtherSettings(new JsonBuilder(lGemsMap).toString());
            pExecutionContext.setAutoCommit(false);
            lInstrumentMap = aggregatorInstrumentBO.createInstrument(pRequest,pExecutionContext,lInstrumentBean,(AppUserBean)lUserBean, lArrId,lIsPlatform,lMap,lGemInvoiceBean);
            pExecutionContext.commitAndDispose();
            lResponse = new JsonBuilder(lInstrumentMap).toString();
        }catch(Exception e){
                lResponse = e.getMessage();
                pExecutionContext.rollback();
        		logger.error("Error in gem addinvoice : ", e);
                throw e;
        }finally {
        	try(Connection lConnectionForGemResend = DBHelper.getInstance().getConnection()){
                //IN CASE OF RESEND ONLY
        		if (lIsPlatform) {
	            	GEMInvoiceResendBean lGemInvoiceResendBean = new GEMInvoiceResendBean();
	            	//if (lInstrumentMap==null || Objects.isNull(lInstrumentMap.containsKey("fuId"))) {
	            	if (lInstrumentMap==null || Objects.isNull(lInstrumentMap.containsKey("inId"))) {
	            		lGemInvoiceResendBean.setStatus(Status.Failed);
	            		lGemInvoiceBean.setStatus(GEMInvoiceBean.Status.Pending);
	            	}else {
	            		lGemInvoiceResendBean.setStatus(Status.Success);
	            		lGemInvoiceBean.setStatus(GEMInvoiceBean.Status.Success);
	            	}
	            	gemInvoiceDAO.update(lConnectionForGemResend, lGemInvoiceBean);
	            	lGemInvoiceResendBean.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
	            	lGemInvoiceResendBean.setResponseData(lResponse);
	            	lGemInvoiceResendBean.setGiId(lGemInvoiceBean.getId());
	            	gemInvoiceResendDAO.insert(lConnectionForGemResend, lGemInvoiceResendBean);
        		}
        	}
    		if(lClientAdapter!=null){
                lProcessInformationBean.setTredsReturnResponseData(lResponse);
                lClientAdapter.logInComing(lProcessInformationBean, "/v1/gem/addinvoice",lResponseStatus, false, false);
                PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
            }
            if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
                    lAdapterConnection.close();
            }
            AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
        }
        return lResponse;
    }

    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="gem-fustatus")
    @Path("/gem/factstatus")
    public String getAllFactoringUnit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	 int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
    	try{
    		AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Connection lConnection = pExecutionContext.getConnection();
	        Map<String, Object> lMap = parseJson(pMessage);
	        //
	        if ( !((lMap!=null && lMap.containsKey("gemUniqueRequestIdentifier") && StringUtils.isNotEmpty((String)lMap.get("gemUniqueRequestIdentifier")))
	         || (lMap!=null && lMap.containsKey("fromDate") && StringUtils.isNotEmpty((String)lMap.get("fromDate")) )
	         ||  (lMap!=null && lMap.containsKey("toDate") && StringUtils.isNotEmpty((String)lMap.get("toDate")) ) ) ){
	        	throw new CommonBusinessException("Please provide an input. Provide gemUniqueRequestIdentifier or (fromDate/toDate).");
	        }
	        //
	        lResponse = aggregatorInstrumentBO.getFactoringUnitStatusByGem(lConnection, lMap , lUserBean);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in gem factoring unit status : ", e);
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

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="gem-withdraw")
    @Path("/gem/withdraw")
    public String withdrawFactUnt(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
        String lResponse = null;
    	try{
    		AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Map<String, Object> lMap = parseJson(pMessage);
	        //
	        if (lMap==null || !lMap.containsKey("gemUniqueRequestIdentifier") || StringUtils.isEmpty((String)lMap.get("gemUniqueRequestIdentifier"))){
	        	throw new CommonBusinessException("Please provide an input i.e gemUniqueRequestIdentifier.");
	        }
	        //
	        lResponse = aggregatorInstrumentBO.withdrawGem(pExecutionContext, lMap , lUserBean);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in gem withdraw : ", e);
    		throw e;
    	}finally {
    		 AggregatorLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
    	return lResponse;
    }
	
	@POST
    @Path("/aggpaymentadvice")
    public String aggPaymentAdvice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	String lResponse = null;
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	try{
    		AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Connection lConnection = pExecutionContext.getConnection();
	        Map<String, Object> lMap = parseJson(pMessage);
	        Date lSettlementDate = CommonUtilities.getDate(lMap.get("settlementDate").toString(),AppConstants.DATE_FORMAT);
	        CashInvoicePaymentAdviceGenerator lCIPAGenerator = new CashInvoicePaymentAdviceGenerator();
	        lCIPAGenerator.cashInvoicePayAdvice(lConnection,lSettlementDate);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator payment advice : ", e);
    		throw e;
    	}
    	return lResponse;
    }
    
    
    @POST
    @Path("/aggpaymentdetails")
    public String aggPaymentDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	String lResponse = null;
        IClientAdapter lClientAdapter  = null;
        ProcessInformationBean lProcessInformationBean = null;
        Connection lAdapterConnection = null;
        ApiResponseStatus lResponseStatus = null;
        if(StringUtils.isEmpty(pMessage)){
        	throw new CommonBusinessException("No Data received.");
        }
    	try{
            AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		    if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && 
		                    CommonAppConstants.Yes.Yes.equals(lUserBean.getEnableAPI()) ){
		        lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
		        //if api user is using our apis then there will be no adpater
		        if(lClientAdapter!=null){
		                lAdapterConnection = DBHelper.getInstance().getConnection();
		                lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
		                lProcessInformationBean.setClientDataForProcessing(pMessage);
		                lClientAdapter.logInComing(lProcessInformationBean, "/v1/aggpaymentdetails",null, true, false);
		                lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
		        }
		    }
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator payment details : ", e);
    		throw e;
    	}
    	return lResponse;
    }
    
    
    @POST
    @Path("/aggvendordetails")
    public String getVendorDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pMessage, this.getClass().getName(),0);
    	AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
    	if(!lMap.containsKey("buyerCode") || StringUtils.isEmpty(lMap.get("buyerCode").toString())){
    		throw new CommonBusinessException("Please provide buyer code.");
    	}
    	if(lAppEntityBean.isPurchaserAggregator()) {
      		purchaserAggregatorBO.validateMappedEntity(pExecutionContext.getConnection(), lUserBean.getDomain(), lMap.get("buyerCode").toString());
    	}
    	if(!lMap.containsKey("linkFromDate") || StringUtils.isEmpty(lMap.get("linkFromDate").toString())){
    		throw new CommonBusinessException("Please provide from date.");
    	}
    	if(!lMap.containsKey("linkToDate") || StringUtils.isEmpty(lMap.get("linkToDate").toString())){
    		throw new CommonBusinessException("Please provide to date.");
    	}
    	String lBuyerCode = lMap.get("buyerCode").toString();
    	Date lFromDate = null;
    	Date lToDate = null;
    	try {
        	lFromDate = FormatHelper.getDate(lMap.get("linkFromDate").toString(), AppConstants.DATE_FORMAT);
        	lToDate = FormatHelper.getDate(lMap.get("linkToDate").toString(), AppConstants.DATE_FORMAT);
        	if(lFromDate==null || lToDate==null) {
        		throw new CommonBusinessException("Invalid date format.");
        	}
    	}catch(Exception lEx) {
    		throw new CommonBusinessException("Invalid date format.");
    	}
    	return aggregatorInstrumentBO.getVendorDetails(pExecutionContext, lBuyerCode, lFromDate, lToDate);
    }
 
    //PATANJALI
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
//	@Secured(secKey = "factunitsp-acceptbid")
	@Path("/agg/factunitsp/acceptbid")
	public String acceptBid(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
    	int lRequestId = AggregatorLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    		JsonSlurper lJsonSlurper = new JsonSlurper();
    		List<Object> lList = (ArrayList<Object>) lJsonSlurper.parseText(pFilter);
    		if(lList!=null && lList.size() > 0){
    		 	Map<String,Object> lMap =  (Map<String, Object>) lList.get(0);
    			Boolean lSpecificBid = (Boolean) lMap.get("specificBidAccept");
    			if(lSpecificBid){
    				logger.info("/acceptbid specific bid "+((Integer)lMap.get("bdId")).toString()+" received.");
    			}
    		}
    		lResponse = updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Factored);
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator acceptbid : ", e);
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
    	return lResponse;
	}


    //PATANJALI
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
//	@Secured(secKey = "factunitsp-withdraw")
	@Path("/agg/factunitsp/withdraw")
	public String withdraw(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
    		lResponse = updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Withdrawn);
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.error("Error in aggregator withdraw : ", e);
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
    	return lResponse;
	}

    //PATANJALI
    private String updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, 
            String pFilter, FactoringUnitBean.Status pStatus) throws Exception {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		Map<String, String> lDataHash = new HashMap<String,String>();
        if (FactoringUnitBean.Status.Active.equals(pStatus)){
        	lDataHash.put("action", "factoring unit sent to auction");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUSENTTOAUCTION, AppConstants.TEMPLATE_PREFIX_FUSENTTOAUCTION, lDataHash);
        }else if (FactoringUnitBean.Status.Factored.equals(pStatus)){
        	lDataHash.put("action", "factoring unit sent to auction");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_BIDACCEPTCB, AppConstants.TEMPLATE_PREFIX_BIDACCEPTCB, lDataHash);
        }else if (FactoringUnitBean.Status.Withdrawn.equals(pStatus)){
        	lDataHash.put("action", "factoring unit withdrawal");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
        }else if (FactoringUnitBean.Status.Suspended.equals(pStatus)){
        	lDataHash.put("action", "factoring unit put on hold");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
        }
		List<Map<String, Object>> lJsonList = (List<Map<String, Object>>) lJsonSlurper.parseText(pFilter);
		List<FactoringUnitBean> lFilterList = new ArrayList<FactoringUnitBean>();
		for (Map<String, Object> lMap : lJsonList) {
			FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
			factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, lMap, null);
			lFilterList.add(lFactoringUnitBean);
		}
		FactoringUnitBO lFactoringUnitBO = new FactoringUnitBO();
		return lFactoringUnitBO.updateStatus(pExecutionContext, lFilterList, lUserBean, pStatus);
	}
 
    //PATANJALI
    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey="factunitfin-depth")
    @Path("/agg/factunitfin/depth/{id}")
    public String depth(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("id") Long pId) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pId.toString(), this.getClass().getName(),0);
    	String lResponse = null;
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        try{
    		FactoringUnitBO lFactoringUnitBO = new FactoringUnitBO();
        	lResponse = lFactoringUnitBO.depth(pExecutionContext, pId, lUserBean);
        }catch(Exception e){
        	lResponse = e.getMessage();
    		logger.error("Error in aggregator depth : ", e);
        	throw e;
        }
        return lResponse;
    }
    

}