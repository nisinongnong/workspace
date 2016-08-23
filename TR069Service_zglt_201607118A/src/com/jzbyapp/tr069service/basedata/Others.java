package com.jzbyapp.tr069service.basedata;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;

import android.content.Context;
import android.os.RemoteException;

/**
 * 对于一些解析出来的非常规节点（不从属于任何类的节点）如：终端设备为了通讯自定义的节点,
 * 在此约定如后续需自定义节点都放此类里面处理.
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class Others extends Utils {
	private static Others tr069Other = null;
	private Context mContext = null;

	public Others(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static Others GetInstance(Context context) {
		if (tr069Other == null) {
			tr069Other = new Others(context);
		}
		return tr069Other;
	}
	
	/**
	 * 详细解析本类中的节点，并作出相应的操作动作.
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return Other类
	 */
	public String Process(Boolean action, String name, String value) {
		String Other = "";

		if ("JAVA_Tr069_SoftWareVersion".equals(name) || Config.UPGRADJUDGEMNT.equals(name)) {
			Other = Process_special(action, name, value);
		} else if ("Device.STBDisPlay.Fmt".equals(name)) {
			Other = Process_display(action, name, value);
		} else if (("cwmp.trans.starttime".equals(name)) || ("cwmp.trans.endtime".equals(name))) {
			Other = Utils.getTransforeTime(name);
		}  else {
			Other = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (Other == null) {
			Other = "";
		}

		return Other;

	}
	
	/**
	 * 自定义的一个版本号记录标记，主要用于升级判断，终端设备实时的的版本号由DeviceInfo.SoftwareVersion节点表示；
	 * 当升级完并将升级完成事件上报完给前端后需要将自定义的版本号标记置为实时版本号DeviceInfo.SoftwareVersion的值.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return OtherInfo
	 */
	private String Process_special(Boolean action, String name, String value) {
		String otherStr = "";
		if(action){
			otherStr = Config.mDevInfoManager.getValue(Config.UPGRADJUDGEMNT);
		}else{
			Config.mDevInfoManager.update(Config.UPGRADJUDGEMNT, value, Config.devVersion);
		}
		
		return otherStr;
	}

	private String Process_display(Boolean action, String name, String value) {
		LogUtils.i("Process_display >>> action="+action+",name="+name+",value="+value);
		String result = "";
		try {
			if(action){
				int resoultion = Config.HisiSettingService.getResoultion();
				result = Integer.toString(resoultion);
			}else{
				result = "" + Config.HisiSettingService.setResoultion(Integer.parseInt(value));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
