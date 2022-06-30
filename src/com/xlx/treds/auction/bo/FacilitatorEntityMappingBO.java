package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

public class FacilitatorEntityMappingBO {
    
    private GenericDAO<FacilitatorEntityMappingBean> facilitatorEntityMappingDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailsBeanDAO;

    public FacilitatorEntityMappingBO() {
        super();
        facilitatorEntityMappingDAO = new GenericDAO<FacilitatorEntityMappingBean>(FacilitatorEntityMappingBean.class);
        companyBankDetailsBeanDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
    }
    
    public FacilitatorEntityMappingBean findBean(ExecutionContext pExecutionContext, 
        FacilitatorEntityMappingBean pFilterBean) throws Exception {
        FacilitatorEntityMappingBean lFacilitatorEntityMappingBean = facilitatorEntityMappingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lFacilitatorEntityMappingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lFacilitatorEntityMappingBean;
    }
    
    public List<FacilitatorEntityMappingBean> findList(ExecutionContext pExecutionContext, FacilitatorEntityMappingBean pFilterBean, 
        List<String> pColumnList, AppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        List<FacilitatorEntityMappingBean> lMappingList = null;
        StringBuilder lStrCBDIds = new StringBuilder();
        List<CompanyBankDetailBean> lCBDList = null;
        HashMap<Long, CompanyBankDetailBean> lCBDHash = null;
        
        lMappingList = facilitatorEntityMappingDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
        
        for(FacilitatorEntityMappingBean lBean : lMappingList){
        	if(lBean.getCbdId()!=null && lBean.getCbdId().longValue() > 0){
        		if(lStrCBDIds.length() > 0) {
        			lStrCBDIds.append(",");
        		}
        		lStrCBDIds.append(lBean.getCbdId().toString());
        	}
        }
        if(lStrCBDIds.length() > 0){
        	StringBuilder lSql = new StringBuilder();
        	lSql.append(" SELECT * FROM COMPANYBANKDETAILS WHERE CBDRecordVersion > 0 ");
        	lSql.append(" AND CBDId IN ( ").append(lStrCBDIds.toString()).append(" ) ");
        	lCBDList = companyBankDetailsBeanDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        	lCBDHash = new HashMap<Long, CompanyBankDetailBean>();
        	for(CompanyBankDetailBean lCBDBean : lCBDList){
        		lCBDHash.put(lCBDBean.getId(), lCBDBean);
        	}
        }
        if(lCBDList != null && lCBDList.size() > 0){
        	CompanyBankDetailBean lCBDBean = null;
            for(FacilitatorEntityMappingBean lBean : lMappingList){
            	lCBDBean = lCBDHash.get(lBean.getCbdId());
            	if(lCBDBean != null){
            		lBean.setDesignatedBankName(TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCBDBean.getBank()));
            		lBean.setIfsc(lCBDBean.getIfsc());
            		lBean.setAccNo(lCBDBean.getAccNo());
            	}
            }
        }
        return lMappingList;
    }
    
    public String save(ExecutionContext pExecutionContext, FacilitatorEntityMappingBean pFacilitatorEntityMappingBean, AppUserBean pUserBean, 
        boolean pNew) throws Exception {
    	String lRetVal = null;
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        int result=0;
        Connection lConnection = pExecutionContext.getConnection();
        FacilitatorEntityMappingBean lOldFacilitatorEntityMappingBean = null;
        lOldFacilitatorEntityMappingBean = facilitatorEntityMappingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFacilitatorEntityMappingBean);
        if (pNew) {
            if (lOldFacilitatorEntityMappingBean != null)
                throw new CommonBusinessException(CommonBusinessException.RECORD_ALREADY_EXISTS);
            pFacilitatorEntityMappingBean.setRecordCreator(pUserBean.getId());
            facilitatorEntityMappingDAO.insert(lConnection, pFacilitatorEntityMappingBean);
            facilitatorEntityMappingDAO.insertAudit(lConnection, pFacilitatorEntityMappingBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        } else {
            if (lOldFacilitatorEntityMappingBean == null) 
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            pFacilitatorEntityMappingBean.setRecordCreator(lOldFacilitatorEntityMappingBean.getRecordCreator());
            pFacilitatorEntityMappingBean.setRecordCreateTime(lOldFacilitatorEntityMappingBean.getRecordCreateTime());
            pFacilitatorEntityMappingBean.setRecordUpdator(pUserBean.getId());
            facilitatorEntityMappingDAO.getBeanMeta().copyBean(pFacilitatorEntityMappingBean, lOldFacilitatorEntityMappingBean);
            if (facilitatorEntityMappingDAO.update(lConnection, lOldFacilitatorEntityMappingBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            facilitatorEntityMappingDAO.insertAudit(lConnection, lOldFacilitatorEntityMappingBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            result = findPendingObligations(lConnection,lOldFacilitatorEntityMappingBean);
            if(result > 0){
            	lRetVal ="{\"Message\" : \"" + result + " obligations pending.\"  }";
            }
        }
        pExecutionContext.commitAndDispose();
        return lRetVal;
    }
    
    private int findPendingObligations(Connection lConnection,FacilitatorEntityMappingBean pFEMBean){
    	//here check whether obligations pending for the bank
    	//FEM CoLoc	Obligation
    	List<FacilitatorEntityMappingBean> lFacilitatorEntityMappingBeans = null;
    	
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM ");
    	lSql.append(" ( ");
    	lSql.append(" SELECT * FROM Obligations ");
    	lSql.append(" LEFT OUTER JOIN CompanyLocations ON (CLENABLESETTLEMENT='Y' AND CLID=OBSETTLEMENTCLID) ");
    	lSql.append(" ) ");
    	lSql.append(" WHERE CLCBDID=").append(pFEMBean.getCbdId());
    	lSql.append(" AND OBSETTLEDDATE IS NULL AND OBDATE >= sysdate " );
    	try {
    		lFacilitatorEntityMappingBeans=facilitatorEntityMappingDAO.findListFromSql(lConnection, lSql.toString(), -1);
    		return ((lFacilitatorEntityMappingBeans!=null)?lFacilitatorEntityMappingBeans.size():0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return 0;
    }
    
}
