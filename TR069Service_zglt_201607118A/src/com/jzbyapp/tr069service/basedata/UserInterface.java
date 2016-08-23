package com.jzbyapp.tr069service.basedata;

import java.util.Locale;

import android.content.Context;

import com.jzbyapp.utils.Utils;

/**
 * 用户接口相关参数类
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class UserInterface extends Utils {
	private static UserInterface tr069UserInterface = null;
	private Context mcontext = null;

	public UserInterface(Context context) {
		mcontext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static UserInterface GetInstance(Context context) {
		if (tr069UserInterface == null){
			tr069UserInterface = new UserInterface(context);
		}	
		return tr069UserInterface;
	}

	/**
	 * 解析本类下的具体节点，需要特殊处理的节点解析出来作具体动作，普通节点通过dataBaseGetOrSet()走正常的数据存储.
	 * 如果后续要扩展需特殊处理的节点，只需在本方法里面进行节点解析的扩展并添加相应的动作方法即可.
	 * @param action:boolean类型，协议C层或其他进程需要本程序执行的动作
	 * @param name:String类型，具体的节点名称
	 * @param value:String类型，如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author 
	 * @return UserInterface类
	 */
	public String Process(Boolean action, String name, String value) {
		String UserInterfaceinfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		if ("AvailableLanguages".equals(namearray[2])) {
			UserInterfaceinfo = Process_AvailableLanguages(mcontext, action, name, value);
		} else if ("CurrentLanguage".equals(namearray[2])) {
			UserInterfaceinfo = Process_CurrentLanguage(mcontext, action, name, value);
		}

		if (UserInterfaceinfo == null){
			UserInterfaceinfo = "";
		}	

		return UserInterfaceinfo;

	}
	
	/**
	 * 获取终端设备当前可选语种
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该节点为只读，action只能为get
	 * @author
	 * @return String类型的值，终端设备当前可选的语种
	 */
	private String Process_AvailableLanguages(Context context, Boolean action, String name,
			String value) {
		String AvailableLanguages = null;
		if (action) {
			AvailableLanguages = getAvailableLanguageFormatString();
		} else {
			// read only
		}
		return AvailableLanguages;

	}
	
	/**
	 * 获取终端设备当前语种
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；该节点为只读，action只能为get
	 * @author
	 * @return String类型的值，终端设备当前的语种
	 */
	private String Process_CurrentLanguage(Context context, Boolean action, String name,
			String value) {
		String CurrentLanguage = null;
		if (action) {
			Locale locale = Locale.getDefault();
			CurrentLanguage = locale.getLanguage();
		} else {
			Locale[] locales = Locale.getAvailableLocales();
			Locale destLocale = null;
			for (Locale locale : locales) {
				if (locale != null && locale.getLanguage().equals(value)) {
					destLocale = locale;
					break;
				}
			}
			if (destLocale != null) {
				Locale.setDefault(destLocale);
			}
		}
		return CurrentLanguage;
	}
}
