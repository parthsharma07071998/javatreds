package com.xlx.treds;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.treds.AppConstants.MailerType;
import com.xlx.treds.AppConstants.MessageType;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyKYCDocumentBean;
import com.xlx.treds.entity.bean.EntityNotificationSettingBean;
import com.xlx.treds.entity.bean.EntityOtpNotificationSettingBean;
import com.xlx.treds.master.bean.AlertMasterBean;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.master.bean.HolidayMasterBean;
import com.xlx.treds.master.bean.KYCDocumentMasterBean;
import com.xlx.treds.master.bean.NotificationMasterBean;
import com.xlx.treds.master.bean.OtpNotificationMasterBean;

import groovy.json.JsonBuilder;

public class OtherResourceCache {
	private static final Logger logger = LoggerFactory.getLogger(OtherResourceCache.class);

	public static final String REFCODE_INDUSTRY = "INDUSTRY";
	public static final String REFCODE_SUBSEGMENT = "SUBSEGMENT";
	public static final String REFCODE_RATING = "RATING_VALUE";
	public static final String REFCODE_SECTOR = "SECTOR";
	public static final String REFCODE_CONSTITUTION = "CONSTITUTION";
	public static final String REFCODE_ENTITYTYPE = "ENTITYTYPE";
	public static final String REFCODE_DOCUMENTTYPE = "DOCUMENTTYPE";
	public static final String REFCODE_DOCUMENTCATEGORY = "DOCUMENTCATEGORY";
	public static final String REFCODE_DOCUMENT = "DOCUMENT";
	public static final String REFCODE_DESIGNATION = "DESIGNATION";

	public static final String REPEAT_TYPE = "repeatDocType";
	public static final String DOCUMENT_FOR_CONTACTID = "docForCCId";	
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String DOCUMENT_TYPE_DESC = "documentTypeDesc";
	public static final String DOCUMENT_CATEGORY = "docCat";
	public static final String DOCUMENT_CATEGORY_DESC = "docCatDesc";
	public static final String DOCUMENT_SOFTCOPY = "soft";
	public static final String DOCUMENT_HARDCOPY = "hard";
	public static final String MIN_COUNT = "minCount";
	public static final String MAX_COUNT = "maxCount";
	public static final String DOCUMENT_LIST = "documentList";

	public static final String SEPERATOR = ".";
	public static final String AUCTIONTYPE_NORMAL = "N";

	private static OtherResourceCache theInstance;

	public static final long UTC_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	private String industryJson;
	private Map<String, String> subSegmentJsonMap;// key=indcode,
	// value=subsegmentjson
	private Map<String, String> designationJsonMap;// key=constitution,
	// value=designationjson
	private Map<String, List<Map<String, Object>>> kycDocumentMap; // key=const^entitytype,
	// value=list
	// of map
	private Map<String, Map<String, Integer>> documentTypeDocumentWiseIndex; // key=const^entitytype,
	// value
	// =
	// {key=documenttype.docCat.document,
	// value=Index
	// in
	// kycdocumentMap};
	private Map<String, String> ratingJsonMap;// key=ratingType,
	// value=ratingjson
	
	private List<Map<String, Object>>[] notificationSettings;// index:0-supp,1=purc,2=fin
	private List<Map<String, Object>>[] otpNotificationSettings;// index:0-supp,1=purc,2=fin
	private Map<String, Object> categoryWiseRolesHash; //Key:Category , Value:RoleList
	private ArrayList<Object> alertSettings;
	// map element :
	// {cat:,list:[]}

	private GenericDAO<EntityNotificationSettingBean> entityNotificationSettingDAO;
	private GenericDAO<EntityOtpNotificationSettingBean> entityOtpNotificationSettingDAO;

	public static OtherResourceCache getInstance() {
		if (theInstance == null) {
			synchronized (OtherResourceCache.class) {
				if (theInstance == null) {
					OtherResourceCache tmpTheInstance = new OtherResourceCache();
					theInstance = tmpTheInstance;
				}
			}
		}
		return theInstance;
	}

	private OtherResourceCache() {
		super();
		try (Connection lConnection = DBHelper.getInstance().getConnection();) {
			loadIndSegSect();
			loadDesignation();
			loadKYCMaster(lConnection);
			loadNotificationSettings(lConnection);
			loadOtpNotificationSettings(lConnection);
			loadAlertSettings(lConnection);
			loadRatingValues();
			entityNotificationSettingDAO = new GenericDAO<EntityNotificationSettingBean>(
					EntityNotificationSettingBean.class);
			entityOtpNotificationSettingDAO = new GenericDAO<EntityOtpNotificationSettingBean>(
					EntityOtpNotificationSettingBean.class);
		} catch (Exception lException) {
			logger.error("Error while loading other resource cache", lException);
        }
	}

	private void loadIndSegSect() {
		try {
			List<Map<String, String>> lIndustriesJson = new ArrayList<Map<String, String>>();
			ArrayList<RefCodeValuesBean> lIndustries = RefMasterHelper.getInstance().getRefCodeValues(REFCODE_INDUSTRY);
			for (RefCodeValuesBean lIndustryBean : lIndustries) {
				Map<String, String> lIndustryJson = new HashMap<String, String>();
				lIndustryJson.put(BeanFieldMeta.JSONKEY_VALUE, lIndustryBean.getValue());
				lIndustryJson.put(BeanFieldMeta.JSONKEY_TEXT, lIndustryBean.getDesc());
				lIndustriesJson.add(lIndustryJson);
			}
			industryJson = new JsonBuilder(lIndustriesJson).toString();

			// subsegment json
			Map<String, List<Map<String, String>>> lIndustryWiseSubSegmentsJson = new HashMap<String, List<Map<String, String>>>();
			ArrayList<RefCodeValuesBean> lSubSegments = RefMasterHelper.getInstance().getRefCodeValues(REFCODE_SUBSEGMENT);
			for (RefCodeValuesBean lSubSegmentBean : lSubSegments) {
				String[] lSplits = CommonUtilities.splitString(lSubSegmentBean.getValue(), SEPERATOR);
				List<Map<String, String>> lSubSegmentJsonsList = lIndustryWiseSubSegmentsJson.get(lSplits[0]);
				if (lSubSegmentJsonsList == null) {
					lSubSegmentJsonsList = new ArrayList<Map<String, String>>();
					lIndustryWiseSubSegmentsJson.put(lSplits[0], lSubSegmentJsonsList);
				}
				Map<String, String> lSubSegmentJson = new HashMap<String, String>();
				lSubSegmentJson.put(BeanFieldMeta.JSONKEY_VALUE, lSplits[1]);
				lSubSegmentJson.put(BeanFieldMeta.JSONKEY_TEXT, lSubSegmentBean.getDesc());
				lSubSegmentJsonsList.add(lSubSegmentJson);
			}
			subSegmentJsonMap = new HashMap<String, String>();
			for (String lKey : lIndustryWiseSubSegmentsJson.keySet()) {
				subSegmentJsonMap.put(lKey, new JsonBuilder(lIndustryWiseSubSegmentsJson.get(lKey)).toString());
				logger.info(lKey + "\n\t" + subSegmentJsonMap.get(lKey));
			}

		} catch (Exception lException) {
			logger.error("Error while loading industry, subsegment", lException);
		}
	}
	
	private void loadRatingValues() {
		try {
			Map<String, List<Map<String, String>>> lRatingTypeWiseRatingJson = new HashMap<String, List<Map<String, String>>>();
			ArrayList<RefCodeValuesBean> lRatings = RefMasterHelper.getInstance().getRefCodeValues(REFCODE_RATING);
			if(lRatings!=null && lRatings.size() > 0) {
				for (RefCodeValuesBean lRatingBean : lRatings) {
					String[] lSplits = CommonUtilities.splitString(lRatingBean.getValue(), SEPERATOR);
					List<Map<String, String>> lRatingJsonsList = lRatingTypeWiseRatingJson.get(lSplits[0]);
					if (lRatingJsonsList == null) {
						lRatingJsonsList = new ArrayList<Map<String, String>>();
						lRatingTypeWiseRatingJson.put(lSplits[0], lRatingJsonsList);
					}
					Map<String, String> lRatingJson = new HashMap<String, String>();
					lRatingJson.put(BeanFieldMeta.JSONKEY_VALUE, lSplits[0]+SEPERATOR+lSplits[1]);
					lRatingJson.put(BeanFieldMeta.JSONKEY_TEXT, lRatingBean.getDesc());
					lRatingJsonsList.add(lRatingJson);
				}
				ratingJsonMap = new HashMap<String, String>();
				for (String lKey : lRatingTypeWiseRatingJson.keySet()) {
					ratingJsonMap.put(lKey, new JsonBuilder(lRatingTypeWiseRatingJson.get(lKey)).toString());
					logger.info(lKey + "\n\t" + ratingJsonMap.get(lKey));
				}
			}
		} catch (Exception lException) {
			logger.error("Error while loading rating", lException);
		}
	}

	private void loadKYCMaster(Connection pConnection) {
		try {
            GenericDAO<KYCDocumentMasterBean> lKYCDocumentMasterDAO = new GenericDAO<KYCDocumentMasterBean>(KYCDocumentMasterBean.class);
			StringBuffer lSql = new StringBuffer();
			lSql.append(" SELECT * FROM KYCDocumentMaster WHERE  KDMRecordVersion > 0 ORDER BY KDMID ");
            List<KYCDocumentMasterBean> lAllDocuments = lKYCDocumentMasterDAO.findListFromSql(pConnection, lSql.toString(), -1);
			Map<String, List<KYCDocumentMasterBean>> lConstWiseDocuments = new HashMap<String, List<KYCDocumentMasterBean>>();
			for (KYCDocumentMasterBean lKYCDocumentMasterBean : lAllDocuments) {
				for (String lConstitution : lKYCDocumentMasterBean.getConstitutionList()) {
					List<KYCDocumentMasterBean> lDocuments = lConstWiseDocuments.get(lConstitution);
					if (lDocuments == null) {
						lDocuments = new ArrayList<KYCDocumentMasterBean>();
						lConstWiseDocuments.put(lConstitution, lDocuments);
					}
					lDocuments.add(lKYCDocumentMasterBean);
				}
			}
			ArrayList<RefCodeValuesBean> lEntityTypes = RefMasterHelper.getInstance().getRefCodeValues(REFCODE_ENTITYTYPE);
			kycDocumentMap = new ConcurrentHashMap<String, List<Map<String, Object>>>();
			documentTypeDocumentWiseIndex = new ConcurrentHashMap<String, Map<String, Integer>>();
			for (String lConstitution : lConstWiseDocuments.keySet()) {
				List<KYCDocumentMasterBean> lDocuments = lConstWiseDocuments.get(lConstitution);
				for (RefCodeValuesBean lEntityTypeBean : lEntityTypes) {
					String lValue = lEntityTypeBean.getValue();
					boolean lSupplier = CommonAppConstants.YesNo.Yes.getCode().equals(lValue.substring(0, 1));
					boolean lPurchaser = CommonAppConstants.YesNo.Yes.getCode().equals(lValue.substring(1, 2));
					boolean lFinancier = CommonAppConstants.YesNo.Yes.getCode().equals(lValue.substring(2, 3));
					List<Map<String, Object>> lDocumentTypeList = new ArrayList<Map<String, Object>>();
					Map<String, Integer> lDocumentTypeDocumentIndexMap = new ConcurrentHashMap<String, Integer>();// key
					// =
					// documentType
					// +
					// seperator
					// +
					// document
					for (KYCDocumentMasterBean lKYCDocumentMasterBean : lDocuments) {
						int lMinCount = Integer.MAX_VALUE, lMaxCount = 0;
						if (lSupplier) {
                            lMinCount = lMinCount < lKYCDocumentMasterBean.getMinSupplier().intValue() ? lMinCount : lKYCDocumentMasterBean.getMinSupplier().intValue();
                            lMaxCount = lMaxCount > lKYCDocumentMasterBean.getMaxSupplier().intValue() ? lMaxCount : lKYCDocumentMasterBean.getMaxSupplier().intValue();
						}
						if (lPurchaser) {
                            lMinCount = lMinCount < lKYCDocumentMasterBean.getMinPurchaser().intValue() ? lMinCount : lKYCDocumentMasterBean.getMinPurchaser().intValue();
                            lMaxCount = lMaxCount > lKYCDocumentMasterBean.getMaxPurchaser().intValue() ? lMaxCount : lKYCDocumentMasterBean.getMaxPurchaser().intValue();
						}
						if (lFinancier) {
                            lMinCount = lMinCount < lKYCDocumentMasterBean.getMinFinancier().intValue() ? lMinCount : lKYCDocumentMasterBean.getMinFinancier().intValue();
                            lMaxCount = lMaxCount > lKYCDocumentMasterBean.getMaxFinancier().intValue() ? lMaxCount : lKYCDocumentMasterBean.getMaxFinancier().intValue();
						}
						if ((lMinCount <= lMaxCount) && (lMaxCount > 0)) {
							Map<String, Object> lDocumentTypeDetail = new HashMap<String, Object>();

							lDocumentTypeDetail.put(DOCUMENT_TYPE, lKYCDocumentMasterBean.getDocumentType());
                            RefCodeValuesBean lDocumentTypeBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lKYCDocumentMasterBean.getDocumentType(),
                                    REFCODE_DOCUMENTTYPE);
							lDocumentTypeDetail.put(DOCUMENT_TYPE_DESC, lDocumentTypeBean.getDesc());
							lDocumentTypeDetail.put(MIN_COUNT, Long.valueOf(lMinCount));
							lDocumentTypeDetail.put(MAX_COUNT, Long.valueOf(lMaxCount));
							lDocumentTypeDetail.put(DOCUMENT_CATEGORY, lKYCDocumentMasterBean.getDocumentCat());
							lDocumentTypeDetail.put(DOCUMENT_FOR_CONTACTID, null);
                            RefCodeValuesBean lDocumentCategoryBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lKYCDocumentMasterBean.getDocumentCat(),
                                    REFCODE_DOCUMENTCATEGORY);
							lDocumentTypeDetail.put(DOCUMENT_CATEGORY_DESC, lDocumentCategoryBean.getDesc());
							if (lKYCDocumentMasterBean.getSoftCopy() == CommonAppConstants.YesNo.Yes)
								lDocumentTypeDetail.put(DOCUMENT_SOFTCOPY, Boolean.TRUE);
							if (lKYCDocumentMasterBean.getHardCopy() == CommonAppConstants.YesNo.Yes)
								lDocumentTypeDetail.put(DOCUMENT_HARDCOPY, Boolean.TRUE);
							List<Map<String, String>> lDocumentList = new ArrayList<Map<String, String>>();
							lDocumentTypeDetail.put(DOCUMENT_LIST, lDocumentList);
							lDocumentTypeDetail.put(REPEAT_TYPE, (lKYCDocumentMasterBean.getRepeatType()!=null?lKYCDocumentMasterBean.getRepeatType().getCode():""));

							for (String lDocumentStr : lKYCDocumentMasterBean.getDocumentList()) {
								RefCodeValuesBean lDocumentBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lDocumentStr, REFCODE_DOCUMENT);
								Map<String, String> lDocument = new HashMap<String, String>();
								lDocument.put(BeanFieldMeta.JSONKEY_VALUE, lDocumentStr);
								lDocument.put(BeanFieldMeta.JSONKEY_TEXT, lDocumentBean.getDesc());
								lDocumentList.add(Collections.unmodifiableMap(lDocument));
                                lDocumentTypeDocumentIndexMap.put(lKYCDocumentMasterBean.getDocumentType() + SEPERATOR + lKYCDocumentMasterBean.getDocumentCat() + SEPERATOR + lDocumentStr, 
                                        Integer.valueOf(lDocumentTypeList.size()));
							}
							lDocumentTypeList.add(Collections.unmodifiableMap(lDocumentTypeDetail));
						}// end if
					}// end for kycdocuments
					String lKey = lConstitution + SEPERATOR + lValue;
					kycDocumentMap.put(lKey, Collections.unmodifiableList(lDocumentTypeList));
					documentTypeDocumentWiseIndex.put(lKey, Collections.unmodifiableMap(lDocumentTypeDocumentIndexMap));
				}// end for entitytype
			}// end for constitution
			kycDocumentMap = Collections.unmodifiableMap(kycDocumentMap);
			documentTypeDocumentWiseIndex = Collections.unmodifiableMap(documentTypeDocumentWiseIndex);
		} catch (Exception lException) {
			logger.error("Error while loading kyc document master", lException);
		}
	}

	private void loadNotificationSettings(Connection pConnection) {
		try {
            GenericDAO<NotificationMasterBean> lNotificationMasterDAO = new GenericDAO<NotificationMasterBean>(NotificationMasterBean.class);
            List<NotificationMasterBean> lList = lNotificationMasterDAO.findListFromSql(pConnection, 
                    "SELECT * FROM NotificationMaster WHERE NMRecordVersion > 0 ORDER BY NMSequence", -1);
            notificationSettings = new List[]{new ArrayList<Map<String,Object>>(), new ArrayList<Map<String,Object>>(), new ArrayList<Map<String,Object>>()};
            Map<String,Object>[] lNotificationsMap = new Map[]{new HashMap<String,Object>(), new HashMap<String,Object>(), new HashMap<String,Object>()}; // key category, value=list of map
																					// category,
																					// value=list
			// of map
            List<HashMap<String, Object>> lGrpList = new ArrayList<>();
            Map<String, Object> lGrpMap = null;
            categoryWiseRolesHash = new HashMap<String, Object>();
            for (NotificationMasterBean lBean : lList) {
                CommonAppConstants.YesNo[] lEntityList = new CommonAppConstants.YesNo[]{lBean.getSupplier(), lBean.getPurchaser(), lBean.getFinancier()};
				for (int lPtr = 0; lPtr < lEntityList.length; lPtr++) {
					if (lEntityList[lPtr] == CommonAppConstants.YesNo.Yes) {
						List<Map<String, Object>> lCategoryWiseList = (List<Map<String, Object>>) lNotificationsMap[lPtr].get(lBean.getCategory());
						if (lCategoryWiseList == null) {
							lCategoryWiseList = new ArrayList<Map<String, Object>>();
							lNotificationsMap[lPtr].put(lBean.getCategory(), lCategoryWiseList);
							//
							lGrpMap =  new HashMap<>();
							lGrpList = new ArrayList<HashMap<String,Object>>();
							lGrpMap.put(lBean.getGroupDescription(), lGrpList);
							lCategoryWiseList.add(lGrpMap);
							//
							Map<String, Object> lSettingMap = new HashMap<String, Object>();
							lSettingMap.put("cat", lBean.getCategory());
							lSettingMap.put("list", lCategoryWiseList);
							lSettingMap.put("idx", Long.valueOf(notificationSettings[lPtr].size()));
							//
							notificationSettings[lPtr].add(lSettingMap);
							//
						}else{
							lGrpList = null;
							lGrpMap = null;
							for(Map<String, Object> lTmpGroupMap : lCategoryWiseList){
								if(lTmpGroupMap.containsKey(lBean.getGroupDescription())){
									lGrpMap = lTmpGroupMap;
									lGrpList = (List<HashMap<String, Object>>) lTmpGroupMap.get(lBean.getGroupDescription());
									if (lGrpList!=null){
										break;
									}
								}
							}
							if (lGrpMap==null){
								lGrpMap = new HashMap<>();
								lGrpList = new ArrayList<>();
								lCategoryWiseList.add(lGrpMap);
								lGrpMap.put(lBean.getDescription(),lGrpList);
							}
						}
						//
						HashMap<String, Object> lMap = new HashMap<String, Object>();
						lMap.put("notificationType", lBean.getNotificationType());
						lMap.put("description", lBean.getDescription());
						lMap.put("mailerInfo", lBean.getMailerInfo());
						lMap.put("implicit", Boolean.FALSE);								
						lMap.put("explicit", Boolean.FALSE);							
						if (lBean.getMalierType() != null){
							if (MailerType.Implicit.equals(lBean.getMalierType()) || MailerType.Both.equals(lBean.getMalierType()))
								lMap.put("implicit", Boolean.TRUE);								
							if (MailerType.Explicit.equals(lBean.getMalierType()) || MailerType.Both.equals(lBean.getMalierType()))
								lMap.put("explicit", Boolean.TRUE);							
						}
						if(!categoryWiseRolesHash.containsKey(lBean.getNotificationType())){
							categoryWiseRolesHash.put(lBean.getNotificationType(), lBean.getRoleIdList());
						}
						lMap.put("extraSettings", lBean.getExtraSettingDesc());
						lMap.put("mandatory", CommonAppConstants.Yes.Yes.equals(lBean.getMandatory())?true:false);
						
						//
						lGrpList.add(lMap);
					}
				}
			}
		} catch (Exception lException) {
			logger.error("Error while loading notification master", lException.getMessage());
		}
	}

	private void loadOtpNotificationSettings(Connection pConnection) {
		try {
            GenericDAO<OtpNotificationMasterBean> lNotificationMasterDAO = new GenericDAO<OtpNotificationMasterBean>(OtpNotificationMasterBean.class);
            List<OtpNotificationMasterBean> lList = lNotificationMasterDAO.findListFromSql(pConnection, 
                    "SELECT * FROM OtpNotificationMaster WHERE ONMRecordVersion > 0 ORDER BY ONMSequence", -1);
            otpNotificationSettings = new List[]{new ArrayList<Map<String,Object>>(), new ArrayList<Map<String,Object>>(), new ArrayList<Map<String,Object>>()};
            Map<String,Object>[] lNotificationsMap = new Map[]{new HashMap<String,Object>(), new HashMap<String,Object>(), new HashMap<String,Object>()}; // key category, value=list of map
																					// category,
																					// value=list
			// of map
			for (OtpNotificationMasterBean lBean : lList) {
                CommonAppConstants.YesNo[] lEntityList = new CommonAppConstants.YesNo[]{lBean.getSupplier(), lBean.getPurchaser(), lBean.getFinancier()};
				for (int lPtr = 0; lPtr < lEntityList.length; lPtr++) {
					if (lEntityList[lPtr] == CommonAppConstants.YesNo.Yes) {
						List<Map<String, Object>> lCategoryWiseList = (List<Map<String, Object>>) lNotificationsMap[lPtr].get(lBean.getCategory());
						if (lCategoryWiseList == null) {
							lCategoryWiseList = new ArrayList<Map<String, Object>>();
							lNotificationsMap[lPtr].put(lBean.getCategory(), lCategoryWiseList);
							Map<String, Object> lSettingMap = new HashMap<String, Object>();
							lSettingMap.put("cat", lBean.getCategory());
							lSettingMap.put("list", lCategoryWiseList);
							lSettingMap.put("idx", Long.valueOf(otpNotificationSettings[lPtr].size()));
							otpNotificationSettings[lPtr].add(lSettingMap);
						}
						Map<String, Object> lMap = new HashMap<String, Object>();
						lMap.put("notificationType", lBean.getNotificationType());
						lMap.put("description", lBean.getDescription());
						lMap.put("messageInfo", lBean.getMessageInfo());
						lMap.put("smsImplicit", Boolean.FALSE);								
						lMap.put("smsExplicit", Boolean.FALSE);							
						if (lBean.getSmsMessageType() != null){
							if (MessageType.Implicit.equals(lBean.getSmsMessageType()) || MessageType.Both.equals(lBean.getSmsMessageType()))
								lMap.put("smsImplicit", Boolean.TRUE);								
							if (MessageType.Explicit.equals(lBean.getSmsMessageType()) || MessageType.Both.equals(lBean.getSmsMessageType()))
								lMap.put("smsExplicit", Boolean.TRUE);							
						}
						lMap.put("emailImplicit", Boolean.FALSE);								
						lMap.put("emailExplicit", Boolean.FALSE);							
						if (lBean.getSmsMessageType() != null){
							if (MessageType.Implicit.equals(lBean.getEmailMessageType()) || MessageType.Both.equals(lBean.getEmailMessageType()))
								lMap.put("emailImplicit", Boolean.TRUE);								
							if (MessageType.Explicit.equals(lBean.getEmailMessageType()) || MessageType.Both.equals(lBean.getEmailMessageType()))
								lMap.put("emailExplicit", Boolean.TRUE);							
						}
						lCategoryWiseList.add(lMap);
					}
				}
			}
		} catch (Exception lException) {
			logger.error("Error while loading otpnotification master", lException);
		}
	}
	
	private void loadAlertSettings(Connection pConnection) {
		try {
            GenericDAO<AlertMasterBean> lAlertMasterDAO = new GenericDAO<AlertMasterBean>(AlertMasterBean.class);
            List<AlertMasterBean> lList = lAlertMasterDAO.findListFromSql(pConnection, 
                    "SELECT * FROM AlertMaster WHERE AMRecordVersion > 0 ORDER BY AMSequence", -1);
            alertSettings = new ArrayList<Object> ();
            Map<String,Object> lSetting = new HashMap<>();
            int lCounter = 0;
			for (AlertMasterBean lBean : lList) {
				if (!lSetting.containsKey(lBean.getCategory())) {
					lSetting = new HashMap<>();
					lSetting.put("key", ++lCounter);
					lSetting.put("type",lBean.getCategory());
					lSetting.put(lBean.getCategory(), lBean.getCategory());
					lSetting.put("data", new ArrayList<Map<String, Object>>());
					alertSettings.add(lSetting);
				}
				List<Map<String, Object>> lSettingList = (List<Map<String, Object>>) lSetting.get("data");
				Map<String, Object> lMap = new HashMap<String, Object>();
				lMap.put("notificationType", lBean.getNotificationType());
				lMap.put("description", lBean.getDescription());
				lMap.put("sms", Boolean.FALSE);								
				if (lBean.getSmsMessageType() != null){
					lMap.put("sms", Boolean.TRUE);							
				}
				lMap.put("email", Boolean.FALSE);								
				if (lBean.getSmsMessageType() != null){
					lMap.put("email", Boolean.TRUE);								
				}
				lSettingList.add(lMap);
			}
		} catch (Exception lException) {
			logger.error("Error while loading alert master", lException);
		}
	}

	private void loadDesignation() {
		try {
			Map<String, List<Map<String, String>>> lConstituentWiseDesignationJson = new HashMap<String, List<Map<String, String>>>();
			ArrayList<RefCodeValuesBean> lDesignations = RefMasterHelper.getInstance().getRefCodeValues(REFCODE_DESIGNATION);
			for (RefCodeValuesBean lDesignationBean : lDesignations) {
				String[] lSplits = CommonUtilities.splitString(lDesignationBean.getValue(), SEPERATOR);
				List<Map<String, String>> lDesignationJsonsList = lConstituentWiseDesignationJson.get(lSplits[0]);
				if (lDesignationJsonsList == null) {
					lDesignationJsonsList = new ArrayList<Map<String, String>>();
					lConstituentWiseDesignationJson.put(lSplits[0], lDesignationJsonsList);
				}
				Map<String, String> lSubSegmentJson = new HashMap<String, String>();
				lSubSegmentJson.put(BeanFieldMeta.JSONKEY_VALUE, lSplits[1]);
				lSubSegmentJson.put(BeanFieldMeta.JSONKEY_TEXT, lDesignationBean.getDesc());
				lDesignationJsonsList.add(lSubSegmentJson);
			}
			designationJsonMap = new HashMap<String, String>();
			for (String lKey : lConstituentWiseDesignationJson.keySet()) {
				designationJsonMap.put(lKey, new JsonBuilder(lConstituentWiseDesignationJson.get(lKey)).toString());
				logger.info(lKey + "\n\t" + designationJsonMap.get(lKey));
			}

		} catch (Exception lException) {
			logger.error("Error while loading designation", lException);
		}
	}

	public List<Map<String, Object>> getKycDocument(String pConstitution, String pEntityType) {
		return kycDocumentMap.get(pConstitution + SEPERATOR + pEntityType);
	}

	public int getDocumentIndex(String pConstitution, String pEntityType, CompanyKYCDocumentBean pCompanyKYCDocumentBean) {
		Map<String, Integer> lIndexMap = documentTypeDocumentWiseIndex.get(pConstitution + SEPERATOR + pEntityType);
		if (lIndexMap != null) {
            String lKey = pCompanyKYCDocumentBean.getDocumentType() + SEPERATOR + pCompanyKYCDocumentBean.getDocumentCat() + SEPERATOR + pCompanyKYCDocumentBean.getDocument();
			Integer lIndex = lIndexMap.get(lKey);
			if (lIndex != null)
				return lIndex.intValue();
		}
		return -1;
	}

	public String getDesignationJson(String pConstitution) {
		return designationJsonMap.get(pConstitution);
	}

	public String getIndustryJson() {
		return industryJson;
	}

	public String getSubSegmentJson(String pIndustryCode) {
		return subSegmentJsonMap.get(pIndustryCode);
	}
	
	public String getRatingTypeJson(String pRatingType) {
		return ratingJsonMap.get(pRatingType);
	}


	
	public RefCodeValuesBean getIndustryBean(String pIndustryCode) {
		try {
			return RefMasterHelper.getInstance().getRefCodeValuesBean(pIndustryCode, REFCODE_INDUSTRY);
		} catch (Exception lException) {
			logger.error("Error in getIndustryBean", lException);
			return null;
		}
	}

	public RefCodeValuesBean getSubSegmentBean(String pIndustryCode, String pSubSegmentCode) {
		try {
			String lKey = pIndustryCode + SEPERATOR + pSubSegmentCode;
			return RefMasterHelper.getInstance().getRefCodeValuesBean(lKey, REFCODE_SUBSEGMENT);
		} catch (Exception lException) {
			logger.error("Error in getSubSegmentBean", lException);
			return null;
		}
	}

	public RefCodeValuesBean getSectorBean(String pIndustryCode, String pSubSegmentCode, String pSectorCode) {
		try {
			String lKey = pIndustryCode + SEPERATOR + pSubSegmentCode + SEPERATOR + pSectorCode;
			return RefMasterHelper.getInstance().getRefCodeValuesBean(lKey, REFCODE_INDUSTRY);
		} catch (Exception lException) {
			logger.error("Error in getSectorBean", lException);
			return null;
		}
	}

	public List<Map<String, Object>> getNotificationSettings(String pEntityCode) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		AppEntityBean lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pEntityCode });
		if (lAppEntityBean == null)
			throw new CommonBusinessException("Entity not found " + pEntityCode);
		if (lAppEntityBean.isSupplier())
			return notificationSettings[0];
		else if (lAppEntityBean.isPurchaser())
			return notificationSettings[1];
		else if (lAppEntityBean.isFinancier())
			return notificationSettings[2];
		else if (lAppEntityBean.isRegistringEntity())
			return notificationSettings[3];
		else
			throw new CommonBusinessException("Invalid entity for notifications " + pEntityCode);
	}

	public List<Map<String, Object>> getOtpNotificationSettings(String pEntityCode) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		AppEntityBean lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pEntityCode });
		if (lAppEntityBean == null)
			throw new CommonBusinessException("Entity not found " + pEntityCode);
		if (lAppEntityBean.isSupplier())
			return otpNotificationSettings[0];
		else if (lAppEntityBean.isPurchaser())
			return otpNotificationSettings[1];
		else if (lAppEntityBean.isFinancier())
			return otpNotificationSettings[2];
		else if (lAppEntityBean.isRegistringEntity())
			return otpNotificationSettings[3];
		else
			throw new CommonBusinessException("Invalid entity for notifications " + pEntityCode);
	}
	
	public ArrayList<Object> getAlertSettings() throws Exception {
		return alertSettings;
	}


	public Date getPreviousDate(Date pDate, int pCount) throws Exception {
		return getDate(pDate, true, pCount);
	}

	public Date getNextDate(Date pDate, int pCount) throws Exception {
		return getDate(pDate, false, pCount);
	}

	public Date getPreviousTradingDate(Date pDate, int pCount) throws Exception {
		return getWorkingDate(pDate, true, true, pCount);
	}

	public Date getNextTradingDate(Date pDate, int pCount) throws Exception {
		return getWorkingDate(pDate, false, true, pCount);
	}

	public Date getPreviousClearingDate(Date pDate, int pCount) throws Exception {
		return getWorkingDate(pDate, true, false, pCount);
	}

	public Date getNextClearingDate(Date pDate, int pCount) throws Exception {
		return getWorkingDate(pDate, false, false, pCount);
	}

	public Date getCurrentDate() {
		Calendar lCalendar = Calendar.getInstance();
		lCalendar.setTimeInMillis(System.currentTimeMillis());
		lCalendar.set(Calendar.HOUR_OF_DAY, 0);
		lCalendar.set(Calendar.MINUTE, 0);
		lCalendar.set(Calendar.SECOND, 0);
		lCalendar.set(Calendar.MILLISECOND, 0);
		return new Date(lCalendar.getTimeInMillis());
	}

	public long getCurrentTimeUTC() {
		return System.currentTimeMillis() - getCurrentDate().getTime() - UTC_OFFSET;
	}

	public long getDiffInDays(java.util.Date pDate1, java.util.Date pDate2) {
		return (pDate1.getTime() - pDate2.getTime()) / 86400000;
	}

	public Date addDaysToDate(Date pDate, int pDays) {
		Calendar lCalendar = Calendar.getInstance();
		lCalendar.setTime(pDate);
		lCalendar.add(Calendar.DATE, pDays);
		return new Date(lCalendar.getTimeInMillis());
	}

	private Date getDate(Date pDate, boolean pPrevious, int pCount) throws Exception {
		Calendar lCalendar = Calendar.getInstance();
		Date lDate = pDate;
		lCalendar.setTime(lDate);
		lCalendar.set(Calendar.HOUR_OF_DAY, 0);
		lCalendar.set(Calendar.MINUTE, 0);
		lCalendar.set(Calendar.SECOND, 0);
		lCalendar.set(Calendar.MILLISECOND, 0);
		lCalendar.add(Calendar.DATE, pPrevious ? -1 * pCount : pCount);
		return new Date(lCalendar.getTimeInMillis());
	}

	public Date getWorkingDate(Date pDate, boolean pPrevious, boolean pTrading, int pCount) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(HolidayMasterBean.ENTITY_NAME);
		Calendar lCalendar = Calendar.getInstance();
		Date lDate = pDate;
		lCalendar.setTime(lDate);
		lCalendar.set(Calendar.HOUR_OF_DAY, 0);
		lCalendar.set(Calendar.MINUTE, 0);
		lCalendar.set(Calendar.SECOND, 0);
		lCalendar.set(Calendar.MILLISECOND, 0);
		Date[] lFilter = new Date[] { lDate };
        String lWeekDays = RegistryHelper.getInstance().getString(pTrading?AppConstants.REGISTRY_TRADINGWEEKDAYS:AppConstants.REGISTRY_CLEARINGWEEKDAYS);
		int lDelta = pPrevious ? -1 : 1;
		int lCounter = 0;
		for (int lPtr = 0; lPtr < 100; lPtr++) { // loop limit to avoid infinite looping
			if (lWeekDays.charAt(lCalendar.get(Calendar.DAY_OF_WEEK) - 1) == 'Y') { 
				HolidayMasterBean lHolidayMasterBean = (HolidayMasterBean) lMemoryTable.selectSingleRow(HolidayMasterBean.f_Date, lFilter);
				if ((lHolidayMasterBean == null)
						|| ((lHolidayMasterBean.getType() == HolidayMasterBean.Type.Trading) && !pTrading)
						|| ((lHolidayMasterBean.getType() == HolidayMasterBean.Type.Clearing) && pTrading)) {
					lCounter++;
					if (lCounter > pCount)
						break;
				}
			}
			// change date
			lCalendar.add(Calendar.DATE, lDelta);
			lDate = new Date(lCalendar.getTimeInMillis());
			lFilter[0] = lDate;
		}
		return lDate;
	}

	public AuctionCalendarBean getAuctionCalendarBean(String pType) throws Exception {
		return getAuctionCalendarBean(pType, AuctionCalendarBean.AuctionDay.Today);
	}

    public AuctionCalendarBean getAuctionCalendarBean(String pType, AuctionCalendarBean.AuctionDay pAuctionDay) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AuctionCalendarBean.ENTITY_NAME);
        return (AuctionCalendarBean)lMemoryTable.selectSingleRow(AuctionCalendarBean.f_TypeAuctionDay, new Object[]{pType, pAuctionDay});
	}

	public ConfirmationWindowBean getConfirmationWindowBean(String pType) throws Exception {
		AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType);
		if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
			for (ConfirmationWindowBean lConfirmationWindowBean : lAuctionCalendarBean.getConfWinList()) {
				if (lConfirmationWindowBean.getStatus() == ConfirmationWindowBean.Status.Open)
					return lConfirmationWindowBean;
			}
		}
		return null;
	}

	public boolean isFirstConfirmationWindowBean(String pType, ConfirmationWindowBean pConfirmationWindowBean) throws Exception {
		if(pConfirmationWindowBean==null) return false;
		AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType);
		if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
			for (ConfirmationWindowBean lConfirmationWindowBean : lAuctionCalendarBean.getConfWinList()) {
				if(!lConfirmationWindowBean.equals(pConfirmationWindowBean)){
					if(lConfirmationWindowBean.getConfEndTime().equals(pConfirmationWindowBean.getConfEndTime())){
						if(lConfirmationWindowBean.getConfEndTimeTime().before(pConfirmationWindowBean.getConfEndTimeTime()))
							return false;
					}else if(lConfirmationWindowBean.getConfEndTime().before(pConfirmationWindowBean.getConfEndTime())){
						return false;
					}
				}
			}
		}else{
			return false;
		}
		return true;
	}
	public boolean isSecondConfirmationWindowBean(String pType, ConfirmationWindowBean pConfirmationWindowBean) throws Exception {
		if(pConfirmationWindowBean==null) return false;
		AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType);
		if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
			int lWindowCount = 0;
			for (ConfirmationWindowBean lConfirmationWindowBean : lAuctionCalendarBean.getConfWinList()) {
				lWindowCount++;
				if(lConfirmationWindowBean.getConfStartTime().equals(pConfirmationWindowBean.getConfStartTime()) && 
						lConfirmationWindowBean.getConfEndTime().equals(pConfirmationWindowBean.getConfEndTime())){
					return (lWindowCount == 2);
				}
			}
		}
		return false;
	}

	public ConfirmationWindowBean getCurrentNextConfirmationWindowBean(String pType) throws Exception {
		ConfirmationWindowBean lConfirmationWindowBean = null;
		long lCurrentTime = System.currentTimeMillis();
		for (int lPtr = 0; (lPtr < 2) && (lConfirmationWindowBean == null); lPtr++) {
            AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType, lPtr==0?AuctionCalendarBean.AuctionDay.Today:AuctionCalendarBean.AuctionDay.Tomorrow);
			if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
				for (ConfirmationWindowBean lTempBean : lAuctionCalendarBean.getConfWinList()) {
					if (lCurrentTime <= lTempBean.getConfEndTime().getTime()) {
                        if ((lConfirmationWindowBean == null) || (lConfirmationWindowBean.getConfEndTime().compareTo(lTempBean.getConfEndTime()) > 0))
							lConfirmationWindowBean = lTempBean;
					}
				}
			}
		}
		return lConfirmationWindowBean;
	}

	public ConfirmationWindowBean getFirstConfirmationWindowBean(String pType) throws Exception {
		AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType);
		if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
			for (ConfirmationWindowBean lConfirmationWindowBean : lAuctionCalendarBean.getConfWinList()) {
				return lConfirmationWindowBean;
			}
		}
		return null;
	}

    public ConfirmationWindowBean getCurrentNextConfirmationWindowBeanForToday(String pType) throws Exception {
        ConfirmationWindowBean lConfirmationWindowBean = null;
        long lCurrentTime = System.currentTimeMillis();
        AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType);
        if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
            for (ConfirmationWindowBean lTempBean : lAuctionCalendarBean.getConfWinList()) {
                if ((lConfirmationWindowBean == null) || (lConfirmationWindowBean.getStatus() == ConfirmationWindowBean.Status.Closed)
                        || (lTempBean.getStatus() == ConfirmationWindowBean.Status.Open))
                    lConfirmationWindowBean = lTempBean;
            }
        }
        return lConfirmationWindowBean;
    }

	public boolean isConfirmationWindowPending(String pType) throws Exception {
		long lCurrentTime = System.currentTimeMillis();
        AuctionCalendarBean lAuctionCalendarBean = getAuctionCalendarBean(pType, AuctionCalendarBean.AuctionDay.Today);
		if (lAuctionCalendarBean != null && lAuctionCalendarBean.getConfWinList() != null) {
			for (ConfirmationWindowBean lTempBean : lAuctionCalendarBean.getConfWinList()) {
				if (lCurrentTime <= lTempBean.getConfEndTime().getTime()) {
					return true;
				}
			}
		}
		return false;
	}
	
    public boolean isNotificationEnabled(Connection pConnection, String pEntityCode, String pNotificationType) {
        EntityNotificationSettingBean lBean = null;
        try {
            lBean = getEntityNotificationSettings(pConnection, pEntityCode, pNotificationType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lBean != null) {
            return (!MailerType.None.equals(lBean.getMailerType()));
        }
        return true;
    }

	public List<String> getEmailIdsFromNotificationSettings(Connection pConnection, String pEntityCode, String pNotificationType) throws Exception {
	    EntityNotificationSettingBean lEntityNotificationSettingBean = getEntityNotificationSettings(pConnection, pEntityCode, pNotificationType);
	    if ((lEntityNotificationSettingBean != null && lEntityNotificationSettingBean.notifyExplicitMailIds() )) {
	        List<String> lEmailIds = lEntityNotificationSettingBean.getEmailList();
	        if ((lEmailIds != null) && (lEmailIds.size() > 0))
	            return lEmailIds;
	    }
	    return null;
	}
	
    public EntityNotificationSettingBean getEntityNotificationSettings(Connection pConnection, String pEntityCode,
           String pNotificationType) throws Exception {

    	if(StringUtils.isEmpty(pNotificationType)){
    		return null;
    	}
    	if(StringUtils.isEmpty(pEntityCode)){
    		return null;
    	}		
    	EntityNotificationSettingBean lBean = null;
/*		if (lAppUserBean.getType() != AppUserBean.Type.Admin)
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);*/
		EntityNotificationSettingBean lFilterBean = new EntityNotificationSettingBean();
		lFilterBean.setCode(pEntityCode);
		lFilterBean.setNotificationType(pNotificationType);

		List<EntityNotificationSettingBean> lEntityNotificationSettingList = entityNotificationSettingDAO.findList(pConnection, lFilterBean, (String) null);

		if (lEntityNotificationSettingList != null && lEntityNotificationSettingList.size() > 0) {
			lBean = lEntityNotificationSettingList.get(0);
		}
		return lBean;
	}

    public boolean isOtpImplicitEmailNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getEmailMessageType()!=null){
            return (MessageType.Both.equals(pBean.getEmailMessageType()) || MessageType.Implicit.equals(pBean.getEmailMessageType()));
    	}
    	return false;
    }
    public boolean isOtpExplicitEmailNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getEmailMessageType()!=null){
            return (MessageType.Both.equals(pBean.getEmailMessageType()) || MessageType.Explicit.equals(pBean.getEmailMessageType()));
    	}
    	return false;
    }
    public boolean isOtpEmailNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getEmailMessageType()!=null){
            return (!MessageType.None.equals(pBean.getEmailMessageType()));
    	}
    	return false;
    }
    public boolean isOtpImplicitSmsNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getEmailMessageType()!=null){
            return (MessageType.Both.equals(pBean.getEmailMessageType()) || MessageType.Implicit.equals(pBean.getEmailMessageType()));
    	}
    	return false;
    }
    public boolean isOtpExplicitSmsNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getSmsMessageType()!=null){
            return (MessageType.Both.equals(pBean.getSmsMessageType()) || MessageType.Explicit.equals(pBean.getSmsMessageType()));
    	}
    	return false;
    }
    public boolean isOtpSmsNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null && pBean.getSmsMessageType()!=null){
            return (!MessageType.None.equals(pBean.getSmsMessageType()));
    	}
    	return false;
    }
    public boolean isOtpNotificationEnabled(EntityOtpNotificationSettingBean pBean){
    	if(pBean != null){
            boolean lSendEmail = false;
            if(pBean.getEmailMessageType()!=null){
            	lSendEmail = (!MessageType.None.equals(pBean.getEmailMessageType()));
            }
            boolean lSendSms = false;
            if(pBean.getSmsMessageType()!=null){
            	lSendSms = (!MessageType.None.equals(pBean.getSmsMessageType()));
            }
            return (lSendEmail || lSendSms);
    	}
    	return false;
    }

    public EntityOtpNotificationSettingBean getEntityOtpNotificationSettings(Connection pConnection, String pEntityCode,
           String pNotificationType) throws Exception {

		EntityOtpNotificationSettingBean lBean = null;
		EntityOtpNotificationSettingBean lFilterBean = new EntityOtpNotificationSettingBean();
		lFilterBean.setCode(pEntityCode);
		lFilterBean.setNotificationType(pNotificationType);

		List<EntityOtpNotificationSettingBean> lEntityOtpNotificationSettingList = entityOtpNotificationSettingDAO.findList(pConnection, lFilterBean, (String) null);

		if (lEntityOtpNotificationSettingList != null && lEntityOtpNotificationSettingList.size() > 0) {
			lBean = lEntityOtpNotificationSettingList.get(0);
		}
		return lBean;
	}

	public static void main(String pArgs[]) {
		OtherResourceCache.getInstance();
	}
	 
	public Map<String, Object> getCategoryWiseRoles(){
		return categoryWiseRolesHash;
	}

}
