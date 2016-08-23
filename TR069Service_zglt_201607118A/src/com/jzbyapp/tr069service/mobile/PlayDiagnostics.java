package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;

/**
 * 华为扩展参数，远程播控参数,根据对接的经验，暂时还没遇到那家对此类有要求.也没有具体实现细节，现此类只为后续如果有了
 * 要求扩展用.现不对该类的字节点进行解析只走普通数据库存取.
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class PlayDiagnostics extends Utils {
	private static PlayDiagnostics mtr069PlayDiagnostics = null;
	private Context mContext = null;

	public PlayDiagnostics(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static PlayDiagnostics GetInstance(Context context) {
		if (mtr069PlayDiagnostics == null) {
			mtr069PlayDiagnostics = new PlayDiagnostics(context);
		}

		return mtr069PlayDiagnostics;
	}
	
	/**
	 * 解析PlayDiagnostics类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.现暂时
	 * 放空
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return PlayDiagnostics类，从属于X_00E0FC类
	 */
	public String Process(Boolean action, String name, String value) {
		String mPlayDiagnosticsInfo = "";
		// String[] namearray = name.split("[.]");

		mPlayDiagnosticsInfo = dataBaseGetOrSet(mContext, action, name, value);

		if (mPlayDiagnosticsInfo == null) {
			mPlayDiagnosticsInfo = "";
		}

		return mPlayDiagnosticsInfo;

	}

}
