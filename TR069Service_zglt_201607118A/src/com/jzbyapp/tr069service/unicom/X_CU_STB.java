package com.jzbyapp.tr069service.unicom;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 联通扩展参数
 * @author
 */
public class X_CU_STB extends Utils {
	private static X_CU_STB mtr069X_CU_STB = null;
	private Context mContext = null;

	public X_CU_STB(Context context) {
		mContext = context;
	}

	public static X_CU_STB GetInstance(Context context) {
		if (mtr069X_CU_STB == null) {
			mtr069X_CU_STB = new X_CU_STB(context);
		}
		return mtr069X_CU_STB;
	}
	
	
	/**
	 * 解析X_CU_STB类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return X_CU_STB类
	 */
	public String Process(Boolean action, String name, String value) {
		String mX_CU_STBInfo = "";
		String[] namearray = name.split("[.]");
		if ("STBInfo".equals(namearray[2])) {
			STBInfo mSTBInfo = STBInfo.GetInstance(mContext);
			mX_CU_STBInfo = mSTBInfo.Process(action, name, value);
		} else if ("StatisticConfiguration".equals(namearray[2])) {
			StatisticConfiguration mStatisticConfiguration = StatisticConfiguration.GetInstance(mContext);
			mX_CU_STBInfo = mStatisticConfiguration.Process(action, name, value);
		} else if ("ServiceStatistics".equals(namearray[2])) {
			ServiceStatistics mServiceStatistics = ServiceStatistics.GetInstance(mContext);
			mX_CU_STBInfo = mServiceStatistics.Process(action, name, value);
		} else if ("AuthServiceInfo".equals(namearray[2])) {
			AuthServiceInfo mAuthServiceInfo = AuthServiceInfo.GetInstance(mContext);
			mX_CU_STBInfo = mAuthServiceInfo.Process(action, name, value);
		} else if ("Alarm".equals(namearray[2])) {
			Alarm mAlarm = Alarm.GetInstance(mContext);
			mX_CU_STBInfo = mAlarm.Process(action, name, value);
		} else {
			mX_CU_STBInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mX_CU_STBInfo == null) {
			mX_CU_STBInfo = "";
		}

		return mX_CU_STBInfo;

	}

}
