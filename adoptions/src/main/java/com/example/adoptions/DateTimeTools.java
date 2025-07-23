package com.example.adoptions;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tangtian
 * @date 2025-07-22 16:53
 */
public class DateTimeTools {
	@Tool(description = "获得当前的时间")
	String getCurrentDateTime() {
		return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
	}

	@Tool(description = "设置当前的时间")
	void setAlarm(String time) {
		LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
		System.out.println("Alarm set for " + alarmTime);
	}
}
