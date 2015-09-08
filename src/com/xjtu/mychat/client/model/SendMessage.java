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
			// ͨ��account�ҵ����̣߳��Ӷ��õ�OutputStream
			(ManageClientConServer.getClientConServerThread(myAccount).getS()
					.getOutputStream());
			// ������Ϣ
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

	// �������ļ���ͼƬ
	public static void sendMes(Context context, MCMessage m) {
		final MCMessage m1 = m;
		// final Context context1 = context;
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					// ͨ��account�ҵ����̣߳��Ӷ��õ�OutputStream
					String myAccount = Util.me.getAccount();
					ObjectOutputStream oos = new ObjectOutputStream(
							ManageClientConServer
									.getClientConServerThread(myAccount).getS()
									.getOutputStream());
					// ������Ϣ
					oos.writeObject(m1);
					System.out.println("�ļ��������");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	// ����������Ϣ
	// �������ӷ������������󡢱����ӷ�����ͬ���ܾ���Ϣ
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
