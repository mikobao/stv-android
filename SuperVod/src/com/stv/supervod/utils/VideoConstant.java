package com.stv.supervod.utils;

public interface VideoConstant {
	public static final String APPLYSERVICE = "com.supervod.applyService";//申请服务
	public static final String PLAY_SUCCESS = "com.supervod.play";
	public static final String FORWARD = "com.supervod.forward";
	public static final String BACKWARD = "com.supervod.backward";
	public static final String PAUSE = "com.supervod.pause";
	public static final String SYSMSG = "com.supervod.sysmsg";
	public static final String ERROR = "com.supervod.error";
	public static final String NEXT = "com.supervod.sysmsg.nextplay";
	public static final String QUERY_MSG = "com.supervod.querymsg";
	public static final String FILTER_ACTION = "com.stv.supervod.Video_Service";
	public static final String PLAY_FINISHED="com.supervod.playfinished";
	
	public static final int VIDEO_PLAY = 11;
	public static final int VIDEO_PAUSE = 12;
	public static final int VIDEO_STOP = 14;
	public static final int PROGRESS_CHANGE = 15;
	public static final int VIDEO_REWIND = 10;
	public static final int VIDEO_FORWARD = 13;
	public static final int VIDEO_APPLY_SERVICE=9;
	public static final int PLAY=20;//判断播放类型：单片、电影电视批量、mtv批量
	public static final int BACK=21;
	
	public static final int SUCCESS_PLAY = 80;  //点播成功：VOD播放状态
	public static final int SUCCESS_PAUSE = 81; //点播成功：vod暂停状态
	public static final int FAIL_NOT_EXIST_RESOURCE = 82;   //申请服务失败：资源不存在
	public static final int FAIL_NOT_EXIST_USER = 83;   //申请服务失败：无此用户
	public static final int FAIL_SYSTEM_ERROR = 84;//申请服务失败：VOD系统错误
	public static final int FAIL_RESOURCE_NOT_ENOUGH = 85;//申请服务失败：vod资源不足
	public static final int FAIL_CA_SWITHCHFREP = 86; //申请服务失败：CA系统切换频点失败
	public static final int FAIL_MULTI_ACTIVE_USER=87;//申请服务失败：多个活跃用户
	public static final int FAIL_SYSTEM_MAINTAINING=88;//申请服务失败，系统维护中
	public static final int FAIL_PASSWORD_ERROR=89;//点播失败：密码错误
	public static final int SUCCESS_FINISHED_PLAY=90;//点播节目已播放完成
	public static final int FAIL_SESSION_NOT_EXIST=91;//会话已经不存在
	public static final int FAIL_ILLEGAL_DATA = 92;//非法数据，解析数据有问题时返回
	public static final int SUCCESS_ALL_FINISHED_PLAY=93;//全部播放完成
	public static final int FAIL_LOGIN_OTHER=94;//用户在另一处登陆
	public static final int FAIL_TIMEOUT=95;//操作超时
	public static final int FAIL_MONEY_ENOUGH=96;//余额不足
	public static final int FAIL_NOT_ORDER=97;//没有订购该业务
	
	public static final byte APPLY_SERVICE=0;//申请服务
	public static final byte MTV_BATCH_PLAY=1;//mtv批量点播
	public static final byte VIDEO_BATCH_PLAY=2;//视频批量点播、
	public static final byte SINGLE_PLAY=3;//单片点播
	public static final byte PLAY_CONTROL=4;//播放控制
	public static final byte HEART_BEAT=5;//心跳
	public static final byte STOP_SERVICE=6;//停止服务
	public static final byte SYS_MSG=7;//系统消息
	public static final byte QUERY_STATUS=8;//查询状态
	public static final byte NETWORK_CHECK=9;//网络监测
	

}
