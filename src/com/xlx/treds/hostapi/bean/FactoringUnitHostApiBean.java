package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FactoringUnitHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FactoringUnitHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private List<FactoringUnitWatchHostApiBean> factoringUnitWatchList = new ArrayList<>();
	private List<FactoringUnitBidHostApiBean> factoringUnitBidHostApiBeans = new ArrayList<>();

	private BigDecimal FUID;

	public BigDecimal getFUID() {
		return this.FUID;
	}

	public void setFUID(BigDecimal value) {
		this.FUID = value;
	}

	private Date FUMATURITYDATE;

	public Date getFUMATURITYDATE() {
		return this.FUMATURITYDATE;
	}

	public void setFUMATURITYDATE(Date value) {
		this.FUMATURITYDATE = value;
	}

	private Date FUSTATDUEDATE;

	public Date getFUSTATDUEDATE() {
		return this.FUSTATDUEDATE;
	}

	public void setFUSTATDUEDATE(Date value) {
		this.FUSTATDUEDATE = value;
	}

	private String FUENABLEEXTENSION;

	public String getFUENABLEEXTENSION() {
		return this.FUENABLEEXTENSION;
	}

	public void setFUENABLEEXTENSION(String value) {
		this.FUENABLEEXTENSION = value;
	}

	private BigDecimal FUEXTENDEDCREDITPERIOD;

	public BigDecimal getFUEXTENDEDCREDITPERIOD() {
		return this.FUEXTENDEDCREDITPERIOD;
	}

	public void setFUEXTENDEDCREDITPERIOD(BigDecimal value) {
		this.FUEXTENDEDCREDITPERIOD = value;
	}

	private Date FUEXTENDEDDUEDATE;

	public Date getFUEXTENDEDDUEDATE() {
		return this.FUEXTENDEDDUEDATE;
	}

	public void setFUEXTENDEDDUEDATE(Date value) {
		this.FUEXTENDEDDUEDATE = value;
	}

	private String FUCURRENCY;

	public String getFUCURRENCY() {
		return this.FUCURRENCY;
	}

	public void setFUCURRENCY(String value) {
		this.FUCURRENCY = value;
	}

	private BigDecimal FUAMOUNT;

	public BigDecimal getFUAMOUNT() {
		return this.FUAMOUNT;
	}

	public void setFUAMOUNT(BigDecimal value) {
		this.FUAMOUNT = value;
	}

	private String FUPURCHASER;

	public String getFUPURCHASER() {
		return this.FUPURCHASER;
	}

	public void setFUPURCHASER(String value) {
		this.FUPURCHASER = value;
	}

	private String FUPURCHASERREF;

	public String getFUPURCHASERREF() {
		return this.FUPURCHASERREF;
	}

	public void setFUPURCHASERREF(String value) {
		this.FUPURCHASERREF = value;
	}

	private String FUSUPPLIER;

	public String getFUSUPPLIER() {
		return this.FUSUPPLIER;
	}

	public void setFUSUPPLIER(String value) {
		this.FUSUPPLIER = value;
	}

	private String FUSUPPLIERREF;

	public String getFUSUPPLIERREF() {
		return this.FUSUPPLIERREF;
	}

	public void setFUSUPPLIERREF(String value) {
		this.FUSUPPLIERREF = value;
	}

	private String FUINTRODUCINGENTITY;

	public String getFUINTRODUCINGENTITY() {
		return this.FUINTRODUCINGENTITY;
	}

	public void setFUINTRODUCINGENTITY(String value) {
		this.FUINTRODUCINGENTITY = value;
	}

	private BigDecimal FUINTRODUCINGAUID;

	public BigDecimal getFUINTRODUCINGAUID() {
		return this.FUINTRODUCINGAUID;
	}

	public void setFUINTRODUCINGAUID(BigDecimal value) {
		this.FUINTRODUCINGAUID = value;
	}

	private String FUCOUNTERENTITY;

	public String getFUCOUNTERENTITY() {
		return this.FUCOUNTERENTITY;
	}

	public void setFUCOUNTERENTITY(String value) {
		this.FUCOUNTERENTITY = value;
	}

	private BigDecimal FUCOUNTERAUID;

	public BigDecimal getFUCOUNTERAUID() {
		return this.FUCOUNTERAUID;
	}

	public void setFUCOUNTERAUID(BigDecimal value) {
		this.FUCOUNTERAUID = value;
	}

	private String FUOWNERENTITY;

	public String getFUOWNERENTITY() {
		return this.FUOWNERENTITY;
	}

	public void setFUOWNERENTITY(String value) {
		this.FUOWNERENTITY = value;
	}

	private BigDecimal FUOWNERAUID;

	public BigDecimal getFUOWNERAUID() {
		return this.FUOWNERAUID;
	}

	public void setFUOWNERAUID(BigDecimal value) {
		this.FUOWNERAUID = value;
	}

	private String FUSTATUS;

	public String getFUSTATUS() {
		return this.FUSTATUS;
	}

	public void setFUSTATUS(String value) {
		this.FUSTATUS = value;
	}

	private Date FUFACTORSTARTDATETIME;

	public Date getFUFACTORSTARTDATETIME() {
		return this.FUFACTORSTARTDATETIME;
	}

	public void setFUFACTORSTARTDATETIME(Date value) {
		this.FUFACTORSTARTDATETIME = value;
	}

	private Date FUFACTORENDDATETIME;

	public Date getFUFACTORENDDATETIME() {
		return this.FUFACTORENDDATETIME;
	}

	public void setFUFACTORENDDATETIME(Date value) {
		this.FUFACTORENDDATETIME = value;
	}

	private Date FUFACTORMAXENDDATETIME;

	public Date getFUFACTORMAXENDDATETIME() {
		return this.FUFACTORMAXENDDATETIME;
	}

	public void setFUFACTORMAXENDDATETIME(Date value) {
		this.FUFACTORMAXENDDATETIME = value;
	}

	private String FUAUTOACCEPT;

	public String getFUAUTOACCEPT() {
		return this.FUAUTOACCEPT;
	}

	public void setFUAUTOACCEPT(String value) {
		this.FUAUTOACCEPT = value;
	}

	private String FUAUTOACCEPTABLEBIDTYPES;

	public String getFUAUTOACCEPTABLEBIDTYPES() {
		return this.FUAUTOACCEPTABLEBIDTYPES;
	}

	public void setFUAUTOACCEPTABLEBIDTYPES(String value) {
		this.FUAUTOACCEPTABLEBIDTYPES = value;
	}

	private String FUAUTOCONVERT;

	public String getFUAUTOCONVERT() {
		return this.FUAUTOCONVERT;
	}

	public void setFUAUTOCONVERT(String value) {
		this.FUAUTOCONVERT = value;
	}

	private String FUPERIOD1COSTBEARER;

	public String getFUPERIOD1COSTBEARER() {
		return this.FUPERIOD1COSTBEARER;
	}

	public void setFUPERIOD1COSTBEARER(String value) {
		this.FUPERIOD1COSTBEARER = value;
	}

	private BigDecimal FUPERIOD1COSTPERCENT;

	public BigDecimal getFUPERIOD1COSTPERCENT() {
		return this.FUPERIOD1COSTPERCENT;
	}

	public void setFUPERIOD1COSTPERCENT(BigDecimal value) {
		this.FUPERIOD1COSTPERCENT = value;
	}

	private String FUPERIOD2COSTBEARER;

	public String getFUPERIOD2COSTBEARER() {
		return this.FUPERIOD2COSTBEARER;
	}

	public void setFUPERIOD2COSTBEARER(String value) {
		this.FUPERIOD2COSTBEARER = value;
	}

	private BigDecimal FUPERIOD2COSTPERCENT;

	public BigDecimal getFUPERIOD2COSTPERCENT() {
		return this.FUPERIOD2COSTPERCENT;
	}

	public void setFUPERIOD2COSTPERCENT(BigDecimal value) {
		this.FUPERIOD2COSTPERCENT = value;
	}

	private String FUPERIOD3COSTBEARER;

	public String getFUPERIOD3COSTBEARER() {
		return this.FUPERIOD3COSTBEARER;
	}

	public void setFUPERIOD3COSTBEARER(String value) {
		this.FUPERIOD3COSTBEARER = value;
	}

	private BigDecimal FUPERIOD3COSTPERCENT;

	public BigDecimal getFUPERIOD3COSTPERCENT() {
		return this.FUPERIOD3COSTPERCENT;
	}

	public void setFUPERIOD3COSTPERCENT(BigDecimal value) {
		this.FUPERIOD3COSTPERCENT = value;
	}

	private String FUSUPGSTSTATE;

	public String getFUSUPGSTSTATE() {
		return this.FUSUPGSTSTATE;
	}

	public void setFUSUPGSTSTATE(String value) {
		this.FUSUPGSTSTATE = value;
	}

	private String FUSUPGSTN;

	public String getFUSUPGSTN() {
		return this.FUSUPGSTN;
	}

	public void setFUSUPGSTN(String value) {
		this.FUSUPGSTN = value;
	}

	private String FUPURGSTSTATE;

	public String getFUPURGSTSTATE() {
		return this.FUPURGSTSTATE;
	}

	public void setFUPURGSTSTATE(String value) {
		this.FUPURGSTSTATE = value;
	}

	private String FUPURGSTN;

	public String getFUPURGSTN() {
		return this.FUPURGSTN;
	}

	public void setFUPURGSTN(String value) {
		this.FUPURGSTN = value;
	}

	private String FUSETTLELEG3FLAG;

	public String getFUSETTLELEG3FLAG() {
		return this.FUSETTLELEG3FLAG;
	}

	public void setFUSETTLELEG3FLAG(String value) {
		this.FUSETTLELEG3FLAG = value;
	}

	private BigDecimal FUBDID;

	public BigDecimal getFUBDID() {
		return this.FUBDID;
	}

	public void setFUBDID(BigDecimal value) {
		this.FUBDID = value;
	}

	private String FUACCEPTEDBIDTYPE;

	public String getFUACCEPTEDBIDTYPE() {
		return this.FUACCEPTEDBIDTYPE;
	}

	public void setFUACCEPTEDBIDTYPE(String value) {
		this.FUACCEPTEDBIDTYPE = value;
	}

	private BigDecimal FUACCEPTEDRATE;

	public BigDecimal getFUACCEPTEDRATE() {
		return this.FUACCEPTEDRATE;
	}

	public void setFUACCEPTEDRATE(BigDecimal value) {
		this.FUACCEPTEDRATE = value;
	}

	private BigDecimal FUACCEPTEDHAIRCUT;

	public BigDecimal getFUACCEPTEDHAIRCUT() {
		return this.FUACCEPTEDHAIRCUT;
	}

	public void setFUACCEPTEDHAIRCUT(BigDecimal value) {
		this.FUACCEPTEDHAIRCUT = value;
	}

	private Date FULEG1DATE;

	public Date getFULEG1DATE() {
		return this.FULEG1DATE;
	}

	public void setFULEG1DATE(Date value) {
		this.FULEG1DATE = value;
	}

	private BigDecimal FUFACTOREDAMOUNT;

	public BigDecimal getFUFACTOREDAMOUNT() {
		return this.FUFACTOREDAMOUNT;
	}

	public void setFUFACTOREDAMOUNT(BigDecimal value) {
		this.FUFACTOREDAMOUNT = value;
	}

	private BigDecimal FUPURCHASERLEG1INTEREST;

	public BigDecimal getFUPURCHASERLEG1INTEREST() {
		return this.FUPURCHASERLEG1INTEREST;
	}

	public void setFUPURCHASERLEG1INTEREST(BigDecimal value) {
		this.FUPURCHASERLEG1INTEREST = value;
	}

	private BigDecimal FUSUPPLIERLEG1INTEREST;

	public BigDecimal getFUSUPPLIERLEG1INTEREST() {
		return this.FUSUPPLIERLEG1INTEREST;
	}

	public void setFUSUPPLIERLEG1INTEREST(BigDecimal value) {
		this.FUSUPPLIERLEG1INTEREST = value;
	}

	private BigDecimal FUPURCHASERLEG2INTEREST;

	public BigDecimal getFUPURCHASERLEG2INTEREST() {
		return this.FUPURCHASERLEG2INTEREST;
	}

	public void setFUPURCHASERLEG2INTEREST(BigDecimal value) {
		this.FUPURCHASERLEG2INTEREST = value;
	}

	private BigDecimal FULEG2EXTENSIONINTEREST;

	public BigDecimal getFULEG2EXTENSIONINTEREST() {
		return this.FULEG2EXTENSIONINTEREST;
	}

	public void setFULEG2EXTENSIONINTEREST(BigDecimal value) {
		this.FULEG2EXTENSIONINTEREST = value;
	}

	private BigDecimal FUCHARGES;

	public BigDecimal getFUCHARGES() {
		return this.FUCHARGES;
	}

	public void setFUCHARGES(BigDecimal value) {
		this.FUCHARGES = value;
	}

	private String FUENTITYGSTSUMMARY;

	public String getFUENTITYGSTSUMMARY() {
		return this.FUENTITYGSTSUMMARY;
	}

	public void setFUENTITYGSTSUMMARY(String value) {
		this.FUENTITYGSTSUMMARY = value;
	}

	private String FUFINANCIER;

	public String getFUFINANCIER() {
		return this.FUFINANCIER;
	}

	public void setFUFINANCIER(String value) {
		this.FUFINANCIER = value;
	}

	private String FUACCEPTINGENTITY;

	public String getFUACCEPTINGENTITY() {
		return this.FUACCEPTINGENTITY;
	}

	public void setFUACCEPTINGENTITY(String value) {
		this.FUACCEPTINGENTITY = value;
	}

	private BigDecimal FUACCEPTINGAUID;

	public BigDecimal getFUACCEPTINGAUID() {
		return this.FUACCEPTINGAUID;
	}

	public void setFUACCEPTINGAUID(BigDecimal value) {
		this.FUACCEPTINGAUID = value;
	}

	private Date FUACCEPTDATETIME;

	public Date getFUACCEPTDATETIME() {
		return this.FUACCEPTDATETIME;
	}

	public void setFUACCEPTDATETIME(Date value) {
		this.FUACCEPTDATETIME = value;
	}

	private BigDecimal FULIMITUTILIZED;

	public BigDecimal getFULIMITUTILIZED() {
		return this.FULIMITUTILIZED;
	}

	public void setFULIMITUTILIZED(BigDecimal value) {
		this.FULIMITUTILIZED = value;
	}

	private String FULIMITIDS;

	public String getFULIMITIDS() {
		return this.FULIMITIDS;
	}

	public void setFULIMITIDS(String value) {
		this.FULIMITIDS = value;
	}

	private BigDecimal FUPURSUPLIMITUTILIZED;

	public BigDecimal getFUPURSUPLIMITUTILIZED() {
		return this.FUPURSUPLIMITUTILIZED;
	}

	public void setFUPURSUPLIMITUTILIZED(BigDecimal value) {
		this.FUPURSUPLIMITUTILIZED = value;
	}

	private String FUSALESCATEGORY;

	public String getFUSALESCATEGORY() {
		return this.FUSALESCATEGORY;
	}

	public void setFUSALESCATEGORY(String value) {
		this.FUSALESCATEGORY = value;
	}

	private BigDecimal FUPURCHASERSETTLELOC;

	public BigDecimal getFUPURCHASERSETTLELOC() {
		return this.FUPURCHASERSETTLELOC;
	}

	public void setFUPURCHASERSETTLELOC(BigDecimal value) {
		this.FUPURCHASERSETTLELOC = value;
	}

	private BigDecimal FUSUPPLIERSETTLELOC;

	public BigDecimal getFUSUPPLIERSETTLELOC() {
		return this.FUSUPPLIERSETTLELOC;
	}

	public void setFUSUPPLIERSETTLELOC(BigDecimal value) {
		this.FUSUPPLIERSETTLELOC = value;
	}

	private BigDecimal FUFINANCIERSETTLELOC;

	public BigDecimal getFUFINANCIERSETTLELOC() {
		return this.FUFINANCIERSETTLELOC;
	}

	public void setFUFINANCIERSETTLELOC(BigDecimal value) {
		this.FUFINANCIERSETTLELOC = value;
	}

	private Date FUSTATUSUPDATETIME;

	public Date getFUSTATUSUPDATETIME() {
		return this.FUSTATUSUPDATETIME;
	}

	public void setFUSTATUSUPDATETIME(Date value) {
		this.FUSTATUSUPDATETIME = value;
	}

	private BigDecimal FUCOSTBEARERBILLID;

	public BigDecimal getFUCOSTBEARERBILLID() {
		return this.FUCOSTBEARERBILLID;
	}

	public void setFUCOSTBEARERBILLID(BigDecimal value) {
		this.FUCOSTBEARERBILLID = value;
	}

	private BigDecimal FUFINANCIERBILLID;

	public BigDecimal getFUFINANCIERBILLID() {
		return this.FUFINANCIERBILLID;
	}

	public void setFUFINANCIERBILLID(BigDecimal value) {
		this.FUFINANCIERBILLID = value;
	}

	private Date FURECORDCREATETIME;

	public Date getFURECORDCREATETIME() {
		return this.FURECORDCREATETIME;
	}

	public void setFURECORDCREATETIME(Date value) {
		this.FURECORDCREATETIME = value;
	}

	private BigDecimal FURECORDVERSION;

	public BigDecimal getFURECORDVERSION() {
		return this.FURECORDVERSION;
	}

	public void setFURECORDVERSION(BigDecimal value) {
		this.FURECORDVERSION = value;
	}

	private BigDecimal FUFINANCIERBILLLOC;

	public BigDecimal getFUFINANCIERBILLLOC() {
		return this.FUFINANCIERBILLLOC;
	}

	public void setFUFINANCIERBILLLOC(BigDecimal value) {
		this.FUFINANCIERBILLLOC = value;
	}

	private BigDecimal FUPURCHASERBILLLOC;

	public BigDecimal getFUPURCHASERBILLLOC() {
		return this.FUPURCHASERBILLLOC;
	}

	public void setFUPURCHASERBILLLOC(BigDecimal value) {
		this.FUPURCHASERBILLLOC = value;
	}

	private BigDecimal FUSUPPLIERBILLLOC;

	public BigDecimal getFUSUPPLIERBILLLOC() {
		return this.FUSUPPLIERBILLLOC;
	}

	public void setFUSUPPLIERBILLLOC(BigDecimal value) {
		this.FUSUPPLIERBILLLOC = value;
	}

	private BigDecimal FUEXTBILLID1;

	public BigDecimal getFUEXTBILLID1() {
		return this.FUEXTBILLID1;
	}

	public void setFUEXTBILLID1(BigDecimal value) {
		this.FUEXTBILLID1 = value;
	}

	private BigDecimal FUEXTBILLID2;

	public BigDecimal getFUEXTBILLID2() {
		return this.FUEXTBILLID2;
	}

	public void setFUEXTBILLID2(BigDecimal value) {
		this.FUEXTBILLID2 = value;
	}

	private String FUPERIOD1CHARGEBEARER;

	public String getFUPERIOD1CHARGEBEARER() {
		return this.FUPERIOD1CHARGEBEARER;
	}

	public void setFUPERIOD1CHARGEBEARER(String value) {
		this.FUPERIOD1CHARGEBEARER = value;
	}

	private BigDecimal FUPERIOD1CHARGEPERCENT;

	public BigDecimal getFUPERIOD1CHARGEPERCENT() {
		return this.FUPERIOD1CHARGEPERCENT;
	}

	public void setFUPERIOD1CHARGEPERCENT(BigDecimal value) {
		this.FUPERIOD1CHARGEPERCENT = value;
	}

	private String FUPERIOD2CHARGEBEARER;

	public String getFUPERIOD2CHARGEBEARER() {
		return this.FUPERIOD2CHARGEBEARER;
	}

	public void setFUPERIOD2CHARGEBEARER(String value) {
		this.FUPERIOD2CHARGEBEARER = value;
	}

	private BigDecimal FUPERIOD2CHARGEPERCENT;

	public BigDecimal getFUPERIOD2CHARGEPERCENT() {
		return this.FUPERIOD2CHARGEPERCENT;
	}

	public void setFUPERIOD2CHARGEPERCENT(BigDecimal value) {
		this.FUPERIOD2CHARGEPERCENT = value;
	}

	private String FUPERIOD3CHARGEBEARER;

	public String getFUPERIOD3CHARGEBEARER() {
		return this.FUPERIOD3CHARGEBEARER;
	}

	public void setFUPERIOD3CHARGEBEARER(String value) {
		this.FUPERIOD3CHARGEBEARER = value;
	}

	private BigDecimal FUPERIOD3CHARGEPERCENT;

	public BigDecimal getFUPERIOD3CHARGEPERCENT() {
		return this.FUPERIOD3CHARGEPERCENT;
	}

	public void setFUPERIOD3CHARGEPERCENT(BigDecimal value) {
		this.FUPERIOD3CHARGEPERCENT = value;
	}

	public List<FactoringUnitWatchHostApiBean> getFactoringUnitWatchList() {
		return factoringUnitWatchList;
	}

	public void setFactoringUnitWatchList(List<FactoringUnitWatchHostApiBean> factoringUnitWatchList) {
		this.factoringUnitWatchList = factoringUnitWatchList;
	}

	public List<FactoringUnitBidHostApiBean> getFactoringUnitBidHostApiBeans() {
		return factoringUnitBidHostApiBeans;
	}

	public void setFactoringUnitBidHostApiBeans(List<FactoringUnitBidHostApiBean> factoringUnitBidHostApiBeans) {
		this.factoringUnitBidHostApiBeans = factoringUnitBidHostApiBeans;
	}

}
