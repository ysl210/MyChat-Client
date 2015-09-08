package com.xjtu.mychat.client.view;
 
import com.xjtu.mc.common.NoticeUtil;
import com.xjtu.mychat.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	Button yes , cancel;
	EditText ip_add;
	SharedPreferences sharedPreferences ;
	RadioGroup radioGroup;
	RadioButton all,vibrate,sound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		
		init();
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) { 
				if (checkedId == R.id.vibrate){
					NoticeUtil.NoticeType = NoticeUtil.DEFAULT_VIBRATE; 
				}else if (checkedId == R.id.sound) {
					NoticeUtil.NoticeType = NoticeUtil.DEFAULT_SOUND;
				}else if (checkedId == R.id.all) {
					NoticeUtil.NoticeType = NoticeUtil.DEFAULT_ALL;
				}
				Editor editor = sharedPreferences.edit();// 获取编辑器
				editor.putString("noticetype", NoticeUtil.NoticeType);
				editor.commit();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( "".equals(ip_add.getText().toString().trim())){
					Toast.makeText(getApplicationContext(), "请输入ip地址", Toast.LENGTH_LONG).show();
				}else {
					int myport = 5469;
					String myip = ip_add.getText().toString().trim();
					Editor editor = sharedPreferences.edit();// 获取编辑器
					editor.putInt("port", myport);
					editor.putString("ip", myip);
					editor.commit();// 提交修改
					finish();
				}
			}
		});
	}

	private void init() {
		yes = (Button)findViewById(R.id.config_btn_sure);
		cancel = (Button)findViewById(R.id.config_btn_cancel);
		ip_add = (EditText)findViewById(R.id.config_ip); 
		radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
		vibrate = (RadioButton)findViewById(R.id.vibrate);
		all = (RadioButton)findViewById(R.id.all);
		sound = (RadioButton)findViewById(R.id.sound);
		
		sharedPreferences = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String ip = sharedPreferences.getString("ip", "");
		int port = sharedPreferences.getInt("port", 5469);
		NoticeUtil.NoticeType = sharedPreferences.getString("noticetype", NoticeUtil.DEFAULT_VIBRATE);
		if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_VIBRATE)) {
			vibrate.setChecked(true);
		}else if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_SOUND)) {
			sound.setChecked(true);
		}else if (NoticeUtil.NoticeType.equals(NoticeUtil.DEFAULT_ALL)) {
			all.setChecked(true);
		} 
		ip_add.setText(ip);
	}
 

}
