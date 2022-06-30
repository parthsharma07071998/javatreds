package com.xlx.treds;

import java.math.BigDecimal;

import com.xlx.common.messaging.EmailSender;
import com.xlx.commonn.IKeyValEnumInterface;

public class AppConstants {
    public static final String DOMAIN_PLATFORM = "TREDS";
    public static final String DOMAIN_REGUSER = "REGUSER";
    public static final String DOMAIN_REGENTITY = "REGENTITY"; //entity which registers users on behalf of RXIL
    public static final String DOMAIN_REGULATOR = "REGULATOR";
    public static final String DOMAIN_PURCHASERAGGREGATOR = "AGGREGATOR"; //entity which manages instrument on behalf of Purchaser
    
    public static final String INSTRUMENT_CHECKER = "INST"; 
    public static final String INSTRUMENT_COUNTER_CHECKER = "INSTCNTR";
    public static final String BID_CHECKER = "BID"; 
    public static final String PLATFORM_LIMIT_CHECKER = "PLIMIT"; 
    public static final String BUYER_LIMIT_CHECKER = "BLIMIT"; 
    public static final String BUYERSELLER_LIMIT_CHECKER = "BSLIMIT"; 
    public static final String USER_LIMIT_CHECKER = "ULIMIT"; 
    
    public static final String FACILITATOR_ICICI = "ICICI";
    public static final String FACILITATOR_ICICINEFT = "ICICINEFT";
    public static final String FACILITATOR_NPCI = "NPCI";
    public static final String FACILITATOR_DIRECT = "DIRECT";
    
    public static final String PARAM_SENDER ="sender";
    public static final String PARAM_SENDER_VALUE_UI ="UI";
    public static final String PARAM_SENDER_VALUE_API ="API";
    
    public static final int RECORDS_DISPLAY_MAX = 1000;
    public static final int RECORDS_DOWNLOAD_ALL = -1;
    
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATETIME_FORMAT ="dd-MM-yyyy HH:mm";
    
    public static final String RESOURCEGROUP_CLICKWRAPAGREEMENT = "CLICKWRAPAGREEMENT";
    public final static String CLICKWRAP_FILECODE_AGREEMENT = "AG";
    public final static String CLICKWRAP_FILECODE_AMENDMENT = "AM";
    public final static String CLICKWRAP_QUERYPARAMETER_FILENAME = "file";
    public final static String CLICKWRAP_QUERYPARAMETER_WARNING = "warning";
    
    public static final String CURRENCY_INR = "INR";
    
    public static final String REGISTRY_INSTRUMENTSETTINGS = "server.settings.instrumentsettings";
    public static final String ATTRIBUTE_GOODSACCEPTDATE = "goodsacceptdate";
    
    public static final String REGISTRY_MSMESETTINGS = "server.settings.msmesettings";
    public static final String ATTRIBUTE_MINUSANCE = "minusance";
    public static final String ATTRIBUTE_CREDITPERIOD = "creditperiod";
    
    public static final String REGISTRY_TRADINGWEEKDAYS = "server.settings.tradingweekdays";
    public static final String REGISTRY_CLEARINGWEEKDAYS = "server.settings.clearingweekdays";
    public static final String REGISTRY_STATUTORYREPORTTEMPLATE = "common.config.statuatoryreportfile";
    public static final String REGISTRY_STATICDOCUMENTS = "common.config.staticdocuments";
    public static final String KEY_MENULIST = "menulist";

    public static final String REGISTRY_ARCHIVEDAYCOUNT = "server.settings.archivedaycount";
    public static final String REGISTRY_OBLIGATIONHOLIDAYPREPONEDAYS = "server.settings.obligationholidaypreponedays";
    public static final String REGISTRY_NCPIMAXOBLIGATIONSPERFILE = "server.settings.npcimaxobligationsperfile";
    public static final String REGISTRY_MAXCASHDISCOUNTPERCENT = "server.settings.maxcashdiscountpercent";
    
    public static final String REGISTRY_DEFAULTPASSWORD = "defaultpassword";
    
    public static final String REGISTRY_MAXUSANCE = "server.settings.maxusance";
    
    public static final String REGISTRY_MAXALLOWEDTOTALCREDITPERIOD = "server.settings.maxallowedtotalcreditperiod";
    
    public static final String REGISTRY_OBLIGATIONEXTENSION = "server.settings.obligationextension";
    public static final String ATTRIBUTE_ALLOWOBLIGATIONEXTENSION = "allowobligationextension";
    public static final String ATTRIBUTE_MAXGRACEPERIOD = "maxgraceperiod";
    public static final String ATTRIBUTE_MAXDAYSFOREXTENSION = "maxdaysforextension";
    public static final String ATTRIBUTE_MAXDAYSFOREXTENSIONACCEPTANCE = "maxdayseorextensionacceptance";
    //
    public static final String REGISTRY_FINANCIERLIMITEXPIRYNEARINGDAYS= "server.other.financierlimitexpirynearingdays";
    //
    public static final String REGISTRY_MONETAGO = "server.settings.monetagoapi";
    public static final String ATTRIBUTE_MONETAGO_BASEURI = "baseuri";
    public static final String ATTRIBUTE_MONETAGO_NAME_APIKEY = "paramnameapikey";
    public static final String ATTRIBUTE_MONETAGO_VALUE_APIKEY = "paramvalueapikey";
    public static final String ATTRIBUTE_MONETAGO_NAME_APIPAYLOAD = "paramnameapipayload";
    public static final String ATTRIBUTE_MONETAGO_NAME_APISIGNATURE = "paramnameapisignature";
    public static final String ATTRIBUTE_MONETAGO_NAME_APINONCE = "paramnameapinonce";
    public static final String ATTRIBUTE_MONETAGO_VALUE_SECRET = "paramvalueapisecret";
    public static final String REGISTRY_MONETAGO_ENABLED = "server.settings.monetagoenabled";
    //
    public static final String REGISTRY_DESMONETAGO = "server.settings.desmonetagoapi";
    public static final String REGISTRY_DESMONETAGO_ENABLED = "server.settings.monetagodesflag";
    //
    public static final String REGISTRY_OBLIGATIONSPLITTING = "server.settings.obligationsplitsettings";
    public static final String ATTRIBUTE_MANDATEPERCENT = "percentofmandate";
    public static final String ATTRIBUTE_MANDATEGRACEPERIOD = "mandategraceperiod";
    //
    public static final String REGISTRY_INSTCLUBSPLITTING = "server.settings.instclubsettings";
    public static final String ATTRIBUTE_MAXNOOFINSTRUMENTS = "maxnoofInstruments";
    public static final String ATTRIBUTE_DATERANGEFORINSTCLUB = "daterangeforinstclub";
    public static final String ATTRIBUTE_MAXNOOFFACTINSTRUMENTS = "maxnooffactInstruments";
    //
    public static final String REGISTRY_MONETAGO_IA = "server.settings.monetagoinvoiceapi";
    public static final String REGISTRY_DESMONETAGO_IA = "server.settings.desmonetagoinvoiceapi";
    
    public static final String REGISTRY_NPCIEMAILIDS = "server.settings.npcimailids";

    public static final String REGISTRY_BUYERCREDITRATING = "server.settings.buyercreditrating";
    public static final String ATTRIBUTE_BUYERCREDITRATING_EXPIRYDAYS = "expirydays";
    public static final String ATTRIBUTE_BUYERCREDITRATING_EXPIRYMAILRANGEDAYS = "expirymailrangedays";
    public static final String ATTRIBUTE_BUYERCREDITRATING_CHANGEINRATINGTIME = "changeinratingtime";
    
    public static final String REGISTRY_MONETAGOLOGING = "server.settings.monetagologing";
    public static final String ATTRIBUTE_MONETAGOLOGING_REGISTER = "register";
    public static final String ATTRIBUTE_MONETAGOLOGING_CANCEL = "cancel";
    public static final String ATTRIBUTE_MONETAGOLOGING_FACTOR = "factor";
    
    public static final String RC_ENTITYTYPE = "ENTITYTYPE";
    public static final String RC_CONSTITUTION = "CONSTITUTION";
    public static final String RC_FINANCIERCATEGORY = "FINCATEGORY";
    public static final String RC_STATE = "STATE";
    public static final String RC_STATE_GST = "GSTSTATE";
    public static final String RC_COUNTRY = "COUNTRY";
    public static final String RC_INDUSTRY = "INDUSTRY";
    public static final String RC_SUBSEGMENT = "SUBSEGMENT";
    public static final String RC_SECTOR = "SECTOR";
    public static final String RC_EXPORTORIENTATION = "EXPORTORIENTATION";
    public static final String RC_CURRENCY = "CURRENCY";
    public static final String RC_MSMESTATUS = "MSMESTATUS";
    public static final String RC_MSMEREGTYPE = "MSME_REG_TYPE";
    public static final String RC_DESIGNATION = "DESIGNATION";
    public static final String RC_DESIGNATION_CONTACT = "DESIGNATION_CONTACT";
    public static final String RC_PROMOTERCATEGORY = "PROMOTERCATEGORY";
    public static final String RC_BANK = "BANK";
    public static final String RC_YEARSINBUSINESS = "YEARSINBUSINESS";
    //
    public static final String RC_CONSTITUENTS_PRIVATE = "PRIV";
    public static final String RC_CONSTITUENTS_PUBLIC = "PUB";
    public static final String RC_CONSTITUENTS_PROPRITORYSHIP = "PROP";
    public static final String RC_CONSTITUENTS_PARTNERSHIP = "PART";
    public static final String RC_CONSTITUENTS_TRUST = "TRST";
    public static final String RC_CONSTITUENTS_HUF = "HUF";
    //
    public static final String RC_SECTOR_MANUFACTURING = "MANUFACT";
    public static final String RC_SECTOR_SERVICE = "SERV";
    public static final String RC_SECTOR_SERVICE_TRADING = "SERVTRADE";
    //Payment advise CASHINVOICE_REASONCODES
    public static final String RC_CASHINVOICE_REASONCODES = "CASHINVOICE_REASONCODES";
    //
    public static final String RC_PLATFORM_REASON_CODE = "PLATFORM_REASON_CODE";
    //
    public static final String OTHR_SETTING_ENTITYNAME ="companyName";
    public static final String OTHR_SETTING_PAN ="pan";
    public static final String OTHR_SETTING_ENTITYTYPE ="entityType";
    public static final String OTHR_SETTING_CONSTITUTION ="constitution";
    //
    public static final long DAY_IN_MILLIS = 86400000L; 
    
    public static final Long ADMIN_ROLE_ID = Long.valueOf(0);
    public static final String LOGINID_ADMIN = "ADMIN";

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100.0);
    public static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365.0);
    //
    public static final String REG_OFFICE_DESC = "Reg. Office";
    //
    //Email Notification - EntityNotificationTypes
    public static final String EMAIL_NOTIFY_TYPE_INSTSUBMIT_1 = "InstSubmit_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTSUBMIT_2 = "InstSubmit_2";
    public static final String EMAIL_NOTIFY_TYPE_INSTCHKRRET_1 = "InstChkrRet_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCHKRREJ_1 = "InstChkrRej_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCHKRAPP_1 = "InstChkrApp_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCHKRAPPCNTR_1 = "InstChkrAppCntr_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCNTRRET_1 = "InstCntrRet_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCNTRREJ_1 = "InstCntrRej_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCNTRACC_1 = "InstCntrAcc_1";
    public static final String EMAIL_NOTIFY_TYPE_CNTRINSTACC_1 = "CntrInstAcc_1";
    public static final String EMAIL_NOTIFY_TYPE_CNTRINSTREJ_1 = "CntrInstRej_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCNTRMKRSUB_1 = "InstCntrMkrSub_1";
    public static final String EMAIL_NOTIFY_TYPE_INSTCNTRCHKRET_1 = "InstCntrChkRet_1";
    public static final String EMAIL_NOTIFY_TYPE_FUAUCREADY_1 = "FUAucReady_1";
    public static final String EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWOWNER_1 = "FUSuspWithdrawOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWNONOWNER_1 = "FUSuspWithdrawNonOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWFINANCIER_1 = "FUSuspWithdrawFinancier_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1 = "BidAcceptCB_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1 = "BidAcceptFin_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDENTRYOWNER_1 = "BidEntryOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDMODIFYOWNER_1 = "BidModifyOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDCANCELOWNER_1 = "BidCancelOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDENTRYNONOWNER_1 = "BidEntryNonOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDMODIFYNONOWNER_1 = "BidModifyNonOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_BIDCANCELNONOWNER_1 = "BidCancelNonOwner_1";
    public static final String EMAIL_NOTIFY_TYPE_LINKACTIONS_1 = "LinkActions_1";
    public static final String EMAIL_NOTIFY_TYPE_NOTICEOFASSIGNMENT_1 = "NoticeOfAssignment_1";
    public static final String EMAIL_NOTIFY_TYPE_CURRENTFINOBLIGATIONDUE_1 = "CurrentFinObligationDue_1";
    public static final String EMAIL_NOTIFY_TYPE_CURRENTPUROBLIGATIONDUE_1 = "CurrentPurObligationDue_1";
    public static final String EMAIL_NOTIFY_TYPE_FUTUREDATEOBLIGATIONDUE_1 = "FutureDateObligationDue_1";
    public static final String EMAIL_NOTIFY_TYPE_OBLIGATIONSTATUS_1 = "ObligationStatus_1";
    public static final String EMAIL_NOTIFY_TYPE_FUCLUBBEDINSTCHANGED_1 = "FUClubbedInstChanged_1";
    public static final String EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1 = "BestBidChangedFin_1";
    public static final String EMAIL_NOTIFY_TYPE_FUACTIVEMAPPEDFINANCIER_1 = "FUActiveMappedFinancier_1";
    public static final String EMAIL_NOTIFY_TYPE_BUYERCHANGEINRATINGNOTIFICATION_1 = "FUActiveMappedFinancier_1";  
    public static final String EMAIL_NOTIFY_TYPE_BUYERRATINGEXPIRY_1 = "BuyerRatingExpiry_1";
    public static final String EMAIL_NOTIFY_TYPE_BUYERRATINGCHANGE_1 = "BuyerRatingChange_1";
    public static final String EMAIL_NOTIFY_TYPE_PAYMENTADVICEL1SET_1 = "PaymentAdviceL1Set_1";
    public static final String EMAIL_NOTIFY_TYPE_OBLIGATIONL2STATUS_1 = "ObligationL2Status_1";

    //
    // email templates
    public static final String TEMPLATE_CREATEPASSWORD = "CreatePassword.json";
    public static final String TEMPLATE_INSTRUMENTCHECKERACTION = "InstrumentCheckerAction.json";
    public static final String TEMPLATE_INSTRUMENTSUBMIT = "InstrumentSubmit.json";
    public static final String TEMPLATE_INSTRUMENTCOUNTERINBOX = "InstrumentCounterInbox.json";
    public static final String TEMPLATE_INSTRUMENTCOUNTERACTION = "InstrumentCounterAction.json";
    public static final String TEMPLATE_INSTRUMENTCOUNTERACTIONSELF = "InstrumentCounterActionSelf.json";
    public static final String TEMPLATE_FUCREATEDINFORMDESIGNATEDBANK = "FUCreatedInformDesignatedBank.json";
    public static final String TEMPLATE_INSTRUMENTREADYFORAUCTION = "InstrumentReadyForAuction.json";
    public static final String TEMPLATE_FUACTIVATIONINFORMMAPPEDFINANCIER = "FuActivationInformMappedFinanciers.json";
    public static final String TEMPLATE_FUCLUBBEDINSTCHANGED = "FUClubbedInstChanged.json";
    public static final String TEMPLATE_INSTRUMENTCOUNTERMAKCHKACTION = "InstrumentCounterMakChkAction.json";

    

    public static final String TEMPLATE_BIDMANAGEOWNER = "BidManageOwner.json";
    public static final String TEMPLATE_BIDMANAGENONOWNER = "BidManageNonOwner.json";
    public static final String TEMPLATE_BIDACCEPTSELF = "BidAcceptSelf.json";
    public static final String TEMPLATE_BIDACCEPTFINANCIER = "BidAcceptFinancier.json";
    public static final String TEMPLATE_UNACCEPTBIDSFINANCIER = "UnAcceptBidsFinancier.json";
    public static final String TEMPLATE_BESTBIDCHANGEDFINANCIER = "BestBidChanged.json";
    public static final String TEMPLATE_PAYMENTADVMAIL = "PaymentAdvMail.json";

    public static final String TEMPLATE_FUSUSPWITHDRAWOWNER = "FUSuspWithdrawOwner.json";
    public static final String TEMPLATE_FUSUSPWITHDRAWNONOWNER = "FUSuspWithdrawNonOwner.json";
    public static final String TEMPLATE_FUSUSPWITHDRAWFINANCIER = "FUSuspWithdrawFinancier.json";

    public static final String TEMPLATE_REGISTRATIONBUYERBANK = "RegistrationBuyerBank.json";
    public static final String TEMPLATE_REGISTRATIONSELLERBANK = "RegistrationSellerBank.json";
    public static final String TEMPLATE_REGISTRATIONSUBMISSION = "RegistrationSubmission.json";
    public static final String TEMPLATE_REGISTRATIONAPPROVED = "RegistrationApproved.json";
    public static final String TEMPLATE_REGISTRATIONRETURNED = "RegistrationReturned.json";
    public static final String TEMPLATE_REGISTRATIONLOGINDETAILS = "RegistrationLoginDetails.json";
    public static final String TEMPLATE_REGISTRATIONLOGINDETAILSENTITY = "RegistrationLoginDetailsEntity.json";
    public static final String TEMPLATE_REGISTRATIONDEACTIVATE = "RegistrationLoginDeActivation.json";

    public static final String TEMPLATE_BUYERSELLERLINKACTIONS = "BuyerSellerLinkActions.json";
    public static final String TEMPLATE_NOTICEOFASSIGNMENT = "NoticeOfAssignment.json";
    //
    public static final String TEMPLATE_OBLIDUEL1FINANCIER = "ObligationDueLeg1Financier.json";
    public static final String TEMPLATE_OBLIDUEL1PURCHASER = "ObligationDueLeg1Purchaser.json";
    //
    //ObligationLeg1Status.json also used for leg2status.
    public static final String TEMPLATE_OBLILEG1STATUSPURCHSER = "ObligationLeg1SupDetailsToPur.json";
    public static final String TEMPLATE_OBLILEG1STATUS = "ObligationLeg1Status.json";
    public static final String TEMPLATE_OBLILEG2DUENEXT5DAYS = "ObligationLeg2DueNext5Days.json";
    public static final String TEMPLATE_OBLILEG2STATUS = "ObligationLeg2Status.json";
    //
    public static final String TEMPLATE_FINSETTLE_LEGFILE = "FinancierSettlementLegFile.json";
    public static final String TEMPLATE_FINSETTLE_MISFILE = "FinancierSettlementMISFile.json";
	//
    public static final String TEMPLATE_EOD_SUCCESS = "EODAutoEmail.json";
    public static final String TEMPLATE_NPCISETTLEMENTTEMPLATE = "NPCISettlementTemplate.json";
    //
    public static final String TEMPLATE_MEMO = "Memohtml.json";
    public static final String TEMPLATE_MANDATE = "Mandatehtml.json";
    //
    public static final String TEMPLATE_FINANCIERLIMITNEAREXPIRY = "FinacierLimitNearExpiry.json";
    public static final String TEMPLATE_BUYERCHANGEINRATINGNOTIFICATION = "BuyerChangeInRatingNotification.json";
    public static final String TEMPLATE_BUYEREXPIRYRATINGNOTIFICATION = "BuyerExpiryRatingNotification.json";
    //payment Advise template
    public static final String TEMPLATE_PAYMENTADVICEINFO = "PaymentAdviceInfo.json";
    //Purchaser Supplier Banker 
    public static final String TEMPLATE_PURCHASERSUPPLIERBANKER = "PurchaserSupplierBanker.json";
    //
    //OTP
    public static final String OTP_NOTIFY_TYPE_BIDENTRY = "BidEntry";
    public static final String OTP_NOTIFY_TYPE_BUYSELLLINKAPPROVAL = "BuySellLinkApproval";
    public static final String OTP_NOTIFY_TYPE_INSTCHKAPPROVAL = "InstChkApproval";
    public static final String OTP_NOTIFY_TYPE_INSTCNTRAPPROVAL = "InstCntrApproval";
    public static final String OTP_NOTIFY_TYPE_INSTSUBMIT = "InstSubmit";
    public static final String OTP_NOTIFY_TYPE_FUSENTTOAUCTION = "FUSentToAuction";
    public static final String OTP_NOTIFY_TYPE_FUWITHDRAWAL = "FUWithdrawal";
    public static final String OTP_NOTIFY_TYPE_BIDACCEPTCB = "BidAcceptCB";
    public static final String OTP_NOTIFY_TYPE_BIDCHKAPPREJ = "BidChkAppRej";
    public static final String OTP_NOTIFY_TYPE_FINLIMITSELFAPPROVAL = "FinLimitSelfApproval";
    public static final String OTP_NOTIFY_TYPE_FINLIMITSELFMODIFY = "FinLimitSelfModify";
    public static final String OTP_NOTIFY_TYPE_FINUSERLIMITNEW = "FinUserLimitNew";
    public static final String OTP_NOTIFY_TYPE_FINUSERLIMITMODIFY = "FinUserLimitModify";
    public static final String OTP_NOTIFY_TYPE_FINUSERLIMITAPPROVAL = "FinUserLimitApproval";
    public static final String OTP_NOTIFY_TYPE_FINBUYERLIMITAPPROVAL = "FinBuyerLimitApproval";
    public static final String OTP_NOTIFY_TYPE_FINBUYERLIMITNEW = "FinBuyerLimitNew";
    public static final String OTP_NOTIFY_TYPE_FINBUYERLIMITMODIFY = "FinBuyerLimitModify";
    public static final String OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITAPPROVAL = "FinBuyerSellerLimitApproval";
    public static final String OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITNEW = "FinBuyerSellerLimitNew";
    public static final String OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITMODIFY = "FinBuyerSellerLimitModify";
    public static final String OTP_NOTIFY_TYPE_BUYSELLLINKSUBMISSION = "BuySellLinkSubmission";

    //
    //public static final String TEMPLATE_PREFIX_OTPBIDACCEPT = AuthenticationHandler.DEFAULT_OTP_VERIFICATION_TEMPLATE_PREFIX; // "VerificationOtp";
    //
    public static final String TEMPLATE_PREFIX_BIDENTRY = "BidEntry";
    public static final String TEMPLATE_PREFIX_INSTSUBMIT = "InstSubmit";
    public static final String TEMPLATE_PREFIX_INSTCHKAPPROVAL = "InstChkApproval";
    public static final String TEMPLATE_PREFIX_INSTCNTRAPPROVAL = "InstCntrApproval";
    public static final String TEMPLATE_PREFIX_FUSENTTOAUCTION = "FUSentToAuction";
    public static final String TEMPLATE_PREFIX_BIDACCEPTCB = "BidAcceptCB";
    public static final String TEMPLATE_PREFIX_FUWITHDRAWAL = "FUWithdrawal";
    public static final String TEMPLATE_PREFIX_BIDCHKAPPREJ = "BidChkAppRej";
    public static final String TEMPLATE_PREFIX_FINLIMITSELFMODIFY = "FinLimitSelfModify";
    public static final String TEMPLATE_PREFIX_FINLIMITSELFAPPROVAL = "FinLimitSelfApproval";
    public static final String TEMPLATE_PREFIX_FINUSERLIMITNEW = "FinUserLimitNew";
    public static final String TEMPLATE_PREFIX_FINUSERLIMITMODIFY = "FinUserLimitModify";
    public static final String TEMPLATE_PREFIX_FINUSERLIMITAPPROVAL = "FinUserLimitApproval";
    public static final String TEMPLATE_PREFIX_FINBUYERLIMITNEW = "FinBuyerLimitNew";
    public static final String TEMPLATE_PREFIX_FINBUYERLIMITMODIFY = "FinBuyerLimitModify";
    public static final String TEMPLATE_PREFIX_FINBUYERLIMITAPPROVAL = "FinBuyerLimitApproval";
    public static final String TEMPLATE_PREFIX_FINBUYERSELLERLIMITNEW = "FinBuyerSellerLimitNew";
    public static final String TEMPLATE_PREFIX_FINBUYERSELLERLIMITMODIFY = "FinBuyerSellerLimitModify";
    public static final String TEMPLATE_PREFIX_FINBUYERSELLERLIMITAPPROVAL = "FinBuyerSellerLimitApproval";
    public static final String TEMPLATE_PREFIX_BUYSELLLINK = "BuySellLinkApproval";
    
    public static final String TEMPLATE_EXTENSIONREQUEST = "ExtensionRequest.json";	
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONREQUEST_1 = "ExtensionRequest_1";
    public static final String TEMPLATE_ExtensionBidApproval= "ExtensionBidApproval.json";	
    public static final String EMAIL_NOTIFY_TYPE_ExtensionBidApproval_1 = "ExtensionBidApproval_1";
    public static final String TEMPLATE_EXTENSIONEXPIRY = "ExtensionExpiry.json";	
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONEXPIRY_1 = "ExtensionExpiry_1";
    public static final String TEMPLATE_EXTENSIONREQUESTFINACT = "ExtensionRequestFinAct.json";	
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONREQUESTFINACT_1 = "ExtensionRequestFinAct_1";
    public static final String TEMPLATE_EXTENSIONREQUESTFINREJ = "ExtensionRequestFinRej.json";	
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONREQUESTFINREJ_1 = "ExtensionRequestFinRej_1";
    public static final String TEMPLATE_EXTENSIONWITHDRAW = "ExtensionWithdraw.json";
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONBIDWITHDRAW_1 = "ExtensionBidWithdraw_1";

    public static final String TEMPLATE_LETTEROFEXTENSION = "LetterOfExtension.json";
    public static final String TEMPLATE__EXTENSIONDETAILS = "ExtensionDetails.json";
    public static final String EMAIL_NOTIFY_TYPE_EXTENSIONDETAILS_1 = "ExtensionDetails_1";
    public static final String TEMPLATE__ANUALCHARGEEXPIRY ="AnualChargeExpiry.json";
    public static final String EMAIL_NOTIFY_TYPE_AnualChargeExpiry_1 = "AnualChargeExpiry_1";

    
    public static final Long DEFAULT_PLAN_PURCHASER = new Long(1);
    public static final Long DEFAULT_PLAN_ZERO = new Long(2);
    //
    //API responses
    public static final Long HTTP_RESPONSE_STATUS_200_OK = new Long(200);
    public static final Long HTTP_RESPONSE_STATUS_202_ACCEPTED = new Long(202);
    //
    
    public static enum RegEntityType implements IKeyValEnumInterface<String> {
        Purchaser("P"), Supplier("S"), Financier("F");
        private final String code;
        private RegEntityType(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }    
    public static enum EntityType implements IKeyValEnumInterface<String> {
        Purchaser("P"), Supplier("S"), Financier("F"), Platform("T"), RegEntity("R"), Regulator("G"), Aggregator("A");
        private final String code;
        private EntityType(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
    public static enum CompanyApprovalStatus implements IKeyValEnumInterface<String> {
        Draft("D"), Submitted("S"), ReSubmitted("E"), Approved("A"), Returned("B"), Rejected("R"), ApprovalModification("M"),ApprovalModificationSubmit("F"),ApprovalModificationApproved("X"),ApprovalModificationReturned("Z");
        private final String code;
        private CompanyApprovalStatus(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
    
    public static enum AppEntityStatus implements IKeyValEnumInterface<String> {
        Active("A"), Suspended("S"), Disabled("D");
        private final String code;
        private AppEntityStatus(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
    public enum CostBearer implements IKeyValEnumInterface<String>{
        Seller("S","Seller"),Buyer("P","Buyer");
        
        private final String code;
        private final String desc;
        private CostBearer(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    public enum CostBearingType implements IKeyValEnumInterface<String>{
        Seller("S","Seller"),Buyer("P","Buyer"),Periodical_Split("PD","Periodical Split"),Percentage_Split("PC","Percentage Split");
        
        private final String code;
        private final String desc;
        private CostBearingType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    public enum AutoAcceptBid implements IKeyValEnumInterface<String>{
        OnRecepitOfBid("R","On Receipt of Bid"),CutOffTime("C","Cut-off Time"),Disabled("D","Disable");
        
        private final String code;
        private final String desc;
        private AutoAcceptBid(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    public enum AutoAcceptableBidTypes implements IKeyValEnumInterface<String>{
        OpenBids("YN","Open Bids"),AllBids("YY","All Bids");
        
        private final String code;
        private final String desc;
        private AutoAcceptableBidTypes(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    public enum CostCollectionLeg implements IKeyValEnumInterface<String>{
        Leg_1("L1","Leg 1"),Leg_2("L2","Leg 2");
        
        private final String code;
        private final String desc;
        private CostCollectionLeg(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    public static enum CheckerType implements IKeyValEnumInterface<String> {
        Instrument("I"), Limit("L"), Bidding("B");
        private final String code;
        private CheckerType(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
    public enum Owner implements IKeyValEnumInterface<String>{
        Private("PRIVATE","Private"),Public("PUBLIC","Public");
        
        private final String code;
        private final String desc;
        private Owner(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    
    public enum AutoConvert implements IKeyValEnumInterface<String>{
        Auto("Y","Auto"),Supplier("S","Supplier"),Purchaser("P","Purchaser");
        
        private final String code;
        private final String desc;
        private AutoConvert(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }
    
    public enum MailerType implements IKeyValEnumInterface<String>{
        Implicit("I","Implicit"),Explicit("E","Explicit"),Both("B","Both"),None("N","None");;
        
        private final String code;
        private final String desc;
        private MailerType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    
    public enum EmailSenders implements IKeyValEnumInterface<String>{
    	TO(EmailSender.TO, "TO"), CC(EmailSender.CC,"CC"),BCC(EmailSender.BCC,"BCC");
        
        private final String code;
        private final String desc;
        private EmailSenders(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    
    public enum EntityEmail implements IKeyValEnumInterface<String>{
    	AdminEmail("AdminE", "Admin Email"), UserEmail("UserE","User Email"), NOAEmail("NoaE","NOA Email"), Explicit("Expli", "Explicit"), RoleBased("RoleBase", "Role-based"), UserDefined("UserDefined", "User-Defined");
        
        private final String code;
        private final String desc;
        private EntityEmail(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }

    public enum MessageType implements IKeyValEnumInterface<String>{
        Implicit("I","Implicit"),Explicit("E","Explicit"),Both("B","Both"),None("N","None");;
        
        private final String code;
        private final String desc;
        private MessageType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }

    public enum EntityContact implements IKeyValEnumInterface<String>{
    	AdminContact("AdminC", "Admin Contac"), UserContact("UserC","User Contact"),  Explicit("Expli", "Explicit");
        
        private final String code;
        private final String desc;
        private EntityContact(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    
    public enum ChargeType implements IKeyValEnumInterface<String>{
    	Normal("Normal", "N"), Split("Split","S"), Extension("Extension","E");
        
        private final String code;
        private final String desc;
        private ChargeType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
            return desc;
        }
    }
    
    public enum FieldType implements IKeyValEnumInterface<String>{
        String("STRING","String"),Integer("INTEGER","Integer"),Decimal("DECIMAL","Decimal"),DATE("DATE","DATE"),DATETIME("DATETIME","DATETIME");
        
        private final String code;
        private final String desc;
        private FieldType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }
    
}