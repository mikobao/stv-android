package com.stv.supervod.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.stv.supervod.service.Connect2RCServer;
import com.stv.supervod.utils.ChangeGestureDetector;
import com.stv.supervod.utils.ErrorCode;
import com.stv.supervod.utils.VideoConstant;

/**
 * @author      Administrator
 * @description 播放控制页面
 * @authority   激活用户
 * @function    用户点击播放、暂停、快进、快退、上一个，下一个、进度条、停止播放进行相应功能操作
 */
public class Play extends BaseActivity{
	private ImageButton playBtn = null;//播放、暂停	
	private ImageButton forwardBtn = null;//快进
	private ImageButton rewindBtn = null;//快退
	private Button stopBtn=null;//停止
	private TextView playtime = null;//已播放时间
	private TextView durationTime = null;//视频时间
	private TextView videoName;
	public SeekBar seekbar = null;//视频进度	
	
	private int currentPosition=0;//当前播放位置
	private int duration;//视频时长
	private int scale=1;//播放倍速
		
	private static final int STATE_PLAY = 1;
	private static final int STATE_PAUSE = 2;
	
	private int flag=1;
	private int i;
	private final int STATE_FINISH = 0;
	private final int STATE_EXCEPTION = 1;
	private boolean isApplyService;
	private boolean isPlaySuccess=false;
	private TextView name = null;
    private Handler handler=new Handler();//处理进度条更新
    private Bundle bundle;
	private GestureDetector gestureDetector;
	private ProgressDialog conDialog;//连接vod服务器对话框

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play); 
//        service=new VideoService(this);
        bundle=getIntent().getExtras();        
        String name=bundle.getString("name");
        conDialog=new ProgressDialog(this);
                  
		gestureDetector=new GestureDetector(new ChangeGestureDetector(this));	//手势识别
        //视频名称
		videoName=(TextView)findViewById(R.id.video_name_view);
		videoName.setText(name);
        //响应返回事件
        Button btn=(Button) this.findViewById(R.id.play_btn_back);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(isPlaySuccess){
					callService();
					Play.this.moveTaskToBack(true);
				}else{
					showErrorInfo();
				}
				
			}			
		});
        //响应播放、暂停事件
        playBtn = (ImageButton)findViewById(R.id.playBtn);       
//		playBtn.setBackgroundResource(R.drawable.pause_selecor);
		playBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (flag) {
				case STATE_PLAY:
					playControl();
					break;

				case STATE_PAUSE:
					pause();
					break;
				}
			}
		});
		//响应停止播放事件
		stopBtn=(Button)findViewById(R.id.play_stop);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stop();
				handler.removeCallbacks(runnable);
				flag = STATE_PLAY;
				playBtn.setBackgroundResource(R.drawable.play_selecor);
			    cancleNotification();
				Play.this.finish();
			}
		});
				
		//响应快退事件
		rewindBtn = (ImageButton)findViewById(R.id.backwordBtn);
		rewindBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:				
					rewind();
					break;
//				case MotionEvent.ACTION_UP:
//					play();
//					break;
				}
				return true;
			}
		});
		//响应快进事件
		forwardBtn = (ImageButton)findViewById(R.id.forwardBtn);
		forwardBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:					
					forward();
					break;
//				case MotionEvent.ACTION_UP:
//					play();
//					break;
				}
				return true;
			}
		});
		//响应拖动滚动条事件
		seekbar = (SeekBar)findViewById(R.id.seekbar);
		seekbar.setMax(100);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {				
				if (fromUser){
					handler.removeCallbacks(runnable);
					seekbar_change(progress);
				}
			}
		});
		
		playtime=(TextView)findViewById(R.id.playtime);
		durationTime=(TextView)findViewById(R.id.duration);
//	 showConInfo(conDialog);//提示登录信息        
		
		init();
		new ApplayServiceTask().execute();
    }
	
     protected void showErrorInfo() {
    	 new AlertDialog.Builder(Play.this)
			.setTitle(R.string.tip)
			.setMessage("很抱歉，出错了！网络连接失败" )
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Play.this.finish();
				}
			})
			.show();
		
	}

	protected void cancleNotification() {
		// TODO Auto-generated method stub
    	 NotificationManager mgr=(NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
    	 mgr.cancel(R.id.app_notification_id);		
	}

	private void play(Bundle bundle) {
		// TODO Auto-generated method stub
        flag = STATE_PAUSE;
 		playBtn.setBackgroundResource(R.drawable.pause_selecor);
 		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.PLAY);
		intent.putExtra("info", bundle);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);		
	}

   
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	private void init(){
		IntentFilter filter = new IntentFilter();	
		filter.addAction(VideoConstant.APPLYSERVICE);	
		filter.addAction(VideoConstant.PLAY_SUCCESS);
		filter.addAction(VideoConstant.PAUSE);
		filter.addAction(VideoConstant.FORWARD);
		filter.addAction(VideoConstant.BACKWARD);
		filter.addAction(VideoConstant.SYSMSG);
		filter.addAction(VideoConstant.NEXT);
		filter.addAction(VideoConstant.ERROR);
		filter.addAction(VideoConstant.QUERY_MSG);
		filter.addAction(VideoConstant.PLAY_FINISHED);
		registerReceiver(videoReceiver, filter);
	}

	private void rewind() {
		handler.removeCallbacks(runnable);
		flag = STATE_PLAY;
		playBtn.setBackgroundResource(R.drawable.play_selecor);
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_REWIND);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);
		
	}

	private void forward() {
		handler.removeCallbacks(runnable);
		flag = STATE_PLAY;		
		playBtn.setBackgroundResource(R.drawable.play_selecor);
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_FORWARD);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);
	}
		

	//播放
	private void playControl() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(runnable);
		flag = STATE_PAUSE;		
		playBtn.setBackgroundResource(R.drawable.pause_selecor);
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_PLAY);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);		
	}
  @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(videoReceiver);
	}

	//暂停
	private void pause() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(runnable);
		flag = STATE_PLAY;
		playBtn.setBackgroundResource(R.drawable.play_selecor);
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_PAUSE);
		intent.setAction(VideoConstant.FILTER_ACTION);
		startService(intent);
//		showInfo("pause");
	}
	/**
	 * 用户拖动进度条
	 */
	private void seekbar_change(int progress){	
		flag = STATE_PAUSE;		
		playBtn.setBackgroundResource(R.drawable.pause_selecor);
//		Toast.makeText(this, "改变进度条"+progress, Toast.LENGTH_LONG).show();
		Intent intent = new Intent();		
		intent.setAction(VideoConstant.FILTER_ACTION);
		intent.putExtra("op", VideoConstant.PROGRESS_CHANGE);
		intent.putExtra("progress", progress);
		startService(intent);
		
	}
	

	//响应返回事件
	private void callService() {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
//		intent.addCategory(Intent.CATEGORY_HOME); 
//		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("op", VideoConstant.BACK);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);
	}
	//申请服务
	private void applyService() {		
		
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_APPLY_SERVICE);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);
		
		
	}

	//停止播放
	private void stop(){
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.putExtra("op", VideoConstant.VIDEO_STOP);
		intent.setAction(VideoConstant.FILTER_ACTION);		                
		startService(intent);
	}
			
	Handler updateProgress=new Handler(){
		@Override
		public void handleMessage(Message msg){ 
			playtime.setText(toTime(msg.arg1));
		}
	};
	/**
	 * 定义videoReceiver,接收VideoService发送的广播
	 */
	protected BroadcastReceiver videoReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("-----------------------------", "-------------------------");
			String action = intent.getAction();
		    if(action.equals(VideoConstant.PLAY_SUCCESS)){
				isPlaySuccess=true;//成功播放
				String info=intent.getExtras().getString("info");								
				currentPosition =  intent.getExtras().getInt("nptBegin");//获得当前播放位置	
                if(intent.getExtras().getString("isplaycontrol")!=null){                  	
                   scale=intent.getExtras().getByte("scale");
                   Toast.makeText(Play.this, scale+"X", 100).show();
				}else{
				   Toast.makeText(Play.this, info, 100).show();
				   duration =intent.getExtras().getInt("nptTotal");	
				   scale=1;
				}
											
				seekbar.setMax(duration);
				durationTime.setText(toTime(duration));//设置播放时长
				seekbar.setProgress(currentPosition);			
				handler.postDelayed(runnable,2000);
				
			}
			else if(action.equals(VideoConstant.ERROR)){
				String info=intent.getExtras().getString("error");
				new AlertDialog.Builder(Play.this)
				.setTitle(R.string.tip)
				.setMessage("很抱歉，出错了！" + info)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Play.this.finish();
//						unregisterReceiver(videoReceiver);
					}
				})
				.show();
				
			}
			else if(action.equals(VideoConstant.APPLYSERVICE)){
//				currentPosition =  intent.getExtras().getInt("nptBegin");//获得当前播放位置
//				playtime.setText(toTime(currentPosition));
//				duration =intent.getExtras().getInt("nptTotal");
				play(bundle);//申请服务成功开始播放视频
//				Toast.makeText(Play.this, currentPosition+","+duration, 1000).show();
//				seekbar.setMax(duration);
//				seekbar.setProgress(currentPosition);//设置进度条
				
			}
			else if(action.equals(VideoConstant.SYSMSG)){
				Toast.makeText(Play.this, "开始播放下一个", 100).show();
				currentPosition=0;
				seekbar.setProgress(currentPosition);
				playtime.setText(toTime(currentPosition));
				flag = STATE_PLAY;
				handler.removeCallbacks(runnable);
				playBtn.setBackgroundResource(R.drawable.play_selecor);
				
			}
			else if(action.equals(VideoConstant.PLAY_FINISHED)){
				Toast.makeText(Play.this, "播放完成", 100).show();
				Play.this.finish();
			}
			else if(action.equals(VideoConstant.NEXT)){
//				Toast.makeText(Play.this, "开始播放", Toast.LENGTH_SHORT).show();
				currentPosition=0;
				duration =intent.getExtras().getInt("TotalTime");
				System.out.println("sssssssssssssssssssssssssssssssssssssssss="+duration);
				seekbar.setMax(duration);
				seekbar.setProgress(currentPosition);
				playtime.setText(toTime(currentPosition));
				durationTime.setText(toTime(duration));
				flag = STATE_PAUSE;				
				playBtn.setBackgroundResource(R.drawable.pause_selecor);
				handler.postDelayed(runnable,1000);
			}
			else if(action.equals(VideoConstant.PAUSE)){
				String info=intent.getExtras().getString("info");
				Toast.makeText(Play.this, info, 100).show();
			}
		}
	};
	/**
	 * 时间格式转换函数
	 * @param time
	 * @return
	 */
	public String toTime(int time) {

//		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	 
	Runnable runnable=new Runnable(){
		
		@Override
			public void run() {
		
			    handler.postDelayed(runnable, 1000);			    
				currentPosition = currentPosition +1*scale ;
		     	playtime.setText(toTime(currentPosition));
 			    seekbar.setProgress(currentPosition);
			    if(currentPosition>=duration||currentPosition<=0){
			    	handler.removeCallbacks(runnable);
			    }			    				
			}
			
		};	

	private class ApplayServiceTask extends AsyncTask<Void, Void, Integer> {  
		
		@Override 
		protected void onPreExecute() {     
			conDialog = new ProgressDialog(Play.this);
			conDialog.setMessage("正在努力为您连接网络...请耐心等待");
			conDialog.show();
		}
		
		/* 执行那些很耗时的后台计算工作。可以调用publishProgress方法来更新实时的任务进度。 */ 
		@SuppressWarnings("unchecked")
		@Override 
		protected Integer doInBackground(Void... voids) { 
			/*下载具体分类列表数据*/
			try {
				while (true) {
					if (Connect2RCServer.getInstance().getConnectionStatus()) {
						applyService();
						break;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				ErrorCode.getErrorInfo(e);
				return STATE_EXCEPTION; 
			}
			
			return STATE_FINISH; 
		} 
		
		@Override 
		protected void onPostExecute(Integer result) {     
			int state = result.intValue(); 
			switch (state) {
			case STATE_FINISH:
				conDialog.cancel();
				break;
			case STATE_EXCEPTION:
				new AlertDialog.Builder(Play.this)
				.setTitle(R.string.tip)
				.setMessage("网络连接失败，请检查网络设置后重新登陆！")
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
			default:
				break;
			}
		}
	}
	
}