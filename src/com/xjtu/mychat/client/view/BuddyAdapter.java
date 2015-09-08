package com.xjtu.mychat.client.view;
import java.util.List;
import com.xjtu.mychat.R;
 

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BuddyAdapter extends BaseAdapter{
	//private Context context;
	private List<BuddyEntity> list;
	LayoutInflater inflater;
	
	public BuddyAdapter(Context context,List<BuddyEntity> list){
		//this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup root) {
		convertView = inflater.inflate(R.layout.buddy_listview_item, null);
		
		TextView nick=(TextView) convertView.findViewById(R.id.tv_nick);
		ImageView imageView = (ImageView)convertView.findViewById(R.id.iv_avatar);
		
		imageView.setImageResource(ChatActivity.avatar[2]);
		BuddyEntity be=list.get(position);
		nick.setText(new String(be.getAccount()));

		return convertView;
	}
	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
}
