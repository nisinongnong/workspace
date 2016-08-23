package com.jzbyapp.tr069service.basedata;

import android.content.Context;

import com.jzbyapp.tr069service.telecom.DeviceInfo_MemoryStatus;
import com.jzbyapp.tr069service.telecom.DeviceInfo_ProcessStatus;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;

/**
 * 机顶盒详细信息描述类
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class DeviceInfo extends Utils {
	private static DeviceInfo tr069DeviceInfo = null;
	private Context mContext = null;

	public DeviceInfo(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static DeviceInfo GetInstance(Context context) {
		if (tr069DeviceInfo == null) {
			tr069DeviceInfo = new DeviceInfo(context);
		}
		return tr069DeviceInfo;
	}

	/**
	 * 解析Deviceinfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return Deviceinfo类
	 */
	public String Process(Boolean action, String name, String value) {
		String deviceInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

		if ("SerialNumber".equals(namearray[2])) {
			deviceInfo = Process_STBID(mContext, action, name, value);
		} else if ("HardwareVersion".equals(namearray[2])) {
			deviceInfo = Process_HardwareVersion(mContext, action, name, value);
		} else if ("SoftwareVersion".equals(namearray[2])) {
			deviceInfo = Process_SoftwareVersion(mContext, action, name, value);
		} else if("UpTime".equals(namearray[2])){
			deviceInfo = getUpTime();
		} else if("FirstUseDate".equals(namearray[2])){
			deviceInfo = dataBaseGetOrSet(mContext, action, name, value);
		} else if ("ProvisioningCode".equals(namearray[2])) {
			deviceInfo = dataBaseGetOrSet(mContext, action, name, value);
		} else if ("DeviceLog".equals(namearray[2])) {
			deviceInfo = LogUtils.getSystemLog();
		} else if ("ProcessStatus".equals(namearray[2])) {
			DeviceInfo_ProcessStatus ProcessStatus = DeviceInfo_ProcessStatus.GetInstance(mContext);
			deviceInfo = ProcessStatus.Process(action, name, value);
		} else if ("MemoryStatus".equals(namearray[2])) {
			DeviceInfo_MemoryStatus MemoryStatus = DeviceInfo_MemoryStatus.GetInstance(mContext);
			deviceInfo = MemoryStatus.Process(action, name, value);
		} else {// not special nodes get/set
			deviceInfo = Utils.dataBaseGetOrSet(mContext, action, name, value);
		}

		if (deviceInfo == null) {
			deviceInfo = "";
		}

		return deviceInfo;
	}
}
