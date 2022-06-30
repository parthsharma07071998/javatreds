package com.xlx.treds;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.MonetagoRequiredFieldsBean;
import com.xlx.treds.other.bean.MonetagoRequestResponseBean;
import com.xlx.treds.other.bean.MonetagoRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.other.bean.MonetagoRequestResponseBean.Type;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class MonetagoTredsHelper {
	public static Logger logger = Logger.getLogger(MonetagoTredsHelper.class);
    private static MonetagoTredsHelper theInstance;
    //
    private GenericDAO<MonetagoRequestResponseBean> monetagoRequestResponseDAO;
    //
	private static final String TARGET_METHOD_TYPE_POST = "POST";
	private static final String TARGET_METHOD_TYPE_PATCH = "PATCH";
	private static final String TARGET_METHOD_TYPE_GET = "GET";
	//private static final String 
    //
	//
	// register 	POST 	Upload a new Invoice to the distributed Ledger.
	private static final String TARGET_METHOD_REGISTER = "register";
	// factor	PATCH	Factor the Invoice
	private static final String TARGET_METHOD_FACTOR = "factor";
	// cancel	PATCH	Cancel the invoice with a given reason code.
	private static final String TARGET_METHOD_CANCEL = "cancel";
	// getInvoiceByLedgerID	GET	Query for Invoice by Ledger ID
	private static final String TARGET_METHOD_GETINVOICEBYLEDGERID = "getInvoiceByLedgerID";
	// registeredInvoices	GET	Query for all registered Invoices for a given Exchange
	private static final String TARGET_METHOD_REGISTEREDINVOICES = "registeredInvoices";
	// factoredInvoices	GET	Query for all factored Invoices for a given Exchange
	private static final String TARGET_METHOD_FACTOREDINVOICES = "factoredInvoices";
	// canceledInvoices	GET	Query for all canceled Invoices for a given Exchange
	private static final String TARGET_METHOD_CANCELEDINVOICES = "canceledInvoices";
	// allInvoices	GET	Query for all Invoices for a given Exchange, regardless of status.
	private static final String TARGET_METHOD_ALLINVOICES = "allInvoices";
	// duplicateInvoices	GET	Query for Invoices for a given Exchange that are duplicates of other invoices
	private static final String TARGET_METHOD_DUPLICATEINVOICES = "duplicateInvoices";
	// duplicateInvoices	GET	Query for Invoices for a given Exchange that are duplicates of other invoices
	private static final String TARGET_METHOD_REGISTERBATCHJSON = "registerBatchJSON";
    //Monetago input parameters
	private static final String MONETAGO_PARAMETER_SELLERGSTN = "SellerGSTN";
	private static final String MONETAGO_PARAMETER_BUYERGSTN = "BuyerGSTN";
	private static final String MONETAGO_PARAMETER_INVOICEID = "InvoiceID";
	private static final String MONETAGO_PARAMETER_INVOICEDATE = "InvoiceDate";
	private static final String MONETAGO_PARAMETER_AMOUNT = "Amount";
	private static final String MONETAGO_PARAMETER_EXCHRECVDATETIME = "ExchRecvDateTime";
	private static final String MONETAGO_PARAMETER_REGISTERINVOICE = "RegisterInvoice";
	private static final String MONETAGO_PARAMETER_OVERIDE = "Override";
	private static final String MONETAGO_PARAMETER_LEDGERID = "LedgerID";
	private static final String MONETAGO_PARAMETER_REASONCODE = "ReasonCode";
	private static final String MONETAGO_PARAMETER_EXTID = "ExtID";
	private static final String MONETAGO_PARAMETER_BATCH = "Batch";
	//
	private static final String MONETAGO_PARAMETER_PAYLOAD = "payload";
	private static final String MONETAGO_PARAMETER_ERROR = "error";
	private static final String MONETAGO_PARAMETER_DETAILS = "details";
	private static final String MONETAGO_PARAMETER_HASH = "hash"; //txn id
	private static final String MONETAGO_PARAMETER_STATUS = "Status";
	private static final String MONETAGO_PARAMETER_DUPLICATES = "Duplicates";
	private static final String MONETAGO_PARAMETER_DUPLICATEFLAG = "DuplicateFlag";
	//Monetago response 
	public static final String TREDS_RESPONSE_LEDGERID = "LedgerID";
	public static final String TREDS_RESPONSE_MESSAGE = "message";
	public static final String TREDS_RESPONSE_TRANSID = "transId";
	public static final String TREDS_RESPONSE_FACTSTATUS = "Status";
	public static final String TREDS_RESPONSE_DUPLICATELEDGERID = "DuplicateLedgerID";
	//
	private boolean monetagoEnabled=false;	
	private boolean disasterEnabled=false;
	//
	public static final long MONETAGO_STATUS_REGISTERED = 0;
	public static final long MONETAGO_STATUS_FACTORED = 1;
	public static final long MONETAGO_STATUS_CANCELED = 2;
	//
	private boolean monetagoLoging_Register=false;	
	private boolean monetagoLoging_Cancel=false;
	private boolean monetagoLoging_Factor=false;
	
	 public enum CancelResonCode implements IKeyValEnumInterface<String>{
	        Expired("2","Expired"),Withdrawn("5","Withdrawn"),Unknown("99","Unknown"),Rejected("1","Rejected")
	        	,NotFinanced("6","NotFinanced"),Leg1Financed("7","Leg1Financed");
	        
	        private final String code;
	        private final String desc;
	        private CancelResonCode(String pCode, String pDesc) {
	            code = pCode;
	            desc = pDesc;
	        }
	        public String getCode() {
	            return code;
	        }
	        public String toString() {
	        	return desc;
	        }
	    }
	
	public static MonetagoTredsHelper getInstance()
	{
		if (theInstance == null)
		{
			synchronized(MonetagoTredsHelper.class)
			{
				if (theInstance == null)
				{
					try
					{
						MonetagoTredsHelper tmpTheInstance = new MonetagoTredsHelper();
						theInstance = tmpTheInstance;
					}
					catch(Exception lException)
					{
						logger.fatal("Error while instantiating MonetaGo Treds Helper",lException);
					}
				}
			}
		}
		return theInstance;
	}

	protected MonetagoTredsHelper(){
		Boolean lEnabled =RegistryHelper.getInstance().getBoolean(AppConstants.REGISTRY_MONETAGO_ENABLED);
		monetagoEnabled = (lEnabled!=null && lEnabled.booleanValue()==true);
		Boolean lDisasterEnabled =RegistryHelper.getInstance().getBoolean(AppConstants.REGISTRY_DESMONETAGO_ENABLED);
		disasterEnabled = (lDisasterEnabled!=null && lDisasterEnabled.booleanValue()==true);
		monetagoRequestResponseDAO = new GenericDAO<MonetagoRequestResponseBean>(MonetagoRequestResponseBean.class);
		HashMap<String, Object> lMonetagoLogingSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_MONETAGOLOGING);
		monetagoLoging_Register = (boolean) lMonetagoLogingSetting.get(AppConstants.ATTRIBUTE_MONETAGOLOGING_REGISTER);
		monetagoLoging_Cancel = (boolean) lMonetagoLogingSetting.get(AppConstants.ATTRIBUTE_MONETAGOLOGING_CANCEL);
		monetagoLoging_Factor = (boolean) lMonetagoLogingSetting.get(AppConstants.ATTRIBUTE_MONETAGOLOGING_FACTOR);
	}
	
	public boolean performMonetagoCheck(){
		
		return monetagoEnabled;		
	}
	public boolean performMonetagoCheck(String pPurchaserEntity){
		if(pPurchaserEntity!=null) {
			AppEntityBean lAeBean;
			try {
				lAeBean = TredsHelper.getInstance().getAppEntityBean(pPurchaserEntity);
				if (lAeBean.getPreferences()!=null && lAeBean.getPreferences().getSkipmonetago()!=null && CommonAppConstants.Yes.Yes.equals((lAeBean.getPreferences().getSkipmonetago()))) {
					return false;
				}
			} catch (MemoryDBException e) {
				logger.error("error in performMonetagoCheck(pPurchaserEntity) : "+e.getMessage());
			}
			
		}
		return monetagoEnabled;		
	}
	public boolean isDisasterEnabled(){
		
		return disasterEnabled;		
	}
	
	
	public Map<String,String> register(String pSupplierGSTN, String pPurchaserGSTN, String pInstNumber, Date pInstDate, BigDecimal pAmount, String pInfoMessage, Long pInId ){
		//return testRegister(pSupplierGSTN, pPurchaserGSTN, pInstNumber,pInstDate,pAmount,pInfoMessage);
		MonetagoRequestResponseBean lMRRBean = new MonetagoRequestResponseBean();
		lMRRBean.setType(Type.Register);
		lMRRBean.setInId(pInId);
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker(TARGET_METHOD_REGISTER);
		//CREATE THE DATA TO SEND
		Map<String, Object> lInputHash = new HashMap<String, Object>();
		Map<String, Object> lDataHash = new HashMap<String, Object>();
		//
		//EXAMPLE : String lInputStr = "{ \"RegisterInvoice\": { \"SellerGSTN\": \"MYSELLERGSTN12345\", \"BuyerGSTN\": \"MYBUYERGSTN\", \"InvoiceID\": \"INV123456789\", \"InvoiceDate\": \"2017-12-12T00:00:00.000Z\", \"Amount\": \"1234.56\", \"ExchRecvDateTime\": \"2017-12-20T05:08:42.000Z\" }, \"Override\": 0}";
		lDataHash.put(MONETAGO_PARAMETER_SELLERGSTN, pSupplierGSTN);
		lDataHash.put(MONETAGO_PARAMETER_BUYERGSTN, pPurchaserGSTN);
		lDataHash.put(MONETAGO_PARAMETER_INVOICEID, pInstNumber);
		lDataHash.put(MONETAGO_PARAMETER_INVOICEDATE, TredsHelper.getInstance().getISO8601FormattedDate(pInstDate));
		lDataHash.put(MONETAGO_PARAMETER_AMOUNT, (pAmount!=null?pAmount.setScale(2).toString(): BigDecimal.ZERO.setScale(2).toString()));
		lDataHash.put(MONETAGO_PARAMETER_EXCHRECVDATETIME, TredsHelper.getInstance().getISO8601FormattedDate(CommonUtilities.getCurrentDateTime()));
		//
		lInputHash.put(MONETAGO_PARAMETER_REGISTERINVOICE, lDataHash);
		lInputHash.put(MONETAGO_PARAMETER_OVERIDE, TredsHelper.getInstance().getOverride());
		//
		String lInputStr = new JsonBuilder(lInputHash).toString();
//		lMRRBean.setApiRequestData(lInputStr);
		lMRRBean.setSellerGSTN(pSupplierGSTN);
		lMRRBean.setBuyerGSTN(pPurchaserGSTN);
		lMRRBean.setInvoiceID(pInstNumber);
		lMRRBean.setInvoiceDate(pInstDate);
		lMRRBean.setAmount(pAmount);
		lMRRBean.setExchRecvDateTime(CommonUtilities.getCurrentDateTime());
		lMRRBean.setOverride(TredsHelper.getInstance().getOverride());
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Info : "+pInfoMessage);
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + TARGET_METHOD_REGISTER);
		logger.debug("Input : "+lInputStr);
		String lDateTime = CommonUtilities.getCurrentDateTime().toString();
		lMRRBean.setRequestDateTime(CommonUtilities.getCurrentDateTime());
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lReponseStatus = 0;
		try{
			lResponse = lBuilder.post(Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);
			lReponseStatus = lResponse.getStatus();
			logger.debug("Register Response Status : " + lResponse.getStatus());
			logger.debug("Register Response readEntity : " + lResponseStr);
			logger.debug("*****************************************************************************************************");
		}catch(Exception lException){
			logger.debug("Error while sending to monetago : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
			return lReturnValue;
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		lMRRBean.setResponseDateTime(new Timestamp(System.currentTimeMillis()));
		//
		 JsonSlurper lJsonSlurper = new JsonSlurper();
		 Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		//UPDATE THE INPUT AS PER THE RESPONSE
		if(lReponseStatus == 200) {
			//readEntity : {"payload":[{"LedgerID":"1cae771ebfe507eeaabe25094c4748f31013168ead08b677408bcc2f666b77e0","DuplicateFlag":0}]}
			List<Map<String,Object>> lResult = (List<Map<String,Object>>) lResponseMap.get(MONETAGO_PARAMETER_PAYLOAD);
//			lMRRBean.setApiResponseData(new JsonBuilder(lResult).toString());
			lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
			for(Map<String,Object> lLedgers : lResult){
				lReturnValue.put(TREDS_RESPONSE_LEDGERID, (String) lLedgers.get(MONETAGO_PARAMETER_LEDGERID));
				lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Success");
				lMRRBean.setOutputTxnId((String) lLedgers.get(MONETAGO_PARAMETER_LEDGERID));
				lMRRBean.setDuplicateFlag(Long.valueOf(lLedgers.get(MONETAGO_PARAMETER_DUPLICATEFLAG).toString()));
				break;
			}
			try(Connection lConnection = DBHelper.getInstance().getConnection();){
				if(monetagoLoging_Register){
					monetagoRequestResponseDAO.insert(lConnection, lMRRBean);
				}
			} catch (Exception lEx) {
				logger.info("Error while inserting logs in MonetagoRequestResponse Table: " + lEx.getMessage());
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,String> lResultDetails = (Map<String,String>) lResult.get(MONETAGO_PARAMETER_DETAILS);		
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
			//logger.info("");
		}

		return lReturnValue;
	}
	
	
	public Map<String,String> cancel(String pLedgerID, CancelResonCode pResaonCode , String pInfoMessage, Long pInId){
		//return testCancel(pLedgerID, pResaonCode, pInfoMessage);
		MonetagoRequestResponseBean lMRRBean = new MonetagoRequestResponseBean();
		lMRRBean.setType(Type.Cancel);
		lMRRBean.setInId(pInId);
		lMRRBean.setReasonCode(Long.valueOf(pResaonCode.getCode()));
		lMRRBean.setInputTxnId(pLedgerID);
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker(TARGET_METHOD_CANCEL);
		lBuilder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
		Map<String, Object> lDataHash = new HashMap<String, Object>();
		//
		lDataHash.put(MONETAGO_PARAMETER_LEDGERID,pLedgerID);
		lDataHash.put(MONETAGO_PARAMETER_REASONCODE , pResaonCode.getCode());
		//
		String lInputStr = new JsonBuilder(lDataHash).toString();
//		lMRRBean.setApiRequestData(lInputStr);
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Info : "+pInfoMessage);
		logger.debug("Traget : "+TARGET_METHOD_CANCEL);
		logger.debug("Input : "+lInputStr);
		String lDateTime = CommonUtilities.getCurrentDateTime().toString();
		lMRRBean.setRequestDateTime(CommonUtilities.getCurrentDateTime());
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lReponseStatus = 0;
		try{
			lResponse = lBuilder.method(TARGET_METHOD_TYPE_PATCH, Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);
			lReponseStatus = lResponse.getStatus();
			logger.debug("Cancel Response Status : " + lResponse.getStatus());
			logger.debug("Cancel Response readEntity : " + lResponseStr);
			logger.debug("*****************************************************************************************************");
		}catch(Exception lException){
			logger.debug("Error while sending to monetago : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
			return lReturnValue;
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		lMRRBean.setResponseDateTime(new Timestamp(System.currentTimeMillis()));
		//
		
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(lReponseStatus == 200) {
			 lReturnValue.put(TREDS_RESPONSE_TRANSID, (String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH));
			 lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Success");
//			 lMRRBean.setApiResponseData(new JsonBuilder(lResponseMap).toString());
			 lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
			 lMRRBean.setOutputTxnId(((String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH)));
			try(Connection lConnection = DBHelper.getInstance().getConnection();){
				if(monetagoLoging_Cancel){
					monetagoRequestResponseDAO.insert(lConnection, lMRRBean);
				}
			} catch (Exception lEx) {
				logger.info("Error while inserting logs in MonetagoRequestResponse Table: " + lEx.getMessage());
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
//			lMRRBean.setApiResponseData(new JsonBuilder(lResult).toString());
			lMRRBean.setApiResponseStatus(ApiResponseStatus.Failed);
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,String> lResultDetails = (Map<String,String>) lResult.get(MONETAGO_PARAMETER_DETAILS);		
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
		}
		return lReturnValue;
    }
	
	
	public Map<String,String> factor(String pLedgerID, String pInfoMessage,Long pInId){
		//return testFactor(pLedgerID, pInfoMessage);
		MonetagoRequestResponseBean lMRRBean = new MonetagoRequestResponseBean();
		lMRRBean.setType(Type.Factor);
		lMRRBean.setInId(pInId);
		lMRRBean.setInputTxnId(pLedgerID);
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker(TARGET_METHOD_FACTOR);
		lBuilder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
		//CREATE THE DATA TO SEND
		Map<String, Object> lDataHash = new HashMap<String, Object>();
		lDataHash.put(MONETAGO_PARAMETER_LEDGERID, pLedgerID);
		lDataHash.put(MONETAGO_PARAMETER_OVERIDE, TredsHelper.getInstance().getOverride());
		//
		String lInputStr = new JsonBuilder(lDataHash).toString();
//		lMRRBean.setApiRequestData(lInputStr);
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Info : "+pInfoMessage);
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + TARGET_METHOD_FACTOR);
		logger.debug("Input : "+lInputStr);
		String lDateTime = CommonUtilities.getCurrentDateTime().toString();
		lMRRBean.setRequestDateTime(CommonUtilities.getCurrentDateTime());
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lReponseStatus = 0;
		try{
			lResponse = lBuilder.method(TARGET_METHOD_TYPE_PATCH, Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);
			lReponseStatus = lResponse.getStatus();
			logger.debug("Fact Response Status : " + lResponse.getStatus());
			logger.debug("Fact Response readEntity : " + lResponseStr);
			logger.debug("*****************************************************************************************************");
		}catch(Exception lException){
			logger.debug("Error while sending to monetago : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
			return lReturnValue;
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		lMRRBean.setResponseDateTime(new Timestamp(System.currentTimeMillis()));
		//
		 JsonSlurper lJsonSlurper = new JsonSlurper();
		 Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		//UPDATE THE INPUT AS PER THE RESPONSE
		if(lReponseStatus == 200) {
			 lReturnValue.put(TREDS_RESPONSE_TRANSID, (String) lResponseMap.get(MONETAGO_PARAMETER_HASH));
			 lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Success");
//			 lMRRBean.setApiResponseData(new JsonBuilder(lResponseMap).toString());
			 lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
			 lMRRBean.setOutputTxnId(((String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH)));
			try(Connection lConnection = DBHelper.getInstance().getConnection();){
				if(monetagoLoging_Factor){
					monetagoRequestResponseDAO.insert(lConnection, lMRRBean);
				}
			} catch (Exception lEx) {
				logger.info("Error while inserting logs in MonetagoRequestResponse Table: " + lEx.getMessage());
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,String> lResultDetails = (Map<String,String>) lResult.get(MONETAGO_PARAMETER_DETAILS);
//			lMRRBean.setApiResponseData(new JsonBuilder(lResult).toString());
			lMRRBean.setApiResponseStatus(ApiResponseStatus.Failed);
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
		}
		return lReturnValue;
	}
	
	public Map<Long, MonetagoRequiredFieldsBean> registerBatch(Map<Long,MonetagoRequiredFieldsBean> pMonetagoData , Long pGroupId) throws Exception{
		Map<Long , MonetagoRequestResponseBean> lMRRMapBean = new HashMap<Long , MonetagoRequestResponseBean>();
		//Map<String,String> lReturnValue = new HashMap<String, String>();
		MonetagoRequestResponseBean lMRRBean = null;
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker(TARGET_METHOD_REGISTERBATCHJSON);
		//CREATE THE DATA TO SEND
		Map<String, Object> lInputHash = new HashMap<String, Object>();
		List<Object> lBatchList = new ArrayList<>();
		HashMap<String, String> lData = null;
		MonetagoRequiredFieldsBean lBean = new MonetagoRequiredFieldsBean();
		String lExchRecvDateTime = TredsHelper.getInstance().getISO8601FormattedDate(CommonUtilities.getCurrentDateTime());
		//
		for(Long lkey : pMonetagoData.keySet()){
			lMRRBean =  new MonetagoRequestResponseBean();
			lData= new HashMap<String, String>();
			lBean = pMonetagoData.get(lkey);
			lMRRBean.setInId(lBean.getId());
			lMRRBean.setSellerGSTN(lBean.getSupGstn());
			lMRRBean.setBuyerGSTN(lBean.getPurGstn());
			lMRRBean.setInvoiceID(lBean.getInstNumber());
			lMRRBean.setInvoiceDate(lBean.getInstDate());
			lMRRBean.setAmount(lBean.getAmount());
			lMRRBean.setExchRecvDateTime(CommonUtilities.getCurrentDateTime());
			lMRRBean.setOverride(TredsHelper.getInstance().getOverride());
			lMRRBean.setInputTxnId(lBean.getLedgerId());
			lMRRBean.setType(Type.RegisterBatch);
			lMRRBean.setGroupInId(pGroupId);
			lMRRMapBean.put(lBean.getId(), lMRRBean);
			lData.put(MONETAGO_PARAMETER_EXTID, lBean.getId().toString());
			lData.put(MONETAGO_PARAMETER_SELLERGSTN, lBean.getSupGstn());
			lData.put(MONETAGO_PARAMETER_BUYERGSTN, lBean.getPurGstn());
			lData.put(MONETAGO_PARAMETER_INVOICEID, lBean.getInstNumber());
			lData.put(MONETAGO_PARAMETER_INVOICEDATE, TredsHelper.getInstance().getISO8601FormattedDate(lBean.getInstDate()));
			lData.put(MONETAGO_PARAMETER_AMOUNT, (lBean.getAmount()!=null?lBean.getAmount().setScale(2).toString(): BigDecimal.ZERO.setScale(2).toString()));
			lData.put(MONETAGO_PARAMETER_EXCHRECVDATETIME, lExchRecvDateTime );
			lBatchList.add(lData);
		}
		//
		lInputHash.put(MONETAGO_PARAMETER_BATCH, lBatchList);
		lInputHash.put(MONETAGO_PARAMETER_OVERIDE, TredsHelper.getInstance().getOverride());
		//
		String lInputStr = new JsonBuilder(lInputHash).toString();
		//
		logger.debug("*****************************************************************************************************");
		//logger.debug("Info : "+pInfoMessage);
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + TARGET_METHOD_REGISTERBATCHJSON);
		logger.debug("Input : "+lInputStr);
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lResponseStatus = 0;
		try{
			lResponse = lBuilder.post(Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);			
			lResponseStatus  = lResponse.getStatus();
		}catch(Exception lException){
			logger.info("Error registerBatch : " + lException.getMessage());
			//lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
			if(pMonetagoData!=null && pMonetagoData.size() > 0){
				for(Object lKey : pMonetagoData.keySet()){
					MonetagoRequiredFieldsBean lRespBean = pMonetagoData.get(lKey);
					if(lRespBean!=null){
						lRespBean.setMessage("Failed to connect to monetago");
					}
				}
			}
			return pMonetagoData;
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		logger.debug("Register Batch Response Status : " + lResponseStatus);
		logger.debug("Register Batch Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		//
		lMRRBean.setResponseDateTime(new Timestamp(System.currentTimeMillis()));
		
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(lResponse.getStatus() == 200){
			List<Map<String,Object>> lResult = (List<Map<String,Object>>) lResponseMap.get(MONETAGO_PARAMETER_PAYLOAD);
			for(Map<String,Object> lLedgers : lResult){
				 lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
				MonetagoRequiredFieldsBean lRespBean = pMonetagoData.get(Long.valueOf((String)lLedgers.get(MONETAGO_PARAMETER_EXTID)));
				if (lRespBean != null ){
					lRespBean.setLedgerId((String) lLedgers.get(MONETAGO_PARAMETER_LEDGERID));
					if(lMRRMapBean.containsKey(Long.valueOf(lLedgers.get(MONETAGO_PARAMETER_EXTID).toString()))){
						lMRRBean = lMRRMapBean.get(Long.valueOf(lLedgers.get(MONETAGO_PARAMETER_EXTID).toString()));
						lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
						lMRRBean.setDuplicateFlag(Long.valueOf(lLedgers.get(MONETAGO_PARAMETER_DUPLICATEFLAG).toString()));
						lMRRBean.setOutputTxnId((String) lLedgers.get(MONETAGO_PARAMETER_LEDGERID));
					}
					lRespBean.setMessage((String) lLedgers.get("Error"));
				}
			}
			try(Connection lConnection = DBHelper.getInstance().getConnection();){
				if(monetagoLoging_Register){
					for(Long lKey : lMRRMapBean.keySet()){
						monetagoRequestResponseDAO.insert(lConnection, lMRRMapBean.get(lKey));	
					}
				}
			} catch (Exception lEx) {
				logger.info("Error while inserting logs in MonetagoRequestResponse Table: " + lEx.getMessage());
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);
			if( lResult.containsKey("details")) {
				Map<String,Object> lDetails = (Map<String,Object>) lResult.get("details");
				throw new CommonBusinessException((String) lDetails.get("ParseError"));
			}
			for(MonetagoRequiredFieldsBean lRespBean : pMonetagoData.values()){
				if( StringUtils.isNotEmpty((String) lResult.get("message")))lRespBean.setError((String) lResult.get("message"));
			}
		}
		return pMonetagoData;
	}
	
	public Map<String, String> factBatch(Map<Long, String> pLegderMap){
		//return testFactor(pLedgerID, pInfoMessage);
		MonetagoRequestResponseBean lMRBean = null;
		List<MonetagoRequestResponseBean> lMRRBeanList = new ArrayList<MonetagoRequestResponseBean>();
		for(Long lKey : pLegderMap.keySet()){
			lMRBean = new MonetagoRequestResponseBean();
			lMRBean.setType(Type.FactorBatch);
			lMRBean.setInId(lKey);
			lMRBean.setInputTxnId(pLegderMap.get(lKey));
			lMRRBeanList.add(lMRBean);
		}
		List<String> lLedgerList =  new ArrayList<String>(pLegderMap.values());
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker("factorBatchJSON");
		lBuilder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
		//CREATE THE DATA TO SEND
		Map<String, Object> lDataHash = new HashMap<String, Object>();
		lDataHash.put("Batch", lLedgerList);
		lDataHash.put(MONETAGO_PARAMETER_OVERIDE, "62");
		//
		String lInputStr = new JsonBuilder(lDataHash).toString();
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + "factorBatchJSON");
		logger.debug("Input : "+lInputStr);
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lResponseStatus = 0;
		try{
			lResponse = lBuilder.method(TARGET_METHOD_TYPE_PATCH, Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);
			lResponseStatus  = lResponse.getStatus();
		}catch(Exception lException){
			logger.info("Error factoredInvoices : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		logger.debug("Fact Batch Response Status : " + lResponseStatus);
		logger.debug("Fact Batch Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		//
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(lResponse.getStatus() == 200){
			 lReturnValue.put(TREDS_RESPONSE_TRANSID, (String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH));
			 lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Success");
			 for(MonetagoRequestResponseBean lMRRBean : lMRRBeanList){
				 lMRRBean.setResponseDateTime(new Timestamp(System.currentTimeMillis()));
				 lMRRBean.setApiResponseStatus(ApiResponseStatus.Success);
				 lMRRBean.setOutputTxnId(((String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH)));
			 }
			try(Connection lConnection = DBHelper.getInstance().getConnection();){
				if(monetagoLoging_Factor){
					monetagoRequestResponseDAO.insert(lConnection, lMRBean);	
				}
			} catch (Exception lEx) {
				logger.info("Error while inserting logs in MonetagoRequestResponse Table: " + lEx.getMessage());
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,String> lResultDetails = (Map<String,String>) lResult.get(MONETAGO_PARAMETER_DETAILS);	
			Map<String,String> lErrorDetails = (Map<String,String>) lResult.get(MONETAGO_PARAMETER_BATCH);	
			Map<String,String> lError = (Map<String,String>) lResult.get("Error");	
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
			lReturnValue.put(MONETAGO_PARAMETER_DETAILS, lErrorDetails.toString());
			lReturnValue.put("Error", lError.toString());
		}
		return lReturnValue;
	}
	
	public  Map<String, String> cancelBatch(List<String> pDataList, CancelResonCode pCancelReasonCode){
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker("cancelBatchJSON");
		lBuilder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
		Map<String, Object> lDataHash = new HashMap<String, Object>();
		lDataHash.put(MONETAGO_PARAMETER_BATCH, pDataList);
		lDataHash.put(MONETAGO_PARAMETER_REASONCODE , pCancelReasonCode.getCode());
		lDataHash.put(MONETAGO_PARAMETER_DETAILS ,"Cancelling group." );
		//
		String lInputStr = new JsonBuilder(lDataHash).toString();
		//
		logger.debug("*****************************************************************************************************");
		//logger.debug("Info : "+pInfoMessage);
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + "cancelBatchJSON");
		logger.debug("Input : "+lInputStr);
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lResponseStatus = 0;
		try{
			lResponse = lBuilder.method(TARGET_METHOD_TYPE_PATCH, Entity.entity(lInputStr, MediaType.APPLICATION_JSON));
			lResponseStr = lResponse.readEntity(String.class);
			lResponseStatus  = lResponse.getStatus();
		}catch(Exception lException){
			logger.info("Error cancelBatch : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
			//return lReturnValue;
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}		
		logger.debug("Cancel Batch Response Status : " + lResponseStatus);
		logger.debug("Cancel Batch Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		//
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(lResponse.getStatus() == 200){
			 lReturnValue.put(TREDS_RESPONSE_TRANSID, (String) lResponseMap.get(MonetagoTredsHelper.MONETAGO_PARAMETER_HASH));
			 lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Success");
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,Object> lResultDetails = (Map<String,Object>) lResult.get(MONETAGO_PARAMETER_DETAILS);		
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
			
		}
		return lReturnValue;
	}

	//these function are for testing etc NOT USED IN CURRENT CODE
	
	public  Map<String,String> factoredInvoices(Date pStartDate, Date pEndDate){
		Map<String,String> lReturnValue = new HashMap<String, String>();
		
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker("factoredInvoices/" + FormatHelper.getDisplay("yyyy-MM-dd", pStartDate) +"/"+ FormatHelper.getDisplay("yyyy-MM-dd", pEndDate));
		//CREATE THE DATA TO SEND
		String lData = "{"+ pStartDate + "}/{" + pEndDate + "}";
		//
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + "factoredInvoices");
		logger.debug("Input : "+lData);
		
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lResponseStatus = 0;
		try{
			lResponse = lBuilder.get();
			lResponseStr = lResponse.readEntity(String.class);
			lResponseStatus  = lResponse.getStatus();
		}catch(Exception lException){
			logger.info("Error factoredInvoices : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		logger.debug("Response Status : " + lResponseStatus);
		logger.debug("Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		//
		 JsonSlurper lJsonSlurper = new JsonSlurper();
		 Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		//UPDATE THE INPUT AS PER THE RESPONSE
		 return lReturnValue;
	}
	
	public  boolean checkConnectivity(Date pStartDate, Date pEndDate){
		Map<String,String> lReturnValue = new HashMap<String, String>();
		
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker("factoredInvoices/" + FormatHelper.getDisplay("yyyy-MM-dd", pStartDate) +"/"+ FormatHelper.getDisplay("yyyy-MM-dd", pEndDate));
		//CREATE THE DATA TO SEND
		String lData = "{"+ pStartDate + "}/{" + pEndDate + "}";
		//
		//
		logger.debug("*****************************************************************************************************");
		logger.debug("Target : "+JerseyClient.getInstance().baseUri + "/" + "factoredInvoices");
		logger.debug("Input : "+lData);
		
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		int lResponseStatus = 0;
		try{
			lResponse = lBuilder.get();
			lResponseStr = lResponse.readEntity(String.class);
			lResponseStatus  = lResponse.getStatus();
			if(lResponseStatus == 200){
				return true;
			}
		}catch(Exception lException){
			logger.info("Error factoredInvoices : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		logger.debug("Response Status : " + lResponseStatus);
		logger.debug("Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		//
		 JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		//UPDATE THE INPUT AS PER THE RESPONSE
		 return false;
	}
	
	
	
	public Map<String,String> getInvoiceByLedgerId(String pLedgerID){
		//return testCancel(pLedgerID, pResaonCode, pInfoMessage);
		Map<String,String> lReturnValue = new HashMap<String, String>();
		Invocation.Builder lBuilder = JerseyClient.getInstance().getRequestInvoker("getInvoiceByLedgerID/"+pLedgerID);
		lBuilder.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
	
		logger.debug("*****************************************************************************************************");
		logger.debug("Traget : "+"getInvoiceByLedgerID/"+pLedgerID);
		//
		//SEND AND RECEIVE THE RESPONSE
		Response lResponse= null;
		String lResponseStr = null;
		try{
			lResponse = lBuilder.get();
			lResponseStr = lResponse.readEntity(String.class);
		}catch(Exception lException){
			logger.info("Error factoredInvoices : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
		}finally{
			if(lResponse!=null){
				lResponse.close();
			}
		}
		logger.debug("Response Status : " + lResponse.getStatus());
		logger.debug("Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(lResponse.getStatus() == 200){
			List<Map<String,Object>> lResult = (List<Map<String,Object>>) lResponseMap.get(MONETAGO_PARAMETER_PAYLOAD);
			int lCount=1;
			logger.info( " Monetago Duplicate Flag : " +  TredsHelper.getInstance().getDuplicateFlagValue()  );
			for(Map<String,Object> lDataHash : lResult){
				logger.info( " DataHash : " +  (lCount++)  );
				if ( Long.parseLong(lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString())==MONETAGO_STATUS_FACTORED){
					logger.info("self");
					 lReturnValue.put(TREDS_RESPONSE_LEDGERID, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
					 lReturnValue.put(TREDS_RESPONSE_FACTSTATUS, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString());
					 break;
				}else{
					logger.info("other");
					List<Map<String,Object>> lDuplicates = (List<Map<String,Object>>)lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_DUPLICATES);
					int lDuplicate = 0;
					for(Map<String,Object> lDuplicateHash : lDuplicates){
						logger.info("other : duplicate : " + (lDuplicate++));
						if (TredsHelper.getInstance().checkDuplicateFlag(Long.parseLong(lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_DUPLICATEFLAG).toString()))){
							logger.info("duplicate flag match");
							if ( Long.parseLong(lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString())==MONETAGO_STATUS_FACTORED){
								logger.info("status flag match");
								lReturnValue.put(TREDS_RESPONSE_LEDGERID, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
								lReturnValue.put(TREDS_RESPONSE_DUPLICATELEDGERID, lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
								lReturnValue.put(TREDS_RESPONSE_FACTSTATUS, lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString());
								break;
							}
						}
					}
				}
			}
		}else{
			Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,Object> lResultDetails = (Map<String,Object>) lResult.get(MONETAGO_PARAMETER_DETAILS);		
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
			
		}
		return lReturnValue;
    }
	
    public static void main(String args[]) throws Exception {
        try {
            BeanMetaFactory.createInstance(null);
            
        	BigDecimal lAmount = new BigDecimal(10000);
            //MonetagoTredsHelper.getInstance().testRegisterXInvoices(lAmount);
            //MonetagoTredsHelper.getInstance().testCancelAndRegisterXInvoicesThread(lAmount);
            //
            //MonetagoTredsHelper.getInstance().cancel("4ba7105324078abcc9e06e14c78f035c5cd5938fcf1344cc6ccccccb6b1f9e72",CancelResonCode.NotFinanced, "",new Long("2003170000368"));
            //cancelInvoice("0a09847d94969f0a6ad1483f1e0f8e1d8a40d55f6504c4cc164e25aed9165bfb", new Long(1));
            //registerXInvoices();
        	MonetagoTredsHelper.getInstance().factor("5eecd314d99ca27174d40359c1cc4c7cd5d45d77846e67432edd471ef2c5e9ab", "", new Long("2003190000093"));
        } catch(Exception e) {
               System.out.println(e);
        }
    }
    
    private void testRegisterXInvoices(BigDecimal pAmount) {
    	int lPtr=1;
    	List<String> lCancelLedgerIds = new ArrayList<String>();
    	MonetagoTredsHelper lMonetagoTredsHelper = MonetagoTredsHelper.getInstance();
    	//
    	lCancelLedgerIds.add(""); //zeorth position
    	for(lPtr=1; lPtr <=100; lPtr++) {
    		lCancelLedgerIds.add(lMonetagoTredsHelper.testRegisterInvoice(pAmount, lPtr));
    	}
    	//
    	StringBuilder lLedgerIds = new StringBuilder();
    	for(String lId : lCancelLedgerIds) {
    		lLedgerIds.append("\"").append(lId).append("\"").append(",");
    	}
    	//
    	System.out.println("**************************MY CANCELLATION LIST************************");
    	System.out.println(lLedgerIds);
    	System.out.println("**************************************************");
    }

    
    private  void testCancelAndRegisterXInvoicesThread(BigDecimal pAmount) {
    	int lPtr=1;
    	String[] lCancelledLedgerIds = new String[] {"","c68e27e862a14adfe51ed7970baf4740bda7cae13f6bd9e81782d40cc68aba44","6588b4647f2b9c7d6eadcf6e5dffd89d6e36d0591e8bda9c1de95cfb98b6de40","cd5fa7839a6a7af180f0817bbdeaebdec8ea5b4975edcc4c6d45d15508e067a6","2455a85d7bbbb74db97be758fe6c08c419ec922e493c86e8c4332c900ed8c622","71123798197d8f697a5861c0c305b12e3c544578bbfc6db4d487442ee5b9cb19","4594304aad0025d417cdf4eecb2e500dc37eaed95f0ea2210d4f2e651fe32864","c361e56e6c8ffe119f7265e2567838b540dc3726c34a1ba4be39284011f659cb","f1f37cfaa5eb08a4558b8912fa9d170afa5e2044d8ba4b59475131e86bd7c576","ee9e414cc17a95602c8fb6f90aa03d99475cd21e9acff39301674c030b0a3f62","3c6b99a94a5d551efd56a3ffb3d1590329ac9f87a5f4cda2bd964bf69f1be157","b983c449979b2c4f95b7056dca8695ab8bb2a986a21cf01cc73a310f3130f343","c4d8ae76cceb49119b60f9e6ec78da64a460ef692db2e8112c1a5fc8f7ade28d","9734dbc5b7b55e60737e1d12e1f16a989d5b62fccac6a8f133b6559ea34f8d15","1ec2ff3a20d7b0439440bb687ebe1135e215e38cc572e1c036831ab054f5e3af","82e001b68b918f6f71d54b6a0c0c2e5f4fd376ecef11927fd20d62204d1d5835","d855a8913422589f06d0ae012973f5b634d035e518757899e30a792b2fbffc5f","d367ff982312f6c3e6742e0a83946a1ec79afed0cbbc32de86177cf4c7d33769","d4083a804376dacbc2cae58f00070a2f4ff790e5915c1926d8278ad10cbef515","830b802bcdc09452b9a394d44518d64220f2aa992865e03e36d57e7c5c648742","e65897039d84de940d945953e8cb1b24cf12e2a5c9a7613ed857d7a447838a9e","94911e3048bba2052aaf185651357062491019b4780964b37752c0f8ee43b19b","0a86fb604ec3cb7ca0e466e41fe0a8c2acfde8669edde9d15f68b6e69e34a583","f2dcd1706f8092137924ca6f5322015895c46d0ef9edec5406a3ba56e4b7a790","54c359743cb32208d8eb1d9766ec1c77bb51d1e380379a18dbc2c75f216c3d64","2bcb8be4b7dedc20e68a5d33e91ac986d10d29e37bd55b1b44f74f07907c634b","b79ab95bda34d52512bf0b5257870f830722f5a37c1b2a9ae383c457678eb58c","1e945196d5c97969d82ee5480804eae0ff97a40bf3206d7b48742d616f7222e3","c90bb3af80410665217e648f53ad787d6260099a3745299927d523330271f6f2","fcc3e13c97e3283dc63878592b21bde5d4b04455e4bcc3c52dd3ae11e4560451","88eb2d8437cc9871392a98c72128bab3c6225f9df50a97316ff8c1c5d6d20090","a2b2667ec97425e3be4d8e9faa559980c17730f5b325bfd08141a91f9aaee6be","770e7f7e258304c3e97eb305c2e55e986e69e5eefeb832bbbebe503dba23bf4a","15909c76a932c0dae295faf8fbbbcb20d32e90b47a79231773da2a536a2e558b","aed30f0246ad2c249b0abaedb2b4e4dfbd8ad9023706d8855dae2cd2ccbb1e51","c79283209022f185a18ef06e9b659c271692220a52d79a97544c873a932dffab","d81273d9d1bc52b991bcc78a71c98756567cea5e4676b895f34ef083fdbf0331","2626b971dccafc15058144d565d03e7def560d2d73630474537d4101ae89aff8","3b81f23222fecc5f1f9cec53b59d6d48171eb3a6701d56d604850343b24e4061","bab0fab22121dcf89e8ac33b8494d35e7a5d82db02b380f0c7a4ca02f1767041","5ac6a9f59bf9f7132e194ab08c686e1860960a153306f5e9c636aef4753fcaf2","37d30b86bb5a7dd6f10bcde4704fdcdc0f676fb9450b54e3026bc495d3450256","4a1b07ebb1aeefb67b63960485c065043ce5a11386eac5647da4246456bf4ab3","a0a3cd06e12a17f8908ecf46eecd1f6f8cdad0a386ba6a1840255d069f9fa98a","afcf145c995f4a72f9d2a1b8bc4b283345e9cc47d2f2c5e5fdcf8a5fe7ddaaea","ce6a835e76320b0e9310f8c2bbea58277bef3839a80a0fe9c069da779a621a38","00cce8b52e460610a26b728f9d5726a6ec29576a2c8d5e85ee017c794b2708f3","ad0bf542474da1267c2ea75a30ba659ec1deb03de431614a5a57a3ec0cc4bf62","e4c51fe7f5dd57df0caa0dec11d18bb319343bf7c48e990476d0c6ba3f973f3b","d5aa3904acdcd1abc8e691ab020f3009f1a4a04d05faf73fd2fbae08b7135c21","a47a04f349aa0ee4db7bdbd4a2e8db480421a50ba04f182f7355ba7f1b14b406","5296a31e4c3186d2372986ea3c01724a04a834c37f659bdcfa869c5045838c89","1e3d593b1e309ad86555532a43b118be36c08ec5932b7039b0ea753e3ad58b49","f5e1516f58853414b662dc0f5e46b38919978dea11181e73a33be1ed192bb56a","38d32f260a9285394dee0a9c69c1f6821c944285118326a93b4598be1703ddbb","cf8fa2601d16e6702f19b9a2c08915e10666ca7a04f5c19584281abaa8b5c359","90c375fffcfaaf4079311f89da39b0533b90f2697c844d58fc0d19006f121c6a","30dbf915fca7292b30c4d12abf79b0d4cc6c397eceeda27530a6a75f967bb8a9","7d369561f475f6baa2d08dd2df46b31e9d79cbc5b5b9c7b69aa7950c7eaf95af","6141fb4adf951cc7a30be8b613817cf85cb9a3c0da2eb1cd0a1f45ac77ed1bf0","cd3c6d9007ccd77f103f8016dcee703f489cf11676ae3cb751abecee3f5bde40","152f3d84fef3a9f51a6eebab0752b2aa2fee142f8419ae44e943d0095a9986ce","c49d38d8f02543bfb9d8d08487de8dcfced903e54e4c05a759a91bb7b4a65a67","94b5eb2e6ea037b6421d97134de1425707f2530de4747e25b253b24ce7018aad","0bb6a05f76a8fd8038c8f76c888b02ad498c131364041234bd678181619d2acf","672464bb437f35e8ac5ccd11ef6f0c2f88986cf9b0df6813911064e9b089a36b","55614e5c7fa354d95742c1f62ef5300e1922c827402a4a525229ff023e767bb2","883a468b89f919dc0fd9c1cc97ad88ad50df438528d78f8339a4b500fa5d3e8d","2589d72e3e01a88ace2aac0e8281cdcf9c5f06e47f47a0054d398781ef524600","c50d94cbc3cccaf6d9aef186b56d3f20b5d9f1438e88ca90aa5606a3d38726d0","3f1825b211bec9e9d7eb2f1a2df31a83a983f15a68a96b057e660290b23d1803","2fa5c9c909dae1e399a2a3a6293cdaea0eb4f1905644bffad4729f876a930552","89f909637e8607d5484ffe1b2965fbefdedb0af0fb69b3368b5705f8486af0c4","48577a56db7041b56bcfbca7c959763b286194c66ddb665066ac42f797ff62b4","a3e4ee48ad329d91c7970d9e5b2c6fd2ba8d3171b4a98446498cf2869cbf9aa6","d8db573e367d13b027332894e1feae0b1529adcda1b84b8f8f4d657d4de54188","8efedb69b5de85b8fafaf860f004ce37a13654c4640cbf3c573472c076f93c7b","d8cb281f85490c320be42ec5c18dcb5c4312857e8200a8761269f9f9d855268f","08dd911dc770101206a343939d42d191f64768d8ce579bd64082ede81daa920b","410001c8ff7bb966a56468c8bfcc219f08f7b02f2c8521acd40fa7d0d16b5c4b","a379c56957d4cfc1efe2f6bbd276af7104a66b923fafd0bb50c8e5d539f17a98","8d8102872da3a69e1578bde1e99045ac4f2f1bd3000fea10ba084d46ac5464bc","14cc68478a3c3d7c0ce6cc67056e5b37a5ba3106a623507650ee565699d819a2","b8f1066bf84c21e4c1370cc6b6a3a391c0011348bbd322ddf2f59be17a41fb16","574551d34d80887e402f301410bffc639c2866c9118bfaf35bb7ea4b4e0f7226","56ea2a8d3b28577c1e804dcc6631bd8f16747073be1c2c94544c02f6dc94de9c","df3e2182fe321a87abd7f898d78df759efc58d7c453cffe33998127672dddf7a","bb2a6d826242be1030666d5ecfa2f0a0fee2101cc586908eb1c54d6dc0044625","66ad7b9c46c63c7072221750b0046ee37c4e43adbdbc0aa54bb5b38a2d4af3c0","370696ec4a22911e627be5fd091aa827a2d92856cfeb4533fada0c13beeae900","8bf695760ca4528f23aa07c709ab0716dda6daec606c02113bc6168bc9d32e23","e895eaf3dff5575a81112a81b5fb9831494160f1dc58c64924644ce1cd1543c1","01858b96a54001c27cc90a326a2767c23a50d50154878abfe2eb9c95a652eb12","46820b11a67e1849a6236f01c39be6d82fc45148b868b18851cd6383e8978c13","d89eccb987820fffd5f66728ab762798b956a0a7a71bd513cee45bc525b3c9fa","da49a85ee0acca008186483b3eafe0caced0d00f88c8132994369e979470db25","d99ad2a17a4100f364ba601f4414b3335809c8c48e1cd24b67105cd5a7095921","3bd419ffa1c80c2dc3b3c7e7211841eb40c629250f6a53c5ab66ac0b64115563","027abd5b1c512f6749946a10544722c9c988569ab7a2aebdabe27fa1d2afedeb","3e5ae78da32b321c01a90ab3eef9a0d169b7b094e1bd9696482dce1456ab0117","17aad7738e00421fd79bc9686e7246a89571c8425a7902afe23abec8f2123459"};
    	//
    	for(lPtr=1; lPtr <=100; lPtr++) {
    		MyRunnable myRunnable = new MyRunnable(lPtr, lCancelledLedgerIds[lPtr], pAmount);
	        Thread t = new Thread(myRunnable);
	        t.start();
    	}
    }
    
    
    private String testRegisterInvoice(BigDecimal pAmont, int pInstPtr) {
    	MonetagoTredsHelper lMonetagoTredsHelper = MonetagoTredsHelper.getInstance();
    	Date lDate = CommonUtilities.getDate("20-01-2020", "dd-MM-yyyy");
    	Map<String,String> lRetVal = null;
    	//
    	//register(pSupplierGSTN, pPurchaserGSTN, pInstNumber, pInstDate, pAmount, pInfoMessage, pInId)
		lRetVal = lMonetagoTredsHelper.register("05ABCDE1234F2Z5", "22CQTTB4054U1Z8", "INST2400"+pInstPtr, lDate , pAmont ,"INFO"+pInstPtr, new Long(pInstPtr));
		//logger.info(lRetVal);
		return lRetVal.get("LedgerID");
    }
    
    private String testCancelInvoice(String pLedgerId, Long pInId) {
    	MonetagoTredsHelper lMonetagoTredsHelper = MonetagoTredsHelper.getInstance();
    	Map<String,String> lRetVal = null;
    	lRetVal =  lMonetagoTredsHelper.getInstance().cancel(pLedgerId, MonetagoTredsHelper.CancelResonCode.Withdrawn , "Some cancel msg", new Long(pInId));
    	logger.info(lRetVal);
    	return lRetVal.get("transId");
    }
    
    
    public static void prevTest() {
//    	MonetagoTredsHelper.getInvoiceByLedgerId();
//    	MonetagoTredsHelper lMonetagoTredsHelper = MonetagoTredsHelper.getInstance();
//    	System.out.println(lMonetagoTredsHelper.lMonetagoLogingSetting);
  	  	//lMonetagoTredsHelper.allInvoices("allInvoices/2018-03-01/2018-03-14");
		//List<String> lList = new ArrayList<String>();
		//lList.add("2c043884881ca5da301aec1d9dfdf3d5983d40673f229b7217ec951a3c95be22");        
		//lMonetagoTredsHelper.factBatch(lList);
		//lMonetagoTredsHelper.register("05ABCDE1234F2Z5", "22CQTTB4054U1Z8", "INST2346", CommonUtilities.getDate("01-07-2018", "dd-MM-yyyy") , BigDecimal.valueOf(154001000,2) ,"INST2346-1807160000031");
    	//lMonetagoTredsHelper.factor("", "INST2346-1807160000031");
		//lMonetagoTredsHelper.registerBatch(null);
    	//lMonetagoTredsHelper.factoredInvoices();
//       	lMonetagoTredsHelper.getInvoiceByLedgerId("86199b885fc1f9e2ea81f188b42225c2b169e247fecc00af8913cd1aa6c12536");
        //lMonetagoTredsHelper.getInvoiceByLedgerId("ac00c149e3db66f836ca2f2b05cf6f808dba3483619fa273e4a10ab36dda45b7");
		//testBits();
		
    }
    
    public static void testDuplicate(){
    	int lMonetagoFlag = 6;
        System.out.println("MONETAGO_DUPLICATE_FLAG_VALUE : "+ lMonetagoFlag);
    	for(int lPtr=1; lPtr < 33; lPtr++){
    		if(((lMonetagoFlag | lPtr) == lMonetagoFlag))
    			System.out.println(lPtr +" : "+ ((lMonetagoFlag | lPtr) == lMonetagoFlag) );
        }
    }
    public static void testBits(){
    	int MONETAGO_DUPLICATE_FLAG_VALUE = 13;
    	int pDuplicateFlagValue = 8;
    	System.out.println((MONETAGO_DUPLICATE_FLAG_VALUE & 1 << pDuplicateFlagValue));
    }
    public static String getISO8601FormattedDate(Timestamp pDate){
    	String lRetDate = "";
        lRetDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+05:30'").format(pDate);
    	return lRetDate;
    }
    public static Map<String,String> getInvoiceByLedgerId(){
		Map<String,String> lReturnValue = new HashMap<String, String>();
		String lResponseStr = null;
		try{
			lResponseStr = CommonUtilities.readFile("d:\\temp\\getInvoiceByLedgerId.response.txt");
		}catch(Exception lException){
			logger.info("Error factoredInvoices : " + lException.getMessage());
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, "Failed to connect to monetago");
		}finally{
		}
		logger.debug("Response readEntity : " + lResponseStr);
		logger.debug("*****************************************************************************************************");
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lResponseMap = (Map<String, Object>)lJsonSlurper.parseText(lResponseStr);
		if(true){
			List<Map<String,Object>> lResult = (List<Map<String,Object>>) lResponseMap.get(MONETAGO_PARAMETER_PAYLOAD);
			int lCount=1;
			logger.info( " Monetago Duplicate Flag : " +  TredsHelper.getInstance().getDuplicateFlagValue()  );
			for(Map<String,Object> lDataHash : lResult){
				logger.info( " DataHash : " +  (lCount++)  );
				if ( Long.parseLong(lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString())==MONETAGO_STATUS_FACTORED){
					logger.info("self");
					 lReturnValue.put(TREDS_RESPONSE_LEDGERID, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
					 lReturnValue.put(TREDS_RESPONSE_FACTSTATUS, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString());
					 break;
				}else{
					logger.info("other");
					List<Map<String,Object>> lDuplicates = (List<Map<String,Object>>)lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_DUPLICATES);
					int lDuplicate = 0;
					for(Map<String,Object> lDuplicateHash : lDuplicates){
						logger.info("other : duplicate : " + (lDuplicate++));
						if (TredsHelper.getInstance().checkDuplicateFlag(Long.parseLong(lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_DUPLICATEFLAG).toString()))){
							logger.info("duplicate flag match");
							if ( Long.parseLong(lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString())==MONETAGO_STATUS_FACTORED){
								logger.info("status flag match");
								lReturnValue.put(TREDS_RESPONSE_LEDGERID, lDataHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
								lReturnValue.put(TREDS_RESPONSE_DUPLICATELEDGERID, lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_LEDGERID).toString());
								lReturnValue.put(TREDS_RESPONSE_FACTSTATUS, lDuplicateHash.get(MonetagoTredsHelper.MONETAGO_PARAMETER_STATUS).toString());
								break;
							}
						}
					}
				}
			}
		}else{
		/*	Map<String,Object> lResult = (Map<String,Object>) lResponseMap.get(MONETAGO_PARAMETER_ERROR);		
			lReturnValue.put(TREDS_RESPONSE_LEDGERID, "");			
			StringBuilder lMessage = new StringBuilder();
			lMessage.append((String)lResult.get(TREDS_RESPONSE_MESSAGE)).append("<br>");
			Map<String,Object> lResultDetails = (Map<String,Object>) lResult.get(MONETAGO_PARAMETER_DETAILS);		
			if(lResultDetails!=null){
				for(String lKey : lResultDetails.keySet()){
					lMessage.append(lKey).append(" : ").append(String.valueOf(lResultDetails.get(lKey))).append("<br>");
				}
			}else{
				lMessage.append(" No Details.");
			}
			lReturnValue.put(TREDS_RESPONSE_MESSAGE, lMessage.toString());
			*/
		}
		return lReturnValue;
    }

    public class MyRunnable implements Runnable {

        private int instCounter;
        private String ledgerId = null;
        private BigDecimal amount;

        public MyRunnable(int pInstCounter, String pLedgerId, BigDecimal pAmount) {
            this.instCounter = pInstCounter;
            this.ledgerId = pLedgerId;
            this.amount = pAmount;
        }

        public void run() {
        	MonetagoTredsHelper.getInstance().testCancelInvoice(ledgerId, new Long(instCounter));
        	MonetagoTredsHelper.getInstance().testRegisterInvoice(amount, instCounter);
        }
    }


}
