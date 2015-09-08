package com.xjtu.mychat.client.view;

public class ChatEntity {
	// ------消息的状态-----
	public static String CHAT_TEXT = "1";
	public static String CHAT_VOICE = "2";
	public static String CHAT_FILE = "3";
	public static String CHAT_PIC = "4";
	// -----------------
	private int avatar;
	private String content;
	private String account;
	private String time;
	private String mes_type;// 消息类型 文本/文件/语音/图片
	private String ext_name;

	private boolean isLeft;// 是否为收到的消息，在左边

	public ChatEntity(String account, String content, String time,
			String mes_type, boolean isLeft) {
		this.account = account;
		this.content = content;
		this.time = time;
		this.mes_type = mes_type;
		this.isLeft = isLeft;
	}
	public ChatEntity(String account, String content, String time,
			String mes_type, boolean isLeft,String ext_name) {
		this.account = account;
		this.content = content;
		this.time = time;
		this.mes_type = mes_type;
		this.isLeft = isLeft;
		this.ext_name = ext_name;
	}
	// public ChatEntity( String account,String ext,String time,String
	// mes_type,boolean isLeft){
	// this.account=account;
	// this.content = content;
	// this.time = time;
	// this.mes_type = mes_type;
	// this.isLeft = isLeft;
	// }

	public String getMes_type() {
		return mes_type;
	}

	public void setMes_type(String mes_type) {
		this.mes_type = mes_type;
	}

	public String getExt_name() {
		return ext_name;
	}

	public void setExt_name(String ext_name) {
		this.ext_name = ext_name;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}
}
