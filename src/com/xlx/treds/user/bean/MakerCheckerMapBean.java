package com.xlx.treds.user.bean;

import java.sql.Timestamp;

import com.xlx.commonn.IKeyValEnumInterface;

public class MakerCheckerMapBean {
    public enum CheckerType implements IKeyValEnumInterface<String>{
        Instrument("IN","Instrument"),Platform_Limit("PL","Platform Limit"),Buyer_Limit("BL","Buyer Limit"),Buyer_Seller_Limit("BSL","Buyer Seller Limit"),User_Limit("UL","User Limit"),Bid("BID","Bid"),InstrumentCounter("INC","InstrumentCounter");
        private final String code;
        private final String desc;
        private CheckerType(String pCode, String pDesc) {
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
    private CheckerType checkerType;
    private Long makerId;
    private Long checkerId;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public CheckerType getCheckerType() {
        return checkerType;
    }

    public void setCheckerType(CheckerType pCheckerType) {
        checkerType = pCheckerType;
    }

    public Long getMakerId() {
        return makerId;
    }

    public void setMakerId(Long pMakerId) {
        makerId = pMakerId;
    }

    public Long getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(Long pCheckerId) {
        checkerId = pCheckerId;
    }

    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordUpdator() {
        return recordUpdator;
    }

    public void setRecordUpdator(Long pRecordUpdator) {
        recordUpdator = pRecordUpdator;
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