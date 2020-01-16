package com.nhncorp.ooms.redis.app;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.nhncorp.ooms.redis.calendar.dao.CalendarDao;
import com.nhncorp.ooms.redis.core.RedisApi;
import com.nhncorp.ooms.redis.deptIncome.model.DeptIncomeModel;
import com.nhncorp.ooms.redis.deptIncome.service.DeptIncomeService;
import com.nhncorp.ooms.redis.model.UserInfoModel;
import com.nhncorp.ooms.redis.util.SerializeUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=RedisApplication.class)
public class RedisApiTest {
	private final Log log = LogFactory.getLog(RedisApiTest.class); 

	@Autowired
	RedisApi<String,Object> redisApi;
	
	@Autowired
	DeptIncomeService deptIncomeService;

	@Autowired
	CalendarDao calendarDao;
	
	 @Test
     public void setString()  {
		 redisApi.setString("abc", "ccc");
		 log.debug("dddaa : "+redisApi.getString("abc"));
	     
     }
	 
	 
	 @Test
	 public void flushDb()  {
		 redisApi.flushDb();
	 }
	 
	 
	 @Test
	 public void zrange()  {
		 UserInfoModel infoModel1 = new UserInfoModel();
		 infoModel1.setId(1);
		 infoModel1.setName("Y1");
		 infoModel1.setTime("20180401");
		 
		 UserInfoModel infoModel2 = new UserInfoModel();
		 infoModel2.setId(2);
		 infoModel2.setName("Y2");
		 infoModel2.setTime("20180402");
		 
		 
		 UserInfoModel infoModel3 = new UserInfoModel();
		 infoModel3.setId(3);
		 infoModel3.setName("Y3");
		 infoModel3.setTime("20180403");
		 
		 UserInfoModel infoModel4 = new UserInfoModel();
		 infoModel4.setId(4);
		 infoModel4.setName("Y4");
		 infoModel4.setTime("20180404");
		 
		 UserInfoModel infoModel5 = new UserInfoModel();
		 infoModel5.setId(5);
		 infoModel5.setName("Y5");
		 infoModel5.setTime("20180405");
		 
		 redisApi.delete("UserRange");
		 redisApi.zaddByScore("UserRange",Long.parseLong(infoModel1.getTime()), infoModel1);
		 redisApi.zaddByScore("UserRange",Long.parseLong(infoModel2.getTime()),  infoModel2);
		 redisApi.zaddByScore("UserRange",Long.parseLong(infoModel3.getTime()),  infoModel3);
		 redisApi.zaddByScore("UserRange",Long.parseLong(infoModel4.getTime()),  infoModel4);
		 redisApi.zaddByScore("UserRange",Long.parseLong(infoModel5.getTime()),  infoModel5);
		 
		 
		 log.debug("zlist"+redisApi.zrangeByScore("UserRange", 20180402, 20180403).toString());
		 
	 }
	 
	 
	 
	 @Test
	 public void selectActivityMap(){
		 
		 Map<String,Object> param = new HashMap<String, Object>();
		 param.put("startYmd", "20140101");
		 param.put("endYmd", "20140131");
		 List<String> dataList = calendarDao.selectCalendar(param);
		 
		 Set<Object> set = redisApi.zrangeByScore("ActivityList:GW00055", Long.parseLong("20140101"), Long.parseLong("20140107"));
		 transToMap(set,dataList);
	 }
	 
	 @Test
	 public void selectSubTaskMap(){
		 
		 Map<String,Object> param = new HashMap<String, Object>();
		 param.put("startYmd", "20140101");
		 param.put("endYmd", "20140131");
		 List<String> dataList = calendarDao.selectCalendar(param);
		 
		 Set<Object> set = redisApi.zrangeByScore("SubTaskList:GW00036", Long.parseLong("20140107"), Long.parseLong("20140107"));
		 transToMap(set,dataList);
	 }
	 
	 @Test
	 public void selectTaskMap(){
		 Map<String,Object> param = new HashMap<String, Object>();
		 param.put("startYmd", "20140101");
		 param.put("endYmd", "20140131");
		 List<String> dataList = calendarDao.selectCalendar(param);
		 
		 Set<Object> set = redisApi.zrangeByScore("TaskList:IN00092", Long.parseLong("20140101"), Long.parseLong("20140107"));
		 transToMap(set,dataList);
	 }
	 
	 @Test
	 public void selectJobMap(){
		 Map<String,Object> param = new HashMap<String, Object>();
		 param.put("startYmd", "20140101");
		 param.put("endYmd", "20140131");
		 List<String> dataList = calendarDao.selectCalendar(param);
		 
		 Set<Object> set = redisApi.zrangeByScore("JobList:GW00010", Long.parseLong("20140227"), Long.parseLong("20140228"));
		 transToMap(set,dataList);
	 }
	 
	 public void insertActivitys(List<String> dataList) throws InterruptedException{
		 
		 //redisApi.flushDb();
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("ymd", ymd);
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpActivityByOrderList(param);
			 if(list.size()!=0){
				 for (DeptIncomeModel model:list){
					 long score = Long.parseLong(ymd);
					 redisApi.zaddByScore("ActivityList:"+model.getUserOrgId(), score, model);
				 }
				 
			 }
		 }
		 
		 
		 
	 }
	 
	 public void insertSubTasts(List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("ymd", ymd);
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpSubTaskByOrderList(param);
			 if(list.size()!=0){
				 for (DeptIncomeModel model:list){
					 long score = Long.parseLong(ymd);
					 redisApi.zaddByScore("SubTaskList:"+model.getUserOrgId(), score, model);
				 }
				 
			 }
		 }
		 
		 
		 
	 }
	 
	 public void insertTasts(List<String> dataList) throws InterruptedException{
		 
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("ymd", ymd);
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpTaskByOrderList(param);
			 if(list.size()!=0){
				 for (DeptIncomeModel model:list){
					 long score = Long.parseLong(ymd);
					 redisApi.zaddByScore("TaskList:"+model.getUserOrgId(), score, model);
				 }
				 
			 }
		 }
		 
		 
		 
	 }
	 
	 public void insertJobs(List<String> dataList) throws InterruptedException{
		 
		 System.out.println("dataList"+dataList.toString());
		 for(String ymd : dataList){
			 System.out.println("ymd"+ymd);
			 Map<String,String> param = new HashMap<String, String>();
			 param.put("ymd", ymd);
			 List<DeptIncomeModel> list = deptIncomeService.selectOrgWrkAcmpJobByOrderList(param);
			 if(list.size()!=0){
				 for (DeptIncomeModel model:list){
					 long score = Long.parseLong(ymd);
					 redisApi.zaddByScore("JobList:"+model.getUserOrgId(), score, model);
				 }
				 
			 }
		 }
		 
		 
		 
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
	 
	 /**
	  * 导入REDIS数据
	  * @throws InterruptedException
	  */
	 @Test
	 public void insertAll() throws InterruptedException{
		 Map<String,Object> param = new HashMap<String, Object>();
		 param.put("startYmd", "20180101");
		 param.put("endYmd", "20180510");
		 List<String> dataList = calendarDao.selectCalendar(param);
		 //redisApi.flushDb();
		 
		 insertJobsByPipeLine(null,dataList);
		 insertTasksByPipeLine(null,dataList);
		 insertSubTasksByPipeLine(null,dataList);
		 insertActivitysByPipeLine(null,dataList);
	 }
	 
	 
	 public  List<Map<String,Object>> transToMap(Set<?> set,List<String> dataList){
		 Iterator<?> iterator =  set.iterator();
		 
		 Set<Map<String,Object>> resultList = new HashSet<Map<String,Object>>();
		 while(iterator.hasNext()){
			 DeptIncomeModel deptIncoModel = (DeptIncomeModel) iterator.next();
			 System.out.println(deptIncoModel.toString());
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
	     
		 System.out.println("resultListSize"+resultList.size());
		 return list;
		 
	 }
	 
	 
	 
	 @Test
	 public void insertNewData() throws InterruptedException{
		 List<DeptIncomeModel> list = deptIncomeService.selectNewDdbyData();
		 
		 
		 for(DeptIncomeModel model : list){
			 redisApi.zremrangeByScore("ActivityList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
			 redisApi.zremrangeByScore("SubTaskList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
			 redisApi.zremrangeByScore("TaskList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
			 redisApi.zremrangeByScore("JobList:"+model.getUserOrgId(), Long.parseLong(model.getYmd()), Long.parseLong(model.getYmd()));
			 
			 List<String> dataList = new ArrayList<String>();
			 dataList.add(model.getYmd());
			 
			 insertJobsByPipeLine(model.getUserOrgId(),dataList);
			 insertTasksByPipeLine(model.getUserOrgId(),dataList);
			 insertSubTasksByPipeLine(model.getUserOrgId(),dataList);
			 insertActivitysByPipeLine(model.getUserOrgId(),dataList);
			 System.out.println(model.getYmd()+model.getUserOrgId());
		 }
		 
	 }
	 
}
