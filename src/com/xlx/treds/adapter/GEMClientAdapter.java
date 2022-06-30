package com.xlx.treds.adapter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.http.SSLConfigurationProvider;
import com.xlx.commonn.http.bean.ApiRequestBean;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.commonn.http.client.RestClient;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiRequestType;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.RequestStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ResponseAckStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.Type;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class GEMClientAdapter  implements IClientAdapter {
    private static Logger logger = Logger.getLogger(GEMClientAdapter.class);
	private static final String LOG_HEADER = "GEMClientAdapter :: ";
    private final static String PARAM_INSTRUMENTNUMBER = "INST_NUMBER";
    private final static String PARAM_UNIQUEID = "UID";
	private String entityCode = null;
    public RestClient gemClient = null;
    private ClientSettingsBean clientSettingsBean;
    private GenericDAO<AdapterRequestResponseBean> adapterRequestResponseDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    
	public GEMClientAdapter(String pEntityCode,ClientSettingsBean lClientSettingsBean){
		super();
		entityCode = pEntityCode;
		clientSettingsBean = lClientSettingsBean;
		adapterRequestResponseDAO = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class);
		factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
	}
	
    @Override
	public String convertClientDataToTredsData(ProcessInformationBean pProcessInformationBean) throws Exception {
    	JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lReturnMap = null;
		Object lTmpStr = pProcessInformationBean.getClientDataForProcessing();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(lTmpStr.toString());
        if(lMap != null){
        	pProcessInformationBean.setKey(lMap.get("gemUniqueRequestIdentifier").toString());
	        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedTredsData();
        }
		return new JsonBuilder(lReturnMap).toString();
	}

	@Override
	public String convertTredsDataToClientData(ProcessInformationBean pProcessInformationBean) throws Exception {
		Map<String,Object> lReturnMap = null;
		if(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessInformationBean.getProcessId()) ||
				ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessInformationBean.getProcessId())){
			lReturnMap = new HashMap<String, Object>();
			Connection lConnection = DBHelper.getInstance().getConnection();
			Object[] lObj = (Object[]) pProcessInformationBean.getTredsDataForProcessing();
			if(lObj!=null && lObj.length > 1){
				FactoringUnitBean lFuBean = (FactoringUnitBean) lObj[0];
				InstrumentBean lInstrumentBean = (InstrumentBean) lObj[1];
				if (lInstrumentBean.getIsAggregatorCreated()==null) {
					throw new CommonBusinessException(lInstrumentBean.getId()+" Not Created by GEM ");
				}
				if(lObj.length > 2) {
					//ObligationBean lOBBean = (ObligationBean) lLeg1Details[2];
				}
				if(lObj.length > 3) {
					//List<ObligationSplitsBean> lSplitsBeans = (List<ObligationSplitsBean>) lLeg1Details[3];
				}
				if(lInstrumentBean!=null){
					Map<String,Object> lTmpGemMap = (Map<String,Object>) new JsonSlurper().parseText(lInstrumentBean.getOtherSettings());
					if(lTmpGemMap!=null){
						lReturnMap.put("senderID", "RXIL");
						lReturnMap.put("messsageID", lTmpGemMap.get("gemMesssageId"));
						lReturnMap.put("uniqueInvoiceID", lTmpGemMap.get("gemUniqueRequestIdentifier"));
						lReturnMap.put("createdOn", lTmpGemMap.get("gemCreatedDate"));
						lReturnMap.put("receiverID", "GEM");
						
					}
					lReturnMap.put("requestTyp", lTmpGemMap.get("requestTyp"));
					lReturnMap.put("sellerGSTN", lInstrumentBean.getSupGstn());
					lReturnMap.put("sellerPAN", lInstrumentBean.getSupPan());
					lReturnMap.put("buyerGSTN", lInstrumentBean.getPurGstn());
					lReturnMap.put("buyerPAN", lInstrumentBean.getPurPan());
					lReturnMap.put("invoiceNo", lInstrumentBean.getInstNumber());
					lReturnMap.put("invoiceAmt", lInstrumentBean.getAmount());
					lReturnMap.put("currency", lInstrumentBean.getCurrency());
					lReturnMap.put("rejectionReason", ""); //Optional / Mandatory when “Not Accepted”
					//
					String lFinStatus = "", lFinName = "", lInvoiceStatus = "REJECTED";
					FactoringUnitBean lFactoringUnitBean = lFuBean;
					if(lFuBean == null){
						FactoringUnitBean lFilterBean = new FactoringUnitBean();
						lFilterBean.setId(lInstrumentBean.getFuId());
						lFactoringUnitBean = factoringUnitDAO.findBean(lConnection, lFilterBean);
					}
					if(lFactoringUnitBean!=null) {
						//Accepted, Not Accepted, Factored, Expired, Cancelled, Paid, Payment Failed
						if(FactoringUnitBean.Status.Active.equals(lFactoringUnitBean.getStatus()) 
							|| (FactoringUnitBean.Status.Factored.equals(lFactoringUnitBean.getStatus()))
							|| (FactoringUnitBean.Status.Expired.equals(lFactoringUnitBean.getStatus())) 
							|| (FactoringUnitBean.Status.Leg_1_Failed.equals(lFactoringUnitBean.getStatus()))
							|| (FactoringUnitBean.Status.Withdrawn.equals(lFactoringUnitBean.getStatus()))
							|| (FactoringUnitBean.Status.Leg_1_Settled.equals(lFactoringUnitBean.getStatus())) ) {
							lFinStatus = lFactoringUnitBean.getStatus().getCode();
						}else if(FactoringUnitBean.Status.Leg_2_Settled.equals(lFactoringUnitBean.getStatus()) ||
								(FactoringUnitBean.Status.Leg_2_Failed.equals(lFactoringUnitBean.getStatus()))	) {
							lFinStatus = FactoringUnitBean.Status.Leg_1_Settled.getCode();
						}
						if(lFactoringUnitBean.getFinancier() != null){
				    	AppEntityBean lFinEntityBean = TredsHelper.getInstance().getAppEntityBean(lFactoringUnitBean.getFinancier());
							if(lFinEntityBean != null){
								lFinName = lFinEntityBean.getName();
							}
						}
						if((FactoringUnitBean.Status.Factored.equals(lFactoringUnitBean.getStatus()))
								|| (FactoringUnitBean.Status.Leg_1_Settled.equals(lFactoringUnitBean.getStatus())) 
								|| (FactoringUnitBean.Status.Leg_2_Settled.equals(lFactoringUnitBean.getStatus())) 
								||	(FactoringUnitBean.Status.Leg_2_Failed.equals(lFactoringUnitBean.getStatus()))	) {
							lInvoiceStatus = "FACTORED";
						}
					}
					lReturnMap.put("invoiceStatus", lInvoiceStatus);
					 
					lReturnMap.put("financingStatus", lFinStatus);
					lReturnMap.put("financierName", lFinName);
					lReturnMap.put("dueDt", (lFactoringUnitBean!=null?lFactoringUnitBean.getLeg2MaturityExtendedDate():null));
					
					//
					//only in case of Leg1 Settlment below info is returned in 2nd and 3rd position in array, 2=ParentObli 3=List<ObliSplit>
					List<ObligationDetailBean> lDetailList = TredsHelper.getInstance().getObligationDetailBean(pProcessInformationBean.getConnection(),lInstrumentBean.getFuId(),null, null);
					ObligationBean lObliBean = null;
					ObligationSplitsBean lObliSplitsBean = null; 
					Map<Long,ObligationBean> lParentObligBeans = new HashMap<Long,ObligationBean>();
					String lUtrNos = "";
					String lErrorCodes = "";
					String lErrorMessages = "";
					for (ObligationDetailBean lBean : lDetailList){
						lObliSplitsBean = lBean.getObligationSplitsBean();
						//to have only one reference of parent in the splits
						if(!lParentObligBeans.containsKey(lObliSplitsBean.getId())) {
							lObliBean = lBean.getObligationBean();
							lParentObligBeans.put(lObliBean.getId(), lObliBean);
						}else {
							lObliBean = lParentObligBeans.get(lObliBean.getId());
						}
						lObliSplitsBean.setParentObligation(lObliBean);
						
						if(ObligationBean.Type.Leg_1.equals(lObliBean.getType()) && 
								lInstrumentBean.getSupplier().equals(lObliBean.getTxnEntity()) ){
							if(  ObligationBean.Status.Success.equals(lObliSplitsBean.getStatus())) {
								if(StringUtils.isNotEmpty(lObliSplitsBean.getPaymentRefNo())) {
									if(!StringUtils.isEmpty(lUtrNos)) {
										lUtrNos+=",";
									}
									//lUtrNos += lObliSplitsBean.getId()+"_"+lObliSplitsBean.getPartNumber()+":";
									lUtrNos += lObliSplitsBean.getPaymentRefNo();
								}
							}else {
								if(ObligationBean.Status.Failed.equals(lObliSplitsBean.getStatus()) ||
										ObligationBean.Status.Cancelled.equals(lObliSplitsBean.getStatus()) ||
										ObligationBean.Status.Returned.equals(lObliSplitsBean.getStatus())) {
									if(StringUtils.isNotEmpty(lObliSplitsBean.getRespErrorCode())) {
										if(!StringUtils.isEmpty(lErrorCodes)) {
											lErrorCodes+=",";
										}
										lErrorCodes += lObliSplitsBean.getRespErrorCode();
									}
									if(StringUtils.isNotEmpty(lObliSplitsBean.getRespRemarks())) {
										if(!StringUtils.isEmpty(lErrorMessages)) {
											lErrorMessages+=",";
										}
										lErrorMessages += lObliSplitsBean.getId()+"_"+lObliSplitsBean.getPartNumber()+":";
										lErrorMessages += lObliSplitsBean.getRespRemarks();
									}
								}
							}
						}
					}
					// Optional / Mandatory when “factored”, “paid”, “payment failed”
					BigDecimal lDisbursedAmt = BigDecimal.ZERO;
					BigDecimal lRepaymentAmt = BigDecimal.ZERO;
					Date lDisburseDate = null;
					Date lRepayDate = (lFactoringUnitBean!=null?lFactoringUnitBean.getLeg2MaturityExtendedDate():null);
					if(lFactoringUnitBean!=null) {
						for(ObligationBean lParentObliBean : lParentObligBeans.values()) {
							if(lFactoringUnitBean.getSupplier() !=null &&
									lFactoringUnitBean.getSupplier().equals(lParentObliBean.getTxnEntity())) {
								if(ObligationBean.Type.Leg_1.equals(lParentObliBean.getType()) &&
										ObligationBean.Status.Success.equals(lParentObliBean.getStatus())	){
									lDisbursedAmt = lParentObliBean.getAmount();
									lDisburseDate = lParentObliBean.getSettledDate();
								}
							}else if(lFactoringUnitBean.getPurchaser() !=null &&
									lFactoringUnitBean.getPurchaser().equals(lParentObliBean.getTxnEntity())) {
								//ObligationBean.Status.Success.equals(lObliBean.getStatus()) 
								if(ObligationBean.Type.Leg_2.equals(lParentObliBean.getType()) &&
										ObligationBean.Status.Success.equals(lParentObliBean.getStatus())	){
									lRepaymentAmt = lParentObliBean.getAmount();
									lRepayDate = lParentObliBean.getSettledDate();
								}
							}
						}
					}
					//
					lReturnMap.put("disbursedAmt", lDisbursedAmt);
					lReturnMap.put("disbursementDt", lDisburseDate);
					lReturnMap.put("repaymentAmt", lRepaymentAmt);
					lReturnMap.put("repaymentDt", lRepayDate);
					lReturnMap.put("remarks", lInstrumentBean.getStatusRemarks());
					//
					lReturnMap.put("source", "API");
					lReturnMap.put("interestBorneBy", (lInstrumentBean.getCostBearingType() != null)?lInstrumentBean.getCostBearingType().getCode():""); //optional						
					lReturnMap.put("createdBy", lInstrumentBean.getMakerEntity()); //optional

					lReturnMap.put("createdAt", (lFactoringUnitBean!=null?lFactoringUnitBean.getStatusUpdateTime():""));
					lReturnMap.put("changedBy", "");
					lReturnMap.put("changedOn", "");
					lReturnMap.put("changedAt", "");
					lReturnMap.put("uniqueFinancingID", lInstrumentBean.getFuId());
					lReturnMap.put("paymentRefNo ", lUtrNos);
					lReturnMap.put("errorCode", lErrorCodes);
					lReturnMap.put("errorMessage", lErrorMessages);
					
					pProcessInformationBean.setProcessedClientData(lReturnMap);

				}
			}
		}
		return new JsonBuilder(lReturnMap).toString();
	}

	@Override
	public boolean connectClient() {
		logger.info(LOG_HEADER+"connectClient() called.");
		//call the login api and check
		try{
			logger.info("connectClient URL : "+clientSettingsBean.getClientUrl());
			logger.info("Certificate Path : "+clientSettingsBean.getCertificatePath()!=null?clientSettingsBean.getCertificatePath():"");
			if(CommonUtilities.hasValue(clientSettingsBean.getCertificatePath())){
				SSLConfigurationProvider lConfigurationProvider = new SSLConfigurationProvider(clientSettingsBean.getCertificatePath(), clientSettingsBean.getCertificateIdentityPassword(), clientSettingsBean.getCertificateAlias(),null, null);
				gemClient = new RestClient(clientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort(), lConfigurationProvider);
			}else{
				gemClient = new RestClient(clientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort());
			}
		}catch(Exception lException){
			lException.printStackTrace();
			logger.info(LOG_HEADER+"Error : "+lException.getMessage());
		}
		return true;
	}

	@Override
	public boolean isClientConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendResponseToClient(ProcessInformationBean pProcessInformationBean) throws Exception {
		logger.info("Send Response called  ");
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		lApiRequestBean.setBody(convertTredsDataToClientData(pProcessInformationBean));
		if(clientSettingsBean != null){
			if(!clientSettingsBean.getHeaders().isEmpty()){
				for(String lKey : clientSettingsBean.getHeaders().keySet()){
					lApiRequestBean.setHeaders(lKey, clientSettingsBean.getHeaders().get(lKey));
				}
			}
		}
		logger.info("DATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
		RestClient.addBasicAuthentication(lApiRequestBean, clientSettingsBean.getClientUsername() , clientSettingsBean.getClientPassword() );
		ApiResponseBean lRespBean = null;
		//logging in the response before checking in connectivity so that if the connectivity is down, the failed response can be send later
		AdapterRequestResponseBean lARRBean = new AdapterRequestResponseBean();
		logOutgoing(pProcessInformationBean, getURL(pProcessInformationBean.getProcessId()), lRespBean, lARRBean, true);
		connectClient();
		lRespBean = gemClient.sendRequest(getURL(pProcessInformationBean.getProcessId()), lApiRequestBean, MediaType.APPLICATION_JSON);
		logger.info("Response From GEM : "+lRespBean.getStatusCode());
		logger.info("Response From GEM : "+lRespBean.getResponseText());
		logOutgoing(pProcessInformationBean, getURL(pProcessInformationBean.getProcessId()), lRespBean, lARRBean, false);
		//
		return false;
	}

	@Override
	public String getURL(Long pProcessId) {
		
		if(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessId)){
			return "ARTEC/SCF/INVFINSTATUSUPD/";
		}else if(ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessId)){
			return "ARTEC/SCF/INVFINSTATUSUPD/";
		}
		return "";
	}

	@Override
	public Long logOutgoing(ProcessInformationBean pProcessInformationBean, String pOutUrl,
			ApiResponseBean pApiResponseBean, AdapterRequestResponseBean pARRBean, boolean pNew) {
		Long lArrId = null;
		Connection lConnection = pProcessInformationBean.getConnection();
		try {
	        if (pNew) {
	        	lConnection.setAutoCommit(false);
	        	pARRBean.setType(Type.Out);
	        	pARRBean.setEntityCode(entityCode);
	        	pARRBean.setKey(pProcessInformationBean.getKey());
	        	pARRBean.setProcessId(pProcessInformationBean.getProcessId());
	        	pARRBean.setApiRequestType(ApiRequestType.POST);
	        	pARRBean.setApiRequestUrl(getURL(pProcessInformationBean.getProcessId()));
	        	pARRBean.setApiRequestData(new JsonBuilder(pProcessInformationBean.getProcessedClientData()).toString());
	        	pARRBean.setUid(pProcessInformationBean.getUID()); //set uid from above data map
	        	pARRBean.setTimestamp(CommonUtilities.getCurrentDateTime());
	        	pARRBean.setRequestStatus(RequestStatus.Sent);
	        	pARRBean.setResponseAckStatus(ResponseAckStatus.Not_Read);
	        	if(pApiResponseBean==null){
	        		//the api response is sent null when we have to log the outgoing request before sending
	        		//hence keeping it failed, so that if the connector is not connected, the same can be sent later.
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
	        	}else{
		        	if (AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED.equals(pApiResponseBean.getStatusCode()) ||
		        			AppConstants.HTTP_RESPONSE_STATUS_200_OK.equals(pApiResponseBean.getStatusCode())	){
		        		pARRBean.setApiResponseStatus(ApiResponseStatus.Success);
		        	}else{
		        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
		        	}
		        	if (StringUtils.isNotEmpty(pApiResponseBean.getResponseText())){
			        	pARRBean.setApiResponseData(pApiResponseBean.getResponseText());
		        	}
	        	}
	            adapterRequestResponseDAO.insert(lConnection, pARRBean);
	            lConnection.commit();
	            lArrId = pARRBean.getId();
	        } else {
	        	if (pApiResponseBean.getStatusCode()!=null  && 
	        			( AppConstants.HTTP_RESPONSE_STATUS_200_OK.equals(pApiResponseBean.getStatusCode()) ||
	        					AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED.equals(pApiResponseBean.getStatusCode()))){
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Success);
		        	if (StringUtils.isNotEmpty(pApiResponseBean.getResponseText())){
			        	pARRBean.setApiResponseData(pApiResponseBean.getResponseText());
		        	}
	        	}else{
	        		pARRBean.setLastSendDateTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
	        	}
	        	adapterRequestResponseDAO.update(lConnection, pARRBean, BeanMeta.FIELDGROUP_UPDATE);
	        	lConnection.commit();
	            lArrId = pARRBean.getId();
	        }
		} catch (Exception e) {
			logger.info("Error in logOutgoing : "+e.getMessage());
		}
		return lArrId;
	}

	@Override
	public Long logInComing(ProcessInformationBean pProcessInformationBean, String pInCommingUrl, ApiResponseStatus pApiResponseStatus, boolean pNew, boolean pValidateUniqueRequest) throws CommonBusinessException {
		Long lArrId = null;
		AdapterRequestResponseBean lARRBean = new AdapterRequestResponseBean();
		try(Connection lConnection = DBHelper.getInstance().getConnection()) {
	        if (pNew) {
	        	JsonSlurper lJsonSlurper = new JsonSlurper();
	        	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pProcessInformationBean.getClientDataForProcessing().toString());
	        	//
	        	lARRBean.setType(Type.In);
	        	lARRBean.setApiRequestType(ApiRequestType.POST);
	        	lARRBean.setApiRequestUrl(pInCommingUrl);
	        	lARRBean.setEntityCode(entityCode);
	        	lARRBean.setKey(lMap.get("gemUniqueRequestIdentifier").toString());
	        	if(pProcessInformationBean.getKey() == null){
	        		if(lMap.get(PARAM_INSTRUMENTNUMBER) instanceof String){
						lARRBean.setKey((String)lMap.get(PARAM_INSTRUMENTNUMBER));
	        		}else{
		        		if(getLong(lMap.get(PARAM_INSTRUMENTNUMBER))!=null){
		        			lARRBean.setKey(getLong(lMap.get(PARAM_INSTRUMENTNUMBER)).toString());
		        		}
	        		}
	        	}
	        	lARRBean.setProcessId(pProcessInformationBean.getProcessId());
	        	if(ProcessInformationBean.PROCESSID_PURSUPLINK.equals(pProcessInformationBean.getProcessId())){
		        	lARRBean.setUid(lMap.get("id").toString()); //set uid from above data map
		        	lMap.put(PARAM_UNIQUEID, lMap.get("id").toString());
	        	}else{
		        	lARRBean.setUid(""); //set uid from above data map
	        	}
	        	//
	        	lARRBean.setApiRequestData(pProcessInformationBean.getClientDataForProcessing().toString());
	        	lARRBean.setTimestamp(CommonUtilities.getCurrentDateTime());
	        	lARRBean.setRequestStatus(null);
	        	lARRBean.setResponseAckStatus(ResponseAckStatus.Not_Read);

	            adapterRequestResponseDAO.insert (lConnection, lARRBean, BeanMeta.FIELDGROUP_INSERT);
	            pProcessInformationBean.setAdapterRequestResponseBean(lARRBean);
	            lArrId = lARRBean.getId();
	        } else {
	        	lARRBean = pProcessInformationBean.getAdapterRequestResponseBean();
	        	if(lARRBean !=null){
		        	if (lARRBean.getApiRequestUrl().equals(pInCommingUrl)){
		        		lARRBean.setApiResponseData((String) pProcessInformationBean.getTredsReturnResponseData());
		        		lARRBean.setApiResponseStatus(pApiResponseStatus);
		        		if (adapterRequestResponseDAO.update(lConnection, lARRBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
		        			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		        	}
		            lArrId = lARRBean.getId();
	        	}else{
	        		logger.info("Duplicate received.");
	        	}
	     }
		} catch (Exception e) {
			logger.info("Error in logInComing : "+ e.getMessage());
			throw new CommonBusinessException(e.getMessage());
		}
		return lArrId;
	}

	@Override
	public void addPostActionToQueue(ProcessInformationBean pOldProcessInformationBean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performActionPostIncoming(ProcessInformationBean pOldProcessInformationBean) {
		if (ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pOldProcessInformationBean.getProcessId()) ||
				ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pOldProcessInformationBean.getProcessId())){
			try {
				ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_LEG1SETTLED, pOldProcessInformationBean.getConnection());
				lProcessInformationBean.setKey(pOldProcessInformationBean.getKey());
				lProcessInformationBean.setTredsDataForProcessing(pOldProcessInformationBean.getTredsDataForProcessing());
				//lProcessInformationBean.setUID(getKey(lProcessInformationBean));
				sendResponseToClient(lProcessInformationBean);
			} catch (Exception e) {
				logger.info("Error in performActionPostIncoming : "+e.getMessage());
			}
		}
	}

	@Override
	public boolean reSendResponseToClient(ProcessInformationBean pProcessInformationBean,
			AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	private Object getLong(Object pValue){
		Object lValue = null;
        if (pValue != null)
        {
            if (pValue instanceof Long){
            	lValue = (Long)pValue;
            }
            else if (pValue instanceof Integer){
            	lValue = Long.valueOf(((Integer)pValue).longValue());
            }
            else if (pValue instanceof String)
            {
            	if(CommonUtilities.hasValue((String) pValue)){
                	lValue = Long.valueOf((String)pValue);
            	}
            }
        }
		return lValue;
	}
	
	public static void main(String[] args) throws Exception{
		BeanMetaFactory.getInstance().createInstance(null);
		ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS, DBHelper.getInstance().getConnection());
		Long lId = new Long("1911260000212");
		IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter("CO0000181");
		if (lClientAdapter!=null){
			lProcessInformationBean.setTredsDataForProcessing(getObjectArray(DBHelper.getInstance().getConnection(), lId));
			lClientAdapter.sendResponseToClient(lProcessInformationBean);
		}
	}
		
	public static  Object[] getObjectArray(Connection pConnection , Long pInId) throws Exception{
		GenericDAO<InstrumentBean> instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		GenericDAO<FactoringUnitBean> factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
		InstrumentBean lInstrumentFilterBean = new InstrumentBean();
		lInstrumentFilterBean.setId(pInId);
		InstrumentBean lInstrumentBean = instrumentDAO.findByPrimaryKey(pConnection, lInstrumentFilterBean);
		FactoringUnitBean lFactoringUnitFilterBean = new FactoringUnitBean();
		lFactoringUnitFilterBean.setId(lInstrumentBean.getFuId());
		FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(pConnection, lFactoringUnitFilterBean);
		Object[] lObj = new Object[] {lFactoringUnitBean, lInstrumentBean};
		return lObj;
	}

}
