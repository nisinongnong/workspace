package com.jzbyapp.tr069service.basedata;

import com.jzbyapp.utils.Utils;
import android.content.Context;

/**
 * The set-top box streaming media related information
 * 机顶盒流媒体相关信息,一般情况下这个类里面的节点都是初始化好上报个给前端，不会由前端来设置该类的值.
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class STBService extends Utils {
	private static STBService tr069StbService = null;
	private Context mcontext = null;

	public STBService(Context context) {
		mcontext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static STBService GetInstance(Context context) {
		if (tr069StbService == null){
			tr069StbService = new STBService(context);
		}	
		return tr069StbService;
	}
	
	/**
	 * 解析本类下的具体节点，需要特殊处理的节点解析出来作具体动作，普通节点通过dataBaseGetOrSet()走正常的数据存取.
	 * 如果后续要扩展需特殊处理的节点，只需在本方法里面进行节点解析的扩展并添加相应的动作方法即可.
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return ManagementServer类
	 */
	public String Process(Boolean action, String name, String value) {
		String stbServiceInfo = "";

		stbServiceInfo = dataBaseGetOrSet(mcontext, action, name, value);

		if (stbServiceInfo == null) {
			stbServiceInfo = "";
		}

		return stbServiceInfo;

	}

}
