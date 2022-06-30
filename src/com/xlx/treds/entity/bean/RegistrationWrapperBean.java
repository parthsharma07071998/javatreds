package com.xlx.treds.entity.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyLocationBean.LocationType;
import com.xlx.treds.entity.bo.CompanyBankDetailBO;
import com.xlx.treds.entity.bo.CompanyContactBO;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.entity.bo.CompanyShareEntityBO;
import com.xlx.treds.entity.bo.CompanyShareIndividualBO;

public class RegistrationWrapperBean
{
	public static Logger logger = Logger.getLogger(RegistrationWrapperBean.class);
	public static String ENTITY_NAME = "RegistrationWrapperBean";
	//
	//input parameters
	private ExecutionContext executionContext;
	private IAppUserBean appUserBean;
	private Long companyDetailId = null; //ComanyDetails.CDId=AppUser.AUId
	//data for generating output
	private CompanyDetailBean companyDetailBean; //1-29,
	private CompanyLocationBean regCompanyLocationBean;
	//30-33, Management=>ChiefPromoter,OtherPromoter,AuthOff,Admin
	private List<CompanyContactBean> contactChiefPromoters; 
	private List<CompanyContactBean> contactOtherPromoters; 
	private List<CompanyContactBean> contactAuthOfficials; 
	private CompanyContactBean contactAdmin; 
	private int chiefPromoterCount=0, otherPromoterCount=0, authOfficialCount=0, adminCount=0;
	//
	private List<CompanyLocationBean> companyLocationBeans; //34, Location/Branches
	private List<CompanyLocationBean> allCompanyLocationBeans; 
	private List<CompanyBankDetailBean> companyBankDetailBeans; //35, Banking
	private List<CompanyBankDetailBean> allCompanyBankDetailBeans;
	private int locationCount=0, bankDetailCount=0, otherBankDetailsCount;
	private CompanyBankDetailBean designatedBankDetails; //Appendix2 - Pt 5
	private List<CompanyBankDetailBean> otherBankDetailBeans; //Appendix2 - Pt 6
	//
	//
	private List<CompanyShareIndividualBean> companyShareIndividuals; 
	private List<CompanyShareEntityBean> companyShareEntities; 
	private List<CompanyContactBean> companyContactList;
	private int entityCount=0, individualCount=0;
	private boolean isProvisional;
	
	public CompanyDetailBean getCompanyDetails()
	{
		return companyDetailBean;
	}
	public List<CompanyContactBean> getChiefPromoters()
	{
		return contactChiefPromoters;
	}
	public List<CompanyContactBean> getOtherPromoters()
	{
		return contactOtherPromoters;
	}
	public List<CompanyContactBean> getAuthOfficials()
	{
		return contactAuthOfficials;
	}
	public CompanyContactBean getAdmin()
	{
		return contactAdmin;
	}
	public List<CompanyLocationBean> getLocationBranches()
	{
		return companyLocationBeans;
	}
	public List<CompanyBankDetailBean> getBankDetails()
	{
		return companyBankDetailBeans;
	}
	public List<CompanyBankDetailBean> getOtherBankDetails()
	{
		return otherBankDetailBeans;
	}
	public CompanyBankDetailBean getDesignatedBankDetails()
	{
		return designatedBankDetails;
	}
	public boolean showChiefPromoters()
	{
		return true;
	}
	public boolean showOtherPromoters()
	{
		return true;
	}
	public boolean showAuthOfficials()
	{
		return true;
	}
	public boolean showAdmin()
	{
		return true;
	}
	public boolean showLocations()
	{
		return true;
	}
	public boolean showBankingDetails()
	{
		return true;
	}
	public boolean showOtherBankingDetails()
	{
		return true;
	}
	
	public List<CompanyShareIndividualBean> getCompanyShareIndividuals()
	{
		return companyShareIndividuals;
	}
	public List<CompanyShareEntityBean> getCompanyShareEntities()
	{
		return companyShareEntities;
	}
	public boolean showShareIndivudals()
	{
		return true;
	}
	public boolean showShareEntity()
	{
		return true;
	}

	
	public RegistrationWrapperBean(ExecutionContext pExecutionContext, IAppUserBean pAppUserBean, Long pCompanyDetailId, boolean pIsProvisional)
	{
		executionContext = pExecutionContext;
		appUserBean = pAppUserBean;
		companyDetailId = pCompanyDetailId;
		isProvisional = pIsProvisional;
		//
		processData();
	}
	private void processData()
	{
		CompanyDetailBO lCompanyDetailBO = null;
		CompanyContactBO lCompanyContactBO = null;
		CompanyContactBean lCompanyContactBean = null;
		List<CompanyContactBean> lCompanyContactBeans = null;
		CompanyLocationBO lCompanyLocationBO = null;
		CompanyBankDetailBO lCompanyBankDetailBO = null;
		CompanyLocationBean lCompanyLocationBean = null;
		CompanyBankDetailBean lCompanyBankDetailBean = null;
		List<CompanyLocationBean> lCompanyLocationBeans = null;
		List<CompanyBankDetailBean> lCompanyBankDetailBeans = null;
		List<CompanyShareEntityBean> lCompanyShareEntityBeans = null;
		CompanyShareIndividualBean lCompanyShareIndividualBean = null;
		CompanyShareEntityBean lCompanyShareEntityBean = null;
		List<CompanyShareIndividualBean>lCompanyShareIndividualBeans = null;
		CompanyShareEntityBO lCompanyShareEntityBO = null;
		CompanyShareIndividualBO lCompanyShareIndividualBO = null;
        try
		{
        	//Company Details
    		lCompanyDetailBO = new CompanyDetailBO();
    		companyDetailBean = new CompanyDetailBean();
    		companyDetailBean.setId(companyDetailId);
    		companyDetailBean.setIsProvisional(isProvisional);
			companyDetailBean = lCompanyDetailBO.findBean(executionContext, companyDetailBean, appUserBean, false);
			//
			
			//share holders
			//entities
			lCompanyShareEntityBO = new CompanyShareEntityBO();
			companyShareEntities = new ArrayList<CompanyShareEntityBean>();
			lCompanyShareEntityBean = new CompanyShareEntityBean();
			lCompanyShareEntityBean.setCdId(companyDetailId);
			lCompanyShareEntityBean.setIsProvisional(isProvisional);
			lCompanyShareEntityBeans = lCompanyShareEntityBO.findList(executionContext, lCompanyShareEntityBean, null, appUserBean);
			if(lCompanyShareEntityBeans!=null)
			{
				for(CompanyShareEntityBean lCSEBean : lCompanyShareEntityBeans)
				{
					companyShareEntities.add(lCSEBean);
					entityCount++;
				}
			}
			//to add blank beans to the list so that blank gets printed if none found or if count is odd
			int lBlankCount=0;
			int[] lActualCount = new int[] {entityCount};
			ArrayList<List<CompanyShareEntityBean>> lEntityLists = new ArrayList<List<CompanyShareEntityBean>>();
			lEntityLists.add(companyShareEntities);
			for(int lPtr=0; lPtr < lActualCount.length; lPtr++)
			{
				if(lActualCount[lPtr]==0 || (lActualCount[lPtr]%2) > 0)
				{
					lBlankCount = (lActualCount[lPtr]==0?2:(lActualCount[lPtr]%2)); //add 2 if blank
					for(int lPtr2=0; lPtr2<lBlankCount; lPtr2++)
						lEntityLists.get(lPtr).add(new CompanyShareEntityBean());
				}
			}
			//individuals
			lCompanyShareIndividualBO = new CompanyShareIndividualBO();
			companyShareIndividuals = new ArrayList<CompanyShareIndividualBean>();
			lCompanyShareIndividualBean = new CompanyShareIndividualBean();
			lCompanyShareIndividualBean.setCdId(companyDetailId);
			lCompanyShareIndividualBean.setIsProvisional(isProvisional);
			lCompanyShareIndividualBeans = lCompanyShareIndividualBO.findList(executionContext, lCompanyShareIndividualBean, null, appUserBean);
			if(lCompanyShareEntityBeans!=null)
			{
				for(CompanyShareIndividualBean lCSIBean : lCompanyShareIndividualBeans)
				{
					companyShareIndividuals.add(lCSIBean);
					individualCount++;
				}
			}
			//to add blank beans to the list so that blank gets printed if none found or if count is odd
			lBlankCount=0;
			lActualCount = new int[] {individualCount};
			ArrayList<List<CompanyShareIndividualBean>> lIndivList = new ArrayList<List<CompanyShareIndividualBean>>();
			lIndivList.add(companyShareIndividuals);
			for(int lPtr=0; lPtr < lActualCount.length; lPtr++)
			{
				if(lActualCount[lPtr]==0 || (lActualCount[lPtr]%2) > 0)
				{
					lBlankCount = (lActualCount[lPtr]==0?2:(lActualCount[lPtr]%2)); //add 2 if blank
					for(int lPtr2=0; lPtr2<lBlankCount; lPtr2++)
						lIndivList.get(lPtr).add(new CompanyShareIndividualBean());
				}
			}
			//
			
			
			//Company Contacts
			lCompanyContactBO = new CompanyContactBO();
			lCompanyContactBean = new CompanyContactBean();
			lCompanyContactBean.setCdId(companyDetailId);
			lCompanyContactBean.setIsProvisional(isProvisional);
			lCompanyContactBeans = lCompanyContactBO.findList(executionContext, lCompanyContactBean, null, appUserBean);
			contactChiefPromoters = new ArrayList<CompanyContactBean>();
			contactOtherPromoters = new ArrayList<CompanyContactBean>();
			contactAuthOfficials = new ArrayList<CompanyContactBean>();
			contactAdmin = null;
			companyLocationBeans = new ArrayList<CompanyLocationBean>();
			companyBankDetailBeans = new ArrayList<CompanyBankDetailBean>();
			designatedBankDetails = new CompanyBankDetailBean();
			otherBankDetailBeans = new ArrayList<CompanyBankDetailBean>();
			chiefPromoterCount=0;
			otherPromoterCount=0;
			authOfficialCount=0;
			adminCount=0;
			
			if(lCompanyContactBeans!=null)
			{	
				companyContactList = lCompanyContactBeans;
				for(CompanyContactBean lCCBean : lCompanyContactBeans)
				{
					if(CommonAppConstants.Yes.Yes.equals(lCCBean.getChiefPromoter()))
					{
						contactChiefPromoters.add(lCCBean);
						chiefPromoterCount++;
					}
					else if(CommonAppConstants.Yes.Yes.equals(lCCBean.getPromoter()))
					{
						contactOtherPromoters.add(lCCBean);
						otherPromoterCount++;
					}
					
					if(CommonAppConstants.Yes.Yes.equals(lCCBean.getAuthPer()))
					{
						contactAuthOfficials.add(lCCBean);
						authOfficialCount++;
					}
					if(CommonAppConstants.Yes.Yes.equals(lCCBean.getAdmin()))
					{
						contactAdmin = lCCBean;
						adminCount++;
					}
				}
			}
			//to add blank beans to the list so that blank gets printed if none found or if count is odd
			lBlankCount=0;
			lActualCount = new int[] {chiefPromoterCount, otherPromoterCount, authOfficialCount };
			ArrayList<List<CompanyContactBean>> lCCLists = new ArrayList<List<CompanyContactBean>>();
			lCCLists.add(contactChiefPromoters);
			lCCLists.add(contactOtherPromoters);
			lCCLists.add(contactAuthOfficials);
			for(int lPtr=0; lPtr < lActualCount.length; lPtr++)
			{
				if(lActualCount[lPtr]==0 || (lActualCount[lPtr]%2) > 0)
				{
					lBlankCount = (lActualCount[lPtr]==0?2:(lActualCount[lPtr]%2)); //add 2 if blank
					for(int lPtr2=0; lPtr2<lBlankCount; lPtr2++)
						lCCLists.get(lPtr).add(new CompanyContactBean());
				}
			}
			if(adminCount==0) contactAdmin = new CompanyContactBean();
			//
			//Company Location/Branches
			lCompanyLocationBO = new CompanyLocationBO();
			lCompanyLocationBean = new CompanyLocationBean();
			lCompanyLocationBean.setCdId(companyDetailId);
			lCompanyLocationBean.setIsProvisional(isProvisional);
			lCompanyLocationBeans = lCompanyLocationBO.findList(executionContext, lCompanyLocationBean, null, appUserBean);
			allCompanyLocationBeans = new ArrayList<CompanyLocationBean>();
			if (!Objects.isNull(lCompanyLocationBeans)) {
				allCompanyLocationBeans.addAll(lCompanyLocationBeans);
			}
			companyLocationBeans = new ArrayList<CompanyLocationBean>();
			locationCount = 0;
			if(lCompanyLocationBeans!=null)
			{
				for( CompanyLocationBean lCLBean : lCompanyLocationBeans)
				{
					//Get Reg Address
					if(lCLBean!=null && LocationType.RegOffice.equals(lCLBean.getLocationType())){
						regCompanyLocationBean = lCLBean;
						continue;
					}
					companyLocationBeans.add(lCLBean);
					locationCount++;
				}
			}
			lBlankCount=0;
			lActualCount = new int[] {locationCount};
			ArrayList<List<CompanyLocationBean>> lCLLists = new ArrayList<List<CompanyLocationBean>>();
			lCLLists.add(companyLocationBeans);
			for(int lPtr=0; lPtr < lActualCount.length; lPtr++)
			{
				if(lActualCount[lPtr]==0 || (lActualCount[lPtr]%2) > 0)
				{
					lBlankCount = (lActualCount[lPtr]==0?2:(lActualCount[lPtr]%2)); //add 2 if blank
					for(int lPtr2=0; lPtr2<lBlankCount; lPtr2++)
						lCLLists.get(lPtr).add(new CompanyLocationBean());
				}
			}	
			//
			//Company Bank Details
			lCompanyBankDetailBO = new CompanyBankDetailBO();
			lCompanyBankDetailBean = new CompanyBankDetailBean();
			lCompanyBankDetailBean.setCdId(companyDetailId);
			lCompanyBankDetailBean.setIsProvisional(isProvisional);
			lCompanyBankDetailBeans = lCompanyBankDetailBO.findList(executionContext, lCompanyBankDetailBean, null,appUserBean);
			allCompanyBankDetailBeans = new ArrayList<CompanyBankDetailBean>();
			if (!Objects.isNull(lCompanyBankDetailBeans)) {
				allCompanyBankDetailBeans.addAll(lCompanyBankDetailBeans);
			}
			companyBankDetailBeans = new ArrayList<CompanyBankDetailBean>();
			bankDetailCount = 0;
			if(lCompanyBankDetailBeans!=null)
			{
				for( CompanyBankDetailBean lCDBean : lCompanyBankDetailBeans)
				{
					companyBankDetailBeans.add(lCDBean);
					bankDetailCount++;
					if(CommonAppConstants.Yes.Yes.equals(lCDBean.getDefaultAccount()))
						designatedBankDetails = lCDBean;
					if( (lCDBean.getDefaultAccount()==null || CommonAppConstants.YesNo.No.equals(lCDBean.getDefaultAccount())) &&
						(lCDBean.getLeadBank()==null || CommonAppConstants.YesNo.No.equals(lCDBean.getLeadBank())) )
					{
						otherBankDetailBeans.add(lCDBean);
						otherBankDetailsCount++;
					}
				}
			}
			lBlankCount=0;
			lActualCount = new int[] {bankDetailCount,otherBankDetailsCount};
			ArrayList<List<CompanyBankDetailBean>> lCBDLists = new ArrayList<List<CompanyBankDetailBean>>();
			lCBDLists.add(companyBankDetailBeans);
			lCBDLists.add(otherBankDetailBeans);
			for(int lPtr=0; lPtr < lActualCount.length; lPtr++)
			{
				if(lActualCount[lPtr]==0 || (lActualCount[lPtr]%2) > 0)
				{
					lBlankCount = (lActualCount[lPtr]==0?2:(lActualCount[lPtr]%2)); //add 2 if blank
					for(int lPtr2=0; lPtr2<lBlankCount; lPtr2++)
						lCBDLists.get(lPtr).add(new CompanyBankDetailBean());
				}
			}			
			//
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getApplicantTypeDesc()
	{
		if(companyDetailBean != null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_ENTITYTYPE, companyDetailBean.getType());
		return "";
	}
	
	public String getConstitutionDesc()
	{
		if(companyDetailBean != null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CONSTITUTION, companyDetailBean.getConstitution());
		return "";
	}
	
	public String getEntityConstitutionDesc(CompanyShareEntityBean lCompanyShareEntityBean)
	{
		if( lCompanyShareEntityBean != null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CONSTITUTION, lCompanyShareEntityBean.getConstitution());
		return "";
	}
	
	public String getFinancierCategoryDesc()
	{
		if(companyDetailBean != null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_FINANCIERCATEGORY, companyDetailBean.getFinancierCategory());
		return "";
	}
	
	public String getStateDesc(String pState)
	{
		if(CommonUtilities.hasValue(pState))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_STATE, pState);
		return "";
	}
	
	public String getStateGSTDesc(String pState)
	{
		if(CommonUtilities.hasValue(pState))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_STATE_GST, pState);
		return "";
	}

	public String getCountryDesc(String pCountry)
	{
		if(CommonUtilities.hasValue(pCountry))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_COUNTRY, pCountry);
		return "";
	}

	public String getRegStateDesc()
	{
		if(regCompanyLocationBean != null)
			return getStateDesc(regCompanyLocationBean.getState());
		return "";
	}

	public String getCorStateDesc()
	{
		if(companyDetailBean != null)
			return getStateGSTDesc(companyDetailBean.getCorState());
		return "";
	}
	
	public String getRegCountryDesc()
	{
		if(regCompanyLocationBean != null)
			return getStateGSTDesc(regCompanyLocationBean.getCountry());
		return "";
	}

	public String getCorCountryDesc()
	{
		if(companyDetailBean != null)
			return getCountryDesc(companyDetailBean.getCorCountry());
		return "";
	}
	
	public String getRegDistrict()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getDistrict();
		return "";
	}

	public String getCorDistrict()
	{
		if(companyDetailBean != null)
			return companyDetailBean.getCorDistrict();
		return "";
	}
	
	public String getRegName()
	{
		String lName = "";
		if(regCompanyLocationBean!=null)
		{
			if(CommonUtilities.hasValue(regCompanyLocationBean.getSalutation())) lName += regCompanyLocationBean.getSalutation();
			if(CommonUtilities.hasValue(regCompanyLocationBean.getFirstName())) lName += " " + regCompanyLocationBean.getFirstName();
			if(CommonUtilities.hasValue(regCompanyLocationBean.getMiddleName())) lName += " " + regCompanyLocationBean.getMiddleName();
			if(CommonUtilities.hasValue(regCompanyLocationBean.getLastName())) lName +=" " + regCompanyLocationBean.getLastName();
		}
		return lName.trim();
	}
	
	public String getCorName()
	{
		String lName = "";
		if(companyDetailBean!=null)
		{
			if(CommonUtilities.hasValue(companyDetailBean.getCorSalutation())) lName += companyDetailBean.getCorSalutation();
			if(CommonUtilities.hasValue(companyDetailBean.getCorFirstName())) lName += " " + companyDetailBean.getCorFirstName();
			if(CommonUtilities.hasValue(companyDetailBean.getCorMiddleName())) lName += " " + companyDetailBean.getCorMiddleName();
			if(CommonUtilities.hasValue(companyDetailBean.getCorLastName())) lName +=" " + companyDetailBean.getCorLastName();
		}
		return lName.trim();
	}
	public String getExistenceYearsDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getExistenceYears()!= null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_YEARSINBUSINESS,companyDetailBean.getExistenceYears().toString());
		return "";
	}
	public String getIndustryDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getIndustry()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_INDUSTRY, companyDetailBean.getIndustry());
		return "";
	}
	public String getEntityIndustryDesc(CompanyShareEntityBean pCompanyShareEntityBean)
	{
		if(pCompanyShareEntityBean != null && pCompanyShareEntityBean.getIndustry()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_INDUSTRY, pCompanyShareEntityBean.getIndustry());
		return "";
	}
	public String getSubSegmentDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getIndustry()!=null && companyDetailBean.getSubSegment()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_SUBSEGMENT, companyDetailBean.getIndustry() +'.'+ companyDetailBean.getSubSegment());
		return "";
	}
	public String getEntitySubSegmentDesc(CompanyShareEntityBean pCompanyShareEntityBean)
	{
		if(pCompanyShareEntityBean != null && pCompanyShareEntityBean.getIndustry()!=null && pCompanyShareEntityBean.getSubSegment()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_SUBSEGMENT, pCompanyShareEntityBean.getIndustry() +'.'+ pCompanyShareEntityBean.getSubSegment());
		return "";
	}
	public String getSectorDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getSector()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_SECTOR, companyDetailBean.getSector());
		return "";
	}
	public String getExportOrientationDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getExportOrientation()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_EXPORTORIENTATION, companyDetailBean.getExportOrientation());
		return "";
	}
	public String getCurrencyDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getExportOrientation()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CURRENCY, companyDetailBean.getCurrency());
		return "";
	}
	
	public String getMsmeStatus()
	{
		if(companyDetailBean != null && companyDetailBean.getMsmeStatus()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_MSMESTATUS, companyDetailBean.getMsmeStatus());
		return "";
	}
	public String getMsmeRegTypeDesc()
	{
		if(companyDetailBean != null && companyDetailBean.getMsmeRegType()!=null)
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_MSMEREGTYPE, companyDetailBean.getMsmeRegType());
		return "";
	}
	public String getContactFullName(CompanyContactBean pCompanyContactBean)
	{
		return TredsHelper.getInstance().getContactFullName(pCompanyContactBean);
	}
	public String getCercaiFullName(CompanyContactBean pCompanyContactBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyContactBean.getCersaiFirstName()) ||
				CommonUtilities.hasValue(pCompanyContactBean.getCersaiMiddleName()) ||
				CommonUtilities.hasValue(pCompanyContactBean.getCersaiLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyContactBean.getCersaiSalutation()))
				lName.append(pCompanyContactBean.getCersaiSalutation());
			if(CommonUtilities.hasValue(pCompanyContactBean.getCersaiFirstName()))
				lName.append(" ").append(pCompanyContactBean.getCersaiFirstName());
			if(CommonUtilities.hasValue(pCompanyContactBean.getCersaiMiddleName()))
				lName.append(" ").append(pCompanyContactBean.getCersaiMiddleName());
			if(CommonUtilities.hasValue(pCompanyContactBean.getCersaiLastName()))
				lName.append(" ").append(pCompanyContactBean.getCersaiLastName());
		}
		return lName.toString();
	}
	
	public String getShareEntityFullName(CompanyShareEntityBean pCompanyShareEntityBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyShareEntityBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyShareEntityBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyShareEntityBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getSalutation()))
				lName.append(pCompanyShareEntityBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getFirstName()))
				lName.append(" ").append(pCompanyShareEntityBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getMiddleName()))
				lName.append(" ").append(pCompanyShareEntityBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyShareEntityBean.getLastName()))
				lName.append(" ").append(pCompanyShareEntityBean.getLastName());
		}
		return lName.toString();
	}
	
	public String getShareIndividualFullName(CompanyShareIndividualBean pCompanyShareIndividualBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getSalutation()))
				lName.append(pCompanyShareIndividualBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFirstName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getMiddleName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getLastName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getLastName());
		}
		return lName.toString();
	}
	
	public String getShareIndividualFamilyFullName(CompanyShareIndividualBean pCompanyShareIndividualBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyFirstName()) ||
				CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyMiddleName()) ||
				CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilySalutation()))
				lName.append(pCompanyShareIndividualBean.getFamilySalutation());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyFirstName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getFamilyFirstName());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyMiddleName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getFamilyMiddleName());
			if(CommonUtilities.hasValue(pCompanyShareIndividualBean.getFamilyLastName()))
				lName.append(" ").append(pCompanyShareIndividualBean.getFamilyLastName());
		}
		return lName.toString();
	}
	
	public String getLocationContactFullName(CompanyLocationBean pCompanyLocationBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyLocationBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyLocationBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyLocationBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyLocationBean.getSalutation()))
				lName.append(pCompanyLocationBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyLocationBean.getFirstName()))
				lName.append(" ").append(pCompanyLocationBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyLocationBean.getMiddleName()))
				lName.append(" ").append(pCompanyLocationBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyLocationBean.getLastName()))
				lName.append(" ").append(pCompanyLocationBean.getLastName());
		}
		return lName.toString();
	}
	public String getBankRealMgrContactFullName(CompanyBankDetailBean pCompanyBankDetailBean)
	{
		StringBuilder lName = new StringBuilder();
		
		if(CommonUtilities.hasValue(pCompanyBankDetailBean.getFirstName()) ||
				CommonUtilities.hasValue(pCompanyBankDetailBean.getMiddleName()) ||
				CommonUtilities.hasValue(pCompanyBankDetailBean.getLastName()) )
		{
			if(CommonUtilities.hasValue(pCompanyBankDetailBean.getSalutation()))
				lName.append(pCompanyBankDetailBean.getSalutation());
			if(CommonUtilities.hasValue(pCompanyBankDetailBean.getFirstName()))
				lName.append(" ").append(pCompanyBankDetailBean.getFirstName());
			if(CommonUtilities.hasValue(pCompanyBankDetailBean.getMiddleName()))
				lName.append(" ").append(pCompanyBankDetailBean.getMiddleName());
			if(CommonUtilities.hasValue(pCompanyBankDetailBean.getLastName()))
				lName.append(" ").append(pCompanyBankDetailBean.getLastName());
		}
		return lName.toString();
	}
	public String getDesignationDesc(String pDesignation)
	{
		if(CommonUtilities.hasValue(pDesignation) && companyDetailBean!=null && CommonUtilities.hasValue(companyDetailBean.getConstitution()))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_DESIGNATION_CONTACT, pDesignation);
		return "";
	}
	public String getCPCategory(String pChiefPromoterCategory)
	{
		if(CommonUtilities.hasValue(pChiefPromoterCategory))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_PROMOTERCATEGORY, pChiefPromoterCategory);
		return "";
	}
	public String getBankDesc(String pBank)
	{
		if(CommonUtilities.hasValue(pBank))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_BANK, pBank);
		return "";
	}
	public String getBankAccountTypeDesc(IKeyValEnumInterface<String> pBankAccountType)
	{
		if(pBankAccountType!= null)
			return pBankAccountType.toString();
		return "";
	}
	public String getBankingFacilityTypeDesc(IKeyValEnumInterface<String> pBankingFacilityType)
	{
		if(pBankingFacilityType != null)
			return pBankingFacilityType.toString();
		return "";
	}
	public String getYesNoDesc(Yes pYesNo)
	{
		if(pYesNo != null)
		{
			if(pYesNo == Yes.Yes)
				return "Yes";
			else 
				return "-";
		}
		return "";
	}

    
	public static void main(String[] args) throws IOException
    {
		int lCount = 0;

		lCount = 0;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
		lCount = 1;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
		lCount = 2;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
		lCount = 3;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
		lCount = 4;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
		lCount = 5;
		System.out.println(" " + lCount + " % 2 = " + (lCount%2));
    }

	public String getRegLine1()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getLine1();
		return "";
	}
	
	public String getRegLine2()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getLine2();
		return "";
	}
	
	public String getRegLine3()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getLine3();
		return "";
	}
	
	public String getRegZipCode()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getZipCode();
		return "";
	}
	
	public String getRegCity()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getCity();
		return "";
	}
	
	public String getRegTelephone()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getTelephone();
		return "";
	}
	
	public String getRegMobile()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getMobile();
		return "";
	}
	
	public String getRegFax()
	{
		if(regCompanyLocationBean != null)
			return getStateDesc(regCompanyLocationBean.getFax());
		return "";
	}
	
	public String getRegEmail()
	{
		if(regCompanyLocationBean != null)
			return regCompanyLocationBean.getEmail();
		return "";
	}
	public String getRegOfficeGSTN(){
		if(regCompanyLocationBean != null){
			return regCompanyLocationBean.getGstn();
		}
		return "";
	}
	
	public List<CompanyContactBean> getCompanyContactList (){
		return companyContactList;
	}
	
	public List<CompanyLocationBean> getAllLocationList (){
		return allCompanyLocationBeans;
	}
	
	public List<CompanyBankDetailBean> getAllBankList (){
		return allCompanyBankDetailBeans;
	}
	
}
