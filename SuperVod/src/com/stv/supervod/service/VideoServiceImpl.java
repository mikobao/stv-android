package com.stv.supervod.service;

import android.os.Bundle;
import android.os.Handler;

import com.stv.supervod.exception.ErrorCodeException;
import com.stv.supervod.service.HttpDownloadImpl.TypeEnum;
import com.stv.supervod.utils.VodModel;

/**
 * 
 * Copyright (c) 永新视博 All Rights Reserved.
 * @author Administrator
 * create 2012-3-1 下午2:50:50
 *
 */
public class VideoServiceImpl {
	
	private Bundle bundle;
	private  TCPClient client;
	private Handler handler=new Handler();

	private String offeringId;//视频id
	private boolean status=false;//播放状态		
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public void setClient(TCPClient client) {
		this.client = client;
	}

	public void setOfferingId(String offeringId) {
		this.offeringId = offeringId;
	}


	private static final VideoServiceImpl videoService=new VideoServiceImpl();
	
	public static VideoServiceImpl getInstance(){
		return videoService;
	}
	private VideoServiceImpl(){
		
	}
	
	
	
	
	public boolean getPlayStatus(String offeringId){
		if(this.offeringId==offeringId&&status==true){
			return true;
		}
			return false;
	}
	
	/**
	 * 申请服务 验证是否合法用户能否进行点播操作
	 * @param username
	 * @param password
	 * @param pwtype
	 * @throws ErrorCodeException
	 */
	public void appService(String username,String password,String pwtype) throws ErrorCodeException{
		VodModel model=new VodModel();
		model.setSmartCardId(username);
		model.setPassword(password);
		model.setIsPassword(Byte.parseByte(pwtype));
		client.applyServcie(model);
	}
	
	/**
	 * 播放控制 ：快进、快退、播放、暂停
	 * @param operation
	 * @throws ErrorCodeException
	 */
	public void PlayControl(byte operation,int startTime) throws ErrorCodeException{
		VodModel model=new VodModel();
		model.setOperation(operation);		
		model.setNpt_begin((short)startTime);		
		client.playControl(model);
	}
	/**
	 * 退出播放状态
	 * @throws ErrorCodeException
	 */
	public void stopPlay() throws ErrorCodeException{
		client.Quit();
	}
	/**
	 * 发送心跳
	 * @throws ErrorCodeException
	 */
	public void sendHeardBeat() throws ErrorCodeException{
		client.Heartbeat();
	}
	
	/**
	 * Description: 通知RCserver更新我的频道数据
	 * @Version1.0 2012-2-28 上午11:38:50 zhaojunfeng created
	 * @param type ： TypeEnum中的MTV or MOVIE
	 * @param channelId ： 频道ID
	 * @return : 返回批量点播的结果的键值对
	 * @throws ErrorCodeException
	 */
	public void myvodChannelUpdate( String type, String channelId ) throws ErrorCodeException	{
		VodModel model=new VodModel();
		model.setChannelId(new Integer(channelId).byteValue());
		model.setAction((byte)1);
		if(TypeEnum.KTV.toString()==type){
			client.mtvBatchPlay(model);
		}else{
			client.videoBatchPlay(model);
		}		
	}
	
	/**
	 * Description: 向RCServer查询节目播放状态
	 * @Version1.0 2012-2-28 下午1:43:55 zhaojunfeng created
	 * @return
	 * @throws ErrorCodeException
	 */
	public void myvodState() throws ErrorCodeException {
		VodModel model=new VodModel();
		client.queryStatus(model);		
	}
	
	/**
	 * Description: myvod的MTV批量点播
	 * @Version1.0 2012-2-28 下午1:54:39 zhaojunfeng created
	 * @param playMode
	 * @param playCount
	 * @param channelId
	 * @param offingId
	 * @return
	 * @throws ErrorCodeException
	 */
	public void myvodMtvPlay( byte playMode, int playCount, 
			String channelId, String offeringId) throws ErrorCodeException {
		VodModel model=new VodModel();
		model.setAction((byte)0);//播放
		model.setChannelId(new Integer(channelId).byteValue());
		model.setOfferingId(offeringId);
		model.setPlayMode(playMode);
		model.setPlayCount(playCount);
		client.mtvBatchPlay(model);

	}
	
	/**
	 * Description: myvod的视频批量点播
	 * @Version1.0 2012-2-28 下午1:55:08 zhaojunfeng created
	 * @param channelId
	 * @param offingId
	 * @param isBookmark
	 * @return
	 * @throws ErrorCodeException
	 */
	public void myvodMoviePlay(String channelId, String offeringId, byte isBookmark)
			throws ErrorCodeException{
		VodModel model=new VodModel();
		model.setAction((byte)0);//播放
		model.setChannelId(new Integer(channelId).byteValue());
		model.setOfferingId(offeringId);
		model.setIsBookmark(isBookmark);
		client.videoBatchPlay(model);
	}
	
	/**
	 * Description: 普通视频点播，需要启动播放前界面
	 * @Version1.0 2012-2-28 下午1:55:18 zhaojunfeng created
	 * @param offingId
	 * @param isBookmark
	 * @throws ErrorCodeException
	 */
	public void commPlay(String offeringId, byte isBookmark) throws ErrorCodeException{
		VodModel model=new VodModel();
		model.setAction((byte)0);//播放		
		model.setOfferingId(offeringId);
		model.setIsBookmark(isBookmark);
		client.play(model);
	}
    //移除心跳
	public void removeHeardBeat(){
		handler.removeCallbacks(sendh);
	}
	public void startSendHeardBeat(){
//		new Thread(new ServiceWorker()).start();
		
		handler.postDelayed(sendh, 1000*60);
		
	}
	
	 //发送心跳
	private Runnable sendh = new  Runnable(){
		
		public void run(){
			//do something
			try {													
				sendHeardBeat();
				handler.postDelayed(sendh, 1000*60);
				
			} catch (ErrorCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	};
		
}