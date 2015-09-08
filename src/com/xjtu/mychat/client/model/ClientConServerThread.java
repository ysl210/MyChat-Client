/**
 * 客户端和服务器端保持通信的线程
 * org.yhn.yq.mes广播
 * 不断地读取服务器发来的数据
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
				if (m.getType().equals(MCMessageType.COM_MES)) { // 如果是聊天消息
					byte[] str = m.getContent();
					str = Util.encrypt(str, 2);// 解密
					System.out.println("（ClientConServerThread）收到的消息："
							+ new String(str));
					// 把从服务器获得的消息通过广播发送
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] { m.getSender() + "",
							new String(str), m.getSendTime(), m.getType() };
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
					// 如果 ChatActivity不在前台则发送通知
					if (!NoticeUtil.isTopActivity(
							ChatActivity.getActivityName(),
							ChatActivity.instance)) {
						NoticeUtil.SetNotice(ChatActivity.instance,
								NoticeUtil.MES_CHAT, NoticeUtil.NoticeType,
								message);
					}
				} else if (m.getType().equals(MCMessageType.RET_ONLINE_FRIENDS)) {// 如果是好友列表
					Util.buddyStr = new String(m.getContent());
					System.out.println("buddystr:" + BuddyActivity.buddyStr);
//					Intent intent = new Intent("yusi");
//					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.REQ_CONNECT)) { // /表示被请求连接(被动方)到dialog
					Util.otherDeviceId = m.getDeviceId();// 保存对方的设备ID
					Util.otherCode = new String(m.getContent());// 保存对方的随机码
					System.out.println("（ClientConServerThread）对方的设备ID"
							+ Util.otherDeviceId);
					System.out.println("对方的随机码" + Util.otherCode);
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.REQ_CONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.CONFIRM_CONNECT)) { // 确认连接(被动方)，跳转到聊天界面
					if (m.getDeviceId() != null) {
						Util.otherDeviceId = m.getDeviceId();// 保存对方的设备ID
					}
					if (m.getContent() != null) {
						Util.otherCode = new String(m.getContent());// 保存对方的随机码
					}
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.CONFIRM_CONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.CONNECT_SUCCESS)) { // 表示请求连接成功(主动方),到聊天界面
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.CONNECT_SUCCESS);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.FAIL)) { // //表示请求连接失败(主动方)
					Toast.makeText(context, "请求失败，对方繁忙或不在线！",
							Toast.LENGTH_SHORT).show();
				} else if (m.getType().equals(MCMessageType.REFUSE_CONNECT)) { // ///表示对方拒绝连接(主动方)
					Intent intent = new Intent("isconnect");
					intent.putExtra("type", MCMessageType.REFUSE_CONNECT);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(
						MCMessageType.HELLO_ONLINE_FRIENDS)) {
					Util.buddyStr = new String(m.getContent());
					System.out.println(Util.buddyStr);
					Intent intent  = new Intent(context,McServer.class);
					context.startService(intent);
				} else if (m.getType().equals(MCMessageType.REQ_DISCONNECT)) {// 请求断开连接（被动方）
					Intent intent = new Intent("isconnect");
					String[] conn_mes = new String[] { m.getSender() + "",
							m.getSendTime() };
					intent.putExtra("type", MCMessageType.REQ_DISCONNECT);
					intent.putExtra("conn_mes", conn_mes);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.SEND_FILE)) { 
					//byte[] b = m.getContent();
					System.out.println("文件接收开始");
					File file = getFileFromBytes(
							Util.encrypt(m.getContent(), 2),  //给语音文件消息解密
							Environment.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/"
									+ m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/" + m.getExt());
					System.out.println("文件接收完毕");
					// 把从服务器获得的消息通过广播发送
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] {
							m.getSender() + "",
							"我发送一个文件，在" + file.getAbsolutePath(),
							m.getSendTime(), m.getType(),""};
					Log.i("--", message.toString());
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
				} else if (m.getType().equals(MCMessageType.SEND_PIC)) {// 图片
					System.out.println("图片接收开始");
					byte[] b = m.getContent();
					b = Util.encrypt(b, 2);
					m.setExt(MyTime.geVoiceTime(2)+".jpg");
					
					System.out.println("我命名的："+m.getExt());
					getFileFromBytes(b, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println("文件接收完毕"); 
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
				} else if (m.getType().equals(MCMessageType.SEND_REC)) {// 语音
					System.out.println("文件接收开始");
					byte[] b = m.getContent();
					b = Util.encrypt(b, 2);
					getFileFromBytes(b, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mychat/" + m.getExt());
					System.out.println("文件接收完毕"); 
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message = new String[] {
							m.getSender() + "",
							"语音消息",
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
