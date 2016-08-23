package com.jzbyapp.tr069service.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import com.jzbyapp.jzdownloadutil.JZDownload;
import com.jzbyapp.jzdownloadutil.OnDownloadListener;
import com.jzbyapp.tr069service.R;
import com.jzbyapp.tr069service.mesQueue.MessageQueue;
import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;

/**
 * 下载工具类，主要包含升级配置文件的下载:getIni(final Context , final UpgradeInfo);
 * 				   升级配置文件的解析:getAllData(String);
 * 				   升级包下载的触发:StartUpdateProcess(Context, UpgradeInfo);
 * 				   版本的校验:UpdateVersionCheck(Context);
 * @author
 */
public class AnalyzeUtil {
	static BufferedReader br;
	static Map<String, String> mMap = new HashMap<String, String>();
	static JZDownload download;
	
	/**
	 * 升级配置文件下载.在该接口里有一个下载回调函数，当下载工具下载完成后会回调onDownloadFinish(String,int)函数,
	 * 在该函数里面创建线程，线程里的主要人物是解析配置文件/版本校验，触发升级包的下载.
	 * @param mContext:描述程序的环境信息，即上下文
	 * @param info:升级信息的描述类
	 */
	public static void getIni(final Context mContext, final UpgradeInfo info) {
		String VersionIniUrl = info.UpgradeFileURL + "Version.ini";
		LogUtils.d("StartUpdateProcess line 30");
		download = new JZDownload(mContext, VersionIniUrl, mContext.getString(R.string.VersionIniFullPath));
		download.setRetryTimes(1);
		download.setOnDownloadListener(new OnDownloadListener() {
			@Override
			public void onDownloadProgress(String arg0, int arg1, int arg2) {
			}

			@Override
			public void onDownloadFinish(String arg0, int arg1) {
				if (arg1 == 0) {
					new Thread() {
						@Override
						public void run() {
							super.run();

							String VersionIniPath = mContext.getString(R.string.VersionIniFullPath);
							getAllData(VersionIniPath);
							if (UpdateVersionCheck(mContext)) {
								StartUpdateProcess(mContext, info);
							}
						}
					}.start();
				}
			}
		});
		download.setUseBroadcast();
		download.startDownload();
	}
	
	/**
	 * 对配置文件的解析
	 * @param filepath:配置文件的保存路径
	 * @author
	 */
	public static void getAllData(String filepath) {
		try {
			File file = new File(filepath);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			String valueString = null;
			while ((valueString = br.readLine()) != null) {
				String[] result = valueString.split("=");
				if (result != null && result.length == 2) {
					int index = result[1].length();
					result[1] = result[1].substring(0, index - 1);
					mMap.put(result[0], result[1]);
					LogUtils.d("the Version.ini >>>" +result[0] + " >>> " + result[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对升级包下载的触发
	 * @param mContext:描述程序的环境信息，即上下文
	 * @param info:升级信息的描述类
	 * @author
	 */
    public static void StartUpdateProcess(Context mContext, UpgradeInfo info) {
        String UpdateFile = (null == mMap.get("UpdateFile")) ? "update.zip" : mMap.get("UpdateFile");
        String SavePath = (null == mMap.get("UpdateFile")) ? mContext.getString(R.string.VersionIniPath) : mMap.get("SavePath");
        String UpgradeUsrname = info.UpgradeUsrname;
        String UpgradePwd = info.UpgradePwd;
        String UpgradeFileURL = info.UpgradeFileURL + UpdateFile;

        if (!SavePath.endsWith("/"))
        {
            SavePath = SavePath + "/";
        }

        Message msg = Message.obtain();
        msg.what = MessageQueue.MESSAGE_UPGRADE;

        Bundle data = new Bundle();
        data.putString("url", UpgradeFileURL);
        data.putString("patch", SavePath + UpdateFile);
        data.putString("upgrade_pwd", UpgradePwd);
        data.putString("upgrade_usrname", UpgradeUsrname);
        LogUtils.d("StartUpdateProcess >>> url == " + UpgradeFileURL + "; patch == " + SavePath + UpdateFile);
        LogUtils.d("StartUpdateProcess >>> upgrade_pwd == " + UpgradePwd + "; upgrade_usrname == " + UpgradeUsrname);

        msg.setData(data);
        Config.messageQueue.sendMessage(msg);
    }
    
    /**
     * 对升级包版本的校验
     * @param mContext:描述程序的环境信息，即上下文
     * @return CheckResult:boolean类型，版本校验结果，true为需要下载升级，false为不触发升级
     * @author
     */
    public static boolean UpdateVersionCheck(Context mContext) {
    	String curVersion = Utils.Process_SoftwareVersion(mContext, Config.GETACTION, "Device.DeviceInfo.SoftwareVersion", null);
    	String curHardVersion =  Utils.Process_HardwareVersion(mContext, Config.GETACTION, "Device.DeviceInfo.HardwareVersion", null);
        String OUI = Utils.dataBaseGetOrSet(mContext, Config.GETACTION, "Device.DeviceInfo.ManufacturerOUI", null);
    	String IsCheckIni = (null == mMap.get("IsCheckIni")) ?  "0" : mMap.get("IsCheckIni");
        LogUtils.d("UpdateVersionCheck >>> " + curVersion + " >> " + curHardVersion + " >> " + OUI + " >> " + IsCheckIni);
        boolean CheckResult = true;
        if (IsCheckIni.equals("1")) {
            if (null != mMap.get("Version"))
                CheckResult &= !curVersion.equals(mMap.get("Version"));
            if (null != mMap.get("Product_ModeID"))
                CheckResult &= Build.MODEL.equals(mMap.get("Product_ModeID"));
            if (null != mMap.get("HardwareID"))
                CheckResult &= curHardVersion.equals(mMap.get("HardwareID"));
            if (null != mMap.get("OUI"))
                CheckResult &= OUI.equals(mMap.get("OUI"));
        }
        LogUtils.d("UpdateVersionCheck >>>  " + CheckResult);
        return CheckResult;
    }
}
