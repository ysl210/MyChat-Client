package com.xjtu.mychat.client.model;

import java.io.ObjectOutputStream;

import android.content.Context;

import com.xjtu.mc.common.MCMessage;
import com.xjtu.mc.common.MyTime;
import com.xjtu.mc.common.Util;

public class SendMessage {
	public static void sendMes(String dfAccount, byte[] content, String type) {
		try {
			String myAccount = Util.me.getAccount();
			ObjectOutputStream oos = new ObjectOutputStream
			// 通过account找到该线程，从而得到OutputStream
			(ManageClientConServer.getClientConServerThread(myAccount).getS()
					.getOutputStream());
			// 发送消息
			MCMessage m = new MCMessage();
			m.setType(type);
			m.setReceiver(dfAccount);
			m.setSender(myAccount);
			m.setContent(content);
			m.setSendTime(MyTime.geTime());
			oos.writeObject(m);
		} catch (Exception e) {
			try{
				
			}catch(Exception e1){
				
			}
			e.printStackTrace();
		}
	}

	// 语音，文件，图片
	public static void sendMes(Context context, MCMessage m) {
		final MCMessage m1 = m;
		// final Context context1 = context;
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					// 通过account找到该线程，从而得到OutputStream
					String myAccount = Util.me.getAccount();
					ObjectOutputStream oos = new ObjectOutputStream(
							ManageClientConServer
									.getClientConServerThread(myAccount).getS()
									.getOutputStream());
					// 发送消息
					oos.writeObject(m1);
					System.out.println("文件发送完毕");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	// 发送连接信息
	// 请求连接方发送连接请求、被连接方发送同意或拒绝信息
	public static void sendConnectMes(String myAccount, String dfAccount,
			String type) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					ManageClientConServer.getClientConServerThread(myAccount)
							.getS().getOutputStream());
			MCMessage m = new MCMessage();
			// if
			// (type.equals(MCMessageType.REQ_CONNECT)||type.equals(MCMessageType.ALLOW_CONNECT))
			// {
			// m.setDeviceId(Util.deviceId);
			// m.setContent(Util.code.getBytes());
			// }
			m.setDeviceId(Util.deviceId);
			m.setContent(Util.code.getBytes());

			m.setType(type);
			m.setSender(myAccount);
			m.setReceiver(dfAccount);
			oos.writeObject(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
