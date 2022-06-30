package com.xlx.treds.master.bean;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;

public class ConfirmationWindowBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),Open("B","Open"),Closed("C","Closed");
        
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

    private Timestamp confStartTime;
    private Timestamp confEndTime;
    private Date settlementDate;
    private YesNo active;
    private YesNo skipClearingHoliday;

    public Timestamp getConfStartTime() {
        return confStartTime;
    }

    public void setConfStartTime(Timestamp pConfStartTime) {
        confStartTime = pConfStartTime;
    }

    public Timestamp getConfEndTime() {
        return confEndTime;
    }

    public void setConfEndTime(Timestamp pConfEndTime) {
        confEndTime = pConfEndTime;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date pSettlementDate) {
        settlementDate = pSettlementDate;
    }

    public YesNo getActive() {
        return active;
    }

    public void setActive(YesNo pActive) {
        active = pActive;
    }

    public Status getStatus() {
        long lCurrentTime = System.currentTimeMillis();
        if (lCurrentTime < confStartTime.getTime())
            return Status.Pending;
        else if (lCurrentTime <= confEndTime.getTime())
            return Status.Open;
        else
            return Status.Closed;
    }

    public void setStatus(Status pStatus) {
    }
    public Time getConfStartTimeTime() {
        return confStartTime==null?null:new Time(confStartTime.getTime());
    }

    public void setConfStartTimeTime(Time pConfStartTimeTime) {
    }

    public Time getConfEndTimeTime() {
        return confEndTime==null?null:new Time(confEndTime.getTime());
    }

    public void setConfEndTimeTime(Time pConfEndTimeTime) {
    }
    public YesNo getSkipClearingHoliday() {
        return skipClearingHoliday;
    }

    public void setSkipClearingHoliday(YesNo pSkipClearingHoliday) {
        skipClearingHoliday = pSkipClearingHoliday;
    }

}