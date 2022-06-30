package com.xlx.treds.other.bean;

import java.sql.Timestamp;

public interface IUploadFileBean {

    public Long getId();
    public void setId(Long pId) ;
    public String getType();
    public void setType(String pType);
    public String getKey();
    public void setKey(String pKey);
    public String getFileName();
    public void setFileName(String pFileName);
    public String getStorageFileName();
    public void setStorageFileName(String pStorageFileName);
    public Long getRecordCreator();
    public void setRecordCreator(Long pRecordCreator);
    public Timestamp getRecordCreateTime();
    public void setRecordCreateTime(Timestamp pRecordCreateTime);
    public Long getRecordUpdator();
    public void setRecordUpdator(Long pRecordUpdator);
    public Timestamp getRecordUpdateTime();
    public void setRecordUpdateTime(Timestamp pRecordUpdateTime);
    public Long getRecordVersion();
    public void setRecordVersion(Long pRecordVersion);
}