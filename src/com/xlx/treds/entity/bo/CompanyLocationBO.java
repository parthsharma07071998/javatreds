package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.CompanyLocationBean.LocationType;
import com.xlx.treds.entity.bean.CompanyLocationDetailsBean;
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class CompanyLocationBO {
	public static Logger logger = Logger.getLogger(CompanyLocationBO.class);
    public static final String TABLENAME_PROV = "CompanyLocations_P";
    
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<CompanyLocationBean> companyLocationProvDAO;
    private CompositeGenericDAO<CompanyLocationDetailsBean> companyLocationDetailsDAO;

    public CompanyLocationBO() {
        super();
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        companyLocationProvDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class,TABLENAME_PROV);
        companyLocationDetailsDAO = new CompositeGenericDAO<CompanyLocationDetailsBean>(CompanyLocationDetailsBean.class);
    }
    
    private GenericDAO<CompanyLocationBean> getDAO(boolean pIsProvisonal){
    	if(pIsProvisonal) {
    		return companyLocationProvDAO;
    	}
    	return companyLocationDAO;
    }
   
    public CompanyLocationBean findBean(ExecutionContext pExecutionContext, 
        CompanyLocationBean pFilterBean) throws Exception {
        CompanyLocationBean lCompanyLocationBean =  getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCompanyLocationBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lCompanyLocationBean.getCdId(), pFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lCompanyLocationBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        }
        if(pFilterBean.getIsProvisional()) {
        	CompanyLocationBean lCBDActualBean = getDAO(false).findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
            if(lCBDActualBean!=null) {
            	lCBDActualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
                //
                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(getDAO(false), lCBDActualBean,lCompanyLocationBean);
                lCompanyLocationBean.setModifiedData(lDiffData);
                return lCompanyLocationBean;
            }
        }
        return lCompanyLocationBean;
    }
    
    public List<CompanyLocationBean> findList(ExecutionContext pExecutionContext, CompanyLocationBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
    		if(pFilterBean.getCdId()==null){
    			throw new CommonBusinessException("Entity required.");
    		}
    	}else{
    		if(pFilterBean.getCdId()==null)
    			pFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(pUserBean));
    	}
    	String TABLE_SUFFIX = "";
    	if(pFilterBean.getIsProvisional()) {
    		TABLE_SUFFIX = "_P";
    	}
    	CompanyDetailBean lCompanyDetailBean = TredsHelper.getInstance().getCompanyDetailsBean(lConnection, pFilterBean.getCdId(),pFilterBean.getIsProvisional());
    	//boolean isLocationwiseSettlementEnabled = (TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, pFilterBean.getCdId()));
    	boolean isLocationwiseSettlementEnabled = (lCompanyDetailBean!=null?(CommonAppConstants.Yes.Yes.equals(lCompanyDetailBean.getEnableLocationwiseSettlement())):false);
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM ( ");
    	lSql.append(" SELECT CompanyLocations").append(TABLE_SUFFIX).append(".*, b.clname BBDADDRESS, BBDBranchname ,FEMACTIVE,FEMEXPIRY ,CBDBANK,CBDACCNO, FEMENTITYCODE CLCompanyCode");
    	lSql.append(" FROM CompanyLocations").append(TABLE_SUFFIX);
    	lSql.append(" LEFT OUTER JOIN CompanyBankDetails").append(TABLE_SUFFIX).append(" ON (CompanyLocations").append(TABLE_SUFFIX).append(".clcbdid=cbdid) ");
    	lSql.append(" LEFT OUTER JOIN BankBranchDetail").append(" ON (CBDIFSC=BBDIFSC) ");
    	lSql.append(" LEFT OUTER JOIN CompanyLocations").append(TABLE_SUFFIX).append(" b ON (CompanyLocations").append(TABLE_SUFFIX).append(".CLSETTLEMENTCLID=b.clid)  ");
    	lSql.append("LEFT OUTER JOIN FACILITATORENTITYMAPPING ON (FEMFacilitator = ").append(DBHelper.getInstance().formatString(AppConstants.FACILITATOR_NPCI)).append(" AND FEMCBDID = nvl(CompanyLocations").append(TABLE_SUFFIX).append(".clcbdid, b.clcbdid)) ");
    	lSql.append(" ) ");
    	lSql.append(" WHERE 1=1 ");
    	getDAO(pFilterBean.getIsProvisional()).appendAsSqlFilter(lSql, pFilterBean, false);
    	//
    	if(TredsHelper.getInstance().checkAccessToLocations((AppUserBean) pUserBean)){
			lSql.append(" AND CLID in (").append(TredsHelper.getInstance()
					.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(")");    		
    	}
        List<CompanyLocationDetailsBean> lCompositeList = companyLocationDetailsDAO.findListFromSql(lConnection, lSql.toString(), 0);
        List<CompanyLocationBean> lList = null;
        if(lCompositeList!=null && lCompositeList.size() > 0){
        	CompanyLocationBean lCLBean = null;
        	CompanyBankDetailBean lCBDBean = null;
        	BankBranchDetailBean lBBDBean = null;
        	FacilitatorEntityMappingBean lFEMEntBean = null;
        	String lBankName,lBankNACHStatus;
        	AppEntityBean lTmpAEBean = null;
        	lList = new ArrayList<CompanyLocationBean>();
        	for (CompanyLocationDetailsBean lDetails : lCompositeList){
        		lCLBean = lDetails.getCompanyLocationBean();
        		lBBDBean = lDetails.getBankBranchDetailBean();
        		lCBDBean = lDetails.getCompanyBankDetailBean();
        		lFEMEntBean = lDetails.getFacilitatorEntityMappingBean();
        		if (isLocationwiseSettlementEnabled){
        			if(lBBDBean!=null) lCLBean.setBankBranchName(lBBDBean.getBranchname());
        			if(lBBDBean!=null) lCLBean.setSettlementName(lBBDBean.getAddress());
        			if(lCBDBean!=null && lCBDBean.getBank() != null){
        				lBankName = TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, lCBDBean.getBank());
        				lCLBean.setBankBranchName(lBankName.concat(" [").concat(lCBDBean.getAccNo()).concat("]"));
        			}
    				lBankNACHStatus="InActive";
    				if(lTmpAEBean==null || !lTmpAEBean.getCdId().equals(lCLBean.getCdId())) {
    					if(lCompanyDetailBean!=null) {
            				lTmpAEBean=TredsHelper.getInstance().getAppEntityBean(lCompanyDetailBean.getCode());
    					}
    				}
    				if(lTmpAEBean==null) {
    					logger.info("Enity not found while fetching company location list for cdid : "+lCLBean.getCdId());
    				}
        			if(CommonAppConstants.YesNo.Yes.equals(lFEMEntBean.getActive())) { 
        				if(lTmpAEBean!=null) {
        					if(lTmpAEBean.isPurchaser() ) {
        						if ( lFEMEntBean.getExpiry() == null ||
        							TredsHelper.getInstance().getBusinessDate().equals(lFEMEntBean.getExpiry()) ||
        						TredsHelper.getInstance().getBusinessDate().before(lFEMEntBean.getExpiry())){
        							lBankNACHStatus="Active";
        						}
        					}else if ( lTmpAEBean.isSupplier()) {
        	    				lBankNACHStatus="Active";
        					}
        				}
	    			}
        			lCLBean.setBankNACHStatus(lBankNACHStatus);
        		}
        		lList.add(lCLBean);
        	}
        }        
        return lList;
    }

    
    public List<CompanyLocationBean> findActiveList(ExecutionContext pExecutionContext, CompanyLocationBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean, boolean pActiveOnly) throws Exception {
        	Connection lConnection = pExecutionContext.getConnection();
        	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        	Long lCbdId = null;
        	if(AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        		if(pFilterBean.getCdId()==null){
        			throw new CommonBusinessException("Entity required.");
        		}
        	}else{
        		if(pFilterBean.getCdId()==null)
        			pFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(pUserBean));
        	}
        	pFilterBean.setEnableSettlement(null);
        	StringBuilder lSql = new StringBuilder();
        	//
        	boolean lFetchForSelf = lAppEntityBean.getCdId().equals(pFilterBean.getCdId());
        	lCbdId=pFilterBean.getCbdId();
        	pFilterBean.setCbdId(null);
        	//
        	//TODO: this has to be changed and the parameter should come from filterbean
        	//currently setting it explicitly to true 
        	GenericDAO<CompanyLocationBean> lCompanyLocationDAO = getDAO(true);
        	//
        	if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        		
        		if(TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, pFilterBean.getCdId(),true)){
            		lSql.append(" select * from  ( ");
            		lSql.append( " select a.* from CompanyLocations a,FacilitatorEntityMapping  " );
            		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT = 'Y'  " );
            		lSql.append( " AND a.CLCBDID = FEMCBDID ");
            		lSql.append( " AND FEMRECORDVERSION > 0 ");
            		if(pActiveOnly){
            			lSql.append( " AND FEMACTIVE = 'Y' ");
    	        		lSql.append( " AND (femexpiry is null or femexpiry >= " );
    	        		lSql.append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append(" ) " );
            		}
            		lSql.append(" AND FEMFACILITATOR = ").append(DBHelper.getInstance().formatString(AppConstants.FACILITATOR_NPCI));

            		if(lFetchForSelf && TredsHelper.getInstance().checkAccessToLocations((AppUserBean)pUserBean)){
                      	lSql.append(" and CLID in (").append(TredsHelper.getInstance()
                      			.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(")");
            		}
            		if(lCbdId!=null){
            			lSql.append(" and a.CLCBDID = ").append(lCbdId);
            		}
            		lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
            		lSql.append( " union all " );
            		lSql.append( " select a.* from CompanyLocations a,CompanyLocations b,FacilitatorEntityMapping  " );
            		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT is null " );
            		lSql.append( " and b.CLRecordVersion > 0 AND b.CLENABLESETTLEMENT = 'Y' AND a.CLSETTLEMENTCLID=b.clid " );
            		lSql.append( " AND b.CLCBDID = FEMCBDID ");
            		lSql.append( " AND FEMRECORDVERSION > 0 ");
            		 
            		if(pActiveOnly){
            			lSql.append( " AND FEMACTIVE = 'Y' ");
    	        		lSql.append( " AND (femexpiry is null or femexpiry >= " );
    	        		lSql.append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append(" ) " );
            		}
            		lSql.append(" AND FEMFACILITATOR = ").append(DBHelper.getInstance().formatString(AppConstants.FACILITATOR_NPCI));
            		if(lCbdId!=null){
            			lSql.append(" and b.CLCBDID = ").append(lCbdId);
            		}
            		lSql.append(" ) CompanyLocations WHERE 1=1 ");
            		if(lFetchForSelf && TredsHelper.getInstance().checkAccessToLocations((AppUserBean)pUserBean)){
                      	lSql.append(" and CLID in (").append(TredsHelper.getInstance()
                      			.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(")");
            		}
            		lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
        		}else{
    				lSql.append("SELECT * FROM COMPANYLOCATIONS where 1=1 ");
    				if(lFetchForSelf && TredsHelper.getInstance().checkAccessToLocations((AppUserBean)pUserBean)){
                      	lSql.append(" and CLID in (").append(TredsHelper.getInstance()
                      			.getCSVIdsForInQuery(((AppUserBean) pUserBean).getLocationIdList())).append(")");
            		}
    				lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
        		}        		
        	}else{
        		//for platform - to be shown in nach code scrren
        		if((TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, pFilterBean.getCdId(),true))){
            		lSql.append(" select * from  ( ");
            		lSql.append( " select a.* from CompanyLocations a " );
            		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT = 'Y'  " );
            		if(lCbdId!=null){
            			lSql.append(" and a.CLCBDID = ").append(lCbdId);
            		}
            		lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
            		lSql.append( " union all " );
            		lSql.append( " select a.* from CompanyLocations a,CompanyLocations b" );
            		lSql.append( " WHERE a.CLRecordVersion > 0 AND a.CLENABLESETTLEMENT is null " );
            		lSql.append( " and b.CLRecordVersion > 0 AND b.CLENABLESETTLEMENT = 'Y' AND a.CLSETTLEMENTCLID=b.clid " );
            		if(lCbdId!=null){
            			lSql.append(" and b.CLCBDID = ").append(lCbdId);
            		}
            		lSql.append(" ) CompanyLocations WHERE 1=1 ");
            		lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
        		}else{
                	lSql.append("SELECT * FROM COMPANYLOCATIONS where 1=1 ");
                	lCompanyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
        		}
        	}
        	//
            return lCompanyLocationDAO.findListFromSql(lConnection, lSql.toString(), 0);
        }

    public void save(ExecutionContext pExecutionContext, CompanyLocationBean pCompanyLocationBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        // check if registration details are editable
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())){
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
            pCompanyLocationBean.setCdId(pUserBean.getId());
        }
        else if (AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
        	if(pCompanyLocationBean.getCdId()==null || pCompanyLocationBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	CompanyDetailBean lFilterBean = new CompanyDetailBean();
        	lFilterBean.setId(pCompanyLocationBean.getCdId());
        }else if (AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
        	if(pCompanyLocationBean.getCdId()==null || pCompanyLocationBean.getCdId().longValue() == 0)
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	CompanyDetailBean lFilterBean = new CompanyDetailBean();
        	lFilterBean.setId(pCompanyLocationBean.getCdId());
            if(TredsHelper.getInstance().isRegistrationApproved(lConnection, pCompanyLocationBean.getCdId())){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
        }
        CompanyLocationBean lOldCompanyLocationBean = null;
        
        //check if settlement enabled
        if(TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, pCompanyLocationBean.getCdId(),pCompanyLocationBean.getIsProvisional())){
        	if(CommonAppConstants.Yes.Yes.equals(pCompanyLocationBean.getEnableSettlement()) && 
        			pCompanyLocationBean.getCbdId() == null ){
        		throw new CommonBusinessException("Bank details required.");
        	}else if (!CommonAppConstants.Yes.Yes.equals(pCompanyLocationBean.getEnableSettlement()) && 
        			pCompanyLocationBean.getSettlementCLId() == null) {
        		throw new CommonBusinessException("Location details required.");
        	}
        }else{
        	pCompanyLocationBean.setCbdId(null);
        	pCompanyLocationBean.setSettlementCLId(null);
        	pCompanyLocationBean.setEnableSettlement(null);
        }
        //validation 
        if(LocationType.RegOffice.equals(pCompanyLocationBean.getLocationType())){
        	CompanyLocationBean lRegLocationBean = TredsHelper.getInstance().getRegisteredOfficeLocation(lConnection, pCompanyLocationBean.getCdId());
        	if(lRegLocationBean!=null && lRegLocationBean.getId()!=null){
        		if(!lRegLocationBean.getId().equals(pCompanyLocationBean.getId())){
            		throw new CommonBusinessException("Registered Office Location already exists.");        	
        		}
        	}
        }
        //
        //SAVE FUNCTION WILL ALWAYS SAVE TO PROVISIONAL
        //this is done for any subsequent findBean and findList functions of BO if called subsequently
        //pCompanyLocationBean.setIsProvisional(true);//THIS IS DONE IN RESOURCE -SINCE DATA COMING FROM JOCATA DIRECTLY SAVES TO MAIN TABLE
        GenericDAO<CompanyLocationBean> lCompanyLocationDAO = getDAO(pCompanyLocationBean.getIsProvisional());//this is true ie from provisional
        //
        if (pNew) {
        	pCompanyLocationBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, companyLocationDAO.getTableName()+".id"));
            pCompanyLocationBean.setRecordCreator(pUserBean.getId());
            lCompanyLocationDAO.insert(lConnection, pCompanyLocationBean);
            //only for jocata
            if(!pCompanyLocationBean.getIsProvisional()) {
                lCompanyLocationDAO.insertAudit(lConnection, pCompanyLocationBean, AuditAction.Insert, pUserBean.getId());
            }
        } else {
            lOldCompanyLocationBean = findBean(pExecutionContext, pCompanyLocationBean);
            if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            	if(AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain())){
                	if(!lOldCompanyLocationBean.getRecordCreator().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}else {
                	if(!lOldCompanyLocationBean.getCdId().equals(pUserBean.getId()))
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            	}
            }
            pCompanyLocationBean.setRecordUpdator(pUserBean.getId());
            if(pCompanyLocationBean.getRecordVersion() == null){
            	pCompanyLocationBean.setRecordVersion(lOldCompanyLocationBean.getRecordVersion());
            }
            if (lCompanyLocationDAO.update(lConnection, pCompanyLocationBean, BeanMeta.FIELDGROUP_UPDATE) == 0){
            	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            }
            lCompanyLocationDAO.getBeanMeta().copyBean(pCompanyLocationBean, lOldCompanyLocationBean,BeanMeta.FIELDGROUP_UPDATE,null);
            //only for jocata
            if(!pCompanyLocationBean.getIsProvisional()) {
                lCompanyLocationDAO.insertAudit(lConnection, lOldCompanyLocationBean, AuditAction.Update, pUserBean.getId());
            }
        }
    }
    
    public void delete(ExecutionContext pExecutionContext, CompanyLocationBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	CompanyLocationBean lCompanyLocationBean = getDAO(pFilterBean.getIsProvisional()).findByPrimaryKey(lConnection, pFilterBean);
    	//
    	if (lCompanyLocationBean==null) {
         	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain())) {
        	CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        	lCompanyDetailBO.findBean(pExecutionContext, null, pUserBean, true);
        }
        if((AppConstants.DOMAIN_REGENTITY.equals(pUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGUSER.equals(pUserBean.getDomain()))&& 
    			TredsHelper.getInstance().isRegistrationApproved(lConnection, lCompanyLocationBean.getCdId())){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        pExecutionContext.setAutoCommit(false);
        lCompanyLocationBean.setRecordUpdator(pUserBean.getId());
        getDAO(pFilterBean.getIsProvisional()).delete(lConnection, lCompanyLocationBean);  
        //getDAO(pFilterBean.getIsProvisional()).insertAudit(lConnection, lCompanyLocationBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }

    public List<CompanyLocationBean> findLocationwiseGSTNList(ExecutionContext pExecutionContext, CompanyLocationBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean, AppEntityBean.EntityType pEntityType ) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	List<CompanyLocationBean> lList =  null;
    	StringBuilder lSql = new StringBuilder();
        String lDBColumnNames =  companyLocationDAO.getDBColumnNameCsv(null);
        lSql.append(" SELECT ");
    	lSql.append(lDBColumnNames).append(", CdCode \"CLCompanyCode\" ");
    	lSql.append(" FROM COMPANYLOCATIONS, COMPANYDETAILS WHERE CLRecordVersion > 0 AND CLCDID=CDID ");
    	if(AppEntityBean.EntityType.Supplier.equals(pEntityType)){
        	lSql.append(" AND CDSUPPLIERFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()) );
    	}
    	else if(AppEntityBean.EntityType.Purchaser.equals(pEntityType)){
        	lSql.append(" AND CDPURCHASERFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()) );
    	}
    	else if(AppEntityBean.EntityType.Financier.equals(pEntityType)){
        	lSql.append(" AND CDFINANCIERFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()) );
    	}
    	companyLocationDAO.appendAsSqlFilter(lSql, pFilterBean, false);
    	if(pFilterBean.getCompanyCode()!=null){
        	lSql.append(" AND CdCode = ").append(DBHelper.getInstance().formatString(pFilterBean.getCompanyCode()) );
    	}
    	lSql.append(" order by cdcode ");
    	
    	lList = companyLocationDAO.findListFromSql(lConnection, lSql.toString(), 0);
    	return lList;
    }
    
    public String saveCompanyLocation(ExecutionContext pExecutionContext,AppUserBean pUserBean,CompanyLocationBean pCompanyLocationBean, Long pCdId) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
		boolean lNew = true;
		if (pCdId!=null) {
			pCompanyLocationBean.setCdId(pCdId);
		}
		if (pCompanyLocationBean.getRefId()!=null) {
			CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
			lCompanyLocationBean.setRefId(pCompanyLocationBean.getRefId());
			lCompanyLocationBean = getDAO(false).findBean(lConnection, lCompanyLocationBean);
			if (lCompanyLocationBean != null) {
				lNew = false;
				pCompanyLocationBean.setId(lCompanyLocationBean.getId());
			}
		}else {
			throw new CommonBusinessException("refId missing.");
		}
		save(pExecutionContext, pCompanyLocationBean, pUserBean, lNew);
		Map <String,String> lMap = new HashMap<>();
		lMap.put("message", "Saved Successfully");
		return new JsonBuilder(lMap).toString();
	}

    //TODO: THE RESOURCE ATTACHED TO THIS FUNCTION IS NOT CALLED FROM ANYWHERE = BUT STILL WE NEED TO CHECK HOW THE SETTLEMENT IS ENABLED DISABLED
	public void saveTransactionalData(Connection pConnection, Map<String, Object> lAllLocationsHash) throws Exception{
		List lLocationFields = Arrays.asList(new String[]{"id","enableSettlement","cbdId","settlementCLId"});
		if (!lAllLocationsHash.isEmpty()) {
			for  (Object lLocationMap : lAllLocationsHash.values()) {
				CompanyLocationBean lCompanyLocBean = new CompanyLocationBean();
		        List<ValidationFailBean> lValidationFailBeans = companyLocationDAO.getBeanMeta().validateAndParse(lCompanyLocBean, (Map<String, Object>) lLocationMap, null, lLocationFields, null);
		        if (lCompanyLocBean!=null) {
		        	CompanyLocationBean lOldCompanyLocationBean = companyLocationDAO.findByPrimaryKey(pConnection, lCompanyLocBean);
	    			if (!companyLocationDAO.getBeanMeta().equalBean(lCompanyLocBean, lOldCompanyLocationBean, null, lLocationFields)) {
	    				lCompanyLocBean.setRecordVersion(lOldCompanyLocationBean.getRecordVersion());
	    				companyLocationDAO.update(pConnection, lCompanyLocBean, lLocationFields);
	    			}
		        }
			}
		}
	}
}
