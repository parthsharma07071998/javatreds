package com.xlx.treds.stats.bean;

import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class StatsCacheBean {
    public enum Type implements IKeyValEnumInterface<String>{
        Instrument_Invoice("INSTINV","Instrument Invoice"),Purchaser_Supplier_Link("PSLINK","Purchaser Supplier Link");
        
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
    private String value;
    private Timestamp expiry;

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

    public String getValue() {
        return value;
    }

    public void setValue(String pValue) {
        value = pValue;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp pExpiry) {
        expiry = pExpiry;
    }

}