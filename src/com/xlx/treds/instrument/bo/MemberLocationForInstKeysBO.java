
package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.MemberLocationForInstKeysBean;

public class MemberLocationForInstKeysBO {

	public static final Logger logger = LoggerFactory.getLogger(MemberLocationForInstKeysBO.class);
    
    private GenericDAO<MemberLocationForInstKeysBean> memberLocationForInstKeysDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;

    public MemberLocationForInstKeysBO() {
        super();
        memberLocationForInstKeysDAO = new GenericDAO<MemberLocationForInstKeysBean>(MemberLocationForInstKeysBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
    }
    
    public MemberLocationForInstKeysBean findBean(ExecutionContext pExecutionContext, 
        MemberLocationForInstKeysBean pFilterBean) throws Exception {
        MemberLocationForInstKeysBean lMemberLocationForInstKeysBean = memberLocationForInstKeysDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lMemberLocationForInstKeysBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lMemberLocationForInstKeysBean;
    }
    
    public List<MemberLocationForInstKeysBean> findList(ExecutionContext pExecutionContext, MemberLocationForInstKeysBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return memberLocationForInstKeysDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public List<Map<String, Object>> lov(ExecutionContext pExecutionContext, List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT CLID, CLGSTN,CDCODE CLLINE1,CLNAME FROM COMPANYLOCATIONS ");
		lSql.append(" JOIN COMPANYDETAILS ON (CLCDID = CDID )");
		lSql.append(" WHERE CLRECORDVERSION > 0 ");
		lSql.append(" AND CDRECORDVERSION > 0 ");
		lSql.append(" AND CDCODE = ").append(DBHelper.getInstance().formatString(pUserBean.getDomain()));
        List<CompanyLocationBean> lCompanyLocationBeans = companyLocationDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        if(lCompanyLocationBeans != null && !lCompanyLocationBeans.isEmpty()){
        	for(CompanyLocationBean lCompanyLocationBean : lCompanyLocationBeans){
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyLocationBean.getLine1() +CommonConstants.KEY_SEPARATOR + lCompanyLocationBean.getId()+CommonConstants.KEY_SEPARATOR+lCompanyLocationBean.getGstn());
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lCompanyLocationBean.getName() +" - "+lCompanyLocationBean.getGstn());
                lResults.add(lData);
        	}
        }
        return lResults;
        }
    
    public void save(ExecutionContext pExecutionContext, MemberLocationForInstKeysBean pMemberLocationForInstKeysBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        Map<String,MemberLocationForInstKeysBean> lMap = new HashMap<String,MemberLocationForInstKeysBean>();
        MemberLocationForInstKeysBean lBean = null;
        MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
        lFilterBean.setCode(pMemberLocationForInstKeysBean.getCode());
        List<MemberLocationForInstKeysBean> lList = memberLocationForInstKeysDAO.findList(lConnection, lFilterBean);
        String[] lKeyArr = null;
        if(!lList.isEmpty()){
        	for(MemberLocationForInstKeysBean lMemLocInstBean : lList){
        		lMap.put(lMemLocInstBean.getKey(), lMemLocInstBean);
        	}
        }
        if(pMemberLocationForInstKeysBean.getClIdList()!=null && !pMemberLocationForInstKeysBean.getClIdList().isEmpty()){
        	for(String lKey : pMemberLocationForInstKeysBean.getClIdList()){
            	lBean = new MemberLocationForInstKeysBean();
            	lBean.setCode(pMemberLocationForInstKeysBean.getCode());
            	lBean.setRecordCreator(pUserBean.getId());
            	lKeyArr = CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR);
            	Long lClId = new Long(lKeyArr[1]);
            	lBean.setClId(lClId);
            	lBean.setGstn(lKeyArr[2]);
            	if(lMap.containsKey(lKey)){
            		lMap.remove(lBean.getKey());
            	}else{
            		memberLocationForInstKeysDAO.insert(lConnection, lBean, MemberLocationForInstKeysBean.FIELDGROUP_INSERTDB); 
            	}
        	}
        }
        if(lMap!=null && !lMap.isEmpty()){
    		for(MemberLocationForInstKeysBean lMapBean:lMap.values()){
    			StringBuilder lSql = new StringBuilder();
    			lSql.append(" DELETE FROM MemberLocForInstKeys ");
    			lSql.append(" where MLKCODE = ").append(DBHelper.getInstance().formatString(lMapBean.getCode()));
    			lSql.append(" and MLKCLID = ").append(lMapBean.getClId());
    			lSql.append(" and MLKGSTN = ").append(DBHelper.getInstance().formatString(lMapBean.getGstn()));
    			
    			try(Statement lStatement = lConnection.createStatement()){
    				logger.info(lSql.toString());
    				lStatement.executeUpdate(lSql.toString());
    				
    			}catch(Exception ex){
    				logger.error("error in delete ",ex.getMessage() );
    			}
    		}
    	}
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, MemberLocationForInstKeysBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        MemberLocationForInstKeysBean lMemberLocationForInstKeysBean = findBean(pExecutionContext, pFilterBean);
        lMemberLocationForInstKeysBean.setRecordUpdator(pUserBean.getId());
        memberLocationForInstKeysDAO.delete(lConnection, lMemberLocationForInstKeysBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
