package com.xjtu.mc.common;

import com.xjtu.mychat.client.view.Info;

/*
 * ����user���ӽ���
 * */
public class Util {
	public static String buddyStr = "";
	public static User me; //��ǰ�˺ŵĸ�������
	public static String code; //ÿ�ε�¼ʱ���ص������
	public static String deviceId ; //�ҵ��豸ID
	public static String otherDeviceId ;//���˵��豸ID
	public static String otherCode ;//���˵��豸�������
	//��Ϊ�ڱ���������õ�me��������ǰ�����˸������ϣ�
	static{
		System.out.println("myin:"+Info.myInfo);
		code=jieXi(Info.myInfo);
		me = Info.me;
	}
	//���ܷ��͵�content
	public static byte[] encrypt(byte[] content,int type) {
		String id="";
		String code_m="";
		switch (type) {
		case 1:
			id = deviceId;
			code_m = code;
			break;
		case 2:
			id = otherDeviceId;
			code_m = otherCode;
			break;
		default:
			break;
		}
		byte[] b = id.getBytes();
		for (int i = 0; i < content.length; i++) {
			content[i] = (byte) (content[i]^(Integer.parseInt(code_m)|(int)((b[0]+b[1]))));
			//content[i] = (byte) (content[i]^111);
		}
		return content;
	}
	private static String jieXi(String str) { 
		String s[] = str.split("_");
		String mycode = "";
    	if(s!=null){  
	        mycode = s[0];
	        System.out.println("woedxiaox"+Info.myInfo);
	        System.out.println("�ҵ������"+mycode);
    	}
		return mycode;
	}
}
