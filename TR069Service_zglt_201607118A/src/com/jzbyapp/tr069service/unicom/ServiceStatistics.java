package com.jzbyapp.tr069service.unicom;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 联通扩展参数，机顶盒业务性能统计信息
 * @author
 * @see com.jzbyapp.tr069service.unicom.X_CU_STB
 */
public class ServiceStatistics extends Utils {
	private static ServiceStatistics mtr069ServiceStatistics = null;
	private Context mContext = null;

	public ServiceStatistics(Context context) {
		this.mContext = context;
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
	 * 解析ServiceStatistics类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ServiceStatistics类,从属于X_CU_STB类
	 */
	public String Process(Boolean action, String name, String value) {
		String mServiceStatisticsInfo = "";
		//String[] namearray = name.split("[.]");

		mServiceStatisticsInfo = dataBaseGetOrSet(mContext, action, name, value);

		if (mServiceStatisticsInfo == null){
			mServiceStatisticsInfo = "";
		}

		return mServiceStatisticsInfo;

	}

}
