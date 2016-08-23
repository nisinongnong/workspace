package com.jzbyapp.tr069service.unicom;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 机顶盒信息
 * @author
 * @see com.jzbyapp.tr069service.unicom.X_CU_STB
 */
public class STBInfo extends Utils{
	private static STBInfo mtr069STBInfo = null;
	private Context mContext = null;

	public STBInfo(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static STBInfo GetInstance(Context context) {
		if (mtr069STBInfo == null){
			mtr069STBInfo = new STBInfo(context);
		}
			
		return mtr069STBInfo;
	}
	
	
	/**
	 * 解析STBInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return STBInfo类，从属X_CU_STB类
	 */
	public String Process(Boolean action, String name, String value) {
		String mSTBInfo = "";
		String[] namearray = name.split("[.]");

		if ("STBID".equals(namearray[3])) {
			mSTBInfo = Process_STBID(mContext, action, name,value);
		} else if ("PhyMemSize".equals(namearray[3])) {
			mSTBInfo = Process_PhyMemSize(mContext,action, name, value);
		} else if ("StorageSize".equals(namearray[3])) {
			mSTBInfo = Process_StorageSize(mContext, action, name, value);
		} else if ("BrowserURL1".equals(namearray[3])) {
			mSTBInfo = Process_AuthURL(mContext, action, name, value);
		} else if ("BrowserURL2".equals(namearray[3])) {
			mSTBInfo = Process_AuthURLBackup(mContext, action, name, value);
		} else{
			mSTBInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mSTBInfo == null){
			mSTBInfo = "";
		}
		
		return mSTBInfo;

	}

}
