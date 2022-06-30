package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.PurchaserSupplierCapRateBean;
import com.xlx.treds.entity.bean.AppEntityBean;

public class PurchaserSupplierCapRateBO {
    
    private GenericDAO<PurchaserSupplierCapRateBean> purchaserSupplierCapRateDAO;
    public static final String CAPRATEDATA_COUNTER_ENTITY = "counter";
    public static final String CAPRATEDATA_COLUMNS = "cols";
    public static final String CAPRATEDATA_ROWS = "rows";
    public static final String CAPRATEDATA_DATA_CAPRATES = "data";
    public static final String CAPRATEKEY_FROMHAIRCUT = "fromHaircut";
    public static final String CAPRATEKEY_TOHAIRCUT = "toHaircut";
    public static final String CAPRATEKEY_FROMUSANCE = "fromUsance";
    public static final String CAPRATEKEY_TOUSANCE = "toUsance";

    public PurchaserSupplierCapRateBO() {
        super();
        purchaserSupplierCapRateDAO = new GenericDAO<PurchaserSupplierCapRateBean>(PurchaserSupplierCapRateBean.class);
    }
    
    public PurchaserSupplierCapRateBean findBean(ExecutionContext pExecutionContext, 
        PurchaserSupplierCapRateBean pFilterBean) throws Exception {
        PurchaserSupplierCapRateBean lPurchaserSupplierCapRateBean = purchaserSupplierCapRateDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPurchaserSupplierCapRateBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lPurchaserSupplierCapRateBean;
    }
    
    public List<PurchaserSupplierCapRateBean> findList(ExecutionContext pExecutionContext, PurchaserSupplierCapRateBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return purchaserSupplierCapRateDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, PurchaserSupplierCapRateBean pPurchaserSupplierCapRateBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierCapRateBean lOldPurchaserSupplierCapRateBean = null;
        if (pNew) {

            purchaserSupplierCapRateDAO.insert(lConnection, pPurchaserSupplierCapRateBean);
        } else {
            lOldPurchaserSupplierCapRateBean = findBean(pExecutionContext, pPurchaserSupplierCapRateBean);
            

            if (purchaserSupplierCapRateDAO.update(lConnection, pPurchaserSupplierCapRateBean) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, PurchaserSupplierCapRateBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        PurchaserSupplierCapRateBean lPurchaserSupplierCapRateBean = findBean(pExecutionContext, pFilterBean);
        purchaserSupplierCapRateDAO.delete(lConnection, lPurchaserSupplierCapRateBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public HashMap<String, Object> getPurchSuppCapRateMatrix(ExecutionContext pExecutionContext, PurchaserSupplierCapRateBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception
    {
    	HashMap<String, Object> lMatrixData = null;
    	List<PurchaserSupplierCapRateBean>  lPSCapRateBeans = null;
    	
    	lPSCapRateBeans = findList(pExecutionContext, pFilterBean, pColumnList, pUserBean);
    	
    	if(lPSCapRateBeans==null || lPSCapRateBeans.size() == 0) {
    	    lPSCapRateBeans = new ArrayList<PurchaserSupplierCapRateBean>();
    	    PurchaserSupplierCapRateBean lPurchaserSupplierCapRateBean = new PurchaserSupplierCapRateBean();
    	    lPurchaserSupplierCapRateBean.setFromHaircut(BigDecimal.ZERO);
    	    lPurchaserSupplierCapRateBean.setToHaircut(BigDecimal.valueOf(100));
    	    lPurchaserSupplierCapRateBean.setFromUsance(Long.valueOf(0));
    	    Long lMaxUsance = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_MAXUSANCE);
    	    lPurchaserSupplierCapRateBean.setToUsance(lMaxUsance);
    	    lPSCapRateBeans.add(lPurchaserSupplierCapRateBean);
    	}
        ArrayList<HashMap<String, Long>> lColHeaderData = new ArrayList<HashMap<String,Long>>();
        ArrayList<HashMap<String, BigDecimal>> lRowHeaderData = new ArrayList<HashMap<String,BigDecimal>>();
        ArrayList<ArrayList<BigDecimal>> lData = new ArrayList<ArrayList<BigDecimal>>();
        HashSet<String> lRowHash = new HashSet<String>(), lColHash = new HashSet<String>();
        String lRowKey = null, lColKey = null, lDataKey=null;
        Boolean lNewRow = false, lNewCol = false;
        HashMap<String, BigDecimal> lCapRates = new HashMap<String, BigDecimal>();
        //
        lMatrixData = new HashMap<String, Object>();
        //for columns and rows
        for(PurchaserSupplierCapRateBean lBean : lPSCapRateBeans){
            lColKey = (lBean.getFromUsance()!=null?lBean.getFromUsance():"") + CommonConstants.KEY_SEPARATOR +(lBean.getToUsance()!=null?lBean.getToUsance():"");
            lRowKey = (lBean.getFromHaircut()!=null?lBean.getFromHaircut():"") + CommonConstants.KEY_SEPARATOR +(lBean.getToHaircut()!=null?lBean.getToHaircut():"");
            lDataKey = lColKey + CommonConstants.KEY_SEPARATOR + lRowKey;
            lNewRow = !lRowHash.contains(lRowKey);
            lNewCol = !lColHash.contains(lColKey);
            if(lNewCol)
            {
                lColHash.add(lColKey);
                HashMap<String, Long> lCol = new HashMap<String, Long>();
                lCol.put(CAPRATEKEY_FROMUSANCE, lBean.getFromUsance());
                lCol.put(CAPRATEKEY_TOUSANCE, lBean.getToUsance());
                Long lFromUsance = null;
                int lIndex = 0;
                //for sequencing
                if(lBean.getFromUsance()!=null) //if it is null then add to top ie. index zero
                {
                    for(lIndex=0; lIndex < lColHeaderData.size(); lIndex++)
                    {
                        lFromUsance = lColHeaderData.get(lIndex).get(CAPRATEKEY_FROMUSANCE);
                        if(lFromUsance!=null && lBean.getFromUsance()!=null && lBean.getFromUsance() < lFromUsance)
                            break;
                    }
                }
                lColHeaderData.add(lIndex, lCol);
            }
            if(lNewRow)
            {
                lRowHash.add(lRowKey);
                HashMap<String, BigDecimal> lRow = new HashMap<String, BigDecimal>();
                lRow.put(CAPRATEKEY_FROMHAIRCUT, lBean.getFromHaircut());
                lRow.put(CAPRATEKEY_TOHAIRCUT, lBean.getToHaircut());
                BigDecimal lFromHaircut = null;
                int lIndex = 0;
                //for sequencing
                if(lBean.getFromHaircut()!=null) //if it is null then add to top ie. index zero
                {
                    for(lIndex=0; lIndex < lRowHeaderData.size(); lIndex++)
                    {
                        lFromHaircut = lRowHeaderData.get(lIndex).get(CAPRATEKEY_FROMHAIRCUT);
                        if(lFromHaircut!=null && lBean.getFromHaircut()!=null && lBean.getFromHaircut().doubleValue() < lFromHaircut.doubleValue())
                            break;
                    }
                }
                lRowHeaderData.add(lIndex, lRow);
            }
            if(!lCapRates.containsKey(lDataKey))
            {
                lCapRates.put(lDataKey, lBean.getCapRate());
            }
            else
            {
                //log duplicate data
            }
        }
        //for data - loop through columnHeader and rowHeaders
        lData = new ArrayList<ArrayList<BigDecimal>>();
        for(int lRowPtr=0; lRowPtr < lRowHeaderData.size(); lRowPtr++)
        {
            lRowKey = (lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_FROMHAIRCUT)!=null?lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_FROMHAIRCUT):"") + CommonConstants.KEY_SEPARATOR +(lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_TOHAIRCUT)!=null?lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_TOHAIRCUT):"");
            lData.add(new ArrayList<BigDecimal>());
            for(int lColPtr=0; lColPtr < lColHeaderData.size(); lColPtr++)
            {
                lColKey = (lColHeaderData.get(lColPtr).get(CAPRATEKEY_FROMUSANCE)!=null?lColHeaderData.get(lColPtr).get(CAPRATEKEY_FROMUSANCE):"") + CommonConstants.KEY_SEPARATOR +(lColHeaderData.get(lColPtr).get(CAPRATEKEY_TOUSANCE)!=null?lColHeaderData.get(lColPtr).get(CAPRATEKEY_TOUSANCE):"");
                lDataKey = lColKey + CommonConstants.KEY_SEPARATOR + lRowKey;
                lData.get(lRowPtr).add(lCapRates.get(lDataKey));
            }
        }
        lMatrixData.put(CAPRATEDATA_COUNTER_ENTITY, pFilterBean.getCounterEntityCode());
        lMatrixData.put(CAPRATEDATA_COLUMNS, lColHeaderData);
        lMatrixData.put(CAPRATEDATA_ROWS, lRowHeaderData);
        lMatrixData.put(CAPRATEDATA_DATA_CAPRATES, lData);
        return lMatrixData;
    }
    
    public void setPurchSuppCapRateMatrix(ExecutionContext pExecutionContext, Map<String, Object> pCapRateMatrix, IAppUserBean pUserBean) throws Exception
    {
        PurchaserSupplierCapRateBean lFilterBean = new PurchaserSupplierCapRateBean();
        if(pUserBean==null)
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        String lCounterEntityCode = null;
        if(lAppEntityBean==null)
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        if(pCapRateMatrix!=null)
        	lCounterEntityCode = (String)pCapRateMatrix.get(CAPRATEDATA_COUNTER_ENTITY);
        lFilterBean.setEntityCode(pUserBean.getDomain());
        if(lAppEntityBean.isPurchaser())
        {
        	lFilterBean.setCounterEntityCode(pUserBean.getDomain());
        }
        else if (lAppEntityBean.isSupplier() )
    	{
            if(StringUtils.isEmpty(lCounterEntityCode))
            	throw new CommonBusinessException("Counter Entity Code empty for supplier.");
            AppEntityBean lCounterAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lCounterEntityCode});
            if(lCounterAppEntityBean==null || lCounterAppEntityBean.getCode()==null)
            	throw new CommonBusinessException("Counter Entity Code [" + lCounterEntityCode + "] not found.");
            lFilterBean.setCounterEntityCode(lCounterEntityCode);
    	}
        else
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
    	Map<String,PurchaserSupplierCapRateBean>  lOldCapRateBeans = new HashMap<String, PurchaserSupplierCapRateBean>();
    	Map<String,PurchaserSupplierCapRateBean>  lNewCapRateBeans = new HashMap<String, PurchaserSupplierCapRateBean>();
    	String lDataKey = null;
    	//Old Beans to DataHash
    	List<PurchaserSupplierCapRateBean> lDbCapRateBeans = findList(pExecutionContext, lFilterBean, null, pUserBean);
    	if(lDbCapRateBeans!=null)
    	{
    		for(PurchaserSupplierCapRateBean lBean : lDbCapRateBeans)
    		{
    			lDataKey = getDataKey(lBean);
    			lOldCapRateBeans.put(lDataKey, lBean);
    		}
    	}
    	//New Beans to DataHash
    	if(pCapRateMatrix!=null)
    	{
    		List<Map<String,Object>> lColHeaderData  = (List<Map<String,Object>>) pCapRateMatrix.get(CAPRATEDATA_COLUMNS);
    		List<Map<String,Object>> lRowHeaderData  = (List<Map<String,Object>>) pCapRateMatrix.get(CAPRATEDATA_ROWS);
    		List<List<Object>> lData = (List<List<Object>>) pCapRateMatrix.get(CAPRATEDATA_DATA_CAPRATES);
    		PurchaserSupplierCapRateBean lPSCapRateBean = null;
    		//
    		//validate - overlap 
    		validateOverlap(lColHeaderData, CAPRATEKEY_FROMUSANCE, CAPRATEKEY_TOUSANCE, BigDecimal.ONE);
    		validateOverlap(lRowHeaderData, CAPRATEKEY_FROMHAIRCUT, CAPRATEKEY_TOHAIRCUT, BigDecimal.valueOf(0.01));
    		//
            Long lMaxUsance = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_MAXUSANCE);
        	for(int lRowPtr=0; lRowPtr < lRowHeaderData.size(); lRowPtr++)
        	{
            	for(int lColPtr=0; lColPtr < lColHeaderData.size(); lColPtr++)
            	{
            		lPSCapRateBean = new PurchaserSupplierCapRateBean();
            		lPSCapRateBean.setEntityCode(lFilterBean.getEntityCode());
            		lPSCapRateBean.setCounterEntityCode(lFilterBean.getCounterEntityCode());
            		lPSCapRateBean.setFromHaircut(new BigDecimal((String)lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_FROMHAIRCUT)));
            		lPSCapRateBean.setToHaircut(new BigDecimal((String)lRowHeaderData.get(lRowPtr).get(CAPRATEKEY_TOHAIRCUT)));
            		lPSCapRateBean.setFromUsance(new Long((String)lColHeaderData.get(lColPtr).get(CAPRATEKEY_FROMUSANCE)));
            		lPSCapRateBean.setToUsance(new Long((String)lColHeaderData.get(lColPtr).get(CAPRATEKEY_TOUSANCE)));
            		lPSCapRateBean.setCapRate(new BigDecimal((String)lData.get(lRowPtr).get(lColPtr)));
            		lDataKey = getDataKey(lPSCapRateBean);
            		//validation
            		if(lPSCapRateBean.getFromUsance() > lPSCapRateBean.getToUsance())
            			throw new CommonBusinessException("To-Usance should be greater than From-Usance. From[" + lPSCapRateBean.getFromUsance() +"] To[" + lPSCapRateBean.getToUsance() + "]");
            		if(lPSCapRateBean.getFromHaircut().compareTo(lPSCapRateBean.getToHaircut()) > 0)
            			throw new CommonBusinessException("To-Retention should be greater than From-Haircut. From[" + lPSCapRateBean.getFromHaircut() +"] To[" + lPSCapRateBean.getToHaircut() + "]" );
            		if ((lMaxUsance != null) && lPSCapRateBean.getToUsance().compareTo(lMaxUsance) > 0)
                        throw new CommonBusinessException("To-Usance should be less than " + lMaxUsance + ". To[" + lPSCapRateBean.getToUsance() + "]" );
            		if (lPSCapRateBean.getToHaircut().compareTo(AppConstants.HUNDRED) > 0)
                        throw new CommonBusinessException("To-Retention should be less than 100." + " To[" + lPSCapRateBean.getToHaircut() + "]" );
            		lNewCapRateBeans.put(lDataKey, lPSCapRateBean);
            	}
        	}
    	}
    	//loop through new capRateBeans get the old beans and then comapre the caprate and determine to save or not
    	PurchaserSupplierCapRateBean lNewBean = null, lOldBean = null;
    	List<PurchaserSupplierCapRateBean> lSaveList = new ArrayList<PurchaserSupplierCapRateBean>();
    	for(String lKey : lNewCapRateBeans.keySet())
    	{
    		lNewBean = lNewCapRateBeans.get(lKey);
    		lOldBean = lOldCapRateBeans.get(lKey);
    		if(lNewBean!=null && lOldBean==null)//newly added
    		{
    			lNewBean.setEntityCode(pUserBean.getDomain());
    			
    			lSaveList.add(lNewBean);
    		}
    		else if (lNewBean!=null && lOldBean!=null)//modified or unmodified
    		{
    			if(lNewBean.getCapRate() != null && 
    					lOldBean.getCapRate() != null &&
    					(lNewBean.getCapRate().compareTo(lOldBean.getCapRate())!=0))
    			{
    				lOldBean.setCapRate(lNewBean.getCapRate());
    				lSaveList.add(lOldBean);
    			}
    			lOldCapRateBeans.remove(lKey);
    		}
    	}
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        // insert/update
        if(lSaveList.size() > 0)
        {
    		for(PurchaserSupplierCapRateBean lBean : lSaveList)
    		{
    			if(lBean.getId()!=null) //update
    			{
        			purchaserSupplierCapRateDAO.update(lConnection, lBean);
        			purchaserSupplierCapRateDAO.insertAudit(lConnection, lBean, AuditAction.Update, pUserBean.getId());
    			}
    			else //insert
    			{
        			purchaserSupplierCapRateDAO.insert(lConnection, lBean);
        			purchaserSupplierCapRateDAO.insertAudit(lConnection, lBean, AuditAction.Insert, pUserBean.getId());
    			}
    		}
        }
		//delete
    	if(lOldCapRateBeans.size() > 0)
    	{
    		for(PurchaserSupplierCapRateBean lBean : lOldCapRateBeans.values())
    		{
    			purchaserSupplierCapRateDAO.delete(lConnection, lBean);
    			purchaserSupplierCapRateDAO.insertAudit(lConnection, lBean, AuditAction.Delete, pUserBean.getId());
    		}
    	}
        pExecutionContext.commitAndDispose();
    }
    
    private void validateOverlap(List<Map<String,Object>> pList, 
    		String pFromKey, String pToKey, BigDecimal pStepSize) throws CommonBusinessException
    {
    	BigDecimal lFrom1, lFrom2, lTo1, lTo2;
    	if(pList!= null)
    	{
    		Map<String,Object> lElement1 = null, lElement2 = null;
    		for(int lPtr=0; lPtr < pList.size(); lPtr++)
    		{
    			lElement1 = pList.get(lPtr);
    			lFrom1 = new BigDecimal(lElement1.get(pFromKey).toString());
    			lTo1 = new BigDecimal(lElement1.get(pToKey).toString());
    			BigDecimal lNextFrom = null;
        		for(int lPtr2=0; lPtr2 < pList.size(); lPtr2++)
        		{
        			lElement2 = pList.get(lPtr2);
        			lFrom2 = new BigDecimal(lElement2.get(pFromKey).toString());
        			lTo2 = new BigDecimal(lElement2.get(pToKey).toString());
        			if (lPtr2 > lPtr) {
	        			if((lFrom2.compareTo(lTo1)<=0) && lTo2.compareTo(lFrom1)>=0)
	        			{
	        				throw new CommonBusinessException(pFromKey.substring(4) + " range overlap : {" + lFrom1 + "-" + lTo1 +"} with {" + lFrom2 + "-" + lTo2 +"}");
	        			}
        			}
        			if (lFrom2.compareTo(lTo1) > 0) {
        				if ((lNextFrom == null) || (lNextFrom.compareTo(lFrom2) > 0))
        					lNextFrom = lFrom2;
        			}
        		}
        		// check for continuity
        		if (lNextFrom != null) {
        			if (lNextFrom.subtract(lTo1).compareTo(pStepSize) != 0)
        				throw new CommonBusinessException("No contiguous " + pFromKey.substring(4) + " range defined after range {" + lFrom1 + "-" + lTo1 + "}");
        		}
    		}
    	}
    }
    
    private String getDataKey(PurchaserSupplierCapRateBean pCapRateBean)
    {
    	StringBuffer lBuffer = new StringBuffer();
    	if(pCapRateBean!=null)
    	{
    		if(pCapRateBean.getFromUsance()!=null) lBuffer.append(pCapRateBean.getFromUsance());
    		lBuffer.append(CommonConstants.KEY_SEPARATOR);
    		if(pCapRateBean.getToUsance()!=null) lBuffer.append(pCapRateBean.getToUsance());
    		lBuffer.append(CommonConstants.KEY_SEPARATOR);
    		if(pCapRateBean.getFromHaircut()!=null) lBuffer.append(pCapRateBean.getFromHaircut().toString());
    		lBuffer.append(CommonConstants.KEY_SEPARATOR);
    		if(pCapRateBean.getToHaircut()!=null) lBuffer.append(pCapRateBean.getToHaircut().toString());
    	}
    	return lBuffer.toString();
    }
    
    private String getDataKey(Long pFromUsance, Long pToUsance, BigDecimal pFromHairCut, BigDecimal pToHairCut)
    {
    	StringBuffer lBuffer = new StringBuffer();
		if(pFromUsance!=null) lBuffer.append(pFromUsance);
		lBuffer.append(CommonConstants.KEY_SEPARATOR);
		if(pToUsance!=null) lBuffer.append(pToUsance);
		lBuffer.append(CommonConstants.KEY_SEPARATOR);
		if(pFromHairCut!=null) lBuffer.append(pFromHairCut.toString());
		lBuffer.append(CommonConstants.KEY_SEPARATOR);
		if(pToHairCut!=null) lBuffer.append(pToHairCut.toString());
    	return lBuffer.toString();
    }
}
