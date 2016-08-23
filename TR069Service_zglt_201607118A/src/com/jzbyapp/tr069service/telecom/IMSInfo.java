package com.jzbyapp.tr069service.telecom;

import com.jzbyapp.utils.Utils;

import android.content.Context;


/**
 * 终端支持IMS业务相关信息，仅当Device.X_CMCC_IPTV.ServiceInfo.ServiceList中包含"VideoComm"时此参数对象生效
 * 仅在江苏电信项目上见过此类，但没有具体实现细节，此类现放空(走普通数据存储)，仅为后续有了具体要求和具体实现细节再扩展使用.
 * @author
 * @see com.jzbyapp.tr069service.telecom.X_CTC_IPTV 
 */
public class IMSInfo extends Utils{
	private static IMSInfo mtr069IMSInfo = null;
	private Context mContext = null;
	
	public IMSInfo(Context context){
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static IMSInfo GetInstance(Context context){
		if(mtr069IMSInfo == null){
			mtr069IMSInfo = new IMSInfo(context);
		}		
		return mtr069IMSInfo;
	}
	
	/**
	 * 解析IMSInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.见X_CTC_IPTV.java
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return IMSInfo类，从属于X_CTC_IPTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mIMSInfoInfo = "";
		// String[] namearray = name.split("[.]");

		mIMSInfoInfo = dataBaseGetOrSet(mContext, action, name, value);

		if (mIMSInfoInfo == null) {
			mIMSInfoInfo = "";
		}

		return mIMSInfoInfo;
	}
}
