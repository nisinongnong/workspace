package com.jzbyapp.tr069service.telecom;

import android.content.Context;
import android.os.RemoteException;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.Utils;

/**
 * 机顶盒业务相关信息
 * @author
 * @see com.jzbyapp.tr069service.telecom.X_CTC_IPTV
 */
public class ServiceInfo extends Utils {
	private static ServiceInfo mtr069ServiceInfo = null;
	private Context mContext = null;
	
	private final static String DHCP_AUTHENTIC_ENABLE = "1";

	public ServiceInfo(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static ServiceInfo GetInstance(Context context) {
		if (mtr069ServiceInfo == null) {
			mtr069ServiceInfo = new ServiceInfo(context);
		}
		return mtr069ServiceInfo;
	}
	
	/**
	 * 解析ServiceInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ServiceInfo类，从属于X_CTC_IPTV类
	 */
	public String Process(Boolean action, String name, String value) {
		String mServiceInfoInfo = "";
		String[] namearray = name.split("[.]");

		if ("ServiceList".equals(namearray[3])) {
			mServiceInfoInfo = dataBaseGetOrSet(mContext, action, name, value);
		} else if ("PPPoEID".equals(namearray[3])) {
			mServiceInfoInfo = Process_PPPoEID(mContext, action, name, value);
		} else if ("PPPoEPassword".equals(namearray[3])) {
			mServiceInfoInfo = Process_PPPoEPassword(mContext,action, name,
					value);
		} else if ("DHCPID".equals(namearray[3])) {
			mServiceInfoInfo = Process_DHCPID(mContext, action, name,
					value);
		} else if ("DHCPPassword".equals(namearray[3])) {
			mServiceInfoInfo = Process_DHCPPassword(mContext, action,
					name, value);
		} else if ("IPTV_DHCP_Enable".equals(namearray[3])) {
			mServiceInfoInfo = Process_DHCPEnable(mContext, action,
					name, value);
		} else if ("IPTV_DHCP_Username".equals(namearray[3])) {
			mServiceInfoInfo = Process_DHCPID(mContext, action, name,
					value);
		} else if ("IPTV_DHCP_Password".equals(namearray[3])) {
			mServiceInfoInfo = Process_DHCPPassword(mContext, action,
					name, value);
		} else if ("AuthURL".equals(namearray[3])) {//iptv Auth not tr069
			mServiceInfoInfo = Process_AuthURL(mContext, action,
					name, value);
		} else if ("AuthURLBackup".equals(namearray[3])) {
			mServiceInfoInfo = Process_AuthURLBackup(mContext,
					action, name, value);
		} else if ("UserID".equals(namearray[3])) {
			mServiceInfoInfo = Process_AuthUserID(mContext, action,
					name, value);
		} else if ("Password".equals(namearray[3])) {
			mServiceInfoInfo = Process_AuthUserIDPassword(mContext, action,
					name, value);
		} else if ("UserPassword".equals(namearray[3])) {
			mServiceInfoInfo = Process_AuthUserIDPassword(mContext, action,
					name, value);
		} else {
			mServiceInfoInfo = dataBaseGetOrSet(mContext, action,
					name, value);
		}

		if (mServiceInfoInfo == null) {
			mServiceInfoInfo = "";
		}

		return mServiceInfoInfo;
	}
	
	/**
	 *前端对终端网络设置相关，操作DHCP，从属于ServiceInfo类，只见于江苏电信项目，在该项目上藐视也没有使用到
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，IPTV_DHCP_Enable
	 */
	private String Process_DHCPEnable(Context mcontext, Boolean action,
			String name, String value) {
		// TODO Auto-generated method stub
		String X_CTC_IPTV_ServiceInfoInfo_DHCPEnable = "false";
		if (action) {
			String state = null;
			try {
				state = Config.HisiSettingService.getValue(Config.DHCPAUTHENTIC, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (state.equals(DHCP_AUTHENTIC_ENABLE)) {
				X_CTC_IPTV_ServiceInfoInfo_DHCPEnable = "1";
			} else
				X_CTC_IPTV_ServiceInfoInfo_DHCPEnable = "0";
		} else {
			String dhcpAuthState = null;
			try {
				dhcpAuthState = Config.HisiSettingService.getValue(Config.DHCPAUTHENTIC, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (1 != Integer.parseInt(dhcpAuthState)&& "1".equals(value)) {
				try {
					Config.HisiSettingService.setValue(Config.DHCPAUTHENTIC, "1");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (1 == Integer.parseInt(dhcpAuthState) && "0".equals(value)) {
				try {
					Config.HisiSettingService.setValue(Config.DHCPAUTHENTIC, "0");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return X_CTC_IPTV_ServiceInfoInfo_DHCPEnable;
	}
}
