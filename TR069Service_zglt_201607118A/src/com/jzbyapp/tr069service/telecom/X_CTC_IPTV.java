package com.jzbyapp.tr069service.telecom;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 电信扩展参数
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class X_CTC_IPTV extends Utils {
	private static X_CTC_IPTV mtr069X_CTC_IPTV = null;
	private Context mcontext = null;

	public X_CTC_IPTV(Context context) {
		mcontext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static X_CTC_IPTV GetInstance(Context context) {
		if (mtr069X_CTC_IPTV == null){
			mtr069X_CTC_IPTV = new X_CTC_IPTV(context);
		}	
		return mtr069X_CTC_IPTV;
	}
	
	/**
	 * 解析X_CTC_IPTV类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.见Tr069_Process.java
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return X_CTC_IPTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mX_CTC_IPTVInfo = "";
		String[] namearray = name.split("[.]");

		if ("STBID".equals(namearray[2])) {
			mX_CTC_IPTVInfo = Process_STBID(mcontext, action, name, value);
		} else if ("PhyMemSize".equals(namearray[2])) {
			mX_CTC_IPTVInfo = Process_PhyMemSize(mcontext, action, name, value);
		} else if ("StorageSize".equals(namearray[2])) {
			mX_CTC_IPTVInfo = Process_StorageSize(mcontext, action, name, value);
		} else if ("StatisticConfiguration".equals(namearray[2])) {
			StatisticConfiguration mStatisticConfiguration = StatisticConfiguration.GetInstance(mcontext);
			mX_CTC_IPTVInfo = mStatisticConfiguration.Process(action, name, value);
		} else if ("ServiceStatistics".equals(namearray[2])) {
			ServiceStatistics mServiceStatistics = ServiceStatistics.GetInstance(mcontext);
			mX_CTC_IPTVInfo = mServiceStatistics.Process(action, name, value);
		} else if ("ServiceInfo".equals(namearray[2])) {
			ServiceInfo mServiceInfo = ServiceInfo.GetInstance(mcontext);
			mX_CTC_IPTVInfo = mServiceInfo.Process(action, name, value);
		} else if ("LogMsg".equals(namearray[2])) {
			LogMsg mLogMsg = LogMsg.GetInstance(mcontext);
			mX_CTC_IPTVInfo = mLogMsg.Process(action, name, value);
		} else if ("IMSInfo".equals(namearray[2])) {
			IMSInfo mIMSInfo = IMSInfo.GetInstance(mcontext);
			mX_CTC_IPTVInfo = mIMSInfo.Process(action, name, value);
		} else {
			mX_CTC_IPTVInfo = dataBaseGetOrSet(mcontext, action, name, value);
		}
		
		if (mX_CTC_IPTVInfo == null){
			mX_CTC_IPTVInfo = "";
		}
			
		return mX_CTC_IPTVInfo;
	}
}
