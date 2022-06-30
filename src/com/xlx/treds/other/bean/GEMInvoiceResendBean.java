package com.xlx.treds.other.bean;

import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class GEMInvoiceResendBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Success("S","Success"),Failed("F","Failed");
        
        private final String code;
        private final String desc;
        private Status(String pCode, String pDesc) {
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
    private Long giId;
    private Status status;
    private Timestamp createDateTime;
    private String responseData;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getGiId() {
        return giId;
    }

    public void setGiId(Long pGiId) {
        giId = pGiId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp pCreateDateTime) {
        createDateTime = pCreateDateTime;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String pResponseData) {
        responseData = pResponseData;
    }

}