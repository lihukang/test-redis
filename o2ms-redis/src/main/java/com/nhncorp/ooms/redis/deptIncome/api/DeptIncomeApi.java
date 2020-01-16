package com.nhncorp.ooms.redis.deptIncome.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.nhncorp.ooms.redis.app.RedisApiTest;
import com.nhncorp.ooms.redis.calendar.dao.CalendarDao;
import com.nhncorp.ooms.redis.core.RedisApi;
import com.nhncorp.ooms.redis.deptIncome.model.DeptIncomeModel;
import com.nhncorp.ooms.redis.deptIncome.service.DeptIncomeService;
import com.nhncorp.ooms.redis.util.SerializeUtil;

@RestController
public class DeptIncomeApi {
	
	private final Log log = LogFactory.getLog(DeptIncomeApi.class); 
	@Autowired
	RedisApi<String,Object> redisApi;
	
	@Autowired
	DeptIncomeService deptIncomeService;

	@Autowired
	CalendarDao calendarDao;
	
	@PostMapping(value="depetIncome/selectDeptIncome",produces = "application/json; charset=utf-8")
	public Map<String,Object> selectDeptIncome(@RequestBody Map<String, Object> params){
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String orgId = (String) params.get("orgId");
		long startYmd =  Long.parseLong(String.valueOf(params.get("startYmd")).replaceAll("-",""));
		long endYmd =  Long.parseLong(String.valueOf(params.get("endYmd")).replaceAll("-",""));
		
		
		List<String> dataList = calendarDao.selectCalendar(params);
		 
		result.put("dayList", dataList);
		result.put("JobList", selectJobMap(orgId,startYmd,endYmd,dataList));
		result.put("taskList", selectTaskMap(orgId,startYmd,endYmd,dataList));
		result.put("subTaskList", selectSubTaskMap(orgId,startYmd,endYmd,dataList));
		result.put("activityList", selectActivityMap(orgId,startYmd,endYmd,dataList));
		
		log.info("redis result : "+result);
		return result;
	}
	

	@GetMapping(value="depetIncome/execTimer")
	public void execTimer(){
		
		Timer timer = new Timer();
	     timer.schedule(new TimerTask() {
	     public void run() {
	    	 List<DeptIncomeModel> list = deptIncomeService.selectNewDdbyData();
			 
			 
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
				 System.out.println(model.getYmd()+model.getUserOrgId());
			 }
			 
		    }
		 },0,10000);
		
		
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
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpActivityByOrderList(param);
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
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpSubTaskByOrderList(param);
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
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpTaskByOrderList(param);
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
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpJobByOrderList(param);
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
	
	
	 private List<Map<String,Object>> selectActivityMap(String orgId,long startYmd,long endYmd,List<String> dataList){
		 
		 Set<Object> set = redisApi.zrangeByScore("ActivityList:"+orgId, startYmd, endYmd);
		 List<Map<String,Object>> result = transToMap(set,dataList);
		 
		 return result;
	 }
	 
	 private List<Map<String,Object>> selectSubTaskMap(String orgId,long startYmd,long endYmd,List<String> dataList){
		 
		 
		 Set<Object> set = redisApi.zrangeByScore("SubTaskList:"+orgId, startYmd, endYmd);
		 List<Map<String,Object>> result = transToMap(set,dataList);
		 
		 return result;
	 }
	 
	 private List<Map<String,Object>> selectTaskMap(String orgId,long startYmd,long endYmd,List<String> dataList){
		 
		 
		 Set<Object> set = redisApi.zrangeByScore("TaskList:"+orgId, startYmd, endYmd);
		 List<Map<String,Object>> result = transToMap(set,dataList);
		 
		 return result;
	 }
	 
	 private List<Map<String,Object>> selectJobMap(String orgId,Long startYmd,Long endYmd,List<String> dataList){
		 String org = "JobList:"+orgId;
		 System.out.println("org:"+org);
		 Set<Object> set = redisApi.zrangeByScore(org, startYmd, endYmd);
		 List<Map<String,Object>> result = transToMap(set,dataList);
		 
		 return result;
	 }
	
	 public  List<Map<String,Object>> transToMap(Set<?> set,List<String> dataList){
		 Iterator<?> iterator =  set.iterator();
		 
		 Set<Map<String,Object>> resultList = new HashSet<Map<String,Object>>();
		 while(iterator.hasNext()){
			 DeptIncomeModel deptIncoModel = (DeptIncomeModel) iterator.next();
			 Map<String,Object> map = new HashMap<String, Object>();
			 map.put("USER_ID", deptIncoModel.getUserId());
			 map.put("TASK_NO", deptIncoModel.getTaskNo());
			 map.put("TASK_NM", deptIncoModel.getTaskNm());
			 map.put("SUB_TASK_SEQ", deptIncoModel.getSubTaskSeq());
			 map.put("USER_ORG_ID", deptIncoModel.getUserOrgId());
			 map.put("ORDER_JOB_NO", deptIncoModel.getOrderJobNo());
			 map.put("USER_NM", deptIncoModel.getUserNm());
			 map.put("TASK_LEVEL", deptIncoModel.getTaskLevel());
			 map.put("UPR_SUB_TASK_SEQ", deptIncoModel.getUprSubTaskSeq());
			 for(String data : dataList){
				 map.put("TM_"+data, 0);
				 map.put("CNT_"+data, 0);
			 }
			 for(Object model : set){
				 
				 DeptIncomeModel newmodel = (DeptIncomeModel) model;
				 
				 if(map.get("USER_ID").equals(newmodel.getUserId())&&map.get("TASK_NO").equals(newmodel.getTaskNo())&&map.get("SUB_TASK_SEQ").equals(newmodel.getSubTaskSeq())&&map.get("ORDER_JOB_NO").equals(newmodel.getOrderJobNo())){
					 for(String data : dataList){
						 if(data.equals(newmodel.getYmd())){
							 map.put("TM_"+data, newmodel.getTmData());
							 map.put("CNT_"+data, newmodel.getCntData());
						 }
					 }
				 }
				 
				 
			 }
			 
			 resultList.add(map);
		 }
		 
		 List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(resultList);
	     Collections.sort(list,new Comparator<Map<String,Object>>() {

				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					// TODO Auto-generated method stub
					return o1.get("USER_ID").toString().compareTo(o2.get("USER_ID").toString());
				}

	        });
	     
		 System.out.println("resultListSize"+list.size());
		 return list;
		 
	 }

}
