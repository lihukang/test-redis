package com.mi.ooms.redis.deptIncome.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mi.ooms.redis.deptIncome.model.DeptIncomeModel;

@Repository
public interface DeptIncomeDao {
	
	public List<DeptIncomeModel> selectOrgWrkAcmpActivityByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpSubTaskByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpTaskByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectOrgWrkAcmpJobByOrderList(Map<String,String> map);
	public List<DeptIncomeModel> selectNewDdbyData();

}
