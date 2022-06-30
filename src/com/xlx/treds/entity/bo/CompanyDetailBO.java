package com.xlx.treds.entity.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.user.bean.IAppUserBean.Status;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AppEntityStatus;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyContactBean.Nationality;
import com.xlx.treds.entity.bean.CompanyContactBean.ResidentailStatus;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyKYCDocumentBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.entity.bean.CompanyWorkFlowBean;
import com.xlx.treds.entity.bean.CompanyWrapperBean;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.other.bean.BuyerCreditRatingBean;
import com.xlx.treds.other.bean.RegistrationFilesBean;
import com.xlx.treds.other.bean.UploadFileBean;
import com.xlx.treds.other.bo.BuyerCreditRatingBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class CompanyDetailBO {
	private static final Logger logger = LoggerFactory.getLogger(CompanyDetailBO.class);
    public static final String TABLENAME_PROV = "CompanyDetails_P";
    
    private GenericDAO<CompanyDetailBean> companyDetailProvDAO;
    private GenericDAO<CompanyDetailBean> companyDetailDAO;
    //main
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<CompanyContactBean> companyContactDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    //provisional-used only in validateAndSubmit
    private GenericDAO<CompanyLocationBean> companyLocationProvDAO;
    private GenericDAO<CompanyContactBean> companyContactProvDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailProvDAO;
    //
    private GenericDAO<CompanyWorkFlowBean> companyWorkFlowDAO;
    private GenericDAO<BuyerCreditRatingBean> buyerCreditRatingDAO;
    private GenericDAO<RegistrationChargeBean> registrationChargeDAO;
    private GenericDAO<UploadFileBean> uploadFileDAO;
    public static String FIELDGROUP_UPDATECOMPANYNAME = "updateCompanyName";
    public static String FIELDGROUP_STARTMODIFICATION = "startModification";
    public static String FIELDGROUP_UPDATEAPPENTITYNAME = "updateAppEntityName";
    private GenericDAO<AppEntityBean> appEntityDAO;
    private GenericDAO<AppUserBean> appUserDAO;
    private GenericDAO<MemberwisePlanBean> memberwisePlanDAO;
    
    private BeanMeta companyDetailBeanMeta;
    private BeanMeta companyWorkFlowBeanMeta;
    
    
    private static Long TAB_INBOX = Long.valueOf(0);
	private static Long TAB_CHECKERPENDING = Long.valueOf(1);
	private static Long TAB_APPROVED= Long.valueOf(2);
	private static Long TAB_REJECTED= Long.valueOf(3);
	private static Long TAB_INMODIFICATION= Long.valueOf(4);
	private static Long TAB_FORMODIFICATIONAPPROVAL= Long.valueOf(5);
	private static Long TAB_DRAFTJOCATA= Long.valueOf(6);
	private static Long TAB_APPROVEDJOCATA= Long.valueOf(7);
	
	
	public void setTab(CompanyDetailBean pBean) {
		if(pBean.getApprovalStatus()==null) {
			pBean.setApprovalStatus(CompanyApprovalStatus.Draft);
		}
			switch (pBean.getApprovalStatus()) {
			case Draft:
			case Returned:
				pBean.setTab(TAB_INBOX);
				pBean.setIsProvisional(Boolean.TRUE);
				if(StringUtils.isNotEmpty(pBean.getCreatorIdentity())) {
					pBean.setTab(TAB_DRAFTJOCATA);
				}
				return;
			case ReSubmitted:
			case Submitted:
				pBean.setTab(TAB_CHECKERPENDING);
				pBean.setIsProvisional(Boolean.TRUE);
				return;
			case Approved:
			case ApprovalModificationApproved:
				pBean.setTab(TAB_APPROVED);
				if(StringUtils.isNotEmpty(pBean.getCreatorIdentity())) {
					pBean.setTab(TAB_APPROVEDJOCATA);
				}
				return;
			case Rejected:
				pBean.setTab(TAB_REJECTED);
				return;
			case ApprovalModification:
			case ApprovalModificationReturned:
				pBean.setTab(TAB_INMODIFICATION);
				pBean.setIsProvisional(Boolean.TRUE);
				return;
			case ApprovalModificationSubmit:
				pBean.setTab(TAB_FORMODIFICATIONAPPROVAL);
				pBean.setIsProvisional(Boolean.TRUE);
				return;
			}
		
	}

    
    public static enum ScoreIndex implements IKeyValEnumInterface<Integer> {
    	EntityDetails(0), ManagementDetails(1), LocationsBranches(2), BankingDetails(3), EnclosuresUploads(4);
        private final Integer index;
        private ScoreIndex(int pIndex) {
            index = pIndex;
        }
        public Integer getCode() {
            return index;
        }
    };

    public CompanyDetailBO() {
        super();
        companyDetailProvDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class, TABLENAME_PROV);
        companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        companyLocationProvDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class, CompanyLocationBO.TABLENAME_PROV);
        companyContactDAO= new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        companyContactProvDAO= new GenericDAO<CompanyContactBean>(CompanyContactBean.class,CompanyContactBO.TABLENAME_PROV);
        companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        companyBankDetailProvDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class, CompanyBankDetailBO.TABLENAME_PROV);
        companyWorkFlowDAO = new GenericDAO<CompanyWorkFlowBean>(CompanyWorkFlowBean.class);
        appEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
        memberwisePlanDAO = new GenericDAO<MemberwisePlanBean>(MemberwisePlanBean.class);
        uploadFileDAO = new GenericDAO<UploadFileBean>(UploadFileBean.class);
        
        companyDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyDetailBean.class);
        companyWorkFlowBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyWorkFlowBean.class);
        buyerCreditRatingDAO = new GenericDAO<BuyerCreditRatingBean>(BuyerCreditRatingBean.class);
        
        registrationChargeDAO = new GenericDAO<RegistrationChargeBean>(RegistrationChargeBean.class);
    }
    
    private GenericDAO<CompanyDetailBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyDetailProvDAO;
    	}
    	return companyDetailDAO;
    }
    public CompanyDetailBean findBean(ExecutionContext pExecutionContext, 
        CompanyDetailBean pFilterBean, IAppUserBean pAppUserBean, boolean pForEdit) throws Exception {
        if (pFilterBean == null) {
            pFilterBean = new CompanyDetailBean();
            pFilterBean.setId(pAppUserBean.getId());
        }
        if(AppConstants.DOMAIN_REGUSER.equals(pAppUserBean.getDomain())){
            pFilterBean.setIsProvisional(true);//since this is logginin through 
        }
        CompanyDetailBean lCompanyDetailBean = null;
        GenericDAO<CompanyDetailBean> lCompanyDetailDAO = getDAO(pFilterBean.getIsProvisional());
    	if(AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) ||
    			AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain())){
    		if(pFilterBean.getId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
            lCompanyDetailBean = lCompanyDetailDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            //if(lCompanyDetailBean==null){
            //    lCompanyDetailBean = companyDetailProvDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
           // }
    	}else{
    		if(pFilterBean.getId()==null){
        		pFilterBean.setId(TredsHelper.getInstance().getCompanyId(pAppUserBean));
    		}else{
    			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
    			if(lAppEntityBean!=null && lAppEntityBean.isFinancier()){
    				//keep the company id received from filter as is 
    			}else{
            		pFilterBean.setId(TredsHelper.getInstance().getCompanyId(pAppUserBean));
    			}
    		}
            lCompanyDetailBean = lCompanyDetailDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
    	}
        //pFilterBean.setRecordVersion(null);
        /*if (AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) && 
        		lCompanyDetailBean == null)
        	throw new CommonBusinessException("Platform Admin can only edit existing registration forms.");*/
        if (lCompanyDetailBean == null) {
        	//TODO: WHY THIS IS DONE - WE ARE CREATING THE SAME IN SAVE
        	/*if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()))
            	throw new CommonBusinessException("Platform Admin can only edit existing registration forms.");*/
            lCompanyDetailBean = new CompanyDetailBean();
            lCompanyDetailBean.setId(pAppUserBean.getId());
            lCompanyDetailBean.setRecordCreator(pAppUserBean.getId());
            lCompanyDetailBean.setApprovalStatus(CompanyApprovalStatus.Draft);
            pExecutionContext.setAutoCommit(true);
            lCompanyDetailDAO.insert(pExecutionContext.getConnection(), lCompanyDetailBean);
        } else {
            if (pForEdit) {
            	AppUserBean lAppUserBean = (AppUserBean) pAppUserBean;
            	if( !(Yes.Yes.equals(lAppUserBean.getEnableAPI()))){
            		boolean lModify = TredsHelper.getInstance().canModifyRegistration(pAppUserBean.getDomain(), lCompanyDetailBean.getApprovalStatus());
                	if(!lModify){
                        throw new CommonBusinessException("Registration details cannot be changed.");
                	}
            	}
            }
            lCompanyDetailBean.setIsProvisional(pFilterBean.getIsProvisional());
        }
        return lCompanyDetailBean;
    }
    
    public List<CompanyDetailBean> findList(ExecutionContext pExecutionContext, CompanyDetailBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	if(pUserBean!=null && AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
    		if(pFilterBean==null) pFilterBean = new CompanyDetailBean();
    		pFilterBean.setRecordCreator(pUserBean.getId());
    	}
        List<CompanyDetailBean> lCDList = getDAO(pFilterBean.getIsProvisional()).findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
        for (CompanyDetailBean lBean : lCDList ) {
        	setTab(lBean);
        }
        return lCDList;
    }
    
    public void save(ExecutionContext pExecutionContext, CompanyDetailBean pCompanyDetailBean, IAppUserBean pUserBean, IAppUserBean pLoggedInUserBean, 
        boolean pNew) throws Exception {
    	if(AppConstants.DOMAIN_PLATFORM.equals(pLoggedInUserBean.getDomain())){
    		/*if(pNew)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);*/
    	}else if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
    		//check if i am the creator - this we will check only for edit company details after fetching the old bean
    	}else{
        	if (!pUserBean.getId().equals(pCompanyDetailBean.getId()))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        CompanyDetailBean lOldCompanyDetailBean = null;
        
    	//cash discount validation only for Purchaser
    	if(CommonAppConstants.YesNo.Yes.equals(pCompanyDetailBean.getPurchaserFlag()) && 
    			pCompanyDetailBean.getCashDiscountPercent() != null ){
    		Double lMaxCashDiscountPercent = RegistryHelper.getInstance().getDouble(AppConstants.REGISTRY_MAXCASHDISCOUNTPERCENT);
    		if(lMaxCashDiscountPercent!=null){
    			BigDecimal lMaxCDPerc = BigDecimal.valueOf(lMaxCashDiscountPercent);
    			if(lMaxCDPerc.compareTo(pCompanyDetailBean.getCashDiscountPercent()) < 0){
    				throw new CommonBusinessException("Cash Discount for Buyer cannot exceed the Platform level Max Cash Discount of " + lMaxCDPerc );
    			}
    		}else {
    			throw new CommonBusinessException("Max Cash Discount Percentage not defined at Platform Level.");
    		}
    	}
    	//saving only to provisional tables
    	GenericDAO<CompanyDetailBean> lCompanyDetailDAO = getDAO(true);
    	pCompanyDetailBean.setIsProvisional(true);
    	//
        if (pNew) {
            pCompanyDetailBean.setCode(TredsHelper.getInstance().createCompanyOrEntityCode(lConnection, pCompanyDetailBean.getCompanyName()));
            pCompanyDetailBean.setRecordCreator(pLoggedInUserBean.getId());
            lCompanyDetailDAO.insert(lConnection, pCompanyDetailBean);
        } else {
        	logger.info("Going to update pCompanyDetailBean : "+pCompanyDetailBean.getId());
            lOldCompanyDetailBean = findBean(pExecutionContext, pCompanyDetailBean, pUserBean, true);
            //lOldCompanyDetailBean = lCompanyDetailDAO.findByPrimaryKey(lConnection, pCompanyDetailBean);
            //check if the user saving is regentity - if so then check whether it belong to him
            if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
            	if (!pUserBean.getId().equals(lOldCompanyDetailBean.getRecordCreator()))
                    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            }
            pCompanyDetailBean.setRecordUpdator(pLoggedInUserBean.getId());
        	if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        		if(lOldCompanyDetailBean != null && 
        				CompanyApprovalStatus.Approved.equals(lOldCompanyDetailBean.getApprovalStatus())){
        			//allowing only specific changes to the already approved 
                    if (lCompanyDetailDAO.update(lConnection, pCompanyDetailBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                        throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);    
                    lCompanyDetailDAO.getBeanMeta().copyBean(pCompanyDetailBean, lOldCompanyDetailBean,BeanMeta.FIELDGROUP_UPDATE,null);
                    //lCompanyDetailDAO.insertAudit(lConnection, lOldCompanyDetailBean, AuditAction.Update, pUserBean.getId());
        		}else{
//                    if (pCompanyDetailBean.getApprovalStatus() == CompanyApprovalStatus.Approved &&
//                    		lCompanyDetailDAO.update(lConnection, pCompanyDetailBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
//                        throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND); 
//                    else 
                    if (lCompanyDetailDAO.update(lConnection, pCompanyDetailBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                    	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
                    if(pCompanyDetailBean.getApprovalStatus() == CompanyApprovalStatus.Approved){
                    	lCompanyDetailDAO.getBeanMeta().copyBean(pCompanyDetailBean, lOldCompanyDetailBean,BeanMeta.FIELDGROUP_UPDATE,null);
                    	//lCompanyDetailDAO.insertAudit(lConnection, lOldCompanyDetailBean, AuditAction.Update, pUserBean.getId());
                    }
                    if(lOldCompanyDetailBean!=null) {
                    	if(CompanyApprovalStatus.Draft.equals(lOldCompanyDetailBean.getApprovalStatus()) ||
                    			CompanyApprovalStatus.ReSubmitted.equals(lOldCompanyDetailBean.getApprovalStatus()) ) {
                    		if(CompanyApprovalStatus.Approved.equals(pCompanyDetailBean.getApprovalStatus())) {
                                //TODO: create registration charge
                    			try {
                    				RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
                    				lRegistrationChargeBO.createCharge(lConnection, pCompanyDetailBean.getCode(), pUserBean, RegistrationChargeBean.ChargeType.Registration, new java.sql.Date(pCompanyDetailBean.getRecordCreateTime().getTime()));
                    			}catch(Exception lEx) {
                    				logger.error("Erro while creating charge : skipped : ",lEx);
                    			}
                    		}
                    	}
                    }
        		}
        	}else{
                if (lCompanyDetailDAO.update(lConnection, pCompanyDetailBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                    throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);        		
        	}
        	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pCompanyDetailBean.getCode());
        	if(lAppEntityBean!= null){
        		List<String> lFields = new ArrayList<String>();
        		if((pCompanyDetailBean.getSalesCategory()==null && lAppEntityBean.getSalesCategory()!=null) ||
            			(pCompanyDetailBean.getSalesCategory() != null && !pCompanyDetailBean.getSalesCategory().equals(lAppEntityBean.getSalesCategory()))){
    	        		lAppEntityBean.setSalesCategory(pCompanyDetailBean.getSalesCategory());
    	        		lFields.add("salesCategory");
        		}
        		if((pCompanyDetailBean.getMsmeStatus()==null && lAppEntityBean.getMsmeStatus()!=null) ||
            			(pCompanyDetailBean.getMsmeStatus() != null && !pCompanyDetailBean.getMsmeStatus().equals(lAppEntityBean.getMsmeStatus()))){
    	        		lAppEntityBean.setMsmeStatus(pCompanyDetailBean.getMsmeStatus());
    	        		lFields.add("msmeStatus");
        		}
        		if((pCompanyDetailBean.getPan()==null && lAppEntityBean.getPan()!=null) ||
            			(pCompanyDetailBean.getPan() != null && !pCompanyDetailBean.getPan().equals(lAppEntityBean.getPan()))){
    	        		lAppEntityBean.setPan(pCompanyDetailBean.getPan());
    	        		lFields.add("pan");
        		}
        		//promoter category
        		if(lFields.size() > 0){
            		appEntityDAO.update(lConnection, lAppEntityBean, lFields );
                    lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
                    lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
        		}
        	}
        }
        pExecutionContext.commitAndDispose();
        if (pNew && (AppConstants.DOMAIN_PLATFORM.equals(pLoggedInUserBean.getDomain()) || 
        		AppConstants.DOMAIN_REGENTITY.equals(pLoggedInUserBean.getDomain()))){
			//user getting created through API then do not send mail
        	if(!CommonAppConstants.Yes.Yes.equals(pLoggedInUserBean.getPreference(IAppUserBean.PreferenceKey.APIUser))) {
            	Map<String,Object>lDataValues = new HashMap<String, Object>();
            	Long lExpiryDays = TredsHelper.getInstance().getExpiryDays();
            	lDataValues.put("loginId", pUserBean.getLoginId());
                String lPassword = TredsHelper.getInstance().getRegistrationPassword();
                if (StringUtils.isBlank(lPassword)) lPassword = "Treds@123";
            	lDataValues.put("password", lPassword);
            	lDataValues.put(EmailSender.TO, pUserBean.getEmail());
            	lDataValues.put(EmailSender.CC, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
            	if(lExpiryDays!=null){
            		lDataValues.put("regPwdValidDays", TredsHelper.getInstance().getExpiryDays()); 
            	}

            	List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
            	MimeBodyPart lMimeBodyPart = null;
            	lMimeBodyPart = getFileAsAttachment(pExecutionContext.getConnection(),pCompanyDetailBean.getConstitution(),pCompanyDetailBean.getType());
            	if(lMimeBodyPart!=null){
            		lAttachList.add(lMimeBodyPart);
                	lDataValues.put(EmailSender.ATTACHMENTS, lAttachList);
            	}
            	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONLOGINDETAILSENTITY, lDataValues);
        	}
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyDetailBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        if (!pUserBean.getId().equals(pFilterBean.getId()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        pFilterBean.setIsProvisional(true);
        CompanyDetailBean lCompanyDetailBean = findBean(pExecutionContext, pFilterBean, pUserBean, true);
        lCompanyDetailBean.setRecordUpdator(pUserBean.getId());
        getDAO(true).delete(lConnection, lCompanyDetailBean);        

        pExecutionContext.commitAndDispose();
    }
    
    public Map<String, Object> validateAndSubmit(ExecutionContext pExecutionContext, Long pCompanyId, IAppUserBean pAppUserBean, boolean pSubmit) throws Exception {
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        if(AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain()) ){
        	if(pCompanyId==null)
        		throw new CommonBusinessException("Entity required.");
        	lFilterBean.setId(pCompanyId);
        }else {	
            lFilterBean.setId(pAppUserBean.getId());
        }
        //this is done so tht below function will fetch from provisional table
        lFilterBean.setIsProvisional(true);
		CompanyDetailBean lCompanyDetailBean = findBean(pExecutionContext, lFilterBean, pAppUserBean, pSubmit);
        Map<String, Object> lData = new HashMap<String, Object>();
        Boolean lScores[] = new Boolean[] {false,false,false,false,false};
        lData.put("id", lCompanyDetailBean.getId());
        lData.put("status", lCompanyDetailBean.getApprovalStatus().getCode());
        lData.put("statusDesc", lCompanyDetailBean.getApprovalStatus().toString());

        boolean lValid = true;

        List<Map<String, Object>> lMessageList = new ArrayList<Map<String,Object>>();
        int lMaxScore = 0 , lScore = 0;
        // check if form is complete
        // companydetails
        List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(lCompanyDetailBean, companyDetailBeanMeta.formatAsJson(lCompanyDetailBean), null, null);
        // conditional checks
        
        boolean lSupp = lCompanyDetailBean.getSupplierFlag() == CommonAppConstants.YesNo.Yes;
        boolean lPurc = lCompanyDetailBean.getPurchaserFlag() == CommonAppConstants.YesNo.Yes;
        boolean lFin = lCompanyDetailBean.getFinancierFlag() == CommonAppConstants.YesNo.Yes;
        boolean[] lFlags = new boolean[] {lSupp, lPurc, lFin};
        boolean lSectorManuf = AppConstants.RC_SECTOR_MANUFACTURING.equals(lCompanyDetailBean.getSector());
        boolean lSectorServ = AppConstants.RC_SECTOR_SERVICE.equals(lCompanyDetailBean.getSector());
        boolean lSTExempted = CommonAppConstants.Yes.Yes.equals(lCompanyDetailBean.getStExempted());
        String[] lEntityFieldGrp = new String[] {CompanyDetailBean.FIELDGROUP_SUPPLIER,CompanyDetailBean.FIELDGROUP_PURCHASER,CompanyDetailBean.FIELDGROUP_FINANCIER};
        //
        if(lSectorManuf && lSupp) {
        	lEntityFieldGrp[0] = CompanyDetailBean.FIELDGROUP_SUPPLIER_MANUFACTURER;
        }
        if(lSectorManuf && lPurc)
        	lEntityFieldGrp[1] = CompanyDetailBean.FIELDGROUP_PURCHASER_MANUFACTURER;
        if(!lSTExempted && lSectorServ && lSupp) {
        	lEntityFieldGrp[0] = CompanyDetailBean.FIELDGROUP_SUPPLIER_SERVICE;
        }
        if(!lSTExempted && lSectorServ && lPurc)
        	lEntityFieldGrp[1] = CompanyDetailBean.FIELDGROUP_PURCHASER_SERVICE;
        //
        List<BeanFieldMeta>[] lFieldGroups = new List[] {
                companyDetailBeanMeta.getJsonFields(lEntityFieldGrp[0]),
                companyDetailBeanMeta.getJsonFields(lEntityFieldGrp[1]),
                companyDetailBeanMeta.getJsonFields(lEntityFieldGrp[2])
        };
		//TODO: ?????
        if (lCompanyDetailBean.getCreatorIdentity()=="J") {
        	
        }
        for (int lPtr=0;lPtr<lFlags.length;lPtr++) {
            if (lFlags[lPtr]) {
                for (BeanFieldMeta lBeanFieldMeta : lFieldGroups[lPtr]) {
                    Object lValue = lBeanFieldMeta.getProperty(lCompanyDetailBean);
                    if (lValue == null) 
                        lValidationFailBeans.add(new ValidationFailBean(lBeanFieldMeta.getLabel(), BeanFieldMeta.MESSAGE_NOTNULL));
                    else if (lBeanFieldMeta.getDataType() == BeanFieldMeta.DataType.STRING) {
                        String lString = (String)lValue;
                        if (StringUtils.isBlank(lString))
                            lValidationFailBeans.add(new ValidationFailBean(lBeanFieldMeta.getLabel(), BeanFieldMeta.MESSAGE_NOTBLANK));
                    }
                }
            }
        }

        {
            Map<String, Object> lMessageMap = new HashMap<String, Object>();
            List<String> lMessages = new ArrayList<String>();
            List<String> lCustomerList = new ArrayList<String>();
            HashSet<String> lCustomers = new HashSet<String>();
            
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                lValid = false;
                for (ValidationFailBean lValidationFailBean : lValidationFailBeans) {
                    lMessages.add(lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage());
                }
            } 
            //Customers
            if(CommonUtilities.hasValue(lCompanyDetailBean.getCustomer1())) lCustomerList.add(lCompanyDetailBean.getCustomer1().trim());
            if(CommonUtilities.hasValue(lCompanyDetailBean.getCustomer2())) lCustomerList.add(lCompanyDetailBean.getCustomer2().trim());
            if(CommonUtilities.hasValue(lCompanyDetailBean.getCustomer3())) lCustomerList.add(lCompanyDetailBean.getCustomer3().trim());
            if(CommonUtilities.hasValue(lCompanyDetailBean.getCustomer4())) lCustomerList.add(lCompanyDetailBean.getCustomer4().trim());
            if(CommonUtilities.hasValue(lCompanyDetailBean.getCustomer5())) lCustomerList.add(lCompanyDetailBean.getCustomer5().trim());
            for(int lPtr=0; lPtr<lCustomerList.size(); lPtr++)
            {
            	if(!lCustomers.contains(lCustomerList.get(lPtr)))
            		lCustomers.add(lCustomerList.get(lPtr));
            	else
            	{
            		lMessages.add("Customer name duplicated.");
            		break;//msg will be displayed only once.
            	}
            }
            if(CommonUtilities.hasValue(lCompanyDetailBean.getPan()) && CommonUtilities.hasValue(lCompanyDetailBean.getStRegNo()))
            {
            	if(!lCompanyDetailBean.getStRegNo().startsWith(lCompanyDetailBean.getPan()))
            		lMessages.add("Service Tax number should start with the PAN number of the entity.");
            }
            
			if(lMessages.size() > 0)
        	{
        		lValid = false;
                lMessageMap.put("title", "Entity Details");
    			lMessageMap.put("messages", lMessages);
                lMessageList.add(lMessageMap);
        	}
        	else{
        		lScore +=1;
        		lScores[ScoreIndex.EntityDetails.getCode()]=true;
        	}
            lMaxScore += 1;
        }
        
        // company contact (atleast one with authorised contact)
        CompanyContactBean lCompanyContactBean = new CompanyContactBean();
        lCompanyContactBean.setCdId(lCompanyDetailBean.getId());
        //
        //check all for the following
        //if Public or Private Co. => auth person should be there
        {
            List<String> lFields = null;
            List<CompanyContactBean> lCompanyContactBeans =  companyContactProvDAO.findList(pExecutionContext.getConnection(), lCompanyContactBean, lFields);
            Map<String, Object> lMessageMap = new HashMap<String, Object>();
            List<String> lMessages = new ArrayList<String>();
            boolean lHasPromoter =false, lHasAuthPer = false;
        	boolean lIsPublicPrivateCo = (AppConstants.RC_CONSTITUENTS_PRIVATE.equals(lCompanyDetailBean.getType())) || (AppConstants.RC_CONSTITUENTS_PUBLIC.equals(lCompanyDetailBean.getType())) ;
        	int lAdminCount = 0;
            if(lCompanyContactBeans!=null && lCompanyContactBeans.size() > 0)
            {
            	for(int lPtr=0; lPtr< lCompanyContactBeans.size(); lPtr++)
            	{
            		lCompanyContactBean = lCompanyContactBeans.get(lPtr);
                    //if Public or Private => one auth person should not be there
                	if(CommonAppConstants.Yes.Yes.equals(lCompanyContactBean.getAuthPer()))
                		lHasAuthPer = true;
                	if(CommonAppConstants.Yes.Yes.equals(lCompanyContactBean.getPromoter()))
                		lHasPromoter = true;
                	if(CommonAppConstants.Yes.Yes.equals(lCompanyContactBean.getAdmin()))
                		lAdminCount++;
            	}
            }
            else
            	lMessages.add("Management details not provided.");
        	if(lIsPublicPrivateCo && !lHasAuthPer)
        		lMessages.add("Authorised contact person is mandatory for Public and Private Cos.");
        	if(!lFin && !lHasPromoter)
        		lMessages.add("No Promoter specified.");
        	if(lAdminCount==0 || lAdminCount > 1)
        		lMessages.add("Entities must have only one Administrator.");

        	if(lMessages.size() > 0)
        	{
        		lValid = false;
                lMessageMap.put("title", "Management");
    			lMessageMap.put("messages", lMessages);
                lMessageList.add(lMessageMap);
        	}
        	else{
        		lScore +=1;
        		lScores[ScoreIndex.ManagementDetails.getCode()]=true;
        	}
            //
            lMaxScore += 1;
        }

        // company location if purchaser or financier
        if ((lCompanyDetailBean.getPurchaserFlag() == CommonAppConstants.YesNo.Yes) || 
                (lCompanyDetailBean.getSupplierFlag() == CommonAppConstants.YesNo.Yes)) {
            CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
            lCompanyLocationBean.setCdId(lCompanyDetailBean.getId());
            List<CompanyLocationBean> lLocations = companyLocationProvDAO.findList(pExecutionContext.getConnection(), lCompanyLocationBean, (String)null);
            List<String> lMessages = new ArrayList<String>();
            Map<String, Object> lMessageMap = new HashMap<String, Object>();
            if ((lLocations == null) || (lLocations.size() == 0)) 
                lMessages.add("Locations not defined.");
            else 
            {
            	boolean lRegLocFound = false;
            	String lLocationNames = "";
            	for(int lPtr=0; lPtr < lLocations.size(); lPtr++)
            	{
            		lCompanyLocationBean = lLocations.get(lPtr);
            		if(!lRegLocFound){
                		lRegLocFound =  (lCompanyLocationBean!=null && CompanyLocationBean.LocationType.RegOffice.equals(lCompanyLocationBean.getLocationType()));
            		}
            		if(CommonAppConstants.Yes.Yes.equals(lCompanyDetailBean.getEnableLocationwiseSettlement())){
            			if ((lCompanyLocationBean.getCbdId()==null && lCompanyLocationBean.getSettlementCLId()==null)) {
            				if(StringUtils.isNotEmpty(lLocationNames)){
            					lLocationNames += ", ";
            				}
            				lLocationNames += lCompanyLocationBean.getName() ;
            			}
            		}
            	}
            	if(!lRegLocFound)
            		lMessages.add("Atleast one Location should be marked as Registered Office.");
            	if(StringUtils.isNotEmpty(lLocationNames)){
            		lMessages.add("Please select a bank or a location for "+lLocationNames + ".");
            	}
            }
        	if(lMessages.size() > 0)
        	{
        		lValid = false;
                lMessageMap.put("title", "Location/Branch");
    			lMessageMap.put("messages", lMessages);
                lMessageList.add(lMessageMap);
        	}
        	else{
        		lScore +=1;
        		lScores[ScoreIndex.LocationsBranches.getCode()]=true;
        	}
            lMaxScore += 1;
        }
        // company bank
        {
            CompanyBankDetailBean lCompanyBankDetailBean = new CompanyBankDetailBean();
            lCompanyBankDetailBean.setCdId(lCompanyDetailBean.getId());
            List<CompanyBankDetailBean> lCompanyBankDetailBeans = companyBankDetailProvDAO.findList(pExecutionContext.getConnection(), lCompanyBankDetailBean, (String)null);
            List<String> lMessages = new ArrayList<String>();
            Map<String, Object> lMessageMap = new HashMap<String, Object>();
            if ((lCompanyBankDetailBeans == null) || (lCompanyBankDetailBeans.size() == 0)) {
                lMessages.add("Bank account details not provided.");
            } 
            else 
            {
            	ArrayList<Long> lDefaultBank = new ArrayList<Long>();
            	ArrayList<Long> lLeadBank = new ArrayList<Long>();
            	ArrayList<Long> lCashCreditActId = new ArrayList<Long>();
            	ArrayList<Long> lTermLoanActId = new ArrayList<Long>();
            	
            	for(int lPtr=0; lPtr < lCompanyBankDetailBeans.size(); lPtr++)
            	{
            		lCompanyBankDetailBean = lCompanyBankDetailBeans.get(lPtr);
            		if(CommonAppConstants.Yes.Yes.equals(lCompanyBankDetailBean.getDefaultAccount()))
            			lDefaultBank.add(lCompanyBankDetailBean.getId());
            		if(CommonAppConstants.Yes.Yes.equals(lCompanyBankDetailBean.getLeadBank()))
            			lLeadBank.add(lCompanyBankDetailBean.getId());
            		if(CompanyBankDetailBean.AccType.Cash_Credit.equals(lCompanyBankDetailBean.getAccType()))
            			lCashCreditActId.add(lCompanyBankDetailBean.getId());
            		if(CompanyBankDetailBean.AccType.Term_Loan.equals(lCompanyBankDetailBean.getAccType()))
            			lTermLoanActId.add(lCompanyBankDetailBean.getId());                		
            	}
            	if(lDefaultBank.size()==0)
            		lMessages.add("Designated transaction account not specified.");
            	else
            	{
            		if(lDefaultBank.size() > 1)
                		lMessages.add("Multiple Designated transaction account cannot be specified.");                			
            		else
            		{
            			//As TReDS is allowing Buyers to designate a current account as TReDS designated account, the validation for designated account to be a Cash Credit /Overdraft account may be removed in case of Buyer.
                		//if(lCashCreditActId.size() > 0 && !lCashCreditActId.contains(lDefaultBank.get(0)))
                		//	lMessages.add("Cash Credit should be marked Designated transaction account.");
                		if(lTermLoanActId.size() > 0 && lTermLoanActId.contains(lDefaultBank.get(0)))
                			lMessages.add("Term loan cannot be marked Designated transaction account.");
            		}
            	}
            	if(CompanyBankDetailBean.BankingType.Consortium.equals(lCompanyBankDetailBean.getBankingType())){
                	if(lLeadBank.size() == 0){
            			lMessages.add("Lead Bank not specified.");
            		}else if (lLeadBank.size() > 1){
            			lMessages.add("Multiple Lead Bank not permitted.");
            		}
        		}
            }
        	if(lMessages.size() > 0)
        	{
        		lValid = false;
                lMessageMap.put("title", "Banking Details");
                lMessageMap.put("messages", lMessages);
                lMessageList.add(lMessageMap);
        	}
        	else{
        		lScore +=1;
        		lScores[ScoreIndex.BankingDetails.getCode()]=true;
        	}
        }
        lMaxScore += 1;
        // company kyc
        if(StringUtils.isNotEmpty(lCompanyDetailBean.getCreatorIdentity())) {
        	lScore += 1;
    		lScores[ScoreIndex.EnclosuresUploads.getCode()]=true;
        }else {
            if (lCompanyDetailBean.getType() != null) {
                // bo not created in constructor since it was causing constructor dead lock
                CompanyKYCDocumentBO lCompanyKYCDocumentBO = new CompanyKYCDocumentBO();
                Map<String, Object> lDocuments = lCompanyKYCDocumentBO.findList(pExecutionContext, lCompanyDetailBean, pAppUserBean);
                List<Map<String, Object>> lDocumentTypeList = (List<Map<String, Object>>)lDocuments.get("meta");
                Map<String,List<Map<String,Object>>> lResults = ( Map<String,List<Map<String,Object>>>)lDocuments.get("data");
                Map<String, Map<String,Object>> lUpdateList = (Map<String, Map<String,Object>>)lDocuments.get("update");
                boolean lDocumentError = false;
                List<String> lMessages = null;
            	Map<String,Object> lUpdateItems = null;
            	List<Map<String,String>> lDocumentList = null;
            	
                for (int lPtr=0;lPtr<lDocumentTypeList.size();lPtr++) {
                    Map<String, Object> lDocTypeMap = lDocumentTypeList.get(lPtr);
                    //
                    //fetch the update list
                    if(lUpdateList!=null)
                    {
                    	lDocumentList = (List<Map<String,String>>)lDocTypeMap.get(OtherResourceCache.DOCUMENT_LIST);
                    	lUpdateItems = lUpdateList.get(lDocumentList.get(0).get("value"));
                    }
                    //
                    Boolean lSoftCopy = (Boolean)lDocTypeMap.get(OtherResourceCache.DOCUMENT_SOFTCOPY);
                    if ((lSoftCopy == null) || (lSoftCopy.booleanValue() == false)) continue;
                    Long lMinCount = (Long)lDocTypeMap.get(OtherResourceCache.MIN_COUNT);
                    Long lMaxCount = (Long)lDocTypeMap.get(OtherResourceCache.MAX_COUNT);
                    
                    //write the update list to the variables since the Map is unmodifiable
                	if(lUpdateItems!=null)
                	{
                		for(String lKey : lUpdateItems.keySet())
                		{
                			if(OtherResourceCache.MIN_COUNT.equals(lKey))
                				lMinCount = (Long)lUpdateItems.get(lKey);
                			else if(OtherResourceCache.MAX_COUNT.equals(lKey))
                				lMaxCount = (Long)lUpdateItems.get(lKey);
                			else if(OtherResourceCache.DOCUMENT_SOFTCOPY.equals(lKey))
                				lSoftCopy = (Boolean)lUpdateItems.get(lKey);
                		}
                	}
                    int lMinCountInt = lMinCount==null?0:lMinCount.intValue();
                    int lActualCountInt = 0;
                    List<Map<String,Object>> lTempResult = null;
                    for(String lKey : lResults.keySet()){
                    	lTempResult = lResults.get(lKey);
                    	if(lTempResult!=null){
                    		lActualCountInt += lTempResult.size();
                    	}
                    }
                    if (lActualCountInt < lMinCountInt) {
                        lValid = false;
                        lDocumentError = true;
                        if (lMessages == null) {
                            lMessages =  new ArrayList<String>();
                            Map<String, Object> lMessageMap = new HashMap<String, Object>();
                            lMessageMap.put("title", "Enclosures/Uploads");
                            lMessageMap.put("messages", lMessages);
                            lMessageList.add(lMessageMap);
                        }
                        lMessages.add("Minimum " + lMinCountInt + " documents need to be uploaded for '" + lDocTypeMap.get(OtherResourceCache.DOCUMENT_CATEGORY_DESC) 
                                + " / " + lDocTypeMap.get(OtherResourceCache.DOCUMENT_TYPE_DESC) + "'");
                    }
                }
                if (!lDocumentError){
                    lScore += 1;
            		lScores[ScoreIndex.EnclosuresUploads.getCode()]=true;
                }
            }
        }
        lMaxScore += 1;
        lData.put("valid", lValid);
        lData.put("messages", lMessageList);
        lData.put("score", Integer.valueOf(lScore));
        lData.put("maxScore", Integer.valueOf(lMaxScore));
        //
        //
        if ((lCompanyDetailBean.getApprovalStatus() == AppConstants.CompanyApprovalStatus.Approved)){
            lData.put("score", Integer.valueOf(lScores.length));
            lData.put("maxScore", Integer.valueOf(lScores.length));
            for(int lPtr=0; lPtr < lScores.length; lPtr++)
            	lScores[lPtr]=true;
        }
        //
        boolean lCanModify = TredsHelper.getInstance().canModifyRegistration( pAppUserBean.getDomain() , lCompanyDetailBean.getApprovalStatus());
        //
        if (lCanModify && pSubmit && lValid) {
        	boolean lCanSubmit = false;
        	CompanyApprovalStatus lNewApprovalStatus = CompanyApprovalStatus.Submitted;
        	
        	if(AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain())){
        		lCanSubmit = (AppConstants.CompanyApprovalStatus.Submitted.equals(lCompanyDetailBean.getApprovalStatus()) || AppConstants.CompanyApprovalStatus.Returned.equals(lCompanyDetailBean.getApprovalStatus())) ;
        		lNewApprovalStatus = CompanyApprovalStatus.ReSubmitted;
        	}else if(AppConstants.DOMAIN_REGUSER.equals(pAppUserBean.getDomain())){
        		lCanSubmit = (AppConstants.CompanyApprovalStatus.Draft.equals(lCompanyDetailBean.getApprovalStatus()) || 
        				AppConstants.CompanyApprovalStatus.Returned.equals(lCompanyDetailBean.getApprovalStatus()));
        		lNewApprovalStatus = CompanyApprovalStatus.Submitted;
        	}
            if (!lCanSubmit){
                throw new CommonBusinessException("Application cannot be submitted.");
            }
        	lCompanyDetailBean.setRegistrationNo(getRegFormNo(lCompanyDetailBean));
        	//lCompanyDetailBean.setRegistrationNo(getRegFormNo(pExecutionContext.getConnection()));

            lCompanyDetailBean.setApprovalStatus(lNewApprovalStatus);
            pExecutionContext.setAutoCommit(false);
            try {
                Connection lConnection = pExecutionContext.getConnection();
                
                companyDetailProvDAO.update(lConnection, lCompanyDetailBean, CompanyDetailBean.FIELDGROUP_APPROVALSTATUS);
                
                // workflow
                CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
                lCompanyWorkFlowBean.setCdId(lCompanyDetailBean.getId());
                lCompanyWorkFlowBean.setApprovalStatus(lNewApprovalStatus);
                lCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
                lCompanyWorkFlowBean.setReason("Registration No : " + lCompanyDetailBean.getRegistrationNo());
                lCompanyWorkFlowBean.setRecordCreator(pAppUserBean.getId());
                companyWorkFlowDAO.insert(lConnection, lCompanyWorkFlowBean);
                
                pExecutionContext.commit();

                lData.put("status", lCompanyDetailBean.getApprovalStatus().getCode());
                lData.put("statusDesc", lCompanyDetailBean.getApprovalStatus().toString());
                
                Map<String,Object> lDataValues = new HashMap<String, Object>();
            	lDataValues.put("memberCode", lCompanyDetailBean.getCode() );
            	lDataValues.put("memberName", lCompanyDetailBean.getCompanyName());
            	lDataValues.put("memberType", TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_ENTITYTYPE, lCompanyDetailBean.getType()));            	
            	lDataValues.put(EmailSender.TO, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
            	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONSUBMISSION, lDataValues);                
                //
            	//create charge
            	try {
            		RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
            		lRegistrationChargeBO.createRegistrationCharge(lConnection, lCompanyDetailBean.getCode(), pAppUserBean);
            	}catch(Exception lEx) {
            		logger.error("Error in creating charge for entity "+ lCompanyDetailBean.getCode(),lEx);
            	}
            	//
            } catch (Exception lException) {
                pExecutionContext.rollback();
                throw lException;
            }
        }
        // workflows
        CompanyWorkFlowBean lFilterCompanyWorkFlowBean = new CompanyWorkFlowBean();
        lFilterCompanyWorkFlowBean.setCdId(lCompanyDetailBean.getId());
        String lSql = "SELECT * FROM CompanyWorkFlow WHERE CWFCdId = " + lCompanyDetailBean.getId() + " ORDER BY CWFRecordCreateTime DESC";
        List<CompanyWorkFlowBean> lWorkFlows = companyWorkFlowDAO.findListFromSql(pExecutionContext.getConnection(), lSql, -1);
        List<Map<String, Object>> lWorkFlowJsons = new ArrayList<Map<String,Object>>();
        for (CompanyWorkFlowBean lCompanyWorkFlowBean : lWorkFlows) {
            lWorkFlowJsons.add(companyWorkFlowBeanMeta.formatAsMap(lCompanyWorkFlowBean, null, null, true, true));
        }
        lData.put("workFlow", lWorkFlowJsons);
        lData.put("scores", lScores);
        return lData;

    }

    public String getCompanyJson(ExecutionContext pExecutionContext, 
        Long pId, String pEntityCode, boolean pFinal, IAppUserBean pAppUserBean) throws Exception {
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        
        if (AppConstants.DOMAIN_REGUSER.equals(pAppUserBean.getDomain()))
            lFilterBean.setId(pAppUserBean.getId());
        else if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()) && 
        		 !AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain()) ) {
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
            if (lAppEntityBean.isFinancier() && StringUtils.isNotBlank(pEntityCode)) {
                AppEntityBean lViewAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
                if (lViewAppEntityBean == null)
                    throw new CommonBusinessException("Invalid entity " + pEntityCode);
                else if (!lViewAppEntityBean.isPurchaser() && !lViewAppEntityBean.isSupplier())
                    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
                lFilterBean.setId(lViewAppEntityBean.getCdId());
            } else
                lFilterBean.setId(lAppEntityBean.getCdId());
        } else if (pId != null) {
            lFilterBean.setId(pId);
        } else if (pEntityCode != null) {
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
            if (lAppEntityBean == null)
                throw new CommonBusinessException("Invalid entity " + pEntityCode);
            lFilterBean.setId(lAppEntityBean.getCdId());
        } else
            throw new CommonBusinessException("Input parameters missing");
            
        CompanyDetailBean lCompanyDetailBean = null;
        Connection lConnection = pExecutionContext.getConnection();
        if (pFinal) 
            lCompanyDetailBean = companyDetailDAO.findByPrimaryKey(lConnection, lFilterBean);
        else
            lCompanyDetailBean = companyDetailProvDAO.findByPrimaryKey(lConnection, lFilterBean);
        if (lCompanyDetailBean == null)
            throw new CommonBusinessException("Company details not found");
        Map<String, Object> lDetailMap = getDAO(pFinal).getBeanMeta().formatAsMap(lCompanyDetailBean, null, null, true, true);
        //
        String lTempCode = null;
        if (lDetailMap.containsKey("industry"))
        	lTempCode = (String)lDetailMap.get("industry"); 
        if(CommonUtilities.hasValue(lTempCode))
        {
        	lTempCode =  TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_INDUSTRY, lTempCode);
        	lDetailMap.put("industry", lTempCode);
        }
        lTempCode = null;
        if (lDetailMap.containsKey("subSegment"))
        	lTempCode = (String)lDetailMap.get("subSegment"); 
        if(CommonUtilities.hasValue(lTempCode))
        {
        	lTempCode =  TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_SUBSEGMENT, lTempCode);
        	lDetailMap.put("subSegment", lTempCode);
        }
        //
        // locations
        List<Map<String, Object>> lLocationMaps = new ArrayList<Map<String,Object>>();
        CompanyLocationBean lCompanyLocationFilterBean = new CompanyLocationBean();
        lCompanyLocationFilterBean.setCdId(lFilterBean.getId());
        List<CompanyLocationBean> lLocations = (pFinal?companyLocationDAO:companyLocationProvDAO).findList(lConnection, lCompanyLocationFilterBean, (String)null);
        for (CompanyLocationBean lCompanyLocationBean : lLocations) {
            lLocationMaps.add(companyLocationProvDAO.getBeanMeta().formatAsMap(lCompanyLocationBean, null, null, true, true));
        }
        lDetailMap.put("locations", lLocationMaps);
        
        // contactpersons
        List<Map<String, Object>> lContactMaps = new ArrayList<Map<String,Object>>();
        CompanyContactBean lCompanyContactFilterBean = new CompanyContactBean();
        lCompanyContactFilterBean.setCdId(lFilterBean.getId());
        List<CompanyContactBean> lContacts = (pFinal?companyContactDAO:companyContactProvDAO).findList(lConnection, lCompanyContactFilterBean, (String)null);
        for (CompanyContactBean lCompanyContactBean : lContacts) {
            lContactMaps.add(companyContactProvDAO.getBeanMeta().formatAsMap(lCompanyContactBean, null, null, true, true));
        }
        lDetailMap.put("contacts", lContactMaps);
        
        // bankaccounts
        List<Map<String, Object>> lBankDetailMaps = new ArrayList<Map<String,Object>>();
        CompanyBankDetailBean lCompanyBankDetailFilterBean = new CompanyBankDetailBean();
        lCompanyBankDetailFilterBean.setCdId(lFilterBean.getId());
        List<CompanyBankDetailBean> lBankDetails = (pFinal?companyBankDetailDAO:companyBankDetailProvDAO).findList(lConnection, lCompanyBankDetailFilterBean, (String)null);
        for (CompanyBankDetailBean lCompanyBankDetailBean : lBankDetails) {
            lBankDetailMaps.add(companyBankDetailProvDAO.getBeanMeta().formatAsMap(lCompanyBankDetailBean, null, null, true, true));
        }
        lDetailMap.put("bankDetails", lBankDetailMaps);
        
        // kycdocuments
        // bo not created in constructor since it was causing constructor dead lock
        CompanyKYCDocumentBO lCompanyKYCDocumentBO = new CompanyKYCDocumentBO();
        Map<String, Object> lDocuments = lCompanyKYCDocumentBO.findList(pExecutionContext, lCompanyDetailBean, pAppUserBean);
        lDetailMap.put("kycDocuments", lDocuments);
        
        // workflow
        List<Map<String, Object>> lWorkFlowMaps = new ArrayList<Map<String,Object>>();
        CompanyWorkFlowBean lCompanyWorkFlowFilterBean = new CompanyWorkFlowBean();
        lCompanyWorkFlowFilterBean.setCdId(lFilterBean.getId());
        List<CompanyWorkFlowBean> lWorkFlows = companyWorkFlowDAO.findList(lConnection, lCompanyWorkFlowFilterBean, (String)null);
        for (CompanyWorkFlowBean lCompanyWorkFlowBean : lWorkFlows) {
            lWorkFlowMaps.add(companyWorkFlowDAO.getBeanMeta().formatAsMap(lCompanyWorkFlowBean, null, null, true, true));
        }
        lDetailMap.put("workFlows", lWorkFlowMaps);
        
        //Buyer Credit Rating
        List<Map<String, Object>> lBuyerCreditMaps = new ArrayList<Map<String,Object>>();
        BuyerCreditRatingBO lBuyerCreditRatingBO = new BuyerCreditRatingBO();
        Map<String, Object> lMap = new HashMap<String, Object>();
        AppEntityBean lTmpAppEntityBean = null;
        //
        if(StringUtils.isNotEmpty(pEntityCode) ){
        	lTmpAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
        }else{
        	lTmpAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAppUserBean.getDomain());
        }
    	if(!lTmpAppEntityBean.isPurchaser()){
    		lTmpAppEntityBean = null;
    	}
        if(lTmpAppEntityBean!=null){
        	lMap.put("buyerCode", lTmpAppEntityBean.getCode());
        	List<BuyerCreditRatingBean> lBuyerCreditRatingBeanList = lBuyerCreditRatingBO.findValidRatings(lConnection, lMap, pAppUserBean);
            if(lBuyerCreditRatingBeanList != null && lBuyerCreditRatingBeanList.size()> 0){
            	for(BuyerCreditRatingBean lBuyerCreditRatingBean : lBuyerCreditRatingBeanList){
            		lBuyerCreditMaps.add(buyerCreditRatingDAO.getBeanMeta().formatAsMap(lBuyerCreditRatingBean, null, null, true, true));
            	}
            }
            if(!StringUtils.isEmpty(lTmpAppEntityBean.getCreditReport())){
            	lDetailMap.put("creditReport", lTmpAppEntityBean.getCreditReport());
            }
			lDetailMap.put("buyerCreditRating", lBuyerCreditMaps);
        }
        RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
        List<RegistrationChargeBean> lRegChrgBeans = lRegistrationChargeBO.getRegistrationData(lConnection, StringUtils.isNotEmpty(pEntityCode)?pEntityCode:pAppUserBean.getDomain(), pAppUserBean);
        if(lRegChrgBeans!=null && lRegChrgBeans.size() > 0) {
            List<Map<String, Object>> lRCMaps = new ArrayList<Map<String,Object>>();
        	for(RegistrationChargeBean lRCBean : lRegChrgBeans) {
        		lRCMaps.add(registrationChargeDAO.getBeanMeta().formatAsMap(lRCBean, null, null, true, true));
        	}
            lDetailMap.put("regAnnFees", lRCMaps);
        }
        return new JsonBuilder(lDetailMap).toString();
    }
    
    public void updateStatus(ExecutionContext pExecutionContext, CompanyWorkFlowBean pCompanyWorkFlowBean, IAppUserBean pUserBean, 
            boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        lFilterBean.setId(pCompanyWorkFlowBean.getCdId());
        CompanyDetailBean lCompanyDetailBean = companyDetailProvDAO.findByPrimaryKey(lConnection, lFilterBean);
        boolean lSendEmail = true;
        boolean lApprovalModificationSubmit = false;
        if (lCompanyDetailBean == null)
            throw new CommonBusinessException("Registration details not found.");
        if (CompanyApprovalStatus.ApprovalModificationSubmit.equals(lCompanyDetailBean.getApprovalStatus())){
        	lSendEmail = false;
        	lApprovalModificationSubmit = true;
        }
    	if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) && 
    			( CompanyApprovalStatus.Approved.equals(pCompanyWorkFlowBean.getApprovalStatus()) 
    				|| CompanyApprovalStatus.Returned.equals(pCompanyWorkFlowBean.getApprovalStatus()) 
    					|| CompanyApprovalStatus.Rejected.equals(pCompanyWorkFlowBean.getApprovalStatus()))) {
    		//only admin can return
    		if(!( CompanyApprovalStatus.ReSubmitted.equals(lCompanyDetailBean.getApprovalStatus()) 
    				|| CompanyApprovalStatus.Submitted.equals(lCompanyDetailBean.getApprovalStatus())
    				|| CompanyApprovalStatus.ApprovalModificationSubmit.equals(lCompanyDetailBean.getApprovalStatus())
    				)){
    			if (CompanyApprovalStatus.Approved.equals(pCompanyWorkFlowBean.getApprovalStatus()) && 
    					( CompanyApprovalStatus.Draft.equals(lCompanyDetailBean.getApprovalStatus()) 
    							|| CompanyApprovalStatus.Returned.equals(lCompanyDetailBean.getApprovalStatus()) )){
    				throw new CommonBusinessException("Please make sure that registration form is submitted before approving it.");
    			}
    			throw new CommonBusinessException("Registration detail status cannot be modified now.");
    		}
    	}else{
    		boolean lModify = TredsHelper.getInstance().canModifyRegistration(pUserBean.getDomain(), pCompanyWorkFlowBean.getApprovalStatus()) ;
            if (!lModify){
                throw new CommonBusinessException("Registration detail status cannot be modified now.");
            }
    	}
        //
        if(pCompanyWorkFlowBean.getApprovalStatus() == CompanyApprovalStatus.Approved)
        {
        	String lReason = "";
        	if(CommonUtilities.hasValue(pCompanyWorkFlowBean.getReason())) lReason = pCompanyWorkFlowBean.getReason();
        	pCompanyWorkFlowBean.setReason(lReason + " New Login Entity : " + lCompanyDetailBean.getCode());
        }else if (CompanyApprovalStatus.Returned.equals(pCompanyWorkFlowBean.getApprovalStatus()) && CompanyApprovalStatus.Approved.equals(lCompanyDetailBean.getApprovalStatus())){
        	throw new CommonBusinessException("Registration details approved status cannot be modified now. ");
        }
        //
        pCompanyWorkFlowBean.setRecordCreator(pUserBean.getId());
        pCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
        companyWorkFlowDAO.insert(lConnection, pCompanyWorkFlowBean);
        
        lCompanyDetailBean.setApprovalStatus(pCompanyWorkFlowBean.getApprovalStatus());
        companyDetailProvDAO.update(lConnection, lCompanyDetailBean, CompanyDetailBean.FIELDGROUP_APPROVALSTATUS);
        //
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
	    AppUserBean lRegUserBean = null;
	    lRegUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lCompanyDetailBean.getId()});
	    
        //
        if (lCompanyDetailBean.getApprovalStatus() == CompanyApprovalStatus.Approved) {
            // insert into final table
            CompanyDetailBean lFinalCompanyDetailBean = new CompanyDetailBean();
            lFinalCompanyDetailBean.setId(lCompanyDetailBean.getId());
            lFinalCompanyDetailBean = companyDetailDAO.findByPrimaryKey(lConnection, lFinalCompanyDetailBean);
            if (lFinalCompanyDetailBean == null) {
                lCompanyDetailBean.setRecordCreator(pUserBean.getId());
                companyDetailDAO.insert(lConnection, lCompanyDetailBean);
                lFinalCompanyDetailBean = lCompanyDetailBean;
                //Insert default plan for the entity purchaser 
                //Insert plan zero for the entity financier 
                if(CommonAppConstants.YesNo.Yes.equals(lCompanyDetailBean.getPurchaserFlag()) 
                		|| CommonAppConstants.YesNo.Yes.equals(lCompanyDetailBean.getFinancierFlag()) ){
                    MemberwisePlanBean lMemPlan = new MemberwisePlanBean();
                    lMemPlan.setCode(lCompanyDetailBean.getCode());
                    if(CommonAppConstants.YesNo.Yes.equals(lCompanyDetailBean.getPurchaserFlag())) {
                    	lMemPlan.setAcpId(AppConstants.DEFAULT_PLAN_PURCHASER);
                    }else if (CommonAppConstants.YesNo.Yes.equals(lCompanyDetailBean.getFinancierFlag())) {
                    	lMemPlan.setAcpId(AppConstants.DEFAULT_PLAN_ZERO);
                    }
                    lMemPlan.setEffectiveStartDate(TredsHelper.getInstance().getBusinessDate());
                    lMemPlan.setRecordCreator(pUserBean.getId());
                    memberwisePlanDAO.insert(lConnection, lMemPlan);
                    //inser audit 
                    memberwisePlanDAO.insertAudit(lConnection, lMemPlan, AuditAction.Insert, pUserBean.getId());
                }
            } else {
                lCompanyDetailBean.setRecordUpdator(pUserBean.getId());
                lCompanyDetailBean.setRecordVersion(lFinalCompanyDetailBean.getRecordVersion());
                companyDetailDAO.update(lConnection, lCompanyDetailBean, BeanMeta.FIELDGROUP_UPDATE);
            }
            //
			if(!IAppUserBean.Status.Disabled.equals(lRegUserBean.getStatus())){
		    	AppUserBean lOldRegUserBean = new AppUserBean();
				appUserDAO.getBeanMeta().copyBean(lRegUserBean, lOldRegUserBean);
				lRegUserBean.setStatus(IAppUserBean.Status.Disabled);
				if (appUserDAO.update(lConnection, lRegUserBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
	                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
	            appUserDAO.insertAudit(lConnection, lOldRegUserBean, GenericDAO.AuditAction.Update, pUserBean.getId());
	            lMemoryDBConnection.deleteRow(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Id, lOldRegUserBean);
	            lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, lRegUserBean);
	            if(lOldRegUserBean.getRecordCreator()==null){
	            	lOldRegUserBean.setRecordCreator(lRegUserBean.getRecordCreator()!=null?lRegUserBean.getRecordCreator(): pUserBean.getId());
	            }
		    }
		    //
            AppEntityBean lAppEntityBean = new AppEntityBean();
            AppUserBean lAppUserBean = null;
            Map <String,String> lPasswordMap = new HashMap<>();
			if (!lApprovalModificationSubmit){
	            // add record to app entity
	            lAppEntityBean.setCdId(lCompanyDetailBean.getId());
	            AppEntityBean lOldAppEntityBean = appEntityDAO.findBean(lConnection, lAppEntityBean);

	            lAppEntityBean.setCode(lCompanyDetailBean.getCode());
	            lAppEntityBean.setName(lCompanyDetailBean.getCompanyName());
	            lAppEntityBean.setType(lCompanyDetailBean.getType());
	            lAppEntityBean.setStatus(AppEntityStatus.Active);
	            lAppEntityBean.setSalesCategory(lCompanyDetailBean.getSalesCategory());
	            lAppEntityBean.setMsmeStatus(lCompanyDetailBean.getMsmeStatus());
	            //
	            //find the chief promoter and set his category to appentitybean to use in factoring filter.
	            CompanyContactBean lChiefPromoterContactBean = new CompanyContactBean();
	            lChiefPromoterContactBean.setCdId(lCompanyDetailBean.getId());
	            lChiefPromoterContactBean.setChiefPromoter(CommonAppConstants.Yes.Yes);
	            lChiefPromoterContactBean = companyContactProvDAO.findBean(lConnection, lChiefPromoterContactBean);
	            //
	            lAppEntityBean.setPromoterCategory((lChiefPromoterContactBean!=null?lChiefPromoterContactBean.getCpCat():null));
	            lAppEntityBean.setPan(lCompanyDetailBean.getPan());
	            //            
	            
	            if (lOldAppEntityBean == null) {
	                lAppEntityBean.setRecordCreator(pUserBean.getId());
	                appEntityDAO.insert(lConnection, lAppEntityBean);
	            } else {
	                lAppEntityBean.setRecordUpdator(pUserBean.getId());
	                lAppEntityBean.setRecordVersion(lOldAppEntityBean.getRecordVersion());
	                appEntityDAO.update(lConnection, lAppEntityBean, BeanMeta.FIELDGROUP_UPDATE);
	                lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
	            }
	            lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
	            lFinalCompanyDetailBean.setIsProvisional(true);
	            lAppUserBean = createAdminUser(lConnection, lFinalCompanyDetailBean ,lMemoryDBConnection ,pUserBean,lPasswordMap);
	            if(lOldAppEntityBean==null) {
	            	//create charge
	            	try {
	            		RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
	            		lRegistrationChargeBO.createRegistrationCharge(lConnection, lCompanyDetailBean.getCode(), pUserBean);
	            	}catch(Exception lEx) {
	            		logger.error("Error in creating charge for entity : "+ lCompanyDetailBean.getCode(),lEx);
	            	}
	            }
			}else {
	            lAppEntityBean.setCdId(lCompanyDetailBean.getId());
	            lAppEntityBean = appEntityDAO.findBean(lConnection, lAppEntityBean);
	            lAppUserBean = TredsHelper.getInstance().getAppUserBean(lAppEntityBean.getCode(), AppConstants.LOGINID_ADMIN);
			}
            // send email on approval
            if(lSendEmail && (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))){
            	//On Approval Send 3 Mails
            	//1. All Working Capital Banker of the Buyer/Seller and TReDS Designated Bank 
            	//2. Applicant entity (Registration Approval)
            	//3. Applicant entity (Login Details)
            	HashMap<String, Object> lDataValues = new HashMap<String, Object>();
            	if(lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()){
                	//1. All Working Capital Banker of the Buyer/Seller and TReDS Designated Bank 
            		CompanyBankDetailBean lCBDBean = new CompanyBankDetailBean();
            		List<CompanyBankDetailBean> lCompanyBankDetailBeans = null;
            		lCBDBean.setCdId(lAppEntityBean.getCdId());
            		lCompanyBankDetailBeans = companyBankDetailProvDAO.findList(lConnection, lCBDBean,(String)null);
            		if(lCompanyBankDetailBeans!=null){
            			String lTemplateFile = null;
            			String lTredsEmail = TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM);
                    	if(lAppEntityBean.isPurchaser()){
                        	lTemplateFile = AppConstants.TEMPLATE_REGISTRATIONBUYERBANK;
                    	}else if( lAppEntityBean.isSupplier()){
                        	lTemplateFile = AppConstants.TEMPLATE_REGISTRATIONSELLERBANK;
                    	}
                		CompanyBankDetailBean lDesignatedCBDBean = null;
            			for(int lPtr=0; lPtr < lCompanyBankDetailBeans.size(); lPtr++){
            				lCBDBean = lCompanyBankDetailBeans.get(lPtr);
            				if(Yes.Yes.equals(lCBDBean.getDefaultAccount())){
                				lDesignatedCBDBean = lCBDBean;
                				break;
            				}
            			}
            			if(lDesignatedCBDBean!=null){
                			for(int lPtr=0; lPtr < lCompanyBankDetailBeans.size(); lPtr++){
                				lCBDBean = lCompanyBankDetailBeans.get(lPtr);
                				if(CompanyBankDetailBean.AccType.Cash_Credit.equals(lCBDBean.getAccType()) 
                						|| CompanyBankDetailBean.AccType.Overdraft.equals(lCBDBean.getAccType())
                						||  Yes.Yes.equals(lCBDBean.getDefaultAccount())){
                                	lDataValues = new HashMap<String, Object>();
                                	//1. All Working Capital Banker of the Buyer/Seller and TReDS Designated Bank 
                                	lDataValues.put("memberName", lCompanyDetailBean.getCompanyName());
                                	lDataValues.put("constitution", TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CONSTITUTION, lCompanyDetailBean.getConstitution()));
                                	//show the designated bank name
                                	lDataValues.put("bankName", TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lDesignatedCBDBean.getBank()));	
                                	//send the mail to all the banks including designated
                                	lDataValues.put(EmailSender.TO, lCBDBean.getEmail());
                                	lDataValues.put(EmailSender.CC, lTredsEmail);
                                	//
                                	EmailSender.getInstance().addMessage(lTemplateFile, new HashMap<String,Object>(lDataValues));
                				}
                			}
            			}else{
            				logger.info("Designated bank not found for " + lAppEntityBean.getCode());
            			}
            		}
            	}
            	//2. Applicant entity (Registration Approval)
    			if (!lApprovalModificationSubmit){
                	lDataValues = new HashMap<String, Object>();
                	lDataValues.put("memberCode", lAppUserBean.getDomain() );
                	lDataValues.put("loginId", lAppUserBean.getLoginId());
                	lDataValues.put(EmailSender.TO, lAppUserBean.getEmail());
                	lDataValues.put(EmailSender.CC, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
                	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONAPPROVED, new HashMap<String,Object>(lDataValues));
                	//3. Applicant entity (Login Details)
                	sendAdminUserCreationEmail(lPasswordMap,lAppUserBean);
    			}
            }
            CompanyWrapperBean lCWMainBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), false, lAppUserBean.getId());
            CompanyWrapperBean lCWProvBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), true, lAppUserBean.getId());
            lCWMainBean.updateDatabase(lCWProvBean);
        }else if (lCompanyDetailBean.getApprovalStatus() == CompanyApprovalStatus.Returned) {
        	Map<String, Object> lDataValues = new HashMap<String, Object>();
            CompanyContactBean lCompanyContactBean = new CompanyContactBean();
            lCompanyContactBean.setCdId(lCompanyDetailBean.getId());
            lCompanyContactBean.setAdmin(CommonAppConstants.Yes.Yes);
            lCompanyContactBean = companyContactProvDAO.findBean(lConnection, lCompanyContactBean);
        	lDataValues.put("memberCode", lCompanyDetailBean.getCode());
        	List<String> lToEmails = new ArrayList<String>();
            List<String> lCCEmails = new ArrayList<String>();
            if (lCompanyContactBean != null && CommonUtilities.hasValue(lCompanyContactBean.getEmail())){
            	lToEmails.add(lCompanyContactBean.getEmail());
            }
            if(lRegUserBean!=null){
                lToEmails.add(lRegUserBean.getEmail());
                lCCEmails.add(TredsHelper.getInstance().getUserEmail(lRegUserBean.getRecordCreator()));
            }
        	lDataValues.put("remarks", pCompanyWorkFlowBean.getReason());
        	lCCEmails.add(TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
            if(!lToEmails.isEmpty()){
            	lDataValues.put(EmailSender.TO, lToEmails);
            }
        	lDataValues.put(EmailSender.CC, lCCEmails);
        	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONRETURNED, lDataValues);
        	//TODO: CHECK - REVERSING - COPYING DATA FROM MAIN TO PROVISIONAL
            CompanyWrapperBean lCWMainBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), false, lRegUserBean.getRecordCreator());
            CompanyWrapperBean lCWProvBean = new CompanyWrapperBean(lConnection, lCompanyDetailBean.getId(), true, lRegUserBean.getRecordCreator());
            lCWProvBean.updateDatabase(lCWMainBean);
        }
        pExecutionContext.commitAndDispose();
    }
    
    private void sendAdminUserCreationEmail(Map<String, String> pPasswordMap, AppUserBean pAppUserBean) {
    	HashMap<String, Object> lDataValues = new HashMap<String, Object>();
        lDataValues.put("memberCode", pAppUserBean.getDomain() );
    	lDataValues.put("loginId", pAppUserBean.getLoginId());
    	lDataValues.put("password", pPasswordMap.get("password"));
    	lDataValues.put(EmailSender.TO, pAppUserBean.getEmail());
    	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONLOGINDETAILS, new HashMap<String,Object>(lDataValues));
		
	}

	public String getAddressAsJson(ExecutionContext pExecutionContext, 
            Long pCompanyId, IAppUserBean pAppUserBean, boolean pCorrespondanceAddress, boolean pIsProvisional) throws Exception {
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        if (AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain())){
            lFilterBean.setId(pCompanyId);
        }else {
        	Long lCompanyId = null;
        	if(AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain())){
        		lCompanyId = pCompanyId;
        	}else{
            	lCompanyId = TredsHelper.getInstance().getCompanyId(pAppUserBean);
        	}
        	if(lCompanyId!=null && lCompanyId.longValue() > 0){
                lFilterBean.setId(lCompanyId);
        	}else{
                throw new CommonBusinessException("Entity details not found for " + pAppUserBean.getDomain());
        	}
        }
        CompanyDetailBean lCompanyDetailBean = null;
        Connection lConnection = pExecutionContext.getConnection();
        lCompanyDetailBean = getDAO(pIsProvisional).findByPrimaryKey(lConnection, lFilterBean);
//        if (lCompanyDetailBean == null) 
//            lCompanyDetailBean = getDAO(true).findByPrimaryKey(lConnection, lFilterBean);
        if (lCompanyDetailBean == null)
            throw new CommonBusinessException("Company details not found");
    	return companyDetailBeanMeta.formatAsJson(lCompanyDetailBean, (pCorrespondanceAddress?"corAddDetails":"regAddDetails"), null, false);
    }

	private String getRegFormNo(CompanyDetailBean pCompanyDetailBean)
    {
    	String lFormRegNo = "";
    	lFormRegNo = pCompanyDetailBean.getCompanyName().substring(0, 2);
    	lFormRegNo += FormatHelper.getDisplay("yyyyMMddhhmmss", CommonUtilities.copyDateToCurrent(new Date(System.currentTimeMillis())));
    	if(pCompanyDetailBean.getId()!=null)
    		lFormRegNo += "_" + pCompanyDetailBean.getId().toString();
    	return lFormRegNo;
    }
	private MimeBodyPart getFileAsAttachment(Connection pConnection, String pConstitution, String pEntityType){
    	byte[] lByteArray= null;
    	String lFileType = "application/zip";
    	try{
    		RegistrationFilesBean lRegistrationFilesBean = new RegistrationFilesBean();
    		lRegistrationFilesBean.setConstitution(pConstitution);
    		lRegistrationFilesBean.setEntityType(pEntityType);
    		UploadFileBean lFilterBean = new UploadFileBean();
        	lFilterBean.setKey(lRegistrationFilesBean.getKey());
        	UploadFileBean lBean = uploadFileDAO.findBean(pConnection, lFilterBean);
        	StringBuffer lSql = new StringBuffer();
        	lSql.append(" SELECT * FROM FILEDATA_REGISTRATIONS WHERE FDID = ").append(DBHelper.getInstance().formatString(lBean.getStorageFileName()));
        	try{
        		Statement lStatement = pConnection.createStatement();
        		ResultSet lResultSet = lStatement.executeQuery(lSql.toString());
        		if (lResultSet.next()) {
        			lByteArray = lResultSet.getBytes("FDCONTENT");
                }
        	}catch(Exception e){
        		logger.info("error in getFileAsAttachment : "+e.getMessage());
        	}finally{
        		if(pConnection!=null){
        			pConnection.close();
        		}
        		
        	}
		    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		    lFileType = "application/" + StringUtils.substringAfterLast(lBean.getFileName(), ".");
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArray, lFileType)));
			lMimeBodyPart.setFileName(lBean.getFileName());
			return lMimeBodyPart;
		} 
		catch (Exception ex) 
		{
			logger.info("Err in getFileAsAttachment " + ex.getMessage());
		}
		return null;
	}
	public CompanyDetailBean createEntity(ExecutionContext pExecutionContext, Map<String, Object> pMap,
			AppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		CompanyDetailBean lCompanyBean = new CompanyDetailBean();
		List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(lCompanyBean, pMap, CompanyDetailBean.FIELDGROUP_INCOMINGREQUESTONBOARDING, null);
		   if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
               for (ValidationFailBean lValidationFailBean : lValidationFailBeans) {
                   throw new CommonBusinessException("Error for " +lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage());
               }
           } 
		CompanyDetailBean lFilterBean = new CompanyDetailBean();
		lFilterBean.setCompanyName(pMap.get("companyName").toString());
		CompanyDetailBean lCompanyDetailBean = companyDetailProvDAO.findBean(lConnection, lFilterBean);
		if( lCompanyDetailBean!=null && lCompanyDetailBean.getCompanyName().equals(pMap.get("companyName").toString())){
			if (lCompanyDetailBean.getPan().equals(pMap.get("pan")) && lCompanyDetailBean.getCreatorIdentity()!=null ) {
				return lCompanyDetailBean;
			}
			throw new CommonBusinessException("Company with the same name already exist");
		}
		String lCode = TredsHelper.getInstance().createCompanyOrEntityCode(lConnection, pMap.get("companyName").toString());
		lCompanyBean.setCode(lCode);
		lCompanyBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, "AppUsers.id"));
		lCompanyBean.setRecordCreator(pUserBean.getId());
		try{
			companyDetailProvDAO.insert(lConnection, lCompanyBean , CompanyDetailBean.FIELDGROUP_INSERTONBOARDING);
			// workflow
            CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
            lCompanyWorkFlowBean.setCdId(lCompanyBean.getId());
            lCompanyWorkFlowBean.setApprovalStatus(CompanyApprovalStatus.Draft);
            lCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
            lCompanyWorkFlowBean.setReason("Creation");
            lCompanyWorkFlowBean.setRecordCreator(pUserBean.getId());
            companyWorkFlowDAO.insert(lConnection, lCompanyWorkFlowBean);
			return lCompanyBean;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String updateCompany(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		   CompanyDetailBean lCompanyBean = new CompanyDetailBean();
		   pExecutionContext.setAutoCommit(false);
		   Connection lConnection = pExecutionContext.getConnection();
		   boolean lApprovalFlag = false;
		   boolean lCompNameFlag = false;
		   List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
		   validateAPIData(lCompanyBean,pMap,lMessages);
		   CompanyDetailBean lFilterBean = new CompanyDetailBean();
		   AppEntityBean lAppEntityBean = null;
		   lFilterBean.setCode(pMap.get("code").toString());
		   lFilterBean = companyDetailProvDAO.findBean(lConnection, lFilterBean);
		   if(lFilterBean != null){
			   String lFieldGrp = CompanyDetailBean.FIELDGROUP_UPDATECOMPANY;
			   lCompanyBean.setId(lFilterBean.getId());
			   if (lCompanyBean.getApprovalStatus() == null) {
				   lCompanyBean.setApprovalStatus(CompanyApprovalStatus.Approved);
				   if(lCompanyBean.getRegistrationNo() == null){
					   lCompanyBean.setRegistrationNo(getRegFormNo(lFilterBean));
				   }
				   lFieldGrp = CompanyDetailBean.FIELDGROUP_UPDATECOMPANYDB;
			   }
			   companyDetailProvDAO.getBeanMeta().copyBean(lCompanyBean, lFilterBean,lFieldGrp, null);
			   lCompanyBean = lFilterBean;
			   companyDetailProvDAO.update(lConnection, lCompanyBean ,lFieldGrp );
		   }else {
			   throw new CommonBusinessException("Company not found");
		   }
		   MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
		   boolean lCreateCharge = false;
		   if (CompanyApprovalStatus.Approved.equals(lCompanyBean.getApprovalStatus())){
	            // insert into final table
	            CompanyDetailBean lFinalCompanyDetailBean = new CompanyDetailBean();
	            lFinalCompanyDetailBean.setId(lCompanyBean.getId());
	            lFinalCompanyDetailBean = companyDetailDAO.findByPrimaryKey(lConnection, lFinalCompanyDetailBean);
	            if (lFinalCompanyDetailBean == null) {
	            	lApprovalFlag = true;
	            	lCompanyBean.setRecordCreateTime(CommonUtilities.getCurrentDateTime());
	            	lCompanyBean.setRecordCreator(pUserBean.getId());
	                companyDetailDAO.insert(lConnection, lCompanyBean);
	           	    //Insert default plan for the entity purchaser 
	                if(CommonAppConstants.YesNo.Yes.equals(lCompanyBean.getPurchaserFlag())){
	                    MemberwisePlanBean lMemPlan = new MemberwisePlanBean();
	                    lMemPlan.setCode(lCompanyBean.getCode());
	                    lMemPlan.setAcpId(new Long(1));
	                    lMemPlan.setEffectiveStartDate(TredsHelper.getInstance().getBusinessDate());
	                    lMemPlan.setRecordCreator(pUserBean.getId());
	                    memberwisePlanDAO.insert(lConnection, lMemPlan);
	                    //inser audit 
	                    memberwisePlanDAO.insertAudit(lConnection, lMemPlan, AuditAction.Insert, pUserBean.getId());
	                    ///
	                }
	                lCreateCharge =true;
	            } else {
	                lCompanyBean.setRecordUpdator(pUserBean.getId());
	                lCompanyBean.setRecordVersion(lFinalCompanyDetailBean.getRecordVersion());
	                if(!lFinalCompanyDetailBean.getCompanyName().equals(lCompanyBean.getCompanyName())){
	            		lCompNameFlag = true;
	            	}
	                companyDetailDAO.update(lConnection, lCompanyBean, BeanMeta.FIELDGROUP_UPDATE);
	            }
	            // add record to app entity
	            lAppEntityBean = new AppEntityBean();
	            lAppEntityBean.setCdId(lCompanyBean.getId());
	            AppEntityBean lOldAppEntityBean = appEntityDAO.findBean(lConnection, lAppEntityBean);

	            lAppEntityBean.setCode(lCompanyBean.getCode());
	            lAppEntityBean.setName(lCompanyBean.getCompanyName());
	            lAppEntityBean.setType(lCompanyBean.getType());
	            lAppEntityBean.setStatus(AppEntityStatus.Active);
	            lAppEntityBean.setSalesCategory(lCompanyBean.getSalesCategory());
	            lAppEntityBean.setMsmeStatus(lCompanyBean.getMsmeStatus());
	            //
	            //find the chief promoter and set his category to appentitybean to use in factoring filter.
	            CompanyContactBean lChiefPromoterContactBean = new CompanyContactBean();
	            lChiefPromoterContactBean.setCdId(lCompanyBean.getId());
	            lChiefPromoterContactBean.setChiefPromoter(CommonAppConstants.Yes.Yes);
	            lChiefPromoterContactBean = companyContactDAO.findBean(lConnection, lChiefPromoterContactBean);
	            //
	            lAppEntityBean.setPromoterCategory((lChiefPromoterContactBean!=null?lChiefPromoterContactBean.getCpCat():null));
	            lAppEntityBean.setPan(lCompanyBean.getPan());
	            //            
	            // workflow
	            CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
	            lCompanyWorkFlowBean.setCdId(lCompanyBean.getId());
	            lCompanyWorkFlowBean.setApprovalStatus(lCompanyBean.getApprovalStatus());
	            lCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
	            lCompanyWorkFlowBean.setRecordCreator(pUserBean.getId());
	            
	            if (lOldAppEntityBean == null) {
	                lAppEntityBean.setRecordCreator(pUserBean.getId());
	                appEntityDAO.insert(lConnection, lAppEntityBean);
	                lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME,  lAppEntityBean);
	                lCompanyWorkFlowBean.setReason("Registration No : " + lCompanyBean.getRegistrationNo());
	            } else {
            		List<String> lFields = new ArrayList<String>();
            		if((lCompanyBean.getSalesCategory()==null && lOldAppEntityBean.getSalesCategory()!=null) ||
                			(lCompanyBean.getSalesCategory() != null && !lCompanyBean.getSalesCategory().equals(lOldAppEntityBean.getSalesCategory()))){
            			    lOldAppEntityBean.setSalesCategory(lCompanyBean.getSalesCategory());
        	        		lFields.add("salesCategory");
            		}
            		if((lCompanyBean.getMsmeStatus()==null && lAppEntityBean.getMsmeStatus()!=null) ||
                			(lCompanyBean.getMsmeStatus() != null && !lCompanyBean.getMsmeStatus().equals(lAppEntityBean.getMsmeStatus()))){
            				lOldAppEntityBean.setMsmeStatus(lCompanyBean.getMsmeStatus());
        	        		lFields.add("msmeStatus");
            		}
            		if((lCompanyBean.getPan()==null && lOldAppEntityBean.getPan()!=null) ||
                			(lCompanyBean.getPan() != null && !lCompanyBean.getPan().equals(lOldAppEntityBean.getPan()))){
            				lOldAppEntityBean.setPan(lCompanyBean.getPan());
        	        		lFields.add("pan");
            		}
            		//promoter category
            		
            		//checking whether company name is changed.
            		if(lCompNameFlag){
            			lOldAppEntityBean.setName(lCompanyBean.getCompanyName());
    	        		lFields.add("name");
            		}
            		if(lFields.size() > 0){
                		appEntityDAO.update(lConnection, lOldAppEntityBean, lFields );
                        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lOldAppEntityBean);
                        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lOldAppEntityBean);
            		}
            		
            		lCompanyWorkFlowBean.setReason("Updation");
	            }
	            companyWorkFlowDAO.insert(lConnection, lCompanyWorkFlowBean);
		   }
		   if (lCompanyBean!=null) {
			   //Contact
			   CompanyContactBO lCompanyContactBo = new CompanyContactBO();
			   CompanyContactBean lCCFilterBean = new CompanyContactBean();
			   lCCFilterBean.setCdId(lCompanyBean.getId());
			   List<CompanyContactBean> lCCList = lCompanyContactBo.findList(pExecutionContext, lCCFilterBean, null, pUserBean);
			   Map<Long, CompanyContactBean> lDBContactMap = new HashMap<Long, CompanyContactBean>();
			   if (lCCList!=null &&  !lCCList.isEmpty()) {
				   for (CompanyContactBean lBean : lCCList) {
					   if (!Objects.isNull(lBean.getRefId())) {
						   lDBContactMap.put(lBean.getRefId(), lBean); 
					   }
				   }
			   }
			   if (!lCompanyBean.getContacts().isEmpty()) {
				   for (CompanyContactBean lCCBean : lCompanyBean.getContacts()) {
					   if (!lDBContactMap.isEmpty() && lDBContactMap.containsKey(lCCBean.getRefId())){
						   lDBContactMap.remove(lCCBean.getRefId());
					   }
					   lCompanyContactBo.saveCompanyContact(pExecutionContext, pUserBean, lCCBean,lCompanyBean.getId());
				   }
			   }
			   if (!lDBContactMap.isEmpty()) {
				   for (CompanyContactBean lBeanToDelete : lDBContactMap.values()) {
						   lCompanyContactBo.delete(pExecutionContext, lBeanToDelete, pUserBean);
				   }
			   }
			   
			   //Location
			   CompanyLocationBO lCompanyLocationBo = new CompanyLocationBO();
			   CompanyLocationBean lCLFilterBean = new CompanyLocationBean();
			   lCLFilterBean.setCdId(lCompanyBean.getId());
			   List<CompanyLocationBean> lCLList = lCompanyLocationBo.findList(pExecutionContext, lCLFilterBean, null, pUserBean);
			   Map<Long, CompanyLocationBean> lDBLocationMap = new HashMap<Long, CompanyLocationBean>();
			   if (lCLList!=null && !lCLList.isEmpty()) {
				   for (CompanyLocationBean lBean : lCLList) {
					   if (!Objects.isNull(lBean.getRefId())) {
						   lDBLocationMap.put(lBean.getRefId(), lBean); 
					   }
				   }
			   }
			   if (!lCompanyBean.getLocations().isEmpty()) {
				   for (CompanyLocationBean lCLBean : lCompanyBean.getLocations()) {
					   if (!lDBLocationMap.isEmpty() && lDBLocationMap.containsKey(lCLBean.getRefId())){
						   lDBLocationMap.remove(lCLBean.getRefId());
					   }
					   lCompanyLocationBo.saveCompanyLocation(pExecutionContext, pUserBean, lCLBean ,lCompanyBean.getId());
				   }
				   
			   }
			   if (!lDBLocationMap.isEmpty()) {
				   for (CompanyLocationBean lBeanToDelete : lDBLocationMap.values()) {
						   lCompanyLocationBo.delete(pExecutionContext, lBeanToDelete, pUserBean);
				   }
			   }
			   //Bank
			   CompanyBankDetailBO lCompanyBankDetailBo = new CompanyBankDetailBO();
			   CompanyBankDetailBean lCBDFilterBean = new CompanyBankDetailBean();
			   lCBDFilterBean.setCdId(lCompanyBean.getId());
			   List<CompanyBankDetailBean> lCBDList = lCompanyBankDetailBo.findList(pExecutionContext, lCBDFilterBean, null, pUserBean);
			   Map<Long, CompanyBankDetailBean> lDBBankMap = new HashMap<Long, CompanyBankDetailBean>();
			   if (lCBDList!=null && ! lCBDList.isEmpty()) {
				   for (CompanyBankDetailBean lBean : lCBDList) {
					   if (!Objects.isNull(lBean.getRefId())) {
						   lDBBankMap.put(lBean.getRefId(), lBean); 
					   }
				   }
			   }
			   if (!lCompanyBean.getBankDetails().isEmpty()) {
				   boolean lDefaultBankFound = false;
				   for (CompanyBankDetailBean lCBBean : lCompanyBean.getBankDetails()) {
					   if (!lDBBankMap.isEmpty() && lDBBankMap.containsKey(lCBBean.getRefId())){
						   lDBBankMap.remove(lCBBean.getRefId());
					   }
					   if (!lDefaultBankFound && CommonAppConstants.Yes.Yes.equals(lCBBean.getDefaultAccount())) {
						   lDefaultBankFound = true;
					   }
					   lCompanyBankDetailBo.saveCompanyBank(pExecutionContext, pUserBean, lCBBean,lCompanyBean.getId());
				   }
				   if (!lDefaultBankFound) {
					   throw new CommonBusinessException("Atleast one default bank required. ");
				   }
				   
			   }
			   if (!lDBBankMap.isEmpty()) {
				   for (CompanyBankDetailBean lBeanToDelete : lDBBankMap.values()) {
					   		lCompanyBankDetailBo.delete(pExecutionContext, lBeanToDelete, pUserBean);
				   }
			   }
			   //document
			   if (!lCompanyBean.getDocuments().isEmpty()) {
				   Long lContactRefid = null;
					if(pMap.containsKey("contactRefId") && pMap.get("contactRefId")!=null) {
						lContactRefid = Long.valueOf(pMap.get("contactRefId").toString());
					}
				   CompanyKYCDocumentBO lCompanyKYCDocumentBo = new CompanyKYCDocumentBO();
				   for (CompanyKYCDocumentBean lCKycBean : lCompanyBean.getDocuments()) {
					  lCompanyKYCDocumentBo.saveCompanyDocument(pExecutionContext, pUserBean, lCKycBean,lCompanyBean.getId(),lContactRefid);
				   }
				   
			   }
			   //share entity
			   CompanyShareEntityBO lCompanyShareEntityBo = new CompanyShareEntityBO();
			   CompanyShareEntityBean lCSEFilterBean = new CompanyShareEntityBean();
			   lCSEFilterBean.setCdId(lCompanyBean.getId());
			   List<CompanyShareEntityBean> lCSEList = lCompanyShareEntityBo.findList(pExecutionContext, lCSEFilterBean, null, pUserBean);
			   Map<Long, CompanyShareEntityBean> lDBSEMap = new HashMap<Long, CompanyShareEntityBean>();
			   if (lCSEList!=null && ! lCSEList.isEmpty()) {
				   for (CompanyShareEntityBean lBean : lCSEList) {
					   if (!Objects.isNull(lBean.getRefId())) {
						   lDBSEMap.put(lBean.getRefId(), lBean); 
					   }
				   }
			   }
			   if (!lCompanyBean.getShareEntities().isEmpty()) {
				   for (CompanyShareEntityBean lCSEBean : lCompanyBean.getShareEntities()) {
					   if (!lDBSEMap.isEmpty() && lDBSEMap.containsKey(lCSEBean.getRefId())){
						   lDBSEMap.remove(lCSEBean.getRefId());
					   }
					   lCompanyShareEntityBo.saveCompanyShareEntity(pExecutionContext, pUserBean, lCSEBean,lCompanyBean.getId());
				   }
				   
			   }
			   if (!lDBSEMap.isEmpty()) {
				   for (CompanyShareEntityBean lBeanToDelete : lDBSEMap.values()) {
						   lCompanyShareEntityBo.delete(pExecutionContext, lBeanToDelete, pUserBean);
				   }
			   }
			   //share individual
			   CompanyShareIndividualBO lCompanyShareIndividualBo = new CompanyShareIndividualBO();
			   CompanyShareIndividualBean lCSIFilterBean = new CompanyShareIndividualBean();
			   lCSIFilterBean.setCdId(lCompanyBean.getId());
			   List<CompanyShareIndividualBean> lCSIList = lCompanyShareIndividualBo.findList(pExecutionContext, lCSIFilterBean, null, pUserBean);
			   Map<Long, CompanyShareIndividualBean> lDBSIMap = new HashMap<Long, CompanyShareIndividualBean>();
			   if (lCSIList!=null && ! lCSIList.isEmpty()) {
				   for (CompanyShareIndividualBean lBean : lCSIList) {
					   if (!Objects.isNull(lBean.getRefId())) {
						   lDBSIMap.put(lBean.getRefId(), lBean); 
					   }
				   }
			   }
			   if (!lCompanyBean.getShareIndividuals().isEmpty()) {
				   for (CompanyShareIndividualBean lCSIBean : lCompanyBean.getShareIndividuals()) {
					   if (!lDBSIMap.isEmpty() && lDBSIMap.containsKey(lCSIBean.getRefId())){
						   lDBSIMap.remove(lCSIBean.getRefId());
					   }
					   lCompanyShareIndividualBo.saveCompanyShareIndividual(pExecutionContext, pUserBean, lCSIBean,lCompanyBean.getId());
				   }
				   
			   }
			   if (!lDBSIMap.isEmpty()) {
				   for (CompanyShareIndividualBean lBeanToDelete : lDBSIMap.values()) {
						   lCompanyShareIndividualBo.delete(pExecutionContext, lBeanToDelete, pUserBean);
				   }
			   }
		   }
		   if(lCreateCharge) {
	           //TODO: create registration charge
			   try {
				   RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
				   lRegistrationChargeBO.createCharge(lConnection, lCompanyBean.getCode(), pUserBean, RegistrationChargeBean.ChargeType.Registration, new java.sql.Date(lCompanyBean.getRecordCreateTime().getTime()));
			   }catch(Exception lEx) {
				   throw new CommonBusinessException("Error while creating charge.");
			   }
		   }
		   if (lApprovalFlag) {
			   Map <String,String> lPasswordMap = new HashMap<>();
			   lCompanyBean.setIsProvisional(false);
			   AppUserBean lAppUserBean = createAdminUser(lConnection, lCompanyBean, lMemoryDBConnection, pUserBean, lPasswordMap);
			   sendAdminUserCreationEmail(lPasswordMap, lAppUserBean);
		   }
		   pExecutionContext.commit();
		   Map <String,String> lMap = new HashMap<>();
		   lMap.put("message", "Success");
		   return new JsonBuilder(lMap).toString();
//		   Map<String,Object> lDataMap = validateAndSubmit(pExecutionContext, lCompanyBean.getId(), pUserBean, false);
//		   if ((boolean)lDataMap.get("valid")) {
//			   if (lApprovalFlag) {
//				   Map <String,String> lPasswordMap = new HashMap<>();
//				   AppUserBean lAppUserBean = createAdminUser(lConnection, lCompanyBean, lMemoryDBConnection, pUserBean, lPasswordMap);
//				   sendAdminUserCreationEmail(lPasswordMap, lAppUserBean);
//			   }
//			   pExecutionContext.commit();
//			   Map <String,String> lMap = new HashMap<>();
//			   lMap.put("message", "Success");
//			   return new JsonBuilder(lMap).toString();
//		   }else {
//			   lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code,lAppEntityBean );
//			   pExecutionContext.rollback();
//			   throw new CommonBusinessException(new JsonBuilder(lDataMap.get("messages")).toString());
//		   }
		
	}
	
	private void validateAPIData(CompanyDetailBean pCompanyDetailBean, Map<String, Object> pMap, List<Map<String, Object>> lMessages) throws Exception {
		   List<Map<String, Object>> lContactsListForValidation = new ArrayList<Map<String,Object>>();
		   List<Map<String, Object>> lUboList = new ArrayList<Map<String,Object>>();
		   List<Map<String, Object>> lContactList = (pMap.containsKey("contacts")?(List<Map<String, Object>>)pMap.get("contacts"):null);
		   for (Map<String, Object> lContactData : lContactList) {
			   if ( lContactData.containsKey("ultimateBeneficiary") && StringUtils.isNotBlank((String)lContactData.get("ultimateBeneficiary"))){
				   lUboList.add(lContactData);
			   }else {
				   lContactsListForValidation.add(lContactData);
			   }
		   }
		   pMap.put("contacts", lContactsListForValidation);
		   List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(pCompanyDetailBean, pMap, CompanyDetailBean.FIELDGROUP_UPDATECOMPANY, null);
		   if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
			  for (ValidationFailBean lValidationFailBean : lValidationFailBeans) {
				  if(!"id".equalsIgnoreCase(lValidationFailBean.getName())) {
				      appendMessage(lMessages, lValidationFailBean.getName(),lValidationFailBean.getMessage());
				  }
			  }
			  if(!lMessages.isEmpty()){
			       throw new CommonBusinessException( new JsonBuilder(lMessages).toString());
			  }
		   }
		   List<CompanyContactBean> lCCList = pCompanyDetailBean.getContacts();
		   for(Map<String,Object> lMap : lUboList ) {
			   CompanyContactBean lCContactBean = new CompanyContactBean();
			   companyContactDAO.getBeanMeta().validateAndParse(lCContactBean, lMap,null, null);
			   lCContactBean.setResidentailStatus(ResidentailStatus.Residential_Indian);
			   lCContactBean.setNationality(Nationality.Indian);
			   lCCList.add(lCContactBean);
		   }
		   
	}

	public static void appendMessage(List<Map<String, Object>> pMessages, String pAction, String pRemarks) {
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("field", pAction);
        lMap.put("error", pRemarks);
        if(pMessages!=null)
        	pMessages.add(lMap);
    }

	public String getSettings(ExecutionContext pExecutionContext, AppUserBean lLoggedInUserBean, String pId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		CompanyDetailBean lCompanyDetailBean = TredsHelper.getInstance().getCompanyDetails(lConnection, Long.valueOf(pId), false);
		CompanyLocationBean lCLFilterBean = new CompanyLocationBean();
		List lCDFields = Arrays.asList(new String[]{"id","enableLocationwiseSettlement","cashDiscountPercent"});
		List lLocationFields = Arrays.asList(new String[]{"id","name","enableSettlement","cbdId","settlementCLId"});
		lCLFilterBean.setCdId(Long.valueOf(pId));
		Map<String,Object> lMap = companyDetailDAO.getBeanMeta().formatAsMap(lCompanyDetailBean, null, lCDFields, false);
		List<CompanyLocationBean> lCompanyLocationList = companyLocationDAO.findList(lConnection, lCLFilterBean, lLocationFields, -1);
		List<Object> lLocationMapList = new ArrayList<Object>();
		for (CompanyLocationBean lClBean : lCompanyLocationList) {
			lLocationMapList.add(companyLocationDAO.getBeanMeta().formatAsMap(lClBean, null, lLocationFields, false,false));
		}
		lMap.put("locations", lLocationMapList);
		return new JsonBuilder(lMap).toString();
	}
	
	public AppUserBean createAdminUser(Connection pConnection,CompanyDetailBean pCompanyDetailBean, MemoryDBConnection pMemoryDBConnection, IAppUserBean pUserBean, Map<String, String> pPasswordMap) throws Exception {
		// create admin user
        CompanyContactBean lCompanyContactBean = new CompanyContactBean();
        lCompanyContactBean.setCdId(pCompanyDetailBean.getId());
        lCompanyContactBean.setAdmin(CommonAppConstants.Yes.Yes);
        GenericDAO<CompanyContactBean> lContactDAO = null;
        if (pCompanyDetailBean.getIsProvisional()) {
        	lContactDAO = companyContactProvDAO;
        }else {
        	lContactDAO = companyContactDAO;
        }
        lCompanyContactBean = lContactDAO.findBean(pConnection, lCompanyContactBean);
        if (lCompanyContactBean == null)
            throw new CommonBusinessException("Authorised Official not found");
        AppUserBean lAppUserBean = new AppUserBean();
        lAppUserBean.setDomain(pCompanyDetailBean.getCode());
        lAppUserBean.setLoginId(AppConstants.LOGINID_ADMIN);
        AppUserBean lOldAppUserBean = appUserDAO.findBean(pConnection, lAppUserBean);
        String lPassword = RegistryHelper.getInstance().getString(AppConstants.REGISTRY_DEFAULTPASSWORD);
        if (StringUtils.isBlank(lPassword)) lPassword = "Treds@123";
        lAppUserBean.setPassword1(CommonUtilities.encryptSHA(lPassword));
        //
        pPasswordMap.put("password", lPassword);
        //
        lAppUserBean.setForcePasswordChange(CommonAppConstants.YesNo.Yes);
        lAppUserBean.setStatus(Status.Active);
        lAppUserBean.setType(AppUserBean.Type.Admin);
        lAppUserBean.setSalutation(lCompanyContactBean.getSalutation());
        lAppUserBean.setFirstName(lCompanyContactBean.getFirstName());
        lAppUserBean.setMiddleName(lCompanyContactBean.getMiddleName());
        lAppUserBean.setLastName(lCompanyContactBean.getLastName());
        lAppUserBean.setEmail(lCompanyContactBean.getEmail());
        lAppUserBean.setMobile(lCompanyContactBean.getMobile());
        lAppUserBean.setTelephone(lCompanyContactBean.getTelephone());
        lAppUserBean.setEnable2FA(CommonAppConstants.YesNo.No);
        List<Long> lRoles = new ArrayList<Long>();
        lRoles.add(AppConstants.ADMIN_ROLE_ID);
        lAppUserBean.setRmIdList(lRoles);
        if (lOldAppUserBean == null) {
            lAppUserBean.setRecordCreator(pUserBean.getId());
            appUserDAO.insert(pConnection, lAppUserBean);
        } else {
            lAppUserBean.setId(lOldAppUserBean.getId());
            lAppUserBean.setRecordUpdator(pUserBean.getId());
            lAppUserBean.setRecordVersion(lOldAppUserBean.getRecordVersion());
            appUserDAO.update(pConnection, lAppUserBean, BeanMeta.FIELDGROUP_UPDATE);
            pMemoryDBConnection.deleteRow(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Domain_LoginId, lAppUserBean);
        }
        pMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, lAppUserBean);
        return lAppUserBean;
	}

	public String getSaveSettings(ExecutionContext pExecutionContext, AppUserBean lLoggedInUserBean, String pMessage) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		List lCDFields = Arrays.asList(new String[]{"id","enableLocationwiseSettlement","cashDiscountPercent"});
		CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(lCompanyDetailBean, 
            pMessage, false ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if (Objects.isNull(lCompanyDetailBean.getId())) {
        	
        }
        boolean lUpdateCompanyDetails = false;
        CompanyDetailBean lOldCompanyDetailBean = companyDetailDAO.findByPrimaryKey(lConnection , lCompanyDetailBean);
        if (!companyDetailDAO.getBeanMeta().equalBean(lCompanyDetailBean, lOldCompanyDetailBean, null, lCDFields)) {
        	lCompanyDetailBean.setRecordVersion(lOldCompanyDetailBean.getRecordVersion());
        	companyDetailDAO.update(lConnection, lCompanyDetailBean, lCDFields);
		}
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        Map<String,Object> lAllLocationsHash = new HashMap<String, Object>(); 
        for (String lKey: lMap.keySet()) {
        	String[] lKeyArr = lKey.split("_");
        	if(lKeyArr.length==2) {
        		if(!lAllLocationsHash.containsKey(lKeyArr[1])){
        			lAllLocationsHash.put(lKeyArr[1], new HashMap<String, Object>());
        		}
        		Map<String,Object> lLocationMap = (Map<String, Object>) lAllLocationsHash.get(lKeyArr[1]);
        		lLocationMap.put(lKeyArr[0], lMap.get(lKey));
        		if(!lLocationMap.containsKey("id")){
        			lLocationMap.put("id", Long.valueOf(lKeyArr[1]));
        		}
        	}
        }
        CompanyLocationBO lCompanyLocationBO = new CompanyLocationBO();
        lCompanyLocationBO.saveTransactionalData(lConnection,lAllLocationsHash);
		return null;
	}

	public void updateCompanyName(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		try{
			CompanyDetailBean lFilterBean = new CompanyDetailBean();
			if(pMap.get("id") != null){
				lFilterBean.setId(Long.valueOf(pMap.get("id").toString()));
			}
			if(StringUtils.isNotBlank(pMap.get("code").toString())){
				lFilterBean.setCode(pMap.get("code").toString());
			}
			CompanyDetailBean lCDProvBean = companyDetailProvDAO.findBean(lConnection, lFilterBean);
			CompanyDetailBean lCBean = companyDetailDAO.findBean(lConnection, lFilterBean);
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pMap.get("code").toString());
		    MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
		    if(lCDProvBean != null){
				if(lCDProvBean.getCompanyName().equals(pMap.get("companyName").toString())){
					throw new CommonBusinessException(" Please change the company name. ");
				}
				lCDProvBean.setCompanyName(pMap.get("companyName").toString());
				companyDetailProvDAO.update(lConnection, lCDProvBean, FIELDGROUP_UPDATECOMPANYNAME);
				if(lAppEntityBean != null){
					lAppEntityBean.setName(pMap.get("companyName").toString());
				}
				if(lCBean!=null){
					lCBean.setCompanyName(pMap.get("companyName").toString());
				}
				if(lCBean != null && lAppEntityBean != null){
					if(CompanyApprovalStatus.Approved.equals(lCDProvBean.getApprovalStatus())){
						companyDetailDAO.update(lConnection, lCBean , FIELDGROUP_UPDATECOMPANYNAME);
						companyDetailDAO.insertAudit(lConnection, lCBean, AuditAction.Update, pUserBean.getId());
						appEntityDAO.update(lConnection, lAppEntityBean, FIELDGROUP_UPDATEAPPENTITYNAME);
					    appEntityDAO.insertAudit(lConnection, lAppEntityBean, AuditAction.Update, pUserBean.getId());
				        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
				        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
					}
				}
		    }
            CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
            lCompanyWorkFlowBean.setCdId(lCDProvBean.getId());
            lCompanyWorkFlowBean.setApprovalStatus(lCDProvBean.getApprovalStatus());
            lCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
            lCompanyWorkFlowBean.setReason(" Company Name Changed. ");
            lCompanyWorkFlowBean.setRecordCreator(pUserBean.getId());
            companyWorkFlowDAO.insert(lConnection, lCompanyWorkFlowBean);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	private boolean hasProvisionalData(Connection pConnection, Long pCompanyId) {
		if(pCompanyId!=null) {
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT SUM(MyCount) MyTotalCount ");
			lSql.append(" FROM ( ");
			lSql.append(" SELECT COUNT(*) MYCOUNT FROM COMPANYBANKDETAILS_P WHERE CBDRECORDVERSION > 0 AND CBDCDID = ").append(pCompanyId);
			lSql.append(" UNION SELECT COUNT(*) MYCOUNT FROM COMPANYCONTACTS_P WHERE CCRECORDVERSION > 0 AND CCCDID = ").append(pCompanyId);
			lSql.append(" UNION SELECT COUNT(*) MYCOUNT FROM COMPANYKYCDOCUMENTS_P WHERE CKDRECORDVERSION > 0 AND CKDCDID = ").append(pCompanyId);
			lSql.append(" UNION SELECT COUNT(*) MYCOUNT FROM COMPANYLOCATIONS_P WHERE CLRECORDVERSION > 0 AND CLCDID = ").append(pCompanyId);
			lSql.append(" UNION SELECT COUNT(*) MYCOUNT FROM COMPANYSHAREENTITY_P WHERE CSERECORDVERSION > 0 AND CSECDID = ").append(pCompanyId);
			lSql.append(" UNION SELECT COUNT(*) MYCOUNT FROM COMPANYSHAREINDIVIDUAL_P WHERE CSIRECORDVERSION > 0 AND CSICDID = ").append(pCompanyId);
			lSql.append(" ) ");
        	try{
        		Statement lStatement = pConnection.createStatement();
        		ResultSet lResultSet = lStatement.executeQuery(lSql.toString());
        		if (lResultSet.next()) {
        			Long lCount = lResultSet.getLong("MyTotalCount");
        			if(lCount!=null&&lCount.longValue() > 0) {
        				return true;
        			}
                }
        	}catch(Exception e){
        		logger.info("error in hasProvisionalData : "+e.getMessage());
        	}
		}
		return false;
	}
	
	private boolean copyDataToProvisional(Connection pConnection, Long pCompanyId) throws Exception {
		if(pCompanyId!=null) {
			//insert into new_table ( select * from old_table);
			Map<String,String> lSqls = new HashMap<String,String>();//key=TableName value=Sql
			//
			lSqls.put("BankDetails", "INSERT INTO COMPANYBANKDETAILS_P ( SELECT * FROM COMPANYBANKDETAILS WHERE CBDRECORDVERSION > 0 AND CBDCDID = "+pCompanyId.toString() + " ) ");
			lSqls.put("Contacts", "INSERT INTO COMPANYCONTACTS_P ( SELECT * FROM COMPANYCONTACTS WHERE CCRECORDVERSION > 0 AND CCCDID = "+pCompanyId.toString() + " ) ");
			lSqls.put("KycDocuments","INSERT INTO COMPANYKYCDOCUMENTS_P ( SELECT * FROM COMPANYKYCDOCUMENTS WHERE CKDRECORDVERSION > 0 AND CKDCDID = "+pCompanyId.toString() + " ) ");
			lSqls.put("Locations","INSERT INTO COMPANYLOCATIONS_P ( SELECT * FROM COMPANYLOCATIONS WHERE CLRECORDVERSION > 0 AND CLCDID = "+pCompanyId.toString() + " ) ");
			lSqls.put("ShareEntity","INSERT INTO COMPANYSHAREENTITY_P ( SELECT * FROM COMPANYSHAREENTITY WHERE CSERECORDVERSION > 0 AND CSECDID = "+pCompanyId.toString() + " ) ");
			lSqls.put("ShareIndividual","INSERT INTO COMPANYSHAREINDIVIDUAL_P ( SELECT * FROM COMPANYSHAREINDIVIDUAL WHERE CSIRECORDVERSION > 0 AND CSICDID = "+pCompanyId.toString() + " ) ");
    		String lSql = "", lName="";
        	try{
        		for(String lKey : lSqls.keySet()) {
    				lName = lKey;
    				lSql = lSqls.get(lName);
            		Statement lStatement = pConnection.createStatement();
            		int lCount = lStatement.executeUpdate(lSql.toString());
            		logger.info("Transfered to provisional table "+lName+" "+lCount+ " records.");
        		}
        	}catch(Exception e){
        		logger.info("error in copyDataToProvisional : "+lName);
        		logger.info("error in copyDataToProvisional : "+lSql);
        		logger.info("error in copyDataToProvisional : "+e.getMessage());
        		throw new CommonBusinessException("Error while copying data to provisional data of "+lName);
        	}
}
		return false;
	}
	
	
	public void startModification(ExecutionContext pExecutionContext, CompanyDetailBean pFilterBean, IAppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		lConnection.setAutoCommit(false);
		if (!hasProvisionalData(lConnection, pFilterBean.getId())) {
			copyDataToProvisional(lConnection, pFilterBean.getId());
		}
		CompanyDetailBean lCDBean = companyDetailDAO.findBean(lConnection, pFilterBean);
		CompanyDetailBean lCDProvBean = companyDetailProvDAO.findBean(lConnection, pFilterBean);
		lCDBean.setApprovalStatus(CompanyApprovalStatus.ApprovalModification);
		lCDBean.setRecordUpdator(pUserBean.getId());
		lCDProvBean.setApprovalStatus(CompanyApprovalStatus.ApprovalModification);
		lCDProvBean.setRecordUpdator(pUserBean.getId());
		companyDetailDAO.update(lConnection, lCDBean , FIELDGROUP_STARTMODIFICATION);
		companyDetailProvDAO.update(lConnection, lCDProvBean, FIELDGROUP_STARTMODIFICATION);
		companyDetailDAO.insertAudit(lConnection, lCDBean, AuditAction.Update, pUserBean.getId());
		CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
        lCompanyWorkFlowBean.setCdId(lCDBean.getId());
        lCompanyWorkFlowBean.setApprovalStatus(lCDBean.getApprovalStatus());
        lCompanyWorkFlowBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
        lCompanyWorkFlowBean.setReason(" Modification started by login :  "+pUserBean.getLoginId());
        lCompanyWorkFlowBean.setRecordCreator(pUserBean.getId());
        companyWorkFlowDAO.insert(lConnection, lCompanyWorkFlowBean);
        lConnection.commit();
	}

	public void changeAppModStatus(ExecutionContext pExecutionContext, CompanyDetailBean pFilterBean,
			AppUserBean pUserBean) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		CompanyDetailBean lCDBean = companyDetailDAO.findBean(lConnection, pFilterBean);
		CompanyDetailBean lCDProvBean = companyDetailProvDAO.findBean(lConnection, pFilterBean);
		if(lCDProvBean != null){
			if(CompanyApprovalStatus.ApprovalModification.equals(lCDProvBean.getApprovalStatus())){
				lCDBean.setApprovalStatus(CompanyApprovalStatus.ApprovalModificationSubmit);
				lCDBean.setRecordUpdator(pUserBean.getId());
				lCDProvBean.setApprovalStatus(CompanyApprovalStatus.ApprovalModificationSubmit);
				lCDProvBean.setRecordUpdator(pUserBean.getId());
				companyDetailDAO.update(lConnection, lCDBean , FIELDGROUP_STARTMODIFICATION);
				companyDetailProvDAO.update(lConnection, lCDProvBean, FIELDGROUP_STARTMODIFICATION);
				companyDetailDAO.insertAudit(lConnection, lCDBean, AuditAction.Update, pUserBean.getId());
			}else{
				throw new CommonBusinessException("Company Not found.");
			}
		}else{
			throw new CommonBusinessException("Company Not found.");
		}
		
	}
}
