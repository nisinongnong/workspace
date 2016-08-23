package com.jzbyapp.tr069service.mesQueue;

import java.io.File;
import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jzbyapp.jzdownloadutil.JZDownload;
import com.jzbyapp.jzupload.JZFtpUpload;
import com.jzbyapp.jzupload.JZHttpUpload;
import com.jzbyapp.openuser.ZeroConfigActivity;
import com.jzbyapp.tr069service.R;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.DataBackup;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;


/**
 * 消息队列处理，这里主要用来接受消息并对接收到的消息进行处理，在这里约定如果需要改动频繁的模块接口不放入此类中，
 * 现在这里有的功能接口如下：RebootSystem()重启接口，不同版本该接口固定
 * 						ResetSystem()恢复出厂接口，不同版本该接口固定
 * 						Upgrade(Message, String)下载准备工作(初始化)接口，不同版本该接口固定
 * 						Download(String, String, String, String)下载接口，不同版本该接口已固定
 * 						Upload(Message)文件上传接口，不同版本该接口已固定
 * 						InitUpgradeView()下载进度条显示
 * 在这里约定，如果后续版本移植如需要扩展该类，对与没有共性的接口尽量不要放入此类中，仅可能做到这个类功能接口的稳定性，
 * 不轻易改变，只做到扩展该类的消息类型不实现缺乏共性功能的接口. 
 * @author
 */
public class MessageQueue {
	public final int MESSAGE_UPLOAD = 1;
	public final int MESSAGE_REBOOT = 2;
	public final int MESSAGE_FACTORY = 3;
	public final static int MESSAGE_UPGRADE = 4;
	public final static int MESSAGE_UPGRADEBOOTPIC = 5;
	
	//bjlt zeroConfig
	public final int MESSAGE_OPENUSER = 6;
	public final int MESSAGE_OPENUSERACTIVE = 7;
	public final int MESSAGE_OPENUSERFAILED = 8;
	public final static int MESSAGE_SCREENSHOT = 9;
	public final static int MESSAGE_CAPTURE = 10;
	public final static int MESSAGE_INFORMATION_COLLECT = 11;
	public final static int MESSAGE_DATABACKUP = 12;
	public final static int MESSAGE_DATARECOVERY = 13;
	public final int MESSAGE_LOGCATSTART = 14;
	public final int MESSAGE_LOGCATSTOP = 15;

	public Context mContext;
	ZeroConfigActivity mActivity = null;
	private JZDownload downloadTask;
	public TextView textview = null;
	public WindowManager WindowUpgrade;
	public View mlayout;
	private AlertDialog.Builder AlterD;
	private AlertDialog mDialog = null;
	private LayoutInflater myLayoutInflater;
	private LinearLayout myLayout;
	private String Logpath = "";
	private boolean CollectionFlag = false;
	

	private AsyncHttpResponseHandler mResponse = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			LogUtils.i("http upload onStart() >>> ");
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBody) {
			// Successfully got a response
			LogUtils.i("http upload onSuccess() >>> ");
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			// Response failed :(
			LogUtils.i("http upload onFailure() >>> ");
		}

		@Override
		public void onRetry(int retryNo) {
			// Request was retried
			LogUtils.i("http upload onRetry() >>> ");
		}

		@Override
		public void onProgress(int bytesWritten, int totalSize) {
			// Progress notification
			LogUtils.i("http upload onProgress() >>> ");
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
			LogUtils.i("http upload onFinish() >>> ");
			Utils.rmFile(Logpath);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			LogUtils.i("messageQueue received a message >>>" + msg.what);
			switch (msg.what) {
				case MESSAGE_UPLOAD:
					Upload(msg);
					break;
				case MESSAGE_REBOOT:
					RebootSystem();
					break;
				case MESSAGE_FACTORY:
					ResetSystem();
					break;
				case MESSAGE_UPGRADE:
					Upgrade(msg,"system");
					break;
				case MESSAGE_UPGRADEBOOTPIC:
					Upgrade(msg,"bootpic");
					break;
				case MESSAGE_OPENUSER:
					startActivityForzeroConfig("TRUE");
					break;
				case MESSAGE_OPENUSERACTIVE:
					callProgressDialog(mContext.getString(R.string.openuser_activate), 2);
					break;
				case MESSAGE_OPENUSERFAILED:
					cacelProgressDialog();
					startActivityForzeroConfig("FALSE");
					break;
				case MESSAGE_SCREENSHOT:
					saveCustomViewBitmap();
					break;
				case MESSAGE_CAPTURE:
					capture(true,false);
					break;
				case MESSAGE_INFORMATION_COLLECT:
					informationCollect();
					break;
				case MESSAGE_DATABACKUP:
					databackup();
					break;
				case MESSAGE_DATARECOVERY:
					datarecovery();
					break;
				case MESSAGE_LOGCATSTART:
					logcatAnyTime(true);
					break;
				case MESSAGE_LOGCATSTOP:
					logcatAnyTime(false);
					break;
			}
		}
	};

	public MessageQueue(Context context) {
		mContext = context;
	}
	
	/**
	 * 设备系统重启，由于系统重启需要系统权限，而eclipse编译出来的apk不具备系统权限，
	 * 为了调试方便，让本应用能够在eclipse进行编译，将该功能通过跨进程通讯交给ServiceSetting进程进行处理
	 * 见com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl.java
	 * @author
	 * @see com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl
	 */
	private void RebootSystem() {
		if(Config.HisiSettingService != null){
			try {
				Config.HisiSettingService.setValue(Config.REBOOTRESET, null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设备恢复出厂设置，由于该功能需要系统权限，而eclipse编译出来的apk不具备系统权限，
	 * 为了调试方便，让本应用能够在eclipse进行编译，将该功能通过跨进程通讯交给ServiceSetting进程进行处理
	 * 见com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl.java
	 * @author
	 * @see com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl
	 */
	private void ResetSystem() {	
		if (Config.HisiSettingService != null) {
			try {
				Config.HisiSettingService.setValue(Config.FACTORYRESET, null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 升级包下载准备工作的接口，一开始的设计，这个接口提供下载系统升级包，为了适用不同前端，该接口进行扩展，
	 * 可在此约定:凡是下载准备工作(初始化:获取到下载链接/下载接入用户名/下载接入用户密码)都使用这个接口
	 * @param msg:封装的消息，里面含有下载的链接地址/用户名/密码等
	 * @param pictureOrSystem:判断下载何种文件，现有系统升级包和开机画面升级包两种
	 * @author
	 */
	private void Upgrade(Message msg, String pictureOrSystem) {
		String url = msg.getData().getString("url");
		String patch = msg.getData().getString("patch");
		String Pwd = msg.getData().getString("upgrade_pwd");
		String Usrname = msg.getData().getString("upgrade_usrname");
		Utils.saveCurrentTime("cwmp.trans.starttime");
		if("system".equalsIgnoreCase(pictureOrSystem)){
			Download(url, patch, Usrname, Pwd);
		}else if("bootpic".equalsIgnoreCase(pictureOrSystem)){
			Download(url, "/cache/bootanimation.zip", Usrname, Pwd);
		}
		InitUpgradeView();
	}
	
	/**
	 * 下载线程入口，该接口主要用来启动下载线程，开始进入下载工作
	 * @param url:下载的链接地址
	 * @param patch:下载到本地的路径
	 * @param Usrname:下载用户名
	 * @param Pwd:下载密码
	 * @author
	 */
	private void Download(String url, String patch, String Usrname, String Pwd) {
		downloadTask = new JZDownload(mContext, url, patch);
		downloadTask.setUseBroadcast();
		if (Usrname != null && Pwd != null) {
		LogUtils.d("downloadTask.setAuthority >>> ");
			downloadTask.setAuthority(Usrname.toString().trim(), Pwd.toString()
					.trim());
		}
		downloadTask.startDownload();
	}
	
	/**
	 * 文件上传接口，向前端服务器上传文件的接口.支持对http/ftp类型服务器上传
	 * @param msg：封装的消息体，里面携带上传的目的地址/服务器类型/需要上传本地文件的存储路径
	 * @author
	 */
	private void Upload(Message msg) {
		String type = msg.getData().getString("type");
		String url = msg.getData().getString("url");
		String patch = msg.getData().getString("patch");
		LogUtils.i("the file path is >>> " + patch);
		Logpath = patch;
		String userName = msg.getData().getString("username");
		String passWord = msg.getData().getString("password");
		
		LogUtils.i("upload service type is >>> " + type);
		if ("http".equals(type)) {
			//JZHttpUpload.upload(url, new File(patch), mResponse);
			JZHttpUpload.upload(url, new File(patch), userName, passWord, mResponse);
			// e.g: JZHttpUpload.upload("http://192.168.2.200", new
			// File("/cache/test.txt"), mResponse);
		} else if ("ftp".equals(type)) {
			String count = msg.getData().getString("count");
			String password = msg.getData().getString("password");

			JZFtpUpload.upload(url, -1, count, password, null, patch, null);
			// e.g: JZFtpUpload.upload(url, -1, "admin", "12345678", null,
			// "/cache/update.zip" , null);
		}
	}

	public void sendMessage(Message msg) {
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 下载进度条显示，这个接口本与View相关，但考虑到这个显示基本固定不会进行更改.另从代码的逻辑性和使用的便捷性考虑，
	 * 决定将该接口放入此类中
	 * @author
	 */
	private void InitUpgradeView() {	
		//WindowManager WindowUpgrade;
		WindowManager.LayoutParams LayoutParamsUpgrade;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mlayout = inflater.inflate(R.layout.upgrade, null);
	    textview = (TextView) mlayout
				.findViewById(R.id.upgrade_percent);

		LayoutParamsUpgrade = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		LayoutParamsUpgrade.gravity = Gravity.RIGHT | Gravity.BOTTOM;

		WindowUpgrade = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		WindowUpgrade.addView(mlayout, LayoutParamsUpgrade);

		textview.setText("0 %");
	}
	
	/**
	 * 零配值弹框接口，一般电信和联通前端都会要求零配置功能(移动运营商还没遇到过)，现该接口基本固定(即通过该接口启动弹窗
	 * 界面而已)，以后根据不同项目要修改的就是ZeroConfigActivity类中的布局.
	 * 在此约定，以后零配置的界面修改都在ZeroConfigActivity类里面完成，见ZeroConfigActivity.java
	 * @param str:String字串
	 * @author
	 * @see com.jzbyapp.openuser.ZeroConfigActivity
	 */
	private void startActivityForzeroConfig(String str) {
		LogUtils.i("start Activity for zeroconfig >>> ");
		Intent intent = new Intent(mContext, ZeroConfigActivity.class);
		Bundle bundle = new Bundle();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		bundle.putString("openflag", str);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
	}
	
	/**
	 * 在零配置弹框基于零配置与用户交互的dialog显示，此专为北京联通零配置要求的实现(北京联通零配置要求给用户提示:
	 * 零配置串号输入显示--输入串号后提示用户正在激活--激活成功提示激活成功/失败提示激活失败).此接口即为在用户输
	 * 入串号后提示用户正在激活和激活成功的dialog显示.
	 * 在此约定，此接口不作为标准版功能，在此出现只是为了利用标准版去对接北京联通前端的需要而作的简单功能实现并没有
	 * 作细致考虑.以后对接不同项目可另行更好的实现方法.
	 * @param str:String类型，提示用户的显示字段
	 * @param arg:int类型
	 */
	public void callProgressDialog(String str,int arg){
		if(AlterD == null){
			AlterD = new AlertDialog.Builder(mContext);
		}
		LogUtils.i("callProgressDialog() is in >>> " + str + "arg is >>> " + arg);
		myLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myLayout = (LinearLayout) myLayoutInflater.inflate(R.layout.progressbar_dialog,null);
		ProgressBar bar = (ProgressBar) myLayout.findViewById(R.id.myprogressbar);
		AlterD.setTitle(R.string.openuser_reminder);
		AlterD.setMessage(str);
		AlterD.setView(myLayout);
		AlterD.setCancelable(true);
		mDialog = AlterD.create();
		if(arg == 1){
			bar.setVisibility(ProgressBar.VISIBLE);
		}else if(arg == 2){
			bar.setVisibility(ProgressBar.GONE);
		}
		LogUtils.d("mdialog is show begin >>> ");
		mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mDialog.show();
		LogUtils.d("mdialog is show end >>> ");
		WindowManager.LayoutParams pl = mDialog.getWindow().getAttributes();
		pl.width = 220;
		pl.height = 280;
		mDialog.getWindow().setAttributes(pl);
		LogUtils.d("the display width is" + pl.width + "height is" + pl.height);
	}

	public void cacelProgressDialog(){
		if (null != mDialog) {
			mDialog.dismiss();
		}
	}

	private void saveCustomViewBitmap() {
		String captime = Utils.getCurrentTime("yyyyMMdd-HHmmss",0);
		String mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "screenshot_" + captime + ".png";
		try {
			Runtime.getRuntime().exec("screencap -p " + mSavedPath); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private void capture(boolean flag, boolean timeflag) {
		String mSavedPath = null;
		if (timeflag){
			String captime = Utils.getCurrentTime("yyyyMMdd-HHmmss",0);
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "capture" + captime + ".pcap";			
		}else {
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "capture.pcap";			
		}
		
		if (flag && !CollectionFlag) {
			String Address = " -i any -p -s 0 -w "+mSavedPath;
			String jztcpdump = "jztcpdump:"+Address;
			SystemProperties.set("ctl.start",jztcpdump);
		} else if (!flag && CollectionFlag) {
			SystemProperties.set("ctl.stop","jztcpdump");
		}
	}

	private void logcat(boolean flag, boolean timeflag) {
		String mSavedPath = null;
		if(timeflag){
			String captime = Utils.getCurrentTime("yyyyMMdd-HHmmss",0);
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "logcat" + captime + ".log";
		}else{
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "logcat.log";
		}
		
		if (flag && !CollectionFlag) {
			String jzlogcat = "jzlogcat:"+mSavedPath;
			SystemProperties.set("ctl.start",jzlogcat);
		} else if (!flag && CollectionFlag) {
			SystemProperties.set("ctl.stop","jzlogcat");
		}
	}
	
	private void  logcatAnyTime(boolean flag) {
		String mSavedPath = null;
		String captime = Utils.getCurrentTime("yyyyMMdd-HHmmss",0);
		mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "currentlogcat"+captime+".log";
		if (flag) {
			String jzlogcat = "jzlogcat:"+mSavedPath;
			SystemProperties.set("ctl.start",jzlogcat);
		} else {
			SystemProperties.set("ctl.stop","jzlogcat");
		}
	}
	
	private void databackup() {
		DataBackup mDataBackup = new DataBackup(mContext);
		mDataBackup.StartDataBackup();
	}
	
	private void datarecovery() {
		DataBackup mDataBackup = new DataBackup(mContext);
		mDataBackup.StartDataRecovery();
	}
	
	private void informationCollect() {
		Thread th = new Thread(new Runnable() { 

            @Override 
            public void run() { 
                // TODO Auto-generated method stub          		        	
                try {
                	capture(true,false);
            		logcat(true,false);
            		GetSystemState(true,false);
            		CollectionFlag = true;
            		LogUtils.i("before Thread.sleep");
            		// collect 5 minute STB information.
            		Thread.sleep(1000*60*5);
            		LogUtils.i("after Thread.sleep");
            		capture(false,false);
	        		logcat(false,false);
	        		GetSystemState(false,false);
	        		CollectionFlag = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            } 
        }); 
        th.start(); 
	}

	private void GetSystemState(boolean flag, boolean timeflag){
		String mSavedPath = null;
		if(timeflag){
			String captime = Utils.getCurrentTime("yyyyMMdd-HHmmss",0);
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "systate" + captime + ".log";
		}else{
			mSavedPath = Environment.getExternalStorageDirectory()+File.separator + "systate.log";
		}
		
		if (flag && !CollectionFlag) {
			String jzsystate = "jzsystate:"+mSavedPath;
			SystemProperties.set("ctl.start",jzsystate);
		} else if (!flag && CollectionFlag) {
			SystemProperties.set("ctl.stop","jzsystate");
		}
	}
}
