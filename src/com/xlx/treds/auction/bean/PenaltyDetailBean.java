package com.xlx.treds.auction.bean;

import java.math.BigDecimal;


public class PenaltyDetailBean {

    private Long uptoDays;
    private BigDecimal rate;

    public Long getUptoDays() {
        return uptoDays;
    }

    public void setUptoDays(Long pUptoDays) {
        uptoDays = pUptoDays;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal pRate) {
        rate = pRate;
    }

}