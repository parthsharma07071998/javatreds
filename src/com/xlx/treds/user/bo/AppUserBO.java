package com.xlx.treds.user.bo;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.LoginSessionBean;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.user.bean.RoleMasterBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean.CheckerType;

import groovy.json.JsonBuilder;

public class AppUserBO {
    
    private GenericDAO<AppUserBean> appUserDAO;
    private GenericDAO<MakerCheckerMapBean> makerCheckerMapDAO;

    public AppUserBO() {
        super();
        appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
        makerCheckerMapDAO = new GenericDAO<MakerCheckerMapBean>(MakerCheckerMapBean.class);
    }
    
    public AppUserBean findBean(ExecutionContext pExecutionContext, 
        AppUserBean pFilterBean, AppUserBean pLoggedInUserBean) throws Exception {
        
        if (!AppConstants.DOMAIN_PLATFORM.equals(pLoggedInUserBean.getDomain())){
            pFilterBean.setDomain(pLoggedInUserBean.getDomain());
        }
        if (pLoggedInUserBean.getType() != AppUserBean.Type.Admin)
            pFilterBean.setId(pLoggedInUserBean.getId());
        AppUserBean lAppUserBean = appUserDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lAppUserBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        //add Checkers to the list
        populateCheckers(pExecutionContext.getConnection(), lAppUserBean);
        return lAppUserBean;
    }
    
    public String getJsonForProfile(ExecutionContext pExecutionContext, 
            AppUserBean pFilterBean, AppUserBean pLoggedInUserBean) throws Exception {
        AppUserBean lAppUserBean = findBean(pExecutionContext, pFilterBean, pLoggedInUserBean);
        lAppUserBean.setPassword1(null);
        Map<String, Object> lMap = appUserDAO.getBeanMeta().formatAsMap(lAppUserBean);
        if (lAppUserBean.getRmIdList() != null) {
            List<String> lRoleNames = new ArrayList<String>();
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(RoleMasterBean.ENTITY_NAME);
            Long[] lFilter = new Long[1];
            for (Long lRMId : lAppUserBean.getRmIdList()) {
                lFilter[0] = lRMId;
                RoleMasterBean lRoleMasterBean = (RoleMasterBean)lMemoryTable.selectSingleRow(RoleMasterBean.f_Id, lFilter);
                if (lRoleMasterBean != null)
                    lRoleNames.add(lRoleMasterBean.getName());
            }
            lMap.put("rmIdList", lRoleNames);
        }
        /*if (lAppUserBean.getCheckers() != null) {
            List<String> lCheckerNames = new ArrayList<String>();
            for (Long lAuId : lAppUserBean.getCheckers()) {
                IAppUserBean lCheckerAppUserBean = AuthenticationHandler.getInstance().getUserDataSource().getUserBean(pExecutionContext, lAuId);
                if (lCheckerAppUserBean != null)
                    lCheckerNames.add(lCheckerAppUserBean.getLoginId());
            }
            lMap.put("checkers", lCheckerNames);
        }*/
        return new JsonBuilder(lMap).toString();
    }
    public List<AppUserBean> findList(ExecutionContext pExecutionContext, AppUserBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        AppUserBean lLoggedInUser = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInUser.getDomain())){
            pFilterBean.setDomain(lLoggedInUser.getDomain());
        }
        return appUserDAO.findList(pExecutionContext.getConnection(), pFilterBean, new ArrayList<String>());
    }
    
    public List<AppUserBean> findListWithLastLoginInfo(ExecutionContext pExecutionContext, AppUserBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
            AppUserBean lLoggedInUser = (AppUserBean)pUserBean;
            if (!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInUser.getDomain())){
                pFilterBean.setDomain(lLoggedInUser.getDomain());
            }
            //select till where clause explicitly 
            //appendSqlfilter
            List<String> lList=new ArrayList<String>();
            lList.add("recordUpdateTime");
            StringBuilder lSql =new StringBuilder();
            lSql.append("SELECT ");
            String lDBColumnNames =  appUserDAO.getDBColumnNameCsv(null,lList);
            lSql.append(lDBColumnNames+=",S AURecordUpdateTime FROM APPUSERS ");
            lSql.append(" LEFT OUTER JOIN ( ");
            lSql.append(" SELECT LSAUID, MAX(LSRECORDCREATETIME) S ");
            lSql.append(" from LOGINSESSIONS ");
            lSql.append(" WHERE LSSTATUS IN ('S','C') ");
            lSql.append(" GROUP BY LSAUID ) ON AUID=LSAUID WHERE 1=1 ");
            appUserDAO.appendAsSqlFilter(lSql, pFilterBean, false);
           return appUserDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        }
    
    public List<LoginSessionBean> findLoginSessionList(ExecutionContext pExecutionContext, LoginSessionBean pFilterBean,
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        AppUserBean lLoggedInUser = (AppUserBean)pUserBean;
        GenericDAO<LoginSessionBean> lLoginSessionDAO = new GenericDAO<LoginSessionBean>(LoginSessionBean.class);
        StringBuilder lFilter = new StringBuilder();
        String lDomain = pFilterBean.getDomain();
        String lLoginId = pFilterBean.getLoginId();
        if (!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInUser.getDomain())) {
            if (lLoggedInUser.getType() == AppUserBean.Type.Admin) {
                lDomain = lLoggedInUser.getDomain();
            } else {
                lDomain = lLoggedInUser.getDomain();
                lLoginId = lLoggedInUser.getLoginId(); 
            }
        }
        if (StringUtils.isNotBlank(lDomain) || StringUtils.isNotBlank(lLoginId)) {
            lFilter.append(" LSAUId IN (SELECT AUId FROM AppUsers WHERE AURecordVersion > 0 ");
            if (StringUtils.isNotBlank(lDomain))
                lFilter.append(" AND AUDomain = ").append(DBHelper.getInstance().formatString(lDomain));
            if (StringUtils.isNotBlank(lLoginId))
                lFilter.append(" AND AULoginId = ").append(DBHelper.getInstance().formatString(lLoginId));
            lFilter.append(")");
        }
        lLoginSessionDAO.appendAsSqlFilter(lFilter, pFilterBean, false);
        StringBuilder lSql = new StringBuilder();
        lSql.append("SELECT * FROM LoginSessions ");
        if (lFilter.length() > 0)
            lSql.append(" WHERE ").append(lFilter);
        lSql.append(" ORDER BY LSRecordCreateTime DESC");
        return lLoginSessionDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
    }
    
    public void save(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        AppUserBean lLoggedInUser = (AppUserBean)pUserBean;
        if (!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInUser.getDomain())) {
            pAppUserBean.setDomain(lLoggedInUser.getDomain());
            if (pNew)
                pAppUserBean.setType(AppUserBean.Type.User);
        }
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        AppUserBean lOldAppUserBean = null;
        String lPassword = null;
        //
        checkHasAccessToActions(pAppUserBean);
        //
        // check duplicate
        String lDuplicateSql = "SELECT * FROM AppUsers WHERE AURecordVersion>0 AND AUDomain=" + DBHelper.getInstance().formatString(pAppUserBean.getDomain())
                + " AND AULoginId = " + DBHelper.getInstance().formatString(pAppUserBean.getLoginId());
        if (!pNew)
            lDuplicateSql += " AND AUId != " + pAppUserBean.getId();
        AppUserBean lDuplicateBean = appUserDAO.findBean(lConnection, lDuplicateSql);
        if (lDuplicateBean != null)
            throw new CommonBusinessException("User with the given loginid already exists");
        if (AppUserBean.Type.Admin.equals(pAppUserBean.getType()) && (pAppUserBean.getMinUserLimit()!=null || pAppUserBean.getMaxUserLimit()!=null)){
        	throw new CommonBusinessException("Limits cannot be set for admin user");
        }
        if (pNew) {
        	pAppUserBean.setLoginId(pAppUserBean.getLoginId().trim());
            pAppUserBean.setRecordCreator(pUserBean.getId());
            lPassword = TredsHelper.getInstance().getGeneratedPassword();
            pAppUserBean.setPassword1(CommonUtilities.encryptSHA(lPassword));
            pAppUserBean.setPasswordUpdatedAt1(new Timestamp(System.currentTimeMillis()));
            pAppUserBean.setForcePasswordChange(YesNo.Yes);
            if(pAppUserBean.getDomain()!= null){
            	AppEntityBean lAppEntityBean =  TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
            	if(lAppEntityBean.isPurchaserAggregator()){
            		pAppUserBean.setEnableAPI(Yes.Yes);
            	}
            	if(!(lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier())){
            		pAppUserBean.setFullOwnership(null);
            	}
            }
            if (pAppUserBean.getMinUserLimit()!=null && pAppUserBean.getMaxUserLimit()!=null  && pAppUserBean.getMinUserLimit().compareTo(pAppUserBean.getMaxUserLimit()) > 0)
                throw new CommonBusinessException("Minimum limit should not be greater than maximum limit");
            appUserDAO.insert(lConnection, pAppUserBean);
            lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, pAppUserBean);
            appUserDAO.insertAudit(lConnection, pAppUserBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
            //
        } else {
            if (pAppUserBean.getId().equals(lLoggedInUser.getId()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            if (CommonAppConstants.Yes.Yes.equals(pAppUserBean.getResetPassword())){
                lPassword = TredsHelper.getInstance().getGeneratedPassword();
                pAppUserBean.setPassword1(CommonUtilities.encryptSHA(lPassword));
                pAppUserBean.setPasswordUpdatedAt1(new Timestamp(System.currentTimeMillis())); //to supress expiry message as per dhwanis request
            }
            pAppUserBean.setRecordUpdator(pUserBean.getId());
            //
            lOldAppUserBean = findBean(pExecutionContext, pAppUserBean, (AppUserBean)pUserBean);
            //this is done so that we can have hidden admin roles to some member user ie. admin
			//if non domain user is modifier - then from the previous bean find private roles and if found add them to the new bean
            if(!AppConstants.DOMAIN_PLATFORM.equals(lLoggedInUser.getDomain())){
            	if(lOldAppUserBean.getRmIdList()!=null && !lOldAppUserBean.getRmIds().equals(pAppUserBean.getRmIds())){
            		 List<RoleMasterBean> lRoleMasterBeans = getRoles(lOldAppUserBean.getDomain());
            		 ArrayList<Long> lAdminRoles = new ArrayList<Long>();
            		 for(RoleMasterBean lRoleMasterBean : lRoleMasterBeans){
            			 if(AppConstants.Owner.Private.getCode().equals(lRoleMasterBean.getOwner())){
            				 lAdminRoles.add(lRoleMasterBean.getId());
            			 }
            		 }
            		 if(lAdminRoles.size() > 0){
            			 List<Long> lNewRMIdList = new ArrayList<Long>();
            			 if(pAppUserBean.getRmIdList()!=null){
            				 lNewRMIdList = pAppUserBean.getRmIdList();
            			 }
            			 for(Long lRoleId : lAdminRoles){
            				 if(lNewRMIdList.contains(lRoleId)){
            					lNewRMIdList.add(lRoleId);
            				 }
            			 }
            			 pAppUserBean.setRmIdList(lNewRMIdList);
            		 }
            	}
            }
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
            lBeanMeta.copyBean(pAppUserBean, lOldAppUserBean, BeanMeta.FIELDGROUP_UPDATE, null);
            lMemoryDBConnection.deleteRow(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Id, pAppUserBean);
            if (pAppUserBean.getMinUserLimit()!=null && pAppUserBean.getMaxUserLimit()!=null  && pAppUserBean.getMinUserLimit().compareTo(pAppUserBean.getMaxUserLimit()) > 0)
                throw new CommonBusinessException("Minimum limit should not be greater than maximum limit");
            if (appUserDAO.update(lConnection, lOldAppUserBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);

            lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, lOldAppUserBean);
            appUserDAO.insertAudit(lConnection, lOldAppUserBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            pAppUserBean = lOldAppUserBean;
        }
        if(CommonUtilities.hasValue(lPassword)){
        	sendPasswordResetMail(pAppUserBean, lPassword);
        }
        //saving the Checker for Maker
        saveCheckers(pExecutionContext, pAppUserBean, pNew, pUserBean);
        pExecutionContext.commitAndDispose();
    }
    
    private void sendPasswordResetMail(AppUserBean pAppUserBean, String pNewPassword){
        // send email - send password  =requesting the user to login and change the same
        if (StringUtils.isNotBlank(pAppUserBean.getEmail())) {
            Map<String, Object> lDataValues = new HashMap<String, Object>();
            lDataValues.put(EmailSender.TO, pAppUserBean.getEmail());
            lDataValues.put("name", pAppUserBean.getName());
            lDataValues.put("password", pNewPassword);
            lDataValues.put("loginid", pAppUserBean.getLoginId());
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_CREATEPASSWORD, (HashMap)lDataValues);
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, AppUserBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        AppUserBean lAppUserBean = findBean(pExecutionContext, pFilterBean, (AppUserBean)pUserBean);
        lAppUserBean.setRecordUpdator(pUserBean.getId());
        appUserDAO.delete(lConnection, lAppUserBean);        

        pExecutionContext.commitAndDispose();
    }
    
    
    //Maker Checker Mapping functions.
    
    public void populateCheckers(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
		MakerCheckerMapBean lFilterBean = new MakerCheckerMapBean();
		lFilterBean.setMakerId(pAppUserBean.getId());
       List<MakerCheckerMapBean> lMakerCheckerMaps = makerCheckerMapDAO.findList(pConnection, lFilterBean, (String)null);
       
       for (MakerCheckerMapBean lMakerCheckerMapBean : lMakerCheckerMaps){
			switch(lMakerCheckerMapBean.getCheckerType()) {
			case Instrument:
			    if (pAppUserBean.getCheckersInstrument() == null)
			        pAppUserBean.setCheckersInstrument(new ArrayList<Long>());
			    pAppUserBean.getCheckersInstrument().add(lMakerCheckerMapBean.getCheckerId());
			    break;
            case Platform_Limit:
                if (pAppUserBean.getCheckersPlatformLimit() == null)
                    pAppUserBean.setCheckersPlatformLimit(new ArrayList<Long>());
                pAppUserBean.getCheckersPlatformLimit().add(lMakerCheckerMapBean.getCheckerId());
                break;
            case Buyer_Limit:
                if (pAppUserBean.getCheckersBuyerLimit() == null)
                    pAppUserBean.setCheckersBuyerLimit(new ArrayList<Long>());
                pAppUserBean.getCheckersBuyerLimit().add(lMakerCheckerMapBean.getCheckerId());
                break;
            case Buyer_Seller_Limit:
                if (pAppUserBean.getCheckersBuyerSellerLimit() == null)
                    pAppUserBean.setCheckersBuyerSellerLimit(new ArrayList<Long>());
                pAppUserBean.getCheckersBuyerSellerLimit().add(lMakerCheckerMapBean.getCheckerId());
                break;
            case User_Limit:
                if (pAppUserBean.getCheckersUserLimit() == null)
                    pAppUserBean.setCheckersUserLimit(new ArrayList<Long>());
                pAppUserBean.getCheckersUserLimit().add(lMakerCheckerMapBean.getCheckerId());
                break;
            case Bid:
                if (pAppUserBean.getCheckersBid() == null)
                    pAppUserBean.setCheckersBid(new ArrayList<Long>());
                pAppUserBean.getCheckersBid().add(lMakerCheckerMapBean.getCheckerId());
                break;
            case InstrumentCounter:
            	if (pAppUserBean.getCheckersInstrumentCounter() == null)
            		 pAppUserBean.setCheckersInstrumentCounter(new ArrayList<Long>());
                pAppUserBean.getCheckersInstrumentCounter().add(lMakerCheckerMapBean.getCheckerId());
                break;
			}
		}
    }
    

    public void validateCheckers(MemoryDBConnection pMemoryDBConnection, String pMakerDomain, Long pMakerId, List<Long> pCheckerIds) throws Exception {
    	if(pCheckerIds == null || pCheckerIds.size() == 0)
    		return ;
    	HashSet<Long> lCheckerIds = new HashSet<Long>();
    	if(pCheckerIds.contains(pMakerId))
    		throw new CommonBusinessException("Maker cannot assign self as checker.");
    	// check THE DOMAIN of the checkers to be same as domain of maker?
    	if(!CommonUtilities.hasValue(pMakerDomain))
    		throw new CommonBusinessException("Domain of Maker not specified.");
    	AppUserBean lAppUserBean = null;
    	for(Long lCheckerId : pCheckerIds)
    	{
    		if(lCheckerIds.contains(lCheckerId))
    			throw new CommonBusinessException("Duplicate checker in list.");
    		lCheckerIds.add(lCheckerId);
    		lAppUserBean = new AppUserBean();
    		lAppUserBean.setId(lCheckerId);
    		lAppUserBean = (AppUserBean) pMemoryDBConnection.selectSingleRow(AppUserBean.ENTITY_NAME, AppUserBean.f_Id, lAppUserBean);
    		if(!pMakerDomain.equals(lAppUserBean.getDomain()))
    			throw new CommonBusinessException("Maker Domain mismatch for Checker " + lAppUserBean.getLoginId() +".");
    	}
    }

    public boolean isValidChecker(Connection pConnection, Long pMakerId, Long pCheckerId, 
            MakerCheckerMapBean.CheckerType pCheckerType) throws Exception {
        MakerCheckerMapBean lFilterBean = new MakerCheckerMapBean();
        lFilterBean.setMakerId(pMakerId);
        lFilterBean.setCheckerId(pCheckerId);
        lFilterBean.setCheckerType(pCheckerType);
        return (makerCheckerMapDAO.findBean(pConnection, lFilterBean) != null);
    }
    
    public List<MakerCheckerMapBean> getCheckers(Connection pConnection, Long pMakerId, 
            MakerCheckerMapBean.CheckerType pCheckerType) throws Exception {
        if (pCheckerType == null)
            return null;
        MakerCheckerMapBean lFilterBean = new MakerCheckerMapBean();
        lFilterBean.setMakerId(pMakerId);
        lFilterBean.setCheckerType(pCheckerType);
        return makerCheckerMapDAO.findList(pConnection, lFilterBean, (String)null);
    }
    
    public void saveCheckers(ExecutionContext pExecutionContext, AppUserBean pAppUserBean, boolean pNew, 
            IAppUserBean pLoggedInUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        MakerCheckerMapBean.CheckerType lCheckerTypes[] = new MakerCheckerMapBean.CheckerType[] {
                MakerCheckerMapBean.CheckerType.Instrument, MakerCheckerMapBean.CheckerType.Platform_Limit, 
                MakerCheckerMapBean.CheckerType.Buyer_Limit, MakerCheckerMapBean.CheckerType.Buyer_Seller_Limit,
                MakerCheckerMapBean.CheckerType.User_Limit, MakerCheckerMapBean.CheckerType.Bid ,MakerCheckerMapBean.CheckerType.InstrumentCounter
        };
        List<Long>[] lNewCheckers = new List[]{pAppUserBean.getCheckersInstrument(), pAppUserBean.getCheckersPlatformLimit(),
                pAppUserBean.getCheckersBuyerLimit(), pAppUserBean.getCheckersBuyerSellerLimit(),
                pAppUserBean.getCheckersUserLimit(), pAppUserBean.getCheckersBid(), pAppUserBean.getCheckersInstrumentCounter()};
        Map<MakerCheckerMapBean.CheckerType, Map<Long, MakerCheckerMapBean>> lOldCheckers = new HashMap<MakerCheckerMapBean.CheckerType, Map<Long,MakerCheckerMapBean>>(); 
        
        // get old checker map values
        MakerCheckerMapBean lFilterBean = new MakerCheckerMapBean();
        lFilterBean.setMakerId(pAppUserBean.getId());
        List<MakerCheckerMapBean> lMakerCheckerMaps = makerCheckerMapDAO.findList(lConnection, lFilterBean, (String)null);
        for (MakerCheckerMapBean lMakerCheckerMapBean : lMakerCheckerMaps) {
            Map<Long, MakerCheckerMapBean> lCheckerMap = lOldCheckers.get(lMakerCheckerMapBean.getCheckerType());
            if (lCheckerMap == null) {
                lCheckerMap = new HashMap<Long, MakerCheckerMapBean>();
                lOldCheckers.put(lMakerCheckerMapBean.getCheckerType(), lCheckerMap);
            }
            lCheckerMap.put(lMakerCheckerMapBean.getCheckerId(), lMakerCheckerMapBean);
        }

        for (int lPtr=0;lPtr<lCheckerTypes.length;lPtr++) {
            validateCheckers(pExecutionContext.getMemoryDBConnection(), pAppUserBean.getDomain(), pAppUserBean.getId(), lNewCheckers[lPtr]);
            Map<Long, MakerCheckerMapBean> lCheckerMap = lOldCheckers.get(lCheckerTypes[lPtr]);
            List<Long> lAddCheckerIds = new ArrayList<Long>();
            
            if (lNewCheckers[lPtr] != null) {
                for (Long lCheckerId : lNewCheckers[lPtr]) {
                    if ((lCheckerMap == null) || (lCheckerMap.remove(lCheckerId) == null))
                        lAddCheckerIds.add(lCheckerId);
                }
            }
            
            MakerCheckerMapBean lMCMapBean = null;      
            for(Long lCheckerId : lAddCheckerIds) {
                lMCMapBean = new MakerCheckerMapBean();
                lMCMapBean.setMakerId(pAppUserBean.getId());
                lMCMapBean.setCheckerType(lCheckerTypes[lPtr]);
                lMCMapBean.setCheckerId(lCheckerId);
                lMCMapBean.setRecordCreator(pLoggedInUserBean.getId());
                makerCheckerMapDAO.insert(lConnection, lMCMapBean);
            }
            if (lCheckerMap != null) {
                for (MakerCheckerMapBean lMCBean : lCheckerMap.values()) {
                    lMCBean.setRecordUpdator(pLoggedInUserBean.getId());
                    makerCheckerMapDAO.delete(lConnection, lMCBean);
                }
            }
        }
    }
    
    public void reset2FA(ExecutionContext pExecutionContext, Long pId, AppUserBean pUserBean) throws Exception {
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("id", pId);
        IAppUserBean lAppUserBean = AuthenticationHandler.getInstance().getUserDataSource().getUserBean(pExecutionContext, pId);
        if (!pUserBean.getDomain().equals(AppConstants.DOMAIN_PLATFORM)) {
            if (pUserBean.getType() == AppUserBean.Type.Admin) {
                if (!pUserBean.getDomain().equals(lAppUserBean.getDomain()))
                    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            } else if (!pId.equals(pUserBean.getId()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AuthenticationHandler.getInstance().updateSecuritySettings(pExecutionContext, lAppUserBean, lMap);
    }
    
    public void toggleEnableAPI(ExecutionContext pExecutionContext, Long pId, AppUserBean pUserBean) throws Exception {
        if (!pUserBean.getDomain().equals(AppConstants.DOMAIN_PLATFORM)) {
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        AppUserBean lAppUserBean = new AppUserBean();
        lAppUserBean.setId(pId);
        lAppUserBean = appUserDAO.findByPrimaryKey(pExecutionContext.getConnection(), lAppUserBean);
        if (lAppUserBean == null)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        
        if (lAppUserBean.getEnableAPI() == null)
            lAppUserBean.setEnableAPI(CommonAppConstants.Yes.Yes);
        else
            lAppUserBean.setEnableAPI(null);
        pExecutionContext.setAutoCommit(false);
        appUserDAO.update(pExecutionContext.getConnection(), lAppUserBean, AppUserBean.FIELDGROUP_UPDATEENABLEAPI);
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppUserBean.ENTITY_NAME, AppUserBean.f_Id, lAppUserBean);
        lMemoryDBConnection.addRow(AppUserBean.ENTITY_NAME, lAppUserBean);
        pExecutionContext.commitAndDispose();
    }
    public List<RoleMasterBean> getRoles(ExecutionContext pExecutionContext, String pDomain) throws Exception {
        return getRoles(pDomain);
    }
    
    private List<RoleMasterBean> getRoles(String pDomain) throws Exception {
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pDomain});
            if (lAppEntityBean != null)
                return AccessControlHelper.getInstance().getRoleList((String[])lAppEntityBean.getEntityTypes().toArray(new String[0]));
        } catch (Exception lException) {
        }
        return null;
    }
    
    public String getChecherLevel(String pCheckerType,AppUserBean pAppUserBean) {
    	CheckerType lCheckerType =  (CheckerType) TredsHelper.getInstance().getValue(MakerCheckerMapBean.class,"checkerType",pCheckerType);
		switch(lCheckerType) {
		case Instrument:
			if (pAppUserBean.getInstLevel()!=null && pAppUserBean.getInstLevel()>0) {
				return "( Level "+pAppUserBean.getInstLevel()+" )";
			}
		    break;
        case Platform_Limit:
        	if (pAppUserBean.getPlatformLimitLevel()!=null && pAppUserBean.getPlatformLimitLevel()>0) {
				return "( Level "+pAppUserBean.getPlatformLimitLevel()+" )";
			}
            break;
        case Buyer_Limit:
        	if (pAppUserBean.getBuyerLimitLevel()!=null && pAppUserBean.getBuyerLimitLevel()>0) {
				return "( Level "+pAppUserBean.getBuyerLimitLevel()+" )";
			}
            break;
        case Buyer_Seller_Limit:
        	if (pAppUserBean.getBuyerSellerLimitLevel()!=null && pAppUserBean.getBuyerSellerLimitLevel()>0) {
				return "( Level "+pAppUserBean.getBuyerSellerLimitLevel()+" )";
			}
            break;
        case User_Limit:
        	if (pAppUserBean.getUserLimitLevel()!=null && pAppUserBean.getUserLimitLevel()>0) {
				return "( Level "+pAppUserBean.getUserLimitLevel()+" )";
			}
            break;
        case Bid:
        	if (pAppUserBean.getBidLevel()!=null && pAppUserBean.getBidLevel()>0) {
				return "( Level "+pAppUserBean.getBidLevel()+" )";
			}
            break;
        case InstrumentCounter:
        	if (pAppUserBean.getInstCntrLevel()!=null && pAppUserBean.getInstCntrLevel()>0) {
				return "( Level "+pAppUserBean.getInstCntrLevel()+" )";
			}
        	break;
		}
		return null;
    }
    
    public void checkHasAccessToActions(AppUserBean pAppUserBean) throws Exception {
    	if (pAppUserBean.getCheckersInstrument()!=null && !pAppUserBean.getCheckersInstrument().isEmpty()) {
    		if ( !( AccessControlHelper.getInstance().hasAccess("inst-save", pAppUserBean) ||
    				AccessControlHelper.getInstance().hasAccess("inst-status", pAppUserBean)) ) {
        		throw new CommonBusinessException(" Instruments checkers cannot be set as role not defined. ");
    		}
    	}
    	if (pAppUserBean.getCheckersInstrumentCounter()!=null && !pAppUserBean.getCheckersInstrumentCounter().isEmpty()) {
    		if (!AccessControlHelper.getInstance().hasAccess("instcntr-approve", pAppUserBean)) {
        		throw new CommonBusinessException(" Instruments counter checkers cannot be set as role not defined. ");
        	}
    	}
    }
}
