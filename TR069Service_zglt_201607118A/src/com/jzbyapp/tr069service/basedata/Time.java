package com.jzbyapp.tr069service.basedata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.jzbyapp.utils.Utils;


/**
 * 终端中NTP时间的相关参数
 * @author
 * @see com.jzbyapp.tr069service.Tr069_Process
 */
public class Time extends Utils {
	private static Time tr069Time = null;
	private Context mcontext = null;

	public Time(Context context) {
		mcontext = context;
	}
	
	/**
	 * 获取本对象的一个引用，并保证该引用的个数不超过一个，这是java编程的一种经典设计模式，可查阅相关资料了解
	 * @param context:描述程序的环境信息，即上下文
	 * @return
	 */
	public static Time GetInstance(Context context) {
		if (tr069Time == null){
			tr069Time = new Time(context);
		}	
		return tr069Time;
	}

	
	/**
	 * 解析本类下面的节点，对于特殊处理的节点做相应的处理，普通节点直接通过dataBaseGetOrSet走数据库进行存取，
	 * 如果后续需要扩展该类需特殊处理的节点，只需在该方法中添加需要扩展的节点解析和对应的方法即可.
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作 
	 * @param name:String类型，具体节点名称 
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值 
	 * @author 
	 * @return Time类
	 */
	public String Process(Boolean action, String name, String value) {
		String timeInfo = "";
		String[] namearray = { "" };
		try {
			namearray = name.split("[.]");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		if ("NTPServer1".equals(namearray[2])) {
			timeInfo = Process_NTPServer1(mcontext, action, name, value);
		} else if ("NTPServer2".equals(namearray[2])) {
			timeInfo = Process_NTPServer2(action, name, value);
		} else if ("CurrentLocalTime".equals(namearray[2])) {
			timeInfo = Process_CurrentLocalTime(mcontext, action, name, value);
		} else if ("LocalTimeZone".equals(namearray[2])) {
			timeInfo = Process_LocalTimeZone(mcontext, action, name, value);
		} else {
			timeInfo = dataBaseGetOrSet(mcontext, action, name, value);
		}

		if (timeInfo == null) {
			timeInfo = "";
		}

		return timeInfo;
	}
	
	/**
	 * 终端设备第一个NTP时间服务器，可以为域名或IP地址
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，NTP时间服务器地址
	 */
	private String Process_NTPServer1(Context context, Boolean action, String name, String value) {
		String NTPServer1 = "";
		if (action) {
			NTPServer1 = Settings.Global.getString(mcontext.getContentResolver(), Settings.Global.NTP_SERVER);
		} else {
			Settings.Global.putString(mcontext.getContentResolver(),Settings.Global.NTP_SERVER, value);
		}
		return NTPServer1;
	}
	
	/**
	 * 终端设备第一个NTP时间服务器，可以为域名或IP地址，暂未实现，后续实现
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，NTP时间服务器地址
	 */
	private String Process_NTPServer2(Boolean action, String name, String value) {
		String NTPServer2 = "";
		if (action) {
			//NTPServer2 = mcontext.getResources().getString(com.android.internal.R.string.config_ntpServer);
		} else {
			// TODO:will do it in future
		}
		return NTPServer2;
	}
	
	/**
	 * 终端设备本地时区中的当前日期和时间
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，终端设备本地时区中的当前日期和时间
	 */
	private String Process_CurrentLocalTime(Context context, Boolean action, String name, String value) {
		String CurrentLocalTime = "";
		if (action) {
			try {
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				CurrentLocalTime = DateFormat.format("yyyy-MM-ddTHH:mm:ss",calendar).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Calendar calendar = Calendar.getInstance();
			value = value.replace("T", " ");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = dateFormat.parse(value);
				calendar.setTime(date);
				SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return CurrentLocalTime;
	}
	
	/**
	 * 本地时间与UTC的偏差
	 * @param context:描述程序的环境信息，即上下文
	 * @param action:boolean类型，为协议C层或其他进程需要本程序处理的动作
	 * @param name:String类型，具体节点名称
	 * @param value:如果action是要获取，则传null；如果action是要设置，则为具体需要设置的值
	 * @author
	 * @return String类型的值，本地时间与UTC的偏差
	 */
	private String Process_LocalTimeZone(Context context, Boolean action, String name,
			String value) {
		String LocalTimeZone = "";
		if (action) {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			int offset = calendar.get(Calendar.ZONE_OFFSET);
			LocalTimeZone = Utils.getOffsetTimeFormatString(offset);
		} else {
			String[] ids = TimeZone.getAvailableIDs();
			TimeZone timeZone = null;
			String str = null;
			for (String id : ids) {
				timeZone = TimeZone.getTimeZone(id);
				str = Utils.getOffsetTimeFormatString(timeZone.getRawOffset());
				if (str.equals(value)) {
					break;
				}
			}
			if (timeZone != null) {
				TimeZone.setDefault(timeZone);
			}
		}
		return LocalTimeZone;
	}

}
