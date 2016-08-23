package com.jzbyapp.tr069service.unicom;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 联通报警相关参数
 * @author
 * @see com.jzbyapp.tr069service.unicom.X_CU_STB
 */
public class Alarm extends Utils {
	private static Alarm mtr069Alarm = null;
	private Context mcontext = null;

	public Alarm(Context context) {
		mcontext = context;
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
	 * 解析Alarm类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走普通
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return Alarm类,从属于X_CU_STB类
	 */
	public String Process(Boolean action, String name, String value) {
		String X_CU_STB_AlarmInfo = "";
		//String[] namearray = name.split("[.]");

		X_CU_STB_AlarmInfo = dataBaseGetOrSet(mcontext, action, name, value);
		
		if (X_CU_STB_AlarmInfo == null){
			X_CU_STB_AlarmInfo = "";			
		}

		return X_CU_STB_AlarmInfo;

	}

}
