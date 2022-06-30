package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstrumentHostApiBean implements Serializable {

	public InstrumentHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal INID;
	private String INTYPE;
	private String INSUPPLIER;
	private String INSUPPLIERREF;
	private BigDecimal INSUPCLID;
	private String INSUPGSTSTATE;
	private String INSUPGSTN;
	private BigDecimal INSUPSETTLECLID;
	private String INSUPMSMESTATUS;
	private List<InstrumentWorkFlowHostApiBean> instrumentWorkFlowList;
	private Map<String, Object> customFieldMap = new HashMap<String, Object>();

	public BigDecimal getINID() {
		return this.INID;
	}

	public void setINID(BigDecimal value) {
		this.INID = value;
	}

	public String getINTYPE() {
		return this.INTYPE;
	}

	public void setINTYPE(String value) {
		this.INTYPE = value;
	}

	public String getINSUPPLIER() {
		return this.INSUPPLIER;
	}

	public void setINSUPPLIER(String value) {
		this.INSUPPLIER = value;
	}

	public String getINSUPPLIERREF() {
		return this.INSUPPLIERREF;
	}

	public void setINSUPPLIERREF(String value) {
		this.INSUPPLIERREF = value;
	}

	public BigDecimal getINSUPCLID() {
		return this.INSUPCLID;
	}

	public void setINSUPCLID(BigDecimal value) {
		this.INSUPCLID = value;
	}

	public String getINSUPGSTSTATE() {
		return this.INSUPGSTSTATE;
	}

	public void setINSUPGSTSTATE(String value) {
		this.INSUPGSTSTATE = value;
	}

	public String getINSUPGSTN() {
		return this.INSUPGSTN;
	}

	public void setINSUPGSTN(String value) {
		this.INSUPGSTN = value;
	}

	public BigDecimal getINSUPSETTLECLID() {
		return this.INSUPSETTLECLID;
	}

	public void setINSUPSETTLECLID(BigDecimal value) {
		this.INSUPSETTLECLID = value;
	}

	public String getINSUPMSMESTATUS() {
		return this.INSUPMSMESTATUS;
	}

	public void setINSUPMSMESTATUS(String value) {
		this.INSUPMSMESTATUS = value;
	}

	private String INPURCHASER;

	public String getINPURCHASER() {
		return this.INPURCHASER;
	}

	public void setINPURCHASER(String value) {
		this.INPURCHASER = value;
	}

	private String INPURCHASERREF;

	public String getINPURCHASERREF() {
		return this.INPURCHASERREF;
	}

	public void setINPURCHASERREF(String value) {
		this.INPURCHASERREF = value;
	}

	private BigDecimal INPURCLID;

	public BigDecimal getINPURCLID() {
		return this.INPURCLID;
	}

	public void setINPURCLID(BigDecimal value) {
		this.INPURCLID = value;
	}

	private String INPURGSTSTATE;

	public String getINPURGSTSTATE() {
		return this.INPURGSTSTATE;
	}

	public void setINPURGSTSTATE(String value) {
		this.INPURGSTSTATE = value;
	}

	private String INPURGSTN;

	public String getINPURGSTN() {
		return this.INPURGSTN;
	}

	public void setINPURGSTN(String value) {
		this.INPURGSTN = value;
	}

	private BigDecimal INPURSETTLECLID;

	public BigDecimal getINPURSETTLECLID() {
		return this.INPURSETTLECLID;
	}

	public void setINPURSETTLECLID(BigDecimal value) {
		this.INPURSETTLECLID = value;
	}

	private Date INPODATE;

	public Date getINPODATE() {
		return this.INPODATE;
	}

	public void setINPODATE(Date value) {
		this.INPODATE = value;
	}

	private String INPONUMBER;

	public String getINPONUMBER() {
		return this.INPONUMBER;
	}

	public void setINPONUMBER(String value) {
		this.INPONUMBER = value;
	}

	private String INCOUNTERREFNUM;

	public String getINCOUNTERREFNUM() {
		return this.INCOUNTERREFNUM;
	}

	public void setINCOUNTERREFNUM(String value) {
		this.INCOUNTERREFNUM = value;
	}

	private Date INGOODSACCEPTDATE;

	public Date getINGOODSACCEPTDATE() {
		return this.INGOODSACCEPTDATE;
	}

	public void setINGOODSACCEPTDATE(Date value) {
		this.INGOODSACCEPTDATE = value;
	}

	private String INDELCAT;

	public String getINDELCAT() {
		return this.INDELCAT;
	}

	public void setINDELCAT(String value) {
		this.INDELCAT = value;
	}

	private String INDESCRIPTION;

	public String getINDESCRIPTION() {
		return this.INDESCRIPTION;
	}

	public void setINDESCRIPTION(String value) {
		this.INDESCRIPTION = value;
	}

	private String ININSTNUMBER;

	public String getININSTNUMBER() {
		return this.ININSTNUMBER;
	}

	public void setININSTNUMBER(String value) {
		this.ININSTNUMBER = value;
	}

	private Date ININSTDATE;

	public Date getININSTDATE() {
		return this.ININSTDATE;
	}

	public void setININSTDATE(Date value) {
		this.ININSTDATE = value;
	}

	private Date ININSTDUEDATE;

	public Date getININSTDUEDATE() {
		return this.ININSTDUEDATE;
	}

	public void setININSTDUEDATE(Date value) {
		this.ININSTDUEDATE = value;
	}

	private Date INSTATDUEDATE;

	public Date getINSTATDUEDATE() {
		return this.INSTATDUEDATE;
	}

	public void setINSTATDUEDATE(Date value) {
		this.INSTATDUEDATE = value;
	}

	private Date INMATURITYDATE;

	public Date getINMATURITYDATE() {
		return this.INMATURITYDATE;
	}

	public void setINMATURITYDATE(Date value) {
		this.INMATURITYDATE = value;
	}

	private Date INFACTORMAXENDDATETIME;

	public Date getINFACTORMAXENDDATETIME() {
		return this.INFACTORMAXENDDATETIME;
	}

	public void setINFACTORMAXENDDATETIME(Date value) {
		this.INFACTORMAXENDDATETIME = value;
	}

	private String INCURRENCY;

	public String getINCURRENCY() {
		return this.INCURRENCY;
	}

	public void setINCURRENCY(String value) {
		this.INCURRENCY = value;
	}

	private BigDecimal INAMOUNT;

	public BigDecimal getINAMOUNT() {
		return this.INAMOUNT;
	}

	public void setINAMOUNT(BigDecimal value) {
		this.INAMOUNT = value;
	}

	private BigDecimal INHAIRCUTPERCENT;

	public BigDecimal getINHAIRCUTPERCENT() {
		return this.INHAIRCUTPERCENT;
	}

	public void setINHAIRCUTPERCENT(BigDecimal value) {
		this.INHAIRCUTPERCENT = value;
	}

	private BigDecimal INADJAMOUNT;

	public BigDecimal getINADJAMOUNT() {
		return this.INADJAMOUNT;
	}

	public void setINADJAMOUNT(BigDecimal value) {
		this.INADJAMOUNT = value;
	}

	private BigDecimal INCASHDISCOUNTPERCENT;

	public BigDecimal getINCASHDISCOUNTPERCENT() {
		return this.INCASHDISCOUNTPERCENT;
	}

	public void setINCASHDISCOUNTPERCENT(BigDecimal value) {
		this.INCASHDISCOUNTPERCENT = value;
	}

	private BigDecimal INCASHDISCOUNTVALUE;

	public BigDecimal getINCASHDISCOUNTVALUE() {
		return this.INCASHDISCOUNTVALUE;
	}

	public void setINCASHDISCOUNTVALUE(BigDecimal value) {
		this.INCASHDISCOUNTVALUE = value;
	}

	private BigDecimal INTDSAMOUNT;

	public BigDecimal getINTDSAMOUNT() {
		return this.INTDSAMOUNT;
	}

	public void setINTDSAMOUNT(BigDecimal value) {
		this.INTDSAMOUNT = value;
	}

	private BigDecimal INNETAMOUNT;

	public BigDecimal getINNETAMOUNT() {
		return this.INNETAMOUNT;
	}

	public void setINNETAMOUNT(BigDecimal value) {
		this.INNETAMOUNT = value;
	}

	private String ININSTIMAGE;

	public String getININSTIMAGE() {
		return this.ININSTIMAGE;
	}

	public void setININSTIMAGE(String value) {
		this.ININSTIMAGE = value;
	}

	private String INCREDITNOTEIMAGE;

	public String getINCREDITNOTEIMAGE() {
		return this.INCREDITNOTEIMAGE;
	}

	public void setINCREDITNOTEIMAGE(String value) {
		this.INCREDITNOTEIMAGE = value;
	}

	private String INSUPPORTINGS;

	public String getINSUPPORTINGS() {
		return this.INSUPPORTINGS;
	}

	public void setINSUPPORTINGS(String value) {
		this.INSUPPORTINGS = value;
	}

	private BigDecimal INCREDITPERIOD;

	public BigDecimal getINCREDITPERIOD() {
		return this.INCREDITPERIOD;
	}

	public void setINCREDITPERIOD(BigDecimal value) {
		this.INCREDITPERIOD = value;
	}

	private String INENABLEEXTENSION;

	public String getINENABLEEXTENSION() {
		return this.INENABLEEXTENSION;
	}

	public void setINENABLEEXTENSION(String value) {
		this.INENABLEEXTENSION = value;
	}

	private BigDecimal INEXTENDEDCREDITPERIOD;

	public BigDecimal getINEXTENDEDCREDITPERIOD() {
		return this.INEXTENDEDCREDITPERIOD;
	}

	public void setINEXTENDEDCREDITPERIOD(BigDecimal value) {
		this.INEXTENDEDCREDITPERIOD = value;
	}

	private Date INEXTENDEDDUEDATE;

	public Date getINEXTENDEDDUEDATE() {
		return this.INEXTENDEDDUEDATE;
	}

	public void setINEXTENDEDDUEDATE(Date value) {
		this.INEXTENDEDDUEDATE = value;
	}

	private String INAUTOACCEPT;

	public String getINAUTOACCEPT() {
		return this.INAUTOACCEPT;
	}

	public void setINAUTOACCEPT(String value) {
		this.INAUTOACCEPT = value;
	}

	private String INAUTOACCEPTABLEBIDTYPES;

	public String getINAUTOACCEPTABLEBIDTYPES() {
		return this.INAUTOACCEPTABLEBIDTYPES;
	}

	public void setINAUTOACCEPTABLEBIDTYPES(String value) {
		this.INAUTOACCEPTABLEBIDTYPES = value;
	}

	private String INAUTOCONVERT;

	public String getINAUTOCONVERT() {
		return this.INAUTOCONVERT;
	}

	public void setINAUTOCONVERT(String value) {
		this.INAUTOCONVERT = value;
	}

	private String INPERIOD1COSTBEARER;

	public String getINPERIOD1COSTBEARER() {
		return this.INPERIOD1COSTBEARER;
	}

	public void setINPERIOD1COSTBEARER(String value) {
		this.INPERIOD1COSTBEARER = value;
	}

	private BigDecimal INPERIOD1COSTPERCENT;

	public BigDecimal getINPERIOD1COSTPERCENT() {
		return this.INPERIOD1COSTPERCENT;
	}

	public void setINPERIOD1COSTPERCENT(BigDecimal value) {
		this.INPERIOD1COSTPERCENT = value;
	}

	private String INPERIOD2COSTBEARER;

	public String getINPERIOD2COSTBEARER() {
		return this.INPERIOD2COSTBEARER;
	}

	public void setINPERIOD2COSTBEARER(String value) {
		this.INPERIOD2COSTBEARER = value;
	}

	private BigDecimal INPERIOD2COSTPERCENT;

	public BigDecimal getINPERIOD2COSTPERCENT() {
		return this.INPERIOD2COSTPERCENT;
	}

	public void setINPERIOD2COSTPERCENT(BigDecimal value) {
		this.INPERIOD2COSTPERCENT = value;
	}

	private String INPERIOD3COSTBEARER;

	public String getINPERIOD3COSTBEARER() {
		return this.INPERIOD3COSTBEARER;
	}

	public void setINPERIOD3COSTBEARER(String value) {
		this.INPERIOD3COSTBEARER = value;
	}

	private BigDecimal INPERIOD3COSTPERCENT;

	public BigDecimal getINPERIOD3COSTPERCENT() {
		return this.INPERIOD3COSTPERCENT;
	}

	public void setINPERIOD3COSTPERCENT(BigDecimal value) {
		this.INPERIOD3COSTPERCENT = value;
	}

	private String INSETTLELEG3FLAG;

	public String getINSETTLELEG3FLAG() {
		return this.INSETTLELEG3FLAG;
	}

	public void setINSETTLELEG3FLAG(String value) {
		this.INSETTLELEG3FLAG = value;
	}

	private BigDecimal INFILEID;

	public BigDecimal getINFILEID() {
		return this.INFILEID;
	}

	public void setINFILEID(BigDecimal value) {
		this.INFILEID = value;
	}

	private String INSTATUS;

	public String getINSTATUS() {
		return this.INSTATUS;
	}

	public void setINSTATUS(String value) {
		this.INSTATUS = value;
	}

	private String INSTATUSREMARKS;

	public String getINSTATUSREMARKS() {
		return this.INSTATUSREMARKS;
	}

	public void setINSTATUSREMARKS(String value) {
		this.INSTATUSREMARKS = value;
	}

	private Date INSTATUSUPDATETIME;

	public Date getINSTATUSUPDATETIME() {
		return this.INSTATUSUPDATETIME;
	}

	public void setINSTATUSUPDATETIME(Date value) {
		this.INSTATUSUPDATETIME = value;
	}

	private String INMAKERENTITY;

	public String getINMAKERENTITY() {
		return this.INMAKERENTITY;
	}

	public void setINMAKERENTITY(String value) {
		this.INMAKERENTITY = value;
	}

	private BigDecimal INMAKERAUID;

	public BigDecimal getINMAKERAUID() {
		return this.INMAKERAUID;
	}

	public void setINMAKERAUID(BigDecimal value) {
		this.INMAKERAUID = value;
	}

	private BigDecimal INCHECKERAUID;

	public BigDecimal getINCHECKERAUID() {
		return this.INCHECKERAUID;
	}

	public void setINCHECKERAUID(BigDecimal value) {
		this.INCHECKERAUID = value;
	}

	private String INCOUNTERENTITY;

	public String getINCOUNTERENTITY() {
		return this.INCOUNTERENTITY;
	}

	public void setINCOUNTERENTITY(String value) {
		this.INCOUNTERENTITY = value;
	}

	private BigDecimal INCOUNTERAUID;

	public BigDecimal getINCOUNTERAUID() {
		return this.INCOUNTERAUID;
	}

	public void setINCOUNTERAUID(BigDecimal value) {
		this.INCOUNTERAUID = value;
	}

	private BigDecimal INCOUNTERCHECKERAUID;

	public BigDecimal getINCOUNTERCHECKERAUID() {
		return this.INCOUNTERCHECKERAUID;
	}

	public void setINCOUNTERCHECKERAUID(BigDecimal value) {
		this.INCOUNTERCHECKERAUID = value;
	}

	private String INOWNERENTITY;

	public String getINOWNERENTITY() {
		return this.INOWNERENTITY;
	}

	public void setINOWNERENTITY(String value) {
		this.INOWNERENTITY = value;
	}

	private BigDecimal INOWNERAUID;

	public BigDecimal getINOWNERAUID() {
		return this.INOWNERAUID;
	}

	public void setINOWNERAUID(BigDecimal value) {
		this.INOWNERAUID = value;
	}

	private String INMONETAGOLEDGERID;

	public String getINMONETAGOLEDGERID() {
		return this.INMONETAGOLEDGERID;
	}

	public void setINMONETAGOLEDGERID(String value) {
		this.INMONETAGOLEDGERID = value;
	}

	private String INMONETAGOFACTORTXNID;

	public String getINMONETAGOFACTORTXNID() {
		return this.INMONETAGOFACTORTXNID;
	}

	public void setINMONETAGOFACTORTXNID(String value) {
		this.INMONETAGOFACTORTXNID = value;
	}

	private String INMONETAGOCANCELTXNID;

	public String getINMONETAGOCANCELTXNID() {
		return this.INMONETAGOCANCELTXNID;
	}

	public void setINMONETAGOCANCELTXNID(String value) {
		this.INMONETAGOCANCELTXNID = value;
	}

	private BigDecimal INFUID;

	public BigDecimal getINFUID() {
		return this.INFUID;
	}

	public void setINFUID(BigDecimal value) {
		this.INFUID = value;
	}

	private String INCOUNTERMODIFIEDFIELDS;

	public String getINCOUNTERMODIFIEDFIELDS() {
		return this.INCOUNTERMODIFIEDFIELDS;
	}

	public void setINCOUNTERMODIFIEDFIELDS(String value) {
		this.INCOUNTERMODIFIEDFIELDS = value;
	}

	private String INSALESCATEGORY;

	public String getINSALESCATEGORY() {
		return this.INSALESCATEGORY;
	}

	public void setINSALESCATEGORY(String value) {
		this.INSALESCATEGORY = value;
	}

	private String INEWAYBILLNO;

	public String getINEWAYBILLNO() {
		return this.INEWAYBILLNO;
	}

	public void setINEWAYBILLNO(String value) {
		this.INEWAYBILLNO = value;
	}

	private String INSUPPLYTYPE;

	public String getINSUPPLYTYPE() {
		return this.INSUPPLYTYPE;
	}

	public void setINSUPPLYTYPE(String value) {
		this.INSUPPLYTYPE = value;
	}

	private String INDOCTYPE;

	public String getINDOCTYPE() {
		return this.INDOCTYPE;
	}

	public void setINDOCTYPE(String value) {
		this.INDOCTYPE = value;
	}

	private String INDOCNO;

	public String getINDOCNO() {
		return this.INDOCNO;
	}

	public void setINDOCNO(String value) {
		this.INDOCNO = value;
	}

	private Date INDOCDATE;

	public Date getINDOCDATE() {
		return this.INDOCDATE;
	}

	public void setINDOCDATE(Date value) {
		this.INDOCDATE = value;
	}

	private String INFROMPINCODE;

	public String getINFROMPINCODE() {
		return this.INFROMPINCODE;
	}

	public void setINFROMPINCODE(String value) {
		this.INFROMPINCODE = value;
	}

	private String INTOPINCODE;

	public String getINTOPINCODE() {
		return this.INTOPINCODE;
	}

	public void setINTOPINCODE(String value) {
		this.INTOPINCODE = value;
	}

	private BigDecimal INTRANSMODE;

	public BigDecimal getINTRANSMODE() {
		return this.INTRANSMODE;
	}

	public void setINTRANSMODE(BigDecimal value) {
		this.INTRANSMODE = value;
	}

	private String INTRANSPORTERNAME;

	public String getINTRANSPORTERNAME() {
		return this.INTRANSPORTERNAME;
	}

	public void setINTRANSPORTERNAME(String value) {
		this.INTRANSPORTERNAME = value;
	}

	private String INTRANSPORTERID;

	public String getINTRANSPORTERID() {
		return this.INTRANSPORTERID;
	}

	public void setINTRANSPORTERID(String value) {
		this.INTRANSPORTERID = value;
	}

	private String INTRANSDOCNO;

	public String getINTRANSDOCNO() {
		return this.INTRANSDOCNO;
	}

	public void setINTRANSDOCNO(String value) {
		this.INTRANSDOCNO = value;
	}

	private Date INTRANSDOCDATE;

	public Date getINTRANSDOCDATE() {
		return this.INTRANSDOCDATE;
	}

	public void setINTRANSDOCDATE(Date value) {
		this.INTRANSDOCDATE = value;
	}

	private String INVEHICLENO;

	public String getINVEHICLENO() {
		return this.INVEHICLENO;
	}

	public void setINVEHICLENO(String value) {
		this.INVEHICLENO = value;
	}

	private String INGROUPFLAG;

	public String getINGROUPFLAG() {
		return this.INGROUPFLAG;
	}

	public void setINGROUPFLAG(String value) {
		this.INGROUPFLAG = value;
	}

	private BigDecimal INGROUPINID;

	public BigDecimal getINGROUPINID() {
		return this.INGROUPINID;
	}

	public void setINGROUPINID(BigDecimal value) {
		this.INGROUPINID = value;
	}

	private String INGROUPREFNO;

	public String getINGROUPREFNO() {
		return this.INGROUPREFNO;
	}

	public void setINGROUPREFNO(String value) {
		this.INGROUPREFNO = value;
	}

	private BigDecimal INCERSAIFILEID;

	public BigDecimal getINCERSAIFILEID() {
		return this.INCERSAIFILEID;
	}

	public void setINCERSAIFILEID(BigDecimal value) {
		this.INCERSAIFILEID = value;
	}

	private BigDecimal INEBDID;

	public BigDecimal getINEBDID() {
		return this.INEBDID;
	}

	public void setINEBDID(BigDecimal value) {
		this.INEBDID = value;
	}

	private BigDecimal INMKRCHKLEVEL;

	public BigDecimal getINMKRCHKLEVEL() {
		return this.INMKRCHKLEVEL;
	}

	public void setINMKRCHKLEVEL(BigDecimal value) {
		this.INMKRCHKLEVEL = value;
	}

	private BigDecimal INCNTCHKLEVEL;

	public BigDecimal getINCNTCHKLEVEL() {
		return this.INCNTCHKLEVEL;
	}

	public void setINCNTCHKLEVEL(BigDecimal value) {
		this.INCNTCHKLEVEL = value;
	}

	private String INOTHERSETTINGS;

	public String getINOTHERSETTINGS() {
		return this.INOTHERSETTINGS;
	}

	public void setINOTHERSETTINGS(String value) {
		this.INOTHERSETTINGS = value;
	}

	private Date INRECORDCREATETIME;

	public Date getINRECORDCREATETIME() {
		return this.INRECORDCREATETIME;
	}

	public void setINRECORDCREATETIME(Date value) {
		this.INRECORDCREATETIME = value;
	}

	private BigDecimal INRECORDVERSION;

	public BigDecimal getINRECORDVERSION() {
		return this.INRECORDVERSION;
	}

	public void setINRECORDVERSION(BigDecimal value) {
		this.INRECORDVERSION = value;
	}

	private String INAGGREGATORENTITY;

	public String getINAGGREGATORENTITY() {
		return this.INAGGREGATORENTITY;
	}

	public void setINAGGREGATORENTITY(String value) {
		this.INAGGREGATORENTITY = value;
	}

	private BigDecimal INAGGREGATORAUID;

	public BigDecimal getINAGGREGATORAUID() {
		return this.INAGGREGATORAUID;
	}

	public void setINAGGREGATORAUID(BigDecimal value) {
		this.INAGGREGATORAUID = value;
	}

	private BigDecimal INPURBILLCLID;

	public BigDecimal getINPURBILLCLID() {
		return this.INPURBILLCLID;
	}

	public void setINPURBILLCLID(BigDecimal value) {
		this.INPURBILLCLID = value;
	}

	private BigDecimal INSUPBILLCLID;

	public BigDecimal getINSUPBILLCLID() {
		return this.INSUPBILLCLID;
	}

	public void setINSUPBILLCLID(BigDecimal value) {
		this.INSUPBILLCLID = value;
	}

	private String INCOUNTERUPDATEFIELDS;

	public String getINCOUNTERUPDATEFIELDS() {
		return this.INCOUNTERUPDATEFIELDS;
	}

	public void setINCOUNTERUPDATEFIELDS(String value) {
		this.INCOUNTERUPDATEFIELDS = value;
	}

	private BigDecimal INCFID;

	public BigDecimal getINCFID() {
		return this.INCFID;
	}

	public void setINCFID(BigDecimal value) {
		this.INCFID = value;
	}

	private String INCFDATA;

	public String getINCFDATA() {
		return this.INCFDATA;
	}

	public void setINCFDATA(String value) {
		this.INCFDATA = value;
	}

	private String INPERIOD1CHARGEBEARER;

	public String getINPERIOD1CHARGEBEARER() {
		return this.INPERIOD1CHARGEBEARER;
	}

	public void setINPERIOD1CHARGEBEARER(String value) {
		this.INPERIOD1CHARGEBEARER = value;
	}

	private BigDecimal INPERIOD1CHARGEPERCENT;

	public BigDecimal getINPERIOD1CHARGEPERCENT() {
		return this.INPERIOD1CHARGEPERCENT;
	}

	public void setINPERIOD1CHARGEPERCENT(BigDecimal value) {
		this.INPERIOD1CHARGEPERCENT = value;
	}

	private String INPERIOD2CHARGEBEARER;

	public String getINPERIOD2CHARGEBEARER() {
		return this.INPERIOD2CHARGEBEARER;
	}

	public void setINPERIOD2CHARGEBEARER(String value) {
		this.INPERIOD2CHARGEBEARER = value;
	}

	private BigDecimal INPERIOD2CHARGEPERCENT;

	public BigDecimal getINPERIOD2CHARGEPERCENT() {
		return this.INPERIOD2CHARGEPERCENT;
	}

	public void setINPERIOD2CHARGEPERCENT(BigDecimal value) {
		this.INPERIOD2CHARGEPERCENT = value;
	}

	private String INPERIOD3CHARGEBEARER;

	public String getINPERIOD3CHARGEBEARER() {
		return this.INPERIOD3CHARGEBEARER;
	}

	public void setINPERIOD3CHARGEBEARER(String value) {
		this.INPERIOD3CHARGEBEARER = value;
	}

	private BigDecimal INPERIOD3CHARGEPERCENT;

	public BigDecimal getINPERIOD3CHARGEPERCENT() {
		return this.INPERIOD3CHARGEPERCENT;
	}

	public void setINPERIOD3CHARGEPERCENT(BigDecimal value) {
		this.INPERIOD3CHARGEPERCENT = value;
	}

	public List<InstrumentWorkFlowHostApiBean> getInstrumentWorkFlowList() {
		return instrumentWorkFlowList;
	}

	public void setInstrumentWorkFlowList(List<InstrumentWorkFlowHostApiBean> instrumentWorkFlowList) {
		this.instrumentWorkFlowList = instrumentWorkFlowList;
	}

	public Map<String, Object> getCustomFieldMap() {
		return customFieldMap;
	}

	public void setCustomFieldMap(Map<String, Object> customFieldMap) {
		this.customFieldMap = customFieldMap;
	}
	
	

}
