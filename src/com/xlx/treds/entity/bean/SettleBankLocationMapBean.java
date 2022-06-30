
package com.xlx.treds.entity.bean;

import com.xlx.commonn.CommonAppConstants.Yes;

public class SettleBankLocationMapBean {

    private Long cdId;
    private Long clId;
    private Yes enableSetLoc;
    private Long settleClId;
    private Long l1DCbdid;
    private Long l2DCbdid;
    private Long l1CCbdid;
    private Long l2CCbdid;
    private Boolean isLocationEnable = Boolean.FALSE;
    private Boolean isEnable = Boolean.FALSE;

    public Long getCdId() {
        return cdId;
    }

    public void setCdId(Long pCdId) {
        cdId = pCdId;
    }

    public Long getClId() {
        return clId;
    }

    public void setClId(Long pClId) {
        clId = pClId;
    }

    public Yes getEnableSetLoc() {
        return enableSetLoc;
    }

    public void setEnableSetLoc(Yes pEnableSetLoc) {
        enableSetLoc = pEnableSetLoc;
    }

    public Long getSettleClId() {
        return settleClId;
    }

    public void setSettleClId(Long pSettleClId) {
        settleClId = pSettleClId;
    }

    public Long getL1DCbdid() {
        return l1DCbdid;
    }

    public void setL1DCbdid(Long pL1DCbdid) {
        l1DCbdid = pL1DCbdid;
    }

    public Long getL2DCbdid() {
        return l2DCbdid;
    }

    public void setL2DCbdid(Long pL2DCbdid) {
        l2DCbdid = pL2DCbdid;
    }

    public Long getL1CCbdid() {
        return l1CCbdid;
    }

    public void setL1CCbdid(Long pL1CCbdid) {
        l1CCbdid = pL1CCbdid;
    }

    public Long getL2CCbdid() {
        return l2CCbdid;
    }

    public void setL2CCbdid(Long pL2CCbdid) {
        l2CCbdid = pL2CCbdid;
    }
    
    public void setIsLocationEnable(Boolean pIsLocationEnable) {
    	isLocationEnable = pIsLocationEnable;
    }
    public Boolean getIsLocationEnable() {
    	if(isLocationEnable == null) {
    		return Boolean.FALSE;
    	}
    	return isLocationEnable;
    }
    
    public void setIsEnable(Boolean pIsEnable) {
    	isEnable = pIsEnable;
    }
    public Boolean getIsEnable() {
    	if(isEnable == null) {
    		return Boolean.FALSE;
    	}
    	return isEnable;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}