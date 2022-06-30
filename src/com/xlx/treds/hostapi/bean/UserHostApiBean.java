package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private Map<String, Object> otherSettings = new HashMap<String, Object>();

	private List<Map<String, Object>> checkerLevelSettings = new ArrayList<>();

	private Map<String, Object> securitySettings = new HashMap<String, Object>();

	private Map<String, Object> userLimits = new HashMap<String, Object>();

	private List<UserHostApiRmIdMapping> rmMappingList = new ArrayList<>();

	private List<UserHostApiMakerCheckerMap> makerCheckerMap = new ArrayList<>();

	private BigDecimal AUID;

	public BigDecimal getAUID() {
		return this.AUID;
	}

	public void setAUID(BigDecimal value) {
		this.AUID = value;
	}

	private String AUDOMAIN;

	public String getAUDOMAIN() {
		return this.AUDOMAIN;
	}

	public void setAUDOMAIN(String value) {
		this.AUDOMAIN = value;
	}

	private String AULOGINID;

	public String getAULOGINID() {
		return this.AULOGINID;
	}

	public void setAULOGINID(String value) {
		this.AULOGINID = value;
	}

	private String AUPASSWORD1;

	public String getAUPASSWORD1() {
		return this.AUPASSWORD1;
	}

	public void setAUPASSWORD1(String value) {
		this.AUPASSWORD1 = value;
	}

	private Date AUPASSWORDUPDATEDAT1;

	public Date getAUPASSWORDUPDATEDAT1() {
		return this.AUPASSWORDUPDATEDAT1;
	}

	public void setAUPASSWORDUPDATEDAT1(Date value) {
		this.AUPASSWORDUPDATEDAT1 = value;
	}

	private String AUFORCEPASSWORDCHANGE;

	public String getAUFORCEPASSWORDCHANGE() {
		return this.AUFORCEPASSWORDCHANGE;
	}

	public void setAUFORCEPASSWORDCHANGE(String value) {
		this.AUFORCEPASSWORDCHANGE = value;
	}

	private String AUSTATUS;

	public String getAUSTATUS() {
		return this.AUSTATUS;
	}

	public void setAUSTATUS(String value) {
		this.AUSTATUS = value;
	}

	private String AUREASON;

	public String getAUREASON() {
		return this.AUREASON;
	}

	public void setAUREASON(String value) {
		this.AUREASON = value;
	}

	private BigDecimal AUFAILEDLOGINCOUNT;

	public BigDecimal getAUFAILEDLOGINCOUNT() {
		return this.AUFAILEDLOGINCOUNT;
	}

	public void setAUFAILEDLOGINCOUNT(BigDecimal value) {
		this.AUFAILEDLOGINCOUNT = value;
	}

	private BigDecimal AUTYPE;

	public BigDecimal getAUTYPE() {
		return this.AUTYPE;
	}

	public void setAUTYPE(BigDecimal value) {
		this.AUTYPE = value;
	}

	private String AUSALUTATION;

	public String getAUSALUTATION() {
		return this.AUSALUTATION;
	}

	public void setAUSALUTATION(String value) {
		this.AUSALUTATION = value;
	}

	private String AUFIRSTNAME;

	public String getAUFIRSTNAME() {
		return this.AUFIRSTNAME;
	}

	public void setAUFIRSTNAME(String value) {
		this.AUFIRSTNAME = value;
	}

	private String AUMIDDLENAME;

	public String getAUMIDDLENAME() {
		return this.AUMIDDLENAME;
	}

	public void setAUMIDDLENAME(String value) {
		this.AUMIDDLENAME = value;
	}

	private String AULASTNAME;

	public String getAULASTNAME() {
		return this.AULASTNAME;
	}

	public void setAULASTNAME(String value) {
		this.AULASTNAME = value;
	}

	private String AUTELEPHONE;

	public String getAUTELEPHONE() {
		return this.AUTELEPHONE;
	}

	public void setAUTELEPHONE(String value) {
		this.AUTELEPHONE = value;
	}

	private String AUMOBILE;

	public String getAUMOBILE() {
		return this.AUMOBILE;
	}

	public void setAUMOBILE(String value) {
		this.AUMOBILE = value;
	}

	private String AUEMAIL;

	public String getAUEMAIL() {
		return this.AUEMAIL;
	}

	public void setAUEMAIL(String value) {
		this.AUEMAIL = value;
	}

	private String AUALTEMAIL;

	public String getAUALTEMAIL() {
		return this.AUALTEMAIL;
	}

	public void setAUALTEMAIL(String value) {
		this.AUALTEMAIL = value;
	}

	private String AUENABLE2FA;

	public String getAUENABLE2FA() {
		return this.AUENABLE2FA;
	}

	public void setAUENABLE2FA(String value) {
		this.AUENABLE2FA = value;
	}

	private String AUSECURITYSETTINGS;

	public String getAUSECURITYSETTINGS() {
		return this.AUSECURITYSETTINGS;
	}

	public void setAUSECURITYSETTINGS(String value) {
		this.AUSECURITYSETTINGS = value;
	}

	private String AUOTHERSETTINGS;

	public String getAUOTHERSETTINGS() {
		return this.AUOTHERSETTINGS;
	}

	public void setAUOTHERSETTINGS(String value) {
		this.AUOTHERSETTINGS = value;
	}

	private String AUENABLEAPI;

	public String getAUENABLEAPI() {
		return this.AUENABLEAPI;
	}

	public void setAUENABLEAPI(String value) {
		this.AUENABLEAPI = value;
	}

	private String AURMIDS;

	public String getAURMIDS() {
		return this.AURMIDS;
	}

	public void setAURMIDS(String value) {
		this.AURMIDS = value;
	}

	private String AUFULLOWNERSHIP;

	public String getAUFULLOWNERSHIP() {
		return this.AUFULLOWNERSHIP;
	}

	public void setAUFULLOWNERSHIP(String value) {
		this.AUFULLOWNERSHIP = value;
	}

	private BigDecimal AUOWNERAUID;

	public BigDecimal getAUOWNERAUID() {
		return this.AUOWNERAUID;
	}

	public void setAUOWNERAUID(BigDecimal value) {
		this.AUOWNERAUID = value;
	}

	private String AUIPS;

	public String getAUIPS() {
		return this.AUIPS;
	}

	public void setAUIPS(String value) {
		this.AUIPS = value;
	}

	private String AUUSERLIMITS;

	public String getAUUSERLIMITS() {
		return this.AUUSERLIMITS;
	}

	public void setAUUSERLIMITS(String value) {
		this.AUUSERLIMITS = value;
	}

	private BigDecimal AURECORDCREATOR;

	public BigDecimal getAURECORDCREATOR() {
		return this.AURECORDCREATOR;
	}

	public void setAURECORDCREATOR(BigDecimal value) {
		this.AURECORDCREATOR = value;
	}

	private Date AURECORDCREATETIME;

	public Date getAURECORDCREATETIME() {
		return this.AURECORDCREATETIME;
	}

	public void setAURECORDCREATETIME(Date value) {
		this.AURECORDCREATETIME = value;
	}

	private BigDecimal AURECORDUPDATOR;

	public BigDecimal getAURECORDUPDATOR() {
		return this.AURECORDUPDATOR;
	}

	public void setAURECORDUPDATOR(BigDecimal value) {
		this.AURECORDUPDATOR = value;
	}

	private Date AURECORDUPDATETIME;

	public Date getAURECORDUPDATETIME() {
		return this.AURECORDUPDATETIME;
	}

	public void setAURECORDUPDATETIME(Date value) {
		this.AURECORDUPDATETIME = value;
	}

	private BigDecimal AURECORDVERSION;

	public BigDecimal getAURECORDVERSION() {
		return this.AURECORDVERSION;
	}

	public void setAURECORDVERSION(BigDecimal value) {
		this.AURECORDVERSION = value;
	}

	private String AULOCATIONIDS;

	public String getAULOCATIONIDS() {
		return this.AULOCATIONIDS;
	}

	public void setAULOCATIONIDS(String value) {
		this.AULOCATIONIDS = value;
	}

	private String AUCHECKERLEVELSETTING;

	public String getAUCHECKERLEVELSETTING() {
		return this.AUCHECKERLEVELSETTING;
	}

	public void setAUCHECKERLEVELSETTING(String value) {
		this.AUCHECKERLEVELSETTING = value;
	}

	private String AURMLOCATION;

	public String getAURMLOCATION() {
		return this.AURMLOCATION;
	}

	public void setAURMLOCATION(String value) {
		this.AURMLOCATION = value;
	}

	public Map<String, Object> getOtherSettings() {
		return otherSettings;
	}

	public void setOtherSettings(Map<String, Object> otherSettings) {
		this.otherSettings = otherSettings;
	}

	public List<Map<String, Object>> getCheckerLevelSettings() {
		return checkerLevelSettings;
	}

	public void setCheckerLevelSettings(List<Map<String, Object>> checkerLevelSettings) {
		this.checkerLevelSettings = checkerLevelSettings;
	}

	public Map<String, Object> getSecuritySettings() {
		return securitySettings;
	}

	public void setSecuritySettings(Map<String, Object> securitySettings) {
		this.securitySettings = securitySettings;
	}

	public List<UserHostApiRmIdMapping> getRmMappingList() {
		return rmMappingList;
	}

	public void setRmMappingList(List<UserHostApiRmIdMapping> rmMappingList) {
		this.rmMappingList = rmMappingList;
	}

	public Map<String, Object> getUserLimits() {
		return userLimits;
	}

	public void setUserLimits(Map<String, Object> userLimits) {
		this.userLimits = userLimits;
	}

	public List<UserHostApiMakerCheckerMap> getMakerCheckerMap() {
		return makerCheckerMap;
	}

	public void setMakerCheckerMap(List<UserHostApiMakerCheckerMap> makerCheckerMap) {
		this.makerCheckerMap = makerCheckerMap;
	}

}
