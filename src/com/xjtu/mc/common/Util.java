package com.xjtu.mc.common;

import com.xjtu.mychat.client.view.Info;

/*
 * 解析user，加解密
 * */
public class Util {
	public static String buddyStr = "";
	public static User me; //当前账号的个人资料
	public static String code; //每次登录时返回的随机码
	public static String deviceId ; //我的设备ID
	public static String otherDeviceId ;//别人的设备ID
	public static String otherCode ;//别人的设备的随机码
	//因为在别的类中有用到me，所以提前解析了个人资料，
	static{
		System.out.println("myin:"+Info.myInfo);
		code=jieXi(Info.myInfo);
		me = Info.me;
	}
	//加密发送的content
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
	        System.out.println("我的随机码"+mycode);
    	}
		return mycode;
	}
}
