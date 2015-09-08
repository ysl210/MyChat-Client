package com.xjtu.mychat.client.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import com.xjtu.mychat.R; 

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileBrowserActivity extends Activity implements
		OnItemClickListener {
	private static final String TAG = "FSExplorer";
	ListView itemlist = null;
	String path = "sdcard/";
	int type; //为1时发图片，0时发文件
	List<Map<String, Object>> list;
	public void sendPathToActivity(String path) {
		Intent intent = new Intent(); 
		intent.putExtra("path", path);
		setResult(RESULT_OK, intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_browser);
		setTitle("文件浏览器");
		itemlist = (ListView) findViewById(R.id.itemlist);
		type = getIntent().getIntExtra("value", -1);
		refreshListItems(path);
	}

	/* 根据path更新路径列表 */
	private void refreshListItems(String path) {
		setTitle("文件浏览器 > " + path);
		list = buildListForSimpleAdapter(path);
		SimpleAdapter notes = new SimpleAdapter(this, list, R.layout.file_row,
				new String[] { "name", "path", "img" }, new int[] { R.id.name,
						R.id.desc, R.id.img });
		itemlist.setAdapter(notes);
		itemlist.setOnItemClickListener(this);
		itemlist.setSelection(0);
	}

	/* 根据路径生成一个包含路径的列表 */
	private List<Map<String, Object>> buildListForSimpleAdapter(String path) {
		File[] files = new File(path).listFiles();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
				files.length);
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("name", "sdcard/");
		root.put("img", R.drawable.file_root);
		root.put("path", "go to root directory");
		list.add(root);
		Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("name", "..");
		pmap.put("img", R.drawable.file_paranet);
		pmap.put("path", "go to paranet Directory");
		list.add(pmap);
		for (File file : files) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (type==0) {
				//只显示图片
				if (file.isDirectory()) {
					map.put("img", R.drawable.directory);
				} else { 
					System.out.println(file.getName());
					String[] s = file.getName().split("[.]");
					System.out.println("长度" + s.length);
					if (s.length < 2) {
						continue;
					} else if (s[1].equals("jpg")||s[1].equals("png")||s[1].equals("gif")) {
						map.put("img", R.drawable.file_doc);
					}else {
						continue;
					}
				}
			}else {
				//发文件 
				if (file.isDirectory()) {
					map.put("img", R.drawable.directory);
				} else {
					map.put("img", R.drawable.file_doc);
				} 
			}
			map.put("name", file.getName());
			map.put("path", file.getAbsolutePath());
			list.add(map);
			
			
		}
		return list;
	}

	/* 跳转到上一层 */
	private void goToParent() {
		File file = new File(path);
		File str_pa = file.getParentFile();
		if (str_pa == null) {
			Toast.makeText(FileBrowserActivity.this, "已经是根目录", Toast.LENGTH_SHORT)
					.show();
			refreshListItems(path);
		} else {
			path = str_pa.getAbsolutePath();
			refreshListItems(path);
		}
	}

	/* 实现OnItemClickListener接口 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Log.i(TAG, "item clicked! [" + position + "]");
		if (position == 0) {
			path = "sdcard/";
			refreshListItems(path);
		} else if (position == 1) {
			goToParent();
		} else {
			path = (String) list.get(position).get("path");
			File file = new File(path);
			if (file.isDirectory())
				refreshListItems(path);
			else {
//				Toast.makeText(FileBrowserActivity.this, path, Toast.LENGTH_SHORT)
//						.show();
//				sendPathToActivity(path);
//				finish();
				System.out.println(file.getAbsolutePath());
				Intent data = new Intent();
				data.putExtra("path", file.getAbsolutePath());
				data.putExtra("filename", file.getName());
				System.out.println("put的filename:"+file.getName());
				
				setResult(RESULT_OK, data);
				finish();
				
				
			}

		}

	}

}
