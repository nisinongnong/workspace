package com.jzbyapp.tr069service.basedata;

import android.content.Context;
import android.os.RemoteException;

import com.android.smart.terminal.iptv.aidl.EthernetDevInfo;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;


/**
 * 终端设备基于IP的网络连接信息
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class LAN extends Utils {
	public final String AddressingType_DHCP = "DHCP";
	public final String AddressingType_Static = "Static";
	public final String AddressingType_PPPoE = "PPPoE";
	public final String AddressingType_IPoE = "IPoE";//DHCP+
	private static LAN tr069LAN = null;
	private Context mContext = null;

	private EthernetDevInfo ethernetDevInfo = null;
	public EthernetInfo ethinfo = null;

	public LAN(Context context) {
		mContext = context;
	}

	public void ClearEthernetInfo() {
		if (ethinfo == null){
			ethinfo = new EthernetInfo();
		}
		ethinfo.dns = null;
		ethinfo.ipaddr = null;
		ethinfo.mask = null;
		ethinfo.mode = null;
		ethinfo.route = null;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static LAN GetInstance(Context context) {
		if (tr069LAN == null) {
			tr069LAN = new LAN(context);
		}
		return tr069LAN;
	}
	
	/**
	 * 解析本类下的具体节点，需要特殊处理的节点解析出来作具体动作，普通节点通过dataBaseGetOrSet()走正常的数据存取.
	 * 如果后续有需要特殊处理的节点，只需在本方法里添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return LAN类
	 */
	public String Process(Boolean action, String name, String value) {
		String lanInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		if ("AddressingType".equals(namearray[2])) {
			lanInfo = Process_AddressingType(action, name, value);
		} else if ("IPAddress".equals(namearray[2])) {
			lanInfo = Process_IPAddress(action, name, value);
		} else if ("SubnetMask".equals(namearray[2])) {
			lanInfo = Process_SubnetMask(action, name, value);
		} else if ("DefaultGateway".equals(namearray[2])) {
			lanInfo = Process_DefaultGateway(action, name, value);
		} else if ("DNSServers".equals(namearray[2])) {
			lanInfo = Process_DNSServers(action, namearray[2], value);
		} else if ("DNSServers2".equals(namearray[2])) {
			lanInfo = Process_DNSServers(action, namearray[2], value);
		} else if ("MACAddress".equals(namearray[2])) {
			lanInfo = Process_MACAddress(mContext, action, name, value);
		} else {
			lanInfo = dataBaseGetOrSet(mContext, action, name, value);
		}


		if (lanInfo == null) {
			lanInfo = "";
		}

		return lanInfo;

	}
	

	/**
	 * 设置或获取设备网络连接的类型
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return AddressingType:设备的网络连接方式
	 */
	public String Process_AddressingType(Boolean action, String name,String value) {
		String AddressingType = null;
		LogUtils.d("addressing type call is in >>> ");
		if (action) {
			try {
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
				if (ethernetDevInfo.mode.equals(Config.ETH_CONN_MODE_PPPOE))
					AddressingType = AddressingType_PPPoE;
				else if (ethernetDevInfo.mode.equals(Config.ETH_CONN_MODE_DHCPPLUS))
					AddressingType = AddressingType_IPoE;
				else if (ethernetDevInfo.mode.equals(Config.ETH_CONN_MODE_DHCP))
					AddressingType = AddressingType_DHCP;
				else if (ethernetDevInfo.mode.equals(Config.ETH_CONN_MODE_MANUAL))
					AddressingType = AddressingType_Static;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} else {
			String Key = "";
			if(AddressingType_PPPoE.equalsIgnoreCase(value)){
				Key = Config.ETH_CONN_MODE_PPPOE;
			}else if(AddressingType_IPoE.equalsIgnoreCase(value)){
				Key = Config.ETH_CONN_MODE_DHCPPLUS;
			}else if(AddressingType_DHCP.equalsIgnoreCase(value)){
				Key = Config.ETH_CONN_MODE_DHCP;
			}else if(AddressingType_Static.equalsIgnoreCase(value)){
				Key = Config.ETH_CONN_MODE_MANUAL;
			}
			try {
				LogUtils.d("set LAN key is >>> " + Key);
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
				ethernetDevInfo.mode = Key;
				Config.HisiSettingService.setEthernetDevInfo(ethernetDevInfo);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return AddressingType;
	}

	/**
	 * 设置或获取设备IP地址
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return IPAddress:设备的IP地址
	 */
	private String Process_IPAddress(Boolean action, String name, String value) {
		String IPAddress = null;
		if (action) {
			try {
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IPAddress = ethernetDevInfo.ipaddr;
		} else {
			setEthDevInfo("ipaddr",value);
		}
		return IPAddress;
	}
	
	/**
	 * 设置或获取设备的子网掩码
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return SubnetMask:设备的子网掩码
	 */
	private String Process_SubnetMask(Boolean action, String name, String value) {
		String SubnetMask = "";
		if (action) {
			try {
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SubnetMask = ethernetDevInfo.netmask;
		} else {
			setEthDevInfo("netmask",value);
		}
		return SubnetMask;
	}
	
	/**
	 * 设置或获取设备的网关
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return DefaultGateway:设备的网关
	 */
	private String Process_DefaultGateway(Boolean action, String name,
			String value) {
		String DefaultGateway = "";
		if (action) {
			try {
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DefaultGateway = ethernetDevInfo.route;
		} else {
			setEthDevInfo("route",value);
		}
		return DefaultGateway;
	}
	
	/**
	 * 设置或获取设备的DNS
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @return DNSServers:设备的DNS
	 */
	private String Process_DNSServers(Boolean action, String name, String value) {
		String DNSServers = "";
		if (action) {
			try {
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
				if(name.equalsIgnoreCase("DNSServers")){
					DNSServers = ethernetDevInfo.dns;
				}else if(name.equalsIgnoreCase("DNSServers2")){
					DNSServers = ethernetDevInfo.dns2;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			if(name.equalsIgnoreCase("DNSServers")){
				setEthDevInfo("dns",value);
			}else if(name.equalsIgnoreCase("DNSServers2")){
				setEthDevInfo("dns2",value);
			}
		}
		return DNSServers;
	}
	
	/**
	 * 为了减少重复代码，封装给自己用的一个接口，功能为设置网络信息
	 * @param str:String类型，需要设置EthDevInfo的成员
	 * @param value:String类型，设置EthDevInfo成员对应的值
	 * @author
	 */
	private void setEthDevInfo(String str, String value){
		try {
			ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
			if(str.equals("ipaddr")){
				ethernetDevInfo.ipaddr = value;
			}else if(str.equals("netmask")){
				ethernetDevInfo.netmask = value;
			}else if(str.equals("route")){
				ethernetDevInfo.route = value;
			}else if(str.equals("dns")){
				ethernetDevInfo.dns = value;
			}else if(str.equals("dns2")){
				ethernetDevInfo.dns2 = value;
			}
			LogUtils.d("the Lan mode is >>> " + ethernetDevInfo.mode);
			if((ethernetDevInfo.mode).equalsIgnoreCase("manual")){
				Config.HisiSettingService.setEthernetDevInfo(ethernetDevInfo);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void commitEthDevInfo(){
		try {
			if("Static".equalsIgnoreCase(ethinfo.mode)){
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();
				ethernetDevInfo.ipaddr = (null == ethinfo.ipaddr)?ethernetDevInfo.ipaddr:ethinfo.ipaddr;
				ethernetDevInfo.netmask = (null == ethinfo.mask)?ethernetDevInfo.netmask:ethinfo.mask;
				ethernetDevInfo.route = (null == ethinfo.route)?ethernetDevInfo.route:ethinfo.route;
				ethernetDevInfo.dns = (null == ethinfo.dns)?ethernetDevInfo.dns:ethinfo.dns;
				ethernetDevInfo.mode = "manual";						
				Config.HisiSettingService.setEthernetDevInfo(ethernetDevInfo);
			}			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	public class EthernetInfo {
		public String ipaddr;
		public String mode;
		public String route;
		public String mask;
		public String dns;
	}
}
