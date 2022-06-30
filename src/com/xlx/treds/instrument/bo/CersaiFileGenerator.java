package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.instrument.bean.CersaiFileBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.CersaiFileDataWrapper.CersaiEntityType;
import com.xlx.treds.user.bean.AppUserBean;

public class CersaiFileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CersaiFileGenerator.class);
	//
    private Map<String,Long> financierPurchaserLimits = new HashMap<String,Long>();
    private static final Map<String, String> cersaiStateMap = getCersaiStateMap();
    //
    private static String FIELD_SEPERATOR = "|";
    private static String LINE_SEPERATOR = "\r\n";
    private static String DATE_FORMAT = AppConstants.DATE_FORMAT;
    private static String ADDRESS_ALLOWED_FORMAT = "[^a-zA-Z0-9///,/-/./&/#/(/)]";
    private static String INVOICE_DESC_ALLOWED_FORMAT = "[^a-zA-Z0-9///&]";
    private static String DISTRICT_CITY_ALLOWED_FORMAT = "[^a-zA-Z]";
    private static String ALLOWED_CHARS = "[^a-zA-Z0-9]";
     
    //
	//CERSAI BASE FILE HEADER
	private static int CERSAI_BFH_FH = 0;
	private static int CERSAI_BFH_FCTR = 1;
	private static int CERSAI_BFH_NO_OF_RECORDS = 2;
	private static int CERSAI_BFH_DATE_OF_FILE_UPLOAD = 3;
	private static int CERSAI_BFH_RECORDSIZE = 4;
	private static int[] CERSAI_BFH_FIXED_FIELD_SIZES = getCersaiBaseFileHeaderFixedFieldSizes();
	//
    private static int[] getCersaiBaseFileHeaderFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_BFH_RECORDSIZE];
    	lSizes[CERSAI_BFH_FH] = 2;
    	lSizes[CERSAI_BFH_FCTR] = 4;
    	lSizes[CERSAI_BFH_NO_OF_RECORDS] = 6;
    	lSizes[CERSAI_BFH_DATE_OF_FILE_UPLOAD] = 10;
    	return lSizes;
    }
    //
    private static Map<String,String> constitutionMapping = getConstitutionMapping();
    private static Map<String,String> getConstitutionMapping(){
    	Map<String,String> lRetVal = new HashMap<String,String>();
    	lRetVal.put("PROP","PRF");
    	lRetVal.put("PART","PAF");
    	lRetVal.put("PRIV","COM");
    	lRetVal.put("PUB","COM");
    	lRetVal.put("TRST","TRS");
    	lRetVal.put("HUF","HUF");
    	lRetVal.put("GOVT","");
    	lRetVal.put("LLP","LLP");
    	return lRetVal;
    }
    //
    public static String getMappedConsitution(String pConstitution){
    	if(constitutionMapping.containsKey(pConstitution)){
    		return constitutionMapping.get(pConstitution);
    	}
    	return"";
    }
    //
    public static List<Long> baseFileHeaderSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_BFH_DATE_OF_FILE_UPLOAD));
		return lSkipList;
	}
    //
    private String getCersaiBaseFileHeader(CersaiFileDataWrapper pCFDataWrapper){
    	StringBuilder lReturnValue = new StringBuilder();
    	String[] lData = new String[CERSAI_BFH_RECORDSIZE];
    	// 1 - FH - CONSTANT. Value is "FH". - MANDATORY
    	// 1 - FH stands for File Header.  It’s a constant and is "FH".
		lData[CERSAI_BFH_FH] =leftPad("FH",CERSAI_BFH_FIXED_FIELD_SIZES[CERSAI_BFH_FH] ," ",CERSAI_BFH_FH,"CERSAI_BFH_FH");
	    	// 2 - FCTR - CONSTANT. Value is "FCTR". - MANDATORY
	    	// 2 - FCTR stands for Factoring Registration. It's a constant and is "FCTR".
		lData[CERSAI_BFH_FCTR] =leftPad("FCTR",CERSAI_BFH_FIXED_FIELD_SIZES[CERSAI_BFH_FCTR] ," ",CERSAI_BFH_FCTR,"CERSAI_BFH_FCTR");
	    	// 3 - NO_OF_RECORDS - NUMBER(6) - MANDATORY
	    	// 3 - This indicates number of factoring records in the batch file. Maximum 1000 records are allowed per batch file
		lData[CERSAI_BFH_NO_OF_RECORDS] =leftPad(1+"",CERSAI_BFH_FIXED_FIELD_SIZES[CERSAI_BFH_NO_OF_RECORDS] ," ",CERSAI_BFH_NO_OF_RECORDS,"CERSAI_BFH_NO_OF_RECORDS");
	    	// 4 - DATE_OF_FILE_UPLOAD - DATE (DD-MM-YYYY) - MANDATORY
	    	// 4 - This indicates the date of file upload.This date should not be future date.
		lData[CERSAI_BFH_DATE_OF_FILE_UPLOAD] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, CommonUtilities.getCurrentDate()),CERSAI_BFH_FIXED_FIELD_SIZES[CERSAI_BFH_DATE_OF_FILE_UPLOAD] ," ",CERSAI_BFH_DATE_OF_FILE_UPLOAD,"CERSAI_BFH_DATE_OF_FILE_UPLOAD");
		//
		formatData(lData, CERSAI_BFH_RECORDSIZE, lReturnValue, baseFileHeaderSkipList());
		//
    	return lReturnValue.toString();
    }
    
    //
    private static String[] getCersaiBaseFileHeaderText(){
    	String[] lHeader = new String[CERSAI_BFH_RECORDSIZE];
    	lHeader[CERSAI_BFH_FH] = "FILE_HEADER";
    	lHeader[CERSAI_BFH_FCTR] = "FACTORINGREGISTRATION";
    	lHeader[CERSAI_BFH_NO_OF_RECORDS] = "NO_OF_RECORDS";
    	lHeader[CERSAI_BFH_DATE_OF_FILE_UPLOAD] = "DATE_OF_FILE_UPLOAD";
    	return lHeader;
    }

    
    //CERSAI RECORD HEADER
    private static int CERSAI_RH_RH = 0;
    private static int CERSAI_RH_RECORD_NO = 1;
    private static int CERSAI_RH_ENCUMBRANCE_STATUS = 2;
    private static int CERSAI_RH_RECEIVABLE_OWNER_TYPE = 3;
    private static int CERSAI_RH_RECEIVABLE_OWNER_COUNT = 4;
    private static int CERSAI_RH_ASSIGNOR_TYPE = 5;
    private static int CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS = 6;
    private static int CERSAI_RH_DEBTOR_TYPE = 7;
    private static int CERSAI_RH_NO_OF_DEBTOR_PARTNERS = 8;
    private static int CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES = 9;
    private static int CERSAI_RH_FACTORING_TYPE = 10;
    private static int CERSAI_RH_INVOICE_COUNT = 11;
    private static int CERSAI_RH_FACTORING_DATE = 12;
    private static int CERSAI_RH_TOKEN_FIELD = 13;
    private static int CERSAI_RH_RECORDSIZE = 14;
    private static int[] CERSAI_RH_FIXED_FIELD_SIZES = getCersaiRecordHeaderFixedFieldSizes();
    //
    private static int[] getCersaiRecordHeaderFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_RH_RECORDSIZE];
    	lSizes[CERSAI_RH_RH] = 2;
    	lSizes[CERSAI_RH_RECORD_NO] = 6;
    	lSizes[CERSAI_RH_ENCUMBRANCE_STATUS] = 1;
    	lSizes[CERSAI_RH_RECEIVABLE_OWNER_TYPE] = 1;
    	lSizes[CERSAI_RH_RECEIVABLE_OWNER_COUNT] = 3;
    	lSizes[CERSAI_RH_ASSIGNOR_TYPE] = 3;
    	lSizes[CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS] = 3;
    	lSizes[CERSAI_RH_DEBTOR_TYPE] = 3;
    	lSizes[CERSAI_RH_NO_OF_DEBTOR_PARTNERS] = 3;
    	lSizes[CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES] = 3;
    	lSizes[CERSAI_RH_FACTORING_TYPE] = 4;
    	lSizes[CERSAI_RH_INVOICE_COUNT] = 3;
    	lSizes[CERSAI_RH_FACTORING_DATE] = 10;
    	lSizes[CERSAI_RH_TOKEN_FIELD] = 100;
    	return lSizes;
    }
    //
    private static String[] getCersaiRecordHeaderText(){
    	String[] lHeader = new String[CERSAI_RH_RECORDSIZE];
    	lHeader[CERSAI_RH_RH] = "RECORD_HEADER";
    	lHeader[CERSAI_RH_RECORD_NO] = "RECORD_NO";
    	lHeader[CERSAI_RH_ENCUMBRANCE_STATUS] = "ENCUMBRANCE_STATUS";
    	lHeader[CERSAI_RH_RECEIVABLE_OWNER_TYPE] = "RECEIVABLE_OWNER_TYPE";
    	lHeader[CERSAI_RH_RECEIVABLE_OWNER_COUNT] = "RECEIVABLE_OWNER_COUNT";
    	lHeader[CERSAI_RH_ASSIGNOR_TYPE] = "ASSIGNOR_TYPE";
    	lHeader[CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS] = "NO_OF_ASSIGNOR_PARTNERS";
    	lHeader[CERSAI_RH_DEBTOR_TYPE] = "DEBTOR_TYPE";
    	lHeader[CERSAI_RH_NO_OF_DEBTOR_PARTNERS] = "NO_OF_DEBTOR_PARTNERS";
    	lHeader[CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES] = "NO_OF_AGREEMENT_SIGNATORIES";
    	lHeader[CERSAI_RH_FACTORING_TYPE] = "FACTORING_TYPE";
    	lHeader[CERSAI_RH_INVOICE_COUNT] = "INVOICE_COUNT";
    	lHeader[CERSAI_RH_FACTORING_DATE] = "FACTORING_DATE";
    	lHeader[CERSAI_RH_TOKEN_FIELD] = "TOKEN_FIELD";
    	return lHeader;
    }
    
    public static List<Long> recordHeaderSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_RH_FACTORING_DATE));
		return lSkipList;
	}
    //
    private String getCersaiRecordHeader(CersaiFileDataWrapper pCFDataWrapper, int pRecordNumber,int pRecordCount, int pInvoiceCount) {
    	StringBuilder lReturnValue = new StringBuilder();
    	
    	String[] lData = new String[CERSAI_RH_RECORDSIZE];
    	// 1 - RH - CONSTANT. Value is "RH". - MANDATORY
    	// 1 - RH stands for Record Header.  It’s a constant and is "RH".
		lData[CERSAI_RH_RH] =leftPad("RH",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_RH] ," ",CERSAI_RH_RH,"CERSAI_RH_RH");
	    	// 2 - RECORD_NO - NUMBER(6) - MANDATORY
	    	// 2 - This is serial number starting from 1 to 1000. A batch can have a maximum of 1000 records
		lData[CERSAI_RH_RECORD_NO] =leftPad((pRecordCount+1)+"",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_RECORD_NO] ," ",CERSAI_RH_RECORD_NO,"CERSAI_RH_RECORD_NO");
	    	// 3 - ENCUMBRANCE_STATUS - CONSTANT.(Values 'Y, 'N') - MANDATORY
	    	// 3 - This is the indicator of whether the receivables are free from any encumbrance or not. "Y" indicates Yes, "N" indicates No.
		lData[CERSAI_RH_ENCUMBRANCE_STATUS] =leftPad("N",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_ENCUMBRANCE_STATUS] ," ", CERSAI_RH_ENCUMBRANCE_STATUS, "CERSAI_RH_ENCUMBRANCE_STATUS" );//TODO: a mortgage or other claim on property or asse,CERSAI_RH_ENCUMBRANCE_STATUS,"CERSAI_RH_ENCUMBRANCE_STATUS");
	    	// 4 - RECEIVABLE_OWNER_TYPE - CONSTANT.(Values 'S', 'C','J') - MANDATORY(IF ENCUMBRANCE_STATUS is N)
	    	// 4 - This indicates the type of Receivable owner. "S" indicates Single Bank, "C" stands for Consortium, "J" stands for "Joint Charge". Blank if ENCUMBRANCE_STATUS is "Y".
		lData[CERSAI_RH_RECEIVABLE_OWNER_TYPE] =leftPad("S",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_RECEIVABLE_OWNER_TYPE] ," ",CERSAI_RH_RECEIVABLE_OWNER_TYPE,"CERSAI_RH_RECEIVABLE_OWNER_TYPE");
	    	// 5 - RECEIVABLE_OWNER_COUNT - NUMBER(3) - MANDATORY(IF RECEIVABLE_OWNER_TYPE  IS "J" 
	    	// 5 - This indicates the number of Receivable owners. This needs to be defined if RECEIVABLE_OWNER_TYPE is "J", otherwise blank.
		lData[CERSAI_RH_RECEIVABLE_OWNER_COUNT] =leftPad(pCFDataWrapper.getPartnerCount(pRecordNumber,CersaiEntityType.Recivable_Owner),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_RECEIVABLE_OWNER_COUNT] ," ",CERSAI_RH_RECEIVABLE_OWNER_COUNT,"CERSAI_RH_RECEIVABLE_OWNER_COUNT");
	    	// 6 - ASSIGNOR_TYPE - CONSTANT. (Values are IND, COM, HUF, LLP, PAF, PRF, TRS) - MANDATORY
	    	// 6 - This indicates the type of assignor. (Values are IND, COM, HUF, COS, LLP, PAF, PRF, TRS)
		lData[CERSAI_RH_ASSIGNOR_TYPE] =leftPad(pCFDataWrapper.getCersaiEntityType(pRecordNumber,CersaiEntityType.Assignor),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_ASSIGNOR_TYPE]," ",CERSAI_RH_ASSIGNOR_TYPE,"CERSAI_RH_ASSIGNOR_TYPE");
		if(hasPartner(pCFDataWrapper.getCDBean(pRecordNumber, CersaiEntityType.Assignor).getConstitution())) {
			// 7 - NO_OF_ASSIGNOR_PARTNERS - NUMBER(3) - MANDATORY(IF ASSIGNOR_TYPE  IS "LLP" or "PAF" 
	    	// 7 - This indicates the number of partners for ASSIGNOR_TYPE "LLP" and "PAF", otherwise blank.
			lData[CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS] =leftPad(pCFDataWrapper.getPartnerCount(pRecordNumber,CersaiEntityType.Assignor),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS] ," ",CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS,"CERSAI_RH_NO_OF_ASSIGNOR_PARTNERS");
		}
	    	// 8 - DEBTOR_TYPE - CONSTANT. (Values are IND, COM, HUF, LLP, PAF, PRF, TRS) - MANDATORY
	    	// 8 - This indicates the type of debtor (Values are IND, COM, HUF, COS, LLP, PAF, PRF, TRS)
		lData[CERSAI_RH_DEBTOR_TYPE] =leftPad(pCFDataWrapper.getCersaiEntityType(pRecordNumber,CersaiEntityType.Debtor),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_DEBTOR_TYPE] ," ",CERSAI_RH_DEBTOR_TYPE,"CERSAI_RH_DEBTOR_TYPE");
		if(hasPartner(pCFDataWrapper.getCDBean(pRecordNumber, CersaiEntityType.Debtor).getConstitution())) {
			// 9 - NO_OF_DEBTOR_PARTNERS - NUMBER(3) - MANDATORY(IF DEBTOR_TYPE  IS "LLP" or "PAF" 
	    	// 9 - This indicates the number of partners for DEBTOR_TYPE "LLP" and "PAF", otherwise blank.
			lData[CERSAI_RH_NO_OF_DEBTOR_PARTNERS] =leftPad(pCFDataWrapper.getPartnerCount(pRecordNumber,CersaiEntityType.Debtor),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_NO_OF_DEBTOR_PARTNERS] ," ",CERSAI_RH_NO_OF_DEBTOR_PARTNERS,"CERSAI_RH_NO_OF_DEBTOR_PARTNERS");
		}
	    	// 10 - NO_OF_AGREEMENT_SIGNATORIES - NUMBER(3) - MANDATORY
	    	// 10 - This indicates the number of agreement signatories
		lData[CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES] =leftPad(pCFDataWrapper.getDebtorAgreementSignatories(pRecordNumber),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES] ," ",CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES,"CERSAI_RH_NO_OF_AGREEMENT_SIGNATORIES");
	    	// 11 - FACTORING_TYPE - CONSTANT. Value is "NFTB". - MANDATORY
	    	// 11 - This indicates where the factoring type is "Whole Turn-over Basis" or "Non Whole Turn-over Basis" 
		lData[CERSAI_RH_FACTORING_TYPE] =leftPad("NFTB",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_FACTORING_TYPE] ," ",CERSAI_RH_FACTORING_TYPE,"CERSAI_RH_FACTORING_TYPE");
	    	// 12 - INVOICE_COUNT - NUMBER(3) - MANDATORY
	    	// 12 - This indicates the number of invoices.
		lData[CERSAI_RH_INVOICE_COUNT] =leftPad(pInvoiceCount+"",CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_INVOICE_COUNT] ," ",CERSAI_RH_INVOICE_COUNT,"CERSAI_RH_INVOICE_COUNT");
	    	// 13 - FACTORING_DATE - DATE (DD-MM-YYYY) - MANDATORY
	    	// 13 - This indicates the date on which factoring transcation was registered within entity
		lData[CERSAI_RH_FACTORING_DATE] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pCFDataWrapper.getFactorDate()),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_FACTORING_DATE] ," ",CERSAI_RH_FACTORING_DATE,"CERSAI_RH_FACTORING_DATE");
	    	// 14 - TOKEN_FIELD - VARCHAR(100) - OPTIONAL
	    	// 14 - This is used to uniquely identify the individual transaction record
		lData[CERSAI_RH_TOKEN_FIELD] =leftPad(pCFDataWrapper.getInstrument(pRecordNumber).getId().toString(),CERSAI_RH_FIXED_FIELD_SIZES[CERSAI_RH_TOKEN_FIELD] ," ",CERSAI_RH_TOKEN_FIELD,"CERSAI_RH_TOKEN_FIELD");
		//
		formatData(lData, CERSAI_RH_RECORDSIZE, lReturnValue, recordHeaderSkipList());
		//
		return lReturnValue.toString();
    }

    //CERSAI RECEIVABLE OWNER 
    private static int CERSAI_RECO_RECEIVABLE_OWNER = 0;
    private static int CERSAI_RECO_SERIAL_NO = 1;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE = 2;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER   = 3;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME     = 4;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME       = 5;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE           = 6;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_TALUKA            = 7;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_CITY              = 8;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT          = 9;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_STATE             = 10;
    private static int CERSAI_RECO_RECEIVABLE_OWNER_PINCODE           = 11;
    private static int CERSAI_RECO_RECORDSIZE = 12;
    private static int[] CERSAI_RECO_FIXED_FIELD_SIZES = getCersaiReceivableOwnerFixedFieldSizes();

    //TODO: TO ADD HEADER HERE AND UTILIZE IN RESOURCE
	//
    private static int[] getCersaiReceivableOwnerFixedFieldSizes(){
		int[] lSizes = new int[CERSAI_RECO_RECORDSIZE];
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER] = 4;
		lSizes[CERSAI_RECO_SERIAL_NO] = 3;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE] = 5;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_TALUKA] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_CITY] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT] = 100;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_STATE] = 2;
		lSizes[CERSAI_RECO_RECEIVABLE_OWNER_PINCODE] = 6;
    	return lSizes;
    }
	//
    private static String[] getCersaiReceivableOwnerHeaderText(){
		String[] lHeader = new String[CERSAI_RECO_RECORDSIZE];
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER] = "RECEIVABLE_OWNER";
		lHeader[CERSAI_RECO_SERIAL_NO] = "SERIAL_NO";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE] = "RECEIVABLE_OWNER_ENTITY_CODE";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER] = "RECEIVABLE_OWNER_BUILDING_NUMBER";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME] = "RECEIVABLE_OWNER_BUILDING_NAME";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME] = "RECEIVABLE_OWNER_STREET_NAME";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE] = "RECEIVABLE_OWNER_VILLAGE";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_TALUKA] = "RECEIVABLE_OWNER_TALUKA";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_CITY] = "RECEIVABLE_OWNER_CITY";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT] = "RECEIVABLE_OWNER_DISTRICT";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_STATE] = "RECEIVABLE_OWNER_STATE";
		lHeader[CERSAI_RECO_RECEIVABLE_OWNER_PINCODE] = "RECEIVABLE_OWNER_PINCODE";
    	return lHeader;
    }
    //
    public static List<Long> receivableOwnerSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
		return lSkipList;
	}
    //
    private String getCersaiReceivableOwner(CompanyDetailBean pCompanyDetailBean, String pCersaiCode, int pRecordNo,int pRecodCount){
		StringBuilder lReturnValue = new StringBuilder();
		String[] lData = new String[CERSAI_RECO_RECORDSIZE];
		// 1 - RECEIVABLE_OWNER - CONSTANT. Value is "RECO". - MANDATORY
		// 1 - RECO stands for Receivable Owner.
		lData[CERSAI_RECO_RECEIVABLE_OWNER] =leftPad("RECO",CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER] ," ",CERSAI_RECO_RECEIVABLE_OWNER,"CERSAI_RECO_RECEIVABLE_OWNER");
		// 2 - SERIAL_NO - NUMBER(3) - MANDATORY
		// 2 - This is the serial number of Receivable Owner.
		lData[CERSAI_RECO_SERIAL_NO] =leftPad((pRecodCount+1+""),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_SERIAL_NO] ," ",CERSAI_RECO_SERIAL_NO,"CERSAI_RECO_SERIAL_NO");
		// 3 - RECEIVABLE_OWNER_ENTITY_CODE - VARCHAR(5) - MANDATORY
		// 3 - This is the owner of receivable instiutution code assigned by CERSAI. 
		lData[CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE] =leftPad(StringUtils.substring(pCersaiCode,0, 5),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE] ," ",CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE,"CERSAI_RECO_RECEIVABLE_OWNER_ENTITY_CODE");
		// 4 - RECEIVABLE_OWNER_BUILDING_NUMBER - VARCHAR(100) - OPTIONAL
		// 4 - This is a receivable owner details - BUILDING NUMBER. 
		lData[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER] =formatString(leftPad(addNumberToString(pCompanyDetailBean.getCorLine1()),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER] ," ",CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER,"CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NUMBER"),ADDRESS_ALLOWED_FORMAT);
		// 5 - RECEIVABLE_OWNER_BUILDING_NAME - VARCHAR(100) - OPTIONAL
		// 5 - This is a receivable owner details - BUILDING NAME.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME] =formatString(leftPad("",CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME] ," ",CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME,"CERSAI_RECO_RECEIVABLE_OWNER_BUILDING_NAME"),ADDRESS_ALLOWED_FORMAT);
		// 6 - RECEIVABLE_OWNER_STREET_NAME - VARCHAR(100) - OPTIONAL
		// 6 - This is a receivable owner details - STREET NAME.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME] =formatString(leftPad(pCompanyDetailBean.getCorLine2()+" "+pCompanyDetailBean.getCorLine3(),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME] ," ",CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME,"CERSAI_RECO_RECEIVABLE_OWNER_STREET_NAME"),ADDRESS_ALLOWED_FORMAT);
		// 7 - RECEIVABLE_OWNER_VILLAGE - VARCHAR(100) - OPTIONAL
		// 7 - This is a receivable owner details - VILLAGE.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE] =leftPad("",CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE] ," ",CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE,"CERSAI_RECO_RECEIVABLE_OWNER_VILLAGE");
		// 8 - RECEIVABLE_OWNER_TALUKA - VARCHAR(100) - OPTIONAL
		// 8 - This is a receivable owner details - TALUKA.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_TALUKA] =leftPad("",CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_TALUKA] ," ",CERSAI_RECO_RECEIVABLE_OWNER_TALUKA,"CERSAI_RECO_RECEIVABLE_OWNER_TALUKA");
		// 9 - RECEIVABLE_OWNER_CITY - VARCHAR(100) - OPTIONAL
		// 9 - This is a receivable owner details - CITY.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_CITY] =formatString(leftPad(pCompanyDetailBean.getCorCity(),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_CITY] ," ",CERSAI_RECO_RECEIVABLE_OWNER_CITY,"CERSAI_RECO_RECEIVABLE_OWNER_CITY"),DISTRICT_CITY_ALLOWED_FORMAT);
		// 10 - RECEIVABLE_OWNER_DISTRICT - VARCHAR(100) - MANDATORY
		// 10 - This is a receivable owner details - DISTRICT.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT] =formatString(leftPad(pCompanyDetailBean.getCorDistrict(),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT] ," ",CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT,"CERSAI_RECO_RECEIVABLE_OWNER_DISTRICT"),DISTRICT_CITY_ALLOWED_FORMAT);
		// 11 - RECEIVABLE_OWNER_STATE - NUMBER(2) - MANDATORY
		// 11 - This is a receivable owner details - STATE.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_STATE] =leftPad(cersaiStateMap.get(pCompanyDetailBean.getCorState()),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_STATE] ," ",CERSAI_RECO_RECEIVABLE_OWNER_STATE,"CERSAI_RECO_RECEIVABLE_OWNER_STATE");
		// 12 - RECEIVABLE_OWNER_PINCODE - NUMBER(6) - MANDATORY
		// 12 - This is a receivable owner details - PIN CODE.
		lData[CERSAI_RECO_RECEIVABLE_OWNER_PINCODE] =leftPad(pCompanyDetailBean.getCorZipCode(),CERSAI_RECO_FIXED_FIELD_SIZES[CERSAI_RECO_RECEIVABLE_OWNER_PINCODE] ," ",CERSAI_RECO_RECEIVABLE_OWNER_PINCODE,"CERSAI_RECO_RECEIVABLE_OWNER_PINCODE");
		//
		formatData(lData, CERSAI_RECO_RECORDSIZE, lReturnValue, receivableOwnerSkipList());
		//
    	return lReturnValue.toString();
    }
    
    //Assignor
    private static int CERSAI_ANSR_ASSIGNOR = 0;
    private static int CERSAI_ANSR_ASSIGNOR_BSR_CODE = 1;
    private static int CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME = 2;
    private static int CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN = 3;
    private static int CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG = 4;
    private static int CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME = 5;
    private static int CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN = 6;
    private static int CERSAI_ANSR_ASSIGNOR_COM_CIN = 7;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE = 8;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME = 9;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME = 10;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME = 11;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN = 12;
    private static int CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB = 13;
    private static int CERSAI_ANSR_ASSIGNOR_TRUST_TYPE = 14;
    private static int CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO = 15;
    private static int CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER = 16;
    private static int CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER = 17;
    private static int CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER = 18;
    private static int CERSAI_ANSR_ASSIGNOR_BUILDING_NAME = 19;
    private static int CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER = 20;
    private static int CERSAI_ANSR_ASSIGNOR_VILLAGE = 21;
    private static int CERSAI_ANSR_ASSIGNOR_TOWN = 22;
    private static int CERSAI_ANSR_ASSIGNOR_TALUKA = 23;
    private static int CERSAI_ANSR_ASSIGNOR_CITY = 24;
    private static int CERSAI_ANSR_ASSIGNOR_DISTRICT = 25;
    private static int CERSAI_ANSR_ASSIGNOR_STATE = 26;
    private static int CERSAI_ANSR_ASSIGNOR_PIN_CODE = 27;
    private static int CERSAI_ANSR_ASSIGNOR_EMAIL_ID = 28;
    private static int CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER = 29;
    private static int CERSAI_ANSR_RECORDSIZE = 30;
    private static int[] CERSAI_ANSR_FIXED_FIELD_SIZES = getCersaiAssignorFixedFieldSizes();
    //
    private static int[] getCersaiAssignorFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_ANSR_RECORDSIZE];
    	lSizes[CERSAI_ANSR_ASSIGNOR] = 4;
    	lSizes[CERSAI_ANSR_ASSIGNOR_BSR_CODE] = 50;
    	lSizes[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME] = 200;
    	lSizes[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN] = 10;
    	lSizes[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG] = 7;
    	lSizes[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] = 200;
    	lSizes[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] = 10;
    	lSizes[CERSAI_ANSR_ASSIGNOR_COM_CIN] = 21;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE] = 4;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME] = 200;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME] = 50;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN] = 10;
    	lSizes[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB] = 10;
    	lSizes[CERSAI_ANSR_ASSIGNOR_TRUST_TYPE] = 2;
    	lSizes[CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_BUILDING_NAME] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_VILLAGE] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_TOWN] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_TALUKA] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_CITY] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_DISTRICT] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_STATE] = 2;
    	lSizes[CERSAI_ANSR_ASSIGNOR_PIN_CODE] = 6;
    	lSizes[CERSAI_ANSR_ASSIGNOR_EMAIL_ID] = 100;
    	lSizes[CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER] = 10;
    	return lSizes;
    }
    //
    private static String[] getCersaiAssignorHeaderText(){
    	String[] lHeader = new String[CERSAI_ANSR_RECORDSIZE];
    	lHeader[CERSAI_ANSR_ASSIGNOR] = "ASSIGNOR";
    	lHeader[CERSAI_ANSR_ASSIGNOR_BSR_CODE] = "ASSIGNOR_BSR_CODE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME] = "ASSIGNOR_PROP_FIRM_NAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN] = "ASSIGNOR_PROP_FIRM_PAN";
    	lHeader[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG] = "ASSIGNOR_PROP_FIRM_COM_IND_FLAG";
    	lHeader[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] = "ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] = "ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN";
    	lHeader[CERSAI_ANSR_ASSIGNOR_COM_CIN] = "ASSIGNOR_COM_CIN";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN";
    	lHeader[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB] = "ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB";
    	lHeader[CERSAI_ANSR_ASSIGNOR_TRUST_TYPE] = "ASSIGNOR_TRUST_TYPE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO] = "ASSIGNOR_TRUST_REG_NO";
    	lHeader[CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER] = "ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER";
    	lHeader[CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER] = "ASSIGNOR_PLOT_NUMBER";
    	lHeader[CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER] = "ASSIGNOR_BUILDING_NUMBER";
    	lHeader[CERSAI_ANSR_ASSIGNOR_BUILDING_NAME] = "ASSIGNOR_BUILDING_NAME";
    	lHeader[CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER] = "ASSIGNOR_STREET_NAME_NUMBER";
    	lHeader[CERSAI_ANSR_ASSIGNOR_VILLAGE] = "ASSIGNOR_VILLAGE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_TOWN] = "ASSIGNOR_TOWN";
    	lHeader[CERSAI_ANSR_ASSIGNOR_TALUKA] = "ASSIGNOR_TALUKA";
    	lHeader[CERSAI_ANSR_ASSIGNOR_CITY] = "ASSIGNOR_CITY";
    	lHeader[CERSAI_ANSR_ASSIGNOR_DISTRICT] = "ASSIGNOR_DISTRICT";
    	lHeader[CERSAI_ANSR_ASSIGNOR_STATE] = "ASSIGNOR_STATE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_PIN_CODE] = "ASSIGNOR_PIN_CODE";
    	lHeader[CERSAI_ANSR_ASSIGNOR_EMAIL_ID] = "ASSIGNOR_EMAIL_ID";
    	lHeader[CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER] = "ASSIGNOR_TELEPHONE_NUMBER";
    	return lHeader;
    }
    //
    public static List<Long> assignorSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_ANSR_ASSIGNOR_EMAIL_ID));
    	lSkipList.add(Long.valueOf(CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG));
		lSkipList.add(Long.valueOf(CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE));
		return lSkipList;
	}
    //
    private String getCersaiAssignor(CompanyDetailBean pCompanyDetailBean, List<CompanyContactBean> pPartners){
    	StringBuilder lReturnValue = new StringBuilder();
    	String[] lData = new String[CERSAI_ANSR_RECORDSIZE];
    	// 1 - ASSIGNOR - CONSTANT. Value is "ASNR". - MANDATORY
    	// 1 - ASNR stands for Assignor. It’s a constant and is "ASNR".
	lData[CERSAI_ANSR_ASSIGNOR] =leftPad("ASNR",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR] ," ",CERSAI_ANSR_ASSIGNOR,"CERSAI_ANSR_ASSIGNOR");
    	// 2 - ASSIGNOR_BSR_CODE - VARCHAR2(50) - OPTIONAL
    	// 2 - This field contains the BSR code of the assignor
	lData[CERSAI_ANSR_ASSIGNOR_BSR_CODE] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_BSR_CODE] ," ",CERSAI_ANSR_ASSIGNOR_BSR_CODE,"CERSAI_ANSR_ASSIGNOR_BSR_CODE");
    	// 3 - ASSIGNOR_PROP_FIRM_NAME - VARCHAR(200) - MANDATORY(If ASSIGNOR_TYPE is "PRF")
    	// 3 - This is the name of the Assignor Propreiter Firm, otherwise blank.
	lData[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME] =rightPad(("PRF".equals(getMappedConsitution(pCompanyDetailBean.getConstitution()))?pCompanyDetailBean.getCompanyName():""),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME] ," ",CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME,"CERSAI_ANSR_ASSIGNOR_PROP_FIRM_NAME");
    	// 4 - ASSIGNOR_PROP_FIRM_PAN - VARCHAR(10) - MANDATORY(If ASSIGNOR_TYPE is "PRF")
    	// 4 - This is assignor PAN if assignor category is "PRF" (Propreiter Firm) otherwise blank.
	lData[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN] =leftPad(("PRF".equals(getMappedConsitution(pCompanyDetailBean.getConstitution()))?pCompanyDetailBean.getPan():""),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN] ," ",CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN,"CERSAI_ANSR_ASSIGNOR_PROP_FIRM_PAN");
    	// 5 - ASSIGNOR_PROP_FIRM_COM_IND_FLAG - CONSTANT.(Values are "COM_PRF","IND_PRF") - MANDATORY(If ASSIGNOR_TYPE is "PRF")
    	// 5 - This is assignor Company or Individual flag if assignor category is "PRF" (Proprieter ship firm) otherwise blank.
	lData[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG] =leftPad(("PRF".equals(getMappedConsitution(pCompanyDetailBean.getConstitution()))?"IND_PRF":""),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG] ," ",CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG,"CERSAI_ANSR_ASSIGNOR_PROP_FIRM_COM_IND_FLAG");
    	// 6 - ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME - VARCHAR(200) - MANDATORY (IF ASSIGNOR TYPE IS "COM", "LLP","PAF","TRS" or  ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "COM_PRF")
    	// 6 - This field contains the name of Company, Partnership Firm, Trust, name of the company of Proprieter Firm, otherwise blank
    	Set<String> lCoTypes = new HashSet<String>(); 
    	lCoTypes.add("COM");
    	lCoTypes.add("LLP");
    	lCoTypes.add("PAF");
    	lCoTypes.add("TRS");
    	lCoTypes.add("PRF");
    	String lMappedConsitution = getMappedConsitution(pCompanyDetailBean.getConstitution());
	lData[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] =rightPad((lCoTypes.contains(lMappedConsitution)?pCompanyDetailBean.getCompanyName():""),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] ," ",CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME,"CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_NAME");
    	// 7 - ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN - VARCHAR(10) - MANDATORY (IF ASSIGNOR TYPE IS "COM", "LLP","PAF","TRS" or  ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "COM_PRF")
    	// 7 - This field contains the PAN of Company, Partnership Firm, Trust, PAN of the company of Proprieter Firm, otherwise blank
	lData[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] =leftPad((lCoTypes.contains(lMappedConsitution)?pCompanyDetailBean.getPan():""),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] ," ",CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN,"CERSAI_ANSR_ASSIGNOR_COM_LLP_PAF_PRF_COM_TRUST_PAN");
    	// 8 - ASSIGNOR_COM_CIN - VARCHAR(21) - MANDATORY(If ASSIGNOR_TYPE is "COM"  or  ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "COM_PRF")
    	// 8 - This is assignor CIN if borrower category is "COM" (Company) otherwise blank
	lData[CERSAI_ANSR_ASSIGNOR_COM_CIN] =leftPad(pCompanyDetailBean.getCinNo(),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_COM_CIN] ," ",CERSAI_ANSR_ASSIGNOR_COM_CIN,"CERSAI_ANSR_ASSIGNOR_COM_CIN");
    	boolean lShow = lCoTypes.contains(lMappedConsitution) || lMappedConsitution.contains("HUF")|| lMappedConsitution.contains("IND");
    	// 9 - ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE - CONSTANT.(Values are "Mr.","Mrs.","Ms.","Dr.") - MANDATORY(If ASSIGNOR_TYPE is "IND", "HUF","TRS" or ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "IND_PRF"
    	// 9 - This field contains title of the Individual, HUF, Trust and the tilte of the individual of Propreiter Firm, otherwise blank
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE] =leftPad(lShow?pCompanyDetailBean.getCorSalutation():"",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_TITLE");
    	// 10 - ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME - VARCHAR(200) - MANDATORY(If ASSIGNOR_TYPE is "IND", "HUF","TRS" or ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "IND_PRF"
    	// 10 - This field contains name of the Individual, HUF, Trust and the name of the individual of Propreiter Firm, otherwise blank
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME] =rightPad(lShow?pCompanyDetailBean.getCorFirstName():"",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_NAME");
    	// 11 - ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME - VARCHAR(50) - OPTIONAL
    	// 11 - This field contains surname of the Individual, HUF, Trust and the surname of the individual of Propreiter Firm
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME] =rightPad(lShow?pCompanyDetailBean.getCorLastName():"",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_SURNAME");
    	// 12 - ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME - VARCHAR(100) - OPTIONAL
    	// 12 - This field contains father's name of the Individual, HUF, Trust and the father's name of the individual of Propreiter Firm
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] =rightPad(lShow?pCompanyDetailBean.getCorMiddleName():"",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_FATHER_NAME");
    	// 13 - ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN - VARCHAR(10) - MANDATORY(If ASSIGNOR_TYPE is "IND", "HUF","TRS" or ASSIGNOR_PROP_FIRM_COM_IND_FLAG is "IND_PRF"
    	// 13 - This field contains PAN of the Individual, HUF, Trust and the PAN of the individual of Propreiter Firm
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN] =leftPad(lShow?pCompanyDetailBean.getPan():"",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_PAN");
    	// 14 - ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB - DATE (DD-MM-YYYY) - OPTIONAL
    	// 14 - This field contains DOB of the Individual, HUF, Trust and the DOB of the individual of Propreiter Firm
	//ASSIGNOR PARTNER DETAILS
	Date lDateOfBirth = null;
	if(pPartners!=null && pPartners.size() > 0){
		for(CompanyContactBean lPartnerContactBean : pPartners){
			lDateOfBirth = lPartnerContactBean.getCersaiDOB();
			break;
		}
	}
	
	lData[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB] =leftPad(lDateOfBirth!=null?FormatHelper.getDisplay(DATE_FORMAT, lDateOfBirth):"" ,CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB] ," ",CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB,"CERSAI_ANSR_ASSIGNOR_IND_HUF_TRS_PRF_IND_DOB");
    	// 15 - ASSIGNOR_TRUST_TYPE - CONSTANT.(Values are "01","02") - MANDATORY(IS ASSIGNOR_TYPE is "TRS")
    	// 15 - This field contains the type of Trust. "01" signifies PUBLIC and "02" signifies PRIVATE
	lData[CERSAI_ANSR_ASSIGNOR_TRUST_TYPE] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_TRUST_TYPE] ," ",CERSAI_ANSR_ASSIGNOR_TRUST_TYPE,"CERSAI_ANSR_ASSIGNOR_TRUST_TYPE");
    	// 16 - ASSIGNOR_TRUST_REG_NO - VARCHAR(100) - MANDATORY(IS ASSIGNOR_TRUST_TYPE is "01")
    	// 16 - This field contains the registration number of the trust
	lData[CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO] ," ",CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO,"CERSAI_ANSR_ASSIGNOR_TRUST_REG_NO");
    	// 17 - ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER - VARCHAR(100) - OPTIONAL
    	// 17 - This is assignor address details, SHOP/PLOT/HOUSE NUMBER.
	lData[CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER] ," ",CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER,"CERSAI_ANSR_ASSIGNOR_SHOP_FLAT_HOUSE_NUMBER");
    	// 18 - ASSIGNOR_PLOT_NUMBER - VARCHAR(100) - OPTIONAL
    	// 18 - This is assignor address details, PLOT NUMBER.
	lData[CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER] ," ",CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER,"CERSAI_ANSR_ASSIGNOR_PLOT_NUMBER");
    	// 19 - ASSIGNOR_BUILDING_NUMBER - VARCHAR(100) - OPTIONAL
    	// 19 - This is assignor address details, BUILDING NUMBER.
	lData[CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER] ," ",CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER,"CERSAI_ANSR_ASSIGNOR_BUILDING_NUMBER");
    	// 20 - ASSIGNOR_BUILDING_NAME - VARCHAR(100) - OPTIONAL
    	// 20 - This is assignor address details, BUILDING NAME.
	lData[CERSAI_ANSR_ASSIGNOR_BUILDING_NAME] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_BUILDING_NAME] ," ",CERSAI_ANSR_ASSIGNOR_BUILDING_NAME,"CERSAI_ANSR_ASSIGNOR_BUILDING_NAME");
    	// 21 - ASSIGNOR_STREET_NAME_NUMBER - VARCHAR(100) - OPTIONAL
    	// 21 - This is assignor address details, STREET NAME/NUMBER.
	lData[CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER] =formatString(leftPad(addNumberToString(pCompanyDetailBean.getCorLine1()+" "+pCompanyDetailBean.getCorLine2()+" "+pCompanyDetailBean.getCorLine3()),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER] ," ",CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER,"CERSAI_ANSR_ASSIGNOR_STREET_NAME_NUMBER"),ADDRESS_ALLOWED_FORMAT);
    	// 22 - ASSIGNOR_VILLAGE - VARCHAR(100) - OPTIONAL
    	// 22 - This is assignor address details, VILLAGE.
	lData[CERSAI_ANSR_ASSIGNOR_VILLAGE] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_VILLAGE] ," ",CERSAI_ANSR_ASSIGNOR_VILLAGE,"CERSAI_ANSR_ASSIGNOR_VILLAGE");
    	// 23 - ASSIGNOR_TOWN - VARCHAR(100) - OPTIONAL
    	// 23 - This is assignor address details, TOWN.
	lData[CERSAI_ANSR_ASSIGNOR_TOWN] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_TOWN] ," ",CERSAI_ANSR_ASSIGNOR_TOWN,"CERSAI_ANSR_ASSIGNOR_TOWN");
    	// 24 - ASSIGNOR_TALUKA - VARCHAR(100) - OPTIONAL
    	// 24 - This is assignor address details, TALUKA.
	lData[CERSAI_ANSR_ASSIGNOR_TALUKA] =leftPad("",CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_TALUKA] ," ",CERSAI_ANSR_ASSIGNOR_TALUKA,"CERSAI_ANSR_ASSIGNOR_TALUKA");
    	// 25 - ASSIGNOR_CITY - VARCHAR(100) - OPTIONAL
    	// 25 - This is assignor address details, CITY.
	lData[CERSAI_ANSR_ASSIGNOR_CITY] =formatString(leftPad(pCompanyDetailBean.getCorCity(),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_CITY] ," ",CERSAI_ANSR_ASSIGNOR_CITY,"CERSAI_ANSR_ASSIGNOR_CITY"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 26 - ASSIGNOR_DISTRICT - VARCHAR(100) - MANDATORY
    	// 26 - This is assignor address details, DISTRICT.
	lData[CERSAI_ANSR_ASSIGNOR_DISTRICT] =formatString(leftPad(pCompanyDetailBean.getCorDistrict(),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_DISTRICT] ," ",CERSAI_ANSR_ASSIGNOR_DISTRICT,"CERSAI_ANSR_ASSIGNOR_DISTRICT"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 27 - ASSIGNOR_STATE - NUMBER(2) - MANDATORY
    	// 27 - This is assignor address details, STATE.
	lData[CERSAI_ANSR_ASSIGNOR_STATE] =leftPad(cersaiStateMap.get(pCompanyDetailBean.getCorState()),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_STATE] ," ",CERSAI_ANSR_ASSIGNOR_STATE,"CERSAI_ANSR_ASSIGNOR_STATE");
    	// 28 - ASSIGNOR_PIN_CODE - NUMBER(6) - MANDATORY
    	// 28 - This is assignor address details, PINCODE.
	lData[CERSAI_ANSR_ASSIGNOR_PIN_CODE] =leftPad(pCompanyDetailBean.getCorZipCode(),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_PIN_CODE] ," ",CERSAI_ANSR_ASSIGNOR_PIN_CODE,"CERSAI_ANSR_ASSIGNOR_PIN_CODE");
    	// 29 - ASSIGNOR_EMAIL_ID - VARCHAR(100) - MANDATORY
    	// 29 - This is assignor address details, EMAIL ID.
	lData[CERSAI_ANSR_ASSIGNOR_EMAIL_ID] =leftPad(pCompanyDetailBean.getCorEmail(),CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_EMAIL_ID] ," ",CERSAI_ANSR_ASSIGNOR_EMAIL_ID,"CERSAI_ANSR_ASSIGNOR_EMAIL_ID");
    	// 30 - ASSIGNOR_TELEPHONE_NUMBER - NUMBER(10) - MANDATORY
    	// 30 - This is assignor address details, TELEPHONE NUMBER
	String lMobileNo = pCompanyDetailBean.getCorMobile().substring(pCompanyDetailBean.getCorMobile().length()-10);
	lData[CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER] =leftPad(lMobileNo,CERSAI_ANSR_FIXED_FIELD_SIZES[CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER] ," ",CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER,"CERSAI_ANSR_ASSIGNOR_TELEPHONE_NUMBER");
	//
	formatData(lData, CERSAI_ANSR_RECORDSIZE, lReturnValue, assignorSkipList());
	//
	return lReturnValue.toString();
    }

    //CERSAI ASSIGNOR PARTNER
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER = 0;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO = 1;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE = 2;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_NAME = 3;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME = 4;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME = 5;
    private static int CERSAI_ASRP_ASSIGNOR_PARTNER_DOB = 6;
    private static int CERSAI_ASRP_RECORDSIZE = 7;
    private static int[] CERSAI_ASRP_FIXED_FIELD_SIZES = getCersaiAssignorPartnerFixedFieldSizes();

	//
    private static int[] getCersaiAssignorPartnerFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_ASRP_RECORDSIZE];
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER] = 4;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO] = 3;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE] = 4;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_NAME] = 200;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME] = 200;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME] = 200;
    	lSizes[CERSAI_ASRP_ASSIGNOR_PARTNER_DOB] = 10;

    	return lSizes;
    }
	//
    private static String[] getCersaiAssignorPartnerHeaderText(){
    	String[] lHeader = new String[CERSAI_ASRP_RECORDSIZE];
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER] = "ASSIGNOR_PARTNER";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO] = "ASSIGNOR_PARTNER_SERIAL_NO";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE] = "ASSIGNOR_PARTNER_TITLE";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_NAME] = "ASSIGNOR_PARTNER_NAME";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME] = "ASSIGNOR_PARTNER_SURNAME";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME] = "ASSIGNOR_PARTNER_FATHER_NAME";
    	lHeader[CERSAI_ASRP_ASSIGNOR_PARTNER_DOB] = "ASSIGNOR_PARTNER_DOB";
    	return lHeader;
    }
    //
    public static List<Long> assignorpartnerSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_ASRP_ASSIGNOR_PARTNER_DOB));
    	lSkipList.add(Long.valueOf(CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE));
		return lSkipList;
	}
    //
    private String getCersaiAssignorPartner(CompanyContactBean pPartnerContactBean, int pSerialNo){
    	StringBuilder lReturnValue = new StringBuilder();
    	String[] lData = new String[CERSAI_ASRP_RECORDSIZE];
    	// 1 - ASSIGNOR_PARTNER - CONSTANT. Value is "ASRP". - MANDATORY
    	// 1 - ASRP stands for Assignor Partner. 
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER] =leftPad("ASRP",CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER,"CERSAI_ASRP_ASSIGNOR_PARTNER");
    	// 2 - ASSIGNOR_PARTNER_SERIAL_NO - NUMBER(3) - MANDATORY
    	// 2 - This is the serial number of  Partner.
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO] =leftPad(pSerialNo+"",CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO,"CERSAI_ASRP_ASSIGNOR_PARTNER_SERIAL_NO");
    	// 3 - ASSIGNOR_PARTNER_TITLE - CONSTANT.(Values are "Mr.","Mrs.","Ms.","Dr.") - MANDATORY
    	// 3 - This field contains title of the Partner title
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE] =leftPad(pPartnerContactBean.getSalutation(),CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE,"CERSAI_ASRP_ASSIGNOR_PARTNER_TITLE");
    	// 4 - ASSIGNOR_PARTNER_NAME - VARCHAR(200) - MANDATORY
    	// 4 - This field contains title of the Partner name
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_NAME] =leftPad(pPartnerContactBean.getFirstName(),CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_NAME] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_NAME,"CERSAI_ASRP_ASSIGNOR_PARTNER_NAME");
    	// 5 - ASSIGNOR_PARTNER_SURNAME - VARCHAR(200) - OPTIONAL
    	// 5 - This field contains title of the Partner Surname
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME] =leftPad(pPartnerContactBean.getLastName(),CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME,"CERSAI_ASRP_ASSIGNOR_PARTNER_SURNAME");
    	// 6 - ASSIGNOR_PARTNER_FATHER_NAME - VARCHAR(200) - OPTIONAL
    	// 6 - This field contains title of the Partner Father's/Husband's name
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME] =leftPad(pPartnerContactBean.getMiddleName(),CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME,"CERSAI_ASRP_ASSIGNOR_PARTNER_FATHER_NAME");
    	// 7 - ASSIGNOR_PARTNER_DOB - DATE (DD-MM-YYYY) - OPTIONAL
    	// 7 - This field contains title of the Partner DOB
	lData[CERSAI_ASRP_ASSIGNOR_PARTNER_DOB] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pPartnerContactBean.getCersaiDOB()),CERSAI_ASRP_FIXED_FIELD_SIZES[CERSAI_ASRP_ASSIGNOR_PARTNER_DOB] ," ",CERSAI_ASRP_ASSIGNOR_PARTNER_DOB,"CERSAI_ASRP_ASSIGNOR_PARTNER_DOB");
	//
	formatData(lData, CERSAI_ASRP_RECORDSIZE, lReturnValue, assignorpartnerSkipList());
	//
	return lReturnValue.toString();
    }

    //FACTOR
    private static int CERSAI_FACT_FACTOR = 0;
    private static int CERSAI_FACT_FACTOR_CIN_PAN = 1;
    private static int CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER = 2;
    private static int CERSAI_FACT_FACTOR_PLOT_NUMBER = 3;
    private static int CERSAI_FACT_FACTOR_BUILDING_NUMBER = 4;
    private static int CERSAI_FACT_FACTOR_BUILDING_NAME = 5;
    private static int CERSAI_FACT_FACTOR_STREET_NAME_NUMBER = 6;
    private static int CERSAI_FACT_FACTOR_VILLAGE = 7;
    private static int CERSAI_FACT_FACTOR_TOWN = 8;
    private static int CERSAI_FACT_FACTOR_TALUKA = 9;
    private static int CERSAI_FACT_FACTOR_CITY = 10;
    private static int CERSAI_FACT_FACTOR_DISTRICT = 11;
    private static int CERSAI_FACT_FACTOR_STATE = 12;
    private static int CERSAI_FACT_FACTOR_PIN_CODE = 13;
    private static int CERSAI_FACT_FACTOR_EMAIL_ID = 14;
    private static int CERSAI_FACT_FACTOR_TELEPHONE_NUMBER = 15;
    private static int CERSAI_FACT_RECORDSIZE = 16;
    private static int[] CERSAI_FACT_FIXED_FIELD_SIZES = getCersaiFactorFixedFieldSizes();

	//
    private static int[] getCersaiFactorFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_FACT_RECORDSIZE];
    	lSizes[CERSAI_FACT_FACTOR] = 4;
    	lSizes[CERSAI_FACT_FACTOR_CIN_PAN] = 21;
    	lSizes[CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER] = 100;
    	lSizes[CERSAI_FACT_FACTOR_PLOT_NUMBER] = 100;
    	lSizes[CERSAI_FACT_FACTOR_BUILDING_NUMBER] = 100;
    	lSizes[CERSAI_FACT_FACTOR_BUILDING_NAME] = 100;
    	lSizes[CERSAI_FACT_FACTOR_STREET_NAME_NUMBER] = 100;
    	lSizes[CERSAI_FACT_FACTOR_VILLAGE] = 100;
    	lSizes[CERSAI_FACT_FACTOR_TOWN] = 100;
    	lSizes[CERSAI_FACT_FACTOR_TALUKA] = 100;
    	lSizes[CERSAI_FACT_FACTOR_CITY] = 100;
    	lSizes[CERSAI_FACT_FACTOR_DISTRICT] = 100;
    	lSizes[CERSAI_FACT_FACTOR_STATE] = 2;
    	lSizes[CERSAI_FACT_FACTOR_PIN_CODE] = 6;
    	lSizes[CERSAI_FACT_FACTOR_EMAIL_ID] = 100;
    	lSizes[CERSAI_FACT_FACTOR_TELEPHONE_NUMBER] = 10;
    	return lSizes;
    }
	//
    private static String[] getCersaiFactorHeaderText(){
    	String[] lHeader = new String[CERSAI_FACT_RECORDSIZE];
    	lHeader[CERSAI_FACT_FACTOR] = "FACTOR";
    	lHeader[CERSAI_FACT_FACTOR_CIN_PAN] = "FACTOR_CIN_PAN";
    	lHeader[CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER] = "FACTOR_SHOP_FLAT_HOUSE_NUMBER";
    	lHeader[CERSAI_FACT_FACTOR_PLOT_NUMBER] = "FACTOR_PLOT_NUMBER";
    	lHeader[CERSAI_FACT_FACTOR_BUILDING_NUMBER] = "FACTOR_BUILDING_NUMBER";
    	lHeader[CERSAI_FACT_FACTOR_BUILDING_NAME] = "FACTOR_BUILDING_NAME";
    	lHeader[CERSAI_FACT_FACTOR_STREET_NAME_NUMBER] = "FACTOR_STREET_NAME_NUMBER";
    	lHeader[CERSAI_FACT_FACTOR_VILLAGE] = "FACTOR_VILLAGE";
    	lHeader[CERSAI_FACT_FACTOR_TOWN] = "FACTOR_TOWN";
    	lHeader[CERSAI_FACT_FACTOR_TALUKA] = "FACTOR_TALUKA";
    	lHeader[CERSAI_FACT_FACTOR_CITY] = "FACTOR_CITY";
    	lHeader[CERSAI_FACT_FACTOR_DISTRICT] = "FACTOR_DISTRICT";
    	lHeader[CERSAI_FACT_FACTOR_STATE] = "FACTOR_STATE";
    	lHeader[CERSAI_FACT_FACTOR_PIN_CODE] = "FACTOR_PIN_CODE";
    	lHeader[CERSAI_FACT_FACTOR_EMAIL_ID] = "FACTOR_EMAIL_ID";
    	lHeader[CERSAI_FACT_FACTOR_TELEPHONE_NUMBER] = "FACTOR_TELEPHONE_NUMBER";
    	return lHeader;
    }
    //
    public static List<Long> factorSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_FACT_FACTOR_EMAIL_ID));
		return lSkipList;
	}
    //
    private String getCersaiFactor(CompanyDetailBean pCompanyDetailBean){
    	StringBuilder lReturnValue = new StringBuilder();
    	String[] lData = new String[CERSAI_FACT_RECORDSIZE];
    	// 1 - FACTOR - CONSTANT. Value is "FACT". - MANDATORY
    	// 1 - FACT stands for Factor 
	lData[CERSAI_FACT_FACTOR] =leftPad("FACT",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR] ," ",CERSAI_FACT_FACTOR,"CERSAI_FACT_FACTOR");
    	// 2 - FACTOR_CIN_PAN - VARCHAR(21) - MANDATORY
    	// 2 - This field contains CIN/PAN of the factor
	String lValue = (pCompanyDetailBean.getCinNo()!=null?pCompanyDetailBean.getCinNo():pCompanyDetailBean.getPan());
	lData[CERSAI_FACT_FACTOR_CIN_PAN] =leftPad(lValue,CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_CIN_PAN] ," ",CERSAI_FACT_FACTOR_CIN_PAN,"CERSAI_FACT_FACTOR_CIN_PAN");
    	// 3 - FACTOR_SHOP_FLAT_HOUSE_NUMBER - VARCHAR(100) - OPTIONAL
    	// 3 - This is factor address details, SHOP/PLOT/HOUSE NUMBER.
	lData[CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER] ," ",CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER,"CERSAI_FACT_FACTOR_SHOP_FLAT_HOUSE_NUMBER");
    	// 4 - FACTOR_PLOT_NUMBER - VARCHAR(100) - OPTIONAL
    	// 4 - This is factor address details, PLOT NUMBER.
	lData[CERSAI_FACT_FACTOR_PLOT_NUMBER] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_PLOT_NUMBER] ," ",CERSAI_FACT_FACTOR_PLOT_NUMBER,"CERSAI_FACT_FACTOR_PLOT_NUMBER");
    	// 5 - FACTOR_BUILDING_NUMBER - VARCHAR(100) - OPTIONAL
    	// 5 - This is factor address details, BUILDING NUMBER.
	lData[CERSAI_FACT_FACTOR_BUILDING_NUMBER] =formatString(leftPad(addNumberToString(pCompanyDetailBean.getCorLine1()),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_BUILDING_NUMBER] ," ",CERSAI_FACT_FACTOR_BUILDING_NUMBER,"CERSAI_FACT_FACTOR_BUILDING_NUMBER"),ADDRESS_ALLOWED_FORMAT);
    	// 6 - FACTOR_BUILDING_NAME - VARCHAR(100) - OPTIONAL
    	// 6 - This is factor address details, BUILDING NAME.
	lData[CERSAI_FACT_FACTOR_BUILDING_NAME] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_BUILDING_NAME] ," ",CERSAI_FACT_FACTOR_BUILDING_NAME,"CERSAI_FACT_FACTOR_BUILDING_NAME");
    	// 7 - FACTOR_STREET_NAME_NUMBER - VARCHAR(100) - OPTIONAL
    	// 7 - This is factor address details, STREET NAME/NUMBER.
	lData[CERSAI_FACT_FACTOR_STREET_NAME_NUMBER] =formatString(leftPad(addNumberToString(pCompanyDetailBean.getCorLine2()+" "+pCompanyDetailBean.getCorLine3()),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_STREET_NAME_NUMBER] ," ",CERSAI_FACT_FACTOR_STREET_NAME_NUMBER,"CERSAI_FACT_FACTOR_STREET_NAME_NUMBER"),ADDRESS_ALLOWED_FORMAT);
    	// 8 - FACTOR_VILLAGE - VARCHAR(100) - OPTIONAL
    	// 8 - This is factor address details, VILLAGE.
	lData[CERSAI_FACT_FACTOR_VILLAGE] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_VILLAGE] ," ",CERSAI_FACT_FACTOR_VILLAGE,"CERSAI_FACT_FACTOR_VILLAGE");
    	// 9 - FACTOR_TOWN - VARCHAR(100) - OPTIONAL
    	// 9 - This is factor address details, TOWN.
	lData[CERSAI_FACT_FACTOR_TOWN] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_TOWN] ," ",CERSAI_FACT_FACTOR_TOWN,"CERSAI_FACT_FACTOR_TOWN");
    	// 10 - FACTOR_TALUKA - VARCHAR(100) - OPTIONAL
    	// 10 - This is factor address details, TALUKA.
	lData[CERSAI_FACT_FACTOR_TALUKA] =leftPad("",CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_TALUKA] ," ",CERSAI_FACT_FACTOR_TALUKA,"CERSAI_FACT_FACTOR_TALUKA");
    	// 11 - FACTOR_CITY - VARCHAR(100) - OPTIONAL
    	// 11 - This is factor address details, CITY.
	lData[CERSAI_FACT_FACTOR_CITY] =formatString(leftPad(pCompanyDetailBean.getCorCity(),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_CITY] ," ",CERSAI_FACT_FACTOR_CITY,"CERSAI_FACT_FACTOR_CITY"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 12 - FACTOR_DISTRICT - VARCHAR(100) - MANDATORY
    	// 12 - This is factor address details, DISTRICT.
	lData[CERSAI_FACT_FACTOR_DISTRICT] =formatString(leftPad(pCompanyDetailBean.getCorDistrict(),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_DISTRICT] ," ",CERSAI_FACT_FACTOR_DISTRICT,"CERSAI_FACT_FACTOR_DISTRICT"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 13 - FACTOR_STATE - NUMBER(2) - MANDATORY
    	// 13 - This is factor address details, STATE.
	lData[CERSAI_FACT_FACTOR_STATE] =leftPad(cersaiStateMap.get(pCompanyDetailBean.getCorState()),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_STATE] ," ",CERSAI_FACT_FACTOR_STATE,"CERSAI_FACT_FACTOR_STATE");
    	// 14 - FACTOR_PIN_CODE - NUMBER(6) - MANDATORY
    	// 14 - This is factor address details, PINCODE.
	lData[CERSAI_FACT_FACTOR_PIN_CODE] =leftPad(pCompanyDetailBean.getCorZipCode(),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_PIN_CODE] ," ",CERSAI_FACT_FACTOR_PIN_CODE,"CERSAI_FACT_FACTOR_PIN_CODE");
    	// 15 - FACTOR_EMAIL_ID - VARCHAR(100) - MANDATORY
    	// 15 - This is factor address details, EMAIL ID.
	lData[CERSAI_FACT_FACTOR_EMAIL_ID] =leftPad(pCompanyDetailBean.getCorEmail(),CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_EMAIL_ID] ," ",CERSAI_FACT_FACTOR_EMAIL_ID,"CERSAI_FACT_FACTOR_EMAIL_ID");
    	// 16 - FACTOR_TELEPHONE_NUMBER - NUMBER(10) - MANDATORY
    	// 16 - This is factor address details, TELEPHONE NUMBER
	String lMobileNo = pCompanyDetailBean.getCorMobile().substring(pCompanyDetailBean.getCorMobile().length()-10);
	lData[CERSAI_FACT_FACTOR_TELEPHONE_NUMBER] =leftPad(lMobileNo,CERSAI_FACT_FIXED_FIELD_SIZES[CERSAI_FACT_FACTOR_TELEPHONE_NUMBER] ," ",CERSAI_FACT_FACTOR_TELEPHONE_NUMBER,"CERSAI_FACT_FACTOR_TELEPHONE_NUMBER");
	//
	formatData(lData, CERSAI_FACT_RECORDSIZE, lReturnValue, factorSkipList());
	//
	return lReturnValue.toString();
    }
    
    //Debtor
    private static int CERSAI_DBTR_DEBTOR = 0;
    private static int CERSAI_DBTR_DEBTOR_LIMIT = 1;
    private static int CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME = 2;
    private static int CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN = 3;
    private static int CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG = 4;
    private static int CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME = 5;
    private static int CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN = 6;
    private static int CERSAI_DBTR_DEBTOR_COM_CIN = 7;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE = 8;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME = 9;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME = 10;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME = 11;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN = 12;
    private static int CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB = 13;
    private static int CERSAI_DBTR_DEBTOR_TRUST_TYPE = 14;
    private static int CERSAI_DBTR_DEBTOR_TRUST_REG_NO = 15;
    private static int CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER = 16;
    private static int CERSAI_DBTR_DEBTOR_PLOT_NUMBER = 17;
    private static int CERSAI_DBTR_DEBTOR_BUILDING_NUMBER = 18;
    private static int CERSAI_DBTR_DEBTOR_BUILDING_NAME = 19;
    private static int CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER = 20;
    private static int CERSAI_DBTR_DEBTOR_VILLAGE = 21;
    private static int CERSAI_DBTR_DEBTOR_TOWN = 22;
    private static int CERSAI_DBTR_DEBTOR_TALUKA = 23;
    private static int CERSAI_DBTR_DEBTOR_CITY = 24;
    private static int CERSAI_DBTR_DEBTOR_DISTRICT = 25;
    private static int CERSAI_DBTR_DEBTOR_STATE = 26;
    private static int CERSAI_DBTR_DEBTOR_PIN_CODE = 27;
    private static int CERSAI_DBTR_DEBTOR_EMAIL_ID = 28;
    private static int CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER = 29;
    private static int CERSAI_DBTR_RECORDSIZE = 30;
    private static int[] CERSAI_DBTR_FIXED_FIELD_SIZES = getCersaiDebtorFixedFieldSizes();

    //
    private static int[] getCersaiDebtorFixedFieldSizes(){
    	int[] lSizes = new int[CERSAI_DBTR_RECORDSIZE];
    	lSizes[CERSAI_DBTR_DEBTOR] = 4;
    	lSizes[CERSAI_DBTR_DEBTOR_LIMIT] = 23;
    	lSizes[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] = 200;
    	lSizes[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] = 10;
    	lSizes[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] = 7;
    	lSizes[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] = 200;
    	lSizes[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] = 10;
    	lSizes[CERSAI_DBTR_DEBTOR_COM_CIN] = 21;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] = 4;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] = 10;
    	lSizes[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] = 10;
    	lSizes[CERSAI_DBTR_DEBTOR_TRUST_TYPE] = 2;
    	lSizes[CERSAI_DBTR_DEBTOR_TRUST_REG_NO] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_PLOT_NUMBER] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_BUILDING_NUMBER] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_BUILDING_NAME] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_VILLAGE] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_TOWN] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_TALUKA] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_CITY] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_DISTRICT] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_STATE] = 2;
    	lSizes[CERSAI_DBTR_DEBTOR_PIN_CODE] = 6;
    	lSizes[CERSAI_DBTR_DEBTOR_EMAIL_ID] = 100;
    	lSizes[CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER] = 10;
    	return lSizes;
    }
    //
    private static String[] getCersaiDebtorHeaderText(){
    	String[] lHeader = new String[CERSAI_DBTR_RECORDSIZE];
    	lHeader[CERSAI_DBTR_DEBTOR] = "DEBTOR";
    	lHeader[CERSAI_DBTR_DEBTOR_LIMIT] = "DEBTOR_LIMIT";
    	lHeader[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] = "DEBTOR_PROP_FIRM_NAME";
    	lHeader[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] = "DEBTOR_PROP_FIRM_PAN";
    	lHeader[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] = "DEBTOR_PROP_FIRM_COM_IND_FLAG";
    	lHeader[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] = "DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME";
    	lHeader[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] = "DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN";
    	lHeader[CERSAI_DBTR_DEBTOR_COM_CIN] = "DEBTOR_COM_CIN";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] = "DEBTOR_IND_HUF_TRS_PRF_IND_TITLE";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] = "DEBTOR_IND_HUF_TRS_PRF_IND_NAME";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] = "DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] = "DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] = "DEBTOR_IND_HUF_TRS_PRF_IND_PAN";
    	lHeader[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] = "DEBTOR_IND_HUF_TRS_PRF_IND_DOB";
    	lHeader[CERSAI_DBTR_DEBTOR_TRUST_TYPE] = "DEBTOR_TRUST_TYPE";
    	lHeader[CERSAI_DBTR_DEBTOR_TRUST_REG_NO] = "DEBTOR_TRUST_REG_NO";
    	lHeader[CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER] = "DEBTOR_SHOP_FLAT_HOUSE_NUMBER";
    	lHeader[CERSAI_DBTR_DEBTOR_PLOT_NUMBER] = "DEBTOR_PLOT_NUMBER";
    	lHeader[CERSAI_DBTR_DEBTOR_BUILDING_NUMBER] = "DEBTOR_BUILDING_NUMBER";
    	lHeader[CERSAI_DBTR_DEBTOR_BUILDING_NAME] = "DEBTOR_BUILDING_NAME";
    	lHeader[CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER] = "DEBTOR_STREET_NAME_NUMBER";
    	lHeader[CERSAI_DBTR_DEBTOR_VILLAGE] = "DEBTOR_VILLAGE";
    	lHeader[CERSAI_DBTR_DEBTOR_TOWN] = "DEBTOR_TOWN";
    	lHeader[CERSAI_DBTR_DEBTOR_TALUKA] = "DEBTOR_TALUKA";
    	lHeader[CERSAI_DBTR_DEBTOR_CITY] = "DEBTOR_CITY";
    	lHeader[CERSAI_DBTR_DEBTOR_DISTRICT] = "DEBTOR_DISTRICT";
    	lHeader[CERSAI_DBTR_DEBTOR_STATE] = "DEBTOR_STATE";
    	lHeader[CERSAI_DBTR_DEBTOR_PIN_CODE] = "DEBTOR_PIN_CODE";
    	lHeader[CERSAI_DBTR_DEBTOR_EMAIL_ID] = "DEBTOR_EMAIL_ID";
    	lHeader[CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER] = "DEBTOR_TELEPHONE_NUMBER";
    	return lHeader;
    }
    //
    public static List<Long> debtorSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_DBTR_DEBTOR_LIMIT));
    	lSkipList.add(Long.valueOf(CERSAI_DBTR_DEBTOR_EMAIL_ID));
		return lSkipList;
	}
    //
    private String getCersaiDebtor(CompanyDetailBean pCompanyDetailBean, Long pDebtorLimit){
    	StringBuilder lReturnValue = new StringBuilder();
    	String[] lData = new String[CERSAI_DBTR_RECORDSIZE];
    	// 1 - DEBTOR - CONSTANT. Value is "DBTR". - MANDATORY
    	// 1 - DBTR stands for Debtor. 
	lData[CERSAI_DBTR_DEBTOR] =leftPad("DBTR",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR] ," ",CERSAI_DBTR_DEBTOR,"CERSAI_DBTR_DEBTOR");
    	// 2 - DEBTOR_LIMIT - NUMBER(20,2) - MANDATORY
    	// 2 - This field contains the buyer/debtor limit
	lData[CERSAI_DBTR_DEBTOR_LIMIT] =leftPad((pDebtorLimit!=null?pDebtorLimit.toString():""),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_LIMIT] ," ",CERSAI_DBTR_DEBTOR_LIMIT,"CERSAI_DBTR_DEBTOR_LIMIT");
	if (pCompanyDetailBean.getConstitution().equals(AppConstants.RC_CONSTITUENTS_PROPRITORYSHIP)) {
		// 3 - DEBTOR_PROP_FIRM_NAME - VARCHAR(200) - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 3 - This is the name of the debtor Propreiter Firm, otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] =rightPad(formatString(pCompanyDetailBean.getCompanyName(),ADDRESS_ALLOWED_FORMAT),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME,"CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME");
    	// 4 - DEBTOR_PROP_FIRM_PAN - VARCHAR(10) - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 4 - This is debtor PAN if debtor category is "PRF" (Propreiter Firm) otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] =leftPad(pCompanyDetailBean.getPan(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN,"CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN");
    	// 5 - DEBTOR_PROP_FIRM_COM_IND_FLAG - CONSTANT.(Values are "PRF_COM","PRF_IND") - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 5 - This is debtor Company or Individual flag if debtor category is "PRF" (Proprieter ship firm) otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] =leftPad((pCompanyDetailBean.getConstitution()=="PROP"?"PRF_COM":""),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG,"CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG");
	}else {
		// 3 - DEBTOR_PROP_FIRM_NAME - VARCHAR(200) - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 3 - This is the name of the debtor Propreiter Firm, otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] =rightPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME,"CERSAI_DBTR_DEBTOR_PROP_FIRM_NAME");
    	// 4 - DEBTOR_PROP_FIRM_PAN - VARCHAR(10) - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 4 - This is debtor PAN if debtor category is "PRF" (Propreiter Firm) otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN,"CERSAI_DBTR_DEBTOR_PROP_FIRM_PAN");
    	// 5 - DEBTOR_PROP_FIRM_COM_IND_FLAG - CONSTANT.(Values are "PRF_COM","PRF_IND") - MANDATORY(If DEBTOR_TYPE is "PRF")
    	// 5 - This is debtor Company or Individual flag if debtor category is "PRF" (Proprieter ship firm) otherwise blank.
		lData[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG] ," ",CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG,"CERSAI_DBTR_DEBTOR_PROP_FIRM_COM_IND_FLAG");
	}
	if (AppConstants.RC_CONSTITUENTS_PUBLIC.equals(pCompanyDetailBean.getConstitution()) 
			|| AppConstants.RC_CONSTITUENTS_PRIVATE.equals(pCompanyDetailBean.getConstitution())
			|| AppConstants.RC_CONSTITUENTS_PARTNERSHIP.equals(pCompanyDetailBean.getConstitution()))	{
			// 6 - DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME - VARCHAR(200) - MANDATORY (IF DEBTOR TYPE IS "COM", "LLP","PAF","TRS" or  DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_COM"
			// 6 - This field contains the name of Company, Partnership Firm, Trust, name of the company of Proprieter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] =leftPad(formatString(pCompanyDetailBean.getCompanyName(),ADDRESS_ALLOWED_FORMAT),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] ," ",CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME,"CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME");
	    	// 7 - DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN - VARCHAR(10) - MANDATORY (IF DEBTOR TYPE IS "COM", "LLP","PAF","TRS" or  DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_COM"
	    	// 7 - This field contains the PAN of Company, Partnership Firm, Trust, PAN of the company of Proprieter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] =leftPad(pCompanyDetailBean.getPan(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] ," ",CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN,"CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN");
	    	// 8 - DEBTOR_COM_CIN - VARCHAR(21) - MANDATORY(If DEBTOR_TYPE is "COM")
	    	// 8 - This is debtor CIN if borrower category is "COM" (Company) otherwise blank
		String lValue = (pCompanyDetailBean.getCinNo()!=null?pCompanyDetailBean.getCinNo():pCompanyDetailBean.getPan());
		lData[CERSAI_DBTR_DEBTOR_COM_CIN] =leftPad(lValue,CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_CIN] ," ",CERSAI_DBTR_DEBTOR_COM_CIN,"CERSAI_DBTR_DEBTOR_COM_CIN");
	}else {
			// 6 - DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME - VARCHAR(200) - MANDATORY (IF DEBTOR TYPE IS "COM", "LLP","PAF","TRS" or  DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_COM"
			// 6 - This field contains the name of Company, Partnership Firm, Trust, name of the company of Proprieter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME] ," ",CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME,"CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_NAME");
	    	// 7 - DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN - VARCHAR(10) - MANDATORY (IF DEBTOR TYPE IS "COM", "LLP","PAF","TRS" or  DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_COM"
	    	// 7 - This field contains the PAN of Company, Partnership Firm, Trust, PAN of the company of Proprieter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN] ," ",CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN,"CERSAI_DBTR_DEBTOR_COM_LLP_PAF_PRF_COM_TRUST_PAN");
	    	// 8 - DEBTOR_COM_CIN - VARCHAR(21) - MANDATORY(If DEBTOR_TYPE is "COM")
	    	// 8 - This is debtor CIN if borrower category is "COM" (Company) otherwise blank
		lData[CERSAI_DBTR_DEBTOR_COM_CIN] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_COM_CIN] ," ",CERSAI_DBTR_DEBTOR_COM_CIN,"CERSAI_DBTR_DEBTOR_COM_CIN");
	}
	if (AppConstants.RC_CONSTITUENTS_HUF.equals(pCompanyDetailBean.getConstitution()) 
			|| AppConstants.RC_CONSTITUENTS_TRUST.equals(pCompanyDetailBean.getConstitution()))	{
			// 9 - DEBTOR_IND_HUF_TRS_PRF_IND_TITLE - CONSTANT.(Values are "Mr.","Mrs.","Ms.","Dr.") - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 9 - This field contains title of the Individual, HUF, Trust and the tilte of the individual of Propreiter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] =leftPad(pCompanyDetailBean.getCorSalutation(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE");
	    	// 10 - DEBTOR_IND_HUF_TRS_PRF_IND_NAME - VARCHAR(100) - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 10 - This field contains name of the Individual, HUF, Trust and the name of the individual of Propreiter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] =leftPad(pCompanyDetailBean.getCorFirstName(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME");
	    	// 11 - DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME - VARCHAR(100) - OPTIONAL
	    	// 11 - This field contains surname of the Individual, HUF, Trust and the surname of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] =leftPad(pCompanyDetailBean.getCorLastName(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME");
	    	// 12 - DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME - VARCHAR(100) - OPTIONAL
	    	// 12 - This field contains father's name of the Individual, HUF, Trust and the father's name of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] =leftPad(pCompanyDetailBean.getCorMiddleName(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME");
	    	// 13 - DEBTOR_IND_HUF_TRS_PRF_IND_PAN - VARCHAR(10) - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 13 - This field contains PAN of the Individual, HUF, Trust and the PAN of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] =leftPad(pCompanyDetailBean.getPan(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN");
	    	// 14 - DEBTOR_IND_HUF_TRS_PRF_IND_DOB - DATE (DD-MM-YYYY) - OPTIONAL
	    	// 14 - This field contains DOB of the Individual, HUF, Trust and the DOB of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB");
	}else {
			// 9 - DEBTOR_IND_HUF_TRS_PRF_IND_TITLE - CONSTANT.(Values are "Mr.","Mrs.","Ms.","Dr.") - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 9 - This field contains title of the Individual, HUF, Trust and the tilte of the individual of Propreiter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_TITLE");
	    	// 10 - DEBTOR_IND_HUF_TRS_PRF_IND_NAME - VARCHAR(100) - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 10 - This field contains name of the Individual, HUF, Trust and the name of the individual of Propreiter Firm, otherwise blank
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_NAME");
	    	// 11 - DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME - VARCHAR(100) - OPTIONAL
	    	// 11 - This field contains surname of the Individual, HUF, Trust and the surname of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_SURNAME");
	    	// 12 - DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME - VARCHAR(100) - OPTIONAL
	    	// 12 - This field contains father's name of the Individual, HUF, Trust and the father's name of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_FATHER_NAME");
	    	// 13 - DEBTOR_IND_HUF_TRS_PRF_IND_PAN - VARCHAR(10) - MANDATORY(If DEBTOR_TYPE is "IND", "HUF","TRS" or DEBTOR_PROP_FIRM_COM_IND_FLAG is "PRF_IND"
	    	// 13 - This field contains PAN of the Individual, HUF, Trust and the PAN of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_PAN");
	    	// 14 - DEBTOR_IND_HUF_TRS_PRF_IND_DOB - DATE (DD-MM-YYYY) - OPTIONAL
	    	// 14 - This field contains DOB of the Individual, HUF, Trust and the DOB of the individual of Propreiter Firm
		lData[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB] ," ",CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB,"CERSAI_DBTR_DEBTOR_IND_HUF_TRS_PRF_IND_DOB");
	}
    	// 15 - DEBTOR_TRUST_TYPE - CONSTANT.(Values are "01","02") - MANDATORY(IS DEBTOR_TYPE is "TRS")
    	// 15 - This field contains the type of Trust.
	lData[CERSAI_DBTR_DEBTOR_TRUST_TYPE] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_TRUST_TYPE] ," ",CERSAI_DBTR_DEBTOR_TRUST_TYPE,"CERSAI_DBTR_DEBTOR_TRUST_TYPE");
    	// 16 - DEBTOR_TRUST_REG_NO - VARCHAR(100) - MANDATORY(IS DEBTOR_TRUST_TYPE is "01")
    	// 16 - This field contains the registration number of the trust
	lData[CERSAI_DBTR_DEBTOR_TRUST_REG_NO] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_TRUST_REG_NO] ," ",CERSAI_DBTR_DEBTOR_TRUST_REG_NO,"CERSAI_DBTR_DEBTOR_TRUST_REG_NO");
    	// 17 - DEBTOR_SHOP_FLAT_HOUSE_NUMBER - VARCHAR(100) - OPTIONAL
    	// 17 - This is debtor address details, SHOP/PLOT/HOUSE NUMBER.
	lData[CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER] ," ",CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER,"CERSAI_DBTR_DEBTOR_SHOP_FLAT_HOUSE_NUMBER");
    	// 18 - DEBTOR_PLOT_NUMBER - VARCHAR(100) - OPTIONAL
    	// 18 - This is debtor address details, PLOT NUMBER.
	lData[CERSAI_DBTR_DEBTOR_PLOT_NUMBER] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PLOT_NUMBER] ," ",CERSAI_DBTR_DEBTOR_PLOT_NUMBER,"CERSAI_DBTR_DEBTOR_PLOT_NUMBER");
    	// 19 - DEBTOR_BUILDING_NUMBER - VARCHAR(100) - OPTIONAL
    	// 19 - This is debtor address details, BUILDING NUMBER.
	lData[CERSAI_DBTR_DEBTOR_BUILDING_NUMBER] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_BUILDING_NUMBER] ," ",CERSAI_DBTR_DEBTOR_BUILDING_NUMBER,"CERSAI_DBTR_DEBTOR_BUILDING_NUMBER");
    	// 20 - DEBTOR_BUILDING_NAME - VARCHAR(100) - OPTIONAL
    	// 20 - This is debtor address details, BUILDING NAME.
	lData[CERSAI_DBTR_DEBTOR_BUILDING_NAME] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_BUILDING_NAME] ," ",CERSAI_DBTR_DEBTOR_BUILDING_NAME,"CERSAI_DBTR_DEBTOR_BUILDING_NAME");
    	// 21 - DEBTOR_STREET_NAME_NUMBER - VARCHAR(100) - OPTIONAL
    	// 21 - This is debtor address details, STREET NAME/NUMBER.
	lData[CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER] =formatString(leftPad("0 "+pCompanyDetailBean.getCorLine1()+" "+pCompanyDetailBean.getCorLine2()+" "+pCompanyDetailBean.getCorLine3(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER] ," ",CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER,"CERSAI_DBTR_DEBTOR_STREET_NAME_NUMBER"),ADDRESS_ALLOWED_FORMAT);
    	// 22 - DEBTOR_VILLAGE - VARCHAR(100) - OPTIONAL
    	// 22 - This is debtor address details, VILLAGE.
	lData[CERSAI_DBTR_DEBTOR_VILLAGE] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_VILLAGE] ," ",CERSAI_DBTR_DEBTOR_VILLAGE,"CERSAI_DBTR_DEBTOR_VILLAGE");
    	// 23 - DEBTOR_TOWN - VARCHAR(100) - OPTIONAL
    	// 23 - This is debtor address details, TOWN.
	lData[CERSAI_DBTR_DEBTOR_TOWN] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_TOWN] ," ",CERSAI_DBTR_DEBTOR_TOWN,"CERSAI_DBTR_DEBTOR_TOWN");
    	// 24 - DEBTOR_TALUKA - VARCHAR(100) - OPTIONAL
    	// 24 - This is debtor address details, TALUKA.
	lData[CERSAI_DBTR_DEBTOR_TALUKA] =leftPad("",CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_TALUKA] ," ",CERSAI_DBTR_DEBTOR_TALUKA,"CERSAI_DBTR_DEBTOR_TALUKA");
    	// 25 - DEBTOR_CITY - VARCHAR(100) - OPTIONAL
    	// 25 - This is debtor address details, CITY.
	lData[CERSAI_DBTR_DEBTOR_CITY] =formatString(leftPad(pCompanyDetailBean.getCorCity(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_CITY] ," ",CERSAI_DBTR_DEBTOR_CITY,"CERSAI_DBTR_DEBTOR_CITY"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 26 - DEBTOR_DISTRICT - VARCHAR(100) - MANDATORY
    	// 26 - This is debtor address details, DISTRICT.
	lData[CERSAI_DBTR_DEBTOR_DISTRICT] =formatString(leftPad(pCompanyDetailBean.getCorDistrict(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_DISTRICT] ," ",CERSAI_DBTR_DEBTOR_DISTRICT,"CERSAI_DBTR_DEBTOR_DISTRICT"),DISTRICT_CITY_ALLOWED_FORMAT);
    	// 27 - DEBTOR_STATE - NUMBER(2) - MANDATORY
    	// 27 - This is debtor address details, STATE.
	lData[CERSAI_DBTR_DEBTOR_STATE] =leftPad(cersaiStateMap.get(pCompanyDetailBean.getCorState()),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_STATE] ," ",CERSAI_DBTR_DEBTOR_STATE,"CERSAI_DBTR_DEBTOR_STATE");
    	// 28 - DEBTOR_PIN_CODE - NUMBER(6) - MANDATORY
    	// 28 - This is debtor address details, PINCODE.
	lData[CERSAI_DBTR_DEBTOR_PIN_CODE] =leftPad(pCompanyDetailBean.getCorZipCode(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_PIN_CODE] ," ",CERSAI_DBTR_DEBTOR_PIN_CODE,"CERSAI_DBTR_DEBTOR_PIN_CODE");
    	// 29 - DEBTOR_EMAIL_ID - VARCHAR(100) - MANDATORY
    	// 29 - This is debtor address details, EMAIL ID.
	lData[CERSAI_DBTR_DEBTOR_EMAIL_ID] =leftPad(pCompanyDetailBean.getCorEmail(),CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_EMAIL_ID] ," ",CERSAI_DBTR_DEBTOR_EMAIL_ID,"CERSAI_DBTR_DEBTOR_EMAIL_ID");
    	// 30 - DEBTOR_TELEPHONE_NUMBER - NUMBER(10) - MANDATORY
    	// 30 - This is debtor address details, TELEPHONE NUMBER
	String lMobileNo = pCompanyDetailBean.getCorMobile().substring(pCompanyDetailBean.getCorMobile().length()-10);
	lData[CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER] =leftPad(lMobileNo,CERSAI_DBTR_FIXED_FIELD_SIZES[CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER] ," ",CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER,"CERSAI_DBTR_DEBTOR_TELEPHONE_NUMBER");
	//
	formatData(lData, CERSAI_DBTR_RECORDSIZE, lReturnValue, debtorSkipList());
	//
	return lReturnValue.toString();
    }
    
    //DEBTOR PARTNER
    private static int CERSAI_DBTP_DEBTOR_PARTNER = 0;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO = 1;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_TITLE = 2;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_NAME = 3;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_SURNAME = 4;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME = 5;
    private static int CERSAI_DBTP_DEBTOR_PARTNER_DOB = 6;
    private static int CERSAI_DBTP_RECORDSIZE = 7;
    private static int[] CERSAI_DBTP_FIXED_FIELD_SIZES = getCersaiDebtorPartnerFixedFieldSizes();
    
  	//
	private static int[] getCersaiDebtorPartnerFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_DBTP_RECORDSIZE];
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER] = 4;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO] = 3;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_TITLE] = 4;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_NAME] = 200;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_SURNAME] = 200;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME] = 200;
	  	lSizes[CERSAI_DBTP_DEBTOR_PARTNER_DOB] = 10;
	  	return lSizes;
	}
  	//
	private static String[] getCersaiDebtorPartnerHeaderText(){
		String[] lHeader = new String[CERSAI_DBTP_RECORDSIZE];
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER] = "DEBTOR_PARTNER";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO] = "DEBTOR_PARTNER_SERIAL_NO";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_TITLE] = "DEBTOR_PARTNER_TITLE";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_NAME] = "DEBTOR_PARTNER_NAME";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_SURNAME] = "DEBTOR_PARTNER_SURNAME";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME] = "DEBTOR_PARTNER_FATHER_NAME";
		lHeader[CERSAI_DBTP_DEBTOR_PARTNER_DOB] = "DEBTOR_PARTNER_DOB";
	  	return lHeader;
	}
	//
    public static List<Long> debtorPartnerSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_DBTP_DEBTOR_PARTNER_DOB));
		return lSkipList;
	}
    //
	private String getDebtorPartner(CompanyContactBean pPartnerContactBean, int pSerialNo){
	  	 StringBuilder lReturnValue = new StringBuilder();
	  	 String[] lData = new String[CERSAI_DBTP_RECORDSIZE];
		 // 1 - DEBTOR_PARTNER - CONSTANT. Value is "DBTP". - MANDATORY
		 // 1 - DBTP stands for Debtor Partner
		 lData[CERSAI_DBTP_DEBTOR_PARTNER] =leftPad("DBTP",CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER] ," ",CERSAI_DBTP_DEBTOR_PARTNER,"CERSAI_DBTP_DEBTOR_PARTNER");
		 // 2 - DEBTOR_PARTNER_SERIAL_NO - NUMBER(3) - MANDATORY
		 // 2 - This is the serial number of  Partner.
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO] =leftPad(pSerialNo+"",CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO] ," ",CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO,"CERSAI_DBTP_DEBTOR_PARTNER_SERIAL_NO");
		 // 3 - DEBTOR_PARTNER_TITLE - CONSTANT.(Values are "Mr.","Mrs.","Ms.","Dr.") - MANDATORY
		 // 3 - This field contains title of the Partner title
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_TITLE] =leftPad(pPartnerContactBean.getCersaiSalutation(),CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_TITLE] ," ",CERSAI_DBTP_DEBTOR_PARTNER_TITLE,"CERSAI_DBTP_DEBTOR_PARTNER_TITLE");
		 // 4 - DEBTOR_PARTNER_NAME - VARCHAR(200) - MANDATORY
		 // 4 - This field contains title of the Partner name
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_NAME] =leftPad(pPartnerContactBean.getCersaiFirstName(),CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_NAME] ," ",CERSAI_DBTP_DEBTOR_PARTNER_NAME,"CERSAI_DBTP_DEBTOR_PARTNER_NAME");
		 // 5 - DEBTOR_PARTNER_SURNAME - VARCHAR(200) - OPTIONAL
		 // 5 - This field contains title of the Partner Surname
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_SURNAME] =leftPad(pPartnerContactBean.getCersaiLastName(),CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_SURNAME] ," ",CERSAI_DBTP_DEBTOR_PARTNER_SURNAME,"CERSAI_DBTP_DEBTOR_PARTNER_SURNAME");
		 // 6 - DEBTOR_PARTNER_FATHER_NAME - VARCHAR(200) - OPTIONAL
		 // 6 - This field contains title of the Partner Father's/Husband's name
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME] =leftPad(pPartnerContactBean.getCersaiMiddleName(),CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME] ," ",CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME,"CERSAI_DBTP_DEBTOR_PARTNER_FATHER_NAME");
		 // 8 - DEBTOR_PARTNER_DOB - DATE (DD-MM-YYYY) - OPTIONAL
		 // 8 - This field contains title of the Partner DOB
		 lData[CERSAI_DBTP_DEBTOR_PARTNER_DOB] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pPartnerContactBean.getCersaiDOB()),CERSAI_DBTP_FIXED_FIELD_SIZES[CERSAI_DBTP_DEBTOR_PARTNER_DOB] ," ",CERSAI_DBTP_DEBTOR_PARTNER_DOB,"CERSAI_DBTP_DEBTOR_PARTNER_DOB");
		//
		formatData(lData, CERSAI_DBTP_RECORDSIZE, lReturnValue, debtorPartnerSkipList());
		//
		return lReturnValue.toString();
	}

	
	//AGREEMENT
	private static int CERSAI_AGMT_AGREEMENT  = 0;
	private static int CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION = 1;
	private static int CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT = 2;
	private static int CERSAI_AGMT_AGREEMENT_TALUKA = 3;
	private static int CERSAI_AGMT_AGREEMENT_DISTRICT = 4;
	private static int CERSAI_AGMT_AGREEMENT_STATE = 5;
	private static int CERSAI_AGMT_AGREEMENT_PIN_CODE = 6;
    private static int CERSAI_AGMT_RECORDSIZE = 7;
    private static int[] CERSAI_AGMT_FIXED_FIELD_SIZES = getCersaiAgreementFixedFieldSizes();
    
  	//
	private static int[] getCersaiAgreementFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_AGMT_RECORDSIZE];
	  	lSizes[CERSAI_AGMT_AGREEMENT ] = 4;
	  	lSizes[CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION] = 100;
	  	lSizes[CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT] = 10;
	  	lSizes[CERSAI_AGMT_AGREEMENT_TALUKA] = 100;
	  	lSizes[CERSAI_AGMT_AGREEMENT_DISTRICT] = 100;
	  	lSizes[CERSAI_AGMT_AGREEMENT_STATE] = 2;
	  	lSizes[CERSAI_AGMT_AGREEMENT_PIN_CODE] = 6;
	  	return lSizes;
	}
  	//
	private static String[] getCersaiAgreementHeaderText(){
		String[] lHeader = new String[CERSAI_AGMT_RECORDSIZE];
		lHeader[CERSAI_AGMT_AGREEMENT ] = "AGREEMENT ";
		lHeader[CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION] = "AGREEMENT_PLACE_OF_EXECUTION";
		lHeader[CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT] = "AGREEMENT_DATE_OF_DOCUMENT";
		lHeader[CERSAI_AGMT_AGREEMENT_TALUKA] = "AGREEMENT_TALUKA";
		lHeader[CERSAI_AGMT_AGREEMENT_DISTRICT] = "AGREEMENT_DISTRICT";
		lHeader[CERSAI_AGMT_AGREEMENT_STATE] = "AGREEMENT_STATE";
		lHeader[CERSAI_AGMT_AGREEMENT_PIN_CODE] = "AGREEMENT_PIN_CODE";
	  	return lHeader;
	}
	//
    public static List<Long> agreementSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT));
		return lSkipList;
	}
    //
	private String getAgreement(CersaiFileDataWrapper pCFDataWrapper, Date pFileDate){
		  CompanyDetailBean lCDBean = pCFDataWrapper.getCDBeanFiancier();
		  StringBuilder lReturnValue = new StringBuilder();
		  String[] lData = new String[CERSAI_AGMT_RECORDSIZE];
		  // 1 - AGREEMENT  - CONSTANT. Value is "AGMT". - MANDATORY
		  // 1 - AGMT stands for Agreement
		 lData[CERSAI_AGMT_AGREEMENT ] =leftPad("AGMT",CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT ] ," ",CERSAI_AGMT_AGREEMENT ,"CERSAI_AGMT_AGREEMENT ");
		  // 2 -  - VARCHAR(100) - MANDATORY
		  // 2 - This is the place of execution of agreement.
		 lData[CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION] =formatString(leftPad(lCDBean.getCorCity(),CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION] ," ",CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION,"CERSAI_AGMT_AGREEMENT_PLACE_OF_EXECUTION"),DISTRICT_CITY_ALLOWED_FORMAT);
		  // 3 - AGREEMENT_DATE_OF_DOCUMENT - DATE (DD-MM-YYYY) - MANDATORY
		  // 3 - This if the date of document signinig.
		 lData[CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pFileDate),CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT] ," ",CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT,"CERSAI_AGMT_AGREEMENT_DATE_OF_DOCUMENT");
		  // 4 - AGREEMENT_TALUKA - VARCHAR(100) - OPTIONAL
		  // 4 - This is the address details of agreement, TALUKA.
		 lData[CERSAI_AGMT_AGREEMENT_TALUKA] =leftPad("",CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_TALUKA] ," ",CERSAI_AGMT_AGREEMENT_TALUKA,"CERSAI_AGMT_AGREEMENT_TALUKA");
		  // 5 - AGREEMENT_DISTRICT - VARCHAR(100) - MANDATORY
		  // 5 - This is the address details of agreement, DISTRICT.
		 lData[CERSAI_AGMT_AGREEMENT_DISTRICT] =leftPad(lCDBean.getCorDistrict(),CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_DISTRICT] ," ",CERSAI_AGMT_AGREEMENT_DISTRICT,"CERSAI_AGMT_AGREEMENT_DISTRICT");
		  // 6 - AGREEMENT_STATE - NUMBER(2) - MANDATORY
		  // 6 - This is the address details of agreement, STATE.
		 lData[CERSAI_AGMT_AGREEMENT_STATE] =leftPad(cersaiStateMap.get(lCDBean.getCorState()),CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_STATE] ," ",CERSAI_AGMT_AGREEMENT_STATE,"CERSAI_AGMT_AGREEMENT_STATE");
		  // 7 - AGREEMENT_PIN_CODE - NUMBER(6) - MANDATORY
		  // 7 - This is the adress details of agreement, PIN CODE.
		 lData[CERSAI_AGMT_AGREEMENT_PIN_CODE] =leftPad(lCDBean.getCorZipCode(),CERSAI_AGMT_FIXED_FIELD_SIZES[CERSAI_AGMT_AGREEMENT_PIN_CODE] ," ",CERSAI_AGMT_AGREEMENT_PIN_CODE,"CERSAI_AGMT_AGREEMENT_PIN_CODE");
		//
		formatData(lData, CERSAI_AGMT_RECORDSIZE, lReturnValue, agreementSkipList());
		//
		return lReturnValue.toString();
	}

	//Agreement Signatories
	private static int CERSAI_SIGN_AGREEMENT_SIGNATORY = 0;
  	private static int CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO = 1;
  	private static int CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME = 2;
  	private static int CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION = 3;
  	private static int CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_SERIAL_NO = 4;
  	private static int CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_NAME = 5;
  	private static int CERSAI_SIGN_SUP_AGREEMENT_SIGNATOTY_DESIGNATION = 6;
  	private static int CERSAI_SIGN_RECORDSIZE = 4;
  	// CERSAI_SIGN_RECORDSIZE Changed from 7 to 4 (Srini sir 15-10-2019)
    private static int[] CERSAI_SIGN_FIXED_FIELD_SIZES = getCersaiAgreementSignatoriesFixedFieldSizes();
  	//
	private static int[] getCersaiAgreementSignatoriesFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_SIGN_RECORDSIZE];
	  	lSizes[CERSAI_SIGN_AGREEMENT_SIGNATORY] = 4;
	  	lSizes[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO] = 3;
	  	lSizes[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME] = 100;
	  	lSizes[CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION] = 100;
	//  	lSizes[CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_SERIAL_NO] = 3;
	//  	lSizes[CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_NAME] = 100;
	//  	lSizes[CERSAI_SIGN_SUP_AGREEMENT_SIGNATOTY_DESIGNATION] = 100;
	
	  	return lSizes;
	}
		//
	private static String[] getCersaiAgreementSignatoriesHeaderText(){
		String[] lHeader = new String[CERSAI_SIGN_RECORDSIZE];
		lHeader[CERSAI_SIGN_AGREEMENT_SIGNATORY] = "AGREEMENT_SIGNATORY";
		lHeader[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO] = "AGREEMENT_SIGNATORY_SERIAL_NO";
		lHeader[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME] = "AGREEMENT_SIGNATORY_NAME";
		lHeader[CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION] = "AGREEMENT_SIGNATOTY_DESIGNATION";
		return lHeader;
	}
	//
    public static List<Long> agreementSIGNSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
		return lSkipList;
	}
    //
	private String getAgreementSignatories(CersaiFileDataWrapper pCFDataWrapper, int pRecNo){
		  StringBuilder lReturnValue = new StringBuilder();
		  String[] lData = new String[CERSAI_SIGN_RECORDSIZE];
		  CompanyDetailBean lCDFinBean = pCFDataWrapper.getCDBeanFiancier();
		  CompanyDetailBean lCDSupBean = pCFDataWrapper.getCDBeanSupplier(pRecNo);
		  CompanyContactBean lCCFinBean = pCFDataWrapper.getLatestAuthorizedSignatory(lCDFinBean.getId());
		  //CompanyContactBean lCCSupBean = pCFDataWrapper.getLatestAuthorizedSignatory(lCDSupBean.getId());
		  int lFinSerialNo = 1; //((pRecNo*2)-1) ;

		  int lSupSerialNo = 2; //(pRecNo*2);
		  // 1 - AGREEMENT_SIGNATORY - CONSTANT. Value is "SIGN". - MANDATORY
		  // 1 - SIGN stands for Agreement Signatories.
		 lData[CERSAI_SIGN_AGREEMENT_SIGNATORY] =leftPad("SIGN",CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_AGREEMENT_SIGNATORY] ," ",CERSAI_SIGN_AGREEMENT_SIGNATORY,"CERSAI_SIGN_AGREEMENT_SIGNATORY");
		 //FINANCIER
		  // 2 - AGREEMENT_SIGNATORY_SERIAL_NO - NUMBER(3) - MANDATORY
		  // 2 - This is the serial number of agreement signatory.
		 lData[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO] =leftPad(lFinSerialNo+"",CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO,"CERSAI_SIGN_AGREEMENT_SIGNATORY_SERIAL_NO");
		  // 3 - AGREEMENT_SIGNATORY_NAME - VARCHAR(100) - MANDATORY
		  // 3 - This is the name of the agreement signatory
		 lData[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME] =leftPad(lCCFinBean.getFullName(),CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME,"CERSAI_SIGN_AGREEMENT_SIGNATORY_NAME");
		  // 4 - AGREEMENT_SIGNATOTY_DESIGNATION - VARCHAR(100) - MANDATORY
		  // 4 - This is the designation of the agreement signatory
		 lData[CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION] =leftPad(lCCFinBean.getDesignation(),CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION,"CERSAI_SIGN_AGREEMENT_SIGNATOTY_DESIGNATION");
		  //SUPPLIER
		  // 5 - AGREEMENT_SIGNATORY_SERIAL_NO - NUMBER(3) - MANDATORY
		  // 5 - This is the serial number of agreement signatory.
		 //lData[CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_SERIAL_NO] =leftPad(lSupSerialNo+"",CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_SERIAL_NO,"CERSAI_SIGN_AGREEMENT_SIGNATORY_SERIAL_NO");
		  // 6 - AGREEMENT_SIGNATORY_NAME - VARCHAR(100) - MANDATORY
		  // 6 - This is the name of the agreement signatory
		 //lData[CERSAI_SIGN_SUP_AGREEMENT_SIGNATORY_NAME] =leftPad(lCCSupBean.getCersaiFirstName(),CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATORY_NAME,"CERSAI_SIGN_AGREEMENT_SIGNATORY_NAME");
		  // 7 - AGREEMENT_SIGNATOTY_DESIGNATION - VARCHAR(100) - MANDATORY
		  // 7 - This is the designation of the agreement signatory
		 //lData[CERSAI_SIGN_SUP_AGREEMENT_SIGNATOTY_DESIGNATION] =leftPad(lCCSupBean.getDesignation(),CERSAI_SIGN_FIXED_FIELD_SIZES[CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION] ," ",CERSAI_SIGN_FIN_AGREEMENT_SIGNATOTY_DESIGNATION,"CERSAI_SIGN_AGREEMENT_SIGNATOTY_DESIGNATION");
		//
		formatData(lData, CERSAI_SIGN_RECORDSIZE, lReturnValue, agreementSIGNSkipList());
		//
		return lReturnValue.toString();
	}
	
	
	//Assignment
	private static int CERSAI_ASMT_ASSIGNMENT = 0;
	private static int CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT = 1;
	private static int CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD = 2;
	private static int CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC = 3;
	private static int CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG = 4;
	private static int CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC = 5;
	private static int CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE = 6;
	private static int CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE = 7;
	private static int CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE = 8;
	private static int CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE = 9;
  	private static int CERSAI_ASMT_RECORDSIZE = 10;
    private static int[] CERSAI_ASMT_FIXED_FIELD_SIZES = getCersaiAssignmentFixedFieldSizes();
  	//
	private static int[] getCersaiAssignmentFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_ASMT_RECORDSIZE];
	  	lSizes[CERSAI_ASMT_ASSIGNMENT] = 4;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT] = 23;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD] = 3;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC] = 23;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG] = 1;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC] = 23;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE] = 10;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE] = 10;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE] = 100;
	  	lSizes[CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE] = 100;
	  	return lSizes;
	}
  	//
	private static String[] getCersaiAssignmentHeaderText(){
		String[] lHeader = new String[CERSAI_ASMT_RECORDSIZE];
		lHeader[CERSAI_ASMT_ASSIGNMENT] = "ASSIGNMENT";
		lHeader[CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT] = "ASSIGNMENT_FACILITY_SANCTD_AMT";
		lHeader[CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD] = "ASSIGNMENT_REPAY_PERIOD";
		lHeader[CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC] = "ASSIGNMENT_AMT_REC_ASND_SEC";
		lHeader[CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG] = "ASSIGNMENT_ABSOLUTE_FLAG";
		lHeader[CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC] = "ASSIGNMENT_AMT_SEC_REC";
		lHeader[CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE] = "ASSIGNMENT_REPAY_START_DATE";
		lHeader[CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE] = "ASSIGNMENT_REPAY_END_DATE";
		lHeader[CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE] = "ASSIGNMENT_PARTICULARS_RECEIVABLE";
		lHeader[CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE] = "ASSIGNMENT_DESC_FUTURE_RECEIVABLE";
	  	return lHeader;
	}
	//
    public static List<Long> assignmentSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT));
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD));
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC));
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC));
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE));
    	lSkipList.add(Long.valueOf(CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE));
		return lSkipList;
	}
    //
	private String getAssignment(CersaiFileDataWrapper pCFDataWrapper, int pSerialNo){
		  StringBuilder lReturnValue = new StringBuilder();
		  InstrumentBean lInstrumentBean = pCFDataWrapper.getInstrument(pSerialNo);
		  FactoringUnitBean lFactoringUnitBean = pCFDataWrapper.getFactoringUnit(lInstrumentBean.getId());
		  String[] lData = new String[CERSAI_ASMT_RECORDSIZE];
		  String lPurSupKey =lFactoringUnitBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lFactoringUnitBean.getSupplier();
		  // 1 - ASSIGNMENT - CONSTANT. Value is "ASMT". - MANDATORY
		  // 1 - ASMT stands for Assignment.
		 lData[CERSAI_ASMT_ASSIGNMENT] =leftPad("ASMT",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT] ," ",CERSAI_ASMT_ASSIGNMENT,"CERSAI_ASMT_ASSIGNMENT");
		  // 2 - ASSIGNMENT_FACILITY_SANCTD_AMT - NUMBER(20,2) - MANDATORY
		  // 2 - This is Total Limit/Facility sanctioned under the agreement(INR).
		 lData[CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT] =leftPad(pCFDataWrapper.getTotalAmount(lPurSupKey)+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT] ," ",CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT,"CERSAI_ASMT_ASSIGNMENT_FACILITY_SANCTD_AMT");
		  // 3 - ASSIGNMENT_REPAY_PERIOD - NUMBER(3) - MANDATORY
	      // 3 - This is Expected Realization/ Repayment Period (days)
		 lData[CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD] =leftPad(lFactoringUnitBean.getTenure()+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD] ," ",CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD,"CERSAI_ASMT_ASSIGNMENT_REPAY_PERIOD");
		  // 4 - ASSIGNMENT_AMT_REC_ASND_SEC - NUMBER(20,2) - OPTIONAL
	      // 4 - This is amount receivable assigned or given as security for loan/credit
		 lData[CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC] =leftPad(pCFDataWrapper.getTotalAmount(lPurSupKey)+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC] ," ",CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC,"CERSAI_ASMT_ASSIGNMENT_AMT_REC_ASND_SEC");
		  // 5 - ASSIGNMENT_ABSOLUTE_FLAG - CONSTANT.VALUE is "Y" or "N" - MANDATORY
		  // 5 - This is the flag for whether assignment is absolute without recourse to assignee
		 lData[CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG] =leftPad(CommonAppConstants.Yes.Yes.getCode()+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG] ," ",CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG,"CERSAI_ASMT_ASSIGNMENT_ABSOLUTE_FLAG");
		  // 6 - ASSIGNMENT_AMT_SEC_REC - NUMBER(20,2) - MANDATORY(IF ASSIGNMENT_ABSOLUTE_FLAG is "N" )
		  // 6 - This is the amount of loan/credit secured by receivables
		 lData[CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC] =leftPad(pCFDataWrapper.getTotalAmount(lPurSupKey)+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC] ," ",CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC,"CERSAI_ASMT_ASSIGNMENT_AMT_SEC_REC");
		  // 7 - ASSIGNMENT_REPAY_START_DATE - DATE (DD-MM-YYYY) - MANDATORY(IF ASSIGNMENT_ABSOLUTE_FLAG is "N" )
		  // 7 - This is repayment schedule start date
		 lData[CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, lFactoringUnitBean.getLeg2MaturityExtendedDate())+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE] ," ",CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE,"CERSAI_ASMT_ASSIGNMENT_REPAY_START_DATE");
		  // 8 - ASSIGNMENT_REPAY_END_DATE - DATE (DD-MM-YYYY) - MANDATORY(IF ASSIGNMENT_ABSOLUTE_FLAG is "N" )
		  // 8 - This is repayment schedule end date
		 lData[CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, lFactoringUnitBean.getLeg2MaturityExtendedDate())+"",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE] ," ",CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE,"CERSAI_ASMT_ASSIGNMENT_REPAY_END_DATE");
		  // 9 - ASSIGNMENT_PARTICULARS_RECEIVABLE - VARCHAR(100) - OPTIONAL
		  // 9 - This is the particulars of specific receivables
		 lData[CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE] =leftPad("",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE] ," ",CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE,"CERSAI_ASMT_ASSIGNMENT_PARTICULARS_RECEIVABLE");
		  // 10 - ASSIGNMENT_DESC_FUTURE_RECEIVABLE - VARCHAR(100) - OPTIONAL
		  // 10 - This is the description of future receivables
		 lData[CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE] =leftPad("",CERSAI_ASMT_FIXED_FIELD_SIZES[CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE] ," ",CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE,"CERSAI_ASMT_ASSIGNMENT_DESC_FUTURE_RECEIVABLE");
		//
		formatData(lData, CERSAI_ASMT_RECORDSIZE, lReturnValue, assignmentSkipList());
		//
		return lReturnValue.toString();
	}
	
	
	//INVOICE
	private static int CERSAI_INVC_INVOICE = 0;
	private static int CERSAI_INVC_INVOICE_SERIAL_NO = 1;
	private static int CERSAI_INVC_INVOICE_AMOUNT = 2;
	private static int CERSAI_INVC_INVOICE_NUMBER = 3;
	private static int CERSAI_INVC_INVOICE_RAISE_DATE = 4;
	private static int CERSAI_INVC_INVOICE_GOODS_DESCRIPTION = 5;
	private static int CERSAI_INVC_INVOICE_IDENTIFCATION_MARK = 6;
	private static int CERSAI_INVC_INVOICE_PAYABLE_BY_DATE = 7;
	private static int CERSAI_INVC_RECORDSIZE = 8;
    private static int[] CERSAI_INVC_FIXED_FIELD_SIZES = getCersaiInvoiceFixedFieldSizes();
	//
	private static int[] getCersaiInvoiceFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_INVC_RECORDSIZE];
	  	lSizes[CERSAI_INVC_INVOICE] = 4;
	  	lSizes[CERSAI_INVC_INVOICE_SERIAL_NO] = 3;
	  	lSizes[CERSAI_INVC_INVOICE_AMOUNT] = 23;
	  	lSizes[CERSAI_INVC_INVOICE_NUMBER] = 100;
	  	lSizes[CERSAI_INVC_INVOICE_RAISE_DATE] = 10;
	  	lSizes[CERSAI_INVC_INVOICE_GOODS_DESCRIPTION] = 1000;
	  	lSizes[CERSAI_INVC_INVOICE_IDENTIFCATION_MARK] = 100;
	  	lSizes[CERSAI_INVC_INVOICE_PAYABLE_BY_DATE] = 10;
	  	return lSizes;
	}
	//
	private static String[] getCersaiInvoiceHeaderText(){
		String[] lHeader = new String[CERSAI_INVC_RECORDSIZE];
		lHeader[CERSAI_INVC_INVOICE] = "INVOICE";
		lHeader[CERSAI_INVC_INVOICE_SERIAL_NO] = "INVOICE_SERIAL_NO";
		lHeader[CERSAI_INVC_INVOICE_AMOUNT] = "INVOICE_AMOUNT";
		lHeader[CERSAI_INVC_INVOICE_NUMBER] = "INVOICE_NUMBER";
		lHeader[CERSAI_INVC_INVOICE_RAISE_DATE] = "INVOICE_RAISE_DATE";
		lHeader[CERSAI_INVC_INVOICE_GOODS_DESCRIPTION] = "INVOICE_GOODS_DESCRIPTION";
		lHeader[CERSAI_INVC_INVOICE_IDENTIFCATION_MARK] = "INVOICE_IDENTIFCATION_MARK";
		lHeader[CERSAI_INVC_INVOICE_PAYABLE_BY_DATE] = "INVOICE_PAYABLE_BY_DATE";
	  	return lHeader;
	}
	//
	//
    public static List<Long> invoiceSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_INVC_INVOICE_AMOUNT));
//    	lSkipList.add(Long.valueOf(CERSAI_INVC_INVOICE_NUMBER)); //TODO: WHAT TO DO?
    	lSkipList.add(Long.valueOf(CERSAI_INVC_INVOICE_RAISE_DATE));
    	lSkipList.add(Long.valueOf(CERSAI_INVC_INVOICE_PAYABLE_BY_DATE));
		return lSkipList;
	}
    //
	private String getInvoice(CersaiFileDataWrapper pCFDataWrapper, int pRecordNumber){
		  InstrumentBean lInstrumentBean = pCFDataWrapper.getInstrument(pRecordNumber);
		  CompanyDetailBean lCDSupBean = pCFDataWrapper.getCDBeanSupplier(pRecordNumber);
		  return getInvoice(lInstrumentBean,1,lCDSupBean);
	}
	private String getInvoice(InstrumentBean pInstrumentBean, int pDisplayCount, CompanyDetailBean pCDSupBean){
		  StringBuilder lReturnValue = new StringBuilder();
		  String[] lData = new String[CERSAI_INVC_RECORDSIZE];
		  // 1 - INVOICE - CONSTANT. Value is "INVC". - MANDATORY
		  // 1 - INVC stands for Invoice
		 lData[CERSAI_INVC_INVOICE] =leftPad("INVC",CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE] ," ",CERSAI_INVC_INVOICE,"CERSAI_INVC_INVOICE");
		  // 2 - INVOICE_SERIAL_NO - NUMBER(3) - MANDATORY
		  // 2 - This the serial number of the invoice
		 lData[CERSAI_INVC_INVOICE_SERIAL_NO] =leftPad(pDisplayCount+"",CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_SERIAL_NO] ," ",CERSAI_INVC_INVOICE_SERIAL_NO,"CERSAI_INVC_INVOICE_SERIAL_NO");
		  // 3 - INVOICE_AMOUNT - NUMBER(20,2) - MANDATORY
		  // 3 - This is the amount on invoice
		 lData[CERSAI_INVC_INVOICE_AMOUNT] =leftPad(pInstrumentBean.getAmount().toString(),CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_AMOUNT] ," ",CERSAI_INVC_INVOICE_AMOUNT,"CERSAI_INVC_INVOICE_AMOUNT");
		  // 4 - INVOICE_NUMBER - VARCHAR(100) - MANDATORY
		  // 4 - This is amount receivable assigned or given as security for loan/credit
		 lData[CERSAI_INVC_INVOICE_NUMBER] =leftPad(pInstrumentBean.getInstNumber(),CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_NUMBER] ," ",CERSAI_INVC_INVOICE_NUMBER,"CERSAI_INVC_INVOICE_NUMBER");
		  // 5 - INVOICE_RAISE_DATE - DATE (DD-MM-YYYY) - MANDATORY
		  // 5 - This is the date on which the invoice was raised
		 lData[CERSAI_INVC_INVOICE_RAISE_DATE] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pInstrumentBean.getInstDate()),CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_RAISE_DATE] ," ",CERSAI_INVC_INVOICE_RAISE_DATE,"CERSAI_INVC_INVOICE_RAISE_DATE");
		  // 6 - INVOICE_GOODS_DESCRIPTION - VARCHAR(1000) - MANDATORY
		  // 6 - This is the description if the goods
		 String lDesc = !StringUtils.isBlank(pCDSupBean.getCompanyDesc())?pCDSupBean.getCompanyDesc():null;
		 if (lDesc==null) {
			 lDesc = TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_INDUSTRY,pCDSupBean.getIndustry());
		 }
		 if (lDesc==null || lDesc.equals("OTHERS")) {
			 lDesc = TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_SUBSEGMENT,pCDSupBean.getIndustry()+"."+pCDSupBean.getSubSegment());
		 }
		 lData[CERSAI_INVC_INVOICE_GOODS_DESCRIPTION] =formatString(leftPad((!StringUtils.isBlank(pInstrumentBean.getDescription())?pInstrumentBean.getDescription():lDesc),CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_GOODS_DESCRIPTION] ," ",CERSAI_INVC_INVOICE_GOODS_DESCRIPTION,"CERSAI_INVC_INVOICE_GOODS_DESCRIPTION"),INVOICE_DESC_ALLOWED_FORMAT);
		  // 7 - INVOICE_IDENTIFCATION_MARK - VARCHAR(100) - OPTIONAL
		  // 7 - This is the indentification of the invoice ie Serail Number
		 lData[CERSAI_INVC_INVOICE_IDENTIFCATION_MARK] =leftPad("",CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_IDENTIFCATION_MARK] ," ",CERSAI_INVC_INVOICE_IDENTIFCATION_MARK,"CERSAI_INVC_INVOICE_IDENTIFCATION_MARK");
		  // 8 - INVOICE_PAYABLE_BY_DATE - DATE (DD-MM-YYYY) - MANDATORY
		  // 8 - This is the date by which the invoice is payable
		 lData[CERSAI_INVC_INVOICE_PAYABLE_BY_DATE] =leftPad(FormatHelper.getDisplay(DATE_FORMAT, pInstrumentBean.getMaturityDate()) ,CERSAI_INVC_FIXED_FIELD_SIZES[CERSAI_INVC_INVOICE_PAYABLE_BY_DATE] ," ",CERSAI_INVC_INVOICE_PAYABLE_BY_DATE,"CERSAI_INVC_INVOICE_PAYABLE_BY_DATE");
		//
		formatData(lData, CERSAI_INVC_RECORDSIZE, lReturnValue, invoiceSkipList());
		//
		return lReturnValue.toString();
	}

	//MISCELLANEOUS
	private static int CERSAI_MISC_MISCELLANEOUS = 0;
	private static int CERSAI_MISC_MISC_DISCOUNT = 1;
	private static int CERSAI_MISC_MISC_MARGIN_AMT_DETAILS = 2;
	private static int CERSAI_MISC_MISC_EXTENT_OPERATION_CHARGE = 3;
	private static int CERSAI_MISC_MISC_OTHER_INFO = 4;
	private static int CERSAI_MISC_RECORDSIZE = 5;
    private static int[] CERSAI_MISC_FIXED_FIELD_SIZES = getCersaiMiscellaneousFixedFieldSizes();
	//
	private static int[] getCersaiMiscellaneousFixedFieldSizes(){
	  	int[] lSizes = new int[CERSAI_MISC_RECORDSIZE];
	  	lSizes[CERSAI_MISC_MISCELLANEOUS] = 4;
	  	lSizes[CERSAI_MISC_MISC_DISCOUNT] = 23;
	  	lSizes[CERSAI_MISC_MISC_MARGIN_AMT_DETAILS] = 23;
	  	lSizes[CERSAI_MISC_MISC_EXTENT_OPERATION_CHARGE] = 50;
	  	lSizes[CERSAI_MISC_MISC_OTHER_INFO] = 100;
	  	return lSizes;
	}
	//
	private static String[] getCersaiMiscellaneousHeaderText(){
		String[] lHeader = new String[CERSAI_MISC_RECORDSIZE];
		lHeader[CERSAI_MISC_MISCELLANEOUS] = "MISCELLANEOUS";
		lHeader[CERSAI_MISC_MISC_DISCOUNT] = "MISC_DISCOUNT";
		lHeader[CERSAI_MISC_MISC_MARGIN_AMT_DETAILS] = "MISC_MARGIN_AMT_DETAILS";
		lHeader[CERSAI_MISC_MISC_EXTENT_OPERATION_CHARGE] = "MISC_EXTENT_OPERATION_CHARGE";
		lHeader[CERSAI_MISC_MISC_OTHER_INFO] = "MISC_OTHER_INFO";
	  	return lHeader;
	}
	//
    public static List<Long> miscellaneousSkipList() {
    	List<Long> lSkipList = new ArrayList<>();
    	lSkipList.add(Long.valueOf(CERSAI_MISC_MISC_DISCOUNT));
		return lSkipList;
	}
    //
	private String getMiscellaneous(CersaiFileDataWrapper pCFDataWrapper, String pPurSupKey){
		  StringBuilder lReturnValue = new StringBuilder();
		  String[] lData = new String[CERSAI_MISC_RECORDSIZE];
		  // 1 - INVOICE - CONSTANT. Value is "MISC". - MANDATORY
		  // 1 - INVC stands for Invoice
		 lData[CERSAI_MISC_MISCELLANEOUS] =leftPad("MISC",CERSAI_MISC_FIXED_FIELD_SIZES[CERSAI_MISC_MISCELLANEOUS] ," ",CERSAI_MISC_MISCELLANEOUS,"CERSAI_MISC_MISCELLANEOUS");
		 lData[CERSAI_MISC_MISC_DISCOUNT] = leftPad(pCFDataWrapper.getFuLeg1Intrest(pPurSupKey).toString(), CERSAI_MISC_FIXED_FIELD_SIZES[CERSAI_MISC_MISC_DISCOUNT]," ", CERSAI_MISC_MISC_DISCOUNT, "CERSAI_MISC_MISC_DISCOUNT");
		//
		formatData(lData, CERSAI_MISC_RECORDSIZE, lReturnValue, miscellaneousSkipList());
		//
		return lReturnValue.toString();
	}
	
    //
    private static Map<String,String[]> headerMapping = getHeaderMapping();
    private static Map<String,String[]> getHeaderMapping(){
    	Map<String,String[]> lRetVal = new HashMap<String,String[]>();
    	try {
        	lRetVal.put("FH",getCersaiBaseFileHeaderText());
        	lRetVal.put("RH",getCersaiRecordHeaderText());
        	lRetVal.put("RECO",getCersaiReceivableOwnerHeaderText());
        	lRetVal.put("ASNR",getCersaiAssignorHeaderText());
        	lRetVal.put("ASRP",getCersaiAssignorPartnerHeaderText());
        	lRetVal.put("FACT",getCersaiFactorHeaderText());
        	lRetVal.put("DBTR",getCersaiDebtorHeaderText());
        	lRetVal.put("DBTP",getCersaiDebtorPartnerHeaderText());
        	lRetVal.put("AGMT",getCersaiAgreementHeaderText());
        	lRetVal.put("SIGN",getCersaiAgreementSignatoriesHeaderText());
        	lRetVal.put("ASMT",getCersaiAssignmentHeaderText());
        	lRetVal.put("INVC",getCersaiInvoiceHeaderText());
        	lRetVal.put("MISC",getCersaiMiscellaneousHeaderText());
    	}catch(Exception lEx) {
    		logger.error("Error in HeaderMapping", lEx);
    	}
    	return lRetVal;
    }
    //
    public static String[] getMappedHeaders(String pHeaderCode){
    	if(headerMapping.containsKey(pHeaderCode)){
    		return headerMapping.get(pHeaderCode);
    	}
    	return null;
    }

	
    public String cersaiDownload(Connection pConnection,CersaiFileBean pCersaiFileBean, AppUserBean pAppUserBean) throws Exception {
        final StringBuilder lData = new StringBuilder();
        CersaiFileDataWrapper lCersaiFileDataWrapper = new CersaiFileDataWrapper(pCersaiFileBean, pConnection);
        // BATCH FILE HEADER
        int lRecNo=0;
        int lLineNo=0;
        try{
            lData.append(getCersaiBaseFileHeader(lCersaiFileDataWrapper));
            lData.append(LINE_SEPERATOR);
            //composite bean - with inst, p,s,f, fact
            // instrumnent wise loop
            // all the function below will be called linewise
            //construct the recordHeader
            CompanyDetailBean lCompanyDetailBean = null;
            String lPurchaser = null, lFinancier = null, lPurSupKey = null;
            InstrumentBean lInstrumentBean = new InstrumentBean();
            Set<String> lPurSupHash = new HashSet<>();
            //hash of P^S 
            int lRecordCount=0;
            for(lRecNo=0; lRecNo<lCersaiFileDataWrapper.getRecordCount(); lRecNo++){
            	// 
            	lInstrumentBean =  lCersaiFileDataWrapper.getInstrument(lRecNo);
            	lPurSupKey = lInstrumentBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lInstrumentBean.getSupplier();
            	if (!lPurSupHash.contains(lPurSupKey)) {
            		lPurSupHash.add(lPurSupKey);
            	}else {
            		continue;
            	}
    			lLineNo=1;
    			//HEADER DETAILS
    			lData.append(getCersaiRecordHeader(lCersaiFileDataWrapper, lRecNo ,lRecordCount,lCersaiFileDataWrapper.getInstrumentList(lPurSupKey).size()));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//RECEIVABLE OWNER DETAILS
    			lCompanyDetailBean = lCersaiFileDataWrapper.getCDBean(lRecNo, CersaiEntityType.Recivable_Owner);
    			String lCersaiCode = lCersaiFileDataWrapper.getCersaiCodeForFinancier(lCompanyDetailBean.getCode());
    			lData.append(getCersaiReceivableOwner(lCompanyDetailBean, lCersaiCode, lRecNo+1, lRecordCount));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//ASSIGNOR DETAILS
    			lCompanyDetailBean = lCersaiFileDataWrapper.getCDBean(lRecNo, CersaiEntityType.Assignor);
    			List<CompanyContactBean> lPartners = lCersaiFileDataWrapper.getPartners(lCompanyDetailBean.getId());
    			//
    			lData.append(getCersaiAssignor(lCompanyDetailBean, lPartners));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//ASSIGNOR PARTNER DETAILS
    		if (hasPartner(lCompanyDetailBean.getConstitution())) {
    			if(lPartners!=null && lPartners.size() > 0){
    				int lSerialNo = 1;
    				for(CompanyContactBean lPartnerContactBean : lPartners){
    					lData.append(getCersaiAssignorPartner(lPartnerContactBean, lSerialNo++));
    					lData.append(LINE_SEPERATOR);
    				}
    			}
    			lLineNo++;
    		}
    			//FACTOR DETAILS
    			lCompanyDetailBean = lCersaiFileDataWrapper.getCDBean(lRecNo, CersaiEntityType.Factor);
    			lFinancier = lCompanyDetailBean.getCode();
    			lData.append(getCersaiFactor(lCompanyDetailBean));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//DEBTOR DETAILS
    			lCompanyDetailBean = lCersaiFileDataWrapper.getCDBean(lRecNo, CersaiEntityType.Debtor);
    			lPurchaser = lCompanyDetailBean.getCode();
    			Long lLimit = getLimit(pConnection, lFinancier, lPurchaser);
    			lData.append(getCersaiDebtor(lCompanyDetailBean, lLimit));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//DEBTOR PARTNER DETAILS
    			//NOT FOR --> IND,COM,HUF,PRF,TRS
    			if (hasPartner(lCompanyDetailBean.getConstitution())){
    				lPartners = lCersaiFileDataWrapper.getPartners(lCompanyDetailBean.getId());
        			if(lPartners!=null && lPartners.size() > 0){
        				int lSerialNo = 1;
        				for(CompanyContactBean lPartnerContactBean : lPartners){
        					lData.append(getDebtorPartner(lPartnerContactBean, lSerialNo++));
        					lData.append(LINE_SEPERATOR);
        				}
        			}
        			lLineNo++;
        			
    			}
    			//AGREEMENT DETAILS
    			lData.append(getAgreement(lCersaiFileDataWrapper,pCersaiFileBean.getDate()));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//AGREEMENT SIGNATORIES DETAILS
    			lData.append(getAgreementSignatories(lCersaiFileDataWrapper , lRecNo));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//ASSIGNMENT DETAILS
    			lData.append(getAssignment(lCersaiFileDataWrapper, lRecNo));
    			lData.append(LINE_SEPERATOR);
    			lLineNo++;
    			//INVOICE DETAILS
    			int lCounter = 1;
    			for (InstrumentBean lInstBean: lCersaiFileDataWrapper.getInstrumentList(lPurSupKey)) {
    				lData.append(getInvoice(lInstBean, lCounter, lCersaiFileDataWrapper.getCDBeanSupplier(lRecNo)));
        			lData.append(LINE_SEPERATOR);
        			lLineNo++;
        			lCounter++;
    			}
    			//MISCELLANEOUS DETAILS
    			lData.append(getMiscellaneous(lCersaiFileDataWrapper,lPurSupKey));
    			lData.append(LINE_SEPERATOR);
    			lRecordCount++;
            }
        }catch(Exception lEx){
        	logger.info("CERSAI ERROR : RecNo : " + lRecNo + " : Line No : "+lLineNo);
        	logger.info(lEx.getMessage());
        	throw lEx;
        }
        return lData.toString();
    }
    
	private Long getLimit(Connection pConnection, String pFinancier, String pPurchaser){
    	Long lRetVal = null;
    	String lKey = pFinancier + CommonConstants.KEY_SEPARATOR + pPurchaser;
    	try {
    		if(!financierPurchaserLimits.containsKey(lKey)){
    			lRetVal = TredsHelper.getInstance().getLimit(pConnection, pFinancier, pPurchaser);
    			financierPurchaserLimits.put(lKey, lRetVal);
    		}else{
    			lRetVal = financierPurchaserLimits.get(lKey);
    		}
		} catch (Exception e) {
			logger.info("CERSAI Error while fetching limits." );
		}
    	return lRetVal;
    }

    private String leftPad(String pString, int pSize, String pPadCharacter, int pIndex, String pIndexName){
    	if(StringUtils.isNotEmpty(pString)){
    		if(pString.length() > pSize){
    			logger.info("CERSAI LENGTH MISMATCH : " + pIndexName + " , Index : "+ pIndex + ", Size : " + pSize +", Data : "+ pString);
    		}
    	}
    	//return StringUtils.leftPad(pString==null?"":((pString.length() > pSize)?pString.substring(0, pSize-1):pString), pSize, pPadCharacter);
    	return pString==null?"":((pString.length() > pSize)?pString.substring(0, pSize-1):pString);
    }

    private String rightPad(String pString, int pSize, String pPadCharacter, int pIndex, String pIndexName){
    	if(StringUtils.isNotEmpty(pString)){
    		if(pString.length() > pSize){
    			logger.info("CERSAI LENGTH MISMATCH : " + pIndexName + " , Index : "+ pIndex + ", Size : " + pSize +", Data : "+ pString);
    		}
    	}
    	//return StringUtils.rightPad(pString==null?"":((pString.length() > pSize)?pString.substring(0, pSize-1):pString), pSize, pPadCharacter);
    	return pString==null?"":((pString.length() > pSize)?pString.substring(0, pSize-1):pString);
    }
    
    private boolean hasPartner(String pConstitution) {
    	if (pConstitution!=null 
				&& !AppConstants.RC_CONSTITUENTS_HUF.equals(pConstitution)
					&& !AppConstants.RC_CONSTITUENTS_PUBLIC.equals(pConstitution)
						&& !AppConstants.RC_CONSTITUENTS_PRIVATE.equals(pConstitution)
								&& !AppConstants.RC_CONSTITUENTS_PROPRITORYSHIP.equals(pConstitution)
									&& !AppConstants.RC_CONSTITUENTS_TRUST.equals(pConstitution)) {
    		return true;
    	}
    	return false;
    }
    
    private static final Map <String,String> getCersaiStateMap() {
     Map <String,String> lMap = new HashMap<String,String>();
     lMap.put("35","01");
     lMap.put("28","02");
     lMap.put("37","02");
     lMap.put("12","03");
     lMap.put("18","04");
     lMap.put("10","05");
     lMap.put("04","06");
     lMap.put("22","07");
     lMap.put("26","08");
     lMap.put("25","09");
     lMap.put("07","10");
     lMap.put("30","11");
     lMap.put("24","12");
     lMap.put("06","13");
     lMap.put("02","14");
     lMap.put("01","15");
     lMap.put("20","16");
     lMap.put("29","17");
     lMap.put("32","18");
     lMap.put("31","19");
     lMap.put("23","20");
     lMap.put("27","21");
     lMap.put("14","22");
     lMap.put("17","23");
     lMap.put("15","24");
     lMap.put("13","25");
     lMap.put("21","26");
     lMap.put("34","27");
     lMap.put("03","28");
     lMap.put("08","29");
     lMap.put("11","30");
     lMap.put("33","31");
     lMap.put("36","36");
     lMap.put("16","32");
     lMap.put("09","33");
     lMap.put("05","34");
     lMap.put("19","35");
     return lMap;
    }
    
    public String formatString(String pString,String pFormat) {
    	if (StringUtils.isBlank(pString)) {
    	}else {
    		pString = pString.replaceAll(pFormat, " ");
        	StringTokenizer st = new StringTokenizer(pString, " ");
        	StringBuffer sb = new StringBuffer();
        	while(st.hasMoreElements()){
        	    sb.append(st.nextElement()).append(" ");
        	}
        	pString = sb.toString().trim();
    	}
    	return pString;
    }
    
    public String addNumberToString(String pString) {
    	if (!Objects.isNull(pString)) {
    		if (!pString.matches(".*\\d.*")) {
    			return "0 "+pString;
    		}
    	}
		return pString;
    }
    
    public String sanitize(String pString) {
    	if (StringUtils.isBlank(pString)) {
    	}else {
    		pString = pString.replaceAll("&", "and");
    		pString = pString.replaceAll(ALLOWED_CHARS, " ");
        	StringTokenizer st = new StringTokenizer(pString, " ");
        	StringBuffer sb = new StringBuffer();
        	while(st.hasMoreElements()){
        	    sb.append(st.nextElement()).append(" ");
        	}
        	pString = sb.toString().trim();
    	}
    	return pString;
    }
    
    public void formatData(String[] pData, int pRecordSize,StringBuilder pReturnValue, List<Long> pSkipList) {
    	String lDataStr = null;
		for(int lPtr=0; lPtr < pRecordSize; lPtr++){
			if (pData[lPtr]==null) {
				lDataStr = "";
			}else {
				if (!pSkipList.isEmpty() && pSkipList.contains(Long.valueOf(lPtr))) {
					lDataStr = pData[lPtr] ;
				}else {
					lDataStr = sanitize(pData[lPtr]);
				}
			}
			pReturnValue.append(lDataStr);
			pReturnValue.append(FIELD_SEPERATOR);
    	}
    }
    
}
