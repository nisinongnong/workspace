package com.jzbyapp.tr069service.mobile;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 移动扩展参数：业务相关信息
 * @author 
 * @see com.jzbyapp.tr069service.mobile.X_CMCC_OTV
 */
public class ServiceInfo extends Utils {
	private static ServiceInfo mtr069ServiceInfo = null;
	private Context mContext = null;

	public ServiceInfo(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static ServiceInfo GetInstance(Context context) {
		if (mtr069ServiceInfo == null) {
			mtr069ServiceInfo = new ServiceInfo(context);
		}
		return mtr069ServiceInfo;
	}
	
	/**
	 * 解析ServiceInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可。
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ServiceInfo类,从属于X_CMCC_OTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mServiceInfo = "";
		String[] namearray = name.split("[.]");
		if ("PPPoEID".equals(namearray[3])) {
			mServiceInfo = Process_PPPoEID(mContext,action, name, value);
		} else if ("PPPoEPassword".equals(namearray[3])) {
			mServiceInfo = Process_PPPoEPassword(mContext, action, name,value);
		} else if ("DHCPID".equals(namearray[3])) {
			mServiceInfo = Process_DHCPID(mContext, action, name,value);
		} else if ("DHCPPassword".equals(namearray[3])) {
			mServiceInfo = Process_DHCPPassword(mContext, action, name, value);
		} else if ("UserID".equals(namearray[3])) {
			mServiceInfo = Process_AuthUserID(mContext, action, name,value);
		} else if ("Password".equals(namearray[3])) {
			mServiceInfo = Process_AuthUserIDPassword(mContext, action,name, value);
		} else if ("AuthURL".equals(namearray[3])) {
			mServiceInfo = Process_AuthURL(mContext, action, name,value);
		} else if ("AuthURLBackup".equals(namearray[3])) {
			mServiceInfo = Process_AuthURLBackup(mContext,action, name, value);
		}else {
			mServiceInfo = dataBaseGetOrSet(mContext, action,name, value);
		}

		if (mServiceInfo == null){
			mServiceInfo = "";
		}
			
		return mServiceInfo;
	}
	
}
