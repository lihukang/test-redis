package com.nhncorp.ooms.redis.deptIncome.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.nhncorp.ooms.redis.core.RedisApi;
import com.nhncorp.ooms.redis.deptIncome.dao.DeptIncomeDao;
import com.nhncorp.ooms.redis.deptIncome.model.DeptIncomeModel;
import com.nhncorp.ooms.redis.util.SerializeUtil;

@Service("deptIncomeService")
public class DeptIncomeServiceImpl implements DeptIncomeService, InitializingBean{
	
	@Autowired
	RedisApi<String,Object> redisApi;
	
	@Autowired
	private DeptIncomeDao deptIncomeDao;

	@Override
	public List<DeptIncomeModel> selectOrgWrkAcmpActivityByOrderList(Map<String,String> map) {
		return deptIncomeDao.selectOrgWrkAcmpActivityByOrderList(map);
	}
	
	@Override
	public List<DeptIncomeModel> selectOrgWrkAcmpSubTaskByOrderList(Map<String,String> map) {
		return deptIncomeDao.selectOrgWrkAcmpSubTaskByOrderList(map);
	}
	
	@Override
	public List<DeptIncomeModel> selectOrgWrkAcmpTaskByOrderList(Map<String,String> map) {
		return deptIncomeDao.selectOrgWrkAcmpTaskByOrderList(map);
	}
	
	@Override
	public List<DeptIncomeModel> selectOrgWrkAcmpJobByOrderList(Map<String,String> map) {
		return deptIncomeDao.selectOrgWrkAcmpJobByOrderList(map);
	}
	
	@Override
	public List<DeptIncomeModel> selectNewDdbyData() {
		return deptIncomeDao.selectNewDdbyData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Timer timer = new Timer();
	     timer.schedule(new TimerTask() {
	     public void run() {
	    	 List<DeptIncomeModel> list = deptIncomeDao.selectNewDdbyData();
			 
			 for(DeptIncomeModel model : list){
				 redisApi.zremrangeByScore("ActivityList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
				 redisApi.zremrangeByScore("SubTaskList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
				 redisApi.zremrangeByScore("TaskList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
				 redisApi.zremrangeByScore("JobList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
				 
				 List<String> dataList = new ArrayList<String>();
				 dataList.add(model.getYmd());
				 
				 try {
					 insertJobsByPipeLine(model.getUserOrgId(),dataList);
					 insertTasksByPipeLine(model.getUserOrgId(),dataList);
					 insertSubTasksByPipeLine(model.getUserOrgId(),dataList);
					 insertActivitysByPipeLine(model.getUserOrgId(),dataList);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 
		    }
		 },0,50000);
		
	}
	

	 public void insertActivitysByPipeLine(String userOrgId,List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Jedis jedis = redisApi.getJedis();
			 Pipeline pipe = jedis.pipelined();
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("startYmd", ymd);
			 if(!("").equals(userOrgId)&&userOrgId!=null){
				 param.put("orgId", userOrgId);
			 }
			 List<DeptIncomeModel> list = deptIncomeDao.selectOrgWrkAcmpActivityByOrderList(param);
			 try {
				if(list.size()!=0){
					 for (DeptIncomeModel model:list){
						 long score = Long.parseLong(ymd);
						 pipe.zadd(SerializeUtil.writeToByteArray("SubTaskList:"+model.getUserOrgId()),score, SerializeUtil.writeToByteArray(model));
					 }
					 pipe.sync();
				 }
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					 jedis.close();
					 pipe.close();
				 } catch (IOException e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
			}
		 }
		 
		 
		 
	 }
	 
	 public void insertSubTasksByPipeLine(String userOrgId,List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Jedis jedis = redisApi.getJedis();
			 Pipeline pipe = jedis.pipelined();
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("startYmd", ymd);
			 if(!("").equals(userOrgId)&&userOrgId!=null){
				 param.put("orgId", userOrgId);
			 }
			 List<DeptIncomeModel> list = deptIncomeDao.selectOrgWrkAcmpSubTaskByOrderList(param);
			 try {
				if(list.size()!=0){
					 for (DeptIncomeModel model:list){
						 long score = Long.parseLong(ymd);
						 pipe.zadd(SerializeUtil.writeToByteArray("SubTaskList:"+model.getUserOrgId()),score, SerializeUtil.writeToByteArray(model));
					 }
					 pipe.sync();
				 }
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					 jedis.close();
					 pipe.close();
				 } catch (IOException e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
			}
		 }
		 
		 
		 
	 }
	 
	 public void insertTasksByPipeLine(String userOrgId,List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Jedis jedis = redisApi.getJedis();
			 Pipeline pipe = jedis.pipelined();
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("startYmd", ymd);
			 if(!("").equals(userOrgId)&&userOrgId!=null){
				 param.put("orgId", userOrgId);
			 }
			 List<DeptIncomeModel> list = deptIncomeDao.selectOrgWrkAcmpTaskByOrderList(param);
			 try {
				if(list.size()!=0){
					 for (DeptIncomeModel model:list){
						 long score = Long.parseLong(ymd);
						 pipe.zadd(SerializeUtil.writeToByteArray("TaskList:"+model.getUserOrgId()),score, SerializeUtil.writeToByteArray(model));
					 }
					 pipe.sync();
				 }
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					 jedis.close();
					 pipe.close();
				 } catch (IOException e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
			}
		 }
		 
		 
		 
	 }
	 
	 
	 public void insertJobsByPipeLine(String userOrgId,List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Jedis jedis = redisApi.getJedis();
			 Pipeline pipe = jedis.pipelined();
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("startYmd", ymd);
			 if(!("").equals(userOrgId)&&userOrgId!=null){
				 param.put("orgId", userOrgId);
			 }
			 List<DeptIncomeModel> list = deptIncomeDao.selectOrgWrkAcmpJobByOrderList(param);
			 try {
				if(list.size()!=0){
					 for (DeptIncomeModel model:list){
						 long score = Long.parseLong(ymd);
						 pipe.zadd(SerializeUtil.writeToByteArray("JobList:"+model.getUserOrgId()),score, SerializeUtil.writeToByteArray(model));
					 }
					 pipe.sync();
				 }
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					 jedis.close();
					 pipe.close();
				 } catch (IOException e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
			}
		 }
		 
		 
		 
	 }

}
