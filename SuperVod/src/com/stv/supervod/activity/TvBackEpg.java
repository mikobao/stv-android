package com.stv.supervod.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stv.supervod.adapter.WeekListItemAdapter;
import com.stv.supervod.service.HttpDownloadImpl;
import com.stv.supervod.service.HttpDownloadImpl.KeyEnum;
import com.stv.supervod.service.HttpDownloadImpl.TypeEnum;
import com.stv.supervod.utils.AlertUtils;
import com.stv.supervod.utils.Constant.PlayerType;
import com.stv.supervod.utils.Constant.ServiceTypeEnum;
import com.stv.supervod.utils.DpPxUtil;
import com.stv.supervod.utils.ErrorCode;
import com.stv.supervod.widget_extended.MarqueeTextView;

public class TvBackEpg extends BaseActivity {
	private final String TAG = "TvBackEpg";
	
	private final String WEEKLIST_KEY_DATE = "DateInDate";
	private final String WEEKLIST_KEY_WEEK = "DateInWeek";
	private final String WEEKLIST_KEY_NET = "DateInNet";
	
	private final long MillisOneDay = 1000 * 60 * 60 * 24;
		
	private ListView mListView;
	private ArrayList<HashMap<String, Object>> mListItem = new ArrayList<HashMap<String, Object>>();
	private Map<String, Object> mServiceInfo = null;
	private Button mBtnBack;
	private MarqueeTextView mTVChannelName;
	private ArrayList<HashMap<String, Object>> mListWeek = new ArrayList<HashMap<String, Object>>();
	private GridView mGvWeekList;
	private WeekListItemAdapter   mWeekListAdapter = null;
	private ProgramListAdapter mPrglistAdapter = null;
	
	private ProgressDialog mProgressDlg;
	private Toast mToast;
	
	private int mListWeekSelect = 6;
	private int mProgramSelect = 0;
	private String mAssetId;
	// 由于回看节目EPG不太多(最多50)，并且没有图片，所以一次就全部获取完毕
	private final int mNumPerPage  =  10; // 500
	private final int mPageIndex  =  1;
	
	private processAsyncTask mProcessTask = null;
	
	private enum TstvListOp {
		error, get_list, get_info, play, none, quit
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tvbackepg);
		
		// 初始化
        mListView = (ListView) findViewById(R.id.tvbackepg_lv_epglist);
        mTVChannelName = (MarqueeTextView) findViewById(R.id.tvbackepg_channel_name);
        mBtnBack = (Button) findViewById(R.id.tvbackepg_btn_back);
        mGvWeekList = (GridView) findViewById(R.id.tvbackepg_gv_week);
        
		mProgressDlg = new ProgressDialog(this);
		mProgressDlg.setMessage("数据加载中...");
		mProgressDlg.setIndeterminate(true);
		mProgressDlg.setCancelable(true);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, DpPxUtil.dp2px(this, 60));
        
        initWeekListGui();      

        // 绑定数据
        mListView.setOnItemClickListener(mItemListListener);
        mGvWeekList.setOnItemClickListener(mListWeekListener);
        mBtnBack.setOnClickListener(mBtnBackListener);

        // 获取数据进行显示
        mAssetId = getIntent().getExtras().getString(KeyEnum.channelId.toString());
        mTVChannelName.setText(getIntent().getExtras().getString(KeyEnum.name.toString()));  
        
        mProcessTask = new processAsyncTask(this);
        mProcessTask.execute(TstvListOp.get_list);	      
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mToast.cancel();
		super.onDestroy();
	}
	
	private void initWeekListGui(){
        // init weeklist
        String[] WeekDays = getResources().getStringArray(R.array.tvback_text_week);
        Time currTime = new Time();
        long lCurrTime = System.currentTimeMillis();
        for (int i = 6; i >= 0; i--) {
            HashMap<String, Object> map = new HashMap<String, Object>(); 
            currTime.set(lCurrTime - i * MillisOneDay);            
            map.put(WEEKLIST_KEY_DATE, currTime.format("%Y/%m/%d"));  
            map.put(WEEKLIST_KEY_NET, currTime.format("%Y-%m-%d"));  
            
            if( i != 0 ){
            	map.put(WEEKLIST_KEY_WEEK, WeekDays[currTime.weekDay]); // 星期
            } else {
            	map.put(WEEKLIST_KEY_WEEK, WeekDays[WeekDays.length-1]); // 今天
            }
            mListWeek.add(map);              
		}
        
        mGvWeekList.setHorizontalSpacing(0); 
        mGvWeekList.setVerticalSpacing(0); 
        mGvWeekList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mWeekListAdapter = new WeekListItemAdapter(this, mListWeek, mListWeekSelect);
        mGvWeekList.setAdapter(mWeekListAdapter); 		
	}
	
	//[start] 后台操作线程
	private class processAsyncTask extends AsyncTask<TstvListOp, String, TstvListOp>{
		private Context mCon;
		private String mLastError = "";
		
		public processAsyncTask( Context con ){
			mCon = con;
			mProgressDlg.show();
		}
		
		@Override
		protected void onPostExecute(TstvListOp result) {
			// TODO Auto-generated method stub
			switch (result) {
			case get_list:
				showList();
				if( mListItem.size() == 0 ){
					showMessage( "该天没有节目列表" );
				}
				break;
			case play:
				String offingId = mServiceInfo.get(KeyEnum.offeringId.toString()).toString();
				if( ServiceTypeEnum.MOD.toString().equalsIgnoreCase(
						mServiceInfo.get(KeyEnum.serviceType.toString()).toString()) ){
					// 显示收费提示框
					float price = Integer.parseInt(mServiceInfo.get(KeyEnum.price.toString()).toString()) / 100;
					showServiceDialog( Float.toString(price) );
				} else { // 免费节目，直接播放
					TvBackEpg.this.showPlayer( 
							offingId, 
							mListItem.get(mProgramSelect).get(KeyEnum.title.toString()).toString(),
							false,PlayerType.common);
				}					
				break;
			case error:
			default:
				showMessage(mLastError);
				break;
			}
			
			if( mProgressDlg.isShowing() ){
				mProgressDlg.dismiss();
			}
		}
		
		@Override
		protected TstvListOp doInBackground(TstvListOp... params) {
			// TODO Auto-generated method stub
			TstvListOp ret = params[0];
			try {
				if( params[0] == TstvListOp.get_list ){
					//[start] 获取节目列表
					String date = mListWeek.get(mListWeekSelect).get(WEEKLIST_KEY_NET).toString();
					HttpDownloadImpl httpDownload = new HttpDownloadImpl();
					
					// 清空当前显示，否则新的显示位置是上次的位置
					mListItem.clear();
					mListItem = ((ArrayList<HashMap<String, Object>>) httpDownload.
							downloadProgramList( date, mAssetId ).get(KeyEnum.list.toString()));
					//[end]
				} else if( params[0] == TstvListOp.play ){
					//[start] 加入我的频道
					HttpDownloadImpl httpDownload = new HttpDownloadImpl();
					Map<String, Object> it = mListItem.get(mProgramSelect);
					mServiceInfo = httpDownload.downloadOffingId(
							it.get(KeyEnum.assetId.toString()).toString(),
							TypeEnum.TSTV.toString());
					if( !(mServiceInfo != null && !mServiceInfo.isEmpty())  ){
						mLastError = "无效节目，播放失败!";
						ret = TstvListOp.error;
					}
					//[end]
				} 
			} catch (Exception e) {
				e.printStackTrace();
				mLastError = ErrorCode.getErrorInfo(e);
				ret = TstvListOp.error;
			}	
			return ret;
		}
	
	}
	//[end]
	
	//[start] Listerner
    private OnItemClickListener mItemListListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			// TODO Auto-generated method stub
			mProgramSelect = position;
			makeOptionDialog();
		}
	}; 
	
	private OnItemClickListener mListWeekListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if( position != mListWeekSelect ){
				mWeekListAdapter.updateFocus(position);
				mListWeekSelect = position;
		        mProcessTask = new processAsyncTask(TvBackEpg.this);
		        mProcessTask.execute(TstvListOp.get_list);	 
			}
		}
	};
	
	private View.OnClickListener mBtnBackListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Framework.switchActivityBack();
		}
	}; 
	//[end]
	
	//[start] 对话框
	private void makeOptionDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.dlg_title_play_option));
		String[] options = getResources().getStringArray(R.array.dlg_tvback_dlg_option);
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if( which == 0 ){
			        mProcessTask = new processAsyncTask(TvBackEpg.this);
			        mProcessTask.execute(TstvListOp.play);	 
				} else if(which == 1){
					addMyFav();
				} else if(which == 2){
					//addMyVod();
				}
			}
		});
		AlertDialog dlg = builder.create();
		dlg.setCanceledOnTouchOutside(true);
		dlg.show();
	}
	
	/**
	 * Description: 显示收费对话框 
	 * @Version1.0 2012-3-8 上午10:36:51 zhaojunfeng created
	 */
	private void showServiceDialog( String price ){
		AlertDialog.Builder bd = new AlertDialog.Builder(TvBackEpg.this);
		bd.setTitle(R.string.further_dlg_title);
		
		String msg = String.format(getString(R.string.detail_txt_price), price);
		
		bd.setMessage(msg);
		bd.setPositiveButton( getString(R.string.framework_dlg_btn_ok)
				, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				TvBackEpg.this.showPlayer( 
						mServiceInfo.get(KeyEnum.offeringId.toString()).toString(), 
						mListItem.get(mProgramSelect).get(KeyEnum.title.toString()).toString(),
						false, PlayerType.common);
			}
		});
		
		bd.setNegativeButton(getString(R.string.framework_dlg_btn_cancel)
				, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		bd.setCancelable(false);
		bd.show();
	}

	//[end]
	
	//[start] 内部函数
	private void addMyFav()
	{
		Map<String, Object> program = mListItem.get(mProgramSelect);

		boolean ret = addMyfav(program.get(KeyEnum.title.toString()).toString(), 
				TypeEnum.TSTV.toString().toString(), 
				program.get(KeyEnum.categories.toString()).toString(), 
				"xxx", program.get(KeyEnum.assetId.toString()).toString());
	
		if( ret ){
			AlertUtils.displayToast(TvBackEpg.this, getString(R.string.detail_txt_addfav_ok) );				
		} else {
			AlertUtils.displayToast(TvBackEpg.this, getString(R.string.detail_txt_addfav_error) );				
		}
	}
	//[end]
	
	//[start] 显示
	/**
	 * Description: 显示节目列表
	 * @Version1.0 2012-3-16 下午5:34:22 zhaojunfeng created
	 */
	private void showList()
	{
		if( mPrglistAdapter == null ){
			mPrglistAdapter = new ProgramListAdapter(this);
			mListView.setAdapter(mPrglistAdapter);
		} else {
			mPrglistAdapter.notifyDataSetChanged();
		}
		if( mProgressDlg.isShowing() ){
			mProgressDlg.dismiss();
		}
	}
	
	/**
	 * Description: Toast显示信息
	 * @Version1.0 2012-3-8 上午10:57:12 zhaojunfeng created
	 * @param msg
	 */
	private void showMessage( String msg ){
		mToast.cancel();
		mToast.setText(msg);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}	
	//[end]
	
	//[start] 内部ProgramListAdapter
	private class ProgramListAdapter extends BaseAdapter{
		private Context mCon;
		
		public ProgramListAdapter(Context c){
			mCon = c;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListItem.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if( convertView == null ){
				LayoutInflater mInflater = (LayoutInflater) mCon.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				
				convertView = mInflater.inflate(R.layout.tvbackepg_program_item, parent, false);
				
				holder = new ViewHolder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tvbackepg_prg_item_name);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tvbackepg_prg_item_time);
				holder.tv_type = (TextView) convertView.findViewById(R.id.tvbackepg_prg_item_type);
				convertView.setTag(holder);				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			// title
			Object obj = mListItem.get(position).get(KeyEnum.title.toString());
			if( obj != null ){
				holder.tv_name.setText(obj.toString());
			} else {
				holder.tv_name.setText("");
			}
			
			obj = mListItem.get(position).get(KeyEnum.startTime.toString());
			if(obj!=null){
				holder.tv_time.setText(obj.toString());
			} else {
				holder.tv_time.setText("");
			}
			
			return convertView;
		}
		private class ViewHolder{
			TextView  tv_name;
			TextView  tv_time;
			TextView  tv_type;
		};
	}
	//[end]
	
	//[start] test data
	private void getTestProgramData(){
		for (int i = 0; i < 22; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>(); 
			if( i == 3 ){
				map.put(KeyEnum.title.toString(), String.format("东方时空%d-%s,2100110加上降低阿散井嗲", i, 
		        		  mListWeek.get(mListWeekSelect).get(WEEKLIST_KEY_WEEK)));				
			} else {
				map.put(KeyEnum.title.toString(), String.format("东方时空%d-%s", i, 
		        		  mListWeek.get(mListWeekSelect).get(WEEKLIST_KEY_WEEK)));				
			}

			map.put(KeyEnum.startTime.toString(), String.format("%02d:%02d", i,i));
			map.put(KeyEnum.categories.toString(), "回看");
			map.put(KeyEnum.recommendedRank.toString(), 8.5f);
			map.put(KeyEnum.assetId.toString(), i);
			map.put(KeyEnum.runTime.toString(), "7200");
			mListItem.add(map);  
		}
	}
	//[end]
}
