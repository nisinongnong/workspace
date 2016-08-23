package com.jzbyapp.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;

import com.jzbyapp.tr069service.Tr069Service;

public class Utils {
	public static void sendBroadcase(byte[] bs, int len, int port, short times,String ip) throws IOException {
		DatagramSocket toSocket = null;

		//Log.e("1","goal ip="+ip);
		try {
			toSocket = new DatagramSocket();
			DatagramPacket toPacket = new DatagramPacket(bs, len, InetAddress.getByName(ip), port);
			toSocket.setBroadcast(true);
			//Log.e("1","LocalHost"+InetAddress.getByName(null));
			//Log.e("1","BCAdress"+InetAddress.getByName("255.255.255.255"));


			try {
				//Log.e("DEV", "send to " + port + "," + bs.length + "," + bs[0] + "," + bs[1]);
				toSocket.send(toPacket);
			}
			catch(IOException ie) {
				ie.printStackTrace();
			}

			for(int i=1; i<times; i++) {
				try {
					Thread.sleep(100);
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				try {
					//Log.e("DEV", "send to " + port + "," + bs.length + "," + bs[0] + "," + bs[1]);
					toSocket.send(toPacket);
				}
				catch(IOException ie) {
					ie.printStackTrace();
				}
			}
		}
		catch(SocketException se) {
			se.printStackTrace();
		}
		finally {
			if(toSocket != null) {
				try {
					toSocket.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void sendBroadcase(byte[] bs, int len, int port,String ip) throws IOException {
		sendBroadcase(bs, len, port, (short)1,ip);
	}
	
	/**
	 * 普通数据的获取或设置操作,对普通数据库操作的入口
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param GetOrSet:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该参数只可读
	 * @author
	 * @return result:操作数据库的结果
	 */
	public static synchronized String dataBaseGetOrSet(Context mcontext,
			boolean GetOrSet, String name, String value) {
		String result = "";
		if (GetOrSet) {
			result = Settings.System.getString(mcontext.getContentResolver(),name);
		} else {
			if (value != null) {
				Settings.System.putString(mcontext.getContentResolver(), name,value);
			}
		}
		return result;
	}
	
	/**
	 * 获取终端的系列号(STBID)
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该参数只可读
	 * @author
	 * @return STBID:一般为32位，后12为MAC地址
	 */
	public String Process_STBID(Context mcontext, Boolean action, String name,
			String value) {
		String STBID = null;
		if (action) {
			//STBID = SystemProperties.get("ro.product.stb.stbid");
			STBID = SystemProperties.get("ro.serialno") + 
					SystemProperties.get("ro.mac").replaceAll(":", "");
			LogUtils.d("the stbid is " + STBID);
			if(null == STBID || "".equals(STBID)){
				STBID = getMacAddress();
			}
		} else {
			//ready only
			/*if (value.length() == 32) {
				if (Config.mDevInfoManager != null) {
					Config.mDevInfoManager.update("STB_SN", value, Config.devVersion);
				}
			}*/
		}
		return STBID;
	}
	
	/**
	 * 获取终端的MAC地址
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该参数只可读
	 * @author
	 * @return MACAddress:为12位
	 */
	public String Process_MACAddress(Context mcontext, Boolean action, String name, String value) {
		String MACAddress = "";
		if (action) {
			MACAddress = getMacAddress();
		} else {
			// read only
		}
		return MACAddress;
	}
	
	/**
	 * 获取终端设备MAC地址的接口
	 * @author
	 * @return mac:设备的MAC地址
	 */
	public String getMacAddress() {
		String mac = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("/sys/class/net/eth0/address")));
			mac = reader.readLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(mac)) {
			return mac;
		}else{
			return "";
		}
	}
	
	/**
	 * 获取终端设备的硬件版本号
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值，该节点应该是只读的
	 * @author
	 * @return HardwareVersion:硬件版本号
	 */
	public static String Process_HardwareVersion(Context mcontext, Boolean action, String name,
			String value) {
		String HardwareVersion = "";
		HardwareVersion = "0x441";
/*		if (action) {
			try {
				String spStr[] = Build.DISPLAY.split("_");
				HardwareVersion = spStr[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
*/		return HardwareVersion;
	}
	
	/**
	 * 获取终端设备的软件版本号
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值，该节点应该是只读的
	 * @author
	 * @return SoftwareVersion:软件版本号
	 */
	public static String Process_SoftwareVersion(Context mcontext, Boolean action, String name,String value) {
		String SoftwareVersion = "";
		SoftwareVersion = "0x6010";
/*		if (action) {
			try {
				String spStr[] = Build.DISPLAY.split("_");
				SoftwareVersion = spStr[2];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LogUtils.d("the softWareVersion is " + SoftwareVersion);
*/		return SoftwareVersion;
	}

	/**
	 * 获取终端设备CPU的频率
	 * @author
	 * @return cpuRate:CPU的速率
	 */
	public float getProcessCpuRate() {
		float totalCpuTime1 = getTotalCpuTime();
		float processCpuTime1 = getAppCpuTime();
		try {
			Thread.sleep(360);

		} catch (Exception e) {
			e.printStackTrace();
		}

		float totalCpuTime2 = getTotalCpuTime();
		float processCpuTime2 = getAppCpuTime();

		float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
				/ (totalCpuTime2 - totalCpuTime1);

		return cpuRate;
	}
	
	
	/**
	 * 获取终端设备CPU使用的总时间
	 * @author
	 * @return totalCpu:CPU使用的总时间
	 */
	public static long getTotalCpuTime() { // 获取系统总CPU使用时间
		String[] cpuInfos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long totalCpu = Long.parseLong(cpuInfos[2])
				+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
				+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
				+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		return totalCpu;
	}
	
	/**
	 * 获取应用占用的CPU时间
	 * @author
	 * @return appCpuTime:应用占用的CPU时间
	 */
	public static long getAppCpuTime() { // 获取应用占用的CPU时间
		String[] cpuInfos = null;
		try {
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		return appCpuTime;
	}
	
	/**
	 * 终端设备本地时间的格式化
	 * @param offset:
	 * @author
	 * @return sb:格式化后的时间
	 */
	public static String getOffsetTimeFormatString(int offset) {
		StringBuilder sb = new StringBuilder();
		if (offset >= 0) {
			sb.append('+');
		} else {
			sb.append('-');
			offset = -offset;
		}
		int offsetHour = offset / 1000 / 60 / 60;
		if (offsetHour < 10) {
			sb.append("0" + offsetHour);
		} else {
			sb.append(offsetHour);
		}
		sb.append(':');
		int offsetMin = offset / 1000 / 60 % 60;
		if (offsetMin < 10) {
			sb.append("0" + offsetMin);
		} else {
			sb.append(offsetMin);
		}
		return sb.toString();
	}
	
	/**
	 * 前端对终端网络设置相关，操作DHCPPassword，从属于ServiceInfo类，对接过北京联通的，没有DHCP模式，联通关于网络设置从属于AuthServiceInfo类
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，DHCPPassword
	 */
	protected synchronized String Process_DHCPPassword(Context mcontext,
			Boolean action, String name, String value) {
		String dhcpPassword = "";
		if(action){
			try {
				dhcpPassword = Config.HisiSettingService.getValue(Config.DHCPUserName, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				Config.HisiSettingService.setValue(Config.DHCPUserName, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return dhcpPassword;
	}

	/**
	 * 前端对终端网络设置相关，操作DHCPID,从属于ServiceInfo类，对接过北京联通的，没有DHCP模式，联通关于网络设置从属于AuthServiceInfo类
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，DHCPID
	 */
	protected synchronized String Process_DHCPID(Context mcontext, Boolean action,
			String name, String value) {
		String dhcpID = "";
		if(action){
			try {
				dhcpID = Config.HisiSettingService.getValue(Config.DHCPPassword, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				Config.HisiSettingService.setValue(Config.DHCPPassword, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dhcpID;
	}
	
	/**
	 * 前端对终端网络设置相关，操作PPPoEPassword，从属于ServiceInfo类，联通从属于AuthServiceInfo类
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，PPPoEPassword
	 */
	protected String Process_PPPoEPassword(Context context, Boolean action, String name,
			String value) {
		String pppoePassWord = "";
		if(action){
			try {
				pppoePassWord = Config.HisiSettingService.getValue(Config.PPPOEPassword, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				Config.HisiSettingService.setValue(Config.PPPOEPassword, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pppoePassWord;
	}
	
	
	/**
	 * 前端对终端网络设置相关，操作pppoeID，从属于ServiceInfo类，联通从属于AuthServiceInfo类
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，pppoeID
	 */
	protected String Process_PPPoEID(Context context, Boolean action, String name, String value) {
		String pppoeID = null;
		if(action){
			try {
				pppoeID = Config.HisiSettingService.getValue(Config.PPPOEUserName, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				Config.HisiSettingService.setValue(Config.PPPOEUserName, value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pppoeID;
	}
	
	/**
	 * 对iptv业务认证用户名的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 如果不存在零配置的前端一般会需要初始化这个节点，如果需要零配置的前端，这个节点值一般由前端下发(这里描述的是一般情况).
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，iptv的用户名
	 */
	protected String Process_AuthUserID(Context context, Boolean action, String name, String value) {
		String iptvUserID = "";
		String[] nameArray = name.split("[.]");
		if (action) {
			LogUtils.d("the userId is >>>" + name);
			if (Config.mDevInfoManager != null) {
				if(nameArray[3].equalsIgnoreCase("UserID")){
					iptvUserID = Config.mDevInfoManager.getValue(Config.IPTVUSERID);
				}else {
					iptvUserID = Config.mDevInfoManager.getValue(Config.IPTVUSERID2);
				}
			}
		} else {
			if (Config.mDevInfoManager != null) {
				if(nameArray[3].equalsIgnoreCase("UserID")){
					Config.mDevInfoManager.update(Config.IPTVUSERID,value,Config.devVersion);
				}else{
					Config.mDevInfoManager.update(Config.IPTVUSERID2,value,Config.devVersion);

				}
			}

		}
		return iptvUserID;
	}
	
	/**
	 * 对iptv业务认证用户名密码的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 如果不存在零配置的前端一般会需要初始化这个节点，如果需要零配置的前端，这个节点值一般由前端下发(这里描述的是一般情况).
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，iptv的用户名密码
	 */
	protected String Process_AuthUserIDPassword(Context context,Boolean action, String name,String value) {
		String iptvPassword = "";
		if (action) {
			if (Config.mDevInfoManager != null) {
				if(name.equalsIgnoreCase("UserIDPassword")){
					iptvPassword = Config.mDevInfoManager.getValue(Config.IPTVUSERPASSWORD);					
				}else{
					iptvPassword = Config.mDevInfoManager.getValue(Config.IPTVUSERPASSWORD2);
				}
			}
		} else {
			if (Config.mDevInfoManager != null) {
				if(name.equalsIgnoreCase("UserIDPassword")){
					Config.mDevInfoManager.update(Config.IPTVUSERPASSWORD, value,Config.devVersion);
				}else{
					Config.mDevInfoManager.update(Config.IPTVUSERPASSWORD2, value,Config.devVersion);	
				}
			}

		}
		return iptvPassword;
	}
	
	/**
	 * 对iptv业务认证地址的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 如果不存在零配置的前端一般会需要初始化这个节点，如果需要零配置的前端，这个节点值一般由前端下发(这里描述的是一般情况).
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，iptv的认证地址
	 */
	protected String Process_AuthURL(Context mcontext, Boolean action, String name, String value) {
		String IPTVAUTHURL = null;
		if (action) {
			if (Config.mDevInfoManager != null) {
				IPTVAUTHURL = Config.mDevInfoManager.getValue(Config.IPTVAUTHURL);
			}
		} else {
			if (Config.mDevInfoManager != null) {
				Config.mDevInfoManager.update(Config.IPTVAUTHURL, value, Config.devVersion);
			}
		}
		return IPTVAUTHURL;
	}
	
	/**
	 * 对iptv业务认证备用地址的操作，这个节点的值存在关键数据区/flashdata/dbdevinfodb数据库中的devinfodb表里面
	 * 如果不存在零配置的前端一般会需要初始化这个节点，如果需要零配置的前端，这个节点值一般由前端下发(这里描述的是一般情况).
	 * @param mcontext:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，iptv的认证地址
	 */
	protected String Process_AuthURLBackup(Context mcontext, Boolean action, String name, String value) {
		String IPTVAUTHURLBAKUP = null;
		if (action) {
			if (Config.mDevInfoManager != null) {
				IPTVAUTHURLBAKUP = Config.mDevInfoManager.getValue(Config.IPTVAUTHURLBAKUP);
			}
		} else {
			if (Config.mDevInfoManager != null) {
				Config.mDevInfoManager.update(Config.IPTVAUTHURLBAKUP, value, Config.devVersion);
			}

		}
		return IPTVAUTHURLBAKUP;
	}
	
	/**
	 * 获取终端设备当前可选语种
	 * @author
	 * @return String类型的值，终端设备当前可选的语种
	 */
	protected String getAvailableLanguageFormatString() {
		Locale[] locales = Locale.getAvailableLocales();
		ArrayList<String> languages = new ArrayList<String>();
		String language = null;
		for (Locale locale : locales) {
			language = locale.getLanguage();
			if (!languages.contains(language)) {
				languages.add(language);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String str : languages) {
			sb.append(str).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 获取终端设备自开机运行的毫秒数
	 * @author
	 * @return durationTime，秒数
	 */
	protected String getUpTime() {
		// TODO Auto-generated method stub
		long duration = SystemClock.uptimeMillis();
		long durationTime = duration/1000;
		return durationTime+"";
	}
	
	/**
	 * 获取设备内存的总大小
	 * @author
	 * @return long型：设备SD卡的总大小
	 */
	private long getSDCardStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long totalStorage = blockSize * blockCount / 1024;
			//long availCount = sf.getAvailableBlocks();
			//long availStorage = blockSize * availCount / 1024;
			return totalStorage;
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取设备内存的总大小
	 * @author
	 * @return long型：设备内存的总大小
	 */
	private long getSystemTotalStorage() {
		try {
			File root = Environment.getRootDirectory();
			StatFs sf = new StatFs(root.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long totalStorage = blockSize * blockCount / 1024;
			//long availCount = sf.getAvailableBlocks();
			//long availStorage = blockSize * availCount / 1024;
			return totalStorage;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}
	
	/**
	 * 获取设备总内存的大小
	 * @author
	 * @return String类型的值
	 */
	protected String getMemoryStatusTotalSize() {
		long sdcardtotal = getSDCardStorage();
		long systemtotal = getSystemTotalStorage();
		String total = String.valueOf(sdcardtotal + systemtotal);
		return total;
	}
	

	 /**
	 * 获取设备SD卡可用存储
	 * @author
	 * @return long类型，设备SD卡的可用存储
	 */
	private long getSDCardAvailableStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			//long blockCount = sf.getBlockCount();
			//long totalStorage = blockSize * blockCount / 1024;
			long availStorage = blockSize * availCount / 1024;
			return availStorage;
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取设备的可用存储
	 * @author
	 * @return long类型，设备的有效存储
	 */
	private long getSystemAvailableStorage() {
		try {
			File root = Environment.getRootDirectory();
			StatFs sf = new StatFs(root.getPath());
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			//long blockCount = sf.getBlockCount();
			//long totalStorage = blockSize * blockCount / 1024;
			long availStorage = blockSize * availCount / 1024;
			return availStorage;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}
	
	/**
	 * 获取设备可用内存大小
	 * @author
	 * @return String类型：设备的可用内存大小
	 */
	protected String getMemoryStatusFreeSize() {
		long sdcardava = getSDCardAvailableStorage();
		long systemava = getSystemAvailableStorage();
		String ava = String.valueOf(sdcardava + systemava);
		return ava;
	}
	
	protected String getMemoryStatusRate() {
		long sdcardava = getSDCardAvailableStorage();
		long systemava = getSystemAvailableStorage();
		long sdcardtotal = getSDCardStorage();
		long systemtotal = getSystemTotalStorage();
		float memoryrate_free = (float)(sdcardava+systemava) / (float)(sdcardtotal+systemtotal);	
		float memoryrate = (1 - memoryrate_free)*100;
		return String.valueOf(memoryrate)+"%";	
	}

	/**
	 * 获取终端设备的RAM大小，对应节点为PhyMemSize，单位为kB
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该节点为只读，action只能为get
	 * @author
	 * @return String类型的值，终端设备的RAM大小
	 */
	protected String Process_PhyMemSize(Context context, Boolean action, String name, String value) {
		String PhyMemSize = null;
		if (action) {
			PhyMemSize = getTotalMemory();
		}

		return PhyMemSize;

	}
	
	/**
	 * 获取设备的RAM大小，单位为kB
	 * @author
	 * @return String类型的值，终端设备的RAM大小
	 */
	public String getTotalMemory() {
		String str2 = "";
		String[] arrayOfString = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"));
			str2 = bufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			if (arrayOfString != null && arrayOfString.length > 1) {
				return arrayOfString[1];
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	/**
	 * 获取终端设备的存储大小,对应的节点为StorageSize.单位为kB
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该节点为只读，action只能为get
	 * @author
	 * @return String类型的值，终端设备的存储大小
	 */
	public String Process_StorageSize(Context context, Boolean action, String name, String value) {
		String StorageSize = null;
		if (action) {
			StorageSize = String.valueOf(getSDCardStorage());
		}
		return StorageSize;
	}
	
	/**
	 * 获取系统时间接口，
	 * @param strParam,String类型，时间的输出格式，如："yyyy:MM:dd:HH:mm:ss"或"yyyy-MM-dd-HH-mm-ss"
	 * @param duration,long型，指点具体时间进行格式化,如只是需要格式化当前的系统时间，传0进去即可
	 * @author 
	 * @return String类型，格式化后的时间串
	 */
	public static String getCurrentTime(String strParam, long duration) {
		String str = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(strParam);
			java.util.Date curDate = new java.util.Date(System.currentTimeMillis()+duration);
			LogUtils.d("the current time is >>> " + System.currentTimeMillis());
			str = format.format(curDate);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}

	}
    
	public static void saveCurrentTime(String name) {
		String str = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			java.util.Date curDate = new java.util.Date(System.currentTimeMillis());
			LogUtils.d("the current time is >>> " + System.currentTimeMillis());
			str = format.format(curDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Config.mDevInfoManager.update(name, str, Config.devVersion);                
	}

	public static String getTransforeTime(String name) {
		return Config.mDevInfoManager.getValue(name);                
	}
	
	/**
	 * 工具类关闭流
	 * @param io 可变参数
	 * @author 
	 */
	public static void close(Closeable ... io){
		for (Closeable temp : io) {
			try {
				if (null != temp) {
					temp.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 实现文件拷贝的接口
	 * @param srcFileName:源文件
	 * @param targetFileName:目标路径
	 * @author
	 * @return
	 */
	public static boolean copyFile(String srcFileName, String targetFileName) {
		InputStream inStream = null;
		LogUtils.d("copy file in!!!");
		try {
			inStream = new FileInputStream(srcFileName);
		} catch (FileNotFoundException e) {
			LogUtils.d("srcFileName read failed" + srcFileName + "" + e);
			return false;
		}
		File targetFile = new File(targetFileName);
		OutputStream outStream = null;
		try {
			targetFile.createNewFile();
			LogUtils.d("create file end !!!");
			outStream = new FileOutputStream(targetFile);
			LogUtils.d("create outStream !!!");
			byte[] by = new byte[1024];
			while (inStream.available() > 0) {
				inStream.read(by);
				outStream.write(by);
			}
			Tr069Service.TR069ServiceSendMsg(3,"TRUE");
			LogUtils.d("copy success!!!");
		} catch (IOException e) {
			LogUtils.d("targetFileName create failed" + targetFileName+""+e);
			return false;
		} finally {
			if (null != inStream) {
				try {
					inStream.close();
				} catch (IOException e) {
					LogUtils.d("inStream close failed >>> " + e);
				}
			}
			if (null != outStream) {
				try {
					outStream.flush();
				} catch (IOException e) {
					LogUtils.d("outStream flush failed>>> " + e);
				}
				try {
					outStream.close();
				} catch (IOException e) {
					LogUtils.d("outStream close failed>>> " + e);
				}
			}
		}
		return true;
	}
	
	/**
	 * 文件删除接口
	 * @param srcFileName:需要删除的文件名
	 * @author
	 */
	public static void rmFile(String srcFileName) {
		File file = new File(srcFileName);
		if (file.exists()) {
			file.delete();
			LogUtils.i("file delete ok >>> ");
		} else {
			LogUtils.i("file not exit >>> ");
		}
	}
	
	/**
	 * 改变文件权限的接口，只是改变本应用对指定文件的权限
	 * @param srcFileName:需要改变权限的文件
	 * @author
	 */
	public static void changePerssionFile(String srcFileName){
		File file = new File(srcFileName);
		if(file.exists()){
			file.setWritable(true);
			file.setReadable(true);
			file.setExecutable(true);
			LogUtils.d("the file permission is >>> " + file.canExecute() + " >> " + file.canRead() + " >> "+ file.canWrite());
		}else{
			LogUtils.d("file is not exit or permisson is refused");
		}
	}
	
	/**
	 * 改变文件的执行权限，对所有应用
	 * @param srcFileName:需要改变的文件名
	 * @author
	 */
	public static void chmodFile(String srcFileName){
		File file = new File(srcFileName);
		if(file.exists()){
			String command = "chmod 777 " + file;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogUtils.d("chmod file execption >>" + e);
				e.printStackTrace();
			}
			LogUtils.d("the file permission is >>> " + file.canExecute() + " >> " + file.canRead() + " >> "+ file.canWrite());
		}else{
			LogUtils.d("file is not exit or permisson is refused");
		}
	}
}
