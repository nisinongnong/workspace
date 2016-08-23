package com.jzbyapp.tr069service.telecom;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.Utils;

import android.content.Context;

/**
 * 电信扩展参数，机顶盒业务性能统计信息
 * @author
 * @see com.jzbyapp.tr069service.telecom.X_CTC_IPTV
 */
public class ServiceStatistics extends Utils {
	private static ServiceStatistics mtr069ServiceStatistics = null;
	private Context mContext = null;

	public ServiceStatistics(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static ServiceStatistics GetInstance(Context context) {
		if (mtr069ServiceStatistics == null) {
			mtr069ServiceStatistics = new ServiceStatistics(context);
		}

		return mtr069ServiceStatistics;
	}
	
	
	/**
	 * 解析ServiceStatistics类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走普通
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ServiceStatistics类,从属于X_CTC_IPTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mServiceStatisticsInfo = "";
		String[] namearray = name.split("[.]");

		if ("AuthNumbers".equals(namearray[3])) {
			mServiceStatisticsInfo = Process_AuthNumbers(mContext,action,name, value);
		} else if ("AuthFailNumbers".equals(namearray[3])) {
			mServiceStatisticsInfo = Process_AuthFailNumbers(mContext,action,name, value);
		} else{
			mServiceStatisticsInfo = dataBaseGetOrSet(mContext,action, name, value);
		}
			
		if (mServiceStatisticsInfo == null){
			mServiceStatisticsInfo = "";
		}
			
		return mServiceStatisticsInfo;

	}
	
	
	/**
	 * 统计IPTV认证次数
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型，IPTV认证次数
	 */
	private String Process_AuthNumbers(Context context, Boolean action, String name, String value) {
		String AuthNumbers_info = "";
		if (action) {
			AuthNumbers_info = dataBaseGetOrSet(mContext, action, name, value);
		} else {
			String oldAuthNumbers = dataBaseGetOrSet(mContext,Config.GETACTION, name, value);
			String newAuthNumbers;
			try {
				newAuthNumbers = String.valueOf(Integer.parseInt(oldAuthNumbers) + 1);
			} catch (Exception e) {
				newAuthNumbers = "1";
			}
			AuthNumbers_info = dataBaseGetOrSet(mContext, action, name,newAuthNumbers);
		}

		return AuthNumbers_info;
	}
	
	
	/**
	 * 统计IPTV认证失败的次数
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型，IPTV认证失败的次数
	 */
	private String Process_AuthFailNumbers(Context context, Boolean action, String name,
			String value) {
		String AuthFailNumbers_info = "";
		if (action) {
			AuthFailNumbers_info = dataBaseGetOrSet(mContext, action, name,
					value);
		} else {
			String oldAuthFailNumbers = dataBaseGetOrSet(mContext,Config.GETACTION, name, value);
			String newAuthFailNumbers;
			try {
				newAuthFailNumbers = String.valueOf(Integer.parseInt(oldAuthFailNumbers) + 1);
			} catch (Exception e) {
				newAuthFailNumbers = "1";
			}
			AuthFailNumbers_info = dataBaseGetOrSet(mContext, action, name,newAuthFailNumbers);
		}

		return AuthFailNumbers_info;
	}

}
