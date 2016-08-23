package com.jzbyapp.tr069service.mobile;

import android.content.Context;

import com.jzbyapp.utils.Utils;


/**
 * 扩展参数类，主要见于移动华为的平台，根据开发经验，移动的扩展参数有：X_OOEOC(该类主要是化为平台自己的扩展，一般情况下运营商不作要求)
 * 和X_CMCC_OTV两个类，而电信的扩展类于X_CTC_IPTV类，联通的扩展类有:X_CU_STB类
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class X_00E0FC extends Utils {
	private static X_00E0FC mtr069X_00E0FC = null;
	private Context mContext = null;

	public X_00E0FC(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static X_00E0FC GetInstance(Context context) {
		if (mtr069X_00E0FC == null){
			mtr069X_00E0FC = new X_00E0FC(context);
		}
		return mtr069X_00E0FC;
	}
	
	
	/**
	 * 解析X_00E0FC类下面的节点或类，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return X_00E0FC类，从属于device类
	 */
	public String Process(Boolean action, String name, String value) {
		String mX_00E0FCInfo = "";
		String[] namearray = name.split("[.]");
		if ("ErrorCode".equals(namearray[2])) {
			ErrorCode mErrorCode = ErrorCode.GetInstance(mContext);
			mX_00E0FCInfo = mErrorCode.Process(action, name, value);
		} else if ("Alarm".equals(namearray[2])) {
			Alarm mAlarm = Alarm.GetInstance(mContext);
			mX_00E0FCInfo = mAlarm.Process(action, name, value);
		} else if ("SQMConfiguration".equals(namearray[2])) {
			SQMConfiguration mSQMConfiguration = SQMConfiguration.GetInstance(mContext);
			mX_00E0FCInfo = mSQMConfiguration.Process(action, name, value);
		} else if ("PlayDiagnostics".equals(namearray[2])) {
			PlayDiagnostics mPlayDiagnostics = PlayDiagnostics.GetInstance(mContext);
			mX_00E0FCInfo = mPlayDiagnostics.Process(action, name, value);
		} else if ("LogParaConfiguration".equals(namearray[2])) {
			LogParaConfiguration mLogParaConfiguration = LogParaConfiguration.GetInstance(mContext);
			mX_00E0FCInfo = mLogParaConfiguration.Process(action, name, value);
		} else if ("AutoOnOffConfiguration".equals(namearray[2])) {
			AutoOnOffConfiguration mAutoOnOffConfiguration = AutoOnOffConfiguration.GetInstance(mContext);
			mX_00E0FCInfo = mAutoOnOffConfiguration.Process(action, name, value);
		} else if ("BandwidthDiagnostics".equals(namearray[2])) {
			BandwidthDiagnostics mBandwidthDiagnostics = BandwidthDiagnostics.GetInstance(mContext);
			mX_00E0FCInfo = mBandwidthDiagnostics.Process(action, name, value);
		} else if ("PacketCapture".equals(namearray[2])) {
			PacketCapture mPacketCapture = PacketCapture.GetInstance(mContext);
			mX_00E0FCInfo = mPacketCapture.Process(action, name, value);
		} else {
			mX_00E0FCInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		if (mX_00E0FCInfo == null){
			mX_00E0FCInfo = "";			
		}

		return mX_00E0FCInfo;

	}

}
