package com.jzbyapp.tr069service.telecom;

import com.jzbyapp.utils.Utils;

import android.content.Context;

/**
 * 实时日志上报，采用文件的方式记录日志，并以规定的文本格式上传
 * @author
 * @see com.jzbyapp.tr069service.telecom.X_CTC_IPTV
 */
public class LogMsg extends Utils {
	private static LogMsg mtr069LogMsg = null;
	private Context mContext = null;

	public LogMsg(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static LogMsg GetInstance(Context context) {
		if (mtr069LogMsg == null) {
			mtr069LogMsg = new LogMsg(context);
		}
		return mtr069LogMsg;
	}
	
	/**
	 * 解析ServiceInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.见X_CTC_IPTV.java
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ServiceInfo类，从属于X_CTC_IPTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mLogMsgInfo = "";
		//String[] namearray = name.split("[.]");

		mLogMsgInfo = dataBaseGetOrSet(mContext, action, name, value);

		if (mLogMsgInfo == null) {
			mLogMsgInfo = "";
		}

		return mLogMsgInfo;
	}

}
