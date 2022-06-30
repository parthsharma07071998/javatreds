package com.xlx.treds.entity.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants.RegEntityType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.bill.bean.BillBean;
import com.xlx.treds.bill.bean.BillBean.BillingType;
import com.xlx.treds.bill.bo.BillBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ApprovalStatus;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ChargeType;
import com.xlx.treds.entity.bean.RegistrationChargeBean.RequestType;
import com.xlx.treds.master.bean.RegistrationChargeMasterBean;

public class RegistrationChargeBO {

	public static final Logger logger = LoggerFactory.getLogger(RegistrationChargeBO.class);
    
    private GenericDAO<RegistrationChargeBean> registrationChargeDAO;
    private GenericDAO<RegistrationChargeMasterBean> registrationChargeMasterDAO;
    private GenericDAO<AppEntityBean> appEntityDAO;
    private BillBO billBO;
    
    private static Long TAB_INBOX = Long.valueOf(0);
	private static Long TAB_CHECKERPENDING = Long.valueOf(1);
	private static Long TAB_APPROVED= Long.valueOf(2);
	
	public void setTab(RegistrationChargeBean pBean) {
		switch (pBean.getApprovalStatus()) {
		case Draft:
		case Returned:
			pBean.setTab(TAB_INBOX);
			return;
		case Pending:
			pBean.setTab(TAB_CHECKERPENDING);
			return;
		case Approved:
			pBean.setTab(TAB_APPROVED);
			return;
		}
	}

    public RegistrationChargeBO() {
        super();
        registrationChargeDAO = new GenericDAO<RegistrationChargeBean>(RegistrationChargeBean.class);
        registrationChargeMasterDAO = new GenericDAO<RegistrationChargeMasterBean>(RegistrationChargeMasterBean.class);
        appEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        billBO = new BillBO();
    }
    
    public RegistrationChargeBean findBean(ExecutionContext pExecutionContext, 
            RegistrationChargeBean pFilterBean) throws Exception {
            RegistrationChargeBean lRegistrationChargeBean = registrationChargeDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if (lRegistrationChargeBean == null) 
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            lRegistrationChargeBean.setRegistrationDate(new Date(TredsHelper.getInstance().getAppEntityBean(lRegistrationChargeBean.getEntityCode()).getRecordCreateTime().getTime()));
            return lRegistrationChargeBean;
        }

    private RegistrationChargeBean findBean(Connection pConnection, 
            RegistrationChargeBean pFilterBean) throws Exception {
            RegistrationChargeBean lRegistrationChargeBean = registrationChargeDAO.findByPrimaryKey(pConnection, pFilterBean);
            if (lRegistrationChargeBean == null) 
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
      
            return lRegistrationChargeBean;
        }
    
    public List<RegistrationChargeBean> findList(ExecutionContext pExecutionContext, RegistrationChargeBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    		return findList(pExecutionContext.getConnection(), pFilterBean, pColumnList, pUserBean);
    }
    private List<RegistrationChargeBean> findList(Connection pConnection, RegistrationChargeBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT * FROM REGISTRATIONCHARGES ");
			lSql.append(" WHERE 1 = 1 ");
			if(pFilterBean.getEffectiveStartDate() != null){
				lSql.append(" AND RCEFFECTIVEDATE >= ").append(DBHelper.getInstance().formatDate(pFilterBean.getEffectiveStartDate()));
			}
			if(pFilterBean.getEffectiveEndDate() != null){
				lSql.append(" AND RCEFFECTIVEDATE <= ").append(DBHelper.getInstance().formatDate(pFilterBean.getEffectiveEndDate()));
			}
			registrationChargeDAO.appendAsSqlFilter(lSql, pFilterBean, false);
			List<RegistrationChargeBean> lList = registrationChargeDAO.findListFromSql(pConnection, lSql.toString(), -1);
            for (RegistrationChargeBean lBean : lList ) {
            	setTab(lBean);
            }
            return lList;
        }
    
    public void save(ExecutionContext pExecutionContext, RegistrationChargeBean pRegistrationChargeBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        RegistrationChargeBean lOldRegistrationChargeBean = null;
        if (pNew) {

            pRegistrationChargeBean.setRecordCreator(pUserBean.getId());
            registrationChargeDAO.insert(lConnection, pRegistrationChargeBean);
        } else {
            lOldRegistrationChargeBean = findBean(pExecutionContext, pRegistrationChargeBean);

            pRegistrationChargeBean.setRecordUpdator(pUserBean.getId());
            if (registrationChargeDAO.update(lConnection, pRegistrationChargeBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, RegistrationChargeBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        RegistrationChargeBean lRegistrationChargeBean = findBean(pExecutionContext, pFilterBean);
        lRegistrationChargeBean.setRecordUpdator(pUserBean.getId());
        registrationChargeDAO.delete(lConnection, lRegistrationChargeBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public RegistrationChargeBean createRegistrationCharge(Connection pConnection, String pEntityCode, IAppUserBean pUserBean) throws Exception {
    	//System.out.println(getEffectiveDate(pEntityCode));    	
    	//return null;
		//TODO: effectiveDate( approval date )
    	Date lApprovalDate = getRegistrationDate(pConnection, pEntityCode);
		return createCharge(pConnection, pEntityCode, pUserBean, RegistrationChargeBean.ChargeType.Registration, lApprovalDate);
    }

    public RegistrationChargeBean createAnnualFeesCharge(Connection pConnection, String pEntityCode, IAppUserBean pUserBean) throws Exception {
		//TODO: effectiveDate(registrationExpiryDate + 1 day)
    	Date lEffectiveDate = getEffectiveDate(pEntityCode);
		return createCharge(pConnection, pEntityCode , pUserBean, RegistrationChargeBean.ChargeType.Annual, lEffectiveDate);
    }

    public RegistrationChargeBean createChargeFromExtension(Connection pConnection, String pEntityCode, Long pExtensionId, RequestType pRequestType, IAppUserBean pUserBean) throws Exception {
    	//first find the extension from which charge to create
    	RegistrationChargeBean lRCExtensionBean = null;
    	RegistrationChargeBean lRCFilterBean = new RegistrationChargeBean();
    	lRCFilterBean.setId(pExtensionId);
    	lRCExtensionBean = registrationChargeDAO.findBean(pConnection, lRCFilterBean);
    	//
    	if(lRCExtensionBean==null) {
    		throw new CommonBusinessException("No extension bean found to create charge.");
    	}
    	//capture the charge type to pass 
    	ChargeType lChargeType = lRCExtensionBean.getChargeType();
    	//TODO: check whether it is the final approved extension. (all extensions should be approved)
    	RegistrationChargeBean lRCFilter2Bean = new RegistrationChargeBean();
    	lRCFilter2Bean.setEntityCode(lRCFilterBean.getEntityCode());
    	lRCFilter2Bean.setEffectiveDate(lRCFilterBean.getEffectiveDate());
    	List<RegistrationChargeBean> lRCBeanList = registrationChargeDAO.findList(pConnection, lRCFilterBean);
    	if(lRCBeanList!=null && lRCBeanList.size() > 0) {
    		for(RegistrationChargeBean lRCBean : lRCBeanList) {
    			if(RegistrationChargeBean.RequestType.Extenstion.equals(lRCBean.getRequestType()) &&
    					!RegistrationChargeBean.ApprovalStatus.Approved.equals(lRCBean.getApprovalStatus())) {
    				throw new CommonBusinessException("Cannot create charge from extension when unapproved items exists.");
    			}else if(!RegistrationChargeBean.RequestType.Extenstion.equals(lRCBean.getRequestType()) &&
    					RegistrationChargeBean.ApprovalStatus.Approved.equals(lRCBean.getApprovalStatus())) {
    				throw new CommonBusinessException("Cannot create charge from extension when approved payment or waiver exists.");
    			}
    		}
    	}
    	Date lEffectiveDate = getEffectiveDate(pEntityCode);
		return createCharge(pConnection, pEntityCode , pUserBean, lChargeType, lEffectiveDate, pRequestType );
    }

    public Date getRegistrationDate(Connection pConnection, String pEntityCode){
    	Date lRegDate = null;
    	AppEntityBean lAppEntityBean = null; 
    	try {
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
			lRegDate = new Date(lAppEntityBean.getRecordCreateTime().getTime());
		} catch (Exception e) {
			logger.debug("Error in getRegistrationDate : "+e.getMessage());
		}
    	return lRegDate;
    }
    public Date getAnnualFeeDate(String pEntityCode, int pAnnualFeeYear){
    	Date lRegDate = null;
    	Date lAnnDate = null;
    	AppEntityBean lAppEntityBean = null; 
    	try {
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
			lRegDate = new Date(lAppEntityBean.getRecordCreateTime().getTime());
	    	try {
	    		java.util.Date lNewDate = CommonUtilities.addYears(new Date(lRegDate.getTime()), pAnnualFeeYear);
	    		lAnnDate = new Date(lNewDate.getTime());
			} catch (Exception e) {
				System.out.println("Error in getAnnualFeeDate : "+e.getMessage());
			}
		} catch (Exception e) {
			logger.debug("Error in getRegistrationDate : "+e.getMessage());
		}
    	return lAnnDate;
    }
    private Date getEffectiveDate(String pEntityCode){
		//TODO: effectiveDate(registrationExpiryDate + 1 day)
    	Date lEffectiveDate = null;
    	AppEntityBean lAppEntityBean = null; 
    	try {
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
    		Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
        	if(lAppEntityBean.getRegExpiryDate()!=null) {
        		//(registrationExpiryDate + 1 day)
        		lEffectiveDate = getEffectiveDate(lAppEntityBean.getRegExpiryDate(), lBusinessDate); //effective date as per the business date
        		//the above is done so that the if we have old expirydate then also current charge will be developed as per business date
        		//
            	//lEffectiveDate = lAppEntityBean.getRegExpiryDate();
        		//java.util.Date lNewDate = CommonUtilities.addDays(new Date(lEffectiveDate.getTime()), 1);
        		//lEffectiveDate = new Date(lNewDate.getTime());
        	}else {
        		Date lRegDate = new Date(lAppEntityBean.getRecordCreateTime().getTime());
        		lEffectiveDate = getEffectiveDate(lRegDate, lBusinessDate); //effective date as per the business date
        	}
		} catch (Exception e) {
			logger.debug("Error in getRegistrationDate : "+e.getMessage());
		}
    	return lEffectiveDate;
    }
    private  Date getEffectiveDate(Date pRegDate, Date pCurrentDate){
    	Date lEffectiveDate = null;
    	try {
    		long lDays = ((pCurrentDate.getTime() - pRegDate.getTime()) / 86400000); //getDiffInDays
    		int lYears = (int)lDays/365;
    		java.util.Date lNewDate = CommonUtilities.addYears(new Date(pRegDate.getTime()), lYears);
    		lEffectiveDate = new Date(lNewDate.getTime());
		} catch (Exception e) {
			System.out.println("Error in getRegistrationDate : "+e.getMessage());
		}
    	return lEffectiveDate;
    }

    public RegistrationChargeBean createCharge(Connection pConnection, String pEntityCode, IAppUserBean pUserBean, RegistrationChargeBean.ChargeType pChargeType, Date pEffectiveDate) throws Exception {
    	return createCharge(pConnection, pEntityCode, pUserBean, pChargeType, pEffectiveDate, null);
    }
    
    public RegistrationChargeBean createCharge(Connection pConnection, String pEntityCode, IAppUserBean pUserBean, RegistrationChargeBean.ChargeType pChargeType, Date pEffectiveDate, RequestType pRequestType) throws Exception {
    	//REGISTRATION
    	//first find whether the registration charage is already applied
    	//The following will be stored when creating charge
    	//REGISTRATION --> entityCode, entityType, chargeType, effectiveDate, chargeAmount, billedEntityLocationId, approvalStatus
    	//ANNUALFEES   --> entityCodE, entityType, chargeType, effectiveDate(registrationExpiryDate + 1 day), chargeAmount, billedEntityLocationId(registered location of entity), approvalStatus

    	//find the chargeamount from the masters
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
    	RegEntityType lRegEntityType = getEntityType(pEntityCode);
    	RegistrationChargeBean lRCBean = null;
    	RegistrationChargeBean lRCFilterBean = new RegistrationChargeBean();
    	RegistrationChargeMasterBean lRCMBean = getChargeMaster(pConnection, lRegEntityType);
    	//
    	lRCFilterBean.setChargeType(pChargeType);
    	lRCFilterBean.setEntityCode(pEntityCode);
    	lRCFilterBean.setEntityType(lRegEntityType);
    	//for annual charge creating 
		if(ChargeType.Annual.equals(pChargeType)) {
	    	lRCFilterBean.setEffectiveDate(pEffectiveDate);
		}
		//checking whether charge alreday exist
		if(ChargeType.Annual.equals(pChargeType)) {
			List<RegistrationChargeBean> lRCBeans = registrationChargeDAO.findList(pConnection, lRCFilterBean);
			if(lRCBeans!=null) {
				for(RegistrationChargeBean lTmpRCBean : lRCBeans) {
					//if(lTmpRCBean.getRequestType()==null || (RequestType.Payment.equals(lTmpRCBean.getRequestType()) || 
					//		RequestType.Waiver.equals(lTmpRCBean.getRequestType())) ) {
					if((RequestType.Payment.equals(lTmpRCBean.getRequestType()) || RequestType.Waiver.equals(lTmpRCBean.getRequestType()) ) ) {
	    				throw new CommonBusinessException("Charge already created for entity code " + pEntityCode);
					}
					//}
				}
			}
		}else if (ChargeType.Registration.equals(pChargeType)) {
			lRCBean = registrationChargeDAO.findBean(pConnection, lRCFilterBean);
			if(lRCBean!=null) {
				if(!RequestType.Extenstion.equals(lRCBean.getRequestType())) {
					throw new CommonBusinessException("Charge already created for entity code " + pEntityCode);
				}
			}
		}
		//create the new Charge bean and set the default values
		lRCBean = new RegistrationChargeBean();
		lRCBean.setEntityCode(pEntityCode);
		lRCBean.setEntityType(lRegEntityType);
		lRCBean.setChargeType(pChargeType);
		if(RegistrationChargeBean.ChargeType.Registration.equals(pChargeType)) {
			java.util.Date lDate = CommonUtilities.getDate(lAppEntityBean.getRecordCreateTime());
			lRCBean.setEffectiveDate(new Date(CommonUtilities.truncTime(lDate)));
			lRCBean.setChargeAmount(lRCMBean.getRegistrationCharge());
		}else if(RegistrationChargeBean.ChargeType.Annual.equals(pChargeType)) {
			lRCBean.setChargeAmount(lRCMBean.getAnnualCharge());
			lRCBean.setEffectiveDate(pEffectiveDate);
		}
		if(pRequestType!=null) {
			lRCBean.setRequestType(pRequestType);
		}
		lRCBean.setBilledEntityCode(pEntityCode);
		lRCBean.setBilledEntityClId(null);
		lRCBean.setApprovalStatus(ApprovalStatus.Draft);
		//
        lRCBean.setRecordCreator(pUserBean.getId());
        registrationChargeDAO.insert(pConnection, lRCBean);
        registrationChargeDAO.insertAudit(pConnection, lRCBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        return lRCBean;
    }
    
    private RegistrationChargeMasterBean getChargeMaster(Connection pConnection, RegEntityType pEntityType) throws Exception {
    	RegistrationChargeMasterBean lRCMBean = null;
    	RegistrationChargeMasterBean lRCMFilterBean = new RegistrationChargeMasterBean();
		//fetching the charge amount from master
		lRCMFilterBean.setEntityType(pEntityType);
		lRCMBean = registrationChargeMasterDAO.findBean(pConnection, lRCMFilterBean);
		if(lRCMBean == null) {
			throw new CommonBusinessException("No charges found for entity code type " + pEntityType.toString());
		}    	
		return lRCMBean;
    }
    
    private RegEntityType getEntityType(String pEntityCode) {
    	AppEntityBean lAppEntityBean = null;
    	if(StringUtils.isNotEmpty(pEntityCode)) {
        	try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
				if(lAppEntityBean!=null) {
		        	if(lAppEntityBean.isPurchaser()) {
		        		return RegEntityType.Purchaser;
		        	}else if(lAppEntityBean.isSupplier()) {
		        		return RegEntityType.Supplier;
		        	}else if(lAppEntityBean.isFinancier()) {
		        		return RegEntityType.Financier;
		        	}	
				}
			} 
        	catch (Exception e) 
        	{
        		logger.error("Error in getEntityType : " + e.getMessage());
			}
    	}
    	return null;
    }

    public void reExtend(Connection pConnection, Long pId, IAppUserBean pUserBean) throws Exception {
    	if(pId==null) {
    		throw new CommonBusinessException("Insufficient data to extend.");
    	}
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        //find the actual bean from which extension is to be sought
        lFilterBean.setId(pId);
        //
        RegistrationChargeBean lRCBean = findBean(pConnection, lFilterBean);
        //validate
        if(lRCBean == null) {
        	throw new CommonBusinessException("Extension data not found for re-extension.");
        }
        if(!RequestType.Extenstion.equals(lRCBean.getRequestType())) {
        	throw new CommonBusinessException("Invalid request type for reextension.");
        }
        if(!ApprovalStatus.Approved.equals(lRCBean.getApprovalStatus())) {
        	throw new CommonBusinessException("Invalid status for reextension.");
        }
        //
        //reset the filter and fetch request type for that entity of that effective date
        lFilterBean.setId(null);
        lFilterBean.setEntityCode(lRCBean.getEntityCode());
        lFilterBean.setEffectiveDate(lRCBean.getEffectiveDate());
        //
        List<RegistrationChargeBean> lList = findList(pConnection, lFilterBean, null, pUserBean);
        int lExtCount = 0;
        if(lList!=null&& lList.size() > 0) {
        	for(RegistrationChargeBean lBean :  lList) {
        		if(!RequestType.Extenstion.equals(lRCBean.getRequestType())) {
        			throw new CommonBusinessException("Other request type already exist for this periods extension.");
        		}
        		if(!pId.equals(lBean.getId()) && ( ApprovalStatus.Draft.equals(lBean.getApprovalStatus()) || ApprovalStatus.Pending.equals(lBean.getApprovalStatus()) )  ) {
        			throw new CommonBusinessException("Extension request type already exist for this periods extension in draft or for approval mode.");
        		}
        		if(RequestType.Extenstion.equals(lRCBean.getRequestType())) {
        			lExtCount++;
        		}
        	}
        	if(lExtCount == 2) {
        		throw new CommonBusinessException("Already 2 extension granted for the same entity.");
        	}
        }
        //
      	try {
      		//
      		lRCBean.setApprovalStatus(ApprovalStatus.Draft);
      		lRCBean.setPrevExtendedDate(lRCBean.getExtendedDate());
      		lRCBean.setId(null);
            lRCBean.setRecordCreator(pUserBean.getId());
            lRCBean.setRecordUpdator(null);
            lRCBean.setRecordUpdateTime(null);
            lRCBean.setCheckerAuId(null);
            lRCBean.setCheckerTimestamp(null);
            lRCBean.setRemarks(null);
            lRCBean.setSupportingDoc(null);
            //
            registrationChargeDAO.insert(pConnection, lRCBean);
	        registrationChargeDAO.insertAudit(pConnection, lRCBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
		} catch (Exception e) {
			logger.error("Error in reExtend ", e);
		}    	
    }

    public void saveMakerChanges(Connection pConnection, RegistrationChargeBean pModifiedRCBean, IAppUserBean pUserBean) throws Exception {
    	if(pModifiedRCBean!=null && pModifiedRCBean.getId()!=null) {
    		//get old Bean
    		RegistrationChargeBean lOldRCBean = null;
    		lOldRCBean = findBean(pConnection, pModifiedRCBean);
    		if(lOldRCBean!=null) {
    			//VALIDATIONS START END
    			ApprovalStatus lOldApprovalStatus = lOldRCBean.getApprovalStatus();
    			ApprovalStatus lNewApprovalStatus = pModifiedRCBean.getApprovalStatus();
    			RequestType lRequestType = pModifiedRCBean.getRequestType();
    			//
    			if(lRequestType==null) {
    				throw new CommonBusinessException("Invalid request type.");
    			}
    			//
    			if(ApprovalStatus.Approved.equals(lOldApprovalStatus) && 
    				ApprovalStatus.Draft.equals(lNewApprovalStatus) && //to add registration chrage type and extension
    				ChargeType.Annual.equals(pModifiedRCBean.getChargeType()) && 
    				RequestType.Extenstion.equals(pModifiedRCBean.getRequestType())) {
    				//allow to change from Approved to Draft only for Annual Fees - Extension
    			}else {
        			if(!(ApprovalStatus.Draft.equals(lOldApprovalStatus) ||
        					ApprovalStatus.Returned.equals(lOldApprovalStatus))) {
        				throw new CommonBusinessException("Cannot modify registration charge details, invalid status.");
        			}
    			}
    			//new status can in draft, returned or is submitted to checker
    			if(!(ApprovalStatus.Draft.equals(lNewApprovalStatus) ||
    					ApprovalStatus.Returned.equals(lNewApprovalStatus)||
    					ApprovalStatus.Pending.equals(lNewApprovalStatus) ) ) {
    				throw new CommonBusinessException("Invalid submission status.");
    			}
    			//
    			validateChanges(pConnection, pModifiedRCBean);
    			//    				
    			//unsetting of other fields
    			if(RequestType.Extenstion.equals(lRequestType)) {
    				//if waiver then payemnt details are null
    				pModifiedRCBean.setPaymentAmount(null);
    				pModifiedRCBean.setPaymentDate(null);
    				pModifiedRCBean.setPaymentRefrence(null);
    				//while submitting check whether the new extended date is above the previous extension sought
    				if(ApprovalStatus.Pending.equals(pModifiedRCBean.getApprovalStatus())) {
    					if(lOldRCBean.getPrevExtendedDate()!=null && !pModifiedRCBean.getExtendedDate().after(lOldRCBean.getPrevExtendedDate())) {
    						throw new CommonBusinessException("New extension date should be after the previous extension date");
    					}
    				}
    				//
    				//supporting document might be present for extension
    			}else if(RequestType.Payment.equals(lRequestType)) {
    				//
    				pModifiedRCBean.setExtendedDate(null);
    				pModifiedRCBean.setExtensionCount(null);    				
    			}else if(RequestType.Waiver.equals(lRequestType)) {
    				//if waiver then payemnt details are null
    				pModifiedRCBean.setPaymentAmount(null);
    				pModifiedRCBean.setPaymentDate(null);
    				pModifiedRCBean.setPaymentRefrence(null);
    				//
    				pModifiedRCBean.setExtendedDate(null);
    				pModifiedRCBean.setExtensionCount(null);
    				//
    				pModifiedRCBean.setBilledEntityCode(null);
    				pModifiedRCBean.setBilledEntityClId(null);
    			}
    			//
				if(RequestType.Payment.equals(lRequestType)) {
	    			if(StringUtils.isNotEmpty(pModifiedRCBean.getBilledEntityCode())) {
	    				
	    			}else {
	    				pModifiedRCBean.setBilledEntityClId(null);
	    			}
				}
				//save the actual bean
				pModifiedRCBean.setMakerAuId(pUserBean.getId());
				pModifiedRCBean.setMakerTimestamp(CommonUtilities.getCurrentDateTime());
				pModifiedRCBean.setRecordUpdator(pUserBean.getId());
		        if (registrationChargeDAO.update(pConnection, pModifiedRCBean, RegistrationChargeBean.FIELDGROUP_UPDATEMAKER) == 0)
		            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
				//
		        if(ApprovalStatus.Pending.equals(lNewApprovalStatus)) {
			        registrationChargeDAO.insertAudit(pConnection, pModifiedRCBean, GenericDAO.AuditAction.Update, pUserBean.getId());
    			}
    		}
    	}
    }

    public void checkerApprovesChanges(Connection pConnection, RegistrationChargeBean pModifiedRCBean,MemoryDBConnection pMemoryDBConnection, IAppUserBean pUserBean) throws Exception {
		//get old Bean
		RegistrationChargeBean lOldRCBean = null;
		lOldRCBean = findBean(pConnection, pModifiedRCBean);
		if(lOldRCBean!=null) {
			ApprovalStatus lOldApprovalStatus = lOldRCBean.getApprovalStatus();
			ApprovalStatus lNewApprovalStatus = pModifiedRCBean.getApprovalStatus();
			RequestType lRequestType = pModifiedRCBean.getRequestType();
			//
			if(lOldApprovalStatus == null) {
				throw new CommonBusinessException("Cannot modify registration charge details, Invalid old status.");
			}
			//
			if(!ApprovalStatus.Pending.equals(lOldApprovalStatus)) {
				throw new CommonBusinessException("Cannot modify registration charge details, Invalid status.");
			}
			//new status can in draft, returned or is submitted to checker
			if(!(ApprovalStatus.Pending.equals(lNewApprovalStatus) ||
					ApprovalStatus.Approved.equals(lNewApprovalStatus)||
					ApprovalStatus.Returned.equals(lNewApprovalStatus) ) ) {
				throw new CommonBusinessException("Invalid approval status.");
			}

	    	if(!(RegistrationChargeBean.ApprovalStatus.Approved.equals(lNewApprovalStatus) || 
	    			RegistrationChargeBean.ApprovalStatus.Returned.equals(lNewApprovalStatus) ) ) {
	    		throw new CommonBusinessException("Invalid approval status.");
	    	}
	    	validateChanges(pConnection, pModifiedRCBean);
	    	//
	    	//update status to approved
	    	//insert audit record in RegistrationCharges_A table
	    	//generate bill i.e. only insert records in bills table
	    	//if it is extension then update AppEntityMaster set extendedRegistrationExpiryDate=extendedDate
	    	//else update AppEntityMaster set extendedRegistrationExpiryDate=null, registrationExpiryDate=effectiveDate + 12 months - 1 day
	    	//
			//save the actual bean
	    	if(RegistrationChargeBean.ApprovalStatus.Approved.equals(lNewApprovalStatus) ) {
				if(RequestType.Extenstion.equals(lRequestType)) {
					Long lExtensionCount =  new Long(1);
					if(pModifiedRCBean.getExtensionCount()!=null) {
						lExtensionCount = new Long(pModifiedRCBean.getExtensionCount()+1);
					}
					pModifiedRCBean.setExtensionCount(lExtensionCount);
				}
	    	}
			pModifiedRCBean.setCheckerAuId(pUserBean.getId());
			pModifiedRCBean.setCheckerTimestamp(CommonUtilities.getCurrentDateTime());
			pModifiedRCBean.setRecordUpdator(pUserBean.getId());
			if (pModifiedRCBean.getRecordVersion() ==null ) {
				pModifiedRCBean.setRecordVersion(lOldRCBean.getRecordVersion());
			}
	        if (registrationChargeDAO.update(pConnection, pModifiedRCBean, RegistrationChargeBean.FIELDGROUP_UPDATECHECKER) == 0)
	            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
	        //insert into audit table
	        registrationChargeDAO.insertAudit(pConnection, pModifiedRCBean, GenericDAO.AuditAction.Update, pUserBean.getId());
	        //
	    	if(RegistrationChargeBean.ApprovalStatus.Approved.equals(lNewApprovalStatus)) {
	    		if(RequestType.Payment.equals(lRequestType)) {
	    			//generate bill
	    			Long lBillId = generateRegBill(pConnection, pModifiedRCBean, pUserBean);
	    			pModifiedRCBean.setBillId(lBillId);
	    			updateBillId(pConnection, pModifiedRCBean, pUserBean);
	    			//update entity
	    		}else if(RequestType.Extenstion.equals(lRequestType)) {
	    			//update entity
	    		}else if(RequestType.Waiver.equals(lRequestType)) {
	    			//update entity
	    		}
	    		updateEntityExpiryDate(pConnection, pModifiedRCBean, pMemoryDBConnection, pUserBean,false);
	    	}else if(RegistrationChargeBean.ApprovalStatus.Returned.equals(lNewApprovalStatus)) {
	    		//do nothing
	    	}
		}
    }
    
    private void updateBillId(Connection pConnection, RegistrationChargeBean pModifiedRCBean, IAppUserBean pUserBean) throws Exception {
      	try {
      		pModifiedRCBean.setRecordUpdator(pUserBean.getId());
            registrationChargeDAO.update(pConnection, pModifiedRCBean, RegistrationChargeBean.FIELDGROUP_UPDATEBILLID);
	        registrationChargeDAO.insertAudit(pConnection, pModifiedRCBean, GenericDAO.AuditAction.Update, pUserBean.getId());
		} catch (Exception e) {
			logger.error("Error in updating BillId ", e);
		}    	
    }
    
    private Long generateRegBill(Connection pConnection, RegistrationChargeBean pModifiedRCBean, IAppUserBean pUserBean) throws Exception {
    	//TODO: GENERATE BILL FOR THE BILLED ENTITY.
    	if(!(pModifiedRCBean.getPaymentAmount()!=null && pModifiedRCBean.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0)) {
    		throw new CommonBusinessException("Cannot generate bill.");    		
    	}
    	BillBean lBillBean = new BillBean();
    	if(ChargeType.Registration.equals(pModifiedRCBean.getChargeType())) {
        	lBillBean.setBillingType(BillingType.RegistrationFee);
    	}else if(ChargeType.Annual.equals(pModifiedRCBean.getChargeType())) {
        	lBillBean.setBillingType(BillingType.AnnualFee);
    	}
    	if(StringUtils.isNotEmpty(pModifiedRCBean.getBilledEntityCode())) {
        	lBillBean.setEntity(pModifiedRCBean.getBilledEntityCode());
    	}else {
        	lBillBean.setEntity(pModifiedRCBean.getEntityCode());
    	}
    	lBillBean.setChargeAmount(pModifiedRCBean.getPaymentAmount());
    	lBillBean.setBilledForentity(pModifiedRCBean.getEntityCode());
    	lBillBean.setRegBillEntityLocId(pModifiedRCBean.getBilledEntityClId());
    	//
    	lBillBean.setRecordCreator(pUserBean.getId());
    	//
    	billBO.generateRegBill(pConnection, lBillBean);
    	return lBillBean.getId();
    }
    
    public int[] generateBills(Connection pConnection, IAppUserBean pUserBean) {
      	int[] lCounts = new int[] { 0,  0 };
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM RegistrationCharges  WHERE RCRecordversion > 0 ");
    	lSql.append(" AND RCBILLID IS NULL " );
    	lSql.append(" AND RCREQUESTTYPE = ").append(DBHelper.getInstance().formatString(RequestType.Payment.getCode()));
    	lSql.append(" AND RCAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(ApprovalStatus.Approved.getCode()));
    	
    	List<RegistrationChargeBean> lRegChrgBeans;
		try {
			lRegChrgBeans = registrationChargeDAO.findListFromSql(pConnection, lSql.toString(), 0);
			//generate bill
	    	if(lRegChrgBeans!=null) {
	    		int lSuccessCount = 0;
	    		lCounts[1] = lRegChrgBeans.size();
	    		for(RegistrationChargeBean lRCBean : lRegChrgBeans) {
	    			try {
		    	   		Long lBillId = generateRegBill(pConnection, lRCBean, pUserBean);
		    	   		lRCBean.setBillId(lBillId);
		        		updateBillId(pConnection, lRCBean, pUserBean);
		        		lSuccessCount++;
	    			}catch(Exception lEx) {
	    				logger.error("Error in generateBills for id :"+lRCBean.getId().toString() +" : "+lEx.getMessage());
	    			}
	    		}
	    		lCounts[0]=lSuccessCount;
	     	}
		} catch (Exception e) {
			logger.error("Error in generateBills :" +e.getMessage());
		}
		return lCounts;
    }
    
    public void updateEntityExpiryDate(Connection pConnection, RegistrationChargeBean pModifiedRCBean, MemoryDBConnection pMemoryDBConnection, IAppUserBean pUserBean, boolean pUpdateLatestExpiryDate) throws Exception {
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pModifiedRCBean.getEntityCode());
    	if(lAppEntityBean!=null) {
			RequestType lRequestType = pModifiedRCBean.getRequestType();
    		if(RequestType.Payment.equals(lRequestType)) {
    			lAppEntityBean.setExtendedRegExpiryDate(null);
    			java.util.Date lNextDate = CommonUtilities.addYears(pModifiedRCBean.getEffectiveDate(), 1);
    			lAppEntityBean.setRegExpiryDate(TredsHelper.getInstance().convertDate(lNextDate));
    			if(pUpdateLatestExpiryDate) {
    				if(TredsHelper.getInstance().getBusinessDate().after(lNextDate)) {
    					Date lLatestEffectiveDate = getEffectiveDate(pModifiedRCBean.getEntityCode());
        				if(TredsHelper.getInstance().getBusinessDate().after(lLatestEffectiveDate)) {
        					lNextDate = CommonUtilities.addYears(lLatestEffectiveDate, 1);
        	    			lAppEntityBean.setRegExpiryDate(TredsHelper.getInstance().convertDate(lNextDate));
        				}
    				}
    			}
    		}else if(RequestType.Extenstion.equals(lRequestType)) {
    			lAppEntityBean.setExtendedRegExpiryDate(pModifiedRCBean.getExtendedDate());
    		}else if(RequestType.Waiver.equals(lRequestType)) {
    			lAppEntityBean.setExtendedRegExpiryDate(null);
    			java.util.Date lNextDate = CommonUtilities.addYears(pModifiedRCBean.getEffectiveDate(), 1);
    			lAppEntityBean.setRegExpiryDate(TredsHelper.getInstance().convertDate(lNextDate));
    		}    		
    		//UPDATE APPENTITIES
            if (appEntityDAO.update(pConnection, lAppEntityBean, AppEntityBean.FIELDGROUP_UPDATEREGEXPIRY) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            appEntityDAO.insertAudit(pConnection, lAppEntityBean, AuditAction.Insert, pUserBean.getId());
            pMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
            pMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
    	}else {
    		logger.debug("No entity found in updateEnityExpiryDate.");
    	}
    }

    private void validateChanges(Connection pConnection, RegistrationChargeBean pModifiedRCBean) throws Exception {
		Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
		RequestType lRequestType = pModifiedRCBean.getRequestType();
		//
		if(lBusinessDate==null) {
			throw new CommonBusinessException("Business date not found.");
		}
		if(RequestType.Extenstion.equals(lRequestType)) {
			if(pModifiedRCBean.getExtendedDate()==null) {
				throw new CommonBusinessException("Extension is sought, hence extension date is mandatory.");
			}
			if(pModifiedRCBean.getExtendedDate().before(lBusinessDate)) {
				throw new CommonBusinessException("Extended date should be greater than the business date.");
			}
			//TODO: max extension date validation to be put
			java.util.Date lMaxExtensionDate = CommonUtilities.addDays(lBusinessDate, 90);
			if(pModifiedRCBean.getExtendedDate().after(lMaxExtensionDate)) {
				throw new CommonBusinessException("Extended date cannot be greate than 3 months.");
			}
		}else if(RequestType.Payment.equals(lRequestType)) {
			if(pModifiedRCBean.getPaymentDate()==null) {
				throw new CommonBusinessException("Request type is Payment, hence payment date is mandatory.");
			}
			if(pModifiedRCBean.getPaymentDate().after(lBusinessDate)) {
				throw new CommonBusinessException("Payment date should be on or before the business date.");
			}
			if(pModifiedRCBean.getPaymentAmount()==null) {
				throw new CommonBusinessException("Request type is Payment, hence payment amount is mandatory.");
			}
			if(BigDecimal.ZERO.compareTo(pModifiedRCBean.getPaymentAmount()) >= 0) {
				throw new CommonBusinessException("Payment amount should be greater than zero.");
			}
		}else if(RequestType.Waiver.equals(lRequestType)) {
			if(StringUtils.isEmpty(pModifiedRCBean.getSupportingDoc())) {
				throw new CommonBusinessException("Supporting documents mandatory for wavier.");
			}
		}
		if(RequestType.Payment.equals(lRequestType)) {
			if(StringUtils.isNotEmpty(pModifiedRCBean.getBilledEntityCode())) {
				if(!pModifiedRCBean.getEntityCode().equals(pModifiedRCBean.getBilledEntityCode())) {
					if(pModifiedRCBean.getBilledEntityClId()==null) {
						throw new CommonBusinessException("Billing location mandatory is billing entity is other than self.");
					}
				}
			}else {
				//if billing entity is empty then it will be self and registered location
				//this will be put when approval is done
			}
		}
		//VALIDATIONS END
    }
    
    public List<AppEntityBean> getEntitiyCodesForRegistration(Connection pConnection, Date pDate){
    	List<AppEntityBean> lEntities = new ArrayList<AppEntityBean>();
    	StringBuilder lSql = new StringBuilder();
    	//select CDCODE, RCID From companydetails 
    	//LEFT OUTER JOIN registrationcharges ON ( CDCODE = RCENTITYCODE AND RCCHARGETYPE = 'R' AND RCRECORDVERSION > 0 )
    	//where cdrecordversion > 0 
    	//and CDRECORDCREATETIME > to_Date('01-01-2019','DD-MM-YYYY')
    	//AND RCID IS NULL
    	//
    	lSql.append(" SELECT CDCODE, RCID FROM companydetails ");
    	lSql.append(" LEFT OUTER JOIN registrationcharges ");
    	lSql.append(" ON ( CDCODE = RCENTITYCODE AND RCCHARGETYPE = ").append(DBHelper.getInstance().formatString(ChargeType.Registration.getCode()));
    	lSql.append(" AND RCRECORDVERSION > 0 ) ");
    	lSql.append(" WHERE cdrecordversion > 0 ");
    	lSql.append(" AND CDRECORDCREATETIME >= ").append(DBHelper.getInstance().formatDate(pDate));
    	lSql.append(" AND RCID IS NULL ");

		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		ResultSet lResultSet = null;
		String lTmpCode = null;
		try {
			lStatement = pConnection.createStatement();
		    lResultSet = lStatement.executeQuery(lSql.toString());
		    while (lResultSet.next()){
		    	 lTmpCode = lResultSet.getString("CDCODE");
		    	 lEntities.add(TredsHelper.getInstance().getAppEntityBean(lTmpCode));
		    }
	    }catch(Exception lEx) {
	    	logger.info("Error in getEntityCodeForRegistration : "+lEx.getMessage());
		} finally {
			if (lStatement != null) {
				try {
					lStatement.close();
				} catch (SQLException e) {
			    	logger.info("Error in getEntityCodeForRegistration : statment close "+e.getMessage());
				}
			}
		}
    	return lEntities;
    }
    
    public List<RegistrationChargeBean> getRegistrationData(Connection pConnection, String pEntityCode, IAppUserBean pUserBean) {
    	List<RegistrationChargeBean> lRegChrgBeans = null;
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM RegistrationCharges  WHERE RCRecordversion > 0 ");
    	lSql.append(" AND RCENTITYCODE = ").append(DBHelper.getInstance().formatString(pEntityCode));
    	lSql.append(" AND RCAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(ApprovalStatus.Approved.getCode()));
    	lSql.append(" ORDER BY RCEFFECTIVEDATE DESC ");
    	
		try {
			lRegChrgBeans = registrationChargeDAO.findListFromSql(pConnection, lSql.toString(), 0);
		} catch (Exception e) {
			logger.error("Error in generateBills :" +e.getMessage());
		}
		return lRegChrgBeans;
    }

    
}
