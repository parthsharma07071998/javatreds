
package com.xlx.treds.monetago.bean;


public class MonetagoInvoiceInfoItemsBean {

    private Long num;
    private MonetagoInvoiceInfoItemsDeatilsBean itm_det;

    public Long getNum() {
        return num;
    }

    public void setNum(Long pNum) {
        num = pNum;
    }

    public MonetagoInvoiceInfoItemsDeatilsBean getItm_det() {
        return itm_det;
    }

    public void setItm_det(MonetagoInvoiceInfoItemsDeatilsBean pItm_det) {
        itm_det = pItm_det;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}