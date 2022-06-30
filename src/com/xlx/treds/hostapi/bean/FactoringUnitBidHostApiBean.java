package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FactoringUnitBidHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BigDecimal BDFUID;

	public BigDecimal getBDFUID() {
		return this.BDFUID;
	}

	public void setBDFUID(BigDecimal value) {
		this.BDFUID = value;
	}

	private String BDFINANCIERENTITY;

	public String getBDFINANCIERENTITY() {
		return this.BDFINANCIERENTITY;
	}

	public void setBDFINANCIERENTITY(String value) {
		this.BDFINANCIERENTITY = value;
	}

	private BigDecimal BDFINANCIERAUID;

	public BigDecimal getBDFINANCIERAUID() {
		return this.BDFINANCIERAUID;
	}

	public void setBDFINANCIERAUID(BigDecimal value) {
		this.BDFINANCIERAUID = value;
	}

	private BigDecimal BDRATE;

	public BigDecimal getBDRATE() {
		return this.BDRATE;
	}

	public void setBDRATE(BigDecimal value) {
		this.BDRATE = value;
	}

	private BigDecimal BDHAIRCUT;

	public BigDecimal getBDHAIRCUT() {
		return this.BDHAIRCUT;
	}

	public void setBDHAIRCUT(BigDecimal value) {
		this.BDHAIRCUT = value;
	}

	private Date BDVALIDTILL;

	public Date getBDVALIDTILL() {
		return this.BDVALIDTILL;
	}

	public void setBDVALIDTILL(Date value) {
		this.BDVALIDTILL = value;
	}

	private String BDSTATUS;

	public String getBDSTATUS() {
		return this.BDSTATUS;
	}

	public void setBDSTATUS(String value) {
		this.BDSTATUS = value;
	}

	private String BDSTATUSREMARKS;

	public String getBDSTATUSREMARKS() {
		return this.BDSTATUSREMARKS;
	}

	public void setBDSTATUSREMARKS(String value) {
		this.BDSTATUSREMARKS = value;
	}

	private BigDecimal BDID;

	public BigDecimal getBDID() {
		return this.BDID;
	}

	public void setBDID(BigDecimal value) {
		this.BDID = value;
	}

	private Date BDTIMESTAMP;

	public Date getBDTIMESTAMP() {
		return this.BDTIMESTAMP;
	}

	public void setBDTIMESTAMP(Date value) {
		this.BDTIMESTAMP = value;
	}

	private BigDecimal BDLASTAUID;

	public BigDecimal getBDLASTAUID() {
		return this.BDLASTAUID;
	}

	public void setBDLASTAUID(BigDecimal value) {
		this.BDLASTAUID = value;
	}

	private String BDBIDTYPE;

	public String getBDBIDTYPE() {
		return this.BDBIDTYPE;
	}

	public void setBDBIDTYPE(String value) {
		this.BDBIDTYPE = value;
	}

	private BigDecimal BDPROVRATE;

	public BigDecimal getBDPROVRATE() {
		return this.BDPROVRATE;
	}

	public void setBDPROVRATE(BigDecimal value) {
		this.BDPROVRATE = value;
	}

	private BigDecimal BDPROVHAIRCUT;

	public BigDecimal getBDPROVHAIRCUT() {
		return this.BDPROVHAIRCUT;
	}

	public void setBDPROVHAIRCUT(BigDecimal value) {
		this.BDPROVHAIRCUT = value;
	}

	private Date BDPROVVALIDTILL;

	public Date getBDPROVVALIDTILL() {
		return this.BDPROVVALIDTILL;
	}

	public void setBDPROVVALIDTILL(Date value) {
		this.BDPROVVALIDTILL = value;
	}

	private String BDPROVBIDTYPE;

	public String getBDPROVBIDTYPE() {
		return this.BDPROVBIDTYPE;
	}

	public void setBDPROVBIDTYPE(String value) {
		this.BDPROVBIDTYPE = value;
	}

	private String BDPROVACTION;

	public String getBDPROVACTION() {
		return this.BDPROVACTION;
	}

	public void setBDPROVACTION(String value) {
		this.BDPROVACTION = value;
	}

	private String BDAPPSTATUS;

	public String getBDAPPSTATUS() {
		return this.BDAPPSTATUS;
	}

	public void setBDAPPSTATUS(String value) {
		this.BDAPPSTATUS = value;
	}

	private String BDAPPREMARKS;

	public String getBDAPPREMARKS() {
		return this.BDAPPREMARKS;
	}

	public void setBDAPPREMARKS(String value) {
		this.BDAPPREMARKS = value;
	}

	private BigDecimal BDCHECKERAUID;

	public BigDecimal getBDCHECKERAUID() {
		return this.BDCHECKERAUID;
	}

	public void setBDCHECKERAUID(BigDecimal value) {
		this.BDCHECKERAUID = value;
	}

	private BigDecimal BDLIMITUTILISED;

	public BigDecimal getBDLIMITUTILISED() {
		return this.BDLIMITUTILISED;
	}

	public void setBDLIMITUTILISED(BigDecimal value) {
		this.BDLIMITUTILISED = value;
	}

	private BigDecimal BDBIDLIMITUTILISED;

	public BigDecimal getBDBIDLIMITUTILISED() {
		return this.BDBIDLIMITUTILISED;
	}

	public void setBDBIDLIMITUTILISED(BigDecimal value) {
		this.BDBIDLIMITUTILISED = value;
	}

	private String BDLIMITIDS;

	public String getBDLIMITIDS() {
		return this.BDLIMITIDS;
	}

	public void setBDLIMITIDS(String value) {
		this.BDLIMITIDS = value;
	}

	private String BDCOSTLEG;

	public String getBDCOSTLEG() {
		return this.BDCOSTLEG;
	}

	public void setBDCOSTLEG(String value) {
		this.BDCOSTLEG = value;
	}

	private BigDecimal BDCHKLEVEL;

	public BigDecimal getBDCHKLEVEL() {
		return this.BDCHKLEVEL;
	}

	public void setBDCHKLEVEL(BigDecimal value) {
		this.BDCHKLEVEL = value;
	}

	private String BDCHARGES;

	public String getBDCHARGES() {
		return this.BDCHARGES;
	}

	public void setBDCHARGES(String value) {
		this.BDCHARGES = value;
	}

}
