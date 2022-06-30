package com.xlx.treds.bill.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObliFUInstDetailBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.bill.bean.BillBean;
import com.xlx.treds.bill.bean.BillBean.BillingType;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean.BillType;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyLocationBean.LocationType;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.GstSummaryBean;
import com.xlx.treds.master.bean.GSTRateBean;
import com.xlx.treds.master.bo.EndOfDayBO;

public class BillBO {
    public static final Logger logger = LoggerFactory.getLogger(BillBO.class);

	private GenericDAO<BillBean> billDAO;
	private CompositeGenericDAO<ObliFUInstDetailBean> obliFUDetailDao;
	private GenericDAO<CompanyDetailBean> companyDetailDao;
	private GenericDAO<CompanyLocationBean> companyLocationDAO;
	private GenericDAO<CompanyContactBean> companyContactDao;
	private GenericDAO<ObligationBean> obligationDao;
	private GenericDAO<FactoringUnitBean> factoringUnitDao;
	//
    public static final String REGISTRY_TREDS_BILLINGDETAILS = "server.settings.tredsbillingdetails";
    public static final String ATTRIBUTE_TREDS_NAME= "name";
    public static final String ATTRIBUTE_TREDS_ADDRESSLINE1= "addressline1";
    public static final String ATTRIBUTE_TREDS_ADDRESSLINE2= "addressline2";
    public static final String ATTRIBUTE_TREDS_ADDRESSLINE3= "addressline3";
    public static final String ATTRIBUTE_TREDS_COUNTRY= "country";
    public static final String ATTRIBUTE_TREDS_STATECODE= "statecode";
    public static final String ATTRIBUTE_TREDS_DISTRICT= "district";
    public static final String ATTRIBUTE_TREDS_CITY= "city";
    public static final String ATTRIBUTE_TREDS_ZIPCODE= "zipcode";
    public static final String ATTRIBUTE_TREDS_TELEPHONE= "telephone";
    public static final String ATTRIBUTE_TREDS_EMAIL= "email";
    public static final String ATTRIBUTE_TREDS_PAN= "pan";
    public static final String ATTRIBUTE_TREDS_GSTN= "gstn";
    public static final String ATTRIBUTE_TREDS_CIN= "cin";
    public static final String ATTRIBUTE_TREDS_NATUREOFTRANS= "natureoftrans";
    public static final String ATTRIBUTE_TREDS_SACCODE= "saccode";
    public static final String ATTRIBUTE_TREDS_SACDESC= "sacdesc";
    //
	private final String INVOICE_SEPERATOR = "/";
	private final String FIN_YEAR_SEPERATOR = "-";

	private final String months[] = {"", "Jan", "Feb", "Mar", "Apr","May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	public BillBO() {
		super();
		billDAO = new GenericDAO<BillBean>(BillBean.class);
		obliFUDetailDao = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
		obligationDao = new GenericDAO<ObligationBean>(ObligationBean.class);
		companyDetailDao = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
		companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
		companyContactDao = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
		factoringUnitDao = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
	}

	public BillBean findBean(ExecutionContext pExecutionContext, BillBean pFilterBean) throws Exception {
		BillBean lBillBean = billDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
		if (lBillBean == null)
			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);

		return lBillBean;
	}

	public List<BillBean> findList(ExecutionContext pExecutionContext, BillBean pFilterBean, List<String> pColumnList,
			IAppUserBean pUserBean) throws Exception {
		return billDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
	}

	public void save(ExecutionContext pExecutionContext, BillBean pBillBean, IAppUserBean pUserBean, boolean pNew)
			throws Exception {
		pExecutionContext.setAutoCommit(false);
		Connection lConnection = pExecutionContext.getConnection();
		BillBean lOldBillBean = null;
		if (pNew) {

			pBillBean.setRecordCreator(pUserBean.getId());
			billDAO.insert(lConnection, pBillBean);
		}
		else {
			lOldBillBean = findBean(pExecutionContext, pBillBean);

			if (billDAO.update(lConnection, pBillBean) == 0)
				throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		}

		pExecutionContext.commitAndDispose();
	}

	public void delete(ExecutionContext pExecutionContext, BillBean pFilterBean, IAppUserBean pUserBean)
			throws Exception {
		pExecutionContext.setAutoCommit(false);
		Connection lConnection = pExecutionContext.getConnection();

		BillBean lBillBean = findBean(pExecutionContext, pFilterBean);
		billDAO.delete(lConnection, lBillBean);

		pExecutionContext.commitAndDispose();
	}

	public List<Map<String, Object>> generateBill(ExecutionContext pExecutionContext, Date pBillDate, AppEntityPreferenceBean.BillType pBillType,
			IAppUserBean pUserBean) throws Exception {
		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();
		pExecutionContext.setAutoCommit(false);
		Connection lConnection = pExecutionContext.getConnection();
		StringBuilder lSql = new StringBuilder();
		Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
		Date lFirstDay = null, lLastDay = null;
		Date lBDFirstDay = null, lBDPrevMthLastDay = null;
		//
		logger.info("******************************* "+ pBillType.toString() + " Billing started for "+ pBillDate + " ***************************************");
		// Inputmonth last day should be less than current business date
		if (pBillDate == null)
			throw new CommonBusinessException("Billing Date is mandatory.");
		if (lBusinessDate.before(pBillDate)) {
			throw new CommonBusinessException("Billing Date should be before the BusinessDate.");
		}
		if(AppEntityPreferenceBean.BillType.Monthly.equals(pBillType)) {
			lFirstDay = CommonUtilities.getMonthFirstDate(pBillDate);
			lLastDay = CommonUtilities.getMonthLastDate(pBillDate);
		}else if (AppEntityPreferenceBean.BillType.Daily.equals(pBillType)) {
			lFirstDay = CommonUtilities.getMonthFirstDate(pBillDate);
			lLastDay = pBillDate;
		}
		if(AppEntityPreferenceBean.BillType.Monthly.equals(pBillType)) {
			//check if last day of billing month is equal to the last day of previous month of business date
			lBDFirstDay = CommonUtilities.getMonthFirstDate(lBusinessDate);
			lBDPrevMthLastDay = CommonUtilities.addRemoveDays(lBDFirstDay, -1);
			logger.info("FirstDay : " + lFirstDay + " LastDay : "+lLastDay + " BDFirstDay : "+lBDFirstDay + " BdPrevMthLastDay : "+ lBDPrevMthLastDay);
			if(!lBDPrevMthLastDay.equals(lLastDay)){
				throw new CommonBusinessException("Billing Date should be previous month of the BusinessDate.");
			}
		}
		//
		// Select * from factoringuntis,obligations
		// where obdate between <first day input month> and <last day of input
		// month>
		// and obtype=tredscharge and obstatus !=failed order by chargebearer
		lSql.append("SELECT * FROM FactoringUnits, Obligations ");
		lSql.append(" WHERE FURECORDVERSION > 0 AND OBRECORDVERSION > 0 AND OBFUID = FUID ");
		//
		lSql.append(" AND OBDATE >= ").append(DBHelper.getInstance().formatDate(lFirstDay));
		lSql.append(" AND OBDATE <= ").append(DBHelper.getInstance().formatDate(lLastDay));
		lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(AppConstants.DOMAIN_PLATFORM));
		lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Credit.getCode()));
		lSql.append(" AND OBTYPE IN ( ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" , ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" ) ");
		lSql.append(" AND OBSTATUS IN ( ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Created.getCode()));
		lSql.append(CommonConstants.COMMA).append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(CommonConstants.COMMA).append(DBHelper.getInstance().formatString(ObligationBean.Status.Sent.getCode()));
		lSql.append(CommonConstants.COMMA).append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode())).append(" ) ");
		lSql.append(" AND OBBillId IS NULL ");
		//
		// If (record with status other than successs found then terminate
		// billing process)
		// If (record.billid is not null) then error
		List<ObliFUInstDetailBean> lObliFUDetails = obliFUDetailDao.findListFromSql(lConnection, lSql.toString(), 0);
		if (lObliFUDetails != null && lObliFUDetails.size() > 0) {
			//EndOfDayBO.appendMessage(lRetMsg, "Obligations Count ", "Total " + lObliFUDetails.size() + " obligations found.");
			logger.info("Obligations Count ", "Total " + lObliFUDetails.size() + " obligations found.");			
			//
			FactoringUnitBean lFUBean = null;
			ObligationBean lObliBean = null;
			//
			Map<String, BillBean> lCBwiseBills = new HashMap<String, BillBean>();
			Map<String, List<ObligationBean>> lCBObligations = new HashMap<String, List<ObligationBean>>();
			List<BillBean> lBills = new ArrayList<BillBean>();
			BillBean lBillBean = null;
			List<ObligationBean> lObliList = null;
			List<String> lMemberErrors = new ArrayList<String>();
			//String lKeySettleLocwiseCBCode = null; //
			//
			Map<Long,FactoringUnitBean> lFuHash = new HashMap<Long,FactoringUnitBean>();
			Set<String> lSkippedEntities = new HashSet<String>();
			for (ObliFUInstDetailBean lObliFUDetail : lObliFUDetails) {
				lObliBean = lObliFUDetail.getObligationBean();
				lFUBean = lObliFUDetail.getFactoringUnitBean();
				if(!lFuHash.containsKey(lFUBean.getId())){
					lFuHash.put(lFUBean.getId(), lFUBean);
				}
				//TODO: THE KEYS FOR BILLING IS DETERMINED ON CHARGEBEARER (L1) , BUT IN L2 CHARGEBEARER IS PURCHASER
				String[] lKeys = lFUBean.getKeysForBilling(lObliBean.getType());
				lKeys  = getKeysToBill(lKeys, lSkippedEntities, pBillType);
				if(lKeys.length == 0) {
					continue;
				}
				for(String lKey : lKeys){
				//
					String lEntityCode = lFUBean.getEntityCode(lKey);
				logger.info("********** FUId : "+lFUBean.getId() + " Obli : "+lObliBean.getId());
					if(lMemberErrors.contains(lKey)){
						logger.info("Skip Entity : Entity : " + lKey + " Obli : " + lObliBean.getId() + " Status : " + lObliBean.getStatus().toString() + " FU : " + lFUBean.getId());
					continue;
				}
				//
				if (!ObligationBean.Status.Success.equals(lObliBean.getStatus())) {
						EndOfDayBO.appendMessage(lRetMsg, "Skip Non-Success", "Status Error : Entity : " + lKey + " Obli : " + lObliBean.getId() + " Status : " + lObliBean.getStatus().toString() + " FU : " + lFUBean.getId());
						logger.info("Status Error : Entity : " + lKey + " Obli : " + lObliBean.getId() + " Status : " + lObliBean.getStatus().toString() + " FU : " + lFUBean.getId());
						if(!lMemberErrors.contains(lKey)){
							lMemberErrors.add(lKey);
							if(lCBwiseBills.containsKey(lKey)){
								Object lTmp = lCBwiseBills.get(lKey);
							if(lTmp!=null) {
								lBills.remove(lTmp);
									logger.info("Removed Bill for Entity : " +  lKey );
							}
						}
					}
					continue;
				}
				//
				if(!lCBwiseBills.containsKey(lKey)){
					lBillBean = new BillBean();
					//temporarily setting the key to the billnumber - so as to get the obligation list hash for this bill 
						lBillBean.setBillNumber(lKey);
					//
					lBillBean.setBillYearMonth(pBillDate); //the year month selected for generating the bill
						lCBwiseBills.put(lKey, lBillBean);
					lBills.add(lBillBean);
					//
					lObliList = new ArrayList<ObligationBean>();
					lObliList.add(lObliBean);
						lCBObligations.put(lKey, lObliList);
					lBillBean.setRecordVersion(new Long(1));
						logger.info("FIRST : Entity : " + lKey + " Obli : " + lObliBean.getId() + " Status : " + lObliBean.getStatus().toString() + " FU : " + lFUBean.getId() + " FUAmt : " + lFUBean.getFactoredAmount() );
				}else{
						lBillBean = lCBwiseBills.get(lKey);
					//
						lObliList = lCBObligations.get(lKey);
					lObliList.add(lObliBean);
					lBillBean.setRecordVersion(new Long(lBillBean.getRecordVersion()+1));
						logger.info("ADDED : Entity : " + lKey + " Obli : " + lObliBean.getId() + " Status : " + lObliBean.getStatus().toString() + " FU : " + lFUBean.getId() + " FUAmt : " + lFUBean.getFactoredAmount());
				}
				//
					fillBillBean(lConnection, lBillBean, lEntityCode, lFUBean, pBillDate, lBDPrevMthLastDay,lObliBean);				
				}
			}
			//
			if(lBills.size() > 0){
				//get the next invoice number from the registry
				int lFinYearStart = TredsHelper.getInstance().getFinYearStart(pBillDate);
				int lMonth = CommonUtilities.getMonth(pBillDate);
				String lKey = null;
				//
				for(int lPtr=0; lPtr < lBills.size(); lPtr++){
					lBillBean = lBills.get(lPtr);
					//We has stored the Key=Member^LocId in the billnumber to retrive the relevant obligation has below
					lKey= lBillBean.getBillNumber();
					//
					//create the invoice number
					Long lLastBillNo = DBHelper.getInstance().getUniqueNumber(lConnection, "BillNoFY"+lFinYearStart);
					lBillBean.setBillNumber(getInvoiceNo(lBillBean.getEntity(), lLastBillNo.longValue(), lFinYearStart, lMonth, pBillType, pBillDate));
					//save the bill to db
					lBillBean.setRecordCreator(pUserBean.getId());
					
					EndOfDayBO.appendMessage(lRetMsg,"Bill " , lBillBean.getEntity() + " has " + lBillBean.getRecordVersion()+ " obligations.");
					logger.info(lBillBean.getEntity() + " has " + lBillBean.getRecordVersion()+ " obligations.");
					if (lBillBean.getBillDate()==null) {
						lBillBean.setBillDate(pBillDate);
					}
					billDAO.insert(lConnection, lBillBean);
					//update the obligations as per the bill no
					lObliList = lCBObligations.get(lKey);
					for(int lObliPtr = 0; lObliPtr < lObliList.size(); lObliPtr++){
						lObliBean = lObliList.get(lObliPtr);
						//TODO: MOVE BILL ID TO FU
//						FactoringUnitBean lFactoringUnitBean = lFuHash.get(lObliBean.getFuId());
//						if (lFactoringUnitBean!=null) {
//							if (lFactoringUnitBean.getFinancier().equals(lBillBean.getEntity())) {
//								//set finbillid
//							}else {
//							   //set cbbillid
//							}
//						}
						lObliBean.setBillId(lBillBean.getId());
						if(lFuHash.containsKey(lObliBean.getFuId())){
							FactoringUnitBean lFactoringUnitBean = lFuHash.get(lObliBean.getFuId());
							if(Type.Leg_1.equals(lObliBean.getType())) {
								if(lBillBean.getEntity().equals(lFactoringUnitBean.getChargeBearerEntityCode())){
									lFactoringUnitBean.setCostBearerBillId(lBillBean.getId());
								}
								if(lBillBean.getEntity().equals(lFactoringUnitBean.getFinancier())){
									lFactoringUnitBean.setFinancierBillId(lBillBean.getId());
								}
							}else if (Type.Leg_2.equals(lObliBean.getType())){
								if (lFactoringUnitBean.getExtBillId1()==null) {
									lFactoringUnitBean.setExtBillId1(lBillBean.getId());
								}else {
									lFactoringUnitBean.setExtBillId2(lBillBean.getId());
								}
							}
						}
						obligationDao.update(lConnection, lObliBean, ObligationBean.FIELDGROUP_UPDATEBILLID);
					}
				}
				for (Long lId : lFuHash.keySet()) {
					FactoringUnitBean lFactoringUnitBean = lFuHash.get(lId);
					factoringUnitDao.update(lConnection, lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATEBILLID);
					factoringUnitDao.insertAudit(lConnection, lFactoringUnitBean, AuditAction.Update, pUserBean.getId());
				}
			}
			if(lMemberErrors.size() > 0){
				String lEntityNames = "";
				for(String lName : lMemberErrors){
					if(lEntityNames.length() > 0) lEntityNames += CommonConstants.COMMA;
					lEntityNames += lName;
				}
				EndOfDayBO.appendMessage(lRetMsg,"Bill generation for following Entities skipped. ", lEntityNames );				
			}
		}else{
			EndOfDayBO.appendMessage(lRetMsg,"Obligation Search", "No obligations available for billing, for the month " + pBillDate);				
		}
		//
		pExecutionContext.commitAndDispose();
		return lRetMsg;
	}
	private String[] getKeysToBill(String[] pKeys, Set<String> pSkippedEntities, AppEntityPreferenceBean.BillType pBillType) throws Exception {
		String[] lRetKeys = new String[0];
		if(pKeys!=null && pKeys.length > 0) {
			List<String> lTmpRetKeys = new ArrayList<String>();
			String lTmpEntity = null;
			AppEntityBean lTmpAEBean = null;
			BillType lTmpEntityBillType = null;
			for(int lPtr=pKeys.length-1; lPtr>=0; lPtr--) {
				String[] lTmpList = CommonUtilities.splitString(pKeys[lPtr],CommonConstants.KEY_SEPARATOR );
	    		if(lTmpList.length > 0){
	    			lTmpEntity = lTmpList[0];
	    			//if same is repeated for next fu's obligation
	    			if(pSkippedEntities.contains(lTmpEntity)) {
	    				logger.info("Billing "+pBillType.toString()+ ": Skipped key : "+ pKeys[lPtr]);
	    				continue;
	    			}
	    			lTmpAEBean = TredsHelper.getInstance().getAppEntityBean(lTmpEntity);
	    			if(lTmpAEBean!=null) {
	    				if (lTmpAEBean.getPreferences()!=null) {
	    					lTmpEntityBillType = lTmpAEBean.getPreferences().getBillType();
	    				}
	    				if(lTmpEntityBillType==null) {
	    					lTmpEntityBillType = BillType.Monthly;
	    				}
	    			}else {
	    				logger.debug("Entity not found : " + lTmpEntity);
	    				throw new CommonBusinessException("Entity not found while billing : "+lTmpEntity);
	    			}
	    			//remove all the entities which have different billing
	    			if(!pBillType.equals(lTmpEntityBillType)) {
	    				pSkippedEntities.add(lTmpEntity);
	    				logger.info("Billing "+pBillType.toString()+ ": Skipped key : "+ pKeys[lPtr]);
	    				continue;
	    			}
	    			//if billing to be done is same as entities billing type then 
	    			lTmpRetKeys.add(pKeys[lPtr]);
	    		}
			}
			if(lTmpRetKeys.size() > 0) {
				lRetKeys = new String[lTmpRetKeys.size()];
				for(int lPtr=0; lPtr < lTmpRetKeys.size(); lPtr++) {
					lRetKeys[lPtr] = lTmpRetKeys.get(lPtr);
				}
			}
		}
		return lRetKeys;
	}
	
	public void generateRegBill(Connection pConnection, BillBean pBillBean) throws Exception {
		String lTmpBillEntity = pBillBean.getEntity();
		Date lBillDate = TredsHelper.getInstance().getBusinessDate();
		//unset entity
		pBillBean.setEntity(null);
		//
		fillBillBean(pConnection, pBillBean, lTmpBillEntity, null, lBillDate, null,null);
		//
		//reset entity
		pBillBean.setEntity(lTmpBillEntity);
		//
		//
		//create the invoice number
		/*
		lBillBean.setBillNumber(getInvoiceNo(lBillBean.getEntity(), lLastBillNo.longValue(), lFinYearStart, lMonth, pBillType, pBillDate));
		//save the bill to db
		*/
		pBillBean.setBillDate(lBillDate);
		pBillBean.setBillYearMonth(lBillDate); //the year month selected for generating the bill
		//
		int lFinYearStart = TredsHelper.getInstance().getFinYearStart(pBillBean.getBillDate());
		Long lLastBillNo = DBHelper.getInstance().getUniqueNumber(pConnection, "BillNoFY"+lFinYearStart);
		int lMonth = CommonUtilities.getMonth(pBillBean.getBillDate());
		pBillBean.setBillNumber(getInvoiceNo(pBillBean.getEntity(), lLastBillNo.longValue(), lFinYearStart, lMonth, BillType.Daily, pBillBean.getBillDate()));
		billDAO.insert(pConnection, pBillBean);
	}
	
	private void fillBillBean(Connection pConnection, BillBean pBillBean, String  pEntityCode, FactoringUnitBean pFUBean, Date pBillDate, Date pPrevMthLastDay, ObligationBean lObliBean) throws Exception{
		//first time fill, fill details 
		if(pBillBean.getEntity()==null){
			if(pBillBean.getBillingType()==null) {
				pBillBean.setBillingType(BillingType.TransactionCharge);
			}
			//fill the entity details from the CompanyDetails 
			CompanyDetailBean lCDBean = new CompanyDetailBean();
			lCDBean.setCode(pEntityCode);
			lCDBean = companyDetailDao.findBean(pConnection, lCDBean);
			if(lCDBean!=null){
				pBillBean.setEntity(lCDBean.getCode());
				pBillBean.setEntName(lCDBean.getCompanyName()+"-"+lCDBean.getCode());
				pBillBean.setBillDate(pPrevMthLastDay);
				pBillBean.setEntPan(lCDBean.getPan());
				//
				CompanyLocationBean lCLBean = new CompanyLocationBean();
				lCLBean.setCdId(lCDBean.getId());
				if(BillingType.TransactionCharge.equals(pBillBean.getBillingType())) {
					if (Type.Leg_2.equals(lObliBean.getType())) {
						if(pFUBean.getPurchaserBillLoc()!=null) {
							lCLBean.setId(pFUBean.getPurchaserBillLoc());
						}else if (pFUBean.getPurchaserSettleLoc()!=null) {
							lCLBean.setId(pFUBean.getPurchaserSettleLoc());
						}else {
							lCLBean.setLocationType(LocationType.RegOffice);
						}
					}else {
						if (pFUBean.getFinancier().equals(pEntityCode)){
							lCLBean.setId(pFUBean.getFinancierBillLoc()!=null?pFUBean.getFinancierBillLoc():pFUBean.getFinancierSettleLoc());
						}else{
							if(pFUBean.getChargeBearerBillLocationId()!=null) {
								lCLBean.setId(pFUBean.getChargeBearerBillLocationId());
							}else {
								lCLBean.setLocationType(LocationType.RegOffice);
							}
						}
					}
				}else if(BillingType.RegistrationFee.equals(pBillBean.getBillingType()) ||
						BillingType.AnnualFee.equals(pBillBean.getBillingType())) {
					if(pBillBean.getRegBillEntityLocId()!=null) {
						lCLBean.setId(pBillBean.getRegBillEntityLocId());
					}else {
						lCLBean.setLocationType(LocationType.RegOffice);
					}
				}
				lCLBean = companyLocationDAO.findBean(pConnection, lCLBean);
				if(lCLBean!=null){
					pBillBean.setEntGstn(lCLBean.getGstn());
					pBillBean.setEntLine1(lCLBean.getLine1());
					pBillBean.setEntLine2(lCLBean.getLine2());
					pBillBean.setEntLine3(lCLBean.getLine3());
					pBillBean.setEntCountry(lCLBean.getCountry());
					pBillBean.setEntState(lCLBean.getState());
					pBillBean.setEntDistrict(lCLBean.getDistrict());
					pBillBean.setEntCity(lCLBean.getCity());
					pBillBean.setEntZipCode(lCLBean.getZipCode());
				}
			}else{
				throw new CommonBusinessException("Company Details not found for " + pEntityCode);
			}
			//Company Admin details
			CompanyContactBean lCCBean = new CompanyContactBean();
			lCCBean.setCdId(lCDBean.getId());
			lCCBean.setAdmin(CommonAppConstants.Yes.Yes);
			lCCBean = companyContactDao.findBean(pConnection, lCCBean);
			if(lCCBean!=null){
				pBillBean.setEntSalutation(lCCBean.getSalutation());
				pBillBean.setEntFirstName(lCCBean.getFirstName());
				pBillBean.setEntMiddleName(lCCBean.getMiddleName());
				pBillBean.setEntLastName(lCCBean.getLastName());
				pBillBean.setEntEmail(lCCBean.getEmail());
				pBillBean.setEntTelephone(lCCBean.getTelephone());
				pBillBean.setEntMobile(lCCBean.getMobile());
				pBillBean.setEntFax(lCCBean.getFax());
			}else{
				throw new CommonBusinessException("Company Admin contact details not found for " + pEntityCode);
			}
		}
		//treds information
		fillTredsDetails(pBillBean);
		if(pBillBean.getFuAmount()==null){
			pBillBean.setFuAmount(BigDecimal.ZERO);
			if(BillingType.TransactionCharge.equals(pBillBean.getBillingType())) {
				pBillBean.setChargeAmount(BigDecimal.ZERO);
			}
			//
			pBillBean.setCgst(BigDecimal.ZERO);
			pBillBean.setCgstSurcharge(BigDecimal.ZERO);
			pBillBean.setSgst(BigDecimal.ZERO);
			pBillBean.setSgstSurcharge(BigDecimal.ZERO);
			pBillBean.setIgst(BigDecimal.ZERO);
			pBillBean.setIgstSurcharge(BigDecimal.ZERO);
			pBillBean.setCgstValue(BigDecimal.ZERO);
			pBillBean.setSgstValue(BigDecimal.ZERO);
			pBillBean.setIgstValue(BigDecimal.ZERO);
		}
		//fill trasactions
		GstSummaryBean lTotalGstSummaryBean =  null;
		if(BillingType.TransactionCharge.equals(pBillBean.getBillingType())) {
			pBillBean.setFuAmount(pBillBean.getFuAmount().add(pFUBean.getFactoredAmount()));
			if (Type.Leg_2.equals(lObliBean.getType())) {
				lTotalGstSummaryBean = pFUBean.getGstSummary(pEntityCode, ChargeType.Extension,null);
			}else {
				lTotalGstSummaryBean = pFUBean.getTotalGstSummary(pEntityCode);
			}
		}else if(BillingType.RegistrationFee.equals(pBillBean.getBillingType()) ||
				BillingType.AnnualFee.equals(pBillBean.getBillingType())) {
			BigDecimal lChargeAmount = pBillBean.getChargeAmount();
			//since for transaction we are cumulating the charge, we have to reset charge to zero since we are setting again from the gstnsummary bean
			pBillBean.setChargeAmount(BigDecimal.ZERO);
			//
	        Timestamp lTimestamp = new Timestamp(System.currentTimeMillis());
	        GSTRateBean lGSTRateBean = TredsHelper.getInstance().getGSTRate(pConnection, CommonUtilities.getDate(lTimestamp), lChargeAmount);
			BigDecimal[]  lCgstSgstIgstValues = TredsHelper.getInstance().getCgstSgstIgst(lGSTRateBean,  lChargeAmount, pBillBean.getEntState());

			GstSummaryBean lGstSummaryBean = new GstSummaryBean();
	        lGstSummaryBean.setCharge(lChargeAmount);
	        lGstSummaryBean.setCgst(lGSTRateBean.getCgst());
	        lGstSummaryBean.setSgst(lGSTRateBean.getSgst());
	        lGstSummaryBean.setIgst(lGSTRateBean.getIgst());
	        lGstSummaryBean.setCgstSurcharge(lGSTRateBean.getCgstSurcharge());
	        lGstSummaryBean.setSgstSurcharge(lGSTRateBean.getSgstSurcharge());
	        lGstSummaryBean.setIgstSurcharge(lGSTRateBean.getIgstSurcharge());
	        lGstSummaryBean.setCgstValue(lCgstSgstIgstValues[0]);
	        lGstSummaryBean.setSgstValue(lCgstSgstIgstValues[1]);
	        lGstSummaryBean.setIgstValue(lCgstSgstIgstValues[2]);
	        //
	        lTotalGstSummaryBean = lGstSummaryBean;
		}
		//fill Normal transactions 
		pBillBean.setCgst(lTotalGstSummaryBean.getCgst());
		pBillBean.setSgst(lTotalGstSummaryBean.getSgst());
		pBillBean.setIgst(lTotalGstSummaryBean.getIgst());
		pBillBean.setCgstValue(pBillBean.getCgstValue().add(lTotalGstSummaryBean.getCgstValue()));
		pBillBean.setSgstValue(pBillBean.getSgstValue().add(lTotalGstSummaryBean.getSgstValue()));
		pBillBean.setIgstValue(pBillBean.getIgstValue().add(lTotalGstSummaryBean.getIgstValue()));
		pBillBean.setChargeAmount(pBillBean.getChargeAmount().add(lTotalGstSummaryBean.getCharge()));
	}
	
	private void fillTredsDetails(BillBean pBillBean){
		if(pBillBean.getTredsGstn() == null){
			RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
			HashMap<String,Object> lSettings = (HashMap<String,Object>) lRegistryHelper.getStructure(REGISTRY_TREDS_BILLINGDETAILS);
			//from the registry fill the above details
			pBillBean.setTredsName((String)lSettings.get(ATTRIBUTE_TREDS_NAME));
			pBillBean.setTredsLine1((String)lSettings.get(ATTRIBUTE_TREDS_ADDRESSLINE1));
			pBillBean.setTredsLine2((String)lSettings.get(ATTRIBUTE_TREDS_ADDRESSLINE2));
			pBillBean.setTredsLine3((String)lSettings.get(ATTRIBUTE_TREDS_ADDRESSLINE3));
			pBillBean.setTredsCountry((String)lSettings.get(ATTRIBUTE_TREDS_COUNTRY));
			pBillBean.setTredsState((String)lSettings.get(ATTRIBUTE_TREDS_STATECODE));
			pBillBean.setTredsDistrict((String)lSettings.get(ATTRIBUTE_TREDS_DISTRICT));
			pBillBean.setTredsCity((String)lSettings.get(ATTRIBUTE_TREDS_CITY));
			pBillBean.setTredsZipCode((String)lSettings.get(ATTRIBUTE_TREDS_ZIPCODE));
			pBillBean.setTredsTelephone((String)lSettings.get(ATTRIBUTE_TREDS_TELEPHONE));
			pBillBean.setTredsEmail((String)lSettings.get(ATTRIBUTE_TREDS_EMAIL));
			pBillBean.setTredsPan((String)lSettings.get(ATTRIBUTE_TREDS_PAN));
			pBillBean.setTredsGstn((String)lSettings.get(ATTRIBUTE_TREDS_GSTN));
			pBillBean.setTredsCin((String)lSettings.get(ATTRIBUTE_TREDS_CIN));
			pBillBean.setTredsNatureOfTrans((String)lSettings.get(ATTRIBUTE_TREDS_NATUREOFTRANS));
			pBillBean.setTredsSACCode((String)lSettings.get(ATTRIBUTE_TREDS_SACCODE));
			pBillBean.setTredsSACDesc((String)lSettings.get(ATTRIBUTE_TREDS_SACDESC));
			if(pBillBean!=null&&pBillBean.getBillingType()!=null) {
				pBillBean.setTredsSACDesc(pBillBean.getBillingType().toString());
			}
			//
			pBillBean.setTredsMobile((String)lSettings.get(ATTRIBUTE_TREDS_TELEPHONE));
		}
	}
	
	private String getInvoiceNo(String pEntityCode, long pBillNo, int pFinYearStart, int pMonth, BillType pBillType, Date pBillDate){
		StringBuilder lInvoiceNo = new StringBuilder();
		//
		lInvoiceNo.append("RXIL").append(INVOICE_SEPERATOR);
		if(BillType.Monthly.equals(pBillType)) {
			lInvoiceNo.append(months[pMonth]);
		}else if(BillType.Daily.equals(pBillType)) {
			lInvoiceNo.append(months[pMonth]);
			lInvoiceNo.append(StringUtils.leftPad(getDayOfMonth(pBillDate)+"", 2, "0"));
		}
		if(pMonth > 3){
			lInvoiceNo.append(pFinYearStart-2000);
		}else{
			lInvoiceNo.append((pFinYearStart+1)-2000);
		}
		lInvoiceNo.append(INVOICE_SEPERATOR);
		lInvoiceNo.append(CommonUtilities.appendChars(5, pBillNo+"", "0",false));
		//
		return lInvoiceNo.toString();
	}
	private static int getDayOfMonth(Date pDate) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(pDate);
	    return cal.get(Calendar.DAY_OF_MONTH);
	}
}
