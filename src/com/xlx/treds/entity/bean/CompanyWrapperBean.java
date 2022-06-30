package com.xlx.treds.entity.bean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.treds.TredsHelper;

public class CompanyWrapperBean
{

	public static Logger logger = Logger.getLogger(CompanyWrapperBean.class);
	public static String ENTITY_NAME = "CompanyWrapperBean";
	//
    public static final String TABLENAME_COMPANY_PROV = "CompanyDetails_P";
    public static final String TABLENAME_BANKDETAILS_PROV = "CompanyBankDetails_P";
    public static final String TABLENAME_CONTACTS_PROV = "CompanyContacts_P";
    public static final String TABLENAME_KYCDOCUMENTS_PROV = "CompanyKYCDocuments_P";
    public static final String TABLENAME_LOCATIONS_PROV = "CompanyLocations_P";
    public static final String TABLENAME_SHAREENTITY_PROV = "CompanyShareEntity_P";
    public static final String TABLENAME_SHAREINDIVIDUAL_PROV = "CompanyShareIndividual_P";
	//
	private CompanyDetailBean companyDetailBean; 
	private Map<Long,CompanyBankDetailBean> companyBankDetails;
	private Map<Long,CompanyContactBean> companyContacts;
	private Map<Long,CompanyKYCDocumentBean> companyKYCDocuments;
	private Map<Long,CompanyLocationBean> companyLocations; 
	private Map<Long,CompanyShareEntityBean> companyShareEntities; 
	private Map<Long,CompanyShareIndividualBean> companyShareIndividuals; 
	//
	//bank,contact,kyc,location,shareEntity,shareIndiv
    private GenericDAO<CompanyDetailBean> companyDetailDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    private GenericDAO<CompanyContactBean> companyContactDAO;
    private GenericDAO<CompanyKYCDocumentBean> companyKYCDocumentDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<CompanyShareEntityBean> companyShareEntityDAO;
    private GenericDAO<CompanyShareIndividualBean> companyShareIndividualDAO;
    //
    private Connection connection;
    private Long userId;
	//
    private boolean provisional;
    //
	public boolean isProvisional() {
		return provisional;
	}
	public CompanyWrapperBean(Connection pConnection, Long pCompanyId, boolean pProvisional, Long pUserId) {
		provisional = pProvisional;
		connection = pConnection;
		userId = pUserId;
		fetchData(pConnection, pCompanyId, pProvisional);
	}
	private void fetchData(Connection pConnection, Long pCompanyId, boolean pProvisional)
	{
		if(pProvisional) {
			companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class, TABLENAME_COMPANY_PROV);
			companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class, TABLENAME_BANKDETAILS_PROV);
			companyContactDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class, TABLENAME_CONTACTS_PROV);
			companyKYCDocumentDAO = new GenericDAO<CompanyKYCDocumentBean>(CompanyKYCDocumentBean.class, TABLENAME_KYCDOCUMENTS_PROV);
			companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class, TABLENAME_LOCATIONS_PROV);
			companyShareEntityDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class, TABLENAME_SHAREENTITY_PROV);
	        companyShareIndividualDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class, TABLENAME_SHAREINDIVIDUAL_PROV);
		}else {
			companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
			companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
			companyContactDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
			companyKYCDocumentDAO = new GenericDAO<CompanyKYCDocumentBean>(CompanyKYCDocumentBean.class);
			companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
			companyShareEntityDAO = new GenericDAO<CompanyShareEntityBean>(CompanyShareEntityBean.class);
	        companyShareIndividualDAO = new GenericDAO<CompanyShareIndividualBean>(CompanyShareIndividualBean.class);
		}
		//company
		CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
		lCompanyDetailBean.setId(pCompanyId);
		try {
			companyDetailBean = companyDetailDAO.findBean(pConnection,  lCompanyDetailBean);
		} catch (Exception e) {
			logger.debug("Error in company details : "+e.getMessage());
		}
		//bank
		CompanyBankDetailBean lCompanyBankDetailBean = new CompanyBankDetailBean();
		lCompanyBankDetailBean.setCdId(pCompanyId);
		try {
			companyBankDetails = new HashMap<Long, CompanyBankDetailBean>();
			List<CompanyBankDetailBean> lBeans = companyBankDetailDAO.findList(pConnection, lCompanyBankDetailBean);
			if(lBeans!=null) {
				for(CompanyBankDetailBean lBean: lBeans) {
					companyBankDetails.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company bank list : "+e.getMessage());
		}
		//contact
		CompanyContactBean lCompanyContactBean = new CompanyContactBean();
		lCompanyContactBean.setCdId(pCompanyId);
		try {
			companyContacts = new HashMap<Long, CompanyContactBean>();
			List<CompanyContactBean> lBeans = companyContactDAO.findList(pConnection, lCompanyContactBean);
			if(lBeans!=null) {
				for(CompanyContactBean lBean: lBeans) {
					companyContacts.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company contact list : "+e.getMessage());
		}		
		//kycdocument
		CompanyKYCDocumentBean lCompanyKYCDocumentBean = new CompanyKYCDocumentBean();
		lCompanyKYCDocumentBean.setCdId(pCompanyId);
		try {
			companyKYCDocuments = new HashMap<Long, CompanyKYCDocumentBean>();
			List<CompanyKYCDocumentBean> lBeans = companyKYCDocumentDAO.findList(pConnection, lCompanyKYCDocumentBean);
			if(lBeans!=null) {
				for(CompanyKYCDocumentBean lBean: lBeans) {
					companyKYCDocuments.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company kyc doc list : "+e.getMessage());
		}	
		//location
		CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
		lCompanyLocationBean.setCdId(pCompanyId);
		try {
			companyLocations = new HashMap<Long, CompanyLocationBean>();
			List<CompanyLocationBean> lBeans = companyLocationDAO.findList(pConnection, lCompanyLocationBean);
			if(lBeans!=null) {
				for(CompanyLocationBean lBean: lBeans) {
					companyLocations.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company location list : "+e.getMessage());
		}	
		//share entity
		CompanyShareEntityBean lCompanyShareEntityBean = new CompanyShareEntityBean();
		lCompanyShareEntityBean.setCdId(pCompanyId);
		try {
			companyShareEntities = new HashMap<Long, CompanyShareEntityBean>();
			List<CompanyShareEntityBean> lBeans = companyShareEntityDAO.findList(pConnection, lCompanyShareEntityBean);
			if(lBeans!=null) {
				for(CompanyShareEntityBean lBean: lBeans) {
					companyShareEntities.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company share entity list : "+e.getMessage());
		}	
		//share individual
		CompanyShareIndividualBean lCompanyShareIndividualBean = new CompanyShareIndividualBean();
		lCompanyShareIndividualBean.setCdId(pCompanyId);
		try {
			companyShareIndividuals = new HashMap<Long, CompanyShareIndividualBean>();
			List<CompanyShareIndividualBean> lBeans = companyShareIndividualDAO.findList(pConnection, lCompanyShareIndividualBean);
			if(lBeans!=null) {
				for(CompanyShareIndividualBean lBean: lBeans) {
					companyShareIndividuals.put(lBean.getId(), lBean);
				}
			}
		} catch (Exception e) {
			logger.debug("Error in company share individuals list : "+e.getMessage());
		}	
	}

	public CompanyDetailBean getCompanyDetailBean() {
		return companyDetailBean;
	}
	public Map<Long,CompanyLocationBean> getCompanyLocations() {
		return companyLocations;
	}
	public Map<Long,CompanyBankDetailBean> getCompanyBankDetails() {
		return companyBankDetails;
	}
	public Map<Long,CompanyShareIndividualBean> getCompanyShareIndividuals() {
		return companyShareIndividuals;
	}
	public Map<Long,CompanyShareEntityBean> getCompanyShareEntities() {
		return companyShareEntities;
	}
	public Map<Long,CompanyContactBean> getCompanyContacts() {
		return companyContacts;
	}
	public Map<Long,CompanyKYCDocumentBean> getCompanyKYCDocuments() {
		return companyKYCDocuments;
	}
	
	public void updateDatabase(CompanyWrapperBean pCompanyWrapperProv) throws Exception {
		if(pCompanyWrapperProv.isProvisional() && !isProvisional()) {
			//TODO: PENDING COMPANYDETAILS
			
			//bank,contact,kyc,location,shareEntity,shareIndiv
			//bank
			for(Long lId : pCompanyWrapperProv.getCompanyBankDetails().keySet()) {
				//insert
				if(!companyBankDetails.containsKey(lId)) {
					companyBankDetailDAO.insert(connection, pCompanyWrapperProv.getCompanyBankDetails().get(lId));
					companyBankDetailDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyBankDetails().get(lId),AuditAction.Insert,userId);
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyBankDetailDAO, companyBankDetails.get(lId),pCompanyWrapperProv.getCompanyBankDetails().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyBankDetailDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyBankDetails().get(lId), companyBankDetails.get(lId), null, lFields);
	                	companyBankDetailDAO.update(connection, companyBankDetails.get(lId), lFields );
	                	companyBankDetailDAO.insertAudit(connection, companyBankDetails.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyBankDetails.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyBankDetails().containsKey(lId)) {
					companyBankDetailDAO.delete(connection, companyBankDetails.get(lId));
					companyBankDetailDAO.insertAudit(connection, companyBankDetails.get(lId),AuditAction.Delete,userId);
				}
			}
			//contact
			for(Long lId : pCompanyWrapperProv.getCompanyContacts().keySet()) {
				//insert
				if(!companyContacts.containsKey(lId)) {
					companyContactDAO.insert(connection, pCompanyWrapperProv.getCompanyContacts().get(lId));
					companyContactDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyContacts().get(lId), AuditAction.Insert, userId );
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyContactDAO, companyContacts.get(lId),pCompanyWrapperProv.getCompanyContacts().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyContactDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyContacts().get(lId), companyContacts.get(lId), null, lFields);
	                	companyContactDAO.update(connection, companyContacts.get(lId), lFields );
	                	companyContactDAO.insertAudit(connection, companyContacts.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyContacts.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyContacts().containsKey(lId)) {
					companyContactDAO.delete(connection, companyContacts.get(lId));
					companyContactDAO.insertAudit(connection, companyContacts.get(lId), AuditAction.Delete, userId );
				}
			}
			//kyc
			for(Long lId : pCompanyWrapperProv.getCompanyKYCDocuments().keySet()) {
				//insert
				if(!companyKYCDocuments.containsKey(lId)) {
					companyKYCDocumentDAO.insert(connection, pCompanyWrapperProv.getCompanyKYCDocuments().get(lId));
					companyKYCDocumentDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyKYCDocuments().get(lId), AuditAction.Insert, userId );
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyKYCDocumentDAO, companyKYCDocuments.get(lId),pCompanyWrapperProv.getCompanyKYCDocuments().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyKYCDocumentDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyKYCDocuments().get(lId), companyKYCDocuments.get(lId), null, lFields);
	                	companyKYCDocumentDAO.update(connection, companyKYCDocuments.get(lId), lFields );
	                	companyKYCDocumentDAO.insertAudit(connection, companyKYCDocuments.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyKYCDocuments.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyKYCDocuments().containsKey(lId)) {
					companyKYCDocumentDAO.delete(connection, companyKYCDocuments.get(lId));
					companyKYCDocumentDAO.insertAudit(connection, companyKYCDocuments.get(lId), AuditAction.Delete, userId );
				}
			}
			//loction
			for(Long lId : pCompanyWrapperProv.getCompanyLocations().keySet()) {
				//insert
				if(!companyLocations.containsKey(lId)) {
					companyLocationDAO.insert(connection, pCompanyWrapperProv.getCompanyLocations().get(lId));
					companyLocationDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyLocations().get(lId), AuditAction.Insert, userId);
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyLocationDAO, companyLocations.get(lId),pCompanyWrapperProv.getCompanyLocations().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyLocationDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyLocations().get(lId), companyLocations.get(lId), null, lFields);
	                	companyLocationDAO.update(connection, companyLocations.get(lId), lFields );
	                	companyLocationDAO.insertAudit(connection, companyLocations.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyLocations.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyLocations().containsKey(lId)) {
					companyLocationDAO.delete(connection, companyLocations.get(lId));
					companyLocationDAO.insertAudit(connection, companyLocations.get(lId), AuditAction.Delete, userId);
				}
			}
			//shareEntity
			for(Long lId : pCompanyWrapperProv.getCompanyShareEntities().keySet()) {
				//insert
				if(!companyShareEntities.containsKey(lId)) {
					companyShareEntityDAO.insert(connection, pCompanyWrapperProv.getCompanyShareEntities().get(lId));
					companyShareEntityDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyShareEntities().get(lId), AuditAction.Insert, userId);
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyShareEntityDAO, companyShareEntities.get(lId),pCompanyWrapperProv.getCompanyShareEntities().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyShareEntityDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyShareEntities().get(lId), companyShareEntities.get(lId), null, lFields);
	                	companyShareEntityDAO.update(connection, companyShareEntities.get(lId), lFields );
	                	companyShareEntityDAO.insertAudit(connection, companyShareEntities.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyShareEntities.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyShareEntities().containsKey(lId)) {
					companyShareEntityDAO.delete(connection, companyShareEntities.get(lId));
					companyShareEntityDAO.insertAudit(connection, companyShareEntities.get(lId), AuditAction.Delete, userId);
				}
			}
			//shareIndividual
			for(Long lId : pCompanyWrapperProv.getCompanyShareIndividuals().keySet()) {
				//insert
				if(!companyShareIndividuals.containsKey(lId)) {
					companyShareIndividualDAO.insert(connection, pCompanyWrapperProv.getCompanyShareIndividuals().get(lId));
					companyShareIndividualDAO.insertAudit(connection, pCompanyWrapperProv.getCompanyShareIndividuals().get(lId), AuditAction.Insert, userId);
				}else {
					//compare and update
	                Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(companyShareIndividualDAO, companyShareIndividuals.get(lId),pCompanyWrapperProv.getCompanyShareIndividuals().get(lId));
	                lDiffData = removeUnwanted(lDiffData);
	                if(lDiffData!=null && lDiffData.size() > 0) {
	                	List<String> lFields = getKeyList(lDiffData) ;
	                	companyShareIndividualDAO.getBeanMeta().copyBean( pCompanyWrapperProv.getCompanyShareIndividuals().get(lId), companyShareIndividuals.get(lId), null, lFields);
	                	companyShareIndividualDAO.update(connection, companyShareIndividuals.get(lId), lFields );
	                	companyShareIndividualDAO.insertAudit(connection, companyShareIndividuals.get(lId),AuditAction.Update, userId);
	                }
				}
			}
			for(Long lId : companyShareIndividuals.keySet()) {
				//delete
				if(!pCompanyWrapperProv.getCompanyShareIndividuals().containsKey(lId)) {
					companyShareIndividualDAO.delete(connection, companyShareIndividuals.get(lId));
					companyShareIndividualDAO.insertAudit(connection, companyShareIndividuals.get(lId), AuditAction.Delete, userId);
				}
			}
		}else {
			logger.debug("Incorrect data updation wrappers.");
		}
	}

	private Map<String,Object> removeUnwanted(Map<String,Object> pDiffData){
		if(pDiffData!=null) {
			String[] lKeys = new String[] {"recordVersion","recordUpdator","recordUpdateTime"};
			for(String lKey : lKeys) {
				if(pDiffData.containsKey(lKey)) {
					pDiffData.remove(lKey);
				}
			}
		}
		return pDiffData;
	}
	private List<String> getKeyList(Map<String,Object> pKeyValueData){
		if(pKeyValueData!=null&&pKeyValueData.size()> 0) {
			List<String> lList = new ArrayList<String>();
			for(String lKey : pKeyValueData.keySet()) {
				lList.add(lKey);
			}
			return lList;
		}
		return null;
	}
	
	public void deleteAllData() {
		//bank,contact,kyc,location,shareEntity,shareIndiv
		//bank
		try {
			for(Long lId : companyBankDetails.keySet()) {
				companyBankDetailDAO.delete(connection, companyBankDetails.get(lId));
			}
			//contact
			for(Long lId : companyContacts.keySet()) {
				companyContactDAO.delete(connection, companyContacts.get(lId));
			}
			//kyc
			for(Long lId : companyKYCDocuments.keySet()) {
				companyKYCDocumentDAO.delete(connection, companyKYCDocuments.get(lId));
			}
			//loction
			for(Long lId : companyLocations.keySet()) {
				companyLocationDAO.delete(connection, companyLocations.get(lId));
			}
			//shareEntity
			for(Long lId : companyShareEntities.keySet()) {
				companyShareEntityDAO.delete(connection, companyShareEntities.get(lId));
			}
			//shareIndividual
			for(Long lId : companyShareIndividuals.keySet()) {
				companyShareIndividualDAO.delete(connection, companyShareIndividuals.get(lId));
			}
			//this can be null since the company may be in provisional state only.
			//but due to data migration we might have details in the above tables
			if(companyDetailBean!=null) {
				companyDetailDAO.delete(connection, companyDetailBean);
				AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(companyDetailBean.getCode());
				if(lAppEntityBean!=null) {
					GenericDAO<AppEntityBean> lAppEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
	        		lAppEntityDAO.delete(connection, lAppEntityBean);
					MemoryDBConnection lMemoryDBConnection = MemoryDBManager.getInstance().getConnection();
	                lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
				}
			}
		}catch(Exception lEx) {
			logger.error("Error while deleting company data.",lEx);
		}
	}

	public boolean isJocataData() {
		if(companyDetailBean!=null && "J".equals(companyDetailBean.getCreatorIdentity())) {
			return true;
		}
		return false;
	}
	
}
