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
	 * ��Ϣ���ѵ����ͼ�����
	 */

	public static String DEFAULT_VIBRATE = "1"; // ����
	public static String DEFAULT_SOUND = "2"; // ������
	public static String DEFAULT_ALL = "3"; // ��������

	public static String NoticeType = DEFAULT_VIBRATE; // ���ѵ�����Ĭ��Ϊ��
	// ---------------------------------------------------------
	// ��Ϣ���ͣ�������Ϣ��������Ϣ��
	public static String MES_RES = "1"; // ��ʾΪ������Ϣ
	public static String MES_CHAT = "2"; // ��ʾΪ������Ϣ

	// ---------------------------------------------------------- 

	/**
	 * ������Ϣ����
	 * 
	 * @param context
	 *            Ӧ�ó��������Ļ���
	 * @param mes_type
	 *            ��Ϣ����
	 * @param noticeType
	 *            ��Ϣ��ʾ�����ͣ���NoticeUtil.NoticeType
	 * 
	 * @param mes
	 *            ��Ϣ����Դ
	 */
	@SuppressWarnings("deprecation")
	public static void SetNotice(Context context, String mes_type,
			String noticeType, final String[] mes) {
		 
		// ����һ��NotificationManager������
		Intent notificationIntent = new Intent();
		CharSequence noticeTitle = "";
		CharSequence noticeText = "";
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// ����Notification�ĸ�������
		int icon = R.drawable.logo; // ֪ͨͼ��
		long when = System.currentTimeMillis(); // ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ

		if (mes_type.equals(NoticeUtil.MES_RES)) {

			noticeTitle = mes[0] + "��������Ự";
			noticeText = "";
			notificationIntent.setClass(context, IsConnDialog.class);
			notificationIntent.putExtra("conn_mes", mes);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} else if (mes_type.equals(NoticeUtil.MES_CHAT)) {
			noticeTitle = mes[0] + "����һ����Ϣ";
			noticeText = mes[0] + "��" + mes[1];
			notificationIntent.setClass(context, ChatActivity.class);
		}

		// ����������Գ�ʼ�� Nofification
		Notification notification = new Notification(icon, noticeTitle, when);

		if (noticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
			notification.defaults |= Notification.DEFAULT_VIBRATE; // ��
		} else if (noticeType.equals(NoticeUtil.DEFAULT_SOUND)) {
			notification.defaults |= Notification.DEFAULT_SOUND; // ����
		} else if (noticeType.equals(NoticeUtil.DEFAULT_ALL)) {
			notification.defaults |= Notification.DEFAULT_ALL; // ����+��
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// ����֪ͨ���¼���Ϣ
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, noticeTitle, noticeText,
				contentIntent);
		// ��Notification���ݸ� NotificationManager
		mNotificationManager.notify(icon, notification);
	}
	
	// �鿴activity�Ƿ���ǰ̨
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
