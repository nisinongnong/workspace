package com.jzbyapp.tr069service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.android.smart.terminal.iptv.aidl.EthernetDevInfo;
import com.jzbyapp.tr069service.basedata.LAN;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;
import com.jzbyapp.tr069service.upgrade.AnalyzeUtil;
import com.jzbyapp.tr069service.upgrade.UpgradeInfo;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;

public class ParseThread extends Thread {
	public static ParseThread aParseThread;
	private boolean UpgradeListChangedFlag = false;
	private boolean UpgradebootPicListChangedFlag = false;
	private boolean UploadListChangedFlag = false;
	private boolean NetworkListChangedFlag = false;
	public static int SocketThreads = 0;
	Socket socket;
	Context mContext;
	private Tr069_Process TR069_PROCESS;
	public static PowerManager pm = null;
	public static WakeLock wakeLock = null;

	public ParseThread(Context context, Socket s) {
		mContext = context;
		TR069_PROCESS = new Tr069_Process(context);
		SocketThreads++;
		this.socket = s;
	}

	public ParseThread(Context context) {
		this(context,null);
	}

	public static ParseThread getInstance(Context contex) {
		if (aParseThread == null) {
			aParseThread = new ParseThread(contex);
		}
		return aParseThread;
	}

	@Override
	public void run() {
		super.run();
		Looper.prepare();
		InputStream is = null;
		OutputStream os = null;
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			isr = new InputStreamReader(is, "UTF-8");
			osw = new OutputStreamWriter(os, "UTF-8");
			br = new BufferedReader(isr);
			bw = new BufferedWriter(osw);

			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				LogUtils.d("tr069service is received before Process >>>" + line);
				String result = Process(line);
				LogUtils.d("tr069service is received >> " + result + " ,the line is >>>" + line);
				bw.write(result);
				bw.newLine();
				bw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
					SocketThreads--;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	List<Map<String, String>> setIPList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> setUpgradeList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> setUploadList = new ArrayList<Map<String, String>>();
	static List<Map<String, String>> setNetworkList = new ArrayList<Map<String, String>>();

	List<Map<String, String>> setIPTVList = new ArrayList<Map<String, String>>();
	List<Map<String, String>> IPTVLists = new ArrayList<Map<String, String>>();
	List<Map<String, String>> setWifiList = new ArrayList<Map<String, String>>();

	public String Process(String cmd) {
		LogUtils.d("the Process is in >> " + cmd);
		String result = "";
		String parameter = "";
		String[] array = { "" };
		try {
			array = cmd.split("\\$");
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.d("process exception is >>>" + e);
			result = "fail";
			return result;
		}
		if (array.length > 1)
			LogUtils.d("the parsethread array length is >>> " + array.length + " >>> " + array[0] + " >>> " + array[1]);
		else if (1 == array.length)
			LogUtils.d("the parsethread array length is >>> " + array.length + " >>> " + array[0]);			
			
		if (Config.KEY_GET.equals(array[0])) {
			LogUtils.v("the get is in >>> ");
			result = dealWithKeyGetOrSet(Config.GETACTION, parameter, array);
		}else if (Config.KEY_SET.equals(array[0])) {
			result = dealWithKeyGetOrSet(Config.SETACTION, parameter, array);
		} else if (Config.KEY_COMMIT.equals(array[0])) {
			result = dealWithKeyCommit(parameter, array);
		} else if (Config.KEY_RESET.equals(array[0])) {
			dealWithSystemCommand(Config.messageQueue.MESSAGE_FACTORY);
		} else if (Config.KEY_REBOOT.equals(array[0])) {
			dealWithSystemCommand(Config.messageQueue.MESSAGE_REBOOT);
		} else if (Config.KEY_UPGRADE.equals(array[0])) {	
			result = dealWithBatchData(Config.KEY_UPGRADE, parameter, array);	
		} else if (Config.KEY_UPLOAD.equals(array[0])){
			result = dealWithBatchData(Config.KEY_UPLOAD, parameter, array);
		} else if(Config.KEY_WAKELOCK.equals(array[0])) {
			result = dealWithKeyWakeLock();
		} else if(Config.KEY_ADDDNODE.equals(array[0])) {
			result = dealWithKeyAddNode(array);
		} else if (Config.KEY_PUT.equals(array[0])) {
			result = dealWithKeyPut(array);
		} else if (Config.KEY_LOCAL_UPGRADE.equals(array[0])) {
			dealWithKeyLocalUpgrade();
		} else if(Config.KEY_LOGCATSTART.equals(array[0])){
			dealWithSystemCommand(Config.messageQueue.MESSAGE_LOGCATSTART);
		} else if(Config.KEY_LOGCATSTOP.equals(array[0])){
			dealWithSystemCommand(Config.messageQueue.MESSAGE_LOGCATSTOP);
		} 
		return result;
	}
	

	/**
	 * 待机锁的流程为：当遥控器按下待机键后，系统会发出ACTION_SCREEN_OFF广播，接收到广播（见BroadcastListener类）设备理应立马待机。
	 * 但由于终端待机了将停止一切通讯，所以我们要先占有待机锁不让待机（见tr069service类中initService方法中先占住待机锁），
	 * 让我们在接受到待机广播后处理完事件上报完状态给前端后再释放该锁，设备实现待机。
	 * @return String
	 */
	private String dealWithKeyWakeLock() {
		// TODO Auto-generated method stub
		Config.wakeLock.release();
		return "success";
	}
	
	/**
	 * batch data processing,if use upgrade/upload/put and so on,must be commint to make effect
	 * 批量数据处理，如果使用了upgrade/upload/put等，最后必须commit才能生效。
	 * @param  result
	 * @param  array:数组类型，由节点分割出的数组
	 * @author
	 */
	private String dealWithKeyCommit(String result, String[] array) {
		if (UpgradeListChangedFlag) {
			LogUtils.d("the UpgradeListChangedFlag >>> " + UpgradeListChangedFlag);
			result = SystemUpgrade();
		}else if(UploadListChangedFlag){
			result = SystemUpload();
		}else if(UpgradebootPicListChangedFlag){
			LogUtils.d("the UpgradebootPicListChangedFlag >>> " + UpgradebootPicListChangedFlag);
			result = bootPicUpgrade();
		}else if(NetworkListChangedFlag){
			LogUtils.i("the NetworkListChangedFlag >>> " + NetworkListChangedFlag);
			result = RefreshNetwork();
		}
		
		return result;
	}

	
	/**
	 * 创建日志文件的内容，按照北京联通规范要求来添加的内容。
	 * 一般情况下电信和联通的要求差不多，要求终端采集相应的节点值以文件的形式上报到前端，区别是日志要求上报的节点不一致；
	 * 移动的日志上文件上传比较复杂没有见过其要求的实现细节，该接口对接电信和联通只需修改Utils.dataBaseGetOrSet()
	 * 获取的节点名即可实现通用。
	 * @param filename:String类型
	 * @return String
	 */
	private String getLogContents(String filename) {
		// TODO Auto-generated method stub
		LogUtils.d("the getLogContents() is in >>> "); 
		String result = "";
		String value = "";
		FileWriter writer = null;
		//get /system/app/tr069Service_std.apk/
		//String fileDir = mContext.getApplicationContext().getPackageResourcePath() + "/files/" + filename;
		//get /data/data/com.jzbyapp.tr069service/files/
		String fileDir = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + filename;
		int length = Config.contents.length;
		LogUtils.d("the log file node length is >>>" + length + "the log file name is >>>" + fileDir);
		try {
			writer = new FileWriter(fileDir, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i<Config.contents.length-1; i++){
			result = Utils.dataBaseGetOrSet(mContext, Config.GETACTION, Config.contents[i][1], value);
			String temp = Config.contents[i][0]+" , "+result;
			try {
				writer.write(temp);
				writer.write("\r\n");
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileDir;
	}
	
	/**
	 * 创建需要上传的日志文件的名称，这个名称按照北京联通的规范要求的格式来创建的，
	 * 一般情况下电信和联通的要求格式差不多，该接口对接这两家已经可以统一，只需安照规范修改日志文件名的拼接方式。
	 * @param duration,文件采集持续时间
	 * @return String类型
	 */
	private String createLogFile(int duration) {
		// TODO Auto-generated method stub
		LogUtils.i("the createLogFile() is in >>> "); 
		String stbId = SystemProperties.get("ro.serialno") + 
			SystemProperties.get("ro.mac").replaceAll(":", "");

		String ipaddress = null;
		String qosVersion = "1_0";
		EthernetDevInfo ethernetDevInfo = null;
		try {
			if(Config.HisiSettingService != null){
				ethernetDevInfo = Config.HisiSettingService.getEthernetDevInfo();				
			}
			LogUtils.d("the craeteLogFile ethernetDevInfo is in");
			if(ethernetDevInfo != null){
				LogUtils.d("the craeteLogFile ethernetDevInfo is " + ethernetDevInfo);
				ipaddress = ethernetDevInfo.ipaddr;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String beginTime = Utils.getCurrentTime("yyyy:MM:dd:HH:mm:ss", 0);
		String endTime = Utils.getCurrentTime("yyyy:MM:dd:HH:mm:ss", duration*1000);
		String fileName = stbId + "_" + ipaddress + "_" + qosVersion + "_" + beginTime + "_" + endTime + ".csv";
		LogUtils.d("the filename is >>>" + fileName);
		return fileName;
	}
	
	/**
	 * 日志文件的上传，支持http和ftp服务器类型
	 * @return
	 */
	public String SystemUpload() {
		// TODO Auto-generated method stub
		LogUtils.i("SystemUpload() is in >>> "); 
		String UploadUsrname = null;
        String UploadPwd = null;
        String UploadFileURL = null;
        String DelaySeconds = null;
        String UploadType = null;
        List<Map<String, String>> commitlist = new ArrayList<Map<String, String>>();
        commitlist = setUploadList;
		
		for (int i = 0; i < commitlist.size(); i++) {
			Map<String, String> getcmd = commitlist.get(i);

			String key = getcmd.get("key");
			String value = getcmd.get("value");

			if ("UploadUsrname".equals(key)) {
				UploadUsrname = value;
			} else if ("UploadPwd".equals(key)) {
				UploadPwd = value;
			} else if ("UploadFileURL".equals(key)) {
				UploadFileURL = value;
			} else if ("UploadSeconds".equals(key)) {
				DelaySeconds = value;
			} else if ("UploadType".equals(key)) {
				UploadType = value;
			}
		}
		LogUtils.i("the UploadUsrname is >>> " + UploadUsrname + "pwd is >>> " + UploadPwd + "UploadFileURL is >>> " + UploadFileURL +
				"DelaySeconds is >>> " + DelaySeconds + "the uploadType is >>> " + UploadType);
		int durationTime = Integer.parseInt(DelaySeconds);
		String logFileName = createLogFile(durationTime);
		String fileDir = getLogContents(logFileName);
		
		LogUtils.d("the message MESSAGE_UPLOAD is send ");
		Message msg = Message.obtain();
		msg.what = Config.messageQueue.MESSAGE_UPLOAD;
		Bundle bundle = new Bundle();
		bundle.putString("url", UploadFileURL);
		bundle.putString("patch", fileDir);
		bundle.putString("password", UploadPwd);
		bundle.putString("username", UploadUsrname);
		bundle.putString("filetype", UploadType);
		
		String str[] = UploadFileURL.split("://");
		if("http".equals(str[0])){
			bundle.putString("type", "http");
		}else if("ftp".equals(str[0])){
			bundle.putString("type", "ftp");
		}
		msg.setData(bundle);
		Config.messageQueue.sendMessage(msg);
		UploadListChangedFlag = false;
		setUploadList.clear();
		 
		return null;
	}

	/**
	 * upload一般情况是前端需要终端上传文件之类的，比如日志文件。会下发upload时间。提示：针对不同的平台有可能下发不一样的事件
	 * upgrade对应前端下发升级事件，由C协议层作了转义成对应的upgrade
	 * @param  keyType:boolean类型，action的类型，一般由upload/upgrade/put的（批量数据commit前的一步动作）。
	 * @param  result:String类型
	 * @param  array:由节点分割出来的数组类型
	 * @author 
	 * @return  result
	 */
	private String dealWithBatchData(String keyType, String result, String[] array) {
		// TODO Auto-generated method stub
		if(array.length == 3){
			Map<String, String> setcmd = new HashMap<String, String>();
			setcmd.put("key", array[1]);
			setcmd.put("value", array[2]);
			if(Config.KEY_UPGRADE.equals(keyType)){
				if("UpgradeFileURL".equals(array[1])){
					TR069_PROCESS.tr069SetValue("Device.ManagementServer.UpgradeURL", array[2]);
				}
				if("FileType".equals(array[1])){//just for suit bjlt bootpic and system upgrade
					if(array[2].equals("4 CU downloadPic")){
						LogUtils.d("the pic flag is set >>>");
						UpgradebootPicListChangedFlag = true;
					}else if(array[2].equals("1 Firmware Upgrade Image")){
						UpgradeListChangedFlag = true;
					}
				}
				setUpgradeList.add(setcmd);
				result = "wait";
			}else if(Config.KEY_UPLOAD.equals(keyType)){
				setUploadList.add(setcmd);
				UploadListChangedFlag = true;
				LogUtils.d("parseThread is KEY_UPLoad is in >>> " + UploadListChangedFlag);
				result = "wait";
			}
		}else {
			result = "fail";
		}
		return result;
	}
	
	/**
	 * 对于前端下发涉及到系统级的命令，如重启/恢复出厂设置等，在此不执行任何操作只作消息转发，统一由Messagequeue来处理
	 * @param  commandType:系统级的命令类型（都通过了转义，如Reboot--KEY_REBOOT）
	 * @author 
	 */
	private void dealWithSystemCommand(int commandType) {
		Message msg = Message.obtain();
		msg.what = commandType;
		LogUtils.d("sendMessage type is >>> " + commandType);
		Config.messageQueue.sendMessage(msg);
	}

	private void dealWithKeyLocalUpgrade() {
		String filepath = "/cache/update.zip";
		LogUtils.i("system upgrade >>> LocalUpgrade " + filepath);
		try {
			Config.HisiSettingService.installUpgrade("Upgrade",
					filepath, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	private String dealWithKeyPut(String[] array) {
		LogUtils.d("dealWithKeyPut length is >>> " + array.length + "array[1] = " + array[1]);
		Map<String, String> setcmd = new HashMap<String, String>();

		setcmd.put("key", array[1]);
		setcmd.put("value", array[2]);
		setNetworkList.add(setcmd);
		NetworkListChangedFlag = true;
		return "wait";
	}
	
        private String dealWithKeyAddNode(String[] array) {
            int index;
            String rst;
            LogUtils.d("dealWithKeyAddNode  array.length == " + array.length);
            if (3 == array.length)
            {
                /* waiting for complete, now just for test */
                index = Config.dynamicnode.AddDynamicNode(array[1]);
                Config.dynamicnode.SetDynamicNode(array[1] +"." + index, array[2]);
                rst = Config.dynamicnode.GetDynamicNode(array[1] + "." + index);
                rst = array[1] + "." + index + " == " + rst;
                return rst;
            }
            else
                return "failed";
        }
	/**
	 * 单个数据的获取，从此进入节点的具体解析找到对应的类，联通的在.unicom包里面，移动的在.mobile包里面，电信的在.telecom包里面。
	 * @param  action:boolean类型，GETACTION为true,通过程序去获取数据
	 * @param  result:
	 * @param  array:由节点分割出的数组类型
	 * @author 
	 */
	private String dealWithKeyGetOrSet(boolean action, String result, String[] array){
		LogUtils.d("the array length is >>> " + array.length + "the action is >>> " + action + "array[1] = " + array[1]);
		if(action){
			if(array.length == 1){
				result = "fail";
			}else{
				result = TR069_PROCESS.tr069GetValue(array[1]);
			}
		}else{
			if (array.length == 3) {
				TR069_PROCESS.tr069SetValue(array[1].toString().trim(),array[2].toString().trim());
				result = "success";
			} else if (array.length == 2) {
				TR069_PROCESS.tr069SetValue(array[1].toString().trim(), "");
				result = "success";
			} else{
				result = "fail";
			}
		}
		
		return result;
		
	}

	private String RefreshNetwork() {
		String result = "success";
		LAN mLAN = LAN.GetInstance(mContext);
		List<Map<String, String>> commitlist = new ArrayList<Map<String, String>>();
		commitlist = setNetworkList;
		mLAN.ClearEthernetInfo();
		
		for (int i = 0; i < commitlist.size(); i++) {
			Map<String, String> getcmd = commitlist.get(i);

			String key = getcmd.get("key");
			String value = getcmd.get("value");
			LogUtils.d("RefreshNetwork i == " + i + ";key == " + key + ";value == " + value);

			if ("Device.LAN.IPAddress".equals(key)) {
				mLAN.ethinfo.ipaddr = value;
			} else if ("Device.LAN.DNSServers".equals(key)) {
				mLAN.ethinfo.dns = value;
			} else if ("Device.LAN.DefaultGateway".equals(key)) {
				mLAN.ethinfo.route = value;
			} else if ("Device.LAN.SubnetMask".equals(key)) {
				mLAN.ethinfo.mask = value;
			} else if ("Device.LAN.AddressingType".equals(key)) {
				mLAN.ethinfo.mode = value;
			}
		}
		
		mLAN.commitEthDevInfo();
		
		NetworkListChangedFlag = false;
		setNetworkList.clear();
		
		return result;
	}
	
	private String bootPicUpgrade() {
		String UpgradeUsrname = null;
		String UpgradePwd = null;
		String UpgradeForced = null;
		String UpgradeFileURL = null;
		String DelaySeconds = null;
		String TargetFileName = null;
		List<Map<String, String>> commitlist = new ArrayList<Map<String, String>>();
		commitlist = setUpgradeList;

		for (int i = 0; i < commitlist.size(); i++) {
			Map<String, String> getcmd = commitlist.get(i);

			String key = getcmd.get("key");
			String value = getcmd.get("value");

			if ("UpgradeUsrname".equals(key)) {
				UpgradeUsrname = value;
			} else if ("UpgradePwd".equals(key)) {
				UpgradePwd = value;
			} else if ("UpgradeForced".equals(key)) {
				UpgradeForced = value;
			} else if ("UpgradeFileURL".equals(key)) {
				UpgradeFileURL = value;
			} else if ("DelaySeconds".equals(key)) {
				DelaySeconds = value;
			} else if ("TargetFileName".equals(key)) {
				TargetFileName = value;
			}
		}
		
		 UpgradeInfo bootPicInfo = new UpgradeInfo();
		 bootPicInfo.UpgradeUsrname=UpgradeUsrname;
		 bootPicInfo.UpgradePwd=UpgradePwd;
		 bootPicInfo.UpgradeForced=UpgradeForced;
		 bootPicInfo.UpgradeFileURL=UpgradeFileURL;
		 bootPicInfo.DelaySeconds=DelaySeconds;
		 bootPicInfo.TargetFileName=TargetFileName;
		 //AnalyzeUtil.StartUpdateProcess(mContext, bootPicInfo);
		 Message msg = Message.obtain();
		 msg.what = MessageQueue.MESSAGE_UPGRADEBOOTPIC;

		Bundle data = new Bundle();
		data.putString("url", UpgradeFileURL);
		data.putString("patch", mContext.getString(R.string.VersionIniPath));
		data.putString("upgrade_pwd", UpgradePwd);
		data.putString("upgrade_usrname", UpgradeUsrname);
		LogUtils.d("StartUpdateProcess url == " + UpgradeFileURL);

		msg.setData(data);
		Config.messageQueue.sendMessage(msg);

		UpgradebootPicListChangedFlag = false;
		setUpgradeList.clear();
		return "success";
	}

	private String SystemUpgrade()
    {
        String UpgradeUsrname = null;
        String UpgradePwd = null;
        String UpgradeForced = null;
        String UpgradeFileURL = null;
        String DelaySeconds = null;
        String TargetFileName = null;
        List<Map<String, String>> commitlist = new ArrayList<Map<String, String>>();
        commitlist = setUpgradeList;
        
		for (int i = 0; i < commitlist.size(); i++) {
			Map<String, String> getcmd = commitlist.get(i);

			String key = getcmd.get("key");
			String value = getcmd.get("value");

			if ("UpgradeUsrname".equals(key)) {
				UpgradeUsrname = value;
			} else if ("UpgradePwd".equals(key)) {
				UpgradePwd = value;
			} else if ("UpgradeForced".equals(key)) {
				UpgradeForced = value;
			} else if ("UpgradeFileURL".equals(key)) {
				UpgradeFileURL = value;
			} else if ("DelaySeconds".equals(key)) {
				DelaySeconds = value;
			} else if ("TargetFileName".equals(key)) {
				TargetFileName = value;
			}
		}

        if (null != DelaySeconds)
			try {
				sleep(Integer.parseInt(DelaySeconds));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        UpgradeInfo info = new UpgradeInfo();
        info.UpgradeUsrname=UpgradeUsrname;
        info.UpgradePwd=UpgradePwd;
        info.UpgradeForced=UpgradeForced;
        info.UpgradeFileURL=UpgradeFileURL;
        info.DelaySeconds=DelaySeconds;
        info.TargetFileName=TargetFileName;
		AnalyzeUtil.getIni(mContext, info);
        //AnalyzeUtil.StartUpdateProcess(mContext, info);
        //AnalyzeUtil.getIni(mContext, uri, path, info, false);

        UpgradeListChangedFlag=false;
        setUpgradeList.clear();
        
        return "success";        
    }
}
