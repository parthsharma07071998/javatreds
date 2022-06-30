package com.xlx.treds.hostapi.bo;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Instrument;
import javax.ws.rs.core.Context;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkWorkFlowBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.auction.bo.PurchaserSupplierLinkBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.hostapi.bean.AggregatorPurchaserMapHostApiBean;
import com.xlx.treds.hostapi.bean.AggregatorPurchaserMapResponseBean;
import com.xlx.treds.hostapi.bean.BillResponseBean;
import com.xlx.treds.hostapi.bean.BillsHostApiBean;
import com.xlx.treds.hostapi.bean.BuyerSellerInpurBean;
import com.xlx.treds.hostapi.bean.BuyerSellerLinkBean;
import com.xlx.treds.hostapi.bean.FactoringUnitHostApiBean;
import com.xlx.treds.hostapi.bean.FactoringUnitResponseBean;
import com.xlx.treds.hostapi.bean.InstrumentHostApiBean;
import com.xlx.treds.hostapi.bean.InstrumentResponseBean;
import com.xlx.treds.hostapi.bean.ObligationHostApiBean;
import com.xlx.treds.hostapi.bean.ObligationResponseBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLimitUtilizationHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLimitUtilizationResponseBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLinkHostApiBean;
import com.xlx.treds.hostapi.bean.PurchaserSupplierLinkResponseBean;
import com.xlx.treds.hostapi.bean.UserHostApiBean;
import com.xlx.treds.hostapi.bean.UserHostApiRmIdMapping;
import com.xlx.treds.hostapi.bean.UserResponseBean;
import com.xlx.treds.hostapi.dao.HostApiDao;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentWorkFlowBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class HostApiBo {

	private static Logger logger = Logger.getLogger(HostApiBo.class);

	private PurchaserSupplierLinkBO purchaserSupplierLinkBO;
	private HostApiDao hostApiDao;
	private BeanMeta instrumentBeanMeta;
	private GenericDAO<InstrumentWorkFlowBean> instrumentWorkFlowBeanDao;
	private InstrumentBO instrumentBO;
	private GenericDAO<AppUserBean> appUserDAO;
	private BeanMeta factoringUnitBeanMeta;
	private FactoringUnitBO factoringUnitBO;
	private BeanMeta purchaserSupplierLinkBeanMeta;
	private BeanMeta obligationBeanMeta;
	private ObligationBO obligationBO;
	private GenericDAO<InstrumentBean> instrumentDAO;

	public HostApiBo() {
		purchaserSupplierLinkBO = new PurchaserSupplierLinkBO();
		hostApiDao = new HostApiDao();
		instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
		// instrumentResponse =
		// BeanMetaFactory.getInstance().getBeanMeta(InstrumentResponseBean.class);
		instrumentWorkFlowBeanDao = new GenericDAO<InstrumentWorkFlowBean>(InstrumentWorkFlowBean.class);
		instrumentBO = new InstrumentBO();
		appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
		factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
		factoringUnitBO = new FactoringUnitBO();
		purchaserSupplierLinkBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class);
		obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
		obligationBO = new ObligationBO();
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
	}

	public String getPurchaserSupplierBO(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {
		logger.info("Inside getPurchaserSupplierBO");

		PurchaserSupplierLinkResponseBean purchaserSupplierLinkBean = hostApiDao.getPurchaserSupplier(pExecutionContext,
				inputMap);

		if (purchaserSupplierLinkBean != null
				&& purchaserSupplierLinkBean.getPurchaserSupplierLinkHostApiBeans() != null
				&& purchaserSupplierLinkBean.getPurchaserSupplierLinkHostApiBeans().size() > 0) {

			for (PurchaserSupplierLinkHostApiBean currentPurchaserSupplier : purchaserSupplierLinkBean
					.getPurchaserSupplierLinkHostApiBeans()) {
				// set workflow data
				currentPurchaserSupplier
						.setPurchaserSupplierWorkFlow(hostApiDao.getPurchaserSupplierWorkFlow(pExecutionContext,
								currentPurchaserSupplier.getPSLPURCHASER(), currentPurchaserSupplier.getPSLSUPPLIER()));

				// set PSLSUPPLIERPURCHASERREF and PSLPURCHASERSUPPLIERREF comma
				// separated
				currentPurchaserSupplier.setSupplierPurchaserRef(
						Arrays.asList(currentPurchaserSupplier.getPSLSUPPLIERPURCHASERREF().split(",")));

				currentPurchaserSupplier
						.setPurchaserSupplierRef(Arrays.asList(currentPurchaserSupplier.getPSLPURCHASERSUPPLIERREF()));

				currentPurchaserSupplier.setCapRate(hostApiDao.getPurchaserSupplierCapRateDao(pExecutionContext,
						currentPurchaserSupplier.getPSLPURCHASER()));
			}

		} else {
			purchaserSupplierLinkBean.setMessage("Error - No Record Found");
			purchaserSupplierLinkBean.setMessageCode("1");
		}

		return new JsonBuilder(purchaserSupplierLinkBean).toString();
	}

	public String getInstrumentBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap) throws Exception {

		logger.info("Inside getInstrumentBo");
		// List<InstrumentBean> instrumentData =
		// hostApiDao.getInstrumentDataDao(pExecutionContext, inputMap);
		// logger.info("Size of InstrumentData - " +
		// instrumentData.size());
		// List<Map<String, Object>> instrumentResponse = new
		// ArrayList<Map<String, Object>>();
		// int counter = 0;
		// Map<String, List<Map<String, Object>>> newMap = new HashMap<>();
		// for (InstrumentBean instrument : instrumentData) {
		// instrument.populateNonDatabaseFields();
		// instrument.populateDatabaseFields();
		// Map<String, Object> instrumentBeanMap =
		// instrumentBeanMeta.formatAsMap(instrument, null, null, false,
		// false);
		//
		// List<InstrumentWorkFlowBean> getInstrumentBeanWorkFlowData =
		// hostApiDao
		// .getInstrumentWorkFLowDao(pExecutionContext, instrument.getId());
		// List<Map<String, Object>> instrumentWorkFlowMap = new
		// ArrayList<Map<String, Object>>();
		// for (InstrumentWorkFlowBean instrumentWorkFlowBean :
		// getInstrumentBeanWorkFlowData) {
		// instrumentWorkFlowMap.add(instrumentWorkFlowBeanDao.getBeanMeta().formatAsMap(instrumentWorkFlowBean,
		// null, null, true, true));
		// }
		// instrumentBeanMap.put("workFlows", instrumentWorkFlowMap);
		//
		// logger.info("instrumentBeanMap - " +
		// instrumentBeanMap.get("id"));
		//
		// instrumentResponse.add(instrumentBeanMap);
		// }
		// newMap.put("instrumentResponse", instrumentResponse);
		//
		// return new JsonBuilder(instrumentResponse).toString();

		InstrumentResponseBean instrumentResponseBean = hostApiDao.getInstrumentData(pExecutionContext, inputMap);

		// Map<String, Object> instrumentBeanMap =
		// instrumentResponse.formatAsMap(instrumentResponseBean, null, null,
		// false, false);
		//
		// System.out.print("after Map Conversion - " +
		// instrumentBeanMap.size());

		if (instrumentResponseBean != null && instrumentResponseBean.getInstrumentBean() != null
				&& instrumentResponseBean.getInstrumentBean().size() > 0) {
			for (InstrumentHostApiBean currentInstrument : instrumentResponseBean.getInstrumentBean()) {
				currentInstrument.setInstrumentWorkFlowList(
						hostApiDao.getInstrumentWorkFlow(pExecutionContext, currentInstrument.getINID().longValue()));
				if (currentInstrument.getINCFDATA() != null && currentInstrument.getINCFDATA().length() > 0) {
					JsonSlurper lJsonSlurper = new JsonSlurper();
					Map<String, Object> customFieldMap = (Map<String, Object>) lJsonSlurper
							.parseText(currentInstrument.getINCFDATA());
					currentInstrument.setCustomFieldMap(customFieldMap);
				}

			}
		} else {
			instrumentResponseBean.setMessage("Error - No Record Found");
			instrumentResponseBean.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(instrumentResponseBean).toString();

		return jsonResponse;

	}

	public String getUserBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap) throws Exception {

		logger.info("Inside getUserBo");

		UserResponseBean userResponseBean = hostApiDao.getUserData(pExecutionContext, inputMap);

		if (userResponseBean != null && userResponseBean.getUserHostApiBean() != null
				&& userResponseBean.getUserHostApiBean().size() > 0) {
			JsonSlurper lJsonSlurper = new JsonSlurper();
			for (UserHostApiBean currentUser : userResponseBean.getUserHostApiBean()) {

				if (currentUser.getAUOTHERSETTINGS() != null && currentUser.getAUOTHERSETTINGS().length() > 0) {

					Map<String, Object> otherSettingMap = (Map<String, Object>) lJsonSlurper
							.parseText(currentUser.getAUOTHERSETTINGS());
					currentUser.setOtherSettings(otherSettingMap);

				}

				if (currentUser.getAUSECURITYSETTINGS() != null && currentUser.getAUSECURITYSETTINGS().length() > 0) {
					Map<String, Object> securitySetting = (Map<String, Object>) lJsonSlurper
							.parseText(currentUser.getAUSECURITYSETTINGS());
					logger.info("securitySetting -- " + securitySetting);
					currentUser.setSecuritySettings(securitySetting);
				}

				if (currentUser.getAUCHECKERLEVELSETTING() != null
						&& currentUser.getAUCHECKERLEVELSETTING().length() > 0) {
					List<Map<String, Object>> checkerLevelSetting = (List<Map<String, Object>>) lJsonSlurper
							.parseText(currentUser.getAUCHECKERLEVELSETTING());
					logger.info("securitySetting -- " + checkerLevelSetting);
					currentUser.setCheckerLevelSettings(checkerLevelSetting);
				}

				// rmid logic
				if (currentUser.getAURMIDS() != null && currentUser.getAURMIDS().length() > 0) {

					List<UserHostApiRmIdMapping> rmList = hostApiDao.getRMMappingDao(pExecutionContext,
							currentUser.getAURMIDS().replaceAll("\\[", "").replaceAll("\\]", ""));

					currentUser.setRmMappingList(rmList);

				}

				// userSettings
				if (currentUser.getAUUSERLIMITS() != null && currentUser.getAUUSERLIMITS().length() > 0) {
					Map<String, Object> userLimits = (Map<String, Object>) lJsonSlurper
							.parseText(currentUser.getAUUSERLIMITS());
					logger.info("securitySetting -- " + userLimits);
					currentUser.setUserLimits(userLimits);
					;
				}

				// set maker checker map
				currentUser.setMakerCheckerMap(hostApiDao.getUserChecker(pExecutionContext, currentUser.getAUID()));

			}
		} else {
			userResponseBean.setMessage("Error - No Record Found");
			userResponseBean.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(userResponseBean).toString();

		return jsonResponse;

	}

	public String getAggregatorPurchaserBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside aggregatorpurchaserbo");

		AggregatorPurchaserMapResponseBean aggregatorPurchaserMapResponseBean = hostApiDao
				.getAggregatorPurchaserMap(pExecutionContext, inputMap);

		if (aggregatorPurchaserMapResponseBean != null
				&& aggregatorPurchaserMapResponseBean.getAggregatorPurchaserMap() != null
				&& aggregatorPurchaserMapResponseBean.getAggregatorPurchaserMap().size() > 0) {
			// some business logic if required
		} else {
			aggregatorPurchaserMapResponseBean.setMessage("Error - No record found");
			aggregatorPurchaserMapResponseBean.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(aggregatorPurchaserMapResponseBean).toString();

		return jsonResponse;

	}

	public String getFactoringUnitBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {

		logger.info("inside getFactoringUnitBo");

		FactoringUnitResponseBean factoringUnitResponseBean = hostApiDao.getFactoringUnitDao(pExecutionContext,
				inputMap);

		if (factoringUnitResponseBean != null && factoringUnitResponseBean.getFactoringUnitList() != null
				&& factoringUnitResponseBean.getFactoringUnitList().size() > 0) {
			// some business logic if required

			for (FactoringUnitHostApiBean currentFactoringUnit : factoringUnitResponseBean.getFactoringUnitList()) {

				currentFactoringUnit.setFactoringUnitWatchList(
						hostApiDao.getFactoringUnitWatchDao(pExecutionContext, currentFactoringUnit.getFUID()));
				currentFactoringUnit.setFactoringUnitBidHostApiBeans(
						hostApiDao.getFactoringUnitBidDao(pExecutionContext, currentFactoringUnit.getFUID()));
			}

		} else {
			factoringUnitResponseBean.setMessage("Error - No record found");
			factoringUnitResponseBean.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(factoringUnitResponseBean).toString();

		return jsonResponse;

	}

	public String saveInstrumentBo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {

		InstrumentBean lInstrumentBean = new InstrumentBean();
		List<ValidationFailBean> lValidationFailBeans = null;
		boolean pNew = true;
		// this
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
		Map<String, Object> lDiffMap = getExtraPayloadRecd(instrumentDAO, lMap);
		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		AppUserBean pFilterBean = new AppUserBean();
		if (pRequest.getHeader("domain") != null) {
			pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		}

		if (pRequest.getHeader("login") != null) {
			pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		}

		AppUserBean lAppUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

		lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, pMessage,
				pNew ? BeanMeta.FIELDGROUP_INSERT + "Api" : BeanMeta.FIELDGROUP_UPDATE + "Api", null);

		boolean lSubmit = false;
		String lRemarks = null;
		if (lMap.containsKey("status")) {
			String lStatusStr = (String) lMap.get("status");
			lRemarks = (String) lMap.get("statusRemarks");
			lSubmit = InstrumentBean.Status.Submitted.getCode().equals(lStatusStr);
		}
		logger.info("userBean - " + lAppUserBean.getId());
		instrumentBO.save(pExecutionContext, lInstrumentBean, lAppUserBean, pNew, false, lSubmit, lRemarks, true, null,
				lDiffMap);
		pExecutionContext.dispose();
		return instrumentBeanMeta.formatAsJson(lInstrumentBean, InstrumentBean.FIELDGROUP_APIRESPONSE, null, false);

	}

	public String approveInstrumentBo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {

		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		AppUserBean pFilterBean = new AppUserBean();
		if (pRequest.getHeader("domain") != null) {
			pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		}

		if (pRequest.getHeader("login") != null) {
			pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		}

		String lResponse = null;
		try {
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> inputMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
			// AppUserBean lAppUserBean = (AppUserBean)
			// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			AppUserBean lAppUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);
			// conversion for IOCL
			IClientAdapter lClientAdapter = null;
			ProcessInformationBean lProcessInformationBean = null;
			Connection lAdapterConnection = null;
			ApiResponseStatus lResponseStatus = null;
			Map<String, Object> lReturnMap = new HashMap<String, Object>();
			//
			try {
				if (!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())
						&& CommonAppConstants.Yes.Yes.equals(lAppUserBean.getEnableAPI())) {
					lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lAppUserBean.getDomain());
					// if api user is using our apis then there will be no
					// adpater
					if (lClientAdapter != null) {
						lAdapterConnection = DBHelper.getInstance().getConnection();
						lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST,
								lAdapterConnection);
						lProcessInformationBean.setClientDataForProcessing(pFilter);
						lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couapp", null, true,
								false);
						pFilter = lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
					}
				}

				// Status status = InstrumentBean.Status.Counter_Approved;
				// if("COURET".eq)

				logger.info("status - " + inputMap.get("status"));
				logger.info("condition - " + ((inputMap.get("status") == "COUAPP")
						? InstrumentBean.Status.Counter_Approved : InstrumentBean.Status.Counter_Returned));
				List<Map> lList = (ArrayList<Map>) lJsonSlurper.parseText(updateStatus(pExecutionContext, pRequest,
						pFilter,
						(String.valueOf(inputMap.get("status")).equals("COUAPP")
								? InstrumentBean.Status.Counter_Approved : InstrumentBean.Status.Counter_Returned),
						lAppUserBean));
				for (Map lMap : lList) {
					lReturnMap.put("id", lMap.get("act"));
					lReturnMap.put("message", lMap.get("rem"));
				}
				logger.info(new JsonBuilder(lReturnMap).toString());
			} catch (Exception lMainException) {
				lReturnMap.put("message", TredsHelper.getInstance().getErrorMessageString(lMainException));
				lReturnMap.put("status", "FL");
				lResponseStatus = ApiResponseStatus.Failed;
				if (lClientAdapter != null) {
					return TredsHelper.getInstance().returnErrorMessage(lMainException);
				}
				throw lMainException;
			} finally {
				if (lClientAdapter != null) {
					// log the response to be sent
					lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lReturnMap).toString());
					lClientAdapter.logInComing(lProcessInformationBean, "/v1/instcntr/status/couapp", lResponseStatus,
							false, false);
				}
				if (lAdapterConnection != null && !lAdapterConnection.isClosed()) {
					lAdapterConnection.close();
				}
			}
			if (lClientAdapter != null) {
				PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
			}
			lResponse = new JsonBuilder(lReturnMap).toString();
		} catch (Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}

		return lResponse;
	}

	public String approveInstrumentCheckerBo(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		AppUserBean pFilterBean = new AppUserBean();
		String response = null;
		if (pRequest.getHeader("domain") != null) {
			pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		}

		if (pRequest.getHeader("login") != null) {
			pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		}

		AppUserBean lAppUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> inputMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);

		if (String.valueOf(inputMap.get("status")).equals("COUAPP")) {
			// approve flow
			response = updateStatusCheckerApprove(pExecutionContext, pRequest, pFilter,
					InstrumentBean.Status.Counter_Approved, lAppUserBean);
		} else {
			response = updateStatusCheckerReturn(pExecutionContext, pRequest, pFilter, lAppUserBean);
		}

		logger.info("response -- " + response);

		return response;

	}

	private String updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, String pFilter,
			InstrumentBean.Status pStatus, AppUserBean lUserBean) throws Exception {
		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		InstrumentBean lFilterBean = new InstrumentBean();
		JsonSlurper lJsonSlurper = new JsonSlurper();
		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();
		//
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		List<Long> lIdList = (List<Long>) lMap.get("idList");
		if (lIdList != null && !lIdList.isEmpty()) {
			// do nothing
		} else {
			lIdList = new ArrayList<Long>();
			lIdList.add((Long) lMap.get("id"));
		}
		if (pStatus.equals(InstrumentBean.Status.Counter_Returned)) {
			if (!lMap.containsKey("statusRemarks") || StringUtils.isBlank(lMap.get("statusRemarks").toString())) {
				throw new CommonBusinessException("Remarks are mandatory.");
			}
		}
		for (Long lId : lIdList) {
			instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
			lFilterBean.setStatus(pStatus);
			lFilterBean.setId(lId);
			try {
				if (Status.Counter_Approved.equals(lFilterBean.getStatus())) {
					List<MakerCheckerMapBean> lCheckers = null;
					AppUserBO lAppUserBO = new AppUserBO();
					lCheckers = lAppUserBO.getCheckers(pExecutionContext.getConnection(), lUserBean.getId(),
							MakerCheckerMapBean.CheckerType.InstrumentCounter);
					if (lCheckers != null && lCheckers.size() > 0) {
						lFilterBean.setStatus(InstrumentBean.Status.Counter_Checker_Pending);
					}
				}
				String lReturnMsg = instrumentBO.updateCounterStatus(pExecutionContext, lFilterBean, lUserBean,
						lFilterBean);
				Map<String, Object> lRetMap = (Map<String, Object>) lJsonSlurper.parseText(lReturnMsg);
				TredsHelper.getInstance().appendMessage(lRetMsg, lFilterBean.getId().toString(),
						lRetMap.get("message").toString(), lRetMap);
			} catch (Exception e) {
				logger.info(e.getStackTrace());
				TredsHelper.getInstance().appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage(), null);
			}
		}
		return new JsonBuilder(lRetMsg).toString();
	}

	private String updateStatusCheckerApprove(ExecutionContext pExecutionContext, HttpServletRequest pRequest,
			String pFilter, InstrumentBean.Status pStatus, AppUserBean lUserBean) throws Exception {
		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		// AppUserBean pFilterBean = new AppUserBean();
		// if (pRequest.getHeader("domain") != null) {
		// pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		// }
		//
		// if (pRequest.getHeader("login") != null) {
		// pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		// }
		//
		// AppUserBean lUserBean =
		// appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

		JsonSlurper lJsonSlurper = new JsonSlurper();
		InstrumentBean lFilterBean = new InstrumentBean();
		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		List<Long> lIdList = (List<Long>) lMap.get("idList");
		if (lIdList != null && !lIdList.isEmpty()) {
			// do nothing
		} else {
			if (lMap.get("id") != null) {
				lIdList = new ArrayList<Long>();
				lIdList.add(new Long(lMap.get("id").toString()));
			}
		}
		Map<String, String> lDataHash = new HashMap<String, String>();
		if (Status.Counter_Approved.equals(pStatus)) {
			lDataHash.put("action", "instrument approval");
		} else if (Status.Counter_Checker_Return.equals(pStatus)) {
			lDataHash.put("action", "instrument return");
		} else if (Status.Counter_Checker_Pending.equals(pStatus)) {
			lDataHash.put("action", "instrument approval");
		}
		if (!lDataHash.isEmpty()) {
			TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
					AppConstants.OTP_NOTIFY_TYPE_INSTCHKAPPROVAL, AppConstants.TEMPLATE_PREFIX_INSTCHKAPPROVAL,
					lDataHash);
		}
		for (Long lId : lIdList) {
			instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
			lFilterBean.setStatus(pStatus);
			lFilterBean.setId(lId);
			if (Status.Counter_Approved.equals(pStatus)) {
				InstrumentBean lInstBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
				lInstBean.populateNonDatabaseFields();
				if (!Status.Counter_Checker_Pending.equals(lInstBean.getStatus())) {
					throw new CommonBusinessException("Invalid instrument status.");
				}
				lInstBean.setStatus(pStatus);
				lInstBean.setStatusRemarks(lFilterBean.getStatusRemarks());
				AppEntityBean lAeBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
				if (CommonAppConstants.Yes.Yes.equals(lAeBean.getPreferences().getCcmod())) {
					Long lMaxLevel = instrumentBO.getMaxLevelChecker(pExecutionContext.getConnection(),
							lInstBean.getCounterAuId(), MakerCheckerMapBean.CheckerType.InstrumentCounter);
					if (lUserBean.getInstCntrLevel() != null && lMaxLevel != null
							&& lUserBean.getInstCntrLevel().equals(lMaxLevel)) {
						List<BeanFieldMeta> lFieldsMeta = instrumentBeanMeta.getFieldMetaList(null, null);
						BeanFieldMeta lFieldMeta = null;
						for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
							lFieldMeta = lFieldsMeta.get(lPtr);
							Object lNewVal = lFieldMeta.getProperty(lFilterBean);
							if (lNewVal != null)
								lFieldMeta.setProperty(lInstBean, lNewVal, null);
						}
						lInstBean.setSupplier(null);
						lInstBean.setCreditPeriod(null);
					}
				}
				lFilterBean = lInstBean;
			}
			try {

				String lReturnMsg = instrumentBO.updateCounterStatus(pExecutionContext, lFilterBean, lUserBean, null);
				Map<String, Object> lRetMap = (Map<String, Object>) lJsonSlurper.parseText(lReturnMsg);
				EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), lRetMap.get("message").toString());
			} catch (Exception e) {
				EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage());
			}
		}
		//
		return new JsonBuilder(lRetMsg).toString();
	}

	public String updateStatusCheckerReturn(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter, AppUserBean lUserBean) throws Exception {
		Map<String, Object> lRetMap = new HashMap<String, Object>();
		List<Map<String, Object>> lReturnMap = new ArrayList<>();
		try {

			if (StringUtils.isEmpty(pFilter)) {
				throw new CommonBusinessException("Input parmeters expected.");
			}
			// AppUserBean lUserBean = (AppUserBean)
			// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			InstrumentBean lFilterBean = null;
			JsonSlurper lJsonSlurper = new JsonSlurper();

			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
			if (lMap != null) {
				lFilterBean = new InstrumentBean();
				instrumentBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
				InstrumentBean lInstBean = instrumentBO.findBean(pExecutionContext, lFilterBean);
				lInstBean.populateNonDatabaseFields();
				// lInstBean.setStatus(InstrumentBean.Status.Counter_Checker_Pending);
				if (!Status.Counter_Checker_Pending.equals(lInstBean.getStatus())) {
					throw new CommonBusinessException("Invalid instrument status.");
				}
				lFilterBean.setStatus(InstrumentBean.Status.Counter_Checker_Return);
				lReturnMap = instrumentBO.UpdateCounterCheckerStatus(pExecutionContext, lFilterBean, lUserBean);

				if (lReturnMap != null && !lReturnMap.isEmpty()) {
					if (lRetMap.containsKey("act")) {
						lRetMap.put("id", lRetMap.get("act"));
					}
					if (lRetMap.containsKey("msg")) {
						lRetMap.put("message", lRetMap.get("msg"));
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonBuilder(lReturnMap).toString();
	}

	public String getObligationBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap) throws Exception {

		logger.info("inside getObligationBo");

		ObligationResponseBean obligationResponse = hostApiDao.getObligationsDao(pExecutionContext, inputMap);

		if (obligationResponse != null && obligationResponse.getObligationHostApiBeans() != null
				&& obligationResponse.getObligationHostApiBeans().size() > 0) {
			// some business logic if required

			for (ObligationHostApiBean currentObligation : obligationResponse.getObligationHostApiBeans()) {

				currentObligation.setObligationExtensionList(
						hostApiDao.getObligationExtensionsDao(pExecutionContext, currentObligation.getOBID()));

				currentObligation.setObligationDetailsList(
						hostApiDao.getObligationDetailsDao(pExecutionContext, currentObligation.getOBFUID()));

				currentObligation.setObligationSplitsList(
						hostApiDao.getObligationSplitsDao(pExecutionContext, currentObligation.getOBID()));

				currentObligation.setModificationDetailsList(
						hostApiDao.getObligationModificationDao(pExecutionContext, currentObligation.getOBID()));
			}

		} else {
			obligationResponse.setMessage("Error - No record found");
			obligationResponse.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(obligationResponse).toString();

		return jsonResponse;

	}

	public String approveInitChecker(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		AppUserBean pFilterBean = new AppUserBean();
		String response = null;
		try {
			if (pRequest.getHeader("domain") != null) {
				pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
			}

			if (pRequest.getHeader("login") != null) {
				pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
			}

			AppUserBean lAppUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> inputMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);

			if (String.valueOf(inputMap.get("status")).equals("CHKAPP")) {
				// approve flow
				response = updateStatusInitCheckerBo(pExecutionContext, pRequest, pFilter,
						InstrumentBean.Status.Checker_Approved, lAppUserBean);

			} else if (String.valueOf(inputMap.get("status")).equals("CHKRET")) {
				response = updateStatusInitCheckerBo(pExecutionContext, pRequest, pFilter,
						InstrumentBean.Status.Checker_Returned, lAppUserBean);

			} else if (String.valueOf(inputMap.get("status")).equals("CHKREJ")) {
				response = updateStatusInitCheckerBo(pExecutionContext, pRequest, pFilter,
						InstrumentBean.Status.Checker_Rejected, lAppUserBean);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	private String updateStatusInitCheckerBo(ExecutionContext pExecutionContext, HttpServletRequest pRequest,
			String pFilter, InstrumentBean.Status pStatus, AppUserBean lUserBean) throws Exception {
		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		List<Map<String, Object>> lRetMsg = new ArrayList<Map<String, Object>>();
		try {
			InstrumentBean lFilterBean = new InstrumentBean();
			JsonSlurper lJsonSlurper = new JsonSlurper();

			Connection lAdapterConnection = DBHelper.getInstance().getConnection();
			//
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
			List<Long> lIdList = (List<Long>) lMap.get("idList");
			if (lIdList != null && !lIdList.isEmpty()) {
				// do nothing
			} else {
				lIdList = new ArrayList<Long>();
				lIdList.add((Long) lMap.get("id"));
			}
			Map<String, String> lDataHash = new HashMap<String, String>();
			if (Status.Checker_Approved.equals(pStatus)) {
				lDataHash.put("action", "instrument approval");
			} else if (Status.Checker_Rejected.equals(pStatus)) {
				lDataHash.put("action", "instrument rejection");
			} else if (Status.Checker_Returned.equals(pStatus)) {
				lDataHash.put("action", "instrument return");
			}
			if (!lDataHash.isEmpty()) {
				// TredsHelper.getInstance().verifyOrSendOTP(pRequest,
				// lUserBean, pExecutionContext.getConnection(),
				// AppConstants.OTP_NOTIFY_TYPE_INSTCHKAPPROVAL,
				// AppConstants.TEMPLATE_PREFIX_INSTCHKAPPROVAL,
				// lDataHash);
			}
			for (Long lId : lIdList) {
				instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);
				lFilterBean.setStatus(pStatus);
				lFilterBean.setId(lId);
				try {
					instrumentBO.updateCheckerStatus(pExecutionContext, lFilterBean, lUserBean);
					EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), "Success.");

					if (InstrumentBean.Status.Checker_Approved.equals(lFilterBean.getStatus())) {
						try {
							Connection lConnection = pExecutionContext.getConnection();
							InstrumentBean lInstrumentBean = instrumentBO.findBean(lConnection, lFilterBean);
							IClientAdapter lClientAdapter = ClientAdapterManager.getInstance()
									.getClientAdapter(lInstrumentBean.getPurchaser());
							if (lClientAdapter != null) {
								ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(
										ProcessInformationBean.PROCESSID_INST, lAdapterConnection);
								lProcessInformationBean.setTredsDataForProcessing(lInstrumentBean);
								lClientAdapter.sendResponseToClient(lProcessInformationBean);
							}
						} catch (Exception lEx) {
							lEx.printStackTrace();
							logger.info("Error : " + lEx.getMessage());
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					EndOfDayBO.appendMessage(lRetMsg, lFilterBean.getId().toString(), e.getMessage());
				}
			}
			if (lAdapterConnection != null && !lAdapterConnection.isClosed()) {
				lAdapterConnection.close();
			}
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonBuilder(lRetMsg).toString();
	}

	public String updateStatusInitApprove(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {

		int lRequestId = ApiLogger.logApiRequestResponse(true, pRequest, pFilter, this.getClass().getName(), 0);
		String lResponse = null;
		try {
			// AppUserBean lUserBean = (AppUserBean)
			// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			// Domain Check?

			AppUserBean pFilterBean = new AppUserBean();
			if (pRequest.getHeader("domain") != null) {
				pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
			}

			if (pRequest.getHeader("login") != null) {
				pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
			}

			AppUserBean lUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

			InstrumentBean lFilterBean = new InstrumentBean();
			instrumentBeanMeta.validateAndParse(lFilterBean, pFilter, null, null);

			Map<String, String> lDataHash = new HashMap<String, String>();
			if (Status.Submitted.equals(lFilterBean.getStatus())) {
				lDataHash.put("action", "instrument submition");
				TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
						AppConstants.OTP_NOTIFY_TYPE_INSTSUBMIT, AppConstants.TEMPLATE_PREFIX_INSTSUBMIT, lDataHash);
			}
			List<Map<String, Object>> lList = instrumentBO.updateMakerStatus(pExecutionContext, lFilterBean, lUserBean);
			lResponse = new JsonBuilder(lList).toString();
		} catch (Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		} finally {
			ApiLogger.logApiRequestResponse(false, pRequest, lResponse, this.getClass().getName(), lRequestId);
		}
		return lResponse;
	}

	public String bidAcceptBO(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter, FactoringUnitBean.Status pStatus) throws Exception {

		JsonSlurper lJsonSlurper = new JsonSlurper();
		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		AppUserBean pFilterBean = new AppUserBean();
		if (pRequest.getHeader("domain") != null) {
			pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		}

		if (pRequest.getHeader("login") != null) {
			pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		}

		AppUserBean lUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

		Map<String, String> lDataHash = new HashMap<String, String>();
		if (FactoringUnitBean.Status.Active.equals(pStatus)) {
			lDataHash.put("action", "factoring unit sent to auction");
			TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
					AppConstants.OTP_NOTIFY_TYPE_FUSENTTOAUCTION, AppConstants.TEMPLATE_PREFIX_FUSENTTOAUCTION,
					lDataHash);
		} else if (FactoringUnitBean.Status.Factored.equals(pStatus)) {
			lDataHash.put("action", "factoring unit sent to auction");
			TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
					AppConstants.OTP_NOTIFY_TYPE_BIDACCEPTCB, AppConstants.TEMPLATE_PREFIX_BIDACCEPTCB, lDataHash);
		} else if (FactoringUnitBean.Status.Withdrawn.equals(pStatus)) {
			lDataHash.put("action", "factoring unit withdrawal");
			TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
					AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
		} else if (FactoringUnitBean.Status.Suspended.equals(pStatus)) {
			lDataHash.put("action", "factoring unit put on hold");
			TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(),
					AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
		}
		List<Map<String, Object>> lJsonList = (List<Map<String, Object>>) lJsonSlurper.parseText(pFilter);
		List<FactoringUnitBean> lFilterList = new ArrayList<FactoringUnitBean>();
		for (Map<String, Object> lMap : lJsonList) {
			FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
			factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, lMap, null);
			lFilterList.add(lFactoringUnitBean);
		}
		System.out.println("lFilterList" + lFilterList);
		return factoringUnitBO.updateStatus(pExecutionContext, lFilterList, lUserBean, pStatus);

	}

	public String bulkUploadBSLinkBo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {

		String lResponse = null;
		try {
			List<Object> lResultList = new ArrayList<>();
			// AppUserBean lLoggedInAppUserBean = (AppUserBean)
			// AuthenticationHandler.getInstance()
			// .getLoggedInUserBean(pRequest);

			AppUserBean pFilterBean = new AppUserBean();
			if (pRequest.getHeader("domain") != null) {
				pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
			}

			if (pRequest.getHeader("login") != null) {
				pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
			}

			AppUserBean lLoggedInAppUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

			PurchaserSupplierLinkBean lPSBean = null;
			List<Object> lInputList = (List<Object>) new JsonSlurper().parseText(pMessage);
			String lPurchaser = null;

			if (!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInAppUserBean.getDomain())
					&& CommonAppConstants.Yes.Yes.equals(lLoggedInAppUserBean.getEnableAPI())) {
				throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
			}
			AppUserBean lPurchaserLogin = new AppUserBean();
			for (Object lData : lInputList) {
				lPurchaser = (String) ((Map<String, Object>) lData).get("purchaser");
				lPurchaserLogin.setDomain(lPurchaser);
				lPurchaserLogin.setId(lLoggedInAppUserBean.getId());
				//
				String lMessage = new JsonBuilder(lData).toString();
				try {
					lPSBean = save(pExecutionContext, pRequest, lMessage, true, lPurchaserLogin);
					lPSBean.setInWorkFlow(Yes.Yes);
					lPSBean.setApprovalStatus(ApprovalStatus.Submitted);
					purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPSBean, lPurchaserLogin);
					lResultList.add(purchaserSupplierLinkBeanMeta.formatAsMap(lPSBean,
							PurchaserSupplierLinkBean.FIELDGROUP_RESPFIELDSAPI, null, false));
				} catch (Exception e) {
					e.printStackTrace();
					lResultList.add(e.getMessage());
				}

			}
			lResponse = new JsonBuilder(lResultList).toString();
		} catch (Exception e) {
			e.printStackTrace();
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}
		return lResponse;

	}

	private PurchaserSupplierLinkBean save(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pMessage, boolean pNew, IAppUserBean pAppUserBean)
			throws Exception {
		if (StringUtils.isEmpty(pMessage)) {
			pMessage = "{}";
		}
		IAppUserBean lUserBean = null;
		if (pAppUserBean == null) {
			lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		} else {
			lUserBean = pAppUserBean;
		}
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();

		List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(
				lPurchaserSupplierLinkBean, pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE,
				null);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
		// if it is new then no need to check status and approvalStatus since it
		// will be put as default
		if (pNew) {
			if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
				ValidationFailBean lFailBean = null;
				Map<String, BeanFieldMeta> lFieldMap = purchaserSupplierLinkBeanMeta.getFieldMap();
				BeanFieldMeta lStatusMeta = lFieldMap.get("status");
				BeanFieldMeta lAppStatusMeta = lFieldMap.get("approvalStatus");
				for (int lPtr = lValidationFailBeans.size() - 1; lPtr >= 0; lPtr--) {
					lFailBean = lValidationFailBeans.get(lPtr);
					if (lStatusMeta != null && lStatusMeta.getLabel().equals(lFailBean.getName())) {
						lValidationFailBeans.remove(lPtr);
					}
					if (lAppStatusMeta != null && lAppStatusMeta.getLabel().equals(lFailBean.getName())) {
						lValidationFailBeans.remove(lPtr);
					}
				}
			}
		}
		if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
			throw new CommonValidationException(lValidationFailBeans);
		if (PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getStatus())) {
			// API WILL NOT HAVE OTP VERIFICATION
		}
		validatePSFields(lPurchaserSupplierLinkBean);
		if (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())
				&& CommonAppConstants.Yes.Yes.equals(((AppUserBean) lUserBean).getEnableAPI())) {
			IClientAdapter lClientAdpater = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
			// if api user is using our apis then there will be no adpater
			if (lClientAdpater == null) {
				// defaulting for BHEL
				lPurchaserSupplierLinkBean.setPurchaserAutoApproveInvoice(YesNo.No);
				lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(null);
			}
		}

		lPurchaserSupplierLinkBean = purchaserSupplierLinkBO.save(pExecutionContext, lPurchaserSupplierLinkBean,
				lUserBean, pNew, false, null, true);
		return lPurchaserSupplierLinkBean;
	}

	private void validatePSFields(PurchaserSupplierLinkBean pPurchaserSupplierLinkBean) throws Exception {
		// ,"purchaserSupplierRef"
		// ,"purchaserAutoApproveInvoice"-default N
		// ,"sellerAutoApproveInvoice"-default null
		String[] lMandatoryFields = new String[] { "supplier", "purchaser", "creditPeriod", "bidAcceptingEntityType",
				"costBearingType", "chargeBearer", "settleLeg3Flag", "autoAccept", "autoAcceptableBidTypes",
				"autoConvert", "invoiceMandatory", "remarks", "cashDiscountPercent", "haircutPercent",
				"instrumentCreation" };
		List<String> lMFList = new ArrayList<String>();
		for (String lField : lMandatoryFields) {
			lMFList.add(lField);
		}
		List<BeanFieldMeta> lMandtoryBFMeta = purchaserSupplierLinkBeanMeta.getFieldListFromNames(lMFList);
		Object lValue = null;
		String lErrorMessage = "";
		for (BeanFieldMeta lBeanFieldMeta : lMandtoryBFMeta) {
			lValue = lBeanFieldMeta.getProperty(pPurchaserSupplierLinkBean);
			if (lValue == null || StringUtils.isEmpty(lValue.toString())) {
				lErrorMessage += lBeanFieldMeta.getName() + ":Cannot be empty(null). ";
			}
		}
		if (StringUtils.isNotEmpty(lErrorMessage)) {
			throw new CommonBusinessException(lErrorMessage);
		}
	}

	public Object getSettlementMISBo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {

		// AppUserBean lUserBean = (AppUserBean)
		// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

		AppUserBean pFilterBean = new AppUserBean();
		if (pRequest.getHeader("domain") != null) {
			pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
		}

		if (pRequest.getHeader("login") != null) {
			pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
		}

		AppUserBean lUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		ObligationBean lFilterBean = new ObligationBean();
		obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

		List<String> lFields = (List<String>) lMap.get(BeanMeta.PARAM_COLUMNNAMES);
		lFilterBean.setForSettlementReport(true);
		List<ObligationBean> lObligationList = obligationBO.findList(pExecutionContext.getConnection(), lFilterBean,
				lFields, lUserBean);
		// List<Object> lResults = new ArrayList<Object>();
		// for (ObligationBean lObligationBean : lObligationList) {
		// if (lFields == null)
		// lResults.add(obligationBeanMeta.formatAsMap(lObligationBean, null,
		// defaultListFields, false)); // api
		// else
		// lResults.add(obligationBeanMeta.formatAsArray(lObligationBean, null,
		// lFields, true));
		// }
		return new JsonBuilder(lObligationList).toString();

	}

	public String updateStatusBSLinkBo(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pMessage) throws Exception {
		int lRequestId = ApiLogger.logApiRequestResponse(true, pRequest, pMessage, this.getClass().getName(), 0);
		String lResponse = null;
		try {

			AppUserBean pFilterBean = new AppUserBean();
			if (pRequest.getHeader("domain") != null) {
				pFilterBean.setDomain(String.valueOf(pRequest.getHeader("domain")));
			}

			if (pRequest.getHeader("login") != null) {
				pFilterBean.setLoginId(String.valueOf(pRequest.getHeader("login")));
			}

			AppUserBean lUserBean = appUserDAO.findBean(pExecutionContext.getConnection(), pFilterBean);

			// IAppUserBean lUserBean =
			// AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
			Connection lConnection = pExecutionContext.getConnection();
			PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
			JsonSlurper lJsonSlurper = new JsonSlurper();
			Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
			IClientAdapter lClientAdapter = null;
			ProcessInformationBean lProcessInformationBean = null;
			Connection lAdapterConnection = null;
			Map<String, String> lRtnMap = new HashMap<String, String>();
			ApiResponseStatus lResponseStatus = null;
			//
			try {
				lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lUserBean.getDomain());
				if (lClientAdapter != null) {
					lAdapterConnection = DBHelper.getInstance().getConnection();
					lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_PURSUPLINK,
							lAdapterConnection);
					lProcessInformationBean.setClientDataForProcessing(pMessage);
					lClientAdapter.logInComing(lProcessInformationBean, "/v1/pursuplnk/status", null, true, false);
					// conversion to actual data to be used by BO
					lClientAdapter.convertClientDataToTredsData(lProcessInformationBean);
					lMap = (Map<String, Object>) lProcessInformationBean.getProcessedTredsData();
				}
				//
				String lPrimaryKey = null;
				String[] lParts = new String[3];
				String lSup = null;
				String lPur = null;
				//
				lPrimaryKey = (String) lMap.get("id");
				if (CommonUtilities.hasValue(lPrimaryKey)) {
					lParts = lPrimaryKey.split("/");
					lSup = lParts[0];
					lPur = lParts[1];
				} else {
					// throw error
				}
				lPurchaserSupplierLinkBean.setSupplier(lSup);
				lPurchaserSupplierLinkBean.setPurchaser(lPur);
				// "supplierPurchaserRef","sellerAutoApproveInvoice", "remarks"
				String lApprovalStatusCode = (String) lMap.get("approvalStatus");
				String lApprovalRemarks = (String) lMap.get("approvalRemarks");
				String lReference = null, lAutoApprove = null;
				// supplierPurchaserRef - sellerAutoApproveInvoice --> only if
				// status is it is approved
				ApprovalStatus lApprovalStatus = null;
				for (ApprovalStatus lAStatus : ApprovalStatus.values()) {
					if (lAStatus.getCode().equals(lApprovalStatusCode)) {
						lApprovalStatus = lAStatus;
						break;
					}
				}
				lPurchaserSupplierLinkBean.setApprovalStatus(lApprovalStatus);
				lPurchaserSupplierLinkBean.setRemarks(lApprovalRemarks);

				// these two status are returned by seller hence allowing him to
				// save the same on returning also
				if (ApprovalStatus.Approved.equals(lApprovalStatus)
						|| ApprovalStatus.Returned.equals(lApprovalStatus)) {
					lReference = (String) lMap.get("supplierPurchaserRef");
					lAutoApprove = (String) lMap.get("sellerAutoApproveInvoice");
					lPurchaserSupplierLinkBean.setSupplierPurchaserRef(lReference);
					if (CommonAppConstants.YesNo.Yes.getCode().equals(lAutoApprove)) {
						lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.Yes);
					} else {
						lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.No);
					}
					String lAutoConvert = (String) lMap.get("autoConvert");
					AutoConvert lTmpAutoConvert = AutoConvert.Auto;
					if (StringUtils.isNotBlank(lAutoConvert)) {
						for (AutoConvert lTmp : AutoConvert.values()) {
							if (lTmp.getCode().equals(lAutoConvert)) {
								lTmpAutoConvert = lTmp;
								break;
							}
						}
					}
					lPurchaserSupplierLinkBean.setAutoConvert(lTmpAutoConvert);
				}
				String lBeanJson = purchaserSupplierLinkBeanMeta.formatAsJson(lPurchaserSupplierLinkBean);

				List<ValidationFailBean> lValidationFailBeans = null;

				String lFieldGroup = null;

				if (lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getPurchaser())) {
					lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS;
				} else if (lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getSupplier())) {
					lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIERAPPROVALSTATUS;
				}
				lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean,
						lBeanJson, lFieldGroup, null);

				if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
					throw new CommonValidationException(lValidationFailBeans);
				purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean);
				lRtnMap.put("status", "success");
				logger.info(new JsonBuilder(lRtnMap).toString());
				lResponseStatus = ApiResponseStatus.Success;
			} catch (Exception lMainException) {
				lRtnMap.put("message", TredsHelper.getInstance().returnErrorMessage(lMainException));
				lResponseStatus = ApiResponseStatus.Failed;
				if (lClientAdapter != null) {
					lResponse = TredsHelper.getInstance().returnErrorMessage(lMainException);
				} else {
					throw lMainException;
				}
			} finally {
				if (lClientAdapter != null) {
					// log the response to be sent
					lProcessInformationBean.setTredsReturnResponseData(new JsonBuilder(lRtnMap).toString());
					lClientAdapter.logInComing(lProcessInformationBean, "/v1/pursuplnk/status", lResponseStatus, false,
							false);
				}
				if (lAdapterConnection != null && !lAdapterConnection.isClosed()) {
					lAdapterConnection.close();
				}
			}
			if (StringUtils.isEmpty(lResponse)) {
				lResponse = new JsonBuilder(lRtnMap).toString();
			}
		} catch (Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		} finally {
			ApiLogger.logApiRequestResponse(false, pRequest, lResponse, this.getClass().getName(), lRequestId);
		}
		return lResponse;
	}

	public String getPurSuppLimitUtil(ExecutionContext pExecutionContext, Map<String, Object> inputMap)
			throws Exception {
		logger.info("Inside getPurSuppLimitUtil");

		PurchaserSupplierLimitUtilizationResponseBean purchaserSupplierLimitUtilizationHostApiBeans = hostApiDao
				.getPurSuppLimitUtilizationDao(pExecutionContext, inputMap);

		if (purchaserSupplierLimitUtilizationHostApiBeans != null
				&& purchaserSupplierLimitUtilizationHostApiBeans
						.getPurchaserSupplierLimitUtilizationHostApiBeans() != null
				&& purchaserSupplierLimitUtilizationHostApiBeans.getPurchaserSupplierLimitUtilizationHostApiBeans()
						.size() > 0) {

			// if any logic required

		} else {
			purchaserSupplierLimitUtilizationHostApiBeans.setMessage("Error - No Data Found");
			purchaserSupplierLimitUtilizationHostApiBeans.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(purchaserSupplierLimitUtilizationHostApiBeans).toString();

		return jsonResponse;

	}

	public String getSecond(ExecutionContext pExecutionContext, String seller, String buyer) throws Exception {
		logger.info("Inside hostapibo");
		PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
		// Connection lConnection = pExecutionContext.getConnection();
		lFilterBean.setSupplier(seller);
		lFilterBean.setPurchaser(buyer);
		logger.info("purchaserSupplierLinkBO -- " + purchaserSupplierLinkBO);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkBO.findBean(pExecutionContext,
				lFilterBean);
		lPurchaserSupplierLinkBean.populateNonDatabaseFields();

		return new JsonBuilder(lPurchaserSupplierLinkBean).toString();
	}

	private Map<String, Object> getExtraPayloadRecd(GenericDAO pGenericDAO, Map<String, Object> pPayLoad) {
		if (pPayLoad != null && pPayLoad.size() > 0) {
			List<BeanFieldMeta> lFieldsMeta = pGenericDAO.getBeanMeta().getFieldMetaList(null, null);
			Map<String, Object> lDiffHash = new HashMap<String, Object>();
			Set<String> lBeanFields = new HashSet<String>();
			//
			for (BeanFieldMeta lFieldMeta : lFieldsMeta) {
				lBeanFields.add(lFieldMeta.getName());
			}
			for (String lKey : pPayLoad.keySet()) {
				if (!lBeanFields.contains(lKey)) {
					lDiffHash.put(lKey, pPayLoad.get(lKey));
				}
			}
			if (lDiffHash.size() > 0) {
				return lDiffHash;
			}
		}
		return null;
	}

	public String getBIllDetailsBo(ExecutionContext pExecutionContext, Map<String, Object> inputMap) throws Exception {
		logger.info("Inside getBIllDetailsBo");

		BillResponseBean billResponseBean = hostApiDao.getBillsDao(pExecutionContext, inputMap);

		if (billResponseBean != null && billResponseBean.getBillsHostApiBeans() != null
				&& billResponseBean.getBillsHostApiBeans().size() > 0) {

			for (BillsHostApiBean currentBillHostApiBean : billResponseBean.getBillsHostApiBeans()) {
				currentBillHostApiBean.setBillFactoringHostApiBeanList(
						hostApiDao.getBillFactoringDao(pExecutionContext, currentBillHostApiBean.getBILID()));

				currentBillHostApiBean.setBillsRegistrationChargesHostApiBeans(
						hostApiDao.getBillRegistrationChargesDao(pExecutionContext, currentBillHostApiBean.getBILID()));

			}

		} else {
			billResponseBean.setMessage("Error - No Data Found");
			billResponseBean.setMessageCode("1");
		}

		String jsonResponse = new JsonBuilder(billResponseBean).toString();

		return jsonResponse;

	}

	public String withDrawFactoringUnit(ExecutionContext pExecutionContext, HttpServletRequest pRequest, String pFilter)
			throws Exception {

		return bidAcceptBO(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Withdrawn);
	}

	private void doTest() {

		final String username = "ocid1.user.oc1..aaaaaaaaafc7jj6we7f7orhrobopuhoirjrym52cjsdbyhusqmotlekcvcgq@ocid1.tenancy.oc1..aaaaaaaakwpg357wtd7gd5rhavng63xdo7h6hmeu24ct26c7pxzzwotl46hq.co.com";
		final String passwd = "{xKoH9JNy>2eq1[E1lc!";
		final String from = "maajid.shaikh@rxil.in";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.email.ap-mumbai-1.oci.oraclecloud.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "*");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, passwd);
			}
		});

		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, "rxilitteam@rxil.in");
			msg.setSubject("Testing SMTP using [" + from + "]");
			msg.setSentDate(new Date());
			msg.setText("Hey, this is a test from [" + from + "]");
			Transport.send(msg);

		} catch (MessagingException e) {
			System.out.println("send failed, exception: " + e);
		}
		System.out.println("Sent Ok");
	}

}