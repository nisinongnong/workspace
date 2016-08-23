package com.jzbyapp.tr069service.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.NetWorkSpeedInfo;
import com.jzbyapp.utils.Utils;

/**
 * 华为扩展参数，STB宽带测试
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class BandwidthDiagnostics extends Utils {
	private static BandwidthDiagnostics mtr069BandwidthDiagnostics = null;
	private Context mContext = null;

	public BandwidthDiagnostics(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static BandwidthDiagnostics GetInstance(Context context) {
		if (mtr069BandwidthDiagnostics == null) {
			mtr069BandwidthDiagnostics = new BandwidthDiagnostics(context);
		}
		return mtr069BandwidthDiagnostics;
	}
	
	/**
	 * 解析BandwidthDiagnostics类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走普通
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return BandwidthDiagnostics类，从属于X_00E0FC类，移动特有
	 */
	public String Process(Boolean action, String name, String value) {
		String mBandwidthDiagnostics = "";
		String[] namearray = name.split("[.]");

		if ("DiagnosticsState".equals(namearray[3])) {
			mBandwidthDiagnostics = Process_DiagnosticsState(action, name, value);
		} else if ("Password".equals(namearray[3])) {
			mBandwidthDiagnostics = dataBaseGetOrSet(mContext, action, name, value);
		} else {
			mBandwidthDiagnostics = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mBandwidthDiagnostics == null) {
			mBandwidthDiagnostics = "";
		}

		return mBandwidthDiagnostics;

	}

	/**
	 * 诊断状态，1.None 
	 * 		   2.Requested:诊断请求，下发命令把参数设置为Requested,机顶盒开始宽带测试
	 * 		   3.Complete:诊断完成后，机顶盒把参数设置为Complete 
	 * 		   4.Error:诊断失败 
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，网络诊断情况
	 */
	private String Process_DiagnosticsState(Boolean action, String name, String value) {
		String bandwidthDiagnosticsInfo = "";
		if (action) {
			bandwidthDiagnosticsInfo = dataBaseGetOrSet(mContext, action, name, value);
		} else {
			dataBaseGetOrSet(mContext, action, name, value);
			new Thread() {
				@Override
				public void run() {
					String url = dataBaseGetOrSet(mContext, Config.GETACTION,
							"Device.X_00E0FC.BandwidthDiagnostics.DownloadURL",
							null);
					String name = dataBaseGetOrSet(mContext, Config.GETACTION,
							"Device.X_00E0FC.BandwidthDiagnostics.Username",
							null);
					String pwd = dataBaseGetOrSet(mContext, Config.GETACTION,
							"Device.X_00E0FC.BandwidthDiagnostics.Password",
							null);

					NetWorkSpeedInfo netWorkSpeedInfo = getFileFromUrl(url, name, pwd);

					dataBaseGetOrSet(mContext, Config.GETACTION,"Device.X_00E0FC.BandwidthDiagnostics.ErrorCode",
							netWorkSpeedInfo.ErrorCode);
					dataBaseGetOrSet(mContext, Config.GETACTION,"Device.X_00E0FC.BandwidthDiagnostics.AvgSpeed",
							netWorkSpeedInfo.AvgSpeed + "");
					dataBaseGetOrSet(mContext, Config.GETACTION,"Device.X_00E0FC.BandwidthDiagnostics.MaxSpeed",
							netWorkSpeedInfo.MaxSpeed + "");
					dataBaseGetOrSet(mContext, Config.GETACTION,"Device.X_00E0FC.BandwidthDiagnostics.MinSpeed",
							netWorkSpeedInfo.MinSpeed + "");
					if ("".equals(netWorkSpeedInfo.ErrorCode)) {
						dataBaseGetOrSet(mContext,Config.GETACTION,"Device.X_00E0FC.BandwidthDiagnostics.DiagnosticsState","3");
					} else
						dataBaseGetOrSet(
								mContext,
								Config.GETACTION,
								"Device.X_00E0FC.BandwidthDiagnostics.DiagnosticsState",
								"4");
				}
			}.start();
		}
		return bandwidthDiagnosticsInfo;

	}
	
	/**
	 * 通过到一个指定的连接里去下载一个文件，通过计算消耗的时间来计算宽带的大小
	 * @param url:String类型，指定的链接
	 * @param name:String类型，下载用户名
	 * @param pwd:String类型，下载用户密码
	 * @author
	 * @return netWorkSpeedInfo类，采用javaBean的模式，该类用来记录网络宽带的信息
	 */
	private NetWorkSpeedInfo getFileFromUrl(String url, String name, String pwd) {
		final long TIME_ONETIME = 10;
		int checkcount = 1;
		int fileLength = 0;
		long startTime = 0;
		long intervalTime = 0;
		long curspeed = 0;
		NetWorkSpeedInfo netWorkSpeedInfo = new NetWorkSpeedInfo();
		long lastTime = 0;
		long lastbytecount = 0;

		String getUrl = url + "?username=" + name + "&password=" + pwd;

		URL urlx = null;
		URLConnection con = null;
		InputStream stream = null;
		try {
			LogUtils.d("URL >>> " + getUrl);
			urlx = new URL(getUrl);
			con = urlx.openConnection();
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			fileLength = con.getContentLength();
			stream = con.getInputStream();
			netWorkSpeedInfo.totalBytes = fileLength;
			lastTime = startTime = System.currentTimeMillis();
			while ((stream.read()) != -1) {
				netWorkSpeedInfo.hadFinishedBytes++;
				long curtime = System.currentTimeMillis();
				intervalTime = curtime - startTime;
				// check one time per 10milliseconds
				if (intervalTime >= TIME_ONETIME * checkcount) {
					checkcount++;
					netWorkSpeedInfo.AvgSpeed = (netWorkSpeedInfo.hadFinishedBytes / intervalTime) * 1000 * 8 / 1024;
					curspeed = (netWorkSpeedInfo.hadFinishedBytes - lastbytecount)
							/ (intervalTime - lastTime) * 1000 * 8 / 1024;

					if ((netWorkSpeedInfo.MaxSpeed == 0)
							|| (curspeed > netWorkSpeedInfo.MaxSpeed)) {
						netWorkSpeedInfo.MaxSpeed = curspeed;
					}
					if ((netWorkSpeedInfo.MinSpeed == 0)
							|| (curspeed < netWorkSpeedInfo.MinSpeed)) {
						netWorkSpeedInfo.MinSpeed = curspeed;
					}
					lastTime = curtime;
					lastbytecount = netWorkSpeedInfo.hadFinishedBytes;

				}
			}
		} catch (MalformedURLException e) {
			netWorkSpeedInfo.ErrorCode = NetWorkSpeedInfo.ErrorCode_URLerror;
			LogUtils.e("exception >>> " + e.getMessage());
		} catch (IOException e) {
			netWorkSpeedInfo.ErrorCode = NetWorkSpeedInfo.ErrorCode_servererror;
			LogUtils.e("exception >>> " + e.getMessage());
		} catch (Exception e) {
			netWorkSpeedInfo.ErrorCode = NetWorkSpeedInfo.ErrorCode_processkerror;
			LogUtils.e("exception >>> " + e.getMessage());
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception e) {
				LogUtils.e("exception >>> " + e.getMessage());
			}

		}

		return netWorkSpeedInfo;
	}
}
