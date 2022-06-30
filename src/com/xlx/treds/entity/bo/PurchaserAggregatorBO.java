package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AggregatorPurchaserMapBean;
import com.xlx.treds.entity.bean.AppEntityBean;

public class PurchaserAggregatorBO {
    
    private GenericDAO<AppEntityBean> appEntityDAO;
    private GenericDAO<AggregatorPurchaserMapBean> aggregatorPurchaserMapBeanDAO;

    public PurchaserAggregatorBO() {
        super();
        appEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        aggregatorPurchaserMapBeanDAO = new GenericDAO<AggregatorPurchaserMapBean>(AggregatorPurchaserMapBean.class);
    }
    
    public AppEntityBean findBean(ExecutionContext pExecutionContext, 
        AppEntityBean pFilterBean) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	
    	if(pFilterBean==null){
    		pFilterBean = new AppEntityBean();
    	}
    	pFilterBean.setType("AAA");
    	pFilterBean.setCdId(new Long(0));
    	
        AppEntityBean lAppEntityBean = appEntityDAO.findByPrimaryKey(lConnection, pFilterBean);
        if (lAppEntityBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        //
        lAppEntityBean.setPurchaserList(getPurchasersList(lConnection, lAppEntityBean.getCode()));
        return lAppEntityBean;
    }
    
    public AppEntityBean findBean(Connection pConnection, 
            AppEntityBean pFilterBean) throws Exception {
                
                if(pFilterBean==null){
                        pFilterBean = new AppEntityBean();
                }
                pFilterBean.setType("AAA");
                pFilterBean.setCdId(new Long(0));
                
            AppEntityBean lAppEntityBean = appEntityDAO.findByPrimaryKey(pConnection, pFilterBean);
            if (lAppEntityBean == null) 
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            //
            lAppEntityBean.setPurchaserList(getPurchasersList(pConnection, lAppEntityBean.getCode()));
            return lAppEntityBean;
     }

    
    public List<AppEntityBean> findList(ExecutionContext pExecutionContext, AppEntityBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	if(pFilterBean==null){
    		pFilterBean = new AppEntityBean();
    	}
    	pFilterBean.setType("AAA");
    	pFilterBean.setCdId(new Long(0));
        return appEntityDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, AppEntityBean pAppEntityBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AppEntityBean lOldAppEntityBean = null;
        List<String> lNewPurchaserList = pAppEntityBean.getPurchaserList();
        if (pNew) {
        	if(StringUtils.isEmpty(pAppEntityBean.getName())){
        		throw new CommonBusinessException("Name not specified.");
        	}
        	pAppEntityBean.setType("AAA");
        	//GENERATE THE CODE
        	pAppEntityBean.setCode(TredsHelper.getInstance().createCompanyOrEntityCode(lConnection, pAppEntityBean.getName()));
        	// always 0 for aggregator
        	pAppEntityBean.setCdId(new Long(0));
        	pAppEntityBean.setRecordCreator(pUserBean.getId());
            appEntityDAO.insert(lConnection, pAppEntityBean,AppEntityBean.FIELDGROUP_INSERTPURCHASERAGGERGATOR);
            appEntityDAO.insertAudit(lConnection, pAppEntityBean, AuditAction.Insert, pUserBean.getId());
            lOldAppEntityBean = pAppEntityBean;
        } else {
            lOldAppEntityBean = findBean(pExecutionContext, pAppEntityBean);
            appEntityDAO.getBeanMeta().copyBean(pAppEntityBean, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEPURCHASERAGGERGATOR, null);
            lOldAppEntityBean.setRecordUpdator(pUserBean.getId());
            if (appEntityDAO.update(lConnection, lOldAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEPURCHASERAGGERGATOR) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            appEntityDAO.insertAudit(lConnection, lOldAppEntityBean, AuditAction.Update, pUserBean.getId());
        }
      //saving of purchaser mappings
        //get the old list and hash it
        //loop throught the received list
        //if not in the old hash then insert else remove from old hash
        //after loop check old hash delete all contents.
        HashMap<String,AggregatorPurchaserMapBean> lOldPurchaserAggMapBeans = new HashMap<String, AggregatorPurchaserMapBean>();
        List<String> lNewPurchasers = new ArrayList<String>();
        String lNewPurchaser = null;
        if(lNewPurchaserList!=null){
            for(String lPur : lNewPurchaserList){
            	lNewPurchasers.add(lPur);
            }
        }
        if(!pNew){
        	List<AggregatorPurchaserMapBean> lTmpList = getPurchaserAggBeanList(lConnection, pAppEntityBean.getCode());
        	if (lTmpList != null){
        		for(AggregatorPurchaserMapBean lBean : lTmpList){
        			lOldPurchaserAggMapBeans.put(lBean.getPurchaser(), lBean);
        		}
        	}
        }
        AggregatorPurchaserMapBean lAggPurBean = null;
        for(int lPtr=0; lPtr< lNewPurchasers.size(); lPtr++){
        	lNewPurchaser = lNewPurchasers.get(lPtr);
        	if(lOldPurchaserAggMapBeans.containsKey(lNewPurchaser)){
        		lOldPurchaserAggMapBeans.remove(lNewPurchaser);
        	}else{
        		lAggPurBean = new AggregatorPurchaserMapBean();
        		lAggPurBean.setAggregator(pAppEntityBean.getCode());
        		lAggPurBean.setPurchaser(lNewPurchaser);
        		lAggPurBean.setRecordCreator(pUserBean.getId());
        		aggregatorPurchaserMapBeanDAO.insert(lConnection, lAggPurBean);
        	}
        }
        if (lOldPurchaserAggMapBeans.size()>0){
        	for(AggregatorPurchaserMapBean lBean : lOldPurchaserAggMapBeans.values()){
	        	aggregatorPurchaserMapBeanDAO.delete(lConnection, lBean);
        	}
        }
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
        
    	List<AggregatorPurchaserMapBean> lTmpList = getPurchaserAggBeanList(lConnection, lAppEntityBean.getCode());
        if (lTmpList!= null){
        	for(AggregatorPurchaserMapBean lBean : lTmpList){
	        	aggregatorPurchaserMapBeanDAO.delete(lConnection, lBean);
        	}
        }
        pExecutionContext.commitAndDispose();
    }
    
    private List<String> getPurchasersList(Connection lConnection,String pCode) throws Exception{
        List<String> lPurchaserList = new ArrayList<String>();
    	List<AggregatorPurchaserMapBean> lMapList = getPurchaserAggBeanList(lConnection, pCode);
    	if(lMapList!= null){
    		for(AggregatorPurchaserMapBean lBean : lMapList){
            	lPurchaserList.add(lBean.getPurchaser());
    		}
    	}
    	return lPurchaserList;
    }
    
    private List<AggregatorPurchaserMapBean> getPurchaserAggBeanList(Connection lConnection,String pCode) throws Exception{
    	AggregatorPurchaserMapBean lFilterBean = new AggregatorPurchaserMapBean();
    	lFilterBean.setAggregator(pCode);
     	return aggregatorPurchaserMapBeanDAO.findList(lConnection, lFilterBean, new ArrayList<String>());
    }
    
    public void validateMappedEntity(Connection pConnection, String pAggregatorEntity, String pPurchaserEntity) throws Exception {
		AppEntityBean lAggregatorEntityBean = TredsHelper.getInstance().getAppEntityBean(pAggregatorEntity);
		List<String> lPurchasers = lAggregatorEntityBean.getPurchaserList();
		if (lPurchasers == null || !lPurchasers.contains(pPurchaserEntity)) {
            PurchaserAggregatorBO lPurchaserAggregatorBO = new PurchaserAggregatorBO();
            AppEntityBean lPurchaserAggregatorEntityBean = lPurchaserAggregatorBO.findBean(pConnection, lAggregatorEntityBean);
            if(lPurchaserAggregatorEntityBean.getPurchaserList() == null || (lPurchaserAggregatorEntityBean.getPurchaserList()!=null && 
                            !lPurchaserAggregatorEntityBean.getPurchaserList().contains(pPurchaserEntity))) {
                    throw new CommonBusinessException("Purchaser not mapped to aggregator.");
            }
		}
    }
    
}
