package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;


/**
 * 移动扩展参数，终端相关信息
 * 该类下的字节点有STBID:机顶盒ID
 * 				PhyMemSize：总RAM大小，单位KB
 * 				StorageSize：存储大小，单位KB
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_CMCC_OTV
 */
public class STBInfo extends Utils {
	private static STBInfo mtr069STBInfo = null;
	private Context mContext = null;

	public STBInfo(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static STBInfo GetInstance(Context context) {
		if (mtr069STBInfo == null) {
			mtr069STBInfo = new STBInfo(context);
		}
		return mtr069STBInfo;
	}
	
	/**
	 * 解析STBInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return STBInfo类，从属于C_CMCC_OTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mSTBInfoInfo = "";
		String[] namearray = name.split("[.]");
		if ("STBID".equals(namearray[3])) {
			mSTBInfoInfo = Process_STBID(mContext, action, name,value);
		} else if ("PhyMemSize".equals(namearray[3])) {
			mSTBInfoInfo = Process_PhyMemSize(mContext, action, name, value);
		} else if ("StorageSize".equals(namearray[3])) {
			mSTBInfoInfo = Process_StorageSize(mContext, action, name, value);
		} else{
			mSTBInfoInfo = dataBaseGetOrSet(mContext, action, name,value);
		}

		if (mSTBInfoInfo == null){
			mSTBInfoInfo = "";
		}

		return mSTBInfoInfo;

	}

}
