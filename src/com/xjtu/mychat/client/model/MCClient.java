package com.xjtu.mychat.client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.xjtu.mc.common.MCMessage;
import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.User;
import com.xjtu.mychat.client.view.Info;
import com.xjtu.mychat.client.view.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MCClient {
	private Context context;
	public Socket s;
	private String ip;
	private int port;
	SharedPreferences sharedPreferences;

	public MCClient(Context context) {
		sharedPreferences = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		this.context = context;
		this.ip = sharedPreferences.getString("ip", "");
		this.port = sharedPreferences.getInt("port", 5469);
	}

	public boolean sendLoginInfo(Object obj) {
		boolean b = false;
		try {
			s = new Socket();
			try {
				s.connect(new InetSocketAddress(this.ip, this.port), 2000);
				s.setKeepAlive(true);
				System.out.println(this.ip);
			} catch (SocketTimeoutException e) {
				// 连接服务器超时
				System.out.println("超时");
				return false;
			}
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(obj);
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			MCMessage ms = (MCMessage) ois.readObject();
			System.out.println("Type" + ms.getType());
			if (ms.getType().equals(MCMessageType.SUCCESS)) {
				Info.myInfo = new String(ms.getContent());  
				// 创建一个该账号和服务器保持连接的线程
				ClientConServerThread ccst = new ClientConServerThread(context,
						s);
				// 启动该通信线程
				ccst.start();
				// 加入到管理类中
				ManageClientConServer.addClientConServerThread(
						((User) obj).getAccount(), ccst);
				b = true;
			} else if (ms.getType().equals(MCMessageType.ALREADY_LOGIN)) {
				LoginActivity.isConnError = false;
				Toast.makeText(context, "你的账号已经登录", Toast.LENGTH_LONG).show();
				b = false;
			} else if (ms.getType().equals(MCMessageType.LOGIN_FAIL)) {
				LoginActivity.isConnError = false;
				Toast.makeText(context, "账号或者密码错误", Toast.LENGTH_LONG).show();
				b = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}
 
	public void sendLogoutInfo(Object obj) {
		try {
			s = new Socket();
			try {
				s.connect(new InetSocketAddress(this.ip, this.port), 2000);
			} catch (SocketTimeoutException e) {
			}
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
