
package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.PlatformStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierRelationshipHistoryBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class PurchaserSupplierRelationshipHistoryBO {

	public static final Logger logger = LoggerFactory.getLogger(PurchaserSupplierRelationshipHistoryBO.class);
    
    private GenericDAO<PurchaserSupplierRelationshipHistoryBean> purchaserSupplierRelationshipHistoryDAO;
    private BeanMeta purSupRelBeanMeta;
    private static String FIELGROUP_UPDATECHANGEINRELATION= "updateChangeInRelation";

    public PurchaserSupplierRelationshipHistoryBO() {
        super();
        purchaserSupplierRelationshipHistoryDAO = new GenericDAO<PurchaserSupplierRelationshipHistoryBean>(PurchaserSupplierRelationshipHistoryBean.class);
        purSupRelBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierRelationshipHistoryBean.class);
    }
    
    public PurchaserSupplierRelationshipHistoryBean findBean(ExecutionContext pExecutionContext, 
        PurchaserSupplierRelationshipHistoryBean pFilterBean) throws Exception {
        PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean = purchaserSupplierRelationshipHistoryDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPurchaserSupplierRelationshipHistoryBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lPurchaserSupplierRelationshipHistoryBean;
    }
    
    public List<PurchaserSupplierRelationshipHistoryBean> findList(ExecutionContext pExecutionContext, PurchaserSupplierRelationshipHistoryBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return purchaserSupplierRelationshipHistoryDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, PurchaserSupplierRelationshipHistoryBean pPurchaserSupplierRelationshipHistoryBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierRelationshipHistoryBean lOldPurchaserSupplierRelationshipHistoryBean = null;
        if (pNew) {

            pPurchaserSupplierRelationshipHistoryBean.setRecordCreator(pUserBean.getId());
            purchaserSupplierRelationshipHistoryDAO.insert(lConnection, pPurchaserSupplierRelationshipHistoryBean);
        } else {
            lOldPurchaserSupplierRelationshipHistoryBean = findBean(pExecutionContext, pPurchaserSupplierRelationshipHistoryBean);
            

            pPurchaserSupplierRelationshipHistoryBean.setRecordUpdator(pUserBean.getId());
            if (purchaserSupplierRelationshipHistoryDAO.update(lConnection, pPurchaserSupplierRelationshipHistoryBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, PurchaserSupplierRelationshipHistoryBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        PurchaserSupplierRelationshipHistoryBean lPurchaserSupplierRelationshipHistoryBean = findBean(pExecutionContext, pFilterBean);
        lPurchaserSupplierRelationshipHistoryBean.setRecordUpdator(pUserBean.getId());
        purchaserSupplierRelationshipHistoryDAO.delete(lConnection, lPurchaserSupplierRelationshipHistoryBean);        


        pExecutionContext.commitAndDispose();
    }

	public String getRelationship(ExecutionContext pExecutionContext,
			Map<String, Object> pMap, IAppUserBean pUserBean) throws Exception {
        PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
        List<Map<String, Object>> lResultList = new ArrayList<Map<String, Object>>(); 
        Map<String , Object> lReturnMap = new HashMap<String,Object>();
        Map<String, Object> lMap = null;
        if(pMap == null || (StringUtils.isEmpty((String)pMap.get("purchaser")) && StringUtils.isEmpty((String)pMap.get("supplier"))) ){
        	throw new CommonBusinessException("Purchaser and Supplier required.");
        }else{
        	if(StringUtils.isEmpty((String)pMap.get("purchaser"))){
            	throw new CommonBusinessException("Purchaser required.");
        	}
        	if(StringUtils.isEmpty((String)pMap.get("supplier"))){
            	throw new CommonBusinessException("Supplier required.");
        	}
        }
        if(pMap != null){
        	lFilterBean.setPurchaser(pMap.get("purchaser").toString());
        	lFilterBean.setSupplier(pMap.get("supplier").toString());
        }
		List<PurchaserSupplierRelationshipHistoryBean> lPurSupRelBeans = purchaserSupplierRelationshipHistoryDAO.findList(pExecutionContext.getConnection(), lFilterBean);
		if((lPurSupRelBeans != null) && (!lPurSupRelBeans.isEmpty())){
			for(PurchaserSupplierRelationshipHistoryBean lBean : lPurSupRelBeans){
				lMap = new HashMap<String, Object>();
				lMap.put("id", lBean.getId());
//				lMap.put("startDate", new Timestamp(=ysyslBean.getStartDate()));
//				lMap.put("endDate", lBean.getEndDate());
				lMap.put("startDate", FormatHelper.getDisplay("dd-MM-YYYY",lBean.getStartDate()));
//				lMap.put("endDate", FormatHelper.getDisplay("dd-MM-YYYY",lBean.getEndDate()));
				lResultList.add(lMap);
			}

		}else{
			throw new CommonBusinessException("Relationship not found for buyer "+pMap.get("purchaser").toString()+" and supplier "+pMap.get("supplier").toString());
		}
		lReturnMap.put("purchaser", pMap.get("purchaser").toString());
		lReturnMap.put("supplier", pMap.get("supplier").toString());
		lReturnMap.put("pursupRelation", lResultList);
		
		return new JsonBuilder(lReturnMap).toString();
	}

	public void changeRelation(ExecutionContext pExecutionContext, Map<String, Object> pMap, IAppUserBean pUserBean) throws Exception {
    	Long lId = new Long(pMap.get("id").toString());
    	Date lStartDate = FormatHelper.getDate(pMap.get("startDate").toString(), AppConstants.DATE_FORMAT);
    	Date lEndDate = FormatHelper.getDate(pMap.get("endDate").toString(), AppConstants.DATE_FORMAT);
    	if(lStartDate.compareTo(lEndDate) > 0){
    		throw new CommonBusinessException("End date cannot be greater than start date. ");
    	}
		PurchaserSupplierRelationshipHistoryBean lFilterBean = new PurchaserSupplierRelationshipHistoryBean();
    	lFilterBean.setId(lId);
//    	lFilterBean.setStartDate(lStartDate);
//    	lFilterBean.setEndDate(lEndDate);
    	PurchaserSupplierRelationshipHistoryBean lPurSupRelBean = purchaserSupplierRelationshipHistoryDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
    	if(lPurSupRelBean != null){
    		lPurSupRelBean.setStartDate(lStartDate);
//    		lPurSupRelBean.setEndDate(lEndDate);
    		purchaserSupplierRelationshipHistoryDAO.update(pExecutionContext.getConnection(), lPurSupRelBean, FIELGROUP_UPDATECHANGEINRELATION);
    	}else{
    		throw new CommonBusinessException(" Record not found. ");
    	}
	}

	public void addRelation(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
    	String lPurchaser = pMap.get("purchaser").toString();
    	String lSupplier = pMap.get("supplier").toString();
    	Date lStartDate = FormatHelper.getDate(pMap.get("startDate").toString(), AppConstants.DATE_FORMAT);
    	Date lEndDate = FormatHelper.getDate(pMap.get("endDate").toString(), AppConstants.DATE_FORMAT);
    	if(lStartDate.compareTo(CommonUtilities.getCurrentDate()) > 0){
    		throw new CommonBusinessException("Start date cannot be future date. ");
    	}
    	if(lEndDate.compareTo(CommonUtilities.getCurrentDate()) < 0){
    		throw new CommonBusinessException("End date cannot be past date. ");
    	}
    	if(lStartDate.compareTo(lEndDate) > 0){
    		throw new CommonBusinessException("End date cannot be greater than start date. ");
    	}
		PurchaserSupplierRelationshipHistoryBean lPurSupRelBean = new PurchaserSupplierRelationshipHistoryBean();
		lPurSupRelBean.setPurchaser(lPurchaser);
		lPurSupRelBean.setSupplier(lSupplier);
		lPurSupRelBean.setStartDate(lStartDate);
//		lPurSupRelBean.setEndDate(lEndDate);
		lPurSupRelBean.setRecordCreator(pUserBean.getId());
		lPurSupRelBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
    	purchaserSupplierRelationshipHistoryDAO.insert(pExecutionContext.getConnection(), lPurSupRelBean);
	}

	public void removeRelation(ExecutionContext pExecutionContext,
			PurchaserSupplierRelationshipHistoryBean pFilterBean, AppUserBean pUserBean) throws Exception {
        PurchaserSupplierRelationshipHistoryBean lPurSupRelBean = findBean(pExecutionContext, pFilterBean);
        lPurSupRelBean.setRecordUpdator(pUserBean.getId());
        purchaserSupplierRelationshipHistoryDAO.delete(pExecutionContext.getConnection(), lPurSupRelBean); 
	}

	public void updateRelation(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pUserBean) {
		PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
		lFilterBean.setPurchaser(pMap.get("purchaser").toString());
		lFilterBean.setSupplier(pMap.get("supplier").toString());
		if(pMap.get("platformStatus").toString().equals(PlatformStatus.Active.toString())){
			lFilterBean.setPlatformStatus(PlatformStatus.Active);
		}else{
			lFilterBean.setPlatformStatus(PlatformStatus.Suspended);
		}
	}
	
	public Map<String, Object> getPurSupRelHistory(Connection pConnection,PurchaserSupplierRelationshipHistoryBean lPSRHBean, AppUserBean lUserBean) throws Exception{
		//TODO: CHANGE THE RETRIVAL LOGIC
		Map<String, Object> lResultMap = new HashMap<String, Object>();
		List<Map<String, Object>> lResultList = new ArrayList<Map<String, Object>>();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM PURSUPRELATIONSHIPHISTORY ");
		lSql.append(" WHERE PSRRECORDVERSION > 0 ");
		lSql.append(" AND PSRPURCHASER = ").append(DBHelper.getInstance().formatString(lPSRHBean.getPurchaser()));
		lSql.append(" AND  PSRSUPPLIER = ").append(DBHelper.getInstance().formatString(lPSRHBean.getSupplier()));
		lSql.append(" ORDER BY PSRSTARTDATE DESC ");
		List<PurchaserSupplierRelationshipHistoryBean> lPSRHBeanList =  purchaserSupplierRelationshipHistoryDAO.findListFromSql(pConnection, lSql.toString(), -1);
		if(lPSRHBeanList != null && !lPSRHBeanList.isEmpty()){
			Map<String, Object> lMap = null;
			for (PurchaserSupplierRelationshipHistoryBean lBean : lPSRHBeanList) {
				lMap = new HashMap<String, Object>();
				if(lBean.getRelationFlag() != null){
					lMap.put("relationFlag", lBean.getRelationFlag());
				}
				if(lBean.getStartDate() != null){
					lMap.put("startDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lBean.getStartDate()));
				}
				if(lBean.getRelationDocName() != null){
					lMap.put("relationDocName", (lBean.getRelationDocName()==null?"":lBean.getRelationDocName().split("\\.")[1]+"."+lBean.getRelationDocName().split("\\.")[2]));
				}
				lResultList.add(lMap);
			}
		}else{
			return null;
		}
		lResultMap.put("relationshipHistory", lResultList);
		return lResultMap;
	}
    
}
