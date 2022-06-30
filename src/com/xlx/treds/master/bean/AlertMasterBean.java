package com.xlx.treds.master.bean;

import com.xlx.treds.AppConstants.MessageType;

public class AlertMasterBean {

    private String notificationType;
    private String category;
    private String description;
    private MessageType smsMessageType;
    private MessageType emailMessageType;
    private Long sequence;
    private Long recordVersion;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String pNotificationType) {
        notificationType = pNotificationType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String pCategory) {
        category = pCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public MessageType getSmsMessageType() {
        return smsMessageType;
    }

    public void setSmsMessageType(MessageType pSmsMessageType) {
        smsMessageType = pSmsMessageType;
    }

    public MessageType getEmailMessageType() {
        return emailMessageType;
    }

    public void setEmailMessageType(MessageType pEmailMessageType) {
        emailMessageType = pEmailMessageType;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long pSequence) {
        sequence = pSequence;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}