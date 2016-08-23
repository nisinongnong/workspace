package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;

/**
 * 华为平台扩展类，只见于移动.根据对接的经验，没有遇到过营运商对此类有要求，此仅为华为平台的扩展类，
 * 也为见过其类下面的字节点和具体实现规范，暂时不做任何实现，只为后续有了具体实现细节和要求时扩展使用
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class LogParaConfiguration extends Utils {
	private static LogParaConfiguration mtr069LogParaConfiguration = null;
	private Context mContext = null;

	public LogParaConfiguration(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static LogParaConfiguration GetInstance(Context context) {
		if (mtr069LogParaConfiguration == null) {
			mtr069LogParaConfiguration = new LogParaConfiguration(context);
		}
		return mtr069LogParaConfiguration;
	}
	
	/**
	 * 由于该类暂时没有任何实现细节和子节点规范，暂时不做任何操作只走普通数据库
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return LogParaConfiguration类，从属于X_00E0FC类
	 */
	public String Process(Boolean action, String name, String value) {
		String mLogParaConfigurationInfo = "";
		//String[] namearray = name.split("[.]");

		mLogParaConfigurationInfo = dataBaseGetOrSet(mContext, action,name, value);


		if (mLogParaConfigurationInfo == null) {
			mLogParaConfigurationInfo = "";
		}

		return mLogParaConfigurationInfo;

	}

}
