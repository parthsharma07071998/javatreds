
package com.xlx.treds.auction.bean;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.PaymentFileBean.PayfileType;
import com.xlx.treds.auction.bean.PaymentFileBean.Status;
import com.xlx.treds.auction.bo.FinancierSettlementFileGenerator;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.EmailGeneratorBO;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class NPCIPaymentInterace implements IPaymentInterface {
	private static final Logger logger = LoggerFactory.getLogger(NPCIPaymentInterace.class);
	public static final String COLUMN_SEPERATOR = "|";
	public static final String RECORD_SEPERATOR = "\r\n";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_FORMAT2 = "ddMMyyyy";
	public static final String NPCI_FULLFILE_PREFIX = "ACH-TR";
	public static final String NPCI_INTERIMDEBITFILE_PREFIX = "ACH-DR";
	public static final String NPCI_INTERIMCREDITFILE_PREFIX = "ACH-CR";

	public static final String PAYINSTATUS_SUCCESS = "SUCCESS";
	public static final String PAYINSTATUS_FAIL = "FAILED";
	public static final String PAYOUTSTATUS_SUCCESS = "P";

	public static final String REFCODE_NPCITXNFLAG = "NPCITXNFLAG";
	public static final String REFCODE_NPCIREASONCODE = "NPCIREASONCODE";

	public static final String TREDSCODE_LEG1FAILED = "TR001";
	public static final String TREDSCODE_LEG1FAILED_DESC = "Transaction cancelled since corresponding Leg1 failed.";

	private GenericDAO<ObligationBean> obligationDAO;
	private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
	private GenericDAO<PaymentFileBean> paymentFileDAO;
	private CompositeGenericDAO<ObligationSettlementBankDetailsBean> obliSettleBankDetailDAO;
	private GenericDAO<AuctionCalendarBean> auctionCalenderDAO;
	private GenericDAO<FactoringUnitBean> factoringUnitDAO;
	private EmailGeneratorBO emailGeneratorBO;
	private ObligationBO obligationBO;
	private List<Long> newObligationList = null;
	//
	private IPaymentSettlor paymentSettlor = null;
	private static final int CHECK_NPCI_FILE_OFFSET = 25;
	private HashMap<String, String> entityTypes = null;
	private static final String ENTITYTYPE_FINANCIEAR = "FIN";
	private static final String ENTITYTYPE_PURCHASER = "BUY";
	private static final String ENTITYTYPE_SUPPLIER = "SEL";
	private static final String ENTITYTYPE_PLATFORM = "TRE";
	//
    public static final int SETTLEMENT_EXCELL_SHEET1_ID = 0;
    public static final int SETTLEMENT_EXCELL_SHEET1_FUID = 1;
    public static final int SETTLEMENT_EXCELL_SHEET1_TXNENTITY = 2;
    public static final int SETTLEMENT_EXCELL_SHEET1_TXNTYPE = 3;
    public static final int SETTLEMENT_EXCELL_SHEET1_DATE  = 4;
    public static final int SETTLEMENT_EXCELL_SHEET1_AMOUNT = 5;
    public static final int SETTLEMENT_EXCELL_SHEET1_TYPE  = 6;
    public static final int SETTLEMENT_EXCELL_SHEET1_STATUS  = 7;
    public static final int SETTLEMENT_EXCELL_SHEET1_BILLINGSTATUS  = 8;
    public static final int SETTLEMENT_EXCELL_SHEET1_PFID = 9;
    public static final int SETTLEMENT_EXCELL_SHEET1_PFSEQNO = 10;
    public static final int SETTLEMENT_EXCELL_SHEET1_DETAIL1 = 11;
    public static final int SETTLEMENT_EXCELL_SHEET1_DETAIL2 = 12;
    public static final int SETTLEMENT_EXCELL_SHEET1_DETAIL3 = 13;
    public static final int SETTLEMENT_EXCELL_SHEET1_DETAIL4 = 14;
    public static final int SETTLEMENT_EXCELL_SHEET1_PAN = 15;
    public static final int SETTLEMENT_EXCELL_SHEET1_SALESCAT = 16;
    public static final int SETTLEMENT_EXCELL_SHEET1_BIDACCEPTTIME = 17;
	//
    public static final int SETTLEMENT_EXCELL_SHEET2_ID = 0;
    public static final int SETTLEMENT_EXCELL_SHEET2_MATURITYDATE = 1;
    public static final int SETTLEMENT_EXCELL_SHEET2_CURRENCY = 2;
    public static final int SETTLEMENT_EXCELL_SHEET2_AMOUNT = 3;
    public static final int SETTLEMENT_EXCELL_SHEET2_BUYER = 4;
    public static final int SETTLEMENT_EXCELL_SHEET2_BUYERREF = 5;
    public static final int SETTLEMENT_EXCELL_SHEET2_SELLER = 6;
    public static final int SETTLEMENT_EXCELL_SHEET2_SELLERREF = 7;
    public static final int SETTLEMENT_EXCELL_SHEET2_MAKERENTITY = 8;
    public static final int SETTLEMENT_EXCELL_SHEET2_MAKERLOGIN = 9;
    public static final int SETTLEMENT_EXCELL_SHEET2_COUNTERLOGIN = 10;
    public static final int SETTLEMENT_EXCELL_SHEET2_OWNERENTITY = 11;
    public static final int SETTLEMENT_EXCELL_SHEET2_OWNERLOGIN = 12;
    public static final int SETTLEMENT_EXCELL_SHEET2_STATUS = 13;
    public static final int SETTLEMENT_EXCELL_SHEET2_FACTSTARTDATE = 14;
    public static final int SETTLEMENT_EXCELL_SHEET2_FACTENDDATE = 15;
    public static final int SETTLEMENT_EXCELL_SHEET2_FACTMAXENDDATE = 16;
    public static final int SETTLEMENT_EXCELL_SHEET2_AUTOACCEPTBIDS = 17;
    public static final int SETTLEMENT_EXCELL_SHEET2_AUTOACCEPTBIDTYPES = 18;
    public static final int SETTLEMENT_EXCELL_SHEET2_P1COSTBEARER = 19;
    public static final int SETTLEMENT_EXCELL_SHEET2_P1COSTPERCENT = 20;
    public static final int SETTLEMENT_EXCELL_SHEET2_P2COSTBEARER = 21;
    public static final int SETTLEMENT_EXCELL_SHEET2_P2COSTPERCENT = 22;
    public static final int SETTLEMENT_EXCELL_SHEET2_P3COSTBEARER = 23;
    public static final int SETTLEMENT_EXCELL_SHEET2_P3COSTPERCENT = 24;
    public static final int SETTLEMENT_EXCELL_SHEET2_CHARGEBEARER = 25;
    public static final int SETTLEMENT_EXCELL_SHEET2_ENABLEL3 = 26;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTEDBID = 27;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTEDRATE = 28;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTEDHAIRCUT = 29;
    public static final int SETTLEMENT_EXCELL_SHEET2_FACTOREDAMOUT = 30;
    public static final int SETTLEMENT_EXCELL_SHEET2_BUYERL1INST = 31;
    public static final int SETTLEMENT_EXCELL_SHEET2_SELLERL1INST = 32;
    public static final int SETTLEMENT_EXCELL_SHEET2_BUYERL2INST = 33;
    public static final int SETTLEMENT_EXCELL_SHEET2_CHARGES = 34;
    public static final int SETTLEMENT_EXCELL_SHEET2_FINANCIER = 35;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTINGENTITY = 36;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTINGLOGIN = 37;
    public static final int SETTLEMENT_EXCELL_SHEET2_ACCEPTTIME = 38;
    public static final int SETTLEMENT_EXCELL_SHEET2_LIMITUTILIZED = 39;
    public static final int SETTLEMENT_EXCELL_SHEET2_BUYSELSLIMITUTILIZED = 40;
    //


	public NPCIPaymentInterace() {
		super();
		obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
		obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
		paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
		obliSettleBankDetailDAO = new CompositeGenericDAO<ObligationSettlementBankDetailsBean>(ObligationSettlementBankDetailsBean.class);
		entityTypes = new HashMap<String, String>();
		emailGeneratorBO = new EmailGeneratorBO();
		obligationBO = new ObligationBO();
		newObligationList = new ArrayList<Long>();
		auctionCalenderDAO = new GenericDAO<AuctionCalendarBean>(AuctionCalendarBean.class);
		factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
	}

	@Override
	public void generateFile(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
		generateFiles(pConnection, pPaymentFileBean, pAppUserBean);
		if (pPaymentFileBean != null && pPaymentFileBean.getId() != null) {
			emailGeneratorBO.sendObligationsDueDetails(pConnection, pPaymentFileBean.getId(), pPaymentFileBean.getDate());

			String lHeader = "Financier Settlement File Generation Leg1";
			try {
				if(!CommonAppConstants.Yes.Yes.equals(pPaymentFileBean.getSkipL1FileGeneration())) {
					FinancierSettlementFileGenerator lFinSettleFileGenerator = new FinancierSettlementFileGenerator();
					int[] lFinFileCount = lFinSettleFileGenerator.generateLegFile(pConnection, pPaymentFileBean.getDate(), ObligationBean.Type.Leg_1, null);
					logger.info("NPCI paymentInterface : " + lHeader + " :: Financier Count : " + lFinFileCount[0] + " :: File Count : " + lFinFileCount[1]);
				}else {
					logger.info("NPCI paymentInterface : Sending of L1 file skipped.");
				}
			} catch (Exception e) {
				logger.info("NPCI paymentInterface : Error while " + lHeader + " : " + e.getMessage());
				logger.info("NPCI paymentInterface : " + ((e.getStackTrace() != null) ? e.getStackTrace().toString() : ""));
			}
		}
	}

	@Override
	public String getQuery(PaymentFileBean pPaymentFileBean) {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		//
		// leftouter join CompanyLocations, CompanyBankDetails
		lSql.append("SELECT * FROM OBLIGATIONS ");
		lSql.append(" left outer join CompanyLocations on CLID=OBSETTLEMENTCLID ");
		lSql.append(" left outer join CompanyDetails on CDID=CLCDID ");
		lSql.append(" left outer join CompanyBankDetails ");
		lSql.append(" on ((CDENABLELOCATIONWISESETTLEMENT=").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()));
		lSql.append("  AND CBDID=CLCBDID ) OR (CDENABLELOCATIONWISESETTLEMENT IS NULL AND CDID=CBDCDID AND CBDDEFAULTACCOUNT = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode())).append(" ) ) ");
		lSql.append(" left outer join ObligationSplits on OBID=OBSOBID ");
		lSql.append(" LEFT OUTER JOIN FACTORINGUNITS ON FUID=OBFUID ");
		lSql.append(" LEFT OUTER JOIN INSTRUMENTS ON FUID=INFUID ");
		lSql.append(" WHERE OBRECORDVERSION > 0 ");
		lSql.append(" AND ( OBSRECORDVERSION is null OR OBSRECORDVERSION > 0 ) ");
		lSql.append(" AND ( CDRECORDVERSION is null OR CDRECORDVERSION > 0 ) ");
		lSql.append(" AND ( CBDRECORDVERSION is null OR CBDRECORDVERSION > 0 ) ");
		lSql.append(" AND ( CLRECORDVERSION is null OR CLRECORDVERSION > 0 ) ");
		lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pPaymentFileBean.getDate()));
		lSql.append(" AND OBSTATUS IN ( ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(ObligationBean.Status.Created.getCode())).append(" ) ");
		lSql.append(" AND OBSSTATUS IN ( ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(ObligationBean.Status.Created.getCode())).append(" ) ");
		lSql.append(" AND OBSPAYMENTSETTLOR = ").append(lDbHelper.formatString(pPaymentFileBean.getFacilitator()));
		lSql.append(" ORDER BY OBFUID, OBSOBID, OBTYPE, OBTXNTYPE DESC, OBSPARTNUMBER, OBDATE  "); 
		return lSql.toString();
	}

	private void generateFiles(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
		// seperate function since we will be generating multiple payment files.
		// pPaymentFileBean is just a dummy file containing date, facilitator
		// and txnType
		// we should generate multiple file from the same
		String lSql = null;
		DBHelper lDbHelper = DBHelper.getInstance();
		int lNPCIMaxObligations = 0;
		//
		Long lNCPIMaxObli = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_NCPIMAXOBLIGATIONSPERFILE);
		if (lNCPIMaxObli == null) {
			throw new CommonBusinessException("Max Obligation per file not set.");
		}
		lNPCIMaxObligations = lNCPIMaxObli.intValue();
		lSql = getQuery(pPaymentFileBean);

		List<ObligationSettlementBankDetailsBean> lObligationList = obliSettleBankDetailDAO.findListFromSql(pConnection, lSql, 0);
		if ((lObligationList == null) || (lObligationList.size() == 0))
			throw new CommonBusinessException("No obligations");

		// cache entity mappings
		Map<String, FacilitatorEntityMappingBean> lFacilitatorMap = null;
		Map<Long, CompanyBankDetailBean> lDesignatedBankMap = null;
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);

		int lFileSeqNo = 1;
		IObligation lObligationBean = null;
		IObligation lParentObligationBean = null;
		CompanyBankDetailBean lCompanyBankDetailBean = null;
		InstrumentBean lInstrumentBean = null;
		FactoringUnitBean lFactoringUnitBean = null;
		BigDecimal lTotalValue = BigDecimal.ZERO;
		boolean lCreateFile = false;
		boolean lCheckFUIdChange = false;
		int lTotalObliSize = lObligationList.size();
		Long lNextFUId = null;
		//
		lDesignatedBankMap = TredsHelper.getInstance().getEntityBankDetailsMap(pConnection);
		lFacilitatorMap = TredsHelper.getInstance().getFacilitatorEntityMap(pConnection, AppConstants.FACILITATOR_NPCI);

		HashSet<Long> lUpdatedParentObliList = new HashSet<Long>();
		Map<String,Map<String,BigDecimal>> lDebitCreditMap = new HashMap<>(); 
		List<IObligation> lObligationBatchList = null;
		List<Long> lDeletedFuIds = new ArrayList<Long>();
		List<Long> lNegativeValueFuIds = new ArrayList<Long>();
		//
		// default setting before starting loop
		lObligationBatchList = new ArrayList<IObligation>();
		pPaymentFileBean.setId(lDbHelper.getUniqueNumber(pConnection, "PaymentFile.Id"));
		lTotalValue = BigDecimal.ZERO;
		lFileSeqNo = 1;
		lCreateFile = false;
		lCheckFUIdChange = false;
		lNextFUId = null;
		//
		String lDebitCreditKey = null;
		Map<String,BigDecimal> lAmountHash = null;
		for (int lPtr = 0; lPtr < lTotalObliSize; lPtr++) {
			lObligationBean = lObligationList.get(lPtr).getActualObligationBean(); 
			lParentObligationBean = lObligationList.get(lPtr).getObligationParentBean();
			lCompanyBankDetailBean = lObligationList.get(lPtr).getCompanyBankDetailBean();
			lInstrumentBean = lObligationList.get(lPtr).getInstrumentBean();
			lFactoringUnitBean = lObligationList.get(lPtr).getFactoringUnitBean();
			if (!lDeletedFuIds.contains(lFactoringUnitBean.getId())) {
				if (lFactoringUnitBean==null || lFactoringUnitBean.getRecordVersion().equals(new Long(0))) {
					if (!lDeletedFuIds.contains(lFactoringUnitBean.getId())) lDeletedFuIds.add(lParentObligationBean.getFuId());
				}
				if (lInstrumentBean==null || lInstrumentBean.getRecordVersion().equals(new Long(0))) {
					if (!lDeletedFuIds.contains(lFactoringUnitBean.getId())) lDeletedFuIds.add(lParentObligationBean.getFuId());
				}
			}
			if (!lNegativeValueFuIds.contains(lParentObligationBean.getFuId())) {
				if (lObligationBean.getAmount().compareTo(BigDecimal.ZERO) < 0) {
					lNegativeValueFuIds.add(lParentObligationBean.getFuId());
	    		}
			}
			lDebitCreditKey = lParentObligationBean.getFuId()+CommonConstants.KEY_SEPARATOR+lObligationBean.getPartNumber();
			if (!lDebitCreditMap.containsKey(lDebitCreditKey)) {
				lDebitCreditMap.put(lDebitCreditKey,new HashMap<String,BigDecimal>());
			}
			lAmountHash = lDebitCreditMap.get(lDebitCreditKey);
			if (!lAmountHash.containsKey(lParentObligationBean.getTxnType().getCode())) {
				lAmountHash.put(lParentObligationBean.getTxnType().getCode(), BigDecimal.ZERO);			
			}
			lAmountHash.put(lParentObligationBean.getTxnType().getCode(), lAmountHash.get(lParentObligationBean.getTxnType().getCode()).add(lObligationBean.getAmount()));
		}
		List<Map<String, Object>> lMessages = new ArrayList<>();
		for (Long lKey: lDeletedFuIds) {
			TredsHelper.getInstance().appendMessage(lMessages, "Fuid : "+lKey, "Instrument or Factoring unit not found.", null);
		}
		for (Long lKey: lNegativeValueFuIds) {
			TredsHelper.getInstance().appendMessage(lMessages, "Fuid : "+lKey, "Obligations with negative value found.", null);
		}
		for (String lKey: lDebitCreditMap.keySet()) {
			if (lDebitCreditMap.get(lKey).get(ObligationBean.TxnType.Debit.getCode()).compareTo(lDebitCreditMap.get(lKey).get(ObligationBean.TxnType.Credit.getCode())) != 0) {
				TredsHelper.getInstance().appendMessage(lMessages, "Fuid : "+lKey, "There is some mismatch in debit and credit amount.", null);
			}
		}
		if(!lMessages.isEmpty()){
	       throw new CommonBusinessException( new JsonBuilder(lMessages).toString());
		}
		for (int lPtr = 0; lPtr < lTotalObliSize; lPtr++) {
			lObligationBean = lObligationList.get(lPtr).getActualObligationBean(); 
			lParentObligationBean = lObligationList.get(lPtr).getObligationParentBean();
			lCompanyBankDetailBean = lObligationList.get(lPtr).getCompanyBankDetailBean();
			lObligationBean.setParentObligation(lParentObligationBean);
			

			//
			if (!lCheckFUIdChange) {
				// last obligation in the complete list
				if (lPtr == lTotalObliSize - 1) {
					lCreateFile = true;
				} else if (lFileSeqNo == (lNPCIMaxObligations - CHECK_NPCI_FILE_OFFSET)) {
					// batch offset reached
					if ((lTotalObliSize <= (lPtr + CHECK_NPCI_FILE_OFFSET))) {
						// last batch and remaining can be accommodated in the
						// same last file
					} else {
						// not last batch
						lCheckFUIdChange = true;
						//
						// this is done here also because the coming FUId may be
						// different hence we will stop here
						// we can skip this
						/*
						 * lNextFUId = null; if(lPtr+1 <
						 * lObligationList.size()){ lNextFUId =
						 * lObligationList.get(lPtr+1).getFuId(); }
						 * if(!lObligationBean.getFuId().equals(lNextFUId)){
						 * lCheckFUIdChange = false; lCreateFile = true; }
						 */
					}
				}
			} else {
				lNextFUId = null;
				if (lPtr + 1 < lObligationList.size()) {
					lNextFUId = lObligationList.get(lPtr + 1).getObligationBean().getFuId();
				}
				if (!lObligationBean.getFuId().equals(lNextFUId)) {
					lCheckFUIdChange = false;
					lCreateFile = true;
				}
			}
			//
			if (!lObligationBean.isParentObligation()) {
				lObligationBean.setFileSeqNo(Long.valueOf(lFileSeqNo++));
			}
			lObligationBean.setRecordUpdator(pAppUserBean.getId());
			//
			updateObligation(pConnection, lObligationBean, pPaymentFileBean, lFacilitatorMap, lDesignatedBankMap, lMemoryTable, lCompanyBankDetailBean);
			if (!lObligationBean.isParentObligation()) {
				if (!lUpdatedParentObliList.contains(lParentObligationBean.getId())) {
					lUpdatedParentObliList.add(lParentObligationBean.getId());
					updateObligation(pConnection, lParentObligationBean, pPaymentFileBean, lFacilitatorMap, lDesignatedBankMap, lMemoryTable, lCompanyBankDetailBean);
				}
			}
			//
			lTotalValue = lTotalValue.add(lObligationBean.getAmount());
			lObligationBatchList.add(lObligationBean);

			if (lCreateFile) {
				pPaymentFileBean.setFileName(getFileName(pPaymentFileBean));
				pPaymentFileBean.setRecordCount(Long.valueOf(lObligationList.size()));
				pPaymentFileBean.setTotalValue(lTotalValue);
				pPaymentFileBean.setGeneratedByAuId(pAppUserBean.getId());
				pPaymentFileBean.setGeneratedTime(new Timestamp(System.currentTimeMillis()));
				pPaymentFileBean.setStatus(PaymentFileBean.Status.Generated);
				paymentFileDAO.insert(pConnection, pPaymentFileBean);
				//
				tempCreateFile(lObligationBatchList, pPaymentFileBean);
				//
				lObligationBatchList = new ArrayList<IObligation>();
				lCreateFile = false;
				lCheckFUIdChange = false;
				lNextFUId = null;
				lTotalValue = BigDecimal.ZERO;
				lFileSeqNo = 1;
				// generate id for the next batch only if it is not the last of
				// the total obligations
				if (!(lPtr == lTotalObliSize - 1)) {
					// this is necessary since the same is added in the
					// obligation before updating it
					pPaymentFileBean.setId(lDbHelper.getUniqueNumber(pConnection, "PaymentFile.Id"));
				}
				sendEmailToNpci(pConnection,pPaymentFileBean,pAppUserBean);
			}
		}
	}

	private void sendEmailToNpci(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
		//Send Emails.
		Map<String, Object> lDataValues = new HashMap<>();
		getNpciEmailData(pConnection,pPaymentFileBean,lDataValues);
        PaymentFileBean lPaymentFileBean = getFileContents(pConnection, pPaymentFileBean, pAppUserBean);
        final String lContents = lPaymentFileBean.getContents();
    	byte[] lData = lContents.getBytes();
	    String lFileType = "text/plain";
	    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lData, lFileType)));
		lMimeBodyPart.setFileName(lPaymentFileBean.getFileName());
		List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
		lListAttach.add(lMimeBodyPart);
		lDataValues.put(EmailSender.ATTACHMENTS,lListAttach);
		String lEmailIds =  RegistryHelper.getInstance().getString(AppConstants.REGISTRY_NPCIEMAILIDS);
		String[] lToEmailIds = StringUtils.split(lEmailIds,CommonConstants.COMMA); 
		lDataValues.put(EmailSender.TO,lToEmailIds);
		lDataValues.put(EmailSender.CC, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
		EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_NPCISETTLEMENTTEMPLATE, lDataValues);
		//Email Sent
	}
	
	private void getNpciEmailData(Connection pConnection,PaymentFileBean pPaymentFileBean,Map<String, Object> pDataValues) {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT OBTXNTYPE ,COUNT(*) DEBITCREDITCOUNT FROM OBLIGATIONS , OBLIGATIONSPLITS WHERE OBSOBID=OBID ");
		lSql.append(" AND OBPFID = ").append(pPaymentFileBean.getId());
		lSql.append(" AND OBSPFID = ").append(pPaymentFileBean.getId());
		lSql.append(" GROUP BY OBTXNTYPE ");
		logger.info(lSql.toString());
		try (Statement lStatement = pConnection.createStatement();
			ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
		    while (lResultSet.next()){
		    	if(ObligationBean.TxnType.Debit.getCode().equals(lResultSet.getString("OBTXNTYPE"))) {
		    		pDataValues.put("debitCount",lResultSet.getInt("DEBITCREDITCOUNT"));
		    	}
		    	if(ObligationBean.TxnType.Credit.getCode().equals(lResultSet.getString("OBTXNTYPE"))) {
		    		pDataValues.put("creditCount",lResultSet.getInt("DEBITCREDITCOUNT"));
		    	}
		    }
		    if(!pDataValues.containsKey("creditCount")) {
		    	pDataValues.put("creditCount",0);
		    }
		    if(!pDataValues.containsKey("debitCount")) {
		    	pDataValues.put("debitCount",0);
		    }
		} catch (Exception e) {
			logger.info("Error in getDataForNpciEmail : "+e.getMessage());
		} 
		lSql = new StringBuilder();
		lSql.append(" SELECT ( SUBSTR(OBPAYDETAIL2 , 0, 4 ) || ' - ' || BANKNAME ) BANKDET ");
		lSql.append(" FROM OBLIGATIONS  ");
		lSql.append(" LEFT OUTER JOIN ");
		lSql.append(" ( ");
		lSql.append(" SELECT RCVVALUE BANKCODE, RCVDESC BANKNAME ");
		lSql.append(" FROM REFCODEVALUES ");
		lSql.append(" WHERE RCVRECORDVERSION > 0 ");
		lSql.append(" AND RCVRECID = 11 ");
		lSql.append(" ) MYREFCODES ON (MYREFCODES.BANKCODE = SUBSTR(OBPAYDETAIL2 , 0,4)) ");
		lSql.append(" WHERE OBRECORDVERSION > 0 AND  OBPFID = ").append(pPaymentFileBean.getId());
		lSql.append(" GROUP BY SUBSTR(OBPAYDETAIL2 , 0, 4 ), BANKNAME ");
		lSql.append(" ORDER BY 1 ");
		logger.info(lSql.toString());
		List<String> lBankNameList = new ArrayList<>();
		try (Statement lStatement = pConnection.createStatement();
		ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
			while (lResultSet.next()){
				lBankNameList.add(lResultSet.getString("BANKDET"));
		    }
		} catch (Exception e) {
			logger.info("Error in getDataForNpciEmail : "+e.getMessage());
		} 
		if(!lBankNameList.isEmpty()) {
			pDataValues.put("banks", lBankNameList);
		}
		pDataValues.put("date", pPaymentFileBean.getDate());
	}

	public byte[] createExcellFile(ExecutionContext pExecutionContext, Date pSettlementDate,AppUserBean pAppUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		ObligationBean lFilterBean = new ObligationBean();
		lFilterBean.setDate((java.sql.Date) pSettlementDate);
		lFilterBean.setFilterToDate((java.sql.Date) pSettlementDate);
		List<ObligationBean> lObligationsList = obligationBO.findList(lConnection, lFilterBean, null, pAppUserBean);
		Map<String,Object> lSheet3Data = new HashMap<>();
		List<String> lBankCodes = new ArrayList<>();
		Map<Long, Object[]> lSheetData1 = getObligationsForExcell(lConnection,lObligationsList,lSheet3Data);
		Map<Long, Object[]> lSheetData2 = getFactoringUnits(lConnection,pSettlementDate,lSheet3Data);
		Map<Long, Object[]> lSheetData3 = getOligationAndFuidComparisionData(lSheet3Data);
		Map<Long, Object[]> lSheetData5 = getPayFileContent(lConnection, pSettlementDate, lBankCodes);
 		Map<Long, Object[]> lSheetData6 = getBankCodeAndNames(lBankCodes);
        
 		Object[] lWorkBookData = new Object[]{lSheetData1,lSheetData2,lSheetData3,lSheetData5,lSheetData6};
 		// Blank workbook 
        XSSFWorkbook lWorkbook = new XSSFWorkbook(); 
        // Create a blank sheet 
        XSSFSheet lSheet1 = lWorkbook.createSheet("Settlement");
        XSSFSheet lSheet2 = lWorkbook.createSheet("Factored");
        XSSFSheet lSheet3 = lWorkbook.createSheet("Compare");
        XSSFSheet lSheet5 = lWorkbook.createSheet("PayFile");
        XSSFSheet lSheet6 = lWorkbook.createSheet("Banks");
        XSSFSheet[] lSheetArr = new XSSFSheet[]{lSheet1,lSheet2,lSheet3,lSheet5,lSheet6};
        DataFormat lDataFormat = lWorkbook.createDataFormat();
        CellStyle lDateCellStyle = lWorkbook.createCellStyle();
        lDateCellStyle.setDataFormat(lDataFormat.getFormat("dd-mm-yyyy"));
        CellStyle lNumCellStyle = lWorkbook.createCellStyle();
        lNumCellStyle.setDataFormat(lDataFormat.getFormat("##0"));
        for (int i=0; i<lWorkBookData.length; i++ ){
        if (lWorkBookData[i] !=null){
          	 for (Long lKey : ((Map<Long, Object[]>) lWorkBookData[i]).keySet()) { 
                 Row row = lSheetArr[i].createRow(lKey.intValue()); 
                 lSheetArr[i].autoSizeColumn(1000000000);
                 Object[] lObjArr = (Object[]) ((Map<Long, Object[]>) lWorkBookData[i]).get(lKey);
                 int lCellnum = 0; 
                 for (Object lData : lObjArr) { 
                     Cell lCell = row.createCell(lCellnum); 
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
                     }else if (lData instanceof Timestamp) {
                      	lCell.setCellValue((Timestamp)lData);
                      	lCell.setCellStyle(lDateCellStyle);
                     }else {
                     	lCell.setCellValue("");
                     	logger.info("test");
                     }
                     lCellnum++;
                 } 
          	   } 
        	}
        }
//        XSSFSheet lSheet4 = lWorkbook.createSheet();
//        CellReference cr = new CellReference("A1");
//        XSSFPivotTable lPivotTable = lSheet4.createPivotTable(formatReferenceAsString(lSheet1),cr, lSheet1);
//        lPivotTable.setParentSheet(lSheet1);
//        //Configure the pivot table
//        //Use first column as row label
//		lPivotTable.addReportFilter(6);
//		lPivotTable.addReportFilter(7);
//		lPivotTable.addRowLabel(2);
//		//the following makes PivotFields(1) a DataField and creates a DataColumn for this
//		lPivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 5, "Count Of Amount");
//		lPivotTable.addColumnLabel(DataConsolidateFunction.SUM, 5, "Sum of Amount");
//        CTPivotFields lPivotFields = lPivotTable.getCTPivotTableDefinition().getPivotFields();
//
//        CTPivotField pivotField = CTPivotField.Factory.newInstance();
//        CTItems items = pivotField.addNewItems();
//
//        pivotField.setAxis(STAxis.AXIS_COL);
//        pivotField.setShowAll(false);
//        for (int i = 0; i <= 17; i++) {
//            items.addNewItem().setT(STItemType.DEFAULT);
//        }
//        items.setCount(items.sizeOfItemArray());
//        lPivotFields.setPivotFieldArray(5, pivotField);
//
//        // colfield should be added for the second one.
//        CTColFields colFields;
//        if (lPivotTable.getCTPivotTableDefinition().getColFields() != null) {
//            colFields = lPivotTable.getCTPivotTableDefinition().getColFields();
//        } else {
//            colFields = lPivotTable.getCTPivotTableDefinition().addNewColFields();
//        }
//        colFields.addNewField().setX(3);
//        colFields.setCount(colFields.sizeOfFieldArray());
       ByteArrayOutputStream lByetOutputStream = new ByteArrayOutputStream();
   	try {
   		lWorkbook.write(lByetOutputStream);
   		lByetOutputStream.close();
   		return lByetOutputStream.toByteArray();
   	}catch(Exception e){
   		
   	}
		return null;
	}
	
	protected final AreaReference formatReferenceAsString(XSSFSheet lSheet) {
		  Row lFirstRow =  lSheet.getRow(lSheet.getFirstRowNum());
		  Row lLastRow =  lSheet.getRow(lSheet.getLastRowNum());
		  CellReference topLeft = new CellReference(lSheet.getFirstRowNum(),lFirstRow.getFirstCellNum(),false, false);
		  CellReference aaLeft = new CellReference(lSheet.getFirstRowNum(),lFirstRow.getLastCellNum()-1,false, false);
		  CellReference botRight = new CellReference(lSheet.getLastRowNum(),lLastRow.getLastCellNum()-1,false , false);
		  return new AreaReference(topLeft, botRight);
	}

	private Map<Long, Object[]> getOligationAndFuidComparisionData(Map<String, Object> lSheet3Data) {
		ArrayList<Long> lFuIdList = (ArrayList<Long>) lSheet3Data.get("fuids");
		ArrayList<Long> lObFuIdList = (ArrayList<Long>) lSheet3Data.get("obfuids");
		ArrayList<Long> lSuperSetFuids = new ArrayList<Long>();
		Object[] lHeaders = new Object[]{"OBFUID","FUID","COMPARE"};
		Object[] lRowData = null;
		int lCount = 1;
		Map<Long,Object[]> lDataHash = new HashMap<>();
     	lDataHash.put(new Long(0), lHeaders);
		for (Long lFuid : lFuIdList){
			lSuperSetFuids.add(lFuid);
		}
		for (Long lFuid : lObFuIdList){
			if (!lSuperSetFuids.contains(lFuid)){
				lSuperSetFuids.add(lFuid);
			}
		}
		for (Long lIds : lSuperSetFuids){
			lRowData = new Object[3];
			lRowData[0] = lObFuIdList.contains(lIds)?lIds:"";
			lRowData[1] = lFuIdList.contains(lIds)?lIds:"";
			lRowData[2] = (lObFuIdList.contains(lIds)&&lFuIdList.contains(lIds))?"":"Check";
			lDataHash.put(new Long(lCount), lRowData);
			lCount++;
		}
		return lDataHash;
	}

	private Map<Long, Object[]> getObligationsForExcell(Connection pConnection, List<ObligationBean> lObligationsList, Map<String, Object> pSheet3Data) throws Exception {
		
		Object[] lHeaders = new Object[]{"Id","Factoring Unit","Transacting Entity Name","Transaction Type","Date","Amount","Type","Status","Billing Status","Pay File Id","Pay File Sequence No","Detail 1","Detail 2","Detail 3","Detail 4","PAN","Sales Category Description","Bid Accept Date Time"};
		List<Long> lObidToBeAddedToExcell = new ArrayList<>();
		List<Long> lFuids = new ArrayList<>();
		int lCount = 1;
     	Object[] lRowData = null;
     	Map<Long,Object[]> lDataHash = new HashMap<>();
     	lDataHash.put(new Long(0), lHeaders);
     	for (ObligationBean lObligationBean : lObligationsList){
			//skip any obliations which are already generated, so that the pre checksum report will be correct
     		if (lObligationBean.getPfId()!=null){
     			continue; 
     		}
     		if (!lObidToBeAddedToExcell.contains(lObligationBean.getId())){
     			if (lObligationBean.getType().equals(Type.Leg_1)){
     				if (!lFuids.contains(lObligationBean.getFuId())){
     					lFuids.add(lObligationBean.getFuId());
     				}
     			}
     			lObidToBeAddedToExcell.add(lObligationBean.getId());
         		lRowData = new Object[18];
             	lRowData[SETTLEMENT_EXCELL_SHEET1_ID] = lObligationBean.getId();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_FUID] = lObligationBean.getFuId();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_TXNENTITY] = lObligationBean.getTxnEntityName();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_TXNTYPE] = lObligationBean.getTxnType().toString();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_DATE] = lObligationBean.getDate();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_AMOUNT] = lObligationBean.getAmount();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_TYPE] = lObligationBean.getType().toString();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_STATUS] = lObligationBean.getStatus().toString();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_BILLINGSTATUS] = "";
             	lRowData[SETTLEMENT_EXCELL_SHEET1_PFID] = lObligationBean.getPfId();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_PFSEQNO] = "";
             	lRowData[SETTLEMENT_EXCELL_SHEET1_DETAIL1] = "";
             	lRowData[SETTLEMENT_EXCELL_SHEET1_DETAIL2] = "";
            	lRowData[SETTLEMENT_EXCELL_SHEET1_DETAIL3] = "";
             	lRowData[SETTLEMENT_EXCELL_SHEET1_DETAIL4] = "";
             	lRowData[SETTLEMENT_EXCELL_SHEET1_PAN] = lObligationBean.getPan();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_SALESCAT] = lObligationBean.getSalesCategoryDesc();
             	lRowData[SETTLEMENT_EXCELL_SHEET1_BIDACCEPTTIME] = lObligationBean.getBidAcceptDateTime();
             	lDataHash.put(new Long(lCount),lRowData );
             	lCount++;
     		}
     	}
     	Collections.sort(lFuids);
     	pSheet3Data.put("obfuids", lFuids);
     	return lDataHash;
	}

	private Map<Long, Object[]> getFactoringUnits(Connection pConnection, Date pSettlementDate, Map<String, Object> pSheet3Data) throws Exception {
		StringBuilder lSql = new StringBuilder();
		List<AuctionCalendarBean> lList = null;
		Timestamp lFirstDay = null;
		Timestamp lLastDay = null;
		lSql.append(" SELECT * FROM AUCTIONCALENDAR WHERE ACCONFWINDOWS LIKE '%\"settlementDate\":\""+FormatHelper.getDisplay("dd-MMM-yyyy",pSettlementDate)+"\"%' ");
		lList = auctionCalenderDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Date lStartDay = null;
		Date lEndDay = null;
		AuctionCalendarBean lStartBean = null;
		AuctionCalendarBean lEndBean = null;
		for (AuctionCalendarBean lAuctionCalendarBean : lList){
			if (lStartDay==null && lEndDay==null){
				lStartDay =lAuctionCalendarBean.getDate();
				lStartBean = lAuctionCalendarBean;
				lEndDay = lAuctionCalendarBean.getDate();
				lEndBean = lAuctionCalendarBean;
			}
			if (lAuctionCalendarBean.getDate().before(lStartDay)){
				lStartDay =lAuctionCalendarBean.getDate();
				lStartBean = lAuctionCalendarBean;
			}
			if (lAuctionCalendarBean.getDate().after(lStartDay)){
				lEndDay = lAuctionCalendarBean.getDate();
				lEndBean = lAuctionCalendarBean;
			}
		}	
		for (ConfirmationWindowBean lConfBean:lStartBean.getConfWinList()){ 
			if(CommonUtilities.compareDate(pSettlementDate, lConfBean.getSettlementDate())){
				lFirstDay = lConfBean.getConfStartTime();
			}
		}
		for (ConfirmationWindowBean lConfBean:lEndBean.getConfWinList()){ 
			if(CommonUtilities.compareDate(pSettlementDate, lConfBean.getSettlementDate())){
				lLastDay = lConfBean.getConfStartTime();
			}
		}
		lSql = new StringBuilder();
		lSql.append(" SELECT * FROM FACTORINGUNITS WHERE FUACCEPTDATETIME BETWEEN ");
		lSql.append(" TO_DATE ( '"+FormatHelper.getDisplay("dd-MM-yyyy HH:mm:ss", lFirstDay)+"','DD-MM-YYYY HH24:MI:SS')");
		lSql.append(" AND ");
		lSql.append(" TO_DATE ( '"+FormatHelper.getDisplay("dd-MM-yyyy HH:mm:ss", lLastDay)+"','DD-MM-YYYY HH24:MI:SS')");
		List<FactoringUnitBean> lFuList = factoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Object[] lHeaders = new Object[]{"Id","Maturity Date","Currency","Amount","Buyer","Buyer Reference","Seller","Seller Reference","Introducing Entity","Introducing Login","Counter Login","Owner Entity","Owner Login","Status","Factoring Start Date","Factoring End Date","Factoring Maximum End Date","Auto Accept Bids","Auto Acceptable Bid Types","Period 1 Cost Bearer","Period 1 Cost Percent","Period 2 Cost Bearer","Period 2 Cost Percent","Period 3 Cost Bearer","Period 3 Cost Percent","Charge Bearer","Enable Leg 3 Settlement","Accepted Bid","Accepted Rate","Accepted Haircut","FactoredAmount","Buyer Interest Leg1","Seller Interest Leg1","Buyer Interest Leg2","Charges","Financier","Accepting Entity","Accepting Login","Accept Time","Limit Utilized","Buyer-Seller Limit Utilized"};
		int lCount = 1;
     	Object[] lRowData = null;
     	Map<Long,Object[]> lDataHash = new HashMap<>();
     	List<Long> lFuids = new ArrayList<>();
     	lDataHash.put(new Long(0), lHeaders);
		for (FactoringUnitBean lFuUnitBean : lFuList){
			if (!lFuids.contains(lFuUnitBean.getId())){
				lFuids.add(lFuUnitBean.getId());
			}
			lRowData = new Object[41];
			lRowData[SETTLEMENT_EXCELL_SHEET2_ID] = lFuUnitBean.getId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_MATURITYDATE] = lFuUnitBean.getMaturityDate();
			lRowData[SETTLEMENT_EXCELL_SHEET2_CURRENCY] = lFuUnitBean.getCurrency();
			lRowData[SETTLEMENT_EXCELL_SHEET2_AMOUNT] = lFuUnitBean.getAmount();
			lRowData[SETTLEMENT_EXCELL_SHEET2_BUYER] = lFuUnitBean.getPurchaser();
			lRowData[SETTLEMENT_EXCELL_SHEET2_BUYERREF] = lFuUnitBean.getPurchaserRef();
			lRowData[SETTLEMENT_EXCELL_SHEET2_SELLER] = lFuUnitBean.getSupplier();
			lRowData[SETTLEMENT_EXCELL_SHEET2_SELLERREF] = lFuUnitBean.getSupplierRef();
			lRowData[SETTLEMENT_EXCELL_SHEET2_MAKERENTITY] = lFuUnitBean.getIntroducingEntity();
			lRowData[SETTLEMENT_EXCELL_SHEET2_MAKERLOGIN] = lFuUnitBean.getIntroducingLoginId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_COUNTERLOGIN] = lFuUnitBean.getCounterLoginId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_OWNERENTITY] = lFuUnitBean.getOwnerEntity();
			lRowData[SETTLEMENT_EXCELL_SHEET2_OWNERLOGIN] = lFuUnitBean.getOwnerLoginId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_STATUS] = lFuUnitBean.getStatus().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_FACTSTARTDATE] = lFuUnitBean.getFactorStartDateTime();
			lRowData[SETTLEMENT_EXCELL_SHEET2_FACTENDDATE] = lFuUnitBean.getFactorEndDateTime();
			lRowData[SETTLEMENT_EXCELL_SHEET2_FACTMAXENDDATE] = lFuUnitBean.getFactorMaxEndDateTime();
			lRowData[SETTLEMENT_EXCELL_SHEET2_AUTOACCEPTBIDS] = lFuUnitBean.getAutoAccept().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_AUTOACCEPTBIDTYPES] = lFuUnitBean.getAutoAcceptableBidTypes().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P1COSTBEARER] = lFuUnitBean.getPeriod1CostBearer().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P1COSTPERCENT] = lFuUnitBean.getPeriod1CostPercent();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P2COSTBEARER] = lFuUnitBean.getPeriod2CostBearer().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P2COSTPERCENT] = lFuUnitBean.getPeriod2CostPercent();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P3COSTBEARER] = lFuUnitBean.getPeriod3CostBearer().toString();
			lRowData[SETTLEMENT_EXCELL_SHEET2_P3COSTPERCENT] = lFuUnitBean.getPeriod3CostPercent();
			lRowData[SETTLEMENT_EXCELL_SHEET2_CHARGEBEARER] = lFuUnitBean.getChargeBearer();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ENABLEL3] = lFuUnitBean.getSettleLeg3Flag();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTEDBID] = lFuUnitBean.getBdId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTEDRATE] = lFuUnitBean.getAcceptedRate();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTEDHAIRCUT] = lFuUnitBean.getAcceptedHaircut();
			lRowData[SETTLEMENT_EXCELL_SHEET2_FACTOREDAMOUT] = lFuUnitBean.getFactoredAmount();
			lRowData[SETTLEMENT_EXCELL_SHEET2_BUYERL1INST] = lFuUnitBean.getPurchaserLeg1Interest();
			lRowData[SETTLEMENT_EXCELL_SHEET2_SELLERL1INST] = lFuUnitBean.getSupplierLeg1Interest();
			lRowData[SETTLEMENT_EXCELL_SHEET2_BUYERL2INST] = lFuUnitBean.getPurchaserLeg2Interest();
			lRowData[SETTLEMENT_EXCELL_SHEET2_CHARGES] = lFuUnitBean.getTransactionCharge(lFuUnitBean.getChargeBearerEntityCode());
			lRowData[SETTLEMENT_EXCELL_SHEET2_FINANCIER] = lFuUnitBean.getFinancier();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTINGENTITY] = lFuUnitBean.getAcceptingEntity();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTINGLOGIN] = lFuUnitBean.getAcceptingLoginId();
			lRowData[SETTLEMENT_EXCELL_SHEET2_ACCEPTTIME] = lFuUnitBean.getAcceptDateTime();
			lRowData[SETTLEMENT_EXCELL_SHEET2_LIMITUTILIZED] = lFuUnitBean.getLimitUtilized();
			lRowData[SETTLEMENT_EXCELL_SHEET2_BUYSELSLIMITUTILIZED] = lFuUnitBean.getPurSupLimitUtilized();
         	lDataHash.put(new Long(lCount),lRowData );
         	lCount++;
		}
		Collections.sort(lFuids);
     	pSheet3Data.put("fuids", lFuids);
		return lDataHash;
	}
	
    private  HashMap<Long, Object[]> getPayFileContent(Connection pConnection, Date pSettlementDate, List<String> lBankCodes) throws Exception{
    	try{
    		List<IObligation> lObligationListForPayFile = getObligationListForFile(pConnection, null, pSettlementDate, false);
    		String lContent = getFileContentAsString(null, lObligationListForPayFile);
        	int[] lDataFieldWidths = new int[]{	2,9,2,3,10,1,2,2,40,16,20,13,13,10,10,1,2,11,35,11,18,3,27,3,15,20,7};
            List<String> lRows = new ArrayList<>();
            HashMap<Long, Object[]> lDataHash = new HashMap<>();
            BufferedReader lDataBufferedReader = null;
            lDataBufferedReader = new BufferedReader(new StringReader(lContent));
        	String lRow = null;
        	String lBankCode = null;
    		while ((lRow = lDataBufferedReader.readLine()) != null) {
    			lRows.add(lRow);
    		}
            for(int lPtr2=0; lPtr2 < lRows.size(); lPtr2++){
            	String lString = lRows.get(lPtr2);
                List<String> lResult  = new ArrayList<String>();
                String lTmp = "";
                int lIndex = 0;
                for(int lPtr=0; lPtr < lDataFieldWidths.length; lPtr++){
                	try{
                    	lTmp = lString.substring(lIndex, lIndex+lDataFieldWidths[lPtr]);
                    	lIndex += lDataFieldWidths[lPtr];
                    	lResult.add(lTmp);
                	}catch(Exception lEx){
                		System.out.println("Error : lPtr2=" + lPtr2 + " : lPtr="+lPtr+ " : lIndex="+lIndex + "lDataFieldWidths=" +lDataFieldWidths[lPtr]);
                	}
                }
                Object[] lRowData = null;
                for(int lPtr=0; lPtr < lResult.size(); lPtr++){
                	lBankCode = lResult.get(17).substring( 0 , 4 );
                	if (!lBankCodes.contains(lBankCode)){
                    	lBankCodes.add(lBankCode);
                	}
                	lRowData = lResult.toArray();
                	lDataHash.put(new Long(lPtr2), lRowData);
                }
            }
    		return lDataHash;
    	}catch(Exception e){
    		logger.info("Error in getPayFileContent : "+e.getMessage());
    	}
		return null;
	
    }
    
    private Map<Long, Object[]> getBankCodeAndNames(List<String> lBankCodes) {
    	String lBankBranch = null;
    	HashMap<Long, Object[]> lDataHash = new HashMap<>();
    	int lCount = 0;
    	for (String lCode : lBankCodes){
    		lBankBranch = TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCode);
    		if(!CommonUtilities.hasValue(lBankBranch)){
                logger.info( "Invalid Bank Code "+lCode);
    		}else{
    			lDataHash.put(new Long(lCount) , new Object[]{lCode+" - "+lBankBranch});
    			lCount++;
    		}
    	}
		return lDataHash;
	}
    
	@Override
	public String getFileName(PaymentFileBean pPaymentFileBean) throws CommonBusinessException {
		String lFileName = null;
		Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
		// TODO: : TRA-Bank Short code-Uploading user name-DDMMYYYY-Sequence
		// number-INP.csv
        lFileName = "ACH-TR-ICIC-ICICH2H" + "103355" + "-" + new SimpleDateFormat("ddMMyyyy").format(lCurrentTime) + "-" + 
                StringUtils.leftPad(pPaymentFileBean.getId().toString(), 6, '0') + "-INP.txt";
		return lFileName;
	}

	@Override
	public void updateObligation(Connection pConnection, IObligation pObligationBean, PaymentFileBean pPaymentFileBean, Map<String, FacilitatorEntityMappingBean> pFacilitatorMap, Map<Long, CompanyBankDetailBean> pDesignatedBankMap, MemoryTable pMemoryTable, CompanyBankDetailBean pCompanyBankDetailBean) throws Exception {
		AppEntityBean lAppEntityBean = null;
		IObligation lObligationBean = pObligationBean;
		Long lCdId = null;

		if (AppConstants.DOMAIN_PLATFORM.equals(lObligationBean.getTxnEntity())) {
			lCdId = new Long(0);
		} else {
			lAppEntityBean = (AppEntityBean) pMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { lObligationBean.getTxnEntity() });
			lCdId = lAppEntityBean.getCdId();
		}

		CompanyBankDetailBean lDesignatedBank = null;
		// find the company details bean and check whether the settlementwise
		if (lObligationBean.getSettlementCLId() != null && pCompanyBankDetailBean != null) {
			lDesignatedBank = pCompanyBankDetailBean;
		} else {
			lDesignatedBank = pDesignatedBankMap.get(lCdId);
		}
		if (lDesignatedBank == null || lDesignatedBank.getId() == null)
			throw new CommonBusinessException("Designated Bank for " + lObligationBean.getTxnEntity() + " not found");
		lObligationBean.setPayDetail1(lDesignatedBank.getAccNo());
		lObligationBean.setPayDetail2(lDesignatedBank.getIfsc());
		if (lAppEntityBean != null) {
			lObligationBean.setPayDetail3(lAppEntityBean.getName());
		} else {
			lObligationBean.setPayDetail3(lDesignatedBank.getFirstName());
		}
		if (lAppEntityBean != null) {
			if (lAppEntityBean.isPurchaser() || lAppEntityBean.isFinancier()) {
				Date lExpiry = null;
				String lKey = lObligationBean.getTxnEntity() + CommonConstants.KEY_SEPARATOR + lDesignatedBank.getId();
				FacilitatorEntityMappingBean lFEMBean = pFacilitatorMap.get(lKey);
				String lMappingCode = (lFEMBean != null) ? lFEMBean.getMappingCode() : "";
				if (StringUtils.isBlank(lMappingCode))
					throw new CommonBusinessException("Mapping code for " + lObligationBean.getTxnEntity() + " not found.");
				lObligationBean.setPayDetail4(lMappingCode);
				lExpiry = pFacilitatorMap.get(lKey).getExpiry();
				if (lExpiry != null && lExpiry.before(lObligationBean.getDate())) {
					throw new CommonBusinessException("Mapping code for " + lObligationBean.getTxnEntity() + " is  expired.");
				}
			}
		}
		lObligationBean.setStatus(ObligationBean.Status.Sent);
		lObligationBean.setPfId(pPaymentFileBean.getId());

		lObligationBean.setRecordUpdateTime(new Timestamp(System.currentTimeMillis()));
		if (lObligationBean.isParentObligation()) {
			obligationDAO.update(pConnection, (ObligationBean) lObligationBean, ObligationBean.FIELDGROUP_GENERATE);
		} else {
			obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationBean.FIELDGROUP_GENERATE);
		}
	}

	@Override
	public void tempCreateFile(List<IObligation> pListObligation, PaymentFileBean pPaymentFileBean) {
		if (pListObligation != null) {
			Long lPFId = pPaymentFileBean.getId();
			BigDecimal lTotal = new BigDecimal(0);
			StringBuilder lFileContent = new StringBuilder();
			int[] lHeaderFieldWidths = new int[] { 2, 7, 40, 14, 9, 11, 13, 3, 13, 13, 8, 10, 10, 3, 18, 18, 11, 35, 9, 2, 57 };
			int[] lDataFieldWidths = new int[] { 2, 9, 2, 3, 10, 1, 2, 2, 40, 16, 20, 13, 13, 10, 10, 1, 2, 11, 35, 11, 18, 3, 27, 3, 15, 20, 7 };
			String lTemp = null, lPartNo = null;
			StringBuilder lHeader = new StringBuilder();
			String lAppendChar = " ";
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
			long lAmount = 0,  lTotalDebitAmount = 0, lTotalCreditAmount = 0;
			int lPayRefPtr = 1;
			for (IObligation lBean : pListObligation) {
				lPartNo = CommonUtilities.appendChars(3, lBean.getPartNumber().toString(), "0", false);
				for (int lPtr = 0; lPtr < lDataFieldWidths.length; lPtr++) {
					lTemp = "";
					lAppendChar = " ";
					if (lPtr == 0) {
						lAmount = lBean.getAmount().multiply(AppConstants.HUNDRED).longValue();
						if (ObligationBean.TxnType.Debit.equals(lBean.getTxnType())) {
							lTemp = "12";// Record Identifier
							lTotalDebitAmount += lAmount;
						} else if (ObligationBean.TxnType.Credit.equals(lBean.getTxnType())) {
							lTemp = "11";// Record Identifier
							lTotalCreditAmount += lAmount;
						}
					} else if (lPtr == 2) {
						// Destination Account Type
						// As provided by Bank - Needs to be as per
						// NECS(10/11/12/13/29/30/31) or blank.

					} else if (lPtr == 9) {
						lTemp = lBean.getFuId().toString() + lPartNo; // Group
																		// Ref -
																		// 16
					} else if (lPtr == 10) {
						lTemp = lBean.getId().toString() + lPartNo;// Narration
																		// - 20
																		// =
																		// 5+13+3
					} else if (lPtr == 12) {
						lTemp = lBean.getAmount().multiply(new BigDecimal(100)).longValue() + "";
						lAppendChar = "0";
	            	}
                	else if(lPtr==13) {
						lTemp = "PAYREFNO"+(lPayRefPtr++); // paymentref no
                	}
                	else if(lPtr==15) {
						lTemp = "1"; // SUCCESS
                	}
                	else if(lPtr==17) {
						lTemp = lBean.getPayDetail2();
                	}
                	else if(lPtr==18) {
						lTemp = lBean.getPayDetail1();
                	}
                	else if(lPtr==21){
						lTemp = getEntityType(lMemoryTable, lBean.getTxnEntity());
                	}
                	else if(lPtr==22){
						lTemp = lBean.getId() + lPartNo; // 27
					}
					lTemp = CommonUtilities.appendChars(lDataFieldWidths[lPtr], lTemp, lAppendChar, false);
					lFileContent.append(lTemp);
				}
				lTotal = lTotal.add(lBean.getAmount());
				lFileContent.append("\n");
			}
			for (int lPtr = 0; lPtr < lHeaderFieldWidths.length; lPtr++) {
				lTemp = "";
				lAppendChar = " ";
            	if(lPtr==0) lTemp="TR";
				if (lPtr == 9) {
            		lTemp=new BigDecimal(lTotalCreditAmount).multiply(new BigDecimal(100)).longValue()+""; //Total credit Amount
					lAppendChar = "0";
            	}
            	else if(lPtr==6) {
					lTemp = new BigDecimal(lTotalDebitAmount).multiply(new BigDecimal(100)).longValue() + "";
					lAppendChar = "0";
            	}
            	else if(lPtr==15) {
					// The User reference Field value should be in this format
					// RXIL<Date in DDMMYYYY><6 digit seq number>
					// RXIL05122016000001
					// SimpleDateFormat lSimpleDateFormat = new
					// SimpleDateFormat("ddMMyyyy");
					// lTemp = "RXIL";
					// lTemp +=
					// lSimpleDateFormat.format(pListObligation.get(0).getDate());//Settlement
					// Date (DDMMYYYY)
					// lTemp += StringUtils.leftPad(String.valueOf(pPFId), 6,
					// "0"); // PFId
					lTemp = StringUtils.leftPad(String.valueOf(lPFId), 18, "0"); // PFId
        		}
            	else if(lPtr==18) {
					lTemp = pListObligation.size() + ""; // RecordCount
					lAppendChar = "0";
				}
				lTemp = CommonUtilities.appendChars(lHeaderFieldWidths[lPtr], lTemp, lAppendChar, false);
				lHeader.append(lTemp);
			}
			logger.debug("*********************************");
			logger.debug("CREDIT NPCI RETURN FILE");
			logger.debug(lHeader.toString() + "\n" + lFileContent.toString());
			logger.debug("*********************************");
		}

	}

	@Override
	public PaymentFileBean getFileContents(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
		PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(pConnection, pPaymentFileBean);
		if (lPaymentFileBean == null)
			throw new CommonBusinessException("Invalid File");
		List<IObligation> lObligationList = getObligationListForFile(pConnection, lPaymentFileBean, null, false);
		if ((lObligationList == null) || (lObligationList.size() == 0))
			throw new CommonBusinessException("No obligations");

		lPaymentFileBean.setContents(getFileContentAsString(lPaymentFileBean, lObligationList));

		return lPaymentFileBean;
	}

	@Override
	public String getFileContentAsString(PaymentFileBean pPaymentFileBean, List<IObligation> pObligationList) {
		StringBuilder lContents = new StringBuilder();
		SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("ddMMyyyy");
		char lZero = '0';
		CompanyBankDetailBean lTredsBankAccount = TredsHelper.getInstance().getTredsAccount();
		String lUserName = lTredsBankAccount.getFirstName();
		String lSponsorBankIfsc = lTredsBankAccount.getIfsc();
		String lUserNumber = lTredsBankAccount.getLastName();
		String lTredsAccountNo = lTredsBankAccount.getAccNo();
        String lPartStr = null;
		long lTotalDebitAmount = 0, lCreditAmount = 0;
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
		// int[] lDataFieldWidths = new int[]{
		// 2,9,2,3,10,1,2,2,40,16,20,13,13,10,10,1,2,11,35,11,18,3,27,3,15,20,7};

		for (IObligation lObligationBean : pObligationList) {
			long lAmount = lObligationBean.getAmount().multiply(AppConstants.HUNDRED).longValue();
            lPartStr = "";
			lContents.append(RECORD_SEPERATOR);
			if (ObligationBean.TxnType.Debit.equals(lObligationBean.getTxnType())) {
				lTotalDebitAmount += lAmount;
				lContents.append("12");// Record Identifier
			} else if (ObligationBean.TxnType.Credit.equals(lObligationBean.getTxnType())) {
				lCreditAmount += lAmount;
				lContents.append("11");// Record Identifier
			}
			lContents.append(fixedLengthString(null, 9));// Control
			lContents.append("  ");// Destination Account Type
			lContents.append(fixedLengthString(null, 3));// Ledger Folio Number
            lContents.append(fixedLengthString(null, 10));//Reversal ACH Item Seq No - generated by NPCI in  RES
			lContents.append(fixedLengthString(null, 1));// Reversal Transaction
            lContents.append(fixedLengthString(null, 2));//Reversal Transaction Reason Code 
			lContents.append(fixedLengthString(null, 2));// Control
			lContents.append(fixedLengthString(lObligationBean.getPayDetail3(), 40));// Beneficiary
    		lPartStr=CommonUtilities.appendChars(3,lObligationBean.getPartNumber().toString(),"0",false);
            lContents.append(fixedLengthString(String.valueOf(lObligationBean.getFuId().longValue())+lPartStr, 16));//Transaction group reference - send Factoring unit id (Group reference should be same for the set of credit and debit transactions)
			//change for DR file
            lContents.append(fixedLengthString(String.valueOf(lObligationBean.getId().longValue())+lPartStr, 20));//Narration
			lContents.append(fixedLengthString(null, 13));// Reversal Credit
															// Amount
			lContents.append(StringUtils.leftPad(String.valueOf(lAmount), 13, lZero));// Amount
			lContents.append(fixedLengthString(null, 10));// Reserved (ACH Item
															// Seq No.)
			lContents.append(fixedLengthString(null, 10));// Reserved (Checksum)
			lContents.append(fixedLengthString(null, 1));// Reserved (Flag for
															// success / return)
			lContents.append(fixedLengthString(null, 2));// Reserved (Reason
															// Code)
			lContents.append(fixedLengthString(lObligationBean.getPayDetail2(), 11));// Destination
																						// Bank
																						// IFSC
																						// /
																						// MICR
																						// /
																						// IIN
			lContents.append(fixedLengthString(lObligationBean.getPayDetail1(), 35));// Beneficiary's
																						// Bank
																						// Account
																						// number
			lContents.append(fixedLengthString(lSponsorBankIfsc, 11));// Sponsor
																		// Bank
																		// IFSC
																		// /
																		// MICR
																		// / IIN
			lContents.append(fixedLengthString(lUserNumber, 18));// User Number
			lContents.append(fixedLengthString(getEntityType(lMemoryTable, lObligationBean.getTxnEntity()), 3));// Entity
																												// Type
																												// -
																												// financier(FIN)/seller(SEL)/buyer(BUY)/TReDS
																												// charges(TRE)
            lContents.append(fixedLengthString(lObligationBean.getId()+lPartStr, 27));//Transaction Reference
																						// Reference
			lContents.append(ENTITYTYPE_PLATFORM);// Product Type
			// 15-aadhar,20-umrn,7-filler
			// 20-umrn,22-filler
			lContents.append(fixedLengthString(null, 15));// Beneficiary Aadhaar
															// Number
			if (ObligationBean.TxnType.Debit.equals(lObligationBean.getTxnType())) {
				lContents.append(fixedLengthString(lObligationBean.getPayDetail4(), 20));// UMRN
			} else if (ObligationBean.TxnType.Credit.equals(lObligationBean.getTxnType())) {
				lContents.append(fixedLengthString(null, 20));// UMRN
			}
			lContents.append(fixedLengthString(null, 7));// Filler
		}
		lContents.append("\n"); // New Line at the end of file
		StringBuilder lHeader = new StringBuilder();
		lHeader.append("TR");// ACH transaction code
		lHeader.append(fixedLengthString(null, 7));// Control
		lHeader.append(fixedLengthString(lUserName, 40));// User Name
		lHeader.append(fixedLengthString(null, 14));// Control
		lHeader.append(fixedLengthString(null, 9));// ACH File Number
		lHeader.append(fixedLengthString(null, 11));// Control
		lHeader.append(StringUtils.leftPad(String.valueOf(lTotalDebitAmount), 13, lZero));// Total
																							// Debit
																							// Amount
																							// in
																							// paise
		lHeader.append(fixedLengthString(null, 3));// Ledger Folio Number
		lHeader.append("0000000000000");// User Defined limit for individual
										// items
		lHeader.append(StringUtils.leftPad(String.valueOf(lCreditAmount), 13, lZero));// Total
																						// Amount
																						// in
																						// paise
																						// (Balancing
																						// Amount)
		lHeader.append(lSimpleDateFormat.format(pObligationList.get(0).getDate()));// Settlement
																					// Date
																					// (DDMMYYYY)
		lHeader.append(fixedLengthString(null, 10));// Reserved (kept blank by
													// user)
		lHeader.append(fixedLengthString(null, 10));// Reserved (kept blank by
													// user)
		lHeader.append(fixedLengthString(null, 3));// Filler
		lHeader.append(fixedLengthString(lUserNumber, 18));// User Number
		if(pPaymentFileBean==null){
			lHeader.append(fixedLengthString(null, 18));// User Reference
		}else{
			lHeader.append(fixedLengthString(pPaymentFileBean.getId().toString(), 18));// User Reference
		}
		lHeader.append(fixedLengthString(lSponsorBankIfsc, 11));// Sponsor Bank
																// IFSC / MICR /
																// IIN
		lHeader.append(fixedLengthString(lTredsAccountNo, 35));// User's Bank
																// Account
																// Number
		lHeader.append(StringUtils.leftPad(String.valueOf(pObligationList.size()), 9, lZero));// Total
																								// Items
		lHeader.append(fixedLengthString(null, 2));// Settlement Cycle (Kept
													// blank by User)
		lHeader.append(fixedLengthString(null, 57));// Filler
		lHeader.append(lContents);
		return lHeader.toString();
	}

	private String getEntityType(MemoryTable pMemoryTable, String pTxnEntity) {
		String lEntityType = "";
		if (!entityTypes.containsKey(pTxnEntity)) {
			AppEntityBean lAppEntityBean = null;
			try {
				lAppEntityBean = (AppEntityBean) pMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[] { pTxnEntity });
			} catch (MemoryDBException e) {
				e.printStackTrace();
			}
			// financier(FIN)/seller(SEL)/buyer(BUY)/TReDS charges(TRE)
			if (lAppEntityBean != null) {
				if (lAppEntityBean.isFinancier()) {
					lEntityType = ENTITYTYPE_FINANCIEAR;
				} else if (lAppEntityBean.isPurchaser()) {
					lEntityType = ENTITYTYPE_PURCHASER;
				} else if (lAppEntityBean.isSupplier()) {
					lEntityType = ENTITYTYPE_SUPPLIER;
				} else if (lAppEntityBean.isPlatform()) {
					lEntityType = ENTITYTYPE_PLATFORM;
				}
			}
			entityTypes.put(pTxnEntity, lEntityType);
		} else {
			lEntityType = entityTypes.get(pTxnEntity);
		}
		return lEntityType;
	}

	@Override
	public void uploadReturnFile(FileUploadBean pFileUploadBean) throws Exception {
		pFileUploadBean.getExecutionContext().setAutoCommit(false);
		String lSettlorRemarks = null;
		try {
			PayfileType lPayfileType = PayfileType.Full_File;
			String lPFId = pFileUploadBean.getFormParameters().get("pfId");
			PaymentFileBean lFilterBean = new PaymentFileBean();
			lFilterBean.setId(Long.valueOf(lPFId));
			Connection lConnection = pFileUploadBean.getExecutionContext().getConnection();
			PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(lConnection, lFilterBean);
			if (lPaymentFileBean == null)
				throw new CommonBusinessException("Invalid File");
			// allow uploading of file till it is marked completely processed.
			if (StringUtils.startsWith(pFileUploadBean.getFileName(), NPCI_FULLFILE_PREFIX)) {
				lPayfileType = PayfileType.Full_File;
			} else if (StringUtils.startsWith(pFileUploadBean.getFileName(), NPCI_INTERIMDEBITFILE_PREFIX)) {
				lPayfileType = PayfileType.Interim_Debit_File;
            }
            else if (StringUtils.startsWith(pFileUploadBean.getFileName(),NPCI_INTERIMCREDITFILE_PREFIX) ){
				lPayfileType = PayfileType.Interim_Credit_File;
			} else {
				throw new CommonBusinessException(" Please upload a valid file. ");
			}
			if ( Status.Interim_File_Uploaded.equals(lPaymentFileBean.getStatus())
				&& PayfileType.Full_File.equals(lPayfileType)) {
				throw new CommonBusinessException("Please process the Interim File before uploading the Full File.");
			}
			lPaymentFileBean.setPayfileType(lPayfileType);
			validatePaymentFileStatus(lPayfileType, lPaymentFileBean);

			List<IObligation> lObligationList = getObligationListForFile(lConnection, lPaymentFileBean, null, (PaymentFileBean.PayfileType.Interim_Credit_File.equals(lPaymentFileBean.getPayfileType()) ? false : true));
			Map<String, IObligation> lMap = new HashMap<String, IObligation>();
			Map<Long, IObligation> lParentMap = new HashMap<Long, IObligation>();
			Map<String, List<IObligation>> lFuMap = new HashMap<String, List<IObligation>>();
			List<IObligation> lObligations = null;
			String lSplitkey = null;
			for (IObligation lObligationBean : lObligationList) {
				lMap.put(lObligationBean.getUniqueObligationKey(), lObligationBean);
				lSplitkey = lObligationBean.getFuId() + CommonConstants.KEY_SEPARATOR + lObligationBean.getPartNumber();
				if (!lFuMap.containsKey(lSplitkey)) {
					lObligations = new ArrayList<IObligation>();
					lFuMap.put(lSplitkey, lObligations);
				} else {
					lObligations = lFuMap.get(lSplitkey);
				}
				lObligations.add(lObligationBean);
				if (!lParentMap.containsKey(lObligationBean.getId())) {
					lParentMap.put(lObligationBean.getId(), lObligationBean.getParentObligation());
				}
				// System.out.println("KEY :
				// "+lObligationBean.getUniqueObligationKey());
				// System.out.println("Before : Id = "+lObligationBean.getId()+"
				// , REC VER : " + lObligationBean.getRecordVersion());
			}

			paymentSettlor = new NPCIPaymentSettlor(lPaymentFileBean);

			int lFileRecordCount = updateObligationSplits(lConnection, lPaymentFileBean, pFileUploadBean, lMap, lPayfileType, lFuMap);

			if (lFileRecordCount > 0) {
				logger.debug("updating paymentFile ");
				// here we will always update that the return file is uploaded
				// (whether interim file or actual return file)
				// the change of status to complete will be done in the process
				// click
				if (PayfileType.Full_File.equals(lPayfileType)) {
					lPaymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Uploaded);
				} else {
					lPaymentFileBean.setStatus(PaymentFileBean.Status.Interim_File_Uploaded);
				}
			}
			lPaymentFileBean.setReturnUploadedByAuId(pFileUploadBean.getAuId());
			lPaymentFileBean.setReturnUploadedTime(new Timestamp(System.currentTimeMillis()));
			paymentFileDAO.update(lConnection, lPaymentFileBean, PaymentFileBean.FIELDGROUP_RETURN);
			pFileUploadBean.getExecutionContext().commit();
		} catch (Exception lException) {
			logger.debug("Error in uploadReturnFile : " + lException.getMessage());
			pFileUploadBean.setSuccessCount(new Long(0));
			pFileUploadBean.getExecutionContext().rollback();
			if(paymentSettlor!=null) {
			lSettlorRemarks = paymentSettlor.getRemarks();
			}
			if (StringUtils.isNotEmpty(lSettlorRemarks)) {
				throw new CommonBusinessException(lException.getMessage() + " " + lSettlorRemarks);
			}
			throw lException;
		}
	}

    private int updateObligationSplits(Connection pConnection, PaymentFileBean pPaymentFileBean, 
            FileUploadBean pFileUploadBean, Map<String, IObligation> pObligationMap, PayfileType pPayfileType, Map<String, List<IObligation>> pFuMap) throws Exception {
		List<String> lRecords = (List<String>) pFileUploadBean.getContext();
		String lSplitKey = null;
		int lCount = lRecords.size();
		int[] lHeaderFieldWidths = new int[] { 2, 7, 40, 14, 9, 11, 13, 3, 13, 13, 8, 10, 10, 3, 18, 18, 11, 35, 9, 2, 57 };
		int[] lDataFieldWidths = new int[] { 2, 9, 2, 3, 10, 1, 2, 2, 40, 16, 20, 13, 13, 10, 10, 1, 2, 11, 35, 11, 18, 3, 27, 3, 20, 22 };
		String[] lHeader = CommonUtilities.splitString(lRecords.get(0), lHeaderFieldWidths);
		// check header
		Long lPfId = Long.valueOf(lHeader[15].trim());
		int lRecordCount = Integer.parseInt(lHeader[18]);
		int lActualRecordCount = 0;
		if (!pPaymentFileBean.getId().equals(lPfId))
            throw new CommonBusinessException("Mismatch in payment file id. Selected : " 
                + pPaymentFileBean.getId() + ", Header Record : " + lPfId);
		if (lHeader == null || lHeader.length != lHeaderFieldWidths.length)
			throw new CommonBusinessException("Record length mismatch in header record.");
		//
		List<IObligation> lObligations = new ArrayList<IObligation>();
		IObligation lObligationBean = null;
		Map<String,String> lDbFailed = new HashMap<String,String>(); // Key=FuId^PartNo, Value=Remarks
		//
		for (int lPtr = 1; lPtr < lCount; lPtr++) {
			String[] lRecord = CommonUtilities.splitString(lRecords.get(lPtr), lDataFieldWidths);
			if (lRecord == null || lRecord.length != lDataFieldWidths.length)
				throw new CommonBusinessException("Record length mismatch in record " + (lPtr));
			String lObliUniqueKey = lRecord[22].trim();
			//change for DR file
            if(PayfileType.Interim_Debit_File.equals(pPayfileType)){
                lObliUniqueKey = lRecord[10].trim();
                logger.info("DR FILE - KEY : "+lObliUniqueKey);
            }else if(PayfileType.Interim_Credit_File.equals(pPayfileType)){
                lObliUniqueKey = lRecord[10].trim();
                logger.info("CR FILE - KEY : "+lObliUniqueKey);
            }
			lObligationBean = pObligationMap.remove(lObliUniqueKey);
			if (lObligationBean == null) {
				// When uploading Full File - remove all the records already
				// processed through interim file
				if (PayfileType.Full_File.equals(pPaymentFileBean.getPayfileType()) && 
					Status.Interim_File_Processed.equals(pPaymentFileBean.getStatus())) {
					lActualRecordCount++;
					continue;
				}
				logger.info("Obligation record not found for id " + lObliUniqueKey + " for record " + (lPtr + 1));
				lActualRecordCount++;
				continue;
			}
			String lAccount = lRecord[18].trim();
			String lIFSC = lRecord[17].trim();
			long lAmount = Long.parseLong(lRecord[12]);
			BigDecimal lDecimalAmount = new BigDecimal(lAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
			String lTxnRefNo = lRecord[13];
			String lTxnFlag = lRecord[15];
			int lIntTxnFlag = Integer.parseInt(lTxnFlag);
			String lReasonCode = lRecord[16];

			if (!lObligationBean.getPayDetail1().equals(lAccount))
				throw new CommonBusinessException("Account No mismatch in record " + (lPtr + 1));
			if (!lObligationBean.getPayDetail2().equals(lIFSC))
				throw new CommonBusinessException("IFSC mismatch in record " + (lPtr + 1));
            if (lObligationBean.getAmount()==null || 
            		lDecimalAmount==null || 
            		(lObligationBean.getAmount().compareTo(lDecimalAmount) != 0 ))
				throw new CommonBusinessException("Amount mismatch in record " + (lPtr + 1));
			if (lIntTxnFlag == 1) {
				// check if reversal transaction status exists
				// if yes and 1 then txn was successful but returned due to
				// sibling rejection
				// if other than 1 then txn status = success but return failed.
				// PaymentSettlor object will handle such cases by generating
				// NEFT reversal
				if (StringUtils.isBlank(lRecord[5])) {
					lObligationBean.setStatus(ObligationBean.Status.Success);
					lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
				} else {
					int lIntRevTxnFlag = Integer.parseInt(lRecord[5]);
					if (lIntRevTxnFlag == 1) { // reversal was successful
						lObligationBean.setStatus(ObligationBean.Status.Returned);
					} else {
						lObligationBean.setStatus(ObligationBean.Status.Success);
						lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
						lObligationBean.setRespRemarks("Return failed. Flag:" + lIntRevTxnFlag + ". Reason Code:" + lRecord[6]);
					}
				}
				lObligationBean.setSettledAmount(lDecimalAmount);
			} else if ((lIntTxnFlag == 7) && (lObligationBean.getTxnType() == ObligationBean.TxnType.Credit)) {
				lObligationBean.setStatus(ObligationBean.Status.Success);
				lObligationBean.setSettledAmount(lDecimalAmount);
				lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
			} else if (lIntTxnFlag == 9) {
				lObligationBean.setStatus(ObligationBean.Status.Cancelled);
				lObligationBean.setSettledAmount(BigDecimal.ZERO);
			} else {
				lObligationBean.setStatus(ObligationBean.Status.Failed);
				lObligationBean.setSettledAmount(BigDecimal.ZERO);
			}
            if ((lObligationBean.getStatus() == ObligationBean.Status.Failed) || 
            		(lObligationBean.getStatus() == ObligationBean.Status.Prov_Success) || 
            		(lObligationBean.getStatus() == ObligationBean.Status.Cancelled)) {
				RefCodeValuesBean lRefCodeValuesBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lRecord[15], REFCODE_NPCITXNFLAG);
				StringBuilder lReason = new StringBuilder();
				lReason.append(lTxnFlag);
				if (lRefCodeValuesBean != null)
					lReason.append(":").append(lRefCodeValuesBean.getDesc());
				if (StringUtils.isNotBlank(lReasonCode)) {
					lRefCodeValuesBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lReasonCode, REFCODE_NPCIREASONCODE);
					lReason.append(".").append(lReasonCode);
					if (lRefCodeValuesBean != null)
						lReason.append(":").append(lRefCodeValuesBean.getDesc());
				}
				lObligationBean.setRespRemarks(lReason.toString());
			}
			lObligationBean.setPaymentRefNo(lTxnRefNo);
			lObligationBean.setRecordUpdator(pFileUploadBean.getAuId());
			lObligations.add(lObligationBean);
			if ((PayfileType.Full_File.equals(pPayfileType) || PayfileType.Interim_Debit_File.equals(pPayfileType) )
					&& ObligationBean.Status.Failed.equals(lObligationBean.getStatus())
					&& TxnType.Debit.equals(lObligationBean.getTxnType())) {
				lSplitKey = lObligationBean.getFuId() + CommonConstants.KEY_SEPARATOR + lObligationBean.getPartNumber();
				lDbFailed.put(lSplitKey, lObligationBean.getRespRemarks());
			}
		}
		if (PayfileType.Full_File.equals(pPayfileType) || PayfileType.Interim_Debit_File.equals(pPayfileType)) {
			for (int lPtr = 0; lPtr < lObligations.size(); lPtr++) {
				lObligationBean = lObligations.get(lPtr);
				lSplitKey = lObligationBean.getFuId() + CommonConstants.KEY_SEPARATOR + lObligationBean.getPartNumber();
				if(lDbFailed.containsKey(lSplitKey) ){
					if (ObligationBean.Status.Failed.equals(lObligationBean.getStatus())){
						continue;
					}
					lObligationBean.setStatus(ObligationBean.Status.Failed);
					lObligationBean.setRespRemarks(lDbFailed.get(lSplitKey));
				}
			}
		}
		for (int lPtr = 0; lPtr < lObligations.size(); lPtr++) {
			lObligationBean = lObligations.get(lPtr);
			// When uploading interim file - skip all the success records coming
			// in the file, thus keeping them in Sent mode
			if (!PayfileType.Interim_Credit_File.equals(pPayfileType)) {
				// DEBIT OR FULL FILE
				//
				if (PayfileType.Interim_Debit_File.equals(pPayfileType) && 
					ObligationBean.Status.Success.equals(lObligationBean.getStatus())) {
					if(StringUtils.isNotEmpty(lObligationBean.getPaymentRefNo())) {
						try {
							obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationSplitsBean.FIELDGROUP_UPDATEPAYMENTREFNO);
						}catch(Exception lEx1) {
							logger.info("Error in Dr Success payment ref no update : "+ lEx1.getMessage());
						}
					}
					lActualRecordCount++;
					continue;
				}
				obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationBean.FIELDGROUP_RETURN);
				if (PayfileType.Interim_Debit_File.equals(pPayfileType)) {
					lSplitKey = lObligationBean.getFuId() + CommonConstants.KEY_SEPARATOR + lObligationBean.getPartNumber();
					List<IObligation> lList = pFuMap.get(lSplitKey);
					for (IObligation lObligationSplit : lList) {
						if (TxnType.Credit.equals(lObligationSplit.getTxnType())) {
							/*
							 * if (PayfileType.Interim_Debit_File.equals(
							 * pPayfileType)){
							 * lObligationSplit.setStatus(lObligationBean.
							 * getStatus()); //prov_success }else if
							 * (PayfileType.Full_File.equals(pPayfileType) ){
							 * lObligationSplit.setStatus(lObligationBean.
							 * getStatus()); } if (lObligationBean.getStatus()
							 * == ObligationBean.Status.Prov_Success &&
							 * lObligationBean.getTxnType() ==
							 * ObligationBean.TxnType.Debit){
							 * 
							 * }
							 */
							lObligationSplit.setStatus(lObligationBean.getStatus());
							lObligationSplit.setSettledAmount(BigDecimal.ZERO);
							lObligationSplit.setRespRemarks("DRFAIL: " + lObligationBean.getRespRemarks());
							obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationSplit, ObligationBean.FIELDGROUP_RETURN);
						}
					}
				}
			} else {
				// CREDIT FILE
				lSplitKey = lObligationBean.getFuId() + CommonConstants.KEY_SEPARATOR + lObligationBean.getPartNumber();
				List<IObligation> lList = pFuMap.get(lSplitKey);
				int lDebitFailCount = 0;
				//finding debit fail record if any
				for (IObligation lObligationSplit : lList) {
					if (TxnType.Debit.equals(lObligationSplit.getTxnType())) {
						if (ObligationBean.Status.Prov_Success.equals(lObligationSplit.getStatus()) 
						|| ObligationBean.Status.Failed.equals(lObligationSplit.getStatus()) 
						|| ObligationBean.Status.Cancelled.equals(lObligationSplit.getStatus()) 
						|| ObligationBean.Status.Returned.equals(lObligationSplit.getStatus())) {
							lDebitFailCount++;
						}
					}
				}
				if (ObligationBean.Status.Success.equals(lObligationBean.getStatus())) {
					if (lDebitFailCount > 0) {
						logger.info("missmatch in CR obligation " + lObligationBean.getId() + " DR Obligation already failed. ");
					}else {
						if(StringUtils.isNotEmpty(lObligationBean.getPaymentRefNo())) {
							try {
								obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationSplitsBean.FIELDGROUP_UPDATEPAYMENTREFNO);
							}catch(Exception lEx1) {
								logger.info("Error in Cr Success payment ref no update : "+ lEx1.getMessage());
							}
						}						
					}
				} else if (ObligationBean.Status.Prov_Success.equals(lObligationBean.getStatus()) 
					|| ObligationBean.Status.Failed.equals(lObligationBean.getStatus()) 
					|| ObligationBean.Status.Cancelled.equals(lObligationBean.getStatus()) 
					|| ObligationBean.Status.Returned.equals(lObligationBean.getStatus())) {
					if (lDebitFailCount > 0) {
						obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationBean.FIELDGROUP_REMARKS);
					} else {
						obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationBean, ObligationBean.FIELDGROUP_RETURN);
						for (IObligation lObligationSplit : lList) {
							if (TxnType.Debit.equals(lObligationSplit.getTxnType())) {
								lObligationSplit.setStatus(lObligationBean.getStatus());
								lObligationSplit.setSettledAmount(BigDecimal.ZERO);
								lObligationSplit.setRespRemarks("CRFAIL: " + lObligationBean.getRespRemarks());
								obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lObligationSplit, ObligationBean.FIELDGROUP_RETURN);
							}
						}
					}
				}
				lActualRecordCount++;
				continue;
			}

			// if Credit File
			// if Credit File Record is Success
			// Get Debit Record
			// if Debit SENT then skip
			// if Debit Failed then skip BUT ADD LOGS SINCE THIS IS A CONTITION
			// WHICH CANNOT ARISE.
			// if Credit File Record is Failed
			// Get Debit Record
			// if Debit SENT then Mark Debit Failed (with remarks as "CRFAIL : "
			// and Mark Credit Failed and put he remarks
			// if Debit Failed then just update Credit Remarks (since it must
			// already been marked as Failed)
			//
			// paymentSettlor.addObligation(lObligationBean);
			if (!newObligationList.contains(lObligationBean.getId())) {
				newObligationList.add(lObligationBean.getId());
			}
			lActualRecordCount++;
		}
		if (lActualRecordCount != lRecordCount)
			throw new CommonBusinessException("Record count mismatch. Actual : " + lActualRecordCount + " Header : " + lRecordCount);
		return lActualRecordCount;
	}

	private String fixedLengthString(String pString, int pLength) {
		if (pString == null)
			pString = "";
		if (pString.length() > pLength)
			pString = pString.substring(0, pLength);
		else
			pString = StringUtils.rightPad(pString, pLength, ' ');
		return pString;
	}

	@Override
	public List<IObligation> getObligationListForFile(Connection pConnection, PaymentFileBean pPaymentFileBean, Date pDate, boolean pOnlySentObligations) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		//
		lSql.append(" SELECT * FROM OBLIGATIONS ");
		lSql.append(" left  outer join OBLIGATIONSPLITS ");
		lSql.append(" on obid = obsobid");
		lSql.append(" WHERE OBRECORDVERSION > 0 ");
		lSql.append(" AND OBSRECORDVERSION > 0 ");
		if (pPaymentFileBean!=null){
			lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pPaymentFileBean.getDate()));
		}else if (pDate!=null){
			lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pDate));
		}
		
		if (pOnlySentObligations) {
			// lSql.append(" AND OBSTATUS =
			// ").append(lDbHelper.formatString(ObligationBean.Status.Sent.getCode()));
			lSql.append(" AND OBSSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Sent.getCode()));
		}
		if(pPaymentFileBean!=null){
			lSql.append(" AND OBPFID = ").append(pPaymentFileBean.getId());
		}
		lSql.append(" ORDER BY OBSFileSeqNo ");

		List<ObligationSettlementBankDetailsBean> lList = obliSettleBankDetailDAO.findListFromSql(pConnection, lSql.toString(), 0);
		List<IObligation> lObligationList = new ArrayList<IObligation>();
		if ((lList == null) || (lList.size() == 0))
			throw new CommonBusinessException("No obligations");
		HashMap<Long, IObligation> lParentHash = new HashMap<Long, IObligation>();
		for (int lPtr = 0; lPtr < lList.size(); lPtr++) {
			IObligation lBean = lList.get(lPtr).getActualObligationBean();
			if (!lParentHash.containsKey(lBean.getId())) {
				lParentHash.put(lBean.getId(), lList.get(lPtr).getObligationBean());
			}
			lBean.setParentObligation(lParentHash.get(lBean.getId()));
			lObligationList.add(lBean);
		}
		return lObligationList;
	}

	public void processUploadedReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
		if (PaymentFileBean.Status.Generated.equals(pPaymentFileBean.getStatus())) {
			throw new CommonBusinessException("Please select a valid file for processing.");
		} else if (PaymentFileBean.Status.Return_File_Processed.equals(pPaymentFileBean.getStatus())) {
			throw new CommonBusinessException("File already processed.");
		} else if (PayfileType.Interim_Debit_File.equals(pPaymentFileBean.getPayfileType()) && PaymentFileBean.Status.Interim_File_Processed.equals(pPaymentFileBean.getStatus())) {
			throw new CommonBusinessException("Interim already processed.");
		}
		paymentSettlor = new NPCIPaymentSettlor(pPaymentFileBean);
		paymentSettlor.process(pConnection);
		// if(CommonUtilities.hasValue(paymentSettlor.getRemarks()))
		// pFileUploadBean.setRemarks(paymentSettlor.getRemarks()); ASK SIR
		List<Long> lUnprocessedObIds = paymentSettlor.getObligationListReceivedForProcessing();
		if (lUnprocessedObIds != null) {
			emailGeneratorBO.sendLeg1StatusTransactionDetails(pConnection, pPaymentFileBean.getDate(), lUnprocessedObIds);
			emailGeneratorBO.sendLeg1StatusTransactionDetailsPurchaser(pConnection, pPaymentFileBean.getDate(), lUnprocessedObIds);
			emailGeneratorBO.sendLeg2StatusTransactionDetails(pConnection,pPaymentFileBean.getDate(), lUnprocessedObIds);
		}
		// pPaymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Processed);
		// add audit for who processed????? ASK SIR
		// pPaymentFileBean.setReturnUploadedByAuId(pFileUploadBean.getAuId());
		pPaymentFileBean.setReturnUploadedTime(new Timestamp(System.currentTimeMillis()));
		paymentFileDAO.update(pConnection, pPaymentFileBean, PaymentFileBean.FIELDGROUP_RETURN);
	}

	private void validatePaymentFileStatus(PayfileType pPayfileType, PaymentFileBean pPaymentFileBean) throws CommonBusinessException {
		boolean lIsInterimDebitFile = PayfileType.Interim_Debit_File.equals(pPayfileType);
		boolean lIsInterimCreditFile = PayfileType.Interim_Credit_File.equals(pPayfileType);
		if (Status.Generated.equals(pPaymentFileBean.getStatus())) {
			if (PayfileType.Interim_Credit_File.equals(pPayfileType)) {
				throw new CommonBusinessException(" CR file cannot be uploaded now.");
			}
		} else if (Status.Interim_File_Uploaded.equals(pPaymentFileBean.getStatus())) {
			Long lCount = checkForPendingModificationRequests(pPaymentFileBean.getId());
			if (lCount != null && lCount.longValue() > 0) {
				throw new CommonBusinessException(" Obligations Modified payment file cannot be uploaded.");
			}
    	}else if ( (lIsInterimDebitFile || lIsInterimCreditFile )&& 
    			PaymentFileBean.Status.Return_File_Uploaded.equals(pPaymentFileBean.getStatus())){
			throw new CommonBusinessException("Return file already uploaded. Cannot upload Interim file after return file upload.");
		} else if (PaymentFileBean.Status.Interim_File_Processed.equals(pPaymentFileBean.getStatus())) {
			if (lIsInterimDebitFile || lIsInterimCreditFile) {
				throw new CommonBusinessException("Interim file already processed.");
			} else {
				// do nothing
			}
		} else if (PaymentFileBean.Status.Return_File_Processed.equals(pPaymentFileBean.getStatus())) {
			throw new CommonBusinessException("Return file already processed.");
		}
	}

	private Long checkForPendingModificationRequests(Long pPfId) {
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT count(*) Count FROM ObligationsModiRequests ");
		lSql.append(" WHERE OMRRECORDVERSION >0 ");
		lSql.append(" AND OMRFUID IN ( SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBPFID = ").append(pPfId);
		lSql.append(" AND OMRSTATUS IN ( ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Created.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Sent.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Approved.getCode())).append(" ) ");
		lSql.append(" ) "); 
		logger.info(lSql.toString());
		try (Connection lConnection = lDBHelper.getConnection();
		 Statement lStatement = lConnection.createStatement();
		 ResultSet lResultSet = lStatement.executeQuery(lSql.toString());) {
			while (lResultSet.next()) {
				return lResultSet.getLong("Count");
			}
		} catch (Exception lEx) {
			logger.info("Error in checkForPendingModificationRequests : " + lEx.getMessage());
		}
		return null;
	}
    public static void main(String[] args){
    	testDrFileData();
    	//testTrFileData();
    }
    private static HashMap<Long, Object[]> testTrFileData(String lPaymentFile){
        int[] lDataFieldWidths = new int[]{	2,9,2,3,10,1,2,2,40,16,20,13,13,10,10,1,2,11,35,11,18,3,27,3,15,20,7};
        List<String> lRows = new ArrayList<>();
        HashMap<Long, Object[]> lDataHash = new HashMap<>();
        BufferedReader lDataBufferedReader = null;
        lDataBufferedReader = new BufferedReader(new StringReader(lPaymentFile));
        try {
        	String lRow = null;
			while ((lRow = lDataBufferedReader.readLine()) != null) {
				lRows.add(lRow);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for(int lPtr2=0; lPtr2 < lRows.size(); lPtr2++){
        	String lString = lRows.get(lPtr2);
            List<String> lResult  = new ArrayList<String>();
            String lTmp = "";
            int lIndex = 0;
            for(int lPtr=0; lPtr < lDataFieldWidths.length; lPtr++){
            	try{
                	lTmp = lString.substring(lIndex, lIndex+lDataFieldWidths[lPtr]);
                	lIndex += lDataFieldWidths[lPtr];
                	lResult.add(lTmp);
            	}catch(Exception lEx){
            		System.out.println("Error : lPtr2=" + lPtr2 + " : lPtr="+lPtr+ " : lIndex="+lIndex + "lDataFieldWidths=" +lDataFieldWidths[lPtr]);
            	}
            }
            Object[] lRowData = null;
            for(int lPtr=0; lPtr < lResult.size(); lPtr++){
            	lRowData = lResult.toArray();
            	lDataHash.put(new Long(lPtr2), lRowData);
            	System.out.println(lPtr+"|"+lResult.get(lPtr).length()+ "|"+ lResult.get(lPtr));
            }
            System.out.println("--------------------------------------------");
        }
		return lDataHash;
    }
    private static void testDrFileData(){
        int[] lDataFieldWidths = new int[]{	2,9,2,3,10,1,2,2,40,16,20,13,13,10,10,1,2,11,35,11,18,3,27,3,15,20,7};
        String[] lStrings = new String[] {"56 RIEL TREDS 000000000 000000000000000002940662000904201988633430364851450468 NACH00000000004933488 ICIC0000004000405113595 00000000107 67 00 Badve Engineering Limited 09042019 1183450000108001 000029406620088633430377782430630100SRCB0000070070100100001780 ICIC0000004NACH00000000004933DRBUY272089 TRE000000000000000SRCB0000000000595613"};
        for(int lPtr2=0; lPtr2 < lStrings.length; lPtr2++){
        	String lString = lStrings[lPtr2];
            List<String> lResult  = new ArrayList<String>();
            String lTmp = "";
            int lIndex = 0;
            for(int lPtr=0; lPtr < lDataFieldWidths.length; lPtr++){
            	try{
                	lTmp = lString.substring(lIndex, lIndex+lDataFieldWidths[lPtr]);
                	lIndex += lDataFieldWidths[lPtr];
                	lResult.add(lTmp);
            	}catch(Exception lEx){
            		System.out.println("Error : lPtr2=" + lPtr2 + " : lPtr="+lPtr+ " : lIndex="+lIndex + "lDataFieldWidths=" +lDataFieldWidths[lPtr]);
            	}
            }
            for(int lPtr=0; lPtr < lResult.size(); lPtr++){
            	System.out.println(lPtr+"|"+lResult.get(lPtr).length()+ "|"+ lResult.get(lPtr));
            }
            System.out.println("--------------------------------------------");
        }
    }
}
