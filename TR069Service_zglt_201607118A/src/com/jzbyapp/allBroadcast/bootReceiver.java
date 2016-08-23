package com.jzbyapp.allBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jzbyapp.tr069service.Tr069Service;
import com.jzbyapp.tr069service.WatchAgain;
import com.jzbyapp.tr069service.WatchService;
import com.jzbyapp.utils.LogUtils;

/**
 * 开机广播接收器,当系统启动时会发送一个系统的开机广播，接受到广播后，启动本应用程序.最初的设计使用的是系统的开机广播:
 * android.intent.action.BOOT_COMPLETED，
 * 但是有的项目是集成第三方的luancher，不再发送系统广播了(也有可能被集成的应用修改了该广播action导致我们不能捕获到该
 * 开机广播)，自然tr069服务将不能唤起，所以我们在framework里面自己添加一个私有广播，可以通过我们自己的私有广播来拉起服务.
 * jzby的私有广播为:android.intent.action.JZBY_BOOT_BROADCAST,会比系统的开机广播快，添加广播方法见相关维护文档
 * 在此约定，标准版使用jzby的私有开机广播.见BroadcastListener.java
 * @author
 * @see com.jzbyapp.allBroadcast.BroadcastListener
 */
public class bootReceiver extends BroadcastReceiver {
	@Override
	/**
	 * 复写广播接收器，最原始的设计是在接收到广播后启动tr069service,但在浙江移动魔百盒的一个项目中，集成了比较多第三方
	 * 的应用，导致内存吃紧，在盒子开机跑了一段时间之后集成的第三方应用总是莫名将我们的service杀死，一旦被杀死，盒子掉线
	 * 不重启设备的话tr069将不会再运行，盒子一直处于离线状态，为了应对这一情况，添加两个服务来检测我们的主程序，一旦发现
	 * 程序死了，再次拉起程序避免盒子掉线，但可能会给程序的逻辑型带来一点凌乱.为了避免程序以后还有可能遇到被莫名杀死的情况，
	 * 约定标准版添加服务自检测功能watchService/watchagain服务.见WatchService.java和WatchAgain.java
	 * @param context:描述程序的环境信息，即上下文
	 * @param intent:android数据通信机制的一个组件
	 * @author
	 * @see com.jzbyapp.tr069service.WatchAgain  com.jzbyapp.tr069service.WatchService
	 */
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LogUtils.i("onReceive() is in, receivered a braodcast action is >>>>>"+intent.getAction());
		//if(intent.getAction().equalsIgnoreCase("android.intent.action.JZBY_BOOT_BROADCAST"))
		if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){
			Intent startService = new Intent(context, Tr069Service.class);
			startService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(startService);
			
/*			Intent watchService = new Intent(context, WatchService.class);
			watchService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(watchService);
			
			Intent watchagain = new Intent(context, WatchAgain.class);
			watchagain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(watchagain);
*/			
		}

	}

}
