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
	private String chatContent;// 消息内容
	ListView chatListView;
	Dialog dialog;
	private boolean isVoice = false;
	public List<ChatEntity> chatEntityList = new ArrayList<ChatEntity>();// 所有聊天内容
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
		// 设置top面板信息
		instance = this;

		// -------创建保存语音和图片的文件夹----------------
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			System.out.println("SD卡不存在");
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
		int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		et_input.setWidth((int) (width * (0.6)));
		// send_voice.setOnClickListener(new ButtonListener());
		send_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				 
				// TODO Auto-generated method stub
				if (isVoice) {
					isVoice = false;
					send_voice.setText("语音");
					et_input.setVisibility(EditText.VISIBLE);
					send.setVisibility(Button.VISIBLE);
					speek_voice.setVisibility(Button.INVISIBLE);
				} else {
					isVoice = true;
					et_input.setVisibility(EditText.INVISIBLE);
//					et_input.setFocusable(false);
					send.setVisibility(Button.INVISIBLE);
					speek_voice.setVisibility(Button.VISIBLE);
					send_voice.setText("文字");
				}
			}
		});
		speek_voice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ChatActivity.this, "SD卡不存在，请插入SD卡",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("test", "录音 ---> 完成");
						dialog.dismiss();
						voiceStop();
						sendVoice();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ChatActivity.this, "SD卡不存在，请插入SD卡",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("test", "录音 ---> 开始");
						dialog = ProgressDialog.show(ChatActivity.this, null,
								"正录音中 …", true, true);
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
						.setTitle("其他")
						.setItems(new String[] { "发送文件", "发送图片" },
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											// 发送文件
											flietype = 1;
											Intent intent = new Intent(
													getApplicationContext(),
													FileBrowserActivity.class);
											intent.putExtra("value", flietype);
											startActivityForResult(intent, 0);
											Toast.makeText(ChatActivity.this,
													"仅能发送小于300KB的文件",
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
													"仅能发送小于300KB的图片",
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
					Toast.makeText(getApplicationContext(), "请输入内容",
							Toast.LENGTH_LONG).show();
				} else {
					// 得到输入的数据，并清空EditText

					chatContent = et_input.getText().toString();
					et_input.setText("");
					// 更新聊天内容-----------------------------------------------------
					updateChatView(new ChatEntity(Util.me.getAccount(),
							chatContent, MyTime.geTime(),
							MCMessageType.COM_MES, false));
					// 发送消息
					byte[] str = chatContent.getBytes();
					str = Util.encrypt(str, 1);// 加密
					System.out.println("(ChatActivity):" + new String(str));
					SendMessage
							.sendMes(chatAccount, str, MCMessageType.COM_MES);
				}

			}
		});
		// 注册广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("org.yhn.yq.mes");
		br = new MyBroadcastReceiver();
		registerReceiver(br, myIntentFilter);
		ManageActivity.addActiviy("ChatActivity", this);
	}

	public void sendVoice() {
		// 发送语音
		MCMessage m_voice = new MCMessage();
		File file;
		file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/mychat/" + myVoiceTime + ".amr");
		System.out.println("设置的文件路径----------"
				+ Environment.getExternalStorageDirectory().getAbsoluteFile()
				+ "/mychat/" + myVoiceTime + ".amr");
		byte[] b = FileToByte(file);
		b = Util.encrypt(b, 1);// 给语音消息加密
		m_voice.setContent(b);
		m_voice.setExt(myVoiceTime + ".amr");
		m_voice.setSender(Util.me.getAccount());
		m_voice.setReceiver(chatAccount);
		m_voice.setType(MCMessageType.SEND_REC);
		m_voice.setSendTime(MyTime.geTime());
		SendMessage.sendMes(ChatActivity.this, m_voice);
		ChatEntity chatEntity = new ChatEntity(m_voice.getSender(), "语音消息",
				MyTime.geTime(), MCMessageType.SEND_REC, false,
				m_voice.getExt());
		updateChatView(chatEntity);
	}

	public void voiceStop() {
		// 结束录音
		if (soundFile != null && soundFile.exists()) {
			md.stop();
			md.release();
			md = null;
		}
	}

	public void voiceBegin() {
		// 开始录音
		myVoiceTime = MyTime.geVoiceTime(1);
		try {
			soundFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/mychat/" + myVoiceTime + ".amr");
			System.out.println("录音路径----------"
					+ Environment.getExternalStorageDirectory()
							.getAbsoluteFile() + "/mychat/" + myVoiceTime
					+ ".amr");
			md = new MediaRecorder();
			// 设置声音来源
			md.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置录制声音的输出格式（必须在设置声音编码格式之前设置）
			md.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// 设置声音的编码格式
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
			System.out.println("get的filename:" + filename);
			System.out.println("绝对路径--------------" + path);
			System.out.println("文件名----------------" + filename);

			MCMessage fm = new MCMessage();
			File file = new File(path);

			FileInputStream fis;
			int leng = 0;
			try {
				fis = new FileInputStream(file);
				leng = fis.available();
				System.out.println("文件大小--------" + fis.available());
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (leng > 307200) {
				Toast.makeText(getApplicationContext(), "对不起，无法发送大于300KB的文件",
						Toast.LENGTH_LONG).show();
			} else {
				byte[] b = FileToByte(file);
				b = Util.encrypt(b, 1);
				fm.setContent(b);

				fm.setSender(Util.me.getAccount());
				fm.setReceiver(chatAccount);
				if (flietype == 1) {// 发文件
					fm.setExt(filename);
					fm.setType(MCMessageType.SEND_FILE);
					SendMessage.sendMes(ChatActivity.this, fm);
					updateChatView(new ChatEntity(Util.me.getAccount(),
							"我发送了一个文件", MyTime.geTime(), MCMessageType.COM_MES,
							false));
				} else {// 发图片
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

	// 文件转换字节数组
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

	// 广播接收器
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String[] mes = intent.getStringArrayExtra("message");
			// 判断消息提醒的类型
			// 播放音频使用
			final MediaPlayer alarmMusic = MediaPlayer.create(context,
					R.raw.notice);
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (NoticeUtil.isTopActivity(ChatActivity.getActivityName(),
					ChatActivity.this)) {
				//表示chatactvity在当前页面，则消息提醒
				if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
					// 仅震动
					vibrator.vibrate(500);
				} else if (NoticeUtil.NoticeType
						.equals(NoticeUtil.DEFAULT_SOUND)) {
					// 仅铃声
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
					// 铃声加震动
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

			// 更新聊天内容
			if (mes[3].equals(MCMessageType.SEND_REC)) { // 收到的是语音
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
		// 更新聊天内容以及监听点击事件
		chatEntityList.add(chatEntity);
		chatListView = (ListView) findViewById(R.id.lv_chat);
		chatListView.setAdapter(new ChatAdapter(this, chatEntityList));
		chatListView.setSelection(chatListView.getBottom());
	}

	public static String getActivityName() {
		return "ChatActivity";
	}

	// 删除文件夹及文件
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

	// 关闭聊天窗口的时候发送关闭连接信息
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			closeConnect();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME
				&& event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}

	// 关闭连接
	void closeConnect() {
		new AlertDialog.Builder(ChatActivity.this).setTitle("关闭连接")
				.setMessage("是否关闭与" + chatAccount + "的会话！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						SendMessage.sendConnectMes(Util.me.getAccount(),
								chatAccount, MCMessageType.REQ_DISCONNECT);
						delete(dir);// 退出时删除语音文件夹和文件
						Intent intent = new Intent(getApplicationContext(),
								BuddyActivity.class);
						unregisterReceiver(br);
						ChatActivity.this.startActivity(intent);
						ChatActivity.this.finish();
					}
				}).setNegativeButton("取消", null).show();
	}
	// public void unregisterMyReceiver() {
	// unregisterReceiver(br);
	// }

}
