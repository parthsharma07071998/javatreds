package com.xlx.treds.master.bo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;

public class AuctionCalendarBO {
    
    private GenericDAO<AuctionCalendarBean> auctionCalendarDAO;

    public AuctionCalendarBO() {
        super();
        auctionCalendarDAO = new GenericDAO<AuctionCalendarBean>(AuctionCalendarBean.class);
    }
    
    public AuctionCalendarBean findBean(ExecutionContext pExecutionContext, 
        AuctionCalendarBean pFilterBean) throws Exception {
        AuctionCalendarBean lAuctionCalendarBean = auctionCalendarDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lAuctionCalendarBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lAuctionCalendarBean;
    }
    
    public List<AuctionCalendarBean> findList(ExecutionContext pExecutionContext, AuctionCalendarBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        String lSql = auctionCalendarDAO.getListSql(pFilterBean, pColumnList) + " ORDER BY ACDate, ACType";
        return auctionCalendarDAO.findListFromSql(pExecutionContext.getConnection(), lSql, 0);
    }
    
    public void save(ExecutionContext pExecutionContext, AuctionCalendarBean pAuctionCalendarBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AuctionCalendarBean lOldAuctionCalendarBean = null;
        if (!pNew) {
            lOldAuctionCalendarBean = findBean(pExecutionContext, pAuctionCalendarBean);
            pAuctionCalendarBean.setType(lOldAuctionCalendarBean.getType());
            pAuctionCalendarBean.setDate(lOldAuctionCalendarBean.getDate());
        }
        // duplicate check type and date
        StringBuilder lSql = new StringBuilder();
        lSql.append("SELECT * FROM AuctionCalendar WHERE ACRecordVersion > 0");
        lSql.append(" AND ACType = ").append(DBHelper.getInstance().formatString(pAuctionCalendarBean.getType()));
        lSql.append(" AND ACDate = ").append(DBHelper.getInstance().formatDate(pAuctionCalendarBean.getDate()));
        if (!pNew)
            lSql.append(" AND ACId != ").append(pAuctionCalendarBean.getId());
        AuctionCalendarBean lDuplicateBean = auctionCalendarDAO.findBean(lConnection, lSql.toString());
        if (lDuplicateBean != null)
            throw new CommonBusinessException("Auction calendar already defined for the given type and date");
        // date should be current or future date
        Timestamp lCurrentDate = CommonUtilities.getCurrentDate();
        Date lEpochDate = new Date(0); //admin should be able to change the template date hence skipping the check
        if (!CommonUtilities.compareDate(lEpochDate, pAuctionCalendarBean.getDate())){
    		if (lCurrentDate.compareTo(pAuctionCalendarBean.getDate()) > 0)
    			throw new CommonBusinessException("Auction calendar for past dates cannot be " + (pNew?"defined":"modified"));        	
        }
        // bid start time < bid end time
        if (pAuctionCalendarBean.getBidStartTime().compareTo(pAuctionCalendarBean.getBidEndTime()) > 0)
            throw new CommonBusinessException("Bid start time cannot be greater than bid end time");
        
        if (!DateUtils.isSameDay(pAuctionCalendarBean.getDate(), pAuctionCalendarBean.getBidStartTime()))
            throw new CommonBusinessException("Date part of bidding start time should be same as auction date");
        if (!DateUtils.isSameDay(pAuctionCalendarBean.getDate(), pAuctionCalendarBean.getBidEndTime()))
            throw new CommonBusinessException("Date part of bidding end time should be same as auction date");
        List<ConfirmationWindowBean> lConfWinList = pAuctionCalendarBean.getConfWinList();
        if (lConfWinList != null) {
            int lCount = lConfWinList.size();
            for (int lPtr=0;lPtr<lCount;lPtr++) {
                ConfirmationWindowBean lConfirmationWindowBean = lConfWinList.get(lPtr);
                // for all conf windows conf start time should be less than conf end time and should be within bid window
                if (lConfirmationWindowBean.getConfStartTime().compareTo(lConfirmationWindowBean.getConfEndTime()) > 0)
                    throw new CommonBusinessException("Confirmation start time cannot be greater than end time. Window no : " + (lPtr+1));
                if (lConfirmationWindowBean.getSettlementDate().compareTo(pAuctionCalendarBean.getDate()) < 0)
                    throw new CommonBusinessException("Settlement Date cannot be less than the auction date. Window no : " + (lPtr+1));
                if (!DateUtils.isSameDay(pAuctionCalendarBean.getDate(), lConfirmationWindowBean.getConfStartTime()))
                    throw new CommonBusinessException("Date part of confirmation window start time should be same as auction date. Window no : " + (lPtr+1));
                if (!DateUtils.isSameDay(pAuctionCalendarBean.getDate(), lConfirmationWindowBean.getConfEndTime()))
                    throw new CommonBusinessException("Date part of confirmation window end time should be same as auction date. Window no : " + (lPtr+1));
                // no overlap of conf windows
                for (int lPtr1=0;lPtr1<lPtr;lPtr1++) {
                    ConfirmationWindowBean lOtherWindowBean = lConfWinList.get(lPtr1);
                    if ((lConfirmationWindowBean.getConfStartTime().compareTo(lOtherWindowBean.getConfEndTime()) < 0) && 
                            (lConfirmationWindowBean.getConfEndTime().compareTo(lOtherWindowBean.getConfStartTime()) > 0))
                        throw new CommonBusinessException("Confirmation window " + (lPtr+1) + " overlaps with window " + (lPtr1 + 1));
                }
            }
        }
        
        if (pNew) {
            pAuctionCalendarBean.setRecordCreator(pUserBean.getId());
            auctionCalendarDAO.insert(lConnection, pAuctionCalendarBean);
            auctionCalendarDAO.insertAudit(lConnection, pAuctionCalendarBean, AuditAction.Insert, pUserBean.getId());
        } else {
            pAuctionCalendarBean.setRecordUpdator(pUserBean.getId());
            if (auctionCalendarDAO.update(lConnection, pAuctionCalendarBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            BeanMeta lBeanMeta = auctionCalendarDAO.getBeanMeta(); 
            lBeanMeta.copyBean(pAuctionCalendarBean, lOldAuctionCalendarBean, lBeanMeta.getDatabaseFields(BeanMeta.FIELDGROUP_UPDATE));
            auctionCalendarDAO.insertAudit(lConnection, lOldAuctionCalendarBean, AuditAction.Update, pUserBean.getId());
        }
        pExecutionContext.commitAndDispose();
        
        MemoryDBManager.getInstance().reloadTableDistributed(AuctionCalendarBean.ENTITY_NAME);
    }
}
