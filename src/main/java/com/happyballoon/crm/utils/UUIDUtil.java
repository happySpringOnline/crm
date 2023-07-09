package com.happyballoon.crm.utils;

import java.util.UUID;

public class UUIDUtil {
	public static void main(String[] args) {
		String uuid = UUIDUtil.getUUID();
		System.out.println(uuid);
	}
	
	public static String getUUID(){
		
		return UUID.randomUUID().toString().replaceAll("-","");
		
	}
	
}
