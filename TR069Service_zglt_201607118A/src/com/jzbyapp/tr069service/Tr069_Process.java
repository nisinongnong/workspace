package com.jzbyapp.tr069service;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.jzbyapp.tr069service.basedata.DeviceInfo;
import com.jzbyapp.tr069service.basedata.DeviceSummary;
import com.jzbyapp.tr069service.basedata.GatewayInfo;
import com.jzbyapp.tr069service.basedata.IPTV;
import com.jzbyapp.tr069service.basedata.LAN;
import com.jzbyapp.tr069service.basedata.ManagementServer;
import com.jzbyapp.tr069service.basedata.Others;
import com.jzbyapp.tr069service.basedata.STBService;
import com.jzbyapp.tr069service.basedata.Time;
import com.jzbyapp.tr069service.basedata.UserInterface;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;
import com.jzbyapp.tr069service.mobile.X_00E0FC;
import com.jzbyapp.tr069service.mobile.X_CMCC_OTV;
import com.jzbyapp.tr069service.telecom.X_CTC_IPTV;
import com.jzbyapp.tr069service.unicom.X_CU_STB;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.DataBackup;
import com.jzbyapp.utils.LogUtils;

/**
 * 模型树节点的第一级解析
 * tr069对终端与前端交互的数据以指定的格式实现，如：Ddevice.DeviceInfo.XXX.XXX,具体节点代表某个特定的含义
 * （注：存在不同前端对同一个节点所表达的含义不同）.终端的功能即通过解析节点，根据规范的要求实现具体节点的功能，
 * 再通过协议层传送给前端.
 * 所以tr069的实现分为C协议层，与前端通讯的一个通道；java功能层，解析节点实现节点要求的具体功能，并做好数据的管理.
 * @author 
 */
public class Tr069_Process {
	Context mContext;

	public Tr069_Process(Context context) {
		super();
		this.mContext = context;
	}
	
	/**
	 * 普通数据的获取接口，即在provider(系统数据库)中获取数据的接口
	 * @param name：需要获取数据的key值(实则对应的节点名)
	 * @return
	 */
	public String tr069GetValue(String name) {
		return TR069_Process(Config.GETACTION, name, null);
	}
	
	/**
	 * 普通数据的存储接口，即在provider(系统数据库)中存储数据的接口
	 * @param name:需要设置数据的key值(实则对应的节点名)
	 * @param value:需要设置节点对应的值
	 */
	public void tr069SetValue(String name, String value) {
		TR069_Process(Config.SETACTION, name, value);
	}
	
	/**
	 * 所有节点解析的入口，为了数据交互的便捷性，不管是C协议层还是其他进程如（iptv/播放器等）都通过统一形式来进行通讯，
	 * 通过传递一个key(即为一个节点名)来获取对应的值，本程序通过解析这个key来实现节点的解析，再根据规范中节点的含义来
	 * 做相关的动作.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作(如set/get and so on...),
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null,如果action为其他动作则对应具体需要的value值；
	 * @return
	 */
	public String TR069_Process(Boolean action, String name, String value) {
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
			if (namearray == null) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

		try {
			if (namearray.length > 1) {
				// 解析节点namearray[1]
				if (JustifyDeviceRootNode(namearray[1])) {
					DeviceSummary mDeviceSummary = DeviceSummary.GetInstance(mContext);
					return mDeviceSummary.Process(action, name, value);
				} else if ("DeviceInfo".equals(namearray[1])) {
					DeviceInfo mDeviceInfo = DeviceInfo.GetInstance(mContext);
					return mDeviceInfo.Process(action, name, value);
				} else if ("ManagementServer".equals(namearray[1])) {
					ManagementServer mManagementServer = ManagementServer.GetInstance(mContext);
					return mManagementServer.Process(action, name, value);
				} else if ("STBService".equals(namearray[1])) {
					STBService mSTBService = STBService.GetInstance(mContext);
					return mSTBService.Process(action, name, value);
				} else if ("Time".equals(namearray[1])) {
					Time mTime = Time.GetInstance(mContext);
					return mTime.Process(action, name, value);
				} else if ("LAN".equals(namearray[1])) {
					LAN mLAN = LAN.GetInstance(mContext);
					return mLAN.Process(action, name, value);
				} else if ("GatewayInfo".equals(namearray[1])) {
					GatewayInfo mGatewayInfo = GatewayInfo.GetInstance(mContext);
					return mGatewayInfo.Process(action, name, value);
				} else if ("Config".equals(namearray[1])) {
					DataBackup mDataBackup = new DataBackup(mContext);
					return mDataBackup.Process(action, name, value);
				} else if ("UserInterface".equals(namearray[1])) {
					UserInterface mUserInterface = UserInterface.GetInstance(mContext);
					return mUserInterface.Process(action, name, value);
				} else if ("X_CTC_IPTV".equals(namearray[1])) {// telecom expansion
					X_CTC_IPTV mX_CTC_IPTV = X_CTC_IPTV.GetInstance(mContext);
					return mX_CTC_IPTV.Process(action, name, value);
				} else if ("X_CU_STB".equals(namearray[1])) {// unicom expansion
					X_CU_STB mX_CU_STB = X_CU_STB.GetInstance(mContext);
					return mX_CU_STB.Process(action, name, value);
				} else if ("X_CMCC_OTV".equals(namearray[1])) {// mobile expansion
					X_CMCC_OTV mX_CMCC_OTV = X_CMCC_OTV.GetInstance(mContext);
					return mX_CMCC_OTV.Process(action, name, value);
				} else if ("X_00E0FC".equals(namearray[1])) {// mobile expansion
					X_00E0FC mX_00E0FC = X_00E0FC.GetInstance(mContext);
					return mX_00E0FC.Process(action, name, value);
				} else if ("ZeroConfig".equals(namearray[1])) {
					/*Tr069_ZeroConfig_Bind ZeroConfig_Bind = Tr069_ZeroConfig_Bind
							.GetInstance(context);
					return ZeroConfig_Bind.Process(action, name, value);*/
				} else if ("IPTV".equals(namearray[1])) {
					IPTV mIPTV = IPTV.GetInstance(mContext);
					return mIPTV.Process(action, name, value);					
				} else {
					Others Other = Others.GetInstance(mContext);
					return Other.Process(action, name, value);
				}	
			} else if(Config.UPGRADJUDGEMNT.equals(name)){
				LogUtils.d("the other UPGRADJUDGEMNT in arryname is " + namearray[0]);
				if(action){
					return Config.mDevInfoManager.getValue(Config.UPGRADJUDGEMNT);
				}else{
					Config.mDevInfoManager.update(Config.UPGRADJUDGEMNT, value, Config.devVersion);
				}
			} else {
				LogUtils.d("the other is in arryname is " + namearray[0]);
				Others Other = Others.GetInstance(mContext);
				return Other.Process(action, name, value);
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}
	
	private boolean JustifyDeviceRootNode (String name) {
		int i = 0;
		String NodeArray[] = {"STBID", "DeviceSummary", "CPU", "PhyMemSize", "StorageSize", 
				"DeviceType"};
		for (i = 0; i < NodeArray.length; i++) {
			if (NodeArray[i].equals(name))
				return true;
		}
		return false;
	}
}
