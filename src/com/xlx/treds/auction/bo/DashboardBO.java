package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean.Status;
import com.xlx.treds.instrument.bean.FactoringUnitBidBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class DashboardBO {
	public static Logger logger = Logger.getLogger(DashboardBO.class);

	public static final String TAG_TREDS_SUMMARY = "tredSum";
	public static final String TAG_AUCTION_CALENDAR = "aucCal";
	public static final String TAG_AUCTION_CALENDAR_NEXT = "aucCalNext";
	public static final String TAG_NEWS = "news";
	public static final String TAG_FACTORING_SUMMARY = "factSum";

	public static final String TAG_MY_INSTRUMENTS = "myInst";
	public static final String TAG_CHECKER_INSTRUMENTS = "chkInst";
	public static final String TAG_COUNTER_INSTRUMENTS = "couInst";
	public static final String TAG_INST_SUMM = "instSum";
	public static final String TAG_FU_SUMM = "fuSum";
	public static final String TAG_OBLIG_RECEIVABLES = "obgRecv";
	public static final String TAG_OBLIG_PAYABLES = "obgPay";
	public static final String TAG_FINANCIER_BUYERS = "finBuyers";
	public static final String TAG_FINANCIER_FACTORED = "finFactored";
	public static final String TAG_PURCH_LIMIT = "purchLimit";
	public static final BigDecimal AMT_ONE_LAKH = new BigDecimal(100000);
	public static final int AMT_SCALE = 2;

	private InstrumentBO instrumentBO;
	private GenericDAO<InstrumentBean> instrumentDAO;
	private GenericDAO<ObligationBean> obligationDAO;
	private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
	private CompositeGenericDAO<FactoringUnitBidBean> factoringUnitBidDAO;
	private BeanMeta financierAuctionSettingBeanMeta;

	public DashboardBO() {
		super();
		instrumentBO = new InstrumentBO();
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
		financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
		factoringUnitBidDAO = new CompositeGenericDAO<FactoringUnitBidBean>(FactoringUnitBidBean.class);
		financierAuctionSettingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FinancierAuctionSettingBean.class);
	}

	public String getData(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, List<String> pTagNames)
			throws Exception {
		Map<String, Object> lMap = new HashMap<String, Object>();
		boolean lIsPurchSupp = false;
		AppEntityBean lAppEntityBean = null;
		if (pAppUserBean != null) {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
			lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pAppUserBean.getDomain() });
			lIsPurchSupp = lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier();
		}
		Connection lConnection = pExecutionContext.getConnection();
		if (lAppEntityBean.isSupplier() || lAppEntityBean.isPurchaser())
			lMap.put(TAG_INST_SUMM, getInstSummary(lConnection, pAppUserBean));
		if (lAppEntityBean.isSupplier() || lAppEntityBean.isPurchaser())
			lMap.put(TAG_FU_SUMM, getFuSummary(lConnection, pAppUserBean));
		if (lAppEntityBean.isSupplier() || lAppEntityBean.isFinancier())
			lMap.put(TAG_OBLIG_RECEIVABLES, getObligations(lConnection, pAppUserBean, true));
		if (lAppEntityBean.isPurchaser() || lAppEntityBean.isFinancier())
			lMap.put(TAG_OBLIG_PAYABLES, getObligations(lConnection, pAppUserBean, false));
		if (lAppEntityBean.isPlatform())
			lMap.put(TAG_TREDS_SUMMARY, getTredsSummary(lConnection));
		if (lAppEntityBean.isFinancier())
			lMap.put(TAG_FINANCIER_BUYERS, getFinancierBuyerSummary(lConnection, pAppUserBean));
		if (lAppEntityBean.isPurchaser())
			lMap.put(TAG_PURCH_LIMIT, getPurchaserLimit(lConnection, pAppUserBean));
		AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
		if (lAuctionCalendarBean != null) {
			BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
			lMap.put(TAG_AUCTION_CALENDAR, lBeanMeta.formatAsMap(lAuctionCalendarBean, null, null, true));
			lMap.put("nextBusinessDate",   FormatHelper.getDisplay("dd-MMM-yyyy",OtherResourceCache.getInstance().getNextTradingDate(lAuctionCalendarBean.getDate(), 1)));
		}
		AuctionCalendarBean lNextAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL, AuctionCalendarBean.AuctionDay.Tomorrow);
		if (lNextAuctionCalendarBean != null) {
			BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
			lMap.put(TAG_AUCTION_CALENDAR_NEXT, lBeanMeta.formatAsMap(lNextAuctionCalendarBean, null, null, true));
		}
		if (lAppEntityBean.isFinancier())
			lMap.put(TAG_FINANCIER_FACTORED, getFactored(lConnection, pAppUserBean));
		/*
		 * lMap.put(TAG_NEWS, Boolean.TRUE); lMap.put(TAG_FACTORING_SUMMARY,
		 * Boolean.TRUE);
		 */
		// Regulatory Authority Dashboard data
		if (lAppEntityBean.isPlatform() || lAppEntityBean.isRegulatoryEntity()) {
			Map<String, Set<String>> lCatwiseCatTypes = new HashMap<String, Set<String>>();
			Map<String,Object> lRegData = getEntityCategoryTypesData(lConnection, lCatwiseCatTypes);
			// Registration
			lMap.put("regEntCatTypeData", lRegData);
			lMap.put("regEntCatTypes", getEntityCategoryTypes(lConnection, lCatwiseCatTypes)); // take in variable and remove items not in data

			// Transaction
			Map<String, Object> lTransData = getTransactionData(lConnection,null);
			lMap.put("transTypes", getTransSummaryTypes());
			lMap.put("transData", lTransData);
			lMap.put("transSummaryData", getTransSummaryData(lTransData));
			//
			lMap.put("hasRegData", (lRegData!=null&& lRegData.size()>0));
			lMap.put("hasTransData", (lTransData!=null&&lTransData.size()>0));
			//			
		}
		return new JsonBuilder(lMap).toString();
	}

	public Object getTransSummaryTypes() throws Exception {
		// "catType":"MSMESTATUS",
		// "data":
		// [{"value":"Micro","desc":"Micro"},{"value":"Small","value":"Small"},{"value":"Medium","desc":"Medium"}]
		List<Object> lEntityGroups = null;
		Map<String, Object> lEntityGroup = null;
		Map<String, String> lData = null;
		List<Map<String, String>> lDataList = null;
		//
		lEntityGroups = new ArrayList<Object>();
		lEntityGroup = new HashMap<String, Object>();
		lEntityGroups.add(lEntityGroup);
		
		lEntityGroup.put("entCat", "TRANSSUMMARY");
		lDataList = new ArrayList<Map<String, String>>();
		lEntityGroup.put("catTypes", lDataList);

		//
		lData = new HashMap<String, String>();
		lData.put("value", "invcount");
		lData.put("desc", "Invoice Count");
		lDataList.add(lData);
		lData = new HashMap<String, String>();
		lData.put("value", "invamount");
		lData.put("desc", "Invoice TurnOver");
		lDataList.add(lData);

		// "catType":"MSMESTATUS",
		// "data":
		// [{"value":"Micro","desc":"Micro"},{"value":"Small","value":"Small"},{"value":"Medium","desc":"Medium"}]
		return lEntityGroups;
	}

	public Map<String, Object>  getEntityCategoryTypesData(Connection pConnection, Map<String, Set<String>> pCatwiseCatTypes)
			throws Exception {
		Map<String, Object> lCatwiseRegData = null;
		Map<String, Object> lCatListData = null;
		String[] lCategories = new String[] { "MSMESTATUS", "CONSTITUTION", "FINCATEGORY" };
		String[] lCatColumn = new String[] { "CDMSMESTATUS", "CDCONSTITUTION", "CDFINANCIERCATEGORY" };
		String[] lCatFlag = new String[] { "CDSUPPLIERFLAG", "CDPURCHASERFLAG", "CDFINANCIERFLAG" };
		//
		lCatwiseRegData = new HashMap<String, Object>();
		for (int lPtr = 0; lPtr < lCategories.length; lPtr++) {
			lCatListData = (Map<String, Object>) getRegistrationEntityData(pConnection, lCategories[lPtr], lCatColumn[lPtr], lCatFlag[lPtr], pCatwiseCatTypes);
			lCatwiseRegData.put(lCategories[lPtr], lCatListData);
		}
		return lCatwiseRegData;
	}

	public Object getEntityCategoryTypes(Connection pConnection, Map<String, Set<String>> pCatwiseCatTypes)
			throws Exception {
		List<Object> lEntityGroups = null;
		Map<String, Object> lEntityGroup = null;
		List<String> lEntityGroupCodes = null;
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
		ArrayList<RefCodeValuesBean> lGroupBeans = null;
		Map<String, String> lData = null;
		List<Map<String, String>> lDataList = null;
		boolean lAdd = false;
		Set<String> lCatTypes = null;
		//
		lEntityGroupCodes = new ArrayList<String>();
		lEntityGroupCodes.add(AppConstants.RC_MSMESTATUS); // Seller
		lEntityGroupCodes.add(AppConstants.RC_CONSTITUTION); // Buyer
		lEntityGroupCodes.add(AppConstants.RC_FINANCIERCATEGORY); // Financier
		//
		lEntityGroups = new ArrayList<Object>();
		// "catType":"MSMESTATUS",
		// "data":
		// [{"value":"Micro","desc":"Micro"},{"value":"Small","value":"Small"},{"value":"Medium","desc":"Medium"}]
		for (String lEntityGroupCode : lEntityGroupCodes) {
			lEntityGroup = new HashMap<String, Object>();
			lDataList = new ArrayList<Map<String, String>>();
			//
			lCatTypes = pCatwiseCatTypes.get(lEntityGroupCode);
			//
			lEntityGroups.add(lEntityGroup);
			lEntityGroup.put("entCat", lEntityGroupCode);
			lEntityGroup.put("catTypes", lDataList);
			//
			lGroupBeans = TredsHelper.getInstance().getRefCodeValues(lEntityGroupCode);
			for (RefCodeValuesBean lGrpBean : lGroupBeans) {
				lAdd = ((pCatwiseCatTypes == null) || lCatTypes.contains(lGrpBean.getValue()));
				if (lAdd) {
					lData = new HashMap<String, String>();
					lData.put("value", lGrpBean.getValue());
					lData.put("desc", lGrpBean.getDesc());
					lDataList.add(lData);
				}
			}
		}
		return lEntityGroups;
	}

	public Object getRegistrationEntityData(Connection pConnection, String pCategoryKey, String pCategoryColumnName,
			String pEntityFlagColumnName, Map<String, Set<String>> pCatwiseValidCatTypes) throws Exception {
		StringBuilder lSql = new StringBuilder();
		Date lCurrDate = null;
		Date lCurrMonthStartDate = null, lCurrMonthEndDate = null, lPrevMonthStartDate = null, lPrevMonthEndDate = null;
		Date lFYStartDate = null, lFYEndDate = null;
		DBHelper lDbHelper = DBHelper.getInstance();
		Set<String> lCatTypeKeys = null;
		//
		if (pCatwiseValidCatTypes == null)
			pCatwiseValidCatTypes = new HashMap<String, Set<String>>();
		if (!pCatwiseValidCatTypes.containsKey(pCategoryKey)) {
			lCatTypeKeys = new HashSet<String>();
			pCatwiseValidCatTypes.put(pCategoryKey, lCatTypeKeys);
		}
		else {
			lCatTypeKeys = pCatwiseValidCatTypes.get(pCategoryKey);
		}
		//
		lCurrDate = TredsHelper.getInstance().getBusinessDate();
		lCurrMonthStartDate = CommonUtilities.getMonthFirstDate(lCurrDate);
		lCurrMonthEndDate = CommonUtilities.getMonthLastDate(lCurrDate);
		lPrevMonthStartDate = CommonUtilities.getPrevMonthFirstDate(lCurrDate);
		lPrevMonthEndDate = CommonUtilities.getPrevMonthLastDate(lCurrDate);

		Date[] lTmpDates = TredsHelper.getInstance().getFinYearDates(lCurrDate);
		lFYStartDate = lTmpDates[0];
		lFYEndDate = lTmpDates[1];
		//
		lDbHelper.formatDate(lCurrDate);
		lSql.append(" SELECT ");
		lSql.append(pCategoryColumnName).append(" CategoryType ");
		lSql.append(" ,SUM(CASE WHEN (to_date(CDRECORDCREATETIME) = ").append(lDbHelper.formatDate(lCurrDate)).append(") THEN RegCount ELSE 0 END) CurrRegCount ");
		lSql.append(" ,SUM(CASE WHEN (to_date(CDRECORDCREATETIME) BETWEEN ").append(lDbHelper.formatDate(lCurrMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lCurrMonthEndDate)).append(") THEN RegCount ELSE 0 END) CurrMthRegCount ");
		lSql.append(" ,SUM(CASE WHEN (to_date(CDRECORDCREATETIME) BETWEEN ").append(lDbHelper.formatDate(lPrevMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lPrevMonthEndDate)).append(") THEN RegCount ELSE 0 END) PrevMthRegCount ");
		lSql.append(" ,SUM(CASE WHEN (to_date(CDRECORDCREATETIME) BETWEEN ").append(lDbHelper.formatDate(lFYStartDate)).append(" AND ").append(lDbHelper.formatDate(lFYEndDate)).append(") THEN RegCount ELSE 0 END) FYRegCount ");
		lSql.append(" ,SUM(RegCount) SIRegCount ");
		lSql.append(" FROM ( ");
		lSql.append(" SELECT ");
		lSql.append(pCategoryColumnName); // CDCONSTITUTION
		lSql.append(" , CDRECORDCREATETIME ");
		lSql.append(" , count(*) RegCount ");
		lSql.append(" FROM COMPANYDETAILS ");
		lSql.append(" WHERE ").append(pEntityFlagColumnName).append(" = 'Y' "); // CDPURCHASERFLAG
		lSql.append(" GROUP BY ").append(pCategoryColumnName).append(" , CDRECORDCREATETIME ");
		lSql.append(" ) ");
		lSql.append(" GROUP BY ").append(pCategoryColumnName);// CDCONSTITUTION
		//
		Statement lStatement = null;
		ResultSet lResultSet = null;
		String lCatType = null;
		Map<String, Object> lCTwiseData = null, lData = null; // key="Category"
																// ,value="data in hash"
		// data
		// { "Micro" : {"cur":1, "week":12, "curmonth":2 },"TOTAL" : {"cur":3,
		// "week":36, "curmonth":42 } }
		Long lTmpCount = null, lTotal = null;
		String[] lColName = new String[] { "CurrRegCount", "CurrMthRegCount", "PrevMthRegCount", "FYRegCount",
				"SIRegCount" };
		String[] lKeyName = new String[] { "cur", "curmth", "prevmth", "fy", "si" };
		long[] lTotals = new long[] { 0, 0, 0, 0, 0 };
		try {
			lStatement = pConnection.createStatement();
			if (logger.isDebugEnabled())
				logger.debug(lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			lCTwiseData = new HashMap<String, Object>();
			while (lResultSet.next()) {
				lCatType = lResultSet.getString("CategoryType");
				//
				if (!lCatTypeKeys.contains(lCatType))
					lCatTypeKeys.add(lCatType);
				//
				lData = new HashMap<String, Object>();
				lCTwiseData.put(lCatType, lData);

				for (int lPtr = 0; lPtr < lColName.length; lPtr++) {
					lTmpCount = lResultSet.getLong(lColName[lPtr]);
					lTotal = lTotals[lPtr];
					if (lTmpCount != null)
						lTotal += lTmpCount;
					lTotals[lPtr] = lTotal;
					lData.put(lKeyName[lPtr], lTmpCount);
				}
			}
			//
			lData = new HashMap<String, Object>();
			lCTwiseData.put("TOTAL", lData);
			//
			for (int lPtr = 0; lPtr < lColName.length; lPtr++) {
				lData.put(lKeyName[lPtr], new Long(lTotals[lPtr]));
			}
		} catch (Exception lEx) {
			logger.info(lEx.getMessage());
		}
		finally {
			if (lResultSet != null)
				lResultSet.close();
			if (lStatement != null)
				lStatement.close();
		}
		//
		return lCTwiseData;
	}

	private HashMap<String, String[]> getEntityCategories(Connection pConnection) throws SQLException {
		// Key=EntityCode , Value=[0]=Category [1]=SubCategory
		HashMap<String, String[]> lData = new HashMap<String, String[]>();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT CDCODE EntityCode ");
		lSql.append(" , (CASE  CONCAT(CONCAT(CDSUPPLIERFLAG, CDPURCHASERFLAG),CDFINANCIERFLAG) ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Supplier.getCode())).append(" THEN ").append(DBHelper.getInstance().formatString("MSMESTATUS")).append(" ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Purchaser.getCode())).append(" THEN ").append(DBHelper.getInstance().formatString("CONSTITUTION")).append(" ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode())).append(" THEN ").append(DBHelper.getInstance().formatString("FINCATEGORY")).append(" ");
		lSql.append(" 	ELSE ").append(DBHelper.getInstance().formatString(" ")).append(" END ) Category ");
		lSql.append(" 	, (CASE  CONCAT(CONCAT(CDSUPPLIERFLAG, CDPURCHASERFLAG),CDFINANCIERFLAG)  ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Supplier.getCode())).append(" THEN CDMSMESTATUS  ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Purchaser.getCode())).append(" THEN CDCONSTITUTION  ");
		lSql.append(" 	WHEN ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode())).append(" THEN CDFINANCIERCATEGORY  ");
		lSql.append(" 	ELSE ").append(DBHelper.getInstance().formatString(" ")).append(" END ) SubCategory ");
		lSql.append(" 	FROM COMPANYDETAILS WHERE CDRECORDVERSION>0");
		//
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
			if (logger.isDebugEnabled())
				logger.debug(lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			String lEntityCode, lCategory, lSubCategory;
			while (lResultSet.next()) {
				lEntityCode = lResultSet.getString("EntityCode");
				lCategory = lResultSet.getString("Category");
				lSubCategory = lResultSet.getString("SubCategory");
				lData.put(lEntityCode, new String[] { lCategory, lSubCategory });
			}
			//
		} catch (Exception lEx) {
			logger.info(lEx.getMessage());
		}
		finally {
			if (lResultSet != null)
				lResultSet.close();
			if (lStatement != null)
				lStatement.close();
		}
		//
		return lData;
	}

	public Object getTransSummaryData(Map<String, Object> pTransData) throws Exception {
		Map<String, Object> lData = new HashMap<String, Object>();
		HashMap<String, Object> lSubCat = new HashMap<String, Object>();
		HashMap<String, Object> lSubCatValues0 = null;
		HashMap<String, Object> lSubCatValues1 = null;
		String[] lValKeys = new String[] { "cur", "curmth", "prevmth", "fy", "si" };
		
		lData.put("TRANSSUMMARY", lSubCat);
		if (pTransData != null) {
			Map<String, Object> lTotalHash = (Map<String,Object>) pTransData.get("GRANDTOTAL");
			//
			lSubCatValues0 = new HashMap<String, Object>();
			lSubCatValues1 = new HashMap<String, Object>();
			for(String lKey : lTotalHash.keySet()){
				lSubCatValues0.put(lKey, ((Object[])lTotalHash.get(lKey))[0]);
				lSubCatValues1.put(lKey, ((Object[])lTotalHash.get(lKey))[1]);
			}
			//
			lSubCat.put("invcount", lSubCatValues0);
			lSubCat.put("invamount", lSubCatValues1);
		}
		
		return lData;
	}

	public Map<String, Object> getTransactionData(Connection pConnection,String pEntityCode) throws Exception {
		Map<String, Object> lData = new HashMap<String, Object>(); // Key1=Category,
																	// Value=Map
																	// ,
																	// Key2=CategoryType
																	// Value2=[TotalInv,
																	// TotalAmt]
		HashMap<String, String[]> lEntityCategories = getEntityCategories(pConnection);
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		Date lCurrDate = null;
		Date lCurrMonthStartDate = null, lCurrMonthEndDate = null, lPrevMonthStartDate = null, lPrevMonthEndDate = null, lFYStartDate = null, lFYEndDate = null;

		lCurrDate = TredsHelper.getInstance().getBusinessDate();
		lCurrMonthStartDate = CommonUtilities.getMonthFirstDate(lCurrDate);
		lCurrMonthEndDate = CommonUtilities.getMonthLastDate(lCurrDate);
		lPrevMonthStartDate = CommonUtilities.getPrevMonthFirstDate(lCurrDate);
		lPrevMonthEndDate = CommonUtilities.getPrevMonthLastDate(lCurrDate);
		Date[] lFYDates = TredsHelper.getInstance().getFinYearDates(lCurrDate);
		lFYStartDate = lFYDates[0];
		lFYEndDate = lFYDates[1];
		//
		lSql.append(" SELECT FUSUPPLIER, FUPURCHASER, FUFINANCIER ");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) = ").append(lDbHelper.formatDate(lCurrDate)).append(" ) THEN InvCount ELSE 0 END) CurrDateInvCount");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) = ").append(lDbHelper.formatDate(lCurrDate)).append(") THEN FactAmount ELSE 0 END) CurrDateFactAmt");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lCurrMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lCurrMonthEndDate)).append(") THEN InvCount ELSE 0 END) CurrMthInvCount");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lCurrMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lCurrMonthEndDate)).append(") THEN FactAmount ELSE 0 END) CurrMthFactAmt");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lPrevMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lPrevMonthEndDate)).append(") THEN InvCount ELSE 0 END) PrevMthInvCount");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lPrevMonthStartDate)).append(" AND ").append(lDbHelper.formatDate(lPrevMonthEndDate)).append(") THEN FactAmount ELSE 0 END) PrevMthFactAmt");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lFYStartDate)).append(" AND ").append(lDbHelper.formatDate(lFYEndDate)).append(") THEN InvCount ELSE 0 END) FYInvCount");
		lSql.append(" ,SUM(CASE WHEN (to_date(FUACCEPTDATETIME) BETWEEN ").append(lDbHelper.formatDate(lFYStartDate)).append(" AND ").append(lDbHelper.formatDate(lFYEndDate)).append(") THEN FactAmount ELSE 0 END) FYFactAmt");
		lSql.append(" ,SUM(InvCount) TillDateInvCount");
		lSql.append(" ,SUM(FactAmount) TillDateFYFactAmt");
		lSql.append(" FROM (");
		lSql.append(" SELECT FUSUPPLIER, FUPURCHASER, FUFINANCIER , FUACCEPTDATETIME, FUID , FactAmount ,to_date(FUACCEPTDATETIME) ,nvl(groupcount, 1) InvCount from ( ");
		lSql.append(" SELECT FUSUPPLIER, FUPURCHASER, FUFINANCIER");
		lSql.append(" , FUACCEPTDATETIME, FUID ");
		lSql.append(" , inid,INgroupFlag,ingroupinid ,FUFACTOREDAMOUNT FactAmount ");
		lSql.append(" ,TO_DATE(FUACCEPTDATETIME)");
		lSql.append(" FROM FACTORINGUNITS, INSTRUMENTS");
		lSql.append(" WHERE FURECORDVERSION > 0");
		lSql.append(" AND INRECORDVERSION > 0");
		lSql.append(" AND INFUID = FUID");
		lSql.append(" AND FUACCEPTDATETIME IS NOT NULL ");
		lSql.append(" AND FUSTATUS NOT IN ( ").append(lDbHelper.formatString(Status.Expired.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(Status.Leg_1_Failed.getCode())).append(" ) ");
		if (StringUtils.isNotEmpty(pEntityCode)){
			lSql.append(" AND FUFINANCIER = ").append(lDbHelper.formatString(pEntityCode));
		}
		lSql.append(" ) factInst LEFT OUTER JOIN ");
		lSql.append(" ( SELECT ingroupinid , count(*) groupcount FROM INSTRUMENTS WHERE INRECORDVERSION > 0 ");
		lSql.append(" AND INGROUPFLAG IS NULL AND INGROUPINID IS NOT NULL ");
		lSql.append(" GROUP BY INGROUPINID ) grpinst ON (factinst.inid = grpinst.ingroupinid ) ");
		lSql.append(" UNION");
		lSql.append(" SELECT FUSUPPLIER, FUPURCHASER, FUFINANCIER , FUACCEPTDATETIME, FUID , FactAmount ,to_date(FUACCEPTDATETIME) ,nvl(groupcount, 1) InvCount from ( ");
		lSql.append("  SELECT FUSUPPLIER, FUPURCHASER, FUFINANCIER ");
		lSql.append(" , FUACCEPTDATETIME, FUID ");
		lSql.append(" ,inid,INgroupFlag,ingroupinid ,FUFACTOREDAMOUNT FactAmount ");
		lSql.append(" ,TO_DATE(FUACCEPTDATETIME)");
		lSql.append(" FROM FACTORINGUNITS_ARCH, INSTRUMENTS_ARCH");
		lSql.append(" WHERE FURECORDVERSION > 0");
		lSql.append(" AND INRECORDVERSION > 0");
		lSql.append(" AND INFUID = FUID");
		lSql.append(" AND FUACCEPTDATETIME IS NOT NULL");
		if (StringUtils.isNotEmpty(pEntityCode)){
			lSql.append(" AND FUFINANCIER = ").append(lDbHelper.formatString(pEntityCode));
		}
		lSql.append(" ) factInst LEFT OUTER JOIN ");
		lSql.append(" ( SELECT ingroupinid , count(*) groupcount FROM INSTRUMENTS WHERE INRECORDVERSION > 0 ");
		lSql.append(" AND INGROUPFLAG IS NULL AND INGROUPINID IS NOT NULL ");
		lSql.append(" GROUP BY INGROUPINID ) grpinst ON (factinst.inid = grpinst.ingroupinid ) ");
		lSql.append(" ) GROUP BY FUSUPPLIER, FUPURCHASER, FUFINANCIER");
		//
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
			if (logger.isDebugEnabled())
				logger.debug(lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			String lEntityCode, lCategory, lSubCategory;
			String[] lEntities = new String[] { "", "", "" }; // Purchaser,
																// Supplier,
																// Financier
			String[] lCatCatType; // 0=Category, 1=CategoryType
			HashMap<String, Object> lCatTypes = null;
			HashMap<String, Object> lCatTypeValues = null;
			HashMap<String, Object> lTimewiseHash = null;
			Object[] lInvCountAmt; // 0=InvoiceCount 1=InvoiceFactoredAmount
			String[] lInvCountCol = new String[] { "CurrDateInvCount", "CurrMthInvCount", "PrevMthInvCount",
					"FYInvCount", "TillDateInvCount" };
			String[] lInvAmtCol = new String[] { "CurrDateFactAmt", "CurrMthFactAmt", "PrevMthFactAmt", "FYFactAmt", 
					"TillDateFYFactAmt" };
			String[] lValKeys = new String[] { "cur", "curmth", "prevmth", "fy", "si" };
			HashMap<String, Object> lTotalTimewiseHash = null;
			HashMap<String, Object> lGrandTotalTimewiseHash = null;
			// long[] lTotalInvoices = new long[] { 0, 0, 0, 0, 0 };
			// BigDecimal[] lTotalTurnover = new BigDecimal[] { BigDecimal.ZERO,
			// BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			// BigDecimal.ZERO };
			Object[] lCatTotal = null;
			long lTotalInvoices = 0;
			BigDecimal lTotalTurnover = BigDecimal.ZERO;
			Object[] lTotalInvCountAmt; // 0=InvoiceCount
										// 1=InvoiceFactoredAmount
			Object[] lGrandTotalInvCountAmt; // 0=InvoiceCount
												// 1=InvoiceFactoredAmount
			//
			lGrandTotalTimewiseHash = new HashMap<String, Object>();
			for (int lPtr1 = 0; lPtr1 < lInvCountCol.length; lPtr1++) {
				lGrandTotalTimewiseHash.put(lValKeys[lPtr1], new Object[] { lTotalInvoices, lTotalTurnover });
			}
			//
			while (lResultSet.next()) {
				lEntities[0] = lResultSet.getString("FUPURCHASER");
				lEntities[1] = lResultSet.getString("FUSUPPLIER");
				lEntities[2] = lResultSet.getString("FUFINANCIER");
				//
				for (int lPtr = 0; lPtr < lEntities.length; lPtr++) {
					lEntityCode = lEntities[lPtr];
					lCatCatType = (String[]) lEntityCategories.get(lEntityCode);
					lCategory = lCatCatType[0];
					lSubCategory = lCatCatType[1];
					//logger.info("Category : " + lCategory + " SubCategory : " + lSubCategory);
					//
					if (!lData.containsKey(lCatCatType[0])) {
						lCatTypeValues = new HashMap<String, Object>();
						lData.put(lCatCatType[0], lCatTypeValues);// Key=Category
					}
					lCatTypeValues = (HashMap<String, Object>) lData.get(lCategory);
					//
					if (!lCatTypeValues.containsKey(lSubCategory)) {
						lTimewiseHash = new HashMap<String, Object>();
						lCatTypeValues.put(lSubCategory, lTimewiseHash);// Key=SubCategory
						if (!lCatTypeValues.containsKey("TOTAL")) {
							lTotalTimewiseHash = new HashMap<String, Object>();
							for (int lPtr1 = 0; lPtr1 < lInvCountCol.length; lPtr1++) {
								lTotalTimewiseHash.put(lValKeys[lPtr1], new Object[] { lTotalInvoices, lTotalTurnover });
							}
							lCatTypeValues.put("TOTAL", lTotalTimewiseHash);
						}
					}
					lTimewiseHash = (HashMap<String, Object>) lCatTypeValues.get(lSubCategory);
					lTotalTimewiseHash = (HashMap<String, Object>) lCatTypeValues.get("TOTAL"); // always remove total with category

					for (int lPtr1 = 0; lPtr1 < lInvCountCol.length; lPtr1++) {
						if (!lTimewiseHash.containsKey(lValKeys[lPtr1])) {
							lInvCountAmt = new Object[] { 0, BigDecimal.ZERO };
							lTimewiseHash.put(lValKeys[lPtr1], lInvCountAmt);// Key=cur/curMth Values=Object[] { LongInvoiceCount , BigDecimalInvAmt}
						}
						lInvCountAmt = (Object[]) lTimewiseHash.get(lValKeys[lPtr1]);
						lInvCountAmt[0] = Long.parseLong(lInvCountAmt[0].toString())
								+ lResultSet.getLong(lInvCountCol[lPtr1]);
						lInvCountAmt[1] = ((BigDecimal) lInvCountAmt[1]).add(BigDecimal.valueOf(lResultSet.getDouble(lInvAmtCol[lPtr1])));
						//
						lTotalInvCountAmt = (Object[]) lTotalTimewiseHash.get(lValKeys[lPtr1]);
						lTotalInvCountAmt[0] = Long.parseLong(lTotalInvCountAmt[0].toString())
								+ lResultSet.getLong(lInvCountCol[lPtr1]);
						lTotalInvCountAmt[1] = ((BigDecimal) lTotalInvCountAmt[1]).add(BigDecimal.valueOf(lResultSet.getDouble(lInvAmtCol[lPtr1])));
						//
						// Collect grand total only of one entity
						if (lPtr == 0) {
							lGrandTotalInvCountAmt = (Object[]) lGrandTotalTimewiseHash.get(lValKeys[lPtr1]);
							lGrandTotalInvCountAmt[0] = Long.parseLong(lGrandTotalInvCountAmt[0].toString())
									+ lResultSet.getLong(lInvCountCol[lPtr1]);
							lGrandTotalInvCountAmt[1] = ((BigDecimal) lGrandTotalInvCountAmt[1]).add(BigDecimal.valueOf(lResultSet.getDouble(lInvAmtCol[lPtr1])));
						}
					}
				}
			}
			// dividing amount by 100000 after totaling
			for (String lTmpCat : lData.keySet()) {
				HashMap<String, Object> lSubCat = (HashMap<String, Object>) lData.get(lTmpCat);
				for (String lTmpSubCat : lSubCat.keySet()) {
					HashMap<String, Object> lTimeCountVal = (HashMap<String, Object>) lSubCat.get(lTmpSubCat);
					for (String lTmpTimeKeys : lTimeCountVal.keySet()) {
						try
						{
							Object[] lVals = (Object[]) lTimeCountVal.get(lTmpTimeKeys);
							//logger.info("Category : " + lTmpCat + ", Sub Category : " + lTmpSubCat + ", Time Key : "
							//		+ lTmpTimeKeys + ", Count : " + lVals[0] + ", Amt : " + lVals[1]);
							lVals[1] = ((BigDecimal)lVals[1]).divide(AMT_ONE_LAKH).setScale(AMT_SCALE, BigDecimal.ROUND_HALF_UP);
						}
						catch(Exception lTmp){
							logger.info(lTmp.toString());
						}
					}
				}
			}
			for (String lTmpTimeKeys : lGrandTotalTimewiseHash.keySet()) {
				try
				{
					Object[] lVals = (Object[]) lGrandTotalTimewiseHash.get(lTmpTimeKeys);
					lVals[1] = ((BigDecimal)lVals[1]).divide(AMT_ONE_LAKH).setScale(AMT_SCALE, BigDecimal.ROUND_HALF_UP);
				}
				catch(Exception lTmp){
					logger.info(lTmp.toString());
				}
			}
			lData.put("GRANDTOTAL", lGrandTotalTimewiseHash);
			//
		} catch (Exception lEx) {
			logger.info(lEx.getMessage());
		}
		finally {
			if (lResultSet != null)
				lResultSet.close();
			if (lStatement != null)
				lStatement.close();
		}
		return lData;
	}

	public String getAuctinCalendar(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, List<String> pTagNames)
			throws Exception {
		Map<String, Object> lMap = new HashMap<String, Object>();
		AppEntityBean lAppEntityBean = null;
		if (pAppUserBean != null) {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
			lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pAppUserBean.getDomain() });
		}
		AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
		if (lAuctionCalendarBean != null) {
			BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
			lMap.put(TAG_AUCTION_CALENDAR, lBeanMeta.formatAsMap(lAuctionCalendarBean, null, null, true));
		}
		return new JsonBuilder(lMap).toString();
	}

	private Object getTredsSummary(Connection pConnection) throws Exception {
		Map<String, Object> lMap = new HashMap<String, Object>();
		DBHelper lDBHelper = DBHelper.getInstance();
		String lSql = "SELECT SUM(CASE SUBSTR(AEType,1,1) WHEN 'Y' THEN 1 ELSE 0 END),SUM(CASE SUBSTR(AEType,2,1) WHEN 'Y' THEN 1 ELSE 0 END),SUM(CASE SUBSTR(AEType,3,1) WHEN 'Y' THEN 1 ELSE 0 END) FROM AppEntities WHERE AERecordVersion > 0";
		List<Object> lEntitySummary = getResultSet(pConnection, lSql);
		if (lEntitySummary != null) {
			lMap.put("sup", lEntitySummary.get(0));
			lMap.put("pur", lEntitySummary.get(1));
			lMap.put("fin", lEntitySummary.get(2));
		}
		lSql = "SELECT COUNT(1) FROM Instruments WHERE INRECORDVERSION > 0 AND INGROUPINID IS NULL AND INSTATUS = "
				+ lDBHelper.formatString(InstrumentBean.Status.Factored.getCode());
		List<Object> lInstSummary = getResultSet(pConnection, lSql);
		if (lInstSummary != null)
			lMap.put("instCount", lInstSummary.get(0));
		return lMap;
	}

	private Object getFinancierBuyerSummary(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT FUPURCHASER FASPurchaser,FASLimit,FASUtilised,COUNT(1) FASAuId,SUM(FUAMOUNT) FASMinBidRate");
		lSql.append(" ,MIN(FUMATURITYDATE) FASRecordCreateTime,MAX(FUMATURITYDATE) FASRecordUpdateTime");
		lSql.append(" FROM FACTORINGUNITS LEFT OUTER JOIN FINANCIERAUCTIONSETTINGS");
		lSql.append(" ON FASPURCHASER = FUPURCHASER AND FASLEVEL = ").append(lDBHelper.formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode()));
		lSql.append(" AND FASACTIVE = ").append(lDBHelper.formatString(CommonAppConstants.YesNo.Yes.getCode()));
		lSql.append(" AND ( FASEXPIRYDATE IS NULL  OR FASEXPIRYDATE > ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate())).append("  ) ");
		lSql.append(" AND FASFINANCIER = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		lSql.append(" WHERE FURECORDVERSION > 0 AND FUSTATUS = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
		lSql.append(" GROUP BY FUPURCHASER,FASLIMIT,FASUTILISED ORDER BY FASLIMIT DESC,SUM(FUAMOUNT) DESC");

		List<FinancierAuctionSettingBean> lList = financierAuctionSettingDAO.findListFromSql(pConnection, lSql.toString(), 0);

		Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();
		List<Map<String, Object>> lMyBuyers = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> lOthers = new ArrayList<Map<String, Object>>();
		AppEntityBean lPurchaser = null;
		List<String> lBlockedFinanciers = null;
		for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lList) {
			Map<String, Object> lRecord = new HashMap<String, Object>();
			lPurchaser = TredsHelper.getInstance().getAppEntityBean(lFinancierAuctionSettingBean.getPurchaser());
			lBlockedFinanciers = lPurchaser.getBlockedFinancierList();
			if (lBlockedFinanciers != null && lBlockedFinanciers.contains(pAppUserBean.getDomain())) {
				continue; // skipping blocked financier.
			}
			lRecord.put("pur", lFinancierAuctionSettingBean.getPurchaser());
			lRecord.put("purName", lFinancierAuctionSettingBean.getPurName());
			lRecord.put("cnt", lFinancierAuctionSettingBean.getAuId());
			lRecord.put("amt", TredsHelper.getInstance().getFormattedAmount(lFinancierAuctionSettingBean.getMinBidRate(), true));
			lRecord.put("min", Long.valueOf(OtherResourceCache.getInstance().getDiffInDays(lFinancierAuctionSettingBean.getRecordCreateTime(), lCurrentDate)));
			lRecord.put("max", Long.valueOf(OtherResourceCache.getInstance().getDiffInDays(lFinancierAuctionSettingBean.getRecordCreateTime(), lCurrentDate)));
			if (lFinancierAuctionSettingBean.getLimit() == null) {
				lOthers.add(lRecord);
			}
			else {
				lRecord.put("lmt", TredsHelper.getInstance().getFormattedAmount(lFinancierAuctionSettingBean.getLimit(), true));
				lRecord.put("bal", TredsHelper.getInstance().getFormattedAmount(lFinancierAuctionSettingBean.getBalance(), true));
				lMyBuyers.add(lRecord);
			}
		}
		return new Object[] { lMyBuyers, lOthers };
	}

	private Object getFactored(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT * FROM FactoringUnits,Bids,FactoringUnitWatch WHERE FURecordVersion > 0 AND FUId = BDFuId AND FUWFuId = FUId ");
		lSql.append(" AND FUWAuId = ").append(pAppUserBean.getId());
		lSql.append(" AND BDFinancierEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Factored.getCode()));
		lSql.append(" AND (FUAcceptDateTime IS NULL OR FUAcceptDateTime > ").append(lDBHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate())).append(")");
		// lSql.append(" AND (FUFinancier IS NULL OR FUFinancier = ").append(lDBHelper.formatString(pUserBean.getDomain())).append(")");
		lSql.append(" ORDER BY FUId");

		List<FactoringUnitBidBean> lList = factoringUnitBidDAO.findListFromSql(pConnection, lSql.toString(), -1);
		int lMyFactored = 0, lOtherFactored = 0;
		Map<String, Object> lRecord = new HashMap<String, Object>();

		for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
			if (lFactoringUnitBidBean.getBidBean() != null
					&& lFactoringUnitBidBean.getBidBean().getId() != null
					&& lFactoringUnitBidBean.getBidBean().getId().equals(lFactoringUnitBidBean.getFactoringUnitBean().getBdId()))
				lMyFactored++;
			else
				lOtherFactored++;
		}
		lRecord.put("myFact", lMyFactored);
		lRecord.put("otherFact", lOtherFactored);

		return lRecord;
	}

	private Object getInstSummary(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		//The following counters are added only for maker
		//Draft Means all Instr showin in Makers Inbox = Draft+CheckerReturned+CounterReturned 
		Map<String, Long> lMap = new HashMap<String, Long>();
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT INStatus, COUNT(1) INId FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL ");
		instrumentBO.appendMakerFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		for (InstrumentBean lInstrumentBean : lList) {
			lMap.put(lInstrumentBean.getStatus().getCode(), lInstrumentBean.getId());
		}
		long lDraft = 0;
		Long lValue = lMap.get(InstrumentBean.Status.Drafting.getCode());
		if (lValue != null)
			lDraft += lValue.longValue();
		lValue = lMap.get(InstrumentBean.Status.Checker_Returned.getCode());
		if (lValue != null)
			lDraft += lValue.longValue();
		lValue = lMap.get(InstrumentBean.Status.Counter_Returned.getCode());
		if (lValue != null)
			lDraft += lValue.longValue();
		// lValue = lMap.get(InstrumentBean.Status.Withdrawn.getCode());
		// if (lValue != null) lDraft += lValue.longValue();
		if (lDraft > 0)
			lMap.put(InstrumentBean.Status.Drafting.getCode(), Long.valueOf(lDraft));

		long lFactored = 0;
		lValue = lMap.get(InstrumentBean.Status.Factored.getCode());
		if (lValue != null)
			lFactored += lValue.longValue();
		lValue = lMap.get(InstrumentBean.Status.Leg_1_Settled.getCode());
		if (lValue != null)
			lFactored += lValue.longValue();
		if (lFactored > 0)
			lMap.put(InstrumentBean.Status.Factored.getCode(), Long.valueOf(lFactored));

		long lInAuction = 0;
		lValue = lMap.get(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		if (lValue != null)
			lInAuction += lValue.longValue();
		if (lFactored > 0)
			lMap.put(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode(), Long.valueOf(lInAuction));
		
		long lFailed = 0;
		lValue = lMap.get(InstrumentBean.Status.Leg_1_Failed.getCode());
		if (lValue != null)
			lFailed += lValue.longValue();
		lValue = lMap.get(InstrumentBean.Status.Leg_2_Failed.getCode());
		if (lValue != null)
			lFailed += lValue.longValue();
		if (lFailed > 0)
			lMap.put(InstrumentBean.Status.Leg_1_Failed.getCode(), Long.valueOf(lFailed));

		//this is for Makers Checker Inbox 
		//Makers Checker Inbox will show Submitted  for checking
		lSql = new StringBuilder();
		lSql.append("SELECT INStatus, COUNT(1) INId FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL ");
		lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Submitted.getCode()));
		if (pAppUserBean.getInstLevel()!=null && pAppUserBean.getInstLevel()>0 && !pAppUserBean.getLoginId().equals(AppConstants.LOGINID_ADMIN)) {
			lSql.append(" AND ( INMKRChkLevel = ").append(pAppUserBean.getInstLevel()).append(" OR INMKRChkLevel IS NULL ) ");
		}
		instrumentBO.appendCheckerFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		for (InstrumentBean lInstrumentBean : lList) {
			lMap.put(lInstrumentBean.getStatus().getCode() + "_1", lInstrumentBean.getId());
		}

		//this is for Coutner Maker Inbox
		//Counter Makers Inbox will show CheckerAppoved and Counter Checker Returned
		lSql = new StringBuilder();
		lSql.append("SELECT INStatus, COUNT(1) INId FROM Instruments WHERE INRecordVersion > 0 ");
		lSql.append(" AND INStatus IN ( ").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Approved.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Return.getCode())).append(" ) ");
		lSql.append(" AND INGROUPINID IS NULL ");
		instrumentBO.appendCounterFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		for (InstrumentBean lInstrumentBean : lList) {
			lMap.put(lInstrumentBean.getStatus().getCode() + "_1", lInstrumentBean.getId());
		}
		long lCounter = 0;
		lValue = lMap.get(InstrumentBean.Status.Checker_Approved.getCode()+ "_1");
		if (lValue != null)
			lCounter += lValue.longValue();
		lValue = lMap.get(InstrumentBean.Status.Counter_Checker_Return.getCode() + "_1");
		if (lValue != null)
			lCounter += lValue.longValue();
		if (lCounter > 0)
			lMap.put(InstrumentBean.Status.Checker_Approved.getCode() + "_1", Long.valueOf(lCounter));

		//this is for Coutner Checker Inbox
		//Counter Checker Inbox will show Counter_Checker_Pending
		lSql = new StringBuilder();
		lSql.append("SELECT INStatus, COUNT(1) INId FROM Instruments WHERE INRecordVersion > 0 ");
		lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Pending.getCode()));
		if (pAppUserBean.getInstCntrLevel()!=null && pAppUserBean.getInstCntrLevel()>0 && !pAppUserBean.getLoginId().equals(AppConstants.LOGINID_ADMIN)) {
			lSql.append(" AND ( INCNTChkLevel = ").append(pAppUserBean.getInstCntrLevel()).append(" OR INCNTChkLevel IS NULL ) ");
		}
		instrumentBO.appendCounterCheckerFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		for (InstrumentBean lInstrumentBean : lList) {
			lMap.put(lInstrumentBean.getStatus().getCode() + "_1", lInstrumentBean.getId());
		}
		return lMap;
	}
	
	private Object getFuSummary(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		Map<String, Long> lMap = new HashMap<String, Long>();
		//
		lMap.put(FactoringUnitBean.Status.Ready_For_Auction.getCode(), getReadyCount(pConnection, pAppUserBean));
	    //
		return lMap;
	}
	
    public long getReadyCount(Connection pConnection, AppUserBean pUserBean) throws Exception{
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        lSql.append("SELECT Count(*) FROM FactoringUnits ");
        boolean lCheckAccessToLocations = TredsHelper.getInstance().checkAccessToLocations((AppUserBean) pUserBean);
		if (lCheckAccessToLocations){
			lSql.append(" join Instruments on infuid = fuid ");
		}
		lSql.append(" WHERE FURecordVersion > 0 ");
        lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Ready_For_Auction.getCode()));
        if(pUserBean!=null && AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
        {
        	//no filters
        }
        else
        {
        	StringBuilder lWhere1, lWhere2, lWhere3;
        	lWhere1 = new StringBuilder();
        	lWhere2 = new StringBuilder();
        	lWhere3 = new StringBuilder();
        	//
        	lWhere1.append(" FUIntroducingEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere2.append(" FUCounterEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere3.append(" FUOwnerEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	//
        	if (pUserBean.getType() != AppUserBean.Type.Admin) {
                if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
                   	lWhere1.append(" AND ( FUIntroducingAUId = ").append(pUserBean.getId()).append(" OR FUIntroducingAUId IS NULL ) ");
                   	lWhere2.append(" AND ( FUCounterAUId = ").append(pUserBean.getId()).append(" OR FUCounterAUId IS NULL ) ");
                    lWhere3.append(" AND ( FUOwnerAuId = ").append(pUserBean.getId()).append(" OR  FUOwnerAuId IS NULL ) ");            
                }
        	}
            lSql.append(" AND ( ");
            lSql.append(" ( ").append(lWhere1.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere2.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere3.toString()).append(" ) ");
            lSql.append(" ) ");
        }
        lSql.append(" AND (FUAcceptDateTime IS NULL OR FUAcceptDateTime > ").append(lDBHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate())).append(")");
		if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) && lCheckAccessToLocations){
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
			if(lAppEntityBean.isPurchaser()){
        		lSql.append(" AND INPurClId in (").append(TredsHelper.getInstance()
        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");
			}else if(lAppEntityBean.isSupplier()){
	        	lSql.append(" AND INSupClId in (").append(TredsHelper.getInstance()
	        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");				
			}
        }
		if(pUserBean.hasUserLimit()){
			if(pUserBean.getMinUserLimit()!=null){
				lSql.append(" AND FUAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if(pUserBean.getMaxUserLimit()!=null){
				lSql.append(" AND FUAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}
        lSql.append(" ORDER BY FUId");
        //
        long lReadyCount = 0;
    	logger.info(lSql.toString());
		try(Statement lStatement =  pConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
			while (lResultSet.next()){
				lReadyCount = lResultSet.getLong(1);
			}
		} catch (Exception e) {
			logger.info("Error in getReadyCount : "+e.getMessage());
		}
        return lReadyCount;
    }


	private Object getPurchaserLimit(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		FinancierAuctionSettingBO lFASBo = new FinancierAuctionSettingBO();
		return lFASBo.getTotalFinancierLimit(pConnection, pAppUserBean, null);
	}

	private Object getMyInstruments(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT INStatus, COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		if (pAppUserBean.getType() != AppUserBean.Type.Admin)
			lSql.append(" AND INMakerAUId = ").append(pAppUserBean.getId());
		lSql.append(" GROUP BY INStatus");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		Map<String, Object> lData[] = new HashMap[6];
		String[] lBgColors = new String[] { "olive", "aqua", "olive", "aqua", "aqua", "aqua" };
		String[] lLabels = new String[] { "With Me", "In Process", "Accepted", "Rejected", "Being Factored", "Factored" };
		Long lZero = Long.valueOf(0);
		for (int lPtr = 0; lPtr < lData.length; lPtr++) {
			Map<String, Object> lMap = new HashMap<String, Object>();
			lMap.put("l", lLabels[lPtr]);
			lMap.put("b", lBgColors[lPtr]);
			lMap.put("c", lZero);
			lMap.put("v", lZero);
			lData[lPtr] = lMap;
		}

		for (InstrumentBean lInstrumentBean : lList) {
			instrumentBO.setTabForMaker(lInstrumentBean, Boolean.FALSE);
			if (lInstrumentBean.getTab() != null) {
				Map<String, Object> lMap = lData[lInstrumentBean.getTab().intValue()];
				lMap.put("c", lInstrumentBean.getId());
				lMap.put("v", lInstrumentBean.getNetAmount());
			}
		}
		return lData;
	}

	private Object getCheckerInstruments(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT INStatus, COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		instrumentBO.appendCheckerFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		Map<String, Object> lData[] = new HashMap[6];
		String[] lBgColors = new String[] { "olive", "aqua", "aqua", "aqua", "aqua", "aqua" };
		String[] lLabels = new String[] { "With Me", "In Process", "Accepted", "Rejected", "Being Factored", "Factored" };
		Long lZero = Long.valueOf(0);
		for (int lPtr = 0; lPtr < lData.length; lPtr++) {
			Map<String, Object> lMap = new HashMap<String, Object>();
			lMap.put("l", lLabels[lPtr]);
			lMap.put("b", lBgColors[lPtr]);
			lMap.put("c", lZero);
			lMap.put("v", lZero);
			lData[lPtr] = lMap;
		}

		for (InstrumentBean lInstrumentBean : lList) {
			instrumentBO.setTabForChecker(lInstrumentBean, Boolean.FALSE);
			if (lInstrumentBean.getTab() != null) {
				Map<String, Object> lMap = lData[lInstrumentBean.getTab().intValue()];
				lMap.put("c", lInstrumentBean.getId());
				lMap.put("v", lInstrumentBean.getNetAmount());
			}
		}
		return lData;
	}

	private Object getCounterInstruments(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT INStatus, COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INCounterEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		instrumentBO.appendCounterFilter(lSql, pAppUserBean);
		lSql.append(" GROUP BY INStatus");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
		Map<String, Object> lData[] = new HashMap[6];
		String[] lBgColors = new String[] { "olive", "aqua", "aqua", "aqua", "aqua", "aqua" };
		String[] lLabels = new String[] { "With Me", "In Process", "Accepted", "Rejected", "Being Factored", "Factored" };
		Long lZero = Long.valueOf(0);
		for (int lPtr = 0; lPtr < lData.length; lPtr++) {
			Map<String, Object> lMap = new HashMap<String, Object>();
			lMap.put("l", lLabels[lPtr]);
			lMap.put("b", lBgColors[lPtr]);
			lMap.put("c", lZero);
			lMap.put("v", lZero);
			lData[lPtr] = lMap;
		}

		for (InstrumentBean lInstrumentBean : lList) {
			instrumentBO.setTabForCounter(lInstrumentBean, Boolean.FALSE);
			if (lInstrumentBean.getTab() != null) {
				Map<String, Object> lMap = lData[lInstrumentBean.getTab().intValue()];
				lMap.put("c", lInstrumentBean.getId());
				lMap.put("v", lInstrumentBean.getNetAmount());
			}
		}
		return lData;
	}

	private Object getInstSummary1(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		StringBuilder lSql;
		DBHelper lDBHelper = DBHelper.getInstance();
		Map<String, Object> lData[] = new HashMap[6];

		lSql = new StringBuilder();
		lSql.append("SELECT  COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		if (pAppUserBean.getType() != AppUserBean.Type.Admin)
			lSql.append(" AND INMakerAUId = ").append(pAppUserBean.getId());
		lSql.append(" AND INStatus IN (").append(lDBHelper.formatString(InstrumentBean.Status.Drafting.getCode()));
		lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Returned.getCode()));
		lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Returned.getCode())).append(")");
		lData[0] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		lSql = new StringBuilder();
		lSql.append("SELECT COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		instrumentBO.appendCheckerFilter(lSql, pAppUserBean);
		lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Submitted.getCode()));
		lData[1] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		lSql = new StringBuilder();
		lSql.append("SELECT COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INCounterEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		instrumentBO.appendCounterFilter(lSql, pAppUserBean);
		lSql.append(" AND INStatus IN ( ").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Approved.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Return.getCode())).append(" ) ");
		lData[2] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		lSql = new StringBuilder();
		lSql.append("SELECT COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		if (pAppUserBean.getType() != AppUserBean.Type.Admin)
			lSql.append(" AND INMakerAUId = ").append(pAppUserBean.getId());
		lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Approved.getCode()));
		lData[3] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		lSql = new StringBuilder();
		lSql.append("SELECT COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		if (pAppUserBean.getType() != AppUserBean.Type.Admin)
			lSql.append(" AND INMakerAUId = ").append(pAppUserBean.getId());
		lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Submitted.getCode()));
		lData[4] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		lSql = new StringBuilder();
		lSql.append("SELECT COUNT(1) INId, SUM(INNetAmount) INNetAmount FROM Instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL AND INMakerEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		if (pAppUserBean.getType() != AppUserBean.Type.Admin)
			lSql.append(" AND INMakerAUId = ").append(pAppUserBean.getId());
		lSql.append(" AND INStatus IN ( ").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Approved.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Return.getCode())).append(" ) ");
		lData[5] = getDataMap(pConnection, pAppUserBean, lSql.toString());

		return lData;
	}

	private Map<String, Object> getDataMap(Connection pConnection, AppUserBean pAppUserBean, String pSql)
			throws Exception {
		Map<String, Object> lMap;
		InstrumentBean lInstrumentBean = instrumentDAO.findBean(pConnection, pSql);
		lMap = new HashMap<String, Object>();
		lMap.put("c", lInstrumentBean == null ? Long.valueOf(0) : lInstrumentBean.getId());
		lMap.put("v", lInstrumentBean == null ? BigDecimal.ZERO : lInstrumentBean.getNetAmount());
		return lMap;
	}

	private Object getFactoringUnits(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		Map<String, Object> lMap = new HashMap<String, Object>();
		DBHelper lDBHelper = DBHelper.getInstance();
		String lSql = "";
		List<Object> lData = getResultSet(pConnection, lSql);
		return lData;
	}

	private Object getObligations(Connection pConnection, AppUserBean pAppUserBean, boolean pReceivable)
			throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		java.sql.Date lDate = OtherResourceCache.getInstance().getCurrentDate();
		java.sql.Date lPrevDate = OtherResourceCache.getInstance().getPreviousDate(lDate, 1);
		java.sql.Date lNextDate = OtherResourceCache.getInstance().getNextDate(lDate, 1);
		java.sql.Date lPrevWeekDate = OtherResourceCache.getInstance().getPreviousDate(lDate, 7);
		java.sql.Date lNextDate30 = OtherResourceCache.getInstance().getNextDate(lDate, 30);
		java.sql.Date lNextDate31 = OtherResourceCache.getInstance().getNextDate(lDate, 31);
		java.sql.Date lNextDate60 = OtherResourceCache.getInstance().getNextDate(lDate, 60);
		//
		/*
		 * lSql = new StringBuilder(); lSql.append(
		 * "SELECT OBDate,SUM(OBAmount) OBAmount,SUM(OBSettledAmount) OBSettledAmount "
		 * ); //lSql.append(" OBStatus = ");
		 * lSql.append(" FROM Obligations WHERE OBRecordVersion > 0 ");
		 * lSql.append
		 * (" AND OBTxnType = ").append(lDBHelper.formatString(pReceivable
		 * ?TxnType.Credit.getCode():TxnType.Debit.getCode()));
		 * lSql.append(" AND OBTxnEntity = "
		 * ).append(lDBHelper.formatString(pAppUserBean.getDomain()));
		 * lSql.append
		 * (" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean
		 * .Status.Failed.getCode()));
		 * lSql.append(" AND OBDate BETWEEN ").append
		 * (lDBHelper.formatDate(lPrevWeekDate
		 * )).append(" AND ").append(lDBHelper.formatDate(lPrevDate));
		 * lSql.append(" GROUP BY OBTxnEntity, OBDate ORDER BY OBDate");
		 * List<ObligationBean> lFailedList =
		 * obligationDAO.findListFromSql(pConnection, lSql.toString(), 0);
		 */
		//
		lSql = new StringBuilder();
		lSql.append("SELECT OBStatus,OBDate,SUM(OBAmount) OBAmount,SUM(OBSettledAmount) OBSettledAmount ");
		// lSql.append(" OBStatus = ");
		lSql.append(" FROM Obligations WHERE OBRecordVersion > 0 ");
		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(pReceivable ? TxnType.Credit.getCode()
				: TxnType.Debit.getCode()));
		lSql.append(" AND OBTxnEntity = ").append(lDBHelper.formatString(pAppUserBean.getDomain()));
		lSql.append(" AND OBStatus IN ( ");
		lSql.append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationBean.Status.Prov_Success.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode()));
		lSql.append(" ) ");
		lSql.append(" AND OBDate BETWEEN ").append(lDBHelper.formatDate(lPrevWeekDate)).append(" AND ").append(lDBHelper.formatDate(lNextDate60));
		lSql.append(" GROUP BY OBStatus, OBDate ORDER BY OBDate");
		List<ObligationBean> lList = obligationDAO.findListFromSql(pConnection, lSql.toString(), 0);

		SimpleDateFormat lSimpleDateFormat = BeanMetaFactory.getInstance().getDateFormatter();
		String[] lLabels = new String[] { "PrevWeek", "Today", "Tomorrow", "Next 30", "Next 60" };
		java.sql.Date[] lDate1 = new java.sql.Date[] { lPrevWeekDate, lDate, lNextDate, lNextDate, lNextDate31 };
		java.sql.Date[] lDate2 = new java.sql.Date[] { lPrevDate, lDate, lNextDate, lNextDate30, lNextDate60 };
		int lCount = lLabels.length;
		BigDecimal[][] lValues = new BigDecimal[lCount][];
		for (int lPtr = 0; lPtr < lCount; lPtr++)
			lValues[lPtr] = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };
		/*
		 * if(lFailedList!=null&&lFailedList.size()>0){ ObligationBean
		 * lObligationBean = lFailedList.get(0); lValues[0][0] =
		 * lObligationBean.getAmount(); //TODO: the below amount may not be
		 * required - check if (lObligationBean.getSettledAmount() != null)
		 * lValues[0][1] =
		 * lValues[0][1].add(lObligationBean.getSettledAmount()); }
		 */
		for (ObligationBean lObligationBean : lList) {
			for (int lPtr = 0; lPtr < lCount; lPtr++) {
				if ((lObligationBean.getDate().compareTo(lDate1[lPtr]) >= 0)
						&& (lObligationBean.getDate().compareTo(lDate2[lPtr]) <= 0)) {
					lValues[lPtr][0] = lValues[lPtr][0].add(lObligationBean.getAmount());
					if ((lObligationBean.getStatus() == ObligationBean.Status.Success)
							|| (lObligationBean.getStatus() == ObligationBean.Status.Prov_Success)) {
						if (lObligationBean.getSettledAmount() != null)
							lValues[lPtr][1] = lValues[lPtr][1].add(lObligationBean.getSettledAmount());
					}
				}
			}
		}
		Map<String, Object>[] lData = new HashMap[lValues.length];
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lMap = new HashMap<String, Object>();
			lMap.put("l", lLabels[lPtr]);
			lMap.put("a", lValues[lPtr][0]);
			lMap.put("s", lValues[lPtr][1]);
			lMap.put("d1", lSimpleDateFormat.format(lDate1[lPtr]));
			lMap.put("d2", lSimpleDateFormat.format(lDate2[lPtr]));
			BigDecimal lPending = lValues[lPtr][0].subtract(lValues[lPtr][1]);
			lMap.put("p", lPending);
			if ((lPtr == 0) && (lPending.compareTo(BigDecimal.ZERO) > 0))
				lMap.put("hl", Boolean.TRUE);
			lData[lPtr] = lMap;
		}

		return lData;
	}

	private List<Object> getResultSet(Connection pConnection, String pSql) throws Exception {
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
			lResultSet = lStatement.executeQuery(pSql);
			int lColCount = lResultSet.getMetaData().getColumnCount();
			if (lResultSet.next()) {
				List<Object> lList = new ArrayList<Object>();
				for (int lPtr = 1; lPtr <= lColCount; lPtr++)
					lList.add(lResultSet.getObject(lPtr));
				return lList;
			}
		}
		finally {
			if (lResultSet != null)
				lResultSet.close();
			if (lStatement != null)
				lStatement.close();
		}
		return null;
	}

	public Map<String, Object> getDataForFinancierDashboard(Connection pConnection, String pEntityCode) throws Exception {
		Map<String, Object> lMap = new HashMap<>();
		Map<String, Set<String>> lCatwiseCatTypes = new HashMap<String, Set<String>>();
		Map<String,Object> lRegData = getEntityCategoryTypesData(pConnection, lCatwiseCatTypes);
		// Registration
		lMap.put("regEntCatTypeData", lRegData);
		lMap.put("regEntCatTypes", getEntityCategoryTypes(pConnection, lCatwiseCatTypes)); // take in variable and remove items not in data

		// Transaction
		Map<String, Object> lTransData = getTransactionData(pConnection,pEntityCode);
		lMap.put("transTypes", getTransSummaryTypes());
		lMap.put("transData", lTransData);
		lMap.put("transSummaryData", getTransSummaryData(lTransData));
		//
		lMap.put("hasRegData", (lRegData!=null&& lRegData.size()>0));
		lMap.put("hasTransData", (lTransData!=null&&lTransData.size()>0));
		return lMap;
	}
}
