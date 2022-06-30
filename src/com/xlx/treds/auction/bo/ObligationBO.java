package com.xlx.treds.auction.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.AssignmentNoticeDetailsBean;
import com.xlx.treds.auction.bean.AssignmentNoticeGroupDetailsBean;
import com.xlx.treds.auction.bean.AssignmentNoticeInfo;
import com.xlx.treds.auction.bean.AssignmentNoticeWrapperBean;
import com.xlx.treds.auction.bean.AssignmentNoticesBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FactoredDetailBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.MISFinancierReportBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationDetailInfoBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyLocationBean.LocationType;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.GstSummaryBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class ObligationBO {
    public static Logger logger = Logger.getLogger(ObligationBO.class);
    
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private CompositeGenericDAO<ObligationDetailInfoBean> obligationDetailInfoDAO;
    private CompositeGenericDAO<FactoredBean> factoredBeanDAO;
    private CompositeGenericDAO<AssignmentNoticeInfo> assignmentNoticeInfoDAO;
    private CompositeGenericDAO<MISFinancierReportBean> misFinancierReportDAO;
    private GenericDAO<ObligationSplitsBean>  obligationSplitsDAO; 
    private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<ObligationExtensionBean> obligationExtDAO;
    //
    public static final String RESP_RECORD_SEPARATOR = "\r\n";
    public static final String RECORD_SEPARATOR = "\n";
    public static final String FIELD_SEPARATOR = "|";
    public static final String DATE_FORMAT = AppConstants.DATE_FORMAT;
    //
    public ObligationBO() {
        super();
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        obligationDetailInfoDAO = new CompositeGenericDAO<ObligationDetailInfoBean>(ObligationDetailInfoBean.class);
        factoredBeanDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
        assignmentNoticeInfoDAO = new CompositeGenericDAO<AssignmentNoticeInfo>(AssignmentNoticeInfo.class);
        misFinancierReportDAO = new CompositeGenericDAO<MISFinancierReportBean>(MISFinancierReportBean.class);
        obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        obligationExtDAO = new GenericDAO<ObligationExtensionBean>(ObligationExtensionBean.class);
    }
    
    public ObligationBean findBean(ExecutionContext pExecutionContext, 
        ObligationBean pFilterBean) throws Exception {
        ObligationBean lObligationBean = obligationDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lObligationBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lObligationBean;
    }
    
    public List<ObligationBean> findList(Connection pConnection, ObligationBean pFilterBean, 
        List<String> pColumnList, AppUserBean pUserBean) throws Exception {
        StringBuilder lFilter = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        java.sql.Date lFromDate = pFilterBean.getDate();
        pFilterBean.setDate(null);
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            pFilterBean.setTxnEntity(pUserBean.getDomain());
        //
        obligationDAO.appendAsSqlFilter(lFilter, pFilterBean, false);
        if (lFromDate != null) {
            lFilter.append(" AND OBDate >= ").append(lDBHelper.formatDate(lFromDate));
        }
        if (pFilterBean.getFilterToDate() != null) {
            lFilter.append(" AND OBDate <= ").append(lDBHelper.formatDate(pFilterBean.getFilterToDate()));
        }
        if(pFilterBean.getBillingStatus()!=null){
        	if(ObligationBean.BillingStatus.Billed.equals(pFilterBean.getBillingStatus()))
        		lFilter.append(" AND OBBillId IS NOT NULL ");
        	else if(ObligationBean.BillingStatus.UnBilled.equals(pFilterBean.getBillingStatus()))
        		lFilter.append(" AND OBBillId IS NULL AND OBtxnEntity = ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
        }
        lFilter.append(" ORDER BY OBDate ASC");
        
        StringBuilder lSql = new StringBuilder();
        if(pFilterBean.isForSettlementReport()) {
            lSql.append(" SELECT Obligations.* ");
            lSql.append(" , FUACCEPTDATETIME OBRECORDCREATETIME ");
            lSql.append(" , CHILDCOUNT obrecordversion ");
            lSql.append(" , nvl(nvl(PURPenalty.OEPALLOWEXTENSION,DEFPenalty.OEPALLOWEXTENSION),'N') \"OBAllowExtension\" ");
            lSql.append(" FROM Obligations ");
            lSql.append(" join factoringunits on (obfuid = factoringunits.fuid) ");
            lSql.append(" join INSTFUINVOICECOUNT on (factoringunits.fuid = INSTFUINVOICECOUNT.infuid) ");
            lSql.append(" left outer join OBLIGATIONEXTENSIONPENALTIES DEFPenalty  on (DEFPenalty.OEPFINANCIER = fuFINANCIER and DEFPenalty.OEPPURCHASER='DEFAULT' and OBTXNENTITY=FUPURCHASER) ");
        	lSql.append(" left outer join OBLIGATIONEXTENSIONPENALTIES PURPenalty  on (PURPenalty.OEPFINANCIER = fuFINANCIER and PURPenalty.OEPPURCHASER=FUPURCHASER and OBTXNENTITY=FUPURCHASER) ");
        }else {
        	lSql.append("SELECT * FROM Obligations ");
        }
        
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        boolean lCheckLocation = !AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) &&
        		TredsHelper.getInstance().isLocationwiseSettlementEnabled( pConnection, lAppEntityBean.getCdId(),false);
        if (lCheckLocation && !AppUserBean.Type.Admin.equals(pUserBean.getType())){
            if(pFilterBean.isForSettlementReport()) {
            	lSql.append(" join instruments on (instruments.infuid = factoringunits.fuid) ");
            }else {
            	lSql.append(" join Factoringunits on obfuid = fuid ");
            	lSql.append(" join instruments on (infuid = fuid) ");
            }
        }
        lSql.append(" WHERE 1=1 ");
        if(pFilterBean.isForSettlementReport()) {
            if(pFilterBean.getFilterBidAcceptFromDate()!=null) {
            	lSql.append(" AND FUACCEPTDATETIME >= ").append(lDBHelper.formatDate(pFilterBean.getFilterBidAcceptFromDate()));
            }
            if(pFilterBean.getFilterBidAcceptToDate()!=null) {
            	lSql.append(" AND FUACCEPTDATETIME <= ").append(lDBHelper.formatDate(pFilterBean.getFilterBidAcceptToDate()));
            }
        }
  
        if (lCheckLocation && !AppUserBean.Type.Admin.equals(pUserBean.getType()) && TredsHelper.getInstance().checkAccessToLocations(pUserBean)){
			if(lAppEntityBean.isPurchaser()){
        		lSql.append(" AND INPurClId in (").append(TredsHelper.getInstance()
        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");
			}else if(lAppEntityBean.isSupplier()){
	        	lSql.append(" AND INSupClId in (").append(TredsHelper.getInstance()
	        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");				
			}
        }
        if(lFilter.length() > 0 )
        	lSql.append(" AND ").append(lFilter);
        return obligationDAO.findListFromSql(pConnection, lSql.toString(), -1);
    }
    
    public Map<String, Object> findJsonForFU(ExecutionContext pExecutionContext, ObligationBean pFilterBean, AppUserBean pUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
    	List<ObligationBean> lList = obligationDAO.findList(lConnection, pFilterBean, (String)null);
        if (lList != null) {
        	List<ObligationBean> lExtendedList = new ArrayList<ObligationBean>();
        	//remove the extended obligations
            for(int lPtr=lList.size()-1; lPtr >= 0; lPtr--){
        		if(lList.get(lPtr).getStatus().equals(ObligationBean.Status.Extended)){
        			lExtendedList.add(lList.remove(lPtr));
        		}
        	}
            if(lList.size() == 0 && lExtendedList.size() > 0){
            	lList.addAll(lExtendedList);
            }
        	//
            boolean lMatches = false;
            List<Map<String,Object>> lDebits = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> lCredits = new ArrayList<Map<String,Object>>();
            boolean lLeg1 = false;
            int lObligCount=0;
            for (ObligationBean lObligationBean : lList) {
                if (lObligationBean.getTxnEntity().equals(pUserBean.getDomain())) 
                    lMatches = true;
                Map<String, Object> lEntry = new HashMap<String, Object>();
                lEntry.put("e", lObligationBean.getTxnEntity());
                lEntry.put("ename", lObligationBean.getTxnEntityName());
                lEntry.put("a", lObligationBean.getAmount());
                lEntry.put("n", (lObligationBean.getNarration()!=null?lObligationBean.getNarration():""));
                if (lObligationBean.getTxnType() == ObligationBean.TxnType.Debit)
                    lDebits.add(lEntry);
                else
                    lCredits.add(lEntry);
                if(ObligationBean.Type.Leg_1.equals(lObligationBean.getType())){
                	lLeg1 = true;
                	if(ObligationBean.Status.Success.equals(lObligationBean.getStatus())){
                    	lObligCount++;
                	}
                }
            }
            if (!lMatches && !AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            Map<String, Object> lData = new HashMap<String, Object>();

        	lData.put("noa", (lLeg1 && lList.size() == lObligCount));
    		lData.put("date", BeanMetaFactory.getInstance().getDateFormatter().format(pFilterBean.getDate()));
            lData.put("fuId", pFilterBean.getFuId());
            lData.put("d", lDebits);
            lData.put("c", lCredits);
            
            if (pFilterBean.getFuId() != null) {
                FactoringUnitBean lFilterBean = new FactoringUnitBean();
                lFilterBean.setId(pFilterBean.getFuId());
                FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(lConnection, lFilterBean);
                if (lFactoringUnitBean != null) {
                    lData.put("amt", lFactoringUnitBean.getFactoredAmount());
                	lData.put("cost", lFactoringUnitBean.getTotalCost());
                    //
                    lData.put("currency", lFactoringUnitBean.getCurrency());
                    //in the breakup show for both legs
                    List<Map<String, Object>> lGstSummaryMapList = new ArrayList<Map<String, Object>>();
                    Map<String, Object> lGstSummaryMap = null;
                    lData.put("chgSumm",lGstSummaryMapList);
                    for(String lEntityCode : lFactoringUnitBean.getEntitiesCharged()){
                    	if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) || pUserBean.getDomain().equals(lEntityCode)) {
                    		GstSummaryBean lGstSummaryBean = lFactoringUnitBean.getGstSummary(lEntityCode);
                            if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO)!=0){
                                lGstSummaryMap = new HashMap<String, Object>();
                                setChargesToMap(lGstSummaryMap,lGstSummaryBean,lEntityCode);
                                lGstSummaryMapList.add(lGstSummaryMap);
                            }
                            lGstSummaryBean = lFactoringUnitBean.getGstSummary(lEntityCode,ChargeType.Split,null);
                            if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO)!=0){
                                lGstSummaryMap = new HashMap<String, Object>();
                                setChargesToMap(lGstSummaryMap,lGstSummaryBean,lEntityCode);
                                lGstSummaryMapList.add(lGstSummaryMap);
                            }
                    	}
                    }
                }
        		if (pUserBean.getDomain().equals(lFactoringUnitBean.getPurchaser())){
        			//check the factoring unit status to be l1 settled 
                    List<FactoringUnitBean.Status> lStatusList = new ArrayList<FactoringUnitBean.Status>();
                    lStatusList.add(FactoringUnitBean.Status.Leg_1_Settled);
                    lStatusList.add(FactoringUnitBean.Status.Leg_1_Failed);
                    lStatusList.add(FactoringUnitBean.Status.Leg_2_Failed);
                    lStatusList.add(FactoringUnitBean.Status.Leg_2_Settled);
                    lStatusList.add(FactoringUnitBean.Status.Leg_3_Generated);
                    if (lStatusList.contains(lFactoringUnitBean.getStatus())){
                    	List<ObligationDetailBean> lOBDetailsList = TredsHelper.getInstance().getObligationDetailBean(lConnection, pFilterBean.getFuId(), null, null);
                    	List<Map<String, Object>> lTxnList = new ArrayList<Map<String, Object>>();
                    	if(!lOBDetailsList.isEmpty()){
                    		ObligationBean lParentBean = null;
                			ObligationSplitsBean lSplitBean = null;
                			Map<String, Object> lTxnEntry =null;
                    		for(ObligationDetailBean lOBDetailBean:lOBDetailsList){
                    			lParentBean = lOBDetailBean.getObligationBean();
                    			lSplitBean = lOBDetailBean.getObligationSplitsBean();
                    			lSplitBean.setParentObligation(lParentBean);
                    			lTxnEntry = new HashMap<String, Object>();
                    			if(TxnType.Credit.equals(lParentBean.getTxnType()) && 
                    					lFactoringUnitBean.getSupplier().equals(lParentBean.getTxnEntity()) ){
                    				if((Status.Success.equals(lSplitBean.getStatus()) ||
                    						Status.Failed.equals(lSplitBean.getStatus()) ||
                    							Status.Cancelled.equals(lSplitBean.getStatus()) )){
                    					lTxnEntry.put("entity", lParentBean.getTxnEntityName());
                    					lTxnEntry.put("amt", lSplitBean.getSettledAmount());
                        				lTxnEntry.put("status", lSplitBean.getStatus());
                        				lTxnEntry.put("payRefNo", lSplitBean.getPaymentRefNo());
                        				lTxnEntry.put("date", lSplitBean.getSettledDate());
                        				lTxnList.add(lTxnEntry);
                    				}
                    			}
                    		}
                			lData.put("txnList", lTxnList);
                    	}
                    }
        		}
            }
            return lData;
        }
        return null;
    }
    
    private void setChargesToMap(Map<String, Object> pGstSummaryMap, GstSummaryBean pGstSummaryBean,String pEntityCode) throws Exception {
        pGstSummaryMap.put("ent", TredsHelper.getInstance().getAppEntityBean(pEntityCode).getName());
        pGstSummaryMap.put("chg", pGstSummaryBean.getCharge());
        if(pGstSummaryBean.getCgstValue().compareTo(BigDecimal.ZERO)!=0){
        	pGstSummaryMap.put("cgst", pGstSummaryBean.getCgst());
        	pGstSummaryMap.put("cgstSurcharge", pGstSummaryBean.getCgstSurcharge());
        	pGstSummaryMap.put("cgstValue", pGstSummaryBean.getCgstValue());
        }
        if(pGstSummaryBean.getSgstValue().compareTo(BigDecimal.ZERO)!=0){
        	pGstSummaryMap.put("sgst", pGstSummaryBean.getSgst());
        	pGstSummaryMap.put("sgstSurcharge", pGstSummaryBean.getSgstSurcharge());
        	pGstSummaryMap.put("sgstValue", pGstSummaryBean.getSgstValue());
        }
        if(pGstSummaryBean.getIgstValue().compareTo(BigDecimal.ZERO)!=0){
        	pGstSummaryMap.put("igst", pGstSummaryBean.getIgst());
        	pGstSummaryMap.put("igstSurcharge", pGstSummaryBean.getIgstSurcharge());
        	pGstSummaryMap.put("igstValue", pGstSummaryBean.getIgstValue());
        }
		
	}

	public Map<String, Object> findJsonForSplitObligations(ExecutionContext pExecutionContext, ObligationSplitsBean pFilterBean, AppUserBean pUserBean) throws Exception {
    	 List<ObligationSplitsBean> lList = obligationSplitsDAO.findList(pExecutionContext.getConnection(), pFilterBean, new ArrayList<String>());;
         if (lList != null) {
        	 Map<String, Object> lData = new HashMap<String, Object>();
        	 List<Object> lDataList = new ArrayList<Object>();
        	 Map<String, Object> lSplitData = null;
             for (ObligationSplitsBean lObligationSplitBean : lList) {
            	 lSplitData = new HashMap<String, Object>();
                 lSplitData.put("obid", lObligationSplitBean.getObid());
                 lSplitData.put("partNumber", lObligationSplitBean.getPartNumber());
                 lSplitData.put("amount", lObligationSplitBean.getAmount());
                 lSplitData.put("status", lObligationSplitBean.getStatus());
                 lSplitData.put("settledAmount", lObligationSplitBean.getSettledAmount());
                 lSplitData.put("paymentRefNo", lObligationSplitBean.getPaymentRefNo());
                 lSplitData.put("responseCode", lObligationSplitBean.getRespRemarks());
                 lSplitData.put("settlor", lObligationSplitBean.getPaymentSettlor());
                 lSplitData.put("remarks", lObligationSplitBean.getRespRemarks());
                 if (!Status.Cancelled.equals(lObligationSplitBean.getStatus()) && TredsHelper.getInstance().getValidOldStatusForModification().contains(lObligationSplitBean.getStatus().getCode()) && lObligationSplitBean.getSettlorProcessed()==null){	
               	 	lSplitData.put("modify",true);
             	 }
                 lDataList.add(lSplitData);
             }
             lData.put("splitlist", lDataList);
             return lData;
         }
         return null;
    }
    
    public List<FactoredDetailBean>  getObligationReport(Connection pConnection, AppUserBean pUserBean, FactoringUnitBean pFilterFUBean, ObligationBean pFilterObliBean, InstrumentBean pInstrumentBean) throws Exception{
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<FactoredBean> lFactoredUnits = null;
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        //
        lSql.append("SELECT * FROM FactoringUnits, Bids, INSTRUMENTS ");
        lSql.append(" WHERE INRECORDVERSION > 0 ");
        lSql.append(" AND FUID = INFUID "); 
        lSql.append(" AND FUBDID = BDID "); 

        StringBuilder lFUFilter = new StringBuilder();
        lFUFilter.append(" FURecordVersion > 0 ");
        
        lFUFilter.append(" AND FUStatus != ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lFUFilter.append(" AND FUAcceptDateTime IS NOT NULL ");

        if(lAppEntityBean.isPlatform()){
        	//all should be considered
        }else if(lAppEntityBean.isFinancier()){
        	lFUFilter.append(" AND FUFINANCIER = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        }else if(lAppEntityBean.isPurchaser()){
        	lFUFilter.append(" AND FUPurchaser = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        }else if(lAppEntityBean.isSupplier()){
        	lFUFilter.append(" AND FUSupplier = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        }
        if(pInstrumentBean != null){
        	if(pInstrumentBean.getId() != null){
        		lSql.append(" AND INID = ").append(pInstrumentBean.getId());
        	}
        	if(pInstrumentBean.getGoodsAcceptDate() != null){
        		lSql.append(" AND INGOODSACCEPTDATE = ").append(lDBHelper.formatDate(pInstrumentBean.getGoodsAcceptDate()));
        	}
        	if(pInstrumentBean.getStatDueDate() != null){
        		lSql.append(" AND INSTATDUEDATE = ").append(lDBHelper.formatDate(pInstrumentBean.getStatDueDate()));
        	}
        }
        
        if(pFilterFUBean.getId()!=null){
        	lFUFilter.append(" AND FUId = ").append(pFilterFUBean.getId());
        }

        if(pFilterObliBean.getDate()!=null ||
        		pFilterObliBean.getFilterToDate()!=null || 
        		pFilterObliBean.getStatus()!=null || pFilterObliBean.getType() !=null ||
        		pFilterObliBean.getTxnType() !=null || pFilterObliBean.getPfId()!=null ){
        	lFUFilter.append(" AND FUId IN ( ");
        	lFUFilter.append(" SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBRecordVersion > 0 ");
            if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
            	lFUFilter.append(" AND OBTXNENTITY = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            }else{
            	if (StringUtils.isNotEmpty(pFilterObliBean.getTxnEntity())){
            		lFUFilter.append(" AND OBTXNENTITY = ").append(lDBHelper.formatString(pFilterObliBean.getTxnEntity()));
            	}
            }
            if(pFilterObliBean.getDate()!=null){
            	lFUFilter.append(" AND OBDate >= ").append(lDBHelper.formatDate(pFilterObliBean.getDate()));
            }
            if(pFilterObliBean.getFilterToDate()!=null){
            	lFUFilter.append(" AND OBDate <= ").append(lDBHelper.formatDate(pFilterObliBean.getFilterToDate()));
            }
            if(pFilterObliBean.getStatus()!=null){
            	lFUFilter.append(" AND OBSTATUS = ").append(lDBHelper.formatString(pFilterObliBean.getStatus().getCode()));
            }
            if(pFilterObliBean.getType()!=null){//Leg
            	lFUFilter.append(" AND OBTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getType().getCode()));
            }
            if(pFilterObliBean.getTxnType()!=null){
            	lFUFilter.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getTxnType().getCode()));
            }
            if(pFilterObliBean.getPfId()!=null){
            	lFUFilter.append(" AND OBPFID = ").append(pFilterObliBean.getPfId().toString());
            }
            //lFUFilter.append(" AND OBDate >= ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate()));
            lFUFilter.append(" ) ");
        }
        lSql.append(" AND ").append(lFUFilter);
        lSql.append(" ORDER BY FUId");
        lFactoredUnits =  factoredBeanDAO.findListFromSql(pConnection, lSql.toString(), -1);
        //
        List<FactoredDetailBean> lFactoredDetailBeans = new ArrayList<FactoredDetailBean>();
        HashMap<Long, FactoredDetailBean> lFactoredUnitHash = new HashMap<Long, FactoredDetailBean>();
        FactoredDetailBean lFactoredDetailBean = null;
        //
        for(FactoredBean lFactoredBean : lFactoredUnits){
        	lFactoredDetailBean = new FactoredDetailBean(lFactoredBean);
        	lFactoredUnitHash.put(lFactoredBean.getFactoringUnitBean().getId(), lFactoredDetailBean);
        	//lFactoredDetailBeans.add(lFactoredDetailBean);
        }

        if (lFactoredUnits.size() > 0){
            lSql.setLength(0);
            lSql.trimToSize();
            //
        	lSql.append(" SELECT APPENTITIES.*, COMPANYDETAILS.*, PAYMENTFILES.*, FACILITATORENTITYMAPPING.*,  COMPANYBANKDETAILS.*, OBLIGATIONS.* ");
        	lSql.append(" ,CASE WHEN (CompanyLoc.CLNAME is not null) THEN REPLACE(RegLoc.CLNAME, ',', ' ')  " );
        	lSql.append(" ELSE REPLACE(RegLoc.CLNAME, ',', ' ')  END AS \"OBSettlementLocationName\" ");
        	lSql.append(" ,CASE WHEN (CompanyLoc.CLCITY is not null) THEN REPLACE(RegLoc.CLCITY, ',', ' ')  " );
        	lSql.append(" ELSE REPLACE(RegLoc.CLCITY, ',', ' ')  END AS \"OBSettlementLocationCity\" ");
        	lSql.append(" , CHILDCOUNT as \"OBInstrumentCount\" ");
        	lSql.append(" FROM OBLIGATIONS ");
            lSql.append(" JOIN APPENTITIES ON OBTXNENTITY = AECODE ");
            lSql.append(" LEFT OUTER JOIN COMPANYDETAILS ON ( AECDID = CDID AND CDRECORDVERSION > 0 ) "); 
            lSql.append(" LEFT OUTER JOIN PAYMENTFILES  ON  OBPFID = PFID ");
            //TODO: change the query to accomodate multiple facilitators
            lSql.append(" LEFT OUTER JOIN FACILITATORENTITYMAPPING ON ( FEMFACILITATOR = 'NPCI' AND FEMENTITYCODE = OBTXNENTITY AND FEMRECORDVERSION > 0 ) ");
            lSql.append(" LEFT OUTER JOIN COMPANYBANKDETAILS ON ( FEMCBDID = CBDID  AND CBDRECORDVERSION > 0  ) ");
            lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS CompanyLoc ON  OBSETTLEMENTCLID = CompanyLoc.CLID ");
            lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS RegLoc  ON  (RegLoc.CLCDID = CDID AND RegLoc.CLLOCATIONTYPE = ").append(lDBHelper.formatString(LocationType.RegOffice.getCode())).append(" ) ");
            lSql.append(" join INSTFUINVOICECOUNT on (OBfuid = INSTFUINVOICECOUNT.infuid) ");
            lSql.append("  WHERE OBRECORDVERSION > 0 AND AERECORDVERSION > 0 "); 
            lSql.append(" AND OBFUID IN ( SELECT FUId FROM FactoringUnits WHERE ").append(lFUFilter).append(" ) ");
            
            List<ObligationDetailInfoBean> lObligationDetailInfos = obligationDetailInfoDAO.findListFromSql(pConnection, lSql.toString(),-1);

            CompanyBankDetailBean lTredsAccount = TredsHelper.getInstance().getTredsChargeAccount();
            
            for (ObligationDetailInfoBean lODBean : lObligationDetailInfos) {
            	lFactoredDetailBean = lFactoredUnitHash.get(lODBean.getObligationBean().getFuId());
            	if(lFactoredDetailBean == null){
            		//data inconsistency - this will not arise for live data
            		logger.info("Data inconsistency (Check Bid for FUID : " + lODBean.getObligationBean().getFuId());
            		continue;
            	}
            	if(AppConstants.DOMAIN_PLATFORM.equals(lODBean.getObligationBean().getTxnEntity())){
            		lODBean.setCompanyBankDetailBean(lTredsAccount);
            	}
            	//updating the Company Bank details from the obligation itself so as to server properly for old records
            	if(lODBean.getObligationBean().getPfId()!=null){
            		if(lODBean.getObligationBean().getPayDetail1()!=null && 
            				lODBean.getObligationBean().getPayDetail2()!=null){
            			lODBean.getCompanyBankDetailBean().setAccNo(lODBean.getObligationBean().getPayDetail1());
            			lODBean.getCompanyBankDetailBean().setIfsc(lODBean.getObligationBean().getPayDetail2());
            		}
            	}
            	lFactoredDetailBean.addObligationDetailInfo(lODBean);
             }
            for(FactoredDetailBean lFDBean : lFactoredUnitHash.values()){
            	lFactoredDetailBeans.add(lFDBean);
            }
        }
        return lFactoredDetailBeans;
    }
    
    public StringBuilder  getObligationReportFileData(Connection pConnection, AppUserBean pUserBean, FactoringUnitBean pFilterFUBean, ObligationBean pFilterObliBean, InstrumentBean pInstrumentBean) throws Exception{
    	 final StringBuilder lData  = new StringBuilder();
    	 //
         List<FactoredDetailBean> lFactoredDetailBeans = getObligationReport(pConnection, pUserBean, pFilterFUBean, pFilterObliBean,pInstrumentBean);
         //
     	//for displaying header
         AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
         lData.append(getObligationReportHeader(pUserBean, lAppEntityBean));
     	//display data rowwise
     	FactoringUnitBean lFUBean = null;
     	InstrumentBean lInstruBean = null;
     	ObligationDetailInfoBean lObliDetailInfoBean = null;
 		List<ObligationDetailInfoBean> lObliDetailInfoBeans = new ArrayList<ObligationDetailInfoBean>();
     	//Leg1_D_Fin
     	//Leg1_D_Buy (optional)
     	//Leg1_C_Sell 
     	//Leg1_C_TREDS (optional)
     	//Leg2_D_Buy
     	//Leg2_C_Fin
         for (FactoredDetailBean lFactoredDetailBean : lFactoredDetailBeans) {
         	lFUBean = lFactoredDetailBean.getFactoringUnitBean();
         	lInstruBean = lFactoredDetailBean.getInstrumentBean();
         	lInstruBean.populateNonDatabaseFields();
         	//
         	lData.append(lInstruBean.getId()).append(",");
         	lData.append(lInstruBean.getGoodsAcceptDate()).append(",");
         	lData.append(lInstruBean.getStatDueDate()).append(",");
         	lData.append(lInstruBean.getInstDueDate()).append(",");
     		lData.append(lFUBean.getId()).append(",");
         	if(!lAppEntityBean.isFinancier()){
         		lData.append(cleanData(lInstruBean.getInstNumber())).append(",");
         		lData.append(cleanData(lInstruBean.getAmount())).append(",");
         		lData.append(cleanData(lInstruBean.getAdjAmount())).append(","); //"Deductions"
         		lData.append(cleanData(lInstruBean.getTdsAmount())).append(",");
         		if (cleanData(lInstruBean.getPoNumber()).toString().contains(",")){
					lData.append("\"").append(cleanData(lInstruBean.getPoNumber())).append("\"").append(",");
				}else{
					lData.append(cleanData(lInstruBean.getPoNumber())).append(",");
				}
         	}
     		lData.append(cleanData(lFUBean.getAmount())).append(",");
     		lData.append(cleanData(lFUBean.getFactoredAmount())).append(",");
     		lData.append(cleanData(lInstruBean.getSupMsmeStatus())).append(",");
     		//
     		lObliDetailInfoBeans.clear();
         	//Leg1_D_Fin
     		lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_1,TxnType.Debit, EntityType.Financier));
         	//Leg1_D_Buy (optional)
     		lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_1,TxnType.Debit, EntityType.Purchaser));
         	//Leg1_C_Sell 
     		lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_1,TxnType.Credit, EntityType.Supplier));
         	//Leg1_C_TREDS (optional)  == 3
     		lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_1,TxnType.Credit, EntityType.Platform));
     		if(!lAppEntityBean.isSupplier()){
             	//Leg2_D_Buy
     			lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_2,TxnType.Debit, EntityType.Purchaser));
             	//Leg2_C_Fin
     			lObliDetailInfoBeans.add(lFactoredDetailBean.getObligationDetailInfo(Type.Leg_2,TxnType.Credit, EntityType.Financier));
     		}
     		//
     		for(int lPtr=0; lPtr < lObliDetailInfoBeans.size(); lPtr++){
     			lObliDetailInfoBean = lObliDetailInfoBeans.get(lPtr);

 				if(lObliDetailInfoBean!=null){
             		lData.append(cleanData(lObliDetailInfoBean.getCompanyDetailBean().getCompanyName())).append(",");
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getTxnEntity())).append(",");
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getSettlementLocationName())).append(",");
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getSettlementLocationCity())).append(",");
             		if(lPtr !=3 || lAppEntityBean.isPlatform()){ // 3 is the optional Leg1 Platform Credit
                 		lData.append(cleanData(lObliDetailInfoBean.getAppEntityBean().getPan())).append(",");
                 		lData.append(cleanData(lObliDetailInfoBean.getCompanyBankDetailBean().getIfsc())).append(",");
                 		lData.append(cleanData(lObliDetailInfoBean.getCompanyBankDetailBean().getAccNo())).append(",");
                 	}else{
             			lData.append(",,,");    			
             		}    			
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getId())).append(",");            			
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getAmount())).append(",");
             		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getDate())).append(",");
         		}else{
         			lData.append(",,,,,,,,,,");
         		}
     		}
         	// here show conditional is loggedinuser bearing the cost
     		boolean lShowCost = ( lAppEntityBean.isPlatform() || lAppEntityBean.isFinancier() || 
     				(lAppEntityBean.isPurchaser() && lFUBean.isPurchaserCostBearer()) ||
     						(lAppEntityBean.isSupplier() && lFUBean.isSupplierCostBearer()) );
     		boolean lShowCharge = (lAppEntityBean.isPlatform() || 
     				(lAppEntityBean.isPurchaser() && CostBearer.Buyer.equals(lFUBean.getChargeBearer()) ||
     						(lAppEntityBean.isSupplier() && CostBearer.Seller.equals(lFUBean.getChargeBearer()))));
     		//if(lShowCost)lData.append(cleanData(lFUBean.getCostBearer().toString()));
     		//
     		if(lShowCost)lData.append(cleanData(lInstruBean.getCostBearingType()));
     		//
     		lData.append(",");
     		if(lShowCost) lData.append(cleanData(lFactoredDetailBean.getBidBean().getCostLeg())); //"Interest Cost leg"
     		lData.append(",");
         	if(!lAppEntityBean.isFinancier()){
             	if(lShowCharge) lData.append(cleanData(lFUBean.getChargeBearer()));
             	lData.append(",");
         	}
         	if(lShowCost) lData.append(cleanData(lFUBean.getAcceptedRate()));
         	lData.append(",");
         	lData.append(lFUBean.getTotalCost());
     		lData.append(","); //"Interest Amount"
     		//
     		lData.append(cleanData(lFUBean.getAcceptedHaircut())).append(",");
     		if(lFUBean.getAcceptedHaircut()!=null && lFUBean.getAcceptedHaircut().longValue() >0){
     			lData.append(lFUBean.getAmount().multiply(lFUBean.getAcceptedHaircut()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP));
     		}
     		lData.append(",");//Haircut comma
     		lData.append(cleanData(lFUBean.getStatus())).append(",");
     		lData.append(cleanData(lFUBean.getPurchaserRef())).append(",");
     		lData.append(cleanData(lFUBean.getSupplierRef())).append(",");
     		lData.append(cleanData(FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,new Date(lFUBean.getAcceptDateTime().getTime())))).append(",");
     		if (lObliDetailInfoBean!=null && lObliDetailInfoBean.getObligationBean()!=null) {
     		lData.append(cleanData(lObliDetailInfoBean.getObligationBean().getInstrumentCount()));
     		}else {
     			lData.append(cleanData("0"));
     		}
     		lData.append("\n");
     	}
         //
    	 return lData;
    }
    private String getObligationReportHeader(AppUserBean pAppUserBean, AppEntityBean pAppEntityBean) throws Exception{
        StringBuilder lRetVal = new StringBuilder();
        
        lRetVal.append("Instrument number - Autogenerated by RXIL").append(",");
        lRetVal.append("Goods Acceptance Date").append(",");
        lRetVal.append("Statutory Due Date").append(",");
        lRetVal.append("Invoice Due Date").append(",");
        lRetVal.append("Factoring Unit Id - Autogenerated by RXIL").append(",");
        if(!pAppEntityBean.isFinancier()){
            lRetVal.append("Invoice no").append(",");
            lRetVal.append("Invoice Amount").append(",");
            lRetVal.append("Deductions").append(",");
            lRetVal.append("TDS").append(",");
            lRetVal.append("Purchase Order Number").append(",");
        }
        lRetVal.append("Factoring Unit value").append(",");
        lRetVal.append("Financed Amount").append(",");
        lRetVal.append("Supplier MSME Status").append(",");
        //
        //Leg1_D_Fin
        //Leg1_D_Buy (optional)
        //Leg1_C_Sell 
        //Leg1_C_TREDS (optional)
        //Leg2_D_Buy
        //Leg2_C_Fin
        //
        //Leg1_D_Fin
        lRetVal.append("Financier Name").append(",");
        lRetVal.append("Financier Code").append(",");
        lRetVal.append("Financier Location").append(",");
        lRetVal.append("Financier City").append(",");
        lRetVal.append("Financier PAN").append(",");
        lRetVal.append("Financier IFSC").append(",");
        lRetVal.append("Financier Account Number").append(",");
        lRetVal.append("Obligation Number - Autogenerated by RXIL").append(",");
        lRetVal.append("Debit Amount").append(",");
        lRetVal.append("Debit Date").append(",");
        //Leg1_D_Buy (optional)
        lRetVal.append("Buyer Name").append(",");
        lRetVal.append("Buyer Code").append(",");
        lRetVal.append("Buyer Location").append(",");
        lRetVal.append("Buyer City").append(",");
        lRetVal.append("Buyer PAN").append(",");
        lRetVal.append("Buyer Bank Branch IFSC").append(",");
        lRetVal.append("Buyer Account number").append(",");
        lRetVal.append("Obligation number - Autogenerated by RXIL").append(",");
        lRetVal.append("Interest Amount").append(",");
        lRetVal.append("Debit Date").append(",");
        //
        //Leg1_C_Sell 
        lRetVal.append("Seller Name").append(",");
        lRetVal.append("Seller Code").append(",");
        lRetVal.append("Seller Location").append(",");
        lRetVal.append("Seller City").append(",");
        lRetVal.append("Seller PAN").append(",");
        lRetVal.append("Seller IFSC").append(",");
        lRetVal.append("Seller Account number").append(",");
        lRetVal.append("Obligation number - Autogenerated by RXIL ").append(",");
        lRetVal.append("Amount to be credited to the Seller").append(",");
        lRetVal.append("Credit Date").append(",");
        //Leg1_C_TREDS (optional)
        lRetVal.append("Leg 1 Credit Party Name").append(",");
        lRetVal.append("Leg 1 Credit party Code").append(",");
        lRetVal.append("Location").append(",");
        lRetVal.append("City").append(",");
        lRetVal.append("Leg 1 Credit party PAN").append(",");
        lRetVal.append("Leg 1 Credit party IFSC").append(",");
        lRetVal.append("Leg 1 Credit party Account no").append(",");
        lRetVal.append("Leg 1 Credit Obligation ID").append(",");
        lRetVal.append("Leg 1 Credit Amount").append(",");
        lRetVal.append("Leg 1 Credit Date").append(",");
        //
        if(!pAppEntityBean.isSupplier()){
            //Leg2_D_Buy
            lRetVal.append("Buyer Name").append(",");
            lRetVal.append("Buyer Code").append(",");
            lRetVal.append("Buyer Location").append(",");
            lRetVal.append("Buyer City").append(",");
            lRetVal.append("Buyer PAN").append(",");
            lRetVal.append("Buyer Bank Branch IFSC").append(",");
            lRetVal.append("Buyer Account Number").append(",");
            lRetVal.append("Obligation Number - Autogenerated by RXIL").append(",");
            lRetVal.append("Debit amount").append(",");
            lRetVal.append("Debit date").append(",");
            //Leg2_C_Fin
            lRetVal.append("Financier Name").append(",");
            lRetVal.append("Financier Code").append(",");
	        lRetVal.append("Financier Location").append(",");
	        lRetVal.append("Financier City").append(",");
            lRetVal.append("Financier PAN").append(",");
            lRetVal.append("Financier IFSC").append(",");
            lRetVal.append("Financier Account Number").append(",");
            lRetVal.append("Obligation Number - Autogenerated by RXIL").append(",");
            lRetVal.append("Credit Amount").append(",");
            lRetVal.append("Credit Date").append(",");
        }
        //
        lRetVal.append("Cost bearer - Party which accepts the bill").append(",");
        lRetVal.append("Upfront Discounting").append(",");
        if(!pAppEntityBean.isFinancier()){
            lRetVal.append("Platform Charge Bearer").append(",");
        }
        lRetVal.append("Discounting Rate").append(",");
        lRetVal.append("Interest Amount").append(",");
        lRetVal.append("Haircut(%)").append(",");
        lRetVal.append("Haircut Amount").append(",");
        lRetVal.append("Transaction Status").append(",");
        lRetVal.append("Buyer Ref").append(",");
        lRetVal.append("Seller Ref").append(",");
        lRetVal.append("Accept Date Time").append(",");
        lRetVal.append("Invoice Count");
        lRetVal.append("\n");

        return lRetVal.toString();
    }

    private String cleanData(Object pData){
    	if(pData!=null){
    		return TredsHelper.getInstance().getSanatisedObject(pData).toString();
    	}
    	return "";
    }
    private String cleanData(BigDecimal pData){
    	if(pData!=null){
    		return pData.toString();
    	}
    	return "0";
    }

    public Map<String, Object> findJsonForNOA(Connection pConnection, Long pAssignmentNoticeId, AppUserBean pUserBean) throws Exception {
    	StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * ");
		lSql.append(" FROM ASSIGNMENTNOTICES, ASSIGNMENTNOTICEDETAILS ");
		lSql.append(" FULL OUTER JOIN ASSIGNMENTNOTICEGROUPDETAILS ON ANGGROUPINID=ANDINID ");
		lSql.append(" WHERE ANDANID = ANID  AND ANID = ").append(pAssignmentNoticeId);
		//
		List<AssignmentNoticeInfo> lANInfoBeans = assignmentNoticeInfoDAO.findListFromSql(pConnection, lSql.toString(), 0);

		Map<String, Object> lData = null;
		Date lSettlementDate = null;
		//
		HashMap<String, AssignmentNoticeWrapperBean> lANWrapperHash = new HashMap<String, AssignmentNoticeWrapperBean>();
		AssignmentNoticeWrapperBean lANWrapperBean = null;
		//
		AssignmentNoticesBean lANBean = null;
		for(AssignmentNoticeInfo lANInfoBean : lANInfoBeans){
			lANBean = lANInfoBean.getAssignmentNoticesBean();
			AssignmentNoticeDetailsBean lANDBean = lANInfoBean.getAssignmentNoticeDetailsBean();
    		AssignmentNoticeGroupDetailsBean lANGBean = lANInfoBean.getAssignmentNoticeGroupDetailsBean();
    		//
    		if(!lANWrapperHash.containsKey(lANBean.getKey())){
    			lANWrapperBean = new AssignmentNoticeWrapperBean(lANBean);
    			lANWrapperHash.put(lANBean.getKey(), lANWrapperBean);
    		}else{
        		lANWrapperBean = lANWrapperHash.get(lANBean.getKey());
    		}
    		lANWrapperBean.addDetails(lANDBean, lANBean.getKey());
    		lANWrapperBean.addChildDetails(lANGBean, lANBean.getKey());
		}

		for(AssignmentNoticeWrapperBean lWrapperBean : lANWrapperHash.values()){
			lANBean = lWrapperBean.getAssignmentNoticesBean();

			lData = new HashMap<String, Object>();
			//purchaser
			lData.put("pEntity", lANBean.getPurchaser());
			lData.put("pName", lANBean.getPurName());
			lData.put("pAdd1", lANBean.getPurLine1());
			lData.put("pAdd2", lANBean.getPurLine2());
			lData.put("pAdd3", lANBean.getPurLine3());
			lData.put("pCity", lANBean.getPurCity());
			lData.put("pZipCode", lANBean.getPurZipCode());
			lData.put("pDistrict", lANBean.getPurDistrict());
			lData.put("pState", lANBean.getPurState());
			//supplier
			lData.put("sEntity", lANBean.getSupplier());
			lData.put("sName", lANBean.getSupName());
			lData.put("sAdd1", lANBean.getSupLine1());
			lData.put("sAdd2", lANBean.getSupLine2());
			lData.put("sAdd3", lANBean.getSupLine3());
			lData.put("sCity", lANBean.getSupCity());
			lData.put("sZipCode", lANBean.getSupZipCode());
			lData.put("sDistrict", lANBean.getSupDistrict());
			lData.put("sState", lANBean.getSupState());
			//supplier bank info
			if(StringUtils.isNotEmpty(lANBean.getSupBankName())){
				lData.put("sDesigBank", lANBean.getSupBankName());
			}
			if(lANBean.getSupAccType()!=null){
				lData.put("sDesigBAnkAccType", lANBean.getSupAccType());
			}
			if(StringUtils.isNotEmpty(lANBean.getSupBankEmail())){
				lData.put("sDesigBankEmail", lANBean.getSupBankEmail());
			}
			//supplier address
			String lSupplierAddress = "";
			if(CommonUtilities.hasValue(lANBean.getFinLine1())) lSupplierAddress = lANBean.getSupLine1();
			if(CommonUtilities.hasValue(lANBean.getFinLine2())) lSupplierAddress += ", " + lANBean.getSupLine2();
			if(CommonUtilities.hasValue(lANBean.getFinLine3())) lSupplierAddress += ", " + lANBean.getSupLine3();
			if(CommonUtilities.hasValue(lANBean.getFinCity())) lSupplierAddress += ", " + lANBean.getSupCity();
			if(CommonUtilities.hasValue(lANBean.getFinZipCode())) lSupplierAddress += ", " + lANBean.getSupZipCode();
			if(CommonUtilities.hasValue(lANBean.getFinDistrict())) lSupplierAddress += ", " + lANBean.getSupDistrict();
			if(CommonUtilities.hasValue(lANBean.getFinState())) lSupplierAddress += ", " + lANBean.getSupState();
			lData.put("sAdd", lSupplierAddress);
			//financier
			lData.put("fEntity", lANBean.getFinancier());
			lData.put("fName", lANBean.getFinName());
			lData.put("fAdd1", lANBean.getFinLine1());
			lData.put("fAdd2", lANBean.getFinLine2());
			lData.put("fAdd3", lANBean.getFinLine3());
			lData.put("fCity", lANBean.getFinCity());
			lData.put("fZipCode", lANBean.getFinZipCode());
			lData.put("fDistrict", lANBean.getFinDistrict());
			lData.put("fState", lANBean.getFinState());
			//financier address
			String lAddress = "";
			if(CommonUtilities.hasValue(lANBean.getFinLine1())) lAddress = lANBean.getFinLine1();
			if(CommonUtilities.hasValue(lANBean.getFinLine2())) lAddress += ", " + lANBean.getFinLine2();
			if(CommonUtilities.hasValue(lANBean.getFinLine3())) lAddress += ", " + lANBean.getFinLine3();
			if(CommonUtilities.hasValue(lANBean.getFinCity())) lAddress += ", " + lANBean.getFinCity();
			if(CommonUtilities.hasValue(lANBean.getFinZipCode())) lAddress += ", " + lANBean.getFinZipCode();
			if(CommonUtilities.hasValue(lANBean.getFinDistrict())) lAddress += ", " + lANBean.getFinDistrict();
			if(CommonUtilities.hasValue(lANBean.getFinState())) lAddress += ", " + lANBean.getFinState();
			lData.put("fAdd", lAddress);
			//considering that for all fu's the banks will be the same
    		lData.put("fBankBranch0", lANBean.getFinBranchName()); 
    		lData.put("fBankIFSC0", lANBean.getFinIfsc()); //IFSC CODE
    		lData.put("fBankAcNo0", lANBean.getFinAccNo()); //ACCOUNT NO
    		//
    		lSettlementDate = lANBean.getBusinessDate();
    		//
			List<Map<String, Object>> lFactUnits = new ArrayList<Map<String, Object>>();
			int lFUCount = 0;
			BigDecimal lTotalFactAmt = new BigDecimal(0);
			//
    		for(AssignmentNoticeDetailsBean lANDBean : lWrapperBean.getDetails()){
    			if(lANDBean != null){
    				Map<String,Object> lFUBeanHash = new HashMap<String, Object>();
        			lFUBeanHash.put("invoiceNo", lANDBean.getInstNumber());
        			lFUBeanHash.put("invoiceDate", FormatHelper.getDisplay(DATE_FORMAT, lANDBean.getInstDate()));
        			lFUBeanHash.put("invoiceAmt", TredsHelper.getInstance().getFormattedAmount(lANDBean.getInstAmount(),true));
        			lFUBeanHash.put("fuId", lANDBean.getFuId());
        			lFUBeanHash.put("fuDate", FormatHelper.getDisplay(DATE_FORMAT, lANDBean.getFuDate()));
        			lFUBeanHash.put("fuAmt", TredsHelper.getInstance().getFormattedAmount(lANDBean.getFuFactoredAmount(),true));
        			lFUBeanHash.put("fuDueDate", FormatHelper.getDisplay(DATE_FORMAT, lANDBean.getFuDueDate()));
        			//
    				lFactUnits.add(lFUBeanHash);
    				//
    				List<AssignmentNoticeGroupDetailsBean> lChildBeans = lWrapperBean.getChildDetails(lANDBean.getInId());
        			if(lChildBeans!=null && lChildBeans.size() > 0){
        				List<Map<String,Object>> lChildList = new ArrayList<Map<String,Object>>();
        				for(AssignmentNoticeGroupDetailsBean lANGBean : lChildBeans){
        		    		Map<String,Object> lChildDataHash = new HashMap<String, Object>();
        	    			lChildDataHash.put("invoiceNo", lANGBean.getInstNumber());
        	    			lChildDataHash.put("invoiceDate", FormatHelper.getDisplay(DATE_FORMAT, lANGBean.getInstDate()));
        	    			lChildDataHash.put("invoiceAmt", TredsHelper.getInstance().getFormattedAmount(lANGBean.getInstAmount(),true));
        	    			lChildDataHash.put("fuId", lANDBean.getFuId());
        	    			lChildDataHash.put("fuDate", FormatHelper.getDisplay(DATE_FORMAT, lANDBean.getFuDate()));
        	    			lChildDataHash.put("fuAmt", TredsHelper.getInstance().getFormattedAmount(lANGBean.getNetAmount(),true));
        	    			lChildDataHash.put("fuDueDate", FormatHelper.getDisplay(DATE_FORMAT, lANDBean.getFuDueDate()));
        	    			lChildList.add(lChildDataHash);
        				}
        				lFUBeanHash.put("childList",lChildList);
        			}
        			//
    				lTotalFactAmt = lTotalFactAmt.add(lANDBean.getFuFactoredAmount());
    				lFUCount++;
        		}
    		}
        	if(lData!= null){
    			//
    			lData.put("fUnits", lFactUnits);
        		lData.put("fBankCount", 1);
        		//for email - currently not used - can be used in the pdf in future
        		lData.put("fuCount", lFUCount);
        		lData.put("totalFactAmt", TredsHelper.getInstance().getFormattedAmount(lTotalFactAmt, true));
        		lData.put("settlementDate", FormatHelper.getDisplay(DATE_FORMAT, lSettlementDate));
        	}
		}
		return lData;
    }
    
    public Map<String,List<MISFinancierReportBean>> getMISFinancierReport(Connection pConnection, AppUserBean pUserBean, java.util.Date pBusinessDate) throws Exception{
    	//EVENTS
    	//1. Day of factoring
    	//2. Day of Settlement L1 - Success
    	//3. Day of Settlement L2 - Success
    	//4. Extension scenario after L2 failure
    	//5. Day of Settlement of Extended L2 - Success
    	//
    	
    	Map<String,List<MISFinancierReportBean>> lData = null;
    	StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM OBLIGATIONS, FactoringUnits, INSTRUMENTS, FINANCIERAUCTIONSETTINGS ");
		lSql.append(" WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
		lSql.append(" AND OBFUID = FUID AND INFUID = FUID ");
		lSql.append(" AND OBTXNENTITY = FUFINANCIER ");
		lSql.append(" AND FASLEVEL = ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode())).append(" AND FASFINANCIER = FUFINANCIER AND FASPURCHASER = FUPURCHASER ");
		if(pUserBean!=null && CommonUtilities.hasValue(pUserBean.getDomain())){
			lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(pUserBean.getDomain()));
		}
		lSql.append( " AND ( ");
		lSql.append(" ( TRUNC(FUACCEPTDATETIME) = ").append(DBHelper.getInstance().formatDate(pBusinessDate)).append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode())).append(" ) ");		
		lSql.append(" OR ( OBSETTLEDDATE = ").append(DBHelper.getInstance().formatDate(pBusinessDate)).append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode())).append(" ) ");
		lSql.append( " ) ");
		//
		//
		lSql.append(" ORDER BY OBTXNENTITY, OBFUID, OBTYPE, OBTXNTYPE, OBDATE ");
		//
		List<MISFinancierReportBean> lTmpData = misFinancierReportDAO.findListFromSql(pConnection, lSql.toString(), 0);
		Map<Long, MISFinancierReportBean> lTmpHash = new HashMap<Long, MISFinancierReportBean>();
		if(lTmpData!=null && lTmpData.size() > 0){
			MISFinancierReportBean lTmpMISFinReportBean = null;
			String lFinancier = null;
			List<MISFinancierReportBean> lFinWiseFUList = null;
			//
			lData = new HashMap<String, List<MISFinancierReportBean>>();
			for(MISFinancierReportBean lMisFinReportBean : lTmpData){
				lFinancier = lMisFinReportBean.getFactoringUnitBean().getFinancier();
				//
				if(lTmpHash.containsKey(lMisFinReportBean.getFactoringUnitBean().getId())){
					lTmpMISFinReportBean = lTmpHash.get(lMisFinReportBean.getFactoringUnitBean().getId());
				}else{
					lTmpMISFinReportBean = lMisFinReportBean;
					lTmpHash.put(lMisFinReportBean.getFactoringUnitBean().getId(), lTmpMISFinReportBean);
				}
				lTmpMISFinReportBean.addObligation(lMisFinReportBean.getObligationBean()); //collecting obligations in first bean
				//
				if(!lData.containsKey(lFinancier)){
					lFinWiseFUList = new ArrayList<MISFinancierReportBean>();
					lData.put(lFinancier, lFinWiseFUList);
				}else{
					lFinWiseFUList = lData.get(lFinancier);
				}
				if(!lFinWiseFUList.contains(lTmpMISFinReportBean)){
					lFinWiseFUList.add(lTmpMISFinReportBean); //always first bean will be checked against the list
				}
			}
		}
		//
		return lData;
    }

    public StringBuilder getMISFinancierReportFileData(Connection pConnection, List<MISFinancierReportBean> pFUWiseObligationList, java.util.Date pBusinessDate) throws Exception{
    	final StringBuilder lData  = new StringBuilder();
    	//this is financier wise list of factoring units which contains a list of obligations
    	//
    	FactoringUnitBean lFUBean = null;
    	InstrumentBean lInstBean = null;
    	FinancierAuctionSettingBean lFASBean = null;
    	List<ObligationBean> lObliBeans = null;
    	AppEntityBean lAEFinBean = null, lAEBuyerBean = null, lAESellerBean=null;
    	String lFinancier = null, lFinAcNo = null, lDesc = null;
    	BigDecimal lTotalInt = new BigDecimal(0);
    	BigDecimal lAmount = new BigDecimal(0);
    	BigDecimal lHairCutAmt = new BigDecimal(0);
    	int lAmendments = 0;
    	boolean lDayFactoring = false;
    	java.util.Date lDate = pBusinessDate;
    	//
    	if(pFUWiseObligationList!=null && pFUWiseObligationList.size() > 0){
    		for(MISFinancierReportBean lMISFinReportBean: pFUWiseObligationList){
        		lFUBean = lMISFinReportBean.getFactoringUnitBean();
        		lInstBean = lMISFinReportBean.getInstrumentBean();
        		lObliBeans = lMISFinReportBean.getObligations();
        		lFASBean = lMISFinReportBean.getFinancierAuctionSettingBean();
        		//
        		lFinancier = lFUBean.getFinancier();
        		lAEFinBean = TredsHelper.getInstance().getAppEntityBean(lFinancier);
        		lAEBuyerBean = TredsHelper.getInstance().getAppEntityBean(lFUBean.getPurchaser());
        		lAESellerBean = TredsHelper.getInstance().getAppEntityBean(lFUBean.getSupplier());
        		lFinAcNo = TredsHelper.getInstance().getDesignatedBankAccountNumber(pConnection,lFinancier);
        		//
        		for(ObligationBean lObliBean : lObliBeans){
        			//determine the day
        			lDayFactoring = false;
        			if(ObligationBean.Type.Leg_1.equals(lObliBean.getType())){
        				if(CommonUtilities.getDate(lFUBean.getAcceptDateTime()).equals(lDate)){
        					lDayFactoring = true;
        				}
        			}
        			//
        			lData.append(cleanData(lFUBean.getId())).append(FIELD_SEPARATOR); //1. Instrument number - Autogenerated by RXIL
        			lData.append(cleanData(lFUBean.getAmount())).append(FIELD_SEPARATOR); //2. Factoring Unit value
        			lAmount = lFUBean.getAmount();
        			if(lFUBean.getAcceptedHaircut()!=null && lFUBean.getAcceptedHaircut().doubleValue() > 0){
    		    		lHairCutAmt = lFUBean.getAmount().multiply(BigDecimal.valueOf(lFUBean.getAcceptedHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);//17. Haircut Amount
        			}else{
        				lHairCutAmt = new BigDecimal(0);
        			}
    				lAmount = lAmount.subtract(lHairCutAmt);
        			lData.append(cleanData(lAmount)).append(FIELD_SEPARATOR); //3. Amount
        			lData.append(cleanData(lAEFinBean.getName())).append(FIELD_SEPARATOR); //4. Financier Name
        			if(ObligationBean.Status.Success.equals(lObliBean.getStatus())){
        				logger.info("Oblig Success : " + lObliBean.getPayDetail1());
            			lData.append(cleanData(lObliBean.getPayDetail1())).append(FIELD_SEPARATOR);//5.Financier Account Number     				
        			}else{
        				logger.info("Oblig FinAcNo : " + lFinAcNo);
            			lData.append(cleanData(lFinAcNo)).append(FIELD_SEPARATOR); //5. Financier Account Number 
        			}
        			lData.append(cleanData(lObliBean.getAmount())).append(FIELD_SEPARATOR);  //6. Debit Amount
        			lData.append(cleanData(FormatHelper.getDisplay(DATE_FORMAT, lFUBean.getLeg1Date()))).append(FIELD_SEPARATOR); //7. Debit Date
        			lData.append(cleanData(lAEBuyerBean.getName())).append(FIELD_SEPARATOR); //8. Buyer Name
        			lData.append(cleanData(lAEBuyerBean.getCode())).append(FIELD_SEPARATOR); //9. Buyer Code
        			lData.append(cleanData(lAEBuyerBean.getPan())).append(FIELD_SEPARATOR); //10. Buyer PAN
        			lTotalInt = new BigDecimal(0);
        			if(ObligationBean.Type.Leg_1.equals(lObliBean.getType())){
        				if(lFUBean.getPurchaserLeg1Interest()!=null) lTotalInt = lTotalInt.add(lFUBean.getPurchaserLeg1Interest());
        				if(lFUBean.getSupplierLeg1Interest()!=null) lTotalInt = lTotalInt.add(lFUBean.getSupplierLeg1Interest());
        			}else if(ObligationBean.Type.Leg_2.equals(lObliBean.getType())){
        				if(lFUBean.getPurchaserLeg2Interest()!=null) lTotalInt = lTotalInt.add(lFUBean.getPurchaserLeg2Interest());
        			}
        			lData.append(cleanData(lTotalInt)).append(FIELD_SEPARATOR); //11. Interest Amount
        			lData.append(cleanData(FormatHelper.getDisplay(DATE_FORMAT, lFUBean.getLeg1Date()))).append(FIELD_SEPARATOR); //12. Debit Date
        			lData.append(cleanData(lAESellerBean.getName())).append(FIELD_SEPARATOR); //13. Seller Name
        			lData.append(cleanData(lAESellerBean.getCode())).append(FIELD_SEPARATOR); //14. Seller Code
        			lData.append(cleanData(lAESellerBean.getPan())).append(FIELD_SEPARATOR); //15. Seller PAN
        			lData.append(cleanData(lFUBean.getAcceptedRate())).append(FIELD_SEPARATOR); //16. Discounting Rate
        			lData.append(cleanData(lFUBean.getAcceptedHaircut())).append(FIELD_SEPARATOR); //17. Haircut(%)
		    		lData.append(cleanData(lHairCutAmt)).append(FIELD_SEPARATOR); //18. Haircut Amount
		    		//19. Due Date
		    		Date lDueDate = lFUBean.getMaturityDate();
		    		// in case of leg2 extension the new due date would have been populated in the extended obligation record
		    		if (lObliBean.getType() == ObligationBean.Type.Leg_2)
		    			lDueDate = lObliBean.getDate();
        			lData.append(cleanData(FormatHelper.getDisplay(DATE_FORMAT,lDueDate))).append(FIELD_SEPARATOR); //19. Due date
		    		//20. Tenor        			
        			lData.append(cleanData(TredsHelper.getInstance().getTenure(lDueDate, lFUBean.getLeg1Date()) )).append(FIELD_SEPARATOR); //20. tenor
        			lData.append(cleanData(lFASBean.getPurchaserRef())).append(FIELD_SEPARATOR); //21. buyerRef
        			//22. Interest_overdue
        			lData.append(cleanData(lFUBean.getLeg2ExtensionInterest())).append(FIELD_SEPARATOR); //22. Interest_overdue
        			//23. principal overdue - 
        			if(lDayFactoring){
            			lData.append(cleanData(lFUBean.getFactoredAmount())).append(FIELD_SEPARATOR); //23. Principal Overdue
        			}else{
            			lData.append(cleanData("0")).append(FIELD_SEPARATOR); //23. Principal Overdue
        			}
        			lAmendments = ((ObligationBean.Type.Leg_2.equals(lObliBean.getType()) && 
    						lFUBean.getLeg2ExtensionInterest()!=null && lFUBean.getLeg2ExtensionInterest().doubleValue() > 0))?1:0;
        			lData.append(cleanData(lAmendments)).append(FIELD_SEPARATOR); //24. No. of amendments
        			lData.append(cleanData(lObliBean.getType().getCode())).append(FIELD_SEPARATOR); //25. Leg type        				
        			lDesc = lInstBean.getDescription();
        			if(CommonUtilities.hasValue(lDesc)) lDesc = lDesc.replace(",", " ");
        			lData.append(cleanData(lDesc)).append(FIELD_SEPARATOR); //26. Description of goods
        			lData.append(cleanData(FormatHelper.getDisplay(DATE_FORMAT, lInstBean.getInstDate()))).append(FIELD_SEPARATOR); //27. Invoice raise date 
        			lData.append(cleanData(lInstBean.getInstNumber())).append(RECORD_SEPARATOR); //28. Invoice number
        		}
    		}
    	}
    	//
    	return lData;
    }
    
    public HashMap<String, List<ObligationSplitsBean>> getObligationMap(Connection pConnection,Long pFuId,Long pPartNumber,Type pType, boolean pFetchParts){
    	List <ObligationDetailBean> lOBDetailList = TredsHelper.getInstance().getObligationDetailBean(pConnection, pFuId, pType, pPartNumber);
    	Map<String, List <ObligationSplitsBean>> lSplitsHash = new HashMap<String, List <ObligationSplitsBean>>();
    	for (ObligationDetailBean lBean : lOBDetailList){
    		ObligationSplitsBean lSplitsBean = lBean.getObligationSplitsBean();
    		lSplitsBean.setParentObligation(lBean.getObligationBean());
    		lSplitsHash.put(lSplitsBean.getFuId()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getType()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getPartNumber(),null);
    	}
    	return null;
    }
	
	private List<ObligationDetailBean> getObligationDetailBean(Connection pConnection, List<String> lKeys, boolean lIsSplits) {
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        
        
        //
        lSql.append(" SELECT * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE OBSRECORDVERSION>0 AND OBRECORDVERSION>0 AND OBSOBID=OBID ");
        lSql.append(" AND OBSSTATUS not in ( ").append(DBHelper.getInstance().formatString(Status.Shifted.getCode())).append(" ," );
        lSql.append(DBHelper.getInstance().formatString(Status.Extended.getCode())).append( " ) ");
        if(lIsSplits){
        	lSql.append(" AND OBFUID || '^' || OBTYPE || '^' || OBSPARTNUMBER IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(lKeys)).append(" ) "); 
        }else{
        	lSql.append(" AND OBFUID || '^' || OBTYPE  IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(lKeys)).append(" ) ");
        }
        lSql.append(" ORDER BY OBFUID , OBSPARTNUMBER");
        try {
			lObligationDetails =  obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
			
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return lObligationDetails;
	}
	
	public void changePaymentSettlor(Connection pConnection, List<String> pKeys, boolean pIsSplits,String pSettlor) throws Exception {
		ObligationBean lParentBean = null;
        ObligationSplitsBean lSplitsBean = null;
		List<ObligationDetailBean> lObligationDetails = getObligationDetailBean(pConnection, pKeys, pIsSplits);
		for (ObligationDetailBean lCompositBean : lObligationDetails){
			lParentBean = lCompositBean.getObligationBean();
			lSplitsBean = lCompositBean.getObligationSplitsBean();
			lSplitsBean.setParentObligation(lParentBean);
			//split bean status to be checked and the current settlor to be checked
			// crt rdy  
			// suc snt 
			if (!(Status.Ready.equals(lSplitsBean.getStatus()) ||
					Status.Created.equals(lSplitsBean.getStatus()))){
				throw new CommonBusinessException("Invalid Status.");
			}
		}
		for (ObligationDetailBean lCompositBean : lObligationDetails){
			lParentBean = lCompositBean.getObligationBean();
			lSplitsBean = lCompositBean.getObligationSplitsBean();
			lSplitsBean.setParentObligation(lParentBean);
			//split bean status to be checked and the current settlor to be checked
			// crt rdy  
			// suc snt 
			lSplitsBean.setPaymentSettlor(pSettlor);
			obligationSplitsDAO.update(pConnection, lSplitsBean, ObligationSplitsBean.FIELDGROUP_UPDATESETTLOR );
		}
	}
	
	public List<ObligationBean> getObligationsForModification(Connection pConnection, Long pFuId, Type pType) {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance(); 
        List<ObligationBean> lObligations = new ArrayList<ObligationBean>();
        List<Long> lObIds = new ArrayList<Long>();
        //
        lSql.append(" SELECT * FROM Obligations ");
        lSql.append(" WHERE ");
        lSql.append(" OBFUID = ").append(pFuId);
        lSql.append(" AND OBTYPE = ").append(lDbHelper.formatString(pType.getCode()));
		lSql.append(" AND OBSTATUS IN ( ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(ObligationBean.Status.Created.getCode())).append(" ) ");
        try {
			lObligations= obligationDAO.findListFromSql(pConnection, lSql.toString(), -1);
			for(ObligationBean lBean :lObligations){
				lObIds.add(lBean.getId());
			}
			String lSettlor = null;
			lSql = new StringBuilder();
			lSql.append(" Select distinct OBSPAYMENTSETTLOR from Obligationsplits where OBSOBID in ( ");
			lSql.append(TredsHelper.getInstance().getCSVIdsForInQuery(lObIds)).append(" ) ");
			Statement lStatement = pConnection.createStatement();
			ResultSet lResultSet = lStatement.executeQuery(lSql.toString());
			while (lResultSet.next()){
				if(StringUtils.isEmpty(lSettlor)){
					lSettlor = lResultSet.getString("OBSPAYMENTSETTLOR");
				}else if (!(lSettlor.equals(lResultSet.getString("OBSPAYMENTSETTLOR")))) {
					throw new CommonBusinessException("Multiple settlors found for splits.");
				}
			}
			for(ObligationBean lBean :lObligations){
				lBean.setPaymentSettlor(lSettlor);
			}
			return lObligations;
        } catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	public void revertObligation(Connection pConnection, Long pFuId, Type pType) {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance(); 
		FactoringUnitBean lFuBean = null;
		InstrumentBean lInstBean = null;
		lFuBean.setId(pFuId);
		lInstBean.setFuId(pFuId);
        List<ObligationDetailBean> lObligationDetails = null;
        
        
        //
        lSql.append(" SELECT * FROM ( SELECT * FROM Obligations, ObligationSplits where obid=obsobid ) ");
        lSql.append(" WHERE ");
        lSql.append(" OBFUID = ").append(pFuId);
        lSql.append(" AND OBTYPE = ").append(lDbHelper.formatString(pType.getCode()));
		try {
			lFuBean = factoringUnitDAO.findByPrimaryKey(pConnection, lFuBean);
			lInstBean = instrumentDAO.findBean(pConnection, lInstBean);
			lObligationDetails = obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		
	}
	public static String getQuery1(List<String> pPurchaser , List<String> pSupplier){
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT  CDCODE AS MemberCode ");
		lSql.append(" , CDCOMPANYNAME AS MemberName ");
		lSql.append(" , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append(" WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append(" WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append(" ELSE '' END) AS RegType ");
		lSql.append(" , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append(" WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append(" WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append(" WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append(" ELSE '' END) AS Status ");
		lSql.append(" , (CASE WHEN CCCHIEFPROMOTER ='Y' AND CCCPWOMENENT = 'Y' THEN 'Chief Women Promoter' ");
		lSql.append(" WHEN CCCHIEFPROMOTER='Y' THEN 'Chief Promoter' ");
		lSql.append(" WHEN CCCPWOMENENT ='Y' THEN 'Women Promoter' ");
		lSql.append(" ELSE 'Promoter' END) AS EntityType ");
		lSql.append(" , (CASE WHEN CCCPCAT ='GEN' THEN 'General' ");
		lSql.append(" WHEN CCCPCAT ='SC' THEN 'SC' ");
		lSql.append(" WHEN CCCPCAT ='ST' THEN 'ST' ");
		lSql.append(" WHEN CCCPCAT ='MIN' THEN 'Minority' ");
		lSql.append(" ELSE ' ' END) AS Category ");
		lSql.append(" , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append(" , CCTelephone AS Telphone ");
		lSql.append(" , CCMobile AS Mobile ");
		lSql.append(" , CCEMAIL AS Email ");
		lSql.append(" FROM COMPANYDETAILS, COMPANYCONTACTS ");
		lSql.append(" WHERE CDRECORDVERSION > 0 ");
		lSql.append(" AND CCRECORDVERSION > 0 ");
		lSql.append(" AND CDID = CCCDID ");
		lSql.append(" AND CCPROMOTER = 'Y' ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append(" UNION ");
		lSql.append(" SELECT  CDCODE AS MemberCode ");
		lSql.append(" , CDCOMPANYNAME AS MemberName ");
		lSql.append(" , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append(" WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append(" WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append(" ELSE '' END) AS RegType ");
		lSql.append(" , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append(" WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append(" WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append(" WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append(" ELSE '' END) AS Status ");
		lSql.append(" , (CASE WHEN CCCHIEFPROMOTER='Y' AND CCCPWOMENENT = 'Y' THEN 'Chief Promoter' ");
		lSql.append(" WHEN CCCHIEFPROMOTER='Y' THEN 'Chief Promoter' ");
		lSql.append(" ELSE 'Promoter' END) AS EntityType ");
		lSql.append(" , (CASE WHEN CCCPCAT ='GEN' THEN 'General' ");
		lSql.append(" WHEN CCCPCAT ='SC' THEN 'SC' ");
		lSql.append(" WHEN CCCPCAT ='ST' THEN 'ST' ");
		lSql.append(" WHEN CCCPCAT ='MIN' THEN 'Minority' ");
		lSql.append(" ELSE ' ' END) AS Category ");
		lSql.append(" , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append(" , CCTelephone AS Telphone ");
		lSql.append(" , CCMobile AS Mobile ");
		lSql.append(" , CCEMAIL AS Email ");
		lSql.append(" FROM COMPANYDETAILS_P, COMPANYCONTACTS ");
		lSql.append(" WHERE CDRECORDVERSION > 0 ");
		lSql.append(" AND CCRECORDVERSION > 0 ");
		lSql.append(" AND CDID = CCCDID ");
		lSql.append(" AND CCPROMOTER = 'Y' ");
		lSql.append(" AND CDAPPROVALSTATUS != 'A' ");
		lSql.append(" AND CDID NOT IN ( SELECT CDID FROM COMPANYDETAILS ) ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append(" ORDER BY MemberCode, EntityType ");
		return lSql.toString();
	}
	public static String getQuery2(List<String> pPurchaser , List<String> pSupplier){
		StringBuilder lSql = new StringBuilder();
		lSql.append("  SELECT Rownum SrNo,CDCODE MemberCode, CDCOMPANYNAME COMPANYNAME  ");
		lSql.append("  , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("  WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser' ");
		lSql.append("  ELSE '' END) AS RegistrationType, ");
		lSql.append("  (CASE WHEN CDCONSTITUTION='PROP' THEN 'Proprietorship'  ");
		lSql.append("  WHEN CDCONSTITUTION= 'PRIV' THEN 'Private' ");
		lSql.append("  WHEN CDCONSTITUTION= 'PUB' THEN 'Public' ");
		lSql.append("  WHEN CDCONSTITUTION= 'PART' THEN 'Partnership' ");
		lSql.append("  WHEN CDCONSTITUTION= 'TRST' THEN 'Trust' ");
		lSql.append("  WHEN CDCONSTITUTION= 'HUF' THEN 'HUF' ");
		lSql.append("  ELSE '' END)AS CONSTITUTION ");
		lSql.append("  , CDPAN Pan ");
		lSql.append("  , NVL(CDMSMESTATUS,' ') MSMEStatus ");
		lSql.append("  , NVL(CDCINNO,' ') CINNO ");
		lSql.append("  ,Sector.RefDesc Sector ");
		lSql.append("  ,Industry.RefDesc Industry ");
		lSql.append("  ,SubSegment.RefDesc SubSegment ");
		lSql.append("  , State.RefDesc State ");
		lSql.append("  , CLSTATE StateCode ");
		lSql.append("  FROM COMPANYDETAILS  ");
		lSql.append("  LEFT OUTER JOIN  ");
		lSql.append("  ( ");
		lSql.append("  SELECT RECCODE Code, RCVVALUE RefValue, RCVDESC RefDesc  ");
		lSql.append("  FROM REFCODEVALUES, REFCODES ");
		lSql.append("  WHERE RECRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECID = RECID ");
		lSql.append("  AND RECCODE = 'SECTOR' ");
		lSql.append("  ) Sector ON (Sector.RefValue = CDSECTOR) ");
		lSql.append("  LEFT OUTER JOIN  ");
		lSql.append("  ( ");
		lSql.append("  SELECT RECCODE Code, RCVVALUE RefValue, RCVDESC RefDesc  ");
		lSql.append("  FROM REFCODEVALUES, REFCODES ");
		lSql.append("  WHERE RECRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECID = RECID ");
		lSql.append("  AND RECCODE = 'INDUSTRY' ");
		lSql.append("  ) Industry ON (Industry.RefValue = CDIndustry) ");
		lSql.append("  LEFT OUTER JOIN  ");
		lSql.append("  ( ");
		lSql.append("  SELECT RECCODE Code, RCVVALUE RefValue, RCVDESC RefDesc  ");
		lSql.append("  FROM REFCODEVALUES, REFCODES ");
		lSql.append("  WHERE RECRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECID = RECID ");
		lSql.append("  AND RECCODE = 'SUBSEGMENT' ");
		lSql.append("  ) SubSegment ON (SubSegment.RefValue = (CDIndustry||'.'||CDSubSegment)) ");
		lSql.append("  LEFT OUTER JOIN COMPANYLOCATIONS ON (CLCDID = CDID AND CLLOCATIONTYPE = 'R' AND CLRECORDVERSION > 0 ) ");
		lSql.append("  LEFT OUTER JOIN  ");
		lSql.append("  ( ");
		lSql.append("  SELECT RECCODE Code, RCVVALUE RefValue, RCVDESC RefDesc  ");
		lSql.append("  FROM REFCODEVALUES, REFCODES ");
		lSql.append("  WHERE RECRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECORDVERSION > 0 ");
		lSql.append("  AND RCVRECID = RECID ");
		lSql.append("  AND RECCODE = 'GSTSTATE' ");
		lSql.append("  ) State ON (State.RefValue = COMPANYLOCATIONS.CLSTATE) ");
		lSql.append("  WHERE ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		return lSql.toString();
	}
	public static String getQuery3(List<String> pPurchaser , List<String> pSupplier){
		StringBuilder lSql = new StringBuilder();
		lSql.append("SELECT (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser' ");
		lSql.append("ELSE '' END) AS RegistrationType ");
		lSql.append(", (CASE WHEN CDCONSTITUTION='PROP' THEN 'Proprietorship'  ");
		lSql.append("WHEN CDCONSTITUTION= 'PRIV' THEN 'Private' ");
		lSql.append("WHEN CDCONSTITUTION= 'PUB' THEN 'Public' ");
		lSql.append("WHEN CDCONSTITUTION= 'PART' THEN 'Partnership' ");
		lSql.append("WHEN CDCONSTITUTION= 'TRST' THEN 'Trust' ");
		lSql.append("WHEN CDCONSTITUTION= 'HUF' THEN 'HUF' ");
		lSql.append("ELSE '' END)AS CONSTITUTION ");
		lSql.append(",CDCODE CODE, CDCOMPANYNAME COMPANYNAME ");
		lSql.append(", clname locationname ");
		lSql.append(",TRIM(CLCITY) CITY ");
		lSql.append(", TRIM(CLZIPCODE) ZIPCODE  ");
		lSql.append(",TRIM(REFCODEVALUES.RCVDESC) STATE  ");
		lSql.append(", clgstn GSTN,(CASE WHEN cllocationtype='R' THEN 'Registered Location'  ");
		lSql.append("ELSE 'Other' END) AS LocationType,CDCUSTOMER1,CDCUSTOMER2,CDCUSTOMER3 ");
		lSql.append(", (CASE WHEN CDMSMEREGTYPE = 'UAN' THEN CDMSMEREGNO ELSE '' END) AS UdyogAadharNo ");
		lSql.append("FROM COMPANYDETAILS,COMPANYLOCATIONS ");
		lSql.append("LEFT OUTER JOIN REFCODEVALUES ON (RCVVALUE=CLSTATE AND RCVRECID=31) ");
		lSql.append("WHERE CLCDID=CDID ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append("order by cdcode,LOCATIONTYPE desc ");
		return lSql.toString();
	}
	public static String getQuery4(List<String> pPurchaser , List<String> pSupplier){
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT CDCODE MemberCode ");
		lSql.append(" , CDCOMPANYNAME CompanyName ");
		lSql.append(" , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append(" WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append(" WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append(" ELSE '' END) AS RegistrationType ");
		lSql.append(" , TRIM(CDCORDISTRICT) District ");
		lSql.append(" ,TRIM(CLLINE1)||','||TRIM(CLLINE2)||','||TRIM(CLLINE3)||','||TRIM(CLCITY)||'-'||TRIM(CLZIPCODE)||','||TRIM(CLDISTRICT)||','||TRIM(REFCODEVALUES.RCVDESC)||','||TRIM(CLCOUNTRY) AS REGISTEREDADDRESS ");
		lSql.append(" ,TRIM(CLCITY) RegisteredCity,TRIM(CLZIPCODE) RegisteredZipCode,TRIM(REFCODEVALUES.RCVDESC) RegisteredState,TRIM(CLCOUNTRY) RegisteredCountry ");
		lSql.append(" ,TRIM(CDCORLINE1)||','||TRIM(CDCORLINE2)||','||TRIM(CDCORLINE3)||','||TRIM(CDCORCITY)||'-'||TRIM(CDCORZIPCODE)||','||TRIM(CDCORDISTRICT)||','||TRIM(REFCODEVALUES.RCVDESC)||','||TRIM(CDCORCOUNTRY) AS CORRESPONDANCEADDRESS ");
		lSql.append(" ,TRIM(CDCORCITY) CorrespondanceCity,TRIM(CDCORZIPCODE) CorrespondanceZipCode,TRIM(REFCODEVALUES.RCVDESC) CorrespondanceState , TRIM(CDCORCOUNTRY) CorrespondanceCountry ");
		lSql.append(" FROM COMPANYDETAILS,COMPANYLOCATIONS ");
		lSql.append(" LEFT OUTER JOIN REFCODEVALUES ON (RCVVALUE=CLSTATE AND RCVRECID=31) ");
		lSql.append(" WHERE CLCDID=CDID AND CLLOCATIONTYPE='R' ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		return lSql.toString();
	}
	public static String getQuery5(List<String> pPurchaser , List<String> pSupplier){
		StringBuilder lSql = new StringBuilder();
		lSql.append("  SELECT  CDCODE AS MemberCode ");
		lSql.append("  , CDCOMPANYNAME AS MemberName ");
		lSql.append("  , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("  WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append("  WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append("  ELSE '' END) AS RegType ");
		lSql.append("  , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append("  WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append("  WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append("  WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append("  ELSE '' END) AS Status ");
		lSql.append("  , 'Authorsized Person' AS EntityType ");
		lSql.append("  , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append("  , CCTelephone AS Telphone ");
		lSql.append("  , CCMobile AS Mobile ");
		lSql.append("  , CCEMAIL AS Email ");
		lSql.append("  FROM COMPANYDETAILS, COMPANYCONTACTS ");
		lSql.append("  WHERE CDRECORDVERSION > 0 ");
		lSql.append("  AND CCRECORDVERSION > 0 ");
		lSql.append("  AND CDID = CCCDID ");
		lSql.append("  AND CCAUTHPER = 'Y' ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append("  UNION  ");
		lSql.append("  SELECT  CDCODE AS MemberCode ");
		lSql.append("  , CDCOMPANYNAME AS MemberName ");
		lSql.append("  , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("  WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append("  WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append("  ELSE '' END) AS RegType ");
		lSql.append("  , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append("  WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append("  WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append("  WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append("  ELSE '' END) AS Status ");
		lSql.append("  , 'Administrator' AS EntityType ");
		lSql.append("  , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append("  , CCTelephone AS Telphone ");
		lSql.append("  , CCMobile AS Mobile ");
		lSql.append("  , CCEMAIL AS Email ");
		lSql.append("  FROM COMPANYDETAILS, COMPANYCONTACTS ");
		lSql.append("  WHERE CDRECORDVERSION > 0 ");
		lSql.append("  AND CCRECORDVERSION > 0 ");
		lSql.append("  AND CDID = CCCDID ");
		lSql.append("  AND CCADMIN = 'Y' ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append("  UNION ");
		lSql.append("  SELECT  CDCODE AS MemberCode ");
		lSql.append("  , CDCOMPANYNAME AS MemberName ");
		lSql.append("  , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("  WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append("  WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append("  ELSE '' END) AS RegType ");
		lSql.append("  , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append("  WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append("  WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append("  WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append("  ELSE '' END) AS Status ");
		lSql.append("  , 'Authorsized Person' AS EntityType ");
		lSql.append("  , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append("  , CCTelephone AS Telphone ");
		lSql.append("  , CCMobile AS Mobile ");
		lSql.append("  , CCEMAIL AS Email ");
		lSql.append("  FROM COMPANYDETAILS_P, COMPANYCONTACTS ");
		lSql.append("  WHERE CDRECORDVERSION > 0 ");
		lSql.append("  AND CCRECORDVERSION > 0 ");
		lSql.append("  AND CDID = CCCDID ");
		lSql.append("  AND CCAUTHPER = 'Y' ");
		lSql.append("  AND CDAPPROVALSTATUS != 'A' ");
		lSql.append("  AND CDID NOT IN ( SELECT CDID FROM COMPANYDETAILS ) ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append("  UNION  ");
		lSql.append("  SELECT  CDCODE AS MemberCode ");
		lSql.append("  , CDCOMPANYNAME AS MemberName ");
		lSql.append("  , (CASE WHEN CDSUPPLIERFLAG='Y' THEN 'Supplier'  ");
		lSql.append("  WHEN CDPURCHASERFLAG = 'Y' THEN 'Purchaser'  ");
		lSql.append("  WHEN CDFINANCIERFLAG = 'Y' THEN 'Financier'  ");
		lSql.append("  ELSE '' END) AS RegType ");
		lSql.append("  , (CASE WHEN CDApprovalStatus ='A' THEN 'Approved' ");
		lSql.append("  WHEN CDApprovalStatus ='D' THEN 'Draft' ");
		lSql.append("  WHEN CDApprovalStatus ='B' THEN 'Returned' ");
		lSql.append("  WHEN CDApprovalStatus ='R' THEN 'Rejected' ");
		lSql.append("  ELSE '' END) AS Status ");
		lSql.append("  , 'Administrator' AS EntityType ");
		lSql.append("  , (TRIM(CCSALUTATION) || ' ' || TRIM(CCFIRSTNAME) || ' ' || TRIM(CCMIDDLENAME) || ' ' || TRIM(CCLASTNAME)) AS PersonName ");
		lSql.append("  , CCTelephone AS Telphone ");
		lSql.append("  , CCMobile AS Mobile ");
		lSql.append("  , CCEMAIL AS Email ");
		lSql.append("  FROM COMPANYDETAILS_P, COMPANYCONTACTS ");
		lSql.append("  WHERE CDRECORDVERSION > 0 ");
		lSql.append("  AND CCRECORDVERSION > 0 ");
		lSql.append("  AND CDID = CCCDID ");
		lSql.append("  AND CCADMIN = 'Y' ");
		lSql.append("  AND CDAPPROVALSTATUS != 'A' ");
		lSql.append("  AND CDID NOT IN ( SELECT CDID FROM COMPANYDETAILS ) ");
		lSql.append(" AND ( ");
		lSql.append("  ( CDPURCHASERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pPurchaser) + " ))");
		lSql.append("  OR ( CDSUPPLIERFLAG = 'Y' AND CDCODE IN ( " + TredsHelper.getInstance().getCSVStringForInQuery(pSupplier) + " )) "); 
		lSql.append("  ) ");
		lSql.append("  ORDER BY MemberCode, EntityType  ");
		logger.info(lSql.toString());
		return lSql.toString();
	}
	public byte[] getFinReportForCersai(String pFinancier) throws Exception {
		Object[] lObject = TredsHelper.getInstance().getPurchaserSupplierList(pFinancier);
		List<String> lPurchaser = new ArrayList<String>();
		List<String> lSupplier = new ArrayList<String>();
		lPurchaser = (List<String>) lObject[0];
		lSupplier = (List<String>) lObject[1];
		String[] lSqlQuery = new String[]{getQuery1(lPurchaser , lSupplier),getQuery2(lPurchaser , lSupplier),getQuery3(lPurchaser , lSupplier),getQuery4(lPurchaser , lSupplier),getQuery5(lPurchaser, lSupplier)};
		String[] lFileName = new String[]{"Company Details","Partner Propriter","Gstn","Address","Authorised Signatory"};
		byte[] lData = generateReport(lSqlQuery , lFileName);
		return lData;
	}
	private byte[]  generateReport(String[] pSql ,String[] pSheetName) throws Exception{
		XSSFWorkbook lWorkbook = new XSSFWorkbook();
		ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
		ResultSet lResultSet = null;
		Connection lConnection = DBHelper.getInstance().getConnection();
		for (int lSqlPtr = 0; lSqlPtr<pSql.length; lSqlPtr++){
			int lRowPtr = 0;
			XSSFSheet lSheet = lWorkbook.createSheet(pSheetName[lSqlPtr]);
			XSSFCellStyle lCellStyle = null;
			lCellStyle = lWorkbook.createCellStyle();
			lCellStyle.setWrapText(false);
			Statement lStatement = lConnection.createStatement();
			lResultSet = lStatement.executeQuery(pSql[lSqlPtr]);
			XSSFRow lRowValues= null;
			ResultSetMetaData lResultSetMetaData =  lResultSet.getMetaData();
			int lColCount = lResultSetMetaData.getColumnCount();
			lRowValues= lSheet.createRow(lRowPtr++);
			for(int lColPtr=1; lColPtr <= lColCount; lColPtr++){
				lRowValues.createCell(lColPtr-1).setCellValue(lResultSetMetaData.getColumnName(lColPtr));
				lRowValues.setRowStyle(lCellStyle);
				lSheet.autoSizeColumn(lColPtr);
			}
			while(lResultSet.next()){
				lRowValues= lSheet.createRow(lRowPtr++);
				for(int lColPtr=1; lColPtr <= lColCount; lColPtr++){
					lRowValues.createCell(lColPtr-1).setCellValue(lResultSet.getString(lColPtr));
				}
			}
			for(int lColPtr=1; lColPtr <= lColCount; lColPtr++){
				lSheet.autoSizeColumn(lColPtr-1);
			}
			lByteArrayOutputStream = new ByteArrayOutputStream();
			lWorkbook.write(lByteArrayOutputStream);
		}
		lByteArrayOutputStream.close();
		return lByteArrayOutputStream.toByteArray();
	}
	public void updateStatusOutForSettlement(Connection pConnection, Long pFuId, AppUserBean pUserBean) throws Exception {
    	ObligationBean lObligationBean = null;
    	ObligationSplitsBean lObligationSplitsBean = null;
    	List<Long> lUpdatedParentObIds = new ArrayList<Long>();
    	List<ObligationDetailBean> lObligationDetailBeanList = TredsHelper.getInstance().getObligationDetailBean(pConnection, pFuId, Type.Leg_2, null);
    	if(lObligationDetailBeanList != null){
    	   	for (ObligationDetailBean lBean : lObligationDetailBeanList) {
        		lObligationBean = lBean.getObligationBean();
        		lObligationSplitsBean = lBean.getObligationSplitsBean();
        		if(!lUpdatedParentObIds.contains(lObligationBean.getId())){
        			obligationDAO.insertAudit(pConnection, lObligationBean, AuditAction.Update, pUserBean.getId());
        			lObligationBean.setStatus(Status.L2_Prov_Outside);
        			obligationDAO.update(pConnection, lObligationBean , ObligationBean.FIELDGROUP_UPDATESTATUS);
        			lUpdatedParentObIds.add(lObligationBean.getId());
        		}
        		obligationSplitsDAO.insertAudit(pConnection, lObligationSplitsBean, AuditAction.Update, pUserBean.getId());
        		lObligationSplitsBean.setStatus(Status.L2_Prov_Outside);
        		obligationSplitsDAO.update(pConnection, lObligationSplitsBean, ObligationSplitsBean.FIELDGROUP_UPDATESTATUS);
    		}
    	}else{
    		throw new CommonBusinessException("Record not found");
    	}
	}
	public void updateUtrNumber(Connection pConnection, Map<String, Object> lMap, AppUserBean pUserBean) throws Exception {
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
    	if (lAppEntityBean.isPurchaser() || lAppEntityBean.isPlatform()){
    	 	Long lFuId = Long.valueOf((String) lMap.get("fuID"));
        	String lUtrNumber = (String) lMap.get("UtrNumber");
        	String lRemarks = (String) lMap.get("Remarks");
    		ObligationBean lObligationBean = null;
    		ObligationSplitsBean lObligationSplitsBean = null;
    		String lFieldGroup = null;
    		List<Long> lUpdatedParentObIds = new ArrayList<Long>();
    		List<ObligationDetailBean> lObligationDetailBeanList = TredsHelper.getInstance().getObligationDetailBean(pConnection, lFuId,Type.Leg_2,null);
    		if (lObligationDetailBeanList != null){
    			for (ObligationDetailBean lBean : lObligationDetailBeanList) {
    				lObligationBean = lBean.getObligationBean();
    				lObligationSplitsBean = lBean.getObligationSplitsBean();
    				obligationSplitsDAO.insertAudit(pConnection, lObligationSplitsBean, AuditAction.Update, pUserBean.getId());
    				lObligationSplitsBean.setStatus(Status.L2_Set_Outside);
    				if(!lUpdatedParentObIds.contains(lObligationBean.getId())){
    					obligationDAO.insertAudit(pConnection, lObligationBean, AuditAction.Update, pUserBean.getId());
    	    			lObligationBean.setStatus(Status.L2_Set_Outside);
    	    			obligationDAO.update(pConnection, lObligationBean , ObligationBean.FIELDGROUP_UPDATESTATUS);
    	    			lUpdatedParentObIds.add(lObligationBean.getId());
    	    		}
    				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lObligationBean.getTxnEntity());
					if(lAppEntityBean.isFinancier() || lAppEntityBean.isPurchaser()){
    					lObligationSplitsBean.setPaymentRefNo(lUtrNumber);
    					lObligationSplitsBean.setRespRemarks(lRemarks);
    					lFieldGroup = ObligationSplitsBean.FIELDGROUP_UPDATEUTR;
					}else {
    					lFieldGroup = ObligationSplitsBean.FIELDGROUP_UPDATESTATUS;
					}
					obligationSplitsDAO.update(pConnection, lObligationSplitsBean, lFieldGroup);
    			}
    		}else{
    			throw new CommonBusinessException("Record not found");
    		}
    	}else{
    		throw new CommonBusinessException("Access Denied");
    	}
}
	public void markStatus(Connection pConnection, Long pFuId, String pStatus, AppUserBean pUserBean) throws Exception {
		Status lStatus = null;
		if (Status.Failed.getCode().equals(pStatus)) {
			lStatus = Status.Failed;
		}else if (Status.Success.getCode().equals(pStatus)) {
			lStatus = Status.Success;
		}
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
    	if (lAppEntityBean.isFinancier() || lAppEntityBean.isPlatform()){
    		ObligationBean lObligationBean = null;
    		ObligationSplitsBean lObligationSplitsBean = null;
    		String lParentFieldGroup = null;
    		String lSplitFieldGroup = null;
    		List<Long> lUpdatedParentObIds = new ArrayList<Long>();
    		List<ObligationDetailBean> lObligationDetailBeanList = TredsHelper.getInstance().getObligationDetailBean(pConnection, pFuId,Type.Leg_2,null);
    		if (lObligationDetailBeanList != null){
    			for (ObligationDetailBean lBean : lObligationDetailBeanList) {
    				lObligationBean = lBean.getObligationBean();
    				lObligationSplitsBean = lBean.getObligationSplitsBean();
    				if(!lUpdatedParentObIds.contains(lObligationBean.getId())){
    					obligationDAO.insertAudit(pConnection, lObligationBean, AuditAction.Update, pUserBean.getId());
    					lObligationBean.setStatus(lStatus);
    					lParentFieldGroup = ObligationBean.FIELDGROUP_UPDATESTATUS;
    					if (Status.Success.equals(lStatus)) {
    						lParentFieldGroup = ObligationBean.FIELDGROUP_UPDATESETTLEDAMOUNT;
    						lObligationBean.setSettledAmount(lObligationBean.getAmount());
    						lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
    					}
    					obligationDAO.update(pConnection, lObligationBean , lParentFieldGroup);
    	    			lUpdatedParentObIds.add(lObligationBean.getId());
    	    		}
    				obligationSplitsDAO.insertAudit(pConnection, lObligationSplitsBean, AuditAction.Update, pUserBean.getId());
    				lObligationSplitsBean.setStatus(lStatus);
					lObligationSplitsBean.setSettlorProcessed(Yes.Yes);
					lObligationSplitsBean.setPaymentSettlor(AppConstants.FACILITATOR_DIRECT);
					lSplitFieldGroup =  ObligationSplitsBean.FIELDGROUP_UPDATESTATUS;
					if (Status.Success.equals(lStatus)) {
						lSplitFieldGroup =  ObligationSplitsBean.FIELDGROUP_MARKASSUCCESS;
						lObligationSplitsBean.setSettledAmount(lObligationSplitsBean.getAmount());
						lObligationSplitsBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
					}
	    			obligationSplitsDAO.update(pConnection, lObligationSplitsBean ,lSplitFieldGroup);
    				}
    			if (Status.Success.equals(lStatus)) {
    				FactoringUnitBean lFilterBean = new FactoringUnitBean();
        			lFilterBean.setId(pFuId);
        			FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFilterBean);
        			if(lFactoringUnitBean != null){
        				factoringUnitDAO.insertAudit(pConnection, lFactoringUnitBean, AuditAction.Update, pUserBean.getId());
        				lFactoringUnitBean.setStatus(FactoringUnitBean.Status.Leg_2_Settled);
        				lFactoringUnitBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
            			factoringUnitDAO.update(pConnection, lFactoringUnitBean , lFactoringUnitBean.FIELDGROUP_UPDATESTATUS);
        			}else{
        				throw new CommonBusinessException("Factoring Unit not found");
        			}
        			InstrumentBean lInstrumentFilterBean = new InstrumentBean();
        			lInstrumentFilterBean.setFuId(pFuId);
        			InstrumentBean lInstrumentBean = instrumentDAO.findBean(pConnection, lInstrumentFilterBean);
        			if(lInstrumentBean != null){
        				instrumentDAO.insertAudit(pConnection, lInstrumentBean, AuditAction.Update, pUserBean.getId());
        				lInstrumentBean.setStatus(InstrumentBean.Status.Leg_2_Settled);
        				lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
            			instrumentDAO.update(pConnection, lInstrumentBean, lInstrumentBean.FIELDGROUP_UPDATESTATUS);
        			}else{
        				throw new CommonBusinessException("Instrument not found");
        			}
    			}
			}else{
				throw new CommonBusinessException("Record not found");
			}
		}else{
			throw new CommonBusinessException("Access Denied");
		}
	}

	public List<ObligationBean> findVendorList(Connection pConnection, ObligationBean pFilterBean, List<String> pFields, AppUserBean pUserBean) throws Exception {
	        StringBuilder lFilter = new StringBuilder();
	        DBHelper lDBHelper = DBHelper.getInstance();
	        java.sql.Date lFromDate = pFilterBean.getDate();
	        if (pFilterBean.getFuId()==null) {
	        	if (pFilterBean.getDate()==null && pFilterBean.getTxnEntity()==null) {
	        		throw new CommonBusinessException("Atleast fuId or date or txnEntity required.");
	        	}
	        }
	        pFilterBean.setDate(null);
	        obligationDAO.appendAsSqlFilter(lFilter, pFilterBean, false);
	        if (lFromDate != null) {
	            lFilter.append(" AND OBDate >= ").append(lDBHelper.formatDate(lFromDate));
	        }
	        if (pFilterBean.getFilterToDate() != null) {
	            lFilter.append(" AND OBDate <= ").append(lDBHelper.formatDate(pFilterBean.getFilterToDate()));
	        }
	        lFilter.append(" ORDER BY OBDate ASC");
	        
	        StringBuilder lSql = new StringBuilder();
	        lSql.append(" SELECT * FROM OBLIGATIONS,FACTORINGUNITS WHERE 1=1  AND OBTYPE='L1' AND OBTXNTYPE='C' AND OBFUID=FUID  AND FUSUPPLIER=OBTXNENTITY ");
	        lSql.append(" AND FUPURCHASER = ").append(lDBHelper.formatString(pUserBean.getDomain()));
	        if(lFilter.length() > 0 )
	        	lSql.append(" AND ").append(lFilter);
	        return obligationDAO.findListFromSql(pConnection, lSql.toString(), -1);
	}

	public String recalculateObligations(Connection pConnection, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		pConnection.setAutoCommit(false);
		List<Map<String, Object>> lMessages = new ArrayList<Map<String, Object>>();
		List<Long> lList = new ArrayList<>();
		String lIds = "";
		Object[] lFuIdArr = CommonUtilities.split(pMap.get("fuId").toString(),",");
		for (int lPtr=0; lPtr<lFuIdArr.length; lPtr++){
			if(lList.isEmpty()){
				lList.add(Long.valueOf((String)lFuIdArr[lPtr]));
			}else{
				if(!lList.contains(Long.valueOf((String)lFuIdArr[lPtr]))){
					lList.add(Long.valueOf((String)lFuIdArr[lPtr]));
				}
			}
		}
    	for (Long lFuId : lList) {
        	java.sql.Date lDate = FormatHelper.getDate(pMap.get("date").toString(),AppConstants.DATE_FORMAT);
        	ObligationBean lOBean = new ObligationBean();
        	lOBean.setFuId(lFuId);
        	lOBean.setType(ObligationBean.Type.Leg_1);
        	try{
            	List<ObligationBean> lObligationBeanList = obligationDAO.findList(pConnection, lOBean);
            	if(lObligationBeanList != null){
            		for(ObligationBean lObligationBean : lObligationBeanList){
            			if(ObligationBean.Type.Leg_1.equals(lObligationBean.getType())){
                			if(!(ObligationBean.Status.Created.equals(lObligationBean.getStatus()) || 
                					ObligationBean.Status.Ready.equals(lObligationBean.getStatus()) ||
                						ObligationBean.Status.Sent.equals(lObligationBean.getStatus()))){
                				throw new CommonBusinessException(" Connot process Obligations ");
                			}
            			}else{
            				throw new CommonBusinessException("Only L1 obligations are allowed.");
            			}
            		}
            		FactoringUnitBean lFUBean = new FactoringUnitBean();
        			FactoringUnitBO lFactoringUnitBO = new FactoringUnitBO();
        			lFUBean.setId(lFuId);
        			lFUBean = factoringUnitDAO.findBean(pConnection, lFUBean);
        			if(!Objects.isNull(lFUBean)){
        				if(FactoringUnitBean.Status.Factored.equals(lFUBean.getStatus())){
        		    		AppUserBean lAppUserBean = new AppUserBean();
        		    		lAppUserBean.setId(new Long(0));
        		    		if (!lFactoringUnitBO.changeFU(pConnection, lFUBean, lAppUserBean, lDate)){
        		    			if (!StringUtils.isBlank(lIds)){
        		    				lIds += ",";
        		    			}
        		    			lIds += String.valueOf(lFuId);
        		    		}
        				}
        			}
            	}
            	TredsHelper.getInstance().appendMessage(lMessages,lFuId.toString(),"Obligations shifted successfully",null) ;
            	
        	}catch(Exception ex){
        		TredsHelper.getInstance().appendMessage(lMessages,lFuId.toString(),ex.getMessage(),null) ;
        		logger.info(ex.getMessage());
        	}
    	}
		return new JsonBuilder(lMessages).toString();
    }
	public Map<String, Object> getPayAdviseBuyer(Connection lConnection, ObligationBean pFilterBean, AppUserBean pUserBean) throws Exception{
		Map<String, Object> lMap = new HashMap<String, Object>();
		FactoringUnitBean lFilterFactBean = new FactoringUnitBean();
		lFilterFactBean.setId(pFilterBean.getFuId());
		FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findBean(lConnection, lFilterFactBean);
		InstrumentBean lFilterInstBean = new InstrumentBean();
		lFilterInstBean.setFuId(pFilterBean.getFuId());
		InstrumentBean lInstrumentBean = instrumentDAO.findBean(lConnection, lFilterInstBean);
		ObligationBean lFilterObligBean = new ObligationBean();
		lFilterObligBean.setFuId(pFilterBean.getFuId());
		List<ObligationBean> lObligationList = obligationDAO.findList(lConnection, lFilterObligBean);
		StringBuilder lSql = new StringBuilder();
		lSql.append(" select * from obligationextensions where oeobid in " );
		lSql.append(" ( ");
		lSql.append(" select obid from obligations where obfuid= ").append(lFactoringUnitBean.getId());
		lSql.append(" and obtxnentity= ").append(DBHelper.getInstance().formatString(lFactoringUnitBean.getPurchaser()));
		lSql.append(" and OBRECORDVERSION>0 ");
		lSql.append(" )");
		ObligationExtensionBean lOBExtBean = obligationExtDAO.findBean(lConnection, lSql.toString());
		CompanyLocationBean lClFilterBean = new CompanyLocationBean(); 
		ObligationBean lFinObligationDebitBean = null;
		for (ObligationBean lBean : lObligationList) {
			if (lBean.getTxnEntity().equals(lFactoringUnitBean.getFinancier()) 
					&& ObligationBean.Type.Leg_1.equals(lBean.getType())) {
				lFinObligationDebitBean = lBean;
			}
		}
		if(lInstrumentBean != null && lFactoringUnitBean != null){
			lMap.put("instNumber", lInstrumentBean.getInstNumber());
			lMap.put("inId", lInstrumentBean.getId());
			lMap.put("instDate", lInstrumentBean.getInstDate());
			lMap.put("instCount", lInstrumentBean.getInstCount());
			lMap.put("poNumber", lInstrumentBean.getPoNumber());
			lMap.put("poDate", lInstrumentBean.getPoDate());
			lMap.put("fuId", lInstrumentBean.getFuId());
			lMap.put("goodsAcceptDate", lInstrumentBean.getGoodsAcceptDate());
			lMap.put("statDueDate", lInstrumentBean.getStatDueDate());
			lMap.put("amount", lInstrumentBean.getAmount());
			lMap.put("deductions", lInstrumentBean.getAdjAmount());
			lMap.put("tds", lInstrumentBean.getTdsAmount());
			lMap.put("financedPer", AppConstants.HUNDRED.subtract(lFactoringUnitBean.getAcceptedHaircut() != null?lFactoringUnitBean.getAcceptedHaircut():BigDecimal.ZERO));
			lMap.put("factUnitValue", lFactoringUnitBean.getAmount());
			lMap.put("financedAmount", lFactoringUnitBean.getFactoredAmount());
			lMap.put("discountingRate", lFactoringUnitBean.getAcceptedRate());
			AppEntityBean lPurAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getPurchaser());
			lMap.put("buyerName", lPurAppEntityBean.getName());
			lMap.put("buyerCode", lPurAppEntityBean.getCode());
			lMap.put("buyerRefCode", lInstrumentBean.getPurchaserRef());
			CompanyLocationBean lClBean = null;
			if (lFactoringUnitBean.getPurchaserSettleLoc()!=null) {
				lClFilterBean.setId(lFactoringUnitBean.getPurchaserSettleLoc());
				lClBean = companyLocationDAO.findByPrimaryKey(lConnection, lClFilterBean);
			}else {
				lClBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lPurAppEntityBean.getCode());
			}
			//TODO : Location purclid
			lMap.put("buyerLocation", lClBean.getName());
			AppEntityBean lSupAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getSupplier());
			lMap.put("sellerName", lSupAppEntityBean.getName());
			lMap.put("sellerCode", lSupAppEntityBean.getCode());
			lMap.put("sellerRefCode", lInstrumentBean.getSupplierRef());
			if (lFactoringUnitBean.getSupplierSettleLoc()!=null) {
				lClFilterBean.setId(lFactoringUnitBean.getSupplierSettleLoc());
				lClBean = companyLocationDAO.findByPrimaryKey(lConnection, lClFilterBean);
			}else {
				lClBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lSupAppEntityBean.getCode());
			}
			
			//TODO : Location supclid
			lMap.put("sellerLocation", lClBean.getName());
			lMap.put("msmeCategory", lSupAppEntityBean.getMsmeStatus());
			AppEntityBean lFinAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFactoringUnitBean.getFinancier());
			lMap.put("financierName", lFinAppEntityBean.getName());
			lMap.put("financierCode", lFinAppEntityBean.getCode());
			if (lFactoringUnitBean.getFinancierSettleLoc()!=null) {
				lClFilterBean.setId(lFactoringUnitBean.getFinancierSettleLoc());
				lClBean = companyLocationDAO.findByPrimaryKey(lConnection, lClFilterBean);
			}else {
				lClBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, lFinAppEntityBean.getCode());
			}
			//TODO : Location fu settleclid or reg
			lMap.put("financierLocation", lClBean.getName());
			lMap.put("bidAcceptanceDateTime", lFactoringUnitBean.getAcceptDateTime());
			lMap.put("financingDebitDate", lFactoringUnitBean.getLeg1Date());
			lMap.put("dueDateLeg2", lFactoringUnitBean.getLeg2MaturityExtendedDate());
			lMap.put("tenor", lFactoringUnitBean.getTenure());
			if(lInstrumentBean.getCostBearingType()!= null && CostBearingType.Buyer.equals(lInstrumentBean.getCostBearingType())){
				lMap.put("intCostBearer", lFactoringUnitBean.getPurchaser());
				lMap.put("intAmountLeg1", lFactoringUnitBean.getPurchaserLeg1Interest());
				lMap.put("intAmountLeg2", lFactoringUnitBean.getPurchaserLeg2Interest());
				lMap.put("purchaserTotalIntrest", lFactoringUnitBean.getTotalPurchaserInterest());
			}else{
				lMap.put("intCostBearer", lFactoringUnitBean.getSupplier());
				lMap.put("intAmountLeg1", lFactoringUnitBean.getSupplierLeg1Interest());
				lMap.put("sellerTotalIntrest", lFactoringUnitBean.getSupplierLeg1Interest());
			}
			lMap.put("platformChargeBearer", lFactoringUnitBean.getChargeBearerEntityCode());
			lMap.put("rxilChargesAmount", lFactoringUnitBean.getTotalChargesExcludingTax());
			lMap.put("gstAmount", lFactoringUnitBean.getTotalChargesTax());
			//Seller intrest + seller platfrom charge
			BigDecimal lTotalDeductions = BigDecimal.ZERO;
			if (lFactoringUnitBean.getSupplierLeg1Interest()!=null) {
				lTotalDeductions = lTotalDeductions.add(lFactoringUnitBean.getSupplierLeg1Interest());
			}
			GstSummaryBean lGstSummaryBean = lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getSupplier());
			if (lGstSummaryBean!=null) {
				lTotalDeductions = lTotalDeductions.add(lGstSummaryBean.getTotalCharge());
			}
			lGstSummaryBean = lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getSupplier(),ChargeType.Split, null);
			if (lGstSummaryBean!=null) {
				lTotalDeductions = lTotalDeductions.add(lGstSummaryBean.getTotalCharge());
			}
			lMap.put("totalDeduction",lTotalDeductions );
			// Fact Amt - above amt
			lMap.put("amountCreditToseller", lFactoringUnitBean.getFactoredAmount().subtract(lTotalDeductions));
			BigDecimal lTotalCharge = BigDecimal.ZERO; 
			GstSummaryBean lGstPurBean = lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getPurchaser());
			if (lGstSummaryBean!=null) {
				lTotalCharge = lTotalCharge.add(lGstPurBean.getTotalCharge());
			}
			lGstPurBean = lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getPurchaser(),ChargeType.Split, null);
			if (lGstSummaryBean!=null) {
				lTotalCharge = lTotalCharge.add(lGstPurBean.getTotalCharge());
			}
			lMap.put("buyerCost", lFactoringUnitBean.getPurchaserLeg1Interest().add(lFactoringUnitBean.getPurchaserLeg2Interest()).add(lTotalCharge));
			lMap.put("debitToFinancier", lFinObligationDebitBean.getAmount());
			lMap.put("intOnLeg2", lFactoringUnitBean.getPurchaserLeg2Interest());
			lMap.put("leg2Amount", lFactoringUnitBean.getFinLeg2Amount());
			lMap.put("transactionStatus", lFactoringUnitBean.getStatus().toString());
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
			if(lAppEntityBean.isPurchaser()){
				if (lOBExtBean!=null) {
					lMap.put("extendedBidRate", lOBExtBean.getInterestRate().add(lOBExtBean.getPenaltyRate()));
					lMap.put("extendedDueDate", lOBExtBean.getNewDate());
					lMap.put("interestExtAmount", lFactoringUnitBean.getLeg2ExtensionInterest());
					lMap.put("extendedPlatformCharges", lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getPurchaser(), ChargeType.Extension, null).getCharge());
					GstSummaryBean lBeanExtGst = lFactoringUnitBean.getGstSummary(lFactoringUnitBean.getPurchaser(), ChargeType.Extension, null);
					lMap.put("extendedPlatformChargesGst", lBeanExtGst.getCgstValue().add(lBeanExtGst.getIgstValue()).add(lBeanExtGst.getSgstValue()));
					lMap.put("extendedTenor", lOBExtBean.getTenor());
				}else {
					lMap.put("extendedBidRate", null);
					lMap.put("extendedDueDate", null);
					lMap.put("interestExtAmount", null);
					lMap.put("extendedPlatformCharges", null);
					lMap.put("extendedPlatformChargesGst", null);
					lMap.put("extendedTenor", null);
				}
				lMap.put("isPurchaser", true);
				lMap.put("intColLeg1Leg2", lFactoringUnitBean.getInterest());
			}else{
				lMap.put("isPurchaser", false);
			}
			return lMap;
		}else{
			return null;
		}

	}
	
	
}
