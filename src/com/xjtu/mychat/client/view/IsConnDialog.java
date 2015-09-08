package com.xjtu.mychat.client.view;

import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.MyTime;
import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.R;
import com.xjtu.mychat.client.model.SendMessage;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class IsConnDialog extends Activity {
	TextView textView;
	Button yes, no;
	String[] mes;
	public static IsConnDialog instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		instance = this;
		setContentView(R.layout.activity_is_conn_dialog);
		textView = (TextView) findViewById(R.id.text_isconn);
		yes = (Button) findViewById(R.id.yes);
		no = (Button) findViewById(R.id.no);

		int noticetype = getIntent().getIntExtra("Noticetype", 0);
		if (noticetype == 1) {
			// ��ʾIsConnDialog����һ��ҳ����BuddyActivity�������ǰ̨������ں�̨ʱ�Ѿ�����ʾ������Ҫ�ظ���ʾ��
			// ��Ϣ����
			final MediaPlayer alarmMusic = MediaPlayer.create(this,
					R.raw.notice);
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
				// ����
				vibrator.vibrate(500);
			} else if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_SOUND)) {
				// ������
				alarmMusic.start();
				alarmMusic.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						alarmMusic.release();
					}
				});
			} else if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_ALL)) {
				// ��������
				vibrator.vibrate(500);
				alarmMusic.start();
				alarmMusic.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						alarmMusic.release();
					}
				});
			}
		}

		Intent intent = getIntent();
		mes = intent.getStringArrayExtra("conn_mes");
		String mytext = mes[0] + "��" + MyTime.geTime() + "������������,�Ƿ�ͬ�⣿";
		textView.setText(mytext);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SendMessage.sendConnectMes(Util.me.getAccount(), mes[0],
						MCMessageType.ALLOW_CONNECT);// ͬ����������
				finish();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SendMessage.sendConnectMes(Util.me.getAccount(), mes[0],
						MCMessageType.REFUSE_CONNECT);// �ܾ���������
				finish();
			}
		});
	}

	public static String getActivityName() {
		return "IsConnDialog";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.is_conn_dialog, menu);
		return true;
	}

}
