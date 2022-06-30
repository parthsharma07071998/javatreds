
package com.xlx.treds;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.messaging.SMSSender;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.DataType;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.commonn.rest.CommonExceptionMapper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.AppConstants.EntityEmail;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.AppConstants.MailerType;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.BidBean.BidType;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.IObligation;
import com.xlx.treds.auction.bean.ObliFUInstDetailBean;
import com.xlx.treds.auction.bean.ObliFUInstDetailSplitsBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.bill.bo.BillBO;
import com.xlx.treds.entity.bean.AggregatorPurchaserMapBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyContactBean.Gender;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyLocationBean.LocationType;
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.entity.bean.EntityNotificationSettingBean;
import com.xlx.treds.entity.bean.EntityOtpNotificationSettingBean;
import com.xlx.treds.entity.bean.MemberTurnoverBean;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.instrument.bean.BHELPEMInstrumentBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentWorkFlowBean;
import com.xlx.treds.instrument.bean.MemberLocationForInstKeysBean;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.instrument.bo.InstrumentCounterUploader;
import com.xlx.treds.instrument.bo.InstrumentUploader;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.AuctionChargePlanBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean.ChargeType;
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.master.bean.GSTRateBean;
import com.xlx.treds.master.bo.AuctionChargePlansBO;
import com.xlx.treds.other.bean.CustomFieldBean;
import com.xlx.treds.other.bean.FactoredReportBean;
import com.xlx.treds.other.bo.CustomFieldBO;
import com.xlx.treds.sftp.AdapterSFTPConfigBean;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class TredsHelper
{
	public static Logger logger = Logger.getLogger(TredsHelper.class);
    private static TredsHelper theInstance;
    //
    private GenericDAO<CompanyDetailBean> companyDetailDAO;
    private GenericDAO<CompanyDetailBean> companyDetailProvDAO;
    private HashMap<String, String> gstStateCodeDesc = null; //Key=GSTStateCode, Value= GST State Desc
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<CompanyLocationBean> companyLocationProvDAO;
    private GenericDAO<MemberwisePlanBean> memberwisePlanDAO;
    private GenericDAO<MemberTurnoverBean> memberTurnoverDAO;
    private AuctionChargePlansBO auctionChargePlansBO;
    private GenericDAO<FacilitatorEntityMappingBean> facilitatorEntityMappingBeanDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<AggregatorPurchaserMapBean> aggregatorPurchaserMapBeanDAO;
    private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
    private CompositeGenericDAO<ObliFUInstDetailSplitsBean> obliFUInstDetailSplitsDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private GenericDAO<FactoredReportBean> factoredReportDAO;
    private GenericDAO<InstrumentWorkFlowBean> instrumentWorkFlowDAO;
    private GenericDAO<MemberLocationForInstKeysBean> memberLocationForInstKeysDAO;
    private GenericDAO<BHELPEMInstrumentBean> bhelPemInstrumentDAO;
    private CompositeGenericDAO<FactoredBean> factoredDAO;
    private Pattern pattern;
    
    private AppUserBO appUserBO;
    
    public static final String PROP_APP_URL = "ApplicationURL";
    public static final String PROP_API_URL = "APIURL";
    public static final String PROP_PROXY_IP = "proxyip";
    public static final String PROP_PROXY_PORT = "proxyport";
	private final String applicationURL;
	private final String apiURL;
	private final String proxyIp;
	private final String proxyPort;
    public static final String FILE_NAME = "treds";
	public static final String FORMAT_DATE="dd MMM yyyy";
	public static final String FORMAT_TIME="HH:mm:ss";
	public static final String FORMAT_AMOUNT="#########0.00";	
	public static final String FORMAT_AMOUNT_COMMA="##,##,##,##,##,##,##,##0.00";
	public static final String JQUERYXFORMSVERSION = "?v1.1";
    //
	//
    public static final String REFCODE_ICICINACHDESC = "ICICINACHDESC";
    public static final String REGISTRY_ICICITREDSACCOUNT = "server.settings.icicitredsaccount";
    public static final String REGISTRY_TREDSCHARGEACCOUNT = "server.settings.tredschargeaccount";
    public static final String ATTRIBUTE_NEFTPOOLACCOUNTNO = "neftpoolaccountno";
    public static final String ATTRIBUTE_NACHPOOLACCOUNTNO = "nachpoolaccountno";
    public static final String ATTRIBUTE_NACHUSERNAME = "nachusername";
    public static final String ATTRIBUTE_NACHUSERNUMBER = "nachusernumber";
    public static final String ATTRIBUTE_NACHSPONSORBANKIFSC = "nachsponsorbankifsc";
    //
    public static final String REGISTRY_MONETAGOOVERRIDEFLAGS = "server.settings.monetagoverrideflags";
    public static final String ATTRIBUTE_MONETAGOOVERRIDEBITPOSITION = "bitposition";
    public static final String ATTRIBUTE_MONETAGOOVERRIDEFLAGVALUE = "overrideflag";
    public static long MONETAGO_OVERRIDE_FLAG_VALUE = 0;
    public static long MONETAGO_DUPLICATE_FLAG_VALUE = 0;
    //
    public static final String REGISTRY_EODAUTOMATIONSETTINGS = "server.settings.eodautomationsettings";
    public static final String ATTRIBUTE_EODEnable = "enable";
    public static final String ATTRIBUTE_EODStartAfter = "eodstartafter";
    //
    public static final String REGISTRY_REGISTRATIONSETTINGS = "server.settings.registrationsettings";
    public static final String ATTRIBUTE_PASSWORD = "password";
    public static final String ATTRIBUTE_EXPIRYDAYS = "expirydays";
    //
    public static final String REGISTRY_DEEDOFASSIGNMENT ="server.settings.deedofassignment";
    //
    private List<String> validOldStatusForModification;
    private Map<Status,List<Status>> validStatusMap;
    private Map<String, Boolean> validModificationRequestMap;
    //
    private boolean otpEmail = false;
    private boolean otpSms = false;
    private Integer otpExpiryMinutes = 15;
    //
    //
	public static TredsHelper getInstance()
	{
		if (theInstance == null)
		{
			synchronized(TredsHelper.class)
			{
				if (theInstance == null)
				{
					try
					{
						TredsHelper tmpTheInstance = new TredsHelper();
						tmpTheInstance.pattern = Pattern.compile("^[=+@-]");
						theInstance = tmpTheInstance;
					}
					catch(Exception lException)
					{
						logger.fatal("Error while instantiating Treds Helper",lException);
					}
				}
			}
		}
		return theInstance;
	}

	protected TredsHelper() 
	{
		applicationURL = CommonUtilities.getProperty(FILE_NAME, PROP_APP_URL);
		apiURL = CommonUtilities.getProperty(FILE_NAME, PROP_API_URL);
		proxyIp = CommonUtilities.getProperty(FILE_NAME, PROP_PROXY_IP);
		proxyPort = CommonUtilities.getProperty(FILE_NAME, PROP_PROXY_PORT);
        companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);   
        companyDetailProvDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class, CompanyDetailBO.TABLENAME_PROV);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);   
        companyLocationProvDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class, CompanyLocationBO.TABLENAME_PROV);   
        MONETAGO_OVERRIDE_FLAG_VALUE = getMonetagoOverrideFlagValue();
        MONETAGO_DUPLICATE_FLAG_VALUE = getMonetagoDuplicateFlagValue();
        memberwisePlanDAO = new GenericDAO<MemberwisePlanBean>(MemberwisePlanBean.class);
        memberTurnoverDAO = new GenericDAO<MemberTurnoverBean>(MemberTurnoverBean.class);
        auctionChargePlansBO = new AuctionChargePlansBO();
        facilitatorEntityMappingBeanDAO = new GenericDAO<FacilitatorEntityMappingBean>(FacilitatorEntityMappingBean.class);
        obligationSplitsDAO=new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        aggregatorPurchaserMapBeanDAO = new GenericDAO<AggregatorPurchaserMapBean>(AggregatorPurchaserMapBean.class);
    	obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
    	financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
    	companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
    	instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
    	factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
    	factoredReportDAO = new GenericDAO<FactoredReportBean>(FactoredReportBean.class);
    	obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
    	instrumentWorkFlowDAO = new GenericDAO<InstrumentWorkFlowBean>(InstrumentWorkFlowBean.class);
    	//
        validOldStatusForModification = Arrays.asList(new String[]{Status.Returned.getCode(),Status.Cancelled.getCode(),Status.Prov_Success.getCode(),Status.Failed.getCode()});
        validStatusMap = createValidModificationStatuses();
        validModificationRequestMap = createValidModificationRequestMap();
        appUserBO = new AppUserBO();
        memberLocationForInstKeysDAO = new GenericDAO<MemberLocationForInstKeysBean>(MemberLocationForInstKeysBean.class);
        //
        bhelPemInstrumentDAO = new GenericDAO<BHELPEMInstrumentBean>(BHELPEMInstrumentBean.class);
        factoredDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
        obliFUInstDetailSplitsDAO= new CompositeGenericDAO<ObliFUInstDetailSplitsBean>(ObliFUInstDetailSplitsBean.class);
		//
        //2FA OTP
        Object lValue = null;
        HashMap lOtpSettings = RegistryHelper.getInstance().getStructure(AuthenticationHandler.REGISTRY_2FA_OTP);
        lValue = (lOtpSettings == null) ? null : lOtpSettings.get(AuthenticationHandler.ATTRIBUTE_OTPEMAIL);
        otpEmail = (lValue == null) ? false : ((Boolean) lValue).booleanValue();
        lValue = (lOtpSettings == null) ? null : lOtpSettings.get(AuthenticationHandler.ATTRIBUTE_OTPSMS);
        otpSms = (lValue == null) ? false : ((Boolean) lValue).booleanValue();
        lValue = (lOtpSettings == null) ? null : lOtpSettings.get(AuthenticationHandler.ATTRIBUTE_OTPEXPIRYMINUTES);
        otpExpiryMinutes = (lValue == null) ? 15 : Integer.parseInt(lValue.toString());
	}
	public String getApplicationURL()
	{
		return applicationURL;
	}

	public String getAPIURL()
	{
		return apiURL;
	}
	public String getProxyIp()
	{
		return proxyIp;
	}
	public Long getProxyPort()
	{
		if(!StringUtils.isBlank(proxyPort))
			return new Long(proxyPort);
		return null;
	}
	private long getMonetagoOverrideFlagValue(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        long lRetVal = 0, lBitPos = 0;
        HashMap[] lOverrideFlagList =  lRegistryHelper.getStructureList(REGISTRY_MONETAGOOVERRIDEFLAGS);
        
        for (HashMap lOverrideFlagStruct : lOverrideFlagList) {
        	if( (Boolean) lOverrideFlagStruct.get(ATTRIBUTE_MONETAGOOVERRIDEFLAGVALUE)){
        		lBitPos  = (Long) lOverrideFlagStruct.get(ATTRIBUTE_MONETAGOOVERRIDEBITPOSITION);
        		lRetVal += Math.pow(2, lBitPos);
        	}
        }
        //
		return lRetVal;
	}
	public long getOverride()
	{
		return MONETAGO_OVERRIDE_FLAG_VALUE;
	}
	public long getDuplicateFlagValue()
	{
		return MONETAGO_DUPLICATE_FLAG_VALUE;
	}	
	private long getMonetagoDuplicateFlagValue(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        long lRetVal = 0, lBitPos = 0;
        HashMap[] lOverrideFlagList =  lRegistryHelper.getStructureList(REGISTRY_MONETAGOOVERRIDEFLAGS);
        
        for (HashMap lOverrideFlagStruct : lOverrideFlagList) {
        	if( !(Boolean) lOverrideFlagStruct.get(ATTRIBUTE_MONETAGOOVERRIDEFLAGVALUE)){
        		lBitPos  = (Long) lOverrideFlagStruct.get(ATTRIBUTE_MONETAGOOVERRIDEBITPOSITION);
        		lRetVal += Math.pow(2, lBitPos);
        	}
        }
        //
		return lRetVal;
	}
	
    public boolean checkDuplicateFlag(long pDuplicateFlagValue){
        return ((MONETAGO_DUPLICATE_FLAG_VALUE | pDuplicateFlagValue) == pDuplicateFlagValue);
    }
	
	public String getRefCodeValue(String pRCCode, String pCode)
	{
		RefCodeValuesBean lRefCodeValuesBean = null;
		try
		{
			lRefCodeValuesBean = RefMasterHelper.getInstance().getRefCodeValuesBean(pCode, pRCCode);
		}
		catch (MemoryDBException e)
		{
		}
		if(lRefCodeValuesBean!= null)
			return lRefCodeValuesBean.getDesc();
		return "";
	}

	public ArrayList<RefCodeValuesBean> getRefCodeValues(String pCode)
	{
		ArrayList<RefCodeValuesBean> lRefCodeValueBeans = new ArrayList<RefCodeValuesBean>();
		try
		{
			lRefCodeValueBeans = RefMasterHelper.getInstance().getRefCodeValues(pCode);
		}
		catch (MemoryDBException e)
		{
		}
		return lRefCodeValueBeans;
	}
	
	public boolean getIsOtpSmsEnabled(){
		return otpSms;
	}
	public boolean getIsOtpEmailEnabled(){
		return otpEmail;
	}
	
	public String getFormattedDate(Date pDate)
	{
		if(pDate!=null)
			return  CommonUtilities.getDisplay(FORMAT_DATE, pDate);
		return "";
	}
	
	public String getCleanData(Object pData)
	{
		try
		{
			if(pData != null)
				return StringEscapeUtils.escapeXml(pData.toString());
		}
		catch(Exception lEx)
		{
			
		}
		return "";
	}

	public String getUserEmail(Long pAuId) throws Exception {
		return getUserEmail(pAuId, false);
	}
	private String getUserEmail(Long pAuId, Boolean pActiveOnly) throws Exception {
		if(pAuId==null || pAuId.longValue() == 0) return null;
	    MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	    AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
	    if(pActiveOnly && lAppUserBean !=null && !IAppUserBean.Status.Active.equals(lAppUserBean.getStatus())) {
	    	lAppUserBean = null;
	    }
	    return lAppUserBean==null?null:lAppUserBean.getEmail();
	}
	
	private String getAdminUserEmail(Long pAuId) throws Exception {
		return getAdminUserEmail(pAuId, true);
	}
	private String getAdminUserEmail(Long pAuId, Boolean pActiveOnly) throws Exception {
		if(pAuId==null || pAuId.longValue() == 0) return null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
        if (lAppUserBean != null) {
            AppUserBean lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
                    new String[]{lAppUserBean.getDomain(), AppConstants.LOGINID_ADMIN});
            if (lAdminUserBean != null) {
            	if(!pActiveOnly || IAppUserBean.Status.Active.equals(lAdminUserBean.getStatus())) {
            		return lAdminUserBean.getEmail();
            	}
            }
        }
        return null;
	}
	public String getUserMobileNo(Long pAuId) throws Exception {
		if(pAuId==null || pAuId.longValue() == 0) return null;
	    MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	    AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
	    return lAppUserBean==null?null:lAppUserBean.getMobile();
	}
    public String getAdminUserEmail(String pEntityCode) throws Exception {
    	return getAdminUserEmail(pEntityCode, false);
    }
    public String getAdminUserEmail(String pEntityCode, Boolean pActiveOnly) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
                new String[]{pEntityCode, AppConstants.LOGINID_ADMIN});
	    if(pActiveOnly && lAdminUserBean !=null && !IAppUserBean.Status.Active.equals(lAdminUserBean.getStatus())) {
	    	lAdminUserBean = null;
	    }
        return lAdminUserBean==null?null:lAdminUserBean.getEmail();
    }
    public AppUserBean getAdminUser(String pEntityCode) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
                new String[]{pEntityCode, AppConstants.LOGINID_ADMIN});
        return lAdminUserBean;
    }

    public Map<String,List<String>> getEmails(Connection pConnection, List<NotificationInfo> pNotificationInfos ) throws Exception {
    	return getEmails(pConnection, pNotificationInfos, false);
    }
    private Map<String,List<String>> getEmails(Connection pConnection, List<NotificationInfo> pNotificationInfos, Boolean pActiveOnly) throws Exception {
    	Map<String,List<String>> lEmailSenderwise = new HashMap<String, List<String>>();
    	if(pNotificationInfos!=null){
	        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
    		AppEntityBean lEntityBean = null;
    		String lEntityCode = null;
    		Long lEntityAuId = null;
            AppUserBean lAdminUserBean = null, lAppUserBean = null; 
            Map<String,EntityNotificationSettingBean> lENSBeanHash = new HashMap<String, EntityNotificationSettingBean>();
            
    		for(NotificationInfo lNotificationInfo : pNotificationInfos){
	    		if(lNotificationInfo.getNotificationType() == null){
	    			logger.info("EXCEPTIONAL CONDITION TredsHelper.getEmails - lENSBean is coming null for notification type null.");
	    			continue;
	    		}
    	    	List<String> lEmails = new ArrayList<String>();
    			lEntityAuId = null;
    			lEntityCode = null;
    			lEntityBean = null;
    			if(lNotificationInfo.getEntity() instanceof Long){
    				lEntityAuId = (Long) lNotificationInfo.getEntity();
    			}else if(lNotificationInfo.getEntity() instanceof String){
    				lEntityCode = (String) lNotificationInfo.getEntity();
    			}
    			if(EntityEmail.AdminEmail.equals(lNotificationInfo.getEntityEmail())){
    				//admin entity email
    				if(!StringUtils.isEmpty(lEntityCode)){
    		            lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
    		                    new String[]{lEntityCode, AppConstants.LOGINID_ADMIN});
    				}else if (lEntityAuId != null){
    					lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lEntityAuId});
    					if(lAppUserBean != null){
    						lEntityCode = lAppUserBean.getDomain();
        		            lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
        		                    new String[]{lEntityCode, AppConstants.LOGINID_ADMIN});
    					}
    				}
    			}else if(EntityEmail.UserEmail.equals(lNotificationInfo.getEntityEmail())){
    				if(!StringUtils.isEmpty(lEntityCode)){
    					//this case is not possible
    				}else if (lEntityAuId != null){
    					lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lEntityAuId});
    					lEntityCode = lAppUserBean.getDomain();
    				}
    			}else if(EntityEmail.NOAEmail.equals(lNotificationInfo.getEntityEmail()) ||
    					EntityEmail.Explicit.equals(lNotificationInfo.getEntityEmail()) ){
    				//noa works and explicit on entity code - hence we need tofind entity code
    				if(!StringUtils.isEmpty(lEntityCode)){
    					//no need to find entity code
    				}else if (lEntityAuId != null){
    					lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lEntityAuId});
    					if(lAppUserBean != null){
    						lEntityCode = lAppUserBean.getDomain();
        		            lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
        		                    new String[]{lEntityCode, AppConstants.LOGINID_ADMIN});
    					}
    				}
    			}
    			if (StringUtils.isEmpty(lEntityCode)){
    				continue;
    			}
    	    	//find the notification settings.
    	    	EntityNotificationSettingBean lENSBean = null;
    	    	String lENSKey = lEntityCode + CommonConstants.KEY_SEPARATOR + lNotificationInfo.getNotificationType();
    	    	if(lENSBeanHash.containsKey(lENSKey)){
    	    		lENSBean = lENSBeanHash.get(lENSKey);
    	    	}else{
    	    		lENSBean = OtherResourceCache.getInstance().getEntityNotificationSettings(pConnection, lEntityCode, lNotificationInfo.getNotificationType());
    	    		lENSBeanHash.put(lENSKey, lENSBean);
    	    	}
    	    	boolean lImplicitMails = true, lExplicitMails = true;
    	    	if(lENSBean!=null){
    	    		lImplicitMails = (MailerType.Both.equals(lENSBean.getMailerType()) || MailerType.Implicit.equals(lENSBean.getMailerType()));
    	    		lExplicitMails = (MailerType.Both.equals(lENSBean.getMailerType()) || MailerType.Explicit.equals(lENSBean.getMailerType()));
    	    	}
    	    	//
    	    	// from the ENSBean - to check whether the Role has to be checked against the user 
    	    	// each user check whether has role 
    	    	if(EntityEmail.RoleBased.equals(lNotificationInfo.getEntityEmail())){
	    			if(OtherResourceCache.getInstance().getCategoryWiseRoles()==null){
	    				logger.info("Error in getEmails : getCategoryWiseRoles is null");
	    			}
	    			List<Long> lRoles = (List<Long>) OtherResourceCache.getInstance().getCategoryWiseRoles().get(lNotificationInfo.getNotificationType());
	    			if(lRoles!=null && !lRoles.isEmpty()){
	    				for(Long lRoleId : lRoles){
        	    			List<AppUserBean> lUsers = TredsHelper.getInstance().getRoleBasedUsers((String)lNotificationInfo.getEntity(), lRoleId);
        	    			for(AppUserBean lUser : lUsers){
        	    				if(!pActiveOnly || IAppUserBean.Status.Active.equals(lUser.getStatus())) {
            	    				List<Long> lLocationIds = lUser.getLocationIdList();
            	    				if( (lLocationIds!=null && lLocationIds.contains(lNotificationInfo.getLocationId())) ||
            	    					lLocationIds == null ){
        	            	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
        	            	        	if(lEmails==null){
        	            	        		lEmails = new ArrayList<String>();
        	            	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
        	            	        	}
        	            	        	lEmails.add(lUser.getEmail());
            	    				}
        	    				}
        	    			}
	    				}
	    			}
	    		}
    	    	if(lImplicitMails){
    	    		if(EntityEmail.AdminEmail.equals(lNotificationInfo.getEntityEmail()) && 
        	    		lAdminUserBean != null && !StringUtils.isEmpty(lAdminUserBean.getEmail())){
    	    			if(!pActiveOnly || IAppUserBean.Status.Active.equals(lAdminUserBean.getStatus())) {
            	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
            	        	if(lEmails==null){
            	        		lEmails = new ArrayList<String>();
            	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
            	        	}
            	        	lEmails.add(lAdminUserBean.getEmail());
    	    			}
        			}else if(EntityEmail.UserEmail.equals(lNotificationInfo.getEntityEmail()) && 
        					lAppUserBean != null && !StringUtils.isEmpty(lAppUserBean.getEmail())){
    	    			if(!pActiveOnly || IAppUserBean.Status.Active.equals(lAppUserBean.getStatus())) {
	        	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
	        	        	if(lEmails==null){
	        	        		lEmails = new ArrayList<String>();
	        	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
	        	        	}
	        	        	lEmails.add(lAppUserBean.getEmail());
    	    			}
        			}else if(EntityEmail.NOAEmail.equals(lNotificationInfo.getEntityEmail())){
        				if(StringUtils.isNotEmpty(lEntityCode)){
            	        	String lNOAEmail = TredsHelper.getInstance().getCompanyAdminNOAEmail(pConnection, lEntityCode);
            	        	if(!StringUtils.isEmpty(lNOAEmail)){
                	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
                	        	if(lEmails==null){
                	        		lEmails = new ArrayList<String>();
                	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
                	        	}
                	        	lEmails.add(lNOAEmail);
            	        	}
        				}
        			}else if(EntityEmail.UserDefined.equals(lNotificationInfo.getEntityEmail())){
        				if(StringUtils.isNotEmpty(lEntityCode)){
        					lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
            	        	if(lEmails==null){
            	        		lEmails = new ArrayList<String>();
            	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
            	        	}
            	        	lEmails.addAll(lNotificationInfo.getEmails());
        				}
        			}
    	    	}
    			//For sending emails to RM and RSM of the Entity as per the notification settings
    			if(StringUtils.isNotEmpty(lEntityCode)) {
    				lEntityBean = TredsHelper.getInstance().getAppEntityBean(lEntityCode);
    				if(lEntityBean!=null) {
    					if(lENSBean!=null && CommonAppConstants.YesNo.Yes.equals(lENSBean.getMailRm())) {
        					if(lEntityBean.getRmUserId()!=null) {
        						AppUserBean lRMUserBean = TredsHelper.getInstance().getAppUser(lEntityBean.getRmUserId());
        						if(lRMUserBean!=null && StringUtils.isNotEmpty(lRMUserBean.getEmail()) ) {
        	        	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
        	        	        	if(lEmails==null){
        	        	        		lEmails = new ArrayList<String>();
        	        	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
        	        	        	}
        	        	        	//to avoid duplicates
        	        	        	if(!lEmails.contains(lRMUserBean.getEmail())) {
        	        	        		lEmails.add(lRMUserBean.getEmail());
        	        	        	}
        						}
        					}
        					if(lEntityBean.getRsmUserId()!=null) {
        						AppUserBean lRSMUserBean = TredsHelper.getInstance().getAppUser(lEntityBean.getRsmUserId());
        						if(lRSMUserBean!=null && StringUtils.isNotEmpty(lRSMUserBean.getEmail()) ) {
        	        	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
        	        	        	if(lEmails==null){
        	        	        		lEmails = new ArrayList<String>();
        	        	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
        	        	        	}
        	        	        	//to avoid duplicates
        	        	        	if(!lEmails.contains(lRSMUserBean.getEmail())) {
        	        	        		lEmails.add(lRSMUserBean.getEmail());
        	        	        	}
        						}
        					}
    					}
    				}
    			}
    			//
    	    	//WE HAVE CONFIGURED SENDING OF EXPLICIT MAILS BY SPECIFYING THE MAIL TYPE AS EXPLICIT
    	    	//BEACUSE THERE ARE MULTIPLE IMPLICIT MAILS
    	    	if (EntityEmail.Explicit.equals(lNotificationInfo.getEntityEmail()) ||
    	    			EntityEmail.RoleBased.equals(lNotificationInfo.getEntityEmail()) 	){
        	    	if(lExplicitMails){
            	    	if(lENSBean!= null && lENSBean.getEmailList()!=null && !lENSBean.getEmailList().isEmpty()){
            	        	lEmails = lEmailSenderwise.get(lNotificationInfo.getEmailSenders().getCode());
            	        	if(lEmails==null){
            	        		lEmails = new ArrayList<String>();
            	            	lEmailSenderwise.put(lNotificationInfo.getEmailSenders().getCode(), lEmails);
            	        	}
                        	lEmails.addAll(lENSBean.getEmailList());
            			}
        	    	}
    	    	}
    		}
    	}
    	return lEmailSenderwise;
    }    
    
    public void setEmailsToData(Map<String,List<String>> pEmailIds, Map<String, Object> pDataValues){
        if (!pEmailIds.isEmpty()) {
        	for(String lEmailSenderKey : pEmailIds.keySet()){
                pDataValues.put(lEmailSenderKey, pEmailIds.get(lEmailSenderKey));
        	}
        }
    }
    
    public void mergeMails(Map<String,List<String>> pMainList, Map<String,List<String>> pIncrementalList){
    	if(pIncrementalList!=null){
    		List<String> lMainList = null, lIncrementalList = null;
        	for(String lKey : pIncrementalList.keySet()){
        		lMainList = pMainList.get(lKey);
        		lIncrementalList = pIncrementalList.get(lKey);
        		if(lMainList == null){
        			pMainList.put(lKey, lIncrementalList);
        		}else{
        			lMainList.addAll(lIncrementalList);
        		}
        	}
    	}
    }

    
    public String getAdminUserMobileNo(String pEntityCode) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lAdminUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
                new String[]{pEntityCode, AppConstants.LOGINID_ADMIN});
        return lAdminUserBean==null?null:lAdminUserBean.getMobile();
    }

    public List<String> getCompanyContactAdminEmail(Connection pConnection, String pEntityCode) throws Exception {
    	return getCompanyPersonEmail(pConnection, pEntityCode, true, false);
    }
    public List<String> getCompanyContactAuthPersonsEmail(Connection pConnection, String pEntityCode) throws Exception {
    	return getCompanyPersonEmail(pConnection, pEntityCode, false, true);
    }
    private List<String> getCompanyPersonEmail(Connection pConnection, String pEntityCode, boolean pAdmin, boolean pAuthorisedPerson) throws Exception {
        List<String> lMailIds = new  ArrayList<String>();
    	AppEntityBean lAppEntityBean = getAppEntityBean(pEntityCode);
		if(lAppEntityBean!=null && lAppEntityBean.getCdId()!=null)
    	{
    		CompanyContactBean lCCBean = null;
    		GenericDAO<CompanyContactBean> lCCDAO = null;
    		try
			{
        		List<String> lColumns = null;
    			lCCDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        		lCCBean = new CompanyContactBean();
        		lCCBean.setCdId(lAppEntityBean.getCdId());
        		if(pAdmin){
            		lCCBean.setAdmin(Yes.Yes);
    				lCCBean = lCCDAO.findBean(pConnection, lCCBean);						
    	    		if(lCCBean!=null && lCCBean.getEmail()!=null)
    	    			lMailIds.add(lCCBean.getEmail());
        		}else if (pAuthorisedPerson){
            		lCCBean.setAuthPer(Yes.Yes);
    				List<CompanyContactBean> lCCBeans = lCCDAO.findList(pConnection, lCCBean, lColumns);
    				for(CompanyContactBean lBean : lCCBeans){
    					if(CommonUtilities.hasValue(lBean.getEmail())){
            				lMailIds.add(lBean.getEmail());
    					}
    				}
        		}
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
				e.printStackTrace();
				lCCBean = null;
	        }
    	}
        return lMailIds;
    }
    
    public String getCompanyAdminNOAEmail(Connection pConnection, String pEntityCode) throws Exception {
    	AppEntityBean lAppEntityBean = getAppEntityBean(pEntityCode);
		if(lAppEntityBean!=null && lAppEntityBean.getCdId()!=null)
    	{
    		CompanyContactBean lCCBean = null;
    		GenericDAO<CompanyContactBean> lCCDAO = null;
    		try
			{
    			lCCDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        		lCCBean = new CompanyContactBean();
        		lCCBean.setCdId(lAppEntityBean.getCdId());
        		lCCBean.setAdmin(Yes.Yes);
				lCCBean = lCCDAO.findBean(pConnection, lCCBean);						
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
				e.printStackTrace();
				lCCBean = null;
	        }finally{
	        }
    		if(lCCBean!=null && lCCBean.getNoaEmail()!=null)
    			return lCCBean.getNoaEmail();
    	}
        return null;
    }

    public CompanyDetailBean getCompanyDetails(Connection pConnection, Long pCompanyId, boolean pIsProvisional){
		CompanyDetailBean lCDBean = new CompanyDetailBean();
		lCDBean.setId(pCompanyId);
		try {
			if(pIsProvisional) {
				lCDBean = companyDetailProvDAO.findByPrimaryKey(pConnection, lCDBean);
			}else {
				lCDBean = companyDetailDAO.findByPrimaryKey(pConnection, lCDBean);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lCDBean = null;
		}
		return lCDBean;
    }

    public boolean hasAccessOnCompany(Connection pConnection, Long pCompanyId, AppUserBean pLoggedInUserBean) throws Exception {
    	if(pCompanyId!=null){
    		if(AppConstants.DOMAIN_PLATFORM.equals(pLoggedInUserBean.getDomain())){
    			return true;
    		}
			CompanyDetailBean lCDBean = new CompanyDetailBean();
			lCDBean.setId(pCompanyId);
			lCDBean = companyDetailDAO.findByPrimaryKey(pConnection, lCDBean);
			if(lCDBean==null){
				lCDBean = new CompanyDetailBean();
				lCDBean.setId(pCompanyId);
				lCDBean = companyDetailProvDAO.findByPrimaryKey(pConnection, lCDBean);
			}
    		if(AppConstants.DOMAIN_REGENTITY.equals(pLoggedInUserBean.getDomain())){
    			if(!pLoggedInUserBean.getId().equals(lCDBean.getRecordCreator())){
    				return false;//throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    			}
    			return true;
    		}else if(AppConstants.DOMAIN_REGUSER.equals(pLoggedInUserBean.getDomain())){
    			if(!pLoggedInUserBean.getId().equals(lCDBean.getId())){
    				return false;//throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    			}
    			return true;
    		}else {
    			if(pLoggedInUserBean.getDomain().equals(lCDBean.getCode())) {
    				return false;//throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    			}
    			return true;
    		}
    	}
    	return false;
    }
    
    public String getRegistrationNo(Connection pConnection, Long pCompanyId)
    {
    	if(pCompanyId!=null)
    	{
    		CompanyDetailBean lCDBean = null;
    		GenericDAO<CompanyDetailBean> lCDProvDAO = null;
    		try
			{
    			lCDProvDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class, CompanyDetailBO.TABLENAME_PROV);
        		lCDBean = new CompanyDetailBean();
        		lCDBean.setId(pCompanyId);
				lCDBean = lCDProvDAO.findByPrimaryKey(pConnection, lCDBean);
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
				e.printStackTrace();
				lCDBean = null;
	        }finally{
	        }
    		if(lCDBean!=null && lCDBean.getRegistrationNo()!=null)
    			return lCDBean.getRegistrationNo();
    	}
    	return "";
    }
    public Date getSettlementDate() {
        try
		{
			ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
            if (lConfirmationWindowBean != null) {
                return lConfirmationWindowBean.getSettlementDate();
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
    }
    
    public Long getTenure(Date pMaturityDate) {
		if(pMaturityDate!=null){
	        try
			{
				ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
	            if (lConfirmationWindowBean != null) {
	                java.sql.Date lLeg1Date = lConfirmationWindowBean.getSettlementDate();
	                java.sql.Date lLeg2Date = pMaturityDate;
	                BigDecimal lDuration = BigDecimal.valueOf((lLeg2Date.getTime() - lLeg1Date.getTime())/86400000);
	            	return new Long(lDuration.longValue());
	            }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
    }
    public Long getTenure(Date pLeg2Date, Date pLeg1Date) {
		if(pLeg2Date!=null && pLeg1Date!=null){
	        try
			{
                BigDecimal lDuration = BigDecimal.valueOf((pLeg2Date.getTime() - pLeg1Date.getTime())/86400000);
            	return new Long(lDuration.longValue());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
    }

    public Long getFactoringUnitId(Connection pConnection) throws Exception{
    	//"yyD", "FactoringUnits.id.", 7
        AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        Date lBusinessDate = lAuctionCalendarBean==null?null:lAuctionCalendarBean.getDate();
        String lStrDate = "";
        lStrDate = FormatHelper.getDisplay("yy", lBusinessDate==null?lCurrentTime:lBusinessDate); //years last 2 digits
        lStrDate += StringUtils.leftPad(FormatHelper.getDisplay("D", lBusinessDate==null?lCurrentTime:lBusinessDate),3,"0");//day of year 001 to 366
        Long lId = Long.valueOf("1" + lStrDate + StringUtils.leftPad(DBHelper.getInstance().getUniqueNumber(pConnection, "FactoringUnits.id." + lStrDate).toString(), 7, "0"));
        return lId;
    }

    public Long getNextId(Connection pConnection, String pDateFormat, String pEntityName,int pPaddingLen) throws Exception{
        AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        Date lBusinessDate = lAuctionCalendarBean==null?null:lAuctionCalendarBean.getDate();
        String lStrDate = FormatHelper.getDisplay(pDateFormat, lBusinessDate==null?lCurrentTime:lBusinessDate);
        Long lId = Long.valueOf(lStrDate + StringUtils.leftPad(DBHelper.getInstance().getUniqueNumber(pConnection, pEntityName + lStrDate).toString(), pPaddingLen, "0"));
        return lId;
    }
    
    public Long getObligationId(Connection pConnection) throws Exception {
    	//"yyD", "Obligations.id.", 7
        AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        Date lBusinessDate = lAuctionCalendarBean==null?null:lAuctionCalendarBean.getDate();
        String lStrDate = "";
        lStrDate = FormatHelper.getDisplay("yy", lBusinessDate==null?lCurrentTime:lBusinessDate); //years last 2 digits
        lStrDate += StringUtils.leftPad(FormatHelper.getDisplay("D", lBusinessDate==null?lCurrentTime:lBusinessDate),3,"0");//day of year 001 to 366
        Long lId = Long.valueOf("1" + lStrDate + StringUtils.leftPad(DBHelper.getInstance().getUniqueNumber(pConnection, "Obligations.id." + lStrDate).toString(), 7, "0"));
        return lId;
    }
    
    public Date getBusinessDate(){
        AuctionCalendarBean lAuctionCalendarBean = null;
        Date lBusinessDate = null;
        try {
			lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
	        lBusinessDate = lAuctionCalendarBean==null?null:lAuctionCalendarBean.getDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return lBusinessDate;
    }
    
    public String getGeneratedPassword(){
    	String lNewPassword = "";
	    RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        String[] lCharSet = lRegistryHelper.getStringList(AuthenticationHandler.REGISTRY_PASSWORD_CHAR_SET);
        Long lTemp = null;
        long lMinLen=0, lMaxLen=0, lPwdLen=0, lCharSetLen = 0;
        
        if(lCharSet==null||lCharSet.length==0){
        	lCharSet = lRegistryHelper.getStringList(AuthenticationHandler.REGISTRY_PASSWORD_CHAR_SUPERSET);
        }
        lTemp = lRegistryHelper.getLong(AuthenticationHandler.REGISTRY_PASSWORD_MIN_LENGTH);
        if(lTemp!=null) lMinLen = lTemp.longValue();
        lTemp = lRegistryHelper.getLong(AuthenticationHandler.REGISTRY_PASSWORD_MAX_LENGTH);
        if(lTemp!=null) lMaxLen = lTemp.longValue();
        lMaxLen = 12; //Hardcoded Since the LoginBean.json restricts the password length to twelve
        
        lPwdLen = lMinLen + (int)(Math.random() * (lMaxLen-lMinLen));
        if(lPwdLen > lMaxLen) lPwdLen = lMaxLen;
        lCharSetLen = lCharSet.length;
        
        int lIndex =0;
        int lCharSetIndex=0;
        for (int lPtr = 0; lPtr < lPwdLen; lPtr++)
        {
        	lIndex = (int)(Math.random()*lCharSetLen);
        	lCharSetIndex = (int)(Math.random()*lCharSet[lIndex].length());
        	lNewPassword += lCharSet[lIndex].substring(lCharSetIndex,lCharSetIndex+1);
        }
    	return lNewPassword;
    }

    public AppEntityBean getAppEntityBean(String pCode) throws MemoryDBException {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        return (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pCode});
    }

    public String getCompanyPAN(String pDomain){
        String lRetVal = "";
        try{
            AppEntityBean lAppEntityBean = getAppEntityBean(pDomain);
            if ((lAppEntityBean != null) && (lAppEntityBean.getPan() != null))
                lRetVal = lAppEntityBean.getPan();
    	} catch(Exception lException) {
    	}
        return lRetVal;
    }
    
    public Long getCompanyId(IAppUserBean pAppUserBean) {
    	Long lCompanyId = new Long(0);
    	try
    	{
    		if(AppConstants.DOMAIN_REGUSER.equals(pAppUserBean.getDomain())) {
    			lCompanyId = pAppUserBean.getId();
    		}else{
    			AppEntityBean lAppEntityBean = getAppEntityBean(pAppUserBean.getDomain());
    			if(lAppEntityBean!=null && lAppEntityBean.getCdId()!=null){
    				lCompanyId = lAppEntityBean.getCdId();
    			}
    		}    			
    	}catch(Exception lException){
    		lCompanyId = new Long(0);
    	}
    	return lCompanyId;
    }
    
    public String getFormattedAmount(Object pAmount, boolean pShowCommas){
    	String lRetVal = "";
    	if(pAmount!=null){
    		BigDecimal lAmount = null;
    		try{
    			lAmount = (BigDecimal) pAmount;
                lRetVal = FormatHelper.getDisplay(pShowCommas?FORMAT_AMOUNT_COMMA:FORMAT_AMOUNT, lAmount);
    		}catch(Exception lException){
    			logger.error("Error while formatting amount.");
    		}
    	}
    	return lRetVal;
    }
    
	public String getContactFullName(CompanyContactBean pCompanyContactBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyContactBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyContactBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyContactBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyContactBean.getSalutation()))
				lName.append(pCompanyContactBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyContactBean.getFirstName()))
				lName.append(" ").append(pCompanyContactBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyContactBean.getMiddleName()))
				lName.append(" ").append(pCompanyContactBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyContactBean.getLastName()))
				lName.append(" ").append(pCompanyContactBean.getLastName());
		}
		return lName.toString();
	}
	
	public String getContactEntityFullName(CompanyShareEntityBean pCompanyShareEntityBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyShareEntityBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyShareEntityBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyShareEntityBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getSalutation()))
				lName.append(pCompanyShareEntityBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getFirstName()))
				lName.append(" ").append(pCompanyShareEntityBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getMiddleName()))
				lName.append(" ").append(pCompanyShareEntityBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getLastName()))
				lName.append(" ").append(pCompanyShareEntityBean.getLastName());
		}
		return lName.toString();
	}
	
	public boolean isTransactionableBidType(BidBean.BidType pBidType, AppConstants.AutoAcceptableBidTypes pAcceptableBidTypes){
		if(AppConstants.AutoAcceptableBidTypes.AllBids.equals(pAcceptableBidTypes)){
			if(pBidType!=null) return true;
		}else if(AppConstants.AutoAcceptableBidTypes.OpenBids.equals(pAcceptableBidTypes)){
			if(BidType.Open.equals(pBidType)) return true;
		}
		return false;
	}
	
	public Date getNEFTReversalDate(){
		Date lDate = TredsHelper.getInstance().getBusinessDate();
		Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();
		if(lDate.equals(lCurrentDate)){
			Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
			Timestamp lCutoffTime = null;
			Long lLongTime = RegistryHelper.getInstance().getTime("server.settings.neftcutofftime");
			if(lLongTime!=null){
				Calendar lCalendar = Calendar.getInstance();		
				lCalendar.setTimeInMillis(lLongTime.longValue());
				lCutoffTime = new Timestamp(lCalendar.getTimeInMillis()-lCalendar.getTimeZone().getRawOffset());
				lCutoffTime = CommonUtilities.copyTimeToCurrent(lCutoffTime);
			}
			if(lCutoffTime!=null){
				if(lCurrentTime.after(lCutoffTime)){
					try {
						lDate = OtherResourceCache.getInstance().getWorkingDate(lDate, false, true, 1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}else if (lCurrentDate.after(lDate)){
			try {
				lDate = OtherResourceCache.getInstance().getWorkingDate(lDate, false, true, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lDate;
	}

    public CompanyBankDetailBean getTredsAccount(){
    	//TODO: Logic to switch on the basis of facilitator will come here (facilitator from paymentfilebean to be taken
        HashMap<String,Object> lSettings = RegistryHelper.getInstance().getStructure(REGISTRY_ICICITREDSACCOUNT);
        String lUserName = (String)lSettings.get(ATTRIBUTE_NACHUSERNAME);
        String lSponsorBankIfsc = (String)lSettings.get(ATTRIBUTE_NACHSPONSORBANKIFSC);
        String lUserNumber = (String)lSettings.get(ATTRIBUTE_NACHUSERNUMBER); //this is given by facilitator npci
        String lTredsAccountNo = (String)lSettings.get(ATTRIBUTE_NACHPOOLACCOUNTNO); 
        CompanyBankDetailBean lTredsBankDetailBean = new CompanyBankDetailBean();
        lTredsBankDetailBean.setId(new Long(0));
        lTredsBankDetailBean.setCdId(new Long(0));
        lTredsBankDetailBean.setAccNo(lTredsAccountNo);
        lTredsBankDetailBean.setIfsc(lSponsorBankIfsc);;
        lTredsBankDetailBean.setFirstName(lUserName);
        lTredsBankDetailBean.setLastName(lUserNumber);
        return lTredsBankDetailBean;
    }

    public CompanyBankDetailBean getTredsChargeAccount(){
    	//TODO: Logic to switch on the basis of facilitator will come here (facilitator from paymentfilebean to be taken
        HashMap<String,Object> lSettings = RegistryHelper.getInstance().getStructure(REGISTRY_TREDSCHARGEACCOUNT);
        String lUserName = (String)lSettings.get(ATTRIBUTE_NACHUSERNAME);
        String lSponsorBankIfsc = (String)lSettings.get(ATTRIBUTE_NACHSPONSORBANKIFSC); 
        String lUserNumber = (String)lSettings.get(ATTRIBUTE_NACHUSERNUMBER); //this is given by facilitator npci
        String lTredsAccountNo = (String)lSettings.get(ATTRIBUTE_NACHPOOLACCOUNTNO); 
        CompanyBankDetailBean lTredsBankDetailBean = new CompanyBankDetailBean();
        lTredsBankDetailBean.setId(new Long(0));
        lTredsBankDetailBean.setCdId(new Long(0));
        lTredsBankDetailBean.setAccNo(lTredsAccountNo);
        lTredsBankDetailBean.setIfsc(lSponsorBankIfsc);;
        lTredsBankDetailBean.setFirstName(lUserName);
        lTredsBankDetailBean.setLastName(lUserNumber);
        return lTredsBankDetailBean;
    }
    
    public String getSalesCategoryDescription(String pSalesCategory){
        //fetch RefCode for - SECTOR and MSMESTATUS, concatinate with hifen
        ArrayList<RefCodeValuesBean> lSectors = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_SECTOR);
        ArrayList<RefCodeValuesBean> lMSMEStatus = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_MSMESTATUS);
        String lCategory = null;
        for(RefCodeValuesBean lSector : lSectors){
            for(RefCodeValuesBean lStatus : lMSMEStatus){
            	lCategory = lSector.getValue() + CommonConstants.KEY_SEPARATOR + lStatus.getValue();
            	if(lCategory.equals(pSalesCategory))
            		return lSector.getDesc() + " - " + lStatus.getDesc();
            }        	
        }
        return "";
    }
    
    public String getBankBranch(Connection pConnection, String pIFSCCode) throws Exception {
    	String lBankBranch = "";
    	if(CommonUtilities.hasValue(pIFSCCode) && pIFSCCode.trim().length() > 4){
    		String lBankCode = pIFSCCode.substring(0, 4);
    		lBankBranch = TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lBankCode);
    		if(!CommonUtilities.hasValue(lBankBranch)){
                return "Invalid Bank Code."; //throw new CommonBusinessException("Invalid Bank Code.");
    		}
            BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
            lFilterBean.setIfsc(pIFSCCode);
            BankBranchDetailBean lBankBranchDetailBean = (new GenericDAO<BankBranchDetailBean>(BankBranchDetailBean.class)).findByPrimaryKey(pConnection, lFilterBean);
            if (lBankBranchDetailBean == null) 
                return "Invalid IFSC Code."; //throw new CommonBusinessException("Invalid IFSC Code.");
            lBankBranch = lBankBranch + " - " + lBankBranchDetailBean.getBranchname();
       }
    	return lBankBranch;
    }
    public HashMap<String, String> getBankName() throws Exception {
    	HashMap<String ,String> lBankhash = new HashMap<>();
    	ArrayList<RefCodeValuesBean> lBank = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_BANK);
    	for(RefCodeValuesBean lRCVBen : lBank){
    		lBankhash.put(lRCVBen.getValue(), lRCVBen.getDesc());
    	}
    	return lBankhash;
    }
    
    public String getDesignatedBankAccountNumber(Connection pConnection, String pEntityCode){
    	if(!CommonUtilities.hasValue(pEntityCode)) return "";
    	CompanyBankDetailBean lDesignatedBank = null;
    	AppEntityBean lAppEntityBean = null;
    	Long lCdId = null;
    	try{
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
        	if(lAppEntityBean!=null){
                lCdId = lAppEntityBean.getCdId();
                GenericDAO<CompanyBankDetailBean> lCompanyBankDetailsDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
                CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
                lFilterBean.setCdId(lCdId);
                lFilterBean.setDefaultAccount(CommonAppConstants.Yes.Yes);
                lDesignatedBank = lCompanyBankDetailsDAO.findBean(pConnection, lFilterBean);
                if (lDesignatedBank != null){
                	return lDesignatedBank.getAccNo();
                }else{
            		logger.info("getDesignatedBankAccountNumber Designated bank not found : " + lCdId);
                }
        	}else{
        		logger.info("getDesignatedBankAccountNumber Entity Code not found : " + pEntityCode);
        	}
    	}catch(Exception lEx){
    		logger.error("Error while getting Designatedbank for : "+pEntityCode,lEx);
    	}
    	return "";
    }  
    public Long getDesignatedBankId(Connection pConnection, String pEntityCode){
    	if(!CommonUtilities.hasValue(pEntityCode)) return null;
    	CompanyBankDetailBean lDesignatedBank = null;
    	AppEntityBean lAppEntityBean = null;
    	Long lCdId = null;
    	try{
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
        	if(lAppEntityBean!=null){
                lCdId = lAppEntityBean.getCdId();
                GenericDAO<CompanyBankDetailBean> lCompanyBankDetailsDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
                CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
                lFilterBean.setCdId(lCdId);
                lFilterBean.setDefaultAccount(CommonAppConstants.Yes.Yes);
                lDesignatedBank = lCompanyBankDetailsDAO.findBean(pConnection, lFilterBean);
                if (lDesignatedBank != null){
                	return lDesignatedBank.getId();
                }else{
            		logger.info("getDesignatedBankAccount Designated bank not found : " + lCdId);
                }
        	}else{
        		logger.info("getDesignatedBankAccount Entity Code not found : " + pEntityCode);
        	}
    	}catch(Exception lEx){
    		logger.info("Error while getting Designatedbank for : "+pEntityCode);
    	}
    	return null;
    }
    public CompanyBankDetailBean getDesignatedBank(Connection pConnection, String pEntityCode){
    	if(!CommonUtilities.hasValue(pEntityCode)) return null;
    	CompanyBankDetailBean lDesignatedBank = null;
    	AppEntityBean lAppEntityBean = null;
    	Long lCdId = null;
    	try{
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
        	if(lAppEntityBean!=null){
                lCdId = lAppEntityBean.getCdId();
                GenericDAO<CompanyBankDetailBean> lCompanyBankDetailsDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
                CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
                lFilterBean.setCdId(lCdId);
                lFilterBean.setDefaultAccount(CommonAppConstants.Yes.Yes);
                lDesignatedBank = lCompanyBankDetailsDAO.findBean(pConnection, lFilterBean);
                if (lDesignatedBank != null){
                	return lDesignatedBank;
                }else{
            		logger.info("getDesignatedBankAccount Designated bank not found : " + lCdId);
                }
        	}else{
        		logger.info("getDesignatedBankAccount Entity Code not found : " + pEntityCode);
        	}
    	}catch(Exception lEx){
    		logger.info("Error while getting Designatedbank for : "+pEntityCode);
    	}
    	return null;
    }
    
    public String getGSTStateDesc(String pGSTStateCode){
    	String lRetVal = null;
    	if(CommonUtilities.hasValue(pGSTStateCode)){
    		if(gstStateCodeDesc==null){
    			gstStateCodeDesc = new HashMap<String, String>();
    	        ArrayList<RefCodeValuesBean> lStates = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_STATE_GST);
    	        for(RefCodeValuesBean lState : lStates){
    	        	gstStateCodeDesc.put(lState.getValue(), lState.getDesc());
    	        }
    		}
    		if(StringUtils.length(pGSTStateCode) == 1){
    			pGSTStateCode = "0"+pGSTStateCode;
    		}
    		lRetVal = gstStateCodeDesc.get(pGSTStateCode);
    	}
    	return lRetVal;
    }
    
    public BigDecimal[] getComputeSplitChargeValue(BidBean pBidBean, FactoringUnitBean pFUBean,Date pLeg1Date){
    	logger.info(" Charge Type : "+com.xlx.treds.AppConstants.ChargeType.Split.toString());
    	BigDecimal lAmount = pFUBean.getFactoredAmount();
		BigDecimal lChargeBearerValue = BigDecimal.ZERO;
		BigDecimal lFinancierChargeValue = BigDecimal.ZERO;
        BigDecimal lTotalChargeValue = new BigDecimal(0);
        BigDecimal lTotalShare = new BigDecimal(0);
        lTotalShare = pBidBean.getSplitChargeBearerPercent().add(pBidBean.getSplitPercent());
        logger.info(" compute charge threshold ");
    	Long lTenor = TredsHelper.getInstance().getTenure(pFUBean.getLeg2MaturityExtendedDate(), pLeg1Date);
    	logger.info("Charge = ((FUAmt*Charge%)/365)*Tenure) where Tenure=(Leg2-Leg1) " );
    	logger.info("FUAmt = "+ lAmount + " Charge = " + lTotalShare + " Tenure = " + lTenor + " Leg2Date : " + pFUBean.getMaturityDate() + " Leg1Date : " + pLeg1Date );
    	lTotalChargeValue = lTotalShare.multiply(lAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128);
    	lTotalChargeValue = lTotalChargeValue.multiply(BigDecimal.valueOf(lTenor)).divide(BigDecimal.valueOf(365.0), MathContext.DECIMAL128);
    	lTotalChargeValue = lTotalChargeValue.setScale(2, RoundingMode.HALF_UP);
    	logger.info("ChargeValue : " + lTotalChargeValue);
    	logger.info("Abs Charge Value : " + pBidBean.getSplitMinCharge());
    	//lTotalChargeValue = lTotalChargeValue.max(pACSBean.getChargeAbsoluteValue());
    	//logger.info("Max Charge Value : " + (pACSBean.getChargeMaxValue()!=null?pACSBean.getChargeMaxValue():""));
    	//
    	logger.info("Total Charge"+lTotalChargeValue);
    	lFinancierChargeValue = pBidBean.getSplitPercent().multiply(lAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128);
    	lFinancierChargeValue = lFinancierChargeValue.multiply(BigDecimal.valueOf(lTenor)).divide(BigDecimal.valueOf(365.0), MathContext.DECIMAL128);
    	lFinancierChargeValue = lFinancierChargeValue.setScale(2, RoundingMode.HALF_UP);
    	logger.info("Fin Charge"+lFinancierChargeValue);
    	lChargeBearerValue = lTotalChargeValue.subtract(lFinancierChargeValue);
    	logger.info("Cb Charge"+lChargeBearerValue);
    	//
    	logger.info("lChargeBearerValue : " + lChargeBearerValue);
    	if(lChargeBearerValue.compareTo(BigDecimal.ZERO) > 0) {
    		lChargeBearerValue = lChargeBearerValue.max(pBidBean.getSplitMinCharge());
    		//FINANCIER SPLITTING - MAX CHARGE IS IGNORED
    	}else {
    		lChargeBearerValue = BigDecimal.ZERO;
    	}
    	//
    	logger.info("lChargeBearerValue : " + lChargeBearerValue);
        return new BigDecimal[] { lFinancierChargeValue , lChargeBearerValue };
    }

    public BigDecimal getComputeChargeValue(ChargeType pChargeType, BigDecimal pAbsoluteValue, BigDecimal pChargePercent, BigDecimal pChargeMaxValue, FactoringUnitBean pFUBean, BigDecimal pAmount){
    	Long lTenor = TredsHelper.getInstance().getTenure(pFUBean.getLeg2MaturityExtendedDate(), pFUBean.getLeg1Date());
    	logger.info("FUAmt = "+ pAmount + " Charge = " + pChargePercent + " Tenure = " + lTenor + " Leg2Date : " + pFUBean.getMaturityDate() + " Leg1Date : " + pFUBean.getLeg1Date() );
    	return getComputeChargeValue(pChargeType, pAbsoluteValue, pChargePercent, pChargeMaxValue, lTenor, pAmount);
    }

    public BigDecimal getComputeChargeValue(ChargeType pChargeType, BigDecimal pAbsoluteValue, BigDecimal pChargePercent, BigDecimal pChargeMaxValue, Long pTenor, BigDecimal pAmount){
        BigDecimal lChargeValue = new BigDecimal(0);
        logger.info("Charge Type : " + com.xlx.treds.AppConstants.ChargeType.Normal);
        if(ChargeType.Absolute.equals(pChargeType)){
            lChargeValue = pAbsoluteValue;
            logger.info(" compute charge aboslute " + (pAbsoluteValue!=null?pAbsoluteValue:""));
        }
        else if(ChargeType.Percentage.equals(pChargeType)){
        	logger.info("Charge = ((FUAmt*Charge%)/365)*Tenure) where Tenure=(Leg2-Leg1) " );
        	lChargeValue = pChargePercent.multiply(pAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128);
        	lChargeValue = lChargeValue.multiply(BigDecimal.valueOf(pTenor)).divide(BigDecimal.valueOf(365.0), MathContext.DECIMAL128);
        	lChargeValue = lChargeValue.setScale(2, RoundingMode.HALF_UP);
        	logger.info("ChargeValue : " + lChargeValue);
        }
        else if(ChargeType.Threshold.equals(pChargeType)){
        	//lChargeValue =  //platform charges on instrument amount
            ////max of percentage or abosulute
            //lChargeValue = lChargeValue.max(pACSBean.getChargePercentValue());
            //
        	//ChargeFormula = ((FUAmt*Charge%)/365)*Tenure) where Tenure=(Leg2-Leg1)
            logger.info(" compute charge threshold ");
        	logger.info("Charge = ((FUAmt*Charge%)/365)*Tenure) where Tenure=(Leg2-Leg1) " );
        	lChargeValue = pChargePercent.multiply(pAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128);
        	lChargeValue = lChargeValue.multiply(BigDecimal.valueOf(pTenor)).divide(BigDecimal.valueOf(365.0), MathContext.DECIMAL128);
        	lChargeValue = lChargeValue.setScale(2, RoundingMode.HALF_UP);
        	logger.info("ChargeValue : " + lChargeValue);
        	logger.info("Abs Charge Value : " + pAbsoluteValue);
        	lChargeValue = lChargeValue.max(pAbsoluteValue);
        	logger.info("Max Charge Value : " + (pChargeMaxValue!=null?pChargeMaxValue:""));
        	if(pChargeMaxValue != null && pChargeMaxValue.doubleValue() > 0)
        		lChargeValue = lChargeValue.min(pChargeMaxValue);
        	lChargeValue = lChargeValue.setScale(2, RoundingMode.HALF_UP);
        	logger.info("Therefore max of above ChargeValue : " + lChargeValue);
        }
        return lChargeValue;
    }
        
    public String getTredsCIN(){
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
		HashMap<String,Object> lSettings = (HashMap<String,Object>) lRegistryHelper.getStructure(BillBO.REGISTRY_TREDS_BILLINGDETAILS);
		//from the registry fill the above details
		return ((String)lSettings.get(BillBO.ATTRIBUTE_TREDS_CIN));
    }
    
    public String getTredsGSTN(){
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
		HashMap<String,Object> lSettings = (HashMap<String,Object>) lRegistryHelper.getStructure(BillBO.REGISTRY_TREDS_BILLINGDETAILS);
		//from the registry fill the above details
		return ((String)lSettings.get(BillBO.ATTRIBUTE_TREDS_GSTN));
    }
    
    public String getTredsGstState(){
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
		HashMap<String,Object> lSettings = (HashMap<String,Object>) lRegistryHelper.getStructure(BillBO.REGISTRY_TREDS_BILLINGDETAILS);
		//from the registry fill the above details
		return ((String)lSettings.get(BillBO.ATTRIBUTE_TREDS_STATECODE));
    }
    
    public GSTRateBean getGSTRate(Connection pConnection, java.util.Date pDate, BigDecimal pAmount) throws Exception{
		StringBuilder lSql = new StringBuilder();
		GenericDAO<GSTRateBean> lGSTRatesDao = new GenericDAO<GSTRateBean>(GSTRateBean.class);
		lSql.append(" SELECT * FROM GSTRATES WHERE GRRECORDVERSION > 0 ");
		lSql.append(" AND GRFROMDATE <= ").append(DBHelper.getInstance().formatDate(pDate));
		lSql.append(" AND (GRTODATE IS NULL OR GRTODATE >= ").append(DBHelper.getInstance().formatDate(pDate)).append(" ) ");    		
		List<GSTRateBean> lGSTRateBeans = lGSTRatesDao.findListFromSql(pConnection, lSql.toString(), 0);
		if(lGSTRateBeans != null && lGSTRateBeans.size() == 1 ){
			return lGSTRateBeans.get(0);
		}else{
			logger.info("GST Rates not found. Beans Count : " + (lGSTRateBeans!=null?lGSTRateBeans.size():-1) );
			throw new CommonBusinessException("GST Rates not found.");
		}
    }
    
    public BigDecimal[] getCgstSgstIgst(GSTRateBean pGSTRateBean , BigDecimal pAmount, String lChargeBearerSettlementLocationStateCode) throws Exception{
    	BigDecimal[] lRetVals = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
    	if(pAmount != null && CommonUtilities.hasValue(lChargeBearerSettlementLocationStateCode) && pGSTRateBean!=null){
        	//fetch the gst rates and calculate accordingly
			BigDecimal lCharge = null, lSurCharge = null;
			if(getTredsGstState().equals(lChargeBearerSettlementLocationStateCode)){
				//CGST
				lCharge = pAmount.multiply(pGSTRateBean.getCgst()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
				if(pGSTRateBean.getCgstSurcharge().compareTo(BigDecimal.ZERO) > 0)
					lSurCharge = lCharge.multiply(pGSTRateBean.getCgstSurcharge()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
				else
					lSurCharge = BigDecimal.ZERO;
				lRetVals[0] = lCharge.add(lSurCharge);
				logger.info("CGST % : " + pGSTRateBean.getCgst() + " Surcharge % : "+ pGSTRateBean.getCgstSurcharge());
				logger.info("CGST : " + lCharge + " Surcharge : "+ lSurCharge + " Total : " + lRetVals[0]);
				//SGST
				lCharge = pAmount.multiply(pGSTRateBean.getSgst()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
				if(pGSTRateBean.getSgstSurcharge().compareTo(BigDecimal.ZERO) > 0)
					lSurCharge = lCharge.multiply(pGSTRateBean.getSgstSurcharge()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
				else
					lSurCharge = BigDecimal.ZERO;
				lRetVals[1] = lCharge.add(lSurCharge);
				logger.info("SGST % : " + pGSTRateBean.getSgst() + " Surcharge % : "+ pGSTRateBean.getSgstSurcharge());
				logger.info("SGST : " + lCharge + " Surcharge : "+ lSurCharge + " Total : " + lRetVals[1]);
			}else{
				//IGST
    			lCharge = pAmount.multiply(pGSTRateBean.getIgst()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
    			if(pGSTRateBean.getIgstSurcharge().compareTo(BigDecimal.ZERO) > 0)
    				lSurCharge = lCharge.multiply(pGSTRateBean.getIgstSurcharge()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);    				
    			else
    				lSurCharge = BigDecimal.ZERO;
    			lRetVals[2] = lCharge.add(lSurCharge);
    			logger.info("IGST % : " + pGSTRateBean.getSgst() + " Surcharge % : "+ pGSTRateBean.getSgstSurcharge());
    			logger.info("IGST : " + lCharge + " Surcharge : "+ lSurCharge + " Total : " + lRetVals[2]);
			}
    	}
    	return lRetVals;
    }

    public CompanyLocationBean getRegisteredOfficeLocation(Connection pConnection, Long pCdId){
    	return getRegisteredOfficeLocation(pConnection, pCdId, null, false);
    }
    public CompanyLocationBean getRegisteredOfficeLocation(Connection pConnection, String pCdCode){
    	return getRegisteredOfficeLocation(pConnection, null, pCdCode, false);
    }
    public CompanyLocationBean getRegisteredOfficeLocation(Connection pConnection, Long pCdId,boolean pIsProvisional){
    	return getRegisteredOfficeLocation(pConnection, pCdId, null, pIsProvisional);
    }
    public CompanyLocationBean getRegisteredOfficeLocation(Connection pConnection, String pCdCode,boolean pIsProvisional){
    	return getRegisteredOfficeLocation(pConnection, null, pCdCode, pIsProvisional);
    }
    private CompanyLocationBean getRegisteredOfficeLocation(Connection pConnection, Long pCdId, String pCdCode,boolean pIsProvisional){
    	AppEntityBean lAEBean=null;
    	Long lCdId = pCdId;
		try {
			if(lCdId == null) {
				lAEBean = getAppEntityBean(pCdCode);
				lCdId = lAEBean.getCdId();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
    	CompanyLocationBean lCLBean = new CompanyLocationBean();
    	if(lCdId!=null){
    		lCLBean.setCdId(lCdId);
    		lCLBean.setLocationType(LocationType.RegOffice);
			try {
				if(pIsProvisional) {
					lCLBean = companyLocationProvDAO.findBean(pConnection, lCLBean);
				}else {
					lCLBean = companyLocationDAO.findBean(pConnection, lCLBean);
				}
			} catch (InstantiationException | IllegalAccessException | SQLException e) {
				logger.info("Reg Off Not found for CDId " + (lAEBean.getCdId()!=null?lAEBean.getCdId():"."));
			}
		}
		return lCLBean;
    }

    
	public int getFinYearStart(Date pDate){
		if(pDate!=null){
			int lYear = CommonUtilities.getYear(pDate);
			int lMonth = CommonUtilities.getMonth(pDate);
			if(lMonth > 3)
				return lYear;
			return (lYear-1);			
		}
		return 0;
	}
	public Date[] getFinYearDates(Date pDate){
		Date[] lFinDates = new Date[] {null, null};
		if(pDate!=null){
			int lYear = CommonUtilities.getYear(pDate);
			int lMonth = CommonUtilities.getMonth(pDate);
			if(lMonth > 3){
				//start
			}else{
				lYear -=1;
			}
			lFinDates[0] = CommonUtilities.getDate("04-01-"+ (lYear+""), "MM-dd-yyyy");
			lFinDates[1] = CommonUtilities.getDate("03-31-"+ ((lYear+1)+""), "MM-dd-yyyy");
		}
		return lFinDates;
	}
	
	
    public String getGSTN(Connection pConnection, String pEntityCode, Long pCLId){
    	CompanyLocationBean lCLBean = null;
    	if(pCLId!=null){
			lCLBean = new CompanyLocationBean();
			lCLBean.setId(pCLId);
    		try {
    			if(pCLId.longValue() == -1){
    				AppEntityBean lAppEntityBean =  TredsHelper.getInstance().getAppEntityBean(pEntityCode);
    				lCLBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lAppEntityBean.getCdId());
    			}else
    				lCLBean = companyLocationDAO.findByPrimaryKey(pConnection, lCLBean);
			} catch (Exception e) {
				logger.info("Company Locationwise GSTN not found for CLId : " + (pCLId!=null?pCLId.toString():""));
			}
    	}
    	return lCLBean.getGstn();
    }

	public boolean checkAccessToLocations(AppUserBean pUserBean) {
		if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
			return false;
		if(AppUserBean.Type.Admin.equals(pUserBean.getType()))
			return false;
		if(pUserBean.getLocationIdList()==null)
			return false;
		return (pUserBean.getLocationIdList()!=null && (pUserBean.getLocationIdList().size() > 0));
	}
	
	public boolean hasAccessToLocations(AppUserBean pUserBean ,Long pCompanyLocation) {
		if(AppUserBean.Type.Admin.equals(pUserBean.getType()))
			return true;
		return (pUserBean.getLocationIdList()==null ||  pUserBean.getLocationIdList().contains(pCompanyLocation));
	}
	
	public boolean isLocationwiseSettlementEnabled(Connection pConnection,  Long pCompanyDetailsId, boolean pIsProv) {
		CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
		lCompanyDetailBean.setId(pCompanyDetailsId);
		
		try {
			lCompanyDetailBean = companyDetailDAO.findByPrimaryKey(pConnection, lCompanyDetailBean);
			AppEntityBean lAppEntityBean = getAppEntityBean(lCompanyDetailBean.getCode());
			return (lAppEntityBean != null && CommonAppConstants.Yes.Yes.equals(lAppEntityBean.getEnableLocationwiseSettlement()));

		} catch (Exception e) {
			logger.info("Error while checking settlement location : "+e.getMessage());
		}
		return false;
	}
	public CompanyDetailBean getCompanyDetailsBean(Connection pConnection,  Long pCompanyDetailsId, Boolean pIsProv) {
		CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
		lCompanyDetailBean.setId(pCompanyDetailsId);
		try {
			if(pIsProv) {
				lCompanyDetailBean = companyDetailProvDAO.findByPrimaryKey(pConnection, lCompanyDetailBean);
			}else {
				lCompanyDetailBean = companyDetailDAO.findByPrimaryKey(pConnection, lCompanyDetailBean);
			}
		} catch (Exception e) {
			logger.info("Error while checking settlement location : "+e.getMessage());
		}
		return lCompanyDetailBean;
	}

	public String getCSVIdsForInQuery(List<Long> pIds){
		StringBuffer lRetVal = new StringBuffer();
		if(pIds !=null && pIds.size() > 0){
			for(Long lId : pIds){
				if(lRetVal.length() > 0) lRetVal.append(",");
				lRetVal.append(lId);
			}
		}else{
			lRetVal.append("0");
		}
		return lRetVal.toString();
	}

	public String[] getCSVIdsListForInQuery(List<Long> pIds){
		String[] lRetList = null;
		StringBuffer lRetVal = null;
		if(pIds !=null && pIds.size() > 0){
			int lSize = pIds.size() / 1000;
			if((pIds.size() % 1000) > 0){
				lSize +=1;
			}
			lRetList = new String[lSize];
			int lIdx=-1;
			for(int lPtr=0; lPtr < pIds.size(); lPtr++){
				if((lPtr%1000)==0){
					if(lRetVal!=null){
						lRetList[lIdx] = lRetVal.toString(); //adding to previous list
					}
					lIdx++;
					lRetVal = new StringBuffer();
				}
				if(lRetVal.length() > 0) lRetVal.append(",");
				lRetVal.append(pIds.get(lPtr));
			}
			if(lRetVal!=null){
				lRetList[lIdx] = lRetVal.toString(); //adding to previous list to the last index
			}
		}else{
			lRetList = new String[1];
			lRetList[0] = "0";
		}
		return lRetList;
	}
	public String[] getCSVStringListForInQuery(List<String> pText){
		String[] lRetList = null;
		StringBuffer lRetVal = null;
		if(pText !=null && pText.size() > 0){
			int lSize = pText.size() / 1000;
			if((pText.size() % 1000) > 0){
				lSize +=1;
			}
			lRetList = new String[lSize];
			int lIdx=-1;
			for(int lPtr=0; lPtr < pText.size(); lPtr++){
				if((lPtr%1000)==0){
					if(lRetVal!=null){
						lRetList[lIdx] = lRetVal.toString(); //adding to previous list
					}
					lIdx++;
					lRetVal = new StringBuffer();
				}
				if(lRetVal.length() > 0) lRetVal.append(",");
				lRetVal.append("'").append(pText.get(lPtr)).append("'");
			}
			if(lRetVal!=null){
				lRetList[lIdx] = lRetVal.toString(); //adding to previous list to the last index
			}
		}else{
			lRetList = new String[1];
			lRetList[0] = "";
		}
		return lRetList;
	}
	public String getInQuery(String pColumnName, String[] pItemList){
		StringBuffer lRetValue = new StringBuffer();
		lRetValue.append(" ( ");
		for(int lPtr=0; lPtr < pItemList.length; lPtr++){
			if(lPtr > 0){
				lRetValue.append(" OR ");
			}
			lRetValue.append(" ").append(pColumnName).append(" IN ( ").append(pItemList[lPtr]).append(" ) ");
		}
		lRetValue.append(" ) ");
		return lRetValue.toString();
	}
	
	
	public String getCSVEnumsForInQuery(List pIds){
		StringBuffer lRetVal = new StringBuffer();
		if(pIds !=null && pIds.size() > 0){
			for(Object lId : pIds){
				if(lRetVal.length() > 0) lRetVal.append(",");
				lRetVal.append("'").append(lId).append("'");
			}
		}else{
			lRetVal.append(" '' ");
		}
		return lRetVal.toString();
	}
	public String getCSVStringForInQuery(List<String> pIds){
		return getCSVStringForInQuery(pIds, true);
	}
	public String getCSVStringForInQuery(List<String> pIds, boolean pValueIsString){
		StringBuffer lRetVal = new StringBuffer();
		if(pIds !=null && pIds.size() > 0){
			for(String lId : pIds){
				if(lRetVal.length() > 0) lRetVal.append(",");
				if(pValueIsString) lRetVal.append("'");
				lRetVal.append(lId);
				if(pValueIsString) lRetVal.append("'");
			}
		}else{
			lRetVal.append(" '' ");
		}
		return lRetVal.toString();
	}
	
	 public boolean isBankMappedToLocation(Connection pConnection, Long pCdId, Long pCBDId){
    	CompanyLocationBean lCLBean = new CompanyLocationBean();
    	lCLBean.setCbdId(pCBDId);
    	lCLBean.setCdId(pCdId);
    	if(pCdId!=null && pCBDId != null){
    		 try {
				List<CompanyLocationBean> lList = companyLocationDAO.findList(pConnection, lCLBean, new ArrayList<String>());
				return (lList!=null && lList.size() > 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return false;
    }

	public boolean isNACHCodeActive(Connection pConnection, String pEntityCode, Long pCompanyDetailId, Long pCompanyLocationId) {
		List<CompanyLocationBean> lList = null;
		StringBuilder lSql = new StringBuilder();

    	if(TredsHelper.getInstance().isLocationwiseSettlementEnabled(pConnection, pCompanyDetailId,false)){
    		lSql.append(" select * from  ( ");
    		lSql.append( " select a.* from CompanyLocations a,FacilitatorEntityMapping  " );
    		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT = 'Y'  " );
    		lSql.append( " AND a.CLCBDID = FEMCBDID AND FEMACTIVE = 'Y' and FEMRECORDVERSION > 0 and (femexpiry is null or femexpiry >= ");
    		lSql.append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append(" ) " );
    		lSql.append(" AND a.CLCDId = ").append(pCompanyDetailId);
    		lSql.append(" AND FEMFACILITATOR = ").append(DBHelper.getInstance().formatString(AppConstants.FACILITATOR_NPCI));
    		lSql.append( " union all " );
    		lSql.append( " select a.* from CompanyLocations a,CompanyLocations b,FacilitatorEntityMapping  " );
    		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT is null " );
    		lSql.append( " and b.CLRecordVersion > 0 AND b.CLENABLESETTLEMENT = 'Y' AND a.CLSETTLEMENTCLID=b.clid " );
    		lSql.append( " AND b.CLCBDID = FEMCBDID AND FEMACTIVE = 'Y' and FEMRECORDVERSION > 0 and (femexpiry is null or femexpiry >= " );
    		lSql.append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append(" ) " );
    		lSql.append(" AND a.CLCDId = ").append(pCompanyDetailId);
    		lSql.append(" AND b.CLCDId = ").append(pCompanyDetailId);
    		lSql.append(" AND FEMFACILITATOR = ").append(DBHelper.getInstance().formatString(AppConstants.FACILITATOR_NPCI));
    		lSql.append(" ) CompanyLocations WHERE 1=1 ");
    		lSql.append(" AND CLId = ").append(pCompanyLocationId);
    		try
    		{
                lList =  companyLocationDAO.findListFromSql(pConnection, lSql.toString(), 0);
    			return (lList != null && lList.size() > 0);
    		}
    		catch(Exception ex)
    		{
    			
    		}
    		finally {
    		  
    		}
    	}else{
    		return true;
    	}
		return false;
	}
	 public String getISO8601FormattedDate(Date pDate){
	    	String lRetDate = "";
	        lRetDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+05:30'").format(pDate);
	    	return lRetDate;
	    }
	 public String getISO8601FormattedDate(Timestamp pDate){
	    	String lRetDate = "";
	        lRetDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+05:30'").format(pDate);
	    	return lRetDate;
	    }
	 public boolean isRegistrationApproved(Connection pConnection,Long pCdId){
	    	Long lCdId= pCdId;
	    	CompanyDetailBean lCDBean = new CompanyDetailBean();
	    	lCDBean.setId(lCdId);
	    	try {
				lCDBean = companyDetailDAO.findBean(pConnection, lCDBean);
				return (lCDBean!=null && CompanyApprovalStatus.Approved.equals(lCDBean.getApprovalStatus()));
			} catch (Exception e) {
				logger.info("Details Not Found" + (pCdId!=null?pCdId:"."));
			}
			return false;
	    }
	 
	 public Long getSettlementLocation(Connection pConnection, Long pCLId){
    	if(pCLId!=null){
	    	CompanyLocationBean lCLBean = new CompanyLocationBean();
	    	CompanyDetailBean lCDBean = new CompanyDetailBean();
    		lCLBean.setId(pCLId);
			try {
				lCLBean = companyLocationDAO.findBean(pConnection, lCLBean);
				if(lCLBean!=null){
					lCDBean.setId(lCLBean.getCdId());
					lCDBean = companyDetailDAO.findByPrimaryKey(pConnection, lCDBean);
					if(CommonAppConstants.Yes.Yes.equals(lCDBean.getEnableLocationwiseSettlement())){
						if(CommonAppConstants.Yes.Yes.equals(lCLBean.getEnableSettlement()))
							return lCLBean.getId();
						return lCLBean.getSettlementCLId();
					}else{
						CompanyLocationBean lRegCLBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lCLBean.getCdId());
						if(lRegCLBean!=null){
							return lRegCLBean.getId();
						}
					}
				}
			} catch (Exception lEx) {
				logger.info("Error while finding SettlementLocation for CLId " +(pCLId!=null?pCLId:""));
			}
		}
    	return null;
	 }

	 public CompanyBankDetailBean getSettlementBank(Connection pConnection, String pEntityCode, Long pCLId) throws CommonBusinessException{
		 CompanyBankDetailBean lCompanyBankDetailBean = null;
		 if(AppConstants.DOMAIN_PLATFORM.equals(pEntityCode)){
	        return TredsHelper.getInstance().getTredsChargeAccount();
		 }
		 if(pCLId!=null){
	    	CompanyLocationBean lCLBean = new CompanyLocationBean();
	    	CompanyLocationBean lSettlementCLBean = new CompanyLocationBean();
	    	CompanyDetailBean lCDBean = new CompanyDetailBean();
    		lCLBean.setId(pCLId);
			try {
				lCLBean = companyLocationDAO.findBean(pConnection, lCLBean);
				if(lCLBean!=null){
					lCDBean.setId(lCLBean.getCdId());
					lCDBean = companyDetailDAO.findByPrimaryKey(pConnection, lCDBean);
					if(CommonAppConstants.Yes.Yes.equals(lCDBean.getEnableLocationwiseSettlement())){
						lSettlementCLBean = new CompanyLocationBean();
						if(CommonAppConstants.Yes.Yes.equals(lCLBean.getEnableSettlement())){
							lSettlementCLBean = lCLBean;
						}else{
							lSettlementCLBean = new CompanyLocationBean();
							lSettlementCLBean.setId(lCLBean.getSettlementCLId());
							lSettlementCLBean.setCdId(lCDBean.getId());
							lSettlementCLBean = companyLocationDAO.findBean(pConnection, lSettlementCLBean);
						}
						lCompanyBankDetailBean = new CompanyBankDetailBean();
						lCompanyBankDetailBean.setId(lSettlementCLBean.getCbdId());
						lCompanyBankDetailBean = companyBankDetailDAO.findBean(pConnection, lCompanyBankDetailBean);
					}
				}
			} catch (Exception lEx) {
				logger.info("Error while finding Settlement Bank for CLId " +(pCLId!=null?pCLId:""));
			}
		}
		if(lCompanyBankDetailBean==null){
			lCompanyBankDetailBean = TredsHelper.getInstance().getDesignatedBank(pConnection, pEntityCode);
		}
    	return lCompanyBankDetailBean;
	 }

	 
	 public Long getOwnerAuId(AppUserBean pUserBean){
     	if(pUserBean.getOwnerAuId()!=null)
    		return pUserBean.getOwnerAuId();
    	return pUserBean.getId(); 
	 }
	 public boolean checkOwnership(AppUserBean pUserBean){
		 if (AppUserBean.Type.Admin.equals(pUserBean.getType()))
			 return false;
		 if(CommonAppConstants.Yes.Yes.equals(pUserBean.getFullOwnership())){
			 return false;
		 }
		 return true;
	 }
	 public Long getCurrentPlan(Connection pConnection,String pEntityCode){
		 MemberwisePlanBean lMPBean = getPlan(pConnection, pEntityCode, TredsHelper.getInstance().getBusinessDate(),true);
		 if(lMPBean!=null)
			 return lMPBean.getAcpId();
		 return null;
	 }

	 public MemberwisePlanBean getPlan(Connection pConnection,String pEntityCode, Date pDate, boolean pBeforeDate){
		List<MemberwisePlanBean> lList = getPlans(pConnection, pEntityCode, pDate, pBeforeDate,false);
		if(lList != null && lList.size() > 0){
			return (MemberwisePlanBean) lList.get(0);
		}
		return null;
	 }	 
	 public MemberwisePlanBean getNextPlan(Connection pConnection,String pEntityCode, Date pDate){
		List<MemberwisePlanBean> lList = getPlans(pConnection, pEntityCode, pDate, false, true);
		if(lList != null && lList.size() > 0){
			return (MemberwisePlanBean) lList.get(0);
		}
		return null;
	 }	 

	 private List<MemberwisePlanBean> getPlans(Connection pConnection,String pEntityCode, Date pDate, boolean pBeforeDate, boolean pMinDate){
		DBHelper lDBHelper = DBHelper.getInstance();
		List<MemberwisePlanBean> lList = new ArrayList<>();
		Long lPlanId = new Long(0);
		StringBuilder lSql = new StringBuilder();
		 
		lSql.append(" select *  from MEMBERWISEPLANS");
		lSql.append(" INNER JOIN ( ");
		lSql.append(" SELECT ").append(pMinDate?"Min":"Max").append(" (MPEFFECTIVESTARTDATE) MPEFFECTIVESTARTDATE , MPCODE  ");
		lSql.append(" FROM MEMBERWISEPLANS " );
		lSql.append(" WHERE 1=1 ");
		if(pDate!=null)
			lSql.append(" AND MPEFFECTIVESTARTDATE " ).append((pBeforeDate?" <= ":" >= ")).append(lDBHelper.formatDate(pDate)).append("  ");
		lSql.append(" AND MPCODE = ").append(DBHelper.getInstance().formatString(pEntityCode));
		lSql.append(" GROUP BY MPCODE ) ");
		lSql.append(" CURPLAN ON ( ");
		lSql.append(" CURPLAN.MPCODE = MEMBERWISEPLANS.MPCODE " );
		lSql.append(" AND CURPLAN.MPEFFECTIVESTARTDATE = MEMBERWISEPLANS.MPEFFECTIVESTARTDATE ) " );
		try
		{
		    lList =  memberwisePlanDAO.findListFromSql(pConnection, lSql.toString(), 0);
		}
		catch(Exception ex)
		{
			logger.info("Error in getPlan " + ex.getMessage());
		}
		if(lList==null || lList.isEmpty())
			logger.info("No Plan found for User " + pEntityCode);
		return lList;
	 }

	public AuctionChargePlanBean getPlanDetails(Connection pConnection,Long pAcpId){
		 AuctionChargePlanBean lPlanFilterBean = new AuctionChargePlanBean();
		 lPlanFilterBean.setId(pAcpId);
		 AuctionChargePlanBean lResult = null;
		try {
			lResult = auctionChargePlansBO.getPlanDetailsBean(pConnection, lPlanFilterBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lResult; 
	 }
//	public BigDecimal[] getSplitChargableAmount(AuctionChargePlanBean pAuctionChargePlanBean, FactoringUnitBean pFactoringUnitBean, BigDecimal pInvAmt, BigDecimal pTurnoverAmt , BigDecimal pTotalShare,  BigDecimal pFinancierShare , Date pLeg1Date){
//		BigDecimal lAmount = new BigDecimal(0);
//		BigDecimal lFuAmount = pInvAmt;
//		BigDecimal[] lChargeValue = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };
//		if(AuctionChargePlanBean.Type.Invoice.equals(pAuctionChargePlanBean.getType())){
//			lAmount = pInvAmt;
//			logger.info(" invoice type : "+ (pInvAmt!=null?pInvAmt:""));
//		}
//		else if(AuctionChargePlanBean.Type.TurnOver.equals(pAuctionChargePlanBean.getType())){
//			lAmount = pTurnoverAmt;
//			logger.info(" turnover type : "+ (pTurnoverAmt!=null?pTurnoverAmt:""));
//		}
//		AuctionChargeSlabBean lActualChargeBean = null;
//		for (AuctionChargeSlabBean lACSBean: pAuctionChargePlanBean.getAuctionChargeSlabList()) {
//           if (((lACSBean.getMinAmount() == null) || (lAmount.compareTo(lACSBean.getMinAmount()) >= 0)) && 
//                   ((lACSBean.getMaxAmount() == null) || (lAmount.compareTo(lACSBean.getMaxAmount()) < 0))){
//        	   logger.info("found id : "+lACSBean.getId()+" : " + (lACSBean.getMinAmount()!=null?lACSBean.getMinAmount():"") + (lACSBean.getMaxAmount()!=null?lACSBean.getMaxAmount():"") );
//               lActualChargeBean = lACSBean;
//               break;
//           }
//       }
//		lChargeValue = getComputeSplitChargeValue(lActualChargeBean, pFactoringUnitBean,lFuAmount,  pFinancierShare , pLeg1Date );
//		return lChargeValue;
//	}
	
	public AuctionChargeSlabBean getChargableDetailsForFinancier(AuctionChargePlanBean pAuctionChargePlanBean, FactoringUnitBean pFactoringUnitBean, BigDecimal pInvAmt, BigDecimal pTotalShare,  BigDecimal pFinancierShare , Date pLeg1Date){
		BigDecimal lAmount = new BigDecimal(0);
		BigDecimal lFuAmount = pInvAmt;
		BigDecimal[] lChargeValue = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };
		if(AuctionChargePlanBean.Type.Invoice.equals(pAuctionChargePlanBean.getType())){
			lAmount = pInvAmt;
			logger.info(" invoice type : "+ (pInvAmt!=null?pInvAmt:""));
		}
		AuctionChargeSlabBean lActualChargeBean = null;
		for (AuctionChargeSlabBean lACSBean: pAuctionChargePlanBean.getAuctionChargeSlabList()) {
           if (((lACSBean.getMinAmount() == null) || (lAmount.compareTo(lACSBean.getMinAmount()) >= 0)) && 
                   ((lACSBean.getMaxAmount() == null) || (lAmount.compareTo(lACSBean.getMaxAmount()) < 0))){
        	   logger.info("found id : "+lACSBean.getId()+" : " + (lACSBean.getMinAmount()!=null?lACSBean.getMinAmount():"") + (lACSBean.getMaxAmount()!=null?lACSBean.getMaxAmount():"") );
               lActualChargeBean = lACSBean;
               break;
           }
       }
		return lActualChargeBean;
	}
	
	public BigDecimal getChargableAmount(AuctionChargePlanBean pAuctionChargePlanBean, FactoringUnitBean pFactoringUnitBean ,BigDecimal pInvAmt, BigDecimal pTurnoverAmt,Long pExtensionDays,Long pTenor){
		BigDecimal lAmount = new BigDecimal(0);
		BigDecimal lFuAmount = pInvAmt;
		BigDecimal lChargeValue = new BigDecimal(0);
		if(AuctionChargePlanBean.Type.Invoice.equals(pAuctionChargePlanBean.getType())){
			lAmount = pInvAmt;
			logger.info(" invoice type : "+ (pInvAmt!=null?pInvAmt:""));
		}
		else if(AuctionChargePlanBean.Type.TurnOver.equals(pAuctionChargePlanBean.getType())){
			lAmount = pTurnoverAmt;
			logger.info(" turnover type : "+ (pTurnoverAmt!=null?pTurnoverAmt:""));
		}
		AuctionChargeSlabBean lActualChargeBean = null;
		for (AuctionChargeSlabBean lACSBean: pAuctionChargePlanBean.getAuctionChargeSlabList()) {
           if (((lACSBean.getMinAmount() == null) || (lAmount.compareTo(lACSBean.getMinAmount()) >= 0)) && 
                   ((lACSBean.getMaxAmount() == null) || (lAmount.compareTo(lACSBean.getMaxAmount()) < 0))){
        	   logger.info("found id : "+lACSBean.getId()+" : " + (lACSBean.getMinAmount()!=null?lACSBean.getMinAmount():"") + (lACSBean.getMaxAmount()!=null?lACSBean.getMaxAmount():"") );
               lActualChargeBean = lACSBean;
               break;
           }
       }
		if (lActualChargeBean==null) {
			logger.info("Charge Slab Not found." );
		}else {
			if (pExtensionDays!=null && lActualChargeBean.getExtendedChargeRate()!=null) {
				lChargeValue = lFuAmount.multiply(lActualChargeBean.getExtendedChargeRate()).multiply(new BigDecimal(pExtensionDays));
				lChargeValue = lChargeValue.divide(AppConstants.DAYS_IN_YEAR, MathContext.DECIMAL128).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
			}else {
				if (pTenor!=null) {
					lChargeValue = getComputeChargeValue(lActualChargeBean.getChargeType(),lActualChargeBean.getChargeAbsoluteValue(),lActualChargeBean.getChargePercentValue(),lActualChargeBean.getChargeMaxValue(), pTenor, lFuAmount );
				}else {
					lChargeValue = getComputeChargeValue(lActualChargeBean.getChargeType(),lActualChargeBean.getChargeAbsoluteValue(),lActualChargeBean.getChargePercentValue(),lActualChargeBean.getChargeMaxValue(), pFactoringUnitBean,lFuAmount );
				}
				
			}
		}
		return lChargeValue;
	}
	
	public BigDecimal getChargableAmount(AuctionChargePlanBean pAuctionChargePlanBean, FactoringUnitBean pFactoringUnitBean ,BigDecimal pInvAmt, BigDecimal pTurnoverAmt){
		return getChargableAmount( pAuctionChargePlanBean,  pFactoringUnitBean , pInvAmt, pTurnoverAmt,null,null);
	}
	
	public BigDecimal getTurnoverAmount(Connection pConnection,String pEntityCode){
		List<MemberTurnoverBean> lList = new ArrayList<>();
		DBHelper lDBHelper = DBHelper.getInstance();
		BigDecimal lTurnover = new BigDecimal(0);
		Date lCurrDate=null , lFYStartDate=null ,lFYEndDate =null;
		lCurrDate = TredsHelper.getInstance().getBusinessDate();
		Date[] lFYDates = TredsHelper.getInstance().getFinYearDates(lCurrDate);
		lFYStartDate = lFYDates[0];
		lFYEndDate = lFYDates[1];
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM MEMBERTURNOVER");
		lSql.append(" WHERE  MTCODE =  ").append(DBHelper.getInstance().formatString(pEntityCode));
		lSql.append(" AND MTFINYEARSTARTDATE = ").append(lDBHelper.formatDate(lFYStartDate));
		lSql.append(" AND ");
		lSql.append(" MTFINYEARENDDATE = ").append(lDBHelper.formatDate(lFYEndDate));
		
		try {
			lList = memberTurnoverDAO.findListFromSql(pConnection, lSql.toString(), 0);
			if(lList != null && lList.size() > 0){
				MemberTurnoverBean lResult = lList.get(0);
				lTurnover = lResult.getTurnover();
			}
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
		}
		return lTurnover;
	}
	
	public void updateTurnover(Connection pConnection,String pEntityCode,BigDecimal pTurnover, BigDecimal pFUAmount, Long pUserId){
		Date lCurrDate=null , lFYStartDate=null ,lFYEndDate =null;
		lCurrDate = TredsHelper.getInstance().getBusinessDate();
		Date[] lFYDates = TredsHelper.getInstance().getFinYearDates(lCurrDate);
		lFYStartDate = lFYDates[0];
		lFYEndDate = lFYDates[1];
		BigDecimal lNewTurnover = new BigDecimal(0);
		lNewTurnover = pTurnover.add(pFUAmount);
		MemberTurnoverBean lBean = new MemberTurnoverBean();
		lBean.setCode(pEntityCode);
		lBean.setFinYearStartDate(lFYStartDate);
		try {
			lBean = memberTurnoverDAO.findBean(pConnection, lBean);
			if(lBean!=null){
				lBean.setTurnover(lNewTurnover);
				lBean.setRecordUpdator(pUserId);
				memberTurnoverDAO.update(pConnection, lBean);
				memberTurnoverDAO.insertAudit(pConnection, lBean, AuditAction.Update,pUserId);			
			}
			else
			{
				lBean = new MemberTurnoverBean();
				lBean.setCode(pEntityCode);
				lBean.setFinYearStartDate(lFYStartDate);
				lBean.setFinYearEndDate(lFYEndDate);
				lBean.setTurnover(lNewTurnover);
				lBean.setRecordCreator(pUserId);
				lBean.setRecordVersion(new Long(1));
				memberTurnoverDAO.insert(pConnection, lBean);
				memberTurnoverDAO.insertAudit(pConnection, lBean, AuditAction.Insert,pUserId);			
			}
		} catch (Exception e) {
			logger.info("Error while inserting Memberwise Turnover");
		}
	}

	public void calculateTurnover(String pEntityCode,Date pFYStartDate, Date pFYEndDate){
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		//delete query based on financial year and/or entitycode
		lSql.append(" UPDATE MEMBERTURNOVER ");
		lSql.append(" SET MTRECORDVERSION = 0 ");
		lSql.append(" WHERE MTRECORDVERSION > 0 ");
		lSql.append(" AND MTFINYEARSTARTDATE = ").append(lDBHelper.formatDate(pFYStartDate));
		lSql.append(" AND MTFINYEARENDDATE = ").append(lDBHelper.formatDate(pFYEndDate));
		if (StringUtils.isNotEmpty(pEntityCode)){
			lSql.append(" AND FUPURCHASER = ").append(pEntityCode);
		}
		//
		lSql.append(" Insert into MEMBERTURNOVER (MTCODE,MTFINYEARSTARTDATE ,MTFINYEARENDDATE ,MTTURNOVER, ");
		lSql.append(" MTRECORDCREATOR,MTRECORDCREATETIME,MTRECORDVERSION ");
		lSql.append(" select FUPURCHASER ,");
		lSql.append(lDBHelper.formatDate(pFYStartDate)).append(" , ");
		lSql.append(lDBHelper.formatDate(pFYEndDate)).append(" , ");
		lSql.append(" sum(FUFACTOREDAMOUNT),1,SYSDATE,1 from FactoringUnits " );
		lSql.append(" where fustatus in ('FACT','L1SET','L2SET') ");
		lSql.append(" and TO_DATE(FUACCEPTDATETIME) BETWEEN ");
		lSql.append(lDBHelper.formatDate(pFYStartDate)).append(" and ");
		lSql.append(lDBHelper.formatDate(pFYEndDate));
		if (StringUtils.isNotEmpty(pEntityCode)){
			lSql.append(" and FUPURCHASER = ").append(pEntityCode);
		}
		lSql.append(" group by fupurchaser ");

	}
	
	public FacilitatorEntityMappingBean getNachBeanForPurchaser(Connection pConnection, Long pSettleCLId,String pEntityCode ){
		CompanyLocationBean lFilterBean = new CompanyLocationBean(),lCLBean =  new CompanyLocationBean() ;
		Long lCoBankId = null;
		lFilterBean.setId(pSettleCLId);
		try {
			lCLBean = companyLocationDAO.findByPrimaryKey(pConnection, lFilterBean);
			if(lCLBean != null){
				if (TredsHelper.getInstance().isLocationwiseSettlementEnabled(pConnection, lCLBean.getCdId(),false)){
					lCoBankId = lCLBean.getCbdId();
				}
				if(lCoBankId==null){
					lCoBankId = TredsHelper.getInstance().getDesignatedBankId(pConnection, pEntityCode);
				}
			}
		} catch (Exception e) {
			logger.info("Error while getNachBeanForPurchaser : " + e.getMessage());
			e.printStackTrace();
		}
		if(lCoBankId==null)
			return null;
		FacilitatorEntityMappingBean lFEMFilterBean = new FacilitatorEntityMappingBean() , lFEMBean= new FacilitatorEntityMappingBean();
		lFEMFilterBean.setCbdId(lCoBankId);
		lFEMFilterBean.setEntityCode(pEntityCode);
		lFEMFilterBean.setFacilitator(AppConstants.FACILITATOR_NPCI);
		try {
			lFEMBean = facilitatorEntityMappingBeanDAO.findBean(pConnection,lFEMFilterBean);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return lFEMBean;
	}
	public FacilitatorEntityMappingBean getNachBeanForFinancier(Connection pConnection, String pPurchaserCode,  String pFinancierEntityCode, FinancierAuctionSettingBean pFinAucSettingBean ) throws CommonBusinessException{
		FacilitatorEntityMappingBean lFEMFilterBean = new FacilitatorEntityMappingBean() , lFEMBean= new FacilitatorEntityMappingBean();
		Long lCoBankId = null;
		FinancierAuctionSettingBean lFinAucSettingBean = pFinAucSettingBean;
		if (StringUtils.isNotEmpty(pPurchaserCode)){
			if(lFinAucSettingBean==null){
				lFinAucSettingBean = TredsHelper.getInstance().getFinancierAuctionSettingBean(pConnection, pFinancierEntityCode, pPurchaserCode);
			}
			if(lFinAucSettingBean != null && lFinAucSettingBean.getFinClId()!=null){
				CompanyBankDetailBean lCompanyBankDetailBean = TredsHelper.getInstance().getSettlementBank(pConnection, pFinancierEntityCode, lFinAucSettingBean.getFinClId());
				if(lCompanyBankDetailBean!=null){
					lCoBankId = lCompanyBankDetailBean.getId();
				}
			}
		}
		if(lCoBankId==null){
			lCoBankId = TredsHelper.getInstance().getDesignatedBankId(pConnection, pFinancierEntityCode);
		}
		if(lCoBankId==null){
			throw new CommonBusinessException("Designated bank not found for " + pFinancierEntityCode+".");
		}
		lFEMFilterBean.setEntityCode(pFinancierEntityCode);
		lFEMFilterBean.setFacilitator(AppConstants.FACILITATOR_NPCI);
		lFEMFilterBean.setCbdId(lCoBankId);
		try {
			lFEMBean = facilitatorEntityMappingBeanDAO.findBean(pConnection,lFEMFilterBean);
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
		return lFEMBean;
	}
	
	public FinancierAuctionSettingBean getFinancierAuctionSettingBean(Connection pConnection, String pFinancier, String pPurchaser){
		FinancierAuctionSettingBean lFinAucSettingBean = null;
    	AppEntityBean lFinancierEntityBean=null;
		try {
			lFinancierEntityBean = TredsHelper.getInstance().getAppEntityBean(pFinancier);
		} catch (MemoryDBException e) {
			logger.info("Error while fetching FASBean : "+e.getMessage());
		}
    	//
    	if(lFinancierEntityBean != null &&
    			TredsHelper.getInstance().isLocationwiseSettlementEnabled(pConnection, lFinancierEntityBean.getCdId(),false)){
    		lFinAucSettingBean = new FinancierAuctionSettingBean();
    		lFinAucSettingBean.setActive(FinancierAuctionSettingBean.Active.Active);
    		lFinAucSettingBean.setFinancier(pFinancier);
    		lFinAucSettingBean.setPurchaser(pPurchaser);
    		lFinAucSettingBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Buyer);
    		try {
				lFinAucSettingBean = financierAuctionSettingDAO.findBean(pConnection, lFinAucSettingBean);
			} catch (Exception e) {
				logger.info("Error while fetching FASBean : "+e.getMessage());
			}
    	}
    	return lFinAucSettingBean;
	}
	
	
    public Map<String, FacilitatorEntityMappingBean> getFacilitatorEntityMap(Connection pConnection, String pFacilitator) throws Exception {
        Map<String, FacilitatorEntityMappingBean> lMap = new HashMap<String, FacilitatorEntityMappingBean>();
        FacilitatorEntityMappingBean lFilterBean = new FacilitatorEntityMappingBean();
        lFilterBean.setActive(CommonAppConstants.YesNo.Yes);
        lFilterBean.setFacilitator(pFacilitator);
        GenericDAO<FacilitatorEntityMappingBean> lFacilitatoryEntityMappingDAO = new GenericDAO<FacilitatorEntityMappingBean>(FacilitatorEntityMappingBean.class);
        List<FacilitatorEntityMappingBean> lList = lFacilitatoryEntityMappingDAO.findList(pConnection, lFilterBean, (String)null);
        if (lList != null) {
            for (FacilitatorEntityMappingBean lFacilitatorEntityMappingBean : lList)
                lMap.put(lFacilitatorEntityMappingBean.getEntityCode() 
                		+ CommonConstants.KEY_SEPARATOR + lFacilitatorEntityMappingBean.getCbdId(), 
                		lFacilitatorEntityMappingBean);
        }
        return lMap;
    }

    public Map<Long, CompanyBankDetailBean> getEntityBankDetailsMap(Connection pConnection) throws Exception {
        Map<Long, CompanyBankDetailBean> lMap = new HashMap<Long, CompanyBankDetailBean>();
        GenericDAO<CompanyBankDetailBean> lCompanyBankDetailsDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        CompanyBankDetailBean lFilterBean = new CompanyBankDetailBean();
        lFilterBean.setDefaultAccount(CommonAppConstants.Yes.Yes);
        List<CompanyBankDetailBean> lList = lCompanyBankDetailsDAO.findList(pConnection, lFilterBean,(String)null);
        if (lList != null) {
            for (CompanyBankDetailBean lCompanyBankDetailBean : lList)
                lMap.put(lCompanyBankDetailBean.getCdId(), lCompanyBankDetailBean);
            //adding the current facilitator wise Treds account
            CompanyBankDetailBean lTredsBankDetailBean = TredsHelper.getInstance().getTredsChargeAccount();
            lMap.put(lTredsBankDetailBean.getCdId(), lTredsBankDetailBean);
        }
        return lMap;
    }
    
    //only for leg3 and obligationextensions
    public void getObligationsSplitsForPurchaser(Connection pConnection,InstrumentBean pInstrumentBean, FactoringUnitBean pFactoringUnitBean,ObligationBean pPurchaserObligationBeanLeg2,List<ObligationBean> pObligations) throws Exception{
    	FacilitatorEntityMappingBean lPurchaserFEMBean = null; 
    	BigDecimal lMandateAmtPurchaser = BigDecimal.ZERO;
    	HashMap  lObliSplitSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONSPLITTING);
		//
		lPurchaserFEMBean = TredsHelper.getInstance().getNachBeanForPurchaser(pConnection, pInstrumentBean.getPurSettleClId(),pFactoringUnitBean.getPurchaser());
		if(lPurchaserFEMBean==null){
			throw new CommonBusinessException("NACH mandate not found.");
		}
		if(!CommonAppConstants.YesNo.Yes.equals(lPurchaserFEMBean.getActive())){
			throw new CommonBusinessException("NACH mandate is inactive.");
		}
		if(lPurchaserFEMBean.getExpiry()!=null && pPurchaserObligationBeanLeg2.getDate().after(lPurchaserFEMBean.getExpiry())){
			throw new CommonBusinessException("Leg-1 date " + getFormattedDate(pPurchaserObligationBeanLeg2.getDate()) +  " falls beyond the NACH mandate expiry date "+ getFormattedDate(lPurchaserFEMBean.getExpiry()) + "."  );
		}
		//
    	int lObligationSplitCount = 1;
		AppEntityBean lPurchaserEntity = TredsHelper.getInstance().getAppEntityBean(pFactoringUnitBean.getPurchaser());
		//
		BigDecimal lLeg2Amount = pPurchaserObligationBeanLeg2.getAmount();
    	BigDecimal lMandatePercent =  BigDecimal.valueOf((Double)lObliSplitSetting.get(AppConstants.ATTRIBUTE_MANDATEPERCENT));
		//
    	lMandateAmtPurchaser = (lPurchaserFEMBean.getMandateAmount().multiply(lMandatePercent, MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
    	//
		if(CommonAppConstants.YesNo.Yes.equals(lPurchaserEntity.getAllowObliSplitting())){
	    	if(lLeg2Amount.compareTo(lMandateAmtPurchaser) > 0){
	    		BigDecimal[] lSplits = lLeg2Amount.divideAndRemainder(lMandateAmtPurchaser);
	    		if(lSplits[1].compareTo(BigDecimal.ZERO)==1){
	    			lObligationSplitCount = lSplits[0].add(BigDecimal.ONE).intValue();
	    		}else{
	    			lObligationSplitCount = lSplits[0].intValue();
	    		}
	    	}
		}else{
			//NO SPLITTING THEN  ONLY CHECK THE OBLIGATIONS AMOUNT TO BE BELOW BOTH MANDATE VALUES 
    		if(lLeg2Amount.compareTo(lMandateAmtPurchaser) > 0){
    			logger.info("Adjusted NACH mandate amount " + lMandateAmtPurchaser.toString() +" is less than instrument amount " + getFormattedAmount(lLeg2Amount, true) +"." );
    			throw new CommonBusinessException("Adjusted NACH mandate amount is less than Leg-2 amount.");
    		}
		}
		//splitting 
		splitObligations(pConnection,pObligations,lObligationSplitCount); 
    	//use list of Obligations insted of null
    }
    
//    public void splitObligations(Connection pConnection,List<ObligationBean> pObligations, int pSplitParts) throws CommonBusinessException{
//    	BigDecimal[] lParts = new BigDecimal[pObligations.size()];
//    	ArrayList<ObligationSplitsBean> lSplitList = new ArrayList<ObligationSplitsBean>();
//    	
//    	int index=0 ;
//    	BigDecimal lTotalDebit = BigDecimal.ZERO;
//    	BigDecimal lTotalCredit = BigDecimal.ZERO;
//		try {
//			Collections.sort(pObligations);
//	    	//
//			ObligationBean.Type lType = null;
//	    	for (ObligationBean lBean : pObligations){
//	    		if(!lBean.getType().equals(lType)){
//    				// last part
//	    			if(index > 0){
//	    				if(lTotalCredit.compareTo(lTotalDebit) > 0 ){
//		    				lParts[index-1] = lParts[index-1].subtract(lTotalCredit).add(lTotalDebit);
//		    				logger.debug("Adjusting last part of credit to " + lParts[index-1]);	    					
//	    				}else if(lTotalCredit.compareTo(lTotalDebit) < 0 ){
//		    				lParts[index-1] = lParts[index-1].subtract(lTotalDebit).add(lTotalCredit);
//		    				logger.debug("Adjusting last part of credit to " + lParts[index-1]);	    					
//	    				}else{
//	    					logger.info(" Both equal "+ lTotalCredit);
//	    				}
//	    			}
//	    			//reset totals on change of leg
//    				lTotalDebit = BigDecimal.ZERO;
//	    	    	lTotalCredit = BigDecimal.ZERO;
//	    		}
//	    		lParts[index] = lBean.getAmount().divide(new BigDecimal(pSplitParts), MathContext.DECIMAL128).setScale(2,RoundingMode.HALF_UP);
//	    		if (lBean.getTxnType() == ObligationBean.TxnType.Debit) {
//		    		logger.info("Debit : " +lBean.getId() + " : " + lBean.getType().toString() + " : " + lBean.getTxnType() + " : " + lBean.getTxnEntity() + " : " + lBean.getAmount() + " : " + lParts[index]);
//	    			lTotalDebit = lTotalDebit.add(lParts[index]);
//	    		} else {
//		    		logger.info("Credit : " +lBean.getId() + " : " + lBean.getType().toString() + " : " + lBean.getTxnType() + " : " + lBean.getTxnEntity() + " : " + lBean.getAmount() + " : " + lParts[index]);
//	    			lTotalCredit = lTotalCredit.add(lParts[index]);
//	    			//this is only in credit since our obligations are sorted
//	    			if (index == pObligations.size() - 1) {
//	    				if(lTotalCredit.compareTo(lTotalDebit) > 0 ){
//		    				lParts[index] = lParts[index].subtract(lTotalCredit).add(lTotalDebit);
//		    				logger.debug("Adjusting last part of credit to " + lParts[index]);	    					
//	    				}else if(lTotalCredit.compareTo(lTotalDebit) < 0 ){
//		    				lParts[index] = lParts[index].subtract(lTotalDebit).add(lTotalCredit);
//		    				logger.debug("Adjusting last part of credit to " + lParts[index]);	    					
//	    				}else{
//	    					logger.info(" Both equal "+ lTotalCredit);
//	    				}
//	    			}
//	    		}
//	    		index++;
//	    		lType = lBean.getType();
//	    	}
//	    	ObligationSplitsBean lOSBean = null;
//	    	int lPartIndex = 0;
//			for (ObligationBean lBean : pObligations) {
//	    		//logger.info(lBean.getId() + " : " + lBean.getType().toString() + " : " + lBean.getTxnType() + " : " + lBean.getTxnEntity() + " : " + lBean.getAmount() + " : " +lParts[lPartIndex]);
//				for (int lPtr = 0; lPtr < pSplitParts; lPtr++){
//					lOSBean=new ObligationSplitsBean();
//					lOSBean.setId(lBean.getId());
//					lOSBean.setPartNumber(new Long(lPtr+1));
//					lOSBean.setStatus(lBean.getStatus());
//					lOSBean.setPaymentSettlor(AppConstants.FACILITATOR_NPCI);
//					if(lPtr==pSplitParts-1){
//						//last part
//						lOSBean.setAmount(lBean.getAmount().subtract( (lParts[lPartIndex]).multiply(new BigDecimal(pSplitParts-1)) ) );
//					}else{
//						lOSBean.setAmount(lParts[lPartIndex]);
//					}
//	    			lSplitList.add(lOSBean);
//				}
//				lPartIndex++;
//			}
//			for(ObligationSplitsBean lBean:lSplitList){
//				obligationSplitsDAO.insert(pConnection, lBean);
//			}
//		} catch (Exception e) {
//			logger.error("Error while splitting obligations. : " + e.getMessage());
//			throw new CommonBusinessException("Error while splitting obligations.");
//		}
//    }
	
	 public List<String> getMappedFinancier(Connection pConnection,String pEntityCode) throws Exception{
		DBHelper lDBHelper = DBHelper.getInstance();
		List<String> lList = new ArrayList<String>();
		StringBuilder lSql = new StringBuilder();
			 
			
		lSql.append(" SELECT FASFinancier ");
		lSql.append(" FROM FinancierAuctionSettings ");
		lSql.append(" WHERE FASLevel = ").append(lDBHelper.formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode()));
		lSql.append(" AND FASActive = ").append(lDBHelper.formatString(FinancierAuctionSettingBean.Active.Active.getCode()));
		lSql.append(" AND FASPurchaser = ").append(lDBHelper.formatString(pEntityCode));
			
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
		    lResultSet = lStatement.executeQuery(lSql.toString());
		    while (lResultSet.next()){
		    	lList.add(lResultSet.getString("FASFinancier"));
		    }
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
		return lList;
	 }
	 public Long getLimit(Connection pConnection,String pFinancierCode, String pPurchaserCode) throws Exception{
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		Long lRetVal = null;
			
		lSql.append(" SELECT FASLimit ");
		lSql.append(" FROM FinancierAuctionSettings ");
		lSql.append(" WHERE FASLevel = ").append(lDBHelper.formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode()));
		lSql.append(" AND FASActive = ").append(lDBHelper.formatString(FinancierAuctionSettingBean.Active.Active.getCode()));
		lSql.append(" AND FASFinancier = ").append(lDBHelper.formatString(pFinancierCode));
		lSql.append(" AND FASPurchaser = ").append(lDBHelper.formatString(pPurchaserCode));
			
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
		    lResultSet = lStatement.executeQuery(lSql.toString());
		    while (lResultSet.next()){
		    	lRetVal = lResultSet.getLong("FASLimit");
		    }
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
		return lRetVal;
	 }

	public Object getSanatisedObject(Object pValue){
		if(pValue != null && pValue.getClass().equals(String.class)){
			if(pattern.matcher(pValue.toString()).find()){
				return "'" + pValue.toString();
			}
		}
		return pValue;
	}
	
	public String createCompanyOrEntityCode(Connection pConnection, String pCompanyName) throws Exception{
        String lCode = null;
        lCode = String.valueOf(DBHelper.getInstance().getUniqueNumber(pConnection, "AppEntities.Code")); 
        //removing special chars from companyname
        Pattern pt = Pattern.compile("[^a-zA-Z]");
        Matcher match= pt.matcher(pCompanyName);
        pCompanyName = match.replaceAll("");
        lCode = pCompanyName.toUpperCase().substring(0, 2) + StringUtils.leftPad(lCode, 7, '0');
        return lCode;
	}
	
	//HashMap<Aggregator, List<Purchaser>>
	public HashMap<String, List<String>> getAggregatorPurchaserMap(Connection pConnection) throws Exception {
		HashMap<String, List<String>> lAggPurMap = new HashMap<String, List<String>>();
		 List<AggregatorPurchaserMapBean> lAggPurchMapBeans =  aggregatorPurchaserMapBeanDAO.findList(pConnection, new AggregatorPurchaserMapBean(), new ArrayList<String>());
		 if(lAggPurchMapBeans !=null){
			 List<String> lPurchaserList = null;
			 for(AggregatorPurchaserMapBean lBean : lAggPurchMapBeans){
				 lPurchaserList = lAggPurMap.get(lBean.getAggregator());
				 if(lPurchaserList==null){
					 lPurchaserList = new ArrayList<String>();
					 lAggPurMap.put(lBean.getAggregator(), lPurchaserList);
				 }
				 lPurchaserList.add(lBean.getPurchaser());
			 }
		 }
		return lAggPurMap;
	}
	
    public Map<Status,List<Status>> getValidModificationStatuses(){
    	return validStatusMap;
    }
    private Map<Status,List<Status>> createValidModificationStatuses(){
    	Map<Status,List<Status>> lStatusMap = new HashMap<Status, List<Status>>();
    	Status lOldStatus = null;
    	List<Status> lNewStatuses = null;
    	//
    	lOldStatus = Status.Returned;
    	lNewStatuses = new ArrayList<Status>();
    	lNewStatuses.add(Status.Ready);
    	lNewStatuses.add(Status.Success);
    	lNewStatuses.add(Status.Failed);
    	lStatusMap.put(lOldStatus, lNewStatuses);
    	//
    	lOldStatus = Status.Failed;
    	lNewStatuses = new ArrayList<Status>();
    	lNewStatuses.add(Status.Ready);
    	lNewStatuses.add(Status.Success);
    	lStatusMap.put(lOldStatus, lNewStatuses);
    	//
    	lOldStatus = Status.Cancelled;
    	lNewStatuses = new ArrayList<Status>();
    	lNewStatuses.add(Status.Ready);
    	lNewStatuses.add(Status.Success);
    	lNewStatuses.add(Status.Failed);
    	lStatusMap.put(lOldStatus, lNewStatuses);
    	//
    	lOldStatus = Status.Prov_Success;
    	lNewStatuses = new ArrayList<Status>();
    	lNewStatuses.add(Status.Success);
    	lNewStatuses.add(Status.Failed);
    	lStatusMap.put(lOldStatus, lNewStatuses);
    	//
    	return lStatusMap;
    }
    
    private Map<String, Boolean> createValidModificationRequestMap(){
    	Map<Status,List<Status>> lStatusMap = getValidModificationStatuses();
    	Map<String, Boolean> lMap = new HashMap<String, Boolean>();
    	for(Status lStatus : lStatusMap.keySet()){
        	boolean lCheckPreviousObli = false;
    		for(Status lValidStatus : lStatusMap.get(lStatus) ){
    			if(Status.Cancelled.equals(lStatus)){
    				lCheckPreviousObli= true;
    			}
    			//OldStatuCode^NewStatusCode , CheckPreviousObligation
    			lMap.put(lStatus.getCode()+ CommonConstants.KEY_SEPARATOR + lValidStatus.getCode(), lCheckPreviousObli);
    		}
    	}
		return lMap;
    }
    
    public boolean allowObligationModificationRequest(Connection pConnection, Long pFuId, Long pPartNumber, Type pLegType, Status pOldStatus, Status pNewStatus) throws CommonBusinessException{
		//OldStatuCode^NewStatusCode , CheckPreviousObligation
    	String lStatusKey = pOldStatus.getCode()+ CommonConstants.KEY_SEPARATOR + pNewStatus.getCode();
    	if(validModificationRequestMap.containsKey(lStatusKey)){
    		if(validModificationRequestMap.get(lStatusKey)){
    			if (Type.Leg_2.equals(pLegType)){
    				if(!TredsHelper.getInstance().validateObligation(pConnection, pFuId,Type.Leg_1, pPartNumber)){
    					throw new CommonBusinessException("Previous Leg not settled.");    					
    				}
    			}
    		}
    		return true;
    	}else{
    		if(!(ObligationBean.Status.Success.equals(pOldStatus) && ObligationBean.Status.Success.equals(pNewStatus))){
        		throw new CommonBusinessException("Please select a valid status.");
    		}
        	return true;
    	}
    }
    public List<ObligationDetailBean> getObligationDetailBean(Connection pConnection,Long pFuId, Type pType ,Long pPartNumber){
    	DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        
        //
        lSql.append("SELECT * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0 ");
        lSql.append(" AND OBFUID = ").append(pFuId.toString()); 
        lSql.append(" AND OBSOBID = OBID ");
        if(pType!=null){
            lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(pType.getCode()));
        }
        if(pPartNumber!=null){
            lSql.append(" AND OBSPARTNUMBER = ").append(lDBHelper.formatString(pPartNumber.toString()));
        }
        //
        lSql.append(" AND OBSTATUS NOT IN ( ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) "); 
		lSql.append(" AND OBSSTATUS NOT IN ( ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) "); 
        //
        lSql.append(" ORDER BY OBTYPE, OBTXNTYPE DESC, OBSPARTNUMBER ");
        try {
			lObligationDetails =  obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return lObligationDetails;
		
    }
    
    public Map<Long,Object> getObligationSplitMap(Connection pConnection,List<Long> lObids){
    	DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationSplitsBean> lObligationSplits = null;
        Map <Long,Object> lObligationMap = new HashMap<>();
        Map <Long,String> lTmpMap = null;
        ArrayList<Map <Long,String>> lTmpList = null;
        //
        lSql.append("SELECT * FROM  ObligationSplits ");
        lSql.append(" WHERE  OBSRECORDVERSION > 0  ");
		String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lObids);
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBSOBID", lListStr)).append("  ");
        try {
        	lObligationSplits =  obligationSplitsDAO.findListFromSql(pConnection, lSql.toString(), -1);
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
        for (ObligationSplitsBean lObligationSplitsBean : lObligationSplits) {
        	if( !lObligationMap.containsKey(lObligationSplitsBean.getObid()) ){
        		lObligationMap.put(lObligationSplitsBean.getObid(), new ArrayList<>());
        	}
        	lTmpList = (ArrayList<Map<Long, String>>) lObligationMap.get(lObligationSplitsBean.getObid());
        	lTmpMap = new HashMap<>();
        	lTmpMap.put(lObligationSplitsBean.getPartNumber(), lObligationSplitsBean.getPaymentRefNo());
        	lTmpList.add(lTmpMap);
        }
		return lObligationMap;
    }
    
    public Map<Long, Long> getFactoringInstIdMap(Connection pConnection,List<Long> lFuids){
    	DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<InstrumentBean> lInstruments = null;
        Map <Long,Long> lMap = new HashMap<>();
        Map <Long,String> lTmpMap = null;
        ArrayList<Map <Long,String>> lTmpList = null;
        //
        lSql.append("SELECT INID,INFUID FROM  Instruments ");
        lSql.append(" WHERE  INRECORDVERSION > 0 ");
        lSql.append(" AND INFUID IN ( ").append(TredsHelper.getInstance().getCSVIdsForInQuery(lFuids)).append(" ) "); 
        try {
        	lInstruments =  instrumentDAO.findListFromSql(pConnection, lSql.toString(), -1);
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
        for (InstrumentBean lBean : lInstruments) {
        	if( !lMap.containsKey(lBean.getFuId()) ){
        		lMap.put(lBean.getFuId(), lBean.getId());
        	}
        }
		return lMap;
    }
    
    private boolean validateObligation(Connection pConnection,Long pFuId, Type pType ,Long pPartNumber){
    		Boolean lReturnFlag = false;
    		List<ObligationDetailBean> lObligationDetails = getObligationDetailBean(pConnection, pFuId, pType, pPartNumber);
            ObligationSplitsBean lObligationSplitsBean = null;
			for(ObligationDetailBean lObligationDetailBean : lObligationDetails){
            	lObligationSplitsBean = lObligationDetailBean.getObligationSplitsBean();
            	//
            	if(Status.Success.equals(lObligationSplitsBean.getStatus())){
            		lReturnFlag = true;
            	}
            }
			return lReturnFlag;
    }
    
    public void verifyOrSendOTP(HttpServletRequest pRequest, IAppUserBean pUserBean, Connection pConnection, String pNotificationType, String pTemplatePrefix, Map<String,String> pData) throws Exception{
    	boolean lSendSms = false, lSendEmail=false;
        //checking the global level General settings for sms / mail
		if(!otpEmail && !otpSms){
			return;
		}
        EntityOtpNotificationSettingBean lSettingBean = OtherResourceCache.getInstance().getEntityOtpNotificationSettings(pConnection, pUserBean.getDomain(), pNotificationType);
        lSendSms = OtherResourceCache.getInstance().isOtpSmsNotificationEnabled(lSettingBean) && otpSms;
        lSendEmail = OtherResourceCache.getInstance().isOtpEmailNotificationEnabled(lSettingBean) && otpEmail;
        if(lSendSms||lSendEmail){
            Map<String,Object> lDataHash = new HashMap<String, Object>();
        	boolean lExplicit = false;
            if(lSendSms){
            	lExplicit = OtherResourceCache.getInstance().isOtpExplicitSmsNotificationEnabled(lSettingBean);
            	if(lExplicit){
            		if(lSettingBean.getMobileList()!=null && lSettingBean.getMobileList().size() > 0){
                    	lDataHash.put(SMSSender.MOBILENO, lSettingBean.getMobileList());
            		}else{
            			lSendSms = false;
            		}
            	}
            }
            if(lSendEmail){
            	lExplicit = OtherResourceCache.getInstance().isOtpExplicitEmailNotificationEnabled(lSettingBean);
            	if(lExplicit){
            		if(lSettingBean.getEmailList()!=null && lSettingBean.getEmailList().size() > 0){
                    	lDataHash.put(EmailSender.TO, lSettingBean.getEmailList());
            		}else{
            			lSendEmail = false;
            		}
            	}
            }
            if(lSendSms||lSendEmail){
            	lDataHash.put("name",TredsHelper.getInstance().getFullName(pUserBean));
            	lDataHash.put("validity", StringUtils.leftPad(otpExpiryMinutes.toString(),2, "0"));
            	for (String lKey : pData.keySet()){
            		lDataHash.put(lKey,pData.get(lKey));
            	}
            	AuthenticationHandler.getInstance().verifyOrSendOTP(pRequest, pUserBean , pTemplatePrefix, lDataHash, lSendSms, lSendEmail);
            }
        }
    }
    
    public String getFullName(IAppUserBean pUserBean){
		String lName = "";
    	if(pUserBean!=null){
    		if(StringUtils.isNotEmpty(pUserBean.getFirstName())){
    			lName += pUserBean.getFirstName();
    		}
    		if(StringUtils.isNotEmpty(pUserBean.getMiddleName())){
    			if(StringUtils.isNotEmpty(lName)){
    				lName += " ";
    			}
    			lName += pUserBean.getMiddleName();
    		}
    		if(StringUtils.isNotEmpty(pUserBean.getLastName())){
    			if(StringUtils.isNotEmpty(lName)){
    				lName += " ";
    			}
    			lName += pUserBean.getLastName();
    		}
    	}
    	return lName;
    }
    
    	
			// change date

//    	if(isAutoEODEnabled()){
//    		Long lTriggerAfterMinutes = getTriggerAutoEODAfter();
//    		if(lTriggerAfterMinutes!=null && lTriggerAfterMinutes.longValue() > 0){
//    			Timestamp eodTriggerTime = new Timestamp(DateUtils.addMinutes(Calendar.getInstance().getTime(), lTriggerAfterMinutes.intValue()).getTime());
//    			System.out.println(" eodTriggerTime ------- "+eodTriggerTime);
//    			System.out.println(" eodTriggerTime Time ------- "+eodTriggerTime.getTime());
//    			System.out.println(" test ------- "+CommonUtilities.getCurrentDate().after(eodTriggerTime));
//    		}
//    	}
    
        	
		    //
		    //
//		 	try {
//				lByteArrayOutputStream.write(lDataP.getBytes());
//			} catch (Exception e1) {
//				System.out.println("error : " + e1.getMessage());
//			}
				// TODO Auto-generated catch block
		    //lParsedDataP = (Map<String, Object>)(new JsonSlurper()).parseText(lStringWriter.toString());
		 	
				// TODO Auto-generated catch block
    
	public long getDiffInDays(java.util.Date pDate1, java.util.Date pDate2) {
		return (pDate1.getTime() - pDate2.getTime()) / 86400000;
	}

	public String getErrorMessageString(Exception pException){
	    StringBuilder lResponse = new StringBuilder();
		if (pException instanceof CommonValidationException){
			List<ValidationFailBean> lValidationFailBeans = ((CommonValidationException) pException).getValidationFailBeans();
			int lPtr=0;
			for (ValidationFailBean lFailBean : lValidationFailBeans){
				if(lPtr>0) {
					lResponse.append(",");
				}
				lResponse.append(lFailBean.getName()).append(" : ").append(lFailBean.getMessage());
				lPtr++;
			}
		}else{
			lResponse.append(pException.getMessage());
		}
        return lResponse.toString();
	}

	public String returnErrorMessage(Exception pException){
		Map<String, Object> lErrorResponse = new HashMap<String, Object>();
	    List<String> lMessages = new ArrayList<String>();
		if (pException instanceof CommonValidationException){
			List<ValidationFailBean> lValidationFailBeans = ((CommonValidationException) pException).getValidationFailBeans();
			for (ValidationFailBean lFailBean : lValidationFailBeans){
				lMessages.add(lFailBean.getName() +" : "+ lFailBean.getMessage());
			}
		}else{
			lMessages.add(pException.getMessage());
		}
        lErrorResponse.put(CommonExceptionMapper.KEY_CODE, 200);
        lErrorResponse.put(CommonExceptionMapper.KEY_MESSAGES, lMessages);
        return new JsonBuilder(lErrorResponse).toString();
	}
	
	public List<String> getValidOldStatusForModification(){
		return validOldStatusForModification;
	}
	
	public  boolean isAutoEODEnabled(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        HashMap lMap = lRegistryHelper.getStructure(REGISTRY_EODAUTOMATIONSETTINGS);
		return (Boolean)lMap.get(ATTRIBUTE_EODEnable);
	}
	
	public  Long getTriggerAutoEODAfter(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        HashMap lMap = lRegistryHelper.getStructure(REGISTRY_EODAUTOMATIONSETTINGS);
		return (Long) lMap.get(ATTRIBUTE_EODStartAfter);
	}
	
	public  Long getExpiryDays(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        HashMap lMap = lRegistryHelper.getStructure(REGISTRY_REGISTRATIONSETTINGS);
		return (Long) lMap.get(ATTRIBUTE_EXPIRYDAYS);
	}
	
	public  String getRegistrationPassword(){
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        HashMap lMap = lRegistryHelper.getStructure(REGISTRY_REGISTRATIONSETTINGS);
		return (String) lMap.get(ATTRIBUTE_PASSWORD);
	}
	
	public boolean canModifyRegistration(String pUserDomain, CompanyApprovalStatus pApprovalStatus){
		boolean lCanModify = false;
    	if(AppConstants.DOMAIN_PLATFORM.equals(pUserDomain)){
    		lCanModify = CompanyApprovalStatus.Approved.equals(pApprovalStatus) || CompanyApprovalStatus.ApprovalModification.equals(pApprovalStatus) || CompanyApprovalStatus.ApprovalModificationSubmit.equals(pApprovalStatus);
    	}else if(AppConstants.DOMAIN_REGENTITY.equals(pUserDomain)){
    		lCanModify = (AppConstants.CompanyApprovalStatus.Submitted.equals(pApprovalStatus) || AppConstants.CompanyApprovalStatus.ReSubmitted.equals(pApprovalStatus) ||  AppConstants.CompanyApprovalStatus.Returned.equals(pApprovalStatus)  );
    	}else if(AppConstants.DOMAIN_REGUSER.equals(pUserDomain)){
    		lCanModify = (AppConstants.CompanyApprovalStatus.Draft.equals(pApprovalStatus) || 
    				AppConstants.CompanyApprovalStatus.Returned.equals(pApprovalStatus));
    	}else{
    		
    	}
		return lCanModify;
	}
	private FactoredBean getFactoredBean(Connection pConnection, Long pFuId) throws Exception{
		StringBuilder lSql = new StringBuilder();
	
		lSql.append(" SELECT *  FROM FactoringUnits, Instruments, Bids ");
		lSql.append(" WHERE FUID = INFUID AND FUBDID = BDID AND INRECORDVERSION>0 AND FURECORDVERSION>0 ");
		lSql.append(" AND FUID = ").append(pFuId);
		return  factoredDAO.findBean(pConnection, lSql.toString());
	}
	public Map<String, Object> getDeedOfAssignment(Long pFuId) throws Exception{
		try(Connection lConnection = DBHelper.getInstance().getConnection()){
	        FactoredBean lFactoredBean = getFactoredBean(lConnection, pFuId);
	        FactoringUnitBean lFactoringUnitBean = lFactoredBean.getFactoringUnitBean();
	    	InstrumentBean lInstrumentBean = lFactoredBean.getInstrumentBean();
	    	BidBean lBidBean = lFactoredBean.getBidBean();
			AppUserBean lSellerUserBean = getAdminUser(lFactoringUnitBean.getSupplier());
	    	AppEntityBean lSellerEntityBean = getAppEntityBean(lFactoringUnitBean.getSupplier());
	    	AppEntityBean lFinEntityBean = getAppEntityBean(lFactoringUnitBean.getFinancier());
	    	CompanyDetailBean lFinCompanyBean = getCompanyDetails(lConnection, lFinEntityBean.getCdId(),false);
	    	CompanyLocationBean lFinCompanyLocBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lFinEntityBean.getCdId());
	    	AppUserBean lBuyerUserBean = getAdminUser(lFactoringUnitBean.getPurchaser());
	    	AppEntityBean lBuyerEntityBean = getAppEntityBean(lFactoringUnitBean.getPurchaser());
	    	List<InstrumentBean> lInstList = new ArrayList<InstrumentBean>();
	    	if (lInstrumentBean.getGroupFlag()!=null && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
	    		InstrumentBO instrumentBO = new InstrumentBO();
	    		lInstList = instrumentBO.getClubbedBeans(lConnection, lInstrumentBean.getId());
	    	}
	    	Map<String,Object> lMap = new HashMap<>();
	    	lMap.put("user", lSellerEntityBean.getName());
	    	lMap.put("cin", lFinCompanyBean.getCinNo());
	    	lMap.put("companyName", lFinEntityBean.getName());
	    	lMap.put("location",lFinCompanyLocBean.getName() );
	    	lMap.put("assignor", "");
	    	lMap.put("representative", "");
	    	lMap.put("signOn", "");
		    List<Map<String, Object>> lInstDetails = new ArrayList<>();
		    Map<String,Object> lInstMap = new HashMap<>();
		    lInstMap.put("invoiceNo", lInstrumentBean.getInstNumber());
		    lInstMap.put("debtor", lBuyerEntityBean.getName());
		    lInstMap.put("invoiceDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lInstrumentBean.getInstDate()));
		    lInstMap.put("invoiceAmt", TredsHelper.getInstance().getFormattedAmount(lInstrumentBean.getAmount(),true));
		    lInstMap.put("fuId", lFactoringUnitBean.getId());
		    lInstMap.put("fuDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lFactoringUnitBean.getFactorStartDateTime()));
		    lInstMap.put("fuAmt", TredsHelper.getInstance().getFormattedAmount(lFactoringUnitBean.getFactoredAmount(),true));
		    lInstDetails.add(lInstMap);
	        if ( lInstList.size()>0 && !lInstList.isEmpty() ) {
	        	for (InstrumentBean lBean : lInstList) {
	        		lInstMap = new HashMap<>();
	        		lInstMap.put("invoiceNo", lBean.getInstNumber());
	    		    lInstMap.put("debtor", lBuyerEntityBean.getName());
	    		    lInstMap.put("invoiceDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lInstrumentBean.getInstDate()));
	    		    lInstMap.put("invoiceAmt",TredsHelper.getInstance().getFormattedAmount(lBean.getAmount(),true));
	    		    lInstMap.put("fuId", "");
	    		    lInstMap.put("fuDate", "");
	    		    lInstMap.put("fuAmt", "");
	    		    lInstDetails.add(lInstMap);
	        	}
	        }
	    	lMap.put("instDetils", lInstDetails);
	    	//
	        Map<String, Object> lUserDetails = new HashMap<>();
	    	lUserDetails.put("buyer",lBuyerEntityBean.getName());
	    	lUserDetails.put("buyerUser",lBuyerUserBean.getName());
	    	lUserDetails.put("seller",lSellerEntityBean.getName());
	    	lUserDetails.put("sellerUser",lSellerUserBean.getName());
	    	lUserDetails.put("fuId",lFactoringUnitBean.getId());
	    	lUserDetails.put("fuAmount",TredsHelper.getInstance().getFormattedAmount(lFactoringUnitBean.getFactoredAmount(),true));
	    	lUserDetails.put("bidRate",lFactoringUnitBean.getAcceptedRate());
	    	lUserDetails.put("acceptDate",FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lFactoringUnitBean.getAcceptDateTime()));
			String lSql = "SELECT * FROM InstrumentWorkFlow WHERE IWFInId = " + lInstrumentBean.getId() + " ORDER BY IWFStatusUpdateTime DESC, IWFId DESC";
			List<InstrumentWorkFlowBean> lInstWorkFlow = instrumentWorkFlowDAO.findListFromSql(lConnection, lSql,-1);
	    	if(lInstWorkFlow!=null) {
	    		boolean lFound = false;
				for(InstrumentWorkFlowBean lIWFBean : lInstWorkFlow) {
					if(InstrumentBean.Status.Counter_Approved.equals(lIWFBean.getStatus())) {
						lFound =true;
						//Counter party approval
			    		AppUserBean lAppUserBean = getAppUser(lIWFBean.getAuId());
			    		if(TredsHelper.getInstance().isCashInvoie(lAppUserBean)){
			    			lAppUserBean = TredsHelper.getInstance().getAdminUser(lInstrumentBean.getSupplier());
			    		}
			    		lUserDetails.put("cntrInstApprUser", lAppUserBean.getName());
			    		lUserDetails.put("cntrInstApprTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lIWFBean.getStatusUpdateTime()));
			    		lUserDetails.put("cntrUserName", lAppUserBean.getName());
			    		lUserDetails.put("cntrUserTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lIWFBean.getStatusUpdateTime()));
					}else if(InstrumentBean.Status.Checker_Approved.equals(lIWFBean.getStatus()) &&
							lInstrumentBean.getSupplier().equals(lIWFBean.getEntity())) {
						lFound =true;
				    	//Seller User name accepting instrument
			    		AppUserBean lAppUserBean = getAppUser(lIWFBean.getAuId());
			    		lUserDetails.put("supAcptInstUser", lAppUserBean.getName());
			    		lUserDetails.put("supAcptInstTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lIWFBean.getStatusUpdateTime()));
			    		if(!lUserDetails.containsKey("cntrUserName")) {
				    		lUserDetails.put("cntrUserName", lAppUserBean.getName());
				    		lUserDetails.put("cntrUserTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lIWFBean.getStatusUpdateTime()));
			    		}
					}
				}
				//TODO: counter party for Buyer seller link in case of auto approv
				if(!lFound) {
					if(lInstrumentBean.getSupplier().equals(lInstrumentBean.getCounterEntity())) {
						lUserDetails.put("cntrEntity",lInstrumentBean.getCounterEntity());
			    		if(!lUserDetails.containsKey("cntrUserName")) {
				    		lUserDetails.put("cntrUserName", lInstrumentBean.getCounterEntity());
				    		lUserDetails.put("cntrUserTime", "");
			    		}
					}
				}
	    	}
	    	if(!lUserDetails.containsKey("cntrUserName")) {
	    		lUserDetails.put("cntrUserName", "");
	    		lUserDetails.put("cntrUserTime", "");
	    	}
	    	//Financier User name placing bids
	    	AppUserBean lFinUserPlacingBid = getAppUser(lBidBean.getFinancierAuId());
	    	lUserDetails.put("finBidUser", lFinUserPlacingBid.getName());
	    	lUserDetails.put("finBidTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lBidBean.getTimestamp()));
	    	//
	    	lMap.put("userDetails", lUserDetails);
	    	//
	    	return lMap;
	    }catch(Exception lEx){
	    	logger.info("Error in getDeedOfAssignment" +lEx.getMessage());
	    }
		return null;
	}
	public void checkRegistringEntityAccess(Long pCdId, Long pRegEntityId) throws Exception{
		CompanyDetailBean pFilterBean = new CompanyDetailBean();
		pFilterBean.setId(pCdId);
		pFilterBean.setRecordCreator(pRegEntityId);
		CompanyDetailBean lCdBean = companyDetailProvDAO.findBean(DBHelper.getInstance().getConnection(), pFilterBean);
		if (lCdBean== null || !pRegEntityId.equals(lCdBean.getRecordCreator())){
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		}
	}
	
	public BankBranchDetailBean getBankBranchBean(Connection pConnection, String pIFSCCode) throws Exception {
        BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
        lFilterBean.setIfsc(pIFSCCode);
        BankBranchDetailBean lBankBranchDetailBean = (new GenericDAO<BankBranchDetailBean>(BankBranchDetailBean.class)).findByPrimaryKey(pConnection, lFilterBean);
    	return lBankBranchDetailBean;
    }

	public AppUserBean getAppUser(Long pAuId) throws Exception {
		if(pAuId==null || pAuId.longValue() == 0) return null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
        return lAppUserBean;
	}

	public void validateUserLimit(AppUserBean pUserBean, BigDecimal pAmount) throws CommonBusinessException{
		//check if Maker has limit been set
		if(!AppUserBean.Type.Admin.equals(pUserBean.getType())){
			//here we have to check whether he is not an aggregator ?
			if(pUserBean.hasUserLimit()){
				 if(!((pUserBean.getMinUserLimit()==null ||  pUserBean.getMinUserLimit().compareTo(pAmount) <= 0) &&
						(pUserBean.getMaxUserLimit()==null || pUserBean.getMaxUserLimit().compareTo(pAmount) >= 0)) ){
					 throw new CommonBusinessException("User Limit violation.");
				 }
			}
		}
	}
	
	public void validateCheckersLimit(Connection pConnection, Long pEntityMakerAuId, BigDecimal pAmount, Long pLocationId) throws CommonBusinessException{
		//The Entity can be Maker or Counter
		//In Case of Maker the User can send the Makers AuId  to get his checker
		//--- In Case of Counter check, the Introducer sends the Counter Entity and AuId as NULL 
		AppUserBean lMakerUser = null;
		try {
			lMakerUser = TredsHelper.getInstance().getAppUser(pEntityMakerAuId);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(lMakerUser!=null){
			try {
				appUserBO.populateCheckers(pConnection, lMakerUser);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//instrument chekcers if location is present (this is buyer or seller)
			//if location is absent then it is financier
			List<Long> lCheckerAuIds =  (pLocationId!=null?lMakerUser.getCheckersInstrument():lMakerUser.getCheckersBid());
			if(lCheckerAuIds!=null && !lCheckerAuIds.isEmpty()){
				AppUserBean lCheckUser = null;
				boolean lFoundCheckerWithLimit = false;
				for(Long lCheckerAuId : lCheckerAuIds){
					try {
						lCheckUser = TredsHelper.getInstance().getAppUser(lCheckerAuId);
						if(lCheckUser!=null && !AppUserBean.Type.Admin.equals(lCheckUser.getType())){
							try{
								validateUserLimit(lCheckUser, pAmount);
								//TODO: validate the Locations 
								//incase of financier the location id will be NULL
								if(pLocationId != null){
									if(lCheckUser.getLocationIdList()==null || lCheckUser.getLocationIdList().contains(pLocationId)){
										lFoundCheckerWithLimit = true;
									}
								}else{
									lFoundCheckerWithLimit = true;
								}
							}catch(Exception lEx){
								//do nothing
							}
						}
						if ( AppUserBean.Type.Admin.equals(lCheckUser.getType()) ){
							lFoundCheckerWithLimit = true;
						}
						if(lFoundCheckerWithLimit){
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if( !lFoundCheckerWithLimit ){
					throw new CommonBusinessException("No Checker with adequate limits found for checking the instrument.");
				}
			}
		}
	}
	
	public Vector getAppusers(String pDomain){
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
		try {
			return lMemoryTable.selectRow(IAppUserBean.f_Domain_LoginId, new String[]{pDomain});
		} catch (Exception e) {
			logger.info("Error in getRoleBasedUsers for Domain : "+pDomain+" , "+e.getMessage());
		}
		return null;
	}
	
	public List<AppUserBean> getRoleBasedUsers(String pDomain, Long pRoleId){
		List<AppUserBean> lReturnList = new ArrayList<>();
		Vector lList;
		try {
			lList = getAppusers(pDomain);
			AppUserBean lBean = null;
			for(int i=0; i<lList.size() ; i++ ){
				lBean = (AppUserBean) lList.get(i);
				if (lBean.getRmIdList().contains(pRoleId)){
					lReturnList.add(lBean);
				}
			}
		} catch (Exception e) {
			logger.info("Error in getRoleBasedUsers for Domain : "+pDomain+" , "+e.getMessage());
		}
		
		return lReturnList;
	}
	

    public void splitObligations(Connection pConnection,List<ObligationBean> pObligations, int pSplitCount) throws CommonBusinessException{
    	ArrayList<ObligationSplitsBean> lSplitList = new ArrayList<ObligationSplitsBean>();
    	
		try {
			Collections.sort(pObligations);
	    	//
			Map<String, List<IObligation>> lLegWiseCreditMap = new HashMap<String, List<IObligation>>();
	    	Map<String, List<IObligation>> lLegWiseDebitMap = new HashMap<String, List<IObligation>>();
	    	
	    	ObligationBean.Type lLeg = null; //L1,L2,L3
	    	ObligationBean.TxnType lTxnType = null; //Debit/Credit
	    	Map<String, List<IObligation>> lTmpLegWiseMap = null;
	    	List<IObligation> lTmpList = null;
	    	//
	    	for(IObligation lObligation : pObligations){
	    		lLeg = lObligation.getType();
	    		lTxnType = lObligation.getTxnType();
	    		if(TxnType.Debit.equals(lTxnType)){
	    			lTmpLegWiseMap = lLegWiseDebitMap;
	    		}else if(TxnType.Credit.equals(lTxnType)){
	    			lTmpLegWiseMap = lLegWiseCreditMap;
	    		}
	    		if(!lTmpLegWiseMap.containsKey(lLeg.getCode())){
	    			lTmpLegWiseMap.put(lLeg.getCode(), new ArrayList<IObligation>());
	    		}
	    		lTmpList = lTmpLegWiseMap.get(lLeg.getCode());
	    		lTmpList.add(lObligation);
	    	}
	    	//
	    	List<IObligation> lLegWiseDrList = null;
	    	List<IObligation> lLegWiseCrList = null;
	    	BigDecimal[] lSplitAmounts = null;
	    	//
	    	for(String lLegKey : lLegWiseCreditMap.keySet()){
	    		lLegWiseCrList = lLegWiseCreditMap.get(lLegKey);
	    		lLegWiseDrList = lLegWiseDebitMap.get(lLegKey);
	        	BigDecimal[] lTotalDrSplits = new BigDecimal[pSplitCount];
	        	BigDecimal[] lTotalCrSplits = new BigDecimal[pSplitCount];
	        	for (int lPtr=0;lPtr<pSplitCount;lPtr++) {
	        		lTotalDrSplits[lPtr] = BigDecimal.ZERO;
	        		lTotalCrSplits[lPtr] = BigDecimal.ZERO;
	        	}
	    		// split each debit obligation into equal parts
	    		// accumulate split index wise total of debit obligation amounts (lTotalDrSplits)
	    		for (IObligation lDrObligation : lLegWiseDrList){
	    			lSplitAmounts = splitAmounts(lDrObligation.getAmount(), pSplitCount);
	    			print(lDrObligation, lSplitAmounts);
	    			lSplitList.addAll(getObligations((ObligationBean) lDrObligation, lSplitAmounts));
	    			for (int lTmpPtr=0; lTmpPtr < pSplitCount; lTmpPtr++){
	    				lTotalDrSplits[lTmpPtr] = lTotalDrSplits[lTmpPtr].add(lSplitAmounts[lTmpPtr]);
	    			}
	    		}
	    		
	    		// split each credit obligation except last credit obligation in list. accumulate in lTotalCrSplits
	    		int lCount = lLegWiseCrList.size();
	    		for (int lCrPtr=0;lCrPtr<lCount-1;lCrPtr++) {
	    			IObligation lCrObligation = lLegWiseCrList.get(lCrPtr);
	    			lSplitAmounts = splitAmounts(lCrObligation.getAmount(), pSplitCount);
	    			print(lCrObligation, lSplitAmounts);
	    			lSplitList.addAll(getObligations((ObligationBean) lCrObligation, lSplitAmounts));
	    			for (int lTmpPtr=0; lTmpPtr < pSplitCount; lTmpPtr++){
	    				lTotalCrSplits[lTmpPtr] = lTotalCrSplits[lTmpPtr].add(lSplitAmounts[lTmpPtr]);
	    			}
	    		}
	    		// for last credit obligation, use remaining amounts i.e. lTotalDrSplits - lTotalCrSplits respectively
	    		lSplitAmounts = new BigDecimal[pSplitCount];
	    		for (int lTmpPtr=0; lTmpPtr < pSplitCount; lTmpPtr++){
	    			lSplitAmounts[lTmpPtr] = lTotalDrSplits[lTmpPtr].subtract(lTotalCrSplits[lTmpPtr]);
				}
	    		print(lLegWiseCrList.get(lCount-1), lSplitAmounts);
    			lSplitList.addAll(getObligations((ObligationBean) lLegWiseCrList.get(lCount-1), lSplitAmounts));
	    	} // end for
	    	System.out.println("over");

			for(ObligationSplitsBean lBean:lSplitList){
				obligationSplitsDAO.insert(pConnection, lBean);
			}
		} catch (Exception e) {
			logger.error("Error while splitting obligations. : " + e.getMessage());
			throw new CommonBusinessException("Error while splitting obligations.");
		}
    }
    
    private List<ObligationSplitsBean> getObligations(ObligationBean pParentBean, BigDecimal[] pSplitAmounts){
    	List<ObligationSplitsBean> lList = new ArrayList<ObligationSplitsBean>();
    	ObligationSplitsBean lOSBean = null;
    	for(int lPtr=0; lPtr<pSplitAmounts.length; lPtr++){
    		lOSBean = new ObligationSplitsBean();
    		lOSBean.setFuId(pParentBean.getFuId());
    		lOSBean.setPartNumber(new Long(lPtr+1));
			lOSBean.setStatus(pParentBean.getStatus());
			lOSBean.setPaymentSettlor(AppConstants.FACILITATOR_NPCI);
			lOSBean.setAmount(pSplitAmounts[lPtr]);
			lOSBean.setId(pParentBean.getId()); //was done in pervious code hence adding it
			lList.add(lOSBean);
    	}
    	return lList;
    }
    
    private static BigDecimal[] splitAmounts(BigDecimal pAmount, int pSplitCount){
    	BigDecimal[] lAmounts = new BigDecimal[pSplitCount];
    	int lCount = pSplitCount;
    	BigDecimal lAmount = BigDecimal.valueOf(pAmount.doubleValue());
    	BigDecimal lSplitAmount = null;
    	int lPtr=0;
    	//
    	//System.out.println("********************************** splitAmounts Starts **********************************");
    	//System.out.println("pAmount="+pAmount);
    	//System.out.println("lCount="+lCount);
    	//
    	while ( lCount > 0 ){
    		lSplitAmount  = lAmount.divide(new BigDecimal(lCount), MathContext.DECIMAL128).setScale(2,RoundingMode.HALF_UP);
    		lAmounts[lPtr++] = lSplitAmount;
    		lAmount = lAmount.subtract(lSplitAmount);
    		//System.out.println("lCount="+lCount+"; lSplitAmount="+lSplitAmount + "; lAmount="+lAmount);
    		lCount--;
    	}
    	//System.out.println("------");
    	//for(int lPtr2=0; lPtr2 < lAmounts.length; lPtr2++){
    	//	System.out.println("lAmounts["+lPtr2+"]="+lAmounts[lPtr2]);
    	//}
    	//System.out.println("********************************** splitAmounts Ends **********************************");
    	return lAmounts;
    }
	
    private static void print(IObligation pObligationBean, BigDecimal[] pSplitAmts) {
    	System.out.print(pObligationBean.getType() + ", " + pObligationBean.getTxnType() + ", " + pObligationBean.getAmount() + ", ");
    	for (BigDecimal lAmt : pSplitAmts)
    		System.out.print(lAmt + ", ");
    	System.out.println("");
    }	
    
    public Object getFactoredReportFinancier(Connection pConnection,FactoredReportBean pFilterBean,boolean pDownload,List<String> pFields ) throws Exception {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * From FactoredReport_VW ");
        StringBuilder lFilterSql = new StringBuilder();
        DBHelper lDbHelper = DBHelper.getInstance();
        if (pFilterBean.getFromDate()!=null && pFilterBean.getToDate()!=null){
        	lFilterSql.append(" AND FAFUACCEPTDATETIME BETWEEN ");
        	lFilterSql.append(lDbHelper.formatDate(pFilterBean.getFromDate()));
        	lFilterSql.append(" AND ");
        	lFilterSql.append(lDbHelper.formatDate(pFilterBean.getToDate()));
    	}else{
    		if (pFilterBean.getFromDate()!=null){
    			lFilterSql.append(" AND TO_DATE(FAFuAcceptDateTime,'dd-mm-yyyy') >=  ");
    			lFilterSql.append(lDbHelper.formatDate(pFilterBean.getFromDate()));
    		}
    		if (pFilterBean.getToDate()!=null){
    			lFilterSql.append(" AND TO_DATE(FAinFuAcceptDateTime,'dd-mm-yyyy') <= ");
    			lFilterSql.append(lDbHelper.formatDate(pFilterBean.getToDate()));
        	}
    	}
    	if (pFilterBean.getFuPurchaser()!=null){
    		lFilterSql.append(" AND fAFUPURCHASER = ").append(lDbHelper.formatString(pFilterBean.getFuPurchaser()));
    	}
    	if (pFilterBean.getInSalesCategory()!=null){
    		lFilterSql.append(" AND fainSalesCategory = ").append(lDbHelper.formatString(pFilterBean.getInSalesCategory()));
    	}
    	if (!lFilterSql.toString().isEmpty()){
    		lSql.append(" where 1=1");
    		lSql.append(lFilterSql.toString());
    		lSql.append(" order by fafuid");
    	}
    	List<FactoredReportBean> lList = factoredReportDAO.findListFromSql(pConnection, lSql.toString(), -1);
    	if (!pDownload){
    		List<Object[]> lResults = new ArrayList<Object[]>();
            for (FactoredReportBean lFactoredReportBean : lList) {
            	lFactoredReportBean.setPurchaser(getAppEntityBean(lFactoredReportBean.getFuPurchaser()).getName());
            	lFactoredReportBean.setSupplier(getAppEntityBean(lFactoredReportBean.getFuSupplier()).getName());
            	if (lFactoredReportBean.getGroupFlag()==null){
            		lResults.add(factoredReportDAO.getBeanMeta().formatAsArray(lFactoredReportBean, null, pFields, true));
            	}
            }
    		return lResults;
    	}
    	return lList;
    }

	public void getFactoredReportExcel(List<FactoredReportBean> lFactoredReportList, Map<String, byte[]> pFilesHash) throws Exception {
		List<FactoredReportBean> lGroupBeans = new ArrayList<FactoredReportBean>();
		Map<Long,Object[]> lDataHash = null;
		Map<Long,Object[]> lGroupDataHash = null;
		int lCount = 1;
		if (lDataHash==null || lDataHash.isEmpty()) {
			lDataHash = new HashMap<>();
			lDataHash.put(new Long(0),FactoredReportBean.getHeaders());
		}
		for (FactoredReportBean lFactoredReportBean : lFactoredReportList) {
			lFactoredReportBean.setPurchaser(getAppEntityBean(lFactoredReportBean.getFuPurchaser()).getName());
        	lFactoredReportBean.setSupplier(getAppEntityBean(lFactoredReportBean.getFuSupplier()).getName());
			if(lFactoredReportBean.getGroupFlag()!=null) {
				lGroupBeans.add(lFactoredReportBean);
				continue;
			}
			lDataHash.put(new Long(lCount), lFactoredReportBean.getObjectArrayForExcell());
			if (lFactoredReportBean.getInInstImage()!=null) {
				try{
					byte[] lByteArr = FileUploadHelper.readFile(lFactoredReportBean.getInInstImage(), null, "INSTRUMENTS");
					if (lByteArr!=null) {
						pFilesHash.put(lFactoredReportBean.getInInstImage(),lByteArr);
					}
				}catch (Exception e) {
					
				}
			}
			lCount++;
		}
		lCount = 1;
		if (lGroupDataHash==null || lGroupDataHash.isEmpty()) {
			lGroupDataHash = new HashMap<>();
			lGroupDataHash.put(new Long(0),FactoredReportBean.getHeaders());
		}
		for (FactoredReportBean lFactoredReportBean : lGroupBeans) {
			lGroupDataHash.put(new Long(lCount), lFactoredReportBean.getObjectArrayForExcell());
			if (lFactoredReportBean.getInInstImage()!=null) {
				try {
					byte[] lByteArr = FileUploadHelper.readFile(lFactoredReportBean.getInInstImage(), null, "INSTRUMENTS");
					if (lByteArr!=null) {
						pFilesHash.put(lFactoredReportBean.getInInstImage(),lByteArr);
					}	
				}catch (Exception e){
					logger.info("Error in getFactoredReportExcel" + e.getMessage());
				}
			}
			lCount++;
		}
		Object[] lWorkBookData = new Object[]{lDataHash,lGroupDataHash};
        // Blank workbook 
        XSSFWorkbook lWorkbook = new XSSFWorkbook(); 
        // Create a blank sheet 
        XSSFSheet[] lSheetArr = new XSSFSheet[]{lWorkbook.createSheet("Factored Details"),lWorkbook.createSheet("Group Instruments")};
        DataFormat lDataFormat = lWorkbook.createDataFormat();
        CellStyle lDateCellStyle = lWorkbook.createCellStyle();
        lDateCellStyle.setDataFormat(lDataFormat.getFormat("dd-mm-yyyy"));
        CellStyle lNumCellStyle = lWorkbook.createCellStyle();
        lNumCellStyle.setDataFormat(lDataFormat.getFormat("##0"));
        for (int i=0; i<lWorkBookData.length; i++ ){
        	 for (Long lKey : ((Map<Long, Object[]>) lWorkBookData[i]).keySet()) { 
                     Row row = lSheetArr[i].createRow(lKey.intValue()); 
                     lSheetArr[i].autoSizeColumn(1000000000);
                     Object[] lObjArr = (Object[]) ((Map<Long, Object[]>) lWorkBookData[i]).get(lKey);
                     int lCellnum = 0; 
                     for (Object lData : lObjArr) { 
                         Cell lCell = row.createCell(lCellnum++); 
                         if (lData instanceof String) {
                             lCell.setCellValue((String)lData);
             	        } else if (lData instanceof BigDecimal) {
             	            lCell.setCellValue(((BigDecimal)lData).doubleValue());
             	            lCell.setCellStyle(lNumCellStyle);
                         } else if(lData instanceof Integer) {
                         	lCell.setCellValue(((Integer) lData).intValue());
                         	lCell.setCellStyle(lNumCellStyle);
                         } else if(lData instanceof Double) {
                         	lCell.setCellValue(((Double) lData).doubleValue());
                         	lCell.setCellStyle(lNumCellStyle);
                         } else if(lData instanceof Float) {
                         	lCell.setCellValue(((Float) lData).doubleValue());
                         	lCell.setCellStyle(lNumCellStyle);
                         } else if(lData instanceof Long) {
                         	lCell.setCellValue(((Long) lData).longValue());
                         	lCell.setCellStyle(lNumCellStyle);
                         } else if (lData instanceof Date) {
                         	lCell.setCellValue((Date)lData);
                         	lCell.setCellStyle(lDateCellStyle);
                         } else if (lData instanceof Timestamp) {
                          	lCell.setCellValue((java.util.Date)lData);
                          	lCell.setCellStyle(lDateCellStyle);
                     } 
                 } 
        	 }
        }
        ByteArrayOutputStream lByetOutputStream = new ByteArrayOutputStream();
    	try {
    		lWorkbook.write(lByetOutputStream);
    		lByetOutputStream.close();
    		pFilesHash.put("FactoredReport.xlsx",lByetOutputStream.toByteArray());
    	}catch(Exception e){
    		
    	}
	}
	
	public void addZipToFile(ZipOutputStream pOut, String lFileName, byte[] pByteArray) throws Exception {
		pOut.putNextEntry(new ZipEntry(lFileName.replace("/", "_")));
		BufferedInputStream lInputStream = new BufferedInputStream(new ByteArrayInputStream(pByteArray));
		byte buffer[] = new byte[0xffff];
		try{
			int b;
			while ((b = lInputStream.read(buffer)) != -1){
				pOut.write(buffer, 0, b);
			}
		}catch(Exception e){
			
		}
		lInputStream.close();
	}
	
	
	public void bringBackToAuction(Connection pConnection,Long pFuId, AppUserBean pUserBean) throws Exception{
		//Find the FU and Inst from FUid
		//Update the Factoring Unit with the Old InstId
		//Cancel the Instrument in Monetago - If Normal then cancel one else if it is group cancel all the childs
		//Audit the Instrument
		//Update the Instruments Status(COUAPP), FUId(null), LedgerId(null), FactoringId(null), InCounterModified(null)
		//Workflow update the remarks and status of instrument
		//Register the New Instrument if normal then single else all the childs
		//Convert the Instrument to Factoring Unit.
		//
		pConnection.setAutoCommit(false);
		//
		List<InstrumentBean> lChildInstList = null;
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		lFactoringUnitBean.setId(pFuId);
		lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitBean);
		if (!FactoringUnitBean.Status.Leg_1_Failed.equals(lFactoringUnitBean.getStatus())){
			throw new CommonBusinessException("Invalid factoring unit status.");
		}
		//
		InstrumentBean lInstBean = new InstrumentBean();
		lInstBean.setFuId(pFuId);
		lInstBean = instrumentDAO.findBean(pConnection, lInstBean);
		if (lInstBean==null) {
			throw new CommonBusinessException("No Instrument Linked to factoring units.");
		}
		if (!InstrumentBean.Status.Leg_1_Failed.equals(lInstBean.getStatus())){
			throw new CommonBusinessException("Invalid Instrument status.");
		}
		lInstBean.setStatusRemarks("Rolled back to Auction (old Fuid : "+lFactoringUnitBean.getId()+ " )");
		instrumentDAO.insertAudit(pConnection, lInstBean,AuditAction.Update,pUserBean.getId());
		//
		lFactoringUnitBean.setOldInstId(lInstBean.getId());
		factoringUnitDAO.update(pConnection, lFactoringUnitBean,FactoringUnitBean.FIELDGROUP_BRINGBACKTOAUCTION);
		//
		if (StringUtils.isNotEmpty(lInstBean.getMonetagoLedgerId())) {
			if (MonetagoTredsHelper.getInstance().performMonetagoCheck()) {
				if (!CommonAppConstants.Yes.Yes.equals(lInstBean.getGroupFlag())) {
					Map<String, String> lResult = new HashMap<String, String>();
					String lInfoMessage = "Instrument No :" + lInstBean.getId();
					lResult = MonetagoTredsHelper.getInstance().cancel(lInstBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced, lInfoMessage,lInstBean.getId());
					if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
						lInstBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
						lInstBean.setMonetagoLedgerId(null);
						lInstBean.setMonetagoFactorTxnId(null);
						logger.info("Message Success :  "+ lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					} else {
						logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						throw new CommonBusinessException("Error while cancelling : "										+ lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					}
				}
			}
				}else {
			if (MonetagoTredsHelper.getInstance().performMonetagoCheck()) {
				if (CommonAppConstants.Yes.Yes.equals(lInstBean.getGroupFlag())) {
					InstrumentBean lGrBean = new InstrumentBean();
					lGrBean.setGroupInId(lInstBean.getId());
					lChildInstList = instrumentDAO.findList(pConnection, lInstBean, (String) null);
					if(lChildInstList!=null && !lChildInstList.isEmpty()) {
						for(InstrumentBean lChildBean:lChildInstList) {
							//audit the instrument and then cancel
							if (StringUtils.isNotEmpty(lChildBean.getMonetagoLedgerId())) {
							cancelMonetago(pConnection, lChildBean);
							}
						}
					}
				}
			}
		}
		//Audit the previous instrument
		
		//
		lInstBean.setStatus(InstrumentBean.Status.Counter_Approved);
		lInstBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		lInstBean.setMonetagoLedgerId(null);
		lInstBean.setMonetagoFactorTxnId(null);
		instrumentDAO.update(pConnection, lInstBean, InstrumentBean.FIELDGROUP_UPDATECONVFACTUNIT);
		
		InstrumentWorkFlowBean lIWFBean = new InstrumentWorkFlowBean();
		lIWFBean.setInId(lInstBean.getId());
		lIWFBean.setStatus(lInstBean.getStatus());
		lIWFBean.setStatusRemarks("Back to Auction");
		lIWFBean.setEntity(pUserBean.getDomain());
		lIWFBean.setAuId(pUserBean.getId());
		lIWFBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		instrumentWorkFlowDAO.insert(pConnection, lIWFBean);
		//
		if (lInstBean.getGroupFlag()==null) {
			registerMonetago(lInstBean);
		}else {
			if( lChildInstList != null && !lChildInstList.isEmpty()) {
			for (InstrumentBean lChildInst : lChildInstList) {
				registerMonetago(lChildInst);
				}
			}
		}
		//
		InstrumentBO lInstrumentBO = new InstrumentBO();
		lInstrumentBO.convertToFactoringUnit(pConnection, lInstBean, pUserBean, false, FactoringUnitBean.Status.Active);
		pConnection.commit();		
	}
	
	private void cancelMonetago(Connection pConnection, InstrumentBean pInstrumentBean) throws  Exception{
		String lInfoMessage = null;
		Map<String,String> lResult= new HashMap<String, String>();
 		lInfoMessage = "Instrument No :"+pInstrumentBean.getId() +" " + "FactoringUnit No :"+pInstrumentBean.getFuId();
		lResult=MonetagoTredsHelper.getInstance().cancel(pInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced,lInfoMessage,pInstrumentBean.getId());
		if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
				pInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
				pInstrumentBean.setMonetagoLedgerId(null);
				pInstrumentBean.setMonetagoFactorTxnId(null);
				logger.info("Message Success :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
		}else{
			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			throw new CommonBusinessException("Error while Settlement : " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
		}
		instrumentDAO.update(pConnection, pInstrumentBean,InstrumentBean.FIELDGROUP_UPDATEMONETAGOCANCEL);
	}
	
	public static Object[] getPurchaserSupplierList(String pFinancier) throws Exception{
		Object[] lReturn = null;
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" select FUPURCHASER,FUSUPPLIER from Factoringunits ");
		lSql.append(" where FUFINANCIER = " + lDbHelper.formatString(pFinancier));
		lSql.append(" group by FUPURCHASER,FUSUPPLIER ");		
		Connection lConnection = DBHelper.getInstance().getConnection();
		Statement lStatement = lConnection.createStatement();
		ResultSet lResultSet = lStatement.executeQuery(lSql.toString());
		List<String> lPurchasers = new ArrayList<String>();
		List<String> lSupplier = new ArrayList<String>();
		try{
			while(lResultSet.next()){
				lPurchasers.add(lResultSet.getString("FUPURCHASER"));
				lSupplier.add(lResultSet.getString("FUSUPPLIER"));
			}
			lReturn = new Object[]{ lPurchasers,lSupplier };
			return lReturn ;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public Map<String, Object> getPyamentDetails(Connection pConnection, Long pFuId, AppUserBean pUserBean) throws Exception {
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		lFactoringUnitBean.setId(pFuId);
		lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitBean);
		if(lFactoringUnitBean == null){
			throw new CommonBusinessException("Factoring unit not found");
		}
		ObligationBean lFilterBean = new ObligationBean();
		lFilterBean.setFuId(pFuId);
		lFilterBean.setType(Type.Leg_1);
		lFilterBean.setTxnEntity(lFactoringUnitBean.getFinancier());
		ObligationBean lObligationBean = obligationDAO.findBean(pConnection, lFilterBean);
		Map<String, Object> lMap = new HashMap<String, Object>();
		lMap.put("paymentDetails1", lObligationBean.getPayDetail1());
		lMap.put("paymentDetails2", lObligationBean.getPayDetail2());
		lMap.put("paymentDetails3", lObligationBean.getPayDetail3());
		lMap.put("paymentDetails4", lObligationBean.getPayDetail4());
		lMap.put("fuId", lObligationBean.getFuId());
		return lMap;
	}
	
	private void registerMonetago(InstrumentBean pInstrumentBean) throws CommonBusinessException {
		if (MonetagoTredsHelper.getInstance().performMonetagoCheck()) {
			String lPurchaserGSTN = pInstrumentBean.getPurGstn();
			String pSupplierGSTN = pInstrumentBean.getSupGstn();
			String pInstNumber = pInstrumentBean.getInstNumber();
			Date pInstDate = pInstrumentBean.getInstDate();
			BigDecimal pAmount = pInstrumentBean.getAmount();
			Map<String, String> lResult = new HashMap<String, String>();
			String lInfoMessage = " Invoice No :" + pInstrumentBean.getInstNumber() + " Instrument No :"+ pInstrumentBean.getId();
			lResult = MonetagoTredsHelper.getInstance().register(pSupplierGSTN, lPurchaserGSTN, pInstNumber,pInstDate, pAmount, lInfoMessage,pInstrumentBean.getId());
			if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID))) {
				pInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
				logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			} else {
				pInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
				logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
				throw new CommonBusinessException("Error while submitting (Please save): "+ lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			}
		}
	}

	public BigDecimal getAcceptedRate(Connection pConnection, Long pFuId) throws Exception {
		FactoringUnitBean lFilterBean = new FactoringUnitBean();
		lFilterBean.setId(pFuId);
		FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFilterBean);
		return lFactoringUnitBean.getAcceptedRate();
	}
	
    public static void appendMessage(List<Map<String, Object>> pMessages, String pAction, String pMessage, Map<String, Object> lRetMap) {
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("act", pAction);
        lMap.put("rem", pMessage);
        if (lRetMap!=null) {
        	if (lRetMap.containsKey("responseStatus")) {
        		lMap.put("responseStatus", lRetMap.get("responseStatus").toString());
        	}
        	if (lRetMap.containsKey("currentStatus")) {
        		lMap.put("currentStatus", lRetMap.get("currentStatus").toString());
        	}
        }
        if(pMessages!=null)
        	pMessages.add(lMap);
    }
    
    public AppUserBean getAppUserBean(String pDomain,String pLogin) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        AppUserBean lUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, 
                new String[]{pDomain, pLogin});
        return lUserBean;
	}
    
    public CompanyDetailBean getCompanyDetailsForShareEntity(Connection pConnection, Long pCompanyId){
		CompanyDetailBean lCDBean = new CompanyDetailBean();
		lCDBean.setId(pCompanyId);
		try {
			lCDBean = companyDetailProvDAO.findByPrimaryKey(pConnection, lCDBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lCDBean;
    }
    
	public String getCompanyShareIndividualFullName(CompanyShareIndividualBean pCompanyShareIndividualBean) {
        StringBuilder lName = new StringBuilder();
        
        if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()) ||
                        CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()) ||
                        CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()) )
        {
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getSalutation()))
                        lName.append(pCompanyShareIndividualBean.getSalutation());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getFirstName());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getMiddleName());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getLastName());
        }
        return lName.toString();
  }
	
  public String getCompanyShareEntityFullName(CompanyShareEntityBean pCompanyShareIndividualBean) {
        StringBuilder lName = new StringBuilder();
        
        if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()) ||
                        CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()) ||
                        CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()) )
        {
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getSalutation()))
                        lName.append(pCompanyShareIndividualBean.getSalutation());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getFirstName());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getMiddleName());
                if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()))
                        lName.append(" ").append(pCompanyShareIndividualBean.getLastName());
        }
        return lName.toString();
  }
  
  private List<AppUserBean> getAllActiveUsers(String pDomain) throws Exception {
	MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	Vector<AppUserBean> lRows = lMemoryTable.selectRow(IAppUserBean.f_Domain_LoginId, new Object[]{pDomain});
	 List<AppUserBean> lList = new ArrayList<AppUserBean>();
     for (AppUserBean lAppUserBean : lRows) {
        if (IAppUserBean.Status.Active.equals(lAppUserBean.getStatus())) {
        	lList.add(lAppUserBean);
        }
     }
	return lList;
  }
  
  public HashMap<String, HashSet<Long>> getLevelwiseUsers(String pDomain) throws Exception {
	  List<AppUserBean> lList = getAllActiveUsers(pDomain);
	  HashMap<String, HashSet<Long>> lLevelwiseUsersHash = getGlobalCheckerSetting(pDomain);
	  if (lList!=null && !lList.isEmpty()) {
		  HashSet<Long> lTmpHash = null;
		  for (AppUserBean lAppUserBean : lList) {
			  if ( lAppUserBean.getInstLevel()!=null && lAppUserBean.getInstLevel()>0 ) {
				  if (lLevelwiseUsersHash.containsKey(AppConstants.INSTRUMENT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getInstLevel())){
					  lTmpHash = lLevelwiseUsersHash.get(AppConstants.INSTRUMENT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getInstLevel());
					  lTmpHash.add(lAppUserBean.getId());
				  }
			  }
			  if ( lAppUserBean.getInstCntrLevel()!=null && lAppUserBean.getInstCntrLevel()>0 ) {
				  if (lLevelwiseUsersHash.containsKey(AppConstants.INSTRUMENT_COUNTER_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getInstCntrLevel())){
					  lTmpHash = lLevelwiseUsersHash.get(AppConstants.INSTRUMENT_COUNTER_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getInstCntrLevel());
					  lTmpHash.add(lAppUserBean.getId());
				  }
			  }
			  if ( lAppUserBean.getBidLevel()!=null && lAppUserBean.getBidLevel()>0 ) {
					if (lLevelwiseUsersHash.containsKey(AppConstants.BID_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBidLevel())){
						lTmpHash = lLevelwiseUsersHash.get(AppConstants.BID_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBidLevel());
						lTmpHash.add(lAppUserBean.getId());				  
					}
			  }
			  if ( lAppUserBean.getPlatformLimitLevel()!=null && lAppUserBean.getPlatformLimitLevel()>0 ) {
					if (lLevelwiseUsersHash.containsKey(AppConstants.PLATFORM_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getPlatformLimitLevel())){
						lTmpHash = lLevelwiseUsersHash.get(AppConstants.PLATFORM_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getPlatformLimitLevel());
						lTmpHash.add(lAppUserBean.getId());
					}				  
			  }
			  if ( lAppUserBean.getBuyerSellerLimitLevel()!=null && lAppUserBean.getBuyerSellerLimitLevel()>0 ) {
					if (lLevelwiseUsersHash.containsKey(AppConstants.BUYERSELLER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBuyerSellerLimitLevel())){
						lTmpHash = lLevelwiseUsersHash.get(AppConstants.BUYERSELLER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBuyerSellerLimitLevel());
						lTmpHash.add(lAppUserBean.getId());
					}				  
			  }
			  if ( lAppUserBean.getBuyerLimitLevel()!=null && lAppUserBean.getBuyerLimitLevel()>0 ) {
					if (lLevelwiseUsersHash.containsKey(AppConstants.BUYER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBuyerLimitLevel())){
						lTmpHash = lLevelwiseUsersHash.get(AppConstants.BUYER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getBuyerLimitLevel());
						lTmpHash.add(lAppUserBean.getId());
					}				  
			  }
			  if ( lAppUserBean.getUserLimitLevel()!=null && lAppUserBean.getUserLimitLevel()>0 ) {
					if (lLevelwiseUsersHash.containsKey(AppConstants.USER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getUserLimitLevel())){
						lTmpHash = lLevelwiseUsersHash.get(AppConstants.USER_LIMIT_CHECKER+CommonConstants.KEY_SEPARATOR+lAppUserBean.getUserLimitLevel());
						lTmpHash.add(lAppUserBean.getId());
					}				  
			  }
		  }
	  }
	  return lLevelwiseUsersHash;
  }
  
  public HashMap<String, Object> getCheckerInfo(AppEntityBean pEntityBean) throws Exception {
	MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	HashMap<String, HashSet<Long>> lDataHash= getLevelwiseUsers(pEntityBean.getCode());
	HashMap<String, Object> lTempRtnMap = new HashMap<>();
	Map<String,String> lTmpMap = null;
	HashMap<String, Object> lReturnMap = new HashMap<>();
	AppUserBean lAppUserBean = null;
	if (lDataHash!=null && !lDataHash.isEmpty()) {
		for (String lKey :lDataHash.keySet() ) {
			List<Object> lTmpList = new ArrayList<>();
			if (lDataHash.get(lKey)==null || !lDataHash.get(lKey).isEmpty()) {
				for(Long lId : lDataHash.get(lKey)) {
					lTmpMap = new HashMap<>();
					lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lId});
					lTmpMap.put("login",lAppUserBean.getLoginId());
					lTmpMap.put("name",lAppUserBean.getName());
					String[] arrOfStr = CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR); 
			        lTmpMap.put("level",arrOfStr[1]);
			        lTmpMap.put("levelName",arrOfStr[0]);
					lTmpList.add(lTmpMap);
				}
			}else {
				lTmpMap = new HashMap<>();
				lTmpMap.put("login","");
				lTmpMap.put("name","");
				String[] arrOfStr = CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR); 
		        lTmpMap.put("level",arrOfStr[1]);
		        lTmpMap.put("levelName",arrOfStr[0]);
				lTmpList.add(lTmpMap);
			}
			lTempRtnMap.put(lKey, lTmpList);
		}
	}
	if(pEntityBean.isPurchaser() || pEntityBean.isSupplier()) {
		lReturnMap.put("Instrument",new ArrayList<>());
		lReturnMap.put("Instrument_Counter",new ArrayList<>());
	}
	if (pEntityBean.isFinancier()) {
		lReturnMap.put("Bid",new ArrayList<>());
		lReturnMap.put("Platform_Limit",new ArrayList<>());
		lReturnMap.put("User_Limit",new ArrayList<>());
		lReturnMap.put("BuyerSeller_Limit",new ArrayList<>());
		lReturnMap.put("Buyer_Limit",new ArrayList<>());
	}
	List<Object> lTmpList = null;
	for (String lKey: lTempRtnMap.keySet()) {
		String[] arrOfStr = CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR); 
		if(pEntityBean.isPurchaser() || pEntityBean.isSupplier()) {
			if (arrOfStr[0].equals(AppConstants.INSTRUMENT_CHECKER) ) {
				lTmpList = (List<Object>) lReturnMap.get("Instrument");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
			if (arrOfStr[0].equals(AppConstants.INSTRUMENT_COUNTER_CHECKER) && lReturnMap.containsKey("Instrument_Counter") ) {
				lTmpList = (List<Object>) lReturnMap.get("Instrument_Counter");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
		}
		if (pEntityBean.isFinancier()) {
			if (arrOfStr[0].equals(AppConstants.BID_CHECKER) && lReturnMap.containsKey("Bid") ) {
				lTmpList = (List<Object>) lReturnMap.get("Bid");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
			if (arrOfStr[0].equals(AppConstants.PLATFORM_LIMIT_CHECKER) && lReturnMap.containsKey("Platform_Limit") ) {
				lTmpList = (List<Object>) lReturnMap.get("Platform_Limit");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
			if (arrOfStr[0].equals(AppConstants.BUYER_LIMIT_CHECKER) && lReturnMap.containsKey("Buyer_Limit") ) {
				lTmpList = (List<Object>) lReturnMap.get("Buyer_Limit");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
			if (arrOfStr[0].equals(AppConstants.BUYERSELLER_LIMIT_CHECKER) && lReturnMap.containsKey("BuyerSeller_Limit") ) {
				lTmpList = (List<Object>) lReturnMap.get("BuyerSeller_Limit");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
			if (arrOfStr[0].equals(AppConstants.USER_LIMIT_CHECKER) && lReturnMap.containsKey("User_Limit") ) {
				lTmpList = (List<Object>) lReturnMap.get("User_Limit");
				lTmpList.add(lTempRtnMap.get(lKey));
			}
		}
	}
	return lReturnMap;
  }
  
  public HashMap<String, HashSet<Long>> getGlobalCheckerSetting(String pDomain) throws Exception {
	  AppEntityBean lAppEntityBean = getAppEntityBean(pDomain);
	  HashMap<String, Long> lGlobalLevel = new HashMap<>();
	  HashMap<String, HashSet<Long>> lLevelwiseUsersHash = new HashMap<>();
	  if ((lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()) && (lAppEntityBean.getInstLevel()!=null || lAppEntityBean.getInstLevel()>0)) {
		  lGlobalLevel.put(AppConstants.INSTRUMENT_CHECKER, lAppEntityBean.getInstLevel());
	  }
	  if ((lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()) && (lAppEntityBean.getInstCntrLevel()!=null || lAppEntityBean.getInstCntrLevel()>0)) {
		  lGlobalLevel.put(AppConstants.INSTRUMENT_COUNTER_CHECKER, lAppEntityBean.getInstCntrLevel() );
	  }
	  if ( (lAppEntityBean.isFinancier()) && (lAppEntityBean.getBidLevel()!=null || lAppEntityBean.getBidLevel()>0)) {
		  lGlobalLevel.put(AppConstants.BID_CHECKER, lAppEntityBean.getBidLevel());
	  }
	  if ( (lAppEntityBean.isFinancier()) && (lAppEntityBean.getPlatformLimitLevel()!=null || lAppEntityBean.getPlatformLimitLevel()>0)){
		  lGlobalLevel.put(AppConstants.PLATFORM_LIMIT_CHECKER, lAppEntityBean.getPlatformLimitLevel());
	  }
	  if ( (lAppEntityBean.isFinancier()) && (lAppEntityBean.getBuyerLimitLevel()!=null || lAppEntityBean.getBuyerLimitLevel()>0)){
		  lGlobalLevel.put(AppConstants.BUYER_LIMIT_CHECKER, lAppEntityBean.getBuyerLimitLevel());
	  }
	  if ( (lAppEntityBean.isFinancier()) && (lAppEntityBean.getBuyerSellerLimitLevel()!=null || lAppEntityBean.getBuyerSellerLimitLevel()>0)){
		  lGlobalLevel.put(AppConstants.BUYERSELLER_LIMIT_CHECKER, lAppEntityBean.getBuyerSellerLimitLevel());
	  }
	  if ( (lAppEntityBean.isFinancier()) && (lAppEntityBean.getUserLimitLevel()!=null || lAppEntityBean.getUserLimitLevel()>0)){
		  lGlobalLevel.put(AppConstants.USER_LIMIT_CHECKER, lAppEntityBean.getUserLimitLevel());
	  }
	  for (String lLevelKey: lGlobalLevel.keySet()) {
		  if (lGlobalLevel.get(lLevelKey).intValue()>0) {
			  for (int lPtr=1; lPtr<=lGlobalLevel.get(lLevelKey).intValue(); lPtr++) {
				  if (!lLevelwiseUsersHash.containsKey(lLevelKey+CommonConstants.KEY_SEPARATOR+lPtr)){
					  lLevelwiseUsersHash.put(lLevelKey+CommonConstants.KEY_SEPARATOR+lPtr, new HashSet<Long>());
				  }
			  }
		  }
	  }
	  return lLevelwiseUsersHash;
  }
  
  public HashMap<Long, Object> getCheckersList(String pDomain ,MakerCheckerMapBean.CheckerType pCheckerType) throws Exception {
	  List<AppUserBean> lList = getAllActiveUsers(pDomain);
	  MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	  HashMap<Long, Object> lMap = new HashMap<>();
	  if (lList!=null && !lList.isEmpty()) {
		  for (AppUserBean lUserBean : lList) {
			  List<AppUserBean> lRtnList = getAllActiveUsers(pDomain);
			  try(Connection lConn = DBHelper.getInstance().getConnection();){
				  List<MakerCheckerMapBean> lMCMList = appUserBO.getCheckers(lConn, lUserBean.getId(), pCheckerType);
				  for (MakerCheckerMapBean lMCMBean : lMCMList) {
					  if (!lMap.containsKey(lUserBean.getId())){
						  lMap.put(lUserBean.getId(),new ArrayList<AppUserBean>());
					  }
					  List<AppUserBean> lTmpList =(List<AppUserBean>) lMap.get(lUserBean.getId());
					  lTmpList.add((AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lMCMBean.getCheckerId()}));
				  }
			  }
		  }
	  }
	  return lMap;
  }
  
 public static IKeyValEnumInterface getValue(Class pClass, String pField, String pValue) {
	 Map<String, Map<String, IKeyValEnumInterface>> lDataSetKeyValueReverseMap = new HashMap<String, Map<String, IKeyValEnumInterface>>();
     BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(pClass);
     BeanFieldMeta lFieldMeta = lBeanMeta.getFieldMap().get(pField);
     IKeyValEnumInterface lKeyVal = lFieldMeta.getDataSetKeyValueMap().get(pValue); 
     if (lKeyVal == null) {
             lKeyVal = lFieldMeta.getDataSetKeyValueReverseMap().get(pValue); 
     }
     if (lKeyVal == null) {
             String lMetaKey = pClass.getName() + "|" + pField;
                 Map<String, IKeyValEnumInterface> lMap = lDataSetKeyValueReverseMap.get(lMetaKey);
                 if(lMap == null) {
                         lMap = new HashMap<String, IKeyValEnumInterface>();
                         Map lRevMap = lFieldMeta.getDataSetKeyValueReverseMap();
                         for(Object lKeyObj : lRevMap.keySet()) {
                                 String lKey = (String) lKeyObj;
                                 lMap.put(lKey.toUpperCase(), (IKeyValEnumInterface) lRevMap.get(lKeyObj));
                         }
                         lDataSetKeyValueReverseMap.put(lMetaKey,lMap);
                 }
                 lKeyVal = lMap.get(pValue.toUpperCase());
     }
             return lKeyVal;
 }
 
   public CompanyContactBean.Gender getGender(String pSalutation) {
	  if(StringUtils.isNotBlank(pSalutation)){
		  if("Mr.".toUpperCase().equals(pSalutation.toUpperCase())){
			  return Gender.Male;
		  }
		  if("Mrs.".toUpperCase().equals(pSalutation.toUpperCase()) ||
				  "Ms.".toUpperCase().equals(pSalutation.toUpperCase())){
			  return Gender.Female;
		  }
	  }
	  return null;
  }

   public String getCsv(Connection pConnection, BeanMeta pBeanMeta, String pFieldGroup, List<String> pFields, ArrayList<Object> pFilterList, IAppUserBean pAppUserBean) throws Exception {
	   final String PIPE = "|";
	   final String NEWLINE = "\r\n";
	   StringBuilder lBuilder = new StringBuilder();
	   List<BeanFieldMeta> lListFieldMeta =  pBeanMeta.getFieldMetaList(pFieldGroup, pFields);
       for (int i=0; i<lListFieldMeta.size() ; i++) {
    	   lBuilder.append(lListFieldMeta.get(i).getName());
    	   if (i<lListFieldMeta.size()-1) {
    		   lBuilder.append(PIPE);
    	   }
       }
       Object lTmp = null;
       for (Object lBean : pFilterList) {
    	   lBuilder.append(NEWLINE);
	       for (int i=0; i<lListFieldMeta.size() ; i++) {
	    	   lTmp = lListFieldMeta.get(i).getFormattedValue(lBean, false);
	    	   if(lTmp==null){
	    		   if(DataType.STRING == lListFieldMeta.get(i).getDataType() || 
	    				   DataType.DATE == lListFieldMeta.get(i).getDataType() ||
	    				   DataType.DATETIME == lListFieldMeta.get(i).getDataType() ||
	    				   DataType.TIME == lListFieldMeta.get(i).getDataType() ||
	    				   DataType.OBJECT == lListFieldMeta.get(i).getDataType()){
	    			   lTmp = "";
	    		   }else if(DataType.INTEGER == lListFieldMeta.get(i).getDataType()){
	    			   lTmp = 0;
	    		   }else if(DataType.DECIMAL == lListFieldMeta.get(i).getDataType()){
	    			   lTmp = BigDecimal.ZERO;
	    		   }

	    	   }
	    	   lBuilder.append(lTmp);
	    	   if (i<lListFieldMeta.size()-1) {
	    		   lBuilder.append(PIPE);
	    	   }
	       }
       }
	   lBuilder.append(NEWLINE);
       return lBuilder.toString();
   }
   
   public String getCsv(List<String> pFields, List<Map<String,Object>> pList) throws Exception {
	   final String PIPE = "|";
	   final String NEWLINE = "\r\n";
	   StringBuilder lBuilder = new StringBuilder();
       for (int i=0; i<pFields.size() ; i++) {
    	   lBuilder.append(pFields.get(i));
    	   if (i<pFields.size()-1) {
    		   lBuilder.append(PIPE);
    	   }
       }
       Object lTmp = null;
       for (Map<String,Object> lMap : pList) {
    	   lBuilder.append(NEWLINE);
	       for (int i=0; i<pFields.size() ; i++) {
	    	   lTmp = lMap.get(pFields.get(i));
	    	   if(lTmp==null){
	    		   lTmp = "";
	    	   }
	    	   lBuilder.append(lTmp);
	    	   if (i<pFields.size()-1) {
	    		   lBuilder.append(PIPE);
	    	   }
	       }
       }
	   lBuilder.append(NEWLINE);
       return lBuilder.toString();
   }
   
   private List<CompanyDetailBean> getEntityList(Connection pConnection, String pEntityPan, String pEntityGstn) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT CDID, CDCODE, CDPAN FROM COMPANYDETAILS ");
		lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS ON ( CDID = CLCDID AND CLRECORDVERSION > 0) ");
		lSql.append(" WHERE CDRecordVersion > 0 ");
		lSql.append(" AND ( CLRECORDVERSION IS NULL OR CLRECORDVERSION > 0 ) ");
		if(!StringUtils.isBlank(pEntityPan)){
			lSql.append(" AND CDPAN =  ").append(DBHelper.getInstance().formatString(pEntityPan));
		}
		if(!StringUtils.isBlank(pEntityGstn)){
			lSql.append(" AND CLGSTN = ").append(DBHelper.getInstance().formatString(pEntityGstn));
		}
		lSql.append(" GROUP BY CDID, CDCODE, CDPAN ");
		if(!StringUtils.isBlank(pEntityGstn)){
			lSql.append(" , CLGSTN ");
		}
		return companyDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
   }
   public String getEntityCode(Connection pConnection,String pEntityPan, String pEntityGstn ) throws Exception {
	   String lEntityCode = null;
	   List<CompanyDetailBean> lCompanyDetailBeanList = getEntityList(pConnection, pEntityPan, pEntityGstn);
	   if(Objects.isNull(lCompanyDetailBeanList) || lCompanyDetailBeanList.size() > 1){
		   return null;
	   }
	   for(CompanyDetailBean lCompanyDetailBean : lCompanyDetailBeanList){
		   lEntityCode = lCompanyDetailBean.getCode();
	   }
	   return lEntityCode;
   }
   
   public String getEntityCode(Connection pConnection,String pEntityPan, String pEntityGstn,AppEntityBean.EntityType pEntityType) throws Exception {
	   String lEntityCode = null;
	   DBHelper lDbHelper = DBHelper.getInstance();
	   StringBuilder lSql = new StringBuilder();
	   lSql.append(" SELECT COMPANYDETAILS.* FROM COMPANYDETAILS,COMPANYLOCATIONS ");
	   lSql.append(" WHERE CDID=CLCDID ");
	   lSql.append(" AND CDPAN = ").append(lDbHelper.formatString(pEntityPan));
	   lSql.append(" AND CLGSTN = ").append(lDbHelper.formatString(pEntityGstn));
	   if (EntityType.Purchaser.equals(pEntityType)) {
		   lSql.append(" AND CDPURCHASERFLAG = ").append(lDbHelper.formatString(CommonAppConstants.Yes.Yes.getCode()));
	   }else if (EntityType.Supplier.equals(pEntityType)) {
		   lSql.append(" AND CDSUPPLIERFLAG = ").append(lDbHelper.formatString(CommonAppConstants.Yes.Yes.getCode()));
	   }else if (EntityType.Financier.equals(pEntityType)) {
		   lSql.append(" AND CDFINANCIERFLAG = ").append(lDbHelper.formatString(CommonAppConstants.Yes.Yes.getCode()));
	   }
	   List<CompanyDetailBean> lCompanyDetailBeanList = companyDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
	   if(Objects.isNull(lCompanyDetailBeanList) || lCompanyDetailBeanList.size() == 0){
		   return null;
	   }
	   String lPrevEntityCode = null;
	   for(CompanyDetailBean lCompanyDetailBean : lCompanyDetailBeanList){
		   lEntityCode = lCompanyDetailBean.getCode();
		   if(StringUtils.isNotEmpty(lPrevEntityCode)){
			   if(!lEntityCode.equals(lPrevEntityCode)) {
				   //skip if multiple found
				   lEntityCode = null;
				   break;
			   }
		   }
		   lPrevEntityCode = lEntityCode;
	   }
	   return lEntityCode;
   }
   
   public boolean isCashInvoie(AppUserBean pAppUserBean){
	   String lEntityCode = RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_CASH_INVOICE);
	   if(pAppUserBean.getDomain().equals(lEntityCode)){
		   return true;
	   }
	   return false;
   }
	public List<InstrumentBean> getClubbedBeans(Connection pConnection, Long pInstrumentId) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM Instruments ");
		lSql.append(" WHERE INRECORDVERSION > 0 ");
		lSql.append(" AND INGROUPINID = ").append(pInstrumentId);
		lSql.append(" ORDER BY INPURCHASER,INSUPPLIER,INGOODSACCEPTDATE ");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lList;
	}
	public boolean supportsInstrumentKeys(String pEntityCode){
    	AppEntityBean lAEBean=null;
		try {
			lAEBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
	    	if(lAEBean!=null){
	    		AppEntityPreferenceBean lAEPrefBean = lAEBean.getPreferences();
	    		return lAEPrefBean!=null&& Yes.Yes.equals(lAEPrefBean.getIck());
	    	}
		} catch (MemoryDBException e) {
			logger.info("Error in supportsInstrumentKeys : "+ e.getMessage());
		}
		return false;
	}
	
	public boolean locationSupportsInstrumentKeys(Connection pConnection,String pEntityCode,Long pCdid,String pGstn){
		try {
			MemberLocationForInstKeysBean lBean = new MemberLocationForInstKeysBean();
			lBean.setCode(pEntityCode);
			if (pCdid != null) {
				lBean.setClId(pCdid);
			}else {
				lBean.setGstn(pGstn);
			}
			lBean = memberLocationForInstKeysDAO.findBean(pConnection, lBean);
			if  (lBean==null) {
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			logger.info("Error in locationSupportsInstrumentKeys : "+ e.getMessage());
		}
		return false;
	}

	public List<String> getEntityGstnList(Connection pConnection,String pEntityCode) throws Exception {
		AppEntityBean lAppEntityBean = getAppEntityBean(pEntityCode);
		CompanyLocationBean lClFilterBean = new CompanyLocationBean();
		lClFilterBean.setCdId(lAppEntityBean.getCdId());
		List<CompanyLocationBean> lCompanyLocationList = companyLocationDAO.findList(pConnection, lClFilterBean, Arrays.asList(new String[] {"id","cdid","gstn"}));
		return lCompanyLocationList.stream().map(pKeyBean -> pKeyBean.getGstn()).collect(Collectors.toList());
	}
    public Response sendFileContents(String lFileName, byte[] lData) throws Exception {
         StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData);
               output.flush();
            }
        };
        String lContentType = Files.probeContentType(Paths.get(lFileName));
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+lFileName).build();
        
    }
    
    public void createAndReadFiles(AdapterSFTPConfigBean configBean) throws Exception {
		createBehlFiles(configBean);
		readBhelFile(configBean);
    }
    
    private void createBehlFiles(AdapterSFTPConfigBean pSftpConfigBean) throws Exception {
    	try(Connection lConnection = DBHelper.getInstance().getConnection()){
			StringBuilder lSql = new StringBuilder();
			lSql.append(" Select * from BHELPEMINSTRUMENT WHERE BPISENTTIME IS NULL ");
			lSql.append(" AND BPIENTITYCODE = ").append(DBHelper.getInstance().formatString(pSftpConfigBean.getEntityCode()));
			List<BHELPEMInstrumentBean> lListToSend = bhelPemInstrumentDAO.findListFromSql(lConnection, lSql.toString(), -1);
			List<Long> lInIds = new ArrayList<Long>();
			List<Long> lL1Obids = new ArrayList<Long>();
			List<Long> lL1InterestObids = new ArrayList<Long>();
			List<Long> lL2Obids = new ArrayList<Long>();
			List<Long> lL2FutureObids = new ArrayList<Long>();
			HashMap<BHELPEMInstrumentBean.Type, List<BHELPEMInstrumentBean>> lMap = new HashMap<>();
			lMap.put(BHELPEMInstrumentBean.Type.Instrument, new ArrayList<>());
			lMap.put(BHELPEMInstrumentBean.Type.Leg1, new ArrayList<>());
			lMap.put(BHELPEMInstrumentBean.Type.Leg1Interest, new ArrayList<>());
			lMap.put(BHELPEMInstrumentBean.Type.Leg2, new ArrayList<>());
			lMap.put(BHELPEMInstrumentBean.Type.Leg2Future, new ArrayList<>());
			String lDBColumnNames =  obligationDAO.getDBColumnNameCsv(null,Arrays.asList("OBRECORDVERSION"));
			String lDBColumnNames1 =  obligationSplitsDAO.getDBColumnNameCsv(null,Arrays.asList(""));
			List<BHELPEMInstrumentBean> lTmpList = null;
			if (lListToSend!=null && !lListToSend.isEmpty()) {
				for (BHELPEMInstrumentBean lBean : lListToSend) {
					if (BHELPEMInstrumentBean.Type.Instrument.equals(lBean.getType()) ) {
						lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Instrument);
						lTmpList.add(lBean);
					}else if (BHELPEMInstrumentBean.Type.Leg1.equals(lBean.getType()) ) {
						lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg1);
						lTmpList.add(lBean);
					}else if (BHELPEMInstrumentBean.Type.Leg1Interest.equals(lBean.getType()) ) {
						lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg1Interest);
						lTmpList.add(lBean);
					}else if (BHELPEMInstrumentBean.Type.Leg2.equals(lBean.getType()) ) {
						lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg2);
						lTmpList.add(lBean);
					}else if (BHELPEMInstrumentBean.Type.Leg2Future.equals(lBean.getType()) ) {
						lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg2Future);
						lTmpList.add(lBean);
					}
					if (BHELPEMInstrumentBean.Type.Instrument.equals(lBean.getType()) 
							&& !lInIds.contains(lBean.getInId())) {
						lInIds.add(lBean.getInId());
					}else if (BHELPEMInstrumentBean.Type.Leg1.equals(lBean.getType()) 
							&& !lL1Obids.contains(lBean.getInId())) {
						lL1Obids.add(lBean.getInId());
					}else if (BHELPEMInstrumentBean.Type.Leg1Interest.equals(lBean.getType()) 
							&& !lL1InterestObids.contains(lBean.getInId())) {
						lL1InterestObids.add(lBean.getInId());
					}else if (BHELPEMInstrumentBean.Type.Leg2.equals(lBean.getType()) 
							&& !lL2Obids.contains(lBean.getInId())) {
						lL2Obids.add(lBean.getInId());
					}else if (BHELPEMInstrumentBean.Type.Leg2Future.equals(lBean.getType()) 
							&& !lL2FutureObids.contains(lBean.getInId())) {
						lL2FutureObids.add(lBean.getInId());
					}
				}
			}
			AppUserBean lAppUserBean = new AppUserBean();
			lAppUserBean.setId(new Long(0));
			StringBuilder lSqlForOblig = new StringBuilder();
			lSqlForOblig.append("Select ");
			lSqlForOblig.append(lDBColumnNames).append(",").append(lDBColumnNames1) .append(", INID \"OBRECORDVERSION\" ");
			lSqlForOblig.append(" , instruments.*, factoringunits.* ");
			lSqlForOblig.append(" from OBLIGATIONS,Obligationsplits,instruments,factoringunits WHERE OBRECORDVERSION>0  and OBSRECORDVERSION>0 and OBSOBID=OBID and OBFUID=INFUID and OBFUID=FUID ");
			if (lInIds!=null && !lInIds.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append("Select * from INSTRUMENTS WHERE INRECORDVERSION>0 ");
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lInIds);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("INID", lListStr)).append("  ");
				List<InstrumentBean> lInstrumentsList = instrumentDAO.findListFromSql(lConnection, lSql.toString(), -1);
				ArrayList<Object> lList = new ArrayList<Object>();
				lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Instrument);
				if (lTmpList!=null && !lTmpList.isEmpty()) {
					for (InstrumentBean lInstBean : lInstrumentsList ) {
						if (lInstBean.getTdsAmount()==null) lInstBean.setTdsAmount(BigDecimal.ZERO);
						if (lInstBean.getAdjAmount()==null) lInstBean.setAdjAmount(BigDecimal.ZERO);
						if (lInstBean.getCreditPeriod()==null) lInstBean.setCreditPeriod(new Long(0));
						if (lInstBean.getExtendedCreditPeriod()==null) lInstBean.setExtendedCreditPeriod(new Long(0));
						lList.add(lInstBean);
					}

					for (BHELPEMInstrumentBean lBean : lTmpList) {
						lBean.setSentTime(new Timestamp(System.currentTimeMillis()));
						bhelPemInstrumentDAO.update(lConnection, lBean);
					}
				}
			   if (lTmpList!=null && !lTmpList.isEmpty()) {
				   	File lFile = new File(pSftpConfigBean.getSrcPath()+getUniqueFileName(lConnection,"INVOICE_",TredsHelper.getInstance().getBusinessDate()));
					String lData = TredsHelper.getInstance().getCsv(lConnection, instrumentDAO.getBeanMeta(), InstrumentBean.FIELDGROUP_BHELPEMINST, null, lList, lAppUserBean);
					FileUtils.writeStringToFile(lFile, lData);
			   }
			   
			}
			if (lL1Obids!=null && !lL1Obids.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append(lSqlForOblig);
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL1Obids);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBID", lListStr)).append("  ");
				List<ObliFUInstDetailSplitsBean> lObligDetailList = obliFUInstDetailSplitsDAO.findListFromSql(lConnection, lSql.toString(), -1);
				ArrayList<Object[]> lList = new ArrayList<Object[]>();
				lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg1);
				if (lTmpList!=null && !lTmpList.isEmpty()) {
					createAndProcessFile(lTmpList,lObligDetailList,lList,pSftpConfigBean,lConnection,"OBLIG_L1_");
				}
			   
			}
			if (lL1InterestObids!=null && !lL1InterestObids.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append(lSqlForOblig);
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL1InterestObids);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBID", lListStr)).append("  ");
				List<ObliFUInstDetailSplitsBean> lObligDetailList = obliFUInstDetailSplitsDAO.findListFromSql(lConnection, lSql.toString(), -1);
				ArrayList<Object[]> lList = new ArrayList<Object[]>();
				lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg1Interest);
				if (lTmpList!=null && !lTmpList.isEmpty()) {
					createAndProcessFile(lTmpList,lObligDetailList,lList,pSftpConfigBean,lConnection,"INTEREST_L1_");	
				}
			   
			}
			
			if (lL2Obids!=null && !lL2Obids.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append(lSqlForOblig);
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL2Obids);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBID", lListStr)).append("  ");
				List<ObliFUInstDetailSplitsBean> lObligDetailList = obliFUInstDetailSplitsDAO.findListFromSql(lConnection, lSql.toString(), -1);
				ArrayList<Object[]> lList = new ArrayList<Object[]>();
				lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg2);
				if (lTmpList!=null && !lTmpList.isEmpty()) {
					createAndProcessFile(lTmpList,lObligDetailList,lList,pSftpConfigBean,lConnection,"OBLIG_L2_");	
				}
			   
			}
			if (lL2FutureObids!=null && !lL2FutureObids.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append(lSqlForOblig);
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL2FutureObids);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBID", lListStr)).append("  ");
				List<ObliFUInstDetailSplitsBean> lObligDetailList = obliFUInstDetailSplitsDAO.findListFromSql(lConnection, lSql.toString(), -1);
				ArrayList<Object[]> lList = new ArrayList<Object[]>();
				lTmpList = lMap.get(BHELPEMInstrumentBean.Type.Leg2Future);
				if (lTmpList!=null && !lTmpList.isEmpty()) {
					createAndProcessFile(lTmpList,lObligDetailList,lList,pSftpConfigBean,lConnection,"OBLIG_L2_FUTURE_");
				}
			}
		}catch (Exception e) {
			
		}
    }
    
    private void readBhelFile(AdapterSFTPConfigBean lSftpConfigBean) throws Exception {
    	InstrumentBean lInstrumentBean = null;
    	Connection lConnection = DBHelper.getInstance().getConnection();
    	try{
	    	File lDir = new File(lSftpConfigBean.getInDestPath());
			String[] lFiles = lDir.list();
			Map<String, String> lFormParam = null;
			for (int lFilePtr=0 ; lFilePtr<lFiles.length; lFilePtr++) {
				lFormParam = new HashMap<String, String>();
				byte[] lContent = FileUtils.readFileToByteArray(new File(lSftpConfigBean.getInDestPath()+lFiles[lFilePtr]));
				AppUserBean lUserBean  = TredsHelper.getInstance().getAppUserBean(lSftpConfigBean.getEntityCode(), "ADMIN");
				String lFileType = null;
				if (lFiles[lFilePtr].contains("CAR_")) {
					lFileType = InstrumentCounterUploader.FILE_TYPE.toLowerCase();
					lFormParam.put("sftp", CommonAppConstants.Yes.Yes.getCode());
				}else if ( lFiles[lFilePtr].contains("INST_")) {
					lFileType = InstrumentUploader.FILE_TYPE.toLowerCase();
				}
				if (lFileType!=null) {
					FileUploadBean lFileUploadBean = FileUploaderFactory.getInstance().upload(lFiles[lFilePtr], lContent, lFormParam , new HashMap<String, List<String>>(), lFileType.toLowerCase() , false, lUserBean, null);
					FileUtils.writeStringToFile(new File(lSftpConfigBean.getSrcPath()+lFileUploadBean.getReturnFileName()), lFileUploadBean.getReturnFileContents());
					Path fileToMovePath = Paths.get(lSftpConfigBean.getInDestPath()+lFiles[lFilePtr]);
			        Path destinationDir = Paths.get(lSftpConfigBean.getInSuccessPath()+lFiles[lFilePtr]);
			        Path path = Files.move(fileToMovePath, destinationDir, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			logger.error("error",e);
		}finally {
			lConnection.close();
		}
    }
    
    private void createAndProcessFile(List<BHELPEMInstrumentBean> pDataList, List<ObliFUInstDetailSplitsBean> pObligDetailList, ArrayList<Object[]> pList, AdapterSFTPConfigBean pSftpConfigBean, Connection pConnection, String pFilePrefix) throws Exception {
    	HashMap<Long,Object[]> lObMap = new HashMap<>();
		ObligationBean lObligationBean = null;
		ObligationSplitsBean lObligationSplitsBean = null;
		InstrumentBean lInstrumentBean = null;
		FactoringUnitBean lFactUntBean = null;
		Object[] lObj = null;//0=ObligationBean, 1=InstrumentBean , 2=FACTUNT
		for (ObliFUInstDetailSplitsBean lObligDetBean : pObligDetailList ) {
			lObligationBean = lObligDetBean.getObligationBean();
			lObligationSplitsBean = lObligDetBean.getObligationSplitBean();
			lInstrumentBean = lObligDetBean.getInstrumentBean();
			lInstrumentBean.populateDatabaseFields();
			lInstrumentBean.populateNonDatabaseFields();
			CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
			lCompanyLocationBean.setId(lInstrumentBean.getPurClId());
			lCompanyLocationBean = companyLocationDAO.findBean(pConnection, lCompanyLocationBean);
			lInstrumentBean.setPurLocation(lCompanyLocationBean.getName());
			lFactUntBean = lObligDetBean.getFactoringUnitBean();
			if(!lObMap.containsKey(lObligationBean.getId())) {
				lObj = new Object[3];
				lObj[0] = lObligationBean;
				lObj[1] = lInstrumentBean;
				lObj[2] = lFactUntBean;
				lObMap.put(lObligationBean.getId(),lObj );
				lObligationBean.setInId(lObligationBean.getRecordVersion());
			}
			Object[] lTmpObj = lObMap.get(lObligationSplitsBean.getId());
			lObligationBean = (ObligationBean) lTmpObj[0];
			lInstrumentBean = (InstrumentBean) lTmpObj[1];
			if(lObligationBean.getPaymentRefNo()==null) {
				lObligationBean.setPaymentRefNo(lObligationSplitsBean.getPaymentRefNo());
			}else {
				lObligationBean.setPaymentRefNo(lObligationBean.getPaymentRefNo()+"^"+lObligationSplitsBean.getPaymentRefNo()!=null?lObligationSplitsBean.getPaymentRefNo():"");
			}
			if(lObligationBean.getRespErrorCode()==null) {
				lObligationBean.setRespErrorCode(lObligationSplitsBean.getRespErrorCode());
			}else {
				lObligationBean.setRespErrorCode(lObligationBean.getRespErrorCode()+"^"+lObligationSplitsBean.getRespErrorCode()!=null?lObligationSplitsBean.getRespErrorCode():"");
			}
			if(lObligationBean.getRespRemarks()==null) {
				lObligationBean.setRespRemarks(lObligationSplitsBean.getRespRemarks());
			}else {
				lObligationBean.setRespRemarks(lObligationBean.getRespRemarks()+"^"+lObligationSplitsBean.getRespRemarks()!=null?lObligationSplitsBean.getRespRemarks():"");
			}
			pList.add(lTmpObj);
		}

		for (BHELPEMInstrumentBean lBean : pDataList) {
			lBean.setSentTime(new Timestamp(System.currentTimeMillis()));
			bhelPemInstrumentDAO.update(pConnection, lBean);
		}
    	//
    	boolean lAddFileds = true;
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	List<String> lRtnFields = new ArrayList<>();
		InstrumentBean lInstBean = null;
	   	List<Map<String,Object>> lRtnList = new ArrayList<Map<String,Object>>();
	   	Map<String,Object> lObligMap = null;
	   	Map<String,Object> lInstMap = null;
	   	Map<String,Object> lInstFinalMap = null;
	   	File lFile = new File(pSftpConfigBean.getSrcPath()+getUniqueFileName(pConnection,pFilePrefix,TredsHelper.getInstance().getBusinessDate()));
	   	for (Object[] lObjArr : pList ) {
	   		lInstBean = (InstrumentBean)lObjArr[1];
	   		if (lAddFileds) {
	   			lRtnFields.addAll(pSftpConfigBean.getObligList());
	   		}
	   		lObligMap = obligationDAO.getBeanMeta().formatAsMap((ObligationBean)lObjArr[0], null, pSftpConfigBean.getObligList(), false);
	   		if (pSftpConfigBean.getInstDetInOblig()!=null && CommonAppConstants.YesNo.Yes.getCode().equals(pSftpConfigBean.getInstDetInOblig())) {
	   			lInstMap = instrumentDAO.getBeanMeta().formatAsMap(lInstBean, null, pSftpConfigBean.getInstList(), false);
	   			if (lAddFileds) {
	   				lRtnFields.addAll(pSftpConfigBean.getInstList());
	   			}
	   			lInstFinalMap = new HashMap<String, Object>();
	   			lInstFinalMap.putAll(lInstMap);
		   		if (lInstBean.getCfData()!=null && !lInstBean.getCfData().isEmpty()) {
		   			Map<String,Object> lCustFieldMap = (Map<String, Object>) lJsonSlurper.parseText(lInstBean.getCfData());
		   			if (lAddFileds) {
			   			CustomFieldBean lCustomFieldBean = new CustomFieldBean();
			   			lCustomFieldBean.setId(lInstBean.getCfId());
			   			lCustomFieldBean.setCode(lInstBean.getPurchaser());
			   			CustomFieldBO lCFBo = new CustomFieldBO();
			   			lCustomFieldBean = lCFBo.findBean(pConnection, lCustomFieldBean);
			   			if (lCustomFieldBean!=null) {
			   				if (StringUtils.isNotEmpty(lCustomFieldBean.getField1Name()) ) {
			   					lRtnFields.add(lCustomFieldBean.getField1Name());
			   				}
							if (StringUtils.isNotEmpty(lCustomFieldBean.getField2Name()) ) {
								lRtnFields.add(lCustomFieldBean.getField2Name());   					
							}
							if (StringUtils.isNotEmpty(lCustomFieldBean.getField3Name()) ) {
								lRtnFields.add(lCustomFieldBean.getField3Name());
							}
							if (StringUtils.isNotEmpty(lCustomFieldBean.getField4Name()) ) {
								lRtnFields.add(lCustomFieldBean.getField4Name());	
							}
			   			}
		   			}
		   			lInstFinalMap.putAll(lCustFieldMap);
		   		}
		   		lInstFinalMap.put("financier", ((FactoringUnitBean)lObjArr[2]).getFinancier());
		   		lObligMap.putAll(lInstFinalMap);
		   		if (lAddFileds) {
		   			lRtnFields.addAll(pSftpConfigBean.getFactUnitList());
		   		}
	   		}
	   		lRtnList.add(lObligMap);
	   		lAddFileds = false;
	   	}
	   	String lData = TredsHelper.getInstance().getCsv(lRtnFields,lRtnList);
//		String lData = TredsHelper.getInstance().getCsv(lConnection, obligationDAO.getBeanMeta(), null, lFields, lList, lAppUserBean);
		FileUtils.writeStringToFile(lFile, lData);
    }
    
    private String getUniqueFileName(Connection pConnection, String pFileKey, Date pDate ) throws Exception{
		Long lNumber = DBHelper.getInstance().getUniqueNumber(pConnection, pFileKey);
		// CAR_<YYYYMMDD>_01.CSV
		String lFileName="";
		lFileName+=pFileKey;
		lFileName+="_";
		lFileName += FormatHelper.getDisplay("yyyyMMdd", pDate);
		lFileName+="_";
		lFileName+=StringUtils.leftPad(lNumber.toString(), 3,"0");
		lFileName+=".CSV";
		return lFileName;
	}
	public Date convertDate(java.util.Date pDate) {
    	Date lRetDate = null;
    	lRetDate = new Date(pDate.getTime());
    	return lRetDate;
    }
	
	public List<Long> getFactoringUnits(Map<Long, Object[]> pSupplierObligations){
		List<Long> lFuIds = new ArrayList<Long>();
        ObligationBean lOBBean = null;
		for(Object[] lTmpObjs : pSupplierObligations.values()){
			//0=ObligationBean, 1=List<ObligtionSplit>
			lOBBean = (ObligationBean)lTmpObjs[0];
			lFuIds.add(lOBBean.getFuId());
		}
		return lFuIds;
	}
	
	public Map<Long, Object[]> getFactoredBeans(Connection pConnection,Map<Long, Object[]> pSupplierObligations) throws Exception{
		StringBuilder lSql = new StringBuilder(); 
		List<Long> lIds = getFactoringUnits(pSupplierObligations);
		Map<Long, Object[]> lFactoredMap = new HashMap<Long, Object[]>();
		InstrumentBean lInstrumentBean = null;
		FactoringUnitBean lFactoringUnitBean = null;
		Object[] lTmpObj = null;
		lSql.append(" SELECT *  FROM FactoringUnits, Instruments ");
		lSql.append(" WHERE FUID = INFUID AND INRECORDVERSION>0 AND FURECORDVERSION>0 ");
		lSql.append(" AND FUID IN ( ").append(TredsHelper.getInstance().getCSVIdsForInQuery(getFactoringUnits(pSupplierObligations))).append(" ) ");
		List<FactoredBean> lFactoredList = factoredDAO.findListFromSql(pConnection, lSql.toString(), -1);
		for(FactoredBean lBean : lFactoredList){
			lTmpObj = new Object[2];
			lInstrumentBean = lBean.getInstrumentBean();
			lInstrumentBean.populateNonDatabaseFields();
			lFactoringUnitBean = lBean.getFactoringUnitBean();
			lTmpObj[0] = lFactoringUnitBean;
			lTmpObj[1] = lInstrumentBean;
			lFactoredMap.put(lFactoringUnitBean.getId(), lTmpObj);
		}
		return lFactoredMap;
	}
	
	public Map<String,Object> getFieldListDiff(GenericDAO pGenericDAO, Object pFinalBean, Object pProvisionalBean) {
		List<BeanFieldMeta> lFieldsMeta = pGenericDAO.getBeanMeta().getFieldMetaList(null, null);
		List<String> lDiffFields = new ArrayList<String>();
		BeanFieldMeta lFieldMeta = null;
		Object lOldVal = null, lNewVal;
		Map<String,Object> lDiffHash = new HashMap<String,Object>();
		//
		for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
			lFieldMeta = lFieldsMeta.get(lPtr);
			lOldVal = lFieldMeta.getProperty(pFinalBean);
			lNewVal = lFieldMeta.getProperty(pProvisionalBean);
			if (lOldVal == null && lNewVal == null) {
				// both are same - null
			} else if (lOldVal != null && lNewVal != null) {
				if (lOldVal instanceof BigDecimal) {
					if ((((BigDecimal) lOldVal).compareTo((BigDecimal) lNewVal)) != 0) {
						lDiffFields.add(lFieldMeta.getName());
						lDiffHash.put(lFieldMeta.getName(), lOldVal);						
					}
				} else {
					if (!lOldVal.equals(lNewVal)) {
						lDiffFields.add(lFieldMeta.getName());
						lDiffHash.put(lFieldMeta.getName(), lOldVal);						
					}
				}
			} else if (lOldVal == null && (lNewVal instanceof BigDecimal)	&& (BigDecimal.ZERO.compareTo((BigDecimal) lNewVal) == 0)) {
				// skip
			} else if (lNewVal == null && (lOldVal instanceof BigDecimal) && (BigDecimal.ZERO.compareTo((BigDecimal) lOldVal) == 0)) {
				// skip
			} else {
				lDiffFields.add(lFieldMeta.getName());
				lDiffHash.put(lFieldMeta.getName(), lOldVal);						
			}
		}
		if (lDiffFields.size() > 0) {
			//String lModifedJson = instrumentDAO.getBeanMeta().formatAsJson(pFinalBean, null, lDiffFields,false);
			//Map<String,Object> lModifedMap = instrumentDAO.getBeanMeta().formatAsMap(pFinalBean, null, lDiffFields,false);
			//return instrumentDAO.getBeanMeta().formatAsMap(pProvisionalBean, null, lDiffFields,false);
			return lDiffHash;
		}
		return null;
	}
	
	 public BigDecimal getNormalPlanCharge(Connection pConnection, FactoringUnitBean pFactoringUnitBean,String pEntityPlan, String pChargeBearingEntity, BigDecimal pFactoredAmount, Long pSettlementLocation,Long pExtensionDays,Long pTenor) throws Exception{
	        BigDecimal lChargeValue = new BigDecimal(0);
	        Long lPlanId =TredsHelper.getInstance().getCurrentPlan(pConnection, pEntityPlan);
	        if(lPlanId == null){
	        	String lName="";
	        	if(pFactoringUnitBean.getPurchaser().equals(pEntityPlan)){
	        		lName = "buyer";
	        	}else {
	        		lName = "financier";
	        	}
	        	throw new CommonBusinessException("No active plan found for the "+ lName + " " + pChargeBearingEntity + ".");
	        }
	        AuctionChargePlanBean lAuctionChargePlanBean = getPlanDetails(pConnection, lPlanId);
	        BigDecimal lTurnover = getTurnoverAmount(pConnection, pChargeBearingEntity);
	        logger.info("lTurnover  : "+ (lTurnover!=null?lTurnover:""));
	        lChargeValue = getChargableAmount(lAuctionChargePlanBean, pFactoringUnitBean, pFactoredAmount, lTurnover,pExtensionDays,pTenor);
	        logger.info("lChargeValue  : "+ (lChargeValue!=null?lChargeValue:""));
	        return lChargeValue;
	  }
	 
	 public void removeUnwantedValidation(List<ValidationFailBean> pValidationFailBeans, String pKey) {
        if (pValidationFailBeans != null && pValidationFailBeans.size() > 0) {
    		for(ValidationFailBean lBean : pValidationFailBeans) {
    			if(lBean.getName().equalsIgnoreCase(pKey)) {
    				pValidationFailBeans.remove(lBean);
    				break;
    			}
    		}
        }
	 }
	 
	 public Map<String,Object> getExtraPayloadRecd(GenericDAO pGenericDAO,  Map<String,Object> pPayLoad) {
	        if(pPayLoad!=null&&pPayLoad.size() > 0) {
	            List<BeanFieldMeta> lFieldsMeta = pGenericDAO.getBeanMeta().getFieldMetaList(null, null);
	            Map<String,Object> lDiffHash = new HashMap<String,Object>();
	            Set<String> lBeanFields = new HashSet<String>();
	            //
	            for(BeanFieldMeta lFieldMeta : lFieldsMeta) {
	                lBeanFields.add(lFieldMeta.getName());
	            }
	            for(String lKey: pPayLoad.keySet()) {
	                if(!lBeanFields.contains(lKey)) {
	                    lDiffHash.put(lKey, pPayLoad.get(lKey));
	                }
	            }
	            if (lDiffHash.size() > 0) {
	                return lDiffHash;
	            }
	        }
	        return null;
	 } 
}