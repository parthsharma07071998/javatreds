package com.xlx.treds.stats;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ConfigRepository.Config;

import groovy.json.JsonSlurper;

public class StatsCacheFactory {
	private static Logger logger = Logger.getLogger(StatsCacheFactory.class);
	private static final String LOG_HEADER = "StatsCacheFactory :: ";

	public static final String STAT = "stat";
	public static final String CLASS = "class";
	private static final String SETTINGS_JSON = "statsConfig.json";
	public static final String STATSLIST = "statsList";
	public static final String CONFIG = "config";
	//
	public static final String STAT_INSTRUMENT_INV = "INSTINV";
	public static final String STAT_PURCHASER_SUPPLIER_LINK_FU = "PSLNK";
	//
	private static StatsCacheFactory theInstance;
	private Map<String, IStatsCacheGenerator> statsCacheGenerators = null;
	//

	public static StatsCacheFactory getInstance() {
		if (theInstance == null) {
			synchronized (StatsCacheFactory.class) {
				if (theInstance == null) {
					StatsCacheFactory tmpTheInstance = new StatsCacheFactory();
					try{
						tmpTheInstance.statsCacheGenerators = new HashMap<String,IStatsCacheGenerator>();
						tmpTheInstance.readSettings();
						theInstance = tmpTheInstance;
					}catch(Exception lEx){
						logger.info("Error in StatsCacheFactory : " + lEx.getMessage());
					}
				}
			}
		}
		return theInstance;
	}
	
	public IStatsCacheGenerator getStatsCacheGenerator(String pStatType){
		IStatsCacheGenerator lStatsCacheGenerator = null;
		if(StringUtils.isNotEmpty(pStatType)){
			if(statsCacheGenerators!= null && statsCacheGenerators.containsKey(pStatType)){
				lStatsCacheGenerator = statsCacheGenerators.get(pStatType);
			}
		}
		return lStatsCacheGenerator;
	}
	
	public List<String> getStatsList(){
		List<String> lTmp = null;
		if(statsCacheGenerators!=null) {
			lTmp = new ArrayList<>();
			lTmp.addAll(statsCacheGenerators.keySet());
		}
		return lTmp;
	}
	
	private void readSettings(){
		//Here we will read the list of Adapter for each client, and then load the specific adapter using the class name
		//logger.info(LOG_HEADER+"readSettings() called.");

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		List<Map> lSettings = (List<Map>) lMasterConfig.get(STATSLIST); 
		int lCount = lSettings.size();
		String lStatType, lFullClassName;
		Object lConfigMap = null;
		IStatsCacheGenerator IStatsCacheGenerator =null;
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lSettingMap = (Map<String, Object>) lSettings.get(lPtr);
			lFullClassName =(String) lSettingMap.get(CLASS);
			lStatType = (String) lSettingMap.get(STAT);
			lConfigMap = (Object) lSettingMap.get(CONFIG);
	       try
	        {
	    	   Constructor constructor = Class.forName(lFullClassName).getConstructor(String.class,Object.class);
	    	   IStatsCacheGenerator = (IStatsCacheGenerator) constructor.newInstance(lStatType,lConfigMap);
	    	   if(IStatsCacheGenerator!=null){
	    		   statsCacheGenerators.put(lStatType, IStatsCacheGenerator);
	        	}
	        }
	        catch (Exception lException)
	        {
	    		logger.info(LOG_HEADER+"Error : "+lException.getMessage());
	        }
		}
		logger.info(LOG_HEADER+"Size : "+ statsCacheGenerators.size() +".");
		logger.info(LOG_HEADER+"readSettings() ended.");
	}
	
}