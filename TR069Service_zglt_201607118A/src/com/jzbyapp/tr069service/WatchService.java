package com.jzbyapp.tr069service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * 监听tr069service，防止被意外杀死，不考虑该服务在不在运行每隔5s就启动一次tr069service，显得有点简单粗暴(貌似没有
 * 找到更好的方法了)，这样就是要保证tr069service的start()方法中最好放空预防tr069service在正常跑的情况下强制启动同
 * 一个服务导致一些其他问题出现(因为只要service在跑，再一次启动该service会回调service的start方法，详细了解可查阅android
 * 系统中service组件的运行周期)
 * @author
 * @see com.jzbyapp.tr069service.WatchAgain
 */
public class WatchService extends Service {

	private Context mContext;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 1000: {
				startTr069Service();
			}
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		mContext = this;
		startTr069Service();
	}
	
	/**
	 * 每隔5秒发送一次消息启动service
	 */
	private void startTr069Service() {
		Intent i = new Intent(mContext, Tr069Service.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startService(i);

		Intent ii = new Intent(mContext, WatchAgain.class);
		ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startService(ii);

		Message msg = new Message();
		msg.what = 1000;
		mHandler.sendMessageDelayed(msg, 5000);
	}
}
