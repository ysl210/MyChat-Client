package com.xjtu.mychat.client.model;
import java.util.HashMap;

public class ManageClientConServer {
	private static HashMap<String,ClientConServerThread> hm=new HashMap<String,ClientConServerThread>();
	//�Ѵ����õ�ClientConServerThread���뵽hm
	public static void addClientConServerThread(String account,ClientConServerThread ccst){
		hm.put(account, ccst);
	}
	
	//����ͨ��accountȡ�ø��߳�
	public static ClientConServerThread getClientConServerThread(String i){
		return (ClientConServerThread)hm.get(i);
	}
	
	public static void remove(String i){
		hm.remove(i);
	}
}
