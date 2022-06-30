package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.messaging.EmailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FactoredPaymentBean;
import com.xlx.treds.auction.bean.IObligation;
import com.xlx.treds.auction.bean.ObliFUInstDetailSplitsBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.AppEntityBean.EntityType;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.other.bean.GEMInvoiceBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class AggregatorInstrumentBO {
	private static final Logger logger = LoggerFactory.getLogger(AggregatorInstrumentBO.class);
	private InstrumentBO instrumentBO = null;
	private GenericDAO<InstrumentBean> instrumentDAO;
	private GenericDAO<FactoringUnitBean> factoringUnitDAO;
	private GenericDAO<GEMInvoiceBean> gemInvoiceDao;
	private CompositeGenericDAO<FactoredBean> factoredBeanDAO;
    private CompositeGenericDAO<FactoredPaymentBean> factoredPaymentDAO;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    
    //
    private FactoringUnitBO factoringUnitBO;
	private BeanMeta instrumentBeanMeta;
    private BeanMeta factoringUnitBeanMeta;
    private BeanMeta obligationSplitBeanMeta;
	public static String FIELDGROUP_AGGCHILDINST = "aggChildInst";
	public static String FIELDGROUP_AGG_COPY_PARENT_TO_CHILD = "aggPrntToChld";
	public static String FIELDGROUP_AGGPURSUPLINK = "aggPurSupLink";
	public static String FIELDGROUP_FACTAGGMISDATA = "factAggMisData";
	public static String FIELDGROUP_INSTAGGMISDATA = "instAggMisData";
	public static String FIELDGROUP_AGGCHILDTOPARENT = "aggChildToParent";
	public static String FIELDGROUP_AGGSPLITSDETAILS = "aggSplitsDetails";
	public static String FIELDGROUP_AGGPARENTINST = "aggParentInst";

	public AggregatorInstrumentBO() {
		super();
		instrumentBO = new InstrumentBO();
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		factoredBeanDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
		factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
		gemInvoiceDao = new GenericDAO<GEMInvoiceBean>(GEMInvoiceBean.class);
		instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        factoredPaymentDAO = new CompositeGenericDAO<FactoredPaymentBean>(FactoredPaymentBean.class);
        factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
        obligationSplitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationSplitsBean.class);
        factoringUnitBO = new FactoringUnitBO();
        purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
	}
	public Map<Long, Object[]> getFactoringUnits(Connection pConnection, Date pFromDate, Date pToDate,  List<String> pPurchaserCodes, IAppUserBean pUserBean) throws Exception {
		Map<Long, Object[]> lFuwiseDetails = new HashMap<Long,Object[]>();
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		//
    	CompositeGenericDAO<ObliFUInstDetailSplitsBean> lObligationFactoringUnitDAO = null;

        lSql.append("SELECT * FROM FactoringUnits ");
		lSql.append(" JOIN AGGREGATORPURCHASERMAP ON (  APMPURCHASER = FUPURCHASER ) ");
    	lSql.append(" LEFT OUTER JOIN Obligations ON ( OBFUID=FUID ) ");
    	lSql.append(" LEFT OUTER JOIN ObligationSplits ON ( OBSOBID=OBID ) ");
    	lSql.append(" WHERE FURECORDVERSION > 0 AND APMRECORDVERSION > 0 ");
    	lSql.append(" AND ( OBRECORDVERSION IS NULL OR OBRECORDVERSION > 0 ) ");
    	lSql.append(" AND ( OBSRECORDVERSION IS NULL OR OBSRECORDVERSION > 0 ) ");
        //
		lSql.append(" AND APMAGGREGATOR = ").append(lDBHelper.formatString(pUserBean.getDomain()));
    	//
		lSql.append(" AND ( OBSTATUS IS NULL ");
        lSql.append("  OR  OBSTATUS NOT IN ( ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) "); 
		lSql.append(" ) ");
		lSql.append(" AND ( OBSSTATUS IS NULL ");
		lSql.append("  OR OBSSTATUS NOT IN ( ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) "); 
		lSql.append(" ) ");
        //
        String lFuFilterColumn = "FUStatusUpdateTime";
		if(pFromDate != null && pToDate != null){
			lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ").append(DBHelper.getInstance().formatDate(pFromDate)).append(" AND ").append(DBHelper.getInstance().formatDate(pToDate));
		}else{
			if(pFromDate != null){
				lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') >= ").append(DBHelper.getInstance().formatDate(pFromDate));
			}
			if(pToDate != null){
				lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') <= ").append(DBHelper.getInstance().formatDate(pToDate));
			}
		}
		if(pPurchaserCodes!=null && pPurchaserCodes.size() > 0){
			String[] lItemList = TredsHelper.getInstance().getCSVStringListForInQuery(pPurchaserCodes);
			lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("FUPURCHASER", lItemList));
		}
        //
        lSql.append(" ORDER BY OBTYPE, OBTXNTYPE DESC, OBSPARTNUMBER ");
        try {
            lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailSplitsBean>(ObliFUInstDetailSplitsBean.class);
        	List<ObliFUInstDetailSplitsBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
        	if(lObligations!=null && lObligations.size() > 0){
        		FactoringUnitBean lFuBean =  null;
        		ObligationBean lObliBean = null;
        		ObligationSplitsBean lObliSplitBean = null;
        		List<ObligationDetailBean> lDetailList = null;
        		Object[] lFuAndObliDetailList = null;
        		Map<Long,ObligationBean> lParentObligBeans = new HashMap<Long,ObligationBean>();
        		//
        		for(ObliFUInstDetailSplitsBean lObliFUInstBean : lObligations){
        			lFuBean = lObliFUInstBean.getFactoringUnitBean();
        			lObliSplitBean = lObliFUInstBean.getObligationSplitBean();
        			lObliBean = null;
        			//to have only one reference of parent in the splits
        			if(lObliSplitBean.getId()!=null) {
        			if(!lParentObligBeans.containsKey(lObliSplitBean.getId())) {
        				lObliBean = lObliFUInstBean.getObligationBean();
        				lParentObligBeans.put(lObliBean.getId(), lObliBean);
        			}else {
        				lObliBean = lParentObligBeans.get(lObliSplitBean.getId());
        			}
        			lObliSplitBean.setParentObligation(lObliBean);
        			}
        			//
        			if(!lFuwiseDetails.containsKey(lFuBean.getId())){
        				lFuAndObliDetailList=new Object[2];
        				lDetailList = new ArrayList<ObligationDetailBean>();
        				lFuAndObliDetailList[0] = lFuBean;
        				lFuAndObliDetailList[1] = lDetailList;
        				//
        				lFuwiseDetails.put(lFuBean.getId(), lFuAndObliDetailList);
        			}
        			lDetailList = (List<ObligationDetailBean>)lFuwiseDetails.get(lFuBean.getId())[1];
        			ObligationDetailBean lObligationDetailBean = new ObligationDetailBean();
        			if(lObliSplitBean.getId()!=null) {
        			lObligationDetailBean.setObligationSplitsBean(lObliSplitBean);
        			lObligationDetailBean.setObligationBean((ObligationBean)lObliSplitBean.getParentObligation());
        			lDetailList.add(lObligationDetailBean);
        			}
        		}    		
        	}

        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return lFuwiseDetails;		
	}
	
	public Object[] getFactoringUnits(Connection pConnection, Long pFactoringUnitId, IAppUserBean pUserBean) throws Exception {
		Object[] lReturnValue = new Object[2];
		// Index 0 = factoring unit bean
		// Index 1 = (obligationList) - List of ObligationDetailBean
		// one who queries this will have to make the transactionList by selecting appropriate obligation splits
		//factoring unit
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		lSql.append(" SELECT * FROM FACTORINGUNITS ");
		lSql.append(" JOIN AGGREGATORPURCHASERMAP ON (  APMPURCHASER = FUPURCHASER ) ");
		lSql.append(" WHERE FUID  = ").append(pFactoringUnitId).append(" ");
		lSql.append(" AND APMAGGREGATOR = ").append(lDbHelper.formatString(pUserBean.getDomain()));
		FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lSql.toString());
		lReturnValue[0] = lFactoringUnitBean;
		//parent obligations
		List<ObligationDetailBean> lDetailList = TredsHelper.getInstance().getObligationDetailBean(pConnection, pFactoringUnitId, null, null);
		ObligationBean lObliBean = null;
		ObligationSplitsBean lObliSplitsBean = null; 
		Map<Long,ObligationBean> lParentObligBeans = new HashMap<Long,ObligationBean>();
		for (ObligationDetailBean lBean : lDetailList){
			lObliSplitsBean = lBean.getObligationSplitsBean();
			//to have only one reference of parent in the splits
			if(!lParentObligBeans.containsKey(lObliSplitsBean.getId())) {
				lObliBean = lBean.getObligationBean();
				lParentObligBeans.put(lObliBean.getId(), lObliBean);
			}else {
				lObliBean = lParentObligBeans.get(lObliSplitsBean.getId());
			}
			lObliSplitsBean.setParentObligation(lObliBean);
		}
		lReturnValue[1]= lDetailList;
		return lReturnValue;
	}
	//TML-CASHINVOICE
	public Map<Long, Object[]> getDataForMIS(Connection pConnection, List<String> pPurchaserCodes, ObligationBean.Type pLeg, Date pFromDate, Date pToDate, boolean pObligation, IAppUserBean pUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		lSql.append(" SELECT * FROM FACTORINGUNITS JOIN INSTRUMENTS ON INFUID = FUId  ");
		if(pObligation) {
			lSql.append(" LEFT OUTER JOIN OBLIGATIONS on (OBFUID = FUID) ");
		}else {
			lSql.append(" LEFT OUTER JOIN OBLIGATIONS on (OBFUID = FUID AND OBTXNENTITY = FUFINANCIER) ");
		}
		lSql.append(" LEFT OUTER JOIN OBLIGATIONSPLITS ON (OBID = OBSOBID) ");
		lSql.append(" JOIN AGGREGATORPURCHASERMAP ON (  APMPURCHASER = FUPURCHASER ").append(" )");
		lSql.append(" WHERE FURECORDVERSION > 0 AND INRECORDVERSION > 0 AND OBRECORDVERSION > 0 AND OBSRECORDVERSION >0 ");
		lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(pLeg.getCode()));
		lSql.append(" AND APMAGGREGATOR = ").append(lDbHelper.formatString(pUserBean.getDomain()));
		lSql.append(" AND FUPURCHASER = APMPURCHASER ");
		if(pPurchaserCodes!=null && pPurchaserCodes.size() > 0){
			String[] lItemList = TredsHelper.getInstance().getCSVStringListForInQuery(pPurchaserCodes);
			lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("FUPURCHASER", lItemList));
		}
		//AS PER CASH INVOICE - THEY SHOULD GET THE MIS REPROT FOR THE ACTUAL SETTLED DATE 
		String lFuFilterColumn = "OBSETTLEDDATE";
		if(pObligation) {
			lFuFilterColumn = "OBDATE";
		}
		lSql.append(" AND ").append(lFuFilterColumn).append(" IS NOT NULL ");
		if(pFromDate != null && pToDate != null){
			lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') BETWEEN ").append(DBHelper.getInstance().formatDate(pFromDate)).append(" AND ").append(DBHelper.getInstance().formatDate(pToDate));
		}else{
			if(pFromDate != null){
				lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') >= ").append(DBHelper.getInstance().formatDate(pFromDate));
			}
			if(pToDate != null){
				lSql.append(" AND TO_DATE(TO_CHAR(").append(lFuFilterColumn).append(",'DD-MM-YYYY'),'DD-MM-YYYY') <= ").append(DBHelper.getInstance().formatDate(pToDate));
			}
		}

    	List<FactoredPaymentBean> lFactoredPaymentBeanList = factoredPaymentDAO.findListFromSql(pConnection, lSql.toString(), -1);
    	Map<Long,Object[]> lMap = new HashMap<Long,Object[]>();
    	FactoringUnitBean lFactoringUnitBean = null;
    	InstrumentBean lInstrumentBean = null;
    	ObligationBean lObligationBean = null;
    	Object[] lObjArr = null;
    	List<ObligationSplitsBean> lObligationSplitsList = null;
    	for (FactoredPaymentBean lFactoredPaymentBean : lFactoredPaymentBeanList) {
    		lFactoringUnitBean = lFactoredPaymentBean.getFactoringUnitBean();
    		if (!lMap.containsKey(lFactoringUnitBean.getId())) {
    			lInstrumentBean = lFactoredPaymentBean.getInstrumentBean();
    			lInstrumentBean.populateNonDatabaseFields();
    			List<InstrumentBean> lClubbedInstList = getClubbedBeans(pConnection, lInstrumentBean.getId());
    			lInstrumentBean.setGroupedInstruments(lClubbedInstList);
    			lObligationBean = lFactoredPaymentBean.getObligationBean();
    			lObjArr = new Object[]{lInstrumentBean,lFactoringUnitBean,lObligationBean,new ArrayList<ObligationSplitsBean>()};
    			lMap.put(lFactoringUnitBean.getId(), lObjArr);
    		}
    		lObjArr = lMap.get(lFactoringUnitBean.getId());
    		if(lObjArr != null ){
        		lObligationSplitsList = (List<ObligationSplitsBean>) lObjArr[3];
        		IObligation lSplitObli = lFactoredPaymentBean.getObligationSplitsBean();
        		lSplitObli.setParentObligation(lFactoredPaymentBean.getObligationBean());
        		lObligationSplitsList.add(lFactoredPaymentBean.getObligationSplitsBean());
    		}
    	}
		return lMap;
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
	//TML-CASHINVOICE
	public List<Object> formatMISData(Map<Long, Object[]> lResultMap) throws Exception {
		List<Object> lReturnMapList = new ArrayList<Object>();
		for (Object[] lObjArr : lResultMap.values()){
    		Map<String,Object> lTmpMap = new HashMap<String, Object>();
        	FactoringUnitBean lFUBean = (FactoringUnitBean) lObjArr[1];
        	InstrumentBean lInstBean =  (InstrumentBean) lObjArr[0];
        	//ObligationBean lObligationBean = (ObligationBean) lObjArr[2];
        	List<ObligationSplitsBean> lObligationSplitBeans = (List<ObligationSplitsBean>) lObjArr[3];
			//12. Interest on TML
			//13. Interest on Vendor
			//15. Charges on TML
			//16. Charges on Vendor
        	//"factAggMisData" :["id","acceptDateTime","status","tenure","factoredAmount","acceptedRate","interest","charges","financier","finName","supplier","supName","supGstn","leg1Date","maturityDate","purchaserLeg1Interest","supplierLeg1Interest","purchaserLeg2Interest","extendedDueDate"],
    		lTmpMap.putAll(factoringUnitBeanMeta.formatAsMap(lFUBean, FIELDGROUP_FACTAGGMISDATA, null, false));
    		if (lFUBean.getFinancier()!=null) {
    			lTmpMap.put("finPan", (TredsHelper.getInstance().getAppEntityBean(lFUBean.getFinancier())).getPan());
    		}
    		lTmpMap.put("inId",lInstBean.getId());
    		lTmpMap.put("fuId",lFUBean.getId());
    		lTmpMap.put("chargeBearer", lFUBean.getChargeBearerEntityCode());
    		lTmpMap.put("chargeBearerType", lFUBean.getChargeBearer().getCode());
    		lTmpMap.put("charges", lFUBean.getTotalChargesIncludingTax());
    		lTmpMap.putAll(instrumentBeanMeta.formatAsMap(lInstBean, FIELDGROUP_INSTAGGMISDATA, null, false));
    		Map<String,Object> lChildMap = new HashMap<String,Object>();
    		List<Map<String,Object>> lChildList = new ArrayList<Map<String,Object>>();
    		List<InstrumentBean> lChildBeanList = lInstBean.getGroupedInstruments();
    		for(InstrumentBean lBean : lChildBeanList){
    			lChildMap.putAll(instrumentBeanMeta.formatAsMap(lInstBean, FIELDGROUP_INSTAGGMISDATA, null, false));
    			lChildList.add(lChildMap);
    		}
    		Map<String,Object> lSplitMap = new HashMap<String,Object>();
    		List<Map<String,Object>> lSplitList = new ArrayList<Map<String,Object>>();
    		for(ObligationSplitsBean lBean : lObligationSplitBeans){
    			IObligation lTmpParentOblig = lBean.getParentObligation();
        		lSplitMap = new HashMap<String,Object>();
        		//"aggSplitsDetails": ["partNumber","settledDate","amount","status","obId","partNumber" ]
    			lSplitMap.putAll(obligationSplitBeanMeta.formatAsMap(lBean, FIELDGROUP_AGGSPLITSDETAILS, null, false));
    			lSplitMap.put("type", lTmpParentOblig.getType().getCode());
    			lSplitMap.put("txnType", lTmpParentOblig.getTxnType().getCode());
    			lSplitMap.put("utrNumber", lBean.getPaymentRefNo());
    			lSplitMap.put("bankAccountNumber", lTmpParentOblig.getPayDetail1());
    			lSplitMap.put("obligationDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT, ((ObligationBean)lTmpParentOblig).getOriginalDate()));
    			lSplitMap.put("entity", lTmpParentOblig.getTxnEntity());
    			lSplitList.add(lSplitMap);
    		}
    		lTmpMap.put("paymentDetails", lSplitList);
    		lTmpMap.put("instrumentList", lChildList);
    		lReturnMapList.add(lTmpMap);
    	}
		return lReturnMapList;
	}
	//GEM
	public String getWithdrawFactoringUnits(ExecutionContext pExecutionContext, List<Long> pFuIds , AppUserBean pUserBean) throws Exception {
		FactoringUnitBean lFilterBean = new FactoringUnitBean();
		InstrumentBean lInstrumentFilterBean = new InstrumentBean();
		Map<Long, Long> lMap = new HashMap<Long, Long>();
		List<Long> lList = new ArrayList<Long>();
		List<FactoringUnitBean> lFactoringUnitList = new ArrayList<FactoringUnitBean>();
		for(Long lFuId : pFuIds){
			lFilterBean.setId(lFuId);
			FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
			if(lFactoringUnitBean!=null && lFactoringUnitBean.getId()!=null){
				lFactoringUnitList.add(lFactoringUnitBean);
				lInstrumentFilterBean.setFuId(lFuId);
				InstrumentBean lInstrumentBean = instrumentDAO.findBean(pExecutionContext.getConnection(), lInstrumentFilterBean);
				lMap.put(lInstrumentBean.getFuId(),lInstrumentBean.getId());
			}else{
				lMap.put(lFuId, null);
			}
		}
		String lTmpReturnStr = factoringUnitBO.updateStatus(pExecutionContext, lFactoringUnitList, pUserBean, FactoringUnitBean.Status.Withdrawn);
		Object lRetVal = new JsonSlurper().parseText(lTmpReturnStr);
    	List<Map<String,Object>> lTmpData = null;
        if(lRetVal!=null){
	    	if(lRetVal instanceof List<?>){
	    		List<Object> lTmpRetVal = (List<Object>) lRetVal;
	        	for(Object lTmp : lTmpRetVal){
	            	if(lTmp instanceof Map<?,?> && ((Map<String,Object>)lTmp).containsKey("data")){
	                    Map<String,Object> lTemplMap = new HashMap<String,Object>();
	                    lTemplMap = (Map<String, Object>) ((Map<String,Object>)lTmp).get("data");
	                    String lStatus =  lTemplMap.get("status").toString();
	            		((Map<String,Object>)lTmp).remove("data");
	            		((Map<String, Object>)lTmp).put("status", lStatus);
	            		if(!((Map<String, Object>)lTmp).containsKey("error")){
	            			Long lId = (Long) ((Map<String, Object>)lTmp).get("id");
	            			if(Objects.nonNull(lId)){
	            				if(lMap.containsKey(lId)){
	            					lList.add(lMap.get(lId));
	            				}
	            			}
	            			((Map<String, Object>)lTmp).put("message", "Withdrawn Successfully.");
	            		}
	            	}
	        	}
	        	if(lList != null && !lList.isEmpty()){
	                instrumentBO.unGroup(pExecutionContext.getConnection(), lList, pUserBean);
	        	}
	        }else{
	        	//error
	        }
	    	lTmpData = (List<Map<String,Object>>) lRetVal;
        }else {
	    	lTmpData = new ArrayList<Map<String,Object>>();
	    	lRetVal = lTmpData;
        }
        if( lTmpData!=null ){
        	for(Long lFuId : lMap.keySet()){
        		Long lInstId = lMap.get(lFuId);
        		if(lInstId==null){
        			Map<String,Object> lTmpFuData = new HashMap<String,Object>();
        			lTmpFuData.put("fuid", lFuId);
        			lTmpFuData.put("error", "Factoring Unit not found for FUId : "+ lFuId);
        			lTmpData.add(lTmpFuData);
        		}
        	}
        }
        
		return new JsonBuilder(lRetVal).toString();
	}
	//FOR GEM
	public Map<String, Object> createInstrument(HttpServletRequest pRequest,ExecutionContext pExecutionContext,InstrumentBean pInstrumentBean,AppUserBean pAppUserBean, Long pArrId, boolean pIsPlatform,Map<String, Object> pMap, GEMInvoiceBean pGemInvoiceBean) throws Exception {
		 List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
		 Connection lConnection = pExecutionContext.getConnection();
		 if(StringUtils.isBlank(pInstrumentBean.getPoNumber())){
			 throw new CommonBusinessException("PO number is mandatory.");
		 }
		 pInstrumentBean.setPoDate(pInstrumentBean.getInstDate());
	     pInstrumentBean.setPurchaser(TredsHelper.getInstance().getEntityCode(lConnection, pMap.get("purPan").toString(), pInstrumentBean.getPurGstn(),EntityType.Purchaser));
	     pInstrumentBean.setSupplier(TredsHelper.getInstance().getEntityCode(lConnection, pMap.get("supPan").toString(), pInstrumentBean.getSupGstn(),EntityType.Supplier));
	     pInstrumentBean.setCounterEntity(pInstrumentBean.getPurchaser());
	     pInstrumentBean.setMakerEntity(pInstrumentBean.getSupplier());
	     pInstrumentBean.setAggregatorEntity(pAppUserBean.getDomain());
	     pInstrumentBean.setAggregatorAuId(pAppUserBean.getId());
	     if(StringUtils.isEmpty(pInstrumentBean.getPurchaser())){
	 		appendMessage(lMessages, "purchaser", "Purchaser not found for " + pMap.get("purPan").toString() + " and " + pInstrumentBean.getPurGstn());
	     }
	     if(StringUtils.isEmpty(pInstrumentBean.getSupplier())){
	     	appendMessage(lMessages, "supplier", "Supplier not found for " +pMap.get("supPan").toString() + " and " + pInstrumentBean.getSupGstn());
	     }
	     if (pGemInvoiceBean!=null) {
	    	 if (Objects.isNull(pGemInvoiceBean.getPurchaser())) {
		    	 pGemInvoiceBean.setPurchaser(pInstrumentBean.getPurchaser());
		     }
		     if (Objects.isNull(pGemInvoiceBean.getSupplier())) {
		    	 pGemInvoiceBean.setSupplier(pInstrumentBean.getSupplier());
		     }
	     }
	     if(lMessages.size() > 0){
	    	 if (!pIsPlatform) {
	    		 logGemInvoiceForResending(pAppUserBean,pInstrumentBean, pArrId,pMap);
	    	 }
	     	 throw new CommonBusinessException(new JsonBuilder(lMessages).toString());
		 }
		//find the buyer seller link and populate the fields from there
		instrumentBO.validatePurchaserSupplierLink(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean, false);
		instrumentBO.validateAndSetSupplierLocationDetails(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean);
		instrumentBO.validateAndSetPurchaserLocationDetails(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean);
        //then call the populate function
        //then call the instBo.saveWithoutCommit
		instrumentBO.saveWithoutCommit(pExecutionContext, pInstrumentBean, (AppUserBean) pAppUserBean, true, false, false, "", true, AuthenticationHandler.getInstance().getLoginKey(pRequest),null);
		
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		if(pInstrumentBean.getFuId() != null){
			lFactoringUnitBean.setId(pInstrumentBean.getFuId());
			lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(lConnection, lFactoringUnitBean);
		}
        //
		InstrumentBean lReturnInstrumentBean = pInstrumentBean;
		Map<String, Object> lInstrumentMap = new HashMap<String , Object>();
		lInstrumentMap.put("inId", lReturnInstrumentBean.getId());
		lInstrumentMap.put("inStatus", pInstrumentBean.getStatus().getCode());
		if(pInstrumentBean.getFuId() != null){
			lInstrumentMap.put("fuId", lReturnInstrumentBean.getFuId());
			lInstrumentMap.put("fuStatus",lFactoringUnitBean.getStatus().getCode());
			lInstrumentMap.put("message", "Factoring unit created successfully.");
		}else {
			lInstrumentMap.put("message", "Instrument created successfully.");
		}
		return lInstrumentMap;
	}
	//FOR GEM - previously used
	public Map<String, Object> createInstrumentAndConvertToFactUnit(HttpServletRequest pRequest,ExecutionContext pExecutionContext,InstrumentBean pInstrumentBean,AppUserBean pAppUserBean, Long pArrId, boolean pIsPlatform,Map<String, Object> pMap, GEMInvoiceBean pGemInvoiceBean) throws Exception {
		 List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
		 Connection lConnection = pExecutionContext.getConnection();
		 if(StringUtils.isBlank(pInstrumentBean.getPoNumber())){
			 throw new CommonBusinessException("PO number is mandatory.");
		 }
		 pInstrumentBean.setPoDate(pInstrumentBean.getInstDate());
	     pInstrumentBean.setPurchaser(TredsHelper.getInstance().getEntityCode(lConnection, pMap.get("purPan").toString(), pInstrumentBean.getPurGstn(),EntityType.Purchaser));
	     pInstrumentBean.setSupplier(TredsHelper.getInstance().getEntityCode(lConnection, pMap.get("supPan").toString(), pInstrumentBean.getSupGstn(),EntityType.Supplier));
	     pInstrumentBean.setCounterEntity(pInstrumentBean.getSupplier());
	     pInstrumentBean.setMakerEntity(pInstrumentBean.getPurchaser());
	     pInstrumentBean.setAggregatorEntity(pAppUserBean.getDomain());
	     pInstrumentBean.setAggregatorAuId(pAppUserBean.getId());
	     if(StringUtils.isEmpty(pInstrumentBean.getPurchaser())){
	 		appendMessage(lMessages, "purchaser", "Purchaser not found for " + pInstrumentBean.getPurPan() + " and " + pInstrumentBean.getPurGstn());
	     }
	     if(StringUtils.isEmpty(pInstrumentBean.getSupplier())){
	     	appendMessage(lMessages, "supplier", "Supplier not found for " + pInstrumentBean.getSupPan() + " and " + pInstrumentBean.getSupGstn());
	     }
	     if (pGemInvoiceBean!=null) {
	    	 if (Objects.isNull(pGemInvoiceBean.getPurchaser())) {
		    	 pGemInvoiceBean.setPurchaser(pInstrumentBean.getPurchaser());
		     }
		     if (Objects.isNull(pGemInvoiceBean.getSupplier())) {
		    	 pGemInvoiceBean.setSupplier(pInstrumentBean.getSupplier());
		     }
	     }
	     if(lMessages.size() > 0){
	    	 if (!pIsPlatform) {
	    		 logGemInvoiceForResending(pAppUserBean,pInstrumentBean, pArrId,pMap);
	    	 }
	     	 throw new CommonBusinessException(new JsonBuilder(lMessages).toString());
		 }
		//find the buyer seller link and populate the fields from there
		instrumentBO.validatePurchaserSupplierLink(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean, false);
		instrumentBO.validateAndSetSupplierLocationDetails(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean);
		instrumentBO.validateAndSetPurchaserLocationDetails(lConnection, pInstrumentBean, (AppUserBean) pAppUserBean);
        //then call the populate function
        //then call the instBo.saveWithoutCommit
		instrumentBO.saveWithoutCommit(pExecutionContext, pInstrumentBean, (AppUserBean) pAppUserBean, true, false, true, "", true, AuthenticationHandler.getInstance().getLoginKey(pRequest),null);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		if(pInstrumentBean.getFuId() == null){
			throw new CommonBusinessException(" Factoring Unit not created please check purchaser supplier link");
		}else {
			lFactoringUnitBean.setId(pInstrumentBean.getFuId());
			lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(lConnection, lFactoringUnitBean);
		}
        //
		InstrumentBean lReturnInstrumentBean = pInstrumentBean;
		Map<String, Object> lInstrumentMap = new HashMap<String , Object>();
		lInstrumentMap.put("inId", lReturnInstrumentBean.getId());
		lInstrumentMap.put("inStatus", pInstrumentBean.getStatus().getCode());
		lInstrumentMap.put("fuId", lReturnInstrumentBean.getFuId());
		lInstrumentMap.put("fuStatus",lFactoringUnitBean.getStatus().getCode());
		lInstrumentMap.put("message", "Instrument created successfully.");
		return lInstrumentMap;
	}
	
	
	public static void appendMessage(List<Map<String, Object>> pMessages, String pFieldName, String pErrorMessage) {
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("field", pFieldName);
        lMap.put("error", pErrorMessage);
        if(pMessages!=null)
        	pMessages.add(lMap);
    }
	private List<FactoredBean> getFactoringUnitsByGem(Connection pConnection, Date pFromDate, Date pToDate, String pGemUniqueRequestIdentifier, AppUserBean pUserBean) throws Exception {
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM FACTORINGUNITS, INSTRUMENTS ");
		lSql.append(" WHERE FURECORDVERSION > 0 ");
		lSql.append(" AND FUINTRODUCINGAUID = ").append(pUserBean.getId());
		if(pFromDate != null){
			lSql.append(" AND TO_CHAR(FURECORDCREATETIME,'dd-mm-yyyy') >= ").append(lDbHelper.formatString(FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pFromDate)));
		}
		if(pToDate != null){
			lSql.append(" AND TO_CHAR(FURECORDCREATETIME,'dd-mm-yyyy') <= ").append(lDbHelper.formatString(FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pToDate)));
		}
//		if (pPurGSTN != null) {
//			lSql.append(" FUPURGSTN = ").append(lDbHelper.formatString(pPurGSTN));
//		}
		lSql.append(" AND INFUID = FUID ");
		lSql.append(" AND INOTHERSETTINGS LIKE '%").append(pGemUniqueRequestIdentifier).append("%' ");
		List<FactoredBean> lFactInstBeanList = factoredBeanDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lFactInstBeanList;
	}
	public String getFactoringUnitStatusByGem(Connection pConnection, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		String lResponse = null;
		Date lFromDate = null;
		Date lToDate = null;
		String lPurGSTN = null;
		String lGemUniqueRequestIdentifier =null;
		if(pMap.containsKey("fromDate") && StringUtils.isNotEmpty(pMap.get("fromDate").toString())){
			lFromDate = CommonUtilities.getDate(pMap.get("fromDate").toString(),AppConstants.DATE_FORMAT);
		}
		if(pMap.containsKey("toDate") && StringUtils.isNotEmpty(pMap.get("toDate").toString())){
			lToDate = CommonUtilities.getDate(pMap.get("toDate").toString(),AppConstants.DATE_FORMAT);
		}
		if(StringUtils.isNotEmpty(pMap.get("gemUniqueRequestIdentifier").toString())){
			lGemUniqueRequestIdentifier = pMap.get("gemUniqueRequestIdentifier").toString();
		}
		List<FactoredBean> lFactoredList = getFactoringUnitsByGem(pConnection, lFromDate, lToDate, lGemUniqueRequestIdentifier , pUserBean);
		Map<String,Object> lFactoringUnitMap = null;
    	List<Map<String , Object>> lList = new ArrayList<Map<String , Object>>();
		if (StringUtils.isNotEmpty(lGemUniqueRequestIdentifier) && lFactoredList.isEmpty() ) {
			lFactoringUnitMap = new HashMap<String,Object>();
    		lFactoringUnitMap.put("status", "NOTFOUND");
    		lFactoringUnitMap.put("fuId", "");
			lFactoringUnitMap.put("gemUniqueRequestIdentifier", lGemUniqueRequestIdentifier);
			lList.add(lFactoringUnitMap);
			lResponse = new JsonBuilder(lList).toString();
		}
    	if(lFactoredList != null){
        	for (FactoredBean lFactoredBean : lFactoredList) {
        		FactoringUnitBean lFactoringUnitBean = lFactoredBean.getFactoringUnitBean();
        		InstrumentBean lInstrumentBean = lFactoredBean.getInstrumentBean();
				String lInvoiceStatus = "REJECTED";
				//
        		lFactoringUnitMap = new HashMap<String,Object>();
        		lFactoringUnitMap.put("status", lFactoringUnitBean.getStatus().getCode());
        		lFactoringUnitMap.put("fuId", lFactoringUnitBean.getId());
				lFactoringUnitMap.put("gemUniqueRequestIdentifier", lGemUniqueRequestIdentifier);
				//
				if((FactoringUnitBean.Status.Factored.equals(lFactoringUnitBean.getStatus()))
						|| (FactoringUnitBean.Status.Leg_1_Settled.equals(lFactoringUnitBean.getStatus())) 
						|| (FactoringUnitBean.Status.Leg_2_Settled.equals(lFactoringUnitBean.getStatus())) 
						||	(FactoringUnitBean.Status.Leg_2_Failed.equals(lFactoringUnitBean.getStatus()))	) {
					lInvoiceStatus = "FACTORED";
				}else {
					lInvoiceStatus = lInstrumentBean.getStatus().getCode();
				}
				lFactoringUnitMap.put("invoiceStatus", lInvoiceStatus);
				//
        		if(StringUtils.isNotEmpty(lGemUniqueRequestIdentifier)){
        			if(hasGemUniqueRequestIdentifier(lInstrumentBean, lGemUniqueRequestIdentifier)){
                    	lList.add(lFactoringUnitMap);
        				break;
        			}else{
        				continue;
        			}
        		}
            	lList.add(lFactoringUnitMap);
    		}
    	}else{
    		throw new CommonBusinessException(" FactoringUnit not found ");
    	}
    	lResponse = new JsonBuilder(lList).toString();
    	return lResponse;
	}
	
	public String withdrawGem(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		String lResponse = null;
		String lGemUniqueRequestIdentifier =null;
		if(StringUtils.isNotEmpty(pMap.get("gemUniqueRequestIdentifier").toString())){
			lGemUniqueRequestIdentifier = pMap.get("gemUniqueRequestIdentifier").toString();
		}
		Connection lConnection = pExecutionContext.getConnection();
		List<FactoredBean> lFactoredList = getFactoringUnitsByGem(lConnection, null, null, lGemUniqueRequestIdentifier , pUserBean);
		Map<String,Object> lFactoringUnitMap = null;
    	List<Map<String , Object>> lList = new ArrayList<Map<String , Object>>();
		if (StringUtils.isNotEmpty(lGemUniqueRequestIdentifier) && lFactoredList.isEmpty() ) {
			lFactoringUnitMap = new HashMap<String,Object>();
    		lFactoringUnitMap.put("status", "NOTFOUND");
    		lFactoringUnitMap.put("fuId", "");
			lFactoringUnitMap.put("gemUniqueRequestIdentifier", lGemUniqueRequestIdentifier);
			lList.add(lFactoringUnitMap);
			lResponse = new JsonBuilder(lList).toString();
		}
    	if(lFactoredList != null){
        	for (FactoredBean lFactoredBean : lFactoredList) {
        		FactoringUnitBean lFactoringUnitBean = lFactoredBean.getFactoringUnitBean();
        		InstrumentBean lInstrumentBean = lFactoredBean.getInstrumentBean();
        		List<FactoringUnitBean> lTmpList = new ArrayList<FactoringUnitBean>();
        		lTmpList.add(lFactoringUnitBean);
        		String lResponseStr = factoringUnitBO.updateStatus(pExecutionContext, lTmpList, pUserBean, FactoringUnitBean.Status.Withdrawn);
        		lConnection = pExecutionContext.getConnection();
        		lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(lConnection, lFactoringUnitBean);
    			lFactoringUnitMap = new HashMap<String,Object>();
        		lFactoringUnitMap.put("status", lFactoringUnitBean.getStatus().getCode());
        		lFactoringUnitMap.put("fuId", lFactoringUnitBean.getId());
				lFactoringUnitMap.put("gemUniqueRequestIdentifier", lGemUniqueRequestIdentifier);
				if(StringUtils.isNotEmpty(lResponseStr)) {
					try {
						JsonSlurper lJsonSlurper = new JsonSlurper();
						Map<String,Object> lTmpMap = null;
						ArrayList<Object> lTmpArray = (ArrayList<Object>) lJsonSlurper.parseText(lResponseStr);
						if(lTmpArray != null && lTmpArray.size() > 0){
							lTmpMap = (Map<String,Object>)lTmpArray.get(0);
							if(lTmpMap!=null && lTmpMap.containsKey("error")) {
								lFactoringUnitMap.put("remarks",  (String) lTmpMap.get("error"));
							}
						}
					}catch(Exception lEx) {
						logger.error("Error in putting remarks.",lEx);
					}
				}
    		}
    	}else{
    		throw new CommonBusinessException(" FactoringUnit not found ");
    	}
    	lResponse = new JsonBuilder(lFactoringUnitMap).toString();
    	return lResponse;
	}
	
	private boolean hasGemUniqueRequestIdentifier(InstrumentBean pInstrumentBean, String pGemUniqueRequestIdentifier){
		List<String> lOtherSettingList = new ArrayList<String>();
 		Map<String,Object> lOtherSettingMap = new HashMap<String, Object>();
		JsonSlurper lJsonSlurper = new JsonSlurper();
		lOtherSettingMap = (Map<String, Object>) lJsonSlurper.parseText(pInstrumentBean.getOtherSettings());
		if(lOtherSettingMap != null){
			String lGemUniqueRequestIdentifier = (String) lOtherSettingMap.get("gemUniqueRequestIdentifier");
			return pGemUniqueRequestIdentifier.equals(lGemUniqueRequestIdentifier);
		}
		return false;
	}
	
	public boolean logGemInvoiceForResending(AppUserBean pAppUserBean,InstrumentBean pInstrumentBean, Long pArrId,Map<String, Object> pMap) {
		if(pInstrumentBean!=null) {
			GEMInvoiceBean lGemInvoiceBean = new GEMInvoiceBean();
			lGemInvoiceBean.setArrId(pArrId); 
			//TODO : THIS HAS TO BE SET SOMEHOW.
			lGemInvoiceBean.setInstNumber(pInstrumentBean.getInstNumber());
			lGemInvoiceBean.setSupplier(pInstrumentBean.getSupplier());
			
			lGemInvoiceBean.setSupGstn(pInstrumentBean.getSupGstn());
			lGemInvoiceBean.setPurchaser(pInstrumentBean.getPurchaser());
			if (!pMap.isEmpty()) {
				lGemInvoiceBean.setSupPan(pMap.get("supPan").toString());
				lGemInvoiceBean.setPurPan(pMap.get("purPan").toString());
			}
			lGemInvoiceBean.setPurGstn(pInstrumentBean.getPurGstn());
			lGemInvoiceBean.setGoodsAcceptDate(pInstrumentBean.getGoodsAcceptDate());
			lGemInvoiceBean.setPoDate(pInstrumentBean.getPoDate());
			lGemInvoiceBean.setPoNumber(pInstrumentBean.getPoNumber());
			lGemInvoiceBean.setInstDate(pInstrumentBean.getInstDate());
			lGemInvoiceBean.setInstDueDate(pInstrumentBean.getInstDueDate());
			lGemInvoiceBean.setAmount(pInstrumentBean.getAmount());
			lGemInvoiceBean.setAdjAmount(pInstrumentBean.getAdjAmount());
			lGemInvoiceBean.setCreditPeriod(pInstrumentBean.getCreditPeriod());
			lGemInvoiceBean.setCreateTime(new Timestamp(System.currentTimeMillis()));
			lGemInvoiceBean.setCreator(pAppUserBean.getId());
			lGemInvoiceBean.setStatus(com.xlx.treds.other.bean.GEMInvoiceBean.Status.Pending);
			try (Connection lConnection = DBHelper.getInstance().getConnection();){
				gemInvoiceDao.insert(lConnection, lGemInvoiceBean);
				//TODO: generate a Notification to inform TREDS operation
			} catch (SQLException e) {
				logger.info("Error in logGemInvoiceForResending : "+ e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public String getAddress(CompanyDetailBean pCDBean){
		String lAddress = "";
		if(CommonUtilities.hasValue(pCDBean.getCorLine1())) lAddress += pCDBean.getCorLine1();
		if(CommonUtilities.hasValue(pCDBean.getCorLine2())) lAddress += ", " + pCDBean.getCorLine2();
		if(CommonUtilities.hasValue(pCDBean.getCorLine3())) lAddress += ", " + pCDBean.getCorLine3();
		if(CommonUtilities.hasValue(pCDBean.getCorCity())) lAddress += ", " + pCDBean.getCorCity();
		if(CommonUtilities.hasValue(pCDBean.getCorDistrict())){
			if(!pCDBean.getCorDistrict().equalsIgnoreCase(pCDBean.getCorCity())){
				lAddress += ", " + pCDBean.getCorDistrict();
			}
		}
		if(CommonUtilities.hasValue(pCDBean.getCorState())){
			lAddress += ", " + TredsHelper.getInstance().getGSTStateDesc(pCDBean.getCorState());
		}
		if(CommonUtilities.hasValue(pCDBean.getCorZipCode())) lAddress += ", " + pCDBean.getCorZipCode();
		return lAddress;
	}

	public String getVendorDetails(ExecutionContext pExecutionContext, String pBuyerCode, Date pFromDate, Date pToDate) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		List<Map<String, Object>> lReturnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> lReturnMap = null;
		StringBuilder lSql = new StringBuilder();
        String lDBColumnNames =  purchaserSupplierLinkDAO.getDBColumnNameCsv(null,Arrays.asList("RELATIONEFFECTIVEDATE"));
		//
		lSql.append(" SELECT ");
    	lSql.append(lDBColumnNames).append(", PSW.LinkDate \"PSLRELATIONEFFECTIVEDATE\" ");
		lSql.append(" FROM PURCHASERSUPPLIERLINKS ");
		lSql.append(" JOIN ( ");
		lSql.append(" SELECT PLWSUPPLIER, PLWPURCHASER, PLWSTATUS ");
		lSql.append(" , MIN(PLWSTATUSUPDATETIME) LinkDate ");
		lSql.append(" , MAX(PLWSTATUSUPDATETIME) LinkUpdateDate ");
		lSql.append(" FROM PurchaserSupplierLinkWorkFlow ");
		lSql.append(" WHERE PLWSTATUS = ").append(DBHelper.getInstance().formatString(PurchaserSupplierLinkBean.ApprovalStatus.Approved.getCode()));
		lSql.append(" GROUP BY PLWSUPPLIER, PLWPURCHASER, PLWSTATUS ");
		lSql.append(" ) PSW ON ( PSLSUPPLIER=PSW.PLWSUPPLIER AND PSLPURCHASER=PSW.PLWPURCHASER) ");
		lSql.append(" AND PSLPURCHASER = ").append(DBHelper.getInstance().formatString(pBuyerCode));
		lSql.append(" AND ( ");
		lSql.append(" TO_date(to_char(PSW.LinkDate, 'DD-MM-YYYY') , 'DD-MM-YYYY')  BETWEEN ").append(DBHelper.getInstance().formatDate(pFromDate)).append(" AND ").append(DBHelper.getInstance().formatDate(pToDate));
		lSql.append(" OR TO_date(to_char(PSW.LinkUpdateDate, 'DD-MM-YYYY')  , 'DD-MM-YYYY')  BETWEEN ").append(DBHelper.getInstance().formatDate(pFromDate)).append(" AND ").append(DBHelper.getInstance().formatDate(pToDate));
		lSql.append(" ) ");
		
		List<PurchaserSupplierLinkBean> lPSLBeanList = purchaserSupplierLinkDAO.findListFromSql(lConnection, lSql.toString(), -1);
		if(lPSLBeanList != null){
			for(PurchaserSupplierLinkBean lPSlBean : lPSLBeanList){
				lReturnMap = new HashMap<String, Object>();
				AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lPSlBean.getSupplier());
				List<String> lSupGstnList = new ArrayList<String>();
				if(lAppEntityBean != null){
					CompanyLocationBean lFilterBean = new CompanyLocationBean();
					lFilterBean.setCdId(lAppEntityBean.getCdId());
					List<CompanyLocationBean> lCompanyLocationBeanList = companyLocationDAO.findList(lConnection, lFilterBean);
					if(lCompanyLocationBeanList != null){
						for(CompanyLocationBean lCLBean : lCompanyLocationBeanList){
							lSupGstnList.add(lCLBean.getGstn());
						}
					}
					lReturnMap.put("gstns", lSupGstnList);
					lReturnMap.put("vendorCode",lPSlBean.getSupplier());
					lReturnMap.put("vendorName",TredsHelper.getInstance().getAppEntityBean(lPSlBean.getSupplier()).getName());
					lReturnMap.put("erpCode", lPSlBean.getPurchaserSupplierRef());
					lReturnMap.put("onBoardDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lAppEntityBean.getRecordCreateTime()));
					lReturnMap.put("linkDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lPSlBean.getRelationEffectiveDate()));
					lReturnMap.put("creditPeriod", lPSlBean.getCreditPeriod());
					lReturnMap.put("extendedCreditPeriod", lPSlBean.getExtendedCreditPeriod()!=null?lPSlBean.getExtendedCreditPeriod():0);
				}
				lReturnList.add(lReturnMap);
			}
		}
		return new JsonBuilder(lReturnList).toString();
	}
}

