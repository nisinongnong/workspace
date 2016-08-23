package com.jzbyapp.tr069service.basedata;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 终端设备描述
 * 不同的前端对于终端的设备描述由具体要求，有的前端不支持中文描述，有的前端对描述的字段长度有限制（有的前端对规范中定义的
 * 长度要求不一致）等，对接不同的前端需要按照前端的要求来初始化该类的描述(在前端于规范起冲突时以前端为准).
 * 一般情况下如果参数上报不对的话，终端会与前段有数据交互，但每次前端都会回复204 NO Content,拒接终端上线，无法进入后续调试
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class DeviceSummary extends Utils  {
	private static DeviceSummary tr069DeviceSummary = null;
	private Context mContext = null;

	public DeviceSummary(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static DeviceSummary GetInstance(Context context) {
		if (tr069DeviceSummary == null) {
			tr069DeviceSummary = new DeviceSummary(context);
		}
		return tr069DeviceSummary;
	}

	/**
	 * 解析本类下的具体节点，需要特殊处理的节点解析出来作具体动作，普通节点通过dataBaseGetOrSet()走正常的数据存取,
	 * 这个类暂时没有遇到需要特殊处理的节点，如果后续有需要特殊处理的节点，只需在本方法里添加需要扩展的节点解析和对应的
	 * 方法即可
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return DeviceSummary类
	 */
	public String Process(Boolean action, String name, String value) {
		String deviceSummary = "";
		String[] namearray = name.split("[.]");
		if ("STBID".equals(namearray[1])) {
			deviceSummary = Process_STBID(mContext, action, name, value);
		} else if("PhyMemSize".equals(namearray[1])) {
			deviceSummary = Process_PhyMemSize(mContext, action, name, value);
		} else if ("StorageSize".equals(namearray[1])) {
			deviceSummary = Process_StorageSize(mContext, action, name, value);
		} else {
			deviceSummary = dataBaseGetOrSet(mContext, action, name, value);			
		}
		return deviceSummary;
	}
}
