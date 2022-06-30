
package com.xlx.treds.instrument.bean;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class MemberLocationForInstKeysBean {
	public static String FIELDGROUP_INSERTDB = "insertDB";
    private String code;
    private Long clId;
    private List<String> clIdList;
    private String gstn;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public Long getClId() {
        return clId;
    }

    public void setClId(Long pClId) {
        clId = pClId;
    }
    
    public List<String> getClIdList() {
        return clIdList;
    }

    public void setClIdList(List<String> pClIdList) {
        clIdList = pClIdList;
    }

    public String getGstn() {
        return gstn;
    }

    public void setGstn(String pGstn) {
        gstn = pGstn;
    }
    
    public String getLocations() {
        if (clIdList == null)
            return null;
        else 
            return new JsonBuilder(clIdList).toString();
    }
    
    public void setLocations(String pLocations) {
        if (StringUtils.isNotBlank(pLocations))
            clIdList = (List<String>)new JsonSlurper().parseText(pLocations);
        else
            clIdList = null;
    }
    
    public String getKey(){
    	return code+CommonConstants.KEY_SEPARATOR+clId+CommonConstants.KEY_SEPARATOR+gstn;
    }


    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordUpdator() {
        return recordUpdator;
    }

    public void setRecordUpdator(Long pRecordUpdator) {
        recordUpdator = pRecordUpdator;
    }

    public Timestamp getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Timestamp pRecordUpdateTime) {
        recordUpdateTime = pRecordUpdateTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}