package com.xlx.treds.master.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.xlx.commonn.IKeyValEnumInterface;

public class AuctionChargeSlabBean  implements Comparable<AuctionChargeSlabBean>{
    public enum ChargeType implements IKeyValEnumInterface<String>{
        Absolute("A","Absolute"),Percentage("P","Percentage"),Threshold("T","Threshold");
        
        private final String code;
        private final String desc;
        private ChargeType(String pCode, String pDesc) {
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
    private Long acpId;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private ChargeType chargeType;
    private BigDecimal chargePercentValue;
    private BigDecimal chargeAbsoluteValue;
    private BigDecimal chargeMaxValue;
    private BigDecimal extendedChargeRate;
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

    public Long getAcpId() {
        return acpId;
    }

    public void setAcpId(Long pAcpId) {
        acpId = pAcpId;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal pMinAmount) {
        minAmount = pMinAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal pMaxAmount) {
        maxAmount = pMaxAmount;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType pChargeType) {
        chargeType = pChargeType;
    }

    public BigDecimal getChargePercentValue() {
        return chargePercentValue;
    }

    public void setChargePercentValue(BigDecimal pChargePercentValue) {
        chargePercentValue = pChargePercentValue;
    }

    public BigDecimal getChargeAbsoluteValue() {
        return chargeAbsoluteValue;
    }

    public void setChargeAbsoluteValue(BigDecimal pChargeAbsoluteValue) {
        chargeAbsoluteValue = pChargeAbsoluteValue;
    }

    public BigDecimal getChargeMaxValue() {
        return chargeMaxValue;
    }

    public void setChargeMaxValue(BigDecimal pChargeMaxValue) {
        chargeMaxValue = pChargeMaxValue;
    }
    
    public BigDecimal getExtendedChargeRate() {
        return extendedChargeRate;
    }

    public void setExtendedChargeRate(BigDecimal pExtendedChargeRate) {
        extendedChargeRate = pExtendedChargeRate;
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

	@Override
	public int compareTo(AuctionChargeSlabBean o) {
		return this.getMinAmount().compareTo(o.getMinAmount());  

	}
}