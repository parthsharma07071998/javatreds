package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.ObligationExtensionPenaltyBean;
import com.xlx.treds.auction.bean.PenaltyDetailBean;
import com.xlx.treds.entity.bean.AppEntityBean;

public class ObligationExtensionPenaltyBO {
    
    private GenericDAO<ObligationExtensionPenaltyBean> obligationExtensionPenaltyDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
    
    public ObligationExtensionPenaltyBO() {
        super();
        obligationExtensionPenaltyDAO = new GenericDAO<ObligationExtensionPenaltyBean>(ObligationExtensionPenaltyBean.class);
        financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
    }
    
    public ObligationExtensionPenaltyBean findBean(ExecutionContext pExecutionContext, 
        ObligationExtensionPenaltyBean pFilterBean, IAppUserBean pUserBean, boolean pThrowError) throws Exception {
        ObligationExtensionPenaltyBean lObligationExtensionPenaltyBean = obligationExtensionPenaltyDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lObligationExtensionPenaltyBean == null) {
            if (pThrowError)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            else
                return null;
        }
        if (!lObligationExtensionPenaltyBean.getFinancier().equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        return lObligationExtensionPenaltyBean;
    }
    
    public List<ObligationExtensionPenaltyBean> findList(ExecutionContext pExecutionContext, ObligationExtensionPenaltyBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        if (pFilterBean == null) pFilterBean = new ObligationExtensionPenaltyBean();
        pFilterBean.setFinancier(pUserBean.getDomain());
        return obligationExtensionPenaltyDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, ObligationExtensionPenaltyBean pObligationExtensionPenaltyBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        ObligationExtensionPenaltyBean lOldObligationExtensionPenaltyBean = null;
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        if (!lAppEntityBean.isFinancier())
            throw new CommonBusinessException("Not a financier");
        // check if auction setting defined for purchaser by financier
        if (!ObligationExtensionPenaltyBean.DEFAULT.equals(pObligationExtensionPenaltyBean.getPurchaser())) {
            FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
            lFilterBean.setFinancier(lAppEntityBean.getCode());
            lFilterBean.setPurchaser(pObligationExtensionPenaltyBean.getPurchaser());
            FinancierAuctionSettingBean lFinancierAuctionSettingBean = financierAuctionSettingDAO.findBean(lConnection, lFilterBean);
            if (lFinancierAuctionSettingBean == null)
                throw new CommonBusinessException("Buyer limits not defined for " + pObligationExtensionPenaltyBean.getPurchaser());
        }
        // check if max grace period within global limits
        if (pObligationExtensionPenaltyBean.getMaxExtension() != null) {
            HashMap<String, Object> lGlobalSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONEXTENSION);
            if (lGlobalSettings != null) {
                Long lGlobalMaxDaysForExtension = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXDAYSFOREXTENSION);
                if ((lGlobalMaxDaysForExtension != null) && (lGlobalMaxDaysForExtension.longValue() < pObligationExtensionPenaltyBean.getMaxExtension().longValue())) {
                    throw new CommonBusinessException("Maximum days for extension cannot exceed " + lGlobalMaxDaysForExtension + " days");
                }
            }
        }
        if (ObligationExtensionPenaltyBean.DEFAULT.equals(pObligationExtensionPenaltyBean.getPurchaser()) 
                && (pObligationExtensionPenaltyBean.getAllowExtension() == CommonAppConstants.YesNo.Yes)) {
            if ((pObligationExtensionPenaltyBean.getPenaltyList() == null) || (pObligationExtensionPenaltyBean.getPenaltyList().isEmpty()))
                throw new CommonBusinessException("Penalty details mandatory for global settings.");
        }
        if ((pObligationExtensionPenaltyBean.getPenaltyList() != null) && (!pObligationExtensionPenaltyBean.getPenaltyList().isEmpty())) {
            Map<Long, Long> lUniqueDays = new HashMap<Long, Long>();
            for (PenaltyDetailBean lPenaltyDetailBean : pObligationExtensionPenaltyBean.getPenaltyList()) {
                if (lPenaltyDetailBean.getUptoDays().compareTo(pObligationExtensionPenaltyBean.getMaxExtension()) > 0)
                    throw new CommonBusinessException("Upto days in the penalty slabs should not exceed max extension period " + pObligationExtensionPenaltyBean.getMaxExtension());
                if (lUniqueDays.containsKey(lPenaltyDetailBean.getUptoDays()))
                    throw new CommonBusinessException("Multiple rates specified for days = " + lPenaltyDetailBean.getUptoDays());
                else
                    lUniqueDays.put(lPenaltyDetailBean.getUptoDays(), null);
            }
        }
        if (pNew) {
            pObligationExtensionPenaltyBean.setFinancier(lAppEntityBean.getCode());
            lOldObligationExtensionPenaltyBean = findBean(pExecutionContext, pObligationExtensionPenaltyBean, pUserBean, false);
            if (lOldObligationExtensionPenaltyBean != null)
                throw new CommonBusinessException(CommonBusinessException.RECORD_ALREADY_EXISTS);
            pObligationExtensionPenaltyBean.setRecordCreator(pUserBean.getId());
            obligationExtensionPenaltyDAO.insert(lConnection, pObligationExtensionPenaltyBean);
            obligationExtensionPenaltyDAO.insertAudit(lConnection, pObligationExtensionPenaltyBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        } else {
            lOldObligationExtensionPenaltyBean = findBean(pExecutionContext, pObligationExtensionPenaltyBean, pUserBean, true);
            pObligationExtensionPenaltyBean.setRecordUpdator(pUserBean.getId());
            if (obligationExtensionPenaltyDAO.update(lConnection, pObligationExtensionPenaltyBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            pObligationExtensionPenaltyBean.setRecordCreator(lOldObligationExtensionPenaltyBean.getRecordCreator());
            pObligationExtensionPenaltyBean.setRecordCreateTime(lOldObligationExtensionPenaltyBean.getRecordCreateTime());
            obligationExtensionPenaltyDAO.insertAudit(lConnection, pObligationExtensionPenaltyBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        }
        pExecutionContext.commitAndDispose();
    }
    
}
