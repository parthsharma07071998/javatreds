package com.xlx.treds.adapter;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;

public interface IClientAdapter  {

	public String convertClientDataToTredsData(ProcessInformationBean pProcessInformationBean) throws Exception;
	public String convertTredsDataToClientData(ProcessInformationBean pProcessInformationBean) throws Exception;
	//
	public boolean connectClient();	
	public boolean isClientConnected();	
	public boolean sendResponseToClient(ProcessInformationBean pProcessInformationBean) throws Exception;
	
	public String getURL(Long pProcessId);
	
	public Long logOutgoing(ProcessInformationBean pProcessInformationBean, String pOutUrl, ApiResponseBean pApiResponseBean,AdapterRequestResponseBean pAdapterRequestResponseBean, boolean pNew);
	public Long logInComing(ProcessInformationBean pProcessInformationBean, String pInApiUrl, ApiResponseStatus pApiResponseStatus, boolean pNew, boolean pValidateUniqueRequest) throws CommonBusinessException;
	//
	public void addPostActionToQueue(ProcessInformationBean pOldProcessInformationBean);
	public void performActionPostIncoming(ProcessInformationBean pOldProcessInformationBean);
	boolean reSendResponseToClient(ProcessInformationBean pProcessInformationBean, AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception;
}
