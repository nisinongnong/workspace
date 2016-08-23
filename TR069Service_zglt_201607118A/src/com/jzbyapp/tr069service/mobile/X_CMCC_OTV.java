package com.jzbyapp.tr069service.mobile;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 移动扩展参数类，根据开发经验，移动的扩展参数有：X_OOEOC(该类主要是化为平台自己的扩展，一般情况下运营商不作要求)
 * 和X_CMCC_OTV两个类，而电信的扩展类于X_CTC_IPTV类，联通的扩展类有:X_CU_STB类
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class X_CMCC_OTV extends Utils {
	private static X_CMCC_OTV mtr069X_CMCC_OTV = null;
	private Context mContext = null;

	public X_CMCC_OTV(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static X_CMCC_OTV GetInstance(Context context) {
		if (mtr069X_CMCC_OTV == null) {
			mtr069X_CMCC_OTV = new X_CMCC_OTV(context);
		}
		return mtr069X_CMCC_OTV;
	}
	
	/**
	 * 解析X_CMCC_OTV类下面的节点或类，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return X_CMCC_OTV类,从属于device类
	 */
	public String Process(Boolean action, String name, String value) {
		String mX_CMCC_OTVInfo = "";
		String[] namearray = name.split("[.]");
		if ("STBInfo".equals(namearray[2])) {
			STBInfo mSTBInfo = STBInfo.GetInstance(mContext);
			mX_CMCC_OTVInfo = mSTBInfo.Process(action, name, value);
		} else if ("ServiceInfo".equals(namearray[2])) {
			ServiceInfo mServiceInfo = ServiceInfo.GetInstance(mContext);
			mX_CMCC_OTVInfo = mServiceInfo.Process(action, name, value);
		} else {
			mX_CMCC_OTVInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mX_CMCC_OTVInfo == null){
			mX_CMCC_OTVInfo = "";
		}

		return mX_CMCC_OTVInfo;

	}

}
