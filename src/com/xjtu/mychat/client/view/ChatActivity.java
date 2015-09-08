package com.xjtu.mychat.client.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.xjtu.mc.common.MCMessage;
import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.MyTime;
import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.R;
import com.xjtu.mychat.client.model.SendMessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	int flietype = 0;
	EditText et_input;
	Button send_other, send_voice, speek_voice, send;
	private String chatContent;// ��Ϣ����
	ListView chatListView;
	Dialog dialog;
	private boolean isVoice = false;
	public List<ChatEntity> chatEntityList = new ArrayList<ChatEntity>();// ������������
	private String chatAccount;
	private File soundFile;
	private MediaRecorder md;
	public static int[] avatar = new int[] { R.drawable.avatar_default,
			R.drawable.h001, R.drawable.h002, R.drawable.h003, R.drawable.h004,
			R.drawable.h005, R.drawable.h006, R.drawable.group_avatar };
	static MyBroadcastReceiver br;

	File dir;
	String myVoiceTime = "";
	public static ChatActivity instance = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		// ����top�����Ϣ
		instance = this;

		// -------��������������ͼƬ���ļ���----------------
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			System.out.println("SD��������");
			return;
		} else {
			String DATABASE_PATH;
			DATABASE_PATH = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/mychat";
			dir = new File(DATABASE_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}
		send = (Button) findViewById(R.id.ib_send);
		speek_voice = (Button) findViewById(R.id.press_voice);
		chatAccount = getIntent().getStringExtra("account");
		send_voice = (Button) findViewById(R.id.send_voice);
		ImageView avatar_iv = (ImageView) findViewById(R.id.chat_top_avatar);
		avatar_iv.setImageResource(ChatActivity.avatar[2]);
		TextView nick_tv = (TextView) findViewById(R.id.chat_top_nick);
		et_input = (EditText) findViewById(R.id.et_input);
		send_other = (Button) findViewById(R.id.send_other);
		nick_tv.setText(chatAccount);

		speek_voice.setVisibility(Button.INVISIBLE);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();// ��Ļ���
		et_input.setWidth((int) (width * (0.6)));
		// send_voice.setOnClickListener(new ButtonListener());
		send_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				 
				// TODO Auto-generated method stub
				if (isVoice) {
					isVoice = false;
					send_voice.setText("����");
					et_input.setVisibility(EditText.VISIBLE);
					send.setVisibility(Button.VISIBLE);
					speek_voice.setVisibility(Button.INVISIBLE);
				} else {
					isVoice = true;
					et_input.setVisibility(EditText.INVISIBLE);
//					et_input.setFocusable(false);
					send.setVisibility(Button.INVISIBLE);
					speek_voice.setVisibility(Button.VISIBLE);
					send_voice.setText("����");
				}
			}
		});
		speek_voice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ChatActivity.this, "SD�������ڣ������SD��",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("test", "¼�� ---> ���");
						dialog.dismiss();
						voiceStop();
						sendVoice();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ChatActivity.this, "SD�������ڣ������SD��",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("test", "¼�� ---> ��ʼ");
						dialog = ProgressDialog.show(ChatActivity.this, null,
								"��¼���� ��", true, true);
						voiceBegin();
					}
				}
				return false;
			}
		});
		send_other.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(ChatActivity.this)
						.setTitle("����")
						.setItems(new String[] { "�����ļ�", "����ͼƬ" },
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											// �����ļ�
											flietype = 1;
											Intent intent = new Intent(
													getApplicationContext(),
													FileBrowserActivity.class);
											intent.putExtra("value", flietype);
											startActivityForResult(intent, 0);
											Toast.makeText(ChatActivity.this,
													"���ܷ���С��300KB���ļ�",
													Toast.LENGTH_LONG).show();
											break;

										case 1:
											flietype = 0;
											Intent intent1 = new Intent(
													getApplicationContext(),
													FileBrowserActivity.class);
											intent1.putExtra("value", flietype);
											startActivityForResult(intent1, 0);
											Toast.makeText(ChatActivity.this,
													"���ܷ���С��300KB��ͼƬ",
													Toast.LENGTH_LONG).show();
											break;
										}
									}
								}).show();
			}
		});

		findViewById(R.id.ib_send).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if ("".equals(et_input.getText().toString().trim())) {
					Toast.makeText(getApplicationContext(), "����������",
							Toast.LENGTH_LONG).show();
				} else {
					// �õ���������ݣ������EditText

					chatContent = et_input.getText().toString();
					et_input.setText("");
					// ������������-----------------------------------------------------
					updateChatView(new ChatEntity(Util.me.getAccount(),
							chatContent, MyTime.geTime(),
							MCMessageType.COM_MES, false));
					// ������Ϣ
					byte[] str = chatContent.getBytes();
					str = Util.encrypt(str, 1);// ����
					System.out.println("(ChatActivity):" + new String(str));
					SendMessage
							.sendMes(chatAccount, str, MCMessageType.COM_MES);
				}

			}
		});
		// ע��㲥
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("org.yhn.yq.mes");
		br = new MyBroadcastReceiver();
		registerReceiver(br, myIntentFilter);
		ManageActivity.addActiviy("ChatActivity", this);
	}

	public void sendVoice() {
		// ��������
		MCMessage m_voice = new MCMessage();
		File file;
		file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/mychat/" + myVoiceTime + ".amr");
		System.out.println("���õ��ļ�·��----------"
				+ Environment.getExternalStorageDirectory().getAbsoluteFile()
				+ "/mychat/" + myVoiceTime + ".amr");
		byte[] b = FileToByte(file);
		b = Util.encrypt(b, 1);// ��������Ϣ����
		m_voice.setContent(b);
		m_voice.setExt(myVoiceTime + ".amr");
		m_voice.setSender(Util.me.getAccount());
		m_voice.setReceiver(chatAccount);
		m_voice.setType(MCMessageType.SEND_REC);
		m_voice.setSendTime(MyTime.geTime());
		SendMessage.sendMes(ChatActivity.this, m_voice);
		ChatEntity chatEntity = new ChatEntity(m_voice.getSender(), "������Ϣ",
				MyTime.geTime(), MCMessageType.SEND_REC, false,
				m_voice.getExt());
		updateChatView(chatEntity);
	}

	public void voiceStop() {
		// ����¼��
		if (soundFile != null && soundFile.exists()) {
			md.stop();
			md.release();
			md = null;
		}
	}

	public void voiceBegin() {
		// ��ʼ¼��
		myVoiceTime = MyTime.geVoiceTime(1);
		try {
			soundFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/mychat/" + myVoiceTime + ".amr");
			System.out.println("¼��·��----------"
					+ Environment.getExternalStorageDirectory()
							.getAbsoluteFile() + "/mychat/" + myVoiceTime
					+ ".amr");
			md = new MediaRecorder();
			// ����������Դ
			md.setAudioSource(MediaRecorder.AudioSource.MIC);
			// ����¼�������������ʽ���������������������ʽ֮ǰ���ã�
			md.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// ���������ı����ʽ
			md.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			md.setOutputFile(soundFile.getAbsolutePath());
			md.prepare();
			// start
			md.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0) {
			String path = data.getStringExtra("path");
			String filename = data.getStringExtra("filename");
			System.out.println("get��filename:" + filename);
			System.out.println("����·��--------------" + path);
			System.out.println("�ļ���----------------" + filename);

			MCMessage fm = new MCMessage();
			File file = new File(path);

			FileInputStream fis;
			int leng = 0;
			try {
				fis = new FileInputStream(file);
				leng = fis.available();
				System.out.println("�ļ���С--------" + fis.available());
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (leng > 307200) {
				Toast.makeText(getApplicationContext(), "�Բ����޷����ʹ���300KB���ļ�",
						Toast.LENGTH_LONG).show();
			} else {
				byte[] b = FileToByte(file);
				b = Util.encrypt(b, 1);
				fm.setContent(b);

				fm.setSender(Util.me.getAccount());
				fm.setReceiver(chatAccount);
				if (flietype == 1) {// ���ļ�
					fm.setExt(filename);
					fm.setType(MCMessageType.SEND_FILE);
					SendMessage.sendMes(ChatActivity.this, fm);
					updateChatView(new ChatEntity(Util.me.getAccount(),
							"�ҷ�����һ���ļ�", MyTime.geTime(), MCMessageType.COM_MES,
							false));
				} else {// ��ͼƬ
					fm.setExt(path);
					fm.setType(MCMessageType.SEND_PIC);
					SendMessage.sendMes(ChatActivity.this, fm);
					updateChatView(new ChatEntity(Util.me.getAccount(), "",
							MyTime.geTime(), MCMessageType.SEND_PIC, false,
							fm.getExt()));
				}
			}

		}
	}

	// �ļ�ת���ֽ�����
	private byte[] FileToByte(File file) {
		if (file == null) {
			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				out.write(b, 0, n);
			}
			fis.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	// �㲥������
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String[] mes = intent.getStringArrayExtra("message");
			// �ж���Ϣ���ѵ�����
			// ������Ƶʹ��
			final MediaPlayer alarmMusic = MediaPlayer.create(context,
					R.raw.notice);
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (NoticeUtil.isTopActivity(ChatActivity.getActivityName(),
					ChatActivity.this)) {
				//��ʾchatactvity�ڵ�ǰҳ�棬����Ϣ����
				if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
					// ����
					vibrator.vibrate(500);
				} else if (NoticeUtil.NoticeType
						.equals(NoticeUtil.DEFAULT_SOUND)) {
					// ������
					alarmMusic.start();
					alarmMusic
							.setOnCompletionListener(new OnCompletionListener() {
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
					alarmMusic
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer arg0) {
									// TODO Auto-generated method stub
									alarmMusic.release();
								}
							});
				}
			}

			// ������������
			if (mes[3].equals(MCMessageType.SEND_REC)) { // �յ���������
				updateChatView(new ChatEntity(mes[0], mes[1], mes[2], mes[3],
						true, mes[4]));
			} else if (mes[3].equals(MCMessageType.COM_MES)
					|| mes[3].equals(MCMessageType.SEND_FILE)) {
				updateChatView(new ChatEntity(mes[0], mes[1], mes[2], mes[3],
						true));
			} else if (mes[3].equals(MCMessageType.SEND_PIC)) {
				updateChatView(new ChatEntity(mes[0], mes[1], mes[2], mes[3],
						true, mes[4]));
			}
		}
	}

	public void updateChatView(ChatEntity chatEntity) {
		// �������������Լ���������¼�
		chatEntityList.add(chatEntity);
		chatListView = (ListView) findViewById(R.id.lv_chat);
		chatListView.setAdapter(new ChatAdapter(this, chatEntityList));
		chatListView.setSelection(chatListView.getBottom());
	}

	public static String getActivityName() {
		return "ChatActivity";
	}

	// ɾ���ļ��м��ļ�
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	// �ر����촰�ڵ�ʱ���͹ر�������Ϣ
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			closeConnect();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME
				&& event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}

	// �ر�����
	void closeConnect() {
		new AlertDialog.Builder(ChatActivity.this).setTitle("�ر�����")
				.setMessage("�Ƿ�ر���" + chatAccount + "�ĻỰ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						SendMessage.sendConnectMes(Util.me.getAccount(),
								chatAccount, MCMessageType.REQ_DISCONNECT);
						delete(dir);// �˳�ʱɾ�������ļ��к��ļ�
						Intent intent = new Intent(getApplicationContext(),
								BuddyActivity.class);
						unregisterReceiver(br);
						ChatActivity.this.startActivity(intent);
						ChatActivity.this.finish();
					}
				}).setNegativeButton("ȡ��", null).show();
	}
	// public void unregisterMyReceiver() {
	// unregisterReceiver(br);
	// }

}
