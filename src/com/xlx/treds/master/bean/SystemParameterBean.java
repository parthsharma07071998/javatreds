package com.xlx.treds.master.bean;

import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.memdb.IMemoryTableRow;
import com.xlx.common.user.bean.UserFormatsBean;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.bean.BeanMeta;

public class SystemParameterBean implements IMemoryTableRow {

    private Long id;
    private Date date;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
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

    public static final String ENTITY_NAME = "SYSTEMPARAMETER";
    public static final String f_Id = "SPId";
    public static final int idx_Id = 0;
    private SystemParameterBean backupBean;
    
    public String getEntityName() {
        return ENTITY_NAME;
    }

    public void fill(String pMessage, UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SystemParameterBean.class);
        lBeanMeta.validateAndParse(this, pMessage, null);
    }

    public String getAsString(UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SystemParameterBean.class);
        return lBeanMeta.formatAsJson(this);
    }

    public Object getColumnValue(String pColumnName) {
        if (f_Id.equals(pColumnName))
            return id;
        else 
            return null;
    }

    public Object getColumnValue(int pColumnIndex) {
        if (pColumnIndex == idx_Id)
            return id;
        else
            return null;
    }

    public void backup() {
        if (backupBean == null)
            backupBean = new SystemParameterBean();
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SystemParameterBean.class);
        lBeanMeta.copyBean(this, backupBean);
    }

    public void rollback() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SystemParameterBean.class);
            lBeanMeta.copyBean(backupBean, this);
        }
    }

    public void commit() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SystemParameterBean.class);
            lBeanMeta.clearBean(backupBean);
        }
    }
}