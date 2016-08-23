package com.jzbyapp.allBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.WindowManager;

import com.jzbyapp.tr069service.Tr069Service;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;
import com.jzbyapp.tr069service.upgrade.UpgradeDownloadCompleteActivity;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;


/**
 * 广播监听器，这个广播监听器与bootReceiver广播接收器有区别的，之所以利用两个类分开，是因为bootRecceiver里面接受的是
 * 开机广播，进而拉起本程序。对于本程序来说开机广播必须要使用静态注册的方式才能拉起本程序的作用（见Mainfest）.如果为了代
 * 码统一将其放入下面的BroadcastReceiver里面，系统会默认为动态注册广播(而动态注册广播是在tr069service起来之后注册的)
 * 导致不能接受到开机广播(动态注册的广播优先级默认比静态注册的广播高)。但是有关待机锁的系统广播，如果为了与bootReceiver统
 * 一而注册为静态广播，应用是收不到该广播的，这两个广播又必须要注册为动态广播的形式，权衡再三，还是采用两个类来实现广播的接收.
 * 见bootReceiver.java和AndroidMainfest.xml
 * @author 
 * @see com.jzbyapp.allBroadcast.bootReceiver
 */
public class BroadcastListener {
	public static final String DOWNLOAD_BROADCAST = "com.jzbyapp.jzdownloadutil";
	public static final String SCREEN_SHOT_BROADCAST = "android.intent.action.SCREEN_SHOT";
	
	/**
	 * 广播接受器，在这里做一个约定，以后需要扩展广播的接收动作都是放到此处，具体实现两步，在tr069service注册
	 * 广播的地方添加action字段，然后在下面添加接受收到对应action广播后的动作.
	 * 见com.jzbyapp.tr069service.Tr069Service.registerReceiver();
	 * @author 
	 * @see com.jzbyapp.tr069service.Tr069Service
	 */
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context mContext, Intent intent) {
			LogUtils.i("onReceive is in receivered a broadcast >>> " + intent.getAction());
			if (DOWNLOAD_BROADCAST.equals(intent.getAction())) {
				if ("finish".equals(intent.getExtras().getString("type"))&&(0 == intent.getExtras().getInt("status"))) {
					WindowManager mWindowUpgrade = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
					Utils.saveCurrentTime("cwmp.trans.endtime");
					if (intent.getExtras().getString(Config.FILEPATH).endsWith("update.zip")) {
						Intent path = new Intent(mContext,UpgradeDownloadCompleteActivity.class);
						path.putExtra(Config.UPDATE_FILE_PATH, intent.getExtras().getString(Config.FILEPATH));
						path.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(path);
						Config.mDevInfoManager.update(Config.UPGRADJUDGEMNT, "1", Config.devVersion);
						mWindowUpgrade.removeView(Config.messageQueue.mlayout);
					}else if(intent.getExtras().getString(Config.FILEPATH).endsWith("bootanimation.zip")){
						boolean isOk = Utils.copyFile("/cache/bootanimation.zip", "/data/local/bootanimation.zip");
					    if(isOk){
					    	Utils.rmFile("/cache/bootanimation.zip");
					    	Utils.chmodFile("/data/local/bootanimation.zip");
					    	Tr069Service.TR069ServiceSendMsg(3,"TRUE");
					    	LogUtils.d("send to c >>>>> ");
					    	mWindowUpgrade.removeView(Config.messageQueue.mlayout);
					    }
					}
				} else if ("downloading".equals(intent.getExtras().getString("type"))) {
					if (intent.getExtras().getString(Config.FILEPATH).endsWith("update.zip")
							|| intent.getExtras().getString(Config.FILEPATH).endsWith("bootanimation.zip")) {						
						long cur = intent.getIntExtra("current", 0);
						long tol = intent.getIntExtra("total", 0);
						int percent = (int) ((long) cur * 100 / tol);
						Config.messageQueue.textview.setText(percent + "%");
					}
				}
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				try {
					Tr069Service.Tr069ServiceSystemDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(SCREEN_SHOT_BROADCAST)) {
				LogUtils.i("SCREEN_SHOT_BROADCAST >>>");
		        Message msg = Message.obtain();
		        msg.what = MessageQueue.MESSAGE_SCREENSHOT;
		        Config.messageQueue.sendMessage(msg);				
			}
		}
	};
}
