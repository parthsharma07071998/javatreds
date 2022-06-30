package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.InstrumentCreation;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.PlatformStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.Status;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkWorkFlowBean;
import com.xlx.treds.auction.bean.PurchaserSupplierRelationshipHistoryBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.AppEntityBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class PurchaserSupplierLinkBO {
    
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkProvDAO;
    private GenericDAO<PurchaserSupplierLinkWorkFlowBean> purchaserSupplierLinkWorkFlowDAO;
	private GenericDAO<CompanyDetailBean> companyDetailDao;
	private GenericDAO<PurchaserSupplierRelationshipHistoryBean> purSupRelHistoryDAO;
	private BeanMeta purchaserSupplierLinkBeanMeta;
	public static final String FIELDGROUP_UPDATEPLATFORMSTATUS = "updatePlatformStatus";

    public PurchaserSupplierLinkBO() {
        super();
        purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        purchaserSupplierLinkProvDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class, "PurchaserSupplierLinks_P" );
        purchaserSupplierLinkWorkFlowDAO = new GenericDAO<PurchaserSupplierLinkWorkFlowBean>(PurchaserSupplierLinkWorkFlowBean.class);
        companyDetailDao = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
        purSupRelHistoryDAO = new GenericDAO<PurchaserSupplierRelationshipHistoryBean>(PurchaserSupplierRelationshipHistoryBean.class);
        purchaserSupplierLinkBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class);
    }
    
    public PurchaserSupplierLinkBean findBean(ExecutionContext pExecutionContext, 
        PurchaserSupplierLinkBean pFilterBean) throws Exception {
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPurchaserSupplierLinkBean == null && !"TEMPLATE".equals(pFilterBean.getSupplier())) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        if (!"TEMPLATE".equals(pFilterBean.getSupplier())) {
        	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
            AppEntityBean lSupplierAppEntityBean =  TredsHelper.getInstance().getAppEntityBean(lPurchaserSupplierLinkBean.getSupplier());
            if(lSupplierAppEntityBean!=null) {
                lPurchaserSupplierLinkBean.setSupPan(lSupplierAppEntityBean.getPan());
                CompanyLocationBean lCLBean =  TredsHelper.getInstance().getRegisteredOfficeLocation(pExecutionContext.getConnection(), lPurchaserSupplierLinkBean.getSupplier());
                if(lCLBean!=null) {
                    lPurchaserSupplierLinkBean.setSupGstn(lCLBean.getGstn());
                }
            }
        }
        return lPurchaserSupplierLinkBean;
    }
    
    public List<PurchaserSupplierLinkBean> findList(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM PurchaserSupplierLinks WHERE 1=1 ");
        if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
            lSql.append(" AND ( PSLPurchaser = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            lSql.append(" OR PSLSupplier = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            lSql.append(" ) ");
        }else {
        	if (pFilterBean.getPurchaser()!=null) {
        		lSql.append(" AND PSLPurchaser = ").append(lDBHelper.formatString(pFilterBean.getPurchaser()));
        	}
        	if (pFilterBean.getSupplier()!=null){
        		lSql.append(" AND PSLSupplier = ").append(lDBHelper.formatString(pFilterBean.getSupplier()));
        	}
        }
        //lSql.append(" AND PSLStatus = ").append(lDBHelper.formatString(PurchaserSupplierLinkBean.Status.Active.getCode()));
        lSql.append(" UNION ALL ");
        lSql.append("SELECT * FROM PurchaserSupplierLinks_P WHERE 1=1 ");
        if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
            lSql.append(" AND ( PSLPurchaser = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            lSql.append(" OR PSLSupplier = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            lSql.append(" ) ");
        }else {
        	if (pFilterBean.getPurchaser()!=null) {
        		lSql.append(" AND PSLPurchaser = ").append(lDBHelper.formatString(pFilterBean.getPurchaser()));
        	}
        	if (pFilterBean.getSupplier()!=null){
        		lSql.append(" AND PSLSupplier = ").append(lDBHelper.formatString(pFilterBean.getSupplier()));
        	}
        }
        lSql.append(" AND PSLApprovalStatus NOT IN ( ").append(lDBHelper.formatString(PurchaserSupplierLinkBean.ApprovalStatus.Approved.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(PurchaserSupplierLinkBean.ApprovalStatus.Deleted.getCode())).append(" ) ");
        List<PurchaserSupplierLinkBean> lList = purchaserSupplierLinkDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lList) {
            //TODO : determine on the basis of status whether Active or Suspended and by whom.
        	if ("TEMPLATE".equals(lPurchaserSupplierLinkBean.getSupplier())){
        		continue;
        	}
        	if(PurchaserSupplierLinkBean.Status.Active.equals(lPurchaserSupplierLinkBean.getStatus())){
                if(PurchaserSupplierLinkBean.ApprovalStatus.Approved.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
                    lPurchaserSupplierLinkBean.setTab(Long.valueOf(1));
                }else{
                    if(lPurchaserSupplierLinkBean.getPurchaser().equals(pUserBean.getDomain())){
                    	if(PurchaserSupplierLinkBean.ApprovalStatus.Draft.equals(lPurchaserSupplierLinkBean.getApprovalStatus()) || 
                    			PurchaserSupplierLinkBean.ApprovalStatus.Returned.equals(lPurchaserSupplierLinkBean.getApprovalStatus()) )
                            lPurchaserSupplierLinkBean.setTab(Long.valueOf(0));
                    	else if (PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
                            lPurchaserSupplierLinkBean.setTab(Long.valueOf(2));
                    	}
                    	else if(PurchaserSupplierLinkBean.ApprovalStatus.Suspended.equals(lPurchaserSupplierLinkBean.getApprovalStatus()))
                    		lPurchaserSupplierLinkBean.setTab(Long.valueOf(3));
                    }else if(lPurchaserSupplierLinkBean.getSupplier().equals(pUserBean.getDomain())){
                    	if (PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
                            lPurchaserSupplierLinkBean.setTab(Long.valueOf(0));
                    	}
                    	else if(PurchaserSupplierLinkBean.ApprovalStatus.Suspended.equals(lPurchaserSupplierLinkBean.getApprovalStatus()))
                              lPurchaserSupplierLinkBean.setTab(Long.valueOf(3));
                    }else if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
                    	 if(PurchaserSupplierLinkBean.ApprovalStatus.Suspended.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
                    		 lPurchaserSupplierLinkBean.setTab(Long.valueOf(3));
                    	 }
                	}
                }
        	}else{
                lPurchaserSupplierLinkBean.setTab(Long.valueOf(3));
        	}
        }
        return lList;
    }
   
    public List<PurchaserSupplierLinkBean> findListForMap(ExecutionContext pExecutionContext, IAppUserBean pUserBean) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM PurchaserSupplierLinks WHERE (PSLPurchaser = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        lSql.append(" OR PSLSupplier = ").append(lDBHelper.formatString(pUserBean.getDomain())).append(")");
        lSql.append(" AND PSLStatus = ").append(lDBHelper.formatString(PurchaserSupplierLinkBean.Status.Active.getCode()));
        List<PurchaserSupplierLinkBean> lList = purchaserSupplierLinkDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
        if(lList!=null){
        	for(PurchaserSupplierLinkBean lPSBean : lList){
        		lPSBean.populateNonDatabaseFields();        		
        	}
        }
        return lList;
    }
    
    public String findListForLov(ExecutionContext pExecutionContext, boolean pPurchaserFlag, AppUserBean pUserBean, PurchaserSupplierLinkBean pFilterBean) throws Exception {
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        if (lAppEntityBean.isPlatform()) {
            AppEntityBO lAppEntityBO = new AppEntityBO();
            List<AppEntityBean> lAppEntityList = lAppEntityBO.findList(pExecutionContext, 
                    pPurchaserFlag?AppConstants.EntityType.Purchaser:AppConstants.EntityType.Supplier, pUserBean, false);
            for (AppEntityBean lBean : lAppEntityList) {
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lBean.getCode());
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lBean.getCode());
                lData.put(BeanFieldMeta.JSONKEY_DESC, lBean.getName());
                lResults.add(lData);
            }
        } else if ((pPurchaserFlag && lAppEntityBean.isPurchaser()) ||
                (!pPurchaserFlag && lAppEntityBean.isSupplier())) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lAppEntityBean.getName());
            lResults.add(lData);
        } else {
            PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
            if(pFilterBean!=null){
            	lFilterBean = pFilterBean;
            }else{
                if (pPurchaserFlag)
                    lFilterBean.setSupplier(pUserBean.getDomain());
                else
                    lFilterBean.setPurchaser(pUserBean.getDomain());
            }
            List<PurchaserSupplierLinkBean> lList = purchaserSupplierLinkDAO.findList(pExecutionContext.getConnection(), lFilterBean, (String)null);
            
            for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lList) {
                String lEntityCode = pPurchaserFlag?lPurchaserSupplierLinkBean.getPurchaser():lPurchaserSupplierLinkBean.getSupplier();
                lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lEntityCode});
                if (lAppEntityBean != null) {
                    Map<String, Object> lData = new HashMap<String, Object>();
                    lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
                    lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCode());
                    lData.put(BeanFieldMeta.JSONKEY_DESC, lAppEntityBean.getName());
                    lResults.add(lData);
                }
            }
        }
        return new JsonBuilder(lResults).toString();
    }

    public PurchaserSupplierLinkBean save(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, IAppUserBean pUserBean, 
        boolean pNew, boolean pUpload, List<String> pHeaderFieldList, boolean pApiUser) throws Exception {
    	PurchaserSupplierLinkBean lReturnBean = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
	        if ((lAppEntityBean == null) || (!lAppEntityBean.isPurchaser() && !lAppEntityBean.isSupplier()))
	            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
	        if (!pPurchaserSupplierLinkBean.getPurchaser().equals(pUserBean.getDomain()) && 
	                !pPurchaserSupplierLinkBean.getSupplier().equals(pUserBean.getDomain()))
	            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
         
        pPurchaserSupplierLinkBean.populateDatabaseFields();
        //
        boolean lLinkActivated = false;
        List<String> lFields = new ArrayList<String>();
        String lFieldGroup = BeanMeta.FIELDGROUP_INSERT;
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierLinkBean lOldPurchaserSupplierLinkBean = null;
        PurchaserSupplierLinkBean lFinalLinkBean = null;
        lOldPurchaserSupplierLinkBean = purchaserSupplierLinkProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), pPurchaserSupplierLinkBean);
        lFinalLinkBean = purchaserSupplierLinkDAO.findByPrimaryKey(pExecutionContext.getConnection(), pPurchaserSupplierLinkBean);
        if(lFinalLinkBean!=null && CommonAppConstants.Yes.Yes.equals(lFinalLinkBean.getInWorkFlow())){
        	if(lOldPurchaserSupplierLinkBean == null || !PurchaserSupplierLinkBean.ApprovalStatus.Returned.equals(lOldPurchaserSupplierLinkBean.getApprovalStatus()))
        			throw new CommonBusinessException("Link cannot be modified, because WorkFlow has already started.");
        }
        //try and find entity 
    	String lEntityToFind = null;
        if (!lAppEntityBean.isPurchaser()) {
        	lEntityToFind = pPurchaserSupplierLinkBean.getPurchaser();
        }
        else if (!lAppEntityBean.isSupplier()) {
        	lEntityToFind = pPurchaserSupplierLinkBean.getSupplier();
        }
        AppEntityBean lCounterEntity = TredsHelper.getInstance().getAppEntityBean(lEntityToFind);
        if(lCounterEntity == null ){
        	if (!"TEMPLATE".equals(lEntityToFind)) {
        		throw new CommonBusinessException("Entity with code " + lEntityToFind + " not found.");
        	}
        }
        //

        if (pNew 
        		|| (pUpload && lOldPurchaserSupplierLinkBean==null) 
        			|| (!pNew && lOldPurchaserSupplierLinkBean==null ) ) {
        	boolean lUpdate = false;
        	lUpdate = (lOldPurchaserSupplierLinkBean != null);
            if (!lAppEntityBean.isPurchaser()) {
            	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASER;
            }
            else if (!lAppEntityBean.isSupplier()) {
            	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIER;
            }
            validateCreditPeriod(pPurchaserSupplierLinkBean.getCreditPeriod(), CommonAppConstants.Yes.Yes, pPurchaserSupplierLinkBean.getExtendedCreditPeriod());
            validateCDandHaircut(lConnection, pPurchaserSupplierLinkBean.getPurchaser(), pPurchaserSupplierLinkBean.getCashDiscountPercent(), pPurchaserSupplierLinkBean.getHaircutPercent());
            validateRelation(lConnection,pPurchaserSupplierLinkBean);
            pPurchaserSupplierLinkBean.setApprovalStatus(ApprovalStatus.Draft);
            pPurchaserSupplierLinkBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
            purchaserSupplierLinkProvDAO.getBeanMeta().clearBean(pPurchaserSupplierLinkBean, lFieldGroup, null);
            if(pPurchaserSupplierLinkBean.getInstrumentCreation()==null){
            	pPurchaserSupplierLinkBean.setInstrumentCreation(InstrumentCreation.Both);
            }
            if(lUpdate){
                if (purchaserSupplierLinkProvDAO.update(lConnection, pPurchaserSupplierLinkBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASER) == 0)
                    throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }else{
                purchaserSupplierLinkProvDAO.insert(lConnection, pPurchaserSupplierLinkBean);
            }
            lReturnBean = pPurchaserSupplierLinkBean;
	        insertPurchaserSupplierLinkWorkFlow(lConnection, pPurchaserSupplierLinkBean, pUserBean, null, pPurchaserSupplierLinkBean.getRemarks());
        } else {
        	if ("TEMPLATE".equals(lEntityToFind)) {
        		throw new CommonBusinessException("Template Cannot be Modified.");
        	}
        	if(!PurchaserSupplierLinkBean.Status.Active.equals(lOldPurchaserSupplierLinkBean.getStatus())){
        		throw new CommonBusinessException("Link not Active.");
        	}
            if (lAppEntityBean.isPurchaser()) {
            	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASER;
            	if(lOldPurchaserSupplierLinkBean.getApprovalStatus().equals(PurchaserSupplierLinkBean.ApprovalStatus.Approved) ||
            			lOldPurchaserSupplierLinkBean.getApprovalStatus().equals(PurchaserSupplierLinkBean.ApprovalStatus.Deleted))
            		pPurchaserSupplierLinkBean.setApprovalStatus(ApprovalStatus.Draft);
            }else{
            	throw new Exception("Only Purchasers can modify Buyer Seller Link.");
            }
            //old status was not active and the new status is active  then only conclude that link activated
        	lLinkActivated = !PurchaserSupplierLinkBean.Status.Active.equals(lOldPurchaserSupplierLinkBean.getStatus()) && PurchaserSupplierLinkBean.Status.Active.equals(pPurchaserSupplierLinkBean.getStatus());

            validateCreditPeriod(pPurchaserSupplierLinkBean.getCreditPeriod(), CommonAppConstants.Yes.Yes, pPurchaserSupplierLinkBean.getExtendedCreditPeriod());
            validateCDandHaircut(lConnection, pPurchaserSupplierLinkBean.getPurchaser(), pPurchaserSupplierLinkBean.getCashDiscountPercent(), pPurchaserSupplierLinkBean.getHaircutPercent());
            validateRelation(lConnection,pPurchaserSupplierLinkBean);
            //
            PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = pPurchaserSupplierLinkBean;
            //put relevant values from old bean to the new bean
            if(pApiUser || (pHeaderFieldList!=null && lOldPurchaserSupplierLinkBean != null)){
            	purchaserSupplierLinkProvDAO.getBeanMeta().copyBean(pPurchaserSupplierLinkBean, 
        	            lOldPurchaserSupplierLinkBean, null, pHeaderFieldList);
            	lPurchaserSupplierLinkBean = lOldPurchaserSupplierLinkBean;
            	lPurchaserSupplierLinkBean.setApprovalStatus(pPurchaserSupplierLinkBean.getApprovalStatus());
            }
            //
            if (purchaserSupplierLinkProvDAO.update(lConnection, lPurchaserSupplierLinkBean, lFieldGroup) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            lReturnBean = lPurchaserSupplierLinkBean;
            //avoiding continuious changes in the draft by the purchaser ie. avoid loggin to workflow when Draft mode to Draft mode again
            if(!lOldPurchaserSupplierLinkBean.getApprovalStatus().equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
    	        insertPurchaserSupplierLinkWorkFlow(lConnection, lPurchaserSupplierLinkBean, pUserBean, null, lPurchaserSupplierLinkBean.getRemarks());
            }
        }
        
        if (pPurchaserSupplierLinkBean.getApprovalStatus() == PurchaserSupplierLinkBean.ApprovalStatus.Approved)
            copyToFinal(lConnection, pPurchaserSupplierLinkBean, pUserBean.getId());
        
        pExecutionContext.commit();
        if (lLinkActivated){
        	String[] lMsg = new String[] {"Re-Activated","re-activated"};
        	sendMail(lConnection, pPurchaserSupplierLinkBean, lAppEntityBean, lMsg);
        }
        pExecutionContext.dispose();
        return lReturnBean;
    }
    
    private void sendMail(Connection pConnection, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, AppEntityBean pAppEntityBean, String[] pMsgText) throws Exception{
        //Send mail if Link Activated
        String lCounterCode = null, lMemberCode = null, lMemberName=null;
        if (pAppEntityBean.isSupplier()) {
            lMemberCode = pPurchaserSupplierLinkBean.getSupplier();
            lMemberName = pPurchaserSupplierLinkBean.getSupName();
            lCounterCode = pPurchaserSupplierLinkBean.getPurchaser();
        }
        else if (pAppEntityBean.isPurchaser()) {
            lMemberCode = pPurchaserSupplierLinkBean.getPurchaser();
            lMemberName = pPurchaserSupplierLinkBean.getPurName();
            lCounterCode = pPurchaserSupplierLinkBean.getSupplier();
        }
    	List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lCounterCode, AppConstants.EMAIL_NOTIFY_TYPE_LINKACTIONS_1);
        if (lEmailIds != null) {
        	HashMap<String, Object> lDataValues = new HashMap<String, Object>();
        	lDataValues.put("memberCode", lMemberCode);
        	lDataValues.put("memberName", lMemberName);
        	lDataValues.put("subjectActionText", pMsgText[0]);
        	lDataValues.put("bodyActionText", pMsgText[1]);
        	lDataValues.put(EmailSender.TO, lEmailIds);
        	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BUYERSELLERLINKACTIONS, lDataValues);
        }
    }
    
    private void validateCreditPeriod(Long pCreditPeriod, CommonAppConstants.Yes pEnableExtension, Long pExtendeCreditPeriod ) throws CommonBusinessException{
        Long lMaxAllowedTotalCP = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_MAXALLOWEDTOTALCREDITPERIOD);
        Long lTotalCreditPeriod = pCreditPeriod!=null?pCreditPeriod:new Long(0);
        if(CommonAppConstants.Yes.Yes.equals(pEnableExtension) && pExtendeCreditPeriod != null )
        	lTotalCreditPeriod = new Long(pCreditPeriod.longValue() + pExtendeCreditPeriod.longValue() );
        if(lTotalCreditPeriod.longValue() > lMaxAllowedTotalCP.longValue()){
        	throw new CommonBusinessException("The total credit period exceeds the max allowed of " + lMaxAllowedTotalCP.longValue() +" days.");
        }        
    }

    private void validateCDandHaircut(Connection pConnection, String pPurchaserCode, BigDecimal pCashDiscount, BigDecimal pHaircut) throws CommonBusinessException, Exception{
    	if(pCashDiscount!=null){
    		//check platform level
            Double lTmpMaxCD = RegistryHelper.getInstance().getDouble(AppConstants.REGISTRY_MAXCASHDISCOUNTPERCENT);
            if(lTmpMaxCD!=null){
            	BigDecimal lMaxCashDiscount = BigDecimal.valueOf(lTmpMaxCD);
            	if(lMaxCashDiscount.compareTo(pCashDiscount) < 0){
                	throw new CommonBusinessException("Cash Discount for Purchaser should be less than Max Cash Discount " + lMaxCashDiscount + "  set at Platform Level.");
            	}
            }else{
            	throw new CommonBusinessException("Max Cash Discount not set at Platform Level.");
            }
            //check for purchaser at Registration Level
			CompanyDetailBean lCDBean = new CompanyDetailBean();
			lCDBean.setCode(pPurchaserCode);
			lCDBean = companyDetailDao.findBean(pConnection, lCDBean);
			if(lCDBean!=null){
				if(lCDBean.getCashDiscountPercent()!=null){
					if(lCDBean.getCashDiscountPercent().compareTo(pCashDiscount) < 0){
						throw new CommonBusinessException("Cash Discount cannot exceed " + lCDBean.getCashDiscountPercent()  + " set at Registration Level.");
					}
				}
			}else {
				throw new CommonBusinessException("Company Detail not found for " + pPurchaserCode);
			}
    	}
    	if(pHaircut!=null){
    		
    	}
    }
    
    public void validateRelation(Connection pConnection, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean) throws Exception{
    	PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
		lFilterBean.setPurchaser(pPurchaserSupplierLinkBean.getPurchaser());
		lFilterBean.setSupplier(pPurchaserSupplierLinkBean.getSupplier());
		PurchaserSupplierRelationshipHistoryBean lLastPSRHBean = getLastRelationship(pConnection, pPurchaserSupplierLinkBean.getPurchaser(), pPurchaserSupplierLinkBean.getSupplier());
		if(lLastPSRHBean != null){
	    	if(pPurchaserSupplierLinkBean.getRelationFlag() == null && lLastPSRHBean.getRelationFlag() != null){
	    		throw new CommonBusinessException(" Relation flag is mandatory.");
	    	}else{
	        	if(pPurchaserSupplierLinkBean.getRelationEffectiveDate() == null){
	    			throw new CommonBusinessException("Effective Date is mandatory.");
	    		}
	        	if(YesNo.Yes.equals(pPurchaserSupplierLinkBean.getRelationFlag())){
	    			if(StringUtils.isEmpty(pPurchaserSupplierLinkBean.getRelationDoc())){
	    				throw new CommonBusinessException("Relationship document is mandatory.");
	    			}
	    			if(pPurchaserSupplierLinkBean.getRelationEffectiveDate().after(OtherResourceCache.getInstance().getCurrentDate())){
	    				throw new CommonBusinessException("Effective date cannot be after current date.");
	    			}
	        	}else{
	            	if(YesNo.No.equals(pPurchaserSupplierLinkBean.getRelationFlag())){
            			if(pPurchaserSupplierLinkBean.getRelationEffectiveDate().before(lLastPSRHBean.getStartDate())){
            				throw new CommonBusinessException("Effective date cannot be before last effective date.");
            			}
	            	}
	        	}
	    	}
		}
    }

    public void updateModifyCode(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, 
            IAppUserBean pUserBean) throws Exception {
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), pPurchaserSupplierLinkBean);
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBeanMain = purchaserSupplierLinkDAO.findByPrimaryKey(pExecutionContext.getConnection(), pPurchaserSupplierLinkBean);
        if(!(lPurchaserSupplierLinkBean.getStatus().equals(PurchaserSupplierLinkBean.Status.Active)) && !(lPurchaserSupplierLinkBean.getApprovalStatus().equals(PurchaserSupplierLinkBean.ApprovalStatus.Approved))){
    	   throw new CommonBusinessException("Cannot Modify the value since workflow has already started.");  
        }
        else if((lPurchaserSupplierLinkBeanMain.getStatus().equals(PurchaserSupplierLinkBean.Status.Suspended_by_Buyer))){
            throw new CommonBusinessException("Cannot Modify the value its already Suspended ");
   		}
        else if((lPurchaserSupplierLinkBeanMain.getStatus().equals(PurchaserSupplierLinkBean.Status.Suspended_by_Seller))){
            throw new CommonBusinessException("Cannot Modify the value its already Suspended ");
   		}else if((lPurchaserSupplierLinkBeanMain.getStatus().equals(PurchaserSupplierLinkBean.Status.Suspended_by_Platform))){
            throw new CommonBusinessException("Cannot Modify the value its already Suspended by Platform.");
   		}
        Connection lConnection = pExecutionContext.getConnection();
        String lFieldGroup = null;
        if(lPurchaserSupplierLinkBeanMain.getSupplier().equals(pUserBean.getDomain())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIREREFCODE;
        }else if(lPurchaserSupplierLinkBeanMain.getPurchaser().equals(pUserBean.getDomain())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASEREFCODE;
        }
        purchaserSupplierLinkDAO.update(lConnection, pPurchaserSupplierLinkBean,lFieldGroup);
        purchaserSupplierLinkProvDAO.update(lConnection, pPurchaserSupplierLinkBean,lFieldGroup);
        purchaserSupplierLinkDAO.insertAudit(lConnection, lPurchaserSupplierLinkBeanMain, GenericDAO.AuditAction.Update, pUserBean.getId());
    }
    
    public void updateApprovalStatus(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, 
            IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        PurchaserSupplierLinkBean lFinalBean = null;
        PurchaserSupplierLinkBean.ApprovalStatus lOldApprovalStatus = null, lNewApprovalStatus = null;
        PurchaserSupplierLinkBean.Status lOldStatus = null;
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierLinkBean lOldPurchaserSupplierLinkBean = findBean(pExecutionContext, pPurchaserSupplierLinkBean);
        
        if ((pPurchaserSupplierLinkBean.getPurchaser() != null) && (pPurchaserSupplierLinkBean.getSupplier() != null) &&
		!(pPurchaserSupplierLinkBean.getPurchaser().equals(lOldPurchaserSupplierLinkBean.getPurchaser()) ||
		!(pPurchaserSupplierLinkBean.getSupplier().equals(lOldPurchaserSupplierLinkBean.getSupplier())))) 
            throw new CommonBusinessException("Purchaser Supplier Link mismatch.");
        
        lNewApprovalStatus = pPurchaserSupplierLinkBean.getApprovalStatus();
        if(lOldPurchaserSupplierLinkBean!=null){
        	lOldApprovalStatus = lOldPurchaserSupplierLinkBean.getApprovalStatus();
        	lOldStatus = lOldPurchaserSupplierLinkBean.getStatus();
        }
        //original table bean
    	lFinalBean = purchaserSupplierLinkDAO.findByPrimaryKey(lConnection, lOldPurchaserSupplierLinkBean);

        // access check
        switch (lNewApprovalStatus) {
        case Submitted:
        	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        		//purchaser can submit the draft to supplier for approval
                if (!(PurchaserSupplierLinkBean.ApprovalStatus.Draft.equals(lOldApprovalStatus) ||
                		PurchaserSupplierLinkBean.ApprovalStatus.Returned.equals(lOldApprovalStatus) ||
                		PurchaserSupplierLinkBean.ApprovalStatus.Approved.equals(lOldApprovalStatus)))
                    throw new CommonBusinessException("Cannot perform action. Record is not in Draft or Returned or Approved state.");
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        		throw new CommonBusinessException("Supplier cannot submit link.");
        	}
        	break;
        case Deleted:
        	//TODO: not used currently
        	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        	}
            break;
        case Approved:
        	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        		throw new CommonBusinessException("Only Seller can perform this action.");
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        		if(!(lOldPurchaserSupplierLinkBean != null && 
        				PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lOldApprovalStatus)))
        			throw new CommonBusinessException("Cannot perfrom action. Record not in Submit state.");
        	}
        case Returned:
        	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        		throw new CommonBusinessException("Only Seller can perform this action.");
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        		if(!(lOldPurchaserSupplierLinkBean != null && 
        				PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lOldApprovalStatus)))
        			throw new CommonBusinessException("Cannot perfrom action. Record not in Submit state.");
        	}
        	break;
        case Withdraw:
        	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        		if(!(PurchaserSupplierLinkBean.ApprovalStatus.Draft.equals(lOldApprovalStatus) ||
        				PurchaserSupplierLinkBean.ApprovalStatus.Returned.equals(lOldApprovalStatus)))
        			throw new CommonBusinessException("Cannot perfrom action. Record not in Submit state.");
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        		throw new CommonBusinessException("Supplier cannot perform this action.");
        	}
            break;
        case Suspended:
        	if(!PurchaserSupplierLinkBean.Status.Active.equals(lOldStatus))
        		throw new CommonBusinessException("Link already suspended.");
        	//bean cannot be suspended if the link is not yet created (just initiated by buyer) 
            if(lFinalBean==null)
            	throw new CommonBusinessException("Link cannot be suspended. It is in Draft mode.");
            else if (lFinalBean != null && CommonAppConstants.Yes.Yes.equals(lFinalBean.getInWorkFlow()))
            	throw new CommonBusinessException("Link cannot be suspended. WorkFlow has been initiated.");
            break;
        case ReActivate:
        	if(!PurchaserSupplierLinkBean.Status.Active.equals(lOldStatus)){
            	if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
            			if(!PurchaserSupplierLinkBean.Status.Suspended_by_Buyer.equals(lOldPurchaserSupplierLinkBean.getStatus()))
            				throw new CommonBusinessException("Link suspended by Seller.");
            	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        			if(!PurchaserSupplierLinkBean.Status.Suspended_by_Seller.equals(lOldPurchaserSupplierLinkBean.getStatus()))
        				throw new CommonBusinessException("Link suspended by Purchaser.");
            	}
        	}
            break;
        default:
        	throw new CommonBusinessException("Invalid status code [" +(lNewApprovalStatus!=null?lNewApprovalStatus:"")+ "] recived.");
        }
        //TODO:
        String lFieldGroup = null;

        if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS;
        }else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIERAPPROVALSTATUS;
        }
	    purchaserSupplierLinkDAO.getBeanMeta().copyBean(pPurchaserSupplierLinkBean, 
	            lOldPurchaserSupplierLinkBean, lFieldGroup, null);

	    if(PurchaserSupplierLinkBean.ApprovalStatus.Suspended.equals(lNewApprovalStatus)){
    		PurchaserSupplierLinkBean.Status lNewStatus = null;
    		if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getPurchaser())){
            	lNewStatus = PurchaserSupplierLinkBean.Status.Suspended_by_Buyer;
        	}else if(pUserBean.getDomain().equals(pPurchaserSupplierLinkBean.getSupplier())){
        		lNewStatus = PurchaserSupplierLinkBean.Status.Suspended_by_Seller;
        	}
        	lOldPurchaserSupplierLinkBean.setStatus(lNewStatus);
	        purchaserSupplierLinkProvDAO.update(lConnection, lOldPurchaserSupplierLinkBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATESTATUS);
	        insertPurchaserSupplierLinkWorkFlow(lConnection, lOldPurchaserSupplierLinkBean, pUserBean, null, lOldPurchaserSupplierLinkBean.getRemarks());
        	//update the actual bean to 
            if (lFinalBean != null){
        		lFinalBean.setStatus(lNewStatus);
            	lFinalBean.setRemarks(pPurchaserSupplierLinkBean.getRemarks());
                purchaserSupplierLinkDAO.update(lConnection, lFinalBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATESTATUS);
                purchaserSupplierLinkDAO.insertAudit(lConnection, lFinalBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            }
	        
    	}else if(PurchaserSupplierLinkBean.ApprovalStatus.ReActivate.equals(lNewApprovalStatus)){
        	lOldPurchaserSupplierLinkBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
	        purchaserSupplierLinkProvDAO.update(lConnection, lOldPurchaserSupplierLinkBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATESTATUS);
	        insertPurchaserSupplierLinkWorkFlow(lConnection, lOldPurchaserSupplierLinkBean, pUserBean, null, lOldPurchaserSupplierLinkBean.getRemarks());
	        //
        	//update the actual bean to 
            if (lFinalBean != null){
        		lFinalBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
            	lFinalBean.setRemarks(pPurchaserSupplierLinkBean.getRemarks());
                purchaserSupplierLinkDAO.update(lConnection, lFinalBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATESTATUS);
                purchaserSupplierLinkDAO.insertAudit(lConnection, lFinalBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            }
    	}else if(PurchaserSupplierLinkBean.ApprovalStatus.Withdraw.equals(lNewApprovalStatus)){
	        insertPurchaserSupplierLinkWorkFlow(lConnection, lOldPurchaserSupplierLinkBean, pUserBean, null, lOldPurchaserSupplierLinkBean.getRemarks());
        	if(lFinalBean != null) {
        		copyToProvisional(lConnection, lFinalBean); //if it is first time creation, there will be no final bean
        	}else{
        		//mark the provisional as deleted
        		lOldPurchaserSupplierLinkBean.setApprovalStatus(ApprovalStatus.Deleted);
    	        purchaserSupplierLinkProvDAO.update(lConnection, lOldPurchaserSupplierLinkBean, PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS);
        	}
    	}else{
	        purchaserSupplierLinkProvDAO.update(lConnection, lOldPurchaserSupplierLinkBean, lFieldGroup);
	        insertPurchaserSupplierLinkWorkFlow(lConnection, lOldPurchaserSupplierLinkBean, pUserBean, null, lOldPurchaserSupplierLinkBean.getRemarks());
		    if(ApprovalStatus.Submitted.equals(lNewApprovalStatus)){
	        	if(lFinalBean!=null){
			    	lFinalBean.setInWorkFlow(CommonAppConstants.Yes.Yes);
			    	List<String> lFieldList = new ArrayList<String>();
			    	lFieldList.add("inWorkFlow");
			        purchaserSupplierLinkDAO.update(lConnection, lFinalBean, lFieldList);
			        purchaserSupplierLinkDAO.insertAudit(lConnection, lFinalBean, GenericDAO.AuditAction.Update, pUserBean.getId());
	        	}
		    }
    	}
        if (PurchaserSupplierLinkBean.ApprovalStatus.Approved.equals(lNewApprovalStatus)) {
        	lOldPurchaserSupplierLinkBean.setInWorkFlow(null);
            copyToFinal(lConnection, lOldPurchaserSupplierLinkBean, pUserBean.getId());
        }else if (PurchaserSupplierLinkBean.ApprovalStatus.Withdraw.equals(lNewApprovalStatus)) {
        	if(lFinalBean!= null){
            	lFinalBean.setInWorkFlow(null);
    	    	List<String> lFieldList = new ArrayList<String>();
    	    	lFieldList.add("inWorkFlow");
    	        purchaserSupplierLinkDAO.update(lConnection, lFinalBean, lFieldList);
    	        purchaserSupplierLinkDAO.insertAudit(lConnection, lFinalBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        	}
        }
        //sending of mail on new approval status
        boolean lSendMail = false;
        String[] lMsg = null;
        if(!lNewApprovalStatus.equals(lOldApprovalStatus)){
            switch (lNewApprovalStatus) {
                case Submitted:
                	lSendMail = true;
                	if(lFinalBean == null)
                        lMsg = new String[] { "Initiated", "initiated" };
                	else 
                        lMsg = new String[] { "Modified", "modified" };
                	break;
                case Deleted:
                	//TODO: not used currently
                    break;
                case Approved:
                	lSendMail = true;
                    lMsg = new String[] { "Approved", "approved" };
                	break;
                case Returned:
                	lSendMail = true;
                    lMsg = new String[] { "Modification Request", "requested modification of" };
                	break;
                case Withdraw:
                	//no mail as decided
                    break;
                case Suspended:
                	lSendMail = true;
                    lMsg = new String[] { "Suspension", "supspended" };
                    break;
                case ReActivate:
                	lSendMail = true;
                    lMsg = new String[] { "ReActivation", "reactivated" };
                    break;
                }
        }
        if(lSendMail){
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        	sendMail(lConnection, pPurchaserSupplierLinkBean, lAppEntityBean, lMsg);
        }
        pExecutionContext.commit();
    }
    
    private void copyToFinal(Connection pConnection, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, Long pUserId) throws Exception {
        PurchaserSupplierLinkBean lOldPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findByPrimaryKey(pConnection, pPurchaserSupplierLinkBean);
        if (lOldPurchaserSupplierLinkBean == null) {
            purchaserSupplierLinkDAO.insert(pConnection, pPurchaserSupplierLinkBean);
	        purchaserSupplierLinkDAO.insertAudit(pConnection, pPurchaserSupplierLinkBean, GenericDAO.AuditAction.Insert, pUserId);
        } else {
            purchaserSupplierLinkDAO.update(pConnection, pPurchaserSupplierLinkBean, BeanMeta.FIELDGROUP_UPDATE);
	        purchaserSupplierLinkDAO.insertAudit(pConnection, pPurchaserSupplierLinkBean, GenericDAO.AuditAction.Update, pUserId);
        }
        //COPY THE VALUES OF RELATIONSHIP FROM PSLBEAN INTO THE RELATION TABLE
        if(pPurchaserSupplierLinkBean.getRelationFlag()!=null){
			PurchaserSupplierRelationshipHistoryBean lPurSupHistoryBean = null;
        	if(pPurchaserSupplierLinkBean.getRelationEffectiveDate()!=null){
    			lPurSupHistoryBean = new PurchaserSupplierRelationshipHistoryBean();
				lPurSupHistoryBean.setPurchaser(pPurchaserSupplierLinkBean.getPurchaser());
				lPurSupHistoryBean.setSupplier(pPurchaserSupplierLinkBean.getSupplier());
				lPurSupHistoryBean.setRelationDocName(pPurchaserSupplierLinkBean.getRelationDoc());
				lPurSupHistoryBean.setRelationFlag(pPurchaserSupplierLinkBean.getRelationFlag());
				lPurSupHistoryBean.setStartDate(pPurchaserSupplierLinkBean.getRelationEffectiveDate());
				lPurSupHistoryBean.setRecordCreator(pUserId);
				lPurSupHistoryBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
        	}
        	if(lPurSupHistoryBean!=null){
            	PurchaserSupplierRelationshipHistoryBean lLastPSRHBean = getLastRelationship(pConnection, pPurchaserSupplierLinkBean.getPurchaser(), pPurchaserSupplierLinkBean.getSupplier());
            	if(lLastPSRHBean!=null){
            		if(!lLastPSRHBean.getRelationShipAsString().equals(lPurSupHistoryBean.getRelationShipAsString())){
            			purSupRelHistoryDAO.insert(pConnection, lPurSupHistoryBean);
            		}
            	}else{
        			purSupRelHistoryDAO.insert(pConnection, lPurSupHistoryBean);
            	}
        	}
        }
   }
    
    private PurchaserSupplierRelationshipHistoryBean getLastRelationship(Connection pConnection, String pPurchaser, String pSupplier) throws Exception {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM PURSUPRELATIONSHIPHISTORY WHERE PSRRECORDVERSION > 0 ");
    	lSql.append(" AND psrid = ( SELECT max(psrid) FROM PURSUPRELATIONSHIPHISTORY  WHERE PSRRECORDVERSION > 0  ");
    	lSql.append(" AND PSRPURCHASER = ").append(DBHelper.getInstance().formatString(pPurchaser));
    	lSql.append(" AND  PSRSUPPLIER = ").append(DBHelper.getInstance().formatString(pSupplier));
    	lSql.append(" ) ");
    	List<PurchaserSupplierRelationshipHistoryBean> lPSRHBeans = purSupRelHistoryDAO.findListFromSql(pConnection, lSql.toString(), 0);
    	if(lPSRHBeans!=null && !lPSRHBeans.isEmpty()){
    		return lPSRHBeans.get(0);
    	}
    	return null;
    }
    
    private void copyToProvisional(Connection pConnection, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean) throws Exception {
        PurchaserSupplierLinkBean lOldPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findByPrimaryKey(pConnection, pPurchaserSupplierLinkBean);
        if (lOldPurchaserSupplierLinkBean == null) {
            purchaserSupplierLinkProvDAO.insert(pConnection, pPurchaserSupplierLinkBean);
        } else {
        	purchaserSupplierLinkProvDAO.update(pConnection, pPurchaserSupplierLinkBean, BeanMeta.FIELDGROUP_UPDATE);
        }
    }
    
    
    public PurchaserSupplierLinkWorkFlowBean insertPurchaserSupplierLinkWorkFlow(Connection pConnection, PurchaserSupplierLinkBean pPurchaserSupplierLinkBean, 
            IAppUserBean pUserBean, String pAutoAcceptingEntity, String pRemarks) throws Exception {
        PurchaserSupplierLinkWorkFlowBean lWorkFlowBean = new PurchaserSupplierLinkWorkFlowBean();
        lWorkFlowBean.setPurchaser(pPurchaserSupplierLinkBean.getPurchaser());
        lWorkFlowBean.setSupplier(pPurchaserSupplierLinkBean.getSupplier());
        if(pUserBean!=null){
        	AppUserBean lAppUserBean = TredsHelper.getInstance().getAppUser(pUserBean.getId());
            lWorkFlowBean.setEntity(lAppUserBean.getDomain());
            lWorkFlowBean.setAuId(lAppUserBean.getId());
        }else{
            //this is case when the there is auto acceptance by the counter entity through the Auction Pref settings
            lWorkFlowBean.setEntity(pAutoAcceptingEntity);
            lWorkFlowBean.setAuId(new Long(0));
        }
        lWorkFlowBean.setStatus(pPurchaserSupplierLinkBean.getApprovalStatus());
        lWorkFlowBean.setStatusRemarks(pRemarks);
        lWorkFlowBean.setStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
        purchaserSupplierLinkWorkFlowDAO.insert(pConnection, lWorkFlowBean);
        return lWorkFlowBean;
    }
    
    public void sendReminder(Connection pConnection, PurchaserSupplierLinkBean pPSLinkFilterBean, AppUserBean pUserBean) throws Exception{
        //Send mail if Link Activated
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        if ((lAppEntityBean == null) || (!lAppEntityBean.isPurchaser() && !lAppEntityBean.isSupplier()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = null;
        lPurchaserSupplierLinkBean = purchaserSupplierLinkProvDAO.findByPrimaryKey(pConnection, pPSLinkFilterBean);
        if (!lPurchaserSupplierLinkBean.getPurchaser().equals(pUserBean.getDomain()) && 
                !lPurchaserSupplierLinkBean.getSupplier().equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
         
        lPurchaserSupplierLinkBean.populateDatabaseFields();
        
        String[] lMsg = new String[] { "Approval Reminder" , "sent reminder for approval" };
        sendMail(pConnection, lPurchaserSupplierLinkBean, lAppEntityBean, lMsg);
        
    }
    
	public void updatePlatformStatus(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pPSLBean, AppUserBean pUserBean) throws Exception {
		if(pPSLBean!=null){
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
			if(!lAppEntityBean.isPlatform()){
	        	throw new CommonBusinessException("Only platform is allowed to Activate/Suspend.");
			}
			PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
			lFilterBean.setPurchaser(pPSLBean.getPurchaser());
			lFilterBean.setSupplier(pPSLBean.getSupplier());
			PurchaserSupplierLinkBean lPSLBean = purchaserSupplierLinkDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
			PurchaserSupplierLinkBean lPSLProvBean = purchaserSupplierLinkProvDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
			
			if(lPSLProvBean == null)
				throw new CommonBusinessException("No relation found.");

			if(lPSLProvBean != null){
				validatePlatformStatus(lPSLProvBean, pPSLBean);
				//
				//changing the provisional also, so that when the same comes into active state, the state provided by the Platform is maintained.
				//
				lPSLBean.setPlatformStatus(pPSLBean.getPlatformStatus());
				lPSLBean.setPlatformReasonCode(pPSLBean.getPlatformReasonCode());
				lPSLBean.setPlatformRemarks(pPSLBean.getPlatformRemarks());
				//
				lPSLProvBean.setPlatformStatus(pPSLBean.getPlatformStatus());
				lPSLProvBean.setPlatformReasonCode(pPSLBean.getPlatformReasonCode());
				lPSLProvBean.setPlatformRemarks(pPSLBean.getPlatformRemarks());
				//
				purchaserSupplierLinkDAO.update(pExecutionContext.getConnection(), lPSLBean,PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPLATFORMSTATUS);
				purchaserSupplierLinkProvDAO.update(pExecutionContext.getConnection(), lPSLProvBean,PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPLATFORMSTATUS);
				//workflow will always be there if the treds/platform activates/supsends relationship
//    	        insertPurchaserSupplierLinkWorkFlow(pExecutionContext.getConnection(), lPSLProvBean, pUserBean, null, lPSLProvBean.getRemarks());
			}
		}
	}
	
	private void validatePlatformStatus(PurchaserSupplierLinkBean pPSLOriginalProvBean, PurchaserSupplierLinkBean pPSLNewUpdatedBean) throws Exception{
		if(pPSLOriginalProvBean.getRelationFlag()==null){
			//TODO: As business if No Relationship then also we can Activate/Deactivate.
			if(PlatformStatus.Active.equals(pPSLNewUpdatedBean.getPlatformStatus())){
				throw new CommonBusinessException("No relationship defined, cannot Activate platform status.");
			}else if(PlatformStatus.Suspended.equals(pPSLNewUpdatedBean.getPlatformStatus())){
				throw new CommonBusinessException("No relationship defined. cannot Deactivate platform status.");
			}
			throw new CommonBusinessException("No relationship defined.");
		}else{
			if(pPSLOriginalProvBean.getPlatformStatus()!=null){
				if(pPSLOriginalProvBean.getPlatformStatus().equals(pPSLNewUpdatedBean.getPlatformStatus())){
					throw new CommonBusinessException("Cannot change status since already "+ pPSLNewUpdatedBean.getPlatformStatus().toString());
				}
			}
		}
	}

	public boolean hasRelationChanged(PurchaserSupplierLinkBean pPSLBean, PurchaserSupplierLinkBean pPSLProvBean) throws Exception{
		if(pPSLBean!=null && pPSLProvBean != null){
			if(pPSLBean.getRelationShipAsString().equals(pPSLProvBean.getRelationShipAsString())){
				return false;
			}
		}
		return true;
	}
	
	public List<PurchaserSupplierLinkBean> findAuthorizePurchaser(Connection pConnection, PurchaserSupplierLinkBean pFilterBean, IAppUserBean pUserBean) throws Exception{
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT DISTINCT(PSLPURCHASER) FROM PURCHASERSUPPLIERLINKS_P ");
		lSql.append(" WHERE ");
//		lSql.append(" PSLAUTHORIZERXIL = ").append(DBHelper.getInstance().formatString(CommonAppConstants.YesNo.Yes.getCode()));
//		lSql.append(" AND  ");
		lSql.append(" PSLSUPPLIER = 'TEMPLATE' ");
		List<PurchaserSupplierLinkBean> lPSLBeanList = purchaserSupplierLinkDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lPSLBeanList;
	}
	
	public String findSuppliers(ExecutionContext pExecutionContext, PurchaserSupplierLinkBean pFilterBean, IAppUserBean pUserBean) throws Exception{
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM ( ");
		lSql.append(" SELECT DISTINCT(PSLSUPPLIER) FROM PURCHASERSUPPLIERLINKS_P ");
		lSql.append(" WHERE ");
		lSql.append(" PSLPURCHASER = ").append(DBHelper.getInstance().formatString(pFilterBean.getPurchaser()));
		lSql.append(" UNION ");
		lSql.append(" SELECT DISTINCT(PSLSUPPLIER) FROM PURCHASERSUPPLIERLINKS ");
		lSql.append(" WHERE ");
		lSql.append(" PSLPURCHASER = ").append(DBHelper.getInstance().formatString(pFilterBean.getPurchaser()));
		lSql.append(" ) ");
		List<PurchaserSupplierLinkBean> lPSLBeanList = purchaserSupplierLinkDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
		List<String> lSuppliers = new ArrayList<String>();
		for (PurchaserSupplierLinkBean lTmpBean : lPSLBeanList) {
			lSuppliers.add(lTmpBean.getSupplier());
		}
		AppEntityBO lAppEntityBO = new AppEntityBO();
		List<AppEntityBean> lAppEntityList = lAppEntityBO.findList(pExecutionContext, EntityType.Supplier, pUserBean, false);
		List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
		for (AppEntityBean lAppEntityBean : lAppEntityList) {
			if (lSuppliers.contains(lAppEntityBean.getCode())) {
				continue;
			}
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lAppEntityBean.getCode());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lAppEntityBean.getName());
            lResults.add(lData);
        }
		return new JsonBuilder(lResults).toString();

	}

	public void findSupplierList(Connection pConnection, Map<String, Object> pMap,
			IAppUserBean lUserBean) throws Exception {
		List<String> lList = new ArrayList<String>();
		String lPurchaser = null;
		if(pMap.containsKey("purchaser") && pMap.get("purchaser")!= null){
			lPurchaser = pMap.get("purchaser").toString();
		}else{
			throw new CommonBusinessException("Please select the Purchaser.");
		}
		String lAdmin = null;
		if(pMap != null){
			if(pMap.containsKey("supplier") && pMap.get("supplier")!= null){
				lList = (List<String>) pMap.get("supplier");
			}else{
				throw new CommonBusinessException("Please select the Supplier.");
			}
			for(String lSupplier: lList){
				PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
				lPSLFilterBean.setSupplier(lSupplier);
				lPSLFilterBean.setPurchaser(lPurchaser);
				PurchaserSupplierLinkBean lPSLBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
				PurchaserSupplierLinkBean lPSLProvBean = purchaserSupplierLinkProvDAO.findBean(pConnection, lPSLFilterBean);
				if(PurchaserSupplierLinkBean.Status.Suspended_by_Buyer.equals(lPSLProvBean.getStatus())){
					throw new CommonBusinessException("Purchasser Supplier link is suspended by "+lPSLProvBean.getPurName());
				}else if(PurchaserSupplierLinkBean.Status.Suspended_by_Seller.equals(lPSLProvBean.getStatus())){
					throw new CommonBusinessException("Purchasser Supplier link is suspended by "+lPSLProvBean.getSupName());
				}else if(PurchaserSupplierLinkBean.Status.Suspended_by_Platform.equals(lPSLProvBean.getStatus())){
					throw new CommonBusinessException("Purchasser Supplier link is suspended by Admin for "+ lPSLProvBean.getPurchaser() +" and "+ lPSLProvBean.getSupplier());
				}
				if(lPSLProvBean != null){
					if(pMap.containsKey("enableCdtPer") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableCdtPer").toString())){
						if(pMap.get("creditPeriod")!= null){
							lPSLProvBean.setCreditPeriod(new Long(pMap.get("creditPeriod").toString()));
						}
					}
					if(pMap.containsKey("enableExtCdtPer") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableExtCdtPer").toString())){
						if(pMap.get("extendedCreditPeriod")!= null){
							lPSLProvBean.setExtendedCreditPeriod(new Long(pMap.get("extendedCreditPeriod").toString()));
						}
					}
					if(pMap.containsKey("enableSendAuc") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableSendAuc").toString())){
						if(pMap.get("autoConvert")!= null){
							if(AutoConvert.Auto.getCode().equals(pMap.get("autoConvert").toString())){
								lPSLProvBean.setAutoConvert(AutoConvert.Auto);
							}else if(AutoConvert.Purchaser.getCode().equals(pMap.get("autoConvert").toString())){
								lPSLProvBean.setAutoConvert(AutoConvert.Purchaser);
							}else if(AutoConvert.Supplier.getCode().equals(pMap.get("autoConvert").toString())){
								lPSLProvBean.setAutoConvert(AutoConvert.Supplier);
							}
						}
					}
					if(pMap.containsKey("enableInvMandatory") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableInvMandatory").toString())){
						if(pMap.get("invoiceMandatory")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("invoiceMandatory").toString())){
								lPSLProvBean.setInvoiceMandatory(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("invoiceMandatory").toString())){
								lPSLProvBean.setInvoiceMandatory(CommonAppConstants.YesNo.No);
							}
						}
					}
					if(pMap.containsKey("enableAutoAccept") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableAutoAccept").toString())){
						if(pMap.get("autoAccept")!= null){
							if(AutoAcceptBid.CutOffTime.getCode().equals(pMap.get("autoAccept").toString())){
								lPSLProvBean.setAutoAccept(AutoAcceptBid.CutOffTime);
							}else if(AutoAcceptBid.OnRecepitOfBid.getCode().equals(pMap.get("autoAccept").toString())){
								lPSLProvBean.setAutoAccept(AutoAcceptBid.OnRecepitOfBid);
							}else if(AutoAcceptBid.Disabled.getCode().equals(pMap.get("autoAccept").toString())){
								lPSLProvBean.setAutoAccept(AutoAcceptBid.Disabled);
							}
						}
					}
					if(pMap.containsKey("enableAutoAcceptBidType") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableAutoAcceptBidType").toString())){
						if(pMap.get("autoAcceptableBidTypes")!= null){
							if(AutoAcceptableBidTypes.AllBids.getCode().equals(pMap.get("autoAcceptableBidTypes").toString())){
								lPSLProvBean.setAutoAcceptableBidTypes(AutoAcceptableBidTypes.AllBids);
							}else if(AutoAcceptableBidTypes.OpenBids.getCode().equals(pMap.get("autoAcceptableBidTypes").toString())){
								lPSLProvBean.setAutoAcceptableBidTypes(AutoAcceptableBidTypes.OpenBids);
							}else if(AutoAcceptBid.Disabled.getCode().equals(pMap.get("autoAcceptableBidTypes").toString())){
								lPSLProvBean.setAutoAccept(AutoAcceptBid.Disabled);
							}
						}
					}
					if(pMap.containsKey("enablePurAutoAppInv") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enablePurAutoAppInv").toString())){
						if(pMap.get("purchaserAutoApproveInvoice")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("purchaserAutoApproveInvoice").toString())){
								lPSLProvBean.setPurchaserAutoApproveInvoice(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("purchaserAutoApproveInvoice").toString())){
								lPSLProvBean.setPurchaserAutoApproveInvoice(CommonAppConstants.YesNo.No);
							}
						}
					}
					if(pMap.containsKey("enableSelAutoAppInv") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableSelAutoAppInv").toString())){
						if(pMap.get("sellerAutoApproveInvoice")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("sellerAutoApproveInvoice").toString())){
								lPSLProvBean.setPurchaserAutoApproveInvoice(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("sellerAutoApproveInvoice").toString())){
								lPSLProvBean.setPurchaserAutoApproveInvoice(CommonAppConstants.YesNo.No);
							}
						}
						
					}
					if(pMap.containsKey("enableChargeBearer") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableChargeBearer").toString())){
						if(pMap.get("chargeBearer")!= null){
							if(AppConstants.CostBearer.Buyer.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setChargeBearer(AppConstants.CostBearingType.Buyer);
							}else if(AppConstants.CostBearer.Seller.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setChargeBearer(AppConstants.CostBearingType.Seller);
							}
						}
						if(pMap.get("chargeBearer")!= null){
							if(AppConstants.CostBearingType.Buyer.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Buyer);
							}else if(AppConstants.CostBearingType.Seller.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Seller);
							}else if(AppConstants.CostBearingType.Percentage_Split.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Percentage_Split);
							}else if(AppConstants.CostBearingType.Periodical_Split.getCode().equals(pMap.get("chargeBearer").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Periodical_Split);
							}
						}
					}
					if(pMap.containsKey("enableCostBearingType") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableCostBearingType").toString())){
						if(pMap.get("costBearingType")!= null){
							if(AppConstants.CostBearingType.Buyer.getCode().equals(pMap.get("costBearingType").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Buyer);
							}else if(AppConstants.CostBearingType.Seller.getCode().equals(pMap.get("costBearingType").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Seller);
							}else if(AppConstants.CostBearingType.Percentage_Split.getCode().equals(pMap.get("costBearingType").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Percentage_Split);
							}else if(AppConstants.CostBearingType.Periodical_Split.getCode().equals(pMap.get("costBearingType").toString())){
								lPSLProvBean.setCostBearingType(AppConstants.CostBearingType.Periodical_Split);
							}
						}
						if(pMap.get("splittingPoint")!= null){
							if(PurchaserSupplierLinkBean.SplittingPoint.Statutory_Due_Date.getCode().equals(pMap.get("splittingPoint").toString())){
								lPSLProvBean.setSplittingPoint(PurchaserSupplierLinkBean.SplittingPoint.Statutory_Due_Date);
							}else if(PurchaserSupplierLinkBean.SplittingPoint.Invoice_Due_Date.getCode().equals(pMap.get("splittingPoint").toString())){
								lPSLProvBean.setSplittingPoint(PurchaserSupplierLinkBean.SplittingPoint.Invoice_Due_Date);
							}
						}
						if(pMap.get("preSplittingCostBearer")!= null){
							if(AppConstants.CostBearer.Buyer.getCode().equals(pMap.get("preSplittingCostBearer").toString())){
								lPSLProvBean.setPreSplittingCostBearer(AppConstants.CostBearer.Buyer);
							}else if(AppConstants.CostBearer.Seller.getCode().equals(pMap.get("preSplittingCostBearer").toString())){
								lPSLProvBean.setPreSplittingCostBearer(AppConstants.CostBearer.Seller);
							}
						}
						if(pMap.get("postSplittingCostBearer")!= null){
							if(AppConstants.CostBearer.Buyer.getCode().equals(pMap.get("postSplittingCostBearer").toString())){
								lPSLProvBean.setPreSplittingCostBearer(AppConstants.CostBearer.Buyer);
							}else if(AppConstants.CostBearer.Seller.getCode().equals(pMap.get("postSplittingCostBearer").toString())){
								lPSLProvBean.setPreSplittingCostBearer(AppConstants.CostBearer.Seller);
							}
						}
						if(pMap.get("buyerPercent")!= null){
							lPSLProvBean.setBuyerPercent(new BigDecimal(pMap.get("buyerPercent").toString()));
						}
						if(pMap.get("sellerPercent")!= null){
							lPSLProvBean.setBuyerPercent(new BigDecimal(pMap.get("sellerPercent").toString()));
						}
					}
					if(pMap.containsKey("enableBidAcceptingEntityType") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableBidAcceptingEntityType").toString())){
						if(AppConstants.CostBearer.Buyer.getCode().equals(pMap.get("bidAcceptingEntityType").toString())){
							lPSLProvBean.setBidAcceptingEntityType(CostBearer.Buyer);
						}else if(AppConstants.CostBearer.Seller.getCode().equals(pMap.get("bidAcceptingEntityType").toString())){
							lPSLProvBean.setBidAcceptingEntityType(CostBearer.Seller);
						}
					}
					if(pMap.containsKey("enableCashDis") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableCashDis").toString())){
						if(pMap.get("cashDiscountPercent")!= null){
							lPSLProvBean.setCashDiscountPercent(new BigDecimal(pMap.get("cashDiscountPercent").toString()));
						}
					}
					if(pMap.containsKey("enableHaircut") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableHaircut").toString())){
						if(pMap.get("haircutPercent")!= null){
							lPSLProvBean.setCashDiscountPercent(new BigDecimal(pMap.get("haircutPercent").toString()));
						}
					}
					if(pMap.containsKey("enableSettleLeg3Flag") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableSettleLeg3Flag").toString())){
						if(pMap.get("settleLeg3Flag")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("settleLeg3Flag").toString())){
								lPSLProvBean.setSettleLeg3Flag(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("settleLeg3Flag").toString())){
								lPSLProvBean.setSettleLeg3Flag(CommonAppConstants.YesNo.No);
							}
						}
					}
					if(pMap.containsKey("enableInstrumentCreation") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableInstrumentCreation").toString())){
						if(pMap.get("instrumentCreation")!= null){
							if(PurchaserSupplierLinkBean.InstrumentCreation.Purchaser.getCode().equals(pMap.get("instrumentCreation"))){
								lPSLProvBean.setInstrumentCreation(PurchaserSupplierLinkBean.InstrumentCreation.Purchaser);
							}else if(PurchaserSupplierLinkBean.InstrumentCreation.Supplier.getCode().equals(pMap.get("instrumentCreation"))){
								lPSLProvBean.setInstrumentCreation(PurchaserSupplierLinkBean.InstrumentCreation.Supplier);
							}else if(PurchaserSupplierLinkBean.InstrumentCreation.Both.getCode().equals(pMap.get("instrumentCreation"))){
								lPSLProvBean.setInstrumentCreation(PurchaserSupplierLinkBean.InstrumentCreation.Both);
							}
						}
					}
					if(pMap.containsKey("enableRemarks") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableRemarks").toString())){
						if(pMap.get("remarks")!= null){
							lPSLProvBean.setRemarks(pMap.get("remarks").toString());
						}
					}
					if(pMap.containsKey("enableRelation") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableRelation").toString())){
						if(pMap.get("relationFlag")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("relationFlag").toString())){
								lPSLProvBean.setRelationFlag(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("relationFlag").toString())){
								lPSLProvBean.setRelationFlag(CommonAppConstants.YesNo.No);
							}
						}
						if(pMap.get("relationDoc")!= null){
							lPSLProvBean.setRelationDoc(pMap.get("relationFlag").toString());
						}
						if(pMap.get("relationEffectiveDate")!= null){
							lPSLProvBean.setRelationEffectiveDate(CommonUtilities.getDate(pMap.get("relationEffectiveDate").toString(),AppConstants.DATE_FORMAT));
						}
					}
					if(pMap.containsKey("enableBuyerTds") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableBuyerTds").toString())){
						if(pMap.get("buyerTds")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("buyerTds").toString())){
								lPSLProvBean.setBuyerTds(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("buyerTds").toString())){
								lPSLProvBean.setBuyerTds(CommonAppConstants.YesNo.No);
							}
						}
						if(pMap.get("buyerTdsPer")!= null){
							lPSLProvBean.setBuyerTdsPercent(new BigDecimal(pMap.get("buyerTdsPer").toString()));
						}
					}
					if(pMap.containsKey("enableSellerTds") && CommonAppConstants.Yes.Yes.getCode().equals(pMap.get("enableSellerTds").toString())){
						if(pMap.get("sellerTds")!= null){
							if(CommonAppConstants.YesNo.Yes.getCode().equals(pMap.get("sellerTds").toString())){
								lPSLProvBean.setSellerTds(CommonAppConstants.YesNo.Yes);
							}else if(CommonAppConstants.YesNo.No.getCode().equals(pMap.get("sellerTds").toString())){
								lPSLProvBean.setSellerTds(CommonAppConstants.YesNo.No);
							}
						}
						if(pMap.get("sellerTdsPer")!= null){
							lPSLProvBean.setSellerTdsPercent(new BigDecimal(pMap.get("sellerTdsPer").toString()));
						}
					}
				}
				lPSLProvBean.setApprovalStatus(ApprovalStatus.Draft);
				purchaserSupplierLinkProvDAO.update(pConnection, lPSLProvBean);
			}
		}
	}
}
