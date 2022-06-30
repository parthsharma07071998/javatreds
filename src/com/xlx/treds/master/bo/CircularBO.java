package com.xlx.treds.master.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.master.bean.CircularBean;
import com.xlx.treds.user.bean.AppUserBean;

public class CircularBO {
    
    private GenericDAO<CircularBean> circularDAO;

    public CircularBO() {
        super();
        circularDAO = new GenericDAO<CircularBean>(CircularBean.class);
    }
    
    public CircularBean findBean(ExecutionContext pExecutionContext, 
        CircularBean pFilterBean) throws Exception {
        CircularBean lCircularBean = circularDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCircularBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lCircularBean;
    }
    
    public List<CircularBean> findList(ExecutionContext pExecutionContext, CircularBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
    	if(lAppEntityBean.isPurchaser()){
    		pFilterBean.setPurchaser(CommonAppConstants.YesNo.Yes);
    	}else if(lAppEntityBean.isSupplier()){
    		pFilterBean.setSupplier(CommonAppConstants.YesNo.Yes);
    	}else if (lAppEntityBean.isFinancier()){
    		pFilterBean.setFinancier(CommonAppConstants.YesNo.Yes);
    	}
    	if(!lAppEntityBean.isPlatform()){
    		if(pUserBean!=null){
    			if(AppUserBean.Type.Admin.equals(((AppUserBean)pUserBean).getType())){
    				pFilterBean.setAdmin(CommonAppConstants.YesNo.Yes);
    			}else if(AppUserBean.Type.User.equals(((AppUserBean)pUserBean).getType())){
    				pFilterBean.setUser(CommonAppConstants.YesNo.Yes);
    			}
    		}
    	}
    	//set filter according to latest/non-latest/archived
    	if(pFilterBean.getTab()!=null){
    		if((new Long(3)).equals(pFilterBean.getTab())){
    			pFilterBean.setArchive(YesNo.Yes);
    		}else if((new Long(2)).equals(pFilterBean.getTab())){
    			pFilterBean.setArchive(YesNo.No);
    		}else if((new Long(1)).equals(pFilterBean.getTab())){
    			pFilterBean.setArchive(YesNo.No);
    		}
    	}else{
    		//whenever only list without tab is asked we assume that its first call ans show the latest+circular(non-archived)
			pFilterBean.setArchive(YesNo.No);
    	}
    	//TODO: for optimization - only for Latest and Circular tab - we will have to modify the query and compute the age in the query so as to filter them out.
    	StringBuilder lSql = new StringBuilder();
    	DBHelper lDbHelper = DBHelper.getInstance();
    	lSql.append(circularDAO.getListSql(pFilterBean, pColumnList));
    	if(pFilterBean.getFilterFromDate()!=null && pFilterBean.getFilterToDate()!=null){
    		lSql.append(" AND CIRDATE between ").append(lDbHelper.formatDate(pFilterBean.getFilterFromDate()));
    		lSql.append(" AND ").append(lDbHelper.formatDate(pFilterBean.getFilterToDate()));
    	}else{
    		if (pFilterBean.getFilterFromDate()!=null){
        		lSql.append(" AND CIRDATE >= ").append(lDbHelper.formatDate(pFilterBean.getFilterFromDate()));
        	}
        	if (pFilterBean.getFilterToDate()!=null){
        		lSql.append(" AND CIRDATE <= ").append(lDbHelper.formatDate(pFilterBean.getFilterToDate()));
        	}
    	}
    	lSql.append(" ORDER BY CIRDATE ");
    	List<CircularBean>  lList = circularDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1); 
    			//circularDAO.findList(pExecutionContext.getConnection(), pFilterBean, lTmpColumns, 0);
    	List<CircularBean>  lReturnList = null;
    	if(lList != null){
    		lReturnList = new ArrayList<CircularBean>();
    		for(CircularBean lBean : lList){
    			if(YesNo.Yes.equals(lBean.getArchive())){
    				lBean.setTab(new Long(3));
    			}
    			else if(lBean.getAge() < lBean.getDisplayAsNewForDays()){
    				lBean.setTab(new Long(1));
    			}
    			else{
    				lBean.setTab(new Long(2));
    			}
    			if(pFilterBean.getTab()!=null && pFilterBean.getTab().equals(lBean.getTab())){
        			lReturnList.add(lBean);
    			}else if (pFilterBean.getTab()==null && YesNo.No.equals(lBean.getArchive())){
        			lReturnList.add(lBean);
    			}
    		}
    	}
    	return lReturnList;
    }
    
    public void save(ExecutionContext pExecutionContext, CircularBean pCircularBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CircularBean lOldCircularBean = null;
        if (!StringUtils.isNotEmpty(pCircularBean.getStorageFileName())){
        	throw new CommonBusinessException("Please upload a file.");
        }
        if (pNew) {
        	int index = StringUtils.indexOf(pCircularBean.getStorageFileName(), ".");
        	pCircularBean.setFileName(StringUtils.substring(pCircularBean.getStorageFileName(), index+1));
        	pCircularBean.setRecordCreator(pUserBean.getId());
            circularDAO.insert(lConnection, pCircularBean);
        } else {
            lOldCircularBean = findBean(pExecutionContext, pCircularBean);
            int index = StringUtils.indexOf(pCircularBean.getStorageFileName(), ".");
            pCircularBean.setFileName(StringUtils.substring(pCircularBean.getStorageFileName(), index+1));
            pCircularBean.setRecordUpdator(pUserBean.getId());
            if (circularDAO.update(lConnection, pCircularBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, CircularBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        CircularBean lCircularBean = findBean(pExecutionContext, pFilterBean);
        lCircularBean.setRecordUpdator(pUserBean.getId());
        circularDAO.delete(lConnection, lCircularBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public void archive(Connection pConnection, List<String> pIds, YesNo pArchive,IAppUserBean pUserBean) throws Exception {
    	CircularBean lCircularBean = null;
    	for(String lId : pIds){
    		lCircularBean = new CircularBean();
    		lCircularBean.setId(Long.valueOf(lId));
    		lCircularBean.setArchive(pArchive);
    		lCircularBean.setRecordUpdator(pUserBean.getId());
    		circularDAO.update(pConnection, lCircularBean,CircularBean.FIELDGROUP_ARCHIVE);
    	}
    }
    
}
