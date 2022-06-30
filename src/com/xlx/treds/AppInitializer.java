package com.xlx.treds;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.IMemoryDBLoader;
import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.BulkMailSenderFactory;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RegistryEntryBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.DefaultBinder;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IUserDataSource;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.LoginBean;
import com.xlx.commonn.bean.LoginSessionBean;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.IAccessRuleDefiner;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.user.bean.RoleMasterBean;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bo.BSLinkCounterPendingApproval;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.HolidayMasterBean;
import com.xlx.treds.master.bean.SystemParameterBean;
import com.xlx.treds.sftp.SFTPClientHolder;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.AppUserBean.Type;

@ApplicationPath("rest")
public class AppInitializer extends ResourceConfig implements IMemoryDBLoader, IUserDataSource, IAccessRuleDefiner {
    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    private GenericDAO<AppUserBean> appUserDAO;
    //purchaser
    public static final String REGISTRY_CLICKWRAPLATESTVERSION_PURCHASER = "server.settings.latestrevisionversionkeypurchaser"; //CHANGE
    public static final String REGISTRY_VALIDCLICKWRAPAGREEMENTS_PURCHASER = "server.settings.validclickwrapagreementspurchaser"; //CHANGE
    //supplier
    public static final String REGISTRY_CLICKWRAPLATESTVERSION_SUPPLIER = "server.settings.latestrevisionversionkeysupplier"; //CHANGE
    public static final String REGISTRY_VALIDCLICKWRAPAGREEMENTS_SUPPLIER = "server.settings.validclickwrapagreementssupplier"; //CHANGE
    
    //THE FOLLOWING WILL BE SAME FOR BOTH PURCHASER AND SUPPLIER
    public static final String PARAM_CLICKWRAP_REVISIONDATE ="revisiondate";
    public static final String PARAM_CLICKWRAP_AGREEMENTFILE ="agreementfile";
    public static final String PARAM_CLICKWRAP_AMENDMENTFILE ="amendmentfile";
    public static final String PARAM_CLICKWRAP_GRACEPERIOD ="graceperiod";
    
    //
    public static final String REGISTRY_CASH_INVOICE = "server.settings.cashinvoiceentity"; //CHANGE
    public static final String REGISTRY_GEM_ARTERIA = "server.settings.gemarteriaentity"; 
    
    public AppInitializer(@Context ServletContext pServletContext) {
        super();
        int lCounter=1;
        final ResourceConfig lResourceConfig = packages(
        		"com.xlx.commonn.rest"
        		,"com.xlx.commonn.user.rest"
        		,"com.xlx.commonn.report.rest"
        		,"com.xlx.treds.user.rest"
        		,"com.xlx.treds.entity.rest"
        		,"com.xlx.treds.instrument.rest"
        		,"com.xlx.treds.master.rest"
        		,"com.xlx.treds.auction.rest"
        		,"com.xlx.treds.test.rest"
        		,"com.xlx.treds.bill.rest"
        		,"com.xlx.treds.other.rest"
        		,"com.xlx.treds.monitor.rest"
        		,"com.xlx.treds.monetago.rest"
        		,"com.xlx.treds.notialrt.rest"
        		,"com.xlx.treds.sftp.rest"
        		,"com.xlx.treds.adapter.rest"
        		,"com.xlx.treds.hostapi.rest");
        register(new DefaultBinder(lResourceConfig));
        property(ServerProperties.MONITORING_STATISTICS_MBEANS_ENABLED, true);
        Map<String, Object> lConfig = new HashMap<String, Object>();
        lConfig.put(BeanMetaFactory.KEY_DATE_FORMAT, "dd-MMM-yyyy");
        lConfig.put(BeanMetaFactory.KEY_DECIMAL_FORMAT, "##,##,##,##,##,##,##,##0.00##");
        BeanMetaFactory.createInstance(lConfig);
        //
	    BeanFieldMeta.PATTERNS.put("PATTERN_TAN",new Object[]{Pattern.compile("^[A-Z]{4}\\d{5}[A-Z]{1}$"), "First 4 characters should be uppercase alphabets, followed by 5 numeric digits and then an uppercase alphabet. E.g. MKPM32898L"});
	    BeanFieldMeta.PATTERNS.put("PATTERN_ST",new Object[]{Pattern.compile("^[A-Z]{5}\\d{4}[A-Z]{1}[S][T|D]\\d{3}$"), "First 5 characters should be uppercase alphabets, followed by 4 numeric digits, then an uppercase alphabet, ST or SD and atlast 3 numeric digits. E.g. AMKPM3289LSD890"});
	    BeanFieldMeta.PATTERNS.put("PATTERN_MOBILE",new Object[]{Pattern.compile("^(\\+?\\d{1,4}[\\s-])?(?!0+\\s+,?$)[1-9][0-9]{9}\\s*,?$"), "10 digit mobile number. Eg. 9878787654. Optionally ISD code can be included. Eg. 91 9878787654, +91 9878787654"});
	    BeanFieldMeta.PATTERNS.put("PATTERN_PHONE",new Object[]{Pattern.compile("^(\\+?\\d{1,5}[\\s-])?(?!0+\\s+,?$)\\d{1,8}\\s*,?$"), "13 digit telephone number along with STD Code(5 digit) seperated by space or hyphen. 23451212, 22 23451212, 02345 22345121, 22-23451212"});
	    BeanFieldMeta.PATTERNS.put("PATTERN_IP",new Object[]{Pattern.compile("(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"),"Valid IP of the form nnn.nnn.nnn.nnn, where nnn is number between 1 and 255"});
	    BeanFieldMeta.PATTERNS.put("PATTERN_GST",new Object[]{Pattern.compile("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z0-9]{1}[A-Z0-9]{1}[A-Z\\d]{1}"), "First 2 digits of the GST Number will represent State Code. Next 10 digits will be PAN. (First 5 alphabets, Next 4 numbers, Last check code). The 13th digit will be character registration you take within a state. 14th digit will be blank by default. Last would be the check code. E.g. 05AHTPM8096N2Z5"});
	    //OVERRIDING THE FIRST/MIDDLE/LAST NAME - TO ALLOW SPACE IN THE NAME - AFTER JOCATA PUSHED NAME "MOHAMMAD NOOR ALAM"
	    BeanFieldMeta.PATTERNS.put("PATTERN_ALPHA",new Object[]{Pattern.compile("^[a-zA-Z' ]*$"), "Only alphabets allowed."});
	    logger.debug("AppInitializer : "+ (lCounter++) + " : BeanMetaFactory loaded.");
        //
        try (Connection lConnection = DBHelper.getInstance().getConnection();) {
            appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
            MemoryDBManager.getInstance().setMemoryDBLoader(this);
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : MemoryDBManager.");
            loadTable(null, true);
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : loadTable.");
            RegistryHelper.getInstance();
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : RegistryHelper.");
            AccessControlHelper.createInstance(this);
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : AccessControlHelper.");
            RefMasterHelper.getInstance();
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : RefMasterHelper.");
            AuthenticationHandler.createInstance(this, lConnection);
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : AuthenticationHandler.");
            if ("Y".equals(System.getProperty("ISMASTER"))){
                MarketMonitor.createInstance();
        	    logger.debug("AppInitializer : "+ (lCounter++) + " : MarketMonitor.");
				//change here
                SFTPClientHolder.createInstance(SFTPClientHolder.KEY_TREDSSFTPCLIENT, SFTPClientHolder.FILENAME_TREDSSFTPCLIENT);
                SFTPClientHolder.createInstance(SFTPClientHolder.KEY_TREDSDBBACKUP, SFTPClientHolder.FILENAME_TREDSDBBACKUP);
        	    logger.debug("AppInitializer : "+ (lCounter++) + " : Treds SFTPClient.");
            }
            //logs are present on all servers hence, this will run on all servers
            //SFTPClientHolder.createInstance(SFTPClientHolder.KEY_LOGSFTPCLIENT, SFTPClientHolder.FILENAME_LOGSSFTPCLIENT);
    	    //logger.debug("AppInitializer : "+ (lCounter++) + " : Treds Log SFTPClient.");
    	    //
            initializeBulkMailSender();
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : initializeBulkMailSender.");
            PostProcessMonitor.createInstance();
    	    logger.debug("AppInitializer : "+ (lCounter++) + " : PostProcessMonitor.");
    	    logger.debug("AppInitializer : FINISED SUCCESFULLY.");
        } catch (Exception lException) {
            logger.error("Error while initializing", lException);
        } 
    }
    
    public void loadTable(String pTableName,boolean pForce) throws Exception {
        Connection lConnection = null;
        try {
            lConnection = DBHelper.getInstance().getConnection();
            MemoryDBManager lMemoryDBManager = MemoryDBManager.getInstance();
            if ((pTableName == null) || IAppUserBean.ENTITY_NAME.equals(pTableName)) {
                MemoryTable lMemoryTable = new MemoryTable(IAppUserBean.ENTITY_NAME, AppUserBean.class);
                if (pForce || !lMemoryDBManager.tableExists(lMemoryTable)) {
                    lMemoryTable.addIndex(IAppUserBean.f_Domain_LoginId, true, new int[]{AppUserBean.idx_Domain, AppUserBean.idx_LoginId});
                    lMemoryTable.addIndex(IAppUserBean.f_Id, true, new int[]{AppUserBean.idx_Id});
                    String lSql = "SELECT * FROM AppUsers WHERE AURecordVersion > 0";
                    List<AppUserBean> lList = appUserDAO.findListFromSql(lConnection, lSql, -1);
                    lMemoryTable.addRows(lList);
                    lMemoryDBManager.addDistributedTable(lMemoryTable,true);
                }
            }
            if ((pTableName == null) || AppEntityBean.ENTITY_NAME.equals(pTableName)) {
                MemoryTable lMemoryTable = new MemoryTable(AppEntityBean.ENTITY_NAME, AppEntityBean.class);
                if (pForce || !lMemoryDBManager.tableExists(lMemoryTable)) {
                    lMemoryTable.addIndex(AppEntityBean.f_Code, true, new int[]{AppEntityBean.idx_Code});
                    String lSql = "SELECT * FROM AppEntities WHERE AERecordVersion > 0";
                    GenericDAO<AppEntityBean> lAppEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
                    List<AppEntityBean> lList = lAppEntityDAO.findListFromSql(lConnection, lSql, -1);
                    lMemoryTable.addRows(lList);
                    lMemoryDBManager.addDistributedTable(lMemoryTable,true);
                }
            }
            if ((pTableName == null) || HolidayMasterBean.ENTITY_NAME.equals(pTableName)) {
                MemoryTable lMemoryTable = new MemoryTable(HolidayMasterBean.ENTITY_NAME, HolidayMasterBean.class);
                if (pForce || !lMemoryDBManager.tableExists(lMemoryTable)) {
                    lMemoryTable.addIndex(HolidayMasterBean.f_Id, true, new int[]{HolidayMasterBean.idx_Id});
                    lMemoryTable.addIndex(HolidayMasterBean.f_Date, true, new int[]{HolidayMasterBean.idx_Date});
                    String lSql = "SELECT * FROM HolidayMaster WHERE HMRecordVersion > 0";
                    GenericDAO<HolidayMasterBean> lHolidayMasterDAO = new GenericDAO<HolidayMasterBean>(HolidayMasterBean.class);
                    List<HolidayMasterBean> lList = lHolidayMasterDAO.findListFromSql(lConnection, lSql, -1);
                    lMemoryTable.addRows(lList);
                    lMemoryDBManager.addDistributedTable(lMemoryTable,true);
                }
            }

            if ((pTableName == null) || AuctionCalendarBean.ENTITY_NAME.equals(pTableName)) {
                MemoryTable lMemoryTable = new MemoryTable(AuctionCalendarBean.ENTITY_NAME, AuctionCalendarBean.class);
                if (pForce || !lMemoryDBManager.tableExists(lMemoryTable)) {
                    lMemoryTable.addIndex(AuctionCalendarBean.f_Id, true, new int[]{AuctionCalendarBean.idx_Id});
                    lMemoryTable.addIndex(AuctionCalendarBean.f_TypeAuctionDay, true, new int[]{AuctionCalendarBean.idx_Type, AuctionCalendarBean.idx_AuctionDay});
                    String lSql = "SELECT * FROM AuctionCalendar WHERE ACRecordVersion > 0 AND ACDate >= (SELECT SPDate FROM SystemParameters WHERE SPRecordVersion > 0) ORDER BY ACDate";
                    GenericDAO<AuctionCalendarBean> lAuctionCalendarDAO = new GenericDAO<AuctionCalendarBean>(AuctionCalendarBean.class);
                    List<AuctionCalendarBean> lList = lAuctionCalendarDAO.findListFromSql(lConnection, lSql, 2);
                    for (int lPtr=0;lPtr<lList.size();lPtr++) {
                        AuctionCalendarBean lAuctionCalendarBean = lList.get(lPtr);
                        lAuctionCalendarBean.setAuctionDay(lPtr==0?AuctionCalendarBean.AuctionDay.Today:AuctionCalendarBean.AuctionDay.Tomorrow);
                    }
                    lMemoryTable.addRows(lList);
                    lMemoryDBManager.addDistributedTable(lMemoryTable,true);
                }
            }
            if ((pTableName == null) || SystemParameterBean.ENTITY_NAME.equals(pTableName)) {
                MemoryTable lMemoryTable = new MemoryTable(SystemParameterBean.ENTITY_NAME, SystemParameterBean.class);
                if (pForce || !lMemoryDBManager.tableExists(lMemoryTable)) {
                    lMemoryTable.addIndex(SystemParameterBean.f_Id, true, new int[]{SystemParameterBean.idx_Id});
                    String lSql = "SELECT * FROM SystemParameters WHERE SPRecordVersion > 0";
                    GenericDAO<SystemParameterBean> lSystemParameterDAO = new GenericDAO<SystemParameterBean>(SystemParameterBean.class);
                    List<SystemParameterBean> lList = lSystemParameterDAO.findListFromSql(lConnection, lSql, -1);
                    lMemoryTable.addRows(lList);
                    lMemoryDBManager.addDistributedTable(lMemoryTable,true);
                }
            }
        } finally {
            if (lConnection != null) {
                try {
                    lConnection.close();
                } catch (Exception lException) {
					logger.error("loadTable : Error while closing database connection", lException);
                }
            }
        }
    }
    
    public void notifyRecord(String pTableName, int pTransactionType,
            String pRecord) throws Exception {
    }
    
    public IAppUserBean getUserBean(ExecutionContext pExecutionContext, String pDomain, String pLoginId) throws Exception {
        if ((pExecutionContext==null) || pExecutionContext.isAutoCommit()) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
            return (IAppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Domain_LoginId, new String[]{pDomain, pLoginId});
        } else {
            MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
            return (IAppUserBean)lMemoryDBConnection.selectSingleRowForUpdate(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Domain_LoginId, new String[]{pDomain, pLoginId});
        }
    }
    
    public IAppUserBean getUserBean(ExecutionContext pExecutionContext, Long pAuId) throws Exception {
        if ((pExecutionContext==null) || pExecutionContext.isAutoCommit()) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
            return (IAppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{pAuId});
        } else {
            MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
            return (IAppUserBean)lMemoryDBConnection.selectSingleRowForUpdate(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Id, new Long[]{pAuId});
        }
    }
    
	public void handleSession(ExecutionContext pExecutionContext, LoginSessionBean pLoginSessionBean) throws Exception {
		// set request ip from header
		if (pLoginSessionBean.getRequest() != null) {
			String lRequestIp = pLoginSessionBean.getRequest().getHeader("True-Client-IP");
			if (StringUtils.isNotBlank(lRequestIp))
				pLoginSessionBean.setRequestIp(lRequestIp);
		}

	    if (pLoginSessionBean.getStatus() != LoginSessionBean.Status.Success)
	        return;
		IAppUserBean lAppUserBean = getUserBean(pExecutionContext, pLoginSessionBean.getAuId());
		if (lAppUserBean == null)
			throw new CommonBusinessException("Unexpected error. Appuser not found " + pLoginSessionBean.getAuId());
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lAppUserBean.getDomain()});
        if (lAppEntityBean == null) 
        	throw new CommonBusinessException("Unexpected error. Appentity not found " + lAppUserBean.getDomain());
        // check for click wrap agreement
        if (lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier() ) { // && appentity.lastversion is null or (mismatch and (version date + graceperiod) < currentbusinessdate)
        	RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        	String lLatestVersion = null;
        	HashMap lValidAgreements = null;
        	HashMap lLatestAgreement = null;
        	HashMap lRequiredAgreement = null;
        	Date lLatestVersionDate = null, lRequiredVersionDate=null;
        	Long lLatestVersionGracePeriod = null, lRequiredVersionGracePeriod=null;
        	String lPurAcceptedVersion=null, lPurRequiredVersion;
        	
        	if (ClickWrapHelper.getInstance().isAgreementEnabled(lAppEntityBean.getCode()))
       	    {        	
	       	    String KEY_LATESTVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_SUPPLIER:"");
	       	    String KEY_VALIDVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_SUPPLIER:"");
	
	        	//find latest version information
	        	lLatestVersion = lRegistryHelper.getString(KEY_LATESTVERSION);
	        	lValidAgreements = lRegistryHelper.getKeyedValues(KEY_VALIDVERSION,RegistryEntryBean.DATATTYPE_STRUCTURE);
	        	lLatestAgreement = (HashMap) lValidAgreements.get(lLatestVersion);
	        	lLatestVersionDate = CommonUtilities.getDate( new Timestamp((Long)lLatestAgreement.get(PARAM_CLICKWRAP_REVISIONDATE)));
	        	lLatestVersionGracePeriod = (Long) lLatestAgreement.get(PARAM_CLICKWRAP_GRACEPERIOD);
	        	//find Purcahsers last accepted aggreemnt
	        	lPurAcceptedVersion = lAppEntityBean.getAcceptedAgreementVersion();
	        	//required version details
	        	lPurRequiredVersion = lAppEntityBean.getRequiredAgreementVersion();
	        	if(StringUtils.isEmpty(lPurRequiredVersion))lPurRequiredVersion = lLatestVersion;
	        	lRequiredAgreement = (HashMap) lValidAgreements.get(lPurRequiredVersion);
	        	lRequiredVersionDate = CommonUtilities.getDate( new Timestamp((Long)lRequiredAgreement.get(PARAM_CLICKWRAP_REVISIONDATE)));
	        	lRequiredVersionGracePeriod = (Long) lRequiredAgreement.get(PARAM_CLICKWRAP_GRACEPERIOD);
	        	
	        	Date lLastDate = CommonUtilities.addDays(lRequiredVersionDate, lRequiredVersionGracePeriod.intValue());
	    		Date lCurrentDate = TredsHelper.getInstance().getBusinessDate();
	    		if (!CommonUtilities.hasValue(lPurAcceptedVersion) || (!lPurRequiredVersion.equalsIgnoreCase(lPurAcceptedVersion) && 
	    				(lCurrentDate.compareTo(lLastDate) > 0))) {
	    			//ONLY IF THE ADMIN IS SYSTEM CREATED ADMIN
	        		if( AppUserBean.Type.Admin.equals(((AppUserBean)lAppUserBean).getType()) &&
	        				AppConstants.LOGINID_ADMIN.equals(lAppUserBean.getLoginId()) ){
	            		pLoginSessionBean.setRestrictedToGroup(AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT);
	        		}else if( AppUserBean.Type.User.equals(((AppUserBean)lAppUserBean).getType())){
	            		// else if dealer plogisessionbean.setsessionkey null, status=failed, reason
	        			pLoginSessionBean.setSessionKey(null);
	            		pLoginSessionBean.setStatus(LoginSessionBean.Status.Failed);;
	            		pLoginSessionBean.setReason("Your company has not accepted the Agreement. " + CommonBusinessException.ACCESS_DENIED);
	        		}
	    		}
       	    }
        }
	}

	public void validateLogin(IAppUserBean pAppUserBean, LoginSessionBean pLoginSessionBean) throws Exception {
	    AppUserBean lAppUserBean = (AppUserBean)pAppUserBean;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pAppUserBean.getDomain()});
        //api user cannot login from browser
        if( CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) && pLoginSessionBean.getMode()!=null ){
        	throw new CommonBusinessException("API user cannot login from Browser.");
        }
        //normal user cannot login from api
        if(!CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI()) && pLoginSessionBean.getMode()==null){
        	throw new CommonBusinessException("User cannot login through API.");
        }
        //
        List<String> lIpList = lAppUserBean.getIpList();
        if ((lIpList == null) || (lIpList.size() == 0)) {
            lIpList = lAppEntityBean.getIpList();
        }
        if ((lIpList != null) && (lIpList.size() > 0)) {
            boolean lFound = false;
            for (String lIp:lIpList) {
                if (lIp.equals(pLoginSessionBean.getRequestIp())) {
                    lFound = true;
                    break;
                }
            }
            if (!lFound)
                throw new CommonBusinessException("Login not allowed from this IP : " + pLoginSessionBean.getRequestIp());
        }
	}
	
    public void addUserBean(ExecutionContext pExecutionContext, IAppUserBean pUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        appUserDAO.insert(lConnection, (AppUserBean)pUserBean);
        lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, pUserBean);
    }

    public void updateUserBean(ExecutionContext pExecutionContext,
            IAppUserBean pUserBean, String pFieldGroup) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        appUserDAO.update(lConnection, (AppUserBean)pUserBean, pFieldGroup);
        lMemoryDBConnection.deleteRow(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Id, pUserBean);
        lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, pUserBean);
    }

    public void appendUserInfo(ExecutionContext pExecutionContext, IAppUserBean pAppUserBean, 
            LoginSessionBean pLoginSessionBean, Map<String, Object> pUserInfo) {
        AppUserBean lAppUserBean = (AppUserBean)pAppUserBean;
        pUserInfo.put("userType", lAppUserBean.getType().getCode());
        boolean lPlatformUser = AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain());
        pUserInfo.put("platform", Boolean.valueOf(lPlatformUser));
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        try {
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pAppUserBean.getDomain()});
            if (lAppEntityBean != null) {
                pUserInfo.put("entity", lAppEntityBean.getName());
                //pUserInfo.put("logo","logo.png");
                pUserInfo.put("entityType", lAppEntityBean.getType());
                //for controlling through state- 
                List<String> lEntityTypeList = lAppUserBean.getUserTypes();
                if(AppUserBean.Type.Admin.equals(lAppUserBean.getType())){
                    if(lEntityTypeList!=null){
                    	List<String> lNewTypeList = new ArrayList<String>();
                    	List<String> lEntityCodes= new ArrayList<String>();
                    	lEntityCodes.add(AppConstants.EntityType.Purchaser.getCode());
                    	lEntityCodes.add(AppConstants.EntityType.Supplier.getCode());
                    	lEntityCodes.add(AppConstants.EntityType.Financier.getCode());
                    	for(String lEntityType : lEntityTypeList){
                    		if(lEntityCodes.contains(lEntityType)){
                    			lNewTypeList.add(lEntityType+"1");
                    		}
                    	}
                    	lEntityTypeList.addAll(lNewTypeList);
                    }
                }
                pUserInfo.put("entityTypeList", lEntityTypeList);
                pUserInfo.put("entityTypeDesc", lAppEntityBean.getTypeDesc());
                // if purchaser and mismatch and version.date + grace < curr bus date { set warnAgreement = true}
                if((lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()) && (lAppUserBean.getType()==Type.Admin)){
                	if ( ClickWrapHelper.getInstance().isAgreementEnabled(lAppEntityBean.getCode()) ){
                 	RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
                	String lLatestVersion = null;
                	HashMap lValidAgreements = null;
                	HashMap lLatestAgreement = null;
                	Date lLatestVersionDate = null;
                	Long lLatestVersionGracePeriod = null;
                	String lPurVersion=null;
               	    String KEY_LATESTVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_SUPPLIER:"");
               	    String KEY_VALIDVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_SUPPLIER:"");

                	//find latest version information
                	lLatestVersion = lRegistryHelper.getString(KEY_LATESTVERSION);
                	lValidAgreements = lRegistryHelper.getKeyedValues(KEY_VALIDVERSION,RegistryEntryBean.DATATTYPE_STRUCTURE);
                	lLatestAgreement = (HashMap) lValidAgreements.get(lLatestVersion);
                	lLatestVersionDate = CommonUtilities.getDate( new Timestamp((Long)lLatestAgreement.get(PARAM_CLICKWRAP_REVISIONDATE)));
                	lLatestVersionGracePeriod = (Long) lLatestAgreement.get(PARAM_CLICKWRAP_GRACEPERIOD);
                	//find Purcahsers last accepted aggreemnt
                	lPurVersion = lAppEntityBean.getAcceptedAgreementVersion();
                	if (!lLatestVersion.equals(lPurVersion)) {
                		Date lLastDate = CommonUtilities.addDays(lLatestVersionDate, lLatestVersionGracePeriod.intValue());
                		Date lCurrentDate = TredsHelper.getInstance().getBusinessDate();
                		if (CommonUtilities.hasValue(lPurVersion) && lCurrentDate.compareTo(lLastDate) <= 0)
                			pUserInfo.put("warnAgreement", Boolean.TRUE);
	                	}
                	}
                }
            }
        } catch (Exception lException) {
            logger.error("Error while getting app entity bean for " + pAppUserBean.getDomain(), lException);
        }
    }

    public void checkRoleManageAccess(RoleMasterBean pRoleMasterBean, IAppUserBean pAppUserBean) throws CommonBusinessException {
    	if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) && !AppConstants.Owner.Public.getCode().equals(pRoleMasterBean.getOwner()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    }

    public void appendFilterForRoleList(RoleMasterBean pFilterBean, IAppUserBean pUserBean) {
    	if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
    		pFilterBean.setOwner(AppConstants.Owner.Public.getCode());
    	}
    }

    public String[][] getUserTypes(ExecutionContext pExecutionContext)
            throws Exception {
        return new String[][] {
                new String[]{AppConstants.EntityType.Purchaser.getCode(), AppConstants.EntityType.Purchaser.toString()}
                ,new String[]{AppConstants.EntityType.Supplier.getCode(), AppConstants.EntityType.Supplier.toString()}
                ,new String[]{AppConstants.EntityType.Financier.getCode(), AppConstants.EntityType.Financier.toString()}
                ,new String[]{AppConstants.EntityType.Platform.getCode(), AppConstants.EntityType.Platform.toString()}
                ,new String[]{AppConstants.EntityType.RegEntity.getCode(), AppConstants.EntityType.RegEntity.toString()}
                ,new String[]{AppConstants.EntityType.Regulator.getCode(), AppConstants.EntityType.Regulator.toString()}
                ,new String[]{AppConstants.EntityType.Aggregator.getCode(), AppConstants.EntityType.Aggregator.toString()}
        };
    }
    
    public boolean isPlatformAdmin(IAppUserBean pAppUserBean) {
        return AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain());
    }

    private void initializeBulkMailSender(){
    	BulkMailSenderFactory lBulkMailSenderFactory = BulkMailSenderFactory.getInstance(BulkMailSenderFactory.class);
    	lBulkMailSenderFactory.registerMailSender(new BSLinkCounterPendingApproval());
    }

	@Override
	public void updateUserBeanPassword(ExecutionContext pExecutionContext, IAppUserBean pUserBean, LoginBean pLoginBean)
			throws Exception {
		updateUserBean(pExecutionContext, pUserBean, IAppUserBean.FIELDGROUP_PASSWORD);
	}
}
