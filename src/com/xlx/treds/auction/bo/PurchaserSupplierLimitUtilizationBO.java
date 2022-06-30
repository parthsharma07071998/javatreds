package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLimitUtilizationBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

public class PurchaserSupplierLimitUtilizationBO {
    
    private GenericDAO<PurchaserSupplierLimitUtilizationBean> purchaserSupplierLimitUtilizationDAO;

    public PurchaserSupplierLimitUtilizationBO() {
        super();
        purchaserSupplierLimitUtilizationDAO = new GenericDAO<PurchaserSupplierLimitUtilizationBean>(PurchaserSupplierLimitUtilizationBean.class);
    }
    
    public PurchaserSupplierLimitUtilizationBean findBean(ExecutionContext pExecutionContext, 
        PurchaserSupplierLimitUtilizationBean pFilterBean, IAppUserBean pUserBean) throws Exception {
        PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean = purchaserSupplierLimitUtilizationDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPurchaserSupplierLimitUtilizationBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        if (!lPurchaserSupplierLimitUtilizationBean.getPurchaser().equals(pUserBean.getDomain()))
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
  
        return lPurchaserSupplierLimitUtilizationBean;
    }
    
    public List<PurchaserSupplierLimitUtilizationBean> findList(ExecutionContext pExecutionContext, PurchaserSupplierLimitUtilizationBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	List<PurchaserSupplierLimitUtilizationBean> lList = null;
    	pFilterBean.setPurchaser(pUserBean.getDomain());
        lList = purchaserSupplierLimitUtilizationDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
        if(lList!=null && lList.size() > 0){
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = null;
        	for(PurchaserSupplierLimitUtilizationBean lBean : lList){
                lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lBean.getSupplier()});
                lBean.setSupName(lAppEntityBean.getName());
        	}
        }
        return lList;
    }
    
    public void save(ExecutionContext pExecutionContext, PurchaserSupplierLimitUtilizationBean pPurchaserSupplierLimitUtilizationBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierLimitUtilizationBean lOldPSLimitUtilizationBean = null;
        AppEntityBean lAppEntityBean = null;
        if (pUserBean != null) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
            if(!lAppEntityBean.isPurchaser())
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        if (pNew) {
        	if(pPurchaserSupplierLimitUtilizationBean.getPurchaser()==null)
        		pPurchaserSupplierLimitUtilizationBean.setPurchaser(lAppEntityBean.getCode());
        	else if (!lAppEntityBean.getCode().equals(pPurchaserSupplierLimitUtilizationBean.getPurchaser()))
            	throw new CommonBusinessException("Purchaser admin can modify self utilization.");
        }
        if (pNew) {
        	PurchaserSupplierLimitUtilizationBean lFilterBean = new PurchaserSupplierLimitUtilizationBean();
        	lFilterBean.setPurchaser(lAppEntityBean.getCode());
        	lFilterBean.setSupplier(pPurchaserSupplierLimitUtilizationBean.getSupplier());
            lOldPSLimitUtilizationBean = purchaserSupplierLimitUtilizationDAO.findBean(lConnection, lFilterBean);
        	if(lOldPSLimitUtilizationBean!=null && lOldPSLimitUtilizationBean.getId()!=null)
        		throw new CommonBusinessException("Buyer-Seller Limit Utilization already exits.");
        	pPurchaserSupplierLimitUtilizationBean.setPurchaser(lAppEntityBean.getCode());
        	pPurchaserSupplierLimitUtilizationBean.setLimitUtilized(new BigDecimal(0));
            pPurchaserSupplierLimitUtilizationBean.setRecordCreator(pUserBean.getId());
            purchaserSupplierLimitUtilizationDAO.insert(lConnection, pPurchaserSupplierLimitUtilizationBean);
        } else {
            lOldPSLimitUtilizationBean = findBean(pExecutionContext, pPurchaserSupplierLimitUtilizationBean, pUserBean);
            if(lOldPSLimitUtilizationBean!=null && !lOldPSLimitUtilizationBean.getPurchaser().equals(lAppEntityBean.getCode()))
            	throw new CommonBusinessException("Purchaser admin can modify self utilization.");
            pPurchaserSupplierLimitUtilizationBean.setRecordUpdator(pUserBean.getId());
            if (purchaserSupplierLimitUtilizationDAO.update(lConnection, pPurchaserSupplierLimitUtilizationBean,BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, PurchaserSupplierLimitUtilizationBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        PurchaserSupplierLimitUtilizationBean lPurchaserSupplierLimitUtilizationBean = findBean(pExecutionContext, pFilterBean, pUserBean);
        lPurchaserSupplierLimitUtilizationBean.setRecordUpdator(pUserBean.getId());
        purchaserSupplierLimitUtilizationDAO.delete(lConnection, lPurchaserSupplierLimitUtilizationBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public BigDecimal updatePSLimitUtilization(Connection pConnection, String pPurchaser, String pSupplier, AppUserBean pAppUserBean, 
            BigDecimal pLimit, boolean pReleaseLimit) throws Exception{
    	if(CommonUtilities.hasValue(pPurchaser) && CommonUtilities.hasValue(pSupplier)){
        	List<PurchaserSupplierLimitUtilizationBean> lList = null;
        	PurchaserSupplierLimitUtilizationBean lFilterBean = new PurchaserSupplierLimitUtilizationBean();
        	lFilterBean.setSupplier(pSupplier);
        	lFilterBean.setPurchaser(pPurchaser);
        	PurchaserSupplierLimitUtilizationBean lPSLimitUtilBean = purchaserSupplierLimitUtilizationDAO.findBean(pConnection, lFilterBean);
            if (lPSLimitUtilBean != null) {
                if(pReleaseLimit){
                    //Release Limit
                    BigDecimal lNewLimitUtilized = new BigDecimal(0);
                    if(lPSLimitUtilBean.getLimitUtilized()!=null)
                        lNewLimitUtilized = lPSLimitUtilBean.getLimitUtilized();
                    lNewLimitUtilized = lNewLimitUtilized.subtract(pLimit);
                    lPSLimitUtilBean.setLimitUtilized(lNewLimitUtilized);
                    lPSLimitUtilBean.setRecordUpdator((pAppUserBean!=null?pAppUserBean.getId():new Long(0)));
                    purchaserSupplierLimitUtilizationDAO.update(pConnection, lPSLimitUtilBean, PurchaserSupplierLimitUtilizationBean.FIELDGROUP_UPDATESTATUS);
                } else {
                    if (PurchaserSupplierLimitUtilizationBean.Status.Active.equals(lPSLimitUtilBean.getStatus())) {
                        //Block Limit
                        BigDecimal lOldLimitUtilised = new BigDecimal(0);
                        if(lPSLimitUtilBean.getLimitUtilized()!=null) lOldLimitUtilised = lPSLimitUtilBean.getLimitUtilized();
                        BigDecimal lBalanceLimit = new BigDecimal(0);
                        if(lPSLimitUtilBean.getLimit()!=null) lBalanceLimit = lPSLimitUtilBean.getLimit();
                        lBalanceLimit = lBalanceLimit.subtract(lOldLimitUtilised);
                        lBalanceLimit = lBalanceLimit.subtract(pLimit); //block/reduce
                        if(lBalanceLimit.doubleValue() >= 0){
                            lPSLimitUtilBean.setLimitUtilized(lOldLimitUtilised.add(pLimit));
                            lPSLimitUtilBean.setRecordUpdator((pAppUserBean!=null?pAppUserBean.getId():new Long(0)));
                            purchaserSupplierLimitUtilizationDAO.update(pConnection, lPSLimitUtilBean, PurchaserSupplierLimitUtilizationBean.FIELDGROUP_UPDATESTATUS);
                            return pLimit;
                        } else {
                            throw new CommonBusinessException("Buyer-Seller Limit deficit by " + lBalanceLimit + ".");
                        }
                    }
                }
            }
    	}
    	return null;
    }
    
}
