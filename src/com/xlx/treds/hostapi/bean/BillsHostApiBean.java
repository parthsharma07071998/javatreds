package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class BillsHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<BillFactoringUnitHostApiBean> billFactoringHostApiBeanList;

	private List<BillsRegistrationChargesHostApiBean> billsRegistrationChargesHostApiBeans;

	private BigDecimal BILID;

	public BigDecimal getBILID() {
		return this.BILID;
	}

	public void setBILID(BigDecimal value) {
		this.BILID = value;
	}

	private String BILBILLNUMBER;

	public String getBILBILLNUMBER() {
		return this.BILBILLNUMBER;
	}

	public void setBILBILLNUMBER(String value) {
		this.BILBILLNUMBER = value;
	}

	private Date BILBILLYEARMONTH;

	public Date getBILBILLYEARMONTH() {
		return this.BILBILLYEARMONTH;
	}

	public void setBILBILLYEARMONTH(Date value) {
		this.BILBILLYEARMONTH = value;
	}

	private Date BILBILLDATE;

	public Date getBILBILLDATE() {
		return this.BILBILLDATE;
	}

	public void setBILBILLDATE(Date value) {
		this.BILBILLDATE = value;
	}

	private String BILENTITY;

	public String getBILENTITY() {
		return this.BILENTITY;
	}

	public void setBILENTITY(String value) {
		this.BILENTITY = value;
	}

	private String BILENTNAME;

	public String getBILENTNAME() {
		return this.BILENTNAME;
	}

	public void setBILENTNAME(String value) {
		this.BILENTNAME = value;
	}

	private String BILENTGSTN;

	public String getBILENTGSTN() {
		return this.BILENTGSTN;
	}

	public void setBILENTGSTN(String value) {
		this.BILENTGSTN = value;
	}

	private String BILENTPAN;

	public String getBILENTPAN() {
		return this.BILENTPAN;
	}

	public void setBILENTPAN(String value) {
		this.BILENTPAN = value;
	}

	private String BILENTLINE1;

	public String getBILENTLINE1() {
		return this.BILENTLINE1;
	}

	public void setBILENTLINE1(String value) {
		this.BILENTLINE1 = value;
	}

	private String BILENTLINE2;

	public String getBILENTLINE2() {
		return this.BILENTLINE2;
	}

	public void setBILENTLINE2(String value) {
		this.BILENTLINE2 = value;
	}

	private String BILENTLINE3;

	public String getBILENTLINE3() {
		return this.BILENTLINE3;
	}

	public void setBILENTLINE3(String value) {
		this.BILENTLINE3 = value;
	}

	private String BILENTCOUNTRY;

	public String getBILENTCOUNTRY() {
		return this.BILENTCOUNTRY;
	}

	public void setBILENTCOUNTRY(String value) {
		this.BILENTCOUNTRY = value;
	}

	private String BILENTSTATE;

	public String getBILENTSTATE() {
		return this.BILENTSTATE;
	}

	public void setBILENTSTATE(String value) {
		this.BILENTSTATE = value;
	}

	private String BILENTDISTRICT;

	public String getBILENTDISTRICT() {
		return this.BILENTDISTRICT;
	}

	public void setBILENTDISTRICT(String value) {
		this.BILENTDISTRICT = value;
	}

	private String BILENTCITY;

	public String getBILENTCITY() {
		return this.BILENTCITY;
	}

	public void setBILENTCITY(String value) {
		this.BILENTCITY = value;
	}

	private String BILENTZIPCODE;

	public String getBILENTZIPCODE() {
		return this.BILENTZIPCODE;
	}

	public void setBILENTZIPCODE(String value) {
		this.BILENTZIPCODE = value;
	}

	private String BILENTSALUTATION;

	public String getBILENTSALUTATION() {
		return this.BILENTSALUTATION;
	}

	public void setBILENTSALUTATION(String value) {
		this.BILENTSALUTATION = value;
	}

	private String BILENTFIRSTNAME;

	public String getBILENTFIRSTNAME() {
		return this.BILENTFIRSTNAME;
	}

	public void setBILENTFIRSTNAME(String value) {
		this.BILENTFIRSTNAME = value;
	}

	private String BILENTMIDDLENAME;

	public String getBILENTMIDDLENAME() {
		return this.BILENTMIDDLENAME;
	}

	public void setBILENTMIDDLENAME(String value) {
		this.BILENTMIDDLENAME = value;
	}

	private String BILENTLASTNAME;

	public String getBILENTLASTNAME() {
		return this.BILENTLASTNAME;
	}

	public void setBILENTLASTNAME(String value) {
		this.BILENTLASTNAME = value;
	}

	private String BILENTEMAIL;

	public String getBILENTEMAIL() {
		return this.BILENTEMAIL;
	}

	public void setBILENTEMAIL(String value) {
		this.BILENTEMAIL = value;
	}

	private String BILENTTELEPHONE;

	public String getBILENTTELEPHONE() {
		return this.BILENTTELEPHONE;
	}

	public void setBILENTTELEPHONE(String value) {
		this.BILENTTELEPHONE = value;
	}

	private String BILENTMOBILE;

	public String getBILENTMOBILE() {
		return this.BILENTMOBILE;
	}

	public void setBILENTMOBILE(String value) {
		this.BILENTMOBILE = value;
	}

	private String BILENTFAX;

	public String getBILENTFAX() {
		return this.BILENTFAX;
	}

	public void setBILENTFAX(String value) {
		this.BILENTFAX = value;
	}

	private String BILTREDSNAME;

	public String getBILTREDSNAME() {
		return this.BILTREDSNAME;
	}

	public void setBILTREDSNAME(String value) {
		this.BILTREDSNAME = value;
	}

	private String BILTREDSGSTN;

	public String getBILTREDSGSTN() {
		return this.BILTREDSGSTN;
	}

	public void setBILTREDSGSTN(String value) {
		this.BILTREDSGSTN = value;
	}

	private String BILTREDSLINE1;

	public String getBILTREDSLINE1() {
		return this.BILTREDSLINE1;
	}

	public void setBILTREDSLINE1(String value) {
		this.BILTREDSLINE1 = value;
	}

	private String BILTREDSLINE2;

	public String getBILTREDSLINE2() {
		return this.BILTREDSLINE2;
	}

	public void setBILTREDSLINE2(String value) {
		this.BILTREDSLINE2 = value;
	}

	private String BILTREDSLINE3;

	public String getBILTREDSLINE3() {
		return this.BILTREDSLINE3;
	}

	public void setBILTREDSLINE3(String value) {
		this.BILTREDSLINE3 = value;
	}

	private String BILTREDSCOUNTRY;

	public String getBILTREDSCOUNTRY() {
		return this.BILTREDSCOUNTRY;
	}

	public void setBILTREDSCOUNTRY(String value) {
		this.BILTREDSCOUNTRY = value;
	}

	private String BILTREDSSTATE;

	public String getBILTREDSSTATE() {
		return this.BILTREDSSTATE;
	}

	public void setBILTREDSSTATE(String value) {
		this.BILTREDSSTATE = value;
	}

	private String BILTREDSDISTRICT;

	public String getBILTREDSDISTRICT() {
		return this.BILTREDSDISTRICT;
	}

	public void setBILTREDSDISTRICT(String value) {
		this.BILTREDSDISTRICT = value;
	}

	private String BILTREDSCITY;

	public String getBILTREDSCITY() {
		return this.BILTREDSCITY;
	}

	public void setBILTREDSCITY(String value) {
		this.BILTREDSCITY = value;
	}

	private String BILTREDSZIPCODE;

	public String getBILTREDSZIPCODE() {
		return this.BILTREDSZIPCODE;
	}

	public void setBILTREDSZIPCODE(String value) {
		this.BILTREDSZIPCODE = value;
	}

	private String BILTREDSEMAIL;

	public String getBILTREDSEMAIL() {
		return this.BILTREDSEMAIL;
	}

	public void setBILTREDSEMAIL(String value) {
		this.BILTREDSEMAIL = value;
	}

	private String BILTREDSTELEPHONE;

	public String getBILTREDSTELEPHONE() {
		return this.BILTREDSTELEPHONE;
	}

	public void setBILTREDSTELEPHONE(String value) {
		this.BILTREDSTELEPHONE = value;
	}

	private String BILTREDSMOBILE;

	public String getBILTREDSMOBILE() {
		return this.BILTREDSMOBILE;
	}

	public void setBILTREDSMOBILE(String value) {
		this.BILTREDSMOBILE = value;
	}

	private String BILTREDSFAX;

	public String getBILTREDSFAX() {
		return this.BILTREDSFAX;
	}

	public void setBILTREDSFAX(String value) {
		this.BILTREDSFAX = value;
	}

	private String BILTREDSPAN;

	public String getBILTREDSPAN() {
		return this.BILTREDSPAN;
	}

	public void setBILTREDSPAN(String value) {
		this.BILTREDSPAN = value;
	}

	private String BILTREDSCIN;

	public String getBILTREDSCIN() {
		return this.BILTREDSCIN;
	}

	public void setBILTREDSCIN(String value) {
		this.BILTREDSCIN = value;
	}

	private String BILTREDSNATUREOFTRANS;

	public String getBILTREDSNATUREOFTRANS() {
		return this.BILTREDSNATUREOFTRANS;
	}

	public void setBILTREDSNATUREOFTRANS(String value) {
		this.BILTREDSNATUREOFTRANS = value;
	}

	private String BILTREDSSACCODE;

	public String getBILTREDSSACCODE() {
		return this.BILTREDSSACCODE;
	}

	public void setBILTREDSSACCODE(String value) {
		this.BILTREDSSACCODE = value;
	}

	private String BILTREDSSACDESC;

	public String getBILTREDSSACDESC() {
		return this.BILTREDSSACDESC;
	}

	public void setBILTREDSSACDESC(String value) {
		this.BILTREDSSACDESC = value;
	}

	private BigDecimal BILCHARGEAMOUNT;

	public BigDecimal getBILCHARGEAMOUNT() {
		return this.BILCHARGEAMOUNT;
	}

	public void setBILCHARGEAMOUNT(BigDecimal value) {
		this.BILCHARGEAMOUNT = value;
	}

	private BigDecimal BILFUAMOUNT;

	public BigDecimal getBILFUAMOUNT() {
		return this.BILFUAMOUNT;
	}

	public void setBILFUAMOUNT(BigDecimal value) {
		this.BILFUAMOUNT = value;
	}

	private BigDecimal BILCGST;

	public BigDecimal getBILCGST() {
		return this.BILCGST;
	}

	public void setBILCGST(BigDecimal value) {
		this.BILCGST = value;
	}

	private BigDecimal BILSGST;

	public BigDecimal getBILSGST() {
		return this.BILSGST;
	}

	public void setBILSGST(BigDecimal value) {
		this.BILSGST = value;
	}

	private BigDecimal BILIGST;

	public BigDecimal getBILIGST() {
		return this.BILIGST;
	}

	public void setBILIGST(BigDecimal value) {
		this.BILIGST = value;
	}

	private BigDecimal BILCGSTSURCHARGE;

	public BigDecimal getBILCGSTSURCHARGE() {
		return this.BILCGSTSURCHARGE;
	}

	public void setBILCGSTSURCHARGE(BigDecimal value) {
		this.BILCGSTSURCHARGE = value;
	}

	private BigDecimal BILSGSTSURCHARGE;

	public BigDecimal getBILSGSTSURCHARGE() {
		return this.BILSGSTSURCHARGE;
	}

	public void setBILSGSTSURCHARGE(BigDecimal value) {
		this.BILSGSTSURCHARGE = value;
	}

	private BigDecimal BILIGSTSURCHARGE;

	public BigDecimal getBILIGSTSURCHARGE() {
		return this.BILIGSTSURCHARGE;
	}

	public void setBILIGSTSURCHARGE(BigDecimal value) {
		this.BILIGSTSURCHARGE = value;
	}

	private BigDecimal BILCGSTVALUE;

	public BigDecimal getBILCGSTVALUE() {
		return this.BILCGSTVALUE;
	}

	public void setBILCGSTVALUE(BigDecimal value) {
		this.BILCGSTVALUE = value;
	}

	private BigDecimal BILSGSTVALUE;

	public BigDecimal getBILSGSTVALUE() {
		return this.BILSGSTVALUE;
	}

	public void setBILSGSTVALUE(BigDecimal value) {
		this.BILSGSTVALUE = value;
	}

	private BigDecimal BILIGSTVALUE;

	public BigDecimal getBILIGSTVALUE() {
		return this.BILIGSTVALUE;
	}

	public void setBILIGSTVALUE(BigDecimal value) {
		this.BILIGSTVALUE = value;
	}

	private BigDecimal BILRECORDCREATOR;

	public BigDecimal getBILRECORDCREATOR() {
		return this.BILRECORDCREATOR;
	}

	public void setBILRECORDCREATOR(BigDecimal value) {
		this.BILRECORDCREATOR = value;
	}

	private Date BILRECORDCREATETIME;

	public Date getBILRECORDCREATETIME() {
		return this.BILRECORDCREATETIME;
	}

	public void setBILRECORDCREATETIME(Date value) {
		this.BILRECORDCREATETIME = value;
	}

	private BigDecimal BILRECORDVERSION;

	public BigDecimal getBILRECORDVERSION() {
		return this.BILRECORDVERSION;
	}

	public void setBILRECORDVERSION(BigDecimal value) {
		this.BILRECORDVERSION = value;
	}

	private String BILBILLINGTYPE;

	public String getBILBILLINGTYPE() {
		return this.BILBILLINGTYPE;
	}

	public void setBILBILLINGTYPE(String value) {
		this.BILBILLINGTYPE = value;
	}

	private String BILBILLEDFORENTITY;

	public String getBILBILLEDFORENTITY() {
		return this.BILBILLEDFORENTITY;
	}

	public void setBILBILLEDFORENTITY(String value) {
		this.BILBILLEDFORENTITY = value;
	}

	public List<BillFactoringUnitHostApiBean> getBillFactoringHostApiBeanList() {
		return billFactoringHostApiBeanList;
	}

	public void setBillFactoringHostApiBeanList(List<BillFactoringUnitHostApiBean> billFactoringHostApiBeanList) {
		this.billFactoringHostApiBeanList = billFactoringHostApiBeanList;
	}

	public List<BillsRegistrationChargesHostApiBean> getBillsRegistrationChargesHostApiBeans() {
		return billsRegistrationChargesHostApiBeans;
	}

	public void setBillsRegistrationChargesHostApiBeans(
			List<BillsRegistrationChargesHostApiBean> billsRegistrationChargesHostApiBeans) {
		this.billsRegistrationChargesHostApiBeans = billsRegistrationChargesHostApiBeans;
	}

}
