package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean.Level;
import com.xlx.treds.auction.bean.TenureWiseBaseRateBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.FactoringUnitBidsReportBean.BidStatus;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bo.AppUserBO;

public class FinancierAuctionSettingBO {
    
    private AppUserBO appUserBO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingProvDAO;
    private GenericDAO<BidBean> bidDAO;

    public FinancierAuctionSettingBO() {
        super();
        appUserBO = new AppUserBO();
        financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
        financierAuctionSettingProvDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class, "FinancierAuctionSettings_P");
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
    }
    
    public FinancierAuctionSettingBean findBean(ExecutionContext pExecutionContext, 
        FinancierAuctionSettingBean pFilterBean) throws Exception {
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = financierAuctionSettingProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lFinancierAuctionSettingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lFinancierAuctionSettingBean;
    }
    
    public List<FinancierAuctionSettingBean> findList(ExecutionContext pExecutionContext, FinancierAuctionSettingBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        if(lAppEntityBean.isPlatform()){
        	//no filters
        }
        else{
            if (lAppEntityBean.isFinancier())
                pFilterBean.setFinancier(pUserBean.getDomain());
            else if (lAppEntityBean.isPlatform())
                pFilterBean.setLevel(FinancierAuctionSettingBean.Level.System_Buyer);
            else if (lAppEntityBean.isPurchaser())
                pFilterBean.setPurchaser(pUserBean.getDomain());
            else
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        StringBuilder lSql = new StringBuilder();

        if (Level.Financier_Buyer.equals(pFilterBean.getLevel()) && lAppEntityBean.isFinancier()){
	        lSql.append(" SELECT FINANCIERAUCTIONSETTINGS.* ,CLNAME \"FASFinancierLocation\" ");
	        lSql.append(" FROM FinancierAuctionSettings ");
      		lSql.append(" LEFT OUTER JOIN AppEntities ON (FASFINANCIER IS NOT NULL AND FASFINANCIER = AECODE) ");
      		lSql.append(" LEFT OUTER JOIN COMPANYDETAILS ON ( AECDID = CDID ) ");
            lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS ON ( FASFINCLID IS NOT NULL AND CLCDID = CDID AND CLID = FASFINCLID ) ");
            lSql.append(" WHERE 1=1 ");
	        financierAuctionSettingDAO.appendAsSqlFilter(lSql, pFilterBean, false);
	        lSql.append(" UNION ALL ");
	        lSql.append(" SELECT FINANCIERAUCTIONSETTINGS_p.* ,CLNAME \"FASFinancierLocation\" ");
	        lSql.append(" FROM FinancierAuctionSettings_p ");
      		lSql.append(" LEFT OUTER JOIN AppEntities ON (FASFINANCIER IS NOT NULL AND FASFINANCIER = AECODE) ");
      		lSql.append(" LEFT OUTER JOIN COMPANYDETAILS ON ( AECDID = CDID ) ");
            lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS ON ( FASFINCLID IS NOT NULL AND CLCDID = CDID AND CLID = FASFINCLID ) ");
            lSql.append(" WHERE 1=1 ");
	        financierAuctionSettingDAO.appendAsSqlFilter(lSql, pFilterBean, false);
	        lSql.append(" AND FASApprovalStatus NOT IN (").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.ApprovalStatus.Approved.getCode()));
	        lSql.append(", ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.ApprovalStatus.Deleted.getCode())).append(")");
    	}else{
        	lSql.append(financierAuctionSettingDAO.getListSql(pFilterBean, pColumnList));
        	lSql.append(" UNION ALL ").append(financierAuctionSettingProvDAO.getListSql(pFilterBean, pColumnList));
            lSql.append(" AND FASApprovalStatus NOT IN (").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.ApprovalStatus.Approved.getCode()));
            lSql.append(", ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.ApprovalStatus.Deleted.getCode())).append(")");
        }
        
        List<FinancierAuctionSettingBean> lList = financierAuctionSettingDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), 0);
        
        if ((FinancierAuctionSettingBean.Level.Financier_Self.equals(pFilterBean.getLevel())) && (lList.size() == 0)) {
            FinancierAuctionSettingBean lFinancierAuctionSettingBean = new FinancierAuctionSettingBean();
            lList = new ArrayList<FinancierAuctionSettingBean>();
            lFinancierAuctionSettingBean.setId(DBHelper.getInstance().getUniqueNumber(pExecutionContext.getConnection(), "FinancierAuctionSettings.id"));
            lFinancierAuctionSettingBean.setLevel(pFilterBean.getLevel());
            lFinancierAuctionSettingBean.setFinancier(pUserBean.getDomain());
            lFinancierAuctionSettingBean.setCurrency("INR");
            lFinancierAuctionSettingBean.setLimit(BigDecimal.ZERO);
            lFinancierAuctionSettingBean.setUtilised(BigDecimal.ZERO);
            lFinancierAuctionSettingBean.setPurchaserCostLeg(AppConstants.CostCollectionLeg.Leg_1);
            lFinancierAuctionSettingBean.setActive(FinancierAuctionSettingBean.Active.Active);
            lFinancierAuctionSettingBean.setMakerAUId(pUserBean.getId());
            lFinancierAuctionSettingBean.setRecordCreator(pUserBean.getId());
            List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(pExecutionContext.getConnection(), pUserBean.getId(), getCheckerType(lFinancierAuctionSettingBean.getLevel()));
            if ((lCheckers == null) || lCheckers.isEmpty()) {
                // no checkers
                lFinancierAuctionSettingBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Approved);
                financierAuctionSettingDAO.insert(pExecutionContext.getConnection(), lFinancierAuctionSettingBean);
            } else {
                // checkers exist
                lFinancierAuctionSettingBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Draft);
            }
            financierAuctionSettingProvDAO.insert(pExecutionContext.getConnection(), lFinancierAuctionSettingBean);

            lList.add(lFinancierAuctionSettingBean);
        }
        if (FinancierAuctionSettingBean.Level.Financier_Buyer.equals(pFilterBean.getLevel()) 
                || FinancierAuctionSettingBean.Level.Financier_User.equals(pFilterBean.getLevel())) {
            // get financier level setting to get base rate
            FinancierAuctionSettingBean lSelfFilterBean = new FinancierAuctionSettingBean();
            lSelfFilterBean.setFinancier(pFilterBean.getFinancier());
            lSelfFilterBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Self);
            FinancierAuctionSettingBean lSelfBean = financierAuctionSettingDAO.findBean(pExecutionContext.getConnection(), lSelfFilterBean);
            // TODO
            if ((lSelfBean != null) && (lSelfBean.getBaseRateList() != null)) {
                for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lList) {
                    lFinancierAuctionSettingBean.setBaseRateList(lSelfBean.getBaseRateList());
                }
            }
        }
        else if (FinancierAuctionSettingBean.Level.Financier_Buyer_Seller.equals(pFilterBean.getLevel())) {
            // fill purchserRef for display purposes only
            pFilterBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Buyer_Seller);
            List<FinancierAuctionSettingBean> lBuyerLimits = financierAuctionSettingDAO.findList(pExecutionContext.getConnection(), pFilterBean, (String)null);
            Map<String, String> lPurchaserRefMap = new HashMap<String, String>();
            for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lBuyerLimits)
                lPurchaserRefMap.put(lFinancierAuctionSettingBean.getPurchaser(), lFinancierAuctionSettingBean.getPurchaserRef());
            for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lList) {
                String lPurchaserRef = lPurchaserRefMap.get(lFinancierAuctionSettingBean.getPurchaser());
                if (lPurchaserRef != null)
                    lFinancierAuctionSettingBean.setPurchaserRef(lPurchaserRef);
            }
        }
        Map<Long, Boolean> lCheckerMap = new HashMap<Long, Boolean>();
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lList) {
            if (FinancierAuctionSettingBean.ApprovalStatus.Submitted.equals(lFinancierAuctionSettingBean.getApprovalStatus())) {
                // check if logged in user is checker for corresponding maker of this record
                if (!pUserBean.getId().equals(lFinancierAuctionSettingBean.getMakerAUId())) {
                    Boolean lIsChecker = lCheckerMap.get(lFinancierAuctionSettingBean.getMakerAUId());
                    if (lIsChecker == null) {
                        lIsChecker = Boolean.valueOf(appUserBO.isValidChecker(pExecutionContext.getConnection(), lFinancierAuctionSettingBean.getMakerAUId(), 
                                pUserBean.getId(), getCheckerType(lFinancierAuctionSettingBean.getLevel())));
                        lCheckerMap.put(lFinancierAuctionSettingBean.getMakerAUId(), lIsChecker);
                    }
                    if (lIsChecker.booleanValue())
                        lFinancierAuctionSettingBean.setCheckerFlag("Y");
                }
            }
        }
        return lList;
    }
    
    public void save(ExecutionContext pExecutionContext, FinancierAuctionSettingBean pFinancierAuctionSettingBean, IAppUserBean pUserBean, 
        boolean pNew, boolean pUpload) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        if (lAppEntityBean.isFinancier()) {
            if (FinancierAuctionSettingBean.Level.System_Buyer.equals(pFinancierAuctionSettingBean.getLevel()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            pFinancierAuctionSettingBean.setFinancier(lAppEntityBean.getCode());
        } else if (lAppEntityBean.isPlatform()) {
            if (!FinancierAuctionSettingBean.Level.System_Buyer.equals(pFinancierAuctionSettingBean.getLevel()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            pFinancierAuctionSettingBean.setFinancier(null);
        } else
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        if ((pFinancierAuctionSettingBean.getMinBidRate() != null) || (pFinancierAuctionSettingBean.getMaxBidRate() != null)) {
            if ((pFinancierAuctionSettingBean.getMinBidRate() == null) || (pFinancierAuctionSettingBean.getMaxBidRate() == null))
                throw new CommonBusinessException("Incomplete bid rate range provided");
            else if (pFinancierAuctionSettingBean.getMinBidRate().compareTo(pFinancierAuctionSettingBean.getMaxBidRate()) > 0)
                throw new CommonBusinessException("Minimum bid rate should not be greater than maximum bid rate");
        }
        if ((pFinancierAuctionSettingBean.getMinSpread() != null) || (pFinancierAuctionSettingBean.getMaxSpread() != null)) {
            if ((pFinancierAuctionSettingBean.getMinSpread() == null) || (pFinancierAuctionSettingBean.getMaxSpread() == null))
                throw new CommonBusinessException("Incomplete repo rate offset (Spread) range provided");
            else if (pFinancierAuctionSettingBean.getMinSpread().compareTo(pFinancierAuctionSettingBean.getMaxSpread()) > 0)
                throw new CommonBusinessException("Minimum repo rate offset should not be greater than maximum repo rate offset");
        }

    	if(Level.Financier_User.equals(pFinancierAuctionSettingBean.getLevel())){
    		pFinancierAuctionSettingBean.setBidLimit(pFinancierAuctionSettingBean.getLimit());
		}
        
    	if (Level.Financier_Self.equals(pFinancierAuctionSettingBean.getLevel()) || Level.Financier_Buyer.equals(pFinancierAuctionSettingBean.getLevel())) {
    	    if (pFinancierAuctionSettingBean.getPurchaserCostLeg() == null) {
    	        throw new CommonBusinessException("Buyer Cost Leg is mandatory");
    	    }
    	}
    	
    	if (StringUtils.isNotBlank(pFinancierAuctionSettingBean.getSupplier())) {
            // valid supplier
            AppEntityBean lSupplierEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pFinancierAuctionSettingBean.getSupplier()});
            if ((lSupplierEntityBean == null) || (!lSupplierEntityBean.isSupplier()))
                throw new CommonBusinessException("Invalid Supplier code");
    	}
    	if (StringUtils.isNotBlank(pFinancierAuctionSettingBean.getPurchaser())) {
            // valid purchaser
            AppEntityBean lPurchaserEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pFinancierAuctionSettingBean.getPurchaser()});
            if ((lPurchaserEntityBean == null) || (!lPurchaserEntityBean.isPurchaser()))
                throw new CommonBusinessException("Invalid Purchaser code");
    	}
    	if (StringUtils.isNotBlank(pFinancierAuctionSettingBean.getLoginId())) {
    	    AppUserBean lAppUserBean = (AppUserBean)AuthenticationHandler.getInstance().getUserDataSource().getUserBean(pExecutionContext, 
    	            pFinancierAuctionSettingBean.getFinancier(), pFinancierAuctionSettingBean.getLoginId());
            if (lAppUserBean == null)
                throw new CommonBusinessException("Invalid Login Id");
    	}

    	if (Level.Financier_Self.equals(pFinancierAuctionSettingBean.getLevel())) {
    	    if ((pFinancierAuctionSettingBean.getBaseRateList() == null) || (pFinancierAuctionSettingBean.getBaseRateList().size() == 0)) 
    	        throw new CommonBusinessException("Minimum one lending rate slab required.");
    	    HashSet<Long> lTenureSet = new HashSet<Long>();
    	    Long lMaxTenure = new Long(0);
    	    for (TenureWiseBaseRateBean lBean : pFinancierAuctionSettingBean.getBaseRateList()) {
    	        if (lTenureSet.contains(lBean.getTenure()))
    	            throw new CommonBusinessException("Duplicate entries for tenure " + lBean.getTenure());
    	        lTenureSet.add(lBean.getTenure());
				//TODO : _PM - what to do ????
    	        //if(lBean.getTenure().compareTo(lMaxTenure) > 0) {
    	        //	lMaxTenure = new Long(lBean.getTenure().longValue());
    	        //}
    	    }
    	    Collections.sort(pFinancierAuctionSettingBean.getBaseRateList());
        	if(lMaxTenure.longValue() > 0) {
        		//TODO: _PM CHECK ACTIVE BIDS HAVING TENURE GREATER THAN MAXTENURE
            	Date lBusinessDate = null;
                AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
                if (lAuctionCalendarBean == null) {
                    throw new CommonBusinessException("Auction calendar not found");
                }
                lBusinessDate = lAuctionCalendarBean.getDate();
                StringBuilder lSql = new StringBuilder();
                DBHelper lDBHelper = DBHelper.getInstance();
                lSql.append("SELECT Count(*) TotalBids FROM Bids WHERE BDFinancierEntity = ").append(DBHelper.getInstance().formatString(pFinancierAuctionSettingBean.getFinancier()));
                lSql.append(" AND BDRate IS NOT NULL AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
                lSql.append(" AND (BDValidTill IS NULL OR BDValidTill >= ").append(lDBHelper.formatDate(lBusinessDate)).append(")");
        		Statement lStatement = null;
        		ResultSet lResultSet = null;
        		Long lTotalBids = new Long(0);
        		try {
        			lStatement = pExecutionContext.getConnection().createStatement();
        		    lResultSet = lStatement.executeQuery(lSql.toString());
        		    while (lResultSet.next()){
        		    	lTotalBids = lResultSet.getLong("TotalBids");
        		    }
        		} finally {
        			if (lStatement != null)
        				lStatement.close();
        		}
        		if(lMaxTenure.longValue() < lTotalBids.longValue()) {
        			throw new CommonBusinessException(lTotalBids + " bids exits.");
        		}
        	}
    	}
    	
    	
    	if(pFinancierAuctionSettingBean.getExpiryDate()!=null){
    		if(!pFinancierAuctionSettingBean.getExpiryDate().after(TredsHelper.getInstance().getBusinessDate())) {
    			throw new CommonBusinessException("Expiry Date should be beyond the current business date.");
    		}
    		if(!pFinancierAuctionSettingBean.getExpiryDate().after(CommonUtilities.getCurrentDate())) {
    			throw new CommonBusinessException("Expiry Date should be beyond the current date.");
        	}
    	}
    	
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        FinancierAuctionSettingBean lOldFinancierAuctionSettingBean = null;
        
        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        lFilterBean.setFinancier(pFinancierAuctionSettingBean.getFinancier());
        lFilterBean.setLevel(pFinancierAuctionSettingBean.getLevel());
        if (pFinancierAuctionSettingBean.getId() != null) {
            lFilterBean.setId(pFinancierAuctionSettingBean.getId());
        } else {
            switch (pFinancierAuctionSettingBean.getLevel()) {
            case Financier_Self:
                if ((pFinancierAuctionSettingBean.getPurchaser() != null) || (pFinancierAuctionSettingBean.getSupplier() != null)
                        || (pFinancierAuctionSettingBean.getAuId() != null)) 
                    throw new CommonValidationException("Invalid Data");
                break;
            case Financier_Buyer:
                if ((pFinancierAuctionSettingBean.getPurchaser() == null) || (pFinancierAuctionSettingBean.getSupplier() != null)
                        || (pFinancierAuctionSettingBean.getAuId() != null)) 
                    throw new CommonValidationException("Invalid Data");
                lFilterBean.setPurchaser(pFinancierAuctionSettingBean.getPurchaser());
                break;
            case Financier_Buyer_Seller:
                if ((pFinancierAuctionSettingBean.getPurchaser() == null) || (pFinancierAuctionSettingBean.getSupplier() == null)
                        || (pFinancierAuctionSettingBean.getAuId() != null)) 
                    throw new CommonValidationException("Invalid Data");
                lFilterBean.setPurchaser(pFinancierAuctionSettingBean.getPurchaser());
                lFilterBean.setSupplier(pFinancierAuctionSettingBean.getSupplier());
                break;
            case Financier_User:
                if ((pFinancierAuctionSettingBean.getPurchaser() != null) || (pFinancierAuctionSettingBean.getSupplier() != null)
                        || (pFinancierAuctionSettingBean.getAuId() == null)) 
                    throw new CommonValidationException("Invalid Data");
                lFilterBean.setAuId(pFinancierAuctionSettingBean.getAuId());
                break;
            case System_Buyer:
                if ((pFinancierAuctionSettingBean.getPurchaser() == null) || (pFinancierAuctionSettingBean.getSupplier() != null)
                        || (pFinancierAuctionSettingBean.getAuId() != null)) 
                    throw new CommonValidationException("Invalid Data");
                lFilterBean.setPurchaser(pFinancierAuctionSettingBean.getPurchaser());
                break;
            }
        }
        lOldFinancierAuctionSettingBean = financierAuctionSettingProvDAO.findBean(lConnection, lFilterBean);
        if (!pUpload) {
            if (!pNew) {
                if (lOldFinancierAuctionSettingBean == null)
                    throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
                else if (!lOldFinancierAuctionSettingBean.getId().equals(pFinancierAuctionSettingBean.getId()))
                    throw new CommonBusinessException("More than one record exist for given combination");
            }
        }
        
        pFinancierAuctionSettingBean.setMakerAUId(pUserBean.getId());
        // check if checkers assigned
        List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, pUserBean.getId(), getCheckerType(pFinancierAuctionSettingBean.getLevel()));
        if ((lCheckers == null) || lCheckers.isEmpty()) {
            // no checkers
            pFinancierAuctionSettingBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Approved);
        } else {
            // checkers exist
            if ((lOldFinancierAuctionSettingBean != null) 
                    && (FinancierAuctionSettingBean.ApprovalStatus.Submitted.equals(lOldFinancierAuctionSettingBean.getApprovalStatus())))
                throw new CommonBusinessException("Cannot modify. Record is already submitted to checker for approval");
            pFinancierAuctionSettingBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Draft);
        }
        if (lOldFinancierAuctionSettingBean==null) {
            pFinancierAuctionSettingBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, "FinancierAuctionSettings.id"));
            pFinancierAuctionSettingBean.setUtilised(BigDecimal.ZERO);
            pFinancierAuctionSettingBean.setBidLimitUtilised(BigDecimal.ZERO);
            pFinancierAuctionSettingBean.setRecordCreator(pUserBean.getId());
            validate(lConnection, pFinancierAuctionSettingBean);
            financierAuctionSettingProvDAO.insert(lConnection, pFinancierAuctionSettingBean);
            financierAuctionSettingDAO.insertAudit(lConnection, pFinancierAuctionSettingBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        } else {
            pFinancierAuctionSettingBean.setId(lOldFinancierAuctionSettingBean.getId());
            financierAuctionSettingProvDAO.getBeanMeta().copyBean(pFinancierAuctionSettingBean, lOldFinancierAuctionSettingBean, BeanMeta.FIELDGROUP_UPDATE, null);
            lOldFinancierAuctionSettingBean.setRecordUpdator(pUserBean.getId());
            validate(lConnection, lOldFinancierAuctionSettingBean);
            if (financierAuctionSettingProvDAO.update(lConnection, lOldFinancierAuctionSettingBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            financierAuctionSettingDAO.insertAudit(lConnection, lOldFinancierAuctionSettingBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            pFinancierAuctionSettingBean = lOldFinancierAuctionSettingBean;
        }
        if (FinancierAuctionSettingBean.ApprovalStatus.Approved.equals(pFinancierAuctionSettingBean.getApprovalStatus()))
            copyToFinal(lConnection, pFinancierAuctionSettingBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void updateApprovalStatus(ExecutionContext pExecutionContext, FinancierAuctionSettingBean pFinancierAuctionSettingBean, 
            IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        FinancierAuctionSettingBean lOldFinancierAuctionSettingBean = findBean(pExecutionContext, pFinancierAuctionSettingBean);
        if ((pFinancierAuctionSettingBean.getLevel() != null) && (pFinancierAuctionSettingBean.getLevel() != lOldFinancierAuctionSettingBean.getLevel()))
            throw new CommonBusinessException("Level mismatch");
        // access check
        switch (pFinancierAuctionSettingBean.getApprovalStatus()) {
        case Submitted:
        case Deleted:
            if (FinancierAuctionSettingBean.ApprovalStatus.Submitted.equals(lOldFinancierAuctionSettingBean.getApprovalStatus()))
                throw new CommonBusinessException("Cannot perform action. Record is already submitted to the checker");
            break;
        case Approved:
        case Returned:
        case Rejected:
            pFinancierAuctionSettingBean.setCheckerAUId(pUserBean.getId());
            if (!appUserBO.isValidChecker(lConnection, lOldFinancierAuctionSettingBean.getMakerAUId(), 
                    pUserBean.getId(), getCheckerType(lOldFinancierAuctionSettingBean.getLevel())))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            if (lOldFinancierAuctionSettingBean.getApprovalStatus() != FinancierAuctionSettingBean.ApprovalStatus.Submitted)
                throw new CommonBusinessException("Cannot perform action. Record is not yet submitted to the checker");
            break;
        }
        financierAuctionSettingDAO.getBeanMeta().copyBean(pFinancierAuctionSettingBean, 
                lOldFinancierAuctionSettingBean, FinancierAuctionSettingBean.FIELDGROUP_UPDATEAPPROVALSTATUS, null);
        lOldFinancierAuctionSettingBean.setRecordUpdator(pUserBean.getId());
        if (FinancierAuctionSettingBean.ApprovalStatus.Rejected.equals(lOldFinancierAuctionSettingBean.getApprovalStatus())) {
            // in case of rejection copy the final record back on to provisional record
            financierAuctionSettingDAO.insertAudit(lConnection, lOldFinancierAuctionSettingBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            FinancierAuctionSettingBean lFinalBean = financierAuctionSettingDAO.findByPrimaryKey(lConnection, lOldFinancierAuctionSettingBean);
            if (lFinalBean != null)
                financierAuctionSettingProvDAO.update(lConnection, lFinalBean, BeanMeta.FIELDGROUP_UPDATE);
            else {
                lOldFinancierAuctionSettingBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Deleted);
                financierAuctionSettingProvDAO.update(lConnection, lOldFinancierAuctionSettingBean, FinancierAuctionSettingBean.FIELDGROUP_UPDATEAPPROVALSTATUS);
            }
        } else {
            financierAuctionSettingProvDAO.update(lConnection, lOldFinancierAuctionSettingBean, FinancierAuctionSettingBean.FIELDGROUP_UPDATEAPPROVALSTATUS);
            financierAuctionSettingDAO.insertAudit(lConnection, lOldFinancierAuctionSettingBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            if (FinancierAuctionSettingBean.ApprovalStatus.Approved.equals(lOldFinancierAuctionSettingBean.getApprovalStatus())) {
                copyToFinal(lConnection, lOldFinancierAuctionSettingBean);
            }
        }
        pExecutionContext.commitAndDispose();
    }
    
    private MakerCheckerMapBean.CheckerType getCheckerType(FinancierAuctionSettingBean.Level pLevel) {
        switch (pLevel) {
        case Financier_Self:
            return  MakerCheckerMapBean.CheckerType.Platform_Limit;
        case Financier_Buyer:
            return MakerCheckerMapBean.CheckerType.Buyer_Limit;
        case Financier_Buyer_Seller:
            return MakerCheckerMapBean.CheckerType.Buyer_Seller_Limit;
        case Financier_User:
            return MakerCheckerMapBean.CheckerType.User_Limit;
        }
        return null;
    }
    private void copyToFinal(Connection pConnection, FinancierAuctionSettingBean pFinancierAuctionSettingBean) throws Exception {
        FinancierAuctionSettingBean lOldFinancierAuctionSettingBean = financierAuctionSettingDAO.findByPrimaryKey(pConnection, pFinancierAuctionSettingBean);
        if (lOldFinancierAuctionSettingBean == null) {
            financierAuctionSettingDAO.insert(pConnection, pFinancierAuctionSettingBean);
        } else {
            financierAuctionSettingDAO.update(pConnection, pFinancierAuctionSettingBean, BeanMeta.FIELDGROUP_UPDATE);
        }
    }
    
    private void copyToProvisional(Connection pConnection, FinancierAuctionSettingBean pFinancierAuctionSettingBean) throws Exception {
        FinancierAuctionSettingBean lOldFinancierAuctionSettingBean = financierAuctionSettingDAO.findByPrimaryKey(pConnection, pFinancierAuctionSettingBean);
        if (lOldFinancierAuctionSettingBean == null) {
            financierAuctionSettingDAO.insert(pConnection, pFinancierAuctionSettingBean);
        } else {
            financierAuctionSettingDAO.update(pConnection, pFinancierAuctionSettingBean, BeanMeta.FIELDGROUP_UPDATE);
        }
    }

    private void validate(Connection pConnection, FinancierAuctionSettingBean pFinancierAuctionSettingBean) throws Exception{
    	//
    	String lMandatoryFieldGroup = null, lNAFieldGroup = null; //NA=NotApplicable (to be made blank);
        switch (pFinancierAuctionSettingBean.getLevel()) {
	        case Financier_Self:
	        	lMandatoryFieldGroup = "financierSelf";
	        	lNAFieldGroup = "financierSelfX";
	            break;
	        case Financier_Buyer:
	        	lMandatoryFieldGroup = "buyer";
	        	lNAFieldGroup = "buyerX";
	            break;
	        case Financier_Buyer_Seller:
	        	lMandatoryFieldGroup = "buyerSeller";
	        	lNAFieldGroup = "buyerSellerX";
	            break;
	        case Financier_User:
	        	lMandatoryFieldGroup = "user";
	        	lNAFieldGroup = "userX";
	            break;
	        case System_Buyer:
	            break;
        }
        List<BeanFieldMeta> lBeanFieldMetaList = null;
        if(CommonUtilities.hasValue(lMandatoryFieldGroup)){
        	Object lValue = null;
        	lBeanFieldMetaList = financierAuctionSettingDAO.getBeanMeta().getFieldMetaList(lMandatoryFieldGroup, null);
        	for(BeanFieldMeta lBeanFieldMeta : lBeanFieldMetaList){
        		lValue = lBeanFieldMeta.getProperty(pFinancierAuctionSettingBean);
        		if(lValue==null && !"expiryDate".equals(lBeanFieldMeta.getName())){
        			throw new CommonValidationException(lBeanFieldMeta.getLabel() + " is mandatory.");
        		}
        	}
        }
        if(FinancierAuctionSettingBean.Level.Financier_Buyer.equals(pFinancierAuctionSettingBean.getLevel())){
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pFinancierAuctionSettingBean.getFinancier());
            if (TredsHelper.getInstance().isLocationwiseSettlementEnabled(pConnection, lAppEntityBean.getCdId(),false)){
            	if (pFinancierAuctionSettingBean.getFinClId()== null){
            		throw new CommonValidationException("Financier location is mandatory.");
            	}
            }
        }
        if(CommonUtilities.hasValue(lNAFieldGroup)){
        	lBeanFieldMetaList = financierAuctionSettingDAO.getBeanMeta().getFieldMetaList(lNAFieldGroup, null);
        	financierAuctionSettingDAO.getBeanMeta().clearBean(pFinancierAuctionSettingBean, lBeanFieldMetaList);
        }
        if (FinancierAuctionSettingBean.Level.Financier_User.equals(pFinancierAuctionSettingBean.getLevel()))
            pFinancierAuctionSettingBean.setBidLimit(pFinancierAuctionSettingBean.getLimit());
    }

    
    public Map<String, Object>  getTotalFinancierLimit(Connection pConnection, IAppUserBean pAppUserBean, String pPurchaserCode) throws Exception{
    	Map<String, Object> lData = new HashMap<String, Object>();        	
    	StringBuilder lSql = new StringBuilder();
    	//
    	AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
    	if(lAEBean.isPurchaser())
    		pPurchaserCode = pAppUserBean.getDomain();
    	//
    	lSql.append("SELECT FASPURCHASER AS FASPurchaser ");
    	lSql.append(" ,COUNT(*) AS FASId ");
    	lSql.append(" ,SUM(FASLIMIT) FASLimit ");
    	lSql.append(", SUM(FASUTILISED) FASUtilised ");
    	lSql.append(" FROM FINANCIERAUCTIONSETTINGS ");
    	lSql.append(" WHERE FASRECORDCREATOR > 0 ");
    	lSql.append(" AND FASLEVEL = ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode()));
    	lSql.append(" AND FASAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.ApprovalStatus.Approved.getCode()));
    	lSql.append(" AND FASACTIVE = ").append(DBHelper.getInstance().formatString(FinancierAuctionSettingBean.Active.Active.getCode()));
		lSql.append(" AND ( FASEXPIRYDATE IS NULL  OR FASEXPIRYDATE >= ").append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append("  ) ");
    	lSql.append(" AND FASPURCHASER = ").append(DBHelper.getInstance().formatString(pPurchaserCode));
    	lSql.append(" GROUP BY FASLEVEL, FASPURCHASER ");    	
    	//
    	//
        FinancierAuctionSettingBean lFASBean = financierAuctionSettingDAO.findBean(pConnection, lSql.toString());
        //
    	lData.put("purchaser", pPurchaserCode);
        if(lFASBean!=null){
        	lData.put("finCount", lFASBean.getId());
        	lData.put("limit", lFASBean.getLimit());
        	lData.put("utilised", lFASBean.getUtilised());
        	lData.put("unUtilised", lFASBean.getLimit().subtract(lFASBean.getUtilised()));
        	lData.put("utilisedPerc", lFASBean.getUtilPercent());
        	lData.put("unUtilisedPerc", BigDecimal.valueOf(100).subtract(lFASBean.getUtilPercent()));
        }
    	return lData;
    }
}
