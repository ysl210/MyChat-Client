/**
 * �ͻ��˺ͷ������˱���ͨ�ŵ��߳�
 * org.yhn.yq.mes�㲥
 * ���ϵض�ȡ����������������
 */
package com.xjtu.mychat.client.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.xjtu.mc.common.MCMessage;
import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.MyTime;
import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.client.view.BuddyActivity;
import com.xjtu.mychat.client.view.ChatActivity;
import com.xjtu.mychat.client.view.McServer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ClientConServerThread extends Thread {
	private Context context;
	private Socket s;
	public static boolean isClosed=false;
	
	
	public Socket getS() {
		return s;
	}

	public ClientConServerThread(Context context, Socket s) {
		this.context = context;
		this.s = s;
	}

	@Override
	public void run() {
		while (true) {
			if(isClosed){
				isClosed = false;
				return;
			}
			
			
			
			ObjectInputStream ois = null;
			MCMessage m;
			try {
				ois = new ObjectInputStream(s.getInputStream());
				m = (MCMessage) ois.readObject();
				if (m.getType().equals(MCMessageType.COM_MES)) { // �����������Ϣ
					byte[] str = m.getContent();
					str = Util.encrypt(str, 2);// ����
					System.out.println("��ClientConServerThread���յ�����Ϣ��"
							+ new String(str));
					// �Ѵӷ�������õ���Ϣͨ���㲥����
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] { m.getSender() + "",
							new String(str), m.getSendTime(), m.getType() };
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
					// ��� ChatActivity����ǰ̨����֪ͨ
					if (!NoticeUtil.isTopActivity(
							ChatActivity.getActivityName(),
							ChatActivity.instance)) {
						NoticeUtil.SetNotice(ChatActivity.instance,
								NoticeUtil.MES_CHAT, NoticeUtil.NoticeType,
								message);
					}
				} else if (m.getType().equals(MCMessageType.RET_ONLINE_FRIENDS)) {// ����Ǻ����б�
					Util.buddyStr = new String(m.getContent());
					System.out.println("buddystr:" + BuddyActivity.buddyStr);
//					Intent intent = new Intent("yusi");
//					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.REQ_CONNECT)) { // /��ʾ����������(������)��dialog
					Util.otherDeviceId = m.getDeviceId();// ����Է����豸ID
					Util.otherCode = new String(m.getContent());// ����Է��������
					System.out.println("��ClientConServerThread���Է����豸ID"
							+ Util.otherDeviceId);
					System.out.println("�Է��������" + Util.otherCode);
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.REQ_CONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.CONFIRM_CONNECT)) { // ȷ������(������)����ת���������
					if (m.getDeviceId() != null) {
						Util.otherDeviceId = m.getDeviceId();// ����Է����豸ID
					}
					if (m.getContent() != null) {
						Util.otherCode = new String(m.getContent());// ����Է��������
					}
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.CONFIRM_CONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.CONNECT_SUCCESS)) { // ��ʾ�������ӳɹ�(������),���������
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.CONNECT_SUCCESS);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.FAIL)) { // //��ʾ��������ʧ��(������)
					Toast.makeText(context, "����ʧ�ܣ��Է���æ�����ߣ�",
							Toast.LENGTH_SHORT).show();
				} else if (m.getType().equals(MCMessageType.REFUSE_CONNECT)) { // ///��ʾ�Է��ܾ�����(������)
					Intent intent = new Intent("isconnect");
					intent.putExtra("type", MCMessageType.REFUSE_CONNECT);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(
						MCMessageType.HELLO_ONLINE_FRIENDS)) {
					Util.buddyStr = new String(m.getContent());
					System.out.println(Util.buddyStr);
					Intent intent  = new Intent(context,McServer.class);
					context.startService(intent);
				} else if (m.getType().equals(MCMessageType.REQ_DISCONNECT)) {// ����Ͽ����ӣ���������
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.REQ_DISCONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.SEND_FILE)) { 
					//byte[] b = m.getContent();
					System.out.println("�ļ����տ�ʼ");
					File file = getFileFromBytes(
							Util.encrypt(m.getContent(), 2),  //�������ļ���Ϣ����
							Environment.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/"
									+ m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/" + m.getExt());
					System.out.println("�ļ��������");
					// �Ѵӷ�������õ���Ϣͨ���㲥����
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] {
							m.getSender() + "",
							"�ҷ���һ���ļ�����" + file.getAbsolutePath(),
							m.getSendTime(), m.getType(),""};
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.SEND_PIC)) {// ͼƬ
					System.out.println("ͼƬ���տ�ʼ");
					byte[] b = m.getContent();
					b = Util.encrypt(b, 2);
					m.setExt(MyTime.geVoiceTime(2)+".jpg");
					
					System.out.println("�������ģ�"+m.getExt());
					getFileFromBytes(b, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println("�ļ��������"); 
					m.setExt(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/"+m.getExt());
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] {
							m.getSender() + "",
							"",
							MyTime.geTime(), m.getType(),m.getExt()};
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.SEND_REC)) {// ����
					System.out.println("�ļ����տ�ʼ");
					byte[] b = m.getContent();
					b = Util.encrypt(b, 2);
					getFileFromBytes(b, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println("�ļ��������"); 
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] {
							m.getSender() + "",
							"������Ϣ",
							m.getSendTime(), m.getType(),m.getExt()};
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
				}
			} catch (Exception e) {
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private File getFileFromBytes(byte[] b, String filename) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			stream = new BufferedOutputStream(fos);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

}
