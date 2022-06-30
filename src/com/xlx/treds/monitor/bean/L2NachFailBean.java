package com.xlx.treds.monitor.bean;

import java.math.BigDecimal;
import java.sql.Date;

public class L2NachFailBean {

    private String entity;
    private String companyName;
    private Date obligationDate;
    private BigDecimal totalOblig;
    private BigDecimal npciSuccess;
    private BigDecimal npciFail;
    private BigDecimal failPercent;
    private BigDecimal directSuccess;
    private BigDecimal directPending;
    private BigDecimal directFail;
    private Long fuCount;
    private Long instCount;
    private Date toDate;
    private Date fromDate;
    private BigDecimal filterPercent;
    
    public String getEntity() {
        return entity;
    }

    public void setEntity(String pEntity) {
        entity = pEntity;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String pCompanyName) {
        companyName = pCompanyName;
    }

    public Date getObligationDate() {
        return obligationDate;
    }

    public void setObligationDate(Date pObligationDate) {
        obligationDate = pObligationDate;
    }

    public BigDecimal getTotalOblig() {
        return totalOblig;
    }

    public void setTotalOblig(BigDecimal pTotalOblig) {
        totalOblig = pTotalOblig;
    }

    public BigDecimal getNpciSuccess() {
        return npciSuccess;
    }

    public void setNpciSuccess(BigDecimal pNpciSuccess) {
        npciSuccess = pNpciSuccess;
    }

    public BigDecimal getNpciFail() {
        return npciFail;
    }

    public void setNpciFail(BigDecimal pNpciFail) {
        npciFail = pNpciFail;
    }

    public BigDecimal getFailPercent() {
        return failPercent;
    }

    public void setFailPercent(BigDecimal pPercent) {
        failPercent = pPercent;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

    public BigDecimal getFilterPercent() {
        return filterPercent;
    }

    public void setFilterPercent(BigDecimal pFilterPercent) {
        filterPercent = pFilterPercent;
    }

	public Long getInstCount() {
		return instCount;
	}

	public void setInstCount(Long instCount) {
		this.instCount = instCount;
	}

	public Long getFuCount() {
		return fuCount;
	}

	public void setFuCount(Long fuCount) {
		this.fuCount = fuCount;
	}

	public BigDecimal getDirectSuccess() {
		return directSuccess;
	}

	public void setDirectSuccess(BigDecimal directSuccess) {
		this.directSuccess = directSuccess;
	}

	public BigDecimal getDirectPending() {
		return directPending;
	}

	public void setDirectPending(BigDecimal directPending) {
		this.directPending = directPending;
	}

	public BigDecimal getDirectFail() {
		return directFail;
	}

	public void setDirectFail(BigDecimal directFail) {
		this.directFail = directFail;
	}

}