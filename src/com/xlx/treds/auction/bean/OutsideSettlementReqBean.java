package com.xlx.treds.auction.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import com.xlx.commonn.IKeyValEnumInterface;

public class OutsideSettlementReqBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Created("CRT","Created"),Sent("SNT","Sent"),Rejected("REJ","Rejected"),Approved("APP","Approved"),Returned("RET","Returned");
        
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

    private Long id;
    private String buyerCode;
    private String financierCode;
    private Status status;
    private List<OutsideSettlementDetBean> outSettleDetailList;
    private String splitList;
    private Date createDate;
    private Long createrAuId;
    private String createrName;
    private String createrLogin;
    private Date approveRejectDate;
    private Long approveRejectAuId;
    private String approveRejectName;
    private String approveRejectLogin;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    public Long tab;
    
    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String pBuyerCode) {
        buyerCode = pBuyerCode;
    }

    public String getFinancierCode() {
        return financierCode;
    }

    public void setFinancierCode(String pFinancierCode) {
        financierCode = pFinancierCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public List<OutsideSettlementDetBean> getOutSettleDetailList() {
        return outSettleDetailList;
    }

    public void setOutSettleDetailList(List<OutsideSettlementDetBean> pOutSettleDetailList) {
        outSettleDetailList = pOutSettleDetailList;
    }

    public String getSplitList() {
        return splitList;
    }

    public void setSplitList(String pSplitList) {
        splitList = pSplitList;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date pCreateDate) {
        createDate = pCreateDate;
    }

    public Long getCreaterAuId() {
        return createrAuId;
    }

    public void setCreaterAuId(Long pCreaterAuId) {
        createrAuId = pCreaterAuId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String pCreaterName) {
        createrName = pCreaterName;
    }

    public String getCreaterLogin() {
        return createrLogin;
    }

    public void setCreaterLogin(String pCreaterLogin) {
        createrLogin = pCreaterLogin;
    }

    public Date getApproveRejectDate() {
        return approveRejectDate;
    }

    public void setApproveRejectDate(Date pApproveRejectDate) {
        approveRejectDate = pApproveRejectDate;
    }

    public Long getApproveRejectAuId() {
        return approveRejectAuId;
    }

    public void setApproveRejectAuId(Long pApproveRejectAuId) {
        approveRejectAuId = pApproveRejectAuId;
    }

    public String getApproveRejectName() {
        return approveRejectName;
    }

    public void setApproveRejectName(String pApproveRejectName) {
        approveRejectName = pApproveRejectName;
    }

    public String getApproveRejectLogin() {
        return approveRejectLogin;
    }

    public void setApproveRejectLogin(String pApproveRejectLogin) {
        approveRejectLogin = pApproveRejectLogin;
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

    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }
    
}