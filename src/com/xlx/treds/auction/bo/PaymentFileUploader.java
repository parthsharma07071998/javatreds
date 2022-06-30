package com.xlx.treds.auction.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.treds.auction.bean.IPaymentInterface;
import com.xlx.treds.auction.bean.PaymentInterfaceFactory;

public class PaymentFileUploader extends BaseFileUploader {
    public static final String FILE_TYPE = "PAYRET";
    
    public PaymentFileUploader() {
        super();
    }
    
    @Override
    public int getHeaderCount() {
        return 0;
    }
    
    @Override
    public String getFileStore() {
        return FILE_TYPE;
    }
    
    @Override
    public boolean buildReturnFile() {
        return false;
    }
    
    @Override
    public void onStart(FileUploadBean pFileUploadBean) throws Exception {
        super.onStart(pFileUploadBean);
        List<String> lRecords = new ArrayList<String>();
        pFileUploadBean.setContext(lRecords);
    }
    
    @Override
    public boolean onData(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        if (StringUtils.isNotBlank(pRecord)) {
            List<String> lRecords = (List<String>)pFileUploadBean.getContext();
            lRecords.add(pRecord);
        }
        return true;
    }
    
    @Override
    public void onEndOfFile(FileUploadBean pFileUploadBean) throws Exception {
    	IPaymentInterface lPaymentImpl = PaymentInterfaceFactory.getPaymentInterface("NPCI");
    	lPaymentImpl.uploadReturnFile(pFileUploadBean);
    }
}
