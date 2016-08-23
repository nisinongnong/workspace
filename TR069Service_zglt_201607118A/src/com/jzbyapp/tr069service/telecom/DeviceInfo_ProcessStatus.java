package com.jzbyapp.tr069service.telecom;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 第四级节点的解析，由于出现四级节点的情况比较少见(见DeviceInfo_MemoryStatus类)，暂时不再另建包来存放四级节点的解析类.
 * 如果以后出现较多这种情况足以将其归为一类的时候可以新建一个包来处理这一情况的节点以使节点的解析结构更为清晰.
 * 主要见于江苏电信项目规范中出现了四级节点（见DeviceInfo类里面解析出的ProcessStatus节点）.见DeviceInfo.java和DeviceInfo_MemoryStatus.java
 * @author
 * @see com.jzbyapp.tr069service.basedata.DeviceInfo
 */
public class DeviceInfo_ProcessStatus extends Utils {
	private static DeviceInfo_ProcessStatus tr069DevProcessStatus = null;
	private Context mContext = null;

	public DeviceInfo_ProcessStatus(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static DeviceInfo_ProcessStatus GetInstance(Context context) {
		if (tr069DevProcessStatus == null){
			tr069DevProcessStatus = new DeviceInfo_ProcessStatus(
					context);
		}
		return tr069DevProcessStatus;
	}
	
	/**
	 * 解析ProcessStatus类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 该类暂时只涉及CPUUsage一个节点，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作 
	 * @param name:String类型，具体节点名称 
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值 
	 * @author 
	 * @return ProcessStatus
	 */
	public String Process(Boolean action, String name, String value) {
		String ProcessStatus = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		if (("CPUUsage".equals(namearray[3]))||("CpuRate".equals(namearray[3]))) {
			ProcessStatus = Process_CpuRate(action, name, value);
		} else {
			ProcessStatus = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (ProcessStatus == null){
			ProcessStatus = "";
		}
	
		return ProcessStatus;
	}
	
	/**
	 * 获取终端cpu的频率，该参数为只读.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该节点为只读类型，action为get操作
	 * @author
	 * @return String类型的值，表示cpu的频率
	 */
	private String Process_CpuRate(Boolean action, String name, String value) {
		String CpuRate = null;
		float cpuRate = getProcessCpuRate();
		if (action) {
			CpuRate = String.valueOf(cpuRate);
		}

		return CpuRate;
	}

}
