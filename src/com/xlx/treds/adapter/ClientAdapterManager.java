package com.xlx.treds.adapter;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.http.SSLConfigurationProvider;
import com.xlx.commonn.http.bean.ApiRequestBean;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.commonn.http.client.RestClient;
import com.xlx.treds.TredsHelper;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class ClientAdapterManager {
	private static Logger logger = Logger.getLogger(ClientAdapterManager.class);
	private static final String LOG_HEADER = "ClientAdapterManager :: ";

	public static final String ENTITYCODE = "entityCode";
	public static final String CLASS = "class";
	private static final String SETTINGS_JSON = "entity.json";
	public static final String ENTITIES = "entities";
	public static final String CLIENTURL = "clientUrl";
	public static final String CLIENTUSERNAME = "clientUsername";
	public static final String CLIENTPASSWORD = "clientPassword";
	public static final String CERTIFICATEPATH = "certificatePath";
	public static final String CERTIFICATEIDENTITYPASSWORD = "certificateIdentityPassword";
	public static final String CERTIFICATEALIAS = "certificateAlias";
	public static final String HEADERS = "headers";
	//
	private static ClientAdapterManager theInstance;
	private Map<String, IClientAdapter> clientwiseAdapters = null;
	private List<String> entityList = null;
	//

	public static ClientAdapterManager getInstance() {
		if (theInstance == null) {
			synchronized (ClientAdapterManager.class) {
				if (theInstance == null) {
					ClientAdapterManager tmpTheInstance = new ClientAdapterManager();
					try{
						tmpTheInstance.clientwiseAdapters = new HashMap<String,IClientAdapter>();
						tmpTheInstance.readSettings();
						theInstance = tmpTheInstance;
					}catch(Exception lEx){
						logger.info("Error in ClientAdapterManager : " + lEx.getMessage());
					}
				}
			}
		}
		return theInstance;
	}
	
	public IClientAdapter getClientAdapter(String pEntityCode){
		IClientAdapter lClientAdapter = null;
		if(StringUtils.isNotEmpty(pEntityCode)){
			if(clientwiseAdapters!= null && clientwiseAdapters.containsKey(pEntityCode)){
				lClientAdapter = clientwiseAdapters.get(pEntityCode);
			}
		}
		return lClientAdapter;
	}
	
	public List<String> getClientEntityList(){
		return entityList;
	}
	
	private void readSettings(){
		//Here we will read the list of Adapter for each client, and then load the specific adapter using the class name
		//logger.info(LOG_HEADER+"readSettings() called.");

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		List<Map> lSettings = (List<Map>) lMasterConfig.get(ENTITIES); 
		int lCount = lSettings.size();
		String lEntityCode, lFullClassName;
		IClientAdapter lClientAdapter =null;
		ClientSettingsBean lClientSettingsBean = null;
		entityList = new ArrayList<String>();
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lSettingMap = (Map<String, Object>) lSettings.get(lPtr);
			lFullClassName =(String) lSettingMap.get(CLASS);
			lEntityCode = (String) lSettingMap.get(ENTITYCODE);
			lClientSettingsBean = new ClientSettingsBean((String) lSettingMap.get(CLIENTURL), (String) lSettingMap.get(CLIENTUSERNAME), (String) lSettingMap.get(CLIENTPASSWORD), (String) lSettingMap.get(CERTIFICATEPATH), (String) lSettingMap.get(CERTIFICATEIDENTITYPASSWORD), (String) lSettingMap.get(CERTIFICATEALIAS), (Map<String,String>) lSettingMap.get(HEADERS));
	       try
	        {
	    	   Constructor constructor = Class.forName(lFullClassName).getConstructor(String.class,ClientSettingsBean.class);
	    	   lClientAdapter = (IClientAdapter) constructor.newInstance(lEntityCode,lClientSettingsBean);
	    	   if(lClientAdapter!=null){
	    		   clientwiseAdapters.put(lEntityCode, lClientAdapter);
	    		   entityList.add(lEntityCode);
	        	}
	        }
	        catch (Exception lException)
	        {
	    		logger.info(LOG_HEADER+"Error : "+lException.getMessage());
	        }
		}
		logger.info(LOG_HEADER+"Client Size : "+ clientwiseAdapters.size() +".");
		logger.info(LOG_HEADER+"readSettings() ended.");
	}
	
	public void testConnection(Map<Integer,Object> pDataMap){
		logger.info(LOG_HEADER+"testConnection() called.");

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		List<Map> lSettings = (List<Map>) lMasterConfig.get(ENTITIES); 
		int lCount = lMasterConfig.size();
		String lEntityCode, lFullClassName;
		IClientAdapter lClientAdapter =null;
		ClientSettingsBean lClientSettingsBean = null;
		entityList = new ArrayList<String>();
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lSettingMap = (Map<String, Object>) lSettings.get(lPtr);
			lFullClassName =(String) lSettingMap.get(CLASS);
			lEntityCode = (String) lSettingMap.get(ENTITYCODE);
			lClientSettingsBean = new ClientSettingsBean((String) lSettingMap.get(CLIENTURL), (String) lSettingMap.get(CLIENTUSERNAME), (String) lSettingMap.get(CLIENTPASSWORD), (String) lSettingMap.get(CERTIFICATEPATH), (String) lSettingMap.get(CERTIFICATEIDENTITYPASSWORD), (String) lSettingMap.get(CERTIFICATEALIAS),(Map<String,String>) lSettingMap.get(HEADERS));
			logger.info(LOG_HEADER+" test settings.");
			//call the login api and check
			try{
				RestClient lIoclClient = null;
				logger.info(LOG_HEADER+"connectClient URL : "+lClientSettingsBean.getClientUrl());
				logger.info(LOG_HEADER+"Certificate Path : "+lClientSettingsBean.getCertificatePath()!=null?lClientSettingsBean.getCertificatePath():"");
				if(CommonUtilities.hasValue(lClientSettingsBean.getCertificatePath())){
					SSLConfigurationProvider lConfigurationProvider = new SSLConfigurationProvider(lClientSettingsBean.getCertificatePath(), lClientSettingsBean.getCertificateIdentityPassword(), lClientSettingsBean.getCertificateAlias(),null, null);
					lIoclClient = new RestClient(lClientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort(), lConfigurationProvider);
				}else{
					lIoclClient = new RestClient(lClientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort());
				}
				logger.info(LOG_HEADER+"TestConnection Success.");
				if(lIoclClient!=null){
					logger.info(LOG_HEADER+"TestSend Response called  ");
					ApiRequestBean lApiRequestBean = new ApiRequestBean();
					lApiRequestBean.setMethod(HttpMethod.POST);
					RestClient.addBasicAuthentication(lApiRequestBean, lClientSettingsBean.getClientUsername() , lClientSettingsBean.getClientPassword() );
					ApiResponseBean lRespBean = null;
					//
					lApiRequestBean.setBody(new JsonBuilder(pDataMap.get(0)).toString());
					logger.info(LOG_HEADER+"TestDATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
					lRespBean = lIoclClient.sendRequest( "BC_INSTDET/PAYACK", lApiRequestBean, MediaType.APPLICATION_JSON);
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getStatusCode());
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getResponseText());
					//
					lApiRequestBean.setBody(new JsonBuilder(pDataMap.get(1)).toString());
					logger.info(LOG_HEADER+"TestDATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
					lRespBean = lIoclClient.sendRequest( "BC_INSTDET/PAYSTATUS", lApiRequestBean, MediaType.APPLICATION_JSON);
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getStatusCode());
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getResponseText());
					//
					lApiRequestBean.setBody(new JsonBuilder(pDataMap.get(2)).toString());
					logger.info(LOG_HEADER+"TestDATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
					lRespBean = lIoclClient.sendRequest( "BC_INSTDET/ZMSME_INST_DET", lApiRequestBean, MediaType.APPLICATION_JSON);
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getStatusCode());
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getResponseText());
					//
					lApiRequestBean.setBody(new JsonBuilder(pDataMap.get(3)).toString());
					logger.info(LOG_HEADER+"TestDATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
					lRespBean = lIoclClient.sendRequest( "BC_VENLIN/ZVENLIN", lApiRequestBean, MediaType.APPLICATION_JSON);
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getStatusCode());
					logger.info(LOG_HEADER+"TestResponse From IOCL : "+lRespBean.getResponseText());


				}
			}catch(Exception lException){
				lException.printStackTrace();
				logger.info(LOG_HEADER+"Error : "+lException.getMessage());
			}	
		}
		logger.info(LOG_HEADER+"Client Size : "+ clientwiseAdapters.size() +".");
				logger.info(LOG_HEADER+"readSettings() ended.");
	}
}