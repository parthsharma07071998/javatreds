package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;

public class CompanyWorkFlowBean {

    private Long id;
    private Long cdId;
    private CompanyApprovalStatus approvalStatus;
    private String reason;
    private Long recordCreator;
    private Timestamp recordCreateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getCdId() {
        return cdId;
    }

    public void setCdId(Long pCdId) {
        cdId = pCdId;
    }

    public CompanyApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(CompanyApprovalStatus pApprovalStatus) {
        approvalStatus = pApprovalStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String pReason) {
        reason = pReason;
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

}