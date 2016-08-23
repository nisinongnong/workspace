package com.jzbyapp.tr069service.basedata;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.Utils;

import android.content.Context;

/**
 * 网管前端的信息参数
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class ManagementServer extends Utils {
	private static ManagementServer tr069ManagementServer = null;
	private Context mContext = null;

	public ManagementServer(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static ManagementServer GetInstance(Context context) {
		if (tr069ManagementServer == null) {
			tr069ManagementServer = new ManagementServer(context);
		}
		return tr069ManagementServer;
	}

	/**
	 * 解析本类下的具体节点，需要特殊处理的节点解析出来作具体动作，普通节点通过dataBaseGetOrSet()走正常的数据存取.
	 * 如果后续要扩展需特殊处理的节点，只需在本方法里面进行节点解析的扩展并添加相应的动作方法即可.
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ManagementServer类
	 */
	public String Process(Boolean action, String name, String value) {
		String managementServerInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		if ("URL".equals(namearray[2])) {// tr069 not iptv epg address
			managementServerInfo = Process_URL(action, name, value);
		} else if ("URLBackup".equals(namearray[2])) {
			managementServerInfo = Process_URLBackup(action, name, value);
		} else {
			managementServerInfo = dataBaseGetOrSet(mContext, action, name,
					value);
		}

		if (managementServerInfo == null) {
			managementServerInfo = "";
		}

		return managementServerInfo;

	}

	/**
	 * 对前端注册地址的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 首先需要初始化一个正确的值才能注册上前端.
	 * 有的前端这个值是不会变化的，但华为平台会在终端通过初始化的地址注册上终端后重新下发一个新地址，终端则需使用
	 * 新地址与前端交互了.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值
	 */
	private String Process_URL(Boolean action, String name, String value) {
		String TR069AUTHURL = null;
		if (action) {
			if (Config.mDevInfoManager != null) {
				TR069AUTHURL = Config.mDevInfoManager.getValue(Config.TR069AUTHURL);
			}
		} else {
			if (Config.mDevInfoManager != null) {
				Config.mDevInfoManager.update(Config.TR069AUTHURL, value,Config.devVersion);
			}
		}
		return TR069AUTHURL;
	}
	
	/**
	 * 对前端注册的备用地址的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 首先需要初始化一个正确的值才能注册上前端，如果这个地址注册不上，终端要尝试使用备用地址去注册前端.经验告诉我们
	 * 大部分前端给的这个值和主认证地址都是一样的.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值
	 */
	private String Process_URLBackup(Boolean action, String name, String value) {
		String TR069AUTHURLBAKUP = null;
		if (action) {
			if (Config.mDevInfoManager != null) {
				TR069AUTHURLBAKUP = Config.mDevInfoManager.getValue(Config.TR069AUTHURLBAKUP);
			}
		} else {
			if (Config.mDevInfoManager != null) {
				Config.mDevInfoManager.update(Config.TR069AUTHURLBAKUP, value,Config.devVersion);
			}
		}
		return TR069AUTHURLBAKUP;
	}

}
