package com.xjtu.mychat.client.view;

import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.NoticeUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class ConnectReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String type = arg1.getStringExtra("type");
		if (type.equals(MCMessageType.REQ_CONNECT)) { // /表示被请求连接(被动方) 到dialog
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			if (NoticeUtil.isTopActivity(IsConnDialog.getActivityName(),
					IsConnDialog.instance)) {
				// 当前已经有请求
				Intent intent = new Intent(arg0, IsConnDialog.class);
				intent.putExtra("conn_mes", mes);
				intent.putExtra("Noticetype", 1);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent);
			} else if (NoticeUtil.isTopActivity(
					BuddyActivity.getActivityName(), BuddyActivity.instance)) {
				// 当前就在好友列表中
				// 启动一个dialog，判断是否同意连接
				Intent intent = new Intent(arg0, IsConnDialog.class);
				intent.putExtra("conn_mes", mes);
				intent.putExtra("Noticetype", 1);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent);
			} else {
				// 当前在其他程序内
				System.out.println("消息类型------------"+NoticeUtil.NoticeType);
				NoticeUtil.SetNotice(arg0, NoticeUtil.MES_RES,
						NoticeUtil.NoticeType, mes);
			}
		} else if (type.equals(MCMessageType.CONFIRM_CONNECT)) { // 确认连接(被动方)，跳转到聊天界面
			// 打开聊天页面
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			Intent intent = new Intent(arg0, ChatActivity.class);
			intent.putExtra("account", mes[0]);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intent);
		} else if (type.equals(MCMessageType.CONNECT_SUCCESS)) { // 表示请求连接成功(主动方),到聊天界面
			// 打开聊天页面
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			Intent intent = new Intent(arg0, ChatActivity.class);
			intent.putExtra("account", mes[0]);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intent);
		} else if (type.equals(MCMessageType.REQ_DISCONNECT)) { // 结束连接
			if (ChatActivity.instance != null) {
//				ChatActivity.instance.unregisterMyReceiver();
				ChatActivity.instance.finish();
				Intent intent = new Intent(arg0, BuddyActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent); 
			}
			Toast.makeText(arg0, "你或对方已经关闭连接！", Toast.LENGTH_LONG).show();
		} else if (type.equals(MCMessageType.REFUSE_CONNECT)) {
			Toast.makeText(arg0, "对方正忙！", Toast.LENGTH_LONG).show();
		}

	}

}
