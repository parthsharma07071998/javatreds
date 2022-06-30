package com.xlx.treds.instrument.bean;

import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class BHELPEMInstrumentBean {
    public enum Type implements IKeyValEnumInterface<String>{
        Instrument("I","Instrument"),Leg1("L1","Leg1"),Leg2("L2","Leg2"),Leg2Future("FU","Leg2Future"),Leg1Interest("LI","Leg1Interest");
        
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
    private Long inId;
    private Type type;
    private Timestamp createTime;
    private Timestamp sentTime;
    private String entityCode;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp pCreateTime) {
        createTime = pCreateTime;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp pSentTime) {
        sentTime = pSentTime;
    }

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String pEntityCode) {
		entityCode = pEntityCode;
	}

}