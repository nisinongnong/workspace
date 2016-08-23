package com.jzbyapp.tr069service.mobile;

import com.jzbyapp.utils.Utils;

import android.content.Context;


/**
 * 机顶盒错误码信息，为一动态节点的形式，主要见于浙江移动魔百盒项目
 * 现阶段动态节点只需要java做数据库的管理，不需要任何操作，所以该类暂时不实现功能
 * @author
 * @see com.jzbyapp.tr069service.mobile.X_00E0FC
 */
public class ErrorCode extends Utils {
	private static ErrorCode mtr069ErrorCode = null;
	private Context mContext = null;

	public ErrorCode(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static ErrorCode GetInstance(Context context) {
		if (mtr069ErrorCode == null){
			mtr069ErrorCode = new ErrorCode(context);
		}
		return mtr069ErrorCode;
	}

	public String Process(Boolean action, String name, String value) {
		String mErrorCodeInfo = "";
		String[] namearray = name.split("[.]");
		
		//先调用该接口，获取i值，再拼成节点的全路经
		//JNI_ADD_DYNAMICNODE(name);
		
		if ("ErrorCodeTime".equals(namearray[3])) {
			//X_00E0FC_ErrorCodeInfo = JNI_GET_DYNAMICNODE(name);
		} else if("ErrorCodeValue".equals(namearray[3])){
			//X_00E0FC_ErrorCodeInfo = dataBaseGetOrSet(mContext, action, name, value);
		}

		mErrorCodeInfo = dataBaseGetOrSet(mContext, action, name, value);

		if (mErrorCodeInfo == null){
			mErrorCodeInfo = "";
		}

		return mErrorCodeInfo;

	}

}
