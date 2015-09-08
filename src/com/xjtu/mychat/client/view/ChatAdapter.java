package com.xjtu.mychat.client.view;

import java.io.IOException;
import java.util.List;

import com.xjtu.mc.common.MCMessageType;
import com.xjtu.mc.common.MyTime;
import com.xjtu.mychat.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	private Context context;
	private List<ChatEntity> list;
	LayoutInflater inflater;
	private boolean isPlay = false;
	private MediaPlayer mp = null;
	// private int[] avatar = new int[] { 0, R.drawable.h001, R.drawable.h002,
	// R.drawable.h003, R.drawable.h004, R.drawable.h005, R.drawable.h006 };

	public ChatAdapter(Context context, List<ChatEntity> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup root) {
		ImageView avatar, sendImage;
		final TextView content;
		TextView time;
		RelativeLayout relativeLayout;
		final ChatEntity ce = list.get(position);
		if (ce.isLeft()) {
			convertView = inflater.inflate(R.layout.chat_listview_item_left,
					null);
			relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_chat_left);
			avatar = (ImageView) convertView
					.findViewById(R.id.avatar_chat_left);
			content = (TextView) convertView
					.findViewById(R.id.message_chat_left);
			time = (TextView) convertView.findViewById(R.id.sendtime_chat_left);
			sendImage = (ImageView) convertView
					.findViewById(R.id.image_chat_left);
			avatar.setImageResource(ChatActivity.avatar[2]);
			content.setText(new String(ce.getContent()));
			time.setText(ce.getTime());
			if (ce.getMes_type().equals(MCMessageType.SEND_FILE)
					|| ce.getMes_type().equals(MCMessageType.COM_MES)) {
				sendImage.setVisibility(ImageView.INVISIBLE);
			}
			if (ce.getMes_type().equals(MCMessageType.SEND_PIC)) {
				content.setVisibility(TextView.INVISIBLE);
				Bitmap bitmap = BitmapFactory.decodeFile(ce.getExt_name());
				System.out.println("加载文件时----------" + ce.getExt_name());
				relativeLayout.setVisibility(RelativeLayout.INVISIBLE);
				sendImage.setImageBitmap(bitmap); // 设置Bitmap
			} else if (ce.getMes_type().equals(MCMessageType.SEND_REC)) {
				sendImage.setVisibility(ImageView.INVISIBLE);

				content.setText("语音消息"); 
				
				relativeLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// -----------播放声音--------------------------
						content.setTextColor(Color.BLACK);  
						if (isPlay) {
							System.out.println("停止---------");
//							content.setText(mp.getDuration()+"  语音消息");
							content.setText("语音消息");
							mp.pause();
							mp.release(); 
//							mp = null;
							isPlay = false;
						} else {
							mp = new MediaPlayer();
							try {
								mp.setDataSource(Environment
										.getExternalStorageDirectory()
										.getCanonicalFile()
										+ "/mychat/" + ce.getExt_name()); 
								mp.prepare();
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SecurityException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalStateException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
							content.setText("正在播放......");
							System.out.println("播放----------");
							isPlay = true; 
							try { 
								mp.start();
								mp.setOnCompletionListener(new OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										// TODO Auto-generated method stub
										content.setText("语音消息");
										mp.release();
										//mp = null;
										isPlay = false;
									}
								});
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}

		} else {

			convertView = inflater.inflate(R.layout.chat_listview_item_right,
					null);
			relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_chat_right);
			sendImage = (ImageView) convertView
					.findViewById(R.id.image_chat_right);
			avatar = (ImageView) convertView
					.findViewById(R.id.avatar_chat_right);
			content = (TextView) convertView
					.findViewById(R.id.message_chat_right);
			time = (TextView) convertView
					.findViewById(R.id.sendtime_chat_right);
			avatar.setImageResource(ChatActivity.avatar[1]);
			content.setText(new String(ce.getContent()));
			time.setText(ce.getTime());
			if (ce.getMes_type().equals(MCMessageType.SEND_FILE)
					|| ce.getMes_type().equals(MCMessageType.COM_MES)) {
				sendImage.setVisibility(ImageView.INVISIBLE);
			}
			if (ce.getMes_type().equals(MCMessageType.SEND_PIC)) {
				content.setVisibility(TextView.INVISIBLE);
				Bitmap bitmap = BitmapFactory.decodeFile(ce.getExt_name());
				sendImage.setImageBitmap(bitmap); // 设置Bitmap
				relativeLayout.setVisibility(RelativeLayout.INVISIBLE);
			} else if (ce.getMes_type().equals(MCMessageType.SEND_REC)) {
				sendImage.setVisibility(ImageView.INVISIBLE);
				
				content.setText("语音消息"); 
				
				relativeLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// -----------播放声音--------------------------
						content.setTextColor(Color.BLACK);  
						if (isPlay) {
							System.out.println("停止---------");
//							content.setText(mp.getDuration()+"  语音消息");
							content.setText("语音消息");
							mp.pause(); 
							mp.release();
//							mp = null;
							isPlay = false;
							
						} else {
							mp = new MediaPlayer();
							try {
								mp.setDataSource(Environment
										.getExternalStorageDirectory()
										.getCanonicalFile()
										+ "/mychat/" + ce.getExt_name()); 
								mp.prepare();
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SecurityException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalStateException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
							System.out.println("播放----------");
							isPlay = true; 
							try { 
								mp.start();
								content.setText("正在播放......");
								mp.setOnCompletionListener(new OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										// TODO Auto-generated method stub
										content.setText("语音消息");
										mp.release();
										//mp = null;
										isPlay = false;
									}
								});
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				});
			}
		}

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
