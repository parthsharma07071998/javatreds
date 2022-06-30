package com.xlx.treds.other.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.other.bean.IUploadFileBean;
import com.xlx.treds.other.bean.UploadFileBean;

public class UploadFileBO {
    
    private GenericDAO<UploadFileBean> uploadFileDAO;

    public UploadFileBO() {
        super();
        uploadFileDAO = new GenericDAO<UploadFileBean>(UploadFileBean.class);
    }
    
    public UploadFileBean findBean(ExecutionContext pExecutionContext, 
        UploadFileBean pFilterBean) throws Exception {
        UploadFileBean lUploadFileBean = uploadFileDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lUploadFileBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lUploadFileBean;
    }
    
    public List<IUploadFileBean> findList(ExecutionContext pExecutionContext, UploadFileBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	List<UploadFileBean> lUploadFileBeans = uploadFileDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    	List<IUploadFileBean> lReturnList = null;
    	if(lUploadFileBeans!=null){
    		lReturnList = new ArrayList<IUploadFileBean>();
			for(UploadFileBean lBean : lUploadFileBeans){
				IUploadFileBean lTmpBean = (IUploadFileBean)lBean;
				lReturnList.add(lTmpBean);
			}
    	}
    	return lReturnList;
    }
    
    public void save(ExecutionContext pExecutionContext, IUploadFileBean pUploadFileBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        if (!StringUtils.isNotEmpty(pUploadFileBean.getStorageFileName())){
        	throw new CommonBusinessException("Please upload a file.");
        }
        if (pNew) {
        	int index = StringUtils.indexOf(pUploadFileBean.getStorageFileName(), ".");
        	pUploadFileBean.setFileName(StringUtils.substring(pUploadFileBean.getStorageFileName(), index+1));
            pUploadFileBean.setRecordCreator(pUserBean.getId());
            uploadFileDAO.insert(lConnection, (UploadFileBean) pUploadFileBean);
        } else {
        	UploadFileBean lOldUploadFileBean = findBean(pExecutionContext, (UploadFileBean)pUploadFileBean);
            int index = StringUtils.indexOf(pUploadFileBean.getStorageFileName(), ".");
            pUploadFileBean.setFileName(StringUtils.substring(pUploadFileBean.getStorageFileName(), index+1));
            pUploadFileBean.setRecordUpdator(pUserBean.getId());
            if (uploadFileDAO.update(lConnection, (UploadFileBean)pUploadFileBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, IUploadFileBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        UploadFileBean lUploadFileBean = findBean(pExecutionContext, (UploadFileBean)pFilterBean);
        lUploadFileBean.setRecordUpdator(pUserBean.getId());
        uploadFileDAO.delete(lConnection, lUploadFileBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public static void main(String[] args){
//        BeanMetaFactory.createInstance(null);
//    	RegistrationFilesBean lRegistrationBean = new RegistrationFilesBean();
//    	lRegistrationBean.setEntityType("NNY");
//    	lRegistrationBean.setConstitution("PROP");
//    	lRegistrationBean.setFileName("ABC.txt");
//    	lRegistrationBean.setStorageFileName("something.ABC.txt");
//    	lRegistrationBean.setRecordCreator(new Long(0));
//    	lRegistrationBean.setKey(lRegistrationBean.getEntityType()+CommonConstants.KEY_SEPARATOR+lRegistrationBean.getConstitution());
//        GenericDAO<UploadFileBean> lUploadFileDAO = new GenericDAO<UploadFileBean>(UploadFileBean.class);
//        try {
//			lUploadFileDAO.insert(DBHelper.getInstance().getConnection(), lRegistrationBean);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    
}
