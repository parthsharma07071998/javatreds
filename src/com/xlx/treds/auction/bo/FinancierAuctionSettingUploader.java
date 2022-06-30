package com.xlx.treds.auction.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean.Level;
import com.xlx.treds.auction.bean.TenureWiseBaseRateBean;
import com.xlx.treds.user.bean.AppUserBean;

public class FinancierAuctionSettingUploader extends BaseFileUploader {
    public static final String SEPERATOR = ","; 
    public static final String FILE_TYPE = "FINAUCSET";
    private static final String FIELD_TENURE = "tenure";
    private static final String FIELD_BASERATE = "baseRate";
    
    private FinancierAuctionSettingBO financierAuctionSettingBO;
    private BeanMeta financierAuctionSettingBeanMeta;
    private BeanMeta tenureWiseBaseRateBeanMeta;
    private List<String> headerFieldList;
    private FinancierAuctionSettingBean.Level level;
    
    private int maxSlabCount;
    
    public FinancierAuctionSettingUploader() {
        super();
        financierAuctionSettingBO = new FinancierAuctionSettingBO();
        financierAuctionSettingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FinancierAuctionSettingBean.class);
        tenureWiseBaseRateBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(TenureWiseBaseRateBean.class);
        Integer lMaxSlabs = financierAuctionSettingBeanMeta.getFieldMap().get("baseRateList").getMaxItems();
        maxSlabCount = lMaxSlabs==null?20:lMaxSlabs.intValue();

    }
    
    @Override
    public int getHeaderCount() {
        return 1;
    }
    
    @Override
    public String getFileStore() {
        return FILE_TYPE;
    }
    
    @Override
    public boolean buildReturnFile() {
        return true;
    }

    @Override
    public void onStart(FileUploadBean pFileUploadBean) throws Exception {
        Map<String, String> lParameters = pFileUploadBean.getFormParameters();
        String lLevel = null;
        if (lParameters != null)
            lLevel = lParameters.get("level");
        if (StringUtils.isBlank(lLevel))
            throw new CommonBusinessException("Input parameter level missing");
        for (Level lFinSetLevel : FinancierAuctionSettingBean.Level.values()){
            if(lFinSetLevel.getCode().equals(lLevel)) {
                level = lFinSetLevel;
                break;
            }
        }
        if (level == null)
            throw new CommonBusinessException("Input parameter level is invalid");
    }

    @Override
    public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        headerFieldList = Arrays.asList(CommonUtilities.splitString(pRecord, SEPERATOR));
        Map<String, BeanFieldMeta> lFieldMap = financierAuctionSettingBeanMeta.getFieldMap();
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader)) {
                if (lHeader.startsWith(FIELD_TENURE) || lHeader.startsWith(FIELD_BASERATE)) {
                    String lStrIndex = lHeader.substring(lHeader.startsWith(FIELD_TENURE)?FIELD_TENURE.length():FIELD_BASERATE.length());
                    int lIntIdx = Integer.parseInt(lStrIndex); 
                    if ((lIntIdx > maxSlabCount) || (lIntIdx <= 0))
                        throw new Exception("Invalid field in header : " + lHeader);
                } else
                    throw new Exception("Invalid field in header : " + lHeader);
            }
        }
        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord).append(SEPERATOR).append("id").append(SEPERATOR);
        lResponseBuffer.append("uploadStatus").append(SEPERATOR).append("uploadMessage").append(FileUploaderFactory.RECORD_SEPARATOR);
    }
    
    @Override
    public boolean onData(int pIndex, String pRecord, FileUploadBean pFileUploadBean) {
        String[] lValues = CommonUtilities.splitString(pRecord, SEPERATOR);
        Map<String, Object> lMap = new HashMap<String, Object>();
        int lCount = headerFieldList.size()<lValues.length?headerFieldList.size():lValues.length;
        Map<String,Object>[] lSlabsList = new Map[maxSlabCount];
        String[] lLevel = new String[]{"N","N","N","N"};
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            if (StringUtils.isNotBlank(lValues[lPtr])){
                String lHeader = headerFieldList.get(lPtr); 
                if (lHeader.startsWith(FIELD_TENURE) || lHeader.startsWith(FIELD_BASERATE)) {
                    String lStrIndex = lHeader.substring(lHeader.startsWith(FIELD_TENURE)?FIELD_TENURE.length():FIELD_BASERATE.length());
                    int lIdx = Integer.parseInt(lStrIndex) - 1;
                    Map<String,Object> lSlabMap = lSlabsList[lIdx];
                    if (lSlabMap == null) {
                        lSlabMap = new HashMap<String, Object>();
                        lSlabsList[lIdx] = lSlabMap;
                    }
                    lSlabMap.put(lHeader.startsWith(FIELD_TENURE)?FIELD_TENURE:FIELD_BASERATE,lValues[lPtr]);
                } else {
                    lMap.put(headerFieldList.get(lPtr), lValues[lPtr]);
                    if(headerFieldList.get(lPtr).equalsIgnoreCase("financier")){
                    	if(CommonUtilities.hasValue(lValues[lPtr])){
                    		lLevel[0] = "Y";
                    	}
                    }else if(headerFieldList.get(lPtr).equalsIgnoreCase("purchaser")){
                    	if(CommonUtilities.hasValue(lValues[lPtr])){
                    		lLevel[1] = "Y";
                    	}
                    }else if(headerFieldList.get(lPtr).equalsIgnoreCase("supplier")){
                    	if(CommonUtilities.hasValue(lValues[lPtr])){
                    		lLevel[2] = "Y";
                    	}
                    }
                }
            }
        }
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = new FinancierAuctionSettingBean();

        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord);
        Long lId = null;
        boolean lSuccess = false;
        String lMessage = null;
        try {
            //
            List<ValidationFailBean> lValidationFailBeans = financierAuctionSettingBeanMeta.validateAndParse(lFinancierAuctionSettingBean, lMap, null, headerFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
                List<TenureWiseBaseRateBean> lTenureWiseSlabs = new ArrayList<TenureWiseBaseRateBean>();
                for (int lPtr=0;lPtr<lSlabsList.length;lPtr++) {
                    Map<String,Object> lSlabMap = lSlabsList[lPtr];
                    if (lSlabMap != null) {
                        TenureWiseBaseRateBean lTenureWiseBaseRateBean = new TenureWiseBaseRateBean();
                        lValidationFailBeans = tenureWiseBaseRateBeanMeta.validateAndParse(lTenureWiseBaseRateBean, lSlabMap, null);
                        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                            ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                            lMessage = lValidationFailBean.getName() + (lPtr+1) + ":" + lValidationFailBean.getMessage();
                            if (lValidationFailBeans.size() > 1)
                                lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
                            break;
                        }
                        lTenureWiseSlabs.add(lTenureWiseBaseRateBean);
                    }
                }
                if (lMessage == null) {
                    if (!lTenureWiseSlabs.isEmpty())
                        lFinancierAuctionSettingBean.setBaseRateList(lTenureWiseSlabs);

                    //for user limit and limitlevel
                    String lUser = (String) lMap.get("loginId");
                    if(CommonUtilities.hasValue(lUser)){
                        //
                        lLevel[3] = "Y";
                        AppUserBean lAppUserBean = (AppUserBean)AuthenticationHandler.getInstance().getUserDataSource().getUserBean(
                                pFileUploadBean.getExecutionContext(), (String)lMap.get("financier"), lUser);
                        if (lAppUserBean == null)
                            throw new CommonBusinessException("Invalid Login Id " + lUser);
                        lFinancierAuctionSettingBean.setAuId(lAppUserBean.getId());
                    }

                    String lLevelStr = CommonUtilities.joinString(lLevel, "");
                    if (!lLevelStr.equals(level.getCode()))
                        throw new CommonBusinessException("The record does not correspond to " + level + " limit record");
                    lFinancierAuctionSettingBean.setLevel(level);
                    financierAuctionSettingBO.save(pFileUploadBean.getExecutionContext(), lFinancierAuctionSettingBean, (AppUserBean)pFileUploadBean.getAppUserBean(), lFinancierAuctionSettingBean.getId()==null, true);
                    lId = lFinancierAuctionSettingBean.getId();
                    lSuccess = true;
                }
            }
        } catch (CommonBusinessException lException) {
            lMessage = lException.getMessage();
        } catch (Exception lException) {
            lMessage = "Internal Error : " + lException.getMessage();
            logger.error("Error while uploading file " + pFileUploadBean.getFileName(), lException);
        }
        lResponseBuffer.append(SEPERATOR);
        if (lId != null)
            lResponseBuffer.append(lFinancierAuctionSettingBean.getId());
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        lResponseBuffer.append(SEPERATOR);
        if (lMessage != null)
            lResponseBuffer.append(lMessage);
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
    }
    
}
