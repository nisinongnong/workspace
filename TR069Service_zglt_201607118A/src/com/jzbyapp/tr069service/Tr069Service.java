package com.jzbyapp.tr069service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;

import com.android.smart.terminal.iptv.aidl.ServiceSettingsInfoAidl;
import com.jzbyapp.allBroadcast.BroadcastListener;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;
import com.jzbyapp.tr069service.socketListener.ServerDevice;
import com.jzbyapp.tr069service.socketListener.TCPSERVERThread;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;
import com.jzbyapp.utils.domXmlParse;

/**
 * tr069服务类，通过com.jzbyapp.allBroadcast.bootReceiver包里的bootReceiver接收到开机广播后启动该服务
 * 主要职能就是初始化数据(终端需要上线的必须数据，在xml文件里配置)initData()，初始化与其他进程通讯需要的服务(如
 * 与播放器通讯使用tcp)initService()，声明JNI通讯的本地方法.
 * 在此约定:本服务类，只进行必要的初始化工作/本地方法的声明/消息的转发.为了保持代码的纯洁性，不再添加任何不必要的
 * 冗余代码
 * @author
 */
public class Tr069Service extends Service {
	public static boolean JNILOAD = false;
	private int versionIsInitData;
	private String isFirstUseData = "";
	private Context mContext;
	private LogUtils logutil;
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Config.HisiSettingService = ServiceSettingsInfoAidl.Stub.asInterface(service);
		}
	};
	
	public native void Tr069ServiceInit();
	public native static void Tr069ServiceSystemDown();  
    public native static void TR069ServiceSendMsg(int type,String rslt);
    
    /**
     * 往动态节点数据库里面插入一个节点
     * @param name:需要插入的节点名称
     * @author
     * @return
     */
    public int[] JNI_GET_DYNAMICNODE_INSTANCE(String name) {
		return Config.dynamicnode.GetAllSubInstance(name);
	}
    
    /**
     * 往动态节点数据库里面添加一个节点.动态节点的全称由上层java控制，在添加一个节点时必须先调用该接口返回一个i值给调用者
     * @param name:需要添加的节点名
     * @author
     * @return
     */
	public int JNI_ADD_DYNAMICNODE(String name) {
		return Config.dynamicnode.AddDynamicNode(name);
	}
	
	/**
	 * 设置动态节点
	 * @param name:需要设置的节点名称
	 * @param value:对应节点的设置值
	 * @author
	 */
	public void JNI_SET_DYNAMICNODE(String name, String value) {
		Config.dynamicnode.SetDynamicNode(name, value);
	}
	
	/**
	 * 获取动态节点
	 * @param name:需要获取的节点名称
	 * @author
	 * @return
	 */
	public String JNI_GET_DYNAMICNODE(String name) {
		return Config.dynamicnode.GetDynamicNode(name);
	}
	
	/**
	 * 删除某个动态节点
	 * @param name:需要删除的节点名称
	 * @author
	 */
	public void JNI_DEL_DYNAMICNODE(String name) {
		Config.dynamicnode.DelDynamicNode(name);
	}
	
	/**
	 * 设置动态节点属性
	 * @param name:节点名称
	 * @param attrbute:属性值
	 * @param attrlist:属性列表
	 * @author
	 */
	public void JNI_SET_ATTRBUTE(String name, String attrbute, String attrlist) {
		Config.dynamicnode.SetParamAttribute(name, attrbute, attrlist);
	}
	
	/**
	 * 获取动态节点属性
	 * @param name:需要获取的动态节点名称
	 * @author
	 * @return String数组类型
	 */
	public String[] JNI_GET_ATTRBUTE(String name) {
		return Config.dynamicnode.GetParamAttribute(name);
	}

	public int JNI_GET_LOGLEVEL() {
		return logutil.loglevelControl;
	}

	public String JNI_GET_LOGTAG() {
		StackTraceElement caller = LogUtils.getCallerStackTraceElement();
		return LogUtils.generateTag(caller);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	static{		
		System.loadLibrary("tr069jni");
		JNILOAD = true;	
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		LogUtils.i("tr069Service onCreate is in >>>>>");
		LogUtils.writeSystemLog("tr069Service", "onCreate", "tr069");
		initService();
		initData();
		registerReceiver();
				
		if(JNILOAD){
			Tr069ServiceInit();
		}
	}

	/**
	 * 注册广播接收器，在此做一个约定，以后需要扩展其他广播，两步：在此处添加广播接收要匹配的action,然后
	 * 在BroadCastListener中添加接收到广播后的动作(见com.jzbyapp.allBroadcast.BroadCastListener.java).
	 * @author 
	 * @see com.jzbyapp.allBroadcast.BroadcastListener
	 */
	private void registerReceiver() {
		// TODO Auto-generated method stub
		LogUtils.i("register recevicer broadcast action");
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("android.intent.action.SCREEN_OFF");
		//mFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mFilter.addAction("android.intent.action.SCREEN_ON");
		//mFilter.addAction(Intent.ACTION_SCREEN_ON);
		mFilter.addAction("com.jzbyapp.jzdownloadutil");
		mFilter.addAction("android.intent.action.SCREEN_SHOT");
		BroadcastListener broadcastListener = new BroadcastListener();
		registerReceiver(broadcastListener.receiver, mFilter);
	}
	
	@Override
	public int onStartCommand(Intent arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return super.onStartCommand(arg0, arg1, arg2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(serviceConnection);
		super.onDestroy();
	}

	
    /**
     * 初始化数据，这里统一使用xml文件的格式来进行初始化数据.为了尽可能缩小本程序与系统的耦合性，将初始化节点的配置文件
     * 放在应用的assets路径下.
     * 特别处理下节点Device.DeviceInfo.FirstUseData(终端设备第一次开机的时间)节点值，在此约定:恢复出厂/升级被认为
     * 第一次开机(如认为不妥，可商讨修改)
     * @author
     */
	private void initData() {
		// TODO Auto-generated method stub
		LogUtils.i("initData() is in >>> ");
		InputStream is = null;
		try {
			isFirstUseData = Settings.System.getString(this.getContentResolver(),"Device.DeviceInfo.FirstUseData");
			if(isFirstUseData==null || isFirstUseData.equals("")){
				String firstUseData = Utils.getCurrentTime("yyyy:MM:dd:HH:mm:ss",0);
				Settings.System.putString(this.getContentResolver(), "Device.DeviceInfo.FirstUseData", firstUseData);
			}
			versionIsInitData = Settings.System.getInt(this.getContentResolver(), "JiuZhou.STB.VersionControl",0);
			LogUtils.i("Current version is >>> " + versionIsInitData);
			File file = new File("/system/etc/initDataNode.xml");
			if(file.isFile() && file.exists() ){
				is = new FileInputStream(file);
			}else{
				is = getAssets().open("initDataNode.xml");
			}
			domXmlParse xmlParse = new domXmlParse();
			xmlParse.parse(mContext, versionIsInitData, is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *初始化服务: DevInfoManagerService主要是操作关键数据库devinfodb;
	 *          MessageQueue主要处理消息事件
	 *          com.android.smart.terminal.iptv.aidl.ServiceSettings主要操作网络等上层应用没有权限执行的任务
	 *          ServerDevice为一个tcp服务端，由本应用实现，用来监听iptv和播放器(后续有其他进程需要与tr069通讯都可使用该服务端)
	 *          LogUtils为自己实现的一个打印控制工具，不喜欢使用的话可以关闭
	 *          DynamicNode为动态节点操作管理
	 *          POWER_SERVICE为待机锁的一个服务，在此抢占待机锁，避免当设备待机后相关信息没有处理完即进入待机状态
	 *@author      
	 */
	@SuppressLint("Wakelock")
	private void initService() {
		LogUtils.i("initService() is in >>> ");
		
		//print level control
		Config.pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
		if (getVersionSwitch().equals(Config.RockChipVersion)) {
			Config.wakeLock = Config.pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				this.getClass().getCanonicalName());
			logutil = new LogUtils(mContext, "com.jzbyapp.tr069service", "/iptv_data/printfConfig.xml");
		} else {
			Config.wakeLock = Config.pm.newWakeLock(Config.SUSPEND_WAKE_LOCK,
				this.getClass().getCanonicalName());
			logutil = new LogUtils(mContext, "com.jzbyapp.tr069service", "/flashdata/printfConfig.xml");                
		}
		Config.wakeLock.acquire();			
			
		//service for keydata access
		Config.mDevInfoManager = new DevInfoManagerService(mContext, getVersionSwitch());
		Config.messageQueue = new MessageQueue(mContext);
		if(Config.dataParseThread == null){
			Config.dataParseThread = new ParseThread(mContext);
			LogUtils.i("the ParseThread is create >>> ");
		}
		
		//STB collection
        Message msg = Message.obtain();
        msg.what = MessageQueue.MESSAGE_INFORMATION_COLLECT;
        Config.messageQueue.sendMessage(msg);
        
		//start hisi system service
		//bindService(new Intent("com.android.smart.terminal.iptv.aidl.ServiceSettings"), serviceConnection, Context.BIND_AUTO_CREATE);
		//Explicit for up android4.4
		Intent intent = new Intent("com.android.smart.terminal.iptv.aidl.ServiceSettings");
		//intent.setPackage("com.android.smart.terminal.iptv.aidl");
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		
		//start UDP service
		ServerDevice sd = new ServerDevice(8300, 8301);
		sd.startListening();
		//tcp service for iptv or mediaplayer
		TCPSERVERThread tcpTask = new TCPSERVERThread(Tr069Service.this);
		tcpTask.start();
		
		//dynamicNode control
		Config.dynamicnode = new DynamicNodeManagerService(this);
		
		databackup();
	}
	
	/**
	 * 协议层C通过JNI数据交互的入口.每一条数据都是通过这个入口建立一个线程来处理.
	 * @param spellStrData:协议C层传过来需要处理的命令
	 * @author
	 * @return String类型，数据处理的结果
	 */
	public String TR069_COMMUNICATION_VALUE(String spellStrData) {
		LogUtils.d("the jni string is >>>" + spellStrData);
		String dataParseThreadResult = "";
		dataParseThreadResult = Config.dataParseThread.Process(spellStrData);
		return dataParseThreadResult;
	}
	
	private String getVersionSwitch() {
		String Version = null;
		InputStream is = null;
		try {
			File file = new File("/system/etc/initDataNode.xml");
			if(file.isFile() && file.exists() ){
				is = new FileInputStream(file);
			}else{
				is = getAssets().open("initDataNode.xml");
			}
			domXmlParse xmlParse = new domXmlParse();
			Version = xmlParse.getVersionSwitch(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return Version;
	}
	
	private void databackup() {
        Message msg = Message.obtain();
        msg.what = MessageQueue.MESSAGE_DATABACKUP;
        Config.messageQueue.sendMessage(msg);
	}
}
