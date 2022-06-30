package com.xlx.treds.adapter;

import java.sql.Connection;

import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;

public class ProcessInformationBean {

    public static final Long PROCESSID_PURSUPLINK = new Long(1);
    public static final Long PROCESSID_INST_PRE = new Long(2);
    public static final Long PROCESSID_INST = new Long(3);
    public static final Long PROCESSID_INST_ACK = new Long(4);
    public static final Long PROCESSID_FACTORINGUNIT_STATUS = new Long(5);
    public static final Long PROCESSID_LEG1SETTLED = new Long(6);
    public static final Long PROCESSID_LEG2SETTLED = new Long(7);
    
    
	private Long processId;
	private String entityCode;
	private Connection connection;
	private String uid;
	private String key;
	//INBOUND
	//C->T
	private Object clientData; //String-Map json-xml
	private Object processedTredsData; //bean
	//RETURN DATA
	//T->C
	private Object tredsData; //bean
	private Object processedClientData; //String json-xml 
	//
	private AdapterRequestResponseBean adapterRequestResponseBean;
	//
	private String tredsReturnResponseData;
	//
	public ProcessInformationBean(Long pProcessId, Connection pConnection){
		super();
		processId = pProcessId;
		connection = pConnection;
	}
	
	public Long getProcessId() {
		return processId;
	}

	public String getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}
	public Connection getConnection(){
		return connection;
	}
	//INBOUND
	public Object getClientDataForProcessing() {
		return clientData;
	}
	public void setClientDataForProcessing(Object pClientDataStr) {
		clientData = pClientDataStr;
	}
	public void setProcessedTredsData(Object pTredsBean) {
		processedTredsData = pTredsBean;
	}
	public Object getProcessedTredsData() {
		return processedTredsData;
	}
	//OUTBOUND
	public Object getTredsDataForProcessing() {
		return tredsData;
	}
	public void setTredsDataForProcessing(Object pTredsDataBean) {
		tredsData = pTredsDataBean;
	}
	public Object getProcessedClientData() {
		return processedClientData;
	}
	public void setProcessedClientData(Object processedClientDataStr) {
		this.processedClientData = processedClientDataStr;
	}
	
	public AdapterRequestResponseBean getAdapterRequestResponseBean(){
		return adapterRequestResponseBean;
	}
	
	public void setAdapterRequestResponseBean(AdapterRequestResponseBean pAdapterRequestResponseBean){
		adapterRequestResponseBean = pAdapterRequestResponseBean;
	}
	
	public String getTredsReturnResponseData(){
		return tredsReturnResponseData;
	}
	
	public void setTredsReturnResponseData(String pTredsReturnResponseData){
		tredsReturnResponseData = pTredsReturnResponseData;
	}
	
	public String getUID(){
		return uid;
	}
	
	public void setUID(String pUID){
		uid = pUID;
	}
	
	public String getKey(){
		return key;
	}
	
	public void setKey(String pKey){
		key = pKey;
	}
}

