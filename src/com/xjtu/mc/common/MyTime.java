package com.xjtu.mc.common;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTime {
	public static String geTimeNoS(){
		Date date=new Date();   
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm");   
		String time=df.format(date);
		return time;
	}
	public static String geTime(){
		Date date=new Date();   
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm:ss");   
		String time=df.format(date);
		return time;
	}
	public static String geVoiceTime(int type){
		Date date=new Date();   
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH-mm-ss");   
		String time=df.format(date);
		return time+"-"+type;
	}
}
