package com.xlx.treds.notialrt.bean;

import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class NotiAlrtBean {
    public enum Type implements IKeyValEnumInterface<String>{
        Instrument_Invoice("ALRTINSTINV","Instrument Invoice"),Instrument_Amount("ALRTINSTAMT","Instrument Amount"),Instrument_PO_Number("ALRTINSTPO","Instrument PO Number"),Purchaser_Supplier_Link("ALRTPSLNK","Purchaser Supplier Link");
        
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

    private Long id;
    private Type type;
    private String key;
    private String alertDesc;
    private Timestamp recordCreateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String pKey) {
        key = pKey;
    }

    public String getAlertDesc() {
        return alertDesc;
    }

    public void setAlertDesc(String pAlertDesc) {
        alertDesc = pAlertDesc;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

}