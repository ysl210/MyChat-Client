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
		if (type.equals(MCMessageType.REQ_CONNECT)) { // /��ʾ����������(������) ��dialog
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			if (NoticeUtil.isTopActivity(IsConnDialog.getActivityName(),
					IsConnDialog.instance)) {
				// ��ǰ�Ѿ�������
				Intent intent = new Intent(arg0, IsConnDialog.class);
				intent.putExtra("conn_mes", mes);
				intent.putExtra("Noticetype", 1);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent);
			} else if (NoticeUtil.isTopActivity(
					BuddyActivity.getActivityName(), BuddyActivity.instance)) {
				// ��ǰ���ں����б���
				// ����һ��dialog���ж��Ƿ�ͬ������
				Intent intent = new Intent(arg0, IsConnDialog.class);
				intent.putExtra("conn_mes", mes);
				intent.putExtra("Noticetype", 1);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent);
			} else {
				// ��ǰ������������
				System.out.println("��Ϣ����------------"+NoticeUtil.NoticeType);
				NoticeUtil.SetNotice(arg0, NoticeUtil.MES_RES,
						NoticeUtil.NoticeType, mes);
			}
		} else if (type.equals(MCMessageType.CONFIRM_CONNECT)) { // ȷ������(������)����ת���������
			// ������ҳ��
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			Intent intent = new Intent(arg0, ChatActivity.class);
			intent.putExtra("account", mes[0]);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intent);
		} else if (type.equals(MCMessageType.CONNECT_SUCCESS)) { // ��ʾ�������ӳɹ�(������),���������
			// ������ҳ��
			String[] mes = arg1.getStringArrayExtra("conn_mes");
			Intent intent = new Intent(arg0, ChatActivity.class);
			intent.putExtra("account", mes[0]);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intent);
		} else if (type.equals(MCMessageType.REQ_DISCONNECT)) { // ��������
			if (ChatActivity.instance != null) {
//				ChatActivity.instance.unregisterMyReceiver();
				ChatActivity.instance.finish();
				Intent intent = new Intent(arg0, BuddyActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(intent); 
			}
			Toast.makeText(arg0, "���Է��Ѿ��ر����ӣ�", Toast.LENGTH_LONG).show();
		} else if (type.equals(MCMessageType.REFUSE_CONNECT)) {
			Toast.makeText(arg0, "�Է���æ��", Toast.LENGTH_LONG).show();
		}

	}

}
