package com.jzbyapp.tr069service.basedata;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 主要见于移动网关类设备涉及到此类，对于电信和联通规范中暂时没有遇到过涉及此类，而移动的网关设备里面对该类的要求
 * 也仅限于ManufacturerOUI：设备厂商描述；ProductClass：设备类型描述；SerialNumber：设备系列号，都在终端
 * 设备初始化中已经完成(可以查看assets路径下的initDataNode.xml文件，该文件在编译的时候会自动打包到apk里面，
 * 对外是不可见的，正因为这样，在终端设备system/etc下面也放置一份该文件，本程序代码会先操作system/etc路径下的
 * initDataNode.xml，如果该文件不存在再去操作assets路径下的initDataNode.xml文件，这样的好处就是如果只需要
 * 修改一些节点值的话不需要开发人员提供代码修改的支持方便测试人员在不能修改代码的情况下可以将system/etc路径下的文
 * 件拿出来修改，对initDataNode.xml文件的使用详见相关文档).
 * 如果后续对接工作中对该类有了新要求再作具体实现.
 * @author 
 */
public class GatewayInfo extends Utils {
	/*
	 * Information of gateway device
	 * ManufacturerOUI
	 * ProductClass
	 * SerialNumber   for init in xml
	 * look up com.jzbyapp.tr069service.Tr069_Process
	 */

	private Context mContext = null;
	private static GatewayInfo tr069GatewayInfo = null;
	
	public GatewayInfo(Context context) {
		mContext = context;
	}
	
	public static GatewayInfo GetInstance(Context context) {
		if (tr069GatewayInfo == null) {
			tr069GatewayInfo = new GatewayInfo(context);
		}
		return tr069GatewayInfo;
	}
	
	public String Process(Boolean action, String name, String value) {
		String gatewayInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

		if ("SerialNumber".equals(namearray[2])) {
			gatewayInfo = Process_STBID(mContext, action, name, value);
		} else {// not special nodes get/set
			gatewayInfo = Utils.dataBaseGetOrSet(mContext, action, name, value);
		}

		if (gatewayInfo == null) {
			gatewayInfo = "";
		}

		return gatewayInfo;
	}	
}
