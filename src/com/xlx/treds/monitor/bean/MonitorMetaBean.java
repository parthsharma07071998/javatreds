package com.xlx.treds.monitor.bean;

import java.sql.Date;

public class MonitorMetaBean {

    private String code;
    private String name;
    private String description;
    private Long frequency;
    private Date startDateTime;
    private String dataHandler;
    private String templateName;
    private String handelbarTemplate;
    private String group;
    private String secKey;

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long pFrequency) {
        frequency = pFrequency;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date pStartDateTime) {
        startDateTime = pStartDateTime;
    }

    public String getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(String pDataHandler) {
        dataHandler = pDataHandler;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String pTemplateName) {
        templateName = pTemplateName;
    }

    public String getHandelbarTemplate() {
        return handelbarTemplate;
    }

    public void setHandelbarTemplate(String pHandelbarTemplate) {
        handelbarTemplate = pHandelbarTemplate;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String pGroup) {
        group = pGroup;
    }

    public String getSecKey() {
        return secKey;
    }
    
    public void setSecKey(String pSecKey) {
        secKey = pSecKey;
    }

}