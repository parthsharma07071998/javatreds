package com.xlx.treds.adapter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.http.SSLConfigurationProvider;
import com.xlx.commonn.http.bean.ApiRequestBean;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.commonn.http.client.RestClient;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiRequestType;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.RequestStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ResponseAckStatus;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.Type;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class IOCLClientAdapter implements IClientAdapter {
    private static Logger logger = Logger.getLogger(IOCLClientAdapter.class);
	private static final String LOG_HEADER = "IOCLClientAdapter :: ";
	
	private static final String DATE_FORMAT = AppConstants.DATE_FORMAT;
	private String entityCode = null;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private ClientSettingsBean clientSettingsBean;
    public RestClient ioclClient = null;
    private GenericDAO<AdapterRequestResponseBean> adapterRequestResponseDAO;
    List<String> instCntrAppFields,instCntrRejFields,instCntrRetFields;
    private final static String KEY_SEPERATOR = "-";
    private final static String PARAM_VENDORCODE = "IOCL_VEN_CODE";
    private final static String PARAM_UNIQUEID = "UID";
    private final static String PARAM_RXILVENDORCODE = "RXIL_VEN_CODE";
    private final static String PARAM_VENDORREGAGENCY = "VEN_REG_AGENCY";
    private final static String PARAM_RXILREGNO = "RXIL_REG_NO";
    private final static String PARAM_INSTRUMENTNUMBER = "INST_NUMBER";
    private final static String PARAM_RETURNSTATUS = "RETURN_STATUS";
    private final static String PARAM_VENDORREGION = "VEN_REGION";
    //
    private final static String VALUES_COUNTER_STATUS_FAIL = "F";
    private final static String VALUES_COUNTER_STATUS_SUCCESS = "S";
    //
    private final static String VALUES_RETURN_STATUS_SUCCESS = "SU";
    private final static String VALUES_RETURN_STATUS_RETURN = "RT";
    private final static String VALUES_RETURN_STATUS_REJECT = "RJ";
    private final static String VALUES_FACTORINGUNIT_WITHDRAWN = "W";
    private final static String VALUES_EXPIRED = "EX";
    private final static String VALUES_LEG1SETTLED = "SU";
    private final static String VALUES_LEG1FAILED = "RE";
    //
    
	public IOCLClientAdapter(String pEntityCode,ClientSettingsBean lClientSettingsBean){
		super();
		entityCode = pEntityCode;
		clientSettingsBean = lClientSettingsBean;
		purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		adapterRequestResponseDAO = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class);
		logger.info(LOG_HEADER + pEntityCode + " : initialized.");
		instCntrAppFields = Arrays.asList(new String[]{"id", "status"});
		instCntrRejFields = Arrays.asList(new String[]{"id", "status", "statusRemarks"});
		instCntrRetFields = Arrays.asList(new String[]{"id", "status", "statusRemarks"});
	}
	
	@Override
	public String convertClientDataToTredsData(ProcessInformationBean pProcessInformationBean) throws Exception {
        JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lMap = new HashMap<String,Object>();
		Map<String,Object> lReturnMap = null;
		//
		if(ProcessInformationBean.PROCESSID_PURSUPLINK.equals(pProcessInformationBean.getProcessId())){
			//reset the string data to map
			Object lTmpStr = pProcessInformationBean.getClientDataForProcessing();
			lMap = (Map<String, Object>)lJsonSlurper.parseText(lTmpStr.toString());
			pProcessInformationBean.setClientDataForProcessing(lMap);
			//
	        if(convertClientPurchaserSupplierLinkToTredsPSLMap(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedTredsData();
	        }
		}
		if(ProcessInformationBean.PROCESSID_INST_PRE.equals(pProcessInformationBean.getProcessId()) ||
				ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
			//reset the string data to map
			Object lTmpStr = pProcessInformationBean.getClientDataForProcessing();
			lMap = (Map<String, Object>)lJsonSlurper.parseText(lTmpStr.toString());
			pProcessInformationBean.setClientDataForProcessing(lMap);
			//
	        if(convertClientInstrumentToTredsInstMap(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedTredsData();
	        }
		}
		return new JsonBuilder(lReturnMap).toString();
	}


	@Override
	public String convertTredsDataToClientData(ProcessInformationBean pProcessInformationBean) throws Exception {
		Map<String,Object> lReturnMap = null;
		Map<String,Object> lTmpMap = null;
		if(ProcessInformationBean.PROCESSID_PURSUPLINK.equals(pProcessInformationBean.getProcessId())){
	        if(convertTredsPurchaserSupplierLinkBeanToClientBean(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedClientData();
	        }
			return new JsonBuilder(lReturnMap).toString();
		}else if(ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
	        if(convertTredsInstrumentBeanToClientBean(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedClientData();
	        }
			return new JsonBuilder(lReturnMap).toString();
		}else if(ProcessInformationBean.PROCESSID_INST_ACK.equals(pProcessInformationBean.getProcessId())){
	        if(convertTredsInstrumentBeanToClientBean(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedClientData();
	        }
			return new JsonBuilder(lReturnMap).toString();
		}else if(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessInformationBean.getProcessId())){
			if(convertTredsFactoredBeanToClientBean(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedClientData();
	        }			
		}else if(ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessInformationBean.getProcessId())){
			if(convertTredsFactoredBeanToClientBean(pProcessInformationBean)){
		        lReturnMap = (Map<String,Object>)pProcessInformationBean.getProcessedClientData();
	        }
		}
		return new JsonBuilder(lReturnMap).toString();
	}
	
	private boolean convertClientPurchaserSupplierLinkToTredsPSLMap(ProcessInformationBean pProcessInformationBean) throws Exception{
		Map<String,Object> lReturnMap = new HashMap<String,Object>();
		PurchaserSupplierLinkBean lPSLinkBean = null;
		if(pProcessInformationBean.getClientDataForProcessing() != null){
			Map<String,Object> lTmpMap = (Map<String,Object>) pProcessInformationBean.getClientDataForProcessing();
			Object lValue = null;
			lPSLinkBean = new PurchaserSupplierLinkBean();
			//
			//lPSLinkBean.setPurchaser(entityCode);
			logger.info("IOCL PSL MAP : "+lTmpMap.toString());
			
			lValue = (Object)lTmpMap.get("id");
			if (StringUtils.isEmpty((String) lValue)){
				throw new CommonBusinessException("UID not found.");
			}
			String[] lParts = CommonUtilities.splitString((String)lValue,KEY_SEPERATOR);
			pProcessInformationBean.setUID(lValue!=null?lValue.toString():"");
			//
			if(lParts!=null && lParts.length > 1){
				lTmpMap.put("supplier", lParts[1]);
				lTmpMap.put("purchaser", lParts[0]);
			}
			//receiving approvalRemarks but not validating it as it is not mandatory
			List<String> defaultListFields = Arrays.asList(new String[]{"supplier", "purchaser","approvalStatus"});
			List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkDAO.getBeanMeta().validateAndParse(lPSLinkBean, lTmpMap, null, defaultListFields, null);
			if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)){
				 throw new CommonValidationException(lValidationFailBeans);
			}
			//actual conversion of bean to map
			if(!entityCode.equals(lPSLinkBean.getPurchaser())){
				throw new CommonBusinessException("Invalid Purchaser sent.");
			}
			if(!ApprovalStatus.ReActivate.getCode().equals(lPSLinkBean.getApprovalStatus().getCode())){
				throw new CommonBusinessException("Invalid approval status sent.");
			}
			//
			lReturnMap = purchaserSupplierLinkDAO.getBeanMeta().formatAsMap(lPSLinkBean);
			lReturnMap.put("id", lParts[1]+"/"+ lParts[0]);
	        pProcessInformationBean.setProcessedTredsData(lReturnMap);
			pProcessInformationBean.setKey(lPSLinkBean.getSupplier());
	        return true;
		}
		return false;
	}
	
	private boolean convertClientInstrumentToTredsInstMap(ProcessInformationBean pProcessInformationBean) throws Exception {
		Map<String,Object> lReturnMap = new HashMap<String,Object>();
		InstrumentBean lInstBean = null;
		if(pProcessInformationBean.getClientDataForProcessing() != null){
			Map<String,Object> lTmpMap = (Map<String,Object>) pProcessInformationBean.getClientDataForProcessing();
			Object lValue = null;
			lInstBean = new InstrumentBean();
			//
			logger.info("IOCL INST MAP : "+lTmpMap.toString());
			
			lValue = (Object)lTmpMap.get(PARAM_UNIQUEID);
			pProcessInformationBean.setUID(lValue!=null?lValue.toString():"");
			if(ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
    			lTmpMap.put("id", getLong2(lTmpMap.get(PARAM_INSTRUMENTNUMBER)));
			}else{
				String[] lParts = CommonUtilities.splitString((String)lValue,KEY_SEPERATOR);
				//
				if(lParts!=null && lParts.length > 1){
					lTmpMap.put("id",Long.parseLong(lParts[0]));
				}
			}
			//
			List<String> defaultListFields = Arrays.asList(new String[]{});
			lValue = (Object)lTmpMap.get(PARAM_RETURNSTATUS);
			Map <String, List<String>> lStatuswiseFieldgroupMap = new HashMap<String, List<String>>();
			List<String> lTmpFieldGrp = null;
			if (VALUES_RETURN_STATUS_SUCCESS.equals(((String)lValue))) {
				lTmpMap.put("status",Status.Counter_Approved.getCode());
				lStatuswiseFieldgroupMap.put(Status.Counter_Approved.getCode(), instCntrAppFields);
				if(ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
					lTmpFieldGrp = new ArrayList<String>();
					lTmpMap.put("amount", getBigDecimal(lTmpMap.get("INVOICE_AMOUNT")));
					//we will recalculate netAmount
					//lTmpMap.put("netAmount", getBigDecimal(lTmpMap.get("PAYMENT_AMOUNT")));
					if(lTmpMap.get("DEDUCTION_AMOUNT")!=null){
						//we will put the adjAmount in tdsAmount
						lTmpMap.put("tdsAmount", getBigDecimal(lTmpMap.get("DEDUCTION_AMOUNT")));
						lTmpFieldGrp.add("tdsAmount");
					}
					if(lTmpMap.get("INVOICE_CRDT_PERIOD")!=null){
						lTmpMap.put("creditPeriod", getLong(lTmpMap.get("INVOICE_CRDT_PERIOD")));
						lTmpFieldGrp.add("creditPeriod");
					}
					lTmpFieldGrp.add("status");
					lTmpFieldGrp.add("id");
					lTmpFieldGrp.add("amount");
					//lTmpFieldGrp.add("netAmount");
				}
			}else if (VALUES_RETURN_STATUS_REJECT.equals(((String)lValue))) {
				lTmpMap.put("status",Status.Counter_Rejected.getCode());
				lStatuswiseFieldgroupMap.put(Status.Counter_Rejected.getCode(), instCntrRejFields);
			}else if (VALUES_RETURN_STATUS_RETURN.equals(((String)lValue))) {
				lTmpMap.put("status",Status.Counter_Returned.getCode());
				lStatuswiseFieldgroupMap.put(Status.Counter_Returned.getCode(), instCntrRetFields);
			}else{
				throw new CommonBusinessException("Please enter a valid status.");
			}
			if (lTmpMap.containsKey("ERROR_DESC")){
				lValue = (Object)lTmpMap.get("ERROR_DESC");
				lTmpMap.put("statusRemarks",(String) lValue);
			}
			if(lTmpFieldGrp==null || lTmpFieldGrp.isEmpty()){
				lTmpFieldGrp = lStatuswiseFieldgroupMap.get((String)lTmpMap.get("status"));
			}
			List<ValidationFailBean> lValidationFailBeans = instrumentDAO.getBeanMeta().validateAndParse(lInstBean, lTmpMap, null, lTmpFieldGrp , null);
			if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)){
                throw new CommonValidationException(lValidationFailBeans);
			}
			lReturnMap = instrumentDAO.getBeanMeta().formatAsMap(lInstBean);
			pProcessInformationBean.setKey(lInstBean.getId().toString());
	        pProcessInformationBean.setProcessedTredsData(lReturnMap);
	        return true;
		}
		return false;
	}
	
	private Object getBigDecimal(Object pValue){
		Object lValue = null;
        if (pValue != null)
        {
            if (pValue instanceof BigDecimal){
            	return pValue;
            }else if (pValue instanceof Long){
            	lValue = new BigDecimal(((Long)pValue).longValue());
            }
            else if (pValue instanceof Integer){
            	lValue = new BigDecimal(((Integer)pValue).longValue());
            }
            else if (pValue instanceof String)
            {
            	if(CommonUtilities.hasValue((String) pValue)){
                	lValue = new BigDecimal((String)pValue);
            	}
            }
        }
		return lValue;
	}
	private Object getLong(Object pValue){
		Object lValue = null;
        if (pValue != null)
        {
            if (pValue instanceof Long){
            	lValue = (Long)pValue;
            }
            else if (pValue instanceof Integer){
            	lValue = Long.valueOf(((Integer)pValue).longValue());
            }
            else if (pValue instanceof String)
            {
            	if(CommonUtilities.hasValue((String) pValue)){
                	lValue = Long.valueOf((String)pValue);
            	}
            }
        }
		return lValue;
	}
	private Long getLong2(Object pValue){
		Long lValue = null;
        if (pValue != null)
        {
            if (pValue instanceof Long){
            	lValue = (Long)pValue;
            }
            else if (pValue instanceof Integer){
            	lValue = Long.valueOf(((Integer)pValue).longValue());
            }
            else if (pValue instanceof String)
            {
            	if(CommonUtilities.hasValue((String) pValue)){
                	lValue = Long.valueOf((String)pValue);
            	}
            }
        }
		return lValue;
	}

	private boolean convertTredsPurchaserSupplierLinkBeanToClientBean(ProcessInformationBean pProcessInformationBean) throws Exception {
		Map<String,Object> lClientDataMap = null;
		PurchaserSupplierLinkBean lPSLinkBean = (PurchaserSupplierLinkBean)pProcessInformationBean.getTredsDataForProcessing();
		if(lPSLinkBean != null){
			List<String> lFieldList = Arrays.asList(new String[] { "supplier", "purchaser", "creditPeriod", "bidAcceptingEntityType", "costBearingType", "chargeBearer", "settleLeg3Flag", "autoAccept", "autoAcceptableBidTypes", "autoConvert", "purchaserAutoApproveInvoice", "invoiceMandatory"});
			lClientDataMap = purchaserSupplierLinkDAO.getBeanMeta().formatAsMap(lPSLinkBean, null, lFieldList, false);
			/*			
			"UID": " RX123456789", PUR^SUP^RECVER
			"IOCL_VEN_CODE": "1234567890", GSTN
			"VEN_REGION": "HR", STATE
			"VEN_REG_AGENCY": "XXXXXXXXXX", SUPPLIER
			"RXIL_REG_NO": "XXXXXXXXX", //CIN = U67190MH2016PLC273522 - GSTN = 27AAHCR6707P12P //CAN BE FETCHED FROM REGISTRY
			"TYPE_OF_CONTRACT": "02",
			"TYPE_OF_DATA": "N",
			"VALIDITY_PERIOD": "28.09.2020" //INFactorMaxEndDateTime CHANGE FORMAT
			*/	
			String lUID = lPSLinkBean.getPurchaser()+KEY_SEPERATOR+lPSLinkBean.getSupplier();
			lUID += KEY_SEPERATOR + DBHelper.getInstance().getUniqueNumber(pProcessInformationBean.getConnection(), lUID).toString();
			lClientDataMap.put(PARAM_UNIQUEID,lUID);
			pProcessInformationBean.setUID(lUID);
			CompanyLocationBean lSupplierCLBean = null;
			try {
				lSupplierCLBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pProcessInformationBean.getConnection(),lPSLinkBean.getSupplier());
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			lClientDataMap.put(PARAM_VENDORCODE,lSupplierCLBean.getGstn());
			lClientDataMap.put(PARAM_VENDORREGION,TredsHelper.getInstance().getGSTStateDesc(lSupplierCLBean.getState()));
			lClientDataMap.put(PARAM_VENDORREGAGENCY,"RXIL");
			lClientDataMap.put(PARAM_RXILREGNO,lPSLinkBean.getSupplier() );
			lClientDataMap.put("TYPE_OF_CONTRACT","");
			lClientDataMap.put("TYPE_OF_DATA","01");
			lClientDataMap.put("VALIDITY_PERIOD","31-12-2099");
			//
			pProcessInformationBean.setProcessedClientData(lClientDataMap);
			pProcessInformationBean.setKey(lPSLinkBean.getSupplier());
			return true;
		}
		return false;
	}
	
	private boolean convertTredsInstrumentBeanToClientBean(ProcessInformationBean pProcessInformationBean){
		Map<String,Object> lClientDataMap = null;
		InstrumentBean lInstBean = (InstrumentBean)pProcessInformationBean.getTredsDataForProcessing();
		if(lInstBean != null){
			lInstBean.populateNonDatabaseFields();
			List<String> lFields = Arrays.asList(new String[]{ "poNumber","poDate","extendedCreditPeriod","goodsAcceptDate","statDueDate","extendedDueDate","adjAmount","netAmount","tdsAmount","makerLoginId"});
			lClientDataMap = instrumentDAO.getBeanMeta().formatAsMap(lInstBean, null, lFields, false, true);
			//
			String lUID = null;
			//lClientDataMap = new HashMap<String,Object>();
			if(ProcessInformationBean.PROCESSID_INST_ACK.equals(pProcessInformationBean.getProcessId())){
				lUID = pProcessInformationBean.getUID();
			}else{
				lUID = lInstBean.getId()+KEY_SEPERATOR+lInstBean.getRecordVersion();
				lClientDataMap.put(PARAM_UNIQUEID,lUID);
			}
			lClientDataMap.put(PARAM_VENDORCODE,lInstBean.getSupGstn());
			lClientDataMap.put(PARAM_RXILREGNO,lInstBean.getSupplier());
			lClientDataMap.put("MATURITY_DATE",FormatHelper.getDisplay(DATE_FORMAT,lInstBean.getMaturityDate()));
			lClientDataMap.put(PARAM_INSTRUMENTNUMBER,lInstBean.getId());
			lClientDataMap.put(PARAM_VENDORREGAGENCY,"RXIL");
			pProcessInformationBean.setUID(lUID);
			pProcessInformationBean.setKey(lInstBean.getId().toString());
			if(ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())){
				lClientDataMap.put(PARAM_VENDORREGION,TredsHelper.getInstance().getGSTStateDesc(lInstBean.getSupGstState()));
				lClientDataMap.put("INVOICE_NUMBER",lInstBean.getInstNumber());
				lClientDataMap.put("PO_NUMBER",lInstBean.getPoNumber());
				lClientDataMap.put("INVOICE_DATE",FormatHelper.getDisplay(DATE_FORMAT,lInstBean.getInstDate()));
				lClientDataMap.put("INVOICE_AMOUNT",getAsString(lInstBean.getAmount()));
				lClientDataMap.put("INVOICE_DUEDATE",FormatHelper.getDisplay(DATE_FORMAT,lInstBean.getInstDueDate()));
				lClientDataMap.put("IOCL_GRN_NO","");
				lClientDataMap.put("IOCL_GRN_DATE",FormatHelper.getDisplay(DATE_FORMAT,lInstBean.getGoodsAcceptDate()));
				lClientDataMap.put("IOCL_GST_NO",lInstBean.getPurGstn());
				lClientDataMap.put("VEN_GST_NO",lInstBean.getSupGstn());
				lClientDataMap.put("INVOICE_CRDT_PERIOD",lInstBean.getCreditPeriod());
				lClientDataMap.put("INVOICE_UPL_ON",FormatHelper.getDisplay(DATE_FORMAT,getInvoiceUploadTime(lInstBean.getId())));
				lClientDataMap.put("INVOICE_UPL_BY",lInstBean.getMakerLoginId());
				//3 items to be sent
				//1. tdsAmount, 2. netAmount (NETAMOUNT) as PAYMENT_AMOUNT and 3. cashDiscount
			}else if (ProcessInformationBean.PROCESSID_INST_ACK.equals(pProcessInformationBean.getProcessId())){
				lClientDataMap.put(PARAM_UNIQUEID,pProcessInformationBean.getUID());
				lClientDataMap.put("LAST_FACTOR_DATE",FormatHelper.getDisplay(DATE_FORMAT,lInstBean.getFactorMaxEndDateTime()));
				if (InstrumentBean.Status.Counter_Approved.equals(lInstBean.getStatus()) || 
						InstrumentBean.Status.Converted_To_Factoring_Unit.equals(lInstBean.getStatus()) ){
					lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_COUNTER_STATUS_SUCCESS);
				}else if ( InstrumentBean.Status.Counter_Returned.equals(lInstBean.getStatus()) ||
						InstrumentBean.Status.Counter_Rejected.equals(lInstBean.getStatus())){
					lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_COUNTER_STATUS_FAIL);
					if (lInstBean.getStatusRemarks()!=null){
						lClientDataMap.put("ERROR_DESC",lInstBean.getStatusRemarks());
					}
				}
			}
			for(String lKey : lClientDataMap.keySet()){
				if(lClientDataMap.get(lKey) == null){
					lClientDataMap.put(lKey, "");
				}
			}
			//
			pProcessInformationBean.setProcessedClientData(lClientDataMap);
			pProcessInformationBean.setKey(lInstBean.getId().toString());
			return true;
		}
		return false;
	}

	private boolean  convertTredsFactoredBeanToClientBean(ProcessInformationBean pProcessInformationBean){
		Map<String,Object> lClientDataMap = null;
		Object[] lTmpObj = (Object[]) pProcessInformationBean.getTredsDataForProcessing();
		FactoringUnitBean lFactBean = null;
		InstrumentBean lInstrumentBean = null;
		ObligationBean lObligationBean = null;
		List<ObligationSplitsBean> lSplitList = null;
		if (ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessInformationBean.getProcessId())){
			lFactBean = (FactoringUnitBean)lTmpObj[0];
			lInstrumentBean = (InstrumentBean)lTmpObj[1];
		}else if (ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessInformationBean.getProcessId())){
			lFactBean = (FactoringUnitBean) lTmpObj[0];
			lInstrumentBean = (InstrumentBean) lTmpObj[1];
			lObligationBean = (ObligationBean) lTmpObj[2];
			lSplitList = (List<ObligationSplitsBean>) lTmpObj[3];
		}
		String lUID = "";
		if(lInstrumentBean!=null){
			lUID = getUID(pProcessInformationBean);
			lClientDataMap = new HashMap<String,Object>();
			lClientDataMap.put(PARAM_UNIQUEID,lUID);
			lClientDataMap.put(PARAM_VENDORCODE,lInstrumentBean.getSupGstn());
			lClientDataMap.put(PARAM_RXILVENDORCODE,lInstrumentBean.getSupplier());
			lClientDataMap.put(PARAM_RXILREGNO,lInstrumentBean.getSupplier());
			lClientDataMap.put(PARAM_INSTRUMENTNUMBER,lInstrumentBean.getId());
			lClientDataMap.put("INVOICE_DATE",FormatHelper.getDisplay(DATE_FORMAT,lInstrumentBean.getInstDate()));
			lClientDataMap.put("INVOICE_AMOUNT",getAsString(lInstrumentBean.getAmount()));
			lClientDataMap.put("INVOICE_DUEDATE",FormatHelper.getDisplay(DATE_FORMAT,lInstrumentBean.getInstDueDate()));
			lClientDataMap.put(PARAM_RXILVENDORCODE,lInstrumentBean.getSupplier());
			
			/*
			 		--------------- Success/Rejection Flag - S/R ---------------
			*/
			//F=Factored W=Withdraw S=L1 Success R=L1 Fail
			
			lClientDataMap.put(PARAM_VENDORREGAGENCY,"RXIL");

			if (lObligationBean!=null && !lSplitList.isEmpty()){
				lClientDataMap.put("PAYMENT_AMOUNT",getAsString(lObligationBean.getSettledAmount()));
				lClientDataMap.put("PAYMENT_DATE",FormatHelper.getDisplay(DATE_FORMAT,lObligationBean.getSettledDate()));
				List<String> lTxnIds = new ArrayList<String>();
				List<String> lBankDetails = new ArrayList<String>();
				for (ObligationSplitsBean lOBSBean : lSplitList ){
					lTxnIds.add(lOBSBean.getPaymentRefNo());
					lBankDetails.add(lOBSBean.getPayDetail1());
				}
				lClientDataMap.put("UTR_NUMBER",lTxnIds);
				lClientDataMap.put("BANK_DETAILS",lBankDetails);
				if(ObligationBean.Status.Success.equals(lObligationBean.getStatus())){
					lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_LEG1SETTLED);
				}else{
					lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_LEG1FAILED);
				}
			}else{
				lClientDataMap.put("PAYMENT_AMOUNT","");
				lClientDataMap.put("PAYMENT_DATE","");
				lClientDataMap.put("UTR_NUMBER","");
				lClientDataMap.put("BANK_DETAILS","");
				if(lFactBean!=null){
					if(FactoringUnitBean.Status.Withdrawn.equals(lFactBean.getStatus())){
						lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_FACTORINGUNIT_WITHDRAWN);
					}else if(FactoringUnitBean.Status.Expired.equals(lFactBean.getStatus())){
						lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_EXPIRED);
					}
				}else{
					if(InstrumentBean.Status.Expired.equals(lInstrumentBean.getStatus())){
						lClientDataMap.put(PARAM_RETURNSTATUS,VALUES_EXPIRED);
					}
				}
			}
			pProcessInformationBean.setProcessedClientData(lClientDataMap);
			pProcessInformationBean.setKey(lInstrumentBean.getId().toString());
			pProcessInformationBean.setUID(lUID);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean connectClient() {
		logger.info(LOG_HEADER+"connectClient() called.");
		//call the login api and check
		try{
			logger.info("connectClient URL : "+clientSettingsBean.getClientUrl());
			logger.info("Certificate Path : "+clientSettingsBean.getCertificatePath()!=null?clientSettingsBean.getCertificatePath():"");
			if(CommonUtilities.hasValue(clientSettingsBean.getCertificatePath())){
				SSLConfigurationProvider lConfigurationProvider = new SSLConfigurationProvider(clientSettingsBean.getCertificatePath(), clientSettingsBean.getCertificateIdentityPassword(), clientSettingsBean.getCertificateAlias(),null, null);
				ioclClient = new RestClient(clientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort(), lConfigurationProvider);
			}else{
				ioclClient = new RestClient(clientSettingsBean.getClientUrl(),TredsHelper.getInstance().getProxyIp(),TredsHelper.getInstance().getProxyPort());
			}
		}catch(Exception lException){
			lException.printStackTrace();
			logger.info(LOG_HEADER+"Error : "+lException.getMessage());
		}
		return true;
	}

	@Override
	public boolean isClientConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean sendResponseToClient(ProcessInformationBean pProcessInformationBean) throws Exception {
		logger.info("Send Response called  ");
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		lApiRequestBean.setBody(convertTredsDataToClientData(pProcessInformationBean));
		logger.info("DATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
		RestClient.addBasicAuthentication(lApiRequestBean, clientSettingsBean.getClientUsername() , clientSettingsBean.getClientPassword() );
		ApiResponseBean lRespBean = null;
		//logging in the response before checking in connectivity so that if the connectivity is down, the failed response can be send later
		AdapterRequestResponseBean lARRBean = new AdapterRequestResponseBean();
		logOutgoing(pProcessInformationBean, getURL(pProcessInformationBean.getProcessId()), lRespBean, lARRBean, true);
		connectClient();
		lRespBean = ioclClient.sendRequest(getURL(pProcessInformationBean.getProcessId()), lApiRequestBean, MediaType.APPLICATION_JSON);
		logger.info("Response From IOCL : "+lRespBean.getStatusCode());
		logger.info("Response From IOCL : "+lRespBean.getResponseText());
		logOutgoing(pProcessInformationBean, getURL(pProcessInformationBean.getProcessId()), lRespBean, lARRBean, false);
		//
		return false;
	}
	
	@Override
	public boolean reSendResponseToClient(ProcessInformationBean pProcessInformationBean, AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception {
		logger.info("Send Response called  ");
		ApiRequestBean lApiRequestBean = new ApiRequestBean();
		lApiRequestBean.setMethod(HttpMethod.POST);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		System.out.println(pAdapterRequestResponseBean.getApiRequestData());
		Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pAdapterRequestResponseBean.getApiRequestData());
		lApiRequestBean.setBody(new JsonBuilder(lMap).toString());
		logger.info("DATA TO CLIENT : "+ lApiRequestBean.getBody().toString());
		RestClient.addBasicAuthentication(lApiRequestBean, clientSettingsBean.getClientUsername() , clientSettingsBean.getClientPassword() );
		connectClient();
		ApiResponseBean lRespBean = null;
		lRespBean = ioclClient.sendRequest(pAdapterRequestResponseBean.getApiRequestUrl(), lApiRequestBean, MediaType.APPLICATION_JSON);
		logger.info("Response From IOCL : "+lRespBean.getStatusCode());
		logger.info("Response From IOCL : "+lRespBean.getResponseText());
		logOutgoing(pProcessInformationBean, pAdapterRequestResponseBean.getApiRequestUrl(),lRespBean, pAdapterRequestResponseBean, false );
		return (AppConstants.HTTP_RESPONSE_STATUS_200_OK.equals(lRespBean.getStatusCode()) || AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED.equals(lRespBean.getStatusCode()));
	}
	@Override
	public String getURL(Long pProcessId){
		if(ProcessInformationBean.PROCESSID_INST.equals(pProcessId)){
			//
			return "BC_INSTDET/ZMSME_INST_DET";
		}else if(ProcessInformationBean.PROCESSID_PURSUPLINK.equals(pProcessId)){
			//
			return "BC_VENLIN/ZVENLIN";
		}else if(ProcessInformationBean.PROCESSID_INST_ACK.equals(pProcessId)){
			//URL??
			return "BC_INSTDET/PAYACK";
		}else if(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessId)){
			//URL??
			return "BC_INSTDET/PAYSTATUS";
		}else if(ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessId)){
			//URL??
			return "BC_INSTDET/PAYSTATUS";
		}
		return null;
	}

	@Override
	public Long logOutgoing(ProcessInformationBean pProcessInformationBean, String pOutUrl, ApiResponseBean pApiResponseBean,AdapterRequestResponseBean pARRBean, boolean pNew) {
        Long lArrId = null;
		try (Connection lConnection = DBHelper.getInstance().getConnection()) {
			lConnection.setAutoCommit(false);
	        if (pNew) {
	        	pARRBean.setType(Type.Out);
	        	pARRBean.setEntityCode(entityCode);
	        	pARRBean.setKey(pProcessInformationBean.getKey());
	        	pARRBean.setProcessId(pProcessInformationBean.getProcessId());
	        	pARRBean.setApiRequestType(ApiRequestType.POST);
	        	pARRBean.setApiRequestUrl(getURL(pProcessInformationBean.getProcessId()));
	        	pARRBean.setApiRequestData(new JsonBuilder(pProcessInformationBean.getProcessedClientData()).toString());
	        	pARRBean.setUid(pProcessInformationBean.getUID()); //set uid from above data map
	        	pARRBean.setTimestamp(CommonUtilities.getCurrentDateTime());
	        	pARRBean.setRequestStatus(RequestStatus.Sent);
	        	pARRBean.setResponseAckStatus(ResponseAckStatus.Not_Read);
	        	if(pApiResponseBean==null){
	        		//the api response is sent null when we have to log the outgoing request before sending
	        		//hence keeping it failed, so that if the connector is not connected, the same can be sent later.
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
	        	}else{
		        	if (AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED.equals(pApiResponseBean.getStatusCode())){
		        		pARRBean.setApiResponseStatus(ApiResponseStatus.Success);
		        	}else{
		        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
		        	}
		        	if (pApiResponseBean.getResponseText()!=null  &&pApiResponseBean.getResponseText()!=null){
			        	pARRBean.setApiResponseData(pApiResponseBean.getResponseText());
		        	}
	        	}
	            adapterRequestResponseDAO.insert(lConnection, pARRBean);
	            lConnection.commit();
	            lArrId = pARRBean.getId();
	        } else {
	        	if (pApiResponseBean.getStatusCode()!=null  && pApiResponseBean.getStatusCode().equals(AppConstants.HTTP_RESPONSE_STATUS_202_ACCEPTED)){
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Success);
	        	}else{
	        		pARRBean.setLastSendDateTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        		pARRBean.setApiResponseStatus(ApiResponseStatus.Failed);
	        	}
	        	adapterRequestResponseDAO.update(lConnection, pARRBean, BeanMeta.FIELDGROUP_UPDATE);
	        	lConnection.commit();
	            lArrId = pARRBean.getId();
	        }
		} catch (Exception e) {
			logger.info("Error in logOutgoing : "+e.getMessage());
		}
		return lArrId;
	}
	

	@Override
	public Long logInComing(ProcessInformationBean pProcessInformationBean, String pInCommingUrl, ApiResponseStatus pApiResponseStatus, boolean pNew, boolean pValidateUniqueRequest) throws CommonBusinessException {
		Long lArrId = null;
		AdapterRequestResponseBean lARRBean = new AdapterRequestResponseBean();
		try (Connection lConnection = DBHelper.getInstance().getConnection()) {
			lConnection.setAutoCommit(false);
	        if (pNew) {
	        	JsonSlurper lJsonSlurper = new JsonSlurper();
	        	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pProcessInformationBean.getClientDataForProcessing().toString());
	        	//
	        	lARRBean.setType(Type.In);
	        	lARRBean.setApiRequestType(ApiRequestType.POST);
	        	lARRBean.setApiRequestUrl(pInCommingUrl);
	        	lARRBean.setEntityCode(entityCode);
	        	lARRBean.setKey(pProcessInformationBean.getKey());
	        	if(pProcessInformationBean.getKey() == null){
	        		if(lMap.get(PARAM_INSTRUMENTNUMBER) instanceof String){
						lARRBean.setKey((String)lMap.get(PARAM_INSTRUMENTNUMBER));
	        		}else{
		        		if(getLong(lMap.get(PARAM_INSTRUMENTNUMBER))!=null){
		        			lARRBean.setKey(getLong(lMap.get(PARAM_INSTRUMENTNUMBER)).toString());
		        		}
	        		}
	        	}
	        	lARRBean.setProcessId(pProcessInformationBean.getProcessId());
	        	if(ProcessInformationBean.PROCESSID_PURSUPLINK.equals(pProcessInformationBean.getProcessId())){
		        	lARRBean.setUid(lMap.get("id").toString()); //set uid from above data map
		        	lMap.put(PARAM_UNIQUEID, lMap.get("id").toString());
	        	}else{
		        	lARRBean.setUid(lMap.get(PARAM_UNIQUEID).toString()); //set uid from above data map
	        	}
	        	//find if already exists
	        	if(pValidateUniqueRequest){
		        	AdapterRequestResponseBean lOldARRBean = adapterRequestResponseDAO.findBean(lConnection, lARRBean);
		        	if(lOldARRBean!=null){
		        		throw new CommonBusinessException("UID " +lOldARRBean.getUid() + " already received on "+FormatHelper.getDisplay("dd-MM-yyyy HH:mm:SS",lOldARRBean.getTimestamp())+".");
		        	}
	        	}
	        	//
	        	lARRBean.setApiRequestData(pProcessInformationBean.getClientDataForProcessing().toString());
	        	lARRBean.setTimestamp(CommonUtilities.getCurrentDateTime());
	        	lARRBean.setRequestStatus(null);
	        	lARRBean.setResponseAckStatus(ResponseAckStatus.Not_Read);
/*
 *            	private String uid;
	            private RequestStatus requestStatus;
	            private ResponseAckStatus responseAckStatus;
	            
	            private String apiResponseUrl;
	            private String apiResponseData;
	            private String apiResponseStatus;
	            private String apiResponseDataReturned;
	            //private ProvResponseAckStatus provResponseAckStatus;
	            //private String provResponseData;
*/
	            adapterRequestResponseDAO.insert (lConnection, lARRBean, BeanMeta.FIELDGROUP_INSERT);
	            lConnection.commit();
	            pProcessInformationBean.setAdapterRequestResponseBean(lARRBean);
	            lArrId = lARRBean.getId();
	        } else {
	        	lARRBean = pProcessInformationBean.getAdapterRequestResponseBean();
	        	if(lARRBean !=null){
		        	if (lARRBean.getApiRequestUrl().equals(pInCommingUrl)){
		        		lARRBean.setApiResponseData(pProcessInformationBean.getTredsReturnResponseData().toString());
		        		lARRBean.setApiResponseStatus(pApiResponseStatus);
		        		if (adapterRequestResponseDAO.update(lConnection, lARRBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
		        			throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		        		lConnection.commit();
		        	}
		            lArrId = lARRBean.getId();
	        	}else{
	        		logger.info("Duplicate received.");
	        	}
/*	        	lOldAdapterRequestResponseBean = adapterRequestResponseDAO.findByPrimaryKey(lConnection, pFilterBean);
	            if (adapterRequestResponseDAO.update(lConnection, pAdapterRequestResponseBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
	                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
*/	        }

		} catch (Exception e) {
			logger.info("Error in logInComing : "+ e.getMessage());
			throw new CommonBusinessException(e.getMessage());
		}
		return lArrId;
	}
	

	@Override
	public void addPostActionToQueue(ProcessInformationBean pOldProcessInformationBean) {
	}

	@Override
	public void performActionPostIncoming(ProcessInformationBean pOldProcessInformationBean) {
		//
		//when IOCL provisionally returns or rejects; and or
		//when IOCL finally approves or return or rejects
		Connection lConnection = null;
		if(ProcessInformationBean.PROCESSID_INST.equals(pOldProcessInformationBean.getProcessId()) ||
				ProcessInformationBean.PROCESSID_INST_PRE.equals(pOldProcessInformationBean.getProcessId())){
			InstrumentBean lInstBean = new InstrumentBean();
			String[] lParts = CommonUtilities.splitString(pOldProcessInformationBean.getUID(),KEY_SEPERATOR);
			//
			try {
				lConnection = DBHelper.getInstance().getConnection();
				if(ProcessInformationBean.PROCESSID_INST.equals(pOldProcessInformationBean.getProcessId())){
					Map<String, Object> lTmpMap =  (Map<String, Object>) pOldProcessInformationBean.getClientDataForProcessing();
					if(lTmpMap!=null){
						lInstBean.setId(getLong2(lTmpMap.get(PARAM_INSTRUMENTNUMBER)));
					}
				}else{
					if(lParts!=null && lParts.length > 1){
						lInstBean.setId(Long.parseLong(lParts[0]));
					}
				}
				lInstBean = instrumentDAO.findByPrimaryKey(lConnection, lInstBean);
				lInstBean.populateNonDatabaseFields();
				if (ProcessInformationBean.PROCESSID_INST_PRE.equals(pOldProcessInformationBean.getProcessId()) && 
						Status.Counter_Approved.equals(lInstBean.getStatus())){
	 				//no action on success
				}else{
					//new adapter will be created
					ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_INST_ACK, DBHelper.getInstance().getConnection());
					lProcessInformationBean.setTredsDataForProcessing(lInstBean);
					lProcessInformationBean.setKey(lInstBean.getId().toString());
					lProcessInformationBean.setUID(pOldProcessInformationBean.getUID());
					sendResponseToClient(lProcessInformationBean);
				}
			} catch (Exception e) {
				logger.info("Error in performActionPostIncoming : "+e.getMessage());
			}
		}else if (ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pOldProcessInformationBean.getProcessId())){
			String[] lParts = CommonUtilities.splitString(pOldProcessInformationBean.getUID(),KEY_SEPERATOR);
			//
			try {
				ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS, pOldProcessInformationBean.getConnection());
				lProcessInformationBean.setKey(pOldProcessInformationBean.getKey());
				lProcessInformationBean.setTredsDataForProcessing(pOldProcessInformationBean.getTredsDataForProcessing());
				lProcessInformationBean.setUID(getUID(lProcessInformationBean));
				sendResponseToClient(lProcessInformationBean);
			} catch (Exception e) {
				logger.info("Error in performActionPostIncoming : "+e.getMessage());
			}
		}else if (ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pOldProcessInformationBean.getProcessId())){
			//String[] lParts = CommonUtilities.splitString(pOldProcessInformationBean.getUID(),KEY_SEPERATOR);
			/*
			 * what will be the uid here???????
			*/
			try {
				ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_LEG1SETTLED, pOldProcessInformationBean.getConnection());
				lProcessInformationBean.setKey(pOldProcessInformationBean.getKey());
				lProcessInformationBean.setTredsDataForProcessing(pOldProcessInformationBean.getTredsDataForProcessing());
				lProcessInformationBean.setUID(getUID(lProcessInformationBean));
				sendResponseToClient(lProcessInformationBean);
			} catch (Exception e) {
				logger.info("Error in performActionPostIncoming : "+e.getMessage());
			}
		}
	}
	
	public Timestamp getInvoiceUploadTime(Long pInId){
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuffer lSql = new StringBuffer();
		lSql.append("Select min(actiontime) ACTIONTIME from instruments_a where inid = ");
		lSql.append(pInId).append(" and action = ").append(lDbHelper.formatString(AuditAction.Insert.getCode()));
		logger.info(lSql.toString());
		try(Connection lConnection = lDbHelper.getConnection();
				Statement lStatement =  lConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
			while (lResultSet.next()){
				return lResultSet.getTimestamp("ACTIONTIME");
			}
		} catch (Exception e) {
			logger.info("Error in getInvoiceUploadTime : "+e.getMessage());
		}
		return null;
	}
	
	private String getUID(ProcessInformationBean pProcessInformationBean){
		String lKey = null;
	 	if (ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS.equals(pProcessInformationBean.getProcessId()) ||
	 			ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pProcessInformationBean.getProcessId()) ){
			DBHelper lDbHelper = DBHelper.getInstance();
			StringBuffer lSql = new StringBuffer();
			lSql.append("SELECT ARRUID FROM AdapterRequestResponses WHERE ARRRecordVersion > 0 ");
			lSql.append(" AND ( ");
			lSql.append(" ARRKey = ").append(lDbHelper.formatString(pProcessInformationBean.getKey()));
			lSql.append(" OR ");
			lSql.append(" ARRKey = ").append(lDbHelper.formatString("00000000000000000"+ pProcessInformationBean.getKey()));
			lSql.append(" ) ");
			lSql.append(" AND ARRProcessId = ").append(ProcessInformationBean.PROCESSID_INST.longValue()) ;
			lSql.append(" ORDER BY ARRId DESC ");
			logger.info(lSql.toString());
			//TODO: We can use pProcessInformationBean.getConnection(), but will have to check that the same is being filled in pProcessInformationBean always....
			try(Connection lConnection = lDbHelper.getConnection();
					Statement lStatement =  lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
				while (lResultSet.next()){
					if(lKey == null){
						lKey = lResultSet.getString("ARRUID");
					}
				}
			} catch (Exception e) {
				logger.info("Error in getKey : " + e.getMessage());
			}
	 	}else{
	 		lKey  = pProcessInformationBean.getKey();
	 	}
		return lKey;
	}
	
    private String getAsString(Object pValue){
    	if(pValue == null){
    		return "";
    	}else{
    		return pValue.toString();
    	}
    }

	
}
