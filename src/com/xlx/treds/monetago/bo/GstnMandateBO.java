
package com.xlx.treds.monetago.bo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.InstAuthJerseyClient;
import com.xlx.treds.MiniInstrument;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.DocType;
import com.xlx.treds.instrument.bean.InstrumentBean.SupplyType;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.monetago.bean.EwayBillDetailsBean;
import com.xlx.treds.monetago.bean.GstnMandateBean;
import com.xlx.treds.monetago.bean.GstnMandateBean.Status;
import com.xlx.treds.monetago.bean.MonetagoEwaybillInfoBean;
import com.xlx.treds.monetago.bean.MonetagoEwaybillVehicleListDetailsBean;
import com.xlx.treds.monetago.bean.MonetagoInvoiceInfoBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class GstnMandateBO {

	public static final Logger logger = LoggerFactory.getLogger(GstnMandateBO.class);
    
    private GenericDAO<GstnMandateBean> gstnMandateDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<EwayBillDetailsBean> ewayBillDetailsDAO;
	private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
	private InstrumentBO instrumentBO;
	
	private static final int DATES_INSTRUMENTDUEDATE = 0;
	private static final int DATES_STATUTORYDUEDATE = 1;
	private static final int DATES_MATURITYDATE = 2;
	private static final int DATES_EXTENDEDDUEDATE = 3;

    public GstnMandateBO() {
        super();
        gstnMandateDAO = new GenericDAO<GstnMandateBean>(GstnMandateBean.class);
        purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        ewayBillDetailsDAO = new GenericDAO<EwayBillDetailsBean>(EwayBillDetailsBean.class);
		purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
		instrumentBO = new InstrumentBO();
    }
    
    public GstnMandateBean findBean(ExecutionContext pExecutionContext, 
        GstnMandateBean pFilterBean) throws Exception {
        GstnMandateBean lGstnMandateBean = gstnMandateDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lGstnMandateBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lGstnMandateBean;
    }
    
    public List<GstnMandateBean> findList(ExecutionContext pExecutionContext, GstnMandateBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return gstnMandateDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, GstnMandateBean pGstnMandateBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        GstnMandateBean lOldGstnMandateBean = null;
        if (pNew) {

            gstnMandateDAO.insert(lConnection, pGstnMandateBean);
        } else {
            lOldGstnMandateBean = findBean(pExecutionContext, pGstnMandateBean);
            

            if (gstnMandateDAO.update(lConnection, pGstnMandateBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, GstnMandateBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        GstnMandateBean lGstnMandateBean = findBean(pExecutionContext, pFilterBean);
        gstnMandateDAO.delete(lConnection, lGstnMandateBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public List<GstnMandateBean> getVerifiedGSTNList(ExecutionContext pExecutionContext , IAppUserBean pUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        if(!lAppEntityBean.isSupplier()){
        	throw new CommonBusinessException("Not a Supplier.");
        }
        DBHelper lDbHelper = DBHelper.getInstance();
        Long lCDID = lAppEntityBean.getCdId();
        StringBuilder lSql = new StringBuilder();
        lSql.append(" SELECT "); 
        lSql.append(" GMID ");
        lSql.append(" , ").append(lDbHelper.formatString(lAppEntityBean.getCode())).append(" \"GMSupplierCode\" ");
        lSql.append(" , CLGSTN \"GMGSTN\" ");
        lSql.append(" , GMSTATUS ");
        lSql.append(" , GMSTATUSDATE ");
        lSql.append(" , GMSTATUSPAYLOAD ");
        //lSql.append(" , CDCOREMAIL GST");
        lSql.append(" , GMAUID ");
        lSql.append(" , GMRECORDVERSION ");
        lSql.append(" FROM COMPANYLOCATIONS ");
        lSql.append(" JOIN APPENTITIES ON ( CLCDID = AECDID AND AECODE = ").append(lDbHelper.formatString(lAppEntityBean.getCode())).append(" ) ");
        lSql.append(" LEFT OUTER JOIN GSTNMANDATE  ON (CLGSTN = GMGSTN) ");
        //lSql.append(" LEFT OUTER JOIN COMPANYDETAILS  ON (CDID = CLCDID) ");
        lSql.append(" WHERE CLCDID = ").append(lCDID).append(" AND CLRECORDVERSION>0 ");
        List<GstnMandateBean> lList = gstnMandateDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        if(lList!=null){
        	//TODO: TEST CODE
        	//
        	GstnMandateBean lGstnMandateBean = null;
    		CompanyDetailBean lCDBean= TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lAppEntityBean.getCdId(), false);
    		String lCDEmail = lCDBean.getCorEmail();
        	for(int lPtr=0; lPtr < lList.size(); lPtr++){
        		lGstnMandateBean = lList.get(lPtr);
        		lGstnMandateBean.setEmailList( Arrays.asList(new String[]{lCDEmail, pUserBean.getEmail()}) ) ;
        		// get Concent and also set payload
        		getConsent(lConnection,lGstnMandateBean, pUserBean);
        		//
        		if(!CommonUtilities.hasValue(lGstnMandateBean.getSupplierCode())){
            		lGstnMandateBean.setSupplierCode(lAppEntityBean.getCode());
        		}
/*        		if(!GstnMandateBean.Status.Completed.equals(lGstnMandateBean.getStatus())){
        			//make a call to monetago and fetch 
        			verifyWithMonetago(lGstnMandateBean); 
        			if(lGstnMandateBean.getId()==null || 
        					GstnMandateBean.Status.Completed.equals(lGstnMandateBean.getStatus()) ){
        				lGstnMandateBean.setAuId(pUserBean.getId());
        				//store to dbatabse, set status to not completed if we d
        				gstnMandateDAO.insert(lConnection, lGstnMandateBean);
        			}
        		}
*/        	}
        	
        }
        return lList;
    }
    
    private void verifyWithMonetago(GstnMandateBean pGstnMandateBean){
    	//make a call to monetago
    	//fill the payload in the bean
    	//if payload is ok then setStatus to completed //or else set status to pending
    	//send it back
    	//pGstnMandateBean.setStatusPayload(InstAuthJerseyClient.getInstance().getRegisteredSupplierPayLoad(pGstnMandateBean.getGstn()));
    	if( InstAuthJerseyClient.getInstance().isSupplierRegistered(pGstnMandateBean.getGstn(), pGstnMandateBean.getStatusPayload())){
    		pGstnMandateBean.setStatus(GstnMandateBean.Status.Completed);
    	}
    }
    
    private void getConsent(Connection pConnection, GstnMandateBean pGstnMandateBean, IAppUserBean pUserBean) throws Exception{
    	Long lConsentType = null;
    	String lPayLoad = InstAuthJerseyClient.getInstance().getRegisteredSupplierPayLoad(pGstnMandateBean.getGstn());
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	if(CommonUtilities.hasValue(lPayLoad)){
        	Map<String,Object> lTmpJson = (Map<String, Object>) lJsonSlurper.parseText(lPayLoad);
        	if(lTmpJson.containsKey("payload")){
        		pGstnMandateBean.setStatusPayload(lPayLoad);
	        	lTmpJson = (Map<String, Object>) lTmpJson.get("payload");
	        	if(lTmpJson.containsKey("supplier")){
	            	lTmpJson = (Map<String, Object>) lTmpJson.get("supplier");
	            	if(lTmpJson.containsKey("consent_type")){
	                	if(lTmpJson.get("consent_type")!=null && CommonUtilities.hasValue(lTmpJson.get("consent_type").toString())){
	                		lConsentType = new Long(lTmpJson.get("consent_type").toString());
	                		//single invoice registration
	                		if(lConsentType.longValue()==2 || lConsentType.longValue()==3){
	                			GstnMandateBean lFilterBean = new GstnMandateBean();
	                			lFilterBean.setSupplierCode(pGstnMandateBean.getSupplierCode());
	                			lFilterBean.setGstn(pGstnMandateBean.getGstn());
	                			try {
									GstnMandateBean lBean = gstnMandateDAO.findBean(pConnection, lFilterBean);
									if (lBean == null || (!Status.Completed.equals(lBean.getStatus()))){
										if(lBean == null){
											lBean=new GstnMandateBean();
											gstnMandateDAO.getBeanMeta().copyBean(pGstnMandateBean, lBean);
											lBean.setStatus(Status.Completed);
											lBean.setAuId(pUserBean.getId());
											gstnMandateDAO.insert(pConnection, lBean);
										}else{
											lBean.setStatus(Status.Completed);
											gstnMandateDAO.update(pConnection, lBean);
										}
										gstnMandateDAO.getBeanMeta().copyBean(lBean, pGstnMandateBean);
									}
								} catch (Exception e) {
									logger.info("Error in getConsent( ) "+e.getMessage()); 
								} 
	                		}else if (lConsentType.longValue()==1){
	                			//TODO: temperory Code to be removed
	                			GstnMandateBean lFilterBean = new GstnMandateBean();
	                			lFilterBean.setSupplierCode(pGstnMandateBean.getSupplierCode());
	                			lFilterBean.setGstn(pGstnMandateBean.getGstn());
	                			try {
									GstnMandateBean lBean = gstnMandateDAO.findBean(pConnection, lFilterBean);
									if (lBean == null){
										if(lBean == null){
											lBean=new GstnMandateBean();
											gstnMandateDAO.getBeanMeta().copyBean(pGstnMandateBean, lBean);
											lBean.setStatus(Status.Pending);
											lBean.setAuId(pUserBean.getId());
											gstnMandateDAO.insert(pConnection, lBean);
										}
										gstnMandateDAO.getBeanMeta().copyBean(lBean, pGstnMandateBean);
									}
								} catch (Exception e) {
									logger.info("Error in getConsent( ) "+e.getMessage()); 
								} 
	                		}
	                		pGstnMandateBean.setConsentType(lConsentType);
	                	}
	            	}
	        	}
        	}
    	}
    	
    }
    
    public List<PurchaserSupplierLinkBean> findListForMap(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM PurchaserSupplierLinks ");
        lSql.append(" JOIN GSTNMANDATE ON ( GMSUPPLIERCODE = PSLSupplier ) ");
        if ( pAppEntityBean.isPurchaser() ){
        	lSql.append(" WHERE PSLPurchaser = ").append(lDBHelper.formatString(pAppEntityBean.getCode()));
        }else if (pAppEntityBean.isSupplier()) {
        	lSql.append(" WHERE PSLSupplier = ").append(lDBHelper.formatString(pAppEntityBean.getCode()));
        }
        lSql.append(" AND PSLStatus = ").append(lDBHelper.formatString(PurchaserSupplierLinkBean.Status.Active.getCode()));
        List<PurchaserSupplierLinkBean> lList = purchaserSupplierLinkDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
        return lList;
    }

	public boolean uploadInstrumentToMonetago(ExecutionContext pExecutionContext, String pMessage, AppUserBean pUserBean) throws Exception {
		CompanyLocationBO lCompanyLocationBO = new CompanyLocationBO();
		JsonSlurper lJsonSlurper = new JsonSlurper();
		String lGstrPayLoad = null;
		String lEwayPayLoad = null;
		InstAuthJerseyClient lInstAuthJerseyClient = InstAuthJerseyClient.getInstance();
		Map<String,Object> lDataHash = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
		//icoming data validation
		validateInvoiceDetails(lDataHash);
		//
		Connection lConnection = pExecutionContext.getConnection();
		Long lSupLocation = Long.valueOf(lDataHash.get("supClId").toString());
		CompanyLocationBean lSupCLBean = new CompanyLocationBean();
		lSupCLBean.setId(lSupLocation);
		lSupCLBean = lCompanyLocationBO.findBean(pExecutionContext, lSupCLBean);
		Long lPurLocation = Long.valueOf(lDataHash.get("purClId").toString());
		CompanyLocationBean lPurCLBean = new CompanyLocationBean();
		lPurCLBean.setId(lPurLocation);
		lPurCLBean = lCompanyLocationBO.findBean(pExecutionContext, lPurCLBean);

		MiniInstrument lMiniInstrument = new MiniInstrument(lPurCLBean.getGstn(), lDataHash.get("instNumber").toString(), FormatHelper.getDate(lDataHash.get("instDate").toString(),"dd-MMM-yyyy"), new BigDecimal(lDataHash.get("amount").toString()), lDataHash.get("eway").toString());
		List<String> lEmails = new ArrayList<String>();
		lEmails.add(lSupCLBean.getEmail());
		if(InstAuthJerseyClient.getInstance().uploadInvoice(lSupCLBean.getGstn(), lMiniInstrument, lEmails)){
				EwayBillDetailsBean lEWayDetailsBean = new EwayBillDetailsBean();
				lEWayDetailsBean.setSupplier(lDataHash.get("supplier").toString());
				lEWayDetailsBean.setInstNumber(lDataHash.get("instNumber").toString());
				lEWayDetailsBean.setEwayBillNo(lDataHash.get("eway").toString());
				lEWayDetailsBean.setRecordCreator(pUserBean.getId());
				//
				//if gstr not found skip
				lGstrPayLoad = lInstAuthJerseyClient.getGstrData(lSupCLBean.getGstn(), lDataHash.get("instNumber").toString());
				MonetagoInvoiceInfoBean lMonetagoInvoiceInfoBean = lInstAuthJerseyClient.getGstrData(lGstrPayLoad);
				if(lMonetagoInvoiceInfoBean==null){
					//for now do nothing since it is not mandatory
				}
				//if eway not found throw
				lEwayPayLoad = lInstAuthJerseyClient.getEWayBillData(lSupCLBean.getGstn(), lDataHash.get("instNumber").toString());
				lEwayPayLoad = lEwayPayLoad.replaceAll("VehiclListDetails", "vehicleListDetails");
				MonetagoEwaybillInfoBean lEwaybillInfoBean = lInstAuthJerseyClient.getEWayBillData(lEwayPayLoad);
				if(lEwaybillInfoBean==null){
					
				}
				lEWayDetailsBean.setGstrPayload(lGstrPayLoad);
				lEWayDetailsBean.setEwayPayload(lEwayPayLoad);
		    	lEWayDetailsBean.setTredsPayload(pMessage);
		    	//
		    	ewayBillDetailsDAO.insert(lConnection, lEWayDetailsBean);

	    	//InstrumentBean lInstrumentBean = getInstrumentBeanWithEwayBillId(lConnection, lEWayDetailsBean, lInvoiceInfoBean, lEwayInfoBean);
	    	//Map<String,Object> lMap = instrumentDAO.getBeanMeta().formatAsMap(lInstrumentBean);
	    	return true;
		}else{
			return false;
		}
    }
	
	private void validateInvoiceDetails(Map<String,Object> pDataHash) throws CommonBusinessException{
		if(pDataHash!=null){
			String[] lKeys = new String[] {"supClId","purClId", "instNumber","instDate", "amount","eway" }; 
			String[] lNames = new String[] {"Supplier Location","Purchaser Location", "Instrument Number","Instrument Date", "Invoice Amount","E-Way Bill No." }; 
			String lMessage = "";
			for(int lPtr=0; lPtr < lKeys.length; lPtr++){
				String lKey = lKeys[lPtr];
				if(!pDataHash.containsKey(lKey) || !CommonUtilities.hasValue(pDataHash.get(lKey).toString())){
					if(lMessage.length() > 0 ) lMessage += ", ";
					lMessage += lNames[lPtr] + " missing";
				}
			}
			if(StringUtils.isNotBlank(lMessage)){
				throw new CommonBusinessException(lMessage);
			}
		}
	}
	
	public EwayBillDetailsBean getInfoFromDb(Connection pConnection,Map<String,Object> pDataHash) throws Exception{
		EwayBillDetailsBean lFilterBean = new EwayBillDetailsBean();
		//icoming data validation
		validateInvoiceDetails(pDataHash);
		//
		lFilterBean.setSupplier(pDataHash.get("supplier").toString());
		lFilterBean.setInstNumber(pDataHash.get("instNumber").toString());
		lFilterBean.setEwayBillNo(pDataHash.get("eway").toString());
		//
		EwayBillDetailsBean lEWayDetailsBean = ewayBillDetailsDAO.findBean(pConnection, lFilterBean);
		return lEWayDetailsBean;
	}

	public String getDataFromMonetago(ExecutionContext pExecutionContext, EwayBillDetailsBean pEwayBillDetailsBean, String pLoginKey) throws Exception {
		CompanyLocationBO lCompanyLocationBO = new CompanyLocationBO();
		JsonSlurper lJsonSlurper = new JsonSlurper();
		InstAuthJerseyClient lInstAuthJerseyClient = InstAuthJerseyClient.getInstance();
		Map<String,Object> lDataHash = (Map<String, Object>) lJsonSlurper.parseText(pEwayBillDetailsBean.getTredsPayload());
		Connection lConnection = pExecutionContext.getConnection();
		//call monetago api send the details from pmessage 
		//convert the response to following bean
		// eway details bean and pass its id in inst bean
		MonetagoInvoiceInfoBean lInvoiceInfoBean = null;
		MonetagoEwaybillInfoBean lEwayInfoBean = null;
		Long lSupLocation = Long.valueOf(lDataHash.get("supClId").toString());
		CompanyLocationBean lCLBean = new CompanyLocationBean();
		lCLBean.setId(lSupLocation);
		lCLBean = lCompanyLocationBO.findBean(pExecutionContext, lCLBean);
		lInvoiceInfoBean = lInstAuthJerseyClient.getGstrData(pEwayBillDetailsBean.getGstrPayload());
		lEwayInfoBean = lInstAuthJerseyClient.getEWayBillData(pEwayBillDetailsBean.getEwayPayload());
		//
    	InstrumentBean lInstrumentBean = getInstrumentBeanWithEwayBillId(pExecutionContext, pEwayBillDetailsBean, lInvoiceInfoBean, lEwayInfoBean);
    	if(lEwayInfoBean.getEwbNo()==null){
    		throw new CommonBusinessException("Verified Eway not found.");
    	}
    	Map<String,Object> lMap = instrumentDAO.getBeanMeta().formatAsMap(lInstrumentBean);
		return new JsonBuilder(lMap).toString();
    }
	
	public InstrumentBean getInstrumentBeanWithEwayBillId(ExecutionContext pExecutionContext,EwayBillDetailsBean pEwayBillDetailsBean, MonetagoInvoiceInfoBean pInvoiceInfoBean, MonetagoEwaybillInfoBean pEwayInfoBean) throws Exception{
		//convert both the beans to instrument beans 
    	InstrumentBean lInstrumentBean = getInstrumentBean(pExecutionContext.getConnection(), pInvoiceInfoBean,pEwayInfoBean,pEwayBillDetailsBean.getTredsPayload());
    	//filled instrument bean
    	lInstrumentBean.setEbdId(pEwayBillDetailsBean.getId());
    	return lInstrumentBean;
	}
    
	public InstrumentBean getInstrumentBean(Connection pConnection, MonetagoInvoiceInfoBean pInvoiceInfoBean, MonetagoEwaybillInfoBean pEwaybillInfoBean,String pMessage) throws Exception {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lDataHash = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
		InstrumentBean lInstrumentBean = new InstrumentBean();
		lInstrumentBean.setPurchaser(lDataHash.get("purchaser").toString());
		lInstrumentBean.setPurClId(Long.valueOf(lDataHash.get("purClId").toString()));
		lInstrumentBean.setSupplier(lDataHash.get("supplier").toString());
		lInstrumentBean.setSupClId(Long.valueOf(lDataHash.get("supClId").toString()));
		if (pInvoiceInfoBean != null){
			lInstrumentBean.setInstDate(pInvoiceInfoBean.getIdt());
			lInstrumentBean.setAmount(pInvoiceInfoBean.getVal());
			// if etin is for supplier set it for supplier or if for purchaser set for purchaser 
			lInstrumentBean.setSupGstn(pInvoiceInfoBean.getEtin());
			lInstrumentBean.setInstNumber(pInvoiceInfoBean.getInum());
		}else{
			//if gstr data is not present
			GenericDAO<CompanyLocationBean> lCompanyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
			CompanyLocationBean lCLBean = new CompanyLocationBean();
			lCLBean.setId(Long.valueOf(lDataHash.get("supClId").toString()));
			lCLBean = lCompanyLocationDAO.findBean(pConnection, lCLBean);
			lInstrumentBean.setInstDate(CommonUtilities.getDate(lDataHash.get("instDate").toString(),"dd-MM-yyyy"));
			lInstrumentBean.setAmount(new BigDecimal(lDataHash.get("amount").toString()));
			lInstrumentBean.setSupGstn(lCLBean.getGstn());
			lInstrumentBean.setInstNumber(lDataHash.get("instNumber").toString());
		}
		if (pEwaybillInfoBean != null) {
			lInstrumentBean.setEwayBillNo(pEwaybillInfoBean.getEwbNo().toString());
			if ( pEwaybillInfoBean.getSupplyType() != null ){
				if ( InstrumentBean.SupplyType.Inward.getCode().equals(pEwaybillInfoBean.getSupplyType()) ){
					lInstrumentBean.setSupplyType(SupplyType.Inward);
				}else if (InstrumentBean.SupplyType.Outward.getCode().equals(pEwaybillInfoBean.getSupplyType())){
					lInstrumentBean.setSupplyType(SupplyType.Outward);
				}
			}
			lInstrumentBean.setDocDate(pEwaybillInfoBean.getDocDate());
			lInstrumentBean.setDocNo(pEwaybillInfoBean.getDocNo());
			if ( pEwaybillInfoBean.getDocType() != null ){
				if ( InstrumentBean.DocType._Others.getCode().equals(pEwaybillInfoBean.getDocType()) ){
					lInstrumentBean.setDocType(DocType._Others);
				}else if (InstrumentBean.DocType.Invoice.getCode().equals(pEwaybillInfoBean.getDocType())){
					lInstrumentBean.setDocType(DocType.Invoice);
				}else if (InstrumentBean.DocType.Bill_of_Entry.getCode().equals(pEwaybillInfoBean.getDocType())){
					lInstrumentBean.setDocType(DocType.Bill_of_Entry);
				}else if (InstrumentBean.DocType.Challan.getCode().equals(pEwaybillInfoBean.getDocType())){
					lInstrumentBean.setDocType(DocType.Challan);
				}else if (InstrumentBean.DocType.Credit_Note.getCode().equals(pEwaybillInfoBean.getDocType())){
					lInstrumentBean.setDocType(DocType.Credit_Note);
				}else if (InstrumentBean.DocType.Invoice.getCode().equals(pEwaybillInfoBean.getDocType())){
					lInstrumentBean.setDocType(DocType.Invoice);
				}
			}
			lInstrumentBean.setFromPincode(pEwaybillInfoBean.getFromPincode().toString());
			lInstrumentBean.setToPincode(pEwaybillInfoBean.getToPincode().toString());
			MonetagoEwaybillVehicleListDetailsBean lVeichleBean = null;
			if( pEwaybillInfoBean.getVehicleListDetails() != null && pEwaybillInfoBean.getVehicleListDetails().size() > 0 ){
				lVeichleBean = pEwaybillInfoBean.getVehicleListDetails().get(0);
				if ( lVeichleBean.getTransMode()!= null ){
					if ( InstrumentBean.TransMode.Air.getCode().toString().equals(lVeichleBean.getTransMode()) ){
						lInstrumentBean.setTransMode(InstrumentBean.TransMode.Air);
					}else if ( InstrumentBean.TransMode.Rail.getCode().toString().equals(lVeichleBean.getTransMode()) ){
						lInstrumentBean.setTransMode(InstrumentBean.TransMode.Rail);
					}else if ( InstrumentBean.TransMode.Road.getCode().toString().equals(lVeichleBean.getTransMode()) ){
						lInstrumentBean.setTransMode(InstrumentBean.TransMode.Road);
					}else if ( InstrumentBean.TransMode.Ship.getCode().toString().equals(lVeichleBean.getTransMode()) ){
						lInstrumentBean.setTransMode(InstrumentBean.TransMode.Ship);
					}
				}
				lInstrumentBean.setTransporterName(pEwaybillInfoBean.getTransporterName());
				lInstrumentBean.setTransporterId(pEwaybillInfoBean.getTransporterId());
				lInstrumentBean.setTransDocDate(lVeichleBean.getTransDocDate());
				lInstrumentBean.setTransDocNo(lVeichleBean.getTransDocNo());
				lInstrumentBean.setVehicleNo(lVeichleBean.getVehicleNo());
			}
			}
		//populate purchasersupplier link
		// get purchaser supplier link
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = null;
		if (lPurchaserSupplierLinkBean == null) {
			lPSLFilterBean = new PurchaserSupplierLinkBean();
			lPSLFilterBean.setPurchaser(lInstrumentBean.getPurchaser());
			lPSLFilterBean.setSupplier(lInstrumentBean.getSupplier());
			lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
			lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
			if (lPurchaserSupplierLinkBean == null)
				throw new CommonBusinessException("Purchaser Supplier link not defined or Inactive.");
		}
		// only new instruments cannot be created, existing in draft can be
		// modified.
        if((lInstrumentBean.getId() == null) && CommonAppConstants.Yes.Yes.equals(lPurchaserSupplierLinkBean.getInWorkFlow())){
			throw new CommonBusinessException("Purchaser Supplier link in work flow, cannot add new instrument.");
		}
		lPurchaserSupplierLinkBean.populateNonDatabaseFields();
		// TODO: use field data and use beanmeta copy
		lInstrumentBean.setCostBearingType(lPurchaserSupplierLinkBean.getCostBearingType());
		lInstrumentBean.setSplittingPoint(lPurchaserSupplierLinkBean.getSplittingPoint());
		lInstrumentBean.setPreSplittingCostBearer(lPurchaserSupplierLinkBean.getPreSplittingCostBearer());
		lInstrumentBean.setPostSplittingCostBearer(lPurchaserSupplierLinkBean.getPostSplittingCostBearer());
		lInstrumentBean.setPeriod1CostBearer(lPurchaserSupplierLinkBean.getPeriod1CostBearer());
		lInstrumentBean.setPeriod1CostPercent(lPurchaserSupplierLinkBean.getPeriod1CostPercent());
		lInstrumentBean.setPeriod2CostBearer(lPurchaserSupplierLinkBean.getPeriod2CostBearer());
		lInstrumentBean.setPeriod2CostPercent(lPurchaserSupplierLinkBean.getPeriod2CostPercent());
		lInstrumentBean.setPeriod3CostBearer(lPurchaserSupplierLinkBean.getPeriod3CostBearer());
		lInstrumentBean.setPeriod3CostPercent(lPurchaserSupplierLinkBean.getPeriod3CostPercent());
		//
		lInstrumentBean.setAutoConvert(lPurchaserSupplierLinkBean.getAutoConvert());
		lInstrumentBean.setBuyerPercent(lPurchaserSupplierLinkBean.getBuyerPercent());
		lInstrumentBean.setChargeBearer(lPurchaserSupplierLinkBean.getChargeBearer());
		lInstrumentBean.setSettleLeg3Flag(lPurchaserSupplierLinkBean.getSettleLeg3Flag());
		lInstrumentBean.setBidAcceptingEntityType(lPurchaserSupplierLinkBean.getBidAcceptingEntityType());

		lInstrumentBean.setAutoAccept(lPurchaserSupplierLinkBean.getAutoAccept());
		lInstrumentBean.setAutoAcceptableBidTypes(lPurchaserSupplierLinkBean.getAutoAcceptableBidTypes());

		lInstrumentBean.setHaircutPercent(lPurchaserSupplierLinkBean.getHaircutPercent());
		lInstrumentBean.setCashDiscountPercent(lPurchaserSupplierLinkBean.getCashDiscountPercent());
		lInstrumentBean.setAdjAmount(BigDecimal.ZERO);
		lInstrumentBean.setCurrency(AppConstants.CURRENCY_INR);
		lInstrumentBean.setPurchaserRef(lPurchaserSupplierLinkBean.getSupplierPurchaserRef());
		lInstrumentBean.setSupplierRef(lPurchaserSupplierLinkBean.getPurchaserSupplierRef());
		//
		lInstrumentBean.setSalesCategory(lPurchaserSupplierLinkBean.getSalesCategory());
		lInstrumentBean.setCreditPeriod(lPurchaserSupplierLinkBean.getCreditPeriod());
		lInstrumentBean.setExtendedCreditPeriod(lPurchaserSupplierLinkBean.getExtendedCreditPeriod());
		//
		lInstrumentBean.setGoodsAcceptDate(lInstrumentBean.getInstDate());
		lInstrumentBean.setPoDate(lInstrumentBean.getInstDate());
		lInstrumentBean.setPoNumber(lInstrumentBean.getInstNumber());
		//
		calculateDateAndAmount(lInstrumentBean);
		//
		return lInstrumentBean;
	}
	
	public void calculateDateAndAmount(InstrumentBean pInstrumentBean) throws Exception{
		Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();
		AppEntityBean lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getSupplier());
		HashMap<String, Object> lMsmeSettings = (HashMap<String, Object>) RegistryHelper.getInstance().getStructureHash(AppConstants.REGISTRY_MSMESETTINGS).get(lSupplierEntityBean.getMsmeStatus());
		Long lMinUsance = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_MINUSANCE);
		Long lMSMECreditPeriod = (Long) lMsmeSettings.get(AppConstants.ATTRIBUTE_CREDITPERIOD);// 
		boolean lCreditPeriodUpdated = instrumentBO.updateCreditPeriod(pInstrumentBean,lMSMECreditPeriod);
		logger.info("Fetching DueDates for inid : "+(pInstrumentBean.getId()!=null?pInstrumentBean.getId():""));
		Date[] lDates = instrumentBO.getDueDates(pInstrumentBean.getGoodsAcceptDate(), pInstrumentBean.getCreditPeriod(),
				pInstrumentBean.getExtendedCreditPeriod(), lMSMECreditPeriod, pInstrumentBean.getEnableExtension());
		if (!lCreditPeriodUpdated && pInstrumentBean.getInstDueDate() != null) {
			if (!pInstrumentBean.getInstDueDate().equals(lDates[DATES_INSTRUMENTDUEDATE]))
				throw new CommonBusinessException("Mismatch in invoice due date");
		} else
			if (!lCreditPeriodUpdated) pInstrumentBean.setInstDueDate(lDates[DATES_INSTRUMENTDUEDATE]);
		
		if (!lCreditPeriodUpdated && pInstrumentBean.getStatDueDate() != null) {
			if (!pInstrumentBean.getStatDueDate().equals(lDates[DATES_STATUTORYDUEDATE]))
				throw new CommonBusinessException("Mismatch in statutory due date");
		} else
			pInstrumentBean.setStatDueDate(lDates[DATES_STATUTORYDUEDATE]);
		
		pInstrumentBean.setMaturityDate(lDates[DATES_MATURITYDATE]);
		// max factoring end date
		Date lEndDate = lDates[DATES_STATUTORYDUEDATE].compareTo(lDates[DATES_MATURITYDATE]) < 0? lDates[DATES_STATUTORYDUEDATE] : lDates[DATES_MATURITYDATE];
		// min of	maturity and stat due date
		lEndDate = instrumentBO.getEndDate(pInstrumentBean, lMinUsance, lEndDate);
		pInstrumentBean.setFactorMaxEndDateTime(new Timestamp(lEndDate.getTime() + AppConstants.DAY_IN_MILLIS - 1));
		logger.info(" FactorMaxEndDateTime  :" + pInstrumentBean.getFactorMaxEndDateTime());
//		if (pInstrumentBean.getFactorMaxEndDateTime().compareTo(lCurrentDate) < 0) {
//			throw new CommonBusinessException("Instrument is not factorable since maturity["+ FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_MATURITYDATE]) + "]/statutory["+ FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDates[DATES_STATUTORYDUEDATE])					+ "] due date does not meet the minimum usance criteria of " + lMinUsance);
//		}
		pInstrumentBean.setExtendedDueDate(lDates[DATES_EXTENDEDDUEDATE]);
		pInstrumentBean.setSupMsmeStatus(lSupplierEntityBean.getMsmeStatus());
		
		// usance from registry
//		if (OtherResourceCache.getInstance().getDiffInDays(pInstrumentBean.getStatDueDate(), lCurrentDate) < lMinUsance.longValue())
//			throw new CommonBusinessException("Instrument does not meet the minumum usance criteria of " + lMinUsance + " days");
//		
		//validateCreditPeriod(lInstrumentBean.getCreditPeriod(), lInstrumentBean.getEnableExtension(),lInstrumentBean.getExtendedCreditPeriod());
		
		// validate cash discount percentage - any system level value has been
		// modified.
		if (pInstrumentBean.getCashDiscountPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getCashDiscountPercent())) {
//			validateCashDiscount(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getCashDiscountPercent());
		}
		// calculate on basis of percentages received and compare the values
		// with received values
		// VALIDATE HAIRCUTVALUE
		if (pInstrumentBean.getHaircutPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getHaircutPercent())) {
			BigDecimal lHaircutValue = (pInstrumentBean.getAmount().multiply(pInstrumentBean.getHaircutPercent(), MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			pInstrumentBean.setAdjAmount(lHaircutValue);
		}
		// VALIDATE CASHDISCOUNT VALUE
		if (pInstrumentBean.getCashDiscountPercent() != null && !BigDecimal.ZERO.equals(pInstrumentBean.getCashDiscountPercent())) {
			BigDecimal lCashDiscValue = (pInstrumentBean.getAmount().multiply(pInstrumentBean.getCashDiscountPercent(), MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			pInstrumentBean.setCashDiscountValue(lCashDiscValue);
		}
		
		
		BigDecimal lNetAmount = pInstrumentBean.getAmount();
		if (pInstrumentBean.getAdjAmount() != null) lNetAmount = lNetAmount.subtract(pInstrumentBean.getAdjAmount());
		if (pInstrumentBean.getCashDiscountValue() != null) lNetAmount = lNetAmount.subtract(pInstrumentBean.getCashDiscountValue());
		if (pInstrumentBean.getTdsAmount() != null) lNetAmount = lNetAmount.subtract(pInstrumentBean.getTdsAmount());
		if (lNetAmount.compareTo(BigDecimal.ZERO) <= 0)
			throw new CommonBusinessException("Factoring unit cost cannot be zero or negative.");
		pInstrumentBean.setNetAmount(lNetAmount);
		//
		//
	}
	
	public Map<String,Object> getDataForInvoice(Connection pConnection,EwayBillDetailsBean pEwayBillDetailsBean ) throws Exception{
	 	EwayBillDetailsBean lEwayBillDetailsBean = ewayBillDetailsDAO.findBean(pConnection, pEwayBillDetailsBean);
        if(lEwayBillDetailsBean == null){
        	throw new CommonBusinessException("Data not found for Id : " + pEwayBillDetailsBean.getId());
        }
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String,Object> lMap = new HashMap<String,Object>();
        InstAuthJerseyClient lInstAuthJerseyClient = InstAuthJerseyClient.getInstance();
        // 
        Map<String,Object> lTempJson = (Map<String, Object>) lJsonSlurper.parseText(lEwayBillDetailsBean.getGstrPayload());
		lTempJson = (Map<String,Object>) lTempJson.get(InstAuthJerseyClient.JSON_KEY_GSTR_PAYLOAD);
		lTempJson = (Map<String,Object>) lTempJson.get(InstAuthJerseyClient.JSON_KEY_GSTR);
		lTempJson = (Map<String,Object>) lTempJson.get("payload");
		List<Map<String,Object>> lTempJson2 = (List<Map<String,Object>>) lTempJson.get("b2b");
		lTempJson2 = (List<Map<String,Object>>) lTempJson2.get(0).get("inv");
		lTempJson = lTempJson2.get(0);
		lMap.put("gstrData",lTempJson);
		
		lTempJson = (Map<String, Object>) lJsonSlurper.parseText(lEwayBillDetailsBean.getEwayPayload());
		lTempJson = (Map<String,Object>) lTempJson.get(InstAuthJerseyClient.JSON_KEY_EWAY_PAYLOAD);
		lTempJson = (Map<String,Object>) lTempJson.get(InstAuthJerseyClient.JSON_KEY_EWAY);
		lMap.put("ewayData",(Map<String,Object>) lTempJson.get("payload"));
        lMap.put("tredsData",(Map<String,Object>) lJsonSlurper.parseText(lEwayBillDetailsBean.getTredsPayload()));
		return lMap;
	}
	
}
