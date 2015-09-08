package com.xjtu.mychat.client.view;

import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mc.common.User;
import com.xjtu.mc.common.Util;
import com.xjtu.mychat.client.model.ManageClientConServer;
import com.xjtu.mychat.client.model.MCClient;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

public class McServer extends Service {

	private int count;
	private int num = 0;
	private boolean isClosed = false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println("oncreate");
		count = 60;// 130秒判断是否掉线
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				while (true) {
					System.out.println("num:" + num);
					if (isClosed) {
						System.out.println("关闭线程");
						isClosed = false;
						return;
					}
					if (num > 3) {
						System.out.println("退出");
						// Toast.makeText(context, "",
						// Toast.LENGTH_LONG).show();
						McServer.this.onDestroy();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);

						break;

					}
					if (count < 0) {
						// reconnect
						System.out.println("关闭线程");
						ManageClientConServer.getClientConServerThread(Util.me
								.getAccount()).isClosed = true;
						ManageClientConServer.remove(Util.me.getAccount());
						//
						User user = new User();
						SharedPreferences spf = getSharedPreferences("config",
								Context.MODE_PRIVATE);
						user.setAccount(spf.getString("account", ""));
						user.setPwd(spf.getString("pwd", ""));
						user.setOperation("login");
						boolean b = false;
						b = new MCClient(McServer.this).sendLoginInfo(user);
						if (b) {
							count = 60;
							System.out.println("登录成功");
							//
							if (NoticeUtil.isTopActivity(
									ChatActivity.getActivityName(),
									ChatActivity.instance)
											|| NoticeUtil.isTopActivity(
													BuddyActivity
															.getActivityName(),
													BuddyActivity.instance)){
								Intent intent = new Intent(McServer.this,
										BuddyActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}else {
								ChatActivity.instance.finish();
							}
						} else {
							num++;
							System.out.println("num++:" + num);
						}
						// return;
					} else {
						count--;
						System.out.println(count);
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		isClosed = true;
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("start");
		count = 60;
		num = 0;
		return 0;

	}

}
