package com.jzbyapp.utils;


/**
 * 网络测速封装类，采用java bean的设计模式，想了解java编程的这个设计模式可以自行查阅相关资料
 * @author
 *
 */
public class NetWorkSpeedInfo {
	/** Network speed */
	public long AvgSpeed = 0;
	public long MaxSpeed = 0;
	public long MinSpeed = 0;
	/** Had finished bytes */
	public long hadFinishedBytes = 0;
	/** Total bytes of a file, default is 1024 bytes,1K */
	public long totalBytes = 1024;

	/** The net work type, 3G or GSM and so on */
	public int networkType = 0;

	/** Down load the file percent 0----100 */
	public int downloadPercent = 0;
	
	public String ErrorCode = "";
	
	public static final String ErrorCode_networkerror = "102001";
	public static final String ErrorCode_URLerror = "102050";
	public static final String ErrorCode_servererror = "102051";
	public static final String ErrorCode_processkerror = "102052";
}
