package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaserSupplierLinkHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierLinkHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private List<PurchaserSupplierLinkWorkFlowHostApiBean> purchaserSupplierWorkFlow = new ArrayList<>();

	private List<String> purchaserSupplierRef = new ArrayList<>();

	private List<String> supplierPurchaserRef = new ArrayList<>();

	private List<PurchaserSupplierCapRateHostApiBean> capRate = new ArrayList<>();

	private String PSLSUPPLIER;

	public String getPSLSUPPLIER() {
		return this.PSLSUPPLIER;
	}

	public void setPSLSUPPLIER(String value) {
		this.PSLSUPPLIER = value;
	}

	private String PSLPURCHASER;

	public String getPSLPURCHASER() {
		return this.PSLPURCHASER;
	}

	public void setPSLPURCHASER(String value) {
		this.PSLPURCHASER = value;
	}

	private String PSLSUPPLIERPURCHASERREF;

	public String getPSLSUPPLIERPURCHASERREF() {
		return this.PSLSUPPLIERPURCHASERREF;
	}

	public void setPSLSUPPLIERPURCHASERREF(String value) {
		this.PSLSUPPLIERPURCHASERREF = value;
	}

	private BigDecimal PSLCREDITPERIOD;

	public BigDecimal getPSLCREDITPERIOD() {
		return this.PSLCREDITPERIOD;
	}

	public void setPSLCREDITPERIOD(BigDecimal value) {
		this.PSLCREDITPERIOD = value;
	}

	private BigDecimal PSLEXTENDEDCREDITPERIOD;

	public BigDecimal getPSLEXTENDEDCREDITPERIOD() {
		return this.PSLEXTENDEDCREDITPERIOD;
	}

	public void setPSLEXTENDEDCREDITPERIOD(BigDecimal value) {
		this.PSLEXTENDEDCREDITPERIOD = value;
	}

	private String PSLPURCHASERSUPPLIERREF;

	public String getPSLPURCHASERSUPPLIERREF() {
		return this.PSLPURCHASERSUPPLIERREF;
	}

	public void setPSLPURCHASERSUPPLIERREF(String value) {
		this.PSLPURCHASERSUPPLIERREF = value;
	}

	private String PSLPERIOD1COSTBEARER;

	public String getPSLPERIOD1COSTBEARER() {
		return this.PSLPERIOD1COSTBEARER;
	}

	public void setPSLPERIOD1COSTBEARER(String value) {
		this.PSLPERIOD1COSTBEARER = value;
	}

	private BigDecimal PSLPERIOD1COSTPERCENT;

	public BigDecimal getPSLPERIOD1COSTPERCENT() {
		return this.PSLPERIOD1COSTPERCENT;
	}

	public void setPSLPERIOD1COSTPERCENT(BigDecimal value) {
		this.PSLPERIOD1COSTPERCENT = value;
	}

	private String PSLPERIOD2COSTBEARER;

	public String getPSLPERIOD2COSTBEARER() {
		return this.PSLPERIOD2COSTBEARER;
	}

	public void setPSLPERIOD2COSTBEARER(String value) {
		this.PSLPERIOD2COSTBEARER = value;
	}

	private BigDecimal PSLPERIOD2COSTPERCENT;

	public BigDecimal getPSLPERIOD2COSTPERCENT() {
		return this.PSLPERIOD2COSTPERCENT;
	}

	public void setPSLPERIOD2COSTPERCENT(BigDecimal value) {
		this.PSLPERIOD2COSTPERCENT = value;
	}

	private String PSLPERIOD3COSTBEARER;

	public String getPSLPERIOD3COSTBEARER() {
		return this.PSLPERIOD3COSTBEARER;
	}

	public void setPSLPERIOD3COSTBEARER(String value) {
		this.PSLPERIOD3COSTBEARER = value;
	}

	private BigDecimal PSLPERIOD3COSTPERCENT;

	public BigDecimal getPSLPERIOD3COSTPERCENT() {
		return this.PSLPERIOD3COSTPERCENT;
	}

	public void setPSLPERIOD3COSTPERCENT(BigDecimal value) {
		this.PSLPERIOD3COSTPERCENT = value;
	}

	private String PSLBIDACCEPTINGENTITYTYPE;

	public String getPSLBIDACCEPTINGENTITYTYPE() {
		return this.PSLBIDACCEPTINGENTITYTYPE;
	}

	public void setPSLBIDACCEPTINGENTITYTYPE(String value) {
		this.PSLBIDACCEPTINGENTITYTYPE = value;
	}

	private String PSLSETTLELEG3FLAG;

	public String getPSLSETTLELEG3FLAG() {
		return this.PSLSETTLELEG3FLAG;
	}

	public void setPSLSETTLELEG3FLAG(String value) {
		this.PSLSETTLELEG3FLAG = value;
	}

	private String PSLAUTOACCEPT;

	public String getPSLAUTOACCEPT() {
		return this.PSLAUTOACCEPT;
	}

	public void setPSLAUTOACCEPT(String value) {
		this.PSLAUTOACCEPT = value;
	}

	private String PSLAUTOACCEPTABLEBIDTYPES;

	public String getPSLAUTOACCEPTABLEBIDTYPES() {
		return this.PSLAUTOACCEPTABLEBIDTYPES;
	}

	public void setPSLAUTOACCEPTABLEBIDTYPES(String value) {
		this.PSLAUTOACCEPTABLEBIDTYPES = value;
	}

	private String PSLAUTOCONVERT;

	public String getPSLAUTOCONVERT() {
		return this.PSLAUTOCONVERT;
	}

	public void setPSLAUTOCONVERT(String value) {
		this.PSLAUTOCONVERT = value;
	}

	private String PSLPURCHASERAUTOAPPROVEINVOICE;

	public String getPSLPURCHASERAUTOAPPROVEINVOICE() {
		return this.PSLPURCHASERAUTOAPPROVEINVOICE;
	}

	public void setPSLPURCHASERAUTOAPPROVEINVOICE(String value) {
		this.PSLPURCHASERAUTOAPPROVEINVOICE = value;
	}

	private String PSLSELLERAUTOAPPROVEINVOICE;

	public String getPSLSELLERAUTOAPPROVEINVOICE() {
		return this.PSLSELLERAUTOAPPROVEINVOICE;
	}

	public void setPSLSELLERAUTOAPPROVEINVOICE(String value) {
		this.PSLSELLERAUTOAPPROVEINVOICE = value;
	}

	private String PSLSTATUS;

	public String getPSLSTATUS() {
		return this.PSLSTATUS;
	}

	public void setPSLSTATUS(String value) {
		this.PSLSTATUS = value;
	}

	private String PSLAPPROVALSTATUS;

	public String getPSLAPPROVALSTATUS() {
		return this.PSLAPPROVALSTATUS;
	}

	public void setPSLAPPROVALSTATUS(String value) {
		this.PSLAPPROVALSTATUS = value;
	}

	private String PSLINVOICEMANDATORY;

	public String getPSLINVOICEMANDATORY() {
		return this.PSLINVOICEMANDATORY;
	}

	public void setPSLINVOICEMANDATORY(String value) {
		this.PSLINVOICEMANDATORY = value;
	}

	private String PSLREMARKS;

	public String getPSLREMARKS() {
		return this.PSLREMARKS;
	}

	public void setPSLREMARKS(String value) {
		this.PSLREMARKS = value;
	}

	private String PSLINWORKFLOW;

	public String getPSLINWORKFLOW() {
		return this.PSLINWORKFLOW;
	}

	public void setPSLINWORKFLOW(String value) {
		this.PSLINWORKFLOW = value;
	}

	private BigDecimal PSLCASHDISCOUNTPERCENT;

	public BigDecimal getPSLCASHDISCOUNTPERCENT() {
		return this.PSLCASHDISCOUNTPERCENT;
	}

	public void setPSLCASHDISCOUNTPERCENT(BigDecimal value) {
		this.PSLCASHDISCOUNTPERCENT = value;
	}

	private BigDecimal PSLHAIRCUTPERCENT;

	public BigDecimal getPSLHAIRCUTPERCENT() {
		return this.PSLHAIRCUTPERCENT;
	}

	public void setPSLHAIRCUTPERCENT(BigDecimal value) {
		this.PSLHAIRCUTPERCENT = value;
	}

	private String PSLINSTRUMENTCREATION;

	public String getPSLINSTRUMENTCREATION() {
		return this.PSLINSTRUMENTCREATION;
	}

	public void setPSLINSTRUMENTCREATION(String value) {
		this.PSLINSTRUMENTCREATION = value;
	}

	private String PSLPLATFORMSTATUS;

	public String getPSLPLATFORMSTATUS() {
		return this.PSLPLATFORMSTATUS;
	}

	public void setPSLPLATFORMSTATUS(String value) {
		this.PSLPLATFORMSTATUS = value;
	}

	private String PSLRELATIONFLAG;

	public String getPSLRELATIONFLAG() {
		return this.PSLRELATIONFLAG;
	}

	public void setPSLRELATIONFLAG(String value) {
		this.PSLRELATIONFLAG = value;
	}

	private String PSLPLATFORMREASONCODE;

	public String getPSLPLATFORMREASONCODE() {
		return this.PSLPLATFORMREASONCODE;
	}

	public void setPSLPLATFORMREASONCODE(String value) {
		this.PSLPLATFORMREASONCODE = value;
	}

	private String PSLRELATIONDOC;

	public String getPSLRELATIONDOC() {
		return this.PSLRELATIONDOC;
	}

	public void setPSLRELATIONDOC(String value) {
		this.PSLRELATIONDOC = value;
	}

	private Date PSLRELATIONEFFECTIVEDATE;

	public Date getPSLRELATIONEFFECTIVEDATE() {
		return this.PSLRELATIONEFFECTIVEDATE;
	}

	public void setPSLRELATIONEFFECTIVEDATE(Date value) {
		this.PSLRELATIONEFFECTIVEDATE = value;
	}

	private String PSLPLATFORMREMARKS;

	public String getPSLPLATFORMREMARKS() {
		return this.PSLPLATFORMREMARKS;
	}

	public void setPSLPLATFORMREMARKS(String value) {
		this.PSLPLATFORMREMARKS = value;
	}

	private String PSLPERIOD1CHARGEBEARER;

	public String getPSLPERIOD1CHARGEBEARER() {
		return this.PSLPERIOD1CHARGEBEARER;
	}

	public void setPSLPERIOD1CHARGEBEARER(String value) {
		this.PSLPERIOD1CHARGEBEARER = value;
	}

	private BigDecimal PSLPERIOD1CHARGEPERCENT;

	public BigDecimal getPSLPERIOD1CHARGEPERCENT() {
		return this.PSLPERIOD1CHARGEPERCENT;
	}

	public void setPSLPERIOD1CHARGEPERCENT(BigDecimal value) {
		this.PSLPERIOD1CHARGEPERCENT = value;
	}

	private String PSLPERIOD2CHARGEBEARER;

	public String getPSLPERIOD2CHARGEBEARER() {
		return this.PSLPERIOD2CHARGEBEARER;
	}

	public void setPSLPERIOD2CHARGEBEARER(String value) {
		this.PSLPERIOD2CHARGEBEARER = value;
	}

	private BigDecimal PSLPERIOD2CHARGEPERCENT;

	public BigDecimal getPSLPERIOD2CHARGEPERCENT() {
		return this.PSLPERIOD2CHARGEPERCENT;
	}

	public void setPSLPERIOD2CHARGEPERCENT(BigDecimal value) {
		this.PSLPERIOD2CHARGEPERCENT = value;
	}

	private String PSLPERIOD3CHARGEBEARER;

	public String getPSLPERIOD3CHARGEBEARER() {
		return this.PSLPERIOD3CHARGEBEARER;
	}

	public void setPSLPERIOD3CHARGEBEARER(String value) {
		this.PSLPERIOD3CHARGEBEARER = value;
	}

	private BigDecimal PSLPERIOD3CHARGEPERCENT;

	public BigDecimal getPSLPERIOD3CHARGEPERCENT() {
		return this.PSLPERIOD3CHARGEPERCENT;
	}

	public void setPSLPERIOD3CHARGEPERCENT(BigDecimal value) {
		this.PSLPERIOD3CHARGEPERCENT = value;
	}

	private String PSLBUYERTDS;

	public String getPSLBUYERTDS() {
		return this.PSLBUYERTDS;
	}

	public void setPSLBUYERTDS(String value) {
		this.PSLBUYERTDS = value;
	}

	private String PSLSELLERTDS;

	public String getPSLSELLERTDS() {
		return this.PSLSELLERTDS;
	}

	public void setPSLSELLERTDS(String value) {
		this.PSLSELLERTDS = value;
	}

	private BigDecimal PSLBUYERTDSPERCENT;

	public BigDecimal getPSLBUYERTDSPERCENT() {
		return this.PSLBUYERTDSPERCENT;
	}

	public void setPSLBUYERTDSPERCENT(BigDecimal value) {
		this.PSLBUYERTDSPERCENT = value;
	}

	private BigDecimal PSLSELLERTDSPERCENT;

	public BigDecimal getPSLSELLERTDSPERCENT() {
		return this.PSLSELLERTDSPERCENT;
	}

	public void setPSLSELLERTDSPERCENT(BigDecimal value) {
		this.PSLSELLERTDSPERCENT = value;
	}

	private String PSLAUTHORIZERXIL;

	public String getPSLAUTHORIZERXIL() {
		return this.PSLAUTHORIZERXIL;
	}

	public void setPSLAUTHORIZERXIL(String value) {
		this.PSLAUTHORIZERXIL = value;
	}

	public List<PurchaserSupplierLinkWorkFlowHostApiBean> getPurchaserSupplierWorkFlow() {
		return purchaserSupplierWorkFlow;
	}

	public void setPurchaserSupplierWorkFlow(List<PurchaserSupplierLinkWorkFlowHostApiBean> purchaserSupplierWorkFlow) {
		this.purchaserSupplierWorkFlow = purchaserSupplierWorkFlow;
	}

	public List<String> getPurchaserSupplierRef() {
		return purchaserSupplierRef;
	}

	public void setPurchaserSupplierRef(List<String> purchaserSupplierRef) {
		this.purchaserSupplierRef = purchaserSupplierRef;
	}

	public List<String> getSupplierPurchaserRef() {
		return supplierPurchaserRef;
	}

	public void setSupplierPurchaserRef(List<String> supplierPurchaserRef) {
		this.supplierPurchaserRef = supplierPurchaserRef;
	}

	public List<PurchaserSupplierCapRateHostApiBean> getCapRate() {
		return capRate;
	}

	public void setCapRate(List<PurchaserSupplierCapRateHostApiBean> capRate) {
		this.capRate = capRate;
	}

}
