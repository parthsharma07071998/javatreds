package com.xlx.treds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RegistryEntryBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityBean.EntityType;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.IAgreementAcceptanceBean;
import com.xlx.treds.user.bean.PurchaserAgreementAcceptanceBean;
import com.xlx.treds.user.bean.SupplierAgreementAcceptanceBean;

public class ClickWrapHelper {

	public static Logger logger = Logger.getLogger(ClickWrapHelper.class);
    private static ClickWrapHelper theInstance;
    //
    private GenericDAO<PurchaserAgreementAcceptanceBean> purchaserAgreementAcceptanceDAO;
    private GenericDAO<SupplierAgreementAcceptanceBean> supplierAgreementAcceptanceDAO;
    public static final String REGISTRY_PURCHASER_AGREEMENT_ENABLED = "server.settings.enablepurchaseragreement";
    public static final String REGISTRY_SUPPLIER_AGREEMENT_ENABLED = "server.settings.enablesupplieragreement";
    //
	public static ClickWrapHelper getInstance()
	{
		if (theInstance == null)
		{
			synchronized(TredsHelper.class)
			{
				if (theInstance == null)
				{
					try
					{
						ClickWrapHelper tmpTheInstance = new ClickWrapHelper();
						theInstance = tmpTheInstance;
					}
					catch(Exception lException)
					{
						logger.fatal("Error while instantiating ClickWrap Helper",lException);
					}
				}
			}
		}
		return theInstance;
	}

	protected ClickWrapHelper() 
	{
        purchaserAgreementAcceptanceDAO = new GenericDAO<PurchaserAgreementAcceptanceBean>(PurchaserAgreementAcceptanceBean.class);
        supplierAgreementAcceptanceDAO = new GenericDAO<SupplierAgreementAcceptanceBean>(SupplierAgreementAcceptanceBean.class);
	}
	
	
	public ArrayList<Map<String,String>> getClickWrapAggrementVersions(EntityType pEntityType){
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
    	HashMap lValidAgreements = null;

    	//find latest version information
    	if(EntityType.Purchaser.equals(pEntityType)){
    		lValidAgreements = lRegistryHelper.getKeyedValues(AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_PURCHASER,RegistryEntryBean.DATATTYPE_STRUCTURE);
    	}else if(EntityType.Supplier.equals(pEntityType)){
    		lValidAgreements = lRegistryHelper.getKeyedValues(AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_SUPPLIER,RegistryEntryBean.DATATTYPE_STRUCTURE);
    	}
    	ArrayList<Map<String,String>> lVersionList = new ArrayList<Map<String,String>>();
    	HashMap<String,String> lVersionObj = null;
    	
    	for(Object lVersion : lValidAgreements.keySet()){
    		lVersionObj = new HashMap<String,String>();
    		lVersionObj.put(BeanFieldMeta.JSONKEY_TEXT, lVersion.toString());
    		lVersionObj.put(BeanFieldMeta.JSONKEY_VALUE, lVersion.toString());
    		lVersionList.add(lVersionObj);
    	}
    	return lVersionList;
	}

    private PurchaserAgreementAcceptanceBean findBean(ExecutionContext pExecutionContext, 
            PurchaserAgreementAcceptanceBean pFilterBean) throws Exception {
        PurchaserAgreementAcceptanceBean lPurchaserAgreementAcceptanceBean = purchaserAgreementAcceptanceDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPurchaserAgreementAcceptanceBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lPurchaserAgreementAcceptanceBean;
    }

    public void acceptClickWrapAgreement(ExecutionContext pExecutionContext, AppUserBean pAppUserBean) throws Exception{
    	AppEntityBean lAppEntityBean = null;
    	pExecutionContext.setAutoCommit(false);
    	Connection lConnection = pExecutionContext.getConnection();
    	MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
    	
    	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
        if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) && !(lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	
        RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
        java.util.Date lRequiredVersionDate=null;
   	    String KEY_LATESTVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_SUPPLIER:"");
   	    String KEY_VALIDVERSION = lAppEntityBean.isPurchaser()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_PURCHASER:(lAppEntityBean.isSupplier()?AppInitializer.REGISTRY_VALIDCLICKWRAPAGREEMENTS_SUPPLIER:"");

        String lLatestVersion = lRegistryHelper.getString(KEY_LATESTVERSION);
        String lRequiredVersion = lAppEntityBean.getRequiredAgreementVersion(); 
        if(StringUtils.isEmpty(lRequiredVersion)){
        	lRequiredVersion = lLatestVersion ;
        }

        if(CommonUtilities.hasValue(lRequiredVersion)){
    		HashMap lValidAgreements = lRegistryHelper.getKeyedValues(KEY_VALIDVERSION, RegistryEntryBean.DATATTYPE_STRUCTURE);
    		HashMap lRequiredAgreement = (HashMap) lValidAgreements.get(lRequiredVersion);
        	lRequiredVersionDate = CommonUtilities.getDate(new Timestamp((Long)lRequiredAgreement.get(AppInitializer.PARAM_CLICKWRAP_REVISIONDATE)));
        	//
        	IAgreementAcceptanceBean lFilterPAABean = null;
        	IAgreementAcceptanceBean lPAABean = null;
        	if(lAppEntityBean.isPurchaser()){
            	lFilterPAABean = new PurchaserAgreementAcceptanceBean();
            	lPAABean = new PurchaserAgreementAcceptanceBean();
        	}else if (lAppEntityBean.isSupplier()){
            	lFilterPAABean = new SupplierAgreementAcceptanceBean();
            	lPAABean = new SupplierAgreementAcceptanceBean();
        	}
        	lFilterPAABean.setEntity(lAppEntityBean.getCode());
        	lFilterPAABean.setRevisionDate(lRequiredVersionDate);
        	lFilterPAABean.setVersion(lRequiredVersion);

        	if(lAppEntityBean.isPurchaser()){
        		lPAABean= purchaserAgreementAcceptanceDAO.findByPrimaryKey(lConnection,(PurchaserAgreementAcceptanceBean) lFilterPAABean);
        	}else if (lAppEntityBean.isSupplier()){
        		lPAABean= supplierAgreementAcceptanceDAO.findByPrimaryKey(lConnection, (SupplierAgreementAcceptanceBean)lFilterPAABean);
        	}
        	
        	if(lPAABean==null || lPAABean.getId() == null){
        		GenericDAO<AppEntityBean> lAppEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        		//insert transaction record in the purAggAccp table
            	if(lAppEntityBean.isPurchaser()){
            		lPAABean = new PurchaserAgreementAcceptanceBean();
            	}else if (lAppEntityBean.isSupplier()){
            		lPAABean = new SupplierAgreementAcceptanceBean();
            	}
             	lPAABean.setEntity(lAppEntityBean.getCode());
             	lPAABean.setRevisionDate(lRequiredVersionDate);
             	lPAABean.setVersion(lRequiredVersion);
            	lPAABean.setRecordCreator(pAppUserBean.getId()); 
            	if(lAppEntityBean.isPurchaser()){
                	purchaserAgreementAcceptanceDAO.insert(lConnection, (PurchaserAgreementAcceptanceBean) lPAABean);
            	}else if (lAppEntityBean.isSupplier()){
                	supplierAgreementAcceptanceDAO.insert(lConnection, (SupplierAgreementAcceptanceBean) lPAABean);
            	}
            	//update the latest accepted version in the appentity table
            	lAppEntityBean.setAcceptedAgreementVersion(lRequiredVersion);
            	lAppEntityDAO.update(lConnection, lAppEntityBean, AppEntityBean.FIELDGROUP_UPDATECLIKWRAP);
            	//update the memory db collection of the appentity
                lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
                lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
        	}else{
        		// already present and acccepted.
        	}
    	}
        pExecutionContext.commitAndDispose();
    }

	public List<IAgreementAcceptanceBean> getAcceptedAggrementDetails(Connection pConnection,String pDomain ,Long pInstId){
    	AppEntityBean lAppEntityBean = null;
    	
    	try {
			lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pDomain);
			if(lAppEntityBean.isPurchaser()){
				return getPurAcceptedAggrementDetails(pConnection, pDomain, pInstId);
			}else if(lAppEntityBean.isSupplier()){
				return getSupAcceptedAggrementDetails(pConnection, pDomain, pInstId);
			}
		} catch (Exception e) {
			logger.info("getAcceptedAggrementDetails : Domain not found : " + e.getMessage());
		}
    	return null;
	}

	private List<IAgreementAcceptanceBean> getPurAcceptedAggrementDetails(Connection pConnection,String pDomain ,Long pInstId){
		List<IAgreementAcceptanceBean> lList = new ArrayList<>();
		PurchaserAgreementAcceptanceBean lResult = null;
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT PURCHASERAGREEMENTACCEPTANCE.* ");
		lSql.append(" FROM PURCHASERAGREEMENTACCEPTANCE ");
		lSql.append(" INNER JOIN  ");
		lSql.append(" (  ");
		lSql.append(" 	SELECT PAAPURCHASER purchaser, Max(PAARecordCreateTime) lastestdatetime ");
		lSql.append(" 	FROM PURCHASERAGREEMENTACCEPTANCE ");
		lSql.append(" 	WHERE PAARECORDVERSION > 0 ");
		lSql.append(" 	AND PAAINSTRUMENTID IS NULL ");
		lSql.append(" 	AND PAAPURCHASER = ").append(lDBHelper.formatString(pDomain));;
		lSql.append(" 	GROUP BY PAAPURCHASER ");
		lSql.append(" ) p1 ");
		lSql.append(" ON PAAPURCHASER = purchaser ");
		lSql.append(" AND PAARecordCreateTime = lastestdatetime ");
		lSql.append(" WHERE PAARECORDVERSION > 0 ");
		lSql.append(" AND PAAPURCHASER = ").append(lDBHelper.formatString(pDomain));

		if(pInstId!=null) {
			lSql.append(" UNION ");

			lSql.append(" SELECT PURCHASERAGREEMENTACCEPTANCE.*  ");
			lSql.append(" FROM PURCHASERAGREEMENTACCEPTANCE ");
			lSql.append(" INNER JOIN  ");
			lSql.append(" ( ");
			lSql.append(" 	SELECT PAAPURCHASER purchaser, PAAINSTRUMENTID instrumentid, Max(PAARecordCreateTime) lastestdatetime ");
			lSql.append(" 	FROM PURCHASERAGREEMENTACCEPTANCE ");
			lSql.append(" 	WHERE PAARECORDVERSION > 0 ");
			lSql.append(" 	AND PAAINSTRUMENTID IS NOT NULL ");
			lSql.append(" 	AND PAAPURCHASER = ").append(lDBHelper.formatString(pDomain));
			lSql.append(" 	AND PAAINSTRUMENTID = ").append(pInstId);
			lSql.append(" 	GROUP BY PAAPURCHASER,PAAINSTRUMENTID ");
			lSql.append(" ) p1 ");
			lSql.append(" ON PAAPURCHASER = purchaser ");
			lSql.append(" AND PAARecordCreateTime = lastestdatetime ");
			lSql.append(" AND PAAInstrumentId = instrumentid ");
			lSql.append(" WHERE PAARECORDVERSION > 0 ");
			lSql.append(" AND PAAPURCHASER =  ").append(lDBHelper.formatString(pDomain));
			lSql.append(" AND PAAINSTRUMENTID = ").append(pInstId);
			
		}
		try {
			List<PurchaserAgreementAcceptanceBean> lPurList = purchaserAgreementAcceptanceDAO.findListFromSql(pConnection, lSql.toString(), 0);
			if(lPurList!=null){
				lList = new ArrayList<IAgreementAcceptanceBean>();
				for(PurchaserAgreementAcceptanceBean lBean : lPurList){
					lList.add(lBean);
				}
			}
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
		}
    	return lList;
	}

	private List<IAgreementAcceptanceBean> getSupAcceptedAggrementDetails(Connection pConnection,String pDomain ,Long pInstId){
		List<IAgreementAcceptanceBean> lList = new ArrayList<>();
		SupplierAgreementAcceptanceBean lResult = null;
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT SUPPLIERAGREEMENTACCEPTANCE.* ");
		lSql.append(" FROM SUPPLIERAGREEMENTACCEPTANCE ");
		lSql.append(" INNER JOIN  ");
		lSql.append(" (  ");
		lSql.append(" 	SELECT SAASUPPLIER supplier, Max(SAARecordCreateTime) lastestdatetime ");
		lSql.append(" 	FROM SUPPLIERAGREEMENTACCEPTANCE ");
		lSql.append(" 	WHERE SAARECORDVERSION > 0 ");
		lSql.append(" 	AND SAAFACTORINGUNITID IS NULL ");
		lSql.append(" 	AND SAASUPPLIER = ").append(lDBHelper.formatString(pDomain));;
		lSql.append(" 	GROUP BY SAASUPPLIER ");
		lSql.append(" ) p1 ");
		lSql.append(" ON SAASUPPLIER = supplier ");
		lSql.append(" AND SAARecordCreateTime = lastestdatetime ");
		lSql.append(" WHERE SAARECORDVERSION > 0 ");
		lSql.append(" AND SAASUPPLIER = ").append(lDBHelper.formatString(pDomain));

		if(pInstId!=null) {
			lSql.append(" UNION ");
 
			lSql.append(" SELECT SUPPLIERAGREEMENTACCEPTANCE.*  ");
			lSql.append(" FROM SUPPLIERAGREEMENTACCEPTANCE ");
			lSql.append(" INNER JOIN  ");
			lSql.append(" ( ");
			lSql.append(" 	SELECT SAASUPPLIER supplier, SAAFACTORINGUNITID instrumentid, Max(SAARecordCreateTime) lastestdatetime ");
			lSql.append(" 	FROM SUPPLIERAGREEMENTACCEPTANCE ");
			lSql.append(" 	WHERE SAARECORDVERSION > 0 ");
			lSql.append(" 	AND SAAINSTRUMENTID IS NOT NULL ");
			lSql.append(" 	AND SAASUPPLIER = ").append(lDBHelper.formatString(pDomain));
			lSql.append(" 	AND SAAINSTRUMENTID = ").append(pInstId);
			lSql.append(" 	GROUP BY SAASUPPLIER,SAAINSTRUMENTID ");
			lSql.append(" ) p1 ");
			lSql.append(" ON SAASUPPLIER = supplier ");
			lSql.append(" AND SAARecordCreateTime = lastestdatetime ");
			lSql.append(" AND SAAInstrumentId = instrumentid ");
			lSql.append(" WHERE SAARECORDVERSION > 0 ");
			lSql.append(" AND SAASUPPLIER =  ").append(lDBHelper.formatString(pDomain));
			lSql.append(" AND SAAINSTRUMENTID = ").append(pInstId);
			
		}
		try {
			List<SupplierAgreementAcceptanceBean> lTempList = supplierAgreementAcceptanceDAO.findListFromSql(pConnection, lSql.toString(), 0);
			if(lTempList!=null){
				lList = new ArrayList<IAgreementAcceptanceBean>();
				for(SupplierAgreementAcceptanceBean lBean : lTempList){
					lList.add(lBean);
				}
			}
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
		}
    	return lList;
	}

	public void removeClickWrapAgreements(Connection pConnection, List<IAgreementAcceptanceBean> pClickWrapAgreementList, Long pAuId){
		if(pClickWrapAgreementList != null && pClickWrapAgreementList.size() > 0){
			if(pClickWrapAgreementList.get(0) instanceof PurchaserAgreementAcceptanceBean){
		    	PurchaserAgreementAcceptanceBean lPAABean = null;
		    	for(IAgreementAcceptanceBean lTmpBean : pClickWrapAgreementList){
		    		lPAABean = (PurchaserAgreementAcceptanceBean) lTmpBean;
		    		if(lPAABean.getKeyId()!=null){
		    			lPAABean.setRecordUpdator(pAuId);
		        		try{
		        			purchaserAgreementAcceptanceDAO.delete(pConnection, lPAABean);
		        		}catch(Exception lExcep){
		        			logger.error(lExcep.toString());
		        		}
		    		}
		    	}
			}else if(pClickWrapAgreementList.get(0) instanceof SupplierAgreementAcceptanceBean){
				//TODO: SUPPLIERS TO BE CODED
			}

		}
	}
	

/*	private PurchaserAgreementAcceptanceBean getAggrementDetailsForInstrument(Connection pConnection,String pDomain){
		List<IAgreementAcceptanceBean> lPAAList = getAcceptedAggrementDetails(pConnection,pDomain,null);
		PurchaserAgreementAcceptanceBean lPAABean=null;
		if(lPAAList!=null){
			lPAABean = (PurchaserAgreementAcceptanceBean) lPAAList.get(0);
		}
		return lPAABean;		
	}
*/	
	public void addCounterAcceptanceForInstrument(Connection pConnection,String pPurchaser, Long pInstrument, Long pAuId, boolean pCheckSizeOne){
		List<IAgreementAcceptanceBean> lPAAList = getAcceptedAggrementDetails(pConnection,pPurchaser,null);
		PurchaserAgreementAcceptanceBean lPAABean=null;
		if(lPAAList!=null && lPAAList.size() > 0){
			lPAABean = (PurchaserAgreementAcceptanceBean) lPAAList.get(0);
		}
    	if(lPAABean!=null){
    		if(!pCheckSizeOne || (pCheckSizeOne && lPAAList.size() == 1)){
        		lPAABean.setRecordCreateTime(null);
        		lPAABean.setId(null);
        		lPAABean.setRecordCreator(pAuId);
        		lPAABean.setRecordVersion(null);
        		lPAABean.setInstrumentId(pInstrument);
        		try {
    				purchaserAgreementAcceptanceDAO.insert(pConnection, lPAABean);
    			} catch (SQLException e) {
    				logger.info("Error in addCounterAcceptanceForInstrument : "+ e.getMessage());
    			}
    		}
    	}        	

	}
	
	public Map<String, Object> getAgreementAttributes(Connection pConnection,String pDomain,Long pInstId,boolean pSkipLogs) throws Exception {
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pDomain);
		Map <String,Object> lMap = new HashMap<>();
		String pPath ="agreements//";
		if(lAppEntityBean.isPurchaser()){
			pPath += "buyer//";
			pPath += (lAppEntityBean.getRequiredAgreementVersion()!=null?lAppEntityBean.getRequiredAgreementVersion():RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_PURCHASER));
		}else if (lAppEntityBean.isSupplier()){
			pPath += "seller//";
			pPath += (lAppEntityBean.getRequiredAgreementVersion()!=null?lAppEntityBean.getRequiredAgreementVersion():RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_CLICKWRAPLATESTVERSION_SUPPLIER));
			lMap.put("msmeType", lAppEntityBean.getMsmeStatus());
		}
		pPath += "//agreement.jsp";
		lMap.put("path", pPath );
		//
		if(!pSkipLogs){
			if(pConnection!=null){
				List<IAgreementAcceptanceBean> lPAAList  = getAcceptedAggrementDetails(pConnection, lAppEntityBean.getCode(), (pInstId!=null)?pInstId:null);
				List<Map<String,Object>> lList = new ArrayList<Map<String,Object>>();
				for(IAgreementAcceptanceBean lPAABean : lPAAList){
	            	try {
	                    MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	                    AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lPAABean.getRecordCreator()});
	                    Map <String,Object> lAcceptMap = new HashMap<>();
	                    lAcceptMap.put("memberId", lPAABean.getEntity());
	                    lAcceptMap.put("name", lAppEntityBean.getName());
	                    lAcceptMap.put("userId", lAppUserBean.getLoginId());
	                    lAcceptMap.put("userName", lAppUserBean.getName());
	                    lAcceptMap.put("dateTime", FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT,lPAABean.getRecordCreateTime()));
	                    lList.add(lAcceptMap);
	            	} catch (Exception lException) {
	                }            	
		     	}
				lMap.put("acceptDetails", lList);
			}
		}
		return lMap;
	}
	
	public boolean isAgreementEnabled(String pDomain){
		boolean lEnabled = false;
		try {
			AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(pDomain);
			if (lAEBean.isPurchaser()){
				lEnabled = RegistryHelper.getInstance().getBoolean(REGISTRY_PURCHASER_AGREEMENT_ENABLED);
			}else if (lAEBean.isSupplier()){
				lEnabled = RegistryHelper.getInstance().getBoolean(REGISTRY_SUPPLIER_AGREEMENT_ENABLED);
			}else if (lAEBean.isFinancier() || lAEBean.isPlatform()){
				lEnabled = true;
			}
		} catch (MemoryDBException e) {
			logger.info(e.getMessage());
		}		
		return lEnabled;
	}
}
