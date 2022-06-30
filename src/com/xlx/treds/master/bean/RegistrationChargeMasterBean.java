
package com.xlx.treds.master.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.RegEntityType;

public class RegistrationChargeMasterBean {

    private RegEntityType entityType;
    private BigDecimal registrationCharge;
    private BigDecimal annualCharge;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public RegEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(RegEntityType pEntityType) {
        entityType = pEntityType;
    }

    public BigDecimal getRegistrationCharge() {
        return registrationCharge;
    }

    public void setRegistrationCharge(BigDecimal pRegistrationCharge) {
        registrationCharge = pRegistrationCharge;
    }

    public BigDecimal getAnnualCharge() {
        return annualCharge;
    }

    public void setAnnualCharge(BigDecimal pAnnualCharge) {
        annualCharge = pAnnualCharge;
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
	public String toString() {
		return super.toString();
	}
}