package com.xlx.treds.instrument.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean.Level;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.instrument.bean.CersaiFileBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class CersaiFileDataWrapper {
	private static final Logger logger = LoggerFactory.getLogger(CersaiFileDataWrapper.class);
	
	//
	public static final String DESIGNATION_PARTNER = "PART";
	private Connection connection;
	private CersaiFileBean cersaiFileBean;
	//
	private GenericDAO<CompanyDetailBean> companyDetailDAO;
	private GenericDAO<CompanyContactBean> companyContactDAO;
    private CompositeGenericDAO<FactoredBean> factoredDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
	//
	private List<FactoredBean>  factoredList = null;
	private List<InstrumentBean>  instrumentList =  new ArrayList<InstrumentBean>();
	private  Map<String,List<InstrumentBean>> instrumentMap =  new HashMap<String,List<InstrumentBean>>();
	private Map<Long,FactoringUnitBean>  factoringUnitMap  = new HashMap<Long, FactoringUnitBean>();
	private Map<String, AppEntityBean> entityMap = new HashMap<String, AppEntityBean>();
	private Map<String, CompanyDetailBean> companyMap = new HashMap<String, CompanyDetailBean>();
	private Map<Long, List<CompanyContactBean>> companyContactMap = new HashMap<Long, List<CompanyContactBean>>();
	
    public enum CersaiEntityType implements IKeyValEnumInterface<String>{
        Recivable_Owner("SS","Recievable Owner"), Assignor("S","Assignor"), Factor("A","Factor"), Debtor("P", "Debtor") ;
        
        private final String code;
        private final String desc;
        private CersaiEntityType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }
	
	public CersaiFileDataWrapper(CersaiFileBean pCersaiFileBean, Connection pConnection){
		connection = pConnection;
		cersaiFileBean = pCersaiFileBean;
		companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
		companyContactDAO = new GenericDAO<CompanyContactBean>(CompanyContactBean.class);
		factoredDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
		financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
		//
		try{
			gatherData();
		}catch(Exception lEx){
			logger.info("Error in gathering cersai data : " + lEx.getMessage());
		}
	}
	
	private void gatherData() throws Exception{
		factoredList = getListOfFactoredInstruments();
		//
		instrumentList.clear();
		factoringUnitMap.clear();
		//
		if(factoredList!=null){
			List<InstrumentBean>  lTempInstrumentList =  null;
			InstrumentBean lInstBean = new InstrumentBean();
			for(FactoredBean lBean : factoredList ){
				lInstBean = lBean.getInstrumentBean();
				instrumentList.add(lInstBean);
				if (!instrumentMap.containsKey(lInstBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lInstBean.getSupplier())) {
					instrumentMap.put(lInstBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lInstBean.getSupplier(),new ArrayList<>());
				}
				lTempInstrumentList = instrumentMap.get(lInstBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lInstBean.getSupplier());
				lTempInstrumentList.add(lInstBean);
				factoringUnitMap.put(lBean.getInstrumentBean().getId(), lBean.getFactoringUnitBean());
			}
		}
		if(!instrumentList.isEmpty()){
			AppEntityBean lAEBean = null;
			List<Long> lCDIds = new ArrayList<Long>();
			if(!entityMap.containsKey(cersaiFileBean.getFinancier())){
				lAEBean = TredsHelper.getInstance().getAppEntityBean(cersaiFileBean.getFinancier());
				entityMap.put(cersaiFileBean.getFinancier(),lAEBean);
				lCDIds.add(lAEBean.getCdId());
			}
			for(InstrumentBean lInstrumentBean : instrumentList){
				if(!entityMap.containsKey(lInstrumentBean.getPurchaser())){
					lAEBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getPurchaser());
					entityMap.put(lInstrumentBean.getPurchaser(),lAEBean);
					lCDIds.add(lAEBean.getCdId());
				}
				if(!entityMap.containsKey(lInstrumentBean.getSupplier())){
					lAEBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getSupplier());
					entityMap.put(lInstrumentBean.getSupplier(),lAEBean);
					lCDIds.add(lAEBean.getCdId());
				}
			}
			companyMap = getListOfCompanyDetails(lCDIds);
			companyContactMap = getListOfCompanyContacts(lCDIds);
		}
	}

	private Map<String, CompanyDetailBean> getListOfCompanyDetails(List<Long> pCdIds) throws Exception{
		Map<String, CompanyDetailBean> lCompanyHash = new HashMap<String,CompanyDetailBean>();
		StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT *  FROM COMPANYDETAILS WHERE CDRECORDVERSION>0 AND CDID IN ( ");
    	lSql.append(TredsHelper.getInstance().getCSVIdsForInQuery(pCdIds));
    	lSql.append(" ) ");
    	List<CompanyDetailBean> lCompanyDetailsList = companyDetailDAO.findListFromSql(connection, lSql.toString(), -1);
		for (CompanyDetailBean lBean:lCompanyDetailsList){
			if(!lCompanyHash.containsKey(lBean.getCode())){
				lCompanyHash.put(lBean.getCode(), lBean);
			}
		}
    	return lCompanyHash;
	}
	
    private List<FactoredBean> getListOfFactoredInstruments() throws Exception{
    	//The CERSAI understands only Instruments and not Factoring Units.
    	//More-ever we have Clubbed Instruments.
    	//Therefore we have to bring all the factoring units which are factored, exclude the grouped instrument but include the child instrument
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT INSTRUMENTS.*, FactoringUnits.* FROM INSTRUMENTS ");
    	lSql.append(" LEFT OUTER JOIN Instruments PARENTS ON ( nvl(INSTRUMENTS.ingroupinid, INSTRUMENTS.inid) = PARENTS.inid ) ");
    	lSql.append(" LEFT OUTER JOIN FactoringUnits ON ( PARENTS.infuid = FUID and FuRecordversion>0 ) ");
    	lSql.append(" WHERE INSTRUMENTS.INRECORDVERSION>0 ");
    	lSql.append(" AND PARENTS.INRECORDVERSION>0 ");
    	lSql.append(" AND INSTRUMENTS.INCERSAIFILEID = ").append(cersaiFileBean.getId());
		return factoredDAO.findListFromSql(connection, lSql.toString(), -1);
    }
    
    private Map<Long, List<CompanyContactBean>> getListOfCompanyContacts(List<Long> pCdIds) throws Exception{
    	//The CERSAI understands only Instruments and not Factoring Units.
    	//More-ever we have Clubbed Instruments.
    	//Therefore we have to bring all the factoring units which are factored, exclude the grouped instrument but include the child instrument
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT *  FROM COMPANYCONTACTS  WHERE CCRECORDVERSION>0 AND CCCDID IN ( ");
    	lSql.append(TredsHelper.getInstance().getCSVIdsForInQuery(pCdIds));
    	lSql.append(" ) ");
    	lSql.append(" ORDER BY CCCDID , CCID ");
		List<CompanyContactBean> lCompanyContactBeans = companyContactDAO.findListFromSql(connection, lSql.toString(), -1);
		List<CompanyContactBean> lCompanyContactList = null;
		Map<Long, List<CompanyContactBean>> lCompanyContactHash = new HashMap<Long, List<CompanyContactBean>>();
		for (CompanyContactBean lCCBean : lCompanyContactBeans){
			if(!lCompanyContactHash.containsKey(lCCBean.getCdId())){
				lCompanyContactList=new ArrayList<CompanyContactBean>();
				lCompanyContactHash.put(lCCBean.getCdId(), lCompanyContactList);
			}else{
				lCompanyContactList = lCompanyContactHash.get(lCCBean.getCdId());
			}
			lCompanyContactList.add(lCCBean);
		}
		return lCompanyContactHash;
    }

    //
    public Long getRecordCount(){
    	if(cersaiFileBean!=null){
    		return cersaiFileBean.getRecordCount();
    	}
    	return new Long(0);
    }
    
    public Date getFactorDate(){
    	if(cersaiFileBean!=null){
    		return cersaiFileBean.getDate();
    	}
    	return null;
    }
    public Long getSerialNumber(){
    	if(cersaiFileBean!=null){
    		return cersaiFileBean.getSerialNumber();
    	}
    	return new Long(0);
    }
    public String getFinancierCode(){
    	if(cersaiFileBean!=null){
    		return cersaiFileBean.getFinancier();
    	}
    	return null;
    }
    
    public List<InstrumentBean> getInstruments(){
    	return instrumentList;
    }
    
    public InstrumentBean getInstrument(int pRecordNumber){
    	return instrumentList.get(pRecordNumber);
    }
    
    public FactoringUnitBean getFactoringUnit(Long pInId){
    	return factoringUnitMap.get(pInId);
    }
    
    public AppEntityBean getPurchaser(int pRecordNumber){
    	InstrumentBean lInstrumentBean = instrumentList.get(pRecordNumber);
    	return entityMap.get(lInstrumentBean.getPurchaser());
    }

    public AppEntityBean getSupplier(int pRecordNumber){
    	InstrumentBean lInstrumentBean = instrumentList.get(pRecordNumber);
    	return entityMap.get(lInstrumentBean.getSupplier());
    }
    
    public AppEntityBean getFiancier(){
    	return entityMap.get(cersaiFileBean.getFinancier());
    }
    
    public CompanyDetailBean getCDBeanPurchaser(int pRecordNumber){
    	InstrumentBean lInstrumentBean = instrumentList.get(pRecordNumber);
    	return companyMap.get(lInstrumentBean.getPurchaser());
    }

    public CompanyDetailBean getCDBeanSupplier(int pRecordNumber){
    	InstrumentBean lInstrumentBean = instrumentList.get(pRecordNumber);
    	return companyMap.get(lInstrumentBean.getSupplier());
    }
    
    public  CompanyDetailBean getCDBeanFiancier(){
    	return companyMap.get(cersaiFileBean.getFinancier());
    }

    public CompanyDetailBean getCDBean(int pRecordNumber, CersaiEntityType pType){
    	InstrumentBean lInstrumentBean = instrumentList.get(pRecordNumber);
    	if(CersaiEntityType.Assignor.equals(pType)){
        	return companyMap.get(lInstrumentBean.getSupplier());
    	}else if(CersaiEntityType.Debtor.equals(pType)){
        	return companyMap.get(lInstrumentBean.getPurchaser());
    	}else if(CersaiEntityType.Factor.equals(pType) || CersaiEntityType.Recivable_Owner.equals(pType)){
        	return companyMap.get(cersaiFileBean.getFinancier());
    	}
    	return null;
    }

    //
    //**************************** RECEIVABLE OWNER = SELLERS ****************************************
    //**************************** ASSIGNOR = SELLERS ****************************************
    //**************************** DEBTOR = PURCHASER ****************************************
    //**************************** FACTOR = FINANCIER ****************************************
    //
    public String getCersaiEntityType(int pRecordNumber, CersaiEntityType pType){
    	CompanyDetailBean lCompanyDetailBean = getCDBean(pRecordNumber, pType);
    	// 6 - ASSIGNOR_TYPE - CONSTANT. (Values are IND, COM, HUF, LLP, PAF, PRF, TRS) - MANDATORY
    	// 6 - This indicates the type of assignor. (Values are IND, COM, HUF, COS, LLP, PAF, PRF, TRS)
    	// 8 - DEBTOR_TYPE - CONSTANT. (Values are IND, COM, HUF, LLP, PAF, PRF, TRS) - MANDATORY
    	// 8 - This indicates the type of debtor (Values are IND, COM, HUF, COS, LLP, PAF, PRF, TRS)
    	if(CersaiEntityType.Recivable_Owner.equals(pType) ||
    			CersaiEntityType.Debtor.equals(pType) || CersaiEntityType.Assignor.equals(pType) ){
        	// 4 - RECEIVABLE_OWNER_TYPE - CONSTANT.(Values 'S', 'C','J') - MANDATORY(IF ENCUMBRANCE_STATUS is N)
        	// 4 - This indicates the type of Receivable owner. "S" indicates Single Bank, "C" stands for Consortium, "J" stands for "Joint Charge". Blank if ENCUMBRANCE_STATUS is "Y".    		
//    		PROP	Proprietorship
//    		PART	Partnership
//    		PRIV	Private Limited Company
//    		PUB	Public Limited Company
//    		TRST	Trust
//    		HUF	HUF
//    		GOVT	Government Department
//    		LLP	Limited Liability Partnership
    		return CersaiFileGenerator.getMappedConsitution(lCompanyDetailBean.getConstitution());
    	}
    	return "";
    }
    public String getPartnerCount(int pRecordNumber, CersaiEntityType pType){
    	CompanyDetailBean lCompanyDetailBean = getCDBean(pRecordNumber, pType);
    	//TODO: No PAF for Seller in Constitution
		String lMappedConstituion = CersaiFileGenerator.getMappedConsitution(lCompanyDetailBean.getConstitution());
    	if(CersaiEntityType.Recivable_Owner.equals(pType)){
        	// 5 - RECEIVABLE_OWNER_COUNT - NUMBER(3) - MANDATORY(IF RECEIVABLE_OWNER_TYPE  IS "J" 
        	// 5 - This indicates the number of Receivable owners. This needs to be defined if RECEIVABLE_OWNER_TYPE is "J", otherwise blank.
    		Long lCount = getPartnerCount(lCompanyDetailBean.getId());
    		if(lCount!= null){
    			return lCount.toString();
    		}
    	}else if(CersaiEntityType.Assignor.equals(pType)){
        	// 7 - NO_OF_ASSIGNOR_PARTNERS - NUMBER(3) - MANDATORY(IF ASSIGNOR_TYPE  IS "LLP" or "PAF" 
        	// 7 - This indicates the number of partners for ASSIGNOR_TYPE "LLP" and "PAF", otherwise blank.
        	if("LLP".equals(lMappedConstituion) || "PAF".equals(lMappedConstituion) ){
        		Long lCount = getPartnerCount(lCompanyDetailBean.getId());
        		if(lCount!= null){
        			return lCount.toString();
        		}
        	}
    	}else if(CersaiEntityType.Debtor.equals(pType)){
        	// 9 - NO_OF_DEBTOR_PARTNERS - NUMBER(3) - MANDATORY(IF DEBTOR_TYPE  IS "LLP" or "PAF" 
        	// 9 - This indicates the number of partners for DEBTOR_TYPE "LLP" and "PAF", otherwise blank.
        	//TODO: No PAF for Seller in Constitution
        	if("LLP".equals(lMappedConstituion) || "PAF".equals(lMappedConstituion)) {
        		Long lCount = getPartnerCount(lCompanyDetailBean.getId());
        		if(lCount!= null){
        			return lCount.toString();
        		}
        	}
    	}
    	return "0";
    }
    private List<CompanyContactBean> getCompanyContacts(Long pCdId){
    	if(pCdId!=null){
    		List<CompanyContactBean> lList = companyContactMap.get(pCdId);
    		if(lList != null){
    			return lList;
    		}
    	}
    	return null;
    }
    private Long getPartnerCount(Long pCdId){
    	if(pCdId!=null){
    		List<CompanyContactBean> lList = getCompanyContacts(pCdId);
    		if(lList != null){
    			int lCount = 0;
    			for(CompanyContactBean lBean:lList){
    				if(DESIGNATION_PARTNER.equals(lBean.getDesignation())){
    					lCount++;
    				}
    			}
    			return new Long(lCount);
    		}
    	}
    	return null;
    }
    public List<CompanyContactBean> getPartners(Long pCdId){
    	if(pCdId!=null){
    		List<CompanyContactBean> lTmpList = null;
    		lTmpList = companyContactMap.get(pCdId);
    		if(lTmpList != null && lTmpList.size() > 0){
    			List<CompanyContactBean> lList = new ArrayList<CompanyContactBean>();
    			for(CompanyContactBean lBean : lTmpList){
    				if(DESIGNATION_PARTNER.equals(lBean.getDesignation())){
    					lList.add(lBean);
    				}
    			}
    			return lList;
    		}
    	}
    	return null;
    }

    public String getDebtorAgreementSignatories(int pRecordNumber){
    	// 10 - NO_OF_AGREEMENT_SIGNATORIES - NUMBER(3) - MANDATORY
    	// 10 - This indicates the number of agreement signatories
    	return "1";
    }
    
    //**************************** OTHER FUCTIONS ****************************************
    public CompanyContactBean getLatestAuthorizedSignatory(Long pCdId){
    	if(pCdId!=null){
    		List<CompanyContactBean> lTmpList = null;
    		lTmpList = companyContactMap.get(pCdId);
    		if(lTmpList != null && lTmpList.size() > 0){
    			CompanyContactBean lLatestAuthSignatory = null;
    			for(CompanyContactBean lBean : lTmpList){
    				if(CommonAppConstants.Yes.Yes.equals(lBean.getAuthPer())){
    					if (lLatestAuthSignatory==null || 
    							(!Objects.isNull(lLatestAuthSignatory.getAuthPerAuthDate()) && lLatestAuthSignatory.getAuthPerAuthDate().before(lBean.getAuthPerAuthDate()) ) ) {
    						lLatestAuthSignatory = lBean;
    					}
    				}
    			}
    			return lLatestAuthSignatory;
    		}
    	}
    	return null;
    }
    
    public Map<String, List<InstrumentBean>>  getInstrumentMap(){
    	return instrumentMap;
    }
    
    public List<InstrumentBean>  getInstrumentList(String pPurSupKey){
    	return instrumentMap.get(pPurSupKey);
    }
    
    public BigDecimal  getTotalAmount(String pPurSupKey){
    	BigDecimal lAmount = BigDecimal.ZERO;
    	for  (InstrumentBean lBean : instrumentMap.get(pPurSupKey)) {
    		lAmount = lAmount.add(lBean.getNetAmount());
    	}
    	return lAmount;
    }
    
    public BigDecimal  getFuLeg1Intrest(String pPurSupKey){
    	BigDecimal lAmount = BigDecimal.ZERO;
    	for  (FactoringUnitBean lFUBean : factoringUnitMap.values()) {
    		if (pPurSupKey.equals(lFUBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lFUBean.getSupplier())) {
    			lAmount = lAmount.add(lFUBean.getInterest());
    		}
    	}
    	return lAmount;
    }

	public String getCersaiCodeForFinancier(String pCode) throws Exception {
		FinancierAuctionSettingBean lFinancierAuctionSettingBean = new FinancierAuctionSettingBean();
		lFinancierAuctionSettingBean.setFinancier(pCode);
		lFinancierAuctionSettingBean.setLevel(Level.Financier_Self);
		lFinancierAuctionSettingBean = financierAuctionSettingDAO.findBean(connection, lFinancierAuctionSettingBean);
		return lFinancierAuctionSettingBean.getCersaiCode();
	}
}
