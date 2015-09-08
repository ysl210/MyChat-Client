package com.xjtu.mychat.client.view;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.xjtu.mc.common.MCMessage;
import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mc.common.User;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.R;
import com.xjtu.mychat.client.model.ManageClientConServer;
import com.xjtu.mychat.client.model.MCClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static String userInfo;
	EditText accountEt, passwordEt;
	boolean isSave = false;// 保存账号密码
	Dialog dialog;
	SharedPreferences sharedPreferences;
	CheckBox remeberBox;
	public static boolean isConnError = true;
	public static LoginActivity instance = null;

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		instance = this;
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		remeberBox = (CheckBox) findViewById(R.id.btn_remeber);
		accountEt = (EditText) findViewById(R.id.et_account);
		passwordEt = (EditText) findViewById(R.id.et_password);
		Button btnLogin = (Button) findViewById(R.id.btn_login);

		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		String account = sharedPreferences.getString("account", "");
		String pwd = sharedPreferences.getString("pwd", "");
		NoticeUtil.NoticeType = sharedPreferences.getString("noticetype",
				NoticeUtil.DEFAULT_VIBRATE);

		if (sharedPreferences.getInt("save", -1) == 1) {
			remeberBox.setChecked(true);
			accountEt.setText(account);
			passwordEt.setText(pwd);
			isSave = true;
		} else {
			remeberBox.setChecked(false);
			accountEt.setText("");
			passwordEt.setText("");
			isSave = false;
		}

		remeberBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = sharedPreferences.edit();// 获取编辑器
				if (isChecked) {
					isSave = true;
					editor.putInt("save", 1);
				} else {
					isSave = false;
					editor.putInt("save", 0);
				}
				editor.commit();
			}
		});
		btnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				System.out.println("消息提醒类型：" + NoticeUtil.NoticeType);
				if ("".equals(accountEt.getText().toString().trim())
						|| "".equals(passwordEt.getText().toString().trim())) {
					Toast.makeText(LoginActivity.this, "账号或密码不能为空！",
							Toast.LENGTH_SHORT).show();
				} else if (accountEt.getText().toString().trim().length() < 4
						|| passwordEt.getText().toString().trim().length() < 4) {
					Toast.makeText(getApplicationContext(), "账号和密码长度不能小于4位",
							Toast.LENGTH_LONG).show();
				} else {
					// 保存账号密码
					if (isSave) {
						String account = accountEt.getText().toString().trim();
						String pwd = passwordEt.getText().toString().trim();
						Editor editor = sharedPreferences.edit();// 获取编辑器
						editor.putString("account", account);
						editor.putString("pwd", pwd);
						editor.commit();// 提交修改
					}
					User user = new User();
					user.setAccount(accountEt.getText().toString().trim());
					Info.me = user;
					dialog = ProgressDialog.show(LoginActivity.this, null,
							"正在登陆中 …", true, true);
					handler.post(new Runnable() {
						public void run() {
							boolean b = login((accountEt.getText().toString()),
									passwordEt.getText().toString());

							if (b) {
								Message m = new Message();
								m.what = 1;
								TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
								Util.deviceId = tm.getDeviceId().substring(
										tm.getDeviceId().length() - 2,
										tm.getDeviceId().length());
								System.out.println("设备ID最后两位：" + Util.deviceId);
								System.out.println("服务器返回的随机码：" + Util.code);
								Intent intent = new Intent(LoginActivity.this,
										BuddyActivity.class);
								startActivity(intent);
								dialog.dismiss();
								LoginActivity.this.finish();
							} else {
								// Toast.makeText(LoginActivity.this,
								// "登陆失败！", Toast.LENGTH_SHORT)
								// .show();
								dialog.dismiss();
								System.out.println("登录状态：" + b);
							}
						}
					});
				}
			}
		});
		// 配置ip地址
		findViewById(R.id.btn_config).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startActivity(new Intent(LoginActivity.this,
						ConfigActivity.class));
			}
		});
		ManageActivity.addActiviy("LoginActivity", this);
	}

	boolean login(String a, String p) {
		User user = new User();
		user.setAccount(a);
		user.setPwd(p);
		user.setOperation("login");
		boolean b = new MCClient(this).sendLoginInfo(user);
		// 登陆成功
		if (b) {
			try {
				// 发送一个要求返回在线好友的请求的Message
				ObjectOutputStream oos = new ObjectOutputStream(
						ManageClientConServer
								.getClientConServerThread(user.getAccount())
								.getS().getOutputStream());
				MCMessage m = new MCMessage();
				m.setType(MCMessageType.GET_ONLINE_FRIENDS);
				m.setSender(user.getAccount());
				oos.writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// this.finish();
			isConnError = true;
			return true;
		} else {
			if (isConnError) {
				Toast.makeText(getApplicationContext(), "连接超时,服务器已关闭,或者网络故障！",
						5000).show();
			}
			isConnError = true;
			return false;
		}
		
	}

	private Handler handler = new Handler();

}
