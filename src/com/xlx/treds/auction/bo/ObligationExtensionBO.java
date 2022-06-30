package com.xlx.treds.auction.bo;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.github.mustachejava.Mustache;
import com.xlx.common.messaging.CommonMessageSender.MessageBean;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.AppConstants.EmailSenders;
import com.xlx.treds.AppConstants.EntityEmail;
import com.xlx.treds.NotificationInfo;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.IObligation;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean.Status;
import com.xlx.treds.auction.bean.ObligationExtensionPenaltyBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PenaltyDetailBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.GstSummaryBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.user.bean.AppUserBean;

public class ObligationExtensionBO {
    
    private GenericDAO<ObligationExtensionBean> obligationExtensionDAO;
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitDAO;
    private GenericDAO<ObligationExtensionPenaltyBean> obligationExtensionPenaltyDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    
    private static Long PURCHASER_PENDING = Long.valueOf(0);
    private static Long PURCHASER_SentForApproval = Long.valueOf(1);
    private static Long PURCHASER_ForBidApproval  = Long.valueOf(2);
    private static Long PURCHASER_APPROVED  = Long.valueOf(3);
    private static Long PURCHASER_REJECTED = Long.valueOf(4);
    private static Long PURCHASER_EXPIRED = Long.valueOf(5);
    
    private static Long FINANCIER_PENDING = Long.valueOf(0);
    private static Long FINANCIER_FORAPPROVAL = Long.valueOf(1);
    private static Long FINANCIER_APPROVED = Long.valueOf(2);
    private static Long FINANCIER_REJECTED = Long.valueOf(3);
    private static Long FINANCIER_EXPIRED = Long.valueOf(4);
    
    public ObligationExtensionBO() {
        super();
        obligationExtensionDAO = new GenericDAO<ObligationExtensionBean>(ObligationExtensionBean.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        obligationExtensionPenaltyDAO = new GenericDAO<ObligationExtensionPenaltyBean>(ObligationExtensionPenaltyBean.class);
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        obligationSplitDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
    }
    
    public void setTabs(ObligationExtensionBean pBean, Boolean pIsPurchaser) {
		if (Boolean.TRUE.equals(pIsPurchaser)) {
			switch (pBean.getStatus()) {
			case Pending:
			case Returned:
				pBean.setTab(PURCHASER_PENDING);
				return;
			case ForApproval:
			case BidReturned:
				pBean.setTab(PURCHASER_SentForApproval);
				return;
			case BidApproval:
				pBean.setTab(PURCHASER_ForBidApproval);
				return;
			case Approved:
				pBean.setTab(PURCHASER_APPROVED);
				return;
			case Rejected:
				pBean.setTab(PURCHASER_REJECTED);
				return;
			case Expired:
				pBean.setTab(PURCHASER_EXPIRED);
				return;
			}
		}else {
			switch (pBean.getStatus()) {
			case ForApproval:
			case BidReturned:
				pBean.setTab(FINANCIER_PENDING);
				return;
			case BidApproval:
				pBean.setTab(FINANCIER_FORAPPROVAL);
				return;
			case Approved:
				pBean.setTab(FINANCIER_APPROVED);
				return;
			case Rejected:
				pBean.setTab(FINANCIER_REJECTED);
				return;
			case Expired:
				pBean.setTab(FINANCIER_EXPIRED);
				return;
			}
		}
		
	}
    
    public ObligationExtensionBean findBean(ExecutionContext pExecutionContext, 
        ObligationExtensionBean pFilterBean, IAppUserBean pAppUserBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        ObligationExtensionBean lObligationExtensionBean = obligationExtensionDAO.findByPrimaryKey(lConnection, pFilterBean);
        //
        HashMap<String, Object> lGlobalSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONEXTENSION);
        Boolean lIsExtensionAllowed = (Boolean)lGlobalSettings.get(AppConstants.ATTRIBUTE_ALLOWOBLIGATIONEXTENSION);
        Long lMaxDaysForExtension = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXDAYSFOREXTENSION);
        Long lMaxGracePeriod = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXGRACEPERIOD);
        //
        FactoringUnitBean  lFUBean = null;
        if (lObligationExtensionBean == null) {
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
            if (!lAppEntityBean.isPurchaser())
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            // debit obligation
            IObligation lFilterBean = new ObligationBean();
            lFilterBean.setId(pFilterBean.getObId());
            IObligation lObligationBean = (ObligationBean)obligationDAO.findByPrimaryKey(lConnection, (ObligationBean) lFilterBean);
            lFUBean = getFactoringUnitBean(lConnection, lObligationBean.getId());
            if (lObligationBean == null) {
            	throw new CommonBusinessException("Obligation does not exist");
            }
            if (FactoringUnitBean.Status.Leg_1_Failed.equals(lFUBean.getStatus())) {
            	throw new CommonBusinessException("Extension can be sought only for obligations whose Leg 1 is settled.");
            }
            if (lObligationBean.getType() != ObligationBean.Type.Leg_2) {
            	throw new CommonBusinessException("Extension can be sought only for Leg 2 obligations.");
            }
            if (!lObligationBean.getTxnEntity().equals(pAppUserBean.getDomain())) {
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            }
            if (CommonAppConstants.Yes.Yes.equals(((ObligationBean)lObligationBean).getIsUpfrontOblig())) {
            	throw new CommonBusinessException("Interest Obligations cannot be modified.");
            }
            if(!(lObligationBean.getStatus() == ObligationBean.Status.Failed || lObligationBean.getStatus() == ObligationBean.Status.Ready ) || 
            		(lObligationBean.getType() != ObligationBean.Type.Leg_2) || 
            		(lObligationBean.getTxnType() != ObligationBean.TxnType.Debit)){
            	if ((ObligationBean.Status.Success.equals(lObligationBean.getStatus()) &&  !ObligationBean.Type.Leg_2.equals(lObligationBean.getStatus()) && !lObligationBean.getAmount().equals(lObligationBean.getSettledAmount()))){
            		//do nothing
                }else{
                	throw new CommonBusinessException("Extension can be sought only for failed or ready Leg 2 debit obligations.");
                }
        	}
            if (ObligationBean.Status.Extended.equals(lObligationBean.getStatus()) 
            		||ObligationBean.Status.Shifted.equals(lObligationBean.getStatus()) ) {
            	throw new CommonBusinessException("Invalid Status.");
            }
                
            // credit obligation financier
            lFilterBean.setId(null);
            lFilterBean.setFuId(lObligationBean.getFuId());
            lFilterBean.setDate(lObligationBean.getDate());
            lFilterBean.setType(ObligationBean.Type.Leg_2);
            lFilterBean.setTxnType(ObligationBean.TxnType.Credit);
            IObligation lCreditObligationBean = (ObligationBean)obligationDAO.findBean(lConnection, (ObligationBean) lFilterBean);
            if (lCreditObligationBean == null)
                throw new CommonBusinessException("Could not get corresponding credit leg of the obligation");
            // check global settings. is extension allowd, is current date within maxDaysForExtension
            if ((lIsExtensionAllowed == null) || (!lIsExtensionAllowed.booleanValue()))
                throw new CommonBusinessException("Obligation extension not allowed by platform");
            if (lMaxDaysForExtension != null) {
                Date lDate = TredsHelper.getInstance().getBusinessDate();
                if (OtherResourceCache.getInstance().getDiffInDays(lDate, lObligationBean.getDate()) > lMaxDaysForExtension.intValue())
                    throw new CommonBusinessException("Obligation extension cannot be done after " + lMaxDaysForExtension + " days of original due date");
            }
            
            lObligationExtensionBean = new ObligationExtensionBean();
            lObligationExtensionBean.setObId(lObligationBean.getId());
            lObligationExtensionBean.setCreditObId(lCreditObligationBean.getId());
            lObligationExtensionBean.setPurchaser(lObligationBean.getTxnEntity());
            lObligationExtensionBean.setFinancier(lCreditObligationBean.getTxnEntity());
            lObligationExtensionBean.setOldDate(lObligationBean.getDate());
            lObligationExtensionBean.setCurrency(lObligationBean.getCurrency());
            if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO) == 1) {
            	lObligationExtensionBean.setOldAmount(lFUBean.getFactoredAmount());
            }else {
            	lObligationExtensionBean.setOldAmount(getFailedAmount(lConnection, lObligationBean.getId(), lObligationBean.getStatus()));
            }
            if (pFilterBean.getUpfrontCharge()!=null) {
            	lObligationExtensionBean.setUpfrontCharge(Yes.Yes);
        	}
        } else {
            if (!lObligationExtensionBean.getPurchaser().equals(pAppUserBean.getDomain()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            IObligation lFilterBean = new ObligationBean();
            lFilterBean.setId(pFilterBean.getObId());
            IObligation lObligationBean = (ObligationBean)obligationDAO.findByPrimaryKey(lConnection, (ObligationBean) lFilterBean);
            lFUBean = getFactoringUnitBean(lConnection, lObligationBean.getId());
            if (ObligationBean.Status.Extended.equals(lObligationBean.getStatus()) 
            		||ObligationBean.Status.Shifted.equals(lObligationBean.getStatus()) ) {
            	throw new CommonBusinessException("Invalid Status.");
            }
              
        }
        // check financer and financier purchaser settings. is extension allowed, 
        ObligationExtensionPenaltyBean lPenaltyBean = new ObligationExtensionPenaltyBean();
        lPenaltyBean.setFinancier(lObligationExtensionBean.getFinancier());
        lPenaltyBean.setPurchaser(ObligationExtensionPenaltyBean.DEFAULT);
        ObligationExtensionPenaltyBean lDefaultPenaltyBean = obligationExtensionPenaltyDAO.findByPrimaryKey(lConnection, lPenaltyBean);
        lPenaltyBean.setPurchaser(lObligationExtensionBean.getPurchaser());
        ObligationExtensionPenaltyBean lPurchaserPenaltyBean = obligationExtensionPenaltyDAO.findByPrimaryKey(lConnection, lPenaltyBean);
        // merge
        if (lDefaultPenaltyBean != null) {
            lPenaltyBean.setAllowExtension(lDefaultPenaltyBean.getAllowExtension());
            lPenaltyBean.setMaxExtension(lDefaultPenaltyBean.getMaxExtension());
            lPenaltyBean.setPenaltyList(lDefaultPenaltyBean.getPenaltyList());
        }
        if (lPurchaserPenaltyBean != null) {
            if ((lPenaltyBean.getAllowExtension() == null) || (lPurchaserPenaltyBean.getAllowExtension() == CommonAppConstants.YesNo.No))
                lPenaltyBean.setAllowExtension(lPurchaserPenaltyBean.getAllowExtension());
            if (lPenaltyBean.getMaxExtension() == null)
                lPenaltyBean.setMaxExtension(lPurchaserPenaltyBean.getMaxExtension());
            else if ((lPurchaserPenaltyBean.getMaxExtension() != null) && (lPurchaserPenaltyBean.getMaxExtension().compareTo(lPenaltyBean.getMaxExtension()) < 0))
                lPenaltyBean.setMaxExtension(lPurchaserPenaltyBean.getMaxExtension());
            if ((lPurchaserPenaltyBean.getPenaltyList() != null) && (lPurchaserPenaltyBean.getPenaltyList().size() > 0))
                lPenaltyBean.setPenaltyList(lPurchaserPenaltyBean.getPenaltyList());
        }
        if (lPenaltyBean.getAllowExtension() != CommonAppConstants.YesNo.Yes) 
            throw new CommonBusinessException("Obligation extension not allowed by financier");
        
        lObligationExtensionBean.setPenaltySetting(lPenaltyBean);
        
        
        //TODO: to check lObligationBean.getDate() + maxgraceperiodfromregistry should be lessthan or equalto businessdate        
        if(TredsHelper.getInstance().getBusinessDate().after(CommonUtilities.addDays(lObligationExtensionBean.getOldDate(), lMaxGracePeriod.intValue())) ) {
        	throw new CommonBusinessException("Max Grace period to place request exceeds Business date.");
        }
        
        // if new date provided then check and compute penalties
        if (pFilterBean.getNewDate() != null) {
            lObligationExtensionBean.setNewDate(pFilterBean.getNewDate());
            long lExtension = OtherResourceCache.getInstance().getDiffInDays(lObligationExtensionBean.getNewDate(), lObligationExtensionBean.getOldDate());
            lObligationExtensionBean.setUpfrontCharge(pFilterBean.getUpfrontCharge());
            long lTotalExtension = lExtension;
            ObligationBean lFilterBean = new ObligationBean();
            lFilterBean.setId(lObligationExtensionBean.getObId());
            ObligationBean lObligationBean = obligationDAO.findByPrimaryKey(lConnection,  lFilterBean);
            if ( lObligationBean.getExtendedDays()!=null ) {
            	lTotalExtension = lTotalExtension+lObligationBean.getExtendedDays();
            }
            if (lTotalExtension <= 0)
                throw new CommonBusinessException("New date should be greater than the old obligation date");
            if (!lObligationExtensionBean.getNewDate().equals(OtherResourceCache.getInstance().getNextClearingDate(lObligationExtensionBean.getNewDate(), 0)))
                throw new CommonBusinessException("New Obligation date is not a working clearing date");
            if (lTotalExtension > lMaxDaysForExtension.longValue())
                throw new CommonBusinessException("Obligation extension cannot be greater than the platform limit of " + lMaxDaysForExtension + " days");
            if ((lPenaltyBean.getMaxExtension() != null) && (lTotalExtension > lPenaltyBean.getMaxExtension().longValue()))
                throw new CommonBusinessException("Obligation extension cannot be greater than the financier limit of " + lPenaltyBean.getMaxExtension() + " days");
            if ((lPenaltyBean.getMaxExtension() != null) && (lTotalExtension > lPenaltyBean.getMaxExtension().longValue()))
                throw new CommonBusinessException("Obligation of the Factoring unit is extended multiple times and extension cannot be greater than the financier limit of " + lPenaltyBean.getMaxExtension() + " days");
            
            // compute rate using applicable slab
            BigDecimal lRate = null;
            long lSlab = 99999;
            if (lPenaltyBean.getPenaltyList()!=null) {
            	for (PenaltyDetailBean lPenaltyDetailBean : lPenaltyBean.getPenaltyList()) {
                    long lUptoDays = lPenaltyDetailBean.getUptoDays().longValue();
                    if ((lUptoDays >= lTotalExtension) && (lUptoDays < lSlab)) {
                        lSlab = lUptoDays;
                        lRate = lPenaltyDetailBean.getRate();
                    }
                }
            }
//            if (lRate == null)
//                throw new CommonBusinessException("Penalty rate not defined for extension period of " + lExtension + " days");
            if (lRate==null) {
            	lRate = BigDecimal.ZERO;
            }
            lObligationExtensionBean.setPenaltyRate(lRate);
            //calculate the interest for the extention and add to the OldAmount before computing penalty
            BigDecimal lOldAmount =  lObligationExtensionBean.getOldAmount();
            BigDecimal lInterest = new BigDecimal(0);
            Long lExtensionForCalculation = null;
            BigDecimal lPrevInterest = null;
            if (lFUBean==null) {
        		lFUBean = getFactoringUnitBean(lConnection, lObligationExtensionBean.getObId());
        	}
            boolean lAddPrevInterest = false;
            if (lObligationBean.getExtendedDays()!=null) {
            	lExtensionForCalculation = lExtension;
            	if (!CommonAppConstants.Yes.Yes.equals(lObligationBean.getIsUpfront())) {
            		lAddPrevInterest = true;
            		lPrevInterest= lOldAmount.subtract(lFUBean.getFactoredAmount());
            		lOldAmount = lFUBean.getFactoredAmount();
            	}
        	}else {
        		lExtensionForCalculation = lTotalExtension;
        	}
            BigDecimal lIntrestRate = null;
            if (lObligationExtensionBean.getInterestRate()==null) {
                if(lFUBean!=null){
                	lIntrestRate = lFUBean.getAcceptedRate();
                }
            }else {
            	lIntrestRate = lObligationExtensionBean.getInterestRate();
            }
            lInterest = lOldAmount.multiply(lIntrestRate).multiply(new BigDecimal(lExtensionForCalculation));
        	lInterest = lInterest.divide(AppConstants.DAYS_IN_YEAR, MathContext.DECIMAL128).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
        	lObligationExtensionBean.setInterestRate(lFUBean.getAcceptedRate());
        	lObligationExtensionBean.setInterest(lInterest);
            BigDecimal lPenalty = lOldAmount.multiply(lRate).multiply(new BigDecimal(lExtensionForCalculation));
            lPenalty = lPenalty.divide(AppConstants.DAYS_IN_YEAR, MathContext.DECIMAL128).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
            lObligationExtensionBean.setPenalty(lPenalty);
            lObligationExtensionBean.setNewAmount(lOldAmount.add(lPenalty).add(lObligationExtensionBean.getInterest()));
            if (lAddPrevInterest) {
            	lObligationExtensionBean.setNewAmount(lObligationExtensionBean.getNewAmount().add(lPrevInterest));
            }
         }
        return lObligationExtensionBean;
    }
    
    private BigDecimal getFailedAmount(Connection pConnection, Long pObligationId, ObligationBean.Status pL2ObliStatus) throws SQLException{
    	BigDecimal lAmount = BigDecimal.ZERO;
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT SUM(OBSAMOUNT) FailAmt FROM ObligationSplits WHERE OBSRecordVersion > 0 ");
    	lSql.append(" AND OBSObId = ").append(pObligationId);
    	lSql.append(" AND OBSStatus = ").append(DBHelper.getInstance().formatString(pL2ObliStatus.getCode()));
    	//System.out.println(lSql.toString());
		Statement lStatement = null;
		ResultSet lResultSet = null;
		try {
			lStatement = pConnection.createStatement();
			lResultSet = lStatement.executeQuery(lSql.toString());
			while (lResultSet.next()){
				lAmount = new BigDecimal(lResultSet.getString("FailAmt"));
		    }
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
		return lAmount;
    }
    
    private FactoringUnitBean getFactoringUnitBean(Connection pConnection, Long pObligationId) throws Exception{
    	FactoringUnitBean lFUBean = null;
    	ObligationBean lObliFilter = new ObligationBean();
    	lObliFilter.setId(pObligationId);
    	ObligationBean lObligationBean = obligationDAO.findBean(pConnection, lObliFilter);
    	if(lObligationBean != null){
    		FactoringUnitBean lFUFilterBean = new FactoringUnitBean();
    		lFUFilterBean.setId(lObligationBean.getFuId());
    		lFUBean = factoringUnitDAO.findBean(pConnection, lFUFilterBean);
    	}
    	return lFUBean;
    }
    
    public List<ObligationExtensionBean> findList(ExecutionContext pExecutionContext, ObligationExtensionBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        if (lAppEntityBean.isPurchaser())
            pFilterBean.setPurchaser(pUserBean.getDomain());
        else if (lAppEntityBean.isFinancier())
            pFilterBean.setFinancier(pUserBean.getDomain());
        else if (!lAppEntityBean.isPlatform())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        List<ObligationExtensionBean> lObligationExtensions = null;
        Connection lConnection = pExecutionContext.getConnection();
        StringBuilder lFilter = new StringBuilder();
        
        obligationExtensionDAO.appendAsSqlFilter(lFilter, pFilterBean, false);
        
        StringBuilder lSql = new StringBuilder();
        boolean lCheckAccessToLocations = TredsHelper.getInstance().checkAccessToLocations((AppUserBean)pUserBean);
        lSql.append("SELECT OBLIGATIONEXTENSIONS.*,FUPURCHASERLEG1INTEREST+FUSUPPLIERLEG1INTEREST+FUPURCHASERLEG2INTEREST \"OEOriginalInterest\" FROM OBLIGATIONEXTENSIONS ");
        lSql.append(" join OBLIGATIONS on (OBID=OEOBID) ");
        lSql.append(" join Factoringunits on (obfuid = fuid)");
		if (lCheckAccessToLocations){
        	lSql.append(" join instruments on (infuid = fuid)");
		}
        lSql.append(" WHERE 1=1 ");
		if (lCheckAccessToLocations){
			if(lAppEntityBean.isPurchaser()){
        	lSql.append(" AND INPurClId in (").append(TredsHelper.getInstance()
        			.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(") ");
			}else if(lAppEntityBean.isSupplier()){
	        	lSql.append(" AND INSupClId in (").append(TredsHelper.getInstance()
	        			.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(") ");
			}
        }
        if(lFilter.length() > 0 )
        	lSql.append(" AND ").append(lFilter);
        
        lObligationExtensions = obligationExtensionDAO.findListFromSql(lConnection, lSql.toString(), -1);
        if(lObligationExtensions!=null){
        	FactoringUnitBean lFUBean = null;
        	for (ObligationExtensionBean lOEBean : lObligationExtensions){
        		if (lOEBean.getInterestRate()==null) {
        			lFUBean = getFactoringUnitBean(lConnection, lOEBean.getObId());
        			if(lFUBean!=null){
            			lOEBean.setInterestRate(lFUBean.getAcceptedRate());
            		}
        		}
        		ObligationExtensionPenaltyBean lPenaltyBean = new ObligationExtensionPenaltyBean();
                lPenaltyBean.setFinancier(lOEBean.getFinancier());
                lPenaltyBean.setPurchaser(ObligationExtensionPenaltyBean.DEFAULT);
                ObligationExtensionPenaltyBean lDefaultPenaltyBean = obligationExtensionPenaltyDAO.findByPrimaryKey(lConnection, lPenaltyBean);
                lPenaltyBean.setPurchaser(lOEBean.getPurchaser());
                ObligationExtensionPenaltyBean lPurchaserPenaltyBean = obligationExtensionPenaltyDAO.findByPrimaryKey(lConnection, lPenaltyBean);
                if (lPurchaserPenaltyBean==null) {
                	if (lDefaultPenaltyBean!=null) {
                		if(!lDefaultPenaltyBean.getPenaltyList().isEmpty()) {
                			lOEBean.setPenaltyRateApplied(YesNo.Yes);
                		}
                	}
                	lOEBean.setPenaltyRateApplied(YesNo.No);
                }else {
                	if(lPurchaserPenaltyBean.getPenaltyList()!=null && !lPurchaserPenaltyBean.getPenaltyList().isEmpty()) {
                		lOEBean.setPenaltyRateApplied(YesNo.Yes);
            		}else {
            			lOEBean.setPenaltyRateApplied(YesNo.No);
            		}
                }
        		setTabs(lOEBean, lAppEntityBean.isPurchaser());
        	}
        }
        return lObligationExtensions;
    }
    
    public void save(ExecutionContext pExecutionContext, ObligationExtensionBean pObligationExtensionBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        ObligationExtensionBean lOldObligationExtensionBean = null;
        lOldObligationExtensionBean = findBean(pExecutionContext, pObligationExtensionBean, pUserBean);
        // check if penalty , penalty rate and new amount figures are equal
//        if (lOldObligationExtensionBean.getPenaltyRate().compareTo(pObligationExtensionBean.getPenaltyRate()) != 0)
//            throw new CommonBusinessException("Mismatch in penalty rates");
//        if (lOldObligationExtensionBean.getPenalty().compareTo(pObligationExtensionBean.getPenalty()) != 0)
//            throw new CommonBusinessException("Mismatch in penalty amount");
//        if (lOldObligationExtensionBean.getNewAmount().compareTo(pObligationExtensionBean.getNewAmount()) != 0)
//            throw new CommonBusinessException("Mismatch in new obligation amount");
        lOldObligationExtensionBean.setRemarks(pObligationExtensionBean.getRemarks());
//        obligationExtensionDAO.getBeanMeta().copyBean(pObligationExtensionBean, lOldObligationExtensionBean, BeanMeta.FIELDGROUP_UPDATE, null);
        boolean lSubmitMail = false;
        if (lOldObligationExtensionBean.getRecordCreator() == null) {
        	if(ObligationExtensionBean.Status.ForApproval.equals(pObligationExtensionBean.getStatus())){
        		lOldObligationExtensionBean.setStatus(pObligationExtensionBean.getStatus());
        		lOldObligationExtensionBean.setSubmitDate(new Date(System.currentTimeMillis()));
        		lSubmitMail = true;
        	}else {
        		lOldObligationExtensionBean.setStatus(ObligationExtensionBean.Status.Pending);
        	}
            lOldObligationExtensionBean.setRecordCreator(pUserBean.getId());
            FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, lOldObligationExtensionBean.getObId());
            Long lBillLoc = (lFUBean.getPurchaserBillLoc()!=null?lFUBean.getPurchaserBillLoc():lFUBean.getPurchaserSettleLoc());
            lOldObligationExtensionBean.setTredsCharge(TredsHelper.getInstance().getNormalPlanCharge(lConnection, lFUBean, lOldObligationExtensionBean.getPurchaser(), lOldObligationExtensionBean.getPurchaser(), lOldObligationExtensionBean.getOldAmount(), lBillLoc, lOldObligationExtensionBean.getTenor(),null));
            FactoringUnitBO lFuBo = new FactoringUnitBO();
            GstSummaryBean lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            if (lGstBean!=null) {
            	lFuBo.setGstSummary(lConnection, lFUBean, lOldObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lOldObligationExtensionBean.getTredsCharge(),ChargeType.Extension,(long) 1);
                lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,(long) 1);
            }else{
            	lFuBo.setGstSummary(lConnection, lFUBean, lOldObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lOldObligationExtensionBean.getTredsCharge(),ChargeType.Extension,null);
                lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            }
            lOldObligationExtensionBean.setTredsCharge(lGstBean.getTotalCharge());
            obligationExtensionDAO.insert(lConnection, lOldObligationExtensionBean);
            obligationExtensionDAO.insertAudit(lConnection, lOldObligationExtensionBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        } else {
            if (!(ObligationExtensionBean.Status.Pending.equals(lOldObligationExtensionBean.getStatus()) 
            	 || ObligationExtensionBean.Status.Returned.equals(lOldObligationExtensionBean.getStatus())) ) {
            	throw new CommonBusinessException("Obligation extension cannot be modified now.");
            }
            lOldObligationExtensionBean.setRecordUpdator(pUserBean.getId());
            FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, pObligationExtensionBean.getObId());
            Long lBillLoc = (lFUBean.getPurchaserBillLoc()!=null?lFUBean.getPurchaserBillLoc():lFUBean.getPurchaserSettleLoc());
            pObligationExtensionBean.setTredsCharge(TredsHelper.getInstance().getNormalPlanCharge(lConnection, lFUBean, pObligationExtensionBean.getPurchaser(), pObligationExtensionBean.getPurchaser(), pObligationExtensionBean.getOldAmount(), lBillLoc, pObligationExtensionBean.getTenor(),null));
            FactoringUnitBO lFuBo = new FactoringUnitBO();
            GstSummaryBean lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            if (lGstBean!=null) {
            	lFuBo.setGstSummary(lConnection, lFUBean, lOldObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lOldObligationExtensionBean.getTredsCharge(),ChargeType.Extension,(long) 1);
                lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,(long) 1);
            }else{
            	lFuBo.setGstSummary(lConnection, lFUBean, lOldObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lOldObligationExtensionBean.getTredsCharge(),ChargeType.Extension,null);
                lGstBean = lFUBean.getGstSummary(lOldObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            }
            pObligationExtensionBean.setTredsCharge(lGstBean.getTotalCharge());
            obligationExtensionDAO.update(lConnection, pObligationExtensionBean, BeanMeta.FIELDGROUP_UPDATE);
            obligationExtensionDAO.insertAudit(lConnection, lOldObligationExtensionBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        }
        pExecutionContext.commit();
        if (lSubmitMail) {
        	FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, lOldObligationExtensionBean.getObId());
            InstrumentBean lInstrumentBean = new InstrumentBean();
            lInstrumentBean.setFuId(lFUBean.getId());
            lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
            ObligationBean lObligBean = new ObligationBean();
            lObligBean.setId(lOldObligationExtensionBean.getObId());
            lObligBean = obligationDAO.findByPrimaryKey(lConnection, lObligBean);
    		Map<String, Object> lDataValues = null;
            lDataValues = new HashMap<>();
        	lDataValues.put("id", lFUBean.getId());
        	lDataValues.put("inId", lInstrumentBean.getId());
        	lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
    		lDataValues.put("createDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT, new Date(System.currentTimeMillis())));
        	lDataValues.put("amount", lFUBean.getAmount());
        	lDataValues.put("purName", lFUBean.getPurName());
        	lDataValues.put("finName", lFUBean.getFinName());
        	lDataValues.put("oldDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligBean.getOriginalDate()));
        	lDataValues.put("newDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lOldObligationExtensionBean.getNewDate()));
        	lDataValues.put("extendedRate",lOldObligationExtensionBean.getExtendedBidRate());
        	String lNotificationType = null;
        	String lTemplate = null;
        	// email
            List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONREQUEST_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getFinancier(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONREQUEST;
        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(lConnection, lNotificationInfos); 
            if (!lEmailIds.isEmpty()) {
            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
                EmailSender.getInstance().addMessage(lTemplate,lDataValues);
            }
    	}
        pExecutionContext.dispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, ObligationExtensionBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        ObligationExtensionBean lObligationExtensionBean = findBean(pExecutionContext, pFilterBean, pUserBean);
        if (!(ObligationExtensionBean.Status.Pending .equals(lObligationExtensionBean.getStatus()) 
        		|| ObligationExtensionBean.Status.BidApproval.equals(lObligationExtensionBean.getStatus())
        		|| ObligationExtensionBean.Status.ForApproval.equals(lObligationExtensionBean.getStatus())))
            throw new CommonBusinessException("Obligation extension cannot be removed now.");
        lObligationExtensionBean.setRecordUpdator(pUserBean.getId());
        obligationExtensionDAO.delete(lConnection, lObligationExtensionBean);        
        obligationExtensionDAO.insertAudit(lConnection, lObligationExtensionBean, GenericDAO.AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commit();
        if (!ObligationExtensionBean.Status.Pending .equals(lObligationExtensionBean.getStatus())){
        	FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, lObligationExtensionBean.getObId());
        	InstrumentBean lInstrumentBean = new InstrumentBean();
            lInstrumentBean.setFuId(lFUBean.getId());
            lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
            ObligationBean lObligBean = new ObligationBean();
            lObligBean.setId(lObligationExtensionBean.getObId());
            lObligBean = obligationDAO.findByPrimaryKey(lConnection, lObligBean);
    		Map<String, Object> lDataValues = null;
            lDataValues = new HashMap<>();
        	lDataValues.put("id", lFUBean.getId());
        	lDataValues.put("inId", lInstrumentBean.getId());
        	lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
    		lDataValues.put("createDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getSubmitDate()));
        	lDataValues.put("amount", lFUBean.getAmount());
        	lDataValues.put("purName", lFUBean.getPurName());
        	lDataValues.put("finName", lFUBean.getFinName());
        	lDataValues.put("oldDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligBean.getOriginalDate()));
        	lDataValues.put("newDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getNewDate()));
        	lDataValues.put("extendedRate",lObligationExtensionBean.getExtendedBidRate());
        	String lNotificationType = null;
        	String lTemplate = null;
        	// email
            List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONBIDWITHDRAW_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getFinancier(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONWITHDRAW;
        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(lConnection, lNotificationInfos); 
        	pExecutionContext.dispose();
            if (!lEmailIds.isEmpty()) {
            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
                EmailSender.getInstance().addMessage(lTemplate,lDataValues);
            }
        }
        pExecutionContext.dispose();
    }
    

    public void updateStatus(ExecutionContext pExecutionContext, ObligationExtensionBean pObligationExtensionBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        ObligationExtensionBean lObligationExtensionBean = obligationExtensionDAO.findByPrimaryKey(lConnection, pObligationExtensionBean);
        if (lObligationExtensionBean == null)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        if (lAppEntityBean.isPurchaser()) {
        	if (ObligationExtensionBean.Status.Pending.equals(lObligationExtensionBean.getStatus()) ||
        			ObligationExtensionBean.Status.Returned.equals(lObligationExtensionBean.getStatus())) {
        		if (!ObligationExtensionBean.Status.ForApproval.equals(pObligationExtensionBean.getStatus())) {
        			throw new CommonBusinessException("Invalid Status");
        		}
        	}else if (ObligationExtensionBean.Status.BidApproval.equals(lObligationExtensionBean.getStatus())) {
        		if (!(ObligationExtensionBean.Status.BidReturned.equals(pObligationExtensionBean.getStatus())
        				|| ObligationExtensionBean.Status.Approved.equals(pObligationExtensionBean.getStatus()))) {
        			throw new CommonBusinessException("Invalid Status");
        		}
        	}else {
    			throw new CommonBusinessException("Invalid Status");
    		}
        }else if (lAppEntityBean.isFinancier()) {
        	if ( ObligationExtensionBean.Status.ForApproval.equals(lObligationExtensionBean.getStatus()) 
        			||ObligationExtensionBean.Status.BidReturned.equals(lObligationExtensionBean.getStatus())) {
        		if (!(ObligationExtensionBean.Status.BidApproval.equals(pObligationExtensionBean.getStatus())
        				|| ObligationExtensionBean.Status.Rejected.equals(pObligationExtensionBean.getStatus())
        				|| ObligationExtensionBean.Status.Returned.equals(pObligationExtensionBean.getStatus()))) {
        			throw new CommonBusinessException("Invalid Status");
        		}
        	}else {
        		throw new CommonBusinessException("Invalid Status");
        	}
        }
        ConfirmationWindowBean  lConfirmationWindowBean = null;
        if (ObligationExtensionBean.Status.Approved.equals(pObligationExtensionBean.getStatus())) {
        	lConfirmationWindowBean = OtherResourceCache.getInstance().getConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
            if (lConfirmationWindowBean == null)
                throw new CommonBusinessException("Bid acceptance not allowed at this time.");
            if (lConfirmationWindowBean.getActive() == CommonAppConstants.YesNo.No)
                throw new CommonBusinessException("Bid acceptance has been stopped.");
            lObligationExtensionBean.setApproveDate(new Date(System.currentTimeMillis()));
            lObligationExtensionBean.setChargeDate(lConfirmationWindowBean.getSettlementDate());
        }
        String lCopyFieldGroup = ObligationExtensionBean.FIELDGROUP_UPDATESTATUS;
        if (Status.ForApproval.equals(pObligationExtensionBean.getStatus())){
        	lCopyFieldGroup = ObligationExtensionBean.FIELDGROUP_UPDATESTATUSSUBMIT;
        }else if(Status.Approved.equals(pObligationExtensionBean.getStatus())){
        	lCopyFieldGroup = ObligationExtensionBean.FIELDGROUP_UPDATESTATUSAPPROVE;
        }
        GstSummaryBean lGstBean = null;
        obligationExtensionDAO.getBeanMeta().copyBean(pObligationExtensionBean, lObligationExtensionBean, ObligationExtensionBean.FIELDGROUP_UPDATESTATUS, null);
        lObligationExtensionBean.setRecordUpdator(pUserBean.getId());
        String lFieldGrp = ObligationExtensionBean.FIELDGROUP_UPDATESTATUS;
        FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, lObligationExtensionBean.getObId());
        InstrumentBean lInstrumentBean = new InstrumentBean();
        lInstrumentBean.setFuId(lFUBean.getId());
        lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
        if (Status.ForApproval.equals(lObligationExtensionBean.getStatus())) {
        	lObligationExtensionBean.setSubmitDate(new Date(System.currentTimeMillis()));
        	if (lObligationExtensionBean.getTredsCharge()==null) {
                Long lBillLoc = (lFUBean.getPurchaserBillLoc()!=null?lFUBean.getPurchaserBillLoc():lFUBean.getPurchaserSettleLoc());
                lObligationExtensionBean.setTredsCharge(TredsHelper.getInstance().getNormalPlanCharge(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lObligationExtensionBean.getPurchaser(), lObligationExtensionBean.getOldAmount(), lBillLoc, lObligationExtensionBean.getTenor(),null));
                FactoringUnitBO lFuBo = new FactoringUnitBO();
                lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
                if (lGstBean!=null) {
                	lFuBo.setGstSummary(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lObligationExtensionBean.getTredsCharge(),ChargeType.Extension,(long) 1);
                    lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,(long) 1);
                }else{
                	lFuBo.setGstSummary(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lObligationExtensionBean.getTredsCharge(),ChargeType.Extension,null);
                    lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
                }
                lObligationExtensionBean.setTredsCharge(lGstBean.getTotalCharge());
        	}
        	lFieldGrp = ObligationExtensionBean.FIELDGROUP_UPDATESTATUSSUBMIT;
        }
        if (Status.Approved.equals(pObligationExtensionBean.getStatus())) {
        	Long lBillLoc = (lFUBean.getPurchaserBillLoc()!=null?lFUBean.getPurchaserBillLoc():lFUBean.getPurchaserSettleLoc());
            FactoringUnitBO lFuBo = new FactoringUnitBO();
            BigDecimal lTredsCharge = TredsHelper.getInstance().getNormalPlanCharge(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lObligationExtensionBean.getPurchaser(), lObligationExtensionBean.getOldAmount(), lBillLoc, lObligationExtensionBean.getTenor(),null);
            lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            if (lGstBean!=null) {
            	lFuBo.setGstSummary(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lTredsCharge,ChargeType.Extension,(long) 1);
            	lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,(long) 1);
            }else {
            	lFuBo.setGstSummary(lConnection, lFUBean, lObligationExtensionBean.getPurchaser(), lFUBean.getPurchaserBillLoc(), lTredsCharge,ChargeType.Extension,null);
            	lGstBean = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,null);
            }
            lFieldGrp = ObligationExtensionBean.FIELDGROUP_UPDATESTATUSAPPROVE;
        }
        obligationExtensionDAO.update(lConnection, lObligationExtensionBean,lFieldGrp );        
        obligationExtensionDAO.insertAudit(lConnection, lObligationExtensionBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        ObligationBean lObligBean = new ObligationBean();
        StringBuilder lSql = new StringBuilder();
        lObligBean.setId(lObligationExtensionBean.getObId());
        lObligBean = obligationDAO.findByPrimaryKey(lConnection, lObligBean);
        List<ObligationBean> lObligations = new ArrayList<ObligationBean>();
        List<ObligationBean> lUpfrontObligations = new ArrayList<ObligationBean>();
        ObligationBean lDebitBean = null;
        Statement lStatement = null;
        ObligationBean lUpFrontDebitBean = null;
        if (lObligationExtensionBean.getStatus() == ObligationExtensionBean.Status.Approved) {
            ObligationBean lFilterBean = new ObligationBean();
            lFilterBean.setId(lObligationExtensionBean.getObId());
            lDebitBean = obligationDAO.findByPrimaryKey(lConnection, lFilterBean);
            if (lDebitBean == null)
                throw new CommonBusinessException("Debit obligation not found");
            lFilterBean.setId(lObligationExtensionBean.getCreditObId());
            ObligationBean lCreditBean = obligationDAO.findByPrimaryKey(lConnection, lFilterBean);
            if (lCreditBean == null)
                throw new CommonBusinessException("Credit obligation not found");
//            
            lDebitBean.setStatus(ObligationBean.Status.Extended);
            lDebitBean.setRecordUpdator(pUserBean.getId());
            obligationDAO.update(lConnection, lDebitBean, ObligationBean.FIELDGROUP_UPDATESTATUS);
            lSql = new StringBuilder();
            lSql.append(" UPDATE OBLIGATIONSPLITS SET OBSSTATUS= ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Extended.getCode()));
            lSql.append(" ,OBSRecordUpdateTime = SYSDATE ");
            lSql.append(" WHERE OBSRECORDVERSION > 0 AND OBSOBID = ").append(lDebitBean.getId());
            try {
    			lStatement = lConnection.createStatement();
    		    int lCount = lStatement.executeUpdate(lSql.toString());
    		}catch(Exception lEx){  
    		}finally {
    			if (lStatement != null)
    				lStatement.close();
    		}
            
//            
            lCreditBean.setStatus(ObligationBean.Status.Extended);
            lCreditBean.setRecordUpdator(pUserBean.getId());
            obligationDAO.update(lConnection, lCreditBean, ObligationBean.FIELDGROUP_UPDATESTATUS);
            lSql = new StringBuilder();
            lSql.append(" UPDATE OBLIGATIONSPLITS SET OBSSTATUS= ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Extended.getCode()));
            lSql.append(" ,OBSRecordUpdateTime = SYSDATE ");
            lSql.append(" WHERE OBSRECORDVERSION > 0 AND OBSOBID = ").append(lCreditBean.getId());
            try {
    			lStatement = lConnection.createStatement();
    		    int lCount = lStatement.executeUpdate(lSql.toString());
    		}catch(Exception lEx){  
    		}finally {
    			if (lStatement != null)
    				lStatement.close();
    		}
//            
            lDebitBean.setStatus(ObligationBean.Status.Ready);
            lCreditBean.setStatus(ObligationBean.Status.Created);
            ObligationBean lTredsCreditBean = new ObligationBean();
            obligationDAO.getBeanMeta().copyBean(lCreditBean,lTredsCreditBean);
            lTredsCreditBean.setTxnEntity(AppConstants.DOMAIN_PLATFORM);
            lTredsCreditBean.setStatus(ObligationBean.Status.Created);
            lUpFrontDebitBean = new ObligationBean(); 
            obligationDAO.getBeanMeta().copyBean(lDebitBean,lUpFrontDebitBean);
            ObligationBean lUpFrontCreditBean = null;
            if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
            	lUpFrontCreditBean = new ObligationBean(); 
                obligationDAO.getBeanMeta().copyBean(lCreditBean,lUpFrontCreditBean);
            }
            for (ObligationBean lNewBean : new ObligationBean[]{lDebitBean, lCreditBean}) {
                lNewBean.setOldObligationId(lNewBean.getId());
                lNewBean.setId(TredsHelper.getInstance().getObligationId(lConnection));
                lNewBean.setDate(lObligationExtensionBean.getNewDate());
                 if(lFUBean.getPurchaser().equals(lNewBean.getTxnEntity())) {
                	 if (!CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                 		if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                 				&& CommonAppConstants.Yes.Yes.equals(lDebitBean.getIsUpfront())) {
                 			lNewBean.setAmount(lObligationExtensionBean.getNewAmount());
                 		}else if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                 				&& !CommonAppConstants.Yes.Yes.equals(lDebitBean.getIsUpfront())) {
                 			lNewBean.setAmount(lObligationExtensionBean.getNewAmount().add(lFUBean.getPurchaserLeg2Interest()));
                 		}else {
                 			lNewBean.setAmount(lObligationExtensionBean.getNewAmount().add(lFUBean.getPurchaserLeg2Interest()));
                 		}
             		}else {
             			lNewBean.setAmount(lObligationExtensionBean.getOldAmount());
             		}
                }else if(lFUBean.getFinancier().equals(lNewBean.getTxnEntity())) {
                	if (!CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                		if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                				&& CommonAppConstants.Yes.Yes.equals(lDebitBean.getIsUpfront())) {
                			lNewBean.setAmount(lObligationExtensionBean.getNewAmount());
                		}else if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                				&& !CommonAppConstants.Yes.Yes.equals(lDebitBean.getIsUpfront())) {
                			lNewBean.setAmount(lObligationExtensionBean.getNewAmount().add(lFUBean.getPurchaserLeg2Interest()));
                		}else {
                 			lNewBean.setAmount(lObligationExtensionBean.getNewAmount().add(lFUBean.getPurchaserLeg2Interest()));
                 		}
            		}else {
            			lNewBean.setAmount(lObligationExtensionBean.getOldAmount());
            		}
                }
	            long lExtension = OtherResourceCache.getInstance().getDiffInDays(lObligationExtensionBean.getNewDate(), lObligationExtensionBean.getOldDate());
	            if (lNewBean.getExtendedDays()!=null) {
	            	lNewBean.setExtendedDays(lNewBean.getExtendedDays() + lExtension);
	            }else {
	             	lNewBean.setExtendedDays(lExtension);
	            }
                if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                	lNewBean.setIsUpfront(Yes.Yes);
                }else {
                	lNewBean.setIsUpfront(null);
                }
                //TODO: no change in OriginalAmount
                lNewBean.setPfId(null);
                lNewBean.setFileSeqNo(null);
                lNewBean.setPayDetail1(null);
                lNewBean.setPayDetail2(null);
                lNewBean.setPayDetail3(null);
                lNewBean.setPayDetail4(null);
                lNewBean.setSettledAmount(null);
                lNewBean.setPaymentRefNo(null);
                lNewBean.setRespErrorCode(null);
                lNewBean.setRespRemarks(null);
                lNewBean.setSettledDate(null);
                lNewBean.setRecordCreator(pUserBean.getId());
                if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                	lNewBean.setIsUpfront(Yes.Yes);
                }else {
                	lNewBean.setIsUpfront(null);
                }
                obligationDAO.insert(lConnection, lNewBean);
            }
            lObligations.add(lDebitBean);
            lObligations.add(lCreditBean);
            TredsHelper.getInstance().getObligationsSplitsForPurchaser(lConnection, lInstrumentBean, lFUBean, lDebitBean, lObligations);
            //update the leg2 interest after obligation extension approval
            for (ObligationBean lNewBean : new ObligationBean[]{lUpFrontDebitBean, lTredsCreditBean, lUpFrontCreditBean}) {
                if (lNewBean == null) {
                	continue;
                }
                lNewBean.setOldObligationId(lNewBean.getId());
                lNewBean.setId(TredsHelper.getInstance().getObligationId(lConnection));
                lNewBean.setDate(lConfirmationWindowBean.getSettlementDate());
                lNewBean.setOriginalDate(lConfirmationWindowBean.getSettlementDate());
                if (AppConstants.DOMAIN_PLATFORM.equals(lNewBean.getTxnEntity())) {
                	lNewBean.setAmount(lObligationExtensionBean.getTredsCharge());
                	lNewBean.setSettlementCLId(null);
                	lNewBean.setSalesCategory(null);
                }else if(lFUBean.getPurchaser().equals(lNewBean.getTxnEntity())) {
                		lNewBean.setAmount(lObligationExtensionBean.getTredsCharge());
                		if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                			if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                    				&& CommonAppConstants.Yes.Yes.equals(lUpFrontDebitBean.getIsUpfront())) {
                				lNewBean.setAmount(lObligationExtensionBean.getNewInterest().add(lObligationExtensionBean.getTredsCharge()));
                    		}else if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
                    				&& !CommonAppConstants.Yes.Yes.equals(lUpFrontDebitBean.getIsUpfront())) {
                    			lNewBean.setAmount(lObligationExtensionBean.getNewInterest().add(lObligationExtensionBean.getTredsCharge()).add(lFUBean.getPurchaserLeg2Interest()));
                    		}else {
                    			lNewBean.setAmount(lObligationExtensionBean.getNewInterest().add(lObligationExtensionBean.getTredsCharge()));
                    		}
                		}
                }else if(lFUBean.getFinancier().equals(lNewBean.getTxnEntity())) {
                	if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
            				&& CommonAppConstants.Yes.Yes.equals(lUpFrontCreditBean.getIsUpfront())) {
        				lNewBean.setAmount(lObligationExtensionBean.getNewInterest());
            		}else if (lFUBean.getPurchaserLeg2Interest().compareTo(BigDecimal.ZERO)==1 
            				&& !CommonAppConstants.Yes.Yes.equals(lUpFrontCreditBean.getIsUpfront())) {
            			lNewBean.setAmount(lObligationExtensionBean.getNewInterest().add(lFUBean.getPurchaserLeg2Interest()));
            		}else {
            			lNewBean.setAmount(lObligationExtensionBean.getNewInterest());
            		}
                }
                long lExtension = OtherResourceCache.getInstance().getDiffInDays(lObligationExtensionBean.getNewDate(), lObligationExtensionBean.getOldDate());
                if (lNewBean.getExtendedDays()!=null) {
                	lNewBean.setExtendedDays(lNewBean.getExtendedDays() + lExtension);
                }else {
                	lNewBean.setExtendedDays(lExtension);
                }
                //TODO: no change in OriginalAmount
                lNewBean.setPfId(null);
                lNewBean.setFileSeqNo(null);
                lNewBean.setPayDetail1(null);
                lNewBean.setPayDetail2(null);
                lNewBean.setPayDetail3(null);
                lNewBean.setPayDetail4(null);
                lNewBean.setSettledAmount(null);
                lNewBean.setPaymentRefNo(null);
                lNewBean.setRespErrorCode(null);
                lNewBean.setRespRemarks(null);
                lNewBean.setSettledDate(null);
                lNewBean.setOriginalAmount(lNewBean.getAmount());
                lNewBean.setRecordCreator(pUserBean.getId());
                if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
                	lNewBean.setIsUpfront(Yes.Yes);
                }else {
                	lNewBean.setIsUpfront(null);
                }
                lNewBean.setIsUpfrontOblig(Yes.Yes);
                if (lNewBean.getAmount().compareTo(BigDecimal.ZERO) == 0 ) {
                	continue;
                }
                obligationDAO.insert(lConnection, lNewBean);
                if (AppConstants.DOMAIN_PLATFORM.equals(lNewBean.getTxnEntity())) {
                	lGstBean.setObId(lTredsCreditBean.getId());
                }
            }
            lUpfrontObligations.add(lUpFrontDebitBean);
            if (CommonAppConstants.Yes.Yes.equals(lObligationExtensionBean.getUpfrontCharge())) {
            	if (lUpFrontCreditBean.getAmount().compareTo(BigDecimal.ZERO) != 0 ) {
            		lUpfrontObligations.add(lUpFrontCreditBean);
                }
            }
            lUpfrontObligations.add(lTredsCreditBean);
            TredsHelper.getInstance().getObligationsSplitsForPurchaser(lConnection, lInstrumentBean, lFUBean, lUpFrontDebitBean, lUpfrontObligations);
            lFUBean.setPurchaserLeg2Interest(lFUBean.getPurchaserLeg2Interest()); //should be recomputed
            BigDecimal lExtensionInterest = BigDecimal.ZERO;
            if (lObligationExtensionBean.getInterest() != null)
            	lExtensionInterest = lExtensionInterest.add(lObligationExtensionBean.getInterest());
            if (lObligationExtensionBean.getPenalty() != null)
            	lExtensionInterest = lExtensionInterest.add(lObligationExtensionBean.getPenalty());
            lFUBean.setLeg2ExtensionInterest(lExtensionInterest);
            lGstBean  = lFUBean.getGstSummary(lObligationExtensionBean.getPurchaser(), ChargeType.Extension,(long) 1);
            if (lGstBean!=null) {
            	lGstBean.setObId(lUpFrontDebitBean.getId());
            }
            List<String> lFieldList = new ArrayList<String>();
            lFieldList.add("purchaserLeg2Interest");
            lFieldList.add("leg2ExtensionInterest");
            lFieldList.add("entityGstSummary");
            factoringUnitDAO.update(lConnection, lFUBean, lFieldList);
        }
        pExecutionContext.commit();
        Map<String, Object> lDataValues = null;
        lDataValues = new HashMap<>();
    	lDataValues.put("id", lFUBean.getId());
    	lDataValues.put("inId", lInstrumentBean.getId());
    	lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
    	if (ObligationExtensionBean.Status.BidApproval.equals(lObligationExtensionBean.getStatus())
    			|| ObligationExtensionBean.Status.Approved.equals(lObligationExtensionBean.getStatus())
    			|| ObligationExtensionBean.Status.BidReturned.equals(lObligationExtensionBean.getStatus())
    		) {
    		lDataValues.put("createDate",FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getSubmitDate()));
    	}
    	lDataValues.put("amount", lFUBean.getAmount());
    	lDataValues.put("purName", lFUBean.getPurName());
    	lDataValues.put("finName", lFUBean.getFinName());
    	lDataValues.put("oldDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligBean.getOriginalDate()));
    	lDataValues.put("newDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getNewDate()));
    	lDataValues.put("extendedRate",lObligationExtensionBean.getExtendedBidRate());
    	lDataValues.put("extDate", lObligBean.getBidAcceptDateTime());
    	lDataValues.put("fuId", lFUBean.getId());
    	lDataValues.put("upfrontAmount",lObligationExtensionBean.getNewInterest());
    	lDataValues.put("tredsCharges",lObligationExtensionBean.getTredsCharge());
    	lDataValues.put("chargeDate", lObligationExtensionBean.getChargeDate());
    	lDataValues.put("fuAmount", lFUBean.getFactoredAmount());
    	lDataValues.put("obId", lObligationExtensionBean.getObId());
    	if (lDebitBean!=null) {
    		lDataValues.put("newObId",lDebitBean.getId());
    	}
    	if (lUpFrontDebitBean!=null) {
    		lDataValues.put("upfrontObId", lUpFrontDebitBean.getId());
    	}
    	lDataValues.put("tenor", lObligationExtensionBean.getTenor());
    	if (ObligationExtensionBean.Status.Approved.equals(lObligationExtensionBean.getStatus())) {
    		lDataValues.put("date", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getApproveDate()));
    		lDataValues.put("oldDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lFUBean.getMaturityDate()));
    		CompanyLocationBean lClBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection,TredsHelper.getInstance().getAppEntityBean(lFUBean.getPurchaser()).getCdId());
    		String lAddress = "";
    		if(CommonUtilities.hasValue(lClBean.getLine1())) lAddress += lClBean.getLine1();
    		if(CommonUtilities.hasValue(lClBean.getLine2())) lAddress += ", " + lClBean.getLine2();
    		if(CommonUtilities.hasValue(lClBean.getLine3())) lAddress += ", " + lClBean.getLine3();
    		if(CommonUtilities.hasValue(lClBean.getCity())) lAddress += ", " + lClBean.getCity();
    		if(CommonUtilities.hasValue(lClBean.getDistrict())){
    			if(!lClBean.getDistrict().equalsIgnoreCase(lClBean.getCity())){
    				lAddress += ", " + lClBean.getDistrict();
    			}
    		}
    		if(CommonUtilities.hasValue(lClBean.getState())){
    			lAddress += ", " + TredsHelper.getInstance().getGSTStateDesc(lClBean.getState());
    		}
    		if(CommonUtilities.hasValue(lClBean.getZipCode())) lAddress += ", " + lClBean.getZipCode();
    		lDataValues.put("purAdd", lAddress);
    	}
    	String lNotificationType = null;
    	String lNotificationType1 = null;
    	String lTemplate = null;
    	String lTemplate1 = null;
    	// email
        List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		List<NotificationInfo> lNotificationInfos1 = new ArrayList<NotificationInfo>();
        if(ObligationExtensionBean.Status.ForApproval.equals(lObligationExtensionBean.getStatus())){
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONREQUEST_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getFinancier(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONREQUEST;
        }else if(ObligationExtensionBean.Status.BidApproval.equals(lObligationExtensionBean.getStatus())) {
        	lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONREQUESTFINACT_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getPurchaser(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONREQUESTFINACT;
        }else if(ObligationExtensionBean.Status.Rejected.equals(lObligationExtensionBean.getStatus())) {
        	lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONREQUESTFINREJ_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getPurchaser(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONREQUESTFINREJ;
        }else if(ObligationExtensionBean.Status.Approved.equals(lObligationExtensionBean.getStatus())) {
			lNotificationType1 = AppConstants.EMAIL_NOTIFY_TYPE_ExtensionBidApproval_1;
        	lNotificationInfos1.add(new NotificationInfo(lNotificationType1, EntityEmail.AdminEmail,lFUBean.getPurchaser(), EmailSenders.TO));
        	lTemplate1 = AppConstants.TEMPLATE__EXTENSIONDETAILS;
			lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_ExtensionBidApproval_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getPurchaser(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_ExtensionBidApproval;
        }else if(ObligationExtensionBean.Status.BidReturned.equals(lObligationExtensionBean.getStatus())) {
        	lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONBIDWITHDRAW_1;
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.TO));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getFinancier(), EmailSenders.CC));
            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.CC));
            lTemplate = AppConstants.TEMPLATE_EXTENSIONWITHDRAW;
        } 
    	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(lConnection, lNotificationInfos);
    	Map<String,List<String>> lEmailIds1 = null;
    	if (lNotificationType1!=null) {
    		lEmailIds1 = TredsHelper.getInstance().getEmails(lConnection, lNotificationInfos1);
    	}
    	pExecutionContext.dispose();
        if (!lEmailIds.isEmpty()) {
        	if(ObligationExtensionBean.Status.Approved.equals(lObligationExtensionBean.getStatus())){
        		List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
        		MessageBean lMessageBean = new MessageBean(AppConstants.TEMPLATE_LETTEROFEXTENSION, lDataValues);
				EmailSender lEmailSender = EmailSender.getInstance();
				Mustache lMustache = lEmailSender.getMustache(lMessageBean.getTemplate());
				if (lMustache == null)
					throw new Exception("Template not found : " + lMessageBean.getTemplate());
			    StringWriter lStringWriter = new StringWriter();
			    lMustache.execute(lStringWriter, new Object[]{lMessageBean.getDataValues(), null});
			 	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
			 	ITextRenderer renderer = new ITextRenderer();
			 	renderer.setDocumentFromString(lStringWriter.toString());
			    renderer.layout();
			    try {
					renderer.createPDF(lByteArrayOutputStream);
					lByteArrayOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			 	byte[] lPdf = lByteArrayOutputStream.toByteArray();
			 	MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			    String lFileType = "application/pdf";
				lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
				lMimeBodyPart.setFileName("LetterOfExtension.pdf");
				if(lMimeBodyPart!=null){
	        		lAttachList.add(lMimeBodyPart);
	        	}
				lDataValues.put(EmailSender.ATTACHMENTS,lAttachList);
        	}
        	if (lNotificationType1!=null) {
            	TredsHelper.getInstance().setEmailsToData(lEmailIds1, lDataValues);
                EmailSender.getInstance().addMessage(lTemplate1,new HashMap<String,Object>(lDataValues));
        	}
        	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
        	lDataValues.remove(EmailSender.ATTACHMENTS);
            EmailSender.getInstance().addMessage(lTemplate,new HashMap<String,Object>(lDataValues));
        }
    }

	public void placeBid(ExecutionContext pExecutionContext, ObligationExtensionBean pObligationExtensionBean, IAppUserBean pUserBean, boolean pSubmit) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		ObligationExtensionBean lObligationExtensionBean = obligationExtensionDAO.findByPrimaryKey(lConnection, pObligationExtensionBean);
		if (pObligationExtensionBean.getExtendedBidRate()!=null) {
            long lExtension = OtherResourceCache.getInstance().getDiffInDays(lObligationExtensionBean.getNewDate(), lObligationExtensionBean.getOldDate());
            BigDecimal lInterest = lObligationExtensionBean.getOldAmount().multiply(pObligationExtensionBean.getExtendedBidRate()).multiply(new BigDecimal(lExtension));
        	lInterest = lInterest.divide(AppConstants.DAYS_IN_YEAR, MathContext.DECIMAL128).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
        	lObligationExtensionBean.setInterestRate(pObligationExtensionBean.getExtendedBidRate());
        	lObligationExtensionBean.setInterest(lInterest);
            BigDecimal lRate = BigDecimal.ZERO;
			BigDecimal lPenalty = lObligationExtensionBean.getOldAmount().multiply(lRate).multiply(new BigDecimal(lExtension));
	        lPenalty = lPenalty.divide(AppConstants.DAYS_IN_YEAR, MathContext.DECIMAL128).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
	        lObligationExtensionBean.setPenalty(lPenalty);
	        lObligationExtensionBean.setPenaltyRate(lRate);
	        lObligationExtensionBean.setNewAmount(lObligationExtensionBean.getOldAmount().add(lObligationExtensionBean.getPenalty()).add(lObligationExtensionBean.getInterest()));
	        if ( !(ObligationExtensionBean.Status.ForApproval.equals(lObligationExtensionBean.getStatus())
	        		||ObligationExtensionBean.Status.BidReturned.equals(lObligationExtensionBean.getStatus()) )) {
	        	throw new CommonBusinessException("Obligation extension cannot be modified now.");
	        }
        	lObligationExtensionBean.setRecordUpdator(pUserBean.getId());
        	if(pSubmit) {
        		lObligationExtensionBean.setStatus(ObligationExtensionBean.Status.BidApproval);
        	}
            obligationExtensionDAO.update(lConnection, lObligationExtensionBean, ObligationExtensionBean.FIELDGROUP_PLACEBID);
            obligationExtensionDAO.insertAudit(lConnection, lObligationExtensionBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            if (pSubmit) {
            	FactoringUnitBean lFUBean = getFactoringUnitBean(lConnection, lObligationExtensionBean.getObId());
                InstrumentBean lInstrumentBean = new InstrumentBean();
                lInstrumentBean.setFuId(lFUBean.getId());
                lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
                ObligationBean lObligBean = new ObligationBean();
                lObligBean.setId(lObligationExtensionBean.getObId());
                lObligBean = obligationDAO.findByPrimaryKey(lConnection, lObligBean);
        		Map<String, Object> lDataValues = null;
                lDataValues = new HashMap<>();
            	lDataValues.put("id", lFUBean.getId());
            	lDataValues.put("inId", lInstrumentBean.getId());
            	lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
        		lDataValues.put("createDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT, new Date(System.currentTimeMillis())));
            	lDataValues.put("amount", lFUBean.getAmount());
            	lDataValues.put("purName", lFUBean.getPurName());
            	lDataValues.put("finName", lFUBean.getFinName());
            	lDataValues.put("oldDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligBean.getOriginalDate()));
            	lDataValues.put("newDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationExtensionBean.getNewDate()));
            	lDataValues.put("extendedRate",lObligationExtensionBean.getExtendedBidRate());
            	String lNotificationType = null;
            	String lTemplate = null;
            	// email
                List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_EXTENSIONREQUESTFINACT_1;
                lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getPurchaser(), EmailSenders.TO));
                lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lFUBean.getPurchaser(), EmailSenders.CC));
                lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lFUBean.getFinancier(), EmailSenders.CC));
                lTemplate = AppConstants.TEMPLATE_EXTENSIONREQUESTFINACT;
            	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(lConnection, lNotificationInfos); 
            	pExecutionContext.dispose();
                if (!lEmailIds.isEmpty()) {
                	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
                    EmailSender.getInstance().addMessage(lTemplate,lDataValues);
                }
        	}
        }
        
	}
	
	 public void autoExpireExtensionRequest(Connection pConnection, Date pBusinessDate, AppUserBean pUserBean) throws Exception{
		HashMap<String, Object> lGlobalSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONEXTENSION);
	    Boolean lIsExtensionAllowed = (Boolean)lGlobalSettings.get(AppConstants.ATTRIBUTE_ALLOWOBLIGATIONEXTENSION);
        Long lMaxDaysForExtension = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXDAYSFOREXTENSION);
        Long lMaxGracePeriod = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXGRACEPERIOD);
        Long lMaxDaysForExtensionAcceptance = (Long)lGlobalSettings.get(AppConstants.ATTRIBUTE_MAXDAYSFOREXTENSIONACCEPTANCE);
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		lSql.append(" SELECT * FROM OBLIGATIONEXTENSIONS ");
		lSql.append(" WHERE OESTATUS NOT IN ( ");
		lSql.append(lDbHelper.formatString(Status.Approved.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(Status.Expired.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(Status.Rejected.getCode()));
		lSql.append(" ) ");
		lSql.append(" AND To_Date(To_Char(OERECORDCREATETIME,'dd-mm-yyyy'),'dd-mm-yyyy') < ").append(DBHelper.getInstance().formatDate(OtherResourceCache.getInstance().addDaysToDate(pBusinessDate,  -(lMaxDaysForExtensionAcceptance.intValue()) )));
		List<ObligationExtensionBean> lObligationExtList = obligationExtensionDAO.findListFromSql(pConnection, lSql.toString(), -1);
		if (!lObligationExtList.isEmpty()) {
			for (ObligationExtensionBean lOEBean : lObligationExtList) {
				lOEBean.setStatus(Status.Expired);
				lOEBean.setRemarks("Expired");
				obligationExtensionDAO.update(pConnection, lOEBean);
		        obligationExtensionDAO.insertAudit(pConnection, lOEBean, GenericDAO.AuditAction.Update, (pUserBean==null?new Long(0):pUserBean.getId()));
			}
		}
	 }
}
