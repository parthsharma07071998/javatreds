
package com.xlx.treds.monetago.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class MonetagoInvoiceInfoBean {

    private Date idt;
    private Long pos;
    private BigDecimal val;
    private String etin;
    private String inum;
    private List<MonetagoInvoiceInfoItemsBean> itms;
    private String cflag;
    private String rchrg;
    private String updby;
    private String chksum;
    private String inv_typ;

    public Date getIdt() {
        return idt;
    }

    public void setIdt(Date pIdt) {
        idt = pIdt;
    }

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pPos) {
        pos = pPos;
    }

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal pVal) {
        val = pVal;
    }

    public String getEtin() {
        return etin;
    }

    public void setEtin(String pEtin) {
        etin = pEtin;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String pInum) {
        inum = pInum;
    }

    public List<MonetagoInvoiceInfoItemsBean> getItms() {
        return itms;
    }

    public void setItms(List<MonetagoInvoiceInfoItemsBean> pItms) {
        itms = pItms;
    }

    public String getCflag() {
        return cflag;
    }

    public void setCflag(String pCflag) {
        cflag = pCflag;
    }

    public String getRchrg() {
        return rchrg;
    }

    public void setRchrg(String pRchrg) {
        rchrg = pRchrg;
    }

    public String getUpdby() {
        return updby;
    }

    public void setUpdby(String pUpdby) {
        updby = pUpdby;
    }

    public String getChksum() {
        return chksum;
    }

    public void setChksum(String pChksum) {
        chksum = pChksum;
    }

    public String getInv_typ() {
        return inv_typ;
    }

    public void setInv_typ(String pInv_typ) {
        inv_typ = pInv_typ;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}