package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;


/**
 * 移动华为平台扩展参数类,现有的经验没有遇到过实现要求，也没有实现细节，暂时不做任何操作此类的出现
 * 仅为适用移动提供的模型树规范并为后需后续有了具体要求和实现细节的扩展需要
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class Alarm extends Utils {
	private static Alarm mtr069Alarm = null;
	private Context mContext = null;

	public Alarm(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static Alarm GetInstance(Context context) {
		if (mtr069Alarm == null){
			mtr069Alarm = new Alarm(context);
		}
		return mtr069Alarm;
	}
	
	/**
	 * 由于该类暂时没有任何实现细节和子节点规范，暂时不做任何操作只走普通数据库
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return Alarm类，从属于X_00E0FC类
	 */
	public String Process(Boolean action, String name, String value) {
		String mAlarmInfo = "";

		mAlarmInfo = dataBaseGetOrSet(mContext, action, name,value);

		if (mAlarmInfo == null){
			mAlarmInfo = "";
		}

		return mAlarmInfo;

	}

}
