package com.happyballoon.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	public static void main(String[] args) {
		String sysTime = DateTimeUtil.getSysTime();
		System.out.println(sysTime);
	}
	
	public static String getSysTime(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date = new Date();
		String dateStr = sdf.format(date);
		
		return dateStr;
		
	}
	
}
