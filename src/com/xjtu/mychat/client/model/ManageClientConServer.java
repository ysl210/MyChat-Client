package com.xjtu.mychat.client.model;
import java.util.HashMap;

public class ManageClientConServer {
	private static HashMap<String,ClientConServerThread> hm=new HashMap<String,ClientConServerThread>();
	//把创建好的ClientConServerThread放入到hm
	public static void addClientConServerThread(String account,ClientConServerThread ccst){
		hm.put(account, ccst);
	}
	
	//可以通过account取得该线程
	public static ClientConServerThread getClientConServerThread(String i){
		return (ClientConServerThread)hm.get(i);
	}
	
	public static void remove(String i){
		hm.remove(i);
	}
}
