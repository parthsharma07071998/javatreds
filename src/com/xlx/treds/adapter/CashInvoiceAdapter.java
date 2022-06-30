package com.xlx.treds.adapter;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiRequestType;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.RequestStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ResponseAckStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.Type;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class CashInvoiceAdapter implements IClientAdapter {
	
    private static Logger logger = Logger.getLogger(CashInvoiceAdapter.class);
	private static final String LOG_HEADER = "CashInvoiceAdapter :: ";
    private final static String PARAM_INSTRUMENTNUMBER = "INST_NUMBER";
    private final static String PARAM_UNIQUEID = "UID";
	private String entityCode = null;

    private GenericDAO<AdapterRequestResponseBean> adapterRequestResponseDAO;
    
	public CashInvoiceAdapter(String pEntityCode,ClientSettingsBean pClientSettingsBean){
		super();
		entityCode = pEntityCode;
		adapterRequestResponseDAO = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class);
	}

	@Override
	public String convertClientDataToTredsData(ProcessInformationBean pProcessInformationBean) throws Exception {
		if(ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
		}
		return null;
	}

	@Override
	public String convertTredsDataToClientData(ProcessInformationBean pProcessInformationBean) throws Exception {
			return null;
	}

	@Override
	public boolean connectClient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClientConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendResponseToClient(ProcessInformationBean pProcessInformationBean) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getURL(Long pProcessId) {
		// TODO Auto-generated method stub
		return null;
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
	        			(AppConstants.HTTP_RESPONSE_STATUS_200_OK.equals(pApiResponseBean.getStatusCode()) ||
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
	public Long logInComing(ProcessInformationBean pProcessInformationBean, String pInApiUrl,
			ApiResponseStatus pApiResponseStatus, boolean pNew, boolean pValidateUniqueRequest)
			throws CommonBusinessException {
		Long lArrId = null;
		AdapterRequestResponseBean lARRBean = new AdapterRequestResponseBean();
		try(Connection lConnection = DBHelper.getInstance().getConnection()) {
	        if (pNew) {
	        	JsonSlurper lJsonSlurper = new JsonSlurper();
	        	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pProcessInformationBean.getClientDataForProcessing().toString());
	        	//
	        	lARRBean.setType(Type.In);
	        	lARRBean.setApiRequestType(ApiRequestType.POST);
	        	lARRBean.setApiRequestUrl(pInApiUrl);
	        	lARRBean.setEntityCode(entityCode);
	        	//??????
	        	String lKey = FormatHelper.getDisplay("yyyymmddHHMMSS", new Timestamp(System.currentTimeMillis()));
	        	lARRBean.setKey(lKey);
	        	//
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
		        	if (lARRBean.getApiRequestUrl().equals(pInApiUrl)){
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
		// TODO Auto-generated method stub
		
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

	@Override
	public boolean reSendResponseToClient(ProcessInformationBean pProcessInformationBean,
			AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
