package com.nhncorp.ooms.redis.calendar.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface CalendarDao {
	
	public List<String> selectCalendar(Map<String,Object> map);

}
