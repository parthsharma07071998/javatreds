package com.xlx.treds.auction.bean;

public class AssignmentNoticeInfo {

	private AssignmentNoticesBean assignmentNoticesBean;
	private AssignmentNoticeDetailsBean assignmentNoticeDetailsBean;
	private AssignmentNoticeGroupDetailsBean assignmentNoticeGroupDetailsBean;
	
	public void setAssignmentNoticesBean(AssignmentNoticesBean pAssignmentNoticesBean){
		assignmentNoticesBean = pAssignmentNoticesBean;
	}

	public AssignmentNoticesBean getAssignmentNoticesBean(){
		return assignmentNoticesBean ;
	}

	public void setAssignmentNoticeDetailsBean(AssignmentNoticeDetailsBean pAssignmentNoticeDetailsBean){
		assignmentNoticeDetailsBean = pAssignmentNoticeDetailsBean;
	}
	
	public AssignmentNoticeDetailsBean getAssignmentNoticeDetailsBean(){
		return assignmentNoticeDetailsBean;
	}

	public void setAssignmentNoticeGroupDetailsBean(AssignmentNoticeGroupDetailsBean pAssignmentNoticeGroupDetailsBean) {
		assignmentNoticeGroupDetailsBean = pAssignmentNoticeGroupDetailsBean;
	}
	
	public AssignmentNoticeGroupDetailsBean getAssignmentNoticeGroupDetailsBean() {
		return assignmentNoticeGroupDetailsBean;
	}
}
