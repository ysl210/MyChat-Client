/**
 * �������е�Activity�Ա���ȫ�˳�����͵õ�Activity��ʵ��
 */
package com.xjtu.mychat.client.view;
import java.util.HashMap;

import android.app.Activity;

public class ManageActivity {
	private static HashMap<String, Activity> allActiviy=new HashMap<String,Activity>();

	public static void addActiviy(String name,Activity activity){
		allActiviy.put(name, activity);
	}
	
	public static Activity getActivity(String name){
		return (Activity)allActiviy.get(name);
	}
}
