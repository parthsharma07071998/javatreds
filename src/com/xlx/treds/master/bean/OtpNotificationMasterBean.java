package com.xlx.treds.master.bean;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.treds.AppConstants.MessageType;

public class OtpNotificationMasterBean {

    private String notificationType;
    private String category;
    private String description;
    private YesNo supplier;
    private YesNo purchaser;
    private YesNo financier;
    private MessageType smsMessageType;
    private MessageType emailMessageType;
    private Long sequence;
    private String messageInfo;
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

    public YesNo getSupplier() {
        return supplier;
    }

    public void setSupplier(YesNo pSupplier) {
        supplier = pSupplier;
    }

    public YesNo getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(YesNo pPurchaser) {
        purchaser = pPurchaser;
    }

    public YesNo getFinancier() {
        return financier;
    }

    public void setFinancier(YesNo pFinancier) {
        financier = pFinancier;
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

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String pMessageInfo) {
        messageInfo = pMessageInfo;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}