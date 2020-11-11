package com.mi.ooms.redis.deptIncome.service;

import java.util.List;
import java.util.Map;

import com.mi.ooms.redis.deptIncome.model.DeptIncomeModel;

public interface DeptIncomeService {
	
	public List<DeptIncomeModel> selectOrgWrkAcmpActivityByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpSubTaskByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpTaskByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpJobByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectNewDdbyData();

}
