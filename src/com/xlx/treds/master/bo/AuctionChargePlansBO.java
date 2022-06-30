package com.xlx.treds.master.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.master.bean.AuctionChargePlanBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;

public class AuctionChargePlansBO {
    
    private GenericDAO<AuctionChargePlanBean> auctionChargePlansDAO;
    private GenericDAO<AuctionChargeSlabBean> auctionChargeSlabsDAO;
    private GenericDAO<MemberwisePlanBean> memberwisePlanDAO;
    //private  String defaultSlabListFields = "ACSId,ACSMinAmount,ACSMaxAmount,ACSChargeType,ACSChargePercentValue,ACSChargeAbsoluteValue,ACSChargeMaxValue,ACSRecordVersion";
    
    public AuctionChargePlansBO() {
        super();
        auctionChargePlansDAO = new GenericDAO<AuctionChargePlanBean>(AuctionChargePlanBean.class);
        auctionChargeSlabsDAO = new GenericDAO<AuctionChargeSlabBean>(AuctionChargeSlabBean.class);
        memberwisePlanDAO = new GenericDAO<MemberwisePlanBean>(MemberwisePlanBean.class);
    }
    
    public AuctionChargePlanBean findBean(ExecutionContext pExecutionContext, 
        AuctionChargePlanBean pFilterBean) throws Exception {
    	return getPlanDetailsBean(pExecutionContext.getConnection(), pFilterBean);
    }

    public AuctionChargePlanBean getPlanDetailsBean(Connection pConnection, 
            AuctionChargePlanBean pFilterBean) throws Exception {
        	//first bring the actual plan details
            AuctionChargePlanBean lAuctionChargePlansBean = auctionChargePlansDAO.findByPrimaryKey(pConnection, pFilterBean);
            if (lAuctionChargePlansBean == null) 
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            //sencondly bring the slab details and add it to plan
            AuctionChargeSlabBean lFilterSlabsBean = new AuctionChargeSlabBean();
            lFilterSlabsBean.setAcpId(pFilterBean.getId());
            List<AuctionChargeSlabBean> lAuctionChargeSlabList = auctionChargeSlabsDAO.findList(pConnection, lFilterSlabsBean, new ArrayList<String>());
            Collections.sort(lAuctionChargeSlabList);
            lAuctionChargePlansBean.setAuctionChargeSlabList(lAuctionChargeSlabList);
            return lAuctionChargePlansBean;
        }
    
    public List<AuctionChargePlanBean> findList(ExecutionContext pExecutionContext, AuctionChargePlanBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return auctionChargePlansDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, AuctionChargePlanBean pAuctionChargePlansBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
    	boolean pNewSlab = false;
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AuctionChargePlanBean lOldAuctionChargePlanBean = null;
        //First Save Plan
        List<AuctionChargeSlabBean> lNewSlabs =  pAuctionChargePlansBean.getAuctionChargeSlabList();
        List<AuctionChargeSlabBean> lOldSlabs = null;
        //validate plan name
        AuctionChargePlanBean lTmpACPBean = new AuctionChargePlanBean();
        lTmpACPBean.setName(pAuctionChargePlansBean.getName());
        lTmpACPBean = auctionChargePlansDAO.findBean(lConnection,lTmpACPBean);
    	if(lTmpACPBean!=null){
    		if(pNew || !lTmpACPBean.getId().equals(pAuctionChargePlansBean.getId()))
    			throw new CommonBusinessException("The Plan Name - "+pAuctionChargePlansBean.getName() + " already exists.");
    	}
        //
        if (pNew) {
            pAuctionChargePlansBean.setRecordCreator(pUserBean.getId());
            auctionChargePlansDAO.insert(lConnection, pAuctionChargePlansBean);
            auctionChargePlansDAO.insertAudit(lConnection, pAuctionChargePlansBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        } else {
        	lOldAuctionChargePlanBean = findBean(pExecutionContext, pAuctionChargePlansBean);
        	if (AppConstants.DEFAULT_PLAN_ZERO.equals(lOldAuctionChargePlanBean.getId())) {
        		throw new CommonBusinessException(" Plan Zero cannot be changed or modified. ");
        	}
        	lOldSlabs = lOldAuctionChargePlanBean.getAuctionChargeSlabList();
            pAuctionChargePlansBean.setRecordUpdator(pUserBean.getId());
            if (auctionChargePlansDAO.update(lConnection, pAuctionChargePlansBean, BeanMeta.FIELDGROUP_UPDATE) == 0  )
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            auctionChargePlansDAO.getBeanMeta().copyBean(pAuctionChargePlansBean, lOldAuctionChargePlanBean, BeanMeta.FIELDGROUP_UPDATE, null);
            auctionChargePlansDAO.insertAudit(lConnection, lOldAuctionChargePlanBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        }
        //Second Save ,Update Delete Slab
        //saveSlab(lConnection, pAuctionChargePlansBean, lNewSlabs, lOldSlabs, pUserBean);
        // index old slab records
        Map<Long, AuctionChargeSlabBean> lOldSlabMap = new HashMap<Long, AuctionChargeSlabBean>();
        if (lOldSlabs != null) {
	        for (AuctionChargeSlabBean lBean : lOldSlabs){
	        	lOldSlabMap.put(lBean.getId(),lBean);
	        }
        }
        //validate the new slabs
        Collections.sort(lNewSlabs);
        AuctionChargeSlabBean lTmpACSBean =null;
        BigDecimal lPrevMaxAmt = BigDecimal.ZERO;
        for(int lPtr=0; lPtr<lNewSlabs.size(); lPtr++){
        	lTmpACSBean = lNewSlabs.get(lPtr);
        	if(lPtr == 0){
        		lPrevMaxAmt = lTmpACSBean.getMaxAmount();
        		continue;
        	}
        	if(!lPrevMaxAmt.equals(lTmpACSBean.getMinAmount())){
        		throw new CommonBusinessException("Please check the slab range " + lTmpACSBean.getMinAmount() + " - "+ lTmpACSBean.getMaxAmount());
        	}
    		lPrevMaxAmt = lTmpACSBean.getMaxAmount();
        }
        //
        // loop thru new list and insert or update based on availablility in loldmap
	    for (AuctionChargeSlabBean lNewSlabBean : lNewSlabs) {
	    	AuctionChargeSlabBean lOldSlabBean = null;
	    	if (lNewSlabBean.getId() != null)
	    		lOldSlabBean = lOldSlabMap.remove(lNewSlabBean.getId());
	    	if (lNewSlabBean.getMinAmount().compareTo(lNewSlabBean.getMaxAmount()) > 0)
	            throw new CommonBusinessException("From Amount cannot be greater than To Amount");
            if(AuctionChargeSlabBean.ChargeType.Threshold.equals(lNewSlabBean.getChargeType())){
                if(lNewSlabBean.getChargeMaxValue().compareTo(BigDecimal.ZERO) > 0 ){
                	if(lNewSlabBean.getChargeAbsoluteValue().compareTo(lNewSlabBean.getChargeMaxValue())> 0){
                		throw new CommonBusinessException("The absolute value cannot be greater than the max charge value.");
                    }
                }
            }
	        if (lOldSlabBean == null) {
	    		// insert
	    		lNewSlabBean.setRecordCreator(pUserBean.getId());
	    		lNewSlabBean.setAcpId(pAuctionChargePlansBean.getId());
	    		auctionChargeSlabsDAO.insert(lConnection, lNewSlabBean);
	    		auctionChargeSlabsDAO.insertAudit(lConnection, lNewSlabBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
	    	} else {
	    		// update
	    		if (!auctionChargeSlabsDAO.getBeanMeta().equalBean(lNewSlabBean, lOldSlabBean, BeanMeta.FIELDGROUP_UPDATE, null)) {
	        		lNewSlabBean.setRecordUpdator(pUserBean.getId());
	        		if (auctionChargeSlabsDAO.update(lConnection, lNewSlabBean, BeanMeta.FIELDGROUP_UPDATE) == 0  )
	                    throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
	        		auctionChargeSlabsDAO.getBeanMeta().copyBean(lNewSlabBean, lOldSlabBean, BeanMeta.FIELDGROUP_UPDATE, null);
	        		auctionChargeSlabsDAO.insertAudit(lConnection, lOldSlabBean, GenericDAO.AuditAction.Update, pUserBean.getId());
	    		}
	    	}
        }
        // delete remaining in oldmap
        for (AuctionChargeSlabBean lSlabBean : lOldSlabMap.values()) {
        	// delete
        	lSlabBean.setRecordUpdator(pUserBean.getId());
        	auctionChargeSlabsDAO.delete(lConnection, lSlabBean);
        	auctionChargeSlabsDAO.insertAudit(lConnection, lSlabBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
        }
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, AuctionChargePlanBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        //validation - check whether the planid exist for any meber
        MemberwisePlanBean lMPFilterBean = new MemberwisePlanBean();
        lMPFilterBean.setAcpId(pFilterBean.getId());
        List<MemberwisePlanBean> lMPBeanList = memberwisePlanDAO.findList(lConnection, lMPFilterBean, new ArrayList<String>());
        if(lMPBeanList!=null && lMPBeanList.size() > 0){
        	String lMembers = "";
        	for(MemberwisePlanBean lTmpMPBean : lMPBeanList){
        		if(!StringUtils.isBlank(lMembers))
        			lMembers += ",";
        		lMembers += lTmpMPBean.getCode();
        	}
        	throw new CommonBusinessException("The Plan has been mapped to " + lMPBeanList.size() + " members.\n Members : " + lMembers);
        }
        //delete the actual plan first
        AuctionChargePlanBean lAuctionChargePlansBean = findBean(pExecutionContext, pFilterBean);
        lAuctionChargePlansBean.setRecordUpdator(pUserBean.getId());
        auctionChargePlansDAO.delete(lConnection, lAuctionChargePlansBean);     
        auctionChargePlansDAO.insertAudit(lConnection, lAuctionChargePlansBean, GenericDAO.AuditAction.Delete, pUserBean.getId()); 
        //secondly delete the slab details
        AuctionChargeSlabBean lFilterSlabsBean = new AuctionChargeSlabBean();
        lFilterSlabsBean.setAcpId(pFilterBean.getId());
        List<AuctionChargeSlabBean> lAuctionChargeSlabList = auctionChargeSlabsDAO.findList(pExecutionContext.getConnection(), lFilterSlabsBean, new ArrayList<String>());
        AuctionChargeSlabBean lAucSlbBean = null;
        for(int lPtr = 0; lPtr < lAuctionChargeSlabList.size(); lPtr++){
        	lAucSlbBean = lAuctionChargeSlabList.get(lPtr);
        	lAucSlbBean.setRecordUpdator(pUserBean.getId());
        	auctionChargeSlabsDAO.delete(lConnection, lAucSlbBean);
        	auctionChargeSlabsDAO.insertAudit(lConnection, lAucSlbBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
		}

        pExecutionContext.commitAndDispose();
    }
}
