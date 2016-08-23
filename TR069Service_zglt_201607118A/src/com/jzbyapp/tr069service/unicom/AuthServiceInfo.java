package com.jzbyapp.tr069service.unicom;

import android.content.Context;
import android.os.Message;

import com.jzbyapp.utils.Config;
import com.jzbyapp.utils.LogUtils;
import com.jzbyapp.utils.Utils;


/**
 * 机顶盒业务相关信息
 * @author
 * @see com.jzbyapp.tr069service.unicom.X_CU_STB
 */
public class AuthServiceInfo extends Utils{
	private static AuthServiceInfo mtr069AuthService = null;
	private Context mContext = null;

	public AuthServiceInfo(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static AuthServiceInfo GetInstance(Context context) {
		if (mtr069AuthService == null){
			mtr069AuthService = new AuthServiceInfo(context);
		}
			
		return mtr069AuthService;
	}
	
	/**
	 * 解析AuthServiceInfo类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走普通
	 * 数据库进行存取，如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return AuthServiceInfo类,从属于X_CU_STB类
	 */
	public String Process(Boolean action, String name, String value) {
		String X_CU_STB_AuthServiceInfo = "";
		String[] namearray = name.split("[.]");
		
		if("Activate".equals(namearray[3])){
			zeroConfigForBjUnicom(mContext, action, name, value);	
		} else if (("PPPoEID".equals(namearray[3]))|| ("PPPoEID2".equals(namearray[3]))) {
			X_CU_STB_AuthServiceInfo = Process_PPPoEID(mContext, action, name, value);
		} else if (("PPPoEPassword".equals(namearray[3]))|| ("PPPoEPassword2".equals(namearray[3]))) {
			X_CU_STB_AuthServiceInfo = Process_PPPoEPassword(mContext, action, name,value);
		} else if (("UserID".equals(namearray[3])) || ("UserID2".equals(namearray[3]))) {
			X_CU_STB_AuthServiceInfo = Process_AuthUserID(mContext, action, name, value);
		} else if (("UserIDPassword".equals(namearray[3])) || ("UserIDPassword2".equals(namearray[3]))) {	
			X_CU_STB_AuthServiceInfo = Process_AuthUserIDPassword(mContext, action, name,value);
		} else{
			X_CU_STB_AuthServiceInfo = dataBaseGetOrSet(mContext, action, name, value);
		}
			
		if (X_CU_STB_AuthServiceInfo == null){
			X_CU_STB_AuthServiceInfo = "";
		}
			
		return X_CU_STB_AuthServiceInfo;

	}

	/**
	 * 机顶盒零配置实现接口，根据规范，Device.X_CU_STB.AuthServiceInfo.Activate的值前端下发为：
	 * 1表示终端要零配置；2表示零配置成功完成；3表示零配置失败，本接口根据前端对这个节点下发的值来作相应的回应动作
	 * 在此约定此接口并不属于标准版范畴，只是为了对接北京联通前端而添加的接口.
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 */
	private void zeroConfigForBjUnicom(Context context, Boolean action, String name, String value) {
		// TODO Auto-generated method stub
		if(action){
			dataBaseGetOrSet(mContext, action, name, value);
		}else{
			if((Integer.parseInt(value) == 1) || "1".equals(value)){
				Message msg = Message.obtain();
				msg.what = Config.messageQueue.MESSAGE_OPENUSER;
				LogUtils.d("KEY_DIALOGSHOW is sendMessage >>> MESSAGE_OPENUSER");
				Config.messageQueue.sendMessage(msg);
			}else if((Integer.parseInt(value) == 2) || "2".equals(value)){
				Message msg = Message.obtain();
				msg.what = Config.messageQueue.MESSAGE_OPENUSERACTIVE;
				LogUtils.d("KEY_DIALOGSHOW is sendMessage >>> MESSAGE_OPENUSERACTIVE");
				Config.messageQueue.sendMessage(msg);
			}else if((Integer.parseInt(value) == 3) || "3".equals(value)){
				Message msg = Message.obtain();
				msg.what = Config.messageQueue.MESSAGE_OPENUSERFAILED;
				LogUtils.d("KEY_DIALOGSHOW is sendMessage >>> MESSAGE_OPENUSERFAILED");
				Config.messageQueue.sendMessage(msg);
			}
		}
	}
}
