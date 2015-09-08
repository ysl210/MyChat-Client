package com.xjtu.mychat.client.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.User;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.R;
import com.xjtu.mychat.client.model.SendMessage;
import com.xjtu.mychat.client.model.MCClient;

public class BuddyActivity extends Activity {
	ListView listView, listView1;
	EditText find_edit;
	ImageButton find_image;
	public static boolean isStop = false;
	public static String buddyStr = "";
	// 好友列表
	BuddyAdapter ba;// 好友列表的adapter
	BuddyEntity temp;
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	public static BuddyActivity instance = null;
	MyThread mt = new MyThread();
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 1) {

				showList();
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_buddy);
		System.out.println("初始化:" + buddyStr);

		ImageView avatarIv = (ImageView) findViewById(R.id.buddy_top_avatar);
		TextView nickTv = (TextView) findViewById(R.id.buddy_top_nick);
		nickTv.setText(Util.me.getAccount());
		avatarIv.setImageDrawable(getResources().getDrawable(R.drawable.h001));
		listView = (ListView) findViewById(R.id.listview);
		find_edit = (EditText) findViewById(R.id.buddy_input_find);
		find_image = (ImageButton) findViewById(R.id.buddy_btn_find);
		instance = this;
		// 填充数据
		ba = new BuddyAdapter(this, firstJieXi(Info.myInfo));
		listView.setAdapter(ba);

		// 查询用户
		find_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!"".equals(find_edit.getText().toString().trim())) {
					showList();
				} else {
					Toast.makeText(getApplicationContext(), "请输入要查询的用户的关键字！",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		setListViewListener();

		mt.start();
	}

	public void showList() {
		ba = new BuddyAdapter(this, jieXi(Util.buddyStr));
		listView.setAdapter(ba);
	}

	private class MyThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (isStop) {
					isStop = false;
					return;
				}
				SendMessage.sendMes(null, null, MCMessageType.HELLO);
				Message m = new Message();
				m.what = 1;

				try {
					//Thread.sleep(1000);
					handler.sendMessage(m);
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 退出时向服务器发送退出消息
	void logout(String account) {
		User user = new User();
		user.setAccount(account);
		user.setOperation("logout");
		new MCClient(this).sendLogoutInfo(user);
	}

	private void setListViewListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long l) {

				temp = (BuddyEntity) listView.getItemAtPosition(position);

				new AlertDialog.Builder(BuddyActivity.this)
						.setTitle("发送请求")
						.setMessage("请求与" + temp.getAccount() + "进行会话！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@SuppressLint("NewApi")
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										Toast.makeText(BuddyActivity.this,
												"连接请求已经发出，请耐心等待！",
												Toast.LENGTH_LONG).show();
										SendMessage.sendConnectMes(
												Util.me.getAccount(),
												temp.getAccount(),
												MCMessageType.REQ_CONNECT);
									}
								}).setNegativeButton("取消", null).show();

			}
		});
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// 打开聊天页面
			Intent intent = new Intent(BuddyActivity.this, ChatActivity.class);
			intent.putExtra("avatar", temp.getAvatar());
			intent.putExtra("account", temp.getAccount());
			intent.putExtra("nick", temp.getNick());
			startActivity(intent);
			break;

		case 2:
			//
			break;
		}
		return super.onContextItemSelected(item);
	}

	private List<BuddyEntity> jieXi(String s) {
		List<BuddyEntity> buddyEntityList = new ArrayList<BuddyEntity>();
		String ss[] = s.split(" ");
		for (String a : ss) {
			if (a != "") {
				String b[] = a.split("_");

				if (!b[0].equals(Util.me.getAccount())) {
					if (!"".equals(find_edit.getText().toString().trim())) {
						if (b[0].contains(find_edit.getText().toString().trim())) {// 包含就加入
							buddyEntityList.add(new BuddyEntity(b[0]));
						}
					} else {
						buddyEntityList.add(new BuddyEntity(b[0]));
					}
				}
			}
		}
		return buddyEntityList;
	}

	private List<BuddyEntity> firstJieXi(String s) {
		List<BuddyEntity> buddyEntityList = new ArrayList<BuddyEntity>();
		System.out.println("Info_----"+s);
		String ss[] = s.split("_");
		System.out.println(ss);
		if (ss.length>1) {
			String sss[] = ss[1].split(" ");
			for (int i = 0; i < sss.length; i++) {
				if (sss[i] != "") {
					if (!sss[i].equals(Util.me.getAccount())) {
						buddyEntityList.add(new BuddyEntity(sss[i]));
					}
				}
			} 
		}else {
			for (int i = 0; i < ss.length; i++) {
				if (ss[i] != "") {
					if (!ss[i].equals(Util.me.getAccount())) {
						buddyEntityList.add(new BuddyEntity(ss[i]));
					}
				}
			} 
		}
		
		return buddyEntityList;
	}

	public static String getActivityName() {
		return "BuddyActivity";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			isStop = true;
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			logout(Util.me.getAccount());
			Intent intent = new Intent(BuddyActivity.this, McServer.class);
			BuddyActivity.this.stopService(intent);
			finish();
			System.exit(0);
		}
	}

}
