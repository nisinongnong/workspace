package com.jzbyapp.tr069service.mobile;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 华为扩展参数，STB定时开关机时间配置.这个类比较特殊，主要有
 * IsAutoPowerOn:状态启用，1启用/0不启用
 * AutoPowerOnTime:自动开机时间，格式HH:MM,如08:00
 * AutoShutDownTime:自动关机时间，格式HH:MM,如08:00
 * 对接过的运营商中，暂时还没有对该类有要求的，只是化为平台自己扩展的类，没有见过其实现细节，暂没实现。
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class AutoOnOffConfiguration extends Utils{
	private static AutoOnOffConfiguration mtr069AutoOnOffConfiguration = null;
	private Context mContext = null;
	
	public AutoOnOffConfiguration(Context context){
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static AutoOnOffConfiguration GetInstance(Context context){
		if(mtr069AutoOnOffConfiguration == null) {
			mtr069AutoOnOffConfiguration = new AutoOnOffConfiguration(context);
		}
		return mtr069AutoOnOffConfiguration;
	}
	
	/**
	 * 解析AutoOnOffConfiguration类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走普通
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return AutoOnOffConfiguration类，从属于X_00E0FC类，移动特有
	 */
	public String Process(Boolean action, String name, String value) {
		String mAutoOnOffConfigurationInfo = "";
		String[] namearray = name.split("[.]");

		if ("IsAutoPowerOn".equals(namearray[3])) {
			// complete in future
		} else if ("AutoPowerOnTime".equals(namearray[3])) {
			// complete in future
		} else if ("AutoShutDownTime".equals(namearray[3])) {
			// complete in future
		} else {
			mAutoOnOffConfigurationInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mAutoOnOffConfigurationInfo == null) {
			mAutoOnOffConfigurationInfo = "";
		}

		return mAutoOnOffConfigurationInfo;
	}
	

	
}
