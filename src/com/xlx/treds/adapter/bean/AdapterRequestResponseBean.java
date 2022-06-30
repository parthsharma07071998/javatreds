package com.xlx.treds.adapter.bean;

import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class AdapterRequestResponseBean {
    public enum Type implements IKeyValEnumInterface<String>{
        In("I","In"),Out("O","Out");
        
        private final String code;
        private final String desc;
        private Type(String pCode, String pDesc) {
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
    public enum ApiRequestType implements IKeyValEnumInterface<String>{
        GET("GET","GET"),POST("POST","POST"),PUT("PUT","PUT");
        
        private final String code;
        private final String desc;
        private ApiRequestType(String pCode, String pDesc) {
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
    public enum RequestStatus implements IKeyValEnumInterface<String>{
        Sent("S","Sent"),Failed("F","Failed");
        
        private final String code;
        private final String desc;
        private RequestStatus(String pCode, String pDesc) {
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
    public enum ResponseAckStatus implements IKeyValEnumInterface<String>{
        Read("R","Read"),Not_Read("N","Not Read");
        
        private final String code;
        private final String desc;
        private ResponseAckStatus(String pCode, String pDesc) {
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
    public enum ApiResponseStatus implements IKeyValEnumInterface<String>{
        Success("S","Success"),Failed("F","Failed");
        
        private final String code;
        private final String desc;
        private ApiResponseStatus(String pCode, String pDesc) {
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
    public enum ProvResponseAckStatus implements IKeyValEnumInterface<String>{
        Read("R","Read"),Not_Read("N","Not Read");
        
        private final String code;
        private final String desc;
        private ProvResponseAckStatus(String pCode, String pDesc) {
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

    private Long id;
    private String entityCode;
    private Long processId;
    private String key;
    private Type type;
    private ApiRequestType apiRequestType;
    private String apiRequestUrl;
    private String apiRequestData;
    private String uid;
    private Timestamp timestamp;
    private RequestStatus requestStatus;
    private ResponseAckStatus responseAckStatus;
    private String apiResponseUrl;
    private String apiResponseData;
    private ApiResponseStatus apiResponseStatus;
    private String apiResponseDataReturned;
    private ProvResponseAckStatus provResponseAckStatus;
    private String provResponseData;
    private Timestamp lastSendDateTime;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String pEntityCode) {
        entityCode = pEntityCode;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long pProcessId) {
        processId = pProcessId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String pKey) {
        key = pKey;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public ApiRequestType getApiRequestType() {
        return apiRequestType;
    }

    public void setApiRequestType(ApiRequestType pApiRequestType) {
        apiRequestType = pApiRequestType;
    }

    public String getApiRequestUrl() {
        return apiRequestUrl;
    }

    public void setApiRequestUrl(String pApiRequestUrl) {
        apiRequestUrl = pApiRequestUrl;
    }

    public String getApiRequestData() {
        return apiRequestData;
    }

    public void setApiRequestData(String pApiRequestData) {
        apiRequestData = pApiRequestData;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String pUid) {
        uid = pUid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp pTimestamp) {
        timestamp = pTimestamp;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus pRequestStatus) {
        requestStatus = pRequestStatus;
    }

    public ResponseAckStatus getResponseAckStatus() {
        return responseAckStatus;
    }

    public void setResponseAckStatus(ResponseAckStatus pResponseAckStatus) {
        responseAckStatus = pResponseAckStatus;
    }

    public String getApiResponseUrl() {
        return apiResponseUrl;
    }

    public void setApiResponseUrl(String pApiResponseUrl) {
        apiResponseUrl = pApiResponseUrl;
    }

    public String getApiResponseData() {
        return apiResponseData;
    }

    public void setApiResponseData(String pApiResponseData) {
        apiResponseData = pApiResponseData;
    }

    public ApiResponseStatus getApiResponseStatus() {
        return apiResponseStatus;
    }

    public void setApiResponseStatus(ApiResponseStatus pApiResponseStatus) {
        apiResponseStatus = pApiResponseStatus;
    }

    public String getApiResponseDataReturned() {
        return apiResponseDataReturned;
    }

    public void setApiResponseDataReturned(String pApiResponseDataReturned) {
        apiResponseDataReturned = pApiResponseDataReturned;
    }

    public ProvResponseAckStatus getProvResponseAckStatus() {
        return provResponseAckStatus;
    }

    public void setProvResponseAckStatus(ProvResponseAckStatus pProvResponseAckStatus) {
        provResponseAckStatus = pProvResponseAckStatus;
    }

    public String getProvResponseData() {
        return provResponseData;
    }

    public void setProvResponseData(String pProvResponseData) {
        provResponseData = pProvResponseData;
    }

    public Timestamp getLastSendDateTime() {
        return lastSendDateTime;
    }

    public void setLastSendDateTime(Timestamp pLastSendDateTime) {
        lastSendDateTime = pLastSendDateTime;
    }

    public Timestamp getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Timestamp pRecordUpdateTime) {
        recordUpdateTime = pRecordUpdateTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}