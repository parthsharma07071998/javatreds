
package com.xlx.treds.monetago.bean;

import java.sql.Date;
import java.sql.Timestamp;

public class MonetagoEwaybillVehicleListDetailsBean {

    private String updMode;
    private String vehicleNo;
    private String fromPlace;
    private String fromState;
    private Long tripshtNo;
    private String userGSTINTransin;
    private Timestamp enteredDate;
    private String transMode;
    private String transDocNo;
    private Date transDocDate;
    private String groupNo;

    public String getUpdMode() {
        return updMode;
    }

    public void setUpdMode(String pUpdMode) {
        updMode = pUpdMode;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String pVehicleNo) {
        vehicleNo = pVehicleNo;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String pFromPlace) {
        fromPlace = pFromPlace;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String pFromState) {
        fromState = pFromState;
    }

    public Long getTripshtNo() {
        return tripshtNo;
    }

    public void setTripshtNo(Long pTripshtNo) {
        tripshtNo = pTripshtNo;
    }

    public String getUserGSTINTransin() {
        return userGSTINTransin;
    }

    public void setUserGSTINTransin(String pUserGSTINTransin) {
        userGSTINTransin = pUserGSTINTransin;
    }

    public Timestamp getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(Timestamp pEnteredDate) {
        enteredDate = pEnteredDate;
    }

    public String getTransMode() {
        return transMode;
    }

    public void setTransMode(String pTransMode) {
        transMode = pTransMode;
    }

    public String getTransDocNo() {
        return transDocNo;
    }

    public void setTransDocNo(String pTransDocNo) {
        transDocNo = pTransDocNo;
    }

    public Date getTransDocDate() {
        return transDocDate;
    }

    public void setTransDocDate(Date pTransDocDate) {
        transDocDate = pTransDocDate;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String pGroupNo) {
        groupNo = pGroupNo;
    }

	@Override
	public String toString() {
		return super.toString();
	}
	
	public String getTransModeDesc() {
		if ("1".equals(transMode)) return "Road";
		if ("2".equals(transMode)) return "Rail";
		if ("3".equals(transMode)) return "Air";
		if ("4".equals(transMode)) return "Ship";
        return transMode;
    }
}