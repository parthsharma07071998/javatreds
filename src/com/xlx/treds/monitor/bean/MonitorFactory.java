package com.xlx.treds.monitor.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.user.bean.IAppUserBean;

import groovy.json.JsonSlurper;

public class MonitorFactory
{
	public static Logger logger = Logger.getLogger(MonitorFactory.class);
	private static MonitorFactory theInstance;
	//
	public static final String SETTINGS_JSON = "monitors.json";
	//
	public static final String KEY_MONITORS = "monitors";
	public static final String KEY_NAME  = "name";
	public static final String KEY_DESCRIPTION  = "description";
	public static final String KEY_FREQUENCY  = "frequency";
	public static final String KEY_STARTDATETIME  = "startDateTime";
	public static final String KEY_DATAHANDLER  = "dataHandler";
	public static final String KEY_UIHANDLER  = "templateName";
	public static final String KEY_GROUP = "group";
	//
	protected Map<String,MonitorMetaBean> monitorBeanHash;// key=monitorcode;value=meata bean 
	protected Map<String,IMonitorHandler> monitorHandlerHash; //key=monitorcode; value=IMonitorHandler

	public static MonitorFactory getInstance()
	{
	    if (theInstance == null)
	    {
	        synchronized(MonitorFactory.class)
	        {
	            if (theInstance == null)
	            {
	                MonitorFactory lMonitoryFactory;
	                try
	                {
	                    lMonitoryFactory = MonitorFactory.class.newInstance();
	                    lMonitoryFactory.initialize();
	                    theInstance = lMonitoryFactory;
	                }
	                catch (Exception lException)
	                {
	                    logger.error("Error while instantiating MonitoryFactory",lException);
	                }
	            }
	        }
	    }
	    return theInstance;
	}

	protected MonitorFactory()
	{
	    monitorBeanHash = new HashMap<String, MonitorMetaBean>();
	    monitorHandlerHash = new HashMap<String,IMonitorHandler>();
	}
	
	public void initialize()
	{
	    //read the json and create objects of monitors.
		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		
		List<Map> lMonitorsList = (List<Map>) lMasterConfig.get(KEY_MONITORS);
		int lCount = lMonitorsList.size();
		//
//		String lDataHandler, lUIHandler, lGroup, lName, lDesc;
//		int lFrequency = 0;
//		Date lStartDateTime = null;
		MonitorMetaBean lMMBean = null;
		GenericDAO<MonitorMetaBean> lMonitorDao = new GenericDAO<>(MonitorMetaBean.class);
		List<ValidationFailBean> lFailList = null;
		Map<String, Object> lSettingMap = null;
	    IMonitorHandler lMonitorHandler = null;
		//
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			lSettingMap = (Map<String, Object>) lMonitorsList.get(lPtr);
			lMMBean = new MonitorMetaBean();
			lFailList = lMonitorDao.getBeanMeta().validateAndParse(lMMBean, lSettingMap,lFailList);
			
			if(!monitorBeanHash.containsKey(lMMBean.getCode())){
				//fetch the template and put it in the metabean
				updateHandelbarTemplate(lMMBean);
				monitorBeanHash.put(lMMBean.getCode(), lMMBean);
				//
				try
				{
					Constructor constructor = Class.forName(lMMBean.getDataHandler()).getConstructor();
					lMonitorHandler = (IMonitorHandler) constructor.newInstance();
					if(lMonitorHandler!=null){
						monitorHandlerHash.put(lMMBean.getCode(), lMonitorHandler);
					}
				}
				catch (Exception lException)
				{
					logger.info("Error while creating Monitor : "+ lMMBean.getCode()+ " : " +lException.getMessage());
				}

			}

		}
	}
	
	public List<MonitorMetaBean> getMonitorHandlerList(IAppUserBean pAppUserBean){
		//get all the Monitors - check the sec-keys with Appuser and then only those should be passed forward. to the ui
		List<MonitorMetaBean> lList = new ArrayList<MonitorMetaBean>();
		for ( MonitorMetaBean lMMBean : monitorBeanHash.values() ){
//			if (AccessControlHelper.getInstance().hasAccess(lMMBean.getSecKey(), pAppUserBean)){
//				lList.add(lMMBean);
//			}
			lList.add(lMMBean);
		}
		return lList;
	}
	
	public IMonitorHandler getMonitorHandler(String pMonitorCode) 
	{
	    IMonitorHandler lMonitorHandler = null;
	    MonitorMetaBean lMMBean = null;
		try
		{
			lMMBean = monitorBeanHash.get(pMonitorCode);
			Constructor constructor = Class.forName(lMMBean.getDataHandler()).getConstructor();
			lMonitorHandler = (IMonitorHandler) constructor.newInstance();
		}
		catch (Exception lException)
		{
			logger.info("Error while creating Monitor : "+ pMonitorCode+ " : " +lException.getMessage());
		}
	    return lMonitorHandler;
	}
	
	private void updateHandelbarTemplate(MonitorMetaBean pMonitorMetaBean){
		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pMonitorMetaBean.getTemplateName()+".tpl");
		BufferedReader reader = new BufferedReader(new InputStreamReader(lInputStream));
		StringBuilder out = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			pMonitorMetaBean.setHandelbarTemplate(out.toString());
		} catch (IOException e) {
			logger.info("Error while reading monitor template : " + pMonitorMetaBean.getCode());
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					logger.info("Error while reading monitor template - closing reader : " + pMonitorMetaBean.getCode());
				}
			}
		}
	}
	
/*	public IMonitorHandler getMessageBeanFromMessage(String pMessage)
	{
	    String[] lFields = CommonUtilities.splitString(pMessage,CommonConstants.COLUMN_SEPARATOR);
	    IMonitorHandler lMessageBean = getMonitorBean(lFields[0]);
	    
	    //if (lMessageBean != null)
	    //    lMessageBean.fill(lFields,null);
	    return lMessageBean;
	}
	
*/
}



