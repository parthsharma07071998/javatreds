package com.xlx.treds.hostapi.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkWorkFlowBean;
import com.xlx.treds.hostapi.bean.AggregatorPurchaserMapHostApiBean;
import com.xlx.treds.hostapi.bean.AggregatorPurchaserMapResponseBean;
import com.xlx.treds.hostapi.bean.BillFactoringInstrumentHostApiBean;
import com.xlx.treds.hostapi.bean.BillFactoringUnitHostApiBean;
import com.xlx.treds.hostapi.bean.BillResponseBean;
import com.xlx.treds.hostapi.bean.BillsHostApiBean;
import com.xlx.treds.hostapi.bean.BillsRegistrationChargesHostApiBean;
import com.xlx.treds.hostapi.bean.BuyerSellerLinkBean;
import com.xlx.treds.hostapi.bean.FactoringUnitBidHostApiBean;
import com.xlx.treds.hostapi.bean.FactoringUnitHostApiBean;
import com.xlx.treds.hostapi.bean.FactoringUnitResponseBean;
import com.xlx.treds.hostapi.bean.FactoringUnitWatchHostApiBean;
import com.xlx.treds.hostapi.bean.InstrumentHostApiBean;
import com.xlx.treds.hostapi.bean.InstrumentResponseBean;
import com.xlx.treds.hostapi.bean.InstrumentWorkFlowHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationDetailsHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationExtensionHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationModificationDetailsHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationResponseBean;
import com.xlx.treds.hostapi.bean.ObligationSplitsHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierCapRateHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLimitUtilizationHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLimitUtilizationResponseBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLinkHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLinkResponseBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLinkWorkFlowHostApiBean;
import com.xlx.treds.hostapi.bean.UserHostApiBean;
import com.xlx.treds.hostapi.bean.UserHostApiMakerCheckerMap;
import com.xlx.treds.hostapi.bean.UserHostApiRmIdMapping;
import com.xlx.treds.hostapi.bean.UserResponseBean;
import com.xlx.treds.hostapi.bo.HostApiBo;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentWorkFlowBean;

public class HostApiDao {

	private static Logger logger = Logger.getLogger(HostApiDao.class);

	// private GenericDAO<BuyerSellerLinkBean> buyerSellerLinkDao;
	private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
	private GenericDAO<InstrumentBean> instrumentDao;
	// private GenericDAO<InstrumentHostApiBean> instrumentHostApiBeanDao;
	private GenericDAO<InstrumentWorkFlowBean> instrumentWorkFlowDao;
	private GenericDAO<PurchaserSupplierLinkWorkFlowBean> purchaserSupplierLinkWorkFlowDAO;

	public HostApiDao() {
		super();
		// buyerSellerLinkDao = new
		// GenericDAO<BuyerSellerLinkBean>(BuyerSellerLinkBean.class);
		purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
		purchaserSupplierLinkWorkFlowDAO = new GenericDAO<PurchaserSupplierLinkWorkFlowBean>(
				PurchaserSupplierLinkWorkFlowBean.class);
		instrumentDao = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		// instrumentHostApiBeanDao = new
		// GenericDAO<InstrumentHostApiBean>(InstrumentHostApiBean.class);
		instrumentWorkFlowDao = new GenericDAO<InstrumentWorkFlowBean>(InstrumentWorkFlowBean.class);

	}

	public List<PurchaserSupplierLinkBean> findListForMap(ExecutionContext pExecutionContext,
			Map<String, Object> inputMap) throws Exception {

		logger.info("inside dao");

		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT * FROM PurchaserSupplierLinks WHERE (PSLPurchaser = ")
				.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));
		lSql.append(" AND PSLSupplier = ").append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))))
				.append(")");
		lSql.append(" AND PSLStatus = ")
				.append(lDBHelper.formatString(PurchaserSupplierLinkBean.Status.Active.getCode()));
		List<PurchaserSupplierLinkBean> lList = purchaserSupplierLinkDAO
				.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
		if (lList != null) {
			for (PurchaserSupplierLinkBean lPSBean : lList) {
				lPSBean.populateNonDatabaseFields();
			}
		}
		return lList;
	}

	public List<InstrumentBean> getInstrumentDataDao(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside getInstrumentDataDao");
		logger.info(String.valueOf(inputMap.get("instrumentId")));
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT * FROM Instruments WHERE INPurchaser = ")
				.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));

		if (String.valueOf(inputMap.get("sellerCode")) != null
				&& !StringUtils.isEmpty(String.valueOf(inputMap.get("sellerCode")))) {
			lSql.append(" AND INSupplier = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))));
		}

		if (inputMap.get("instrumentId") != null
				&& !StringUtils.isEmpty(String.valueOf(inputMap.get("instrumentId")))) {
			lSql.append(" AND INid = ").append(lDBHelper.formatString(String.valueOf(inputMap.get("instrumentId"))));
		}

		List<InstrumentBean> lList = instrumentDao.findListFromSql(pExecutionContext.getConnection(), lSql.toString(),
				0);
		return lList;
	}

	public List<InstrumentWorkFlowBean> getInstrumentWorkFLowDao(ExecutionContext pExecutionContext, Long instrumentId)
			throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("SELECT * FROM InstrumentWorkFlow WHERE IWFinid = ")
				.append(lDBHelper.formatString(String.valueOf(instrumentId)));

		List<InstrumentWorkFlowBean> list = instrumentWorkFlowDao.findListFromSql(pExecutionContext.getConnection(),
				lSql.toString(), 0);

		return list;

	}

	/**
	 * 
	 * @param pExecutionContext
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public PurchaserSupplierLinkResponseBean getPurchaserSupplier(ExecutionContext pExecutionContext,
			Map<String, Object> inputMap) throws Exception {

		logger.info("inside getPurchaserSupplier");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		PurchaserSupplierLinkResponseBean purchaserSupplierLinkResponseBean = new PurchaserSupplierLinkResponseBean();

		try {
			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM PURCHASERSUPPLIERLINKS WHERE PSLPURCHASER = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));

			if (inputMap.get("sellerCode") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("sellerCode")))) {
				lSql.append(" AND PSLSUPPLIER = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))));
			}

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<PurchaserSupplierLinkHostApiBean> purchaserSupplierList = new ArrayList<>();
			while (lResultSet.next()) {
				PurchaserSupplierLinkHostApiBean purchaserSupplierLinkHostApiBean = new PurchaserSupplierLinkHostApiBean();

				logger.info("result set from db -" + lResultSet.getString("PSLSUPPLIER"));

				purchaserSupplierLinkHostApiBean.setPSLSUPPLIER(lResultSet.getString("PSLSUPPLIER"));
				purchaserSupplierLinkHostApiBean.setPSLPURCHASER(lResultSet.getString("PSLPURCHASER"));
				purchaserSupplierLinkHostApiBean
						.setPSLSUPPLIERPURCHASERREF(lResultSet.getString("PSLSUPPLIERPURCHASERREF"));
				purchaserSupplierLinkHostApiBean.setPSLCREDITPERIOD(lResultSet.getBigDecimal("PSLCREDITPERIOD"));
				purchaserSupplierLinkHostApiBean
						.setPSLEXTENDEDCREDITPERIOD(lResultSet.getBigDecimal("PSLEXTENDEDCREDITPERIOD"));
				purchaserSupplierLinkHostApiBean
						.setPSLPURCHASERSUPPLIERREF(lResultSet.getString("PSLPURCHASERSUPPLIERREF"));
				purchaserSupplierLinkHostApiBean.setPSLPERIOD1COSTBEARER(lResultSet.getString("PSLPERIOD1COSTBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD1COSTPERCENT(lResultSet.getBigDecimal("PSLPERIOD1COSTPERCENT"));
				purchaserSupplierLinkHostApiBean.setPSLPERIOD2COSTBEARER(lResultSet.getString("PSLPERIOD2COSTBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD2COSTPERCENT(lResultSet.getBigDecimal("PSLPERIOD2COSTPERCENT"));
				purchaserSupplierLinkHostApiBean.setPSLPERIOD3COSTBEARER(lResultSet.getString("PSLPERIOD3COSTBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD3COSTPERCENT(lResultSet.getBigDecimal("PSLPERIOD3COSTPERCENT"));
				purchaserSupplierLinkHostApiBean
						.setPSLBIDACCEPTINGENTITYTYPE(lResultSet.getString("PSLBIDACCEPTINGENTITYTYPE"));
				purchaserSupplierLinkHostApiBean.setPSLSETTLELEG3FLAG(lResultSet.getString("PSLSETTLELEG3FLAG"));
				purchaserSupplierLinkHostApiBean.setPSLAUTOACCEPT(lResultSet.getString("PSLAUTOACCEPT"));
				purchaserSupplierLinkHostApiBean
						.setPSLAUTOACCEPTABLEBIDTYPES(lResultSet.getString("PSLAUTOACCEPTABLEBIDTYPES"));
				purchaserSupplierLinkHostApiBean.setPSLAUTOCONVERT(lResultSet.getString("PSLAUTOCONVERT"));
				purchaserSupplierLinkHostApiBean
						.setPSLPURCHASERAUTOAPPROVEINVOICE(lResultSet.getString("PSLPURCHASERAUTOAPPROVEINVOICE"));
				purchaserSupplierLinkHostApiBean
						.setPSLSELLERAUTOAPPROVEINVOICE(lResultSet.getString("PSLSELLERAUTOAPPROVEINVOICE"));
				purchaserSupplierLinkHostApiBean.setPSLSTATUS(lResultSet.getString("PSLSTATUS"));
				purchaserSupplierLinkHostApiBean.setPSLAPPROVALSTATUS(lResultSet.getString("PSLAPPROVALSTATUS"));
				purchaserSupplierLinkHostApiBean.setPSLINVOICEMANDATORY(lResultSet.getString("PSLINVOICEMANDATORY"));
				purchaserSupplierLinkHostApiBean.setPSLREMARKS(lResultSet.getString("PSLREMARKS"));
				purchaserSupplierLinkHostApiBean.setPSLINWORKFLOW(lResultSet.getString("PSLINWORKFLOW"));
				purchaserSupplierLinkHostApiBean
						.setPSLCASHDISCOUNTPERCENT(lResultSet.getBigDecimal("PSLCASHDISCOUNTPERCENT"));
				purchaserSupplierLinkHostApiBean.setPSLHAIRCUTPERCENT(lResultSet.getBigDecimal("PSLHAIRCUTPERCENT"));
				purchaserSupplierLinkHostApiBean
						.setPSLINSTRUMENTCREATION(lResultSet.getString("PSLINSTRUMENTCREATION"));
				purchaserSupplierLinkHostApiBean.setPSLPLATFORMSTATUS(lResultSet.getString("PSLPLATFORMSTATUS"));
				purchaserSupplierLinkHostApiBean.setPSLRELATIONFLAG(lResultSet.getString("PSLRELATIONFLAG"));
				purchaserSupplierLinkHostApiBean
						.setPSLPLATFORMREASONCODE(lResultSet.getString("PSLPLATFORMREASONCODE"));
				purchaserSupplierLinkHostApiBean.setPSLRELATIONDOC(lResultSet.getString("PSLRELATIONDOC"));
				purchaserSupplierLinkHostApiBean
						.setPSLRELATIONEFFECTIVEDATE(lResultSet.getDate("PSLRELATIONEFFECTIVEDATE"));
				purchaserSupplierLinkHostApiBean.setPSLPLATFORMREMARKS(lResultSet.getString("PSLPLATFORMREMARKS"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD1CHARGEBEARER(lResultSet.getString("PSLPERIOD1CHARGEBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD1CHARGEPERCENT(lResultSet.getBigDecimal("PSLPERIOD1CHARGEPERCENT"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD2CHARGEBEARER(lResultSet.getString("PSLPERIOD2CHARGEBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD2CHARGEPERCENT(lResultSet.getBigDecimal("PSLPERIOD2CHARGEPERCENT"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD3CHARGEBEARER(lResultSet.getString("PSLPERIOD3CHARGEBEARER"));
				purchaserSupplierLinkHostApiBean
						.setPSLPERIOD3CHARGEPERCENT(lResultSet.getBigDecimal("PSLPERIOD3CHARGEPERCENT"));
				purchaserSupplierLinkHostApiBean.setPSLBUYERTDS(lResultSet.getString("PSLBUYERTDS"));
				purchaserSupplierLinkHostApiBean.setPSLSELLERTDS(lResultSet.getString("PSLSELLERTDS"));
				purchaserSupplierLinkHostApiBean.setPSLBUYERTDSPERCENT(lResultSet.getBigDecimal("PSLBUYERTDSPERCENT"));
				purchaserSupplierLinkHostApiBean
						.setPSLSELLERTDSPERCENT(lResultSet.getBigDecimal("PSLSELLERTDSPERCENT"));
				purchaserSupplierLinkHostApiBean.setPSLAUTHORIZERXIL(lResultSet.getString("PSLAUTHORIZERXIL"));

				purchaserSupplierList.add(purchaserSupplierLinkHostApiBean);

			}

			purchaserSupplierLinkResponseBean.setMessage("Success");
			purchaserSupplierLinkResponseBean.setMessageCode("0");
			purchaserSupplierLinkResponseBean.setPurchaserSupplierLinkHostApiBeans(purchaserSupplierList);

		} catch (Exception e) {
			e.printStackTrace();
			purchaserSupplierLinkResponseBean.setMessage("Error");
			purchaserSupplierLinkResponseBean.setMessageCode("1");
		}

		return purchaserSupplierLinkResponseBean;

	}

	/***
	 * 
	 * @param pExecutionContext
	 * @param purchaserCode
	 * @param supplierCode
	 * @return
	 * @throws Exception
	 */
	public List<PurchaserSupplierLinkWorkFlowHostApiBean> getPurchaserSupplierWorkFlow(
			ExecutionContext pExecutionContext, String purchaserCode, String supplierCode) throws Exception {

		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		List<PurchaserSupplierLinkWorkFlowHostApiBean> purchaserSupplierLinkWorkFlowHostApiBeans = new ArrayList<>();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM PurchaserSupplierLinkWorkFlow WHERE PLWSUPPLIER = ")
					.append(lDBHelper.formatString(supplierCode)).append(" AND PLWPURCHASER = ")
					.append(lDBHelper.formatString(purchaserCode))
					.append(" ORDER BY PLWSTATUSUPDATETIME DESC, PLWId DESC");

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				PurchaserSupplierLinkWorkFlowHostApiBean purchaserSupplierLinkWorkFlowHostApiBean = new PurchaserSupplierLinkWorkFlowHostApiBean();

				purchaserSupplierLinkWorkFlowHostApiBean.setPLWID(lResultSet.getBigDecimal("PLWID"));
				// purchaserSupplierLinkWorkFlowHostApiBean.setPLWSUPPLIER(lResultSet.getString("PLWSUPPLIER"));
				// purchaserSupplierLinkWorkFlowHostApiBean.setPLWPURCHASER(lResultSet.getString("PLWPURCHASER"));
				purchaserSupplierLinkWorkFlowHostApiBean.setPLWSTATUS(lResultSet.getString("PLWSTATUS"));
				purchaserSupplierLinkWorkFlowHostApiBean.setPLWSTATUSREMARKS(lResultSet.getString("PLWSTATUSREMARKS"));
				purchaserSupplierLinkWorkFlowHostApiBean.setPLWENTITY(lResultSet.getString("PLWENTITY"));
				purchaserSupplierLinkWorkFlowHostApiBean.setPLWAUID(lResultSet.getBigDecimal("PLWAUID"));
				purchaserSupplierLinkWorkFlowHostApiBean
						.setPLWSTATUSUPDATETIME(lResultSet.getDate("PLWSTATUSUPDATETIME"));

				purchaserSupplierLinkWorkFlowHostApiBeans.add(purchaserSupplierLinkWorkFlowHostApiBean);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in fetching instrumentWorkFlow Response - ", e);
		}

		return purchaserSupplierLinkWorkFlowHostApiBeans;

	}

	public List<PurchaserSupplierCapRateHostApiBean> getPurchaserSupplierCapRateDao(ExecutionContext pExecutionContext,
			String purchaserCode) throws Exception {

		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		List<PurchaserSupplierCapRateHostApiBean> purchaserSupplierCapRateHostApiBeans = new ArrayList<>();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM PURCHASERSUPPLIERCAPRATE WHERE PSLCOUNTERENTITYCODE = ")
					.append(lDBHelper.formatString(purchaserCode));

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				PurchaserSupplierCapRateHostApiBean purchaserSupplierCapRateHostApiBean = new PurchaserSupplierCapRateHostApiBean();

				purchaserSupplierCapRateHostApiBean.setPSLID(lResultSet.getBigDecimal("PSLID"));
				purchaserSupplierCapRateHostApiBean.setPSLENTITYCODE(lResultSet.getString("PSLENTITYCODE"));
				purchaserSupplierCapRateHostApiBean
						.setPSLCOUNTERENTITYCODE(lResultSet.getString("PSLCOUNTERENTITYCODE"));
				purchaserSupplierCapRateHostApiBean.setPSLFROMHAIRCUT(lResultSet.getBigDecimal("PSLFROMHAIRCUT"));
				purchaserSupplierCapRateHostApiBean.setPSLTOHAIRCUT(lResultSet.getBigDecimal("PSLTOHAIRCUT"));
				purchaserSupplierCapRateHostApiBean.setPSLFROMUSANCE(lResultSet.getBigDecimal("PSLFROMUSANCE"));
				purchaserSupplierCapRateHostApiBean.setPSLTOUSANCE(lResultSet.getBigDecimal("PSLTOUSANCE"));
				purchaserSupplierCapRateHostApiBean.setPSLCAPRATE(lResultSet.getBigDecimal("PSLCAPRATE"));

				purchaserSupplierCapRateHostApiBeans.add(purchaserSupplierCapRateHostApiBean);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in fetching purchaserSupplierCapRate Response - ", e);
		}

		return purchaserSupplierCapRateHostApiBeans;

	}

	/**
	 * @param pExecutionContext
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public InstrumentResponseBean getInstrumentData(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside getInstrumentData");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		InstrumentResponseBean instrumentResponse = new InstrumentResponseBean();

		try {
			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM Instruments WHERE INPurchaser = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));

			if (inputMap.get("sellerCode") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("sellerCode")))) {
				lSql.append(" AND INSupplier = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))));
			}

			if (inputMap.get("instrumentId") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("instrumentId")))) {
				lSql.append(" AND INid = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("instrumentId"))));
			}

			logger.info("query - " + lSql.toString());

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<InstrumentHostApiBean> hostApiBeansList = new ArrayList<>();
			while (lResultSet.next()) {
				InstrumentHostApiBean hostApiBean = new InstrumentHostApiBean();
				logger.info("Result set from db - " + lResultSet.getLong("INid"));

				hostApiBean.setINID(lResultSet.getBigDecimal("INid"));
				hostApiBean.setINTYPE(lResultSet.getString("INTYPE"));
				hostApiBean.setINSUPPLIER(lResultSet.getString("INSUPPLIER"));
				hostApiBean.setINSUPPLIERREF(lResultSet.getString("INSUPPLIERREF"));
				hostApiBean.setINSUPCLID(lResultSet.getBigDecimal("INSUPCLID"));
				hostApiBean.setINSUPGSTSTATE(lResultSet.getString("INSUPGSTSTATE"));
				hostApiBean.setINSUPGSTN(lResultSet.getString("INSUPGSTN"));
				hostApiBean.setINSUPSETTLECLID(lResultSet.getBigDecimal("INSUPSETTLECLID"));
				hostApiBean.setINSUPMSMESTATUS(lResultSet.getString("INSUPMSMESTATUS"));
				hostApiBean.setINPURCHASER(lResultSet.getString("INPURCHASER"));
				hostApiBean.setINPURCHASERREF(lResultSet.getString("INPURCHASERREF"));
				hostApiBean.setINPURCLID(lResultSet.getBigDecimal("INPURCLID"));
				hostApiBean.setINPURGSTSTATE(lResultSet.getString("INPURGSTSTATE"));
				hostApiBean.setINPURGSTN(lResultSet.getString("INPURGSTN"));
				hostApiBean.setINPURSETTLECLID(lResultSet.getBigDecimal("INPURSETTLECLID"));
				hostApiBean.setINPODATE(lResultSet.getDate("INPODATE"));
				hostApiBean.setINPONUMBER(lResultSet.getString("INPONUMBER"));
				hostApiBean.setINCOUNTERREFNUM(lResultSet.getString("INCOUNTERREFNUM"));
				hostApiBean.setINGOODSACCEPTDATE(lResultSet.getDate("INGOODSACCEPTDATE"));
				hostApiBean.setINDELCAT(lResultSet.getString("INDELCAT"));
				hostApiBean.setINDESCRIPTION(lResultSet.getString("INDESCRIPTION"));
				hostApiBean.setININSTNUMBER(lResultSet.getString("ININSTNUMBER"));
				hostApiBean.setININSTDATE(lResultSet.getDate("ININSTDATE"));
				hostApiBean.setININSTDUEDATE(lResultSet.getDate("ININSTDUEDATE"));
				hostApiBean.setINSTATDUEDATE(lResultSet.getDate("INSTATDUEDATE"));
				hostApiBean.setINMATURITYDATE(lResultSet.getDate("INMATURITYDATE"));
				hostApiBean.setINFACTORMAXENDDATETIME(lResultSet.getDate("INFACTORMAXENDDATETIME"));
				hostApiBean.setINCURRENCY(lResultSet.getString("INCURRENCY"));
				hostApiBean.setINAMOUNT(lResultSet.getBigDecimal("INAMOUNT"));
				hostApiBean.setINHAIRCUTPERCENT(lResultSet.getBigDecimal("INHAIRCUTPERCENT"));
				hostApiBean.setINADJAMOUNT(lResultSet.getBigDecimal("INADJAMOUNT"));
				hostApiBean.setINCASHDISCOUNTPERCENT(lResultSet.getBigDecimal("INCASHDISCOUNTPERCENT"));
				hostApiBean.setINCASHDISCOUNTVALUE(lResultSet.getBigDecimal("INCASHDISCOUNTVALUE"));
				hostApiBean.setINTDSAMOUNT(lResultSet.getBigDecimal("INTDSAMOUNT"));
				hostApiBean.setINNETAMOUNT(lResultSet.getBigDecimal("INNETAMOUNT"));
				hostApiBean.setININSTIMAGE(lResultSet.getString("ININSTIMAGE"));
				hostApiBean.setINCREDITNOTEIMAGE(lResultSet.getString("INCREDITNOTEIMAGE"));
				hostApiBean.setINSUPPORTINGS(lResultSet.getString("INSUPPORTINGS"));
				hostApiBean.setINCREDITPERIOD(lResultSet.getBigDecimal("INCREDITPERIOD"));
				hostApiBean.setINENABLEEXTENSION(lResultSet.getString("INENABLEEXTENSION"));
				hostApiBean.setINEXTENDEDCREDITPERIOD(lResultSet.getBigDecimal("INEXTENDEDCREDITPERIOD"));
				hostApiBean.setINEXTENDEDDUEDATE(lResultSet.getDate("INEXTENDEDDUEDATE"));
				hostApiBean.setINAUTOACCEPT(lResultSet.getString("INAUTOACCEPT"));
				hostApiBean.setINAUTOACCEPTABLEBIDTYPES(lResultSet.getString("INAUTOACCEPTABLEBIDTYPES"));
				hostApiBean.setINAUTOCONVERT(lResultSet.getString("INAUTOCONVERT"));
				hostApiBean.setINPERIOD1COSTBEARER(lResultSet.getString("INPERIOD1COSTBEARER"));
				hostApiBean.setINPERIOD1COSTPERCENT(lResultSet.getBigDecimal("INPERIOD1COSTPERCENT"));
				hostApiBean.setINPERIOD2COSTBEARER(lResultSet.getString("INPERIOD2COSTBEARER"));
				hostApiBean.setINPERIOD2COSTPERCENT(lResultSet.getBigDecimal("INPERIOD2COSTPERCENT"));
				hostApiBean.setINPERIOD3COSTBEARER(lResultSet.getString("INPERIOD3COSTBEARER"));
				hostApiBean.setINPERIOD3COSTPERCENT(lResultSet.getBigDecimal("INPERIOD3COSTPERCENT"));
				hostApiBean.setINSETTLELEG3FLAG(lResultSet.getString("INSETTLELEG3FLAG"));
				hostApiBean.setINFILEID(lResultSet.getBigDecimal("INFILEID"));
				hostApiBean.setINSTATUS(lResultSet.getString("INSTATUS"));
				hostApiBean.setINSTATUSREMARKS(lResultSet.getString("INSTATUSREMARKS"));
				hostApiBean.setINSTATUSUPDATETIME(lResultSet.getDate("INSTATUSUPDATETIME"));
				hostApiBean.setINMAKERENTITY(lResultSet.getString("INMAKERENTITY"));
				hostApiBean.setINMAKERAUID(lResultSet.getBigDecimal("INMAKERAUID"));
				hostApiBean.setINCHECKERAUID(lResultSet.getBigDecimal("INCHECKERAUID"));
				hostApiBean.setINCOUNTERENTITY(lResultSet.getString("INCOUNTERENTITY"));
				hostApiBean.setINCOUNTERAUID(lResultSet.getBigDecimal("INCOUNTERAUID"));
				hostApiBean.setINCOUNTERCHECKERAUID(lResultSet.getBigDecimal("INCOUNTERCHECKERAUID"));
				hostApiBean.setINOWNERENTITY(lResultSet.getString("INOWNERENTITY"));
				hostApiBean.setINOWNERAUID(lResultSet.getBigDecimal("INOWNERAUID"));
				hostApiBean.setINMONETAGOLEDGERID(lResultSet.getString("INMONETAGOLEDGERID"));
				hostApiBean.setINMONETAGOFACTORTXNID(lResultSet.getString("INMONETAGOFACTORTXNID"));
				hostApiBean.setINMONETAGOCANCELTXNID(lResultSet.getString("INMONETAGOCANCELTXNID"));
				hostApiBean.setINFUID(lResultSet.getBigDecimal("INFUID"));
				hostApiBean.setINCOUNTERMODIFIEDFIELDS(lResultSet.getString("INCOUNTERMODIFIEDFIELDS"));
				hostApiBean.setINSALESCATEGORY(lResultSet.getString("INSALESCATEGORY"));
				hostApiBean.setINEWAYBILLNO(lResultSet.getString("INEWAYBILLNO"));
				hostApiBean.setINSUPPLYTYPE(lResultSet.getString("INSUPPLYTYPE"));
				hostApiBean.setINDOCTYPE(lResultSet.getString("INDOCTYPE"));
				hostApiBean.setINDOCNO(lResultSet.getString("INDOCNO"));
				hostApiBean.setINDOCDATE(lResultSet.getDate("INDOCDATE"));
				hostApiBean.setINFROMPINCODE(lResultSet.getString("INFROMPINCODE"));
				hostApiBean.setINTOPINCODE(lResultSet.getString("INTOPINCODE"));
				hostApiBean.setINTRANSMODE(lResultSet.getBigDecimal("INTRANSMODE"));
				hostApiBean.setINTRANSPORTERNAME(lResultSet.getString("INTRANSPORTERNAME"));
				hostApiBean.setINTRANSPORTERID(lResultSet.getString("INTRANSPORTERID"));
				hostApiBean.setINTRANSDOCNO(lResultSet.getString("INTRANSDOCNO"));
				hostApiBean.setINTRANSDOCDATE(lResultSet.getDate("INTRANSDOCDATE"));
				hostApiBean.setINVEHICLENO(lResultSet.getString("INVEHICLENO"));
				hostApiBean.setINGROUPFLAG(lResultSet.getString("INGROUPFLAG"));
				hostApiBean.setINGROUPINID(lResultSet.getBigDecimal("INGROUPINID"));
				hostApiBean.setINGROUPREFNO(lResultSet.getString("INGROUPREFNO"));
				hostApiBean.setINCERSAIFILEID(lResultSet.getBigDecimal("INCERSAIFILEID"));
				hostApiBean.setINEBDID(lResultSet.getBigDecimal("INEBDID"));
				hostApiBean.setINMKRCHKLEVEL(lResultSet.getBigDecimal("INMKRCHKLEVEL"));
				hostApiBean.setINCNTCHKLEVEL(lResultSet.getBigDecimal("INCNTCHKLEVEL"));
				hostApiBean.setINOTHERSETTINGS(lResultSet.getString("INOTHERSETTINGS"));
				hostApiBean.setINRECORDCREATETIME(lResultSet.getDate("INRECORDCREATETIME"));
				hostApiBean.setINRECORDVERSION(lResultSet.getBigDecimal("INRECORDVERSION"));
				hostApiBean.setINAGGREGATORENTITY(lResultSet.getString("INAGGREGATORENTITY"));
				hostApiBean.setINAGGREGATORAUID(lResultSet.getBigDecimal("INAGGREGATORAUID"));
				hostApiBean.setINPURBILLCLID(lResultSet.getBigDecimal("INPURBILLCLID"));
				hostApiBean.setINSUPBILLCLID(lResultSet.getBigDecimal("INSUPBILLCLID"));
				hostApiBean.setINCOUNTERUPDATEFIELDS(lResultSet.getString("INCOUNTERUPDATEFIELDS"));
				hostApiBean.setINCFID(lResultSet.getBigDecimal("INCFID"));
				hostApiBean.setINCFDATA(lResultSet.getString("INCFDATA"));
				hostApiBean.setINPERIOD1CHARGEBEARER(lResultSet.getString("INPERIOD1CHARGEBEARER"));
				hostApiBean.setINPERIOD1CHARGEPERCENT(lResultSet.getBigDecimal("INPERIOD1CHARGEPERCENT"));
				hostApiBean.setINPERIOD2CHARGEBEARER(lResultSet.getString("INPERIOD2CHARGEBEARER"));
				hostApiBean.setINPERIOD2CHARGEPERCENT(lResultSet.getBigDecimal("INPERIOD2CHARGEPERCENT"));
				hostApiBean.setINPERIOD3CHARGEBEARER(lResultSet.getString("INPERIOD3CHARGEBEARER"));
				hostApiBean.setINPERIOD3CHARGEPERCENT(lResultSet.getBigDecimal("INPERIOD3CHARGEPERCENT"));

				hostApiBeansList.add(hostApiBean);

			}

			instrumentResponse.setMessage("Success");
			instrumentResponse.setMessageCode("0");
			instrumentResponse.setInstrumentBean(hostApiBeansList);

			// List<InstrumentHostApiBean> lList = instrumentHostApiBeanDao
			// .findListFromSql(pExecutionContext.getConnection(),
			// lSql.toString(), 0);
			// logger.info("size - " + lList.size());

		} catch (Exception e) {
			logger.error("Error in fetching instrument Response - ", e);
			instrumentResponse.setMessage("Error");
			instrumentResponse.setMessageCode("1");
		}
		return instrumentResponse;
	}

	public List<InstrumentWorkFlowHostApiBean> getInstrumentWorkFlow(ExecutionContext pExecutionContext,
			Long instrumentId) throws Exception {

		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		List<InstrumentWorkFlowHostApiBean> hostApiInstrumentWorkFlowBean = new ArrayList<>();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM InstrumentWorkFlow WHERE IWFinid = ")
					.append(lDBHelper.formatString(String.valueOf(instrumentId)));

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {

				InstrumentWorkFlowHostApiBean instrumentWorkFlowHostApiBean = new InstrumentWorkFlowHostApiBean();
				instrumentWorkFlowHostApiBean.setIWFID(lResultSet.getLong("IWFID"));
				instrumentWorkFlowHostApiBean.setIWFINID(lResultSet.getLong("IWFINID"));
				instrumentWorkFlowHostApiBean.setIWFSTATUS(lResultSet.getString("IWFSTATUS"));
				instrumentWorkFlowHostApiBean.setIWFSTATUSREMARKS(lResultSet.getString("IWFSTATUSREMARKS"));
				instrumentWorkFlowHostApiBean.setIWFENTITY(lResultSet.getString("IWFENTITY"));
				instrumentWorkFlowHostApiBean.setIWFAUID(lResultSet.getLong("IWFAUID"));
				instrumentWorkFlowHostApiBean.setIWFSTATUSUPDATETIME(lResultSet.getTimestamp("IWFSTATUSUPDATETIME"));

				hostApiInstrumentWorkFlowBean.add(instrumentWorkFlowHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching instrumentWorkFlow Response - ", e);
		}

		return hostApiInstrumentWorkFlowBean;

	}

	public UserResponseBean getUserData(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {
		logger.info("inside getInstrumentData");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		UserResponseBean userResponseBean = new UserResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM APPUSERS WHERE AUDOMAIN = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("domain"))));

			if (inputMap.get("loginId") != null && !StringUtils.isEmpty(String.valueOf(inputMap.get("loginId")))) {
				lSql.append(" AND AULOGINID = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("loginId"))));
			}

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<UserHostApiBean> userHostApiList = new ArrayList<>();
			while (lResultSet.next()) {
				UserHostApiBean userHostApiBean = new UserHostApiBean();

				logger.info("resutl set from DB - " + lResultSet.getString("AUDOMAIN"));

				userHostApiBean.setAUID(lResultSet.getBigDecimal("AUID"));
				userHostApiBean.setAUDOMAIN(lResultSet.getString("AUDOMAIN"));
				userHostApiBean.setAULOGINID(lResultSet.getString("AULOGINID"));
				userHostApiBean.setAUPASSWORD1(lResultSet.getString("AUPASSWORD1"));
				userHostApiBean.setAUPASSWORDUPDATEDAT1(lResultSet.getDate("AUPASSWORDUPDATEDAT1"));
				userHostApiBean.setAUFORCEPASSWORDCHANGE(lResultSet.getString("AUFORCEPASSWORDCHANGE"));
				userHostApiBean.setAUSTATUS(lResultSet.getString("AUSTATUS"));
				userHostApiBean.setAUFAILEDLOGINCOUNT(lResultSet.getBigDecimal("AUFAILEDLOGINCOUNT"));
				userHostApiBean.setAUTYPE(lResultSet.getBigDecimal("AUTYPE"));
				userHostApiBean.setAUSALUTATION(lResultSet.getString("AUSALUTATION"));
				userHostApiBean.setAUFIRSTNAME(lResultSet.getString("AUFIRSTNAME"));
				userHostApiBean.setAUMIDDLENAME(lResultSet.getString("AUMIDDLENAME"));
				userHostApiBean.setAULASTNAME(lResultSet.getString("AULASTNAME"));
				userHostApiBean.setAUTELEPHONE(lResultSet.getString("AUTELEPHONE"));
				userHostApiBean.setAUMOBILE(lResultSet.getString("AUMOBILE"));
				userHostApiBean.setAUEMAIL(lResultSet.getString("AUEMAIL"));
				userHostApiBean.setAUALTEMAIL(lResultSet.getString("AUALTEMAIL"));
				userHostApiBean.setAUENABLE2FA(lResultSet.getString("AUENABLE2FA"));
				userHostApiBean.setAUSECURITYSETTINGS(lResultSet.getString("AUSECURITYSETTINGS"));
				userHostApiBean.setAUOTHERSETTINGS(lResultSet.getString("AUOTHERSETTINGS"));
				userHostApiBean.setAUENABLEAPI(lResultSet.getString("AUENABLEAPI"));
				userHostApiBean.setAURMIDS(lResultSet.getString("AURMIDS"));
				userHostApiBean.setAUFULLOWNERSHIP(lResultSet.getString("AUFULLOWNERSHIP"));
				userHostApiBean.setAUOWNERAUID(lResultSet.getBigDecimal("AUOWNERAUID"));
				userHostApiBean.setAUIPS(lResultSet.getString("AUIPS"));
				userHostApiBean.setAUUSERLIMITS(lResultSet.getString("AUUSERLIMITS"));
				userHostApiBean.setAURECORDCREATOR(lResultSet.getBigDecimal("AURECORDCREATOR"));
				userHostApiBean.setAURECORDCREATETIME(lResultSet.getDate("AURECORDCREATETIME"));
				userHostApiBean.setAURECORDUPDATOR(lResultSet.getBigDecimal("AURECORDUPDATOR"));
				userHostApiBean.setAURECORDUPDATETIME(lResultSet.getDate("AURECORDUPDATETIME"));
				userHostApiBean.setAURECORDVERSION(lResultSet.getBigDecimal("AURECORDVERSION"));
				userHostApiBean.setAULOCATIONIDS(lResultSet.getString("AULOCATIONIDS"));
				userHostApiBean.setAUCHECKERLEVELSETTING(lResultSet.getString("AUCHECKERLEVELSETTING"));
				userHostApiBean.setAURMLOCATION(lResultSet.getNString("AURMLOCATION"));

				userHostApiList.add(userHostApiBean);

			}

			userResponseBean.setMessage("Success");
			userResponseBean.setMessageCode("0");
			userResponseBean.setUserHostApiBean(userHostApiList);

		} catch (Exception e) {
			logger.error("Error in fetching user Response - ", e);
			userResponseBean.setMessage("Error");
			userResponseBean.setMessageCode("1");

		}

		return userResponseBean;

	}

	public List<UserHostApiRmIdMapping> getRMMappingDao(ExecutionContext pExecutionContext, String roleIds)
			throws Exception {

		logger.info("inside getInstrumentData");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		List<UserHostApiRmIdMapping> rmIdList = new ArrayList<>();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT RMID, RMNAME FROM ROLEMASTER WHERE RMID in (").append(roleIds.toString()).append(")");
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				UserHostApiRmIdMapping hostApiRmIdMapping = new UserHostApiRmIdMapping();
				logger.info("result from db - " + lResultSet.getString("RMID"));

				hostApiRmIdMapping.setRoleId(lResultSet.getInt("RMID"));
				hostApiRmIdMapping.setRoleName(lResultSet.getString("RMNAME"));
				rmIdList.add(hostApiRmIdMapping);

			}

		} catch (Exception e) {
			logger.error("Error in fetching user rmid Response - ", e);
		}

		return rmIdList;

	}

	public List<UserHostApiMakerCheckerMap> getUserChecker(ExecutionContext pExecutionContext, BigDecimal auid)
			throws Exception {
		List<UserHostApiMakerCheckerMap> userCheckerList = new ArrayList<>();
		logger.info("inside getUserChecker");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM MAKERCHECKERMAP WHERE MCMMAKERID = ").append(auid);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				UserHostApiMakerCheckerMap makerChecker = new UserHostApiMakerCheckerMap();

				makerChecker.setMCMCHECKERID(lResultSet.getBigDecimal("MCMCHECKERID"));
				makerChecker.setMCMCHECKERTYPE(lResultSet.getString("MCMCHECKERTYPE"));
				makerChecker.setMCMID(lResultSet.getBigDecimal("MCMID"));
				makerChecker.setMCMMAKERID(lResultSet.getBigDecimal("MCMMAKERID"));
				makerChecker.setMCMRECORDCREATETIME(lResultSet.getDate("MCMRECORDCREATETIME"));
				makerChecker.setMCMRECORDCREATOR(lResultSet.getBigDecimal("MCMRECORDCREATOR"));
				makerChecker.setMCMRECORDUPDATETIME(lResultSet.getDate("MCMRECORDUPDATETIME"));
				makerChecker.setMCMRECORDUPDATOR(lResultSet.getBigDecimal("MCMRECORDUPDATOR"));
				makerChecker.setMCMRECORDVERSION(lResultSet.getBigDecimal("MCMRECORDVERSION"));

				userCheckerList.add(makerChecker);

			}

		} catch (Exception e) {
			logger.error("Error in fetching user maker checker Response - ", e);

		}

		return userCheckerList;
	}

	public AggregatorPurchaserMapResponseBean getAggregatorPurchaserMap(ExecutionContext pExecutionContext,
			Map<String, Object> inputMap) throws Exception {

		logger.info("inside getInstrumentData");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		AggregatorPurchaserMapResponseBean aggregatorPurchaserMapResponseBean = new AggregatorPurchaserMapResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM AGGREGATORPURCHASERMAP WHERE APMAGGREGATOR = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("aggregator"))));

			if (inputMap.get("purchaser") != null && !StringUtils.isEmpty(String.valueOf(inputMap.get("purchaser")))) {
				lSql.append(" AND APMPURCHASER = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("purchaser"))));
			}

			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<AggregatorPurchaserMapHostApiBean> aggregatorPurchaserMapList = new ArrayList<>();
			while (lResultSet.next()) {
				AggregatorPurchaserMapHostApiBean aggregatorPurchaserMapHostApiBean = new AggregatorPurchaserMapHostApiBean();
				logger.info("lResultSet from DB - " + lResultSet.getString("APMAGGREGATOR"));

				aggregatorPurchaserMapHostApiBean.setAPMAGGREGATOR(lResultSet.getString("APMAGGREGATOR"));
				aggregatorPurchaserMapHostApiBean.setAPMPURCHASER(lResultSet.getString("APMPURCHASER"));
				aggregatorPurchaserMapHostApiBean.setAPMRECORDCREATETIME(lResultSet.getDate("APMRECORDCREATETIME"));
				aggregatorPurchaserMapHostApiBean.setAPMRECORDCREATOR(lResultSet.getBigDecimal("APMRECORDCREATOR"));
				aggregatorPurchaserMapHostApiBean.setAPMRECORDUPDATETIME(lResultSet.getDate("APMRECORDUPDATETIME"));
				aggregatorPurchaserMapHostApiBean.setAPMRECORDUPDATOR(lResultSet.getBigDecimal("APMRECORDUPDATOR"));
				aggregatorPurchaserMapHostApiBean.setAPMRECORDVERSION(lResultSet.getBigDecimal("APMRECORDVERSION"));

				aggregatorPurchaserMapList.add(aggregatorPurchaserMapHostApiBean);

			}

			aggregatorPurchaserMapResponseBean.setMessage("Success");
			aggregatorPurchaserMapResponseBean.setMessageCode("0");
			aggregatorPurchaserMapResponseBean.setAggregatorPurchaserMap(aggregatorPurchaserMapList);

		} catch (Exception e) {
			logger.error("Error in fetching aggregator Purchaser Response - ", e);
			aggregatorPurchaserMapResponseBean.setMessage("Error");
			aggregatorPurchaserMapResponseBean.setMessageCode("1");
		}

		return aggregatorPurchaserMapResponseBean;
	}

	public FactoringUnitResponseBean getFactoringUnitDao(ExecutionContext pExecutionContext,
			Map<String, Object> inputMap) throws Exception {

		logger.info("inside getFactoringUnitDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		FactoringUnitResponseBean factoringUnitResponseBean = new FactoringUnitResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM FACTORINGUNITS WHERE FUPURCHASER = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));

			if (inputMap.get("sellerCode") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("sellerCode")))) {
				lSql.append(" AND FUSUPPLIER = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))));
			}

			if (inputMap.get("factoringUnitId") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("factoringUnitId")))) {
				lSql.append(" AND FUID = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("factoringUnitId"))));
			}
			logger.info("sql fact - " + lSql);
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<FactoringUnitHostApiBean> factoringUnitList = new ArrayList<>();
			while (lResultSet.next()) {
				FactoringUnitHostApiBean factoringUnitHostApiBean = new FactoringUnitHostApiBean();
				logger.info("lResultSet from DB - " + lResultSet.getString("FUID"));

				factoringUnitHostApiBean.setFUID(lResultSet.getBigDecimal("FUID"));
				factoringUnitHostApiBean.setFUMATURITYDATE(lResultSet.getDate("FUMATURITYDATE"));
				factoringUnitHostApiBean.setFUSTATDUEDATE(lResultSet.getDate("FUSTATDUEDATE"));
				factoringUnitHostApiBean.setFUENABLEEXTENSION(lResultSet.getString("FUENABLEEXTENSION"));
				factoringUnitHostApiBean.setFUEXTENDEDCREDITPERIOD(lResultSet.getBigDecimal("FUEXTENDEDCREDITPERIOD"));
				factoringUnitHostApiBean.setFUEXTENDEDDUEDATE(lResultSet.getDate("FUEXTENDEDDUEDATE"));
				factoringUnitHostApiBean.setFUCURRENCY(lResultSet.getString("FUCURRENCY"));
				factoringUnitHostApiBean.setFUAMOUNT(lResultSet.getBigDecimal("FUAMOUNT"));
				factoringUnitHostApiBean.setFUPURCHASER(lResultSet.getString("FUPURCHASER"));
				factoringUnitHostApiBean.setFUPURCHASERREF(lResultSet.getString("FUPURCHASERREF"));
				factoringUnitHostApiBean.setFUSUPPLIER(lResultSet.getString("FUSUPPLIER"));
				factoringUnitHostApiBean.setFUSUPPLIERREF(lResultSet.getString("FUSUPPLIERREF"));
				factoringUnitHostApiBean.setFUINTRODUCINGENTITY(lResultSet.getString("FUINTRODUCINGENTITY"));
				factoringUnitHostApiBean.setFUINTRODUCINGAUID(lResultSet.getBigDecimal("FUINTRODUCINGAUID"));
				factoringUnitHostApiBean.setFUCOUNTERENTITY(lResultSet.getString("FUCOUNTERENTITY"));
				factoringUnitHostApiBean.setFUCOUNTERAUID(lResultSet.getBigDecimal("FUCOUNTERAUID"));
				factoringUnitHostApiBean.setFUOWNERENTITY(lResultSet.getString("FUOWNERENTITY"));
				factoringUnitHostApiBean.setFUOWNERAUID(lResultSet.getBigDecimal("FUOWNERAUID"));
				factoringUnitHostApiBean.setFUSTATUS(lResultSet.getString("FUSTATUS"));
				factoringUnitHostApiBean.setFUFACTORSTARTDATETIME(lResultSet.getDate("FUFACTORSTARTDATETIME"));
				factoringUnitHostApiBean.setFUFACTORENDDATETIME(lResultSet.getDate("FUFACTORENDDATETIME"));
				factoringUnitHostApiBean.setFUFACTORMAXENDDATETIME(lResultSet.getDate("FUFACTORMAXENDDATETIME"));
				factoringUnitHostApiBean.setFUAUTOACCEPT(lResultSet.getString("FUAUTOACCEPT"));
				factoringUnitHostApiBean.setFUAUTOACCEPTABLEBIDTYPES(lResultSet.getString("FUAUTOACCEPTABLEBIDTYPES"));
				factoringUnitHostApiBean.setFUAUTOCONVERT(lResultSet.getString("FUAUTOCONVERT"));
				factoringUnitHostApiBean.setFUPERIOD1COSTBEARER(lResultSet.getString("FUPERIOD1COSTBEARER"));
				factoringUnitHostApiBean.setFUPERIOD1COSTPERCENT(lResultSet.getBigDecimal("FUPERIOD1COSTPERCENT"));
				factoringUnitHostApiBean.setFUPERIOD2COSTBEARER(lResultSet.getString("FUPERIOD2COSTBEARER"));
				factoringUnitHostApiBean.setFUPERIOD2COSTPERCENT(lResultSet.getBigDecimal("FUPERIOD2COSTPERCENT"));
				factoringUnitHostApiBean.setFUPERIOD3COSTBEARER(lResultSet.getString("FUPERIOD3COSTBEARER"));
				factoringUnitHostApiBean.setFUPERIOD3COSTPERCENT(lResultSet.getBigDecimal("FUPERIOD3COSTPERCENT"));
				factoringUnitHostApiBean.setFUSUPGSTSTATE(lResultSet.getString("FUSUPGSTSTATE"));
				factoringUnitHostApiBean.setFUSUPGSTN(lResultSet.getString("FUSUPGSTN"));
				factoringUnitHostApiBean.setFUPURGSTSTATE(lResultSet.getString("FUPURGSTSTATE"));
				factoringUnitHostApiBean.setFUPURGSTN(lResultSet.getString("FUPURGSTN"));
				factoringUnitHostApiBean.setFUSETTLELEG3FLAG(lResultSet.getString("FUSETTLELEG3FLAG"));
				factoringUnitHostApiBean.setFUBDID(lResultSet.getBigDecimal("FUBDID"));
				factoringUnitHostApiBean.setFUACCEPTEDBIDTYPE(lResultSet.getString("FUACCEPTEDBIDTYPE"));
				factoringUnitHostApiBean.setFUACCEPTEDRATE(lResultSet.getBigDecimal("FUACCEPTEDRATE"));
				factoringUnitHostApiBean.setFUACCEPTEDHAIRCUT(lResultSet.getBigDecimal("FUACCEPTEDHAIRCUT"));
				factoringUnitHostApiBean.setFULEG1DATE(lResultSet.getDate("FULEG1DATE"));
				factoringUnitHostApiBean.setFUFACTOREDAMOUNT(lResultSet.getBigDecimal("FUFACTOREDAMOUNT"));
				factoringUnitHostApiBean
						.setFUPURCHASERLEG1INTEREST(lResultSet.getBigDecimal("FUPURCHASERLEG1INTEREST"));
				factoringUnitHostApiBean.setFUSUPPLIERLEG1INTEREST(lResultSet.getBigDecimal("FUSUPPLIERLEG1INTEREST"));
				factoringUnitHostApiBean
						.setFUPURCHASERLEG2INTEREST(lResultSet.getBigDecimal("FUPURCHASERLEG2INTEREST"));
				factoringUnitHostApiBean
						.setFULEG2EXTENSIONINTEREST(lResultSet.getBigDecimal("FULEG2EXTENSIONINTEREST"));
				factoringUnitHostApiBean.setFUCHARGES(lResultSet.getBigDecimal("FUCHARGES"));
				factoringUnitHostApiBean.setFUENTITYGSTSUMMARY(lResultSet.getString("FUENTITYGSTSUMMARY"));
				factoringUnitHostApiBean.setFUFINANCIER(lResultSet.getString("FUFINANCIER"));
				factoringUnitHostApiBean.setFUACCEPTINGENTITY(lResultSet.getString("FUACCEPTINGENTITY"));
				factoringUnitHostApiBean.setFUACCEPTINGAUID(lResultSet.getBigDecimal("FUACCEPTINGAUID"));
				factoringUnitHostApiBean.setFUACCEPTDATETIME(lResultSet.getDate("FUACCEPTDATETIME"));
				factoringUnitHostApiBean.setFULIMITUTILIZED(lResultSet.getBigDecimal("FULIMITUTILIZED"));
				factoringUnitHostApiBean.setFULIMITIDS(lResultSet.getString("FULIMITIDS"));
				factoringUnitHostApiBean.setFUPURSUPLIMITUTILIZED(lResultSet.getBigDecimal("FUPURSUPLIMITUTILIZED"));
				factoringUnitHostApiBean.setFUSALESCATEGORY(lResultSet.getString("FUSALESCATEGORY"));
				factoringUnitHostApiBean.setFUPURCHASERSETTLELOC(lResultSet.getBigDecimal("FUPURCHASERSETTLELOC"));
				factoringUnitHostApiBean.setFUSUPPLIERSETTLELOC(lResultSet.getBigDecimal("FUSUPPLIERSETTLELOC"));
				factoringUnitHostApiBean.setFUFINANCIERSETTLELOC(lResultSet.getBigDecimal("FUFINANCIERSETTLELOC"));
				factoringUnitHostApiBean.setFUSTATUSUPDATETIME(lResultSet.getDate("FUSTATUSUPDATETIME"));
				factoringUnitHostApiBean.setFUCOSTBEARERBILLID(lResultSet.getBigDecimal("FUCOSTBEARERBILLID"));
				factoringUnitHostApiBean.setFUFINANCIERBILLID(lResultSet.getBigDecimal("FUFINANCIERBILLID"));
				factoringUnitHostApiBean.setFURECORDCREATETIME(lResultSet.getDate("FURECORDCREATETIME"));
				factoringUnitHostApiBean.setFURECORDVERSION(lResultSet.getBigDecimal("FURECORDVERSION"));
				factoringUnitHostApiBean.setFUFINANCIERBILLLOC(lResultSet.getBigDecimal("FUFINANCIERBILLLOC"));
				factoringUnitHostApiBean.setFUPURCHASERBILLLOC(lResultSet.getBigDecimal("FUPURCHASERBILLLOC"));
				factoringUnitHostApiBean.setFUSUPPLIERBILLLOC(lResultSet.getBigDecimal("FUSUPPLIERBILLLOC"));
				factoringUnitHostApiBean.setFUEXTBILLID1(lResultSet.getBigDecimal("FUEXTBILLID1"));
				factoringUnitHostApiBean.setFUEXTBILLID2(lResultSet.getBigDecimal("FUEXTBILLID2"));
				factoringUnitHostApiBean.setFUPERIOD1CHARGEBEARER(lResultSet.getString("FUPERIOD1CHARGEBEARER"));
				factoringUnitHostApiBean.setFUPERIOD1CHARGEPERCENT(lResultSet.getBigDecimal("FUPERIOD1CHARGEPERCENT"));
				factoringUnitHostApiBean.setFUPERIOD2CHARGEBEARER(lResultSet.getString("FUPERIOD2CHARGEBEARER"));
				factoringUnitHostApiBean.setFUPERIOD2CHARGEPERCENT(lResultSet.getBigDecimal("FUPERIOD2CHARGEPERCENT"));
				factoringUnitHostApiBean.setFUPERIOD3CHARGEBEARER(lResultSet.getString("FUPERIOD3CHARGEBEARER"));
				factoringUnitHostApiBean.setFUPERIOD3CHARGEPERCENT(lResultSet.getBigDecimal("FUPERIOD3CHARGEPERCENT"));

				factoringUnitList.add(factoringUnitHostApiBean);

			}

			factoringUnitResponseBean.setMessage("Success");
			factoringUnitResponseBean.setMessageCode("0");
			factoringUnitResponseBean.setFactoringUnitList(factoringUnitList);

		} catch (Exception e) {
			logger.error("Error in fetching factoring Response - ", e);
			factoringUnitResponseBean.setMessage("Error");
			factoringUnitResponseBean.setMessageCode("1");
		}

		return factoringUnitResponseBean;
	}

	public List<FactoringUnitWatchHostApiBean> getFactoringUnitWatchDao(ExecutionContext pExecutionContext,
			BigDecimal factoringUnitId) throws Exception {
		List<FactoringUnitWatchHostApiBean> factoringUnitWatch = new ArrayList<>();
		logger.info("inside getFactoringUnitWatchDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM FACTORINGUNITWATCH WHERE FUWFUID = ").append(factoringUnitId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				FactoringUnitWatchHostApiBean factoringUnitWatchHostApiBean = new FactoringUnitWatchHostApiBean();

				factoringUnitWatchHostApiBean.setFUWFUID(lResultSet.getBigDecimal("FUWFUID"));
				factoringUnitWatchHostApiBean.setFUWAUID(lResultSet.getBigDecimal("FUWAUID"));

				factoringUnitWatch.add(factoringUnitWatchHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching user maker checker Response - ", e);

		}

		return factoringUnitWatch;
	}

	public List<FactoringUnitBidHostApiBean> getFactoringUnitBidDao(ExecutionContext pExecutionContext,
			BigDecimal bdfuid) throws Exception {
		List<FactoringUnitBidHostApiBean> factoringUnitBidList = new ArrayList<>();
		logger.info("inside getFactoringUnitBidDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM BIDS WHERE BDFUID = ").append(bdfuid);
			logger.info("factoringBidQuery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				FactoringUnitBidHostApiBean factoringUnitBidHostApiBean = new FactoringUnitBidHostApiBean();

				factoringUnitBidHostApiBean.setBDFUID(lResultSet.getBigDecimal("BDFUID"));
				factoringUnitBidHostApiBean.setBDFINANCIERENTITY(lResultSet.getString("BDFINANCIERENTITY"));
				factoringUnitBidHostApiBean.setBDFINANCIERAUID(lResultSet.getBigDecimal("BDFINANCIERAUID"));
				factoringUnitBidHostApiBean.setBDRATE(lResultSet.getBigDecimal("BDRATE"));
				factoringUnitBidHostApiBean.setBDHAIRCUT(lResultSet.getBigDecimal("BDHAIRCUT"));
				factoringUnitBidHostApiBean.setBDVALIDTILL(lResultSet.getDate("BDVALIDTILL"));
				factoringUnitBidHostApiBean.setBDSTATUS(lResultSet.getString("BDSTATUS"));
				factoringUnitBidHostApiBean.setBDSTATUSREMARKS(lResultSet.getString("BDSTATUSREMARKS"));
				factoringUnitBidHostApiBean.setBDID(lResultSet.getBigDecimal("BDID"));
				factoringUnitBidHostApiBean.setBDTIMESTAMP(lResultSet.getDate("BDTIMESTAMP"));
				factoringUnitBidHostApiBean.setBDLASTAUID(lResultSet.getBigDecimal("BDLASTAUID"));
				factoringUnitBidHostApiBean.setBDBIDTYPE(lResultSet.getString("BDBIDTYPE"));
				factoringUnitBidHostApiBean.setBDPROVRATE(lResultSet.getBigDecimal("BDPROVRATE"));
				factoringUnitBidHostApiBean.setBDPROVHAIRCUT(lResultSet.getBigDecimal("BDPROVHAIRCUT"));
				factoringUnitBidHostApiBean.setBDPROVVALIDTILL(lResultSet.getDate("BDPROVVALIDTILL"));
				factoringUnitBidHostApiBean.setBDPROVBIDTYPE(lResultSet.getString("BDPROVBIDTYPE"));
				factoringUnitBidHostApiBean.setBDPROVACTION(lResultSet.getString("BDPROVACTION"));
				factoringUnitBidHostApiBean.setBDAPPSTATUS(lResultSet.getString("BDAPPSTATUS"));
				factoringUnitBidHostApiBean.setBDAPPREMARKS(lResultSet.getString("BDAPPREMARKS"));
				factoringUnitBidHostApiBean.setBDCHECKERAUID(lResultSet.getBigDecimal("BDCHECKERAUID"));
				factoringUnitBidHostApiBean.setBDLIMITUTILISED(lResultSet.getBigDecimal("BDLIMITUTILISED"));
				factoringUnitBidHostApiBean.setBDBIDLIMITUTILISED(lResultSet.getBigDecimal("BDBIDLIMITUTILISED"));
				factoringUnitBidHostApiBean.setBDLIMITIDS(lResultSet.getString("BDLIMITIDS"));
				factoringUnitBidHostApiBean.setBDCOSTLEG(lResultSet.getString("BDCOSTLEG"));
				factoringUnitBidHostApiBean.setBDCHKLEVEL(lResultSet.getBigDecimal("BDCHKLEVEL"));
				factoringUnitBidHostApiBean.setBDCHARGES(lResultSet.getString("BDCHARGES"));

				factoringUnitBidList.add(factoringUnitBidHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching user maker checker Response - ", e);

		}

		return factoringUnitBidList;
	}

	public ObligationResponseBean getObligationsDao(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside getFactoringUnitDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		ObligationResponseBean obligationResponseBean = new ObligationResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM OBLIGATIONS WHERE OBID = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("obligationId"))));

			if (inputMap.get("factoringUnitId") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("factoringUnitId")))) {
				lSql.append(" AND OBFUID = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("factoringUnitId"))));
			}

			logger.info("sql fact - " + lSql);
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<ObligationHostApiBean> obligationHostApiBeans = new ArrayList<>();
			while (lResultSet.next()) {
				ObligationHostApiBean obligationHostApiBean = new ObligationHostApiBean();

				obligationHostApiBean.setOBID(lResultSet.getBigDecimal("OBID"));
				obligationHostApiBean.setOBFUID(lResultSet.getBigDecimal("OBFUID"));
				obligationHostApiBean.setOBBDID(lResultSet.getBigDecimal("OBBDID"));
				obligationHostApiBean.setOBTXNENTITY(lResultSet.getString("OBTXNENTITY"));
				obligationHostApiBean.setOBTXNTYPE(lResultSet.getString("OBTXNTYPE"));
				obligationHostApiBean.setOBDATE(lResultSet.getDate("OBDATE"));
				obligationHostApiBean.setOBORIGINALDATE(lResultSet.getDate("OBORIGINALDATE"));
				obligationHostApiBean.setOBCURRENCY(lResultSet.getString("OBCURRENCY"));
				obligationHostApiBean.setOBAMOUNT(lResultSet.getBigDecimal("OBAMOUNT"));
				obligationHostApiBean.setOBORIGINALAMOUNT(lResultSet.getBigDecimal("OBORIGINALAMOUNT"));
				obligationHostApiBean.setOBTYPE(lResultSet.getString("OBTYPE"));
				obligationHostApiBean.setOBNARRATION(lResultSet.getString("OBNARRATION"));
				obligationHostApiBean.setOBSTATUS(lResultSet.getString("OBSTATUS"));
				obligationHostApiBean.setOBPFID(lResultSet.getBigDecimal("OBPFID"));
				obligationHostApiBean.setOBFILESEQNO(lResultSet.getBigDecimal("OBFILESEQNO"));
				obligationHostApiBean.setOBPAYDETAIL1(lResultSet.getString("OBPAYDETAIL1"));
				obligationHostApiBean.setOBPAYDETAIL2(lResultSet.getString("OBPAYDETAIL2"));
				obligationHostApiBean.setOBPAYDETAIL3(lResultSet.getString("OBPAYDETAIL3"));
				obligationHostApiBean.setOBPAYDETAIL4(lResultSet.getString("OBPAYDETAIL4"));
				obligationHostApiBean.setOBSETTLEDDATE(lResultSet.getDate("OBSETTLEDDATE"));
				obligationHostApiBean.setOBSETTLEDAMOUNT(lResultSet.getBigDecimal("OBSETTLEDAMOUNT"));
				obligationHostApiBean.setOBPAYMENTREFNO(lResultSet.getString("OBPAYMENTREFNO"));
				obligationHostApiBean.setOBRESPERRORCODE(lResultSet.getString("OBRESPERRORCODE"));
				obligationHostApiBean.setOBRESPREMARKS(lResultSet.getString("OBRESPREMARKS"));
				obligationHostApiBean.setOBOLDOBLIGATIONID(lResultSet.getBigDecimal("OBOLDOBLIGATIONID"));
				obligationHostApiBean.setOBSALESCATEGORY(lResultSet.getString("OBSALESCATEGORY"));
				obligationHostApiBean.setOBBILLID(lResultSet.getBigDecimal("OBBILLID"));
				obligationHostApiBean.setOBSETTLEMENTCLID(lResultSet.getBigDecimal("OBSETTLEMENTCLID"));
				obligationHostApiBean.setOBEXTENDEDDAYS(lResultSet.getBigDecimal("OBEXTENDEDDAYS"));
				obligationHostApiBean.setOBRECORDCREATOR(lResultSet.getBigDecimal("OBRECORDCREATOR"));
				obligationHostApiBean.setOBRECORDCREATETIME(lResultSet.getDate("OBRECORDCREATETIME"));
				obligationHostApiBean.setOBRECORDUPDATOR(lResultSet.getBigDecimal("OBRECORDUPDATOR"));
				obligationHostApiBean.setOBRECORDUPDATETIME(lResultSet.getDate("OBRECORDUPDATETIME"));
				obligationHostApiBean.setOBRECORDVERSION(lResultSet.getBigDecimal("OBRECORDVERSION"));
				obligationHostApiBean.setOBISUPFRONTOBLIG(lResultSet.getString("OBISUPFRONTOBLIG"));
				obligationHostApiBean.setOBISUPFRONT(lResultSet.getString("OBISUPFRONT"));

				obligationHostApiBeans.add(obligationHostApiBean);

			}

			obligationResponseBean.setMessage("Success");
			obligationResponseBean.setMessageCode("0");
			obligationResponseBean.setObligationHostApiBeans(obligationHostApiBeans);

		} catch (Exception e) {
			e.printStackTrace();
			obligationResponseBean.setMessage("Error");
			obligationResponseBean.setMessageCode("1");

		}

		return obligationResponseBean;

	}

	public List<ObligationExtensionHostApiBean> getObligationExtensionsDao(ExecutionContext pExecutionContext,
			BigDecimal obId) throws Exception {
		List<ObligationExtensionHostApiBean> obligationExtensionList = new ArrayList<>();
		logger.info("inside getFactoringUnitWatchDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM OBLIGATIONEXTENSIONS WHERE OEOBID = ").append(obId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				ObligationExtensionHostApiBean obligationExtensionHostApiBean = new ObligationExtensionHostApiBean();

				obligationExtensionHostApiBean.setOEOBID(lResultSet.getBigDecimal("OEOBID"));
				obligationExtensionHostApiBean.setOECREDITOBID(lResultSet.getBigDecimal("OECREDITOBID"));
				obligationExtensionHostApiBean.setOEPURCHASER(lResultSet.getString("OEPURCHASER"));
				obligationExtensionHostApiBean.setOEFINANCIER(lResultSet.getString("OEFINANCIER"));
				obligationExtensionHostApiBean.setOEOLDDATE(lResultSet.getDate("OEOLDDATE"));
				obligationExtensionHostApiBean.setOECURRENCY(lResultSet.getString("OECURRENCY"));
				obligationExtensionHostApiBean.setOEOLDAMOUNT(lResultSet.getBigDecimal("OEOLDAMOUNT"));
				obligationExtensionHostApiBean.setOENEWDATE(lResultSet.getDate("OENEWDATE"));
				obligationExtensionHostApiBean.setOEINTEREST(lResultSet.getBigDecimal("OEINTEREST"));
				obligationExtensionHostApiBean.setOEPENALTY(lResultSet.getBigDecimal("OEPENALTY"));
				obligationExtensionHostApiBean.setOEPENALTYRATE(lResultSet.getBigDecimal("OEPENALTYRATE"));
				obligationExtensionHostApiBean.setOENEWAMOUNT(lResultSet.getBigDecimal("OENEWAMOUNT"));
				obligationExtensionHostApiBean.setOESTATUS(lResultSet.getString("OESTATUS"));
				obligationExtensionHostApiBean.setOEREMARKS(lResultSet.getString("OEREMARKS"));
				obligationExtensionHostApiBean.setOERECORDCREATOR(lResultSet.getBigDecimal("OERECORDCREATOR"));
				obligationExtensionHostApiBean.setOERECORDCREATETIME(lResultSet.getDate("OERECORDCREATETIME"));
				obligationExtensionHostApiBean.setOERECORDUPDATOR(lResultSet.getBigDecimal("OERECORDUPDATOR"));
				obligationExtensionHostApiBean.setOERECORDUPDATETIME(lResultSet.getDate("OERECORDUPDATETIME"));
				obligationExtensionHostApiBean.setOEINTERESTRATE(lResultSet.getBigDecimal("OEINTERESTRATE"));
				obligationExtensionHostApiBean.setOETREDSCHARGE(lResultSet.getBigDecimal("OETREDSCHARGE"));
				obligationExtensionHostApiBean.setOECHARGEDATE(lResultSet.getDate("OECHARGEDATE"));
				obligationExtensionHostApiBean.setOESUBMITDATE(lResultSet.getDate("OESUBMITDATE"));
				obligationExtensionHostApiBean.setOEAPPROVEDATE(lResultSet.getDate("OEAPPROVEDATE"));
				obligationExtensionHostApiBean.setOEUPFRONTCHARGE(lResultSet.getString("OEUPFRONTCHARGE"));

				obligationExtensionList.add(obligationExtensionHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getObligationDetailsDao Response - ", e);

		}

		return obligationExtensionList;
	}

	public List<ObligationDetailsHostApiBean> getObligationDetailsDao(ExecutionContext pExecutionContext,
			BigDecimal factoringUnitId) throws Exception {
		List<ObligationDetailsHostApiBean> obligationDetailsHostApiBeans = new ArrayList<>();
		logger.info("inside getObligationDetailsDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM OBLIGATIONDETAILS WHERE OBDFUID = ").append(factoringUnitId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				ObligationDetailsHostApiBean obligationDetailsHostApiBean = new ObligationDetailsHostApiBean();

				obligationDetailsHostApiBean.setOBDID(lResultSet.getBigDecimal("OBDID"));
				obligationDetailsHostApiBean.setOBDFUID(lResultSet.getBigDecimal("OBDFUID"));
				obligationDetailsHostApiBean.setOBDDATE(lResultSet.getDate("OBDDATE"));
				obligationDetailsHostApiBean.setOBDTYPE(lResultSet.getString("OBDTYPE"));
				obligationDetailsHostApiBean.setOBDDEBITENTITY(lResultSet.getString("OBDDEBITENTITY"));
				obligationDetailsHostApiBean.setOBDCREDITENTITY(lResultSet.getString("OBDCREDITENTITY"));
				obligationDetailsHostApiBean.setOBDCURRENCY(lResultSet.getString("OBDCURRENCY"));
				obligationDetailsHostApiBean.setOBDAMOUNT(lResultSet.getBigDecimal("OBDAMOUNT"));
				obligationDetailsHostApiBean.setOBDREASONCODE(lResultSet.getString("OBDREASONCODE"));

				obligationDetailsHostApiBeans.add(obligationDetailsHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getObligationDetailsDao Response - ", e);

		}

		return obligationDetailsHostApiBeans;
	}

	public List<ObligationSplitsHostApiBean> getObligationSplitsDao(ExecutionContext pExecutionContext, BigDecimal obid)
			throws Exception {
		List<ObligationSplitsHostApiBean> obligationSplitsHostApiBeans = new ArrayList<>();
		logger.info("inside getObligationDetailsDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM OBLIGATIONSPLITS WHERE OBSOBID = ").append(obid);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				ObligationSplitsHostApiBean obligationSplitsHostApiBean = new ObligationSplitsHostApiBean();

				obligationSplitsHostApiBean.setOBSOBID(lResultSet.getBigDecimal("OBSOBID"));
				obligationSplitsHostApiBean.setOBSPARTNUMBER(lResultSet.getBigDecimal("OBSPARTNUMBER"));
				obligationSplitsHostApiBean.setOBSAMOUNT(lResultSet.getBigDecimal("OBSAMOUNT"));
				obligationSplitsHostApiBean.setOBSSTATUS(lResultSet.getString("OBSSTATUS"));
				obligationSplitsHostApiBean.setOBSPFID(lResultSet.getBigDecimal("OBSPFID"));
				obligationSplitsHostApiBean.setOBSFILESEQNO(lResultSet.getBigDecimal("OBSFILESEQNO"));
				obligationSplitsHostApiBean.setOBSSETTLEDDATE(lResultSet.getDate("OBSSETTLEDDATE"));
				obligationSplitsHostApiBean.setOBSSETTLEDAMOUNT(lResultSet.getBigDecimal("OBSSETTLEDAMOUNT"));
				obligationSplitsHostApiBean.setOBSPAYMENTREFNO(lResultSet.getString("OBSPAYMENTREFNO"));
				obligationSplitsHostApiBean.setOBSRESPERRORCODE(lResultSet.getString("OBSRESPERRORCODE"));
				obligationSplitsHostApiBean.setOBSRESPREMARKS(lResultSet.getString("OBSRESPREMARKS"));
				obligationSplitsHostApiBean.setOBSPAYMENTSETTLOR(lResultSet.getString("OBSPAYMENTSETTLOR"));
				obligationSplitsHostApiBean.setOBSSETTLORPROCESSED(lResultSet.getString("OBSSETTLORPROCESSED"));
				obligationSplitsHostApiBean.setOBSRECORDUPDATOR(lResultSet.getBigDecimal("OBSRECORDUPDATOR"));
				obligationSplitsHostApiBean.setOBSRECORDUPDATETIME(lResultSet.getDate("OBSRECORDUPDATETIME"));
				obligationSplitsHostApiBean.setOBSRECORDVERSION(lResultSet.getBigDecimal("OBSRECORDVERSION"));
				obligationSplitsHostApiBean.setOBSOSRID(lResultSet.getBigDecimal("OBSOSRID"));

				obligationSplitsHostApiBeans.add(obligationSplitsHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getObligationSplitsDao Response - ", e);

		}

		return obligationSplitsHostApiBeans;
	}

	public List<ObligationModificationDetailsHostApiBean> getObligationModificationDao(
			ExecutionContext pExecutionContext, BigDecimal obid) throws Exception {
		List<ObligationModificationDetailsHostApiBean> modificationDetailsHostApiBeans = new ArrayList<>();
		logger.info("inside getObligationModificationDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM OBLIGATIONSMODIDETAILS WHERE OMDOBID = ").append(obid);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				ObligationModificationDetailsHostApiBean obligationModificationDetailsHostApiBean = new ObligationModificationDetailsHostApiBean();

				obligationModificationDetailsHostApiBean.setOMDID(lResultSet.getBigDecimal("OMDID"));
				obligationModificationDetailsHostApiBean.setOMDOMRID(lResultSet.getBigDecimal("OMDOMRID"));
				obligationModificationDetailsHostApiBean.setOMDOBID(lResultSet.getBigDecimal("OMDOBID"));
				obligationModificationDetailsHostApiBean.setOMDPARTNUMBER(lResultSet.getBigDecimal("OMDPARTNUMBER"));
				obligationModificationDetailsHostApiBean.setOMDTXNTYPE(lResultSet.getString("OMDTXNTYPE"));
				obligationModificationDetailsHostApiBean.setOMDORIGAMOUNT(lResultSet.getBigDecimal("OMDORIGAMOUNT"));
				obligationModificationDetailsHostApiBean.setOMDORIGDATE(lResultSet.getDate("OMDORIGDATE"));
				obligationModificationDetailsHostApiBean.setOMDORIGSTATUS(lResultSet.getString("OMDORIGSTATUS"));
				obligationModificationDetailsHostApiBean
						.setOMDREVISEDAMOUNT(lResultSet.getBigDecimal("OMDREVISEDAMOUNT"));
				obligationModificationDetailsHostApiBean.setOMDREVISEDDATE(lResultSet.getDate("OMDREVISEDDATE"));
				obligationModificationDetailsHostApiBean.setOMDREVISEDSTATUS(lResultSet.getString("OMDREVISEDSTATUS"));
				obligationModificationDetailsHostApiBean
						.setOMDPAYMENTSETTLOR(lResultSet.getString("OMDPAYMENTSETTLOR"));
				obligationModificationDetailsHostApiBean.setOMDREMARKS(lResultSet.getString("OMDREMARKS"));
				obligationModificationDetailsHostApiBean.setOMDPAYMENTREFNO(lResultSet.getString("OMDPAYMENTREFNO"));
				obligationModificationDetailsHostApiBean
						.setOMDRECORDCREATOR(lResultSet.getBigDecimal("OMDRECORDCREATOR"));
				obligationModificationDetailsHostApiBean
						.setOMDRECORDCREATETIME(lResultSet.getDate("OMDRECORDCREATETIME"));
				obligationModificationDetailsHostApiBean
						.setOMDRECORDUPDATOR(lResultSet.getBigDecimal("OMDRECORDUPDATOR"));
				obligationModificationDetailsHostApiBean
						.setOMDRECORDUPDATETIME(lResultSet.getDate("OMDRECORDUPDATETIME"));
				obligationModificationDetailsHostApiBean
						.setOMDRECORDVERSION(lResultSet.getBigDecimal("OMDRECORDVERSION"));

				modificationDetailsHostApiBeans.add(obligationModificationDetailsHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getObligationSplitsDao Response - ", e);

		}

		return modificationDetailsHostApiBeans;
	}

	public List<PurchaserSupplierLinkWorkFlowBean> findListForMapWorkFlow(ExecutionContext pExecutionContext,
			Map<String, Object> inputMap) throws Exception {

		List<Map<String, Object>> lWorkFlowMaps = new ArrayList<Map<String, Object>>();
		DBHelper lDBHelper = DBHelper.getInstance();
		String lSql = "SELECT * FROM PurchaserSupplierLinkWorkFlow WHERE PLWSUPPLIER = "
				+ lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))) + " AND PLWPURCHASER = "
				+ lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode")))
				+ " ORDER BY PLWSTATUSUPDATETIME DESC, PLWId DESC";
		List<PurchaserSupplierLinkWorkFlowBean> lWorkFlows = purchaserSupplierLinkWorkFlowDAO
				.findListFromSql(pExecutionContext.getConnection(), (String) lSql, 0);

		return lWorkFlows;

	}

	public PurchaserSupplierLimitUtilizationResponseBean getPurSuppLimitUtilizationDao(
			ExecutionContext pExecutionContext, Map<String, Object> inputMap) throws Exception {
		List<PurchaserSupplierLimitUtilizationHostApiBean> purchaserSupplierLimitUtilizationHostApiBeans = new ArrayList<>();
		logger.info("inside getPurSuppLimitUtilizationDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		PurchaserSupplierLimitUtilizationResponseBean purchaserSupplierLimitUtilizationResponse = new PurchaserSupplierLimitUtilizationResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM PURCSUPPLIMITUTILIZATIONS WHERE PSLUPURCHASER = ")
					.append(lDBHelper.formatString(String.valueOf(inputMap.get("buyerCode"))));

			if (inputMap.get("sellerCode") != null
					&& !StringUtils.isEmpty(String.valueOf(inputMap.get("sellerCode")))) {
				lSql.append(" AND PSLUSUPPLIER = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("sellerCode"))));
			}
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				PurchaserSupplierLimitUtilizationHostApiBean purchaserSupplierLimitUtilizationHostApiBean = new PurchaserSupplierLimitUtilizationHostApiBean();

				purchaserSupplierLimitUtilizationHostApiBean.setPSLUID(lResultSet.getBigDecimal("PSLUID"));
				purchaserSupplierLimitUtilizationHostApiBean.setPSLULIMIT(lResultSet.getBigDecimal("PSLULIMIT"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLULIMITUTILIZED(lResultSet.getBigDecimal("PSLULIMITUTILIZED"));
				purchaserSupplierLimitUtilizationHostApiBean.setPSLUPURCHASER(lResultSet.getString("PSLUPURCHASER"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLURECORDCREATETIME(lResultSet.getDate("PSLURECORDCREATETIME"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLURECORDCREATOR(lResultSet.getBigDecimal("PSLURECORDCREATOR"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLURECORDUPDATETIME(lResultSet.getDate("PSLURECORDUPDATETIME"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLURECORDUPDATOR(lResultSet.getBigDecimal("PSLURECORDUPDATOR"));
				purchaserSupplierLimitUtilizationHostApiBean
						.setPSLURECORDVERSION(lResultSet.getBigDecimal("PSLURECORDVERSION"));
				purchaserSupplierLimitUtilizationHostApiBean.setPSLUSTATUS(lResultSet.getString("PSLUSTATUS"));
				purchaserSupplierLimitUtilizationHostApiBean.setPSLUSUPPLIER(lResultSet.getString("PSLUSUPPLIER"));

				purchaserSupplierLimitUtilizationHostApiBeans.add(purchaserSupplierLimitUtilizationHostApiBean);

			}

			purchaserSupplierLimitUtilizationResponse.setMessage("Success");
			purchaserSupplierLimitUtilizationResponse.setMessageCode("0");
			purchaserSupplierLimitUtilizationResponse
					.setPurchaserSupplierLimitUtilizationHostApiBeans(purchaserSupplierLimitUtilizationHostApiBeans);

		} catch (Exception e) {
			logger.error("Error in fetching user maker checker Response - ", e);
			purchaserSupplierLimitUtilizationResponse.setMessage("Error");
			purchaserSupplierLimitUtilizationResponse.setMessageCode("1");

		}

		return purchaserSupplierLimitUtilizationResponse;
	}

	public BillResponseBean getBillsDao(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside getBillsDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;
		BillResponseBean billResponseBean = new BillResponseBean();

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();

			if (inputMap.get("billId") != null && String.valueOf(inputMap.get("billId")).length() > 0) {
				lSql.append("SELECT * FROM BILLS WHERE BILID = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("billId"))));
			} else {
				lSql.append("SELECT * FROM BILLS WHERE BILENTITY = ")
						.append(lDBHelper.formatString(String.valueOf(inputMap.get("entityCode"))));
			}

			logger.info("sql fact - " + lSql);
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			List<BillsHostApiBean> billHostApiBean = new ArrayList<>();
			while (lResultSet.next()) {
				BillsHostApiBean billsHostApiBean = new BillsHostApiBean();
				logger.info("lResultSet from DB - " + lResultSet.getString("BILID"));

				billsHostApiBean.setBILID(lResultSet.getBigDecimal("BILID"));
				billsHostApiBean.setBILBILLNUMBER(lResultSet.getString("BILBILLNUMBER"));
				billsHostApiBean.setBILBILLYEARMONTH(lResultSet.getDate("BILBILLYEARMONTH"));
				billsHostApiBean.setBILBILLDATE(lResultSet.getDate("BILBILLDATE"));
				billsHostApiBean.setBILENTITY(lResultSet.getString("BILENTITY"));
				billsHostApiBean.setBILENTNAME(lResultSet.getString("BILENTNAME"));
				billsHostApiBean.setBILENTGSTN(lResultSet.getString("BILENTGSTN"));
				billsHostApiBean.setBILENTPAN(lResultSet.getString("BILENTPAN"));
				billsHostApiBean.setBILENTLINE1(lResultSet.getString("BILENTLINE1"));
				billsHostApiBean.setBILENTLINE2(lResultSet.getString("BILENTLINE2"));
				billsHostApiBean.setBILENTLINE3(lResultSet.getString("BILENTLINE3"));
				billsHostApiBean.setBILENTCOUNTRY(lResultSet.getString("BILENTCOUNTRY"));
				billsHostApiBean.setBILENTSTATE(lResultSet.getString("BILENTSTATE"));
				billsHostApiBean.setBILENTDISTRICT(lResultSet.getString("BILENTDISTRICT"));
				billsHostApiBean.setBILENTCITY(lResultSet.getString("BILENTCITY"));
				billsHostApiBean.setBILENTZIPCODE(lResultSet.getString("BILENTZIPCODE"));
				billsHostApiBean.setBILENTSALUTATION(lResultSet.getString("BILENTSALUTATION"));
				billsHostApiBean.setBILENTFIRSTNAME(lResultSet.getString("BILENTFIRSTNAME"));
				billsHostApiBean.setBILENTMIDDLENAME(lResultSet.getString("BILENTMIDDLENAME"));
				billsHostApiBean.setBILENTLASTNAME(lResultSet.getString("BILENTLASTNAME"));
				billsHostApiBean.setBILENTEMAIL(lResultSet.getString("BILENTEMAIL"));
				billsHostApiBean.setBILENTTELEPHONE(lResultSet.getString("BILENTTELEPHONE"));
				billsHostApiBean.setBILENTMOBILE(lResultSet.getString("BILENTMOBILE"));
				billsHostApiBean.setBILENTFAX(lResultSet.getString("BILENTFAX"));
				billsHostApiBean.setBILTREDSNAME(lResultSet.getString("BILTREDSNAME"));
				billsHostApiBean.setBILTREDSGSTN(lResultSet.getString("BILTREDSGSTN"));
				billsHostApiBean.setBILTREDSLINE1(lResultSet.getString("BILTREDSLINE1"));
				billsHostApiBean.setBILTREDSLINE2(lResultSet.getString("BILTREDSLINE2"));
				billsHostApiBean.setBILTREDSLINE3(lResultSet.getString("BILTREDSLINE3"));
				billsHostApiBean.setBILTREDSCOUNTRY(lResultSet.getString("BILTREDSCOUNTRY"));
				billsHostApiBean.setBILTREDSSTATE(lResultSet.getString("BILTREDSSTATE"));
				billsHostApiBean.setBILTREDSDISTRICT(lResultSet.getString("BILTREDSDISTRICT"));
				billsHostApiBean.setBILTREDSCITY(lResultSet.getString("BILTREDSCITY"));
				billsHostApiBean.setBILTREDSZIPCODE(lResultSet.getString("BILTREDSZIPCODE"));
				billsHostApiBean.setBILTREDSEMAIL(lResultSet.getString("BILTREDSEMAIL"));
				billsHostApiBean.setBILTREDSTELEPHONE(lResultSet.getString("BILTREDSTELEPHONE"));
				billsHostApiBean.setBILTREDSMOBILE(lResultSet.getString("BILTREDSMOBILE"));
				billsHostApiBean.setBILTREDSFAX(lResultSet.getString("BILTREDSFAX"));
				billsHostApiBean.setBILTREDSPAN(lResultSet.getString("BILTREDSPAN"));
				billsHostApiBean.setBILTREDSCIN(lResultSet.getString("BILTREDSCIN"));
				billsHostApiBean.setBILTREDSNATUREOFTRANS(lResultSet.getString("BILTREDSNATUREOFTRANS"));
				billsHostApiBean.setBILTREDSSACCODE(lResultSet.getString("BILTREDSSACCODE"));
				billsHostApiBean.setBILTREDSSACDESC(lResultSet.getString("BILTREDSSACDESC"));
				billsHostApiBean.setBILCHARGEAMOUNT(lResultSet.getBigDecimal("BILCHARGEAMOUNT"));
				billsHostApiBean.setBILFUAMOUNT(lResultSet.getBigDecimal("BILFUAMOUNT"));
				billsHostApiBean.setBILCGST(lResultSet.getBigDecimal("BILCGST"));
				billsHostApiBean.setBILSGST(lResultSet.getBigDecimal("BILSGST"));
				billsHostApiBean.setBILIGST(lResultSet.getBigDecimal("BILIGST"));
				billsHostApiBean.setBILCGSTSURCHARGE(lResultSet.getBigDecimal("BILCGSTSURCHARGE"));
				billsHostApiBean.setBILSGSTSURCHARGE(lResultSet.getBigDecimal("BILSGSTSURCHARGE"));
				billsHostApiBean.setBILIGSTSURCHARGE(lResultSet.getBigDecimal("BILIGSTSURCHARGE"));
				billsHostApiBean.setBILCGSTVALUE(lResultSet.getBigDecimal("BILCGSTVALUE"));
				billsHostApiBean.setBILSGSTVALUE(lResultSet.getBigDecimal("BILSGSTVALUE"));
				billsHostApiBean.setBILIGSTVALUE(lResultSet.getBigDecimal("BILIGSTVALUE"));
				billsHostApiBean.setBILRECORDCREATOR(lResultSet.getBigDecimal("BILRECORDCREATOR"));
				billsHostApiBean.setBILRECORDCREATETIME(lResultSet.getDate("BILRECORDCREATETIME"));
				billsHostApiBean.setBILRECORDVERSION(lResultSet.getBigDecimal("BILRECORDVERSION"));
				billsHostApiBean.setBILBILLINGTYPE(lResultSet.getString("BILBILLINGTYPE"));
				billsHostApiBean.setBILBILLEDFORENTITY(lResultSet.getString("BILBILLEDFORENTITY"));

				billHostApiBean.add(billsHostApiBean);

			}

			billResponseBean.setMessage("Success");
			billResponseBean.setMessageCode("0");
			billResponseBean.setBillsHostApiBeans(billHostApiBean);

		} catch (Exception e) {
			logger.error("Error in fetching factoring Response - ", e);
			billResponseBean.setMessage("Error");
			billResponseBean.setMessageCode("1");
		}

		return billResponseBean;
	}

	public List<BillFactoringUnitHostApiBean> getBillFactoringDao(ExecutionContext pExecutionContext, BigDecimal billId)
			throws Exception {
		List<BillFactoringUnitHostApiBean> billFactoringUnitList = new ArrayList<>();
		logger.info("inside getBillFactoringInstrumentDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append(
					"SELECT FUID,FUAMOUNT,FUEXTBILLID1,FUEXTBILLID2 FROM FACTORINGUNITS WHERE FUCOSTBEARERBILLID = ")
					.append(billId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				BillFactoringUnitHostApiBean billFactoringUnitHostApiBean = new BillFactoringUnitHostApiBean();

				billFactoringUnitHostApiBean.setFactoringUnitId(lResultSet.getBigDecimal("FUID"));
				billFactoringUnitHostApiBean.setFuAmount(lResultSet.getBigDecimal("FUAMOUNT"));
				billFactoringUnitHostApiBean.setFuExtBillId1(lResultSet.getBigDecimal("FUEXTBILLID1"));
				billFactoringUnitHostApiBean.setFuExtBillId2(lResultSet.getBigDecimal("FUEXTBILLID2"));
				// set instrument child
				billFactoringUnitHostApiBean.setBillFactoringInstrumentList(getBillFactoringInstrumentDao(
						pExecutionContext, billFactoringUnitHostApiBean.getFactoringUnitId()));

				// setbillext1 and 2
				Map<String, Object> inputMap1 = new HashMap<String, Object>();
				inputMap1.put("billId", billFactoringUnitHostApiBean.getBillExt1());
				billFactoringUnitHostApiBean
						.setBillExt1(getBillsDao(pExecutionContext, inputMap1).getBillsHostApiBeans());

				Map<String, Object> inputMap2 = new HashMap<String, Object>();
				inputMap2.put("billId", billFactoringUnitHostApiBean.getBillExt2());
				billFactoringUnitHostApiBean
						.setBillExt2(getBillsDao(pExecutionContext, inputMap1).getBillsHostApiBeans());

				billFactoringUnitList.add(billFactoringUnitHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getBillFactoringDao Response - ", e);

		}

		return billFactoringUnitList;
	}

	public List<BillFactoringInstrumentHostApiBean> getBillFactoringInstrumentDao(ExecutionContext pExecutionContext,
			BigDecimal factoringUnitId) throws Exception {
		List<BillFactoringInstrumentHostApiBean> billFactoringUnitInstrumentList = new ArrayList<>();
		logger.info("inside getBillFactoringInstrumentDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT INID,INNETAMOUNT FROM INSTRUMENTS WHERE INFUID = ").append(factoringUnitId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				BillFactoringInstrumentHostApiBean billFactoringUnitInstrumentHostApiBean = new BillFactoringInstrumentHostApiBean();

				billFactoringUnitInstrumentHostApiBean.setInstrumentId(lResultSet.getBigDecimal("INID"));
				billFactoringUnitInstrumentHostApiBean.setNetAmount(lResultSet.getBigDecimal("INNETAMOUNT"));

				billFactoringUnitInstrumentList.add(billFactoringUnitInstrumentHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getBillFactoringInstrumentDao Response - ", e);

		}

		return billFactoringUnitInstrumentList;
	}

	public List<BillsRegistrationChargesHostApiBean> getBillRegistrationChargesDao(ExecutionContext pExecutionContext,
			BigDecimal billId) throws Exception {
		List<BillsRegistrationChargesHostApiBean> billsRegistrationChargesHostApiBeans = new ArrayList<>();
		logger.info("inside getBillRegistrationChargesDao");
		Connection lConnection = pExecutionContext.getConnection();
		Statement lStatement = null;
		ResultSet lResultSet = null;

		try {

			lStatement = lConnection.createStatement();
			StringBuilder lSql = new StringBuilder();
			DBHelper lDBHelper = DBHelper.getInstance();
			lSql.append("SELECT * FROM REGISTRATIONCHARGES WHERE RCBILLID = ").append(billId);
			logger.info("rmquery - " + lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			logger.info("Result Set Size - " + lResultSet.getFetchSize());

			while (lResultSet.next()) {
				BillsRegistrationChargesHostApiBean billsRegistrationChargesHostApiBean = new BillsRegistrationChargesHostApiBean();

				billsRegistrationChargesHostApiBean.setRCID(lResultSet.getBigDecimal("RCID"));
				billsRegistrationChargesHostApiBean.setRCENTITYCODE(lResultSet.getString("RCENTITYCODE"));
				billsRegistrationChargesHostApiBean.setRCENTITYTYPE(lResultSet.getString("RCENTITYTYPE"));
				billsRegistrationChargesHostApiBean.setRCCHARGETYPE(lResultSet.getString("RCCHARGETYPE"));
				billsRegistrationChargesHostApiBean.setRCEFFECTIVEDATE(lResultSet.getDate("RCEFFECTIVEDATE"));
				billsRegistrationChargesHostApiBean.setRCCHARGEAMOUNT(lResultSet.getBigDecimal("RCCHARGEAMOUNT"));
				billsRegistrationChargesHostApiBean.setRCREQUESTTYPE(lResultSet.getString("RCREQUESTTYPE"));
				billsRegistrationChargesHostApiBean.setRCEXTENDEDDATE(lResultSet.getDate("RCEXTENDEDDATE"));
				billsRegistrationChargesHostApiBean.setRCEXTENSIONCOUNT(lResultSet.getBigDecimal("RCEXTENSIONCOUNT"));
				billsRegistrationChargesHostApiBean.setRCPAYMENTDATE(lResultSet.getDate("RCPAYMENTDATE"));
				billsRegistrationChargesHostApiBean.setRCPAYMENTAMOUNT(lResultSet.getBigDecimal("RCPAYMENTAMOUNT"));
				billsRegistrationChargesHostApiBean.setRCPAYMENTREFRENCE(lResultSet.getString("RCPAYMENTREFRENCE"));
				billsRegistrationChargesHostApiBean.setRCBILLEDENTITYCODE(lResultSet.getString("RCBILLEDENTITYCODE"));
				billsRegistrationChargesHostApiBean
						.setRCBILLEDENTITYCLID(lResultSet.getBigDecimal("RCBILLEDENTITYCLID"));
				billsRegistrationChargesHostApiBean.setRCREMARKS(lResultSet.getString("RCREMARKS"));
				billsRegistrationChargesHostApiBean.setRCSUPPORTINGDOC(lResultSet.getString("RCSUPPORTINGDOC"));
				billsRegistrationChargesHostApiBean.setRCMAKERAUID(lResultSet.getBigDecimal("RCMAKERAUID"));
				billsRegistrationChargesHostApiBean.setRCMAKERTIMESTAMP(lResultSet.getDate("RCMAKERTIMESTAMP"));
				billsRegistrationChargesHostApiBean.setRCCHECKERAUID(lResultSet.getBigDecimal("RCCHECKERAUID"));
				billsRegistrationChargesHostApiBean.setRCCHECKERTIMESTAMP(lResultSet.getDate("RCCHECKERTIMESTAMP"));
				billsRegistrationChargesHostApiBean.setRCAPPROVALSTATUS(lResultSet.getString("RCAPPROVALSTATUS"));
				billsRegistrationChargesHostApiBean.setRCRECORDCREATOR(lResultSet.getBigDecimal("RCRECORDCREATOR"));
				billsRegistrationChargesHostApiBean.setRCRECORDCREATETIME(lResultSet.getDate("RCRECORDCREATETIME"));
				billsRegistrationChargesHostApiBean.setRCRECORDUPDATOR(lResultSet.getBigDecimal("RCRECORDUPDATOR"));
				billsRegistrationChargesHostApiBean.setRCRECORDUPDATETIME(lResultSet.getDate("RCRECORDUPDATETIME"));
				billsRegistrationChargesHostApiBean.setRCRECORDVERSION(lResultSet.getBigDecimal("RCRECORDVERSION"));
				billsRegistrationChargesHostApiBean.setRCPREVEXTENDEDDATE(lResultSet.getDate("RCPREVEXTENDEDDATE"));
				billsRegistrationChargesHostApiBean.setRCBILLID(lResultSet.getBigDecimal("RCBILLID"));

				billsRegistrationChargesHostApiBeans.add(billsRegistrationChargesHostApiBean);

			}

		} catch (Exception e) {
			logger.error("Error in fetching getBillFactoringInstrumentDao Response - ", e);

		}

		return billsRegistrationChargesHostApiBeans;
	}

}
