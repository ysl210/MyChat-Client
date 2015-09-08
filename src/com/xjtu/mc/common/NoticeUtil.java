package com.xjtu.mc.common;

import java.util.List;

import com.xjtu.mychat.R;
import com.xjtu.mychat.client.view.ChatActivity;
import com.xjtu.mychat.client.view.IsConnDialog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NoticeUtil {
	/*
	 * 消息提醒的类型及设置
	 */

	public static String DEFAULT_VIBRATE = "1"; // 仅震动
	public static String DEFAULT_SOUND = "2"; // 仅声音
	public static String DEFAULT_ALL = "3"; // 声音加震动

	public static String NoticeType = DEFAULT_VIBRATE; // 提醒的类型默认为震动
	// ---------------------------------------------------------
	// 消息类型（请求消息和聊天消息）
	public static String MES_RES = "1"; // 表示为请求消息
	public static String MES_CHAT = "2"; // 表示为聊天消息

	// ---------------------------------------------------------- 

	/**
	 * 设置消息提醒
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * @param mes_type
	 *            消息类型
	 * @param noticeType
	 *            消息提示的类型，用NoticeUtil.NoticeType
	 * 
	 * @param mes
	 *            消息的来源
	 */
	@SuppressWarnings("deprecation")
	public static void SetNotice(Context context, String mes_type,
			String noticeType, final String[] mes) {
		 
		// 创建一个NotificationManager的引用
		Intent notificationIntent = new Intent();
		CharSequence noticeTitle = "";
		CharSequence noticeText = "";
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 定义Notification的各种属性
		int icon = R.drawable.logo; // 通知图标
		long when = System.currentTimeMillis(); // 通知产生的时间，会在通知信息里显示

		if (mes_type.equals(NoticeUtil.MES_RES)) {

			noticeTitle = mes[0] + "请求与你会话";
			noticeText = "";
			notificationIntent.setClass(context, IsConnDialog.class);
			notificationIntent.putExtra("conn_mes", mes);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} else if (mes_type.equals(NoticeUtil.MES_CHAT)) {
			noticeTitle = mes[0] + "发来一条消息";
			noticeText = mes[0] + "：" + mes[1];
			notificationIntent.setClass(context, ChatActivity.class);
		}

		// 用上面的属性初始化 Nofification
		Notification notification = new Notification(icon, noticeTitle, when);

		if (noticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
			notification.defaults |= Notification.DEFAULT_VIBRATE; // 震动
		} else if (noticeType.equals(NoticeUtil.DEFAULT_SOUND)) {
			notification.defaults |= Notification.DEFAULT_SOUND; // 声音
		} else if (noticeType.equals(NoticeUtil.DEFAULT_ALL)) {
			notification.defaults |= Notification.DEFAULT_ALL; // 声音+震动
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 设置通知的事件消息
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, noticeTitle, noticeText,
				contentIntent);
		// 把Notification传递给 NotificationManager
		mNotificationManager.notify(icon, notification);
	}
	
	// 查看activity是否在前台
	public static boolean isTopActivity(String cmdName, Context context) {
		if (context!=null) {
			cmdName = "ComponentInfo{com.xjtu.mychat/com.xjtu.mychat.client.view."
					+ cmdName + "}";

			ActivityManager manager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> runningTaskInfos = manager
					.getRunningTasks(Integer.MAX_VALUE);
			String cmpNameTemp = null;
			if (null != runningTaskInfos) {
				cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
			}

			if (null == cmpNameTemp) {
				return false;
			} 
			return cmpNameTemp.equals(cmdName);
		}else {
			return false;
		}
		

	}

}
