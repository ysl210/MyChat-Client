package com.xjtu.mc.common;

public class MCMessageType {

	public static final String SUCCESS="1";//表明是否成功
	public static final String FAIL="2";//表明失败
	public static final String COM_MES="3";//普通信息包
	public static final String SEND_FILE="4";//发送文件包
	public static final String SEND_PIC="5";//发送图片
	public static final String SEND_REC="6";//发送语音包
	public static final String SEND_VIDEO="7";//发送视频流
	public static final String GET_ONLINE_FRIENDS="8";//要求在线好友的包
	public static final String RET_ONLINE_FRIENDS="9";//返回在线好友的包
	public static final String HELLO="10";//心跳消息
	public static final String HELLO_ONLINE_FRIENDS="11";//心跳返回消息，返回在线好友的包
	public static final String REQ_CONNECT="12";//请求对端连接
	public static final String REQ_DISCONNECT="13";//请求断开对端连接
	public static final String CONNECT_SUCCESS="14";//连接成功
	public static final String ALLOW_CONNECT="15";//允许对端连接
	public static final String REFUSE_CONNECT="16";//拒绝对端连接
	public static final String CONFIRM_CONNECT="17";//对端连接就绪
	public static final String LOGIN="18";//请求验证登陆
	public static final String ALREADY_LOGIN="19";//已经登录
	public static final String LOGOUT="20";//请求登出
	public static final String LOGIN_FAIL="21";//请求登出

}
