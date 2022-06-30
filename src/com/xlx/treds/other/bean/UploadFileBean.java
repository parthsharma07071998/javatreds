package com.xlx.treds.other.bean;

import java.sql.Timestamp;

public class UploadFileBean implements IUploadFileBean {

	public static final String FILETYPE_REGISTRATIONS = "REG";
	
    private Long id;
    private String type;
    private String key;
    private String fileName;
    private String storageFileName;
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

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String pKey) {
        key = pKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pFileName) {
        fileName = pFileName;
    }

    public String getStorageFileName() {
        return storageFileName;
    }

    public void setStorageFileName(String pStorageFileName) {
        storageFileName = pStorageFileName;
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

}