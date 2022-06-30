package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class FactoringUnitBidsReportBean {
    public enum FactStatus implements IKeyValEnumInterface<String>{
        Ready_For_Auction("RDY","Ready For Auction"),Active("ACT","Active"),Factored("FACT","Factored"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Withdrawn("WTHDRN","Withdrawn"),Suspended("SUSP","Suspended"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed");
        
        private final String code;
        private final String desc;
        private FactStatus(String pCode, String pDesc) {
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
    public enum BidStatus implements IKeyValEnumInterface<String>{
        Active("ACT","Active"),Deleted("DEL","Deleted"),Deleted_By_Owner("DLO","Deleted By Owner"),Accepted("APT","Accepted"),Expired("EXP","Expired"),NotAccepted("NAT","NotAccepted");
        
        private final String code;
        private final String desc;
        private BidStatus(String pCode, String pDesc) {
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
    public enum BidType implements IKeyValEnumInterface<String>{
        Reserved("RES","Reserved"),Open("OPN","Open");
        
        private final String code;
        private final String desc;
        private BidType(String pCode, String pDesc) {
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
    public enum AppStatus implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),Approved("A","Approved"),Rejected("R","Rejected"),Withdrawn("W","Withdrawn");
        
        private final String code;
        private final String desc;
        private AppStatus(String pCode, String pDesc) {
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

    private Long fuId;
    private Long bdId;
    private BigDecimal amount;
    private String purchaser;
    private FactStatus factStatus;
    private Timestamp factorStartDateTime;
    private Timestamp factorEndDateTime;
    private BigDecimal interest;
    private String financierEntity;
    private Long financierAuId;
    private String financierName;
    private BigDecimal rate;
    private BigDecimal haircut;
    private Date validTill;
    private BidStatus bidStatus;
    private Timestamp timestamp;
    private Long lastAuId;
    private String lastLoginId;
    private BidType bidType;
    private BigDecimal provRate;
    private BigDecimal provHaircut;
    private Date provValidTill;
    private BidType provBidType;
    private AppStatus appStatus;
    private Long checkerAuId;
    private String checkerName;

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long pBdId) {
        bdId = pBdId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public FactStatus getFactStatus() {
        return factStatus;
    }

    public void setFactStatus(FactStatus pFactStatus) {
        factStatus = pFactStatus;
    }

    public Timestamp getFactorStartDateTime() {
        return factorStartDateTime;
    }

    public void setFactorStartDateTime(Timestamp pFactorStartDateTime) {
        factorStartDateTime = pFactorStartDateTime;
    }

    public Timestamp getFactorEndDateTime() {
        return factorEndDateTime;
    }

    public void setFactorEndDateTime(Timestamp pFactorEndDateTime) {
        factorEndDateTime = pFactorEndDateTime;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal pInterest) {
        interest = pInterest;
    }

    public String getFinancierEntity() {
        return financierEntity;
    }

    public void setFinancierEntity(String pFinancierEntity) {
        financierEntity = pFinancierEntity;
    }

    public Long getFinancierAuId() {
        return financierAuId;
    }

    public void setFinancierAuId(Long pFinancierAuId) {
        financierAuId = pFinancierAuId;
    }

    public String getFinancierName() {
        return financierName;
    }

    public void setFinancierName(String pFinancierName) {
        financierName = pFinancierName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal pRate) {
        rate = pRate;
    }

    public BigDecimal getHaircut() {
        return haircut;
    }

    public void setHaircut(BigDecimal pHaircut) {
        haircut = pHaircut;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date pValidTill) {
        validTill = pValidTill;
    }

    public BidStatus getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(BidStatus pBidStatus) {
        bidStatus = pBidStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp pTimestamp) {
        timestamp = pTimestamp;
    }

    public Long getLastAuId() {
        return lastAuId;
    }

    public void setLastAuId(Long pLastAuId) {
        lastAuId = pLastAuId;
    }

    public String getLastLoginId() {
        return lastLoginId;
    }

    public void setLastLoginId(String pLastLoginId) {
        lastLoginId = pLastLoginId;
    }

    public BidType getBidType() {
        return bidType;
    }

    public void setBidType(BidType pBidType) {
        bidType = pBidType;
    }

    public BigDecimal getProvRate() {
        return provRate;
    }

    public void setProvRate(BigDecimal pProvRate) {
        provRate = pProvRate;
    }

    public BigDecimal getProvHaircut() {
        return provHaircut;
    }

    public void setProvHaircut(BigDecimal pProvHaircut) {
        provHaircut = pProvHaircut;
    }

    public Date getProvValidTill() {
        return provValidTill;
    }

    public void setProvValidTill(Date pProvValidTill) {
        provValidTill = pProvValidTill;
    }

    public BidType getProvBidType() {
        return provBidType;
    }

    public void setProvBidType(BidType pProvBidType) {
        provBidType = pProvBidType;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(AppStatus pAppStatus) {
        appStatus = pAppStatus;
    }

    public Long getCheckerAuId() {
        return checkerAuId;
    }

    public void setCheckerAuId(Long pCheckerAuId) {
        checkerAuId = pCheckerAuId;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String pCheckerName) {
        checkerName = pCheckerName;
    }

}