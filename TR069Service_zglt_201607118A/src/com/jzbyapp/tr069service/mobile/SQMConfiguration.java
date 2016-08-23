package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;


/**
 * 华为平台扩展参数，IPTV SQM参数配置，主要见于移动运营商华为平台，截至现有的工作经验还没有遇到对此类由要求的项目，
 * 也没有具体实现细节，此类仅为以后如有需求或实现细节再实现，现放空仅走普通数据库存取
 * 华为平台对该类的子类有SQMLisenPort:SQM监听端口，是机顶盒监听mqmc消息的端口，默认是37001
 * 					 SQMLServerPort:SQM服务端口，默认是37000
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class SQMConfiguration extends Utils {
	private static SQMConfiguration mtr069SQMConfiguration = null;
	private Context mContext = null;

	public SQMConfiguration(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static SQMConfiguration GetInstance(Context context) {
		if (mtr069SQMConfiguration == null) {
			mtr069SQMConfiguration = new SQMConfiguration(context);
		}
		return mtr069SQMConfiguration;
	}
	
	/**
	 * 解析SQMConfiguration类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点和对应的方法即可.现暂时
	 * 放空
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return SQMConfiguration类，从属于X_00E0FC类
	 */
	public String Process(Boolean action, String name, String value) {
		String mSQMConfigurationInfo = "";
		//String[] namearray = name.split("[.]");

		mSQMConfigurationInfo = dataBaseGetOrSet(mContext, action,name, value);


		if (mSQMConfigurationInfo == null) {
			mSQMConfigurationInfo = "";
		}

		return mSQMConfigurationInfo;

	}

}
