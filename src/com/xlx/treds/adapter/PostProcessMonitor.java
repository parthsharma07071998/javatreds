package com.xlx.treds.adapter;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Delayed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.collection.DelayedQueue;
import com.xlx.common.io.ManagedThread;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.GenericDAO;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class PostProcessMonitor extends ManagedThread  {
    private static final Logger logger = LoggerFactory.getLogger(PostProcessMonitor.class);
	
    private static PostProcessMonitor theInstance;
	private DelayedQueue<Object[]> processQueue = new DelayedQueue<Object[]>(0);

    public static PostProcessMonitor createInstance() {
        if (theInstance == null) {
            synchronized (PostProcessMonitor.class) {
                if (theInstance == null) {
                	PostProcessMonitor lPostProcessMonitor = new PostProcessMonitor();
                    lPostProcessMonitor.newThread("PosProcessMon");
                    theInstance = lPostProcessMonitor;
                }
            }
        }
        return theInstance;
    }
    
    public static PostProcessMonitor getInstance() {
        return theInstance;
    }
    
    private PostProcessMonitor() {
        super();
    }
    
	@Override
	public boolean initThread() {
		//processPendingList();
		return true;
	}

	private boolean isQueueEmpty(){
		return processQueue.getQueueSize()<=0;
	}
	private Object[] getDataFromQueue(){
		Object[] lData = null;
		lData = processQueue.getMessage();
		return lData;
	}
	
	@Override
	public void serviceThread() {
		while (!isQueueEmpty()){
			Object[] lData = getDataFromQueue();
			if(lData != null){
				IClientAdapter lClientAdapter = null;
				ProcessInformationBean lOldProcessInformationBean  = null;
				AdapterRequestResponseBean lAdapterRequestResponseBean = null;
				try {
					lClientAdapter = (IClientAdapter)lData[0];
					lOldProcessInformationBean  = (ProcessInformationBean)lData[1];
					lAdapterRequestResponseBean = null;
					if(lData.length > 2){
						lAdapterRequestResponseBean = (AdapterRequestResponseBean)lData[2];
					}
					if(lAdapterRequestResponseBean!=null){
						if( !lClientAdapter.reSendResponseToClient(lOldProcessInformationBean ,lAdapterRequestResponseBean) ){
								//processQueue.addMessage(lData,9*100*1000);
						}
					}else{
						lClientAdapter.performActionPostIncoming(lOldProcessInformationBean);
					}
				} catch (Exception e) {
					logger.info("PostProcessMonitor :"+e.getMessage());
					//processQueue.addMessage(lData,9*100*1000);
				}finally{
					if(lOldProcessInformationBean!=null&&lOldProcessInformationBean.getConnection()!=null){

						try {
							if(!lOldProcessInformationBean.getConnection().isClosed()){
								lOldProcessInformationBean.getConnection().close();
							}
						} catch (Exception e) {
							logger.info("PostProcessMonitor : closing conection :"+e.getMessage());
						}
					}
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void destroyThread() {
	}
	
	public void addPostProcess(IClientAdapter pClientAdapter, ProcessInformationBean pProcessInformationBean , AdapterRequestResponseBean pAdapterRequestResponseBean ){
		Object[] lData = null;
		if(pAdapterRequestResponseBean == null){
			lData = new Object[] { pClientAdapter, pProcessInformationBean};
		}else{
			lData = new Object[] { pClientAdapter, pProcessInformationBean, pAdapterRequestResponseBean  };
		}
		processQueue.addMessage(lData);
	}
	
	public void addL1DetailsForProcessing(List<Object[]> pLeg1DetailsList){
		Object[] lData = null;
		ProcessInformationBean lProcessInformationBean = null;
		for(Object[] lLeg1Details:pLeg1DetailsList){
			FactoringUnitBean lFactBean = (FactoringUnitBean) lLeg1Details[0];
			InstrumentBean lInstBean = (InstrumentBean) lLeg1Details[1];
			ObligationBean lOBBean = (ObligationBean) lLeg1Details[2];
			List<ObligationSplitsBean> lSplitsBeans = (List<ObligationSplitsBean>) lLeg1Details[3];
			IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstBean.getAdapterEntity());
			try {
				if(lClientAdapter!=null){
					lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_LEG1SETTLED, DBHelper.getInstance().getConnection());
					lProcessInformationBean.setTredsDataForProcessing(lLeg1Details);
					lProcessInformationBean.setKey(lInstBean.getId().toString());
					lProcessInformationBean.setEntityCode(lInstBean.getAdapterEntity());
					addPostProcess (lClientAdapter , lProcessInformationBean ,null);
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
	}
	
	public void addL2DetailsForProcessing(List<Object[]> pLeg1DetailsList){
		Object[] lData = null;
		ProcessInformationBean lProcessInformationBean = null;
		for(Object[] lLeg1Details:pLeg1DetailsList){
			FactoringUnitBean lFactBean = (FactoringUnitBean) lLeg1Details[0];
			InstrumentBean lInstBean = (InstrumentBean) lLeg1Details[1];
			ObligationBean lOBBean = (ObligationBean) lLeg1Details[2];
			List<ObligationSplitsBean> lSplitsBeans = (List<ObligationSplitsBean>) lLeg1Details[3];
			IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstBean.getAdapterEntity());
			try {
				if(lClientAdapter!=null){
					lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_LEG2SETTLED, DBHelper.getInstance().getConnection());
					lProcessInformationBean.setTredsDataForProcessing(lLeg1Details);
					lProcessInformationBean.setKey(lInstBean.getId().toString());
					lProcessInformationBean.setEntityCode(lInstBean.getAdapterEntity());
					addPostProcess (lClientAdapter , lProcessInformationBean ,null);
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
	}


	private  Map<String, List<AdapterRequestResponseBean>> getPendingRequests(){
		Map<String, List<AdapterRequestResponseBean>> lReturnList = null;
	    GenericDAO<AdapterRequestResponseBean> lARRDao = null;
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuffer lSql = new StringBuffer();
		//
		lSql.append("SELECT * FROM AdapterRequestResponses WHERE ARRRecordVersion > 0 ");
		lSql.append(" AND ARRType =  ").append(lDbHelper.formatString(AdapterRequestResponseBean.Type.Out.getCode()));
		lSql.append(" AND ARRAPIRESPONSESTATUS =  ").append(lDbHelper.formatString(AdapterRequestResponseBean.RequestStatus.Failed.getCode()));
		lSql.append(" ORDER BY ARRId ASC ");
		logger.info(lSql.toString());
	    //
		try(Connection lConnection = lDbHelper.getConnection(); Statement lStatement =  lConnection.createStatement(); ){
		    lARRDao = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class); 
		    List<AdapterRequestResponseBean> lList = lARRDao.findListFromSql(lConnection, lSql.toString(), 0);
		    if(lList!=null && !lList.isEmpty()){
		    	List<AdapterRequestResponseBean> lTmpAdapterList = null;
		    	lReturnList = new HashMap<String, List<AdapterRequestResponseBean>>();
		    	for(AdapterRequestResponseBean lBean : lList){
		    		if(!lReturnList.containsKey(lBean.getEntityCode())){
		    			lTmpAdapterList = new ArrayList<AdapterRequestResponseBean>();
		    			lReturnList.put(lBean.getEntityCode(), lTmpAdapterList);
		    		}
		    		lTmpAdapterList = lReturnList.get(lBean.getEntityCode());
		    		lTmpAdapterList.add(lBean);
		    	}
		    }
		} catch (Exception lEx) {
			logger.info("Error in getPendingRequests : " + lEx.getMessage());
		}
		return lReturnList;
	}
	
	private void processPendingList(){
		Map<String, List<AdapterRequestResponseBean>> lList =  getPendingRequests();
		if(lList!=null){
			ProcessInformationBean lProcessInformationBean = null;
			
			try (Connection lConnection = DBHelper.getInstance().getConnection();){
				for (String lEntity : lList.keySet()){
					IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lEntity);
					for (AdapterRequestResponseBean lArrBean : lList.get(lEntity)){
						if(lClientAdapter!=null){
							try {
								lProcessInformationBean = new ProcessInformationBean(lArrBean.getProcessId(), lConnection);
								addPostProcess (lClientAdapter , lProcessInformationBean , lArrBean);
							} catch (Exception e) {
								logger.info("processPendingList 1: "+e.getMessage());
							}
						}
					}
				}
			}catch (Exception lEx) {
				logger.info("processPendingList 2: "+lEx.getMessage());
			}

		}
	}

}


