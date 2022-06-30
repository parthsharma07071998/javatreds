package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityBean.BusinessSource;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;
import com.xlx.treds.user.bean.AppUserBean;

public class AppEntityBO {
    
    private GenericDAO<AppEntityBean> appEntityDAO;
    private BeanMeta appEntityPreferenceBeanMeta;
    private BeanMeta appEntityBeanMeta;
    
    public AppEntityBO() {
        super();
        appEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        appEntityPreferenceBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityPreferenceBean.class);
        appEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
    }
    
    public AppEntityBean findBean(ExecutionContext pExecutionContext, 
        AppEntityBean pFilterBean) throws Exception {
        AppEntityBean lAppEntityBean = appEntityDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lAppEntityBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lAppEntityBean;
    }
    
    public List<AppEntityBean> findList(ExecutionContext pExecutionContext, AppEntityBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        boolean lIsFinancier = false;
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            //for financiers to see the buyers list 
            AppEntityBean lLoggedInAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
            if (lLoggedInAppEntityBean == null) throw new CommonBusinessException("Invalid logged in entity");
            lIsFinancier = lLoggedInAppEntityBean.isFinancier();
            if (!lIsFinancier) {
                if (pFilterBean == null) pFilterBean = new AppEntityBean();
                pFilterBean.setCode(lLoggedInAppEntityBean.getCode());
            }
        }
        boolean lAppUserFilter = false;
        if(pFilterBean!=null) {
        	lAppUserFilter = (pFilterBean.getRmUserId()!=null||pFilterBean.getRsmUserId()!=null||StringUtils.isNotEmpty(pFilterBean.getRmLocation())||StringUtils.isNotEmpty(pFilterBean.getRsmLocation()));
        }
        String lFieldsStr = appEntityDAO.getDBColumnNameCsv(null,Arrays.asList("recordUpdator"));
        StringBuilder lSql = new StringBuilder();
        lSql.append(" SELECT ").append(lFieldsStr).append(" , nvl(FINCOUNTONPURLIMIT_VW.FINCOUNT, 0) AERECORDUPDATOR FROM APPENTITIES ");
        lSql.append(" LEFT OUTER JOIN FINCOUNTONPURLIMIT_VW ");
        lSql.append(" ON ( AECODE = FASPURCHASER ) ");
        //
        if(lAppUserFilter) {
        	if(pFilterBean.getRmUserId()!=null||StringUtils.isNotEmpty(pFilterBean.getRmLocation())){
                lSql.append(" LEFT OUTER JOIN APPUSERS RMUSERS ON ( AERMUserId = RMUSERS.AUId ) ");
        	}
        	if(pFilterBean.getRsmUserId()!=null||StringUtils.isNotEmpty(pFilterBean.getRsmLocation())){
                lSql.append(" LEFT OUTER JOIN APPUSERS RSMUSERS ON ( AERSMUserId = RSMUSERS.AUId ) ");
        	}
        }
        //if rating then join
        if(StringUtils.isNotEmpty(pFilterBean.getRating())){
        	lSql.append(" LEFT OUTER JOIN ACTIVEBUYERRATINGS_VW ");
        	lSql.append(" ON ( FASPURCHASER = BCRBUYERCODE ) ");
        }
    	lSql.append(" WHERE 1=1 ");
        appEntityDAO.appendAsSqlFilter(lSql, pFilterBean, false );

        
        //if rating then put clause on view
        if(StringUtils.isNotEmpty(pFilterBean.getRating())){
        	lSql.append(" AND BCRRATING = ").append(DBHelper.getInstance().formatString(pFilterBean.getRating()));
        }
        
        if(lAppUserFilter) {
        	if(pFilterBean.getRmUserId()!=null){
                lSql.append(" AND RMUSERS.AUId = ").append(pFilterBean.getRmUserId());
        	}
        	if(StringUtils.isNotEmpty(pFilterBean.getRmLocation())){
                lSql.append(" AND RMUSERS.AURmLocation = ").append(DBHelper.getInstance().formatString(pFilterBean.getRmLocation()));
        	}
        	if(pFilterBean.getRsmUserId()!=null){
                lSql.append(" AND RSMUSERS.AUId = ").append(pFilterBean.getRsmUserId());
        	}
        	if(StringUtils.isNotEmpty(pFilterBean.getRsmLocation())){
                lSql.append(" AND RSMUSERS.AURmLocation = ").append(DBHelper.getInstance().formatString(pFilterBean.getRsmLocation()));
        	}
        }
        
        List<AppEntityBean> lList = appEntityDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        
        if (!lIsFinancier) {
            return lList;
        }
        // filter out blocked buyers for financier list
        List<AppEntityBean> lFilteredList = new ArrayList<AppEntityBean>();
        for (AppEntityBean lAppEntityBean : lList) {
        	lAppEntityBean.setFinancierCount(lAppEntityBean.getRecordUpdator());
            if (lAppEntityBean.isPurchaser() && !lAppEntityBean.isFinancierBlocked(pUserBean.getDomain()))
                lFilteredList.add(lAppEntityBean);
            else if (lAppEntityBean.isSupplier())
                lFilteredList.add(lAppEntityBean);
        }
        return lFilteredList;
    }

    
    public List<AppEntityBean> findList(ExecutionContext pExecutionContext, EntityType pEntityType, 
            IAppUserBean pUserBean, boolean pIncludeSelf) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lUserEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        boolean lPlatformAdmin = AppConstants.DOMAIN_PLATFORM.equals(lUserEntityBean.getCode());
        boolean lSupplier = lUserEntityBean.isSupplier();
        boolean lPurchaser = lUserEntityBean.isPurchaser();
        boolean lFinancier = lUserEntityBean.isFinancier();
        Vector<AppEntityBean>lRows = lMemoryTable.selectAllRows(AppEntityBean.f_Code);
        List<AppEntityBean> lList = new ArrayList<AppEntityBean>();
        for (AppEntityBean lAppEntityBean : lRows) {
            boolean lIncludeSelf = (lAppEntityBean == lUserEntityBean) && pIncludeSelf;
            if ((pEntityType == null))
                lList.add(lAppEntityBean);
            else if ((pEntityType == AppConstants.EntityType.Supplier) && lAppEntityBean.isSupplier()) {
                if (lPurchaser || lFinancier || lPlatformAdmin || lIncludeSelf)
                    lList.add(lAppEntityBean);
            } else if ((pEntityType == AppConstants.EntityType.Purchaser) && lAppEntityBean.isPurchaser()) {
                if (lSupplier || lFinancier || lPlatformAdmin || lIncludeSelf)
                    lList.add(lAppEntityBean);
            } else if ((pEntityType == AppConstants.EntityType.Financier) && lAppEntityBean.isFinancier()) {
                if (lSupplier || lPurchaser || lPlatformAdmin || lIncludeSelf)
                    lList.add(lAppEntityBean);
            } else if ((pEntityType == AppConstants.EntityType.Aggregator) && lAppEntityBean.isPurchaserAggregator()) {
                if (lPlatformAdmin)
                    lList.add(lAppEntityBean);
            }
        }
        Collections.sort(lList, new Comparator<AppEntityBean>() {

            public int compare(AppEntityBean pAppEntityBean1, AppEntityBean pAppEntityBean2) {
                return pAppEntityBean1.getName().toUpperCase().compareTo(pAppEntityBean2.getName().toUpperCase());
            }
        });
        return lList;
    }
    
    public void save(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        AppEntityBean lOldAppEntityBean = null;
        if (pNew) {
            pAppEntityBean.setRecordCreator(pUserBean.getId());
            appEntityDAO.insert(lConnection, pAppEntityBean);
            appEntityDAO.insertAudit(lConnection, pAppEntityBean, AuditAction.Insert, pUserBean.getId());
        } else {
        	lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        	appEntityDAO.getBeanMeta().copyBean(pAppEntityBean, lOldAppEntityBean, null);
        	lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
            if (appEntityDAO.update(lConnection, lOldAppEntityBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
            pAppEntityBean = lOldAppEntityBean;
            lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, pAppEntityBean);
        }
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, pAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void updateIps(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
            throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()))
        {
            if (!pAppEntityBean.getCode().equals(pUserBean.getDomain()) || (lAppUserBean.getType() != AppUserBean.Type.Admin))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        lOldAppEntityBean.setIpList(pAppEntityBean.getIpList());
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEIPLIST) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void update2FA(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
            throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()))
        {
            if (!pAppEntityBean.getCode().equals(pUserBean.getDomain()) || (lAppUserBean.getType() != AppUserBean.Type.Admin))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        lOldAppEntityBean.setTwoFaType(pAppEntityBean.getTwoFaType());
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        System.out.print(lOldAppEntityBean.getSettings());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATE2FA) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void updateReqVer(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
            throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain()))
        {
            if (!pAppEntityBean.getCode().equals(pUserBean.getDomain()) || (lAppUserBean.getType() != AppUserBean.Type.Admin))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        lOldAppEntityBean.setRequiredAgreementVersion(pAppEntityBean.getRequiredAgreementVersion());
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        //System.out.print(lOldAppEntityBean.getSettings());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEREQ_VER) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void updateSplitSettings(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
            throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        if (!lOldAppEntityBean.isPurchaser())
    		throw new CommonBusinessException("Entity is not a buyer");
        lOldAppEntityBean.setAllowObliSplitting(pAppEntityBean.getAllowObliSplitting());
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATESPLITSETTINGS) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void updateBlockedFinanciers(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
    		throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
    	if (!pAppEntityBean.getCode().equals(pUserBean.getDomain()))
    		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
    	// should be a purchaser
    	if (!lOldAppEntityBean.isPurchaser())
    		throw new CommonBusinessException("Entity is not a buyer");
    	// check if list has financiers
    	if (pAppEntityBean.getBlockedFinancierList() != null) {
    		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
    		for (String lCode : pAppEntityBean.getBlockedFinancierList()) {
    			AppEntityBean lFinancier = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lCode});
    			if (lFinancier == null)
    				throw new CommonBusinessException("Financier " + lCode + " does not exist");
    			if (!lFinancier.isFinancier())
    				throw new CommonBusinessException(lCode + " is not a financier");
    		}
    	}
    	lOldAppEntityBean.setBlockedFinancierList(pAppEntityBean.getBlockedFinancierList());
    	lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEBLOCKEDFINANCIERS) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
    	pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, AppEntityBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        AppEntityBean lAppEntityBean = findBean(pExecutionContext, pFilterBean);
        lAppEntityBean.setRecordUpdator(pUserBean.getId());
        appEntityDAO.delete(lConnection, lAppEntityBean);   
        appEntityDAO.insertAudit(lConnection, lAppEntityBean, AuditAction.Delete, pUserBean.getId());


        pExecutionContext.commitAndDispose();
    }
    
    public void updateCheckerLimitSettings(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean)
            throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        lOldAppEntityBean.setCheckerLevelSetting(pAppEntityBean.getCheckerLevelSetting());
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATECHECKERLIMITS) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
        
    }
    
    public void saveEntityPreferences(ExecutionContext pExecutionContext, String pEntityCode, 
    		Map<String,Object> pInputMap, IAppUserBean pUserBean) throws Exception {
    	AppEntityBean lLoggedInEntity = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
    	if (!lLoggedInEntity.isPlatform())
    		pEntityCode = lLoggedInEntity.getCode();

    	AppEntityBean lFilterBean = new AppEntityBean();
    	lFilterBean.setCode(pEntityCode);
    	AppEntityBean lAppEntityBean = findBean(pExecutionContext, lFilterBean);

    	List<String> lFieldList = new ArrayList<String>();
    	String lFieldGroup = null;
    	if (lAppEntityBean.isPurchaser())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_BUYERFIELDS;
    	else if (lAppEntityBean.isSupplier())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_SELLERFIELDS;
    	else if (lAppEntityBean.isFinancier())
    		lFieldGroup = AppEntityPreferenceBean.FIELDGROUP_FINANCIERFIELDS;
    	if (lFieldGroup != null) {
    		List<BeanFieldMeta> lFieldsForType = appEntityPreferenceBeanMeta.getFieldMetaList(lFieldGroup, null);
    		String lAccessFieldGroup = lLoggedInEntity.isPlatform()?
    				AppEntityPreferenceBean.FIELDGROUP_UPDATEBYPLATFORM:AppEntityPreferenceBean.FIELDGROUP_UPDATEBYENTITY;
    		List<BeanFieldMeta> lAccessFields = appEntityPreferenceBeanMeta.getFieldMetaList(lAccessFieldGroup, null);
    		Set<String> lSet = new HashSet<String>();
    		for (BeanFieldMeta lBeanFieldMeta : lAccessFields)
    			lSet.add(lBeanFieldMeta.getName());
    		// intersection
    		for (BeanFieldMeta lField : lFieldsForType) {
    			if (lSet.contains(lField.getName()))
    				lFieldList.add(lField.getName());
    		}
    	}
    	
        AppEntityPreferenceBean lAppEntityPreferenceBean = new AppEntityPreferenceBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityPreferenceBeanMeta.validateAndParse(lAppEntityPreferenceBean, 
        		pInputMap, null, lFieldList, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        lAppEntityBean.setPreferences(lAppEntityPreferenceBean);
        lAppEntityBean.setRecordUpdator(pUserBean.getId());
        //
        //checking if enable location preference is not null and setting elp value to AeEnableLocationwiseSettlement
        if(lAppEntityPreferenceBean.getElp() != null && CommonAppConstants.Yes.Yes.equals(lAppEntityPreferenceBean.getElp())){
        	lAppEntityBean.setEnableLocationwiseSettlement(CommonAppConstants.Yes.Yes);
        }else{
        	lAppEntityBean.setEnableLocationwiseSettlement(null);
        }
        //
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        if (appEntityDAO.update(lConnection, lAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEPREFERENCES) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lAppEntityBean, AuditAction.Update, pUserBean.getId());
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
	    lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
	    pExecutionContext.commitAndDispose();
    }

	public void saveCreditReport(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, AppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		Map<String, Object> lResultMap = new HashMap<String, Object>();
		AppEntityBean lFilterBean = new AppEntityBean();
		lFilterBean.setCode(pAppEntityBean.getCode());
		AppEntityBean lAppEntityBean = appEntityDAO.findBean(lConnection, lFilterBean);
	    MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
		if(lAppEntityBean != null){
			if(lAppEntityBean.isPurchaser()){
				lAppEntityBean.setCreditReport(pAppEntityBean.getCreditReport());
				appEntityDAO.update(lConnection, lAppEntityBean, AppEntityBean.FIELDGROUP_UPDATECREDITREPORT);
				appEntityDAO.insertAudit(lConnection, lAppEntityBean, AuditAction.Update, pUserBean.getId());
				lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
				lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
			}else{
				throw new CommonBusinessException("Please select a Buyer.");
			}
		}else{
			throw new CommonBusinessException("Entity not found.");
		}
	}

	public void updateRmSettings(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppEntityBean lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
        //validation
        //After discussion with Kailash, the below points were finalized:
        //For Buyer and Financier, RM is mandatory & RSM is optional
        //For Seller, RM is optional and RSM is mandatory
        if(lOldAppEntityBean.isPurchaser()||lOldAppEntityBean.isFinancier()) {
        	if(pAppEntityBean.getRmUserId()==null) {
        		throw new CommonBusinessException("RM is mandatory.");
        	}
        }else if (lOldAppEntityBean.isSupplier()) {
        	if(pAppEntityBean.getRsmUserId()==null) {
        		throw new CommonBusinessException("RSM is mandatory.");
        	}
        }
        if(BusinessSource.Referal.equals(pAppEntityBean.getBusinessSource())) {
        	if(StringUtils.isEmpty(pAppEntityBean.getRefererCode())) {
        		throw new CommonBusinessException("For Business source referal, Referal Code is mandatory.");        		
        	}
        }
        //
        appEntityBeanMeta.copyBean(pAppEntityBean, lOldAppEntityBean, AppEntityBean.FIELDGROUP_COPYRM, null);
        lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
        if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATERM) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
        pExecutionContext.commitAndDispose();
	}

}
