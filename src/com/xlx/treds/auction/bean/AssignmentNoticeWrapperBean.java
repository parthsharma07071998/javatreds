package com.xlx.treds.auction.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssignmentNoticeWrapperBean {
	private AssignmentNoticesBean assignmentNoticesBean;
	private List<AssignmentNoticeDetailsBean> assignmentNoticeDetailsList = new ArrayList<AssignmentNoticeDetailsBean>();
	private HashMap<Long,List<AssignmentNoticeGroupDetailsBean>> parentInstWiseChildList = new HashMap<Long, List<AssignmentNoticeGroupDetailsBean>>();

	public AssignmentNoticeWrapperBean(AssignmentNoticesBean pAssignmentNoticesBean){
		assignmentNoticesBean = pAssignmentNoticesBean;
	}

	public AssignmentNoticesBean  getAssignmentNoticesBean(){
		return assignmentNoticesBean ;
	}
	public Long getAnId(){
		return assignmentNoticesBean.getId();
	}
	
	public List<AssignmentNoticeDetailsBean> getDetails(){
		return assignmentNoticeDetailsList;
	}
	
	public List<AssignmentNoticeGroupDetailsBean> getChildDetails(Long pParentInId){
		return parentInstWiseChildList.get(pParentInId);
	}

	//the NOAKey is the Key of the AssignmentNoticeBean
	public void addDetails(AssignmentNoticeDetailsBean pAssignmentNoticeDetailsBean, String pNOAKey){
		if(!assignmentNoticesBean.getKey().equals(pNOAKey)){
			return;
		}
		boolean lFound = false;
		for(AssignmentNoticeDetailsBean lBean :  assignmentNoticeDetailsList){
			if(lBean.getFuId().equals(pAssignmentNoticeDetailsBean.getFuId())){
				lFound = true;
				break;
			}
		}
		if(!lFound){
			assignmentNoticeDetailsList.add(pAssignmentNoticeDetailsBean);
		}
	}
	public void addChildDetails(AssignmentNoticeGroupDetailsBean pAssignmentNoticeGroupDetailsBean, String pNOAKey){
		if(!assignmentNoticesBean.getKey().equals(pNOAKey)){
			return;
		}
		if(pAssignmentNoticeGroupDetailsBean.getChildInId() == null){
			return;
		}
		boolean lFound = false;
		for(AssignmentNoticeDetailsBean lBean :  assignmentNoticeDetailsList){
			if(lBean.getFuId().equals(pAssignmentNoticeGroupDetailsBean.getFuId())){
				lFound = true;
				break;
			}
		}
		if(lFound){
			List<AssignmentNoticeGroupDetailsBean> lChildList = null;
			if(!parentInstWiseChildList.containsKey(pAssignmentNoticeGroupDetailsBean.getGroupInId())){
				parentInstWiseChildList.put(pAssignmentNoticeGroupDetailsBean.getGroupInId(), new ArrayList<AssignmentNoticeGroupDetailsBean>());
			}
			lChildList = parentInstWiseChildList.get(pAssignmentNoticeGroupDetailsBean.getGroupInId());
			lFound = false;
			for(AssignmentNoticeGroupDetailsBean lBean : lChildList){
				if(lBean.getChildInId().equals(pAssignmentNoticeGroupDetailsBean.getChildInId())){
					lFound = true;
					break;
				}
			}
			if(!lFound){
				lChildList.add(pAssignmentNoticeGroupDetailsBean);
			}
		}
	}

	
}
