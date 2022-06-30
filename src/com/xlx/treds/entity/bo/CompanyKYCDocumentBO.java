package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyKYCDocumentBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.master.bean.KYCDocumentMasterBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyKYCDocumentBO {
    public static final String TABLENAME_PROV = "CompanyKYCDocuments_P";
    public static final String TABLENAME_CONTACTS_PROV = "CompanyContacts_P";
    public static final String TABLENAME_LOCATIONS_PROV = "CompanyLocations_P";
    public static final String TABLENAME_SHAREENTITY_PROV = "CompanyShareEntity_P";
    public static final String TABLENAME_SHAREINDIVIDUAL_PROV = "CompanyShareIndividual_P";
    public static final String TABLENAME_BANKDETAILS_PROV = "CompanyBankDetails_P";
    //
    private GenericDAO<CompanyKYCDocumentBean> companyKYCDocumentDAO;
    private GenericDAO<CompanyKYCDocumentBean> companyKYCDocumentProvDAO;
    private BeanMeta companyKYCDocumentBeanMeta;
    private GenericDAO<CompanyContactBean> companyContactDAO;
    private GenericDAO<CompanyContactBean> companyContactProvDAO;
    private GenericDAO<CompanyShareIndividualBean> companyShareIndividualDAO;
    private GenericDAO<CompanyShareIndividualBean> companyShareIndividualProvDAO;
    private GenericDAO<CompanyShareEntityBean> companyShareEntityDAO;
    private GenericDAO<CompanyShareEntityBean> companyShareEntityProvDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankProvDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<CompanyLocationBean> companyLocationProvDAO;
    //
    public static Logger logger = Logger.getLogger(CompanyKYCDocumentBO.class);

    public CompanyKYCDocumentBO() {
        super();
        companyKYCDocumentDAO = new GenericDAO<CompanyKYCDocumentBean>(CompanyKYCDocumentBean.class);
        companyKYCDocumentProvDAO = new GenericDAO<CompanyKYCDocumentBean>(CompanyKYCDocumentBean.class,TABLENAME_PROV);
        companyKYCDocumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyKYCDocumentBean.class);
        companyContactDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
        companyContactProvDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class,TABLENAME_CONTACTS_PROV);
        companyShareIndividualDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class);
        companyShareIndividualProvDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class,TABLENAME_SHAREINDIVIDUAL_PROV);
        companyShareEntityDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class);
        companyShareEntityProvDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class,TABLENAME_SHAREENTITY_PROV);
        companyBankDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        companyBankProvDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class,TABLENAME_BANKDETAILS_PROV);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        companyLocationProvDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class,TABLENAME_LOCATIONS_PROV);
    }
    
    private GenericDAO<CompanyKYCDocumentBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyKYCDocumentProvDAO;
    	}
    	return companyKYCDocumentDAO;
    }
    private GenericDAO<CompanyContactBean> getContactDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyContactProvDAO;
    	}
    	return companyContactDAO;
    }
    private GenericDAO<CompanyShareIndividualBean> getShareIndividualDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyShareIndividualProvDAO;
    	}
    	return companyShareIndividualDAO;
    }
    private GenericDAO<CompanyShareEntityBean> getShareEntityDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyShareEntityProvDAO;
    	}
    	return companyShareEntityDAO;
    }
    private GenericDAO<CompanyBankDetailBean> getBankDetailDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyBankProvDAO;
    	}
    	return companyBankDAO;
    }
    private GenericDAO<CompanyLocationBean> getLocationDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyLocationProvDAO;
    	}
    	return companyLocationDAO;
    }

    public CompanyKYCDocumentBean findBean(ExecutionContext pExecutionContext, 
        CompanyKYCDocumentBean pFilterBean) throws Exception {
        CompanyKYCDocumentBean lCompanyKYCDocumentBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCompanyKYCDocumentBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lCompanyKYCDocumentBean.getCdId(), pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyKYCDocumentBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        if(pFilterBean.getIsProvisional()) {
        	CompanyKYCDocumentBean lCKYCActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lCKYCActualBean!=null) {
            	lCKYCActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                //
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lCKYCActualBean,lCompanyKYCDocumentBean);
                lCompanyKYCDocumentBean.setModifiedData(lDiffData);
                return lCompanyKYCDocumentBean;
            }
        }
        return lCompanyKYCDocumentBean;
    }
    
    public Map<String, Object> findList(ExecutionContext pExecutionContext, CompanyDetailBean pCompanyDetailBean, 
    		IAppUserBean pAppUserBean) throws Exception {
        CompanyKYCDocumentBean lFilterCompanyKYCDocumentBean = new CompanyKYCDocumentBean();
        CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        if(AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain())){
        	if(pCompanyDetailBean.getId()==null)
        		throw new CommonBusinessException("Entity required.");
            lFilterCompanyKYCDocumentBean.setCdId(pCompanyDetailBean.getId());
    	}else if(AppConstants.DOMAIN_REGENTITY.equals(pAppUserBean.getDomain())){
    		if(pCompanyDetailBean.getId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
            lFilterCompanyKYCDocumentBean.setCdId(pCompanyDetailBean.getId());
            TredsHelper.getInstance().checkRegistringEntityAccess(pCompanyDetailBean.getId(), pAppUserBean.getId());
        }else{
        	if(pCompanyDetailBean.getId()==null){
        		lFilterCompanyKYCDocumentBean.setCdId(TredsHelper.getInstance().getCompanyId(pAppUserBean));
        	}
        	else
        		lFilterCompanyKYCDocumentBean.setCdId(pCompanyDetailBean.getId());
        }
        lCompanyDetailBean.setId(lFilterCompanyKYCDocumentBean.getCdId());
        lCompanyDetailBean.setIsProvisional(true);//this is used only to get constitution and type
        CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        lCompanyDetailBean = lCompanyDetailBO.findBean(pExecutionContext, lCompanyDetailBean , pAppUserBean, false);
        //
        boolean lProvFlag = pCompanyDetailBean.getIsProvisional();
        //
        List<CompanyKYCDocumentBean> lCompanyKYCDocumentList = getDAO(lProvFlag).findList(pExecutionContext.getConnection(), lFilterCompanyKYCDocumentBean, (String)null);
        Map<String, Object> lResponse = new HashMap<String, Object>();
        List<Map<String, Object>> lDocumentTypeList = OtherResourceCache.getInstance().getKycDocument(lCompanyDetailBean.getConstitution(), lCompanyDetailBean.getType());
        if(lDocumentTypeList == null)lDocumentTypeList = new ArrayList();
        Map<String,List<Map<String,Object>>> lResults = new HashMap<String, List<Map<String,Object>>>();
        String lKey = null;
        // Hashing all the documents and keeping the first element in list as the key for that item
        Map<String, String> lDatasMetaKeys = new HashMap<String, String>(); // Key=Category^DocType^ActualDoc , Value=Category^DocType^DocumentList[0]  
        Object lDoc = null; ArrayList lDocList=null;
        String lDataDocKey, lDataDocValue, lDocCatType;
        for(Map<String, Object> lDocType : lDocumentTypeList){
        	lDocCatType = (String) lDocType.get("docCat"); //category
        	lDocCatType += "^" + (String) lDocType.get("documentType"); //documenttype
        	lDocList = (ArrayList) lDocType.get("documentList");
        	lDataDocValue = lDocCatType + "^" + ((Map)lDocList.get(0)).get("value"); //documentLists first item
        	//
        	for(int lDocPtr=0; lDocPtr < lDocList.size(); lDocPtr++){
        		lDataDocKey = lDocCatType + "^" + ((Map)lDocList.get(lDocPtr)).get("value");
        		lDatasMetaKeys.put(lDataDocKey, lDataDocValue);
        	}
        }
        
        String lActualKey = null;
        for (CompanyKYCDocumentBean lCompanyKYCDocumentBean : lCompanyKYCDocumentList) {
        	lCompanyKYCDocumentBean.setIsProvisional(pCompanyDetailBean.getIsProvisional());
            Map<String, Object> lDocumentData = companyKYCDocumentBeanMeta.formatAsMap(lCompanyKYCDocumentBean, null, null, true, true);
            RefCodeValuesBean lDocumentBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lCompanyKYCDocumentBean.getDocument(),
                    OtherResourceCache.REFCODE_DOCUMENT);
            lDocumentData.put("documentDesc", lDocumentBean.getDesc());
            
            lActualKey = lCompanyKYCDocumentBean.getDocumentCat() + CommonConstants.KEY_SEPARATOR + lCompanyKYCDocumentBean.getDocumentType() + CommonConstants.KEY_SEPARATOR + lCompanyKYCDocumentBean.getDocument();
            //form the meta make the metaKey and send, metaKey will be Category^DocumentType^DocumentList[0] - always the first item will be the key
            lKey = lDatasMetaKeys.get(lActualKey);
            if(CommonUtilities.hasValue(lKey)){
                if(lCompanyKYCDocumentBean.getDocForCCId()!=null){
                	lKey+=(CommonConstants.KEY_SEPARATOR +lCompanyKYCDocumentBean.getDocForCCId());
                }
                //
                List<Map<String,Object>> lList = new ArrayList<Map<String,Object>>();
                lList.add(lDocumentData);
                lResults.put(lKey, lList);
            }else{
            	logger.info("KYC Document :- Meta key not found for data key " +lActualKey +", Document id :-"+lCompanyKYCDocumentBean.getId());
            }
        }
        lResponse.put("data", lResults);
        lResponse.put("meta", lDocumentTypeList);
        if (lCompanyDetailBean!=null && lCompanyDetailBean.getCreatorIdentity()!=null) {
        	lResponse.put("creatorIdentity", lCompanyDetailBean.getCreatorIdentity());
        	lResponse.put("documentsUrl", lCompanyDetailBean.getDocumentsUrl());
        }
        //
        //FOR OVERRIDING THE DEFAULT SETTINGS OF KYC DOCUMENTS
        //create a new structre and pass it on
        //validation - send ST = 0/1 ie. DocumentCode = Min/Max
        Map<String, Map<String,Object>> lUpdateList = new HashMap<String, Map<String,Object>>();
        Map<String, Object> lUpdate = null;
        if(CommonAppConstants.YesNo.Yes.equals(pCompanyDetailBean.getPurchaserFlag()) || 
        		CommonAppConstants.YesNo.Yes.equals(pCompanyDetailBean.getSupplierFlag()))
        {
            //condition1 : (Buyer+Service)/(Seller+Service) = Upload Service Tax Reg. No Mandatory 
            //             Default: Mandatory for Buyer/Seller
        	if(AppConstants.RC_SECTOR_SERVICE.equals(pCompanyDetailBean.getSector()))
        	{
        		//change the mandatory to non mandatory by 
        		lUpdate = new HashMap<String, Object>();
        		lUpdate.put(OtherResourceCache.MIN_COUNT, new Long(0));
        		lUpdateList.put("VAT",lUpdate);
        	}
        }
        lResponse.put("update", lUpdateList);
        //
        //
        List<CompanyContactBean>  lContactList = null;
        Map<String,List<Object>> lContactTypewiseNames = new HashMap<String, List<Object>>();
        Map<String,Object> lContactName = null;
        List<Object> lContactNameList = null;
        CompanyContactBean lCCFilterBean = new CompanyContactBean();
        TredsHelper lTredsHelper = TredsHelper.getInstance();
        String[] lCodes = new String[] { KYCDocumentMasterBean.RepeatType.Promoter.getCode(), KYCDocumentMasterBean.RepeatType.Authorized_Person.getCode(), KYCDocumentMasterBean.RepeatType.Administrator.getCode() , KYCDocumentMasterBean.RepeatType.Ultimate_Benificiery.getCode()};
        Object[] lFilters = new Object[] { new Yes[] {Yes.Yes,null,null,null}, new Yes[] {null, Yes.Yes,null,null}, new Yes[] {null,null, Yes.Yes,null},new Yes[] {null,null,null,Yes.Yes}};
        //
    	if(AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain())){
    		//TODO: ??
    	}
    	//
		lCCFilterBean.setCdId(lFilterCompanyKYCDocumentBean.getCdId());
    	for(int lPtr=0; lPtr<lCodes.length; lPtr++){
        	lCCFilterBean.setPromoter(((Yes[]) lFilters[lPtr])[0]);
        	lCCFilterBean.setAuthPer(((Yes[]) lFilters[lPtr])[1]);
        	lCCFilterBean.setAdmin(((Yes[]) lFilters[lPtr])[2]);
        	lCCFilterBean.setUltimateBeneficiary(((Yes[]) lFilters[lPtr])[3]);
        	
        	lContactList =  getContactDAO(lProvFlag).findList(pExecutionContext.getConnection(), lCCFilterBean, Arrays.asList(new String[] { "id","salutation","firstName","middleName","lastName"}));
    		lContactNameList = new ArrayList<Object>();
        	if(lContactList!=null&&lContactList.size() > 0){
        		for(CompanyContactBean lCCBean : lContactList){
                	lContactName = new HashMap<String, Object>();
        			lContactName.put("ccId", lCCBean.getId());
        			lContactName.put("ccDisp" ,lTredsHelper.getContactFullName(lCCBean));
        			lContactNameList.add(lContactName);
        		}
        	}
        	lContactTypewiseNames.put(lCodes[lPtr], lContactNameList);
    	}
        //        
        lResponse.put("catContacts", lContactTypewiseNames);
      
        CompanyShareIndividualBean lCompanyShareIndividualBean = new CompanyShareIndividualBean();
        List<CompanyShareIndividualBean> lCompanyShareIndividualBeanList = null;
        Map<String,List<Object>> lCompanyShareIndividualwiseNames = new HashMap<String, List<Object>>();
        Map<String,Object> lCompanyShareIndividualName = new HashMap<String,Object>();
        List<Object> lCompanyShareIndividualNameList = new ArrayList<Object>();
        String lIndivCode =  KYCDocumentMasterBean.RepeatType.Individual.getCode();
        lCompanyShareIndividualBean.setCdId(lFilterCompanyKYCDocumentBean.getCdId());
    	lCompanyShareIndividualBeanList =  getShareIndividualDAO(lProvFlag).findList(pExecutionContext.getConnection(), lCompanyShareIndividualBean, Arrays.asList(new String[] { "id","salutation","firstName","middleName","lastName"}));
    	if(lCompanyShareIndividualBeanList!=null&&lCompanyShareIndividualBeanList.size() > 0){
    		for(CompanyShareIndividualBean lCSIBean : lCompanyShareIndividualBeanList){
    			lCompanyShareIndividualName = new HashMap<String, Object>();
    			lCompanyShareIndividualName.put("csiId", lCSIBean.getId());
    			lCompanyShareIndividualName.put("csiDisp" ,lTredsHelper.getCompanyShareIndividualFullName(lCSIBean));
    			lCompanyShareIndividualNameList.add(lCompanyShareIndividualName);
    		}
    	}
    	lCompanyShareIndividualwiseNames.put(lIndivCode, lCompanyShareIndividualNameList);
    	lResponse.put("catIndiv", lCompanyShareIndividualwiseNames);
        	
        	
    	 CompanyShareEntityBean lCompanyShareEntityBean = new CompanyShareEntityBean();
         List<CompanyShareEntityBean> lCompanyShareEntityBeanList = null;
         Map<String,List<Object>> lCompanyShareEntitywiseNames = new HashMap<String, List<Object>>();
         Map<String,List<Object>> lCompanyShareEntityKmpwiseNames = new HashMap<String, List<Object>>();
         Map<String,Object> lCompanyShareEntityName = new HashMap<String,Object>();
         Map<String,Object> lCompanyShareEntityKmpName = new HashMap<String,Object>();
         List<Object> lCompanyShareEntityNameList = new ArrayList<Object>();
         List<Object> lCompanyShareEntityKmpNameList = new ArrayList<Object>();
         String[] lSECodes = new String[] { KYCDocumentMasterBean.RepeatType.Entity.getCode(), KYCDocumentMasterBean.RepeatType.EntityKmp.getCode()};
         lCompanyShareEntityBean.setCdId(lFilterCompanyKYCDocumentBean.getCdId());
         lCompanyShareEntityBeanList =  getShareEntityDAO(lProvFlag).findList(pExecutionContext.getConnection(), lCompanyShareEntityBean, Arrays.asList(new String[] { "id","salutation","firstName","middleName","lastName","companyName"}));
         	if(lCompanyShareEntityBeanList!=null&&lCompanyShareEntityBeanList.size() > 0){
         		for(CompanyShareEntityBean lCSEBean : lCompanyShareEntityBeanList){
         			lCompanyShareEntityName = new HashMap<String, Object>();
         			lCompanyShareEntityName.put("cseId", lCSEBean.getId());
         			lCompanyShareEntityName.put("cseDisp" ,lCSEBean.getCompanyName());
         			lCompanyShareEntityNameList.add(lCompanyShareEntityName);
         			lCompanyShareEntityKmpName = new HashMap<String, Object>();
         			lCompanyShareEntityKmpName.put("cseId", lCSEBean.getId());
         			lCompanyShareEntityKmpName.put("cseDisp" ,lTredsHelper.getCompanyShareEntityFullName(lCSEBean));
        			lCompanyShareEntityKmpNameList.add(lCompanyShareEntityKmpName);
         		}
         	}
         	lCompanyShareEntitywiseNames.put(lSECodes[0], lCompanyShareEntityNameList);
         	lCompanyShareEntityKmpwiseNames.put(lSECodes[1], lCompanyShareEntityKmpNameList);
         	lResponse.put("catEntity", lCompanyShareEntitywiseNames);
         	lResponse.put("catEntityKmp", lCompanyShareEntityKmpwiseNames);
     	//
             	
        //
        CompanyBankDetailBean lCompanyBankDetailBean = new CompanyBankDetailBean();
        List<CompanyBankDetailBean> lCompanyBankDetailBeanList = null;
        Map<String,List<Object>> lCompanyBankDetailNames = new HashMap<String, List<Object>>();
        Map<String,Object> lCompanyBankDetailName = new HashMap<String,Object>();
        List<Object> lCompanyBankDetailNameList = new ArrayList<Object>();
        String lBankCode =  KYCDocumentMasterBean.RepeatType.Bank.getCode();
        lCompanyBankDetailBean.setCdId(lFilterCompanyKYCDocumentBean.getCdId());
        HashMap<String, String> lBankHash = TredsHelper.getInstance().getBankName();
        lCompanyBankDetailBeanList =  getBankDetailDAO(lProvFlag).findList(pExecutionContext.getConnection(), lCompanyBankDetailBean, Arrays.asList(new String[] { "id","ifsc","accNo"}));
    	if(lCompanyBankDetailBeanList!=null && lCompanyBankDetailBeanList.size() > 0){
    		for(CompanyBankDetailBean lCBBean : lCompanyBankDetailBeanList){
    			lCompanyBankDetailName = new HashMap<String, Object>();
    			lCompanyBankDetailName.put("csbId", lCBBean.getId());
    			lCompanyBankDetailName.put("csbDisp" ,lBankHash.get(lCBBean.getIfsc().substring(0, 4))+" ("+lCBBean.getAccNo()+ ") ");
    			lCompanyBankDetailNameList.add(lCompanyBankDetailName);
    		}
    	}
    	lCompanyBankDetailNames.put(lBankCode, lCompanyBankDetailNameList);
    	lResponse.put("catBank", lCompanyBankDetailNames);
        	
    	
    	//
        CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
        List<CompanyLocationBean> lCompanyLocationBeanList = null;
        Map<String,List<Object>> lCompanyLocationNames = new HashMap<String, List<Object>>();
        Map<String,Object> lCompanyLocationName = new HashMap<String,Object>();
        List<Object> lCompanyLocationNameList = new ArrayList<Object>();
        String lLocationCode =  KYCDocumentMasterBean.RepeatType.Location.getCode();
        lCompanyLocationBean.setCdId(lFilterCompanyKYCDocumentBean.getCdId());
        lCompanyLocationBeanList =  getLocationDAO(lProvFlag).findList(pExecutionContext.getConnection(), lCompanyLocationBean, Arrays.asList(new String[] { "id","name"}));
    	if(lCompanyLocationBeanList!=null && lCompanyLocationBeanList.size() > 0){
    		for(CompanyLocationBean lClBean : lCompanyLocationBeanList){
    			lCompanyLocationName = new HashMap<String, Object>();
    			lCompanyLocationName.put("cslId", lClBean.getId());
    			lCompanyLocationName.put("cslDisp" ,lClBean.getName());
    			lCompanyLocationNameList.add(lCompanyLocationName);
    		}
    	}
    	lCompanyLocationNames.put(lLocationCode, lCompanyLocationNameList);
    	lResponse.put("catLoc", lCompanyLocationNames);
    	
    	
        return lResponse;
    } 
    public void save(ExecutionContext pExecutionContext, CompanyKYCDocumentBean pCompanyKYCDocumentBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyKYCDocumentBean.setCdId(pUserBean.getId());
        }
        else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyKYCDocumentBean.getCdId()==null || pCompanyKYCDocumentBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
    	else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if( !TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), pCompanyKYCDocumentBean.getCdId(), (AppUserBean) pUserBean))
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        Connection lConnection = pExecutionContext.getConnection();
        CompanyKYCDocumentBean lOldCompanyKYCDocumentBean = null;
        //
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyKYCDocumentBean.setIsProvisional(true);//THIS IS DONE IN RESOURCE -SINCE DATA COMING FROM JOCATA DIRECTLY SAVES TO MAIN TABLE
        GenericDAO<CompanyKYCDocumentBean> lCompanyKYCDocumentDAO = getDAO(pCompanyKYCDocumentBean.getIsProvisional());
        //
        if (pNew) {
        	pCompanyKYCDocumentBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyKYCDocumentDAO.getTableName()+".id"));
            pCompanyKYCDocumentBean.setRecordCreator(pUserBean.getId());
            lCompanyKYCDocumentDAO.insert(lConnection, pCompanyKYCDocumentBean);
            if(!pCompanyKYCDocumentBean.getIsProvisional()) {
            	lCompanyKYCDocumentDAO.insertAudit(lConnection, pCompanyKYCDocumentBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyKYCDocumentBean = findBean(pExecutionContext, pCompanyKYCDocumentBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyKYCDocumentBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyKYCDocumentBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            if(pCompanyKYCDocumentBean.getRecordVersion() == null){
            	pCompanyKYCDocumentBean.setRecordVersion(lOldCompanyKYCDocumentBean.getRecordVersion());
            }
            lCompanyKYCDocumentDAO.getBeanMeta().copyBean(pCompanyKYCDocumentBean, lOldCompanyKYCDocumentBean, BeanMeta.FIELDGROUP_UPDATE, null);
            pCompanyKYCDocumentBean.setRecordUpdator(pUserBean.getId());
            if (lCompanyKYCDocumentDAO.update(lConnection, lOldCompanyKYCDocumentBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            if(!pCompanyKYCDocumentBean.getIsProvisional()) {
            	lCompanyKYCDocumentDAO.insertAudit(lConnection, lOldCompanyKYCDocumentBean, GenericDAO.AuditAction.Update, pUserBean.getId());
            }
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyKYCDocumentBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
    	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        pFilterBean.setIsProvisional(true);
        CompanyKYCDocumentBean lCompanyKYCDocumentBean =getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);

        if (lCompanyKYCDocumentBean==null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        if( (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()) ) && 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyKYCDocumentBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        lCompanyKYCDocumentBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyKYCDocumentBean);        
        pExecutionContext.commitAndDispose();
    }

	public String saveCompanyDocument(ExecutionContext pExecutionContext, AppUserBean pUserBean, CompanyKYCDocumentBean pCompanyKYCDocumentBean, Long pCdId, Long pContactRefid) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyKYCDocumentBean.setCdId(pCdId);
		}
		CompanyKYCDocumentBean lCompanyKYCDocumentBean = new CompanyKYCDocumentBean();
		lCompanyKYCDocumentBean.setRefId(pCompanyKYCDocumentBean.getRefId());
		lCompanyKYCDocumentBean = getDAO(false).findBean(lConnection, lCompanyKYCDocumentBean);
		if (pContactRefid!=null) {
			CompanyContactBean lCompanyContactBean = new CompanyContactBean();
			lCompanyContactBean.setRefId(pContactRefid);
			lCompanyContactBean = getContactDAO(false).findBean(lConnection, lCompanyContactBean);
			if (lCompanyContactBean!=null) {
				pCompanyKYCDocumentBean.setDocForCCId(lCompanyContactBean.getId());
			}
		}
		if (lCompanyKYCDocumentBean != null) {
			lNew = false;
			pCompanyKYCDocumentBean.setId(lCompanyKYCDocumentBean.getId());
		}
		save(pExecutionContext, pCompanyKYCDocumentBean, pUserBean, lNew);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
	}
    
}
