package com.xlx.treds.instrument.bean;

import java.sql.Timestamp;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.user.bean.AppUserBean;

public class InstrumentWorkFlowBean {

    private Long id;
    private Long inId;
    private Status status;
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

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
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