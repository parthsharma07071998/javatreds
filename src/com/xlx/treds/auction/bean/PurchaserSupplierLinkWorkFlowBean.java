package com.xlx.treds.auction.bean;

import java.sql.Timestamp;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.user.bean.AppUserBean;

public class PurchaserSupplierLinkWorkFlowBean {

    private Long id;
    private String supplier;
    private String supName;
    private String purchaser;
    private String purName;
    private ApprovalStatus status;
    private String statusRemarks;
    private String entity;
    private Long auId;
    private String loginId;
    private String name;
    private Timestamp statusUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String pSupName) {
        supName = pSupName;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getPurName() {
        return purName;
    }

    public void setPurName(String pPurName) {
        purName = pPurName;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus pStatus) {
        status = pStatus;
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String pStatusRemarks) {
        statusRemarks = pStatusRemarks;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String pEntity) {
        entity = pEntity;
    }

    public Long getAuId() {
        return auId;
    }

    public void setAuId(Long pAuId) {
        auId = pAuId;
    	loginId = null;
    	name = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
            if (lAppUserBean != null) {
                loginId = lAppUserBean.getLoginId();
                name = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String pLoginId) {
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
    }

    public Timestamp getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Timestamp pStatusUpdateTime) {
        statusUpdateTime = pStatusUpdateTime;
    }

}