package com.xlx.treds;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.http.bean.ApiRequestBean;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.commonn.http.client.RestClient;
import com.xlx.treds.monetago.bean.GstnMandateBean;
import com.xlx.treds.monetago.bean.MonetagoEwaybillInfoBean;
import com.xlx.treds.monetago.bean.MonetagoInvoiceInfoBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class InstAuthJerseyClient {
	//
	public static Logger logger = Logger.getLogger(InstAuthJerseyClient.class);
	private static InstAuthJerseyClient theInstance;
	// this will come from regisry
	//	test.key = api 
	//	test.secret = api
	//	test.URL = https://rxil.api.ia.monetago.com/api
	public static String baseUri = "https://rxil.api.ia.monetago.com/api";
	private static String API_SECRET = "api";
	private static String API_KEY = "api";
	//
	private static String PARAM_API_KEY = "X-API-KEY";
	private static String PARAM_API_PAYLOAD = "X-API-PAYLOAD";
	private static String PARAM_API_SIGNATURE = "X-API-SIGNATURE";
	private static String PARAM_NONCE = "nonce";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA384";
	private static String proxyIp = "172.20.7.21";
	private static Long proxyPort = new Long(8080);
	//
	private static String QUERY_PARAM_SUPPLIER_GSTN = "supplier_gstin";
	private static String QUERY_PARAM_INVOICE_ID = "invoice_id";
	private static String QUERY_PARAM_DATA_TYPE = "data_type";
	private static String QUERY_PARAM_DATA_TYPE_GSTR_VALUE = "gstr1";
	private static String QUERY_PARAM_DATA_TYPE_EWAY_VALUE = "eway";
	//
	public static String JSON_KEY_EWAY_PAYLOAD = "payload";
	public static String JSON_KEY_EWAY = "ewaybill";
	public static String JSON_KEY_GSTR_PAYLOAD = "payload";
	public static String JSON_KEY_GSTR = "gstr1";
	//
	public static String JSON_KEY_ERROR = "error";
	public static String JSON_KEY_MESSAGE = "message";
	public static String JSON_KEY_PAYLOAD = "payload";
	public static String JSON_KEY_BATCH = "batch";
	public static String JSON_KEY_INVOICE_LIST = "invoice_list";
	public static String JSON_KEY_AUTHENTICATION_STATUS = "authentication_status";
	public static String JSON_KEY_STATUS_CODE = "status_code";
	public static String JSON_KEY_LOOKUP_RESULT_LIST = "lookup_result_list";
	public static String JSON_KEY_NAME= "name";
	public static String JSON_KEY_LOOKUP_RESULT_LIST_EWAY = "eway";
	public static String JSON_KEY_RESULT = "result";
	public static String JSON_KEY_REGISTERED = "registered";
	public static String JSON_KEY_MATCHED = "match";
	public static String JSON_KEY_INVALID_FIELD_LIST = "invalid_field_list";
	public static String JSON_KEY_REQUEST_ID = "request_id";
	public static String JSON_KEY_FIELD = "field";
	public static String JSON_KEY_USER_PROVIDED_VALUE = "user_provided_value";
	public static String JSON_KEY_REGISTERED_VALUE = "registered_value";
	
	public static Long MONETAGO_STATUS_FAILED = new Long(0);
	public static Long MONETAGO_STATUS_WARNING = new Long(1);
	public static Long MONETAGO_STATUS_PASSED = new Long(2);
	public static Long MONETAGO_STATUS_PARTIALLY_AUTHENTICATED = new Long(3);
	public static Long MONETAGO_STATUS_PENDING = new Long(4);
	
	public GenericDAO<GstnMandateBean> gstnMandateDAO;
	
	private static final Map<String,String> ewayFieldDescriptions = getEwayFieldDesc();
	//
	public static InstAuthJerseyClient getInstance() {
		if (theInstance == null) {
			synchronized (InstAuthJerseyClient.class) {
				if (theInstance == null) {
					try {
						InstAuthJerseyClient tmpTheInstance = new InstAuthJerseyClient();
						theInstance = tmpTheInstance;
					} catch (Exception lException) {
						logger.fatal("Error while instantiating JerseyClient", lException);
					}
				}
			}
		}
		return theInstance;
	}

	protected InstAuthJerseyClient() {
		//
		HashMap<String, Object> lMonetagoSettings = new HashMap<String,Object>();
		gstnMandateDAO = new GenericDAO<GstnMandateBean>(GstnMandateBean.class);
		if(MonetagoTredsHelper.getInstance().isDisasterEnabled()){
			lMonetagoSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_DESMONETAGO_IA);	
		}else{
			lMonetagoSettings= (HashMap<String, Object>) RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_MONETAGO_IA);
		}
		//VALUES
		baseUri = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_BASEURI);
		API_SECRET = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_VALUE_SECRET);
		API_KEY = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_VALUE_APIKEY);
	
		PARAM_API_KEY = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APIKEY);
		PARAM_API_PAYLOAD = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APIPAYLOAD);
		PARAM_NONCE = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APINONCE);
		PARAM_API_SIGNATURE = (String) lMonetagoSettings.get(AppConstants.ATTRIBUTE_MONETAGO_NAME_APISIGNATURE);
		proxyIp = TredsHelper.getInstance().getProxyIp();
		proxyPort = TredsHelper.getInstance().getProxyPort();

        //PRODUCTION SETTINGS
//        baseUri = "https://api.ia.monetagosolutions.com/api";
//        API_SECRET = "e6f86929-675c-4192-a28c-ac93f0ad1f3a";
//        API_KEY = "83e8bc6f-0e15-452c-93c8-402bf6878ce5";
	}
	
	private void setAuthHeaders(ApiRequestBean lApiRequestBean){
		// add key
		lApiRequestBean.setHeaders(PARAM_API_KEY, API_KEY);
		// add payload
		Map<String, Object> lPayLoadHash = new HashMap<String, Object>();
		lPayLoadHash.put(PARAM_NONCE, (CommonUtilities.getCurrentDateTime().getTime()));
		String lPayLoad = Base64.encodeAsString((new JsonBuilder(lPayLoadHash)).toString());
		lApiRequestBean.setHeaders(PARAM_API_PAYLOAD, lPayLoad);
		// add signature
		lApiRequestBean.setHeaders(PARAM_API_SIGNATURE, getSignature(lPayLoad));
	}
	
	private String getSignature(String pPayLoad){
		Mac mac = null;
		try {
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// init with key
		SecretKeySpec key = new SecretKeySpec(API_SECRET.toString().getBytes(), HMAC_SHA1_ALGORITHM);
		try {
			mac.init(key);
			mac.update(pPayLoad.getBytes());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		byte[] digest = mac.doFinal();
		String lSignature = getHexString(digest);
		return lSignature;
	}

	private static String getHexString(byte[] mdbytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
	
	public String getRegisteredSupplierPayLoad(String pGstn) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//
		lApiRequestBean.addParam(QUERY_PARAM_SUPPLIER_GSTN, pGstn);
		//	
		logger.info("************ REQUEST ");
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		//
		logger.info("************ RESPONSE ");
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("lender/get-supplier/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
		//
		return lApiResponseBean.getResponseText();
	}
	
	public boolean isSupplierRegistered(String pGstn, String pPayLoad){
		JsonSlurper lJsonSlurper = new JsonSlurper();
		if(CommonUtilities.hasValue(pPayLoad)){
			Map<String,Object> lTempJson = (Map<String, Object>) lJsonSlurper.parseText(pPayLoad);
			if(lTempJson!=null && !lTempJson.isEmpty()){
				lTempJson = (Map<String,Object>) lTempJson.get("payload");
				if(lTempJson!=null && !lTempJson.isEmpty()){
					lTempJson = (Map<String,Object>) lTempJson.get("supplier");
					if(lTempJson!=null && !lTempJson.isEmpty()){
						Object lTmp = lTempJson.get("supplier_gstin");
						if(lTmp!=null){
							return (((String)lTmp).equals(pGstn));
						}
					}
				}
			}
			//
		}
		return false;
	}
	
	public boolean registerSupplier(Connection pConnection,String pGstn, List<String> pEmails,AppUserBean pAppUserBean) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		setAuthHeaders(lApiRequestBean);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		String lErrorMessage = "";
		//
		Map<String,Object> lTempJson = new HashMap<String,Object>();
		lTempJson.put("supplier_gstin", pGstn);
		lTempJson.put("email_list", pEmails);
		//TODO: TEMP FOR TESTING
		//lTempJson.put("org_name", "DD COTTON PRIVATE LIMITED");
		lApiRequestBean.setBody( new JsonBuilder(lTempJson).toString() );
		//	
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("lender/add-supplier/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
		if(lApiResponseBean.getStatusCode() == 200){
			GstnMandateBean lFilterBean = new GstnMandateBean();
			lFilterBean.setSupplierCode(pAppUserBean.getDomain());
			lFilterBean.setGstn(pGstn);
			GstnMandateBean lGstnMandateBean = gstnMandateDAO.findBean(pConnection, lFilterBean);
			if (lGstnMandateBean == null){
				lGstnMandateBean = new GstnMandateBean();
				lGstnMandateBean.setSupplierCode(pAppUserBean.getDomain());
				lGstnMandateBean.setGstn(pGstn);
				lGstnMandateBean.setStatus(GstnMandateBean.Status.Pending);
				lGstnMandateBean.setAuId(pAppUserBean.getId());
				gstnMandateDAO.insert(pConnection, lGstnMandateBean);
			}
			return true;
		}else{
			if(CommonUtilities.hasValue(lApiResponseBean.getResponseText())){
				Map<String,Object> lJsonMap = (Map<String, Object>) lJsonSlurper.parseText(lApiResponseBean.getResponseText());
				lJsonMap = (Map<String, Object>) lJsonMap.get(JSON_KEY_ERROR);
				lErrorMessage = lJsonMap.get(JSON_KEY_MESSAGE).toString();
			}
			throw new CommonBusinessException("Error while registering gstn : " + pGstn + " : "+ lErrorMessage);
		}
	}

	public boolean uploadInvoice(String pGstn, MiniInstrument pMiniInstrument, List<String> pEmails) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		setAuthHeaders(lApiRequestBean);
		//
		Map<String,Object> lTempJson = new HashMap<String,Object>();
		
		lTempJson.put("supplier_gstin", pGstn);
		lTempJson.put("lookup_gstr1", false);
		lTempJson.put("lookup_eway", true);
		lTempJson.put("lender_user_id", "lender_org_user");
		lTempJson.put("lender_email_list", pEmails);
		List<Object> lMiniInsts = new ArrayList<Object>();
		Map<String,Object> lMini = new HashMap<String,Object>();
		lMini.put("buyer_gstin", pMiniInstrument.getBuyerGstn());
		lMini.put("invoice_id", pMiniInstrument.getInvoiceId());
		lMini.put("invoice_issue_date", pMiniInstrument.getInvoiceIssueDate());
		lMini.put("amount", pMiniInstrument.getAmount());
		lMini.put("eway_id", pMiniInstrument.getEwayId());
    	  //lMini.put("external_id", lMiniInst.getBuyerGstn());
		lMiniInsts.add(lMini);
		//
		lTempJson.put("invoice_list",lMiniInsts);
		//
		lApiRequestBean.setBody( new JsonBuilder(lTempJson).toString() );
		//	
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("upload-batch/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
		if(lApiResponseBean!=null){
			Map<String,Object> lResponse = (Map<String,Object>) lApiResponseBean.getResponseJson();
			if (lApiResponseBean.getStatusCode()==200){
				if (lResponse.containsKey(JSON_KEY_PAYLOAD)){
					Map<String,Object> lTmpMap = (Map<String, Object>) lResponse.get(JSON_KEY_PAYLOAD);
					if (lTmpMap.containsKey(JSON_KEY_REQUEST_ID)){
						return getBatch(lTmpMap.get(JSON_KEY_REQUEST_ID).toString());
					}else{
						throw new CommonBusinessException("Error in upload-batch : request id missing");
					}
				}else{
					throw new CommonBusinessException("Error in upload-batch : payload missing");
				}
			}else {
				if (lResponse.containsKey("error")){
					Map<String,Object> lTmpMap = (Map<String, Object>) lResponse.get("error");
					if (lTmpMap.containsKey("message")){
						throw new CommonBusinessException(lTmpMap.get("message").toString());
					}
				}
			}
			//check whether the upload was succesfull on basis of the return json and then send back true
			//or else if it is already uploaded then also send true
		}
		return false;
	}
	public void uploadBatch(String pGstn, List<MiniInstrument> pMiniInstruments, List<String> pEmails) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		setAuthHeaders(lApiRequestBean);
		//
		Map<String,Object> lTempJson = new HashMap<String,Object>();
		
		lTempJson.put("supplier_gstin", pGstn);
		lTempJson.put("lookup_gstr1", false);
		lTempJson.put("lookup_eway", true);
		lTempJson.put("lender_user_id", "lender_org_user");
		lTempJson.put("lender_email_list", pEmails);
		List<Object> lMiniInsts = new ArrayList<Object>();
		for(MiniInstrument lMiniInst : pMiniInstruments){
			Map<String,Object> lMini = new HashMap<String,Object>();
			lMini.put("buyer_gstin", lMiniInst.getBuyerGstn());
			lMini.put("invoice_id", lMiniInst.getInvoiceId());
			lMini.put("invoice_issue_date", lMiniInst.getInvoiceIssueDate());
			lMini.put("amount", lMiniInst.getAmount());
			lMini.put("eway_id", lMiniInst.getEwayId());
	    	  //lMini.put("external_id", lMiniInst.getBuyerGstn());
			lMiniInsts.add(lMini);
		}
		//
		lTempJson.put("invoice_list",lMiniInsts);
		//
		lApiRequestBean.setBody( new JsonBuilder(lTempJson).toString() );
		//	
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("upload-batch/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
	}

	public boolean getAllSuppliers() throws Exception{
		//
		
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//	
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("lender/get-all-suppliers/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean);
		logger.info("************ ************");
		if(lApiResponseBean.getStatusCode().equals(AppConstants.HTTP_RESPONSE_STATUS_200_OK)||lApiResponseBean.getStatusCode().equals(AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean getBatch(String pRequestId) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//	
		lApiRequestBean.addParam("request_id", pRequestId);
		//
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("get-batch/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean);
		logger.info("************ ************");
		Map<String,Object> lResponse = (Map<String,Object>) lApiResponseBean.getResponseJson();
		if (lApiResponseBean.getStatusCode()==200){
			if (lResponse.containsKey(JSON_KEY_PAYLOAD)){
				Map<String,Object> lTmp = (Map<String, Object>) lResponse.get(JSON_KEY_PAYLOAD);
				if (lTmp.containsKey(JSON_KEY_BATCH)){
					lTmp = (Map<String, Object>) lTmp.get(JSON_KEY_BATCH);
					if (lTmp.containsKey(JSON_KEY_INVOICE_LIST)){
						List<Map<String,Object>>lTmpList = (List<Map<String, Object>>) lTmp.get(JSON_KEY_INVOICE_LIST);
						lTmp = lTmpList.get(0);
						if (lTmp.containsKey(JSON_KEY_AUTHENTICATION_STATUS)){
							lTmp = (Map<String, Object>) lTmp.get(JSON_KEY_AUTHENTICATION_STATUS);
							if (lTmp.containsKey(JSON_KEY_STATUS_CODE)){
								Long lStatusCode = new Long(lTmp.get(JSON_KEY_STATUS_CODE).toString());
								if (MONETAGO_STATUS_PASSED.equals(lStatusCode)){
									return true;
								}else if (MONETAGO_STATUS_PARTIALLY_AUTHENTICATED.equals(lStatusCode)){
									logger.info("upload-batch Status PARTIALLY_AUTHENTICATED");
									//allow for lookup
								}else if (MONETAGO_STATUS_PENDING.equals(lStatusCode)){
									logger.info("upload-batch Status PENDING");
									//allow for lookup
								}else if (MONETAGO_STATUS_WARNING.equals(lStatusCode)){
									logger.info("upload-batch Status WARNING");
									//allow for lookup
								}else if (MONETAGO_STATUS_FAILED.equals(lStatusCode)){
									throw new CommonBusinessException("No Eway Details found.");
								}
								if (lTmp.containsKey(JSON_KEY_LOOKUP_RESULT_LIST)){
									lTmpList = (List<Map<String, Object>>) lTmp.get(JSON_KEY_LOOKUP_RESULT_LIST);
									if (lTmpList.isEmpty() && MONETAGO_STATUS_PENDING.equals(lStatusCode)){
										throw new CommonBusinessException("Invoice Authentication Request has been sent to your E-mail Id.<br></br> Please Initiate the verification process and follow the instructions provided.<br></br> After completion please retry adding the invoice again.");
									}
									for (Map<String, Object> lHash : lTmpList){
										if (lHash.containsKey(JSON_KEY_NAME)){
											if (JSON_KEY_LOOKUP_RESULT_LIST_EWAY.equals(lHash.get(JSON_KEY_NAME).toString())){
												if (lHash.containsKey(JSON_KEY_RESULT)){
													lTmp = (Map<String, Object>) lHash.get(JSON_KEY_RESULT);
													if (lTmp.containsKey(JSON_KEY_REGISTERED) && Boolean.valueOf(lTmp.get(JSON_KEY_REGISTERED).toString())){
														if (lTmp.containsKey(JSON_KEY_MATCHED) && Boolean.valueOf(lTmp.get(JSON_KEY_MATCHED).toString())){
															return true;
														}else{
															String lErrorFields = "";
															String lErrorMessage = "Eway details not matched.";
															if (lTmp.containsKey(JSON_KEY_INVALID_FIELD_LIST)){
																List<Map<String, Object>> lInvalidFieldList = (List<Map<String, Object>>) lTmp.get(JSON_KEY_INVALID_FIELD_LIST);
																Object lFieldDes = null;
																for (Map<String, Object> lFieldMap: lInvalidFieldList){
																	//"supplier_gstin","buyer_gstin","invoice_id","invoice_issue_date","amount","eway_id"
																	lFieldDes = ewayFieldDescriptions.get(lFieldMap.get(JSON_KEY_FIELD));
																	if(lFieldDes==null || StringUtils.isBlank(lFieldDes.toString())) lFieldDes = lFieldMap.get(JSON_KEY_FIELD);
																	lErrorFields += "<br> For Field : "+lFieldDes+",  Expected Value : "+lFieldMap.get(JSON_KEY_REGISTERED_VALUE)+", Entered Value : "+lFieldMap.get(JSON_KEY_USER_PROVIDED_VALUE)+".";
																}
															}
															if (StringUtils.isNotBlank(lErrorFields)){
																lErrorMessage = lErrorMessage +" . "+ lErrorFields;
															}
															throw new CommonBusinessException(lErrorMessage);
														}
													}else{
														throw new CommonBusinessException("Eway details not regsitered.");
													}
												}
											}
										}
									}
								}
							}else{
								throw new CommonBusinessException("Error in upload-batch : Key status code missing.");
							}
						}else{
							throw new CommonBusinessException("Error in upload-batch : Key authentication status missing.");
						}
					}else{
						throw new CommonBusinessException("Error in upload-batch : Key invoice list missing.");
					}
				}else{
					throw new CommonBusinessException("Error in upload-batch : Key batch missing.");
				}
			}else{
				throw new CommonBusinessException("Error in upload-batch : Key payload missing.");
			}
		}
		return false;
	}

	public void getAllInvoices() throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//	
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info("************ REQUEST ");
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("get-all-invoices/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info("************ RESPONSE ");
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
	}

	public String getEWayBillData(String pGstn, String pInvoiceId) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//
		lApiRequestBean.addParam(QUERY_PARAM_SUPPLIER_GSTN, pGstn);
		lApiRequestBean.addParam(QUERY_PARAM_INVOICE_ID, pInvoiceId);
		lApiRequestBean.addParam(QUERY_PARAM_DATA_TYPE, QUERY_PARAM_DATA_TYPE_EWAY_VALUE);
		//	
		logger.info("************ REQUEST ");
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		//
		logger.info("************ RESPONSE ");
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("get-raw-invoice-data/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
		//
		return lApiResponseBean.getResponseText();
	}
	

	public String getGstrData(String pGstn, String pInvoiceId) throws Exception{
		//
		RestClient lRestClient = new RestClient(baseUri, proxyIp, proxyPort);
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.GET);
		setAuthHeaders(lApiRequestBean);
		//
		lApiRequestBean.addParam(QUERY_PARAM_SUPPLIER_GSTN, pGstn);
		lApiRequestBean.addParam(QUERY_PARAM_INVOICE_ID, pInvoiceId);
		lApiRequestBean.addParam(QUERY_PARAM_DATA_TYPE, QUERY_PARAM_DATA_TYPE_GSTR_VALUE);
		//	
		logger.info("************ REQUEST ");
		BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ApiRequestBean.class);
		logger.info(lBeanMeta.formatAsJson(lApiRequestBean));
		//
		logger.info("************ RESPONSE ");
		ApiResponseBean lApiResponseBean = null;
		try{
			lApiResponseBean = lRestClient.sendRequest("get-raw-invoice-data/", lApiRequestBean,"application/json");
		}catch(Exception lEx){
			throw new CommonBusinessException("Connectivity error.");
		}
		logger.info(lApiResponseBean.getResponseText());
		logger.info("************ ************");
		//
//		if(lApiResponseBean.getError()){
//			throw new CommonBusinessException(lApiResponseBean.getResponseText());
//		}
		//
		return lApiResponseBean.getResponseText();
	}
	public MonetagoInvoiceInfoBean getGstrData(String pPayLoad){
		MonetagoInvoiceInfoBean lMonetagoInvoiceInfoBean = null;
		JsonSlurper lJsonSlurper = new JsonSlurper();
		BeanMeta lInvoiceBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(MonetagoInvoiceInfoBean.class);
		Map<String,Object> lReqInvoiceJson = null;
		List<ValidationFailBean> lValidationFailBeans = new ArrayList<ValidationFailBean>();
		if (CommonUtilities.hasValue(pPayLoad)){
			Map<String,Object> lTempJson = (Map<String, Object>) lJsonSlurper.parseText(pPayLoad);
			if( lTempJson.containsKey(JSON_KEY_GSTR_PAYLOAD)){
				lTempJson = (Map<String,Object>) lTempJson.get(JSON_KEY_GSTR_PAYLOAD);
				if( lTempJson.containsKey(JSON_KEY_GSTR)){
					lTempJson = (Map<String,Object>) lTempJson.get(JSON_KEY_GSTR);
					if( lTempJson.containsKey("payload")){
						lTempJson = (Map<String,Object>) lTempJson.get("payload");
						if( lTempJson.containsKey("b2b")){
							List<Map<String,Object>> lTempJson2 = (List<Map<String,Object>>) lTempJson.get("b2b");
							if( lTempJson2.get(0).containsKey("inv")){
								lTempJson2  = (List<Map<String,Object>>) lTempJson2.get(0).get("inv");
								lReqInvoiceJson = (Map<String, Object>) lTempJson2.get(0);
								lMonetagoInvoiceInfoBean = new MonetagoInvoiceInfoBean();
								lInvoiceBeanMeta.validateAndParse(lMonetagoInvoiceInfoBean, lReqInvoiceJson, lValidationFailBeans);
							}
						}
					}
				}
			}
		}
		return lMonetagoInvoiceInfoBean;
	}
	
	public MonetagoEwaybillInfoBean getEWayBillData(String pPayLoad) throws CommonBusinessException{
		MonetagoEwaybillInfoBean lMonetagoEwaybillInfoBean = null;
		JsonSlurper lJsonSlurper = new JsonSlurper();
		BeanMeta lEwayBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(MonetagoEwaybillInfoBean.class);
		Map<String,Object> lReqEwayJson = null;
		List<ValidationFailBean> lValidationFailBeans = new ArrayList<ValidationFailBean>();
		String lErrorMessage = null;
		if (CommonUtilities.hasValue(pPayLoad)){
			Map<String,Object> lTempJson = (Map<String, Object>) lJsonSlurper.parseText(pPayLoad);
			if( lTempJson.containsKey(JSON_KEY_EWAY_PAYLOAD)){
				lTempJson = (Map<String,Object>) lTempJson.get(JSON_KEY_EWAY_PAYLOAD);
				if( lTempJson.containsKey(JSON_KEY_EWAY)){
					lTempJson = (Map<String,Object>) lTempJson.get(JSON_KEY_EWAY);
					if( lTempJson.containsKey("payload")){
						lReqEwayJson = (Map<String,Object>) lTempJson.get("payload");
						lMonetagoEwaybillInfoBean = new MonetagoEwaybillInfoBean();
						lEwayBeanMeta.validateAndParse(lMonetagoEwaybillInfoBean, lReqEwayJson, lValidationFailBeans);
					}else{
						lErrorMessage = "Eway details incorrect. ( Payload 2 missing. )";
					}
				}else{
					lErrorMessage = "Eway details incorrect. ( key ewaybill missing )";
				}
			}else if (lTempJson.containsKey(JSON_KEY_ERROR)) {
				lTempJson = (Map<String,Object>) lTempJson.get(JSON_KEY_ERROR);
				lErrorMessage = "Eway details incorrect. ( " +lTempJson.get(JSON_KEY_MESSAGE)+ " )";
			}
		}else{
			lErrorMessage = "Eway details incorrect. ( No data received. )";
		}
		if(CommonUtilities.hasValue(lErrorMessage)){
			throw new CommonBusinessException(lErrorMessage);
		}
		return lMonetagoEwaybillInfoBean;
	}
	
	private static Map<String,String> getEwayFieldDesc() {
		Map<String,String> lFieldDesc = new HashMap<String,String>();
		lFieldDesc.put("buyer_gstin", "Buyer GSTN");
		lFieldDesc.put("invoice_id", "Invoice Id");
		lFieldDesc.put("invoice_issue_date", "Invoice Issue date");
		lFieldDesc.put("amount", "Invoice amount");
		lFieldDesc.put("eway_id", "Eway Id");
		lFieldDesc.put("supplier_gstin", "Supplier GSTN");
		lFieldDesc.put("lender_email_list", "Emails");
		return lFieldDesc;
	}

	public static void main(String[] args) {
		BeanMetaFactory.createInstance(null);
		//InstAuthJerseyClient.getInstance().getEWayBillData("29ABCDE2234F2Z5", "INV009872018");
		//InstAuthJerseyClient.getInstance().getGstrData("29ABCDE2234F2Z5", "INV009872018");
		//InstAuthJerseyClient.getInstance().isSupplierRegistered("29ABCDE2234F2Z5");
		//InstAuthJerseyClient.getInstance().getEWayBillData("27AAACD4819K1ZJ", "INV_001/2019-20");
		//InstAuthJerseyClient.getInstance().getGstrData("27AAACD4819K1ZJ", "INV_001/2019-20");
		//InstAuthJerseyClient.getInstance().getEWayBillData("24AAACD4819K1ZP", "INV_002/2019-20");
		//InstAuthJerseyClient.getInstance().getEWayBillData("03AAACD4819K1ZT", "INV_003/2019-20");
		//InstAuthJerseyClient.getInstance().getRegisteredSupplierPayLoad("29ABCDE2234F2Z5");
		//
		try {
			InstAuthJerseyClient.getInstance().getAllSuppliers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			InstAuthJerseyClient.getInstance().getAllInvoices();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			InstAuthJerseyClient.getInstance().getBatch("KNAL2ELX3M96DA0SBL5UV34KRC1DQN");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//REGISTER SUPPLIER
		//testRegisterSupplier();
		//FETCH SUPPLIER
		//String lPayLoad = InstAuthJerseyClient.getInstance().getRegisteredSupplierPayLoad("27AAACD4819K1ZJ");
		//logger.info("isSupplierRegistered="+ InstAuthJerseyClient.getInstance().isSupplierRegistered("27AAACD4819K1ZJ", lPayLoad));
		//UPLOAD BATCH
		//testBatch();
		//FETCH EWAY
		//InstAuthJerseyClient.getInstance().getEWayBillData("27AAACD4819K1ZJ", "INV_001/2019-20");
		//FETCH GSTR
		//String lGSTRPayLoad = InstAuthJerseyClient.getInstance().getGstrData("27AAACD4819K1ZJ", "INV_001/2019-20");
		//MonetagoInvoiceInfoBean lMonetagoInvoiceInfoBean = InstAuthJerseyClient.getInstance().getGstrData(lGSTRPayLoad);
		//System.out.print(lMonetagoInvoiceInfoBean.toString());
		try {
			testProductionSetting();
		} catch (Exception e) {
//			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void testProductionSetting() throws Exception{
		InstAuthJerseyClient.getInstance().getAllInvoices();
	}
	
	public static void testRegisterSupplier() throws CommonBusinessException{
		String[] lSupplierGstns = new String[] { "27AAACD4819K1ZJ","03AAACD4819K1ZT","24AAACD4819K1ZP", "29ABCDE2234F2Z5" };
		List<String> lEmails = new ArrayList<String>();
		lEmails.add("prasad@xlxtech.co.in");
		lEmails.add("chinmayd@xlxtech.co.in");
		lEmails.add("pmahimkar@gmail.com");
		//InstAuthJerseyClient.getInstance().registerSupplier(DBHelper.getInstance().getConnection(),lSupplierGstns[0] , lEmails);
	}
	
	public static void testBatch(){
		String[] lSupplierGstns = new String[] { "27AAACD4819K1ZJ","03AAACD4819K1ZT","24AAACD4819K1ZP", "29ABCDE2234F2Z5" };
		List<String> lEmails = new ArrayList<String>();
		lEmails.add("prasad@xlxtech.co.in");
		lEmails.add("chinmayd@xlxtech.co.in");
		lEmails.add("pmahimkar@gmail.com");
		List<MiniInstrument> lMiniInstruments = new ArrayList<MiniInstrument>();
		MiniInstrument lMiniInstrument = new MiniInstrument("24AAACN5327L1ZG", "INV_001/2019-20", CommonUtilities.getDate("2019-04-15","yyyy-MM-dd"), new BigDecimal("125000"), "1562147896");
		lMiniInstruments.add(lMiniInstrument);
		try {
			InstAuthJerseyClient.getInstance().uploadBatch(lSupplierGstns[0], lMiniInstruments, lEmails);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

