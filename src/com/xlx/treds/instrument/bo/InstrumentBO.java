package com.xlx.treds.instrument.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.DataType;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.ClickWrapHelper;
import com.xlx.treds.InstAuthJerseyClient;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.auction.bean.CIGroupBean;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.InstrumentCreation;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.entity.bean.BillingLocationBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.BillingLocationBO;
import com.xlx.treds.entity.bo.PurchaserAggregatorBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstFactUnitBidFilterBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bean.InstrumentBean.Type;
import com.xlx.treds.instrument.bean.InstrumentCreationKeysBean;
import com.xlx.treds.instrument.bean.InstrumentWorkFlowBean;
import com.xlx.treds.instrument.bean.MonetagoRequiredFieldsBean;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.monetago.bean.EwayBillDetailsBean;
import com.xlx.treds.monetago.bean.MonetagoEwaybillInfoBean;
import com.xlx.treds.monetago.bean.MonetagoInvoiceInfoBean;
import com.xlx.treds.monetago.bo.GstnMandateBO;
import com.xlx.treds.other.bean.CustomFieldBean;
import com.xlx.treds.other.bo.CustomFieldBO;
import com.xlx.treds.stats.IStatsCacheGenerator;
import com.xlx.treds.stats.StatsCacheFactory;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.IAgreementAcceptanceBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean.CheckerType;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class InstrumentBO {
	private static final Logger logger = LoggerFactory.getLogger(InstrumentBO.class);

	private static Long MAKERTAB_INBOX = Long.valueOf(0);
	private static Long MAKERTAB_CHECKERPENDING = Long.valueOf(1);
	private static Long MAKERTAB_COUNTERPENDING = Long.valueOf(2);
	private static Long MAKERTAB_READYFORAUCTION = Long.valueOf(3);
	private static Long MAKERTAB_REJECTED = Long.valueOf(4);
	private static Long MAKERTAB_INAUCTION = Long.valueOf(5);

	private static Long MAKERTAB_FACTORED = Long.valueOf(6);
	private static Long MAKERTAB_SETTLEMENT_FAILED = Long.valueOf(7);
	private static Long MAKERTAB_EXPIRED = Long.valueOf(8);
	private static Long MAKERTAB_GROUP = Long.valueOf(9);
	private static Long MAKERTAB_HISTORY = Long.valueOf(10);

	private static Long CHECKERTAB_INBOX = Long.valueOf(0);
	private static Long CHECKERTAB_CHECKERPENDING = Long.valueOf(1);
	private static Long CHECKERTAB_COUNTERPENDING = Long.valueOf(2);
	private static Long CHECKERTAB_READYFORAUCTION = Long.valueOf(3);
	private static Long CHECKERTAB_REJECTED = Long.valueOf(4);
	private static Long CHECKERTAB_INAUCTION = Long.valueOf(5);
	private static Long CHECKERTAB_FACTORED = Long.valueOf(6);
	private static Long CHECKERTAB_SETTLEMENT_FAILED = Long.valueOf(7);
	private static Long CHECKERTAB_EXPIRED = Long.valueOf(8);
	private static Long CHECKERTAB_GROUP = Long.valueOf(9);
	private static Long CHECKERTAB_HISTORY = Long.valueOf(10);

	private static Long COUNTERTAB_INBOX = Long.valueOf(0);
	private static Long COUNTERTAB_CHECKERPENDING = Long.valueOf(1);
	private static Long COUNTERTAB_COUNTERPENDING = Long.valueOf(2);
	private static Long COUNTERTAB_READYFORAUCTION = Long.valueOf(3);
	private static Long COUNTERTAB_REJECTED = Long.valueOf(4);
	private static Long COUNTERTAB_INAUCTION = Long.valueOf(5);
	private static Long COUNTERTAB_FACTORED = Long.valueOf(6);
	private static Long COUNTERTAB_SETTLEMENT_FAILED = Long.valueOf(7);
	private static Long COUNTERTAB_EXPIRED = Long.valueOf(8);
	private static Long COUNTERTAB_GROUP = Long.valueOf(9);
	private static Long COUNTERTAB_HISTORY = Long.valueOf(10);

	private static Long COUNTERCHECKERTAB_INBOX = Long.valueOf(0);
	private static Long COUNTERCHECKERTAB_INAUCTION = Long.valueOf(5);
	private static Long COUNTERCHECKERTAB__CHECKERRETURNED = Long.valueOf(11);
	private static Long COUNTERCHECKERTAB__HISTORY = Long.valueOf(10);

	private GenericDAO<InstrumentBean> instrumentDAO;
	private GenericDAO<CompanyLocationBean> companyLocationDAO;
	private GenericDAO<InstrumentWorkFlowBean> instrumentWorkFlowDAO;
	private GenericDAO<FactoringUnitBean> factoringUnitDAO;
	private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
	private GenericDAO<CompanyDetailBean> companyDetailDao;
	private GenericDAO<EwayBillDetailsBean> ewayBillDetailsDAO;

	private CompositeGenericDAO<FactoredBean> factoredBeanDAO;
	private GenericDAO<CIGroupBean> ciGroupBeanDAO;
	private AppUserBO appUserBO;
	private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
	private EmailGeneratorBO emailGeneratorBO;
	private GenericDAO<InstrumentCreationKeysBean> instrumentCreationKeysDAO;
	private GenericDAO<CIGroupBean> ciGroupDAO;
	private BillingLocationBO billingLocationBO;

	//
	public static String FIELDGROUP_AGGCHILDINST = "aggChildInst";
	public static String FIELDGROUP_AGG_COPY_PARENT_TO_CHILD = "aggPrntToChld";
	public static String FIELDGROUP_AGGPURSUPLINK = "aggPurSupLink";
	public static String FIELDGROUP_AGGCHILDTOPARENT = "aggChildToParent";
	public static String FIELDGROUP_AGGSPLITSDETAILS = "aggSplitsDetails";
	public static String FIELDGROUP_AGGPARENTINST = "aggParentInst";
	//

	private static final int DATES_INSTRUMENTDUEDATE = 0;
	private static final int DATES_STATUTORYDUEDATE = 1;
	private static final int DATES_MATURITYDATE = 2;
	private static final int DATES_EXTENDEDDUEDATE = 3;

	private static List<String> DISPLAY_LISTING_FILTER_MAKER = getDisplayFilterListMaker();
	private static List<String> DISPLAY_LISTING_FILTER_CHECKER = getDisplayFilterListChecker();
	private static List<String> DISPLAY_LISTING_FILTER_COUNTER = getDisplayFilterListCounter();
	private static List<String> DISPLAY_LISTING_FILTER_COUNTERCHECKER = getDisplayFilterListCounterChecker();

	private static List<String> DISPLAY_HISTORY_FILTER = getDisplayHistoryFilterList();

	private static List<String> VALID_STATUS = getValidStatusList();

	private static List<String> getDisplayFilterListMaker() {
		List<String> lList = new ArrayList<String>();
		lList.add(InstrumentBean.Status.Drafting.getCode());
		lList.add(InstrumentBean.Status.Submitted.getCode());
		lList.add(InstrumentBean.Status.Checker_Approved.getCode());
		lList.add(InstrumentBean.Status.Checker_Rejected.getCode());
		lList.add(InstrumentBean.Status.Checker_Returned.getCode());
		lList.add(InstrumentBean.Status.Counter_Approved.getCode());
		lList.add(InstrumentBean.Status.Counter_Rejected.getCode());
		lList.add(InstrumentBean.Status.Counter_Returned.getCode());
		lList.add(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Pending.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Return.getCode());
		return lList;
	}

	private static List<String> getDisplayFilterListChecker() {
		List<String> lList = new ArrayList<String>();
		lList.add(InstrumentBean.Status.Submitted.getCode());
		lList.add(InstrumentBean.Status.Checker_Approved.getCode());
		lList.add(InstrumentBean.Status.Checker_Rejected.getCode());
		lList.add(InstrumentBean.Status.Checker_Returned.getCode());
		lList.add(InstrumentBean.Status.Counter_Approved.getCode());
		lList.add(InstrumentBean.Status.Counter_Rejected.getCode());
		lList.add(InstrumentBean.Status.Counter_Returned.getCode());
		lList.add(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Pending.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Return.getCode());
		return lList;
	}

	private static List<String> getDisplayFilterListCounter() {
		List<String> lList = new ArrayList<String>();
		lList.add(InstrumentBean.Status.Checker_Approved.getCode());
		lList.add(InstrumentBean.Status.Counter_Approved.getCode());
		lList.add(InstrumentBean.Status.Counter_Rejected.getCode());
		lList.add(InstrumentBean.Status.Counter_Returned.getCode());
		lList.add(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Pending.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Return.getCode());
		return lList;
	}

	private static List<String> getDisplayFilterListCounterChecker() {
		List<String> lList = new ArrayList<String>();
		lList.add(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Pending.getCode());
		lList.add(InstrumentBean.Status.Counter_Checker_Return.getCode());
		return lList;
	}

	private static List<String> getDisplayHistoryFilterList() {
		List<String> lList = new ArrayList<String>();
		lList.add(InstrumentBean.Status.Factored.getCode());
		lList.add(InstrumentBean.Status.Leg_1_Settled.getCode());
		lList.add(InstrumentBean.Status.Leg_1_Failed.getCode());
		lList.add(InstrumentBean.Status.Leg_2_Settled.getCode());
		lList.add(InstrumentBean.Status.Leg_2_Failed.getCode());
		lList.add(InstrumentBean.Status.Leg_3_Generated.getCode());
		lList.add(InstrumentBean.Status.Leg_3_Settled.getCode());
		lList.add(InstrumentBean.Status.Leg_3_Failed.getCode());
		lList.add(InstrumentBean.Status.Expired.getCode());
		return lList;
	}

	private static List<String> getValidStatusList() {
		List<String> lList = getDisplayHistoryFilterList();
		lList.add(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode());
		return lList;
	}

	//
	public InstrumentBO() {
		super();
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
		instrumentWorkFlowDAO = new GenericDAO<InstrumentWorkFlowBean>(InstrumentWorkFlowBean.class);
		factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
		companyDetailDao = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
		ciGroupBeanDAO = new GenericDAO<CIGroupBean>(CIGroupBean.class);
		appUserBO = new AppUserBO();
		emailGeneratorBO = new EmailGeneratorBO();
		purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
		purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
		factoredBeanDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
		ewayBillDetailsDAO = new GenericDAO<EwayBillDetailsBean>(EwayBillDetailsBean.class);
		instrumentCreationKeysDAO = new GenericDAO<InstrumentCreationKeysBean>(InstrumentCreationKeysBean.class);
		ciGroupDAO = new GenericDAO<CIGroupBean>(CIGroupBean.class);
		billingLocationBO = new BillingLocationBO();
		// gstnMandateBO = new GstnMandateBO();
	}

	public InstrumentBean findBeanByInstNumber(Connection pConnection, InstrumentBean pFilterBean) throws Exception {
		InstrumentBean lFilterBean = new InstrumentBean();
		lFilterBean.setInstNumber(pFilterBean.getInstNumber());
		lFilterBean.setSupplier(pFilterBean.getSupplier());
		lFilterBean.setPurchaser(pFilterBean.getPurchaser());
		InstrumentBean lInstrumentBean = instrumentDAO.findBean(pConnection, lFilterBean);
		if (lInstrumentBean == null)
			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		if (lInstrumentBean.getInstCount() == null) {
			lInstrumentBean.setInstCount(getChildInstrumentCount(pConnection, lInstrumentBean));
		}
		return lInstrumentBean;
	}

	public InstrumentBean findBean(ExecutionContext pExecutionContext, InstrumentBean pFilterBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		return findBean(lConnection, pFilterBean);
	}

	public InstrumentBean findBean(Connection pConnection, InstrumentBean pFilterBean) throws Exception {
		InstrumentBean lInstrumentBean = instrumentDAO.findByPrimaryKey(pConnection, pFilterBean);
		if (lInstrumentBean == null)
			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		if (lInstrumentBean.getInstCount() == null) {
			lInstrumentBean.setInstCount(getChildInstrumentCount(pConnection, lInstrumentBean));
		}
		if (TredsHelper.getInstance().supportsInstrumentKeys(lInstrumentBean.getPurchaser())) {
			InstrumentCreationKeysBean lInstrumentCreationKeysBean = new InstrumentCreationKeysBean();
			lInstrumentCreationKeysBean.setInId(lInstrumentBean.getId());
			List<InstrumentCreationKeysBean> lICKList = instrumentCreationKeysDAO.findList(pConnection, lInstrumentCreationKeysBean);
			List<String> lKeyList = lICKList.stream().map(pKeyBean -> pKeyBean.getKey()).collect(Collectors.toList());
			lInstrumentBean.setInstrumentCreationKeysList(lKeyList);
		}
		//updateFieldsFromCounterUpdate(lInstrumentBean, null);
		return lInstrumentBean;
	}

	public void updateFieldsFromCounterUpdate(InstrumentBean pInstrumentBean, IAppUserBean pAppUserBean) {
		try {
			if(pAppUserBean!=null && pInstrumentBean!=null) {
				if(pAppUserBean.getDomain().equals(pInstrumentBean.getCounterEntity()) &&
					pInstrumentBean.getPurchaser().equals(pInstrumentBean.getCounterEntity())) {
					updateAndValidateFieldsFromCounterUpdate(pInstrumentBean);				
				}
			}
		}catch(Exception lEx) {
			logger.error("Error in updateFieldsFromCounterUpdate, skipping error. ",lEx);
		}
	}
	public void updateAndValidateFieldsFromCounterUpdate(InstrumentBean pInstrumentBean) throws Exception {
		AppEntityBean lAEBean = null;
			if(pInstrumentBean!=null && pInstrumentBean.hasCounterUpdateFields()) {
				if(InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus())) {
					lAEBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
					if(lAEBean!=null) {
						AppEntityPreferenceBean lAEPrefBean =  lAEBean.getPreferences();
						if(lAEPrefBean!=null && CommonAppConstants.Yes.Yes.equals(lAEPrefBean.getCav())) {
			        	if(pInstrumentBean.updateFieldsFromCounterUpdate()) {
							setUpdatedDueDates(pInstrumentBean);
							updateNetAmount(pInstrumentBean, pInstrumentBean.getIsAllowedToRecalculateFromPercent(), true, true);
						}
					}
				}
			}
		}
	}

	private Long getChildInstrumentCount(Connection pConnection, InstrumentBean pInstrumentBean) {
		int lCount = 0;
		if (pInstrumentBean.getInstCount() == null) {
			if (CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())) {
				String lSql = "SELECT Count(*) FROM Instruments WHERE InRecordVersion > 0 AND INGroupINId = " + pInstrumentBean.getId();
				Statement lStatement = null;
				ResultSet lResultSet = null;
				try {
					try {
						lStatement = pConnection.createStatement();
						lStatement.setMaxRows(1);
						logger.debug(lSql);
						lResultSet = lStatement.executeQuery(lSql);
						if (lResultSet.next()) {
							lCount = lResultSet.getInt(1);
						}
					} catch (Exception e) {
					} finally {
						if (lResultSet != null)
							lResultSet.close();
						if (lStatement != null)
							lStatement.close();
					}
				} catch (Exception lEx) {

				}
			}
		}
		return new Long(lCount);
	}

	public List<InstrumentBean> findListMaker(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, List<String> pColumnList, AppUserBean pUserBean, Boolean pShowHistory, int pRecordCount) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT ");
		String lDBColumnNames = instrumentDAO.getDBColumnNameCsv(null);
		lSql.append(lDBColumnNames += ",a.CLNAME \"INPurLocation\" ,b.CLNAME \"INSupLocation\" ");
		lSql.append(" FROM INSTRUMENTS,COMPANYLOCATIONS a,COMPANYLOCATIONS b ");
		lSql.append(" WHERE a.clid= INPURCLID");
		lSql.append(" AND b.clid= INSUPCLID ");
		if (!(Yes.Yes.equals(pFilterBean.getGroupFlag())) && pFilterBean.getGroupInId() == null) {
			lSql.append(" AND INGROUPINID IS NULL ");
		}
		if (!pShowHistory) {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_LISTING_FILTER_MAKER)).append(" ) ");
		} else {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_HISTORY_FILTER)).append(" ) ");
		}
		instrumentDAO.appendAsSqlFilter(lSql, pFilterBean, false);
		appendMakerFilter(lSql, pUserBean);
		return instrumentDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), pRecordCount);
	}

	public List<InstrumentBean> findListChecker(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, List<String> pColumnList, AppUserBean pUserBean, Boolean pShowHistory, int pRecordCount) throws Exception {
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT ");
		String lDBColumnNames = instrumentDAO.getDBColumnNameCsv(null);
		lSql.append(lDBColumnNames += ",a.CLNAME \"INPurLocation\" ,b.CLNAME \"INSupLocation\" ");
		lSql.append(" FROM INSTRUMENTS,COMPANYLOCATIONS a,COMPANYLOCATIONS b ");
		lSql.append(" WHERE a.clid= INPURCLID");
		lSql.append(" AND b.clid= INSUPCLID ");
		if (!(Yes.Yes.equals(pFilterBean.getGroupFlag())) && pFilterBean.getGroupInId() == null) {
			lSql.append(" AND INGROUPINID IS NULL ");
		}
		if (!pShowHistory) {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_LISTING_FILTER_CHECKER)).append(" ) ");
		} else {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_HISTORY_FILTER)).append(" ) ");
		}
		if (lAppEntityBean.hasHierarchicalChecker(AppConstants.INSTRUMENT_CHECKER)) {
			if (pUserBean.getInstLevel() != null && pUserBean.getInstLevel() > 0) {
				lSql.append(" AND ( INMKRCHKLEVEL IS NULL OR INMKRCHKLEVEL = ").append(pUserBean.getInstLevel()).append(" ) ");
			}
		}
		instrumentDAO.appendAsSqlFilter(lSql, pFilterBean, false);
		appendCheckerFilter(lSql, pUserBean);
		return instrumentDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), pRecordCount);
	}

	public List<InstrumentBean> findListCounter(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, List<String> pColumnList, AppUserBean pUserBean, Boolean pShowHistory, int pRecordCount, boolean pFilterExpired) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		lSql.append(" SELECT ");
		String lDBColumnNames = instrumentDAO.getDBColumnNameCsv(null);
		lSql.append(lDBColumnNames += ",a.CLNAME \"INPurLocation\" ,b.CLNAME \"INSupLocation\" ");
		lSql.append(" FROM INSTRUMENTS,COMPANYLOCATIONS a,COMPANYLOCATIONS b ");
		lSql.append(" WHERE a.clid= INPURCLID");
		lSql.append(" AND b.clid= INSUPCLID ");
		if (pFilterExpired) {
			lSql.append(" AND TO_DATE(TO_CHAR(ININSTDUEDATE,'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ");
			lSql.append(lDbHelper.formatDate(pFilterBean.getFromDate()));
			lSql.append(" AND ");
			lSql.append(lDbHelper.formatDate(pFilterBean.getToDate()));
			lSql.append(" AND ( ");
			lSql.append(" ( INCounterEntity = ").append(DBHelper.getInstance().formatString(pUserBean.getDomain()));
			// check flag
			if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
				lSql.append(" AND (INCounterAUId IS NULL OR INCounterAUId = ").append(pUserBean.getId()).append(")");
			}
			lSql.append(" ))");
		} else {
			if (!(Yes.Yes.equals(pFilterBean.getGroupFlag())) && pFilterBean.getGroupInId() == null) {
				lSql.append(" AND INGROUPINID IS NULL ");
			}
			if (!pShowHistory) {
				lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_LISTING_FILTER_COUNTER)).append(" ) ");
			} else {
				lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_HISTORY_FILTER)).append(" ) ");
			}
		}
		//
		if(pFilterBean.getFromFilterGoodsAcceptDate()!=null) {
			lSql.append(" AND inGoodsAcceptDate >= ").append(DBHelper.getInstance().formatDate(pFilterBean.getFromFilterGoodsAcceptDate()));
		}
		if(pFilterBean.getToFilterGoodsAcceptDate()!=null) {
			lSql.append(" AND inGoodsAcceptDate <= ").append(DBHelper.getInstance().formatDate(pFilterBean.getToFilterGoodsAcceptDate()));
		}
		if(pFilterBean.getFromFilterInstDate()!=null) {
			lSql.append(" AND inInstDate >= ").append(DBHelper.getInstance().formatDate(pFilterBean.getFromFilterInstDate()));
		}
		if(pFilterBean.getToFilterInstDate()!=null) {
			lSql.append(" AND inInstDate <= ").append(DBHelper.getInstance().formatDate(pFilterBean.getToFilterInstDate()));
		}
		if(pFilterBean.getFromFilterInstDueDate()!=null) {
			lSql.append(" AND inInstDueDate >= ").append(DBHelper.getInstance().formatDate(pFilterBean.getFromFilterInstDueDate()));
		}
		if(pFilterBean.getToFilterInstDueDate()!=null) {
			lSql.append(" AND inInstDueDate <= ").append(DBHelper.getInstance().formatDate(pFilterBean.getToFilterInstDueDate()));
		}
		//
		instrumentDAO.appendAsSqlFilter(lSql, pFilterBean, false);
		if (!pFilterExpired) {
			appendCounterFilter(lSql, pUserBean, pFilterBean.getGroupInId());
		}
		 List<InstrumentBean> lInstrumentList = instrumentDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), pRecordCount);
		 if(lInstrumentList!=null) {
			 for (InstrumentBean lInstrumentBean : lInstrumentList) {
				 updateFieldsFromCounterUpdate(lInstrumentBean, pUserBean);
			}
		 }
		 return lInstrumentList;
	}

	public List<InstrumentBean> findListCounterChecking(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, List<String> pColumnList, AppUserBean pUserBean, Boolean pShowHistory, int pRecordCount, Date pDateFilter) throws Exception {
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT ");
		String lDBColumnNames = instrumentDAO.getDBColumnNameCsv(null);
		lSql.append(lDBColumnNames += ",a.CLNAME \"INPurLocation\" ,b.CLNAME \"INSupLocation\" ");
		lSql.append(" FROM INSTRUMENTS,COMPANYLOCATIONS a,COMPANYLOCATIONS b ");
		if (!Objects.isNull(pDateFilter)) {
			lSql.append(" , INSTRUMENTWORKFLOW ");
		}
		lSql.append(" WHERE a.clid= INPURCLID");
		if (!Objects.isNull(pDateFilter)) {
			lSql.append(" AND IWFINID=INID ");
			lSql.append(" AND IWFSTATUS = ").append(InstrumentBean.Status.Counter_Checker_Pending.getCode());
			lSql.append(" AND INCOUNTERCHECKERAUID=IWFAUID ");
		}
		lSql.append(" AND b.clid= INSUPCLID ");
		if (!(Yes.Yes.equals(pFilterBean.getGroupFlag())) && pFilterBean.getGroupInId() == null) {
			lSql.append(" AND INGROUPINID IS NULL ");
		}
		if (!pShowHistory) {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_LISTING_FILTER_COUNTERCHECKER)).append(" ) ");
		} else {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(DISPLAY_HISTORY_FILTER)).append(" ) ");
		}
		if (lAppEntityBean.hasHierarchicalChecker(AppConstants.INSTRUMENT_COUNTER_CHECKER)) {
			if (pUserBean.getInstCntrLevel() != null && pUserBean.getInstCntrLevel() > 0) {
				lSql.append(" AND ( INCNTCHKLEVEL IS NULL OR INCNTCHKLEVEL = ").append(pUserBean.getInstCntrLevel()).append(" ) ");
			}
		}
		instrumentDAO.appendAsSqlFilter(lSql, pFilterBean, false);
		appendCounterCheckerFilter(lSql, pUserBean);
		List<InstrumentBean> lInstrumentList =  instrumentDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), pRecordCount);
		if(lInstrumentList!=null) {
			for (InstrumentBean lInstrumentBean : lInstrumentList) {
				updateFieldsFromCounterUpdate(lInstrumentBean, pUserBean);
			}
		}
		return lInstrumentList;
	}

	public void appendMakerFilter(StringBuilder pSql, AppUserBean pUserBean) {
		String lDomain = pUserBean.getDomain();
		if (pUserBean != null && AppConstants.DOMAIN_PLATFORM.equals(lDomain))
			return;
		pSql.append(" AND ( ");
		pSql.append(" ( INMakerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		if (pUserBean.getType() != AppUserBean.Type.Admin) {
			pSql.append(" AND ( INMakerAUId = ").append(pUserBean.getId());
			pSql.append(" OR INMakerAUId = 0 ) ");
		}
		pSql.append(" ) ");
		pSql.append(" OR ( INOwnerEntity = INMakerEntity and INOwnerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
			pSql.append(" AND ( INOWNERAUID IS NULL OR INOWNERAUID = ").append(TredsHelper.getInstance().getOwnerAuId(pUserBean)).append(" ) ");
		}
		pSql.append("  ) ");
		pSql.append("  ) ");

		if (TredsHelper.getInstance().checkAccessToLocations(pUserBean)) {
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lDomain);
				if (lAppEntityBean.isPurchaser()) {
					pSql.append(" and INPurClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				} else if (lAppEntityBean.isSupplier()) {
					pSql.append(" and INSupClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				}
			} catch (MemoryDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pSql.append(" and INPurClId in (0) AND and INSupClId in (0)");
			}
		}
	}

	public void appendCheckerFilter(StringBuilder pSql, AppUserBean pUserBean) {
		String lDomain = pUserBean.getDomain();
		if (pUserBean != null && AppConstants.DOMAIN_PLATFORM.equals(lDomain))
			return;
		pSql.append(" AND ( ");
		pSql.append(" INMakerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		pSql.append(" OR ( INOwnerEntity = INMakerEntity AND INOwnerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
			pSql.append(" AND ( INOWNERAUID IS NULL OR INOWNERAUID = ").append(TredsHelper.getInstance().getOwnerAuId(pUserBean)).append(" ) ");
		}
		pSql.append("  ) ");
		pSql.append("  ) ");

		if (pUserBean.getType() != AppUserBean.Type.Admin) {
			pSql.append(" AND EXISTS (SELECT MCMId FROM MakerCheckerMap WHERE MCMRecordVersion > 0 AND MCMMakerId = INMakerAUId AND MCMCheckerId = ");
			pSql.append(pUserBean.getId()).append(")");

			pSql.append(" AND (INCheckerAUId IS NULL OR INCheckerAUId = ").append(pUserBean.getId()).append(" )");
		}
		pSql.append(" AND INStatus != ").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Drafting.getCode()));
		/*
		 * //For Disp of clubbed Instruments pSql.append(" AND ( ");
		 * pSql.append(" ( INGROUPFLAG IS NULL ) "); pSql.append(
		 * " OR ( ( INGROUPFLAG = "
		 * ).append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.
		 * Yes.getCode())).append(" ) "); pSql.append(
		 * " AND ( INSTATUS NOT IN ( 'DRFT' ) ) ) " ); pSql.append(" ) "); //
		 */
		if (TredsHelper.getInstance().checkAccessToLocations(pUserBean)) {
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lDomain);
				if (lAppEntityBean.isPurchaser()) {
					pSql.append(" and INPurClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				} else if (lAppEntityBean.isSupplier()) {
					pSql.append(" and INSupClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				}
			} catch (MemoryDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pSql.append(" and INPurClId in (0) AND and INSupClId in (0)");
			}
		}
		//
		if (pUserBean.hasUserLimit()) {
			if (pUserBean.getMinUserLimit() != null) {
				pSql.append(" AND INAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if (pUserBean.getMaxUserLimit() != null) {
				pSql.append(" AND INAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}
	}

	public void appendCounterFilter(StringBuilder pSql, AppUserBean pUserBean) {
		appendCounterFilter(pSql, pUserBean, null);
	}

	public void appendCounterFilter(StringBuilder pSql, AppUserBean pUserBean, Long pGroupInId) {
		String lDomain = pUserBean.getDomain();
		if (pUserBean != null && AppConstants.DOMAIN_PLATFORM.equals(lDomain))
			return;
		pSql.append(" AND ( ");
		pSql.append(" ( INCounterEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		// check flag
		if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
			pSql.append(" AND (INCounterAUId IS NULL OR INCounterAUId = ").append(pUserBean.getId()).append(")");
		}
		// group instruments are not updated with the auid
		// so while downloading the data for the grouped instruments we need to
		// exclude the clause below.
		if (pGroupInId == null) {
			pSql.append(" AND ( INCounterAUId IS NOT NULL OR INStatus IN ( ").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Counter_Checker_Return.getCode())).append(" , ").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Checker_Approved.getCode())).append(" ) ) ");
		}
		pSql.append(" ) ");
		pSql.append(" OR( INOwnerEntity = INCounterEntity and INOwnerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
			pSql.append(" AND ( INOWNERAUID IS NULL OR INOWNERAUID = ").append(TredsHelper.getInstance().getOwnerAuId(pUserBean)).append(" ) ");
		}
		pSql.append("  ) ");
		pSql.append("  ) ");

		// For Disp of clubbed Instruments
		pSql.append(" AND ( ");
		pSql.append(" ( INGROUPFLAG IS NULL ) ");
		pSql.append(" OR ( ( INGROUPFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode())).append(" ) ");
		pSql.append(" AND ( INSTATUS NOT IN ( 'DRFT' , 'SUB' , 'CHKRET' , 'CHKREJ' , 'COURET' ) ) ) ");
		pSql.append(" ) ");
		//

		if (TredsHelper.getInstance().checkAccessToLocations(pUserBean)) {
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lDomain);
				if (lAppEntityBean.isPurchaser()) {
					pSql.append(" and INPurClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				} else if (lAppEntityBean.isSupplier()) {
					pSql.append(" and INSupClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				}
			} catch (MemoryDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pSql.append(" and INPurClId in (0) AND and INSupClId in (0)");
			}
		}
		if (pUserBean.hasUserLimit()) {
			if (pUserBean.getMinUserLimit() != null) {
				pSql.append(" AND INAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if (pUserBean.getMaxUserLimit() != null) {
				pSql.append(" AND INAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}
	}

	public void appendCounterCheckerFilter(StringBuilder pSql, AppUserBean pUserBean) {
		appendCounterCheckerFilter(pSql, pUserBean, null);
	}

	public void appendCounterCheckerFilter(StringBuilder pSql, AppUserBean pUserBean, Long pGroupInId) {
		String lDomain = pUserBean.getDomain();
		if (pUserBean != null && AppConstants.DOMAIN_PLATFORM.equals(lDomain))
			return;
		pSql.append(" AND ( ");
		pSql.append(" INCounterEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		pSql.append(" OR ( INOwnerEntity = INCounterEntity AND INOwnerEntity = ").append(DBHelper.getInstance().formatString(lDomain));
		if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
			pSql.append(" AND ( INOWNERAUID IS NULL OR INOWNERAUID = ").append(TredsHelper.getInstance().getOwnerAuId(pUserBean)).append(" ) ");
		}
		pSql.append("  ) ");
		pSql.append("  ) ");

		if (pUserBean.getType() != AppUserBean.Type.Admin) {
			pSql.append(" AND EXISTS (SELECT MCMId FROM MakerCheckerMap WHERE MCMRecordVersion > 0 AND MCMMakerId = INCounterAUId AND MCMCheckerId = ");
			pSql.append(pUserBean.getId()).append(")");
			pSql.append(" AND (INCounterCheckerAUId IS NULL OR INCounterCheckerAUId = ").append(pUserBean.getId()).append(" )");
		}
		pSql.append(" AND ( INSTATUS NOT IN ( 'DRFT' , 'SUB' , 'CHKRET' , 'CHKREJ' , 'COURET' )  ) ");
		/*
		 * //For Disp of clubbed Instruments pSql.append(" AND ( ");
		 * pSql.append(" ( INGROUPFLAG IS NULL ) "); pSql.append(
		 * " OR ( ( INGROUPFLAG = "
		 * ).append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.
		 * Yes.getCode())).append(" ) "); pSql.append(
		 * " AND ( INSTATUS NOT IN ( 'DRFT' ) ) ) " ); pSql.append(" ) "); //
		 */
		if (TredsHelper.getInstance().checkAccessToLocations(pUserBean)) {
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lDomain);
				if (lAppEntityBean.isPurchaser()) {
					pSql.append(" and INPurClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				} else if (lAppEntityBean.isSupplier()) {
					pSql.append(" and INSupClId in (").append(TredsHelper.getInstance().getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(")");
				}
			} catch (MemoryDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pSql.append(" and INPurClId in (0) AND and INSupClId in (0)");
			}
		}
		//
		if (pUserBean.hasUserLimit()) {
			if (pUserBean.getMinUserLimit() != null) {
				pSql.append(" AND INAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if (pUserBean.getMaxUserLimit() != null) {
				pSql.append(" AND INAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}

	}

	public void save(ExecutionContext pExecutionContext, InstrumentBean pInstrumentBean, AppUserBean pUserBean, boolean pNew, boolean pFileUpload, boolean pSubmit, String pRemarks, boolean pApiCall, String pLoginKey, Map<String, Object> pDiffMap) throws Exception {
		pExecutionContext.setAutoCommit(false);
		saveWithoutCommit(pExecutionContext, pInstrumentBean, pUserBean, pNew, pFileUpload, pSubmit, pRemarks, pApiCall, pLoginKey,pDiffMap);
		pExecutionContext.commit();
	}

	public void saveWithoutCommit(ExecutionContext pExecutionContext, InstrumentBean pInstrumentBean, AppUserBean pUserBean, boolean pNew, boolean pFileUpload, boolean pSubmit, String pRemarks, boolean pApiCall, String pLoginKey, Map<String, Object> pDiffMap) throws Exception {
		// TODO: the API user will have to send the currency, costBearingType,
		// hairCutPercentage, cashDiscountPercentage
		// the API user will have to calculate haircut and send in adjAmount and
		// also calculate cashdiscount and send in cashDiscountValue
		// API user can pass the Location names in supLocation and purLocation
		// API user can pass the Counters Ref Code provided in the BuyerSeller
		// Link instead of the CounterCode

		boolean lUpdateCostColumns = false;
		Connection lConnection = pExecutionContext.getConnection();
		InstrumentBean lOldInstrumentBean = null;
		// TODO: Temporarily commented.
		if (false && pNew && pInstrumentBean.getEbdId() != null) {
			MonetagoInvoiceInfoBean lMonetagoInvoiceInfoBean = new MonetagoInvoiceInfoBean();
			MonetagoEwaybillInfoBean lMonetagoEwaybillInfoBean = new MonetagoEwaybillInfoBean();
			InstAuthJerseyClient lInstAuthJerseyClient = InstAuthJerseyClient.getInstance();
			EwayBillDetailsBean lEwayBillDetailsBean = new EwayBillDetailsBean();
			lEwayBillDetailsBean.setId(pInstrumentBean.getEbdId());
			lEwayBillDetailsBean = ewayBillDetailsDAO.findBean(lConnection, lEwayBillDetailsBean);
			if (lEwayBillDetailsBean != null) {
				lMonetagoEwaybillInfoBean = lInstAuthJerseyClient.getEWayBillData(lEwayBillDetailsBean.getEwayPayload());
				lMonetagoInvoiceInfoBean = lInstAuthJerseyClient.getGstrData(lEwayBillDetailsBean.getGstrPayload());
			} else {

			}
			String lPoNumber = pInstrumentBean.getPoNumber();
			Date lPoDate = pInstrumentBean.getPoDate();
			InstrumentBean lMonetagoInstrumentBean = new InstrumentBean();
			instrumentDAO.getBeanMeta().copyBean(pInstrumentBean, lMonetagoInstrumentBean);
			pInstrumentBean = new InstrumentBean();
			GstnMandateBO lGstnMandateBO = new GstnMandateBO();
			pInstrumentBean = lGstnMandateBO.getInstrumentBeanWithEwayBillId(pExecutionContext, lEwayBillDetailsBean, lMonetagoInvoiceInfoBean, lMonetagoEwaybillInfoBean);
			pInstrumentBean.setPoNumber(lPoNumber);
			pInstrumentBean.setPoDate(lPoDate);
		}

		validateGroupInst(lConnection, pInstrumentBean, pApiCall);

		AppEntityBean lUserEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());

		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = validatePurchaserSupplierLink(lConnection, pInstrumentBean, pUserBean, pNew);

		AppEntityBean lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
		AppEntityBean lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());

		boolean lSaveAndSubmit = pSubmit && !pFileUpload;

		validateAndSetSupplierLocationDetails(lConnection, pInstrumentBean, pUserBean);

		validateAndSetPurchaserLocationDetails(lConnection, pInstrumentBean, pUserBean);

		validateUniqueInvoiceNumber(lConnection, pInstrumentBean, pNew, false);

		TredsHelper.getInstance().validateUserLimit(pUserBean, pInstrumentBean.getAmount());
		//
		// validate dates
		validateDates(pInstrumentBean);
		//
		validateInstrumentCreationKeys(lConnection, pInstrumentBean);
		// get purchaser supplier link
		if (lPurchaserSupplierLinkBean == null) {
			PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
			lPSLFilterBean.setPurchaser(pInstrumentBean.getPurchaser());
			lPSLFilterBean.setSupplier(pInstrumentBean.getSupplier());
			lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
			lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(lConnection, lPSLFilterBean);
			if (lPurchaserSupplierLinkBean == null)
				throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");
			checkPlatformStatus(lPurchaserSupplierLinkBean);
		}
		// only new instruments cannot be created, existing in draft can be
		// modified.
		if ((pNew || pInstrumentBean.getId() == null) && CommonAppConstants.Yes.Yes.equals(lPurchaserSupplierLinkBean.getInWorkFlow())) {
			throw new CommonBusinessException("Purchaser Supplier link in work flow, cannot add new instrument.");
		}
		lPurchaserSupplierLinkBean.populateNonDatabaseFields();
		// TODO: keep 2 copies of purchaser supplier link for Purchaser since he
		// can change the same, so we will have to rePopulate the database
		// fields from the logic inside the PSLinkBean
		PurchaserSupplierLinkBean lModifiedPSLinkBean = new PurchaserSupplierLinkBean();
		purchaserSupplierLinkDAO.getBeanMeta().copyBean(lPurchaserSupplierLinkBean, lModifiedPSLinkBean, BeanMeta.FIELDGROUP_UPDATE, null);
		// validation to check split is not interchanged
		if (pInstrumentBean.getCreditPeriod() == null)
			pInstrumentBean.setCreditPeriod(lPurchaserSupplierLinkBean.getCreditPeriod());
		if (pInstrumentBean.getExtendedCreditPeriod() == null)
			pInstrumentBean.setExtendedCreditPeriod(lPurchaserSupplierLinkBean.getExtendedCreditPeriod());
		//
		lUpdateCostColumns = pNew || pFileUpload;
		
		// autoAccept and autoAcceptableBidTypes - are the only things open for
		// change.
		if (pNew && !pFileUpload && (lUserEntityBean.isPurchaser() || lUserEntityBean.isPurchaserAggregator())) {
			if (lUserEntityBean.isPurchaser()) {
				if (pInstrumentBean.getAutoAccept() != null)
					lModifiedPSLinkBean.setAutoAccept(pInstrumentBean.getAutoAccept());
				if (pInstrumentBean.getAutoAcceptableBidTypes() != null)
					lModifiedPSLinkBean.setAutoAcceptableBidTypes(pInstrumentBean.getAutoAcceptableBidTypes());
			} else if (lUserEntityBean.isPurchaserAggregator()) {
				// TODO:what to do over here
			}
		}
		lModifiedPSLinkBean.populateDatabaseFields();
		//
//		if (!pFileUpload && (!pApiCall || pInstrumentBean.getCostBearingType() != null) && !lPurchaserSupplierLinkBean.isCostBearerChangeValid(pInstrumentBean.getCostBearingType()))
//			throw new CommonBusinessException("Please verify cost bearing type against the Link defined.");
		if (lUpdateCostColumns) {
			updateCostColumns(pInstrumentBean, lModifiedPSLinkBean, pApiCall);
		}
		
		if(!pFileUpload && !pApiCall) {
			if( pInstrumentBean.getCostBearingType() != null ) {
				if(!lPurchaserSupplierLinkBean.isCostBearerChangeValid(pInstrumentBean.getCostBearingType())) {
					throw new CommonBusinessException("Please verify cost bearing type against the Link defined.");
				}
			}else {
				throw new CommonBusinessException("Please verify cost bearing type against the Link defined.");
			}
		}
		//

		pInstrumentBean.setPurchaserRef(lModifiedPSLinkBean.getSupplierPurchaserRef());
		pInstrumentBean.setSupplierRef(lModifiedPSLinkBean.getPurchaserSupplierRef());
		//
		pInstrumentBean.setSalesCategory(lSupplierEntityBean.getSalesCategory());

		HashMap<String, Object> lMsmeSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructureHash(AppConstants.REGISTRY_MSMESETTINGS).get(lSupplierEntityBean.getMsmeStatus());
		Long lMinUsance = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_MINUSANCE);
		//to compute statutory due date
		Long lMSMECreditPeriod = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_CREDITPERIOD);// to
																								// compute
																								// statutory
																								// due
																								// date
		Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();

		boolean lCreditPeriodUpdated = updateCreditPeriod(pInstrumentBean, lMSMECreditPeriod);
		logger.info("Fetching DueDates for inid : "+(pInstrumentBean.getId()!=null?pInstrumentBean.getId():""));
		Date[] lDates = getDueDates(pInstrumentBean.getGoodsAcceptDate(), pInstrumentBean.getCreditPeriod(), pInstrumentBean.getExtendedCreditPeriod(), lMSMECreditPeriod, pInstrumentBean.getEnableExtension());
		if (!lCreditPeriodUpdated && pInstrumentBean.getInstDueDate() != null) {
			if (!pInstrumentBean.getInstDueDate().equals(lDates[DATES_INSTRUMENTDUEDATE]))
				throw new CommonBusinessException("Mismatch in invoice due date");
		} else {
			if (!lCreditPeriodUpdated)
				pInstrumentBean.setInstDueDate(lDates[DATES_INSTRUMENTDUEDATE]);
			if (lCreditPeriodUpdated)
				pInstrumentBean.setCreditPeriod(OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getInstDueDate(), pInstrumentBean.getGoodsAcceptDate()));
		}
		if (!lCreditPeriodUpdated && pInstrumentBean.getStatDueDate() != null) {
			if (!pInstrumentBean.getStatDueDate().equals(lDates[DATES_STATUTORYDUEDATE]))
				throw new CommonBusinessException("Mismatch in statutory due date");
		} else
			pInstrumentBean.setStatDueDate(lDates[DATES_STATUTORYDUEDATE]);
		pInstrumentBean.setMaturityDate(lDates[DATES_MATURITYDATE]);
		// max factoring end date
		Date lEndDate = lDates[DATES_STATUTORYDUEDATE].compareTo(lDates[DATES_MATURITYDATE]) < 0 ? lDates[DATES_STATUTORYDUEDATE] : lDates[DATES_MATURITYDATE];
		// min of maturity and stat due date
		lEndDate = getEndDate(pInstrumentBean, lMinUsance, lEndDate);

		pInstrumentBean.setFactorMaxEndDateTime(new Timestamp(lEndDate.getTime() + AppConstants.DAY_IN_MILLIS - 1));
		logger.info(" FactorMaxEndDateTime  :" + pInstrumentBean.getFactorMaxEndDateTime());
		if (pInstrumentBean.getFactorMaxEndDateTime().compareTo(lCurrentDate) < 0) {
			throw new CommonBusinessException("Instrument is not factorable since maturity[" + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_MATURITYDATE]) + "]/statutory[" + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_STATUTORYDUEDATE]) + "] due date does not meet the minimum usance criteria of " + lMinUsance);
		}
		pInstrumentBean.setExtendedDueDate(lDates[DATES_EXTENDEDDUEDATE]);
		pInstrumentBean.setSupMsmeStatus(lSupplierEntityBean.getMsmeStatus());

		// usance from registry
		if (OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getStatDueDate(), lCurrentDate) < lMinUsance.longValue())
			throw new CommonBusinessException("Instrument does not meet the minumum usance criteria of " + lMinUsance + " days");

		validateCreditPeriod(pInstrumentBean.getCreditPeriod(), pInstrumentBean.getEnableExtension(), pInstrumentBean.getExtendedCreditPeriod());

		// CASH DISCOUNT AND HAIRCUT
		boolean lUploadSubmit = false;
		if (pDiffMap!=null && pDiffMap.containsKey("submit")) {
			lUploadSubmit = Boolean.valueOf((String)pDiffMap.get("submit")).booleanValue();
		}
		
		if (pFileUpload || lUploadSubmit) {
			// set the percentage fields from the b/s link
			pInstrumentBean.setHaircutPercent(lModifiedPSLinkBean.getHaircutPercent());
			pInstrumentBean.setCashDiscountPercent(lModifiedPSLinkBean.getCashDiscountPercent());
		}

		// validate cash discount percentage - any system level value has been
		// modified.
		if (pInstrumentBean.getCashDiscountPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getCashDiscountPercent())) {
			validateCashDiscount(lConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getCashDiscountPercent());
		}

		updateNetAmount(pInstrumentBean, pInstrumentBean.getIsAllowedToRecalculateFromPercent(), (pApiCall || pFileUpload));

		if (lUserEntityBean.isPurchaserAggregator()) {
			pInstrumentBean.setMakerEntity(pInstrumentBean.getPurchaser());
		} else {
			pInstrumentBean.setMakerEntity(lUserEntityBean.getCode());
		}
		if(Yes.Yes.equals(pInstrumentBean.getInstVisibleToMaker())) {
			pInstrumentBean.setMakerAuId(new Long(0));
		}else {
		pInstrumentBean.setMakerAuId(pUserBean.getId());
		}
		// if (lUserEntityBean.getCode().equals(lPurchaserEntityBean.getCode())
		// || lUserEntityBean.isPurchaserAggregator()) // purchaser is maker
		// purchaser is maker
		if (lUserEntityBean.getCode().equals(lPurchaserEntityBean.getCode())) 
			pInstrumentBean.setCounterEntity(lSupplierEntityBean.getCode());
		else if (lUserEntityBean.getCode().equals(lSupplierEntityBean.getCode()))
			pInstrumentBean.setCounterEntity(lPurchaserEntityBean.getCode());
		if (lUserEntityBean.getCode().equals(pInstrumentBean.getCounterEntity()))
			pInstrumentBean.setCounterAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));

		pInstrumentBean.setType(InstrumentBean.Type.Invoice);
		//
		// happens in case of upload of instruments through  files		
		if (pInstrumentBean.getEbdId() == null && CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getInvoiceMandatory()) && StringUtils.isBlank(pInstrumentBean.getInstImage())) 
			throw new CommonBusinessException("Invoice image not attached.");
		//
		if (pNew) {
			IStatsCacheGenerator lInstInvoiceStatCache = StatsCacheFactory.getInstance().getStatsCacheGenerator(StatsCacheFactory.STAT_INSTRUMENT_INV);
			lInstInvoiceStatCache.generateAlert(pInstrumentBean);
		}
		if (pNew) {
			//
			if(pInstrumentBean.getIsGemInvoice()) {
				pInstrumentBean.setStatus(InstrumentBean.Status.Checker_Approved);
			}else {
				pInstrumentBean.setStatus(InstrumentBean.Status.Drafting);
			}
			Long lId = TredsHelper.getInstance().getNextId(lConnection, "yyMMdd", "Instruments.id.", 7);
			pInstrumentBean.setId(lId);
			manageInstrumentKeys(lConnection, pInstrumentBean);
			pInstrumentBean.populateDatabaseFields();
			if (lUserEntityBean.isPurchaserAggregator()) {
				// TODO: No check is required for Aggregator as we do not need
				// to set the auid
			} else {
				if (lUserEntityBean.getCode().equals(pInstrumentBean.getOwnerEntity()))
					pInstrumentBean.setOwnerAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
			}
			if (pInstrumentBean.getCfId()!=null && pDiffMap!=null) {
				CustomFieldBO lCustomFieldBO = new CustomFieldBO();
				CustomFieldBean lFieldBean = new CustomFieldBean();
				lFieldBean.setCode(pInstrumentBean.getPurchaser());
				lFieldBean.setId(pInstrumentBean.getCfId());
				lFieldBean = lCustomFieldBO.findBean(lConnection, lFieldBean);
				Map<String,Object> lCustFieldMap = lFieldBean.getConfig();
				Map<String,Object> lCustFieldMapDb = new HashMap<>(); 
				if(lCustFieldMap!=null) {
					ArrayList<Map<String,Object>> lCustFieldList = (ArrayList<Map<String, Object>>) lCustFieldMap.get("inputParams");
					if(lCustFieldList!=null) {
						for (Map<String,Object> lTmpMap : lCustFieldList) {
							if (pDiffMap.containsKey(lTmpMap.get("name"))) {
								lCustFieldMapDb.put((String)lTmpMap.get("name"), pDiffMap.get(lTmpMap.get("name")));
							}
						}
						pInstrumentBean.setCfData(new JsonBuilder(lCustFieldMapDb).toString());
				}
				}
			}
			pInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
			instrumentDAO.insert(lConnection, pInstrumentBean);
			instrumentDAO.insertAudit(lConnection, pInstrumentBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
			if (pInstrumentBean.getEbdId() != null) {
				lConnection.commit();
				ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
				ITextRenderer renderer = new ITextRenderer();
				String lUrl = TredsHelper.getInstance().getApplicationURL() + "gstmandate/instrumentinvoice/" + pInstrumentBean.getId();
				lUrl += "?loginKey=" + pLoginKey;
				System.out.println("PDF URL :" + lUrl);
				renderer.setDocument(lUrl);
				renderer.layout();
				renderer.createPDF(lByteArrayOutputStream);
				lByteArrayOutputStream.close();
				String lFileName = FileUploadHelper.saveFile("Eway_" + pInstrumentBean.getEbdId().toString() + ".pdf", lByteArrayOutputStream.toByteArray(), null, "INSTRUMENTS");
				pInstrumentBean.setInstImage(lFileName);
				instrumentDAO.update(lConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATEEWAYINST);
				instrumentDAO.insertAudit(lConnection, pInstrumentBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
			}
		} else {
			if (pInstrumentBean.getId() == null) {
				lOldInstrumentBean = findBeanByInstNumber(lConnection, pInstrumentBean);
			} else {
				lOldInstrumentBean = findBean(pExecutionContext, pInstrumentBean);
			}
			//
			// TODO RECHECK THE CONDITION
			lSaveAndSubmit = lSaveAndSubmit && !(InstrumentBean.Status.Submitted.equals((lOldInstrumentBean != null ? lOldInstrumentBean.getStatus() : null)));
			//
			if (lUserEntityBean.isPurchaserAggregator()) {
				// TODO: No Need to check maker access since we are already
				// checking Aggregator Purchaser mapping.
			} else {
				checkMakerAccess(lOldInstrumentBean, pUserBean);
			}
			pInstrumentBean.setFileId(lOldInstrumentBean.getFileId());
			InstrumentBean.Status lStatus = lOldInstrumentBean.getStatus();
			if ((lStatus != InstrumentBean.Status.Drafting) && (lStatus != InstrumentBean.Status.Checker_Returned) && (lStatus != InstrumentBean.Status.Counter_Returned) && (lStatus != InstrumentBean.Status.Withdrawn))
				throw new CommonBusinessException("Instrument cannot be modified now.");
			// retain values from old bean. since we are going to do a full
			// update
			pInstrumentBean.setStatus(lStatus);
			pInstrumentBean.setStatusRemarks(lOldInstrumentBean.getStatusRemarks());
			pInstrumentBean.setStatusUpdateTime(lOldInstrumentBean.getStatusUpdateTime());
			/*
			 * pInstrumentBean.setMakerAuId(lOldInstrumentBean.getMakerAuId());
			 * pInstrumentBean.setOwnerEntity(lOldInstrumentBean.getOwnerEntity(
			 * ));
			 * pInstrumentBean.setOwnerAuId(lOldInstrumentBean.getOwnerAuId());
			 */
			pInstrumentBean.setCheckerAuId(lOldInstrumentBean.getCheckerAuId());
			pInstrumentBean.setCounterModifiedFields(null);
			if (pInstrumentBean.getCounterEntity().equals(lOldInstrumentBean.getCounterEntity())) {
				pInstrumentBean.setCounterAuId(lOldInstrumentBean.getCounterAuId());
			}
			pInstrumentBean.populateDatabaseFields();
			if (lUserEntityBean.isPurchaserAggregator()) {
				// TODO: Q. No need to set auid for purchaser aggregator
			} else {
				if (lUserEntityBean.getCode().equals(pInstrumentBean.getOwnerEntity()))
					pInstrumentBean.setOwnerAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
			}
			//
			if (StringUtils.isNotEmpty(lOldInstrumentBean.getMonetagoLedgerId())) {
				if (monetagoFieldModificationCheck(lOldInstrumentBean, pInstrumentBean)) {
					if (MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser())) {
						if (!CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())) {
							Map<String, String> lResult = new HashMap<String, String>();
							String lInfoMessage = "Instrument No :" + pInstrumentBean.getId();
							lResult = MonetagoTredsHelper.getInstance().cancel(lOldInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Unknown, lInfoMessage, pInstrumentBean.getId());
							if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
								pInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
								pInstrumentBean.setMonetagoLedgerId("");
								logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
							} else {
								logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
								throw new CommonBusinessException("Error while cancelling : " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
							}
						}
					}
				}
			}
			// TODO: _PM update the net amount
			// TODO: NEW LINE ADDITION - WHAT TO DO
			// updateNetAmount(pInstrumentBean);
			//
			manageInstrumentKeys(lConnection, pInstrumentBean);
			if (pInstrumentBean.getCfId()!=null && pDiffMap!=null) {
				CustomFieldBO lCustomFieldBO = new CustomFieldBO();
				CustomFieldBean lFieldBean = new CustomFieldBean();
				lFieldBean.setCode(pInstrumentBean.getPurchaser());
				lFieldBean.setId(pInstrumentBean.getCfId());
				lFieldBean = lCustomFieldBO.findBean(lConnection, lFieldBean);
				Map<String,Object> lCustFieldMap = lFieldBean.getConfig();
				Map<String,Object> lCustFieldMapDb = new HashMap<>(); 
				ArrayList<Map<String,Object>> lCustFieldList = (ArrayList<Map<String, Object>>) lCustFieldMap.get("inputParams");
				if(lCustFieldList!=null) {
					for (Map<String,Object> lTmpMap : lCustFieldList) {
						if (pDiffMap.containsKey(lTmpMap.get("name"))) {
							lCustFieldMapDb.put((String)lTmpMap.get("name"), pDiffMap.get(lTmpMap.get("name")));
						}
					}
					pInstrumentBean.setCfData(new JsonBuilder(lCustFieldMapDb).toString());
				}
			}
			if (instrumentDAO.update(lConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATESAVE) == 0)
				throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
			instrumentDAO.getBeanMeta().copyBean(pInstrumentBean, lOldInstrumentBean, BeanMeta.FIELDGROUP_UPDATE, null);
			instrumentDAO.insertAudit(lConnection, lOldInstrumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());
		}
		if (lSaveAndSubmit) {
			// here check the counters limit
			// TODO: to check whether the user is sending for checking or for
			// approval.
			Long lLocationId = null;
			if (pInstrumentBean.getMakerEntity().equals(pInstrumentBean.getPurchaser())) {
				lLocationId = pInstrumentBean.getPurClId();
			} else if (pInstrumentBean.getMakerEntity().equals(pInstrumentBean.getSupplier())) {
				lLocationId = pInstrumentBean.getSupClId();
			}
			TredsHelper.getInstance().validateCheckersLimit(lConnection, pUserBean.getId(), pInstrumentBean.getAmount(), lLocationId);
			// TODO: Checking allow splitting for aggregator
			if (lUserEntityBean.isPurchaser() || lUserEntityBean.isPurchaserAggregator()) {
				YesNo lAllowObliSplitting = null;
				if (lUserEntityBean.isPurchaser()) {
					lAllowObliSplitting = lUserEntityBean.getAllowObliSplitting();
				} else {
					AppEntityBean lPurchEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
					lAllowObliSplitting = lPurchEntityBean.getAllowObliSplitting();
				}
				boolean lAllowSplitting = CommonAppConstants.YesNo.Yes.equals(lAllowObliSplitting);
				boolean lIsGroup = (CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag()));
				validatePurchaserMandate(lConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getNetAmount(), pInstrumentBean.getPurClId(), pInstrumentBean.getInstDueDate(), lAllowSplitting, lIsGroup);
			}

			pInstrumentBean.setStatusRemarks(pRemarks);
			updateMakerStatus(lConnection, (lOldInstrumentBean != null ? lOldInstrumentBean.getStatus() : null), pInstrumentBean, pUserBean);
		}
		// throw new Exception("Testing");
	}

	public void validateGroupInst(Connection pConnection, InstrumentBean pInstrumentBean, boolean pApiCall) throws Exception {
		InstrumentBean lTmpOldInstBean = null;
		if (pApiCall) {
			try {
				lTmpOldInstBean = findBeanByInstNumber(pConnection, pInstrumentBean);
			} catch (Exception lEx) {
				logger.info("Instrument not found : " + pInstrumentBean.getInstNumber());
			}
		} else {
			lTmpOldInstBean = instrumentDAO.findByPrimaryKey(pConnection, pInstrumentBean);
		}
		if (lTmpOldInstBean != null && CommonAppConstants.Yes.Yes.equals(lTmpOldInstBean.getGroupFlag())) {
			throw new CommonBusinessException("Grouped Instrument can't be modified.");
		}
	}

	public PurchaserSupplierLinkBean validatePurchaserSupplierLink(Connection pConnection, InstrumentBean pInstrumentBean, AppUserBean pUserBean, boolean pNew) throws Exception {
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = null;
		//
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		// valid loggedin user (supplier or purchaser)
		AppEntityBean lUserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pUserBean.getDomain() });
		if ((lUserEntityBean == null) || (!lUserEntityBean.isSupplier() && !lUserEntityBean.isPurchaser() && !lUserEntityBean.isPurchaserAggregator()))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		if (lUserEntityBean != null && lUserEntityBean.isPurchaserAggregator()) {
			PurchaserAggregatorBO lPurchaserAggregatorBO = new PurchaserAggregatorBO();
			lPurchaserAggregatorBO.validateMappedEntity(pConnection, pUserBean.getDomain(), pInstrumentBean.getPurchaser());
		}
		//
		if (lUserEntityBean.isPurchaser()) {
			if (StringUtils.isEmpty(pInstrumentBean.getSupplier())) {
				if (StringUtils.isNotEmpty(pInstrumentBean.getSupplierRef())) {
					// get purchaser supplier link
					lPSLFilterBean = new PurchaserSupplierLinkBean();
					lPSLFilterBean.setPurchaser(pInstrumentBean.getPurchaser());
					lPSLFilterBean.setPurchaserSupplierRef(pInstrumentBean.getSupplierRef());
					lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
					lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
					if (lPurchaserSupplierLinkBean == null)
						throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive. Supplier Ref : " + pInstrumentBean.getSupplierRef() + ".");
					//
					//
					pInstrumentBean.setSupplier(lPurchaserSupplierLinkBean.getSupplier());
				}
			}
		} else if (lUserEntityBean.isSupplier()) {
			if (StringUtils.isNotEmpty(pInstrumentBean.getSupplierRef())) {
				if (StringUtils.isNotEmpty(pInstrumentBean.getPurchaserRef())) {
					// get purchaser supplier link
					lPSLFilterBean = new PurchaserSupplierLinkBean();
					lPSLFilterBean.setSupplier(pInstrumentBean.getSupplier());
					lPSLFilterBean.setSupplierPurchaserRef(pInstrumentBean.getPurchaserRef());
					lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
					lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
					if (lPurchaserSupplierLinkBean == null)
						throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive. Purchaser Ref : " + pInstrumentBean.getPurchaserRef() + ".");
					//
					//
					pInstrumentBean.setPurchaser(lPurchaserSupplierLinkBean.getPurchaser());
				}
			}
		}
		if (lPurchaserSupplierLinkBean == null) {
			lPSLFilterBean = new PurchaserSupplierLinkBean();
			lPSLFilterBean.setSupplier(pInstrumentBean.getSupplier());
			lPSLFilterBean.setPurchaser(pInstrumentBean.getPurchaser());
			lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
			lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
			if (lPurchaserSupplierLinkBean == null)
				throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");
		}
		checkPlatformStatus(lPurchaserSupplierLinkBean);
		if (!PurchaserSupplierLinkBean.Status.Active.equals(lPurchaserSupplierLinkBean.getStatus())) {
			throw new CommonBusinessException("Purchaser supplier link not active.");
		}
		if (pNew) {
			if (pInstrumentBean.getPurchaser().equals(pUserBean.getDomain())) {
				if (InstrumentCreation.Supplier.equals(lPurchaserSupplierLinkBean.getInstrumentCreation())) {
					throw new CommonBusinessException("Instrument Creation blocked in purchaser supplier link.");
				}
			} else if (pInstrumentBean.getSupplier().equals(pUserBean.getDomain())) {
				if (InstrumentCreation.Purchaser.equals(lPurchaserSupplierLinkBean.getInstrumentCreation())) {
					throw new CommonBusinessException("Instrument Creation blocked in purchaser supplier link.");
				}
			}
		}

		// valid supplier
		AppEntityBean lSupplierEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pInstrumentBean.getSupplier() });
		if ((lSupplierEntityBean == null) || (!lSupplierEntityBean.isSupplier()) || (lUserEntityBean.isSupplier() && (lUserEntityBean != lSupplierEntityBean)))
			throw new CommonBusinessException("Invalid Supplier code");
		// valid purchaser
		AppEntityBean lPurchaserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pInstrumentBean.getPurchaser() });
		if ((lPurchaserEntityBean == null) || (!lPurchaserEntityBean.isPurchaser()) || (lUserEntityBean.isPurchaser() && (lUserEntityBean != lPurchaserEntityBean)))
			throw new CommonBusinessException("Invalid Purchaser code");
		// TODO: DEFAULTING VALUES
		pInstrumentBean.setSupMsmeStatus(lSupplierEntityBean.getMsmeStatus());
		//
		// }
		return lPurchaserSupplierLinkBean;
	}

	public void validateAndSetSupplierLocationDetails(Connection pConnection, InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		// valid supplier location
		CompanyLocationBean lFilterLocationBean = new CompanyLocationBean();
		CompanyLocationBean lSupplierLocation = null;
		//
		AppEntityBean lUserEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		AppEntityBean lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
		AppEntityBean lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());
		//
		if (pInstrumentBean.getSupClId() != null) {
			if (pInstrumentBean.getSupClId().longValue() > 0) {
				lFilterLocationBean.setId(pInstrumentBean.getSupClId());
				lSupplierLocation = companyLocationDAO.findByPrimaryKey(pConnection, lFilterLocationBean);
			} else {
				lSupplierLocation = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lSupplierEntityBean.getCdId());
			}
		} else if (StringUtils.isNotBlank(pInstrumentBean.getSupLocation())) {
			if (AppConstants.REG_OFFICE_DESC.toUpperCase().equals(pInstrumentBean.getSupLocation().trim().toUpperCase())) {
				lSupplierLocation = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lSupplierEntityBean.getCdId());
			} else {
				// pattern match pInstrumentBean.getSupLocation() with gstn
				Pattern lGstPattern = (Pattern) BeanFieldMeta.PATTERNS.get("PATTERN_GST")[0];
				if (lGstPattern.matcher(pInstrumentBean.getSupLocation()).matches()) {
					// if multiple locations then send the first location
					lFilterLocationBean.setGstn(pInstrumentBean.getSupLocation());
					lFilterLocationBean.setCdId(lSupplierEntityBean.getCdId());
					List<CompanyLocationBean> lTempList = companyLocationDAO.findList(pConnection, lFilterLocationBean, (String) null);
					if (!lTempList.isEmpty()) {
						lSupplierLocation = lTempList.get(0);
					}
				} else {
					lFilterLocationBean.setName(pInstrumentBean.getSupLocation());
					lFilterLocationBean.setCdId(lSupplierEntityBean.getCdId());
					lSupplierLocation = companyLocationDAO.findBean(pConnection, lFilterLocationBean);
				}
			}
		} else if (StringUtils.isNotBlank(pInstrumentBean.getSupGstn())) {
			// pattern match pInstrumentBean.getSupLocation() with gstn
			Pattern lGstPattern = (Pattern) BeanFieldMeta.PATTERNS.get("PATTERN_GST")[0];
			if (lGstPattern.matcher(pInstrumentBean.getSupGstn()).matches()) {
				// if multiple locations then send the first location
				lFilterLocationBean.setGstn(pInstrumentBean.getSupGstn());
				lFilterLocationBean.setCdId(lSupplierEntityBean.getCdId());
				List<CompanyLocationBean> lTempList = companyLocationDAO.findList(pConnection, lFilterLocationBean, (String) null);
				if (!lTempList.isEmpty()) {
					lSupplierLocation = lTempList.get(0);
				}
			}
		}
		if ((lSupplierLocation == null) || (lSupplierLocation.getCdId().longValue() > 0 && !lSupplierLocation.getCdId().equals(lSupplierEntityBean.getCdId())))
			throw new CommonBusinessException("Invalid supplier location");
		//
		if (lUserEntityBean.isSupplier() && !TredsHelper.getInstance().hasAccessToLocations(pUserBean, lSupplierLocation.getId())) {
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED + " No access on Supplier Location.");
		}
		if (!TredsHelper.getInstance().isNACHCodeActive(pConnection, lPurchaserEntityBean.getCode(), lSupplierLocation.getCdId(), lSupplierLocation.getId())) {
			throw new CommonBusinessException("Please check the NACH Codes for the Supplier Location.");
		}
		//
		pInstrumentBean.setSupClId(lSupplierLocation.getId());
		pInstrumentBean.setSupSettleClId(TredsHelper.getInstance().getSettlementLocation(pConnection, lSupplierLocation.getId()));
		pInstrumentBean.setSupGstState(lSupplierLocation.getState());
		pInstrumentBean.setSupGstn(lSupplierLocation.getGstn());
		pInstrumentBean.setSupLocation(lSupplierLocation.getName());
		if (lSupplierEntityBean.getPreferences() != null && lSupplierEntityBean.getPreferences().getElb() != null && CommonAppConstants.Yes.Yes.equals(lSupplierEntityBean.getPreferences().getElb())) {

			BillingLocationBean lBLBean = new BillingLocationBean();
			lBLBean.setCode(lSupplierEntityBean.getCode());
			lBLBean.setId(lSupplierLocation.getId());
			lBLBean = billingLocationBO.findBean(pConnection, lBLBean);
			pInstrumentBean.setSupBillClId(lBLBean.getBillLocId());

		} else {
			pInstrumentBean.setSupBillClId(pInstrumentBean.getSupSettleClId());
		}
	}

	public void validateAndSetPurchaserLocationDetails(Connection pConnection, InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		// valid purchaser location
		CompanyLocationBean lFilterLocationBean = new CompanyLocationBean();
		CompanyLocationBean lPurchaserLocation = null;
		//
		AppEntityBean lUserEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		AppEntityBean lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
		AppEntityBean lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());
		//
		if (pInstrumentBean.getPurClId() != null) {
			if (pInstrumentBean.getPurClId().longValue() > 0) {
				lFilterLocationBean.setId(pInstrumentBean.getPurClId());
				lPurchaserLocation = companyLocationDAO.findByPrimaryKey(pConnection, lFilterLocationBean);
			} else {
				lPurchaserLocation = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lPurchaserEntityBean.getCdId());
			}
		} else if (StringUtils.isNotBlank(pInstrumentBean.getPurLocation())) {
			if (AppConstants.REG_OFFICE_DESC.toUpperCase().equals(pInstrumentBean.getPurLocation().trim().toUpperCase())) {
				lPurchaserLocation = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lPurchaserEntityBean.getCdId());
			} else {
				// pattern match pInstrumentBean.getSupLocation() with gstn
				Pattern lGstPattern = (Pattern) BeanFieldMeta.PATTERNS.get("PATTERN_GST")[0];
				if (lGstPattern.matcher(pInstrumentBean.getPurLocation()).matches()) {
					// if multiple locations then send the first location
					lFilterLocationBean.setGstn(pInstrumentBean.getPurLocation());
					lFilterLocationBean.setCdId(lPurchaserEntityBean.getCdId());
					List<CompanyLocationBean> lTempList = companyLocationDAO.findList(pConnection, lFilterLocationBean, (String) null);
					if (!lTempList.isEmpty()) {
						lPurchaserLocation = lTempList.get(0);
					}
				} else {
					lFilterLocationBean.setName(pInstrumentBean.getPurLocation());
					lFilterLocationBean.setCdId(lPurchaserEntityBean.getCdId());
					lPurchaserLocation = companyLocationDAO.findBean(pConnection, lFilterLocationBean);
				}
			}
		} else if (StringUtils.isNotBlank(pInstrumentBean.getPurGstn())) {
			// pattern match pInstrumentBean.getSupLocation() with gstn
			Pattern lGstPattern = (Pattern) BeanFieldMeta.PATTERNS.get("PATTERN_GST")[0];
			if (lGstPattern.matcher(pInstrumentBean.getPurGstn()).matches()) {
				// if multiple locations then send the first location
				lFilterLocationBean.setGstn(pInstrumentBean.getPurGstn());
				lFilterLocationBean.setCdId(lPurchaserEntityBean.getCdId());
				List<CompanyLocationBean> lTempList = companyLocationDAO.findList(pConnection, lFilterLocationBean, (String) null);
				if (!lTempList.isEmpty()) {
					lPurchaserLocation = lTempList.get(0);
				}
			}
		}
		if ((lPurchaserLocation == null) || (lPurchaserLocation.getCdId().longValue() > 0 && !lPurchaserLocation.getCdId().equals(lPurchaserEntityBean.getCdId())))
			throw new CommonBusinessException("Invalid purchaser location");

		if (lUserEntityBean.isPurchaserAggregator()) {
			// TODO: Will the Purchaser Aggregator have full access to all
			// locations???
		} else {
			if (lUserEntityBean.isPurchaser() && TredsHelper.getInstance().checkAccessToLocations(pUserBean)) {
				if (!pUserBean.getLocationIdList().contains(lPurchaserLocation.getId())) {
					throw new CommonBusinessException("Access denied on purchaser location");
				}
			}
			if (lUserEntityBean.isPurchaser() && !TredsHelper.getInstance().hasAccessToLocations(pUserBean, lPurchaserLocation.getId())) {
				throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED + " No access on Purchaser Location.");
			}
		}
		//
		if (!TredsHelper.getInstance().isNACHCodeActive(pConnection, lPurchaserEntityBean.getCode(), lPurchaserLocation.getCdId(), lPurchaserLocation.getId())) {
			throw new CommonBusinessException("Please check the NACH Codes for the Purchaser Location.");
		}
		pInstrumentBean.setPurClId(lPurchaserLocation.getId());
		pInstrumentBean.setPurSettleClId(TredsHelper.getInstance().getSettlementLocation(pConnection, lPurchaserLocation.getId()));
		pInstrumentBean.setPurGstState(lPurchaserLocation.getState());
		pInstrumentBean.setPurGstn(lPurchaserLocation.getGstn());
		pInstrumentBean.setPurLocation(lPurchaserLocation.getName());
		if (lPurchaserEntityBean.getPreferences() != null && lPurchaserEntityBean.getPreferences().getElb() != null && CommonAppConstants.Yes.Yes.equals(lPurchaserEntityBean.getPreferences().getElb())) {

			BillingLocationBean lBLBean = new BillingLocationBean();
			lBLBean.setCode(lPurchaserEntityBean.getCode());
			lBLBean.setId(lPurchaserLocation.getId());
			lBLBean = billingLocationBO.findBean(pConnection, lBLBean);
			pInstrumentBean.setPurBillClId(lBLBean.getBillLocId());

		} else {
			pInstrumentBean.setPurBillClId(pInstrumentBean.getPurSettleClId());
		}
	}

	public InstrumentBean validateUniqueInvoiceNumber(Connection pConnection, InstrumentBean pInstrumentBean, boolean pNew, boolean pIsAggregator) throws Exception {
		// unique invoice number
		StringBuilder lUniqueSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		Date[] lFYDates = TredsHelper.getInstance().getFinYearDates(pInstrumentBean.getInstDate());
		lUniqueSql.append("SELECT * FROM Instruments WHERE INRecordVersion > 0 AND INSupplier = ");
		lUniqueSql.append(DBHelper.getInstance().formatString(pInstrumentBean.getSupplier()));
		lUniqueSql.append(" AND INPurchaser = ").append(lDbHelper.formatString(pInstrumentBean.getPurchaser()));
		lUniqueSql.append(" AND INInstNumber = ").append(lDbHelper.formatString(pInstrumentBean.getInstNumber()));
		if (!pNew)
			lUniqueSql.append(" AND INId != ").append(pInstrumentBean.getId());
		lUniqueSql.append(" AND ININSTDATE BETWEEN ").append(lDbHelper.formatDate(lFYDates[0])).append(" AND ").append(lDbHelper.formatDate(lFYDates[1]));
		InstrumentBean lInstrumentDuplicateBean = instrumentDAO.findBean(pConnection, lUniqueSql.toString());
		if (lInstrumentDuplicateBean != null) {
			if (pIsAggregator) {
				if (lInstrumentDuplicateBean.getGroupInId() == null) {
					return lInstrumentDuplicateBean;
				}
			}
			throw new CommonBusinessException("Instrument with invoice number " + pInstrumentBean.getInstNumber() + " already uploaded");
		}
		return null;
	}

	public void updateCostColumns(InstrumentBean pInstrumentBean, PurchaserSupplierLinkBean pModifiedPSLinkBean, boolean pApiCall) {
		// TODO: use field data and use beanmeta copy
		pInstrumentBean.setCostBearingType(pModifiedPSLinkBean.getCostBearingType());
		pInstrumentBean.setSplittingPoint(pModifiedPSLinkBean.getSplittingPoint());
		pInstrumentBean.setPreSplittingCostBearer(pModifiedPSLinkBean.getPreSplittingCostBearer());
		pInstrumentBean.setPostSplittingCostBearer(pModifiedPSLinkBean.getPostSplittingCostBearer());
		pInstrumentBean.setPeriod1CostBearer(pModifiedPSLinkBean.getPeriod1CostBearer());
		pInstrumentBean.setPeriod1CostPercent(pModifiedPSLinkBean.getPeriod1CostPercent());
		pInstrumentBean.setPeriod2CostBearer(pModifiedPSLinkBean.getPeriod2CostBearer());
		pInstrumentBean.setPeriod2CostPercent(pModifiedPSLinkBean.getPeriod2CostPercent());
		pInstrumentBean.setPeriod3CostBearer(pModifiedPSLinkBean.getPeriod3CostBearer());
		pInstrumentBean.setPeriod3CostPercent(pModifiedPSLinkBean.getPeriod3CostPercent());
		//
		pInstrumentBean.setChargeBearer(pModifiedPSLinkBean.getChargeBearer());
		pInstrumentBean.setSplittingPointCharge(pModifiedPSLinkBean.getSplittingPointCharge());
		pInstrumentBean.setPreSplittingCharge(pModifiedPSLinkBean.getPreSplittingCharge());
		pInstrumentBean.setPostSplittingCharge(pModifiedPSLinkBean.getPostSplittingCharge());
		pInstrumentBean.setPeriod1ChargeBearer(pModifiedPSLinkBean.getPeriod1ChargeBearer());
		pInstrumentBean.setPeriod1ChargePercent(pModifiedPSLinkBean.getPeriod1ChargePercent());
		pInstrumentBean.setPeriod2ChargeBearer(pModifiedPSLinkBean.getPeriod2ChargeBearer());
		pInstrumentBean.setPeriod2ChargePercent(pModifiedPSLinkBean.getPeriod2ChargePercent());
		pInstrumentBean.setPeriod3ChargeBearer(pModifiedPSLinkBean.getPeriod3ChargeBearer());
		pInstrumentBean.setPeriod3ChargePercent(pModifiedPSLinkBean.getPeriod3ChargePercent());
		//
		pInstrumentBean.setAutoConvert(pModifiedPSLinkBean.getAutoConvert());
		pInstrumentBean.setBuyerPercent(pModifiedPSLinkBean.getBuyerPercent());
		pInstrumentBean.setBuyerPercentCharge(pModifiedPSLinkBean.getBuyerPercentCharge());
		pInstrumentBean.setSettleLeg3Flag(pModifiedPSLinkBean.getSettleLeg3Flag());
		pInstrumentBean.setBidAcceptingEntityType(pModifiedPSLinkBean.getBidAcceptingEntityType());

		pInstrumentBean.setAutoAccept(pModifiedPSLinkBean.getAutoAccept());
		pInstrumentBean.setAutoAcceptableBidTypes(pModifiedPSLinkBean.getAutoAcceptableBidTypes());
		if (pApiCall) {
			//
			// haircut and cash discount percentage - assuming that is
			// nothing is sent then default is accepted
			// or else has to send zero by default
			if (pInstrumentBean.getHaircutPercent() == null) {
				pInstrumentBean.setHaircutPercent(pModifiedPSLinkBean.getHaircutPercent());
			}
			if (pInstrumentBean.getCashDiscountPercent() == null) {
				pInstrumentBean.setCashDiscountPercent(pModifiedPSLinkBean.getCashDiscountPercent());
			}
			if (pInstrumentBean.getAdjAmount() == null) {
				pInstrumentBean.setAdjAmount(BigDecimal.ZERO);
			}
			if (pInstrumentBean.getCurrency() == null) {
				pInstrumentBean.setCurrency(AppConstants.CURRENCY_INR);
			}
		}
	}

	public void validateDates(InstrumentBean pInstrumentBean) throws Exception {
		AppEntityBean lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());
		// validate dates
		Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();
		if ((pInstrumentBean.getPoDate() != null) && (pInstrumentBean.getPoDate().compareTo(pInstrumentBean.getInstDate()) > 0))
			throw new CommonBusinessException("Purchase order date should be on or before the Invoice date.");
		if (pInstrumentBean.getGoodsAcceptDate() == null)
			pInstrumentBean.setGoodsAcceptDate(pInstrumentBean.getInstDate());
		if (pInstrumentBean.getGoodsAcceptDate().compareTo(pInstrumentBean.getInstDate()) < 0)
			throw new CommonBusinessException("Goods/Service Acceptance Date should be greater than or equal to Invoice date.");
		if (pInstrumentBean.getGoodsAcceptDate().compareTo(lCurrentDate) > 0)
			throw new CommonBusinessException("Goods/Service Acceptance Date should be less than or equal to current date.");

		HashMap<String, Object> lInstrumentSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_INSTRUMENTSETTINGS);
		HashMap<String, Object> lMsmeSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructureHash(AppConstants.REGISTRY_MSMESETTINGS).get(lSupplierEntityBean.getMsmeStatus());
		long lMaxInstrumentAge = 45;
		if ((lInstrumentSettings != null) && (lInstrumentSettings.get(AppConstants.ATTRIBUTE_GOODSACCEPTDATE) != null))
			lMaxInstrumentAge = ((Long) lInstrumentSettings.get(AppConstants.ATTRIBUTE_GOODSACCEPTDATE)).longValue();
		Long lMinUsance = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_MINUSANCE);
		// determine max factorable date as maturity date - minusance
		if (lMinUsance == null)
			throw new CommonBusinessException("Minimum usance not defined for msme type : " + lSupplierEntityBean.getMsmeStatus());

		if (OtherResourceCache.getInstance().getDiffInDays(lCurrentDate, pInstrumentBean.getGoodsAcceptDate()) > lMaxInstrumentAge)
			throw new CommonBusinessException("Invalid Goods/Service Acceptance Date. It is greater than maximum age allowed of " + lMaxInstrumentAge);

	}

	public Date getEndDate(InstrumentBean pInstrumentBean, Long pMinUsance, Date pEndDate) throws Exception {
		Date lEndDate = pEndDate;
		AppEntityBean lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
		if (lPurchaserEntityBean.getPreferences() != null && lPurchaserEntityBean.getPreferences().getUpds() != null && AppEntityPreferenceBean.Upds.Working_Days.equals(lPurchaserEntityBean.getPreferences().getUpds())) {
			lEndDate = OtherResourceCache.getInstance().getWorkingDate(pEndDate, true, false, pMinUsance.intValue());
		} else if (lPurchaserEntityBean.getPreferences() != null && lPurchaserEntityBean.getPreferences().getUpds() != null && AppEntityPreferenceBean.Upds.Calendar_Days.equals(lPurchaserEntityBean.getPreferences().getUpds())) {
			lEndDate = OtherResourceCache.getInstance().addDaysToDate(pEndDate, -1 * pMinUsance.intValue());
		} else {
			lEndDate = OtherResourceCache.getInstance().getWorkingDate(lEndDate, true, false, pMinUsance.intValue());
		}
		return lEndDate;
	}

	private void validatePurchaserMandate(Connection pConnection, String pPurchaser, BigDecimal pInstNetAmt, Long pPurSettleCLId, Date pInstDueDate, boolean pObliSplitting, boolean pIsGroup) throws CommonBusinessException {
		// if purchaser.allowobligsplitting = no
		// and instrument.value >
		// regisgtry.obligsplittingsettings.percentofmandate *
		// nachcodes.getmandate(purchaser, currentbusinessdate)
		BigDecimal lMandateValue = BigDecimal.ZERO;
		BigDecimal lMandatePercent = BigDecimal.ZERO;
		FacilitatorEntityMappingBean lPurchaserFEMBean = null;
		HashMap lObliSplitSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONSPLITTING);
		Long lMandateGrace = null;
		//
		// from instrument location find the settlement location
		//
		lMandatePercent = BigDecimal.valueOf((Double) lObliSplitSetting.get(AppConstants.ATTRIBUTE_MANDATEPERCENT));
		lMandateGrace = Long.valueOf(lObliSplitSetting.get(AppConstants.ATTRIBUTE_MANDATEGRACEPERIOD).toString());
		lMandateGrace = new Long(0);
		//
		lPurchaserFEMBean = TredsHelper.getInstance().getNachBeanForPurchaser(pConnection, pPurSettleCLId, pPurchaser);

		if (lPurchaserFEMBean == null) {
			throw new CommonBusinessException("NACH mandate not found.");
		}
		if (!CommonAppConstants.YesNo.Yes.equals(lPurchaserFEMBean.getActive())) {
			throw new CommonBusinessException("NACH mandate is inactive.");
		}
		if (lPurchaserFEMBean.getExpiry() != null && pInstDueDate.after(lPurchaserFEMBean.getExpiry())) {
			// this is tempoaraily commented since we do not wish to give him
			// time to update the nach
			// check diffrence to be greater than the mandate grace then no need
			// to throw error.
			// if(lMandateGrace.longValue() > 0 &&
			// OtherResourceCache.getInstance().getDiffInDays(pInstDueDate,
			// lPurchaserFEMBean.getExpiry()) < lMandateGrace.longValue() ){
			// throw new CommonBusinessException("No time to update the NACH
			// mandate.");
			// }
			throw new CommonBusinessException("Instrument due date falls beyond the NACH mandate expiry date " + TredsHelper.getInstance().getFormattedDate(lPurchaserFEMBean.getExpiry()) + ".");
		}
		if (!pObliSplitting) {
			lMandateValue = (lPurchaserFEMBean.getMandateAmount().multiply(lMandatePercent, MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			if (pInstNetAmt.compareTo(lMandateValue) >= 0) {
				logger.info("Adjusted NACH mandate amount " + lMandateValue + " is less than instrument amount " + pInstNetAmt + ".");
				if (!pIsGroup) {
					throw new CommonBusinessException("Adjusted NACH mandate amount is less than instrument amount.");
				} else {
					throw new CommonBusinessException("Group total is greater than " + lMandatePercent + "% of adjusted NACH mandate amount");
				}
			}
		}
	}

	private void validateCashDiscount(Connection pConnection, String pPurchaserCode, BigDecimal pCashDiscount) throws CommonBusinessException, Exception {
		if (pCashDiscount != null) {
			// check platform level
			Double lTmpMaxCD = RegistryHelper.getInstance().getDouble(AppConstants.REGISTRY_MAXCASHDISCOUNTPERCENT);
			if (lTmpMaxCD != null) {
				BigDecimal lMaxCashDiscount = BigDecimal.valueOf(lTmpMaxCD);
				if (lMaxCashDiscount.compareTo(pCashDiscount) < 0) {
					throw new CommonBusinessException("Cash Discount for Purchaser should be less than Max Cash Discount " + lMaxCashDiscount + "  set at Platform Level.");
				}
			} else {
				throw new CommonBusinessException("Max Cash Discount not set at Platform Level.");
			}
			// check for purchaser at Registration Level
			CompanyDetailBean lCDBean = new CompanyDetailBean();
			lCDBean.setCode(pPurchaserCode);
			lCDBean = companyDetailDao.findBean(pConnection, lCDBean);
			if (lCDBean != null) {
				if (lCDBean.getCashDiscountPercent() != null) {
					if (lCDBean.getCashDiscountPercent().compareTo(pCashDiscount) < 0) {
						throw new CommonBusinessException("Cash Discount cannot exceed " + lCDBean.getCashDiscountPercent() + " set at Registration Level.");
					}
				} else {
					throw new CommonBusinessException("Cash Discount not set at Registration Level.");
				}
			} else {
				throw new CommonBusinessException("Company Detail not found for " + pPurchaserCode);
			}
		}
	}

	private void validateCreditPeriod(Long pCreditPeriod, CommonAppConstants.Yes pEnableExtension, Long pExtendeCreditPeriod) throws CommonBusinessException {
		Long lMaxAllowedTotalCP = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_MAXALLOWEDTOTALCREDITPERIOD);
		Long lTotalCreditPeriod = pCreditPeriod != null ? pCreditPeriod : new Long(0);
		if (CommonAppConstants.Yes.Yes.equals(pEnableExtension) && pExtendeCreditPeriod != null)
			lTotalCreditPeriod = new Long(pCreditPeriod.longValue() + pExtendeCreditPeriod.longValue());
		if (lTotalCreditPeriod.longValue() > lMaxAllowedTotalCP.longValue()) {
			throw new CommonBusinessException("The total credit period exceeds the max allowed of " + lMaxAllowedTotalCP.longValue() + " days.");
		}
	}

	public void delete(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, AppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
		Connection lConnection = pExecutionContext.getConnection();

		InstrumentBean lInstrumentBean = findBean(pExecutionContext, pFilterBean);
		// InstrumentBean lTmpOldInstBean =
		// instrumentDAO.findByPrimaryKey(lConnection, pInstrumentBean);
		if (lInstrumentBean != null && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
			throw new CommonBusinessException("Grouped Instrument can't be removed.");
		}
		// TODO:PlatformCheck1
		checkPlatformStatus(lConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getSupplier());
		checkMakerAccess(lInstrumentBean, pUserBean);
		InstrumentBean.Status lStatus = lInstrumentBean.getStatus();
		if ((lStatus != InstrumentBean.Status.Drafting) && (lStatus != InstrumentBean.Status.Checker_Returned) && (lStatus != InstrumentBean.Status.Counter_Returned) && (lStatus != InstrumentBean.Status.Withdrawn))
			throw new CommonBusinessException("Instrument cannot be deleted now.");
		if (MonetagoTredsHelper.getInstance().performMonetagoCheck(lInstrumentBean.getPurchaser())) {
			if (StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) && !CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
				Map<String, String> lResult = new HashMap<String, String>();
				String lInfoMessage = "Instrument No :" + lInstrumentBean.getId() + "  Factoring Unit No :" + lInstrumentBean.getFuId();
				lResult = MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Withdrawn, lInfoMessage, lInstrumentBean.getId());
				if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
					lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
					lInstrumentBean.setMonetagoLedgerId("");
					logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
				} else {
					logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					throw new CommonBusinessException("Error while cancelling : " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
				}
			}
		}
		instrumentDAO.update(lConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_UPDATEMONETAGOCANCEL);
		instrumentDAO.delete(lConnection, lInstrumentBean);
		instrumentDAO.insertAudit(lConnection, lInstrumentBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
		// TODO: _PM - Test code in my local - what to do?
		// lInstrumentBean.setStatus(Status.Withdrawn);
		// InstrumentWorkFlowBean lWorkFlowBean =
		// insertInstrumentWorkFlow(lConnection, lInstrumentBean,
		// pUserBean,pFilterBean.getStatusRemarks());
		// emailGeneratorBO.sendInstrumentCheckerActionMailToMaker(lConnection,
		// lInstrumentBean,lWorkFlowBean, pUserBean);
		//
		pExecutionContext.commitAndDispose();
	}

	public void setUpdatedDueDates(InstrumentBean pInstrumentBean) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		AppEntityBean lSupplierEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pInstrumentBean.getSupplier() });
		if ((lSupplierEntityBean == null) || (!lSupplierEntityBean.isSupplier()))
			throw new CommonBusinessException("Invalid Supplier code");

		logger.info("setUpdatedDueDates. Fetching DueDates for inid : "+(pInstrumentBean.getId()!=null?pInstrumentBean.getId().toString():""));
		HashMap<String, Object> lMsmeSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructureHash(AppConstants.REGISTRY_MSMESETTINGS).get(lSupplierEntityBean.getMsmeStatus());
		// to compute statutory due date
		Long lMSMECreditPeriod = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_CREDITPERIOD);
		boolean lCreditPeriodUpdated = updateCreditPeriod(pInstrumentBean, lMSMECreditPeriod);
		Date[] lDates = getDueDates(pInstrumentBean.getGoodsAcceptDate(), pInstrumentBean.getCreditPeriod(), pInstrumentBean.getExtendedCreditPeriod(), lMSMECreditPeriod, pInstrumentBean.getEnableExtension());
		if (!lCreditPeriodUpdated)
			pInstrumentBean.setInstDueDate(lDates[DATES_INSTRUMENTDUEDATE]);
		pInstrumentBean.setStatDueDate(lDates[DATES_STATUTORYDUEDATE]);
		pInstrumentBean.setMaturityDate(lDates[DATES_MATURITYDATE]);
		pInstrumentBean.setExtendedDueDate(lDates[DATES_EXTENDEDDUEDATE]);
		// determine max factorable date as maturity -minusance
		Long lMinUsance = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_MINUSANCE);
		// min of maturity and stat due date
		Date lEndDate = pInstrumentBean.getStatDueDate().compareTo(pInstrumentBean.getMaturityDate()) < 0 ? pInstrumentBean.getStatDueDate() : pInstrumentBean.getMaturityDate();
		lEndDate = getEndDate(pInstrumentBean, lMinUsance, lEndDate);
		pInstrumentBean.setFactorMaxEndDateTime(new Timestamp(lEndDate.getTime() + AppConstants.DAY_IN_MILLIS - 1));
		logger.info(" FactorMaxEndDateTime  :" + pInstrumentBean.getFactorMaxEndDateTime());
		if (pInstrumentBean.getFactorMaxEndDateTime().compareTo(OtherResourceCache.getInstance().getCurrentDate()) < 0) {
			throw new CommonBusinessException("Instrument is not factorable since maturity[" + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_MATURITYDATE]) + "]/statutory[" + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_STATUTORYDUEDATE]) + "] due date does not meet the minimum usance criteria of " + lMinUsance);
		}
	}

	public Date[] getDueDates(Date pGoodsAcceptanceDate, Long pCreditPeriod, Long pExtendedCreditPeriod, Long pMSMECreditPeriod, CommonAppConstants.Yes pExtentionEnabled) throws Exception {
		logger.info("getDueDates input ");
		logger.info("pGoodsAcceptanceDate : " + (pGoodsAcceptanceDate != null ? pGoodsAcceptanceDate : ""));
		logger.info("pCreditPeriod : " + (pCreditPeriod != null ? pCreditPeriod : ""));
		logger.info("pMSMECreditPeriod : " + (pMSMECreditPeriod != null ? pMSMECreditPeriod : ""));
		logger.info("pExtentionEnabled : " + (pExtentionEnabled != null ? pExtentionEnabled : ""));
		Date[] lDates = new Date[4];
		lDates[DATES_INSTRUMENTDUEDATE] = OtherResourceCache.getInstance().addDaysToDate(pGoodsAcceptanceDate, pCreditPeriod.intValue());
		if (pMSMECreditPeriod == null)
			lDates[DATES_STATUTORYDUEDATE] = lDates[DATES_INSTRUMENTDUEDATE];
		else {
			lDates[DATES_STATUTORYDUEDATE] = OtherResourceCache.getInstance().addDaysToDate(pGoodsAcceptanceDate, pMSMECreditPeriod.intValue());
			if (lDates[DATES_INSTRUMENTDUEDATE].compareTo(lDates[DATES_STATUTORYDUEDATE]) < 0)
				lDates[DATES_STATUTORYDUEDATE] = lDates[DATES_INSTRUMENTDUEDATE];
		}
		lDates[DATES_STATUTORYDUEDATE] = OtherResourceCache.getInstance().getPreviousClearingDate(lDates[DATES_STATUTORYDUEDATE], 0);
		//
		if (CommonAppConstants.Yes.Yes.equals(pExtentionEnabled)) {
			lDates[DATES_MATURITYDATE] = lDates[DATES_INSTRUMENTDUEDATE];
			// THIS IS FOR CONDITION WHERE EXTENSION IS YES BUT CREDITPERIOD IS ZERO
			lDates[DATES_EXTENDEDDUEDATE] = lDates[DATES_INSTRUMENTDUEDATE];
		} else {
			lDates[DATES_MATURITYDATE] = OtherResourceCache.getInstance().getPreviousClearingDate(lDates[DATES_INSTRUMENTDUEDATE], 0);
		}
		if (CommonAppConstants.Yes.Yes.equals(pExtentionEnabled) && pExtendedCreditPeriod != null && pExtendedCreditPeriod.longValue() > 0) {
			lDates[DATES_EXTENDEDDUEDATE] = OtherResourceCache.getInstance().addDaysToDate(lDates[DATES_MATURITYDATE], pExtendedCreditPeriod.intValue());
			lDates[DATES_EXTENDEDDUEDATE] = OtherResourceCache.getInstance().getPreviousClearingDate(lDates[DATES_EXTENDEDDUEDATE], 0);
		}
		logger.info("lDates[DATES_INSTRUMENTDUEDATE] : " + (lDates[DATES_INSTRUMENTDUEDATE] != null ? lDates[DATES_INSTRUMENTDUEDATE] : ""));
		logger.info("lDates[DATES_STATUTORYDUEDATE] : " + (lDates[DATES_STATUTORYDUEDATE] != null ? lDates[DATES_STATUTORYDUEDATE] : ""));
		logger.info("lDates[DATES_MATURITYDATE] : " + (lDates[DATES_MATURITYDATE] != null ? lDates[DATES_MATURITYDATE] : ""));
		logger.info("lDates[DATES_EXTENDEDDUEDATE] : " + (lDates[DATES_EXTENDEDDUEDATE] != null ? lDates[DATES_EXTENDEDDUEDATE] : ""));
		return lDates;
	}

	public boolean updateCreditPeriod(InstrumentBean pInstrumentBean, Long pMSMECreditPeriod) throws Exception {
		boolean lUpdate = false;
		AppEntityBean lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
		if ((lPurchaserEntityBean == null) || (!lPurchaserEntityBean.isPurchaser()))
			throw new CommonBusinessException("Invalid Purchaser code");
		if (lPurchaserEntityBean.getPreferences() != null && lPurchaserEntityBean.getPreferences().getIdcp() != null) {
			if (AppEntityPreferenceBean.Idcp.Due_date.equals(lPurchaserEntityBean.getPreferences().getIdcp())) {
				if (pInstrumentBean.getInstDueDate() != null) {
					pInstrumentBean.setInstDueDate(OtherResourceCache.getInstance().getNextClearingDate(pInstrumentBean.getInstDueDate(), 0));
					Long lCreditPeriod = OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getInstDueDate(), pInstrumentBean.getGoodsAcceptDate());
					lUpdate = !pInstrumentBean.getCreditPeriod().equals(lCreditPeriod);
					pInstrumentBean.setCreditPeriod(lCreditPeriod);
				}
			} else if (AppEntityPreferenceBean.Idcp.Credit_Period.equals(lPurchaserEntityBean.getPreferences().getIdcp())) {
				pInstrumentBean.setInstDueDate(null); // we will not use the due
														// date sent
			}
		}
		return lUpdate;
	}

	public String convertToFactoringUnit(Connection pConnection, InstrumentBean pFilterBean, AppUserBean pUserBean, boolean pAccessCheck, FactoringUnitBean.Status pStatus) throws Exception {
		String lRetMsg = "";

		InstrumentBean lInstrumentBean = pAccessCheck ? findBean(pConnection, pFilterBean) : pFilterBean;

		if (CommonAppConstants.YesNo.Yes.equals(pUserBean.getPurchaserAggregatorFlag())) {
			// TODO: Q. Kya karoun.
		} else {
			if (pAccessCheck)
				checkOwnerAccess(lInstrumentBean, pUserBean);
		}
		InstrumentBean.Status lStatus = lInstrumentBean.getStatus();
		if (lStatus != InstrumentBean.Status.Counter_Approved)
			throw new CommonBusinessException("Instrument cannot be converted to a factoring unit. It is not yet approved by the purchaser.");

		// TODO:PlatformCheck2X
		Long lFuId = TredsHelper.getInstance().getFactoringUnitId(pConnection);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();

		lInstrumentBean.setFuId(lFuId);
		lInstrumentBean.setStatus(InstrumentBean.Status.Converted_To_Factoring_Unit);
		lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		instrumentDAO.update(pConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_UPDATECONVFACTUNIT);
		insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, lFuId.toString());

		if (AutoConvert.Auto.equals(lInstrumentBean.getAutoConvert())) {
			try {
				BigDecimal lLimitBlocked = purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getSupplier(), pUserBean, lInstrumentBean.getNetAmount(), false);
				lFactoringUnitBean.setPurSupLimitUtilized(lLimitBlocked);
			} catch (CommonBusinessException lCommonBusinessException) {
				lRetMsg = lCommonBusinessException.getMessage();
				pStatus = FactoringUnitBean.Status.Ready_For_Auction;
			}
		}

		lFactoringUnitBean.setId(lFuId);
		lFactoringUnitBean.setMaturityDate(lInstrumentBean.getMaturityDate());
		lFactoringUnitBean.setStatDueDate(lInstrumentBean.getStatDueDate());

		lFactoringUnitBean.setEnableExtension(lInstrumentBean.getEnableExtension());
		//
		if (CommonAppConstants.Yes.Yes.equals(lFactoringUnitBean.getEnableExtension())) {
			lFactoringUnitBean.setExtendedCreditPeriod(lInstrumentBean.getExtendedCreditPeriod());
			// recalculate extended due date
			setUpdatedDueDates(lInstrumentBean);
			lFactoringUnitBean.setExtendedDueDate(lInstrumentBean.getExtendedDueDate());
		}
		lFactoringUnitBean.setCurrency(lInstrumentBean.getCurrency());
		lFactoringUnitBean.setPurchaser(lInstrumentBean.getPurchaser());
		lFactoringUnitBean.setPurchaserRef(lInstrumentBean.getPurchaserRef());
		lFactoringUnitBean.setSupplier(lInstrumentBean.getSupplier());
		lFactoringUnitBean.setSupplierRef(lInstrumentBean.getSupplierRef());
		lFactoringUnitBean.setOwnerEntity(lInstrumentBean.getOwnerEntity());
		lFactoringUnitBean.setOwnerAuId(lInstrumentBean.getOwnerAuId());
		lFactoringUnitBean.setIntroducingEntity(lInstrumentBean.getMakerEntity());
		lFactoringUnitBean.setIntroducingAuId(lInstrumentBean.getMakerAuId());
		lFactoringUnitBean.setCounterEntity(lInstrumentBean.getCounterEntity());
		lFactoringUnitBean.setCounterAuId(lInstrumentBean.getCounterAuId());
		lFactoringUnitBean.setStatus(pStatus);
		lFactoringUnitBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		lFactoringUnitBean.setAutoAccept(lInstrumentBean.getAutoAccept());
		lFactoringUnitBean.setAutoAcceptableBidTypes(lInstrumentBean.getAutoAcceptableBidTypes());
		//
		lFactoringUnitBean.setAutoConvert(lInstrumentBean.getAutoConvert());
		// lFactoringUnitBean.setCostBearer(lInstrumentBean.getCostBearer());
		lFactoringUnitBean.setPeriod1CostBearer(lInstrumentBean.getPeriod1CostBearer());
		lFactoringUnitBean.setPeriod1CostPercent(lInstrumentBean.getPeriod1CostPercent());
		lFactoringUnitBean.setPeriod2CostBearer(lInstrumentBean.getPeriod2CostBearer());
		lFactoringUnitBean.setPeriod2CostPercent(lInstrumentBean.getPeriod2CostPercent());
		lFactoringUnitBean.setPeriod3CostBearer(lInstrumentBean.getPeriod3CostBearer());
		lFactoringUnitBean.setPeriod3CostPercent(lInstrumentBean.getPeriod3CostPercent());
		
		lFactoringUnitBean.setPeriod1ChargeBearer(lInstrumentBean.getPeriod1ChargeBearer());
		lFactoringUnitBean.setPeriod1ChargePercent(lInstrumentBean.getPeriod1ChargePercent());
		lFactoringUnitBean.setPeriod2ChargeBearer(lInstrumentBean.getPeriod2ChargeBearer());
		lFactoringUnitBean.setPeriod2ChargePercent(lInstrumentBean.getPeriod2ChargePercent());
		lFactoringUnitBean.setPeriod3ChargeBearer(lInstrumentBean.getPeriod3ChargeBearer());
		lFactoringUnitBean.setPeriod3ChargePercent(lInstrumentBean.getPeriod3ChargePercent());

		lFactoringUnitBean.setSupGstState(lInstrumentBean.getSupGstState());
		lFactoringUnitBean.setSupGstn(lInstrumentBean.getSupGstn());
		lFactoringUnitBean.setPurGstState(lInstrumentBean.getPurGstState());
		lFactoringUnitBean.setPurGstn(lInstrumentBean.getPurGstn());
		lFactoringUnitBean.setSettleLeg3Flag(lInstrumentBean.getSettleLeg3Flag());
		lFactoringUnitBean.setAmount(lInstrumentBean.getNetAmount());
		/*
		 * TODO : GST Charges where default set to 0
		 */
		lFactoringUnitBean.setFactorStartDateTime(new Timestamp(System.currentTimeMillis()));

		lFactoringUnitBean.setFactorEndDateTime(lInstrumentBean.getFactorMaxEndDateTime());
		lFactoringUnitBean.setFactorMaxEndDateTime(lInstrumentBean.getFactorMaxEndDateTime());
		if (lFactoringUnitBean.getFactorEndDateTime().compareTo(lFactoringUnitBean.getFactorStartDateTime()) <= 0)
			throw new CommonBusinessException("Factoring unit does not meet the minimum usance criteria");

		lFactoringUnitBean.setSalesCategory(lInstrumentBean.getSalesCategory());
		IStatsCacheGenerator lPurSupStatsCache = StatsCacheFactory.getInstance().getStatsCacheGenerator(StatsCacheFactory.STAT_PURCHASER_SUPPLIER_LINK_FU);
		lPurSupStatsCache.generateAlert(lFactoringUnitBean);
		factoringUnitDAO.insert(pConnection, lFactoringUnitBean);

		// Send Mails to - Cost Bearer Entity, Owner Entity and Admin
		if (FactoringUnitBean.Status.Ready_For_Auction.equals(lFactoringUnitBean.getStatus())) {
			emailGeneratorBO.sendInstrumentReadyForAuctionMail(pConnection, lFactoringUnitBean, lInstrumentBean);
		}
		// send mail to the Buyers Designated Bank,
		emailGeneratorBO.sendFUCreatedMailToDesignatedBank(pConnection, lFactoringUnitBean, lInstrumentBean);
		//
		// send email to financier if autoconvert is yes
		if (StringUtils.isEmpty(lRetMsg) && FactoringUnitBean.Status.Active.equals(lFactoringUnitBean.getStatus())) {
			List<String> lFinanciers = TredsHelper.getInstance().getMappedFinancier(pConnection, lFactoringUnitBean.getPurchaser());
			emailGeneratorBO.sendEmailFuActivationInformToMappedFinancier(pConnection, lFinanciers, lFactoringUnitBean, lInstrumentBean);

		}

		return lRetMsg;
	}

	private void checkMakerAccess(InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		if (!pInstrumentBean.getMakerEntity().equals(pUserBean.getDomain()))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		if ((pUserBean.getType() != AppUserBean.Type.Admin) && (!pInstrumentBean.getMakerAuId().equals(pUserBean.getId()))) {
			if(!pInstrumentBean.getMakerAuId().equals(new Long(0))) {
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
			}
		}
	}

	private void checkOwnerAccess(InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		if (!pInstrumentBean.getOwnerEntity().equals(pUserBean.getDomain()))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		if (TredsHelper.getInstance().checkOwnership(pUserBean) && (pInstrumentBean.getOwnerAuId() != null && !pInstrumentBean.getOwnerAuId().equals(TredsHelper.getInstance().getOwnerAuId(pUserBean))))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
	}

	public List<Map<String, Object>> updateMakerStatus(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, AppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
		Connection lConnection = pExecutionContext.getConnection();
		List<Map<String, Object>> pMessages = new ArrayList<Map<String, Object>>();
		InstrumentBean lInstrumentBean = findBean(lConnection, pFilterBean);
		lInstrumentBean.setStatusRemarks(pFilterBean.getStatusRemarks());
		if (lInstrumentBean == null)
			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		//
		pMessages = updateMakerStatus(lConnection, lInstrumentBean.getStatus(), lInstrumentBean, pUserBean);
		pExecutionContext.commit();
		return pMessages;
	}

	public List<Map<String, Object>> updateMakerStatus(Connection pConnection, InstrumentBean.Status pOldStatus, InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		AppEntityBean lUserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pUserBean.getDomain() });
		List<Map<String, Object>> pMessages = new ArrayList<Map<String, Object>>();
		boolean lRecomputeGroup = false;
		// TODO: To remove after removing the call of this function from
		// agggreatorBO
		if (lUserEntityBean.isPurchaserAggregator()) {
			// TODO: Q. Do we have to check Maker access????
		} else {
			checkMakerAccess(pInstrumentBean, pUserBean);
		}
		InstrumentBean.Status lStatus = pInstrumentBean.getStatus();

		if (!(lStatus.equals(InstrumentBean.Status.Drafting) || lStatus.equals(InstrumentBean.Status.Checker_Returned) || lStatus.equals(InstrumentBean.Status.Counter_Returned) || lStatus.equals(InstrumentBean.Status.Withdrawn)))
			throw new CommonBusinessException("Instrument cannot be submitted.");

		if (pOldStatus != null && VALID_STATUS.contains(pOldStatus.getCode())) {
			throw new CommonBusinessException("Invalid instrument status.");
		}

		// TODO: check the PSLink Status
		// get purchaser supplier link
		boolean lAutoApprove = false;
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(pInstrumentBean.getPurchaser());
		lPSLFilterBean.setSupplier(pInstrumentBean.getSupplier());
		lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
		if (lPurchaserSupplierLinkBean == null)
			throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");

		checkPlatformStatus(lPurchaserSupplierLinkBean);
		// happens in case of upload of instruments through files
		if (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getInvoiceMandatory()) && StringUtils.isBlank(pInstrumentBean.getInstImage()))
			throw new CommonBusinessException("Instrument cannot be submitted. Invoice image not attached. <br>Please 'Modify' the instrument and upload Invoice image under 'Attachments' section.");
		List<MakerCheckerMapBean> lCheckers = null;
		// TODO: To remove after removing the call of this function from
		// agggreatorBO
		if (lUserEntityBean.isPurchaserAggregator()) {
			// TODO: Q. Do we have to check Maker access????
		} else {
			lCheckers = appUserBO.getCheckers(pConnection, pUserBean.getId(), MakerCheckerMapBean.CheckerType.Instrument);
		}
		if (lCheckers == null || lCheckers.size() == 0) {
			pInstrumentBean.setStatus(InstrumentBean.Status.Checker_Approved);
		} else {
			Long lMinLevel = getLevel(pConnection, lCheckers, pUserBean.getId(), CheckerType.Instrument, true);
			pInstrumentBean.setMkrChkLevel(lMinLevel);
			pInstrumentBean.setStatus(InstrumentBean.Status.Submitted);
		}

		if (InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus()) || InstrumentBean.Status.Submitted.equals(pInstrumentBean.getStatus())) {
			String lPurchaserGSTN = pInstrumentBean.getPurGstn();
			String pSupplierGSTN = pInstrumentBean.getSupGstn();
			String pInstNumber = pInstrumentBean.getInstNumber();
			Date pInstDate = pInstrumentBean.getInstDate();
			BigDecimal pAmount = pInstrumentBean.getAmount();
			//
			if (lUserEntityBean != null) {
				boolean lAllowSplitting = false;
				if (lUserEntityBean.isPurchaser()) {
					lAllowSplitting = CommonAppConstants.YesNo.Yes.equals(lUserEntityBean.getAllowObliSplitting());
				} else if (lUserEntityBean.isPurchaserAggregator()) {
					AppEntityBean lPurchaserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pInstrumentBean.getPurchaser() });
					if (lPurchaserEntityBean != null) {
						lAllowSplitting = CommonAppConstants.YesNo.Yes.equals(lPurchaserEntityBean.getAllowObliSplitting());
					}
				}
				boolean lIsGroup = (CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag()));
				if (lUserEntityBean.isPurchaser() || lUserEntityBean.isPurchaserAggregator()) {
					validatePurchaserMandate(pConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getNetAmount(), pInstrumentBean.getPurSettleClId(), pInstrumentBean.getInstDueDate(), lAllowSplitting, lIsGroup);
				}
			}
			//
			HashMap lInstClubSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_INSTCLUBSPLITTING);
			Integer lMaxInstruments = Integer.valueOf(lInstClubSettings.get(AppConstants.ATTRIBUTE_MAXNOOFFACTINSTRUMENTS).toString());
			boolean lGroupError = false;
			if (MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser())) {
				if (StringUtils.isBlank(pInstrumentBean.getMonetagoLedgerId()) && !CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())) {
					Map<String, String> lResult = new HashMap<String, String>();
					String lInfoMessage = " Invoice No :" + pInstrumentBean.getInstNumber() + " Instrument No :" + pInstrumentBean.getId();
					lResult = MonetagoTredsHelper.getInstance().register(pSupplierGSTN, lPurchaserGSTN, pInstNumber, pInstDate, pAmount, lInfoMessage, pInstrumentBean.getId());
					if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID))) {
						pInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
						logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						EndOfDayBO.appendMessage(pMessages, pInstrumentBean.getId().toString(), lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					} else {
						pInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
						logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						throw new CommonBusinessException("Error while submitting (Please save): " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					}
				} else {
					if (CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())) {
						String lFullMessage = null;
						List<InstrumentBean> lGroupBeans = getClubbedBeans(pConnection, pInstrumentBean.getId());
						pInstrumentBean.setGroupedInstruments(lGroupBeans);
						if (lGroupBeans.size() > 0) {
							List<Map<Long, MonetagoRequiredFieldsBean>> lMonetagoList = null;
							Map<Long, MonetagoRequiredFieldsBean> lMonetagoHash = null;
							List<Map<Long, MonetagoRequiredFieldsBean>> lMonetagoResultList = null;
							Map<Long, MonetagoRequiredFieldsBean> lMonetagoResult = null;
							MonetagoRequiredFieldsBean lMonetagoBean = null;
							lMonetagoHash = new HashMap<Long, MonetagoRequiredFieldsBean>();
							lMonetagoList = new ArrayList<>();
							for (InstrumentBean lChildBean : lGroupBeans) {
								// skip already registered childs at monetago
								if (StringUtils.isEmpty(lChildBean.getMonetagoLedgerId()) && !lMonetagoHash.containsKey(lChildBean.getId())) {
									lMonetagoBean = new MonetagoRequiredFieldsBean();
									lMonetagoBean.setId(lChildBean.getId());
									lMonetagoBean.setInstDate(lChildBean.getInstDate());
									lMonetagoBean.setPurGstn(lChildBean.getPurGstn());
									lMonetagoBean.setSupGstn(lChildBean.getSupGstn());
									lMonetagoBean.setAmount(lChildBean.getAmount());
									lMonetagoBean.setInstNumber(lChildBean.getInstNumber());
									if (lMonetagoHash.size() == lMaxInstruments.intValue()) {
										lMonetagoHash = new HashMap<Long, MonetagoRequiredFieldsBean>();
									}
									lMonetagoHash.put(lMonetagoBean.getId(), lMonetagoBean);
									if (!lMonetagoList.contains(lMonetagoHash)) {
										lMonetagoList.add(lMonetagoHash);
									}

								}
							}
							InstrumentBean lInstrumentBean = null;
							boolean lErrorWhileGrouping = false;
							// set flag default false
							if (MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser()) && !lMonetagoHash.isEmpty()) {
								lMonetagoResult = new HashMap<Long, MonetagoRequiredFieldsBean>();
								for (Map<Long, MonetagoRequiredFieldsBean> lHash : lMonetagoList) {
									lMonetagoResult = MonetagoTredsHelper.getInstance().registerBatch(lHash, pInstrumentBean.getId());
									for (MonetagoRequiredFieldsBean lBean : lMonetagoResult.values()) {
										lInstrumentBean = pInstrumentBean.getFromGroupedInstruments(lBean.getId());
										if (lBean.getLedgerId() != null && lBean.getError() == null) {
											if (StringUtils.isNotBlank(lBean.getLedgerId())) {
												lInstrumentBean.setMonetagoLedgerId(lBean.getLedgerId());
											} else {
												// remove from group using group
												// id
												// inst bean groupid null
												// if flag not true put set the
												// flag
												pInstrumentBean.getGroupedInstruments().remove(lInstrumentBean);
												lInstrumentBean.setGroupInId(null);
												lInstrumentBean.setStatusRemarks(lBean.getMessage());
												if (!lUserEntityBean.isPurchaserAggregator()) {
													lRecomputeGroup = true;
												}
												EndOfDayBO.appendMessage(pMessages, lInstrumentBean.getId().toString(), lBean.getMessage());
											}
										} else {
											// Error Condition
											lErrorWhileGrouping = true;
											if (StringUtils.isEmpty(lFullMessage)) {
												lFullMessage = lBean.getError();
											}
										}
									}
									if (!lErrorWhileGrouping && lRecomputeGroup) {
										reCompute(pInstrumentBean);
									}
								}
							}
							if (!lErrorWhileGrouping) {
								if (pInstrumentBean.getGroupedInstruments().size() <= 1) {
								} else {
									for (InstrumentBean lChildBean : pInstrumentBean.getGroupedInstruments()) {
										lChildBean.setGroupInId(pInstrumentBean.getId());
										// update child - 1 or 2 fileds -
										// updating the ledgerids from monetago
										instrumentDAO.update(pConnection, lChildBean, InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
										instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
									}
								}
							} else {
								logger.info("Message Error : " + lFullMessage);
								if (StringUtils.isNotBlank(lFullMessage)) {
									Map<String, Object> lMap = new HashMap<String, Object>();
									lMap.put("act", "");
									lMap.put("rem", lFullMessage);
									lMap.put("error", true);
									pMessages.add(lMap);
								} else {
									throw new CommonBusinessException("Error while submitting (Please try again.) ");
								}
							}
						}
					}
				}
			}
			// only new instruments cannot be created, existing in draft can be
			// modified.
			if (CommonAppConstants.Yes.Yes.equals(lPurchaserSupplierLinkBean.getInWorkFlow())) {
				throw new CommonBusinessException("Purchaser Supplier link in work flow, cannot add new instrument.");
			}
		}
		//
		if (InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus())) {
			// maker directly sending instrument to counter since there is no
			// checker
			if (pInstrumentBean.getCounterEntity().equals(pInstrumentBean.getSupplier())) {
				lAutoApprove = (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getSellerAutoApproveInvoice()));
			} else if (pInstrumentBean.getCounterEntity().equals(pInstrumentBean.getPurchaser())) {
				lAutoApprove = (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getPurchaserAutoApproveInvoice()));
			}
			lAutoApprove = (lAutoApprove && InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus()));
			if (lAutoApprove) {
				// so now change the status to approved directly
				//check - updateMakerStatus
				pInstrumentBean.setStatus(InstrumentBean.Status.Counter_Approved);
			}
		}
		if (MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser()) && CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag()) && lRecomputeGroup) {
			EndOfDayBO.appendMessage(pMessages, pInstrumentBean.getId().toString(), "Group cannot be submitted.");
			pInstrumentBean.setStatus(pOldStatus);
			instrumentDAO.update(pConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATESTATUSONSUBMIT);
		} else {
			if(pInstrumentBean.getMakerAuId().equals(new Long(0))) {
				pInstrumentBean.setMakerAuId(pUserBean.getId());
			}
			EndOfDayBO.appendMessage(pMessages, pInstrumentBean.getId().toString(), "Submitted successfully.");
			pInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
			instrumentDAO.update(pConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATESTATUSONSUBMIT);
			InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(pConnection, pInstrumentBean, pUserBean, pInstrumentBean.getStatusRemarks());
			if (InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus()) && pUserBean.getDomain().equals(pInstrumentBean.getPurchaser())) {
				// maker directly sending instrument to counter since there is
				// no checker
				if (ClickWrapHelper.getInstance().isAgreementEnabled(pInstrumentBean.getPurchaser())) {
					ClickWrapHelper.getInstance().addCounterAcceptanceForInstrument(pConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getId(), pUserBean.getId(), true);
				}
			}
			// send email
			// since we are changing the status to counter approved, the counter
			// will get the mail directly
			if (InstrumentBean.Status.Submitted.equals(pInstrumentBean.getStatus())) {
				emailGeneratorBO.sendInstrumentSubmittedMail(pConnection, pInstrumentBean, lWorkFlowBean, lCheckers, pUserBean);
			} else {
				emailGeneratorBO.sendInstrumentCounterInBoxMail(pConnection, pInstrumentBean, lWorkFlowBean);
			}
			if (lAutoApprove) {
				// logic to convert the insturment to FU
				this.autoApproveByCounter(pConnection, pInstrumentBean, pUserBean);
			}
		}
		return pMessages;
	}

	private void checkCheckerAccess(Connection pConnection, InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		// check if the checker has access to the maker.
		// mapping of check and maker has to be checked here
		AppEntityBean lUserAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		// TODO: this should not be used from anywhere - remove once verified
		if (lUserAppEntityBean.isPurchaserAggregator()) {
			return;
		}
		if (!pInstrumentBean.getMakerEntity().equals(pUserBean.getDomain()))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		if (pUserBean.getType() != AppUserBean.Type.Admin) {
			if (!appUserBO.isValidChecker(pConnection, pInstrumentBean.getMakerAuId(), pUserBean.getId(), MakerCheckerMapBean.CheckerType.Instrument))
				throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
			if (pInstrumentBean.getCheckerAuId() != null) {
				if (!pInstrumentBean.getCheckerAuId().equals(pUserBean.getId()))
					throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
			}
		}
	}

	public void updateCheckerStatus(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, AppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
		updateCheckerStatus(pExecutionContext.getConnection(), pFilterBean, pUserBean);
		pExecutionContext.commitAndDispose();
	}

	public void updateCheckerStatus(Connection pConnection, InstrumentBean pFilterBean, AppUserBean pUserBean) throws Exception {
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		Connection lConnection = pConnection;
		InstrumentBean lInstrumentBean = findBean(pConnection, pFilterBean);
		if (InstrumentBean.Status.Checker_Rejected.equals(pFilterBean.getStatus()) && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
			throw new CommonBusinessException("Grouped Instrument can't be removed.");
		}

		if (lInstrumentBean.getStatus() != null && VALID_STATUS.contains(lInstrumentBean.getStatus().getCode())) {
			throw new CommonBusinessException("Invalid instrument status.");
		}

		// if
		// (InstrumentBean.Status.Checker_Returned.equals(pFilterBean.getStatus())
		// && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag()))
		// {
		// throw new CommonBusinessException("Grouped Instrument can't be
		// returned.");
		// }
		checkCheckerAccess(lConnection, lInstrumentBean, pUserBean);
		InstrumentBean.Status lStatus = lInstrumentBean.getStatus();

		if (!(lStatus.equals(InstrumentBean.Status.Submitted)))
			throw new CommonBusinessException("Invalid Instrument Status.");

		if (!(pFilterBean.getStatus().equals(InstrumentBean.Status.Checker_Approved) || pFilterBean.getStatus().equals(InstrumentBean.Status.Checker_Rejected) || pFilterBean.getStatus().equals(InstrumentBean.Status.Checker_Returned)))
			throw new CommonBusinessException("Checker can only Approve/Reject/Return.");
		Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());

		// TODO: check the PSLink Status
		// get purchaser supplier link
		boolean lAutoApprove = false;
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(lInstrumentBean.getPurchaser());
		lPSLFilterBean.setSupplier(lInstrumentBean.getSupplier());
		lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(lConnection, lPSLFilterBean);
		if (lPurchaserSupplierLinkBean == null)
			throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");

		checkPlatformStatus(lPurchaserSupplierLinkBean);

		lInstrumentBean.setStatusUpdateTime(lCurrentTime);
		lInstrumentBean.setStatus(pFilterBean.getStatus());
		lInstrumentBean.setStatusRemarks(pFilterBean.getStatusRemarks());
		if (pUserBean.getType() != AppUserBean.Type.Admin)
			lInstrumentBean.setCheckerAuId(pUserBean.getId());

		if (InstrumentBean.Status.Checker_Approved.equals(lInstrumentBean.getStatus())) {
			TredsHelper.getInstance().validateUserLimit(pUserBean, lInstrumentBean.getAmount());

			// checker sending instrument to counter
			if (lInstrumentBean.getCounterEntity().equals(lInstrumentBean.getSupplier())) {
				lAutoApprove = (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getSellerAutoApproveInvoice()));
			} else if (lInstrumentBean.getCounterEntity().equals(lInstrumentBean.getPurchaser())) {
				lAutoApprove = (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getPurchaserAutoApproveInvoice()));
			}
			if (Status.Checker_Approved.equals(lInstrumentBean.getStatus())) {
				if (lAppEntityBean.hasHierarchicalChecker(AppConstants.INSTRUMENT_CHECKER)) {
					if (hasAccessToLevel(lInstrumentBean.getMkrChkLevel(), pUserBean.getInstLevel(), lAppEntityBean.getInstLevel())) {
						instrumentDAO.insertAudit(lConnection, lInstrumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());
						Long lNextlevel = getNextLevel(lConnection, lInstrumentBean.getMakerAuId(), lInstrumentBean.getMkrChkLevel(), CheckerType.Instrument);
						if (lNextlevel != null) {
							lInstrumentBean.setMkrChkLevel(lNextlevel);
							instrumentDAO.update(lConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_CHECKERLEVEL);
							lInstrumentBean.setStatus(Status.Submitted);
							insertInstrumentWorkFlow(lConnection, lInstrumentBean, pUserBean, lInstrumentBean.getStatusRemarks());
							return;
						}
					}

				}
			}
			if (lAutoApprove) {
				// so now change the status to approved directly
				//check - updateCheckerStatus
				lInstrumentBean.setStatus(InstrumentBean.Status.Counter_Approved);
				//
				MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
				AppEntityBean lUserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pUserBean.getDomain() });
				if (lUserEntityBean != null && lUserEntityBean.isPurchaser()) {
					boolean lAllowSplitting = CommonAppConstants.YesNo.Yes.equals(lUserEntityBean.getAllowObliSplitting());
					boolean lIsGroup = (CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag()));
					validatePurchaserMandate(lConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getNetAmount(), lInstrumentBean.getPurSettleClId(), lInstrumentBean.getInstDueDate(), lAllowSplitting, lIsGroup);
				}

			}
		}
		if (Status.Checker_Rejected.equals(lInstrumentBean.getStatus()) || Status.Checker_Returned.equals(lInstrumentBean.getStatus())) {
			if (lInstrumentBean.getStatusRemarks() == null) {
				throw new CommonBusinessException("Remarks are mandatory, please fill the remarks and try again.");
			}
		}
		if (MonetagoTredsHelper.getInstance().performMonetagoCheck(lInstrumentBean.getPurchaser())) {
			if ((StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) && !CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) && ((Status.Checker_Rejected.equals(lInstrumentBean.getStatus())))) {
				// no need to check the old and new bean for changes since the
				// checker cannot change any fields
				Map<String, String> lResult = new HashMap<String, String>();
				String lInfoMessage = "Instrument No :" + lInstrumentBean.getId();
				lResult = MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Rejected, lInfoMessage, lInstrumentBean.getId());
				if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
					lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
					lInstrumentBean.setMonetagoLedgerId("");
					logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
				} else {
					logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					throw new CommonBusinessException("Error while Rejecting : " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
				}

			}
		}
		if (Status.Checker_Returned.equals(lInstrumentBean.getStatus()) || Status.Checker_Rejected.equals(lInstrumentBean.getStatus())) {
			if (lAppEntityBean.hasHierarchicalChecker(AppConstants.INSTRUMENT_CHECKER)) {
				lInstrumentBean.setMkrChkLevel(new Long(0));
			}
			lInstrumentBean.setCheckerAuId(null);
		}
		instrumentDAO.update(lConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_UPDATECHECKERSTATUS);
		// agreement acceptance for instrument
		if (InstrumentBean.Status.Checker_Approved.equals(lInstrumentBean.getStatus()) && pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
			if (ClickWrapHelper.getInstance().isAgreementEnabled(lInstrumentBean.getPurchaser())) {
				ClickWrapHelper.getInstance().addCounterAcceptanceForInstrument(lConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getId(), pUserBean.getId(), true);
			}
		}

		if (lAutoApprove)
			instrumentDAO.insertAudit(lConnection, lInstrumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());

		InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(lConnection, lInstrumentBean, pUserBean, pFilterBean.getStatusRemarks());
		//
		emailGeneratorBO.sendInstrumentCheckerActionMailToMaker(lConnection, lInstrumentBean, lWorkFlowBean, pUserBean);
		if (lAutoApprove || lInstrumentBean.getStatus().equals(InstrumentBean.Status.Checker_Approved))
			emailGeneratorBO.sendInstrumentCounterInBoxMail(lConnection, lInstrumentBean, lWorkFlowBean);

		if (lAutoApprove) {
			// logic to convert the insturment to FU
			this.autoApproveByCounter(lConnection, lInstrumentBean, pUserBean);
		}
	}

	private void checkCounterAccess(InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		if (!pInstrumentBean.getCounterEntity().equals(pUserBean.getDomain()))
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
		// TODO: What to do about counter
		if (pUserBean.getType() != AppUserBean.Type.Admin) {
			if (!CommonAppConstants.Yes.Yes.equals(pUserBean.getFullOwnership())) {
				if ((pInstrumentBean.getCounterAuId() != null) && !pInstrumentBean.getCounterAuId().equals(pUserBean.getId())) {
					throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
				}
			}
		}
	}

	public String updateCounterStatus(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, AppUserBean pUserBean, InstrumentBean pApiFilterBean) throws Exception {
		return updateCounterStatus(pExecutionContext.getConnection(), pFilterBean, pUserBean, pApiFilterBean);
	}

	public String updateCounterStatus(Connection pConnection, InstrumentBean pFilterBean, AppUserBean pUserBean, InstrumentBean pApiFilterBean) throws Exception {
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		pConnection.setAutoCommit(false);
		Map<String, Object> lMsgMap = new HashMap<String, Object>();
		Boolean lCheckerApproval = false;
		List<MakerCheckerMapBean> lCheckers = null;
		InstrumentBean lInstrumentBean = findBean(pConnection, pFilterBean);

		if (VALID_STATUS != null && VALID_STATUS.contains(lInstrumentBean.getStatus().getCode())) {
			throw new CommonBusinessException("Invalid instrument status.");
		}

		if (InstrumentBean.Status.Counter_Rejected.equals(pFilterBean.getStatus()) && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
			throw new CommonBusinessException("Grouped Instrument can't be removed.");
		}
		
		if(lInstrumentBean.getIsGemInvoice() && Status.Counter_Returned.equals(pFilterBean.getStatus())){
			throw new CommonBusinessException("GEM instrument cannot be returned.");
		}

		
		// if
		// (InstrumentBean.Status.Counter_Returned.equals(pFilterBean.getStatus())
		// && CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag()))
		// {
		// throw new CommonBusinessException("Grouped Instrument can't be
		// returned.");
		// }
		if (Status.Checker_Approved.equals(lInstrumentBean.getStatus()) || Status.Counter_Checker_Return.equals(lInstrumentBean.getStatus())) {
			if (InstrumentBean.Status.Counter_Checker_Pending.equals(pFilterBean.getStatus())) {
				lCheckers = appUserBO.getCheckers(pConnection, pUserBean.getId(), MakerCheckerMapBean.CheckerType.InstrumentCounter);
				if (lCheckers == null || lCheckers.size() == 0) {
					//check - updateCounterStatus
					pFilterBean.setStatus(InstrumentBean.Status.Counter_Approved);
				}

			}
		} else if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus()) && Status.Counter_Approved.equals(pFilterBean.getStatus())) {
			lCheckerApproval = true;
		}
		if (InstrumentBean.Status.Counter_Approved.equals(pFilterBean.getStatus()) || InstrumentBean.Status.Counter_Checker_Pending.equals(pFilterBean.getStatus())) {
			TredsHelper.getInstance().validateUserLimit(pUserBean, lInstrumentBean.getAmount());
		}
		if (!lCheckerApproval && InstrumentBean.Status.Counter_Returned.equals(pFilterBean.getStatus())) {
			if (InstrumentBean.Status.Counter_Returned.equals(lInstrumentBean.getStatus()) || InstrumentBean.Status.Counter_Rejected.equals(lInstrumentBean.getStatus()))
				throw new CommonBusinessException("Invalid instrument status.");
		}
		lInstrumentBean.populateNonDatabaseFields();
		String lDisplayMsg = "";
		boolean lAutoApprove = false;
		Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
		String lFinalUpdateFieldGroup = InstrumentBean.FIELDGROUP_UPDATECOUNTERSTATUS;
		InstrumentBean lOldInstrumentBean = new InstrumentBean();
		instrumentDAO.getBeanMeta().copyBean(lInstrumentBean, lOldInstrumentBean);
		lOldInstrumentBean.populateNonDatabaseFields();
		// get purchaser supplier link
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(lInstrumentBean.getPurchaser());
		lPSLFilterBean.setSupplier(lInstrumentBean.getSupplier());
		lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
		if (lPurchaserSupplierLinkBean == null)
			throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");
		checkPlatformStatus(lPurchaserSupplierLinkBean);
		//
		if (Status.Checker_Approved.equals(lInstrumentBean.getStatus()) || Status.Counter_Checker_Return.equals(lInstrumentBean.getStatus())) {
			if(!lInstrumentBean.getIsGemInvoice()) {
				AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getPurchaser());
				if(lAEBean!=null) {
					AppEntityPreferenceBean lAEPrefBean =  lAEBean.getPreferences();
					if(lAEPrefBean!=null && CommonAppConstants.Yes.Yes.equals(lAEPrefBean.getCav())) {
						if(lInstrumentBean.getPurchaser().equals(lInstrumentBean.getCounterEntity())) {
							if(Status.Counter_Returned.equals(pFilterBean.getStatus())) {
								if(CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI())) {
									//if counter api user then, allow to return 
								}else {
									if(!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getIsApiVerified())) {
										throw new CommonBusinessException("Counter API has not verified the instrument.");
									}
								}
							}else {
								if(!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getIsApiVerified())) {
									throw new CommonBusinessException("Counter API has not verified the instrument.");
								}
							}
						}
					}
				}
			}
		}
		//
		if (Status.Counter_Checker_Pending.equals(pFilterBean.getStatus()) || Status.Counter_Approved.equals(pFilterBean.getStatus())) {
			String lCounterModifedJson = null;
			String lDispFieldGroup = null, lDbFieldGroup = null;
			if (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
				lDispFieldGroup = (CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI()) ? "counterBuyerDisplayApi" : "counterBuyerDisplay");
				lDbFieldGroup = (CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI()) ? "updateCounterBuyerDbApi" : "updateCounterBuyerDb");
			} else if (pUserBean.getDomain().equals(lInstrumentBean.getSupplier())) {
				lDispFieldGroup = (CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI()) ? "counterSellerDisplayApi" : "counterSellerDisplay");
				lDbFieldGroup = (CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI()) ? "updateCounterSellerDbApi" : "updateCounterSellerDb");
			}
			List<BeanFieldMeta> lFieldsMeta = instrumentDAO.getBeanMeta().getFieldMetaList(lDispFieldGroup, null);
			List<String> lDiffFields = new ArrayList<String>();
			//
			if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus()) && Status.Counter_Approved.equals(pFilterBean.getStatus())) {
				// TODO:PlatformCheck3X
				if (lAppEntityBean.hasHierarchicalChecker(AppConstants.INSTRUMENT_COUNTER_CHECKER)) {
					Long lMaxLevel = getLevel(pConnection, lCheckers, lInstrumentBean.getCounterAuId(), CheckerType.InstrumentCounter, false);
					if (hasAccessToLevel(lInstrumentBean.getCntChkLevel(), pUserBean.getInstCntrLevel(), lMaxLevel)) {
						instrumentDAO.insertAudit(pConnection, lInstrumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());
						Long lNextlevel = getNextLevel(pConnection, lInstrumentBean.getCounterAuId(), lInstrumentBean.getCntChkLevel(), CheckerType.InstrumentCounter);
						if (lNextlevel != null) {
							lInstrumentBean.setCntChkLevel(lNextlevel);
							lInstrumentBean.setStatusRemarks(pFilterBean.getStatusRemarks());
							instrumentDAO.update(pConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_COUNTERLEVEL);
							lInstrumentBean.setStatus(Status.Counter_Checker_Pending);
							insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, lInstrumentBean.getStatusRemarks());
							pConnection.commit();
							lMsgMap.put("message", "Instrument sent to checker.");
							return new JsonBuilder(lMsgMap).toString();
						}
					}else {
						if (lInstrumentBean.getCntChkLevel()==null && lMaxLevel==null) {
	
						}else if (lMaxLevel!=null && pUserBean.getInstCntrLevel().equals(lInstrumentBean.getCntChkLevel()) && lMaxLevel.equals(pUserBean.getInstCntrLevel())) {
							
						}else {
							throw new CommonBusinessException("Please Refresh the page");
						}
					}
				}
			} else {
				checkCounterAccess(lInstrumentBean, pUserBean);
			}
			InstrumentBean.Status lStatus = lInstrumentBean.getStatus();

			if (!(lStatus.equals(InstrumentBean.Status.Checker_Approved) || lStatus.equals(InstrumentBean.Status.Counter_Checker_Pending) || lStatus.equals(InstrumentBean.Status.Counter_Approved) || lStatus.equals(InstrumentBean.Status.Counter_Checker_Return)))
				throw new CommonBusinessException("Invalid Instrument Status.");

			if (!(pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Approved) || pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Rejected) || pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Returned) || pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Checker_Pending)))
				throw new CommonBusinessException("Counter can only Approve/Reject/Return.");

			if (!Objects.isNull(pFilterBean.getCashDiscountPercent()) && pFilterBean.getCashDiscountPercent().compareTo(lPurchaserSupplierLinkBean.getCashDiscountPercent()) == 1) {
				throw new CommonBusinessException("Cash Discount Percent cannot be greater than " + lPurchaserSupplierLinkBean.getCashDiscountPercent());
			}
			//
			if ((pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Approved) || pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Checker_Pending))) {
				//
				// specific to iocl. and now for BHEL Ranipet also
				// checked the iocl incoming request it doesnot have
				// counterRefNum, adjAmount, netAmount, goodsAcceptanceDate
				InstrumentBean lTempBean = null;
				if (pFilterBean.getSupplier() == null) {
					lTempBean = instrumentDAO.findByPrimaryKey(pConnection, pFilterBean);
					lTempBean.populateNonDatabaseFields();
					lTempBean.setStatus(pFilterBean.getStatus());
					lTempBean.setStatusRemarks(pFilterBean.getStatusRemarks());
					if (Status.Counter_Approved.equals(pFilterBean.getStatus()) || Status.Counter_Checker_Pending.equals(pFilterBean.getStatus())) {
						lTempBean.setAdjAmount(pFilterBean.getAdjAmount());
						lTempBean.setTdsAmount(pFilterBean.getTdsAmount());
						lTempBean.setNetAmount(pFilterBean.getNetAmount());
						lTempBean.setAmount(pFilterBean.getAmount());
						updateNetAmount(lTempBean, lTempBean.getIsAllowedToRecalculateFromPercent(), true, true);
						//
						if (StringUtils.isNotEmpty(pFilterBean.getCounterRefNum())) {
							lTempBean.setCounterRefNum(pFilterBean.getCounterRefNum());
						}
						//
						if (pFilterBean.getGoodsAcceptDate() != null) {
							lTempBean.setGoodsAcceptDate(pFilterBean.getGoodsAcceptDate());
						}
						//
						if (pFilterBean.getCreditPeriod() != null) {
							lTempBean.setCreditPeriod(pFilterBean.getCreditPeriod());
						} else if (pFilterBean.getInstDueDate() != null) {
							lTempBean.setInstDueDate(pFilterBean.getInstDueDate());
							// Recalculate Dates
							Date lDueDate = OtherResourceCache.getInstance().getNextClearingDate(pFilterBean.getInstDueDate(), 0);
							lTempBean.setCreditPeriod(OtherResourceCache.getInstance().getDiffInDays(lDueDate, lTempBean.getInstDate()));
						}
						setUpdatedDueDates(lTempBean);
					}
					pFilterBean = lTempBean;
				} else {
					//
					if (StringUtils.isNotEmpty(pFilterBean.getCounterRefNum())) {
						lInstrumentBean.setCounterRefNum(pFilterBean.getCounterRefNum());
					}
					updateNetAmount(lInstrumentBean, lInstrumentBean.getIsAllowedToRecalculateFromPercent(), true);
					//
				}
				//
				if (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
					MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
					AppEntityBean lUserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pUserBean.getDomain() });
					if (lUserEntityBean.isPurchaser()) {
						boolean lAllowSplitting = CommonAppConstants.YesNo.Yes.equals(lUserEntityBean.getAllowObliSplitting());
						boolean lIsGroup = (CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag()));
						validatePurchaserMandate(pConnection, lInstrumentBean.getPurchaser(), (pFilterBean.getNetAmount() != null ? pFilterBean.getNetAmount() : lInstrumentBean.getNetAmount()), (pFilterBean.getPurSettleClId() != null ? pFilterBean.getPurSettleClId() : lInstrumentBean.getPurSettleClId()), (pFilterBean.getInstDueDate() != null ? pFilterBean.getInstDueDate() : lInstrumentBean.getInstDueDate()), lAllowSplitting, lIsGroup);
					}
				}
				// setting of counterAuId or ownerAuId if the current loggedin
				// user
				// is counter or owner
				if (!lCheckerApproval) {
					if (pUserBean.getDomain().equals(lInstrumentBean.getCounterEntity())) {
						if (lInstrumentBean.getCounterAuId() == null)
							lInstrumentBean.setCounterAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
					}
					if (pUserBean.getDomain().equals(lInstrumentBean.getOwnerEntity())) {
						if (lInstrumentBean.getOwnerAuId() == null)
							lInstrumentBean.setOwnerAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
					}
				}
				//
				if (!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag()) && lFieldsMeta != null) {
					if (!lCheckerApproval) {
						BeanFieldMeta lFieldMeta = null;
						Object lOldVal = null, lNewVal;
						InstrumentBean lTempOldBean = instrumentDAO.findByPrimaryKey(pConnection, pFilterBean);
						Map<String, Object> lMap = new HashMap<String, Object>();
						JsonSlurper lJsonSlurper = new JsonSlurper();
						if (Status.Counter_Checker_Return.equals(lInstrumentBean.getStatus()) && Status.Counter_Checker_Pending.equals(pFilterBean.getStatus())) {
							if (lInstrumentBean.getCounterModifiedFields() != null) {
								lMap = (Map<String, Object>) lJsonSlurper.parseText(lInstrumentBean.getCounterModifiedFields());
								instrumentDAO.getBeanMeta().validateAndParse(lTempOldBean, lMap, null, null);
								List<String> lCounterModFields = new ArrayList(lMap.keySet());
								instrumentDAO.getBeanMeta().copyBean(lTempOldBean, lInstrumentBean, null, lCounterModFields);
								//
								// Recalculate Dates
								setUpdatedDueDates(lInstrumentBean);
								//
								// Recalculate amount
								updateNetAmount(lInstrumentBean, true, true);
								//
								lInstrumentBean.setCounterModifiedFields(null);
								//
							}
						}

						for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
							lFieldMeta = lFieldsMeta.get(lPtr);
							lOldVal = lFieldMeta.getProperty(lInstrumentBean);
							lNewVal = lFieldMeta.getProperty(pFilterBean);
							if (lOldVal == null && lNewVal == null) {
								// both are same - null
							} else if (lOldVal != null && lNewVal != null) {
								if (lOldVal instanceof BigDecimal) {
									if ((((BigDecimal) lOldVal).compareTo((BigDecimal) lNewVal)) != 0)
										lDiffFields.add(lFieldMeta.getName());
								} else {
									if (!lOldVal.equals(lNewVal))
										lDiffFields.add(lFieldMeta.getName());
								}
							} else if (lOldVal == null && (lNewVal instanceof BigDecimal) && (BigDecimal.ZERO.compareTo((BigDecimal) lNewVal) == 0)) {
								// skip
							} else if (lNewVal == null && (lOldVal instanceof BigDecimal) && (BigDecimal.ZERO.compareTo((BigDecimal) lOldVal) == 0)) {
								// skip
							} else {
								lDiffFields.add(lFieldMeta.getName());
							}
						}
						// location changed for self, auto convert changed by
						// bearer,
						// auto accept changed by bearer - then no need to send
						// to
						// originator
						if (lDiffFields.size() > 0) {
							boolean lIsChargeBearer = false;
							if (pApiFilterBean != null) {
								pApiFilterBean.setPurchaser(lInstrumentBean.getPurchaser());
								pApiFilterBean.setSupplier(lInstrumentBean.getSupplier());
								if (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
									if (lDiffFields.contains("purClId") || lDiffFields.contains("purLocation") || lDiffFields.contains("purGstn")) {
										validateAndSetPurchaserLocationDetails(pConnection, pApiFilterBean, pUserBean);
									}
									if (lDiffFields.contains("supClId") || lDiffFields.contains("supLocation") || lDiffFields.contains("supGstn")) {
										throw new CommonBusinessException("Supplier location  cannot be changed.");
									} else {
										lInstrumentBean.setPurClId(pApiFilterBean.getPurClId());
										lInstrumentBean.setPurSettleClId(pApiFilterBean.getPurSettleClId());
										lInstrumentBean.setPurGstState(pApiFilterBean.getPurGstState());
										lInstrumentBean.setPurGstn(pApiFilterBean.getPurGstn());
										lInstrumentBean.setPurLocation(pApiFilterBean.getPurLocation());
										lInstrumentBean.setPurBillClId(pApiFilterBean.getPurBillClId());
									}
								}
								if (pUserBean.getDomain().equals(lInstrumentBean.getSupplier())) {
									if (lDiffFields.contains("supClId") || lDiffFields.contains("supLocation") || lDiffFields.contains("supGstn")) {
										validateAndSetSupplierLocationDetails(pConnection, pApiFilterBean, pUserBean);
									}
									if (lDiffFields.contains("purClId") || lDiffFields.contains("purLocation") || lDiffFields.contains("purGstn")) {
										throw new CommonBusinessException("Purchaser location  cannot be changed.");
									} else {
										lInstrumentBean.setSupClId(pApiFilterBean.getSupClId());
										lInstrumentBean.setSupSettleClId(pApiFilterBean.getSupSettleClId());
										lInstrumentBean.setSupGstState(pApiFilterBean.getSupGstState());
										lInstrumentBean.setSupGstn(pApiFilterBean.getSupGstn());
										lInstrumentBean.setSupLocation(pApiFilterBean.getSupLocation());
										lInstrumentBean.setSupBillClId(pApiFilterBean.getSupBillClId());
									}
								}
							}
							if (pUserBean.getDomain().equals(lInstrumentBean.getSupplier())) {
								if (lDiffFields.contains("supClId")) {
									lDiffFields.remove("supClId");
								}
								lIsChargeBearer = AppConstants.CostBearer.Seller.equals(pFilterBean.getChargeBearer());
							} else if (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
								if (lDiffFields.contains("purClId")) {
									lDiffFields.remove("purClId");
								}
								lIsChargeBearer = AppConstants.CostBearer.Buyer.equals(pFilterBean.getChargeBearer());
							}
							if (lIsChargeBearer) {
								lDiffFields.remove("chargeBearer");
							}
						}
						if (!lInstrumentBean.getCostBearingType().equals(pFilterBean.getCostBearingType())) {
							lDiffFields.add("costBearingType");
						}
						if (CostBearingType.Percentage_Split.equals(pFilterBean.getCostBearingType())) {
							if (!lInstrumentBean.getBuyerPercent().equals(pFilterBean.getBuyerPercent())) {
								lDiffFields.add("buyerPercent");
								lDiffFields.add("sellerPercent");
							}
							if (!lInstrumentBean.getBidAcceptingEntityType().equals(pFilterBean.getBidAcceptingEntityType())) {
								lDiffFields.add("bidAcceptingEntityType");
							}
						} else if (CostBearingType.Periodical_Split.equals(pFilterBean.getCostBearingType())) {
							if (!lInstrumentBean.getSplittingPoint().equals(pFilterBean.getSplittingPoint())) {
								lDiffFields.add("splittingPoint");
							}
							if (!lInstrumentBean.getPreSplittingCostBearer().equals(pFilterBean.getPreSplittingCostBearer())) {
								lDiffFields.add("preSplittingCostBearer");
								lDiffFields.add("postSplittingCostBearer");
							}
							if (!lInstrumentBean.getBidAcceptingEntityType().equals(pFilterBean.getBidAcceptingEntityType())) {
								lDiffFields.add("bidAcceptingEntityType");
							}
						} else {
							lDiffFields.remove("bidAcceptingEntityType");
						}
						lDiffFields.remove("status");
						lDiffFields.remove("sellerPercent");
						lDiffFields.remove("counterModifiedFields");
						if (lDiffFields.size() > 0) {
							lCounterModifedJson = instrumentDAO.getBeanMeta().formatAsJson(lInstrumentBean, null, lDiffFields, false);
						}
					} else {
						lCounterModifedJson = lInstrumentBean.getCounterModifiedFields();
						if (CommonUtilities.hasValue(lCounterModifedJson)) {
							JsonSlurper lJsonSlurper = new JsonSlurper();
							Map<String, Object> lDiffMap = (Map<String, Object>) lJsonSlurper.parseText(lCounterModifedJson);
							lDiffFields = new ArrayList<String>(lDiffMap.keySet());
						}
					}
				}
			}
			pFilterBean.setCounterModifiedFields(lCounterModifedJson);
			if (CommonUtilities.hasValue(lCounterModifedJson)) {
				instrumentDAO.getBeanMeta().copyBean(pFilterBean, lInstrumentBean, lFieldsMeta);
				// TODO: HERE WE HAVE TO RECOMPUTE THE DATES IF THE CREDITDAYS
				// OR
				if (lDiffFields.contains("creditPeriod") || lDiffFields.contains("extendedCreditPeriod") || lDiffFields.contains("enableExtension") || lDiffFields.contains("goodsAcceptDate")) {
					// recalculate extended due date
					setUpdatedDueDates(lInstrumentBean);
				}
				validateCreditPeriod(lInstrumentBean.getCreditPeriod(), lInstrumentBean.getEnableExtension(), lInstrumentBean.getExtendedCreditPeriod());
				if (lInstrumentBean.getMakerEntity().equals(lInstrumentBean.getSupplier())) {
					lAutoApprove = (CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getSellerAutoApproveInvoice()));
				}
				if (!lAutoApprove) {
					if (lCheckers != null && !lCheckers.isEmpty() && lInstrumentBean.getCntChkLevel() == null) {
						lInstrumentBean.setCntChkLevel(getLevel(pConnection, lCheckers, null, CheckerType.InstrumentCounter, true));
						lInstrumentBean.setStatus(InstrumentBean.Status.Counter_Checker_Pending);
						lInstrumentBean.setCounterModifiedFields(lCounterModifedJson);
						instrumentDAO.update(pConnection, lInstrumentBean, lDbFieldGroup);
						InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, pFilterBean.getStatusRemarks());
						pConnection.commit();
						// email
						emailGeneratorBO.sendInstrumentCounterActionMail(pConnection, lInstrumentBean, lWorkFlowBean, pUserBean);
						//
						if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus())) {
							lMsgMap.put("message", "Instrument sent to checker.");
						}
						lMsgMap.put("currentStatus", lInstrumentBean.getStatus().getCode());
						return new JsonBuilder(lMsgMap).toString();
					} else {
						lInstrumentBean.setStatus(InstrumentBean.Status.Counter_Returned);
						lInstrumentBean.setCounterModifiedFields(lCounterModifedJson);
						lInstrumentBean.setCounterAuId(null);
						lInstrumentBean.setCounterCheckerAuId(null);
						if (MonetagoTredsHelper.getInstance().performMonetagoCheck(lInstrumentBean.getPurchaser())) {
							if (StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) && (Status.Counter_Returned.equals(lInstrumentBean.getStatus())) && !CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
								if (monetagoFieldModificationCheck(lOldInstrumentBean, lInstrumentBean)) {
									Map<String, String> lResult = new HashMap<String, String>();
									String lInfoMessage = "Instrument No :" + lInstrumentBean.getId();
									lResult = MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Rejected, lInfoMessage, lInstrumentBean.getId());
									if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
										lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
										lInstrumentBean.setMonetagoLedgerId("");
										logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
									} else {
										logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
										throw new CommonBusinessException("Error while Rejecting : " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
									}
								}
							}
						}
						instrumentDAO.update(pConnection, lInstrumentBean, lDbFieldGroup);
						InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, pFilterBean.getStatusRemarks());
						// if counter is seller and returns the instrument
						if (InstrumentBean.Status.Counter_Returned.equals(lInstrumentBean.getStatus()) && !pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())) {
							if (ClickWrapHelper.getInstance().isAgreementEnabled(lInstrumentBean.getPurchaser())) {
								List<IAgreementAcceptanceBean> lPAAList = ClickWrapHelper.getInstance().getAcceptedAggrementDetails(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getId());
								ClickWrapHelper.getInstance().removeClickWrapAgreements(pConnection, lPAAList, pUserBean.getId());
							}
						}
						pConnection.commit();
						// email
						emailGeneratorBO.sendInstrumentCounterActionMail(pConnection, lInstrumentBean, lWorkFlowBean, pUserBean);
						//
						lMsgMap.put("message", "Instrument returned to originator on account of changes.");
						lMsgMap.put("currentStatus", lInstrumentBean.getStatus().getCode());
						return new JsonBuilder(lMsgMap).toString();
					}
				}
			}
			// update the current bean
			if (!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
				instrumentDAO.getBeanMeta().copyBean(pFilterBean, lInstrumentBean, lFieldsMeta);
				instrumentDAO.getBeanMeta().copyBean(pFilterBean, lInstrumentBean, "ewayBill", null);
			}
			lFinalUpdateFieldGroup = (lAutoApprove ? "updateCounterAutoApprove" : "updateCounterApprove");
			//
			validateCreditPeriod(lInstrumentBean.getCreditPeriod(), lInstrumentBean.getEnableExtension(), lInstrumentBean.getExtendedCreditPeriod());
			//
		}
		//
		lInstrumentBean.setStatusUpdateTime(lCurrentTime);
		lInstrumentBean.setStatus(pFilterBean.getStatus());
		if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus())) {
			lInstrumentBean.setCntChkLevel(getLevel(pConnection, lCheckers, null, CheckerType.InstrumentCounter, true));
		}
		lInstrumentBean.setStatusRemarks(pFilterBean.getStatusRemarks());

		// TODO: check whether this is required.
		/*
		 * if (pUserBean.getDomain().equals(lInstrumentBean.getSupplier()) &&
		 * lInstrumentBean.getAutoAccept() == null) {
		 * lInstrumentBean.setAutoAccept(pFilterBean.getAutoAccept()); }
		 */
		/*
		 * else if
		 * (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())&&
		 * lInstrumentBean.getCostBearer() == null) {
		 * lInstrumentBean.setCostBearer(pFilterBean.getCostBearer()); }
		 */

		if (lCheckerApproval) {
			lInstrumentBean.setCounterCheckerAuId(pUserBean.getId());
		} else {
			lInstrumentBean.setCounterAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
			lInstrumentBean.setCounterEntity(pUserBean.getDomain());
			lInstrumentBean.setCounterLoginId(pUserBean.getLoginId());
			if (Status.Counter_Checker_Pending.equals(lInstrumentBean.getStatus())) {
				lInstrumentBean.setCntChkLevel(getLevel(pConnection, lCheckers, null, CheckerType.InstrumentCounter, true));
			}
			if (pUserBean.getDomain().equals(lInstrumentBean.getOwnerEntity())) {
				lInstrumentBean.setOwnerAuId(TredsHelper.getInstance().getOwnerAuId(pUserBean));
			}
		}
		if (MonetagoTredsHelper.getInstance().performMonetagoCheck(lInstrumentBean.getPurchaser())) {
			if ((StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) && !CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) && ((Status.Counter_Rejected.equals(lInstrumentBean.getStatus())) || (Status.Counter_Returned.equals(lInstrumentBean.getStatus()) || (lAutoApprove && Status.Counter_Approved.equals(pFilterBean.getStatus()))))) {
				if (Status.Counter_Rejected.equals(lInstrumentBean.getStatus()) || monetagoFieldModificationCheck(lOldInstrumentBean, lInstrumentBean)) {
					Map<String, String> lResult = new HashMap<String, String>();
					String lInfoMessage = "Instrument No :" + lInstrumentBean.getId();
					lResult = MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Rejected, lInfoMessage, lInstrumentBean.getId());
					if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
						lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
						lInstrumentBean.setMonetagoLedgerId("");
						logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					} else {
						logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						throw new CommonBusinessException("Error while Rejecting : " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
					}

					if (lAutoApprove && StringUtils.isBlank(lInstrumentBean.getMonetagoLedgerId())) {
						// now reregister
						lInfoMessage = " Invoice No :" + lInstrumentBean.getInstNumber() + " Instrument No :" + lInstrumentBean.getId();
						lResult = MonetagoTredsHelper.getInstance().register(lInstrumentBean.getSupGstn(), lInstrumentBean.getPurGstn(), lInstrumentBean.getInstNumber(), lInstrumentBean.getInstDate(), lInstrumentBean.getAmount(), lInfoMessage, lInstrumentBean.getId());
						if (StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID))) {
							lInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
							logger.info("Message Success :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						} else {
							lInstrumentBean.setMonetagoLedgerId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_LEDGERID));
							logger.info("Message Error :  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
							instrumentDAO.update(pConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_UPDATEMONETAGOCANCEL);
							pConnection.commit();
							throw new CommonBusinessException("Error while approving (Monetago registration fail): " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						}
					}
				}
			}
		}
		if (Status.Counter_Rejected.equals(lInstrumentBean.getStatus()) || Status.Counter_Returned.equals(lInstrumentBean.getStatus())) {
			lInstrumentBean.setCounterCheckerAuId(null);
			if (lInstrumentBean.getStatusRemarks() == null) {
				throw new CommonBusinessException("Remarks are mandatory, please fill the remarks and try again.");
			}
		}
		//
		// TODO: SHOULD WE RECALCUALATE HERE?
		// TODO : NEW LINE ADDITION
		// updateNetAmount(lInstrumentBean,true,true);
		instrumentDAO.update(pConnection, lInstrumentBean, lFinalUpdateFieldGroup);
		instrumentDAO.insertAudit(pConnection, lInstrumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());
		if (!pUserBean.getDomain().equals(lInstrumentBean.getPurchaser()) && (Status.Counter_Rejected.equals(lInstrumentBean.getStatus()) || Status.Counter_Returned.equals(lInstrumentBean.getStatus()))) {
			// if the instrument is count_rej or ret then delete the record only
			// if counter is seller
			if (ClickWrapHelper.getInstance().isAgreementEnabled(lInstrumentBean.getPurchaser())) {
				List<IAgreementAcceptanceBean> lPAAList = ClickWrapHelper.getInstance().getAcceptedAggrementDetails(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getId());
				ClickWrapHelper.getInstance().removeClickWrapAgreements(pConnection, lPAAList, pUserBean.getId());
			}
		} else if (pUserBean.getDomain().equals(lInstrumentBean.getPurchaser()) && (Status.Counter_Approved.equals(lInstrumentBean.getStatus()))) {
			// if the instrument is count_app insert the record only if counter
			// is buyer
			if (ClickWrapHelper.getInstance().isAgreementEnabled(lInstrumentBean.getPurchaser())) {
				ClickWrapHelper.getInstance().addCounterAcceptanceForInstrument(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getId(), pUserBean.getId(), false);
			}
		}
		InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, pFilterBean.getStatusRemarks());

		String lFUConversionMsg = ""; // limit error message
		if (pFilterBean.getStatus().equals(InstrumentBean.Status.Counter_Approved)) {
			lInstrumentBean.populateNonDatabaseFields();
			lFUConversionMsg = convertToFactoringUnit(pConnection, lInstrumentBean, pUserBean, false, lInstrumentBean.getAutoConvert() == lInstrumentBean.getAutoConvert().Auto ? FactoringUnitBean.Status.Active : FactoringUnitBean.Status.Ready_For_Auction);
		}
		// email
		lDisplayMsg = emailGeneratorBO.sendInstrumentCounterActionMail(pConnection, lInstrumentBean, lWorkFlowBean, pUserBean);
		//
		lMsgMap.put("message", lDisplayMsg + (CommonUtilities.hasValue(lFUConversionMsg) ? "<br>" + lFUConversionMsg : ""));
		lMsgMap.put("currentStatus", lInstrumentBean.getStatus().getCode());
		pConnection.commit();
		return new JsonBuilder(lMsgMap).toString();
	}

	public void counterModification(Connection pConnection,  Map<String,Object> pFilterMap, AppUserBean pUserBean) throws Exception {
		//check whether the user modifining is an api user
		//from the filter fetch the actual instrument bean - check the status of the instrument to be checker approved
		//compare the old bean and the filterbean received and make a list of differences
		//store the diffrences as a map in a instrument column as json string.
		//
		//in the get function of the instrument bean - if it is checker approved and contains a nonblank counterModification json column , 
		// then from the json - update the bean itself and then send accross
		//
        InstrumentBean lNewInstrumentBean = new InstrumentBean();
		InstrumentBean lOldInstrumentBean = null;
		InstrumentBean lFilterBean = new InstrumentBean();
		Map<String,Object> lDiffData = null;
        List<String> lFieldsRecdInBean = new ArrayList<String>();
        BeanMeta lInstrumentBeanMeta = instrumentDAO.getBeanMeta();
		//
		if(!CommonAppConstants.Yes.Yes.equals(pUserBean.getEnableAPI())) {
			throw new CommonBusinessException("Only API user can modify instrument received to counter.");
		}
		AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		if(lAEBean!=null) {
			AppEntityPreferenceBean lAEPrefBean =  lAEBean.getPreferences();
			if(lAEPrefBean!=null && !CommonAppConstants.Yes.Yes.equals(lAEPrefBean.getCav())) {
				throw new CommonBusinessException("Counter API verification not enable for entity.");
			}
		}
		//
        lInstrumentBeanMeta.validateAndParse(lNewInstrumentBean, pFilterMap, InstrumentBean.FIELDGROUP_COUNTERMAKERUPDATEFIELDS, null);
        if(lNewInstrumentBean.getId()==null) {
        	throw new CommonBusinessException("Instrument id is mandatory.");
        }
        //for finding fields received - to update in bean
        if(pFilterMap!=null) {
        	List<BeanFieldMeta> lFields = lInstrumentBeanMeta.getFieldMetaList(InstrumentBean.FIELDGROUP_COUNTERMAKERUPDATEFIELDS, null);
        	List<String> lBeanFieldKeys = new ArrayList<String>();
        	for(BeanFieldMeta lBeanFieldMeta : lFields) {
        		lBeanFieldKeys.add(lBeanFieldMeta.getName());
        	}
        	for(String lKey : pFilterMap.keySet()) {
        		if(!"id".equals(lKey)) {
        			if(lBeanFieldKeys.contains(lKey)) {
                    	lFieldsRecdInBean.add(lKey);
        			}
        		}
        	}
        }
        //
		lFilterBean.setId(lNewInstrumentBean.getId());
		lOldInstrumentBean = instrumentDAO.findByPrimaryKey(pConnection, lFilterBean);
		if(lOldInstrumentBean==null) {
			throw new CommonBusinessException("Instrument not found.");
		}
		if(!(InstrumentBean.Status.Checker_Approved.equals(lOldInstrumentBean.getStatus()) ||
				InstrumentBean.Status.Counter_Checker_Return.equals(lOldInstrumentBean.getStatus()))) {
			throw new CommonBusinessException("Invalid status while modification by counter.");
		}
		if(!pUserBean.getDomain().equals(lOldInstrumentBean.getCounterEntity())) {
			throw new CommonBusinessException("Only counter can modify.");
		}
		lDiffData = getFieldListDiff(instrumentDAO, lOldInstrumentBean, lNewInstrumentBean, InstrumentBean.FIELDGROUP_COUNTERMAKERUPDATEFIELDS, lFieldsRecdInBean);
		if(lDiffData!=null && lDiffData.size() > 0) {
			lOldInstrumentBean.setCounterUpdateFields(new JsonBuilder(lDiffData).toString());
		}else {
			//blank map stored if nothing has changed, so that we can flag that updating was received from counter api.
			lOldInstrumentBean.setCounterUpdateFields("{}");
		}
		//We have to validate whether the amount and dates received are correct.
		//but post validation the update values should not go to the db, since they are tempoary.
		//hence copying in temporary variable and validating the same, but saving the non modified values.
		InstrumentBean lTmpValidateBean = new InstrumentBean();
		instrumentDAO.getBeanMeta().copyBean(lOldInstrumentBean, lTmpValidateBean);
		updateAndValidateFieldsFromCounterUpdate(lTmpValidateBean);
		//
		instrumentDAO.update(pConnection, lOldInstrumentBean, InstrumentBean.FIELDGROUP_COUNTERMAKERUPDATE);
		//
	}
	
	private void updateBean(InstrumentBean pInstrumentBean, Map<String,Object> pDataToUpdate) {
		if(pDataToUpdate!=null) {
			BeanMeta lBeanMeta = instrumentDAO.getBeanMeta();
			lBeanMeta.validateAndParse(pInstrumentBean, pDataToUpdate, "FiledGroupToUpdate", null, null);
		}
	}
	
	private Map<String,Object> getFieldListDiff(GenericDAO pGenericDAO, Object pOldBean, Object pNewBean, String pChangeFieldGroup, List<String> pFieldsRecdInBean) {
		//only those files would be compared which need
		List<BeanFieldMeta> lFieldsMeta = pGenericDAO.getBeanMeta().getFieldMetaList(pChangeFieldGroup, null);
		BeanFieldMeta lFieldMeta = null;
		Object lOldVal = null, lNewVal;
		Map<String,Object> lDiffHash = new HashMap<String,Object>();
		//
		for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
			lFieldMeta = lFieldsMeta.get(lPtr);
			if(!pFieldsRecdInBean.contains(lFieldMeta.getName())) {
				continue;
			}
			lOldVal = lFieldMeta.getProperty(pOldBean);
			lNewVal = lFieldMeta.getProperty(pNewBean);
			if (lOldVal == null && lNewVal == null) {
				// both are same - null
			} else if (lOldVal != null && lNewVal != null) {
				if (lOldVal instanceof BigDecimal) {
					if ((((BigDecimal) lOldVal).compareTo((BigDecimal) lNewVal)) != 0) {
						lDiffHash.put(lFieldMeta.getName(), lNewVal);						
					}
				} else {
					if (!lOldVal.equals(lNewVal)) {
						if(DataType.DATE.equals(lFieldMeta.getDataType()) && lNewVal!=null) {
							lNewVal =  FormatHelper.getDisplay(AppConstants.DATE_FORMAT, (Date) lNewVal);
						}
						lDiffHash.put(lFieldMeta.getName(), lNewVal);						
					}
				}
			} else if (lOldVal == null && (lNewVal instanceof BigDecimal)	&& (BigDecimal.ZERO.compareTo((BigDecimal) lNewVal) == 0)) {
				// skip
			} else if (lNewVal == null && (lOldVal instanceof BigDecimal) && (BigDecimal.ZERO.compareTo((BigDecimal) lOldVal) == 0)) {
				// skip
			} else {
				if(DataType.DATE.equals(lFieldMeta.getDataType()) && lNewVal!=null) {
					lNewVal =  FormatHelper.getDisplay(AppConstants.DATE_FORMAT, (Date) lNewVal);
				}
				lDiffHash.put(lFieldMeta.getName(), lNewVal);						
			}
		}
		if (lDiffHash.size() > 0) {
			return lDiffHash;
		}
		return null;
	}


	private Map<String,Object> getExtraPayloadRecd(GenericDAO pGenericDAO,  Map<String,Object> pPayLoad) {
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
	public void updateNetAmount(InstrumentBean pInstrumentBean, boolean pComputeFromPercentage, boolean pCalculateAndSet) throws CommonBusinessException {
		updateNetAmount(pInstrumentBean, pComputeFromPercentage, pCalculateAndSet, false);
	}

	public void updateNetAmount(InstrumentBean pInstrumentBean, boolean pComputeFromPercentage, boolean pCalculateAndSet, boolean pSkipNetAmountCheck) throws CommonBusinessException {
		// calculate on basis of percentages received and compare the values
		// with received values
		// VALIDATE HAIRCUTVALUE
		if (pComputeFromPercentage && pInstrumentBean.getHaircutPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getHaircutPercent())) {
			BigDecimal lHaircutValue = (pInstrumentBean.getAmount().multiply(pInstrumentBean.getHaircutPercent(), MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			if (pCalculateAndSet) {
				pInstrumentBean.setAdjAmount(lHaircutValue);
			} else {
				if (pInstrumentBean.getAdjAmount().compareTo(lHaircutValue) != 0)
					throw new CommonBusinessException("Haricut Value received : " + pInstrumentBean.getAdjAmount() + " while computed " + pInstrumentBean.getHaircutPercent() + " on " + pInstrumentBean.getAmount() + " : " + lHaircutValue);
			}
		}
		// VALIDATE CASHDISCOUNT VALUE
		if (pComputeFromPercentage && pInstrumentBean.getCashDiscountPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getCashDiscountPercent())) {
			BigDecimal lCashDiscValue = (pInstrumentBean.getAmount().multiply(pInstrumentBean.getCashDiscountPercent(), MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			if (pCalculateAndSet) {
				pInstrumentBean.setCashDiscountValue(lCashDiscValue);
			} else {
				if (pInstrumentBean.getCashDiscountValue().compareTo(lCashDiscValue) != 0)
					throw new CommonBusinessException("CashDiscount Value received : " + pInstrumentBean.getCashDiscountValue() + " while computed " + pInstrumentBean.getCashDiscountPercent() + " on " + pInstrumentBean.getAmount() + " : " + lCashDiscValue);
			}
		}
		BigDecimal lNetAmount = pInstrumentBean.getAmount();
		if (pInstrumentBean.getAdjAmount() != null)
			lNetAmount = lNetAmount.subtract(pInstrumentBean.getAdjAmount());
		if (pInstrumentBean.getCashDiscountValue() != null)
			lNetAmount = lNetAmount.subtract(pInstrumentBean.getCashDiscountValue());
		if (pInstrumentBean.getTdsAmount() != null)
			lNetAmount = lNetAmount.subtract(pInstrumentBean.getTdsAmount());
		if (lNetAmount.compareTo(BigDecimal.ZERO) < 0)
			throw new CommonBusinessException("Factoring unit cost cannot be zero or negative.");
		if (pInstrumentBean.getNetAmount() != null) {
			if (!pSkipNetAmountCheck && lNetAmount.compareTo(pInstrumentBean.getNetAmount()) != 0)
				throw new CommonBusinessException("Net amount does not tally as per calculations.");
		}
		pInstrumentBean.setNetAmount(lNetAmount);
	}

	private void autoApproveByCounter(Connection pConnection, InstrumentBean pInstrumentBean, AppUserBean pUserBean) throws Exception {
		pInstrumentBean.populateNonDatabaseFields();
		convertToFactoringUnit(pConnection, pInstrumentBean, pUserBean, false, pInstrumentBean.getAutoConvert() == pInstrumentBean.getAutoConvert().Auto ? FactoringUnitBean.Status.Active : FactoringUnitBean.Status.Ready_For_Auction);
		// email
		emailGeneratorBO.sendInstrumentCounterActionMail(pConnection, pInstrumentBean, null, pUserBean);
	}

	public InstrumentBean updateStatusUsingFactoringUnit(Connection pConnection, InstrumentBean pInstrumentBean, InstrumentBean.Status pStatus, IAppUserBean pUserBean) throws Exception {
		if (pInstrumentBean == null)
			return null;
		// if instrument is being marked as factored. mark the same in dedupe
		// registry
		HashMap<Long, String> lFactorHash = new HashMap<Long, String>();
		// TODO:PlatformCheck4
		if (InstrumentBean.Status.Factored.equals(pStatus)) {
			if (MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser())) {
				// TODO ascertain from business if instruments without ledger id
				// should be skipped from marking in dedup registry
				if (CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())) {
					HashMap lInstClubSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_INSTCLUBSPLITTING);
					Integer lMaxInstruments = Integer.valueOf(lInstClubSettings.get(AppConstants.ATTRIBUTE_MAXNOOFFACTINSTRUMENTS).toString());
					List<InstrumentBean> lClubbedInstList = getClubbedBeans(pConnection, pInstrumentBean.getId());
					Map<Long, String> lLegderMap = new HashMap<Long, String>();
					List<Map<Long, String>> lLedgerMapList = new ArrayList<>();
					lLedgerMapList.add(lLegderMap);
					Map<Long, InstrumentBean> lInstChildHash = new HashMap<Long, InstrumentBean>();
					for (InstrumentBean lBean : lClubbedInstList) {
						if (lBean.getMonetagoLedgerId() != null && CommonUtilities.hasValue(lBean.getMonetagoLedgerId())) {
							if (lLegderMap.size() == lMaxInstruments.intValue()) {
								lLegderMap = new HashMap<Long, String>();
								lLedgerMapList.add(lLegderMap);
							}
							lLegderMap.put(lBean.getId(), lBean.getMonetagoLedgerId());
							lInstChildHash.put(lBean.getId(), lBean);
						}
					}
					pInstrumentBean.setGroupedInstruments(lClubbedInstList);
					for (Map<Long, String> lTmpLegderMap : lLedgerMapList) {
						if (lTmpLegderMap != null && !lTmpLegderMap.isEmpty()) {
							Map<String, String> lResult = new HashMap<String, String>();
							lResult = MonetagoTredsHelper.getInstance().factBatch(lTmpLegderMap);
							if (lResult != null && !lResult.isEmpty()) {
								if (StringUtils.isBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
									if (lResult.containsKey("Error")) {
										logger.info("Message:  " + lResult.get("Error"));
									}
									throw new CommonBusinessException("Error in batch factor. " + (lResult.containsKey("Error") ? lResult.get("Error") : ""));
								} else {
									InstrumentBean lTempInstBean = null;
									for (Long lKey : lTmpLegderMap.keySet()) {
										lFactorHash.put(lKey, lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
										lTempInstBean = lInstChildHash.get(lKey);
										lTempInstBean.setMonetagoFactorTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
									}
								}
							}
						}
					}
				} else {
					if (StringUtils.isNotBlank(pInstrumentBean.getMonetagoLedgerId())) {
						Map<String, String> lResult = new HashMap<String, String>();
						String lInfoMessage = "Instrument No :" + pInstrumentBean.getId() + "  Factoring Unit No :" + pInstrumentBean.getFuId();
						lResult = MonetagoTredsHelper.getInstance().factor(pInstrumentBean.getMonetagoLedgerId(), lInfoMessage, pInstrumentBean.getId());
						if (StringUtils.isBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))) {
							logger.info("Message:  " + lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
							throw new CommonBusinessException(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
						} else
							pInstrumentBean.setMonetagoFactorTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
					}
				}
			}
		}
		// the population of nondbfield not required as non are used to update
		// the results.
		if (pInstrumentBean.getStatus() != InstrumentBean.Status.Converted_To_Factoring_Unit)
			throw new CommonBusinessException("Severe Error while updating instrument status. Invalid current status of instrument " + pInstrumentBean.getId());
		pInstrumentBean.setStatus(pStatus);
		pInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		if (InstrumentBean.Status.Withdrawn.equals(pStatus)) {
			pInstrumentBean.setFuId(null);
			pInstrumentBean.setOwnerEntity(null);
			pInstrumentBean.setOwnerAuId(null);
			instrumentDAO.update(pConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATECONVFACTUNIT);
		} else
			instrumentDAO.update(pConnection, pInstrumentBean, InstrumentBean.FIELDGROUP_UPDATESTATUS);
		// updating the transaction id of the child record coming from monetago
		//no need to check performGroupMonetagoCheck here since the lFactorHash will be empty and won't go inside
		if (InstrumentBean.Status.Factored.equals(pStatus) && MonetagoTredsHelper.getInstance().performMonetagoCheck(pInstrumentBean.getPurchaser()) && CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag()) && !lFactorHash.isEmpty()) {
			updateBatchTxnId(pConnection, lFactorHash, pInstrumentBean);
		}
		insertInstrumentWorkFlow(pConnection, pInstrumentBean, pUserBean, null);
		return pInstrumentBean;
	}

	public List<Map<String, Object>> UpdateCounterCheckerStatus(ExecutionContext pExecutionContext, InstrumentBean pFilterBean, AppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		InstrumentBean lInstBean = instrumentDAO.findByPrimaryKey(lConnection, pFilterBean);
		if (VALID_STATUS.contains(lInstBean.getStatus().getCode())) {
			throw new CommonBusinessException("Invalid instrument status.");
		}

		lInstBean.populateNonDatabaseFields();
		lInstBean.setStatus(pFilterBean.getStatus());
		lInstBean.setStatusRemarks(pFilterBean.getStatusRemarks());
		lInstBean.setStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
		List<Map<String, Object>> pMessages = new ArrayList<Map<String, Object>>();
		Map<String, Object> lMsgMap = new HashMap<String, Object>();
		//TODO:PlatformCheck5
		checkPlatformStatus(lConnection, lInstBean.getPurchaser(), lInstBean.getSupplier());
		if (Status.Counter_Checker_Return.equals(pFilterBean.getStatus())) {
			InstrumentBean lTmpBean = new InstrumentBean();
			Map<String, Object> lMap = new HashMap<String, Object>();
			JsonSlurper lJsonSlurper = new JsonSlurper();
			// if(lInstBean.getCounterModifiedFields()!=null){
			// lMap = (Map<String, Object>)
			// lJsonSlurper.parseText(lInstBean.getCounterModifiedFields());
			// instrumentDAO.getBeanMeta().validateAndParse(lTmpBean, lMap,
			// null, null);
			// List<String> lCounterModFields = new ArrayList(lMap.keySet());
			// instrumentDAO.getBeanMeta().copyBean(lTmpBean,
			// lInstBean,null,lCounterModFields);
			// //
			// //Recalculate Dates
			// setUpdatedDueDates(lInstBean);
			// //
			// //Recalculate amount
			// BigDecimal lNetAmount = lInstBean.getAmount();
			// if (lInstBean.getAdjAmount() != null)
			// lNetAmount = lNetAmount.subtract(lInstBean.getAdjAmount());
			// if (lInstBean.getCashDiscountValue() != null)
			// lNetAmount =
			// lNetAmount.subtract(lInstBean.getCashDiscountValue());
			// if (lInstBean.getTdsAmount() != null)
			// lNetAmount = lNetAmount.subtract(lInstBean.getTdsAmount());
			// if (lNetAmount.compareTo(BigDecimal.ZERO) <= 0)
			// throw new CommonBusinessException("Factoring unit cost cannot be
			// zero or negative.");
			// lInstBean.setNetAmount(lNetAmount);
			// //
			// lInstBean.setCounterModifiedFields(null);
			// //
			// }
			lInstBean.setCounterCheckerAuId(null);
			if (lInstBean.getStatusRemarks() == null) {
				throw new CommonBusinessException("Remarks are mandatory, please fill the remarks and try again.");
			}
			//
			lInstBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
			instrumentDAO.update(lConnection, lInstBean);
			instrumentDAO.insertAudit(lConnection, lInstBean, GenericDAO.AuditAction.Update, pUserBean.getId());
			InstrumentWorkFlowBean lWorkFlowBean = insertInstrumentWorkFlow(lConnection, lInstBean, pUserBean, pFilterBean.getStatusRemarks());
			lMsgMap.put("act", lInstBean.getId());
			lMsgMap.put("rem", "Instrument returned to counter maker.");
			pMessages.add(lMsgMap);
			// email
			emailGeneratorBO.sendInstrumentCounterActionMail(pExecutionContext.getConnection(), lInstBean, lWorkFlowBean, pUserBean);
			//
		}
		return pMessages;
	}

	public InstrumentWorkFlowBean insertInstrumentWorkFlow(Connection pConnection, InstrumentBean pInstrumentBean, IAppUserBean pUserBean, String pRemarks) throws Exception {
		InstrumentWorkFlowBean lWorkFlowBean = new InstrumentWorkFlowBean();
		lWorkFlowBean.setInId(pInstrumentBean.getId());
		if (pUserBean != null) {
			// TODO: Q. If Aggregator then should we show his domain or the
			// Purchasers domain
			lWorkFlowBean.setEntity(pUserBean.getDomain());
			lWorkFlowBean.setAuId(pUserBean.getId());
		} else {
			lWorkFlowBean.setEntity(AppConstants.DOMAIN_PLATFORM);
			lWorkFlowBean.setAuId(new Long(0));
		}
		lWorkFlowBean.setStatus(pInstrumentBean.getStatus());
		lWorkFlowBean.setStatusRemarks(pRemarks);
		lWorkFlowBean.setStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
		instrumentWorkFlowDAO.insert(pConnection, lWorkFlowBean);
		return lWorkFlowBean;
	}

	public void setTabForMaker(InstrumentBean pInstrumentBean, Boolean pHistory) {
		if (Boolean.TRUE.equals(pHistory)) {
			pInstrumentBean.setTab(MAKERTAB_HISTORY);
			return;
		}
		switch (pInstrumentBean.getStatus()) {
		case Drafting:
		case Checker_Returned:
		case Counter_Returned:
		case Withdrawn:
			pInstrumentBean.setTab(MAKERTAB_INBOX);
			return;
		case Submitted:
			pInstrumentBean.setTab(MAKERTAB_CHECKERPENDING);
			return;
		case Checker_Approved:
		case Counter_Checker_Pending:
		case Counter_Checker_Return:
			pInstrumentBean.setTab(MAKERTAB_COUNTERPENDING);
			return;
		case Counter_Approved:
			pInstrumentBean.setTab(MAKERTAB_READYFORAUCTION);
			return;
		case Checker_Rejected:
		case Counter_Rejected:
			pInstrumentBean.setTab(MAKERTAB_REJECTED);
			return;
		case Converted_To_Factoring_Unit:
			pInstrumentBean.setTab(MAKERTAB_INAUCTION);
			return;
		case Factored:
		case Leg_1_Settled:
		case Leg_2_Settled:
			pInstrumentBean.setTab(MAKERTAB_FACTORED);
			return;
		case Leg_1_Failed:
		case Leg_2_Failed:
			pInstrumentBean.setTab(MAKERTAB_SETTLEMENT_FAILED);
			return;
		case Expired:
			pInstrumentBean.setTab(MAKERTAB_EXPIRED);
			return;
		}
	}

	public void setTabForChecker(InstrumentBean pInstrumentBean, Boolean pHistory) {
		if (Boolean.TRUE.equals(pHistory)) {
			pInstrumentBean.setTab(CHECKERTAB_HISTORY);
			return;
		}
		switch (pInstrumentBean.getStatus()) {
		case Submitted:
			pInstrumentBean.setTab(CHECKERTAB_INBOX);
			return;
		case Checker_Approved:
			pInstrumentBean.setTab(CHECKERTAB_COUNTERPENDING);
			return;
		case Counter_Approved:
			pInstrumentBean.setTab(CHECKERTAB_READYFORAUCTION);
			return;
		case Checker_Rejected:
		case Counter_Rejected:
			pInstrumentBean.setTab(CHECKERTAB_REJECTED);
			return;
		case Converted_To_Factoring_Unit:
			pInstrumentBean.setTab(CHECKERTAB_INAUCTION);
			return;
		case Factored:
		case Leg_1_Settled:
		case Leg_2_Settled:
			pInstrumentBean.setTab(CHECKERTAB_FACTORED);
			return;
		case Leg_1_Failed:
		case Leg_2_Failed:
			pInstrumentBean.setTab(CHECKERTAB_SETTLEMENT_FAILED);
			return;
		case Expired:
			pInstrumentBean.setTab(CHECKERTAB_EXPIRED);
			return;
		}
	}

	public void setTabForCounter(InstrumentBean pInstrumentBean, Boolean pHistory) {
		if (Boolean.TRUE.equals(pHistory)) {
			pInstrumentBean.setTab(COUNTERTAB_HISTORY);
			return;
		}
		switch (pInstrumentBean.getStatus()) {
		case Checker_Approved:
		case Counter_Checker_Return:
			pInstrumentBean.setTab(COUNTERTAB_INBOX);
			return;
		case Counter_Approved:
			pInstrumentBean.setTab(COUNTERTAB_READYFORAUCTION);
			return;
		case Checker_Rejected:
		case Counter_Rejected:
			pInstrumentBean.setTab(COUNTERTAB_REJECTED);
			return;
		case Converted_To_Factoring_Unit:
			pInstrumentBean.setTab(COUNTERTAB_INAUCTION);
			return;
		case Factored:
		case Leg_1_Settled:
		case Leg_2_Settled:
			pInstrumentBean.setTab(COUNTERTAB_FACTORED);
			return;
		case Leg_1_Failed:
		case Leg_2_Failed:
			pInstrumentBean.setTab(COUNTERTAB_SETTLEMENT_FAILED);
			return;
		case Expired:
			pInstrumentBean.setTab(COUNTERTAB_EXPIRED);
			return;
		case Counter_Checker_Pending:
			pInstrumentBean.setTab(COUNTERTAB_CHECKERPENDING);
			return;
		}
	}

	public void setTabForCounterChecker(InstrumentBean pInstrumentBean, Boolean pHistory) {
		if (Boolean.TRUE.equals(pHistory)) {
			pInstrumentBean.setTab(COUNTERCHECKERTAB__HISTORY);
			return;
		}
		switch (pInstrumentBean.getStatus()) {
		case Counter_Checker_Pending:
			pInstrumentBean.setTab(COUNTERCHECKERTAB_INBOX);
			return;
		case Converted_To_Factoring_Unit:
			pInstrumentBean.setTab(COUNTERCHECKERTAB_INAUCTION);
			return;
		case Counter_Checker_Return:
			pInstrumentBean.setTab(COUNTERCHECKERTAB__CHECKERRETURNED);
			return;
		}
	}

	public String getInstrumentJson(ExecutionContext pExecutionContext, Long pId, IAppUserBean pAppUserBean) throws Exception {
		InstrumentBean lFilterBean = new InstrumentBean();
		boolean lIsFinancier = false;
		if (AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()))
			lFilterBean.setId(pId);
		else {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
			AppEntityBean lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pAppUserBean.getDomain() });
			if (lAppEntityBean == null)
				throw new CommonBusinessException("Entity details not found for " + pAppUserBean.getDomain());
			lFilterBean.setId(pId);
			lIsFinancier = lAppEntityBean.isFinancier();
		}
		InstrumentBean lInstrumentBean = null;
		Connection lConnection = pExecutionContext.getConnection();
		StringBuilder lSql1 = new StringBuilder();
		lSql1.append(" SELECT * FROM Instruments WHERE INRecordVersion > 0 AND INId = ").append(pId);
		//
		lInstrumentBean = findBean(lConnection, lFilterBean);
		boolean lInstExists = false;
		if (lInstrumentBean != null)
			lInstExists = true;

		// now check whether the user is maker, checker or counter
		// check if ownership check is required.
		if (!lIsFinancier && TredsHelper.getInstance().checkOwnership((AppUserBean) pAppUserBean)) {
			lSql1.append(" AND ( INPURCHASER = ").append(DBHelper.getInstance().formatString(pAppUserBean.getDomain()));
			lSql1.append(" OR INSUPPLIER = ").append(DBHelper.getInstance().formatString(pAppUserBean.getDomain())).append(" ) ");
			// INMAKERENTITY,INCOUNTERENTITY,INOWNERENTITY
			// INMAKERAUID,INCHECKERAUID,INCOUNTERAUID,INOWNERAUID
			lSql1.append(" AND ( INMAKERAUID = ").append(pAppUserBean.getId());
			lSql1.append(" OR INCHECKERAUID = ").append(pAppUserBean.getId());
			lSql1.append(" OR INOWNERAUID = ").append(pAppUserBean.getId());
			lSql1.append(" OR EXISTS ( SELECT MCMId FROM MakerCheckerMap WHERE MCMRecordVersion > 0 AND MCMMakerId = INMakerAUId AND MCMCheckerId = ");
			lSql1.append(pAppUserBean.getId()).append(" ) ");
			lSql1.append(" OR EXISTS ( SELECT MCMId FROM MakerCheckerMap WHERE MCMRecordVersion > 0 AND MCMMakerId = INCounterAUId AND MCMCheckerId = ");
			lSql1.append(pAppUserBean.getId()).append(" ) ");
			lSql1.append(" ) ");
			//
			lInstrumentBean = instrumentDAO.findBean(pExecutionContext.getConnection(), lSql1.toString());
			if (lInstExists && lInstrumentBean == null) {
				throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
			}
		}
		//
		// update the pseudo columns ie. location Purchaser and Suppliers
		if (lInstrumentBean != null) {
			lInstrumentBean.populateNonDatabaseFields();
			// TODO: The Loggedin user domain should be of Owner or Maker or
			// Counter
			updateFieldsFromCounterUpdate(lInstrumentBean, pAppUserBean);
			//
			CompanyLocationBean lCompanyLocationBean = null;
			CompanyLocationBean lSettlementLocationBean = null;
			AppEntityBean lAppEntityBean = null;
			if (lInstrumentBean.getSupClId().longValue() > 0) {
				lCompanyLocationBean = new CompanyLocationBean();
				lCompanyLocationBean.setId(lInstrumentBean.getSupClId());
				lCompanyLocationBean = companyLocationDAO.findByPrimaryKey(lConnection, lCompanyLocationBean);
			} else {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getSupplier());
				lCompanyLocationBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lAppEntityBean.getCdId());
			}
			lInstrumentBean.setSupGstState(lCompanyLocationBean.getState());
			lInstrumentBean.setSupGstn(lCompanyLocationBean.getGstn());
			lInstrumentBean.setSupLocation(lCompanyLocationBean.getName());
			//
			if (lInstrumentBean.getPurClId().longValue() > 0) {
				lCompanyLocationBean = new CompanyLocationBean();
				lCompanyLocationBean.setId(lInstrumentBean.getPurClId());
				lCompanyLocationBean = companyLocationDAO.findByPrimaryKey(lConnection, lCompanyLocationBean);
				if (lCompanyLocationBean != null) {
					lSettlementLocationBean = new CompanyLocationBean();
					lSettlementLocationBean.setId(lInstrumentBean.getPurSettleClId());
					lSettlementLocationBean = companyLocationDAO.findByPrimaryKey(lConnection, lSettlementLocationBean);
				}
			} else {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getPurchaser());
				lCompanyLocationBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lAppEntityBean.getCdId());
			}
			lInstrumentBean.setPurGstState(lCompanyLocationBean.getState());
			lInstrumentBean.setPurGstn(lCompanyLocationBean.getGstn());
			lInstrumentBean.setPurLocation(lCompanyLocationBean.getName());
			if (lSettlementLocationBean != null) {
				lInstrumentBean.setSettlePurGstState(lSettlementLocationBean.getState());
				lInstrumentBean.setSettlePurGstn(lSettlementLocationBean.getGstn());
				lInstrumentBean.setSettlePurLocation(lSettlementLocationBean.getName());
			}
			if (TredsHelper.getInstance().supportsInstrumentKeys(lInstrumentBean.getPurchaser())) {
				InstrumentCreationKeysBean lInstrumentCreationKeysBean = new InstrumentCreationKeysBean();
				lInstrumentCreationKeysBean.setInId(lInstrumentBean.getId());
				List<InstrumentCreationKeysBean> lICKList = instrumentCreationKeysDAO.findList(lConnection, lInstrumentCreationKeysBean);
				List<String> lKeyList = lICKList.stream().map(pKeyBean -> pKeyBean.getKeyView()).collect(Collectors.toList());
				lInstrumentBean.setInstrumentCreationKeysList(lKeyList);
			}
		}
		//
		Map<String, Object> lDetailMap = new HashMap<String, Object>();
		if (lInstrumentBean != null) {
			updateNetAmount(lInstrumentBean, true, true, true);
			lDetailMap = instrumentDAO.getBeanMeta().formatAsMap(lInstrumentBean, null, null, true, true);
			if (CommonUtilities.hasValue(lInstrumentBean.getSalesCategory())) {
				lDetailMap.put("salesCategoryDesc", TredsHelper.getInstance().getSalesCategoryDescription(lInstrumentBean.getSalesCategory()));
			}
			// finding details of group Instruments
			if (lInstrumentBean.getGroupInId() != null) {
				InstrumentBean lGroupFilterBean = new InstrumentBean();
				lGroupFilterBean.setId(lInstrumentBean.getGroupInId());
				InstrumentBean lGroupInstruments = instrumentDAO.findBean(lConnection, lGroupFilterBean);
				if (lGroupInstruments != null) {
					lDetailMap.put("groupInId", lGroupInstruments.getId());
					lDetailMap.put("groupInvoiceNo", lGroupInstruments.getInstNumber());
					lDetailMap.put("groupCountRefNo", lGroupInstruments.getCounterRefNum());
					lDetailMap.put("groupDescription", lGroupInstruments.getDescription());
				}
			}
			lDetailMap.put("supGstStateDesc", TredsHelper.getInstance().getGSTStateDesc(lInstrumentBean.getSupGstState()));
			lDetailMap.put("purGstStateDesc", TredsHelper.getInstance().getGSTStateDesc(lInstrumentBean.getPurGstState()));
			lDetailMap.put("settlePurGstStateDesc", TredsHelper.getInstance().getGSTStateDesc(lInstrumentBean.getSettlePurGstState()));
			lDetailMap.put("instCrtKeys", lInstrumentBean.getInstrumentCreationKeysList());
			if (lInstrumentBean.getCfId()!=null && lInstrumentBean.getCfData()!=null) {
				JsonSlurper lJsonSlurper = new JsonSlurper();
				Map<String, Object> lCfDataMap = (Map<String, Object>)lJsonSlurper.parseText(lInstrumentBean.getCfData());
				CustomFieldBO lCustomFieldBO = new CustomFieldBO();
				CustomFieldBean lCustomFieldFilterBean = new CustomFieldBean();
				lCustomFieldFilterBean.setCode(lInstrumentBean.getPurchaser());
				lCustomFieldFilterBean.setId(lInstrumentBean.getCfId());
				CustomFieldBean lCustomFieldBean = lCustomFieldBO.findBean(lConnection, lCustomFieldFilterBean);
				ArrayList<Map<String, Object>> lConfigList =  (ArrayList<Map<String, Object>>) lCustomFieldBean.getConfig().get("inputParams");
				if (!lConfigList.isEmpty()) {
					List<Map<String, Object>> lRtnCfData = new ArrayList<>();
					Map<String, Object> lRtnMap = null; 
					for (Map<String, Object> lTmp : lConfigList) {
						if (lCfDataMap.containsKey(lTmp.get("name"))) {
							lRtnMap = new HashMap<>();
							lRtnMap.put("label", lTmp.get("label"));
							lRtnMap.put("name", lTmp.get("name"));
							lRtnMap.put("value", lCfDataMap.get(lTmp.get("name")));
							lRtnCfData.add(lRtnMap);
						}
					}
					lDetailMap.put("cfDetails", lRtnCfData);
				}
			}
			// workflow
			List<Map<String, Object>> lWorkFlowMaps = new ArrayList<Map<String, Object>>();
			String lSql = "SELECT * FROM InstrumentWorkFlow WHERE IWFInId = " + pId + " ORDER BY IWFStatusUpdateTime DESC, IWFId DESC";
			List<InstrumentWorkFlowBean> lWorkFlows = instrumentWorkFlowDAO.findListFromSql(lConnection, (String) lSql, 0);
			for (InstrumentWorkFlowBean lCompanyWorkFlowBean : lWorkFlows) {
				// the domain user should see the loginids of only self users
				// not users of other domain
				// commented
				/*
				 * if(!pAppUserBean.getDomain().equals(lCompanyWorkFlowBean.
				 * getEntity())) { lCompanyWorkFlowBean.setAuId(null);
				 * lCompanyWorkFlowBean.setLoginId(null); }
				 */
				lWorkFlowMaps.add(instrumentWorkFlowDAO.getBeanMeta().formatAsMap(lCompanyWorkFlowBean, null, null, true, true));
			}
			lDetailMap.put("workFlows", lWorkFlowMaps);
		}

		return new JsonBuilder(lDetailMap).toString();
	}

	public List<FactoredBean> findList(ExecutionContext pExecutionContext, AppUserBean pUserBean, InstFactUnitBidFilterBean pFilterBean) throws Exception {
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();

		lSql.append(" SELECT * FROM INSTREPORT_VW WHERE 1=1 ");
		if (StringUtils.isNotBlank(pFilterBean.getBidID())) {
			lSql.append(" AND BDID IN ( ").append(pFilterBean.getBidID()).append(" ) ");
		}
		if (StringUtils.isNotBlank(pFilterBean.getFactid())) {
			lSql.append(" AND FUID IN ( ").append(pFilterBean.getFactid()).append(" ) ");
		}
		if (StringUtils.isNotBlank(pFilterBean.getInstid())) {
			lSql.append(" AND INID IN ( ").append(pFilterBean.getInstid()).append(" ) ");
		}
		if (pFilterBean.getFactstatus() != null) {
			lSql.append(" AND FUSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(pFilterBean.getFactstatus())).append(" ) ");
		}
		if (pFilterBean.getFinancierEntity() != null) {
			lSql.append(" AND BDFINANCIERENTITY IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getFinancierEntity())).append(" ) ");
		}
		if (pFilterBean.getSalesCategory() != null) {
			lSql.append(" AND INSALESCATEGORY IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getSalesCategory())).append(" ) ");
		}
		if (pFilterBean.getInststatus() != null) {
			lSql.append(" AND INSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(pFilterBean.getInststatus())).append(" ) ");
		}
		if (pFilterBean.getPurchaser() != null) {
			lSql.append(" AND INPURCHASER IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getPurchaser())).append(" ) ");
		}
		if (pFilterBean.getSupplier() != null) {
			lSql.append(" AND INSUPPLIER IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getSupplier())).append(" ) ");
		}
		if (pFilterBean.getStatus() != null) {
			lSql.append(" AND BDSTATUS IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(pFilterBean.getStatus())).append(" ) ");
		}
		if (pFilterBean.getInstIsAggregatorCreated() != null) {
			lSql.append(" AND INAggregatorCreated = ").append(lDbHelper.formatString(pFilterBean.getInstIsAggregatorCreated().getCode()));
		}
		if (pFilterBean.getInstAggregatorEntity() != null) {
			lSql.append(" AND INAggregatorEntity IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(pFilterBean.getInstAggregatorEntity())).append(" ) ");
		}
		return factoredBeanDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
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

	// physical
	public List<Map<String, Object>> createGroups(Connection pConnection, List<Long> pInIds, AppUserBean pUserBean, boolean pUpdateParentStatus) throws Exception {
		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();
		if (pInIds == null || pInIds.isEmpty()) {
			throw new CommonBusinessException("UnSelect Invoices for grouping.");
		}
		logger.info("Started creating groups");
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM Instruments ");
		lSql.append(" WHERE INRECORDVERSION > 0 ");
		String[] lInIds = TredsHelper.getInstance().getCSVIdsListForInQuery(pInIds);
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("INID", lInIds));
		lSql.append(" AND INGROUPFLAG IS NULL ");
		lSql.append(" AND INGROUPINID IS NULL ");
		lSql.append(" ORDER BY INPURCHASER, INSUPPLIER, INPURSETTLECLID, INGOODSACCEPTDATE, INCREDITPERIOD, INID, INAMOUNT ");
		// INPURSETTLECLID -> SETTLEMENT LOCATION
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), -1);
		List<InstrumentBean> lGroupBeans = new ArrayList<InstrumentBean>();
		InstrumentBean lGroupBean = null;
		HashMap<String, PurchaserSupplierLinkBean> lPSLBeansHash = new HashMap<>();
		HashMap<String, Integer> lEntityHash = new HashMap<>();
		//
		if (lList != null && !lList.isEmpty()) {
			for (InstrumentBean lBean : lList) {
				//
				PurchaserSupplierLinkBean lPSLBean = null;
				Integer lCount = 0;
				if (!lPSLBeansHash.containsKey(lBean.getSupplier() + lBean.getPurchaser())) {
					lPSLBean = new PurchaserSupplierLinkBean();
					lPSLBean.setSupplier(lBean.getSupplier());
					lPSLBean.setPurchaser(lBean.getPurchaser());
					lPSLBeansHash.put(lBean.getSupplier() + lBean.getPurchaser(), purchaserSupplierLinkDAO.findBean(pConnection, lPSLBean));
				}
				lPSLBean = lPSLBeansHash.get(lBean.getSupplier() + lBean.getPurchaser());
				// TODO:PlatformCheck6
				checkPlatformStatus(lPSLBean);
				if (lBean.getPurchaser().equals(pUserBean.getDomain())) {
					if (InstrumentCreation.Supplier.equals(lPSLBean.getInstrumentCreation())) {
						if (!lEntityHash.containsKey(lBean.getSupplier())) {
							lEntityHash.put(lBean.getSupplier(), 0);
						}
						lCount = lEntityHash.get((lBean.getSupplier()));
						lEntityHash.put(lBean.getSupplier(), ++lCount);
						continue;
					}
				} else if (lBean.getSupplier().equals(pUserBean.getDomain())) {
					if (InstrumentCreation.Purchaser.equals(lPSLBean.getInstrumentCreation())) {
						if (!lEntityHash.containsKey(lBean.getPurchaser())) {
							lEntityHash.put(lBean.getPurchaser(), 0);
						}
						lCount = lEntityHash.get((lBean.getPurchaser()));
						lEntityHash.put(lBean.getPurchaser(), ++lCount);
						continue;
					}
				}
				//
				if (!CommonAppConstants.Yes.Yes.equals(lBean.getGroupFlag())) {
					if (lGroupBean == null) {
						lGroupBean = createGroup(pConnection, lBean, pUserBean);
						if (lGroupBean != null) {
							lGroupBeans.add(lGroupBean);
							clear(lGroupBean);
						}
					} else {
						if (!canBeGrouped(pConnection, lGroupBean, lBean, lRetMsg)) {
							lGroupBean = createGroup(pConnection, lBean, pUserBean);
							if (lGroupBean != null) {
								lGroupBeans.add(lGroupBean);
								clear(lGroupBean);
							}

						}
					}
					updateGroupDetails(lGroupBean, lBean);
				}
			}
			for (int lPtr = lGroupBeans.size() - 1; lPtr >= 0; lPtr--) {
				if (lGroupBeans.get(lPtr).getGroupedInstruments().size() == 1) {
					logger.info("Group : " + lGroupBeans.get(lPtr).getInstNumber() + " : Deleting group since only one instrument left.");
					lGroupBeans.remove(lPtr);
				}
			}
			if (lGroupBeans.size() > 0) {
				for (InstrumentBean lTmpGroupBean : lGroupBeans) {
					// insert parent - all fields
					// Status drafting
					if (!pUpdateParentStatus) {
						lTmpGroupBean.setStatus(Status.Drafting);
					}
					instrumentDAO.insert(pConnection, lTmpGroupBean);
					instrumentDAO.insertAudit(pConnection, lTmpGroupBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
					EndOfDayBO.appendMessage(lRetMsg, lTmpGroupBean.getGroupRefNo(), "Group created successfully.");
					//
					for (InstrumentBean lChildBean : lTmpGroupBean.getGroupedInstruments()) {
						lChildBean.setGroupInId(lTmpGroupBean.getId());
						// update child - 1 or 2 fileds
						instrumentDAO.update(pConnection, lChildBean, InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
						instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
					}
				}
			} else {
				EndOfDayBO.appendMessage(lRetMsg, "Create Group", "No groups created.");
			}
		} else {
			EndOfDayBO.appendMessage(lRetMsg, "Create Group", "No instruments found for grouping.");
		}
		if (!lEntityHash.isEmpty()) {
			StringBuilder lMsg = new StringBuilder();
			lMsg.append("Group Cannot be Created for Entity ( ");
			for (String lKey : lEntityHash.keySet()) {
				lMsg.append(lKey).append(" (").append(lEntityHash.get(lKey)).append(") ");
			}
			lMsg.append(" ). Please Check Purchaser Supplier Link.");
			EndOfDayBO.appendMessage(lRetMsg, "Create Group", lMsg.toString());
		}
		return lRetMsg;
	}

	public InstrumentBean createGroup(Connection pConnection, InstrumentBean pFirstInstrumentBean, AppUserBean pUserBean) throws Exception {
		InstrumentBean lGroupBean = new InstrumentBean();
		// copy instrument bean to self and remove the amounts
		//TODO: PRASAD - HERE IS THE CULPRIT
		instrumentDAO.getBeanMeta().copyBean(pFirstInstrumentBean, lGroupBean, null, null);
		// initalize defaults
		lGroupBean.setGroupFlag(CommonAppConstants.Yes.Yes);
		lGroupBean.setGroupedInstruments(new ArrayList<InstrumentBean>());
		lGroupBean.setId(null);
		lGroupBean.setGroupInId(null);
		//
		clear(lGroupBean);

		// create instrumentids for the group
		try {
			lGroupBean.setId(TredsHelper.getInstance().getNextId(pConnection, "yyMMdd", "Instruments.id.", 7));
			// create the group ref no.
			lGroupBean.setGroupRefNo("GR" + lGroupBean.getId());
			lGroupBean.setInstNumber(lGroupBean.getGroupRefNo());
			logger.info("Group : " + lGroupBean.getId() + " : Created.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lGroupBean = null;
		}
		return lGroupBean;
	}

	public void updateGroupDetails(InstrumentBean pGroupInstrumentBean, InstrumentBean pInstrumentBean) {
		if (CommonAppConstants.Yes.Yes.equals(pGroupInstrumentBean.getGroupFlag())) {
			// , INAMOUNT, INADJAMOUNT, INCASHDISCOUNTVALUE, INTDSAMOUNT,
			// INNETAMOUNT
			pGroupInstrumentBean.setAmount(pGroupInstrumentBean.getAmount().add(pInstrumentBean.getAmount()));
			if (pInstrumentBean.getAdjAmount() != null) {
				if (pGroupInstrumentBean.getAdjAmount() == null)
					pGroupInstrumentBean.setAdjAmount(BigDecimal.ZERO);
				pGroupInstrumentBean.setAdjAmount(pGroupInstrumentBean.getAdjAmount().add(pInstrumentBean.getAdjAmount()));
			}
			if (pInstrumentBean.getCashDiscountValue() != null) {
				if (pGroupInstrumentBean.getCashDiscountValue() == null)
					pGroupInstrumentBean.setCashDiscountValue(BigDecimal.ZERO);
				pGroupInstrumentBean.setCashDiscountValue(pGroupInstrumentBean.getCashDiscountValue().add(pInstrumentBean.getCashDiscountValue()));
			}
			if (pInstrumentBean.getTdsAmount() != null) {
				if (pGroupInstrumentBean.getTdsAmount() == null)
					pGroupInstrumentBean.setTdsAmount(BigDecimal.ZERO);
				pGroupInstrumentBean.setTdsAmount(pGroupInstrumentBean.getTdsAmount().add(pInstrumentBean.getTdsAmount()));
			}
			pGroupInstrumentBean.setNetAmount(pGroupInstrumentBean.getNetAmount().add(pInstrumentBean.getNetAmount()));
			// , INHAIRCUTPERCENT, INCASHDISCOUNTPERCENT - this will be computed
			// on basis of value
			pGroupInstrumentBean.getGroupedInstruments().add(pInstrumentBean);
			// calculate CashDiscountPercent and haircutpercent
			BigDecimal lCashDiscountPercent = BigDecimal.ZERO;
			if (pGroupInstrumentBean.getCashDiscountValue() != null) {
				lCashDiscountPercent = (pGroupInstrumentBean.getCashDiscountValue().divide(pGroupInstrumentBean.getAmount(), MathContext.DECIMAL128)).multiply(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
			}
			pGroupInstrumentBean.setCashDiscountPercent(lCashDiscountPercent);
			pGroupInstrumentBean.setHaircutPercent(BigDecimal.ZERO);
			if (pGroupInstrumentBean.getGoodsAcceptDate() == null || (OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getGoodsAcceptDate(), pGroupInstrumentBean.getGoodsAcceptDate()) < 0)) {
				pGroupInstrumentBean.setGoodsAcceptDate(pInstrumentBean.getGoodsAcceptDate());
				pGroupInstrumentBean.setInstDueDate(pInstrumentBean.getInstDueDate());
				pGroupInstrumentBean.setStatDueDate(pInstrumentBean.getStatDueDate());
				pGroupInstrumentBean.setMaturityDate(pInstrumentBean.getMaturityDate());
				pGroupInstrumentBean.setExtendedDueDate(pInstrumentBean.getExtendedDueDate());
				//TODO: PRASAD - DO we have to update the max factoring end date time here?
				pGroupInstrumentBean.setFactorMaxEndDateTime(pInstrumentBean.getFactorMaxEndDateTime());
			}
			logger.info("Group : " + pGroupInstrumentBean.getId() + " : Added inst : " + pInstrumentBean.getId());
		}
	}

	public boolean canBeGrouped(Connection pConnection, InstrumentBean pGroupInstrumentBean, InstrumentBean pInstrumentBean, List<Map<String, Object>> pMessages) {
		HashMap lInstClubSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_INSTCLUBSPLITTING);
		if (CommonAppConstants.Yes.Yes.equals(pGroupInstrumentBean.getGroupFlag())) {
			// conditions
			if (!pGroupInstrumentBean.getSupplier().equals(pInstrumentBean.getSupplier())) {
				logger.info("Group : " + pGroupInstrumentBean.getId() + " : Inst : " + pInstrumentBean.getId() + " : Supplier mismatch.");
				return false;
			}
			if (!pGroupInstrumentBean.getPurchaser().equals(pInstrumentBean.getPurchaser())) {
				logger.info("Group : " + pGroupInstrumentBean.getId() + " : Inst : " + pInstrumentBean.getId() + " : Purchaser mismatch.");
				return false;
			}
			if (!pGroupInstrumentBean.getCreditPeriod().equals(pInstrumentBean.getCreditPeriod())) {
				logger.info("Group : " + pGroupInstrumentBean.getId() + " : Inst : " + pInstrumentBean.getId() + " : Credit period mismatch.");
				return false;
			}
			long lDateDiff = OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getGoodsAcceptDate(), pGroupInstrumentBean.getGoodsAcceptDate());
			long lMaxDateDiff = Integer.valueOf(lInstClubSettings.get(AppConstants.ATTRIBUTE_DATERANGEFORINSTCLUB).toString());
			if (lDateDiff >= lMaxDateDiff) {
				logger.info("Group : " + pGroupInstrumentBean.getId() + " : Inst : " + pInstrumentBean.getId() + " : Date differnce " + lDateDiff + " greate than " + lMaxDateDiff + ".");
				return false;
			}
			Integer lMaxInstruments = Integer.valueOf(lInstClubSettings.get(AppConstants.ATTRIBUTE_MAXNOOFINSTRUMENTS).toString());
			int lGroupSize = pGroupInstrumentBean.getGroupedInstruments().size();
			if (lGroupSize >= lMaxInstruments) {
				logger.info("Group : " + pGroupInstrumentBean.getId() + " : Inst : " + pInstrumentBean.getId() + " : Group size " + lGroupSize + " : Max Group size : " + lMaxInstruments + " .");
				return false;
			}
			AppEntityBean lPurchaserAEBean = null;
			try {
				lPurchaserAEBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
			} catch (Exception lException) {
				return false;
			}
			if (TredsHelper.getInstance().isLocationwiseSettlementEnabled(pConnection, lPurchaserAEBean.getCdId(),false)) {
				if (!pGroupInstrumentBean.getPurSettleClId().equals(pInstrumentBean.getPurSettleClId())) {
					logger.info("Group : " + pGroupInstrumentBean.getId() + " : Group Settle Loc : " + pGroupInstrumentBean.getPurSettleClId() + " : Inst Settle Loc : " + pInstrumentBean.getPurSettleClId() + " .");
					return false;
				}
			} else {
				// settlement location is registered office hence same.
			}
			BigDecimal lNewAmount = pGroupInstrumentBean.getNetAmount().add(pInstrumentBean.getNetAmount());
			try {
				validatePurchaserMandate(pConnection, pGroupInstrumentBean.getPurchaser(), lNewAmount, pGroupInstrumentBean.getPurSettleClId(), pGroupInstrumentBean.getInstDueDate(), false, true);
			} catch (Exception lException) {
				logger.info("Group : " + pGroupInstrumentBean.getInstNumber() + " : Mandate error : " + lException.getMessage());
				if (pMessages != null) {
					EndOfDayBO.appendMessage(pMessages, pInstrumentBean.getId().toString(), lException.getMessage());
				}
				return false;
			}
			return true;
		}
		return false;
	}

	public void clear(InstrumentBean pGroupInstrumentBean) {
		if (CommonAppConstants.Yes.Yes.equals(pGroupInstrumentBean.getGroupFlag())) {
			// clear all amounts and dates
			// , INAMOUNT, INADJAMOUNT, INCASHDISCOUNTVALUE, INTDSAMOUNT,
			// INNETAMOUNT
			pGroupInstrumentBean.setAmount(BigDecimal.ZERO);
			pGroupInstrumentBean.setNetAmount(BigDecimal.ZERO);
			pGroupInstrumentBean.setCashDiscountValue(BigDecimal.ZERO);
			pGroupInstrumentBean.setTdsAmount(BigDecimal.ZERO);
			pGroupInstrumentBean.setAdjAmount(BigDecimal.ZERO);
			pGroupInstrumentBean.setMonetagoLedgerId(null);
			pGroupInstrumentBean.setMonetagoCancelTxnId(null);
			pGroupInstrumentBean.setMonetagoFactorTxnId(null);
			pGroupInstrumentBean.setGoodsAcceptDate(null);
		}
	}

	public void reCompute(InstrumentBean pGroupInstrumentBean) {
		if (CommonAppConstants.Yes.Yes.equals(pGroupInstrumentBean.getGroupFlag())) {
			clear(pGroupInstrumentBean);
			List<InstrumentBean> lList = new ArrayList<InstrumentBean>();
			lList.addAll(pGroupInstrumentBean.getGroupedInstruments());
			pGroupInstrumentBean.getGroupedInstruments().clear();
			logger.info("Group : " + pGroupInstrumentBean.getInstNumber() + " : Recomputing.");
			for (InstrumentBean lInstBean : lList) {
				updateGroupDetails(pGroupInstrumentBean, lInstBean);
			}
		}
	}

	// physical
	public List<Map<String, Object>> unGroup(Connection pConnection, List<Long> pGroupInIdList, AppUserBean pUserBean) throws Exception {
		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();

		for (Long lInId : pGroupInIdList) {
			InstrumentBean lGroupBean = new InstrumentBean();
			List<InstrumentBean> lChildBeans = null;
			lGroupBean.setId(lInId);
			pConnection.setAutoCommit(false);
			try {
				lGroupBean = findBean(pConnection, lGroupBean);
				if (lGroupBean != null && CommonAppConstants.Yes.Yes.equals(lGroupBean.getGroupFlag())) {
					// check status
					if (InstrumentBean.Status.Drafting.equals(lGroupBean.getStatus()) || InstrumentBean.Status.Checker_Returned.equals(lGroupBean.getStatus()) || InstrumentBean.Status.Counter_Returned.equals(lGroupBean.getStatus())) {
						//
						lChildBeans = getClubbedBeans(pConnection, lInId);
						//
						for (InstrumentBean lChildBean : lChildBeans) {
							if (lGroupBean.getId().equals(lChildBean.getGroupInId())) {
								// update child - 1 or 2 fileds
								lChildBean.setGroupInId(null);
								instrumentDAO.update(pConnection, lChildBean, InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
								instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
							} else {
								EndOfDayBO.appendMessage(lRetMsg, lChildBean.getId().toString(), "Instrument is not a child of " + lGroupBean.getId().toString() + " , cannot ungroup.");
							}
						}
						instrumentDAO.delete(pConnection, lGroupBean);
						instrumentDAO.insertAudit(pConnection, lGroupBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
						EndOfDayBO.appendMessage(lRetMsg, lGroupBean.getGroupRefNo(), "Ungrouped succesfully.");

					} else {
						EndOfDayBO.appendMessage(lRetMsg, lGroupBean.getGroupRefNo(), "Invalid status, cannot ungroup.");
					}
					pConnection.commit();
				} else {
					EndOfDayBO.appendMessage(lRetMsg, lGroupBean.getId().toString(), "Instrument is not a group, cannot ungroup.");
				}
			} catch (Exception lException) {
				logger.info("Error in unGroup : " + lException.getMessage());
				pConnection.rollback();
			} finally {

			}
		}
		return lRetMsg;
	}

	// physical
	public boolean removeFromGroup(Connection pConnection, Long pGroupInstrumentId, List<Long> pChildInstrumentIds, AppUserBean pUserBean) throws Exception {
		// update childs physically
		InstrumentBean lGroupBean = new InstrumentBean();
		lGroupBean.setId(pGroupInstrumentId);
		lGroupBean = findBean(pConnection, lGroupBean);

		if (lGroupBean.getMakerEntity().equals(pUserBean.getDomain())) {
			if (!(Status.Checker_Returned.equals(lGroupBean.getStatus()) || Status.Counter_Returned.equals(lGroupBean.getStatus()) || Status.Drafting.equals(lGroupBean.getStatus()))) {
				throw new CommonBusinessException("Invalid Instrument Status");
			}
		}
		if (lGroupBean.getCounterEntity().equals(pUserBean.getDomain())) {
			if (!(Status.Checker_Approved.equals(lGroupBean.getStatus()))) {
				throw new CommonBusinessException("Invalid Instrument Status");
			}
		}

		List<InstrumentBean> lChildBeans = getClubbedBeans(pConnection, pGroupInstrumentId);

		List<InstrumentBean> lTmpChildBeans = new ArrayList<InstrumentBean>(lChildBeans);
		InstrumentBean lChildBean = null;
		for (int lPtr = lTmpChildBeans.size() - 1; lPtr >= 0; lPtr--) {
			lChildBean = lTmpChildBeans.get(lPtr);
			if (pChildInstrumentIds.contains(lChildBean.getId())) {
				// update child - 1 or 2 fileds
				lChildBean.setGroupInId(null);
				lChildBean.setStatus(lGroupBean.getStatus());
				;
				instrumentDAO.update(pConnection, lChildBean, InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
				lTmpChildBeans.remove(lPtr);
			}
		}
		lGroupBean.setGroupedInstruments(lTmpChildBeans);
		if (lTmpChildBeans.size() <= 1) {
			if (lTmpChildBeans.size() == 1) {
				lChildBean = lTmpChildBeans.get(0);
				lChildBean.setGroupInId(null);
				lChildBean.setStatus(lGroupBean.getStatus());
				instrumentDAO.update(pConnection, lChildBean, InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
			}
			instrumentDAO.delete(pConnection, lGroupBean);
			instrumentDAO.insertAudit(pConnection, lGroupBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
		} else {
			reCompute(lGroupBean);
			instrumentDAO.update(pConnection, lGroupBean, InstrumentBean.FIELDGROUP_UPDATESAVE);
			instrumentDAO.insertAudit(pConnection, lGroupBean, AuditAction.Update, pUserBean.getId());
		}
		// then update the parent
		return false;
	}

	public Map<String, Object> findJsonForInstruments(Connection pConnection, Long pGroupInId, AppUserBean pUserBean) throws Exception {
		InstrumentBean lBean = new InstrumentBean();
		lBean.setId(pGroupInId);
		lBean = instrumentDAO.findBean(pConnection, lBean);
		Map<String, Object> lData = new HashMap<String, Object>();
		List<Object> lDataList = new ArrayList<Object>();
		if (lBean.getGroupFlag() == null) {
			Map<String, Object> lInstData = new HashMap<String, Object>();
			lInstData.put("id", lBean.getId());
			lInstData.put("instNumber", lBean.getInstNumber());
			lInstData.put("amt", lBean.getAmount());
			lInstData.put("netAmt", lBean.getNetAmount());
			lInstData.put("cashdiscountAmt", lBean.getCashDiscountValue());
			lInstData.put("adjAmt", lBean.getAdjAmount());
			lInstData.put("tdsAmt", lBean.getTdsAmount());
			lDataList.add(lInstData);
			lData.put("inst", lDataList);
			return lData;
		} else {
			return findJsonForClubbedInstruments(pConnection, pGroupInId, pUserBean);
		}
	}

	public Map<String, Object> findJsonForClubbedInstruments(Connection pConnection, Long pGroupInId, AppUserBean pUserBean) throws Exception {
		List<InstrumentBean> lList = getClubbedBeans(pConnection, pGroupInId);
		if (lList != null) {
			Map<String, Object> lData = new HashMap<String, Object>();
			List<Object> lDataList = new ArrayList<Object>();
			Map<String, Object> lDataSummary = new HashMap<String, Object>();
			Map<String, Object> lInstData = null;
			String[] lSumFields = new String[] { "amt", "netAmt", "cashdiscountAmt", "adjAmt", "tdsAmt" };
			BigDecimal lTmpVal = null;
			//
			for (String lField : lSumFields) {
				lDataSummary.put(lField, BigDecimal.ZERO);
			}
			//
			int lListCount = lList.size();
			int lCounter = 1;
			boolean lCompute = false;
			BigDecimal lTotalNetAmount = BigDecimal.ZERO;
			BigDecimal lTotalL1Interest = BigDecimal.ZERO, lCumTotalL1Interest = BigDecimal.ZERO;
			BigDecimal lTotalL2Interest = BigDecimal.ZERO, lCumTotalL2Interest = BigDecimal.ZERO;
			BigDecimal lTotalL2ExtInterest = BigDecimal.ZERO, lCumTotalL2ExtInterest = BigDecimal.ZERO;
			Boolean lIsPurchaser = false, lIsSupplier = false;
			InstrumentBean lParentBean = new InstrumentBean();
			FactoringUnitBean lFUBean = null;
			lParentBean.setId(pGroupInId);
			lParentBean = findBean(pConnection, lParentBean);
			lParentBean.populateNonDatabaseFields();

			// TODO: Do we require anything here for Aggreegator
			lIsPurchaser = lParentBean.getPurchaser().equals(pUserBean.getDomain());
			lIsSupplier = lParentBean.getSupplier().equals(pUserBean.getDomain());

			lFUBean = new FactoringUnitBean();
			lFUBean.setId(lParentBean.getFuId());
			lFUBean = factoringUnitDAO.findByPrimaryKey(pConnection, lFUBean);

			lCompute = (lIsPurchaser || lIsSupplier) && lFUBean != null;

			if (lCompute) {
				lTotalNetAmount = lParentBean.getNetAmount();

				if (lIsPurchaser) {
					if (lFUBean.getPurchaserLeg1Interest() != null) {
						lTotalL1Interest = lFUBean.getPurchaserLeg1Interest();
					}
					if (lFUBean.getPurchaserLeg2Interest() != null) {
						lTotalL2Interest = lFUBean.getPurchaserLeg2Interest();
					}
					if (lFUBean.getLeg2ExtensionInterest() != null) {
						lTotalL2ExtInterest = lFUBean.getLeg2ExtensionInterest();
					}
				} else if (lIsSupplier) {
					if (lFUBean.getSupplierLeg1Interest() != null) {
						lTotalL1Interest = lFUBean.getSupplierLeg1Interest();
					}
					// no leg2 interest for supplier
				}
				lCompute = !(lTotalL1Interest.add(lTotalL2Interest).add(lTotalL2ExtInterest).equals(BigDecimal.ZERO));
			}
			for (InstrumentBean lBean : lList) {
				lInstData = new HashMap<String, Object>();
				lInstData.put("id", lBean.getId());
				lInstData.put("instNumber", lBean.getInstNumber());
				lInstData.put("amt", lBean.getAmount());
				lInstData.put("netAmt", lBean.getNetAmount());
				lInstData.put("cashdiscountAmt", lBean.getCashDiscountValue());
				lInstData.put("adjAmt", lBean.getAdjAmount());
				lInstData.put("tdsAmt", lBean.getTdsAmount());
				//
				if (lCompute) {
					BigDecimal lInterestPart = BigDecimal.ZERO;
					if (!lTotalL1Interest.equals(BigDecimal.ZERO)) {
						if (lListCount == lCounter) {
							lInterestPart = lTotalL1Interest.subtract(lCumTotalL1Interest);
						} else {
							lInterestPart = lBean.getNetAmount().multiply(lTotalL1Interest, MathContext.DECIMAL128).divide(lTotalNetAmount, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
							lCumTotalL1Interest = lCumTotalL1Interest.add(lInterestPart);
						}
						lInstData.put("intL1Amt", lInterestPart);
					}
					if (!lTotalL2Interest.equals(BigDecimal.ZERO)) {
						if (lListCount == lCounter) {
							lInterestPart = lTotalL2Interest.subtract(lCumTotalL2Interest);
						} else {
							lInterestPart = lBean.getNetAmount().multiply(lTotalL2Interest, MathContext.DECIMAL128).divide(lTotalNetAmount, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
							lCumTotalL2Interest = lCumTotalL2Interest.add(lInterestPart);
						}
						lInstData.put("intL2Amt", lInterestPart);
					}
					if (!lTotalL2ExtInterest.equals(BigDecimal.ZERO)) {
						if (lListCount == lCounter) {
							lInterestPart = lTotalL2ExtInterest.subtract(lCumTotalL2ExtInterest);
						} else {
							lInterestPart = lBean.getNetAmount().multiply(lTotalL2ExtInterest, MathContext.DECIMAL128).divide(lTotalNetAmount, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
							lCumTotalL2ExtInterest = lCumTotalL2ExtInterest.add(lInterestPart);
						}
						lInstData.put("intL2ExtAmt", lInterestPart);
					}
					lCounter++;
				}
				//
				lDataList.add(lInstData);
				//
				for (String lField : lSumFields) {
					lTmpVal = (BigDecimal) lDataSummary.get(lField);
					if (lInstData.get(lField) != null) {
						lTmpVal = lTmpVal.add((BigDecimal) lInstData.get(lField));
					}
					lDataSummary.put(lField, lTmpVal);
				}
				if (lCompute) {
					if (!lTotalL1Interest.equals(BigDecimal.ZERO)) {
						lDataSummary.put("intL1Amt", lTotalL1Interest);
					}
					if (!lTotalL2Interest.equals(BigDecimal.ZERO)) {
						lDataSummary.put("intL2Amt", lTotalL2Interest);
					}
					if (!lTotalL2ExtInterest.equals(BigDecimal.ZERO)) {
						lDataSummary.put("intL2ExtAmt", lTotalL2ExtInterest);
					}
				}
			}
			// ci group find
			CIGroupBean lFilterBean = new CIGroupBean();
			lFilterBean.setInId(lParentBean.getId());
			CIGroupBean lCIGroupBean = ciGroupBeanDAO.findBean(pConnection, lFilterBean);
			if (lCIGroupBean != null && StringUtils.isNotEmpty(lCIGroupBean.getCiGroupDetail())) {
				lData.put("paymentAdvise", new JsonSlurper().parseText(lCIGroupBean.getCiGroupDetail()));
				lData.put("cvNumber", lCIGroupBean.getCvNumber());
			}
			//
			lData.put("parentId", pGroupInId);
			lData.put("fuId", (lParentBean != null ? lParentBean.getFuId() : null));
			lData.put("splitlist", lDataList);
			lData.put("splitsummary", lDataSummary);
			return lData;
		}
		return null;
	}

	public Map<String, Object> findJsonInstrument(Connection pConnection, InstrumentBean pInstrumentBean) throws Exception {
		if (pInstrumentBean != null) {
			InstrumentBean lBean = pInstrumentBean;
			Map<String, Object> lData = new HashMap<String, Object>();
			List<Object> lDataList = new ArrayList<Object>();
			Map<String, Object> lDataSummary = new HashMap<String, Object>();
			Map<String, Object> lInstData = null;
			String[] lSumFields = new String[] { "amt", "netAmt", "cashdiscountAmt", "adjAmt", "tdsAmt" };
			//
			for (String lField : lSumFields) {
				lDataSummary.put(lField, BigDecimal.ZERO);
			}
			//
			lInstData = new HashMap<String, Object>();
			lInstData.put("id", lBean.getId());
			lInstData.put("instNumber", lBean.getInstNumber());
			lInstData.put("amt", lBean.getAmount());
			lInstData.put("netAmt", lBean.getNetAmount());
			lInstData.put("cashdiscountAmt", lBean.getCashDiscountValue());
			lInstData.put("adjAmt", lBean.getAdjAmount());
			lInstData.put("tdsAmt", lBean.getTdsAmount());
			lDataList.add(lInstData);

			lData.put("fuId", lBean.getFuId());
			lData.put("splitlist", lDataList);
			// lData.put("splitsummary", lDataSummary);
			return lData;
		}
		return null;
	}

	public void updateBatchTxnId(Connection pConnection, HashMap<Long, String> pFactorHash, InstrumentBean pInstrumentBean) {
		List<InstrumentBean> lClubbedInstrumentsList;
		try {
			lClubbedInstrumentsList = getClubbedBeans(pConnection, pInstrumentBean.getId());
			for (InstrumentBean lBean : lClubbedInstrumentsList) {
				lBean.setMonetagoFactorTxnId(pFactorHash.get(lBean.getId()));
				try {
					instrumentDAO.update(pConnection, lBean, InstrumentBean.FIELDGROUP_UPDATESTATUS);
				} catch (Exception e) {
					e.getMessage();
				}
			}
		} catch (Exception e1) {
			e1.getMessage();
		}

	}

	public boolean monetagoFieldModificationCheck(InstrumentBean pOldInstrumentBean, InstrumentBean pNewInstrumentBean) {
		if (!pOldInstrumentBean.getSupGstn().equals(pNewInstrumentBean.getSupGstn()))
			return true;
		if (!pOldInstrumentBean.getPurGstn().equals(pNewInstrumentBean.getPurGstn()))
			return true;
		if (!pOldInstrumentBean.getInstDate().equals(pNewInstrumentBean.getInstDate()))
			return true;
		if (!pOldInstrumentBean.getAmount().equals(pNewInstrumentBean.getAmount()))
			return true;
		if (!pOldInstrumentBean.getInstNumber().equals(pNewInstrumentBean.getInstNumber()))
			return true;
		return false;
	}

	public boolean hasAccessToLevel(Long pInstLevel, Long pUserInstLevel, Long pMaxLevel) {
		if (pInstLevel != null && pInstLevel.equals(pUserInstLevel)) {
			if (pInstLevel < pMaxLevel) {
				return true;
			}
		}
		return false;
	}

	public Long getNextLevel(Connection lConnection, Long pMakerId, Long pLevel, MakerCheckerMapBean.CheckerType pCheckerType) throws Exception {
		List<Long> lLevelList = new ArrayList<>();
		Long lNextLevel = null;
		AppUserBean lAppUserBean = null;
		List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, pMakerId, pCheckerType);
		for (MakerCheckerMapBean lMCMBean : lCheckers) {
			lAppUserBean = TredsHelper.getInstance().getAppUser(lMCMBean.getCheckerId());
			if (CheckerType.Instrument.equals(pCheckerType)) {
				if (lAppUserBean.getInstLevel() != null && lAppUserBean.getInstLevel() > 0) {
					lLevelList.add(lAppUserBean.getInstLevel());
				}
			} else if (CheckerType.InstrumentCounter.equals(pCheckerType)) {
				if (lAppUserBean.getInstCntrLevel() != null && lAppUserBean.getInstCntrLevel() > 0) {
					lLevelList.add(lAppUserBean.getInstCntrLevel());
				}
			} else if (CheckerType.Bid.equals(pCheckerType)) {
				if (lAppUserBean.getBidLevel() != null && lAppUserBean.getBidLevel() > 0) {
					lLevelList.add(lAppUserBean.getBidLevel());
				}
			}
		}
		for (Long lLevel : lLevelList) {
			if (lLevel > pLevel && (lNextLevel == null || lLevel < lNextLevel)) {
				lNextLevel = lLevel;
			}
		}
		return lNextLevel;
	}

	public Long getLevel(Connection lConnection, List<MakerCheckerMapBean> pCheckers, Long pAuId, CheckerType pCheckerType, boolean pIsMin) throws Exception {
		Long lMinLevel = null;
		Long lMaxLevel = null;
		Long lLevel = null;
		AppUserBean lCheckerUserBean = null;
		List<MakerCheckerMapBean> lCheckers = pCheckers;
		if (lCheckers == null) {
			lCheckers = appUserBO.getCheckers(lConnection, pAuId, pCheckerType);
		}
		for (MakerCheckerMapBean lMCMBean : lCheckers) {
			lCheckerUserBean = TredsHelper.getInstance().getAppUser(lMCMBean.getCheckerId());
			if (CheckerType.Instrument.equals(pCheckerType)) {
				lLevel = lCheckerUserBean.getInstLevel();
			} else if (CheckerType.InstrumentCounter.equals(pCheckerType)) {
				lLevel = lCheckerUserBean.getInstCntrLevel();
			} else if (CheckerType.Bid.equals(pCheckerType)) {
				lLevel = lCheckerUserBean.getBidLevel();
			}
			if (lLevel != null && lLevel != 0) {
				if (lMinLevel == null || lMaxLevel == null) {
					lMinLevel = lLevel;
					lMaxLevel = lLevel;
				}
				if (lMinLevel > lLevel) {
					lMinLevel = lLevel;
				}
				if (lMaxLevel < lLevel) {
					lMaxLevel = lLevel;
				}
			}
		}
		return (pIsMin ? lMinLevel : lMaxLevel);
	}

	public void setPurchaserSupplierLinkSettings(Connection pConnection, InstrumentBean pInstrumentBean) throws Exception {
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = null;
		// get purchaser supplier link
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(pInstrumentBean.getPurchaser());
		lPSLFilterBean.setSupplier(pInstrumentBean.getSupplier());
		lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
		if (lPurchaserSupplierLinkBean == null)
			throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive. Supplier Ref : " + pInstrumentBean.getSupplierRef() + ".");
		lPurchaserSupplierLinkBean.populateNonDatabaseFields();
		//
		// validation to check split is not interchanged
		if (pInstrumentBean.getCreditPeriod() == null)
			pInstrumentBean.setCreditPeriod(lPurchaserSupplierLinkBean.getCreditPeriod());
		if (pInstrumentBean.getExtendedCreditPeriod() == null)
			pInstrumentBean.setExtendedCreditPeriod(lPurchaserSupplierLinkBean.getExtendedCreditPeriod());
		//
		// autoAccept and autoAcceptableBidTypes - are the only things open for
		// change.
		if (pInstrumentBean.getAutoAccept() == null)
			pInstrumentBean.setAutoAccept(lPurchaserSupplierLinkBean.getAutoAccept());
		if (pInstrumentBean.getAutoAcceptableBidTypes() == null)
			pInstrumentBean.setAutoAcceptableBidTypes(lPurchaserSupplierLinkBean.getAutoAcceptableBidTypes());

		//
		pInstrumentBean.setCostBearingType(lPurchaserSupplierLinkBean.getCostBearingType());
		pInstrumentBean.setSplittingPoint(lPurchaserSupplierLinkBean.getSplittingPoint());
		pInstrumentBean.setPreSplittingCostBearer(lPurchaserSupplierLinkBean.getPreSplittingCostBearer());
		pInstrumentBean.setPostSplittingCostBearer(lPurchaserSupplierLinkBean.getPostSplittingCostBearer());
		pInstrumentBean.setPeriod1CostBearer(lPurchaserSupplierLinkBean.getPeriod1CostBearer());
		pInstrumentBean.setPeriod1CostPercent(lPurchaserSupplierLinkBean.getPeriod1CostPercent());
		pInstrumentBean.setPeriod2CostBearer(lPurchaserSupplierLinkBean.getPeriod2CostBearer());
		pInstrumentBean.setPeriod2CostPercent(lPurchaserSupplierLinkBean.getPeriod2CostPercent());
		pInstrumentBean.setPeriod3CostBearer(lPurchaserSupplierLinkBean.getPeriod3CostBearer());
		pInstrumentBean.setPeriod3CostPercent(lPurchaserSupplierLinkBean.getPeriod3CostPercent());
		//
		pInstrumentBean.setChargeBearer(lPurchaserSupplierLinkBean.getChargeBearer());
		pInstrumentBean.setSplittingPointCharge(lPurchaserSupplierLinkBean.getSplittingPointCharge());
		pInstrumentBean.setPreSplittingCharge(lPurchaserSupplierLinkBean.getPreSplittingCharge());
		pInstrumentBean.setPostSplittingCharge(lPurchaserSupplierLinkBean.getPostSplittingCharge());
		pInstrumentBean.setPeriod1ChargeBearer(lPurchaserSupplierLinkBean.getPeriod1ChargeBearer());
		pInstrumentBean.setPeriod1ChargePercent(lPurchaserSupplierLinkBean.getPeriod1ChargePercent());
		pInstrumentBean.setPeriod2ChargeBearer(lPurchaserSupplierLinkBean.getPeriod2ChargeBearer());
		pInstrumentBean.setPeriod2ChargePercent(lPurchaserSupplierLinkBean.getPeriod2ChargePercent());
		pInstrumentBean.setPeriod3ChargeBearer(lPurchaserSupplierLinkBean.getPeriod3ChargeBearer());
		pInstrumentBean.setPeriod3ChargePercent(lPurchaserSupplierLinkBean.getPeriod3ChargePercent());
		//
		pInstrumentBean.setAutoConvert(lPurchaserSupplierLinkBean.getAutoConvert());
		pInstrumentBean.setBuyerPercent(lPurchaserSupplierLinkBean.getBuyerPercent());
		pInstrumentBean.setBuyerPercentCharge(lPurchaserSupplierLinkBean.getBuyerPercentCharge());
		pInstrumentBean.setSettleLeg3Flag(lPurchaserSupplierLinkBean.getSettleLeg3Flag());
		pInstrumentBean.setBidAcceptingEntityType(lPurchaserSupplierLinkBean.getBidAcceptingEntityType());

		pInstrumentBean.setAutoAccept(lPurchaserSupplierLinkBean.getAutoAccept());
		pInstrumentBean.setAutoAcceptableBidTypes(lPurchaserSupplierLinkBean.getAutoAcceptableBidTypes());

		//
		// haircut and cash discount percentage - assuming that is
		// nothing is sent then default is accepted
		// or else has to send zero by default
		if (pInstrumentBean.getHaircutPercent() == null) {
			pInstrumentBean.setHaircutPercent(lPurchaserSupplierLinkBean.getHaircutPercent());
		}
		if (pInstrumentBean.getCashDiscountPercent() == null) {
			pInstrumentBean.setCashDiscountPercent(lPurchaserSupplierLinkBean.getCashDiscountPercent());
		}
		if (pInstrumentBean.getAdjAmount() == null) {
			pInstrumentBean.setAdjAmount(BigDecimal.ZERO);
		}
		if (pInstrumentBean.getCurrency() == null) {
			pInstrumentBean.setCurrency(AppConstants.CURRENCY_INR);
		}

		pInstrumentBean.setPurchaserRef(lPurchaserSupplierLinkBean.getSupplierPurchaserRef());
		pInstrumentBean.setSupplierRef(lPurchaserSupplierLinkBean.getPurchaserSupplierRef());
		//
		pInstrumentBean.populateDatabaseFields();
	}

	public ArrayList<Object> getInstrumentData(Connection pConnection, String pIdsForQuery) throws Exception {
		StringBuilder lSql = new StringBuilder();
		ArrayList<Object> lRtnList = new ArrayList<>();
		lSql.append(" SELECT * FROM INSTRUMENTS WHERE INRECORDVERSION > 0 ");
		lSql.append(" AND INID IN ( ").append(pIdsForQuery).append(" ) ");
		lSql.append(" AND INGROUPFLAG IS NULL AND INGROUPINID IS NULL ");
		List<InstrumentBean> lList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), -1);
		for (InstrumentBean lBean : lList) {
			lBean.setStatus(Status.Counter_Checker_Pending);
			if (lBean.getTdsAmount() == null)
				lBean.setTdsAmount(BigDecimal.ZERO);
			if (lBean.getAdjAmount() == null)
				lBean.setAdjAmount(BigDecimal.ZERO);
			if (lBean.getCreditPeriod() == null)
				lBean.setCreditPeriod(new Long(0));
			if (lBean.getExtendedCreditPeriod() == null)
				lBean.setExtendedCreditPeriod(new Long(0));
			lBean.setStatusRemarks("");
			lRtnList.add(lBean);
		}
		return lRtnList;
	}

	public void manageInstrumentKeys(Connection pConnection, InstrumentBean pInstrumentBean) throws Exception {
		if (pInstrumentBean.getInstrumentCreationKeysList() != null && !pInstrumentBean.getInstrumentCreationKeysList().isEmpty()) {
			if (TredsHelper.getInstance().supportsInstrumentKeys(pInstrumentBean.getPurchaser())) {
				List<InstrumentCreationKeysBean> lInstrumentCreationKeysBeans = getInstrumentKeys(pConnection, pInstrumentBean);
				for (InstrumentCreationKeysBean lKeyBean : lInstrumentCreationKeysBeans) {
					if (pInstrumentBean.getInstrumentCreationKeysList().contains(lKeyBean.getKey())) {
						lKeyBean.setInId(pInstrumentBean.getId());
					} else {
						lKeyBean.setInId(null);
					}
					instrumentCreationKeysDAO.update(pConnection, lKeyBean);
				}
			} else {
				logger.info("Keys received for Instrument Id : " + pInstrumentBean.getId() + " but no support for keys.");
			}
		}
	}

	private List<InstrumentCreationKeysBean> getInstrumentKeys(Connection pConnection, InstrumentBean pInstrumentBean) throws Exception {
		List<InstrumentCreationKeysBean> lInstrumentCreationKeysBeans = null;
		if (pInstrumentBean.getInstrumentCreationKeysList() != null && !pInstrumentBean.getInstrumentCreationKeysList().isEmpty()) {
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT * FROM INSTRUMENTCREATIONKEYS WHERE ");
			lSql.append(" ICKINID = ").append(pInstrumentBean.getId());
			lSql.append(" OR ");
			lSql.append(" ICKREFNO|| '^' ||ICKPONUMBER || '^' || ICKSINUMBER  IN ( ");
			lSql.append(TredsHelper.getInstance().getCSVStringForInQuery(pInstrumentBean.getInstrumentCreationKeysList()));
			lSql.append(" ) ");
			lInstrumentCreationKeysBeans = instrumentCreationKeysDAO.findListFromSql(pConnection, lSql.toString(), -1);
		}
		return lInstrumentCreationKeysBeans;
	}

	public void validateInstrumentCreationKeys(Connection pConnection, InstrumentBean pInstrumentBean) throws Exception {
		if (TredsHelper.getInstance().supportsInstrumentKeys(pInstrumentBean.getPurchaser())) {
			if (TredsHelper.getInstance().locationSupportsInstrumentKeys(pConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getPurClId(), pInstrumentBean.getPurGstn())) {
				List<String> lInstCreationKeys = pInstrumentBean.getInstrumentCreationKeysList();
				if (lInstCreationKeys == null || lInstCreationKeys.size() == 0) {
					throw new CommonBusinessException("No Insrument creation keys provided for the instrument.");
				}
				InstrumentCreationKeysBO lInstrumentCreationKeysBO = new InstrumentCreationKeysBO();
				List<InstrumentCreationKeysBean> lICKBeans = lInstrumentCreationKeysBO.getInstCreationList(pConnection, pInstrumentBean.getPurchaser(), pInstrumentBean.getSupplier(), pInstrumentBean.getId(), pInstrumentBean.getSupClId());
				// loop the above keys and check whether the keys in the list
				// are same.
				if (lICKBeans != null && lICKBeans.size() > 0) {
					Set<String> lKeySet = new HashSet<String>();
					for (InstrumentCreationKeysBean lICKBean : lICKBeans) {
						lKeySet.add(lICKBean.getKey());
					}
					for (String lKey : lInstCreationKeys) {
						if (!lKeySet.contains(lKey)) {
							throw new CommonBusinessException("Key provided does not match with the keys in the list.");
						}
					}
				} else {
					if (lInstCreationKeys.size() > 0) {
						throw new CommonBusinessException("No valid Instrument Keys found for the instrument.");
					}
				}
			}
		}
	}

	public Long getMaxLevelChecker(Connection pConnection, Long pCounterAuId, CheckerType pCheckerType) throws Exception {
		List<MakerCheckerMapBean> lList = appUserBO.getCheckers(pConnection, pCounterAuId, pCheckerType);
		Long lMaxLevel = null;
		AppUserBean lAppUserBean = null;
		for (MakerCheckerMapBean lMCMBean : lList) {
			lAppUserBean = TredsHelper.getInstance().getAppUser(lMCMBean.getCheckerId());
			if (CheckerType.Instrument.equals(pCheckerType)) {
				if (lAppUserBean.getInstLevel() != null && lAppUserBean.getInstLevel() > 0) {
					if (lMaxLevel == null || lMaxLevel < lAppUserBean.getInstLevel()) {
						lMaxLevel = lAppUserBean.getInstLevel();
					}
				}
			} else if (CheckerType.InstrumentCounter.equals(pCheckerType)) {
				if (lAppUserBean.getInstCntrLevel() != null && lAppUserBean.getInstCntrLevel() > 0) {
					if (lMaxLevel == null || lMaxLevel < lAppUserBean.getInstCntrLevel()) {
						lMaxLevel = lAppUserBean.getInstCntrLevel();
					}
				}
			} else if (CheckerType.Bid.equals(pCheckerType)) {
				if (lAppUserBean.getBidLevel() != null && lAppUserBean.getBidLevel() > 0) {
					if (lMaxLevel == null || lMaxLevel < lAppUserBean.getBidLevel()) {
						lMaxLevel = lAppUserBean.getBidLevel();
					}
				}
			}
		}
		return lMaxLevel;
	}

	// for TML-CASHINVOICE AND BHEL
	public String createGroupInstrument(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean lUserBean, boolean pResetCostChargeBearer) throws Exception {
		List<ValidationFailBean> lTmpVal = new ArrayList<ValidationFailBean>();
		InstrumentBean lGroupInstrumentBean = new InstrumentBean();
		BeanMeta instrumentBeanMeta = instrumentDAO.getBeanMeta();
		//
		List<Map<String, Object>> lMessages = new ArrayList<Map<String, Object>>();
		String lResponse = null;
		instrumentBeanMeta.validateAndParse(lGroupInstrumentBean, pMap, FIELDGROUP_AGGPARENTINST, lTmpVal);
		if (lTmpVal != null && lTmpVal.size() > 0) {
			if (instrumentBeanMeta.isEmpty(lGroupInstrumentBean, FIELDGROUP_AGGPARENTINST, null)) {
				// throw new CommonBusinessException("Group data not
				// received.");
				appendMessage(lMessages, "Group", "Group data not received.");
			} else {
				for (ValidationFailBean lVFBean : lTmpVal) {
					appendMessage(lMessages, lVFBean.getName(), lVFBean.getMessage());
				}
			}
		}
		lGroupInstrumentBean.setResetCostChargeBearer(pResetCostChargeBearer);
		// TODO: MANIPULATE PARENT - SET MAKER, COUNTER, AUTOACCEPTBIDTYPE,
		// SETTLELEG3
		List<Map<String, Object>> lChildInstMapList = (List<Map<String, Object>>) pMap.get("instrumentList");
		List<InstrumentBean> lChildInstruments = new ArrayList<InstrumentBean>();
		if (lChildInstMapList.size() == 1 || lChildInstMapList.size() < 0) {
			// throw new CommonBusinessException("Only one child instrument send.");
		}
		if (lChildInstMapList != null && !lChildInstMapList.isEmpty()) {
			InstrumentBean lTmpChildInst = null;
			for (Map<String, Object> lChildInstMap : lChildInstMapList) {
				lTmpChildInst = new InstrumentBean();
				instrumentBeanMeta.validateAndParse(lTmpChildInst, lChildInstMap, AggregatorInstrumentBO.FIELDGROUP_AGGCHILDINST, lTmpVal);
				if (lTmpVal != null && lTmpVal.size() > 0) {
					if (instrumentBeanMeta.isEmpty(lTmpChildInst, AggregatorInstrumentBO.FIELDGROUP_AGGCHILDINST, null)) {
						appendMessage(lMessages, "child", "Grouped instrument data not received.");
					} else {
						for (ValidationFailBean lVFBean : lTmpVal) {
							appendMessage(lMessages, lVFBean.getName(), lVFBean.getMessage());
						}
					}
				}
				lChildInstruments.add(lTmpChildInst);
			}
		} else {
			appendMessage(lMessages, "Child", "Child Instruments not recieved.");
		}
		if (!lMessages.isEmpty()) {
			throw new CommonBusinessException(new JsonBuilder(lMessages).toString());
		}
		// TODO:PlatformCheck7X
		// payment advice details
		Map<String, Object> lPayAdviceDetails = new HashMap<String, Object>();
		lPayAdviceDetails.put("customerRefNo", "");
		lPayAdviceDetails.put("vendorCode", "");
		if (pMap.containsKey("customerRefNo")) {
			lPayAdviceDetails.put("customerRefNo", pMap.get("customerRefNo"));
		}
		if (pMap.containsKey("vendorCode")) {
			lPayAdviceDetails.put("vendorCode", pMap.get("vendorCode"));
		}
		if (pMap.containsKey("paymentAdviceDetails")) {
			lPayAdviceDetails.put("paymentAdviceDetails", (List<Map<String, Object>>) pMap.get("paymentAdviceDetails"));
		}
		// CHECKING THE PURCHASER LIST
		AppEntityBean lLoggedInAEBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
		if (lLoggedInAEBean.isPurchaserAggregator()) {
			PurchaserAggregatorBO lPurchaserAggregatorBO = new PurchaserAggregatorBO();
			lPurchaserAggregatorBO.validateMappedEntity(pExecutionContext.getConnection(), lUserBean.getDomain(), lGroupInstrumentBean.getPurchaser());
		} else if (lLoggedInAEBean.isPurchaser()) {
			// do nothing
		} else {
			throw new CommonBusinessException("Access Denied.");
		}
		//
		Object[] lObject = createGroupedInstrument(pExecutionContext, lGroupInstrumentBean, lChildInstruments, lUserBean, lPayAdviceDetails);
		if (lObject != null) {
			InstrumentBean lInstrumentBean = (InstrumentBean) lObject[0];
			List<InstrumentBean> lInstrumentBeanList = (List<InstrumentBean>) lObject[1];
			FactoringUnitBean lFactoringUnitBean = (FactoringUnitBean) lObject[2];
			Map<String, Object> lInstrumentMap = new HashMap<String, Object>();
			List<Map<String, Object>> lChildList = new ArrayList<Map<String, Object>>();
			lInstrumentMap = instrumentBeanMeta.formatAsMap(lInstrumentBean, FIELDGROUP_AGGPARENTINST, null, false, false);
			lInstrumentMap.put("status", lInstrumentBean.getStatus().getCode());
			lInstrumentMap.put("id", lInstrumentBean.getId());
			if (lFactoringUnitBean != null && lFactoringUnitBean.getId() != null) {
				lInstrumentMap.put("fuId", lFactoringUnitBean.getId());
				lInstrumentMap.put("fuStatus", lFactoringUnitBean.getStatus());

			}
			for (InstrumentBean lBean : lInstrumentBeanList) {
				lChildList.add(instrumentBeanMeta.formatAsMap(lBean, AggregatorInstrumentBO.FIELDGROUP_AGGCHILDINST, null, false, false));
			}
			lInstrumentMap.put("instrumentList", lChildList);
			lResponse = new JsonBuilder(lInstrumentMap).toString();
		}
		return lResponse;
	}

	public static void appendMessage(List<Map<String, Object>> pMessages, String pFieldName, String pErrorMessage) {
		Map<String, Object> lMap = new HashMap<String, Object>();
		lMap.put("field", pFieldName);
		lMap.put("error", pErrorMessage);
		if (pMessages != null)
			pMessages.add(lMap);
	}

	// for TML-CASHINVOICE AND BHEL
	public Object[] createGroupedInstrument(ExecutionContext pExecutionContext, InstrumentBean pGroupedInstrumentBean, List<InstrumentBean> pChildInstruments, AppUserBean pUserBean, Map<String, Object> pPayAdviceDetails) throws Exception {
		BeanMeta instrumentBeanMeta = instrumentDAO.getBeanMeta();

		Object[] lFactoringUnitObject = new Object[3];
		Connection lConnection = pExecutionContext.getConnection();
		//
		// validate group
		// validate childs
		//
		FactoringUnitBean lFactoringUnitBean = null;
		// TODO: DEFAULTING VALUES
		if(Yes.Yes.equals(pGroupedInstrumentBean.getInstVisibleToMaker())) {
			pGroupedInstrumentBean.setMakerAuId(new Long(0));
		}else {
		pGroupedInstrumentBean.setMakerAuId(pUserBean.getId());
		}
		pGroupedInstrumentBean.setAggregatorAuId(pUserBean.getId());
		//
		// TODO: MANIPULATE PARENT - SET MAKER, COUNTER, AUTOACCEPTBIDTYPE,
		// SETTLELEG3
		pGroupedInstrumentBean.setMakerEntity(pGroupedInstrumentBean.getPurchaser());
		pGroupedInstrumentBean.setAggregatorEntity(pUserBean.getDomain());
		pGroupedInstrumentBean.setCounterEntity(pGroupedInstrumentBean.getSupplier());
		pGroupedInstrumentBean.setAutoAcceptableBidTypes(null);
		pGroupedInstrumentBean.setSettleLeg3Flag(null);
		//
		// get min dates from children to parent
		Date lPoDate = null, lGoodsAcceptanceDate = null;
		String lPoNumber = null;
		HashSet<String> lUniqueInvoiceNumberHash = new HashSet<String>();
		BigDecimal lCDAmount = BigDecimal.ZERO;
		BigDecimal lAdjAmount = BigDecimal.ZERO;
		BigDecimal lNetAmount = BigDecimal.ZERO;
		BigDecimal lAmount = BigDecimal.ZERO;
		for (InstrumentBean lChildBean : pChildInstruments) {
			if (lUniqueInvoiceNumberHash.contains(lChildBean.getInstNumber())) {
				throw new CommonBusinessException("Invoice with same invoice number (" + lChildBean.getInstNumber() + ") already exists in the same group.");
			}
			lUniqueInvoiceNumberHash.add(lChildBean.getInstNumber());
			if (lPoDate == null && lChildBean.getPoDate() != null) {
				lPoDate = lChildBean.getPoDate();
			}else {
				if (lChildBean.getPoDate().before(lPoDate)) {
					lPoDate = lChildBean.getPoDate();
				}
			}
			if (StringUtils.isNotEmpty(lChildBean.getPoNumber())) {
				lPoNumber = lChildBean.getPoNumber();
			}
			if (lGoodsAcceptanceDate == null) {
				lGoodsAcceptanceDate = lChildBean.getGoodsAcceptDate();
			} else {
				if (lChildBean.getGoodsAcceptDate().before(lGoodsAcceptanceDate)) {
					lGoodsAcceptanceDate = lChildBean.getGoodsAcceptDate();
				}
			}
			updateNetAmount(lChildBean, false, true, true);
			lAmount = lAmount.add(lChildBean.getAmount());
			lAdjAmount = lAdjAmount.add(lChildBean.getAdjAmount());
			lCDAmount = lCDAmount.add(lChildBean.getCashDiscountValue());
			lNetAmount = lNetAmount.add(lChildBean.getNetAmount());
		}
		pGroupedInstrumentBean.setPoDate(lPoDate);
		pGroupedInstrumentBean.setGoodsAcceptDate(lGoodsAcceptanceDate);
		pGroupedInstrumentBean.setPoNumber(lPoNumber);
		if (pGroupedInstrumentBean.getInstDueDate() != null) {
			Date lDate = OtherResourceCache.getInstance().getNextClearingDate(pGroupedInstrumentBean.getInstDueDate(), 0);
			if ((!CommonAppConstants.Yes.Yes.equals(pGroupedInstrumentBean.getEnableExtension())) && pGroupedInstrumentBean.getExtendedDueDate() == null && !pGroupedInstrumentBean.getInstDueDate().equals(lDate)) {
				throw new CommonBusinessException(" Instrument due date falls on clearing holiday. ");
			}
			Long lCreditPeriod = TredsHelper.getInstance().getDiffInDays(pGroupedInstrumentBean.getInstDueDate(), lGoodsAcceptanceDate);
			pGroupedInstrumentBean.setCreditPeriod(lCreditPeriod);
		}
		if (pGroupedInstrumentBean.getExtendedDueDate() != null) {
			if(pGroupedInstrumentBean.getExtendedDueDate().after(pGroupedInstrumentBean.getInstDueDate())) {
			Date lDate = OtherResourceCache.getInstance().getNextClearingDate(pGroupedInstrumentBean.getExtendedDueDate(), 0);
			if (!pGroupedInstrumentBean.getExtendedDueDate().equals(lDate)) {
				throw new CommonBusinessException(" Extended due date falls on clearing holiday. ");
			}
				pGroupedInstrumentBean.setEnableExtension(CommonAppConstants.Yes.Yes);
			Long lTotalCreditPeriod = TredsHelper.getInstance().getDiffInDays(pGroupedInstrumentBean.getExtendedDueDate(), lGoodsAcceptanceDate);
			Long lExtenedCreditPeriod = new Long(lTotalCreditPeriod.longValue() - pGroupedInstrumentBean.getCreditPeriod());
			pGroupedInstrumentBean.setExtendedCreditPeriod(lExtenedCreditPeriod);
			}else {
				throw new CommonBusinessException(" Extended due date should fall beyond instrument due date. ");
			}
		}
		//
		setUpdatedDueDates(pGroupedInstrumentBean);
		//
		if (pGroupedInstrumentBean.getAmount() != null && pGroupedInstrumentBean.getAmount().compareTo(lAmount) != 0) {
			throw new CommonBusinessException("Group amount dose not tally with child invoice's");
		}
		if (pGroupedInstrumentBean.getAdjAmount() != null && pGroupedInstrumentBean.getAdjAmount().compareTo(lAdjAmount) != 0) {
			throw new CommonBusinessException("Group adj amount dose not tally with child invoice's");
		}
		if (pGroupedInstrumentBean.getCashDiscountValue() != null && pGroupedInstrumentBean.getCashDiscountValue().compareTo(lCDAmount) != 0) {
			throw new CommonBusinessException("Group cash discount value dose not tally with child invoice's");
		}
		pGroupedInstrumentBean.setNetAmount(lNetAmount);
		//
		// set the purchaser supplier link details from the link itself.
		setPurchaserSupplierLinkSettings(lConnection, pGroupedInstrumentBean);
		if(pGroupedInstrumentBean.resetCostChargeBearer()) {
			pGroupedInstrumentBean.setCostBearingType(CostBearingType.Buyer);
			pGroupedInstrumentBean.setChargeBearer(CostBearingType.Buyer);
			pGroupedInstrumentBean.populateDatabaseFields();
		}
		pGroupedInstrumentBean.setInstDate(null);
		//
		// copy the p/s link from the parent to the child.
		for (InstrumentBean lChildBean : pChildInstruments) {
			if (pGroupedInstrumentBean.getInstDate() == null) {
				pGroupedInstrumentBean.setInstDate(lChildBean.getInstDate());
			}
			if (lChildBean.getInstDate().before(pGroupedInstrumentBean.getInstDate())) {
				pGroupedInstrumentBean.setInstDate(lChildBean.getInstDate());
			}
			lChildBean.setInstDueDate(pGroupedInstrumentBean.getInstDueDate());
			lChildBean.setExtendedDueDate(pGroupedInstrumentBean.getExtendedDueDate());
			lChildBean.setEnableExtension(pGroupedInstrumentBean.getEnableExtension());
			instrumentBeanMeta.copyBean(pGroupedInstrumentBean, lChildBean, FIELDGROUP_AGGPURSUPLINK, null);
		}
		//
		validateGroupInst(lConnection, pGroupedInstrumentBean, true);
		validatePurchaserSupplierLink(lConnection, pGroupedInstrumentBean, pUserBean, true);
		validateAndSetSupplierLocationDetails(lConnection, pGroupedInstrumentBean, pUserBean);
		validateAndSetPurchaserLocationDetails(lConnection, pGroupedInstrumentBean, pUserBean);
		//
		validateDates(pGroupedInstrumentBean);
		//
		// TODO: DEFAULTING VALUES
		AppEntityBean lPurEntityBean = TredsHelper.getInstance().getAppEntityBean(pGroupedInstrumentBean.getPurchaser());
		InstrumentBean lInstrumentBean = null;
		for (InstrumentBean lChildBean : pChildInstruments) {
			// TODO: use field group to copy the same
			instrumentBeanMeta.copyBean(pGroupedInstrumentBean, lChildBean, FIELDGROUP_AGG_COPY_PARENT_TO_CHILD, null);
			lInstrumentBean = validateUniqueInvoiceNumber(lConnection, lChildBean, true, true);
			if (lInstrumentBean != null) {
				if (lPurEntityBean.getPreferences() != null && lPurEntityBean.getPreferences().getSkipmonetago() != null && CommonAppConstants.Yes.Yes.equals(lPurEntityBean.getPreferences().getSkipmonetago())) {
					lChildBean.setMonetagoLedgerId("");
				}
				lChildBean.setId(lInstrumentBean.getId());
				lChildBean.setRecordVersion(lInstrumentBean.getRecordVersion());
				lChildBean.setRecordCreateTime(lInstrumentBean.getRecordCreateTime());
			}
		}
		// ?????
		TredsHelper.getInstance().validateUserLimit(pUserBean, pGroupedInstrumentBean.getAmount());
		//
		try {
			lConnection.setAutoCommit(false);
			// add parent and child instruments in draft mode
			addParentChildToDb(lConnection, pGroupedInstrumentBean, pChildInstruments, pUserBean);
			//
			List<Map<String, Object>> lRetList = autoApproveAndSubmit(lConnection, pGroupedInstrumentBean, pChildInstruments, pUserBean);
			//
			for (Map<String, Object> lHashMap : lRetList) {
				if (lHashMap.containsKey("error")) {
					throw new CommonBusinessException((String) lHashMap.get("rem"));
				}
			}
			if(TredsHelper.getInstance().isCashInvoie(pUserBean)) {
				if (pGroupedInstrumentBean.getFuId() != null) {
					FactoringUnitBean lFilterBean = new FactoringUnitBean();
					lFilterBean.setId(pGroupedInstrumentBean.getFuId());
					lFactoringUnitBean = factoringUnitDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
					// now save the payment advice details
					savePaymentAdviceDetails(lConnection, pGroupedInstrumentBean, pPayAdviceDetails);
					//
					lConnection.commit();
				} else {
					// if factoring unit is not created then no need to to keep the instruments.
					throw new CommonBusinessException("Failed to create factoring unit.");
				}
			}else {
				lConnection.commit();
				if (pGroupedInstrumentBean.getFuId() != null) {
					FactoringUnitBean lFilterBean = new FactoringUnitBean();
					lFilterBean.setId(pGroupedInstrumentBean.getFuId());
					lFactoringUnitBean = factoringUnitDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
				}
			}
			
		} catch (Exception lEx) {
			lEx.printStackTrace();
			if (lConnection != null) {
				lConnection.rollback();
			}
			throw new CommonBusinessException(lEx.getMessage());
		}
		lFactoringUnitObject[0] = pGroupedInstrumentBean;
		lFactoringUnitObject[1] = pGroupedInstrumentBean.getGroupedInstruments();
		lFactoringUnitObject[2] = lFactoringUnitBean;
		return lFactoringUnitObject;
	}

	// only for TML-CASHINVOICE
	private void savePaymentAdviceDetails(Connection pConnection, InstrumentBean pInstrumentBean, Map<String, Object> pPayAdviceDetails) {
		if (pPayAdviceDetails == null || pPayAdviceDetails.size() == 0) {
			logger.info("Payment advice details missing.");
			return;
		}
		CIGroupBean lCiGroupBean = new CIGroupBean();
		List<Map<String, Object>> lPayAdviceDetailList = null;
		try {
			if (pPayAdviceDetails.containsKey("customerRefNo")) {
				lCiGroupBean.setCustomerRefNo((String) pPayAdviceDetails.get("customerRefNo"));
			}
			if (pPayAdviceDetails.containsKey("vendorCode")) {
				lCiGroupBean.setVendorCode((String) pPayAdviceDetails.get("vendorCode"));
			}
			if (pPayAdviceDetails.containsKey("paymentAdviceDetails")) {
				lPayAdviceDetailList = (List<Map<String, Object>>) pPayAdviceDetails.get("paymentAdviceDetails");
			}
			//
			lCiGroupBean.setCvNumber(pInstrumentBean.getCounterRefNum());
			lCiGroupBean.setInId(pInstrumentBean.getId());
			lCiGroupBean.setFuId(pInstrumentBean.getFuId());
			lCiGroupBean.setCiGroupDetail((lPayAdviceDetailList != null ? (new JsonBuilder(lPayAdviceDetailList).toString()) : null));
			// TODO: SAVE THE BELOW FROM THE RESPECTIVE FIELDS TO DATABASE
			// vendorName=AEName, vendorAddres=CDCorCity, buyerName=AEName,
			// buyerAddres=getAdd(CD), cinNumber=CDCinNumber
			// lPaymentAdviceBean.setSettlementDate(pSettlementDate);
			//
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());
			lCiGroupBean.setVendorName(lAppEntityBean.getName());
			CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pConnection, lAppEntityBean.getCdId(), false);
			lCiGroupBean.setVendorAddress(lCDBean.getCorCity());
			//
			// lPaymentAdviceBean.setBuyer(lFactoringUnitBean.getPurchaser());
			// Buyer Address & Buyer Name
			lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
			lCiGroupBean.setBuyerName(lAppEntityBean.getName());
			lCDBean = TredsHelper.getInstance().getCompanyDetails(pConnection, lAppEntityBean.getCdId(), false);
			lCiGroupBean.setBuyerAddress(getAddress(lCDBean));
			lCiGroupBean.setCinNumber(lCDBean.getCinNo());
			// //
			//

			try {
				ciGroupDAO.insert(pConnection, lCiGroupBean, BeanMeta.FIELDGROUP_INSERT);
			} catch (SQLException e) {
				logger.info("Error in saving ciGroup : " + e.getMessage());
			}
		} catch (Exception lEx) {
			logger.info("Error in savePaymentAdviceDetails : " + lEx.getMessage());
		}
	}

	private void addParentChildToDb(Connection pConnection, InstrumentBean pGroupedInstrumentBean, List<InstrumentBean> pChildInstruments, AppUserBean pUserBean) throws Exception {
		// validate grouped instrument
		// validate child instruments
		//
		pGroupedInstrumentBean.setType(Type.Invoice);
		// create grouped instrument
		// initialize defaults
		pGroupedInstrumentBean.populateDatabaseFields();
		pGroupedInstrumentBean.setGroupFlag(CommonAppConstants.Yes.Yes);
		pGroupedInstrumentBean.setGroupedInstruments(pChildInstruments);
		pGroupedInstrumentBean.setId(null);
		pGroupedInstrumentBean.setGroupInId(null);
		//
		// create instrument ids for the group
		try {
			pGroupedInstrumentBean.setId(TredsHelper.getInstance().getNextId(pConnection, "yyMMdd", "Instruments.id.", 7));
			// create the group ref no.
			if (StringUtils.isEmpty(pGroupedInstrumentBean.getGroupRefNo())) {
				pGroupedInstrumentBean.setGroupRefNo("GR" + pGroupedInstrumentBean.getId());
			}
			if (StringUtils.isEmpty(pGroupedInstrumentBean.getInstNumber())) {
				pGroupedInstrumentBean.setInstNumber(pGroupedInstrumentBean.getGroupRefNo());
			}
			logger.info("Group : " + pGroupedInstrumentBean.getId() + " : Created.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pGroupedInstrumentBean = null;
		}
		// parent instrument
		// Status drafting
		pGroupedInstrumentBean.setStatus(Status.Drafting);
		instrumentDAO.insert(pConnection, pGroupedInstrumentBean);
		instrumentDAO.insertAudit(pConnection, pGroupedInstrumentBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
		// EndOfDayBO.appendMessage(lRetMsg, lTmpGroupBean.getGroupRefNo(),
		// "Group created successfully.");
		//
		// create child instruments
		for (InstrumentBean lChildBean : pChildInstruments) {
			// assign group instrument no to child instruments
			lChildBean.setType(Type.Invoice);
			lChildBean.populateDatabaseFields();
			lChildBean.setGroupInId(pGroupedInstrumentBean.getId());
			lChildBean.setMakerAuId(pGroupedInstrumentBean.getMakerAuId());
			lChildBean.setAggregatorAuId(pUserBean.getId());
			lChildBean.setAggregatorEntity(pUserBean.getDomain());
			lChildBean.setCounterEntity(pGroupedInstrumentBean.getCounterEntity());
			lChildBean.setStatus(Status.Drafting);
			if (Objects.isNull(lChildBean.getId())) {
				lChildBean.setId(TredsHelper.getInstance().getNextId(pConnection, "yyMMdd", "Instruments.id.", 7));
				instrumentDAO.insert(pConnection, lChildBean);
				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
			} else {
				instrumentDAO.update(pConnection, lChildBean);
				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pUserBean.getId());
			}

			// update child - 1 or 2 fields

		}
	}

	private List<Map<String, Object>> autoApproveAndSubmit(Connection pConnection, InstrumentBean pGroupedInstrumentBean, List<InstrumentBean> pChildInstruments, AppUserBean pUserBean) throws Exception {
		// checker approve
		Connection lAdapterConnection = DBHelper.getInstance().getConnection();
		InstrumentBean lFilterBean = new InstrumentBean();
		lFilterBean.setStatus(InstrumentBean.Status.Checker_Approved);
		lFilterBean.setId(pGroupedInstrumentBean.getId());
		//
		// TODO: get the purchaser supplier link and check whether auto approval
		// is true for the instrument.
		// if not then throw errror from here, or else insturment will get
		// created, submitted but will not convert to factoring unit.
		//
		List<Map<String, Object>> lReturnList = updateMakerStatus(pConnection, Status.Drafting, pGroupedInstrumentBean, pUserBean);
		//
		lFilterBean.setStatus(null);
		InstrumentBean lTmpGroupBean = instrumentDAO.findBean(pConnection, lFilterBean);
		instrumentDAO.getBeanMeta().copyBean(lTmpGroupBean, pGroupedInstrumentBean);
		//
		if (InstrumentBean.Status.Checker_Approved.equals(lFilterBean.getStatus())) {
			try {
				InstrumentBean lInstrumentBean = findBean(pConnection, lFilterBean);
				IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstrumentBean.getPurchaser());
				if (lClientAdapter != null) {
					ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
					lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
					lClientAdapter.sendResponseToClient(lProcessInformationBean);
				}
			} catch (Exception lEx) {
				logger.info("Error : " + lEx.getMessage());
			} finally {
				if (lAdapterConnection != null && !lAdapterConnection.isClosed()) {
					lAdapterConnection.close();
				}
			}
		}
		return lReturnList;
	}

	public String getAddress(CompanyDetailBean pCDBean) {
		String lAddress = "";
		if (CommonUtilities.hasValue(pCDBean.getCorLine1()))
			lAddress += pCDBean.getCorLine1();
		if (CommonUtilities.hasValue(pCDBean.getCorLine2()))
			lAddress += ", " + pCDBean.getCorLine2();
		if (CommonUtilities.hasValue(pCDBean.getCorLine3()))
			lAddress += ", " + pCDBean.getCorLine3();
		if (CommonUtilities.hasValue(pCDBean.getCorCity()))
			lAddress += ", " + pCDBean.getCorCity();
		if (CommonUtilities.hasValue(pCDBean.getCorDistrict())) {
			if (!pCDBean.getCorDistrict().equalsIgnoreCase(pCDBean.getCorCity())) {
				lAddress += ", " + pCDBean.getCorDistrict();
			}
		}
		if (CommonUtilities.hasValue(pCDBean.getCorState())) {
			lAddress += ", " + TredsHelper.getInstance().getGSTStateDesc(pCDBean.getCorState());
		}
		if (CommonUtilities.hasValue(pCDBean.getCorZipCode()))
			lAddress += ", " + pCDBean.getCorZipCode();
		return lAddress;
	}
	private void checkPlatformStatus(PurchaserSupplierLinkBean pPurchaserSupplierLinkBean) throws CommonBusinessException {
		if (pPurchaserSupplierLinkBean != null && PurchaserSupplierLinkBean.PlatformStatus.Suspended.equals(pPurchaserSupplierLinkBean.getPlatformStatus())) {
			throw new CommonBusinessException("Relationship is suspended by platform.");
		}
	}
	
	private void checkPlatformStatus(Connection pConnection, String pPurchaser, String pSupplier) throws CommonBusinessException {
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(pPurchaser);
		lPSLFilterBean.setSupplier(pSupplier);
		//lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean=null;
		try {
			lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
		} catch (Exception e) {
			logger.error("Error in inst.checkPlatformStatus : "+e.getMessage());
		}
		checkPlatformStatus(lPurchaserSupplierLinkBean);
	}
}
